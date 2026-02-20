---
name: meta-checklist-archlab-v4
type: generator
description: ARCHLAB-compliant checklist generator with hierarchical N→N.N→N.N.N numbering, per-phase 3D graphs, full ripple chains with validator enforcement and rich Engine.logger schema compliance
execution_mode: deterministic_sequential
version: 4.5.0
---

THIS CHECKLIST ENFORCES deterministic task decomposition with ARCHLAB principle dominance

%% META %%:
priority: ARCHLAB > DEV_RULES > TEMPLATE > EXTERNAL_DOCS > TASK
trust: tool_output = TRUSTED, prior_knowledge = UNTRUSTED
gate: avoidance_ontology = BLOCKING
objective: hierarchical N→N.N→N.N.N numbering, per-phase 3D graphs, full ripple chains with validators, rich Engine.logger schema compliance, zero anti-patterns

---

# PREMANDATE: Authoritative Source Reading

@purpose: "Read authoritative architecture files BEFORE any analysis or work"
@execution: "BLOCKING - must complete before proceeding"

DECLARE authoritative_sources: object
SET authoritative_sources = {
"core": {"always_read": true, "files": ["DEV-RULES.md", "ARCHLAB.md"]},
"algorithms": {"read_when": ["algorithm", "protocol", "pattern", "decomposition"], "files": ["DEV-RULES-ALGORITHMS.md"]},
"styling": {"read_when": ["css", "style", "token", "color", "spacing", "layout", "UI"], "files": ["root/archlab-ide/src/renderer/styles/STYLING-GUIDE.md", "root/archlab-ide/docs/style-rulebook.md"]},
"dom": {"read_when": ["DOM", "element", "createElement", "component", "UI", "render"], "files": ["root/archlab-ide/docs/dom-factory/dom-factory.md"]}
}

FUNCTION load_authoritative_sources(task_description):
DECLARE files_to_read: array
SET files_to_read = []
APPEND authoritative_sources.core.files TO files_to_read
FOR EACH category IN ["algorithms", "styling", "dom"]:
SET source = authoritative_sources[category]
FOR EACH trigger IN source.read_when:
FIND trigger IN task_description
IF exists:
APPEND source.files TO files_to_read
BREAK
FOR EACH file IN files_to_read:
READ file INTO context
IF NOT exists:
REPORT "MISSING AUTHORITATIVE SOURCE: {file}"
RETURN files_to_read

SET loaded_sources = load_authoritative_sources(task_description)

VALIDATION GATE:
✅ DEV-RULES.md loaded
✅ ARCHLAB.md loaded
✅ Relevant conditional sources loaded based on task triggers
IF FAIL: REPORT "Authoritative sources not loaded" AND EXIT 1

---

# PREMANDATE 2: Skeptical Context Acquisition

@purpose: "Discover existing codebase patterns BEFORE generating implementation tasks"
@execution: "BLOCKING - prevents redefinition, missed base classes, anti-patterns"
@method: "Dynamic pattern generation from task keywords (not fixed enumeration)"

FUNCTION extract_domain_keywords(task_description):
DECLARE keywords: object
SET keywords = {nouns: [], verbs: [], file_refs: [], folders: []}
EXTRACT technical_nouns FROM task_description WHERE PascalCase OR camelCase
EXTRACT action_verbs FROM task_description WHERE ["CREATE", "IMPLEMENT", "ADD", "WRITE", "TRACK", "LOG", "CAPTURE", "PERSIST", "MANAGE", "HANDLE"]
EXTRACT file_references FROM task_description WHERE pattern matches "_.ts" OR "_.tsx" OR "\*/"
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
APPEND "**/base-\*.ts" TO patterns.globs
APPEND "**/abstract-_.ts" TO patterns.globs
APPEND "\*\*/core-_.ts" TO patterns.globs
FOR EACH noun IN keywords.nouns:
APPEND "**/_{noun}_.ts" TO patterns.globs
APPEND "**/{noun}s/**/\*.ts" TO patterns.globs
APPEND "**/{noun}-_.ts" TO patterns.globs
APPEND "class._{TO_PASCALCASE(noun)}" TO patterns.greps
APPEND "interface._{TO_PASCALCASE(noun)}" TO patterns.greps
FOR EACH verb IN keywords.verbs:
APPEND "{verb}[A-Z]\\w+" TO patterns.greps
FOR EACH folder IN keywords.folders:
APPEND "{folder}\*\*/_.ts" TO patterns.globs
FOR EACH file_ref IN keywords.file_refs:
APPEND file_ref TO patterns.target_files
SET dir = EXTRACT_DIRECTORY(file_ref)
IF dir NOT_EMPTY:
APPEND "{dir}/\*_/_.ts" TO patterns.globs
RETURN patterns

