# dev-lab

Introspect runtime, query dispatch, and Vite enforcement plugins extracted from dom-lab. Three concerns in one package:

1. **Runtime** wraps every function in `introspect()`, producing frozen 49-field records with causal context, state diffs, timing, and invariant validation.
2. **Query** dispatches `collect("spine.records")` against the ring buffer. 11 spine queries registered on import. Consumers register project-specific queries on top.
3. **Vite plugins** enforce wrapping at build time via AST analysis and run 12 code quality checks.

## Install

```
npm install dev-lab
```

Peer dependencies (install separately):

```
@babel/parser ^7.28.0
@babel/traverse ^7.28.0
@modelcontextprotocol/sdk ^1.12.0
glob ^13.0.0
vite ^7.0.0
zod ^3.25.0
```

## Runtime

```js
import { introspect, setRecordObserver } from "dev-lab";
```

### introspect(fn, options)

Wraps a synchronous function. Returns `{ result, record }`. Throws on exception (with `error.introspectRecord` attached).

```js
function moveEntity(entity, dx, dy) {
    return introspect(() => {
        entity.x += dx;
        entity.y += dy;
    }, {
        name: "moveEntity",
        causalContext: parentRecord.causalContext,
        preStateCapture: () => ({ x: entity.x, y: entity.y }),
        postStateCapture: () => ({ x: entity.x, y: entity.y }),
        intent: "reposition entity by delta",
        semantics: "mutation",
    }).result;
}
```

**Options (mandatory):**

| Field | Type | Description |
|---|---|---|
| `name` | string | Function name for the record |
| `causalContext` | object or null | Propagated causal chain from parent |
| `preStateCapture` | fn or null | Returns state snapshot before execution |
| `postStateCapture` | fn or null | Returns state snapshot after execution |

**Options (defaulted, fill when ready):**

| Field | Type | Default | Description |
|---|---|---|---|
| `intent` | string | `"pending"` | Purpose of this execution |
| `semantics` | string | `"pending"` | Semantic category |
| `ambiguities` | string[] | `[]` | Competing outcomes |
| `lifecycle` | string or null | `null` | `"created"`, `"mutated"`, or `"deleted"` |

**Options (auto-injected by the Vite plugin at build time):**

| Field | Type | Description |
|---|---|---|
| `file` | string or null | Source file path |
| `class` | string or null | Enclosing class name |

### Auto-wired pipeline

Importing `dev-lab` auto-wires `setRecordObserver(writeToRingBuffer)`. Every `introspect()` call writes its record to the ring buffer immediately. No setup needed.

### setRecordObserver(fn)

Overrides the record observer. The default writes to the ring buffer. Call with a custom function to add behavior (logging, server sync), or `null` to disable storage.

```js
import { setRecordObserver, writeToRingBuffer } from "dev-lab";

setRecordObserver((record) => {
    writeToRingBuffer(record);
    sendToServer(record);
});
```

### Causal context

```js
import { createRootCausalContext, propagateCausalContext } from "dev-lab";

const root = createRootCausalContext({ triggerKey: "user-click" });
// Pass root as causalContext to introspect().
// After execution, propagate:
const next = propagateCausalContext(root, record);
```

`createRootCausalContext(options)` starts a new trace. Options: `triggerKey`, `ownerKey`, `emitMark`, `runtimeContext`.

`propagateCausalContext(parentContext, currentRecord)` returns a new frozen context with incremented depth, appended chain, accumulated aggregates, and recursion tracking.

### Spine store

Fixed-capacity ring buffer (1024 records) with ordinal time.

```js
import {
    writeToRingBuffer, getRecords, getRecordCount,
    getCurrentTickOrdinal, setTickOrdinal, resetStore,
} from "dev-lab";
```

| Function | Description |
|---|---|
| `writeToRingBuffer(record)` | Writes record to ring buffer. Overwrites oldest when full. |
| `getRecords(limit?)` | Returns frozen array of most recent records. |
| `getRecordCount()` | Number of records currently stored. |
| `getCurrentTickOrdinal()` | Current simulation tick. |
| `setTickOrdinal(n)` | Sets tick ordinal (called by your game loop). |
| `resetStore()` | Clears buffer, facts, and counters. |

### Validation

Records are validated at creation time against two rule sets:

