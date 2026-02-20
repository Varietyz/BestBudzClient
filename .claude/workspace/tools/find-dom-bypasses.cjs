#!/usr/bin/env node
/**
 * DOM Bypass Scanner
 *
 * AST-traverses TypeScript/JavaScript files to find DOM operations
 * that bypass the pooling system.
 *
 * Detects:
 * - document.createElement() calls
 * - .innerHTML assignments
 * - .outerHTML assignments
 * - insertAdjacentHTML() calls
 * - document.createTextNode() (informational)
 *
 * Usage: node find-dom-bypasses.js [--output=bypasses.json] [--path=src/renderer]
 */

const fs = require('fs');
const path = require('path');
const ts = require('typescript');

// =============================================================================
// Configuration
// =============================================================================

const DEFAULT_SCAN_PATH = 'root/archlab-ide/src/renderer';
const DEFAULT_OUTPUT = '.claude/workspace/tools/dom-bypasses.json';

// Patterns to detect
const BYPASS_PATTERNS = {
    'document.createElement': {
        severity: 'error',
        description: 'Direct element creation bypasses pool',
        fix: 'Use DOM.createElement() instead'
    },
    'innerHTML': {
        severity: 'error',
        description: 'innerHTML assignment may create untracked elements',
        fix: 'Use DOM.createElement() + appendChild() or ensure no element creation'
    },
    'outerHTML': {
        severity: 'error',
        description: 'outerHTML replacement bypasses pool',
        fix: 'Use DOM methods to replace elements'
    },
    'insertAdjacentHTML': {
        severity: 'warning',
        description: 'insertAdjacentHTML creates untracked elements',
        fix: 'Use DOM.createElement() + insertAdjacent methods'
    },
    'document.createTextNode': {
        severity: 'info',
        description: 'Text nodes are not pooled (usually acceptable)',
        fix: 'Consider if text content could use textContent property instead'
    },
    'document.createDocumentFragment': {
        severity: 'info',
        description: 'Document fragments are not pooled',
        fix: 'Acceptable for batch operations, ensure children are pooled'
    }
};

// Files/patterns to exclude
const EXCLUDE_PATTERNS = [
    /node_modules/,
    /\.d\.ts$/,
    /\.test\.ts$/,
    /\.spec\.ts$/,
    /dom-factory\.ts$/,  // The DOM factory itself is allowed
    /element-pool\.ts$/, // The pool implementation is allowed
];

// =============================================================================
// AST Traversal
// =============================================================================

function shouldExclude(filePath) {
    return EXCLUDE_PATTERNS.some(pattern => pattern.test(filePath));
}

function getLineNumber(sourceFile, pos) {
    const { line } = sourceFile.getLineAndCharacterOfPosition(pos);
    return line + 1; // 1-indexed
}

function getCodeSnippet(sourceFile, node) {
    const start = node.getStart(sourceFile);
    const end = node.getEnd();
    const text = sourceFile.text.substring(start, end);
    // Truncate long snippets
    return text.length > 80 ? text.substring(0, 77) + '...' : text;
}

