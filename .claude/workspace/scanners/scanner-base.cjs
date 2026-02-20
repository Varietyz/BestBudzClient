#!/usr/bin/env node
/**
 * Scanner Base Utilities
 *
 * Shared utilities for all AST scanners including:
 * - TypeScript parsing via compiler API
 * - PAG-formatted remediation log generation
 * - File traversal and filtering
 *
 * @module scanner-base
 */

const ts = require('typescript');
const fs = require('fs');
const path = require('path');
const glob = require('glob');

// =============================================================================
// TypeScript AST Utilities
// =============================================================================

/**
 * Parse a TypeScript file into an AST
 * @param {string} filePath - Absolute path to the file
 * @returns {ts.SourceFile} Parsed source file
 */
function parseFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    return ts.createSourceFile(
        filePath,
        content,
        ts.ScriptTarget.Latest,
        true,
        ts.ScriptKind.TS
    );
}

/**
 * Get line number from position in source file
 * @param {ts.SourceFile} sourceFile
 * @param {number} pos
 * @returns {number}
 */
function getLineNumber(sourceFile, pos) {
    return sourceFile.getLineAndCharacterOfPosition(pos).line + 1;
}

/**
 * Get the full text of a node
 * @param {ts.Node} node
 * @param {ts.SourceFile} sourceFile
 * @returns {string}
 */
function getNodeText(node, sourceFile) {
    return node.getText(sourceFile);
}

/**
 * Walk AST and apply visitor to all nodes
 * @param {ts.Node} node
 * @param {function} visitor - (node, sourceFile) => void
 * @param {ts.SourceFile} sourceFile
 */
function walkAst(node, visitor, sourceFile) {
    visitor(node, sourceFile);
    ts.forEachChild(node, child => walkAst(child, visitor, sourceFile));
}

/**
 * Check if node is an innerHTML assignment
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isInnerHTMLAssignment(node) {
    if (ts.isBinaryExpression(node) &&
        node.operatorToken.kind === ts.SyntaxKind.EqualsToken) {
        const left = node.left;
        if (ts.isPropertyAccessExpression(left)) {
            return left.name.text === 'innerHTML';
        }
    }
    return false;
}

/**
 * Check if node is a document.createElement call
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isDocumentCreateElement(node) {
    if (ts.isCallExpression(node)) {
        const expr = node.expression;
        if (ts.isPropertyAccessExpression(expr)) {
            if (expr.name.text === 'createElement') {
                const obj = expr.expression;
                if (ts.isIdentifier(obj) && obj.text === 'document') {
                    return true;
                }
            }
        }
    }
    return false;
}

/**
 * Check if node is a DOM.createElement call (compliant)
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isDOMFactoryCall(node) {
    if (ts.isCallExpression(node)) {
        const expr = node.expression;
        if (ts.isPropertyAccessExpression(expr)) {
            const obj = expr.expression;
            if (ts.isIdentifier(obj) && obj.text === 'DOM') {
                return true;
            }
        }
    }
    return false;
}

/**
 * Check if node is addEventListener call
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isAddEventListener(node) {
    if (ts.isCallExpression(node)) {
        const expr = node.expression;
        if (ts.isPropertyAccessExpression(expr)) {
            return expr.name.text === 'addEventListener';
        }
    }
    return false;
}

/**
 * Check if node is addManagedListener call (compliant)
 * @param {ts.Node} node
 * @returns {boolean}
 */
function isAddManagedListener(node) {
    if (ts.isCallExpression(node)) {
        const expr = node.expression;
        if (ts.isPropertyAccessExpression(expr)) {
            return expr.name.text === 'addManagedListener';
        }
    }
    return false;
}

/**
 * Get class declaration containing a node
 * @param {ts.Node} node
 * @returns {ts.ClassDeclaration|null}
 */
function getContainingClass(node) {
    let current = node.parent;
    while (current) {
        if (ts.isClassDeclaration(current)) {
            return current;
        }
        current = current.parent;
    }
    return null;
}

/**
 * Get method declaration containing a node
 * @param {ts.Node} node
 * @returns {ts.MethodDeclaration|null}
 */
function getContainingMethod(node) {
    let current = node.parent;
    while (current) {
        if (ts.isMethodDeclaration(current)) {
            return current;
        }
        current = current.parent;
    }
    return null;
}

// =============================================================================
// File Utilities
// =============================================================================

/**
 * Find all TypeScript files in a directory
 * @param {string} srcDir - Source directory
 * @param {string[]} excludePatterns - Patterns to exclude
 * @returns {string[]} Array of file paths
 */
function findTypeScriptFiles(srcDir, excludePatterns = []) {
    const pattern = path.join(srcDir, '**/*.ts').replace(/\\/g, '/');
    const defaultExcludes = ['**/node_modules/**', '**/*.d.ts', '**/dist/**'];

    return glob.sync(pattern, {
        ignore: [...defaultExcludes, ...excludePatterns],
    });
}

