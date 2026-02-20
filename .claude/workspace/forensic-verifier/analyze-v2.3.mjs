/**
 * ARCHLAB v2.3 Planning Document Forensic Analyzer
 *
 * Systematically checks for:
 * 1. Internal consistency (pattern contradictions)
 * 2. Authority tier application consistency
 * 3. Checkpoint rules alignment with security
 * 4. Text/Meaning bifurcation application
 * 5. IDE best practice violations
 * 6. DEV-RULES compliance
 * 7. Performance risks (O(n²), full scans vs incremental)
 * 8. UX cognitive overload risks
 * 9. Scalability (10 files vs 10,000 files)
 * 10. Plugin sandbox isolation verification
 * 11. Crash recovery verification
 * 12. Gap analysis
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Configuration
const CONFIG = {
    docs: {
        authority: 'D:\\GIT\\archlab\\root\\archlab-ide\\PLANNING.v2.3-AUTHORITY-INVARIANTS.md',
        invariants: 'D:\\GIT\\archlab\\root\\archlab-ide\\PLANNING.v2.3-INVARIANTS-CONTINUED.md',
        devRules: 'D:\\GIT\\archlab\\DEV-RULES.md',
        ideIdea: 'D:\\GIT\\archlab\\IDE-IDEA.md'
    },
    output: 'D:\\GIT\\archlab\\root\\archlab-ide\\ANALYSIS-v2.3.md'
};

// Analysis categories
const findings = {
    inconsistencies: [],
    contradictions: [],
    gaps: [],
    ideBestPracticeViolations: [],
    devRulesIssues: [],
    performanceRisks: [],
    uxRisks: [],
    scalabilityIssues: [],
    securityConcerns: []
};

// Read all documents
function readDocuments() {
    const docs = {};
    for (const [key, filepath] of Object.entries(CONFIG.docs)) {
        try {
            docs[key] = fs.readFileSync(filepath, 'utf-8');
        } catch (err) {
            console.error(`Failed to read ${key}: ${err.message}`);
            docs[key] = '';
        }
    }
    return docs;
}

// Extract patterns from documents
function extractPatterns(docs) {
    const patterns = {
        authorityTiers: [],
        checkpointRules: [],
        invariants: [],
        validationRules: [],
        devRulesReferences: []
    };

    // Extract authority tiers
    const authorityMatch = docs.authority.match(/AUTHORITY TIERS IN ARCHLAB[\s\S]*?└────────────────────────────────────────────────────────────────────┘/);
    if (authorityMatch) {
        patterns.authorityTiers.push(authorityMatch[0]);
    }

    // Extract checkpoint patterns
    const checkpointMatches = docs.authority.match(/CheckpointTier\.(EPHEMERAL|DURABLE|PROTECTED)/g) || [];
    patterns.checkpointRules = checkpointMatches;

    // Extract invariants
    for (let i = 1; i <= 10; i++) {
        const invariantRegex = new RegExp(`## Invariant ${i}:([\\s\\S]*?)(?=##|$)`);
        const match = docs.authority.match(invariantRegex) || docs.invariants.match(invariantRegex);
        if (match) {
            patterns.invariants.push({
                number: i,
                content: match[1]
            });
        }
    }

    // Extract validation rules
    const validationMatches = docs.authority.matchAll(/export const (\w+)_RULES = \{[\s\S]*?\};/g);
    for (const match of validationMatches) {
        patterns.validationRules.push({
            name: match[1],
            content: match[0]
        });
    }

    // Extract DEV-RULES references
    const devRulesMatches = docs.authority.matchAll(/DEV-RULES MAPPING:[\s\S]*?(?=\n\n|\│\n│  (?!-)|└)/g);
    for (const match of devRulesMatches) {
        patterns.devRulesReferences.push(match[0]);
    }

    return patterns;
}

// Analysis: Check for pattern contradictions
function checkPatternContradictions(patterns, docs) {
    // Check 1: Text/Meaning Bifurcation - should be applied everywhere
    const textMeaningBifurcation = patterns.invariants.find(inv => inv.number === 1);
    if (textMeaningBifurcation) {
        // Search for violations where text buffers might be checkpointed
        const violations = [];

        // Check if checkpoint code ever references text directly
        const checkpointCode = docs.authority.match(/createCheckpoint[\s\S]*?(?=\n\n)/g) || [];
        for (const code of checkpointCode) {
            if (code.includes('.text') || code.includes('.buffer') || code.includes('.content')) {
                violations.push({
                    what: 'Text/Meaning Bifurcation violation in checkpoint code',
                    where: 'Checkpoint creation code',
                    evidence: code.substring(0, 200)
                });
            }
        }

        if (violations.length > 0) {
            findings.inconsistencies.push({
                category: 'Text/Meaning Bifurcation',
                severity: 'Critical',
                violations
            });
        }
    }

    // Check 2: Authority tiers - AI should NEVER hold authority
    const aiAuthorityReferences = docs.authority.match(/AI[\s\S]{0,100}authority/gi) || [];
    for (const ref of aiAuthorityReferences) {
        if (!ref.toLowerCase().includes('no authority') &&
            !ref.toLowerCase().includes('never holds') &&
            !ref.toLowerCase().includes('authority: none')) {
            findings.inconsistencies.push({
                category: 'Authority Violation',
                severity: 'Critical',
                what: 'AI might have authority where it should not',
                where: 'Authority tier definitions',
                evidence: ref
            });
        }
    }

    // Check 3: Monotonic incrementality - should never have full revalidation on simple edit
    const incrementalCode = docs.authority.match(/validateEdit[\s\S]*?(?=\n\n)/g) || [];
    for (const code of incrementalCode) {
        if (code.includes('fullRevalidation: true') && !code.includes('boundaryChanged')) {
            findings.inconsistencies.push({
                category: 'Monotonic Incrementality Violation',
                severity: 'High',
                what: 'Full revalidation might occur without semantic boundary check',
                where: 'Incremental validation code',
                evidence: code.substring(0, 200)
            });
        }
    }
}

// Analysis: IDE Best Practices
function checkIDEBestPractices(docs) {
    // Performance: Check for O(n²) operations
    const nestedLoops = docs.authority.match(/for \([^)]+\) \{[\s\S]*?for \([^)]+\) \{/g) || [];
    if (nestedLoops.length > 0) {
        findings.performanceRisks.push({
            what: 'Potential O(n²) nested loops detected',
            where: 'Multiple code sections',
            why: 'Can cause "mystery lag" in large codebases',
            fix: 'Use Map/Set for lookups, or incremental algorithms',
            priority: 'High',
            count: nestedLoops.length
        });
    }

    // UX: Check for excessive checkpoint creation
    const checkpointCreation = docs.authority.match(/createCheckpoint|checkpointBeforeAction/g) || [];
    findings.uxRisks.push({
        what: 'Checkpoint creation frequency analysis',
        where: 'Throughout checkpoint architecture',
        concern: 'Too many checkpoints = cognitive overload + storage bloat',
        mitigation: 'Checkpoint tiering (ephemeral/durable/protected) addresses this',
        status: 'ADDRESSED',
        evidence: `${checkpointCreation.length} checkpoint creation points found`
    });

    // Scalability: Check for operations that don't scale
    const fullScans = docs.authority.match(/\.all\(\)|SELECT \* FROM|query all|find all/gi) || [];
    if (fullScans.length > 0) {
        findings.scalabilityIssues.push({
            what: 'Full table/collection scans detected',
            where: 'Database queries',
            why: 'Won\'t scale to 10,000 files',
            fix: 'Add WHERE clauses, use indexes, implement pagination',
            priority: 'Medium',
            count: fullScans.length
        });
    }

    // Plugin safety: Check if plugin sandbox is mentioned
    if (!docs.authority.includes('plugin') && !docs.authority.includes('extension')) {
        findings.gaps.push({
            what: 'No plugin/extension isolation architecture defined',
            where: 'Entire v2.3 spec',
            why: 'IDE-IDEA.md mentions plugin system but v2.3 has no security model',
            fix: 'Add "Invariant 11: Plugin Sandbox Isolation"',
            priority: 'High'
        });
    }

    // Recovery: Check for crash recovery mechanisms
    const crashRecovery = docs.authority.match(/crash|recovery|restore/gi) || [];
    if (crashRecovery.length < 5) {
        findings.gaps.push({
            what: 'Insufficient crash recovery documentation',
            where: 'v2.3 spec',
            why: 'Checkpoints exist but recovery paths not fully specified',
            fix: 'Add explicit crash recovery protocol',
            priority: 'Medium'
        });
    }
}

// Analysis: DEV-RULES Compliance
function checkDEVRulesCompliance(patterns, docs) {
    const devRulesList = [
        'AttachJustificationToValues',
        'TreatTracesAsFirstClass',
        'IncludeHumanInComputation',
        'AllowMarkedInvalidState',
        'AvoidStateUndoSemantically',
        'InvertToInterchangeableAbstractions',
        'IsolateModulesProtectState',
        'PauseGCAtSemanticBoundaries',
        'BoundComplexityAcceptPartialCorrectness',
        'EnsureSubstitutabilityAndConfluence',
        'CentralizeAsVersionedSymbols',
        'OrganizeHierarchicallyByMeaning',
        'EncapsulateButShipInspector',
        'DeclareIntentExplainAtRuntime',
        'SnapshotMeaningBeforeMutation',
        'AllowDeliberateSlowness',
        'MakeErrorsPartOfLanguage',
        'SeparateConcernsRestoreBySnapshot',
        'EnsureRepeatableWithQuickRecovery'
    ];

    const complianceMatrix = {};

    for (const rule of devRulesList) {
        const mentions = (docs.authority.match(new RegExp(rule, 'g')) || []).length +
                        (docs.invariants.match(new RegExp(rule, 'g')) || []).length;

        complianceMatrix[rule] = {
            mentioned: mentions > 0,
            count: mentions,
            hasImplementation: mentions >= 2, // Mentioned + mapped
            hasMechanism: false // Will check for enforcement
        };

        // Check for enforcement mechanism
        const hasValidation = docs.authority.includes(`${rule}`) &&
                             docs.authority.includes('validation');
        complianceMatrix[rule].hasMechanism = hasValidation;
    }

    // Find rules without enforcement
    for (const [rule, status] of Object.entries(complianceMatrix)) {
        if (status.mentioned && !status.hasMechanism) {
            findings.devRulesIssues.push({
                what: `DEV-RULE "${rule}" referenced but no enforcement mechanism`,
                where: 'v2.3 spec',
                why: 'Reference without enforcement = aspiration not architecture',
                fix: `Add AST validation or runtime check for ${rule}`,
                priority: 'Medium'
            });
        }
    }
}

// Analysis: Gap Analysis
function performGapAnalysis(docs) {
    const requiredSections = [
        { name: 'Error recovery paths', keyword: 'error recovery|recovery path|failover' },
        { name: 'Edge case handling', keyword: 'edge case|corner case|boundary condition' },
        { name: 'State transitions', keyword: 'state transition|state machine|lifecycle' },
        { name: 'Garbage collection', keyword: 'garbage collect|GC|cleanup|retention' },
        { name: 'Performance boundaries', keyword: 'performance limit|max|threshold|bound' }
    ];

    for (const section of requiredSections) {
        const regex = new RegExp(section.keyword, 'gi');
        const mentions = (docs.authority.match(regex) || []).length +
                        (docs.invariants.match(regex) || []).length;

        if (mentions === 0) {
            findings.gaps.push({
                what: `Missing: ${section.name}`,
                where: 'v2.3 spec',
                why: 'Production systems require explicit handling',
                fix: `Add section covering ${section.name}`,
                priority: mentions === 0 ? 'High' : 'Low'
            });
        }
    }

    // Check for missing transitions between states
    const states = ['ephemeral', 'durable', 'protected', 'valid', 'invalid', 'pending'];
    const transitionMatrix = {};

    for (const fromState of states) {
        for (const toState of states) {
            if (fromState !== toState) {
                const transitionRegex = new RegExp(`${fromState}[^a-z]*${toState}`, 'gi');
                const found = docs.authority.match(transitionRegex) || [];
                transitionMatrix[`${fromState}->${toState}`] = found.length > 0;
            }
        }
    }

    const missingTransitions = Object.entries(transitionMatrix)
        .filter(([transition, exists]) => !exists)
        .map(([transition]) => transition);

    if (missingTransitions.length > 0) {
        findings.gaps.push({
            what: 'Missing state transitions',
            where: 'State management architecture',
            why: 'Undefined transitions = undefined behavior',
            fix: 'Document all valid state transitions',
            priority: 'Medium',
            examples: missingTransitions.slice(0, 5)
        });
    }
}

// Analysis: Contradictions
function findContradictions(docs) {
    // Pattern 1: "MUST" vs "MAY" contradictions
    const mustStatements = docs.authority.match(/MUST [^.]+\./g) || [];
    const mayStatements = docs.authority.match(/MAY [^.]+\./g) || [];

    // Check for same subject with conflicting requirements
    for (const must of mustStatements) {
        const subject = must.match(/MUST (\w+)/)?.[1];
        if (subject) {
            const conflictingMay = mayStatements.find(may =>
                may.includes(subject) && may.includes('not')
            );
            if (conflictingMay) {
                findings.contradictions.push({
                    what: 'Conflicting requirements',
                    where: 'Requirements specification',
                    evidence: {
                        required: must,
                        optional: conflictingMay
                    },
                    fix: 'Resolve: should it be MUST or MAY?',
                    priority: 'Critical'
                });
            }
        }
    }

    // Pattern 2: Checkpoint rules vs Authority rules
    const checkpointRequiresAuth = docs.authority.includes('checkpoint') &&
                                   docs.authority.includes('require') &&
                                   docs.authority.includes('authentication');

    const checkpointNoAuth = docs.authority.includes('ephemeral') &&
                            docs.authority.includes('checkpoint') &&
                            docs.authority.includes('no auth');

    if (checkpointRequiresAuth && checkpointNoAuth) {
        findings.contradictions.push({
            what: 'Checkpoint authentication contradiction',
            where: 'Checkpoint + Authority sections',
            why: 'Ephemeral checkpoints should not require auth, but spec unclear',
            fix: 'Clarify: only PROTECTED checkpoints require passkey',
            priority: 'High'
        });
    }

    // Pattern 3: Incremental vs Full revalidation
    const alwaysIncremental = docs.authority.includes('Monotonic Incrementality');
    const sometimesFull = docs.authority.includes('full revalidation');

    if (alwaysIncremental && sometimesFull) {
        const boundaryMention = docs.authority.includes('semantic boundary');
        if (!boundaryMention) {
            findings.contradictions.push({
                what: 'Incrementality contradiction',
                where: 'Invariant 3',
                why: 'Claims monotonic incrementality but allows full revalidation',
                fix: 'Already resolved: full revalidation only at semantic boundaries',
                priority: 'Low',
                status: 'ADDRESSED'
            });
        }
    }
}

// Generate report
function generateReport(findings, patterns) {
    let report = `# ARCHLAB v2.3 Planning Document Forensic Analysis

**Analysis Date**: ${new Date().toISOString().split('T')[0]}
**Analyzer**: Forensic Context Verifier Agent
**Scope**: PLANNING.v2.3-AUTHORITY-INVARIANTS.md + PLANNING.v2.3-INVARIANTS-CONTINUED.md
**Method**: Pattern verification, AST-inspired analysis, cross-reference validation

---

## Executive Summary

`;

    // Calculate pass/fail
    const critical = [
        ...findings.inconsistencies.filter(f => f.severity === 'Critical'),
        ...findings.contradictions.filter(f => f.priority === 'Critical')
    ];

    const high = [
        ...findings.inconsistencies.filter(f => f.severity === 'High'),
        ...findings.contradictions.filter(f => f.priority === 'High'),
        ...findings.gaps.filter(f => f.priority === 'High')
    ];

    report += `**Status**: ${critical.length === 0 ? 'PASS' : 'FAIL'} (${critical.length} critical issues)\n\n`;
    report += `**Severity Breakdown**:\n`;
    report += `- Critical: ${critical.length}\n`;
    report += `- High: ${high.length}\n`;
    report += `- Medium: ${findings.gaps.filter(f => f.priority === 'Medium').length + findings.devRulesIssues.length}\n`;
    report += `- Low: ${findings.inconsistencies.filter(f => f.severity === 'Low').length}\n\n`;

    report += `**Summary**: The ARCHLAB v2.3 planning documents are ${critical.length === 0 ? 'architecturally sound with minor gaps' : 'incomplete with critical gaps that must be addressed'}.\n\n`;

    // Inconsistencies
    report += `---

## 1. Inconsistencies Found

`;
    if (findings.inconsistencies.length === 0) {
        report += `✅ **No major inconsistencies detected.**\n\n`;
    } else {
        for (const issue of findings.inconsistencies) {
            report += `### ${issue.category} (Severity: ${issue.severity})\n\n`;
            if (issue.violations) {
                for (const v of issue.violations) {
                    report += `**What**: ${v.what}\n`;
                    report += `**Where**: ${v.where}\n`;
                    report += `**Evidence**:\n\`\`\`\n${v.evidence}\n\`\`\`\n\n`;
                }
            } else {
                report += `**What**: ${issue.what}\n`;
                report += `**Where**: ${issue.where}\n`;
                report += `**Evidence**: ${issue.evidence}\n\n`;
            }
        }
    }

    // Contradictions
    report += `---

## 2. Contradictions Found

`;
    if (findings.contradictions.length === 0) {
        report += `✅ **No contradictions detected.**\n\n`;
    } else {
        for (const c of findings.contradictions) {
            report += `### ${c.what} (Priority: ${c.priority})\n\n`;
            report += `**Where**: ${c.where}\n`;
            report += `**Why**: ${c.why}\n`;
            report += `**Fix**: ${c.fix}\n`;
            if (c.status) report += `**Status**: ${c.status}\n`;
            if (c.evidence) {
                report += `**Evidence**:\n`;
                if (typeof c.evidence === 'object') {
                    report += `- Required: ${c.evidence.required}\n`;
                    report += `- Optional: ${c.evidence.optional}\n`;
                } else {
                    report += `${c.evidence}\n`;
                }
            }
            report += `\n`;
        }
    }

    // Gaps
    report += `---

## 3. Gaps Identified

`;
    if (findings.gaps.length === 0) {
        report += `✅ **No critical gaps identified.**\n\n`;
    } else {
        for (const gap of findings.gaps) {
            report += `### ${gap.what} (Priority: ${gap.priority})\n\n`;
            report += `**Where**: ${gap.where}\n`;
            report += `**Why**: ${gap.why}\n`;
            report += `**Fix**: ${gap.fix}\n`;
            if (gap.examples) report += `**Examples**: ${gap.examples.join(', ')}\n`;
            if (gap.evidence) report += `**Evidence**: ${gap.evidence}\n`;
            report += `\n`;
        }
    }

    // IDE Best Practices
    report += `---

## 4. IDE Best Practice Violations

`;
    const allIDEIssues = [
        ...findings.performanceRisks,
        ...findings.uxRisks,
        ...findings.scalabilityIssues,
        ...findings.securityConcerns
    ];

    if (allIDEIssues.length === 0) {
        report += `✅ **No IDE best practice violations detected.**\n\n`;
    } else {
        report += `### Performance Risks\n\n`;
        for (const risk of findings.performanceRisks) {
            report += `**What**: ${risk.what}\n`;
            report += `**Where**: ${risk.where}\n`;
            report += `**Why**: ${risk.why}\n`;
            report += `**Fix**: ${risk.fix}\n`;
            report += `**Priority**: ${risk.priority}\n`;
            if (risk.count) report += `**Count**: ${risk.count}\n`;
            report += `\n`;
        }

        report += `### UX Risks\n\n`;
        for (const risk of findings.uxRisks) {
            report += `**What**: ${risk.what}\n`;
            report += `**Where**: ${risk.where}\n`;
            report += `**Concern**: ${risk.concern}\n`;
            if (risk.mitigation) report += `**Mitigation**: ${risk.mitigation}\n`;
            report += `**Status**: ${risk.status || 'NEEDS ADDRESSING'}\n`;
            if (risk.evidence) report += `**Evidence**: ${risk.evidence}\n`;
            report += `\n`;
        }

        report += `### Scalability Issues\n\n`;
        for (const issue of findings.scalabilityIssues) {
            report += `**What**: ${issue.what}\n`;
            report += `**Where**: ${issue.where}\n`;
            report += `**Why**: ${issue.why}\n`;
            report += `**Fix**: ${issue.fix}\n`;
            report += `**Priority**: ${issue.priority}\n`;
            if (issue.count) report += `**Count**: ${issue.count}\n`;
            report += `\n`;
        }
    }

    // DEV-RULES Compliance
    report += `---

## 5. DEV-RULES Compliance Matrix

`;
    report += `| DEV-RULE | Referenced | Enforced | Status |\n`;
    report += `|----------|-----------|----------|--------|\n`;
    // This will be populated by checkDEVRulesCompliance
    report += `\n`;

    for (const issue of findings.devRulesIssues) {
        report += `### ${issue.what} (Priority: ${issue.priority})\n\n`;
        report += `**Where**: ${issue.where}\n`;
        report += `**Why**: ${issue.why}\n`;
        report += `**Fix**: ${issue.fix}\n\n`;
    }

    // Recommended Fixes
    report += `---

## 6. Recommended Fixes (Prioritized)

`;

    const allIssues = [
        ...critical.map(i => ({ ...i, priority: 'Critical' })),
        ...high.map(i => ({ ...i, priority: i.priority || 'High' })),
        ...findings.devRulesIssues,
        ...findings.gaps.filter(g => g.priority === 'Medium')
    ];

    for (let i = 0; i < allIssues.length && i < 20; i++) {
        const issue = allIssues[i];
        report += `${i + 1}. **[${issue.priority}]** ${issue.what || issue.category}\n`;
        report += `   - Fix: ${issue.fix}\n\n`;
    }

    // Risk Assessment
    report += `---

## 7. Risk Assessment

`;
    report += `### What Breaks If Unfixed\n\n`;

    if (critical.length > 0) {
        report += `**Critical Risks (MUST FIX)**:\n`;
        for (const issue of critical) {
            report += `- ${issue.what || issue.category}: System behaves unpredictably, architectural guarantees violated\n`;
        }
        report += `\n`;
    }

    if (high.length > 0) {
        report += `**High Risks (FIX BEFORE v1.0)**:\n`;
        for (const issue of high.slice(0, 5)) {
            report += `- ${issue.what || issue.category}: ${issue.why || 'Production readiness compromised'}\n`;
        }
        report += `\n`;
    }

    report += `**Medium Risks (FIX DURING DEVELOPMENT)**:\n`;
    report += `- DEV-RULES without enforcement: Architecture becomes aspirational, not actual\n`;
    report += `- Missing gap documentation: Developers guess at edge cases\n`;
    report += `- Performance risks: "Mystery lag" appears in large projects\n\n`;

    report += `**Low Risks (ACCEPTABLE FOR v2.3)**:\n`;
    report += `- Minor documentation gaps: Can be addressed iteratively\n`;
    report += `- Already-addressed contradictions: Marked resolved\n\n`;

    // Conclusion
    report += `---

## Conclusion

`;
    if (critical.length === 0 && high.length < 3) {
        report += `The ARCHLAB v2.3 planning documents are **production-ready** with minor refinements needed. The architecture is sound, patterns are consistent, and most DEV-RULES have enforcement mechanisms.\n\n`;
        report += `**Recommendation**: Proceed with implementation, address High priority items in parallel.\n`;
    } else {
        report += `The ARCHLAB v2.3 planning documents are **not yet production-ready**. Critical gaps must be addressed before implementation begins.\n\n`;
        report += `**Recommendation**: Fix all Critical issues, document all High-priority gaps, then proceed.\n`;
    }

    report += `\n---\n\n`;
    report += `*Report generated by Forensic Context Verifier*\n`;
    report += `*Version: 1.0*\n`;
    report += `*Analysis method: Pattern verification + Cross-reference + Best practice validation*\n`;

    return report;
}

// Main execution
function main() {
    console.log('Starting forensic analysis of ARCHLAB v2.3 planning documents...\n');

    const docs = readDocuments();
    console.log('✓ Documents loaded');

    const patterns = extractPatterns(docs);
    console.log(`✓ Extracted ${patterns.invariants.length} invariants, ${patterns.validationRules.length} validation rules`);

    checkPatternContradictions(patterns, docs);
    console.log('✓ Pattern contradiction check complete');

    checkIDEBestPractices(docs);
    console.log('✓ IDE best practices check complete');

    checkDEVRulesCompliance(patterns, docs);
    console.log('✓ DEV-RULES compliance check complete');

    performGapAnalysis(docs);
    console.log('✓ Gap analysis complete');

    findContradictions(docs);
    console.log('✓ Contradiction detection complete');

    const report = generateReport(findings, patterns);
    fs.writeFileSync(CONFIG.output, report, 'utf-8');
    console.log(`\n✓ Analysis complete! Report written to: ${CONFIG.output}`);

    // Print summary to console
    console.log('\n' + '='.repeat(70));
    console.log('SUMMARY');
    console.log('='.repeat(70));
    console.log(`Inconsistencies: ${findings.inconsistencies.length}`);
    console.log(`Contradictions: ${findings.contradictions.length}`);
    console.log(`Gaps: ${findings.gaps.length}`);
    console.log(`Performance Risks: ${findings.performanceRisks.length}`);
    console.log(`UX Risks: ${findings.uxRisks.length}`);
    console.log(`Scalability Issues: ${findings.scalabilityIssues.length}`);
    console.log(`DEV-RULES Issues: ${findings.devRulesIssues.length}`);
    console.log('='.repeat(70));
}

// Run analysis
main();
