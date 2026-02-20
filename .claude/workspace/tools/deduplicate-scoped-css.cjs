#!/usr/bin/env node
/**
 * Deduplicate Scoped CSS
 *
 * Converts duplicate selectors to unique modifier classes based on their properties.
 */

const fs = require('fs');
const path = require('path');

function deduplicateScopedCSS(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const lines = content.split('\n');

    const rules = [];
    let currentComment = '';
    let currentSelector = '';
    let currentProperties = [];
    let inRule = false;
    let fileHeader = [];
    let inHeader = true;

    // Parse CSS rules
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmed = line.trim();

        // Collect file header
        if (inHeader) {
            if (trimmed.startsWith('.')) {
                inHeader = false;
            } else {
                fileHeader.push(line);
                continue;
            }
        }

        // Track comments
        if (trimmed.startsWith('/*')) {
            currentComment = line;
            if (!trimmed.endsWith('*/')) {
                while (i < lines.length - 1) {
                    i++;
                    currentComment += '\n' + lines[i];
                    if (lines[i].trim().endsWith('*/')) break;
                }
            }
            continue;
        }

        // Selector start
        if (trimmed.match(/^\.[\w-]+/)) {
            currentSelector = trimmed;
            currentProperties = [];
            inRule = true;
            continue;
        }

        // Rule end
        if (trimmed === '}' && inRule) {
            inRule = false;
            rules.push({
                comment: currentComment,
                selector: currentSelector,
                properties: [...currentProperties]
            });
            currentComment = '';
            currentSelector = '';
            currentProperties = [];
            continue;
        }

        // Property inside rule
        if (inRule && trimmed && !trimmed.startsWith('/*')) {
            currentProperties.push(trimmed);
        }
    }

    // Group rules by unique property combinations
    const uniqueRules = new Map();
    let modifierCounter = 1;

    for (const rule of rules) {
        const propKey = rule.properties.sort().join('|');

        if (!uniqueRules.has(propKey)) {
            // First occurrence - keep original or create modifier
            const baseName = rule.selector.replace(' {', '');
            let newSelector = baseName;

            // Create semantic modifier based on properties
            if (rule.properties.length === 1) {
                const prop = rule.properties[0];

                // Extract property and value
                const [property, ...valueParts] = prop.split(':');
                const value = valueParts.join(':').trim().replace(/;$/, '');

                // Create modifier from property + value
                if (property.trim() === 'border-color') {
                    const colorName = value.match(/--color-([a-z-]+)/)?.[1] || 'default';
                    newSelector = `${baseName}--border-${colorName}`;
                } else if (property.trim() === 'color') {
                    const colorName = value.match(/--color-([a-z-]+)/)?.[1] || 'default';
                    newSelector = `${baseName}--color-${colorName}`;
                } else if (property.trim() === 'background') {
                    const colorName = value.match(/--color-([a-z-]+)/)?.[1] || 'default';
                    newSelector = `${baseName}--bg-${colorName}`;
                } else if (property.trim() === 'display') {
                    newSelector = `${baseName}--display-${value}`;
                } else {
                    newSelector = `${baseName}--${modifierCounter++}`;
                }
            } else {
                newSelector = `${baseName}--${modifierCounter++}`;
            }

            uniqueRules.set(propKey, {
                ...rule,
                selector: newSelector + ' {'
            });
        }
        // Skip duplicates
    }

    // Build output
    const output = [];
    output.push(...fileHeader);

    for (const rule of uniqueRules.values()) {
        if (rule.comment) {
            output.push(rule.comment);
        }
        output.push(rule.selector);
        for (const prop of rule.properties) {
            output.push(`    ${prop}`);
        }
        output.push('}');
        output.push('');
    }

    return output.join('\n');
}

// Get file path from command line
const filePath = process.argv[2];
if (!filePath) {
    console.error('Usage: node deduplicate-scoped-css.cjs <file.css>');
    process.exit(1);
}

try {
    const deduplicated = deduplicateScopedCSS(filePath);
    fs.writeFileSync(filePath, deduplicated, 'utf-8');
    console.log(`Deduplicated: ${path.basename(filePath)}`);
} catch (error) {
    console.error(`Error: ${error.message}`);
    process.exit(1);
}
