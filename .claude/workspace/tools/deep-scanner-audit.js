/**
 * Deep Scanner Audit - Check if scanners actually USE language awareness
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, "../../../root/codebase-validation");

const results = {
    properlyImplemented: [],
    hasMethodButNoUsage: [],
    missingCompletely: [],
    cssScoped: [],
    structureScanner: [],
    metaScanner: [],
};

function analyzeScanner(filePath) {
    const content = fs.readFileSync(filePath, "utf-8");
    const relPath = path.relative(rootDir, filePath);
    const fileName = path.basename(filePath);

    // Skip base classes
    if (fileName.startsWith("base-") && !fileName.includes("base-classes")) {
        return null;
    }

    // Check inheritance
    const extendsCssScanner = content.includes("extends BaseCssScanner");
    const extendsAccessibilityScanner = content.includes("extends BaseAccessibilityScanner");
    const extendsSecurityScanner = content.includes("extends BaseSecurityScanner");
    const extendsMagicLiteralScanner = content.includes("extends BaseMagicLiteralScanner");

    // Check for language awareness methods
    const hasGetSupportedLanguages = /getSupportedLanguages\s*\(\s*\)/.test(content);
    const hasShouldProcessCall = content.includes("shouldProcessForLanguage");
    const hasGetLanguageConfig = content.includes("getLanguageConfig");

    // Determine scanner type
    if (extendsCssScanner) {
        results.cssScoped.push({ file: relPath, reason: "extends BaseCssScanner" });
        return;
    }

    // Check if it's a structure scanner (file/folder limits, naming)
    if (relPath.includes("structure/") || fileName.includes("file-limits") || fileName.includes("folder-limits") || fileName.includes("file-naming")) {
        results.structureScanner.push({ file: relPath, reason: "Structure scanner - applies to all files" });
        return;
    }

    // Check if it's a meta scanner
    if (fileName === "auto-fix-scanner.js" || fileName.includes("-discovered-")) {
        results.metaScanner.push({ file: relPath, reason: "Meta/discovery scanner" });
        return;
    }

    // Check proper implementation
    if (extendsAccessibilityScanner || extendsSecurityScanner || extendsMagicLiteralScanner) {
        // These inherit from base classes that should have the check
        results.properlyImplemented.push({
            file: relPath,
            method: "inherited",
            base: extendsAccessibilityScanner ? "BaseAccessibilityScanner" : extendsSecurityScanner ? "BaseSecurityScanner" : "BaseMagicLiteralScanner",
        });
        return;
    }

    if (hasGetSupportedLanguages && hasShouldProcessCall) {
        results.properlyImplemented.push({
            file: relPath,
            method: "direct",
            hasGetSupportedLanguages,
            hasShouldProcessCall,
        });
        return;
    }

    if (hasGetSupportedLanguages && !hasShouldProcessCall) {
        results.hasMethodButNoUsage.push({
            file: relPath,
            issue: "Has getSupportedLanguages() but doesn't call shouldProcessForLanguage()",
        });
        return;
    }

    // Check if it delegates to a helper that has the check
    const delegatesToHelper = content.includes("BoundaryScanner") || content.includes("CommentCleaner");

    if (delegatesToHelper) {
        results.properlyImplemented.push({
            file: relPath,
            method: "delegated",
            note: "Delegates to helper with language check",
        });
        return;
    }

    // Missing completely
    results.missingCompletely.push({
        file: relPath,
        scanFileMethod: content.includes("scanFile") || content.includes("async scan"),
        extendsBase: content.includes("extends BaseScanner"),
    });
}

function walkDir(dir) {
    if (!fs.existsSync(dir)) return;

    const files = fs.readdirSync(dir, { withFileTypes: true });
    for (const f of files) {
        const fullPath = path.join(dir, f.name);
        if (f.isDirectory()) {
            walkDir(fullPath);
        } else if (f.name.endsWith("-scanner.js")) {
            analyzeScanner(fullPath);
        }
    }
}

console.log("Deep Scanner Audit\n" + "=".repeat(60) + "\n");

walkDir(path.join(rootDir, "scanners"));

console.log("✓ PROPERLY IMPLEMENTED (" + results.properlyImplemented.length + "):");
results.properlyImplemented.forEach((r) => console.log(`  ${r.file} [${r.method}]`));

console.log("\n⚠ HAS METHOD BUT NO USAGE (" + results.hasMethodButNoUsage.length + "):");
results.hasMethodButNoUsage.forEach((r) => console.log(`  ${r.file}\n    Issue: ${r.issue}`));

console.log("\n✗ MISSING COMPLETELY (" + results.missingCompletely.length + "):");
results.missingCompletely.forEach((r) => console.log(`  ${r.file} (extends BaseScanner: ${r.extendsBase})`));

console.log("\n~ CSS SCOPED (" + results.cssScoped.length + "):");
console.log("  (These are fine - they extend BaseCssScanner)");

console.log("\n~ STRUCTURE SCANNERS (" + results.structureScanner.length + "):");
results.structureScanner.forEach((r) => console.log(`  ${r.file}`));

console.log("\n~ META SCANNERS (" + results.metaScanner.length + "):");
results.metaScanner.forEach((r) => console.log(`  ${r.file}`));

console.log("\n" + "=".repeat(60));
console.log("SUMMARY");
console.log("=".repeat(60));
console.log(`Properly implemented: ${results.properlyImplemented.length}`);
console.log(`Has method but no usage: ${results.hasMethodButNoUsage.length}`);
console.log(`Missing completely: ${results.missingCompletely.length}`);
console.log(`CSS scoped (ok): ${results.cssScoped.length}`);
console.log(`Structure scanners (ok): ${results.structureScanner.length}`);
console.log(`Meta scanners (ok): ${results.metaScanner.length}`);
