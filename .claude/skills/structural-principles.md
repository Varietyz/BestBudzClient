---
name: structural-principles
description: Use when applying SOLID principles, SOC (Separation of Concerns), DRY (Don't Repeat Yourself), KISS (Keep It Simple), or understanding architectural design patterns in software development
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Guide application of structural principles (SOLID, SOC, DRY, KISS) with ARCHLAB context.

# PRINCIPLE GUIDANCE

DECLARE principle_triggers: object
SET principle_triggers = {
"SOC": ["whole system", "concerns", "module", "responsibility"],
"KISS": ["symbolic compression", "simple", "complexity"],
"DRY": ["structure sharing", "duplication", "repeat"],
"SRP": ["constraint-first", "single responsibility", "change reason"],
"OCP": ["minimal interfaces", "extend", "modify"],
"LSP": ["confluence", "substitution", "inheritance"],
"ISP": ["semantic addressing", "interface segregation"],
"DIP": ["homoiconicity", "depend on abstractions"]
}

WHEN user ASKS "apply principle":
IDENTIFY principle_name
READ ".claude/\_DEV-RULES.md" INTO context
FIND principle_definition
EXTRACT directive
EXTRACT archlab_mapping
REPORT how_to_apply WITH law_references

WHEN user VIOLATES principle:
GREP codebase FOR similar_violations
READ \_DEV-RULES.md FOR principle
REPORT violation_type
SUGGEST protocol FROM \_DEV-RULES-ALGORITHMS.md

WHEN user ASKS "principle conflict":
READ \_DEV-RULES.md tension_resolutions
CLASSIFY BY domain (see principle-architecture skill)
REPORT resolution_strategy

# VALIDATION

VERIFY principle_understood FROM \_DEV-RULES.md BEFORE applying
CROSS-CHECK WITH algorithmic-protocols skill FOR implementation

# REFERENCES

**Authoritative**:

- `.claude/_DEV-RULES.md` - All principle definitions
- `.claude/_ARCHLAB.md` - Law mappings

**Related Skills**:

- `principle-architecture` - Domain-specific principle application
- `algorithmic-protocols` - Protocols that enforce principles
- `avoidance-enforcement` - What NOT to do
