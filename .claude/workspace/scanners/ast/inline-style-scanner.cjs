#!/usr/bin/env node
/**
 * Inline Style AST Scanner
 *
 * Uses TypeScript compiler API to detect CSS token violations:
 * - Hardcoded pixel values (should use CSS tokens)
 * - Hardcoded colors (should use CSS variables)
 * - Inline style objects (should use CSS classes)
 *
 * Outputs PAG-formatted remediation log to reports/inline-styles.remediation.md
 *
 * @module inline-style-scanner
 */

const path = require('path');
const {
    ts,
    parseFile,
    getLineNumber,
    getNodeText,
    walkAst,
    findTypeScriptFiles,
    getRelativePath,
    writePAGReport,
    writeJSONReport,
} = require('../scanner-base.cjs');

// =============================================================================
// Configuration
// =============================================================================

const SRC_DIR = path.resolve(__dirname, '../../../../root/archlab-ide/src/renderer');
const REPORTS_DIR = path.resolve(__dirname, '../reports');
const REPORT_NAME = 'inline-styles.remediation.md';

const EXCLUDE_PATTERNS = [
    '**/dom-factory.ts',
    '**/*.test.ts',
    '**/styles/**',
    '**/css/**',
];

// Token mappings for remediation suggestions
const TOKEN_MAPPINGS = {
    colors: {
        patterns: [
            /#[0-9a-fA-F]{3,8}/,
            /rgb\s*\([^)]+\)/,
            /rgba\s*\([^)]+\)/,
            /hsl\s*\([^)]+\)/,
            /hsla\s*\([^)]+\)/,
        ],
        suggestions: {
            '#fff': 'var(--color-text-primary)',
            '#ffffff': 'var(--color-text-primary)',
            '#000': 'var(--color-bg-primary)',
            '#1e1e1e': 'var(--color-bg-primary)',
            '#252526': 'var(--color-bg-secondary)',
            '#3c3c3c': 'var(--color-border)',
            '#c9a227': 'var(--color-accent-primary)',
            '#d4af37': 'var(--color-accent-primary)',
        },
        tokenPrefix: '--color-',
    },
    spacing: {
        patterns: [
            /padding:\s*\d+px/,
            /margin:\s*\d+px/,
            /gap:\s*\d+px/,
        ],
        suggestions: {
            '4px': 'var(--space-xs)',
            '8px': 'var(--space-sm)',
            '12px': 'var(--space-md)',
            '16px': 'var(--space-lg)',
            '24px': 'var(--space-xl)',
        },
        tokenPrefix: '--space-',
    },
    fontSize: {
        patterns: [
            /font-size:\s*\d+px/,
            /fontSize:\s*['"]?\d+px['"]?/,
        ],
        suggestions: {
            '10px': 'var(--font-size-xs)',
            '11px': 'var(--font-size-sm)',
            '12px': 'var(--font-size-md)',
            '14px': 'var(--font-size-lg)',
            '16px': 'var(--font-size-xl)',
        },
        tokenPrefix: '--font-size-',
    },
    borderRadius: {
        patterns: [
            /border-radius:\s*\d+px/,
            /borderRadius:\s*['"]?\d+px['"]?/,
        ],
        suggestions: {
            '2px': 'var(--radius-xs)',
            '4px': 'var(--radius-sm)',
            '6px': 'var(--radius-md)',
            '8px': 'var(--radius-lg)',
        },
        tokenPrefix: '--radius-',
    },
    fixedDimensions: {
        patterns: [
            /width:\s*\d+px/,
            /height:\s*\d+px/,
            /min-width:\s*\d+px/,
            /min-height:\s*\d+px/,
            /max-width:\s*\d+px/,
            /max-height:\s*\d+px/,
        ],
        suggestions: {},
        tokenPrefix: '--size-',
        note: 'Consider using responsive units (rem, %, flex) instead of px',
    },
};

// Violation types
const VIOLATION_TYPES = {
    HARDCODED_COLOR: {
        severity: 'error',
        condition: 'Colors use CSS variables',
        message: 'Hardcoded color bypasses theme system',
        remediation: 'Replace with CSS variable from --color-* tokens',
    },
    HARDCODED_SPACING: {
        severity: 'warning',
        condition: 'Spacing uses CSS variables',
        message: 'Hardcoded spacing inconsistent with design system',
        remediation: 'Replace with CSS variable from --space-* tokens',
    },
    HARDCODED_FONT_SIZE: {
        severity: 'warning',
        condition: 'Font sizes use CSS variables',
        message: 'Hardcoded font-size bypasses typography system',
        remediation: 'Replace with CSS variable from --font-size-* tokens',
    },
    HARDCODED_RADIUS: {
        severity: 'warning',
        condition: 'Border radius uses CSS variables',
        message: 'Hardcoded border-radius inconsistent with design system',
        remediation: 'Replace with CSS variable from --radius-* tokens',
    },
    STYLE_OBJECT: {
        severity: 'warning',
        condition: 'Styling uses CSS classes',
        message: 'Inline style object should use CSS class',
        remediation: 'Create CSS class and use className instead',
    },
    FIXED_DIMENSION: {
        severity: 'info',
        condition: 'Dimensions are responsive',
        message: 'Fixed pixel dimension may not be responsive',
        remediation: 'Consider using rem, %, or flex for responsive sizing',
    },
};

// =============================================================================
// Detection Functions
// =============================================================================

/**
 * Detect token violations in a string
 * @param {string} text
 * @returns {object[]}
 */
function detectTokenViolations(text) {
    const violations = [];

    for (const [category, config] of Object.entries(TOKEN_MAPPINGS)) {
        for (const pattern of config.patterns) {
            const matches = text.match(new RegExp(pattern, 'gi')) || [];
            for (const match of matches) {
                const suggestion = config.suggestions[match.toLowerCase()] ||
                    `Use ${config.tokenPrefix}* variable`;

                let violationType;
                switch (category) {
                    case 'colors':
                        violationType = 'HARDCODED_COLOR';
                        break;
                    case 'spacing':
                        violationType = 'HARDCODED_SPACING';
                        break;
                    case 'fontSize':
                        violationType = 'HARDCODED_FONT_SIZE';
                        break;
                    case 'borderRadius':
                        violationType = 'HARDCODED_RADIUS';
                        break;
                    case 'fixedDimensions':
                        violationType = 'FIXED_DIMENSION';
                        break;
                    default:
                        violationType = 'STYLE_OBJECT';
                }

                violations.push({
                    type: violationType,
                    category,
                    match,
                    suggestion,
                    note: config.note,
                });
            }
        }
    }

    return violations;
}

/**
 * Check if node is a style property assignment
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isStyleAssignment(node) {
    if (ts.isBinaryExpression(node) &&
        node.operatorToken.kind === ts.SyntaxKind.EqualsToken) {
        const left = node.left;
        if (ts.isPropertyAccessExpression(left)) {
            const obj = left.expression;
            if (ts.isPropertyAccessExpression(obj) && obj.name.text === 'style') {
                return true;
            }
        }
    }
    return false;
}

/**
 * Check if node is a style object in createElement options
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isStylePropertyInOptions(node) {
    if (ts.isPropertyAssignment(node)) {
        const name = node.name;
        if (ts.isIdentifier(name) && name.text === 'style') {
            return true;
        }
    }
    return false;
}

// =============================================================================
// File Scanner
// =============================================================================

/**
 * Scan a single file
 * @param {string} filePath
 * @param {string} basePath
 * @returns {object[]}
 */
function scanFile(filePath, basePath) {
    const violations = [];
    const sourceFile = parseFile(filePath);
    const relativePath = getRelativePath(filePath, basePath);

    walkAst(sourceFile, (node, sf) => {
        // Check style property assignments: element.style.color = '#fff'
        if (isStyleAssignment(node)) {
            const text = getNodeText(node, sf);
            const tokenViolations = detectTokenViolations(text);

            for (const tv of tokenViolations) {
                const line = getLineNumber(sf, node.getStart());
                violations.push({
                    file: relativePath,
                    line,
                    type: tv.type,
                    status: 'FAILED',
                    ...VIOLATION_TYPES[tv.type],
                    category: tv.category,
                    snippet: text.length > 100 ? text.substring(0, 100) + '...' : text,
                    match: tv.match,
                    suggestedFix: tv.suggestion,
                    note: tv.note,
                });
            }
        }

        // Check style objects in DOM.createElement() options
        if (isStylePropertyInOptions(node)) {
            const parent = node;
            if (ts.isPropertyAssignment(parent) && parent.initializer) {
                const text = getNodeText(parent.initializer, sf);
                const tokenViolations = detectTokenViolations(text);

                for (const tv of tokenViolations) {
                    const line = getLineNumber(sf, node.getStart());
                    violations.push({
                        file: relativePath,
                        line,
                        type: tv.type,
                        status: 'FAILED',
                        ...VIOLATION_TYPES[tv.type],
                        category: tv.category,
                        snippet: text.length > 100 ? text.substring(0, 100) + '...' : text,
                        match: tv.match,
                        suggestedFix: tv.suggestion,
                        note: tv.note,
                    });
                }

                // Also flag the style object itself if it has multiple properties
                if (ts.isObjectLiteralExpression(parent.initializer) &&
                    parent.initializer.properties.length > 2) {
                    const line = getLineNumber(sf, node.getStart());
                    violations.push({
                        file: relativePath,
                        line,
                        type: 'STYLE_OBJECT',
                        status: 'FAILED',
                        ...VIOLATION_TYPES.STYLE_OBJECT,
                        category: 'styleObject',
                        snippet: text.length > 100 ? text.substring(0, 100) + '...' : text,
                        suggestedFix: 'Move styles to CSS class and use className',
                    });
                }
            }
        }

        // Check string literals for inline styles
        if (ts.isStringLiteral(node) || ts.isNoSubstitutionTemplateLiteral(node)) {
            const text = node.text;
            // Only check if it looks like CSS
            if (text.includes(':') && (text.includes('px') || text.includes('#') || text.includes('rgb'))) {
                const tokenViolations = detectTokenViolations(text);

                for (const tv of tokenViolations) {
                    const line = getLineNumber(sf, node.getStart());
                    violations.push({
                        file: relativePath,
                        line,
                        type: tv.type,
                        status: 'FAILED',
                        ...VIOLATION_TYPES[tv.type],
                        category: tv.category,
                        snippet: text.length > 100 ? text.substring(0, 100) + '...' : text,
                        match: tv.match,
                        suggestedFix: tv.suggestion,
                        note: tv.note,
                    });
                }
            }
        }
    }, sourceFile);

    return violations;
}

// =============================================================================
// Main
// =============================================================================

function main() {
    console.log('\nInline Style AST Scanner');
    console.log('========================');
    console.log(`Source: ${SRC_DIR}`);
    console.log(`Output: ${REPORTS_DIR}/${REPORT_NAME}\n`);

    const files = findTypeScriptFiles(SRC_DIR, EXCLUDE_PATTERNS);
    console.log(`Scanning ${files.length} TypeScript files...\n`);

    const allViolations = [];
    const fileStats = {};

    for (const file of files) {
        try {
            const violations = scanFile(file, SRC_DIR);
            if (violations.length > 0) {
                allViolations.push(...violations);
                const relativePath = getRelativePath(file, SRC_DIR);
                fileStats[relativePath] = {
                    total: violations.length,
                    errors: violations.filter(v => v.severity === 'error').length,
                    warnings: violations.filter(v => v.severity === 'warning').length,
                };
            }
        } catch (err) {
            console.error(`Error scanning ${file}: ${err.message}`);
        }
    }

    // Statistics
    const totalErrors = allViolations.filter(v => v.severity === 'error').length;
    const totalWarnings = allViolations.filter(v => v.severity === 'warning').length;
    const totalInfo = allViolations.filter(v => v.severity === 'info').length;

    const byType = {};
    const byCategory = {};
    for (const v of allViolations) {
        byType[v.type] = (byType[v.type] || 0) + 1;
        byCategory[v.category] = (byCategory[v.category] || 0) + 1;
    }

    const sortedFiles = Object.entries(fileStats)
        .sort((a, b) => b[1].total - a[1].total);

    // Recommendations
    const recommendations = [
        `Fix ${byType.HARDCODED_COLOR || 0} hardcoded color values - use --color-* variables`,
        `Fix ${byType.HARDCODED_SPACING || 0} hardcoded spacing values - use --space-* variables`,
        `Fix ${byType.HARDCODED_FONT_SIZE || 0} hardcoded font sizes - use --font-size-* variables`,
        `Review ${byType.STYLE_OBJECT || 0} inline style objects - consider CSS classes`,
        `Review ${byType.FIXED_DIMENSION || 0} fixed dimensions for responsiveness`,
        'Consider creating utility CSS classes for common patterns',
    ];

    // Migration phases
    const migrationPhases = [
        { name: 'Phase 1: Colors', count: byCategory.colors || 0, factoryMethod: '--color-* tokens', priority: 1 },
        { name: 'Phase 2: Spacing', count: byCategory.spacing || 0, factoryMethod: '--space-* tokens', priority: 2 },
        { name: 'Phase 3: Typography', count: byCategory.fontSize || 0, factoryMethod: '--font-size-* tokens', priority: 3 },
        { name: 'Phase 4: Borders', count: byCategory.borderRadius || 0, factoryMethod: '--radius-* tokens', priority: 4 },
        { name: 'Phase 5: Style Objects', count: byCategory.styleObject || 0, factoryMethod: 'CSS classes', priority: 5 },
    ];

    // Write reports
    const reportPath = path.join(REPORTS_DIR, REPORT_NAME);
    writePAGReport(reportPath, {
        name: 'inline-style-remediation',
        description: 'Inline style and CSS token violations requiring remediation',
        intent: 'Centralize all styling through CSS tokens and classes',
        objective: 'Zero hardcoded style values, consistent design system usage',
        violations: allViolations,
        recommendations,
        migrationPhases,
    });

    writeJSONReport(reportPath, {
        scannedAt: new Date().toISOString(),
        sourceDir: SRC_DIR,
        summary: {
            totalFiles: files.length,
            filesWithViolations: Object.keys(fileStats).length,
            totalViolations: allViolations.length,
            errors: totalErrors,
            warnings: totalWarnings,
            info: totalInfo,
            byType,
            byCategory,
        },
        topFiles: sortedFiles.slice(0, 20),
        violations: allViolations,
    });

    // Print summary
    console.log('Scan Complete!');
    console.log('==============');
    console.log(`Total files scanned: ${files.length}`);
    console.log(`Files with violations: ${Object.keys(fileStats).length}`);
    console.log(`Total violations: ${allViolations.length}`);
    console.log(`  - Errors: ${totalErrors}`);
    console.log(`  - Warnings: ${totalWarnings}`);
    console.log(`  - Info: ${totalInfo}`);
    console.log('\nBy Type:');
    for (const [type, count] of Object.entries(byType)) {
        console.log(`  ${type}: ${count}`);
    }
    console.log('\nBy Category:');
    for (const [cat, count] of Object.entries(byCategory)) {
        console.log(`  ${cat}: ${count}`);
    }

    if (sortedFiles.length > 0) {
        console.log('\nTop 10 Files:');
        for (let i = 0; i < Math.min(10, sortedFiles.length); i++) {
            const [file, stats] = sortedFiles[i];
            console.log(`  ${i + 1}. ${path.basename(file)}: ${stats.total} (${stats.errors}E/${stats.warnings}W)`);
        }
    }

    console.log(`\nPAG Report: ${reportPath}`);
    console.log(`JSON Report: ${reportPath.replace('.md', '.json')}`);
}

main();
