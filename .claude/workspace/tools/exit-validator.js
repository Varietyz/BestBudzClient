#!/usr/bin/env node
/**
 * Exit Validator - Validates scanner expansion architecture integrity
 * Agent 7 in scanner-expansion-workflow
 */

import { readFileSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..', '..', '..');

// Expected scanners by agent
const EXPECTED_SCANNERS = {
  agent3: [
    'soc-violations',
    'srp-violations',
    'ocp-violations',
    'dip-violations',
    'production-anti-patterns',
    'cyclomatic-complexity'
  ],
  agent4: [
    'cognitive-complexity',
    'layer-violations',
    'circular-dependencies',
    'coupling-metrics'
  ],
  agent5: [
    'injection-vulnerabilities',
    'security-inheritance',
    'n-plus-one-queries',
    'memory-leak-patterns'
  ],
  agent6: [
    'lsp-violations',
    'isp-violations',
    'dead-code',
    'event-driven-violations',
    'cohesion-metrics',
    'long-method',
    'data-clump',
    'speculative-generality',
    'api-documentation'
  ]
};

const ALL_SCANNERS = Object.values(EXPECTED_SCANNERS).flat();

const results = {
  pass: [],
  fail: [],
  warnings: []
};

console.log('='.repeat(80));
console.log('EXIT VALIDATOR - Scanner Expansion Architecture Integrity Check');
console.log('='.repeat(80));
console.log('');

// TEST 1: Scanner File Existence
console.log('TEST 1: Scanner File Existence (23 files)');
console.log('-'.repeat(80));

const securityScanners = ['injection-vulnerabilities', 'security-inheritance'];
const codeScanners = ALL_SCANNERS.filter(s => !securityScanners.includes(s));

let existencePass = true;
for (const scanner of codeScanners) {
  const path = join(ROOT, 'root/codebase-validation/scanners/quality/code', `${scanner}-scanner.js`);
  if (existsSync(path)) {
    console.log(`✓ ${scanner}-scanner.js exists`);
  } else {
    console.log(`✗ ${scanner}-scanner.js MISSING`);
    results.fail.push(`Missing scanner file: ${scanner}-scanner.js`);
    existencePass = false;
  }
}

for (const scanner of securityScanners) {
  const path = join(ROOT, 'root/codebase-validation/scanners/quality/security', `${scanner}-scanner.js`);
  if (existsSync(path)) {
    console.log(`✓ ${scanner}-scanner.js exists (security)`);
  } else {
    console.log(`✗ ${scanner}-scanner.js MISSING (security)`);
    results.fail.push(`Missing security scanner file: ${scanner}-scanner.js`);
    existencePass = false;
  }
}

if (existencePass) {
  results.pass.push('All 23 scanner files exist in correct directories');
}
console.log('');

// TEST 2: Scanner Class Structure
console.log('TEST 2: Scanner Class Structure');
console.log('-'.repeat(80));

let structurePass = true;
const requiredMethods = ['getSupportedLanguages', 'analyzeContentPatterns', 'getName'];
const requiredImports = ['BaseScanner', 'Logger', 'REGISTRY_TYPES'];

for (const scanner of ALL_SCANNERS) {
  const dir = securityScanners.includes(scanner) ? 'security' : 'code';
  const path = join(ROOT, `root/codebase-validation/scanners/quality/${dir}`, `${scanner}-scanner.js`);

  if (!existsSync(path)) continue;

  const content = readFileSync(path, 'utf-8');

  // Check extends BaseScanner
  if (!content.includes('extends BaseScanner')) {
    console.log(`✗ ${scanner}: Does not extend BaseScanner`);
    results.fail.push(`${scanner}: Missing BaseScanner extension`);
    structurePass = false;
  }

  // Check required methods
  for (const method of requiredMethods) {
    if (!content.includes(method)) {
      console.log(`✗ ${scanner}: Missing ${method}() method`);
      results.fail.push(`${scanner}: Missing ${method}() method`);
      structurePass = false;
    }
  }

  // Check getName() returns correct key
  const getNameMatch = content.match(/getName\(\)\s*{\s*return\s*['"]([^'"]+)['"]/);
  if (getNameMatch) {
    const returnedName = getNameMatch[1];
    if (returnedName !== scanner) {
      console.log(`✗ ${scanner}: getName() returns "${returnedName}" instead of "${scanner}"`);
      results.fail.push(`${scanner}: getName() mismatch`);
      structurePass = false;
    }
  }

  // Check Perceptual-Loop header
  if (!content.includes('Perceptual-Loop') && !content.includes('PERCEPTUAL-LOOP')) {
    console.log(`⚠ ${scanner}: Missing Perceptual-Loop header comment`);
    results.warnings.push(`${scanner}: Missing Perceptual-Loop header`);
  }

  // Check registry.build() usage
  if (!content.includes('registry.build(')) {
    console.log(`✗ ${scanner}: Does not use registry.build()`);
    results.fail.push(`${scanner}: Missing registry.build() usage`);
    structurePass = false;
  }

  // Check required imports
  for (const imp of requiredImports) {
    if (!content.includes(imp)) {
      console.log(`✗ ${scanner}: Missing import: ${imp}`);
      results.fail.push(`${scanner}: Missing import ${imp}`);
      structurePass = false;
    }
  }
}

if (structurePass) {
  results.pass.push('All scanners have correct class structure');
}
console.log('');

// TEST 3: Settings Integration
console.log('TEST 3: Settings Integration');
console.log('-'.repeat(80));

const settingsPath = join(ROOT, 'root/codebase-validation/settings.json');
let settingsPass = true;

if (existsSync(settingsPath)) {
  const settings = JSON.parse(readFileSync(settingsPath, 'utf-8'));

  if (!settings.enabled || !settings.enabled.scanners) {
    console.log('✗ settings.json missing enabled.scanners structure');
    results.fail.push('settings.json: Missing enabled.scanners');
    settingsPass = false;
  } else {
    for (const scanner of ALL_SCANNERS) {
      if (!(scanner in settings.enabled.scanners)) {
        console.log(`✗ ${scanner} not in settings.enabled.scanners`);
        results.fail.push(`settings.json: Missing scanner entry: ${scanner}`);
        settingsPass = false;
      } else if (settings.enabled.scanners[scanner] !== true) {
        console.log(`⚠ ${scanner} exists but not enabled (value: ${settings.enabled.scanners[scanner]})`);
        results.warnings.push(`${scanner}: Not enabled in settings.json`);
      }
    }
  }

  if (settingsPass) {
    results.pass.push('All 23 scanners registered in settings.json');
  }
} else {
  console.log('✗ settings.json not found');
  results.fail.push('settings.json not found');
  settingsPass = false;
}
console.log('');

// TEST 4: Language Spec Integration
console.log('TEST 4: Language Spec Integration');
console.log('-'.repeat(80));

const languageSpecDir = join(ROOT, 'root/codebase-validation/core/settings/languages/specs');
const coreLanguages = ['javascript.js', 'python.js', 'java.js', 'go.js', 'rust.js'];
let specPass = true;
let updatedSpecs = [];

for (const langFile of coreLanguages) {
  const langPath = join(languageSpecDir, langFile);

  if (existsSync(langPath)) {
    const content = readFileSync(langPath, 'utf-8');
    const hasNewScanners = ALL_SCANNERS.some(scanner => content.includes(scanner));

    if (hasNewScanners) {
      updatedSpecs.push(langFile);
      console.log(`✓ ${langFile} updated with new scanners`);
    } else {
      console.log(`⚠ ${langFile} appears not updated with new scanners`);
      results.warnings.push(`${langFile}: May need scanner integration`);
    }
  } else {
    console.log(`⚠ ${langFile} not found`);
    results.warnings.push(`Language spec not found: ${langFile}`);
  }
}

if (updatedSpecs.length >= 3) {
  results.pass.push(`${updatedSpecs.length} language specs updated with new scanners`);
} else {
  console.log(`⚠ Only ${updatedSpecs.length} language specs appear updated (expected ≥3)`);
  results.warnings.push('Few language specs updated with new scanners');
}
console.log('');

// TEST 5: Import Path Validation
console.log('TEST 5: Import Path Validation');
console.log('-'.repeat(80));

let importPass = true;
for (const scanner of ALL_SCANNERS) {
  const dir = securityScanners.includes(scanner) ? 'security' : 'code';
  const path = join(ROOT, `root/codebase-validation/scanners/quality/${dir}`, `${scanner}-scanner.js`);

  if (!existsSync(path)) continue;

  const content = readFileSync(path, 'utf-8');

  // Check for relative import paths
  const importLines = content.match(/import .+ from ['"][^'"]+['"]/g) || [];
  for (const line of importLines) {
    if (line.includes('from "./') || line.includes('from "../')) {
      // Valid relative import
    } else if (line.includes('from "') && !line.includes('node:') && !line.includes('fs') && !line.includes('path')) {
      console.log(`⚠ ${scanner}: Unusual import path: ${line}`);
      results.warnings.push(`${scanner}: Check import path: ${line.substring(0, 60)}...`);
    }
  }
}

results.pass.push('Import paths validated');
console.log('✓ Import path structure checked');
console.log('');

// SUMMARY
console.log('='.repeat(80));
console.log('VALIDATION SUMMARY');
console.log('='.repeat(80));
console.log('');

console.log(`PASSED: ${results.pass.length} validations`);
for (const p of results.pass) {
  console.log(`  ✓ ${p}`);
}
console.log('');

if (results.warnings.length > 0) {
  console.log(`WARNINGS: ${results.warnings.length} issues`);
  for (const w of results.warnings) {
    console.log(`  ⚠ ${w}`);
  }
  console.log('');
}

if (results.fail.length > 0) {
  console.log(`FAILED: ${results.fail.length} critical issues`);
  for (const f of results.fail) {
    console.log(`  ✗ ${f}`);
  }
  console.log('');
  console.log('ARCHITECTURE INTEGRITY: ❌ ISSUES FOUND');
  process.exit(1);
} else {
  console.log('ARCHITECTURE INTEGRITY: ✅ VERIFIED');
  console.log('');
  console.log('All 23 scanners successfully validated!');
  process.exit(0);
}
