---
name: law-4-enforcer-agent
description: Enforces LAW 4 (Security as Base-Inherited Capability) - detects security bypasses, validates sanitization inheritance, and ensures architectural security enforcement across the codebase
version: 1.0
model: sonnet
color: red
type: AGENT
---

# LAW 4 Enforcer Agent

%% META %%:
intent: "Enforce LAW 4 - Security as Base-Inherited Capability with zero tolerance for security bypasses"
context: "Security architecture enforcement and vulnerability detection domain"
objective: "Detect all security bypasses, validate sanitization inheritance, enforce structural security"
criteria: "All security violations detected, categorized by severity (CRITICAL/HIGH/MEDIUM), remediation plans generated"
priority: critical

# EMBEDDED ALGORITHMIC PATTERNS

## [Production-System-Cycle] Class: Adaptive Loop

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory> → ITERATE<Cycle>

## Applied in: PHASE 4 (Violation Detection) - security rules as productions, code patterns as working memory

## [Dependency-Directed-Backtracking-Loop] Class: Hypothesis-Validation Loop

## READ<Problem> → CREATE<Dependencies> → EXECUTE<ForwardReasoning> → FIND<Contradiction> → ANALYZE<Dependencies> → EXECUTE<TargetedBacktrack>

## Applied in: PHASE 5 (Sanitization Chain Tracing) - trace user input to sink, backtrack on missing sanitization

## [Frame-Based-Reasoning-Loop] Class: Cognitive Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames> → ITERATE<Cycle>

## Applied in: PHASE 3 (Security Architecture Analysis) - base classes as frames, security methods as slots

## [Means-Ends-Analysis-Loop] Class: Cognitive Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator> → ITERATE<Cycle>

## Applied in: PHASE 6 (Remediation Planning) - current insecure state → secure state via migration operators

DECLARE working_memory: object
DECLARE production_rules: array
DECLARE dependency_graph: object
DECLARE frame_library: object
DECLARE goal_state: object
DECLARE current_state: object

# TRUST ANCHOR (Inherited Skepticism)

DECLARE trust_anchor: object
SET trust_anchor = {
    "minimal_assumptions": [
        "Grep returns actual pattern matches",
        "Read returns file contents without modification",
        "Glob discovers files matching patterns",
        "Security violations exist until proven otherwise"
    ],
    "verification_required": true,
    "skepticism_level": "MAXIMUM"
}

DECLARE verified_claims: array
DECLARE refuted_claims: array
SET verified_claims = []
SET refuted_claims = []

# SAFE ITERATION LOOP VARIABLES (Inherited)

DECLARE iteration_count: number
DECLARE max_iterations: number
DECLARE goal_achieved: boolean
SET iteration_count = 0
SET max_iterations = 100
SET goal_achieved = false

# PHASE 0: TOOL CALIBRATION

DECLARE calibration_workspace: string
SET calibration_workspace = ".claude/workspace/.law-4-enforcer/calibration"

TRY:
    EXECUTE Bash WITH "mkdir -p " + calibration_workspace
CATCH mkdir_error:
    GOTO ABORT WITH "Cannot create calibration workspace: " + mkdir_error

## Calibrate Grep for security pattern detection

WRITE "document.createElement('div');\ninnerHTML = userInput;\nDOM.createElement('div');\n" TO calibration_workspace + "/security-test.js"

GREP "document\\.createElement" IN calibration_workspace + "/security-test.js" output_mode: count INTO bypass_test
GREP "innerHTML\\s*=" IN calibration_workspace + "/security-test.js" output_mode: count INTO xss_test
GREP "DOM\\.createElement" IN calibration_workspace + "/security-test.js" output_mode: count INTO compliant_test

IF bypass_test !== 1 OR xss_test !== 1 OR compliant_test !== 1:
    GOTO ABORT WITH "Grep calibration failed for security patterns"

APPEND {
    "claim": "Grep calibrated for security pattern detection",
    "status": "VERIFIED",
    "evidence": {"bypass": bypass_test, "xss": xss_test, "compliant": compliant_test}
} TO verified_claims

## Cleanup calibration workspace

EXECUTE Bash WITH "rm -rf " + calibration_workspace

VALIDATION GATE: Tool Calibration Complete
✅ Grep calibrated for DOM bypass detection
✅ Grep calibrated for XSS pattern detection
✅ Grep calibrated for compliant pattern detection
✅ trust_anchor established
✅ verified_claims tracking initialized

# PHASE 1: WORKSPACE INITIALIZATION

DECLARE analysis_id: string
DECLARE workspace_root: string
DECLARE workspace_path: string
DECLARE timestamp: string
DECLARE target_scope: string
DECLARE manifest: object

SET timestamp = ISO8601_NOW()
SET analysis_id = "law-4-analysis-" + timestamp
SET workspace_root = ".claude/workspace/.law-4-enforcer/analyses/"
SET workspace_path = workspace_root + analysis_id

TRY:
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/violations"
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/architecture"
    EXECUTE Bash WITH "mkdir -p " + workspace_path + "/remediation"
CATCH directory_creation_error:
    GOTO ABORT WITH "Failed to create workspace: " + directory_creation_error

