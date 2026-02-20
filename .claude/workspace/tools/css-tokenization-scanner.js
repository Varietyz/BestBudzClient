/**
 * CSS Tokenization Scanner
 *
 * Forensically analyzes codebase for inline styles and hardcoded CSS values
 * that should be extracted into CSS token files.
 *
 * Detects:
 * - Inline style attributes in template literals
 * - Hardcoded CSS values in TypeScript/JavaScript
 * - Magic numbers (sizes, colors, durations)
 * - Repeated CSS patterns across files
 * - SVG attributes (fill, stroke, width, height)
 *
 * Suggests token categories:
 * - icons.css - icon sizing, colors, spacing
 * - svg.css - SVG-specific styles
 * - typography.css - fonts, sizes, weights
 * - layout.css - spacing, margins, padding
 * - borders.css - border widths, radii
 * - shadows.css - box-shadow definitions
 * - animations.css - timing, durations, easing
 * - z-index.css - layering values
 */

import { promises as fs } from "fs";
import path from "path";

// CSS value patterns to detect
const CSS_PATTERNS = {
    // Pixel values: 16px, 1.5rem, 50%, etc.
    size: /(\d+\.?\d*)(px|rem|em|%|vh|vw)/g,
    // Colors: #fff, rgb(), rgba(), hsl()
    color: /#[0-9a-f]{3,8}|rgba?\([^)]+\)|hsla?\([^)]+\)/gi,
    // Timing: 0.3s, 300ms
    timing: /(\d+\.?\d*)(ms|s)(?!\w)/g,
    // Easing functions
    easing: /cubic-bezier\([^)]+\)|ease-in-out|ease-in|ease-out|linear/g,
    // Transform values
    transform: /translate[XY]?\([^)]+\)|scale\([^)]+\)|rotate\([^)]+\)/g,
    // Box shadow
    shadow: /(\d+px\s+){2,4}rgba?\([^)]+\)/g,
    // Border radius
    borderRadius: /border-radius:\s*(\d+\.?\d*)(px|rem|%)/gi,
    // Font weights
    fontWeight: /font-weight:\s*(\d{3}|bold|normal|lighter)/gi,
    // Line heights
    lineHeight: /line-height:\s*(\d+\.?\d*)/gi,
    // Z-index
    zIndex: /z-index:\s*(\d+)/gi,
};

// SVG attribute patterns
const SVG_PATTERNS = {
    fill: /fill="([^"]*)"/g,
    stroke: /stroke="([^"]*)"/g,
    width: /(?:svg|rect|circle|path)[^>]*width="(\d+)"/gi,
    height: /(?:svg|rect|circle|path)[^>]*height="(\d+)"/gi,
    viewBox: /viewBox="([^"]*)"/g,
    strokeWidth: /stroke-width="([^"]*)"/g,
};

// Token category mapping
const TOKEN_CATEGORIES = {
    "icon-size": {
        file: "icons.css",
        patterns: ["width", "height"],
        context: ["icon", "svg"],
        varPrefix: "--icon-size-",
    },
    "svg-color": {
        file: "svg.css",
        patterns: ["fill", "stroke"],
        context: ["svg", "path", "circle", "rect"],
        varPrefix: "--svg-",
    },
    "font-size": {
        file: "typography.css",
        patterns: ["font-size"],
        context: ["font", "text"],
        varPrefix: "--font-size-",
    },
    "font-weight": {
        file: "typography.css",
        patterns: ["font-weight"],
        context: ["font", "text"],
        varPrefix: "--font-weight-",
    },
    "line-height": {
        file: "typography.css",
        patterns: ["line-height"],
        context: ["font", "text"],
        varPrefix: "--line-height-",
    },
    spacing: {
        file: "layout.css",
        patterns: ["margin", "padding", "gap"],
        context: ["margin", "padding", "gap", "spacing"],
        varPrefix: "--space-",
    },
    "border-radius": {
        file: "borders.css",
        patterns: ["border-radius", "rounded"],
        context: ["border", "radius"],
        varPrefix: "--radius-",
    },
    "border-width": {
        file: "borders.css",
        patterns: ["border-width", "border:"],
        context: ["border"],
        varPrefix: "--border-width-",
    },
    shadow: {
        file: "shadows.css",
        patterns: ["box-shadow", "shadow"],
        context: ["shadow"],
        varPrefix: "--shadow-",
    },
    transition: {
        file: "animations.css",
        patterns: ["transition", "duration"],
        context: ["transition", "animation"],
        varPrefix: "--transition-",
    },
    "z-index": {
        file: "z-index.css",
        patterns: ["z-index"],
        context: ["z-index", "layer"],
        varPrefix: "--z-",
    },
};

