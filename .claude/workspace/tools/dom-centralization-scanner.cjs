#!/usr/bin/env node
/**
 * DOM Centralization Scanner
 *
 * Enhanced scanner that categorizes DOM bypasses by component type
 * and provides actionable migration guidance.
 *
 * Extends find-dom-bypasses.cjs with:
 * - Component type categorization (buttons, forms, modals, etc.)
 * - CSS token violation detection
 * - Migration priority scoring
 * - Dependency graph analysis
 *
 * Usage: node dom-centralization-scanner.cjs [--output=report.json]
 */

const fs = require('fs');
const path = require('path');

// =============================================================================
// Configuration
// =============================================================================

const BYPASS_REPORT = '.claude/workspace/tools/dom-bypasses.json';
const DEFAULT_OUTPUT = '.claude/workspace/tools/dom-centralization-report.json';

// Component categorization patterns
const COMPONENT_PATTERNS = {
    buttons: {
        patterns: [/btn/i, /button/i, /icon.*btn/i],
        innerHTML: ['btn.innerHTML', 'iconBtn.innerHTML', 'addBtn.innerHTML', 'deleteBtn.innerHTML'],
        priority: 1,
        factory_method: 'DOM.button() or DOM.iconButton()',
    },
    forms: {
        patterns: [/input/i, /field/i, /form/i, /label/i],
        innerHTML: ['label.innerHTML', 'field.innerHTML', 'input.innerHTML'],
        priority: 2,
        factory_method: 'DOM.input() or DOM.field()',
    },
    modals: {
        patterns: [/modal/i, /dialog/i, /window/i, /wizard/i],
        innerHTML: ['modal.innerHTML', 'dialog.innerHTML'],
        priority: 3,
        factory_method: 'DOM.modal() (needs implementation)',
    },
    sidebars: {
        patterns: [/sidebar/i, /panel/i],
        innerHTML: ['sidebar.innerHTML', 'panel.innerHTML'],
        priority: 4,
        factory_method: 'DOM.sidebar() (needs implementation)',
    },
    headers: {
        patterns: [/header/i, /bar/i, /taskbar/i],
        innerHTML: ['header.innerHTML', 'bar.innerHTML'],
        priority: 5,
        factory_method: 'DOM.header() or DOM.bar()',
    },
    icons: {
        patterns: [/icon/i, /svg/i],
        innerHTML: ['.innerHTML = getIconSvg', '.innerHTML = icons.', '.innerHTML = `<svg'],
        priority: 1,
        factory_method: 'DOM.icon() (already exists)',
    },
    lists: {
        patterns: [/list/i, /item/i, /tree/i],
        innerHTML: ['item.innerHTML', 'list.innerHTML'],
        priority: 6,
        factory_method: 'DOM.listItem()',
    },
    tables: {
        patterns: [/table/i, /thead/i, /row/i, /cell/i],
        innerHTML: ['thead.innerHTML', 'row.innerHTML', 'cell.innerHTML'],
        priority: 7,
        factory_method: 'DOM.table() (needs implementation)',
    },
};

