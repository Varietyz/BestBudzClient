# ARCHLAB Layout Customization System

**Version**: 1.1
**Created**: 2025-12-30
**Type**: UI Customization Infrastructure
**Depends On**: Core Runtime (base classes), Settings System (persistence)

---

## Overview

Full layout customization: resize + visibility for all rendering components.

**Key Principles**:
- REM-based sizing for consistent scaling
- Component visibility configurable per-user
- Minimalist mode: hide what you don't need
- Layout state (size + visibility) persists in app.db
- Integrates with settings presets

---

## Verified Foundation

| Asset | Status | Location |
|-------|--------|----------|
| REM tokens | EXISTS | measurements.css — all values in rem |
| DOM factory | EXISTS | dom-factory.ts — centralized element creation |
| Panel min/max | EXISTS | --left-panel-min, --left-panel-max tokens |
| Settings persistence | EXISTS | settings-store.ts → app.db |

---

## Architecture

### ResizableComponent Base (or Mixin)

| Config | Type | Purpose |
|--------|------|---------|
| edges | 'all' \| 'horizontal' \| 'vertical' \| 'right' \| 'bottom' \| etc. | Which edges are resizable |
| minSize | { width: rem, height: rem } | Minimum dimensions |
| maxSize | { width: rem, height: rem } \| 'content' \| 'viewport' | Maximum dimensions |
| persistKey | string | Key for layout persistence |

### Min/Max Strategies

| Strategy | Behavior |
|----------|----------|
| **Fixed** | minSize/maxSize in rem, set at component level |
| **Content-based** | maxSize: 'content' calculates from scrollHeight/scrollWidth |
| **Free-form** | No max constraint, user resizes freely |
| **Viewport-relative** | maxSize: '80vw' or '50vh' relative to viewport |

### Database Schema

| Table | Columns | Purpose |
|-------|---------|---------|
| layout_state | id, component_key, width, height, visible, updated_at | Per-component dimensions + visibility |
| layout_presets | id, name, layout_json, created_at | Named layout snapshots (size + visibility) |

### Visibility States

| State | Behavior |
|-------|----------|
| visible: true | Component renders normally |
| visible: false | Component hidden, not rendered (saves resources) |
| visible: 'collapsed' | Component renders minimized (header only, expandable) |

---

## Tasks

### Task 1: Database Migration

- [ ] Create migration for layout_state table
- [ ] Create migration for layout_presets table
- [ ] Index on component_key for fast lookups

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/3 subtasks)

---

### Task 2: Layout Store

- [ ] Create layout-store.ts following settings-store.ts pattern
- [ ] getLayout(key): { width, height, visible } | undefined
- [ ] setLayout(key, { width?, height?, visible? }): debounced persist
- [ ] setVisibility(key, visible): shorthand for visibility-only changes
- [ ] resetLayout(key): delete entry, component uses defaults
- [ ] getAllLayouts(): full layout state for preset export
- [ ] getVisibleComponents(): list of component_keys where visible !== false

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/7 subtasks)

---

### Task 3: Visibility Management

- [ ] isVisible(key): boolean check for rendering decisions
- [ ] show(key), hide(key), toggle(key): visibility controls
- [ ] collapse(key), expand(key): for collapsed state
- [ ] Component respects visibility on init: if hidden, don't render
- [ ] Hidden components don't participate in frame loop (resource savings)
- [ ] Visibility changes emit layout intent (optional frame integration)

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtasks)

---

### Task 4: DOM Factory Extension

- [ ] createResizeHandle(edge, onResize): HTMLElement with grip styling
- [ ] wrapResizable(element, config): adds handles based on edges config
- [ ] CSS for resize cursors: ew-resize, ns-resize, nwse-resize, nesw-resize
- [ ] Handle positioning: absolute on edges, z-index above content

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/4 subtasks)

---

### Task 5: LayoutComponent Base

- [ ] Create base/layout-component.ts (combines resize + visibility)
- [ ] Config: { edges, minSize, maxSize, persistKey, defaultVisible }
- [ ] applyResize(width, height): validates against min/max, applies rem values
- [ ] applyVisibility(visible): show/hide/collapse component
- [ ] onResizeStart(), onResize(), onResizeEnd() lifecycle
- [ ] onVisibilityChange(visible) lifecycle hook
- [ ] Debounced persist: 300ms after resize/visibility change
- [ ] restoreLayout(): reads size + visibility from layout-store on init

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/8 subtasks)