- `INVESTIGATIVE_RULES` (11 rules): causal chain integrity, monotonic indexing, trace propagation, state capture consistency, timing, exception shape, mutation diffs, record kind, causal depth bounds, violation shape, async fork/join.
- `SCHEMA_RULES` (10 rules): field presence, type checking, version compliance.

Violations are frozen into `record.invariantViolations`. `record.status` is `"VALID"` or `"INVALID"`.

```js
import { INVESTIGATIVE_RULES, SCHEMA_RULES, validateInvariants } from "dev-lab";

const violations = validateInvariants(record, preState, postState, causalContext, [
    ...INVESTIGATIVE_RULES,
    ...SCHEMA_RULES,
    { id: "my_custom_rule", message: "Custom check", condition: (r) => r.processMs < 16 },
]);
```

## Query

Importing `dev-lab` auto-registers 11 spine queries + 1 prefix query. Call `collect()` to dispatch.

```js
import { collect } from "dev-lab";

const records = collect("spine.records");
const violations = collect("spine.violations");
const graph = collect("spine.causal");
const detail = collect("spine.record.42");  // prefix query: record by ordinal
```

### Built-in queries

| Query type | Returns |
|---|---|
| `spine.records` | All records from the ring buffer |
| `spine.violations` | All invariant violations across records |
| `spine.mutations` | Records that contain state mutations |
| `spine.lifecycle` | Records with lifecycle transitions |
| `spine.counters` | Per-function execution counts + tick ordinal |
| `spine.causal` | Full causal graph (nodes + edges) |
| `spine.edges` | Causal edge list only |
| `spine.deletions` | Records where `lifecycle === "deleted"` |
| `spine.aggregates` | Aggregate metrics (avg process time, violation density, etc.) |
| `spine.count` | Total record count in the buffer |
| `spine.tick` | Current tick ordinal |
| `spine.record.{N}` | Detail view of record at ordinal N (prefix query) |

### Register custom queries

```js
import { registerQuery, registerQueryPrefix, collect } from "dev-lab";

registerQuery("app.active-users", () => {
    return activeUserList.map((u) => ({ id: u.id, name: u.name }));
}, {
    intent: "return list of active users",
    semantics: "state-query",
    owner: "user-tracker",
});

collect("app.active-users");  // dispatches to handler above

registerQueryPrefix("user.", (suffix, param) => {
    return getUserById(suffix || param);
}, {
    intent: "look up user by ID",
    semantics: "state-query",
    owner: "user-tracker",
});

collect("user.abc-123");  // suffix = "abc-123"
```

### Anomaly query pattern

Register a query that classifies spine records into anomaly types:

```js
import { registerQuery, collect } from "dev-lab";

const STALL_MS = 10;

registerQuery("app.anomalies", () => {
    const records = collect("spine.records");
    const results = [];
    for (const r of records) {
        const types = [];
        if (r.exceptionThrown !== null) types.push("exception");
        if (r.invariantViolations.length > 0) types.push("violation");
        if ((r.semantics === "query" || r.semantics === "state-query") && r.mutations.length > 0) types.push("drift");
        if (r.processMs > STALL_MS) types.push("stall");
        if (r.step > 1 && r.causalParents.length === 0) types.push("orphan");
        if (types.length > 0) results.push({ runtimeIndex: r.runtimeIndex, types, record: r });
    }
    return results;
});

const anomalies = collect("app.anomalies");
```

Five anomaly types:

| Type | Condition | Meaning |
|---|---|---|
| `exception` | `exceptionThrown !== null` | Function threw an error |
| `violation` | `invariantViolations.length > 0` | Invariant rules failed |
| `drift` | Query semantics with mutations | Read-only function mutated state |
| `stall` | `processMs > threshold` | Function exceeded time budget |
| `orphan` | `step > 1` with no causal parents | Mid-chain function missing causal link |

This query is also accessible via MCP: `collect` tool with `{ "queryType": "app.anomalies" }`.

### Query introspection

```js
import { getRegisteredQueryTypes, getRegisteredPrefixes, isRegisteredQuery } from "dev-lab";

getRegisteredQueryTypes();   // ["spine.records", "spine.violations", ...]
getRegisteredPrefixes();     // ["spine.record."]
isRegisteredQuery("spine.causal");  // true
```

### Spine view functions

The view functions are also exported directly for use outside the query dispatch:

```js
import {
    queryViolations, queryMutations, queryDeletions, queryLifecycleTransitions,
    queryCausalEdges, queryCausalGraph, queryRecordDetail, queryAggregateMetrics,
    deriveCounters,
} from "dev-lab";

const counters = deriveCounters();
// { total, executions, polls, exceptions, violations, byFunction }
```

