---
name: error-remediation
description: Use when encountering tool errors, file modification conflicts, operation failures, or needing fallback strategies for failed operations
allowed-tools: Read, Write, Edit, Bash
---

# PURPOSE

Handle errors with remediation protocols, not silent failures or shortcuts.

# ERROR HANDLING PROTOCOL

DECLARE error_remediation: object
SET error_remediation = {
"file_unexpectedly_modified": [
"READ file again to get current state",
"MERGE required changes with current content",
"CREATE NEW file using Write (NOT Edit)",
"VERIFY write successful"
],
"tool_execution_failed": [
"LOG error details",
"CHECK environment dependencies",
"TRY alternative tool",
"REPORT if irrecoverable"
],
"permission_denied": [
"CHECK write permissions",
"VERIFY directory exists",
"CREATE parent directories if needed",
"ESCALATE if still failing"
]
}

WHEN file_modification_error:
LOG "File unexpectedly modified - switching to Write tool"
READ target_file INTO current_content
MERGE required_changes WITH current_content INTO new_content
WRITE target_file WITH new_content
VERIFY write_successful

WHEN bash_command_fails:
CAPTURE exit_code
CAPTURE stderr
ANALYZE error_type
IF recoverable: APPLY remediation
IF not_recoverable: REPORT WITH context

WHEN operation_fails:
DO NOT ignore
DO NOT retry blindly
ANALYZE root_cause
APPLY appropriate_remediation
DOCUMENT what_happened

# VALIDATION

NEVER silent_failure
NEVER ignore_errors
ALWAYS log_error_details
ALWAYS attempt_remediation
IF remediation_fails: ESCALATE

# REFERENCES

**Authoritative**:

- `.claude/agents/forensic-context-verifier.md` - File modification error handling

**Related Skills**:

- `investigate-then-act` - Investigate error before fixing
- `skeptical-verification` - Verify fix worked
