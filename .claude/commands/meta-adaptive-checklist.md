---
description: Generate self-evolving ARCHLAB checklist that learns from execution outcomes (100% META)
argument-hint: [task description] | FEEDBACK [checklist-id] | EVOLVE
allowed-tools: Glob, Grep, Read, Write, Edit, Bash(npm run build:*), Bash(npm run verify-codebase:*), AskUserQuestion
model: claude-sonnet-4-5-20250929
---

# meta-adaptive-checklist

Generate a deterministic, ARCHLAB-compliant checklist with **self-evolution capability** (100% META properties):
- All features of `/meta-checklist`
- **Learns** from execution outcomes
- **Promotes** successful patterns to permanent rules
- **Evolves** template with human approval (LAW 6)

## Usage

### Generate Checklist (Uses Learned Patterns)
```
/meta-adaptive-checklist [task description]
```

### Provide Execution Feedback
```
/meta-adaptive-checklist FEEDBACK [checklist-id]
```

### Review Evolution Proposal
```
/meta-adaptive-checklist EVOLVE
```

## What It Does

### Generation Phase
1. **Loads Evolution Log**: Reads promoted patterns from previous executions
2. **Context Discovery**: Generates dynamic + learned patterns from task keywords
3. **Pattern Application**: Applies promoted patterns automatically (no redundant discovery)
4. **Checklist Output**: Includes evolution metadata showing which learned patterns matched

### Learning Phase (After Execution)
1. **Feedback Collection**:
   - Which context discoveries were used?
   - Which discoveries prevented bugs/rework?
   - New anti-patterns detected?
   - Success rating (1-10)?

2. **Pattern Promotion**:
   - Tracks discovery value (1 bug prevented = +1 value)
   - Promotes patterns at threshold (≥5 value)
   - Adds to evolution log for future checklists

3. **Template Evolution**:
   - Checks compression ratio (promoted/baseline)
   - Proposes evolution at ≥1.5 ratio (50% compression)
   - Requires human approval (LAW 6)
   - Backs up before modification

## Examples

### Generate Checklist
```
/meta-adaptive-checklist Implement StateSnapshotManager for checkpoint/rollback system
```

**Output includes:**
```markdown
**Evolution**: 8 learned patterns applied
**Promoted patterns that matched:**
- `**/base-manager.ts` → 3 matches (prevents creating duplicate base class)
- `**/state-*.ts` → 5 matches (discovers existing state patterns)
```

### Provide Feedback
```
/meta-adaptive-checklist FEEDBACK checklist-abc123

Feedback:
- Discoveries used: ["base-manager.ts", "state-recovery.ts"]
- Prevented issues: ["base-manager.ts"] (prevented duplicate implementation)
- New anti-patterns: ["god-class"]
- Success rating: 9/10
```

**System processes:**
- Increments `base-manager.ts` discovery value
- Adds "god-class" to forbidden superlatives
- Updates evolution log
- Checks if promotion threshold reached

### Review Evolution Proposal
```
/meta-adaptive-checklist EVOLVE

Template evolution opportunity detected:

Current version: 1.2.0
Promoted patterns: 12
Compression ratio: 1.6
Patterns compressed from 10 → 16 (60% efficiency gain)

Proposed changes:
+ APPEND "**/state-*.ts" TO base patterns (promoted)
+ APPEND "god-class" TO forbidden_superlatives (learned)

Approve template evolution? (LAW 6: Human Authority)
```

## META Properties Achieved (100%)

| Property | META-CHECKLIST | META-ADAPTIVE | How |
|----------|----------------|---------------|-----|
| Self-reference | ✅ | ✅ | Generates checklists following own rules |
| Recursion | ✅ | ✅ | Hierarchical N→N.N→N.N.N decomposition |
| Observation | ✅ | ✅ | Context discovery (GATE 2) |
| Rule-generating | ✅ | ✅ | Dynamic patterns from keywords |
| Feedback loops | ❌ | ✅ | Execution → learning → promotion |
| Pattern compression | ❌ | ✅ | Successful patterns → permanent rules |
| Meta-optimization | ❌ | ✅ | Discovery heuristics evolve |
| Self-tuning | ❌ | ✅ | Template evolves with approval |
| **META Score** | **50%** | **100%** | True self-improvement |

## Evolution Log Structure

Located: `.claude/workspace/_meta-templates/adaptive/EVOLUTION-LOG.json`

```json
{
  "version": "1.2.0",
  "promoted_patterns": [
    {
      "pattern": "state-*",
      "glob_pattern": "**/state-*.ts",
      "promotion_reason": "prevented 5 issues",
      "success_count": 5
    }
  ],
  "learned_anti_patterns": [
    {"term": "god-class", "context": "refactored to modular"}
  ],
  "discovery_heuristics": {
    "high_value_noun_types": ["tracker", "manager", "state"]
  }
}
```

## Supporting Files

- **Template**: `.claude/workspace/_meta-templates/adaptive/META-ADAPTIVE-CHECKLIST.md`
- **Evolution Log**: `.claude/workspace/_meta-templates/adaptive/EVOLUTION-LOG.json`
- **Documentation**: `.claude/workspace/_meta-templates/adaptive/META-ADAPTIVE-README.md`
- **Architecture**: `.claude/workspace/_meta-templates/adaptive/META-ARCHITECTURE-DIAGRAM.md`

## Feedback Loop Visualization

```
Execution → Feedback → Learning → Compression → Evolution
    ↓                                              ↓
Checklist ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← Template
```

Small structural changes propagate exponentially:
1. Pattern prevents 1 bug → recorded
2. Pattern prevents 5 bugs → promoted
3. Promoted patterns auto-applied → prevent future bugs
4. 50% compression → evolution proposed
5. Human approves → template contains learned rules forever

## Safeguards

### LAW 6: Human Authority
- Template evolution requires approval
- Pattern promotions are reviewable
- Evolution log is version controlled
- Backups created before modifications

### Architectural Anchors (Never Evolve)
- 7 Laws remain fixed
- DEV-RULES.md is authoritative
- ARCHLAB.md defines principles
- GATES 0-4 stay BLOCKING
- Avoidance ontology append-only

## Philosophy

**The system observes itself, learns patterns, compresses knowledge, and evolves with human oversight.**

This achieves AGI-level properties within the narrow domain of checklist generation while maintaining ARCHLAB principles and human authority.

**The system doesn't just generate checklists—it learns to generate better checklists.**

## Task Description

$ARGUMENTS
