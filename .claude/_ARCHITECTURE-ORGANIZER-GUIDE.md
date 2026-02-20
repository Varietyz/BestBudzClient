# Architecture Organizer - Complete Guide

**Purpose**: Systematic architecture organization following the 4-axis PAG protocol from ARCHITECTURE-ORGANIZATION.pag.md

**Status**: Ready to use
**Command**: `/architecture-organizer`
**Scanner**: `.claude/workspace/tools/architecture-scanner-pag.mjs`

---

## Quick Start

```bash
# Option 1: Run through Claude slash command (recommended)
/architecture-organizer

# Option 2: Run scanner directly
node .claude/workspace/tools/architecture-scanner-pag.mjs

# Option 3: Scan only (no fixes)
/architecture-organizer scan
```

---

## What This Does

The architecture organizer implements the complete ARCHITECTURE-ORGANIZATION.pag.md protocol:

### 1. **4-Axis Classification**

Every file is classified into one of 4 architectural axes:

- **Axis 1: STABLE** - Runtime execution with lifecycle (managers, services, components)
- **Axis 2: HELPS** - Pure functions/stateless helpers (helpers, parsers, validators)
- **Axis 3: DEFINES** - Declarative definitions (types, constants, configs)
- **Axis 4: EXTERNAL** - Build-time orchestration (scripts)

### 2. **Role Detection**

Files must match a role pattern from the global_role_registry:

```typescript
// Examples of valid filenames:
terminal - manager.ts; // Axis 1: manager role
ansi - parser.ts; // Axis 2: parser role
terminal - types.ts; // Axis 3: types role
build - script.ts; // Axis 4: script role
```

### 3. **Location Validation**

Files must be in the correct folder based on their axis and role:

- **Axis 1 & 2**: `src/{context}/{role}s/` (e.g., `src/main/managers/`)
- **Axis 3**: `src/shared/{role}s/` (centralized - e.g., `src/shared/types/`)
- **Axis 4**: `scripts/` (outside src/)

### 4. **Systematic Remediation**

Violations are fixed in priority order:

