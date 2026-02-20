# Architecture Organization Skill

**Trigger Contexts** (when to invoke this skill):
- X-Axis: file, folder, structure, organization, naming, location, architecture, classification
- Y-Axis: move, rename, organize, classify, restructure, refactor-structure
- Z-Axis: LAW-compliant-structure, axis-based-organization, role-based-folders, global-role-registry

**Related Skills**: file-naming-conventions, archlab-structure, principle-architecture

---

## Core Concept: 4-Axis Classification System

Every code artifact in ARCHLAB must be classified into one of 4 architectural axes. This is LAW 0 (implicit, enforced before the 7 Laws).

### Axis 1: STABLE (Runtime Execution)

**Characteristic**: HAS lifecycle methods (init, cleanup, destroy) and state

**What belongs here**:
- Managers (coordinate resources)
- Services (business logic with state)
- Components (UI elements with lifecycle)
- Databases (data persistence)
- Registries (runtime lookup tables)
- Stores (state containers)
- Queues (work management)
- Trackers (monitoring state)
- Factories (stateful creation)
- Providers (resource provision)

**Pattern**:
```typescript
// File: terminal-manager.ts
export class TerminalManager extends BaseManager {
    async init() { /* setup */ }
    async cleanup() { /* teardown */ }
    destroy() { /* release */ }
}
```

**Location**: `src/{context}/{role}s/`
- Example: `src/main/managers/terminal-manager.ts`
- Example: `src/renderer/services/graphics-service.ts`

**Inheritance**: REQUIRED - Must extend base class for role

### Axis 2: HELPS (Pure Functions)

**Characteristic**: ONLY pure functions OR stateless transformations

**What belongs here**:
- Helpers (utility functions)
- Parsers (data transformation)
- Validators (pure checks)
- Formatters (presentation logic)
- Utilities (general-purpose tools)

**Pattern**:
```typescript
// File: ansi-parser.ts
export function parseAnsiSequence(input: string): ParsedAnsi {
    // Pure function - no state, no side effects
    return { /* result */ };
}
```

**Location**: `src/{context}/{role}s/`
- Example: `src/main/parsers/ansi-parser.ts`
- Example: `src/renderer/helpers/logger-helper.ts`

**Inheritance**: NOT REQUIRED - Pure functions don't need base classes

**CRITICAL RULE**: If a "helper" or "parser" has lifecycle methods or state, it's misclassified. Split it:
- State/lifecycle → Axis 1 (rename to -manager or -service)
- Pure functions → Axis 2 (keep as -helper or -parser)

### Axis 3: DEFINES (Declarative Definitions)

**Characteristic**: ONLY types, interfaces, constants (no execution)

**What belongs here**:
- Types (TypeScript interfaces/types)
- Constants (SCREAMING_SNAKE_CASE values)
- Configs (declarative configuration)
- Schemas (data structure definitions)

**Pattern**:
```typescript
// File: terminal-types.ts (in src/shared/types/)
export interface TerminalSession {
    id: string;
    pid: number;
    // Only definitions, no methods
}

export const TERMINAL_DEFAULTS = {
    SHELL: 'bash',
    ENCODING: 'utf-8'
};
```

**Location**: `src/shared/{role}s/` (CENTRALIZED - LAW requirement)
- Example: `src/shared/types/terminal-types.ts`
- Example: `src/shared/constants/app-constants.ts`

**Why centralized**: Axis 3 defines the "language" of the system. All contexts (main, renderer, shared) must reference the same definitions to maintain consistency.

**CRITICAL VIOLATION**: Having constants/types in `src/main/` or `src/renderer/` breaks Axis 3 centralization.

### Axis 4: EXTERNAL (Build-Time Orchestration)

**Characteristic**: Runs outside src/ tree, not part of application runtime

**What belongs here**:
- Scripts (build automation)
- Tools (development utilities)
- Generators (code generation)

**Pattern**:
```typescript
// File: build-script.ts (in scripts/)
// Runs at build time, not runtime
async function buildApplication() {
    // Build orchestration
}
```

