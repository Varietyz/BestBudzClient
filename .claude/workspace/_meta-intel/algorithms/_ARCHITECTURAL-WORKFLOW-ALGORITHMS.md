# Architectural Best Practise Algorithms

## 1. SEPARATION OF CONCERNS (SOC)

[Single-Concern-Enforcement]

**ANALYZE<Module> → FIND<Responsibilities> → FILTER<SingleConcern> → EXTRACT<Violations> → EXECUTE<Refactor>**

1. **ANALYZE<Module>** – ANALYZE <target_module> FOR <responsibility_count> USING <change_reason_analysis>.
2. **FIND<Responsibilities>** – FIND <distinct_concerns> IN <module_behavior> BASED_ON <change_triggers>.
3. **FILTER<SingleConcern>** – FILTER <responsibilities> TO <single_concern> USING <cohesion_metrics>.
4. **EXTRACT<Violations>** – EXTRACT <multiple_concerns> FROM <analysis_results> INTO <violation_list>.
5. **EXECUTE<Refactor>** – EXECUTE <module_split> ON <violations> USING <concern_separation_tool>.

---

## 2. KEEP IT SIMPLE, STUPID (KISS)

[Simplest-Solution-Selection]

**CREATE<Solutions> → ANALYZE<Complexity> → RANK<Simplicity> → FILTER<Working> → SET<Implementation>**

1. **CREATE<Solutions>** – CREATE <candidate_implementations> FROM <requirements> USING <solution_generation>.
2. **ANALYZE<Complexity>** – ANALYZE <each_candidate> FOR <complexity_metrics> AGAINST <cognitive_load_threshold>.
3. **RANK<Simplicity>** – RANK <candidates> BASED_ON <lines_of_code, dependencies, abstractions>.
4. **FILTER<Working>** – FILTER <ranked_solutions> TO <functional_subset> USING <requirement_validation>.
5. **SET<Implementation>** – SET <chosen_solution> TO <simplest_working_candidate>.

---

## 3. DON'T REPEAT YOURSELF (DRY)

[Duplication-Elimination]

**FIND<Duplicates> → ANALYZE<Similarity> → EXTRACT<CommonLogic> → CREATE<Abstraction> → LINK<References>**

1. **FIND<Duplicates>** – FIND <similar_code_blocks> IN <codebase> USING <pattern_matching_tool>.
2. **ANALYZE<Similarity>** – ANALYZE <candidates> FOR <semantic_equivalence> AGAINST <threshold>.
3. **EXTRACT<CommonLogic>** – EXTRACT <shared_behavior> FROM <duplicate_instances> INTO <abstract_form>.
4. **CREATE<Abstraction>** – CREATE <reusable_function> FROM <extracted_logic> USING <parameterization>.
5. **LINK<References>** – LINK <original_locations> TO <new_abstraction> USING <call_replacement>.

---

## 4. SINGLE RESPONSIBILITY PRINCIPLE (SRP)

[Change-Reason-Restriction]

**READ<Module> → FIND<ChangeTriggers> → ANALYZE<Reasons> → FILTER<MultipleReasons> → EXECUTE<Decomposition>**

1. **READ<Module>** – READ <module_definition> FROM <source_file> USING <ast_parser>.
2. **FIND<ChangeTriggers>** – FIND <potential_modifications> IN <module_behavior> BASED_ON <stakeholder_analysis>.
3. **ANALYZE<Reasons>** – ANALYZE <change_triggers> FOR <distinct_reasons> USING <actor_identification>.
4. **FILTER<MultipleReasons>** – FILTER <modules> TO <multi_reason_subset> BASED_ON <reason_count>.
5. **EXECUTE<Decomposition>** – EXECUTE <module_split> ON <violations> USING <responsibility_extraction>.

---

## 5. OPEN/CLOSED PRINCIPLE (OCP)

[Extension-Without-Modification]

**ANALYZE<Requirements> → FIND<VariationPoints> → CREATE<Abstractions> → SET<ExtensionMechanism> → EXECUTE<Implementation>**

