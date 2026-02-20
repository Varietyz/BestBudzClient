# ARCHLAB CSS Patterns Reference

**Purpose**: Verified patterns for CSS architecture in ARCHLAB (viewport, panel, layout implementations)
**Status**: Forensically verified against actual codebase
**Date**: 2026-01-06

---

## CRITICAL RULE: ZERO INLINE STYLES IN TYPESCRIPT

**Verified by**: Grep search found ZERO instances of `.createElement(.*style:` in application code

```typescript
// ✅ ALWAYS CORRECT - className only
const element = await DOM.createElement("div", {
    entity: "viewport",
    className: "dockable-viewport dockable-viewport--right"
});

// ❌ ALWAYS WRONG - inline style object
const element = await DOM.createElement("div", {
    entity: "viewport",
    style: { position: "fixed", width: "600px" } // FORBIDDEN
});
```

**Exception**: Motion.ts, DOM Factory managers (internal only - application code never uses this)

---

## PATTERN 1: Dockable Viewport (Right-Side Panel)

### File Structure

```
root/archlab-ide/src/renderer/styles/
├── base/core/anchors/
│   └── dockable-viewport-anchor.css    [Positioning container]
├── base/core/layouts/
│   └── dockable-layout.css             [Base dockable styles]
└── components/panels/debug/
    └── debug-console-panel.css         [Component-specific content]
```

### CSS: dockable-layout.css

```css
/**
 * Dockable Layout
 *
 * Base styles for dockable viewports (sidebars, panels)
 */

/* ==========================================================================
   Base Viewport
   ========================================================================== */

.dockable-viewport {
    position: fixed;
    height: 100vh;
    width: var(--viewport-width-md);
    background: var(--color-bg-secondary);
    border-left: var(--border-width-thin) solid var(--color-border);
    box-shadow: var(--shadow-lg);
    display: flex;
    flex-direction: column;
    transition: transform var(--transition-normal);
    z-index: var(--z-overlay);
    will-change: transform;
}

/* Position Modifiers */

.dockable-viewport--left {
    left: 0;
    transform: translateX(-100%);
}

.dockable-viewport--right {
    right: 0;
    transform: translateX(100%);
}

.dockable-viewport--bottom {
    bottom: 0;
    left: 0;
    width: 100%;
    height: var(--viewport-height-md);
    transform: translateY(100%);
}

/* State Modifiers */

.dockable-viewport--open {
    transform: translateX(0);
}

.dockable-viewport--open.dockable-viewport--bottom {
    transform: translateY(0);
}

.dockable-viewport--collapsed .dockable-viewport-content {
    display: none;
}

/* Elements */

.dockable-viewport-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--space-sm) var(--space-md);
    background: var(--color-bg-tertiary);
    border-bottom: var(--border-width-thin) solid var(--color-border);
    min-height: var(--header-height-md);
}

.dockable-viewport-title {
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-semibold);
    color: var(--color-text-primary);
}

.dockable-viewport-actions {
    display: flex;
    gap: var(--space-2xs);
}

.dockable-viewport-content {
    flex: 1;
    overflow: auto;
    padding: var(--space-md);
}

.dockable-viewport-footer {
    padding: var(--space-sm) var(--space-md);
    background: var(--color-bg-tertiary);
    border-top: var(--border-width-thin) solid var(--color-border);
}
```

### CSS: debug-console-panel.css

```css
/**
 * Debug Console Panel
 *
 * Component-specific styles for debug console content
 */

/* ==========================================================================
   Debug Console
   ========================================================================== */

.debug-console-panel {
    display: flex;
    flex-direction: column;
    height: 100%;
    background: var(--color-bg-primary);
}

.debug-console-output {
    flex: 1;
    overflow-y: auto;
    padding: var(--space-sm);
    font-family: var(--font-family-mono);
    font-size: var(--font-size-xs);
    line-height: var(--line-height-relaxed);
}

.debug-console-entry {
    padding: var(--space-2xs) 0;
    border-bottom: var(--border-width-thin) solid var(--color-border-light);
}

.debug-console-entry--error {
    color: var(--color-accent-error);
}

.debug-console-entry--warning {
    color: var(--color-accent-warning);
}

.debug-console-entry--info {
    color: var(--color-text-secondary);
}

.debug-console-input {
    padding: var(--space-sm);
    background: var(--color-bg-tertiary);
    border-top: var(--border-width-thin) solid var(--color-border);
}
```