**Location**: `scripts/` (outside src/)
- Example: `scripts/build-script.ts`
- Example: `scripts/generate-types-script.ts`

**CRITICAL RULE**: NEVER import Axis 4 files into Axis 1/2/3. Build-time and runtime are separate.

---

## Global Role Registry (Authoritative Source of Truth)

Every file MUST match exactly one role from this registry:

```typescript
const GLOBAL_ROLE_REGISTRY = {
    // Axis 1: STABLE
    "manager": {
        axis: 1,
        pattern: "*-manager.ts",
        base: "BaseManager",
        folder: "managers"
    },
    "service": {
        axis: 1,
        pattern: "*-service.ts",
        base: "BaseService",
        folder: "services"
    },
    "component": {
        axis: 1,
        pattern: "*-component.ts",
        base: "BaseComponent",
        folder: "components"
    },
    "database": {
        axis: 1,
        pattern: "*-database.ts",
        base: "BaseDatabase",
        folder: "databases"
    },
    "registry": {
        axis: 1,
        pattern: "*-registry.ts",
        base: "BaseRegistry",
        folder: "registries"
    },
    "store": {
        axis: 1,
        pattern: "*-store.ts",
        base: "BaseStore",
        folder: "stores"
    },
    "queue": {
        axis: 1,
        pattern: "*-queue.ts",
        base: "BaseQueue",
        folder: "queues"
    },
    "tracker": {
        axis: 1,
        pattern: "*-tracker.ts",
        base: "BaseTracker",
        folder: "trackers"
    },
    "factory": {
        axis: 1,
        pattern: "*-factory.ts",
        base: "BaseFactory",
        folder: "factories"
    },
    "provider": {
        axis: 1,
        pattern: "*-provider.ts",
        base: "BaseProvider",
        folder: "providers"
    },

    // Axis 2: HELPS
    "helper": {
        axis: 2,
        pattern: "*-helper.ts",
        purity: "REQUIRED",
        folder: "helpers"
    },
    "parser": {
        axis: 2,
        pattern: "*-parser.ts",
        purity: "REQUIRED",
        folder: "parsers"
    },
    "validator": {
        axis: 2,
        pattern: "*-validator.ts",
        purity: "REQUIRED",
        folder: "validators"
    },
    "formatter": {
        axis: 2,
        pattern: "*-formatter.ts",
        purity: "REQUIRED",
        folder: "formatters"
    },
    "util": {
        axis: 2,
        pattern: "*-util.ts",
        purity: "REQUIRED",
        folder: "utilities"
    },

    // Axis 3: DEFINES
    "types": {
        axis: 3,
        pattern: "*-types.ts",
        folder: "types",
        centralized: "src/shared/types/"
    },
    "constants": {
        axis: 3,
        pattern: "*-constants.ts",
        folder: "constants",
        centralized: "src/shared/constants/"
    },
    "config": {
        axis: 3,
        pattern: "*-config.ts",
        folder: "config",
        centralized: "src/shared/config/"
    },
    "schemas": {
        axis: 3,
        pattern: "*-schemas.ts",
        folder: "schemas",
        centralized: "src/shared/schemas/"
    },

    // Axis 4: EXTERNAL
    "script": {
        axis: 4,
        pattern: "*-script.ts",
        folder: "scripts",
        location: "scripts/"
    }
};
```

**Usage**:
- Check filename against patterns
- Identify role from suffix
- Determine axis from role
- Validate location based on axis + role

---

## Naming Rules

### R1: Kebab-Case
ALL filenames use kebab-case: `terminal-manager.ts`, NOT `TerminalManager.ts`

### R2: Role Suffix Required
Filename MUST end with role suffix: `terminal-manager.ts`, NOT `terminal.ts`

### R3: Domain Prefix Required
Filename MUST have domain: `terminal-manager.ts`, NOT `manager.ts`

### R4: Pattern Match
Must match exact pattern from global_role_registry:
- ✅ `terminal-manager.ts` matches `*-manager.ts`
- ❌ `manager-terminal.ts` does NOT match (wrong order)