// CSS token violation patterns (inline styles that should use tokens)
const TOKEN_VIOLATIONS = {
    colors: {
        patterns: [/color:\s*#[0-9a-f]{3,6}/i, /background:\s*#[0-9a-f]{3,6}/i, /rgb\(/i, /rgba\(/i],
        token_prefix: '--color-',
        examples: ['--color-text-primary', '--color-bg-secondary', '--color-accent-primary'],
    },
    spacing: {
        patterns: [/padding:\s*\d+px/i, /margin:\s*\d+px/i, /gap:\s*\d+px/i],
        token_prefix: '--space-',
        examples: ['--space-sm', '--space-md', '--space-lg'],
    },
    sizing: {
        patterns: [/width:\s*\d+px/i, /height:\s*\d+px/i, /font-size:\s*\d+px/i],
        token_prefix: '--font-size- or --icon-size- or --button-height-',
        examples: ['--font-size-md', '--icon-size-lg', '--button-height-md'],
    },
    borders: {
        patterns: [/border-radius:\s*\d+px/i, /border:\s*\d+px/i],
        token_prefix: '--radius-',
        examples: ['--radius-sm', '--radius-md', '--radius-lg'],
    },
};

// =============================================================================
// Analysis Functions
// =============================================================================

function categorizeBypass(bypass) {
    const snippet = bypass.snippet.toLowerCase();
    const file = bypass.file.toLowerCase();

    for (const [category, config] of Object.entries(COMPONENT_PATTERNS)) {
        // Check file path patterns
        if (config.patterns.some(pattern => pattern.test(file))) {
            return category;
        }

        // Check innerHTML assignment patterns
        if (config.innerHTML.some(pattern => snippet.includes(pattern.toLowerCase()))) {
            return category;
        }
    }

    return 'uncategorized';
}

function detectTokenViolations(bypass) {
    const violations = [];

    for (const [type, config] of Object.entries(TOKEN_VIOLATIONS)) {
        for (const pattern of config.patterns) {
            if (pattern.test(bypass.snippet)) {
                violations.push({
                    type,
                    token_prefix: config.token_prefix,
                    examples: config.examples,
                });
                break; // One violation per type per bypass
            }
        }
    }

    return violations;
}

function calculatePriority(bypass, category, tokenViolations) {
    let score = 0;

    // Base priority from component type
    const categoryConfig = COMPONENT_PATTERNS[category];
    if (categoryConfig) {
        score += (8 - categoryConfig.priority) * 10; // Higher priority = higher score
    }

    // Severity weight
    const severityWeights = { error: 30, warning: 20, info: 5 };
    score += severityWeights[bypass.severity] || 0;

    // Token violations increase priority
    score += tokenViolations.length * 10;

    // Icon assignments are critical (most common, easiest to fix)
    if (category === 'icons') {
        score += 20;
    }

    return score;
}

function buildDependencyGraph(bypasses) {
    const fileMap = {};

    for (const bypass of bypasses) {
        const file = bypass.file;
        if (!fileMap[file]) {
            fileMap[file] = {
                file,
                bypassCount: 0,
                categories: new Set(),
                tokenViolations: 0,
            };
        }

        fileMap[file].bypassCount++;
        fileMap[file].categories.add(bypass.category);
        fileMap[file].tokenViolations += bypass.tokenViolations.length;
    }

    // Convert to array and sort by bypass count
    return Object.values(fileMap)
        .map(f => ({
            ...f,
            categories: Array.from(f.categories),
        }))
        .sort((a, b) => b.bypassCount - a.bypassCount);
}

// =============================================================================
// Main Analysis
// =============================================================================

function main() {
    // Parse arguments
    const args = process.argv.slice(2);
    let outputPath = DEFAULT_OUTPUT;

    for (const arg of args) {
        if (arg.startsWith('--output=')) {
            outputPath = arg.split('=')[1];
        } else if (arg === '--help' || arg === '-h') {
            console.log(`
DOM Centralization Scanner

Analyzes DOM bypass report and categorizes by component type.

Usage: node dom-centralization-scanner.cjs [options]

Options:
  --output=<file>  Output JSON file (default: ${DEFAULT_OUTPUT})
  --help, -h       Show this help

Prerequisites:
  Run find-dom-bypasses.cjs first to generate ${BYPASS_REPORT}
`);
            process.exit(0);
        }
    }

    console.log(`\nDOM Centralization Scanner`);
    console.log(`==========================`);

    // Load bypass report
    if (!fs.existsSync(BYPASS_REPORT)) {
        console.error(`\nError: ${BYPASS_REPORT} not found.`);
        console.error(`Run find-dom-bypasses.cjs first to generate the report.\n`);
        process.exit(1);
    }

    console.log(`Loading bypass report: ${BYPASS_REPORT}\n`);
    const bypassData = JSON.parse(fs.readFileSync(BYPASS_REPORT, 'utf-8'));

    // Categorize and enrich bypasses
    console.log('Categorizing bypasses...');
    const enrichedBypasses = bypassData.bypasses.map(bypass => {
        const category = categorizeBypass(bypass);
        const tokenViolations = detectTokenViolations(bypass);
        const priority = calculatePriority(bypass, category, tokenViolations);

        return {
            ...bypass,
            category,
            tokenViolations,
            priority,
            factoryMethod: COMPONENT_PATTERNS[category]?.factory_method || 'DOM.createElement()',
        };
    });

    // Sort by priority (highest first)
    enrichedBypasses.sort((a, b) => b.priority - a.priority);

    // Build category summary
    const categoryStats = {};
    for (const bypass of enrichedBypasses) {
        if (!categoryStats[bypass.category]) {
            categoryStats[bypass.category] = {
                count: 0,
                errorCount: 0,
                tokenViolations: 0,
                files: new Set(),
            };
        }
        categoryStats[bypass.category].count++;
        if (bypass.severity === 'error') {
            categoryStats[bypass.category].errorCount++;
        }
        categoryStats[bypass.category].tokenViolations += bypass.tokenViolations.length;
        categoryStats[bypass.category].files.add(bypass.file);
    }

    // Convert sets to arrays
    for (const category in categoryStats) {
        categoryStats[category].files = categoryStats[category].files.size;
    }

    // Build dependency graph
    console.log('Building dependency graph...');
    const dependencyGraph = buildDependencyGraph(enrichedBypasses);

    // Build migration manifest
    const migrationManifest = {
        phase1_icons: enrichedBypasses.filter(b => b.category === 'icons'),
        phase2_buttons: enrichedBypasses.filter(b => b.category === 'buttons'),
        phase3_forms: enrichedBypasses.filter(b => b.category === 'forms'),
        phase4_modals: enrichedBypasses.filter(b => b.category === 'modals'),
        phase5_sidebars: enrichedBypasses.filter(b => b.category === 'sidebars'),
        phase6_headers: enrichedBypasses.filter(b => b.category === 'headers'),
        phase7_lists: enrichedBypasses.filter(b => b.category === 'lists'),
        phase8_tables: enrichedBypasses.filter(b => b.category === 'tables'),
        phase9_uncategorized: enrichedBypasses.filter(b => b.category === 'uncategorized'),
    };

    // Build output report
    const report = {
        scannedAt: new Date().toISOString(),
        sourceReport: BYPASS_REPORT,
        summary: {
            totalBypasses: enrichedBypasses.length,
            errorBypasses: enrichedBypasses.filter(b => b.severity === 'error').length,
            totalFiles: bypassData.summary.totalFiles,
            filesWithBypasses: bypassData.summary.filesWithBypasses,
            categoryStats,
        },
        migrationPhases: Object.keys(migrationManifest).map(phase => ({
            phase,
            count: migrationManifest[phase].length,
            factoryMethod: COMPONENT_PATTERNS[phase.split('_')[1]]?.factory_method || 'TBD',
        })),
        dependencyGraph,
        bypasses: enrichedBypasses,
    };

    // Write output
    const outputDir = path.dirname(outputPath);
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
    }

    fs.writeFileSync(outputPath, JSON.stringify(report, null, 2));

    // Print summary
    console.log(`\nAnalysis Complete!`);
    console.log(`==================`);
    console.log(`Total bypasses: ${report.summary.totalBypasses}`);
    console.log(`Error-level bypasses: ${report.summary.errorBypasses}`);
    console.log(`\nBy Category:`);
    for (const [category, stats] of Object.entries(categoryStats)) {
        console.log(`  ${category}: ${stats.count} (${stats.errorCount} errors, ${stats.tokenViolations} token violations, ${stats.files} files)`);
    }

    console.log(`\nTop 10 Files by Bypass Count:`);
    for (let i = 0; i < Math.min(10, dependencyGraph.length); i++) {
        const f = dependencyGraph[i];
        console.log(`  ${i + 1}. ${path.basename(f.file)}: ${f.bypassCount} bypasses (${f.categories.join(', ')})`);
    }

    console.log(`\nMigration Phases:`);
    for (const phase of report.migrationPhases) {
        console.log(`  ${phase.phase}: ${phase.count} bypasses -> ${phase.factoryMethod}`);
    }

    console.log(`\nOutput written to: ${outputPath}`);
    console.log(`\n✅ Use this report to guide DOM factory centralization migration.\n`);
}

main();
