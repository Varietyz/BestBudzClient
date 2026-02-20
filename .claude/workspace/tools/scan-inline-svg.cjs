/**
 * Inline SVG Scanner
 *
 * Scans the codebase for inline SVG definitions and identifies:
 * 1. Duplicate SVGs that could use centralized icons
 * 2. Unique SVGs that need to be added to the icon registry
 * 3. SVGs with inline styles that need abstraction
 *
 * Usage: node scan-inline-svg.cjs
 */

const fs = require("fs");
const path = require("path");

const ROOT = path.resolve(__dirname, "../../../root/archlab-ide/src/renderer");
const ICONS_FILE = path.join(ROOT, "components/icons.ts");

// =============================================================================
// SVG Pattern Detection
// =============================================================================

/**
 * Extract path data from SVG (the unique identifier)
 */
function extractPathData(svg) {
    const paths = [];

    // Extract <path d="...">
    const pathMatches = svg.matchAll(/d="([^"]+)"/g);
    for (const match of pathMatches) {
        paths.push(match[1]);
    }

    // Extract <circle>
    const circleMatches = svg.matchAll(/<circle[^>]+>/g);
    for (const match of circleMatches) {
        paths.push(match[0]);
    }

    // Extract <rect>
    const rectMatches = svg.matchAll(/<rect[^>]+>/g);
    for (const match of rectMatches) {
        paths.push(match[0]);
    }

    // Extract <line>
    const lineMatches = svg.matchAll(/<line[^>]+>/g);
    for (const match of lineMatches) {
        paths.push(match[0]);
    }

    // Extract <polyline>
    const polylineMatches = svg.matchAll(/<polyline[^>]+>/g);
    for (const match of polylineMatches) {
        paths.push(match[0]);
    }

    // Extract <polygon>
    const polygonMatches = svg.matchAll(/<polygon[^>]+>/g);
    for (const match of polygonMatches) {
        paths.push(match[0]);
    }

    return paths.sort().join("|");
}

/**
 * Normalize SVG for comparison (remove whitespace, quotes, etc.)
 */
function normalizeSvg(svg) {
    return svg
        .replace(/\s+/g, " ")
        .replace(/>\s+</g, "><")
        .replace(/'/g, '"')
        .trim();
}

/**
 * Extract SVG attributes
 */
function extractSvgAttributes(svg) {
    const attrs = {};

    // viewBox
    const viewBoxMatch = svg.match(/viewBox="([^"]+)"/);
    if (viewBoxMatch) attrs.viewBox = viewBoxMatch[1];

    // width/height
    const widthMatch = svg.match(/width="([^"]+)"/);
    if (widthMatch) attrs.width = widthMatch[1];

    const heightMatch = svg.match(/height="([^"]+)"/);
    if (heightMatch) attrs.height = heightMatch[1];

    // fill
    const fillMatch = svg.match(/fill="([^"]+)"/);
    if (fillMatch) attrs.fill = fillMatch[1];

    // stroke
    const strokeMatch = svg.match(/stroke="([^"]+)"/);
    if (strokeMatch) attrs.stroke = strokeMatch[1];

    // stroke-width
    const strokeWidthMatch = svg.match(/stroke-width="([^"]+)"/);
    if (strokeWidthMatch) attrs.strokeWidth = strokeWidthMatch[1];

    // Inline style
    const styleMatch = svg.match(/style="([^"]+)"/);
    if (styleMatch) attrs.inlineStyle = styleMatch[1];

    return attrs;
}

// =============================================================================
// File Scanning
// =============================================================================

/**
 * Scan a file for inline SVG patterns
 */
