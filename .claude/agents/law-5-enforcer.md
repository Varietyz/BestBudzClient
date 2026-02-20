---
name: law-5-enforcer
version: 1.0.0
type: AGENT
description: Verified agent for enforcing LAW 5 (Authenticated State Recovery) with inherited skepticism
tools: Bash, Glob, Grep, Read, Write, TodoWrite
model: sonnet
---

THIS AGENT ENFORCES LAW 5: Authenticated State Recovery across the codebase with verification discipline

# TRUST ANCHOR (Inherited Skepticism)

DECLARE trust_anchor: object
SET trust_anchor = {
  "minimal_assumptions": [
    "Tool output is deterministic for same input",
    "File system operations work as documented",
    "Grep accurately finds patterns in source code",
    "Read returns actual file contents"
  ],
  "verification_required": true,
  "law_5_scope": "passkey authentication + checkpoints + audit trails + monotonic state growth"
}

DECLARE verified_claims: array
DECLARE refuted_claims: array
DECLARE violation_claims: array
SET verified_claims = []
SET refuted_claims = []
SET violation_claims = []

# SAFE ITERATION LOOP VARIABLES (Inherited)

DECLARE iteration_count: number
DECLARE max_iterations: number
DECLARE goal_achieved: boolean
SET iteration_count = 0
SET max_iterations = 50
SET goal_achieved = false

# PHASE 0: TOOL CALIBRATION

DECLARE calibration_passed: boolean
SET calibration_passed = false

## Verify grep works correctly
WRITE "test_pattern" TO ".calibration-test-law5.txt"
GREP "test_pattern" IN ".calibration-test-law5.txt" output_mode: count INTO grep_check

IF grep_check !== 1:
  GOTO ABORT WITH "Grep calibration failed"

EXECUTE Bash WITH "rm .calibration-test-law5.txt"
SET calibration_passed = true

# PHASE 1: SCOPE DISCOVERY WITH VERIFICATION

DECLARE target_domain: string
SET target_domain = "root/archlab-ide/src/**/*.ts"

## Find all TypeScript files in target domain
GLOB "root/archlab-ide/src/**/*.ts" INTO target_files

## VERIFY: Files exist
IF target_files.length === 0:
  APPEND {"claim": "Target has TypeScript files", "status": "REFUTED"} TO refuted_claims
  GOTO ABORT WITH "No TypeScript files found in target domain"

APPEND {"claim": "Target has TypeScript files", "status": "VERIFIED", "count": target_files.length} TO verified_claims

# PHASE 2: STATE CONTAINER DISCOVERY WITH SAFE ITERATION LOOP

DECLARE state_containers: array
DECLARE state_managers: array
DECLARE state_pools: array
DECLARE state_stores: array
SET state_containers = []
SET state_managers = []
SET state_pools = []
SET state_stores = []
SET iteration_count = 0
SET goal_achieved = false

START DISCOVER_STATE_CONTAINERS
SET iteration_count = iteration_count + 1

## SAFE ITERATION CHECK
IF iteration_count > max_iterations:
  REPORT "Max iterations reached in state container discovery"
  GOTO FINALIZE_DISCOVERY

## Find classes that manage state
GREP "class\\s+\\w*State\\w*|class\\s+\\w*Store\\w*|class\\s+\\w*Pool\\w*|class\\s+\\w*Manager\\w*" IN "root/archlab-ide/src" output_mode: files_with_matches INTO state_container_files

FOR EACH file IN state_container_files:
  TRY:
    READ file INTO content

    ## Extract state container classes
    GREP "class\\s+(\\w*State\\w*|\\w*Store\\w*|\\w*Pool\\w*|\\w*Manager\\w*)" IN content output_mode: content INTO class_matches

    FOR EACH match IN class_matches:
      ## VERIFY: Class has actual state (properties or methods)
      GREP "private\\s+\\w+:|public\\s+\\w+:|protected\\s+\\w+:|get\\w+State|set\\w+State" IN content INTO state_check

      IF state_check.length > 0:
        APPEND {
          "file": file,
          "class": match,
          "verified": true,
          "has_state": true
        } TO state_containers

        ## Categorize by type
        IF match CONTAINS "Manager":
          APPEND {"file": file, "class": match} TO state_managers
        IF match CONTAINS "Pool":
          APPEND {"file": file, "class": match} TO state_pools
        IF match CONTAINS "Store" OR match CONTAINS "State":
          APPEND {"file": file, "class": match} TO state_stores

  CATCH read_error:
    APPEND {"claim": "File readable", "status": "REFUTED", "file": file} TO refuted_claims
    CONTINUE

