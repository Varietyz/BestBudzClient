# Development Rules Algorithms

Algorithms that transform architectural principles (DEV-RULES.md) and architectural laws (ARCHLAB.md) into executable development protocols.

These algorithms are universally applicable to any refactoring, development, or architectural task.

Each algorithm is itself queryable data: loop class, semantic guarantees, and principle mappings are first-class attributes.
The algorithms compose: output of one can be input to another (see Integration Example).

---

## 1. MODULE SEPARATION PROTOCOL

[Module-Separation-Protocol] **Class: Meta Loop**

**ANALYZE<Module> → FIND<Concerns> → EXTRACT<Single-Concern> → CREATE<Isolated-Module> → VERIFY<Separation>**

1. **ANALYZE<Module>** – ANALYZE <target_module> FOR <responsibility_count> USING <change_reason_analysis> AGAINST <SOC_principle>.
2. **FIND<Concerns>** – FIND <distinct_concerns> IN <module_behavior> BASED_ON <change_triggers> AND <stakeholder_analysis>.
3. **EXTRACT<Single-Concern>** – EXTRACT <primary_concern> FROM <multiple_concerns> INTO <focused_responsibility> USING <cohesion_metrics>.
4. **CREATE<Isolated-Module>** – CREATE <new_module_file> FROM <extracted_concern> USING <kebab_case_naming> WITHOUT <modifying_existing_files>.
5. **VERIFY<Separation>** – VERIFY <module_has_single_concern> AGAINST <SRP_constraint> USING <change_reason_test>.

**Semantic Guarantees:**

- Step 1: Non-destructive analysis; module state unchanged
- Step 2: Pattern detection only; no code modification
- Step 3: Structural preservation; concern meaning maintained
- Step 4: Forward-only creation; existing files untouched (LAW 1)
- Step 5: Boolean validation; deterministic correctness

**Mapped Principles:**

- SOC + Whole-System: Each module one concern
- SRP + Constraint-First: One change reason derived from invariants
- LAW 1: Forward-only programming via new file creation

---

## 2. EXTENSION WITHOUT MODIFICATION PROTOCOL

[Extension-Without-Modification-Protocol] **Class: Decision Loop**

**CREATE<Extension-Point> → FIND<Variation-Boundary> → FILTER<Stable-Core> → EXECUTE<Interface-Injection>**

1. **CREATE<Extension-Point>** – CREATE <base_class_interface> FROM <anticipated_variations> USING <polymorphism_patterns> BASED_ON <OCP_principle>.
2. **FIND<Variation-Boundary>** – FIND <behavior_change_locations> IN <system_design> BASED_ON <change_frequency_analysis> FOR <extension_isolation>.
3. **FILTER<Stable-Core>** – FILTER <system_components> TO <invariant_behavior_subset> USING <change_history> WITHOUT <modification_permission>.
4. **EXECUTE<Interface-Injection>** – EXECUTE <abstraction_insertion> ON <variation_points> USING <dependency_injection> FROM <registry_hub>.

**Semantic Guarantees:**

- Step 1: Non-deterministic creation; generates candidate interfaces
- Step 2: Boolean existence check; non-invasive pattern detection
- Step 3: Preserves order; removes volatile elements only
- Step 4: Side effects possible; modifies dependency graph via registry

**Mapped Principles:**

- OCP + Minimal Interfaces: Extend without modification
- LAW 1: Forward-Only Programming
- LAW 7: Single Import Boundary via registry injection
- ExtendViaComposablePrimitives: Few minimal interfaces

---

## 3. DEPENDENCY INVERSION PROTOCOL

[Dependency-Inversion-Protocol] **Class: Construction Loop**

**EXTRACT<Concrete-Dependencies> → CREATE<Abstraction-Layer> → ANALYZE<Registry-Token> → WRITE<Registry-Entry>**

1. **EXTRACT<Concrete-Dependencies>** – EXTRACT <direct_class_imports> FROM <module_import_statements> INTO <dependency_list> USING <AST_parser>.
2. **CREATE<Abstraction-Layer>** – CREATE <interface_contract> FROM <concrete_type_signatures> USING <contract_extraction> BASED_ON <DIP_principle>.
3. **ANALYZE<Registry-Token>** – ANALYZE <interface_contract> FOR <semantic_token> USING <meaning_based_addressing> AGAINST <registry_schema>.
4. **WRITE<Registry-Entry>** – WRITE <token_interface_mapping> INTO <centralized_registry> USING <register_method> WITHOUT <direct_imports>.

