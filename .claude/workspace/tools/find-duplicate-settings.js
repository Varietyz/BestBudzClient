/**
 * Duplicate Settings Finder
 *
 * Scans all configuration files to find settings serving the same purpose
 * but defined in multiple locations with potentially conflicting values.
 *
 * DEV-RULES.md: "CentralizeAsVersionedSymbols" - violations detector
 */

import { promises as fs } from "fs";
import path from "path";

const ROOT = process.cwd();

// Semantic categories - settings that serve the same purpose
const SEMANTIC_CATEGORIES = {
    indent: {
        patterns: [
            /indent/i, /tabWidth/i, /tab_width/i, /tab_spaces/i,
            /indentWidth/i, /indent_width/i, /indentSize/i, /indent_size/i,
            /IndentWidth/i
        ],
        description: "Indentation size/style"
    },
    lineLength: {
        patterns: [
            /lineLength/i, /lineWidth/i, /line_length/i, /line_width/i,
            /maxLine/i, /max_line/i, /printWidth/i, /print_width/i,
            /columnLimit/i, /column_limit/i, /maxWidth/i, /max_width/i,
            /ColumnLimit/i
        ],
        description: "Maximum line length"
    },
    quotes: {
        patterns: [
            /quote/i, /singleQuote/i, /single_quote/i, /doubleQuote/i,
            /quoteStyle/i, /quote_style/i
        ],
        description: "Quote style (single/double)"
    },
    semicolons: {
        patterns: [
            /semi(?!colon)/i, /semicolon/i
        ],
        description: "Semicolon usage"
    },
    tabs: {
        patterns: [
            /useTabs/i, /use_tabs/i, /hard_tabs/i, /UseTab/i
        ],
        description: "Tabs vs spaces"
    },
    endOfLine: {
        patterns: [
            /endOfLine/i, /end_of_line/i, /lineEnding/i, /line_ending/i,
            /eol/i
        ],
        description: "Line ending style (lf/crlf)"
    },
    trailingComma: {
        patterns: [
            /trailingComma/i, /trailing_comma/i
        ],
        description: "Trailing comma style"
    },
    bracketSpacing: {
        patterns: [
            /bracketSpacing/i, /bracket_spacing/i
        ],
        description: "Bracket spacing"
    }
};

// Files to scan - include all JSON configs
const CONFIG_PATTERNS = [
    "settings.json",
    "package.json",
    ".prettierrc",
    ".prettierrc.json",
    "prettier.config.js",
    ".eslintrc*",
    "eslint.config.js",
    ".stylelintrc*",
    "biome.json",
    "tsconfig.json",
    ".editorconfig",
    "pyproject.toml",
    "ruff.toml",
    ".black",
    "Cargo.toml",
    ".clang-format"
];

// Specific important files to always scan
const EXPLICIT_FILES = [
    "root/codebase-validation/settings.json",
    "config/.prettierrc",
    "config/eslint.config.js",
    ".vscode/settings.json"
];

class DuplicateSettingsFinder {
    constructor() {
        this.findings = new Map(); // category -> [{file, path, value}]
        this.allSettings = []; // flat list of all settings found
    }

    async findConfigFiles(dir) {
        const files = [];

        async function scan(currentDir, depth = 0) {
            if (depth > 5) return; // limit depth

            try {
                const entries = await fs.readdir(currentDir, { withFileTypes: true });

                for (const entry of entries) {
                    const fullPath = path.join(currentDir, entry.name);

                    if (entry.isDirectory()) {
                        if (!["node_modules", ".git", "dist", "build", "coverage"].includes(entry.name)) {
                            await scan(fullPath, depth + 1);
                        }
                    } else if (entry.isFile()) {
                        // Check against patterns
                        for (const pattern of CONFIG_PATTERNS) {
                            if (pattern.includes("*")) {
                                const regex = new RegExp(pattern.replace("*", ".*"));
                                if (regex.test(entry.name)) {
                                    files.push(fullPath);
                                    break;
                                }
                            } else if (entry.name === pattern || entry.name.endsWith(pattern)) {
                                files.push(fullPath);
                                break;
                            }
                        }
                    }
                }
            } catch (err) {
                // Skip inaccessible directories
            }
        }

        await scan(dir);
        return files;
    }

    extractSettingsFromJSON(obj, filePath, currentPath = "") {
        const settings = [];

        for (const [key, value] of Object.entries(obj)) {
            const settingPath = currentPath ? `${currentPath}.${key}` : key;

            if (value !== null && typeof value === "object" && !Array.isArray(value)) {
                settings.push(...this.extractSettingsFromJSON(value, filePath, settingPath));
            } else {
                settings.push({
                    file: path.relative(ROOT, filePath),
                    path: settingPath,
                    key,
                    value,
                    valueType: typeof value
                });
            }
        }

        return settings;
    }

    categorizeSettings(settings) {
        for (const setting of settings) {
            for (const [category, config] of Object.entries(SEMANTIC_CATEGORIES)) {
                for (const pattern of config.patterns) {
                    if (pattern.test(setting.key) || pattern.test(setting.path)) {
                        if (!this.findings.has(category)) {
                            this.findings.set(category, []);
                        }
                        this.findings.get(category).push({
                            ...setting,
                            matchedPattern: pattern.toString()
                        });
                        break;
                    }
                }
            }
        }
    }

