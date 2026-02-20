#!/usr/bin/env node

/**
 * Architecture Scanner v3 (Simple Folder-Name Matching)
 *
 * RULE: If file is in folder X, filename MUST contain X (singular form)
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, "../../../root/archlab-ide");

// ============================================================================
// Folder → Singular Mapping
// ============================================================================

const FOLDER_SINGULAR_MAP = {
    // Tier 1
    "base": "base",
    "core": "core",

    // Tier 2
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
    "sync": "sync",
    "scanners": "scanner",
    "scripts": "script",

    // Tier 3 (domain folders)
    "terminal": "terminal",
    "claude": "claude",
    "hardware": "hardware",
    "settings": "settings",
    "config": "config",
    "engine": "engine",
    "debug": "debug",
    "styles": "styles",
    "constants": "constants",
    "environment": "environment",
    "project": "project",
    "ai-providers": "ai-providers",
    "terminal-paths": "terminal-paths",
    "cli-bridge": "cli-bridge",
    "database": "database"
};

const TIER_2_FOLDERS = Object.keys(FOLDER_SINGULAR_MAP).filter(k =>
    ["managers", "stores", "services", "databases", "components", "parsers",
     "queues", "trackers", "factories", "utilities", "helpers", "registries",
     "validators", "providers", "handlers", "types", "tiers", "sync", "scanners",
     "scripts", "bridges", "routes"].includes(k)
);

// ============================================================================
// Detection
// ============================================================================

function getImmediateFolder(filePath) {
    const parts = filePath.split(path.sep);
    // Get folder containing the file (not src, main, renderer, shared)
    for (let i = parts.length - 2; i >= 0; i--) {
        const folder = parts[i];
        if (folder !== "src" && folder !== "main" && folder !== "renderer" && folder !== "shared") {
            return folder;
        }
    }
    return null;
}

function detectFolderMarkerInFilename(fileName) {
    // Check if filename contains any Tier 2 folder markers
    const markers = [];

    for (const [folder, singular] of Object.entries(FOLDER_SINGULAR_MAP)) {
        if (TIER_2_FOLDERS.includes(folder)) {
            if (fileName.includes(singular) || fileName.includes(`-${singular}`) || fileName.includes(`${singular}-`)) {
                markers.push({ folder, singular });
            }
        }
    }

    return markers;
}

function analyzeFile(filePath) {
    const relativePath = path.relative(ROOT, filePath);
    const fileName = path.basename(filePath).replace(".ts", "");

    if (fileName === "index") return null; // Skip barrel exports

    const immediateFolder = getImmediateFolder(relativePath);
    if (!immediateFolder) return null;

    const expectedMarker = FOLDER_SINGULAR_MAP[immediateFolder];
    if (!expectedMarker) return null; // Unknown folder

    const violations = [];

    // RULE 1: Filename must contain folder name (singular)
    const hasExpectedMarker = fileName.includes(expectedMarker);

    if (!hasExpectedMarker) {
        violations.push({
            code: "MISSING_FOLDER_MARKER",
            severity: "error",
            message: `File in '${immediateFolder}/' must contain '${expectedMarker}' in filename`,
            folder: immediateFolder,
            expected: `<prefix>-${expectedMarker}.ts or ${expectedMarker}-<suffix>.ts`,
            actual: fileName + ".ts",
            suggestedName: `${fileName}-${expectedMarker}.ts`
        });
    }

    // RULE 2: If filename contains DIFFERENT Tier 2 marker, wrong folder
    const markersInFilename = detectFolderMarkerInFilename(fileName);
    const wrongMarkers = markersInFilename.filter(m => m.folder !== immediateFolder);

    if (wrongMarkers.length > 0 && TIER_2_FOLDERS.includes(immediateFolder)) {
        for (const marker of wrongMarkers) {
            const pathParts = relativePath.split(path.sep);
            const srcIndex = pathParts.findIndex(p => p === "src");
            const context = pathParts[srcIndex + 1]; // main, renderer, shared

            violations.push({
                code: "WRONG_FOLDER",
                severity: "error",
                message: `File contains '${marker.singular}' but is in '${immediateFolder}/' folder`,
                folder: immediateFolder,
                expected: `Should be in ${marker.folder}/`,
                actual: immediateFolder,
                suggestedPath: `src/${context}/${marker.folder}/${fileName}.ts`
            });
        }
    }

    if (violations.length === 0) return null;

    return {
        file: relativePath,
        fileName: fileName + ".ts",
        immediateFolder,
        expectedMarker,
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
    console.log("🔍 Architecture Scanner v3 - Simple Folder-Name Matching\n");
    console.log("RULE: If file is in folder X, filename MUST contain X (singular)\n");

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

    // Group by violation type
    const missingMarker = results.filter(r => r.violations.some(v => v.code === "MISSING_FOLDER_MARKER"));
    const wrongFolder = results.filter(r => r.violations.some(v => v.code === "WRONG_FOLDER"));

    console.log("=".repeat(80));
    console.log(`📊 SUMMARY`);
    console.log("=".repeat(80));
    console.log(`Total files analyzed: ${allFiles.length}`);
    console.log(`Files with violations: ${results.length}`);
    console.log(`  - Missing folder marker in filename: ${missingMarker.length}`);
    console.log(`  - File in wrong folder: ${wrongFolder.length}`);
    console.log();

    // Report: Wrong Folder (more severe)
    if (wrongFolder.length > 0) {
        console.log("=".repeat(80));
        console.log(`❌ WRONG FOLDER (${wrongFolder.length} files) - File belongs in different folder`);
        console.log("=".repeat(80));

        for (const result of wrongFolder.slice(0, 20)) { // Show first 20
            console.log(`\n📄 ${result.file}`);
            console.log(`   In folder: ${result.immediateFolder}/`);

            for (const violation of result.violations) {
                if (violation.code === "WRONG_FOLDER") {
                    console.log(`   ❌ [${violation.code}] ${violation.message}`);
                    console.log(`      Current: ${violation.actual}/`);
                    console.log(`      ✅ Move to: ${violation.suggestedPath}`);
                }
            }
        }

        if (wrongFolder.length > 20) {
            console.log(`\n... and ${wrongFolder.length - 20} more (see JSON report)`);
        }
    }

    // Report: Missing Marker (less severe - just rename)
    if (missingMarker.length > 0) {
        console.log("\n" + "=".repeat(80));
        console.log(`⚠️  MISSING MARKER (${missingMarker.length} files) - Filename needs folder marker`);
        console.log("=".repeat(80));

        for (const result of missingMarker.slice(0, 20)) { // Show first 20
            console.log(`\n📄 ${result.file}`);
            console.log(`   In folder: ${result.immediateFolder}/`);
            console.log(`   Missing: '${result.expectedMarker}' in filename`);

            for (const violation of result.violations) {
                if (violation.code === "MISSING_FOLDER_MARKER") {
                    console.log(`   ⚠️  Current: ${violation.actual}`);
                    console.log(`   ✅ Rename to: ${violation.suggestedName}`);
                }
            }
        }

        if (missingMarker.length > 20) {
            console.log(`\n... and ${missingMarker.length - 20} more (see JSON report)`);
        }
    }

    // Export JSON report
    const reportPath = path.join(__dirname, "architecture-violations-v3.json");
    fs.writeFileSync(reportPath, JSON.stringify({
        timestamp: new Date().toISOString(),
        summary: {
            totalFiles: allFiles.length,
            filesWithViolations: results.length,
            missingMarker: missingMarker.length,
            wrongFolder: wrongFolder.length
        },
        violations: results
    }, null, 2));

    console.log("\n" + "=".repeat(80));
    console.log(`📝 Full report saved to: ${path.relative(ROOT, reportPath)}`);
    console.log("=".repeat(80));

    process.exit(results.length > 0 ? 1 : 0);
}

main();
