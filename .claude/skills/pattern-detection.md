---
name: pattern-detection
description: Use when detecting code duplication, anti-patterns, inconsistencies, copy-paste code, structural duplication, or analyzing patterns across the codebase
allowed-tools: Grep, Glob, Read
---

# PURPOSE

Detect code patterns: duplication, anti-patterns, behavioral inconsistencies.

# PATTERN DETECTION PROTOCOL

DECLARE duplication_types: object
SET duplication_types = {
"copy_paste": "Identical or near-identical code blocks",
"structural": "Same structure, different names/values",
"conceptual": "Same concept, different implementation",
"behavioral": "Inconsistent behavior for same operation"
}

WHEN detecting_duplication: # Copy-paste duplication
GREP "this\\.logger = Logger" IN codebase output_mode: count
IF count >= 3: REPORT "copy_paste duplication detected"

    # Structural duplication
    GREP "class.*Manager" IN codebase
    FOR EACH manager: EXTRACT methods, properties
    COMPARE structures
    IF similar >= 70%: REPORT "structural duplication"

    # Behavioral inconsistency
    GREP "throw new Error" IN codebase output_mode: count INTO throw_count
    GREP "return null" IN codebase output_mode: count INTO null_count
    GREP "console\\.error" IN codebase output_mode: count INTO console_count

    CALCULATE consistency_rate = MAX(throw_count, null_count, console_count) / (throw_count + null_count + console_count)
    IF consistency_rate < 0.7: REPORT "behavioral inconsistency"

WHEN detecting_anti_patterns: # God object
FOR EACH class:
IF method_count > 15 OR property_count > 8:
REPORT "god_object" WITH severity "high"

    # Circular dependency
    BUILD dependency_graph
    DETECT cycles VIA DFS
    IF cycles found: REPORT "circular_dependency" WITH severity "critical"

    # Magic numbers
    GREP "\\b\\d+\\.\\d+\\b|\\b\\d{3,}\\b" WITHOUT const_definition
    IF found: REPORT "magic_number" WITH severity "medium"

# VALIDATION

ALWAYS quantify_occurrences
ALWAYS calculate_consistency_rates
ALWAYS prioritize_by_severity

# REFERENCES

**Authoritative**:

- `.claude/agents/pattern-distiller.md` - Pattern detection framework
- `.claude/agents/code-tracer.md` - Anti-pattern detection

**Related Skills**:

- `base-class-opportunities` - What patterns suggest base class
- `complexity-analysis` - Complexity-based anti-patterns
