# DOM Factory Migration Manifest

**Generated**: 2026-01-02
**Status**: Ready for Execution
**Total Bypasses**: 152 errors across 32 files

---

## Quick Stats

| Category | Count | Files | Priority | Estimated Hours |
|----------|-------|-------|----------|-----------------|
| Icons | 11 | 4 | 1 (Easiest) | 2 |
| Buttons | 74 | 17 | 1 (High Impact) | 16 |
| Forms | 2 | 2 | 2 | 4 |
| Modals | 15 | 6 | 3 | 8 |
| Sidebars | 36 | 5 | 4 | 16 |
| Headers | 3 | 2 | 5 | 4 |
| Uncategorized | 15 | 12 | 6 | 8 |
| **TOTAL** | **156** | **32** | - | **58 hrs** |

---

## Phase 1: Icon Migration (11 bypasses, ~2 hours)

### Files to Migrate

#### 1. `taskbar.ts` (9 bypasses)
**Lines**: 281, 315, 328, 337, 350, 363, 376, 433, 458

**Current Pattern**:
```typescript
homeBtn.innerHTML = taskbarIcons.home;
this.terminalBtn.innerHTML = taskbarIcons.terminal;
notificationsBtn.innerHTML = taskbarIcons.bell;
```

**Migration**:
```typescript
// Replace innerHTML assignments with DOM.icon()
import { DOM } from '../utils/dom-factory';

// Before
homeBtn.innerHTML = taskbarIcons.home;

// After
const homeIcon = DOM.icon({ name: 'home', size: 'md' });
DOM.clear(homeBtn);
homeBtn.appendChild(homeIcon);
```

**Token Violations**: None
**Estimated Time**: 1 hour

---

#### 2. `header-bar.ts` (6 bypasses)
**Lines**: 139, 148, 159, 168, 208, 216

**Current Pattern**:
```typescript
minimizeBtn.innerHTML = headerIcons.hide;
this.maximizeBtn.innerHTML = this.isMaximized ? headerIcons.restore : headerIcons.maximize;
```

**Migration**:
```typescript
// Dynamic icon updates
const minimizeIcon = DOM.icon({ name: 'hide', size: 'sm' });
minimizeBtn.appendChild(minimizeIcon);

// For toggle states, store reference and swap innerHTML via DOM
this.maximizeIcon = DOM.icon({ name: 'maximize', size: 'sm' });
this.maximizeBtn.appendChild(this.maximizeIcon);

// On toggle:
this.maximizeIcon.innerHTML = getIconSvg(this.isMaximized ? 'restore' : 'maximize');
```

**Token Violations**: None
**Estimated Time**: 30 minutes

---

#### 3. `terminal-header.ts` (3 bypasses)
**Lines**: 224, 246, 463-466, 474

**Current Pattern**:
```typescript
addBtn.innerHTML = headerIcons.plus;
closeBtn.innerHTML = headerIcons.close;
iconSpan.innerHTML = shellIcons[shellIcon] ?? shellIcons.terminal;
```

**Migration**: Same as above, replace with `DOM.icon()`

**Token Violations**: None
**Estimated Time**: 30 minutes

---

#### 4. `ai-footer.ts` (1 bypass)
**Lines**: 621

**Current Pattern**:
```typescript
icon.innerHTML = provider?.icon ?? `<svg viewBox="0 0 24 24">...</svg>`;
```

**Migration**:
```typescript
// Fallback SVG should be added to icons.ts as a named icon
const iconEl = DOM.icon({
    name: provider?.iconName ?? 'default-provider',
    size: 'md'
});
```

**Token Violations**: None
**Estimated Time**: 15 minutes

---

## Phase 2: Button Migration (74 bypasses, ~16 hours)

### Top Priority Files (Highest Bypass Count)

#### 1. `ai-sidebar.ts` (23 bypasses)
**Lines**: Multiple button innerHTML assignments

