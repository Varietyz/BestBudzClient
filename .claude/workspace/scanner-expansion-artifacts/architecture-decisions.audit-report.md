# Architecture Decisions

## Workflow: scanner-expansion-workflow
## Phase: Architecture Validation Complete
## Last Updated: 2026-01-01 (Agent 2)

---

## Scanner Inheritance Hierarchy

```
BaseScanner (root/codebase-validation/core/base-scanner.js:38)
│
├── BaseCssScanner (root/codebase-validation/core/base/base-css-scanner.js:7)
│   └── [45+ CSS scanners in scanners/quality/css/**]
│
├── BaseSecurityScanner (root/codebase-validation/core/base/scanners/base-security-scanner.js:23)
│   └── CSPUnsafeEvalScanner, CSPInlineStylesScanner, CSPInlineEventsScanner
│
├── BaseAccessibilityScanner (root/codebase-validation/core/base/scanners/base-accessibility-scanner.js:27)
│   └── AccessibilityImagesScanner, AccessibilityInteractiveScanner
│
├── BaseMagicLiteralScanner (root/codebase-validation/core/base/scanners/base-magic-literal-scanner.js:29)
│   └── MagicColorsScanner, MagicMeasurementsScanner
│
├── BaseConstantsScanner (root/codebase-validation/core/base/scanners/base-constants-scanner.js:4)
│   └── ConstantImportsScanner, ConstantDuplicatesScanner
│
└── [Direct BaseScanner Extensions]
    ├── NativeDialogsScanner (quality/code/)
    ├── CodeDuplicationScanner (quality/code/)
    ├── MagicNumbersScanner (quality/code/magic-literals/)
    ├── JsonStringifyScanner (quality/code/)
    ├── UnderscorePrefixScanner (quality/code/)
    ├── CodeNamingScanner (quality/code/)
    ├── CodeCommentsScanner (quality/code/)
    ├── FileLimitsScanner (quality/structure/)
    ├── FolderLimitsScanner (quality/structure/)
    ├── FileNamingScanner (quality/structure/)
    ├── BaseClassesScanner (code-analysis/)
    ├── ConstantsScanner (code-analysis/)
    ├── UtilitiesScanner (code-analysis/)
    ├── ImportPatternsScanner (code-analysis/)
    └── [NEW SCANNERS WILL EXTEND HERE]
```

---

## Required Scanner Template

### Template for Language-Agnostic Scanner (Applies to All Languages)

