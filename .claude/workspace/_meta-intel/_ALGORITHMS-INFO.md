# Algorithms: Info

## PREPOSITION SLOT DEFINITIONS

| Slot     | Meaning                           | When Used                                         |
| -------- | --------------------------------- | ------------------------------------------------- |
| FROM     | Input origin                      | READ data, EXTRACT structure, COLLECT from source |
| IN       | Location container                | FIND patterns, GREP within structure              |
| INTO     | Destination container             | WRITE outputs, EXTRACT components, COLLECT items  |
| ON       | Direct target of operation        | EXECUTE tools, ITERATE on input                   |
| TO       | Intended destination format/state | FILTER subset, SET value, WRITE destination       |
| USING    | Process, mechanism, or toolset    | Any action requiring method specification         |
| BASED_ON | Criteria used for selection       | FILTER, RANK, SET strategy                        |
| AGAINST  | Evaluation reference              | ANALYZE correctness, COMPARE criteria             |
| FOR      | Objective or purpose              | ANALYZE purpose, INVESTIGATE reason               |
| BETWEEN  | Relational mapping                | LINK elements, COMPARE differences                |
| WITHOUT  | Explicit exclusion                | FILTER constraints, SET without condition         |

## VERBAL ONTOLOGY: PAG-LANG ACTION_KEYWORDS

| Category      | Verb    | Definition                   | Typical Preposition   | Semantic Guarantee                               |
| ------------- | ------- | ---------------------------- | --------------------- | ------------------------------------------------ |
| **INPUT**     | READ    | Acquire data                 | FROM external source  | Non-destructive read; source unmodified          |
|               | EXTRACT | Decompose structure          | INTO components       | Preserves source meaning; extracts structure     |
| **PATTERN**   | FIND    | Locate pattern existence     | IN data               | Boolean existence check; non-invasive            |
|               | ANALYZE | Examine pattern properties   | FOR characteristics   | Deep examination; delegates to tools             |
| **OUTPUT**    | CREATE  | Produce new content          | USING algorithm       | Non-deterministic; creates candidate set         |
|               | CREATE  | Construct structure          | FROM components       | Deterministic; creates structured artifact       |
| **NARROWING** | FILTER  | Remove non-matching elements | TO subset             | Removes elements; preserves order                |
| **EXECUTION** | EXECUTE | Invoke tool sequence         | ON target             | Side effects possible; modifies environment      |
|               | ANALYZE | Examine correctness          | AGAINST criteria      | Evaluation check; non-modifying                  |
| **TRANSFER**  | WRITE   | Write data                   | INTO storage location | Creates new entry; idempotent if overwrite       |
|               | EXECUTE | Invoke modification tool     | ON target             | Tool performs actual modification; LLM delegates |
| **RELATION**  | LINK    | Establish connections        | BETWEEN elements      | Creates bidirectional associations               |
| **CONTROL**   | ITERATE | Repeat algorithm             | ON next input         | Maintains session context; sequential            |
| **SEQUENCE**  | ORDER   | Impose sequence              | ON elements           | Deterministic ordering; preserves elements       |
|               | SORT    | Sort by criteria             | USING comparator      | Deterministic ordering; criteria-based           |
|               | RANK    | Prioritize by score          | BASED_ON metrics      | Priority-based ordering; score-driven            |
| **STATE**     | SET     | Establish state              | TO value              | Assigns state; idempotent                        |
|               | COLLECT | Combine internal states      | FROM sources          | Conflict resolution required; deterministic      |
| **MAPPING**   | MAP     | Transform elements           | TO target space       | One-to-one or one-to-many transformation         |
|               | MATCH   | Establish correspondence     | TO target pattern     | Pattern matching; deterministic                  |
|               | IDENTIFY| Recognize specific instance  | IN dataset            | Classification; deterministic                    |
| **GENERATION**| GENERATE| Produce from template        | USING rules           | Template-based creation; deterministic           |
|               | COMPOSE | Assemble from parts          | FROM components       | Combines elements into whole; deterministic      |
| **VALIDATION**| VERIFY  | Confirm correctness          | AGAINST specification | Boolean validation; non-modifying                |
|               | APPLY   | Execute transformation       | ON target             | Modifies target; side effects possible           |
| **SELECTION** | PRIORITIZE| Establish importance order | BASED_ON criteria     | Importance-based ranking; score-driven           |
|               | SCAN    | Sequential examination       | FOR patterns          | Iterative search; non-modifying                  |

---

## ALGORITHM CLASSIFICATION

| Class                          | Characteristics                                         | Verbs Pattern                        |
| ------------------------------ | ------------------------------------------------------- | ------------------------------------ |
| **Perceptual Loop**            | Signal detection → Analysis → Response                  | FIND → ANALYZE → WRITE/CREATE        |
| **Cognitive Loop**             | Information processing → Understanding → Integration    | READ → FIND → LINK → CREATE          |
| **Decision Loop**              | Option generation → Evaluation → Selection → Execution  | CREATE → FIND → FILTER → EXECUTE     |
| **Construction Loop**          | Component assembly → Validation → Storage               | EXTRACT → CREATE → ANALYZE → WRITE   |
| **Adaptive Loop**              | Environment sensing → Strategy adjustment → Iteration   | READ → ANALYZE → EXECUTE → ITERATE   |
| **Meta Loop**                  | Self-monitoring → Strategy refinement → Optimization    | FIND → ANALYZE → EXECUTE → ITERATE   |
| **Hypothesis-Validation Loop** | Hypothesis generation → Constraint testing → Refinement | CREATE → ANALYZE → ANALYZE → EXECUTE |

---

## ALGORITHM INTEGRATION & CREATION GUIDE

[Algorithm-Integration-Protocol]

