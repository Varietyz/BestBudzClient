#!/usr/bin/env node
/**
 * Comprehensive Compliance Audit for CV Integration (Phase 4 - Agent 5)
 * Verifies architectural patterns and compliance requirements
 */

import { readFileSync } from "fs";
import { getPanelForSetting, getAllPanels } from "../../../root/codebase-validation/core/settings/settings-core.js";

const results = {
    success: true,
    compliance_checks: [],
    key_findings: {},
    critical_files: [
        "root/codebase-validation/core/settings/settings-ui-schema.js",
        "root/codebase-validation/settings.json"
    ],
    blockers: []
};

function check(name, fn) {
    try {
        const result = fn();
        results.compliance_checks.push({
            name,
            status: result.pass ? "PASS" : "FAIL",
            details: result.details,
            evidence: result.evidence
        });
        if (!result.pass) {
            results.success = false;
            if (result.critical) {
                results.blockers.push(name);
            }
        }
        return result;
    } catch (error) {
        results.success = false;
        results.compliance_checks.push({
            name,
            status: "ERROR",
            error: error.message
        });
        results.blockers.push(name);
        return { pass: false, error: error.message };
    }
}

// Load settings.json
const settingsPath = "D:\\GIT\\archlab\\root\\codebase-validation\\settings.json";
const settings = JSON.parse(readFileSync(settingsPath, "utf8"));

// CHECK 1: All settings.json paths map to at least one panel
const check1 = check("All settings.json top-level paths map to panels", () => {
    const topLevelKeys = Object.keys(settings);
    const unmapped = [];
    const mapped = [];

    for (const key of topLevelKeys) {
        const panel = getPanelForSetting(key);
        if (panel) {
            mapped.push({ key, panel: panel.id });
        } else {
            unmapped.push(key);
        }
    }

    return {
        pass: unmapped.length === 0,
        details: `Mapped: ${mapped.length}, Unmapped: ${unmapped.length}`,
        evidence: {
            total_top_level_keys: topLevelKeys.length,
            mapped_count: mapped.length,
            unmapped_keys: unmapped,
            sample_mappings: mapped.slice(0, 5)
        },
        critical: unmapped.length > 0
    };
});

// CHECK 2: Panel IDs are unique
const check2 = check("Panel IDs are unique", () => {
    const panels = getAllPanels();
    const ids = panels.map(p => p.id);
    const uniqueIds = new Set(ids);
    const duplicates = ids.filter((id, index) => ids.indexOf(id) !== index);

    return {
        pass: ids.length === uniqueIds.size,
        details: `Total panels: ${panels.length}, Unique IDs: ${uniqueIds.size}`,
        evidence: {
            panel_count: panels.length,
            unique_count: uniqueIds.size,
            duplicates: duplicates,
            all_ids: ids
        },
        critical: duplicates.length > 0
    };
});

// CHECK 3: Pattern matching works for nested paths
const check3 = check("Pattern matching works for nested paths", () => {
    const testCases = [
        { path: "limits.maxLinesPerFile", expectedPanel: "limits" },
        { path: "enabled.scanners.css-bem", expectedPanel: "scanners" },
        { path: "css.breakpoints.mobileMax", expectedPanel: "css" },
        { path: "rules.formatting.lineLength", expectedPanel: "tooling" },
        { path: "security.csp.enforceNoInlineEvents", expectedPanel: "security" },
        { path: "codeQuality.comments.enforceNoNonFunctional", expectedPanel: "quality" },
        { path: "enabled.validators.format", expectedPanel: "validators" },
        { path: "enabled.fixers.code-comments", expectedPanel: "fixers" },
        { path: "naming.conventions.folders", expectedPanel: "naming" }
    ];

    const failures = [];
    const successes = [];

    for (const testCase of testCases) {
        const panel = getPanelForSetting(testCase.path);
        if (!panel) {
            failures.push({ ...testCase, result: null, reason: "No panel returned" });
        } else if (panel.id !== testCase.expectedPanel) {
            failures.push({ ...testCase, result: panel.id, reason: "Wrong panel" });
        } else {
            successes.push(testCase);
        }
    }

    return {
        pass: failures.length === 0,
        details: `Passed: ${successes.length}/${testCases.length}`,
        evidence: {
            test_cases: testCases.length,
            passed: successes.length,
            failed: failures.length,
            failures: failures
        },
        critical: failures.length > 3
    };
});

// CHECK 4: No orphan settings (comprehensive deep scan)
const check4 = check("No orphan settings (unmapped to panels)", () => {
    const orphans = [];
    const mapped = [];

    function scanObject(obj, prefix = "") {
        for (const [key, value] of Object.entries(obj)) {
            const path = prefix ? `${prefix}.${key}` : key;

            // Test if this path maps to a panel
            const panel = getPanelForSetting(path);
            if (panel) {
                mapped.push({ path, panel: panel.id });
            } else if (typeof value !== "object" || value === null || Array.isArray(value)) {
                // Only flag as orphan if it's a leaf node (actual setting)
                orphans.push(path);
            }

            // Recurse if value is an object (but not array or null)
            if (value && typeof value === "object" && !Array.isArray(value)) {
                scanObject(value, path);
            }
        }
    }

    scanObject(settings);

    return {
        pass: orphans.length === 0,
        details: `Mapped: ${mapped.length} paths, Orphans: ${orphans.length} paths`,
        evidence: {
            total_mapped: mapped.length,
            total_orphans: orphans.length,
            orphan_samples: orphans.slice(0, 20),
            mapped_samples: mapped.slice(0, 10)
        },
        critical: false // Minor issue if some deep paths are orphaned
    };
});

// CHECK 5: Panel order makes sense for UI flow
const check5 = check("Panel order makes sense for UI flow", () => {
    const panels = getAllPanels();
    const expectedOrder = [
        "limits",      // Structure first
        "naming",      // Naming conventions
        "css",         // Styling
        "tooling",     // Tools
        "security",    // Safety
        "quality",     // Code quality
        "scanners",    // Toggles
        "validators",  // Toggles
        "fixers"       // Toggles
    ];

    const actualOrder = panels.map(p => p.id);
    const orderMatches = JSON.stringify(actualOrder) === JSON.stringify(expectedOrder);

    return {
        pass: orderMatches,
        details: orderMatches ? "Panel order matches expected UI flow" : "Panel order differs from expected",
        evidence: {
            expected_order: expectedOrder,
            actual_order: actualOrder,
            matches: orderMatches
        },
        critical: false // Non-critical, just a recommendation
    };
});

// CHECK 6: Verify panel count is exactly 9
const check6 = check("Panel count is exactly 9", () => {
    const panels = getAllPanels();
    const count = panels.length;

    return {
        pass: count === 9,
        details: `Found ${count} panels`,
        evidence: {
            expected: 9,
            actual: count,
            panel_ids: panels.map(p => p.id)
        },
        critical: count !== 9
    };
});

// Generate key findings summary
results.key_findings = {
    all_top_level_mapped: check1.pass,
    panel_ids_unique: check2.pass,
    nested_pattern_matching: check3.pass,
    orphan_settings_check: check4.pass,
    ui_flow_order: check5.pass,
    panel_count_correct: check6.pass
};

// Output results
console.log(JSON.stringify(results, null, 2));
process.exit(results.success ? 0 : 1);