1. 🔴 **CRITICAL** - Axis 3 centralization violations (types/constants not in shared/)
2. 🟠 **HIGH** - Role folder mismatches (correct suffix, wrong folder)
3. 🟡 **MEDIUM** - Role suffix missing (filename doesn't match any role pattern)

---

## Command Modes

### Auto Mode (Default)

Automatically fixes all violations in priority order:

```bash
/architecture-organizer
# or
/architecture-organizer auto
```

**What it does**:

1. Scans codebase with PAG scanner
2. Classifies violations by priority
3. Fixes CRITICAL violations first (Axis 3 centralization)
4. Fixes HIGH violations next (role folder mismatches)
5. Fixes MEDIUM violations last (adds role suffixes)
6. Updates all imports in dependent files
7. Re-scans to verify fixes

### Scan Mode

Only scan and report violations, no fixes:

```bash
/architecture-organizer scan
```

**What it does**:

1. Scans codebase with PAG scanner
2. Generates detailed violation report
3. Saves results to `.claude/workspace/tools/architecture-violations-pag.json`
4. Exits without making any changes

Use this to:

- Review violations before fixing
- Generate compliance reports
- Track progress over time

---

## Violation Types

### WRONG_LOCATION_AXIS_3 (CRITICAL)

**Problem**: Axis 3 (DEFINES) files must be centralized in `src/shared/`

**Example**:

```
❌ src/main/terminal/constants/terminal-window-constants.ts
✅ Should be: src/shared/constants/terminal-window-constants.ts
```

**Why it matters**: Axis 3 files define the "language" of the system. They must be centralized so all contexts (main, renderer, shared) can reference the same definitions.

**Fix**: File is moved to centralized location, all imports updated.

### WRONG_LOCATION (HIGH)

**Problem**: File has correct role suffix but is in wrong folder

**Example**:

```
❌ src/main/ai-providers/ai-provider-registry.ts
   Role: registry (Axis 1: STABLE)
✅ Should be: src/main/registries/ai-provider-registry.ts
```

**Why it matters**: Role-based organization enables:

- Quick navigation (all managers in one place)
- Consistent structure across contexts
- Clear separation of concerns

**Fix**: File is moved to correct role folder, all imports updated.

### ROLE_NOT_DETECTED (MEDIUM)

**Problem**: Filename doesn't match any role pattern from global_role_registry

**Example**:

```
❌ src/main/claude/claude-db.ts
   Cannot determine role from filename
✅ Options:
   - claude-db-manager.ts (if it has lifecycle methods)
   - claude-database.ts (if it extends BaseDatabase)
   - claude-db-helper.ts (if it's pure functions)
```

**Why it matters**: Role suffixes enable:

- Automatic classification
- Tool-based validation
- Self-documenting filenames

**Fix**: File is analyzed, role is inferred from exports/content, suffix is added, imports updated.

---

## Example Workflow

### Starting State

```
📊 Scan Complete:
  → Total files: 302
  → Files with violations: 226

Priority Queue:
  🔴 CRITICAL (Axis 3 centralization): 3
  🟠 HIGH (Role folder mismatch): 25
  🟡 MEDIUM (Role/naming issues): 198
  📊 Total to fix: 226
```

### After Running `/architecture-organizer`

```
📊 Remediation Summary:
  ✅ Fixed: 226
  ⚠️  Failed: 0

🔍 Re-scanning to verify fixes...

✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ ALL VIOLATIONS RESOLVED
✅ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Global Role Registry

The authoritative source of truth for all role classifications:

| Role      | Axis        | Pattern          | Folder        | Base Class    | Notes                  |
| --------- | ----------- | ---------------- | ------------- | ------------- | ---------------------- |
| manager   | 1: STABLE   | `*-manager.ts`   | `managers/`   | BaseManager   | Lifecycle + state      |
| service   | 1: STABLE   | `*-service.ts`   | `services/`   | BaseService   | Lifecycle + state      |
| component | 1: STABLE   | `*-component.ts` | `components/` | BaseComponent | Lifecycle + state      |
| database  | 1: STABLE   | `*-database.ts`  | `databases/`  | BaseDatabase  | Lifecycle + state      |
| registry  | 1: STABLE   | `*-registry.ts`  | `registries/` | BaseRegistry  | Lifecycle + state      |
| store     | 1: STABLE   | `*-store.ts`     | `stores/`     | BaseStore     | Lifecycle + state      |
| helper    | 2: HELPS    | `*-helper.ts`    | `helpers/`    | -             | Pure functions         |
| parser    | 2: HELPS    | `*-parser.ts`    | `parsers/`    | -             | Pure functions         |
| validator | 2: HELPS    | `*-validator.ts` | `validators/` | -             | Pure functions         |
| formatter | 2: HELPS    | `*-formatter.ts` | `formatters/` | -             | Pure functions         |
| util      | 2: HELPS    | `*-util.ts`      | `utilities/`  | -             | Pure functions         |
| types     | 3: DEFINES  | `*-types.ts`     | `types/`      | -             | Centralized in shared/ |
| constants | 3: DEFINES  | `*-constants.ts` | `constants/`  | -             | Centralized in shared/ |
| config    | 3: DEFINES  | `*-config.ts`    | `config/`     | -             | Centralized in shared/ |
| schemas   | 3: DEFINES  | `*-schemas.ts`   | `schemas/`    | -             | Centralized in shared/ |
| script    | 4: EXTERNAL | `*-script.ts`    | `scripts/`    | -             | Outside src/ tree      |

---

## Import Update Mechanism

When a file is moved or renamed, the organizer automatically:

1. **Finds all importers**: Uses `Grep` to find all files importing the moved file
2. **Calculates new paths**: Computes relative import paths from each importer
3. **Updates imports**: Uses `Edit` to replace old import paths with new ones
4. **Preserves semantics**: Only changes the path, not the imported symbols

**Example**:

```typescript
// Before (in src/renderer/components/terminal-view.ts)
import { TerminalManager } from "../managers/internal-terminal-manager";

// File moved: src/renderer/managers/internal-terminal-manager.ts
//          → src/renderer/managers/terminal-manager.ts

// After (automatically updated)
import { TerminalManager } from "@antenna";
```

---

## Integration with Other Commands

### With `/verify-loop`

Complete compliance workflow:

```bash
# 1. Organize architecture
/architecture-organizer

# 2. Run full verification
/verify-loop
```

**Why this order**:

- Architecture organizer ensures files are in correct locations
- Verify loop ensures code quality and compliance
- Both are required for full architectural integrity

### With Codebase Validation

```bash
# After organizing
npm run verify-codebase

# Check specific scanner
npm run cv results architecture-scanner
```

---

## Monitoring Progress

The command uses TodoWrite to track progress:

```
✓ Execute architecture organization (mode: auto)
✓ Fix CRITICAL Axis 3 violations (3 files)
⋯ Fix HIGH role folder mismatches (25 files) - [12/25]
☐ Fix MEDIUM role/naming issues (198 files)
```

Progress updates every 5 files fixed.

---

## Troubleshooting

### Command Not Found

**Problem**: `/architecture-organizer` not recognized

**Solution**:

- Check file exists: `ls .claude/commands/architecture-organizer.md`
- Restart Claude Code session
- Check frontmatter is valid YAML

### Import Updates Failed

**Problem**: After moving files, some imports are broken

**Solution**:

1. Check the violation report for failed fixes
2. Manually update remaining imports
3. Run scanner again to verify

### Role Cannot Be Inferred

**Problem**: File marked as "Cannot infer role from exports"

**Solution**:

1. Read the file to understand its purpose
2. Determine correct axis:
    - Has lifecycle methods? → Axis 1 (use -manager, -service, etc.)
    - Pure functions only? → Axis 2 (use -helper, -parser, -validator)
    - Only types/constants? → Axis 3 (use -types, -constants)
    - Build script? → Axis 4 (use -script)
3. Manually add role suffix
4. Run organizer again

### Git Conflicts

**Problem**: Git mv operations fail due to uncommitted changes

**Solution**:

1. Commit or stash current changes
2. Run organizer
3. Review and commit reorganization
4. Reapply stashed changes

---

## Output Artifacts

All execution generates:

1. **architecture-violations-pag.json** - Full violation report
    - Location: `.claude/workspace/tools/`
    - Format: JSON with detailed violation data
    - Used for tracking and analysis

2. **Console output** - Human-readable summary
    - Violation counts by type
    - Top violations listed
    - Remediation summary

3. **Git history** - File moves tracked
    - All moves use `git mv` (preserves history)
    - Commits can be reviewed before pushing
    - Rollback-friendly

---

## Best Practices

### Before Running

1. ✅ Commit all current work
2. ✅ Review existing violations with scan mode
3. ✅ Ensure tests pass
4. ✅ Backup if needed (though git history is preserved)

### During Execution

1. ✅ Monitor todo list for progress
2. ✅ Watch for "Cannot infer role" messages
3. ✅ Note any failed fixes for manual intervention

### After Running

1. ✅ Review git changes: `git status`
2. ✅ Re-run scanner to verify: `/architecture-organizer scan`
3. ✅ Run tests: `npm test`
4. ✅ Run full verification: `npm run verify-codebase`
5. ✅ Commit reorganization: `git commit -m "Architecture organization: fix 226 violations"`

---

## Architecture Context

This command is part of the ARCHLAB methodology:

- **7 Laws** - Foundational architectural principles
- **4 Axes** - Classification system for all code artifacts
- **Global Role Registry** - Single source of truth for roles
- **PAG Protocol** - Executable control flow for organization

**Related Documents**:

- `.claude/_ARCHLAB.md` - 7 Laws and naming conventions
- `.claude/_DEV-RULES.md` - Core development principles
- `ARCHITECTURE-ORGANIZATION.md` - 4-axis model explained
- `ARCHITECTURE-ORGANIZATION.pag.md` - PAG protocol specification

---

## Questions & Answers

**Q: Will this break my build?**
A: No - all imports are automatically updated. However, always run tests after to verify.

**Q: Can I run this multiple times?**
A: Yes - it's idempotent. Running on an organized codebase will report 0 violations.

**Q: What if I disagree with a classification?**
A: The scanner uses the global_role_registry as source of truth. If you believe the registry is wrong, update ARCHITECTURE-ORGANIZATION.pag.md first, then re-run.

**Q: How long does it take?**
A: Depends on violation count:

- Scan only: ~10 seconds for 300 files
- Fix 10 violations: ~30 seconds
- Fix 200+ violations: ~5-10 minutes

**Q: Can I run this in CI/CD?**
A: Yes - use scan mode and fail builds on violations:

```bash
node .claude/workspace/tools/architecture-scanner-pag.mjs || exit 1
```

---

## Support

For issues or questions:

1. Check this guide first
2. Review ARCHITECTURE-ORGANIZATION.pag.md for protocol details
3. Check scanner output in `.claude/workspace/tools/architecture-violations-pag.json`
4. File issue with violation report attached
