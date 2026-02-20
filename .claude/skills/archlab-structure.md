---
name: archlab-structure
description: Use when organizing project structure, checking file size limits (150 lines), folder limits (6 files), understanding project layout, or working with archlab-ide directory organization
allowed-tools: Read, Glob, Bash
---

# PURPOSE

Enforce ARCHLAB structural limits and project organization.

# STRUCTURE PROTOCOL

WHEN user CREATES file:
COUNT lines IN file
IF lines > 150: BLOCK WITH "Max 150 lines per file"
SUGGEST complexity-boundary protocol FROM algorithmic-protocols skill
READ ".claude/\_ARCHLAB.md" Structure Limits section

WHEN user ADDS file TO folder:
GLOB folder_path FOR file_count
EXCLUDE "index.ts" FROM count
IF file_count >= 6: BLOCK WITH "Max 6 files per folder (excluding index.ts)"
SUGGEST module-separation FROM algorithmic-protocols skill

WHEN user NEEDS project_structure_reference:
READ ".claude/\_ARCHLAB.md" Project Structure section
SHOW: - "root/archlab-ide/src/main/" → Electron main process - "root/archlab-ide/src/preload/" → Preload scripts - "root/archlab-ide/src/renderer/" → UI (DOM Factory consumers) - "root/archlab-ide/src/renderer/components/" → UI components - "root/archlab-ide/src/renderer/engine/" → Pools, culling, validators - "root/archlab-ide/src/renderer/utils/" → DOM factory, managers - "root/archlab-ide/src/renderer/styles/" → CSS with tokens - "root/codebase-validation/" → Validation framework

WHEN user CHECKS circular_dependencies:
VERIFY lower_layers NOT import_from higher_layers
REPORT "No circular dependencies - lower never imports from higher"

WHEN user VIOLATES limits:
READ ".claude/workspace/\_meta-intel/algorithms/\_DEV-RULES-ALGORITHMS.md"
APPLY Complexity-Boundary-Protocol
EXECUTE: FIND → ANALYZE → EXTRACT → CREATE → ITERATE

# VALIDATION

BASH "find . -name '\*.ts' -exec wc -l {} +" | GREP violations
GLOB folders FOR file_count violations

# REFERENCES

**Authoritative**:

- `.claude/_ARCHLAB.md` - Structure Limits section
- `.claude/_ARCHLAB.md` - Project Structure section

**Related Skills**:

- `algorithmic-protocols` - Complexity-Boundary-Protocol
- `file-naming-conventions` - Naming rules for split files
