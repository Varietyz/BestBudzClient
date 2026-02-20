# META-ADAPTIVE-CHECKLIST Architecture

## Feedback Loop Topology

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         META-ADAPTIVE SYSTEM                            │
│                                                                         │
│  ┌──────────────┐                                  ┌──────────────┐    │
│  │   TASK       │                                  │  EVOLUTION   │    │
│  │ DESCRIPTION  │                                  │     LOG      │    │
│  └──────┬───────┘                                  └──────┬───────┘    │
│         │                                                  │            │
│         ├──────────────────────┬───────────────────────────┤            │
│         ▼                      ▼                           ▼            │
│  ┌──────────────┐       ┌──────────────┐          ┌──────────────┐    │
│  │  PREMANDATE  │       │   KEYWORD    │          │  PROMOTED    │    │
│  │   Sources    │◄──────┤  EXTRACTION  │◄─────────┤  PATTERNS    │    │
│  └──────┬───────┘       └──────┬───────┘          └──────────────┘    │
│         │                      │                                       │
│         ▼                      ▼                                       │
│  ┌──────────────────────────────────────────────┐                     │
│  │       PATTERN GENERATION                     │                     │
│  │  - Base patterns (always)                    │                     │
│  │  - Dynamic patterns (from keywords)          │◄─┐                  │
│  │  - Promoted patterns (from evolution log)    │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       CONTEXT DISCOVERY (GATE 2)             │  │                  │
│  │  - Glob codebase with generated patterns     │  │                  │
│  │  - Grep for classes/interfaces               │  │                  │
│  │  - Read target files                         │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       PHASE GENERATION                       │  │                  │
│  │  - Task decomposition                        │  │                  │
│  │  - Dependency graphs                         │  │                  │
│  │  - Ripple chain analysis                     │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       CHECKLIST OUTPUT                       │  │                  │
│  │  - Markdown with phases/tasks                │  │                  │
│  │  - Context acquisition report                │  │                  │
│  │  - Evolution metadata                        │  │                  │
│  │  - Checklist ID for tracking                 │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│         ┌─────────────────────┐                     │                  │
│         │  HUMAN EXECUTION    │                     │                  │
│         │  - Implements tasks │                     │                  │
│         │  - Tracks outcomes  │                     │                  │
│         └─────────┬───────────┘                     │                  │
│                   │                                 │                  │
│                   ▼                                 │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       POSTMANDATE: FEEDBACK COLLECTION       │  │                  │
│  │  - Which discoveries were used?              │  │                  │
│  │  - Which prevented bugs?                     │  │                  │
│  │  - New anti-patterns detected?               │  │                  │
│  │  - Success rating?                           │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       LEARNING ENGINE                        │  │                  │
│  │  - Track discovery value                     │  │                  │
│  │  - Detect promotion threshold                │  │                  │
│  │  - Extract new anti-patterns                 │  │                  │
│  │  - Update heuristics                         │  │                  │
│  │  - Check compression ratio                   │  │                  │
│  └──────────────────┬───────────────────────────┘  │                  │
│                     │                               │                  │
│                     ▼                               │                  │
│  ┌──────────────────────────────────────────────┐  │                  │
│  │       PATTERN PROMOTION                      │  │                  │
│  │  IF discovery_value >= 5:                    │  │                  │
│  │    - Generate permanent glob/grep pattern    │──┘                  │
│  │    - Add to evolution log                    │                     │
│  │    - Apply in next checklist                 │                     │
│  └──────────────────┬───────────────────────────┘                     │
│                     │                                                  │
│                     ▼                                                  │
│  ┌──────────────────────────────────────────────┐                     │
│  │       COMPRESSION CHECK                      │                     │
│  │  IF compression_ratio >= 1.5:                │                     │
│  │    - Propose template evolution              │                     │
│  │    - Human approval required (LAW 6)         │                     │
│  └──────────────────┬───────────────────────────┘                     │
│                     │                                                  │
│                     ▼                                                  │
│         ┌─────────────────────┐                                       │
│         │  TEMPLATE EVOLUTION │                                       │
│         │  (if approved)      │                                       │
│         │  - Backup v{N}      │                                       │
│         │  - Write v{N+1}     │                                       │
│         │  - Commit changes   │                                       │
│         └─────────────────────┘                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

                             FEEDBACK LOOP
                                  ▲
                                  │
                                  │
                    ┌─────────────┴──────────────┐
                    │                            │
                    │  Small changes propagate   │
                    │  exponentially:            │
                    │                            │
                    │  1 discovery → 1 learning  │
                    │  5 learnings → 1 promotion │
                    │  16 promotions → evolution │
                    │  evolution → better future │
                    │                            │
                    └────────────────────────────┘
