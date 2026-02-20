---
name: engine-logger
description: Use when implementing logging with Engine.logger, designing log schemas, classifying functions by architectural domain (RESOURCE/EXECUTION/COMPUTATION), or querying diagnostic data
allowed-tools: Read, Grep, Glob
model: claude-sonnet-4-5-20250929
---

THIS SKILL PROVIDES domain expertise for ARCHLAB's Engine.logger system and architectural domain classification

# EMBEDDED KNOWLEDGE PATTERNS

## [Schema-Based-Logging] Class: Structured Observability

## DECLARE<Schema> → CLASSIFY<Domain> → ENRICH<Context> → VALIDATE<Compliance> → QUERY<Diagnostics>

## Applied in: Teaching Claude how to use Engine.logger correctly and classify functions architecturally

%% META %%:
intent: "Teach Engine.logger schema, domain classification, and observability patterns"
context: "Logging following ARCHLAB architectural principles (RESOURCE/EXECUTION/COMPUTATION)"
priority: high

# SKILL DEFINITION

DECLARE skill_metadata: object
SET skill_metadata = {
"name": "engine-logger",
"type": "model-invoked",
"scope": "logging-and-observability",
"invocation": "automatic when working with logging, debugging, or diagnostics"
}

# PHASE 1: PURPOSE AND SCOPE

## What This Skill Teaches

DECLARE teaching_objectives: array
SET teaching_objectives = [
"Engine.logger schema structure and required fields",
"Architectural domain classification (RESOURCE/EXECUTION/COMPUTATION)",
"When functions require logging vs when they don't",
"Health computation and symbolic diagnosis",
"Query API for diagnostic analysis"
]

## Specific Problem This Skill Addresses

DECLARE problem_domain: object
SET problem_domain = {
"challenge": "Knowing when/how to use Engine.logger correctly with proper schema compliance",
"scope": "Schema-based logging, domain classification, health computation, query API",
"exclusions": "NOT about console.log migration (that's the /engine-logger command)"
}

VALIDATION GATE: Purpose Defined
✅ Teaching objectives specified
✅ Invocation triggers clear
✅ Problem domain bounded

# PHASE 2: CORE KNOWLEDGE

## Concept 1: Engine.logger Schema Structure

DECLARE concept_1: object
SET concept_1 = {
"definition": "Schema-only logging API with mandatory component/execution contexts",
"importance": "Enables queryable, traceable execution following observed-execution principle",
"relationships": "Maps to ARCHLAB principles (single-owner, fail-fast, computed-health)"
}

### Entry Point

```typescript
// Single entry point - NO convenience methods
Engine.logger(options: LogEntryOptions): string // Returns log entry ID
```

### Required Fields (ALWAYS)

```typescript
{
    level: DebugSeverity,        // "trace" | "debug" | "info" | "warn" | "error" | "violation"
    source: string,              // Component name (e.g., "FileTreePanel")
    message: string,             // Human-readable description

    // REQUIRED: Component ownership (single-owner principle)
    component: {
        name: string,            // Component name
        type: ComponentType,     // "manager" | "service" | "component" | "validator" | "pool" | "engine" | "tracker"
        health: HealthState,     // "healthy" | "degraded" | "unhealthy" | "critical"
        uuid?: string,           // Optional: Component UUID if entity-backed
    },

    // REQUIRED: Execution context (observed-execution principle)
    execution: {
        function: string,        // Function/method name
        phase: ExecutionPhase,   // "entry" | "exit" | "error"
        result?: ExecutionResult,// REQUIRED if phase="exit": "success" | "failure" | "partial"
        duration?: number,       // Optional: Execution time (ms)
        stackDepth?: number,     // Optional: Call stack depth
    },
}
```

### Conditionally Required

```typescript
{
    // REQUIRED if level === "error" | "violation" (errors-as-language principle)
    error?: {
        type: ErrorType,         // "validation" | "resource" | "security" | "law_violation" | "system" | "network" | "unknown"
        code: string,            // Machine-readable code (e.g., "FILE_NOT_FOUND")
        recoverable: boolean,    // Can system recover?
        cause?: string,          // Root cause analysis
        remedy?: string,         // Suggested fix
    },
}
```

### Optional Context Fields

