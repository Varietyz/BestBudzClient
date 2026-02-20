# REFACTOR-PLAN.md

<!-- Agent-to-agent PAG-LANG content -->
<!-- Purpose: Detailed implementation steps, method signatures, migration strategy -->

## STATUS
SET phase = 2
SET status = "complete"
SET agent = "refactor-planner"

---

## Executive Summary

This plan provides detailed implementation guidance for achieving algorithm compliance in the codebase-validation framework. The refactor addresses three base classes and their respective implementations, following the DEV-RULES-ALGORITHMS.md specifications.

### Scope Assessment (Verified)

| Component | File | Lines | Implementations | Algorithm Required | Compliance |
|-----------|------|-------|-----------------|-------------------|------------|
| BaseAutoFixer | `auto-fix/base-auto-fixer.js` | 56 | 11 | State-Snapshot-Recovery-Protocol | 0% |
| BaseScanner | `core/base-scanner.js` | 117 | 67 | Perceptual-Loop | 25% |
| BaseValidator | `core/base-validator.js` | 120 | 68 | Verification-Gate-Protocol | 30% |

---

## PHASE 1: BaseAutoFixer Refactor (HIGHEST PRIORITY)

### Current State Analysis

The existing `BaseAutoFixer` (56 lines) lacks:
- State capture before mutation (`EXTRACT<Current-State>`)
- Checkpoint creation (`CREATE<Checkpoint-Snapshot>`)
- Integrity validation (`ANALYZE<State-Integrity>`)
- Checkpoint persistence (`WRITE<Checkpoint-Storage>`)

### Required Algorithm: State-Snapshot-Recovery-Protocol

```
EXTRACT<Current-State> -> CREATE<Checkpoint-Snapshot> -> ANALYZE<State-Integrity> -> WRITE<Checkpoint-Storage>
```

### 1.1 New Method Signatures for BaseAutoFixer

```javascript
/**
 * BaseAutoFixer - Algorithm-Compliant Base Class
 *
 * Implements: State-Snapshot-Recovery-Protocol
 * LAW 5: Authenticated State Recovery
 *
 * @class BaseAutoFixer
 */
export class BaseAutoFixer {
    /**
     * EXTRACT<Current-State> - Capture file state before mutation
     * @param {string} filePath - Absolute path to file
     * @returns {Promise<FileSnapshot>} Captured state including content, mtime, hash
     * @semantic Non-destructive capture; file unchanged
     */
    async extractCurrentState(filePath) {}

    /**
     * CREATE<Checkpoint-Snapshot> - Create atomic checkpoint from state
     * @param {string} filePath - File being modified
     * @param {FileSnapshot} state - Extracted state from extractCurrentState
     * @returns {Promise<Checkpoint>} Immutable checkpoint with ID and metadata
     * @semantic Deterministic creation; frozen state artifact
     */
    async createCheckpoint(filePath, state) {}

    /**
     * ANALYZE<State-Integrity> - Validate checkpoint completeness
     * @param {Checkpoint} checkpoint - Checkpoint to validate
     * @returns {Promise<IntegrityResult>} Validation result with canRestore flag
     * @semantic Evaluation check; non-modifying validation
     */
    async analyzeStateIntegrity(checkpoint) {}

    /**
     * WRITE<Checkpoint-Storage> - Persist checkpoint to storage
     * @param {Checkpoint} checkpoint - Validated checkpoint
     * @returns {Promise<StorageResult>} Storage confirmation with location
     * @semantic Idempotent write; authenticated storage
     */
    async writeCheckpointStorage(checkpoint) {}

    /**
     * Rollback to checkpoint on failure
     * @param {string} checkpointId - ID of checkpoint to restore
     * @returns {Promise<RollbackResult>} Restoration result
     * @semantic Atomic restoration; semantic undo
     */
    async rollbackToCheckpoint(checkpointId) {}

    /**
     * Main fix orchestration - wraps mutation in checkpoint protocol
     * @param {Array<Violation>} violations - Violations to fix
     * @returns {Promise<FixResult>} Results including checkpointId
     */
    async fix(violations) {}
}
```

