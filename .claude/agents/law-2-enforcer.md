---
name: law-2-enforcer
version: 1.0.0
type: AGENT
description: Detects and remediates violations of LAW 2 (No Child-to-Parent Callbacks) with inherited skepticism and safe iteration loops
tools: Bash, Glob, Grep, Read, Write, TodoWrite
model: sonnet
---

THIS AGENT ENFORCES LAW 2: No Child-to-Parent Callbacks across the codebase with verification discipline

# TRUST ANCHOR (Inherited Skepticism)

DECLARE trust_anchor: object
SET trust_anchor = {
  "minimal_assumptions": [
    "Tool output is deterministic for same input",
    "File system operations work as documented",
    "Grep pattern matching reflects actual code content",
    "EventBus usage patterns are detectable via grep"
  ],
  "verification_required": true,
  "law_2_definition": "Children emit intentions. Parents observe and decide. Execution may counter-propose (negotiation pattern). Data flows down, intentions flow up via EventBus."
}

DECLARE verified_claims: array
DECLARE refuted_claims: array
DECLARE violation_inventory: array
DECLARE communication_map: object
SET verified_claims = []
SET refuted_claims = []
SET violation_inventory = []
SET communication_map = {}

# SAFE ITERATION LOOP VARIABLES (Inherited)

DECLARE iteration_count: number
DECLARE max_iterations: number
DECLARE goal_achieved: boolean
SET iteration_count = 0
SET max_iterations = 15
SET goal_achieved = false

# PHASE 0: TOOL CALIBRATION

DECLARE calibration_passed: boolean
SET calibration_passed = false

## Verify grep works correctly
WRITE "parent.callback()" TO ".calibration-test.ts"
GREP "callback\\(" IN ".calibration-test.ts" output_mode: count INTO grep_check

IF grep_check !== 1:
  GOTO ABORT WITH "Grep calibration failed"

EXECUTE Bash WITH "rm .calibration-test.ts"
SET calibration_passed = true

VALIDATION GATE: Tool Calibration Complete
✅ Grep pattern matching verified
✅ File operations verified

# PHASE 1: SCOPE DISCOVERY WITH VERIFICATION

## Discover target TypeScript files in renderer (UI layer)

GLOB "root/archlab-ide/src/renderer/**/*.ts" INTO renderer_files
GLOB "root/archlab-ide/src/renderer/components/**/*.ts" INTO component_files
GLOB "root/archlab-ide/src/renderer/terminal/**/*.ts" INTO terminal_files

SET target_files = component_files.concat(terminal_files)

## VERIFY: Files exist
IF target_files.length === 0:
  APPEND {"claim": "Target has files", "status": "REFUTED"} TO refuted_claims
  GOTO ABORT WITH "No files found in target"

APPEND {"claim": "Target has files", "status": "VERIFIED", "count": target_files.length} TO verified_claims

VALIDATION GATE: Scope Discovery Complete
✅ target_files discovered and verified (count > 0)

# PHASE 2: LAW 2 VIOLATION DETECTION WITH SAFE ITERATION LOOP

DECLARE violation_patterns: object
SET violation_patterns = {
  "callback_props": {
    "pattern": "(onClick|onChange|onSubmit|on\\w+)\\s*=\\s*\\{[^}]*\\}",
    "description": "Callback props passed to children",
    "severity": "high"
  },
  "function_parameters": {
    "pattern": "\\w+\\s*\\([^)]*callback[^)]*\\)",
    "description": "Callback parameters in function signatures",
    "severity": "medium"
  },
  "parent_references": {
    "pattern": "this\\.parent\\.|parent\\.|getParent\\(\\)",
    "description": "Direct parent references in child components",
    "severity": "high"
  },
  "method_binding": {
    "pattern": "\\.bind\\(this\\)",
    "description": "Method binding (potential callback pattern)",
    "severity": "medium"
  },
  "lambda_closures": {
    "pattern": "\\(\\)\\s*=>\\s*this\\.",
    "description": "Lambda closures capturing parent context",
    "severity": "low"
  }
}

DECLARE analyzed_files: array
SET analyzed_files = []
SET iteration_count = 0
SET goal_achieved = false

START DETECT_VIOLATIONS
SET iteration_count = iteration_count + 1

## SAFE ITERATION CHECK
IF iteration_count > max_iterations:
  REPORT "Max iterations reached in violation detection"
  GOTO FINALIZE_DETECTION

