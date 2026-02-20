/**
 * DOM Factory Documentation Generator
 *
 * Dynamically generates comprehensive documentation by analyzing the actual codebase.
 * Extracts managers, reserved keys, types, animation types, icons, violation codes,
 * and lifecycle methods from source files.
 *
 * Usage:
 *   node .claude/workspace/tools/dom-factory-doc-generator.js
 *   node .claude/workspace/tools/dom-factory-doc-generator.js --output docs/dom-factory.md
 *   node .claude/workspace/tools/dom-factory-doc-generator.js --section managers
 *   node .claude/workspace/tools/dom-factory-doc-generator.js --format json
 */

import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// =============================================================================
// Configuration
// =============================================================================

const PROJECT_ROOT = path.resolve(__dirname, "../../../");
const RENDERER_ROOT = path.join(PROJECT_ROOT, "root/archlab-ide/src/renderer");

const SOURCE_FILES = {
    domFactory: path.join(RENDERER_ROOT, "utils/dom-factory.ts"),
    managerTypes: path.join(RENDERER_ROOT, "utils/manager/types.ts"),
    managerDir: path.join(RENDERER_ROOT, "utils/manager"),
    icons: path.join(RENDERER_ROOT, "components/icons.ts"),
    validatorsDir: path.join(RENDERER_ROOT, "engine/validators/dom-factory"),
};

// =============================================================================
// File Reading Utilities
// =============================================================================

function readFile(filePath) {
    try {
        return fs.readFileSync(filePath, "utf-8");
    } catch (error) {
        console.error(`Failed to read ${filePath}:`, error.message);
        return "";
    }
}

function readDirectory(dirPath) {
    try {
        return fs.readdirSync(dirPath).filter((file) => file.endsWith(".ts") && file !== "index.ts");
    } catch (error) {
        console.error(`Failed to read directory ${dirPath}:`, error.message);
        return [];
    }
}

// =============================================================================
// Type/Interface Extractors
// =============================================================================

function extractTypeDefinition(content, typeName) {
    const typeRegex = new RegExp(`export type ${typeName}\\s*=([^;]+);`, "s");
    const match = content.match(typeRegex);
    if (!match) return null;

    const definition = match[1].trim();
    return { name: typeName, definition };
}

function extractInterfaceDefinition(content, interfaceName) {
    const interfaceRegex = new RegExp(
        `export interface ${interfaceName}\\s*(?:extends\\s+[^{]+)?\\{([^}]+)\\}`,
        "s"
    );
    const match = content.match(interfaceRegex);
    if (!match) return null;

    const body = match[1];
    const properties = [];

    // Extract properties with JSDoc comments
    const propRegex = /(?:\/\*\*([^*]+)\*\/)?\s*([a-zA-Z_]\w+)(\?)?:\s*([^;]+);/g;
    let propMatch;
    while ((propMatch = propRegex.exec(body)) !== null) {
        properties.push({
            name: propMatch[2],
            optional: !!propMatch[3],
            type: propMatch[4].trim(),
            comment: propMatch[1] ? propMatch[1].trim() : null,
        });
    }

    return { name: interfaceName, properties };
}

function extractEnumValues(content, enumName) {
    const enumRegex = new RegExp(`export type ${enumName}\\s*=([^;]+);`, "s");
    const match = content.match(enumRegex);
    if (!match) return [];

    const definition = match[1];
    const values = [];
    const valueRegex = /"([^"]+)"/g;
    let valueMatch;
    while ((valueMatch = valueRegex.exec(definition)) !== null) {
        values.push(valueMatch[1]);
    }

    return values;
}

// =============================================================================
// Manager Discovery
// =============================================================================

