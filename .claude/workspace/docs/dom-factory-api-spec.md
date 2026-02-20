# DOM Factory API Specification

**Version**: 2.0 (v2 Production Infrastructure)
**Status**: Design Complete - Ready for Implementation
**Updated**: 2026-01-02

---

## Executive Summary

The DOM factory is the **SINGLE SOURCE OF TRUTH** for all UI element creation in v2. This specification defines the complete API surface required to eliminate all 152 detected DOM bypasses.

### Critical Statistics (From Scanner)

- **Total Bypasses**: 152 error-level violations
- **Files Affected**: 32 component files
- **Top Categories**: Buttons (74), Sidebars (36), Modals (15), Icons (11)
- **Token Violations**: 17 inline styles that should use CSS tokens

---

## Design Principles

1. **Pooling Integration**: All poolable elements use `Engine.pools.acquire()`
2. **Token-Based Styling**: All styling references CSS custom properties
3. **Accessibility First**: ARIA labels, keyboard navigation built-in
4. **Lifecycle Managed**: Event listeners tracked via BaseEventComponent pattern
5. **Type Safety**: Full TypeScript interfaces for all methods

---

## Current API (v1 - Already Implemented)

### Base Creation
- `DOM.createElement<K>(tag, options)` - Core factory with pooling
- `DOM.createFromHTML(html)` - Template string parsing
- `DOM.releaseElement(element)` - Return to pool
- `DOM.clear(element)` - Clear with pool release

### Basic Components
- `DOM.icon(options)` - SVG icons ✅
- `DOM.button(text, options)` - Buttons with variants ✅
- `DOM.iconButton(iconName, options)` - Icon-only buttons ✅
- `DOM.input(options)` - Text inputs ✅
- `DOM.searchInput(options)` - Search with icon ✅
- `DOM.header(text, options)` - Headers with icons ✅
- `DOM.card(options)` - Collapsible cards ✅
- `DOM.listItem(content, options)` - List items ✅
- `DOM.badge(text, options)` - Status badges ✅

### Layout
- `DOM.row(children, options)` - Flex row ✅
- `DOM.column(children, options)` - Flex column ✅

### Animation
- `DOM.animate(element, type)` - Apply animation ✅
- `DOM.fadeIn/fadeOut(element)` - Fade transitions ✅
- `DOM.slideIn(element, direction)` - Slide transitions ✅
- `DOM.staggerChildren(element)` - Stagger animation ✅

---

## Required API Extensions (v2)

### 1. Modal & Dialog System

```typescript
interface ModalOptions extends ElementOptions {
    title: string;
    content: HTMLElement | string;
    footer?: HTMLElement | string;
    width?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
    height?: 'auto' | 'sm' | 'md' | 'lg' | 'full';
    draggable?: boolean;
    resizable?: boolean;
    modal?: boolean; // Blocks interaction with rest of page
    closeOnEscape?: boolean;
    closeOnBackdrop?: boolean;
    onClose?: () => void;
    onOpen?: () => void;
}

DOM.modal(options: ModalOptions): HTMLDivElement
```

**Implementation Notes**:
- Extends `DraggableModal` pattern from `draggable-modal.ts`
- Manages z-index stack (uses `--z-modal` token)
- Integrates with attention system for focus management
- Handles backdrop creation and event management
- All event listeners registered via BaseEventComponent

**Usage Example**:
```typescript
const modal = DOM.modal({
    title: 'Settings',
    content: DOM.column([
        DOM.header('Environment Variables', { level: 3 }),
        DOM.input({ placeholder: 'KEY' }),
        DOM.input({ placeholder: 'VALUE' })
    ]),
    footer: DOM.row([
        DOM.button('Cancel', { variant: 'default' }),
        DOM.button('Save', { variant: 'primary' })
    ]),
    width: 'md',
    draggable: true
});
```

### 2. Sidebar System