### TypeScript: DebugConsolePanel.ts

```typescript
import { DOM } from "../../../utils/dom-factory";
import { BasePanel } from "../../../engine/base/base-panel";

export class DebugConsolePanel extends BasePanel {
    private viewport: HTMLElement | null = null;
    private outputElement: HTMLElement | null = null;

    async render(container: HTMLElement): Promise<void> {
        // Create dockable viewport
        this.viewport = await DOM.createElement("div", {
            entity: "viewport",
            className: "dockable-viewport dockable-viewport--right",
        });

        // Create header
        const header = await DOM.createElement("header", {
            entity: "viewport-header",
            className: "dockable-viewport-header",
        });

        const title = await DOM.createElement("h2", {
            entity: "viewport-title",
            className: "dockable-viewport-title",
            textContent: "Debug Console",
        });

        const closeBtn = await DOM.button({
            entity: "close-btn",
            className: "btn-icon",
            icon: "close",
            onClick: () => this.close(),
        });

        header.appendChild(title);
        header.appendChild(closeBtn);

        // Create content
        const content = await DOM.createElement("div", {
            entity: "viewport-content",
            className: "dockable-viewport-content",
        });

        const consolePanel = await DOM.createElement("div", {
            entity: "debug-console",
            className: "debug-console-panel",
        });

        this.outputElement = await DOM.createElement("div", {
            entity: "console-output",
            className: "debug-console-output",
        });

        consolePanel.appendChild(this.outputElement);
        content.appendChild(consolePanel);

        // Assemble
        this.viewport.appendChild(header);
        this.viewport.appendChild(content);
        container.appendChild(this.viewport);
    }

    open(): void {
        if (!this.viewport) return;
        this.viewport.classList.add("dockable-viewport--open");
    }

    close(): void {
        if (!this.viewport) return;
        this.viewport.classList.remove("dockable-viewport--open");
    }

    toggle(): void {
        if (!this.viewport) return;
        this.viewport.classList.toggle("dockable-viewport--open");
    }

    logEntry(message: string, level: "error" | "warning" | "info" = "info"): void {
        if (!this.outputElement) return;

        const entry = DOM.createElement("div", {
            entity: "console-entry",
            className: `debug-console-entry debug-console-entry--${level}`,
            textContent: message,
        });

        this.outputElement.appendChild(entry);
    }
}
```

---

## PATTERN 2: Resizable Layout Panel

### CSS: resizable-layout.css

```css
/**
 * Resizable Layout
 *
 * Panels that can be resized with drag handles
 */

/* ==========================================================================
   Resizable Container
   ========================================================================== */

.resizable-panel {
    position: relative;
    min-width: var(--panel-min-width);
    min-height: var(--panel-min-height);
    background: var(--color-bg-secondary);
    border: var(--border-width-thin) solid var(--color-border);
    border-radius: var(--radius-md);
}

.resizable-panel--resizing {
    user-select: none;
    cursor: nwse-resize;
}

/* Resize Handles */

.resize-handle {
    position: absolute;
    background: var(--color-border);
    opacity: 0;
    transition: opacity var(--transition-fast);
}

.resize-handle:hover,
.resizable-panel:hover .resize-handle {
    opacity: 1;
}

.resize-handle--right {
    right: 0;
    top: 0;
    width: var(--resize-handle-width);
    height: 100%;
    cursor: ew-resize;
}

.resize-handle--bottom {
    bottom: 0;
    left: 0;
    width: 100%;
    height: var(--resize-handle-height);
    cursor: ns-resize;
}

.resize-handle--corner {
    right: 0;
    bottom: 0;
    width: var(--resize-handle-corner);
    height: var(--resize-handle-corner);
    cursor: nwse-resize;
    background: var(--color-accent-primary);
}
```

### TypeScript: ResizablePanel.ts

