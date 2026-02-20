#!/usr/bin/env node
// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

/**
 * Bridge Script: Run Comment Removal Auto-Fixer
 *
 * This script properly coordinates the code-comments scanner and auto-fixer
 * with the database and coordinator infrastructure.
 *
 * Usage:
 *   node .claude/workspace/tools/run-comment-fixer.js
 */

import path from "path";
import { fileURLToPath } from "url";
import { promises as fs } from "fs";
import { getDb } from "../../../root/codebase-validation/db/utils/connection.js";
import { Logger, PROJECT_PATHS } from "../../../root/codebase-validation/constants/index.js";
import { getLogIcon } from "../../../root/codebase-validation/core/settings/settings-manager.js";
import { CodeCommentsScanner } from "../../../root/codebase-validation/scanners/quality/code/code-comments-scanner.js";
import { CodeCommentsAutoFixer } from "../../../root/codebase-validation/auto-fix/code-comments-auto-fixer.js";
import { AutoFixCoordinator } from "../../../root/codebase-validation/scanners/quality/auto-fix-coordinator.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function main() {
    const startTime = Date.now();
    const dryRun = process.argv.includes("--dry-run") || process.argv.includes("-d");

    Logger.info(`\n${getLogIcon("rocket")} Comment Removal Auto-Fixer ${dryRun ? "(DRY RUN)" : ""}`);
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

        // 2. Run code-comments scanner to get violations directly
        Logger.info(`\n${getLogIcon("search")} Running code-comments scanner...`);
        const scanner = new CodeCommentsScanner(PROJECT_PATHS.root);

        // Get directories to scan
        const dirsToScan = scanner._getDirectoriesToScan();
        const ExclusionManager = (await import("../../../root/codebase-validation/core/exclusion-manager.js")).ExclusionManager;
        const exclusionManager = new ExclusionManager();
        const exclusionsPath = path.join(__dirname, "../../../root/codebase-validation/exclusions");
        await exclusionManager.discoverRules(exclusionsPath);

        // Import helper functions
        const { getFunctionalCommentPatterns, getAllowedInlineCommentPatterns } = await import("../../../root/codebase-validation/core/settings/settings-manager.js");

        // Build patterns from settings
        const functionalPatternStrings = getFunctionalCommentPatterns();
        const functionalPatterns = functionalPatternStrings.map((p) => {
            const escaped = p.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
            return new RegExp(escaped, "i");
        });

        const allowedInlinePatternStrings = getAllowedInlineCommentPatterns();
        const allowedInlinePatterns = allowedInlinePatternStrings.map((p) => {
            const escaped = p.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
            return new RegExp(`\\{\\s*\\/\\/\\s*${escaped}\\s*\\}`, "i");
        });

        // Scan for comments
        const summary = await scanner.scanForComments(
            dirsToScan,
            exclusionManager,
            functionalPatterns,
            allowedInlinePatterns
        );

        // Use summary.by_file directly - it's already in the correct format for the fixer
        const byFileData = summary.by_file || [];

        if (byFileData.length === 0) {
            Logger.info(`   ${getLogIcon("success")} No comments found - codebase is clean!`);
            process.exit(0);
        }

        const totalComments = byFileData.reduce((sum, fd) => sum + fd.count, 0);
        Logger.info(`   ${getLogIcon("success")} Found ${totalComments} comments in ${byFileData.length} files to remove`);

        // 3. Create coordinator
        Logger.info(`\n${getLogIcon("wrench")} Initializing auto-fix coordinator...`);
        const coordinator = new AutoFixCoordinator(PROJECT_PATHS.root, db);
        Logger.info(`   ${getLogIcon("success")} Coordinator ready`);

        // 4. Create results object
        const results = {
            fixes_applied: [],
            total_fixes: 0,
            total_files_modified: 0,
            errors: [],
        };

        // 5. Run fixer (or show dry-run preview)
        if (dryRun) {
            Logger.info(`\n${getLogIcon("info")} DRY RUN - Preview of comments to be removed:`);
            Logger.info("=".repeat(60));

            // Group by file extension
            const byExtension = {};
            for (const fileData of byFileData) {
                const ext = path.extname(fileData.file) || "no-ext";
                if (!byExtension[ext]) {
                    byExtension[ext] = { files: 0, comments: 0, examples: [] };
                }
                byExtension[ext].files++;
                byExtension[ext].comments += fileData.count;

                // Keep first 3 examples per extension
                if (byExtension[ext].examples.length < 3) {
                    byExtension[ext].examples.push({
                        file: fileData.file,
                        count: fileData.count,
                        samples: fileData.comments.slice(0, 2),
                    });
                }
            }

            // Display summary by file type
            Logger.info(`\n${getLogIcon("info")} Comments by file type:`);
            for (const [ext, data] of Object.entries(byExtension).sort((a, b) => b[1].comments - a[1].comments)) {
                Logger.info(`\n  ${ext}: ${data.comments} comments in ${data.files} files`);
                for (const example of data.examples) {
                    Logger.info(`    ${example.file} (${example.count} comments)`);
                    for (const sample of example.samples) {
                        Logger.info(`      Line ${sample.line}: ${sample.preview}`);
                    }
                }
            }

            Logger.info(`\n${"=".repeat(60)}`);
            Logger.info(`${getLogIcon("check")} DRY RUN SUMMARY:`);
            Logger.info(`   Total comments: ${totalComments}`);
            Logger.info(`   Total files: ${byFileData.length}`);
            Logger.info(`\n${getLogIcon("tip")} To apply these changes, run without --dry-run flag:`);
            Logger.info(`   node .claude/workspace/tools/run-comment-fixer.js`);
            process.exit(0);
        }

        Logger.info(`\n${getLogIcon("fix")} Running auto-fixer...`);

        // Ensure .registry directory exists
        const registryDir = path.join(PROJECT_PATHS.root, ".registry");
        await fs.mkdir(registryDir, { recursive: true });

        const fixer = new CodeCommentsAutoFixer(PROJECT_PATHS.root, registryDir);

        const fixResult = await fixer.fix(byFileData);

        if (fixResult.fixed > 0 || fixResult.removed > 0) {
            Logger.info(`   ${getLogIcon("success")} Fixed ${fixResult.fixed || fixResult.removed} comments in ${fixResult.files} files`);

            results.fixes_applied.push({
                name: "Code Comments",
                ...fixResult,
                success: true,
            });

            results.total_fixes += fixResult.fixed || fixResult.removed || 0;
            results.total_files_modified += fixResult.files || 0;
        }

        // 6. Display results
        Logger.info(`\n${"=".repeat(60)}`);
        Logger.info(`${getLogIcon("success")} Auto-Fix Complete`);
        Logger.info(`${"=".repeat(60)}\n`);

        if (results.total_fixes > 0) {
            Logger.info(`${getLogIcon("check")} Fixes Applied:`);
            Logger.info(`   Total comments removed: ${results.total_fixes}`);
            Logger.info(`   Files modified: ${results.total_files_modified}`);

            if (results.fixes_applied.length > 0) {
                Logger.info(`\n${getLogIcon("info")} Details:`);
                results.fixes_applied.forEach((fix) => {
                    Logger.info(`   - ${fix.name}: ${fix.fixed || fix.removed || 0} fixes in ${fix.files || 0} files`);
                });
            }

            Logger.info(`\n${getLogIcon("tip")} Cleanup log saved to: .registry/comment-cleanup-log.json`);
        } else {
            Logger.info(`${getLogIcon("info")} No fixes were applied`);
        }

        if (results.errors.length > 0) {
            Logger.error(`\n${getLogIcon("error")} Errors encountered:`);
            results.errors.forEach((err) => {
                Logger.error(`   - ${err.name}: ${err.error}`);
            });
        }

        const duration = Date.now() - startTime;
        Logger.info(`\n${getLogIcon("clock")} Completed in ${duration}ms`);

        process.exit(results.errors.length > 0 ? 1 : 0);
    } catch (error) {
        Logger.error(`\n${getLogIcon("error")} Fatal error: ${error.message}`);
        Logger.error(`   Stack: ${error.stack}`);
        process.exit(1);
    }
}

main();