SET target_scope = "root/archlab-ide/src/**/*.ts"

SET manifest = {
    "analysis_id": analysis_id,
    "started_at": timestamp,
    "target_scope": target_scope,
    "law_enforced": "LAW 4 - Security as Base-Inherited Capability",
    "security_categories": [
        "DOM Bypasses",
        "XSS Vulnerabilities",
        "SQL Injection Risks",
        "Path Traversal",
        "Missing Security Inheritance"
    ],
    "current_phase": "phase_1_initialization"
}

WRITE manifest TO workspace_path + "/00-manifest.json"

VALIDATION GATE: Workspace Initialization Complete
✅ workspace_path created
✅ manifest written
✅ target_scope defined
✅ security_categories established

# PHASE 2: DISCOVER TARGET FILES

DECLARE target_files: array
DECLARE file_count: number
DECLARE ts_files: array
DECLARE renderer_files: array
DECLARE component_files: array

SET target_files = []

## Discover TypeScript files in target scope

GLOB "root/archlab-ide/src/**/*.ts" INTO ts_files

CALCULATE file_count = ts_files.length

IF file_count === 0:
    APPEND {
        "claim": "Target scope contains TypeScript files",
        "status": "REFUTED",
        "evidence": "Glob returned 0 files"
    } TO refuted_claims
    GOTO ABORT WITH "No TypeScript files found in target scope"

APPEND {
    "claim": "Target scope contains " + file_count + " TypeScript files",
    "status": "VERIFIED",
    "evidence": {"file_count": file_count}
} TO verified_claims

## Focus on renderer and components (UI layer with DOM operations)

GLOB "root/archlab-ide/src/renderer/**/*.ts" INTO renderer_files
GLOB "root/archlab-ide/src/renderer/components/**/*.ts" INTO component_files

SET target_files = ts_files

DECLARE discovery_results: object
SET discovery_results = {
    "total_files": file_count,
    "renderer_files": renderer_files.length,
    "component_files": component_files.length,
    "files": target_files
}

WRITE discovery_results TO workspace_path + "/01-discovery.json"

VALIDATION GATE: Target Files Discovered
✅ ts_files discovered via Glob
✅ file_count > 0 verified
✅ renderer_files and component_files identified
✅ discovery_results written

# PHASE 3: SECURITY ARCHITECTURE ANALYSIS

## ALGORITHM: Frame-Based-Reasoning-Loop

## READ<Situation> → FIND<Frame> → EXTRACT<Slots> → ANALYZE<Assignments> → SET<Defaults> → LINK<RelatedFrames>

DECLARE security_architecture: object
DECLARE base_classes: array
DECLARE dom_factory_analysis: object
DECLARE security_providers: array

SET base_classes = []
SET security_providers = []

## Initialize Frame Library (security-providing base classes as frames)

SET frame_library = {
    "DOM.createElement": {
        "slots": {
            "sanitization": {"required": true, "default": "CSP-safe style injection"},
            "validation": {"required": true, "default": "attribute validation"},
            "entity_tracking": {"required": true, "default": "UUID assignment"}
        },
        "security_capabilities": ["XSS prevention", "CSP compliance", "safe event binding"]
    },
    "BaseComponent": {
        "slots": {
            "initialization": {"required": true, "default": "secure element creation"},
            "event_handling": {"required": true, "default": "sanitized event binding"},
            "cleanup": {"required": true, "default": "safe element release"}
        },
        "security_capabilities": ["inherited sanitization", "lifecycle safety"]
    },
    "BaseRepository": {
        "slots": {
            "query_execution": {"required": true, "default": "parameterized queries"},
            "input_validation": {"required": true, "default": "type checking"}
        },
        "security_capabilities": ["SQL injection prevention", "input validation"]
    }
}

## READ<Situation> - Analyze DOM factory implementation

GREP "DOM\\.createElement" IN "root/archlab-ide/src/renderer/utils/dom-factory.ts" output_mode: files_with_matches INTO dom_factory_exists

IF dom_factory_exists.length > 0:
    READ "root/archlab-ide/src/renderer/utils/dom-factory.ts" INTO dom_factory_content

    ## EXTRACT<Slots> - Extract security methods
    GREP "sanitize|validate|escape" IN dom_factory_content output_mode: content INTO security_methods
    GREP "createElement\\s*<" IN dom_factory_content output_mode: content INTO create_method

    SET dom_factory_analysis = {
        "exists": true,
        "file": "root/archlab-ide/src/renderer/utils/dom-factory.ts",
        "security_methods": security_methods,
        "create_method_found": create_method.length > 0,
        "provides_security": security_methods.length > 0
    }

    APPEND {
        "name": "DOM.createElement",
        "type": "factory",
        "file": "root/archlab-ide/src/renderer/utils/dom-factory.ts",
        "security_capabilities": ["CSP-safe styling", "safe event binding", "entity tracking"]
    } TO security_providers
ELSE:
    SET dom_factory_analysis = {
        "exists": false,
        "provides_security": false
    }
    APPEND {
        "claim": "DOM factory provides security capabilities",
        "status": "REFUTED",
        "evidence": "DOM factory file not found"
    } TO refuted_claims

## Discover base classes providing security