1. **ANALYZE<Requirements>** – ANALYZE <system_requirements> FOR <anticipated_changes> USING <variation_analysis>.
2. **FIND<VariationPoints>** – FIND <behavior_variation_locations> IN <design> BASED_ON <change_frequency>.
3. **CREATE<Abstractions>** – CREATE <interface_definitions> FROM <variation_points> USING <polymorphism_patterns>.
4. **SET<ExtensionMechanism>** – SET <extension_strategy> TO <inheritance, composition, plugin_architecture>.
5. **EXECUTE<Implementation>** – EXECUTE <abstraction_injection> ON <concrete_dependencies> USING <refactoring_tool>.

---

## 6. LISKOV SUBSTITUTION PRINCIPLE (LSP)

[Substitutability-Preservation]

**READ<Hierarchy> → ANALYZE<Contracts> → FIND<Violations> → EXTRACT<BreakingChanges> → EXECUTE<Correction>**

1. **READ<Hierarchy>** – READ <inheritance_structure> FROM <class_definitions> USING <type_analyzer>.
2. **ANALYZE<Contracts>** – ANALYZE <parent_contract> AGAINST <child_implementations> FOR <behavioral_consistency>.
3. **FIND<Violations>** – FIND <contract_breaches> IN <child_methods> BASED_ON <precondition_weakening, postcondition_strengthening>.
4. **EXTRACT<BreakingChanges>** – EXTRACT <incompatible_overrides> FROM <analysis_results> INTO <violation_report>.
5. **EXECUTE<Correction>** – EXECUTE <contract_realignment> ON <violations> USING <signature_adjustment, composition_replacement>.

---

## 7. INTERFACE SEGREGATION PRINCIPLE (ISP)

[Interface-Splitting]

**ANALYZE<Clients> → FIND<UsagePatterns> → FILTER<UnusedMethods> → CREATE<SegregatedInterfaces> → LINK<Clients>**

1. **ANALYZE<Clients>** – ANALYZE <interface_consumers> FOR <method_usage> USING <call_graph_analysis>.
2. **FIND<UsagePatterns>** – FIND <distinct_usage_clusters> IN <client_behavior> BASED_ON <method_grouping>.
3. **FILTER<UnusedMethods>** – FILTER <interface_methods> TO <used_subset> FOR <each_client>.
4. **CREATE<SegregatedInterfaces>** – CREATE <focused_interfaces> FROM <usage_clusters> USING <interface_extraction>.
5. **LINK<Clients>** – LINK <each_client> TO <minimal_interface> USING <dependency_rewiring>.

---

## 8. DEPENDENCY INVERSION PRINCIPLE (DIP)

[Abstraction-Injection]

**FIND<ConcreteDependencies> → ANALYZE<Stability> → CREATE<Abstractions> → EXECUTE<Inversion> → SET<InjectionMechanism>**

1. **FIND<ConcreteDependencies>** – FIND <direct_class_references> IN <module_imports> USING <dependency_scanner>.
2. **ANALYZE<Stability>** – ANALYZE <dependencies> FOR <volatility> AGAINST <stability_metrics>.
3. **CREATE<Abstractions>** – CREATE <interface_definitions> FROM <concrete_types> USING <contract_extraction>.
4. **EXECUTE<Inversion>** – EXECUTE <dependency_replacement> ON <concrete_references> USING <abstraction_substitution>.
5. **SET<InjectionMechanism>** – SET <injection_strategy> TO <constructor_injection, dependency_injection_container>.

---

## 9. DECOUPLED CHILD/PARENT (DCB/CF)

[Callback-Elimination]

**FIND<ParentCalls> → ANALYZE<DirectionViolations> → CREATE<EventSystem> → EXECUTE<InversionOfControl> → LINK<EventHandlers>**

1. **FIND<ParentCalls>** – FIND <upward_method_invocations> IN <child_classes> USING <call_direction_analysis>.
2. **ANALYZE<DirectionViolations>** – ANALYZE <found_calls> FOR <dependency_inversion_violations> AGAINST <hierarchy_rules>.
3. **CREATE<EventSystem>** – CREATE <event_emitter_mechanism> FROM <callback_patterns> USING <observer_pattern>.
4. **EXECUTE<InversionOfControl>** – EXECUTE <callback_removal> ON <parent_references> USING <event_emission_replacement>.
5. **LINK<EventHandlers>** – LINK <parent_behavior> TO <child_events> USING <subscription_mechanism>.

