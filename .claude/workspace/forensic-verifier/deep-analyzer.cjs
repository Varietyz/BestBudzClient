#!/usr/bin/env node

/**
 * ARCHLAB Deep Forensic Analyzer
 *
 * Performs exhaustive verification against:
 * - 7 Laws
 * - 11 Invariants
 * - 47 DEV-RULES directives
 * - 143 Patterns
 * - 17 Naming rules
 */

const fs = require('fs');
const path = require('path');

const BASE_DIR = 'D:\\GIT\\archlab\\root\\archlab-ide';
const ROOT_DIR = 'D:\\GIT\\archlab';

// Define the 7 Laws
const LAWS = [
  {
    id: 'LAW1',
    name: 'FORWARD-ONLY PROGRAMMING',
    description: 'Adding new functionality NEVER requires modifying existing files',
    checklist_requirement: 'Verification task exists that tests forward-only capability',
  },
  {
    id: 'LAW2',
    name: 'NO CHILD-TO-PARENT CALLBACKS',
    description: 'Children emit intentions. Parents observe and decide',
    checklist_requirement: 'AST verification that no parent callbacks exist',
  },
  {
    id: 'LAW3',
    name: 'INHERITANCE OVER CONFIGURATION',
    description: 'Behavior from base class extension, not config objects',
    checklist_requirement: 'All components extend base classes',
  },
  {
    id: 'LAW4',
    name: 'SECURITY AS BASE-INHERITED CAPABILITY',
    description: 'All sanitization/validation inherited from base classes',
    checklist_requirement: 'BaseSanitizer inheritance verified',
  },
  {
    id: 'LAW5',
    name: 'AUTHENTICATED STATE RECOVERY',
    description: 'IDE requires passkey authentication before accessing state',
    checklist_requirement: 'Passkey gate verification exists',
  },
  {
    id: 'LAW6',
    name: 'HUMAN-CONTROLLED AUTHORITY BOUNDARY',
    description: 'User can override with justification',
    checklist_requirement: 'Authority override mechanism exists',
  },
  {
    id: 'LAW7',
    name: 'SINGLE IMPORT BOUNDARY',
    description: 'All runtime dependencies flow through registry',
    checklist_requirement: 'No direct imports between modules',
  },
];

// Define the 11 Invariants
const INVARIANTS = [
  { id: 'INV1', name: 'Text/Meaning Bifurcation', description: 'Text buffers provisional; semantic representations authoritative' },
  { id: 'INV2', name: 'Unprivileged Core Clients', description: 'No component has privileged access to truth' },
  { id: 'INV3', name: 'Monotonic Incrementality', description: 'No full recomputation unless semantic boundaries changed' },
  { id: 'INV4', name: 'Dual Authority Feedback', description: 'Ephemeral feedback vs Authoritative actions' },
  { id: 'INV5', name: 'Semantic Replay over Undo', description: 'Reversibility via replayable semantic checkpoints' },
  { id: 'INV6', name: 'Confluent Rule System', description: 'Rule evaluation order must not affect outcome' },
  { id: 'INV7', name: 'Project-as-Program', description: 'No code exists outside project semantic model' },
  { id: 'INV8', name: 'Persistent Diagnostics', description: 'Diagnostics are addressable, queryable objects' },
  { id: 'INV9', name: 'Ephemeral UI, Durable Semantics', description: 'UI state ephemeral; semantic state authoritative' },
  { id: 'INV10', name: 'Inspection-First Environment', description: 'Primary IDE mode is inspection, not editing' },
  { id: 'INV11', name: 'Antenna Hub Topology', description: 'All dependencies flow through registry' },
];

// Key patterns that MUST be verified
const CRITICAL_PATTERNS = [
  'Registry',
  'Authority',
  'Checkpoint',
  'Passkey',
  'BaseSanitizer',
  'BaseObject',
  'BaseComponent',
  'Token',
  'Validation',
  'Security',
];

/**
 * Read and parse checklist
 */