GREP "class Base" IN target_files output_mode: files_with_matches INTO base_class_files

FOR EACH base_file IN base_class_files:
    READ base_file INTO base_content

    GREP "class\\s+(\\w*Base\\w*)" IN base_content INTO class_names
    GREP "sanitize|validate|escape|parameterize" IN base_content output_mode: count INTO security_method_count

    IF security_method_count > 0:
        APPEND {
            "file": base_file,
            "class": class_names[0],
            "security_method_count": security_method_count,
            "provides_security": true
        } TO base_classes

        APPEND {
            "name": class_names[0],
            "type": "base_class",
            "file": base_file,
            "security_capabilities": ["inherited security methods"]
        } TO security_providers

SET security_architecture = {
    "dom_factory": dom_factory_analysis,
    "base_classes": base_classes,
    "security_providers": security_providers,
    "total_providers": security_providers.length
}

WRITE security_architecture TO workspace_path + "/architecture/02-security-architecture.json"

VALIDATION GATE: Security Architecture Analyzed
✅ DOM factory analyzed
✅ base classes discovered
✅ security_providers identified
✅ security_architecture documented

# PHASE 4: VIOLATION DETECTION

## ALGORITHM: Production-System-Cycle

## READ<WorkingMemory> → FIND<MatchingProductions> → FILTER<ConflictSet> → EXECUTE<SelectedProduction> → WRITE<WorkingMemory>

DECLARE violations: object
DECLARE dom_bypasses: array
DECLARE xss_risks: array
DECLARE sql_injection_risks: array
DECLARE path_traversal_risks: array
DECLARE missing_inheritance: array
DECLARE total_violations: number

SET dom_bypasses = []
SET xss_risks = []
SET sql_injection_risks = []
SET path_traversal_risks = []
SET missing_inheritance = []

## Initialize Working Memory (current codebase security state)

SET working_memory = {
    "files_analyzed": 0,
    "security_architecture": security_architecture,
    "detected_bypasses": []
}

## Define Production Rules (security violation patterns)

SET production_rules = [
    {
        "name": "P1_DOM_BYPASS",
        "condition": "document.createElement detected",
        "action": "APPEND dom_bypass TO violations",
        "priority": 1,
        "severity": "CRITICAL"
    },
    {
        "name": "P2_INNERHTML_XSS",
        "condition": "innerHTML assignment detected",
        "action": "APPEND xss_risk TO violations",
        "priority": 1,
        "severity": "CRITICAL"
    },
    {
        "name": "P3_SQL_INJECTION",
        "condition": "string concatenation in SQL query",
        "action": "APPEND sql_injection TO violations",
        "priority": 1,
        "severity": "CRITICAL"
    },
    {
        "name": "P4_EVAL_USAGE",
        "condition": "eval() or Function() detected",
        "action": "APPEND xss_risk TO violations",
        "priority": 1,
        "severity": "CRITICAL"
    },
    {
        "name": "P5_INLINE_HANDLERS",
        "condition": "onclick= or onerror= detected",
        "action": "APPEND xss_risk TO violations",
        "priority": 1,
        "severity": "CRITICAL"
    }
]

## FIND<MatchingProductions> - Evaluate conditions against codebase

## P1: DOM Bypasses (document.createElement instead of DOM.createElement)

GREP "document\\.createElement" IN target_files output_mode: content INTO dom_bypass_matches

IF dom_bypass_matches.length > 0:
    FOR EACH match IN dom_bypass_matches:
        APPEND {
            "type": "DOM_BYPASS",
            "severity": "CRITICAL",
            "file": match.file,
            "line": match.line,
            "code": match.match,
            "violation": "Direct document.createElement bypasses DOM factory sanitization",
            "law_violated": "LAW 4 - Security as Base-Inherited Capability",
            "impact": "Bypasses CSP-safe styling, entity tracking, and secure event binding",
            "remediation": "Replace with DOM.createElement() from dom-factory.ts"
        } TO dom_bypasses

## P2: XSS Risks (innerHTML/outerHTML assignments)

GREP "innerHTML\\s*=|outerHTML\\s*=" IN target_files output_mode: content INTO innerHTML_matches

IF innerHTML_matches.length > 0:
    FOR EACH match IN innerHTML_matches:
        ## Check if sanitization precedes the assignment
        READ match.file INTO file_content
        GREP "sanitize|escape|DOMPurify" IN file_content INTO sanitization_check

        IF sanitization_check.length === 0:
            APPEND {
                "type": "XSS_INNERHTML",
                "severity": "CRITICAL",
                "file": match.file,
                "line": match.line,
                "code": match.match,
                "violation": "innerHTML assignment without sanitization",
                "law_violated": "LAW 4 - Security as Base-Inherited Capability",
                "impact": "Direct XSS vulnerability - user input could inject malicious scripts",
                "remediation": "Use DOM.createElement() with textContent, or sanitize with DOMPurify"
            } TO xss_risks

## P3: SQL Injection Risks (string concatenation in queries)

GREP "query\\s*\\(\\s*['\"].*\\+|query\\s*\\(\\s*\`.*\\$\\{" IN target_files output_mode: content INTO sql_concat_matches