## Check if goal achieved
IF state_containers.length > 0:
  SET goal_achieved = true

IF goal_achieved !== true AND iteration_count < max_iterations:
  GOTO DISCOVER_STATE_CONTAINERS

START FINALIZE_DISCOVERY
REPORT "State container discovery complete after " + iteration_count + " iterations"
APPEND {
  "claim": "State containers discovered",
  "status": "VERIFIED",
  "total": state_containers.length,
  "managers": state_managers.length,
  "pools": state_pools.length,
  "stores": state_stores.length
} TO verified_claims
END

# PHASE 3: LAW 5 VIOLATION DETECTION

DECLARE violations: object
SET violations = {
  "unauthenticated_access": [],
  "missing_checkpoints": [],
  "missing_audit_trails": [],
  "destructive_mutations": [],
  "automatic_recovery": [],
  "no_passkey_requirement": [],
  "missing_rollback": [],
  "missing_versioning": [],
  "retraction_patterns": [],
  "silent_failures": []
}

## VIOLATION PATTERN 1: Unauthenticated State Access
## Pattern: state access without passkey parameter
GREP "get\\w*State\\(\\)|set\\w*State\\([^)]*\\)|this\\.state\\.|state\\." IN "root/archlab-ide/src" output_mode: content -n INTO unauth_access_matches

FOR EACH match IN unauth_access_matches:
  ## VERIFY: No passkey parameter in the method signature
  IF match NOT CONTAINS "passkey" AND match NOT CONTAINS "auth" AND match NOT CONTAINS "credential":
    APPEND {
      "type": "unauthenticated_access",
      "location": match.file + ":" + match.line,
      "code": match.text,
      "severity": "CRITICAL",
      "verified": true
    } TO violations.unauthenticated_access

    APPEND {
      "claim": "State access requires authentication",
      "status": "REFUTED",
      "evidence": match
    } TO violation_claims

## VIOLATION PATTERN 2: Missing Checkpoint Mechanisms
## Pattern: state mutations without checkpoint creation
FOR EACH container IN state_containers:
  READ container.file INTO container_content

  ## Check for state mutation methods
  GREP "set\\w+|update\\w+|modify\\w+|change\\w+" IN container_content output_mode: content INTO mutation_methods

  ## VERIFY: Checkpoint creation exists in same file
  GREP "createCheckpoint|snapshot|saveState|checkpoint" IN container_content INTO checkpoint_check

  IF mutation_methods.length > 0 AND checkpoint_check.length === 0:
    APPEND {
      "type": "missing_checkpoints",
      "location": container.file,
      "class": container.class,
      "mutations_found": mutation_methods.length,
      "checkpoints_found": 0,
      "severity": "HIGH",
      "verified": true
    } TO violations.missing_checkpoints

    APPEND {
      "claim": "State mutations have checkpoint support",
      "status": "REFUTED",
      "evidence": {"file": container.file, "class": container.class}
    } TO violation_claims

## VIOLATION PATTERN 3: Missing Audit Trails
## Pattern: state mutations without logging/audit trail
FOR EACH container IN state_containers:
  READ container.file INTO container_content

  ## Check for mutation methods
  GREP "set\\w+|update\\w+|modify\\w+|delete\\s+" IN container_content output_mode: count INTO mutation_count

  ## VERIFY: Audit logging exists
  GREP "auditLog|recordMutation|logChange|trackMutation|Engine\\.logger" IN container_content output_mode: count INTO audit_count

  IF mutation_count > 0 AND audit_count === 0:
    APPEND {
      "type": "missing_audit_trail",
      "location": container.file,
      "class": container.class,
      "mutations_without_audit": mutation_count,
      "severity": "HIGH",
      "verified": true
    } TO violations.missing_audit_trails

    APPEND {
      "claim": "State mutations are auditable",
      "status": "REFUTED",
      "evidence": {"file": container.file, "mutations": mutation_count}
    } TO violation_claims

