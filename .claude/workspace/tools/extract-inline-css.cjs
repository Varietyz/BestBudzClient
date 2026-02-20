#!/usr/bin/env node
/**
 * Inline CSS Extractor v2
 *
 * Scans archlab-ide for ALL inline styles and extracts them:
 * - Static values → CSS files (append mode, no duplicates)
 * - Dynamic values → Migration report for tokens/inlined/
 *
 * Patterns detected:
 * 1. style: { property: "value" } - DOM.createElement options
 * 2. .style.cssText = `...` - Multi-line CSS strings
 * 3. .style.property = "value" - Direct assignments (static)
 * 4. .style.property = variable - Dynamic (flagged for tokens/inlined/)
 *
 * APPEND MODE: Merges with existing CSS files, avoids duplicates
 */

const fs = require("fs");
const path = require("path");

// =============================================================================
// Configuration
// =============================================================================

const SRC_DIR = path.resolve(__dirname, "../../../root/archlab-ide/src/renderer");
const TOKENS_DIR = path.resolve(SRC_DIR, "styles/tokens");
const COMPONENTS_CSS_DIR = path.resolve(SRC_DIR, "styles/components");
const REPORTS_DIR = path.resolve(__dirname, "../scanners/reports");

const BASE_FONT_SIZE = 16;
const MATCH_THRESHOLD = 0.2;

const EXCLUDE_PATTERNS = [
    /node_modules/,
    /\.d\.ts$/,
    /styles\/tokens\/inlined\//,  // Don't scan the inlined tokens
];

// =============================================================================
// Token Registry
// =============================================================================

class TokenRegistry {
    constructor() {
        this.tokens = new Map();
        this.byCategory = new Map();
    }

    loadFromDirectory(tokenDir) {
        const files = fs.readdirSync(tokenDir).filter((f) => f.endsWith(".css"));

        for (const file of files) {
            const content = fs.readFileSync(path.join(tokenDir, file), "utf-8");
            const varPattern = /--([a-zA-Z0-9_-]+)\s*:\s*([^;]+);/g;
            let match;

            while ((match = varPattern.exec(content)) !== null) {
                const name = `--${match[1]}`;
                const value = match[2].trim();
                const category = this.categorize(name);
                const numericValue = this.parseNumeric(value);

                const entry = { name, value, numericValue, category, file };
                this.tokens.set(name, entry);

                if (!this.byCategory.has(category)) {
                    this.byCategory.set(category, []);
                }
                this.byCategory.get(category).push(entry);
            }
        }

        console.log(`Loaded ${this.tokens.size} tokens from ${files.length} files`);
    }

    categorize(name) {
        if (name.startsWith("--color-")) return "color";
        if (name.startsWith("--space-")) return "spacing";
        if (name.startsWith("--font-size-")) return "font-size";
        if (name.startsWith("--font-weight-")) return "font-weight";
        if (name.startsWith("--radius-")) return "radius";
        if (name.startsWith("--shadow-")) return "shadow";
        if (name.startsWith("--transition-") || name.startsWith("--anim-")) return "animation";
        if (name.startsWith("--z-")) return "z-index";
        if (name.startsWith("--border-width-")) return "border-width";
        if (name.startsWith("--icon-size-")) return "icon-size";
        if (name.startsWith("--svg-")) return "svg";
        if (name.startsWith("--size-")) return "size";
        return "unknown";
    }

    parseNumeric(value) {
        const remMatch = value.match(/^([\d.]+)rem$/);
        if (remMatch) return parseFloat(remMatch[1]) * BASE_FONT_SIZE;

        const pxMatch = value.match(/^([\d.]+)px$/);
        if (pxMatch) return parseFloat(pxMatch[1]);

        const numMatch = value.match(/^([\d.]+)$/);
        if (numMatch) return parseFloat(numMatch[1]);

        const msMatch = value.match(/^([\d.]+)ms$/);
        if (msMatch) return parseFloat(msMatch[1]);

        const percentMatch = value.match(/^([\d.]+)%$/);
        if (percentMatch) return parseFloat(percentMatch[1]);

        return null;
    }