    async parseFile(filePath) {
        try {
            const content = await fs.readFile(filePath, "utf-8");
            const ext = path.extname(filePath);

            if (ext === ".json" || filePath.endsWith(".prettierrc") || filePath.endsWith("rc")) {
                try {
                    // Try to parse as JSON (with comments stripped)
                    const cleaned = content.replace(/\/\*[\s\S]*?\*\/|\/\/.*/g, "");
                    const json = JSON.parse(cleaned);
                    return this.extractSettingsFromJSON(json, filePath);
                } catch {
                    return [];
                }
            } else if (ext === ".toml") {
                // Basic TOML parsing for key-value pairs
                const settings = [];
                const lines = content.split("\n");
                let currentSection = "";

                for (const line of lines) {
                    const sectionMatch = line.match(/^\[(.*?)\]/);
                    if (sectionMatch) {
                        currentSection = sectionMatch[1];
                        continue;
                    }

                    const kvMatch = line.match(/^(\w+)\s*=\s*(.+)/);
                    if (kvMatch) {
                        settings.push({
                            file: path.relative(ROOT, filePath),
                            path: currentSection ? `${currentSection}.${kvMatch[1]}` : kvMatch[1],
                            key: kvMatch[1],
                            value: kvMatch[2].trim(),
                            valueType: "toml"
                        });
                    }
                }
                return settings;
            } else if (filePath.endsWith(".editorconfig")) {
                const settings = [];
                const lines = content.split("\n");
                let currentSection = "root";

                for (const line of lines) {
                    const sectionMatch = line.match(/^\[(.*?)\]/);
                    if (sectionMatch) {
                        currentSection = sectionMatch[1];
                        continue;
                    }

                    const kvMatch = line.match(/^(\w+)\s*=\s*(.+)/);
                    if (kvMatch) {
                        settings.push({
                            file: path.relative(ROOT, filePath),
                            path: `${currentSection}.${kvMatch[1]}`,
                            key: kvMatch[1],
                            value: kvMatch[2].trim(),
                            valueType: "editorconfig"
                        });
                    }
                }
                return settings;
            }

            return [];
        } catch (err) {
            return [];
        }
    }

    async run() {
        console.log("Scanning for configuration files...\n");

        const configFiles = await this.findConfigFiles(ROOT);

        // Add explicit files
        for (const explicit of EXPLICIT_FILES) {
            const fullPath = path.join(ROOT, explicit);
            try {
                await fs.access(fullPath);
                if (!configFiles.includes(fullPath)) {
                    configFiles.push(fullPath);
                }
            } catch {
                // File doesn't exist
            }
        }

        console.log(`Found ${configFiles.length} configuration files\n`);
        for (const f of configFiles) {
            console.log(`  - ${path.relative(ROOT, f)}`);
        }
        console.log("");

        for (const file of configFiles) {
            const settings = await this.parseFile(file);
            this.allSettings.push(...settings);
        }

        console.log(`Extracted ${this.allSettings.length} settings\n`);

        this.categorizeSettings(this.allSettings);

        // Report findings
        console.log("=".repeat(80));
        console.log("DUPLICATE SETTINGS ANALYSIS");
        console.log("=".repeat(80));
        console.log("");

        let hasConflicts = false;

        for (const [category, settings] of this.findings.entries()) {
            if (settings.length < 2) continue;

            const config = SEMANTIC_CATEGORIES[category];
            console.log(`\n${"─".repeat(80)}`);
            console.log(`CATEGORY: ${category.toUpperCase()}`);
            console.log(`Purpose: ${config.description}`);
            console.log(`Found in ${settings.length} locations:`);
            console.log(`${"─".repeat(80)}`);

            // Group by value to find conflicts
            const byValue = new Map();
            for (const s of settings) {
                const valKey = JSON.stringify(s.value);
                if (!byValue.has(valKey)) {
                    byValue.set(valKey, []);
                }
                byValue.get(valKey).push(s);
            }

            if (byValue.size > 1) {
                hasConflicts = true;
                console.log("⚠️  CONFLICT DETECTED - Different values:\n");
            }

            for (const [valKey, locations] of byValue.entries()) {
                const value = JSON.parse(valKey);
                console.log(`  Value: ${JSON.stringify(value)}`);
                for (const loc of locations) {
                    console.log(`    → ${loc.file}`);
                    console.log(`      Path: ${loc.path}`);
                }
                console.log("");
            }
        }

        if (!hasConflicts) {
            console.log("\n✅ No conflicting values found (but duplicates still exist)\n");
        }

        // Summary
        console.log("\n" + "=".repeat(80));
        console.log("SUMMARY");
        console.log("=".repeat(80));
        console.log("");
        console.log("Categories with duplicates:");
        for (const [category, settings] of this.findings.entries()) {
            if (settings.length >= 2) {
                const uniqueValues = new Set(settings.map(s => JSON.stringify(s.value)));
                const status = uniqueValues.size > 1 ? "⚠️  CONFLICT" : "📋 Duplicate";
                console.log(`  ${status} ${category}: ${settings.length} locations, ${uniqueValues.size} unique value(s)`);
            }
        }

        console.log("\n" + "=".repeat(80));
        console.log("RECOMMENDATION: Centralize all values in tools.codeStyle");
        console.log("=".repeat(80));
    }
}

const finder = new DuplicateSettingsFinder();
finder.run().catch(console.error);
