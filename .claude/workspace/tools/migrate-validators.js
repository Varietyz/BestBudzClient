/**
 * Bulk Validator Migration Script
 * Migrates validators from BaseValidator to BaseDbValidator
 */

import { readFileSync, writeFileSync } from "fs";
import { resolve, dirname, relative } from "path";

const validatorsDir = "D:\\GIT\\archlab\\root\\codebase-validation\\validators";

// Files to skip (already migrated)
const skipFiles = [
    "file-limits-validator.js",
    "naming-validator.js",
    "folder-limits-validator.js",
    "import-patterns-validator.js",
    "code-comments-validator.js",
    "magic-numbers-validator.js",
];

// Scanner ID mapping (validator name → scanner ID)
const scannerIdMap = {
    "magic-colors-validator": "magic-colors",
    "magic-measurements-validator": "magic-measurements",
    "code-duplication-validator": "code-duplication",
    "circular-dependencies-validator": "circular-dependencies",
    "cognitive-complexity-validator": "cognitive-complexity",
    "cyclomatic-complexity-validator": "cyclomatic-complexity",
    "dead-code-validator": "dead-code",
    "long-method-validator": "long-method",
    "json-stringify-validator": "json-stringify",
    "underscore-prefix-validator": "underscore-prefix",
    "native-dialogs-validator": "native-dialogs",
    "api-documentation-validator": "api-documentation",
    "cohesion-metrics-validator": "cohesion-metrics",
    "coupling-metrics-validator": "coupling-metrics",
    "dip-violations-validator": "dip-violations",
    "event-driven-violations-validator": "event-driven-violations",
    "isp-violations-validator": "isp-violations",
    "layer-violations-validator": "layer-violations",
    "lsp-violations-validator": "lsp-violations",
    "n-plus-one-queries-validator": "n-plus-one-queries",
    "ocp-violations-validator": "ocp-violations",
    "production-anti-patterns-validator": "production-anti-patterns",
    "soc-violations-validator": "soc-violations",
    "speculative-generality-validator": "speculative-generality",
    "srp-violations-validator": "srp-violations",
    "data-clump-validator": "data-clump",
    "memory-leak-patterns-validator": "memory-leak-patterns",
    "base-classes-validator": "base-classes",
    "utilities-validator": "utilities",
    "constants-validator": "constants",
    "architecture-validator": "architecture",
    "typescript-validator": "typescript",
    "lint-validator": "lint",
    "format-validator": "format",
    "ts-prune-validator": "ts-prune",
    "depcheck-validator": "depcheck",
    "constant-discovery-validator": "constant-discovery",
    "constant-aliases-validator": "constant-aliases",
    "constant-duplicates-validator": "constant-duplicates",
    "constant-imports-validator": "constant-imports",
    "csp-header-validator": "csp-header",
    "csp-inline-events-validator": "csp-inline-events",
    "csp-inline-styles-validator": "csp-inline-styles",
    "csp-unsafe-eval-validator": "csp-unsafe-eval",
    "injection-vulnerabilities-validator": "injection-vulnerabilities",
    "security-inheritance-validator": "security-inheritance",
    "accessibility-images-validator": "accessibility-images",
    "accessibility-interactive-validator": "accessibility-interactive",
};

// CSS validators - prefix with css-
const cssValidators = [
    "css-variable-naming-validator",
    "css-bem-violations-validator",
    "css-inconsistent-naming-validator",
    "css-hardcoded-line-height-validator",
    "css-hardcoded-font-weight-validator",
    "css-hardcoded-font-family-validator",
    "css-hardcoded-font-size-validator",
    "css-wrong-location-validator",
    "css-unscoped-overrides-validator",
    "css-import-order-validator",
    "css-variable-unit-mismatch-validator",
    "css-variable-location-validator",
    "css-hardcoded-letter-spacing-validator",
    "css-duplicate-components-validator",
    "css-glass-patterns-validator",
    "css-vendor-prefixes-validator",
    "css-hardcoded-spacing-validator",
    "css-non-responsive-units-validator",
    "css-hardcoded-radius-validator",
    "css-animation-naming-validator",
    "css-hardcoded-rgba-validator",
    "css-magic-numbers-validator",
    "css-hardcoded-colors-validator",
    "css-important-usage-validator",
    "css-variable-aliases-validator",
    "css-responsive-patterns-validator",
    "css-complex-selectors-validator",
    "css-variable-category-location-validator",
    "css-hardcoded-transitions-validator",
    "css-layout-hardcoded-values-validator",
    "css-variable-redefinitions-validator",
    "css-hardcoded-z-index-validator",
    "css-variable-consistency-validator",
    "css-hardcoded-shadows-validator",
    "css-dead-code-validator",
    "css-mobile-file-separation-validator",
    "css-mobile-imports-validator",
];

