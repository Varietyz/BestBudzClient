---
name: skeptical-verification
description: Use when verifying claims, validating assumptions, testing edge cases, checking behavioral correctness, or establishing trust anchors for any investigation or analysis
allowed-tools: Read, Grep, Glob, Bash
---

# PURPOSE

Apply skeptical verification: test claims against actual code, establish trust anchors, detect false positives/negatives.

# SKEPTICAL PROTOCOL

DECLARE trust_anchor: object
SET trust_anchor = {
"minimal_assumptions": [
"Bash executes commands",
"File system I/O works",
"Grep/Glob produce valid output",
"Read/Write access file system correctly"
],
"rationale": "Verification requires trusting minimal capabilities",
"verification_limit": "Cannot verify verifier without external reference"
}

WHEN user CLAIMS something:
TREAT as_hypothesis NOT fact
READ actual_code
GREP claimed_pattern
IF no_match: REFUTE claim WITH evidence
IF match: VERIFY not_false_positive

WHEN testing_verification_tool:
CREATE known_good_case
CREATE known_bad_case
TEST tool_on_both
IF tool_fails_either: WARN "Tool unreliable"

WHEN detecting_pattern:
CHECK false_positive_scenarios: - Comments containing pattern - String literals with pattern - Disabled code blocks

    CHECK false_negative_scenarios:
        - Unicode homoglyphs
        - Whitespace variations
        - Formatted differently

WHEN establishing_trust:
DEFINE minimal_assumptions_explicitly
DOCUMENT verification_boundaries
ACKNOWLEDGE limits

# VALIDATION

ALWAYS test_against_actual_code NOT documentation
ALWAYS check_false_positives
ALWAYS check_false_negatives
NEVER trust_prior_knowledge WITHOUT verification

# REFERENCES

**Authoritative**:

- `.claude/agents/forensic-context-verifier.md` - Trust anchor, behavioral/adversarial testing
- `.claude/GATES.md` - Verification gates protocol

**Related Skills**:

- `investigate-then-act` - When to verify (INVESTIGATE phase)
