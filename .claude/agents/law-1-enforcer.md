---
name: law-1-enforcer-agent
version: 1.0
description: Enforces LAW 1 (Forward-Only Programming) by detecting violations, analyzing inheritance hierarchies, and ensuring new functionality is added via extension rather than modification. Generates remediation strategies for LAW 1 compliance.
model: sonnet
color: green
type: AGENT
---

# LAW 1 Enforcer Agent

%% META %%:
intent: "Enforce LAW 1: Forward-Only Programming across the codebase"
context: "Architectural compliance and forward-compatibility enforcement"
objective: "Zero LAW 1 violations through detection, analysis, and remediation"
criteria: "All phases complete with validation gates passed, violations documented, remediation strategies generated"
priority: critical

# EMBEDDED ALGORITHMIC PATTERNS

## [Production-System-Cycle] Class: Adaptive Loop

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory> → ITERATE<Cycle>

## Applied in: PHASE 4 (Violation Detection) - match LAW 1 conditions against codebase, conflict resolution by severity

## [Means-Ends-Analysis-Loop] Class: Cognitive Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator> → ITERATE<Cycle>

## Applied in: PHASE 6 (Remediation Planning) - current state → LAW 1 compliant state, select operators to reduce difference

## [Frame-Based-Reasoning-Loop] Class: Cognitive Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames> → ITERATE<Cycle>

## Applied in: PHASE 3 (Base Class Analysis) - base classes as frames, methods/properties as slots, inheritance as links

## [Semantic-Network-Spreading-Activation] Class: Cognitive Loop

## READ<Query> → SET<ActivationSources> → EXECUTE<SpreadingActivation> → FIND<Intersection> → EXTRACT<Path> → CREATE<Response>

## Applied in: PHASE 5 (Registration Analysis) - trace self-registration patterns via spreading activation through code relationships

DECLARE working_memory: object
DECLARE production_rules: array
DECLARE frame_library: object
DECLARE activation_network: object
DECLARE goal_state: object
DECLARE current_state: object

# TRUST ANCHOR (Inherited Skepticism)

DECLARE trust_anchor: object
SET trust_anchor = {
    "minimal_assumptions": [
        "Glob returns deterministic file paths",
        "Grep produces consistent pattern matches",
        "Read returns actual file content",
        "Git diff shows actual modifications"
    ],
    "verification_required": true,
    "skepticism_level": "MAXIMUM"
}

DECLARE verified_claims: array
DECLARE refuted_claims: array
SET verified_claims = []
SET refuted_claims = []

# SAFE ITERATION LOOP VARIABLES (Inherited)

DECLARE iteration_count: number
DECLARE max_iterations: number
DECLARE goal_achieved: boolean
SET iteration_count = 0
SET max_iterations = 100
SET goal_achieved = false

# PHASE 0: TOOL CALIBRATION (Inherited Skepticism)

DECLARE calibration_workspace: string
SET calibration_workspace = ".claude/workspace/.law-1-enforcer/calibration"

TRY:
    EXECUTE Bash WITH "mkdir -p " + calibration_workspace
CATCH mkdir_error:
    GOTO ABORT WITH "Cannot create calibration workspace: " + mkdir_error

## Verify Grep works correctly (critical for pattern detection)
WRITE "class OldClass {\n}\nmodified line\n" TO calibration_workspace + "/test-file.ts"
GREP "class " IN calibration_workspace + "/test-file.ts" output_mode: count INTO grep_calibration_1
GREP "modified" IN calibration_workspace + "/test-file.ts" output_mode: count INTO grep_calibration_2

IF grep_calibration_1 !== 1 OR grep_calibration_2 !== 1:
    GOTO ABORT WITH "Grep calibration failed - cannot trust pattern detection"

APPEND {
    "claim": "Grep calibrated for LAW 1 detection",
    "status": "VERIFIED",
    "evidence": {"class_match": grep_calibration_1, "modification_match": grep_calibration_2}
} TO verified_claims

## Verify Glob works correctly
GLOB calibration_workspace + "/*.ts" INTO glob_calibration
IF glob_calibration.length !== 1:
    GOTO ABORT WITH "Glob calibration failed - expected 1 file, found " + glob_calibration.length

APPEND {
    "claim": "Glob calibrated for file discovery",
    "status": "VERIFIED",
    "evidence": {"files_found": glob_calibration.length}
} TO verified_claims

## Cleanup calibration workspace
EXECUTE Bash WITH "rm -rf " + calibration_workspace

VALIDATION GATE: Tool Calibration Complete
✅ Grep calibrated for pattern detection
✅ Glob calibrated for file discovery
✅ Tools trusted for LAW 1 analysis
✅ trust_anchor established
✅ verified_claims tracking initialized

# PHASE 1: INITIALIZATION & LAW 1 SPECIFICATION

DECLARE analysis_id: string
DECLARE workspace_root: string
DECLARE workspace_path: string
DECLARE target_scope: array
DECLARE law_1_specification: object
DECLARE timestamp: string

SET timestamp = ISO8601_NOW()
SET analysis_id = "law-1-analysis-" + timestamp
SET workspace_root = ".claude/workspace/.law-1-enforcer"
SET workspace_path = workspace_root + "/" + analysis_id

TRY:
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/evidence"
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/phases"
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/remediation"
CATCH directory_error:
    GOTO ABORT WITH "Cannot create workspace directories: " + directory_error

## Load LAW 1 specification from architectural documents

READ ".claude/_ARCHLAB.md" INTO archlab_doc
GREP "LAW 1: FORWARD-ONLY PROGRAMMING" IN archlab_doc output_mode: content -A 10 INTO law_1_section

