/**
 * Settings Event System Test Suite
 * Tests for Phase 3: Event System Integration
 * @module test-settings-events
 */

import { fileURLToPath } from "url";
import path from "path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Navigate from .claude/workspace/tools to root/codebase-validation/core/settings
const settingsCorePath = path.resolve(__dirname, "../../../root/codebase-validation/core/settings/settings-core.js");
const settingsEventsPath = path.resolve(__dirname, "../../../root/codebase-validation/core/settings/settings-events.js");

const { onSettingsChange, offSettingsChange, SETTINGS_EVENTS } = await import(`file://${settingsCorePath.replace(/\\/g, "/")}`);
const { emitSettingsChange, clearAllListeners } = await import(`file://${settingsEventsPath.replace(/\\/g, "/")}`);

let passed = 0;
let failed = 0;
const results = [];

function test(name, fn) {
    try {
        clearAllListeners(); // Reset before each test
        const result = fn();
        if (result === true || result === undefined) {
            passed++;
            results.push({ name, status: "PASS", details: null });
            console.log(`✓ ${name}`);
        } else {
            failed++;
            results.push({ name, status: "FAIL", details: `Returned: ${result}` });
            console.log(`✗ ${name} - Returned: ${result}`);
        }
    } catch (error) {
        failed++;
        results.push({ name, status: "FAIL", details: error.message });
        console.log(`✗ ${name} - Error: ${error.message}`);
    }
}

console.log("\n=== Settings Event System Test Suite ===\n");

// TEST 1: onSettingsChange receives events
test("TEST 1: onSettingsChange receives events", () => {
    const received = [];
    onSettingsChange((type, data) => received.push({ type, data }));

    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { test: "value" });

    if (received.length !== 1) {
        return `Expected 1 event, received ${received.length}`;
    }
    if (received[0].type !== SETTINGS_EVENTS.CHANGED) {
        return `Expected type ${SETTINGS_EVENTS.CHANGED}, got ${received[0].type}`;
    }
    if (received[0].data.test !== "value") {
        return `Expected data.test = "value", got ${received[0].data.test}`;
    }
    return true;
});

// TEST 2: offSettingsChange removes listener
test("TEST 2: offSettingsChange removes listener", () => {
    const received = [];
    const callback = (type, data) => received.push({ type, data });

    onSettingsChange(callback);
    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { first: true });

    offSettingsChange(callback);
    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { second: true });

    if (received.length !== 1) {
        return `Expected 1 event after unsubscribe, received ${received.length}`;
    }
    if (!received[0].data.first) {
        return `Expected first event to have first:true`;
    }
    return true;
});

// TEST 3: Unsubscribe function returned from onSettingsChange works
test("TEST 3: Unsubscribe function returned from onSettingsChange works", () => {
    const received = [];
    const unsubscribe = onSettingsChange((type, data) => received.push({ type, data }));

    if (typeof unsubscribe !== "function") {
        return `Expected unsubscribe to be a function, got ${typeof unsubscribe}`;
    }

    emitSettingsChange(SETTINGS_EVENTS.LOADED, { before: true });
    unsubscribe();
    emitSettingsChange(SETTINGS_EVENTS.LOADED, { after: true });

    if (received.length !== 1) {
        return `Expected 1 event after unsubscribe, received ${received.length}`;
    }
    if (!received[0].data.before) {
        return `Expected first event to have before:true`;
    }
    return true;
});

// TEST 4: Event emitter handles listener errors gracefully
test("TEST 4: Event emitter handles listener errors gracefully", () => {
    const received = [];

    // First listener throws error
    onSettingsChange(() => { throw new Error("Test error from listener"); });

    // Second listener should still run
    onSettingsChange((type, data) => received.push({ type, data }));

    // Should not throw, second listener should still fire
    emitSettingsChange(SETTINGS_EVENTS.PRESET_APPLIED, { preset: "test" });

    if (received.length !== 1) {
        return `Expected second listener to receive event despite first throwing, received ${received.length} events`;
    }
    if (received[0].data.preset !== "test") {
        return `Expected preset data, got ${received[0].data.preset}`;
    }
    return true;
});