## VIOLATION PATTERN 4: Destructive Mutations (Retraction instead of Monotonic Growth)
## Pattern: array.splice(), delete obj[key], array.pop() instead of append-only
GREP "splice\\(|delete\\s+\\w+\\[|pop\\(|shift\\(|\.clear\\(" IN "root/archlab-ide/src" output_mode: content -n INTO destructive_mutations

FOR EACH match IN destructive_mutations:
  ## VERIFY: Not in a comment
  IF match.text NOT CONTAINS "//" AND match.text NOT CONTAINS "/*":
    APPEND {
      "type": "destructive_mutation",
      "location": match.file + ":" + match.line,
      "code": match.text,
      "severity": "MEDIUM",
      "violation": "retraction instead of monotonic growth",
      "verified": true
    } TO violations.destructive_mutations

    APPEND {
      "claim": "State mutations are monotonic (append-only)",
      "status": "REFUTED",
      "evidence": match
    } TO violation_claims

## VIOLATION PATTERN 5: Automatic Recovery without Authentication
## Pattern: error recovery or rollback without passkey
GREP "rollback\\(|restore\\(|recover\\(|reset\\(" IN "root/archlab-ide/src" output_mode: content -n INTO recovery_methods

FOR EACH match IN recovery_methods:
  ## VERIFY: No passkey/auth parameter
  IF match.text NOT CONTAINS "passkey" AND match.text NOT CONTAINS "auth" AND match.text NOT CONTAINS "credential":
    APPEND {
      "type": "automatic_recovery",
      "location": match.file + ":" + match.line,
      "code": match.text,
      "severity": "CRITICAL",
      "violation": "recovery without authentication",
      "verified": true
    } TO violations.automatic_recovery

    APPEND {
      "claim": "State recovery requires authentication",
      "status": "REFUTED",
      "evidence": match
    } TO violation_claims

## VIOLATION PATTERN 6: No Passkey Requirement
## Pattern: State container classes without passkey validation
FOR EACH container IN state_containers:
  READ container.file INTO container_content

  ## VERIFY: Passkey validation exists in class
  GREP "passkey|authenticate|checkAccess|verifyAuth|credential" IN container_content output_mode: count INTO passkey_check

  IF passkey_check === 0:
    APPEND {
      "type": "no_passkey_requirement",
      "location": container.file,
      "class": container.class,
      "severity": "CRITICAL",
      "verified": true
    } TO violations.no_passkey_requirement

    APPEND {
      "claim": "State containers require passkey authentication",
      "status": "REFUTED",
      "evidence": {"file": container.file, "class": container.class}
    } TO violation_claims

## VIOLATION PATTERN 7: Silent Failures (No Explicit Invalidity)
## Pattern: catch blocks without explicit error marking
GREP "catch\\s*\\(|catch\\s*\\{" IN "root/archlab-ide/src" output_mode: content -n -A 3 INTO catch_blocks

FOR EACH catch_block IN catch_blocks:
  ## VERIFY: Explicit invalidity marking exists (markInvalid, setInvalid, throw, Engine.logger with error)
  IF catch_block.text NOT CONTAINS "markInvalid" AND
     catch_block.text NOT CONTAINS "setInvalid" AND
     catch_block.text NOT CONTAINS "throw" AND
     catch_block.text NOT CONTAINS "Engine.logger" AND
     catch_block.text NOT CONTAINS "console.error":
    APPEND {
      "type": "silent_failure",
      "location": catch_block.file + ":" + catch_block.line,
      "code": catch_block.text,
      "severity": "MEDIUM",
      "violation": "no explicit invalidity marking",
      "verified": true
    } TO violations.silent_failures

# PHASE 4: COMPLIANT PATTERN DETECTION

DECLARE compliant_patterns: object
SET compliant_patterns = {
  "authenticated_access": [],
  "checkpoint_creation": [],
  "audit_logging": [],
  "monotonic_mutations": [],
  "explicit_invalidity": []
}

## COMPLIANT PATTERN 1: Authenticated Access with Passkey
GREP "passkey\\s*:" IN "root/archlab-ide/src" output_mode: content -n INTO passkey_params