**Current Pattern**:
```typescript
const collapseBtn = document.createElement('button');
collapseBtn.className = 'sidebar-collapse-btn';
collapseBtn.innerHTML = sidebarIcons.chevronDown;

const addBtn = document.createElement('button');
addBtn.innerHTML = sidebarIcons.plus;

const playBtn = document.createElement('button');
playBtn.innerHTML = sidebarIcons.play;
```

**Migration**:
```typescript
const collapseBtn = DOM.iconButton('chevron-down', {
    className: 'sidebar-collapse-btn',
    attributes: { 'aria-label': 'Collapse section' }
});

const addBtn = DOM.iconButton('plus', {
    attributes: { 'aria-label': 'Add new item' }
});

const playBtn = DOM.iconButton('play', {
    attributes: { 'aria-label': 'Run command' }
});
```

**Token Violations**: None
**Estimated Time**: 3 hours

---

#### 2. `terminal-sidebar.ts` (22 bypasses)
**Lines**: Similar to ai-sidebar.ts

**Current Pattern**: Same as ai-sidebar.ts
**Migration**: Same as ai-sidebar.ts

**Token Violations**: None
**Estimated Time**: 3 hours

---

#### 3. `claude-panel.ts` (19 bypasses)
**Lines**: 448, 904, 1283, 1711, 1750, 2077, 2084, 2140, 2347, 2535, 2714, etc.

**Current Pattern**:
```typescript
const addBtn = document.createElement('button');
addBtn.innerHTML = `<span style="font-size:13px;">+</span> Add Variable`;

const importBtn = document.createElement('button');
importBtn.innerHTML = `<span style="font-size:11px;">↓</span> Import from IDE`;

const iconEl = document.createElement('span');
iconEl.innerHTML = icon;
```

**Token Violations**: 4 (inline font-size, should use `var(--font-size-md)`)

**Migration**:
```typescript
// Replace inline styles with token-based approach
const addBtn = DOM.button('Add Variable', {
    icon: 'plus',
    iconPosition: 'left',
    variant: 'default'
});

const importBtn = DOM.button('Import from IDE', {
    icon: 'download',
    iconPosition: 'left',
    variant: 'default'
});

// For pure icon elements
const iconEl = DOM.icon({ name: iconName, size: 'md' });
```

**Estimated Time**: 4 hours

---

#### 4. `terminal-chat.ts` (13 bypasses)
**Lines**: 251, 605, 628, 634, 917, 938, 992, 998, 1359, 1435, 1466, 1498, 1990

**Current Pattern**:
```typescript
this.todoButtonEl.innerHTML = `<svg width="12" height="12">...</svg>`;
iconSpan.innerHTML = toolIcon;
statusSpan.innerHTML = `<svg>...</svg>`;
```

**Token Violations**: Inline SVG sizing (12px)

**Migration**:
```typescript
const todoButton = DOM.iconButton('checklist', {
    size: 'sm',
    attributes: { 'aria-label': 'View todos' }
});

const iconEl = DOM.icon({ name: toolName, size: 'xs' });

const statusIcon = DOM.icon({
    name: statusName,
    size: 'xs',
    color: 'var(--color-accent-success)'
});
```

**Estimated Time**: 2 hours

---

#### 5. Other Button Files (29 bypasses across 13 files)

**Files**:
- `ai-history-modal.ts` (1)
- `ai-metrics-panel.ts` (1)
- `context-menu.ts` (1)
- `debug-dashboard.ts` (1)
- `draggable-modal.ts` (1)
- `home-panel.ts` (2)
- `settings-history-modal.ts` (2)
- `toast.ts` (1)
- `tool-permission-editor.ts` (2)
- `ai-provider-dialog.ts` (1)
- Others...

**Migration Strategy**: Same patterns as above
**Estimated Time**: 4 hours

---

## Phase 3: Form Migration (2 bypasses, ~4 hours)

