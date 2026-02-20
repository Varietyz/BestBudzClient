---
name: workflow-coordination
description: Use when sequencing workflow phases, implementing validation gates, managing workflow state, handling phase prerequisites, or coordinating multi-phase implementations
allowed-tools: Read, Write, Bash
---

# PURPOSE

Teach phase sequencing, validation gates, and state tracking for multi-phase workflows.

# PHASE STRUCTURE

DECLARE phase_definition: object
SET phase_definition = {
"name": "phase_name",
"agent_count": 8, // Full bookend sequence per phase
"prerequisites": ["Phase N-1 complete"],
"success_criteria": ["Criteria 1", "Criteria 2"],
"workflow_file": "phase-N-workflow.pag",
"artifact_path": "shared_zone/phase-N-artifacts"
}

# VALIDATION GATES

DECLARE gate_structure: object
SET gate_structure = {
"gate_type": "VALIDATION GATE",
"conditions": [
"✅ Condition 1 verified",
"✅ Condition 2 verified",
"✅ Condition 3 verified"
],
"on_fail": "BLOCK progression, REPORT failures"
}

WHEN phase_begins:
VALIDATION GATE:
✅ phase.prerequisites ALL SATISFIED
✅ Required artifacts FROM previous_phase EXIST
✅ Workflow state INDICATES ready

    IF FAIL:
        BLOCK phase_execution
        REPORT unmet_prerequisites

WHEN phase_ends:
VALIDATION GATE:
✅ All 8 agents completed
✅ Success criteria met
✅ Handoff context created
✅ Workflow state updated

    IF FAIL:
        MARK phase AS incomplete
        CREATE failure_handoff

# WORKFLOW STATE TRACKING

DECLARE workflow_state_file: string
SET workflow_state_file = "workflow-state.md"

STRUCTURE workflow_state: - current_phase: phase_id - completed_phases: [phase_ids] - current_agent: agent_position - blockers: [blocker_descriptions] - key_findings: [findings_with_sources]

WHEN orchestrator_starts:
READ workflow_state_file
EXTRACT completed_phases
EXTRACT current_phase

    FOR EACH phase IN phases:
        IF phase.id NOT IN completed_phases:
            VERIFY phase.prerequisites IN completed_phases
            EXECUTE phase_workflow

WHEN phase_completes:
UPDATE workflow_state:
APPEND phase.id TO completed_phases
SET current_phase = next_phase.id
CLEAR blockers IF resolved

    WRITE workflow_state_file

# RESUMPTION CAPABILITIES

WHEN workflow_interrupted:
READ workflow_state_file
IDENTIFY last_completed_phase
IDENTIFY current_agent_position

    SKIP completed_phases
    RESUME FROM current_phase AT current_agent

ENABLES:

- Resume from any phase checkpoint
- Skip already-completed work
- Preserve context across interruptions
- Modular execution

# STATE MACHINES

WHEN workflow_uses_state_machine:
DECLARE states: array
SET states = ["pending", "approved", "rejected", "deployed"]

    DECLARE transitions: object
    SET transitions = {
        "pending → approved": {trigger: "approval_received"},
        "pending → rejected": {trigger: "rejection_received"},
        "approved → deployed": {trigger: "deployment_complete"}
    }

    VALIDATE transitions:
        ONLY allowed_transitions permitted
        Invalid transitions BLOCKED

# DAG EXECUTION

WHEN workflow_has_parallel_tasks:
DECLARE task_graph: object
SET task_graph = {
"compile": {depends_on: []},
"test": {depends_on: ["compile"]},
"lint": {depends_on: ["compile"]},
"deploy": {depends_on: ["test", "lint"]}
}

    IDENTIFY parallel_tasks:
        IF tasks SHARE_NO dependencies:
            EXECUTE in_parallel

    EXAMPLE:
        compile → (test ‖ lint) → deploy
        // test and lint run in parallel after compile

# ALWAYS / NEVER RULES

ALWAYS verify_prerequisites BEFORE phase_start
ALWAYS update_workflow_state AFTER phase_completion
ALWAYS preserve_state ACROSS context_switches
ALWAYS enforce_validation_gates

NEVER skip_prerequisites
NEVER proceed_without_gate_passing
NEVER lose_context ON interruption
NEVER allow_invalid_state_transitions

# VALIDATION

READ ".pag-docs/orchestrate.md" FOR phase_patterns
VERIFY all_phases HAVE prerequisites
VERIFY all_phases HAVE success_criteria
VERIFY workflow_state PERSISTS correctly

# REFERENCES

**Authoritative**:

- `.pag-docs/orchestrate.md` - Phase execution patterns
- `.claude/workspace/_meta-templates/meta-agentic-workflow.pag` - Workflow coordination

**Related Skills**:

- `orchestration-patterns` - Agent sequencing within phases
- `handoff-protocols` - Agent completion signals
- `checklist-generation` - Phase task breakdown
