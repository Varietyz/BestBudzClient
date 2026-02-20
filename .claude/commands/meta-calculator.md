# meta-calculator

Execute meta-calculator agent to analyze domains through META framework (X/Y/Z/W axes) with inherited skepticism and verification discipline.

## Usage

Analyze a file or directory for META coordinates:

```
meta-calculator <path>
```

Examples:
```
meta-calculator .claude/_ARCHLAB.md
meta-calculator root/archlab-ide/src/renderer/
meta-calculator THE-AI-PROBLEM.md
```

## What This Agent Does

The meta-calculator agent computes META dimensions for any domain, concept, or system by analyzing:

### X-Axis (WHAT - Entities)
- Nouns, objects, components, classes, data structures
- "Thing" space - what entities exist in the domain
- Patterns: `class`, `interface`, `type`, `const`, capitalized words

### Y-Axis (HOW - Relationships)
- Verbs, actions, operations, transformations
- Dependencies, connections, workflows, state transitions
- "Doing" space - how entities relate and interact
- Patterns: `function`, `method`, `async`, `→`, `depends on`, `imports`

### Z-Axis (WHY - Constraints)
- Laws, principles, constraints, invariants, rules
- Context, purpose, justification, architectural decisions
- "Meaning" space - why entities matter
- Patterns: `LAW`, `MUST`, `NEVER`, `ALWAYS`, `ENFORCE`, `REQUIRE`

### W-Axis (OBSERVER - Propagation)
- Observer topology, feedback loops, event propagation
- How observation effects ripple through the system
- Patterns: `observe`, `emit`, `event`, `propagate`, `decay`

## Output Artifacts

All outputs are written to `.claude/workspace/_meta-analysis-output/`:

1. **META-COORDINATES.json** - Complete X/Y/Z breakdown with densities and ratios
2. **META-IMPLOSION-POINTS.json** - Where understanding breaks down (ambiguity, isolation, meaninglessness, contradictions)
3. **META-AXIS-BALANCE.json** - X:Y:Z ratios, dominant axis, balance score
4. **META-RECOMMENDATIONS.md** - How to improve META coverage and resolve implosion points
5. **META-VISUALIZATION.md** - ASCII/text visualization of META space

## Implosion Point Detection

The agent detects 6 types of implosion:

1. **Ambiguity Implosion** - Insufficient X-axis (unclear what things are)
2. **Isolation Implosion** - Insufficient Y-axis (unclear how things relate)
3. **Meaninglessness Implosion** - Insufficient Z-axis (unclear why things matter)
4. **Contradiction Implosion** - Conflicting Z-axis constraints
5. **Complexity Implosion** - X/Y explosion without Z-axis pruning
6. **Scope Implosion** - Domain outside META boundaries

## META Completeness Criteria

- **X-Axis Coverage**: ≥ 70% entities identified
- **Y-Axis Coverage**: ≥ 70% relationships mapped
- **Z-Axis Coverage**: ≥ 70% constraints documented
- **Balance Score**: MIN/MAX ratio ≥ 0.3 (no axis dominates excessively)
- **Implosion Resistance**: < 3 implosion points
- **Verification Rate**: ≥ 80% claims verified

## Inherited Skepticism

This agent includes:
- Tool calibration phase (verifies Grep/Glob work correctly before analysis)
- Claim verification tracking (all discoveries empirically verified)
- Verification rate threshold (0.8 minimum)
- Safe iteration loop patterns (max_iterations, goal_achieved)
- Bounded loops (never infinite WHILE loops)
- Explicit invalidity marking (failed verifications tracked)

## Example Analysis

Input: `.claude/_ARCHLAB.md`

META Coordinates:
- **X-Axis**: 5.2 (520 entities - components, classes, modules)
- **Y-Axis**: 4.8 (480 relationships - dependencies, flows, interactions)
- **Z-Axis**: 6.1 (610 constraints - 7 LAWS, principles, invariants)
- **Dominant Axis**: Z (constraint-heavy architecture)
- **Balance Score**: 0.79 (well balanced)
- **Implosion Points**: 0 (no understanding breakdowns)

## Algorithmic Patterns

The agent uses:
- **Production-System-Cycle** for pattern extraction (X/Y/Z rules)
- **Semantic-Network-Spreading-Activation** for relationship tracing (Y-axis)
- **Frame-Based-Reasoning** for entity analysis (X-axis as frames)
- **Constraint-Satisfaction** for Z-axis validation
- **Means-Ends-Analysis** for implosion point identification

## Scope Boundaries

META is applicable to:
- Code analysis (classes, functions, dependencies, laws)
- Architecture (components, relationships, principles)
- Problem statements (entities, relationships, constraints)
- Documentation (concepts, connections, rules)

META is NOT applicable to:
- Raw binary data without structure
- Random noise without patterns
- Domains without observable entities, relationships, or constraints

## DEV-RULES Mappings

- **X-Axis** = Entity space (`AddressSemanticNotLocation`)
- **Y-Axis** = Relationship space (`ForbidCallbacksAllowNegotiation`)
- **Z-Axis** = Constraint space (`DeriveResponsibilityFromInvariants`)
- **W-Axis** = Observer propagation (`EmitEventsAllowInterruption`)

## LAW Mappings (Z-Axis Examples)

- **LAW 1** = Forward-only programming (Z-axis constraint)
- **LAW 2** = No parent callbacks (Z-axis constraint)
- **LAW 3** = Inheritance over configuration (Z-axis constraint)
- **LAW 4** = Security inherited from base (Z-axis constraint)
- **LAW 5** = Authenticated state recovery (Z-axis constraint)
- **LAW 6** = Human authority boundary (Z-axis constraint)
- **LAW 7** = Single import boundary (Z-axis constraint)

## Meta-Implosion Context

META analysis through deliberate implosion:
1. System operates normally (X-Y-Z with W propagation)
2. Deliberately increase decay rate
3. Watch what survives compression
4. What survives = structural necessity, not noise
5. At maximum compression, self-reference still requires observation
6. Observation with no slack creates pressure
7. Pressure resolves into novel structure
8. New structure expands into unexplored X-Y space

The agent identifies where this compression breaks down (implosion points) and recommends structural improvements.

## Notes

- Analysis is read-only (no files modified)
- Verification rate must be ≥ 0.8 or analysis aborts
- All discoveries are empirically verified before reporting
- Safe iteration loops prevent runaway analysis
- Output artifacts are timestamped and versioned
