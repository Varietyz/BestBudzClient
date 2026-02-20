---
description: Algorithmic verification loop - systematically resolve ALL codebase violations until 90+ gates pass
argument-hint: [max-iterations]
allowed-tools: Bash, Read, Write, Edit, Grep, Glob
model: claude-sonnet-4-5-20250929
---

ALGORITHMIC VERIFICATION LOOP - Systematic violation resolution following INTAKE→VALIDATE→REFINE

%% META %%:
intent: "Execute exponential verification work until all 90+ gates pass"
context: "Codebase validation with systematic remediation"
priority: critical

# VERIFICATION ALGORITHM

**INITIALIZE<Rules> → REPEAT { EXECUTE<Verify> → CAPTURE<Violations> → ANALYZE<Rules> → APPLY<Fix> } UNTIL<AllPass>**

This implements the problem-solving chain:
- **INTAKE**: Parse violation output from `npm run verify-codebase`
- **ABSTRACT**: Map violations to DEV-RULES.md + ARCHLAB.md principles
- **DEFINE**: Target state = 0 violations across all 90+ gates
- **CONSTRAIN**: All fixes must follow architectural laws
- **EXPLORE**: Generate remediation strategies from rules
- **PREDICT**: Validate fix won't introduce new violations
- **CHOOSE**: Select minimal-change fix
- **EXECUTE**: Apply code changes
- **VALIDATE**: Re-run verification
- **REFINE**: Loop if violations remain

---

## PHASE 1: INITIALIZE<ArchitecturalRules>

DECLARE max_iterations: integer
DECLARE violation_count: integer
DECLARE iteration: integer
DECLARE rules_content: string
DECLARE archlab_content: string

SET max_iterations = $1 OR 50
SET iteration = 0
SET violation_count = 999999

## Read architectural rules (MANDATORY before any fix)

READ ".claude/_DEV-RULES.md" INTO rules_content
READ ".claude/_ARCHLAB.md" INTO archlab_content

REPORT "📖 Loaded architectural rules"
REPORT "  → DEV-RULES.md: " + LENGTH(rules_content) + " bytes"
REPORT "  → _ARCHLAB.md: " + LENGTH(archlab_content) + " bytes"
REPORT ""

---

## PHASE 2: REPEAT<VerificationLoop>

WHILE iteration < max_iterations AND violation_count > 0:

    SET iteration = iteration + 1

    REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT "🔄 ITERATION " + iteration + " / " + max_iterations
    REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT ""

    ## EXECUTE<VerifyCodebase>
    REPORT "⚙️  EXECUTING: npm run verify-codebase"
    EXECUTE Bash WITH "cd D:\\GIT\\archlab && npm run verify-codebase 2>&1 | tail -50" INTO verification_output

    ## CAPTURE<ViolationContext>
    REPORT "📊 ANALYZING: Last 50 lines of verification output"

    ## Check if all gates passed
    IF verification_output CONTAINS "All validation gates passed":
        REPORT ""
        REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        REPORT "✅ ALL 90+ GATES PASSED"
        REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        REPORT ""
        REPORT "🎉 Verification complete in " + iteration + " iterations"
        EXIT 0

    ## ANALYZE<Violations> - Extract violation patterns
    DECLARE violations: array
    SET violations = EXTRACT_PATTERNS FROM verification_output WHERE pattern MATCHES:
        - "ERROR"
        - "FAIL"
        - "✗"
        - "violation"
        - "warning"
        - "deprecated"

    REPORT ""
    REPORT "🔍 DETECTED VIOLATIONS:"
    REPORT verification_output
    REPORT ""

    ## ANALYZE<RootCause> - Map violations to architectural rules
    REPORT "📚 CROSS-REFERENCING VIOLATIONS AGAINST:"
    REPORT "  → _DEV-RULES.md principles"
    REPORT "  → _ARCHLAB.md laws"
    REPORT ""

    ## Extract specific violation types
    IF verification_output CONTAINS "naming convention":
        REPORT "📌 VIOLATION TYPE: Naming Convention"
        REPORT "   RULE: _ARCHLAB.md File Naming Conventions"
        REPORT "   FIX: Rename files to kebab-case, classes to PascalCase"
        REPORT ""

    IF verification_output CONTAINS "import":
        REPORT "📌 VIOLATION TYPE: Import/Dependency"
        REPORT "   RULE: LAW 7 (Single Import Boundary)"
        REPORT "   FIX: Use registry-based dependency injection"
        REPORT ""

    IF verification_output CONTAINS "complexity":
        REPORT "📌 VIOLATION TYPE: Complexity Boundary"
        REPORT "   RULE: bounded-complexity (max 150 lines/file)"
        REPORT "   FIX: Split file into smaller modules"
        REPORT ""

    IF verification_output CONTAINS "security":
        REPORT "📌 VIOLATION TYPE: Security"
        REPORT "   RULE: LAW 4 (Security as Base-Inherited Capability)"
        REPORT "   FIX: Use base class sanitization, never inline"
        REPORT ""

    IF verification_output CONTAINS "callback":
        REPORT "📌 VIOLATION TYPE: Parent Callback"
        REPORT "   RULE: LAW 2 (No Child-to-Parent Callbacks)"
        REPORT "   FIX: Emit intent via EventBus instead"
        REPORT ""

    ## APPLY<SystematicRemediation>
    REPORT "🔧 REMEDIATION STRATEGY:"
    REPORT "   1. Analyze violation context"
    REPORT "   2. Reference architectural rules"
    REPORT "   3. Apply minimal-change fix"
    REPORT "   4. Verify no new violations introduced"
    REPORT ""

    ## PREDICT<FixImpact> - Ensure fix won't break other gates
    REPORT "⚠️  APPLYING FIX WITH CONSTRAINT VALIDATION"
    REPORT "   → Must not introduce new violations"
    REPORT "   → Must follow all 7 ARCHLAB laws"
    REPORT "   → Must respect AVOID → ENFORCE mappings"
    REPORT ""

    ## User must apply fixes manually (AI analyzes, human executes)
    REPORT "🛠️  READY FOR FIX APPLICATION"
    REPORT ""
    REPORT "NEXT STEPS:"
    REPORT "1. Review violations above"
    REPORT "2. Apply fixes following architectural rules"
    REPORT "3. Type 'continue' to re-verify"
    REPORT ""

    ## Wait for user to apply fixes
    PAUSE "Press Enter after applying fixes to continue..."

    ## VALIDATE<Resolution> - Check if violations reduced
    EXECUTE Bash WITH "cd D:\\GIT\\archlab && npm run verify-codebase 2>&1 | grep -c 'ERROR\\|FAIL\\|✗'" INTO new_violation_count

    IF new_violation_count < violation_count:
        REPORT "✅ Progress: Violations reduced from " + violation_count + " to " + new_violation_count
        SET violation_count = new_violation_count
    ELSE IF new_violation_count > violation_count:
        REPORT "⚠️  Warning: Violations INCREASED from " + violation_count + " to " + new_violation_count
        REPORT "   → Fix introduced new violations"
        REPORT "   → Review changes against AVOID → ENFORCE mappings"
        SET violation_count = new_violation_count
    ELSE:
        REPORT "⚠️  No change: Violation count still " + violation_count
        REPORT "   → Fix did not resolve violations"
        REPORT "   → Try different remediation strategy"