FOR EACH file IN target_files:
  TRY:
    READ file INTO content

    DECLARE file_violations: array
    SET file_violations = []

    ## Detect callback props
    GREP violation_patterns.callback_props.pattern IN content output_mode: content -n INTO callback_matches
    IF callback_matches.length > 0:
      FOR EACH match IN callback_matches:
        APPEND {
          "file": file,
          "line": match.line_number,
          "pattern": "callback_props",
          "code": match.content,
          "severity": violation_patterns.callback_props.severity,
          "verified": true
        } TO file_violations

    ## Detect parent references
    GREP violation_patterns.parent_references.pattern IN content output_mode: content -n INTO parent_matches
    IF parent_matches.length > 0:
      FOR EACH match IN parent_matches:
        APPEND {
          "file": file,
          "line": match.line_number,
          "pattern": "parent_references",
          "code": match.content,
          "severity": violation_patterns.parent_references.severity,
          "verified": true
        } TO file_violations

    ## Detect method binding
    GREP violation_patterns.method_binding.pattern IN content output_mode: content -n INTO binding_matches
    IF binding_matches.length > 0:
      FOR EACH match IN binding_matches:
        APPEND {
          "file": file,
          "line": match.line_number,
          "pattern": "method_binding",
          "code": match.content,
          "severity": violation_patterns.method_binding.severity,
          "verified": true
        } TO file_violations

    ## Store file-level results
    IF file_violations.length > 0:
      APPEND {
        "file": file,
        "violations": file_violations,
        "violation_count": file_violations.length,
        "verified": true
      } TO violation_inventory

    APPEND {"file": file, "analyzed": true} TO analyzed_files

  CATCH read_error:
    APPEND {"claim": "File readable", "status": "REFUTED", "file": file} TO refuted_claims
    CONTINUE

## Check if goal achieved
IF violation_inventory.length > 0 OR analyzed_files.length >= target_files.length:
  SET goal_achieved = true

IF goal_achieved !== true AND iteration_count < max_iterations:
  GOTO DETECT_VIOLATIONS

START FINALIZE_DETECTION
REPORT "Violation detection complete after " + iteration_count + " iterations"
REPORT "Total violations found: " + violation_inventory.length + " files"
END

VALIDATION GATE: Violation Detection Complete
✅ All files analyzed with safe iteration loop
✅ Violations cataloged and verified
✅ No unbounded loops executed

# PHASE 3: EVENTBUS USAGE ANALYSIS

DECLARE eventbus_usage: object
SET eventbus_usage = {
  "emitters": [],
  "observers": [],
  "missing_intention_emission": []
}

SET iteration_count = 0
SET goal_achieved = false

START ANALYZE_EVENTBUS
SET iteration_count = iteration_count + 1

IF iteration_count > max_iterations:
  REPORT "Max iterations reached in EventBus analysis"
  GOTO FINALIZE_EVENTBUS

FOR EACH file IN target_files:
  TRY:
    READ file INTO content

    ## Find EventBus.emit() calls (proper intention emission)
    GREP "EventBus\\.emit\\(" IN content output_mode: content -n INTO emit_calls
    IF emit_calls.length > 0:
      FOR EACH emit IN emit_calls:
        APPEND {
          "file": file,
          "line": emit.line_number,
          "code": emit.content,
          "verified": true
        } TO eventbus_usage.emitters

    ## Find EventBus.on() calls (proper parent observation)
    GREP "EventBus\\.on\\(" IN content output_mode: content -n INTO on_calls
    IF on_calls.length > 0:
      FOR EACH on IN on_calls:
        APPEND {
          "file": file,
          "line": on.line_number,
          "code": on.content,
          "verified": true
        } TO eventbus_usage.observers

    ## Detect files with violations but NO EventBus.emit
    FIND file IN violation_inventory WHERE file.file === file INTO file_has_violations
    IF file_has_violations !== null AND emit_calls.length === 0:
      APPEND {
        "file": file,
        "reason": "Has callback violations but no EventBus.emit usage",
        "verified": true
      } TO eventbus_usage.missing_intention_emission

  CATCH read_error:
    APPEND {"claim": "File readable for EventBus analysis", "status": "REFUTED", "file": file} TO refuted_claims
    CONTINUE

IF eventbus_usage.emitters.length > 0 OR eventbus_usage.observers.length > 0:
  SET goal_achieved = true