---

## 10. GENERALIZED

[Generalization-Validation]

**ANALYZE<UseCases> → FIND<CommonPatterns> → EXTRACT<InvariantBehavior> → CREATE<GeneralizedSolution> → EXECUTE<Validation>**

1. **ANALYZE<UseCases>** – ANALYZE <problem_instances> FOR <applicability_scope> USING <case_comparison>.
2. **FIND<CommonPatterns>** – FIND <shared_characteristics> BETWEEN <use_cases> BASED_ON <structural_similarity>.
3. **EXTRACT<InvariantBehavior>** – EXTRACT <consistent_operations> FROM <patterns> INTO <abstract_algorithm>.
4. **CREATE<GeneralizedSolution>** – CREATE <parameterized_implementation> FROM <invariants> USING <type_abstraction>.
5. **EXECUTE<Validation>** – EXECUTE <applicability_test> ON <all_cases> AGAINST <generalized_solution>.

---

## 11. PARAMETERIZED

[Behavior-Parameterization]

**FIND<HardcodedValues> → ANALYZE<VariationPoints> → EXTRACT<Parameters> → CREATE<ParameterInterface> → SET<DefaultBehavior>**

1. **FIND<HardcodedValues>** – FIND <literal_constants, magic_numbers> IN <implementation> USING <static_analysis>.
2. **ANALYZE<VariationPoints>** – ANALYZE <values> FOR <behavioral_impact> AGAINST <execution_paths>.
3. **EXTRACT<Parameters>** – EXTRACT <configurable_values> FROM <hardcoded_literals> INTO <parameter_list>.
4. **CREATE<ParameterInterface>** – CREATE <function_signature, constructor_args> FROM <parameters> USING <interface_definition>.
5. **SET<DefaultBehavior>** – SET <default_values> TO <original_constants> FOR <backward_compatibility>.

---

## 12. CENTRALIZED

[Truth-Centralization]

**FIND<DataDuplication> → ANALYZE<AuthoritativeSource> → CREATE<SingleSource> → LINK<References> → EXECUTE<Migration>**

1. **FIND<DataDuplication>** – FIND <redundant_data_stores> IN <system> USING <data_flow_analysis>.
2. **ANALYZE<AuthoritativeSource>** – ANALYZE <data_sources> FOR <authority_determination> BASED_ON <update_frequency, reliability>.
3. **CREATE<SingleSource>** – CREATE <canonical_data_store> FROM <authoritative_source> USING <schema_consolidation>.
4. **LINK<References>** – LINK <data_consumers> TO <single_source> USING <reference_replacement>.
5. **EXECUTE<Migration>** – EXECUTE <data_consolidation> ON <duplicate_sources> USING <migration_script>.

---

## 13. SCALABLE

[Growth-Accommodation]

**ANALYZE<LoadPattern> → FIND<Bottlenecks> → CREATE<ScalableArchitecture> → SET<GrowthStrategy> → EXECUTE<Implementation>**

1. **ANALYZE<LoadPattern>** – ANALYZE <system_usage> FOR <growth_trajectory> USING <metrics_analysis>.
2. **FIND<Bottlenecks>** – FIND <capacity_constraints> IN <architecture> BASED_ON <load_testing>.
3. **CREATE<ScalableArchitecture>** – CREATE <horizontal_scaling_design> FROM <bottleneck_analysis> USING <distribution_patterns>.
4. **SET<GrowthStrategy>** – SET <scaling_approach> TO <stateless_services, data_partitioning, caching_layers>.
5. **EXECUTE<Implementation>** – EXECUTE <architectural_refactor> ON <bottlenecks> USING <scaling_patterns>.

---

## 14. COMPOSABLE

[Module-Composition]

**ANALYZE<System> → EXTRACT<IndependentUnits> → CREATE<CompositionInterfaces> → LINK<Components> → EXECUTE<Assembly>**

1. **ANALYZE<System>** – ANALYZE <requirements> FOR <decomposition_boundaries> USING <domain_analysis>.
2. **EXTRACT<IndependentUnits>** – EXTRACT <self_contained_modules> FROM <system_design> INTO <component_catalog>.
3. **CREATE<CompositionInterfaces>** – CREATE <connection_contracts> FROM <module_boundaries> USING <interface_design>.
4. **LINK<Components>** – LINK <modules> TO <each_other> USING <composition_patterns>.
5. **EXECUTE<Assembly>** – EXECUTE <system_construction> FROM <composed_modules> USING <dependency_injection>.

