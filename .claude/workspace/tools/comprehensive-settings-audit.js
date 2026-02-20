/**
 * Comprehensive Settings Audit
 *
 * Finds ALL settings that appear in multiple places with same or different values.
 * Groups by semantic purpose, not just pattern matching.
 */

import { promises as fs } from "fs";
import path from "path";

const ROOT = process.cwd();

// Semantic categories with all known variations
const SEMANTIC_GROUPS = {
    // Formatting - Indentation
    "format.indent.size": [
        "indentWidth", "indent_width", "indentSize", "indent_size",
        "tabWidth", "tab_width", "tabSpaces", "tab_spaces",
        "IndentWidth", "TabWidth", "indentation.size"
    ],
    "format.indent.style": [
        "useTabs", "use_tabs", "hard_tabs", "UseTab",
        "indentStyle", "indent_style", "indentation.useTabs"
    ],

    // Formatting - Line Length
    "format.lineLength": [
        "lineLength", "lineWidth", "line_length", "line_width",
        "printWidth", "print_width", "maxWidth", "max_width",
        "columnLimit", "column_limit", "ColumnLimit",
        "maxLineLength", "max_line_length"
    ],

    // Formatting - Quotes
    "format.quotes": [
        "singleQuote", "single_quote", "quotes", "quoteStyle", "quote_style"
    ],
    "format.quotes.jsx": [
        "jsxSingleQuote", "jsx_single_quote", "jsxQuotes"
    ],
    "format.quotes.props": [
        "quoteProps", "quote_props"
    ],

    // Formatting - Semicolons
    "format.semicolons": [
        "semi", "semicolons", "useSemicolons"
    ],

    // Formatting - Trailing Comma
    "format.trailingComma": [
        "trailingComma", "trailing_comma", "trailingCommas"
    ],

    // Formatting - Line Endings
    "format.endOfLine": [
        "endOfLine", "end_of_line", "lineEnding", "line_ending", "eol"
    ],

    // Formatting - Brackets
    "format.bracketSpacing": [
        "bracketSpacing", "bracket_spacing"
    ],
    "format.bracketSameLine": [
        "bracketSameLine", "jsxBracketSameLine"
    ],

    // Formatting - Arrow Functions
    "format.arrowParens": [
        "arrowParens", "arrow_parens"
    ],

    // Formatting - Prose
    "format.proseWrap": [
        "proseWrap", "prose_wrap"
    ],

    // Language Versions
    "version.ecma": [
        "ecmaVersion", "ecma_version", "targetES", "target"
    ],
    "version.python": [
        "pythonVersion", "python_version", "targetVersion", "target_version", "target-version"
    ],
    "version.typescript": [
        "target", "module", "moduleResolution"
    ],

    // Rules - Strictness
    "rules.strict": [
        "strict", "strictMode", "typeCheckingMode"
    ],

    // File Limits
    "limits.maxLines": [
        "maxLinesPerFile", "max_lines", "maxLines"
    ],
    "limits.maxFiles": [
        "maxFilesPerFolder", "max_files"
    ],

    // Enabled States
    "enabled": [
        "enabled", "validate"
    ],
    "autoFix": [
        "autoFix", "auto_fix", "fix", "fixable"
    ]
};

class ComprehensiveAudit {
    constructor() {
        this.allSettings = [];
        this.groupedFindings = new Map();
    }

    async loadSettings() {
        const settingsPath = path.join(ROOT, "root/codebase-validation/settings.json");
        const content = await fs.readFile(settingsPath, "utf-8");
        return JSON.parse(content);
    }

    extractAll(obj, filePath, currentPath = "") {
        const settings = [];

        for (const [key, value] of Object.entries(obj)) {
            const settingPath = currentPath ? `${currentPath}.${key}` : key;

            if (value !== null && typeof value === "object" && !Array.isArray(value)) {
                settings.push(...this.extractAll(value, filePath, settingPath));
            } else {
                settings.push({
                    file: filePath,
                    path: settingPath,
                    key,
                    value,
                    valueType: typeof value
                });
            }
        }

        return settings;
    }

    categorize() {
        for (const setting of this.allSettings) {
            for (const [groupName, patterns] of Object.entries(SEMANTIC_GROUPS)) {
                for (const pattern of patterns) {
                    if (setting.key === pattern ||
                        setting.key.toLowerCase() === pattern.toLowerCase() ||
                        setting.path.endsWith(`.${pattern}`)) {

                        if (!this.groupedFindings.has(groupName)) {
                            this.groupedFindings.set(groupName, []);
                        }
                        this.groupedFindings.get(groupName).push(setting);
                        break;
                    }
                }
            }
        }
    }

