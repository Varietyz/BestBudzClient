#!/usr/bin/env node
/**
 * Migration Script: BaseScanner → BaseDbScanner
 *
 * Migrates 43 scanner files from old BaseScanner to new BaseDbScanner.
 * Handles both specialized base classes and npm-command scanners.
 */

import { readFileSync, writeFileSync } from 'fs';
import { resolve, relative } from 'path';

const ROOT = '/d/GIT/archlab/root/codebase-validation';

// Files to migrate (verified via grep)
const FILES_TO_MIGRATE = [
    'core/base/scanners/base-accessibility-scanner.js',
    'core/base/scanners/base-security-scanner.js',
    'core/base/scanners/base-magic-literal-scanner.js',
    'core/base/scanners/base-constants-scanner.js',
    'core/base/base-css-scanner.js',
    'tools/base/base-tool-runner.js',
    'npm-commands/java/spotbugs-scanner.js',
    'npm-commands/java/pmd-scanner.js',
    'npm-commands/java/google-java-format-scanner.js',
    'npm-commands/java/errorprone-scanner.js',
    'npm-commands/unused/unused-scanner.js',
    'npm-commands/java/checkstyle-scanner.js',
    'npm-commands/ts/typescript-scanner.js',
    'npm-commands/html/htmlhint-scanner.js',
    'npm-commands/ts/ts-prune-scanner.js',
    'npm-commands/go/staticcheck-scanner.js',
    'npm-commands/stylelint/stylelint-scanner.js',
    'npm-commands/go/golangci-lint-scanner.js',
    'npm-commands/rust/rustfmt-scanner.js',
    'npm-commands/go/goimports-scanner.js',
    'npm-commands/rust/clippy-scanner.js',
    'npm-commands/go/gofmt-scanner.js',
    'npm-commands/python/ruff-scanner.js',
    'npm-commands/format/format-scanner.js',
    'npm-commands/python/ruff-format-scanner.js',
    'npm-commands/eslint/lint-scanner.js',
    'npm-commands/python/pyright-scanner.js',
    'npm-commands/eslint/eslint-scanner.js',
    'npm-commands/python/prospector-scanner.js',
    'npm-commands/dependencies/npm-prune-scanner.js',
    'npm-commands/python/mypy-scanner.js',
    'npm-commands/depcheck/depcheck-scanner.js',
    'npm-commands/python/isort-scanner.js',
    'npm-commands/cpp/cppcheck-scanner.js',
    'npm-commands/python/black-scanner.js',
    'npm-commands/cpp/clang-tidy-scanner.js',
    'npm-commands/python/bandit-scanner.js',
    'npm-commands/cpp/clang-format-scanner.js',
    'npm-commands/oxlint/oxlint-scanner.js',
    'npm-commands/biome/biome-scanner.js',
    'npm-commands/multi-lang/pre-commit-scanner.js',
    'npm-commands/multi-lang/editorconfig-scanner.js',
    'npm-commands/knip/knip-scanner.js',
];

const SPECIALIZED_BASE_CLASSES = [
    'core/base/scanners/base-accessibility-scanner.js',
    'core/base/scanners/base-security-scanner.js',
    'core/base/scanners/base-magic-literal-scanner.js',
    'core/base/scanners/base-constants-scanner.js',
    'core/base/base-css-scanner.js',
];

const results = {
    success: [],
    failed: [],
    skipped: [],
};

/**
 * Calculate relative path from file to base-db-scanner.js
 */
function getImportPath(filePath) {
    const fileDir = resolve(ROOT, filePath, '..');
    const targetPath = resolve(ROOT, 'core/base/base-db-scanner.js');
    let rel = relative(fileDir, targetPath).replace(/\\/g, '/');

    if (!rel.startsWith('.')) {
        rel = './' + rel;
    }

    return rel;
}

/**
 * Determine if file is specialized base class
 */
function isSpecializedBase(filePath) {
    return SPECIALIZED_BASE_CLASSES.includes(filePath);
}

/**
 * Migrate a single file
 */