## MCP server

Exposes the spine store and query dispatch to MCP clients (Claude Code, Cursor, browser tools). Two transports: stdio and SSE.

Peer dependencies (install separately):

```
@modelcontextprotocol/sdk ^1.12.0
zod ^3.25.0
```

### Start the stdio server

```bash
node node_modules/dev-lab/mcp/mcp-stdio.js
```

### Start the SSE server

```bash
node node_modules/dev-lab/mcp/mcp-sse.js 3100
```

### MCP client configuration

Claude Code (`.mcp.json` at project root):

```json
{
    "mcpServers": {
        "banes-introspect": {
            "command": "node",
            "args": ["node_modules/dev-lab/mcp/mcp-stdio.js"]
        }
    }
}
```

SSE (browser/remote):

```json
{
    "mcpServers": {
        "banes-introspect": {
            "type": "sse",
            "url": "http://localhost:3100/sse"
        }
    }
}
```

### Tools

| Tool | Description | Input |
|---|---|---|
| `collect` | Dispatch a query against the spine store | `{ queryType: string, param?: string }` |
| `register_query` | Register a new filter query over spine records | `{ key, filters, intent, semantics }` |

### Resources

| URI | Description |
|---|---|
| `spine://records` | Current ring buffer contents |
| `spine://queries` | Registered query types and prefixes |
| `spine://count` | Record count and tick ordinal |
| `spine://aggregates` | Aggregate metrics |
| `spine://violations` | Current invariant violations |

### Filter spec for register_query

Each filter in the `filters` array has three fields:

| Field | Type | Description |
|---|---|---|
| `field` | string | Record field name (e.g. `"status"`, `"processMs"`) |
| `op` | string | One of: `eq`, `neq`, `gt`, `lt`, `gte`, `lte`, `contains` |
| `value` | string, number, boolean, or null | Value to compare against |

Example: register a query that finds all invalid records slower than 10ms:

```json
{
    "key": "slow-invalid",
    "filters": [
        { "field": "status", "op": "eq", "value": "INVALID" },
        { "field": "processMs", "op": "gt", "value": 10 }
    ],
    "intent": "find slow invalid records",
    "semantics": "filter-query"
}
```

### Violation notifications

When a record with invariant violations is written to the ring buffer, the MCP server sends a `resource-updated` notification for `spine://violations`. MCP clients that subscribe to resource changes receive it automatically.

### Programmatic access

Import the server factory directly:

```js
import { createMcpServer } from "dev-lab/mcp";

const { server, notifyViolation } = createMcpServer();
```

## Claude Code integration

dev-lab ships with a slash command and an agent for Claude Code that automate introspect enforcement.

### Setup

Copy the `.claude/` directory from this repo into your project root (or create the files manually):

```
your-project/
  .claude/
    commands/
      introspect.md       # /introspect slash command
    agents/
      introspect-enforcer.md  # enforcement agent
```

### `/introspect` command

Run `/introspect` in Claude Code to fix all enforcement violations from the build registry. The command:

1. Runs your build to refresh the `.code-violations/` output
2. Reads wrapping violations from `.code-violations/introspect-enforcement/introspect-enforcement-0.json`
3. Reads field issues from `.code-violations/introspect-fields/introspect-fields-0.json`
4. Wraps unwrapped functions in `introspect()` with derived intent and semantics
5. Fills pending `intent`/`semantics` fields, removes dead `hookName` options
6. Rebuilds every 5 files to keep registries current
7. Repeats until both violation counts reach 0

The command delegates to the `introspect-enforcer` agent automatically.

### `introspect-enforcer` agent

A registry-driven remediation agent that systematically wraps every function. Key behaviors:

- **Auto-detects build command** from `package.json` scripts (falls back to `npx vite build`)
- **Discovers registries via glob** — works regardless of where your `.code-violations/` is written
- **Derives intent and semantics** using META model analysis (what the function interacts with, its role, and its semantic category)
- **Handles event handlers correctly** — detects parameterized `introspect()` calls and converts to the closure wrapping pattern
- **Stall detection** — stops after 3 cycles with no progress to avoid infinite loops
- **File size verification** — warns when modified files exceed the 150-line limit

