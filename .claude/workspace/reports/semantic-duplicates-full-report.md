# SETTINGS.JSON SEMANTIC DUPLICATE ANALYSIS - FULL REPORT

**Analysis Date**: 2025-12-31
**File Analyzed**: `root/codebase-validation/settings.json` (1686 lines)
**Total Categories Identified**: 62
**Settings Requiring Consolidation**: 180+
**Expected Reduction**: ~47% (1686 lines → 800-900 lines)

---

## EXECUTIVE SUMMARY

This report identifies all semantic duplicates in settings.json that should be centralized into the rules architecture. Settings are grouped into 62 categories based on their semantic purpose, with priorities assigned based on conflict severity and duplication level.

### Priority Distribution:
- **🔴 HIGH Priority**: 18 categories (immediate conflicts requiring resolution)
- **🟡 MEDIUM Priority**: 30 categories (duplicated settings, should consolidate)
- **🟢 LOW Priority**: 14 categories (future consistency, tool-specific rules)

### Critical Finding:
**The entire `tools.codeStyle` section (lines 565-579) is a COMPLETE DUPLICATE of `rules.formatting` (lines 524-537) and must be DELETED IMMEDIATELY.**

---

## 🔴 HIGH PRIORITY CATEGORIES (18)

### CATEGORY 1: Tool Enabled Flags
**Priority**: HIGH
**Semantic Purpose**: Control whether a tool is active/disabled

**Settings**:
- `tools.javascript.eslint.enabled` (line 708) = `true`
- `tools.javascript.typescript.enabled` (line 744) = `true`
- `tools.javascript.knip.enabled` (line 751) = `true`
- `tools.javascript.biome.enabled` (line 757) = `true`
- `tools.javascript.biome.formatter.enabled` (line 759) = `true`
- `tools.javascript.biome.linter.enabled` (line 762) = `true`
- `tools.javascript.biome.organizeImports.enabled` (line 765) = `true`
- `tools.javascript.oxlint.enabled` (line 768) = `true`
- `tools.formatting.prettier.enabled` (line 777) = `true`
- `tools.python.ruff.enabled` (line 786) = `true`
- `tools.python.ruff.format.enabled` (line 791) = `true`
- `tools.python.pyright.enabled` (line 795) = `true`
- `tools.python.black.enabled` (line 799) = `false` ⚠️ **CONFLICT**
- `tools.python.isort.enabled` (line 803) = `false` ⚠️ **CONFLICT**
- `tools.python.prospector.enabled` (line 808) = `true`
- `tools.python.mypy.enabled` (line 814) = `true`
- `tools.python.bandit.enabled` (line 818) = `true`
- `tools.css.stylelint.enabled` (line 827) = `true`
- `tools.html.htmlhint.enabled` (line 854) = `true`
- `tools.dependencies.depcheck.enabled` (line 871) = `true`
- `tools.dependencies.npmPrune.enabled` (line 876) = `true`
- `tools.java.checkstyle.enabled` (line 883) = `true`
- `tools.java.googleJavaFormat.enabled` (line 888) = `true`
- `tools.java.pmd.enabled` (line 893) = `true`
- `tools.java.spotbugs.enabled` (line 898) = `true`
- `tools.java.errorProne.enabled` (line 903) = `true`
- `tools.go.golangciLint.enabled` (line 910) = `true`
- `tools.go.staticcheck.enabled` (line 924) = `true`
- `tools.go.gofmt.enabled` (line 928) = `true`
- `tools.go.goimports.enabled` (line 932) = `true`
- `tools.rust.rustfmt.enabled` (line 939) = `true`
- `tools.rust.clippy.enabled` (line 943) = `true`
- `tools.cpp.clangFormat.enabled` (line 955) = `true`
- `tools.cpp.clangTidy.enabled` (line 961) = `true`
- `tools.cpp.cppcheck.enabled` (line 966) = `true`
- `tools.multiLang.editorconfig.enabled` (line 974) = `true`
- `tools.multiLang.preCommit.enabled` (line 983) = `true`

**Recommended Central Rule**:
```json
"rules.tooling.enabled": {
  "linters": true,
  "formatters": true,
  "typeCheckers": true,
  "securityScanners": true,
  "dependencyCheckers": true,
  "legacyTools": false
}
```

---

### CATEGORY 2: Auto-Fix Flags
**Priority**: HIGH
**Semantic Purpose**: Control automatic fixing/correction behavior

**Settings**:
- `tools.javascript.eslint.autoFix` (line 709) = `true`
- `tools.css.stylelint.autoFix` (line 828) = `true`
- `tools.javascript.oxlint.fix` (line 769) = `true`
- `tools.rust.clippy.fix` (line 947) = `true`
- `tools.dependencies.npmPrune.dryRun` (line 877) = `false` (inverted logic)
- `tools.java.googleJavaFormat.fixImports` (line 890) = `true`
- `tools.go.gofmt.simplify` (line 929) = `true`

**Recommended Central Rule**:
```json
"rules.autoFix": {
  "enabled": true,
  "linting": true,
  "formatting": true,
  "imports": true,
  "dryRun": false
}
```

---

### CATEGORY 3: Indentation Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Define indentation size and tab/space preference

