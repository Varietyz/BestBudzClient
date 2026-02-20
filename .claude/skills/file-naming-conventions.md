---
name: file-naming-conventions
description: Use when creating files, naming classes, organizing folders, structuring directories, or ensuring naming consistency with kebab-case, PascalCase, and ARCHLAB file structure rules
allowed-tools: Read, Glob
---

# PURPOSE

Enforce ARCHLAB file and folder naming conventions.

# NAMING PROTOCOL

WHEN user CREATES file:
VERIFY filename IS kebab_case
IF NOT: REPORT "Files use kebab-case: user-service.ts, not UserService.ts"

    EXTRACT parent_folder
    VERIFY filename SUFFIX matches_folder
    EXAMPLE: "/services/" → "*-service.ts"

    IF base_class: VERIFY prefix IS "base-"
    EXAMPLE: "base-component.ts" NOT "component-base.ts"

    READ ".claude/_ARCHLAB.md" File Naming section FOR rules

WHEN user CREATES folder:
VERIFY folder IS plural_noun
EXAMPLE: "/services/" NOT "/service/"

    CHECK nesting_depth <= 2
    IF violation: REPORT "Max 2 levels domain nesting"

    READ ".claude/_ARCHLAB.md" Folder structure section

WHEN user NAMES class:
EXTRACT filename
VERIFY class_name === PascalCase(filename)
EXAMPLE: "file-tree-panel.ts" → class FileTreePanel

    VERIFY one_primary_export per_file

    READ ".claude/_ARCHLAB.md" Class naming section

WHEN user CREATES constants:
VERIFY constants USE SCREAMING_SNAKE_CASE
VERIFY aggregated IN "\*-constants.ts" file

WHEN user CREATES types:
VERIFY type_aliases HAVE descriptive_suffixes
EXAMPLE: "FileId", "SaveResult" NOT "File", "Result"

    VERIFY interfaces FOR capabilities USE "I" prefix
    EXAMPLE: "IDisposable" for capability contracts

# VALIDATION

GLOB pattern FOR verification:

- Files: kebab-case check
- Folders: plural check, nesting depth
- Base classes: "base-" prefix check

# REFERENCES

**Authoritative**:

- `.claude/_ARCHLAB.md` - File Naming Conventions section
- `.claude/_ARCHLAB.md` - Structure Limits section
