# MANDATORY GATES - READ ON EVERY MESSAGE

## AVOID → ENFORCE Mapping

| AVOID                       | Reason                 | ENFORCE                  | Reason                  |
| --------------------------- | ---------------------- | ------------------------ | ----------------------- |
| shortcuts                   | debt                   | constraints              | leverage                |
| backward_compatibility      | debt                   | forward_compatibility    | compounding             |
| fallback                    | debt                   | fail-fast                | clarity                 |
| deprecation                 | debt                   | explicit-removal         | coherence               |
| legacy                      | debt                   | greenfield               | clarity                 |
| dual-path                   | confusion              | single-path              | determinism             |
| deffering                   | forgetting             | immediacy                | continuity              |
| optional                    | user-orientation       | mandatory                | system-orientation      |
| for_now                     | deffering → forgetting | now                      | immediacy → continuity  |
| unobserved-execution        | missed-learning        | observed-execution       | learning                |
| pattern-without-compression | inefficiency           | pattern-with-compression | efficiency              |
| evolution-without-approval  | architectural-drift    | evolution-with-approval  | architectural-integrity |
| feedback-ignored            | stagnation             | feedback-enforced        | adaptation              |
| shared-ownership            | ambiguity              | single-owner             | accountability          |
| unbounded-lifetime          | leaks                  | bounded-lifetime         | deterministic-release   |
| asymmetric-lifecycle        | resource-leaks         | enforced-symmetry        | guaranteed-cleanup      |
| implicit-retention          | hidden-leaks           | explicit-retention       | observable-ownership    |
| discipline-release          | human-error            | structural-release       | automatic-enforcement   |
| mutable-state               | unpredictability       | immutable-data           | reproducibility         |
| silent-errors               | unknown-failure        | errors-as-language       | machine-processable     |
| hidden-invalidity           | false-consistency      | explicit-invalidity      | honest-uncertainty      |
| parent-callbacks            | tight-coupling         | event-emission           | decoupling              |
| retraction                  | complexity             | monotonic-growth         | append-only-scalability |
| location-addressing         | brittleness            | semantic-addressing      | meaning-based-reference |
| timestamp-ordering          | wall-clock-dependency  | ordinal-time             | logical-sequencing      |
| separation                  | duplication            | homoiconicity            | code-data-uniformity    |
| unlimited-complexity        | cognitive-overload     | bounded-complexity       | human-comprehension     |
| metric-health               | symptom-tracking       | computed-health          | symbolic-diagnosis      |

VALIDATION_GATE: Mapping
✅ READ AVOID Reason | ENFORCE Reason
✅ VERIFY mapping is instructional context

## HEARTBEAT PROTOCOL (Execute immediately after reading this file)

1. FIND the current "READ .claude/GATES.md (N)" todo item
2. CREATE new todo: "READ .claude/GATES.md (N+1)" with status: pending
3. MARK the old (N) item as completed
4. PROCEED with gates below

This creates an unbreakable chain - if no pending READ todo exists, re-initialize from (1).

## STATE TRACKING (Prevents Semantic Drift)

DECLARE session_state: object
SET session_state = {
gates_traversed: 0,
last_reads: {
"DEV-RULES.md": null,
"ARCHLAB.md": null,
"STYLING-GUIDE.md": null,
"style-rulebook.md": null,
"dom-factory.md": null
},
refresh_interval: 5
}

DECLARE architectural_documents: array
SET architectural_documents = [
{path: ".claude/_DEV-RULES.md", priority: "CRITICAL", scope: "principles"},
{path: ".claude/_ARCHLAB.md", priority: "CRITICAL", scope: "architecture"},
{path: "root/archlab-ide/src/renderer/styles/STYLING-GUIDE.md", priority: "HIGH", scope: "styling"},
{path: "root/archlab-ide/docs/style-rulebook.md", priority: "HIGH", scope: "styling"},
{path: "root/archlab-ide/docs/dom-factory/dom-factory.md", priority: "HIGH", scope: "dom"}
]

FUNCTION check_document_freshness():
INCREMENT session_state.gates_traversed

FOR EACH doc IN architectural_documents:
SET last_read = session_state.last_reads[doc.path]
SET distance = session_state.gates_traversed - last_read

    IF last_read === null:
      # Never read - immediate refresh
      READ doc.path INTO context
      SET session_state.last_reads[doc.path] = session_state.gates_traversed
      TodoWrite: ADD "REFRESHED {doc.path} @ gate {session_state.gates_traversed}" WITH status: "completed"
    ELSE IF distance >= session_state.refresh_interval:
      # Stale - periodic refresh (every 5 gates)
      READ doc.path INTO context
      SET session_state.last_reads[doc.path] = session_state.gates_traversed
      TodoWrite: ADD "REFRESHED {doc.path} @ gate {session_state.gates_traversed}" WITH status: "completed"

EXECUTE check_document_freshness() BEFORE gates below

INVARIANT: All architectural documents refreshed within last 5 gate traversals
OBSERVABLE: Todo list contains "REFRESHED {doc} @ gate N" entries every ~5 messages
PROOF: Distance between refresh entries ≤ 5

## GATES (Execute sequentially before responding)

GATE 0: EXTRACT claims from this message
GATE 1: DISCOVER relevant tools in .claude/workspace/tools/ (reuse before creating)
GATE 2: VERIFY claims via tools (Glob/Grep/Read) or scripts (Node/Python) - NEVER trust assumptions
GATE 3: RESPOND only from verified evidence
GATE 4: REPORT refuted claims explicitly before proceeding

## ACTIONS (Conditional, triggered by gates)

IF GATE 2 used script → MOVE script to .claude/workspace/tools/ for reuse
IF GATE 4 has refuted claims → LIST them before response content

## TRUST ANCHOR

TRUSTED:

- Tool output reflects actual state
- File system is observable via Glob/Grep/Read
- Scripts in .claude/workspace/tools/ follow DEV-RULES.md

UNTRUSTED:

- My prior knowledge of this codebase
- User claims about file locations
- Assumptions about what exists

## VERIFICATION DISCIPLINE

ON EVERY RESPONSE:

1. What claims did the message contain? (GATE 0)
2. What tools exist for verification? (GATE 1)
3. Did I verify with tools/scripts? (GATE 2)
4. Am I responding from evidence? (GATE 3)
5. Are there refuted claims to report? (GATE 4)

## INVARIANTS

- Silence after verification = valid
- Content without verification = violation
- Discovery before assumption
- Evidence before claim
- Reuse before create (tools)

## Software Development

- Architectural documents refreshed per STATE TRACKING (periodic refresh every 5 gates)
- Apply rules from DEV-RULES.md & ARCHLAB.md to code infrastructure/architecture
- VERIFY CODEBASE with "npm run verify-codebase" from D:\GIT\archlab\ after Code changes
