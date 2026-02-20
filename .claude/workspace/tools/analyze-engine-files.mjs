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

// Categorize engine files
const categories = {
    migrations: [],
    preload: [],
    main_entry: [],
    icons: [],
    modal_form: [],
    pools: [],
    sinks: [],
    compression: [],
    core: [],
    culling: [],
    debug: [],
    validators: [],
    services: [],
    router: [],
    stores: [],
    other: [],
};

remaining.forEach((f) => {
    const basename = path.basename(f.file, ".ts");
    const filepath = f.file;

    if (filepath.includes("/migrations/")) categories.migrations.push(f.file);
    else if (basename === "preload") categories.preload.push(f.file);
    else if (basename === "main") categories.main_entry.push(f.file);
    else if (basename === "icons" || basename === "form-controls") categories.icons.push(f.file);
    else if (basename === "modal") categories.modal_form.push(f.file);
    else if (filepath.includes("/pools/")) categories.pools.push(f.file);
    else if (filepath.includes("/sinks/")) categories.sinks.push(f.file);
    else if (filepath.includes("/compression/")) categories.compression.push(f.file);
    else if (filepath.includes("/engine/core/")) categories.core.push(f.file);
    else if (filepath.includes("/culling/")) categories.culling.push(f.file);
    else if (filepath.includes("/debug/") && !filepath.includes("/sinks/")) categories.debug.push(f.file);
    else if (filepath.includes("/validators/")) categories.validators.push(f.file);
    else if (filepath.includes("/services/") || basename.includes("-service")) categories.services.push(f.file);
    else if (filepath.includes("/router/")) categories.router.push(f.file);
    else if (filepath.includes("/stores/")) categories.stores.push(f.file);
    else categories.other.push(f.file);
});

console.log("=== MIGRATIONS:", categories.migrations.length);
categories.migrations.forEach((f) => console.log("  " + f));

console.log("\n=== PRELOAD:", categories.preload.length);
categories.preload.forEach((f) => console.log("  " + f));

console.log("\n=== MAIN ENTRY:", categories.main_entry.length);
categories.main_entry.forEach((f) => console.log("  " + f));

console.log("\n=== ICONS/FORM-CONTROLS:", categories.icons.length);
categories.icons.forEach((f) => console.log("  " + f));

console.log("\n=== MODAL/FORM:", categories.modal_form.length);
categories.modal_form.forEach((f) => console.log("  " + f));

console.log("\n=== POOLS:", categories.pools.length);
categories.pools.forEach((f) => console.log("  " + f));

console.log("\n=== SINKS:", categories.sinks.length);
categories.sinks.forEach((f) => console.log("  " + f));

console.log("\n=== COMPRESSION:", categories.compression.length);
categories.compression.forEach((f) => console.log("  " + f));

console.log("\n=== CORE (attention, frame, intent):", categories.core.length);
categories.core.forEach((f) => console.log("  " + f));

console.log("\n=== CULLING:", categories.culling.length);
categories.culling.forEach((f) => console.log("  " + f));

console.log("\n=== DEBUG:", categories.debug.length);
categories.debug.forEach((f) => console.log("  " + f));

console.log("\n=== VALIDATORS:", categories.validators.length);
categories.validators.slice(0, 20).forEach((f) => console.log("  " + f));
if (categories.validators.length > 20) console.log(`  ... and ${categories.validators.length - 20} more`);

console.log("\n=== SERVICES:", categories.services.length);
categories.services.forEach((f) => console.log("  " + f));

console.log("\n=== ROUTER:", categories.router.length);
categories.router.forEach((f) => console.log("  " + f));

console.log("\n=== STORES:", categories.stores.length);
categories.stores.forEach((f) => console.log("  " + f));

console.log("\n=== OTHER:", categories.other.length);
categories.other.slice(0, 15).forEach((f) => console.log("  " + f));
if (categories.other.length > 15) console.log(`  ... and ${categories.other.length - 15} more`);