**Semantic Guarantees:**

- Step 1: Preserves source meaning; extracts dependency structure
- Step 2: Deterministic creation; structured artifact from components
- Step 3: Deep examination; delegates to registry validation
- Step 4: Creates new entry; idempotent registration

**Mapped Principles:**

- DIP + Homoiconicity: Depend on abstractions
- ISP + Semantic Addressing: Reference by meaning not location
- LAW 7: Single Import Boundary (Antenna Hub)
- CentralizeAsVersionedSymbols: Registry is symbol table
- InvertToInterchangeableAbstractions: Code/data/state interchangeable

---

## 4. STATE SNAPSHOT RECOVERY PROTOCOL

[State-Snapshot-Recovery-Protocol] **Class: Construction Loop**

**EXTRACT<Current-State> → CREATE<Checkpoint-Snapshot> → ANALYZE<State-Integrity> → WRITE<Checkpoint-Storage>**

1. **EXTRACT<Current-State>** – EXTRACT <complete_viewport_state> FROM <active_session> INTO <serializable_structure> USING <state_capture_mechanism>.
2. **CREATE<Checkpoint-Snapshot>** – CREATE <atomic_checkpoint> FROM <viewport_state> USING <immutable_snapshot_pattern> BASED_ON <semantic_boundaries>.
3. **ANALYZE<State-Integrity>** – ANALYZE <checkpoint_completeness> AGAINST <recovery_requirements> FOR <full_restoration_capability> USING <integrity_validation>.
4. **WRITE<Checkpoint-Storage>** – WRITE <checkpoint_data> INTO <authenticated_storage> USING <encryption> WITH <passkey_requirement>.

**Semantic Guarantees:**

- Step 1: Preserves source meaning; non-destructive capture
- Step 2: Deterministic creation; frozen state artifact
- Step 3: Evaluation check; non-modifying validation
- Step 4: Creates new entry; idempotent with authentication gate

**Mapped Principles:**

- Immutability + Single Assignment: Snapshots frozen after creation
- EnsureRepeatableWithQuickRecovery: Crash-safe recovery designed-in
- SnapshotMeaningBeforeMutation: Pre-action state capture
- SeparateConcernsRestoreBySnapshot: Atomic restoration
- LAW 5: Authenticated State Recovery
- State-Before-Mutation: Resource state protected

---

## 5. INTENTION EMISSION PROTOCOL

[Intention-Emission-Protocol] **Class: Perceptual Loop**

**FIND<User-Action> → ANALYZE<Intention> → CREATE<Intent-Event> → WRITE<Event-Bus>**

1. **FIND<User-Action>** – FIND <interaction_trigger> IN <child_component> BASED_ON <event_listeners> FOR <intention_detection>.
2. **ANALYZE<Intention>** – ANALYZE <action_context> FOR <semantic_meaning> USING <intention_classification> AGAINST <intent_vocabulary>.
3. **CREATE<Intent-Event>** – CREATE <structured_intention> FROM <classified_action> USING <event_object_pattern> WITHOUT <parent_reference>.
4. **WRITE<Event-Bus>** – WRITE <intention_event> INTO <centralized_event_bus> USING <emit_method> FOR <parent_observation>.

**Semantic Guarantees:**

- Step 1: Boolean existence check; non-invasive detection
- Step 2: Deep examination; classifies without modification
- Step 3: Non-deterministic creation; generates intention candidates
- Step 4: Creates new entry; unidirectional upward flow

**Mapped Principles:**

- DCB/CF + Execution Negotiation: No parent callbacks
- EmitEventsAllowInterruption: Event-based communication
- LAW 2: No Child-to-Parent Callbacks
- ForbidCallbacksAllowNegotiation: Children emit, parents decide
- LayerWithLogicalTime: Unidirectional data flow

---

## 6. SECURITY INHERITANCE PROTOCOL

[Security-Inheritance-Protocol] **Class: Hypothesis-Validation Loop**

**CREATE<Security-Base-Class> → ANALYZE<Attack-Surface> → ANALYZE<Capability-Constraints> → EXECUTE<Enforcement>**

