---
name: orchestration-patterns
description: Use when coordinating multiple agents, designing multi-phase workflows, implementing bookend patterns (VERIFY→VALIDATE→ACTION→VERIFY), or structuring sequential agent execution with shared documents
allowed-tools: Read, Write
---

# PURPOSE

Teach multi-agent orchestration patterns for coordinating sequential work through shared documents.

# BOOKEND PATTERN (8-Agent Sequence)

DECLARE bookend_structure: string
SET bookend_structure = "VERIFY → VALIDATE → ACTION → VERIFY"

DECLARE agent_sequence: array
SET agent_sequence = [
"entry_verifier", # Position 1 (ev) - VERIFICATION + DOCS
"architecture_validator", # Position 2 (av) - VALIDATION + DOCS
"primary_implementer", # Position 3 (pi) - ACTION only
"refactor_specialist", # Position 4 (rf) - ACTION only
"compliance_agent", # Position 5 (ce) - ACTION only
"testing_agent", # Position 6 (ts) - ACTION only
"architecture_validator", # Position 7 (av) - VALIDATION + DOCS (REUSED)
"entry_verifier" # Position 8 (ev) - VERIFICATION + DOCS (REUSED)
]

PROPERTIES:

- Positions 1 & 8 share same agent type (bookend)
- Positions 2 & 7 share same agent type (bookend)
- Documentation agents: 1, 2, 7, 8 (write findings)
- Action agents: 3, 4, 5, 6 (execute tasks, no docs)

# WHEN TO USE WHICH PATTERN

DECLARE pattern_selection: object
SET pattern_selection = {
"bookend": {
"use_when": "Implementation with clear phases",
"agents": "8 per phase",
"structure": "VERIFY → VALIDATE → ACTION → VERIFY"
},
"pipeline_enrichment": {
"use_when": "Analysis building comprehensive report",
"agents": "4-6 total",
"structure": "Each agent enriches shared artifacts"
},
"investigate_action_cycle": {
"use_when": "Self-improvement, iterative refinement",
"agents": "1 type, multi-phase",
"structure": "INVESTIGATE → ACTION → INVESTIGATE → ACTION"
},
"cleanup_first": {
"use_when": "Refactoring, migration, tech debt",
"agents": "2 sequential workflows",
"structure": "CLEANUP (complete) → IMPLEMENTATION"
}
}

# SHARED DOCUMENT PROTOCOL

WHEN orchestrating_agents: # Pre-create documents BEFORE Phase 1
DECLARE core_documents: array
SET core_documents = [
"prerequisite-status.md",
"implementation-log.md",
"architecture-decisions.md",
"integration-points.md",
"validation-results.md",
"workflow-state.md"
]

    FOR EACH doc IN core_documents:
        CREATE doc WITH empty_template BEFORE agent_execution

    # Lifecycle rules
    SET lifecycle = "orchestrator-creates-agents-refine"
    ENFORCE "Agents EDIT in-place, NEVER create new versions"
    ENFORCE "READ entire document BEFORE any write"
    ENFORCE "Surgical replacement (remove outdated, add new)"

# AGENT ROLE SEPARATION

DECLARE agent_roles: object
SET agent_roles = {
"documentation_agents": {
"positions": [1, 2, 7, 8],
"responsibilities": ["Verify", "Analyze", "Document", "Write findings"],
"prohibited": ["Execute implementation tasks"]
},
"action_agents": {
"positions": [3, 4, 5, 6],
"responsibilities": ["Execute tasks", "Implement features", "Report completion"],
"prohibited": ["Write to shared documents"]
}
}

WHY separation:

- Reduces context pollution
- Clear scope per agent
- Documentation separated from action

# VALIDATION

READ ".pag-docs/orchestrate.md" FOR complete_patterns
READ ".claude/workspace/\_meta-templates/meta-agentic-workflow.pag" FOR workflow_structure
VERIFY agent_count === 8 FOR bookend_pattern
VERIFY positions_1_8_same_type AND positions_2_7_same_type

# REFERENCES

**Authoritative**:

- `.pag-docs/orchestrate.md` - Complete orchestration fundamentals
- `.claude/workspace/_meta-templates/meta-agentic-workflow.pag` - Workflow template

**Related Skills**:

- `handoff-protocols` - How agents pass context
- `workflow-coordination` - Phase sequencing and validation gates
- `checklist-generation` - Task breakdown for workflows