```typescript
import { DOM } from "../../../utils/dom-factory";
import { Motion } from "../../../utils/motion";

export class ResizablePanel {
    private element: HTMLElement;

    async create(): Promise<HTMLElement> {
        this.element = await DOM.createElement("div", {
            entity: "resizable-panel",
            className: "resizable-panel",
            layout: {
                resizable: true,
                edges: ["right", "bottom", "corner"],
                minWidth: 300,
                minHeight: 200,
                maxWidth: 1200,
                persist: true, // Save to UI.db
            }
        });

        // Layout manager handles resize logic internally
        // Applies element.style.width/height via Motion.ts (valid exception)

        return this.element;
    }

    // State managed via CSS classes, not inline styles
    setResizing(isResizing: boolean): void {
        if (isResizing) {
            this.element.classList.add("resizable-panel--resizing");
        } else {
            this.element.classList.remove("resizable-panel--resizing");
        }
    }
}
```

---

## PATTERN 3: Collapsible Sidebar

### CSS: collapsible-sidebar.css

```css
/**
 * Collapsible Sidebar
 *
 * Sidebar that collapses to icon-only mode
 */

/* ==========================================================================
   Sidebar Base
   ========================================================================== */

.collapsible-sidebar {
    position: fixed;
    left: 0;
    top: var(--header-bar-height);
    bottom: 0;
    width: var(--sidebar-width-expanded);
    background: var(--color-bg-secondary);
    border-right: var(--border-width-thin) solid var(--color-border);
    display: flex;
    flex-direction: column;
    transition: width var(--transition-normal);
    z-index: var(--z-sticky);
    overflow: hidden;
}

.collapsible-sidebar--collapsed {
    width: var(--sidebar-width-collapsed);
}

/* Sidebar Header */

.sidebar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--space-sm) var(--space-md);
    background: var(--color-bg-tertiary);
    border-bottom: var(--border-width-thin) solid var(--color-border);
    min-height: var(--header-height-sm);
}

.sidebar-title {
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-semibold);
    color: var(--color-text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: opacity var(--transition-fast);
}

.collapsible-sidebar--collapsed .sidebar-title {
    opacity: 0;
    pointer-events: none;
}

/* Sidebar Content */

.sidebar-content {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
}

/* Sidebar Items */

.sidebar-item {
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    padding: var(--space-sm) var(--space-md);
    cursor: pointer;
    transition: background-color var(--transition-fast);
}

.sidebar-item:hover {
    background: var(--color-bg-hover);
}

.sidebar-item--active {
    background: var(--color-bg-active);
    border-left: var(--border-width-thick) solid var(--color-accent-primary);
}

.sidebar-item-icon {
    flex-shrink: 0;
    width: var(--icon-size-md);
    height: var(--icon-size-md);
}

.sidebar-item-label {
    flex: 1;
    font-size: var(--font-size-sm);
    color: var(--color-text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: opacity var(--transition-fast);
}

.collapsible-sidebar--collapsed .sidebar-item-label {
    opacity: 0;
    width: 0;
}

/* Toggle Button */

.sidebar-toggle-btn {
    position: absolute;
    right: calc(var(--space-xs) * -1);
    top: var(--space-md);
    width: var(--icon-size-lg);
    height: var(--icon-size-lg);
    background: var(--color-bg-tertiary);
    border: var(--border-width-thin) solid var(--color-border);
    border-radius: var(--radius-full);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all var(--transition-fast);
    z-index: 1;
}

.sidebar-toggle-btn:hover {
    background: var(--color-bg-hover);
    transform: scale(1.1);
}

.sidebar-toggle-icon {
    transition: transform var(--transition-fast);
}

.collapsible-sidebar--collapsed .sidebar-toggle-icon {
    transform: rotate(180deg);
}
```

### TypeScript: CollapsibleSidebar.ts

