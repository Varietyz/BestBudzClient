#!/usr/bin/env node
import { readFileSync, writeFileSync } from 'fs';
import { resolve, dirname, relative, sep } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const ROOT = resolve(__dirname, '../../../root/codebase-validation');

console.log('ROOT:', ROOT);

const FILES = [
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

const SPECIALIZED = new Set([
    'core/base/scanners/base-accessibility-scanner.js',
    'core/base/scanners/base-security-scanner.js',
    'core/base/scanners/base-magic-literal-scanner.js',
    'core/base/scanners/base-constants-scanner.js',
    'core/base/base-css-scanner.js',
]);

const results = { success: [], failed: [], skipped: [] };

function getImportPath(filePath) {
    const fileDir = dirname(resolve(ROOT, filePath));
    const targetPath = resolve(ROOT, 'core/base/base-db-scanner.js');
    let rel = relative(fileDir, targetPath).split(sep).join('/');
    if (!rel.startsWith('.')) rel = './' + rel;
    return rel;
}

for (const file of FILES) {
    const fullPath = resolve(ROOT, file);
    try {
        let content = readFileSync(fullPath, 'utf8');
        const original = content;
        const importPath = getImportPath(file);

        // 1. Import
        content = content.replace(
            /import\s*\{\s*BaseScanner\s*\}\s*from\s*["']([^"']+)["'];?/,
            `import { BaseDbScanner, SCAN_TYPES, DISCOVERY_TYPES } from "${importPath}";`
        );

        // 2. Extends
        content = content.replace(
            /class\s+(\w+)\s+extends\s+BaseScanner/g,
            'class $1 extends BaseDbScanner'
        );

        // 3. Constructor
        content = content.replace(
            /constructor\s*\(\s*rootDir\s*,\s*registryDir\s*\)/g,
            'constructor(rootDir)'
        );
        content = content.replace(
            /super\s*\(\s*rootDir\s*,\s*registryDir\s*\)/g,
            'super(rootDir)'
        );

        // 4. Add methods for npm-command scanners
        if (!SPECIALIZED.has(file)) {
            if (!content.includes('getScanType()')) {
                const match = content.match(/(\s+)getName\(\)/);
                if (match) {
                    const indent = match[1];
                    const method = `${indent}getScanType() {\n${indent}    return SCAN_TYPES.VALIDATION_RESULT;\n${indent}}\n\n`;
                    content = content.replace(/(\s+getName\(\))/, method + '$1');
                } else {
                    content = content.replace(/(\n})\s*$/, '\n\n    getScanType() {\n        return SCAN_TYPES.VALIDATION_RESULT;\n    }\n$1');
                }
            }

            if (!content.includes('getDiscoveryType()')) {
                const match = content.match(/(\s+)getName\(\)/);
                if (match) {
                    const indent = match[1];
                    const method = `${indent}getDiscoveryType() {\n${indent}    return DISCOVERY_TYPES.NONE;\n${indent}}\n\n`;
                    content = content.replace(/(\s+getName\(\))/, method + '$1');
                } else {
                    content = content.replace(/(\n})\s*$/, '\n\n    getDiscoveryType() {\n        return DISCOVERY_TYPES.NONE;\n    }\n$1');
                }
            }
        }

        // 5. Registry calls (simplified for npm-commands)
        if (!SPECIALIZED.has(file)) {
            content = content.replace(
                /await\s+this\.registry\.buildValidationResult\([^)]+\);?/g,
                '// Migration note: return value handled by BaseDbScanner'
            );
            content = content.replace(
                /await\s+this\.registry\.build\([^)]+\);?/g,
                '// Migration note: return value handled by BaseDbScanner'
            );
        }

        if (content !== original) {
            writeFileSync(fullPath, content, 'utf8');
            results.success.push(file);
        } else {
            results.skipped.push({ file, reason: 'No changes' });
        }
    } catch (error) {
        results.failed.push({ file, error: error.message });
    }
}

console.log('\n=== RESULTS ===');
console.log(`✅ Success: ${results.success.length}`);
results.success.forEach(f => console.log(`   ${f}`));
console.log(`\n⚠️  Skipped: ${results.skipped.length}`);
results.skipped.forEach(({ file, reason }) => console.log(`   ${file}: ${reason}`));
console.log(`\n❌ Failed: ${results.failed.length}`);
results.failed.forEach(({ file, error }) => console.log(`   ${file}: ${error}`));
console.log(`\nTotal: ${FILES.length}, Success: ${results.success.length}, Failed: ${results.failed.length}`);
process.exit(results.failed.length > 0 ? 1 : 0);
