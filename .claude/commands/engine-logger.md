---
description: Systematic migration from console.* and empty catches to Engine.logger with architectural filtering
argument-hint: [max-files-per-iteration]
allowed-tools: Bash, Read, Write, Edit, Grep, Glob
model: claude-sonnet-4-5-20250929
---

# engine-logger - Systematic Logging Migration

**ALGORITHMIC MIGRATION**: console.* → Engine.logger() with architectural domain filtering

%% META %%:
intent: "Migrate meaningful functions to Engine.logger (RESOURCE/EXECUTION only)"
context: "100% traceability for stateful operations, skip pure COMPUTATION utilities"
priority: critical

## ARCHITECTURAL CONSTITUTION

Based on `.claude/_DEV-RULES.md` COMPUTATION/RESOURCE/EXECUTION domains:

**✅ MIGRATE (Meaningful - requires logging):**
- **RESOURCE**: File I/O, DB, network, lifecycle (init/shutdown), ownership transfer
- **EXECUTION**: Event handlers, IPC, intent flow, error handling
- **MANDATORY**: ALL empty catches, ALL console.* calls

**⏭️ SKIP (Pure utilities - no logging needed):**
- **COMPUTATION**: Pure functions, type guards, path transformers, formatters, simple getters
- **STRUCTURAL**: Type files, barrel exports, constants

---

## MIGRATION ALGORITHM

**INITIALIZE<Schema> → REPEAT { DETECT<Violations> → ENRICH<Context> → MIGRATE<Logging> → VALIDATE<Schema> } UNTIL<AllMigrated>**

This implements:
- **INTAKE**: Detect violations via custom script
- **ABSTRACT**: Map console.*/empty catches to Engine.logger patterns
- **DEFINE**: Target = 100% Engine.logger saturation
- **CONSTRAIN**: All logs must follow schema requirements
- **EXPLORE**: Identify rich context opportunities per function
- **PREDICT**: Validate schema compliance before application
- **CHOOSE**: Select appropriate logging fields per context
- **EXECUTE**: Apply migrations with context enrichment
- **VALIDATE**: Verify schema compliance
- **REFINE**: Loop until all files saturated

---

## PHASE 0: INITIALIZE<SchemaInspection>

**MANDATORY SELF-UPDATE**: Inspect current Engine.logger schema and update command if changed

DECLARE current_schema: object
DECLARE command_schema: object
DECLARE schema_changed: boolean

REPORT "🔍 PHASE 0: Schema Inspection & Self-Update"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

### Step 0.1: Extract current schema from source

READ "root/archlab-ide/src/renderer/engine/debug/log-schema-types.ts" INTO schema_source
READ "root/archlab-ide/src/renderer/engine/debug/README.md" INTO schema_docs

PARSE schema_source FOR:
  - LogEntryOptions interface definition
  - ComponentContext interface
  - ExecutionContext interface
  - ErrorContext interface
  - All optional context interfaces (entity, intent, pool, graph, ipc)

SET current_schema = EXTRACTED_SCHEMA

### Step 0.2: Compare with embedded schema in this command

SET command_schema = SCHEMA_EMBEDDED_BELOW

IF current_schema !== command_schema:
    REPORT "⚠️  Schema has changed! Updating command..."

    UPDATE_SECTION "ENGINE.LOGGER API SCHEMA (AUTO-UPDATED)" WITH current_schema

    REPORT "✅ Command schema updated to match current implementation"
    REPORT ""
    REPORT "CHANGES DETECTED:"
    REPORT DIFF(command_schema, current_schema)
    REPORT ""

    SET schema_changed = true
ELSE:
    REPORT "✅ Schema is current - no update needed"
    SET schema_changed = false

REPORT ""
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

---

## PHASE 1: INITIALIZE<DetectionManifest>

DECLARE max_files_per_iteration: integer
DECLARE manifest: object
DECLARE unsaturated_files: array
DECLARE saturated_files: array
DECLARE iteration: integer

SET max_files_per_iteration = $1 OR 5
SET iteration = 0

REPORT "🔍 PHASE 1: Detection & Manifest Generation"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

### Step 1.1: Run violation detection script

EXECUTE Bash WITH "node .claude/workspace/tools/detect-logging-violations.cjs" INTO detection_output

REPORT detection_output
REPORT ""

### Step 1.2: Load manifest

READ ".claude/workspace/logging-migration-manifest.json" INTO manifest

SET unsaturated_files = manifest.unsaturated
SET saturated_files = manifest.saturated

