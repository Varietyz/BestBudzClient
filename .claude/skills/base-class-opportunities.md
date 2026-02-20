---
name: base-class-opportunities
description: Use when identifying opportunities to create base classes, detecting shared patterns across classes, evaluating abstraction boundaries, or refactoring duplicated class behavior
allowed-tools: Grep, Glob, Read
---

# PURPOSE

Identify when duplicated patterns warrant base class extraction.

# BASE CLASS BOUNDARY PRINCIPLES

DECLARE boundary_principles: object
SET boundary_principles = {
"universal": "Applies to >= 70% of classes in domain",
"invariant": "Pattern must not vary across implementations",
"foundational": "Related to initialization, lifecycle, cleanup",
"enforcing": "Base class can enforce constraint compliance",
"reducing_load": "Eliminates cognitive/maintenance burden"
}

WHEN evaluating_pattern_for_extraction:
CALCULATE occurrence_rate = pattern_count / total_classes_in_domain

    CHECK boundary_principles:
        IF occurrence_rate >= 0.7:
            SET universal = true

        IF pattern MATCHES "initialization|lifecycle|cleanup|constructor|destroy":
            SET foundational = true
            SET enforcing = true

        IF pattern IS "copy_paste_duplication":
            SET invariant = true
            SET reducing_load = true

    CALCULATE principles_met = COUNT(true values)

    IF principles_met >= 3:
        RECOMMEND base_class_extraction
        DEFINE abstraction_boundary
    ELSE:
        SUGGEST alternative_refactoring

# ABSTRACTION BOUNDARY DEFINITION

WHEN creating_base_class:
DEFINE concrete_methods: - constructor (options handling) - initialize() (orchestrator with guards) - destroy() (cleanup orchestrator) - handleError() (standard error handling)

    DEFINE abstract_hooks:
        - onInitialize() (empty default, subclass override)
        - onDestroy() (empty default, subclass override)

    APPLY template_method_pattern:
        "constructor → initialize() → onInitialize() → ... → onDestroy() → destroy()"

# VALIDATION

GREP pattern IN codebase FOR occurrence_count
VERIFY occurrence_rate >= 0.7 FOR universal
VERIFY principles_met >= 3 BEFORE extraction

# REFERENCES

**Authoritative**:

- `.claude/agents/pattern-distiller.md` - Boundary principles framework
- `.claude/_ARCHLAB.md` - LAW 3: Inheritance Over Configuration

**Related Skills**:

- `pattern-detection` - Detect patterns warranting base class
- `algorithmic-protocols` - Extension-Without-Modification protocol