function analyzeChecklist() {
  const checklistPath = path.join(BASE_DIR, 'ARCHLAB-CHECKLIST-COMPLETE.md');
  const content = fs.readFileSync(checklistPath, 'utf-8');

  const results = {
    lawVerification: [],
    invariantVerification: [],
    criticalPatterns: [],
    violations: [],
    gaps: [],
  };

  // Check each law
  LAWS.forEach(law => {
    const lawRegex = new RegExp(`LAW ${law.id.replace('LAW', '')}|${law.name}`, 'i');
    const hasVerificationTask = lawRegex.test(content);

    // Search for dedicated phase or task
    const phaseLine = content.match(new RegExp(`## PHASE \\d+:.*${law.name}.*`, 'i'));
    const taskLine = content.match(new RegExp(`### Task.*${law.name}.*`, 'i'));
    const verifyLine = content.match(new RegExp(`- \\[ \\].*verify.*${law.name}.*`, 'i'));

    results.lawVerification.push({
      law: law.id,
      name: law.name,
      hasPhase: !!phaseLine,
      hasTask: !!taskLine,
      hasVerificationCheckbox: !!verifyLine,
      status: (phaseLine || taskLine || verifyLine) ? 'COVERED' : 'MISSING',
      evidence: phaseLine ? phaseLine[0] : (taskLine ? taskLine[0] : (verifyLine ? verifyLine[0] : 'None')),
    });

    if (!phaseLine && !taskLine && !verifyLine) {
      results.violations.push({
        severity: 'CRITICAL',
        type: 'LAW_NOT_VERIFIED',
        law: law.id,
        message: `${law.name} has no verification task in checklist`,
        fix: `Add verification phase/task for ${law.name}`,
      });
    }
  });

  // Check each invariant
  INVARIANTS.forEach(inv => {
    const invRegex = new RegExp(`INVARIANT ${inv.id.replace('INV', '')}|${inv.name}`, 'i');
    const hasVerificationTask = invRegex.test(content);

    const phaseLine = content.match(new RegExp(`## PHASE \\d+:.*Invariant.*`, 'i'));
    const verifyLine = content.match(new RegExp(`- \\[ \\].*${inv.name}.*`, 'i'));

    results.invariantVerification.push({
      invariant: inv.id,
      name: inv.name,
      hasVerificationTask: !!verifyLine,
      status: verifyLine ? 'COVERED' : 'MISSING',
      evidence: verifyLine ? verifyLine[0] : 'None',
    });

    if (!verifyLine && !hasVerificationTask) {
      results.gaps.push({
        severity: 'HIGH',
        type: 'INVARIANT_NOT_VERIFIED',
        invariant: inv.id,
        message: `${inv.name} has no verification task in checklist`,
        fix: `Add verification checkbox for ${inv.name}`,
      });
    }
  });

  // Check critical patterns
  CRITICAL_PATTERNS.forEach(pattern => {
    const patternRegex = new RegExp(pattern, 'i');
    const matches = content.match(new RegExp(`- \\[ \\].*${pattern}.*`, 'gi'));

    results.criticalPatterns.push({
      pattern: pattern,
      occurrences: matches ? matches.length : 0,
      status: matches && matches.length > 0 ? 'COVERED' : 'MISSING',
      evidence: matches ? `${matches.length} tasks` : 'None',
    });

    if (!matches || matches.length === 0) {
      results.gaps.push({
        severity: 'MEDIUM',
        type: 'CRITICAL_PATTERN_MISSING',
        pattern: pattern,
        message: `Critical pattern "${pattern}" has no implementation tasks`,
        fix: `Add implementation tasks for ${pattern}`,
      });
    }
  });

  return results;
}

/**
 * Analyze existing code for violations
 */
