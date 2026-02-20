#!/usr/bin/env node
// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

/**
 * Generic Auto-Fixer Runner with Dry-Run Preview + Confirmation
 *
 * Usage:
 *   node .claude/workspace/tools/run-auto-fixer.js <fixer-name> [--dry-run] [--yes]
 *
 * Examples:
 *   node .claude/workspace/tools/run-auto-fixer.js code-comments
 *   node .claude/workspace/tools/run-auto-fixer.js code-comments --dry-run
 *   node .claude/workspace/tools/run-auto-fixer.js json-stringify --yes
 */

import path from "path";
import { fileURLToPath } from "url";
import { promises as fs } from "fs";
import { createInterface } from "readline";
import { getDb } from "../../../root/codebase-validation/db/utils/connection.js";
import { Logger, PROJECT_PATHS } from "../../../root/codebase-validation/constants/index.js";
import { getLogIcon } from "../../../root/codebase-validation/core/settings/settings-manager.js";
import { ExclusionManager } from "../../../root/codebase-validation/core/exclusion-manager.js";
import { getFunctionalCommentPatterns, getAllowedInlineCommentPatterns } from "../../../root/codebase-validation/core/settings/settings-manager.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// =============================================================================
// Fixer Registry - Maps fixer names to scanner classes and fixer classes
// =============================================================================

const FIXER_REGISTRY = {
    // Code Quality Fixers
    "code-comments": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/code-comments-scanner.js",
        scannerClass: "CodeCommentsScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/code-comments-auto-fixer.js",
        fixerClass: "CodeCommentsAutoFixer",
        displayName: "Code Comments",
        description: "Remove non-functional comments while preserving licenses and docs",
        needsCustomScanner: true,
    },
    "json-stringify": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/json-stringify-scanner.js",
        scannerClass: "JsonStringifyScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/json-stringify-auto-fixer.js",
        fixerClass: "JsonStringifyAutoFixer",
        displayName: "JSON.stringify",
        description: "Fix spacing and UTF-8 encoding in JSON.stringify calls",
    },
    "native-dialogs": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/native-dialogs-scanner.js",
        scannerClass: "NativeDialogsScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/native-dialogs-auto-fixer.js",
        fixerClass: "NativeDialogsAutoFixer",
        displayName: "Native Dialogs",
        description: "Replace native alert/confirm/prompt with custom implementations",
    },
    "dead-code": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/dead-code-scanner.js",
        scannerClass: "DeadCodeScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/dead-code-auto-fixer.js",
        fixerClass: "DeadCodeAutoFixer",
        displayName: "Dead Code",
        description: "Remove unused variables, functions, and imports",
    },
    "long-method": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/long-method-scanner.js",
        scannerClass: "LongMethodScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/long-method-auto-fixer.js",
        fixerClass: "LongMethodAutoFixer",
        displayName: "Long Methods",
        description: "Refactor long methods into smaller functions",
    },
    "production-anti-patterns": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/production-anti-patterns-scanner.js",
        scannerClass: "ProductionAntiPatternsScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/production-anti-patterns-auto-fixer.js",
        fixerClass: "ProductionAntiPatternsAutoFixer",
        displayName: "Production Anti-Patterns",
        description: "Remove console.log and debugger statements from production code",
    },

    // Cross-Module Fixers
    "constant-aliases": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/cross-module/constant-aliases-scanner.js",
        scannerClass: "ConstantAliasesScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/constant-aliases-auto-fixer.js",
        fixerClass: "ConstantAliasesAutoFixer",
        displayName: "Constant Aliases",
        description: "Remove duplicate constant definitions",
    },

    // Security Fixers
    "injection-vulnerabilities": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/security/injection-vulnerabilities-scanner.js",
        scannerClass: "InjectionVulnerabilitiesScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/injection-vulnerabilities-auto-fixer.js",
        fixerClass: "InjectionVulnerabilitiesAutoFixer",
        displayName: "Injection Vulnerabilities",
        description: "Fix SQL injection and XSS vulnerabilities",
    },

    // Documentation Fixers
    "api-documentation": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/code/api-documentation-scanner.js",
        scannerClass: "ApiDocumentationScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/api-documentation-auto-fixer.js",
        fixerClass: "ApiDocumentationAutoFixer",
        displayName: "API Documentation",
        description: "Add missing JSDoc comments to public APIs",
    },

    // Electron-Specific Fixers
    "browser-window-icon": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/electron/browser-window-icon-scanner.js",
        scannerClass: "BrowserWindowIconScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/electron/browser-window-icon-auto-fixer.js",
        fixerClass: "BrowserWindowIconAutoFixer",
        displayName: "BrowserWindow Icon",
        description: "Add missing window icons to Electron BrowserWindow instances",
    },

    // CSS Fixers
    "css-responsive-patterns": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/css/responsive/css-responsive-patterns-scanner.js",
        scannerClass: "CssResponsivePatternsScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/css/css-responsive-patterns-auto-fixer.js",
        fixerClass: "CssResponsivePatternsAutoFixer",
        displayName: "CSS Responsive Patterns",
        description: "Fix hardcoded breakpoints and non-responsive units",
    },
    "css-variable-consistency": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/css/variable-architecture/css-variable-consistency-scanner.js",
        scannerClass: "CssVariableConsistencyScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/css/css-variable-consistency-auto-fixer.js",
        fixerClass: "CssVariableConsistencyAutoFixer",
        displayName: "CSS Variable Consistency",
        description: "Fix inconsistent CSS variable naming and usage",
    },
    "css-variable-order": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/css/variable-architecture/ordering/css-variable-order-scanner.js",
        scannerClass: "CssVariableOrderScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/css/css-variable-order-auto-fixer.js",
        fixerClass: "CssVariableOrderAutoFixer",
        displayName: "CSS Variable Order",
        description: "Reorder CSS variables according to category",
    },
    "css-variable-unit": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/css/design-tokens/misc/css-variable-unit-mismatch-scanner.js",
        scannerClass: "CssVariableUnitMismatchScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/css/css-variable-unit-auto-fixer.js",
        fixerClass: "CssVariableUnitAutoFixer",
        displayName: "CSS Variable Units",
        description: "Fix inconsistent units in CSS variable definitions",
    },
    "css-hardcoded-colors": {
        scannerPath: "../../../root/codebase-validation/scanners/quality/css/design-tokens/colors/css-hardcoded-colors-scanner.js",
        scannerClass: "CssHardcodedColorsScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/css/css-design-token-auto-fixer.js",
        fixerClass: "CssDesignTokenAutoFixer",
        displayName: "CSS Hardcoded Colors",
        description: "Replace hardcoded colors with design tokens",
    },
};

