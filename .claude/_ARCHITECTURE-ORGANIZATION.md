# Architecture Organization - 4-Axis Meta-Architecture

**Purpose**: Canonical, enforceable rules for file naming, placement, and structural organization.

**Status**: CANONICAL - All architectural decisions must comply with this document.

**Version**: 2.0 (4-Axis Model)

---

## LAW 0: Single Concern Enforcement

**If a file causes naming ambiguity, it violates single responsibility.**

**Resolution**: SPLIT the file into proper architectural separation.

### Examples of Violations

❌ **Stream parser with state/lifecycle**
```typescript
// ❌ WRONG: Parser with lifecycle (mixed concerns)
export class StreamParser {
    private buffer: string[] = [];
    init() { this.buffer = []; }
    parse(chunk: string) { /* stateful parsing */ }
}
```

✅ **Split into proper roles**
```typescript
// ✅ CORRECT: Pure parser (Axis 2: HELPS)
export function parseChunk(chunk: string): ParsedData {
    // Pure transformation
}

// ✅ CORRECT: Stream handler (Axis 1: STABLE)
export class StreamHandler extends BaseHandler {
    private buffer: string[] = [];
    init() { this.buffer = []; }
    processChunk(chunk: string) {
        const parsed = parseChunk(chunk); // Uses parser
        // Handle state/lifecycle
    }
}
```

❌ **File exporting multiple concerns**
```typescript
// ❌ WRONG: Mixed types and functions
export interface UserData { }
export function formatUser() { }
```

✅ **Split by axis**
```typescript
// ✅ File 1: src/shared/types/user-types.ts (Axis 3)
export interface UserData { }

// ✅ File 2: src/shared/helpers/user-helper.ts (Axis 2)
export function formatUser() { }
```

**Rule**: If you cannot determine the axis/role from filename alone, the file has too many concerns.

---

## Core Principle: 4-Axis Software Architecture

Software development operates across **4 distinct axes**:

1. **STABLE** - What the system **DOES** (runtime execution)
2. **HELPS STABLE** - What **ASSISTS** execution (functional support)
3. **DEFINES STABLE** - What **SHAPES** what's possible (definitions)
4. **EXTERNAL INFLUENCE** - What **ORCHESTRATES** from outside (tooling)

---

## Axis 1: STABLE (Runtime Execution)

### Definition
Behavioral code that executes at runtime with lifecycle, state, and side effects.

### Roles
- `manager` - Coordinates subsystem behavior
- `service` - Provides domain-specific operations
- `component` - UI elements with lifecycle
- `database` - Data persistence layer
- `queue` - Work scheduling and execution
- `handler` - Request/event processing (IPC, HTTP, etc.)
- `registry` - Entity registration and resolution
- `provider` - Capability provisioning
- `store` - State management
- `tracker` - Observation and monitoring
- `factory` - Complex object creation

### Tier Structure
**Tier 1 (Base Classes)**:
```
src/{context}/base/base-{role}.ts
  → export abstract class Base{Role}
```

**Tier 2 (Implementations)**:
```
src/{context}/{role}s/{domain}-{role}.ts
  → export class {Domain}{Role} extends Base{Role}
```

### Rules
- ✅ MUST extend Tier 1 base class
- ✅ MUST have lifecycle methods (init, cleanup)
- ✅ Folder is plural (`managers/`, `services/`)
- ✅ Filename ends with `-{role}.ts` (singular)
- ✅ One primary class export per file

### Examples
```typescript
// Tier 1: Base class
// src/main/base/base-manager.ts
export abstract class BaseManager {
    protected managerId: string;
    abstract init(): Promise<void>;
    abstract cleanup(): Promise<void>;
}

// Tier 2: Implementation
// src/main/managers/terminal-manager.ts
export class TerminalManager extends BaseManager {
    async init() { /* ... */ }
    async cleanup() { /* ... */ }
}
```

### Validation
```
✅ src/main/managers/terminal-manager.ts → extends BaseManager
✅ src/main/services/hardware-service.ts → extends BaseService
✅ src/main/handlers/claude-ipc-handler.ts → extends BaseHandler

❌ src/main/terminal/manager.ts → Missing domain prefix
❌ src/main/managers/terminal.ts → Missing role suffix
❌ src/main/managers/TerminalManager.ts → PascalCase (use kebab-case)
```

---

## Axis 2: HELPS STABLE (Support/Functional)

