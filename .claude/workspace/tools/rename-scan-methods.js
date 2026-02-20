/**
 * Renames scan() to _scan() in scanner subclasses
 * This enables the lifecycle management in BaseScanner
 */

import { promises as fs } from "fs";
import path from "path";

const scannerDirs = [
    "root/codebase-validation/scanners",
    "root/codebase-validation/npm-commands",
    "root/codebase-validation/core/base/scanners",
    "root/codebase-validation/core/base",
    "root/codebase-validation/tools/base",
];

async function findScannerFiles(dir) {
    const files = [];
    try {
        const entries = await fs.readdir(dir, { withFileTypes: true });
        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);
            if (entry.isDirectory()) {
                files.push(...(await findScannerFiles(fullPath)));
            } else if (entry.name.endsWith("-scanner.js")) {
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

    // Check if this file extends BaseScanner and has scan() method
    if (!content.includes("extends BaseScanner") && !content.includes("extends BaseCssScanner")) {
        return { skipped: true, reason: "not a scanner subclass" };
    }

    // Pattern to match: async scan() { or scan() {
    // But NOT _scan() or scanDirectory() etc.
    const scanMethodPattern = /(\s+)(async\s+)?scan\s*\(\s*\)\s*\{/g;

    if (!scanMethodPattern.test(content)) {
        return { skipped: true, reason: "no scan() method found" };
    }

    // Reset regex
    scanMethodPattern.lastIndex = 0;

    // Replace scan() with _scan()
    const newContent = content.replace(scanMethodPattern, "$1$2_scan() {");

    if (newContent === content) {
        return { skipped: true, reason: "no changes needed" };
    }

    await fs.writeFile(filePath, newContent);
    return { updated: true };
}

async function main() {
    const rootDir = process.cwd();
    console.log("Finding scanner files...");

    const allFiles = [];
    for (const dir of scannerDirs) {
        const fullDir = path.join(rootDir, dir);
        const files = await findScannerFiles(fullDir);
        allFiles.push(...files);
    }

    console.log(`Found ${allFiles.length} scanner files`);

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