class CSSTokenizationScanner {
    constructor() {
        this.findings = [];
        this.stats = {
            filesScanned: 0,
            inlineStylesFound: 0,
            hardcodedValuesFound: 0,
            svgAttributesFound: 0,
            totalTokenOpportunities: 0,
        };
        this.valueFrequency = new Map(); // Track how often values appear
    }

    async scanDirectory(dirPath, options = {}) {
        const {
            extensions = [".ts", ".tsx", ".js", ".jsx", ".css"],
            exclude = ["node_modules", ".git", "dist", "build"],
            verbose = false,
        } = options;

        const files = await this.getFiles(dirPath, extensions, exclude);

        for (const file of files) {
            await this.scanFile(file, verbose);
        }

        return this.generateReport();
    }

    async getFiles(dirPath, extensions, exclude) {
        const files = [];

        async function walk(dir) {
            try {
                const entries = await fs.readdir(dir, { withFileTypes: true });

                for (const entry of entries) {
                    const fullPath = path.join(dir, entry.name);

                    if (entry.isDirectory()) {
                        if (!exclude.includes(entry.name)) {
                            await walk(fullPath);
                        }
                    } else if (entry.isFile()) {
                        if (extensions.some((ext) => entry.name.endsWith(ext))) {
                            files.push(fullPath);
                        }
                    }
                }
            } catch (error) {
                // Skip directories we can't read
            }
        }

        await walk(dirPath);
        return files;
    }

    async scanFile(filePath, verbose) {
        try {
            const content = await fs.readFile(filePath, "utf-8");
            this.stats.filesScanned++;

            const lines = content.split("\n");
            const isTypeScript = filePath.endsWith(".ts") || filePath.endsWith(".tsx");
            const isCSS = filePath.endsWith(".css");

            // Scan for inline styles in TypeScript/JS
            if (isTypeScript) {
                this.scanInlineStyles(content, filePath, lines);
                this.scanSVGAttributes(content, filePath, lines);
                this.scanHardcodedCSSValues(content, filePath, lines);
            }

            // Scan CSS files for hardcoded values
            if (isCSS) {
                this.scanCSSFile(content, filePath, lines);
            }

            if (verbose) {
                console.log(`Scanned: ${filePath}`);
            }
        } catch (error) {
            console.error(`Error reading ${filePath}: ${error.message}`);
        }
    }

