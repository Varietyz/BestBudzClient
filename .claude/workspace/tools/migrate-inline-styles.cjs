/**
 * Inline Style Migration Helper
 *
 * Generates migration suggestions for dynamic inline styles to use tokens/inlined/ functions.
 * Part of the zero-inline-styles initiative.
 *
 * Usage: node migrate-inline-styles.cjs [--file=<pattern>] [--apply]
 */

const fs = require("fs");
const path = require("path");

const ROOT = path.resolve(__dirname, "../../../root/archlab-ide/src/renderer");
const REPORT_PATH = path.resolve(__dirname, "../scanners/reports/inline-css-extraction.json");

// =============================================================================
// Migration Patterns
// =============================================================================

const MIGRATION_MAP = {
    // Color properties -> colors.ts
    color: { module: "colors", fn: "setTextColor", args: ["element", "value"] },
    backgroundColor: { module: "colors", fn: "setBackgroundColor", args: ["element", "value"] },
    borderColor: { module: "colors", fn: "setBorderColor", args: ["element", "value"] },
    borderTopColor: { module: "colors", fn: "setBorderSideColor", args: ["element", "'top'", "value"] },
    borderRightColor: { module: "colors", fn: "setBorderSideColor", args: ["element", "'right'", "value"] },
    borderBottomColor: { module: "colors", fn: "setBorderSideColor", args: ["element", "'bottom'", "value"] },
    borderLeftColor: { module: "colors", fn: "setBorderSideColor", args: ["element", "'left'", "value"] },
    fill: { module: "colors", fn: "setFillColor", args: ["element", "value"] },
    stroke: { module: "colors", fn: "setStrokeColor", args: ["element", "value"] },

    // Position properties -> position.ts
    left: { module: "position", fn: "setPositionX", args: ["element", "value"] },
    right: { module: "position", fn: "setPositionX", args: ["element", "value"], note: "// Note: use negative for right positioning" },
    top: { module: "position", fn: "setPositionY", args: ["element", "value"] },
    bottom: { module: "position", fn: "setPositionY", args: ["element", "value"], note: "// Note: use negative for bottom positioning" },
    transform: { module: "position", fn: "setTransform", args: ["element", "value"] },

    // Dimension properties -> dimensions.ts
    width: { module: "dimensions", fn: "setWidth", args: ["element", "value"] },
    height: { module: "dimensions", fn: "setHeight", args: ["element", "value"] },
    minWidth: { module: "dimensions", fn: "setMinWidth", args: ["element", "value"] },
    minHeight: { module: "dimensions", fn: "setMinHeight", args: ["element", "value"] },
    maxWidth: { module: "dimensions", fn: "setMaxWidth", args: ["element", "value"] },
    maxHeight: { module: "dimensions", fn: "setMaxHeight", args: ["element", "value"] },

    // Visibility properties -> visibility.ts
    display: { module: "visibility", fn: "setDisplay", args: ["element", "value"] },
    opacity: { module: "visibility", fn: "setOpacity", args: ["element", "value"] },
    visibility: { module: "visibility", fn: "setVisibility", args: ["element", "value === 'visible'"] },
    clipPath: { module: "visibility", fn: "setClipPath", args: ["element", "value"] },

    // Z-index -> z-index.ts
    zIndex: { module: "z-index", fn: "setZIndex", args: ["element", "value"] },

    // Animation -> animation.ts
    animationDelay: { module: "animation", fn: "setAnimationDelay", args: ["element", "value"] },
    animationDuration: { module: "animation", fn: "setAnimationDuration", args: ["element", "value"] },
    transitionDelay: { module: "animation", fn: "setTransitionDelay", args: ["element", "value"] },
    transitionDuration: { module: "animation", fn: "setTransitionDuration", args: ["element", "value"] },
    transition: { module: "animation", fn: "setTransitionDuration", args: ["element", "value"], note: "// Manual: parse transition string" },
};

// =============================================================================
// Analysis Functions
// =============================================================================

/**
 * Parse the extraction report and group by file
 */
function loadReport() {
    if (!fs.existsSync(REPORT_PATH)) {
        console.error("Error: Extraction report not found at", REPORT_PATH);
        console.error("Run extract-inline-css.cjs first.");
        process.exit(1);
    }
    return JSON.parse(fs.readFileSync(REPORT_PATH, "utf-8"));
}

/**
 * Analyze a TypeScript file for inline styles and generate migrations
 */
