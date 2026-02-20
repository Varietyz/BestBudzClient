/**
 * Preset Compliance Verification Tool
 * Validates preset JSON files for architectural compliance
 */

import { promises as fs } from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Bounds from settings-bounds.js (Phase 1)
const BOUNDS = {
    "limits.maxLinesPerFile": { min: 50, max: 500 },
    "limits.maxFilesPerFolder": { min: 3, max: 20 }
};

const PRESETS_DIR = path.resolve(__dirname, "../../../root/codebase-validation/presets");

const results = {
    verified: [],
    refuted: [],
    details: {}
};

async function verifyPresetCompliance() {
    try {
        // 1. Verify preset files exist and parse as valid JSON
        const files = ["enterprise.json", "startup.json", "library.json"];

        for (const file of files) {
            const filePath = path.join(PRESETS_DIR, file);
            const presetName = file.replace(".json", "");

            results.details[presetName] = {
                path: filePath,
                checks: {}
            };

            try {
                const content = await fs.readFile(filePath, "utf-8");

                // CHECK 1: Valid JSON parsing
                let preset;
                try {
                    preset = JSON.parse(content);
                    results.details[presetName].checks.validJSON = true;
                    results.verified.push(`${presetName}: Valid JSON`);
                } catch (parseError) {
                    results.details[presetName].checks.validJSON = false;
                    results.refuted.push(`${presetName}: JSON parsing failed - ${parseError.message}`);
                    continue; // Cannot check further if JSON is invalid
                }

                // CHECK 2: _preset_meta field exists with name and description
                if (preset._preset_meta && preset._preset_meta.name && preset._preset_meta.description) {
                    results.details[presetName].checks.hasMetadata = true;
                    results.verified.push(`${presetName}: Has _preset_meta with name="${preset._preset_meta.name}" and description`);
                } else {
                    results.details[presetName].checks.hasMetadata = false;
                    results.refuted.push(`${presetName}: Missing or incomplete _preset_meta field`);
                }

                // CHECK 3: Limits within bounds
                if (preset.limits) {
                    const limitsChecks = {};

                    // Check maxLinesPerFile
                    if (preset.limits.maxLinesPerFile !== undefined) {
                        const value = preset.limits.maxLinesPerFile;
                        const bounds = BOUNDS["limits.maxLinesPerFile"];

                        if (value >= bounds.min && value <= bounds.max) {
                            limitsChecks.maxLinesPerFile = true;
                            results.verified.push(`${presetName}: maxLinesPerFile=${value} is within bounds [${bounds.min}, ${bounds.max}]`);
                        } else {
                            limitsChecks.maxLinesPerFile = false;
                            results.refuted.push(`${presetName}: maxLinesPerFile=${value} is OUT OF BOUNDS [${bounds.min}, ${bounds.max}]`);
                        }
                    }

                    // Check maxFilesPerFolder
                    if (preset.limits.maxFilesPerFolder !== undefined) {
                        const value = preset.limits.maxFilesPerFolder;
                        const bounds = BOUNDS["limits.maxFilesPerFolder"];

                        if (value >= bounds.min && value <= bounds.max) {
                            limitsChecks.maxFilesPerFolder = true;
                            results.verified.push(`${presetName}: maxFilesPerFolder=${value} is within bounds [${bounds.min}, ${bounds.max}]`);
                        } else {
                            limitsChecks.maxFilesPerFolder = false;
                            results.refuted.push(`${presetName}: maxFilesPerFolder=${value} is OUT OF BOUNDS [${bounds.min}, ${bounds.max}]`);
                        }
                    }

                    results.details[presetName].checks.limitsWithinBounds = limitsChecks;
                } else {
                    results.refuted.push(`${presetName}: Missing limits field`);
                }

            } catch (readError) {
                results.details[presetName].checks.readable = false;
                results.refuted.push(`${presetName}: Cannot read file - ${readError.message}`);
            }
        }

        // 4. Verify merge logic in settings-core.js (code review)
        const settingsCoreFile = path.resolve(__dirname, "../../../root/codebase-validation/core/settings/settings-core.js");
        const settingsCoreContent = await fs.readFile(settingsCoreFile, "utf-8");

        // Check that deepMerge is defined
        if (settingsCoreContent.includes("function deepMerge(source, target)")) {
            results.verified.push("settings-core.js: deepMerge function is defined");

            // Check that target wins over source (user settings override preset)
            // Line 28 shows: result[key] = target[key]; - this means target (user) wins
            if (settingsCoreContent.includes("result[key] = target[key]")) {
                results.verified.push("settings-core.js: deepMerge gives priority to target (user settings override preset)");
            } else {
                results.refuted.push("settings-core.js: deepMerge priority unclear");
            }

            // Verify loadPreset uses correct merge order: deepMerge(presetSettings, currentSettings)
            // This means preset is source, current is target -> current wins
            if (settingsCoreContent.includes("deepMerge(presetSettings, currentSettings)")) {
                results.verified.push("settings-core.js: loadPreset correctly merges preset as base, user settings as override");
            } else {
                results.refuted.push("settings-core.js: loadPreset merge order incorrect or missing");
            }
        } else {
            results.refuted.push("settings-core.js: deepMerge function not found");
        }

        // 5. Verify listPresets() doesn't expose internal data
        if (settingsCoreContent.includes('map((f) => f.replace(".json", ""))')) {
            results.verified.push("settings-core.js: listPresets() only returns preset names (strips .json extension)");
        } else {
            results.refuted.push("settings-core.js: listPresets() implementation unclear");
        }

        // Summary
        console.log("\n=== PRESET COMPLIANCE VERIFICATION ===\n");
        console.log(`VERIFIED CLAIMS (${results.verified.length}):`);
        results.verified.forEach(v => console.log(`  ✓ ${v}`));

        console.log(`\nREFUTED CLAIMS (${results.refuted.length}):`);
        results.refuted.forEach(r => console.log(`  ✗ ${r}`));

        console.log("\n=== COMPLIANCE STATUS ===");
        if (results.refuted.length === 0) {
            console.log("✅ ALL CHECKS PASSED - Presets are fully compliant");
            process.exit(0);
        } else {
            console.log(`❌ ${results.refuted.length} COMPLIANCE VIOLATION(S) FOUND`);
            process.exit(1);
        }

    } catch (error) {
        console.error("AUDIT ABORTED:", error.message);
        process.exit(1);
    }
}

verifyPresetCompliance();
