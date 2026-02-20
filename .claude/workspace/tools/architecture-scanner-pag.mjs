#!/usr/bin/env node

/**
 * Architecture Scanner - PAG Protocol Implementation
 *
 * Implements ARCHITECTURE-ORGANIZATION.pag.md protocol:
 * - LAW 0: Single concern enforcement
 * - Axis detection (1: STABLE, 2: HELPS, 3: DEFINES, 4: EXTERNAL)
 * - Role classification from global_role_registry
 * - Location validation
 * - Naming rules
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, "../../../root/archlab-ide");

// ============================================================================
// GLOBAL ROLE REGISTRY (AUTHORITATIVE - Single Source of Truth)
// From ARCHITECTURE-ORGANIZATION.pag.md
// ============================================================================

const GLOBAL_ROLE_REGISTRY = {
    // Axis 1: STABLE (Runtime execution with lifecycle)
    "manager": {axis: 1, pattern: "*-manager.ts", base: "BaseManager", folder: "managers"},
    "service": {axis: 1, pattern: "*-service.ts", base: "BaseService", folder: "services"},
    "component": {axis: 1, pattern: "*-component.ts", base: "BaseComponent", folder: "components"},
    "database": {axis: 1, pattern: "*-database.ts", base: "BaseDatabase", folder: "databases"},
    "queue": {axis: 1, pattern: "*-queue.ts", base: "BaseQueue", folder: "queues"},
    "handler": {axis: 1, composite: true, pattern: "*-{area}-handler.ts", base: "BaseHandler", folder: "handlers"},
    "registry": {axis: 1, pattern: "*-registry.ts", base: "BaseRegistry", folder: "registries"},
    "provider": {axis: 1, pattern: "*-provider.ts", base: "BaseProvider", folder: "providers"},
    "store": {axis: 1, pattern: "*-store.ts", base: "BaseStore", folder: "stores"},
    "tracker": {axis: 1, pattern: "*-tracker.ts", base: "BaseTracker", folder: "trackers"},
    "factory": {axis: 1, pattern: "*-factory.ts", base: "BaseFactory", folder: "factories"},

    // Axis 2: HELPS (Pure functions/stateless)
    "helper": {axis: 2, pattern: "*-helper.ts", purity: "REQUIRED", folder: "helpers"},
    "util": {axis: 2, pattern: "*-util.ts", purity: "REQUIRED", folder: "utilities"},
    "parser": {axis: 2, pattern: "*-parser.ts", purity: "REQUIRED", split_rule: "IF state/lifecycle → SPLIT", folder: "parsers"},
    "validator": {axis: 2, pattern: "*-validator.ts", purity: "REQUIRED", folder: "validators"},
    "formatter": {axis: 2, pattern: "*-formatter.ts", purity: "REQUIRED", folder: "formatters"},

    // Axis 3: DEFINES (Definitions/declarative)
    "types": {axis: 3, pattern: "*-types.ts", aggregate: true, folder: "types", centralized: "src/shared/types/"},
    "constants": {axis: 3, pattern: "*-constants.ts", aggregate: true, folder: "constants", centralized: "src/shared/constants/"},
    "config": {axis: 3, pattern: "*-config.ts", folder: "config", centralized: "src/shared/config/"},
    "schemas": {axis: 3, pattern: "*-schemas.ts", folder: "schemas", centralized: "src/shared/schemas/"},

    // Axis 4: EXTERNAL (Build-time orchestration)
    "script": {axis: 4, pattern: "*-script.ts", folder: "scripts", location: "scripts/"}
};

// ============================================================================
// AXIS DEFINITIONS
// ============================================================================

const ARCHITECTURAL_AXES = {
    1: {
        name: "STABLE",
        description: "Runtime execution with lifecycle, state, and side effects",
        characteristic: "HAS lifecycle methods (init, cleanup, destroy)",
        inheritance: true,
        base_location: "src/{context}/base/",
        tier_2_location: "src/{context}/{role}s/"
    },
    2: {
        name: "HELPS",
        description: "Pure functions or stateless classes assisting runtime",
        characteristic: "ONLY pure functions OR stateless transformations",
        inheritance: false,
        tier_2_location: "src/{context}/{role}s/"
    },
    3: {
        name: "DEFINES",
        description: "Declarative definitions shaping what's possible",
        characteristic: "ONLY types/interfaces/constants (no execution)",
        inheritance: false,
        centralized: true,
        location: "src/shared/{artifact}s/"
    },
    4: {
        name: "EXTERNAL",
        description: "Build-time orchestration outside application runtime",
        characteristic: "Runs outside src/ tree",
        inheritance: false,
        location: "scripts/"
    }
};

// ============================================================================
// FILE ANALYSIS
// ============================================================================

function analyzeFile(filePath) {
    const relativePath = path.relative(ROOT, filePath);
    const fileName = path.basename(filePath, ".ts");

    if (fileName === "index") return null; // Skip barrel exports

    const result = {
        file: relativePath,
        fileName: fileName + ".ts",
        violations: []
    };

    // Phase 1: Detect role from filename
    const detectedRole = detectRoleFromFilename(fileName);
    if (!detectedRole) {
        result.violations.push({
            code: "ROLE_NOT_DETECTED",
            severity: "error",
            message: `Cannot determine role from filename`,
            fileName: fileName + ".ts",
            action: "Filename must match a role pattern from global_role_registry"
        });
        return result;
    }

    result.detectedRole = detectedRole;
    result.axis = GLOBAL_ROLE_REGISTRY[detectedRole.role].axis;
    result.axisName = ARCHITECTURAL_AXES[result.axis].name;

    // Phase 2: Validate location based on axis
    const locationCheck = validateLocation(relativePath, result.axis, detectedRole);
    if (locationCheck.violation) {
        result.violations.push(locationCheck);
    }

    // Phase 3: Validate naming
    const namingCheck = validateNaming(fileName, detectedRole);
    if (namingCheck.length > 0) {
        result.violations.push(...namingCheck);
    }

    return result;
}

function detectRoleFromFilename(fileName) {
    // Check each role pattern
    for (const [roleName, roleInfo] of Object.entries(GLOBAL_ROLE_REGISTRY)) {
        const pattern = roleInfo.pattern;

        if (roleInfo.composite) {
            // Composite pattern: *-{area}-handler.ts
            const compositeRegex = /^(.+)-(\w+)-handler$/;
            if (compositeRegex.test(fileName)) {
                return {role: roleName, domain: RegExp.$1, subdomain: RegExp.$2, composite: true};
            }
        } else {
            // Simple pattern: *-{role}.ts
            const suffix = `-${roleName}`;
            if (fileName.endsWith(suffix)) {
                const domain = fileName.substring(0, fileName.length - suffix.length);
                return {role: roleName, domain: domain};
            }
        }
    }

    return null;
}

function validateLocation(relativePath, axis, detectedRole) {
    const roleInfo = GLOBAL_ROLE_REGISTRY[detectedRole.role];
    const parts = relativePath.split(path.sep);

    // Axis 3: MUST be centralized in shared/
    if (axis === 3) {
        const expectedPrefix = roleInfo.centralized.replace(/\//g, path.sep);
        if (!relativePath.startsWith(expectedPrefix)) {
            return {
                code: "WRONG_LOCATION_AXIS_3",
                severity: "error",
                message: `Axis 3 (DEFINES) files MUST be in ${roleInfo.centralized}`,
                current: relativePath,
                expected: roleInfo.centralized,
                action: `MOVE to ${roleInfo.centralized}`,
                violation: true
            };
        }
        return {violation: false};
    }

    // Axis 4: MUST be outside src/
    if (axis === 4) {
        if (relativePath.startsWith("src" + path.sep)) {
            return {
                code: "WRONG_LOCATION_AXIS_4",
                severity: "error",
                message: `Axis 4 (EXTERNAL) files MUST be outside src/`,
                current: relativePath,
                expected: roleInfo.location,
                action: `MOVE to ${roleInfo.location}`,
                violation: true
            };
        }
        return {violation: false};
    }

    // Axis 1 & 2: MUST be in src/{context}/{role}s/
    const expectedFolder = roleInfo.folder;
    const hasRoleFolder = parts.some(p => p === expectedFolder);

    if (!hasRoleFolder) {
        const srcIndex = parts.findIndex(p => p === "src");
        const context = parts[srcIndex + 1]; // main, renderer, shared
        const expected = `src${path.sep}${context}${path.sep}${expectedFolder}${path.sep}`;

        return {
            code: "WRONG_LOCATION",
            severity: "error",
            message: `${ARCHITECTURAL_AXES[axis].name} role '${detectedRole.role}' must be in ${expectedFolder}/ folder`,
            current: relativePath,
            expected: expected,
            action: `MOVE to ${expected}`,
            violation: true
        };
    }

    return {violation: false};
}

function validateNaming(fileName, detectedRole) {
    const violations = [];

    // R1: Kebab-case check
    if (!/^[a-z]+(-[a-z0-9]+)*$/.test(fileName)) {
        violations.push({
            code: "NOT_KEBAB_CASE",
            severity: "error",
            rule: "R1",
            message: "Filename must be kebab-case",
            current: fileName + ".ts",
            action: "Convert to kebab-case"
        });
    }

    // R3: Domain present check
    if (!detectedRole.domain || detectedRole.domain.length === 0) {
        violations.push({
            code: "MISSING_DOMAIN",
            severity: "error",
            rule: "R3",
            message: "Domain prefix missing in filename",
            current: fileName + ".ts",
            expected: "{domain}-{role}.ts",
            action: "Add domain prefix"
        });
    }

    // R6: Composite roles must have subdomain
    if (detectedRole.composite && !detectedRole.subdomain) {
        violations.push({
            code: "MISSING_SUBDOMAIN",
            severity: "error",
            rule: "R6",
            message: "Composite role must have subdomain",
            current: fileName + ".ts",
            expected: "{domain}-{subdomain}-handler.ts",
            action: "Add subdomain identifier"
        });
    }

    return violations;
}

// ============================================================================
// SCAN CODEBASE
// ============================================================================

function scanCodebase(rootPath) {
    const results = {
        timestamp: new Date().toISOString(),
        totalFiles: 0,
        filesWithViolations: 0,
        violationsByType: {},
        files: []
    };

    function scanDir(dir) {
        const entries = fs.readdirSync(dir, {withFileTypes: true});

        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);

            if (entry.isDirectory()) {
                if (!["node_modules", "dist", "build", ".git", ".vscode"].includes(entry.name)) {
                    scanDir(fullPath);
                }
            } else if (entry.isFile() && entry.name.endsWith(".ts")) {
                if (!entry.name.endsWith(".d.ts") &&
                    !entry.name.includes(".test.") &&
                    !entry.name.includes(".spec.")) {

                    results.totalFiles++;
                    const analysis = analyzeFile(fullPath);

                    if (analysis && analysis.violations.length > 0) {
                        results.filesWithViolations++;
                        results.files.push(analysis);

                        // Count violations by type
                        for (const violation of analysis.violations) {
                            results.violationsByType[violation.code] =
                                (results.violationsByType[violation.code] || 0) + 1;
                        }
                    }
                }
            }
        }
    }

    scanDir(path.join(rootPath, "src"));
    return results;
}

// ============================================================================
// REPORT GENERATION
// ============================================================================

function generateReport(results) {
    console.log("🔍 Architecture Scanner - PAG Protocol Implementation\n");
    console.log("ARCHITECTURE-ORGANIZATION.pag.md compliant scanner");
    console.log("Implements: 4-axis classification, global_role_registry, location validation\n");

    console.log("================================================================================");
    console.log("📊 SUMMARY");
    console.log("================================================================================");
    console.log(`Total files analyzed: ${results.totalFiles}`);
    console.log(`Files with violations: ${results.filesWithViolations}`);
    console.log("");

    if (Object.keys(results.violationsByType).length > 0) {
        console.log("Violations by type:");
        for (const [code, count] of Object.entries(results.violationsByType).sort((a, b) => b[1] - a[1])) {
            console.log(`  - ${code}: ${count}`);
        }
        console.log("");
    }

    // Group by violation type
    const wrongLocationAxis3 = results.files.filter(f =>
        f.violations.some(v => v.code === "WRONG_LOCATION_AXIS_3"));
    const wrongLocation = results.files.filter(f =>
        f.violations.some(v => v.code === "WRONG_LOCATION"));
    const roleNotDetected = results.files.filter(f =>
        f.violations.some(v => v.code === "ROLE_NOT_DETECTED"));

    if (wrongLocationAxis3.length > 0) {
        console.log("================================================================================");
        console.log(`❌ WRONG LOCATION - Axis 3 Violation (${wrongLocationAxis3.length} files)`);
        console.log("================================================================================");
        console.log("Axis 3 (DEFINES) files MUST be centralized in src/shared/\n");

        for (const file of wrongLocationAxis3.slice(0, 10)) {
            const violation = file.violations.find(v => v.code === "WRONG_LOCATION_AXIS_3");
            console.log(`📄 ${file.file}`);
            console.log(`   Role: ${file.detectedRole.role}`);
            console.log(`   ❌ ${violation.message}`);
            console.log(`   ✅ ${violation.action}\n`);
        }
        if (wrongLocationAxis3.length > 10) {
            console.log(`   ... and ${wrongLocationAxis3.length - 10} more\n`);
        }
    }

    if (wrongLocation.length > 0) {
        console.log("================================================================================");
        console.log(`❌ WRONG LOCATION - Role Folder (${wrongLocation.length} files)`);
        console.log("================================================================================");
        console.log("Files must be in folder matching their role\n");

        for (const file of wrongLocation.slice(0, 10)) {
            const violation = file.violations.find(v => v.code === "WRONG_LOCATION");
            console.log(`📄 ${file.file}`);
            console.log(`   Role: ${file.detectedRole.role} (Axis ${file.axis}: ${file.axisName})`);
            console.log(`   ❌ ${violation.message}`);
            console.log(`   ✅ ${violation.action}\n`);
        }
        if (wrongLocation.length > 10) {
            console.log(`   ... and ${wrongLocation.length - 10} more\n`);
        }
    }

    if (roleNotDetected.length > 0) {
        console.log("================================================================================");
        console.log(`⚠️  ROLE NOT DETECTED (${roleNotDetected.length} files)`);
        console.log("================================================================================");
        console.log("Filename doesn't match any role pattern from global_role_registry\n");

        for (const file of roleNotDetected.slice(0, 10)) {
            console.log(`📄 ${file.file}`);
            console.log(`   ⚠️  Filename: ${file.fileName}`);
            console.log(`   Action: Add role suffix (e.g., -manager, -service, -helper, -types)\n`);
        }
        if (roleNotDetected.length > 10) {
            console.log(`   ... and ${roleNotDetected.length - 10} more\n`);
        }
    }

    console.log("================================================================================");
    const reportPath = path.join(__dirname, "architecture-violations-pag.json");
    fs.writeFileSync(reportPath, JSON.stringify(results, null, 2));
    console.log(`📝 Full report saved to: ${path.relative(ROOT, reportPath)}`);
    console.log("================================================================================");
}

// ============================================================================
// MAIN
// ============================================================================

function main() {
    const results = scanCodebase(ROOT);
    generateReport(results);

    if (results.filesWithViolations > 0) {
        process.exit(1);
    }
}

main();
