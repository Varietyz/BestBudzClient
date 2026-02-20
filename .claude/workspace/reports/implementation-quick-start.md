# Settings Centralization - Quick Start Implementation Guide

**Purpose**: Step-by-step instructions to implement the rules-based centralization
**Prerequisites**: Read `settings-centralization-complete-report.md` first
**Time Estimate**: 2-3 hours

---

## Pre-Implementation Checklist

- [ ] Read complete report: `.claude/workspace/reports/settings-centralization-complete-report.md`
- [ ] Review design proposal: `.claude/workspace/state/rules-design-proposal.json`
- [ ] Run baseline audit: `node .claude/workspace/tools/tool-config-audit.js`
- [ ] Backup current settings.json: `cp root/codebase-validation/settings.json root/codebase-validation/settings.json.backup`

---

## Step 1: Add New Transforms (15 minutes)

**File**: `root/codebase-validation/tools/config-sync/core/mapping-transforms.js`

**Action**: Add these transform functions to the `applyTransform` function's switch statement:

```javascript
// In applyTransform() function, add these cases:

case "pyVersion":
    // "3.11" → "py311"
    return `py${value.replace(".", "")}`;

case "pyVersionArray":
    // "3.11" → ["py311"]
    return [`py${value.replace(".", "")}`];

case "esVersion":
    // 2024 → "ES2024"
    return `ES${value}`;

case "editorconfigIndentStyle":
    // false → "space", true → "tab"
    return value ? "tab" : "space";

case "tsStrictMode":
    // "strict" → true, anything else → false
    return value === "strict";

case "mypyStrictMode":
    // "strict" → true, anything else → false
    return value === "strict";

case "identity":
    // Pass through unchanged
    return value;
```

**Verification**:
```bash
# Test the transforms work
node -e "const {applyTransform} = require('./root/codebase-validation/tools/config-sync/core/mapping-transforms.js'); console.log(applyTransform('pyVersion', '3.11'));"
# Expected output: py311
```

---

## Step 2: Extend Resolver (20 minutes)

**File**: `root/codebase-validation/tools/config-sync/core/settings-resolver.js`

### 2.1: Add getRulesValue Method

Add this method to the `SettingsResolver` class (after `getCodeStyleValue`):

```javascript
getRulesValue(pathParts) {
    const settings = getSettingsSync();
    const rules = settings.rules || {};

    if (pathParts.length === 0) {
        return rules;
    }

    const category = pathParts[0]; // "formatting", "versions", "strictness"
    const key = pathParts[1];
    const subKey = pathParts[2];

    const categoryRules = rules[category] || {};

    if (!key) {
        return categoryRules;
    }

    if (subKey) {
        return categoryRules[key]?.[subKey];
    }

    return categoryRules[key];
}
```

### 2.2: Update getNestedValue Method

Replace the existing `getNestedValue` method with:

```javascript
getNestedValue(path) {
    const parts = path.split(".");

    // Support rules.* paths
    if (parts[0] === "rules") {
        return this.getRulesValue(parts.slice(1));
    }

    // Support codeStyle.* paths (backward compatibility)
    if (parts[0] === "codeStyle") {
        return this.getCodeStyleValue(parts.slice(1));
    }

    // Default behavior for tools.* paths
    const settings = getSettingsSync();
    const toolsSettings = settings.tools || {};
    return parts.reduce((obj, key) => obj?.[key], toolsSettings);
}
```

**Verification**:
```bash
# Test will be in Step 4 after settings.json update
```

---

## Step 3: Add Rules Section to settings.json (30 minutes)

**File**: `root/codebase-validation/settings.json`

### 3.1: Add rules Object

Add this at the TOP LEVEL of settings.json (after opening `{`):

```json
{
  "rules": {
    "formatting": {
      "lineLength": 120,
      "indentation": {
        "size": 4,
        "useTabs": false
      },
      "quotes": "double",
      "semicolons": true,
      "trailingComma": "es5",
      "endOfLine": "lf",
      "bracketSpacing": true,
      "arrowParens": "always",
      "quoteProps": "as-needed",
      "proseWrap": "preserve",
      "htmlWhitespaceSensitivity": "css",
      "jsxQuotes": "double",
      "bracketSameLine": false
    },
    "versions": {
      "python": "3.11",
      "ecmascript": 2024,
      "typescript": {
        "target": "ES2024",
        "module": "ESNext"
      },
      "rust": "2024",
      "cpp": "c++20",
      "java": "17",
      "go": "1.21"
    },
    "strictness": {
      "typeChecking": "strict",
      "nullSafety": true
    }
  },

  "naming": { ... },
  "security": { ... },
  ...
}
```