    scanInlineStyles(content, filePath, lines) {
        // Match style="..." in template literals
        const styleRegex = /style="([^"]+)"/g;
        let match;

        while ((match = styleRegex.exec(content)) !== null) {
            const styleContent = match[1];
            const lineNumber = this.getLineNumber(content, match.index, lines);

            this.stats.inlineStylesFound++;

            // Parse individual CSS properties
            const properties = styleContent.split(";").filter((p) => p.trim());

            for (const prop of properties) {
                const [property, value] = prop.split(":").map((s) => s.trim());
                if (property && value) {
                    this.categorizeValue(property, value, filePath, lineNumber, "inline-style");
                }
            }
        }
    }

    scanSVGAttributes(content, filePath, lines) {
        // Scan for SVG attributes
        for (const [attr, pattern] of Object.entries(SVG_PATTERNS)) {
            let match;
            while ((match = pattern.exec(content)) !== null) {
                const value = match[1];
                const lineNumber = this.getLineNumber(content, match.index, lines);

                this.stats.svgAttributesFound++;
                this.categorizeValue(attr, value, filePath, lineNumber, "svg-attribute");
            }
        }
    }

    scanHardcodedCSSValues(content, filePath, lines) {
        // Look for CSS-in-JS patterns (e.g., { width: '16px' })
        const cssInJsRegex = /(\w+):\s*['"]([^'"]+)['"]/g;
        let match;

        while ((match = cssInJsRegex.exec(content)) !== null) {
            const property = match[1];
            const value = match[2];

            // Check if this looks like a CSS property
            if (this.isCSSProperty(property, value)) {
                const lineNumber = this.getLineNumber(content, match.index, lines);
                this.stats.hardcodedValuesFound++;
                this.categorizeValue(property, value, filePath, lineNumber, "css-in-js");
            }
        }
    }

    scanCSSFile(content, filePath, lines) {
        // Parse CSS properties
        const cssPropertyRegex = /(\w+(?:-\w+)*):\s*([^;]+);/g;
        let match;

        while ((match = cssPropertyRegex.exec(content)) !== null) {
            const property = match[1];
            const value = match[2].trim();

            // Skip if already using a CSS variable
            if (value.startsWith("var(--")) {
                continue;
            }

            const lineNumber = this.getLineNumber(content, match.index, lines);
            this.categorizeValue(property, value, filePath, lineNumber, "css-file");
        }
    }

    categorizeValue(property, value, filePath, lineNumber, source) {
        // Determine which token category this belongs to
        let category = null;
        let suggestedVar = null;
        let priority = "low";

        // Check against token categories
        for (const [catName, catConfig] of Object.entries(TOKEN_CATEGORIES)) {
            const matchesPattern = catConfig.patterns.some((p) =>
                property.toLowerCase().includes(p.toLowerCase())
            );
            const matchesContext = catConfig.context.some(
                (c) =>
                    property.toLowerCase().includes(c.toLowerCase()) ||
                    value.toLowerCase().includes(c.toLowerCase())
            );

            if (matchesPattern || matchesContext) {
                category = catName;
                suggestedVar = this.generateVariableName(catConfig.varPrefix, property, value);
                break;
            }
        }

        // Fallback category detection
        if (!category) {
            if (CSS_PATTERNS.color.test(value)) {
                category = "color";
                suggestedVar = `--color-${this.normalizeValue(value)}`;
            } else if (CSS_PATTERNS.size.test(value)) {
                category = "size";
                suggestedVar = `--size-${this.normalizeValue(value)}`;
            } else if (CSS_PATTERNS.timing.test(value)) {
                category = "timing";
                suggestedVar = `--duration-${this.normalizeValue(value)}`;
            }
        }

        if (category) {
            // Track value frequency
            const valueKey = `${property}:${value}`;
            this.valueFrequency.set(valueKey, (this.valueFrequency.get(valueKey) || 0) + 1);

            // Higher priority if used multiple times
            const frequency = this.valueFrequency.get(valueKey);
            if (frequency > 5) priority = "high";
            else if (frequency > 2) priority = "medium";

            const finding = {
                category,
                tokenFile: TOKEN_CATEGORIES[category]?.file || "misc.css",
                property,
                value,
                suggestedVar,
                filePath: filePath.replace(/\\/g, "/"),
                lineNumber,
                source,
                priority,
                frequency,
            };

            this.findings.push(finding);
            this.stats.totalTokenOpportunities++;
        }
    }

    isCSSProperty(property, value) {
        const cssProperties = [
            "width",
            "height",
            "margin",
            "padding",
            "color",
            "background",
            "border",
            "font",
            "display",
            "position",
            "top",
            "left",
            "right",
            "bottom",
            "transform",
            "transition",
            "opacity",
            "z-index",
            "gap",
            "flex",
            "grid",
        ];

        return cssProperties.some((p) => property.toLowerCase().includes(p));
    }

    generateVariableName(prefix, property, value) {
        // Generate a semantic variable name
        const normalized = this.normalizeValue(value);
        return `${prefix}${normalized}`;
    }

    normalizeValue(value) {
        // Convert value to a variable name fragment
        return value
            .replace(/[^a-z0-9]/gi, "-")
            .replace(/-+/g, "-")
            .replace(/^-|-$/g, "")
            .toLowerCase()
            .substring(0, 30);
    }

    getLineNumber(content, index, lines) {
        let count = 0;
        for (let i = 0; i < lines.length; i++) {
            count += lines[i].length + 1; // +1 for newline
            if (count > index) {
                return i + 1;
            }
        }
        return lines.length;
    }

    generateReport() {
        // Group by token file
        const byTokenFile = {};
        for (const finding of this.findings) {
            const file = finding.tokenFile;
            if (!byTokenFile[file]) {
                byTokenFile[file] = [];
            }
            byTokenFile[file].push(finding);
        }

        // Group by source file
        const bySourceFile = {};
        for (const finding of this.findings) {
            const file = finding.filePath;
            if (!bySourceFile[file]) {
                bySourceFile[file] = [];
            }
            bySourceFile[file].push(finding);
        }

        // Sort by frequency (most used first)
        this.findings.sort((a, b) => b.frequency - a.frequency);

        return {
            stats: this.stats,
            findings: this.findings,
            byTokenFile,
            bySourceFile,
            summary: {
                totalFiles: this.stats.filesScanned,
                totalFindings: this.findings.length,
                high: this.findings.filter((f) => f.priority === "high").length,
                medium: this.findings.filter((f) => f.priority === "medium").length,
                low: this.findings.filter((f) => f.priority === "low").length,
            },
        };
    }
}