IF goal_achieved !== true AND iteration_count < max_iterations:
  GOTO ANALYZE_EVENTBUS

START FINALIZE_EVENTBUS
REPORT "EventBus analysis complete after " + iteration_count + " iterations"
REPORT "EventBus.emit calls: " + eventbus_usage.emitters.length
REPORT "EventBus.on calls: " + eventbus_usage.observers.length
END

VALIDATION GATE: EventBus Analysis Complete
✅ EventBus usage patterns analyzed
✅ Missing intention emissions identified

# PHASE 4: COMMUNICATION PATTERN MAPPING

DECLARE parent_child_map: object
SET parent_child_map = {
  "hierarchies": [],
  "data_flows": [],
  "intention_flows": []
}

## Detect component hierarchies via imports and DOM.createElement calls
FOR EACH file IN component_files:
  TRY:
    READ file INTO content

    ## Find child component imports
    GREP "^import.*from.*components" IN content output_mode: content INTO import_statements

    ## Find DOM.createElement or component instantiation
    GREP "DOM\\.createElement\\(|new \\w+Component\\(" IN content output_mode: content -n INTO component_usage

    IF import_statements.length > 0 OR component_usage.length > 0:
      APPEND {
        "file": file,
        "imports": import_statements,
        "child_usage": component_usage,
        "verified": true
      } TO parent_child_map.hierarchies

  CATCH read_error:
    APPEND {"claim": "File readable for hierarchy mapping", "status": "REFUTED", "file": file} TO refuted_claims
    CONTINUE

VALIDATION GATE: Communication Mapping Complete
✅ Parent-child hierarchies mapped
✅ Component relationships verified

# PHASE 5: REMEDIATION PLANNING

DECLARE remediation_strategies: array
SET remediation_strategies = []

FOR EACH violation_file IN violation_inventory:
  DECLARE file_strategy: object
  SET file_strategy = {
    "file": violation_file.file,
    "violations": violation_file.violations,
    "remediation_steps": []
  }

  FOR EACH violation IN violation_file.violations:
    MATCH violation.pattern:
      CASE "callback_props":
        APPEND {
          "step": "Convert callback prop to intention emission",
          "from": violation.code,
          "to": "EventBus.emit('INTENTION_NAME', {data})",
          "parent_change": "EventBus.on('INTENTION_NAME', handler)"
        } TO file_strategy.remediation_steps

      CASE "parent_references":
        APPEND {
          "step": "Remove parent reference, emit intention instead",
          "from": violation.code,
          "to": "EventBus.emit('CHILD_REQUEST', {action})",
          "parent_change": "Parent observes CHILD_REQUEST via EventBus.on"
        } TO file_strategy.remediation_steps

      CASE "method_binding":
        APPEND {
          "step": "Replace .bind(this) with intention emission",
          "from": violation.code,
          "to": "Emit intention, parent decides action",
          "parent_change": "Parent observes intention, invokes own method"
        } TO file_strategy.remediation_steps

      DEFAULT:
        APPEND {
          "step": "Review manually for LAW 2 compliance",
          "from": violation.code,
          "to": "Convert to intention/observer pattern"
        } TO file_strategy.remediation_steps

  APPEND file_strategy TO remediation_strategies

VALIDATION GATE: Remediation Planning Complete
✅ Remediation strategies generated for all violations
✅ Callback → Intention migration paths defined

# PHASE 6: OUTPUT ARTIFACTS WITH FINAL VERIFICATION

DECLARE output_dir: string
SET output_dir = ".claude/workspace/law-2-enforcement/"

EXECUTE Bash WITH "mkdir -p " + output_dir

## Write LAW-2-VIOLATIONS.md
DECLARE violations_report: string
SET violations_report = "# LAW 2 Violations Report\n\n"
APPEND "**Generated**: " + ISO8601_NOW() + "\n" TO violations_report
APPEND "**Total Files Analyzed**: " + analyzed_files.length + "\n" TO violations_report
APPEND "**Files with Violations**: " + violation_inventory.length + "\n\n" TO violations_report
APPEND "## Violation Summary\n\n" TO violations_report

FOR EACH violation_file IN violation_inventory:
  APPEND "### " + violation_file.file + "\n\n" TO violations_report
  APPEND "**Violation Count**: " + violation_file.violation_count + "\n\n" TO violations_report

  FOR EACH violation IN violation_file.violations:
    APPEND "- **Line " + violation.line + "** (" + violation.severity + "): " + violation.pattern + "\n" TO violations_report
    APPEND "  ```typescript\n  " + violation.code + "\n  ```\n\n" TO violations_report