### R5: Class Name Derivation
Class name = PascalCase(filename without suffix):
- `terminal-manager.ts` → `class TerminalManager`
- `ansi-parser.ts` → `class AnsiParser` or `export function parseAnsi()`

### R6: Composite Roles
Some roles support composite patterns:
- Handler: `*-{area}-handler.ts` → `terminal-ipc-handler.ts`
- Allows domain + subdomain specification

---

## Location Validation Rules

### Rule L1: Axis Determines Location Category

```typescript
if (axis === 1 || axis === 2) {
    location = "src/{context}/{role}s/";
} else if (axis === 3) {
    location = "src/shared/{role}s/";  // CENTRALIZED
} else if (axis === 4) {
    location = "scripts/";  // OUTSIDE src/
}
```

### Rule L2: Context Extraction
Context = main | renderer | shared

Determined by:
- Main process code → `src/main/`
- Renderer process code → `src/renderer/`
- Shared across processes → `src/shared/`

### Rule L3: Folder Name = Role Plural
- Role: manager → Folder: `managers/`
- Role: service → Folder: `services/`
- Role: helper → Folder: `helpers/`

**Exception**: utilities (not "utils")

---

## Violation Detection Protocol

### PHASE 1: Extract Role from Filename

```typescript
function detectRole(fileName: string): Role | null {
    for (const [role, info] of GLOBAL_ROLE_REGISTRY) {
        if (fileName.endsWith(`-${role}`)) {
            return {role, domain: extractDomain(fileName, role)};
        }
    }
    return null;  // ROLE_NOT_DETECTED violation
}
```

### PHASE 2: Determine Expected Location

```typescript
function expectedLocation(role: Role, currentPath: string): string {
    const axis = GLOBAL_ROLE_REGISTRY[role.name].axis;

    if (axis === 3) {
        // MUST be centralized
        return GLOBAL_ROLE_REGISTRY[role.name].centralized;
    }

    const context = extractContext(currentPath);  // main/renderer/shared
    const folder = GLOBAL_ROLE_REGISTRY[role.name].folder;

    return `src/${context}/${folder}/`;
}
```

### PHASE 3: Compare Actual vs Expected

```typescript
function validate(filePath: string, role: Role): Violation | null {
    const expected = expectedLocation(role, filePath);
    const actual = dirname(filePath);

    if (actual !== expected) {
        return {
            code: axis === 3 ? "WRONG_LOCATION_AXIS_3" : "WRONG_LOCATION",
            severity: axis === 3 ? "CRITICAL" : "HIGH",
            expected,
            actual,
            action: `MOVE to ${expected}`
        };
    }

    return null;
}
```

---

## Remediation Protocol

### Priority Ordering

1. **CRITICAL** - Axis 3 violations (centralization)
2. **HIGH** - Role folder mismatches
3. **MEDIUM** - Role suffix missing

### Fix Execution

```typescript
// For each violation (in priority order):
1. READ file to understand exports/purpose
2. DETERMINE correct axis and role
3. CALCULATE new location and filename
4. MOVE file using git mv (preserves history)
5. FIND all importers with Grep
6. UPDATE each importer's import path
7. VERIFY no broken imports remain
```

### Import Update Algorithm

```typescript
function updateImports(oldPath: string, newPath: string): void {
    // Find all files importing the moved file
    const importers = grep(`import.*${basename(oldPath, '.ts')}`);

    for (const importer of importers) {
        // Calculate old and new relative paths
        const oldRelative = relativePath(dirname(importer), oldPath);
        const newRelative = relativePath(dirname(importer), newPath);

        // Update import statement
        edit(importer)
            .replace(oldRelative, newRelative)
            .save();
    }
}
```

---

## Integration with 7 Laws

### LAW 0 (Implicit): Single Concern
4-axis classification enforces single concern:
- Axis 1: Lifecycle management
- Axis 2: Pure computation
- Axis 3: Definition only
- Axis 4: Build orchestration

No file can span multiple axes.

### LAW 1: Forward-Only Programming
Role-based folders enable forward-only:
- New manager → add to `managers/` (no modification needed elsewhere)
- Self-registration via base class
- No need to modify index files