    async run() {
        console.log("=".repeat(100));
        console.log("COMPREHENSIVE SETTINGS AUDIT - ALL DUPLICATE/CONFLICTING SETTINGS");
        console.log("=".repeat(100));
        console.log("");

        const settings = await this.loadSettings();
        this.allSettings = this.extractAll(settings, "settings.json");

        console.log(`Total settings extracted: ${this.allSettings.length}\n`);

        this.categorize();

        // Report all groups with 2+ entries
        let totalDuplicates = 0;
        let totalConflicts = 0;

        const sortedGroups = [...this.groupedFindings.entries()].sort((a, b) => {
            // Sort by conflict status, then by count
            const aValues = new Set(a[1].map(s => JSON.stringify(s.value)));
            const bValues = new Set(b[1].map(s => JSON.stringify(s.value)));
            const aConflict = aValues.size > 1 ? 1 : 0;
            const bConflict = bValues.size > 1 ? 1 : 0;
            if (aConflict !== bConflict) return bConflict - aConflict;
            return b[1].length - a[1].length;
        });

        for (const [groupName, entries] of sortedGroups) {
            if (entries.length < 2) continue;

            totalDuplicates++;

            const byValue = new Map();
            for (const e of entries) {
                const valKey = JSON.stringify(e.value);
                if (!byValue.has(valKey)) {
                    byValue.set(valKey, []);
                }
                byValue.get(valKey).push(e);
            }

            const hasConflict = byValue.size > 1;
            if (hasConflict) totalConflicts++;

            const status = hasConflict ? "⚠️  CONFLICT" : "📋 DUPLICATE";

            console.log("\n" + "─".repeat(100));
            console.log(`${status}: ${groupName}`);
            console.log(`Found ${entries.length} occurrences with ${byValue.size} unique value(s)`);
            console.log("─".repeat(100));

            for (const [valKey, locations] of byValue.entries()) {
                const value = JSON.parse(valKey);
                console.log(`\n  Value: ${JSON.stringify(value, null, 2).replace(/\n/g, "\n         ")}`);
                for (const loc of locations) {
                    console.log(`    → ${loc.path}`);
                }
            }
        }

        // Print uncategorized settings that might be duplicates
        console.log("\n\n" + "=".repeat(100));
        console.log("POTENTIAL UNCATEGORIZED DUPLICATES");
        console.log("=".repeat(100));

        // Group by key name
        const byKey = new Map();
        for (const s of this.allSettings) {
            const key = s.key.toLowerCase();
            if (!byKey.has(key)) {
                byKey.set(key, []);
            }
            byKey.get(key).push(s);
        }

        let uncategorizedDupes = 0;
        for (const [key, entries] of byKey.entries()) {
            if (entries.length >= 2) {
                // Skip if already categorized
                let alreadyCategorized = false;
                for (const patterns of Object.values(SEMANTIC_GROUPS)) {
                    if (patterns.some(p => p.toLowerCase() === key)) {
                        alreadyCategorized = true;
                        break;
                    }
                }
                if (alreadyCategorized) continue;

                uncategorizedDupes++;
                console.log(`\n  "${key}" appears ${entries.length} times:`);
                for (const e of entries) {
                    console.log(`    → ${e.path} = ${JSON.stringify(e.value)}`);
                }
            }
        }

        if (uncategorizedDupes === 0) {
            console.log("\n  None found");
        }

        // Summary
        console.log("\n\n" + "=".repeat(100));
        console.log("SUMMARY");
        console.log("=".repeat(100));
        console.log(`
  Total settings scanned: ${this.allSettings.length}
  Semantic groups with duplicates: ${totalDuplicates}
  Groups with CONFLICTING values: ${totalConflicts}
  Uncategorized duplicates: ${uncategorizedDupes}
`);

        console.log("RECOMMENDED ACTION:");
        console.log("  1. Add all duplicated values to tools.codeStyle");
        console.log("  2. Update tools.toolMappings for each tool");
        console.log("  3. Remove hardcoded values from tool-specific configs");
        console.log("  4. Create accessor functions that resolve mappings at runtime");
    }
}

const audit = new ComprehensiveAudit();
audit.run().catch(console.error);