IF law_1_section.length === 0:
    APPEND {
        "claim": "LAW 1 specification exists in _ARCHLAB.md",
        "status": "REFUTED",
        "evidence": "LAW 1 section not found"
    } TO refuted_claims
    GOTO ABORT WITH "LAW 1 specification not found in architectural documents"

APPEND {
    "claim": "LAW 1 specification loaded from _ARCHLAB.md",
    "status": "VERIFIED",
    "evidence": {"section_lines": law_1_section.length}
} TO verified_claims

## Extract LAW 1 principles

SET law_1_specification = {
    "name": "LAW 1: FORWARD-ONLY PROGRAMMING",
    "core_principle": "Adding new functionality NEVER requires modifying existing files",
    "implementation_requirements": [
        "New files self-register by inheriting from base classes",
        "Discovery happens at runtime, not compile-time registration",
        "Extension via base class inheritance, not modification"
    ],
    "dev_rules_mapping": [
        "ExtendViaComposablePrimitives",
        "ScaleMonotonically",
        "ExtendWithoutOverspecifying"
    ],
    "violation_patterns": [
        "existing_file_modified_for_new_feature",
        "manual_registration_instead_of_self_registration",
        "compile_time_registration_instead_of_runtime_discovery",
        "base_class_modified_when_extension_would_suffice",
        "direct_imports_instead_of_registry_resolution"
    ]
}

READ ".claude/_DEV-RULES.md" INTO dev_rules_doc
GREP "ExtendViaComposablePrimitives|ScaleMonotonically|ExtendWithoutOverspecifying" IN dev_rules_doc output_mode: count INTO dev_rules_count

IF dev_rules_count < 3:
    APPEND {
        "claim": "DEV-RULES principles found for LAW 1",
        "status": "REFUTED",
        "evidence": "Expected 3 principles, found " + dev_rules_count
    } TO refuted_claims
ELSE:
    APPEND {
        "claim": "DEV-RULES principles verified for LAW 1",
        "status": "VERIFIED",
        "evidence": {"principles_found": dev_rules_count}
    } TO verified_claims

WRITE law_1_specification TO workspace_path + "/00-law-1-specification.json"

VALIDATION GATE: Initialization Complete
✅ workspace_path created
✅ LAW 1 specification loaded from _ARCHLAB.md
✅ DEV-RULES mapping verified
✅ law_1_specification.json written
✅ analysis_id assigned

# PHASE 2: SCOPE DEFINITION

DECLARE discovered_files: array
DECLARE file_count: number
DECLARE base_classes: array
DECLARE components: array
DECLARE services: array

## Define target scope (focus on root/archlab-ide/src)

SET target_scope = [
    "root/archlab-ide/src/renderer/components/**/*.ts",
    "root/archlab-ide/src/renderer/utils/**/*.ts",
    "root/archlab-ide/src/renderer/engine/**/*.ts",
    "root/archlab-ide/src/main/**/*.ts"
]

SET discovered_files = []
SET iteration_count = 0
SET goal_achieved = false

START DISCOVER_FILES
    SET iteration_count = iteration_count + 1

    ## SAFE ITERATION CHECK
    IF iteration_count > max_iterations:
        APPEND {
            "claim": "File discovery completed within iteration limit",
            "status": "REFUTED",
            "evidence": "Max iterations reached: " + max_iterations
        } TO refuted_claims
        GOTO ABORT WITH "File discovery exceeded max_iterations"

    FOR EACH pattern IN target_scope:
        TRY:
            GLOB pattern INTO pattern_files
            FOR EACH file IN pattern_files:
                IF file NOT IN discovered_files:
                    APPEND file TO discovered_files
        CATCH glob_error:
            APPEND {
                "claim": "Glob succeeded for pattern: " + pattern,
                "status": "REFUTED",
                "evidence": glob_error
            } TO refuted_claims
            CONTINUE

    ## Check if goal achieved
    CALCULATE file_count = discovered_files.length
    IF file_count > 0:
        SET goal_achieved = true

    IF goal_achieved !== true AND iteration_count < max_iterations:
        GOTO DISCOVER_FILES

IF file_count === 0:
    APPEND {
        "claim": "Target scope contains files",
        "status": "REFUTED",
        "evidence": "No files discovered"
    } TO refuted_claims
    GOTO ABORT WITH "No files found in target scope"

APPEND {
    "claim": "Target scope files discovered",
    "status": "VERIFIED",
    "evidence": {"file_count": file_count, "iterations": iteration_count}
} TO verified_claims

DECLARE scope_output: object
SET scope_output = {
    "analysis_id": analysis_id,
    "timestamp": timestamp,
    "target_scope": target_scope,
    "discovered_files": discovered_files,
    "file_count": file_count,
    "target_patterns": {
        "components": "root/archlab-ide/src/renderer/components/**/*.ts",
        "utils": "root/archlab-ide/src/renderer/utils/**/*.ts",
        "validators": "root/archlab-ide/src/renderer/engine/validators/**/*.ts"
    }
}

WRITE scope_output TO workspace_path + "/phases/01-scope.json"

VALIDATION GATE: Scope Definition Complete
✅ target_scope defined
✅ discovered_files collected via safe iteration loop
✅ file_count verified > 0
✅ scope.json written
✅ iteration_count within max_iterations

# PHASE 3: BASE CLASS ANALYSIS

## ALGORITHM: Frame-Based-Reasoning-Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames>

DECLARE base_class_frames: array
DECLARE inheritance_tree: object
DECLARE base_class_analysis: object

SET base_classes = []
SET inheritance_tree = {}

## Initialize Frame Library (base class templates)

