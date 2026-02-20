/**
 * Generate Tokenization Summary Report
 */

const fs = require("fs");
const path = require("path");

const reportPath = path.join(__dirname, "css-tokenization-report.json");
const report = JSON.parse(fs.readFileSync(reportPath, "utf-8"));

console.log("═".repeat(80));
console.log("CSS TOKENIZATION ANALYSIS - COMPREHENSIVE SUMMARY");
console.log("═".repeat(80));

console.log("\n## SCAN STATISTICS\n");
console.log(`Files Scanned: ${report.stats.filesScanned}`);
console.log(`Inline Styles Found: ${report.stats.inlineStylesFound}`);
console.log(`Hardcoded Values: ${report.stats.hardcodedValuesFound}`);
console.log(`SVG Attributes: ${report.stats.svgAttributesFound}`);
console.log(`Total Token Opportunities: ${report.stats.totalTokenOpportunities}`);

console.log("\n## PRIORITY BREAKDOWN\n");
console.log(`🔴 HIGH Priority:   ${report.summary.high} (requires immediate attention)`);
console.log(`🟡 MEDIUM Priority: ${report.summary.medium} (should be addressed)`);
console.log(`🟢 LOW Priority:    ${report.summary.low} (nice to have)`);

console.log("\n═".repeat(80));
console.log("SUGGESTED TOKEN FILES WITH UNIQUE VALUES");
console.log("═".repeat(80));

// Process each token file
const tokenFileSummary = {};

for (const [file, findings] of Object.entries(report.byTokenFile)) {
    const unique = new Map();

    // Get unique property:value combinations with highest frequency
    for (const finding of findings) {
        const key = `${finding.property}:${finding.value}`;
        if (!unique.has(key) || unique.get(key).frequency < finding.frequency) {
            unique.set(key, finding);
        }
    }

    tokenFileSummary[file] = {
        totalFindings: findings.length,
        uniqueValues: unique.size,
        values: Array.from(unique.values())
            .sort((a, b) => b.frequency - a.frequency)
            .slice(0, 20), // Top 20 per file
    };
}

// Display each token file
for (const [file, summary] of Object.entries(tokenFileSummary).sort((a, b) => b[1].uniqueValues - a[1].uniqueValues)) {
    console.log(`\n### 📄 ${file}`);
    console.log(`   Total Occurrences: ${summary.totalFindings}`);
    console.log(`   Unique Values: ${summary.uniqueValues}`);
    console.log(`   Priority Distribution: ${summary.values.filter(v => v.priority === 'high').length} high, ${summary.values.filter(v => v.priority === 'medium').length} medium, ${summary.values.filter(v => v.priority === 'low').length} low`);

    console.log("\n   Top Values:");
    for (const value of summary.values.slice(0, 10)) {
        console.log(`   - ${value.suggestedVar}`);
        console.log(`     ${value.property}: ${value.value}`);
        console.log(`     Used ${value.frequency}x (${value.priority} priority)`);
    }
}

console.log("\n═".repeat(80));
console.log("FILES WITH MOST TOKENIZATION OPPORTUNITIES");
console.log("═".repeat(80));

// Group by source file and count
const sourceFileCounts = {};
for (const [file, findings] of Object.entries(report.bySourceFile)) {
    sourceFileCounts[file] = findings.length;
}

const topFiles = Object.entries(sourceFileCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 15);

for (const [file, count] of topFiles) {
    const shortPath = file.split("renderer/")[1] || file;
    console.log(`\n${shortPath}`);
    console.log(`  ${count} tokenization opportunities`);
}

console.log("\n═".repeat(80));
console.log("SPECIFIC EXAMPLES BY CATEGORY");
console.log("═".repeat(80));

// Show concrete examples for each category
const categories = {
    "icon-size": [],
    "svg-color": [],
    "font-size": [],
    "spacing": [],
    "border-radius": [],
    "transition": [],
};

for (const finding of report.findings) {
    if (categories[finding.category]) {
        categories[finding.category].push(finding);
    }
}

for (const [category, findings] of Object.entries(categories)) {
    if (findings.length === 0) continue;

    console.log(`\n### ${category.toUpperCase()}`);
    const unique = new Map();
    for (const f of findings) {
        const key = `${f.property}:${f.value}`;
        if (!unique.has(key)) {
            unique.set(key, f);
        }
    }

    const examples = Array.from(unique.values()).slice(0, 5);
    for (const ex of examples) {
        const shortPath = ex.filePath.split("renderer/")[1] || ex.filePath;
        console.log(`   ${ex.suggestedVar}: ${ex.value}`);
        console.log(`   Found in: ${shortPath}:${ex.lineNumber}`);
    }
}

console.log("\n═".repeat(80));
console.log("RECOMMENDED ACTION PLAN");
console.log("═".repeat(80));

console.log(`
1. Create new token files (highest priority first):
   - svg.css (${tokenFileSummary["svg.css"]?.uniqueValues || 0} unique values)
   - icons.css (${tokenFileSummary["icons.css"]?.uniqueValues || 0} unique values)
   - Extend typography.css (+${tokenFileSummary["typography.css"]?.uniqueValues || 0} values)
   - Extend layout.css (+${tokenFileSummary["layout.css"]?.uniqueValues || 0} values)
   - Extend borders.css (+${tokenFileSummary["borders.css"]?.uniqueValues || 0} values)

2. Focus on HIGH priority values (used 5+ times):
   - ${report.summary.high} opportunities for immediate impact

3. Target files with most inline styles:
   - terminal-sidebar.ts
   - ai-metrics-panel.ts
   - claude-form-builder.ts

4. Extraction strategy:
   a) Create token CSS files
   b) Define CSS variables
   c) Replace inline values with var(--token-name)
   d) Update existing CSS files to use tokens
   e) Add documentation for token usage

Total potential code reduction: ~${Math.round(report.stats.totalTokenOpportunities * 0.7)} lines
Maintenance improvement: Centralized theme/style control
`);

console.log("═".repeat(80));
