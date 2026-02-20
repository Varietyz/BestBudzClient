# architectural-inheritor

Execute architectural-inheritor agent with full structural embedding of principles

## Usage

Use this agent as a base template for creating new agents that need architectural discipline.

This agent can be invoked directly or inherited from.

## Structural Embedding

This agent STRUCTURALLY EMBEDS (not references) all architectural rules:

### Embedded Architectural Operators (11 core operators)
- EnforceSingleConcernWithSystemView (SOC + Whole-System)
- CompressToSimplest (KISS + Symbolic Compression)
- EliminateDuplicationViaSharing (DRY + Structure Sharing)
- DeriveResponsibilityFromInvariants (SRP + Constraint-First)
- ExtendViaComposablePrimitives (OCP + Minimal Interfaces)
- EnsureSubstitutabilityAndConfluence (LSP + Confluence)
- SegregateByMeaning (ISP + Semantic Addressing)
- InvertToInterchangeableAbstractions (DIP + Homoiconicity)
- CentralizeAsVersionedSymbols (Centralized + Symbol Tables)
- FailImmediatelyHaltOnInconsistency (Fail Fast + Deterministic Correctness)
- BoundComplexityAcceptPartialCorrectness (Bounded Complexity + Partial Correctness)

### Embedded Problem-Solving Chain (10 phases)
INTAKE → ABSTRACT → DEFINE → CONSTRAIN → EXPLORE → PREDICT → CHOOSE → EXECUTE → VALIDATE → REFINE

Each phase is a traversable START block with:
- Safe iteration loops (max_phase_iterations: 10)
- Goal-achieved termination conditions
- Verification tracking
- Error handling

### Embedded Foundational Algorithms (4 algorithms)
- Means-Ends-Analysis (GPS/SOAR foundation)
- Production-System-Cycle (Rule-based reasoning)
- Dependency-Directed-Backtracking (Hypothesis validation)
- STRIPS-Planning (Construction planning)

### Inherited Skepticism
- Trust anchor with minimal assumptions
- Tool calibration phase (Grep/Glob verification)
- verified_claims and refuted_claims tracking
- Verification rate threshold (0.7 minimum)
- Certainty factors for all claims

### Safe Iteration Loops
- max_iterations: 20 (global)
- max_phase_iterations: 10 (per phase)
- iteration_count tracking
- goal_achieved termination
- No unbounded loops (WHILE true forbidden)

### Operational Directives
- 33 ALWAYS rules (embedded from DEV-RULES fusion principles)
- 22 NEVER rules (embedded constraints)
- All rules enforced during CONSTRAIN phase

## Inheritance by Child Agents

Child agents automatically receive:
1. All architectural operators as control flow (no file reads)
2. Complete problem-solving chain (all 10 phases)
3. Foundational algorithms as callable START blocks
4. Tool calibration and verification discipline
5. Safe iteration loop patterns
6. ALWAYS/NEVER directives
7. Abort handler with partial results

Child agents can:
- Override specific phases (e.g., EXPLORE_PHASE for domain-specific logic)
- Append domain-specific operators to architectural_operators array
- Extend success_criteria in DEFINE phase
- Add custom constraints in CONSTRAIN phase

## Example Child Agent Pattern

```
---
name: child-agent
inherits: architectural-inheritor
---

## OVERRIDE EXPLORE_PHASE for custom exploration
START EXPLORE_PHASE
## Custom exploration logic
## Must still set goal_achieved and respect max_iterations
END EXPLORE_PHASE

## APPEND domain operator
APPEND {
    "name": "CustomOperation",
    "condition": "Custom trigger",
    "action": "CUSTOM_VERB_CHAIN",
    "principle": "Custom principle"
} TO architectural_operators
```

## Verification Guarantees

This agent guarantees:
- Verification rate >= 0.7 for completion
- All claims tracked in verified_claims or refuted_claims
- All loops bounded by max_iterations
- All file operations verify existence first
- All tool output calibrated before trust
- All phases execute sequentially with validation gates
- Graceful abort with partial results on failure

## No External Dependencies

CRITICAL: This agent has ZERO external file dependencies. All algorithms, principles, and patterns are structurally embedded as control flow, not as file references.

## Source Material Embedded

This agent structurally embeds content from:
- DEV-RULES.md (Modern + 60s-80s Fusion Principles)
- _PROBLEM-SOLVING.md (Universal Problem-Solving Chain)
- _ARCHITECTURAL-WORKFLOW-ALGORITHMS.md (33 Architectural Algorithms - 11 core selected)
- _1960s-1980s-THEORETICAL-FOUNDATIONS.md (Historical Algorithms - 4 foundational selected)

All content is embedded as executable control flow, not referenced.