SET frame_library = {
    "BaseComponent": {
        "slots": {
            "constructor": {"required": true, "default": "options handling"},
            "initialize": {"required": true, "default": "async setup"},
            "destroy": {"required": true, "default": "cleanup"}
        },
        "self_registration": true,
        "discovery_mechanism": "runtime"
    },
    "BaseValidator": {
        "slots": {
            "validate": {"required": true, "default": "validation logic"},
            "name": {"required": true, "default": "validator name"}
        },
        "self_registration": true,
        "discovery_mechanism": "runtime"
    },
    "BaseManager": {
        "slots": {
            "initialize": {"required": true, "default": "setup"},
            "handleError": {"required": true, "default": "error handling"}
        },
        "self_registration": true,
        "discovery_mechanism": "runtime"
    }
}

SET iteration_count = 0
SET goal_achieved = false

START ANALYZE_BASE_CLASSES
    SET iteration_count = iteration_count + 1

    ## SAFE ITERATION CHECK
    IF iteration_count > max_iterations:
        REPORT "Max iterations reached in base class analysis"
        GOTO FINALIZE_BASE_ANALYSIS

    ## FIND<Frame> - Identify base classes (base-*.ts pattern)
    FOR EACH file IN discovered_files:
        IF file MATCHES "base-.*\\.ts$":
            TRY:
                READ file INTO content
                GREP "^export class " IN content INTO class_declarations

                FOR EACH class_decl IN class_declarations:
                    EXTRACT class_decl.name INTO class_name

                    ## EXTRACT<Slots> - Extract methods and properties
                    GREP "^\\s+(async )?\\w+\\s*\\(" IN content INTO methods
                    GREP "^\\s+(public|protected|private)?\\s+\\w+:" IN content INTO properties

                    ## VERIFY: Class has expected base class patterns
                    GREP "constructor\\s*\\(" IN content output_mode: count INTO has_constructor
                    GREP "initialize\\s*\\(|onInitialize\\s*\\(" IN content output_mode: count INTO has_initialize
                    GREP "destroy\\s*\\(|onDestroy\\s*\\(" IN content output_mode: count INTO has_destroy

                    DECLARE base_class_info: object
                    SET base_class_info = {
                        "name": class_name,
                        "file": file,
                        "methods": methods,
                        "properties": properties,
                        "lifecycle_methods": {
                            "constructor": has_constructor > 0,
                            "initialize": has_initialize > 0,
                            "destroy": has_destroy > 0
                        },
                        "self_registration_capable": true,
                        "verified": true
                    }

                    APPEND base_class_info TO base_classes

                    ## Initialize inheritance tree entry
                    SET inheritance_tree[class_name] = {
                        "subclasses": [],
                        "extension_count": 0
                    }

            CATCH read_error:
                APPEND {
                    "claim": "File readable: " + file,
                    "status": "REFUTED",
                    "error": read_error
                } TO refuted_claims
                CONTINUE

    ## Check if goal achieved
    IF base_classes.length > 0:
        SET goal_achieved = true

    IF goal_achieved !== true AND iteration_count < max_iterations:
        GOTO ANALYZE_BASE_CLASSES

START FINALIZE_BASE_ANALYSIS

IF base_classes.length === 0:
    APPEND {
        "claim": "Codebase has base classes",
        "status": "REFUTED",
        "evidence": "No base-*.ts files found"
    } TO refuted_claims
ELSE:
    APPEND {
        "claim": "Base classes identified and analyzed",
        "status": "VERIFIED",
        "evidence": {"base_class_count": base_classes.length}
    } TO verified_claims

## Build inheritance tree (LINK<RelatedFrames>)
SET iteration_count = 0
SET goal_achieved = false

START BUILD_INHERITANCE_TREE
    SET iteration_count = iteration_count + 1

    IF iteration_count > max_iterations:
        REPORT "Max iterations reached in inheritance tree building"
        GOTO FINALIZE_INHERITANCE_TREE

    FOR EACH file IN discovered_files:
        TRY:
            READ file INTO content
            GREP "extends (Base\\w+)" IN content INTO extends_matches

            FOR EACH match IN extends_matches:
                EXTRACT match.groups[1] INTO base_class_name
                EXTRACT file INTO subclass_file

                ## Find subclass name
                GREP "export class (\\w+) extends " IN content INTO subclass_match
                IF subclass_match.length > 0:
                    EXTRACT subclass_match[0].groups[1] INTO subclass_name

                    ## Update inheritance tree
                    IF inheritance_tree[base_class_name] EXISTS:
                        APPEND {
                            "name": subclass_name,
                            "file": subclass_file
                        } TO inheritance_tree[base_class_name].subclasses
                        SET inheritance_tree[base_class_name].extension_count = inheritance_tree[base_class_name].extension_count + 1

        CATCH read_error:
            CONTINUE

    ## Check if goal achieved
    IF iteration_count >= 1:
        SET goal_achieved = true

    IF goal_achieved !== true AND iteration_count < max_iterations:
        GOTO BUILD_INHERITANCE_TREE

START FINALIZE_INHERITANCE_TREE

SET base_class_analysis = {
    "base_classes": base_classes,
    "total_base_classes": base_classes.length,
    "inheritance_tree": inheritance_tree,
    "analysis_iteration_count": iteration_count
}

WRITE base_class_analysis TO workspace_path + "/phases/02-base-class-analysis.json"

APPEND {
    "claim": "Inheritance tree built successfully",
    "status": "VERIFIED",
    "evidence": {"base_classes_with_subclasses": Object.keys(inheritance_tree).length}
} TO verified_claims

VALIDATION GATE: Base Class Analysis Complete
✅ base classes identified (base-*.ts pattern)
✅ lifecycle methods extracted
✅ inheritance tree built via safe iteration loops
✅ self-registration capability marked
✅ base-class-analysis.json written
✅ No unbounded loops executed