```typescript
{
    entity?: { uuid: string, type?: string, subtype?: string, parentUuid?: string },
    intent?: { type: string, phase?: string, originUuid?: string, targetUuid?: string },
    pool?: { name: string, action?: string, size?: number, available?: number },
    graph?: { affectedNodes?: string[], relationshipType?: string },
    ipc?: { channel: string, direction?: "send" | "receive" | "invoke" | "handle" },
    mode?: { console?: boolean, db?: boolean, silent?: boolean, ai?: boolean },
    priority?: number,      // 0-100, default 50. >=75 triggers webhook
    retentionDays?: number, // Days before deletion (default: 7)
    tags?: string[],
    metadata?: Record<string, unknown>,
}
```

WHEN creating_log_entry:
ANALYZE context FOR available_fields
IF level === "error" OR level === "violation":
REQUIRE error_context
IF entity_uuid_available:
INCLUDE entity_context
IF intent_flow_tracked:
INCLUDE intent_context
VALIDATE schema_compliance BEFORE calling Engine.logger

## Concept 2: Architectural Domain Classification

DECLARE concept_2: object
SET concept_2 = {
"definition": "Functions classified by RESOURCE/EXECUTION/COMPUTATION domains to determine logging requirement",
"usage_pattern": "RESOURCE and EXECUTION require logging, COMPUTATION utilities skip",
"constraints": "Based on .claude/\_DEV-RULES.md principle architecture"
}

### Domain Definitions

```
┌─────────────────────────────────────────────────────────────┐
│ COMPUTATION (Stateless)  │ RESOURCE (Stateful) │ EXECUTION  │
├──────────────────────────┼─────────────────────┼────────────┤
│ • Pure functions         │ • File I/O          │ • Events   │
│ • Type guards            │ • DB operations     │ • IPC      │
│ • Path transformers      │ • Network calls     │ • Intents  │
│ • Data formatters        │ • Lifecycle methods │ • Errors   │
│ • Simple getters         │ • Ownership ops     │ • State    │
│                          │                     │            │
│ ⏭️  NO LOGGING NEEDED     │ ✅ LOGGING REQUIRED  │ ✅ REQUIRED │
└─────────────────────────────────────────────────────────────┘
```

### Classification Algorithm

WHEN analyzing_function:
EXTRACT function_name
EXTRACT function_body

    # RESOURCE domain signatures
    IF body CONTAINS ["fs.", "readFile", "writeFile", "db.", "query(", "execute(", "fetch(", "axios", "http."]:
        RETURN "RESOURCE" WITH reason "I/O operation"
    IF name MATCHES /(init|start|stop|shutdown|destroy|acquire|release)/i:
        RETURN "RESOURCE" WITH reason "Lifecycle method"

    # EXECUTION domain signatures
    IF body CONTAINS ["addEventListener", ".on(", "once(", "ipcRenderer", "ipcMain", "window.api", ".emit(", "Intent"]:
        RETURN "EXECUTION" WITH reason "Event/IPC/Intent handling"

    # COMPUTATION domain signatures (SKIP LOGGING)
    IF name MATCHES /^(is|has|should|can)/i AND NOT body CONTAINS ["await", "fs.", "db."]:
        RETURN "COMPUTATION" WITH reason "Pure predicate/type guard"
    IF name MATCHES /^(to|from).*(Path|String|Number|Date|JSON)/i:
        RETURN "COMPUTATION" WITH reason "Pure data transformer"
    IF name MATCHES /(format|parse|normalize|sanitize|convert)/i AND NOT body CONTAINS ["await", "fs."]:
        RETURN "COMPUTATION" WITH reason "Pure formatter"
    IF lines <= 2 AND body.trimmed STARTS WITH "return":
        RETURN "COMPUTATION" WITH reason "Simple getter"

    # UNKNOWN - use complexity heuristics
    IF body_lines >= 10 OR is_async OR has_try_catch:
        RETURN "REQUIRES_ANALYSIS" WITH reason "Complex function"
    ELSE:
        RETURN "COMPUTATION" WITH reason "Simple utility"

### Examples