### 1.2 Settings Schema Additions for AutoFixer

Add to `settings.json` under `tools`:

```json
{
    "tools": {
        "autoFix": {
            "enabled": true,
            "linting": true,
            "formatting": true,
            "imports": true,
            "dryRun": false,
            "snapshot": {
                "enabled": true,
                "storageDir": ".checkpoints",
                "retentionCount": 10,
                "hashAlgorithm": "sha256",
                "authenticatedStorage": false,
                "compressionEnabled": true,
                "integrityValidation": true
            }
        }
    }
}
```

### 1.3 New Accessor Functions Required

Location: `core/settings/accessors/tools/autofix-accessors.js`

```javascript
/**
 * AutoFix Settings Accessors
 */
export function getAutoFixSnapshotEnabled() {}
export function getAutoFixSnapshotStorageDir() {}
export function getAutoFixSnapshotRetentionCount() {}
export function getAutoFixSnapshotHashAlgorithm() {}
export function getAutoFixAuthenticatedStorage() {}
export function getAutoFixCompressionEnabled() {}
export function getAutoFixIntegrityValidation() {}
```

### 1.4 Implementation Files to Update

11 files extending BaseAutoFixer:

1. `root/codebase-validation/auto-fix/code-comments-auto-fixer.js`
2. `root/codebase-validation/auto-fix/constant-aliases-auto-fixer.js`
3. `root/codebase-validation/auto-fix/json-stringify-auto-fixer.js`
4. `root/codebase-validation/auto-fix/css/css-design-token-auto-fixer.js`
5. `root/codebase-validation/auto-fix/css/css-responsive-patterns-auto-fixer.js`
6. `root/codebase-validation/auto-fix/css/css-variable-consistency-auto-fixer.js`
7. `root/codebase-validation/auto-fix/css/css-variable-order-auto-fixer.js`
8. `root/codebase-validation/auto-fix/css/css-variable-unit-auto-fixer.js`
9. `root/codebase-validation/auto-fix/electron/browser-window-icon-auto-fixer.js`
10. `root/codebase-validation/auto-fix/auto-move/css/css-variable-location-fixer.js`

**Migration Strategy for Fixers:**
- Override `fix()` method to call parent with checkpoint protocol
- No changes required to `fixFile()` implementations
- Backward compatible: existing fixers gain checkpointing automatically

---

## PHASE 2: BaseScanner Refactor

### Current State Analysis

The existing `BaseScanner` (117 lines) has:
- Lifecycle management (init/run/shutdown) - PARTIAL
- File scanning utilities - GOOD
- Registry output - PARTIAL

Missing:
- Explicit FIND phase method
- Explicit ANALYZE phase method
- Explicit WRITE phase method (uses registry.build but not formalized)
- Explicit REPORT phase method

### Required Algorithm: Perceptual-Loop

```
FIND<Target-Files> -> ANALYZE<Content-Patterns> -> WRITE<Registry-Output> -> REPORT<Summary>
```

### 2.1 New Method Signatures for BaseScanner

