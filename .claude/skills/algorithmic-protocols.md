---
name: algorithmic-protocols
description: Use when implementing module separation, extension patterns, dependency inversion, state recovery, event handling, security, registry resolution, or any refactoring that maps to the 10 algorithmic protocols
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Map software development tasks to the 10 algorithmic protocols from \_DEV-RULES-ALGORITHMS.md.

# PROTOCOL MAPPING

DECLARE protocols: object
SET protocols = {
"module-separation": ["150+ lines", "7+ files", "mixed concerns", "split"],
"extension-without-modification": ["new feature", "add capability", "extend", "LAW 1"],
"dependency-inversion": ["direct imports", "tight coupling", "registry", "LAW 7"],
"state-snapshot-recovery": ["checkpoint", "recovery", "passkey", "LAW 5"],
"intention-emission": ["child-parent", "event", "emit", "LAW 2"],
"security-inheritance": ["security", "validation", "sanitization", "LAW 3/4"],
"human-authority": ["override", "escalation", "LAW 6"],
"complexity-boundary": ["cognitive overload", "decompose", "150 lines"],
"registry-resolution": ["dependency", "resolve", "LAW 7"],
"verification-gate": ["validation", "gate", "verify", "compliance"]
}

WHEN user TASK detected:
FOR EACH protocol IN protocols:
FOR EACH trigger IN protocol.triggers:
IF task CONTAINS trigger:
READ ".claude/workspace/\_meta-intel/algorithms/\_DEV-RULES-ALGORITHMS.md"
FIND protocol_name section
EXTRACT verb_chain
EXTRACT semantic_guarantees
EXTRACT mapped_principles
REPORT protocol WITH laws

WHEN user ASKS "how to refactor":
ANALYZE task FOR triggers
SELECT matching_protocols
READ protocol_definitions
REPORT verb_chain (e.g., "ANALYZE → FIND → EXTRACT → CREATE → VERIFY")
REFERENCE usage_guide_decision_tree

WHEN user NEEDS "compose protocols":
READ \_DEV-RULES-ALGORITHMS.md integration_example
SHOW protocol_chaining
EXAMPLE: "Complexity-Boundary → Module-Separation → Dependency-Inversion → Registry-Resolution → Verification-Gate"

# VALIDATION

VERIFY protocol_selected BEFORE applying
ALWAYS append "verification-gate" TO protocol_chain

# REFERENCES

**Authoritative**:

- `.claude/workspace/_meta-intel/algorithms/_DEV-RULES-ALGORITHMS.md` - All 10 protocols
- `.claude/_ARCHLAB.md` - 7 Laws cross-reference

**Related Skills**:

- `principle-architecture` - Which principles each protocol enforces
- `avoidance-enforcement` - What patterns protocols replace