/**
 * Get relative path from base
 * @param {string} filePath
 * @param {string} basePath
 * @returns {string}
 */
function getRelativePath(filePath, basePath) {
    return path.relative(basePath, filePath).replace(/\\/g, '/');
}

// =============================================================================
// PAG Report Generation
// =============================================================================

/**
 * Create a PAG-formatted remediation report
 * @param {object} config - Report configuration
 * @returns {string} PAG-formatted markdown
 */
function createPAGReport(config) {
    const {
        name,
        type = 'checklist',
        version = '1.0.0',
        description,
        intent,
        objective,
        violations,
        recommendations,
        migrationPhases,
    } = config;

    const timestamp = new Date().toISOString();
    const passed = violations.filter(v => v.status === 'PASSED').length;
    const failed = violations.filter(v => v.status === 'FAILED').length;
    const passRate = violations.length > 0
        ? Math.round((passed / violations.length) * 100)
        : 100;

    let report = `---
name: ${name}
type: ${type}
version: ${version}
generated: ${timestamp}
---

THIS CHECKLIST TRACKS ${description}

%% META %%:
intent: "${intent}"
objective: "${objective}"

## Validation Summary

**Total Gates**: ${violations.length}
**Passed**: ${passed}
**Failed**: ${failed}
**Pass Rate**: ${passRate}%

`;

    // Group violations by file
    const byFile = {};
    for (const v of violations) {
        if (!byFile[v.file]) {
            byFile[v.file] = [];
        }
        byFile[v.file].push(v);
    }

    // Generate phase sections
    let phaseNum = 1;
    for (const [file, fileViolations] of Object.entries(byFile)) {
        const errorCount = fileViolations.filter(v => v.severity === 'error').length;
        const warnCount = fileViolations.filter(v => v.severity === 'warning').length;

        report += `## Phase ${phaseNum}: ${file}

`;

        let gateNum = 1;
        for (const v of fileViolations) {
            const status = v.status === 'PASSED' ? '✅ PASSED' : '❌ FAILED';
            const marker = v.status === 'PASSED' ? '[x]' : '[ ]';

            report += `### Gate ${phaseNum}.${gateNum}: ${v.type}
- **Status**: ${status}
- **Line**: ${v.line}
- **Severity**: ${v.severity}
- **Conditions**:
  - ${marker} ${v.condition || 'Compliance check'}
`;

            if (v.status === 'FAILED') {
                report += `- **Failure Reason**: ${v.message}
- **Remediation**: ${v.remediation}
`;
                if (v.snippet) {
                    report += `- **Current Code**:
\`\`\`typescript
${v.snippet}
\`\`\`
`;
                }
                if (v.suggestedFix) {
                    report += `- **Suggested Fix**:
\`\`\`typescript
${v.suggestedFix}
\`\`\`
`;
                }
            }

            report += `
`;
            gateNum++;
        }

        phaseNum++;
    }

    // Add recommendations
    if (recommendations && recommendations.length > 0) {
        report += `## Recommendations

`;
        for (let i = 0; i < recommendations.length; i++) {
            report += `${i + 1}. ${recommendations[i]}
`;
        }
        report += `
`;
    }

    // Add migration phases if provided
    if (migrationPhases && migrationPhases.length > 0) {
        report += `## Migration Phases

`;
        for (const phase of migrationPhases) {
            report += `### ${phase.name}
- **Count**: ${phase.count} violations
- **Factory Method**: ${phase.factoryMethod}
- **Priority**: ${phase.priority}

`;
        }
    }

    // Add ALWAYS/NEVER rules
    report += `ALWAYS use DOM factory methods for element creation
ALWAYS use addManagedListener for event binding
ALWAYS use CSS tokens instead of hardcoded values
NEVER use document.createElement directly
NEVER use innerHTML for icon assignment
NEVER add event listeners without managed cleanup
`;

    return report;
}

/**
 * Write PAG report to file
 * @param {string} reportPath
 * @param {object} config
 */
function writePAGReport(reportPath, config) {
    const report = createPAGReport(config);
    const dir = path.dirname(reportPath);
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(reportPath, report);
}

/**
 * Write JSON report alongside PAG report
 * @param {string} reportPath
 * @param {object} data
 */
function writeJSONReport(reportPath, data) {
    const jsonPath = reportPath.replace(/\.md$/, '.json');
    const dir = path.dirname(jsonPath);
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(jsonPath, JSON.stringify(data, null, 2));
}

// =============================================================================
// Exports
// =============================================================================

module.exports = {
    // TypeScript AST
    ts,
    parseFile,
    getLineNumber,
    getNodeText,
    walkAst,
    isInnerHTMLAssignment,
    isDocumentCreateElement,
    isDOMFactoryCall,
    isAddEventListener,
    isAddManagedListener,
    getContainingClass,
    getContainingMethod,

    // File utilities
    findTypeScriptFiles,
    getRelativePath,

    // PAG reporting
    createPAGReport,
    writePAGReport,
    writeJSONReport,
};