```typescript
// RESOURCE - File I/O (REQUIRES LOGGING)
async function saveFile(path: string, content: string): Promise<void> {
    Engine.logger({
        level: "info",
        source: "FileService",
        message: `Saving file: ${path}`,
        component: { name: "FileService", type: "service", health: "healthy" },
        execution: { function: "saveFile", phase: "entry" },
    });
    await fs.writeFile(path, content);
    Engine.logger({
        level: "info",
        source: "FileService",
        message: `File saved: ${path}`,
        component: { name: "FileService", type: "service", health: "healthy" },
        execution: { function: "saveFile", phase: "exit", result: "success" },
    });
}

// EXECUTION - Event handler (REQUIRES LOGGING)
function handleClick(event: MouseEvent): void {
    Engine.logger({
        level: "debug",
        source: "SaveButton",
        message: "User clicked save button",
        component: { name: "SaveButton", type: "component", health: "healthy" },
        execution: { function: "handleClick", phase: "entry" },
        entity: { uuid: buttonUuid, type: "button", subtype: "action" },
    });
    // ... handle click
}

// COMPUTATION - Pure transformer (NO LOGGING)
function windowsToWslPath(windowsPath: string): string {
    const driveMatch = windowsPath.match(/^([A-Za-z]):[\\/]/);
    if (driveMatch) {
        const driveLetter = driveMatch[1].toLowerCase();
        return `/mnt/${driveLetter}/${windowsPath.slice(3).replace(/\\/g, "/")}`;
    }
    return windowsPath.replace(/\\/g, "/");
}
// ✅ Pure, deterministic, no I/O → No logging needed
```

## Concept 3: Health Computation and Query API

DECLARE concept_3: object
SET concept_3 = {
"pattern": "Computed health from log patterns, not metrics",
"applicability": "Symbolic diagnosis using rules R1-R10",
"alternatives": "NOT metric-based monitoring (avoid symptom tracking)"
}

### Health Computation

```typescript
import { computeComponentHealth } from "./engine/debug/health-computation";

const health = computeComponentHealth("ComponentName");
// Returns: "healthy" | "degraded" | "unhealthy" | "critical"
```

### Health Rules (R1-R10)

```
R1: LAW violations (e.g., parent callbacks) → CRITICAL
R2: Error rate > 30% in last 5 minutes → UNHEALTHY
R3: Execution phase imbalance (too many entry, few exit) → DEGRADED
R4: Child→parent callbacks detected → CRITICAL
R5: Pool leaks (acquire > release) → DEGRADED
R10: Default case → HEALTHY
```

WHEN computing_health:
READ logs FROM last_5_minutes FOR component_name
COUNT violations BY type

    IF LAW_violations > 0:
        RETURN "critical"
    IF child_parent_callbacks_detected:
        RETURN "critical"
    IF error_rate > 0.3:
        RETURN "unhealthy"
    IF entry_exit_ratio > 1.5:
        RETURN "degraded"
    IF pool_leak_detected:
        RETURN "degraded"
    ELSE:
        RETURN "healthy"

### Query API

```typescript
// Access via Engine.debug (added in cleanup session)
Engine.debug.query(options: QueryOptions): LogQueryResult
Engine.debug.traceIntent(intentType: string): IntentTrace
Engine.debug.analyzePool(poolName: string): PoolAnalysis
Engine.debug.getEntityLifecycle(uuid: string): EntityLifecycle
```

### Query Examples

```typescript
// Get last 20 logs
const result = Engine.debug.query({ limit: 20 });

// Get errors for specific component
const errors = Engine.debug.query({
    level: "error",
    component: "FileService",
    timeRange: { start: Date.now() - 3600000, end: Date.now() },
});

// Trace intent flow
const trace = Engine.debug.traceIntent("save-file");
// Returns: propagation path, duration, success status, related logs

// Analyze pool health
const poolHealth = Engine.debug.analyzePool("div-pool");
// Returns: snapshots, metrics, leak suspects
```

VALIDATION GATE: Core Knowledge Complete
✅ All concepts defined
✅ Examples provided
✅ Relationships established

# PHASE 3: STEP-BY-STEP PROCESS

## STEP 1: Classify Function Domain

WHEN deciding_if_logging_needed:
EXTRACT function_signature
EXTRACT function_body

    EXECUTE classifyFunctionDomain(name, body)

    IF domain === "COMPUTATION":
        SKIP_LOGGING
        REASON "Pure utility, no side effects"
    ELSE IF domain === "RESOURCE" OR domain === "EXECUTION":
        REQUIRE_LOGGING
        REASON domain + " operation detected"
    ELSE:
        ANALYZE complexity
        IF complex:
            REQUIRE_LOGGING
        ELSE:
            SKIP_LOGGING

