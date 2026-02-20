#!/usr/bin/env node

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const violationsPath = path.join(__dirname, "architecture-violations-pag.json");
const data = JSON.parse(fs.readFileSync(violationsPath, "utf8"));

const remaining = data.files.filter((f) => f.violations.some((v) => v.code === "ROLE_NOT_DETECTED"));

console.log("Total ROLE_NOT_DETECTED:", remaining.length);
console.log("");

// Categorize
const categories = {
    migrations: [],
    preload: [],
    main_entry: [],
    base_classes: [],
    components: [],
    other: [],
};

remaining.forEach((f) => {
    const basename = path.basename(f.file, ".ts");
    const filepath = f.file;

    if (filepath.includes("/migrations/")) {
        categories.migrations.push(f.file);
    } else if (basename === "preload" || filepath.includes("/preload/")) {
        categories.preload.push(f.file);
    } else if (basename === "main") {
        categories.main_entry.push(f.file);
    } else if (basename.startsWith("base-")) {
        categories.base_classes.push(f.file);
    } else if (filepath.includes("/components/") && !basename.endsWith("-component")) {
        categories.components.push(f.file);
    } else {
        categories.other.push(f.file);
    }
});

console.log("=== MIGRATIONS (database schemas):", categories.migrations.length);
categories.migrations.forEach((f) => console.log("  " + f));

console.log("\n=== PRELOAD (Electron preload scripts):", categories.preload.length);
categories.preload.forEach((f) => console.log("  " + f));

console.log("\n=== MAIN ENTRY POINTS:", categories.main_entry.length);
categories.main_entry.forEach((f) => console.log("  " + f));

console.log("\n=== BASE CLASSES (foundational):", categories.base_classes.length);
categories.base_classes.slice(0, 20).forEach((f) => console.log("  " + f));
if (categories.base_classes.length > 20) {
    console.log(`  ... and ${categories.base_classes.length - 20} more`);
}

console.log("\n=== COMPONENTS (missing -component suffix):", categories.components.length);
categories.components.slice(0, 20).forEach((f) => console.log("  " + f));
if (categories.components.length > 20) {
    console.log(`  ... and ${categories.components.length - 20} more`);
}

console.log("\n=== OTHER (needs review):", categories.other.length);
categories.other.slice(0, 30).forEach((f) => console.log("  " + f));
if (categories.other.length > 30) {
    console.log(`  ... and ${categories.other.length - 30} more`);
}
