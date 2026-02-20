# law-3-enforcer

Execute LAW 3 Enforcer agent to detect and remediate violations of LAW 3: Inheritance Over Configuration.

## Usage

```
/law-3-enforcer [target-scope]
```

**Arguments**:
- `target-scope` (optional): Glob pattern for files to analyze. Defaults to `root/archlab-ide/src/**/*.ts`

**Examples**:

```bash
# Analyze entire archlab-ide src directory (default)
/law-3-enforcer

# Analyze specific subdirectory
/law-3-enforcer root/archlab-ide/src/renderer/components/**/*.ts

# Analyze validators only
/law-3-enforcer root/archlab-ide/src/renderer/engine/validators/**/*.ts
```

## What This Agent Does

The LAW 3 Enforcer agent performs comprehensive analysis to ensure behavior comes from base class extension rather than configuration objects. It:

### 1. **Detects LAW 3 Violations**
   - **Config Objects**: Constructors accepting config objects defining behavior
   - **Type Switches**: `switch(config.type)` statements that should be polymorphism
   - **Missing Base Extension**: Classes that should extend base classes but don't
   - **Option-Driven Logic**: `if (options.enableX)` controlling features
   - **Behavior in Config**: Methods reading config to determine behavior
   - **Missing Polymorphism**: Same method names without common base

### 2. **Analyzes Inheritance Patterns**
   - Maps base class hierarchies using frame-based reasoning
   - Identifies classes without base class extension
   - Traces capability propagation from base to children
   - Analyzes substitutability (Liskov Substitution Principle)
   - Detects polymorphism gaps and missing override keywords

### 3. **Generates Remediation Strategies**
   - Suggests base class extraction from config-driven code
   - Designs inheritance hierarchies to replace configuration
   - Proposes polymorphic method overrides instead of config switches
   - Converts option objects to base class extensions
   - Generates base-*.ts files for common capability patterns
   - Plans capability propagation strategies

### 4. **Verifies Compliance**
   - Verifies base class modifications propagate to all children
   - Confirms substitutability (child can replace parent)
   - Validates capability inheritance (methods, not config)
   - Tests polymorphism works correctly
   - Ensures no config objects controlling core behavior

## Algorithmic Approach

The agent uses multiple embedded algorithmic patterns:

- **Production-System-Cycle**: For violation detection (rules = LAW 3 principles)
- **Frame-Based-Reasoning**: For inheritance hierarchy analysis (classes as frames, methods as slots, extends as links)
- **Means-Ends-Analysis**: For remediation planning (config-driven → inheritance-driven)
- **Dependency-Directed-Backtracking**: For capability propagation verification
- **Semantic-Network-Spreading-Activation**: For tracing polymorphic method usage

## Output Artifacts

All artifacts are written to `.claude/workspace/.law-3-enforcer/law-3-analysis-{timestamp}/`:

- **LAW-3-VIOLATIONS.json**: All detected violations with file:line references
- **LAW-3-BASE-CLASS-INVENTORY.json**: Existing base classes and their hierarchies
- **LAW-3-CONFIG-TO-INHERITANCE-PLAN.json**: Migration strategies for each violation
- **LAW-3-BASE-CLASS-OPPORTUNITIES.json**: Classes that should extend bases but don't
- **LAW-3-POLYMORPHISM-GAPS.json**: Switch statements that should be polymorphic
- **LAW-3-VERIFICATION-RESULTS.json**: Post-analysis validation results
- **LAW-3-ENFORCEMENT-REPORT.md**: Human-readable summary report

## Inherited Skepticism

This agent includes:
- **Tool calibration phase**: Verifies Grep/Glob accuracy before analysis
- **Claim verification tracking**: Maintains verified_claims and refuted_claims arrays
- **Verification rate threshold**: Requires >= 0.8 verification rate to proceed
- **Safe iteration loop patterns**: Uses max_iterations and goal_achieved to prevent unbounded loops
- **Empirical validation**: Every discovery claim verified before acceptance

## Key Detection Patterns

**Violation Patterns**:
```typescript
// Config object in constructor
constructor(config: {type: string, behavior: Function}) { }

// Type switch
switch(config.type) { case 'A': ...; case 'B': ... }

// Missing base extension
class Panel { }  // Should extend BasePanel

// Option-driven logic
if (options.enableFeature) { doFeature() }
```

**Compliant Patterns**:
```typescript
// Base class extension
class FilePanel extends BasePanel { }

// Method override
override render() { /* specific behavior */ }

// Capability inheritance
// Base provides validate(), children inherit

// Substitutability
const panel: BasePanel = new FilePanel()  // Works!
```

## Base Class Categories

The agent analyzes these base class categories:

- **BaseComponent**: UI components
- **BaseService**: Business logic services
- **BaseValidator**: Validation rules
- **BaseManager**: Lifecycle managers
- **BasePanel**: Panel/modal components
- **BaseFactory**: Object creation

## Verification Gates

Each phase includes validation gates ensuring:

✅ Tools calibrated before use
✅ LAW 3 specification loaded and verified
✅ Base class inventory complete
✅ Inheritance hierarchies mapped
✅ Violations detected via production system
✅ Substitutability analyzed
✅ Remediation plan generated
✅ Capability propagation verified
✅ Safe iteration loops used
✅ Verification rate >= 0.8

## Example Output

```
## LAW 3 Enforcement Complete

**Analysis ID**: law-3-analysis-2026-01-08T12:34:56Z
**Target Scope**: root/archlab-ide/src/**/*.ts
**Verification Rate**: 95.2%

### Results Summary

- Base Classes Found: 14
- Total Violations: 23
  - HIGH: 8
  - MEDIUM: 12
  - LOW: 3
- Migration Strategies: 23
- Base Class Opportunities: 5

### Output Artifacts

- **Violations**: .claude/workspace/.law-3-enforcer/.../evidence/LAW-3-VIOLATIONS.json
- **Base Inventory**: .../phases/02-base-class-inventory.json
- **Remediation Plan**: .../remediation/LAW-3-REMEDIATION-PLAN.json
- **Full Report**: .../LAW-3-ENFORCEMENT-REPORT.md
```

## Related Agents

- **law-1-enforcer**: Enforces Forward-Only Programming
- **law-2-enforcer**: Enforces No Child-to-Parent Callbacks
- **pattern-distiller**: Detects base class opportunities (referenced methodology)
- **code-tracer**: Traces code execution paths (referenced methodology)

## Notes

- Agent uses **bounded loops** with max_iterations=100 to prevent infinite loops
- All tool output is **calibrated and verified** before trust
- Violations are **categorized by severity** (HIGH/MEDIUM/LOW)
- Remediation strategies include **preconditions and postconditions**
- **Frame-based reasoning** models classes as frames with slots and links
- **Spreading activation** traces method usage through inheritance chains
