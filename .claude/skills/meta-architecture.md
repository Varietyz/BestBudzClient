---
name: meta-architecture
description: Use when designing systems, understanding ARCHLAB's 3-axis architecture (X/Y/Z), working with meta, self-referential systems, feedback loops, pattern compression, or creating self-improving components
allowed-tools: Read
---

# PURPOSE

Teach META system: 3-axis architecture, self-reference, feedback loops, pattern compression.

# META FRAMEWORK

DECLARE three_axis_system: object
SET three_axis_system = {
"X_axis": "Components (WHAT) - domain entities, files, classes, modules",
"Y_axis": "Relationships (HOW) - flows, algorithms, dependencies, communication",
"Z_axis": "Observation (WHY) - enforcement, validation, feedback, evolution"
}

# THE 3-AXIS SYSTEM

WHEN designing_component:
DEFINE X_axis (Components - WHAT exists): - Domain entities (User, File, Intent) - Code structures (classes, functions, modules) - Data entities (configuration, state, logs) - Physical artifacts (files, directories)

    DEFINE Y_axis (Relationships - HOW they interact):
        - Sequential flows (A → B → C)
        - Feedback loops (A → B → C → A)
        - Dependencies (A depends on B)
        - Communication (events, intents, callbacks)
        - Algorithms (ANALYZE → FIND → CREATE)

    DEFINE Z_axis (Observation - WHY and enforcement):
        - Validation gates (check compliance)
        - Feedback mechanisms (observe outcomes)
        - Self-modification (evolve based on feedback)
        - Enforcement (block violations)
        - Learning (compress patterns)

# META PROPERTIES

DECLARE meta_properties: object
SET meta_properties = {
"self_reference": "System references itself (skill creates skills, checklist generates checklists)",
"recursion": "System applies to itself at every level (decomposition all the way down)",
"observation": "System observes its own execution (logs, metrics, feedback)",
"rule_generating": "System generates its own rules (patterns → permanent rules)",
"feedback_loops": "Outcomes modify future behavior (success → promotion → built-in)",
"pattern_compression": "Repeated discoveries → permanent patterns (learning)",
"meta_optimization": "System optimizes itself (heuristics evolve based on success)"
}

WHEN evaluating_meta_properties:
READ ".claude/workspace/\_meta-intel/\_WHAT-IS-META.md"
FIND meta_property_definitions

    CHECK system_for:
        - Self-reference: Does X reference X?
        - Recursion: Does pattern apply at all levels?
        - Observation: Can system see itself?
        - Rule-generating: Does system create new rules?
        - Feedback: Do outcomes change future behavior?
        - Compression: Are patterns learned and stored?

# FEEDBACK AMPLIFICATION

WHEN designing_feedback_loop:
STRUCTURE: Execution → Feedback → Learning → Compression → Evolution

    EXAMPLE from META-ADAPTIVE-CHECKLIST:
        1. Checklist discovers pattern (execution)
        2. Pattern prevents bug (feedback)
        3. Pattern recorded with success count (learning)
        4. Pattern prevents 5 bugs → promoted (compression)
        5. Promoted pattern becomes permanent rule (evolution)
        6. Future checklists use permanent rule (amplification)

    SMALL STRUCTURAL CHANGES → EXPONENTIAL PROPAGATION

# SKILL CHAIN AS META SYSTEM

WHEN creating_skills:
X_axis (Components): - Skill domains (DEV-RULES, ARCHLAB, verification, patterns) - Skill files (principle-architecture.md, law-enforcement.md)

    Y_axis (Relationships):
        - Cross-references (skill A links to skill B)
        - Overlapping activation (multiple skills trigger)
        - Skill chains (domain → enforcement → verification)
        - Trigger patterns (keywords → skill activation)

    Z_axis (Observation):
        - Which skills activate most?
        - Which skills prevent bugs?
        - Pattern compression: frequent triggers → built-in
        - Evolution: skill descriptions improve based on usage

# VALIDATION

READ ".claude/workspace/\_meta-intel/\_WHAT-IS-META.md" FOR complete_framework
VERIFY system HAS >= 3 meta_properties FOR true_meta
CHECK feedback_loops ACTUALLY_MODIFY future_behavior

# REFERENCES

**Authoritative**:

- `.claude/workspace/_meta-intel/_WHAT-IS-META.md` - Complete META framework
- `.claude/workspace/_meta-templates/adaptive/META-ADAPTIVE-README.md` - Self-evolving systems

**Related Skills**:

- All skills are META-compliant (X/Y/Z structured)