The agent uses the official semantics vocabulary (`mutation`, `query`, `side-effect`, `orchestration`, `validation`, `state-mutation`, `state-query`) and lifecycle values (`created`, `mutated`, `deleted`).

### Combined with MCP

When both MCP and Claude Code integration are configured, the AI agent can:

1. Query the live spine via MCP (`collect` tool) to see runtime anomalies
2. Run `/introspect` to fix build-time enforcement violations
3. Register custom filter queries via MCP's `register_query` tool
4. Read `spine://violations` to monitor invariant health in real time

## Vite plugins

```js
// vite.config.js
import { devVite } from "dev-lab/vite";

export default {
    plugins: [
        ...await devVite(
            {
                introspectEnforcement: {},
                fileLength: { max: 150 },
                noInline: {},
                magicNumber: {},
                duplication: {},
                duplicationAudit: {},
                unusedExport: {},
                unusedCss: { cssRoot: "./src/styles" },
                usageCount: {},
                moduleBoundary: {},
                depGraph: {},
                astCapture: {},
            },
            {
                projectRoot: process.cwd(),
                modules: [
                    { name: "my-app", root: "./src", pattern: /\/src\// },
                ],
                primaryModule: "my-app",
                introspectExclusions: {
                    targetModulePattern: /\/src\//,
                    recursionGuardFiles: ["introspect-base.js"],
                    hotPathExclusions: {
                        "render-loop.js": ["tick", "draw"],
                    },
                },
            },
        ),
    ],
};
```

### Available plugins

| Key | What it checks |
|---|---|
| `introspectEnforcement` | Every function body contains an `introspect()` call. Auto-injects `file` and `class` fields. |
| `fileLength` | File line count. Default max: 150. |
| `noInline` | No hardcoded colors, sizes, or inline event handlers. |
| `magicNumber` | No inline numeric literals. |
| `duplication` | No duplicate code blocks (AST-level). |
| `duplicationAudit` | Near-miss detection for similar code blocks. |
| `unusedExport` | No dead exports. |
| `unusedCss` | No unused CSS class selectors. Requires `cssRoot` option. |
| `usageCount` | Tracks definition/reference counts. Reports unused definitions. |
| `moduleBoundary` | Enforces unidirectional import layers between modules. |
| `depGraph` | Generates `dep-graph.md` per module. |
| `astCapture` | Captures pruned AST per file (dev server only). |

### globalOptions

| Field | Type | Required | Description |
|---|---|---|---|
| `projectRoot` | string | yes | Absolute path to project root. |
| `modules` | array | yes | `[{ name, root, pattern }]` where `pattern` is a RegExp matching file paths. |
| `primaryModule` | string | no | Name of the primary module (used by some plugins). |
| `logger` | object | no | `{ info, warn, error, success }`. Defaults to console. |
| `frameworkPatterns` | string[] | no | Path substrings to skip (e.g. `["node_modules"]`). |
| `introspectExclusions` | object | no | See below. |

### introspectExclusions

Passed through `globalOptions.introspectExclusions`. Configures which files the introspect enforcement plugin scans.

| Field | Type | Default | Description |
|---|---|---|---|
| `targetModulePattern` | RegExp | `null` (matches all) | Only enforce on files matching this pattern. |
| `recursionGuardFiles` | string[] | `[]` | File suffixes to skip (the introspect runtime itself). |
| `hotPathExclusions` | object | `{}` | `{ "file-suffix.js": ["fnName1", "fnName2"] }` to exclude specific functions. |

### Custom plugins

Place files in `.dev-vite-plugins/` at project root. Name them `{name}-plugin.js`. Export a function named `{camelName}Plugin(context, options)`.

```
.dev-vite-plugins/
  aria-enforcement-plugin.js   -> exports ariaEnforcementPlugin(context, options)
```

Then include in pluginConfig:

```js
devVite({ ariaEnforcement: {} }, globalOptions)
```

### Build output

Each build writes a `.code-violations/` directory per module containing JSON reports from each plugin. The `dep-graph.md` file is written to each module root.

## Package structure

