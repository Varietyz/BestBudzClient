#!/usr/bin/env node

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const violationsPath = path.join(__dirname, "architecture-violations-pag.json");
const data = JSON.parse(fs.readFileSync(violationsPath, "utf8"));

const medium = data.files.filter((f) => f.violations.some((v) => v.code === "ROLE_NOT_DETECTED"));

// Group by pattern
const patterns = {};
medium.forEach((f) => {
    const basename = path.basename(f.file, ".ts");

    // Detect pattern
    let pattern = "other";
    if (basename.endsWith("-db")) pattern = "db";
    else if (basename.includes("-ipc-handlers") || basename === "ipc-handlers") pattern = "ipc-handlers";
    else if (basename.includes("-handlers") || basename === "runtime-handlers") pattern = "handlers";
    else if (basename.includes("-runner")) pattern = "runner";
    else if (basename.includes("-bridge")) pattern = "bridge";
    else if (basename.includes("-engine")) pattern = "engine";
    else if (f.file.includes("/migrations/")) pattern = "migration";
    else if (basename.includes("preload")) pattern = "preload";

    if (!patterns[pattern]) patterns[pattern] = [];
    patterns[pattern].push(f.file);
});

console.log("Pattern analysis:");
Object.entries(patterns).forEach(([k, v]) => {
    console.log(`  ${k}: ${v.length} files`);
});

console.log("\n=== DB files (suggest -database):");
(patterns.db || []).forEach((f) => console.log("  " + f));

console.log("\n=== IPC handler files (suggest composite *-ipc-handler or keep as *-handler):");
(patterns["ipc-handlers"] || []).forEach((f) => console.log("  " + f));

console.log("\n=== Handler files (suggest *-handler):");
(patterns.handlers || []).slice(0, 10).forEach((f) => console.log("  " + f));

console.log("\n=== Runner files (suggest -service):");
(patterns.runner || []).forEach((f) => console.log("  " + f));

console.log("\n=== Bridge files (suggest -service):");
(patterns.bridge || []).forEach((f) => console.log("  " + f));

console.log("\n=== Engine files (suggest -service or -manager):");
(patterns.engine || []).forEach((f) => console.log("  " + f));

console.log("\n=== Migration files (special case):");
(patterns.migration || []).slice(0, 15).forEach((f) => console.log("  " + f));

console.log("\n=== Other files (need manual analysis):");
console.log("  Total:", (patterns.other || []).length);
(patterns.other || []).slice(0, 20).forEach((f) => console.log("  " + f));
