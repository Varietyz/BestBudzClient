/**
 * Hardcoded Value Detector - AST-based analysis tool
 *
 * Detects hardcoded values that should be configurable via settings.json:
 * - String literals (paths, patterns, directory names)
 * - Numeric literals (thresholds, limits, magic numbers)
 * - Array literals (extension lists, pattern arrays)
 * - Object literals (configuration objects)
 * - Comparison operations with literals
 * - Method calls with literal arguments (.includes(), .startsWith(), etc.)
 */

import * as acorn from "acorn";
import { promises as fs } from "fs";
import path from "path";

// Patterns that are likely configuration candidates
const CONFIG_PATTERNS = {
    // Path-like strings
    pathStrings: /^(\.\.\/|\.\/|\/|[a-z]+\/|[a-z]+-[a-z]+\/)/i,
    // Directory names
    directoryNames: /^(pages|components|shared|config|constants|styles|assets|utils|helpers|core|validators|scanners|auto-fix)$/i,
    // File extensions
    fileExtensions: /^\.[a-z]{2,4}$/i,
    // CSS-related patterns
    cssPatterns: /^(--[a-z-]+|\.?[a-z]+-[a-z]+|@media|@keyframes)/i,
    // Project-specific paths
    projectPaths: /^(beasr-|archlab-|frontend|backend|dashboard)/i,
};

// Values that are NOT configuration candidates (algorithm constants)
const IGNORE_PATTERNS = {
    // Common algorithm values
    numbers: new Set([0, 1, -1, 2, 10, 16, 100, 1000]),
    // Encoding strings
    strings: new Set(["utf-8", "utf8", "base64", "hex", "ascii", "binary"]),
    // Standard keywords
    keywords: new Set(["true", "false", "null", "undefined", "NaN", "Infinity"]),
    // Regex-like patterns (CSS/JS syntax)
    syntaxPatterns: /^(\^|\$|\[|\]|\(|\)|\||\*|\+|\?|\\)/,
};

class HardcodedValueDetector {
    constructor() {
        this.findings = [];
        this.stats = {
            filesScanned: 0,
            stringsFound: 0,
            numbersFound: 0,
            arraysFound: 0,
            objectsFound: 0,
            comparisonsFound: 0,
            methodCallsFound: 0,
        };
    }

    async scanDirectory(dirPath, options = {}) {
        const {
            extensions = [".js"],
            exclude = ["node_modules", ".git", "dist", "build"],
            verbose = false
        } = options;

        const files = await this.getFiles(dirPath, extensions, exclude);

        for (const file of files) {
            await this.scanFile(file, verbose);
        }

        return this.generateReport();
    }

    async getFiles(dirPath, extensions, exclude) {
        const files = [];

        async function walk(dir) {
            const entries = await fs.readdir(dir, { withFileTypes: true });

            for (const entry of entries) {
                const fullPath = path.join(dir, entry.name);

                if (entry.isDirectory()) {
                    if (!exclude.includes(entry.name)) {
                        await walk(fullPath);
                    }
                } else if (entry.isFile()) {
                    if (extensions.some(ext => entry.name.endsWith(ext))) {
                        files.push(fullPath);
                    }
                }
            }
        }

        await walk(dirPath);
        return files;
    }

    async scanFile(filePath, verbose) {
        try {
            const content = await fs.readFile(filePath, "utf-8");
            const ast = acorn.parse(content, {
                ecmaVersion: "latest",
                sourceType: "module",
                locations: true,
            });

            this.stats.filesScanned++;
            this.walkNode(ast, filePath, content);

            if (verbose) {
                console.log(`Scanned: ${filePath}`);
            }
        } catch (error) {
            console.error(`Error parsing ${filePath}: ${error.message}`);
        }
    }

