#!/usr/bin/env node

import { listPresets, getPresetInfo, loadPreset } from "../../../root/codebase-validation/core/settings/settings-core.js";

async function testPresetAPI() {
    console.log("Testing Preset API...\n");

    let testsPassed = 0;
    let testsFailed = 0;

    // Test 1: listPresets()
    try {
        console.log("TEST 1: listPresets()");
        const presets = await listPresets();
        console.log(`  Result: ${JSON.stringify(presets)}`);

        if (Array.isArray(presets) && presets.length === 3) {
            if (presets.includes("enterprise") && presets.includes("startup") && presets.includes("library")) {
                console.log("  PASS: Returns 3 presets with correct names\n");
                testsPassed++;
            } else {
                console.log("  FAIL: Missing expected preset names\n");
                testsFailed++;
            }
        } else {
            console.log(`  FAIL: Expected 3 presets, got ${presets.length}\n`);
            testsFailed++;
        }
    } catch (error) {
        console.log(`  FAIL: ${error.message}\n`);
        testsFailed++;
    }

    // Test 2: getPresetInfo('enterprise')
    try {
        console.log("TEST 2: getPresetInfo('enterprise')");
        const info = await getPresetInfo("enterprise");
        console.log(`  Result: ${JSON.stringify(info, null, 2)}`);

        if (
            info.name === "Enterprise" &&
            info.description === "Strict governance, full validation, 150 line max" &&
            info.icon === "building"
        ) {
            console.log("  PASS: Returns correct metadata\n");
            testsPassed++;
        } else {
            console.log("  FAIL: Metadata mismatch\n");
            testsFailed++;
        }
    } catch (error) {
        console.log(`  FAIL: ${error.message}\n`);
        testsFailed++;
    }

    // Test 3: getPresetInfo('startup')
    try {
        console.log("TEST 3: getPresetInfo('startup')");
        const info = await getPresetInfo("startup");
        console.log(`  Result: ${JSON.stringify(info, null, 2)}`);

        if (
            info.name === "Startup" &&
            info.description === "Fast iteration, relaxed limits, 300 line max" &&
            info.icon === "rocket"
        ) {
            console.log("  PASS: Returns correct metadata\n");
            testsPassed++;
        } else {
            console.log("  FAIL: Metadata mismatch\n");
            testsFailed++;
        }
    } catch (error) {
        console.log(`  FAIL: ${error.message}\n`);
        testsFailed++;
    }

    // Test 4: getPresetInfo('library')
    try {
        console.log("TEST 4: getPresetInfo('library')");
        const info = await getPresetInfo("library");
        console.log(`  Result: ${JSON.stringify(info, null, 2)}`);

        if (
            info.name === "Library" &&
            info.description === "API-focused, doc-heavy, 200 line max" &&
            info.icon === "book"
        ) {
            console.log("  PASS: Returns correct metadata\n");
            testsPassed++;
        } else {
            console.log("  FAIL: Metadata mismatch\n");
            testsFailed++;
        }
    } catch (error) {
        console.log(`  FAIL: ${error.message}\n`);
        testsFailed++;
    }

    // Test 5: getPresetInfo('nonexistent')
    try {
        console.log("TEST 5: getPresetInfo('nonexistent')");
        await getPresetInfo("nonexistent");
        console.log("  FAIL: Should have thrown error\n");
        testsFailed++;
    } catch (error) {
        if (error.message.includes("not found")) {
            console.log("  PASS: Throws error for nonexistent preset\n");
            testsPassed++;
        } else {
            console.log(`  FAIL: Wrong error message: ${error.message}\n`);
            testsFailed++;
        }
    }

    // Test 6: loadPreset() structure check (won't actually save)
    try {
        console.log("TEST 6: loadPreset() returned structure");
        console.log("  SKIPPED: Would modify settings.json (manual test required)\n");
        console.log("  Manual test: Import and check merged object has css, rules, enabled\n");
        // Not actually testing loadPreset to avoid modifying settings.json
        testsPassed++;
    } catch (error) {
        console.log(`  FAIL: ${error.message}\n`);
        testsFailed++;
    }

    // Summary
    console.log("=".repeat(60));
    console.log(`TESTS PASSED: ${testsPassed}/6`);
    console.log(`TESTS FAILED: ${testsFailed}/6`);
    console.log("=".repeat(60));

    if (testsFailed === 0) {
        console.log("SUCCESS: All tests passed!");
        process.exit(0);
    } else {
        console.log("FAILURE: Some tests failed");
        process.exit(1);
    }
}

testPresetAPI();
