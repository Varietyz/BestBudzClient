# Starting a project with dev-lab

This walks through setting up a new project where every function is wrapped in `introspect()` and the build enforces it.

## 1. Create the project

```bash
mkdir my-project && cd my-project
npm init -y
```

Edit `package.json` to set `"type": "module"`:

```json
{
    "name": "my-project",
    "version": "1.0.0",
    "type": "module",
    "scripts": {
        "dev": "vite",
        "build": "vite build"
    }
}
```

## 2. Install dependencies

```bash
npm install dev-lab
npm install -D vite @babel/parser @babel/traverse glob
```

## 3. Create the vite config

Create `vite.config.js`:

```js
import { resolve } from "node:path";
import { devVite } from "dev-lab/vite";

const ROOT = resolve(import.meta.dirname);

export default {
    plugins: [
        ...await devVite(
            {
                introspectEnforcement: {},
                fileLength: { max: 150 },
                noInline: {},
                magicNumber: {},
                duplication: {},
                unusedExport: {},
                depGraph: {},
            },
            {
                projectRoot: ROOT,
                modules: [
                    {
                        name: "my-project",
                        root: resolve(ROOT, "src"),
                        pattern: /\/src\//,
                    },
                ],
                primaryModule: "my-project",
                introspectExclusions: {
                    targetModulePattern: /\/src\//,
                    recursionGuardFiles: ["introspect-setup.js"],
                },
            },
        ),
    ],
};
```

What each section does:

- `introspectEnforcement` scans every JS file in `src/` and fails the build if any exported function is not wrapped in `introspect()`. It also auto-injects `file` and `class` fields into every `introspect()` call at build time, so you never write those by hand.
- `targetModulePattern: /\/src\//` tells introspect enforcement to only scan files under `src/`.
- `recursionGuardFiles` lists files that should not be scanned (the introspect setup file itself, to avoid circular enforcement).
- The other plugins are optional. Remove any you do not need.

## 4. Create the entry point

Importing `dev-lab` auto-wires the full pipeline: every `introspect()` call writes its record to the ring buffer, and all spine queries are registered. No setup file needed.

Create `src/main.js`:

```js
import { greet } from "./greet.js";

greet("world");
```

Create `index.html` at project root:

```html
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><title>My Project</title></head>
<body>
    <script type="module" src="/src/main.js"></script>
</body>
</html>
```

## 5. Write your first wrapped function

Create `src/greet.js`:

```js
import { introspect, createRootCausalContext } from "dev-lab";

function greet(name) {
    return introspect(() => {
        const message = `Hello, ${name}`;
        document.body.textContent = message;
        return message;
    }, {
        name: "greet",
        causalContext: createRootCausalContext({ triggerKey: "app-boot" }),
        preStateCapture: () => ({ bodyText: document.body.textContent }),
        postStateCapture: () => ({ bodyText: document.body.textContent }),
        intent: "display greeting",
        semantics: "side-effect",
    }).result;
}

export { greet };
```

At build time, the enforcement plugin reads the AST and auto-injects two fields into the options object:

```js
// What you write:
{ name: "greet", causalContext: ... }

// What the build outputs:
{ file: "src/greet.js", class: null, name: "greet", causalContext: ... }
```

You never add `file` or `class` yourself. The plugin handles it.

## 6. Build and verify

```bash
npm run build
```

If every function is wrapped, the build passes. If a function is missing `introspect()`, the build fails with a message like:

```
[introspect-enforcement] src/greet.js
  Unwrapped: greet() :7
```

The number after the colon is the line where the unwrapped function starts.

## 7. Run the dev server

```bash
npm run dev
```

Open the browser. Every function call now produces a frozen record in the spine store.

## 8. Read records at runtime

Add a query anywhere in your code:

```js
import { getRecords, getRecordCount } from "dev-lab";

console.log(`${getRecordCount()} records in spine`);

const recent = getRecords(5);
for (const r of recent) {
    console.log(r.queryPath, r.status, `${r.processMs.toFixed(2)}ms`);
}
```

Each record contains 49 fields. The useful ones for debugging:

| Field | What it tells you |
|---|---|
| `queryPath` | `file.class.function` identifier |
| `status` | `"VALID"` or `"INVALID"` |
| `processMs` | How long the function body took |
| `mutations` | `[{ key, before, after }]` state diffs |
| `invariantViolations` | What rules broke and why |
| `causalContext.chain` | The call path that led here |
| `causalContext.depth` | How deep in the causal chain |

## 9. Query the spine with collect()

`collect()` dispatches named queries against the spine store. 11 spine queries are registered automatically when you import `dev-lab`.