1. **CREATE<Security-Base-Class>** – CREATE <base_security_class> FROM <security_invariants> USING <capability_inheritance_pattern> BASED_ON <trust_boundaries>.
2. **ANALYZE<Attack-Surface>** – ANALYZE <input_validation_points> FOR <injection_vulnerabilities> USING <sanitization_requirements> AGAINST <CSP_policy>.
3. **ANALYZE<Capability-Constraints>** – ANALYZE <inherited_security_methods> AGAINST <bypass_attempts> FOR <architectural_enforcement> USING <override_prevention>.
4. **EXECUTE<Enforcement>** – EXECUTE <security_validation> ON <all_inheritors> USING <base_class_checks> WITHOUT <opt_out_capability>.

**Semantic Guarantees:**

- Step 1: Non-deterministic creation; security policy artifact
- Step 2: Deep examination; delegates to security analysis tools
- Step 3: Evaluation check; validates against specification
- Step 4: Side effects possible; modifies execution environment with security gates

**Mapped Principles:**

- LAW 4: Security as Base-Inherited Capability
- EncapsulateButShipInspector: Hide internals, audit visible
- FailImmediatelyHaltOnInconsistency: Security violations halt
- IsolateModulesProtectState: Sandboxed units
- DeriveResponsibilityFromInvariants: Security from constraints
- LAW 3: Inheritance Over Configuration

---

## 7. HUMAN AUTHORITY PROTOCOL

[Human-Authority-Protocol] **Class: Decision Loop**

**CREATE<Authority-Tiers> → FIND<Override-Request> → FILTER<Human-Justification> → EXECUTE<Authorized-Action>**

1. **CREATE<Authority-Tiers>** – CREATE <authority_hierarchy> FROM <trust_model> USING <tier_ranking> BASED_ON <KERNEL > USER > SYSTEM > AI>.
2. **FIND<Override-Request>** – FIND <invariant_violation_attempts> IN <execution_flow> BASED_ON <constraint_checking> FOR <human_escalation>.
3. **FILTER<Human-Justification>** – FILTER <override_requests> TO <justified_subset> USING <minimum_10_char_explanation> BASED_ON <human_input>.
4. **EXECUTE<Authorized-Action>** – EXECUTE <override_operation> ON <target_system> USING <passkey_authentication> WITH <justification_metadata>.

**Semantic Guarantees:**

- Step 1: Deterministic creation; structured authority artifact
- Step 2: Boolean existence check; non-invasive violation detection
- Step 3: Preserves order; removes unjustified requests
- Step 4: Side effects possible; executes with human authority proof

**Mapped Principles:**

- LAW 6: Human-Controlled Authority Boundary
- IncludeHumanInComputation: Human is part of execution
- AttachJustificationToValues: Decisions carry why
- DeclareIntentExplainAtRuntime: System explains itself
- Human-in-Loop: System explains continuously
- LAW 5: Authenticated State Recovery (passkey requirement)

---

## 8. COMPLEXITY BOUNDARY PROTOCOL

[Complexity-Boundary-Protocol] **Class: Meta Loop**

**FIND<Complexity-Violations> → ANALYZE<Cognitive-Load> → EXTRACT<Excess-Content> → CREATE<Split-Modules> → ITERATE<Validation>**

1. **FIND<Complexity-Violations>** – FIND <oversized_files> IN <codebase> BASED_ON <150_line_threshold, 6_file_per_folder_limit> USING <metrics_scanner>.
2. **ANALYZE<Cognitive-Load>** – ANALYZE <file_contents> FOR <human_comprehension_difficulty> USING <complexity_metrics> AGAINST <partial_correctness_threshold>.
3. **EXTRACT<Excess-Content>** – EXTRACT <separable_concerns> FROM <large_files> INTO <logical_groupings> USING <concern_analysis> FOR <modularization>.
4. **CREATE<Split-Modules>** – CREATE <smaller_files> FROM <extracted_content> USING <kebab_case_naming> WITHOUT <modifying_original> BASED_ON <forward_only_law>.
5. **ITERATE<Validation>** – ITERATE <protocol> ON <new_modules> USING <recursive_checking> FOR <all_files_within_bounds>.

**Semantic Guarantees:**

- Step 1: Boolean existence check; non-invasive scan
- Step 2: Deep examination; delegates to cognitive analysis
- Step 3: Preserves source meaning; extracts structure
- Step 4: Deterministic creation; forward-only file generation
- Step 5: Maintains session context; sequential validation