```javascript
import { Logger } from "../../../constants/index.js";
import { BaseScanner } from "../../../core/base-scanner.js";
import {
    getCodeQualitySettingsSync,
    buildSkipMessage,
    getLogIcon
} from "../../../core/settings/settings-manager.js";
import { REGISTRY_TYPES } from "../../../registry/schema/registry-schema.js";

/**
 * {Scanner Name} Scanner
 *
 * Implements: Perceptual-Loop Algorithm
 * Algorithm: FIND<Files> -> ANALYZE<Patterns> -> WRITE<Registry> -> REPORT<Summary>
 *
 * {Description of what this scanner detects}
 *
 * Settings-driven via: codeQuality.{category}.{settingKey}
 *
 * Detected patterns:
 * - {pattern1} -> {suggested fix}
 * - {pattern2} -> {suggested fix}
 *
 * Writes to registry: {registry-key} (VIOLATION_SIMPLE)
 * Corresponding validator: {scanner-name}-validator.js
 * Corresponding fixer: {scanner-name}-auto-fixer.js (optional)
 */
export class {ScannerName}Scanner extends BaseScanner {
    /**
     * Language support: null = all languages, or array of language IDs
     * Example: ["javascript", "python", "java"]
     */
    getSupportedLanguages() {
        return null; // All languages
    }

    async analyzeContentPatterns(_files) {
        // Check if scanner is enabled in settings
        const codeQualitySettings = getCodeQualitySettingsSync();
        const categorySettings = codeQualitySettings?.{category} ?? {};

        if (!categorySettings.{settingKey}) {
            Logger.info(buildSkipMessage(this.getName()));
            return;
        }

        this.dirsToScan = this._getDirectoriesToScan();
        const violations = await this.scanForViolations();
        const summary = this.createSummary(violations);

        // Write to registry using RegistryBuilder
        await this.registry.build("{registry-key}", REGISTRY_TYPES.VIOLATION_SIMPLE, {
            violations,
            summary,
            autoFixAvailable: false, // Set to true if auto-fixer exists
        });

        this.logResults(summary);
    }

    async scanForViolations() {
        const violations = [];

        const scanFile = async (filePath) => {
            // Language-aware filtering (CRITICAL: always include this check)
            if (!this.shouldProcessForLanguage(filePath)) {
                return;
            }

            const content = await this.readFile(filePath);
            const relativePath = this.getRelativePath(filePath);
            this.scanFileContent(content, relativePath, filePath, violations);
        };

        await this.scanDirectories(this.dirsToScan, scanFile);
        return violations;
    }

    scanFileContent(content, relativePath, absolutePath, violations) {
        const lines = content.split("\n");

        lines.forEach((line, index) => {
            const lineNumber = index + 1;

            // Skip comments
            if (/^\s*(\/\/|\/\*|\*)/.test(line)) {
                return;
            }

            // Pattern detection logic here
            const match = line.match(/your-pattern-regex/);
            if (match) {
                violations.push({
                    file: relativePath,
                    absolutePath,
                    line: lineNumber,
                    code: line.trim(),
                    type: "violation-type",
                    message: match[1],
                    rule: "Human-readable rule description",
                    fix: this.generateFix(match[1]), // Optional
                });
            }
        });
    }

    createSummary(violations) {
        const uniqueFiles = new Set(violations.map((v) => v.file));

        return {
            total_violations: violations.length,
            files_with_violations: uniqueFiles.size,
            // Add type-specific counts if needed
        };
    }

    logResults(summary) {
        if (summary.total_violations === 0) {
            Logger.info(`${getLogIcon("success")} No {scanner-name} violations found`);
            return;
        }

        Logger.warn(
            `${getLogIcon("warning")} Found ${summary.total_violations} violations in ${summary.files_with_violations} files`
        );
    }

    getName() {
        return "{scanner-name}"; // Must match settings.json key
    }
}
```

### Template for Language-Specific Scanner (JavaScript/TypeScript Only)

```javascript
export class {ScannerName}Scanner extends BaseScanner {
    /**
     * Restrict to specific languages
     */
    getSupportedLanguages() {
        return ["javascript"]; // Only JS/TS files
    }

    // ... rest follows same structure as above
}
```

### Template Using Specialized Base Class (Security Scanner Example)

```javascript
import { BaseSecurityScanner } from "../../../core/base/scanners/base-security-scanner.js";

export class {ScannerName}Scanner extends BaseSecurityScanner {
    getSupportedLanguages() {
        return ["javascript"];
    }

    getScannerCspRule() {
        return "enforce{Rule}"; // Settings key under security.csp
    }

    getViolationPatterns() {
        return {
            pattern_type: {
                pattern: /regex-pattern/g,
                exemptions: [/\/\/.*exemption/, /\/\*.*exemption.*\*\//],
                description: "Human-readable description",
            },
        };
    }

    getFixSuggestion(type, pattern) {
        if (type === "pattern_type") {
            return "Specific fix suggestion for this pattern";
        }
        return "Generic fix suggestion";
    }

    getName() {
        return "{scanner-name}";
    }
}
```

---

## Design Rationale

| Decision | Rationale | Alternative Considered |
|----------|-----------|------------------------|
| **Extend BaseScanner Directly** | Provides full Perceptual-Loop infrastructure (FIND→ANALYZE→WRITE→REPORT) with minimal boilerplate | Creating standalone scanners - rejected due to code duplication |
| **Language-Aware via getSupportedLanguages()** | Bidirectional filtering: scanner declares support AND language spec includes scanner in applicableScanners array | File extension matching only - rejected as less maintainable |
| **Settings-Driven Enablement** | All scanners check settings before running, respecting enabled.scanners.{name} flag | Always-on scanners - rejected to allow user control |
| **RegistryBuilder for Output** | Standardized JSON output to .registry/ with metadata, validation, and schema enforcement | Direct file writes - rejected due to lack of validation |
| **Flat Settings Structure** | Current settings.json uses flat list under enabled.scanners (lines 560-631) | Category-based structure - not yet implemented, can be added later |

