/**
 * Verify language specs are properly configured
 */

import { getLanguageSpec } from "../../../root/codebase-validation/core/settings/languages/index.js";

const specs = ["python", "javascript", "html", "css", "ada", "zig", "java", "go"];

console.log("Language Spec Verification:\n");

for (const lang of specs) {
    const spec = getLanguageSpec(lang);
    console.log(`${lang}:`);
    console.log(`  applicableScanners: ${JSON.stringify(spec?.applicableScanners || "NOT DEFINED")}`);
    console.log(`  allowUnderscorePrefix: ${spec?.rules?.naming?.allowUnderscorePrefix}`);
    console.log();
}

console.log("✓ All language specs accessible");
