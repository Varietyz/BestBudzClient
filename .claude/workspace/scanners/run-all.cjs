#!/usr/bin/env node
/**
 * Run All AST Scanners
 *
 * Orchestrates execution of all AST-based scanners and integrates with
 * the codebase-validation CLI for comprehensive scanning.
 *
 * Usage:
 *   node run-all.cjs              # Run all scanners (AST + codebase-validation)
 *   node run-all.cjs --quick      # Quick mode (AST scanners only)
 *   node run-all.cjs --scanner=X  # Run specific scanner only
 *   node run-all.cjs --cv-only    # Run only codebase-validation scanners
 *   node run-all.cjs --ast-only   # Run only AST scanners
 *
 * Integrates with:
 *   - root/codebase-validation/cli - 126 scanners, 91 validators
 *   - stylelint, eslint, typescript
 *
 * Output:
 *   - Individual scanner reports in reports/
 *   - Combined summary in reports/scan-summary.remediation.md
 *
 * @module run-all
 */

const { execSync, spawnSync } = require('child_process');
const path = require('path');
const fs = require('fs');

// =============================================================================
// Configuration
// =============================================================================

const SCANNERS_DIR = path.resolve(__dirname, 'ast');
const REPORTS_DIR = path.resolve(__dirname, 'reports');
const SUMMARY_REPORT = 'scan-summary.remediation.md';

// Codebase-validation CLI path
const CV_CLI_DIR = path.resolve(__dirname, '../../../root/codebase-validation');
const CV_CLI = path.join(CV_CLI_DIR, 'cli/index.js');

// Relevant CSS scanners from codebase-validation for DOM factory migration
const CV_CSS_SCANNERS = [
    'css-hardcoded-colors',
    'css-hardcoded-spacing',
    'css-hardcoded-font-size',
    'css-hardcoded-radius',
    'css-hardcoded-z-index',
    'css-variable-consistency',
];

const SCANNERS = [
    {
        name: 'DOM Bypass Scanner',
        file: 'dom-bypass-scanner.cjs',
        description: 'Detects document.createElement, innerHTML bypasses',
        reportFile: 'dom-bypasses.remediation',
        priority: 1,
    },
    {
        name: 'Listener Pattern Scanner',
        file: 'listener-pattern-scanner.cjs',
        description: 'Detects unmanaged listeners, render pattern issues',
        reportFile: 'listener-patterns.remediation',
        priority: 2,
    },
    {
        name: 'Inline Style Scanner',
        file: 'inline-style-scanner.cjs',
        description: 'Detects hardcoded colors, spacing, CSS token violations',
        reportFile: 'inline-styles.remediation',
        priority: 3,
    },
];

// =============================================================================
// Codebase-Validation Integration
// =============================================================================

/**
 * Run a codebase-validation scanner via CLI
 * @param {string} scannerName
 * @returns {object}
 */
function runCVScanner(scannerName) {
    console.log(`\nRunning CV scanner: ${scannerName}`);

    const startTime = Date.now();

    try {
        const result = spawnSync('node', [CV_CLI, 'scan', scannerName], {
            cwd: CV_CLI_DIR,
            encoding: 'utf-8',
            timeout: 60000,
        });

        const duration = Date.now() - startTime;

        return {
            name: `cv:${scannerName}`,
            status: result.status === 0 ? 'SUCCESS' : 'FAILED',
            duration,
            output: result.stdout,
            error: result.stderr,
        };
    } catch (err) {
        return {
            name: `cv:${scannerName}`,
            status: 'ERROR',
            error: err.message,
            duration: Date.now() - startTime,
        };
    }
}

/**
 * Run all CSS scanners from codebase-validation
 * @returns {object[]}
 */
function runCVCSScanners() {
    console.log(`\n${'═'.repeat(60)}`);
    console.log('Running Codebase-Validation CSS Scanners');
    console.log(`${'═'.repeat(60)}`);

    const results = [];

    for (const scanner of CV_CSS_SCANNERS) {
        const result = runCVScanner(scanner);
        results.push(result);
    }

    return results;
}

/**
 * Run stylelint via codebase-validation
 * @returns {object}
 */
function runStylelint() {
    console.log('\nRunning stylelint...');

    const startTime = Date.now();

    try {
        const result = spawnSync('node', [CV_CLI, 'scan', 'stylelint'], {
            cwd: CV_CLI_DIR,
            encoding: 'utf-8',
            timeout: 120000,
        });

        return {
            name: 'cv:stylelint',
            status: result.status === 0 ? 'SUCCESS' : 'FAILED',
            duration: Date.now() - startTime,
            output: result.stdout,
        };
    } catch (err) {
        return {
            name: 'cv:stylelint',
            status: 'ERROR',
            error: err.message,
            duration: Date.now() - startTime,
        };
    }
}

// =============================================================================
// Execution
// =============================================================================

/**
 * Run a single scanner
 * @param {object} scanner
 * @returns {object}
 */