// TEST 5: clearAllListeners works
test("TEST 5: clearAllListeners works", () => {
    const received1 = [];
    const received2 = [];

    onSettingsChange((type, data) => received1.push({ type, data }));
    onSettingsChange((type, data) => received2.push({ type, data }));

    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { before: true });

    if (received1.length !== 1 || received2.length !== 1) {
        return `Expected both listeners to fire before clear: listener1=${received1.length}, listener2=${received2.length}`;
    }

    clearAllListeners();
    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { after: true });

    if (received1.length !== 1 || received2.length !== 1) {
        return `Expected no events after clearAllListeners: listener1=${received1.length}, listener2=${received2.length}`;
    }
    return true;
});

// TEST 6: SETTINGS_EVENTS constants are correct
test("TEST 6: SETTINGS_EVENTS constants are correct", () => {
    if (SETTINGS_EVENTS.CHANGED !== "settings:changed") {
        return `Expected SETTINGS_EVENTS.CHANGED = "settings:changed", got "${SETTINGS_EVENTS.CHANGED}"`;
    }
    if (SETTINGS_EVENTS.LOADED !== "settings:loaded") {
        return `Expected SETTINGS_EVENTS.LOADED = "settings:loaded", got "${SETTINGS_EVENTS.LOADED}"`;
    }
    if (SETTINGS_EVENTS.PRESET_APPLIED !== "settings:preset:applied") {
        return `Expected SETTINGS_EVENTS.PRESET_APPLIED = "settings:preset:applied", got "${SETTINGS_EVENTS.PRESET_APPLIED}"`;
    }
    return true;
});

// TEST 7: Multiple listeners receive same event
test("TEST 7: Multiple listeners receive same event", () => {
    const received1 = [];
    const received2 = [];
    const received3 = [];

    onSettingsChange((type, data) => received1.push({ type, data }));
    onSettingsChange((type, data) => received2.push({ type, data }));
    onSettingsChange((type, data) => received3.push({ type, data }));

    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { value: 42 });

    if (received1.length !== 1 || received2.length !== 1 || received3.length !== 1) {
        return `Expected all 3 listeners to fire: l1=${received1.length}, l2=${received2.length}, l3=${received3.length}`;
    }
    if (received1[0].data.value !== 42 || received2[0].data.value !== 42 || received3[0].data.value !== 42) {
        return `Expected all listeners to receive value 42`;
    }
    return true;
});

// TEST 8: SETTINGS_EVENTS object is frozen (immutable)
test("TEST 8: SETTINGS_EVENTS object is frozen (immutable)", () => {
    if (!Object.isFrozen(SETTINGS_EVENTS)) {
        return `Expected SETTINGS_EVENTS to be frozen`;
    }

    // Attempt to modify should fail silently in strict mode
    const originalValue = SETTINGS_EVENTS.CHANGED;
    try {
        SETTINGS_EVENTS.CHANGED = "modified";
    } catch (e) {
        // Expected in strict mode
    }

    if (SETTINGS_EVENTS.CHANGED !== originalValue) {
        return `Expected SETTINGS_EVENTS.CHANGED to remain unchanged`;
    }
    return true;
});

// TEST 9: emitSettingsChange with no listeners does not throw
test("TEST 9: emitSettingsChange with no listeners does not throw", () => {
    clearAllListeners(); // Ensure no listeners

    // Should not throw
    emitSettingsChange(SETTINGS_EVENTS.CHANGED, { test: true });
    emitSettingsChange(SETTINGS_EVENTS.LOADED, {});
    emitSettingsChange(SETTINGS_EVENTS.PRESET_APPLIED, { preset: "test" });

    return true;
});

// Summary
console.log("\n=== Test Summary ===");
console.log(`Passed: ${passed}/${passed + failed}`);
console.log(`Failed: ${failed}/${passed + failed}`);
console.log(`Success Rate: ${((passed / (passed + failed)) * 100).toFixed(1)}%`);

// Output structured results for validation report
console.log("\n=== Structured Results ===");
console.log(JSON.stringify({ passed, failed, total: passed + failed, results }, null, 2));

// Cleanup
clearAllListeners();

// Exit code
process.exit(failed > 0 ? 1 : 0);
