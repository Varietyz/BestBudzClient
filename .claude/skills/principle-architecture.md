---
name: principle-architecture
description: Use when classifying code by architectural domain (COMPUTATION/RESOURCE/EXECUTION), determining stateless vs stateful design, or resolving principle conflicts in software development
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Teach domain classification (COMPUTATION/RESOURCE/EXECUTION) and which principles apply to each domain.

# INVESTIGATION PROTOCOL

DECLARE investigation_steps: array
SET investigation_steps = [
"READ .claude/_DEV-RULES.md INTO context",
"FIND 'Principle Domains' section",
"EXTRACT domain_definitions",
"EXTRACT classification_algorithm",
"EXTRACT principle_mappings"
]

WHEN user ASKS "classify this code":
READ code FROM context
GREP "fs\.|db\.|fetch\(|http\." IN code
IF found: CLASSIFY AS "RESOURCE" WITH reason "I/O detected"

    GREP "addEventListener|\.on\(|\.emit\(|Intent" IN code
    IF found: CLASSIFY AS "EXECUTION" WITH reason "Event handling"

    IF no_matches: CLASSIFY AS "COMPUTATION" WITH reason "Pure function"

    READ ".claude/_DEV-RULES.md" section FOR domain
    REPORT applicable_principles

WHEN user ASKS "which principles apply":
CLASSIFY code FIRST
READ principle_mappings FROM \_DEV-RULES.md
IF COMPUTATION: APPLY [Immutability, Idempotency, Stateless]
IF RESOURCE: APPLY [Single-Owner, Lifecycle, Fail-Fast]
IF EXECUTION: APPLY [No-Callbacks, Event-Driven, Monotonic-Growth]

WHEN user ENCOUNTERS "principle conflict":
IDENTIFY conflicting_principles
READ ".claude/\_DEV-RULES.md" tension_resolutions
CLASSIFY each_principle BY domain
REPORT resolution

# VALIDATION

VERIFY domain_classified BEFORE applying_principles
VERIFY authoritative_source_read BEFORE answering

# REFERENCES

**Authoritative**:

- `.claude/_DEV-RULES.md` - Principle architecture framework
- `.claude/workspace/_meta-intel/algorithms/_DEV-RULES-ALGORITHMS.md` - Protocol mappings

**Related Skills**:

- `domain-classification` - Detailed logging classification
- `algorithmic-protocols` - When to use which protocol
- `structural-principles` - SOLID/SOC/DRY/KISS deep dive