    walkNode(node, filePath, content, parent = null, parentKey = null) {
        if (!node || typeof node !== "object") return;

        // Check for string literals
        if (node.type === "Literal" && typeof node.value === "string") {
            this.checkStringLiteral(node, filePath, content, parent, parentKey);
        }

        // Check for numeric literals
        if (node.type === "Literal" && typeof node.value === "number") {
            this.checkNumericLiteral(node, filePath, content, parent, parentKey);
        }

        // Check for array expressions
        if (node.type === "ArrayExpression") {
            this.checkArrayExpression(node, filePath, content, parent);
        }

        // Check for object expressions (configuration objects)
        if (node.type === "ObjectExpression") {
            this.checkObjectExpression(node, filePath, content, parent);
        }

        // Check for binary expressions (comparisons)
        if (node.type === "BinaryExpression") {
            this.checkBinaryExpression(node, filePath, content);
        }

        // Check for method calls with literals
        if (node.type === "CallExpression") {
            this.checkCallExpression(node, filePath, content);
        }

        // Recurse into child nodes
        for (const key of Object.keys(node)) {
            if (key === "loc" || key === "range") continue;

            const child = node[key];
            if (Array.isArray(child)) {
                child.forEach(c => this.walkNode(c, filePath, content, node, key));
            } else if (child && typeof child === "object") {
                this.walkNode(child, filePath, content, node, key);
            }
        }
    }

    checkStringLiteral(node, filePath, content, parent, parentKey) {
        const value = node.value;

        // Skip empty strings and very short strings
        if (!value || value.length < 2) return;

        // Skip import/export sources
        if (parent?.type === "ImportDeclaration" || parent?.type === "ExportNamedDeclaration") return;
        if (parentKey === "source") return;

        // Skip ignored patterns
        if (IGNORE_PATTERNS.strings.has(value)) return;
        if (IGNORE_PATTERNS.syntaxPatterns.test(value)) return;

        // Check if this looks like a configurable value
        let category = null;
        let priority = "low";

        if (CONFIG_PATTERNS.pathStrings.test(value)) {
            category = "path";
            priority = "high";
        } else if (CONFIG_PATTERNS.directoryNames.test(value)) {
            category = "directory";
            priority = "high";
        } else if (CONFIG_PATTERNS.fileExtensions.test(value)) {
            category = "extension";
            priority = "medium";
        } else if (CONFIG_PATTERNS.cssPatterns.test(value)) {
            category = "css-pattern";
            priority = "medium";
        } else if (CONFIG_PATTERNS.projectPaths.test(value)) {
            category = "project-path";
            priority = "critical";
        } else if (value.includes("/") || value.includes("\\")) {
            category = "path-like";
            priority = "medium";
        }

        if (category) {
            this.stats.stringsFound++;
            this.findings.push({
                type: "string",
                category,
                priority,
                value,
                file: filePath,
                line: node.loc.start.line,
                column: node.loc.start.column,
                context: this.getContext(content, node.loc.start.line),
                suggestion: this.suggestAccessor(category, value),
            });
        }
    }

    checkNumericLiteral(node, filePath, content, parent, parentKey) {
        const value = node.value;

        // Skip common algorithm values
        if (IGNORE_PATTERNS.numbers.has(value)) return;

        // Skip array indices
        if (parent?.type === "MemberExpression" && parentKey === "property") return;

        // Detect threshold-like usage
        let category = null;
        let priority = "low";

        // Check parent context for comparison
        if (parent?.type === "BinaryExpression") {
            if ([">", "<", ">=", "<=", "===", "!=="].includes(parent.operator)) {
                category = "threshold";
                priority = "high";
            }
        }

        // Magic numbers (larger values are more suspicious)
        if (!category && value > 2 && value !== 10 && value !== 100) {
            category = "magic-number";
            priority = value > 10 ? "medium" : "low";
        }

        if (category) {
            this.stats.numbersFound++;
            this.findings.push({
                type: "number",
                category,
                priority,
                value,
                file: filePath,
                line: node.loc.start.line,
                column: node.loc.start.column,
                context: this.getContext(content, node.loc.start.line),
                suggestion: this.suggestAccessor(category, value),
            });
        }
    }

