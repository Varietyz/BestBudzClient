---
name: code-flow-tracing
description: Use when tracing execution paths, analyzing data flow, detecting security vulnerabilities, tracking variable lifecycles, or following user input through the system
allowed-tools: Grep, Read, Glob
---

# PURPOSE

Trace code execution paths, data flows, and security-critical paths.

# FLOW TRACING PROTOCOL

WHEN tracing_execution_path:
IDENTIFY entry_point (main, route handler, event listener)
GREP "if |switch |while |for |try " FOR decision_nodes
GREP "return |throw " FOR exit_points

    BUILD control_flow_graph:
        - entry: function_start
        - decision_nodes: branching points
        - exit_points: return/throw locations

    CALCULATE cyclomatic_complexity = 1 + decision_nodes.length

WHEN tracing_data_flow:
IDENTIFY variable_declaration
GREP variable_name + "\\s\*=" FOR assignments
GREP "\\b" + variable_name + "\\b" FOR reads

    TRACK lifecycle:
        - declaration_line
        - assignment_locations
        - read_locations
        - mutation_count

WHEN tracing_security_flow:
DECLARE user_input_sources: array
SET user_input_sources = ["req.body", "req.query", "req.params", "req.headers"]

    FOR EACH source IN user_input_sources:
        GREP source IN codebase INTO occurrences

        FOR EACH occurrence:
            EXTRACT variable_name
            TRACE variable TO:
                - database_sinks: "db.query|pool.query|database.exec"
                - output_sinks: "res.send|res.json|innerHTML"

            CHECK sanitization BETWEEN source AND sink:
                GREP "sanitize|escape|validate|parameterized" IN path

            IF no_sanitization:
                REPORT vulnerability:
                    - type: "SQL_injection" OR "XSS"
                    - severity: "critical"
                    - source: input_location
                    - sink: database/output_location

# VALIDATION

ALWAYS verify_complete_path FROM source TO sink
ALWAYS check_sanitization_points
NEVER assume_safe WITHOUT evidence

# REFERENCES

**Authoritative**:

- `.claude/agents/code-tracer.md` - Flow analysis framework

**Related Skills**:

- `complexity-analysis` - Control flow complexity
- `skeptical-verification` - Verify flow paths