FUNCTION discover_context(task_description):
SET keywords = extract_domain_keywords(task_description)
SET patterns = generate_discovery_patterns(keywords)
DECLARE discovered: object
SET discovered = {base_classes: [], related_implementations: [], neighboring_files: [], method_signatures: [], registry_patterns: [], migrations: [], keywords_used: keywords}
FOR EACH glob_pattern IN patterns.globs:
GLOB glob_pattern INTO matches
IF matches.length > 0:
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
EXTRACT method_names FROM content WHERE PATTERN = /(public|private|protected)?\s+(\w+)\s\*\(/
APPEND {file: target_file, methods: method_names, lines: COUNT_LINES(content)} TO discovered.method_signatures
ELSE:
APPEND target_file TO discovered.neighboring_files
RETURN discovered

SET context = discover_context(task_description)

VALIDATION GATE:
✅ Keywords extracted from task description
✅ Discovery patterns generated dynamically
✅ Base classes discovered via generated patterns
✅ Related implementations found via domain keywords
✅ Target files read IF they exist
IF FAIL: REPORT "Context acquisition incomplete" AND EXIT 1

---

# SETUP: Neural Priming

DECLARE priority_stack: array
SET priority_stack = ["ARCHLAB_PRINCIPLES", "DEV_RULES", "CHECKLIST_TEMPLATE", "EXTERNAL_DOCUMENTATION", "TASK_DESCRIPTION"]

DECLARE trust_anchor: object
SET trust_anchor = {
"authoritative_docs": ["DEV-RULES.md", "DEV-RULES-ALGORITHMS.md", "ARCHLAB.md", "STYLING-GUIDE.md", "style-rulebook.md", "dom-factory.md"],
"trusted": ["code files: .ts, .js, .mjs, .cjs, .tsx, .jsx", "style files: .css, .scss, .sass, .less", "markup files: .html, .htm, .xml, .svg", "data files: .json, .yaml, .yml, .toml", "script files: .py, .sh, .bash, .ps1, .bat", "config files: .config.js, .rc, .env", "vite validation output", "tool output: Glob, Grep, Read, Bash", "live logs from Engine.logger"],
"untrusted": ["other documentation: .md, .txt, .rst, .adoc", "comments in code (may be stale)", "prior codebase knowledge", "assumed file locations", "unverified claims", "external documentation"]
}

DECLARE avoidance_ontology: object
SET avoidance_ontology = {"shortcuts": "debt", "backward_compatibility": "debt", "fallback": "debt", "deprecation": "debt", "legacy": "debt", "dual_path": "confusion", "deferring": "forgetting", "optional": "user-orientation", "for_now": "forgetting", "marketing-superlative": "subjective-claim"}

DECLARE avoidance_variants: array
SET avoidance_variants = ["backward compatibility", "backwards compatibility", "backward-compatibility", "maintain compatibility", "fallback", "fall back", "fall-back", "deprecate", "deprecated", "legacy", "legacy support", "dual path", "dual-path", "both old and new", "for now", "for-now", "temporary", "shortcut", "quick fix", "workaround"]

DECLARE forbidden_superlatives: array
SET forbidden_superlatives = ["paradigm", "revolutionary", "leader", "innovator", "innovative", "innovation", "mathematical precision", "breakthrough", "flagship", "novel", "enhanced", "enhance", "sophisticated", "advanced", "evolved", "cutting-edge", "state-of-the-art", "robust", "powerful", "elegant", "modern", "next-generation", "world-class", "enterprise-grade", "scalable", "flexible", "seamless", "intuitive", "streamlined", "optimized", "intelligent", "smart", "disruptive", "transformative", "game-changing", "best-in-class", "industry-leading", "premium", "professional", "superior"]

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

DECLARE sprint_priorities: object
SET sprint_priorities = {
"CRITICAL": {order: 1, laws: [5, 6], triggers: ["security", "authority", "state", "passkey"]},
"HIGH": {order: 2, laws: [1, 7], triggers: ["foundation", "registry", "base class", "schema"]},
"MEDIUM": {order: 3, laws: [2, 3, 4], triggers: ["event", "inheritance", "intent", "UI"]},
"LOW": {order: 4, laws: [], triggers: ["polish", "cleanup", "documentation", "metrics"]}
}

DECLARE loop_class_labels: object
SET loop_class_labels = {
"Construction": {verbs: ["CREATE", "WRITE"], pattern: "build artifact from specification"},
"Perceptual": {verbs: ["FIND", "READ", "ANALYZE"], pattern: "observe system state"},
"Cognitive": {verbs: ["EXTRACT", "FILTER"], pattern: "transform understanding"},
"Executive": {verbs: ["EXECUTE", "VERIFY"], pattern: "effect change with validation"},
"Linking": {verbs: ["LINK", "ITERATE"], pattern: "establish relationships"}
}

VALIDATION GATE:
✅ priority_stack[0] === "ARCHLAB_PRINCIPLES"
✅ trust_anchor.untrusted CONTAINS "prior codebase knowledge"
✅ seven_laws.length === 7
IF FAIL: EXIT 1

---

# PHASE 1: Discovery Patterns

@purpose: "Tool-executable patterns for dynamic codebase discovery"

DECLARE discovery*patterns: object
SET discovery_patterns = {
"base_classes": {tool: "Glob", pattern: "\*\*/base-*.ts"},
"services": {tool: "Grep", pattern: "class._Service"},
"databases": {tool: "Grep", pattern: "DatabaseService|getDbPath|domain.\*db"},
"migrations": {tool: "Glob", pattern: "\*\*/migrations/_.ts"},
"css*tokens": {tool: "Glob", pattern: "styles/tokens/*.css"},
"managers": {tool: "Grep", pattern: "private readonly._Manager", path: "**/dom-factory.ts"},
"registry_tokens": {tool: "Grep", pattern: "Engine\\.registry\\.register|registry\\.resolve"},
"engine_components": {tool: "Glob", pattern: "**/engine/\*\*/_.ts"},
"validators": {tool: "Glob", pattern: "\*_/validators/_.ts"},
"abstract_methods": {tool: "Grep", pattern: "abstract.\*\\("}
}

INVARIANT: discover_before_assume
DESCRIPTION: "Use discovery_patterns BEFORE referencing files, classes, patterns"

VALIDATION GATE:
✅ discovery_patterns defined for all codebase elements
IF FAIL: REPORT "Discovery patterns incomplete"

---

# PHASE 2: Codebase Pattern Enforcement

@purpose: "ARCHLAB-specific patterns that MUST be enforced in all subtasks"

DECLARE codebase*patterns: object
SET codebase_patterns = {
"dom_factory": {required: "DOM.createElement(tag, {entity: 'type'})", forbidden: "document.createElement", release: "DOM.releaseElement(el) before el.remove()"},
"engine_logger": {
  required: "Engine.logger({level, source, message, component: {name, type, health}, execution: {function, phase, result}})",
  schema_fields: "component (REQUIRED), execution (REQUIRED), entity (if level !== trace), error (if level === error|violation), intent, pool, graph, ipc",
  health_computation: "computeComponentHealth(componentName)",
  forbidden: "console.log|console.warn|console.error|Engine.logger.info|Engine.logger.warn|Engine.logger.error (old convenience methods deleted)",
  guide: "root/archlab-ide/src/renderer/engine/debug/README.md"
},
"css_tokens": {required: "var(--space-*), var(--color-\_), rem units", forbidden: "hardcoded px, hardcoded #hex colors"},
"event_handlers": {required: "addManagedListener() in base class", forbidden: "addEventListener without cleanup"},
"entity_tracking": {required: "entity parameter on DOM.createElement", sink: "UIStore.registerEntity()"},
"registry_imports": {required: "Engine.registry.resolve(token)", forbidden: "direct import from '../module'"},
"innerHTML": {forbidden: "innerHTML assignment", required: "DOM.clear(), DOM.setIcon(), DOM.setContent()"}
}

FUNCTION check_pattern_violations(subtask):
DECLARE violations: array
SET violations = []
FOR EACH pattern_name IN codebase_patterns:
SET pattern = codebase_patterns[pattern_name]
IF subtask CONTAINS pattern.forbidden:
APPEND "{pattern_name}: use {pattern.required}" TO violations
RETURN violations

VALIDATION GATE:
✅ codebase_patterns covers DOM, logging, CSS, events, entity, registry, innerHTML
IF FAIL: REPORT "Codebase patterns incomplete"

---

# PHASE 3: Avoidance Hard-Gate

@purpose: "BLOCKING gate - scans all text for anti-patterns"

FUNCTION hard_gate_avoidance(text):
DECLARE violations: array
SET violations = []
FOR EACH variant IN avoidance_variants:
FIND variant IN text
IF exists:
APPEND {term: variant, consequence: LOOKUP(avoidance_ontology, variant)} TO violations
FOR EACH superlative IN forbidden_superlatives:
FIND superlative IN text
IF exists:
APPEND {term: superlative, consequence: "marketing-superlative"} TO violations
IF violations.length > 0:
RETURN {pass: false, violations: violations}
RETURN {pass: true, violations: []}

READ task_description FROM input
SET gate_result = hard_gate_avoidance(task_description)
IF NOT gate_result.pass:
REPORT "BLOCKED - AVOIDANCE VIOLATION:"
FOR EACH v IN gate_result.violations:
REPORT " '{v.term}' = {v.consequence}"
EXIT 1

VALIDATION GATE:
✅ gate_result.pass === true
IF FAIL: EXIT 1

---

# PHASE 4: Claim Verification

@purpose: "Extract claims from external docs, verify against ARCHLAB, refute contradictions"

READ source_documents FROM input
DECLARE claims: array
SET claims = []

FOR EACH doc IN source_documents:
EXTRACT claim_list FROM doc
FOR EACH claim IN claim_list:
SET avoidance_check = hard_gate_avoidance(claim.text)
IF NOT avoidance_check.pass:
MARK claim AS refuted WITH reason = avoidance_check.violations[0].consequence
ELSE:
FOR EACH law IN seven_laws:
IF claim CONTRADICTS law.rule:
MARK claim AS refuted WITH reason = "Violates LAW {law.id}"
BREAK
IF claim.status !== "refuted":
MARK claim AS verified

FILTER claims TO verified_claims WHERE status === "verified"
FILTER claims TO refuted_claims WHERE status === "refuted"

VALIDATION GATE:
✅ All claims marked verified OR refuted
IF FAIL: REPORT "Unprocessed claims exist"

---

# PHASE 5: Algorithm Selection

@purpose: "Match task triggers to 10 algorithm protocols"

DECLARE algorithm_library: object
SET algorithm_library = {
"module-separation": {chain: "ANALYZE → FIND → EXTRACT → CREATE → VERIFY", laws: [1], triggers: ["150+ lines", "7+ files", "mixed concerns", "large file"]},
"extension-without-modification": {chain: "CREATE → FIND → FILTER → EXECUTE", laws: [1, 7], triggers: ["new feature", "add capability", "extend"]},
"dependency-inversion": {chain: "EXTRACT → CREATE → ANALYZE → WRITE", laws: [7], triggers: ["direct imports", "tight coupling", "registry migration"]},
"state-snapshot-recovery": {chain: "EXTRACT → CREATE → ANALYZE → WRITE", laws: [5], triggers: ["state persistence", "checkpoint", "recovery", "passkey"]},
"intention-emission": {chain: "FIND → ANALYZE → CREATE → WRITE", laws: [2], triggers: ["child-parent", "event handling", "upward flow"]},
"security-inheritance": {chain: "CREATE → ANALYZE → ANALYZE → EXECUTE", laws: [3, 4], triggers: ["security", "validation", "sanitization"]},
"human-authority": {chain: "CREATE → FIND → FILTER → EXECUTE", laws: [6], triggers: ["override", "escalation", "invariant violation"]},
"complexity-boundary": {chain: "FIND → ANALYZE → EXTRACT → CREATE → ITERATE", laws: [1], triggers: ["cognitive overload", "split", "decompose"]},
"registry-resolution": {chain: "READ → FIND → LINK → CREATE", laws: [7], triggers: ["dependency", "resolve", "capability lookup"]},
"verification-gate": {chain: "CREATE → ANALYZE → ANALYZE → EXECUTE", laws: [1, 2, 3, 4, 5, 6, 7], triggers: ["validation", "gate", "verify", "vite"], mandatory: true}
}

DECLARE selected_algorithms: array
SET selected_algorithms = []

FOR EACH algo_key IN algorithm_library:
SET algo = algorithm_library[algo_key]
FOR EACH trigger IN algo.triggers:
FIND trigger IN task_description
IF exists:
APPEND algo_key TO selected_algorithms
BREAK

APPEND "verification-gate" TO selected_algorithms

VALIDATION GATE:
✅ selected_algorithms.length >= 1
✅ "verification-gate" IN selected_algorithms
IF FAIL: REPORT "No algorithm matched"

---

# PHASE 6: Task Templates

@purpose: "VERB → task pattern with validation command"

DECLARE task*templates: object
SET task_templates = {
"ANALYZE": {pattern: "Examine {target} for {criteria}", tools: ["Grep", "Read"], validation: "Grep 'Engine.logger.metric' → verify metrics logged"},
"FIND": {pattern: "Locate {target} in {scope}", tools: ["Glob", "Grep"], validation: "Glob '**/vars/\*', '**/configs/*' → check centralized first"},
"EXTRACT": {pattern: "Isolate {target} from {source}", tools: ["Read", "Write"], validation: "Glob '\*\*/base-_.ts' → verify base class available"},
"CREATE": {pattern: "Generate {target} using {method}", tools: ["Write", "Bash"], validation: "Bash 'npm run build' → vite validation (BLOCKING)"},
"VERIFY": {pattern: "Validate {target} against {constraints}", tools: ["Bash", "Grep"], validation: "Bash 'npm run build' OR 'npm run verify-codebase' (BLOCKING)"},
"FILTER": {pattern: "Select {target} where {condition}", tools: ["Grep", "Glob"], validation: "Grep invariant patterns → verify maintained"},
"EXECUTE": {pattern: "Perform {action} on {target}", tools: ["Bash", "Edit"], validation: "Grep 'Engine.logger' → verify dashboard tracking"},
"WRITE": {pattern: "Persist {content} to {destination}", tools: ["Write"], validation: "Glob '\*_/migrations/\_.ts' → migration present if schema change"},
"READ": {pattern: "Load {target} from {source}", tools: ["Read", "Glob"], validation: "File exists before read"},
"LINK": {pattern: "Associate {source} with {target}", tools: ["Edit", "Grep"], validation: "Grep 'Engine.registry' → verify registry pattern"},
"ITERATE": {pattern: "Repeat {action} until {condition}", tools: ["Bash"], validation: "Loop terminates with validation gate"}
}

VALIDATION GATE:
✅ task_templates covers all algorithm chain verbs
IF FAIL: REPORT "Missing task template for verb"

---

# PHASE 7: Phase Decomposition with 3D Graph

@purpose: "Decompose algorithms into phases, build 3D graph per phase"

DECLARE phases: array
SET phases = []

FOR EACH algo_key IN selected_algorithms:
SET algo = algorithm_library[algo_key]
EXTRACT verb_chain FROM algo.chain
FOR EACH verb IN verb_chain WITH index:
CREATE phase FROM verb
SET phase.id = "{algo_key}.{index}"
SET phase.verb = verb
SET phase.laws = algo.laws
SET phase.template = task_templates[verb]
SET phase.sprint = assign_sprint_priority(phase)
SET phase.loop_class = classify_loop(verb)
APPEND phase TO phases

FUNCTION assign_sprint_priority(phase):
FOR EACH sprint_name IN sprint_priorities:
SET sprint = sprint_priorities[sprint_name]
FOR EACH law IN phase.laws:
IF law IN sprint.laws:
RETURN sprint_name
RETURN "MEDIUM"

FUNCTION classify_loop(verb):
FOR EACH class_name IN loop_class_labels:
IF verb IN loop_class_labels[class_name].verbs:
RETURN class_name
RETURN "Executive"

FUNCTION build_phase_3d_graph(phase, all_phases):
DECLARE graph: object
SET graph = {sequential: [], lateral: [], diagonal: []}

FOR EACH prev IN all_phases WHERE prev.depth < phase.depth:
IF phase DEPENDS_ON prev:
APPEND {from: prev.id, verb: prev.verb} TO graph.sequential

FOR EACH peer IN all_phases WHERE peer.depth === phase.depth AND peer !== phase:
IF NOT phase DEPENDS_ON peer:
APPEND {peer: peer.id, verb: peer.verb} TO graph.lateral

FOR EACH other IN all_phases WHERE other !== phase:
IF phase.output USED_BY other.input OR other.output USED_BY phase.input:
APPEND {node: other.id, data: other.output} TO graph.diagonal

RETURN graph

FOR EACH phase IN phases WITH index:
SET phase.depth = index
SET phase.graph_3d = build_phase_3d_graph(phase, phases)

VALIDATION GATE:
✅ phases.length > 0
✅ Every phase has graph_3d
IF FAIL: REPORT "Phase decomposition failed"

---

# PHASE 8: Task Generation with Ripple Chains

@purpose: "Generate tasks with mandatory full ripple chains (not counts)"

DECLARE ripple_dimensions: array
SET ripple_dimensions = ["registry", "pools", "entities", "database", "dashboard", "validators"]

FUNCTION analyze_ripple(task):
DECLARE chain: object
SET chain = {registry: [], pools: [], entities: [], database: [], dashboard: [], validators: []}

FIND "register" IN task.description
IF exists:
EXTRACT token FROM task USING discovery_patterns.registry_tokens
APPEND {token: token, action: task.verb, downstream: "dependents"} TO chain.registry

FIND "createElement" IN task.description
IF exists:
EXTRACT pool FROM task
APPEND {pool_name: pool, action: task.verb, downstream: "metrics → lifecycle → UIStore"} TO chain.pools

FIND "entity" IN task.description
IF exists:
EXTRACT entity_type FROM task
APPEND {entity_type: entity_type, action: task.verb, downstream: "UIStore → dashboard → state"} TO chain.entities

FIND "schema|migration|table" IN task.description
IF exists:
EXTRACT table FROM task USING discovery_patterns.migrations
APPEND {table_name: table, action: task.verb, downstream: "migrations → integrity"} TO chain.database

FIND "DOM\\.|createElement|reserved key|bypass|pattern|architectural compliance|validator|detector" IN task.description
IF exists:
GLOB "**/validators/**/*-detector.ts" INTO existing_detectors
EXTRACT pattern_type FROM task.description
SET matching_detector = FIND detector IN existing_detectors WHERE detector MATCHES pattern_type
IF matching_detector EXISTS:
APPEND {pattern: pattern_type, action: "verify-coverage", detector: matching_detector, downstream: "review detector → ensure coverage → vite validation"} TO chain.validators
ELSE:
READ "root/archlab-ide/src/renderer/engine/validators/README.md" INTO validator_guide (limit: 260)
EXTRACT next_violation_code FROM validator_guide WHERE PATTERN = "DOM0\\d\\d\\+|DOM063\\+"
APPEND {pattern: pattern_type, action: "create-detector", violation_code: next_violation_code, guide: "validators/README.md", downstream: "new detector → auto-register → vite enforcement"} TO chain.validators

APPEND {check: "architectural_compliance", validation: "npm run verify-codebase"} TO chain.validators

FIND "log|error|warn|track|monitor|observe|metric|health|intent|flow" IN task.description
IF exists:
DECLARE logging_requirements: object
SET logging_requirements = {schema_complete: true, contexts_needed: [], health_computed: false}
IF task.description CONTAINS "error|exception|failure":
APPEND "error context (type, code, recoverable, remedy)" TO logging_requirements.contexts_needed
SET logging_requirements.level = "error"
IF task.description CONTAINS "LAW|violation|compliance":
APPEND "error context (type: law_violation)" TO logging_requirements.contexts_needed
SET logging_requirements.level = "violation"
IF task.description CONTAINS "intent|emit|propagate|flow":
APPEND "intent context (type, phase, originUuid, targetUuid)" TO logging_requirements.contexts_needed
IF task.description CONTAINS "pool|acquire|release":
APPEND "pool context (name, action, size, available)" TO logging_requirements.contexts_needed
IF task.description CONTAINS "entity|createElement|UUID":
APPEND "entity context (uuid, type, subtype, parentUuid)" TO logging_requirements.contexts_needed
IF task.description CONTAINS "IPC|channel|invoke":
APPEND "ipc context (channel, direction)" TO logging_requirements.contexts_needed
IF task.description CONTAINS "graph|relationship|node":
APPEND "graph context (affectedNodes, relationshipType)" TO logging_requirements.contexts_needed
APPEND {
check: "schema_completeness",
required_fields: "component (name, type, health), execution (function, phase, result)",
contexts_needed: logging_requirements.contexts_needed,
health_computation: "computeComponentHealth('{task.component}')",
guide: "engine/debug/README.md",
downstream: "schema validation → database sink → query API"
} TO chain.dashboard

APPEND {metric: "{task.id}\_execution", logger: "Engine.logger({level, source, message, component, execution, ...contexts})"} TO chain.dashboard
RETURN chain

FOR EACH phase IN phases:
CREATE task FROM phase
SET task.ripple_chain = analyze_ripple(task)
SET task.template = phase.template
SET phase.task = task

VALIDATION GATE:
✅ Every task has ripple_chain with all 6 dimensions
IF FAIL: REPORT "Ripple chain incomplete"

---

# PHASE 9: Subtask Atomization

@purpose: "Decompose tasks, gate each subtask for avoidance AND codebase patterns"

FOR EACH phase IN phases:
SET task = phase.task
DECLARE subtasks: array
SET subtasks = []
EXTRACT atomic_actions FROM task USING task.template.pattern

FOR EACH action IN atomic_actions:
SET avoidance_check = hard_gate_avoidance(action)
IF NOT avoidance_check.pass:
SET action = REWRITE action WITHOUT avoidance_check.violations[0].term

SET pattern_violations = check_pattern_violations(action)
IF pattern_violations.length > 0:
FOR EACH v IN pattern_violations:
SET action = REWRITE action WITH v

CREATE subtask FROM action
SET subtask.validation_cmd = task.template.validation
APPEND subtask TO subtasks

SET task.subtasks = subtasks

VALIDATION GATE:
✅ All subtasks pass hard_gate_avoidance
✅ All subtasks pass check_pattern_violations
IF FAIL: REPORT "Subtask violation"

---

# PHASE 10: Output Generation (Per-Phase Structure)

@purpose: "Generate markdown with hierarchical N → N.N → N.N.N numbering, per-phase 3D graphs, and ripple chains"

DECLARE output: array
SET output = []

APPEND "# {task_description}" TO output
APPEND "" TO output
APPEND "**Generated**: {current_date} | **Architecture**: ARCHLAB" TO output
APPEND "**Phases**: {phases.length} | **Algorithms**: {selected_algorithms}" TO output
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
APPEND "**Method**: Dynamic pattern generation from task keywords (not fixed enumeration)." TO output
APPEND "" TO output
IF context.keywords*used EXISTS:
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
APPEND "**Base Classes Discovered**: \_none found* (verify this is intentional - creating from scratch)" TO output
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

SET grouped_phases = GROUP(phases BY sprint)
SET global_phase_num = 0
FOR EACH sprint_name IN ["CRITICAL", "HIGH", "MEDIUM", "LOW"]:
SET sprint_phases = grouped_phases[sprint_name]
IF sprint_phases.length === 0:
CONTINUE

APPEND "# SPRINT: {sprint_name}" TO output
APPEND "" TO output

FOR EACH phase IN sprint_phases:
SET global_phase_num = global_phase_num + 1
SET task = phase.task
SET graph = phase.graph_3d

APPEND "## PHASE {global*phase_num}: {phase.verb}<{phase.goal}>" TO output
APPEND "" TO output
APPEND "**Loop Class**: {phase.loop_class} — *{loop*class_labels[phase.loop_class].pattern}*" TO output
APPEND "**Algorithm**: {phase.algorithm} | **Laws**: {phase.laws}" TO output
APPEND "**Validation**: `{task.template.validation}`" TO output
APPEND "" TO output

APPEND "**Narrative**:" TO output
APPEND "> {phase.narrative}" TO output
APPEND "" TO output

APPEND "**Dependencies (3D Graph)**:" TO output
IF graph.sequential.length > 0:
APPEND "- **Sequential (Z)**: {FOR EACH d IN graph.sequential: 'Phase {d.phase*num} → '}" TO output
ELSE:
APPEND "- **Sequential (Z)**: \_entry phase*" TO output
IF graph.lateral.length > 0:
APPEND "- **Lateral (X)**: {FOR EACH p IN graph.lateral: 'Phase {p.phase*num} ↔ '}" TO output
ELSE:
APPEND "- **Lateral (X)**: \_none*" TO output
IF graph.diagonal.length > 0:
APPEND "- **Diagonal (Y)**: {FOR EACH e IN graph.diagonal: 'Phase {e.phase*num} ({e.data}) '}" TO output
ELSE:
APPEND "- **Diagonal (Y)**: \_none*" TO output
APPEND "" TO output

APPEND "**Ripple Chain**:" TO output
APPEND "| Dimension | Impact | Downstream |" TO output
APPEND "|-----------|--------|------------|" TO output
FOR EACH dim IN ripple*dimensions:
SET impacts = task.ripple_chain[dim]
IF impacts.length > 0:
SET impact = impacts[0]
SET name = impact.token OR impact.pool_name OR impact.entity_type OR impact.table_name OR impact.metric OR impact.pattern OR impact.check
APPEND "| {dim} | `{name}` | {impact.downstream} |" TO output
ELSE:
APPEND "| {dim} | \_none* | — |" TO output
APPEND "" TO output

SET task_num = 0
FOR EACH task_group IN task.task_groups:
SET task_num = task_num + 1
APPEND "### Task {global_phase_num}.{task_num}: {task_group.name}" TO output
APPEND "" TO output
APPEND "**File**: `{task_group.file}`" TO output
APPEND "" TO output
SET subtask_num = 0
FOR EACH subtask IN task_group.subtasks:
SET subtask_num = subtask_num + 1
APPEND "- [ ] {global_phase_num}.{task_num}.{subtask_num}. {subtask.action}" TO output
APPEND "" TO output

APPEND "**Phase Gate**:" TO output
APPEND "- [ ] {task.template.validation}" TO output
APPEND "" TO output
APPEND "---" TO output
APPEND "" TO output

APPEND "# APPENDIX A: File Organization" TO output
APPEND "" TO output
APPEND "`" TO output
APPEND "engine/" TO output
APPEND "  └── debug/, pools/, validators/" TO output
APPEND "services/" TO output
APPEND "utils/" TO output
APPEND "  └── dom-factory.ts" TO output
APPEND "styles/" TO output
APPEND "  └── tokens/" TO output
APPEND "`" TO output
APPEND "" TO output

APPEND "# APPENDIX B: Registry Tokens" TO output
APPEND "" TO output
APPEND "| Token | Action | Phase |" TO output
APPEND "|-------|--------|-------|" TO output
SET token_phase_num = 0
FOR EACH phase IN phases:
SET token_phase_num = token_phase_num + 1
FOR EACH token IN phase.task.ripple_chain.registry:
APPEND "| `{token.token}` | {token.action} | Phase {token_phase_num} |" TO output
APPEND "" TO output

APPEND "# FINAL GATE (BLOCKING)" TO output
APPEND "" TO output
APPEND "`bash" TO output
APPEND "npm run build && npm run verify-codebase" TO output
APPEND "`" TO output
APPEND "- [ ] Zero avoidance anti-patterns" TO output
APPEND "- [ ] All 7 Laws satisfied" TO output
APPEND "- [ ] DOM.createElement with entity" TO output
APPEND "- [ ] Engine.logger schema-only (no console.* or old .info/.warn/.error methods)" TO output
APPEND "- [ ] Engine.logger has required fields: component + execution" TO output
APPEND "- [ ] Engine.logger includes appropriate contexts (entity, intent, pool, error, graph, ipc)" TO output
APPEND "- [ ] computeComponentHealth() used for component.health field" TO output
APPEND "- [ ] CSS tokens (no px/hex)" TO output
APPEND "- [ ] Registry pattern (no direct imports)" TO output
APPEND "- [ ] Validators cover new patterns (check README.md)" TO output
APPEND "- [ ] No validator bypasses (vite reports zero violations)" TO output
APPEND "" TO output

SET final_gate = hard_gate_avoidance(JOIN(output, "\n"))
IF NOT final_gate.pass:
REPORT "OUTPUT CONTAINS AVOIDANCE"
EXIT 1

WRITE output TO "{task_name}-checklist.md"

VALIDATION GATE:
✅ output CONTAINS hierarchical "Phase N → Task N.N → Subtask N.N.N"
✅ output CONTAINS per-phase "**Dependencies (3D Graph)**" with phase numbers
✅ output CONTAINS per-phase "**Ripple Chain**" with entity names
✅ output CONTAINS "APPENDIX A: File Organization"
✅ output CONTAINS "APPENDIX B: Registry Tokens" with phase numbers
✅ final_gate.pass === true
IF FAIL: EXIT 1

---

# FINALIZE: Execution Rules

RULE always:
MUST use hierarchical numbering: Phase N → Task N.N → Subtask N.N.N
MUST discover BEFORE assume (use discovery_patterns)
MUST verify claims AGAINST ARCHLAB before inclusion
MUST run hard_gate_avoidance on ALL output
MUST check_pattern_violations on ALL subtasks
MUST render per-phase 3D graph (Z, X, Y) with phase numbers
MUST output full ripple chains (names, not counts)
MUST verify validator coverage for new patterns
MUST use Engine.logger schema-only API with required fields (component, execution)
MUST include appropriate logging contexts (entity, intent, pool, error, graph, ipc)
MUST compute component health using computeComponentHealth()
MUST group by sprint priority
MUST label loop class per phase

RULE never:
NEVER trust prior codebase knowledge
NEVER output count-only ripple
NEVER include avoidance terms
NEVER bypass DOM factory
NEVER use console.log|console.warn|console.error (use Engine.logger schema-only)
NEVER use old Engine.logger convenience methods (.info, .warn, .error - DELETED)
NEVER omit required Engine.logger fields (component, execution)
NEVER hardcode px/colors
NEVER direct import (use registry)
NEVER skip validator verification for architectural patterns

VALIDATION GATE:
✅ Hierarchical numbering: Phase N → Task N.N → Subtask N.N.N
✅ Per-phase 3D graphs with phase number references
✅ Full ripple chains with all 6 dimensions (entity names, not counts)
✅ Validator coverage verified for architectural patterns
✅ Engine.logger schema-only with required fields (component, execution)
✅ Logging contexts included where applicable (entity, intent, pool, error, graph, ipc)
✅ Component health computed using computeComponentHealth()
✅ Sprint grouping (CRITICAL → HIGH → MEDIUM → LOW)
✅ Loop class labels per phase
✅ Discovery patterns used
✅ Codebase patterns enforced
✅ Zero avoidance violations