function discoverManagers() {
    const files = readDirectory(SOURCE_FILES.managerDir);
    const managers = [];

    for (const file of files) {
        if (file === "base-manager.ts" || file === "types.ts" || file === "manager-registry.ts") {
            continue;
        }

        const filePath = path.join(SOURCE_FILES.managerDir, file);
        const content = readFile(filePath);

        // Extract class name
        const classMatch = content.match(/export class (\w+Manager)/);
        if (!classMatch) continue;

        const className = classMatch[1];
        const managerName = className.replace("Manager", "");

        // Extract file description
        const descMatch = content.match(/\/\*\*\s*\n\s*\*\s*([^\n]+)/);
        const description = descMatch ? descMatch[1].trim() : "";

        // Extract public methods
        const methods = [];
        const methodRegex = /(?:public\s+)?(?:async\s+)?(\w+)\s*\([^)]*\):\s*([^{]+)/g;
        let methodMatch;
        while ((methodMatch = methodRegex.exec(content)) !== null) {
            const methodName = methodMatch[1];
            if (
                !methodName.startsWith("_") &&
                methodName !== "constructor" &&
                methodName !== "onInitialize" &&
                methodName !== "onDestroy"
            ) {
                methods.push({
                    name: methodName,
                    returnType: methodMatch[2].trim(),
                });
            }
        }

        managers.push({
            name: managerName,
            className,
            file,
            description,
            methods,
        });
    }

    return managers.sort((a, b) => a.name.localeCompare(b.name));
}

// =============================================================================
// Reserved Keys Extraction
// =============================================================================

function extractReservedKeys() {
    const content = readFile(SOURCE_FILES.managerTypes);
    const reservedKeyInterface = extractInterfaceDefinition(content, "ReservedKeyOptions");

    if (!reservedKeyInterface) {
        console.warn("Failed to extract ReservedKeyOptions interface");
        return [];
    }

    return reservedKeyInterface.properties.map((prop) => ({
        key: prop.name,
        type: prop.type,
        description: prop.comment ? prop.comment.replace(/^\*\s*/, "") : "",
        optional: prop.optional,
    }));
}

// =============================================================================
// Animation Types Extraction
// =============================================================================

function extractAnimationTypes() {
    const content = readFile(SOURCE_FILES.managerTypes);
    return extractEnumValues(content, "AnimationType");
}

// =============================================================================
// Icon Names Extraction
// =============================================================================

function extractIconNames() {
    const content = readFile(SOURCE_FILES.icons);
    return extractEnumValues(content, "IconName");
}

// =============================================================================
// Violation Codes Extraction
// =============================================================================

function extractViolationCodes() {
    const files = readDirectory(SOURCE_FILES.validatorsDir);
    const violations = [];

    for (const file of files) {
        if (file === "base-validator.ts" || file === "types.ts") continue;

        const filePath = path.join(SOURCE_FILES.validatorsDir, file);
        const content = readFile(filePath);

        // Extract violation codes from file header comment
        const headerMatch = content.match(/\/\*\*\s*\n\s*\*\s*([^\n]+)\s*\(([^)]+)\)/);
        if (headerMatch) {
            const description = headerMatch[1].trim();
            const codes = headerMatch[2].trim();

            // Extract individual codes
            const codeMatches = content.matchAll(/code:\s*"((?:BYP|DOM)\d{3})"/g);
            const foundCodes = new Set();
            for (const match of codeMatches) {
                foundCodes.add(match[1]);
            }

            violations.push({
                file,
                description,
                codeRange: codes,
                codes: Array.from(foundCodes).sort(),
            });
        }
    }

    return violations.sort((a, b) => {
        const aCode = a.codes[0] || "";
        const bCode = b.codes[0] || "";
        return aCode.localeCompare(bCode);
    });
}

// =============================================================================
// High-Level Constructors Extraction
// =============================================================================

function extractHighLevelConstructors() {
    const content = readFile(SOURCE_FILES.domFactory);
    const constructors = [];

    // Find public class methods (indented 4 spaces)
    const methodRegex = /^\s{4}(async )?([a-z]\w+)\s*\([^)]*\):\s*([^{]+)/gm;
    let match;
    while ((match = methodRegex.exec(content)) !== null) {
        const isAsync = !!match[1];
        const name = match[2];
        let returnType = match[3].trim();

        // Skip internal/lifecycle/utility methods (pattern-based, not hardcoded list)
        // - constructor, _private methods
        // - release*/clear/replace* = lifecycle
        // - set*/get*/prevent*/escape* = utilities
        if (
            name === "constructor" ||
            name.startsWith("_") ||
            name.startsWith("release") ||
            name.startsWith("set") ||
            name.startsWith("get") ||
            name.startsWith("prevent") ||
            name.startsWith("escape") ||
            name.startsWith("clear") ||
            name.startsWith("replace") ||
            name === "createFromHTML"
        ) {
            continue;
        }

        // Clean up return type (handle Promise wrapping)
        if (isAsync && returnType.startsWith("Promise<")) {
            returnType = returnType.replace(/^Promise<(.+)>$/, "$1");
        }

        constructors.push({
            name,
            returnType,
            async: isAsync,
        });
    }

    return constructors.sort((a, b) => a.name.localeCompare(b.name));
}