---

## 15. EXTENDABLE

[External-Feature-Addition]

[Extension-Point-Design]

**FIND<FutureNeeds> → ANALYZE<StableCore> → CREATE<ExtensionPoints> → SET<PluginMechanism> → EXECUTE<CoreProtection>**

1. **FIND<FutureNeeds>** – FIND <anticipated_features> IN <requirements> BASED_ON <stakeholder_input>.
2. **ANALYZE<StableCore>** – ANALYZE <existing_system> FOR <invariant_behavior> AGAINST <change_frequency>.
3. **CREATE<ExtensionPoints>** – CREATE <plugin_interfaces> FROM <variation_points> USING <strategy_pattern>.
4. **SET<PluginMechanism>** – SET <extension_strategy> TO <hooks, events, dependency_injection>.
5. **EXECUTE<CoreProtection>** – EXECUTE <interface_isolation> ON <stable_core> USING <abstraction_barriers>.

---

## 16. DYNAMICAL

[Runtime-Behavior-Modification]

**ANALYZE<RuntimeContext> → FIND<BehaviorVariants> → CREATE<StrategySelection> → SET<RuntimeParameters> → EXECUTE<DynamicDispatch>**

1. **ANALYZE<RuntimeContext>** – ANALYZE <execution_environment> FOR <contextual_factors> USING <state_inspection>.
2. **FIND<BehaviorVariants>** – FIND <alternative_implementations> IN <strategy_catalog> BASED_ON <context_matching>.
3. **CREATE<StrategySelection>** – CREATE <decision_logic> FROM <context_conditions> USING <conditional_dispatch>.
4. **SET<RuntimeParameters>** – SET <behavior_configuration> TO <context_specific_values> USING <parameter_binding>.
5. **EXECUTE<DynamicDispatch>** – EXECUTE <selected_strategy> ON <input_data> USING <polymorphic_invocation>.

---

## 17. MODULAR

[Module-Independence]

**ANALYZE<Dependencies> → FIND<Coupling> → EXTRACT<Interfaces> → CREATE<IndependentModules> → EXECUTE<IsolationTest>**

1. **ANALYZE<Dependencies>** – ANALYZE <module_relationships> FOR <coupling_degree> USING <dependency_graph>.
2. **FIND<Coupling>** – FIND <tight_dependencies> IN <module_network> BASED_ON <coupling_metrics>.
3. **EXTRACT<Interfaces>** – EXTRACT <contract_definitions> FROM <module_boundaries> INTO <interface_specifications>.
4. **CREATE<IndependentModules>** – CREATE <isolated_units> FROM <coupled_modules> USING <dependency_breaking>.
5. **EXECUTE<IsolationTest>** – EXECUTE <module_swap> ON <system> USING <substitution_testing>.

---

## 18. COMPONENT-BASED

[Component-Reuse]

**READ<Requirements> → FIND<ExistingComponents> → ANALYZE<Compatibility> → LINK<Components> → CREATE<System>**

1. **READ<Requirements>** – READ <system_specification> FROM <requirements_document> USING <requirement_parser>.
2. **FIND<ExistingComponents>** – FIND <reusable_modules> IN <component_library> BASED_ON <capability_matching>.
3. **ANALYZE<Compatibility>** – ANALYZE <components> FOR <interface_compatibility> AGAINST <integration_requirements>.
4. **LINK<Components>** – LINK <selected_components> TO <system_architecture> USING <composition_patterns>.
5. **CREATE<System>** – CREATE <integrated_application> FROM <linked_components> USING <assembly_process>.

---

## 19. HEREDITARY

[Trait-Propagation]

**ANALYZE<ParentTraits> → FIND<InheritableProperties> → CREATE<ChildStructure> → LINK<Inheritance> → EXECUTE<TraitVerification>**

