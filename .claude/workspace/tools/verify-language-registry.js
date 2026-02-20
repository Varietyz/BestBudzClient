/**
 * Language Registry Verification Tool
 *
 * Forensically verifies the language registry system for production quality.
 */

import { readFileSync } from "fs";
import { join } from "path";

const ROOT = "D:\\GIT\\archlab\\root\\codebase-validation\\core\\settings";
const LANGS_DIR = join(ROOT, "languages");

const gaps = {
    schemaViolations: [],
    importExportIssues: [],
    integrationGaps: [],
    devRulesViolations: [],
    missingFields: [],
    productionQuality: [],
};

const specs = ["javascript", "python", "java", "go", "rust", "cpp"];

console.log("=== SCHEMA COMPLIANCE CHECK ===\n");

for (const lang of specs) {
    const specPath = join(LANGS_DIR, "specs", `${lang}.js`);
    const content = readFileSync(specPath, "utf-8");

    if (!content.includes(`export const ${lang.toUpperCase()}_SPEC`)) {
        gaps.importExportIssues.push(`${lang}.js: Missing export`);
    }

    if (!content.includes("Object.freeze")) {
        gaps.schemaViolations.push(`${lang}.js: Not using Object.freeze`);
    }

    const lineCount = content.split("\n").length;
    if (lineCount > 150) {
        gaps.devRulesViolations.push(`${lang}.js: ${lineCount} lines (max 150)`);
    }
}

console.log("=== IMPORT/EXPORT CHAIN CHECK ===\n");

// Check registry-core.js for spec imports (they're now split)
const registryCoreContent = readFileSync(join(LANGS_DIR, "registry-core.js"), "utf-8");

for (const lang of specs) {
    if (!registryCoreContent.includes(`${lang.toUpperCase()}_SPEC`)) {
        gaps.importExportIssues.push(`registry-core.js: Missing ${lang.toUpperCase()}_SPEC`);
    }
}

// Check that language-registry.js exports LANGUAGE_SPECS
const registryPath = join(LANGS_DIR, "language-registry.js");
const registryContent = readFileSync(registryPath, "utf-8");

if (!registryContent.includes("LANGUAGE_SPECS")) {
    gaps.importExportIssues.push("language-registry.js: Missing LANGUAGE_SPECS export");
}

console.log("=== LINE COUNT CHECK ===\n");

const files = [
    { name: "language-schema.js", path: join(LANGS_DIR, "language-schema.js") },
    { name: "language-registry.js", path: join(LANGS_DIR, "language-registry.js") },
    { name: "registry-core.js", path: join(LANGS_DIR, "registry-core.js") },
    { name: "registry-patterns.js", path: join(LANGS_DIR, "registry-patterns.js") },
    { name: "registry-tooling.js", path: join(LANGS_DIR, "registry-tooling.js") },
    { name: "schema-types.js", path: join(LANGS_DIR, "schema-types.js") },
    { name: "schema-utils.js", path: join(LANGS_DIR, "schema-utils.js") },
    { name: "index.js", path: join(LANGS_DIR, "index.js") },
];

for (const file of files) {
    try {
        const content = readFileSync(file.path, "utf-8");
        const lineCount = content.split("\n").length;
        if (lineCount > 150) {
            gaps.devRulesViolations.push(`${file.name}: ${lineCount} lines (max 150)`);
        }
    } catch {
        gaps.missingFields.push(`${file.name}: File not found`);
    }
}

// Check pattern files
for (const lang of specs) {
    const patternPath = join(LANGS_DIR, "specs", `${lang}-patterns.js`);
    const toolingPath = join(LANGS_DIR, "specs", `${lang}-tooling.js`);

    try {
        const content = readFileSync(patternPath, "utf-8");
        const lineCount = content.split("\n").length;
        if (lineCount > 150) {
            gaps.devRulesViolations.push(`${lang}-patterns.js: ${lineCount} lines (max 150)`);
        }
    } catch {
        gaps.missingFields.push(`${lang}-patterns.js: File not found`);
    }

    try {
        const content = readFileSync(toolingPath, "utf-8");
        const lineCount = content.split("\n").length;
        if (lineCount > 150) {
            gaps.devRulesViolations.push(`${lang}-tooling.js: ${lineCount} lines (max 150)`);
        }
    } catch {
        gaps.missingFields.push(`${lang}-tooling.js: File not found`);
    }
}

// REPORT
console.log("\n===========================================");
console.log("LANGUAGE REGISTRY FORENSIC ANALYSIS REPORT");
console.log("===========================================\n");

let totalGaps = 0;

if (gaps.schemaViolations.length > 0) {
    console.log(`\n🔴 SCHEMA VIOLATIONS (${gaps.schemaViolations.length}):`);
    gaps.schemaViolations.forEach((v) => console.log(`  - ${v}`));
    totalGaps += gaps.schemaViolations.length;
}

if (gaps.missingFields.length > 0) {
    console.log(`\n🟠 MISSING FILES (${gaps.missingFields.length}):`);
    gaps.missingFields.forEach((v) => console.log(`  - ${v}`));
    totalGaps += gaps.missingFields.length;
}

if (gaps.importExportIssues.length > 0) {
    console.log(`\n🟠 IMPORT/EXPORT ISSUES (${gaps.importExportIssues.length}):`);
    gaps.importExportIssues.forEach((v) => console.log(`  - ${v}`));
    totalGaps += gaps.importExportIssues.length;
}

if (gaps.devRulesViolations.length > 0) {
    console.log(`\n🟡 DEV-RULES VIOLATIONS (${gaps.devRulesViolations.length}):`);
    gaps.devRulesViolations.forEach((v) => console.log(`  - ${v}`));
    totalGaps += gaps.devRulesViolations.length;
}

if (totalGaps === 0) {
    console.log("✅ ALL CHECKS PASSED - No violations found\n");
}

console.log(`\n===========================================`);
console.log(`TOTAL GAPS FOUND: ${totalGaps}`);
console.log(`===========================================\n`);

console.log(JSON.stringify(gaps, null, 2));
