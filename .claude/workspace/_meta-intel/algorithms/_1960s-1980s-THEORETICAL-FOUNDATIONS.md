# 1960s-1980s Theoretical Foundation Algorithms

[McCulloch-Pitts-Threshold-Logic] **Class: Perceptual Loop**

**READ<Inputs> → ANALYZE<Weights> → EXECUTE<Summation> → ANALYZE<Threshold> → CREATE<Output> → ITERATE<Cycle>**

1. **READ<Inputs>** – READ <input_signals> FROM <connected_neurons> AT <time_t-1>.
2. **ANALYZE<Weights>** – ANALYZE <excitatory_inputs> WITH <weight> AND <inhibitory_inputs> FOR <veto_condition>.
3. **EXECUTE<Summation>** – EXECUTE <weighted_sum> ON <excitatory_inputs> IF <no_inhibitory_active>.
4. **ANALYZE<Threshold>** – ANALYZE <sum> AGAINST <threshold_θ> FOR <firing_condition>.
5. **CREATE<Output>** – CREATE <output_signal> FROM <threshold_comparison> AS <binary_value>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <all_neurons> FOR <next_time_step>.

---

[ELIZA-Pattern-Matching-Loop] **Class: Perceptual Loop**

**READ<Input> → FIND<Keyword> → FIND<Pattern> → EXECUTE<Transform> → CREATE<Response> → ITERATE<Cycle>**

1. **READ<Input>** – READ <user_input> FROM <terminal> USING <preprocessing>.
2. **FIND<Keyword>** – FIND <keyword> IN <input> FROM <keyword_list> BASED_ON <priority_rank>.
3. **FIND<Pattern>** – FIND <decomposition_pattern> FOR <keyword> AGAINST <input_structure>.
4. **EXECUTE<Transform>** – EXECUTE <reassembly_rule> ON <captured_groups> WITH <pronoun_reflection>.
5. **CREATE<Response>** – CREATE <output_text> FROM <reassembly_template> WITH <substitutions>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <next_input> OR RETRIEVE <earlier_transformation> FROM <memory_stack>.

---

[AARON-Generative-Art-Loop] **Class: Construction Loop**

**CREATE<Canvas> → EXECUTE<PrimitiveGeneration> → ANALYZE<Composition> → EXECUTE<Feedback> → ITERATE<Cycle>**

1. **CREATE<Canvas>** – CREATE <drawing_surface> WITH <coordinate_space> AND <initial_state>.
2. **EXECUTE<PrimitiveGeneration>** – EXECUTE <random_selection> FROM <primitive_set> BASED_ON <composition_state>.
3. **ANALYZE<Composition>** – ANALYZE <emerging_shapes> FOR <figure_ground> AND <inside_outside> AND <similarity>.
4. **EXECUTE<Feedback>** – EXECUTE <evaluation> ON <composition> USING <aesthetic_criteria> FOR <advance_or_discard>.
5. **ITERATE<Cycle>** – ITERATE <algorithm> FROM <foreground> TO <background> UNTIL <composition_complete>.

---

[Means-Ends-Analysis-Loop] **Class: Cognitive Loop**

**READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator> → ITERATE<Cycle>**

1. **READ<State>** – READ <current_state> FROM <working_memory> AND READ <goal_state> FROM <problem_specification>.
2. **FIND<Difference>** – FIND <most_significant_difference> BETWEEN <current_state> AND <goal_state> USING <difference_ordering>.
3. **FIND<Operator>** – FIND <operator> IN <operator_table> FOR <reducing_difference> BASED_ON <relevance_index>.
4. **ANALYZE<Preconditions>** – ANALYZE <operator_preconditions> AGAINST <current_state> FOR <applicability>.
5. **EXECUTE<Operator>** – EXECUTE <operator> ON <current_state> OR CREATE<Subgoal> TO <satisfy_preconditions>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <new_state> UNTIL <goal_state_achieved>.

---

[Production-System-Cycle] **Class: Adaptive Loop**

**READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory> → ITERATE<Cycle>**