function findBypasses(sourceFile, filePath) {
    const bypasses = [];

    function visit(node) {
        // Check for document.createElement()
        if (ts.isCallExpression(node)) {
            const expr = node.expression;

            // document.createElement(...)
            if (ts.isPropertyAccessExpression(expr) &&
                ts.isIdentifier(expr.expression) &&
                expr.expression.text === 'document' &&
                ts.isIdentifier(expr.name) &&
                expr.name.text === 'createElement') {

                const pattern = BYPASS_PATTERNS['document.createElement'];
                bypasses.push({
                    file: filePath,
                    line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                    type: 'document.createElement',
                    severity: pattern.severity,
                    description: pattern.description,
                    fix: pattern.fix,
                    snippet: getCodeSnippet(sourceFile, node)
                });
            }

            // document.createTextNode(...)
            if (ts.isPropertyAccessExpression(expr) &&
                ts.isIdentifier(expr.expression) &&
                expr.expression.text === 'document' &&
                ts.isIdentifier(expr.name) &&
                expr.name.text === 'createTextNode') {

                const pattern = BYPASS_PATTERNS['document.createTextNode'];
                bypasses.push({
                    file: filePath,
                    line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                    type: 'document.createTextNode',
                    severity: pattern.severity,
                    description: pattern.description,
                    fix: pattern.fix,
                    snippet: getCodeSnippet(sourceFile, node)
                });
            }

            // document.createDocumentFragment()
            if (ts.isPropertyAccessExpression(expr) &&
                ts.isIdentifier(expr.expression) &&
                expr.expression.text === 'document' &&
                ts.isIdentifier(expr.name) &&
                expr.name.text === 'createDocumentFragment') {

                const pattern = BYPASS_PATTERNS['document.createDocumentFragment'];
                bypasses.push({
                    file: filePath,
                    line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                    type: 'document.createDocumentFragment',
                    severity: pattern.severity,
                    description: pattern.description,
                    fix: pattern.fix,
                    snippet: getCodeSnippet(sourceFile, node)
                });
            }

            // .insertAdjacentHTML(...)
            if (ts.isPropertyAccessExpression(expr) &&
                ts.isIdentifier(expr.name) &&
                expr.name.text === 'insertAdjacentHTML') {

                const pattern = BYPASS_PATTERNS['insertAdjacentHTML'];
                bypasses.push({
                    file: filePath,
                    line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                    type: 'insertAdjacentHTML',
                    severity: pattern.severity,
                    description: pattern.description,
                    fix: pattern.fix,
                    snippet: getCodeSnippet(sourceFile, node)
                });
            }
        }

        // Check for innerHTML/outerHTML assignments
        if (ts.isBinaryExpression(node) &&
            node.operatorToken.kind === ts.SyntaxKind.EqualsToken) {

            const left = node.left;
            if (ts.isPropertyAccessExpression(left) && ts.isIdentifier(left.name)) {
                const propName = left.name.text;

                if (propName === 'innerHTML') {
                    const pattern = BYPASS_PATTERNS['innerHTML'];
                    bypasses.push({
                        file: filePath,
                        line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                        type: 'innerHTML',
                        severity: pattern.severity,
                        description: pattern.description,
                        fix: pattern.fix,
                        snippet: getCodeSnippet(sourceFile, node)
                    });
                }

                if (propName === 'outerHTML') {
                    const pattern = BYPASS_PATTERNS['outerHTML'];
                    bypasses.push({
                        file: filePath,
                        line: getLineNumber(sourceFile, node.getStart(sourceFile)),
                        type: 'outerHTML',
                        severity: pattern.severity,
                        description: pattern.description,
                        fix: pattern.fix,
                        snippet: getCodeSnippet(sourceFile, node)
                    });
                }
            }
        }

        ts.forEachChild(node, visit);
    }

    visit(sourceFile);
    return bypasses;
}

// =============================================================================
// File System Scanning
// =============================================================================

function findTypeScriptFiles(dir, files = []) {
    if (!fs.existsSync(dir)) {
        console.error(`Directory not found: ${dir}`);
        return files;
    }

    const entries = fs.readdirSync(dir, { withFileTypes: true });

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);

        if (entry.isDirectory()) {
            findTypeScriptFiles(fullPath, files);
        } else if (entry.isFile() && /\.(ts|tsx|js|jsx)$/.test(entry.name)) {
            if (!shouldExclude(fullPath)) {
                files.push(fullPath);
            }
        }
    }

    return files;
}

function scanFile(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf-8');
        const sourceFile = ts.createSourceFile(
            filePath,
            content,
            ts.ScriptTarget.Latest,
            true,
            filePath.endsWith('.tsx') ? ts.ScriptKind.TSX : ts.ScriptKind.TS
        );

        return findBypasses(sourceFile, filePath);
    } catch (error) {
        console.error(`Error scanning ${filePath}:`, error.message);
        return [];
    }
}

// =============================================================================
// Main
// =============================================================================

