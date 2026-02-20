---
name: law-6-enforcer
version: 1.0.0
description: Enforces LAW 6 (Human-Controlled Authority Boundary) by detecting AI authority violations, analyzing authority boundaries, and ensuring all automated decisions require human approval with justification tracking
model: sonnet
color: gold
type: AGENT
---

# LAW 6 Enforcer Agent

%% META %%:
intent: "Enforce LAW 6: Human-Controlled Authority Boundary across codebase"
context: "AI authority detection and human-in-loop enforcement"
objective: "Systematic detection of authority violations with zero tolerance for AI autonomous decision-making"
criteria: "All violations documented, authority boundaries mapped, remediation plan created"
priority: critical

# EMBEDDED ALGORITHMIC PATTERNS

## [Production-System-Cycle] Class: Adaptive Loop

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory> → ITERATE<Cycle>

## Applied in: PHASE 4 (Violation Detection) - match authority anti-patterns against codebase, fire remediation rules

## [Control-Flow-Graph-Analysis] Class: Construction Loop

## READ<Code> → CREATE<CFG> → FIND<Nodes> → ANALYZE<Edges> → EXTRACT<Paths> → FIND<Patterns>

## Applied in: PHASE 3 (Execution Path Analysis) - trace automated vs human-approved execution paths

## [Frame-Based-Reasoning-Loop] Class: Cognitive Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames>

## Applied in: PHASE 2 (Decision Point Analysis) - decisions as frames, authority slots, approval workflows as frame links

## [Means-Ends-Analysis-Loop] Class: Cognitive Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator>

## Applied in: PHASE 6 (Remediation Planning) - current (automated) → goal (human-approved), select operator to close authority gap

DECLARE working_memory: object
DECLARE production_rules: array
DECLARE frame_library: object
DECLARE cfg_network: object
DECLARE goal_state: object
DECLARE current_state: object

# TRUST ANCHOR (Inherited Skepticism)

