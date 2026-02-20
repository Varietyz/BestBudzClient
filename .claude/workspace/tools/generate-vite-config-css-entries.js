#!/usr/bin/env node
// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

/**
 * Generate vite.config.mts Import and Plugin Entries
 *
 * Outputs import statements and plugin calls for all CSS validators.
 */

const CSS_SCANNERS = [
    "css-hardcoded-colors",
    "css-hardcoded-rgba",
    "css-hardcoded-spacing",
    "css-hardcoded-radius",
    "css-hardcoded-font-size",
    "css-hardcoded-font-weight",
    "css-hardcoded-font-family",
    "css-hardcoded-line-height",
    "css-hardcoded-letter-spacing",
    "css-hardcoded-shadows",
    "css-hardcoded-transitions",
    "css-hardcoded-z-index",
    "css-bem-violations",
    "css-inconsistent-naming",
    "css-wrong-location",
    "css-unscoped-overrides",
    "css-import-order",
    "css-duplicate-components",
    "css-variable-location",
    "css-variable-consistency",
    "css-variable-naming",
    "css-variable-order",
    "css-variable-aliases",
    "css-variable-redefinitions",
    "css-variable-category-location",
    "css-variable-unit-mismatch",
    "css-important-usage",
    "css-complex-selectors",
    "css-magic-numbers",
    "css-animation-naming",
    "css-vendor-prefixes",
    "css-non-responsive-units",
    "css-layout-hardcoded-values",
    "css-dead-code",
    "css-responsive-patterns",
    "css-glass-patterns",
    "css-mobile-imports",
];

function toPascalCase(str) {
    return str
        .split("-")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join("");
}

function toCamelCase(str) {
    const pascal = toPascalCase(str);
    return pascal.charAt(0).toLowerCase() + pascal.slice(1);
}

function generateImports() {
    console.log("// CSS Validator Plugins - Design Tokens");
    CSS_SCANNERS.filter((s) => s.includes("hardcoded")).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(
            `import { ${camelName}Plugin } from './src/renderer/engine/validators/vite-${scanner}-validator-plugin';`
        );
    });

    console.log("\n// CSS Validator Plugins - Architecture");
    CSS_SCANNERS.filter(
        (s) =>
            (s.includes("bem") ||
                s.includes("naming") ||
                s.includes("location") ||
                s.includes("override") ||
                s.includes("import") ||
                s.includes("duplicate")) &&
            !s.includes("variable")
    ).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(
            `import { ${camelName}Plugin } from './src/renderer/engine/validators/vite-${scanner}-validator-plugin';`
        );
    });

    console.log("\n// CSS Validator Plugins - Variables");
    CSS_SCANNERS.filter((s) => s.includes("variable")).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(
            `import { ${camelName}Plugin } from './src/renderer/engine/validators/vite-${scanner}-validator-plugin';`
        );
    });

    console.log("\n// CSS Validator Plugins - Best Practices & Quality");
    CSS_SCANNERS.filter(
        (s) =>
            !s.includes("hardcoded") &&
            !s.includes("variable") &&
            !s.includes("bem") &&
            !s.includes("naming") &&
            !s.includes("location") &&
            !s.includes("override") &&
            !s.includes("import") &&
            !s.includes("duplicate")
    ).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(
            `import { ${camelName}Plugin } from './src/renderer/engine/validators/vite-${scanner}-validator-plugin';`
        );
    });
}

function generatePluginCalls() {
    console.log("\n\n// Plugin array entries:\n");

    console.log("    // CSS Validators - Design Tokens");
    CSS_SCANNERS.filter((s) => s.includes("hardcoded")).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(`    ${camelName}Plugin({ failOnViolation: true }),`);
    });

    console.log("\n    // CSS Validators - Architecture");
    CSS_SCANNERS.filter(
        (s) =>
            (s.includes("bem") ||
                s.includes("naming") ||
                s.includes("location") ||
                s.includes("override") ||
                s.includes("import") ||
                s.includes("duplicate")) &&
            !s.includes("variable")
    ).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(`    ${camelName}Plugin({ failOnViolation: true }),`);
    });

    console.log("\n    // CSS Validators - Variables");
    CSS_SCANNERS.filter((s) => s.includes("variable")).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(`    ${camelName}Plugin({ failOnViolation: true }),`);
    });

    console.log("\n    // CSS Validators - Best Practices & Quality");
    CSS_SCANNERS.filter(
        (s) =>
            !s.includes("hardcoded") &&
            !s.includes("variable") &&
            !s.includes("bem") &&
            !s.includes("naming") &&
            !s.includes("location") &&
            !s.includes("override") &&
            !s.includes("import") &&
            !s.includes("duplicate")
    ).forEach((scanner) => {
        const camelName = toCamelCase(scanner);
        console.log(`    ${camelName}Plugin({ failOnViolation: true }),`);
    });
}

console.log("=".repeat(80));
console.log("VITE.CONFIG.MTS IMPORTS");
console.log("=".repeat(80));
generateImports();

console.log("\n" + "=".repeat(80));
console.log("VITE.CONFIG.MTS PLUGIN CALLS");
console.log("=".repeat(80));
generatePluginCalls();
console.log("\n" + "=".repeat(80));
