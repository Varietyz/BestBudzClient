# ARCHLAB DB-Driven DOM API

**Version**: 1.0
**Created**: 2025-12-30
**Type**: Core UI Infrastructure
**Depends On**: Database system, DOM Factory

---

## Overview

Unified DOM API where all UI flows through a database layer, enabling:
- Native code defaults
- User-specific overrides at any level
- Real-time UI manipulation
- Full undo capability
- Theme/preset switching

**The Spider Architecture**:
- **Left Legs** (Input): Code defaults, user overrides, themes, plugins
- **Body** (Core): DB reconciliation layer
- **Right Legs** (Output): DOM API renders from resolved DB state

---

## Data Flow

```
Code Defines Default → DB Stores State → DOM API Reads → UI Renders
                              ↑
                     User Override Writes
```

**Key Invariant**: DOM API NEVER reads code directly. Always reads resolved DB state.

---

## What Becomes DB-Driven

| Category | Examples | Override Granularity |
|----------|----------|---------------------|
| **Icons** | SVG code, image paths | Per-icon replacement |
| **Strings** | Labels, tooltips, descriptions | Per-string localization/customization |
| **Colors** | Theme colors, accent colors | Per-token or bulk theme |
| **Fonts** | Font family, sizes, weights | Per-token or bulk |
| **Spacing** | Margins, padding, gaps | Per-token |
| **CSS Classes** | Component styles | Per-class override |
| **Component Config** | Visibility, behavior flags | Per-component |

---

## Database Schema

### ui_defaults Table (Code → DB sync)

| Column | Type | Purpose |
|--------|------|---------|
| key | TEXT PRIMARY KEY | Unique identifier (e.g., 'icon.terminal', 'string.save', 'color.primary') |
| category | TEXT | 'icon', 'string', 'color', 'font', 'spacing', 'css', 'config' |
| default_value | TEXT | JSON-encoded default from code |
| current_value | TEXT | Resolved value (default + overrides) |
| updated_at | TEXT | Last modification timestamp |

### ui_overrides Table (User customizations)

| Column | Type | Purpose |
|--------|------|---------|
| id | INTEGER PRIMARY KEY | Auto-increment |
| element_key | TEXT | References ui_defaults.key |
| override_value | TEXT | User's custom value |
| source | TEXT | 'user', 'theme', 'plugin', 'preset' |
| priority | INTEGER | Higher priority wins (theme: 10, plugin: 20, user: 100) |
| created_at | TEXT | When override was created |

### ui_themes Table (Bulk override sets)

| Column | Type | Purpose |
|--------|------|---------|
| id | TEXT PRIMARY KEY | Theme identifier |
| name | TEXT | Display name |
| overrides_json | TEXT | JSON map of key → value |
| is_active | INTEGER | 0 or 1 |
| created_at | TEXT | Creation timestamp |

### ui_history Table (Undo support)

| Column | Type | Purpose |
|--------|------|---------|
| id | INTEGER PRIMARY KEY | Auto-increment |
| element_key | TEXT | What changed |
| old_value | TEXT | Previous resolved value |
| new_value | TEXT | New resolved value |
| source | TEXT | What triggered change |
| timestamp | TEXT | When it happened |

---

## Tasks

### Task 1: Database Migration

- [ ] Create ui_defaults table
- [ ] Create ui_overrides table with priority system
- [ ] Create ui_themes table
- [ ] Create ui_history table
- [ ] Index on element_key for fast lookups
- [ ] Index on category for bulk queries

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtasks)

---

### Task 2: UI Store (Core Body)

- [ ] Create ui-store.ts as singleton
- [ ] registerDefault(key, category, value): code registers its defaults
- [ ] resolve(key): returns current_value (default + overrides applied)
- [ ] resolveMany(keys[]): batch resolve for performance
- [ ] resolveByCategory(category): all resolved values in category
- [ ] Override priority resolution: user > plugin > theme > default
- [ ] Cache resolved values, invalidate on override change
- [ ] Emit 'ui:changed' event on any value change

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/8 subtasks)

---

### Task 3: Override API

- [ ] setOverride(key, value, source): add user/plugin override
- [ ] removeOverride(key, source): remove specific override
- [ ] clearOverrides(source): remove all overrides from source
- [ ] getOverrides(key): list all overrides for a key with priorities
- [ ] History tracking: every setOverride logs to ui_history

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/5 subtasks)

---

### Task 4: Undo System

- [ ] undo(key): revert last change for specific element
- [ ] undoLast(): revert most recent change globally
- [ ] getHistory(key?, limit?): query change history
- [ ] Undo creates inverse override, logged as source: 'undo'

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/4 subtasks)

---

### Task 5: Theme System

- [ ] createTheme(name, overrides): save new theme
- [ ] activateTheme(id): apply theme overrides (priority: 10)
- [ ] deactivateTheme(): remove theme overrides
- [ ] listThemes(): available themes
- [ ] exportTheme(id): JSON export for sharing
- [ ] importTheme(json): load external theme

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtasks)

---

### Task 6: DOM Factory Integration

Extend existing DOM Factory to read from UI Store:

- [ ] Icon rendering: getIcon(name) → uiStore.resolve('icon.' + name)
- [ ] String rendering: getString(key) → uiStore.resolve('string.' + key)
- [ ] Color tokens: getColor(token) → uiStore.resolve('color.' + token)
- [ ] CSS class resolution: getClass(name) → uiStore.resolve('css.' + name)
- [ ] All createElement methods use resolved values
- [ ] Subscribe to 'ui:changed' for live updates

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtasks)

---

### Task 7: Code Default Registration

Bootstrap process to sync code defaults into DB:

- [ ] Icons: scan icons.ts, register each icon SVG
- [ ] Strings: create strings manifest, register each
- [ ] Colors: scan CSS tokens, register each color variable
- [ ] Fonts: register typography tokens
- [ ] Spacing: register measurement tokens
- [ ] Run on app startup: sync code → DB (add new, preserve overrides)
- [ ] Version tracking: detect code changes, flag stale overrides

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/7 subtasks)

---

### Task 8: Live Update System

- [ ] UI Store emits 'ui:changed' with { key, oldValue, newValue }
- [ ] Components subscribe to keys they use
- [ ] Change triggers re-render of affected elements only
- [ ] Batch updates: collect changes, apply once per frame
- [ ] Integration with FrameScheduler: UI updates as RENDER intents

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/5 subtasks)

---

### Task 9: Customization UI

- [ ] UI Customization panel in settings
- [ ] Browse by category: Icons, Strings, Colors, Fonts
- [ ] Search across all UI elements
- [ ] Edit override inline, preview live
- [ ] Reset to default button per element
- [ ] Bulk reset by category
- [ ] Theme selector with preview
- [ ] Export/Import customizations

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/8 subtasks)

---

### Task 10: Plugin Integration

- [ ] Plugins register defaults via uiStore.registerDefault()
- [ ] Plugin overrides have priority 20 (below user, above theme)
- [ ] Plugin unload removes its overrides
- [ ] User can override plugin values (priority 100 wins)

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/4 subtasks)

---

## Resolution Priority

| Source | Priority | Wins Against |
|--------|----------|--------------|
| Code Default | 0 | nothing |
| Theme | 10 | default |
| Plugin | 20 | theme, default |
| User | 100 | everything |

Higher priority wins. Same priority: later timestamp wins.

---

## Key Categories

### Icons (icon.*)

```
icon.terminal → <svg>...</svg>
icon.settings → <svg>...</svg>
icon.close → <svg>...</svg>
```

User can replace any icon with custom SVG or image URL.

### Strings (string.*)

```
string.save → "Save"
string.cancel → "Cancel"
string.terminal.title → "Terminal"
```

Enables localization + user customization of any text.

### Colors (color.*)

```
color.primary → #3b82f6
color.background → #0a0a0a
color.text.primary → #ffffff
```

Maps to CSS custom properties. Override propagates to all usages.

### Fonts (font.*)

```
font.family.mono → "JetBrains Mono, monospace"
font.size.base → "14px"
font.weight.bold → "600"
```

### CSS (css.*)

```
css.button.primary → "bg-primary text-white rounded-md"
css.panel.header → "flex items-center gap-2 p-2"
```

Override entire class definitions.

---

## Integration Points

| System | Integration |
|--------|-------------|
| **DOM Factory** | All rendering reads from UI Store |
| **Settings History** | UI changes tracked in ui_history |
| **Layout System** | Visibility/sizes in separate layout_state |
| **Core Runtime** | UI updates emit RENDER intents |
| **Plugin System** | Plugins register + override via priority |

---

## Success Criteria

| Criteria | Validation |
|----------|------------|
| All icons replaceable | Change icon.terminal in DB, UI updates |
| All strings editable | Change string.save, button text changes |
| Colors themeable | Activate theme, all colors update |
| Undo works | Make change, undo, original restored |
| Plugins integrate | Plugin adds icon, user overrides it |
| Live updates | Change value, UI updates without reload |
| Export/Import | Export customizations, import on new install |
| No code reads | DOM Factory never reads code directly |

---

## The Spider Visualization

```
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│   CODE DEFAULTS          DB LAYER              DOM API                   │
│   (Left Legs)           (Body)                (Right Legs)               │
│                                                                          │
│   icons.ts ─────┐                         ┌───► Icon Rendering           │
│   strings.ts ───┤                         │                              │
│   colors.css ───┼──► ui_defaults ◄───────►├───► Text Rendering           │
│   fonts.css ────┤         │               │                              │
│   spacing.css ──┘         ▼               ├───► Style Application        │
│                    ┌─────────────┐        │                              │
│   USER OVERRIDE ──►│ Resolution  │────────┼───► Component Config         │
│   THEME ──────────►│   Engine    │        │                              │
│   PLUGIN ─────────►│             │        └───► CSS Classes              │
│                    └─────────────┘                                       │
│                          │                                               │
│                          ▼                                               │
│                    ui_history                                            │
│                    (undo stack)                                          │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

**Invariant**: DOM API is the ONLY consumer of resolved values. Code defines, DB stores, DOM renders.
