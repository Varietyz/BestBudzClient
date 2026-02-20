---
name: dom-factory
description: Use when creating DOM elements, working with UI components, applying reserved keys (style/on/data/animate), implementing entity tracking, or ensuring CSP-safe element creation
allowed-tools: Read, Grep, Glob
---

# PURPOSE

Enforce DOM.createElement() pattern and reserved key usage for CSP-safe UI.

# DOM FACTORY PROTOCOL

WHEN user CREATES element:
VERIFY using "DOM.createElement()" NOT "document.createElement()"
REQUIRE "entity" parameter FOR uuid_tracking
EXAMPLE:
`typescript
        const panel = await DOM.createElement("div", {
            entity: "panel",  // REQUIRED
            className: "settings-panel"
        });
        `

    READ "root/archlab-ide/docs/dom-factory/dom-factory.md" FOR reserved_keys

WHEN user APPLIES styling:
VERIFY using "style" reserved_key NOT inline_styles
VERIFY tokens FROM css-tokens skill
EXAMPLE: {style: {padding: 16, backgroundColor: "var(--color-bg-primary)"}}

WHEN user ADDS event_handlers:
VERIFY using "on" reserved_key NOT addEventListener
EXAMPLE: {on: {click: (e) => handleClick(e)}}

WHEN user RELEASES element:
REQUIRE "DOM.releaseElement(el)" BEFORE "el.remove()"
GREP "remove()" WITHOUT "releaseElement" → violation

WHEN user NEEDS high_level_constructor:
GLOB "root/archlab-ide/src/renderer/utils/dom-factory.ts"
FIND: DOM.button(), DOM.input(), DOM.icon(), DOM.window(), DOM.sidebar(), DOM.card(), etc.

# RESERVED KEYS

DECLARE reserved_keys: array
SET reserved_keys = [
"entity", // REQUIRED - UUID tracking
"style", // CSP-safe styling
"on", // Event handlers
"data", // Data attributes
"animate", // CSS animations
"keyboard", // Keyboard shortcuts
"focus", // Focus management
"scroll", // Scroll detection
"layout", // Resizable layouts
"intent" // LAW 2 intent emission
]

WHEN user USES reserved_key:
READ "root/archlab-ide/docs/dom-factory/dom-factory.md"
FIND key_documentation
APPLY specialized_manager

# VALIDATION

GREP "document.createElement" → BLOCK (use DOM.createElement)
GREP "innerHTML =" → BLOCK (use DOM.setContent)
GREP ".remove()" WITHOUT prior_releaseElement → WARN

# REFERENCES

**Authoritative**:

- `root/archlab-ide/docs/dom-factory/dom-factory.md` - Full DOM Factory docs
- `CLAUDE.md` - DOM Factory section

**Related Skills**:

- `css-tokens` - Token usage in style reserved key
- `law-enforcement` - LAW 2 intent emission via intent key