function main() {
    // Parse arguments
    const args = process.argv.slice(2);
    let scanPath = DEFAULT_SCAN_PATH;
    let outputPath = DEFAULT_OUTPUT;

    for (const arg of args) {
        if (arg.startsWith('--path=')) {
            scanPath = arg.split('=')[1];
        } else if (arg.startsWith('--output=')) {
            outputPath = arg.split('=')[1];
        } else if (arg === '--help' || arg === '-h') {
            console.log(`
DOM Bypass Scanner

Usage: node find-dom-bypasses.js [options]

Options:
  --path=<dir>     Directory to scan (default: ${DEFAULT_SCAN_PATH})
  --output=<file>  Output JSON file (default: ${DEFAULT_OUTPUT})
  --help, -h       Show this help

Output JSON structure:
{
  "scannedAt": "ISO timestamp",
  "scanPath": "path scanned",
  "summary": {
    "totalFiles": N,
    "totalBypasses": N,
    "byType": { "document.createElement": N, ... },
    "bySeverity": { "error": N, "warning": N, "info": N }
  },
  "bypasses": [
    {
      "file": "path/to/file.ts",
      "line": 123,
      "type": "document.createElement",
      "severity": "error",
      "description": "...",
      "fix": "...",
      "snippet": "document.createElement('div')"
    }
  ]
}
`);
            process.exit(0);
        }
    }

    console.log(`\nDOM Bypass Scanner`);
    console.log(`==================`);
    console.log(`Scan path: ${scanPath}`);
    console.log(`Output: ${outputPath}\n`);

    // Find all TypeScript files
    console.log('Finding TypeScript files...');
    const files = findTypeScriptFiles(scanPath);
    console.log(`Found ${files.length} files to scan\n`);

    // Scan each file
    console.log('Scanning for DOM bypasses...');
    const allBypasses = [];
    let filesWithBypasses = 0;

    for (const file of files) {
        const bypasses = scanFile(file);
        if (bypasses.length > 0) {
            filesWithBypasses++;
            allBypasses.push(...bypasses);
        }
    }

    // Calculate summary
    const byType = {};
    const bySeverity = { error: 0, warning: 0, info: 0 };

    for (const bypass of allBypasses) {
        byType[bypass.type] = (byType[bypass.type] || 0) + 1;
        bySeverity[bypass.severity]++;
    }

    // Build output
    const output = {
        scannedAt: new Date().toISOString(),
        scanPath: scanPath,
        summary: {
            totalFiles: files.length,
            filesWithBypasses: filesWithBypasses,
            totalBypasses: allBypasses.length,
            byType: byType,
            bySeverity: bySeverity
        },
        bypasses: allBypasses.sort((a, b) => {
            // Sort by severity (error first), then by file, then by line
            const severityOrder = { error: 0, warning: 1, info: 2 };
            if (severityOrder[a.severity] !== severityOrder[b.severity]) {
                return severityOrder[a.severity] - severityOrder[b.severity];
            }
            if (a.file !== b.file) {
                return a.file.localeCompare(b.file);
            }
            return a.line - b.line;
        })
    };

    // Write output
    const outputDir = path.dirname(outputPath);
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
    }

    fs.writeFileSync(outputPath, JSON.stringify(output, null, 2));

    // Print summary
    console.log(`\nScan Complete!`);
    console.log(`==============`);
    console.log(`Files scanned: ${files.length}`);
    console.log(`Files with bypasses: ${filesWithBypasses}`);
    console.log(`Total bypasses found: ${allBypasses.length}`);
    console.log(`\nBy Type:`);
    for (const [type, count] of Object.entries(byType)) {
        console.log(`  ${type}: ${count}`);
    }
    console.log(`\nBy Severity:`);
    console.log(`  error: ${bySeverity.error} (must fix)`);
    console.log(`  warning: ${bySeverity.warning} (should fix)`);
    console.log(`  info: ${bySeverity.info} (review)`);
    console.log(`\nOutput written to: ${outputPath}`);

    // Exit with error code if errors found
    if (bySeverity.error > 0) {
        console.log(`\n⚠️  ${bySeverity.error} error-level bypasses require fixing before v2`);
        process.exit(1);
    }
}

main();