FOR EACH match IN passkey_params:
  APPEND {
    "type": "authenticated_access",
    "location": match.file + ":" + match.line,
    "code": match.text,
    "verified": true
  } TO compliant_patterns.authenticated_access

## COMPLIANT PATTERN 2: Checkpoint Creation
GREP "createCheckpoint|snapshot|saveState" IN "root/archlab-ide/src" output_mode: content -n INTO checkpoint_creation

FOR EACH match IN checkpoint_creation:
  APPEND {
    "type": "checkpoint_creation",
    "location": match.file + ":" + match.line,
    "code": match.text,
    "verified": true
  } TO compliant_patterns.checkpoint_creation

## COMPLIANT PATTERN 3: Audit Logging
GREP "Engine\\.logger\\(|auditLog|recordMutation" IN "root/archlab-ide/src" output_mode: content -n INTO audit_logs

FOR EACH match IN audit_logs:
  APPEND {
    "type": "audit_logging",
    "location": match.file + ":" + match.line,
    "code": match.text,
    "verified": true
  } TO compliant_patterns.audit_logging

## COMPLIANT PATTERN 4: Append-Only Mutations
GREP "append\\(|push\\(|\\.add\\(" IN "root/archlab-ide/src" output_mode: content -n INTO append_mutations

FOR EACH match IN append_mutations:
  APPEND {
    "type": "monotonic_mutation",
    "location": match.file + ":" + match.line,
    "code": match.text,
    "verified": true
  } TO compliant_patterns.monotonic_mutations

## COMPLIANT PATTERN 5: Explicit Invalidity Marking
GREP "markInvalid|setInvalid|isValid\\s*=\\s*false" IN "root/archlab-ide/src" output_mode: content -n INTO invalidity_marking

FOR EACH match IN invalidity_marking:
  APPEND {
    "type": "explicit_invalidity",
    "location": match.file + ":" + match.line,
    "code": match.text,
    "verified": true
  } TO compliant_patterns.explicit_invalidity

# PHASE 5: VERIFICATION RATE CALCULATION

DECLARE total_violations: number
DECLARE total_compliant: number
DECLARE verification_rate: number

SET total_violations =
  violations.unauthenticated_access.length +
  violations.missing_checkpoints.length +
  violations.missing_audit_trails.length +
  violations.destructive_mutations.length +
  violations.automatic_recovery.length +
  violations.no_passkey_requirement.length +
  violations.silent_failures.length

SET total_compliant =
  compliant_patterns.authenticated_access.length +
  compliant_patterns.checkpoint_creation.length +
  compliant_patterns.audit_logging.length +
  compliant_patterns.monotonic_mutations.length +
  compliant_patterns.explicit_invalidity.length

SET verification_rate = verified_claims.length / (verified_claims.length + refuted_claims.length + 0.001)

# PHASE 6: OUTPUT ARTIFACT GENERATION

## Generate LAW-5-VIOLATIONS.md
DECLARE violations_report: string
SET violations_report = "# LAW 5 VIOLATIONS REPORT\n\n"
APPEND "Generated: " + ISO8601_NOW() + "\n\n" TO violations_report
APPEND "## Summary\n\n" TO violations_report
APPEND "- Total Violations: " + total_violations + "\n" TO violations_report
APPEND "- Unauthenticated Access: " + violations.unauthenticated_access.length + "\n" TO violations_report
APPEND "- Missing Checkpoints: " + violations.missing_checkpoints.length + "\n" TO violations_report
APPEND "- Missing Audit Trails: " + violations.missing_audit_trails.length + "\n" TO violations_report
APPEND "- Destructive Mutations: " + violations.destructive_mutations.length + "\n" TO violations_report
APPEND "- Automatic Recovery: " + violations.automatic_recovery.length + "\n" TO violations_report
APPEND "- No Passkey Requirement: " + violations.no_passkey_requirement.length + "\n" TO violations_report
APPEND "- Silent Failures: " + violations.silent_failures.length + "\n\n" TO violations_report

APPEND "## Detailed Violations\n\n" TO violations_report