1. **READ<WorkingMemory>** – READ <working_memory_elements> FROM <current_state>.
2. **FIND<MatchingProductions>** – FIND <productions> IN <production_memory> WHERE <conditions> MATCH <working_memory>.
3. **FILTER<ConflictSet>** – FILTER <matching_productions> TO <single_production> BASED_ON <recency, specificity, priority>.
4. **EXECUTE<SelectedProduction>** – EXECUTE <action_side> OF <selected_production> ON <working_memory>.
5. **WRITE<WorkingMemory>** – WRITE <new_elements> INTO <working_memory> OR REMOVE <elements> FROM <working_memory>.
6. **ITERATE<Cycle>** – ITERATE <recognize_act_cycle> UNTIL <goal_achieved> OR <no_productions_match>.

---

[SOAR-Problem-Space-Search] **Class: Cognitive Loop**

**READ<Context> → CREATE<ProblemSpace> → FIND<Operators> → ANALYZE<Preferences> → EXECUTE<Decision> → FIND<Impasse> → CREATE<Subgoal> → ITERATE<Cycle>**

1. **READ<Context>** – READ <current_state> AND <goal> FROM <working_memory>.
2. **CREATE<ProblemSpace>** – CREATE <problem_space> FROM <state_representation> WITH <operator_set>.
3. **FIND<Operators>** – FIND <applicable_operators> IN <problem_space> FOR <current_state>.
4. **ANALYZE<Preferences>** – ANALYZE <operator_preferences> USING <acceptable, reject, better, best, worst, indifferent>.
5. **EXECUTE<Decision>** – EXECUTE <selected_operator> ON <state> IF <unique_selection_possible>.
6. **FIND<Impasse>** – FIND <impasse_type> IN <tie, conflict, no-change> WHEN <decision_fails>.
7. **CREATE<Subgoal>** – CREATE <subgoal> FROM <impasse> TO <resolve_impasse> IN <new_problem_space>.
8. **ITERATE<Cycle>** – ITERATE <algorithm> ON <subgoal> UNTIL <impasse_resolved> THEN CHUNK<Resolution>.

---

[STRIPS-Planning-Loop] **Class: Construction Loop**

**READ<Goal> → READ<InitialState> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator> → WRITE<State> → ITERATE<Cycle>**

1. **READ<Goal>** – READ <goal_formula> FROM <goal_specification> AS <conjunctive_wffs>.
2. **READ<InitialState>** – READ <initial_world_model> FROM <world_representation> AS <set_of_wffs>.
3. **FIND<Difference>** – FIND <unsatisfied_goal_literals> IN <goal_formula> AGAINST <current_state>.
4. **FIND<Operator>** – FIND <operator> IN <operator_schema> WHERE <add_list> CONTAINS <unsatisfied_literal>.
5. **ANALYZE<Preconditions>** – ANALYZE <operator_preconditions> AGAINST <current_state> OR CREATE<Subgoal>.
6. **EXECUTE<Operator>** – EXECUTE <operator> ON <current_state> USING <add_list> AND <delete_list>.
7. **WRITE<State>** – WRITE <new_state> INTO <world_model> AS <current_state - delete_list + add_list>.
8. **ITERATE<Cycle>** – ITERATE <algorithm> UNTIL <all_goal_literals_satisfied>.

---

[Frame-Based-Reasoning-Loop] **Class: Cognitive Loop**

**READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames> → ITERATE<Cycle>**

1. **READ<Situation>** – READ <perceptual_input> OR <discourse_context> FROM <input_stream>.
2. **FIND<Frame>** – FIND <frame_template> IN <frame_library> FOR <situation_type> BASED_ON <trigger_conditions>.
3. **EXTRACT<Slots>** – EXTRACT <terminal_slots> FROM <selected_frame> WITH <slot_constraints>.
4. **ANALYZE<Assignments>** – ANALYZE <input_data> AGAINST <slot_constraints> FOR <valid_fillers>.
5. **SET<Defaults>** – SET <slot_values> TO <default_assignments> IF <no_explicit_filler>.
6. **LINK<RelatedFrames>** – LINK <current_frame> TO <related_frames> USING <transformation_links>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> ON <next_input> OR ON <linked_frame>.

---

[Script-Application-Loop] **Class: Cognitive Loop**

**READ<Input> → FIND<Script> → SET<Roles> → FIND<SceneTrack> → ANALYZE<Expectations> → CREATE<Inferences> → ITERATE<Cycle>**

