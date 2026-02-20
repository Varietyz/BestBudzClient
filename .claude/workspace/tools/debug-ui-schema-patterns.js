#!/usr/bin/env node
/**
 * Debug UI Schema Pattern Matching
 */

import { getPanelForSetting, getAllPanels } from "../../../root/codebase-validation/core/settings/settings-core.js";

const testPaths = [
    "limits.maxLinesPerFile",
    "tools.eslint.enabled",
    "naming.conventions.files",
    "css.variables.colors",
    "security.csp.enabled"
];

console.log("Testing pattern matching:\n");

for (const testPath of testPaths) {
    const panel = getPanelForSetting(testPath);
    console.log(`Path: ${testPath}`);
    console.log(`Panel: ${panel ? panel.id : 'NULL'}\n`);
}

console.log("All panels:");
const panels = getAllPanels();
for (const panel of panels) {
    console.log(`${panel.id}: ${panel.settings.join(', ')}`);
}