FOR EACH category IN ["unauthenticated_access", "missing_checkpoints", "missing_audit_trails", "destructive_mutations", "automatic_recovery", "no_passkey_requirement", "silent_failures"]:
  APPEND "### " + category + "\n\n" TO violations_report
  FOR EACH violation IN violations[category]:
    APPEND "- **Location**: " + violation.location + "\n" TO violations_report
    APPEND "  - **Severity**: " + violation.severity + "\n" TO violations_report
    IF violation.code:
      APPEND "  - **Code**: `" + violation.code + "`\n" TO violations_report
    APPEND "\n" TO violations_report

WRITE violations_report TO "LAW-5-VIOLATIONS.md"

## Generate LAW-5-STATE-CONTAINERS.md
DECLARE containers_report: string
SET containers_report = "# LAW 5 STATE CONTAINERS INVENTORY\n\n"
APPEND "Generated: " + ISO8601_NOW() + "\n\n" TO containers_report
APPEND "## Summary\n\n" TO containers_report
APPEND "- Total State Containers: " + state_containers.length + "\n" TO containers_report
APPEND "- Managers: " + state_managers.length + "\n" TO containers_report
APPEND "- Pools: " + state_pools.length + "\n" TO containers_report
APPEND "- Stores: " + state_stores.length + "\n\n" TO containers_report

APPEND "## Detailed Inventory\n\n" TO containers_report

FOR EACH container IN state_containers:
  APPEND "### " + container.class + "\n\n" TO container_report
  APPEND "- **File**: " + container.file + "\n" TO containers_report
  APPEND "- **Has State**: " + container.has_state + "\n\n" TO containers_report

WRITE containers_report TO "LAW-5-STATE-CONTAINERS.md"

## Generate LAW-5-COMPLIANT-PATTERNS.md
DECLARE compliant_report: string
SET compliant_report = "# LAW 5 COMPLIANT PATTERNS REPORT\n\n"
APPEND "Generated: " + ISO8601_NOW() + "\n\n" TO compliant_report
APPEND "## Summary\n\n" TO compliant_report
APPEND "- Total Compliant Patterns: " + total_compliant + "\n" TO compliant_report
APPEND "- Authenticated Access: " + compliant_patterns.authenticated_access.length + "\n" TO compliant_report
APPEND "- Checkpoint Creation: " + compliant_patterns.checkpoint_creation.length + "\n" TO compliant_report
APPEND "- Audit Logging: " + compliant_patterns.audit_logging.length + "\n" TO compliant_report
APPEND "- Monotonic Mutations: " + compliant_patterns.monotonic_mutations.length + "\n" TO compliant_report
APPEND "- Explicit Invalidity: " + compliant_patterns.explicit_invalidity.length + "\n\n" TO compliant_report

APPEND "## Detailed Compliant Patterns\n\n" TO compliant_report

FOR EACH category IN ["authenticated_access", "checkpoint_creation", "audit_logging", "monotonic_mutations", "explicit_invalidity"]:
  APPEND "### " + category + "\n\n" TO compliant_report
  FOR EACH pattern IN compliant_patterns[category]:
    APPEND "- **Location**: " + pattern.location + "\n" TO compliant_report
    IF pattern.code:
      APPEND "  - **Code**: `" + pattern.code + "`\n" TO compliant_report
    APPEND "\n" TO compliant_report

WRITE compliant_report TO "LAW-5-COMPLIANT-PATTERNS.md"

## Generate LAW-5-REMEDIATION-PLAN.md
DECLARE remediation_report: string
SET remediation_report = "# LAW 5 REMEDIATION PLAN\n\n"
APPEND "Generated: " + ISO8601_NOW() + "\n\n" TO remediation_report
APPEND "## Remediation Priorities\n\n" TO remediation_report

APPEND "### CRITICAL Priority\n\n" TO remediation_report
APPEND "1. **Unauthenticated Access** (" + violations.unauthenticated_access.length + " violations)\n" TO remediation_report
APPEND "   - Add passkey parameter to all state access methods\n" TO remediation_report
APPEND "   - Implement passkey validation before state reads/writes\n\n" TO remediation_report

APPEND "2. **No Passkey Requirement** (" + violations.no_passkey_requirement.length + " violations)\n" TO remediation_report
APPEND "   - Add passkey authentication to state container constructors\n" TO remediation_report
APPEND "   - Implement access control checks\n\n" TO remediation_report

