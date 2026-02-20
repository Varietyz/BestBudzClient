#!/usr/bin/env node
// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

/**
 * Rollback Auto-Actions CLI Tool
 *
 * Manage and rollback automatic code transformations tracked by auto-action-logger
 *
 * Usage:
 *   node .claude/workspace/tools/rollback-auto-actions.js list
 *   node .claude/workspace/tools/rollback-auto-actions.js show <transaction-id>
 *   node .claude/workspace/tools/rollback-auto-actions.js rollback <transaction-id>
 *   node .claude/workspace/tools/rollback-auto-actions.js stats [plugin]
 */

import fs from "fs";
import path from "path";

const command = process.argv[2];
const arg = process.argv[3];

async function main() {
    const logPath = path.resolve(process.cwd(), ".registry/auto-actions.json");

    if (!fs.existsSync(logPath)) {
        console.log("\nNo auto-actions log found at:", logPath);
        console.log("Run a build first to generate actions.\n");
        process.exit(1);
    }

    const log = JSON.parse(fs.readFileSync(logPath, "utf-8"));

    if (command === "list") {
        await listTransactions(log);
    } else if (command === "show" && arg) {
        await showTransaction(log, arg);
    } else if (command === "rollback" && arg) {
        await rollbackTransaction(log, logPath, arg);
    } else if (command === "stats") {
        await showStats(log, arg);
    } else {
        showUsage();
    }
}

async function listTransactions(log) {
    if (log.transactions.length === 0) {
        console.log("\nNo transactions found.\n");
        return;
    }

    console.log("\n" + "=".repeat(80));
    console.log("Recent Auto-Action Transactions");
    console.log("=".repeat(80));

    const recent = log.transactions.slice(-20).reverse();

    for (const tx of recent) {
        const txId = tx.id.substring(0, 8);
        const date = new Date(tx.timestamp).toLocaleString();
        const status = tx.completed ? "COMPLETED" : "IN PROGRESS";

        const fileCount = new Set(tx.actions.map((a) => a.file)).size;
        const pluginCount = new Set(tx.actions.map((a) => a.plugin)).size;

        console.log(`\n[${txId}] ${date}`);
        console.log(`  Type: ${tx.buildType}`);
        console.log(`  Status: ${status}`);
        console.log(`  Actions: ${tx.actions.length} across ${fileCount} file(s)`);
        console.log(`  Plugins: ${pluginCount}`);
    }

    console.log("\n" + "=".repeat(80));
    console.log(`Showing ${recent.length} most recent transactions`);
    console.log("=".repeat(80) + "\n");
}

async function showTransaction(log, transactionId) {
    const tx = findTransaction(log, transactionId);
    if (!tx) {
        console.error(`\nTransaction not found: ${transactionId}\n`);
        process.exit(1);
    }

    console.log("\n" + "=".repeat(80));
    console.log(`Transaction: ${tx.id.substring(0, 8)}`);
    console.log("=".repeat(80));
    console.log(`Type: ${tx.buildType}`);
    console.log(`Date: ${new Date(tx.timestamp).toLocaleString()}`);
    console.log(`Status: ${tx.completed ? "COMPLETED" : "IN PROGRESS"}`);
    console.log(`Total Actions: ${tx.actions.length}`);
    console.log("=".repeat(80) + "\n");

    const byPlugin = {};
    for (const action of tx.actions) {
        if (!byPlugin[action.plugin]) {
            byPlugin[action.plugin] = [];
        }
        byPlugin[action.plugin].push(action);
    }

    for (const [plugin, pluginActions] of Object.entries(byPlugin)) {
        console.log(`\n[${plugin}] ${pluginActions.length} action(s):`);
        console.log("-".repeat(80));

        for (const action of pluginActions.slice(0, 10)) {
            console.log(`  ${action.file}:${action.line}`);
            console.log(`    ${action.content}`);
            console.log(`    Context: lines ${action.contextStart}-${action.contextEnd}`);
            console.log();
        }

        if (pluginActions.length > 10) {
            console.log(`  ... and ${pluginActions.length - 10} more actions\n`);
        }
    }

    console.log("=".repeat(80) + "\n");
}