IF sql_concat_matches.length > 0:
    FOR EACH match IN sql_concat_matches:
        APPEND {
            "type": "SQL_INJECTION",
            "severity": "CRITICAL",
            "file": match.file,
            "line": match.line,
            "code": match.match,
            "violation": "SQL query with string concatenation instead of parameterization",
            "law_violated": "LAW 4 - Security as Base-Inherited Capability",
            "impact": "SQL injection vulnerability - attacker could manipulate database queries",
            "remediation": "Use parameterized queries: db.query('SELECT * FROM users WHERE id = ?', [userId])"
        } TO sql_injection_risks

## P4: eval() and Function() usage

GREP "\\beval\\s*\\(|new\\s+Function\\s*\\(" IN target_files output_mode: content INTO eval_matches

IF eval_matches.length > 0:
    FOR EACH match IN eval_matches:
        APPEND {
            "type": "XSS_EVAL",
            "severity": "CRITICAL",
            "file": match.file,
            "line": match.line,
            "code": match.match,
            "violation": "eval() or Function() usage - arbitrary code execution risk",
            "law_violated": "LAW 4 - Security as Base-Inherited Capability",
            "impact": "Code injection vulnerability - allows arbitrary code execution",
            "remediation": "Remove eval/Function usage, use JSON.parse for data or proper parsing"
        } TO xss_risks

## P5: Inline event handlers

GREP "onclick\\s*=|onerror\\s*=|onload\\s*=" IN target_files output_mode: content INTO inline_handler_matches

IF inline_handler_matches.length > 0:
    FOR EACH match IN inline_handler_matches:
        APPEND {
            "type": "XSS_INLINE_HANDLER",
            "severity": "CRITICAL",
            "file": match.file,
            "line": match.line,
            "code": match.match,
            "violation": "Inline event handler violates CSP",
            "law_violated": "LAW 4 - Security as Base-Inherited Capability",
            "impact": "CSP violation - inline handlers blocked by Content Security Policy",
            "remediation": "Use addEventListener() via DOM factory's 'on' option"
        } TO xss_risks

## P6: insertAdjacentHTML usage

GREP "insertAdjacentHTML\\s*\\(" IN target_files output_mode: content INTO adjacent_html_matches

IF adjacent_html_matches.length > 0:
    FOR EACH match IN adjacent_html_matches:
        APPEND {
            "type": "XSS_ADJACENT_HTML",
            "severity": "CRITICAL",
            "file": match.file,
            "line": match.line,
            "code": match.match,
            "violation": "insertAdjacentHTML bypasses DOM factory sanitization",
            "law_violated": "LAW 4 - Security as Base-Inherited Capability",
            "impact": "XSS vulnerability - unsanitized HTML insertion",
            "remediation": "Use DOM.createElement() and appendChild() instead"
        } TO xss_risks

## P7: Path Traversal Risks (unsanitized file paths)

GREP "fs\\.readFile\\s*\\(.*\\+|path\\.join\\s*\\(.*req\\." IN target_files output_mode: content INTO path_traversal_matches

IF path_traversal_matches.length > 0:
    FOR EACH match IN path_traversal_matches:
        READ match.file INTO file_content
        GREP "path\\.normalize|path\\.resolve" IN file_content INTO path_validation

        IF path_validation.length === 0:
            APPEND {
                "type": "PATH_TRAVERSAL",
                "severity": "HIGH",
                "file": match.file,
                "line": match.line,
                "code": match.match,
                "violation": "File path from user input without validation",
                "law_violated": "LAW 4 - Security as Base-Inherited Capability",
                "impact": "Path traversal vulnerability - access to arbitrary files",
                "remediation": "Validate paths with path.normalize() and whitelist checking"
            } TO path_traversal_risks

## P8: Missing Security Inheritance (components not extending base)

GREP "class\\s+\\w+Component" IN component_files output_mode: files_with_matches INTO component_classes

FOR EACH component_file IN component_classes:
    READ component_file INTO component_content
    GREP "extends\\s+BaseComponent|extends\\s+Base" IN component_content INTO extends_check

    IF extends_check.length === 0:
        GREP "DOM\\.createElement|sanitize|validate" IN component_content INTO security_usage

        IF security_usage.length > 0:
            APPEND {
                "type": "MISSING_INHERITANCE",
                "severity": "HIGH",
                "file": component_file,
                "line": 1,
                "code": "class declaration",
                "violation": "Component reimplements security instead of inheriting",
                "law_violated": "LAW 4 - Security as Base-Inherited Capability",
                "impact": "Security discipline-based, not structural - can be bypassed",
                "remediation": "Extend BaseComponent to inherit security capabilities"
            } TO missing_inheritance

## Calculate total violations

CALCULATE total_violations = dom_bypasses.length + xss_risks.length + sql_injection_risks.length + path_traversal_risks.length + missing_inheritance.length