### LAW 3: Inheritance Over Configuration
Axis 1 roles MUST extend base classes:
- `TerminalManager extends BaseManager`
- Behavior from base, not config
- Base class determines lifecycle

### LAW 7: Single Import Boundary (Antenna Hub)
Axis 3 centralization enables antenna hub:
- All types in `src/shared/types/`
- Single source of truth
- Semantic token resolution

---

## Common Patterns

### Pattern: Database + Service Split

```typescript
// WRONG: Database with business logic
class TerminalDatabase {
    async save() { /* SQL */ }
    validateSession() { /* business logic - WRONG */ }
}

// RIGHT: Split into roles
// src/main/databases/terminal-database.ts
class TerminalDatabase extends BaseDatabase {
    async save() { /* SQL only */ }
}

// src/main/services/terminal-service.ts
class TerminalService extends BaseService {
    constructor(private db: TerminalDatabase) {}

    validateSession() { /* business logic */ }
}
```

### Pattern: Helper with State → Split

```typescript
// WRONG: Helper with state
// src/main/helpers/session-helper.ts
class SessionHelper {
    private cache = new Map();  // STATE - WRONG for helper

    parse() { /* pure */ }
}

// RIGHT: Split into helper + manager
// src/main/helpers/session-helper.ts (Axis 2)
export function parseSession(data: string) {
    // Pure function only
}

// src/main/managers/session-manager.ts (Axis 1)
export class SessionManager extends BaseManager {
    private cache = new Map();  // State OK for manager

    parse(data: string) {
        return parseSession(data);  // Delegates to helper
    }
}
```

### Pattern: Type Centralization

```typescript
// WRONG: Types scattered
// src/main/terminal/terminal-types.ts
// src/renderer/components/terminal-types.ts

// RIGHT: Centralized types
// src/shared/types/terminal-types.ts
export interface TerminalSession { /* ... */ }
export interface TerminalConfig { /* ... */ }

// src/main/managers/terminal-manager.ts
import { TerminalSession } from '../../shared/types/terminal-types';

// src/renderer/components/terminal-component.ts
import { TerminalSession } from '../../shared/types/terminal-types';
```

---

## Decision Tree: How to Classify a File

```
START: What does this file do?

├─ Has lifecycle methods (init, cleanup, destroy)?
│  YES → Axis 1: STABLE
│  │    ├─ Coordinates resources? → manager
│  │    ├─ Business logic? → service
│  │    ├─ UI element? → component
│  │    ├─ Data persistence? → database
│  │    └─ Other stateful? → Review global_role_registry
│  NO → Continue
│
├─ Only pure functions? (No state, no side effects)
│  YES → Axis 2: HELPS
│  │    ├─ Transforms data? → parser
│  │    ├─ Checks validity? → validator
│  │    ├─ Formats output? → formatter
│  │    └─ General utility? → helper
│  NO → Continue
│
├─ Only types/interfaces/constants?
│  YES → Axis 3: DEFINES
│  │    ├─ TypeScript types? → types
│  │    ├─ Const values? → constants
│  │    ├─ App configuration? → config
│  │    └─ Data schemas? → schemas
│  NO → Continue
│
└─ Runs at build time? (Outside src/)
   YES → Axis 4: EXTERNAL
        └─ Build automation? → script
```

---

## Tools and Commands

### Scanner
`.claude/workspace/tools/architecture-scanner-pag.mjs`
- Scans entire codebase
- Detects violations
- Generates priority queue
- Outputs JSON report

### Command
`/architecture-organizer`
- Executes scanner
- Fixes violations systematically
- Updates imports
- Verifies compliance

### Manual Check
```bash
# Run scanner only
node .claude/workspace/tools/architecture-scanner-pag.mjs

# View violations
cat .claude/workspace/tools/architecture-violations-pag.json
```

---

## When to Apply This Skill

**Trigger immediately when**:
- Creating new files (classify first, then create)
- Renaming files (ensure pattern compliance)
- Moving files (validate location)
- Refactoring structure
- Reviewing pull requests (check architecture compliance)