    checkArrayExpression(node, filePath, content, parent) {
        // Only check arrays assigned to variables
        if (parent?.type !== "VariableDeclarator") return;

        // Check if all elements are strings (pattern array)
        const elements = node.elements.filter(e => e !== null);
        if (elements.length === 0) return;

        const allStrings = elements.every(e => e.type === "Literal" && typeof e.value === "string");

        if (allStrings && elements.length >= 2) {
            const values = elements.map(e => e.value);

            // Determine category
            let category = "string-array";
            let priority = "medium";

            if (values.every(v => CONFIG_PATTERNS.fileExtensions.test(v))) {
                category = "extension-list";
                priority = "high";
            } else if (values.every(v => CONFIG_PATTERNS.directoryNames.test(v))) {
                category = "directory-list";
                priority = "high";
            }

            this.stats.arraysFound++;
            this.findings.push({
                type: "array",
                category,
                priority,
                value: values,
                file: filePath,
                line: node.loc.start.line,
                column: node.loc.start.column,
                context: this.getContext(content, node.loc.start.line),
                suggestion: `Move to settings.json and create getXxx() accessor`,
            });
        }
    }

    checkObjectExpression(node, filePath, content, parent) {
        // Only check objects assigned to variables with "config", "options", "settings" in name
        if (parent?.type !== "VariableDeclarator") return;

        const varName = parent.id?.name?.toLowerCase() || "";
        if (!varName.includes("config") && !varName.includes("option") &&
            !varName.includes("setting") && !varName.includes("default")) return;

        if (node.properties.length >= 2) {
            this.stats.objectsFound++;
            this.findings.push({
                type: "object",
                category: "config-object",
                priority: "high",
                value: `{${node.properties.length} properties}`,
                file: filePath,
                line: node.loc.start.line,
                column: node.loc.start.column,
                context: this.getContext(content, node.loc.start.line),
                suggestion: `Move to settings.json under appropriate section`,
            });
        }
    }

    checkBinaryExpression(node, filePath, content) {
        // Check for comparisons with literals
        if (![">", "<", ">=", "<=", "===", "!==", "==", "!="].includes(node.operator)) return;

        const hasLiteralRight = node.right.type === "Literal";
        const hasLiteralLeft = node.left.type === "Literal";

        if (hasLiteralRight && typeof node.right.value === "number") {
            const value = node.right.value;
            if (!IGNORE_PATTERNS.numbers.has(value)) {
                this.stats.comparisonsFound++;
                this.findings.push({
                    type: "comparison",
                    category: "threshold-comparison",
                    priority: "high",
                    value: `${node.operator} ${value}`,
                    file: filePath,
                    line: node.loc.start.line,
                    column: node.loc.start.column,
                    context: this.getContext(content, node.loc.start.line),
                    suggestion: `Extract ${value} to settings as a threshold value`,
                });
            }
        }
    }

    checkCallExpression(node, filePath, content) {
        // Check for .includes(), .startsWith(), .endsWith() with string literals
        if (node.callee.type !== "MemberExpression") return;

        const methodName = node.callee.property?.name;
        if (!["includes", "startsWith", "endsWith", "match", "test"].includes(methodName)) return;

        const arg = node.arguments[0];
        if (arg?.type !== "Literal" || typeof arg.value !== "string") return;

        const value = arg.value;

        // Check if this looks like a path/directory check
        if (CONFIG_PATTERNS.pathStrings.test(value) ||
            CONFIG_PATTERNS.directoryNames.test(value) ||
            CONFIG_PATTERNS.projectPaths.test(value) ||
            value.includes("/")) {

            this.stats.methodCallsFound++;
            this.findings.push({
                type: "method-call",
                category: `${methodName}-check`,
                priority: "critical",
                value: `.${methodName}("${value}")`,
                file: filePath,
                line: node.loc.start.line,
                column: node.loc.start.column,
                context: this.getContext(content, node.loc.start.line),
                suggestion: `Use isInDirectory() or settings accessor instead of hardcoded "${value}"`,
            });
        }
    }

    getContext(content, lineNumber) {
        const lines = content.split("\n");
        const line = lines[lineNumber - 1] || "";
        return line.trim().substring(0, 100);
    }