function runScanner(scanner) {
    const scriptPath = path.join(SCANNERS_DIR, scanner.file);

    if (!fs.existsSync(scriptPath)) {
        return {
            name: scanner.name,
            status: 'SKIPPED',
            error: `Scanner not found: ${scanner.file}`,
            duration: 0,
        };
    }

    console.log(`\n${'═'.repeat(60)}`);
    console.log(`Running: ${scanner.name}`);
    console.log(`${'═'.repeat(60)}`);

    const startTime = Date.now();

    try {
        const result = spawnSync('node', [scriptPath], {
            cwd: __dirname,
            stdio: 'inherit',
            encoding: 'utf-8',
        });

        const duration = Date.now() - startTime;

        if (result.status !== 0) {
            return {
                name: scanner.name,
                status: 'FAILED',
                error: `Exit code: ${result.status}`,
                duration,
            };
        }

        // Read the JSON report for summary
        const jsonReportPath = path.join(REPORTS_DIR, scanner.reportFile + '.json');
        let summary = null;
        if (fs.existsSync(jsonReportPath)) {
            const data = JSON.parse(fs.readFileSync(jsonReportPath, 'utf-8'));
            summary = data.summary;
        }

        return {
            name: scanner.name,
            status: 'SUCCESS',
            duration,
            summary,
            reportFile: scanner.reportFile,
        };
    } catch (err) {
        return {
            name: scanner.name,
            status: 'ERROR',
            error: err.message,
            duration: Date.now() - startTime,
        };
    }
}

/**
 * Generate combined summary report
 * @param {object[]} results
 */
function generateSummaryReport(results) {
    const timestamp = new Date().toISOString();

    let totalViolations = 0;
    let totalErrors = 0;
    let totalWarnings = 0;
    let totalInfo = 0;

    for (const r of results) {
        if (r.summary) {
            totalViolations += r.summary.totalViolations || 0;
            totalErrors += r.summary.errors || 0;
            totalWarnings += r.summary.warnings || 0;
            totalInfo += r.summary.info || 0;
        }
    }

    const passedScanners = results.filter(r => r.status === 'SUCCESS').length;
    const failedScanners = results.filter(r => r.status !== 'SUCCESS').length;

    let report = `---
name: scan-summary
type: checklist
version: 1.0.0
generated: ${timestamp}
---

THIS CHECKLIST TRACKS combined AST scanner results

%% META %%:
intent: "Aggregate all scanner findings for remediation planning"
objective: "Zero violations across all scanners"

## Executive Summary

**Scan Date**: ${timestamp}
**Scanners Run**: ${results.length}
**Scanners Passed**: ${passedScanners}
**Scanners Failed**: ${failedScanners}

**Total Violations**: ${totalViolations}
  - Errors: ${totalErrors}
  - Warnings: ${totalWarnings}
  - Info: ${totalInfo}

**Health Score**: ${totalViolations === 0 ? '100%' : Math.max(0, 100 - Math.round(totalErrors * 2 + totalWarnings * 0.5))}%

## Scanner Results

`;

    for (const r of results) {
        const status = r.status === 'SUCCESS' ? '✅' : '❌';
        report += `### ${status} ${r.name}

- **Status**: ${r.status}
- **Duration**: ${r.duration}ms
`;

        if (r.summary) {
            report += `- **Violations**: ${r.summary.totalViolations || 0} (${r.summary.errors || 0} errors, ${r.summary.warnings || 0} warnings)
- **Files Affected**: ${r.summary.filesWithViolations || 0} / ${r.summary.totalFiles || 0}
`;
            if (r.summary.byType) {
                report += `- **By Type**:\n`;
                for (const [type, count] of Object.entries(r.summary.byType)) {
                    report += `  - ${type}: ${count}\n`;
                }
            }
        }

        if (r.error) {
            report += `- **Error**: ${r.error}\n`;
        }

        if (r.reportFile) {
            report += `- **Full Report**: reports/${r.reportFile}.md\n`;
        }

        report += '\n';
    }

    // Add prioritized action plan
    report += `## Prioritized Action Plan

`;

    let actionNum = 1;
    for (const r of results.sort((a, b) => {
        const aErrors = a.summary?.errors || 0;
        const bErrors = b.summary?.errors || 0;
        return bErrors - aErrors;
    })) {
        if (r.summary && r.summary.errors > 0) {
            report += `### Action ${actionNum}: ${r.name}

**Priority**: HIGH (${r.summary.errors} errors)

1. READ reports/${r.reportFile}.md
2. FIX error-level violations first
3. RE-RUN scanner to verify
4. PROCEED to warnings

`;
            actionNum++;
        }
    }

    // Add recommendations
    report += `## Recommendations

1. Fix all error-level violations before proceeding with feature work
2. Address warnings during regular code review
3. Run scanners as part of CI/CD pipeline
4. Track violation counts over time for progress metrics

## Re-Run Command

\`\`\`bash
node .claude/workspace/scanners/run-all.cjs
\`\`\`

## Individual Scanner Commands

`;

    for (const scanner of SCANNERS) {
        report += `\`\`\`bash
# ${scanner.name}
node .claude/workspace/scanners/ast/${scanner.file}
\`\`\`

`;
    }

    // ALWAYS/NEVER rules
    report += `ALWAYS run scanners before major releases
ALWAYS fix errors before warnings
ALWAYS verify fixes with re-scan
NEVER ignore error-level violations
NEVER commit code that increases violation count
NEVER bypass scanner checks without documented reason
`;

    // Write report
    const reportPath = path.join(REPORTS_DIR, SUMMARY_REPORT);
    if (!fs.existsSync(REPORTS_DIR)) {
        fs.mkdirSync(REPORTS_DIR, { recursive: true });
    }
    fs.writeFileSync(reportPath, report);

    // Also write JSON summary
    fs.writeFileSync(
        reportPath.replace('.md', '.json'),
        JSON.stringify({
            generatedAt: timestamp,
            totalViolations,
            totalErrors,
            totalWarnings,
            totalInfo,
            scannerResults: results,
        }, null, 2)
    );

    return reportPath;
}