    findClosest(value, categories) {
        const inputNumeric = this.parseNumeric(value);
        if (inputNumeric === null) return null;

        let bestMatch = null;
        let bestDiff = Infinity;

        for (const cat of categories) {
            const tokens = this.byCategory.get(cat) || [];
            for (const token of tokens) {
                if (token.numericValue === null) continue;

                const diff = Math.abs(inputNumeric - token.numericValue);
                const max = Math.max(Math.abs(inputNumeric), Math.abs(token.numericValue));
                const percentage = max === 0 ? 0 : diff / max;

                if (percentage <= MATCH_THRESHOLD && percentage < bestDiff) {
                    bestDiff = percentage;
                    bestMatch = token;
                }
            }
        }

        return bestMatch;
    }
}

// =============================================================================
// Property to Category Mapping
// =============================================================================

const PROPERTY_CATEGORIES = {
    color: ["color"],
    "background-color": ["color"],
    background: ["color"],
    "border-color": ["color"],
    fill: ["color", "svg"],
    stroke: ["color", "svg"],
    margin: ["spacing"],
    "margin-top": ["spacing"],
    "margin-right": ["spacing"],
    "margin-bottom": ["spacing"],
    "margin-left": ["spacing"],
    padding: ["spacing"],
    "padding-top": ["spacing"],
    "padding-right": ["spacing"],
    "padding-bottom": ["spacing"],
    "padding-left": ["spacing"],
    gap: ["spacing"],
    top: ["spacing"],
    right: ["spacing"],
    bottom: ["spacing"],
    left: ["spacing"],
    "font-size": ["font-size"],
    "font-weight": ["font-weight"],
    width: ["spacing", "icon-size", "size"],
    height: ["spacing", "icon-size", "size"],
    "min-width": ["spacing", "size"],
    "max-width": ["spacing", "size"],
    "min-height": ["spacing", "size"],
    "max-height": ["spacing", "size"],
    "border-radius": ["radius"],
    "border-width": ["border-width"],
    "z-index": ["z-index"],
    transition: ["animation"],
};

