#!/usr/bin/env node

/**
 * ARCHLAB Checklist Forensic Verifier
 *
 * This script performs systematic verification of ARCHLAB-CHECKLIST-COMPLETE.md
 * against all planning documents, DEV-RULES.md, and existing codebase.
 */

const fs = require('fs');
const path = require('path');

const BASE_DIR = 'D:\\GIT\\archlab\\root\\archlab-ide';
const ROOT_DIR = 'D:\\GIT\\archlab';

// File paths
const FILES = {
  checklist: path.join(BASE_DIR, 'ARCHLAB-CHECKLIST-COMPLETE.md'),
  devRules: path.join(ROOT_DIR, 'DEV-RULES.md'),
  planning: [
    path.join(BASE_DIR, 'PLANNING.md'),
    path.join(BASE_DIR, 'PLANNING.v2.md'),
    path.join(BASE_DIR, 'PLANNING.v2.1-SECURITY-PERSISTENCE.md'),
    path.join(BASE_DIR, 'PLANNING.v2.2-CHECKPOINTS-PASSKEYS.md'),
    path.join(BASE_DIR, 'PLANNING.v2.3-AUTHORITY-INVARIANTS.md'),
    path.join(BASE_DIR, 'PLANNING.v2.3-INVARIANTS-CONTINUED.md'),
    path.join(BASE_DIR, 'PLANNING.v2.4-REGISTRY-HUB.md'),
  ],
  srcDir: path.join(BASE_DIR, 'src'),
};

// Results object
const results = {
  executiveSummary: {
    overallStatus: 'PASS',
    criticalIssues: 0,
    highIssues: 0,
    mediumIssues: 0,
    lowIssues: 0,
  },
  namingConventionAudit: [],
  patternCoverageMatrix: [],
  lawComplianceMatrix: [],
  invariantComplianceMatrix: [],
  devRulesAlignment: [],
  existingCodeRefactoring: [],
  dependencyGraph: [],
  gaps: [],
  violations: [],
  recommendations: [],
};

// Naming convention rules from DEV-RULES.md
const NAMING_RULES = {
  F1: { pattern: /^[a-z][a-z0-9]*(-[a-z0-9]+)*$/, description: 'kebab-case for files' },
  F2: { description: 'suffix derived from folder name' },
  F3: { description: 'base/core prefix for abstract/generic files' },
  C1: { pattern: /^[A-Z][a-zA-Z0-9]*$/, description: 'PascalCase for classes matching filename' },
};

/**
 * Read file with error handling
 */
function readFile(filepath) {
  try {
    return fs.readFileSync(filepath, 'utf-8');
  } catch (err) {
    console.error(`Error reading ${filepath}:`, err.message);
    return null;
  }
}

/**
 * Parse checklist into structured data
 */
function parseChecklist(content) {
  const lines = content.split('\n');
  const tasks = [];
  let currentPhase = null;
  let currentTask = null;

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];

    // Detect phase headers
    if (line.match(/^## PHASE \d+:/)) {
      currentPhase = line.replace(/^## /, '').trim();
      continue;
    }

    // Detect task items
    const taskMatch = line.match(/^- \[ \] (.+)/);
    if (taskMatch) {
      currentTask = {
        phase: currentPhase,
        description: taskMatch[1],
        lineNumber: i + 1,
        files: [],
        dependencies: [],
      };
      tasks.push(currentTask);
      continue;
    }

    // Extract file paths from task
    if (currentTask) {
      const fileMatch = line.match(/`([^`]+\.(ts|js|md))`/g);
      if (fileMatch) {
        fileMatch.forEach(match => {
          const filepath = match.replace(/`/g, '');
          currentTask.files.push(filepath);
        });
      }

      // Extract dependencies
      const depMatch = line.match(/Requires: (.+)/);
      if (depMatch) {
        currentTask.dependencies = depMatch[1].split(',').map(d => d.trim());
      }
    }
  }

  return tasks;
}

/**
 * Extract existing TypeScript files
 */
function getExistingFiles(srcDir) {
  const files = [];

  function walk(dir) {
    if (!fs.existsSync(dir)) return;

    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        walk(fullPath);
      } else if (entry.name.endsWith('.ts')) {
        files.push(fullPath);
      }
    }
  }

  walk(srcDir);
  return files;
}

/**
 * Verify naming convention for a file path
 */
function verifyNamingConvention(filepath) {
  const basename = path.basename(filepath, path.extname(filepath));
  const dirname = path.basename(path.dirname(filepath));

  const issues = [];

  // F1: kebab-case check
  if (!NAMING_RULES.F1.pattern.test(basename)) {
    issues.push({
      rule: 'F1',
      severity: 'HIGH',
      message: `Filename "${basename}" violates kebab-case rule`,
    });
  }

  // F2: Check if suffix matches folder (heuristic)
  if (!basename.includes(dirname.toLowerCase()) && dirname !== 'src') {
    // This is a heuristic - may need refinement
    issues.push({
      rule: 'F2',
      severity: 'MEDIUM',
      message: `Filename "${basename}" does not contain folder suffix "${dirname}"`,
    });
  }

  return issues;
}

