---
name: pag-syntax
description: Use when writing PAG files, understanding PAG grammar, converting natural language to executable control flow, validating PAG syntax, or fixing PAG syntax errors
allowed-tools: Read, Write, Edit
---

# PURPOSE

Teach PAG syntax, grammar, and executable control flow patterns for writing AI instructions.

# CORE SYNTAX PATTERN

DECLARE pattern: string
SET pattern = "VERB target PREPOSITION source [INTO destination]"

EXAMPLES:
- READ "config.json" INTO data
- EXTRACT email FROM customer_data INTO email_value
- ANALYZE email_value AGAINST email_pattern
- WRITE validation_result TO output
- FOR EACH item IN collection:
- IF condition:
- VALIDATION GATE:

# KEYWORD CATEGORIES

DECLARE keyword_map: object
SET keyword_map = {
  "Action": ["READ", "WRITE", "EXECUTE", "CREATE", "ANALYZE", "VALIDATE", "EXTRACT", "COLLECT", "FILTER", "GLOB", "GREP"],
  "Control_Flow": ["IF", "ELSE", "FOR EACH", "WHILE", "TRY", "CATCH", "WHEN", "MATCH", "UNLESS"],
  "Declaration": ["SET", "DECLARE", "DEFINE"],
  "Modifier": ["MUST", "NEVER", "ALWAYS", "REQUIRED", "FORBIDDEN"],
  "Coordination": ["AWAIT", "PARALLEL", "DELEGATE", "TASK", "TODO_WRITE", "ASK_USER"],
  "Validation": ["ASSERT", "REQUIRE", "VALIDATION GATE"]
}

# STRUCTURAL RULES

WHEN writing_pag_document:
  STRUCTURE AS:
    1. Frontmatter (YAML metadata)
    2. Document declaration: "THIS {TYPE} {VERB} {description}"
    3. %% META %% block (optional)
    4. ON ERROR handlers (optional)
    5. DECLARE statements (variables)
    6. # PHASE N: Title (with body + VALIDATION GATE)
    7. ALWAYS/NEVER constraints

WHEN writing_phases:
  FORMAT AS:
    # PHASE 1: Phase Name
        @purpose: "what this phase does"

        DECLARE variables: type
        SET variable = value

        FOR EACH item IN collection:
            ACTION item

        VALIDATION GATE:
            ✅ Specific condition verified
            ✅ Another condition verified
            IF FAIL: recovery_action

# COMMON PATTERNS

WHEN validation_gate:
  WRITE AS:
    VALIDATION GATE:
        ✅ data loaded successfully
        ✅ schema validation passed
        ASSERT record_count > 0
        IF FAIL: REPORT "failure message"

WHEN iteration:
  USE "FOR EACH item IN collection:" NOT "FOR item IN collection:"

WHEN conditional:
  USE "IF condition:" NOT "if condition" (uppercase + colon)

WHEN error_handling:
  PATTERN:
    TRY:
        BACKUP file TO file.bak
        WRITE content TO file
        DELETE file.bak
    CATCH:
        RESTORE FROM file.bak

WHEN tool_invocation:
  BASIC: READ "file.json"
  WITH_BINDING: READ "file.json" INTO data
  WITH_PARAMS: GREP "pattern" WITH path: "src/", output_mode: "content"
  TASK: TASK "analyze code" WITH subagent_type: "Explore" -> result

# TYPE ANNOTATIONS

DECLARE types: array
SET types = ["string", "number", "boolean", "array", "object", "file", "context"]

EXAMPLE:
  DECLARE config: object
  DECLARE items: array
  DECLARE counter: number

# COMMON MISTAKES

WHEN user WRITES "Get the customer data":
  CORRECT TO: READ customer_data FROM database

WHEN user WRITES "✅ data looks good":
  CORRECT TO: "✅ data.email matches email_pattern"

WHEN user WRITES "FOR item IN collection:":
  CORRECT TO: "FOR EACH item IN collection:"

WHEN user WRITES "if condition":
  CORRECT TO: "IF condition:"

WHEN user WRITES markdown_list:
  CORRECT TO: PAG_directives (no "* List item", use "APPEND item TO list")

# INDENTATION

ALWAYS use 4 spaces per level
NEVER mix tabs and spaces
ALWAYS indent directives under control flow

EXAMPLE:
  IF condition:
      EXECUTE action
      SET variable = value
  ELSE:
      REPORT failure

# DOCUMENT TYPES

DECLARE doc_types: object
SET doc_types = {
  "AGENT": "PERFORMS",
  "WORKFLOW": "EXECUTES",
  "PROTOCOL": "DEFINES",
  "POLICY": "ENFORCES",
  "CHECKLIST": "PROVIDES",
  "INSTRUCTION": "IS"
}

USAGE: "THIS WORKFLOW EXECUTES multi-phase data processing"

# VALIDATION

WHEN validating_pag_syntax:
  CHECK keywords_uppercase: ["READ", "SET", "IF", "FOR EACH"]
  CHECK control_flow_colons: "IF condition:", "FOR EACH item IN list:"
  CHECK declarations_before_usage: DECLARE before SET/READ
  CHECK validation_gates_present: every phase ends with gate
  CHECK indentation: 4 spaces consistent
  CHECK no_forward_references: phase N cannot use phase N+1 output

# ALWAYS / NEVER RULES

ALWAYS use uppercase keywords
ALWAYS include colon after control flow (IF:, FOR EACH:, WHEN:)
ALWAYS use "FOR EACH" not "FOR" for iteration
ALWAYS declare variables before first use
ALWAYS end phases with VALIDATION GATE

NEVER use lowercase keywords
NEVER skip EACH in FOR EACH
NEVER use prose instead of directives
NEVER use vague validation conditions
NEVER create forward phase references

# REFERENCES

**Authoritative**:
- `.pag-docs/grammar.md` - Complete BNF grammar (5 rule categories)
- `.pag-docs/keywords.md` - Keyword ontology (14 semantic categories)
- `.pag-docs/guide.md` - Practical patterns and examples

**Related Skills**:
- `orchestration-patterns` - Uses PAG for agent sequencing
- `handoff-protocols` - Uses PAG for handoff signals
- `workflow-coordination` - Uses PAG for validation gates