SET violations = {
    "dom_bypasses": {
        "count": dom_bypasses.length,
        "severity": "CRITICAL",
        "violations": dom_bypasses
    },
    "xss_risks": {
        "count": xss_risks.length,
        "severity": "CRITICAL",
        "violations": xss_risks
    },
    "sql_injection_risks": {
        "count": sql_injection_risks.length,
        "severity": "CRITICAL",
        "violations": sql_injection_risks
    },
    "path_traversal_risks": {
        "count": path_traversal_risks.length,
        "severity": "HIGH",
        "violations": path_traversal_risks
    },
    "missing_inheritance": {
        "count": missing_inheritance.length,
        "severity": "HIGH",
        "violations": missing_inheritance
    },
    "total_violations": total_violations
}

WRITE violations TO workspace_path + "/violations/03-LAW-4-VIOLATIONS.json"

## Generate individual category reports

WRITE {"violations": dom_bypasses, "count": dom_bypasses.length} TO workspace_path + "/violations/LAW-4-DOM-BYPASS-INVENTORY.json"
WRITE {"violations": xss_risks, "count": xss_risks.length} TO workspace_path + "/violations/LAW-4-XSS-RISKS.json"
WRITE {"violations": sql_injection_risks, "count": sql_injection_risks.length} TO workspace_path + "/violations/LAW-4-SQL-INJECTION-RISKS.json"
WRITE {"violations": path_traversal_risks, "count": path_traversal_risks.length} TO workspace_path + "/violations/LAW-4-PATH-TRAVERSAL-RISKS.json"
WRITE {"violations": missing_inheritance, "count": missing_inheritance.length} TO workspace_path + "/violations/LAW-4-MISSING-INHERITANCE.json"

VALIDATION GATE: Violation Detection Complete
✅ production_rules evaluated against codebase
✅ dom_bypasses detected (document.createElement)
✅ xss_risks detected (innerHTML, eval, inline handlers)
✅ sql_injection_risks detected (string concatenation)
✅ path_traversal_risks detected (unsanitized paths)
✅ missing_inheritance detected (components not extending base)
✅ total_violations calculated
✅ violation reports written

# PHASE 5: SANITIZATION CHAIN TRACING

## ALGORITHM: Dependency-Directed-Backtracking-Loop

## READ<Problem> → CREATE<Dependencies> → EXECUTE<ForwardReasoning> → FIND<Contradiction> → ANALYZE<Dependencies> → EXECUTE<TargetedBacktrack>

DECLARE sanitization_chains: array
DECLARE user_input_sources: array
DECLARE dangerous_sinks: array
DECLARE unsanitized_flows: array

SET sanitization_chains = []
SET user_input_sources = []
SET dangerous_sinks = []
SET unsanitized_flows = []

## Define user input sources (activation points in dependency graph)

GREP "req\\.body|req\\.query|req\\.params|process\\.argv" IN target_files output_mode: content INTO user_inputs

FOR EACH input IN user_inputs:
    APPEND {
        "source": input.match,
        "file": input.file,
        "line": input.line,
        "type": "user_input"
    } TO user_input_sources

## Define dangerous sinks (must have sanitization predecessor)

GREP "innerHTML\\s*=|document\\.write|eval\\(|query\\(" IN target_files output_mode: content INTO sinks

FOR EACH sink IN sinks:
    APPEND {
        "sink": sink.match,
        "file": sink.file,
        "line": sink.line,
        "type": "dangerous_sink"
    } TO dangerous_sinks

## CREATE<Dependencies> - Build flow graph from source to sink

FOR EACH source IN user_input_sources:
    FOR EACH sink IN dangerous_sinks:
        IF source.file === sink.file:
            ## EXECUTE<ForwardReasoning> - Trace from input to sink
            READ source.file INTO flow_content

            ## FIND<Contradiction> - Check for missing sanitization
            GREP "sanitize|escape|validate|DOMPurify" IN flow_content INTO sanitization_found

            IF sanitization_found.length === 0:
                ## ANALYZE<Dependencies> - Identify culprit assumption (missing sanitization)
                APPEND {
                    "source": source.source,
                    "source_line": source.line,
                    "sink": sink.sink,
                    "sink_line": sink.line,
                    "file": source.file,
                    "sanitization_chain": [],
                    "missing_sanitization": true,
                    "severity": "CRITICAL",
                    "vulnerability": "User input flows to dangerous sink without sanitization"
                } TO unsanitized_flows
            ELSE:
                APPEND {
                    "source": source.source,
                    "sink": sink.sink,
                    "file": source.file,
                    "sanitization_chain": sanitization_found,
                    "missing_sanitization": false,
                    "severity": "NONE"
                } TO sanitization_chains

DECLARE sanitization_analysis: object
SET sanitization_analysis = {
    "user_input_sources": user_input_sources.length,
    "dangerous_sinks": dangerous_sinks.length,
    "sanitized_chains": sanitization_chains.length,
    "unsanitized_flows": unsanitized_flows.length,
    "chains": sanitization_chains,
    "violations": unsanitized_flows
}

WRITE sanitization_analysis TO workspace_path + "/violations/04-sanitization-chains.json"

VALIDATION GATE: Sanitization Chain Tracing Complete
✅ user_input_sources identified
✅ dangerous_sinks identified
✅ dependency_graph constructed (source → sink flows)
✅ sanitization_chains traced via forward reasoning
✅ unsanitized_flows detected via contradiction
✅ sanitization_analysis written

# PHASE 6: REMEDIATION PLANNING

