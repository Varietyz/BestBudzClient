---
name: law-enforcement
description: Use when implementing features, refactoring architecture, working with base classes, registry patterns, event handling, state management, security, or any task involving the 7 Laws of ARCHLAB
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Enforce the 7 Laws of ARCHLAB architecture.

# LAW ENFORCEMENT PROTOCOL

DECLARE seven_laws: object
SET seven_laws = {
"LAW 1": ["forward-only", "add without modifying", "new file", "extension", "self-register"],
"LAW 2": ["no callbacks", "emit intent", "EventBus", "upward flow", "child-parent"],
"LAW 3": ["inheritance", "base class", "behavior from base"],
"LAW 4": ["security base", "sanitization", "CSP", "inherited capability"],
"LAW 5": ["passkey", "checkpoint", "snapshot", "state recovery", "authenticated"],
"LAW 6": ["human authority", "AI zero authority", "override", "justification"],
"LAW 7": ["registry", "single import", "Engine.registry", "resolve", "token"]
}

WHEN user CREATES new_feature:
CHECK LAW 1: "Did you modify existing file or create new?"
IF modified_existing: BLOCK WITH "LAW 1: Forward-Only Programming"
READ ".claude/\_ARCHLAB.md" LAW 1 section
SUGGEST base_class_extension OR self_registration

WHEN user IMPLEMENTS child_to_parent_communication:
CHECK LAW 2: GREP "this.parent|callback|props.on" IN code
IF found: BLOCK WITH "LAW 2: No Callbacks"
READ ".claude/\_ARCHLAB.md" LAW 2 section
SUGGEST "Engine.eventBus.emit(intent_type, data)"

WHEN user ADDS security_validation:
CHECK LAW 3/4: GLOB "\*_/base-_.ts" FOR base_security_class
IF creating_new_validation: SUGGEST "Extend base class instead"
READ ".claude/\_ARCHLAB.md" LAW 3/4 section

WHEN user IMPLEMENTS state_persistence:
CHECK LAW 5: GREP "passkey|checkpoint|snapshot" IN code
READ ".claude/\_ARCHLAB.md" LAW 5 section
REQUIRE authenticated_state_recovery

WHEN user ATTEMPTS override_invariant:
CHECK LAW 6: REQUIRE human_justification >= 10_chars
READ ".claude/\_ARCHLAB.md" LAW 6 section
ENFORCE authority_hierarchy: "KERNEL > USER > SYSTEM > AI"

WHEN user IMPORTS module:
CHECK LAW 7: GREP "import .\* from '../" IN code
IF direct_import: BLOCK WITH "LAW 7: Single Import Boundary"
READ ".claude/\_ARCHLAB.md" LAW 7 section
SUGGEST "Engine.registry.resolve('token')"

# VALIDATION

FOR EACH law IN seven_laws:
VERIFY compliance BEFORE proceeding

ALWAYS reference algorithmic-protocols skill FOR law implementation

# REFERENCES

**Authoritative**:

- `.claude/_ARCHLAB.md` - All 7 Laws definitions
- `.claude/workspace/_meta-intel/algorithms/_DEV-RULES-ALGORITHMS.md` - Law protocols

**Related Skills**:

- `algorithmic-protocols` - Implementation protocols per law
- `principle-architecture` - Domain principles supporting laws
