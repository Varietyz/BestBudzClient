/**
 * Extract language support from all scanners
 * Maps scanners to their supported languages via getSupportedLanguages()
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

async function extractLanguageSupport() {
    const allScanners = await findAllScanners(SCANNERS_ROOT);
    const results = {};

    for (const scannerPath of allScanners) {
        try {
            const module = await import(`file://${scannerPath}`);
            const ScannerClass = module.default;

            if (ScannerClass) {
                const instance = new ScannerClass();
                const name = instance.getName();
                const languages = instance.getSupportedLanguages();

                results[name] = {
                    path: scannerPath.replace(/\\/g, "/"),
                    languages: languages,
                };
            }
        } catch (error) {
            // Skip scanners that can't be imported
        }
    }

    return results;
}

const languageMap = await extractLanguageSupport();

// Group by language
const byLanguage = {};
for (const [scanner, info] of Object.entries(languageMap)) {
    for (const lang of info.languages) {
        if (!byLanguage[lang]) {
            byLanguage[lang] = [];
        }
        byLanguage[lang].push(scanner);
    }
}

console.log("\n=== SCANNERS BY LANGUAGE ===\n");
for (const [lang, scanners] of Object.entries(byLanguage).sort()) {
    console.log(`${lang}: ${scanners.length} scanners`);
    console.log(`  ${scanners.join(", ")}`);
    console.log();
}

console.log("\n=== MULTI-LANGUAGE SCANNERS ===\n");
for (const [scanner, info] of Object.entries(languageMap)) {
    if (info.languages.length > 1) {
        console.log(`${scanner}: ${info.languages.join(", ")}`);
    }
}