// Main execution
async function main() {
    const scanner = new CSSTokenizationScanner();

    const targetDir = process.argv[2] || "./root/archlab-ide/src/renderer";
    console.log(`\n🔍 CSS Tokenization Analysis: ${targetDir}\n`);

    const report = await scanner.scanDirectory(targetDir, {
        extensions: [".ts", ".tsx", ".js", ".jsx", ".css"],
        exclude: ["node_modules", ".git", "dist", "build"],
        verbose: false,
    });

    // Output summary
    console.log("═".repeat(80));
    console.log("CSS TOKENIZATION OPPORTUNITIES");
    console.log("═".repeat(80));
    console.log(`\nFiles scanned: ${report.stats.filesScanned}`);
    console.log(`Inline styles found: ${report.stats.inlineStylesFound}`);
    console.log(`Hardcoded values found: ${report.stats.hardcodedValuesFound}`);
    console.log(`SVG attributes found: ${report.stats.svgAttributesFound}`);
    console.log(`Total tokenization opportunities: ${report.stats.totalTokenOpportunities}`);

    console.log(`\nFindings by priority:`);
    console.log(`  🔴 HIGH:   ${report.summary.high}`);
    console.log(`  🟡 MEDIUM: ${report.summary.medium}`);
    console.log(`  🟢 LOW:    ${report.summary.low}`);

    console.log("\n" + "═".repeat(80));
    console.log("SUGGESTED TOKEN FILES");
    console.log("═".repeat(80));

    for (const [tokenFile, findings] of Object.entries(report.byTokenFile)) {
        console.log(`\n📄 ${tokenFile} (${findings.length} tokens)`);

        // Get unique values by frequency
        const uniqueValues = new Map();
        for (const finding of findings) {
            const key = `${finding.property}:${finding.value}`;
            if (!uniqueValues.has(key) || uniqueValues.get(key).frequency < finding.frequency) {
                uniqueValues.set(key, finding);
            }
        }

        // Show top 10 for this file
        const sorted = Array.from(uniqueValues.values())
            .sort((a, b) => b.frequency - a.frequency)
            .slice(0, 10);

        for (const finding of sorted) {
            console.log(
                `  ${finding.suggestedVar}: ${finding.value} (used ${finding.frequency}x)`
            );
        }
    }

    console.log("\n" + "═".repeat(80));
    console.log("TOP 20 MOST FREQUENT VALUES");
    console.log("═".repeat(80));

    const top20 = report.findings.slice(0, 20);
    for (const finding of top20) {
        const shortPath = finding.filePath.split("renderer/")[1] || finding.filePath;
        console.log(`\n[${finding.priority.toUpperCase()}] ${finding.suggestedVar}`);
        console.log(`  Value: ${finding.property}: ${finding.value}`);
        console.log(`  Used ${finding.frequency}x`);
        console.log(`  Token file: ${finding.tokenFile}`);
        console.log(`  Example: ${shortPath}:${finding.lineNumber}`);
    }

    // Write full report to JSON
    const reportPath = "./.claude/workspace/tools/css-tokenization-report.json";
    await fs.writeFile(reportPath, JSON.stringify(report, null, 2));
    console.log(`\n📄 Full report written to: ${reportPath}`);
}

main().catch(console.error);