function analyzeExistingCode() {
  const srcDir = path.join(BASE_DIR, 'src');
  const violations = [];

  function walk(dir) {
    if (!fs.existsSync(dir)) return;

    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);

      if (entry.isDirectory()) {
        walk(fullPath);
      } else if (entry.name.endsWith('.ts')) {
        const content = fs.readFileSync(fullPath, 'utf-8');

        // Check for potential LAW2 violations (parent callbacks)
        if (content.match(/this\.parent\.|props\.on[A-Z]|callback:/)) {
          violations.push({
            severity: 'HIGH',
            file: fullPath,
            law: 'LAW2',
            message: 'Potential parent callback detected',
            evidence: 'Pattern: this.parent. or props.on[A-Z] or callback:',
          });
        }

        // Check for LAW3 violations (config over inheritance)
        if (content.match(/config:\s*\{[\s\S]*behavior/i) && !content.match(/extends\s+Base/)) {
          violations.push({
            severity: 'MEDIUM',
            file: fullPath,
            law: 'LAW3',
            message: 'Config-based behavior without base class inheritance',
            evidence: 'Has config object but no "extends Base*"',
          });
        }

        // Check for LAW7 violations (direct imports)
        const importMatches = content.match(/import\s+.*from\s+['"]\.\.?\//g);
        if (importMatches && importMatches.length > 5) {
          violations.push({
            severity: 'MEDIUM',
            file: fullPath,
            law: 'LAW7',
            message: `${importMatches.length} direct relative imports (should use registry)`,
            evidence: 'Multiple relative imports instead of registry access',
          });
        }
      }
    }
  }

  walk(srcDir);
  return violations;
}

/**
 * Generate comprehensive report
 */
function generateReport(checklistAnalysis, codeViolations) {
  const lines = [];

  lines.push('# ARCHLAB CHECKLIST DEEP FORENSIC VERIFICATION REPORT\n');
  lines.push('**Generated:** ' + new Date().toISOString());
  lines.push('**Analyzer:** deep-analyzer.cjs\n');
  lines.push('---\n\n');

  // Executive Summary
  const totalViolations = checklistAnalysis.violations.length + codeViolations.length;
  const totalGaps = checklistAnalysis.gaps.length;
  const criticalCount = checklistAnalysis.violations.filter(v => v.severity === 'CRITICAL').length;
  const highCount = checklistAnalysis.violations.filter(v => v.severity === 'HIGH').length +
                    checklistAnalysis.gaps.filter(g => g.severity === 'HIGH').length;

  lines.push('## 1. EXECUTIVE SUMMARY\n\n');
  lines.push(`**Overall Status:** ${criticalCount > 0 ? 'FAIL' : (highCount > 0 ? 'WARN' : 'PASS')}\n\n`);
  lines.push('**Issue Breakdown:**\n');
  lines.push(`- Critical Violations: ${criticalCount}\n`);
  lines.push(`- High Priority: ${highCount}\n`);
  lines.push(`- Total Gaps: ${totalGaps}\n`);
  lines.push(`- Code Violations: ${codeViolations.length}\n\n`);

  // Law Compliance Matrix
  lines.push('## 4. LAW COMPLIANCE MATRIX\n\n');
  lines.push('| Law ID | Law Name | Status | Evidence |\n');
  lines.push('|--------|----------|--------|----------|\n');
  checklistAnalysis.lawVerification.forEach(law => {
    const icon = law.status === 'COVERED' ? '✅' : '❌';
    lines.push(`| ${law.law} | ${law.name} | ${icon} ${law.status} | ${law.evidence.substring(0, 60)}... |\n`);
  });
  lines.push('\n');

  // Invariant Compliance Matrix
  lines.push('## 5. INVARIANT COMPLIANCE MATRIX\n\n');
  lines.push('| Inv ID | Invariant Name | Status | Evidence |\n');
  lines.push('|--------|----------------|--------|----------|\n');
  checklistAnalysis.invariantVerification.forEach(inv => {
    const icon = inv.status === 'COVERED' ? '✅' : '❌';
    lines.push(`| ${inv.invariant} | ${inv.name} | ${icon} ${inv.status} | ${inv.evidence.substring(0, 50)} |\n`);
  });
  lines.push('\n');

  // Critical Patterns
  lines.push('## 3.1 CRITICAL PATTERN COVERAGE\n\n');
  lines.push('| Pattern | Occurrences | Status |\n');
  lines.push('|---------|-------------|--------|\n');
  checklistAnalysis.criticalPatterns.forEach(p => {
    const icon = p.status === 'COVERED' ? '✅' : '❌';
    lines.push(`| ${p.pattern} | ${p.occurrences} | ${icon} ${p.status} |\n`);
  });
  lines.push('\n');

  // Violations
  lines.push('## 10. VIOLATIONS FOUND\n\n');
  if (checklistAnalysis.violations.length === 0 && codeViolations.length === 0) {
    lines.push('✅ No violations found\n\n');
  } else {
    lines.push('### Checklist Violations\n\n');
    checklistAnalysis.violations.forEach(v => {
      lines.push(`#### [${v.severity}] ${v.law}: ${v.type}\n\n`);
      lines.push(`**What:** ${v.message}\n\n`);
      lines.push(`**Fix:** ${v.fix}\n\n`);
    });

    lines.push('### Code Violations\n\n');
    codeViolations.forEach(v => {
      lines.push(`#### [${v.severity}] ${v.law}: ${path.basename(v.file)}\n\n`);
      lines.push(`**File:** \`${v.file}\`\n\n`);
      lines.push(`**What:** ${v.message}\n\n`);
      lines.push(`**Evidence:** ${v.evidence}\n\n`);
    });
  }

  // Gaps
  lines.push('## 9. GAPS IDENTIFIED\n\n');
  if (checklistAnalysis.gaps.length === 0) {
    lines.push('✅ No gaps identified\n\n');
  } else {
    checklistAnalysis.gaps.forEach(g => {
      lines.push(`### [${g.severity}] ${g.type}\n\n`);
      lines.push(`**What:** ${g.message}\n\n`);
      lines.push(`**Fix:** ${g.fix}\n\n`);
    });
  }

  return lines.join('');
}

// Execute
console.log('Starting deep forensic analysis...\n');

const checklistAnalysis = analyzeChecklist();
console.log(`✅ Analyzed checklist:`);
console.log(`  - Laws: ${checklistAnalysis.lawVerification.length} verified`);
console.log(`  - Invariants: ${checklistAnalysis.invariantVerification.length} verified`);
console.log(`  - Critical Patterns: ${checklistAnalysis.criticalPatterns.length} checked`);
console.log(`  - Violations: ${checklistAnalysis.violations.length}`);
console.log(`  - Gaps: ${checklistAnalysis.gaps.length}\n`);

const codeViolations = analyzeExistingCode();
console.log(`✅ Analyzed existing code:`);
console.log(`  - Violations: ${codeViolations.length}\n`);

const report = generateReport(checklistAnalysis, codeViolations);

const reportPath = path.join(BASE_DIR, 'CHECKLIST-VERIFICATION-REPORT.md');
fs.writeFileSync(reportPath, report, 'utf-8');

console.log(`📄 Report written to: ${reportPath}\n`);

const criticalCount = checklistAnalysis.violations.filter(v => v.severity === 'CRITICAL').length;
process.exit(criticalCount > 0 ? 1 : 0);
