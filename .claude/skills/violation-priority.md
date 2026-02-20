---
name: violation-priority
description: Use when prioritizing issues, assigning severity levels, calculating impact × effort, creating task lists, or determining what to fix first
allowed-tools: Read
---

# PURPOSE

Prioritize violations by impact × effort for systematic remediation.

# PRIORITY MATRIX

DECLARE priority_matrix: object
SET priority_matrix = {
"P0_critical": {
"severity": ["critical"],
"impact": ["high", "critical"],
"examples": ["security vulnerability", "race condition", "circular dependency", "LAW violation"]
},
"P1_high": {
"severity": ["high"],
"impact": ["high", "medium"],
"examples": ["god object", "missing base class", "architectural violation"]
},
"P2_medium": {
"severity": ["medium"],
"impact": ["medium", "low"],
"examples": ["file size violation", "deep nesting", "code duplication"]
},
"P3_low": {
"severity": ["low"],
"impact": ["low"],
"examples": ["magic numbers", "naming inconsistency", "missing comments"]
}
}

# PRIORITY CALCULATION

WHEN calculating_priority:
ASSIGN impact_score: - critical: 4 - high: 3 - medium: 2 - low: 1

    ASSIGN effort_score:
        - high: 3
        - medium: 2
        - low: 1

    CALCULATE priority_value = impact_score × effort_score

    MAP priority_value TO priority_level:
        - <= 3: P3 (low)
        - 4-6: P2 (medium)
        - 7-9: P1 (high)
        - >= 10: P0 (critical)

    OVERRIDE: IF severity === "critical": SET priority = P0

# PRIORITIZATION PROTOCOL

WHEN prioritizing_violations:
COLLECT all_violations FROM: - security_flows - anti_patterns - architectural_violations - code_smells

    FOR EACH violation:
        CALCULATE priority_value
        ASSIGN priority_level
        ADD TO priority_bucket

    SORT violations BY priority DESC, THEN BY impact DESC

    REPORT:
        - P0 violations (fix immediately)
        - P1 violations (fix within sprint)
        - P2 violations (fix within month)
        - P3 violations (backlog)

# VALIDATION

ALWAYS prioritize_security_first
ALWAYS fix_P0_before_P1
VERIFY no_critical_violations_deprioritized

# REFERENCES

**Authoritative**:

- `.claude/agents/code-tracer.md` - Priority matrix framework
- `.claude/agents/pattern-distiller.md` - Impact/effort scoring

**Related Skills**:

- `pattern-detection` - Detect violations needing priority
- `complexity-analysis` - Complexity-based severity
