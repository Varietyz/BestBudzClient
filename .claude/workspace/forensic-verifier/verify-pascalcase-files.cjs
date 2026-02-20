#!/usr/bin/env node
/**
 * Forensic verification script for PascalCase TypeScript files
 * Discovers ALL PascalCase .ts files in archlab-ide/src/ and compares against checklist
 */

const fs = require('fs');
const path = require('path');

// Claimed violations from the prerequisite checklist
const CLAIMED_VIOLATIONS = [
  'Database.ts',
  'MigrationRunner.ts',
  'SettingsStore.ts',
  'HeaderBar.ts',
  'Taskbar.ts',
  'Toast.ts',
  'Popover.ts',
  'SettingsPanel.ts',
  'StatusIndicator.ts',
  'Router.ts',
  'DOMFactory.ts',
  'AppStore.ts',
  'GraphicsService.ts'
];

/**
 * Check if a filename is PascalCase
 * Pattern: Starts with uppercase letter, contains at least one more letter
 */
function isPascalCase(filename) {
  const nameWithoutExt = filename.replace(/\.[^.]+$/, '');

  // Ignore index.ts, features.ts, ipc-handlers.ts (kebab-case)
  if (nameWithoutExt === 'index' || nameWithoutExt.includes('-')) {
    return false;
  }

  // PascalCase pattern: starts with uppercase and has at least one uppercase letter
  return /^[A-Z][a-zA-Z]*[A-Z]/.test(nameWithoutExt) || /^[A-Z][a-z]+$/.test(nameWithoutExt);
}

/**
 * Recursively find all .ts files
 */
function findTsFiles(dir, fileList = []) {
  if (!fs.existsSync(dir)) {
    return fileList;
  }

  const files = fs.readdirSync(dir);

  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);

    if (stat.isDirectory()) {
      // Skip node_modules
      if (file !== 'node_modules') {
        findTsFiles(filePath, fileList);
      }
    } else if (file.endsWith('.ts') && !file.endsWith('.d.ts')) {
      fileList.push(filePath);
    }
  });

  return fileList;
}

/**
 * Main verification logic
 */
function verify() {
  const srcDir = path.join(__dirname, '../../../root/archlab-ide/src');

  console.log('FORENSIC VERIFICATION: PascalCase TypeScript Files');
  console.log('='.repeat(70));
  console.log(`Source directory: ${srcDir}`);
  console.log('');

  if (!fs.existsSync(srcDir)) {
    console.log('ERROR: Source directory does not exist');
    console.log(`Path: ${srcDir}`);
    process.exit(1);
  }

  // Find all .ts files
  const allTsFiles = findTsFiles(srcDir);
  console.log(`Total .ts files found: ${allTsFiles.length}`);
  console.log('');

  // Filter for PascalCase files
  const pascalCaseFiles = allTsFiles.filter(filePath => {
    const filename = path.basename(filePath);
    return isPascalCase(filename);
  });

  console.log('DISCOVERED PASCALCASE FILES:');
  console.log('-'.repeat(70));

  const discovered = {};
  pascalCaseFiles.forEach(filePath => {
    const filename = path.basename(filePath);
    const relativePath = path.relative(srcDir, filePath);
    discovered[filename] = relativePath;
    console.log(`  ${filename} → ${relativePath}`);
  });

  console.log('');
  console.log(`Total PascalCase files discovered: ${pascalCaseFiles.length}`);
  console.log('');

  // Verification against claimed violations
  console.log('VERIFICATION AGAINST CHECKLIST:');
  console.log('-'.repeat(70));

  const verified = [];
  const refuted = [];
  const missed = [];

  // Check claimed violations
  CLAIMED_VIOLATIONS.forEach(claimedFile => {
    if (discovered[claimedFile]) {
      verified.push({
        file: claimedFile,
        path: discovered[claimedFile],
        status: 'VERIFIED'
      });
    } else {
      refuted.push({
        file: claimedFile,
        status: 'REFUTED - FILE NOT FOUND'
      });
    }
  });

  // Find missed violations (discovered but not in checklist)
  Object.keys(discovered).forEach(discoveredFile => {
    if (!CLAIMED_VIOLATIONS.includes(discoveredFile)) {
      missed.push({
        file: discoveredFile,
        path: discovered[discoveredFile],
        status: 'MISSED - NOT IN CHECKLIST'
      });
    }
  });

  // Output results
  console.log('');
  console.log('VERIFIED CLAIMS (files exist as claimed):');
  verified.forEach(item => {
    console.log(`  ✓ ${item.file} at ${item.path}`);
  });

  console.log('');
  console.log('REFUTED CLAIMS (files do NOT exist):');
  if (refuted.length === 0) {
    console.log('  (none)');
  } else {
    refuted.forEach(item => {
      console.log(`  ✗ ${item.file} - ${item.status}`);
    });
  }

  console.log('');
  console.log('MISSED VIOLATIONS (PascalCase files NOT in checklist):');
  if (missed.length === 0) {
    console.log('  (none)');
  } else {
    missed.forEach(item => {
      console.log(`  ! ${item.file} at ${item.path}`);
    });
  }

  console.log('');
  console.log('SUMMARY:');
  console.log('-'.repeat(70));
  console.log(`  Claimed violations: ${CLAIMED_VIOLATIONS.length}`);
  console.log(`  Verified: ${verified.length}`);
  console.log(`  Refuted: ${refuted.length}`);
  console.log(`  Missed: ${missed.length}`);
  console.log(`  Total discovered: ${pascalCaseFiles.length}`);
  console.log('');

  // Output JSON for programmatic consumption
  const report = {
    timestamp: new Date().toISOString(),
    sourceDir: srcDir,
    totalTsFiles: allTsFiles.length,
    totalPascalCase: pascalCaseFiles.length,
    claimed: CLAIMED_VIOLATIONS.length,
    verified: verified,
    refuted: refuted,
    missed: missed
  };

  const reportPath = path.join(__dirname, 'verification-report.json');
  fs.writeFileSync(reportPath, JSON.stringify(report, null, 2));
  console.log(`Detailed report written to: ${reportPath}`);

  // Exit code: 0 if perfect match, 1 if discrepancies
  process.exit((refuted.length > 0 || missed.length > 0) ? 1 : 0);
}

verify();
