#!/usr/bin/env node
/**
 * Verify UI Schema Integration
 * Tests that all UI schema functions are accessible from settings-core.js
 */

import { getPanelForSetting, getSettingsForPanel, getAllPanels } from "../../../root/codebase-validation/core/settings/settings-core.js";

const results = {
    success: true,
    tests: []
};

function test(name, fn) {
    try {
        fn();
        results.tests.push({ name, status: "PASS" });
    } catch (error) {
        results.success = false;
        results.tests.push({ name, status: "FAIL", error: error.message });
    }
}

// Test 1: getAllPanels returns array
test("getAllPanels returns array", () => {
    const panels = getAllPanels();
    if (!Array.isArray(panels)) throw new Error("getAllPanels did not return an array");
    if (panels.length !== 9) throw new Error(`Expected 9 panels, got ${panels.length}`);
});

// Test 2: getSettingsForPanel works
test("getSettingsForPanel returns patterns", () => {
    const settings = getSettingsForPanel("limits");
    if (!Array.isArray(settings)) throw new Error("getSettingsForPanel did not return an array");
    if (settings.length === 0) throw new Error("getSettingsForPanel returned empty array");
});

// Test 3: getPanelForSetting works
test("getPanelForSetting finds correct panel", () => {
    const panel = getPanelForSetting("limits.maxLinesPerFile");
    if (!panel) throw new Error("getPanelForSetting returned null");
    if (panel.id !== "limits") throw new Error(`Expected panel 'limits', got '${panel.id}'`);
});

// Test 4: getPanelForSetting handles wildcard patterns
test("getPanelForSetting handles wildcards", () => {
    const panel = getPanelForSetting("tools.eslint.enabled");
    if (!panel) throw new Error("getPanelForSetting returned null for tools.eslint.enabled");
    if (panel.id !== "tooling") throw new Error(`Expected panel 'tooling', got '${panel.id}'`);
});

// Test 5: All panels have required properties
test("All panels have required properties", () => {
    const panels = getAllPanels();
    for (const panel of panels) {
        if (!panel.id) throw new Error(`Panel missing id: ${JSON.stringify(panel)}`);
        if (!panel.label) throw new Error(`Panel ${panel.id} missing label`);
        if (!panel.icon) throw new Error(`Panel ${panel.id} missing icon`);
        if (!panel.description) throw new Error(`Panel ${panel.id} missing description`);
        if (!Array.isArray(panel.settings)) throw new Error(`Panel ${panel.id} settings is not an array`);
    }
});

// Print results
console.log(JSON.stringify(results, null, 2));
process.exit(results.success ? 0 : 1);