```js
import { collect } from "dev-lab";

// All records
const records = collect("spine.records");

// Only violations
const violations = collect("spine.violations");

// Only mutations
const mutations = collect("spine.mutations");

// Causal graph (nodes + edges)
const graph = collect("spine.causal");

// Per-function execution counts
const counters = collect("spine.counters");
// { total, executions, polls, exceptions, violations, byFunction, capturedAtTick }

// Single record by ordinal (prefix query)
const detail = collect("spine.record.42");

// Aggregate metrics
const agg = collect("spine.aggregates");
// { total, validCount, invalidCount, avgProcessMs, avgMutationDensity, ... }
```

Full list of built-in queries: `spine.records`, `spine.violations`, `spine.mutations`, `spine.lifecycle`, `spine.counters`, `spine.causal`, `spine.edges`, `spine.deletions`, `spine.aggregates`, `spine.count`, `spine.tick`, `spine.record.{N}`.

Register project-specific queries:

```js
import { registerQuery, collect } from "dev-lab";

registerQuery("app.session-state", () => {
    return { userId: session.userId, role: session.role, active: true };
}, {
    intent: "return current session state",
    semantics: "state-query",
    owner: "session-manager",
});

collect("app.session-state");  // { userId: "abc", role: "admin", active: true }
```

## 10. Propagate causal context through call chains

When one function calls another, pass the causal context so the chain stays connected:

```js
import { introspect, createRootCausalContext, propagateCausalContext } from "dev-lab";

function handleClick(event) {
    return introspect(() => {
        const root = createRootCausalContext({ triggerKey: "button-click" });
        const { record } = introspect(() => {
            validate();
        }, {
            name: "validate",
            causalContext: root,
            preStateCapture: null,
            postStateCapture: null,
        });

        const next = propagateCausalContext(root, record);

        introspect(() => {
            submit();
        }, {
            name: "submit",
            causalContext: next,
            preStateCapture: null,
            postStateCapture: null,
        });
    }, {
        name: "handleClick",
        causalContext: createRootCausalContext({ triggerKey: "button-click" }),
        preStateCapture: null,
        postStateCapture: null,
        intent: "process button click",
        semantics: "orchestration",
    }).result;
}
```

The chain builds up: `handleClick` -> `validate` -> `submit`. Each record's `causalContext.chain` shows the path.

## 11. Track state mutations

Use `preStateCapture` and `postStateCapture` to diff state before and after execution:

```js
function updateScore(state, points) {
    return introspect(() => {
        state.score += points;
        state.lastUpdate = Date.now();
    }, {
        name: "updateScore",
        causalContext: null,
        preStateCapture: () => ({ score: state.score }),
        postStateCapture: () => ({ score: state.score }),
        intent: "add points to score",
        semantics: "mutation",
        lifecycle: "mutated",
    }).result;
}
```

The record's `mutations` field will contain `[{ key: "score", before: 0, after: 10 }]`.

## 12. Add custom invariant rules

You can extend the built-in rules with project-specific checks:

```js
import { INVESTIGATIVE_RULES, SCHEMA_RULES, validateInvariants } from "dev-lab";

const PROJECT_RULES = [
    {
        id: "proj_no_slow_functions",
        message: "Function exceeded 16ms budget",
        condition: (record) => record.processMs < 16,
    },
    {
        id: "proj_mutations_require_lifecycle",
        message: "Mutations detected but lifecycle field not set",
        condition: (record) => {
            if (record.mutations.length === 0) return true;
            return record.lifecycle !== null;
        },
    },
];

const ALL_RULES = [...INVESTIGATIVE_RULES, ...SCHEMA_RULES, ...PROJECT_RULES];
```

Pass `ALL_RULES` to `validateInvariants()` for manual validation, or use `setRecordObserver` to check every record automatically:

```js
import { setRecordObserver, writeToRingBuffer, validateInvariants } from "dev-lab";

setRecordObserver((record) => {
    writeToRingBuffer(record);
    const extra = validateInvariants(record, record.preState, record.postState, record.causalContext, PROJECT_RULES);
    if (extra.length > 0) {
        console.warn(`[${record.queryPath}] ${extra.length} project rule violations`);
    }
});
```

## 13. Add custom build plugins

Create `.dev-vite-plugins/` at project root. Each file must export a function named `{camelCase}Plugin`:

```
.dev-vite-plugins/
  no-console-plugin.js
```

```js
// .dev-vite-plugins/no-console-plugin.js
export function noConsolePlugin(context) {
    const { isScannable, report } = context;

    return {
        name: "no-console",
        enforce: "post",

        transform(code, id) {
            if (!isScannable(id)) return null;
            report.scanned("no-console");

            const pattern = /console\.(log|warn|error|info|debug)\s*\(/g;
            let match;
            while ((match = pattern.exec(code)) !== null) {
                const line = code.slice(0, match.index).split("\n").length;
                report.error("no-console", id, null, `console.${match[1]}() at line ${line}`);
            }
            return null;
        },
    };
}
```