function scanFile(filePath) {
    const content = fs.readFileSync(filePath, "utf-8");
    const lines = content.split("\n");
    const findings = [];

    // Pattern: <svg ...>...</svg>
    // Handle both single-line and template literals
    const svgPattern = /<svg[^>]*>[\s\S]*?<\/svg>/g;

    let match;
    while ((match = svgPattern.exec(content)) !== null) {
        const svg = match[0];
        const lineNumber = content.substring(0, match.index).split("\n").length;

        // Skip if it's in the icons.ts file itself
        if (filePath.endsWith("icons.ts") && !filePath.includes("test")) {
            continue;
        }

        const pathData = extractPathData(svg);
        const attrs = extractSvgAttributes(svg);

        findings.push({
            file: path.relative(ROOT, filePath),
            line: lineNumber,
            svg: normalizeSvg(svg),
            pathData,
            attrs,
            hasInlineStyle: !!attrs.inlineStyle,
            hasHardcodedColor: attrs.stroke && attrs.stroke !== "currentColor",
        });
    }

    return findings;
}

/**
 * Load existing icons from icons.ts
 */
function loadExistingIcons() {
    const content = fs.readFileSync(ICONS_FILE, "utf-8");
    const icons = {};

    // Extract icon definitions
    const iconPattern = /(\w+(?:-\w+)*):\s*`([^`]+)`/g;
    let match;
    while ((match = iconPattern.exec(content)) !== null) {
        const name = match[1];
        const svg = match[2];
        icons[name] = {
            svg: normalizeSvg(svg),
            pathData: extractPathData(svg),
        };
    }

    return icons;
}

/**
 * Find which existing icon matches this SVG
 */
function findMatchingIcon(pathData, existingIcons) {
    for (const [name, icon] of Object.entries(existingIcons)) {
        if (icon.pathData === pathData) {
            return name;
        }
    }
    return null;
}

// =============================================================================
// Main Analysis
// =============================================================================

function main() {
    console.log("SVG Inline Pattern Scanner");
    console.log("=".repeat(60));

    // Load existing icons
    const existingIcons = loadExistingIcons();
    console.log(`\nLoaded ${Object.keys(existingIcons).length} icons from icons.ts\n`);

    // Find all TypeScript files
    const files = [];
    function walkDir(dir) {
        const entries = fs.readdirSync(dir, { withFileTypes: true });
        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);
            if (entry.isDirectory() && !entry.name.startsWith(".")) {
                walkDir(fullPath);
            } else if (entry.isFile() && entry.name.endsWith(".ts")) {
                files.push(fullPath);
            }
        }
    }
    walkDir(ROOT);

    // Scan all files
    const allFindings = [];
    for (const file of files) {
        const findings = scanFile(file);
        allFindings.push(...findings);
    }

    console.log(`Found ${allFindings.length} inline SVG patterns across ${files.length} files\n`);

    // Categorize findings
    const duplicates = [];
    const unique = [];
    const withInlineStyles = [];
    const withHardcodedColors = [];

    // Track unique path patterns
    const pathPatterns = new Map();

    for (const finding of allFindings) {
        // Check if matches existing icon
        const matchingIcon = findMatchingIcon(finding.pathData, existingIcons);
        if (matchingIcon) {
            duplicates.push({ ...finding, matchingIcon });
        } else {
            // Track unique patterns
            if (!pathPatterns.has(finding.pathData)) {
                pathPatterns.set(finding.pathData, []);
            }
            pathPatterns.get(finding.pathData).push(finding);
            unique.push(finding);
        }

        if (finding.hasInlineStyle) {
            withInlineStyles.push(finding);
        }

        if (finding.hasHardcodedColor) {
            withHardcodedColors.push(finding);
        }
    }

    // Report duplicates (could use existing icons)
    console.log("=".repeat(60));
    console.log("DUPLICATES (use existing icons from icons.ts)");
    console.log("=".repeat(60));
    for (const dup of duplicates) {
        console.log(`  ${dup.file}:${dup.line} → use getIconSvg("${dup.matchingIcon}")`);
    }
    console.log(`\nTotal: ${duplicates.length} duplicates\n`);

    // Report unique patterns that appear multiple times
    console.log("=".repeat(60));
    console.log("REPEATED UNIQUE PATTERNS (add to icons.ts)");
    console.log("=".repeat(60));
    const repeatedPatterns = [];
    for (const [pathData, findings] of pathPatterns.entries()) {
        if (findings.length > 1) {
            repeatedPatterns.push({ pathData, findings, count: findings.length });
        }
    }
    repeatedPatterns.sort((a, b) => b.count - a.count);

    for (const pattern of repeatedPatterns.slice(0, 20)) {
        console.log(`\n  Pattern appears ${pattern.count} times:`);
        for (const f of pattern.findings.slice(0, 3)) {
            console.log(`    ${f.file}:${f.line}`);
        }
        if (pattern.findings.length > 3) {
            console.log(`    ... and ${pattern.findings.length - 3} more`);
        }
    }

    // Report inline styles
    console.log("\n" + "=".repeat(60));
    console.log("INLINE STYLES IN SVGs (need abstraction)");
    console.log("=".repeat(60));
    for (const finding of withInlineStyles) {
        console.log(`  ${finding.file}:${finding.line}`);
        console.log(`    style="${finding.attrs.inlineStyle}"`);
    }
    console.log(`\nTotal: ${withInlineStyles.length} with inline styles\n`);

    // Report hardcoded colors
    console.log("=".repeat(60));
    console.log("HARDCODED COLORS (use currentColor or CSS vars)");
    console.log("=".repeat(60));
    for (const finding of withHardcodedColors) {
        console.log(`  ${finding.file}:${finding.line}`);
        console.log(`    stroke="${finding.attrs.stroke}"`);
    }
    console.log(`\nTotal: ${withHardcodedColors.length} with hardcoded colors\n`);

    // Summary
    console.log("=".repeat(60));
    console.log("SUMMARY");
    console.log("=".repeat(60));
    console.log(`Total inline SVGs: ${allFindings.length}`);
    console.log(`Duplicates (use existing icons): ${duplicates.length}`);
    console.log(`Unique patterns: ${unique.length}`);
    console.log(`  - Repeated patterns to add to icons.ts: ${repeatedPatterns.length}`);
    console.log(`With inline styles: ${withInlineStyles.length}`);
    console.log(`With hardcoded colors: ${withHardcodedColors.length}`);

    // Save detailed report
    const report = {
        generatedAt: new Date().toISOString(),
        summary: {
            total: allFindings.length,
            duplicates: duplicates.length,
            unique: unique.length,
            repeatedPatterns: repeatedPatterns.length,
            withInlineStyles: withInlineStyles.length,
            withHardcodedColors: withHardcodedColors.length,
        },
        duplicates: duplicates.map(d => ({
            file: d.file,
            line: d.line,
            matchingIcon: d.matchingIcon,
        })),
        repeatedPatterns: repeatedPatterns.map(p => ({
            count: p.count,
            locations: p.findings.map(f => ({ file: f.file, line: f.line })),
            suggestedName: suggestIconName(p.findings[0]),
            sampleSvg: p.findings[0].svg.substring(0, 200) + "...",
        })),
        withInlineStyles,
        withHardcodedColors: withHardcodedColors.map(f => ({
            file: f.file,
            line: f.line,
            color: f.attrs.stroke,
        })),
    };

    const outputPath = path.resolve(__dirname, "../scanners/reports/svg-analysis.json");
    fs.writeFileSync(outputPath, JSON.stringify(report, null, 2));
    console.log(`\nDetailed report saved to: ${outputPath}`);
}

/**
 * Suggest an icon name based on the SVG content
 */
function suggestIconName(finding) {
    const svg = finding.svg.toLowerCase();

    if (svg.includes("polygon") && svg.includes("points")) {
        if (svg.includes("5 3 19 12 5 21")) return "play";
        if (svg.includes("triangle")) return "triangle";
    }
    if (svg.includes("circle") && svg.includes("r=\"10\"")) return "circle-lg";
    if (svg.includes("path") && svg.includes("trash")) return "trash";
    if (svg.includes("check") || svg.includes("9 12 2 2 4-4")) return "check-circle";
    if (svg.includes("x") || svg.includes("18 6 6 18")) return "x-circle";
    if (svg.includes("spin")) return "spinner";

    return "icon-" + Date.now().toString(36);
}

main();
