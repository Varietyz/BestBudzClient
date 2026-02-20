/**
 * Renames validate() to _validate() in validator subclasses
 * This enables the lifecycle management in BaseValidator
 */

import { promises as fs } from "fs";
import path from "path";

const validatorDirs = [
    "root/codebase-validation/validators",
];

async function findValidatorFiles(dir) {
    const files = [];
    try {
        const entries = await fs.readdir(dir, { withFileTypes: true });
        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);
            if (entry.isDirectory()) {
                files.push(...(await findValidatorFiles(fullPath)));
            } else if (entry.name.endsWith("-validator.js")) {
                files.push(fullPath);
            }
        }
    } catch {
        // Directory doesn't exist
    }
    return files;
}

async function processFile(filePath) {
    const content = await fs.readFile(filePath, "utf-8");

    // Check if this file extends BaseValidator
    if (!content.includes("extends BaseValidator")) {
        return { skipped: true, reason: "not a validator subclass" };
    }

    // Pattern to match: async validate() { or validate() {
    // But NOT _validate() or validateSimple() etc.
    const validateMethodPattern = /(\s+)(async\s+)?validate\s*\(\s*\)\s*\{/g;

    if (!validateMethodPattern.test(content)) {
        return { skipped: true, reason: "no validate() method found" };
    }

    // Reset regex
    validateMethodPattern.lastIndex = 0;

    // Replace validate() with _validate()
    const newContent = content.replace(validateMethodPattern, "$1$2_validate() {");

    if (newContent === content) {
        return { skipped: true, reason: "no changes needed" };
    }

    await fs.writeFile(filePath, newContent);
    return { updated: true };
}

async function main() {
    const rootDir = process.cwd();
    console.log("Finding validator files...");

    const allFiles = [];
    for (const dir of validatorDirs) {
        const fullDir = path.join(rootDir, dir);
        const files = await findValidatorFiles(fullDir);
        allFiles.push(...files);
    }

    console.log(`Found ${allFiles.length} validator files`);

    let updated = 0;
    let skipped = 0;

    for (const file of allFiles) {
        const result = await processFile(file);
        const relativePath = path.relative(rootDir, file);
        if (result.updated) {
            console.log(`✅ Updated: ${relativePath}`);
            updated++;
        } else {
            console.log(`⏭️  Skipped: ${relativePath} (${result.reason})`);
            skipped++;
        }
    }

    console.log(`\nDone! Updated: ${updated}, Skipped: ${skipped}`);
}

main().catch(console.error);