```typescript
interface SidebarOptions extends ElementOptions {
    title?: string;
    collapsible?: boolean;
    collapsed?: boolean;
    side?: 'left' | 'right';
    width?: string; // CSS value with token support
    sections?: SidebarSection[];
    onToggle?: (collapsed: boolean) => void;
}

interface SidebarSection {
    title: string;
    icon?: IconName;
    collapsible?: boolean;
    collapsed?: boolean;
    items: HTMLElement[];
}

DOM.sidebar(options: SidebarOptions): HTMLDivElement
```

**Implementation Notes**:
- Creates collapse toggle button using `DOM.iconButton()`
- Section headers use `DOM.header()`
- Manages collapse state with CSS transitions (uses `--anim-duration-normal`)
- Persists collapse state to localStorage (optional)

**Usage Example**:
```typescript
const sidebar = DOM.sidebar({
    title: 'Terminal Sidebar',
    collapsible: true,
    side: 'left',
    width: 'var(--left-panel-width)',
    sections: [
        {
            title: 'Agents',
            icon: 'agent',
            collapsible: true,
            items: [
                DOM.listItem('Default Agent'),
                DOM.listItem('Test Agent')
            ]
        },
        {
            title: 'Commands',
            icon: 'command',
            items: [...]
        }
    ]
});
```

### 3. Form System

```typescript
interface FormOptions extends ElementOptions {
    fields: FormField[];
    onSubmit?: (data: Record<string, any>) => void | Promise<void>;
    submitLabel?: string;
    cancelLabel?: string;
    onCancel?: () => void;
}

interface FormField {
    name: string;
    label: string;
    type: 'text' | 'number' | 'password' | 'email' | 'select' | 'textarea' | 'checkbox' | 'radio';
    placeholder?: string;
    value?: any;
    required?: boolean;
    options?: Array<{ label: string; value: any }>; // For select/radio
    validate?: (value: any) => boolean | string; // true or error message
    help?: string; // Helper text below field
}

DOM.form(options: FormOptions): HTMLFormElement
DOM.field(field: FormField): HTMLDivElement
```

**Implementation Notes**:
- Each field wrapped in `DOM.column()` for consistent spacing
- Labels use `DOM.createElement('label')` with token typography
- Validation errors shown using `DOM.badge()` with error variant
- Submit button uses `DOM.button()` with primary variant
- Form submission prevents default and handles async/await

**Usage Example**:
```typescript
const form = DOM.form({
    fields: [
        {
            name: 'apiKey',
            label: 'API Key',
            type: 'password',
            required: true,
            validate: (val) => val.length >= 20 || 'API key must be at least 20 characters'
        },
        {
            name: 'model',
            label: 'Model',
            type: 'select',
            options: [
                { label: 'Claude Opus 4', value: 'opus-4' },
                { label: 'Claude Sonnet 4', value: 'sonnet-4' }
            ]
        }
    ],
    submitLabel: 'Save Configuration',
    onSubmit: async (data) => {
        await saveSettings(data);
    }
});
```

### 4. Table System

```typescript
interface TableOptions extends ElementOptions {
    columns: TableColumn[];
    rows: any[];
    selectable?: boolean;
    sortable?: boolean;
    onRowClick?: (row: any, index: number) => void;
    onSelectionChange?: (selected: any[]) => void;
}

interface TableColumn {
    key: string;
    label: string;
    width?: string;
    sortable?: boolean;
    render?: (value: any, row: any) => HTMLElement | string;
}

DOM.table(options: TableOptions): HTMLTableElement
```

**Implementation Notes**:
- Headers use `--color-bg-tertiary` background
- Rows alternate with `--color-bg-secondary` / `--color-bg-primary`
- Sort icons use `DOM.icon()` with chevron-up/down
- Row selection uses checkboxes from `DOM.input()` type checkbox (needs implementation)

### 5. Bar System (Header/Footer/Status)

```typescript
interface BarOptions extends ElementOptions {
    type: 'header' | 'footer' | 'status' | 'toolbar';
    left?: HTMLElement[];
    center?: HTMLElement[];
    right?: HTMLElement[];
    height?: string; // CSS token value
}

DOM.bar(options: BarOptions): HTMLDivElement
```

**Implementation Notes**:
- Creates three-section layout with `DOM.row()` and flex justify
- Height uses tokens: `--header-height`, `--status-bar-height`
- Applies appropriate classes for styling consistency

