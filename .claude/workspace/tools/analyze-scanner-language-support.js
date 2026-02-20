/**
 * Analyze scanner language support
 * Determine which scanners support Go, Java, Python, Rust, C++ and need fixtures
 */

import { fileURLToPath } from "url";
import path from "path";
import fs from "fs/promises";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const SCANNERS_ROOT = path.resolve(__dirname, "../../../root/codebase-validation/scanners");

async function findAllScanners(dir) {
    const scanners = [];
    const entries = await fs.readdir(dir, { withFileTypes: true });

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            scanners.push(...(await findAllScanners(fullPath)));
        } else if (entry.name.endsWith("-scanner.js")) {
            scanners.push(fullPath);
        }
    }

    return scanners;
}

async function analyzeScannerFile(filePath) {
    const content = await fs.readFile(filePath, "utf-8");
    const nameMatch = content.match(/getName\(\)\s*{[\s\S]*?return\s+["']([^"']+)["'];/);
    const langMatch = content.match(/getSupportedLanguages\(\)\s*{[\s\S]*?return\s+(\[[\s\S]*?\]|null);/);

    if (!nameMatch) return null;

    const name = nameMatch[1];
    let languages = null;

    if (langMatch) {
        const langString = langMatch[1];
        if (langString === "null") {
            languages = null; // All languages
        } else {
            // Extract languages from array literal
            const langArrayMatch = langString.match(/"([^"]+)"/g);
            if (langArrayMatch) {
                languages = langArrayMatch.map((s) => s.replace(/"/g, ""));
            }
        }
    }

    return { name, languages, path: filePath.replace(/\\/g, "/") };
}

async function main() {
    const allScanners = await findAllScanners(SCANNERS_ROOT);
    const results = [];

    for (const scannerPath of allScanners) {
        const info = await analyzeScannerFile(scannerPath);
        if (info) {
            results.push(info);
        }
    }

    // Target languages for fixture creation
    const targetLanguages = ["go", "java", "python", "rust", "cpp", "c++", "csharp"];

    console.log("\n=== SCANNERS SUPPORTING TARGET LANGUAGES ===\n");

    for (const lang of targetLanguages) {
        const scannersForLang = results.filter((s) => {
            if (s.languages === null) return true; // null = all languages
            return s.languages && s.languages.includes(lang);
        });

        console.log(`\n${lang.toUpperCase()}: ${scannersForLang.length} scanners`);

        if (scannersForLang.length > 0) {
            // Categorize
            const allLang = scannersForLang.filter((s) => s.languages === null);
            const specific = scannersForLang.filter((s) => s.languages !== null);

            if (allLang.length > 0) {
                console.log(`  Universal scanners (work on all languages): ${allLang.length}`);
                allLang.forEach((s) => console.log(`    - ${s.name}`));
            }

            if (specific.length > 0) {
                console.log(`  Language-specific scanners: ${specific.length}`);
                specific.forEach((s) => console.log(`    - ${s.name} (${s.languages.join(", ")})`));
            }
        }
    }

    // Check CSS and HTML scanners
    console.log("\n\n=== WEB LANGUAGE SCANNERS ===\n");
    const cssCount = results.filter((s) => s.languages && s.languages.includes("css")).length;
    const htmlCount = results.filter((s) => s.languages && s.languages.includes("html")).length;
    console.log(`CSS: ${cssCount} scanners`);
    console.log(`HTML: ${htmlCount} scanners`);

    // Summary
    console.log("\n\n=== LANGUAGE SUPPORT SUMMARY ===\n");
    const allLangScanners = results.filter((s) => s.languages === null);
    console.log(`Universal scanners (null = all languages): ${allLangScanners.length}`);
    console.log("These work with Go, Java, Python, Rust, C++, and any other language\n");

    const jsOnlyScanners = results.filter((s) => s.languages && s.languages.includes("javascript"));
    console.log(`JavaScript/TypeScript-specific scanners: ${jsOnlyScanners.length}`);
}

main().catch(console.error);