1. **ANALYZE<ParentTraits>** – ANALYZE <parent_module> FOR <inheritable_characteristics> USING <trait_inspection>.
2. **FIND<InheritableProperties>** – FIND <structural_patterns, behavioral_contracts> IN <parent> BASED_ON <inheritance_rules>.
3. **CREATE<ChildStructure>** – CREATE <derived_module> FROM <parent_template> USING <inheritance_mechanism>.
4. **LINK<Inheritance>** – LINK <child> TO <parent> USING <extends_relationship>.
5. **EXECUTE<TraitVerification>** – EXECUTE <trait_validation> ON <child> AGAINST <parent_contract>.

---

## 20. HIERARCHICAL

[Hierarchical-Organization]

**ANALYZE<System> → FIND<AbstractionLevels> → CREATE<LayerStructure> → SET<DependencyDirection> → EXECUTE<LayerEnforcement>**

1. **ANALYZE<System>** – ANALYZE <components> FOR <abstraction_levels> USING <responsibility_analysis>.
2. **FIND<AbstractionLevels>** – FIND <distinct_layers> IN <system> BASED_ON <dependency_distance, detail_level>.
3. **CREATE<LayerStructure>** – CREATE <hierarchical_organization> FROM <layers> USING <tree_or_stack_pattern>.
4. **SET<DependencyDirection>** – SET <allowed_dependencies> TO <downward_only> USING <dependency_rules>.
5. **EXECUTE<LayerEnforcement>** – EXECUTE <dependency_validation> ON <architecture> USING <linting_tools>.

---

## 21. YAGNI (YOU AREN'T GONNA NEED IT)

[Unnecessary-Feature-Prevention]

**ANALYZE<Proposal> → FIND<CurrentNeeds> → FILTER<UnnecessaryFeatures> → EXTRACT<MinimalRequirements> → SET<Implementation>**

1. **ANALYZE<Proposal>** – ANALYZE <feature_request> FOR <necessity> AGAINST <current_requirements>.
2. **FIND<CurrentNeeds>** – FIND <immediate_use_cases> IN <requirements> BASED_ON <stakeholder_priorities>.
3. **FILTER<UnnecessaryFeatures>** – FILTER <proposed_features> TO <essential_subset> USING <necessity_criteria>.
4. **EXTRACT<MinimalRequirements>** – EXTRACT <must_have_features> FROM <filtered_set> INTO <implementation_scope>.
5. **SET<Implementation>** – SET <development_plan> TO <minimal_requirements> WITHOUT <speculative_features>.

---

## 22. FAIL FAST

[Immediate-Error-Detection]

**ANALYZE<InputPoints> → FIND<FailureConditions> → CREATE<ValidationChecks> → SET<CheckLocation> → EXECUTE<EarlyValidation>**

1. **ANALYZE<InputPoints>** – ANALYZE <system_entry_points> FOR <validation_requirements> USING <contract_analysis>.
2. **FIND<FailureConditions>** – FIND <invalid_states> IN <input_space> BASED_ON <constraint_violations>.
3. **CREATE<ValidationChecks>** – CREATE <assertion_logic> FROM <failure_conditions> USING <guard_clauses>.
4. **SET<CheckLocation>** – SET <validation_position> TO <earliest_detection_point> IN <execution_flow>.
5. **EXECUTE<EarlyValidation>** – EXECUTE <checks> ON <inputs> USING <assertion_mechanism>.

---

## 23. LOOSE COUPLING

[Dependency-Minimization]

**ANALYZE<Modules> → FIND<Dependencies> → FILTER<EssentialCoupling> → CREATE<Interfaces> → EXECUTE<Decoupling>**

1. **ANALYZE<Modules>** – ANALYZE <system_components> FOR <inter_module_dependencies> USING <dependency_analysis>.
2. **FIND<Dependencies>** – FIND <coupling_points> BETWEEN <modules> BASED_ON <reference_count, method_calls>.
3. **FILTER<EssentialCoupling>** – FILTER <dependencies> TO <necessary_subset> USING <coupling_justification>.
4. **CREATE<Interfaces>** – CREATE <abstraction_layer> FROM <essential_coupling> USING <interface_extraction>.
5. **EXECUTE<Decoupling>** – EXECUTE <concrete_dependency_removal> ON <modules> USING <abstraction_injection>.

---

## 24. HIGH COHESION

