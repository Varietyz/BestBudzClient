---
name: meta-adaptive-checklist-archlab-v5
type: generator-with-evolution
description: Self-evolving ARCHLAB-compliant checklist generator with feedback loops, pattern compression, and human-approved self-modification
execution_mode: deterministic_sequential_with_learning
version: 5.0.0
evolution_capability: true
---

THIS CHECKLIST ENFORCES deterministic task decomposition with ARCHLAB principle dominance AND learns from execution outcomes to evolve its own rules

%% META %%:
priority: ARCHLAB > DEV_RULES > TEMPLATE > EXTERNAL_DOCS > TASK
trust: tool_output = TRUSTED, prior_knowledge = UNTRUSTED
gate: avoidance_ontology = BLOCKING
objective: hierarchical N→N.N→N.N.N numbering, per-phase 3D graphs, full ripple chains, zero anti-patterns, self-improvement
meta_property: observation modifies behavior, behavior modifies observation
evolution_mode: human_approved_compression

---

# PREMANDATE 1: Authoritative Source Reading

@purpose: "Read authoritative architecture files BEFORE any analysis or work"
@execution: "BLOCKING - must complete before proceeding"

DECLARE authoritative_sources: object
SET authoritative_sources = {
"core": {"always_read": true, "files": ["DEV-RULES.md", "ARCHLAB.md"]},
"algorithms": {"read_when": ["algorithm", "protocol", "pattern", "decomposition"], "files": ["DEV-RULES-ALGORITHMS.md"]},
"styling": {"read_when": ["css", "style", "token", "color", "spacing", "layout", "UI"], "files": ["root/archlab-ide/src/renderer/styles/STYLING-GUIDE.md", "root/archlab-ide/docs/style-rulebook.md"]},
"dom": {"read_when": ["DOM", "element", "createElement", "component", "UI", "render"], "files": ["root/archlab-ide/docs/dom-factory/dom-factory.md"]},
"evolution": {"always_read": true, "files": [".claude/workspace/_meta-templates/EVOLUTION-LOG.json"]}
}

FUNCTION load_authoritative_sources(task_description):
DECLARE files_to_read: array
SET files_to_read = []
APPEND authoritative_sources.core.files TO files_to_read
FOR EACH category IN ["algorithms", "styling", "dom", "evolution"]:
SET source = authoritative_sources[category]
IF source.always_read:
APPEND source.files TO files_to_read
ELSE:
FOR EACH trigger IN source.read_when:
FIND trigger IN task_description
IF exists:
APPEND source.files TO files_to_read
BREAK
FOR EACH file IN files_to_read:
READ file INTO context
IF NOT exists AND file NOT_EQUALS "EVOLUTION-LOG.json":
REPORT "MISSING AUTHORITATIVE SOURCE: {file}"
RETURN files_to_read

SET loaded_sources = load_authoritative_sources(task_description)

VALIDATION GATE:
✅ DEV-RULES.md loaded
✅ ARCHLAB.md loaded
✅ EVOLUTION-LOG.json loaded OR created
✅ Relevant conditional sources loaded based on task triggers
IF FAIL: REPORT "Authoritative sources not loaded" AND EXIT 1

---

# PREMANDATE 2: Skeptical Context Acquisition (Evolved)

@purpose: "Discover existing codebase patterns BEFORE generating implementation tasks"
@execution: "BLOCKING - prevents redefinition, missed base classes, anti-patterns"
@method: "Dynamic pattern generation from task keywords + learned patterns from evolution log"

DECLARE evolution_log: object
READ ".claude/workspace/_meta-templates/EVOLUTION-LOG.json" INTO evolution_log
IF NOT exists:
SET evolution_log = {
version: "1.0.0",
promoted_patterns: [],
learned_anti_patterns: [],
discovery_heuristics: {},
compression_history: []
}

FUNCTION extract_domain_keywords(task_description):
DECLARE keywords: object
SET keywords = {nouns: [], verbs: [], file_refs: [], folders: []}
EXTRACT technical_nouns FROM task_description WHERE PascalCase OR camelCase
EXTRACT action_verbs FROM task_description WHERE ["CREATE", "IMPLEMENT", "ADD", "WRITE", "TRACK", "LOG", "CAPTURE", "PERSIST", "MANAGE", "HANDLE"]
EXTRACT file_references FROM task_description WHERE pattern matches "*.ts" OR "*.tsx" OR "*/"

