/**
 * Semantic Settings Audit
 *
 * Finds settings with SAME PURPOSE but DIFFERENT NAMES across all tools/languages.
 * Groups by semantic intent, not by name matching.
 */

import { promises as fs } from "fs";
import path from "path";

const ROOT = process.cwd();

// SEMANTIC PURPOSES - what the setting DOES, not what it's CALLED
// Each purpose has all the different names used by different tools/languages
const SEMANTIC_PURPOSES = {
    // === FORMATTING: INDENTATION ===
    "INDENT_SIZE": {
        purpose: "How many spaces/tabs to use for indentation",
        variations: [
            "indentWidth", "indent_width", "indentSize", "indent_size",
            "tabWidth", "tab_width", "tabSpaces", "tab_spaces",
            "IndentWidth", "TabWidth", "tab_size", "size",
            "indentation", "shiftwidth", "sw"
        ]
    },
    "INDENT_STYLE": {
        purpose: "Whether to use tabs or spaces",
        variations: [
            "useTabs", "use_tabs", "hard_tabs", "UseTab",
            "indentStyle", "indent_style", "tabs", "expandtab"
        ]
    },

    // === FORMATTING: LINE LENGTH ===
    "LINE_LENGTH": {
        purpose: "Maximum characters per line",
        variations: [
            "lineLength", "line_length", "lineWidth", "line_width",
            "printWidth", "print_width", "maxWidth", "max_width",
            "columnLimit", "column_limit", "ColumnLimit",
            "maxLineLength", "max_line_length", "textwidth",
            "wrap_line_length", "right_margin"
        ]
    },

    // === FORMATTING: QUOTES ===
    "QUOTE_STYLE": {
        purpose: "Single vs double quotes for strings",
        variations: [
            "singleQuote", "single_quote", "quotes", "quoteStyle",
            "quote_style", "string_quotes", "prefer_single_quoted_strings",
            "force_single_line"
        ]
    },
    "JSX_QUOTE_STYLE": {
        purpose: "Quotes for JSX attributes",
        variations: [
            "jsxSingleQuote", "jsx_single_quote", "jsxQuotes"
        ]
    },

    // === FORMATTING: SEMICOLONS ===
    "SEMICOLONS": {
        purpose: "Whether to use semicolons at end of statements",
        variations: [
            "semi", "semicolons", "useSemicolons", "trailing_semicolon"
        ]
    },

    // === FORMATTING: TRAILING COMMAS ===
    "TRAILING_COMMA": {
        purpose: "When to add trailing commas in multi-line structures",
        variations: [
            "trailingComma", "trailing_comma", "trailingCommas",
            "magic_trailing_comma", "skip_magic_trailing_comma"
        ]
    },

    // === FORMATTING: LINE ENDINGS ===
    "END_OF_LINE": {
        purpose: "Line ending style (LF, CRLF, CR)",
        variations: [
            "endOfLine", "end_of_line", "lineEnding", "line_ending",
            "eol", "newline", "line_separator"
        ]
    },

    // === FORMATTING: BRACKET SPACING ===
    "BRACKET_SPACING": {
        purpose: "Spaces inside object literals { }",
        variations: [
            "bracketSpacing", "bracket_spacing", "space_in_braces",
            "spaces_around_brackets"
        ]
    },

    // === FORMATTING: ARROW FUNCTIONS ===
    "ARROW_PARENS": {
        purpose: "Parentheses around single arrow function param",
        variations: [
            "arrowParens", "arrow_parens", "arrow_body_style"
        ]
    },

    // === LANGUAGE VERSIONS ===
    "ECMASCRIPT_VERSION": {
        purpose: "ECMAScript/JavaScript version target",
        variations: [
            "ecmaVersion", "ecma_version", "target", "es_version",
            "javascript_target", "node_version"
        ]
    },
    "PYTHON_VERSION": {
        purpose: "Python interpreter version",
        variations: [
            "pythonVersion", "python_version", "targetVersion",
            "target_version", "target-version", "py_version",
            "python", "requires-python"
        ]
    },
    "TYPESCRIPT_TARGET": {
        purpose: "TypeScript compilation target",
        variations: [
            "target", "module", "moduleResolution", "lib"
        ]
    },
    "JAVA_VERSION": {
        purpose: "Java language level",
        variations: [
            "sourceVersion", "targetVersion", "release", "java_version",
            "languageLevel", "jdk_version"
        ]
    },
    "RUST_EDITION": {
        purpose: "Rust edition year",
        variations: [
            "edition", "rust_edition", "toolchain"
        ]
    },
    "CPP_STANDARD": {
        purpose: "C++ language standard",
        variations: [
            "std", "standard", "cpp_standard", "language_version",
            "cxxstandard", "CMAKE_CXX_STANDARD"
        ]
    },

    // === STRICTNESS ===
    "TYPE_STRICTNESS": {
        purpose: "How strictly to check types",
        variations: [
            "strict", "strictMode", "typeCheckingMode", "strict_mode",
            "strict_null_checks", "no_implicit_any", "strictNullChecks"
        ]
    },

    // === AUTO-FIX ===
    "AUTO_FIX": {
        purpose: "Whether to automatically fix issues",
        variations: [
            "autoFix", "auto_fix", "fix", "fixable", "autofix",
            "write", "in_place", "apply"
        ]
    },

    // === FILE LIMITS ===
    "MAX_FILE_LINES": {
        purpose: "Maximum lines allowed per file",
        variations: [
            "maxLinesPerFile", "max_lines", "maxLines", "lines_per_file"
        ]
    },
    "MAX_FUNCTION_LENGTH": {
        purpose: "Maximum lines/statements per function",
        variations: [
            "maxFunctionLength", "max_statements", "max_lines_per_function"
        ]
    },

    // === SORTING/ORDERING ===
    "IMPORT_SORT": {
        purpose: "Whether/how to sort imports",
        variations: [
            "sortImports", "sort_imports", "reorderImports",
            "organizeImports", "organize_imports", "isort"
        ]
    },
    "PROPERTY_SORT": {
        purpose: "Whether to sort object properties",
        variations: [
            "sortKeys", "sort_keys", "orderPropertiesAlphabetical"
        ]
    },

    // === NAMING CONVENTIONS ===
    "NAMING_CASE": {
        purpose: "Identifier naming convention (camelCase, snake_case, etc)",
        variations: [
            "namingConvention", "naming_convention", "case",
            "casing", "style"
        ]
    },

    // === ENCODING ===
    "FILE_ENCODING": {
        purpose: "Character encoding for files",
        variations: [
            "charset", "encoding", "file_encoding", "source_encoding"
        ]
    },

    // === WHITESPACE ===
    "TRIM_TRAILING_WHITESPACE": {
        purpose: "Remove trailing whitespace from lines",
        variations: [
            "trimTrailingWhitespace", "trim_trailing_whitespace",
            "trailing_whitespace", "strip_trailing"
        ]
    },
    "INSERT_FINAL_NEWLINE": {
        purpose: "Ensure file ends with newline",
        variations: [
            "insertFinalNewline", "insert_final_newline",
            "final_newline", "ensure_newline_at_eof"
        ]
    }
};