/**
 * Extract patterns from planning documents
 */
function extractPatterns(planningDocs) {
  const patterns = new Set();

  planningDocs.forEach(doc => {
    const content = readFile(doc);
    if (!content) return;

    // Look for pattern declarations (heuristic extraction)
    const patternMatches = content.match(/Pattern[:\s]+([A-Z][a-zA-Z0-9]+)/g);
    if (patternMatches) {
      patternMatches.forEach(match => {
        const pattern = match.replace(/Pattern[:\s]+/, '');
        patterns.add(pattern);
      });
    }

    // Also look for explicit lists of patterns
    const lines = content.split('\n');
    for (let i = 0; i < lines.length; i++) {
      if (lines[i].match(/^\d+\.\s+[A-Z][a-zA-Z]+/)) {
        const pattern = lines[i].replace(/^\d+\.\s+/, '').split(/[:\s-]/)[0];
        patterns.add(pattern);
      }
    }
  });

  return Array.from(patterns);
}

/**
 * Extract laws from planning documents
 */
function extractLaws(planningDocs) {
  const laws = [];

  planningDocs.forEach(doc => {
    const content = readFile(doc);
    if (!content) return;

    const lawMatches = content.match(/Law \d+:[^\n]+/g);
    if (lawMatches) {
      lawMatches.forEach(law => laws.push(law));
    }
  });

  return laws;
}

/**
 * Extract invariants from planning documents
 */
function extractInvariants(planningDocs) {
  const invariants = [];

  planningDocs.forEach(doc => {
    const content = readFile(doc);
    if (!content) return;

    const invMatches = content.match(/Invariant \d+:[^\n]+/g);
    if (invMatches) {
      invMatches.forEach(inv => invariants.push(inv));
    }
  });

  return invariants;
}

/**
 * Main verification function
 */
function verify() {
  console.log('Starting ARCHLAB Checklist Forensic Verification...\n');

  // 1. Load all documents
  console.log('Loading documents...');
  const checklistContent = readFile(FILES.checklist);
  const devRulesContent = readFile(FILES.devRules);

  if (!checklistContent || !devRulesContent) {
    console.error('Failed to load required documents');
    process.exit(1);
  }

  // 2. Parse checklist
  console.log('Parsing checklist...');
  const tasks = parseChecklist(checklistContent);
  console.log(`  Found ${tasks.length} tasks`);

  // 3. Get existing files
  console.log('Scanning existing codebase...');
  const existingFiles = getExistingFiles(FILES.srcDir);
  console.log(`  Found ${existingFiles.length} TypeScript files`);

  // 4. Extract patterns, laws, invariants
  console.log('Extracting requirements from planning documents...');
  const patterns = extractPatterns(FILES.planning);
  const laws = extractLaws(FILES.planning);
  const invariants = extractInvariants(FILES.planning);
  console.log(`  Patterns: ${patterns.length}`);
  console.log(`  Laws: ${laws.length}`);
  console.log(`  Invariants: ${invariants.length}`);

  // 5. Naming convention audit
  console.log('\nAuditing naming conventions...');
  existingFiles.forEach(filepath => {
    const issues = verifyNamingConvention(filepath);
    if (issues.length > 0) {
      results.namingConventionAudit.push({
        file: filepath,
        issues: issues,
      });

      issues.forEach(issue => {
        if (issue.severity === 'CRITICAL') results.executiveSummary.criticalIssues++;
        if (issue.severity === 'HIGH') results.executiveSummary.highIssues++;
        if (issue.severity === 'MEDIUM') results.executiveSummary.mediumIssues++;
        if (issue.severity === 'LOW') results.executiveSummary.lowIssues++;
      });
    }
  });

  // 6. Pattern coverage analysis
  console.log('Analyzing pattern coverage...');
  patterns.forEach(pattern => {
    const covered = tasks.some(task =>
      task.description.toLowerCase().includes(pattern.toLowerCase())
    );

    results.patternCoverageMatrix.push({
      pattern: pattern,
      covered: covered,
      status: covered ? 'VERIFIED' : 'MISSING',
    });

    if (!covered) {
      results.gaps.push({
        type: 'PATTERN',
        item: pattern,
        severity: 'MEDIUM',
        message: `Pattern "${pattern}" not found in checklist tasks`,
      });
      results.executiveSummary.mediumIssues++;
    }
  });

  // 7. Law coverage analysis
  console.log('Analyzing law compliance...');
  laws.forEach(law => {
    results.lawComplianceMatrix.push({
      law: law,
      status: 'PENDING_MANUAL_REVIEW',
    });
  });

  // 8. Invariant coverage analysis
  console.log('Analyzing invariant compliance...');
  invariants.forEach(invariant => {
    results.invariantComplianceMatrix.push({
      invariant: invariant,
      status: 'PENDING_MANUAL_REVIEW',
    });
  });

  // 9. Existing code refactoring audit
  console.log('Auditing existing code refactoring tasks...');
  existingFiles.forEach(filepath => {
    const relativePath = filepath.replace(FILES.srcDir, 'src');
    const hasRefactorTask = tasks.some(task =>
      task.files.some(f => f.includes(path.basename(filepath)))
    );

    if (!hasRefactorTask) {
      results.existingCodeRefactoring.push({
        file: relativePath,
        status: 'NO_REFACTOR_TASK',
        severity: 'MEDIUM',
        message: `Existing file has no refactoring task in checklist`,
      });
      results.executiveSummary.mediumIssues++;
    }
  });

  // 10. Set overall status
  if (results.executiveSummary.criticalIssues > 0) {
    results.executiveSummary.overallStatus = 'FAIL';
  } else if (results.executiveSummary.highIssues > 0) {
    results.executiveSummary.overallStatus = 'WARN';
  }

  return results;
}

