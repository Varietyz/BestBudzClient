#!/usr/bin/env node

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const violationsPath = path.join(__dirname, "architecture-violations-pag.json");
const data = JSON.parse(fs.readFileSync(violationsPath, "utf8"));

const medium = data.files.filter((f) => f.violations.some((v) => v.code === "ROLE_NOT_DETECTED"));

// Already processed patterns
const processed = [
    "-db",
    "-ipc-handlers",
    "ipc-handlers",
    "-handlers",
    "-runner",
    "-bridge",
    "-engine",
    "preload",
];

const remaining = medium.filter((f) => {
    const basename = path.basename(f.file, ".ts");
    return !processed.some((pattern) => basename.includes(pattern));
});

console.log("Remaining MEDIUM violations:", remaining.length);
console.log("");

// Categorize by likely role
const categories = {
    helper: [],
    service: [],
    types: [],
    constants: [],
    main: [],
    preload: [],
    migration: [],
    pattern: [],
    other: [],
};

remaining.forEach((f) => {
    const basename = path.basename(f.file, ".ts");
    const filepath = f.file;

    // Main entry points
    if (basename === "main" || basename === "index") {
        categories.main.push(f.file);
    }
    // Preload scripts
    else if (basename.includes("preload")) {
        categories.preload.push(f.file);
    }
    // Migrations
    else if (filepath.includes("/migrations/")) {
        categories.migration.push(f.file);
    }
    // Patterns (likely helper or validator)
    else if (filepath.includes("/patterns/")) {
        categories.pattern.push(f.file);
    }
    // Files with "helper" or "util" in name
    else if (basename.includes("helper") || basename.includes("util") || basename.includes("generator")) {
        categories.helper.push(f.file);
    }
    // Files that look like services (broadcaster, analyzer, coordinator, etc.)
    else if (
        basename.includes("analyzer") ||
        basename.includes("broadcaster") ||
        basename.includes("coordinator") ||
        basename.includes("discovery") ||
        basename.includes("loader") ||
        basename.includes("renderer") ||
        basename.includes("server")
    ) {
        categories.service.push(f.file);
    }
    // Everything else
    else {
        categories.other.push(f.file);
    }
});

console.log("=== MAIN/INDEX FILES (entry points, special case):", categories.main.length);
categories.main.forEach((f) => console.log("  " + f));

console.log("\n=== PRELOAD FILES (Electron preload scripts):", categories.preload.length);
categories.preload.forEach((f) => console.log("  " + f));

console.log("\n=== MIGRATION FILES (database migrations):", categories.migration.length);
categories.migration.slice(0, 10).forEach((f) => console.log("  " + f));
if (categories.migration.length > 10) {
    console.log(`  ... and ${categories.migration.length - 10} more`);
}

console.log("\n=== PATTERN FILES (pty-analyzer patterns):", categories.pattern.length);
categories.pattern.forEach((f) => console.log("  " + f));

console.log("\n=== LIKELY HELPERS (suggest -helper):", categories.helper.length);
categories.helper.slice(0, 15).forEach((f) => console.log("  " + f));

console.log("\n=== LIKELY SERVICES (suggest -service):", categories.service.length);
categories.service.slice(0, 15).forEach((f) => console.log("  " + f));

console.log("\n=== OTHER (needs manual review):", categories.other.length);
categories.other.slice(0, 30).forEach((f) => console.log("  " + f));
if (categories.other.length > 30) {
    console.log(`  ... and ${categories.other.length - 30} more`);
}