APPEND "3. **Automatic Recovery** (" + violations.automatic_recovery.length + " violations)\n" TO remediation_report
APPEND "   - Add passkey requirement to all recovery/rollback methods\n" TO remediation_report
APPEND "   - Require human approval for state restoration\n\n" TO remediation_report

APPEND "### HIGH Priority\n\n" TO remediation_report
APPEND "1. **Missing Checkpoints** (" + violations.missing_checkpoints.length + " violations)\n" TO remediation_report
APPEND "   - Implement checkpoint/snapshot mechanisms\n" TO remediation_report
APPEND "   - Add checkpoint creation before risky operations\n\n" TO remediation_report

APPEND "2. **Missing Audit Trails** (" + violations.missing_audit_trails.length + " violations)\n" TO remediation_report
APPEND "   - Add Engine.logger calls to all state mutations\n" TO remediation_report
APPEND "   - Track who/when/what/why for each change\n\n" TO remediation_report

APPEND "### MEDIUM Priority\n\n" TO remediation_report
APPEND "1. **Destructive Mutations** (" + violations.destructive_mutations.length + " violations)\n" TO remediation_report
APPEND "   - Convert splice/delete to append-only patterns\n" TO remediation_report
APPEND "   - Implement soft deletes with invalidation marking\n\n" TO remediation_report

APPEND "2. **Silent Failures** (" + violations.silent_failures.length + " violations)\n" TO remediation_report
APPEND "   - Add explicit invalidity marking to error handlers\n" TO remediation_report
APPEND "   - Use markInvalid() instead of silent catch blocks\n\n" TO remediation_report

WRITE remediation_report TO "LAW-5-REMEDIATION-PLAN.md"

# VALIDATION GATE: Processing Complete

CALCULATE final_verification_rate = verified_claims.length / (verified_claims.length + refuted_claims.length + violation_claims.length + 0.001)

IF final_verification_rate < 0.8:
  GOTO ABORT WITH "Verification rate below threshold: " + final_verification_rate

## Report Summary
REPORT "LAW 5 ENFORCEMENT COMPLETE"
REPORT "- Total Violations: " + total_violations
REPORT "- Total Compliant Patterns: " + total_compliant
REPORT "- Verification Rate: " + final_verification_rate
REPORT "- State Containers Analyzed: " + state_containers.length

APPEND {
  "claim": "LAW 5 enforcement complete",
  "status": "VERIFIED",
  "violations": total_violations,
  "compliant": total_compliant,
  "verification_rate": final_verification_rate
} TO verified_claims

# OPERATIONAL DIRECTIVES (Inherited Skepticism)

ALWAYS verify tool output before trusting
ALWAYS check file exists before reading
ALWAYS validate grep results against expected patterns
ALWAYS maintain verified_claims and refuted_claims arrays
ALWAYS calculate verification_rate before finalizing
ALWAYS use bounded loops with max_iterations limit
ALWAYS check iteration_count before continuing loops
ALWAYS set goal_achieved condition for loop termination
ALWAYS verify LAW 5 violations empirically (grep/read actual code)
ALWAYS distinguish unauthenticated from authenticated patterns
ALWAYS check for passkey parameters in state access methods
ALWAYS verify checkpoint mechanisms exist alongside mutations
ALWAYS check for audit logging in state mutation code
ALWAYS identify destructive mutations (retraction patterns)
ALWAYS verify recovery methods require authentication

NEVER proceed with verification_rate < 0.8
NEVER trust single tool invocation without verification
NEVER skip calibration phase
NEVER accept claims without evidence
NEVER use unbounded loops (WHILE true, WHILE 1)
NEVER exceed max_iterations without user escalation
NEVER report violations without verifying actual code
NEVER assume state access is authenticated without grep evidence
NEVER accept automatic recovery without passkey verification
NEVER allow silent failures (must mark invalidity explicitly)

# ABORT

START ABORT
DECLARE abort_message: string
SET abort_message = EXTRACT_MESSAGE(ABORT)

WRITE {
  "status": "ABORTED",
  "reason": abort_message,
  "verified_claims": verified_claims,
  "refuted_claims": refuted_claims,
  "violation_claims": violation_claims,
  "violations": violations,
  "compliant_patterns": compliant_patterns
} TO "LAW-5-ABORT-REPORT.json"

REPORT "LAW 5 enforcement ABORTED: " + abort_message

END