### Definition
Pure functions or stateless classes that assist runtime execution without lifecycle.

### Roles
- `helper` - Pure utility functions
- `util` - General-purpose utilities
- `parser` - Data transformation (PURE ONLY - see Parser Rule below)
- `validator` - Input validation (pure functions)
- `formatter` - Output formatting

### Parser Classification Rule

**Critical**: Parsers in Axis 2 MUST be stateless transformations.

```
IF parser needs state/lifecycle/buffer:
  → SPLIT into:
    1. Pure parser function (Axis 2: HELPS)
    2. Stateful handler/processor (Axis 1: STABLE)

Examples:
  ✅ Axis 2: function parseAnsiCode(code: string): AnsiToken
  ❌ Axis 2: class StreamParser with buffer (VIOLATION)
  ✅ Axis 1: class StreamHandler extends BaseHandler (uses parser)
```

**Violation**: A "parser" with `init()`, `cleanup()`, or instance state → NOT Axis 2

**Resolution**: Rename to `processor`, `handler`, or `engine` and move to Axis 1

### Tier Structure
**Tier 2 (Functional Artifacts)**:
```
src/{context}/{role}s/{domain}-{role}.ts
  → export function {functionName}()
```

### Rules
- ✅ NO base class inheritance
- ✅ MUST be pure functions or stateless classes
- ✅ NO lifecycle methods (no init/cleanup)
- ✅ Folder is plural (`helpers/`, `utilities/`)
- ✅ Filename ends with `-{role}.ts` (singular)

### Examples
```typescript
// src/shared/helpers/format-helper.ts
export function formatBytes(bytes: number): string {
    return `${(bytes / 1024).toFixed(2)} KB`;
}

export function formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
}

// src/main/validators/schema-validator.ts
export function validateSchema(data: unknown, schema: Schema): ValidationResult {
    // Pure validation logic
    return { valid: true, errors: [] };
}
```

### Validation
```
✅ src/shared/helpers/format-helper.ts → Pure functions
✅ src/main/parsers/ansi-parser.ts → Stateless parser
✅ src/main/validators/input-validator.ts → Pure validation

❌ src/shared/helpers/format-helpers.ts → Plural suffix (use singular)
❌ src/main/helpers/helper.ts → Missing domain prefix
❌ src/main/helpers/FormatHelper.ts → PascalCase (use kebab-case)
```

---

## Axis 3: DEFINES STABLE (Definitions/Declarative)

### Definition
Declarative code that shapes what's possible - no runtime execution.

### Roles
- `types` - TypeScript interfaces, types, enums
- `constants` - Immutable configuration values
- `config` - System configuration
- `schemas` - Data structure definitions

### Tier Structure
**Centralized in `src/shared/`**:
```
src/shared/{artifact}s/{domain}-{artifact}.ts
  → export interface/type/const
```

### Rules
- ✅ ALL definitions MUST be in `src/shared/`
- ✅ NO base class inheritance
- ✅ NO runtime execution
- ✅ Folder is plural (`types/`, `constants/`)
- ✅ Filename ends with `-{artifact}.ts` (plural for aggregates)

### Examples
```typescript
// src/shared/types/terminal-types.ts
export interface TerminalSession {
    id: string;
    pid: number;
}

export type ShellType = "bash" | "zsh" | "powershell";

// src/shared/constants/ipc-constants.ts
export const IPC_CHANNEL_PREFIX = "archlab:";
export const MAX_MESSAGE_SIZE = 1024 * 1024; // 1MB
```

### Validation
```
✅ src/shared/types/terminal-types.ts → Centralized definitions
✅ src/shared/constants/ipc-constants.ts → Centralized constants
✅ src/shared/config/app-config.ts → Centralized config

❌ src/main/terminal/terminal-types.ts → Must be in shared/types/
❌ src/renderer/constants/ui-constants.ts → Must be in shared/constants/
❌ src/shared/types/types.ts → Missing domain prefix
```

---

## Axis 4: EXTERNAL INFLUENCE (Tooling/Scripts)

### Definition
Development/build-time orchestration that runs outside the application runtime.

### Roles
- `script` - Build, migration, validation scripts

### Location
**Outside `src/` tree**:
```
scripts/{name}-script.ts
```

### Rules
- ✅ MUST be outside `src/` directory
- ✅ MUST use `-script.ts` suffix
- ✅ NO base class inheritance
- ✅ Standalone executable