REPORT "📊 MANIFEST LOADED:"
REPORT "  ✅ Saturated files:   " + LENGTH(saturated_files)
REPORT "  ⚠️  Unsaturated files: " + LENGTH(unsaturated_files)
REPORT "  📈 Average score:     " + manifest.summary.averageScore + "%"
REPORT ""

IF LENGTH(unsaturated_files) === 0:
    REPORT "🎉 ALL FILES SATURATED! Migration complete."
    EXIT 0

REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

---

## PHASE 2: REPEAT<MigrationLoop>

WHILE LENGTH(unsaturated_files) > 0:

    SET iteration = iteration + 1

    REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT "🔄 ITERATION " + iteration
    REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    REPORT ""

    ### Step 2.1: Select batch of files to migrate

    DECLARE batch: array
    SET batch = TAKE_FIRST(unsaturated_files, max_files_per_iteration)

    REPORT "📋 BATCH " + iteration + ": " + LENGTH(batch) + " files"
    REPORT ""

    FOR EACH file IN batch:
        REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        REPORT "📄 FILE: " + file.file
        REPORT "   Score: " + file.score + "%"
        REPORT "   Violations:"
        REPORT "     - Empty catches: " + LENGTH(file.emptyCatches)
        REPORT "     - console.*: " + LENGTH(file.consoleLogs)
        REPORT "     - Functions w/o logging: " + LENGTH(file.functionsWithoutLogging)
        REPORT "     - Engine.logger calls: " + file.engineLoggerCalls
        REPORT ""

        ### Step 2.2: Read file content

        READ file.file INTO file_content

        ### Step 2.3: Analyze function context for rich logging

        REPORT "🔍 ANALYZING FUNCTION CONTEXTS:"
        REPORT ""

        FOR EACH violation IN file.functionsWithoutLogging:
            REPORT "  Function: " + violation.name + " (line " + violation.line + ")"
            REPORT "    Reason: " + violation.reason
            REPORT "    Complexity: " + violation.bodyLines + " lines"

            # Detect available context for this function
            DECLARE available_context: object
            SET available_context = DETECT_CONTEXT(file_content, violation.name, violation.line)

            REPORT "    Available context:"
            IF available_context.hasEntity:
                REPORT "      ✅ Entity (uuid detected)"
            IF available_context.hasIntent:
                REPORT "      ✅ Intent (emit/handle detected)"
            IF available_context.hasPool:
                REPORT "      ✅ Pool (acquire/release detected)"
            IF available_context.hasError:
                REPORT "      ✅ Error (try/catch detected)"
            IF available_context.hasIPC:
                REPORT "      ✅ IPC (channel detected)"
            REPORT ""

        ### Step 2.4: Generate migration strategy

        REPORT "🔧 MIGRATION STRATEGY:"
        REPORT ""

        # 1. Migrate empty catches
        IF LENGTH(file.emptyCatches) > 0:
            REPORT "  1. Fill " + LENGTH(file.emptyCatches) + " empty catch blocks:"
            FOR EACH catch IN file.emptyCatches:
                REPORT "     - Line " + catch.line + ": Add error context with recovery info"

        # 2. Migrate console.* calls
        IF LENGTH(file.consoleLogs) > 0:
            REPORT "  2. Convert " + LENGTH(file.consoleLogs) + " console.* calls:"
            FOR EACH log IN file.consoleLogs:
                DECLARE log_level: string
                SET log_level = MAP_CONSOLE_TO_LEVEL(log.method)
                REPORT "     - Line " + log.line + ": " + log.method + " → Engine.logger (level: " + log_level + ")"

        # 3. Add logging to functions without it
        IF LENGTH(file.functionsWithoutLogging) > 0:
            REPORT "  3. Add Engine.logger to " + LENGTH(file.functionsWithoutLogging) + " functions:"
            FOR EACH fn IN file.functionsWithoutLogging:
                REPORT "     - " + fn.name + " (line " + fn.line + "): Entry/Exit/Error logging"

        REPORT ""

        ### Step 2.5: Apply migrations with rich context

        REPORT "🛠️  APPLYING MIGRATIONS:"
        REPORT ""

        # Import Engine if not present
        IF NOT file_content CONTAINS "import { Engine }":
            REPORT "  ✓ Adding Engine import"
            # Add import at top

        # Import health computation if not present
        IF NOT file_content CONTAINS "import { computeComponentHealth }":
            REPORT "  ✓ Adding computeComponentHealth import"
            # Add import

        # Migrate each violation type
        APPLY_MIGRATIONS(file.file, file_content, {
            emptyCatches: file.emptyCatches,
            consoleLogs: file.consoleLogs,
            functionsWithoutLogging: file.functionsWithoutLogging,
        })

        REPORT "  ✅ Migrations applied to " + file.file
        REPORT ""

        ### Step 2.6: Validate schema compliance

        REPORT "✅ VALIDATING SCHEMA COMPLIANCE:"
        REPORT ""

        READ file.file INTO updated_content

        # Extract all Engine.logger calls
        DECLARE logger_calls: array
        SET logger_calls = EXTRACT_LOGGER_CALLS(updated_content)

        FOR EACH call IN logger_calls:
            VALIDATE_SCHEMA(call) OR REPORT "⚠️  Schema violation at line " + call.line

        REPORT "  ✅ All Engine.logger calls comply with schema"
        REPORT ""

    ### Step 2.7: Re-run detection to update manifest

    REPORT "📊 UPDATING MANIFEST:"
    REPORT ""

    EXECUTE Bash WITH "node .claude/workspace/tools/detect-logging-violations.cjs" INTO updated_detection

    READ ".claude/workspace/logging-migration-manifest.json" INTO updated_manifest

    SET unsaturated_files = updated_manifest.unsaturated
    SET saturated_files = updated_manifest.saturated

    REPORT "  ✅ Saturated files:   " + LENGTH(saturated_files) + " (+" + (LENGTH(saturated_files) - LENGTH(manifest.saturated)) + ")"
    REPORT "  ⚠️  Unsaturated files: " + LENGTH(unsaturated_files) + " (-" + (LENGTH(manifest.unsaturated) - LENGTH(unsaturated_files)) + ")"
    REPORT "  📈 Average score:     " + updated_manifest.summary.averageScore + "%"
    REPORT ""

    SET manifest = updated_manifest

    IF LENGTH(unsaturated_files) === 0:
        REPORT "🎉 ALL FILES SATURATED!"
        BREAK

