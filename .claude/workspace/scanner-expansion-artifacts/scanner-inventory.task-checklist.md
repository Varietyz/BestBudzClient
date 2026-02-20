# Scanner Inventory

## Workflow: scanner-expansion-workflow
## Phase: Architecture Validation Complete
## Last Updated: 2026-01-01 (Agent 2)

---

## Scanner Categories (23 Total)

### SOLID Violations (6) - Directory: `quality/code/`
- [ ] soc-violations-scanner.js
- [ ] srp-violations-scanner.js
- [ ] ocp-violations-scanner.js
- [ ] lsp-violations-scanner.js
- [ ] isp-violations-scanner.js
- [ ] dip-violations-scanner.js

### Anti-Patterns (4) - Directory: `quality/code/`
- [ ] production-anti-patterns-scanner.js
- [ ] cyclomatic-complexity-scanner.js
- [ ] cognitive-complexity-scanner.js
- [ ] dead-code-scanner.js

### Architecture (3) - Directory: `quality/code/` or `quality/architecture/` (new)
- [ ] layer-violations-scanner.js
- [ ] circular-dependencies-scanner.js
- [ ] event-driven-violations-scanner.js

### Metrics (2) - Directory: `quality/code/`
- [ ] coupling-metrics-scanner.js
- [ ] cohesion-metrics-scanner.js

### Code Smells (3) - Directory: `quality/code/`
- [ ] long-method-scanner.js
- [ ] data-clump-scanner.js
- [ ] speculative-generality-scanner.js

### Security (2) - Directory: `quality/security/`
- [ ] injection-vulnerabilities-scanner.js
- [ ] security-inheritance-scanner.js

### Performance (2) - Directory: `quality/code/` or `quality/performance/` (new)
- [ ] n-plus-one-queries-scanner.js
- [ ] memory-leak-patterns-scanner.js

### Documentation (2) - Directory: `quality/code/`
- [ ] api-documentation-scanner.js
- [ ] test-coverage-gaps-scanner.js

---

## Priority Matrix

### High Priority (12) - Agents 3-4

| Scanner | Agent | Directory | Status |
|---------|-------|-----------|--------|
| soc-violations | 3 | quality/code/ | Pending |
| srp-violations | 3 | quality/code/ | Pending |
| ocp-violations | 3 | quality/code/ | Pending |
| dip-violations | 3 | quality/code/ | Pending |
| production-anti-patterns | 3 | quality/code/ | Pending |
| cyclomatic-complexity | 3 | quality/code/ | Pending |
| cognitive-complexity | 4 | quality/code/ | Pending |
| layer-violations | 4 | quality/code/ or quality/architecture/ | Pending |
| circular-dependencies | 4 | quality/code/ | Pending |
| coupling-metrics | 4 | quality/code/ | Pending |
| long-method | 6 | quality/code/ | Pending |
| injection-vulnerabilities | 5 | quality/security/ | Pending |

### Medium Priority (9) - Agents 5-6

| Scanner | Agent | Directory | Status |
|---------|-------|-----------|--------|
| lsp-violations | 6 | quality/code/ | Pending |
| isp-violations | 6 | quality/code/ | Pending |
| dead-code | 6 | quality/code/ | Pending |
| event-driven-violations | 6 | quality/code/ or quality/architecture/ | Pending |
| cohesion-metrics | 6 | quality/code/ | Pending |
| data-clump | 6 | quality/code/ | Pending |
| security-inheritance | 5 | quality/security/ | Pending |
| memory-leak-patterns | 5 | quality/code/ or quality/performance/ | Pending |
| n-plus-one-queries | 5 | quality/code/ or quality/performance/ | Pending |

### Low Priority (3) - Agent 6

| Scanner | Agent | Directory | Status |
|---------|-------|-----------|--------|
| speculative-generality | 6 | quality/code/ | Pending |
| api-documentation | 6 | quality/code/ | Pending |
| test-coverage-gaps | 6 | quality/code/ | Pending |