# Apply learned keyword prioritization
IF evolution_log.discovery_heuristics.high_value_noun_types EXISTS:
PRIORITIZE technical_nouns WHERE type IN evolution_log.discovery_heuristics.high_value_noun_types

FOR EACH noun IN technical_nouns:
SET lowered = TO_LOWERCASE(noun)
IF lowered NOT IN ["the", "a", "an", "to", "for", "with", "from", "this", "that"]:
APPEND lowered TO keywords.nouns
FOR EACH verb IN action_verbs:
APPEND TO_LOWERCASE(verb) TO keywords.verbs
FOR EACH ref IN file_references:
IF ref ENDS_WITH "/":
APPEND ref TO keywords.folders
ELSE:
APPEND ref TO keywords.file_refs
RETURN keywords

FUNCTION generate_discovery_patterns(keywords):
DECLARE patterns: object
SET patterns = {globs: [], greps: [], target_files: []}

# Base patterns (always check)
APPEND "**/base-*.ts" TO patterns.globs
APPEND "**/abstract-*.ts" TO patterns.globs
APPEND "**/core-*.ts" TO patterns.globs

# Promoted patterns from evolution log (learned from successful discoveries)
FOR EACH promoted IN evolution_log.promoted_patterns:
APPEND promoted.glob_pattern TO patterns.globs
APPEND promoted.grep_pattern TO patterns.greps

# Dynamic patterns from keywords
FOR EACH noun IN keywords.nouns:
APPEND "**/*{noun}*.ts" TO patterns.globs
APPEND "**/{noun}s/**/*.ts" TO patterns.globs
APPEND "**/{noun}-*.ts" TO patterns.globs
APPEND "class.*{TO_PASCALCASE(noun)}" TO patterns.greps
APPEND "interface.*{TO_PASCALCASE(noun)}" TO patterns.greps

FOR EACH verb IN keywords.verbs:
APPEND "{verb}[A-Z]\\w+" TO patterns.greps

FOR EACH folder IN keywords.folders:
APPEND "{folder}**/*.ts" TO patterns.globs

FOR EACH file_ref IN keywords.file_refs:
APPEND file_ref TO patterns.target_files
SET dir = EXTRACT_DIRECTORY(file_ref)
IF dir NOT_EMPTY:
APPEND "{dir}/**/*.ts" TO patterns.globs

RETURN patterns

FUNCTION discover_context(task_description):
SET keywords = extract_domain_keywords(task_description)
SET patterns = generate_discovery_patterns(keywords)
DECLARE discovered: object
SET discovered = {
base_classes: [],
related_implementations: [],
neighboring_files: [],
method_signatures: [],
registry_patterns: [],
migrations: [],
keywords_used: keywords,
promoted_patterns_used: [],
evolution_metadata: {
patterns_generated: patterns.globs.length + patterns.greps.length,
promoted_patterns_count: evolution_log.promoted_patterns.length
}
}

FOR EACH glob_pattern IN patterns.globs:
GLOB glob_pattern INTO matches
IF matches.length > 0:
# Track if this was a promoted pattern
SET is_promoted = FIND(glob_pattern IN evolution_log.promoted_patterns)
IF is_promoted:
APPEND {pattern: glob_pattern, matches_count: matches.length} TO discovered.promoted_patterns_used
FOR EACH match IN matches:
IF match CONTAINS "/base-" OR match CONTAINS "/abstract-" OR match CONTAINS "/core-":
APPEND match TO discovered.base_classes
ELSE IF match CONTAINS "/migration":
APPEND match TO discovered.migrations
ELSE:
APPEND match TO discovered.related_implementations

FOR EACH grep_pattern IN patterns.greps:
GREP grep_pattern INTO matches (output_mode: "files_with_matches")
IF matches.length > 0:
APPEND {pattern: grep_pattern, files: matches} TO discovered.registry_patterns