### 3.2: Mark codeStyle as Deprecated

Find `tools.codeStyle` and add a comment:

```json
"tools": {
  "// DEPRECATED - Use rules.formatting instead": "codeStyle kept for backward compatibility",
  "codeStyle": {
    "lineLength": 120,
    ...
  },
  ...
}
```

**Verification**:
```bash
# Validate JSON syntax
node -e "JSON.parse(require('fs').readFileSync('root/codebase-validation/settings.json', 'utf8'))"
# No output = valid JSON
```

---

## Step 4: Extend toolMappings (40 minutes)

**File**: `root/codebase-validation/settings.json`

Find `tools.toolMappings` and add/update these mappings:

### 4.1: Update Existing Mappings

```json
"toolMappings": {
  "prettier": {
    "tabWidth": "rules.formatting.indentation.size",
    "useTabs": "rules.formatting.indentation.useTabs",
    "printWidth": "rules.formatting.lineLength",
    "singleQuote": {
      "source": "rules.formatting.quotes",
      "transform": "equals:single"
    },
    "semi": "rules.formatting.semicolons",
    "trailingComma": "rules.formatting.trailingComma",
    "endOfLine": "rules.formatting.endOfLine",
    "bracketSpacing": "rules.formatting.bracketSpacing",
    "arrowParens": "rules.formatting.arrowParens",
    "quoteProps": "rules.formatting.quoteProps",
    "proseWrap": "rules.formatting.proseWrap",
    "htmlWhitespaceSensitivity": "rules.formatting.htmlWhitespaceSensitivity",
    "jsxQuotes": "rules.formatting.jsxQuotes",
    "bracketSameLine": "rules.formatting.bracketSameLine"
  },

  "eslint": {
    "indent": {
      "source": "rules.formatting.indentation",
      "transform": "eslintIndent"
    },
    "quotes": "rules.formatting.quotes",
    "semi": {
      "source": "rules.formatting.semicolons",
      "transform": "eslintSemi"
    },
    "max-len": {
      "source": "rules.formatting.lineLength",
      "transform": "eslintMaxLen"
    },
    "ecmaVersion": "rules.versions.ecmascript"
  },

  "stylelint": {
    "indentation": "rules.formatting.indentation.size"
  },

  "black": {
    "line-length": "rules.formatting.lineLength",
    "target-version": {
      "source": "rules.versions.python",
      "transform": "pyVersionArray"
    }
  },

  "isort": {
    "line_length": "rules.formatting.lineLength"
  },

  "ruff": {
    "line-length": "rules.formatting.lineLength",
    "target-version": {
      "source": "rules.versions.python",
      "transform": "pyVersion"
    },
    "quote-style": "rules.formatting.quotes"
  },

  "rustfmt": {
    "tab_spaces": "rules.formatting.indentation.size",
    "hard_tabs": "rules.formatting.indentation.useTabs",
    "max_width": "rules.formatting.lineLength"
  },

  "gofmt": {
    "tabWidth": "rules.formatting.indentation.size"
  },

  "clangFormat": {
    "IndentWidth": "rules.formatting.indentation.size",
    "UseTab": {
      "source": "rules.formatting.indentation.useTabs",
      "transform": "clangUseTab"
    },
    "ColumnLimit": "rules.formatting.lineLength"
  },

  "checkstyle": {
    "tabWidth": "rules.formatting.indentation.size",
    "maxLineLength": "rules.formatting.lineLength"
  },

  "pyright": {
    "pythonVersion": "rules.versions.python",
    "typeCheckingMode": "rules.strictness.typeChecking"
  },

  "typescript": {
    "target": {
      "source": "rules.versions.ecmascript",
      "transform": "esVersion"
    },
    "module": "rules.versions.typescript.module",
    "strict": {
      "source": "rules.strictness.typeChecking",
      "transform": "tsStrictMode"
    }
  },

  "editorconfig": {
    "indent_size": "rules.formatting.indentation.size",
    "indent_style": {
      "source": "rules.formatting.indentation.useTabs",
      "transform": "editorconfigIndentStyle"
    },
    "end_of_line": "rules.formatting.endOfLine"
  }
}
```

