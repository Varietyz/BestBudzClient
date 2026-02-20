#!/usr/bin/env node
/**
 * DOM Bypass AST Scanner
 *
 * Uses TypeScript compiler API to accurately detect DOM factory bypasses:
 * - document.createElement() calls (should use DOM.createElement())
 * - innerHTML assignments (should use DOM.icon() or structured creation)
 * - Direct style property assignments (should use CSS classes/tokens)
 *
 * Outputs PAG-formatted remediation log to reports/dom-bypasses.remediation.md
 *
 * @module dom-bypass-scanner
 */

const path = require('path');
const {
    ts,
    parseFile,
    getLineNumber,
    getNodeText,
    walkAst,
    isInnerHTMLAssignment,
    isDocumentCreateElement,
    isDOMFactoryCall,
    getContainingClass,
    getContainingMethod,
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
const REPORT_NAME = 'dom-bypasses.remediation.md';

// Files to exclude from scanning
const EXCLUDE_PATTERNS = [
    '**/dom-factory.ts',        // The factory itself
    '**/dom-factory.test.ts',   // Tests for factory
    '**/polyfills/**',          // Browser polyfills
];

// Violation types and their remediations
const VIOLATION_TYPES = {
    DOCUMENT_CREATE_ELEMENT: {
        severity: 'error',
        condition: 'Element creation uses DOM factory',
        message: 'Direct document.createElement() bypasses DOM factory',
        remediation: 'Replace with DOM.createElement() for lifecycle management',
        factoryMethod: 'DOM.createElement()',
    },
    INNERHTML_ICON: {
        severity: 'error',
        condition: 'Icon SVG uses DOM.icon()',
        message: 'innerHTML assignment with SVG bypasses icon management',
        remediation: 'Replace with DOM.icon({ name: "iconName" })',
        factoryMethod: 'DOM.icon()',
    },
    INNERHTML_GENERIC: {
        severity: 'warning',
        condition: 'Dynamic content uses structured creation',
        message: 'innerHTML assignment can cause XSS and bypasses pooling',
        remediation: 'Replace with DOM.createElement() with children array',
        factoryMethod: 'DOM.createElement()',
    },
    STYLE_PROPERTY: {
        severity: 'warning',
        condition: 'Styling uses CSS classes and tokens',
        message: 'Direct style property assignment bypasses CSS token system',
        remediation: 'Use className with CSS classes that reference tokens',
        factoryMethod: 'CSS classes',
    },
};

// =============================================================================
// Violation Detection
// =============================================================================

/**
 * Detect if an innerHTML assignment is setting an icon SVG
 * @param {ts.BinaryExpression} node
 * @param {ts.SourceFile} sourceFile
 * @returns {boolean}
 */
function isIconInnerHTML(node, sourceFile) {
    const rightText = getNodeText(node.right, sourceFile).toLowerCase();
    return rightText.includes('getIconSvg') ||
        rightText.includes('<svg') ||
        rightText.includes('icon') ||
        rightText.includes('lucide');
}

/**
 * Check if node is a style property assignment
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isStylePropertyAssignment(node) {
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
 * Scan a single file for violations
 * @param {string} filePath
 * @param {string} basePath
 * @returns {object[]} Array of violations
 */
function scanFile(filePath, basePath) {
    const violations = [];
    const sourceFile = parseFile(filePath);
    const relativePath = getRelativePath(filePath, basePath);

    walkAst(sourceFile, (node, sf) => {
        // Check for document.createElement()
        if (isDocumentCreateElement(node)) {
            const line = getLineNumber(sf, node.getStart());
            const snippet = getNodeText(node, sf);

            // Get element type if available
            let elementType = 'unknown';
            if (ts.isCallExpression(node) && node.arguments.length > 0) {
                const firstArg = node.arguments[0];
                if (ts.isStringLiteral(firstArg)) {
                    elementType = firstArg.text;
                }
            }

            violations.push({
                file: relativePath,
                line,
                type: 'DOCUMENT_CREATE_ELEMENT',
                status: 'FAILED',
                ...VIOLATION_TYPES.DOCUMENT_CREATE_ELEMENT,
                snippet,
                suggestedFix: `DOM.createElement("${elementType}", { /* options */ })`,
            });
        }

        // Check for innerHTML assignments
        if (isInnerHTMLAssignment(node)) {
            const line = getLineNumber(sf, node.getStart());
            const snippet = getNodeText(node, sf);
            const isIcon = isIconInnerHTML(node, sf);

            const violationType = isIcon ? 'INNERHTML_ICON' : 'INNERHTML_GENERIC';
            const typeConfig = VIOLATION_TYPES[violationType];

            violations.push({
                file: relativePath,
                line,
                type: violationType,
                status: 'FAILED',
                ...typeConfig,
                snippet: snippet.length > 100 ? snippet.substring(0, 100) + '...' : snippet,
                suggestedFix: isIcon
                    ? 'const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);'
                    : 'Use DOM.createElement() with children array',
            });
        }

        // Check for direct style property assignments
        if (isStylePropertyAssignment(node)) {
            const line = getLineNumber(sf, node.getStart());
            const snippet = getNodeText(node, sf);

            // Only flag if it looks like hardcoded values
            if (snippet.includes('px') || snippet.includes('#') || snippet.includes('rgb')) {
                violations.push({
                    file: relativePath,
                    line,
                    type: 'STYLE_PROPERTY',
                    status: 'FAILED',
                    ...VIOLATION_TYPES.STYLE_PROPERTY,
                    snippet: snippet.length > 100 ? snippet.substring(0, 100) + '...' : snippet,
                    suggestedFix: 'Use CSS class with token variables',
                });
            }
        }
    }, sourceFile);

    return violations;
}

// =============================================================================
// Main Scanner
// =============================================================================

function main() {
    console.log('\nDOM Bypass AST Scanner');
    console.log('======================');
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

    // Calculate statistics
    const totalErrors = allViolations.filter(v => v.severity === 'error').length;
    const totalWarnings = allViolations.filter(v => v.severity === 'warning').length;

    const byType = {};
    for (const v of allViolations) {
        if (!byType[v.type]) {
            byType[v.type] = 0;
        }
        byType[v.type]++;
    }

    // Sort files by violation count
    const sortedFiles = Object.entries(fileStats)
        .sort((a, b) => b[1].total - a[1].total);

    // Generate recommendations
    const recommendations = [
        `Fix ${byType.DOCUMENT_CREATE_ELEMENT || 0} document.createElement() calls - use DOM.createElement()`,
        `Fix ${byType.INNERHTML_ICON || 0} icon innerHTML assignments - use DOM.icon()`,
        `Review ${byType.INNERHTML_GENERIC || 0} generic innerHTML assignments - use structured creation`,
        `Convert ${byType.STYLE_PROPERTY || 0} inline style assignments to CSS classes`,
        'Run this scanner after each migration batch to track progress',
    ];

    // Migration phases
    const migrationPhases = [
        { name: 'Phase 1: Icon innerHTML', count: byType.INNERHTML_ICON || 0, factoryMethod: 'DOM.icon()', priority: 1 },
        { name: 'Phase 2: createElement', count: byType.DOCUMENT_CREATE_ELEMENT || 0, factoryMethod: 'DOM.createElement()', priority: 2 },
        { name: 'Phase 3: Generic innerHTML', count: byType.INNERHTML_GENERIC || 0, factoryMethod: 'Structured creation', priority: 3 },
        { name: 'Phase 4: Style properties', count: byType.STYLE_PROPERTY || 0, factoryMethod: 'CSS classes', priority: 4 },
    ];

    // Write PAG report
    const reportPath = path.join(REPORTS_DIR, REPORT_NAME);
    writePAGReport(reportPath, {
        name: 'dom-bypass-remediation',
        description: 'DOM factory bypass violations requiring remediation',
        intent: 'Centralize all DOM creation through DOM factory',
        objective: 'Zero document.createElement() and innerHTML bypasses',
        violations: allViolations,
        recommendations,
        migrationPhases,
    });

    // Write JSON report for programmatic access
    writeJSONReport(reportPath, {
        scannedAt: new Date().toISOString(),
        sourceDir: SRC_DIR,
        summary: {
            totalFiles: files.length,
            filesWithViolations: Object.keys(fileStats).length,
            totalViolations: allViolations.length,
            errors: totalErrors,
            warnings: totalWarnings,
            byType,
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
    console.log('\nBy Type:');
    for (const [type, count] of Object.entries(byType)) {
        console.log(`  ${type}: ${count}`);
    }

    console.log('\nTop 10 Files:');
    for (let i = 0; i < Math.min(10, sortedFiles.length); i++) {
        const [file, stats] = sortedFiles[i];
        console.log(`  ${i + 1}. ${path.basename(file)}: ${stats.total} (${stats.errors}E/${stats.warnings}W)`);
    }

    console.log(`\nPAG Report: ${reportPath}`);
    console.log(`JSON Report: ${reportPath.replace('.md', '.json')}`);
}

main();