#### 1. `claude-form-builder.ts` (1 bypass)
**Line**: 138

**Current Pattern**:
```typescript
label.innerHTML = `${field.label}${field.required ? ' <span style="color:#ff6666;">*</span>' : ''}`;
```

**Token Violations**: 1 (inline color, should use `var(--color-accent-error)`)

**Migration**:
```typescript
// Implement DOM.field() first, then use it
const fieldEl = DOM.field({
    name: field.name,
    label: field.label,
    type: field.type,
    required: field.required,
    placeholder: field.placeholder
});

// Internally, DOM.field() creates:
const label = DOM.createElement('label', {
    children: [field.label]
});

if (field.required) {
    const asterisk = DOM.createElement('span', {
        className: 'required-indicator',
        style: { color: 'var(--color-accent-error)' },
        children: ['*']
    });
    label.appendChild(asterisk);
}
```

**Estimated Time**: 2 hours (includes implementing DOM.field())

---

#### 2. `ai-footer.ts` (1 bypass)
**Line**: 175, 241

**Current Pattern**: Inline SVG in button innerHTML

**Migration**: Use DOM.iconButton()

**Estimated Time**: 30 minutes

---

## Phase 4: Modal Migration (15 bypasses, ~8 hours)

### Requires: Implement `DOM.modal()` first (4 hours)

#### 1. `ai-provider-dialog.ts` (1 bypass)
**Line**: 151

**Current Pattern**:
```typescript
this.dialog.innerHTML = `
    <h3>...</h3>
    <div>...</div>
`;
```

**Migration**:
```typescript
const modal = DOM.modal({
    title: 'AI Provider Configuration',
    content: DOM.column([
        // Form fields using DOM.field()
    ]),
    width: 'md',
    draggable: true
});
```

**Estimated Time**: 1 hour

---

#### 2. Other Modal Files (14 bypasses)
- `environment-modal.ts` (2)
- `ai-history-modal.ts` (5)
- `env-export-wizard.ts` (3)
- `env-import-wizard.ts` (3)

**Estimated Time**: 3 hours

---

## Phase 5: Sidebar Migration (36 bypasses, ~16 hours)

### Requires: Implement `DOM.sidebar()` first (6 hours)

#### 1. `ai-sidebar.ts` (23 bypasses)
**Lines**: 245, 291, 303, 325, 340, 451, 485, 510, 544, 554, 564, 615, 656, 681, 724, 734, 744, 777, 805, 844, 871, 1156, 1173

**Current Pattern**:
```typescript
// Manual sidebar construction
const sidebar = document.createElement('div');
sidebar.className = 'ai-sidebar';

const header = document.createElement('div');
header.className = 'sidebar-header';

const toggleBtn = document.createElement('button');
toggleBtn.innerHTML = this.isCollapsed ? sidebarIcons.chevronLeft : sidebarIcons.chevronRight;
```

**Token Violations**: 5

**Migration**:
```typescript
const sidebar = DOM.sidebar({
    title: 'AI Tools',
    collapsible: true,
    side: 'right',
    width: 'var(--left-panel-width)',
    sections: [
        {
            title: 'Agents',
            icon: 'agent',
            collapsible: true,
            items: this.buildAgentItems()
        },
        {
            title: 'Commands',
            icon: 'command',
            collapsible: true,
            items: this.buildCommandItems()
        }
    ],
    onToggle: (collapsed) => {
        this.handleSidebarToggle(collapsed);
    }
});
```

**Estimated Time**: 6 hours

---

#### 2. `terminal-sidebar.ts` (22 bypasses)
**Migration**: Same as ai-sidebar.ts

**Estimated Time**: 5 hours

---

#### 3. Other Sidebar Files
- `claude-panel.ts` (partially sidebar)
- `ai-metrics-panel.ts`

**Estimated Time**: 5 hours

---

## Phase 6: Header/Bar Migration (3 bypasses, ~4 hours)