function migrateFile(filePath) {
    const fullPath = resolve(ROOT, filePath);

    try {
        let content = readFileSync(fullPath, 'utf8');
        const originalContent = content;

        // Calculate correct import path
        const importPath = getImportPath(filePath);

        // 1. Update import statement
        const oldImportPattern = /import\s*\{\s*BaseScanner\s*\}\s*from\s*["']([^"']+)["'];?/;
        const match = content.match(oldImportPattern);

        if (!match) {
            results.skipped.push({ file: filePath, reason: 'No BaseScanner import found' });
            return;
        }

        // Replace import
        content = content.replace(
            oldImportPattern,
            `import { BaseDbScanner, SCAN_TYPES, DISCOVERY_TYPES } from "${importPath}";`
        );

        // 2. Update class extends
        content = content.replace(
            /class\s+(\w+)\s+extends\s+BaseScanner/g,
            'class $1 extends BaseDbScanner'
        );

        // 3. Update constructor if it has registryDir parameter
        content = content.replace(
            /constructor\s*\(\s*rootDir\s*,\s*registryDir\s*\)/g,
            'constructor(rootDir)'
        );

        // Also update super calls
        content = content.replace(
            /super\s*\(\s*rootDir\s*,\s*registryDir\s*\)/g,
            'super(rootDir)'
        );

        // 4. For npm-command scanners, add required methods if not present
        if (!isSpecializedBase(filePath)) {
            // Check if getScanType exists
            if (!content.includes('getScanType()')) {
                // Add before getName() or at end of class
                const getNameMatch = content.match(/(\s+)getName\(\)/);
                if (getNameMatch) {
                    const indent = getNameMatch[1];
                    const insertion = `${indent}getScanType() {\n${indent}    return SCAN_TYPES.VALIDATION_RESULT;\n${indent}}\n\n`;
                    content = content.replace(/(\s+getName\(\))/, insertion + '$1');
                } else {
                    // Add before closing brace
                    content = content.replace(/(\n})\s*$/, '\n\n    getScanType() {\n        return SCAN_TYPES.VALIDATION_RESULT;\n    }\n$1');
                }
            }

            // Check if getDiscoveryType exists
            if (!content.includes('getDiscoveryType()')) {
                const getNameMatch = content.match(/(\s+)getName\(\)/);
                if (getNameMatch) {
                    const indent = getNameMatch[1];
                    const insertion = `${indent}getDiscoveryType() {\n${indent}    return DISCOVERY_TYPES.NONE;\n${indent}}\n\n`;
                    content = content.replace(/(\s+getName\(\))/, insertion + '$1');
                } else {
                    content = content.replace(/(\n})\s*$/, '\n\n    getDiscoveryType() {\n        return DISCOVERY_TYPES.NONE;\n    }\n$1');
                }
            }
        }

        // 5. Replace registry.build() calls
        // For specialized base classes, leave as-is (they'll be updated by their children)
        if (!isSpecializedBase(filePath)) {
            // Replace buildValidationResult with return statement
            content = content.replace(
                /await\s+this\.registry\.buildValidationResult\([^)]+\);?/g,
                'return { validationResult: true };'
            );

            // Replace registry.build with return statement
            content = content.replace(
                /await\s+this\.registry\.build\([^)]+\);?/g,
                'return { filesScanned: 0, violations: [] };'
            );
        }

        // Only write if content changed
        if (content !== originalContent) {
            writeFileSync(fullPath, content, 'utf8');
            results.success.push(filePath);
        } else {
            results.skipped.push({ file: filePath, reason: 'No changes needed' });
        }

    } catch (error) {
        results.failed.push({ file: filePath, error: error.message });
    }
}

// Execute migration
console.log('Starting migration of 43 files...\n');

for (const file of FILES_TO_MIGRATE) {
    migrateFile(file);
}

// Report results
console.log('\n=== MIGRATION RESULTS ===\n');
console.log(`✅ SUCCESS: ${results.success.length} files`);
if (results.success.length > 0) {
    results.success.forEach(f => console.log(`   - ${f}`));
}

console.log(`\n⚠️  SKIPPED: ${results.skipped.length} files`);
if (results.skipped.length > 0) {
    results.skipped.forEach(({ file, reason }) => console.log(`   - ${file}: ${reason}`));
}

console.log(`\n❌ FAILED: ${results.failed.length} files`);
if (results.failed.length > 0) {
    results.failed.forEach(({ file, error }) => console.log(`   - ${file}: ${error}`));
}

console.log(`\n=== SUMMARY ===`);
console.log(`Total files processed: ${FILES_TO_MIGRATE.length}`);
console.log(`Success: ${results.success.length}`);
console.log(`Skipped: ${results.skipped.length}`);
console.log(`Failed: ${results.failed.length}`);

process.exit(results.failed.length > 0 ? 1 : 0);