### 6. Panel System (Collapsible Content)

```typescript
interface PanelOptions extends ElementOptions {
    title: string;
    icon?: IconName;
    collapsible?: boolean;
    collapsed?: boolean;
    badge?: string; // Count badge
    actions?: HTMLElement[]; // Header action buttons
    onToggle?: (collapsed: boolean) => void;
}

DOM.panel(options: PanelOptions): HTMLDivElement
```

**Implementation Notes**:
- Extends `DOM.card()` with additional features
- Badge rendered with `DOM.badge()`
- Toggle icon uses `DOM.icon()` with chevron-down/right
- Actions rendered in header using `DOM.row()`

### 7. Enhanced Icon Support

```typescript
// Add to existing icon() method
interface IconOptions extends ElementOptions {
    name: IconName;
    size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
    color?: string; // Supports CSS tokens
    spin?: boolean; // For loading spinners
    pulse?: boolean; // For attention indicators
    badge?: string | number; // Notification badge
}

DOM.icon(options: IconOptions): HTMLSpanElement
```

**Implementation Notes**:
- Spin/pulse use CSS animation classes
- Badge rendered as overlay using `DOM.badge()`
- Color supports both hex and CSS custom property refs

---

## CSS Token Integration

### Color Usage
All color values MUST reference tokens from `tokens/colors.css`:

```typescript
// ❌ BAD
element.style.color = '#c9a227';

// ✅ GOOD
element.style.color = 'var(--color-accent-primary)';
```

### Spacing Usage
All spacing MUST reference tokens from `tokens/measurements.css`:

```typescript
// ❌ BAD
element.style.padding = '8px';

// ✅ GOOD
element.style.padding = 'var(--space-sm)';
```

### Token Reference Map

| Inline Style Pattern | Replacement Token |
|---------------------|-------------------|
| `color: #ccc` | `var(--color-text-primary)` |
| `background: #1e1e1e` | `var(--color-bg-primary)` |
| `padding: 8px` | `var(--space-sm)` |
| `margin: 12px` | `var(--space-md)` |
| `font-size: 14px` | `var(--font-size-base)` |
| `border-radius: 4px` | `var(--radius-sm)` |
| `width: 16px` (icon) | `var(--icon-size-md)` |
| `height: 32px` (button) | `var(--button-height-md)` |

---

## Accessibility Integration

All factory methods MUST implement:

1. **ARIA Labels**:
   ```typescript
   DOM.button('Save', {
       attributes: {
           'aria-label': 'Save configuration',
           'role': 'button'
       }
   });
   ```

2. **Keyboard Navigation**:
   - All interactive elements focusable (tabindex)
   - Enter/Space for activation
   - Escape to close modals/dropdowns

3. **Focus Management**:
   - Modals trap focus inside
   - Restore focus on close
   - Visible focus indicators

4. **Screen Reader Support**:
   - Meaningful labels
   - State announcements (collapsed/expanded)
   - Loading state announcements

---

## Event Lifecycle Management

All event listeners MUST be managed via BaseEventComponent pattern:

```typescript
// ❌ BAD
element.addEventListener('click', handler);

// ✅ GOOD
class MyComponent extends BaseEventComponent {
    protected onInitialize() {
        this.registerEvent(element, 'click', handler);
    }

    protected onDestroy() {
        // Events automatically cleaned up
    }
}
```

For factory-created elements:
```typescript
DOM.button('Click Me', {
    onClick: (e) => {
        // Handler is registered and will be cleaned up
        // when element is released to pool
    }
});
```

---

## Migration Priority Order

Based on scanner analysis, migrate in this order:

### Phase 1: Icons (11 bypasses, 4 files) - **EASIEST**
- Files: `taskbar.ts`, `header-bar.ts`, `terminal-header.ts`, `ai-footer.ts`
- Pattern: Replace `icon.innerHTML = getIconSvg(name)` with `DOM.icon({ name })`
- Estimated effort: 2 hours

