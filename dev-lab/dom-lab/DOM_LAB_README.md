# dom-lab

Canvas-less 2D physics engine. Applies rigid body simulation directly to HTML elements. Elements remain live document participants: inputs accept typing, text is selectable, accessibility works natively, strict CSP compliant by default.

## Install

```bash
npm install dom-lab
```

## Quick Start

```js
import { create, release, on } from "dom-lab";

// Create a physics-enabled element
const box = create({
  tag: "div",
  aria: "physics-box",
  className: "box",
  physics: { radius: 25, restitution: 0.7 },
  drag: { bounds: "viewport" },
});

box.attach(document.body);

// Listen to events
on("entity:created", (event) => {
  console.log("Created:", event.target.schema.aria);
});

// Teardown
release(box);
```

## create() Schema

The `create(schema)` function is the single entry point for DOM element creation.

### Core Keys

| Key | Type | Required | Description |
|-----|------|----------|-------------|
| `tag` | string | Yes | HTML/SVG/MathML tag name |
| `aria` | string | Yes | Unique kebab-case identifier. Set as aria-label. |
| `id` | string | No | Sets element.id |
| `className` | string | No | Sets element.className |
| `text` | string | No | Sets element.textContent |
| `children` | Array | No | Child schemas (recursive) or text strings |
| `culling` | boolean | No | Default true. Set false to skip frustum culler. |
| `generic` | boolean | No | Uses data-label instead of aria-label, skips aria registry |
| `opaque` | boolean | No | Hint for occlusion culling |
| `attributes` | object | No | HTML attributes |
| `properties` | object | No | DOM properties (element[prop] = value) |
| `capabilities` | false or string[] | No | false: no caps. string[]: whitelist |
| `shadow` | object | No | { mode: "open" or "closed", styles?: string[], inlineStyles?: string } |
| `style` | object | No | Inline styles |
| `cssVars` | object | No | CSS custom properties |
| `on` | object | No | DOM event listeners |
| `onWindow` | object | No | Window-level event listeners |

### Capability Keys

Any additional key is resolved against registered capabilities. Accepts `true`, config object, or `null`/`false` (disabled).

**Physics:**
`physics`, `collision`, `drag`, `gravity`, `joint`, `rope`, `chain`, `breakable`, `motor`, `attractField`, `repelField`, `physicsContainer`, `cradle`, `mesh`

**Input:**
`input`, `keyboard`, `gesture`, `focus`, `scroll`, `droppable`, `haptic`

**Visual:**
`animation`, `transition`, `visibility`, `style_`, `tooltip`, `sprite`, `autoscale`, `reducedMotion`

**Data/IO:**
`data`, `backend`, `localStorage`, `network`, `history`, `validate`

**AI:**
`aiContext`, `aiExecute`, `mcp`

**GPU:**
`gpuCanvas`, `gpuParticles`, `gpuPostfx`

**Game:**
`spawner`, `cooldown`, `typewriter`, `tilemap`, `pathfinding`

**Other:**
`audio`, `debug`, `test`, `inspection`, `on`, `windowEvent`, `aria`, `ariaEnrich`, `viewport`

## DomEntity

```js
const entity = create({ tag: "div", aria: "my-element" });

entity.entityId;    // number (monotonic)
entity.uuid;        // crypto.randomUUID()
entity.element;     // HTMLElement
entity.schema;      // original schema
entity.capabilities; // [{key, instance}]
entity.children;    // DomEntity[]
entity.isAttached;  // boolean
entity.isDestroyed; // boolean

entity.attach(parentElement);
entity.detach();
entity.destroy();

entity.setCssVar("--color", "red");
entity.setText("hello");
entity.setHTML("<b>html</b>");
entity.setClass("active");
```

**Lifecycle:** CREATED -> attach() -> ATTACHED -> detach() -> DETACHED -> destroy() -> DESTROYED

### Entity Registry

```js
import { getAllEntities, getEntityByElement, getEntityById } from "dom-lab";

getAllEntities();           // DomEntity[]
getEntityByElement(el);    // DomEntity | undefined
getEntityById(42);         // DomEntity | undefined
```

## Event Bus

```js
import { on, off, emit } from "dom-lab";

on("entity:created", handler);
off("entity:created", handler);
emit("custom:event", target, { key: "value" });
```

Event shape: `{ type, target, detail, timestamp }`

### Key Events

- `entity:created`, `entity:attached`, `entity:detached`, `entity:destroyed`
- `entity:culled`, `entity:visible`
- `physics:collision:enter`, `physics:collision:exit`
- `physics:collision:start`, `physics:collision:active`, `physics:collision:end`
- `physics:sleep:start`, `physics:sleep:end`
- `drag:start`, `drag:end`
- `ai:action:executed`, `ai:execution:completed`
- `backend:load`, `backend:save`, `backend:error`
- `localStorage:saved`, `localStorage:loaded`
- `gpu:contextLost`, `gpu:contextRestored`

