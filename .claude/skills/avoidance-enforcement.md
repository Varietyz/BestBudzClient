---
name: avoidance-enforcement
description: Use when refactoring code, addressing technical debt, preventing anti-patterns, or encountering backwards compatibility, fallback, legacy, deprecation, or shortcut patterns
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Enforce AVOID → ENFORCE mappings to prevent technical debt patterns.

# AVOIDANCE PROTOCOL

DECLARE forbidden_patterns: object
SET forbidden_patterns = {
"shortcuts": "debt",
"backward_compatibility": "debt",
"fallback": "debt",
"deprecation": "debt",
"legacy": "debt",
"for_now": "forgetting",
"temporary": "forgetting",
"dual_path": "confusion"
}

WHEN user WRITES code WITH avoidance_term:
READ ".claude/\_DEV-RULES.md" INTO context
FIND "AVOID → ENFORCE Mapping" table
LOOKUP term IN forbidden_patterns
REPORT consequence
SUGGEST enforce_alternative

WHEN user SAYS "backwards compatible":
BLOCK WITH "backwards_compatibility = debt"
SUGGEST "forward_compatibility = compounding"
READ \_DEV-RULES.md FOR forward_only_examples

WHEN user SAYS "fallback":
BLOCK WITH "fallback = debt"
SUGGEST "fail-fast = clarity"
REFERENCE ".claude/workspace/\_meta-intel/algorithms/\_DEV-RULES-ALGORITHMS.md" Verification-Gate-Protocol

WHEN user SAYS "for now" OR "temporary":
BLOCK WITH "deferring = forgetting"
SUGGEST "immediacy = continuity"
ENFORCE "do it right now or don't do it"

# VALIDATION

SCAN all_output FOR forbidden_patterns
IF found: REPORT violation BEFORE proceeding

# REFERENCES

**Authoritative**:

- `.claude/_DEV-RULES.md` - AVOID → ENFORCE mapping table
- `CLAUDE.md` - Forbidden superlatives list

**Related Skills**:

- `algorithmic-protocols` - Correct protocol instead of shortcut
- `principle-architecture` - Domain-specific enforcement