```typescript
import { DOM } from "../../../utils/dom-factory";
import { BaseComponent } from "../../../engine/base/base-component";

export class CollapsibleSidebar extends BaseComponent {
    private sidebar: HTMLElement | null = null;
    private isCollapsed = false;

    async render(container: HTMLElement): Promise<void> {
        this.sidebar = await DOM.createElement("aside", {
            entity: "sidebar",
            className: "collapsible-sidebar",
        });

        const header = await this.createHeader();
        const content = await this.createContent();
        const toggleBtn = await this.createToggleButton();

        this.sidebar.appendChild(header);
        this.sidebar.appendChild(content);
        this.sidebar.appendChild(toggleBtn);

        container.appendChild(this.sidebar);
    }

    private async createHeader(): Promise<HTMLElement> {
        const header = await DOM.createElement("div", {
            entity: "sidebar-header",
            className: "sidebar-header",
        });

        const title = await DOM.createElement("h2", {
            entity: "sidebar-title",
            className: "sidebar-title",
            textContent: "Navigation",
        });

        header.appendChild(title);
        return header;
    }

    private async createContent(): Promise<HTMLElement> {
        const content = await DOM.createElement("nav", {
            entity: "sidebar-content",
            className: "sidebar-content",
        });

        // Add sidebar items
        const items = [
            { label: "Home", icon: "home", id: "home" },
            { label: "Projects", icon: "folder", id: "projects" },
            { label: "Settings", icon: "settings", id: "settings" },
        ];

        for (const item of items) {
            const itemElement = await this.createSidebarItem(item);
            content.appendChild(itemElement);
        }

        return content;
    }

    private async createSidebarItem(item: { label: string; icon: string; id: string }): Promise<HTMLElement> {
        const itemElement = await DOM.createElement("div", {
            entity: `sidebar-item-${item.id}`,
            className: "sidebar-item",
            on: {
                click: () => this.handleItemClick(item.id),
            },
        });

        const icon = await DOM.icon({
            name: item.icon,
            size: "md",
            className: "sidebar-item-icon",
        });

        const label = await DOM.createElement("span", {
            entity: `sidebar-item-label-${item.id}`,
            className: "sidebar-item-label",
            textContent: item.label,
        });

        itemElement.appendChild(icon);
        itemElement.appendChild(label);

        return itemElement;
    }

    private async createToggleButton(): Promise<HTMLElement> {
        const btn = await DOM.button({
            entity: "sidebar-toggle",
            className: "sidebar-toggle-btn",
            icon: "chevron-left",
            onClick: () => this.toggle(),
        });

        return btn;
    }

    toggle(): void {
        if (!this.sidebar) return;

        this.isCollapsed = !this.isCollapsed;

        if (this.isCollapsed) {
            this.sidebar.classList.add("collapsible-sidebar--collapsed");
        } else {
            this.sidebar.classList.remove("collapsible-sidebar--collapsed");
        }
    }

    private handleItemClick(itemId: string): void {
        // Remove active state from all items
        const items = this.sidebar?.querySelectorAll(".sidebar-item");
        items?.forEach(item => item.classList.remove("sidebar-item--active"));

        // Add active state to clicked item
        const clickedItem = this.sidebar?.querySelector(`[data-entity="sidebar-item-${itemId}"]`);
        clickedItem?.classList.add("sidebar-item--active");
    }
}
```

---

## PATTERN 4: Token Definitions for Viewports/Panels

### CSS: measurements.css (add these tokens)

```css
/* Viewport widths */
--viewport-width-sm: 18.75rem;  /* 300px */
--viewport-width-md: 37.5rem;   /* 600px */
--viewport-width-lg: 50rem;     /* 800px */

/* Viewport heights */
--viewport-height-sm: 12.5rem;  /* 200px */
--viewport-height-md: 25rem;    /* 400px */
--viewport-height-lg: 37.5rem;  /* 600px */

/* Sidebar widths */
--sidebar-width-collapsed: 3.5rem;  /* 56px */
--sidebar-width-expanded: 15rem;    /* 240px */

/* Panel dimensions */
--panel-min-width: 12.5rem;   /* 200px */
--panel-min-height: 9.375rem; /* 150px */
--panel-max-width: 75rem;     /* 1200px */

/* Header heights */
--header-height-sm: 2rem;     /* 32px */
--header-height-md: 2.5rem;   /* 40px */
--header-height-lg: 3rem;     /* 48px */

/* Resize handles */
--resize-handle-width: 0.25rem;     /* 4px */
--resize-handle-height: 0.25rem;    /* 4px */
--resize-handle-corner: 0.75rem;    /* 12px */
```