### Examples
```typescript
// scripts/migrate-script.ts
async function migrate() {
    console.log("Running migrations...");
    // Migration logic
}

migrate();

// scripts/build-script.ts
async function build() {
    console.log("Building project...");
    // Build logic
}

build();
```

### Validation
```
✅ scripts/migrate-script.ts → Outside src/, has suffix
✅ scripts/validate-script.ts → Outside src/, has suffix
✅ scripts/build-script.ts → Outside src/, has suffix

❌ src/main/scripts/migrate.ts → Must be outside src/
❌ scripts/migrate.ts → Missing -script suffix
❌ scripts/MigrateScript.ts → PascalCase (use kebab-case)
```

---

## Axis Assignment Table (Canonical Reference)

| Role | Axis | Inheritance | Location | Pattern |
|------|------|-------------|----------|---------|
| **manager** | 1: STABLE | YES (BaseManager) | `src/{context}/managers/` | `{domain}-manager.ts` |
| **service** | 1: STABLE | YES (BaseService) | `src/{context}/services/` | `{domain}-service.ts` |
| **component** | 1: STABLE | YES (BaseComponent) | `src/{context}/components/` | `{domain}-component.ts` |
| **database** | 1: STABLE | YES (BaseDatabase) | `src/{context}/databases/` | `{domain}-database.ts` |
| **queue** | 1: STABLE | YES (BaseQueue) | `src/{context}/queues/` | `{domain}-queue.ts` |
| **handler** | 1: STABLE | YES (BaseHandler) | `src/{context}/handlers/` | `{domain}-{area}-handler.ts` |
| **registry** | 1: STABLE | YES (BaseRegistry) | `src/{context}/registries/` | `{domain}-registry.ts` |
| **provider** | 1: STABLE | YES (BaseProvider) | `src/{context}/providers/` | `{domain}-provider.ts` |
| **store** | 1: STABLE | YES (BaseStore) | `src/{context}/stores/` | `{domain}-store.ts` |
| **tracker** | 1: STABLE | YES (BaseTracker) | `src/{context}/trackers/` | `{domain}-tracker.ts` |
| **factory** | 1: STABLE | YES (BaseFactory) | `src/{context}/factories/` | `{domain}-factory.ts` |
| **helper** | 2: HELPS | NO | `src/{context}/helpers/` | `{domain}-helper.ts` |
| **util** | 2: HELPS | NO | `src/{context}/utilities/` | `{domain}-util.ts` |
| **parser** | 2: HELPS | NO | `src/{context}/parsers/` | `{domain}-parser.ts` |
| **validator** | 2: HELPS | NO | `src/{context}/validators/` | `{domain}-validator.ts` |
| **formatter** | 2: HELPS | NO | `src/{context}/formatters/` | `{domain}-formatter.ts` |
| **types** | 3: DEFINES | NO | `src/shared/types/` | `{domain}-types.ts` |
| **constants** | 3: DEFINES | NO | `src/shared/constants/` | `{domain}-constants.ts` |
| **config** | 3: DEFINES | NO | `src/shared/config/` | `{domain}-config.ts` |
| **schemas** | 3: DEFINES | NO | `src/shared/schemas/` | `{domain}-schemas.ts` |
| **script** | 4: EXTERNAL | NO | `scripts/` | `{name}-script.ts` |

---

## Naming Rules (Universal)

### R1: Kebab-Case Files
```
✅ terminal-manager.ts
✅ ipc-handler.ts
✅ format-helper.ts

❌ TerminalManager.ts
❌ terminalManager.ts
❌ terminal_manager.ts
```

### R2: Export Matches Filename
```typescript
// ✅ terminal-manager.ts
export class TerminalManager { }

// ❌ terminal-manager.ts
export class Manager { }  // Missing domain
```

**Conversion**: `kebab-case` filename → `PascalCase` export

### R3: Domain Always Present
```
✅ terminal-manager.ts     # Has domain: terminal
✅ claude-service.ts       # Has domain: claude
✅ ipc-handler.ts          # Has domain: ipc

❌ manager.ts              # Missing domain
❌ service.ts              # Missing domain
```

### R4: Role Suffix Required (Axis 1 & 2)
```
✅ terminal-manager.ts     # manager suffix
✅ format-helper.ts        # helper suffix
✅ ansi-parser.ts          # parser suffix

❌ terminal.ts             # Missing role
❌ format.ts               # Missing role
```