## STEP 2: Build Schema-Compliant Log Entry

WHEN creating_log_call:
DECLARE log_options: LogEntryOptions

    # Required fields
    SET log_options.level = DETERMINE_SEVERITY()
    SET log_options.source = component_name
    SET log_options.message = describe_action

    SET log_options.component = {
        name: component_name,
        type: DETERMINE_TYPE(), // manager|service|component|etc
        health: computeComponentHealth(component_name)
    }

    SET log_options.execution = {
        function: function_name,
        phase: DETERMINE_PHASE(), // entry|exit|error
    }

    IF log_options.execution.phase === "exit":
        SET log_options.execution.result = "success" | "failure" | "partial"

    # Conditionally required
    IF log_options.level === "error" OR log_options.level === "violation":
        SET log_options.error = {
            type: classify_error_type,
            code: machine_readable_code,
            recoverable: can_system_recover,
            cause: root_cause_description,
            remedy: suggested_fix
        }

    # Optional context
    IF entity_uuid_available:
        SET log_options.entity = { uuid, type, subtype }

    IF intent_tracked:
        SET log_options.intent = { type, phase, originUuid, targetUuid }

    VALIDATE log_options AGAINST schema
    EXECUTE Engine.logger(log_options)

## STEP 3: Validate Schema Compliance

WHEN validating_log_entry:
CHECK required_fields_present
CHECK execution.result_if_exit_phase
CHECK error_context_if_error_level

    IF validation_fails:
        REPORT schema_violation
        FIX missing_or_incorrect_fields
    ELSE:
        APPROVE log_entry

VALIDATION GATE: Process Complete
✅ Domain classification process
✅ Schema building process
✅ Validation process

# PHASE 4: PRACTICAL EXAMPLES

## Example 1: RESOURCE Domain (File I/O with Error Handling)

```typescript
async function loadConfig(path: string): Promise<Config> {
    Engine.logger({
        level: "info",
        source: "ConfigService",
        message: `Loading config from ${path}`,
        component: {
            name: "ConfigService",
            type: "service",
            health: computeComponentHealth("ConfigService"),
        },
        execution: {
            function: "loadConfig",
            phase: "entry",
        },
    });

    try {
        const content = await fs.readFile(path, "utf-8");
        const config = JSON.parse(content);

        Engine.logger({
            level: "info",
            source: "ConfigService",
            message: `Config loaded successfully from ${path}`,
            component: {
                name: "ConfigService",
                type: "service",
                health: computeComponentHealth("ConfigService"),
            },
            execution: {
                function: "loadConfig",
                phase: "exit",
                result: "success",
            },
        });

        return config;
    } catch (err) {
        Engine.logger({
            level: "error",
            source: "ConfigService",
            message: `Failed to load config from ${path}`,
            component: {
                name: "ConfigService",
                type: "service",
                health: "degraded",
            },
            execution: {
                function: "loadConfig",
                phase: "error",
            },
            error: {
                type: "resource",
                code: "CONFIG_LOAD_FAILED",
                recoverable: true,
                cause: err.message,
                remedy: "Check file exists and has valid JSON",
            },
        });
        throw err;
    }
}
```

## Example 2: EXECUTION Domain (IPC Handler)

```typescript
async function handleSaveRequest(data: SaveData): Promise<void> {
    Engine.logger({
        level: "debug",
        source: "SaveHandler",
        message: "IPC save request received",
        component: {
            name: "SaveHandler",
            type: "service",
            health: "healthy",
        },
        execution: {
            function: "handleSaveRequest",
            phase: "entry",
        },
        ipc: {
            channel: "file:save",
            direction: "receive",
        },
    });

    await performSave(data);

    Engine.logger({
        level: "info",
        source: "SaveHandler",
        message: "Save completed successfully",
        component: {
            name: "SaveHandler",
            type: "service",
            health: "healthy",
        },
        execution: {
            function: "handleSaveRequest",
            phase: "exit",
            result: "success",
        },
        ipc: {
            channel: "file:save",
            direction: "handle",
        },
    });
}
```

## Example 3: COMPUTATION Domain (NO LOGGING)

```typescript
// Type guard - pure predicate
function isValidPath(path: string): boolean {
    return typeof path === "string" && path.length > 0;
}
// ✅ Pure, no I/O, no logging needed

// Path transformer - pure function
function normalizePath(path: string): string {
    return path.replace(/\\/g, "/");
}
// ✅ Pure transformation, no logging needed

// Simple getter
function getDefaultTimeout(): number {
    return 5000;
}
// ✅ Constant return, no logging needed
```