---

## Integration Points

### 1. RegistryBuilder Integration

**Location:** `root/codebase-validation/registry/registry-builder.js`

**Pattern:**
```javascript
await this.registry.build(key, type, data);
```

**Arguments:**
- `key` (string): Registry key (e.g., "native-dialogs", "soc-violations")
- `type` (REGISTRY_TYPES enum): VIOLATION_SIMPLE, CODE_ANALYSIS, etc.
- `data` (object): Must include `violations` and optionally `summary`, `autoFixAvailable`

**Output:**
- Writes to `.registry/{inferred-path}/{key}.json`
- Auto-registers path in `registry/paths/registry-paths.json`
- Includes metadata: generator, timestamp, version

**Evidence:**
- Line 32-45: `build()` method validates data, adds metadata, writes JSON
- Line 26: `RegistryPathInferrer.inferPathForNewKey()` auto-determines file location

### 2. Settings Integration

**Location:** `root/codebase-validation/settings.json`

**Current Structure (Lines 560-631):**
```json
{
  "enabled": {
    "scanners": {
      "native-dialogs": true,
      "magic-numbers": true,
      "code-duplication": true,
      // ... 60+ scanners in flat list
    }
  }
}
```

**Setting Access Pattern:**
```javascript
const codeQualitySettings = getCodeQualitySettingsSync();
if (!codeQualitySettings?.category?.settingKey) {
    Logger.info(buildSkipMessage(this.getName()));
    return;
}
```

**Evidence:**
- Lines 560-631: All scanners listed in flat structure
- Lines 36-43 in native-dialogs-scanner.js: Settings check pattern
- No category-based organization exists yet

**BLOCKER RESOLUTION:**
- **Status:** RESOLVED - Use existing flat structure
- **Approach:** Add new scanners to `enabled.scanners` flat list
- **Future Enhancement:** Can refactor to categories later without breaking changes

### 3. Language Spec Integration

**Location:** `root/codebase-validation/core/settings/languages/specs/{lang}.js`

**applicableScanners Format:**
```javascript
export const JAVASCRIPT_SPEC = Object.freeze({
    // ... meta, extensions, syntax, patterns, naming ...

    applicableScanners: [
        "code-comments",
        "magic-numbers",
        "underscore-prefix",
        "native-dialogs",
        "base-classes",
        "constant-aliases",
        // ... add new scanner names here
    ],

    // ... rest of spec
});
```

**Bidirectional Filtering Logic (base-scanner.js:329-343):**
1. **Scanner declares support:** `getSupportedLanguages()` returns null (all) or array
2. **Language spec includes scanner:** File extension → language → check `applicableScanners` array
3. **Both must match:** Scanner processes file only if both conditions true

**Evidence:**
- javascript.js lines 61-70: applicableScanners array of 8 scanners
- base-scanner.js lines 334-343: shouldProcessForLanguage() bidirectional check

**Update Required:**
- Add new scanner names to applicableScanners for relevant language specs
- Language-agnostic scanners (getSupportedLanguages() returns null) still need manual addition to specs

---

## Scanner Directory Organization

**Verified Structure:**

