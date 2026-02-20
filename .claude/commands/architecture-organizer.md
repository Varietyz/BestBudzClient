---
description: Systematic architecture organization - scan and fix file naming, location, and role violations following 4-axis PAG protocol
argument-hint: [mode: scan|fix|auto]
allowed-tools: Bash, Read, Write, Edit, Grep, Glob, TodoWrite
model: claude-sonnet-4-5-20250929
---

ARCHITECTURE ORGANIZER - Systematic file organization following ARCHITECTURE-ORGANIZATION.pag.md

%% META %%:
intent: "Execute 4-axis classification and systematic violation remediation"
context: "Codebase organization following global_role_registry and architectural axes"
priority: critical
execution_mode: deterministic_sequential

# ALGORITHM

**INITIALIZE<ArchitecturalRules> → SCAN<Violations> → CLASSIFY<ByPriority> → REPEAT { SELECT<NextViolation> → ANALYZE<File> → CLASSIFY<Axis> → DETERMINE<Role> → APPLY<Fix> → UPDATE<Imports> } UNTIL<AllResolved>**

This implements the ARCHITECTURE-ORGANIZATION.pag.md protocol:
- **GATE 0**: Load architectural context
- **GATE 1**: Execute PAG scanner (4-axis classification)
- **GATE 2**: Prioritize violations (CRITICAL → HIGH → MEDIUM)
- **GATE 3**: Systematic remediation with import updates
- **GATE 4**: Verify compliance

---

## PHASE 0: MANDATORY GATE REFRESH

READ ".claude/GATES.md"
READ ".claude/_DEV-RULES.md"
READ ".claude/_ARCHLAB.md"
READ "ARCHITECTURE-ORGANIZATION.md"
READ "ARCHITECTURE-ORGANIZATION.pag.md"

REPORT "📖 Loaded architectural context:"
REPORT "  → GATES.md (mandatory verification)"
REPORT "  → DEV-RULES.md (principles)"
REPORT "  → _ARCHLAB.md (7 Laws, naming rules)"
REPORT "  → ARCHITECTURE-ORGANIZATION.md (4-axis model)"
REPORT "  → ARCHITECTURE-ORGANIZATION.pag.md (PAG protocol)"
REPORT ""

---

## PHASE 1: INITIALIZE<State>

DECLARE mode: string
DECLARE violations: object
DECLARE priority_queue: array
DECLARE fixed_count: integer
DECLARE failed_fixes: array

SET mode = $1 OR "auto"
SET fixed_count = 0
SET failed_fixes = []

TodoWrite: ADD "Execute architecture organization (mode: ${mode})" WITH status: "in_progress"

---

## PHASE 2: SCAN<Codebase>

REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT "🔍 PHASE 2: SCANNING CODEBASE"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

## Execute PAG-compliant scanner
EXECUTE Bash WITH "cd D:\\GIT\\archlab && node .claude/workspace/tools/architecture-scanner-pag.mjs" INTO scan_output

## Load violations JSON
READ ".claude/workspace/tools/architecture-violations-pag.json" INTO violations

REPORT "📊 Scan Complete:"
REPORT "  → Total files: " + violations.totalFiles
REPORT "  → Files with violations: " + violations.filesWithViolations
REPORT ""

IF violations.filesWithViolations === 0:
    REPORT "✅ No violations found - codebase is compliant!"
    TodoWrite: MARK current AS completed
    EXIT 0

IF mode === "scan":
    REPORT "📝 Scan-only mode - no fixes will be applied"
    TodoWrite: MARK current AS completed
    EXIT 0

---

## PHASE 3: CLASSIFY<ByPriority>

REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT "📋 PHASE 3: PRIORITIZING VIOLATIONS"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

## Build priority queue: CRITICAL → HIGH → MEDIUM
DECLARE critical_violations: array
DECLARE high_violations: array
DECLARE medium_violations: array

SET critical_violations = FILTER violations.files WHERE violations CONTAINS code IN ["WRONG_LOCATION_AXIS_3"]
SET high_violations = FILTER violations.files WHERE violations CONTAINS code IN ["WRONG_LOCATION"]
SET medium_violations = FILTER violations.files WHERE violations CONTAINS code IN ["ROLE_NOT_DETECTED", "MISSING_DOMAIN", "NOT_KEBAB_CASE"]

SET priority_queue = CONCAT(critical_violations, high_violations, medium_violations)

REPORT "Priority Queue:"
REPORT "  🔴 CRITICAL (Axis 3 centralization): " + LENGTH(critical_violations)
REPORT "  🟠 HIGH (Role folder mismatch): " + LENGTH(high_violations)
REPORT "  🟡 MEDIUM (Role/naming issues): " + LENGTH(medium_violations)
REPORT "  📊 Total to fix: " + LENGTH(priority_queue)
REPORT ""