    suggestAccessor(category, value) {
        const suggestions = {
            "path": `Use getDirectoryPattern() or isInDirectory()`,
            "directory": `Use isInDirectory(path, "${value}")`,
            "extension": `Use getCodeExtensions() or getStyleExtensions()`,
            "css-pattern": `Add to settings.json css section`,
            "project-path": `Add to settings.json directoryPatterns`,
            "path-like": `Consider adding to directoryPatterns in settings`,
            "threshold": `Add getXxxThreshold() accessor`,
            "magic-number": `Extract to settings with descriptive name`,
        };
        return suggestions[category] || "Consider moving to settings.json";
    }

    generateReport() {
        // Sort by priority
        const priorityOrder = { critical: 0, high: 1, medium: 2, low: 3 };
        this.findings.sort((a, b) => priorityOrder[a.priority] - priorityOrder[b.priority]);

        // Group by file
        const byFile = {};
        for (const finding of this.findings) {
            const relPath = finding.file.replace(/\\/g, "/");
            if (!byFile[relPath]) byFile[relPath] = [];
            byFile[relPath].push(finding);
        }

        return {
            stats: this.stats,
            findings: this.findings,
            byFile,
            summary: {
                critical: this.findings.filter(f => f.priority === "critical").length,
                high: this.findings.filter(f => f.priority === "high").length,
                medium: this.findings.filter(f => f.priority === "medium").length,
                low: this.findings.filter(f => f.priority === "low").length,
            },
        };
    }
}

// Main execution
async function main() {
    const detector = new HardcodedValueDetector();

    const targetDir = process.argv[2] || "./root/codebase-validation";
    console.log(`\n🔍 Scanning: ${targetDir}\n`);

    const report = await detector.scanDirectory(targetDir, {
        extensions: [".js"],
        exclude: ["node_modules", ".git", "dist", "build", "registry"],
        verbose: false,
    });

    // Output summary
    console.log("═".repeat(80));
    console.log("HARDCODED VALUE ANALYSIS REPORT");
    console.log("═".repeat(80));
    console.log(`\nFiles scanned: ${report.stats.filesScanned}`);
    console.log(`\nFindings by type:`);
    console.log(`  Strings:     ${report.stats.stringsFound}`);
    console.log(`  Numbers:     ${report.stats.numbersFound}`);
    console.log(`  Arrays:      ${report.stats.arraysFound}`);
    console.log(`  Objects:     ${report.stats.objectsFound}`);
    console.log(`  Comparisons: ${report.stats.comparisonsFound}`);
    console.log(`  Method calls: ${report.stats.methodCallsFound}`);

    console.log(`\nFindings by priority:`);
    console.log(`  🔴 CRITICAL: ${report.summary.critical}`);
    console.log(`  🟠 HIGH:     ${report.summary.high}`);
    console.log(`  🟡 MEDIUM:   ${report.summary.medium}`);
    console.log(`  🟢 LOW:      ${report.summary.low}`);

    // Output critical and high priority findings
    console.log("\n" + "═".repeat(80));
    console.log("CRITICAL & HIGH PRIORITY FINDINGS");
    console.log("═".repeat(80));

    const criticalAndHigh = report.findings.filter(f =>
        f.priority === "critical" || f.priority === "high"
    );

    for (const finding of criticalAndHigh) {
        const relPath = finding.file.split("codebase-validation")[1] || finding.file;
        console.log(`\n[${finding.priority.toUpperCase()}] ${finding.type}: ${finding.category}`);
        console.log(`  File: ${relPath}:${finding.line}`);
        console.log(`  Value: ${JSON.stringify(finding.value)}`);
        console.log(`  Context: ${finding.context}`);
        console.log(`  Suggestion: ${finding.suggestion}`);
    }

    // Write full report to JSON
    const reportPath = "./root/codebase-validation/registry/hardcoded-values-report.json";
    await fs.writeFile(reportPath, JSON.stringify(report, null, 2));
    console.log(`\n📄 Full report written to: ${reportPath}`);
}

main().catch(console.error);