// =============================================================================
// Entity Lifecycle Methods
// =============================================================================

function extractEntityLifecycle() {
    const content = readFile(SOURCE_FILES.domFactory);
    const lifecycleMethods = [];

    // Extract createElement signature (it's a class method, not exported function)
    const createMatch = content.match(
        /async createElement<([^>]+)>\s*\(\s*([^)]+)\s*\):\s*Promise<([^>]+)>/s
    );
    if (createMatch) {
        lifecycleMethods.push({
            name: "createElement",
            signature: `async createElement<${createMatch[1].trim()}>(${createMatch[2].trim()}): Promise<${createMatch[3].trim()}>`,
            description: "Create a new DOM element with entity tracking and manager setup",
        });
    }

    // Extract releaseElement signature
    const releaseMatch = content.match(/releaseElement\s*\(([^)]+)\):\s*([^{]+)/);
    if (releaseMatch) {
        lifecycleMethods.push({
            name: "releaseElement",
            signature: `releaseElement(${releaseMatch[1].trim()}): ${releaseMatch[2].trim()}`,
            description: "Release an element and clean up all manager subscriptions",
        });
    }

    // Extract releaseElements signature
    const releaseAllMatch = content.match(/releaseElements\s*\(([^)]+)\):\s*([^{]+)/);
    if (releaseAllMatch) {
        lifecycleMethods.push({
            name: "releaseElements",
            signature: `releaseElements(${releaseAllMatch[1].trim()}): ${releaseAllMatch[2].trim()}`,
            description: "Release multiple elements at once",
        });
    }

    return lifecycleMethods;
}

// =============================================================================
// Markdown Generation
// =============================================================================

function generateMarkdown(data) {
    let md = "# DOM Factory API Documentation\n\n";
    md += "_Auto-generated from codebase analysis_\n\n";
    md += "---\n\n";

    // Table of Contents
    md += "## Table of Contents\n\n";
    md += "- [Core API](#core-api)\n";
    md += "- [Reserved Keys](#reserved-keys)\n";
    md += "- [Managers](#managers)\n";
    md += "- [High-Level Constructors](#high-level-constructors)\n";
    md += "- [Animation Types](#animation-types)\n";
    md += "- [Icon Names](#icon-names)\n";
    md += "- [Violation Codes](#violation-codes)\n";
    md += "\n---\n\n";

    // Core API
    md += "## Core API\n\n";
    md += "### Entity Lifecycle\n\n";
    for (const method of data.lifecycle) {
        md += `#### \`${method.name}\`\n\n`;
        md += `${method.description}\n\n`;
        md += "```typescript\n";
        md += method.signature + "\n";
        md += "```\n\n";
    }

    // Reserved Keys
    md += "## Reserved Keys\n\n";
    md +=
        "Reserved keys provide declarative access to DOM factory managers. These keys are intercepted\n";
    md += "by `createElement` and routed to the appropriate manager.\n\n";
    md += "| Key | Type | Description |\n";
    md += "|-----|------|-------------|\n";
    for (const key of data.reservedKeys) {
        const desc = key.description.replace(/\n/g, " ");
        md += `| \`${key.key}\` | \`${key.type}\` | ${desc} |\n`;
    }
    md += "\n";

    // Managers
    md += "## Managers\n\n";
    md += `DOM Factory uses ${data.managers.length} specialized managers for different concerns:\n\n`;
    for (const manager of data.managers) {
        md += `### ${manager.name}Manager\n\n`;
        md += `${manager.description}\n\n`;
        md += `**File:** \`${manager.file}\`\n\n`;

        if (manager.methods.length > 0) {
            md += "**Key Methods:**\n\n";
            for (const method of manager.methods) {
                md += `- \`${method.name}()\`: ${method.returnType}\n`;
            }
            md += "\n";
        }
    }

    // High-Level Constructors
    md += "## High-Level Constructors\n\n";
    md +=
        "High-level constructors provide convenient shorthand for common element patterns.\n\n";
    md += "All constructors are async and return Promises.\n\n";
    md += "| Function | Return Type |\n";
    md += "|----------|-------------|\n";
    for (const ctor of data.highLevelConstructors) {
        const asyncPrefix = ctor.async ? "async " : "";
        md += `| \`${asyncPrefix}${ctor.name}()\` | \`${ctor.returnType}\` |\n`;
    }
    md += "\n";

    // Animation Types
    md += "## Animation Types\n\n";
    md +=
        "Animation types supported by the `animate` reserved key and AnimationManager.\n\n";
    md += "```typescript\n";
    md += "type AnimationType =\n";
    for (let i = 0; i < data.animationTypes.length; i++) {
        const type = data.animationTypes[i];
        const isLast = i === data.animationTypes.length - 1;
        md += `    | "${type}"${isLast ? ";" : ""}\n`;
    }
    md += "```\n\n";

    // Icon Names
    md += "## Icon Names\n\n";
    md += `Total icons available: ${data.iconNames.length}\n\n`;
    md += "```typescript\n";
    md += "type IconName =\n";
    for (let i = 0; i < data.iconNames.length; i++) {
        const icon = data.iconNames[i];
        const isLast = i === data.iconNames.length - 1;
        md += `    | "${icon}"${isLast ? ";" : ""}\n`;
    }
    md += "```\n\n";

    // Violation Codes
    md += "## Violation Codes\n\n";
    md +=
        "Validation rules enforced by the governance engine to ensure DOM factory compliance.\n\n";
    for (const violation of data.violations) {
        md += `### ${violation.description}\n\n`;
        md += `**Codes:** ${violation.codes.join(", ")}\n\n`;
        md += `**Detector:** \`${violation.file}\`\n\n`;
    }

    return md;
}