[Logic-Concentration]

**ANALYZE<Module> → FIND<RelatedFunctionality> → FILTER<UnrelatedCode> → EXTRACT<CohesiveUnit> → EXECUTE<Relocation>**

1. **ANALYZE<Module>** – ANALYZE <module_contents> FOR <functional_relationships> USING <cohesion_metrics>.
2. **FIND<RelatedFunctionality>** – FIND <logically_connected_operations> IN <module> BASED_ON <data_flow, shared_purpose>.
3. **FILTER<UnrelatedCode>** – FILTER <module_functions> TO <cohesive_subset> USING <relationship_strength>.
4. **EXTRACT<CohesiveUnit>** – EXTRACT <tightly_related_code> FROM <module> INTO <focused_module>.
5. **EXECUTE<Relocation>** – EXECUTE <code_movement> ON <unrelated_functions> USING <refactoring_tool>.

---

## 25. ENCAPSULATION

[Implementation-Hiding]

**ANALYZE<Module> → FIND<PublicInterface> → FILTER<InternalDetails> → CREATE<AccessBoundary> → EXECUTE<VisibilityRestriction>**

1. **ANALYZE<Module>** – ANALYZE <module_structure> FOR <exposed_elements> USING <visibility_analysis>.
2. **FIND<PublicInterface>** – FIND <essential_operations> IN <module> BASED_ON <client_requirements>.
3. **FILTER<InternalDetails>** – FILTER <exposed_elements> TO <public_interface> USING <access_necessity>.
4. **CREATE<AccessBoundary>** – CREATE <visibility_rules> FROM <interface_definition> USING <access_modifiers>.
5. **EXECUTE<VisibilityRestriction>** – EXECUTE <access_control> ON <internal_elements> USING <private_marking>.

---

## 26. IMMUTABILITY

[State-Freezing]

**ANALYZE<DataStructures> → FIND<MutableState> → CREATE<ImmutableAlternatives> → EXECUTE<StateProtection> → SET<CreationPolicy>**

1. **ANALYZE<DataStructures>** – ANALYZE <data_objects> FOR <mutation_operations> USING <mutability_scanner>.
2. **FIND<MutableState>** – FIND <modifiable_fields> IN <structures> BASED_ON <setter_presence, field_access>.
3. **CREATE<ImmutableAlternatives>** – CREATE <frozen_structures> FROM <mutable_objects> USING <immutable_patterns>.
4. **EXECUTE<StateProtection>** – EXECUTE <field_finalization> ON <data_structures> USING <const_marking, readonly_fields>.
5. **SET<CreationPolicy>** – SET <initialization_strategy> TO <constructor_only_assignment>.

---

## 27. IDEMPOTENCY

[Repeatable-Operations]

**ANALYZE<Operations> → FIND<StateChanges> → CREATE<IdempotentLogic> → EXECUTE<DuplicateDetection> → SET<GuaranteeCondition>**

1. **ANALYZE<Operations>** – ANALYZE <function_behavior> FOR <state_modification_patterns> USING <effect_analysis>.
2. **FIND<StateChanges>** – FIND <cumulative_effects> IN <operations> BASED_ON <repeated_execution>.
3. **CREATE<IdempotentLogic>** – CREATE <stable_implementation> FROM <operations> USING <conditional_execution, set_semantics>.
4. **EXECUTE<DuplicateDetection>** – EXECUTE <completion_check> ON <operation> USING <state_inspection>.
5. **SET<GuaranteeCondition>** – SET <invariant> TO <same_result_on_repeat>.

---

## 28. ORTHOGONALITY

[Concern-Isolation]

**ANALYZE<Changes> → FIND<ImpactRadius> → FILTER<UnrelatedAffects> → CREATE<IsolationBoundaries> → EXECUTE<ImpactMinimization>**

1. **ANALYZE<Changes>** – ANALYZE <proposed_modifications> FOR <affected_components> USING <dependency_tracing>.
2. **FIND<ImpactRadius>** – FIND <change_propagation_scope> IN <system> BASED_ON <coupling_graph>.
3. **FILTER<UnrelatedAffects>** – FILTER <impacts> TO <logically_related_only> USING <concern_mapping>.
4. **CREATE<IsolationBoundaries>** – CREATE <separation_interfaces> FROM <unrelated_concerns> USING <abstraction_layers>.
5. **EXECUTE<ImpactMinimization>** – EXECUTE <dependency_reduction> ON <boundaries> USING <decoupling_techniques>.