### Phase 2: Buttons (74 bypasses, 17 files) - **HIGHEST IMPACT**
- Top files: `ai-sidebar.ts` (23), `terminal-sidebar.ts` (22), `claude-panel.ts` (19)
- Pattern: Replace `btn.innerHTML = iconSvg` with `DOM.iconButton(name, options)`
- Estimated effort: 1-2 days

### Phase 3: Forms (2 bypasses, 2 files)
- Files: `claude-form-builder.ts`, `ai-footer.ts`
- Pattern: Implement `DOM.field()` and migrate
- Estimated effort: 4 hours

### Phase 4: Modals (15 bypasses, 6 files)
- Files: `ai-provider-dialog.ts`, `environment-modal.ts`, etc.
- Pattern: Implement `DOM.modal()` first, then migrate
- Estimated effort: 1 day

### Phase 5: Sidebars (36 bypasses, 5 files)
- Files: `ai-sidebar.ts`, `terminal-sidebar.ts`, `claude-panel.ts`
- Pattern: Implement `DOM.sidebar()` with section support
- Estimated effort: 1-2 days

### Phase 6: Headers/Bars (3 bypasses, 2 files)
- Files: `header-bar.ts`, `terminal-header.ts`
- Pattern: Implement `DOM.bar()` for structured headers
- Estimated effort: 4 hours

### Phase 7: Uncategorized (15 bypasses, 12 files)
- Review each case individually
- May require new factory methods or custom handling
- Estimated effort: 1 day

**Total Estimated Effort**: 5-7 days for complete migration

---

## Implementation Checklist

- [ ] **Phase 1: Extend DOM Factory**
  - [ ] Implement `DOM.modal()` with DraggableModal pattern
  - [ ] Implement `DOM.sidebar()` with section support
  - [ ] Implement `DOM.form()` and `DOM.field()`
  - [ ] Implement `DOM.table()` with sorting/selection
  - [ ] Implement `DOM.bar()` for headers/footers
  - [ ] Implement `DOM.panel()` for collapsible sections
  - [ ] Extend `DOM.icon()` with spin/pulse/badge

- [ ] **Phase 2: Migrate Components** (See priority order above)
  - [ ] Migrate icons (Phase 1)
  - [ ] Migrate buttons (Phase 2)
  - [ ] Migrate forms (Phase 3)
  - [ ] Migrate modals (Phase 4)
  - [ ] Migrate sidebars (Phase 5)
  - [ ] Migrate headers (Phase 6)
  - [ ] Review uncategorized (Phase 7)

- [ ] **Phase 3: Validation**
  - [ ] Re-run `find-dom-bypasses.cjs` → expect 0 errors
  - [ ] Verify all CSS tokens used (no inline hex/px)
  - [ ] Test element pooling (check pool metrics)
  - [ ] Accessibility audit (screen reader, keyboard nav)
  - [ ] Performance benchmarks (pool hit rate)

- [ ] **Phase 4: Documentation**
  - [ ] Update component documentation
  - [ ] Add JSDoc comments to all new methods
  - [ ] Create migration guide for future components
  - [ ] Update architecture docs with factory pattern

---

## Success Metrics

After full migration:

1. **Zero DOM Bypasses**: `find-dom-bypasses.cjs` returns 0 errors
2. **Token Compliance**: No inline `#hex` or `Npx` values (except base tokens)
3. **Pool Efficiency**: >80% of `div/span/button` from pool
4. **Consistency**: All buttons use same variant system
5. **Accessibility**: WCAG 2.1 AA compliance
6. **Maintainability**: New components auto-comply by using factory

---

## References

- **Current Implementation**: `D:\GIT\archlab\root\archlab-ide\src\renderer\utils\dom-factory.ts`
- **Bypass Report**: `D:\GIT\archlab\.claude\workspace\tools\dom-bypasses.json`
- **Categorization Report**: `D:\GIT\archlab\.claude\workspace\tools\dom-centralization-report.json`
- **CSS Tokens**: `D:\GIT\archlab\root\archlab-ide\src\renderer\styles\tokens\`
- **Base Classes**: BaseEventComponent, BaseRenderComponent, BasePooledResource
