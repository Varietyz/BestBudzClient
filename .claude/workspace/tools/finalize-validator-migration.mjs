#!/usr/bin/env node
/**
 * Finalize Validator Migration
 * Removes MIGRATE markers, fixes loadRegistry calls, updates return values
 */

import { readFileSync, writeFileSync } from "fs";
import { glob } from "glob";

const VALIDATORS_DIR = "D:\\GIT\\archlab\\root\\codebase-validation\\validators";

async function finalizeValidator(filePath) {
    const fileName = filePath.split("\\").pop();
    let content = readFileSync(filePath, "utf-8");
    let modified = false;

    // Skip if no loadRegistry calls remain
    if (!content.includes("/* MIGRATE: */") && !content.includes("return false") && !content.includes("return true")) {
        return { file: fileName, status: "skip", reason: "No changes needed" };
    }

    console.log(`Finalizing: ${fileName}`);

    // Step 1: Remove /* MIGRATE: */ markers from loadRegistry calls
    if (content.includes("/* MIGRATE: */")) {
        content = content.replace(/\/\* MIGRATE: \*\/ /g, "");
        modified = true;
    }

    // Step 2: Replace loadRegistry with state.data check
    if (content.includes("await this.loadRegistry(")) {
        // Find analyzeViolations method and add state check at start
        if (!content.includes("if (!state.data || state.noScanData)")) {
            content = content.replace(
                /(async analyzeViolations\(state, _invariants\)\s*\{)/,
                `$1\n        if (!state.data || state.noScanData) {\n            this.logError("No scan data available - run scanner first");\n            return { violations: [], total: 0 };\n        }\n`
            );
            modified = true;
        }

        // Replace loadRegistry calls with state.data.violations
        content = content.replace(
            /const\s+(\w+)\s*=\s*await this\.loadRegistry\([^)]+\);?/g,
            "const violations = state.data.violations || [];"
        );
        modified = true;

        // Remove null checks after loadRegistry
        content = content.replace(
            /if\s*\(\s*!\w+\s*\)\s*\{\s*return\s+(false|true|\{ violations: \[\], total: 0 \});?\s*\}/g,
            ""
        );
        modified = true;
    }

    // Step 3: Replace return false/true with { violations, total }
    if (content.match(/return (false|true);?\s*$/m)) {
        // Replace "return true" (no violations case)
        content = content.replace(
            /return true;?(\s*$)/m,
            "return { violations: [], total: 0 };$1"
        );

        // Replace "return false" (violations found case)
        content = content.replace(
            /return false;?(\s*$)/m,
            "return { violations, total: violations.length };$1"
        );
        modified = true;
    }

    // Step 4: Add violations variable if missing
    if (!content.match(/const violations = /)) {
        // Try to find where violations are used
        if (content.includes("violations.")) {
            // Add at start of function after state check
            content = content.replace(
                /(if \(!state\.data \|\| state\.noScanData\)[^}]+\})/,
                `$1\n\n        const violations = state.data.violations || [];`
            );
            modified = true;
        }
    }

    if (modified) {
        writeFileSync(filePath, content, "utf-8");
        return { file: fileName, status: "migrated" };
    }

    return { file: fileName, status: "skip", reason: "No modifications needed" };
}

async function main() {
    console.log("Finalizing validator migrations...\n");

    const validatorFiles = await glob(`${VALIDATORS_DIR}/**/*-validator.js`);
    const results = { migrated: 0, skipped: 0 };

    for (const file of validatorFiles) {
        const result = await finalizeValidator(file);
        if (result.status === "migrated") {
            results.migrated++;
        } else {
            results.skipped++;
        }
    }

    console.log("\n" + "=".repeat(60));
    console.log("FINALIZATION SUMMARY");
    console.log("=".repeat(60));
    console.log(`✓ Finalized: ${results.migrated}`);
    console.log(`- Skipped: ${results.skipped}`);
    console.log("=".repeat(60));
}

main().catch(console.error);
