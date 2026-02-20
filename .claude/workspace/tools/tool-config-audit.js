/**
 * Tool Configuration Audit
 *
 * Focused audit of settings that configure THE SAME THING across different tools.
 * This finds where one central value should control multiple tools.
 */

import { promises as fs } from "fs";
import path from "path";

const ROOT = process.cwd();

// Map of PURPOSES to the EXACT settings paths that serve that purpose
// Updated to use the new rules architecture (Rules → Tools → Code)
const PURPOSE_MAPPINGS = {
    "LINE_LENGTH": {
        purpose: "Maximum characters per line",
        centralSource: "rules.formatting.lineLength",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.printWidth" },
            { tool: "eslint", path: "tools.toolMappings.eslint.max-len" },
            { tool: "black", path: "tools.toolMappings.black.line-length" },
            { tool: "isort", path: "tools.toolMappings.isort.line_length" },
            { tool: "ruff", path: "tools.toolMappings.ruff.line-length" },
            { tool: "rustfmt", path: "tools.toolMappings.rustfmt.max_width" },
            { tool: "clangFormat", path: "tools.toolMappings.clangFormat.ColumnLimit" },
            { tool: "checkstyle", path: "tools.toolMappings.checkstyle.maxLineLength" },
        ]
    },

    "INDENT_SIZE": {
        purpose: "Number of spaces for indentation",
        centralSource: "rules.formatting.indentation.size",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.tabWidth" },
            { tool: "eslint", path: "tools.toolMappings.eslint.indent" },
            { tool: "stylelint", path: "tools.toolMappings.stylelint.indentation" },
            { tool: "rustfmt", path: "tools.toolMappings.rustfmt.tab_spaces" },
            { tool: "gofmt", path: "tools.toolMappings.gofmt.tabWidth" },
            { tool: "clangFormat", path: "tools.toolMappings.clangFormat.IndentWidth" },
            { tool: "checkstyle", path: "tools.toolMappings.checkstyle.tabWidth" },
            { tool: "editorconfig", path: "tools.toolMappings.editorconfig.indent_size" },
        ]
    },

    "INDENT_STYLE": {
        purpose: "Use tabs or spaces",
        centralSource: "rules.formatting.indentation.useTabs",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.useTabs" },
            { tool: "rustfmt", path: "tools.toolMappings.rustfmt.hard_tabs" },
            { tool: "clangFormat", path: "tools.toolMappings.clangFormat.UseTab" },
            { tool: "editorconfig", path: "tools.toolMappings.editorconfig.indent_style" },
        ]
    },

    "QUOTE_STYLE": {
        purpose: "Single or double quotes",
        centralSource: "rules.formatting.quotes",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.singleQuote" },
            { tool: "eslint", path: "tools.toolMappings.eslint.quotes" },
            { tool: "ruff", path: "tools.toolMappings.ruff.quote-style" },
        ]
    },

    "END_OF_LINE": {
        purpose: "Line ending style (lf/crlf)",
        centralSource: "rules.formatting.endOfLine",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.endOfLine" },
            { tool: "editorconfig", path: "tools.toolMappings.editorconfig.end_of_line" },
        ]
    },

    "SEMICOLONS": {
        purpose: "Use semicolons",
        centralSource: "rules.formatting.semicolons",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.semi" },
            { tool: "eslint", path: "tools.toolMappings.eslint.semi" },
        ]
    },

    "TRAILING_COMMA": {
        purpose: "Trailing comma style",
        centralSource: "rules.formatting.trailingComma",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.trailingComma" },
        ]
    },

    "BRACKET_SPACING": {
        purpose: "Spaces in object literals",
        centralSource: "rules.formatting.bracketSpacing",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.bracketSpacing" },
        ]
    },

    "ARROW_PARENS": {
        purpose: "Parens around single arrow param",
        centralSource: "rules.formatting.arrowParens",
        toolSettings: [
            { tool: "prettier", path: "tools.toolMappings.prettier.arrowParens" },
        ]
    },

    "PYTHON_VERSION": {
        purpose: "Python target version",
        centralSource: "rules.versions.python",
        toolSettings: [
            { tool: "ruff", path: "tools.toolMappings.ruff.target-version" },
            { tool: "pyright", path: "tools.toolMappings.pyright.pythonVersion" },
            { tool: "black", path: "tools.toolMappings.black.target-version" },
            { tool: "mypy", path: "tools.toolMappings.mypy.python_version" },
        ]
    },

    "ECMA_VERSION": {
        purpose: "ECMAScript/JavaScript version",
        centralSource: "rules.versions.ecmascript",
        toolSettings: [
            { tool: "eslint", path: "tools.toolMappings.eslint.ecmaVersion" },
            { tool: "typescript", path: "tools.toolMappings.typescript.target" },
        ]
    },

    "TYPE_STRICTNESS": {
        purpose: "Type checking strictness level",
        centralSource: "rules.strictness.typeChecking",
        toolSettings: [
            { tool: "pyright", path: "tools.toolMappings.pyright.typeCheckingMode" },
            { tool: "mypy", path: "tools.toolMappings.mypy.strict" },
            { tool: "typescript", path: "tools.toolMappings.typescript.strict" },
        ]
    }
};

class ToolConfigAudit {
    constructor() {
        this.settings = null;
    }

    async loadSettings() {
        const settingsPath = path.join(ROOT, "root/codebase-validation/settings.json");
        const content = await fs.readFile(settingsPath, "utf-8");
        return JSON.parse(content);
    }

    getValue(path) {
        const parts = path.split(".");
        let current = this.settings;
        for (const part of parts) {
            if (current === undefined || current === null) return undefined;
            current = current[part];
        }
        return current;
    }