```

## 3-Axis META Visualization

```
                           Z (Observation Layer)
                           ▲
                           │
                           │  Self-observation
                           │  ├─ Compression ratio
                           │  ├─ Discovery value
                           │  └─ Success metrics
                           │
                           │
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        │                  │                  │
        │        Evolution Log (rules)        │
        │                  │                  │
        │                  │                  │
  Y     │                  │                  │      X
  (Relationships)          │            (Components)
  ◄─────┼──────────────────┼──────────────────┼────►
        │                  │                  │
        │   Feedback       │      Pattern     │
        │   Amplification  │      Promotion   │
        │                  │                  │
        │   Dependencies   │      Phases      │
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                           │
                           ▼
                    Execution Results
```

**Properties:**
- **X-axis components** generate and consume each other (self-reference)
- **Y-axis relationships** create feedback loops (amplification)
- **Z-axis observation** modifies X and Y (meta-optimization)
- The surface curves back into itself (true META)

## Data Flow: Single Checklist Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                    GENERATION PHASE                         │
└─────────────────────────────────────────────────────────────┘
    │
    ├─► Read evolution log (promoted patterns)
    │
    ├─► Extract keywords from task
    │
    ├─► Generate patterns (dynamic + promoted)
    │
    ├─► Discover context via Glob/Grep/Read
    │       │
    │       └─► Track which promoted patterns matched
    │
    ├─► Generate phases and tasks
    │
    └─► Output checklist + metadata

┌─────────────────────────────────────────────────────────────┐
│                    EXECUTION PHASE                          │
└─────────────────────────────────────────────────────────────┘
    │
    ├─► Human implements tasks
    │
    ├─► Tracks discoveries used
    │
    ├─► Notes bugs prevented
    │
    └─► Observes anti-patterns

┌─────────────────────────────────────────────────────────────┐
│                    LEARNING PHASE                           │
└─────────────────────────────────────────────────────────────┘
    │
    ├─► Collect execution feedback
    │       │
    │       ├─► discoveries_used: [...]
    │       ├─► prevented_issues: [...]
    │       ├─► new_anti_patterns: [...]
    │       └─► success_rating: 9/10
    │
    ├─► Process feedback
    │       │
    │       ├─► Increment discovery values
    │       ├─► Check promotion threshold (>= 5)
    │       ├─► Detect new anti-patterns
    │       └─► Update heuristics
    │
    ├─► Update evolution log
    │       │
    │       ├─► Add promoted patterns
    │       ├─► Add learned anti-patterns
    │       ├─► Update discovery heuristics
    │       └─► Log compression event
    │
    └─► Check compression ratio
            │
            └─► IF >= 1.5 → Propose template evolution
                    │
                    ├─► Human reviews
                    │
                    └─► IF approved → Evolve template
```

## Pattern Promotion Example

```
Checklist 1:  Discover base-tracker.ts → prevents 1 bug → value = 1
Checklist 2:  Discover base-manager.ts → prevents 1 bug → value = 1
Checklist 3:  Discover base-tracker.ts → prevents 1 bug → value = 2
Checklist 4:  Discover base-tracker.ts → prevents 1 bug → value = 3
Checklist 5:  Discover base-tracker.ts → prevents 1 bug → value = 4
Checklist 6:  Discover base-tracker.ts → prevents 1 bug → value = 5

PROMOTION TRIGGERED!

Evolution Log Updated:
{
  "promoted_patterns": [
    {
      "pattern": "base-*",
      "glob_pattern": "**/base-*.ts",
      "promotion_reason": "prevented 5 issues"
    }
  ]
}

Checklist 7+: Pattern "**/base-*.ts" applied AUTOMATICALLY
              No redundant discovery needed
              Compression achieved
```