// =============================================================================
// JSON Generation
// =============================================================================

function generateJSON(data) {
    return JSON.stringify(data, null, 2);
}

// =============================================================================
// Main Execution
// =============================================================================

function main() {
    const args = process.argv.slice(2);
    const options = {
        output: null,
        section: null,
        format: "md",
    };

    // Parse arguments
    for (let i = 0; i < args.length; i++) {
        if (args[i] === "--output" && i + 1 < args.length) {
            options.output = args[++i];
        } else if (args[i] === "--section" && i + 1 < args.length) {
            options.section = args[++i];
        } else if (args[i] === "--format" && i + 1 < args.length) {
            options.format = args[++i];
        }
    }

    console.log("🔍 Analyzing DOM Factory codebase...\n");

    // Extract all data
    const data = {
        managers: discoverManagers(),
        reservedKeys: extractReservedKeys(),
        animationTypes: extractAnimationTypes(),
        iconNames: extractIconNames(),
        violations: extractViolationCodes(),
        highLevelConstructors: extractHighLevelConstructors(),
        lifecycle: extractEntityLifecycle(),
    };

    console.log(`✓ Found ${data.managers.length} managers`);
    console.log(`✓ Found ${data.reservedKeys.length} reserved keys`);
    console.log(`✓ Found ${data.animationTypes.length} animation types`);
    console.log(`✓ Found ${data.iconNames.length} icons`);
    console.log(`✓ Found ${data.violations.length} violation detectors`);
    console.log(`✓ Found ${data.highLevelConstructors.length} high-level constructors`);
    console.log(`✓ Found ${data.lifecycle.length} lifecycle methods\n`);

    // Filter by section if requested
    if (options.section) {
        const sectionData = {};
        if (data[options.section]) {
            sectionData[options.section] = data[options.section];
            Object.assign(data, sectionData);
        } else {
            console.error(`❌ Unknown section: ${options.section}`);
            console.error(
                `Available sections: ${Object.keys(data).join(", ")}`
            );
            process.exit(1);
        }
    }

    // Generate output
    let output;
    if (options.format === "json") {
        output = generateJSON(data);
    } else {
        output = generateMarkdown(data);
    }

    // Write or print
    if (options.output) {
        const outputPath = path.resolve(options.output);
        fs.writeFileSync(outputPath, output, "utf-8");
        console.log(`✓ Documentation written to ${outputPath}`);
    } else {
        console.log("---\n");
        console.log(output);
    }
}

main();