**Apply classification before writing code**:
1. Determine what the file will do
2. Classify into axis using decision tree
3. Select role from global_role_registry
4. Calculate correct location
5. Create file with correct name in correct location

**Never**:
- Create files without role suffixes
- Mix concerns (multiple axes in one file)
- Put Axis 3 files outside src/shared/
- Import Axis 4 files into runtime code

---

## Examples

### Example 1: Creating a New Manager

```typescript
// PLANNING PHASE
Purpose: Manage terminal sessions
Has lifecycle? YES (init, cleanup, destroy)
Has state? YES (active sessions)
Classification: Axis 1, role = manager

// LOCATION CALCULATION
Context: main (Electron main process)
Role: manager
Folder: managers
Location: src/main/managers/

// FILENAME
Domain: terminal
Role: manager
Filename: terminal-manager.ts

// FULL PATH
src/main/managers/terminal-manager.ts

// IMPLEMENTATION
export class TerminalManager extends BaseManager {
    private sessions = new Map();

    async init() { /* ... */ }
    async cleanup() { /* ... */ }
    destroy() { /* ... */ }
}
```

### Example 2: Creating a Helper

```typescript
// PLANNING PHASE
Purpose: Parse ANSI escape sequences
Has lifecycle? NO
Has state? NO
Pure functions? YES
Classification: Axis 2, role = parser

// LOCATION CALCULATION
Context: main (used in main process)
Role: parser
Folder: parsers
Location: src/main/parsers/

// FILENAME
Domain: ansi
Role: parser
Filename: ansi-parser.ts

// FULL PATH
src/main/parsers/ansi-parser.ts

// IMPLEMENTATION
export function parseAnsiSequence(input: string): ParsedAnsi {
    // Pure function - no side effects
    return { /* result */ };
}
```

### Example 3: Creating Types

```typescript
// PLANNING PHASE
Purpose: Define terminal data structures
Only types? YES
Classification: Axis 3, role = types

// LOCATION CALCULATION
Axis: 3 (DEFINES)
Role: types
MUST BE CENTRALIZED: src/shared/types/

// FILENAME
Domain: terminal
Role: types
Filename: terminal-types.ts

// FULL PATH
src/shared/types/terminal-types.ts  // ALWAYS in shared/

// IMPLEMENTATION
export interface TerminalSession {
    id: string;
    pid: number;
}

export interface TerminalConfig {
    shell: string;
    encoding: string;
}
```

---

## Relationship to Other Architectural Concepts

### File Naming Conventions Skill
- Architecture organization determines WHAT suffix to use
- File naming conventions determine HOW to format it
- Both must be satisfied

### DOM Factory
- Components (Axis 1) create UI elements
- Must use DOM.createElement() (from DOM factory skill)
- Architecture organization + DOM factory = compliant UI code

### Engine Logger
- All axes can use Engine.logger()
- Logger calls include component context
- Architecture organization ensures components are properly classified

### 7 Laws
- LAW 0 (implicit): Single concern → enforced by 4-axis classification
- LAW 1: Forward-only → enabled by role-based folders
- LAW 3: Inheritance → required for Axis 1 roles
- LAW 7: Single import boundary → enabled by Axis 3 centralization

---

## Quick Reference

**Remember**:
- 4 axes: STABLE, HELPS, DEFINES, EXTERNAL
- Role suffix REQUIRED: `-manager`, `-helper`, `-types`, etc.
- Axis 3 MUST be centralized in `src/shared/`
- Use `/architecture-organizer` to fix violations
- Check global_role_registry for authoritative role list

**File creation checklist**:
1. [ ] Classified into axis (1, 2, 3, or 4)
2. [ ] Role selected from global_role_registry
3. [ ] Filename matches pattern: `{domain}-{role}.ts`
4. [ ] Location matches axis + role requirements
5. [ ] Extends base class if Axis 1
6. [ ] Pure functions only if Axis 2
7. [ ] Only definitions if Axis 3
8. [ ] Outside src/ if Axis 4