## Capabilities

58 capabilities auto-register. Register custom ones:

```js
import { registerCapability } from "dom-lab";

registerCapability("myFeature", (config) => ({
  onAttach(element, config, context) { /* ... */ },
  onDetach(element, config) { /* ... */ },
  onDestroy(element, config) { /* ... */ },
  aiActions: ["do-thing"],
  aiState: (element) => ({ active: true }),
}));
```

Valid hooks: `apply`, `onAttach`, `onDetach`, `onDestroy`, `aiActions`, `aiState`

### BaseCapability

```js
import { BaseCapability } from "dom-lab/base";

class MyCapability extends BaseCapability {
  attach(element, config) {
    this.trackListener(element, "click", handler);
    this.registerAIAction(element, "do-thing", handler);
    this.trackCleanup(() => /* cleanup */);
  }
}
```

## Physics

240 FPS fixed timestep Verlet integration. Transforms via CSS custom properties.

```js
import { getPhysicsShapeManager } from "dom-lab";

const manager = getPhysicsShapeManager();
manager.attach(document.body);

// Schema-driven (recommended)
create({
  tag: "div", aria: "ball",
  physics: { radius: 20, restitution: 0.8 },
  collision: { layer: "balls", collidesWith: ["walls"] },
});

// Direct API
manager.raycast(0, 0, 1, 0, 500);
manager.queryPoint({ x: 100, y: 200 });
manager.queryRegion({ x: 0, y: 0, width: 400, height: 300 });
manager.setDebug(true);
manager.setChaosMode(true);
manager.setTimeScale(0.5);

manager.detach();
```

Physics pipeline per substep: Verlet -> Gravity -> Force Fields -> Mouse -> Collision -> Constraints -> Dynamics -> Render

## AI Bridge

```js
import {
  ariaRegistry,
  extractDOMSchema,
  executeAction,
  executeActionGraph,
  buildAIPrompt,
  configureMCP, enableMCP,
} from "dom-lab";

// Schema extraction
const schema = extractDOMSchema(document.body);

// Single action
executeAction({ target: "my-button", action: "move-to", parameters: { x: 100, y: 200 } });

// Action graph
await executeActionGraph({
  mode: "sequential",
  steps: [
    { target: "player", action: "apply-impulse", parameters: { x: 5, y: -10 } },
    { delay: 300 },
    { target: "player", action: "reset-velocity" },
  ],
});

// Prompt building (PAG format)
const prompt = buildAIPrompt(document.body);

// MCP
configureMCP({ transport: "websocket", endpoint: "ws://localhost:8080" });
enableMCP();
```

## Monitor

```js
import { diagnose, takeFullSnapshot, startTracing, queryCounters, recordHealth } from "dom-lab";

const snapshot = takeFullSnapshot(0);
const health = diagnose(snapshot);
// { overall: "healthy"|"violated", violations: [...], verdicts: violations }

startTracing();
const counters = queryCounters();

recordHealth(health.overall);
```

Health rules check: pool exhaustion, entity leaks, render backlog, cull efficiency, grid fragmentation, attach asymmetry.

## DOM Console

```js
import { domMonitor } from "dom-lab";

const monitor = domMonitor({ container: document.body });
monitor.toggle();
monitor.destroy();
```

Visual overlay with FPS widget, stats cards, trace log, detail views, and inspection overlay.

## Logger

Logging is event-driven. The logger consumer subscribes to `introspect:record` events and derives console output from 4 semantic rules: exception (error), violation (warn), lifecycle (trace), slow-execution (warn). No manual log calls needed for introspect-wrapped code.

```js
import { scoped, createBuffer } from "dom-lab/logger";

const log = scoped({ level: "debug", prefix: "my-module", format: "rich" });
log("step complete");

const buffer = createBuffer(100);
buffer.push(entry);
buffer.release();
```

Formats: rich, compact, verbose, minimal, json. Domain-colored browser output. ANSI node output.

## Render

```js
import { applyRenderOptimizations, removeRenderOptimizations, isOptimized } from "dom-lab";

applyRenderOptimizations(); // auto-called on first create()
removeRenderOptimizations();
isOptimized(); // boolean
```

Applies GPU acceleration, font smoothing, contain: layout style, physics-active class, and reduced-motion support.

## Package Exports

```json
{
  ".": "./index.js",
  "./events": "./events/dom-event-bus.js",
  "./logger": "./logger/logger.js",
  "./test": "./test/index.js"
}
```

## TypeScript

Type declarations at `types/index.d.ts`. Covers all public API: EntitySchema, DomEntity, PhysicsConfig, all capability configs, event types, monitor types.

## License

MIT