---

## 29. LAYERED

[Horizontal-Organization]

**ANALYZE<Responsibilities> → FIND<AbstractionLayers> → CREATE<LayerDefinitions> → SET<CommunicationRules> → EXECUTE<LayerIsolation>**

1. **ANALYZE<Responsibilities>** – ANALYZE <system_functions> FOR <abstraction_levels> USING <responsibility_classification>.
2. **FIND<AbstractionLayers>** – FIND <distinct_levels> IN <architecture> BASED_ON <presentation, logic, data>.
3. **CREATE<LayerDefinitions>** – CREATE <layer_boundaries> FROM <abstraction_levels> USING <horizontal_separation>.
4. **SET<CommunicationRules>** – SET <interaction_policy> TO <adjacent_layers_only> USING <dependency_constraints>.
5. **EXECUTE<LayerIsolation>** – EXECUTE <cross_layer_prevention> ON <architecture> USING <architectural_tests>.

---

## 30. EVENT-DRIVEN

[Event-Communication]

**ANALYZE<Interactions> → FIND<TriggerPoints> → CREATE<EventDefinitions> → SET<PublishSubscribe> → EXECUTE<DecoupledCommunication>**

1. **ANALYZE<Interactions>** – ANALYZE <component_communication> FOR <coupling_patterns> USING <call_graph_analysis>.
2. **FIND<TriggerPoints>** – FIND <state_change_moments> IN <system> BASED_ON <notification_requirements>.
3. **CREATE<EventDefinitions>** – CREATE <event_types> FROM <trigger_points> USING <event_taxonomy>.
4. **SET<PublishSubscribe>** – SET <communication_pattern> TO <emit_listen_mechanism> USING <event_bus>.
5. **EXECUTE<DecoupledCommunication>** – EXECUTE <direct_call_replacement> ON <interactions> USING <event_emission>.

---

## 31. DECLARATIVE

[Intent-Specification]

**ANALYZE<Requirements> → FIND<DesiredOutcomes> → CREATE<DeclarativeSpec> → SET<ExecutionEngine> → EXECUTE<InterpretationLayer>**

1. **ANALYZE<Requirements>** – ANALYZE <task_specifications> FOR <intended_results> USING <goal_extraction>.
2. **FIND<DesiredOutcomes>** – FIND <what_not_how> IN <requirements> BASED_ON <state_descriptions>.
3. **CREATE<DeclarativeSpec>** – CREATE <outcome_declaration> FROM <intentions> USING <constraint_language>.
4. **SET<ExecutionEngine>** – SET <implementation_mechanism> TO <interpreter, compiler, rule_engine>.
5. **EXECUTE<InterpretationLayer>** – EXECUTE <how_determination> ON <declarations> USING <execution_strategy>.

---

## 32. STATELESS

[State-Elimination]

**ANALYZE<Functions> → FIND<PersistentState> → EXTRACT<Parameters> → CREATE<PureFunctions> → EXECUTE<StateExternalization>**

1. **ANALYZE<Functions>** – ANALYZE <operations> FOR <state_dependencies> USING <side_effect_analysis>.
2. **FIND<PersistentState>** – FIND <stored_values> IN <function_closures, class_fields> BASED_ON <lifetime_analysis>.
3. **EXTRACT<Parameters>** – EXTRACT <state_requirements> FROM <operations> INTO <function_arguments>.
4. **CREATE<PureFunctions>** – CREATE <stateless_implementations> FROM <stateful_functions> USING <parameterization>.
5. **EXECUTE<StateExternalization>** – EXECUTE <state_storage_removal> ON <functions> USING <parameter_passing>.

---

## 33. BOUNDED COMPLEXITY

[Complexity-Limitation]

**ANALYZE<Files> → FIND<ComplexityViolations> → EXTRACT<ExcessContent> → CREATE<SplitModules> → EXECUTE<Redistribution>**

