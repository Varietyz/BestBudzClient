#!/usr/bin/env node
import { readFileSync, writeFileSync } from 'fs';
import { resolve, dirname } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const ROOT = resolve(__dirname, '../../../root/codebase-validation');

const FILES = [
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

let fixed = 0;

for (const file of FILES) {
    const fullPath = resolve(ROOT, file);
    try {
        let content = readFileSync(fullPath, 'utf8');
        const original = content;

        // Fix excessive blank lines in getScanType
        content = content.replace(
            /getScanType\(\)\s*\{\s+return\s+SCAN_TYPES\.VALIDATION_RESULT;\s+\}/g,
            'getScanType() {\n        return SCAN_TYPES.VALIDATION_RESULT;\n    }'
        );

        // Fix excessive blank lines in getDiscoveryType
        content = content.replace(
            /getDiscoveryType\(\)\s*\{\s+return\s+DISCOVERY_TYPES\.NONE;\s+\}/g,
            'getDiscoveryType() {\n        return DISCOVERY_TYPES.NONE;\n    }'
        );

        // Remove multiple consecutive blank lines (more than 2)
        content = content.replace(/\n\n\n+/g, '\n\n');

        if (content !== original) {
            writeFileSync(fullPath, content, 'utf8');
            fixed++;
        }
    } catch (error) {
        console.error(`Error processing ${file}:`, error.message);
    }
}

console.log(`Cleaned up formatting in ${fixed} files`);