---

## Implementation Requirements

### Per Scanner Checklist

For each scanner, the implementing agent must:

1. **Create Scanner File** in designated directory
   - Follow template from architecture-decisions.audit-report.md
   - Extend BaseScanner (or specialized base class)
   - Implement required methods: getSupportedLanguages(), analyzeContentPatterns(), getName()
   - Include Perceptual-Loop header comment

2. **Add to Settings** (`root/codebase-validation/settings.json`)
   - Add entry to `enabled.scanners` flat list (lines 560-631)
   - Format: `"{scanner-name}": true`

3. **Update Language Specs** (`root/codebase-validation/core/settings/languages/specs/`)
   - Add scanner name to `applicableScanners` array in relevant language specs
   - Language-agnostic scanners (getSupportedLanguages() returns null) still need explicit addition
   - Typical languages: javascript.js, python.js, java.js, go.js, rust.js, cpp.js, etc.

4. **Create Registry Key** (auto-handled by RegistryBuilder)
   - RegistryBuilder auto-registers paths on first use
   - No manual intervention needed

5. **Optional: Create Validator** (`root/codebase-validation/validators/quality/code-quality/violations/`)
   - Named `{scanner-name}-validator.js`
   - Validates scanner output against expected schema

6. **Optional: Create Auto-Fixer** (`root/codebase-validation/auto-fix/`)
   - Named `{scanner-name}-auto-fixer.js`
   - Implements automated fixes for violations

---

## Pattern Requirements by Scanner Type

### SOLID Violations
- **Language Support:** Language-agnostic (getSupportedLanguages() returns null)
- **Violation Patterns:** Class-level analysis, method-level analysis
- **Registry Key:** `{principle}-violations` (e.g., "soc-violations", "srp-violations")
- **Settings Path:** `codeQuality.solid.enforce{Principle}` (e.g., enforceSOC, enforceSRP)

### Anti-Patterns
- **Language Support:** Language-agnostic or language-specific depending on pattern
- **Violation Patterns:** Complexity metrics, code structure patterns
- **Registry Key:** `{pattern-name}` (e.g., "cyclomatic-complexity", "production-anti-patterns")
- **Settings Path:** `codeQuality.antiPatterns.detect{Pattern}`

### Architecture Violations
- **Language Support:** Language-agnostic
- **Violation Patterns:** Import analysis, dependency graph traversal
- **Registry Key:** `{violation-type}` (e.g., "layer-violations", "circular-dependencies")
- **Settings Path:** `codeQuality.architecture.enforce{Rule}`

### Code Metrics
- **Language Support:** Language-agnostic
- **Violation Patterns:** Calculate metrics, threshold violations
- **Registry Key:** `{metric-name}-metrics` (e.g., "coupling-metrics", "cohesion-metrics")
- **Settings Path:** `codeQuality.metrics.track{Metric}`

### Security
- **Language Support:** Often JavaScript-specific, some language-agnostic
- **Violation Patterns:** String pattern matching, AST analysis for injection points
- **Registry Key:** `{vulnerability-type}` (e.g., "injection-vulnerabilities")
- **Settings Path:** `security.enforce{Rule}` or `codeQuality.security.detect{Vulnerability}`

---

## Implementation Progress

- Total Scanners: 23
- Completed: 0
- In Progress: 0
- Pending: 23
- Completion: 0%

---

## Validation Checklist (Post-Implementation)

After all scanners are created, verify:

- [ ] All 23 scanners exist in correct directories
- [ ] All scanners added to settings.json enabled.scanners list
- [ ] All applicable language specs updated with scanner names
- [ ] All scanners extend BaseScanner or specialized base class
- [ ] All scanners implement getSupportedLanguages(), analyzeContentPatterns(), getName()
- [ ] All scanners use registry.build() for output
- [ ] All scanners include Perceptual-Loop documentation header
- [ ] No compilation errors when running scanners
- [ ] Registry files created successfully in .registry/