// =============================================================================
// CLI Helper Functions
// =============================================================================

function parseArgs() {
    const args = process.argv.slice(2);
    const fixerName = args.find((arg) => !arg.startsWith("--"));
    const dryRun = args.includes("--dry-run") || args.includes("-d");
    const autoYes = args.includes("--yes") || args.includes("-y");

    if (!fixerName) {
        Logger.error(`\n${getLogIcon("error")} No fixer name provided`);
        Logger.info(`\nAvailable fixers:`);
        for (const [name, config] of Object.entries(FIXER_REGISTRY)) {
            Logger.info(`  ${name.padEnd(20)} - ${config.description}`);
        }
        Logger.info(`\nUsage: node .claude/workspace/tools/run-auto-fixer.js <fixer-name> [--dry-run] [--yes]`);
        process.exit(1);
    }

    if (!FIXER_REGISTRY[fixerName]) {
        Logger.error(`\n${getLogIcon("error")} Unknown fixer: ${fixerName}`);
        Logger.info(`\nAvailable fixers: ${Object.keys(FIXER_REGISTRY).join(", ")}`);
        process.exit(1);
    }

    return { fixerName, dryRun, autoYes };
}

async function promptConfirmation(message) {
    const rl = createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    return new Promise((resolve) => {
        rl.question(`${message} (y/n): `, (answer) => {
            rl.close();
            resolve(answer.toLowerCase() === "y" || answer.toLowerCase() === "yes");
        });
    });
}