class SemanticAudit {
    constructor() {
        this.allSettings = [];
        this.findings = new Map(); // purpose -> [{setting, variations used}]
    }

    async loadSettings() {
        const settingsPath = path.join(ROOT, "root/codebase-validation/settings.json");
        const content = await fs.readFile(settingsPath, "utf-8");
        return JSON.parse(content);
    }

    extractAll(obj, currentPath = "", parent = "") {
        const settings = [];

        for (const [key, value] of Object.entries(obj)) {
            const settingPath = currentPath ? `${currentPath}.${key}` : key;

            if (value !== null && typeof value === "object" && !Array.isArray(value)) {
                settings.push(...this.extractAll(value, settingPath, key));
            } else {
                settings.push({
                    path: settingPath,
                    key,
                    value,
                    parent,
                    valueType: typeof value
                });
            }
        }

        return settings;
    }

    findSemanticMatches() {
        for (const setting of this.allSettings) {
            const keyLower = setting.key.toLowerCase();

            for (const [purpose, config] of Object.entries(SEMANTIC_PURPOSES)) {
                for (const variation of config.variations) {
                    if (keyLower === variation.toLowerCase() ||
                        setting.path.toLowerCase().includes(variation.toLowerCase())) {

                        if (!this.findings.has(purpose)) {
                            this.findings.set(purpose, {
                                description: config.purpose,
                                occurrences: []
                            });
                        }

                        this.findings.get(purpose).occurrences.push({
                            path: setting.path,
                            key: setting.key,
                            value: setting.value,
                            matchedVariation: variation
                        });
                        break;
                    }
                }
            }
        }
    }