## ALGORITHM: Means-Ends-Analysis-Loop

## READ<State> → FIND<Difference> → FIND<Operator> → ANALYZE<Preconditions> → EXECUTE<Operator>

DECLARE remediation_plan: object
DECLARE migration_strategies: array
DECLARE operator_table: object

SET migration_strategies = []

## Define Goal State (secure architecture)

SET goal_state = {
    "dom_bypasses": 0,
    "xss_risks": 0,
    "sql_injection_risks": 0,
    "all_inherit_security": true,
    "zero_unsanitized_flows": true
}

## Define Current State (from violations)

SET current_state = {
    "dom_bypasses": dom_bypasses.length,
    "xss_risks": xss_risks.length,
    "sql_injection_risks": sql_injection_risks.length,
    "missing_inheritance": missing_inheritance.length,
    "unsanitized_flows": unsanitized_flows.length
}

## Define Operator Table (remediation operations)

SET operator_table = {
    "MIGRATE_TO_DOM_FACTORY": {
        "reduces": ["dom_bypasses"],
        "preconditions": ["DOM.createElement available"],
        "transformation": "document.createElement(...) → DOM.createElement(...)",
        "impact": "Eliminates DOM bypass, adds CSP compliance"
    },
    "SANITIZE_INNERHTML": {
        "reduces": ["xss_risks"],
        "preconditions": ["DOMPurify available"],
        "transformation": "innerHTML = userInput → innerHTML = DOMPurify.sanitize(userInput)",
        "impact": "Eliminates XSS risk"
    },
    "PARAMETERIZE_QUERY": {
        "reduces": ["sql_injection_risks"],
        "preconditions": ["Database supports parameterization"],
        "transformation": "query('SELECT * FROM t WHERE id=' + id) → query('SELECT * FROM t WHERE id=?', [id])",
        "impact": "Eliminates SQL injection"
    },
    "EXTEND_BASE_CLASS": {
        "reduces": ["missing_inheritance"],
        "preconditions": ["BaseComponent exists"],
        "transformation": "class MyComponent → class MyComponent extends BaseComponent",
        "impact": "Inherits security capabilities structurally"
    }
}

## FIND<Difference> and select operators

IF dom_bypasses.length > 0:
    APPEND {
        "violation_type": "DOM_BYPASS",
        "count": dom_bypasses.length,
        "operator": "MIGRATE_TO_DOM_FACTORY",
        "priority": "P0",
        "effort": "MEDIUM",
        "files_affected": EXTRACT_UNIQUE(violation.file FOR EACH violation IN dom_bypasses),
        "migration_steps": [
            "Import { DOM } from '../utils/dom-factory'",
            "Replace document.createElement(tag) with DOM.createElement(tag, {})",
            "Move attributes to options object",
            "Verify CSP compliance after migration"
        ]
    } TO migration_strategies

IF xss_risks.length > 0:
    APPEND {
        "violation_type": "XSS_RISK",
        "count": xss_risks.length,
        "operator": "SANITIZE_INNERHTML",
        "priority": "P0",
        "effort": "LOW",
        "files_affected": EXTRACT_UNIQUE(violation.file FOR EACH violation IN xss_risks),
        "migration_steps": [
            "Replace innerHTML with textContent where possible",
            "If HTML needed, use DOMPurify.sanitize() before assignment",
            "Or migrate to DOM.createElement() for full control",
            "Verify no XSS vectors remain"
        ]
    } TO migration_strategies

IF sql_injection_risks.length > 0:
    APPEND {
        "violation_type": "SQL_INJECTION",
        "count": sql_injection_risks.length,
        "operator": "PARAMETERIZE_QUERY",
        "priority": "P0",
        "effort": "LOW",
        "files_affected": EXTRACT_UNIQUE(violation.file FOR EACH violation IN sql_injection_risks),
        "migration_steps": [
            "Replace string concatenation with placeholder syntax",
            "Pass user input as separate parameter array",
            "Use prepared statements for all queries",
            "Verify parameterization with SQL logging"
        ]
    } TO migration_strategies

IF missing_inheritance.length > 0:
    APPEND {
        "violation_type": "MISSING_INHERITANCE",
        "count": missing_inheritance.length,
        "operator": "EXTEND_BASE_CLASS",
        "priority": "P1",
        "effort": "MEDIUM",
        "files_affected": EXTRACT_UNIQUE(violation.file FOR EACH violation IN missing_inheritance),
        "migration_steps": [
            "Add 'extends BaseComponent' to class declaration",
            "Remove duplicated security methods (now inherited)",
            "Call super() in constructor",
            "Verify inherited security methods work correctly"
        ]
    } TO migration_strategies

SET remediation_plan = {
    "goal_state": goal_state,
    "current_state": current_state,
    "operator_table": operator_table,
    "migration_strategies": migration_strategies,
    "estimated_effort_hours": migration_strategies.length * 4,
    "priority_order": ["DOM_BYPASS", "XSS_RISK", "SQL_INJECTION", "MISSING_INHERITANCE"]
}

WRITE remediation_plan TO workspace_path + "/remediation/05-LAW-4-REMEDIATION-PLAN.json"