1. **READ<Input>** – READ <narrative_input> FROM <text_stream> USING <conceptual_parser>.
2. **FIND<Script>** – FIND <applicable_script> IN <script_library> BASED_ON <header_conditions>.
3. **SET<Roles>** – SET <actor_bindings> TO <script_roles> USING <referent_resolution>.
4. **FIND<SceneTrack>** – FIND <current_scene> IN <script_sequence> BASED_ON <input_match>.
5. **ANALYZE<Expectations>** – ANALYZE <expected_events> AGAINST <actual_input> FOR <confirmation_or_deviation>.
6. **CREATE<Inferences>** – CREATE <implicit_events> FROM <script_expectations> NOT <explicitly_mentioned>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> ON <next_input> TRACKING <script_progression>.

---

[ATN-Parsing-Loop] **Class: Perceptual Loop**

**READ<Input> → SET<State> → FIND<Arc> → ANALYZE<Test> → EXECUTE<Actions> → WRITE<Registers> → ITERATE<Cycle>**

1. **READ<Input>** – READ <next_token> FROM <input_stream> INTO <input_buffer>.
2. **SET<State>** – SET <current_state> TO <initial_state> OF <network> WITH <empty_registers>.
3. **FIND<Arc>** – FIND <outgoing_arcs> FROM <current_state> WHERE <arc_label> MATCHES <input_token>.
4. **ANALYZE<Test>** – ANALYZE <arc_test_conditions> AGAINST <register_contents> FOR <satisfaction>.
5. **EXECUTE<Actions>** – EXECUTE <arc_actions> TO <manipulate_registers> AND <build_structure>.
6. **WRITE<Registers>** – WRITE <partial_parse_structure> INTO <registers> FOR <subsequent_arcs>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> ON <next_state> UNTIL <final_state> OR <PUSH/POP>.

---

[Earley-Chart-Parsing-Loop] **Class: Construction Loop**

**READ<Input> → CREATE<Chart> → EXECUTE<Prediction> → EXECUTE<Scanning> → EXECUTE<Completion> → ITERATE<Cycle>**

1. **READ<Input>** – READ <token_sequence> FROM <input_string> INTO <indexed_array>.
2. **CREATE<Chart>** – CREATE <chart> AS <array_of_state_sets> WITH <S(0)_initialized>.
3. **EXECUTE<Prediction>** – EXECUTE <prediction> FOR <state (X → α • Y β, j)> ADD <(Y → • γ, k)> TO <S(k)>.
4. **EXECUTE<Scanning>** – EXECUTE <scanning> FOR <state (X → α • a β, j)> ADD <(X → α a • β, j)> TO <S(k+1)>.
5. **EXECUTE<Completion>** – EXECUTE <completion> FOR <state (X → γ •, j)> FIND <(Y → α • X β, i)> ADD TO <S(k)>.
6. **ITERATE<Cycle>** – ITERATE <prediction, scanning, completion> FOR <each_position_k> UNTIL <k = n>.

---

[Conceptual-Dependency-Decomposition] **Class: Perceptual Loop**

**READ<Sentence> → EXTRACT<Verb> → FIND<PrimitiveAct> → EXTRACT<CaseRoles> → CREATE<CDStructure> → LINK<Inferences> → ITERATE<Cycle>**

1. **READ<Sentence>** – READ <natural_language_sentence> FROM <input_text>.
2. **EXTRACT<Verb>** – EXTRACT <main_verb> FROM <sentence> USING <syntactic_parse>.
3. **FIND<PrimitiveAct>** – FIND <primitive_act> IN <ATRANS, PTRANS, MTRANS, MBUILD, PROPEL, MOVE, GRASP, INGEST, EXPEL, SPEAK, ATTEND>.
4. **EXTRACT<CaseRoles>** – EXTRACT <actor, object, from, to, instrument> FROM <sentence_constituents>.
5. **CREATE<CDStructure>** – CREATE <conceptual_dependency_diagram> FROM <primitive_act> WITH <role_bindings>.
6. **LINK<Inferences>** – LINK <CD_structure> TO <inference_rules> FOR <causal_chains>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> ON <next_sentence> LINKING <to_discourse_context>.

---

[Semantic-Network-Spreading-Activation] **Class: Cognitive Loop**