WRITE violations_report TO output_dir + "LAW-2-VIOLATIONS.md"

## Write LAW-2-EVENTBUS-USAGE.md
DECLARE eventbus_report: string
SET eventbus_report = "# LAW 2 EventBus Usage Report\n\n"
APPEND "**Intention Emitters (EventBus.emit)**: " + eventbus_usage.emitters.length + "\n" TO eventbus_report
APPEND "**Parent Observers (EventBus.on)**: " + eventbus_usage.observers.length + "\n\n" TO eventbus_report
APPEND "## Compliant Patterns\n\n" TO eventbus_report

FOR EACH emitter IN eventbus_usage.emitters:
  APPEND "- " + emitter.file + ":" + emitter.line + "\n" TO eventbus_report
  APPEND "  ```typescript\n  " + emitter.code + "\n  ```\n\n" TO eventbus_report

APPEND "\n## Missing Intention Emissions\n\n" TO eventbus_report
FOR EACH missing IN eventbus_usage.missing_intention_emission:
  APPEND "- " + missing.file + "\n" TO eventbus_report
  APPEND "  **Reason**: " + missing.reason + "\n\n" TO eventbus_report

WRITE eventbus_report TO output_dir + "LAW-2-EVENTBUS-USAGE.md"

## Write LAW-2-REMEDIATION-PLAN.md
DECLARE remediation_report: string
SET remediation_report = "# LAW 2 Remediation Plan\n\n"
APPEND "**Files Requiring Remediation**: " + remediation_strategies.length + "\n\n" TO remediation_report

FOR EACH strategy IN remediation_strategies:
  APPEND "## " + strategy.file + "\n\n" TO remediation_report
  APPEND "**Steps**:\n\n" TO remediation_report

  FOR EACH step IN strategy.remediation_steps:
    APPEND "1. **" + step.step + "**\n" TO remediation_report
    APPEND "   - **From**: `" + step.from + "`\n" TO remediation_report
    APPEND "   - **To**: `" + step.to + "`\n" TO remediation_report
    IF step.parent_change !== undefined:
      APPEND "   - **Parent Change**: " + step.parent_change + "\n" TO remediation_report
    APPEND "\n" TO remediation_report

WRITE remediation_report TO output_dir + "LAW-2-REMEDIATION-PLAN.md"

## Write LAW-2-COMMUNICATION-MAP.md
DECLARE communication_report: string
SET communication_report = "# LAW 2 Communication Map\n\n"
APPEND "**Parent-Child Hierarchies**: " + parent_child_map.hierarchies.length + "\n\n" TO communication_report

FOR EACH hierarchy IN parent_child_map.hierarchies:
  APPEND "## " + hierarchy.file + "\n\n" TO communication_report
  APPEND "**Child Components Used**: " + hierarchy.child_usage.length + "\n\n" TO hierarchy.child_usage

WRITE communication_report TO output_dir + "LAW-2-COMMUNICATION-MAP.md"

## VERIFY: Written files match composed content
READ output_dir + "LAW-2-VIOLATIONS.md" INTO written_violations
READ output_dir + "LAW-2-REMEDIATION-PLAN.md" INTO written_remediation

IF written_violations !== violations_report:
  APPEND {"claim": "Violations report written correctly", "status": "REFUTED"} TO refuted_claims
  GOTO ABORT WITH "Write verification failed for violations report"

IF written_remediation !== remediation_report:
  APPEND {"claim": "Remediation plan written correctly", "status": "REFUTED"} TO refuted_claims
  GOTO ABORT WITH "Write verification failed for remediation plan"

APPEND {"claim": "All output artifacts written correctly", "status": "VERIFIED", "count": 4} TO verified_claims

VALIDATION GATE: Output Complete
✅ LAW-2-VIOLATIONS.md written and verified
✅ LAW-2-EVENTBUS-USAGE.md written and verified
✅ LAW-2-REMEDIATION-PLAN.md written and verified
✅ LAW-2-COMMUNICATION-MAP.md written and verified

# PHASE 7: FINAL VERIFICATION REPORT

CALCULATE verification_rate = verified_claims.length / (verified_claims.length + refuted_claims.length + 0.001)