TodoWrite: ADD "Fix CRITICAL Axis 3 violations (" + LENGTH(critical_violations) + " files)" WITH status: "pending"
TodoWrite: ADD "Fix HIGH role folder mismatches (" + LENGTH(high_violations) + " files)" WITH status: "pending"
TodoWrite: ADD "Fix MEDIUM role/naming issues (" + LENGTH(medium_violations) + " files)" WITH status: "pending"

---

## PHASE 4: SYSTEMATIC REMEDIATION

REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT "🔧 PHASE 4: SYSTEMATIC REMEDIATION"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

FOR EACH violation_entry IN priority_queue:
    SET file_path = violation_entry.file
    SET violations_list = violation_entry.violations

    REPORT ""
    REPORT "────────────────────────────────────────────────────────"
    REPORT "📄 Processing: " + file_path
    REPORT "────────────────────────────────────────────────────────"

    ## STEP 1: Read file to understand its purpose
    READ file_path INTO file_content
    EXTRACT exports FROM file_content
    EXTRACT imports FROM file_content

    ## STEP 2: Determine correct axis and role
    DECLARE axis: integer
    DECLARE role: string
    DECLARE new_filename: string
    DECLARE new_location: string

    ## Check violation type and determine fix
    FOR EACH violation IN violations_list:

        IF violation.code === "WRONG_LOCATION_AXIS_3":
            ## CRITICAL: Centralize to src/shared/
            REPORT "  🔴 CRITICAL: Axis 3 violation - centralizing to shared/"
            SET axis = 3
            SET role = violation_entry.detectedRole.role
            SET new_location = violation.expected + violation_entry.fileName
            SET new_filename = violation_entry.fileName

            ## Move file to centralized location
            EXECUTE Bash WITH "cd D:\\GIT\\archlab\\root\\archlab-ide && mkdir -p " + DIRNAME(new_location)
            EXECUTE Bash WITH "cd D:\\GIT\\archlab\\root\\archlab-ide && mv " + file_path + " " + new_location

            ## Update all importers
            GREP "import.*" + BASENAME(file_path, ".ts") INTO importers
            FOR EACH importer IN importers:
                REPORT "    → Updating import in: " + importer
                READ importer INTO importer_content
                SET old_import_path = CALCULATE_RELATIVE_IMPORT(importer, file_path)
                SET new_import_path = CALCULATE_RELATIVE_IMPORT(importer, new_location)
                EDIT importer REPLACE old_import_path WITH new_import_path

            SET fixed_count = fixed_count + 1
            REPORT "  ✅ Fixed: " + file_path + " → " + new_location

        ELSE IF violation.code === "WRONG_LOCATION":
            ## HIGH: Move to correct role folder
            REPORT "  🟠 HIGH: Role folder mismatch - moving to correct folder"
            SET axis = violation_entry.axis
            SET role = violation_entry.detectedRole.role

            ## Determine new location from role info
            SET role_info = GLOBAL_ROLE_REGISTRY[role]
            SET context = EXTRACT_CONTEXT(file_path)  ## main, renderer, shared
            SET new_location = "src/" + context + "/" + role_info.folder + "/" + violation_entry.fileName

            ## Move file
            EXECUTE Bash WITH "cd D:\\GIT\\archlab\\root\\archlab-ide && mkdir -p " + DIRNAME(new_location)
            EXECUTE Bash WITH "cd D:\\GIT\\archlab\\root\\archlab-ide && mv " + file_path + " " + new_location

            ## Update imports
            GREP "import.*" + BASENAME(file_path, ".ts") INTO importers
            FOR EACH importer IN importers:
                REPORT "    → Updating import in: " + importer
                READ importer INTO importer_content
                SET old_import_path = CALCULATE_RELATIVE_IMPORT(importer, file_path)
                SET new_import_path = CALCULATE_RELATIVE_IMPORT(importer, new_location)
                EDIT importer REPLACE old_import_path WITH new_import_path

            SET fixed_count = fixed_count + 1
            REPORT "  ✅ Fixed: " + file_path + " → " + new_location

        ELSE IF violation.code === "ROLE_NOT_DETECTED":
            ## MEDIUM: Analyze file and add role suffix
            REPORT "  🟡 MEDIUM: Role not detected - analyzing file content"

            ## Analyze exports to infer role
            SET inferred_role = INFER_ROLE_FROM_EXPORTS(exports, file_content)

            IF inferred_role === null:
                REPORT "  ⚠️  Cannot infer role - needs manual classification"
                APPEND {file: file_path, reason: "Cannot infer role from exports"} TO failed_fixes
                CONTINUE

            ## Determine new filename with role suffix
            SET current_filename = BASENAME(file_path, ".ts")
            SET new_filename = current_filename + "-" + inferred_role + ".ts"
            SET new_location = DIRNAME(file_path) + "/" + new_filename

            ## Rename file
            EXECUTE Bash WITH "cd D:\\GIT\\archlab\\root\\archlab-ide && mv " + file_path + " " + new_location

            ## Update imports
            GREP "import.*" + current_filename INTO importers
            FOR EACH importer IN importers:
                REPORT "    → Updating import in: " + importer
                READ importer INTO importer_content
                EDIT importer REPLACE current_filename WITH BASENAME(new_filename, ".ts")

            SET fixed_count = fixed_count + 1
            REPORT "  ✅ Fixed: " + file_path + " → " + new_location

    ## Mark progress in todo
    IF fixed_count MOD 5 === 0:
        TodoWrite: UPDATE progress WITH "Fixed " + fixed_count + " / " + LENGTH(priority_queue) + " violations"