cssValidators.forEach((v) => {
    const scannerId = v.replace("-validator", "");
    scannerIdMap[v] = scannerId;
});

function inferScannerId(fileName, validatorName) {
    // Check map first
    if (scannerIdMap[fileName]) {
        return scannerIdMap[fileName];
    }

    // Default: remove -validator suffix
    return validatorName.replace(/-validator$/, "");
}

function migrateValidator(filePath) {
    const fileName = filePath.split("\\").pop();

    if (skipFiles.includes(fileName)) {
        console.log(`Skipping ${fileName} (already migrated)`);
        return false;
    }

    let content = readFileSync(filePath, "utf-8");

    // Check if already migrated
    if (content.includes("BaseDbValidator")) {
        console.log(`Skipping ${fileName} (already uses BaseDbValidator)`);
        return false;
    }

    // Check if uses BaseValidator
    if (!content.includes("BaseValidator")) {
        console.log(`Skipping ${fileName} (doesn't extend BaseValidator)`);
        return false;
    }

    console.log(`Migrating ${fileName}...`);

    // Extract class name and validator name
    const classMatch = content.match(/export class (\w+) extends BaseValidator/);
    if (!classMatch) {
        console.log(`  ERROR: Could not extract class name from ${fileName}`);
        return false;
    }

    const className = classMatch[1];

    // Extract getName() return value if exists
    const getNameMatch = content.match(/getName\(\)\s*\{\s*return\s+["']([^"']+)["']/);
    const currentName = getNameMatch ? getNameMatch[1] : fileName.replace(".js", "");
    const scannerId = inferScannerId(fileName, currentName);
    const newName = fileName.replace(".js", "");

    console.log(`  Class: ${className}, Scanner ID: ${scannerId}, New Name: ${newName}`);

    // Calculate relative import depth for BaseDbValidator
    const depth = (filePath.match(/validators[\\/]/g) || []).length - 1;
    const relativeBase = "../".repeat(depth + 1);
    const baseDbImport = `${relativeBase}core/base/base-db-validator.js`;

    // 1. Change import
    content = content.replace(
        /import \{ BaseValidator \} from ["'](.+?)base-validator\.js["'];?/,
        `import { BaseDbValidator } from "${baseDbImport}";`
    );

    // 2. Change extends
    content = content.replace(/extends BaseValidator/, "extends BaseDbValidator");

    // 3. Remove loadRegistry calls and this.paths references
    content = content.replace(/await this\.loadRegistry\([^)]+\)/g, "MIGRATION_ERROR_loadRegistry");
    content = content.replace(/this\.loadRegistry\([^)]+\)/g, "MIGRATION_ERROR_loadRegistry");
    content = content.replace(/this\.paths\.getRelative\([^)]+\)/g, "MIGRATION_ERROR_paths");
    content = content.replace(/\.registry\//g, "");

    // 4. Restructure analyzeViolations to use state.data
    // This is complex - just mark for manual review
    if (content.includes("analyzeViolations")) {
        content = content.replace(
            /async analyzeViolations\(_state, _invariants\)/,
            "async analyzeViolations(state, _invariants)"
        );
    }

    // 5. Add getScannerId method after getDisplayName
    const getDisplayNameMatch = content.match(
        /(getDisplayName\(\)\s*\{[\s\S]*?return\s+["'][^"']+["'];?\s*\})/
    );
    if (getDisplayNameMatch) {
        const getScannerId = `\n\n    getScannerId() {\n        return "${scannerId}";\n    }`;
        content = content.replace(getDisplayNameMatch[1], getDisplayNameMatch[1] + getScannerId);
    }

    // 6. Update getName to include -validator suffix
    content = content.replace(
        /getName\(\)\s*\{[\s\S]*?return\s+["'][^"']+["'];?\s*\}/,
        `getName() {\n        return "${newName}";\n    }`
    );

    // 7. Move getName before getDisplayName
    const methods = [];
    let getNameMethod = content.match(/getName\(\)\s*\{[\s\S]*?\}/);
    let getDisplayNameMethod = content.match(/getDisplayName\(\)\s*\{[\s\S]*?\}/);
    let getScannerIdMethod = content.match(/getScannerId\(\)\s*\{[\s\S]*?\}/);

    writeFileSync(filePath, content, "utf-8");
    console.log(`  ✓ Migrated ${fileName}`);
    return true;
}

// Process all validators
import { globSync } from "glob";

const allValidators = globSync(`${validatorsDir}/**/*-validator.js`);
let migratedCount = 0;

for (const validator of allValidators) {
    if (migrateValidator(validator)) {
        migratedCount++;
    }
}

console.log(`\n✓ Migrated ${migratedCount} validators`);
console.log(`\nNOTE: Files with MIGRATION_ERROR_ markers need manual review!`);