**Mapped Principles:**

- Bounded Complexity + Partial Correctness: 150 lines/6 files limit
- ConcentrateLogicConstrainCognition: Human mental capacity as design parameter
- BoundComplexityAcceptPartialCorrectness: Stop on uncertainty
- LAW 1: Forward-Only Programming
- ScaleMonotonically: Handle growth without redesign

---

## 9. REGISTRY RESOLUTION PROTOCOL

[Registry-Resolution-Protocol] **Class: Cognitive Loop**

**READ<Registry-State> → FIND<Semantic-Token> → LINK<Token-To-Implementation> → CREATE<Resolved-Dependency>**

1. **READ<Registry-State>** – READ <centralized_registry> FROM <registry_hub> USING <registry_api> FOR <available_capabilities>.
2. **FIND<Semantic-Token>** – FIND <requested_capability_token> IN <registry_entries> BASED_ON <semantic_addressing> USING <meaning_based_lookup>.
3. **LINK<Token-To-Implementation>** – LINK <semantic_token> TO <concrete_implementation> USING <registry_mapping> BETWEEN <abstraction_and_instance>.
4. **CREATE<Resolved-Dependency>** – CREATE <dependency_instance> FROM <linked_implementation> USING <instantiation_mechanism> WITHOUT <direct_import>.

**Semantic Guarantees:**

- Step 1: Non-destructive read; registry unmodified
- Step 2: Boolean existence check; non-invasive lookup
- Step 3: Creates bidirectional association; token ↔ implementation
- Step 4: Deterministic creation; structured dependency artifact

**Mapped Principles:**

- LAW 7: Single Import Boundary (Antenna Hub)
- CentralizeAsVersionedSymbols: Registry is truth source
- SegregateByMeaning: Tokens are semantic addresses
- MinimizeDependenciesDiagnoseSymbolically: Single dependency point
- ISP + Semantic Addressing: Reference by meaning not location
- ExtendViaComposablePrimitives: register() + resolve() primitives

---

## 10. VERIFICATION GATE PROTOCOL

[Verification-Gate-Protocol] **Class: Hypothesis-Validation Loop**

**CREATE<Invariant-Set> → ANALYZE<System-State> → ANALYZE<Violations> → EXECUTE<Halt-Or-Continue>**

1. **CREATE<Invariant-Set>** – CREATE <constraint_specifications> FROM <architectural_laws> USING <assertion_rules> BASED_ON <DEV_RULES_principles>.
2. **ANALYZE<System-State>** – ANALYZE <runtime_state> FOR <invariant_compliance> USING <constraint_checking> AGAINST <invariant_set>.
3. **ANALYZE<Violations>** – ANALYZE <detected_inconsistencies> AGAINST <severity_classification> FOR <resource_vs_computation_context> USING <error_taxonomy>.
4. **EXECUTE<Halt-Or-Continue>** – EXECUTE <system_halt> ON <resource_invariant_violations> OR <mark_invalid_and_continue> ON <computation_uncertainty> USING <fail_fast_principle>.

**Semantic Guarantees:**

- Step 1: Non-deterministic creation; generates constraint candidates
- Step 2: Deep examination; delegates to validation tools
- Step 3: Evaluation check; validates against severity specification
- Step 4: Side effects possible; modifies execution environment (halt or mark)

**Mapped Principles:**

- Fail Fast + Deterministic Correctness: Detect errors immediately, halt on inconsistency
- FailImmediatelyHaltOnInconsistency: Never continue with invalid resource state
- AllowMarkedInvalidState: Computation uncertainty OK if marked
- Explicit Invalidity: Temporary inconsistency marked
- AvoidStateUndoSemantically: Undo meaning not bytes
- DeriveResponsibilityFromInvariants: Behavior from constraints

---

## CROSS-REFERENCE: PRINCIPLES TO ALGORITHMS

Bidirectional lookup: Given a principle → find implementing algorithms. Given an algorithm → see which principles it enforces.