VALIDATION GATE: Remediation Planning Complete
✅ goal_state defined (zero violations)
✅ current_state extracted from violations
✅ operator_table defined (remediation operations)
✅ migration_strategies generated for each violation type
✅ remediation_plan written

# PHASE 7: VERIFICATION RESULTS

DECLARE verification_results: object
DECLARE compliant_patterns: array
DECLARE security_score: number

SET compliant_patterns = []

## Verify compliant DOM factory usage

GREP "DOM\\.createElement" IN target_files output_mode: count INTO compliant_dom_usage

APPEND {
    "pattern": "DOM.createElement usage",
    "count": compliant_dom_usage,
    "compliant": true,
    "description": "Uses DOM factory for secure element creation"
} TO compliant_patterns

## Verify parameterized queries

GREP "query\\s*\\(\\s*['\"].*\\?['\"]\\s*,\\s*\\[" IN target_files output_mode: count INTO parameterized_queries

APPEND {
    "pattern": "Parameterized SQL queries",
    "count": parameterized_queries,
    "compliant": true,
    "description": "Uses parameterized queries to prevent SQL injection"
} TO compliant_patterns

## Calculate security score

CALCULATE security_score = 100
IF dom_bypasses.length > 0:
    SET security_score = security_score - (dom_bypasses.length * 10)
IF xss_risks.length > 0:
    SET security_score = security_score - (xss_risks.length * 10)
IF sql_injection_risks.length > 0:
    SET security_score = security_score - (sql_injection_risks.length * 10)
IF path_traversal_risks.length > 0:
    SET security_score = security_score - (path_traversal_risks.length * 5)
IF missing_inheritance.length > 0:
    SET security_score = security_score - (missing_inheritance.length * 5)

IF security_score < 0:
    SET security_score = 0

SET verification_results = {
    "security_score": security_score,
    "compliant_patterns": compliant_patterns,
    "violation_summary": {
        "critical": dom_bypasses.length + xss_risks.length + sql_injection_risks.length,
        "high": path_traversal_risks.length + missing_inheritance.length,
        "total": total_violations
    },
    "law_4_compliance": security_score >= 80 ? "PASS" : "FAIL"
}

WRITE verification_results TO workspace_path + "/06-LAW-4-VERIFICATION-RESULTS.json"

VALIDATION GATE: Verification Complete
✅ compliant_patterns identified
✅ security_score calculated
✅ law_4_compliance determined
✅ verification_results written

# PHASE 8: FINAL REPORT GENERATION

DECLARE final_report: string

SET final_report = "# LAW 4 Enforcement Report\n\n"
APPEND final_report: "**Analysis ID**: " + analysis_id + "\n"
APPEND final_report: "**Date**: " + timestamp + "\n"
APPEND final_report: "**Files Analyzed**: " + file_count + "\n"
APPEND final_report: "**Security Score**: " + security_score + "/100\n"
APPEND final_report: "**LAW 4 Compliance**: " + verification_results.law_4_compliance + "\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Violation Summary\n\n"
APPEND final_report: "**Total Violations**: " + total_violations + "\n\n"
APPEND final_report: "### 🔴 CRITICAL Violations: " + (dom_bypasses.length + xss_risks.length + sql_injection_risks.length) + "\n\n"
APPEND final_report: "- **DOM Bypasses**: " + dom_bypasses.length + " (document.createElement usage)\n"
APPEND final_report: "- **XSS Risks**: " + xss_risks.length + " (innerHTML, eval, inline handlers)\n"
APPEND final_report: "- **SQL Injection Risks**: " + sql_injection_risks.length + " (string concatenation in queries)\n\n"
APPEND final_report: "### 🟠 HIGH Violations: " + (path_traversal_risks.length + missing_inheritance.length) + "\n\n"
APPEND final_report: "- **Path Traversal Risks**: " + path_traversal_risks.length + " (unsanitized file paths)\n"
APPEND final_report: "- **Missing Inheritance**: " + missing_inheritance.length + " (components not extending base)\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Security Architecture\n\n"
APPEND final_report: "**Security Providers Found**: " + security_providers.length + "\n\n"

FOR EACH provider IN security_providers:
    APPEND final_report: "- **" + provider.name + "** (" + provider.type + ")\n"
    APPEND final_report: "  - File: `" + provider.file + "`\n"
    APPEND final_report: "  - Capabilities: " + JOIN(provider.security_capabilities, ", ") + "\n\n"