VALIDATION GATE: Examples Complete
✅ RESOURCE example shown
✅ EXECUTION example shown
✅ COMPUTATION examples shown

# PHASE 5: COMMON PITFALLS

## Pitfall 1: Forgetting Error Context for Error Levels

DECLARE pitfall_1: object
SET pitfall_1 = {
"description": "Using level='error' without error context object",
"consequence": "Schema validation fails at runtime",
"avoidance": "Always include error field when level is 'error' or 'violation'"
}

```typescript
// ❌ BAD - Missing required error context
Engine.logger({
    level: "error",
    source: "FileService",
    message: "File not found",
    component: { name: "FileService", type: "service", health: "degraded" },
    execution: { function: "readFile", phase: "error" },
    // MISSING: error field!
});

// ✅ GOOD - Error context included
Engine.logger({
    level: "error",
    source: "FileService",
    message: "File not found",
    component: { name: "FileService", type: "service", health: "degraded" },
    execution: { function: "readFile", phase: "error" },
    error: {
        type: "resource",
        code: "FILE_NOT_FOUND",
        recoverable: true,
        cause: "File does not exist at path",
        remedy: "Check file path and permissions",
    },
});
```

## Pitfall 2: Adding Logging to Pure COMPUTATION Functions

DECLARE pitfall_2: object
SET pitfall_2 = {
"description": "Adding Engine.logger to pure utility functions",
"consequence": "Logging noise, performance impact, violates COMPUTATION principle",
"avoidance": "Only log RESOURCE and EXECUTION domains"
}

```typescript
// ❌ BAD - Logging pure transformation
function toUpperCase(str: string): string {
    Engine.logger({
        /* ... */
    }); // Unnecessary!
    return str.toUpperCase();
}

// ✅ GOOD - No logging for pure function
function toUpperCase(str: string): string {
    return str.toUpperCase();
}
```

## Pitfall 3: Missing execution.result for Exit Phase

DECLARE pitfall_3: object
SET pitfall_3 = {
"description": "Using phase='exit' without result field",
"consequence": "Schema validation fails",
"avoidance": "Always include result when phase is 'exit'"
}

```typescript
// ❌ BAD - Missing result for exit
Engine.logger({
    level: "info",
    source: "Service",
    message: "Operation complete",
    component: { name: "Service", type: "service", health: "healthy" },
    execution: {
        function: "doWork",
        phase: "exit", // MISSING: result!
    },
});

// ✅ GOOD - Result included for exit
Engine.logger({
    level: "info",
    source: "Service",
    message: "Operation complete",
    component: { name: "Service", type: "service", health: "healthy" },
    execution: {
        function: "doWork",
        phase: "exit",
        result: "success", // Required!
    },
});
```

VALIDATION GATE: Pitfalls Documented
✅ Common mistakes identified
✅ Consequences explained
✅ Avoidance strategies provided

# PHASE 6: RELATED FILES AND REFERENCES

## Codebase Files

DECLARE file_references: array
SET file_references = [
{
"path": "root/archlab-ide/src/renderer/engine/index.ts",
"relevance": "Engine.logger and Engine.debug exports (lines 60-69)"
},
{
"path": "root/archlab-ide/src/renderer/engine/debug/debug-hub.ts",
"relevance": "logSchema() implementation and schema validation (lines 67-120)"
},
{
"path": "root/archlab-ide/src/renderer/engine/debug/log-schema-types.ts",
"relevance": "LogEntryOptions interface and all type definitions"
},
{
"path": "root/archlab-ide/src/renderer/engine/debug/health-computation.ts",
"relevance": "computeComponentHealth() and rules R1-R10"
},
{
"path": "root/archlab-ide/src/renderer/engine/debug/README.md",
"relevance": "Complete Engine.logger documentation"
},
{
"path": ".claude/workspace/tools/detect-logging-violations.cjs",
"relevance": "classifyFunctionDomain() implementation (lines 317-372)"
},
{
"path": ".claude/_DEV-RULES.md",
"relevance": "COMPUTATION/RESOURCE/EXECUTION principle architecture"
}
]

VALIDATION GATE: References Complete
✅ File references documented
✅ Relevance specified