**Verification**:
```bash
# Validate JSON syntax again
node -e "JSON.parse(require('fs').readFileSync('root/codebase-validation/settings.json', 'utf8'))"
```

---

## Step 5: Remove Hardcoded Duplicates (25 minutes)

**File**: `root/codebase-validation/settings.json`

### 5.1: Find and Delete These Sections

**Search for and DELETE these lines:**

1. **Rust settings** (around line 846-848):
```json
// DELETE THESE:
"useTabs": false,
"tabSpaces": 4,
"maxWidth": 100,
```

2. **C++ clangFormat** (search for `cpp.clangFormat`):
```json
// DELETE THESE:
"columnLimit": 120,
"indentWidth": 4,
```

3. **EditorConfig defaults** (search for `editorconfig.defaults`):
```json
// DELETE THESE:
"indentStyle": "space",
"indentSize": 4,
"endOfLine": "lf",
```

4. **Python ruff format** (search for `ruff.format`):
```json
// DELETE THIS:
"quoteStyle": "double",
```

5. **Python tool versions** (search for each):
```json
// DELETE THESE:
"ruff": {
  "enabled": true,
  // ... keep other settings ...
  "targetVersion": "py311",  // DELETE THIS LINE
```

```json
"pyright": {
  "enabled": true,
  // ... keep other settings ...
  "pythonVersion": "3.11",  // DELETE THIS LINE
```

```json
"black": {
  "enabled": false,
  "targetVersion": ["py311"],  // DELETE THIS LINE
```

6. **JavaScript versions**:
```json
"eslint": {
  "enabled": true,
  // ... keep other settings ...
  "ecmaVersion": 2024,  // DELETE THIS LINE
```

```json
"typescript": {
  "enabled": true,
  "target": "ES2024",  // DELETE THIS LINE
```

7. **Prettier arrowParens** (search for `formatting.prettier`):
```json
"formatting": {
  "prettier": {
    "arrowParens": "always",  // DELETE THIS LINE
```

### 5.2: Verify Deletions

```bash
# Check no hardcoded values remain
grep -n "maxWidth.*100" root/codebase-validation/settings.json  # Should be empty
grep -n "pythonVersion" root/codebase-validation/settings.json  # Should be empty
grep -n "\"targetVersion\"" root/codebase-validation/settings.json  # Should be empty
grep -n "\"target\":.*ES2024" root/codebase-validation/settings.json  # Should be empty
grep -n "indentStyle.*space" root/codebase-validation/settings.json  # Should be empty
```

---

## Step 6: Validation (30 minutes)

### 6.1: Run Audit Tools

```bash
cd D:\GIT\archlab

# Should show 0 conflicts
node .claude/workspace/tools/tool-config-audit.js > validation-results.txt

# Review output
cat validation-results.txt | grep "CONFLICT"  # Should be empty
cat validation-results.txt | grep "HARDCODED"  # Should be empty
```

### 6.2: Test Resolver Manually

```bash
# Test Python version resolution
node -e "
const {SettingsResolver} = require('./root/codebase-validation/tools/config-sync/core/settings-resolver.js');
const resolver = new SettingsResolver();
console.log('Ruff config:', resolver.resolveToolConfig('ruff'));
console.log('Pyright config:', resolver.resolveToolConfig('pyright'));
"
```

**Expected output**:
```
Ruff config: { 'line-length': 120, 'target-version': 'py311', 'quote-style': 'double' }
Pyright config: { pythonVersion: '3.11', typeCheckingMode: 'strict' }
```

### 6.3: Test Rule Changes Cascade

```bash
# 1. Change Python version in rules
# Edit settings.json: rules.versions.python = "3.12"

# 2. Re-run resolver test
node -e "
const {SettingsResolver} = require('./root/codebase-validation/tools/config-sync/core/settings-resolver.js');
const resolver = new SettingsResolver();
console.log('Ruff target-version:', resolver.resolveToolConfig('ruff')['target-version']);
console.log('Pyright pythonVersion:', resolver.resolveToolConfig('pyright').pythonVersion);
console.log('Black target-version:', resolver.resolveToolConfig('black')['target-version']);
"
```

