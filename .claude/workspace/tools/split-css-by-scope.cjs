#!/usr/bin/env node
/**
 * Split CSS by Scope
 *
 * Splits large CSS files into smaller scoped files based on BEM selector prefixes.
 * Pattern: .{component}__{element} → {component}/{component}-{element}.css
 */

const fs = require('fs');
const path = require('path');

function splitCSSByScope(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const lines = content.split('\n');

    // Extract component name from file: terminal-chat-component.css → terminal-chat
    const fileName = path.basename(filePath, '.css');
    const componentName = fileName.replace(/-component$/, '');
    const componentDir = path.join(path.dirname(filePath), componentName);

    // Create component directory
    if (!fs.existsSync(componentDir)) {
        fs.mkdirSync(componentDir, { recursive: true });
    }

    // Group rules by element type
    const scopes = {};
    let currentComment = '';
    let currentSelector = '';
    let currentRule = [];
    let fileHeader = [];
    let inHeader = true;

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmed = line.trim();

        // Collect file header (before first selector)
        if (inHeader) {
            if (trimmed.startsWith('.')) {
                inHeader = false;
            } else {
                if (trimmed) fileHeader.push(line);
                continue;
            }
        }

        // Collect comments
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
            currentRule = [currentComment, line];
            currentComment = '';
            continue;
        }

        // Collect rule body
        if (currentSelector) {
            currentRule.push(line);
        }

        // Rule end
        if (trimmed === '}' && currentSelector) {
            currentRule.push(''); // Empty line after rule

            // Determine scope from selector: .terminal-chat__container → container
            const match = currentSelector.match(/^\.([a-z-]+)__([a-z-]+)/);
            if (match) {
                const [, prefix, element] = match;
                const scopeKey = element.replace(/^style-\d+$/, 'numbered'); // Group numbered styles

                if (!scopes[scopeKey]) {
                    scopes[scopeKey] = [];
                }
                scopes[scopeKey].push(currentRule.join('\n'));
            }

            currentSelector = '';
            currentRule = [];
        }
    }

    // Write scoped files
    const scopeFiles = [];
    for (const [scopeName, rules] of Object.entries(scopes)) {
        const scopeFileName = `${componentName}-${scopeName}.css`;
        const scopeFilePath = path.join(componentDir, scopeFileName);

        const scopeContent = [
            `/**`,
            ` * ${componentName} - ${scopeName} scope`,
            ` *`,
            ` * Auto-split from ${fileName}.css`,
            ` */`,
            '',
            ...rules
        ].join('\n');

        fs.writeFileSync(scopeFilePath, scopeContent, 'utf-8');
        scopeFiles.push(scopeFileName);
        console.log(`  Created: ${scopeFileName} (${rules.length} rules)`);
    }

    // Create barrel index.css
    const indexPath = path.join(componentDir, 'index.css');
    const indexContent = [
        `/**`,
        ` * ${componentName} Component Styles`,
        ` *`,
        ` * Barrel import for all ${componentName} scoped styles.`,
        ` */`,
        '',
        ...scopeFiles.map(file => `@import url("./${file}");`),
        ''
    ].join('\n');

    fs.writeFileSync(indexPath, indexContent, 'utf-8');
    console.log(`  Created: index.css`);

    // Update parent index.css to import from folder
    const parentDir = path.dirname(filePath);
    const parentIndexPath = path.join(parentDir, 'index.css');

    if (fs.existsSync(parentIndexPath)) {
        let parentIndex = fs.readFileSync(parentIndexPath, 'utf-8');
        const oldImport = `@import "./${fileName}.css";`;
        const newImport = `@import "./${componentName}/index.css";`;

        if (parentIndex.includes(oldImport)) {
            parentIndex = parentIndex.replace(oldImport, newImport);
            fs.writeFileSync(parentIndexPath, parentIndex, 'utf-8');
            console.log(`  Updated: parent index.css`);
        }
    }

    // Archive original file
    const archivePath = filePath + '.bak';
    fs.renameSync(filePath, archivePath);
    console.log(`  Archived: ${fileName}.css → ${fileName}.css.bak`);

    return {
        componentName,
        scopeCount: Object.keys(scopes).length,
        scopeFiles
    };
}

// Get file path from command line
const filePath = process.argv[2];
if (!filePath) {
    console.error('Usage: node split-css-by-scope.cjs <component.css>');
    console.error('Example: node split-css-by-scope.cjs terminal-chat-component.css');
    process.exit(1);
}

const fullPath = path.resolve(filePath);
if (!fs.existsSync(fullPath)) {
    console.error(`Error: File not found: ${fullPath}`);
    process.exit(1);
}

console.log(`Splitting: ${path.basename(fullPath)}`);
const result = splitCSSByScope(fullPath);
console.log(`\nSplit into ${result.scopeCount} scoped files in ${result.componentName}/`);