**FIND<Loop-Pattern> → EXTRACT<Type-Parameters> → SET<Preposition-Slots> → CREATE<Directive-Block>**

1. **FIND<Loop-Pattern>** – FIND <algorithm_class> IN <task_requirements> BASED_ON <classification_table>.
2. **EXTRACT<Type-Parameters>** – EXTRACT <domain_objects> FROM <task_specification> INTO <type_placeholders>.
3. **SET<Preposition-Slots>** – SET <concrete_values> TO <preposition_operators> USING <context_mapping>.
4. **CREATE<Directive-Block>** – CREATE <executable_directive> FROM <instantiated_algorithm> INTO <prompt_structure>.

**Integration Example:**

```
AGENT task-processor EXECUTES Strategy-Refinement-Loop:
  EXTRACT<UserRequest> FROM <input>
  READ<Codebase> FROM <filesystem> USING <Read_tool>
  CREATE<ProblemModel> FROM <parsed_data>
  ...
```

---

[Algorithm-Creation-Protocol]

**READ<Domains> → LINK<Patterns> → FIND<Isomorphisms> → EXTRACT<Template> → ANALYZE<Uniqueness>**

**PHASE 1: Domain Pattern Inspection**

1. **READ<Domains>** – READ <cross_domain_sources> FOR <overlapping_patterns> IN <domain_literature>.
    - Natural Language Processing (semantic parsing, discourse analysis)
    - Theory of Mind research (belief modeling, intention recognition)
    - Psychology white-papers (cognitive processes, behavioral patterns)

**PHASE 2: Capability Mapping**

2. **LINK<Patterns>** – LINK <identified_patterns> TO <implementation_capabilities> USING <feasibility_analysis>.
    - ML/NN architectures (attention mechanisms, recurrent structures)
    - Compiler Theory (parsing, optimization passes, code generation)
    - LLM+CLI capabilities (tool execution, context management)

**PHASE 3: Common Ground Detection**

3. **FIND<Isomorphisms>** – FIND <structural_equivalences> BETWEEN <domain_patterns> USING <pattern_matching>.
    - FIND <verb_sequences> IN <cross_domain_operations>
    - LINK <preposition_relationships> TO <data_flow_patterns>
    - EXTRACT <control_flow> INTO <pag_lang_constructs>

**PHASE 4: Template Abstraction**

4. **EXTRACT<Template>** – EXTRACT <replicable_algorithm> FROM <isomorphic_structures> USING <generalization_rules>.
    - CREATE <verb_chain> FROM <common_operations>
    - SET <type_parameters> FOR <domain_objects>
    - WRITE <semantic_guarantees> FOR <each_step>
    - MARK <algorithm> INTO <algorithm_class>

**PHASE 5: Validation**

5. **ANALYZE<Uniqueness>** – ANALYZE <candidate_algorithm> AGAINST <existing_algorithms> USING <redundancy_checks>.
    - ANALYZE <verb_chain> FOR <redundancy> IN <algorithm_repository>
    - ANALYZE <uniqueness> IN <pattern_space>
    - ANALYZE <keywords> AGAINST <pag_lang_specification>

**Research Protocol:**

- ANALYZE <training_data_patterns> USING <cross_domain_frequency_analysis>
- ANALYZE <findings> USING <websearch> FOR <academic_papers>
- READ <literature> IN <nlp, cognitive_science, compiler_design>
- CREATE <meta_principles> USING <llm_analysis> ON <phi_3_model>

---

# ALGORITHM REPRESENTATION: VERB-CHAIN / DIRECTIVE

:START-TEMPLATE:

[Named-Algorithm]

**VERB<Type> → VERB<Type> → VERB<Type> → ...**

1. **VERB<Type>** – VERB <object> [FROM <source>] [TO <destination>] [INTO <storage>] [USING <method>] [BASED_ON <criteria>] [AGAINST <reference>] [FOR <purpose>] [IN <location>] [ON <target>] [BETWEEN <elements>] [WITHOUT <exclusion>].
2. **VERB<Type>** – VERB <object> [FROM <source>] [TO <destination>] [INTO <storage>] [USING <method>] [BASED_ON <criteria>] [AGAINST <reference>] [FOR <purpose>] [IN <location>] [ON <target>] [BETWEEN <elements>] [WITHOUT <exclusion>].
3. **VERB<Type>** – VERB <object> [FROM <source>] [TO <destination>] [INTO <storage>] [USING <method>] [BASED_ON <criteria>] [AGAINST <reference>] [FOR <purpose>] [IN <location>] [ON <target>] [BETWEEN <elements>] [WITHOUT <exclusion>].
4. ...

:END-TEMPLATE:

---

# ALGORITHM CATEGORIES

Navigate to algorithm category files:

- [Perceptual Loop Algorithms](./algorithms/_PERCEPTUAL-LOOP-ALGORITHMS.md)
- [Adaptive Loop Algorithms](./algorithms/_ADAPTIVE-LOOP-ALGORITHMS.md)
- [Cognitive Loop Algorithms](./algorithms/_COGNITIVE-LOOP-ALGORITHMS.md)
- [Construction Loop Algorithms](./algorithms/_CONSTRUCTION-LOOP-ALGORITHMS.md)
- [Decision Loop Algorithms](./algorithms/_DECISION-LOOP-ALGORITHMS.md)
- [Meta Loop Algorithms](./algorithms/_META-LOOP-ALGORITHMS.md)
- [Hypothesis-Validation Loop Algorithms](./algorithms/_HYPOTHESIS-VALIDATION-LOOP-ALGORITHMS.md)
- [Unified Cognitive Architecture Algorithms](./algorithms/_UNIFIED-COGNITIVE-ARCHITECTURE-ALGORITHMS.md)