END WHILE

---

## PHASE 3: VERIFY<CompleteMigration>

REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT "✅ MIGRATION COMPLETE"
REPORT "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
REPORT ""

REPORT "📊 FINAL STATISTICS:"
REPORT "  Total iterations:     " + iteration
REPORT "  Files migrated:       " + LENGTH(saturated_files)
REPORT "  Average score:        " + manifest.summary.averageScore + "%"
REPORT "  Engine.logger calls:  " + manifest.summary.totalEngineLoggerCalls
REPORT "  Logging coverage:     " + ROUND((manifest.summary.totalEngineLoggerCalls / manifest.summary.totalFunctions) * 100) + "%"
REPORT ""

REPORT "🎯 CODEBASE IS NOW 100% TRACEABLE"
REPORT ""

---

## ENGINE.LOGGER API SCHEMA (AUTO-UPDATED)

**Version**: 2.0.0 (Schema-Only)
**Last Updated**: 2026-01-05 (auto-sync from source)

### Single Entry Point

```typescript
Engine.logger(options: LogEntryOptions): string
```

Returns generated log entry ID.

### Required Fields (ALWAYS)

```typescript
{
    level: DebugSeverity,        // "trace" | "debug" | "info" | "warn" | "error" | "violation"
    source: string,              // Component name (e.g., "FileTreePanel")
    message: string,             // Human-readable description

    // REQUIRED: Component ownership (single-owner accountability)
    component: {
        name: string,            // Component name
        type: ComponentType,     // "manager" | "service" | "component" | "validator" | "pool" | "engine" | "tracker"
        health: HealthState,     // "healthy" | "degraded" | "unhealthy" | "critical"
        uuid?: string,           // Optional: Component UUID if entity-backed
    },

    // REQUIRED: Execution context (observed-execution)
    execution: {
        function: string,        // Function/method name
        phase: ExecutionPhase,   // "entry" | "exit" | "error"
        result?: ExecutionResult,// REQUIRED if phase="exit": "success" | "failure" | "partial"
        duration?: number,       // Optional: Execution time (ms)
        stackDepth?: number,     // Optional: Call stack depth
    },
}
```

### Conditionally Required Fields

```typescript
{
    // REQUIRED if level === "error" | "violation"
    error?: {
        type: ErrorType,         // "validation" | "resource" | "security" | "law_violation" | "system" | "network" | "unknown"
        code: string,            // Machine-readable code (e.g., "FILE_NOT_FOUND")
        recoverable: boolean,    // Can system recover?
        cause?: string,          // Root cause analysis
        remedy?: string,         // Suggested fix
    },

    // RECOMMENDED for non-trace logs
    entity?: {
        uuid: string,            // Entity UUID from DOM Factory
        type?: string,           // HTML tag or component type
        subtype?: string,        // Entity role (from entity: "panel")
        parentUuid?: string,     // Parent entity UUID
    },
}
```