```javascript
/**
 * BaseScanner - Algorithm-Compliant Base Class
 *
 * Implements: Perceptual-Loop
 *
 * @class BaseScanner
 */
export class BaseScanner {
    /**
     * FIND<Target-Files> - Discover files matching scan criteria
     * @returns {Promise<FileSet>} Set of file paths to analyze
     * @semantic Boolean existence check; non-invasive scan
     */
    async findTargetFiles() {}

    /**
     * ANALYZE<Content-Patterns> - Examine file contents for patterns
     * @param {FileSet} files - Files from findTargetFiles
     * @returns {Promise<AnalysisResult>} Pattern matches and violations
     * @semantic Deep examination; delegates to pattern matchers
     */
    async analyzeContentPatterns(files) {}

    /**
     * WRITE<Registry-Output> - Persist analysis to registry
     * @param {AnalysisResult} analysis - Results from analyzeContentPatterns
     * @returns {Promise<RegistryResult>} Confirmation of registry write
     * @semantic Creates new entry; structured artifact
     */
    async writeRegistryOutput(analysis) {}

    /**
     * REPORT<Summary> - Log summary for user visibility
     * @param {AnalysisResult} analysis - Analysis results
     * @param {RegistryResult} registry - Registry write confirmation
     * @returns {void}
     * @semantic Console output; no state modification
     */
    reportSummary(analysis, registry) {}

    /**
     * Main scan orchestration - executes Perceptual-Loop
     * Lifecycle: init -> findTargetFiles -> analyzeContentPatterns ->
     *            writeRegistryOutput -> reportSummary -> shutdown
     * @returns {Promise<ScanResult>}
     */
    async scan() {}
}
```

### 2.2 Settings Schema Additions for Scanner

Add to `settings.json` under `rules.scanning`:

```json
{
    "rules": {
        "scanning": {
            "context": {
                "linesBefore": 3,
                "linesAfter": 3,
                "maxExamples": 3,
                "lookAhead": 5,
                "lookBack": 10
            },
            "skipHidden": true,
            "phases": {
                "logPhaseTransitions": false,
                "phaseTimeout": 30000
            },
            "snapshot": {
                "captureInputState": false,
                "captureOutputState": true,
                "snapshotFormat": "json"
            }
        }
    }
}
```

### 2.3 Implementation Files to Update

67 scanner files in `scanners/` directory (grouped by category):

**code-analysis/ (4 files)**
- base-classes-scanner.js
- constants-scanner.js
- import-patterns-scanner.js
- utilities-scanner.js

**discovery/css/ (1 file)**
- css-variables-discovered-scanner.js

**quality/code/ (8 files)**
- code-comments-scanner.js
- code-duplication-scanner.js
- code-naming-scanner.js
- json-stringify-scanner.js
- underscore-prefix-scanner.js
- magic-literals/magic-colors-scanner.js
- magic-literals/magic-measurements-scanner.js
- magic-literals/magic-numbers-scanner.js

**quality/cross-module/ (4 files)**
- constant-aliases-scanner.js
- constant-discovery-scanner.js
- constant-duplicates-scanner.js
- constant-imports-scanner.js

**quality/css/ (40 files)** - Various CSS pattern scanners

**quality/electron/ (1 file)**
- browser-window-icon-scanner.js

**quality/security/ (4 files)**
- csp-header-scanner.js
- csp-inline-events-scanner.js
- csp-inline-styles-scanner.js
- csp-unsafe-eval-scanner.js

**quality/structure/ (3 files)**
- file-limits-scanner.js
- file-naming-scanner.js
- folder-limits-scanner.js

**quality/web/ (2 files)**
- accessibility-images-scanner.js
- accessibility-interactive-scanner.js

**Migration Strategy for Scanners:**
- Current `_scan()` method becomes `analyzeContentPatterns()`
- Add default `findTargetFiles()` that uses `scanDirectory()`
- Add default `writeRegistryOutput()` wrapping `registry.build()`
- Add default `reportSummary()` for logging
- Existing scanners work unchanged (base class provides defaults)

---

## PHASE 3: BaseValidator Refactor

### Current State Analysis

The existing `BaseValidator` (120 lines) has:
- Lifecycle management - PARTIAL
- Registry loading - GOOD
- Display utilities - GOOD

Missing:
- Invariant set creation (`CREATE<Invariant-Set>`)
- State analysis method (`ANALYZE<System-State>`)
- Violation analysis with severity (`ANALYZE<Violations>`)
- Halt-or-continue decision (`EXECUTE<Halt-Or-Continue>`)

### Required Algorithm: Verification-Gate-Protocol

```
CREATE<Invariant-Set> -> ANALYZE<System-State> -> ANALYZE<Violations> -> EXECUTE<Halt-Or-Continue>
```

