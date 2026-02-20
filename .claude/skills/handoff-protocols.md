---
name: handoff-protocols
description: Use when designing agent handoffs, structuring completion signals, passing context between agents, handling failures in workflows, or coordinating multi-agent execution
allowed-tools: Read, Write
---

# PURPOSE

Teach handoff signal structure and context passing for multi-agent coordination.

# HANDOFF SIGNAL STRUCTURE

DECLARE handoff_template: object
SET handoff_template = {
"agent_completed": "agent_name",
"agent_position": N,
"phase": "phase_name",
"artifacts_location": "shared_zone/workflow_name/",
"orchestrator_action": "ACTIVATE_NEXT_AGENT | PAUSE_FOR_USER | ESCALATE_BLOCKER | PHASE_COMPLETE | WORKFLOW_COMPLETE",
"handoff_context": {
"phase_status": "complete | incomplete",
"validation_gates_passed": true | false,
"critical_files": ["src/file.ts:142"],
"key_findings": ["Finding 1", "Finding 2"],
"blockers": [],
"next_agent_focus": "Specific areas needing attention"
}
}

# ORCHESTRATOR ACTIONS

DECLARE action_types: object
SET action_types = {
"ACTIVATE_NEXT_AGENT": {
"behavior": "Execute Task tool with next agent",
"user_action": "None - automatic continuation",
"implementation": "Task(subagent_type='next-agent', prompt='context')"
},
"PAUSE_FOR_USER": {
"behavior": "Present findings, wait for approval",
"user_action": "Required - critical decision needed",
"when": "Unrecoverable blocker or decision point"
},
"ESCALATE_BLOCKER": {
"behavior": "Surface critical issue to user",
"user_action": "Required - resolve blocker",
"when": "Unrecoverable error prevents progression"
},
"PHASE_COMPLETE": {
"behavior": "Mark current phase finished",
"user_action": "None",
"when": "All 8 agents in phase completed"
},
"WORKFLOW_COMPLETE": {
"behavior": "Present final summary, end workflow",
"user_action": "None - workflow finished",
"when": "All phases complete, final summary created"
}
}

# CONTEXT PASSING

WHEN orchestrator RECEIVES handoff_signal:
IF orchestrator_action === "ACTIVATE_NEXT_AGENT":
SET next_agent = agent_sequence[current_index + 1]

        CREATE next_prompt FROM:
            - phase: current_phase
            - focus_points: next_agent.focus_points
            - previous_findings: handoff_signal.handoff_context.key_findings
            - critical_files: handoff_signal.handoff_context.critical_files

        EXECUTE Task(
            subagent_type = next_agent.type,
            prompt = next_prompt
        )

# FAILURE HANDLING

WHEN validation_gates_failed:
SET handoff_context.phase_status = "incomplete"
SET handoff_context.validation_gates_passed = false
SET handoff_context.blockers = ["Specific blocker description"]
SET handoff_context.issues_noted = ["Requires investigation in Phase N"]

    WRITE failure_details TO validation_results
    WRITE blocker TO workflow_state

    # Continue to documentation agents (positions 7, 8)
    REPORT "ACTIVATE_NEXT_AGENT" WITH validation_gates_passed = false

WHY not halt immediately:

- Documentation agents analyze failure
- Root cause traced by Exit Validator (position 7)
- Completion Verifier (position 8) preserves context
- Stopping loses context for debugging

WHEN failure_severity === "BLOCKER":
SET orchestrator_action = "PAUSE_FOR_USER"
SET user_decision_required = true
AWAIT user_decision

# BEST PRACTICES

ALWAYS include:

- artifacts_location in handoff
- critical_files with file:line references
- key_findings (5-10 items maximum)
- Specific, actionable blockers

SIGNAL completion ONLY after validation gates pass

INCLUDE enough context for next agent to start immediately

USE PAUSE_FOR_USER sparingly (only true blockers)

NEVER signal WORKFLOW_COMPLETE without final summary

# VALIDATION

READ ".pag-docs/orchestrate.md" FOR complete_signal_structure
VERIFY handoff CONTAINS all_required_fields
VERIFY critical_files HAVE line_references

# REFERENCES

**Authoritative**:

- `.pag-docs/orchestrate.md` - Handoff signals section
- `.claude/workspace/_meta-templates/meta-agentic-workflow.pag` - Handoff protocol

**Related Skills**:

- `orchestration-patterns` - Agent sequencing
- `workflow-coordination` - Phase validation