    async run() {
        console.log("=".repeat(100));
        console.log("SEMANTIC SETTINGS AUDIT");
        console.log("Finding settings with SAME PURPOSE but DIFFERENT NAMES across all tools/languages");
        console.log("=".repeat(100));
        console.log("");

        const settings = await this.loadSettings();
        this.allSettings = this.extractAll(settings);

        console.log(`Total settings: ${this.allSettings.length}\n`);

        this.findSemanticMatches();

        // Report findings with conflicts
        let totalConflicts = 0;
        const sortedFindings = [...this.findings.entries()].sort((a, b) => {
            // Sort by number of unique values (conflicts first)
            const aUnique = new Set(a[1].occurrences.map(o => JSON.stringify(o.value))).size;
            const bUnique = new Set(b[1].occurrences.map(o => JSON.stringify(o.value))).size;
            if (aUnique !== bUnique) return bUnique - aUnique;
            return b[1].occurrences.length - a[1].occurrences.length;
        });

        for (const [purpose, data] of sortedFindings) {
            if (data.occurrences.length < 2) continue;

            const byValue = new Map();
            for (const occ of data.occurrences) {
                const valKey = JSON.stringify(occ.value);
                if (!byValue.has(valKey)) {
                    byValue.set(valKey, []);
                }
                byValue.get(valKey).push(occ);
            }

            const hasConflict = byValue.size > 1;
            if (hasConflict) totalConflicts++;

            const status = hasConflict ? "🚨 CONFLICT" : "📋 CONSISTENT";

            console.log("\n" + "═".repeat(100));
            console.log(`${status}: ${purpose}`);
            console.log(`Purpose: ${data.description}`);
            console.log(`Found in ${data.occurrences.length} locations with ${byValue.size} unique value(s)`);
            console.log("═".repeat(100));

            for (const [valKey, locations] of byValue.entries()) {
                const value = JSON.parse(valKey);
                const valueDisplay = typeof value === "object"
                    ? JSON.stringify(value, null, 2).replace(/\n/g, "\n         ")
                    : JSON.stringify(value);

                console.log(`\n  VALUE: ${valueDisplay}`);

                for (const loc of locations) {
                    const tool = loc.path.split(".")[1] || "root";
                    console.log(`    [${tool}] ${loc.path}`);
                    if (loc.key !== loc.matchedVariation) {
                        console.log(`           (matched as "${loc.matchedVariation}")`);
                    }
                }
            }

            if (hasConflict) {
                console.log(`\n  ⚠️  ACTION REQUIRED: Unify these ${byValue.size} different values!`);
            }
        }

        // Summary
        console.log("\n\n" + "═".repeat(100));
        console.log("SUMMARY");
        console.log("═".repeat(100));

        const purposes = sortedFindings.filter(([_, d]) => d.occurrences.length >= 2);
        const conflicts = purposes.filter(([_, d]) => {
            return new Set(d.occurrences.map(o => JSON.stringify(o.value))).size > 1;
        });

        console.log(`
  Total semantic purposes tracked: ${Object.keys(SEMANTIC_PURPOSES).length}
  Purposes with multiple occurrences: ${purposes.length}
  Purposes with CONFLICTING values: ${conflicts.length}
`);

        console.log("CONFLICTS REQUIRING ACTION:");
        for (const [purpose, data] of conflicts) {
            const uniqueValues = new Set(data.occurrences.map(o => JSON.stringify(o.value)));
            console.log(`  - ${purpose}: ${data.occurrences.length} occurrences, ${uniqueValues.size} different values`);
        }

        console.log("\n" + "═".repeat(100));
        console.log("SOLUTION:");
        console.log("═".repeat(100));
        console.log(`
1. Create SINGLE SOURCE OF TRUTH in tools.codeStyle:
   - lineLength: 120
   - indentation: { size: 4, useTabs: false }
   - quotes: "double"
   - semicolons: true
   - etc.

2. Use toolMappings to TRANSFORM values for each tool's format:
   - prettier.printWidth -> codeStyle.lineLength
   - black.line-length -> codeStyle.lineLength
   - rustfmt.max_width -> codeStyle.lineLength

3. REMOVE hardcoded values from tool-specific configs

4. Create resolver function that applies mappings at runtime
`);
    }
}

const audit = new SemanticAudit();
audit.run().catch(console.error);
