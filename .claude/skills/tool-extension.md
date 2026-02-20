---
name: tool-extension
description: Use when analysis requires capabilities beyond basic tools (AST parsing, semantic analysis, control flow graphs), or when creating custom verification/analysis scripts
allowed-tools: Write, Bash, Read
---

# PURPOSE

Create custom analysis tools when direct capabilities insufficient.

# TOOL CREATION PROTOCOL

DECLARE direct_capabilities: array
SET direct_capabilities = ["grep", "glob", "bash", "file_read", "file_write"]

DECLARE indirect_capabilities: array
SET indirect_capabilities = [
"AST analysis (via acorn/babel parser scripts)",
"Semantic code analysis (via custom analyzers)",
"Control flow graph generation",
"Dependency tree extraction",
"Symbol table construction",
"Type inference analysis"
]

WHEN verification_requires_advanced_analysis:
IF capability NOT IN direct_capabilities:
EXECUTE tool_creation_steps: 1. Identify required analysis type 2. Design modular tool architecture 3. Write tool to .claude/workspace/tools/ 4. Execute tool against target code 5. Parse tool output as evidence 6. Integrate results into verification

WHEN creating_analysis_script:
VERIFY environment_dependencies: - BASH "node --version" → Node.js available - BASH "npm --version" → npm available - CHECK write_permissions

    WRITE tool_script WITH:
        - Modular design
        - Clear input/output contract
        - Error handling
        - JSON output for parsing

    EXECUTE tool_script
    PARSE results
    USE as_evidence

# VALIDATION

VERIFY dependencies BEFORE creating tool
TEST tool_with_known_cases
PARSE output_reliably
DOCUMENT tool_purpose

# REFERENCES

**Authoritative**:

- `.claude/agents/forensic-context-verifier.md` - Tool creation for advanced analysis

**Related Skills**:

- `investigate-then-act` - Create tools in INVESTIGATE phase
- `skeptical-verification` - Verify tool correctness
- `error-remediation` - Handle tool execution failures