### Optional Context Fields

```typescript
{
    // Intent flow (LAW 2 compliance tracking)
    intent?: {
        type: string,            // Intent identifier (e.g., "save-file")
        phase?: "emitted" | "propagating" | "handled" | "completed" | "failed",
        originUuid?: string,     // Entity that emitted
        targetUuid?: string,     // Target entity
    },

    // Pool operations
    pool?: {
        name: string,            // Pool identifier (e.g., "div-pool")
        action?: "acquire" | "release" | "resize" | "create" | "destroy",
        size?: number,           // Current capacity
        available?: number,      // Available items
    },

    // Graph tracking
    graph?: {
        affectedNodes?: string[],      // UUIDs of affected nodes
        relationshipType?: string,     // Relationship type
    },

    // IPC communication
    ipc?: {
        channel: string,         // Channel name (e.g., "file:save")
        direction?: "send" | "receive" | "invoke" | "handle",
    },

    // Output routing
    mode?: {
        console?: boolean,       // Output to console (default: true)
        db?: boolean,            // Persist to SQLite (default: true)
        silent?: boolean,        // Suppress all output (default: false)
        ai?: boolean,            // Generate PAG output (default: false)
    },

    // Metadata
    priority?: number,           // 0-100, default 50. >=75 triggers webhook
    retentionDays?: number,      // Days before deletion (default: 7)
    webhookUrl?: string,         // External webhook URL
    tags?: string[],             // Custom tags
    metadata?: Record<string, unknown>, // Arbitrary data
}
```

### Health Computation

```typescript
import { computeComponentHealth } from "./health-computation";

const health = computeComponentHealth("ComponentName");
// Returns: "healthy" | "degraded" | "unhealthy" | "critical"
```

Health is computed from recent log patterns (last 5 minutes) using rules R1-R10:
- R1: LAW violations → critical
- R4: Child→parent callbacks → critical
- R2: Error rate > 30% → unhealthy
- R5: Pool leaks → degraded
- R3: Execution imbalance → degraded
- R10: Default → healthy

### Common Patterns

#### Basic Info Log

```typescript
Engine.logger({
    level: "info",
    source: "FileTreePanel",
    message: "File loaded successfully",
    component: {
        name: "FileTreePanel",
        type: "component",
        health: computeComponentHealth("FileTreePanel")
    },
    execution: {
        function: "loadFile",
        phase: "exit",
        result: "success"
    },
});
```

#### Error with Context

```typescript
Engine.logger({
    level: "error",
    source: "StyleManager",
    message: "Failed to apply styles",
    component: {
        name: "StyleManager",
        type: "manager",
        health: computeComponentHealth("StyleManager")
    },
    execution: {
        function: "applyStyles",
        phase: "error"
    },
    error: {
        type: "validation",
        code: "INVALID_CSS_VAR",
        recoverable: true,
        cause: "Referenced CSS variable does not exist",
        remedy: "Use tokens from tokens.css",
    },
    entity: {
        uuid: panelUuid,
        type: "div",
        subtype: "panel"
    },
});
```

#### Empty Catch Migration

```typescript
// BEFORE
try {
    await dangerousOperation();
} catch (err) {
    // Silent fail - BAD!
}

// AFTER
try {
    await dangerousOperation();
} catch (err) {
    Engine.logger({
        level: "error",
        source: "ComponentName",
        message: "Operation failed",
        component: {
            name: "ComponentName",
            type: "component",
            health: computeComponentHealth("ComponentName")
        },
        execution: {
            function: "dangerousOperation",
            phase: "error"
        },
        error: {
            type: "system",
            code: "OPERATION_FAILED",
            recoverable: true,
            cause: err.message,
            remedy: "Retry or check system state",
        },
    });
    // Optionally re-throw or handle
}
```

#### Console.* Migration

```typescript
// BEFORE
console.log("User clicked button");

// AFTER
Engine.logger({
    level: "debug",
    source: "SaveButton",
    message: "User clicked button",
    component: {
        name: "SaveButton",
        type: "component",
        health: "healthy"
    },
    execution: {
        function: "handleClick",
        phase: "entry"
    },
    entity: {
        uuid: buttonUuid,
        type: "button",
        subtype: "action"
    },
});
```

---

## HELPER FUNCTIONS

### DETECT_CONTEXT(content, functionName, lineNumber)

Analyzes function body to detect available context:

```javascript
FUNCTION DETECT_CONTEXT(content, functionName, lineNumber):
    DECLARE context: object
    SET context = {
        hasEntity: false,
        hasIntent: false,
        hasPool: false,
        hasError: false,
        hasIPC: false,
    }

    # Extract function body
    DECLARE functionBody: string
    SET functionBody = EXTRACT_FUNCTION_BODY(content, functionName, lineNumber)

    # Detect patterns
    IF functionBody CONTAINS "uuid" OR functionBody CONTAINS "UUID":
        SET context.hasEntity = true

    IF functionBody CONTAINS "emit" OR functionBody CONTAINS "Intent":
        SET context.hasIntent = true

    IF functionBody CONTAINS ".acquire(" OR functionBody CONTAINS ".release(":
        SET context.hasPool = true

    IF functionBody CONTAINS "try" OR functionBody CONTAINS "catch":
        SET context.hasError = true

    IF functionBody CONTAINS "ipcRenderer" OR functionBody CONTAINS "channel":
        SET context.hasIPC = true

    RETURN context
```

### MAP_CONSOLE_TO_LEVEL(consoleMethod)

Maps console.* method to Engine.logger level:

```javascript
FUNCTION MAP_CONSOLE_TO_LEVEL(consoleMethod):
    SWITCH consoleMethod:
        CASE "log":   RETURN "debug"
        CASE "info":  RETURN "info"
        CASE "warn":  RETURN "warn"
        CASE "error": RETURN "error"
        CASE "debug": RETURN "debug"
        CASE "trace": RETURN "trace"
        DEFAULT:      RETURN "debug"
```

### APPLY_MIGRATIONS(filePath, content, violations)

Applies all migrations to a file:

```javascript
FUNCTION APPLY_MIGRATIONS(filePath, content, violations):
    DECLARE newContent: string
    SET newContent = content

    # 1. Add imports if missing
    IF NOT newContent CONTAINS "import { Engine }":
        PREPEND "import { Engine } from '../engine';\n" TO newContent

    IF NOT newContent CONTAINS "computeComponentHealth":
        PREPEND "import { computeComponentHealth } from '../engine/debug/health-computation';\n" TO newContent

    # 2. Migrate empty catches
    FOR EACH catch IN violations.emptyCatches:
        REPLACE catch.code IN newContent WITH:
            """
            catch (err) {
                Engine.logger({
                    level: "error",
                    source: "[ComponentName]",
                    message: "Error in [function]",
                    component: { name: "[ComponentName]", type: "component", health: "degraded" },
                    execution: { function: "[function]", phase: "error" },
                    error: { type: "system", code: "ERROR", recoverable: true, cause: err.message },
                });
            }
            """

    # 3. Migrate console.* calls
    FOR EACH log IN violations.consoleLogs:
        DECLARE level: string
        SET level = MAP_CONSOLE_TO_LEVEL(log.method)

        REPLACE log.code IN newContent WITH:
            """
            Engine.logger({
                level: "[level]",
                source: "[ComponentName]",
                message: "[original message]",
                component: { name: "[ComponentName]", type: "component", health: computeComponentHealth("[ComponentName]") },
                execution: { function: "[function]", phase: "exit", result: "success" },
            });
            """

    # 4. Add function entry/exit logging
    FOR EACH fn IN violations.functionsWithoutLogging:
        # Add entry log at start of function
        # Add exit log before returns
        # Add error log in catch blocks
        INJECT_FUNCTION_LOGGING(newContent, fn)

    # Write updated content
    WRITE newContent TO filePath
```

---

## USAGE EXAMPLES

### Default (5 files per iteration)

```
/engine-logger
```

### Process 10 files per iteration

```
/engine-logger 10
```

### Process single file per iteration (careful mode)

```
/engine-logger 1
```

---

## OPERATIONAL NOTES

**Algorithm Class**: Systematic Migration Loop

**Semantic Guarantees**:
- Schema compliance validated before write
- Health computation integrated automatically
- All migrations preserve code semantics
- 100% traceability via Engine.logger

**Mapped Principles**:
- **observed-execution**: Every function traceable
- **errors-as-language**: Structured error context
- **single-owner**: Component accountability
- **computed-health**: Symbolic diagnosis
- **fail-fast**: No silent failures

**Constraint Validation**:
- All Engine.logger calls MUST comply with schema
- All empty catches MUST have error context
- All console.* MUST migrate to Engine.logger
- All complex functions MUST have entry/exit logging

---

## TERMINATION CONDITIONS

**SUCCESS**: `manifest.unsaturated.length === 0`

**PROGRESS INDICATORS**:
- ✅ Files moving to saturated → Migration working
- ⚠️  Schema violations → Fix schema compliance
- ⚠️  Score not improving → Review context detection

---

**© 2026 ARCHLAB - Engine Logger Migration v1.0.0**