**READ<Query> → SET<ActivationSources> → EXECUTE<SpreadingActivation> → FIND<Intersection> → EXTRACT<Path> → CREATE<Response> → ITERATE<Cycle>**

1. **READ<Query>** – READ <concept_pair> FROM <query> AS <(concept_A, concept_B)>.
2. **SET<ActivationSources>** – SET <activation_sources> TO <concept_A, concept_B> WITH <initial_activation>.
3. **EXECUTE<SpreadingActivation>** – EXECUTE <activation_spread> FROM <sources> TO <connected_nodes> WITH <decay_factor>.
4. **FIND<Intersection>** – FIND <intersection_node> WHERE <activation_from_A> AND <activation_from_B> MEET.
5. **EXTRACT<Path>** – EXTRACT <path> FROM <concept_A> TO <intersection> TO <concept_B> VIA <edge_labels>.
6. **CREATE<Response>** – CREATE <semantic_relationship> FROM <path_labels> AS <natural_language_response>.
7. **ITERATE<Cycle>** – ITERATE <spreading> UNTIL <intersection_found> OR <activation_exhausted>.

---

[Resolution-Unification-Loop] **Class: Hypothesis-Validation Loop**

**READ<Clauses> → FIND<ResolvablePair> → EXECUTE<Unification> → CREATE<Resolvent> → ANALYZE<EmptyClause> → ITERATE<Cycle>**

1. **READ<Clauses>** – READ <clause_set> FROM <negated_goal> AND <knowledge_base> IN <CNF>.
2. **FIND<ResolvablePair>** – FIND <clause_pair> WHERE <literal_L> IN <C1> AND <complement(L)> IN <C2>.
3. **EXECUTE<Unification>** – EXECUTE <unification_algorithm> ON <L> AND <complement(L)> TO FIND <MGU>.
4. **CREATE<Resolvent>** – CREATE <resolvent> AS <(C1 - L) ∪ (C2 - complement(L))> APPLYING <MGU>.
5. **ANALYZE<EmptyClause>** – ANALYZE <resolvent> FOR <empty_clause> IF <empty> THEN <proof_found>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <clause_set ∪ resolvent> UNTIL <empty_clause> OR <exhausted>.

---

[Arc-Consistency-Propagation-Loop] **Class: Perceptual Loop**

**READ<CSP> → CREATE<Queue> → EXTRACT<Arc> → EXECUTE<Revise> → ANALYZE<Change> → ITERATE<Cycle>**

1. **READ<CSP>** – READ <variables, domains, constraints> FROM <constraint_satisfaction_problem>.
2. **CREATE<Queue>** – CREATE <arc_queue> FROM <all_constraint_arcs> IN <CSP>.
3. **EXTRACT<Arc>** – EXTRACT <arc (Xi, Xj)> FROM <queue>.
4. **EXECUTE<Revise>** – EXECUTE <revise> ON <domain(Xi)> REMOVE <values> WITHOUT <support> IN <domain(Xj)>.
5. **ANALYZE<Change>** – ANALYZE <domain(Xi)_changed> IF <changed> ADD <arcs (Xk, Xi)> TO <queue>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> UNTIL <queue_empty> OR <domain_empty>.

---

[Dependency-Directed-Backtracking-Loop] **Class: Hypothesis-Validation Loop**

**READ<Problem> → CREATE<Dependencies> → EXECUTE<ForwardReasoning> → FIND<Contradiction> → ANALYZE<Dependencies> → EXECUTE<TargetedBacktrack> → ITERATE<Cycle>**

1. **READ<Problem>** – READ <problem_specification> WITH <initial_assumptions> FROM <input>.
2. **CREATE<Dependencies>** – CREATE <dependency_network> TO <record_justifications> FOR <each_derived_fact>.
3. **EXECUTE<ForwardReasoning>** – EXECUTE <inference_rules> ON <current_beliefs> RECORDING <dependencies>.
4. **FIND<Contradiction>** – FIND <contradiction> IN <belief_set> WHEN <mutually_exclusive_facts> DERIVED.
5. **ANALYZE<Dependencies>** – ANALYZE <dependency_chains> FROM <contradiction> TO FIND <culprit_assumptions>.
6. **EXECUTE<TargetedBacktrack>** – EXECUTE <backtrack> TO <most_recent_culprit> RETRACTING <dependent_beliefs>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> WITH <new_assumption> AVOIDING <nogood_combinations>.