| DEV-RULE Principle                       | Primary Algorithm                       | Loop Class                 | Secondary Algorithms                         |
| ---------------------------------------- | --------------------------------------- | -------------------------- | -------------------------------------------- |
| SOC + Whole-System                       | Module-Separation-Protocol              | Meta Loop                  | Complexity-Boundary-Protocol                 |
| KISS + Symbolic Compression              | Complexity-Boundary-Protocol            | Meta Loop                  | -                                            |
| DRY + Structure Sharing                  | Module-Separation-Protocol              | Meta Loop                  | -                                            |
| SRP + Constraint-First                   | Module-Separation-Protocol              | Meta Loop                  | Verification-Gate-Protocol                   |
| OCP + Minimal Interfaces                 | Extension-Without-Modification-Protocol | Decision Loop              | -                                            |
| LSP + Confluence                         | -                                       | -                          | (See \_ARCHITECTURAL-WORKFLOW-ALGORITHMS.md) |
| ISP + Semantic Addressing                | Registry-Resolution-Protocol            | Cognitive Loop             | Dependency-Inversion-Protocol                |
| DIP + Homoiconicity                      | Dependency-Inversion-Protocol           | Construction Loop          | Registry-Resolution-Protocol                 |
| DCB/CF + Execution Negotiation           | Intention-Emission-Protocol             | Perceptual Loop            | -                                            |
| Parameterized + Deferred Naming          | -                                       | -                          | (See \_ARCHITECTURAL-WORKFLOW-ALGORITHMS.md) |
| Centralized + Symbol Tables              | Registry-Resolution-Protocol            | Cognitive Loop             | Dependency-Inversion-Protocol                |
| Scalable + Monotonic Growth              | Complexity-Boundary-Protocol            | Meta Loop                  | Extension-Without-Modification-Protocol      |
| Composable + Fluid Phases                | Extension-Without-Modification-Protocol | Decision Loop              | -                                            |
| Extendable + Deliberate Under-Spec       | Extension-Without-Modification-Protocol | Decision Loop              | -                                            |
| Modular + State Over Code                | Module-Separation-Protocol              | Meta Loop                  | -                                            |
| Hierarchical + Semantic Memory           | Registry-Resolution-Protocol            | Cognitive Loop             | -                                            |
| YAGNI + Explicit Non-Goals               | -                                       | -                          | (See \_ARCHITECTURAL-WORKFLOW-ALGORITHMS.md) |
| Fail Fast + Deterministic Correctness    | Verification-Gate-Protocol              | Hypothesis-Validation Loop | -                                            |
| Loose Coupling + Computed Health         | Dependency-Inversion-Protocol           | Construction Loop          | -                                            |
| High Cohesion + Cognitive Load           | Complexity-Boundary-Protocol            | Meta Loop                  | Module-Separation-Protocol                   |
| Encapsulation + Debug-as-Normal          | Security-Inheritance-Protocol           | Hypothesis-Validation Loop | -                                            |
| Immutability + Single Assignment         | State-Snapshot-Recovery-Protocol        | Construction Loop          | -                                            |
| Idempotency + Crash-Only                 | State-Snapshot-Recovery-Protocol        | Construction Loop          | -                                            |
| Orthogonality + Backtrack by Snapshot    | State-Snapshot-Recovery-Protocol        | Construction Loop          | -                                            |
| Layered + Ordinal Time                   | Intention-Emission-Protocol             | Perceptual Loop            | -                                            |
| Event-driven + Interruptions as Control  | Intention-Emission-Protocol             | Perceptual Loop            | -                                            |
| Declarative + Runtime Explanation        | Human-Authority-Protocol                | Decision Loop              | -                                            |
| Stateless + Semantic Rollback            | State-Snapshot-Recovery-Protocol        | Construction Loop          | -                                            |
| Bounded Complexity + Partial Correctness | Complexity-Boundary-Protocol            | Meta Loop                  | -                                            |
| Human-in-Loop                            | Human-Authority-Protocol                | Decision Loop              | -                                            |

---

## CROSS-REFERENCE: LAWS TO ALGORITHMS

