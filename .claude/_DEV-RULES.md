INITIALIZE<Architecture> → REPEAT {
OBSERVE<CodeState> → SELECT<Pattern> → CHECK<Invariants> → [IF<Violation> DIVERT<AutoFix>] → APPLY<Pattern> → UPDATE<Architecture>
} UNTIL<Stable>

## AVOID → ENFORCE Mapping

| AVOID                       | Reason                 | ENFORCE                  | Reason                  |
| --------------------------- | ---------------------- | ------------------------ | ----------------------- |
| shortcuts                   | debt                   | constraints              | leverage                |
| backward_compatibility      | debt                   | forward_compatibility    | compounding             |
| fallback                    | debt                   | fail-fast                | clarity                 |
| deprecation                 | debt                   | explicit-removal         | coherence               |
| legacy                      | debt                   | greenfield               | clarity                 |
| dual-path                   | confusion              | single-path              | determinism             |
| deffering                   | forgetting             | immediacy                | continuity              |
| optional                    | user-orientation       | mandatory                | system-orientation      |
| for_now                     | deffering → forgetting | now                      | immediacy → continuity  |
| unobserved-execution        | missed-learning        | observed-execution       | learning                |
| pattern-without-compression | inefficiency           | pattern-with-compression | efficiency              |
| evolution-without-approval  | architectural-drift    | evolution-with-approval  | architectural-integrity |
| feedback-ignored            | stagnation             | feedback-enforced        | adaptation              |
| shared-ownership            | ambiguity              | single-owner             | accountability          |
| unbounded-lifetime          | leaks                  | bounded-lifetime         | deterministic-release   |
| asymmetric-lifecycle        | resource-leaks         | enforced-symmetry        | guaranteed-cleanup      |
| implicit-retention          | hidden-leaks           | explicit-retention       | observable-ownership    |
| discipline-release          | human-error            | structural-release       | automatic-enforcement   |
| mutable-state               | unpredictability       | immutable-data           | reproducibility         |
| silent-errors               | unknown-failure        | errors-as-language       | machine-processable     |
| hidden-invalidity           | false-consistency      | explicit-invalidity      | honest-uncertainty      |
| parent-callbacks            | tight-coupling         | event-emission           | decoupling              |
| retraction                  | complexity             | monotonic-growth         | append-only-scalability |
| location-addressing         | brittleness            | semantic-addressing      | meaning-based-reference |
| timestamp-ordering          | wall-clock-dependency  | ordinal-time             | logical-sequencing      |
| separation                  | duplication            | homoiconicity            | code-data-uniformity    |
| unlimited-complexity        | cognitive-overload     | bounded-complexity       | human-comprehension     |
| metric-health               | symptom-tracking       | computed-health          | symbolic-diagnosis      |

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           PRINCIPLE ARCHITECTURE                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    DATA TRANSFORMATIONS              DATA RESOURCES                         │
│    (what happens to data)            (where data lives)                     │
│              │                              │                               │
│              ▼                              ▼                               │
│    ┌───────────────────┐          ┌───────────────────┐                     │
│    │    COMPUTATION    │          │     RESOURCE      │                     │
│    │    (Stateless)    │◄─────────│    (Stateful)     │                     │
│    ├───────────────────┤  observe ├───────────────────┤                     │
│    │ • Pure functions  │          │ • File handles    │                     │
│    │ • Immutability    │          │ • DB connections  │                     │
│    │ • Idempotency     │          │ • Single owner    │                     │
│    │ • Traces/Replay   │          │ • Lifecycle mgmt  │                     │
│    │ • Mark uncertainty│          │ • Halt on error   │                     │
│    └─────────┬─────────┘          └─────────┬─────────┘                     │
│              │                              │                               │
│              └──────────────┬───────────────┘                               │
│                             │                                               │
│                             ▼                                               │
│              ┌─────────────────────────────┐                                │
│              │         EXECUTION           │                                │
│              │    (Control flow/Events)    │────┐                           │
│              ├─────────────────────────────┤    │ feedback                  │
│              │ • Event-driven              │    │                           │
│              │ • No parent callbacks       │    │                           │
│              │ • Monotonic growth          │    │                           │
│              │ • Backtrack by snapshot     │    │                           │
│              └──────────────┬──────────────┘    │                           │
│                             │                   │                           │
│                             ▼                   │                           │
│              ┌─────────────────────────────┐    │                           │
│              │        STRUCTURAL           │◄───┘                           │
│              │   (Universal + Reflective)  │                                │
│              ├─────────────────────────────┤                                │
│              │ • SOC, KISS, DRY, SOLID     │                                │
│              │ • Symbol tables             │                                │
│              │ • Semantic addressing       │                                │
│              │ • Layered time              │                                │
│              │ • Homoiconicity             │                                │
│              └─────────────────────────────┘                                │
│                             │                                               │
│         ┌───────────────────┴───────────────────┐                           │
│         ▼                                       ▼                           │
│    ┌─────────────┐                       ┌─────────────┐                    │
│    │   HUMAN     │                       │  EVOLUTION  │                    │
│    │  FACTORS    │                       │ PRINCIPLES  │                    │
│    ├─────────────┤                       ├─────────────┤                    │
│    │ • 150 lines │                       │ • YAGNI     │                    │
│    │ • 6 files   │                       │ • Under-spec│                    │
│    │ • Cognitive │                       │ • Non-goals │                    │
│    │ • Expression│                       │ • Modular   │                    │
│    │ • Authority │◄──────────────────────│ • Approved  │                    │
│    └─────────────┘                       └─────────────┘                    │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│  Stateless = "What happens to data"   (verbs: transform, validate, compute) │
│  Stateful  = "Where data lives"       (nouns: file, connection, cache)      │
│  Structural = "How code is organized" (applies to both, observes itself)    │
└─────────────────────────────────────────────────────────────────────────────┘
```

================================================================================
STRUCTURAL PRINCIPLES (Apply to all code)
================================================================================

- SOC + Whole-System: Each module one concern, but reason about whole system
- KISS + Symbolic Compression: Simplest solution via rules/generators, not enumeration
- DRY + Structure Sharing: No duplication via maximal sharing and copy-on-write
- SRP + Constraint-First: One change reason, derived from invariants not features
- OCP + Minimal Interfaces: Extend without modification via few composable primitives
- LSP + Confluence: Substitutable components, operation order independent
- ISP + Semantic Addressing: Interfaces by usage, reference by meaning not location
- DIP + Homoiconicity: Depend on abstractions where code/data/state interchangeable
- Centralized + Symbol Tables: Single truth source via versioned queryable symbols
- Hierarchical + Semantic Memory: Layers/trees where placement reflects meaning
- Layered + Ordinal Time: Horizontal layers with logical clocks over timestamps
- Reflective + Observable Execution: System observes own behavior, traces as queryable data

================================================================================
COMPUTATION DOMAIN (Stateless - pure transformations, data flow)
================================================================================

Context: Functions, transformations, data pipelines, business logic.
State model: Data flows through, never mutates. Outputs are frozen.

- Immutability + Single Assignment: Data frozen, variables assigned exactly once
- Stateless + Semantic Rollback: No persistent state, undo meaning not bytes
- Idempotency + Crash-Only: Same result on repeat, quick recovery designed-in
- Declarative + Runtime Explanation: Specify what not how, system answers "why" live
- Parameterized + Deferred Naming: Behavior via inputs, names assigned when needed
- Composable + Fluid Phases: Combine parts, blur compile/runtime boundaries
- Traces as Objects: Execution traces queryable, replayable, first-class
- Justification Metadata: Values carry why computed, not just origin
- Explicit Invalidity: Temporary inconsistency OK if marked (computation uncertainty)
- Errors as Language: Errors use system vocabulary, machine-processable

================================================================================
RESOURCE DOMAIN (Stateful - lifecycle, ownership, persistence)
================================================================================

Context: Memory, files, connections, handles, long-lived components.
State model: Resources are owned, tracked, and explicitly released.

- Single Owner: Every resource has exactly one deterministic owner responsible for release
- Bounded Lifetime: Lifetime(resource) ⊆ Lifetime(owner), unbounded = leak candidate
- Enforced Symmetry: Open→Close, Subscribe→Unsubscribe, Start→Stop guaranteed and exception-safe
- Reachable ≠ Useful: GC frees unreachable, not unused - architectural leaks survive GC
- Weak Non-Owners: References not implying ownership must be weak or ephemeral
- Explicit Retention: Hidden retention (DI, ORM, observers) must be observable and bounded
- Structural Release: If release depends on human discipline, it will leak - enforce via scope/structure
- Teardown Path: Every long-lived component requires init()→run()→shutdown(), no shutdown = leak
- State-Before-Mutation: Snapshot semantic state before changes (resource state, not computation)
- Predictable GC: Deterministic pauses at semantic boundaries
- Concept GC: Collect unused rules and obsolete concepts

================================================================================
EXECUTION PRINCIPLES (Control flow and events)
================================================================================

Context: How computation and resources interact at runtime.

- DCB/CF + Execution Negotiation: No parent callbacks, execution may counter-propose
- Event-driven + Interruptions as Control: Communicate via events, pause/resume is continuation
- Scalable + Monotonic Growth: Handle growth via append-only, never retract
- Orthogonality + Backtrack by Snapshot: Changes isolated, restore via reinstantiation
- Fail Fast + Deterministic Correctness: Detect errors immediately, halt on resource inconsistency

================================================================================
HUMAN FACTORS (Cognitive and discipline constraints)
================================================================================

Context: Limits of human comprehension and attention.

- High Cohesion + Cognitive Load: Related logic together, constrained by human comprehension
- Bounded Complexity + Partial Correctness: 150 lines/6 files, stop on uncertainty
- Encapsulation + Debug-as-Normal: Hide internals but ship with live inspector
- Loose Coupling + Computed Health: Minimize dependencies, diagnose symbolically
- Human-in-Loop: Human is part of computation, system explains itself
- Deliberate Slowness: Insert pauses for comprehension when needed

================================================================================
EVOLUTION PRINCIPLES (Change over time)
================================================================================

Context: How the system grows and adapts.

- Modular + State Over Code: Independent swappable units, protect state first
- Extendable + Deliberate Under-Spec: Add features externally, leave room for evolution
- YAGNI + Explicit Non-Goals: Don't build unneeded code, document what system does NOT do

================================================================================
TENSION RESOLUTIONS
================================================================================

These principles appear to conflict but apply to different domains:

1. Stateless vs State-Before-Mutation
   → Computation is stateless (data flows through, never persists)
   → Resources get snapshotted before mutation (state exists, is tracked)

2. Immutability vs Modular + State Over Code
   → Computed data is frozen after creation (immutable outputs)
   → Resource state is precious and protected (mutable but managed)

3. Fail Fast vs Explicit Invalidity
   → Resource invariant violation → HALT immediately (Fail Fast)
   → Computation uncertainty → MARK and continue (Explicit Invalidity)

4. YAGNI vs Deliberate Under-Spec
   → No code for hypothetical features (YAGNI applies to implementation)
   → No constraints blocking future features (Under-Spec applies to interfaces)

5. Single Owner vs Homoiconicity
   → Ownership applies to runtime resources (memory, handles)
   → Homoiconicity applies to definitions (code/data/config as same structure)

================================================================================
DIRECTIVE MAPPINGS
================================================================================

{ "Directive": "EnforceSingleConcernWithSystemView", "Clarification": "Module handles one concern while maintaining whole-system mental model" },
{ "Directive": "CompressToSimplest", "Clarification": "Simplest solution via rules/generators, never raw enumeration" },
{ "Directive": "EliminateDuplicationViaSharing", "Clarification": "No duplicate logic, maximize structure sharing with copy-on-write" },
{ "Directive": "DeriveResponsibilityFromInvariants", "Clarification": "Single change reason derived from constraints, not features" },
{ "Directive": "ExtendViaComposablePrimitives", "Clarification": "Open for extension via few minimal composable interfaces" },
{ "Directive": "EnsureSubstitutabilityAndConfluence", "Clarification": "Replaceable components where operation order doesn't affect result" },
{ "Directive": "SegregateByMeaning", "Clarification": "Interfaces contain only used methods, addressed semantically" },
{ "Directive": "InvertToInterchangeableAbstractions", "Clarification": "Depend on abstractions where code/data/state are same class" },
{ "Directive": "ForbidCallbacksAllowNegotiation", "Clarification": "No parent callbacks, but execution may refuse or counter-propose" },
{ "Directive": "ParameterizeWithDeferredNames", "Clarification": "Behavior controlled by inputs, names assigned only when necessary" },
{ "Directive": "CentralizeAsVersionedSymbols", "Clarification": "Single truth source via queryable persistent symbol tables" },
{ "Directive": "ScaleMonotonically", "Clarification": "Handle growth without redesign via append-only structures" },
{ "Directive": "ComposeAcrossPhases", "Clarification": "Combine modules freely, no hard compile/runtime boundary" },
{ "Directive": "ExtendWithoutOverspecifying", "Clarification": "Add features externally, deliberately leave room for evolution" },
{ "Directive": "IsolateModulesProtectState", "Clarification": "Independent swappable units, state is precious code is replaceable" },
{ "Directive": "OrganizeHierarchicallyByMeaning", "Clarification": "Layers/trees where memory placement reflects semantic relationships" },
{ "Directive": "AvoidUnnecessaryDocumentNonGoals", "Clarification": "Don't build unneeded features, explicitly state what system won't do" },
{ "Directive": "FailImmediatelyHaltOnInconsistency", "Clarification": "Detect and report errors immediately, never continue with invalid resource state" },
{ "Directive": "MinimizeDependenciesDiagnoseSymbolically", "Clarification": "Reduce coupling, infer system health rather than metric it" },
{ "Directive": "ConcentrateLogicConstrainCognition", "Clarification": "Related code together, human mental capacity is design parameter" },
{ "Directive": "EncapsulateButShipInspector", "Clarification": "Hide internals, but production supports live introspection" },
{ "Directive": "FreezeDataAssignOnce", "Clarification": "Computed data immutable after creation, variables assigned exactly once" },
{ "Directive": "EnsureRepeatableWithQuickRecovery", "Clarification": "Same result on repeat, state reconstruction designed-in" },
{ "Directive": "SeparateConcernsRestoreBySnapshot", "Clarification": "Changes don't affect unrelated areas, backtrack via reinstantiation" },
{ "Directive": "LayerWithLogicalTime", "Clarification": "Horizontal organization with phase markers over wall clocks" },
{ "Directive": "EmitEventsAllowInterruption", "Clarification": "Communicate via events, pause/inspect/resume is control flow" },
{ "Directive": "DeclareIntentExplainAtRuntime", "Clarification": "Specify what not how, system answers 'why' during execution" },
{ "Directive": "AvoidStateUndoSemantically", "Clarification": "Computation has no persistent state, rollback restores meaning not bytes" },
{ "Directive": "BoundComplexityAcceptPartialCorrectness", "Clarification": "150 lines/6 files max, stop with uncertainty over silent incorrectness" },
{ "Directive": "TreatTracesAsFirstClass", "Clarification": "Execution traces are manipulable, queryable, replayable objects" },
{ "Directive": "AttachJustificationToValues", "Clarification": "Values carry why they were computed, not just provenance" },
{ "Directive": "AllowMarkedInvalidState", "Clarification": "Computation uncertainty permitted if explicitly marked" },
{ "Directive": "PauseGCAtSemanticBoundaries", "Clarification": "Deterministic GC pauses aligned with phase transitions" },
{ "Directive": "CollectUnusedConcepts", "Clarification": "GC finds obsolete rules and abandoned hypotheses, not just memory" },
{ "Directive": "TreatRulesAsData", "Clarification": "Constraints and invariants are queryable first-class objects, not implicit assumptions" },
{ "Directive": "EnforceSingleOwnership", "Clarification": "Every runtime resource has exactly one owner; shared ownership requires ref-counting or weak refs" },
{ "Directive": "BoundLifetimeToOwner", "Clarification": "Resource lifetime must be subset of owner lifetime, unbounded = leak candidate" },
{ "Directive": "GuaranteeSymmetricRelease", "Clarification": "Open→Close, Subscribe→Unsubscribe must be guaranteed, single-path, exception-safe" },
{ "Directive": "DistinguishReachableFromUseful", "Clarification": "GC frees unreachable not unused; architectural leaks via caches/observers survive GC" },
{ "Directive": "WeakenNonOwningReferences", "Clarification": "References not implying ownership must be weak, ephemeral, or recalculable" },
{ "Directive": "MakeRetentionExplicit", "Clarification": "Hidden retention in DI/ORM/signals must be observable, bounded, and documented" },
{ "Directive": "EnforceReleaseStructurally", "Clarification": "If release depends on discipline it will leak; enforce via scope/RAII/structure" },
{ "Directive": "RequireTeardownPath", "Clarification": "Long-lived components need init→run→shutdown; missing shutdown = guaranteed leak" },
{ "Directive": "SnapshotMeaningBeforeMutation", "Clarification": "Log resource state before changes, discard only on commit" },
{ "Directive": "AllowDeliberateSlowness", "Clarification": "Insert pauses for human comprehension and inspection" },
{ "Directive": "MakeErrorsPartOfLanguage", "Clarification": "Errors use system vocabulary, parseable and processable" },
{ "Directive": "IncludeHumanInComputation", "Clarification": "Human is part of execution, system explains itself continuously" }