### R5: Plural Folders, Singular Files
```
✅ managers/terminal-manager.ts
✅ helpers/format-helper.ts
✅ types/terminal-types.ts      # Exception: types is plural

❌ manager/terminal-manager.ts
❌ helper/format-helper.ts
```

### R6: Composite Roles (Multi-Segment Names)

Some roles require **sub-domain** specification (e.g., handlers for different protocols).

**Pattern**: `{domain}-{subdomain}-{role}.ts`

**Applies to**: `handler`, `processor`, `engine` (complex cross-cutting roles)

```
✅ claude-ipc-handler.ts        # domain=claude, subdomain=ipc, role=handler
✅ terminal-ipc-handler.ts      # domain=terminal, subdomain=ipc, role=handler
✅ ai-stream-processor.ts       # domain=ai, subdomain=stream, role=processor

❌ ipc-handler.ts               # Missing domain
❌ claude-handler.ts            # Missing subdomain (what kind of handler?)
```

**Rule**: If removing the subdomain makes the filename ambiguous, the subdomain is required.

---

## Folder Structure (Canonical)

```
root/archlab-ide/
├── src/
│   ├── main/                         # Main process
│   │   ├── base/                     # Tier 1: Base classes (Axis 1)
│   │   │   ├── base-manager.ts
│   │   │   ├── base-service.ts
│   │   │   ├── base-handler.ts
│   │   │   └── base-database.ts
│   │   │
│   │   ├── managers/                 # Tier 2: STABLE (Axis 1)
│   │   │   ├── terminal-manager.ts
│   │   │   └── window-manager.ts
│   │   │
│   │   ├── services/                 # Tier 2: STABLE (Axis 1)
│   │   │   └── hardware-service.ts
│   │   │
│   │   ├── handlers/                 # Tier 2: STABLE (Axis 1)
│   │   │   ├── claude-ipc-handler.ts
│   │   │   └── terminal-ipc-handler.ts
│   │   │
│   │   ├── databases/                # Tier 2: STABLE (Axis 1)
│   │   │   ├── claude-database.ts
│   │   │   └── terminal-database.ts
│   │   │
│   │   ├── helpers/                  # Tier 2: HELPS (Axis 2)
│   │   │   └── preload-helper.ts
│   │   │
│   │   └── validators/               # Tier 2: HELPS (Axis 2)
│   │       └── schema-validator.ts
│   │
│   ├── renderer/                     # Renderer process
│   │   ├── base/                     # Tier 1: Base classes (Axis 1)
│   │   │   └── base-component.ts
│   │   │
│   │   ├── components/               # Tier 2: STABLE (Axis 1)
│   │   │   ├── modal-component.ts
│   │   │   └── toast-component.ts
│   │   │
│   │   ├── stores/                   # Tier 2: STABLE (Axis 1)
│   │   │   └── app-store.ts
│   │   │
│   │   └── helpers/                  # Tier 2: HELPS (Axis 2)
│   │       └── dom-helper.ts
│   │
│   └── shared/                       # Shared definitions (Axis 3)
│       ├── types/                    # DEFINES
│       │   ├── terminal-types.ts
│       │   ├── claude-types.ts
│       │   └── engine-types.ts
│       │
│       ├── constants/                # DEFINES
│       │   ├── ipc-constants.ts
│       │   └── app-constants.ts
│       │
│       └── config/                   # DEFINES
│           └── app-config.ts
│
└── scripts/                          # EXTERNAL (Axis 4)
    ├── migrate-script.ts
    ├── build-script.ts
    └── validate-script.ts
```

---

## Migration Examples

### Example 1: Database in Wrong Location
```
❌ Before: src/main/claude/claude-db.ts
✅ After: src/main/databases/claude-database.ts

Reason: File contains database logic (Axis 1: STABLE)
Action: Move to databases/ folder, rename with -database suffix
```

### Example 2: Types Not Centralized
```
❌ Before: src/main/terminal/terminal-types.ts
✅ After: src/shared/types/terminal-types.ts

Reason: Type definitions must be in shared/ (Axis 3: DEFINES)
Action: Move to shared/types/
```

### Example 3: Helper with Wrong Suffix
```
❌ Before: src/shared/helpers/format-helpers.ts
✅ After: src/shared/helpers/format-helper.ts

Reason: Singular suffix required
Action: Rename to singular form
```

