---
name: dom-bypass-remediation
type: checklist
version: 1.0.0
generated: 2026-01-03T13:05:52.648Z
---

THIS CHECKLIST TRACKS DOM factory bypass violations requiring remediation

%% META %%:
intent: "Centralize all DOM creation through DOM factory"
objective: "Zero document.createElement() and innerHTML bypasses"

## Validation Summary

**Total Gates**: 438
**Passed**: 0
**Failed**: 438
**Pass Rate**: 0%

## Phase 1: main.ts

### Gate 1.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 288
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
exitFullscreenBtn.innerHTML = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-wid...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 2: utils/motion.ts

### Gate 2.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.transform = `translate(${state.position.x}px, ${state.position.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 107
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.left = `${state.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 108
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.top = `${state.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 282
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.transform = `translate(${initialPosition.x}px, ${initialPosition.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 332
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.transform = `translate(${state.position.x}px, ${state.position.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 334
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.left = `${state.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 335
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.top = `${state.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 354
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.transform = `translate(${state.position.x}px, ${state.position.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 356
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.left = `${state.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 357
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.top = `${state.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 384
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.left = `${state.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 385
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.top = `${state.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 594
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.width = `${newWidth}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.14: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 595
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.height = `${newHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.15: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 596
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.left = `${newX}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 2.16: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 597
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.top = `${newY}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 3: utils/dom-factory.ts

### Gate 3.1: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 258
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement(tag)
```
- **Suggested Fix**:
```typescript
DOM.createElement("unknown", { /* options */ })
```

### Gate 3.2: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 352
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("template")
```
- **Suggested Fix**:
```typescript
DOM.createElement("template", { /* options */ })
```

### Gate 3.3: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
template.innerHTML = html.trim()
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 3.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 430
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
span.innerHTML = getIconSvg(options.name)
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 3.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 466
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
element.innerHTML = getIconSvg(iconName)
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 3.6: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 474
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
element.innerHTML = svg
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 3.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 698
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
container.style.width = `${collapsedWidth}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 3.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 706
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
container.style.width = `${width}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 3.9: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 774
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconEl.innerHTML = iconSvg
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 3.10: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1034
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg("chevron-down")
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 3.11: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1038
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg("chevron-right")
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 3.12: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
element.innerHTML = ""
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 3.13: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 1251
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("div")
```
- **Suggested Fix**:
```typescript
DOM.createElement("div", { /* options */ })
```

## Phase 4: utils/manager/scroll-manager.ts

### Gate 4.1: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 181
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("div")
```
- **Suggested Fix**:
```typescript
DOM.createElement("div", { /* options */ })
```

### Gate 4.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 182
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
bottomSentinel.style.height = "1px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 4.3: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 203
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("div")
```
- **Suggested Fix**:
```typescript
DOM.createElement("div", { /* options */ })
```

### Gate 4.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 204
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
topSentinel.style.height = "1px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 5: utils/manager/layout-manager.ts

### Gate 5.1: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("div")
```
- **Suggested Fix**:
```typescript
DOM.createElement("div", { /* options */ })
```

### Gate 5.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 234
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
handle.style.width = "4px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 241
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
handle.style.width = "4px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 249
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
handle.style.height = "4px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 256
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
handle.style.height = "4px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 337
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.width = `${newWidth}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.height = `${newHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 479
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.width = `${constrainedWidth}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 5.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 490
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.height = `${constrainedHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 6: utils/manager/base-manager.ts

### Gate 6.1: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 123
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement("div")
```
- **Suggested Fix**:
```typescript
DOM.createElement("div", { /* options */ })
```

## Phase 7: terminal/services/virtualized-entries.ts

### Gate 7.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 197
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
element.style.cssText = "margin-bottom: 4px; line-height: 1.4;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 8: terminal/components/terminal-sidebar.ts

### Gate 8.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 230
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.toggleBtn.innerHTML = this.isCollapsed ? sidebarIcons.chevronRight : sidebarIcons.chevronLeft
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 260
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
collapseBtn.innerHTML = sidebarIcons.chevronDown
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 276
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconEl.innerHTML = icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 300
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
clearAllBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 309
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
refreshBtn.innerHTML = sidebarIcons.refresh
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
saveBtn.innerHTML = sidebarIcons.save
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 333
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
addBtn.innerHTML = sidebarIcons.plus
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.8: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 461
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = shellIcons[shellIcon] ?? shellIcons.terminal
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.9: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 536
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sendBtn.innerHTML = sidebarIcons.sendOut
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.10: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 539
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sendBtn.innerHTML = sidebarIcons.sendIn
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.11: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 558
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.12: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 571
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.13: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 623
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.folder
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.14: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 660
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.15: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 672
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = sidebarIcons.edit
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.16: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 684
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.17: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 714
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.terminal
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.18: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 765
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.19: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 777
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = sidebarIcons.edit
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.20: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 789
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.21: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1330
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.toggleBtn.innerHTML = this.isCollapsed ? sidebarIcons.chevronRight : sidebarIcons.chevronLeft
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 8.22: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1347
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = isCollapsed ? sidebarIcons.chevronRight : sidebarIcons.chevronDown
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 9: terminal/components/terminal-header.ts

### Gate 9.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 230
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
addBtn.innerHTML = headerIcons.plus
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 9.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 258
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
closeBtn.innerHTML = headerIcons.close
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 9.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 464
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = shellIcons[shellIcon] ?? shellIcons.terminal
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 9.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 485
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sendBtn.innerHTML = headerIcons.sendOut
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 9.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 488
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sendBtn.innerHTML = headerIcons.sendIn
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 9.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 498
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
closeBtn.innerHTML = headerIcons.close
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 10: terminal/components/terminal-chat.ts

### Gate 10.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 191
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.container.style.cssText = `
            display: flex;
            flex-direction: column;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.entriesEl.style.cssText = `
            flex: 1;
            min-height: 0;
            overflo...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
footerArea.style.cssText = `
            flex-shrink: 0;
            border-top: 1px solid #3c3c3c;
...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 226
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
pathContainer.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 235
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.pathEl.style.cssText = `
            flex: 1;
            font-size: 11px;
            color: #...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 265
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.todoButtonEl.innerHTML = `
            <svg width="12" height="12" viewBox="0 0 24 24" fill="no...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 275
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 280
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#3c3c3c"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 281
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.optionsEl.style.cssText = `
            display: none;
            flex-wrap: wrap;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.14: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 329
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
inputArea.style.cssText = `
            display: flex;
            align-items: center;
            ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.15: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.promptEl.style.cssText = `
            color: #c9a227;
            font-weight: bold;
         ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.16: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.17: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 414
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.promptEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.18: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 417
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.promptEl.style.color = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.19: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 477
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.20: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 484
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
prompt.style.cssText = `color: #c9a227; font-weight: bold; user-select: none;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.21: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.22: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 635
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = toolIcon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.23: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 636
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `display: flex; align-items: center; color: ${toolCall.isError ? "#f14c4c" ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.24: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 644
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
nameSpan.style.cssText = `color: #9cdcfe; font-weight: 500;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.25: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.26: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 664
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
statusSpan.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#f14c4c"...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.27: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 670
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
statusSpan.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#4a9c6d"...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.28: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 892
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4; padding-left: 16px; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.29: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.30: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 961
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = toolIcon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.31: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 962
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `display: flex; align-items: center; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.32: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 969
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
nameSpan.style.cssText = `color: #9cdcfe; font-weight: 500;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.33: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.34: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 988
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
spinner.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColo...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.35: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 991
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
spinner.style.cssText = `display: flex; align-items: center; margin-left: auto; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.36: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1032
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.borderLeftColor = isError ? "#f14c4c" : "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.37: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1046
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
resultSpan.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#f14c4c"...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.38: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1052
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
resultSpan.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#4a9c6d"...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.39: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionContainer.style.cssText = `
            display: flex;
            flex-direction: column;
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.40: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1116
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionEl.style.cssText = `display: flex; flex-direction: column; gap: 6px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.41: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1122
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionRow.style.cssText = `display: flex; align-items: center; gap: 8px; flex-wrap: wrap;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.42: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.43: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1147
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionText.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.44: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1156
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
hintEl.style.cssText = `font-size: 10px; color: #888;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.45: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1167
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
optionsEl.style.cssText = `display: flex; flex-wrap: wrap; gap: 6px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.46: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.47: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1199
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
submitBtn.style.background = "#5ab87d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.48: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1202
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
submitBtn.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.49: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.50: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.51: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1281
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.52: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1282
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.53: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1283
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
badge.style.background = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.54: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1286
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.55: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1287
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#2d3d30"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.56: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1288
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
badge.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.57: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1300
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.58: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1301
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.59: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1306
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.60: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1307
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.61: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1375
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4; display: flex; align-items: flex-start; ga...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.62: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1380
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
prompt.style.cssText = `color: #c9a227; font-weight: bold; user-select: none;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.63: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
permissionContainer.style.cssText = `
            display: flex;
            flex-direction: column;...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.64: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1441
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
permissionRow.style.cssText = `display: flex; align-items: center; gap: 8px; flex-wrap: wrap;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.65: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1447
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#c9a227" s...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.66: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1457
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionSpan.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.67: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.68: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1480
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
questionMark.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.69: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.70: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1515
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
actionsRow.style.cssText = `display: flex; gap: 8px; margin-top: 4px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.71: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.72: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1535
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
allowBtn.innerHTML = `
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" strok...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.73: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1542
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
allowBtn.style.background = "#5ab87d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.74: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1545
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
allowBtn.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.75: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.76: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1568
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
denyBtn.innerHTML = `
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.77: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1576
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyBtn.style.borderColor = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.78: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1577
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyBtn.style.color = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.79: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1580
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyBtn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.80: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1581
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyBtn.style.color = "#ccc"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.81: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.82: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1602
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
denyExpandBtn.innerHTML = `
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" ...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 10.83: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1616
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonRow.style.cssText = `
            display: none;
            flex-direction: column;
     ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.84: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1628
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonLabel.style.cssText = `color: #888; font-size: 11px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.85: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.86: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1647
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonInput.style.borderColor = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.87: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1650
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonInput.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.88: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1657
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyReasonActions.style.cssText = `display: flex; gap: 8px;`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.89: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.90: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.91: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2025
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.92: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2026
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.93: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2028
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.94: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2029
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.95: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2031
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#3c3c3c"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.96: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2032
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.97: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2053
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.maxHeight = `${maxHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.98: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2070
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.99: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2071
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.100: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2072
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.101: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2074
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.background = "#2d2d30"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.102: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 2106
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
this.todoPanelEl.innerHTML = `
            <div style="padding: 8px 12px; border-bottom: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 10.103: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.104: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.105: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2294
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.106: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2295
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.107: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2298
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = option.isDefault ? "#4a9c6d" : "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 10.108: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 2299
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = option.isDefault ? "#3d3d3d" : "#333"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 11: terminal/components/prompt-overlay.ts

### Gate 11.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
overlay.style.cssText = `
            display: none;
            flex-wrap: wrap;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 79
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
label.style.cssText = `
                width: 100%;
                margin-bottom: 4px;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 173
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 174
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 176
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
keyBadge.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 181
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 182
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 186
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 11.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 187
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 12: terminal/components/ai-sidebar.ts

### Gate 12.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 249
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.toggleBtn.innerHTML = this.isCollapsed ? sidebarIcons.chevronLeft : sidebarIcons.chevronRight
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 301
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
collapseBtn.innerHTML = sidebarIcons.chevronDown
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 317
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconEl.innerHTML = icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 345
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
addBtn.innerHTML = sidebarIcons.plus
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
refreshBtn.innerHTML = sidebarIcons.refresh
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 483
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.agent
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 529
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.8: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 560
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.agent
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.9: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 606
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.10: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 618
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = sidebarIcons.edit
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.11: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 630
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.12: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 689
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.command
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.13: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 744
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.14: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 775
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.command
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.15: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 832
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
playBtn.innerHTML = sidebarIcons.play
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.16: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 844
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = sidebarIcons.edit
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.17: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 856
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.18: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 895
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.context
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.19: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 933
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = sidebarIcons.edit
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.20: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 980
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = sidebarIcons.context
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.21: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1017
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
removeBtn.innerHTML = sidebarIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.22: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1310
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.toggleBtn.innerHTML = this.isCollapsed ? sidebarIcons.chevronLeft : sidebarIcons.chevronRight
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 12.23: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1327
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = isCollapsed ? sidebarIcons.chevronRight : sidebarIcons.chevronDown
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 13: terminal/components/ai-provider-dialog.ts

### Gate 13.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.overlay.style.cssText = `
            position: fixed;
            inset: 0;
            backgr...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 13.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 13.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 155
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.dialog.innerHTML = `
            <h3 style="margin: 0 0 16px 0; font-size: 16px; font-weight: 6...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 13.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 323
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 13.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 337
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = icon.svg
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 13.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 13.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
customColor.style.cssText = `
            width: 32px;
            height: 24px;
            padding...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 13.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 419
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
colorLabel.style.cssText = `
            font-size: 11px;
            color: #888;
            margi...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 14: terminal/components/ai-footer.ts

### Gate 14.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.footerEl.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sep.style.cssText = `
            width: 1px;
            height: 20px;
            background: #444...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 181
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
addBtn.innerHTML = `<svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M19...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 184
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addBtn.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 185
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addBtn.style.color = "#aaa"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 188
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addBtn.style.borderColor = "#555"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 189
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addBtn.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.11: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sessionIcon.innerHTML = `<svg viewBox="0 0 24 24" fill="currentColor" width="12" height="12"><path d...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 269
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 270
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.color = "#aaa"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.14: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 273
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.borderColor = "#666"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.15: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sessionIndicator.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.16: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
activeLabel.style.cssText = `
            margin-left: ${showedSessionIndicator ? "8px" : "auto"};
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.17: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
activeLabel.innerHTML = `<span style="color: ${activeProvider.color};">Active:</span> ${activeProvid...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 14.18: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.19: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.20: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
title.style.cssText = `
            font-size: 10px;
            font-weight: 600;
            color...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.21: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.22: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 403
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
newBtn.innerHTML = `<svg viewBox="0 0 24 24" fill="currentColor" width="10" height="10"><path d="M19...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.23: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 406
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newBtn.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.24: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 407
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newBtn.style.color = "#ccc"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.25: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 410
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newBtn.style.borderColor = "#555"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.26: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 411
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newBtn.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.27: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
            display: flex;
            align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.28: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 469
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
clearAllBtn.innerHTML = sessionIcons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.29: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 474
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearSvg.style.width = "12px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.30: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 475
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearSvg.style.height = "12px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.31: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 478
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.background = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.32: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 483
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.33: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 508
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
list.style.cssText = `
            flex: 1;
            overflow-y: auto;
            padding: 4px;
...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.34: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                        padding: 16px 8px;
                        text-alig...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.35: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                padding: 16px 8px;
                text-align: center;
     ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.36: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 599
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.37: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 611
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
item.style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.38: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 631
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
content.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.39: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
icon.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.40: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 657
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML =
            provider?.icon ??
            `<svg viewBox="0 0 24 24" fill="currentCo...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.41: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 671
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
nameRow.style.cssText = `
            font-size: 10px;
            color: var(--color-text-primary, ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.42: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.43: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
boundBadge.style.cssText = `
                margin-left: 4px;
                font-size: 7px;
     ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.44: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 723
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
meta.style.cssText = `
            font-size: 8px;
            color: var(--color-text-muted, #80808...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.45: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 739
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
actions.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.46: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.47: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 804
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = iconSvg
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.48: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 810
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
svg.style.width = "12px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.49: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 811
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
svg.style.height = "12px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.50: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 815
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.51: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 822
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.color = "var(--color-text-muted, #808080)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.52: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1002
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: flex;
            align-items: center;
            justif...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.53: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 1019
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = provider.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 14.54: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1037
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#444"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 14.55: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 1038
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 15: router/router.ts

### Gate 15.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 129
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
this.viewport.innerHTML = `
            <div class="page-error">
                <h2>Failed to load ...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 16: engine/virtualization/virtualized-buffer.ts

### Gate 16.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 282
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.spacerTop.style.height = `${topHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 16.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 291
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.spacerBottom.style.height = `${bottomHeight}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 17: engine/pools/element-pool.ts

### Gate 17.1: DOCUMENT_CREATE_ELEMENT
- **Status**: ❌ FAILED
- **Line**: 168
- **Severity**: error
- **Conditions**:
  - [ ] Element creation uses DOM factory
- **Failure Reason**: Direct document.createElement() bypasses DOM factory
- **Remediation**: Replace with DOM.createElement() for lifecycle management
- **Current Code**:
```typescript
document.createElement(this.elementType)
```
- **Suggested Fix**:
```typescript
DOM.createElement("unknown", { /* options */ })
```

## Phase 18: components/tool-permission-editor.ts

### Gate 18.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 263
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
(this.addBtn as HTMLButtonElement).innerHTML = `<span style="font-size:13px;">+</span> Add Rule`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 18.2: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
addBtn.innerHTML = `<span style="font-size:13px;">+</span> Add Directory`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 19: components/toast.ts

### Gate 19.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 175
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconEl.innerHTML = config.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 20: components/taskbar.ts

### Gate 20.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 285
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
homeBtn.innerHTML = taskbarIcons.home
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 327
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.terminalBtn.innerHTML = taskbarIcons.terminal
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 341
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
notificationsBtn.innerHTML = taskbarIcons.bell
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.debugBtn.innerHTML = taskbarIcons.debug
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 365
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.settingsBtn.innerHTML = taskbarIcons.settings
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 379
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.aiMetricsBtn.innerHTML = taskbarIcons.aiMetrics
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 393
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.hardwareBtn.innerHTML = taskbarIcons.hardware
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.8: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 457
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
fpsIcon.innerHTML = taskbarIcons.fps
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 20.9: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 488
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
timerIcon.innerHTML = taskbarIcons.timer
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 21: components/settings-history-modal.ts

### Gate 21.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 95
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.tabsContainer.style.cssText = `
            display: flex;
            gap: 4px;
            pa...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 113
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.contentContainer.style.cssText = `
            flex: 1;
            overflow-y: auto;
         ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 129
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            padding: 8px 16px;
            background: transparent;
          ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 185
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            justify-content: space-between;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
actions.style.cssText = "display: flex; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 209
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
clearBtn.innerHTML = `${icons.trash} Clear All`
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 21.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 210
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearBtn.style.cssText = "display: flex; align-items: center; gap: 4px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 225
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
empty.style.cssText = "text-align: center; color: var(--color-text-secondary); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 234
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
item.style.cssText = `
            background: var(--color-bg-secondary);
            border: 1px so...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 260
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
headerRow.style.cssText = `
            display: flex;
            justify-content: space-between;
 ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 276
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
meta.style.cssText = "font-size: 12px; color: var(--color-text-muted);"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 288
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
diffContainer.style.cssText = `
            font-size: 12px;
            font-family: monospace;
   ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.14: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
diffLine.style.cssText = `
                    display: flex;
                    gap: 8px;
        ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.15: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
pathSpan.style.cssText = "color: var(--color-text-secondary); min-width: 150px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.16: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
oldSpan.style.cssText = `
                    background: rgba(255, 100, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.17: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
newSpan.style.cssText = `
                    background: rgba(100, 255, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.18: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            justify-content: space-between;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.19: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 476
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
empty.style.cssText = "text-align: center; color: var(--color-text-secondary); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.20: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 485
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.21: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 499
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            justify-content: space-between;
      ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.22: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 523
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
meta.style.cssText = "font-size: 12px; color: var(--color-text-muted); margin-top: 2px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.23: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 534
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
actions.style.cssText = "display: flex; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.24: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 555
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = icons.trash
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 21.25: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            justify-content: space-between;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.26: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 693
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
noDiff.style.cssText = "text-align: center; color: var(--color-text-muted); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.27: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 704
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
legend.style.cssText = `
            display: flex;
            gap: 16px;
            font-size: 12...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.28: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 715
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
currentLegend.innerHTML = `<span style="color: rgba(255, 100, 100, 0.8);">Current</span>`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 21.29: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 720
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
presetLegend.innerHTML = `<span style="color: rgba(100, 255, 100, 0.8);">Preset</span>`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 21.30: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 731
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.31: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
item.style.cssText = `
                background: var(--color-bg-secondary);
                border...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.32: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 751
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
pathSpan.style.cssText = "font-weight: 600; color: var(--color-text-primary); margin-bottom: 6px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.33: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 756
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
diffRow.style.cssText = "display: flex; gap: 8px; align-items: center;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.34: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
currentVal.style.cssText = `
                background: rgba(255, 100, 100, 0.15);
                ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.35: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
presetVal.style.cssText = `
                background: rgba(100, 255, 100, 0.15);
                p...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 21.36: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 802
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
loadSection.style.cssText = "margin-top: 16px; text-align: right;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 22: components/popover.ts

### Gate 22.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 797
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.container.style.width = `${this.options.width}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 22.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 860
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.pointer.style.left = `${anchorRect.left + anchorRect.width / 2 - 3}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 22.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 863
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.pointer.style.right = `${window.innerWidth - anchorRect.left - anchorRect.width / 2 - 3}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 23: components/modal.ts

### Gate 23.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 786
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
message.style.cssText = "padding: 4px 0; white-space: pre-wrap;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 24: components/home-panel.ts

### Gate 24.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 325
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
editBtn.innerHTML = `<svg viewBox="0 0 16 16" width="12" height="12"><path fill="currentColor" d="M1...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 24.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 338
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
deleteBtn.innerHTML = `<svg viewBox="0 0 16 16" width="12" height="12"><path fill="currentColor" d="...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 25: components/header-bar.ts

### Gate 25.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 149
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
minimizeBtn.innerHTML = headerIcons.hide
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 25.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 159
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.maximizeBtn.innerHTML = this.isMaximized ? headerIcons.restore : headerIcons.maximize
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 25.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 171
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.fullscreenBtn.innerHTML = this.isFullscreen ? headerIcons.exitFullscreen : headerIcons.fullscre...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 25.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 181
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
closeBtn.innerHTML = headerIcons.close
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 25.5: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 221
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.maximizeBtn.innerHTML = this.isMaximized ? headerIcons.restore : headerIcons.maximize
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 25.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 229
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
this.fullscreenBtn.innerHTML = this.isFullscreen ? headerIcons.exitFullscreen : headerIcons.fullscre...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 26: components/form-controls.ts

### Gate 26.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 209
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
input.style.cssText =
        "position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 27: components/environment-modal.ts

### Gate 27.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 109
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
container.style.gap = "12px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 111
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
container.style.padding = "16px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 240
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
table.style.cssText = `
            width: 100%;
            border-collapse: collapse;
            ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.6: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
thead.innerHTML = `
            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bot...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 27.7: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 283
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
emptyRow.innerHTML = `
                <td colspan="3" style="padding: 24px; text-align: center; col...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 27.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
row.style.borderBottom = "1px solid var(--color-border, #3c3c3c)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
nameCell.style.padding = "8px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 311
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
valueCell.style.padding = "8px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 313
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
valueCell.style.maxWidth = "300px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 325
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
actionsCell.style.padding = "8px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 27.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 28: components/env-import-wizard.ts

### Gate 28.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 382
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
sharedOption.innerHTML = `
      <input type="radio" name="scope" value="shared" ${this.selectedScop...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 28.2: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 401
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
projectOption.innerHTML = `
      <input type="radio" name="scope" value="project" ${this.selectedSc...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 28.3: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 470
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
values.innerHTML = `
      <div>
        <div class="env-wizard-conflict-value-label">Existing:</div...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 29: components/env-export-wizard.ts

### Gate 29.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 240
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
message.innerHTML = `A <span class="env-export-path">.env</span> file already exists at this locatio...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 29.2: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 254
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
overrideOption.innerHTML = `
      <input type="radio" name="export-option" value="override" ${this....
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 29.3: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
saveAsContent.innerHTML = `
      <div style="display: flex; align-items: flex-start; gap: var(--spa...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 30: components/draggable-modal.ts

### Gate 30.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 232
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
closeBtn.innerHTML = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
 ...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 30.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 336
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.transform = `translate(${this.position.x}px, ${this.position.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.left = `${this.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 372
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.top = `${this.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 404
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.transform = `translate(${this.position.x}px, ${this.position.y}px)`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 469
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.width = `${this.size.width}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.7: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 470
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.height = `${this.size.height}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 471
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.left = `${this.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 472
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.top = `${this.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 514
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.left = `${this.position.x}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.11: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 515
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.top = `${this.position.y}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.width = `${this.size.width}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 30.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 525
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.element.style.height = `${this.size.height}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 31: components/debug-dashboard.ts

### Gate 31.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 135
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = TAB_ICONS[tab.id]
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 31.2: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 639
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
el.innerHTML = value
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 32: components/context-menu.ts

### Gate 32.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 245
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconEl.innerHTML = item.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 32.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.container.style.left = `${left}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 32.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 295
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
this.container.style.top = `${top}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 32.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 386
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
items[this.focusedIndex].style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 33: components/claude-settings-dialog.ts

### Gate 33.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
keyInput.style.cssText = `
                width: 120px; padding: 6px 8px; font-size: 11px;
        ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 33.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
valueInput.style.cssText = `
                flex: 1; padding: 6px 8px; font-size: 11px;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 33.3: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 404
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
addBtn.innerHTML = `<span style="font-size:13px;">+</span> Add Variable`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 34: components/claude-form-builder.ts

### Gate 34.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 141
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
label.innerHTML = `${field.label}${field.required ? ' <span style="color:#ff6b6b">*</span>' : ""}`
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 34.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 926
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 34.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 927
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4fc3f7"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 34.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 930
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = "#2d2d2d"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 34.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 931
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#444"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 35: components/ai-metrics-panel.ts

### Gate 35.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
noData.innerHTML = `
            <div style="width: 24px; height: 24px; margin: 0 auto 8px;">${getIc...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 35.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 35.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = "display: flex; width: 14px; height: 14px;"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 35.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 297
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = provider.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 35.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 313
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
indicator.style.cssText = "color: var(--color-accent-success, #4caf50);"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 35.6: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
indicator.innerHTML = icons.active
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 35.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 576
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = provider.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 35.8: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 631
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 36: components/ai-history-modal.ts

### Gate 36.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 287
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
clearAllBtn.innerHTML = `${icons.trash} <span>Clear All</span>`
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 289
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.background = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
clearAllBtn.style.color = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.6: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `
            display: flex;
            align-items: center;
            j...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.7: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 378
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
iconSpan.innerHTML = provider.icon
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.8: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.9: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 406
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
tab.style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.10: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 415
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
tab.style.color = "var(--color-text-secondary, #cccccc)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.11: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 511
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
expandIcon.innerHTML = isExpanded ? icons.collapse : icons.expand
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.12: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 698
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
container.style.background = checked ? "var(--color-bg-hover, #2a2a2a)" : "transparent"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.13: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 701
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
checkboxEl.style.marginTop = "2px"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.14: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 720
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
roleIcon.innerHTML = isUser ? icons.user : icons.assistant
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.15: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.16: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 842
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
sendIcon.innerHTML = icons.send
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.17: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.18: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 885
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
btn.innerHTML = iconSvg
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 36.19: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 889
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 36.20: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 896
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
btn.style.color = "var(--color-text-muted, #808080)"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 37: components/claude-hub/claude-hub-renderer.ts

### Gate 37.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 192
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
placeholder.innerHTML = `
        <div class="claude-hub__empty-icon">🚧</div>
        <div class="c...
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 38: components/claude-hub/tabs/runtime/sessions-tab.ts

### Gate 38.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 213
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
info.innerHTML = `
            <div class="sessions-tab__info-row">
                <span class="ses...
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 39: components/claude-hub/tabs/runtime/runtime-config-tab.ts

### Gate 39.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 163
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
flagsContent.innerHTML = '<span class="runtime-config-tab__empty">No feature flags cached</span>'
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

### Gate 39.2: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 185
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
tipsContent.innerHTML = '<span class="runtime-config-tab__empty">No tips viewed</span>'
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 40: components/claude-hub/tabs/runtime/file-history-tab.ts

### Gate 40.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 157
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
preview.innerHTML = '<span class="file-history-tab__preview-empty">Select a file to preview</span>'
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 41: components/claude-hub/tabs/config/local-tab.ts

### Gate 41.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 68
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg("folder")
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 41.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 124
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
content.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 41.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 167
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
empty.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 41.4: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 193
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = isLocal ? getIconSvg("lock") : getIconSvg("folder")
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 41.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 336
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
chip.style.cssText = `background: ${color}22; border: 1px solid ${color}44; color: ${color};`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 42: components/claude-hub/tabs/config/global-tab.ts

### Gate 42.1: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 78
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg("globe")
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 42.2: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 163
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg(iconName)
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

### Gate 42.3: INNERHTML_ICON
- **Status**: ❌ FAILED
- **Line**: 256
- **Severity**: error
- **Conditions**:
  - [ ] Icon SVG uses DOM.icon()
- **Failure Reason**: innerHTML assignment with SVG bypasses icon management
- **Remediation**: Replace with DOM.icon({ name: "iconName" })
- **Current Code**:
```typescript
icon.innerHTML = getIconSvg(iconName)
```
- **Suggested Fix**:
```typescript
const icon = DOM.icon({ name: "iconName" }); parent.appendChild(icon);
```

## Phase 43: components/claude-hub/shared/hub-section.ts

### Gate 43.1: INNERHTML_GENERIC
- **Status**: ❌ FAILED
- **Line**: 77
- **Severity**: warning
- **Conditions**:
  - [ ] Dynamic content uses structured creation
- **Failure Reason**: innerHTML assignment can cause XSS and bypasses pooling
- **Remediation**: Replace with DOM.createElement() with children array
- **Current Code**:
```typescript
toggle.innerHTML = "&#9662;"
```
- **Suggested Fix**:
```typescript
Use DOM.createElement() with children array
```

## Phase 44: components/background/physics-shape-manager.ts

### Gate 44.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 240
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.element.style.marginLeft = `${shape.offsetX}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 44.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 241
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.element.style.marginTop = `${shape.offsetY}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Phase 45: components/background/geometric-background.ts

### Gate 45.1: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 223
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.style.borderLeftWidth = `${halfSize}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 45.2: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 224
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.style.borderRightWidth = `${halfSize}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 45.3: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 225
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.style.borderBottomWidth = `${size}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 45.4: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 230
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.style.width = `${size}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

### Gate 45.5: STYLE_PROPERTY
- **Status**: ❌ FAILED
- **Line**: 231
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes and tokens
- **Failure Reason**: Direct style property assignment bypasses CSS token system
- **Remediation**: Use className with CSS classes that reference tokens
- **Current Code**:
```typescript
shape.style.height = `${size}px`
```
- **Suggested Fix**:
```typescript
Use CSS class with token variables
```

## Recommendations

1. Fix 8 document.createElement() calls - use DOM.createElement()
2. Fix 118 icon innerHTML assignments - use DOM.icon()
3. Review 26 generic innerHTML assignments - use structured creation
4. Convert 286 inline style assignments to CSS classes
5. Run this scanner after each migration batch to track progress

## Migration Phases

### Phase 1: Icon innerHTML
- **Count**: 118 violations
- **Factory Method**: DOM.icon()
- **Priority**: 1

### Phase 2: createElement
- **Count**: 8 violations
- **Factory Method**: DOM.createElement()
- **Priority**: 2

### Phase 3: Generic innerHTML
- **Count**: 26 violations
- **Factory Method**: Structured creation
- **Priority**: 3

### Phase 4: Style properties
- **Count**: 286 violations
- **Factory Method**: CSS classes
- **Priority**: 4

ALWAYS use DOM factory methods for element creation
ALWAYS use addManagedListener for event binding
ALWAYS use CSS tokens instead of hardcoded values
NEVER use document.createElement directly
NEVER use innerHTML for icon assignment
NEVER add event listeners without managed cleanup