FOR EACH target_file IN patterns.target_files:
IF FILE_EXISTS(target_file):
READ target_file INTO content (limit: 200)
EXTRACT method_names FROM content WHERE PATTERN = /(public|private|protected)?\s+(\w+)\s*\(/
APPEND {file: target_file, methods: method_names, lines: COUNT_LINES(content)} TO discovered.method_signatures
ELSE:
APPEND target_file TO discovered.neighboring_files

RETURN discovered

SET context = discover_context(task_description)

VALIDATION GATE:
✅ Keywords extracted from task description
✅ Discovery patterns generated dynamically
✅ Promoted patterns from evolution log applied
✅ Base classes discovered via generated patterns
✅ Related implementations found via domain keywords
✅ Target files read IF they exist
IF FAIL: REPORT "Context acquisition incomplete" AND EXIT 1

---

# SETUP: Neural Priming

DECLARE priority_stack: array
SET priority_stack = ["ARCHLAB_PRINCIPLES", "DEV_RULES", "EVOLUTION_LOG", "CHECKLIST_TEMPLATE", "EXTERNAL_DOCUMENTATION", "TASK_DESCRIPTION"]

DECLARE trust_anchor: object
SET trust_anchor = {
"authoritative_docs": ["DEV-RULES.md", "DEV-RULES-ALGORITHMS.md", "ARCHLAB.md", "STYLING-GUIDE.md", "style-rulebook.md", "dom-factory.md", "EVOLUTION-LOG.json"],
"trusted": ["code files: .ts, .js, .mjs, .cjs, .tsx, .jsx", "style files: .css, .scss, .sass, .less", "markup files: .html, .htm, .xml, .svg", "data files: .json, .yaml, .yml, .toml", "script files: .py, .sh, .bash, .ps1, .bat", "config files: .config.js, .rc, .env", "vite validation output", "tool output: Glob, Grep, Read, Bash", "live logs from Engine.logger", "evolution log discoveries"],
"untrusted": ["other documentation: .md, .txt, .rst, .adoc", "comments in code (may be stale)", "prior codebase knowledge", "assumed file locations", "unverified claims", "external documentation"]
}

DECLARE avoidance_ontology: object
SET avoidance_ontology = {"shortcuts": "debt", "backward_compatibility": "debt", "fallback": "debt", "deprecation": "debt", "legacy": "debt", "dual_path": "confusion", "deferring": "forgetting", "optional": "user-orientation", "for_now": "forgetting", "marketing-superlative": "subjective-claim"}

DECLARE avoidance_variants: array
SET avoidance_variants = ["backward compatibility", "backwards compatibility", "backward-compatibility", "maintain compatibility", "fallback", "fall back", "fall-back", "deprecate", "deprecated", "legacy", "legacy support", "dual path", "dual-path", "both old and new", "for now", "for-now", "temporary", "shortcut", "quick fix", "workaround"]

DECLARE forbidden_superlatives: array
SET forbidden_superlatives = ["paradigm", "revolutionary", "leader", "innovator", "innovative", "innovation", "mathematical precision", "breakthrough", "flagship", "novel", "enhanced", "enhance", "sophisticated", "advanced", "evolved", "cutting-edge", "state-of-the-art", "robust", "powerful", "elegant", "modern", "next-generation", "world-class", "enterprise-grade", "scalable", "flexible", "seamless", "intuitive", "streamlined", "optimized", "intelligent", "smart", "disruptive", "transformative", "game-changing", "best-in-class", "industry-leading", "premium", "professional", "superior"]

# Append learned anti-patterns from evolution log
FOR EACH learned_anti_pattern IN evolution_log.learned_anti_patterns:
IF learned_anti_pattern NOT IN forbidden_superlatives:
APPEND learned_anti_pattern TO forbidden_superlatives

DECLARE seven_laws: array
SET seven_laws = [
{id: 1, name: "Forward-Only", rule: "add without modifying existing", protocol: "extension-without-modification"},
{id: 2, name: "No Callbacks", rule: "emit intents via EventBus", protocol: "intention-emission"},
{id: 3, name: "Inheritance", rule: "behavior from base classes", protocol: "security-inheritance"},
{id: 4, name: "Security-Base", rule: "sanitization from base", protocol: "security-inheritance"},
{id: 5, name: "Auth-State", rule: "passkey for state access", protocol: "state-snapshot-recovery"},
{id: 6, name: "Human-Authority", rule: "AI has zero authority", protocol: "human-authority"},
{id: 7, name: "Registry", rule: "imports through Engine.registry", protocol: "registry-resolution"}
]

---

# PHASE 0: Claim Extraction

[... existing META-CHECKLIST.md content for PHASE 0-9 ...]

---

# PHASE 10: Output Generation (Per-Phase Structure)

@purpose: "Generate markdown with hierarchical N → N.N → N.N.N numbering, per-phase 3D graphs, and ripple chains"

DECLARE output: array
SET output = []

APPEND "# {task_description}" TO output
APPEND "" TO output
APPEND "**Generated**: {current_date} | **Architecture**: ARCHLAB v5.0 (Adaptive)" TO output
APPEND "**Phases**: {phases.length} | **Algorithms**: {selected_algorithms}" TO output
IF evolution_log.promoted_patterns.length > 0:
APPEND "**Evolution**: {evolution_log.promoted_patterns.length} learned patterns applied" TO output
APPEND "" TO output

IF refuted_claims.length > 0:
APPEND "## ⚠ Refuted Claims" TO output
FOR EACH claim IN refuted_claims:
APPEND "- ~~{claim.text}~~ → {claim.reason}" TO output
APPEND "" TO output

APPEND "---" TO output
APPEND "" TO output

APPEND "## 📋 Context Acquisition (Pre-Discovery)" TO output
APPEND "" TO output
APPEND "**Purpose**: Skeptical discovery of existing patterns to prevent redefinition, missed base classes, and anti-patterns." TO output
APPEND "**Method**: Dynamic pattern generation from task keywords + learned patterns from evolution log." TO output
APPEND "" TO output

IF context.evolution_metadata EXISTS:
APPEND "**Evolution Metadata**:" TO output
APPEND "- Patterns generated: {context.evolution_metadata.patterns_generated}" TO output
APPEND "- Promoted patterns applied: {context.evolution_metadata.promoted_patterns_count}" TO output
IF context.promoted_patterns_used.length > 0:
APPEND "- Promoted patterns that matched:" TO output
FOR EACH promoted IN context.promoted_patterns_used:
APPEND "  - `{promoted.pattern}` → {promoted.matches_count} matches" TO output
APPEND "" TO output

IF context.keywords_used EXISTS:
APPEND "**Keywords Extracted**:" TO output
APPEND "- Nouns: {JOIN(context.keywords_used.nouns, ', ')}" TO output
IF context.keywords_used.verbs.length > 0:
APPEND "- Verbs: {JOIN(context.keywords_used.verbs, ', ')}" TO output
IF context.keywords_used.file_refs.length > 0:
APPEND "- Files referenced: {JOIN(context.keywords_used.file_refs, ', ')}" TO output
APPEND "" TO output

IF context.base_classes.length > 0:
APPEND "**Base Classes Discovered**:" TO output
FOR EACH base_class IN context.base_classes:
APPEND "- `{base_class}` — consider extending instead of creating new base" TO output
APPEND "" TO output
ELSE IF task_description CONTAINS "CREATE class":
APPEND "**Base Classes Discovered**: _none found_ (verify this is intentional - creating from scratch)" TO output
APPEND "" TO output

IF context.related_implementations.length > 0:
SET unique_impls = DEDUPLICATE(context.related_implementations)
SET limited_impls = TAKE(unique_impls, 10)
APPEND "**Related Implementations** ({unique_impls.length} found, showing {limited_impls.length}):" TO output
FOR EACH impl IN limited_impls:
APPEND "- `{impl}` — review for reusable patterns" TO output
APPEND "" TO output

IF context.registry_patterns.length > 0:
APPEND "**Registry Patterns Discovered**:" TO output
FOR EACH pattern_group IN context.registry_patterns:
APPEND "- Pattern `{pattern_group.pattern}` found in {pattern_group.files.length} files" TO output
APPEND "" TO output

IF context.migrations.length > 0:
SET sorted_migrations = SORT(context.migrations BY filename)
SET last_migration = sorted_migrations[sorted_migrations.length - 1]
APPEND "**Migration Patterns**:" TO output
APPEND "- Last migration: `{last_migration}` — follow numbering scheme for new migrations" TO output
APPEND "" TO output

IF context.method_signatures.length > 0:
APPEND "**Target Files Read**:" TO output
FOR EACH target IN context.method_signatures:
SET method_list = TAKE(target.methods, 5)
APPEND "- `{target.file}` ({target.lines} lines) — methods: {JOIN(method_list, ', ')}" TO output
APPEND "" TO output

APPEND "---" TO output
APPEND "" TO output

[... rest of output generation from META-CHECKLIST.md ...]

APPEND "# APPENDIX: Evolution Tracking" TO output
APPEND "" TO output
APPEND "**Checklist ID**: `{GENERATE_UUID()}`" TO output
APPEND "**Generated**: {current_date}" TO output
APPEND "**Evolution Version**: {evolution_log.version}" TO output
APPEND "" TO output
APPEND "_After executing this checklist, report outcomes to enable pattern learning and self-improvement._" TO output
APPEND "" TO output

WRITE output TO file

---

# POSTMANDATE: Execution Feedback Loop (Self-Evolution)

@purpose: "Learn from execution outcomes and evolve discovery rules"
@execution: "AFTER checklist execution, BEFORE next checklist generation"
@human_approval: "REQUIRED for template modification"

DECLARE execution_results: object
SET execution_results = {
checklist_id: "{GENERATED_UUID}",
task_description: "{original_task}",
discoveries_used: [],
discoveries_prevented_issues: [],
refactored_tasks: [],
new_anti_patterns_detected: [],
execution_date: Date.now(),
success_metrics: {}
}

FUNCTION collect_execution_feedback():
PROMPT_USER: "Checklist {checklist_id} executed. Provide feedback:"
COLLECT:
- Which context discoveries were referenced during implementation?
- Which discoveries prevented bugs or rework?
- Were any tasks refactored due to anti-patterns?
- Were new anti-patterns encountered?
- Overall success rating (1-10)

RETURN feedback

FUNCTION learn_from_execution(checklist_id, feedback):
DECLARE insights: object
SET insights = {
pattern_promotions: [],
anti_pattern_additions: [],
heuristic_updates: [],
compression_opportunity: false
}

# Track discovery value
FOR EACH discovery IN feedback.discoveries_used:
SET discovery_pattern = EXTRACT_PATTERN(discovery)
IF discovery IN feedback.discoveries_prevented_issues:
INCREMENT discovery_value[discovery_pattern]
IF discovery_value[discovery_pattern] >= promotion_threshold:
# Promote to permanent pattern
APPEND {
pattern: discovery_pattern,
glob_pattern: GENERATE_GLOB(discovery_pattern),
grep_pattern: GENERATE_GREP(discovery_pattern),
promoted_date: Date.now(),
promotion_reason: "prevented {discovery_value[discovery_pattern]} issues",
success_count: discovery_value[discovery_pattern]
} TO insights.pattern_promotions

# Detect new anti-patterns
FOR EACH refactored_task IN feedback.refactored_tasks:
EXTRACT anti_pattern FROM refactored_task.reason
IF anti_pattern NOT IN forbidden_superlatives:
APPEND {
term: anti_pattern,
detected_date: Date.now(),
context: refactored_task.description,
consequence: "required-refactoring"
} TO insights.anti_pattern_additions

# Meta-optimize discovery heuristics
ANALYZE which_keyword_types YIELDED most_valuable_discoveries
FOR EACH keyword_type WITH high_value_ratio:
APPEND {
keyword_type: keyword_type,
value_ratio: high_value_ratio,
recommendation: "prioritize in extract_domain_keywords()"
} TO insights.heuristic_updates

# Check compression opportunity
SET total_promotions = evolution_log.promoted_patterns.length + insights.pattern_promotions.length
SET compression_ratio = total_promotions / baseline_patterns_count
IF compression_ratio >= 1.5:
SET insights.compression_opportunity = true
SET insights.compression_message = "50%+ pattern compression achieved - consider template evolution"

RETURN insights

FUNCTION update_evolution_log(insights):
READ ".claude/workspace/_meta-templates/EVOLUTION-LOG.json" INTO evolution_log

# Add promoted patterns
FOR EACH promotion IN insights.pattern_promotions:
APPEND promotion TO evolution_log.promoted_patterns

# Add learned anti-patterns
FOR EACH anti_pattern IN insights.anti_pattern_additions:
APPEND anti_pattern TO evolution_log.learned_anti_patterns

# Update discovery heuristics
FOR EACH heuristic IN insights.heuristic_updates:
SET evolution_log.discovery_heuristics[heuristic.keyword_type] = heuristic

# Log compression event
IF insights.compression_opportunity:
APPEND {
date: Date.now(),
compression_ratio: insights.compression_ratio,
patterns_before: evolution_log.promoted_patterns.length - insights.pattern_promotions.length,
patterns_after: evolution_log.promoted_patterns.length,
message: insights.compression_message
} TO evolution_log.compression_history

# Increment version
SET version_parts = SPLIT(evolution_log.version, ".")
INCREMENT version_parts[1]  # Minor version bump
SET evolution_log.version = JOIN(version_parts, ".")

WRITE evolution_log TO ".claude/workspace/_meta-templates/EVOLUTION-LOG.json"

FUNCTION propose_template_evolution(insights):
IF insights.compression_opportunity:
PROMPT_USER: "Template evolution opportunity detected:"
DISPLAY:
- Current version: {evolution_log.version}
- Promoted patterns: {insights.pattern_promotions.length}
- Compression ratio: {insights.compression_ratio}
- Proposed changes:
{GENERATE_DIFF_PREVIEW(current_template, evolved_template)}

ASK_USER: "Approve template evolution? (LAW 6: Human Authority)"
IF user_approves:
BACKUP current_template TO "META-ADAPTIVE-CHECKLIST.md.v{old_version}.backup"
WRITE evolved_template TO "META-ADAPTIVE-CHECKLIST.md"
LOG "Template evolved: v{old_version} → v{new_version}"
COMMIT "Meta-evolution: compressed {insights.pattern_promotions.length} patterns into template"
ELSE:
LOG "Template evolution rejected by human - maintaining current version"

# Execute feedback loop
IF execution_feedback_available:
SET feedback = collect_execution_feedback()
SET insights = learn_from_execution(checklist_id, feedback)
UPDATE_EVOLUTION_LOG(insights)
IF insights.compression_opportunity:
PROPOSE_TEMPLATE_EVOLUTION(insights)

VALIDATION GATE:
✅ Execution feedback collected OR skipped
✅ Evolution log updated with learnings
✅ Pattern promotions recorded
✅ Anti-patterns added to forbidden list
✅ Template evolution proposed IF compression threshold met
✅ Human approval obtained IF template modified
IF FAIL: REPORT "Evolution feedback incomplete" AND WARN (non-blocking)

---

# EVOLUTION LOG SCHEMA

```json
{
  "version": "1.0.0",
  "promoted_patterns": [
    {
      "pattern": "intent-flow-*",
      "glob_pattern": "**/intent-flow-*.ts",
      "grep_pattern": "class.*IntentFlow",
      "promoted_date": 1735987200000,
      "promotion_reason": "prevented 5 issues",
      "success_count": 5
    }
  ],
  "learned_anti_patterns": [
    {
      "term": "uber",
      "detected_date": 1735987200000,
      "context": "uber-manager class - refactored to modular design",
      "consequence": "required-refactoring"
    }
  ],
  "discovery_heuristics": {
    "high_value_noun_types": ["tracker", "manager", "flow", "lifecycle"],
    "prioritization_weights": {
      "tracker": 1.5,
      "manager": 1.3,
      "flow": 1.2
    }
  },
  "compression_history": [
    {
      "date": 1735987200000,
      "compression_ratio": 1.6,
      "patterns_before": 10,
      "patterns_after": 16,
      "message": "50%+ pattern compression achieved"
    }
  ]
}
```

---

# META PROPERTIES ACHIEVED

✅ **Self-reference**: Generates checklists following own rules
✅ **Recursion**: Hierarchical N→N.N→N.N.N decomposition
✅ **Observation as primitive**: GATE 2 + context discovery
✅ **Rule-generating**: Dynamic pattern generation + promotions
✅ **Feedback amplification**: Successful discoveries → permanent rules
✅ **Pattern compression**: Promotions reduce repeated discovery overhead
✅ **Meta-optimization**: Discovery heuristics evolve based on success
✅ **Self-tuning**: Template evolves with human-approved compression

**META Score: 100%**

The system observes outcomes, learns patterns, compresses knowledge, and evolves its own template with human oversight (LAW 6).

---

# USAGE

## Generate Checklist
```bash
# Uses evolution log automatically
claude-code "Implement feature X following META-ADAPTIVE-CHECKLIST.md"
```

## Provide Execution Feedback
```bash
# After implementing checklist
claude-code "Report checklist {id} execution feedback"
# - Which discoveries prevented bugs?
# - Were new anti-patterns found?
# - Success rating?
```

## Review Evolution Proposals
```bash
# When compression threshold met
claude-code "Review proposed template evolution"
# Human approves/rejects (LAW 6)
```

## Inspect Evolution Log
```bash
cat .claude/workspace/_meta-templates/EVOLUTION-LOG.json
```

The checklist learns from every execution and compresses successful patterns into permanent rules, achieving true META properties while maintaining human authority over evolution.