### Example 4: Component Missing Suffix
```
❌ Before: src/renderer/terminal/components/ai-sidebar.ts
✅ After: src/renderer/components/ai-sidebar-component.ts

Reason: Components are Axis 1 (STABLE), need -component suffix
Action: Rename with -component suffix, move to components/
```

### Example 5: Script in Wrong Location
```
❌ Before: src/main/scripts/migrate.ts
✅ After: scripts/migrate-script.ts

Reason: Scripts are Axis 4 (EXTERNAL), outside src/
Action: Move to scripts/, add -script suffix
```

---

## Validation Rules

### V1: Axis Assignment (Deterministic)

**Algorithm**:
```
IF file has lifecycle methods (init/cleanup/destroy):
  → Axis 1: STABLE

ELSE IF file exports ONLY pure functions (no classes, no state):
  → Axis 2: HELPS

ELSE IF file exports ONLY types/interfaces/constants (no logic):
  → Axis 3: DEFINES

ELSE IF file is in scripts/ directory:
  → Axis 4: EXTERNAL

ELSE:
  → VIOLATION: Mixed concerns - SPLIT FILE
```

**Edge Cases** (all are violations requiring split):

❌ **Class with only static methods**
```typescript
export class StringUtils {
    static format() { }  // Violation: Use helper functions instead
}
```
Resolution: Convert to pure functions in `{domain}-helper.ts`

❌ **File with both types and functions**
```typescript
export interface User { }
export function formatUser() { }  // Violation: Mixed concerns
```
Resolution: Split into `user-types.ts` (Axis 3) and `user-helper.ts` (Axis 2)

❌ **Parser with state**
```typescript
export class Parser {
    private buffer: string;  // Violation: Stateful parser
    parse() { }
}
```
Resolution: Split into pure `parse()` function (Axis 2) and `StreamHandler` (Axis 1)

**Precedence**: If multiple axes could apply, the file is architecturally wrong.

### V2: Folder Placement
```
IF Axis 1 (STABLE):
  - MUST be in src/{context}/{role}s/
  - MUST extend base class from src/{context}/base/

IF Axis 2 (HELPS):
  - MUST be in src/{context}/{role}s/
  - MUST NOT extend base class

IF Axis 3 (DEFINES):
  - MUST be in src/shared/{artifact}s/
  - MUST NOT extend base class

IF Axis 4 (EXTERNAL):
  - MUST be in scripts/
  - MUST have -script suffix
```

### V3: Filename Pattern
```
Axis 1 & 2: {domain}-{role}.ts
Axis 3: {domain}-{artifact}.ts
Axis 4: {name}-script.ts
```

### V4: Export Validation
```
filename: terminal-manager.ts
expected_export: class TerminalManager

filename: format-helper.ts
expected_export: function formatX() OR class FormatHelper

filename: terminal-types.ts
expected_export: interface/type Terminal*
```

---

## Exception Table

| Pattern | Standard Rule | Exception | Rationale |
|---------|---------------|-----------|-----------|
| `index.ts` | Needs suffix | Barrel export (no suffix) | Re-exports only |
| `main.ts` | Needs suffix | Entry point (no suffix) | Application entry |
| `preload.ts` | Needs suffix | Electron preload (no suffix) | Framework requirement |
| `types/` | Singular suffix | Plural artifact name | Aggregates multiple types |
| `constants/` | Singular suffix | Plural artifact name | Aggregates multiple constants |

---

## Decision Algorithm: File Placement