### 3.1 New Method Signatures for BaseValidator

```javascript
/**
 * BaseValidator - Algorithm-Compliant Base Class
 *
 * Implements: Verification-Gate-Protocol
 *
 * @class BaseValidator
 */
export class BaseValidator {
    /**
     * CREATE<Invariant-Set> - Define constraints for this validator
     * @returns {Promise<InvariantSet>} Set of constraint specifications
     * @semantic Non-deterministic creation; generates constraint candidates
     */
    async createInvariantSet() {}

    /**
     * ANALYZE<System-State> - Load and examine current state
     * @param {InvariantSet} invariants - Constraints to check against
     * @returns {Promise<SystemState>} Current state from registry/files
     * @semantic Deep examination; delegates to loading tools
     */
    async analyzeSystemState(invariants) {}

    /**
     * ANALYZE<Violations> - Classify violations by severity
     * @param {SystemState} state - State from analyzeSystemState
     * @param {InvariantSet} invariants - Constraint set
     * @returns {Promise<ViolationSet>} Classified violations with severity
     * @semantic Evaluation check; validates against severity specification
     */
    async analyzeViolations(state, invariants) {}

    /**
     * EXECUTE<Halt-Or-Continue> - Decision based on violation severity
     * @param {ViolationSet} violations - Classified violations
     * @returns {Promise<ValidationResult>} { halt: boolean, valid: boolean, violations }
     * @semantic Side effects possible; may halt execution
     */
    async executeHaltOrContinue(violations) {}

    /**
     * Main validate orchestration - executes Verification-Gate-Protocol
     * @returns {Promise<ValidationResult>}
     */
    async validate() {}
}
```

### 3.2 Settings Schema Additions for Validator

Add to `settings.json` under `rules.severity` (expand existing):

```json
{
    "rules": {
        "severity": {
            "security": {
                "inlineEvents": "high",
                "inlineStyles": "medium",
                "unsafeEval": "critical",
                "missingHeaders": "medium"
            },
            "designTokens": {
                "colors": "high",
                "spacing": "medium",
                "typography": "medium",
                "effects": "high"
            },
            "architecture": {
                "duplicates": "critical",
                "scoping": "high",
                "naming": "medium",
                "complexity": "medium",
                "unusedCode": "low"
            },
            "thresholds": {
                "high": 3,
                "medium": 1
            },
            "haltOn": {
                "critical": true,
                "high": false,
                "medium": false,
                "low": false
            },
            "classification": {
                "resourceViolations": ["security", "architecture.duplicates"],
                "computationUncertainty": ["designTokens", "naming", "complexity"]
            }
        }
    }
}
```

### 3.3 New Accessor Functions Required

Location: `core/settings/accessors/validation-accessors.js` (expand)

```javascript
/**
 * Severity Accessors
 */
export function getSeverityHaltConfig() {}
export function getSeverityClassification() {}
export function isResourceViolation(category) {}
export function shouldHaltOnSeverity(severity) {}
```

### 3.4 Implementation Files to Update

68 validator files in `validators/` directory (grouped by category):

**quality/code-quality/discovery/ (4 files)**
- architecture-validator.js
- base-classes-validator.js
- constants-validator.js
- utilities-validator.js

**quality/code-quality/violations/ (7 files)**
- code-comments-validator.js
- code-duplication-validator.js
- json-stringify-validator.js
- underscore-prefix-validator.js
- magic-literals/magic-colors-validator.js
- magic-literals/magic-measurements-validator.js
- magic-literals/magic-numbers-validator.js

**quality/cross-module/ (4 files)**
- constant-aliases-validator.js
- constant-discovery-validator.js
- constant-duplicates-validator.js
- constant-imports-validator.js

**quality/css/ (40 files)** - Various CSS validators

**quality/security/ (4 files)**
- csp-header-validator.js
- csp-inline-events-validator.js
- csp-inline-styles-validator.js
- csp-unsafe-eval-validator.js