| ARCHLAB Law                                  | Primary Algorithm                       | Supporting Principles                                                   |
| -------------------------------------------- | --------------------------------------- | ----------------------------------------------------------------------- |
| LAW 1: Forward-Only Programming              | Extension-Without-Modification-Protocol | OCP, ScaleMonotonically, ExtendWithoutOverspecifying                    |
| LAW 2: No Child-to-Parent Callbacks          | Intention-Emission-Protocol             | DCB/CF, EmitEventsAllowInterruption, LayerWithLogicalTime               |
| LAW 3: Inheritance Over Configuration        | Security-Inheritance-Protocol           | DeriveResponsibilityFromInvariants, InvertToInterchangeableAbstractions |
| LAW 4: Security as Base-Inherited Capability | Security-Inheritance-Protocol           | EncapsulateButShipInspector, FailImmediatelyHaltOnInconsistency         |
| LAW 5: Authenticated State Recovery          | State-Snapshot-Recovery-Protocol        | EnsureRepeatableWithQuickRecovery, SnapshotMeaningBeforeMutation        |
| LAW 6: Human-Controlled Authority Boundary   | Human-Authority-Protocol                | IncludeHumanInComputation, AttachJustificationToValues                  |
| LAW 7: Single Import Boundary (Antenna Hub)  | Registry-Resolution-Protocol            | CentralizeAsVersionedSymbols, SegregateByMeaning                        |

---

## USAGE GUIDE

### Algorithm Selection Decision Tree

The tree itself is an algorithm: OBSERVE<task> → MATCH<pattern> → SELECT<protocol>.
Each box is a pattern detector, the entire tree is a routing function.

```
┌─────────────────────────────────────────────────────────────────┐
│  TASK: Need to add new feature?                                │
│  → Use: Extension-Without-Modification-Protocol                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: File exceeds 150 lines or folder has 7+ files?          │
│  → Use: Complexity-Boundary-Protocol                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Module has multiple change reasons?                     │
│  → Use: Module-Separation-Protocol                             │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Need to replace direct imports with registry?           │
│  → Use: Dependency-Inversion-Protocol + Registry-Resolution    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Child component needs to communicate upward?            │
│  → Use: Intention-Emission-Protocol                            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Need to save application checkpoint?                    │
│  → Use: State-Snapshot-Recovery-Protocol                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Adding new input validation or security constraint?     │
│  → Use: Security-Inheritance-Protocol                          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: AI attempting to override architectural invariant?      │
│  → Use: Human-Authority-Protocol                               │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  TASK: Need to validate system against architectural rules?    │
│  → Use: Verification-Gate-Protocol                             │
└─────────────────────────────────────────────────────────────────┘
```

### Integration Example

When refactoring a large service file that directly imports dependencies:

1. **EXECUTE Complexity-Boundary-Protocol** – Split oversized file
2. **EXECUTE Module-Separation-Protocol** – Separate concerns into focused modules
3. **EXECUTE Dependency-Inversion-Protocol** – Extract concrete dependencies to interfaces
4. **EXECUTE Registry-Resolution-Protocol** – Register and resolve via semantic tokens
5. **EXECUTE Verification-Gate-Protocol** – Validate all invariants maintained

### Machine-Processable Format

Each algorithm is:

- **Parseable**: Follows strict VERB-CHAIN grammar
- **Executable**: Can be instantiated with concrete types
- **Composable**: Can be chained with other algorithms
- **Verifiable**: Semantic guarantees are testable

Example instantiation:

```
AGENT code-refactorer EXECUTES Module-Separation-Protocol:
  ANALYZE<file-service.ts> FOR <responsibility_count>
  FIND<[file_operations, validation, logging]> IN <module_behavior>
  EXTRACT<file_operations> FROM <multiple_concerns> INTO <primary_responsibility>
  CREATE<file-operations-service.ts> FROM <extracted_concern>
  VERIFY<module_has_single_concern> AGAINST <SRP_constraint>
```

---

## THEORETICAL FOUNDATION

These algorithms synthesize:

- **1960s-1980s Principles**: Symbolic computation, homoiconicity, LISP machine philosophy
- **Modern Best Practices**: SOLID, DRY, KISS, YAGNI
- **Archlab Laws**: Forward-only, no-callbacks, registry hub, human authority
- **PAG-LANG Specification**: Formal verb ontology and loop classification

The result is a **machine-processable development methodology** where:

- Architectural principles become executable algorithms
- Laws become constraint validation protocols
- Development becomes algorithm instantiation
- Refactoring becomes algorithm composition

---

**Meta-Property**: This document is itself an example of:

- **Symbolic Compression**: Stores generators (algorithms) not enumeration (code examples)
- **Homoiconicity**: Principles, laws, and algorithms share same representation structure
- **Semantic Addressing**: Algorithms referenced by meaning (what they enforce) not location