---

[Backward-Chaining-Inference-Loop] **Class: Decision Loop**

**READ<Goal> → FIND<Rules> → ANALYZE<Premises> → EXECUTE<Subgoaling> → CREATE<Conclusion> → SET<CertaintyFactor> → ITERATE<Cycle>**

1. **READ<Goal>** – READ <goal_parameter> FROM <consultation_context> AS <value_to_determine>.
2. **FIND<Rules>** – FIND <rules> IN <knowledge_base> WHERE <conclusion> ESTABLISHES <goal_parameter>.
3. **ANALYZE<Premises>** – ANALYZE <rule_premises> TO DETERMINE <known_values> AND <unknown_values>.
4. **EXECUTE<Subgoaling>** – EXECUTE <recursive_backward_chain> FOR <unknown_premise> OR <ask_user>.
5. **CREATE<Conclusion>** – CREATE <conclusion_value> FROM <rule_consequent> WHEN <premises_satisfied>.
6. **SET<CertaintyFactor>** – SET <conclusion_CF> TO <combine(premise_CFs, rule_CF)>.
7. **ITERATE<Cycle>** – ITERATE <algorithm> FOR <each_applicable_rule> COMBINING <conclusions>.

---

[Marr-Three-Level-Analysis] **Class: Meta Loop**

**ANALYZE<Task> → CREATE<ComputationalTheory> → CREATE<Algorithm> → CREATE<Implementation> → ANALYZE<Correspondence> → ITERATE<Cycle>**

1. **ANALYZE<Task>** – ANALYZE <information_processing_task> FOR <goal_specification> AND <constraints>.
2. **CREATE<ComputationalTheory>** – CREATE <computational_theory> FOR <what_is_computed> AND <why_appropriate>.
3. **CREATE<Algorithm>** – CREATE <algorithm_specification> FOR <input_representation> AND <transformation_procedure>.
4. **CREATE<Implementation>** – CREATE <physical_realization> FOR <how_representations_instantiated>.
5. **ANALYZE<Correspondence>** – ANALYZE <correspondence> BETWEEN <levels> FOR <explanatory_completeness>.
6. **ITERATE<Cycle>** – ITERATE <analysis> ON <each_subtask> ENSURING <all_levels_addressed>.

---

[SSA-Single-Assignment-Transform] **Class: Construction Loop**

**READ<Program> → FIND<Definitions> → CREATE<VersionedNames> → INSERT<PhiNodes> → WRITE<SSAForm> → ITERATE<Cycle>**

1. **READ<Program>** – READ <program_CFG> FROM <source_representation>.
2. **FIND<Definitions>** – FIND <all_variable_definitions> IN <program> WITH <reaching_definitions>.
3. **CREATE<VersionedNames>** – CREATE <new_versioned_name> FOR <each_definition> AS <variable_n>.
4. **INSERT<PhiNodes>** – INSERT <φ_functions> AT <control_flow_join_points> FOR <multiple_definitions>.
5. **WRITE<SSAForm>** – WRITE <transformed_program> WITH <single_assignment_property>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <nested_regions> MAINTAINING <dominance_properties>.

---

[Unification-Structure-Sharing] **Class: Construction Loop**

**READ<Structures> → FIND<SharedComponents> → ANALYZE<Compatibility> → EXECUTE<Unification> → CREATE<SharedResult> → ITERATE<Cycle>**

1. **READ<Structures>** – READ <feature_structure_A> AND <feature_structure_B> FROM <input>.
2. **FIND<SharedComponents>** – FIND <identical_subtrees> BETWEEN <A> AND <B> FOR <potential_sharing>.
3. **ANALYZE<Compatibility>** – ANALYZE <feature_values> FOR <unifiability> CHECKING <occurs_check>.
4. **EXECUTE<Unification>** – EXECUTE <unification> PRESERVING <shared_subtrees> CREATING <new_nodes_for_differences>.
5. **CREATE<SharedResult>** – CREATE <result_structure> POINTING <to_shared_nodes> FROM <both_inputs>.
6. **ITERATE<Cycle>** – ITERATE <algorithm> ON <recursive_features> MAINTAINING <sharing_invariant>.

---
