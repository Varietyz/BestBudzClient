#!/usr/bin/env node

/**
 * Architecture Scanner
 *
 * Scans archlab-ide codebase for violations of ARCHITECTURE-ORGANIZATION.md rules.
 * Validates tier placement, naming conventions, and inheritance patterns.
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, "../../../root/archlab-ide");

// ============================================================================
// Tier Definitions
// ============================================================================

const TIER_1_FOLDERS = ["base", "core", "cli", "ai", "ui", "ci"];
const TIER_2_FOLDERS = [
    "managers", "stores", "services", "databases", "components",
    "parsers", "queues", "trackers", "factories", "utilities",
    "helpers", "registries", "validators", "providers", "bridges",
    "routes", "api", "scanners", "scripts", "types", "handlers",
    "tiers", "sync"
];
const TIER_3_FOLDERS = [
    "terminal", "claude", "hardware", "settings", "config",
    "engine", "debug", "styles", "paths", "constants",
    "variables", "project", "environment", "database",
    "cli-bridge", "ai-providers", "terminal-paths"
];

// Tier 2 singular forms (folder → suffix)
const TIER_2_SUFFIX_MAP = {
    "managers": "manager",
    "stores": "store",
    "services": "service",
    "databases": "database",
    "components": "component",
    "parsers": "parser",
    "queues": "queue",
    "trackers": "tracker",
    "factories": "factory",
    "utilities": "util",
    "helpers": "helper",
    "registries": "registry",
    "validators": "validator",
    "providers": "provider",
    "bridges": "bridge",
    "routes": "route",
    "handlers": "handler",
    "types": "types",
    "tiers": "tier",
    "sync": "sync"
};

// ============================================================================
// Utility Functions
// ============================================================================

function kebabToPascal(str) {
    return str
        .split("-")
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join("");
}

function getTier(folderPath) {
    const parts = folderPath.split(path.sep);

    for (const part of parts) {
        if (TIER_1_FOLDERS.includes(part)) return { tier: 1, folder: part };
        if (TIER_2_FOLDERS.includes(part)) return { tier: 2, folder: part };
        if (TIER_3_FOLDERS.includes(part)) return { tier: 3, folder: part };
    }

    return { tier: 0, folder: null };
}

function extractExports(content) {
    const exports = [];

    // Match: export class ClassName
    const classMatches = content.matchAll(/export\s+(?:abstract\s+)?class\s+(\w+)/g);
    for (const match of classMatches) {
        exports.push({ type: "class", name: match[1] });
    }

    // Match: export function functionName
    const functionMatches = content.matchAll(/export\s+function\s+(\w+)/g);
    for (const match of functionMatches) {
        exports.push({ type: "function", name: match[1] });
    }

    // Match: export const ConstName
    const constMatches = content.matchAll(/export\s+const\s+(\w+)/g);
    for (const match of constMatches) {
        exports.push({ type: "const", name: match[1] });
    }

    // Match: export interface InterfaceName
    const interfaceMatches = content.matchAll(/export\s+interface\s+(\w+)/g);
    for (const match of interfaceMatches) {
        exports.push({ type: "interface", name: match[1] });
    }

    // Match: export type TypeName
    const typeMatches = content.matchAll(/export\s+type\s+(\w+)/g);
    for (const match of typeMatches) {
        exports.push({ type: "type", name: match[1] });
    }

    return exports;
}

function extractBaseClass(content) {
    // Match: class ClassName extends BaseClass
    const match = content.match(/class\s+\w+\s+extends\s+(\w+)/);
    return match ? match[1] : null;
}

// ============================================================================
// Validation Functions
// ============================================================================

function validateTier1(filePath, fileName, exports, content) {
    const violations = [];
    const { folder } = getTier(filePath);

    // Rule: Tier 1 files should have prefix pattern: <folder>-<identifier>.ts
    const expectedPrefix = folder;
    if (!fileName.startsWith(`${expectedPrefix}-`)) {
        violations.push({
            code: "TIER1_PREFIX",
            severity: "error",
            message: `Tier 1 file must start with '${expectedPrefix}-' prefix`,
            expected: `${expectedPrefix}-<identifier>.ts`,
            actual: fileName
        });
    }

    // Rule: Export name should match filename (PascalCase)
    const primaryExport = exports.find(e => e.type === "class" || e.type === "function");
    if (primaryExport) {
        const expectedName = kebabToPascal(fileName.replace(".ts", ""));
        if (primaryExport.name !== expectedName) {
            violations.push({
                code: "EXPORT_MISMATCH",
                severity: "error",
                message: "Export name must match filename (kebab → PascalCase)",
                expected: expectedName,
                actual: primaryExport.name
            });
        }
    }

    return violations;
}

function validateTier2(filePath, fileName, exports, content) {
    const violations = [];
    const { folder } = getTier(filePath);
    const expectedSuffix = TIER_2_SUFFIX_MAP[folder];

    if (!expectedSuffix) {
        violations.push({
            code: "UNKNOWN_TIER2",
            severity: "warning",
            message: `Unknown Tier 2 folder pattern for '${folder}'`,
            expected: "Define in TIER_2_SUFFIX_MAP",
            actual: folder
        });
        return violations;
    }

    // Rule: Tier 2 files should have suffix pattern: <domain>-<suffix>.ts
    if (!fileName.endsWith(`-${expectedSuffix}.ts`)) {
        violations.push({
            code: "TIER2_SUFFIX",
            severity: "error",
            message: `Tier 2 file must end with '-${expectedSuffix}.ts' suffix`,
            expected: `<domain>-${expectedSuffix}.ts`,
            actual: fileName
        });
    }

    // Rule: Should have domain prefix (not just suffix)
    const withoutSuffix = fileName.replace(`-${expectedSuffix}.ts`, "");
    if (withoutSuffix === "" || withoutSuffix === expectedSuffix) {
        violations.push({
            code: "MISSING_DOMAIN",
            severity: "error",
            message: "Tier 2 file must have domain prefix",
            expected: `<domain>-${expectedSuffix}.ts`,
            actual: fileName
        });
    }

    // Rule: Export name should match filename
    const primaryExport = exports.find(e => e.type === "class");
    if (primaryExport) {
        const expectedName = kebabToPascal(fileName.replace(".ts", ""));
        if (primaryExport.name !== expectedName) {
            violations.push({
                code: "EXPORT_MISMATCH",
                severity: "error",
                message: "Export name must match filename (kebab → PascalCase)",
                expected: expectedName,
                actual: primaryExport.name
            });
        }

        // Rule: Tier 2 should extend Tier 1 base class
        const baseClass = extractBaseClass(content);
        const expectedBase = kebabToPascal(`base-${expectedSuffix}`) ||
                            kebabToPascal(`core-${expectedSuffix}`);

        if (!baseClass) {
            violations.push({
                code: "MISSING_INHERITANCE",
                severity: "warning",
                message: `Tier 2 class should extend Tier 1 base class`,
                expected: `extends Base${kebabToPascal(expectedSuffix)} or Core${kebabToPascal(expectedSuffix)}`,
                actual: "No extends clause found"
            });
        } else if (!baseClass.includes("Base") && !baseClass.includes("Core")) {
            violations.push({
                code: "WRONG_INHERITANCE",
                severity: "warning",
                message: `Tier 2 class should extend Tier 1 base class`,
                expected: `Base/Core class`,
                actual: `extends ${baseClass}`
            });
        }
    }

    return violations;
}

function validateTier3(filePath, fileName, exports, content) {
    const violations = [];
    const { folder } = getTier(filePath);

    // Rule: Tier 3 files should have domain prefix pattern: <domain>-<identifier>.ts
    const expectedPrefix = folder;
    if (!fileName.startsWith(`${expectedPrefix}-`) && fileName !== "index.ts") {
        violations.push({
            code: "TIER3_DOMAIN",
            severity: "error",
            message: `Tier 3 file should start with domain prefix '${expectedPrefix}-'`,
            expected: `${expectedPrefix}-<identifier>.ts`,
            actual: fileName
        });
    }

    // Rule: Export name should match filename
    const primaryExport = exports.find(e => e.type === "class" || e.type === "function");
    if (primaryExport) {
        const expectedName = kebabToPascal(fileName.replace(".ts", ""));
        if (primaryExport.name !== expectedName) {
            violations.push({
                code: "EXPORT_MISMATCH",
                severity: "error",
                message: "Export name must match filename (kebab → PascalCase)",
                expected: expectedName,
                actual: primaryExport.name
            });
        }
    }

    return violations;
}

function validateTypeLocation(filePath, fileName) {
    const violations = [];

    // Rule: All types should be in shared/types/
    if (fileName.endsWith("-types.ts") || fileName.endsWith("-interfaces.ts")) {
        if (!filePath.includes(path.join("shared", "types"))) {
            violations.push({
                code: "TYPE_LOCATION",
                severity: "error",
                message: "Type definitions must be in shared/types/",
                expected: "shared/types/<domain>-types.ts",
                actual: filePath
            });
        }
    }

    return violations;
}

// ============================================================================
// Scanner
// ============================================================================

function scanDirectory(dir, results = []) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);

        if (entry.isDirectory()) {
            // Skip node_modules, dist, build
            if (!["node_modules", "dist", "build", ".git"].includes(entry.name)) {
                scanDirectory(fullPath, results);
            }
        } else if (entry.isFile() && entry.name.endsWith(".ts")) {
            // Skip declaration files and test files
            if (entry.name.endsWith(".d.ts") || entry.name.includes(".test.") || entry.name.includes(".spec.")) {
                continue;
            }

            results.push(fullPath);
        }
    }

    return results;
}

function analyzeFile(filePath) {
    const relativePath = path.relative(ROOT, filePath);
    const fileName = path.basename(filePath);

    // Skip index.ts files (barrel exports)
    if (fileName === "index.ts") {
        return null;
    }

    const content = fs.readFileSync(filePath, "utf-8");
    const exports = extractExports(content);
    const { tier, folder } = getTier(relativePath);

    let violations = [];

    // Validate based on tier
    if (tier === 1) {
        violations = validateTier1(relativePath, fileName, exports, content);
    } else if (tier === 2) {
        violations = validateTier2(relativePath, fileName, exports, content);
    } else if (tier === 3) {
        violations = validateTier3(relativePath, fileName, exports, content);
    } else {
        violations.push({
            code: "UNKNOWN_TIER",
            severity: "warning",
            message: "File not in recognized tier folder",
            expected: "Tier 1/2/3 folder",
            actual: folder || "unknown"
        });
    }

    // Check type location
    violations.push(...validateTypeLocation(relativePath, fileName));

    if (violations.length === 0) {
        return null;
    }

    return {
        file: relativePath,
        tier,
        folder,
        exports: exports.map(e => `${e.type} ${e.name}`),
        violations
    };
}

// ============================================================================
// Main
// ============================================================================

function main() {
    console.log("🔍 Scanning archlab-ide for architectural violations...\n");

    const srcMain = path.join(ROOT, "src", "main");
    const srcRenderer = path.join(ROOT, "src", "renderer");
    const srcShared = path.join(ROOT, "src", "shared");

    const allFiles = [
        ...scanDirectory(srcMain),
        ...scanDirectory(srcRenderer),
        ...scanDirectory(srcShared)
    ];

    console.log(`📂 Found ${allFiles.length} TypeScript files\n`);

    const results = [];
    for (const file of allFiles) {
        const result = analyzeFile(file);
        if (result) {
            results.push(result);
        }
    }

    // Group by severity
    const errors = results.filter(r => r.violations.some(v => v.severity === "error"));
    const warnings = results.filter(r => r.violations.every(v => v.severity === "warning"));

    console.log("=" .repeat(80));
    console.log(`📊 SUMMARY`);
    console.log("=" .repeat(80));
    console.log(`Total files analyzed: ${allFiles.length}`);
    console.log(`Files with violations: ${results.length}`);
    console.log(`  - Errors: ${errors.length}`);
    console.log(`  - Warnings: ${warnings.length}`);
    console.log();

    // Report errors
    if (errors.length > 0) {
        console.log("=" .repeat(80));
        console.log(`❌ ERRORS (${errors.length} files)`);
        console.log("=" .repeat(80));

        for (const result of errors) {
            console.log(`\n📄 ${result.file}`);
            console.log(`   Tier: ${result.tier} (${result.folder || "N/A"})`);
            console.log(`   Exports: ${result.exports.join(", ") || "none"}`);

            for (const violation of result.violations) {
                if (violation.severity === "error") {
                    console.log(`   ❌ [${violation.code}] ${violation.message}`);
                    console.log(`      Expected: ${violation.expected}`);
                    console.log(`      Actual: ${violation.actual}`);
                }
            }
        }
    }

    // Report warnings
    if (warnings.length > 0) {
        console.log("\n" + "=" .repeat(80));
        console.log(`⚠️  WARNINGS (${warnings.length} files)`);
        console.log("=" .repeat(80));

        for (const result of warnings) {
            console.log(`\n📄 ${result.file}`);
            console.log(`   Tier: ${result.tier} (${result.folder || "N/A"})`);

            for (const violation of result.violations) {
                console.log(`   ⚠️  [${violation.code}] ${violation.message}`);
                console.log(`      Expected: ${violation.expected}`);
                console.log(`      Actual: ${violation.actual}`);
            }
        }
    }

    // Export JSON report
    const reportPath = path.join(__dirname, "architecture-violations.json");
    fs.writeFileSync(reportPath, JSON.stringify({
        timestamp: new Date().toISOString(),
        summary: {
            totalFiles: allFiles.length,
            filesWithViolations: results.length,
            errors: errors.length,
            warnings: warnings.length
        },
        violations: results
    }, null, 2));

    console.log("\n" + "=" .repeat(80));
    console.log(`📝 Full report saved to: ${path.relative(ROOT, reportPath)}`);
    console.log("=" .repeat(80));

    process.exit(errors.length > 0 ? 1 : 0);
}

main();