/**
 * Generate report
 */
function generateReport(results) {
  const report = [];

  report.push('# ARCHLAB CHECKLIST VERIFICATION REPORT\n');
  report.push('**Generated:** ' + new Date().toISOString() + '\n');
  report.push('**Verifier:** checklist-analyzer.js\n\n');

  // Executive Summary
  report.push('## 1. EXECUTIVE SUMMARY\n');
  report.push(`**Overall Status:** ${results.executiveSummary.overallStatus}\n\n`);
  report.push('**Issue Breakdown:**\n');
  report.push(`- Critical: ${results.executiveSummary.criticalIssues}\n`);
  report.push(`- High: ${results.executiveSummary.highIssues}\n`);
  report.push(`- Medium: ${results.executiveSummary.mediumIssues}\n`);
  report.push(`- Low: ${results.executiveSummary.lowIssues}\n\n`);

  // Naming Convention Audit
  report.push('## 2. NAMING CONVENTION AUDIT\n\n');
  if (results.namingConventionAudit.length === 0) {
    report.push('✅ All files comply with naming conventions\n\n');
  } else {
    report.push(`❌ Found ${results.namingConventionAudit.length} files with naming issues:\n\n`);
    results.namingConventionAudit.forEach(item => {
      report.push(`### ${item.file}\n\n`);
      item.issues.forEach(issue => {
        report.push(`- **[${issue.severity}]** Rule ${issue.rule}: ${issue.message}\n`);
      });
      report.push('\n');
    });
  }

  // Pattern Coverage Matrix
  report.push('## 3. PATTERN COVERAGE MATRIX\n\n');
  report.push('| Pattern | Status | Coverage |\n');
  report.push('|---------|--------|----------|\n');
  results.patternCoverageMatrix.forEach(item => {
    const icon = item.covered ? '✅' : '❌';
    report.push(`| ${item.pattern} | ${icon} | ${item.status} |\n`);
  });
  report.push('\n');

  // Gaps Identified
  report.push('## 9. GAPS IDENTIFIED\n\n');
  if (results.gaps.length === 0) {
    report.push('✅ No gaps identified\n\n');
  } else {
    results.gaps.forEach(gap => {
      report.push(`### [${gap.severity}] ${gap.type}: ${gap.item}\n\n`);
      report.push(`**Message:** ${gap.message}\n\n`);
    });
  }

  // Existing Code Refactoring Audit
  report.push('## 7. EXISTING CODE REFACTORING AUDIT\n\n');
  if (results.existingCodeRefactoring.length === 0) {
    report.push('✅ All existing files have refactoring tasks\n\n');
  } else {
    report.push(`⚠️ Found ${results.existingCodeRefactoring.length} files without refactoring tasks:\n\n`);
    results.existingCodeRefactoring.forEach(item => {
      report.push(`- **${item.file}**: ${item.message}\n`);
    });
    report.push('\n');
  }

  return report.join('');
}

// Execute verification
const verificationResults = verify();

// Generate report
const reportContent = generateReport(verificationResults);

// Write report to file
const reportPath = path.join(BASE_DIR, 'CHECKLIST-VERIFICATION-REPORT.md');
fs.writeFileSync(reportPath, reportContent, 'utf-8');

console.log('\n✅ Verification complete!');
console.log(`📄 Report written to: ${reportPath}`);
console.log(`\nStatus: ${verificationResults.executiveSummary.overallStatus}`);
console.log(`Issues: ${verificationResults.executiveSummary.criticalIssues + verificationResults.executiveSummary.highIssues + verificationResults.executiveSummary.mediumIssues + verificationResults.executiveSummary.lowIssues} total`);

// Exit with appropriate code
process.exit(verificationResults.executiveSummary.overallStatus === 'FAIL' ? 1 : 0);
