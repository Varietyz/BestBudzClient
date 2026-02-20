#!/usr/bin/env node
/**
 * Comprehensive UI Schema Test Suite - Phase 4
 * Tests all UI schema functions per Agent 6 specification
 */

import { getPanelForSetting, getSettingsForPanel, getAllPanels, UI_PANELS } from "../../../root/codebase-validation/core/settings/settings-ui-schema.js";

const results = {
    success: true,
    total: 0,
    passed: 0,
    failed: 0,
    tests: []
};

function test(name, fn) {
    results.total++;
    try {
        const result = fn();
        results.passed++;
        results.tests.push({ name, status: "PASS", result });
        console.log(`✓ ${name}`);
    } catch (error) {
        results.failed++;
        results.success = false;
        results.tests.push({ name, status: "FAIL", error: error.message });
        console.log(`✗ ${name}: ${error.message}`);
    }
}

console.log("=== Phase 4: UI Schema Comprehensive Test Suite ===\n");

// TEST 1: getPanelForSetting("limits.maxLinesPerFile") returns panel with id: "limits"
test("TEST 1: getPanelForSetting('limits.maxLinesPerFile') returns panel with id 'limits'", () => {
    const panel = getPanelForSetting("limits.maxLinesPerFile");
    if (!panel) throw new Error("getPanelForSetting returned null");
    if (panel.id !== "limits") throw new Error(`Expected panel id 'limits', got '${panel.id}'`);
    return { panelId: panel.id, label: panel.label };
});

// TEST 2: getPanelForSetting("enabled.scanners.css-bem-violations") returns panel with id: "scanners"
test("TEST 2: getPanelForSetting('enabled.scanners.css-bem-violations') returns panel with id 'scanners'", () => {
    const panel = getPanelForSetting("enabled.scanners.css-bem-violations");
    if (!panel) throw new Error("getPanelForSetting returned null");
    if (panel.id !== "scanners") throw new Error(`Expected panel id 'scanners', got '${panel.id}'`);
    return { panelId: panel.id, label: panel.label };
});

// TEST 3: getSettingsForPanel("naming") returns ["naming.*", "projectStructure.*"]
test("TEST 3: getSettingsForPanel('naming') returns expected patterns", () => {
    const settings = getSettingsForPanel("naming");
    if (!settings) throw new Error("getSettingsForPanel returned null");
    if (!Array.isArray(settings)) throw new Error("getSettingsForPanel did not return an array");
    const expected = ["naming.*", "projectStructure.*"];
    if (settings.length !== expected.length) {
        throw new Error(`Expected ${expected.length} patterns, got ${settings.length}: ${JSON.stringify(settings)}`);
    }
    for (const exp of expected) {
        if (!settings.includes(exp)) {
            throw new Error(`Expected pattern '${exp}' not found in: ${JSON.stringify(settings)}`);
        }
    }
    return { settings };
});

// TEST 4: getAllPanels() returns 9 panels with required properties
test("TEST 4: getAllPanels() returns 9 panels", () => {
    const panels = getAllPanels();
    if (!Array.isArray(panels)) throw new Error("getAllPanels did not return an array");
    if (panels.length !== 9) throw new Error(`Expected 9 panels, got ${panels.length}`);
    return { panelCount: panels.length };
});

test("TEST 4a: Each panel has id property", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!panel.id) throw new Error(`Panel missing id property: ${JSON.stringify(panel)}`);
    }
    return { verified: "all panels have id" };
});

test("TEST 4b: Each panel has label property", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!panel.label) throw new Error(`Panel ${panel.id} missing label property`);
    }
    return { verified: "all panels have label" };
});

test("TEST 4c: Each panel has icon property", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!panel.icon) throw new Error(`Panel ${panel.id} missing icon property`);
    }
    return { verified: "all panels have icon" };
});

test("TEST 4d: Each panel has description property", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!panel.description) throw new Error(`Panel ${panel.id} missing description property`);
    }
    return { verified: "all panels have description" };
});

test("TEST 4e: Each panel has settings array", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!Array.isArray(panel.settings)) throw new Error(`Panel ${panel.id} settings is not an array`);
    }
    return { verified: "all panels have settings array" };
});

// TEST 5: getPanelForSetting("nonexistent.path") returns null
test("TEST 5: getPanelForSetting('nonexistent.path') returns null", () => {
    const panel = getPanelForSetting("nonexistent.path");
    if (panel !== null) throw new Error(`Expected null, got ${JSON.stringify(panel)}`);
    return { result: null };
});

// TEST 6: Panel structure validation
test("TEST 6a: Panel IDs are unique", () => {
    const panels = getAllPanels();
    const ids = panels.map(p => p.id);
    const uniqueIds = [...new Set(ids)];
    if (ids.length !== uniqueIds.length) {
        const duplicates = ids.filter((id, index) => ids.indexOf(id) !== index);
        throw new Error(`Duplicate panel IDs found: ${duplicates.join(", ")}`);
    }
    return { uniqueIds: ids };
});

test("TEST 6b: UI_PANELS is frozen (immutable)", () => {
    if (!Object.isFrozen(UI_PANELS)) throw new Error("UI_PANELS is not frozen");
    // Try to modify to verify immutability
    try {
        UI_PANELS.push({ id: "test" });
    } catch (e) {
        // This is expected - frozen objects throw on modification in strict mode
    }
    if (UI_PANELS.length !== 9) throw new Error(`UI_PANELS was modified, length is now ${UI_PANELS.length}`);
    return { frozen: true };
});

// Additional pattern matching tests
test("TEST 7: Pattern matching for 'css.primaryColor'", () => {
    const panel = getPanelForSetting("css.primaryColor");
    if (!panel) throw new Error("getPanelForSetting returned null for css.primaryColor");
    if (panel.id !== "css") throw new Error(`Expected panel id 'css', got '${panel.id}'`);
    return { panelId: panel.id };
});

test("TEST 8: Pattern matching for 'enabled.validators.html-structure'", () => {
    const panel = getPanelForSetting("enabled.validators.html-structure");
    if (!panel) throw new Error("getPanelForSetting returned null for enabled.validators.html-structure");
    if (panel.id !== "validators") throw new Error(`Expected panel id 'validators', got '${panel.id}'`);
    return { panelId: panel.id };
});

test("TEST 9: Pattern matching for 'enabled.fixers.auto-format'", () => {
    const panel = getPanelForSetting("enabled.fixers.auto-format");
    if (!panel) throw new Error("getPanelForSetting returned null for enabled.fixers.auto-format");
    if (panel.id !== "fixers") throw new Error(`Expected panel id 'fixers', got '${panel.id}'`);
    return { panelId: panel.id };
});

console.log("\n=== Test Summary ===");
console.log(`Total: ${results.total}`);
console.log(`Passed: ${results.passed}`);
console.log(`Failed: ${results.failed}`);
console.log(`Success Rate: ${((results.passed / results.total) * 100).toFixed(1)}%`);
console.log("\n" + JSON.stringify(results, null, 2));

process.exit(results.success ? 0 : 1);
