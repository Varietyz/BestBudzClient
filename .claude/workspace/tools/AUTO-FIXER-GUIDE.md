# Auto-Fixer Guide

Generic auto-fixer runner with **dry-run preview** and **confirmation prompts** for safe, iterative code fixes.

## Quick Start

```bash
# Show available fixers
npm run fix

# Dry-run preview (no changes)
npm run fix:comments:dry
npm run fix:json:dry
npm run fix:dialogs:dry
npm run fix:constants:dry

# Interactive mode (shows preview + asks for confirmation)
npm run fix:comments
npm run fix:json
npm run fix:dialogs
npm run fix:constants

# Auto-yes mode (no confirmation, applies immediately)
npm run fix:comments -- --yes
```

## Available Fixers

### 1. **code-comments**
Removes non-functional comments while preserving:
- SPDX license identifiers (`SPDX-License-Identifier`)
- Copyright notices
- JSDoc blocks (`/** ... */`)
- Tool directives (`@ts-`, `@eslint-`, `@prettier-`, `stylelint-`)
- TODO/FIXME markers

**Usage:**
```bash
npm run fix:comments:dry    # Preview what will be removed
npm run fix:comments        # Preview + confirmation
npm run fix:comments -- --yes  # Apply immediately
```

**Supports:** TypeScript, JavaScript, CSS, HTML

### 2. **json-stringify**
Adds proper spacing parameter to `JSON.stringify()` calls for readable output.

**Before:**
```javascript
JSON.stringify(data)
```

**After:**
```javascript
JSON.stringify(data, null, 4)
```

**Usage:**
```bash
npm run fix:json:dry    # Preview
npm run fix:json        # Apply with confirmation
```

### 3. **native-dialogs**
Replaces native browser dialogs with custom implementations:
- `alert()` → `toast.info()`
- `confirm()` → `showConfirm()`
- `prompt()` → `showPrompt()`

**Usage:**
```bash
npm run fix:dialogs:dry    # Preview
npm run fix:dialogs        # Apply with confirmation
```

### 4. **constant-aliases**
Removes duplicate constant definitions across the codebase.

**Usage:**
```bash
npm run fix:constants:dry    # Preview
npm run fix:constants        # Apply with confirmation
```

## Workflow

### Safe First Run (Recommended)

```bash
# 1. Preview what will be fixed (no changes)
npm run fix:comments:dry

# 2. Review the output carefully

# 3. Apply fixes with confirmation
npm run fix:comments

# You'll see:
# ⚠️  Apply these fixes? (y/n):
```

### Batch Mode (For CI/Automation)

```bash
npm run fix:comments -- --yes
```

## Safety Features

### 1. **Dry-Run Mode** (`--dry-run`)
- Shows exactly what will be changed
- No files are modified
- Groups violations by file type
- Shows sample violations with line numbers

### 2. **Interactive Confirmation** (default)
- Shows preview first
- Asks `Apply these fixes? (y/n)`
- Cancel anytime with `n`

### 3. **Checkpoint System**
- Automatic state snapshots before changes
- Stored in `.checkpoints/` directory
- Automatic rollback on error
- Configurable retention (10 checkpoints by default)

### 4. **Integrity Validation**
- Hash verification before/after
- Ensures no corruption
- Reports any integrity issues

## Adding New Fixers

Edit `.claude/workspace/tools/run-auto-fixer.js` and add to `FIXER_REGISTRY`:

```javascript
const FIXER_REGISTRY = {
    "your-fixer": {
        scannerPath: "../../../root/codebase-validation/scanners/.../your-scanner.js",
        scannerClass: "YourScanner",
        fixerPath: "../../../root/codebase-validation/auto-fix/your-auto-fixer.js",
        fixerClass: "YourAutoFixer",
        displayName: "Your Fixer",
        description: "What it does",
    },
};
```

Then add npm scripts to `package.json`:

```json
{
  "scripts": {
    "fix:your-name": "cross-env NODE_ENV=development node .claude/workspace/tools/run-auto-fixer.js your-fixer",
    "fix:your-name:dry": "cross-env NODE_ENV=development node .claude/workspace/tools/run-auto-fixer.js your-fixer --dry-run"
  }
}
```

## Configuration

Auto-fixer behavior is controlled via `root/codebase-validation/settings.json`:

```json
{
  "tools": {
    "autoFix": {
      "enabled": true,
      "snapshot": {
        "enabled": true,
        "storageDir": ".checkpoints",
        "retentionCount": 10,
        "hashAlgorithm": "sha256",
        "compressionEnabled": true,
        "integrityValidation": true
      }
    }
  }
}
```

## Examples

### Example 1: Remove Comments

```bash
$ npm run fix:comments:dry

🔍 Running Code Comments scanner...
   ✅ Found 980 violations in 248 files

ℹ️ Items by file type:

  .css: 974 items in 247 files
    renderer\styles\main.css (6 items)
      Line 8: /* Design Tokens */
      Line 11: /* Dynamics - CSS classes */

  .html: 6 items in 1 files
    renderer\terminal\terminal.html (6 items)
      Line 20: <!-- Flex row: sidebar -->

✓ DRY RUN SUMMARY:
   Total violations: 980
   Total files: 248

💡 To apply these changes, run without --dry-run flag
```

### Example 2: Fix with Confirmation

```bash
$ npm run fix:comments

[... preview shown ...]

⚠️  Apply these fixes? (y/n): y

🔧 Applying fixes...
   ✅ Fixed 980 violations in 248 files

✅ Auto-Fix Complete
============================================================

✓ Summary:
   Violations fixed: 980
   Files modified: 248

⏱️  Completed in 3421ms
```

### Example 3: Auto-Yes Mode

```bash
$ npm run fix:comments -- --yes

[... no confirmation prompt, applies immediately ...]

✅ Auto-Fix Complete
   Violations fixed: 980
   Files modified: 248
```

## Troubleshooting

### "No violations found"
The codebase is clean for this fixer! Nothing to fix.

### Checkpoint errors
Delete `.checkpoints/` directory and retry:
```bash
rm -rf .checkpoints/
npm run fix:comments
```

### Want to restore from checkpoint
Checkpoints are stored in `.checkpoints/` with UUID filenames. Use git to restore instead:
```bash
git restore root/archlab-ide/src/
```

## Architecture

```
run-auto-fixer.js (Generic Runner)
    ↓
FIXER_REGISTRY (Maps fixer name to classes)
    ↓
1. Load Scanner → 2. Get Violations → 3. Show Preview
    ↓
4. Confirm (if not --yes) → 5. Load Fixer → 6. Apply Fixes
    ↓
BaseAutoFixer (Checkpoint/Rollback Protocol)
```

**Design Goals:**
- **Single entry point** for all auto-fixers
- **Safe by default** (dry-run preview + confirmation)
- **Consistent UX** across all fixers
- **Easy to extend** (just add to registry)

---

**© 2026 ARCHLAB - Auto-Fixer System v1.0.0**