**Settings**:
- `rules.formatting.indentation.size` (line 526) = `4`
- `rules.formatting.indentation.useTabs` (line 526) = `false`
- `tools.codeStyle.indentation.size` (line 566) = `4` ⚠️ **EXACT DUPLICATE**
- `tools.codeStyle.indentation.useTabs` (line 566) = `false` ⚠️ **EXACT DUPLICATE**

**Issue**: Complete duplication between `rules.formatting.indentation` and `tools.codeStyle.indentation`

**Recommended Action**:
```
DELETE "tools.codeStyle.indentation" entirely
KEEP ONLY "rules.formatting.indentation"
```

---

### CATEGORY 4: Line Length Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Define maximum line length

**Settings**:
- `rules.formatting.lineLength` (line 525) = `120`
- `tools.codeStyle.lineLength` (line 567) = `120` ⚠️ **EXACT DUPLICATE**

**Issue**: Complete duplication between `rules.formatting.lineLength` and `tools.codeStyle.lineLength`

**Recommended Action**:
```
DELETE "tools.codeStyle.lineLength"
KEEP ONLY "rules.formatting.lineLength"
```

---

### CATEGORY 5: Quotes Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Define quote style (single vs double)

**Settings**:
- `rules.formatting.quotes` (line 527) = `"double"`
- `tools.codeStyle.quotes` (line 568) = `"double"` ⚠️ **EXACT DUPLICATE**

**Issue**: Complete duplication between `rules.formatting.quotes` and `tools.codeStyle.quotes`

**Recommended Action**:
```
DELETE "tools.codeStyle.quotes"
KEEP ONLY "rules.formatting.quotes"
```

---

### CATEGORY 6: Semicolons Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Require/forbid semicolons

**Settings**:
- `rules.formatting.semicolons` (line 528) = `true`
- `tools.codeStyle.semicolons` (line 569) = `true` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.semicolons"
KEEP ONLY "rules.formatting.semicolons"
```

---

### CATEGORY 7: Trailing Comma Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control trailing comma behavior

**Settings**:
- `rules.formatting.trailingComma` (line 529) = `"es5"`
- `tools.codeStyle.trailingComma` (line 570) = `"es5"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.trailingComma"
KEEP ONLY "rules.formatting.trailingComma"
```

---

### CATEGORY 8: End Of Line Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Define line ending style (LF, CRLF, CR)

**Settings**:
- `rules.formatting.endOfLine` (line 530) = `"lf"`
- `tools.codeStyle.endOfLine` (line 571) = `"lf"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.endOfLine"
KEEP ONLY "rules.formatting.endOfLine"
```

---

### CATEGORY 9: Bracket Spacing Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control spacing inside brackets

**Settings**:
- `rules.formatting.bracketSpacing` (line 531) = `true`
- `tools.codeStyle.bracketSpacing` (line 572) = `true` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.bracketSpacing"
KEEP ONLY "rules.formatting.bracketSpacing"
```

---

### CATEGORY 10: Arrow Parens Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control parentheses around arrow function parameters

**Settings**:
- `rules.formatting.arrowParens` (line 532) = `"always"`
- `tools.codeStyle.arrowParens` (line 573) = `"always"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.arrowParens"
KEEP ONLY "rules.formatting.arrowParens"
```

---

### CATEGORY 11: Quote Props Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control when to quote object property names

**Settings**:
- `rules.formatting.quoteProps` (line 533) = `"as-needed"`
- `tools.codeStyle.quoteProps` (line 574) = `"as-needed"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.quoteProps"
KEEP ONLY "rules.formatting.quoteProps"
```

---

### CATEGORY 12: JSX Quotes Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Define quote style for JSX attributes

**Settings**:
- `rules.formatting.jsxQuotes` (line 536) = `"double"`
- `tools.codeStyle.jsxQuotes` (line 575) = `"double"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.jsxQuotes"
KEEP ONLY "rules.formatting.jsxQuotes"
```

---

### CATEGORY 13: Bracket Same Line Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control bracket placement on same line or new line

