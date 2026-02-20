#!/usr/bin/env node

/**
 * Comprehensive Preset API Test Suite
 * Tests: listPresets, getPresetInfo, loadPreset, error handling, user override preservation
 */

import { promises as fs } from "fs";
import path from "path";
import { fileURLToPath } from "url";
import {
    listPresets,
    getPresetInfo,
    loadPreset,
    getSettings,
    reloadSettings
} from "../../../root/codebase-validation/core/settings/settings-core.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const SETTINGS_PATH = path.resolve(__dirname, "../../../root/codebase-validation/settings.json");

let testsPassed = 0;
let testsFailed = 0;
const testResults = [];

function logTest(name, passed, details) {
    testResults.push({ name, passed, details });
    if (passed) {
        testsPassed++;
        console.log(`PASS: ${name}`);
    } else {
        testsFailed++;
        console.log(`FAIL: ${name}`);
    }
    if (details) {
        console.log(`  Details: ${details}`);
    }
    console.log("");
}

async function runTests() {
    console.log("=" .repeat(60));
    console.log("COMPREHENSIVE PRESET API TEST SUITE");
    console.log("=" .repeat(60));
    console.log("");

    // Backup current settings
    let originalSettings;
    try {
        originalSettings = await fs.readFile(SETTINGS_PATH, "utf-8");
    } catch (error) {
        console.log("WARNING: Could not backup settings.json");
        originalSettings = null;
    }

    try {
        // TEST 1: listPresets() returns all preset names
        console.log("TEST 1: listPresets() returns all preset names");
        const presets = await listPresets();
        const expectedPresets = ["enterprise", "library", "startup"];
        const presetsMatch = expectedPresets.every(p => presets.includes(p)) && presets.length === 3;
        logTest(
            "listPresets() returns 3 presets",
            presetsMatch,
            `Expected: ${JSON.stringify(expectedPresets.sort())}, Got: ${JSON.stringify(presets.sort())}`
        );

        // TEST 2: getPresetInfo("enterprise") returns metadata
        console.log("TEST 2: getPresetInfo('enterprise') returns metadata");
        const enterpriseInfo = await getPresetInfo("enterprise");
        const enterpriseValid =
            enterpriseInfo.name === "Enterprise" &&
            enterpriseInfo.description === "Strict governance, full validation, 150 line max" &&
            enterpriseInfo.icon === "building";
        logTest(
            "getPresetInfo('enterprise') returns correct metadata",
            enterpriseValid,
            `Got: ${JSON.stringify(enterpriseInfo)}`
        );

        // TEST 3: getPresetInfo("startup") returns metadata
        console.log("TEST 3: getPresetInfo('startup') returns metadata");
        const startupInfo = await getPresetInfo("startup");
        const startupValid =
            startupInfo.name === "Startup" &&
            startupInfo.description === "Fast iteration, relaxed limits, 300 line max" &&
            startupInfo.icon === "rocket";
        logTest(
            "getPresetInfo('startup') returns correct metadata",
            startupValid,
            `Got: ${JSON.stringify(startupInfo)}`
        );

        // TEST 4: getPresetInfo("library") returns metadata
        console.log("TEST 4: getPresetInfo('library') returns metadata");
        const libraryInfo = await getPresetInfo("library");
        const libraryValid =
            libraryInfo.name === "Library" &&
            libraryInfo.description === "API-focused, doc-heavy, 200 line max" &&
            libraryInfo.icon === "book";
        logTest(
            "getPresetInfo('library') returns correct metadata",
            libraryValid,
            `Got: ${JSON.stringify(libraryInfo)}`
        );

        // TEST 5: Error handling for nonexistent preset
        console.log("TEST 5: Error handling for nonexistent preset");
        let errorThrown = false;
        let errorMessage = "";
        try {
            await getPresetInfo("nonexistent");
        } catch (error) {
            errorThrown = true;
            errorMessage = error.message;
        }
        logTest(
            "getPresetInfo('nonexistent') throws error",
            errorThrown && errorMessage.includes("not found"),
            `Error thrown: ${errorThrown}, Message: ${errorMessage}`
        );

        // TEST 6: loadPreset preserves current settings (defaults win over preset)
        // NOTE: DEFAULTS have limits.maxLinesPerFile = 150, so preset's 300 won't override
        // This is correct behavior: preset provides base values, current settings (including defaults) win
        console.log("TEST 6: loadPreset('startup') preserves current settings over preset");

        // First, restore original settings and reload
        if (originalSettings) {
            await fs.writeFile(SETTINGS_PATH, originalSettings, "utf-8");
        }
        await reloadSettings();

        // Get current settings (which include DEFAULTS with limits.maxLinesPerFile = 150)
        const beforeLoad = await getSettings();
        const beforeMaxLines = beforeLoad?.limits?.maxLinesPerFile;

        // Load startup preset (which has limits.maxLinesPerFile = 300)
        const mergedSettings = await loadPreset("startup");

        // Current settings (including defaults) should be preserved over preset
        // preset provides base, current settings win
        const limitsValid = mergedSettings?.limits?.maxLinesPerFile === beforeMaxLines;
        logTest(
            "loadPreset('startup') preserves current limits.maxLinesPerFile over preset value",
            limitsValid,
            `Before: ${beforeMaxLines}, After: ${mergedSettings?.limits?.maxLinesPerFile}, Preset value was: 300`
        );

        // TEST 7: loadPreset does not overwrite explicit user settings
        console.log("TEST 7: loadPreset does not overwrite explicit user settings");

        // First, restore original settings and reload to clear the limits we just added
        if (originalSettings) {
            await fs.writeFile(SETTINGS_PATH, originalSettings, "utf-8");
        }
        await reloadSettings();

        // Get current settings to check an explicit value
        const currentSettings = await getSettings();
        const originalLineLength = currentSettings?.rules?.formatting?.lineLength;

        // Load enterprise preset (which also has rules.formatting.lineLength = 120)
        const afterEnterpriseLoad = await loadPreset("enterprise");

        // The user's explicit setting should be preserved (current settings win over preset)
        // Since deepMerge(presetSettings, currentSettings) uses current settings as the override
        const lineLengthPreserved = afterEnterpriseLoad?.rules?.formatting?.lineLength === originalLineLength;
        logTest(
            "loadPreset preserves user's explicit settings (rules.formatting.lineLength)",
            lineLengthPreserved,
            `Original: ${originalLineLength}, After loadPreset: ${afterEnterpriseLoad?.rules?.formatting?.lineLength}`
        );

        // TEST 8: loadPreset removes _preset_meta from merged result
        console.log("TEST 8: loadPreset removes _preset_meta from merged result");
        const noPresetMeta = !("_preset_meta" in afterEnterpriseLoad);
        logTest(
            "loadPreset removes _preset_meta from result",
            noPresetMeta,
            `_preset_meta in result: ${!noPresetMeta}`
        );

        // TEST 9: loadPreset returns object with expected structure
        console.log("TEST 9: loadPreset returns object with expected structure");
        const hasExpectedStructure =
            afterEnterpriseLoad.hasOwnProperty("css") &&
            afterEnterpriseLoad.hasOwnProperty("rules") &&
            afterEnterpriseLoad.hasOwnProperty("enabled");
        logTest(
            "loadPreset returns object with css, rules, enabled sections",
            hasExpectedStructure,
            `Has css: ${afterEnterpriseLoad.hasOwnProperty("css")}, rules: ${afterEnterpriseLoad.hasOwnProperty("rules")}, enabled: ${afterEnterpriseLoad.hasOwnProperty("enabled")}`
        );

    } finally {
        // Restore original settings
        if (originalSettings) {
            await fs.writeFile(SETTINGS_PATH, originalSettings, "utf-8");
            await reloadSettings();
            console.log("Settings restored to original state.");
        }
    }

    // Summary
    console.log("=" .repeat(60));
    console.log("TEST SUMMARY");
    console.log("=" .repeat(60));
    console.log(`PASSED: ${testsPassed}/9`);
    console.log(`FAILED: ${testsFailed}/9`);
    console.log(`SUCCESS RATE: ${((testsPassed / 9) * 100).toFixed(1)}%`);
    console.log("=" .repeat(60));

    if (testsFailed === 0) {
        console.log("ALL TESTS PASSED!");
        process.exit(0);
    } else {
        console.log("SOME TESTS FAILED");
        process.exit(1);
    }
}

runTests().catch(error => {
    console.error("Test execution error:", error);
    process.exit(1);
});