Then register it in vite.config.js:

```js
devVite(
    {
        introspectEnforcement: {},
        noConsole: {},  // auto-discovered from .dev-vite-plugins/
    },
    globalOptions,
)
```

## Summary of what the build enforces

After this setup, every `npm run build` will:

1. Parse every JS file in `src/` to an AST.
2. Check that every exported function contains an `introspect()` call. Fail if not.
3. Auto-inject `file` and `class` fields into every `introspect()` options object.
4. Run all other enabled plugins (file length, no inline styles, magic numbers, duplication, unused exports, dependency graph).
5. Write a `.code-violations/` folder with JSON reports per plugin.
6. Write `dep-graph.md` to the module root.

At runtime, importing `dev-lab` auto-wires the complete pipeline:

1. Every `introspect()` call produces a frozen record.
2. The record observer writes it to the ring buffer automatically.
3. Spine queries are registered automatically.
4. `collect("spine.records")` returns all stored records. No setup code needed.

Add a function, wrap it in `introspect()`, and it is immediately stored and queryable. The build enforces wrapping. The runtime handles storage and dispatch.

## 14. Connect AI agents via MCP

The MCP server exposes the spine store and query dispatch to AI agents. Start the stdio server:

```bash
node node_modules/dev-lab/mcp/mcp-stdio.js
```

Add to your Claude Code `.mcp.json` at project root:

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

The AI agent can now query the spine store directly:

1. Call the `collect` tool with `{ "queryType": "spine.count" }` to see how many records exist.
2. Call `collect` with `{ "queryType": "spine.violations" }` to see all invariant violations.
3. Call `collect` with `{ "queryType": "spine.aggregates" }` for aggregate metrics.
4. Read the `spine://records` resource for the full ring buffer contents.
5. Use `register_query` to create a custom filter, then `collect` on it:

```json
{
    "key": "agent.slow-calls",
    "filters": [{ "field": "processMs", "op": "gt", "value": 10 }],
    "intent": "find functions slower than 10ms",
    "semantics": "filter-query"
}
```

Then: `collect` with `{ "queryType": "agent.slow-calls" }` returns matching records.

For SSE transport (browser/remote), start with a port:

```bash
node node_modules/dev-lab/mcp/mcp-sse.js 3100
```

## 15. Fix enforcement violations with `/introspect`

dev-lab includes a Claude Code slash command that automates wrapping every function in `introspect()`.

Copy the `.claude/` directory from the dev-lab repo into your project root:

```
your-project/
  .claude/
    commands/
      introspect.md
    agents/
      introspect-enforcer.md
```

Then in Claude Code, run:

```
/introspect
```

The command reads your build registry, finds every unwrapped function, and wraps it with derived intent, semantics, and state captures. It rebuilds every 5 files and repeats until all violations are resolved.

What it does for each function:

1. Reads the function body and analyzes what it interacts with (DOM, state, APIs)
2. Determines semantics from the official vocabulary (`mutation`, `query`, `side-effect`, `orchestration`, `validation`, `state-mutation`, `state-query`)
3. Derives a specific intent string (e.g. `"add new item to state"`, not `"handle data"`)
4. Sets `lifecycle` for mutations (`created`, `mutated`, `deleted`)
5. Adds `preStateCapture`/`postStateCapture` for functions that touch mutable state
6. Handles event handlers correctly (closure pattern, not direct wrapping)

The agent also fills `"pending"` intent/semantics values and removes dead `hookName` fields from existing `introspect()` calls.

## 16. Register an anomaly query

Register a project-specific query that classifies spine records into anomaly types. This is useful for runtime monitoring and for MCP-connected AI agents.

Create `src/queries.js`:

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
```

Import it as a side-effect in your entry point so the query registers on startup:

```js
// src/main.js
import "./queries.js";
```

Now `collect("app.anomalies")` returns all records that have at least one anomaly, with their types:

```js
const anomalies = collect("app.anomalies");
// [{ runtimeIndex: 12, types: ["violation", "drift"], record: { ... } }, ...]
```

The five anomaly types:

| Type | Condition | What it means |
|---|---|---|
| `exception` | `exceptionThrown !== null` | The function threw an error |
| `violation` | `invariantViolations.length > 0` | One or more invariant rules failed |
| `drift` | Query/state-query semantics with mutations | A read-only function mutated state |
| `stall` | `processMs > STALL_MS` | The function exceeded the time budget |
| `orphan` | `step > 1` with empty `causalParents` | A mid-chain function is missing its causal link |

Adjust `STALL_MS` to match your performance budget. The query is also accessible via MCP: call `collect` with `{ "queryType": "app.anomalies" }`.