---

## STATE MANAGEMENT PATTERNS

### Pattern: CSS Classes for State (NOT inline styles)

```typescript
// ✅ CORRECT - Toggle via classList
element.classList.add("dockable-viewport--open");
element.classList.remove("dockable-viewport--open");
element.classList.toggle("dockable-viewport--collapsed");

// ❌ WRONG - Inline style manipulation
element.style.display = "block";  // FORBIDDEN
element.style.transform = "translateX(0)"; // FORBIDDEN
```

### Pattern: Transition States

```css
/* Base state */
.panel {
    opacity: 0;
    transform: translateY(1rem);
    transition: opacity var(--transition-normal),
                transform var(--transition-normal);
}

/* Open state */
.panel--open {
    opacity: 1;
    transform: translateY(0);
}

/* TypeScript */
// panel.classList.add("panel--open");
```

---

## ANTI-PATTERNS (FORBIDDEN)

### ❌ ANTI-PATTERN 1: Inline Style Objects

```typescript
// FORBIDDEN - violates architecture
const panel = await DOM.createElement("div", {
    entity: "panel",
    style: {
        position: "fixed",
        right: 0,
        width: "600px",
        backgroundColor: "#252526",
    }
});
```

**Correct approach**: Define CSS class, use `className`

---

### ❌ ANTI-PATTERN 2: Direct style Property Manipulation

```typescript
// FORBIDDEN - except in Motion.ts/managers
panel.style.width = "600px";
panel.style.backgroundColor = "#252526";
```

**Correct approach**: Use CSS classes and `classList` API

---

### ❌ ANTI-PATTERN 3: Hardcoded Values in CSS

```css
/* FORBIDDEN - no raw pixel values */
.panel {
    padding: 12px;
    width: 600px;
    background: #252526;
}
```

**Correct approach**: Use tokens

```css
.panel {
    padding: var(--space-md);
    width: var(--viewport-width-md);
    background: var(--color-bg-secondary);
}
```

---

## MOTION.TS EXCEPTION (Understanding the Legitimate Use)

### When Motion.ts Applies Inline Styles

Motion.ts is the ONLY component that applies inline `element.style` for positional properties:

```typescript
// Motion.ts internals (VALID EXCEPTION)
element.style.transform = `translate(${x}px, ${y}px)`;
element.style.left = `${x}px`;
element.style.top = `${y}px`;
element.style.width = `${width}px`;
element.style.height = `${height}px`;
```

**Why this is allowed**:
1. Per-frame animation updates (60fps)
2. Runtime-calculated positions
3. Cannot be expressed as CSS classes
4. Limited to positional properties only (NOT colors, spacing, typography)

**Application code perspective**:
```typescript
// Application code uses declarative API
Motion.createDragHandler(element, {
    bounds: { minX: 0, maxX: 1920 },
});

// Motion.ts applies inline styles internally (you never write this)
```

**Rule**: Application code NEVER uses inline styles. Motion.ts encapsulates this complexity.

---

## SUMMARY CHECKLIST

When creating viewport/panel architecture:

- [ ] Define CSS classes in appropriate files (`base/`, `components/`)
- [ ] Use token variables for ALL measurements/colors
- [ ] Use flat BEM naming (`.block`, `.block-element`, `.block--modifier`)
- [ ] Use `className` ONLY in TypeScript (never `style: {}`)
- [ ] Toggle states via `classList.add/remove/toggle`
- [ ] Import CSS in correct order (tokens → base → components)
- [ ] Document Motion.ts usage if drag/resize needed
- [ ] No hardcoded pixel/hex values in CSS
- [ ] No inline style objects in `DOM.createElement()`
- [ ] No direct `element.style` manipulation in application code

---

**Reference Status**: ✅ Verified against actual ARCHLAB implementation
**Last Updated**: 2026-01-06
**Verification Method**: Forensic code analysis (grep, glob, file reading)