---

### Task 6: Content-Based Sizing

- [ ] getContentSize(): reads scrollWidth/scrollHeight
- [ ] Auto-fit on double-click edge (if maxSize: 'content')
- [ ] Viewport-relative calculations for 'viewport' strategy
- [ ] Clamp to viewport bounds to prevent off-screen

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/4 subtasks)

---

### Task 7: Layout Presets

- [ ] saveLayoutPreset(name): snapshot all layout_state entries
- [ ] loadLayoutPreset(name): apply preset, trigger resize on all components
- [ ] listLayoutPresets(): return available presets
- [ ] deleteLayoutPreset(name): remove preset
- [ ] Integration with settings presets (Task 4.4): include layout in full snapshot

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/5 subtasks)

---

### Task 8: Visibility UI

- [ ] Component Customization panel in settings: list all hideable components
- [ ] Toggle switches for each component visibility
- [ ] "Minimalist Mode" preset: hides non-essential UI
- [ ] "Full Mode" preset: shows all components
- [ ] Context menu option on components: "Hide this panel"
- [ ] Quick-show menu: list hidden components, click to restore

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtasks)

---

### Task 9: Resize UI/UX

- [ ] Visual resize handles on hover (subtle grip indicators)
- [ ] Keyboard resize support: arrow keys when handle focused (accessibility)
- [ ] Layout reset button in settings panel
- [ ] Resize preview guides during drag

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/4 subtasks)

---

### Task 10: Component Integration

Components with layout customization:

| Component | Edges | Default Size | Default Visible | Persist Key | Hideable |
|-----------|-------|--------------|-----------------|-------------|----------|
| Terminal modal | all | 600x400 | true | 'terminal-modal' | yes |
| Settings panel | right, bottom | 400x500 | true | 'settings-panel' | no (core) |
| Hardware panel | right, bottom | 350x450 | true | 'hardware-panel' | yes |
| Debug panel | all | 500x300 | false | 'debug-panel' | yes |
| FPS counter | none | auto | true | 'fps-counter' | yes |
| Clock | none | auto | true | 'clock' | yes |
| Future sidebar | right | 300xfull | true | 'sidebar' | yes |

- [ ] Audit components for layout candidacy (resize + visibility)
- [ ] Extend/compose with LayoutComponent
- [ ] Add persistKey and defaultVisible to each
- [ ] Mark non-hideable components (core UI that must remain)
- [ ] Test resize + visibility + persist + restore cycle

**Progress**: ░░░░░░░░░░░░░░░░░░░░ 0% (0/5 subtasks)

---

## REM Consistency

All sizes stored and applied in rem:
- Scaling html { font-size } scales entire layout proportionally
- Enables accessibility zoom without breaking layout
- Calculations: pixelValue / 16 = remValue

---

## Integration Points

| System | Integration |
|--------|-------------|
| **Core Runtime** | Layout changes emit intent (optional, for frame-aware updates) |
| **Settings History** | Layout changes tracked with source: 'resize' or 'visibility' |
| **Settings Presets** | Layout (size + visibility) included in full preset snapshots |
| **AttentionParticipant** | Hidden components get attention: 0 (no CPU time) |
| **FrameSystem** | Hidden components unregister from frame loop |

---

## Success Criteria

| Criteria | Validation |
|----------|------------|
| Any panel resizable | Edge handles appear, drag resizes |
| Any panel hideable | Toggle visibility, component disappears/reappears |
| Sizes persist | Close + reopen app, sizes restored |
| Visibility persists | Close + reopen app, hidden panels stay hidden |
| Min/max respected | Cannot resize below min or above max |
| Presets work | Save layout, reset, load preset, layout + visibility restored |
| REM consistent | Change root font-size, layout scales proportionally |
| Hidden = no CPU | Hidden components don't participate in frame loop |
| Minimalist mode | Single toggle hides all non-essential UI |