**quality/tooling/ (5 files)**
- format-validator.js
- lint-validator.js
- deps/depcheck-validator.js
- deps/ts-prune-validator.js
- types/typescript-validator.js

**quality/web/ (2 files)**
- accessibility-images-validator.js
- accessibility-interactive-validator.js

**structure/ (4 files)**
- file-limits-validator.js
- folder-limits-validator.js
- import-patterns-validator.js
- naming-validator.js

**Migration Strategy for Validators:**
- Current `_validate()` becomes `analyzeViolations()`
- Add default `createInvariantSet()` returning validator-specific rules
- Add default `analyzeSystemState()` loading from registry
- Add default `executeHaltOrContinue()` using severity config
- Existing validators work unchanged (base class provides defaults)

---

## PHASE 4: Settings Integration

### 4.1 Complete Settings Additions

New keys to add to `settings.json`:

```json
{
    "tools": {
        "autoFix": {
            "snapshot": {
                "enabled": true,
                "storageDir": ".checkpoints",
                "retentionCount": 10,
                "hashAlgorithm": "sha256",
                "authenticatedStorage": false,
                "compressionEnabled": true,
                "integrityValidation": true
            }
        }
    },
    "rules": {
        "scanning": {
            "phases": {
                "logPhaseTransitions": false,
                "phaseTimeout": 30000
            },
            "snapshot": {
                "captureInputState": false,
                "captureOutputState": true,
                "snapshotFormat": "json"
            }
        },
        "severity": {
            "haltOn": {
                "critical": true,
                "high": false,
                "medium": false,
                "low": false
            },
            "classification": {
                "resourceViolations": ["security", "architecture.duplicates"],
                "computationUncertainty": ["designTokens", "naming", "complexity"]
            }
        }
    }
}
```

### 4.2 New Accessor File Structure

Create or update files:

1. `core/settings/accessors/tools/autofix-accessors.js` (NEW)
2. `core/settings/accessors/scanning/phase-accessors.js` (NEW)
3. `core/settings/accessors/validation-accessors.js` (EXPAND)

---

## PHASE 5: Migration Strategy

### 5.1 Implementation Order (Recommended Sequence)

1. **BaseAutoFixer** (Priority 1)
   - Highest risk, smallest scope
   - 11 implementation files
   - Enables checkpoint/rollback for all fixers

2. **BaseScanner** (Priority 2)
   - Largest impact, 67 files
   - Provides explicit phase structure
   - Backward compatible migration

3. **BaseValidator** (Priority 3)
   - 68 files
   - Adds severity classification
   - Enables halt-on-critical

### 5.2 Backward Compatibility Approach

All refactors use **additive extension**:

```javascript
// Example: BaseScanner backward compatibility
class BaseScanner {
    // NEW: Default implementations that wrap existing behavior
    async findTargetFiles() {
        // Default: use existing directory scanning
        return this._collectFilesFromDirectories();
    }

    async analyzeContentPatterns(files) {
        // Default: delegate to existing _scan()
        return await this._scan();
    }

    // EXISTING: Methods remain unchanged
    async scanDirectory(dir, handler) { /* unchanged */ }
    async readFile(path) { /* unchanged */ }
}
```

### 5.3 Testing Strategy

For each base class:

1. **Unit Tests**
   - Test each algorithm phase method independently
   - Verify semantic guarantees (e.g., non-destructive capture)

2. **Integration Tests**
   - Test full algorithm execution flow
   - Verify state transitions
   - Test checkpoint/rollback for fixers

3. **Migration Tests**
   - Run existing implementation test suites
   - Verify no behavioral changes in implementations
   - Validate registry output unchanged

---

## PHASE 6: Rollback Strategy

### 6.1 Checkpoint Locations

Before each phase:
- Git tag: `pre-refactor-{component}-{date}`
- Export settings.json backup

### 6.2 Rollback Procedure

If issues found:

1. **For BaseAutoFixer**:
   - Git revert to `pre-refactor-autofixer-{date}`
   - Restore settings.json `tools.autoFix.snapshot` section

2. **For BaseScanner**:
   - Git revert to `pre-refactor-scanner-{date}`
   - Restore settings.json `rules.scanning.phases` section

3. **For BaseValidator**:
   - Git revert to `pre-refactor-validator-{date}`
   - Restore settings.json `rules.severity.haltOn` section

---

## SUCCESS CRITERIA

### Algorithm Compliance Checklist

| Component | EXTRACT | CREATE | ANALYZE | WRITE/EXECUTE | REPORT |
|-----------|---------|--------|---------|---------------|--------|
| BaseAutoFixer | extractCurrentState() | createCheckpoint() | analyzeStateIntegrity() | writeCheckpointStorage() | - |
| BaseScanner | - | - | analyzeContentPatterns() | writeRegistryOutput() | reportSummary() |
| BaseValidator | - | createInvariantSet() | analyzeSystemState(), analyzeViolations() | executeHaltOrContinue() | - |

### Settings Configurability Checklist

- [ ] `tools.autoFix.snapshot.enabled` - Boolean
- [ ] `tools.autoFix.snapshot.storageDir` - String path
- [ ] `tools.autoFix.snapshot.retentionCount` - Number
- [ ] `tools.autoFix.snapshot.hashAlgorithm` - String
- [ ] `rules.scanning.phases.logPhaseTransitions` - Boolean
- [ ] `rules.scanning.phases.phaseTimeout` - Number
- [ ] `rules.severity.haltOn.critical` - Boolean
- [ ] `rules.severity.haltOn.high` - Boolean
- [ ] `rules.severity.classification.resourceViolations` - Array
- [ ] `rules.severity.classification.computationUncertainty` - Array

### Test Cases

**BaseAutoFixer:**
1. Checkpoint created before file modification
2. Checkpoint contains valid hash of original content
3. Rollback restores exact original content
4. Multiple fixes create separate checkpoints
5. Retention limit removes old checkpoints

**BaseScanner:**
1. findTargetFiles() returns correct file set
2. analyzeContentPatterns() produces structured results
3. writeRegistryOutput() creates valid registry entry
4. reportSummary() logs appropriate messages
5. Phase timeout triggers graceful abort

**BaseValidator:**
1. createInvariantSet() returns validator-specific rules
2. analyzeSystemState() loads from registry
3. analyzeViolations() classifies by severity
4. executeHaltOrContinue() halts on critical (when configured)
5. executeHaltOrContinue() continues on non-critical

---

## CRITICAL FILES FOR IMPLEMENTATION

1. **root/codebase-validation/auto-fix/base-auto-fixer.js** - Core logic requiring State-Snapshot-Recovery-Protocol implementation (56 lines, 0% compliance)

2. **root/codebase-validation/core/base-scanner.js** - Requires Perceptual-Loop phase methods (117 lines, uses existing lifecycle)

3. **root/codebase-validation/core/base-validator.js** - Requires Verification-Gate-Protocol methods (120 lines, uses existing lifecycle)

4. **root/codebase-validation/settings.json** - Must add 10+ new configuration keys for snapshot, phases, and halt-on-severity (1748 lines)

5. **root/codebase-validation/core/component-lifecycle.js** - Pattern reference for adding algorithm phase methods via mixin pattern (88 lines)

---

## HANDOFF CONTEXT FOR PHASE 3

**Next Agent**: base-class-implementer

**Priority Order**:
1. Implement State-Snapshot-Recovery-Protocol in BaseAutoFixer FIRST
2. Then Perceptual-Loop in BaseScanner
3. Then Verification-Gate-Protocol in BaseValidator

**Settings Integration**:
- Add settings keys AS EACH base class is implemented
- Provide accessor functions immediately after adding keys

**Verification**:
- Run `npm run verify-codebase` after each base class refactor
- Ensure all 146 implementation files still work