```
dev-lab/
  index.js                          runtime + query barrel (auto-registers spine queries)
  package.json
  mcp/
    mcp-server.js                   createMcpServer() factory
    mcp-tools.js                    collect + register_query MCP tools
    mcp-resources.js                5 spine resource endpoints
    mcp-stdio.js                    stdio transport entry point
    mcp-sse.js                      SSE transport entry point
  runtime/
    introspect-base.js              introspect(), setRecordObserver()
    introspect-record-builder.js    buildRecord(), computeMutations()
    causal-context.js               createRootCausalContext(), propagateCausalContext()
    introspect-validators.js        evaluateRule(), validateInvariants()
    introspect-field-validator.js   49-field FIELD_SCHEMA, validateRecordFields()
    introspect-invariant-rules.js   11 INVESTIGATIVE_RULES
    introspect-schema-rules.js      10 SCHEMA_RULES
    spine-constants.js              RING_BUFFER_CAPACITY, nextOrdinal()
    spine-store.js                  ring buffer, entityFacts, tick ordinal
  query/
    query-registry.js               registerQuery(), collect(), dispatch engine
    spine-views.js                  queryViolations(), queryCausalGraph(), deriveCounters(), etc.
    spine-query-registrations.js    auto-registers 11 exact + 1 prefix spine query
  vite/
    index.js                        devVite() orchestrator
    plugin-context.js               createPluginContext()
    issue-reporter.js               IssueReporter (JSON registry output)
    module-scanner.js               scanAdditionalModules()
    scanners/
      ast-scanner.js                parseToAST(), isScannable()
      hash-cache.js                 SHA-256 change detection
      css-scanner.js                extractCSSSelectors(), extractCSSCustomProperties()
    helpers/                        10 helper files (AST analysis, reporting, exclusion config)
    rules/                          duplication-allowlist, no-inline-rules, public-api-allowlist
    plugins/                        12 enforcement plugins + base-fixer-plugin factory
```

## Record shape (49 fields)

Every `introspect()` call produces a frozen record with these fields:

| Field | Type | Description |
|---|---|---|
| `runtimeIndex` | number | Monotonic execution counter |
| `file` | string? | Source file (build-injected) |
| `class` | string? | Enclosing class (build-injected) |
| `function` | string | Function name |
| `intent` | string | Declared purpose |
| `semantics` | string | Semantic category |
| `owner` | string? | Owning entity key |
| `schemaVersion` | number | Record schema version (currently 2) |
| `triggeredBy` | string? | Trigger key from causal context |
| `causalParents` | number[] | Parent runtime indices |
| `causalChildren` | number[] | Child indices (always empty at creation) |
| `ordinalTime` | number | Logical sequence number |
| `simTime` | number | Simulation tick at creation |
| `preState` | object? | State snapshot before execution |
| `postState` | object? | State snapshot after execution |
| `mutations` | object[] | `[{ key, before, after }]` diffs |
| `externalMutations` | object[] | External side effects |
| `deliveryMs` | number | Event delivery latency |
| `processMs` | number | Execution wall time |
| `step` | number | Depth in causal chain |
| `chain` | object[] | `[{ functionName, runtimeIndex }]` sliding window |
| `traceGraphId` | string? | UUID grouping a trace |
| `counterfactualSubgraph` | object? | Branching analysis |
| `recursionDepth` | number | Recursion count for this queryPath |
| `executed` | boolean | Always true for execution records |
| `conditionsSatisfied` | object[] | Preconditions met |
| `exceptionThrown` | string? | Error constructor name |
| `exceptionStack` | string? | Stack trace |
| `status` | string | `"VALID"` or `"INVALID"` |
| `invariantViolations` | object[] | `[{ rule, evidence, recordRef }]` |
| `monitorIntrospection` | object? | Monitor-level metadata |
| `context` | object? | Runtime context from causal chain |
| `heapSnapshot` | object? | Memory snapshot |
| `resourceHandles` | object[] | Open resource handles |
| `resourceLatencyMs` | number | Resource access time |
| `emittedEvents` | object[] | Events fired during execution |
| `subscribedEvents` | object[] | Events listened for |
| `asyncForkId` | string? | Async continuation ID |
| `asyncJoinIds` | object[] | Joined async branches |
| `ambiguities` | string[] | Competing outcomes |
| `lifecycle` | string? | `"created"`, `"mutated"`, `"deleted"` |
| `hookName` | string? | Hook identifier |
| `recordKind` | string | `"execution"`, `"poll"`, `"synthetic"`, `"lifecycle"` |
| `queryPath` | string | `file.class.function` lookup key |
| `eventBusPaths` | object[] | Event bus routing paths |
| `aggregates` | object | Accumulated metrics across the causal chain |
| `causalContext` | object | Propagated context for the next call |

## License

MIT
