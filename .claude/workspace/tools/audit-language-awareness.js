/**
 * Audit all scanners, validators, and fixers for language awareness
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, "../../../root/codebase-validation");

const results = {
    scanners: { hasMethod: [], missingMethod: [], cssOnly: [] },
    validators: { hasMethod: [], missingMethod: [], cssOnly: [] },
    fixers: { hasMethod: [], missingMethod: [], cssOnly: [] },
    languageSpecs: { hasApplicable: [], missingApplicable: [] },
};

function checkFile(filePath, category) {
    const content = fs.readFileSync(filePath, "utf-8");
    const relPath = path.relative(rootDir, filePath);

    // Skip base classes
    if (relPath.includes("base-") && !relPath.includes("base-classes")) {
        return;
    }

    // Check if it's CSS-specific (inherits from BaseCssScanner)
    const isCssOnly = content.includes("extends BaseCssScanner") || content.includes("extends BaseCss");

    // Check for language awareness methods
    const hasGetSupportedLanguages = content.includes("getSupportedLanguages()");
    const hasShouldProcessForLanguage = content.includes("shouldProcessForLanguage");

    const info = {
        file: relPath,
        hasGet: hasGetSupportedLanguages,
        hasShould: hasShouldProcessForLanguage,
        isCss: isCssOnly,
    };

    if (isCssOnly) {
        results[category].cssOnly.push(info);
    } else if (hasGetSupportedLanguages || hasShouldProcessForLanguage) {
        results[category].hasMethod.push(info);
    } else if (content.includes("extends Base") || content.includes("class ")) {
        results[category].missingMethod.push(relPath);
    }
}

function walkDir(dir, pattern, category) {
    if (!fs.existsSync(dir)) return;

    const files = fs.readdirSync(dir, { withFileTypes: true });
    for (const f of files) {
        const fullPath = path.join(dir, f.name);
        if (f.isDirectory()) {
            walkDir(fullPath, pattern, category);
        } else if (f.name.endsWith(pattern)) {
            checkFile(fullPath, category);
        }
    }
}

function checkLanguageSpecs() {
    const specsDir = path.join(rootDir, "core/settings/languages/specs");
    if (!fs.existsSync(specsDir)) return;

    const files = fs.readdirSync(specsDir).filter((f) => f.endsWith(".js") && !f.includes("-patterns") && !f.includes("-tooling"));

    for (const file of files) {
        const filePath = path.join(specsDir, file);
        const content = fs.readFileSync(filePath, "utf-8");

        const hasApplicableScanners = content.includes("applicableScanners");
        const hasRules = content.includes("rules:");

        if (hasApplicableScanners) {
            results.languageSpecs.hasApplicable.push({ file, hasRules });
        } else {
            results.languageSpecs.missingApplicable.push(file);
        }
    }
}

// Run audits
console.log("Auditing scanners...");
walkDir(path.join(rootDir, "scanners"), "-scanner.js", "scanners");

console.log("Auditing validators...");
walkDir(path.join(rootDir, "validators"), "-validator.js", "validators");

console.log("Auditing fixers...");
walkDir(path.join(rootDir, "auto-fix"), "-fixer.js", "fixers");
walkDir(path.join(rootDir, "auto-fix"), "-auto-fixer.js", "fixers");

console.log("Auditing language specs...");
checkLanguageSpecs();

// Report results
console.log("\n" + "=".repeat(60));
console.log("SCANNER AUDIT");
console.log("=".repeat(60));
console.log(`\nWith language awareness (${results.scanners.hasMethod.length}):`);
results.scanners.hasMethod.forEach((r) => console.log(`  ✓ ${r.file}`));

console.log(`\nCSS-specific (inherently scoped) (${results.scanners.cssOnly.length}):`);
results.scanners.cssOnly.forEach((r) => console.log(`  ~ ${r.file}`));

console.log(`\nMISSING language awareness (${results.scanners.missingMethod.length}):`);
results.scanners.missingMethod.forEach((f) => console.log(`  ✗ ${f}`));

console.log("\n" + "=".repeat(60));
console.log("VALIDATOR AUDIT");
console.log("=".repeat(60));
console.log(`\nWith language awareness (${results.validators.hasMethod.length}):`);
results.validators.hasMethod.forEach((r) => console.log(`  ✓ ${r.file}`));

console.log(`\nCSS-specific (inherently scoped) (${results.validators.cssOnly.length}):`);
results.validators.cssOnly.forEach((r) => console.log(`  ~ ${r.file}`));

console.log(`\nMISSING language awareness (${results.validators.missingMethod.length}):`);
results.validators.missingMethod.forEach((f) => console.log(`  ✗ ${f}`));

console.log("\n" + "=".repeat(60));
console.log("FIXER AUDIT");
console.log("=".repeat(60));
console.log(`\nWith language awareness (${results.fixers.hasMethod.length}):`);
results.fixers.hasMethod.forEach((r) => console.log(`  ✓ ${r.file}`));

console.log(`\nMISSING language awareness (${results.fixers.missingMethod.length}):`);
results.fixers.missingMethod.forEach((f) => console.log(`  ✗ ${f}`));

console.log("\n" + "=".repeat(60));
console.log("LANGUAGE SPEC AUDIT");
console.log("=".repeat(60));
console.log(`\nWith applicableScanners (${results.languageSpecs.hasApplicable.length}):`);
results.languageSpecs.hasApplicable.forEach((r) => console.log(`  ✓ ${r.file} (hasRules: ${r.hasRules})`));

console.log(`\nMISSING applicableScanners (${results.languageSpecs.missingApplicable.length}):`);
results.languageSpecs.missingApplicable.forEach((f) => console.log(`  ✗ ${f}`));

// Summary
console.log("\n" + "=".repeat(60));
console.log("SUMMARY");
console.log("=".repeat(60));
console.log(`Scanners:   ${results.scanners.hasMethod.length} aware, ${results.scanners.cssOnly.length} CSS-only, ${results.scanners.missingMethod.length} MISSING`);
console.log(`Validators: ${results.validators.hasMethod.length} aware, ${results.validators.cssOnly.length} CSS-only, ${results.validators.missingMethod.length} MISSING`);
console.log(`Fixers:     ${results.fixers.hasMethod.length} aware, ${results.fixers.missingMethod.length} MISSING`);
console.log(`Lang Specs: ${results.languageSpecs.hasApplicable.length} complete, ${results.languageSpecs.missingApplicable.length} MISSING`);
