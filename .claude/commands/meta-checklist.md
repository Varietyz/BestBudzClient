---
description: Generate ARCHLAB-compliant checklist with hierarchical numbering, 3D graphs, and ripple chains
argument-hint: [task description]
allowed-tools: Glob, Grep, Read, Write, Bash(npm run build:*), Bash(npm run verify-codebase:*)
model: claude-sonnet-4-5-20250929
---

# meta-checklist

Generate a deterministic, ARCHLAB-compliant implementation checklist with:

- Hierarchical N→N.N→N.N.N numbering
- Per-phase 3D dependency graphs
- Full ripple chains (registry, pools, entities, database, dashboard)
- Zero anti-pattern violations
- Skeptical context discovery

## Usage

```
/meta-checklist [task description]
```

## What It Does

1. **PREMANDATE**: Reads authoritative sources (\_DEV-RULES.md, \_ARCHLAB.md)
2. **Context Discovery**: Dynamically generates patterns from task keywords to discover:
    - Base classes to extend (prevents duplicate implementations)
    - Related implementations to review
    - Registry patterns to follow
    - Migrations to reference
3. **Algorithm Selection**: Matches task to 10 algorithm protocols
4. **Phase Decomposition**: Generates phases with 3D dependency graphs
5. **Task Generation**: Creates atomic tasks with validation commands
6. **Ripple Analysis**: Tracks impact across 5 dimensions
7. **Output**: Markdown checklist with sprint grouping (CRITICAL→HIGH→MEDIUM→LOW)

## Examples

**Implement new tracking system:**

```
/meta-checklist Implement IntentFlowTracker to capture user intents from UI interactions
```

**Add database feature:**

```
/meta-checklist Add migration system for DatabaseService schema evolution with rollback support
```

**Create UI component:**

```
/meta-checklist Create PoolMetricsPanel component showing real-time pool statistics in debug dashboard
```

## Generated Checklist Structure

```markdown
# [Task Description]

## Context Acquisition (Pre-Discovery)

- Keywords extracted: [nouns, verbs, file references]
- Base classes discovered: [base-*.ts files]
- Related implementations: [existing similar code]
- Registry patterns: [token resolution patterns]

---

# SPRINT: CRITICAL

## PHASE 1: [VERB]<goal>

- Loop Class: [Perceptual|Cognitive|Executive|...]
- Algorithm: [protocol name]
- Validation: `npm run build` or verification command

**Dependencies (3D Graph)**:

- Sequential (Z): Phase dependencies
- Lateral (X): Parallel phases
- Diagonal (Y): Data exchanges

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | token | dependents |
| pools | pool | metrics |
| entities | type | UIStore |
| database | table | migrations |
| dashboard | metric | logger |

### Task 1.1: [Specific task]

- [ ] 1.1.1. Subtask with validation
- [ ] 1.1.2. Subtask with validation

**Phase Gate**: `npm run build` (BLOCKING)

---

# FINAL GATE

- [ ] Zero avoidance anti-patterns
- [ ] All 7 Laws satisfied
- [ ] DOM.createElement with entity
- [ ] Engine.logger (no console)
- [ ] CSS tokens (no px/hex)
- [ ] Registry pattern (no direct imports)
```

## Template Reference

Based on: `.claude/workspace/_meta-templates/META-CHECKLIST.md`

## Enforcement

The checklist generator:

- **BLOCKS** on avoidance terms (shortcuts, fallback, legacy, deprecation, etc.)
- **BLOCKS** on marketing superlatives (revolutionary, innovative, etc.)
- **DISCOVERS** existing patterns before generating tasks
- **ENFORCES** codebase patterns (DOM factory, Engine.logger, CSS tokens, registry)
- **VALIDATES** against 7 Laws and DEV-RULES.md principles

## After Generation

1. Execute tasks sequentially by phase
2. Complete phase gates before moving to next phase
3. Run `npm run verify-codebase` after code changes
4. Run `npm run build` as final blocking gate

## Task Description

$ARGUMENTS