**Settings**:
- `rules.formatting.bracketSameLine` (line 537) = `false`
- `tools.codeStyle.bracketSameLine` (line 576) = `false` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.bracketSameLine"
KEEP ONLY "rules.formatting.bracketSameLine"
```

---

### CATEGORY 14: Prose Wrap Settings (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control markdown/prose text wrapping

**Settings**:
- `rules.formatting.proseWrap` (line 534) = `"preserve"`
- `tools.codeStyle.proseWrap` (line 577) = `"preserve"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.proseWrap"
KEEP ONLY "rules.formatting.proseWrap"
```

---

### CATEGORY 15: HTML Whitespace Sensitivity (DUPLICATE CONFLICT)
**Priority**: HIGH
**Semantic Purpose**: Control whitespace handling in HTML

**Settings**:
- `rules.formatting.htmlWhitespaceSensitivity` (line 535) = `"css"`
- `tools.codeStyle.htmlWhitespaceSensitivity` (line 578) = `"css"` ⚠️ **EXACT DUPLICATE**

**Recommended Action**:
```
DELETE "tools.codeStyle.htmlWhitespaceSensitivity"
KEEP ONLY "rules.formatting.htmlWhitespaceSensitivity"
```

---

### CATEGORY 16: Max Nesting Depth
**Priority**: HIGH
**Semantic Purpose**: Control maximum nesting depth to avoid complexity

**Settings**:
- `css.selectors.maxComplexity` (line 249) = `3`
- `css.selectors.maxSelectorDepth` (line 250) = `3`
- `css.selectors.maxChainedClasses` (line 251) = `3`
- `tools.css.stylelint.rules.maxNestingDepth` (line 841) = `3`

**Recommended Central Rule**:
```json
"rules.complexity.maxDepth": {
  "cssSelectors": 3,
  "cssNesting": 3,
  "classChaining": 3
}
```

---

### CATEGORY 17: Precision Decimal Places
**Priority**: HIGH
**Semantic Purpose**: Control numeric precision in output

**Settings**:
- `css.units.remPrecisionDecimalPlaces` (line 261) = `3`
- `tools.css.stylelint.rules.numberMaxPrecision` (line 846) = `3`

**Recommended Central Rule**:
```json
"rules.formatting.precision": {
  "css": 3,
  "numbers": 3
}
```

---

### CATEGORY 18: Import Sorting and Organizing
**Priority**: HIGH
**Semantic Purpose**: Control import statement organization

**Settings**:
- `tools.javascript.biome.organizeImports.enabled` (line 765) = `true`
- `tools.rust.rustfmt.reorderImports` (line 940) = `true`
- `tools.cpp.clangFormat.sortIncludes` (line 958) = `true`
- `tools.java.googleJavaFormat.fixImports` (line 890) = `true`

**Recommended Central Rule**:
```json
"rules.imports.organize": {
  "enabled": true,
  "sort": true,
  "autoFix": true
}
```

---

## 🟡 MEDIUM PRIORITY CATEGORIES (30)

### CATEGORY 19: Security Severity Levels
**Priority**: MEDIUM
**Semantic Purpose**: Define severity for security violations

**Settings**:
- `security.severity.inlineEvents` (line 175) = `"high"`
- `security.severity.inlineStyles` (line 176) = `"medium"`
- `security.severity.unsafeEval` (line 177) = `"critical"`
- `security.severity.missingHeaders` (line 178) = `"medium"`

**Recommended Central Rule**:
```json
"rules.severity.security": {
  "inlineEvents": "high",
  "inlineStyles": "medium",
  "unsafeEval": "critical",
  "missingHeaders": "medium"
}
```

---

### CATEGORY 20: CSS Design Token Violation Severity
**Priority**: MEDIUM
**Semantic Purpose**: Define severity for CSS/design token violations

**Settings**:
- `patterns.violationSeverities.hardcodedColor` (line 1493) = `"high"`
- `patterns.violationSeverities.hardcodedRadius` (line 1494) = `"high"`
- `patterns.violationSeverities.hardcodedShadow` (line 1495) = `"high"`
- `patterns.violationSeverities.hardcodedSpacing` (line 1496) = `"medium"`
- `patterns.violationSeverities.hardcodedZIndex` (line 1497) = `"medium"`
- `patterns.violationSeverities.hardcodedFontWeight` (line 1498) = `"medium"`
- `patterns.violationSeverities.hardcodedFontSize` (line 1499) = `"medium"`
- `patterns.violationSeverities.hardcodedFontFamily` (line 1500) = `"medium"`
- `patterns.violationSeverities.hardcodedLineHeight` (line 1501) = `"medium"`
- `patterns.violationSeverities.hardcodedLetterSpacing` (line 1502) = `"low"`

**Recommended Central Rule**:
```json
"rules.severity.designTokens": {
  "colors": "high",
  "spacing": "medium",
  "typography": "medium",
  "effects": "high"
}
```

---

### CATEGORY 21: Architectural Violation Severity
**Priority**: MEDIUM
**Semantic Purpose**: Define severity for architectural violations

**Settings**:
- `patterns.violationSeverities.bemViolation` (line 1503) = `"low"`
- `patterns.violationSeverities.namingInconsistency` (line 1504) = `"medium"`
- `patterns.violationSeverities.namingUnderscore` (line 1505) = `"low"`
- `patterns.violationSeverities.scopingViolation` (line 1506) = `"high"`
- `patterns.violationSeverities.duplicateComponent` (line 1507) = `"critical"`
- `patterns.violationSeverities.duplicateVariableValue` (line 1508) = `"high"`
- `patterns.violationSeverities.unusedCode` (line 1509) = `"low"`
- `patterns.violationSeverities.vendorPrefix` (line 1510) = `"low"`
- `patterns.violationSeverities.complexSelector` (line 1511) = `"medium"`

**Recommended Central Rule**:
```json
"rules.severity.architecture": {
  "duplicates": "critical",
  "scoping": "high",
  "naming": "medium",
  "complexity": "medium",
  "unusedCode": "low"
}
```

---

### CATEGORY 22: Tool Severity and Report Levels
**Priority**: MEDIUM
**Semantic Purpose**: Control tool-specific reporting levels

**Settings**:
- `tools.java.checkstyle.severity` (line 885) = `"warning"`
- `tools.python.bandit.severity` (line 819) = `"medium"`
- `tools.python.bandit.confidence` (line 820) = `"medium"`
- `tools.java.spotbugs.reportLevel` (line 900) = `"medium"`

**Recommended Central Rule**:
```json
"rules.tooling.reportLevel": {
  "default": "medium",
  "security": "high"
}
```

---

### CATEGORY 23: Context Lines for Scanning
**Priority**: MEDIUM
**Semantic Purpose**: Define how many lines of context to show around violations

**Settings**:
- `css.scanning.previousLinesContext` (line 256) = `3`
- `css.scanning.contextLines` (line 257) = `2`
- `css.scanning.maxUsageExamples` (line 258) = `3`
- `scanning.lookAheadLines` (line 1181) = `5`
- `scanning.lookBackLines` (line 1182) = `10`

**Recommended Central Rule**:
```json
"rules.scanning.context": {
  "linesBefore": 3,
  "linesAfter": 3,
  "maxExamples": 3,
  "lookAhead": 5,
  "lookBack": 10
}
```

---

### CATEGORY 24: Skip/Exclude Directories
**Priority**: MEDIUM
**Semantic Purpose**: Directories to exclude from all scanning

**Settings**:
- `scanning.skipDirectories` (line 1168) = `["node_modules", "vendor", "deps", "dist", "build", "out", "deploy", "target", "coverage", "__pycache__"]`
- `tools.dependencies.depcheck.ignoreDirs` (line 873) = `["dist", "build", "node_modules"]`
- `exclusions.cspExemptDirectories` (line 443) = `["test", "tests", "benchmark", "benchmarks", "scanners", "validators"]`
- `registry.excludedDirs` (line 435) = `["dsl-quality"]`

**Recommended Central Rule**:
```json
"rules.exclusions.directories": {
  "build": ["dist", "build", "out", "deploy", "target"],
  "dependencies": ["node_modules", "vendor", "deps"],
  "generated": ["coverage", "__pycache__"],
  "testing": ["test", "tests", "benchmark", "benchmarks"]
}
```

---

### CATEGORY 25: Exclude File Patterns
**Priority**: MEDIUM
**Semantic Purpose**: File patterns to exclude from scanning

**Settings**:
- `exclusions.pathExcludePatterns` (line 445) = `["constants/", "-config.js", "-template.js", "-templates.js", "-styles.js", "constants.js", "animation-manager.js"]`
- `tools.dependencies.depcheck.ignoreMatches` (line 872) = `["@types/*", "eslint-*", "prettier"]`
- `tools.python.prospector.ignorePatterns` (line 811) = `["migrations/*", "tests/*"]`
- `tools.python.bandit.exclude` (line 821) = `["tests"]`
- `registry.excludedPathPrefixes` (line 436) = `["agents/"]`

**Recommended Central Rule**:
```json
"rules.exclusions.patterns": {
  "configFiles": ["-config.js", "-template.js"],
  "constants": ["constants/", "constants.js"],
  "tooling": ["@types/*", "eslint-*"],
  "generated": ["migrations/*"],
  "testing": ["tests/*"]
}
```

---

### CATEGORY 26: CSP Security Exemptions
**Priority**: MEDIUM
**Semantic Purpose**: Files/patterns exempt from CSP security checks

**Settings**:
- `exclusions.cspExemptFiles` (line 442) = `["accept-cert.html"]`
- `exclusions.cspExemptDirectories` (line 443) = `["test", "tests", "benchmark", "benchmarks", "scanners", "validators"]`
- `exclusions.cspExemptPatterns` (line 444) = `["principles"]`

**Recommended Central Rule**:
```json
"rules.security.csp.exemptions": {
  "files": ["accept-cert.html"],
  "directories": ["test", "tests", "benchmark", "benchmarks"],
  "patterns": ["principles"]
}
```

---

### CATEGORY 27: Design Token Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Whether to enforce design tokens instead of hardcoded values

**Settings**:
- `css.designTokens.enforceColors` (line 240) = `true`
- `css.designTokens.enforceSpacing` (line 241) = `true`
- `css.designTokens.enforceTypography` (line 242) = `true`
- `css.designTokens.enforceShadows` (line 243) = `true`
- `css.designTokens.enforceZIndex` (line 244) = `true`
- `css.designTokens.enforceTransitions` (line 245) = `true`
- `css.designTokens.enforceRadius` (line 246) = `true`

**Recommended Central Rule**:
```json
"rules.designTokens.enforce": {
  "colors": true,
  "spacing": true,
  "typography": true,
  "effects": true,
  "layout": true
}
```

---

### CATEGORY 28: CSS Selector Rules
**Priority**: MEDIUM
**Semantic Purpose**: Rules for CSS selector complexity and structure

**Settings**:
- `css.selectors.enforceBEM` (line 252) = `true`
- `css.selectors.requireScoping` (line 253) = `true`
- `tools.css.stylelint.rules.selectorNoQualifyingType` (line 840) = `true`
- `tools.css.stylelint.rules.selectorMaxId` (line 838) = `0`
- `tools.css.stylelint.rules.selectorMaxType` (line 839) = `2`

**Recommended Central Rule**:
```json
"rules.css.selectors": {
  "enforceBEM": true,
  "requireScoping": true,
  "maxIdSelectors": 0,
  "maxTypeSelectors": 2,
  "noQualifyingType": true
}
```

---

### CATEGORY 29: Accessibility Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce accessibility requirements

**Settings**:
- `accessibility.images.enforceAltText` (line 402) = `true`
- `accessibility.interactive.enforceLabelText` (line 405) = `true`
- `accessibility.interactive.enforceButtonText` (line 406) = `true`
- `accessibility.interactive.enforceLinkText` (line 407) = `true`
- `accessibility.interactive.enforceRole` (line 408) = `true`
- `accessibility.interactive.enforceAriaLabel` (line 409) = `true`
- `accessibility.forms.enforceFieldIdentifiers` (line 412) = `true`
- `accessibility.forms.enforceLabelFor` (line 413) = `true`

**Recommended Central Rule**:
```json
"rules.accessibility.enforce": {
  "images": true,
  "interactive": true,
  "forms": true,
  "aria": true
}
```

---

### CATEGORY 30: Security CSP Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce Content Security Policy rules

**Settings**:
- `security.csp.enforceNoInlineEvents` (line 169) = `true`
- `security.csp.enforceNoInlineStyles` (line 170) = `true`
- `security.csp.enforceNoUnsafeEval` (line 171) = `true`
- `security.csp.checkHeaders` (line 172) = `true`

**Recommended Central Rule**:
```json
"rules.security.csp.enforce": {
  "inlineEvents": true,
  "inlineStyles": true,
  "unsafeEval": true,
  "headers": true
}
```

---

### CATEGORY 31: Code Quality Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce code quality rules

**Settings**:
- `codeQuality.comments.enforceNoNonFunctional` (line 998) = `true`
- `codeQuality.magicLiterals.enforceNoMagicNumbers` (line 1040) = `true`
- `codeQuality.magicLiterals.enforceNoMagicColors` (line 1041) = `true`
- `codeQuality.magicLiterals.enforceNoMagicMeasurements` (line 1042) = `true`
- `codeQuality.naming.enforceNoUnderscorePrefix` (line 1050) = `true`
- `codeQuality.naming.enforceNamingConventions` (line 1051) = `true`
- `codeQuality.serialization.enforceJsonStringifySpacing` (line 1143) = `true`
- `codeQuality.serialization.enforceJsonStringifyUtf8` (line 1144) = `true`

**Recommended Central Rule**:
```json
"rules.quality.enforce": {
  "comments": true,
  "magicLiterals": true,
  "naming": true,
  "serialization": true
}
```

---

### CATEGORY 32: Cross-Module Constants Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce constant sharing across modules

**Settings**:
- `crossModule.constants.enforceNoDuplicates` (line 419) = `true`
- `crossModule.constants.enforceNoAliases` (line 420) = `true`
- `crossModule.constants.trackImports` (line 421) = `true`
- `crossModule.constants.trackDiscovery` (line 422) = `true`

**Recommended Central Rule**:
```json
"rules.architecture.constants": {
  "noDuplicates": true,
  "noAliases": true,
  "trackUsage": true
}
```

---

### CATEGORY 33: Responsive CSS Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce responsive design patterns

**Settings**:
- `css.responsive.requireMobileSeparation` (line 344) = `true`
- `css.responsive.checkBreakpointOverlap` (line 345) = `true`

**Recommended Central Rule**:
```json
"rules.css.responsive": {
  "requireMobileSeparation": true,
  "checkBreakpointOverlap": true
}
```

---

### CATEGORY 34: CSS Variable Ordering Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce CSS variable organization rules

**Settings**:
- `css.variables.enforceOrdering` (line 285) = `true`

**Recommended Central Rule**:
```json
"rules.css.variables.enforceOrdering": true
```

---

### CATEGORY 35: Electron Enforcement
**Priority**: MEDIUM
**Semantic Purpose**: Enforce Electron-specific requirements

**Settings**:
- `electron.browserWindow.enforceIconProperty` (line 429) = `true`

**Recommended Central Rule**:
```json
"rules.electron.enforce": {
  "browserWindowIcon": true
}
```

---

### CATEGORY 36: Discovery Features
**Priority**: MEDIUM
**Semantic Purpose**: Enable discovery/tracking of code patterns

**Settings**:
- `discovery.cssVariables` (line 469) = `true`
- `discovery.baseClasses` (line 470) = `true`
- `discovery.constants` (line 471) = `true`
- `discovery.utilities` (line 472) = `true`
- `discovery.importPatterns` (line 473) = `true`

**Recommended Central Rule**:
```json
"rules.discovery.enabled": {
  "cssVariables": true,
  "baseClasses": true,
  "constants": true,
  "utilities": true,
  "importPatterns": true
}
```

---

### CATEGORY 37: Tooling Validation Features
**Priority**: MEDIUM
**Semantic Purpose**: Enable specific validation categories

**Settings**:
- `tooling.validateLint` (line 507) = `true`
- `tooling.validateFormat` (line 508) = `true`
- `tooling.validateTypes` (line 509) = `true`
- `tooling.validateDeps` (line 510) = `true`
- `tooling.validateUnusedExports` (line 511) = `true`

**Recommended Central Rule**:
```json
"rules.validation.categories": {
  "linting": true,
  "formatting": true,
  "types": true,
  "dependencies": true,
  "unusedExports": true
}
```

---

### CATEGORY 38: Display Preview Lengths
**Priority**: MEDIUM
**Semantic Purpose**: Control text preview lengths in output

**Settings**:
- `display.previewLengthShort` (line 1150) = `50`
- `display.previewLengthMedium` (line 1151) = `100`
- `display.previewLengthLong` (line 1152) = `200`

**Recommended Central Rule**:
```json
"rules.display.previewLength": {
  "short": 50,
  "medium": 100,
  "long": 200,
  "default": 100
}
```

---

### CATEGORY 39: Display Max Items
**Priority**: MEDIUM
**Semantic Purpose**: Control maximum items shown in lists/displays

**Settings**:
- `display.maxItemsSmall` (line 1153) = `10`
- `display.maxItemsMedium` (line 1154) = `20`
- `display.maxItemsLarge` (line 1155) = `50`
- `display.maxItemsXLarge` (line 1156) = `30` ⚠️ **CONFLICT - less than Large**
- `display.maxItemsHuge` (line 1157) = `50` ⚠️ **CONFLICT - same as Large**

**Recommended Central Rule**:
```json
"rules.display.maxItems": {
  "small": 10,
  "medium": 20,
  "large": 50,
  "default": 20
}
```

---

### CATEGORY 40: Display Sample and Top Counts
**Priority**: MEDIUM
**Semantic Purpose**: Control sample sizes and top N counts

**Settings**:
- `display.sampleSize` (line 1158) = `10`
- `display.firstViolationsCount` (line 1162) = `3`
- `display.topViolatorsCount` (line 1163) = `5`
- `display.violationsPerFileCount` (line 1164) = `2`

**Recommended Central Rule**:
```json
"rules.display.counts": {
  "sampleSize": 10,
  "firstViolations": 3,
  "topViolators": 5,
  "violationsPerFile": 2
}
```

---

### CATEGORY 41: Duplication Thresholds
**Priority**: MEDIUM
**Semantic Purpose**: Thresholds for detecting code duplication

**Settings**:
- `limits.minDuplicateBlockLines` (line 163) = `10`
- `limits.minDuplicateBlockLength` (line 164) = `50`
- `exclusions.duplicateOverlapThreshold` (line 440) = `2`

**Recommended Central Rule**:
```json
"rules.quality.duplication": {
  "minLines": 10,
  "minLength": 50,
  "overlapThreshold": 2
}
```

---

### CATEGORY 42: Severity Thresholds
**Priority**: MEDIUM
**Semantic Purpose**: Thresholds for severity classification

**Settings**:
- `css.severityThresholds.highDefinitionCount` (line 189) = `3`
- `css.severityThresholds.mediumDefinitionCount` (line 190) = `1`

**Recommended Central Rule**:
```json
"rules.severity.thresholds": {
  "high": 3,
  "medium": 1
}
```

---

### CATEGORY 43: Magic Literal Thresholds
**Priority**: MEDIUM
**Semantic Purpose**: Thresholds for flagging magic numbers

**Settings**:
- `codeQuality.magicLiterals.minimumDigitsToFlag` (line 1043) = `2`
- `css.glassEffect.alphaThresholdMedium` (line 264) = `0.1`

**Recommended Central Rule**:
```json
"rules.quality.magicLiterals": {
  "minimumDigits": 2,
  "alphaThreshold": 0.1
}
```

---

### CATEGORY 44: Priority and Weight Settings
**Priority**: MEDIUM
**Semantic Purpose**: Priority scores for ordering/ranking

**Settings**:
- `priority.defaultPriority` (line 463) = `100.0`
- `priority.scannerMultiplier` (line 464) = `10.0`
- `priority.npmCommandMultiplier` (line 465) = `1.0`
- `tools.java.pmd.minimumPriority` (line 895) = `3`

**Recommended Central Rule**:
```json
"rules.priority": {
  "default": 100,
  "scannerMultiplier": 10,
  "npmCommandMultiplier": 1
}
```

---

### CATEGORY 45: TypeScript Compiler Options
**Priority**: MEDIUM
**Semantic Purpose**: TypeScript-specific compiler flags

**Settings**:
- `tools.javascript.typescript.noEmit` (line 745) = `true`
- `tools.javascript.typescript.incremental` (line 746) = `true`
- `tools.javascript.typescript.skipLibCheck` (line 747) = `true`
- `tools.javascript.typescript.isolatedModules` (line 748) = `true`

**Recommended Central Rule**:
```json
"rules.typescript.compiler": {
  "noEmit": true,
  "incremental": true,
  "skipLibCheck": true,
  "isolatedModules": true
}
```

---

### CATEGORY 46: Linter Rule Presets
**Priority**: MEDIUM
**Semantic Purpose**: Enable predefined rule sets

**Settings**:
- `tools.javascript.biome.linter.rules.recommended` (line 763) = `true`
- `tools.javascript.oxlint.rules.recommended` (line 771) = `true`
- `tools.javascript.oxlint.rules.pedantic` (line 771) = `false`

**Recommended Central Rule**:
```json
"rules.linting.presets": {
  "recommended": true,
  "pedantic": false
}
```

---

### CATEGORY 47: Parser Behavior Flags
**Priority**: MEDIUM
**Semantic Purpose**: Control parser-specific behaviors

**Settings**:
- `tools.javascript.eslint.useFlatConfig` (line 710) = `true`
- `tools.python.mypy.ignoreImports` (line 815) = `false`
- `tools.python.pyright.reportMissingImports` (line 796) = `true`

**Recommended Central Rule**:
```json
"rules.parsing.behavior": {
  "flatConfig": true,
  "reportMissingImports": true,
  "ignoreImports": false
}
```

---

### CATEGORY 48: Formatter Simplification Flags
**Priority**: MEDIUM
**Semantic Purpose**: Enable code simplification during formatting

**Settings**:
- `tools.go.gofmt.simplify` (line 929) = `true`
- `tools.python.black.skipStringNormalization` (line 800) = `false`

**Recommended Central Rule**:
```json
"rules.formatting.simplify": {
  "enabled": true,
  "normalizeStrings": true
}
```

---

## 🟢 LOW PRIORITY CATEGORIES (14)

### CATEGORY 49: Language Versions (Already Centralized)
**Priority**: LOW
**Semantic Purpose**: Target language versions

**Settings**:
- `rules.versions.python` (line 540) = `"3.11"`
- `rules.versions.ecmascript` (line 541) = `2024`
- `rules.versions.typescript.target` (line 542) = `"ES2024"`
- `rules.versions.typescript.module` (line 542) = `"ESNext"`
- `rules.versions.rust` (line 543) = `"2024"`
- `rules.versions.cpp` (line 544) = `"c++20"`
- `rules.versions.java` (line 545) = `"17"`
- `rules.versions.go` (line 546) = `"1.21"`

**Status**: ✅ Already centralized properly in `rules.versions`

**Recommended Action**: No change needed

---

### CATEGORY 50: Strictness Modes (Partially Centralized)
**Priority**: LOW
**Semantic Purpose**: Type checking strictness

**Settings**:
- `rules.strictness.typeChecking` (line 549) = `"strict"`
- `rules.strictness.nullSafety` (line 550) = `true`
- `naming.derivedRules.strictMode` (line 121) = `false`
- `tools.python.prospector.strictness` (line 809) = `"medium"`
- `tools.css.stylelint.rules.strictValueColors` (line 847) = `true`

**Recommended Enhancement**:
```json
"rules.strictness": {
  "typeChecking": "strict",
  "nullSafety": true,
  "naming": false,
  "codeQuality": "medium",
  "security": "high",
  "designTokens": true
}
```

---

### CATEGORY 51: Skip Hidden Directories
**Priority**: LOW
**Semantic Purpose**: Whether to skip hidden directories

**Settings**:
- `scanning.skipHiddenDirectories` (line 1180) = `true`

**Recommended Central Rule**:
```json
"rules.scanning.skipHidden": true
```

---

### CATEGORY 52: Test File Suffixes
**Priority**: LOW
**Semantic Purpose**: Patterns to identify test files

**Settings**:
- `scanning.testFileSuffixes` (line 1183) = `[".test", ".spec"]`

**Recommended Central Rule**:
```json
"rules.patterns.testFiles": [".test", ".spec"]
```

---

### CATEGORY 53: Code File Extensions
**Priority**: LOW
**Semantic Purpose**: File extensions to scan as code

**Settings**:
- `scanning.codeExtensions` (line 1185) = `[".ts", ".tsx", ".js", ".jsx", ".mjs", ".cjs", ".py", ".java", ".kt", ".go", ".rs", ".rb", ".php", ".cs", ".swift", ".c", ".cpp", ".h", ".hpp"]`

**Recommended Central Rule**:
```json
"rules.patterns.codeExtensions": [".ts", ".tsx", ".js", ".jsx", ".mjs", ".cjs", ".py", ".java", ".kt", ".go", ".rs", ".rb", ".php", ".cs", ".swift", ".c", ".cpp", ".h", ".hpp"]
```

---

### CATEGORY 54: Style File Extensions
**Priority**: LOW
**Semantic Purpose**: File extensions to scan as stylesheets

**Settings**:
- `scanning.styleExtensions` (line 1206) = `[".css", ".scss", ".sass", ".less", ".styl"]`

**Recommended Central Rule**:
```json
"rules.patterns.styleExtensions": [".css", ".scss", ".sass", ".less", ".styl"]
```

---

### CATEGORY 55: Data File Extensions
**Priority**: LOW
**Semantic Purpose**: File extensions to scan as data files

**Settings**:
- `scanning.dataExtensions` (line 1207) = `[".json", ".yaml", ".yml"]`

**Recommended Central Rule**:
```json
"rules.patterns.dataExtensions": [".json", ".yaml", ".yml"]
```

---

### CATEGORY 56: Markup File Extensions
**Priority**: LOW
**Semantic Purpose**: File extensions to scan as markup

**Settings**:
- `scanning.markupExtensions` (line 1209) = `[".html", ".xml", ".svg"]`

**Recommended Central Rule**:
```json
"rules.patterns.markupExtensions": [".html", ".xml", ".svg"]
```

---

### CATEGORY 57: Import Depth Threshold
**Priority**: LOW
**Semantic Purpose**: Maximum depth for import paths before flagging

**Settings**:
- `syntax.importPatterns.deepImportThreshold` (line 1280) = `3`

**Recommended Central Rule**:
```json
"rules.imports.maxDepth": 3
```

---

### CATEGORY 58: Logging Verbosity
**Priority**: LOW
**Semantic Purpose**: Control log output verbosity

**Settings**:
- `logging.verbosity.silent` (line 1391) = `0`
- `logging.verbosity.error` (line 1392) = `1`
- `logging.verbosity.warn` (line 1393) = `2`
- `logging.verbosity.info` (line 1394) = `3`
- `logging.verbosity.debug` (line 1395) = `4`
- `logging.verbosity.trace` (line 1396) = `5`
- `logging.currentLevel` (line 1398) = `"info"`

**Recommended Central Rule**:
```json
"rules.logging.level": "info"
```

---

### CATEGORY 59: Display History Settings
**Priority**: LOW
**Semantic Purpose**: Control history tracking limits

**Settings**:
- `display.historyMaxSize` (line 1160) = `100`
- `display.historyKeysToShow` (line 1161) = `10`
- `display.maxZIndex` (line 1159) = `1000`

**Recommended Central Rule**:
```json
"rules.display.history": {
  "maxSize": 100,
  "keysToShow": 10
}
```

---

### CATEGORY 60: Naming Derived Rules
**Priority**: LOW
**Semantic Purpose**: Automatic naming rule derivations

**Settings**:
- `naming.derivedRules.autoDetectPlural` (line 119) = `true`
- `naming.derivedRules.fileToClassName` (line 120) = `true`
- `naming.derivedRules.strictMode` (line 121) = `false`

**Recommended Central Rule**:
```json
"rules.naming.derived": {
  "autoDetectPlural": true,
  "fileToClassName": true,
  "strictMode": false
}
```

---

### CATEGORY 61: JSON Serialization Settings
**Priority**: LOW
**Semantic Purpose**: Control JSON.stringify behavior

**Settings**:
- `codeQuality.serialization.enforceJsonStringifySpacing` (line 1143) = `true`
- `codeQuality.serialization.enforceJsonStringifyUtf8` (line 1144) = `true`
- `codeQuality.serialization.requiredSpacing` (line 1145) = `4`

**Recommended Central Rule**:
```json
"rules.serialization.json": {
  "enforceSpacing": true,
  "enforceUtf8": true,
  "spacing": 4
}
```

---

### CATEGORY 62: Tool-Specific Analysis Flags
**Priority**: LOW
**Semantic Purpose**: Enable advanced analysis features per tool

**Settings**:
- `tools.javascript.oxlint.typeAwareLinting` (line 770) = `true`
- `tools.java.errorProne.disableWarningsInGeneratedCode` (line 904) = `true`
- `tools.rust.clippy.allTargets` (line 948) = `true`
- `tools.rust.clippy.allFeatures` (line 949) = `true`
- `tools.java.spotbugs.effort` (line 899) = `"max"`
- `tools.go.golangciLint.timeout` (line 921) = `"5m"`
- `tools.multiLang.preCommit.failFast` (line 984) = `false`

**Recommended Central Rule**:
```json
"rules.analysis": {
  "typeAware": true,
  "ignoreGenerated": true,
  "allTargets": true,
  "allFeatures": true,
  "effort": "max",
  "timeout": "5m",
  "failFast": false
}
```

---

## IMPLEMENTATION RECOMMENDATIONS

### Immediate Actions (HIGH Priority):

1. **DELETE the entire `tools.codeStyle` section (lines 565-579)**
   - This is a complete duplicate of `rules.formatting` (lines 524-537)
   - Affects 13 settings across categories 3-15

2. **Consolidate tool enabled flags (Category 1)**
   - Create `rules.tooling.enabled` with semantic groups
   - Update all 37 tool-specific enabled flags to reference this

3. **Consolidate auto-fix flags (Category 2)**
   - Create `rules.autoFix`
   - Update all 7 auto-fix flags to reference this

4. **Consolidate depth/complexity settings (Category 16)**
   - Create `rules.complexity.maxDepth`
   - Update all 4 CSS depth-related settings

5. **Consolidate precision settings (Category 17)**
   - Create `rules.formatting.precision`
   - Update both precision-related settings

6. **Consolidate import organization settings (Category 18)**
   - Create `rules.imports.organize`
   - Update all 4 import sorting flags

### Medium-Term Actions (MEDIUM Priority):

- Consolidate all severity levels into `rules.severity.*` hierarchy (Categories 19-22)
- Consolidate scanning context settings (Category 23)
- Consolidate all exclusion patterns (Categories 24-26)
- Consolidate all enforcement flags (Categories 27-37)
- Consolidate display settings (Categories 38-40)
- Consolidate threshold settings (Categories 41-44)
- Consolidate TypeScript/parser settings (Categories 45-47)

### Long-Term Actions (LOW Priority):

- Move tool-specific rule configurations to central locations (Categories 49-62)
- Create transformation mappings for tool-specific formats

### Expected Results:

- **Current Size**: 1686 lines
- **Target Size**: 800-900 lines
- **Reduction**: ~47%
- **Duplicates Removed**: 180+ individual settings
- **Categories Consolidated**: 62 semantic groups

---

## VERIFICATION CHECKLIST

After implementing consolidation:

- [ ] All HIGH priority duplicates removed (Categories 1-18)
- [ ] `tools.codeStyle` section completely deleted
- [ ] All severity settings consolidated into `rules.severity.*`
- [ ] All enforcement flags consolidated into `rules.*.enforce`
- [ ] All exclusion patterns consolidated into `rules.exclusions.*`
- [ ] All display settings consolidated into `rules.display.*`
- [ ] All tooling flags consolidated into `rules.tooling.*`
- [ ] Tool mappings updated to reference new central rules
- [ ] Settings file reduced by ~47%
- [ ] All tests pass with new structure
- [ ] Documentation updated to reflect new rule paths

---

**Report Generated**: 2025-12-31
**Analysis Tool**: Claude Code Semantic Duplicate Detector
**Settings File**: root/codebase-validation/settings.json
**Total Lines Analyzed**: 1686
**Total Categories**: 62
**Total Settings to Consolidate**: 180+