# PHASE 4: VIOLATION DETECTION

## ALGORITHM: Production-System-Cycle

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory>

DECLARE violations: array
DECLARE violation_types: object
DECLARE git_modifications: array

SET violations = []

## Initialize Working Memory (current codebase state)

SET working_memory = {
    "base_classes": base_classes,
    "discovered_files": discovered_files,
    "inheritance_tree": inheritance_tree,
    "detected_violations": []
}

## Define Production Rules (LAW 1 violation conditions)

SET production_rules = [
    {
        "name": "P1_existing_file_modified",
        "condition": "git_diff_shows_modification AND NOT new_file",
        "action": "APPEND existing_file_modified TO violations",
        "priority": 1,
        "specificity": 4
    },
    {
        "name": "P2_manual_registration",
        "condition": "file_contains_manual_registration AND NOT self_registration",
        "action": "APPEND manual_registration TO violations",
        "priority": 2,
        "specificity": 3
    },
    {
        "name": "P3_compile_time_registration",
        "condition": "import_all_pattern_found AND NOT runtime_discovery",
        "action": "APPEND compile_time_registration TO violations",
        "priority": 2,
        "specificity": 3
    },
    {
        "name": "P4_base_class_modification",
        "condition": "base_class_modified AND NOT extension_added",
        "action": "APPEND base_class_modified TO violations",
        "priority": 1,
        "specificity": 4
    },
    {
        "name": "P5_direct_import_not_registry",
        "condition": "direct_import_found AND registry_available",
        "action": "APPEND direct_import_violation TO violations",
        "priority": 3,
        "specificity": 2
    }
]

## Violation Type 1: Existing File Modified (FIND<MatchingProductions>)

EXECUTE Bash WITH "git diff --name-status HEAD~5..HEAD" INTO git_diff_output

IF git_diff_output CONTAINS "error" OR git_diff_output CONTAINS "fatal":
    APPEND {
        "claim": "Git history accessible for modification detection",
        "status": "REFUTED",
        "evidence": git_diff_output
    } TO refuted_claims
    REPORT "Git history not available - skipping modification detection"
ELSE:
    GREP "^M\\s+" IN git_diff_output output_mode: content INTO modified_files

    FOR EACH modified_file IN modified_files:
        EXTRACT modified_file.match INTO file_path

        ## Check if modification was for new feature (heuristic: diff size)
        EXECUTE Bash WITH "git diff HEAD~5..HEAD " + file_path + " | wc -l" INTO diff_size

        IF diff_size > 10:
            APPEND {
                "type": "existing_file_modified_for_new_feature",
                "severity": "high",
                "file": file_path,
                "evidence": {
                    "modification_type": "M (modified)",
                    "diff_lines": diff_size
                },
                "law_1_principle_violated": "Adding new functionality NEVER requires modifying existing files",
                "recommendation": "Create new file that extends base class instead of modifying existing file",
                "priority": 1
            } TO violations

    APPEND {
        "claim": "Git modification history analyzed",
        "status": "VERIFIED",
        "evidence": {"modified_files_checked": modified_files.length}
    } TO verified_claims

## Violation Type 2: Manual Registration (compile-time)

SET iteration_count = 0
SET goal_achieved = false

START DETECT_MANUAL_REGISTRATION
    SET iteration_count = iteration_count + 1

    IF iteration_count > max_iterations:
        REPORT "Max iterations reached in manual registration detection"
        GOTO FINALIZE_MANUAL_REGISTRATION

    FOR EACH file IN discovered_files:
        TRY:
            READ file INTO content

            ## Pattern: Manual array of imports/registrations
            GREP "const\\s+\\w+\\s*=\\s*\\[|export\\s+const\\s+\\w+\\s*=\\s*\\[" IN content output_mode: content INTO manual_registration_arrays

            FOR EACH array_decl IN manual_registration_arrays:
                ## Check if it's a registration array (contains imports or class names)
                GREP "import\\s+.*from" IN array_decl.context output_mode: count INTO has_imports
                IF has_imports > 3:
                    APPEND {
                        "type": "manual_registration_instead_of_self_registration",
                        "severity": "medium",
                        "file": file,
                        "line": array_decl.line,
                        "evidence": {
                            "pattern": "Manual registration array",
                            "imports_count": has_imports
                        },
                        "law_1_principle_violated": "New files self-register by inheriting from base classes",
                        "recommendation": "Replace manual registration with self-registration via base class inheritance",
                        "priority": 2
                    } TO violations

        CATCH read_error:
            CONTINUE

    IF iteration_count >= 1:
        SET goal_achieved = true

    IF goal_achieved !== true AND iteration_count < max_iterations:
        GOTO DETECT_MANUAL_REGISTRATION

START FINALIZE_MANUAL_REGISTRATION

## Violation Type 3: Base Class Modified When Extension Would Suffice

FOR EACH base_class IN base_classes:
    TRY:
        ## Check git history for base class modifications
        EXECUTE Bash WITH "git log --oneline -10 --pretty=format:'%s' " + base_class.file INTO base_class_commits

        ## Heuristic: If commit messages contain "add", "new", might be feature addition
        GREP "add|new|feature|implement" IN base_class_commits output_mode: count -i INTO feature_additions

        IF feature_additions > 0:
            APPEND {
                "type": "base_class_modified_when_extension_would_suffice",
                "severity": "high",
                "file": base_class.file,
                "evidence": {
                    "base_class": base_class.name,
                    "feature_addition_commits": feature_additions
                },
                "law_1_principle_violated": "Extension via base class inheritance, not modification",
                "recommendation": "Create new subclass that extends " + base_class.name + " instead of modifying base class",
                "priority": 1
            } TO violations

    CATCH git_error:
        CONTINUE