END WHILE

---

## PHASE 3: VERIFY<LoopTermination>

IF iteration >= max_iterations:
    REPORT ""
    REPORT "⚠️  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT "⚠️  MAX ITERATIONS REACHED"
    REPORT "⚠️  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT ""
    REPORT "Completed " + max_iterations + " iterations"
    REPORT "Remaining violations: " + violation_count
    REPORT ""
    REPORT "RECOMMENDATIONS:"
    REPORT "1. Increase max-iterations: /verify-loop 100"
    REPORT "2. Review violation patterns for systemic issues"
    REPORT "3. Check if rules need updating"
    EXIT 1

IF violation_count <= 0:
    REPORT ""
    REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT "✅ ALL VIOLATIONS RESOLVED"
    REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT ""
    REPORT "🎉 Verification complete in " + iteration + " iterations"
    EXIT 0

---

## OPERATIONAL NOTES

**Algorithm Class**: Meta Loop (operates on verification process itself)

**Semantic Guarantees**:
- EXECUTE<Verify>: Non-destructive analysis only
- CAPTURE<Violations>: Preserves source meaning
- ANALYZE<Rules>: Deep examination against principles
- APPLY<Fix>: Requires human approval (LAW 6: Human-Controlled Authority)

**Mapped Principles**:
- **immediacy** (AVOID: deffering): Fix violations immediately, don't defer
- **feedback-enforced** (AVOID: feedback-ignored): Verification output drives fixes
- **observed-execution** (AVOID: unobserved-execution): All violations visible
- **fail-fast** (AVOID: fallback): No workarounds, fix root cause
- **single-path** (AVOID: dual-path): One verification process

**Constraint Validation**:
- All fixes MUST follow _DEV-RULES.md
- All fixes MUST satisfy _ARCHLAB.md laws
- All fixes MUST respect AVOID → ENFORCE mappings
- All fixes MUST reduce violation count monotonically

---

## USAGE EXAMPLES

### Example 1: Default (50 iterations max)

```
/verify-loop
```

### Example 2: Extended run (100 iterations)

```
/verify-loop 100
```

### Example 3: Quick check (10 iterations)

```
/verify-loop 10
```

---

## ALGORITHM FLOW VISUALIZATION

```
┌─────────────────────────────────────────────────────────────┐
│               ALGORITHMIC VERIFICATION LOOP                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────┐                                          │
│  │  INITIALIZE    │  Read _DEV-RULES.md + _ARCHLAB.md       │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│  ┌────────────────┐                                          │
│  │   EXECUTE      │  npm run verify-codebase                │
│  │   <Verify>     │                                          │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│  ┌────────────────┐                                          │
│  │   CAPTURE      │  Extract last 50 lines                  │
│  │   <Violations> │  Parse error patterns                   │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│  ┌────────────────┐                                          │
│  │   ANALYZE      │  Map violations → rules                 │
│  │   <Rules>      │  Generate remediation                   │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│  ┌────────────────┐                                          │
│  │   APPLY        │  Human applies fixes                    │
│  │   <Fix>        │  (LAW 6: Authority Boundary)            │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│  ┌────────────────┐                                          │
│  │   VALIDATE     │  Re-run verification                    │
│  │   <Resolution> │  Check violation count                  │
│  └────────┬───────┘                                          │
│           │                                                  │
│           ▼                                                  │
│      All gates                                               │
│      passed?                                                 │
│      ┌─Yes─►  EXIT SUCCESS                                  │
│      │                                                       │
│      No                                                      │
│      │                                                       │
│      └──►  LOOP BACK (max 50 iterations)                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## TERMINATION CONDITIONS

**SUCCESS**: `violation_count === 0` AND `output CONTAINS "All validation gates passed"`

**TIMEOUT**: `iteration >= max_iterations` AND `violation_count > 0`

**PROGRESS INDICATORS**:
- ✅ Violations decreasing → Strategy working
- ⚠️  Violations increasing → Fix introduced new issues
- ⚠️  Violations unchanged → Different approach needed

---

**© 2026 ARCHLAB - Algorithmic Verification v1.0.0**