// Dynamic pattern keywords that need tokens/inlined/
const DYNAMIC_INDICATORS = [
    /\$\{/,           // Template literal interpolation
    /^\s*[a-zA-Z_$][a-zA-Z0-9_$]*\s*$/,  // Variable reference
    /\s*\?\s*/,       // Ternary operator
    /String\(/,       // String conversion
    /\.toString\(/,   // toString call
];

// =============================================================================
// Existing CSS Parser
// =============================================================================

class ExistingCSSParser {
    /**
     * Parse existing CSS file and extract rule signatures
     */
    static parseFile(filePath) {
        if (!fs.existsSync(filePath)) {
            return { rules: new Map(), rawContent: "" };
        }

        const content = fs.readFileSync(filePath, "utf-8");
        const rules = new Map();

        // Extract class rules
        const rulePattern = /(\/\*[^*]*\*\/\s*)?(\.[\w-]+(?:__[\w-]+)?)\s*\{([^}]+)\}/g;
        let match;

        while ((match = rulePattern.exec(content)) !== null) {
            const comment = match[1] || "";
            const selector = match[2];
            const body = match[3].trim();

            // Create signature from sorted properties
            const props = body
                .split(";")
                .map((p) => p.trim())
                .filter((p) => p)
                .sort()
                .join(";");

            rules.set(props, { selector, body, comment });
        }

        return { rules, rawContent: content };
    }

    /**
     * Merge new rules with existing, avoiding duplicates
     */
    static mergeRules(existingRules, newRules) {
        const merged = new Map(existingRules);
        const added = [];

        for (const [signature, rule] of newRules) {
            if (!merged.has(signature)) {
                merged.set(signature, rule);
                added.push(rule);
            }
        }

        return { merged, added };
    }
}

// =============================================================================
// Style Extractor
// =============================================================================

class InlineCSSExtractor {
    constructor(registry) {
        this.registry = registry;
        this.staticFindings = [];      // Can be extracted to CSS
        this.dynamicFindings = [];     // Need tokens/inlined/
        this.extractedByComponent = new Map();
        this.classCounter = 0;
    }

    scan(srcDir) {
        const files = this.findTSFiles(srcDir);
        console.log(`Scanning ${files.length} TypeScript files...`);

        for (const file of files) {
            this.scanFile(file, srcDir);
        }

        return {
            static: this.staticFindings,
            dynamic: this.dynamicFindings,
        };
    }

    findTSFiles(dir, files = []) {
        const entries = fs.readdirSync(dir, { withFileTypes: true });

        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);
            const relativePath = fullPath.replace(/\\/g, "/");

            if (EXCLUDE_PATTERNS.some((p) => p.test(relativePath))) continue;

            if (entry.isDirectory()) {
                this.findTSFiles(fullPath, files);
            } else if (entry.isFile() && /\.tsx?$/.test(entry.name)) {
                files.push(fullPath);
            }
        }

        return files;
    }

    scanFile(filePath, basePath) {
        const content = fs.readFileSync(filePath, "utf-8");
        const relativePath = path.relative(basePath, filePath).replace(/\\/g, "/");
        const componentName = this.getComponentName(relativePath);

        // Pattern 1: style: { ... } objects
        this.scanStyleObjects(content, relativePath, componentName);

        // Pattern 2: .style.cssText = `...` or "..."
        this.scanCssTextAssignments(content, relativePath, componentName);

        // Pattern 3: .style.property = "value" (static)
        // Pattern 4: .style.property = variable (dynamic)
        this.scanStylePropertyAssignments(content, relativePath, componentName);
    }

    scanStyleObjects(content, relativePath, componentName) {
        const styleObjectPattern = /style\s*:\s*\{([^}]+)\}/g;
        let match;

        while ((match = styleObjectPattern.exec(content)) !== null) {
            const styleContent = match[1];
            const lineNumber = this.getLineNumber(content, match.index);
            const styles = this.parseStyleObject(styleContent);

            if (Object.keys(styles).length > 0) {
                this.addFinding({
                    file: relativePath,
                    line: lineNumber,
                    type: "style-object",
                    componentName,
                    styles,
                    raw: match[0],
                    isDynamic: false,
                });
            }
        }
    }

    scanCssTextAssignments(content, relativePath, componentName) {
        // Match .style.cssText = `...` or "..." or '...'
        const cssTextPattern = /\.style\.cssText\s*=\s*(`[^`]*`|"[^"]*"|'[^']*')/gs;
        let match;

        while ((match = cssTextPattern.exec(content)) !== null) {
            const cssString = match[1].slice(1, -1); // Remove quotes
            const lineNumber = this.getLineNumber(content, match.index);
            const hasDynamic = DYNAMIC_INDICATORS.some((p) => p.test(cssString));

            if (hasDynamic) {
                // Has interpolation - flag for manual review
                this.dynamicFindings.push({
                    file: relativePath,
                    line: lineNumber,
                    type: "cssText-dynamic",
                    componentName,
                    raw: match[0].substring(0, 200),
                    reason: "Template interpolation detected",
                    suggestedModule: "tokens/inlined/",
                });
            } else {
                const styles = this.parseCssText(cssString);
                if (Object.keys(styles).length > 0) {
                    this.addFinding({
                        file: relativePath,
                        line: lineNumber,
                        type: "cssText-static",
                        componentName,
                        styles,
                        raw: match[0],
                        isDynamic: false,
                    });
                }
            }
        }
    }

    scanStylePropertyAssignments(content, relativePath, componentName) {
        // Match .style.property = value
        const styleAssignPattern = /\.style\.([a-zA-Z]+)\s*=\s*([^;,\n]+)/g;
        let match;

        while ((match = styleAssignPattern.exec(content)) !== null) {
            const property = this.camelToKebab(match[1]);
            const valueExpr = match[2].trim();
            const lineNumber = this.getLineNumber(content, match.index);

            // Check if it's a static literal or dynamic
            const isStaticLiteral = /^["'`][^"'`]*["'`]$/.test(valueExpr);
            const hasDynamic = DYNAMIC_INDICATORS.some((p) => p.test(valueExpr));

            if (isStaticLiteral && !hasDynamic) {
                // Static value - extract
                const value = valueExpr.slice(1, -1);
                this.addFinding({
                    file: relativePath,
                    line: lineNumber,
                    type: "style-assignment",
                    componentName,
                    styles: { [property]: value },
                    raw: match[0],
                    isDynamic: false,
                });
            } else {
                // Dynamic value - flag for tokens/inlined/
                const suggestedModule = this.suggestInlinedModule(property);
                this.dynamicFindings.push({
                    file: relativePath,
                    line: lineNumber,
                    type: "style-dynamic",
                    componentName,
                    property,
                    valueExpr: valueExpr.substring(0, 100),
                    raw: match[0],
                    reason: hasDynamic ? "Dynamic value expression" : "Variable reference",
                    suggestedModule,
                    suggestedFunction: this.suggestInlinedFunction(property),
                });
            }
        }
    }

    suggestInlinedModule(property) {
        const prop = property.toLowerCase();
        if (prop.includes("left") || prop.includes("top") || prop.includes("right") || prop.includes("bottom") || prop.includes("transform")) {
            return "position.ts";
        }
        if (prop.includes("width") || prop.includes("height")) {
            return "dimensions.ts";
        }
        if (prop.includes("color") || prop.includes("background") || prop.includes("border-color") || prop.includes("fill") || prop.includes("stroke")) {
            return "colors.ts";
        }
        if (prop.includes("display") || prop.includes("visibility") || prop.includes("opacity") || prop.includes("clip")) {
            return "visibility.ts";
        }
        if (prop.includes("z-index")) {
            return "z-index.ts";
        }
        if (prop.includes("animation") || prop.includes("transition")) {
            return "animation.ts";
        }
        return "position.ts"; // Default
    }

    suggestInlinedFunction(property) {
        const prop = property.toLowerCase();
        const suggestions = {
            left: "setPositionX",
            top: "setPositionY",
            transform: "setTranslate",
            width: "setWidth",
            height: "setHeight",
            "min-width": "setMinWidth",
            "min-height": "setMinHeight",
            "max-width": "setMaxWidth",
            "max-height": "setMaxHeight",
            color: "setTextColor",
            "background-color": "setBackgroundColor",
            background: "setBackgroundColor",
            "border-color": "setBorderColor",
            display: "setDisplay",
            opacity: "setOpacity",
            visibility: "setVisibility",
            "clip-path": "setClipPath",
            "z-index": "setZIndex",
            "animation-delay": "setAnimationDelay",
            "animation-duration": "setAnimationDuration",
        };
        return suggestions[prop] || `setDynamic${this.kebabToPascal(prop)}`;
    }

    parseStyleObject(content) {
        const styles = {};
        const propPattern = /([a-zA-Z]+)\s*:\s*["'`]([^"'`]+)["'`]/g;
        let match;

        while ((match = propPattern.exec(content)) !== null) {
            const property = this.camelToKebab(match[1]);
            const value = match[2];
            styles[property] = value;
        }

        return styles;
    }

    parseCssText(cssString) {
        const styles = {};
        const parts = cssString.split(";").filter((p) => p.trim());

        for (const part of parts) {
            const colonIdx = part.indexOf(":");
            if (colonIdx > 0) {
                const prop = part.substring(0, colonIdx).trim();
                const val = part.substring(colonIdx + 1).trim();
                if (prop && val) {
                    styles[prop] = val;
                }
            }
        }

        return styles;
    }

    camelToKebab(str) {
        return str.replace(/([A-Z])/g, "-$1").toLowerCase();
    }

    kebabToPascal(str) {
        return str
            .split("-")
            .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
            .join("");
    }

    getLineNumber(content, index) {
        return content.substring(0, index).split("\n").length;
    }

    getComponentName(relativePath) {
        const baseName = path.basename(relativePath, path.extname(relativePath));
        return baseName;
    }

    addFinding(finding) {
        const tokenizedStyles = {};
        const anomalies = [];

        for (const [prop, value] of Object.entries(finding.styles)) {
            if (value.includes("var(")) {
                tokenizedStyles[prop] = value;
                continue;
            }

            const categories = PROPERTY_CATEGORIES[prop];
            if (categories) {
                const closest = this.registry.findClosest(value, categories);
                if (closest) {
                    tokenizedStyles[prop] = `var(${closest.name})`;
                } else {
                    tokenizedStyles[prop] = this.convertToRem(value);
                    anomalies.push({ prop, value });
                }
            } else {
                tokenizedStyles[prop] = this.convertToRem(value);
            }
        }

        finding.tokenizedStyles = tokenizedStyles;
        finding.anomalies = anomalies;
        finding.className = this.generateClassName(finding.componentName, finding.styles);

        this.staticFindings.push(finding);

        if (!this.extractedByComponent.has(finding.componentName)) {
            this.extractedByComponent.set(finding.componentName, []);
        }
        this.extractedByComponent.get(finding.componentName).push(finding);
    }

    generateClassName(componentName, styles) {
        const purpose = this.inferPurpose(styles);
        return `.${componentName}__${purpose}`;
    }

    inferPurpose(styles) {
        const props = Object.keys(styles);

        if (props.some((p) => p.includes("flex") || p.includes("grid"))) return "layout";
        if (props.some((p) => p.includes("padding"))) return "container";
        if (props.some((p) => p.includes("margin"))) return "spacing";
        if (props.some((p) => p.includes("font"))) return "text";
        if (props.some((p) => p.includes("color") || p.includes("background"))) return "themed";
        if (props.some((p) => p.includes("border"))) return "bordered";
        if (props.some((p) => p.includes("width") || p.includes("height"))) return "sized";

        this.classCounter++;
        return `style-${this.classCounter}`;
    }

    convertToRem(value) {
        const pxMatch = value.match(/^(\d+(?:\.\d+)?)px$/);
        if (pxMatch) {
            const px = parseFloat(pxMatch[1]);
            const rem = px / BASE_FONT_SIZE;
            return `${rem}rem`;
        }
        return value;
    }

    generateCSSFiles(outputDir) {
        if (!fs.existsSync(outputDir)) {
            fs.mkdirSync(outputDir, { recursive: true });
        }

        const generatedFiles = [];
        let totalNew = 0;
        let totalExisting = 0;

        for (const [componentName, findings] of this.extractedByComponent) {
            const cssFileName = `${componentName}-component.css`;
            const cssFilePath = path.join(outputDir, cssFileName);

            // Parse existing file
            const { rules: existingRules } = ExistingCSSParser.parseFile(cssFilePath);
            totalExisting += existingRules.size;

            // Generate new rules
            const newRules = new Map();
            for (const finding of findings) {
                const signature = Object.entries(finding.tokenizedStyles)
                    .sort(([a], [b]) => a.localeCompare(b))
                    .map(([k, v]) => `${k}: ${v}`)
                    .join("; ");

                if (!newRules.has(signature)) {
                    newRules.set(signature, {
                        selector: finding.className,
                        styles: finding.tokenizedStyles,
                        sources: [],
                    });
                }
                newRules.get(signature).sources.push(`${finding.file}:${finding.line}`);
            }

            // Merge
            const { added } = ExistingCSSParser.mergeRules(existingRules, newRules);
            totalNew += added.length;

            // Write merged content
            const cssContent = this.generateCSSContent(componentName, findings, existingRules);
            fs.writeFileSync(cssFilePath, cssContent);

            generatedFiles.push({
                component: componentName,
                cssFile: cssFileName,
                existingRules: existingRules.size,
                newRules: added.length,
                totalRules: findings.length,
            });

            const status = existingRules.size > 0 ? `(+${added.length} new)` : "(created)";
            console.log(`  ${cssFileName}: ${findings.length} rules ${status}`);
        }

        console.log(`\nMerge summary: ${totalNew} new rules, ${totalExisting} existing preserved`);

        return generatedFiles;
    }

    generateCSSContent(componentName, findings, existingRules) {
        const lines = [
            `/**`,
            ` * ${this.kebabToPascal(componentName)} Component Styles`,
            ` *`,
            ` * Auto-generated from inline styles extraction.`,
            ` * All values use design tokens where possible.`,
            ` */`,
            ``,
        ];

        // Collect unique rules (new + existing)
        const allRules = new Map();

        // Add existing rules first
        for (const [signature, rule] of existingRules) {
            allRules.set(signature, rule);
        }

        // Add new rules
        for (const finding of findings) {
            const signature = Object.entries(finding.tokenizedStyles)
                .sort(([a], [b]) => a.localeCompare(b))
                .map(([k, v]) => `${k}: ${v}`)
                .join("; ");

            if (!allRules.has(signature)) {
                allRules.set(signature, {
                    selector: finding.className,
                    styles: finding.tokenizedStyles,
                    sources: [`${finding.file}:${finding.line}`],
                });
            }
        }

        // Output rules
        for (const [, rule] of allRules) {
            if (rule.sources) {
                lines.push(`/* Source: ${rule.sources.join(", ")} */`);
            } else if (rule.comment) {
                lines.push(rule.comment.trim());
            }

            lines.push(`${rule.selector} {`);

            const styles = rule.styles || {};
            for (const [prop, value] of Object.entries(styles)) {
                lines.push(`    ${prop}: ${value};`);
            }

            // Handle existing body format
            if (rule.body && !rule.styles) {
                lines.push(`    ${rule.body}`);
            }

            lines.push(`}`);
            lines.push(``);
        }

        return lines.join("\n");
    }

    generateMigrationReport(outputPath) {
        const report = {
            generatedAt: new Date().toISOString(),
            summary: {
                staticFindings: this.staticFindings.length,
                dynamicFindings: this.dynamicFindings.length,
                componentsAffected: this.extractedByComponent.size,
                anomaliesCount: this.staticFindings.reduce((sum, f) => sum + f.anomalies.length, 0),
            },
            dynamicByModule: {},
            dynamicFindings: this.dynamicFindings,
            byComponent: {},
            anomalies: [],
        };

        // Group dynamic findings by suggested module
        for (const finding of this.dynamicFindings) {
            const module = finding.suggestedModule || "unknown";
            if (!report.dynamicByModule[module]) {
                report.dynamicByModule[module] = [];
            }
            report.dynamicByModule[module].push({
                file: finding.file,
                line: finding.line,
                property: finding.property,
                suggestedFunction: finding.suggestedFunction,
            });
        }

        // By component
        for (const [componentName, findings] of this.extractedByComponent) {
            report.byComponent[componentName] = {
                staticRules: findings.length,
                dynamicRules: this.dynamicFindings.filter((f) => f.componentName === componentName).length,
                generatedCSS: `${componentName}-component.css`,
            };
        }

        // Anomalies
        for (const finding of this.staticFindings) {
            for (const anomaly of finding.anomalies) {
                report.anomalies.push({
                    file: finding.file,
                    line: finding.line,
                    property: anomaly.prop,
                    value: anomaly.value,
                });
            }
        }

        fs.writeFileSync(outputPath, JSON.stringify(report, null, 2));
        return report;
    }
}

// =============================================================================
// Main
// =============================================================================

function main() {
    console.log("\n" + "=".repeat(80));
    console.log("INLINE CSS EXTRACTOR v2 (APPEND MODE)");
    console.log("=".repeat(80));
    console.log(`\nSource: ${SRC_DIR}`);
    console.log(`Tokens: ${TOKENS_DIR}`);
    console.log(`Output: ${COMPONENTS_CSS_DIR}`);
    console.log("");

    // Load tokens
    const registry = new TokenRegistry();
    registry.loadFromDirectory(TOKENS_DIR);

    // Scan and extract
    const extractor = new InlineCSSExtractor(registry);
    const { static: staticFindings, dynamic: dynamicFindings } = extractor.scan(SRC_DIR);

    console.log(`\nFound:`);
    console.log(`  Static (extractable):  ${staticFindings.length}`);
    console.log(`  Dynamic (tokens/inlined): ${dynamicFindings.length}`);
    console.log(`  Components affected: ${extractor.extractedByComponent.size}\n`);

    // Generate CSS files (append mode)
    console.log("Generating/Updating CSS files:");
    extractor.generateCSSFiles(COMPONENTS_CSS_DIR);

    // Generate migration report
    const reportPath = path.join(REPORTS_DIR, "inline-css-extraction.json");
    const report = extractor.generateMigrationReport(reportPath);

    // Summary
    console.log("\n" + "=".repeat(80));
    console.log("DYNAMIC STYLES (need tokens/inlined/ migration)");
    console.log("=".repeat(80));

    for (const [module, findings] of Object.entries(report.dynamicByModule)) {
        console.log(`\n📦 ${module}: ${findings.length} occurrences`);
        findings.slice(0, 5).forEach((f) => {
            console.log(`   - ${f.file}:${f.line} → ${f.suggestedFunction}()`);
        });
        if (findings.length > 5) {
            console.log(`   ... and ${findings.length - 5} more`);
        }
    }

    console.log("\n" + "=".repeat(80));
    console.log("SUMMARY");
    console.log("=".repeat(80));
    console.log(`\nStatic CSS extracted: ${staticFindings.length}`);
    console.log(`Dynamic (need manual): ${dynamicFindings.length}`);
    console.log(`Anomalies (new tokens): ${report.summary.anomaliesCount}`);

    console.log(`\n✅ Extraction complete!`);
    console.log(`\nNext steps:`);
    console.log(`  1. Static styles → already in CSS files`);
    console.log(`  2. Dynamic styles → migrate using tokens/inlined/ functions`);
    console.log(`  3. See report: ${reportPath}`);
}

main();