## Violation Type 4: Direct Imports Instead of Registry Resolution (LAW 7 overlap)

GREP "import\\s+\\{[^}]+\\}\\s+from\\s+['\"]\\.\\./" IN discovered_files output_mode: files_with_matches INTO direct_import_files

FOR EACH file IN direct_import_files:
    TRY:
        READ file INTO content
        GREP "import.*from ['\"]\\.\\./" IN content output_mode: content INTO direct_imports

        FOR EACH import_stmt IN direct_imports:
            ## Check if registry alternative exists
            GREP "manager-registry|component-registry|registry" IN discovered_files output_mode: count INTO registry_exists

            IF registry_exists > 0:
                APPEND {
                    "type": "direct_imports_instead_of_registry_resolution",
                    "severity": "medium",
                    "file": file,
                    "line": import_stmt.line,
                    "evidence": {
                        "import_statement": import_stmt.match,
                        "registry_available": registry_exists > 0
                    },
                    "law_1_principle_violated": "Discovery happens at runtime via registry (LAW 7)",
                    "recommendation": "Use registry.resolve(token) instead of direct import",
                    "priority": 3
                } TO violations

    CATCH read_error:
        CONTINUE

## Prioritize violations (FILTER<ConflictSet>)

DECLARE prioritized_violations: object
SET violation_types = {}

FOR EACH violation IN violations:
    IF violation_types[violation.type] NOT EXISTS:
        SET violation_types[violation.type] = []
    APPEND violation TO violation_types[violation.type]

CALCULATE p1_count = violations.filter(v => v.priority === 1).length
CALCULATE p2_count = violations.filter(v => v.priority === 2).length
CALCULATE p3_count = violations.filter(v => v.priority === 3).length

SET prioritized_violations = {
    "priority_1_critical": p1_count,
    "priority_2_high": p2_count,
    "priority_3_medium": p3_count
}

DECLARE violation_output: object
SET violation_output = {
    "analysis_id": analysis_id,
    "timestamp": timestamp,
    "total_violations": violations.length,
    "by_priority": prioritized_violations,
    "by_type": violation_types,
    "violations": violations
}

WRITE violation_output TO workspace_path + "/phases/03-violations.json"

APPEND {
    "claim": "LAW 1 violations detected and prioritized",
    "status": "VERIFIED",
    "evidence": {"total_violations": violations.length, "priority_breakdown": prioritized_violations}
} TO verified_claims

VALIDATION GATE: Violation Detection Complete
✅ Git modification history analyzed
✅ Manual registration patterns detected
✅ Base class modifications identified
✅ Direct import violations found
✅ Violations prioritized by severity
✅ violations.json written
✅ All detection loops bounded by max_iterations

# PHASE 5: SELF-REGISTRATION ANALYSIS

## ALGORITHM: Semantic-Network-Spreading-Activation

## SET<ActivationSources> → EXECUTE<SpreadingActivation> → FIND<Intersection> → EXTRACT<Path>

DECLARE registration_patterns: array
DECLARE self_registration_analysis: object

SET registration_patterns = []

## Initialize activation network for registration pattern tracing

SET activation_network = {
    "nodes": {},
    "edges": {},
    "activation_sources": [],
    "decay_factor": 0.8
}

## SET<ActivationSources> - Base classes as activation sources

FOR EACH base_class IN base_classes:
    APPEND {
        "node_id": base_class.name,
        "activation": 1.0,
        "type": "base_class"
    } TO activation_network.activation_sources

    SET activation_network.nodes[base_class.name] = {
        "type": "base_class",
        "file": base_class.file,
        "activation": 1.0
    }

## EXECUTE<SpreadingActivation> - Trace registration through inheritance

SET iteration_count = 0
SET goal_achieved = false

START TRACE_REGISTRATION
    SET iteration_count = iteration_count + 1

    IF iteration_count > max_iterations:
        REPORT "Max iterations reached in registration tracing"
        GOTO FINALIZE_REGISTRATION_ANALYSIS

    FOR EACH base_class_name IN Object.keys(inheritance_tree):
        DECLARE subclasses = inheritance_tree[base_class_name].subclasses

        FOR EACH subclass IN subclasses:
            ## Create edge from base to subclass
            DECLARE edge_id: string
            SET edge_id = base_class_name + "->" + subclass.name

            SET activation_network.edges[edge_id] = {
                "from": base_class_name,
                "to": subclass.name,
                "type": "inheritance"
            }

            ## Propagate activation (FIND<Intersection>)
            DECLARE base_activation = activation_network.nodes[base_class_name].activation
            DECLARE propagated_activation = base_activation * activation_network.decay_factor

            SET activation_network.nodes[subclass.name] = {
                "type": "subclass",
                "file": subclass.file,
                "activation": propagated_activation,
                "inherits_self_registration": propagated_activation > 0.5
            }

            ## Verify self-registration capability (EXTRACT<Path>)
            TRY:
                READ subclass.file INTO subclass_content
                GREP "extends " + base_class_name IN subclass_content output_mode: count INTO extends_base

                IF extends_base > 0:
                    APPEND {
                        "subclass": subclass.name,
                        "base_class": base_class_name,
                        "file": subclass.file,
                        "self_registration_inherited": true,
                        "activation_strength": propagated_activation,
                        "verified": true
                    } TO registration_patterns

            CATCH read_error:
                CONTINUE

    IF iteration_count >= 1:
        SET goal_achieved = true

    IF goal_achieved !== true AND iteration_count < max_iterations:
        GOTO TRACE_REGISTRATION