function displayViolationPreview(violations, fixerConfig, byFileData = null) {
    Logger.info(`\n${getLogIcon("info")} DRY RUN - Preview of what would be fixed:`);
    Logger.info("=".repeat(60));

    if (byFileData) {
        // Custom preview for code-comments (grouped by extension)
        const byExtension = {};
        for (const fileData of byFileData) {
            const ext = path.extname(fileData.file) || "no-ext";
            if (!byExtension[ext]) {
                byExtension[ext] = { files: 0, count: 0, examples: [] };
            }
            byExtension[ext].files++;
            byExtension[ext].count += fileData.count;

            if (byExtension[ext].examples.length < 3) {
                byExtension[ext].examples.push({
                    file: fileData.file,
                    count: fileData.count,
                    samples: fileData.comments.slice(0, 2),
                });
            }
        }

        Logger.info(`\n${getLogIcon("info")} Items by file type:`);
        for (const [ext, data] of Object.entries(byExtension).sort((a, b) => b[1].count - a[1].count)) {
            Logger.info(`\n  ${ext}: ${data.count} items in ${data.files} files`);
            for (const example of data.examples) {
                Logger.info(`    ${example.file} (${example.count} items)`);
                for (const sample of example.samples) {
                    const preview = sample.preview?.substring(0, 80) || "N/A";
                    Logger.info(`      Line ${sample.line}: ${preview}`);
                }
            }
        }
    } else {
        // Standard preview for other fixers
        const byFile = {};
        for (const v of violations) {
            if (!byFile[v.file]) {
                byFile[v.file] = [];
            }
            byFile[v.file].push(v);
        }

        Logger.info(`\n${getLogIcon("info")} Violations by file:`);
        const fileEntries = Object.entries(byFile).slice(0, 10);
        for (const [file, fileViolations] of fileEntries) {
            Logger.info(`\n  ${file} (${fileViolations.length} violations)`);
            const samples = fileViolations.slice(0, 3);
            for (const v of samples) {
                const preview = v.message || v.preview || "N/A";
                Logger.info(`    Line ${v.line}: ${preview.substring(0, 80)}`);
            }
            if (fileViolations.length > 3) {
                Logger.info(`    ... and ${fileViolations.length - 3} more`);
            }
        }

        if (Object.keys(byFile).length > 10) {
            Logger.info(`\n  ... and ${Object.keys(byFile).length - 10} more files`);
        }
    }

    Logger.info(`\n${"=".repeat(60)}`);
    Logger.info(`${getLogIcon("check")} DRY RUN SUMMARY:`);
    Logger.info(`   Total violations: ${violations.length}`);
    Logger.info(`   Total files: ${Object.keys(byFileData || violations.reduce((acc, v) => ({ ...acc, [v.file]: true }), {})).length}`);
}

// =============================================================================
// Main Runner
// =============================================================================

