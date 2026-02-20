#!/usr/bin/env node
/**
 * Listener Pattern AST Scanner
 *
 * Uses TypeScript compiler API to detect event listener anti-patterns:
 * - addEventListener() without managed cleanup (should use addManagedListener)
 * - render() methods that add listeners without clearing first
 * - Components extending BaseEventComponent with render() patterns
 *
 * Outputs PAG-formatted remediation log to reports/listener-patterns.remediation.md
 *
 * @module listener-pattern-scanner
 */

const path = require('path');
const {
    ts,
    parseFile,
    getLineNumber,
    getNodeText,
    walkAst,
    isAddEventListener,
    isAddManagedListener,
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
const REPORT_NAME = 'listener-patterns.remediation.md';

// Files to exclude
const EXCLUDE_PATTERNS = [
    '**/base-event-component.ts',   // The base class
    '**/*.test.ts',
    '**/polyfills/**',
];

// Violation types
const VIOLATION_TYPES = {
    UNMANAGED_LISTENER: {
        severity: 'error',
        condition: 'Event listeners are managed',
        message: 'addEventListener() without managed cleanup causes memory leaks',
        remediation: 'Replace with this.addManagedListener() for automatic cleanup',
        factoryMethod: 'addManagedListener()',
    },
    RENDER_WITHOUT_CLEAR: {
        severity: 'error',
        condition: 'render() clears listeners before adding new ones',
        message: 'render() method adds listeners without calling clearManagedListeners() first',
        remediation: 'Add this.clearManagedListeners() at the start of render()',
        factoryMethod: 'clearManagedListeners()',
    },
    SHOULD_EXTEND_BASE_RENDER: {
        severity: 'warning',
        condition: 'Component uses BaseRenderComponent for render lifecycle',
        message: 'Component has render pattern but extends BaseEventComponent',
        remediation: 'Migrate to BaseRenderComponent for automatic lifecycle management',
        factoryMethod: 'BaseRenderComponent',
    },
    CLEANUP_IN_HIDE: {
        severity: 'error',
        condition: 'hide() uses reset() not cleanup()',
        message: 'hide() calls cleanup() preventing re-show - should use reset()',
        remediation: 'Replace this.cleanup() with this.reset() in hide() method',
        factoryMethod: 'reset()',
    },
};

// =============================================================================
// AST Helpers
// =============================================================================

/**
 * Check if class extends BaseEventComponent
 * @param {ts.ClassDeclaration} node
 * @returns {boolean}
 */
function extendsBaseEventComponent(node) {
    if (!node.heritageClauses) return false;
    for (const clause of node.heritageClauses) {
        if (clause.token === ts.SyntaxKind.ExtendsKeyword) {
            for (const type of clause.types) {
                const typeName = type.expression.getText();
                if (typeName === 'BaseEventComponent') {
                    return true;
                }
            }
        }
    }
    return false;
}

/**
 * Check if class extends BaseRenderComponent
 * @param {ts.ClassDeclaration} node
 * @returns {boolean}
 */
function extendsBaseRenderComponent(node) {
    if (!node.heritageClauses) return false;
    for (const clause of node.heritageClauses) {
        if (clause.token === ts.SyntaxKind.ExtendsKeyword) {
            for (const type of clause.types) {
                const typeName = type.expression.getText();
                if (typeName === 'BaseRenderComponent') {
                    return true;
                }
            }
        }
    }
    return false;
}

/**
 * Find render method in a class
 * @param {ts.ClassDeclaration} classNode
 * @returns {ts.MethodDeclaration|null}
 */
function findRenderMethod(classNode) {
    for (const member of classNode.members) {
        if (ts.isMethodDeclaration(member)) {
            const name = member.name.getText();
            if (name === 'render') {
                return member;
            }
        }
    }
    return null;
}

/**
 * Find hide method in a class
 * @param {ts.ClassDeclaration} classNode
 * @returns {ts.MethodDeclaration|null}
 */
function findHideMethod(classNode) {
    for (const member of classNode.members) {
        if (ts.isMethodDeclaration(member)) {
            const name = member.name.getText();
            if (name === 'hide') {
                return member;
            }
        }
    }
    return null;
}

/**
 * Check if method body starts with clearManagedListeners call
 * @param {ts.MethodDeclaration} method
 * @param {ts.SourceFile} sourceFile
 * @returns {boolean}
 */
function startsWithClearListeners(method, sourceFile) {
    if (!method.body) return false;

    const statements = method.body.statements;
    if (statements.length === 0) return false;

    // Check first 3 statements for clearManagedListeners
    for (let i = 0; i < Math.min(3, statements.length); i++) {
        const stmt = statements[i];
        const text = stmt.getText(sourceFile);
        if (text.includes('clearManagedListeners')) {
            return true;
        }
    }
    return false;
}

/**
 * Check if method body contains cleanup() call
 * @param {ts.MethodDeclaration} method
 * @param {ts.SourceFile} sourceFile
 * @returns {boolean}
 */
function containsCleanupCall(method, sourceFile) {
    if (!method.body) return false;

    let found = false;
    walkAst(method.body, (node, sf) => {
        if (ts.isCallExpression(node)) {
            const expr = node.expression;
            if (ts.isPropertyAccessExpression(expr)) {
                if (expr.name.getText(sf) === 'cleanup') {
                    found = true;
                }
            }
        }
    }, sourceFile);

    return found;
}

/**
 * Check if method body contains addManagedListener call
 * @param {ts.MethodDeclaration} method
 * @param {ts.SourceFile} sourceFile
 * @returns {boolean}
 */
function containsAddManagedListener(method, sourceFile) {
    if (!method.body) return false;

    let found = false;
    walkAst(method.body, (node, sf) => {
        if (isAddManagedListener(node)) {
            found = true;
        }
    }, sourceFile);

    return found;
}

// =============================================================================
// File Scanner
// =============================================================================

/**
 * Scan a single file for violations
 * @param {string} filePath
 * @param {string} basePath
 * @returns {object[]}
 */
function scanFile(filePath, basePath) {
    const violations = [];
    const sourceFile = parseFile(filePath);
    const relativePath = getRelativePath(filePath, basePath);

    // Track classes that need analysis
    const classesToAnalyze = [];

    // First pass: find classes and addEventListener calls
    walkAst(sourceFile, (node, sf) => {
        // Track class declarations
        if (ts.isClassDeclaration(node) && node.name) {
            classesToAnalyze.push(node);
        }

        // Check for unmanaged addEventListener
        if (isAddEventListener(node)) {
            const containingClass = getContainingClass(node);
            const containingMethod = getContainingMethod(node);

            // Skip if inside a class that extends BaseEventComponent/BaseRenderComponent
            // (they should use addManagedListener instead)
            if (containingClass) {
                const extendsManaged = extendsBaseEventComponent(containingClass) ||
                    extendsBaseRenderComponent(containingClass);

                if (extendsManaged) {
                    const line = getLineNumber(sf, node.getStart());
                    const snippet = getNodeText(node, sf);

                    violations.push({
                        file: relativePath,
                        line,
                        type: 'UNMANAGED_LISTENER',
                        status: 'FAILED',
                        className: containingClass.name?.getText(sf),
                        methodName: containingMethod?.name?.getText(sf),
                        ...VIOLATION_TYPES.UNMANAGED_LISTENER,
                        snippet: snippet.length > 100 ? snippet.substring(0, 100) + '...' : snippet,
                        suggestedFix: 'this.addManagedListener(element, event, handler)',
                    });
                }
            }
        }
    }, sourceFile);

    // Second pass: analyze classes
    for (const classNode of classesToAnalyze) {
        const className = classNode.name?.getText(sourceFile);

        // Only analyze BaseEventComponent subclasses
        if (!extendsBaseEventComponent(classNode)) continue;
        // Skip if already using BaseRenderComponent
        if (extendsBaseRenderComponent(classNode)) continue;

        const renderMethod = findRenderMethod(classNode);
        const hideMethod = findHideMethod(classNode);

        // Check render method pattern
        if (renderMethod) {
            const hasAddManagedListener = containsAddManagedListener(renderMethod, sourceFile);
            const startsWithClear = startsWithClearListeners(renderMethod, sourceFile);

            if (hasAddManagedListener && !startsWithClear) {
                const line = getLineNumber(sourceFile, renderMethod.getStart());

                violations.push({
                    file: relativePath,
                    line,
                    type: 'RENDER_WITHOUT_CLEAR',
                    status: 'FAILED',
                    className,
                    methodName: 'render',
                    ...VIOLATION_TYPES.RENDER_WITHOUT_CLEAR,
                    snippet: `${className}.render() adds listeners without clearManagedListeners()`,
                    suggestedFix: `render() {
    this.clearManagedListeners(); // Add this line
    // ... rest of render
}`,
                });
            }

            // Suggest migration to BaseRenderComponent
            violations.push({
                file: relativePath,
                line: getLineNumber(sourceFile, classNode.getStart()),
                type: 'SHOULD_EXTEND_BASE_RENDER',
                status: 'FAILED',
                className,
                ...VIOLATION_TYPES.SHOULD_EXTEND_BASE_RENDER,
                snippet: `class ${className} extends BaseEventComponent`,
                suggestedFix: `class ${className} extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}`,
            });
        }

        // Check hide method for cleanup() misuse
        if (hideMethod) {
            const hasCleanup = containsCleanupCall(hideMethod, sourceFile);

            if (hasCleanup) {
                const line = getLineNumber(sourceFile, hideMethod.getStart());

                violations.push({
                    file: relativePath,
                    line,
                    type: 'CLEANUP_IN_HIDE',
                    status: 'FAILED',
                    className,
                    methodName: 'hide',
                    ...VIOLATION_TYPES.CLEANUP_IN_HIDE,
                    snippet: `${className}.hide() calls cleanup() preventing re-show`,
                    suggestedFix: `hide() {
    this.reset(); // Use reset() instead of cleanup()
}`,
                });
            }
        }
    }

    return violations;
}

// =============================================================================
// Main
// =============================================================================

function main() {
    console.log('\nListener Pattern AST Scanner');
    console.log('============================');
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

    // Sort files
    const sortedFiles = Object.entries(fileStats)
        .sort((a, b) => b[1].total - a[1].total);

    // Recommendations
    const recommendations = [
        `Fix ${byType.UNMANAGED_LISTENER || 0} unmanaged addEventListener calls`,
        `Add clearManagedListeners() to ${byType.RENDER_WITHOUT_CLEAR || 0} render methods`,
        `Fix ${byType.CLEANUP_IN_HIDE || 0} cleanup() misuses in hide() methods`,
        `Consider migrating ${byType.SHOULD_EXTEND_BASE_RENDER || 0} components to BaseRenderComponent`,
        'Run render-pattern-scanner.cjs for detailed pattern analysis',
    ];

    // Migration phases
    const migrationPhases = [
        { name: 'Phase 1: Unmanaged Listeners', count: byType.UNMANAGED_LISTENER || 0, factoryMethod: 'addManagedListener()', priority: 1 },
        { name: 'Phase 2: Render Pattern Fix', count: byType.RENDER_WITHOUT_CLEAR || 0, factoryMethod: 'clearManagedListeners()', priority: 2 },
        { name: 'Phase 3: Hide Pattern Fix', count: byType.CLEANUP_IN_HIDE || 0, factoryMethod: 'reset()', priority: 3 },
        { name: 'Phase 4: Base Class Migration', count: byType.SHOULD_EXTEND_BASE_RENDER || 0, factoryMethod: 'BaseRenderComponent', priority: 4 },
    ];

    // Write reports
    const reportPath = path.join(REPORTS_DIR, REPORT_NAME);
    writePAGReport(reportPath, {
        name: 'listener-pattern-remediation',
        description: 'Event listener pattern violations requiring remediation',
        intent: 'Ensure all event listeners have managed lifecycle cleanup',
        objective: 'Zero unmanaged listeners, proper render patterns',
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