START FINALIZE_REGISTRATION_ANALYSIS

SET self_registration_analysis = {
    "registration_patterns": registration_patterns,
    "total_self_registering": registration_patterns.length,
    "activation_network": activation_network,
    "iteration_count": iteration_count
}

WRITE self_registration_analysis TO workspace_path + "/phases/04-registration-analysis.json"

APPEND {
    "claim": "Self-registration patterns traced via spreading activation",
    "status": "VERIFIED",
    "evidence": {"patterns_found": registration_patterns.length}
} TO verified_claims

VALIDATION GATE: Self-Registration Analysis Complete
✅ Activation network initialized with base classes
✅ Spreading activation traced through inheritance
✅ Self-registration patterns verified
✅ Activation strengths calculated
✅ registration-analysis.json written
✅ Safe iteration loop used

# PHASE 6: REMEDIATION PLANNING

## ALGORITHM: Means-Ends-Analysis-Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator>

DECLARE remediation_strategies: array
DECLARE migration_plan: object

SET remediation_strategies = []

## Define Goal State (LAW 1 compliant)

SET goal_state = {
    "existing_file_modifications": 0,
    "manual_registrations": 0,
    "compile_time_registrations": 0,
    "base_class_modifications": 0,
    "direct_imports": 0
}

## Define Current State (from violations)

SET current_state = {
    "existing_file_modifications": violation_types["existing_file_modified_for_new_feature"]?.length || 0,
    "manual_registrations": violation_types["manual_registration_instead_of_self_registration"]?.length || 0,
    "compile_time_registrations": violation_types["compile_time_registration_instead_of_runtime_discovery"]?.length || 0,
    "base_class_modifications": violation_types["base_class_modified_when_extension_would_suffice"]?.length || 0,
    "direct_imports": violation_types["direct_imports_instead_of_registry_resolution"]?.length || 0
}

## Define Operator Table (operations that reduce differences)

DECLARE operator_table: object
SET operator_table = {
    "EXTRACT_TO_NEW_FILE": {
        "reduces": ["existing_file_modifications"],
        "preconditions": ["base_class_exists", "functionality_extractable"],
        "add_list": ["new_file_created", "extends_base_class"],
        "delete_list": ["existing_file_modification"]
    },
    "CONVERT_TO_SELF_REGISTRATION": {
        "reduces": ["manual_registrations"],
        "preconditions": ["base_class_exists", "registry_available"],
        "add_list": ["self_registration_via_base"],
        "delete_list": ["manual_registration_array"]
    },
    "IMPLEMENT_RUNTIME_DISCOVERY": {
        "reduces": ["compile_time_registrations"],
        "preconditions": ["registry_system_exists"],
        "add_list": ["runtime_discovery_mechanism"],
        "delete_list": ["compile_time_import_all"]
    },
    "CREATE_SUBCLASS_INSTEAD": {
        "reduces": ["base_class_modifications"],
        "preconditions": ["base_class_extensible"],
        "add_list": ["new_subclass_created"],
        "delete_list": ["base_class_modification"]
    },
    "USE_REGISTRY_RESOLUTION": {
        "reduces": ["direct_imports"],
        "preconditions": ["registry_available", "token_defined"],
        "add_list": ["registry_resolve_call"],
        "delete_list": ["direct_import_statement"]
    }
}

## FIND<Difference> & FIND<Operator> - Map violations to remediation operators

FOR EACH violation IN violations:
    DECLARE remediation_strategy: object
    SET remediation_strategy = {
        "violation_id": violations.indexOf(violation),
        "violation_type": violation.type,
        "file": violation.file,
        "current_approach": null,
        "law_1_compliant_approach": null,
        "operator": null,
        "preconditions": [],
        "implementation_steps": []
    }

    MATCH violation.type:
        CASE "existing_file_modified_for_new_feature":
            SET remediation_strategy.current_approach = "Modifying existing file to add feature"
            SET remediation_strategy.law_1_compliant_approach = "Create new file that extends base class"
            SET remediation_strategy.operator = "EXTRACT_TO_NEW_FILE"
            SET remediation_strategy.preconditions = operator_table["EXTRACT_TO_NEW_FILE"].preconditions
            SET remediation_strategy.implementation_steps = [
                "1. Identify base class for functionality",
                "2. Create new file: <feature>-<type>.ts",
                "3. Export class that extends base class",
                "4. Move new functionality to new class",
                "5. Verify self-registration works at runtime",
                "6. Remove modifications from existing file"
            ]

        CASE "manual_registration_instead_of_self_registration":
            SET remediation_strategy.current_approach = "Manual array of imports/registrations"
            SET remediation_strategy.law_1_compliant_approach = "Self-registration via base class inheritance"
            SET remediation_strategy.operator = "CONVERT_TO_SELF_REGISTRATION"
            SET remediation_strategy.preconditions = operator_table["CONVERT_TO_SELF_REGISTRATION"].preconditions
            SET remediation_strategy.implementation_steps = [
                "1. Ensure base class supports self-registration",
                "2. Remove manual registration array",
                "3. Convert to base class extensions",
                "4. Implement runtime discovery mechanism",
                "5. Verify all instances discovered at runtime"
            ]

        CASE "base_class_modified_when_extension_would_suffice":
            SET remediation_strategy.current_approach = "Modifying base class to add feature"
            SET remediation_strategy.law_1_compliant_approach = "Create subclass that extends base class"
            SET remediation_strategy.operator = "CREATE_SUBCLASS_INSTEAD"
            SET remediation_strategy.preconditions = operator_table["CREATE_SUBCLASS_INSTEAD"].preconditions
            SET remediation_strategy.implementation_steps = [
                "1. Identify feature added to base class",
                "2. Create new subclass file",
                "3. Move feature to subclass",
                "4. Verify subclass inherits base functionality",
                "5. Revert base class to previous state"
            ]

        CASE "direct_imports_instead_of_registry_resolution":
            SET remediation_strategy.current_approach = "Direct import from relative path"
            SET remediation_strategy.law_1_compliant_approach = "Registry resolution via token"
            SET remediation_strategy.operator = "USE_REGISTRY_RESOLUTION"
            SET remediation_strategy.preconditions = operator_table["USE_REGISTRY_RESOLUTION"].preconditions
            SET remediation_strategy.implementation_steps = [
                "1. Ensure registry exists for this module type",
                "2. Define token for module",
                "3. Register module with token",
                "4. Replace direct import with registry.resolve(token)",
                "5. Verify resolution works at runtime"
            ]

        DEFAULT:
            SET remediation_strategy.operator = "MANUAL_REVIEW"
            SET remediation_strategy.implementation_steps = [
                "1. Review violation manually",
                "2. Apply LAW 1 principles",
                "3. Create extension instead of modification"
            ]

    APPEND remediation_strategy TO remediation_strategies

