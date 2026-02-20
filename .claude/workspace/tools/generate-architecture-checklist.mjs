#!/usr/bin/env node

/**
 * Generate Architecture Checklist
 *
 * Creates a markdown checklist for systematic architecture review.
 * No analysis - just folder/file tree with checkboxes for manual review.
 */

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, "../../../root/archlab-ide");

// ============================================================================
// Scan Directory Tree
// ============================================================================

function scanDirectory(dir, basePath = "") {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    const structure = { folders: {}, files: [] };

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        const relativePath = path.join(basePath, entry.name);

        if (entry.isDirectory()) {
            if (!["node_modules", "dist", "build", ".git", ".vscode"].includes(entry.name)) {
                structure.folders[entry.name] = scanDirectory(fullPath, relativePath);
            }
        } else if (entry.isFile() && entry.name.endsWith(".ts")) {
            if (!entry.name.endsWith(".d.ts") &&
                !entry.name.includes(".test.") &&
                !entry.name.includes(".spec.")) {
                structure.files.push(entry.name);
            }
        }
    }

    return structure;
}

// ============================================================================
// Generate Markdown Checklist
// ============================================================================

function generateChecklist(structure, indent = 0, output = []) {
    const prefix = "  ".repeat(indent);

    // First, output all files in current level
    for (const file of structure.files.sort()) {
        output.push(`${prefix}- [ ] ${file}`);
    }

    // Then, output subfolders
    for (const [folderName, subStructure] of Object.entries(structure.folders).sort()) {
        output.push(`${prefix}**${folderName}/**`);
        generateChecklist(subStructure, indent + 1, output);
    }

    return output;
}

// ============================================================================
// Main
// ============================================================================

function main() {
    console.log("📋 Generating architecture checklist...\n");

    const srcMain = path.join(ROOT, "src", "main");
    const srcRenderer = path.join(ROOT, "src", "renderer");
    const srcShared = path.join(ROOT, "src", "shared");

    const mainStructure = scanDirectory(srcMain);
    const rendererStructure = scanDirectory(srcRenderer);
    const sharedStructure = scanDirectory(srcShared);

    // Count files
    function countFiles(structure) {
        let count = structure.files.length;
        for (const sub of Object.values(structure.folders)) {
            count += countFiles(sub);
        }
        return count;
    }

    const totalFiles = countFiles(mainStructure) + countFiles(rendererStructure) + countFiles(sharedStructure);

    // Generate markdown
    const output = [];

    output.push("# Architecture Naming Violations - Systematic Fix Checklist");
    output.push("");
    output.push("**Objective**: Fixing architectural naming violations systematically one by one, marking progression per folder as we complete the files with a 'Read file → analyze code logic file keeps → ask architectural naming questions → perform rename/reorganization' Names are perceived as incorrect until proven otherwise via the semantic rules and naming pattern.");
    output.push("");
    output.push(`**Total Files**: ${totalFiles}`);
    output.push(`**Progress**: 0 / ${totalFiles} (0%)`);
    output.push("");
    output.push("**Process per file**:");
    output.push("1. Read file content");
    output.push("2. Analyze code logic and exports");
    output.push("3. Ask architectural naming questions:");
    output.push("   - What role does this file serve? (manager, service, component, database, etc.)");
    output.push("   - What domain does it belong to? (terminal, claude, hardware, etc.)");
    output.push("   - Is it in the correct tier folder?");
    output.push("   - Does filename match folder pattern?");
    output.push("4. Perform rename/reorganization if needed");
    output.push("5. Mark checkbox when complete");
    output.push("");
    output.push("---");
    output.push("");

    output.push("## src/main/");
    output.push("");
    output.push(...generateChecklist(mainStructure, 0));
    output.push("");

    output.push("## src/renderer/");
    output.push("");
    output.push(...generateChecklist(rendererStructure, 0));
    output.push("");

    output.push("## src/shared/");
    output.push("");
    output.push(...generateChecklist(sharedStructure, 0));
    output.push("");

    // Write to file
    const checklistPath = path.join(__dirname, "../../../ARCHITECTURE-FIX-CHECKLIST.md");
    fs.writeFileSync(checklistPath, output.join("\n"));

    console.log(`✅ Checklist generated: ${path.relative(process.cwd(), checklistPath)}`);
    console.log(`📊 Total files to review: ${totalFiles}`);
    console.log("");
    console.log("Next steps:");
    console.log("1. Open ARCHITECTURE-FIX-CHECKLIST.md");
    console.log("2. Work through each file systematically");
    console.log("3. Check box [x] when file is verified/fixed");
    console.log("4. Update progress counter as you go");
}

main();