    normalizeValue(value) {
        // Normalize different representations to comparable form
        if (typeof value === "boolean") return value;
        if (typeof value === "number") return value;
        if (Array.isArray(value)) return JSON.stringify(value);
        if (typeof value === "string") {
            // Handle reference strings
            if (value.startsWith("codeStyle.")) return `REF:${value}`;
            // Normalize Python version formats
            if (value.match(/^py\d+$/)) return value.replace("py", "");
            if (value.match(/^\d+\.\d+$/)) return value.replace(".", "");
            return value;
        }
        if (typeof value === "object" && value !== null) {
            return JSON.stringify(value);
        }
        return value;
    }

    async run() {
        this.settings = await this.loadSettings();

        console.log("═".repeat(100));
        console.log("TOOL CONFIGURATION AUDIT");
        console.log("Finding settings that should be centralized across all tools");
        console.log("═".repeat(100));
        console.log("");

        let totalIssues = 0;

        for (const [purposeKey, config] of Object.entries(PURPOSE_MAPPINGS)) {
            console.log("\n" + "─".repeat(100));
            console.log(`PURPOSE: ${purposeKey}`);
            console.log(`What it does: ${config.purpose}`);
            console.log("─".repeat(100));

            const centralValue = config.centralSource
                ? this.getValue(config.centralSource)
                : "NOT YET CENTRALIZED";

            console.log(`\n  CENTRAL SOURCE: ${config.centralSource || "NONE - NEEDS CREATION"}`);
            console.log(`  CENTRAL VALUE: ${JSON.stringify(centralValue)}`);

            // Categorize settings
            const correct = [];
            const hardcodedMismatch = [];
            const hardcodedMatch = [];
            const missing = [];

            for (const setting of config.toolSettings) {
                const value = this.getValue(setting.path);

                if (value === undefined) {
                    missing.push(setting);
                } else if (setting.isHardcoded) {
                    const normalizedValue = this.normalizeValue(value);
                    const normalizedCentral = this.normalizeValue(centralValue);

                    if (normalizedValue === normalizedCentral ||
                        (typeof value === "string" && value.includes("codeStyle"))) {
                        hardcodedMatch.push({ ...setting, value });
                    } else {
                        hardcodedMismatch.push({ ...setting, value });
                        totalIssues++;
                    }
                } else {
                    correct.push({ ...setting, value });
                }
            }

            if (correct.length > 0) {
                console.log(`\n  ✅ CORRECT (using toolMappings references):`);
                for (const s of correct) {
                    console.log(`     [${s.tool}] ${s.path}`);
                    console.log(`              = ${JSON.stringify(s.value)}`);
                }
            }

            if (hardcodedMatch.length > 0) {
                console.log(`\n  ⚠️  HARDCODED (matches central, but should use reference):`);
                for (const s of hardcodedMatch) {
                    console.log(`     [${s.tool}] ${s.path}`);
                    console.log(`              = ${JSON.stringify(s.value)}`);
                    console.log(`              → Should reference: ${config.centralSource}`);
                }
            }

            if (hardcodedMismatch.length > 0) {
                console.log(`\n  🚨 CONFLICT (hardcoded with DIFFERENT value!):`);
                for (const s of hardcodedMismatch) {
                    console.log(`     [${s.tool}] ${s.path}`);
                    console.log(`              = ${JSON.stringify(s.value)}`);
                    console.log(`              ≠ central ${JSON.stringify(centralValue)}`);
                }
            }

            if (!config.centralSource) {
                console.log(`\n  ⚠️  NO CENTRAL SOURCE - All these tools have independent values:`);
                for (const s of config.toolSettings) {
                    const value = this.getValue(s.path);
                    if (value !== undefined) {
                        console.log(`     [${s.tool}] ${s.path} = ${JSON.stringify(value)}`);
                    }
                }
            }
        }

        // Summary
        console.log("\n\n" + "═".repeat(100));
        console.log("SUMMARY");
        console.log("═".repeat(100));

        console.log(`\n  Total purposes tracked: ${Object.keys(PURPOSE_MAPPINGS).length}`);
        console.log(`  Issues requiring action: ${totalIssues}`);

        console.log("\n  ISSUES BY TYPE:");

        // Count issues
        let noSource = 0;
        let conflicting = 0;

        for (const [purposeKey, config] of Object.entries(PURPOSE_MAPPINGS)) {
            if (!config.centralSource) {
                noSource++;
            }

            const centralValue = config.centralSource
                ? this.getValue(config.centralSource)
                : null;

            for (const setting of config.toolSettings) {
                if (!setting.isHardcoded) continue;
                const value = this.getValue(setting.path);
                if (value === undefined) continue;

                const normalizedValue = this.normalizeValue(value);
                const normalizedCentral = this.normalizeValue(centralValue);

                if (normalizedValue !== normalizedCentral &&
                    !(typeof value === "string" && value.includes("codeStyle"))) {
                    conflicting++;
                }
            }
        }

        console.log(`    - Purposes without central source: ${noSource}`);
        console.log(`    - Hardcoded values conflicting with central: ${conflicting}`);

        console.log(`\n  RECOMMENDED FIXES:`);
        console.log(`    1. Add missing central sources (PYTHON_VERSION, ECMA_VERSION)`);
        console.log(`    2. Update toolMappings for tools missing references`);
        console.log(`    3. Remove hardcoded values from tool.* sections`);
        console.log(`    4. Create accessor that resolves references at runtime`);
    }
}

const audit = new ToolConfigAudit();
audit.run().catch(console.error);