1. **ANALYZE<Files>** – ANALYZE <source_files> FOR <line_count, folder_file_count> USING <metrics_tool>.
2. **FIND<ComplexityViolations>** – FIND <oversized_units> IN <codebase> BASED_ON <150_line_threshold, 4_file_limit>.
3. **EXTRACT<ExcessContent>** – EXTRACT <separable_concerns> FROM <large_files> INTO <logical_groupings>.
4. **CREATE<SplitModules>** – CREATE <smaller_files> FROM <extracted_content> USING <concern_separation>.
5. **EXECUTE<Redistribution>** – EXECUTE <file_reorganization> ON <codebase> USING <refactoring_tool>.

---

## CROSS-REFERENCE TABLE

| Practice           | Primary Loop | Key Verbs                      | Validation Strategy           |
| ------------------ | ------------ | ------------------------------ | ----------------------------- |
| SOC                | Meta Loop    | ANALYZE→FILTER→EXTRACT→EXECUTE | Change reason count           |
| KISS               | Meta Loop    | CREATE→RANK→FILTER→SET         | Complexity metrics            |
| DRY                | Meta Loop    | FIND→ANALYZE→EXTRACT→CREATE    | Duplication detection         |
| SRP                | Meta Loop    | READ→FIND→ANALYZE→FILTER       | Actor analysis                |
| OCP                | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Extension test                |
| LSP                | Meta Loop    | READ→ANALYZE→FIND→EXTRACT      | Contract validation           |
| ISP                | Meta Loop    | ANALYZE→FIND→FILTER→CREATE     | Usage pattern analysis        |
| DIP                | Meta Loop    | FIND→ANALYZE→CREATE→EXECUTE    | Dependency direction check    |
| DCB/CF             | Meta Loop    | FIND→ANALYZE→CREATE→EXECUTE    | Call direction validation     |
| Generalized        | Meta Loop    | ANALYZE→FIND→EXTRACT→CREATE    | Cross-case applicability      |
| Parameterized      | Meta Loop    | FIND→ANALYZE→EXTRACT→CREATE    | Hardcoded value detection     |
| Centralized        | Meta Loop    | FIND→ANALYZE→CREATE→LINK       | Data source count             |
| Scalable           | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Load testing                  |
| Composable         | Meta Loop    | ANALYZE→EXTRACT→CREATE→LINK    | Module independence test      |
| Extendable         | Meta Loop    | FIND→ANALYZE→CREATE→SET        | Extension point availability  |
| Dynamical          | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Runtime behavior verification |
| Modular            | Meta Loop    | ANALYZE→FIND→EXTRACT→CREATE    | Isolation testing             |
| Component-based    | Meta Loop    | READ→FIND→ANALYZE→LINK         | Reuse metrics                 |
| Hereditary         | Meta Loop    | ANALYZE→FIND→CREATE→LINK       | Trait inheritance validation  |
| Hierarchical       | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Layer dependency rules        |
| YAGNI              | Meta Loop    | ANALYZE→FIND→FILTER→EXTRACT    | Necessity justification       |
| Fail Fast          | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Error detection timing        |
| Loose Coupling     | Meta Loop    | ANALYZE→FIND→FILTER→CREATE     | Coupling metrics              |
| High Cohesion      | Meta Loop    | ANALYZE→FIND→FILTER→EXTRACT    | Cohesion metrics              |
| Encapsulation      | Meta Loop    | ANALYZE→FIND→FILTER→CREATE     | Visibility analysis           |
| Immutability       | Meta Loop    | ANALYZE→FIND→CREATE→EXECUTE    | Mutation operation detection  |
| Idempotency        | Meta Loop    | ANALYZE→FIND→CREATE→EXECUTE    | Repeated execution testing    |
| Orthogonality      | Meta Loop    | ANALYZE→FIND→FILTER→CREATE     | Change impact radius          |
| Layered            | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Layer crossing detection      |
| Event-driven       | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Direct coupling count         |
| Declarative        | Meta Loop    | ANALYZE→FIND→CREATE→SET        | Imperative code detection     |
| Stateless          | Meta Loop    | ANALYZE→FIND→EXTRACT→CREATE    | State persistence detection   |
| Bounded Complexity | Meta Loop    | ANALYZE→FIND→EXTRACT→CREATE    | LOC and file count metrics    |