```
root/codebase-validation/scanners/
├── code-analysis/           # Code discovery (not violations)
│   ├── base-classes-scanner.js
│   ├── constants-scanner.js
│   ├── utilities-scanner.js
│   └── import-patterns-scanner.js
│
├── discovery/               # Automated discovery
│   └── css/
│       └── css-variables-discovered-scanner.js
│
└── quality/                 # Quality violations
    ├── code/                # **NEW SCANNERS GO HERE**
    │   ├── native-dialogs-scanner.js
    │   ├── code-duplication-scanner.js
    │   ├── underscore-prefix-scanner.js
    │   ├── json-stringify-scanner.js
    │   ├── code-naming-scanner.js
    │   ├── code-comments-scanner.js
    │   └── magic-literals/
    │       ├── magic-numbers-scanner.js
    │       ├── magic-colors-scanner.js
    │       └── magic-measurements-scanner.js
    │
    ├── cross-module/        # Cross-file violations
    │   ├── constant-aliases-scanner.js
    │   ├── constant-duplicates-scanner.js
    │   ├── constant-imports-scanner.js
    │   └── constant-discovery-scanner.js
    │
    ├── css/                 # CSS-specific (45+ scanners)
    │   ├── architecture/
    │   ├── best-practices/
    │   ├── design-tokens/
    │   ├── quality/
    │   ├── responsive/
    │   └── variable-architecture/
    │
    ├── electron/            # Electron-specific
    │   └── browser-window-icon-scanner.js
    │
    ├── security/            # **SECURITY SCANNERS GO HERE**
    │   ├── csp-unsafe-eval-scanner.js
    │   ├── csp-inline-styles-scanner.js
    │   ├── csp-inline-events-scanner.js
    │   └── csp-header-scanner.js
    │
    ├── structure/           # File/folder structure
    │   ├── file-limits-scanner.js
    │   ├── folder-limits-scanner.js
    │   └── file-naming-scanner.js
    │
    └── web/                 # Web accessibility
        ├── accessibility-images-scanner.js
        └── accessibility-interactive-scanner.js
```

**Placement Rules for New Scanners:**

| Scanner Category | Directory | Examples |
|------------------|-----------|----------|
| SOLID Violations | `quality/code/` | soc-violations-scanner.js, srp-violations-scanner.js |
| Anti-Patterns | `quality/code/` | production-anti-patterns-scanner.js, cyclomatic-complexity-scanner.js |
| Architecture | `quality/code/` or `quality/architecture/` (create if needed) | layer-violations-scanner.js, circular-dependencies-scanner.js |
| Code Metrics | `quality/code/` | coupling-metrics-scanner.js, cohesion-metrics-scanner.js |
| Code Smells | `quality/code/` | long-method-scanner.js, data-clump-scanner.js |
| Security | `quality/security/` | injection-vulnerabilities-scanner.js, security-inheritance-scanner.js |
| Performance | `quality/code/` or `quality/performance/` (create if needed) | n-plus-one-queries-scanner.js, memory-leak-patterns-scanner.js |
| Documentation | `quality/code/` | api-documentation-scanner.js, test-coverage-gaps-scanner.js |

**Evidence:**
- Bash output: scanners/quality/ contains code/, security/, structure/, css/, web/, cross-module/, electron/
- 65 scanners found across all subdirectories
- Existing pattern: quality/code/ for general code quality, specialized subdirs for domain-specific

---

## Pre/Post Architecture State

### Pre-Implementation (Verified)
- Existing scanners: 65 total
  - code-analysis/: 4 scanners
  - discovery/css/: 1 scanner
  - quality/code/: 10 scanners + 3 magic-literals
  - quality/cross-module/: 4 scanners
  - quality/css/: 45 scanners
  - quality/security/: 4 scanners
  - quality/structure/: 3 scanners
  - quality/web/: 2 scanners
  - quality/electron/: 1 scanner
- Language specs: 53 files (not 41 as originally assumed)
- Settings structure: Flat list (no categories)
- Base classes: 5 specialized (BaseCssScanner, BaseSecurityScanner, BaseAccessibilityScanner, BaseMagicLiteralScanner, BaseConstantsScanner)

### Post-Implementation (Planned)
- New scanners: 23 planned
- Total scanners: 88
- Updated language specs: All 53 specs receive updated applicableScanners arrays
- Settings additions: 23 new entries in enabled.scanners flat list
- New directories (if needed): quality/architecture/, quality/performance/
- All new scanners follow verified template pattern
