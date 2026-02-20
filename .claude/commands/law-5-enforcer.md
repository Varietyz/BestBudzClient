# law-5-enforcer

Execute law-5-enforcer agent to detect and remediate LAW 5 violations (Authenticated State Recovery) with verification discipline

## Usage

$ARGUMENTS

Analyzes the codebase for LAW 5 violations including:
- Unauthenticated state access (no passkey requirement)
- Missing checkpoint/snapshot mechanisms
- Missing audit trails for state mutations
- Destructive mutations (retraction instead of monotonic growth)
- Automatic recovery without authentication
- Silent failures (no explicit invalidity marking)

## Inherited Skepticism

This agent includes:
- Tool calibration phase (verifies grep/read work correctly)
- Claim verification tracking (verified_claims, refuted_claims, violation_claims)
- Verification rate threshold (0.8 minimum)
- Safe iteration loop patterns (max_iterations, goal_achieved)
- Empirical violation detection (grep actual code, don't assume)

## Output Artifacts

- LAW-5-VIOLATIONS.md - Detailed violation report with file:line:code
- LAW-5-STATE-CONTAINERS.md - Inventory of all state management components
- LAW-5-COMPLIANT-PATTERNS.md - Existing compliant patterns to preserve
- LAW-5-REMEDIATION-PLAN.md - Prioritized plan for fixing violations

## Key Detection Patterns

### Violations
- `getState()` without passkey parameter
- `setState(value)` without authentication check
- State mutation without `createCheckpoint()` call
- State changes without `Engine.logger()` audit trail
- `array.splice()` / `delete obj[key]` (retraction patterns)
- `rollback()` without passkey requirement
- Catch blocks without `markInvalid()` or explicit error handling

### Compliant Patterns
- `getState(passkey)` - authenticated access
- `createCheckpoint(passkey)` - checkpoint creation before mutations
- `Engine.logger({...})` - audit trail for state changes
- `array.push()` / `array.append()` - monotonic growth
- `markInvalid(reason)` - explicit invalidity marking

## DEV-RULES Mappings

- **EnsureRepeatableWithQuickRecovery** → Checkpoint/rollback mechanisms
- **SnapshotMeaningBeforeMutation** → Pre-action state capture
- **SeparateConcernsRestoreBySnapshot** → Atomic restoration
- **ForbidRetractionEnforceMonotonicGrowth** → Append-only state mutations
- **MarkInvalidityExplicitly** → Explicit failure states, not silent errors

## Algorithm Context

Uses:
- **Production-System-Cycle** for rule-based violation detection
- **Frame-Based-Reasoning** for state container analysis
- **Dependency-Directed-Backtracking** for state lineage tracing
- **Means-Ends-Analysis** for remediation planning

## Critical Focus

LAW 5 requires:
1. **Passkey authentication** before ANY state access
2. **Checkpoint/snapshot** mechanisms for state recovery
3. **Audit trails** tracking who/when/what/why for mutations
4. **Authenticated recovery** (human approval, not automatic)
5. **Monotonic growth** (append-only, no retractions)
6. **Explicit invalidity** marking (no silent failures)
