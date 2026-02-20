---
name: domain-classification
description: Use when determining if logging is needed, classifying functions by domain (RESOURCE/EXECUTION/COMPUTATION), or deciding whether to use Engine.logger for a specific function
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Classify functions by domain to determine logging requirement.

# CLASSIFICATION PROTOCOL

WHEN analyzing_function FOR logging_requirement:
EXTRACT function_signature
EXTRACT function_body

    # RESOURCE domain (REQUIRES LOGGING)
    GREP "fs\.|readFile|writeFile|db\.|query\(|fetch\(|axios|http\." IN body
    IF found: RETURN "RESOURCE - logging REQUIRED"

    GREP "init|start|stop|shutdown|destroy|acquire|release" IN signature
    IF found: RETURN "RESOURCE - logging REQUIRED"

    # EXECUTION domain (REQUIRES LOGGING)
    GREP "addEventListener|\.on\(|once\(|ipcRenderer|ipcMain|\.emit\(|Intent" IN body
    IF found: RETURN "EXECUTION - logging REQUIRED"

    # COMPUTATION domain (SKIP LOGGING)
    IF signature MATCHES /^(is|has|should|can)/ AND NOT body CONTAINS ["await", "fs.", "db."]:
        RETURN "COMPUTATION - logging NOT needed"

    IF signature MATCHES /^(to|from).*(Path|String|Number|Date|JSON)/:
        RETURN "COMPUTATION - logging NOT needed"

    IF signature MATCHES /(format|parse|normalize|sanitize|convert)/ AND NOT body CONTAINS ["await", "fs."]:
        RETURN "COMPUTATION - logging NOT needed"

    IF lines <= 2 AND body STARTS WITH "return":
        RETURN "COMPUTATION - logging NOT needed"

    # Unknown - analyze complexity
    IF body_lines >= 10 OR is_async OR has_try_catch:
        RETURN "REQUIRES_ANALYSIS - likely RESOURCE/EXECUTION"
    ELSE:
        RETURN "COMPUTATION - simple utility"

# VALIDATION

VERIFY classification BEFORE deciding on logging
IF RESOURCE OR EXECUTION: REFERENCE engine-logger skill FOR schema

# REFERENCES

**Authoritative**:

- `.claude/_DEV-RULES.md` - Domain definitions
- `root/archlab-ide/src/renderer/engine/debug/README.md` - Engine.logger schema

**Related Skills**:

- `engine-logger` - How to log (schema compliance)
- `principle-architecture` - Domain principle mappings