---

## PHASE 5: VERIFICATION

REPORT ""
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT "✅ PHASE 5: VERIFICATION"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

REPORT "📊 Remediation Summary:"
REPORT "  ✅ Fixed: " + fixed_count
REPORT "  ⚠️  Failed: " + LENGTH(failed_fixes)
REPORT ""

IF LENGTH(failed_fixes) > 0:
    REPORT "Failed fixes (manual intervention required):"
    FOR EACH failed IN failed_fixes:
        REPORT "  - " + failed.file + ": " + failed.reason

## Re-run scanner to verify
REPORT "🔍 Re-scanning to verify fixes..."
EXECUTE Bash WITH "cd D:\\GIT\\archlab && node .claude/workspace/tools/architecture-scanner-pag.mjs" INTO verification_output

READ ".claude/workspace/tools/architecture-violations-pag.json" INTO verification_result

IF verification_result.filesWithViolations === 0:
    REPORT ""
    REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT "✅ ALL VIOLATIONS RESOLVED"
    REPORT "✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    TodoWrite: MARK all AS completed
ELSE:
    REPORT ""
    REPORT "⚠️  Remaining violations: " + verification_result.filesWithViolations
    REPORT "💡 Run this command again to continue remediation"

---

## HELPER FUNCTIONS

FUNCTION INFER_ROLE_FROM_EXPORTS(exports, content):
    ## Detect role based on export patterns and content

    ## Check for lifecycle methods → Axis 1 STABLE
    IF exports CONTAINS methods ["init", "cleanup", "destroy"]:
        IF content CONTAINS "extends BaseManager":
            RETURN "manager"
        ELSE IF content CONTAINS "extends BaseService":
            RETURN "service"
        ELSE IF content CONTAINS "extends BaseComponent":
            RETURN "component"
        ## ... other base classes
        RETURN "manager"  ## Default for stateful

    ## Check for pure functions → Axis 2 HELPS
    ELSE IF ALL exports ARE functions AND NO class definitions:
        IF filename CONTAINS "parse":
            RETURN "parser"
        ELSE IF filename CONTAINS "valid":
            RETURN "validator"
        ELSE IF filename CONTAINS "format":
            RETURN "formatter"
        ELSE:
            RETURN "helper"

    ## Check for type definitions → Axis 3 DEFINES
    ELSE IF ALL exports ARE types OR interfaces OR constants:
        IF filename ENDS WITH "types.ts":
            RETURN "types"
        ELSE IF filename ENDS WITH "constants.ts":
            RETURN "constants"
        ELSE IF filename ENDS WITH "config.ts":
            RETURN "config"
        ELSE:
            RETURN "types"  ## Default for definitions

    ## Cannot infer
    RETURN null

FUNCTION CALCULATE_RELATIVE_IMPORT(from_file, to_file):
    ## Calculate relative import path between two files
    SET from_dir = DIRNAME(from_file)
    SET to_dir = DIRNAME(to_file)
    SET to_name = BASENAME(to_file, ".ts")

    SET relative_path = PATH.RELATIVE(from_dir, to_dir)
    IF relative_path === "":
        RETURN "./" + to_name
    ELSE:
        RETURN relative_path + "/" + to_name

FUNCTION EXTRACT_CONTEXT(file_path):
    ## Extract context (main, renderer, shared) from file path
    SET parts = SPLIT(file_path, "/")
    SET src_index = INDEX_OF(parts, "src")
    IF src_index !== -1 AND LENGTH(parts) > src_index + 1:
        RETURN parts[src_index + 1]
    RETURN "main"  ## Default

---

## USAGE EXAMPLES

```bash
# Scan only (no fixes)
architecture-organizer scan

# Fix automatically (recommended)
architecture-organizer auto

# Fix automatically (default)
architecture-organizer
```

---

## INTEGRATION WITH VERIFY-LOOP

This command integrates with `/verify-loop` for complete codebase compliance:

```bash
# 1. Organize architecture
architecture-organizer

# 2. Run verification loop
verify-loop
```

Architecture organization ensures files are in correct locations with correct names.
Verification loop ensures code quality and architectural compliance.