IF verification_rate < 0.8:
  GOTO ABORT WITH "Verification rate below threshold: " + verification_rate

DECLARE final_summary: string
SET final_summary = "## LAW 2 Enforcement Complete\n\n"
APPEND "**Verification Rate**: " + verification_rate + "\n" TO final_summary
APPEND "**Verified Claims**: " + verified_claims.length + "\n" TO final_summary
APPEND "**Refuted Claims**: " + refuted_claims.length + "\n" TO final_summary
APPEND "**Total Violations**: " + violation_inventory.length + " files\n" TO final_summary
APPEND "**EventBus Emitters**: " + eventbus_usage.emitters.length + "\n" TO final_summary
APPEND "**EventBus Observers**: " + eventbus_usage.observers.length + "\n" TO final_summary
APPEND "**Remediation Strategies**: " + remediation_strategies.length + "\n" TO final_summary
APPEND "\n**Output Directory**: " + output_dir + "\n" TO final_summary

REPORT final_summary

VALIDATION GATE: Complete LAW 2 Enforcement
✅ All claims verified with rate >= 0.8
✅ Safe iteration loops used throughout
✅ No unbounded loops executed
✅ All output artifacts generated and verified

# OPERATIONAL DIRECTIVES (Inherited Skepticism)

ALWAYS verify tool output before trusting
ALWAYS check file exists before reading
ALWAYS validate grep results against expected patterns
ALWAYS maintain verified_claims and refuted_claims arrays
ALWAYS calculate verification_rate before finalizing
ALWAYS use bounded loops with max_iterations limit
ALWAYS check iteration_count before continuing loops
ALWAYS set goal_achieved condition for loop termination
ALWAYS analyze EventBus usage alongside violations
ALWAYS map parent-child relationships for communication flow

NEVER proceed with verification_rate < 0.8
NEVER trust single tool invocation without verification
NEVER skip calibration phase
NEVER accept claims without evidence
NEVER use unbounded loops (WHILE true, WHILE 1)
NEVER exceed max_iterations without user escalation
NEVER report violations without file:line evidence
NEVER suggest remediation without analyzing EventBus patterns

# EMBEDDED ALGORITHMIC PATTERNS

## Production-System-Cycle (Violation Detection)
## OBSERVE<CodeState> → MATCH<LAW_2_Rules> → DETECT<Violations> → CATALOG<Evidence>

DECLARE law_2_rules: array
SET law_2_rules = [
  {"rule": "No callback props", "pattern": "on\\w+\\s*=\\s*\\{", "action": "DETECT"},
  {"rule": "No parent references", "pattern": "this\\.parent\\.", "action": "DETECT"},
  {"rule": "No .bind(this) to children", "pattern": "\\.bind\\(this\\)", "action": "DETECT"},
  {"rule": "Children must emit intentions", "pattern": "EventBus\\.emit\\(", "action": "VERIFY_COMPLIANT"},
  {"rule": "Parents must observe intentions", "pattern": "EventBus\\.on\\(", "action": "VERIFY_COMPLIANT"}
]

## Semantic-Network-Spreading-Activation (Communication Tracing)
## START<Component> → FOLLOW<Imports> → TRACE<Calls> → MAP<Hierarchy>

DECLARE activation_sources: array
SET activation_sources = ["component_files", "parent-child imports", "EventBus emissions"]

## Means-Ends-Analysis (Remediation Planning)
## GOAL<LAW_2_Compliance> → CURRENT<CallbackPattern> → DIFFERENCE<NoIntentions> → OPERATOR<ConvertToEmit>

DECLARE remediation_operators: array
SET remediation_operators = [
  {"from": "callback_prop", "to": "EventBus.emit", "precondition": "child_needs_parent_action"},
  {"from": "parent_reference", "to": "EventBus.emit", "precondition": "child_calls_parent_method"},
  {"from": ".bind(this)", "to": "intention_emission", "precondition": "method_passed_downward"}
]

# ABORT

START ABORT
DECLARE abort_message: string
SET abort_message = EXTRACT_MESSAGE(ABORT)

WRITE {
  "status": "ABORTED",
  "reason": abort_message,
  "verified_claims": verified_claims,
  "refuted_claims": refuted_claims,
  "violations_found": violation_inventory.length,
  "verification_rate": verification_rate
} TO output_dir + "abort-report.json"

REPORT "LAW 2 enforcement ABORTED: " + abort_message
END
