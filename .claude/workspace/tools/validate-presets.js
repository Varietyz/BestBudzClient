#!/usr/bin/env node

import { promises as fs } from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const PRESETS_DIR = path.resolve(__dirname, "../../../root/codebase-validation/presets");

async function validatePresets() {
    console.log("Validating preset files...\n");

    let allValid = true;

    try {
        const files = await fs.readdir(PRESETS_DIR);
        const jsonFiles = files.filter((f) => f.endsWith(".json"));

        if (jsonFiles.length === 0) {
            console.error("ERROR: No preset files found");
            process.exit(1);
        }

        for (const file of jsonFiles) {
            const filePath = path.join(PRESETS_DIR, file);
            console.log(`Checking ${file}...`);

            try {
                const content = await fs.readFile(filePath, "utf-8");
                const preset = JSON.parse(content);

                // Check for _preset_meta
                if (!preset._preset_meta) {
                    console.error(`  ERROR: Missing _preset_meta`);
                    allValid = false;
                } else {
                    console.log(`  Meta: ${preset._preset_meta.name} - ${preset._preset_meta.description}`);

                    // Check required meta fields
                    if (!preset._preset_meta.name) {
                        console.error(`  ERROR: Missing _preset_meta.name`);
                        allValid = false;
                    }
                    if (!preset._preset_meta.description) {
                        console.error(`  ERROR: Missing _preset_meta.description`);
                        allValid = false;
                    }
                    if (!preset._preset_meta.icon) {
                        console.error(`  ERROR: Missing _preset_meta.icon`);
                        allValid = false;
                    }
                }

                // Check for settings sections
                const hasSettings =
                    preset.css || preset.rules || preset.enabled || preset.naming || preset.discovery;
                if (!hasSettings) {
                    console.error(`  WARNING: No settings sections found`);
                }

                console.log(`  OK\n`);
            } catch (error) {
                console.error(`  ERROR: Invalid JSON - ${error.message}`);
                allValid = false;
            }
        }

        if (allValid) {
            console.log(`\nAll ${jsonFiles.length} preset files are valid`);
            process.exit(0);
        } else {
            console.error("\nSome preset files have errors");
            process.exit(1);
        }
    } catch (error) {
        console.error(`ERROR: ${error.message}`);
        process.exit(1);
    }
}

validatePresets();