// =============================================================================
// Main
// =============================================================================

function main() {
    const args = process.argv.slice(2);
    const quickMode = args.includes('--quick');
    const astOnly = args.includes('--ast-only');
    const cvOnly = args.includes('--cv-only');
    const specificScanner = args.find(a => a.startsWith('--scanner='))?.split('=')[1];

    console.log('\n╔══════════════════════════════════════════════════════════════╗');
    console.log('║    AST + Codebase-Validation Scanner Suite                   ║');
    console.log('║    PAG-Formatted Remediation Logs                            ║');
    console.log('╚══════════════════════════════════════════════════════════════╝');

    let mode = 'Full';
    if (quickMode) mode = 'Quick (AST only)';
    if (astOnly) mode = 'AST scanners only';
    if (cvOnly) mode = 'Codebase-validation only';

    console.log(`\nMode: ${mode}`);
    console.log(`Output: ${REPORTS_DIR}/`);

    // Ensure reports directory exists
    if (!fs.existsSync(REPORTS_DIR)) {
        fs.mkdirSync(REPORTS_DIR, { recursive: true });
    }

    const results = [];
    const cvResults = [];
    const overallStart = Date.now();

    // Select AST scanners to run
    let scannersToRun = SCANNERS;
    if (specificScanner) {
        scannersToRun = SCANNERS.filter(s =>
            s.file.includes(specificScanner) ||
            s.name.toLowerCase().includes(specificScanner.toLowerCase())
        );
        if (scannersToRun.length === 0 && !cvOnly) {
            console.error(`\nNo AST scanner found matching: ${specificScanner}`);
            console.log('\nAvailable AST scanners:');
            for (const s of SCANNERS) {
                console.log(`  - ${s.file}: ${s.name}`);
            }
        }
    }

    // Run AST scanners (unless cv-only)
    if (!cvOnly) {
        for (const scanner of scannersToRun) {
            const result = runScanner(scanner);
            results.push(result);
        }
    }

    // Run codebase-validation scanners (unless ast-only or quick)
    if (!astOnly && !quickMode && !specificScanner) {
        // Run stylelint FIRST (catches most CSS issues)
        const stylelintResult = runStylelint();
        cvResults.push(stylelintResult);

        // Then run detailed CSS scanners
        const cvScanResults = runCVCSScanners();
        cvResults.push(...cvScanResults);
    }

    // Merge results
    const allResults = [...results, ...cvResults];

    // Generate summary
    console.log(`\n${'═'.repeat(60)}`);
    console.log('Generating Summary Report');
    console.log(`${'═'.repeat(60)}`);

    const summaryPath = generateSummaryReport(allResults);
    const totalDuration = Date.now() - overallStart;

    // Print final summary
    console.log('\n╔══════════════════════════════════════════════════════════════╗');
    console.log('║                       SCAN COMPLETE                          ║');
    console.log('╚══════════════════════════════════════════════════════════════╝');

    let totalViolations = 0;
    let totalErrors = 0;

    // AST scanner results
    if (results.length > 0) {
        console.log('\nAST Scanners:');
        for (const r of results) {
            const status = r.status === 'SUCCESS' ? '✅' : '❌';
            const violations = r.summary?.totalViolations || 0;
            const errors = r.summary?.errors || 0;
            totalViolations += violations;
            totalErrors += errors;
            console.log(`  ${status} ${r.name}: ${violations} violations (${errors} errors)`);
        }
    }

    // CV scanner results
    if (cvResults.length > 0) {
        console.log('\nCodebase-Validation Scanners:');
        for (const r of cvResults) {
            const status = r.status === 'SUCCESS' ? '✅' : '❌';
            console.log(`  ${status} ${r.name}: ${r.duration}ms`);
        }
    }

    console.log(`\nTotal: ${totalViolations} violations (${totalErrors} errors)`);
    console.log(`Duration: ${totalDuration}ms`);
    console.log(`\nReports written to: ${REPORTS_DIR}/`);
    console.log(`Summary: ${summaryPath}`);

    // Exit with error if there are error-level violations
    if (totalErrors > 0) {
        console.log(`\n⚠️  ${totalErrors} error-level violations require attention.`);
        process.exit(1);
    } else {
        console.log('\n✅ No error-level violations found.');
        process.exit(0);
    }
}

main();