async function main() {
    const startTime = Date.now();
    const { fixerName, dryRun, autoYes } = parseArgs();
    const fixerConfig = FIXER_REGISTRY[fixerName];

    Logger.info(`\n${getLogIcon("rocket")} ${fixerConfig.displayName} Auto-Fixer ${dryRun ? "(DRY RUN)" : ""}`);
    Logger.info("=".repeat(60));
    if (dryRun) {
        Logger.info(`   ${getLogIcon("info")} Dry run mode - no files will be modified`);
        Logger.info("=".repeat(60));
    }

    try {
        // 1. Initialize database
        Logger.info(`\n${getLogIcon("database")} Initializing database...`);
        const db = getDb();
        Logger.info(`   ${getLogIcon("success")} Database ready`);

        // 2. Load scanner and run it
        Logger.info(`\n${getLogIcon("search")} Running ${fixerConfig.displayName} scanner...`);

        let violations = [];
        let byFileData = null;

        if (fixerConfig.needsCustomScanner) {
            // Special handling for code-comments scanner
            const { CodeCommentsScanner } = await import(fixerConfig.scannerPath);
            const scanner = new CodeCommentsScanner(PROJECT_PATHS.root);

            const dirsToScan = scanner._getDirectoriesToScan();
            const exclusionManager = new ExclusionManager();
            const exclusionsPath = path.join(__dirname, "../../../root/codebase-validation/exclusions");
            await exclusionManager.discoverRules(exclusionsPath);

            const functionalPatternStrings = getFunctionalCommentPatterns();
            Logger.info(`   ${getLogIcon("info")} Debug: Functional patterns count = ${functionalPatternStrings.length}`);
            Logger.info(`   ${getLogIcon("info")} Debug: SPDX pattern included = ${functionalPatternStrings.includes("SPDX-License-Identifier")}`);

            const functionalPatterns = functionalPatternStrings.map((p) => {
                const escaped = p.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
                return new RegExp(escaped, "i");
            });

            // Test SPDX pattern
            const spdxPattern = functionalPatterns.find(p => p.source.includes("SPDX"));
            if (spdxPattern) {
                const testText = "// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0";
                Logger.info(`   ${getLogIcon("info")} Debug: SPDX pattern test = ${spdxPattern.test(testText)}`);
            }

            const allowedInlinePatternStrings = getAllowedInlineCommentPatterns();
            const allowedInlinePatterns = allowedInlinePatternStrings.map((p) => {
                const escaped = p.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
                return new RegExp(`\\{\\s*\\/\\/\\s*${escaped}\\s*\\}`, "i");
            });

            const summary = await scanner.scanForComments(dirsToScan, exclusionManager, functionalPatterns, allowedInlinePatterns);

            byFileData = summary.by_file || [];

            if (byFileData.length === 0) {
                Logger.info(`   ${getLogIcon("success")} No violations found - codebase is clean!`);
                process.exit(0);
            }

            const totalCount = byFileData.reduce((sum, fd) => sum + fd.count, 0);
            Logger.info(`   ${getLogIcon("success")} Found ${totalCount} violations in ${byFileData.length} files`);

            // Convert to violations format for consistency
            for (const fileData of byFileData) {
                for (const comment of fileData.comments) {
                    violations.push({
                        file: fileData.file,
                        line: comment.line,
                        endLine: comment.endLine,
                        severity: "warning",
                        message: comment.preview || "Non-functional comment",
                        category: comment.type || "comment",
                        type: comment.type,
                    });
                }
            }
        } else {
            // Standard scanner flow
            const { [fixerConfig.scannerClass]: ScannerClass } = await import(fixerConfig.scannerPath);
            const scanner = new ScannerClass(PROJECT_PATHS.root);
            const scanResults = await scanner.scan();

            // BaseDbScanner returns: { success, analysis, database, phases, metrics }
            // Violations are in scanResults.analysis.violations
            if (!scanResults || !scanResults.analysis || !scanResults.analysis.violations || scanResults.analysis.violations.length === 0) {
                Logger.info(`   ${getLogIcon("success")} No violations found - codebase is clean!`);
                process.exit(0);
            }

            violations = scanResults.analysis.violations;
            Logger.info(`   ${getLogIcon("success")} Found ${violations.length} violations`);
        }

        // 3. Display preview
        displayViolationPreview(violations, fixerConfig, byFileData);

        // 4. Exit if dry-run
        if (dryRun) {
            Logger.info(`\n${getLogIcon("tip")} To apply these changes, run without --dry-run flag:`);
            Logger.info(`   node .claude/workspace/tools/run-auto-fixer.js ${fixerName}`);
            process.exit(0);
        }

        // 5. Confirm before applying (unless --yes)
        if (!autoYes) {
            Logger.info("");
            const confirmed = await promptConfirmation(`${getLogIcon("warning")} Apply these fixes?`);
            if (!confirmed) {
                Logger.info(`\n${getLogIcon("info")} Cancelled by user`);
                process.exit(0);
            }
        }

        // 6. Apply fixes
        Logger.info(`\n${getLogIcon("fix")} Applying fixes...`);

        const registryDir = path.join(PROJECT_PATHS.root, ".registry");
        await fs.mkdir(registryDir, { recursive: true });

        const { [fixerConfig.fixerClass]: FixerClass } = await import(fixerConfig.fixerPath);
        const fixer = new FixerClass(PROJECT_PATHS.root, registryDir);

        const fixResult = await fixer.fix(byFileData || violations);

        Logger.info(`   ${getLogIcon("success")} Fixed ${fixResult.fixed || fixResult.removed || 0} violations in ${fixResult.files} files`);

        // 7. Display results
        Logger.info(`\n${"=".repeat(60)}`);
        Logger.info(`${getLogIcon("success")} Auto-Fix Complete`);
        Logger.info(`${"=".repeat(60)}\n`);

        Logger.info(`${getLogIcon("check")} Summary:`);
        Logger.info(`   Violations fixed: ${fixResult.fixed || fixResult.removed || 0}`);
        Logger.info(`   Files modified: ${fixResult.files}`);

        const duration = Date.now() - startTime;
        Logger.info(`\n${getLogIcon("clock")} Completed in ${duration}ms`);

        process.exit(0);
    } catch (error) {
        Logger.error(`\n${getLogIcon("error")} Fatal error: ${error.message}`);
        Logger.error(`   Stack: ${error.stack}`);
        process.exit(1);
    }
}

main();
