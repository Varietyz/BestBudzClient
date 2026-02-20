# law-2-enforcer

Execute LAW 2 enforcement agent with verification discipline to detect and remediate callback violations.

## Usage

```bash
claude law-2-enforcer
```

## What This Agent Does

1. **Detects LAW 2 Violations**:
   - Callback props passed to children (`onClick={handler}`)
   - Direct parent references (`this.parent.method()`)
   - Method binding patterns (`.bind(this)` passed downward)
   - Lambda closures capturing parent context
   - Function parameters accepting callbacks

2. **Analyzes Communication Patterns**:
   - Maps parent-child component hierarchies
   - Traces EventBus.emit() usage (compliant intention emission)
   - Traces EventBus.on() usage (compliant parent observation)
   - Identifies missing intention emissions

3. **Generates Remediation Plans**:
   - Callback → EventBus.emit() migration strategies
   - Parent reference → intention emission conversions
   - Detailed file:line remediation steps

4. **Outputs Artifacts**:
   - `LAW-2-VIOLATIONS.md` - All detected violations with evidence
   - `LAW-2-EVENTBUS-USAGE.md` - Compliant patterns and missing usage
   - `LAW-2-REMEDIATION-PLAN.md` - Step-by-step migration strategies
   - `LAW-2-COMMUNICATION-MAP.md` - Parent-child hierarchies

## Inherited Skepticism

This agent includes:
- Tool calibration phase (verifies grep before trusting output)
- Claim verification tracking (verified_claims/refuted_claims arrays)
- Verification rate threshold (0.8 minimum)
- Safe iteration loop patterns (max_iterations, goal_achieved)
- Embedded algorithmic patterns:
  - **Production-System-Cycle**: Rule-based violation detection
  - **Semantic-Network-Spreading-Activation**: Communication flow tracing
  - **Means-Ends-Analysis**: Remediation planning (current → goal via operators)

## LAW 2 Definition

**LAW 2: No Child-to-Parent Callbacks (ForbidCallbacksAllowNegotiation)**

Children emit intentions. Parents observe and decide.
Execution may counter-propose (negotiation pattern).
Data flows down, intentions flow up via EventBus.

**AVOID**: `parent-callbacks` (tight-coupling)
**ENFORCE**: `event-emission` (decoupling)

## Target Files

- `root/archlab-ide/src/renderer/components/**/*.ts`
- `root/archlab-ide/src/renderer/terminal/**/*.ts`

Focus on UI component layer where parent-child relationships are most common.

## Output Location

`.claude/workspace/law-2-enforcement/`

## Example Violation → Remediation

**Before (Violation)**:
```typescript
// Child component
<Button onClick={this.parent.handleClick}>
```

**After (Compliant)**:
```typescript
// Child component
const button = DOM.button({
  on: {
    click: () => EventBus.emit('BUTTON_CLICKED', {id: this.id})
  }
});

// Parent component
EventBus.on('BUTTON_CLICKED', (data) => this.handleClick(data));
```

## Verification Gates

- ✅ Tool calibration (grep accuracy verified)
- ✅ File discovery (target_files.length > 0)
- ✅ Violation detection (cataloged with file:line evidence)
- ✅ EventBus analysis (usage patterns verified)
- ✅ Output artifacts (written and verified)
- ✅ Verification rate >= 0.8

## Safe Iteration Loops

All loops use bounded iteration:
- `max_iterations = 15`
- `iteration_count` tracked
- `goal_achieved` termination condition
- No unbounded `WHILE true` loops

## Notes

- This agent performs **read-only analysis** - it does NOT modify code
- Remediation plans are **suggestions** requiring human review
- EventBus usage is the **compliant pattern** for LAW 2
- Callback patterns may be **false positives** if used for internal component communication (not parent-child)
