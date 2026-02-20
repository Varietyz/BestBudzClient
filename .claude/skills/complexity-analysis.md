---
name: complexity-analysis
description: Use when measuring cyclomatic complexity, analyzing nesting depth, evaluating cognitive load, detecting high complexity functions, or applying complexity-boundary protocol
allowed-tools: Grep, Read, Glob
---

# PURPOSE

Measure and reduce code complexity via metrics and boundaries.

# COMPLEXITY METRICS

DECLARE complexity_thresholds: object
SET complexity_thresholds = {
"cyclomatic_complexity": 10, // max decision points
"nesting_depth": 3, // max indentation levels
"file_lines": 150, // max lines per file
"folder_files": 6, // max files per folder
"method_count": 15 // max methods per class
}

WHEN measuring_cyclomatic_complexity:
GREP "if |switch |while |for |try |catch " IN function_body
CALCULATE complexity = 1 + decision_nodes.length

    IF complexity > 10:
        REPORT "high_complexity" WITH severity "high"
        SUGGEST complexity_boundary_protocol

WHEN measuring_nesting_depth:
FOR EACH line IN function_body:
CALCULATE indentation = line.match(/^\s+/).length / 4
TRACK max_nesting

    IF max_nesting > 3:
        REPORT "deep_nesting" WITH severity "medium"
        SUGGEST "flatten with early returns, extract functions"

WHEN measuring_file_complexity:
COUNT lines IN file
IF lines > 150:
REPORT "file_size_violation" WITH severity "medium"
APPLY module_separation_protocol

WHEN measuring_class_complexity:
COUNT methods IN class
COUNT properties IN class

    IF method_count > 15 OR property_count > 8:
        REPORT "god_object" WITH severity "high"
        APPLY complexity_boundary_protocol

# COMPLEXITY REDUCTION PROTOCOL

WHEN reducing_complexity:
READ ".claude/workspace/\_meta-intel/algorithms/\_DEV-RULES-ALGORITHMS.md"
FIND Complexity-Boundary-Protocol

    EXECUTE: FIND → ANALYZE → EXTRACT → CREATE → ITERATE
        1. FIND complexity violation
        2. ANALYZE causes (tight coupling, mixed concerns)
        3. EXTRACT separable parts
        4. CREATE new modules/classes
        5. ITERATE until < threshold

# VALIDATION

VERIFY complexity <= threshold AFTER refactoring
RE-MEASURE all metrics POST-split

# REFERENCES

**Authoritative**:

- `.claude/workspace/_meta-intel/algorithms/_DEV-RULES-ALGORITHMS.md` - Complexity-Boundary-Protocol
- `.claude/_ARCHLAB.md` - Structure Limits section

**Related Skills**:

- `archlab-structure` - 150 line/6 file enforcement
- `algorithmic-protocols` - Module-separation, complexity-boundary
