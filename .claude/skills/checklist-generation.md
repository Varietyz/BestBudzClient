---
name: checklist-generation
description: Use when breaking down complex tasks, creating implementation plans, generating hierarchical checklists, or structuring multi-phase work with dependencies and ripple analysis
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Generate ARCHLAB-compliant checklists with hierarchical numbering, 3D graphs, ripple chains.

# WHEN TO USE CHECKLISTS

USE checklists FOR:

- Multi-phase implementations (3+ phases)
- Tasks with complex dependencies
- Work requiring ripple analysis across systems
- Implementation plans needing validation gates

SKIP checklists FOR:

- Single-phase tasks
- Simple modifications
- One-file changes

# SKEPTICAL CONTEXT ACQUISITION (FIRST)

BEFORE generating tasks:
EXTRACT domain_keywords FROM task_description: - Technical nouns (PascalCase, camelCase) - Action verbs (CREATE, IMPLEMENT, ADD) - File references (\*.ts, folders)

    GENERATE discovery_patterns FROM keywords:
        - Base classes: "**/base-*.ts", "**/abstract-*.ts"
        - Domain files: "**/*{noun}*.ts"
        - Class patterns: "class.*{PascalCase(noun)}"

    DISCOVER context BEFORE planning:
        GLOB generated_patterns
        GREP class/interface patterns
        READ target files IF they exist

    RESULT: Prevent redefinition, missed base classes, duplicate work

# HIERARCHICAL NUMBERING STRUCTURE

DECLARE numbering_schema: object
SET numbering_schema = {
"phase": "N",
"task": "N.N",
"subtask": "N.N.N"
}

STRUCTURE:

```
PHASE 1: ANALYZE<Requirements>
  Task 1.1: Extract domain model
    - [ ] 1.1.1. Read architectural docs
    - [ ] 1.1.2. Identify entities
  Task 1.2: Map dependencies
    - [ ] 1.2.1. Build dependency graph

PHASE 2: CREATE<Implementation>
  Task 2.1: Generate base class
    - [ ] 2.1.1. Define abstract methods
```

# 3D GRAPH DEPENDENCIES

DECLARE graph_dimensions: object
SET graph_dimensions = {
"sequential_Z": "Phases that must complete before this phase (depth)",
"lateral_X": "Phases at same depth that can run in parallel (breadth)",
"diagonal_Y": "Phases with data dependencies (cross-cutting)"
}

EXAMPLE per-phase graph:

```
**Dependencies (3D Graph)**:
- **Sequential (Z)**: Phase 1 → Phase 2 → Phase 3
- **Lateral (X)**: Phase 4 ↔ Phase 5 (parallel)
- **Diagonal (Y)**: Phase 6 uses data from Phase 2
```

# RIPPLE CHAIN ANALYSIS

DECLARE ripple_dimensions: array
SET ripple_dimensions = ["registry", "pools", "entities", "database", "dashboard", "validators"]

ANALYZE ripple impact:
FOR EACH dimension:
IF task affects dimension:
EXTRACT entity_name (token, pool, entity_type, table, metric, pattern)
EXTRACT downstream_impact
DOCUMENT ripple

EXAMPLE ripple chain:

```
| Dimension  | Impact              | Downstream                           |
|------------|---------------------|--------------------------------------|
| registry   | `file-tree-token`   | dependents resolve token             |
| pools      | `FileTreePanel`     | metrics → lifecycle → UIStore        |
| entities   | `panel`             | UIStore → dashboard → state          |
| database   | `ui_entities`       | migrations → integrity               |
| dashboard  | `panel_created`     | Engine.logger → query API            |
| validators | `DOM-createElement` | detector → auto-register → vite      |
```

# SPRINT PRIORITIZATION

DECLARE sprint_priorities: object
SET sprint_priorities = {
"CRITICAL": {laws: [5, 6], triggers: ["security", "authority", "state"]},
"HIGH": {laws: [1, 7], triggers: ["foundation", "registry", "base class"]},
"MEDIUM": {laws: [2, 3, 4], triggers: ["event", "inheritance", "UI"]},
"LOW": {laws: [], triggers: ["polish", "documentation", "metrics"]}
}

GROUP phases BY sprint_priority
ORDER BY: CRITICAL → HIGH → MEDIUM → LOW

# ALGORITHM MAPPING

WHEN generating phases:
READ ".claude/workspace/\_meta-intel/algorithms/\_DEV-RULES-ALGORITHMS.md"
FIND protocol matching task triggers

    MAP task TO algorithm:
        - "150+ lines" → complexity-boundary protocol
        - "new feature" → extension-without-modification protocol
        - "registry" → registry-resolution protocol
        - "event" → intention-emission protocol

    EXTRACT verb_chain FROM protocol
    CREATE phase PER verb IN chain

# OUTPUT FORMAT SCHEMA

STRUCTURE checklist: 1. Context Acquisition section (discoveries) 2. Refuted Claims (if any) 3. Sprint-grouped phases (CRITICAL → LOW) 4. Per-phase structure: - Phase N: VERB<Goal> - Loop class (Construction/Perceptual/Cognitive/Executive) - Algorithm + Laws - 3D Graph (Z/X/Y dependencies) - Ripple Chain table (6 dimensions) - Hierarchical tasks (N.N.N) - Phase gate validation 5. Appendix A: File organization 6. Appendix B: Registry tokens 7. Final gate (BLOCKING vite validation)

# VALIDATION GATES

ENFORCE at each level: - Avoidance ontology (no "for now", "fallback", etc.) - Codebase patterns (DOM.createElement, Engine.logger schema) - ARCHLAB compliance (7 Laws, naming, limits)

FINAL GATE (BLOCKING):

```bash
npm run build && npm run verify-codebase
```

# USAGE PROTOCOL

WHEN user REQUESTS checklist: 1. READ ".claude/workspace/\_meta-templates/META-CHECKLIST.md" 2. EXECUTE PREMANDATE: Load authoritative sources 3. EXECUTE PREMANDATE 2: Skeptical context acquisition 4. FOLLOW phases 1-10 in template 5. OUTPUT hierarchical markdown with all sections

# VALIDATION

VERIFY checklist HAS:
✅ Hierarchical numbering (Phase N → Task N.N → Subtask N.N.N)
✅ Per-phase 3D graphs with phase references
✅ Full ripple chains (entity names, not counts)
✅ Sprint grouping (CRITICAL → HIGH → MEDIUM → LOW)
✅ Context acquisition section showing discoveries
✅ Zero avoidance violations
✅ Final blocking gate

# REFERENCES

**Authoritative**:

- `.claude/workspace/_meta-templates/META-CHECKLIST.md` - Full template (execute this)
- `.claude/workspace/_meta-intel/algorithms/_DEV-RULES-ALGORITHMS.md` - Algorithm protocols

**Related Skills**:

- `algorithmic-protocols` - Which protocol to use
- `meta-architecture` - 3-axis system
- `skeptical-verification` - Context acquisition
