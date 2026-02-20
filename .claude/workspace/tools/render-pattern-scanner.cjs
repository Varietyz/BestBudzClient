#!/usr/bin/env node
/**
 * Render Pattern Scanner
 *
 * Scans for components that should use BaseRenderComponent instead of BaseEventComponent.
 * Identifies patterns that lead to event listener accumulation.
 *
 * Usage: node render-pattern-scanner.js [--fix]
 */

const fs = require('fs');
const path = require('path');
const glob = require('glob');

const SRC_DIR = path.resolve(__dirname, '../../../root/archlab-ide/src/renderer');

// Patterns that indicate a component needs BaseRenderComponent
const PATTERNS = {
    // Extends BaseEventComponent
    extendsBaseEvent: /class\s+(\w+)\s+extends\s+BaseEventComponent/g,

    // Has private/public render() method
    hasRenderMethod: /(?:private|public|protected)?\s*render\s*\(\s*\)\s*(?::\s*\w+)?\s*\{/g,

    // Calls cleanup() in hide() - should be reset()
    cleanupInHide: /hide\s*\(\s*\)[^}]*this\.cleanup\s*\(\s*\)/gs,

    // Uses addManagedListener but doesn't call clearManagedListeners before render
    listenerWithoutClear: /render\s*\(\s*\)[^}]*addManagedListener[^}]*\}/gs,

    // Has panel/rootElement that gets removed and recreated
    panelRemovePattern: /this\.(panel|rootElement)\s*\.\s*remove\s*\(\s*\)/g,

    // Uses innerHTML = "" instead of DOM.clear()
    innerHtmlClear: /\.innerHTML\s*=\s*["']["']/g,
};

function scanFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const relativePath = path.relative(SRC_DIR, filePath);
    const issues = [];

    // Check if it extends BaseEventComponent
    const extendsMatch = content.match(PATTERNS.extendsBaseEvent);
    if (!extendsMatch) {
        return null; // Not a BaseEventComponent, skip
    }

    const className = extendsMatch[0].match(/class\s+(\w+)/)[1];

    // Check for render method
    const hasRender = PATTERNS.hasRenderMethod.test(content);
    PATTERNS.hasRenderMethod.lastIndex = 0; // Reset regex

    if (!hasRender) {
        return null; // No render method, no issue
    }

    // Check for cleanup() in hide()
    if (PATTERNS.cleanupInHide.test(content)) {
        PATTERNS.cleanupInHide.lastIndex = 0;
        issues.push({
            type: 'cleanup-in-hide',
            message: 'Uses cleanup() in hide() - should use reset() to allow re-show',
            severity: 'error',
        });
    }

    // Check for panel.remove() without DOM.clear()
    if (PATTERNS.panelRemovePattern.test(content)) {
        PATTERNS.panelRemovePattern.lastIndex = 0;

        // Check if DOM.clear is called before remove
        const clearBeforeRemove = /DOM\.clear\s*\([^)]+\)[^}]*\.remove\s*\(\s*\)/gs.test(content);
        if (!clearBeforeRemove) {
            issues.push({
                type: 'missing-dom-clear',
                message: 'Removes element without DOM.clear() - pooled children not released',
                severity: 'error',
            });
        }
    }

    // Check for clearManagedListeners in render method
    // Check if clearManagedListeners appears shortly after render() declaration
    const hasAddListener = /addManagedListener/.test(content);

    if (hasAddListener) {
        // Find render method DECLARATION (not call) - must have access modifier or be preceded by newline+whitespace
        const renderStart = content.match(/(?:^|\n)\s*(?:private|public|protected)\s+render\s*\(\s*\)[^{]*\{/);
        if (renderStart && renderStart.index !== undefined) {
            const startIndex = renderStart.index + renderStart[0].length;
            // Check the first 500 chars after render() opening brace for clearManagedListeners
            const firstPart = content.slice(startIndex, startIndex + 500);
            // Allow optional comment before the call, and handle newlines/whitespace
            const hasClearAtStart = /^[\s\n]*(\/\/[^\n]*[\n\r]+[\s\n]*)?this\.clearManagedListeners\s*\(\s*\)/.test(firstPart);

            if (!hasClearAtStart) {
                issues.push({
                    type: 'listener-accumulation',
                    message: 'render() adds listeners but doesn\'t call clearManagedListeners() first',
                    severity: 'error',
                });
            }
        }
    }

    // Check for innerHTML clear instead of DOM.clear()
    if (PATTERNS.innerHtmlClear.test(content)) {
        PATTERNS.innerHtmlClear.lastIndex = 0;
        issues.push({
            type: 'innerhtml-clear',
            message: 'Uses innerHTML="" instead of DOM.clear() - pooled elements not released',
            severity: 'warning',
        });
    }

    // Check if already using BaseRenderComponent
    const usesBaseRender = /extends\s+BaseRenderComponent/.test(content);

    if (usesBaseRender) {
        return null; // Already using optimal pattern
    }

    // If no issues and has clearManagedListeners - component is clean, no action needed
    if (issues.length === 0) {
        return null; // Clean pattern - no migration needed
    }

    return {
        file: relativePath,
        className,
        issues,
    };
}

function main() {
    const args = process.argv.slice(2);
    const shouldFix = args.includes('--fix');
    const verbose = args.includes('-v');

    console.log('🔍 Scanning for render pattern issues...\n');

    // Windows path fix - glob requires forward slashes
    const globPattern = path.join(SRC_DIR, '**/*.ts').replace(/\\/g, '/');
    const files = glob.sync(globPattern, {
        ignore: ['**/node_modules/**', '**/*.d.ts', '**/base-event-component.ts'],
    });

    if (verbose) {
        console.log(`Found ${files.length} files to scan\n`);
    }

    const results = {
        errors: [],
        warnings: [],
        info: [],
    };

    for (const file of files) {
        const result = scanFile(file);
        if (result) {
            for (const issue of result.issues) {
                const entry = {
                    file: result.file,
                    className: result.className,
                    ...issue,
                };

                if (issue.severity === 'error') {
                    results.errors.push(entry);
                } else if (issue.severity === 'warning') {
                    results.warnings.push(entry);
                } else {
                    results.info.push(entry);
                }
            }
        }
    }

    // Print results
    if (results.errors.length > 0) {
        console.log('❌ ERRORS (must fix - causes bugs):\n');
        for (const e of results.errors) {
            console.log(`  ${e.file}`);
            console.log(`    Class: ${e.className}`);
            console.log(`    Issue: ${e.message}`);
            console.log(`    Type: ${e.type}\n`);
        }
    }

    if (results.warnings.length > 0) {
        console.log('⚠️  WARNINGS (should fix):\n');
        for (const w of results.warnings) {
            console.log(`  ${w.file}`);
            console.log(`    Class: ${w.className}`);
            console.log(`    Issue: ${w.message}\n`);
        }
    }

    if (results.info.length > 0) {
        console.log('ℹ️  MIGRATION CANDIDATES:\n');
        for (const i of results.info) {
            console.log(`  ${i.file} (${i.className})`);
        }
        console.log('');
    }

    // Summary
    console.log('━'.repeat(60));
    console.log('SUMMARY:');
    console.log(`  Errors:     ${results.errors.length}`);
    console.log(`  Warnings:   ${results.warnings.length}`);
    console.log(`  Candidates: ${results.info.length}`);
    console.log(`  Total:      ${results.errors.length + results.warnings.length + results.info.length}`);

    if (results.errors.length > 0) {
        console.log('\n💡 To fix errors, migrate components to BaseRenderComponent:');
        console.log('   1. Change: extends BaseEventComponent → extends BaseRenderComponent');
        console.log('   2. Rename: render() → createContent()');
        console.log('   3. Remove: manual clearManagedListeners/DOM.clear calls');
        console.log('   4. Remove: show()/hide()/toggle() - inherited from base');
    }

    process.exit(results.errors.length > 0 ? 1 : 0);
}

main();