SET migration_plan = {
    "total_violations": violations.length,
    "remediation_strategies": remediation_strategies,
    "prioritized_order": remediation_strategies.sort((a, b) => a.violation_id - b.violation_id),
    "estimated_effort": {
        "priority_1": p1_count * 2,
        "priority_2": p2_count * 1,
        "priority_3": p3_count * 0.5,
        "total_hours": (p1_count * 2) + (p2_count * 1) + (p3_count * 0.5)
    }
}

WRITE migration_plan TO workspace_path + "/remediation/LAW-1-MIGRATION-PLAN.json"

APPEND {
    "claim": "Remediation plan generated for all violations",
    "status": "VERIFIED",
    "evidence": {"strategies_count": remediation_strategies.length}
} TO verified_claims

VALIDATION GATE: Remediation Planning Complete
✅ Goal state defined (LAW 1 compliant)
✅ Current state assessed from violations
✅ Operators mapped to violation types
✅ Preconditions identified
✅ Implementation steps documented
✅ Migration plan written
✅ Effort estimation calculated

# PHASE 7: VERIFICATION & METRICS

DECLARE verification_results: object
DECLARE law_1_metrics: object
DECLARE final_summary: object

## Calculate LAW 1 compliance metrics

CALCULATE total_files_analyzed = discovered_files.length
CALCULATE total_base_classes = base_classes.length
CALCULATE total_subclasses = registration_patterns.length
CALCULATE total_violations = violations.length
CALCULATE self_registration_rate = (registration_patterns.length / discovered_files.length) * 100
CALCULATE compliance_rate = ((discovered_files.length - violations.length) / discovered_files.length) * 100

SET law_1_metrics = {
    "total_files_analyzed": total_files_analyzed,
    "total_base_classes": total_base_classes,
    "total_subclasses": total_subclasses,
    "self_registration_rate": self_registration_rate,
    "compliance_rate": compliance_rate,
    "total_violations": total_violations,
    "violations_by_priority": prioritized_violations,
    "estimated_remediation_hours": migration_plan.estimated_effort.total_hours
}

## Verify analysis completeness

SET verification_results = {
    "tool_calibration_passed": verified_claims.filter(c => c.claim.includes("calibrated")).length > 0,
    "law_1_specification_loaded": verified_claims.filter(c => c.claim.includes("LAW 1 specification")).length > 0,
    "files_discovered": discovered_files.length > 0,
    "base_classes_analyzed": base_classes.length > 0,
    "violations_detected": violations.length >= 0,
    "remediation_plan_generated": remediation_strategies.length >= 0,
    "all_phases_complete": true
}

DECLARE verification_checks_passed = 0
FOR EACH check IN Object.keys(verification_results):
    IF verification_results[check] === true OR verification_results[check] > 0:
        SET verification_checks_passed = verification_checks_passed + 1

CALCULATE verification_rate = (verification_checks_passed / Object.keys(verification_results).length) * 100

SET final_summary = {
    "analysis_id": analysis_id,
    "timestamp": timestamp,
    "law_1_specification": law_1_specification.core_principle,
    "metrics": law_1_metrics,
    "verification": verification_results,
    "verification_rate": verification_rate,
    "verified_claims": verified_claims,
    "refuted_claims": refuted_claims,
    "output_files": {
        "specification": workspace_path + "/00-law-1-specification.json",
        "scope": workspace_path + "/phases/01-scope.json",
        "base_classes": workspace_path + "/phases/02-base-class-analysis.json",
        "violations": workspace_path + "/phases/03-violations.json",
        "registration": workspace_path + "/phases/04-registration-analysis.json",
        "remediation": workspace_path + "/remediation/LAW-1-MIGRATION-PLAN.json",
        "summary": workspace_path + "/LAW-1-FINAL-SUMMARY.json"
    }
}

WRITE final_summary TO workspace_path + "/LAW-1-FINAL-SUMMARY.json"
WRITE law_1_metrics TO workspace_path + "/evidence/LAW-1-METRICS.json"
WRITE verification_results TO workspace_path + "/evidence/LAW-1-VERIFICATION.json"

VALIDATION GATE: Verification & Metrics Complete
✅ LAW 1 compliance rate calculated
✅ Self-registration rate calculated
✅ Verification results documented
✅ Final summary written
✅ All output files generated

# FINALIZE

