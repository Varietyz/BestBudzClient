#!/usr/bin/env node
/**
 * Comprehensive Validator Migration Script
 * Migrates ALL validators from BaseValidator to BaseDbValidator
 */

import { readFileSync, writeFileSync, existsSync } from "fs";
import { glob } from "glob";
import { relative, dirname } from "path";

const VALIDATORS_DIR = "D:\\GIT\\archlab\\root\\codebase-validation\\validators";
const BASE_PATH = "D:\\GIT\\archlab\\root\\codebase-validation";

// Track results
const results = {
    migrated: [],
    skipped: [],
    errors: [],
};

// Calculate relative import path depth
function getRelativeBasePath(filePath) {
    const rel = relative(BASE_PATH, dirname(filePath));
    const depth = rel.split("\\").length;
    let relPath = "";
    for (let i = 0; i < depth + 1; i++) {
        relPath += "../";
    }
    return `${relPath}core/base/base-db-validator.js`;
}

// Infer scanner ID from validator name
function inferScannerId(validatorName) {
    // Remove -validator suffix
    return validatorName.replace(/-validator$/, "");
}

// Main migration function
function migrateValidator(filePath) {
    const fileName = filePath.split("\\").pop();

    try {
        let content = readFileSync(filePath, "utf-8");

        // Check if already migrated
        if (content.includes("BaseDbValidator")) {
            results.skipped.push({ file: fileName, reason: "Already migrated" });
            return false;
        }

        // Check if uses BaseValidator
        if (!content.includes("extends BaseValidator")) {
            results.skipped.push({ file: fileName, reason: "Doesn't extend BaseValidator" });
            return false;
        }

        console.log(`Migrating: ${fileName}`);

        // Extract current getName() value
        const getNameMatch = content.match(/getName\(\)\s*\{\s*return\s+["']([^"']+)["']/);
        const currentName = getNameMatch ? getNameMatch[1] : fileName.replace(".js", "");
        const validatorName = fileName.replace(".js", "");
        const scannerId = inferScannerId(currentName);

        // Calculate relative import
        const relBasePath = getRelativeBasePath(filePath);

        // Step 1: Update import
        content = content.replace(
            /import \{ BaseValidator \} from ["']([^"']+)base-validator\.js["'];?/g,
            `import { BaseDbValidator } from "${relBasePath}";`
        );

        // Step 2: Update extends
        content = content.replace(/extends BaseValidator/g, "extends BaseDbValidator");

        // Step 3: Update analyzeViolations parameter
        content = content.replace(
            /async analyzeViolations\(_state,/g,
            "async analyzeViolations(state,"
        );

        // Step 4: Remove .registry/ path references
        content = content.replace(/\.registry\//g, "");

        // Step 5: Remove this.paths.getRelative() calls
        content = content.replace(/this\.paths\.getRelative\([^)]+\)/g, '""');

        // Step 6: Mark loadRegistry calls for manual review
        content = content.replace(
            /await this\.loadRegistry\(/g,
            "/* MIGRATE: */ await this.loadRegistry("
        );
        content = content.replace(
            /this\.loadRegistry\(/g,
            "/* MIGRATE: */ this.loadRegistry("
        );

        // Step 7: Update getName() to include -validator suffix
        if (!validatorName.endsWith("-validator")) {
            content = content.replace(
                /getName\(\)\s*\{\s*return\s+["'][^"']+["'];?\s*\}/,
                `getName() {\n        return "${validatorName}";\n    }`
            );
        }

        // Step 8: Add getScannerId() after getDisplayName()
        if (!content.includes("getScannerId()")) {
            content = content.replace(
                /(getDisplayName\(\)\s*\{[^}]+\})/,
                `$1\n\n    getScannerId() {\n        return "${scannerId}";\n    }`
            );
        }

        // Write migrated content
        writeFileSync(filePath, content, "utf-8");

        results.migrated.push({
            file: fileName,
            validatorName,
            scannerId,
        });

        return true;
    } catch (error) {
        results.errors.push({ file: fileName, error: error.message });
        console.error(`ERROR migrating ${fileName}:`, error.message);
        return false;
    }
}

// Main execution
async function main() {
    console.log("Starting comprehensive validator migration...\n");

    const validatorFiles = await glob(`${VALIDATORS_DIR}/**/*-validator.js`);
    console.log(`Found ${validatorFiles.length} validator files\n`);

    for (const file of validatorFiles) {
        migrateValidator(file);
    }

    // Print summary
    console.log("\n" + "=".repeat(60));
    console.log("MIGRATION SUMMARY");
    console.log("=".repeat(60));
    console.log(`✓ Migrated: ${results.migrated.length}`);
    console.log(`- Skipped: ${results.skipped.length}`);
    console.log(`✗ Errors: ${results.errors.length}`);

    if (results.migrated.length > 0) {
        console.log("\nMigrated files:");
        results.migrated.forEach((r) => {
            console.log(`  ✓ ${r.file} (scanner: ${r.scannerId})`);
        });
    }

    if (results.errors.length > 0) {
        console.log("\nErrors:");
        results.errors.forEach((e) => {
            console.log(`  ✗ ${e.file}: ${e.error}`);
        });
    }

    console.log("\n" + "=".repeat(60));
    console.log("MANUAL REVIEW REQUIRED:");
    console.log("=".repeat(60));
    console.log("1. Search for /* MIGRATE: */ comments");
    console.log("2. Replace loadRegistry() with state.data.violations");
    console.log("3. Change return false/true to { violations, total }");
    console.log("4. Handle state.noScanData case");
    console.log("5. Update violation display logic");
    console.log("=".repeat(60));
}

main().catch(console.error);