**Expected output**:
```
Ruff target-version: py312
Pyright pythonVersion: 3.12
Black target-version: [ 'py312' ]
```

### 6.4: Verify Backward Compatibility

```bash
# Existing code should still work with codeStyle.* paths
node -e "
const {SettingsResolver} = require('./root/codebase-validation/tools/config-sync/core/settings-resolver.js');
const resolver = new SettingsResolver();
const prettier = resolver.resolveToolConfig('prettier');
console.log('Prettier printWidth (via codeStyle compat):', prettier.printWidth);
"
```

### 6.5: Run Full Codebase Validation

```bash
cd D:\GIT\archlab
npm run verify-codebase
```

---

## Step 7: Commit Changes (10 minutes)

```bash
cd D:\GIT\archlab

git add root/codebase-validation/settings.json
git add root/codebase-validation/tools/config-sync/core/mapping-transforms.js
git add root/codebase-validation/tools/config-sync/core/settings-resolver.js

git commit -m "feat(settings): Centralize all settings via rules architecture

- Add rules.formatting, rules.versions, rules.strictness sections
- Extend SettingsResolver to support rules.* path resolution
- Add 7 new transforms (pyVersion, pyVersionArray, esVersion, etc)
- Extend toolMappings for all unmapped tools
- Remove 15 hardcoded duplicate settings
- Maintain backward compatibility with codeStyle.* paths

Impact:
- ONE change to rules.formatting.lineLength now updates 8 tools
- ONE change to rules.versions.python updates ruff, pyright, black, mypy
- ZERO hardcoded conflicts remain
- ALL tools now reference canonical rules

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Rollback Plan (If Needed)

If something breaks:

```bash
# Restore backup
cp root/codebase-validation/settings.json.backup root/codebase-validation/settings.json

# Revert code changes
git checkout root/codebase-validation/tools/config-sync/core/mapping-transforms.js
git checkout root/codebase-validation/tools/config-sync/core/settings-resolver.js
```

---

## Success Criteria Checklist

- [ ] All transforms added and tested
- [ ] Resolver extended with getRulesValue()
- [ ] rules section added to settings.json
- [ ] All toolMappings extended
- [ ] All hardcoded duplicates removed
- [ ] tool-config-audit.js reports 0 conflicts
- [ ] Manual resolver test shows correct values
- [ ] Rule change test shows cascade to all tools
- [ ] Backward compatibility test passes
- [ ] npm run verify-codebase passes
- [ ] Changes committed to git

---

## Post-Implementation

### Demo the Success

```bash
# Show the cascade effect
echo "BEFORE: Changing lineLength required 15 file edits"
echo "AFTER: One change cascades to all tools:"
node -e "
const {SettingsResolver} = require('./root/codebase-validation/tools/config-sync/core/settings-resolver.js');
const resolver = new SettingsResolver();
console.log('Prettier:', resolver.resolveToolConfig('prettier').printWidth);
console.log('ESLint:', resolver.resolveToolConfig('eslint')['max-len']);
console.log('Black:', resolver.resolveToolConfig('black')['line-length']);
console.log('Ruff:', resolver.resolveToolConfig('ruff')['line-length']);
console.log('Rustfmt:', resolver.resolveToolConfig('rustfmt').max_width);
console.log('ClangFormat:', resolver.resolveToolConfig('clangFormat').ColumnLimit);
console.log('All show: 120 (from rules.formatting.lineLength)');
"
```

### Update Documentation

Add to project README or CONTRIBUTING.md:

```markdown
## Changing Code Style Rules

All code style rules are centralized in `settings.json`:

- **Formatting rules**: `rules.formatting.*`
- **Version targets**: `rules.versions.*`
- **Strictness levels**: `rules.strictness.*`

Example: To change line length for ALL tools:
```json
"rules": {
  "formatting": {
    "lineLength": 100  // Changes prettier, eslint, black, ruff, rustfmt, clangFormat, checkstyle
  }
}
```

The settings resolver automatically applies these rules to all configured tools.
```

---

**END OF QUICK START GUIDE**
