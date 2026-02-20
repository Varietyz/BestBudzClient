# law-4-enforcer

Execute LAW 4 Enforcer agent to detect and remediate security violations

## Usage

Analyzes the codebase for LAW 4 violations (Security as Base-Inherited Capability) and generates comprehensive security reports with remediation strategies.

## What It Does

This agent forensically analyzes the codebase to detect security violations where code bypasses architectural security measures:

**Security Violation Categories:**
- **DOM Bypasses**: Direct `document.createElement()` usage instead of `DOM.createElement()`
- **XSS Vulnerabilities**: `innerHTML` assignments, `eval()` usage, inline event handlers
- **SQL Injection Risks**: String concatenation in SQL queries instead of parameterization
- **Path Traversal**: Unsanitized file paths from user input
- **Missing Security Inheritance**: Components that reimplement security instead of inheriting from base classes

**Analysis Capabilities:**
- Detects all instances of security bypasses with file:line:code evidence
- Traces sanitization chains from user input sources to dangerous sinks
- Analyzes security architecture (DOM factory, base classes providing security)
- Categorizes violations by severity (CRITICAL/HIGH/MEDIUM)
- Calculates security score (0-100) and LAW 4 compliance status
- Generates migration strategies for each violation type

**Output Artifacts:**
- `LAW-4-ENFORCEMENT-REPORT.md` - Executive summary with security score
- `03-LAW-4-VIOLATIONS.json` - All violations categorized by type
- `LAW-4-DOM-BYPASS-INVENTORY.json` - Direct DOM manipulation instances
- `LAW-4-XSS-RISKS.json` - XSS vulnerability instances
- `LAW-4-SQL-INJECTION-RISKS.json` - SQL injection risk instances
- `LAW-4-PATH-TRAVERSAL-RISKS.json` - Path traversal vulnerability instances
- `LAW-4-MISSING-INHERITANCE.json` - Components missing security inheritance
- `04-sanitization-chains.json` - User input → sink flow analysis
- `05-LAW-4-REMEDIATION-PLAN.json` - Migration strategies with effort estimates
- `02-security-architecture.json` - Security-providing base classes and factories

## Target Scope

**Primary Focus:**
- `root/archlab-ide/src/**/*.ts` - All TypeScript files
- `root/archlab-ide/src/renderer/**/*.ts` - Renderer process (UI layer)
- `root/archlab-ide/src/renderer/components/**/*.ts` - Component classes

**Security Patterns Detected:**

**CRITICAL Violations:**
- `document.createElement()` - DOM bypass (should use `DOM.createElement()`)
- `innerHTML = ...` - XSS risk (should use `textContent` or sanitize)
- `outerHTML = ...` - XSS risk
- `eval(...)` - Code injection
- `new Function(...)` - Code injection
- `onclick="..."` - Inline handler (CSP violation)
- `onerror="..."` - Inline handler (CSP violation)
- `insertAdjacentHTML(...)` - DOM bypass
- `query('... + userInput')` - SQL injection
- `query(\`... \${userInput}\`)` - SQL injection

**HIGH Violations:**
- `fs.readFile(... + userInput)` - Path traversal
- `path.join(... req.body ...)` - Path traversal
- Components not extending `BaseComponent` but using security methods

**Compliant Patterns:**
- `DOM.createElement('div', { ... })` - Secure element creation
- `element.textContent = userInput` - Safe text insertion
- `DOMPurify.sanitize(html)` - Sanitized HTML
- `query('SELECT * FROM t WHERE id = ?', [id])` - Parameterized query
- `class MyComponent extends BaseComponent` - Inherited security

## Inherited Skepticism

This agent includes:
- **Tool Calibration Phase**: Validates Grep accuracy for security pattern detection
- **Claim Verification Tracking**: Maintains `verified_claims` and `refuted_claims` arrays
- **Verification Rate Threshold**: Aborts if verification fails
- **Safe Iteration Loop Patterns**: Bounded loops with `max_iterations`, `goal_achieved` conditions
- **Trust Anchor**: Minimal assumptions, maximum skepticism, empirical validation required