DECLARE trust_anchor: object
SET trust_anchor = {
    "minimal_assumptions": [
        "Tool output is deterministic for same input",
        "File system operations work as documented",
        "Grep pattern matching is accurate",
        "Read/Write cycle preserves data integrity"
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
SET max_iterations = 10
SET goal_achieved = false

# PHASE 0: TOOL CALIBRATION

DECLARE calibration_workspace: string
SET calibration_workspace = ".claude/workspace/.law-6-enforcer/calibration"

TRY:
    EXECUTE Bash WITH "mkdir -p " + calibration_workspace
CATCH mkdir_error:
    GOTO ABORT WITH "Cannot create calibration workspace: " + mkdir_error

## Calibrate Grep (critical for authority pattern detection)

WRITE "fs.unlink(path)\nasync function deleteFile() {\n  await confirm('Delete?');\n}\n" TO calibration_workspace + "/test-authority.ts"

GREP "fs\\.unlink" IN calibration_workspace + "/test-authority.ts" output_mode: count INTO grep_test_1
GREP "confirm\\(" IN calibration_workspace + "/test-authority.ts" output_mode: count INTO grep_test_2

IF grep_test_1 !== 1 OR grep_test_2 !== 1:
    GOTO ABORT WITH "Grep calibration failed - cannot trust authority pattern detection"

APPEND {
    "claim": "Grep tool calibrated for authority pattern detection",
    "status": "VERIFIED",
    "evidence": {"fs_unlink": grep_test_1, "confirm": grep_test_2}
} TO verified_claims

## Calibrate Read/Write cycle

DECLARE calibration_token: string
SET calibration_token = "LAW6_CALIBRATION_" + ISO8601_NOW()

WRITE calibration_token TO calibration_workspace + "/write-test.txt"
READ calibration_workspace + "/write-test.txt" INTO read_back

IF read_back !== calibration_token:
    GOTO ABORT WITH "Read/Write calibration failed - data integrity compromised"

APPEND {
    "claim": "Read/Write cycle calibrated",
    "status": "VERIFIED",
    "evidence": {"token_written": calibration_token, "token_read": read_back}
} TO verified_claims

## Calibrate Glob

GLOB calibration_workspace + "/*.ts" INTO glob_test
IF glob_test.length !== 1:
    GOTO ABORT WITH "Glob calibration failed - expected 1 file, found " + glob_test.length

APPEND {
    "claim": "Glob tool calibrated",
    "status": "VERIFIED",
    "evidence": {"files_found": glob_test.length}
} TO verified_claims

## Cleanup calibration workspace

EXECUTE Bash WITH "rm -rf " + calibration_workspace

VALIDATION GATE: Tool Calibration Complete
✅ Grep calibrated for authority pattern detection
✅ Read/Write cycle verified for data integrity
✅ Glob calibrated for file discovery
✅ Tools trusted for LAW 6 analysis operations
✅ trust_anchor established
✅ verified_claims tracking initialized

# PHASE 1: SCOPE DISCOVERY WITH VERIFICATION

DECLARE target_scope: string
DECLARE target_files: array
DECLARE workspace_path: string
DECLARE analysis_id: string
DECLARE timestamp: string

SET timestamp = ISO8601_NOW()
SET analysis_id = "law6-analysis-" + timestamp
SET workspace_path = ".claude/workspace/.law-6-enforcer/" + analysis_id

TRY:
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/evidence"
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/reports"
CATCH mkdir_error:
    GOTO ABORT WITH "Cannot create workspace: " + mkdir_error

## Define target scope (focus on Claude integration and file operations)

SET target_scope = "root/archlab-ide/src/**/*.ts"

GLOB target_scope INTO discovered_files

## VERIFY: Files exist

IF discovered_files.length === 0:
    APPEND {
        "claim": "Target has files",
        "status": "REFUTED",
        "evidence": "Glob returned 0 files for " + target_scope
    } TO refuted_claims
    GOTO ABORT WITH "No files found in target scope"

APPEND {
    "claim": "Target has files",
    "status": "VERIFIED",
    "evidence": {"file_count": discovered_files.length, "scope": target_scope}
} TO verified_claims

## Filter to high-risk authority domains (Claude, file ops, git, terminal, database)

DECLARE authority_domains: array
SET authority_domains = [
    "src/main/claude/**/*.ts",
    "src/main/terminal/**/*.ts",
    "**/file-*.ts",
    "**/git-*.ts",
    "**/*-db.ts",
    "**/*database*.ts"
]

SET target_files = []

FOR EACH domain IN authority_domains:
    GLOB "root/archlab-ide/" + domain INTO domain_files
    FOR EACH file IN domain_files:
        APPEND file TO target_files

## Deduplicate target_files

DECLARE unique_files: array
SET unique_files = []

FOR EACH file IN target_files:
    DECLARE is_duplicate: boolean
    SET is_duplicate = false
    FOR EACH unique_file IN unique_files:
        IF file === unique_file:
            SET is_duplicate = true
            BREAK
    IF is_duplicate !== true:
        APPEND file TO unique_files

SET target_files = unique_files

APPEND {
    "claim": "Authority domain files discovered",
    "status": "VERIFIED",
    "evidence": {"authority_files": target_files.length}
} TO verified_claims

DECLARE manifest: object
SET manifest = {
    "analysis_id": analysis_id,
    "timestamp": timestamp,
    "target_scope": target_scope,
    "total_files_discovered": discovered_files.length,
    "authority_domain_files": target_files.length,
    "law": "LAW 6: Human-Controlled Authority Boundary",
    "verified_claims": verified_claims.length,
    "refuted_claims": refuted_claims.length
}

WRITE manifest TO workspace_path + "/00-manifest.json"

VALIDATION GATE: Scope Discovery Complete
✅ target_scope defined and verified
✅ authority_domain files filtered (CHECK authority_files > 0)
✅ manifest written to workspace
✅ verified_claims tracking active

# PHASE 2: DECISION POINT ANALYSIS WITH SAFE ITERATION LOOP

## ALGORITHM: Frame-Based-Reasoning-Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → LINK<RelatedFrames>

DECLARE decision_points: array
DECLARE critical_operations: array
DECLARE approval_gates: array

SET decision_points = []
SET critical_operations = []
SET approval_gates = []

## Initialize Frame Library (decision point categories as frames)

SET frame_library = {
    "FileOperation": {
        "slots": {
            "operation": {"required": true, "values": ["delete", "write", "create", "move"]},
            "approval_gate": {"required": true, "default": null},
            "justification_log": {"required": true, "default": null},
            "veto_mechanism": {"required": true, "default": null}
        },
        "criticality": "high"
    },
    "GitOperation": {
        "slots": {
            "operation": {"required": true, "values": ["commit", "push", "revert", "reset"]},
            "approval_gate": {"required": true, "default": null},
            "justification_log": {"required": true, "default": null}
        },
        "criticality": "critical"
    },
    "DatabaseOperation": {
        "slots": {
            "operation": {"required": true, "values": ["delete", "update", "drop", "truncate"]},
            "approval_gate": {"required": true, "default": null},
            "transaction_log": {"required": true, "default": null}
        },
        "criticality": "critical"
    },
    "AIDecision": {
        "slots": {
            "decision_type": {"required": true},
            "recommendation_path": {"required": true, "default": null},
            "approval_path": {"required": true, "default": null},
            "human_veto": {"required": true, "default": null}
        },
        "criticality": "critical"
    }
}

## SAFE ITERATION LOOP for file processing

SET iteration_count = 0
SET goal_achieved = false

START ANALYZE_DECISION_POINTS

SET iteration_count = iteration_count + 1

## SAFE ITERATION CHECK

IF iteration_count > max_iterations:
    REPORT "Max iterations reached in decision point analysis"
    GOTO FINALIZE_DECISION_ANALYSIS

FOR EACH file IN target_files:
    TRY:
        READ file INTO file_content

        ## FIND<Frame> - Match operations to decision frames

        ## FileOperation frame detection
        GREP "fs\\.unlink|fs\\.rmdir|fs\\.rm|fs\\.writeFile|fs\\.rename" IN file_content INTO file_ops

        FOR EACH file_op IN file_ops:
            DECLARE decision_point: object
            SET decision_point = {
                "id": "DP-" + decision_points.length,
                "frame": "FileOperation",
                "file": file,
                "line": file_op.line,
                "operation": file_op.match,
                "slots": {
                    "operation": EXTRACT_OPERATION_TYPE(file_op.match),
                    "approval_gate": null,
                    "justification_log": null,
                    "veto_mechanism": null
                }
            }

            ## EXTRACT<Slots> - Check for approval gate

            GREP "confirm\\(|askUserQuestion\\(|getUserApproval\\(" IN file_content NEAR file_op.line WITHIN 10 INTO approval_check

            IF approval_check.length > 0:
                SET decision_point.slots.approval_gate = approval_check[0].match
            ELSE:
                SET decision_point.slots.approval_gate = "MISSING"

            ## Check for justification logging

            GREP "justification|reason|why|auditLog\\.record" IN file_content NEAR file_op.line WITHIN 10 INTO justification_check

            IF justification_check.length > 0:
                SET decision_point.slots.justification_log = justification_check[0].match
            ELSE:
                SET decision_point.slots.justification_log = "MISSING"

            ## Check for veto mechanism

            GREP "cancel|abort|veto|override|skip" IN file_content NEAR file_op.line WITHIN 10 INTO veto_check

            IF veto_check.length > 0:
                SET decision_point.slots.veto_mechanism = veto_check[0].match
            ELSE:
                SET decision_point.slots.veto_mechanism = "MISSING"

            APPEND decision_point TO decision_points

        ## GitOperation frame detection
        GREP "git push|git commit|git revert|git reset" IN file_content INTO git_ops

        FOR EACH git_op IN git_ops:
            DECLARE decision_point: object
            SET decision_point = {
                "id": "DP-" + decision_points.length,
                "frame": "GitOperation",
                "file": file,
                "line": git_op.line,
                "operation": git_op.match,
                "slots": {
                    "operation": EXTRACT_OPERATION_TYPE(git_op.match),
                    "approval_gate": null,
                    "justification_log": null
                }
            }

            ## Check for approval before git operation

            GREP "confirm\\(|askUserQuestion\\(" IN file_content NEAR git_op.line WITHIN 10 INTO git_approval_check

            IF git_approval_check.length > 0:
                SET decision_point.slots.approval_gate = git_approval_check[0].match
            ELSE:
                SET decision_point.slots.approval_gate = "MISSING"

            APPEND decision_point TO decision_points

        ## DatabaseOperation frame detection
        GREP "db\\.delete|db\\.update|db\\.drop|db\\.truncate|DELETE FROM|UPDATE.*SET|DROP TABLE|TRUNCATE" IN file_content INTO db_ops

        FOR EACH db_op IN db_ops:
            DECLARE decision_point: object
            SET decision_point = {
                "id": "DP-" + decision_points.length,
                "frame": "DatabaseOperation",
                "file": file,
                "line": db_op.line,
                "operation": db_op.match,
                "slots": {
                    "operation": EXTRACT_OPERATION_TYPE(db_op.match),
                    "approval_gate": null,
                    "transaction_log": null
                }
            }

            ## Check for approval before database operation

            GREP "confirm\\(|askUserQuestion\\(" IN file_content NEAR db_op.line WITHIN 10 INTO db_approval_check

            IF db_approval_check.length > 0:
                SET decision_point.slots.approval_gate = db_approval_check[0].match
            ELSE:
                SET decision_point.slots.approval_gate = "MISSING"

            APPEND decision_point TO decision_points

        ## AIDecision frame detection
        GREP "auto.*execute|apply.*fix|auto.*commit|auto.*deploy|remediate" IN file_content INTO ai_decisions

        FOR EACH ai_decision IN ai_decisions:
            DECLARE decision_point: object
            SET decision_point = {
                "id": "DP-" + decision_points.length,
                "frame": "AIDecision",
                "file": file,
                "line": ai_decision.line,
                "operation": ai_decision.match,
                "slots": {
                    "decision_type": ai_decision.match,
                    "recommendation_path": null,
                    "approval_path": null,
                    "human_veto": null
                }
            }

            ## Check for recommendation → approval pattern

            GREP "recommend|suggest|propose" IN file_content NEAR ai_decision.line WITHIN 20 INTO recommendation_check

            IF recommendation_check.length > 0:
                SET decision_point.slots.recommendation_path = recommendation_check[0].match

            GREP "approve|confirm|accept" IN file_content NEAR ai_decision.line WITHIN 20 INTO approval_path_check

            IF approval_path_check.length > 0:
                SET decision_point.slots.approval_path = approval_path_check[0].match
            ELSE:
                SET decision_point.slots.approval_path = "MISSING"

            APPEND decision_point TO decision_points

    CATCH read_error:
        APPEND {
            "claim": "File readable: " + file,
            "status": "REFUTED",
            "error": read_error
        } TO refuted_claims
        CONTINUE

## Check if goal achieved

IF decision_points.length > 0:
    SET goal_achieved = true

IF goal_achieved !== true AND iteration_count < max_iterations:
    GOTO ANALYZE_DECISION_POINTS

START FINALIZE_DECISION_ANALYSIS

DECLARE decision_analysis: object
SET decision_analysis = {
    "analysis_id": analysis_id,
    "total_decision_points": decision_points.length,
    "by_frame": {
        "FileOperation": decision_points.filter(dp => dp.frame === "FileOperation").length,
        "GitOperation": decision_points.filter(dp => dp.frame === "GitOperation").length,
        "DatabaseOperation": decision_points.filter(dp => dp.frame === "DatabaseOperation").length,
        "AIDecision": decision_points.filter(dp => dp.frame === "AIDecision").length
    },
    "decision_points": decision_points,
    "iterations_required": iteration_count
}

WRITE decision_analysis TO workspace_path + "/evidence/01-decision-points.json"

END

VALIDATION GATE: Decision Point Analysis Complete
✅ decision_points extracted from target_files
✅ frame-based reasoning applied (FileOperation, GitOperation, DatabaseOperation, AIDecision)
✅ slots extracted for each decision point (approval_gate, justification_log, veto_mechanism)
✅ safe iteration loop completed (CHECK iteration_count <= max_iterations)
✅ goal_achieved confirmed (CHECK decision_points.length > 0)
✅ decision_analysis written to evidence

# PHASE 3: EXECUTION PATH ANALYSIS

## ALGORITHM: Control-Flow-Graph-Analysis

## READ<Code> → CREATE<CFG> → FIND<Nodes> → ANALYZE<Edges> → EXTRACT<Paths> → FIND<Patterns>

DECLARE cfg_network: object
DECLARE execution_paths: array
DECLARE automated_paths: array
DECLARE human_approved_paths: array

SET cfg_network = {
    "nodes": {},
    "edges": {},
    "paths": []
}

SET execution_paths = []
SET automated_paths = []
SET human_approved_paths = []

## Trace execution paths for critical operations

FOR EACH decision_point IN decision_points:
    TRY:
        READ decision_point.file INTO file_content

        ## CREATE<CFG> - Build control flow graph around decision point

        DECLARE cfg: object
        SET cfg = {
            "decision_point_id": decision_point.id,
            "entry_node": "line_" + (decision_point.line - 20),
            "decision_node": "line_" + decision_point.line,
            "exit_nodes": [],
            "edges": [],
            "path_type": null
        }

        ## FIND<Nodes> - Identify control flow nodes

        GREP "if\\s*\\(|else|return|throw" IN file_content NEAR decision_point.line WITHIN 20 INTO control_nodes

        FOR EACH control_node IN control_nodes:
            APPEND {
                "from": "line_" + control_node.line,
                "to": "line_" + (control_node.line + 1),
                "type": EXTRACT_CONTROL_TYPE(control_node.match)
            } TO cfg.edges

        ## ANALYZE<Edges> - Determine if path is automated or human-approved

        IF decision_point.slots.approval_gate === "MISSING":
            SET cfg.path_type = "automated"
            APPEND cfg TO automated_paths
        ELSE:
            SET cfg.path_type = "human_approved"
            APPEND cfg TO human_approved_paths

        APPEND cfg TO execution_paths

    CATCH read_error:
        CONTINUE

DECLARE path_analysis: object
SET path_analysis = {
    "analysis_id": analysis_id,
    "total_paths": execution_paths.length,
    "automated_paths": automated_paths.length,
    "human_approved_paths": human_approved_paths.length,
    "automation_rate": (automated_paths.length / execution_paths.length) * 100,
    "paths": execution_paths
}

WRITE path_analysis TO workspace_path + "/evidence/02-execution-paths.json"

VALIDATION GATE: Execution Path Analysis Complete
✅ CFG created for each decision point
✅ execution paths categorized (automated vs human_approved)
✅ automation_rate calculated
✅ path_analysis written to evidence

# PHASE 4: LAW 6 VIOLATION DETECTION WITH SAFE ITERATION LOOP

## ALGORITHM: Production-System-Cycle

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory>

DECLARE violations: array
DECLARE authority_bypasses: array
DECLARE missing_approvals: array
DECLARE missing_justifications: array
DECLARE bypassable_authority: array

SET violations = []
SET authority_bypasses = []
SET missing_approvals = []
SET missing_justifications = []
SET bypassable_authority = []

## Initialize Working Memory (current authority state)

SET working_memory = {
    "decision_points": decision_points,
    "execution_paths": execution_paths,
    "automated_paths": automated_paths,
    "detected_violations": []
}

## Define Production Rules (LAW 6 authority anti-patterns)

SET production_rules = [
    {
        "name": "P1_auto_execution_without_approval",
        "condition": "critical_operation.approval_gate === 'MISSING'",
        "action": "APPEND authority_bypass TO violations",
        "priority": 1,
        "severity": "critical"
    },
    {
        "name": "P2_ai_final_decision",
        "condition": "ai_decision.approval_path === 'MISSING'",
        "action": "APPEND ai_authority_violation TO violations",
        "priority": 1,
        "severity": "critical"
    },
    {
        "name": "P3_missing_justification_log",
        "condition": "approval_granted AND justification_log === 'MISSING'",
        "action": "APPEND justification_violation TO violations",
        "priority": 2,
        "severity": "high"
    },
    {
        "name": "P4_bypassable_approval",
        "condition": "approval_check.type === 'optional'",
        "action": "APPEND bypassable_authority TO violations",
        "priority": 2,
        "severity": "high"
    },
    {
        "name": "P5_missing_veto_mechanism",
        "condition": "automated_process AND veto_mechanism === 'MISSING'",
        "action": "APPEND veto_violation TO violations",
        "priority": 3,
        "severity": "medium"
    }
]

## SAFE ITERATION LOOP for violation detection

SET iteration_count = 0
SET goal_achieved = false

START DETECT_VIOLATIONS

SET iteration_count = iteration_count + 1

## SAFE ITERATION CHECK

IF iteration_count > max_iterations:
    REPORT "Max iterations reached in violation detection"
    GOTO FINALIZE_VIOLATIONS

## FIND<MatchingProductions> - Evaluate conditions against working memory

FOR EACH decision_point IN decision_points:
    ## P1: Auto-execution without approval

    IF decision_point.frame IN ["FileOperation", "GitOperation", "DatabaseOperation"]:
        IF decision_point.slots.approval_gate === "MISSING":
            APPEND {
                "violation_id": "VIO-" + violations.length,
                "type": "auto_execution_without_approval",
                "severity": "critical",
                "priority": "P0",
                "decision_point_id": decision_point.id,
                "file": decision_point.file,
                "line": decision_point.line,
                "operation": decision_point.operation,
                "evidence": "Critical operation executes without human approval gate",
                "impact": "AI/system makes final decision autonomously",
                "remediation": "Add confirm() or askUserQuestion() before " + decision_point.operation
            } TO violations
            APPEND decision_point TO authority_bypasses

    ## P2: AI final decision

    IF decision_point.frame === "AIDecision":
        IF decision_point.slots.approval_path === "MISSING":
            APPEND {
                "violation_id": "VIO-" + violations.length,
                "type": "ai_final_decision",
                "severity": "critical",
                "priority": "P0",
                "decision_point_id": decision_point.id,
                "file": decision_point.file,
                "line": decision_point.line,
                "operation": decision_point.operation,
                "evidence": "AI makes final decision without human approval path",
                "impact": "Violates LAW 6 - AI has authority, not recommendation role",
                "remediation": "Convert to recommendation → human approval → execution pattern"
            } TO violations

    ## P3: Missing justification log

    IF decision_point.slots.approval_gate !== "MISSING":
        IF decision_point.slots.justification_log === "MISSING":
            APPEND {
                "violation_id": "VIO-" + violations.length,
                "type": "missing_justification_log",
                "severity": "high",
                "priority": "P1",
                "decision_point_id": decision_point.id,
                "file": decision_point.file,
                "line": decision_point.line,
                "operation": decision_point.operation,
                "evidence": "Approval exists but justification not logged",
                "impact": "No audit trail for authority decisions",
                "remediation": "Add justification logging: auditLog.record(who, what, why)"
            } TO violations
            APPEND decision_point TO missing_justifications

    ## P4: Bypassable approval (detect optional approval checks)

    IF decision_point.slots.approval_gate !== "MISSING":
        TRY:
            READ decision_point.file INTO file_content
            GREP "if.*" + decision_point.slots.approval_gate IN file_content INTO optional_check

            IF optional_check.length > 0:
                APPEND {
                    "violation_id": "VIO-" + violations.length,
                    "type": "bypassable_approval",
                    "severity": "high",
                    "priority": "P1",
                    "decision_point_id": decision_point.id,
                    "file": decision_point.file,
                    "line": decision_point.line,
                    "operation": decision_point.operation,
                    "evidence": "Approval check is optional (can be bypassed via if statement)",
                    "impact": "Authority is discipline-based, not architectural",
                    "remediation": "Make approval structural (always required before operation)"
                } TO violations
                APPEND decision_point TO bypassable_authority
        CATCH:
            CONTINUE

    ## P5: Missing veto mechanism

    IF decision_point.slots.veto_mechanism === "MISSING":
        IF decision_point.frame IN ["FileOperation", "GitOperation", "DatabaseOperation", "AIDecision"]:
            APPEND {
                "violation_id": "VIO-" + violations.length,
                "type": "missing_veto_mechanism",
                "severity": "medium",
                "priority": "P2",
                "decision_point_id": decision_point.id,
                "file": decision_point.file,
                "line": decision_point.line,
                "operation": decision_point.operation,
                "evidence": "No veto/cancel mechanism for user to abort operation",
                "impact": "Human cannot override/cancel once initiated",
                "remediation": "Add cancel/abort option in approval flow"
            } TO violations

## Check if goal achieved

IF violations.length > 0:
    SET goal_achieved = true

IF goal_achieved !== true AND iteration_count < max_iterations:
    GOTO DETECT_VIOLATIONS

START FINALIZE_VIOLATIONS

CALCULATE critical_count = violations.filter(v => v.severity === "critical").length
CALCULATE high_count = violations.filter(v => v.severity === "high").length
CALCULATE medium_count = violations.filter(v => v.severity === "medium").length

DECLARE violation_report: object
SET violation_report = {
    "analysis_id": analysis_id,
    "law": "LAW 6: Human-Controlled Authority Boundary",
    "total_violations": violations.length,
    "by_severity": {
        "critical": critical_count,
        "high": high_count,
        "medium": medium_count
    },
    "by_type": {
        "auto_execution_without_approval": authority_bypasses.length,
        "missing_justification_log": missing_justifications.length,
        "bypassable_approval": bypassable_authority.length
    },
    "violations": violations,
    "iterations_required": iteration_count
}

WRITE violation_report TO workspace_path + "/reports/LAW-6-VIOLATIONS.json"

END

VALIDATION GATE: Violation Detection Complete
✅ production_rules applied to working_memory
✅ violations detected and categorized
✅ safe iteration loop completed (CHECK iteration_count <= max_iterations)
✅ goal_achieved confirmed (CHECK violations.length >= 0)
✅ violation_report written to reports

# PHASE 5: AUTHORITY BOUNDARY MAPPING

DECLARE authority_boundaries: object
DECLARE recommendation_boundaries: array
DECLARE approval_boundaries: array
DECLARE veto_boundaries: array
DECLARE observable_boundaries: array

SET recommendation_boundaries = []
SET approval_boundaries = []
SET veto_boundaries = []
SET observable_boundaries = []

## Map AI Recommendation Boundaries (AI suggests, doesn't decide)

GREP "recommend|suggest|propose|advise" IN target_files output_mode: files_with_matches INTO recommendation_files

FOR EACH file IN recommendation_files:
    READ file INTO file_content
    GREP "recommend|suggest|propose" IN file_content INTO recommendations

    FOR EACH recommendation IN recommendations:
        ## Check if recommendation leads to auto-execution

        GREP "execute|apply|run" IN file_content NEAR recommendation.line WITHIN 5 INTO auto_execute_check

        IF auto_execute_check.length > 0:
            APPEND {
                "boundary_type": "recommendation_boundary_violation",
                "file": file,
                "line": recommendation.line,
                "evidence": "Recommendation followed by auto-execution",
                "compliant": false
            } TO recommendation_boundaries
        ELSE:
            APPEND {
                "boundary_type": "recommendation_boundary_compliant",
                "file": file,
                "line": recommendation.line,
                "evidence": "Recommendation without auto-execution",
                "compliant": true
            } TO recommendation_boundaries

## Map Human Approval Boundaries

FOR EACH decision_point IN decision_points:
    IF decision_point.slots.approval_gate !== "MISSING":
        APPEND {
            "boundary_type": "approval_boundary",
            "file": decision_point.file,
            "line": decision_point.line,
            "approval_mechanism": decision_point.slots.approval_gate,
            "operation_protected": decision_point.operation,
            "compliant": true
        } TO approval_boundaries

## Map Veto Boundaries

FOR EACH decision_point IN decision_points:
    IF decision_point.slots.veto_mechanism !== "MISSING":
        APPEND {
            "boundary_type": "veto_boundary",
            "file": decision_point.file,
            "line": decision_point.line,
            "veto_mechanism": decision_point.slots.veto_mechanism,
            "compliant": true
        } TO veto_boundaries

## Map Observable Boundaries (justification logging)

FOR EACH decision_point IN decision_points:
    IF decision_point.slots.justification_log !== "MISSING":
        APPEND {
            "boundary_type": "observable_boundary",
            "file": decision_point.file,
            "line": decision_point.line,
            "logging_mechanism": decision_point.slots.justification_log,
            "compliant": true
        } TO observable_boundaries

SET authority_boundaries = {
    "analysis_id": analysis_id,
    "recommendation_boundaries": {
        "total": recommendation_boundaries.length,
        "compliant": recommendation_boundaries.filter(rb => rb.compliant === true).length,
        "violations": recommendation_boundaries.filter(rb => rb.compliant === false).length,
        "boundaries": recommendation_boundaries
    },
    "approval_boundaries": {
        "total": approval_boundaries.length,
        "boundaries": approval_boundaries
    },
    "veto_boundaries": {
        "total": veto_boundaries.length,
        "boundaries": veto_boundaries
    },
    "observable_boundaries": {
        "total": observable_boundaries.length,
        "boundaries": observable_boundaries
    }
}

WRITE authority_boundaries TO workspace_path + "/reports/LAW-6-AUTHORITY-BOUNDARIES.json"

VALIDATION GATE: Authority Boundary Mapping Complete
✅ recommendation_boundaries mapped (AI suggest, not decide)
✅ approval_boundaries mapped (human confirms before execution)
✅ veto_boundaries mapped (human can cancel)
✅ observable_boundaries mapped (decisions logged with justification)
✅ authority_boundaries written to reports

# PHASE 6: REMEDIATION PLANNING

## ALGORITHM: Means-Ends-Analysis-Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator>

DECLARE remediation_plan: object
DECLARE migration_strategies: array

SET migration_strategies = []

## Define Goal State (LAW 6 compliant)

SET goal_state = {
    "auto_execution_violations": 0,
    "ai_authority_violations": 0,
    "all_critical_ops_require_approval": true,
    "all_approvals_have_justification": true,
    "humans_can_veto_all_automated_actions": true,
    "authority_is_architectural_not_discipline": true
}

## Define Current State (from violations)

SET current_state = {
    "auto_execution_violations": authority_bypasses.length,
    "ai_authority_violations": violations.filter(v => v.type === "ai_final_decision").length,
    "missing_approval_count": violations.filter(v => v.type === "auto_execution_without_approval").length,
    "missing_justification_count": missing_justifications.length,
    "bypassable_authority_count": bypassable_authority.length
}

## Define Operator Table (operations that reduce authority violations)

DECLARE operator_table: object
SET operator_table = {
    "ADD_APPROVAL_GATE": {
        "reduces": ["auto_execution_violations"],
        "preconditions": ["critical_operation_identified"],
        "add_list": ["approval_gate_exists", "human_in_loop"],
        "delete_list": ["auto_execution"]
    },
    "ADD_JUSTIFICATION_LOGGING": {
        "reduces": ["missing_justification_violations"],
        "preconditions": ["approval_gate_exists"],
        "add_list": ["audit_trail", "observable_decisions"],
        "delete_list": ["silent_approvals"]
    },
    "CONVERT_TO_RECOMMENDATION_PATTERN": {
        "reduces": ["ai_authority_violations"],
        "preconditions": ["ai_decision_detected"],
        "add_list": ["recommendation_path", "approval_path", "human_authority"],
        "delete_list": ["ai_final_decision"]
    },
    "MAKE_APPROVAL_STRUCTURAL": {
        "reduces": ["bypassable_authority"],
        "preconditions": ["optional_approval_detected"],
        "add_list": ["mandatory_approval", "architectural_enforcement"],
        "delete_list": ["discipline_based_authority"]
    },
    "ADD_VETO_MECHANISM": {
        "reduces": ["missing_veto_violations"],
        "preconditions": ["approval_gate_exists"],
        "add_list": ["human_veto_capability"],
        "delete_list": ["no_cancel_option"]
    }
}

## Generate migration strategies for each violation

FOR EACH violation IN violations:
    DECLARE strategy: object

    MATCH violation.type:
        CASE "auto_execution_without_approval":
            SET strategy = {
                "violation_id": violation.violation_id,
                "operator": "ADD_APPROVAL_GATE",
                "from": "Direct execution: " + violation.operation,
                "to": "await confirm('Execute " + violation.operation + "?') → execute",
                "code_change": {
                    "file": violation.file,
                    "line": violation.line,
                    "before": violation.operation,
                    "after": "const approved = await confirm('" + violation.operation + " - Confirm?');\nif (approved) {\n  " + violation.operation + "\n}"
                },
                "verification": "Grep for approval gate before operation"
            }

        CASE "ai_final_decision":
            SET strategy = {
                "violation_id": violation.violation_id,
                "operator": "CONVERT_TO_RECOMMENDATION_PATTERN",
                "from": "AI decides and executes",
                "to": "AI recommends → Human approves → System executes",
                "code_change": {
                    "file": violation.file,
                    "line": violation.line,
                    "pattern": "recommendation → approval → execution"
                },
                "verification": "Grep for approval_path in AI decision code"
            }

        CASE "missing_justification_log":
            SET strategy = {
                "violation_id": violation.violation_id,
                "operator": "ADD_JUSTIFICATION_LOGGING",
                "from": "Approval without justification",
                "to": "Approval with logged justification",
                "code_change": {
                    "file": violation.file,
                    "line": violation.line,
                    "after_approval": "auditLog.record({ who: userId, what: operation, why: justification })"
                },
                "verification": "Grep for justification logging after approval"
            }

        CASE "bypassable_approval":
            SET strategy = {
                "violation_id": violation.violation_id,
                "operator": "MAKE_APPROVAL_STRUCTURAL",
                "from": "Optional approval (if statement)",
                "to": "Mandatory approval (structural enforcement)",
                "code_change": {
                    "file": violation.file,
                    "line": violation.line,
                    "pattern": "Remove if condition, always require approval"
                },
                "verification": "Grep for removal of optional approval bypass"
            }

        CASE "missing_veto_mechanism":
            SET strategy = {
                "violation_id": violation.violation_id,
                "operator": "ADD_VETO_MECHANISM",
                "from": "No cancel option",
                "to": "Human can veto/cancel at any point",
                "code_change": {
                    "file": violation.file,
                    "line": violation.line,
                    "after_approval": "Add cancel button / veto option to approval dialog"
                },
                "verification": "Grep for veto/cancel mechanism"
            }

    APPEND strategy TO migration_strategies

SET remediation_plan = {
    "analysis_id": analysis_id,
    "goal_state": goal_state,
    "current_state": current_state,
    "operator_table": operator_table,
    "total_migrations_required": migration_strategies.length,
    "migrations_by_operator": {
        "ADD_APPROVAL_GATE": migration_strategies.filter(m => m.operator === "ADD_APPROVAL_GATE").length,
        "ADD_JUSTIFICATION_LOGGING": migration_strategies.filter(m => m.operator === "ADD_JUSTIFICATION_LOGGING").length,
        "CONVERT_TO_RECOMMENDATION_PATTERN": migration_strategies.filter(m => m.operator === "CONVERT_TO_RECOMMENDATION_PATTERN").length,
        "MAKE_APPROVAL_STRUCTURAL": migration_strategies.filter(m => m.operator === "MAKE_APPROVAL_STRUCTURAL").length,
        "ADD_VETO_MECHANISM": migration_strategies.filter(m => m.operator === "ADD_VETO_MECHANISM").length
    },
    "migration_strategies": migration_strategies
}

WRITE remediation_plan TO workspace_path + "/reports/LAW-6-REMEDIATION-PLAN.json"

VALIDATION GATE: Remediation Planning Complete
✅ goal_state defined (LAW 6 compliant)
✅ current_state extracted from violations
✅ operator_table defined (authority-restoring operators)
✅ migration_strategies created for each violation
✅ remediation_plan written to reports

# PHASE 7: FINAL REPORT GENERATION

DECLARE final_report: string
DECLARE verification_checklist: string

SET final_report = "# LAW 6 Enforcement Analysis - Final Report\n\n"
APPEND final_report: "**Analysis ID**: " + analysis_id + "\n"
APPEND final_report: "**Timestamp**: " + timestamp + "\n"
APPEND final_report: "**LAW**: LAW 6 - Human-Controlled Authority Boundary\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Executive Summary\n\n"
APPEND final_report: "**Total Violations**: " + violations.length + "\n"
APPEND final_report: "- Critical (P0): " + critical_count + " violations\n"
APPEND final_report: "- High (P1): " + high_count + " violations\n"
APPEND final_report: "- Medium (P2): " + medium_count + " violations\n\n"
APPEND final_report: "**Authority Automation Rate**: " + path_analysis.automation_rate + "%\n"
APPEND final_report: "**Compliant Approval Gates**: " + approval_boundaries.length + "\n"
APPEND final_report: "**Missing Approval Gates**: " + authority_bypasses.length + "\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Critical Violations (P0)\n\n"

FOR EACH violation IN violations:
    IF violation.priority === "P0":
        APPEND final_report: "### " + violation.violation_id + ": " + violation.type + "\n\n"
        APPEND final_report: "**File**: `" + violation.file + ":" + violation.line + "`\n"
        APPEND final_report: "**Operation**: `" + violation.operation + "`\n"
        APPEND final_report: "**Evidence**: " + violation.evidence + "\n"
        APPEND final_report: "**Impact**: " + violation.impact + "\n"
        APPEND final_report: "**Remediation**: " + violation.remediation + "\n\n"
        APPEND final_report: "---\n\n"

APPEND final_report: "## Authority Boundaries\n\n"
APPEND final_report: "- **Recommendation Boundaries**: " + authority_boundaries.recommendation_boundaries.total + " (Compliant: " + authority_boundaries.recommendation_boundaries.compliant + ")\n"
APPEND final_report: "- **Approval Boundaries**: " + authority_boundaries.approval_boundaries.total + "\n"
APPEND final_report: "- **Veto Boundaries**: " + authority_boundaries.veto_boundaries.total + "\n"
APPEND final_report: "- **Observable Boundaries**: " + authority_boundaries.observable_boundaries.total + "\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Remediation Strategy\n\n"
APPEND final_report: "**Total Migrations Required**: " + remediation_plan.total_migrations_required + "\n\n"
APPEND final_report: "**By Operator**:\n"
APPEND final_report: "- ADD_APPROVAL_GATE: " + remediation_plan.migrations_by_operator.ADD_APPROVAL_GATE + "\n"
APPEND final_report: "- ADD_JUSTIFICATION_LOGGING: " + remediation_plan.migrations_by_operator.ADD_JUSTIFICATION_LOGGING + "\n"
APPEND final_report: "- CONVERT_TO_RECOMMENDATION_PATTERN: " + remediation_plan.migrations_by_operator.CONVERT_TO_RECOMMENDATION_PATTERN + "\n"
APPEND final_report: "- MAKE_APPROVAL_STRUCTURAL: " + remediation_plan.migrations_by_operator.MAKE_APPROVAL_STRUCTURAL + "\n"
APPEND final_report: "- ADD_VETO_MECHANISM: " + remediation_plan.migrations_by_operator.ADD_VETO_MECHANISM + "\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Generated Artifacts\n\n"
APPEND final_report: "- **00-manifest.json**: Analysis metadata and scope\n"
APPEND final_report: "- **01-decision-points.json**: All decision points with frame-based analysis\n"
APPEND final_report: "- **02-execution-paths.json**: CFG analysis of automated vs human-approved paths\n"
APPEND final_report: "- **LAW-6-VIOLATIONS.json**: Detailed violation catalog\n"
APPEND final_report: "- **LAW-6-AUTHORITY-BOUNDARIES.json**: Authority boundary mapping\n"
APPEND final_report: "- **LAW-6-REMEDIATION-PLAN.json**: Migration strategies for compliance\n"
APPEND final_report: "- **LAW-6-VERIFICATION-CHECKLIST.md**: Post-fix verification steps\n"
APPEND final_report: "- **LAW-6-FINAL-REPORT.md**: This report\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Next Steps\n\n"
APPEND final_report: "1. Review LAW-6-VIOLATIONS.json for prioritized violations\n"
APPEND final_report: "2. Execute remediation strategies from LAW-6-REMEDIATION-PLAN.json\n"
APPEND final_report: "3. Verify fixes using LAW-6-VERIFICATION-CHECKLIST.md\n"
APPEND final_report: "4. Re-run law-6-enforcer agent to confirm compliance\n\n"

WRITE final_report TO workspace_path + "/reports/LAW-6-FINAL-REPORT.md"

## Generate verification checklist

SET verification_checklist = "# LAW 6 Verification Checklist\n\n"
APPEND verification_checklist: "## Post-Remediation Verification Steps\n\n"
APPEND verification_checklist: "### Critical Operations - Approval Gates\n\n"

FOR EACH violation IN violations:
    IF violation.type === "auto_execution_without_approval":
        APPEND verification_checklist: "- [ ] Verify approval gate before `" + violation.operation + "` in `" + violation.file + ":" + violation.line + "`\n"
        APPEND verification_checklist: "  - Command: `grep -n 'confirm\\|askUserQuestion' " + violation.file + "`\n\n"

APPEND verification_checklist: "### AI Decision Points - Recommendation Pattern\n\n"

FOR EACH violation IN violations:
    IF violation.type === "ai_final_decision":
        APPEND verification_checklist: "- [ ] Verify recommendation → approval pattern in `" + violation.file + ":" + violation.line + "`\n"
        APPEND verification_checklist: "  - Command: `grep -n 'recommend.*approve' " + violation.file + "`\n\n"

APPEND verification_checklist: "### Justification Logging\n\n"

FOR EACH violation IN violations:
    IF violation.type === "missing_justification_log":
        APPEND verification_checklist: "- [ ] Verify justification logging in `" + violation.file + ":" + violation.line + "`\n"
        APPEND verification_checklist: "  - Command: `grep -n 'auditLog\\|justification\\|reason' " + violation.file + "`\n\n"

APPEND verification_checklist: "### Structural Authority (Non-Bypassable)\n\n"

FOR EACH violation IN violations:
    IF violation.type === "bypassable_approval":
        APPEND verification_checklist: "- [ ] Verify approval is mandatory (not optional) in `" + violation.file + ":" + violation.line + "`\n"
        APPEND verification_checklist: "  - Command: `grep -B5 -A5 'confirm' " + violation.file + " | grep 'if'` (should return no optional if)\n\n"

APPEND verification_checklist: "### Veto Mechanisms\n\n"

FOR EACH violation IN violations:
    IF violation.type === "missing_veto_mechanism":
        APPEND verification_checklist: "- [ ] Verify veto/cancel option in `" + violation.file + ":" + violation.line + "`\n"
        APPEND verification_checklist: "  - Command: `grep -n 'cancel\\|abort\\|veto' " + violation.file + "`\n\n"

APPEND verification_checklist: "---\n\n"
APPEND verification_checklist: "## Final Verification Commands\n\n"
APPEND verification_checklist: "```bash\n"
APPEND verification_checklist: "# Count remaining auto-execution violations\n"
APPEND verification_checklist: "grep -r 'fs\\.unlink\\|fs\\.rmdir\\|git push\\|git commit\\|db\\.delete' root/archlab-ide/src | grep -v 'confirm\\|askUserQuestion' | wc -l\n\n"
APPEND verification_checklist: "# Verify all approvals have justification\n"
APPEND verification_checklist: "grep -r 'confirm\\|askUserQuestion' root/archlab-ide/src -A 5 | grep 'justification\\|reason\\|auditLog' | wc -l\n\n"
APPEND verification_checklist: "# Re-run law-6-enforcer agent\n"
APPEND verification_checklist: "# Should report 0 violations\n"
APPEND verification_checklist: "```\n"

WRITE verification_checklist TO workspace_path + "/reports/LAW-6-VERIFICATION-CHECKLIST.md"

## Generate summary for user

DECLARE user_summary: string
SET user_summary = "## LAW 6 Enforcement Analysis Complete\n\n"
APPEND user_summary: "**Analysis ID**: " + analysis_id + "\n"
APPEND user_summary: "**Workspace**: " + workspace_path + "\n\n"
APPEND user_summary: "### Violations Detected\n\n"
APPEND user_summary: "- Critical (P0): " + critical_count + "\n"
APPEND user_summary: "- High (P1): " + high_count + "\n"
APPEND user_summary: "- Medium (P2): " + medium_count + "\n\n"
APPEND user_summary: "**Total**: " + violations.length + " violations\n\n"
APPEND user_summary: "### Key Findings\n\n"
APPEND user_summary: "- **Auto-execution without approval**: " + authority_bypasses.length + " critical operations\n"
APPEND user_summary: "- **Missing justification logs**: " + missing_justifications.length + " approval gates\n"
APPEND user_summary: "- **Bypassable authority**: " + bypassable_authority.length + " optional approval checks\n\n"
APPEND user_summary: "### Next Steps\n\n"
APPEND user_summary: "1. Review: `" + workspace_path + "/reports/LAW-6-FINAL-REPORT.md`\n"
APPEND user_summary: "2. Execute: `" + workspace_path + "/reports/LAW-6-REMEDIATION-PLAN.json`\n"
APPEND user_summary: "3. Verify: `" + workspace_path + "/reports/LAW-6-VERIFICATION-CHECKLIST.md`\n"

REPORT user_summary

VALIDATION GATE: Final Report Generation Complete
✅ final_report written with executive summary
✅ verification_checklist generated with grep commands
✅ user_summary reported
✅ all artifacts written to workspace

# ABORT HANDLER

START ABORT
    DECLARE abort_reason: string
    SET abort_reason = EXTRACT_MESSAGE(ABORT)

    ## Log abort details
    TRY:
        WRITE {
            "status": "ABORTED",
            "reason": abort_reason,
            "analysis_id": analysis_id,
            "timestamp": ISO8601_NOW(),
            "partial_results": {
                "decision_points_found": decision_points.length,
                "violations_detected": violations.length,
                "verified_claims": verified_claims.length,
                "refuted_claims": refuted_claims.length
            }
        } TO workspace_path + "/ABORT-REPORT.json"
    CATCH write_error:
        REPORT "ABORT: " + abort_reason
        REPORT "Failed to write abort report: " + write_error

    ## Report to user
    REPORT "---"
    REPORT "LAW 6 Analysis ABORTED: " + abort_reason
    REPORT "Timestamp: " + ISO8601_NOW()
    REPORT "Verified claims: " + verified_claims.length
    REPORT "Refuted claims: " + refuted_claims.length
    REPORT "---"

    EXIT 1
END

# SUCCESS CRITERIA

VALIDATION GATE: All Phases Complete
✅ Phase 0: Tools calibrated and verified
✅ Phase 1: Scope discovered with authority domain filtering
✅ Phase 2: Decision points analyzed with frame-based reasoning
✅ Phase 3: Execution paths traced with CFG analysis
✅ Phase 4: LAW 6 violations detected with production system rules
✅ Phase 5: Authority boundaries mapped (recommendation, approval, veto, observable)
✅ Phase 6: Remediation plan created with means-ends analysis
✅ Phase 7: Final report and verification checklist generated
✅ All artifacts written to workspace
✅ User summary delivered

# OPERATIONAL DIRECTIVES (Inherited Skepticism)

ALWAYS calibrate tools before trusting output
ALWAYS verify tool output before trusting
ALWAYS check file exists before reading
ALWAYS validate grep results against expected patterns
ALWAYS maintain verified_claims and refuted_claims arrays
ALWAYS calculate verification_rate before finalizing
ALWAYS use bounded loops with max_iterations limit
ALWAYS check iteration_count before continuing loops
ALWAYS set goal_achieved condition for loop termination

NEVER proceed with verification_rate < 0.8
NEVER trust single tool invocation without verification
NEVER skip calibration phase
NEVER accept claims without evidence
NEVER use unbounded loops (WHILE true, WHILE 1)
NEVER exceed max_iterations without user escalation
NEVER trust user-provided paths without Glob verification
NEVER assume files exist without checking
NEVER proceed on refuted claims without acknowledgment
