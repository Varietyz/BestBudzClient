/**
 * Debug Settings Extraction
 * Find ALL indent/lineLength/etc settings in codebase-validation/settings.json
 */

import { promises as fs } from "fs";

const PATTERNS = [
    /indent/i, /tabWidth/i, /tab_width/i, /tab_spaces/i,
    /indentWidth/i, /indent_width/i, /indentSize/i, /indent_size/i,
    /IndentWidth/i, /lineLength/i, /lineWidth/i, /line_length/i,
    /line_width/i, /maxLine/i, /max_line/i, /printWidth/i, /print_width/i,
    /columnLimit/i, /column_limit/i, /maxWidth/i, /max_width/i, /ColumnLimit/i
];

function findKeys(obj, path = "", results = []) {
    for (const [key, value] of Object.entries(obj)) {
        const currentPath = path ? `${path}.${key}` : key;

        // Check if key matches any pattern
        for (const pattern of PATTERNS) {
            if (pattern.test(key)) {
                results.push({ path: currentPath, key, value, pattern: pattern.toString() });
                break;
            }
        }

        // Recurse into objects
        if (value && typeof value === "object" && !Array.isArray(value)) {
            findKeys(value, currentPath, results);
        }
    }
    return results;
}

async function main() {
    const content = await fs.readFile("root/codebase-validation/settings.json", "utf-8");
    const json = JSON.parse(content);

    const found = findKeys(json);

    console.log("=".repeat(80));
    console.log("SETTINGS.JSON - ALL INDENT/LINE-LENGTH RELATED SETTINGS");
    console.log("=".repeat(80));
    console.log(`\nFound ${found.length} relevant settings:\n`);

    // Group by actual numeric value vs reference
    const hardcoded = [];
    const references = [];

    for (const f of found) {
        if (typeof f.value === "number" || (typeof f.value === "string" && !f.value.includes("."))) {
            hardcoded.push(f);
        } else {
            references.push(f);
        }
    }

    console.log("─".repeat(80));
    console.log("HARDCODED VALUES (potential violations):");
    console.log("─".repeat(80));
    for (const f of hardcoded) {
        console.log(`\n  Path: ${f.path}`);
        console.log(`  Value: ${JSON.stringify(f.value)}`);
    }

    console.log("\n" + "─".repeat(80));
    console.log("REFERENCES (correct pattern - references codeStyle):");
    console.log("─".repeat(80));
    for (const f of references) {
        console.log(`\n  Path: ${f.path}`);
        console.log(`  Value: ${JSON.stringify(f.value)}`);
    }

    console.log("\n" + "=".repeat(80));
    console.log(`SUMMARY: ${hardcoded.length} hardcoded, ${references.length} references`);
    console.log("=".repeat(80));
}

main().catch(console.error);
