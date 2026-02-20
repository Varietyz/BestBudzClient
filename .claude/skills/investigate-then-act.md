---
name: investigate-then-act
description: Use when analyzing code, investigating issues, debugging problems, planning refactors, or any task requiring understanding before modification - enforces clean separation between discovery and action phases
allowed-tools: Read, Grep, Glob, Bash
---

# PURPOSE

Enforce INVESTIGATE → ACTION alternating phases with clean separation.

# PHASE SEPARATION PROTOCOL

DECLARE current_phase: string
SET current_phase = "INVESTIGATE"

WHEN in_INVESTIGATE_phase:
ALLOW: - READ files - GREP patterns - GLOB searches - BASH for testing/checking - Gap discovery - Documentation - Testing claims

    FORBID:
        - Edit files
        - Write files
        - Fix gaps
        - Modify code

    OUTPUT: investigation_report WITH findings

WHEN investigation_complete:
SWITCH TO "ACTION" phase

WHEN in_ACTION_phase:
ALLOW: - Edit files - Write files - Fix documented gaps - Apply changes

    FORBID:
        - New gap discovery
        - Further investigation
        - Changing scope

    OUTPUT: action_log WITH changes

# VALIDATION

VERIFY phase_type BEFORE tool_use
IF investigating: NO modifications
IF acting: NO new discoveries
NEVER mix phases

# REFERENCES

**Authoritative**:

- `.claude/agents/forensic-context-verifier.md` - INVESTIGATE → ACTION pattern

**Related Skills**:

- `skeptical-verification` - What to investigate
- `error-remediation` - How to handle issues found
