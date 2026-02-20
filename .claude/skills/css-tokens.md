---
name: css-tokens
description: Use when writing CSS, applying styles, working with colors, spacing, typography, transitions, or ensuring token-only values with REM units and no hardcoded px or hex colors
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Enforce token-only CSS values and REM-first sizing.

# CSS TOKENS PROTOCOL

WHEN user WRITES css:
GREP "\\d+px" IN css_file
IF found: BLOCK WITH "Use tokens: var(--space-\*) or REM units"

    GREP "#[0-9a-fA-F]{3,6}" IN css_file
    IF found: BLOCK WITH "Use tokens: var(--color-*)"

    VERIFY all_values USE tokens_or_rem

    READ "root/archlab-ide/src/renderer/styles/STYLING-GUIDE.md"
    READ "root/archlab-ide/docs/style-rulebook.md"

WHEN user NEEDS token_reference:
GLOB "root/archlab-ide/src/renderer/styles/tokens/_.css"
FIND: - spacing tokens: var(--space-xs|sm|md|lg|xl) - color tokens: var(--color-bg-primary|secondary|...) - radius tokens: var(--radius-sm|md|lg) - transition tokens: var(--transition-fast|normal|slow) - typography tokens: var(--font-size-_), var(--font-weight-\*)

WHEN user APPLIES spacing:
REQUIRE "var(--space-md)" NOT "12px"
EXAMPLE: padding: var(--space-md);

WHEN user APPLIES colors:
REQUIRE "var(--color-bg-primary)" NOT "#1e1e1e"
EXAMPLE: background: var(--color-bg-primary);

WHEN user APPLIES border_radius:
REQUIRE "var(--radius-md)" NOT "6px"
EXAMPLE: border-radius: var(--radius-md);

WHEN user APPLIES transitions:
REQUIRE "var(--transition-fast)" NOT "0.3s ease"
EXAMPLE: transition: var(--transition-fast);

# NAMING CONVENTIONS

WHEN user CREATES css_class:
VERIFY flat_bem_naming
EXAMPLE: - .search-box (block) - .search-input (element) - .search-result--active (modifier)

    READ "root/archlab-ide/docs/style-rulebook.md" Naming section

# IMPORT ORDER

WHEN user ORGANIZES main.css:
VERIFY order: 1. tokens/ (design tokens first) 2. base/ (reset, layout) 3. shared/ (reusable components) 4. components/ (complex components) 5. \*-page/ (page-specific)

# VALIDATION

GREP css_files FOR violations:

- \\d+px → hardcoded pixels
- #[0-9a-fA-F]+ → hardcoded hex colors
- [0-9.]+s → hardcoded transitions

# REFERENCES

**Authoritative**:

- `root/archlab-ide/src/renderer/styles/STYLING-GUIDE.md` - Token architecture
- `root/archlab-ide/docs/style-rulebook.md` - Complete rules
- `CLAUDE.md` - CSS Architecture section

**Related Skills**:

- `dom-factory` - Style reserved key usage