async function rollbackTransaction(log, logPath, transactionId) {
    const tx = findTransaction(log, transactionId);
    if (!tx) {
        console.error(`\nTransaction not found: ${transactionId}\n`);
        process.exit(1);
    }

    console.log("\n" + "=".repeat(80));
    console.log(`Rolling back transaction: ${tx.id.substring(0, 8)}`);
    console.log("=".repeat(80) + "\n");

    const result = {
        success: true,
        filesModified: [],
        actionsReverted: 0,
        errors: [],
    };

    const byFile = {};
    for (const action of tx.actions) {
        if (!byFile[action.file]) {
            byFile[action.file] = [];
        }
        byFile[action.file].push(action);
    }

    for (const [file, fileActions] of Object.entries(byFile)) {
        fileActions.sort((a, b) => b.contextStart - a.contextStart);

        try {
            const filePath = path.resolve(process.cwd(), file);
            const content = fs.readFileSync(filePath, "utf-8");
            const lines = content.split("\n");

            for (const action of fileActions) {
                const currentBlock = lines.slice(action.contextStart - 1, action.contextEnd).join("\n");

                if (currentBlock.trim() !== action.afterCode.trim()) {
                    result.errors.push(`Skipped ${file}:${action.line} - file modified since auto-action`);
                    continue;
                }

                lines.splice(
                    action.contextStart - 1,
                    action.contextEnd - action.contextStart + 1,
                    ...action.beforeCode.split("\n")
                );

                result.actionsReverted++;
            }

            fs.writeFileSync(filePath, lines.join("\n"), "utf-8");
            result.filesModified.push(file);
        } catch (error) {
            result.success = false;
            result.errors.push(`Failed to rollback ${file}: ${error.message}`);
        }
    }

    tx.rolledBack = true;
    tx.rollbackTimestamp = new Date().toISOString();
    fs.writeFileSync(logPath, JSON.stringify(log, null, 2), "utf-8");

    if (result.success) {
        console.log(`✓ Successfully reverted ${result.actionsReverted} action(s)`);
        console.log(`✓ Modified ${result.filesModified.length} file(s):\n`);
        for (const file of result.filesModified) {
            console.log(`  - ${file}`);
        }
    } else {
        console.log(`✗ Rollback completed with errors:\n`);
    }

    if (result.errors.length > 0) {
        console.log(`\nErrors (${result.errors.length}):`);
        for (const error of result.errors) {
            console.log(`  ! ${error}`);
        }
    }

    console.log("\n" + "=".repeat(80) + "\n");

    if (!result.success) {
        process.exit(1);
    }
}

async function showStats(log, plugin) {
    const summary = {};

    for (const tx of log.transactions) {
        for (const action of tx.actions) {
            if (plugin && action.plugin !== plugin) {
                continue;
            }

            if (!summary[action.plugin]) {
                summary[action.plugin] = {
                    actions: 0,
                    total: 0,
                };
            }

            summary[action.plugin].actions++;
            summary[action.plugin].total++;
        }
    }

    console.log("\n" + "=".repeat(80));
    console.log("Auto-Action Statistics");
    console.log("=".repeat(80) + "\n");

    console.log("Summary by Plugin:");
    console.log("-".repeat(80));
    for (const [pluginName, pluginStats] of Object.entries(summary)) {
        console.log(`  ${pluginName}:`);
        console.log(`    Total Actions: ${pluginStats.actions}`);
        console.log(`    Total Occurrences: ${pluginStats.total}`);
    }
    console.log();

    console.log("=".repeat(80) + "\n");
}

function findTransaction(log, partialId) {
    return log.transactions.find((t) => t.id.startsWith(partialId));
}

function showUsage() {
    console.log("\nRollback Auto-Actions CLI Tool");
    console.log("=".repeat(80));
    console.log("\nUsage:");
    console.log("  node .claude/workspace/tools/rollback-auto-actions.js <command> [args]");
    console.log("\nCommands:");
    console.log("  list                     List recent transactions");
    console.log("  show <transaction-id>    Show details of a specific transaction");
    console.log("  rollback <transaction>   Rollback a transaction (restore original code)");
    console.log("  stats [plugin]           Show statistics (optionally filtered by plugin)");
    console.log("\nExamples:");
    console.log("  node .claude/workspace/tools/rollback-auto-actions.js list");
    console.log("  node .claude/workspace/tools/rollback-auto-actions.js show a3f8c2d1");
    console.log("  node .claude/workspace/tools/rollback-auto-actions.js rollback a3f8c2d1");
    console.log("  node .claude/workspace/tools/rollback-auto-actions.js stats comment-cleaner");
    console.log("=".repeat(80) + "\n");
}

main().catch((error) => {
    console.error("\nFatal error:", error.message);
    console.error(error.stack);
    process.exit(1);
});