```
FUNCTION DetermineFilePlacement(file):
    content = READ file
    exports = EXTRACT exports FROM content

    # Step 0: Check for mixed concerns (LAW 0)
    concerns = COUNT_DISTINCT_AXES(exports)
    IF concerns > 1:
        RETURN ERROR "VIOLATION: Mixed concerns - split into {concerns} files"

    # Step 1: Detect axis (deterministic)
    IF exports CONTAINS lifecycle_methods (init/cleanup/destroy):
        axis = 1  # STABLE
    ELSE IF exports ARE_ALL pure_functions:
        axis = 2  # HELPS
    ELSE IF exports ARE_ALL types/interfaces/constants:
        axis = 3  # DEFINES
    ELSE IF file.path STARTS_WITH "scripts/":
        axis = 4  # EXTERNAL
    ELSE:
        RETURN ERROR "VIOLATION: Cannot determine axis - check for mixed concerns"

    # Step 2: Detect role from filename or exports
    role = DETECT_ROLE(filename, exports)
    IF role == NULL:
        RETURN ERROR "VIOLATION: Cannot determine role from filename"

    # Step 3: Validate role matches axis
    IF role IN AXIS_1_ROLES AND axis != 1:
        RETURN ERROR "VIOLATION: {role} must be in Axis 1 (missing lifecycle?)"
    IF role IN AXIS_2_ROLES AND axis != 2:
        RETURN ERROR "VIOLATION: {role} must be in Axis 2 (has state/lifecycle?)"

    # Step 4: Determine location
    IF axis == 1 OR axis == 2:
        location = "src/{context}/{role}s/{domain}-{role}.ts"
    ELSE IF axis == 3:
        location = "src/shared/{artifact}s/{domain}-{artifact}.ts"
    ELSE IF axis == 4:
        location = "scripts/{name}-script.ts"

    RETURN location

FUNCTION COUNT_DISTINCT_AXES(exports):
    axes = []
    IF ANY export HAS lifecycle_methods:
        ADD 1 TO axes
    IF ANY export IS pure_function:
        ADD 2 TO axes
    IF ANY export IS type/interface/constant:
        ADD 3 TO axes
    RETURN LENGTH(axes)
```

---

## Summary: The 4 Axes

| Axis | What | Inheritance | Location | Example |
|------|------|-------------|----------|---------|
| **1: STABLE** | Runtime execution | YES (base class) | `src/{context}/{role}s/` | `managers/terminal-manager.ts` |
| **2: HELPS** | Functional support | NO | `src/{context}/{role}s/` | `helpers/format-helper.ts` |
| **3: DEFINES** | Definitions | NO | `src/shared/{artifact}s/` | `types/terminal-types.ts` |
| **4: EXTERNAL** | Tooling/scripts | NO | `scripts/` | `migrate-script.ts` |

---

## Conflict Resolutions (v2.0)

This section documents how v2.0 resolves architectural ambiguities.

### Resolution 1: Parser Semantics (Axis 2 vs Axis 1)

**Problem**: "Parser" was ambiguous - some parsers have state/lifecycle.

**Solution**: **LAW 0 + Split Rule**
- Parsers in Axis 2 MUST be pure transformation functions
- If parser needs state/lifecycle → Split into:
  - Pure parser (Axis 2: HELPS)
  - Stateful processor/handler (Axis 1: STABLE)

**Example**:
```
❌ stream-parser.ts (class with buffer + lifecycle)
✅ ansi-parser.ts (pure function parseAnsiCode())
✅ stream-handler.ts (class StreamHandler extends BaseHandler)
```

### Resolution 2: Handler Naming (Composite Roles)

**Problem**: `handler` uses `{domain}-{subdomain}-{role}.ts`, breaking the standard pattern.

**Solution**: **Formalized as R6: Composite Roles**
- Some roles (handler, processor, engine) require subdomain
- Pattern: `{domain}-{subdomain}-{role}.ts`
- Rule: If removing subdomain causes ambiguity, it's required

**Example**:
```
✅ claude-ipc-handler.ts (domain + subdomain + role)
✅ terminal-stream-processor.ts (domain + subdomain + role)
❌ claude-handler.ts (ambiguous - handler for what?)
```

### Resolution 3: Axis Detection Ambiguity

**Problem**: Files mixing concerns (e.g., class with static methods, types + functions).

**Solution**: **LAW 0: If ambiguous, file is wrong → SPLIT**
- Algorithm returns ERROR if multiple axes detected
- COUNT_DISTINCT_AXES() checks for mixed concerns
- Resolution: Split into separate files per axis

**Example**:
```
❌ utils.ts with class StringUtils { static format() }
✅ Split into: format-helper.ts with pure functions

❌ user.ts with interface User + function formatUser()
✅ Split into: user-types.ts (Axis 3) + user-helper.ts (Axis 2)
```

### Key Principle

**Naming reflects functionality exactly.**

If you cannot name a file unambiguously, it violates single responsibility and must be split.

---

**Next Steps**:
1. Run architecture scanner to detect violations
2. Use ARCHITECTURE-FIX-CHECKLIST.md to track fixes
3. Follow process: Read → Analyze → Classify Axis → Rename/Move/SPLIT → Mark Complete
