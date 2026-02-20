#!/usr/bin/env node

/**
 * Architecture Scanner v2 (Semantic Detection)
 *
 * Scans archlab-ide codebase with semantic role detection.
 * Analyzes what files ACTUALLY ARE, not just folder location.
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
    "handlers": "handler",
    "types": "types"
};

// ============================================================================
// Semantic Role Detection
// ============================================================================

function detectSemanticRole(fileName, content, exports) {
    const name = fileName.replace(".ts", "");

    // Database detection
    if (name.includes("-db") || name.endsWith("db") ||
        name.includes("database") ||
        content.includes("CREATE TABLE") ||
        content.includes("Database") && exports.some(e => e.name.includes("Db") || e.name.includes("Database"))) {
        return { role: "database", tier: 2, folder: "databases" };
    }

    // Service detection
    if (name.includes("-service") ||
        exports.some(e => e.name.endsWith("Service"))) {
        return { role: "service", tier: 2, folder: "services" };
    }

    // Manager detection
    if (name.includes("-manager") ||
        exports.some(e => e.name.endsWith("Manager"))) {
        return { role: "manager", tier: 2, folder: "managers" };
    }

    // Component detection
    if (name.includes("-component") ||
        exports.some(e => e.name.endsWith("Component")) ||
        content.includes("DOM.createElement")) {
        return { role: "component", tier: 2, folder: "components" };
    }

    // Store detection
    if (name.includes("-store") ||
        exports.some(e => e.name.endsWith("Store"))) {
        return { role: "store", tier: 2, folder: "stores" };
    }

    // Registry detection
    if (name.includes("-registry") ||
        exports.some(e => e.name.endsWith("Registry"))) {
        return { role: "registry", tier: 2, folder: "registries" };
    }

    // Parser detection
    if (name.includes("-parser") ||
        exports.some(e => e.name.endsWith("Parser"))) {
        return { role: "parser", tier: 2, folder: "parsers" };
    }

    // Tracker detection
    if (name.includes("-tracker") ||
        exports.some(e => e.name.endsWith("Tracker"))) {
        return { role: "tracker", tier: 2, folder: "trackers" };
    }

    // Handler detection (IPC handlers)
    if (name.includes("ipc-handler") || name.includes("-handler") ||
        exports.some(e => e.name.includes("Handler"))) {
        return { role: "handler", tier: 2, folder: "handlers" };
    }

    // Types detection
    if (name.includes("-types") || name.includes("-interfaces") ||
        exports.every(e => e.type === "interface" || e.type === "type")) {
        return { role: "types", tier: 2, folder: "types" };
    }

    // Helper/utility detection
    if (name.includes("-helper") || name.includes("-util")) {
        return { role: "helper", tier: 2, folder: "helpers" };
    }

    // Base class detection (Tier 1)
    if (name.startsWith("base-") ||
        exports.some(e => e.name.startsWith("Base") && content.includes("abstract class"))) {
        return { role: "base", tier: 1, folder: "base" };
    }

    // Core class detection (Tier 1)
    if (name.startsWith("core-") ||
        exports.some(e => e.name.startsWith("Core"))) {
        return { role: "core", tier: 1, folder: "core" };
    }

    // If no pattern matches, it's domain-specific (Tier 3)
    return { role: "domain-specific", tier: 3, folder: null };
}

// ============================================================================
// Utility Functions
// ============================================================================

function kebabToPascal(str) {
    return str
        .split("-")
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join("");
}

function getDeepestTierFolder(folderPath) {
    const parts = folderPath.split(path.sep).reverse(); // Reverse to check deepest first

    for (const part of parts) {
        if (TIER_1_FOLDERS.includes(part)) return { tier: 1, folder: part };
        if (TIER_2_FOLDERS.includes(part)) return { tier: 2, folder: part };
    }

    // Check for Tier 3 (domain folders)
    for (const part of parts) {
        // Domain folders are typically specific names
        if (part === "terminal" || part === "claude" || part === "hardware" ||
            part === "ai-providers" || part === "terminal-paths" ||
            part === "settings" || part === "environment" || part === "config") {
            return { tier: 3, folder: part };
        }
    }

    return { tier: 0, folder: null };
}

function extractExports(content) {
    const exports = [];

    const classMatches = content.matchAll(/export\s+(?:abstract\s+)?class\s+(\w+)/g);
    for (const match of classMatches) {
        exports.push({ type: "class", name: match[1] });
    }

    const functionMatches = content.matchAll(/export\s+function\s+(\w+)/g);
    for (const match of functionMatches) {
        exports.push({ type: "function", name: match[1] });
    }

    const constMatches = content.matchAll(/export\s+const\s+(\w+)/g);
    for (const match of constMatches) {
        exports.push({ type: "const", name: match[1] });
    }

    const interfaceMatches = content.matchAll(/export\s+interface\s+(\w+)/g);
    for (const match of interfaceMatches) {
        exports.push({ type: "interface", name: match[1] });
    }

    const typeMatches = content.matchAll(/export\s+type\s+(\w+)/g);
    for (const match of typeMatches) {
        exports.push({ type: "type", name: match[1] });
    }

    return exports;
}

function extractBaseClass(content) {
    const match = content.match(/class\s+\w+\s+extends\s+(\w+)/);
    return match ? match[1] : null;
}

// ============================================================================
// Validation
// ============================================================================

function analyzeFile(filePath) {
    const relativePath = path.relative(ROOT, filePath);
    const fileName = path.basename(filePath);

    if (fileName === "index.ts") return null;

    const content = fs.readFileSync(filePath, "utf-8");
    const exports = extractExports(content);

    // STEP 1: Detect what this file ACTUALLY IS (semantic role)
    const semanticRole = detectSemanticRole(fileName, content, exports);

    // STEP 2: Detect where it CURRENTLY IS (folder tier)
    const currentTier = getDeepestTierFolder(relativePath);

    // STEP 3: Check if location matches semantic role
    const violations = [];

    // Check if file is in correct tier folder
    if (semanticRole.tier === 2 && semanticRole.folder) {
        // This should be in a Tier 2 folder
        const expectedFolder = semanticRole.folder;

        if (currentTier.folder !== expectedFolder) {
            // File is in wrong folder
            const correctPath = relativePath
                .split(path.sep)
                .filter(p => !TIER_2_FOLDERS.includes(p) && p !== currentTier.folder)
                .join(path.sep);

            const pathParts = relativePath.split(path.sep);
            const srcIndex = pathParts.findIndex(p => p === "src");
            const contextIndex = srcIndex + 1; // main, renderer, or shared
            const context = pathParts[contextIndex];

            violations.push({
                code: "WRONG_TIER_FOLDER",
                severity: "error",
                message: `File is a ${semanticRole.role} but not in ${expectedFolder}/ folder`,
                expected: `src/${context}/${expectedFolder}/<domain>-${TIER_2_SUFFIX_MAP[expectedFolder]}.ts`,
                actual: relativePath,
                suggestedPath: `src/${context}/${expectedFolder}/${fileName.replace(".ts", "")}-${TIER_2_SUFFIX_MAP[expectedFolder]}.ts`
            });
        } else {
            // Correct folder, check naming
            const expectedSuffix = TIER_2_SUFFIX_MAP[expectedFolder];
            if (!fileName.endsWith(`-${expectedSuffix}.ts`)) {
                violations.push({
                    code: "TIER2_SUFFIX",
                    severity: "error",
                    message: `Tier 2 file must end with '-${expectedSuffix}.ts'`,
                    expected: `<domain>-${expectedSuffix}.ts`,
                    actual: fileName,
                    suggestedName: `${fileName.replace(".ts", "")}-${expectedSuffix}.ts`
                });
            }
        }
    }

    // Check types location
    if (semanticRole.role === "types" && !relativePath.includes(path.join("shared", "types"))) {
        violations.push({
            code: "TYPE_LOCATION",
            severity: "error",
            message: "Type definitions must be in shared/types/",
            expected: "shared/types/<domain>-types.ts",
            actual: relativePath,
            suggestedPath: relativePath.replace(/src[\\\/](main|renderer)[\\\/].*[\\\/]/, "src/shared/types/")
        });
    }

    // Check inheritance for Tier 2
    if (semanticRole.tier === 2 && currentTier.tier === 2 && currentTier.folder === semanticRole.folder) {
        const baseClass = extractBaseClass(content);
        const primaryClass = exports.find(e => e.type === "class");

        if (primaryClass && !baseClass) {
            const expectedBase = `Base${kebabToPascal(semanticRole.role)}`;
            violations.push({
                code: "MISSING_INHERITANCE",
                severity: "warning",
                message: `Tier 2 class should extend base class`,
                expected: `extends ${expectedBase}`,
                actual: "No extends clause found"
            });
        }
    }

    if (violations.length === 0) return null;

    return {
        file: relativePath,
        detectedRole: semanticRole.role,
        detectedTier: semanticRole.tier,
        currentFolder: currentTier.folder,
        currentTier: currentTier.tier,
        exports: exports.map(e => `${e.type} ${e.name}`),
        violations
    };
}

// ============================================================================
// Scanner
// ============================================================================

function scanDirectory(dir, results = []) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);

        if (entry.isDirectory()) {
            if (!["node_modules", "dist", "build", ".git"].includes(entry.name)) {
                scanDirectory(fullPath, results);
            }
        } else if (entry.isFile() && entry.name.endsWith(".ts")) {
            if (entry.name.endsWith(".d.ts") || entry.name.includes(".test.") || entry.name.includes(".spec.")) {
                continue;
            }
            results.push(fullPath);
        }
    }

    return results;
}

// ============================================================================
// Main
// ============================================================================

function main() {
    console.log("🔍 Scanning archlab-ide with SEMANTIC DETECTION...\n");

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

    const errors = results.filter(r => r.violations.some(v => v.severity === "error"));
    const warnings = results.filter(r => r.violations.every(v => v.severity === "warning"));

    console.log("=".repeat(80));
    console.log(`📊 SUMMARY`);
    console.log("=".repeat(80));
    console.log(`Total files analyzed: ${allFiles.length}`);
    console.log(`Files with violations: ${results.length}`);
    console.log(`  - Errors: ${errors.length}`);
    console.log(`  - Warnings: ${warnings.length}`);
    console.log();

    // Report errors with suggestions
    if (errors.length > 0) {
        console.log("=".repeat(80));
        console.log(`❌ ERRORS (${errors.length} files)`);
        console.log("=".repeat(80));

        for (const result of errors) {
            console.log(`\n📄 ${result.file}`);
            console.log(`   Detected Role: ${result.detectedRole} (Tier ${result.detectedTier})`);
            console.log(`   Current Location: ${result.currentFolder || "root"} (Tier ${result.currentTier})`);
            console.log(`   Exports: ${result.exports.join(", ") || "none"}`);

            for (const violation of result.violations) {
                if (violation.severity === "error") {
                    console.log(`   ❌ [${violation.code}] ${violation.message}`);
                    console.log(`      Expected: ${violation.expected}`);
                    console.log(`      Actual: ${violation.actual}`);
                    if (violation.suggestedPath) {
                        console.log(`      ✅ Suggested: ${violation.suggestedPath}`);
                    }
                    if (violation.suggestedName) {
                        console.log(`      ✅ Suggested: ${violation.suggestedName}`);
                    }
                }
            }
        }
    }

    // Export JSON report
    const reportPath = path.join(__dirname, "architecture-violations-v2.json");
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

    console.log("\n" + "=".repeat(80));
    console.log(`📝 Full report saved to: ${path.relative(ROOT, reportPath)}`);
    console.log("=".repeat(80));

    process.exit(errors.length > 0 ? 1 : 0);
}

main();
