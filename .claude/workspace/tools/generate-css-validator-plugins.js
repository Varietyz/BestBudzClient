#!/usr/bin/env node
// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

/**
 * Generate Individual CSS Validator Vite Plugins
 *
 * Creates one plugin file per CSS scanner for maximum SOC and decoupling (LAW 1).
 * Each plugin bridges to codebase-validation scanner via subprocess.
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const CSS_SCANNERS = [
    { id: "css-hardcoded-colors", category: "design-tokens", severity: "high" },
    { id: "css-hardcoded-rgba", category: "design-tokens", severity: "high" },
    { id: "css-hardcoded-spacing", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-radius", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-font-size", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-font-weight", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-font-family", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-line-height", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-letter-spacing", category: "design-tokens", severity: "low" },
    { id: "css-hardcoded-shadows", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-transitions", category: "design-tokens", severity: "medium" },
    { id: "css-hardcoded-z-index", category: "design-tokens", severity: "medium" },
    { id: "css-bem-violations", category: "architecture", severity: "low" },
    { id: "css-inconsistent-naming", category: "architecture", severity: "medium" },
    { id: "css-wrong-location", category: "architecture", severity: "medium" },
    { id: "css-unscoped-overrides", category: "architecture", severity: "medium" },
    { id: "css-import-order", category: "architecture", severity: "medium" },
    { id: "css-duplicate-components", category: "architecture", severity: "high" },
    { id: "css-variable-location", category: "architecture", severity: "medium" },
    { id: "css-variable-consistency", category: "variables", severity: "medium" },
    { id: "css-variable-naming", category: "variables", severity: "medium" },
    { id: "css-variable-order", category: "variables", severity: "low" },
    { id: "css-variable-aliases", category: "variables", severity: "low" },
    { id: "css-variable-redefinitions", category: "variables", severity: "high" },
    { id: "css-variable-category-location", category: "variables", severity: "medium" },
    { id: "css-variable-unit-mismatch", category: "variables", severity: "medium" },
    { id: "css-important-usage", category: "best-practices", severity: "high" },
    { id: "css-complex-selectors", category: "best-practices", severity: "medium" },
    { id: "css-magic-numbers", category: "best-practices", severity: "low" },
    { id: "css-animation-naming", category: "best-practices", severity: "low" },
    { id: "css-vendor-prefixes", category: "best-practices", severity: "low" },
    { id: "css-non-responsive-units", category: "best-practices", severity: "medium" },
    { id: "css-layout-hardcoded-values", category: "best-practices", severity: "medium" },
    { id: "css-dead-code", category: "quality", severity: "low" },
    { id: "css-responsive-patterns", category: "quality", severity: "medium" },
    { id: "css-glass-patterns", category: "quality", severity: "low" },
    { id: "css-mobile-imports", category: "quality", severity: "low" },
];

const PLUGIN_TEMPLATE = `// Copyright 2026 Baleine Jay | https://banes-lab.com/licensing | Commercial use requires a paid license.
// SPDX-License-Identifier: LicenseRef-ARCHLAB-NonCommercial-1.0

import type { Plugin } from "vite";
import { execSync } from "child_process";
import path from "path";

export interface {{PASCAL_NAME}}PluginOptions {
    failOnViolation?: boolean;
}

const DEFAULT_OPTIONS: Required<{{PASCAL_NAME}}PluginOptions> = {
    failOnViolation: true,
};

const COLORS = {
    reset: "\\x1b[0m",
    bold: "\\x1b[1m",
    red: "\\x1b[31m",
    yellow: "\\x1b[33m",
    cyan: "\\x1b[36m",
    gray: "\\x1b[90m",
};

export function {{CAMEL_NAME}}Plugin(options: {{PASCAL_NAME}}PluginOptions = {}): Plugin {
    const opts = { ...DEFAULT_OPTIONS, ...options };
    let scanResults: { total: number } | null = null;

    return {
        name: "{{SCANNER_ID}}",
        enforce: "pre",

        buildStart(): void {
            console.log(\`\\n\${COLORS.cyan}[{{DISPLAY_NAME}}]\${COLORS.reset} Running scanner...\`);

            try {
                const rootDir = path.resolve(process.cwd(), "../../");

                const output = execSync("npm run cv scan {{SCANNER_ID}}", {
                    cwd: rootDir,
                    encoding: "utf-8",
                    timeout: 120000,
                    stdio: ["pipe", "pipe", "pipe"],
                });

                const violationMatch = output.match(/(\\d+)\\s+violations?\\s+found/i);

                if (!violationMatch) {
                    console.log(\`\${COLORS.cyan}[{{DISPLAY_NAME}}]\${COLORS.reset} No violations found\`);
                    return;
                }

                const totalViolations = parseInt(violationMatch[1], 10);

                if (totalViolations === 0) {
                    console.log(\`\${COLORS.cyan}[{{DISPLAY_NAME}}]\${COLORS.reset} No violations found\`);
                    return;
                }

                scanResults = { total: totalViolations };

                console.log(
                    \`\${COLORS.yellow}[{{DISPLAY_NAME}}]\${COLORS.reset} Found \${totalViolations} violation(s)\`
                );
            } catch (error) {
                console.error(\`\${COLORS.red}[{{DISPLAY_NAME}}]\${COLORS.reset} Scanner failed:\`, error);
            }
        },

        buildEnd(): void {
            if (!scanResults || scanResults.total === 0) {
                return;
            }

            if (opts.failOnViolation) {
                const report = formatReport(scanResults.total);
                this.error(report);
            }
        },
    };
}

function formatReport(totalViolations: number): string {
    const lines: string[] = [];

    lines.push("");
    lines.push(\`\${COLORS.bold}{{DISPLAY_NAME}} Violations\${COLORS.reset}\`);
    lines.push("=".repeat(80));
    lines.push("");
    lines.push(\`\${COLORS.red}\${totalViolations} violation(s)\${COLORS.reset} found\`);
    lines.push("");
    lines.push(\`\${COLORS.bold}How to view full details:\${COLORS.reset}\`);
    lines.push(\`cd root/codebase-validation && npm run cv results {{SCANNER_ID}}\`);
    lines.push("");
    lines.push("=".repeat(80));

    return lines.join("\\n");
}

export default {{CAMEL_NAME}}Plugin;
`;

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

function generatePlugin(scanner) {
    const pascalName = toPascalCase(scanner.id);
    const camelName = toCamelCase(scanner.id);
    const displayName = scanner.id
        .split("-")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

    const content = PLUGIN_TEMPLATE.replace(/{{PASCAL_NAME}}/g, pascalName)
        .replace(/{{CAMEL_NAME}}/g, camelName)
        .replace(/{{SCANNER_ID}}/g, scanner.id)
        .replace(/{{DISPLAY_NAME}}/g, displayName);

    return content;
}

function main() {
    const outputDir = path.resolve(
        __dirname,
        "../../../root/archlab-ide/src/renderer/engine/validators"
    );

    if (!fs.existsSync(outputDir)) {
        console.error(`Output directory does not exist: ${outputDir}`);
        process.exit(1);
    }

    let created = 0;

    for (const scanner of CSS_SCANNERS) {
        const fileName = `vite-${scanner.id}-validator-plugin.ts`;
        const filePath = path.join(outputDir, fileName);

        if (fs.existsSync(filePath)) {
            console.log(`⚠️  Skipping ${fileName} (already exists)`);
            continue;
        }

        const content = generatePlugin(scanner);
        fs.writeFileSync(filePath, content, "utf-8");
        console.log(`✓ Created ${fileName}`);
        created++;
    }

    console.log(`\n${"=".repeat(80)}`);
    console.log(`Created ${created} CSS validator plugins`);
    console.log(`Total scanners: ${CSS_SCANNERS.length}`);
    console.log(`${"=".repeat(80)}\n`);
}

main();