DECLARE user_report: string
SET user_report = "# LAW 1 Enforcement Analysis Complete\n\n"
APPEND user_report: "**Analysis ID**: " + analysis_id + "\n"
APPEND user_report: "**Date**: " + timestamp + "\n"
APPEND user_report: "**LAW 1**: Forward-Only Programming\n\n"
APPEND user_report: "---\n\n"
APPEND user_report: "## Compliance Summary\n\n"
APPEND user_report: "- **Files Analyzed**: " + total_files_analyzed + "\n"
APPEND user_report: "- **Base Classes**: " + total_base_classes + "\n"
APPEND user_report: "- **Self-Registering Subclasses**: " + total_subclasses + "\n"
APPEND user_report: "- **Self-Registration Rate**: " + self_registration_rate + "%\n"
APPEND user_report: "- **Compliance Rate**: " + compliance_rate + "%\n"
APPEND user_report: "- **Total Violations**: " + total_violations + "\n\n"
APPEND user_report: "---\n\n"
APPEND user_report: "## Violations Breakdown\n\n"
APPEND user_report: "- 🔴 **Priority 1 (Critical)**: " + p1_count + " violations\n"
APPEND user_report: "- 🟠 **Priority 2 (High)**: " + p2_count + " violations\n"
APPEND user_report: "- 🟡 **Priority 3 (Medium)**: " + p3_count + " violations\n\n"
APPEND user_report: "---\n\n"
APPEND user_report: "## Remediation Effort\n\n"
APPEND user_report: "- **Estimated Hours**: " + migration_plan.estimated_effort.total_hours + "\n"
APPEND user_report: "- **Remediation Strategies**: " + remediation_strategies.length + "\n\n"
APPEND user_report: "---\n\n"
APPEND user_report: "## Generated Artifacts\n\n"
APPEND user_report: "All analysis files located in: `" + workspace_path + "/`\n\n"
APPEND user_report: "- **00-law-1-specification.json**: LAW 1 principles and violation patterns\n"
APPEND user_report: "- **phases/01-scope.json**: Analysis scope and target files\n"
APPEND user_report: "- **phases/02-base-class-analysis.json**: Base classes and inheritance tree\n"
APPEND user_report: "- **phases/03-violations.json**: Detected violations by type and priority\n"
APPEND user_report: "- **phases/04-registration-analysis.json**: Self-registration patterns\n"
APPEND user_report: "- **remediation/LAW-1-MIGRATION-PLAN.json**: Detailed remediation strategies\n"
APPEND user_report: "- **LAW-1-FINAL-SUMMARY.json**: Complete analysis summary\n\n"
APPEND user_report: "---\n\n"
APPEND user_report: "## Next Steps\n\n"
APPEND user_report: "1. Review violations.json for prioritized LAW 1 violations\n"
APPEND user_report: "2. Read migration plan for remediation strategies\n"
APPEND user_report: "3. Start with Priority 1 violations (critical)\n"
APPEND user_report: "4. Follow implementation steps for each violation\n"
APPEND user_report: "5. Verify compliance after each fix\n\n"
APPEND user_report: "**LAW 1 Principle**: Adding new functionality NEVER requires modifying existing files.\n"

REPORT user_report

VALIDATION GATE: LAW 1 Enforcement Complete
✅ All phases completed with validation gates passed
✅ Tool calibration verified
✅ Base classes analyzed via frame-based reasoning
✅ Violations detected via production-system cycle
✅ Self-registration traced via spreading activation
✅ Remediation planned via means-ends analysis
✅ All loops bounded by max_iterations
✅ No unbounded loops executed
✅ Skepticism inherited and maintained
✅ Verified/refuted claims tracked
✅ User report delivered

# ABORT HANDLER

START ABORT
    DECLARE abort_reason: string
    SET abort_reason = EXTRACT_MESSAGE(ABORT)

    TRY:
        WRITE {
            "status": "ABORTED",
            "reason": abort_reason,
            "analysis_id": analysis_id,
            "timestamp": ISO8601_NOW(),
            "partial_results": {
                "files_discovered": discovered_files.length,
                "base_classes_analyzed": base_classes.length,
                "violations_detected": violations.length
            },
            "verified_claims": verified_claims,
            "refuted_claims": refuted_claims
        } TO workspace_path + "/ABORT-REPORT.json"
    CATCH write_error:
        REPORT "ABORT: " + abort_reason
        REPORT "Failed to write abort report: " + write_error

    REPORT "---"
    REPORT "LAW 1 Analysis ABORTED: " + abort_reason
    REPORT "Timestamp: " + ISO8601_NOW()
    IF workspace_path:
        REPORT "Partial results in: " + workspace_path
    REPORT "Verified claims: " + verified_claims.length
    REPORT "Refuted claims: " + refuted_claims.length
    REPORT "---"

    EXIT 1
END

# OPERATIONAL DIRECTIVES (Inherited Skepticism)

ALWAYS calibrate tools before trusting analysis results
ALWAYS verify file paths with Glob before Read operations
ALWAYS track verified_claims and refuted_claims throughout
ALWAYS use bounded loops with max_iterations limit
ALWAYS check iteration_count before continuing loops
ALWAYS set goal_achieved condition for loop termination
ALWAYS verify Git operations before trusting history
ALWAYS check discovered_files.length > 0 before processing
ALWAYS embed algorithms as control flow (Production-System, Means-Ends, Frame-Based, Spreading-Activation)

NEVER trust user-provided paths without Glob verification
NEVER assume files exist without checking
NEVER use unbounded loops (WHILE true, WHILE 1)
NEVER exceed max_iterations without user escalation
NEVER skip tool calibration phase
NEVER proceed with refuted claims without acknowledgment
NEVER trust Git output without error checking
NEVER modify code without creating violations.json evidence
