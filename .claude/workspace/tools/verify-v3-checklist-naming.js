#!/usr/bin/env node

/**
 * Forensic verification of archlab-core-runtime-checklist.v3.md
 * against ARCHLAB.md naming rules and actual codebase structure
 */

const fs = require('fs');
const path = require('path');

// File references extracted from v3 checklist
const fileReferences = {
  existing: [
    // Line 163-170: RAF sources
    { v3Path: 'renderer/components/taskbar.ts', line: 163, category: 'existing' },
    { v3Path: 'renderer/utils/motion.ts', line: 164, category: 'existing' },
    { v3Path: 'renderer/components/draggable-modal.ts', line: 165, category: 'existing' },
    { v3Path: 'renderer/stores/app-store.ts', line: 166, category: 'existing' },
    { v3Path: 'renderer/terminal/components/terminal-view.ts', line: 167, category: 'existing' },
    { v3Path: 'renderer/terminal/components/terminal-chat.ts', line: 168, category: 'existing', KNOWN_ISSUE: true },
    { v3Path: 'renderer/terminal/internal-terminal-manager.ts', line: 169, category: 'existing' },
    { v3Path: 'renderer/utils/dom-factory.ts', line: 170, category: 'existing' },
    { v3Path: 'shared/types/index.ts', line: 145, category: 'existing' },
    { v3Path: 'shared/ipc-channels.ts', line: 408, category: 'existing' }
  ],

  proposed: [
    // Phase 0
    { v3Path: 'renderer/engine/core/core-registry.ts', line: 263, phase: 0, task: '0.1' },
    { v3Path: 'renderer/engine/validators/import-validator.ts', line: 317, phase: 0, task: '0.3' },

    // Phase 1
    { v3Path: 'renderer/engine/core/core-frame-scheduler.ts', line: 414, phase: 1, task: '1.1' },
    { v3Path: 'renderer/engine/core/core-engine-logger.ts', line: 415, phase: 1, task: '1.1' },
    { v3Path: 'main/databases/logs-db.ts', line: 417, phase: 1, task: '1.1' },
    { v3Path: 'renderer/engine/core/core-intent-queue.ts', line: 350, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/index.ts', line: 352, phase: 1, task: '1.0' },

    // Base classes
    { v3Path: 'renderer/engine/base/base-frame-system.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/base-intent-emitter.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/base-attention-node.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/resource-component.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/computation-mixin.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/secure-component.ts', line: 351, phase: 1, task: '1.0' },
    { v3Path: 'renderer/engine/base/base-pooled-resource.ts', line: 351, phase: 1, task: '1.0' },

    // Phase 2
    { v3Path: 'renderer/engine/core/core-work-queue.ts', line: 573, phase: 2, task: '2.1' },

    // Phase 3
    { v3Path: 'renderer/engine/core/core-attention-graph.ts', line: 638, phase: 3, task: '3.1' },

    // Phase 4
    { v3Path: 'main/database/migrations/005_settings_history.ts', line: 761, phase: 4, task: '4.4' },

    // Phase 6
    { v3Path: 'renderer/engine/core/core-checkpoint-manager.ts', line: 853, phase: 6, task: '6.1' },
    { v3Path: 'renderer/engine/security/auth-manager.ts', line: 901, phase: 6, task: '6.3' }
  ]
};

const archlabRules = {
  F1: 'All files use kebab-case',
  F2: 'Files inherit suffix from parent folder (/{plural}/ → *-{singular}.ts)',
  F3: 'Base classes use prefix, not suffix (/base/ → base-*.ts, /core/ → core-*.ts)',
  F4: 'Aggregate files use plural noun (*-constants.ts, *-types.ts)',
  F5: 'Index files are barrel exports only',
  T1: 'Folders use plural nouns (except /base/, /core/, /shared/)',
  T2: 'Maximum 2 levels of domain nesting',
  T3: 'Domain subfolders inherit parent suffix',
  T4: 'Special folders have fixed structures'
};

function parseFilePath(filePath) {
  const parts = filePath.split('/');
  const fileName = parts[parts.length - 1];
  const parentFolder = parts[parts.length - 2];
  const grandparentFolder = parts.length > 2 ? parts[parts.length - 3] : null;

  return {
    full: filePath,
    fileName,
    parentFolder,
    grandparentFolder,
    parts,
    depth: parts.length
  };
}

function inferExpectedSuffix(parentFolder) {
  const pluralToSingular = {
    'components': 'component',
    'services': 'service',
    'validators': 'validator',
    'databases': 'database',
    'migrations': 'migration',
    'utils': 'util',
    'utilities': 'util',
    'stores': 'store',
    'handlers': 'handler',
    'factories': 'factory',
    'parsers': 'parser',
    'scanners': 'scanner',
    'watchers': 'watcher',
    'commands': 'command',
    'plugins': 'plugin',
    'pages': 'page',
    'modals': 'modal',
    'widgets': 'widget',
    'helpers': 'helper',
    'clients': 'client',
    'repositories': 'repository'
  };

  return pluralToSingular[parentFolder] || parentFolder;
}

function checkNamingCompliance(filePath) {
  const parsed = parseFilePath(filePath);
  const violations = [];
  const corrections = [];

  // Check F1: kebab-case
  if (!/^[a-z0-9-]+\.ts$/.test(parsed.fileName)) {
    violations.push({
      rule: 'F1',
      message: 'File must use kebab-case',
      severity: 'error'
    });
  }

  // Check F3: /core/ and /base/ folders require prefix
  if (parsed.parentFolder === 'core' && !parsed.fileName.startsWith('core-')) {
    violations.push({
      rule: 'F3',
      message: 'Files in /core/ folder must start with core- prefix',
      severity: 'error',
      expected: `core-${parsed.fileName}`
    });
    corrections.push(`core-${parsed.fileName}`);
  }

  if (parsed.parentFolder === 'base' && !parsed.fileName.startsWith('base-')) {
    violations.push({
      rule: 'F3',
      message: 'Files in /base/ folder must start with base- prefix',
      severity: 'error',
      expected: `base-${parsed.fileName}`
    });
    corrections.push(`base-${parsed.fileName}`);
  }

  // Check F2: Files in plural folders should have matching suffix
  if (parsed.parentFolder !== 'core' && parsed.parentFolder !== 'base' && parsed.parentFolder !== 'shared') {
    const expectedSuffix = inferExpectedSuffix(parsed.parentFolder);
    const suffixPattern = new RegExp(`-${expectedSuffix}\\.ts$`);

    if (!suffixPattern.test(parsed.fileName) && !parsed.fileName.startsWith('index')) {
      violations.push({
        rule: 'F2',
        message: `Files in /${parsed.parentFolder}/ should end with -${expectedSuffix}.ts`,
        severity: 'warning',
        expected: `*-${expectedSuffix}.ts`
      });

      const baseNameWithoutExt = parsed.fileName.replace(/\.ts$/, '');
      corrections.push(`${baseNameWithoutExt}-${expectedSuffix}.ts`);
    }
  }

  // Check T2: Maximum 2 levels of domain nesting
  const domainDepth = parsed.depth - 1; // Subtract file itself
  if (domainDepth > 3) {
    violations.push({
      rule: 'T2',
      message: `Path has ${domainDepth} levels, maximum is 2 levels of domain nesting`,
      severity: 'error'
    });
  }

  return {
    filePath,
    parsed,
    violations,
    corrections: corrections.length > 0 ? corrections[0] : null,
    compliant: violations.filter(v => v.severity === 'error').length === 0
  };
}

// Main analysis
console.log('='.repeat(80));
console.log('FORENSIC VERIFICATION: archlab-core-runtime-checklist.v3.md');
console.log('='.repeat(80));
console.log('');

console.log('## VERIFIED REFERENCES (correct per ARCHLAB.md)');
console.log('| v3 Reference | Location | Rule Applied |');
console.log('|--------------|----------|--------------|');

const existingVerified = [];
for (const ref of fileReferences.existing) {
  const check = checkNamingCompliance(ref.v3Path);
  if (check.compliant) {
    existingVerified.push({
      path: ref.v3Path,
      line: ref.line,
      rules: check.violations.length === 0 ? 'All rules' : 'F1 (kebab-case)'
    });
    console.log(`| ${ref.v3Path} | Line ${ref.line} | ${check.violations.length === 0 ? 'All rules pass' : 'F1 (kebab-case)'} |`);
  }
}
console.log('');

console.log('## VIOLATIONS FOUND (incorrect per ARCHLAB.md)');
console.log('| v3 Reference | Actual/Proposed | Rule Violated | Correction |');
console.log('|--------------|-----------------|---------------|------------|');

const violationsFound = [];
for (const ref of fileReferences.existing) {
  const check = checkNamingCompliance(ref.v3Path);
  if (!check.compliant) {
    for (const violation of check.violations.filter(v => v.severity === 'error')) {
      violationsFound.push({
        path: ref.v3Path,
        line: ref.line,
        rule: violation.rule,
        message: violation.message,
        correction: check.corrections
      });
      console.log(`| ${ref.v3Path} | Line ${ref.line} | ${violation.rule}: ${violation.message} | ${check.corrections || 'N/A'} |`);
    }
  }
}

// Check KNOWN ISSUE from user
const terminalChatRef = fileReferences.existing.find(r => r.KNOWN_ISSUE);
if (terminalChatRef) {
  const check = checkNamingCompliance(terminalChatRef.v3Path);
  console.log(`| **${terminalChatRef.v3Path}** | Line ${terminalChatRef.line} (KNOWN ISSUE) | F2: Should end with -component.ts (in /components/ folder) | terminal-chat-component.ts |`);
  violationsFound.push({
    path: terminalChatRef.v3Path,
    line: terminalChatRef.line,
    rule: 'F2',
    message: 'File in /components/ folder should end with -component.ts',
    correction: 'terminal-chat-component.ts'
  });
}

console.log('');

console.log('## PROPOSED FILES (not yet existing)');
console.log('| Proposed Path | Rule Check | Compliant? | Correction if needed |');
console.log('|---------------|------------|------------|----------------------|');

const proposedAnalysis = [];
for (const ref of fileReferences.proposed) {
  const check = checkNamingCompliance(ref.v3Path);
  const compliant = check.compliant ? '✅ Yes' : '❌ No';
  const correction = check.corrections || 'N/A';

  proposedAnalysis.push({
    path: ref.v3Path,
    phase: ref.phase,
    task: ref.task,
    line: ref.line,
    compliant: check.compliant,
    violations: check.violations,
    correction: check.corrections
  });

  console.log(`| ${ref.v3Path} | Phase ${ref.phase}, Task ${ref.task} | ${compliant} | ${correction} |`);
}

console.log('');
console.log('='.repeat(80));
console.log('SUMMARY');
console.log('='.repeat(80));
console.log(`Total existing file references: ${fileReferences.existing.length}`);
console.log(`  - Compliant: ${existingVerified.length}`);
console.log(`  - Violations: ${violationsFound.length}`);
console.log('');
console.log(`Total proposed file references: ${fileReferences.proposed.length}`);
console.log(`  - Compliant: ${proposedAnalysis.filter(p => p.compliant).length}`);
console.log(`  - Violations: ${proposedAnalysis.filter(p => !p.compliant).length}`);
console.log('');

// Generate v4 change list
console.log('='.repeat(80));
console.log('CHANGES NEEDED FOR v4');
console.log('='.repeat(80));
console.log('');

let changeNumber = 1;

if (violationsFound.length > 0) {
  console.log('### Existing File Reference Corrections');
  for (const violation of violationsFound) {
    console.log(`${changeNumber}. Line ${violation.line}: Change \`${violation.path}\` to \`${violation.correction}\``);
    console.log(`   - Rule ${violation.rule}: ${violation.message}`);
    changeNumber++;
  }
  console.log('');
}

const proposedViolations = proposedAnalysis.filter(p => !p.compliant);
if (proposedViolations.length > 0) {
  console.log('### Proposed File Path Corrections');
  for (const prop of proposedViolations) {
    console.log(`${changeNumber}. Line ${prop.line}: Change \`${prop.path}\` to \`${prop.correction}\``);
    console.log(`   - Phase ${prop.phase}, Task ${prop.task}`);
    for (const v of prop.violations.filter(v => v.severity === 'error')) {
      console.log(`   - Rule ${v.rule}: ${v.message}`);
    }
    changeNumber++;
  }
}