### Requires: Implement `DOM.bar()` first (2 hours)

#### 1. `taskbar.ts` (counted in buttons)
Already migrated in Phase 2

#### 2. `header-bar.ts` (counted in icons)
Already migrated in Phase 1

**Estimated Time**: 2 hours for DOM.bar() implementation only

---

## Phase 7: Uncategorized Migration (15 bypasses, ~8 hours)

These require case-by-case analysis:

#### Files:
- `terminal-chat.ts` (large innerHTML with todo panel)
- `router.ts` (error page innerHTML)
- `main.ts` (fullscreen button)
- `ai-metrics-panel.ts` (no data placeholder)
- `claude-hub.ts` (placeholder content)
- `hub-section.ts` (unicode arrow)
- `debug-tab.ts` (log container)
- `sessions-tab.ts` (info panel)

**Strategy**: Review each, determine if needs new factory method or can use existing

**Estimated Time**: 8 hours

---

## Implementation Order (Optimized for Dependencies)

1. **Week 1: Foundation**
   - Day 1-2: Implement new DOM factory methods
     - DOM.modal()
     - DOM.sidebar()
     - DOM.form() / DOM.field()
     - DOM.bar()
     - DOM.panel()
   - Day 3: Extend DOM.icon() (spin, pulse, badge)

2. **Week 2: High-Impact Migrations**
   - Day 1: Phase 1 - Icons (all files)
   - Day 2-3: Phase 2 - Buttons (ai-sidebar, terminal-sidebar)
   - Day 4: Phase 2 - Buttons (claude-panel, terminal-chat)
   - Day 5: Phase 2 - Buttons (remaining files)

3. **Week 3: Advanced Components**
   - Day 1: Phase 3 - Forms
   - Day 2: Phase 4 - Modals
   - Day 3-4: Phase 5 - Sidebars
   - Day 5: Phase 6 - Headers/Bars

4. **Week 4: Cleanup and Validation**
   - Day 1-2: Phase 7 - Uncategorized
   - Day 3: Re-run scanners, verify 0 bypasses
   - Day 4: Accessibility audit
   - Day 5: Performance testing, pool metrics

---

## Verification Commands

After each phase:

```bash
# Check for remaining bypasses
node .claude/workspace/tools/find-dom-bypasses.cjs

# Re-analyze categorization
node .claude/workspace/tools/dom-centralization-scanner.cjs

# Verify no inline styles
grep -r "style=\"color:#" root/archlab-ide/src/renderer/components
grep -r "style=\"font-size:[0-9]" root/archlab-ide/src/renderer/components

# Test pool metrics
# (Run app, check Engine.pools.getStats())
```

---

## Success Criteria

- [ ] Zero bypasses detected by scanner
- [ ] Zero inline hex colors (except in tokens)
- [ ] Zero inline px sizes (except in tokens)
- [ ] All buttons use DOM.button() or DOM.iconButton()
- [ ] All icons use DOM.icon()
- [ ] All modals use DOM.modal()
- [ ] All sidebars use DOM.sidebar()
- [ ] Pool hit rate >80% for div/span/button
- [ ] ARIA labels on all interactive elements
- [ ] Keyboard navigation functional everywhere

---

## Risk Mitigation

1. **Backup Strategy**: Create feature branch, test each phase before merging
2. **Testing**: Visual regression tests for each migrated component
3. **Rollback Plan**: Git revert per-file if issues found
4. **Performance**: Monitor pool stats, memory usage during migration
5. **Accessibility**: Test with screen reader after each phase

---

## References

- **Bypass Report**: `.claude/workspace/tools/dom-bypasses.json`
- **Categorization Report**: `.claude/workspace/tools/dom-centralization-report.json`
- **API Spec**: `.claude/workspace/docs/dom-factory-api-spec.md`
- **Current Factory**: `root/archlab-ide/src/renderer/utils/dom-factory.ts`