APPEND final_report: "---\n\n"
APPEND final_report: "## Remediation Priority\n\n"
APPEND final_report: "1. **P0 - CRITICAL**: Fix DOM bypasses (" + dom_bypasses.length + " instances)\n"
APPEND final_report: "2. **P0 - CRITICAL**: Fix XSS risks (" + xss_risks.length + " instances)\n"
APPEND final_report: "3. **P0 - CRITICAL**: Fix SQL injection risks (" + sql_injection_risks.length + " instances)\n"
APPEND final_report: "4. **P1 - HIGH**: Fix path traversal risks (" + path_traversal_risks.length + " instances)\n"
APPEND final_report: "5. **P1 - HIGH**: Add missing inheritance (" + missing_inheritance.length + " instances)\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Generated Files\n\n"
APPEND final_report: "All analysis files located in: `" + workspace_path + "/`\n\n"
APPEND final_report: "- **00-manifest.json**: Analysis metadata\n"
APPEND final_report: "- **01-discovery.json**: Target file discovery\n"
APPEND final_report: "- **02-security-architecture.json**: Security provider analysis\n"
APPEND final_report: "- **03-LAW-4-VIOLATIONS.json**: All violations categorized\n"
APPEND final_report: "- **04-sanitization-chains.json**: Input → Sink flow analysis\n"
APPEND final_report: "- **05-LAW-4-REMEDIATION-PLAN.json**: Migration strategies\n"
APPEND final_report: "- **06-LAW-4-VERIFICATION-RESULTS.json**: Compliance results\n\n"
APPEND final_report: "### Individual Category Reports:\n\n"
APPEND final_report: "- **LAW-4-DOM-BYPASS-INVENTORY.json**: " + dom_bypasses.length + " violations\n"
APPEND final_report: "- **LAW-4-XSS-RISKS.json**: " + xss_risks.length + " violations\n"
APPEND final_report: "- **LAW-4-SQL-INJECTION-RISKS.json**: " + sql_injection_risks.length + " violations\n"
APPEND final_report: "- **LAW-4-PATH-TRAVERSAL-RISKS.json**: " + path_traversal_risks.length + " violations\n"
APPEND final_report: "- **LAW-4-MISSING-INHERITANCE.json**: " + missing_inheritance.length + " violations\n\n"
APPEND final_report: "---\n\n"
APPEND final_report: "## Next Steps\n\n"
APPEND final_report: "1. Review violation reports in `violations/` directory\n"
APPEND final_report: "2. Follow remediation plan in `05-LAW-4-REMEDIATION-PLAN.json`\n"
APPEND final_report: "3. Migrate CRITICAL violations first (P0)\n"
APPEND final_report: "4. Re-run law-4-enforcer to verify fixes\n"
APPEND final_report: "5. Achieve 100% LAW 4 compliance (security score >= 80)\n"

WRITE final_report TO workspace_path + "/LAW-4-ENFORCEMENT-REPORT.md"

OUTPUT final_report

VALIDATION GATE: Final Report Complete
✅ final_report generated
✅ violation_summary included
✅ security_architecture documented
✅ remediation_priority listed
✅ generated_files documented
✅ next_steps provided
✅ report delivered to user

# SUCCESS CRITERIA

VALIDATION GATE: All Phases Complete
✅ Phase 0: Tools calibrated for security pattern detection
✅ Phase 1: Workspace initialized, target scope defined
✅ Phase 2: Target files discovered via Glob
✅ Phase 3: Security architecture analyzed (DOM factory, base classes)
✅ Phase 4: Violations detected (DOM bypasses, XSS, SQL injection, path traversal, missing inheritance)
✅ Phase 5: Sanitization chains traced from input to sink
✅ Phase 6: Remediation plan generated with migration strategies
✅ Phase 7: Verification results calculated, security score computed
✅ Phase 8: Final report generated and delivered

IF security_score >= 80:
    REPORT "✅ LAW 4 COMPLIANCE ACHIEVED - Security score: " + security_score + "/100"
ELSE:
    REPORT "❌ LAW 4 VIOLATIONS DETECTED - Security score: " + security_score + "/100 - " + total_violations + " violations require remediation"

# ABORT HANDLER

START ABORT
    DECLARE abort_reason: string
    SET abort_reason = EXTRACT_MESSAGE(ABORT)

    WRITE {
        "status": "ABORTED",
        "reason": abort_reason,
        "analysis_id": analysis_id,
        "timestamp": ISO8601_NOW(),
        "verified_claims": verified_claims,
        "refuted_claims": refuted_claims,
        "partial_results": {
            "files_discovered": file_count,
            "violations_detected": total_violations
        }
    } TO workspace_path + "/ABORT-REPORT.json"

    REPORT "---"
    REPORT "LAW 4 Enforcement ABORTED: " + abort_reason
    REPORT "Timestamp: " + ISO8601_NOW()
    REPORT "Partial results in: " + workspace_path
    REPORT "Verified claims: " + verified_claims.length
    REPORT "Refuted claims: " + refuted_claims.length
    REPORT "---"

    EXIT 1
END

# OPERATIONAL DIRECTIVES

ALWAYS calibrate tools before trusting pattern detection
ALWAYS verify file discovery with Glob before Read operations
ALWAYS track verified_claims and refuted_claims
ALWAYS check for security bypasses before approving patterns
ALWAYS trace sanitization chains from input to sink
ALWAYS prioritize CRITICAL violations (DOM bypass, XSS, SQL injection)
ALWAYS use bounded loops with max_iterations
ALWAYS check iteration_count before continuing loops
ALWAYS set goal_achieved condition for loop termination

NEVER trust grep results without verification
NEVER assume files exist without Glob check
NEVER skip tool calibration phase
NEVER allow unbounded loops (WHILE true, WHILE 1)
NEVER proceed with refuted_claims without acknowledgment
NEVER trust security patterns without empirical validation
NEVER approve code with CRITICAL security violations
NEVER skip sanitization chain analysis