## Algorithmic Patterns

**Embedded Algorithms:**
- **Production-System-Cycle**: Security rules as productions, code patterns as working memory
- **Dependency-Directed-Backtracking**: Traces user input → sink flows, backtracks on missing sanitization
- **Frame-Based-Reasoning**: Security base classes as frames with security method slots
- **Means-Ends-Analysis**: Current insecure state → secure state via migration operators

## Security Score Calculation

```
security_score = 100
security_score -= (dom_bypasses * 10)        # -10 per DOM bypass
security_score -= (xss_risks * 10)           # -10 per XSS risk
security_score -= (sql_injection_risks * 10) # -10 per SQL injection
security_score -= (path_traversal_risks * 5) # -5 per path traversal
security_score -= (missing_inheritance * 5)  # -5 per missing inheritance
security_score = max(0, security_score)

LAW 4 Compliance: security_score >= 80 ? PASS : FAIL
```

## Remediation Priority

**P0 - CRITICAL** (Fix Immediately):
1. DOM bypasses - Replace `document.createElement()` with `DOM.createElement()`
2. XSS risks - Replace `innerHTML` with `textContent` or sanitize with DOMPurify
3. SQL injection - Replace string concatenation with parameterized queries

**P1 - HIGH** (Fix Within Sprint):
4. Path traversal - Add `path.normalize()` and whitelist validation
5. Missing inheritance - Extend `BaseComponent` to inherit security

## Example Violations

**DOM Bypass:**
```typescript
// ❌ VIOLATION
const div = document.createElement('div');
div.style.color = 'red';

// ✅ COMPLIANT
const div = await DOM.createElement('div', {
    style: { color: 'red' },
    entity: 'my-div'
});
```

**XSS Risk:**
```typescript
// ❌ VIOLATION
element.innerHTML = userInput;

// ✅ COMPLIANT
element.textContent = userInput;
// OR
element.innerHTML = DOMPurify.sanitize(userInput);
```

**SQL Injection:**
```typescript
// ❌ VIOLATION
db.query('SELECT * FROM users WHERE id = ' + userId);

// ✅ COMPLIANT
db.query('SELECT * FROM users WHERE id = ?', [userId]);
```

**Missing Inheritance:**
```typescript
// ❌ VIOLATION
class MyComponent {
    createElement() {
        return DOM.createElement('div', {}); // Reimplementing security
    }
}

// ✅ COMPLIANT
class MyComponent extends BaseComponent {
    // Inherits security capabilities from base
}
```

## Workflow

1. **Discovery**: Globs all TypeScript files in target scope
2. **Architecture Analysis**: Identifies security-providing base classes (DOM factory, BaseComponent)
3. **Violation Detection**: Searches for security bypass patterns with Production-System-Cycle
4. **Sanitization Tracing**: Traces user input → sink flows with Dependency-Directed-Backtracking
5. **Remediation Planning**: Generates migration strategies using Means-Ends-Analysis
6. **Verification**: Calculates security score and LAW 4 compliance
7. **Reporting**: Delivers comprehensive report with prioritized remediation plan

## Success Criteria

✅ All TypeScript files analyzed
✅ Security architecture documented
✅ All violations detected and categorized
✅ Sanitization chains traced
✅ Remediation plan generated
✅ Security score calculated
✅ LAW 4 compliance determined
✅ All reports written to workspace

## Next Steps After Execution

1. Review `LAW-4-ENFORCEMENT-REPORT.md` for security score and violation summary
2. Examine individual violation reports in `violations/` directory
3. Prioritize CRITICAL (P0) violations for immediate remediation
4. Follow migration strategies in `05-LAW-4-REMEDIATION-PLAN.json`
5. Re-run agent after fixes to verify compliance
6. Achieve security_score >= 80 for LAW 4 compliance

$ARGUMENTS