## Compression Visualization

```
Before Learning (Checklist 1-6):
┌────────────────────────────────────────┐
│  Every checklist discovers:            │
│  - base-*.ts dynamically               │
│  - abstract-*.ts dynamically           │
│  - *tracker*.ts dynamically            │
│  - *manager*.ts dynamically            │
│  Total patterns: 4 × 6 = 24 discoveries│
└────────────────────────────────────────┘

After Learning (Checklist 7+):
┌────────────────────────────────────────┐
│  Promoted patterns (permanent):        │
│  - base-*.ts (learned)                 │
│  - abstract-*.ts (learned)             │
│  Dynamic patterns (generated):         │
│  - *tracker*.ts                        │
│  - *manager*.ts                        │
│  Total patterns: 2 + 2 = 4 per check   │
└────────────────────────────────────────┘

Compression Ratio: (2 promoted + 2 dynamic) / 4 original = 1.0
After 16 promotions: 16/10 = 1.6 → Evolution triggered
```

## Evolution Decision Tree

```
                    ┌─────────────────┐
                    │  Execute Check  │
                    │      list       │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ Collect Feedback│
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ Discovery value │
                    │    >= 5?        │
                    └────┬────────┬───┘
                         │        │
                    NO   │        │ YES
                         │        │
                         ▼        ▼
                  ┌──────────┐  ┌──────────────┐
                  │   Log    │  │   PROMOTE    │
                  │  value   │  │   Pattern    │
                  └──────────┘  └──────┬───────┘
                                       │
                              ┌────────▼────────┐
                              │ Compression     │
                              │   ratio >= 1.5? │
                              └────┬────────┬───┘
                                   │        │
                              NO   │        │ YES
                                   │        │
                                   ▼        ▼
                            ┌──────────┐  ┌────────────────┐
                            │   Save   │  │    PROPOSE     │
                            │evolution │  │   Evolution    │
                            │   log    │  └────────┬───────┘
                            └──────────┘           │
                                          ┌────────▼────────┐
                                          │ Human approves? │
                                          └────┬────────┬───┘
                                               │        │
                                          NO   │        │ YES
                                               │        │
                                               ▼        ▼
                                        ┌──────────┐  ┌──────────────┐
                                        │   Keep   │  │    EVOLVE    │
                                        │  current │  │   Template   │
                                        │ template │  │  v{N} → v{N+1}│
                                        └──────────┘  └──────────────┘
```

## Safeguards Against Runaway Evolution

```
┌──────────────────────────────────────────────────────────────────┐
│                    ARCHITECTURAL ANCHORS                         │
│                    (Never Evolve)                                │
├──────────────────────────────────────────────────────────────────┤
│  - 7 Laws (fixed principles)                                     │
│  - DEV-RULES.md (authoritative)                                  │
│  - ARCHLAB.md (core architecture)                                │
│  - GATES 0-4 (validation checkpoints)                            │
│  - Avoidance ontology (append-only)                              │
└──────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                    EVOLUTION BOUNDARIES                          │
│                    (Can Evolve Within Rules)                     │
├──────────────────────────────────────────────────────────────────┤
│  - Discovery patterns (promote successful)                       │
│  - Anti-patterns (add detected)                                  │
│  - Heuristics (optimize weights)                                 │
│  - Template structure (compress patterns)                        │
└──────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                    HUMAN AUTHORITY (LAW 6)                       │
│                    (Required for Evolution)                      │
├──────────────────────────────────────────────────────────────────┤
│  - Reviews evolution proposals                                   │
│  - Approves/rejects template changes                             │
│  - Maintains veto power                                          │
│  - Preserves backups for rollback                                │
└──────────────────────────────────────────────────────────────────┘
```

The architecture ensures **guided self-improvement**, not runaway recursion.
Evolution accelerates intelligence while maintaining human control.