function analyzeFile(filePath) {
    const fullPath = path.join(ROOT, filePath);
    if (!fs.existsSync(fullPath)) {
        return null;
    }

    const content = fs.readFileSync(fullPath, "utf-8");
    const lines = content.split("\n");
    const migrations = [];
    const requiredImports = new Set();

    // Pattern: element.style.property = value
    const stylePattern = /(\w+)\.style\.(\w+)\s*=\s*(.+?);/g;

    lines.forEach((line, idx) => {
        let match;
        stylePattern.lastIndex = 0;

        while ((match = stylePattern.exec(line)) !== null) {
            const [fullMatch, element, property, value] = match;
            const migration = MIGRATION_MAP[property];

            if (migration) {
                // Check if dynamic (contains variable, ternary, or template literal)
                const isDynamic = /[`${}?:]|[a-zA-Z_]\w*(?!\s*\.)/.test(value) && !/^["'#]/.test(value.trim());

                if (isDynamic || value.includes("${") || value.includes("?")) {
                    requiredImports.add(migration.fn);

                    migrations.push({
                        line: idx + 1,
                        original: fullMatch,
                        property,
                        element,
                        value: value.trim(),
                        migration: {
                            module: migration.module,
                            fn: migration.fn,
                            replacement: `${migration.fn}(${element}, ${value.trim().replace(/;$/, "")})`,
                        },
                        note: migration.note,
                    });
                }
            }
        }

        // Pattern: element.style.cssText = `...${dynamic}...`
        if (line.includes(".style.cssText") && line.includes("${")) {
            migrations.push({
                line: idx + 1,
                original: line.trim(),
                property: "cssText",
                type: "complex",
                note: "// MANUAL: Split static styles to CSS class, dynamic to tokens/inlined",
            });
        }
    });

    return {
        file: filePath,
        migrations,
        requiredImports: Array.from(requiredImports),
    };
}

/**
 * Generate import statement
 */
function generateImportStatement(imports) {
    if (imports.length === 0) return null;
    return `import { ${imports.join(", ")} } from "@/styles/tokens/inlined";`;
}

/**
 * Generate migration report for a file
 */
function generateMigrationReport(analysis) {
    const lines = [];

    lines.push(`\n${"=".repeat(80)}`);
    lines.push(`FILE: ${analysis.file}`);
    lines.push(`${"=".repeat(80)}`);
    lines.push(`Dynamic patterns found: ${analysis.migrations.length}`);

    if (analysis.requiredImports.length > 0) {
        lines.push(`\nRequired import:`);
        lines.push(`  ${generateImportStatement(analysis.requiredImports)}`);
    }

    lines.push(`\nMigrations:`);

    for (const m of analysis.migrations) {
        lines.push(`\n  Line ${m.line}:`);
        lines.push(`    Before: ${m.original}`);
        if (m.migration) {
            lines.push(`    After:  ${m.migration.replacement};`);
        }
        if (m.note) {
            lines.push(`    ${m.note}`);
        }
    }

    return lines.join("\n");
}

// =============================================================================
// Main
// =============================================================================

function main() {
    const args = process.argv.slice(2);
    const filePattern = args.find(a => a.startsWith("--file="))?.replace("--file=", "") || null;
    const shouldApply = args.includes("--apply");

    console.log("Inline Style Migration Helper");
    console.log("=".repeat(40));

    const report = loadReport();

    // Collect all unique files from dynamic findings
    const filesToAnalyze = new Set();

    for (const [module, findings] of Object.entries(report.dynamicByModule || {})) {
        for (const finding of findings) {
            if (!filePattern || finding.file.includes(filePattern)) {
                filesToAnalyze.add(finding.file);
            }
        }
    }

    console.log(`\nAnalyzing ${filesToAnalyze.size} files with dynamic patterns...\n`);

    const allMigrations = [];
    let totalPatterns = 0;

    for (const file of filesToAnalyze) {
        const analysis = analyzeFile(file);
        if (analysis && analysis.migrations.length > 0) {
            allMigrations.push(analysis);
            totalPatterns += analysis.migrations.length;
            console.log(generateMigrationReport(analysis));
        }
    }

    // Summary
    console.log("\n" + "=".repeat(80));
    console.log("SUMMARY");
    console.log("=".repeat(80));
    console.log(`Files analyzed: ${filesToAnalyze.size}`);
    console.log(`Files with migrations: ${allMigrations.length}`);
    console.log(`Total dynamic patterns: ${totalPatterns}`);

    // Aggregate required imports
    const allImports = new Set();
    for (const analysis of allMigrations) {
        analysis.requiredImports.forEach(i => allImports.add(i));
    }

    console.log(`\nAll required imports (add to each file as needed):`);
    console.log(`  import { ${Array.from(allImports).join(", ")} } from "@/styles/tokens/inlined";`);

    // Save detailed report
    const outputPath = path.resolve(__dirname, "../scanners/reports/migration-suggestions.json");
    fs.writeFileSync(outputPath, JSON.stringify({
        generatedAt: new Date().toISOString(),
        summary: {
            filesAnalyzed: filesToAnalyze.size,
            filesWithMigrations: allMigrations.length,
            totalPatterns,
            requiredImports: Array.from(allImports),
        },
        migrations: allMigrations,
    }, null, 2));

    console.log(`\nDetailed report saved to: ${outputPath}`);

    if (shouldApply) {
        console.log("\n--apply flag detected. Auto-migration not yet implemented.");
        console.log("Please apply migrations manually using the suggestions above.");
    }
}

main();
