---
name: inline-style-remediation
type: checklist
version: 1.0.0
generated: 2026-01-03T13:05:55.092Z
---

THIS CHECKLIST TRACKS Inline style and CSS token violations requiring remediation

%% META %%:
intent: "Centralize all styling through CSS tokens and classes"
objective: "Zero hardcoded style values, consistent design system usage"

## Validation Summary

**Total Gates**: 1235
**Passed**: 0
**Failed**: 1235
**Pass Rate**: 0%

## Phase 1: main.ts

### Gate 1.1: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 324
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
            position: "relative",
            display: "flex",
            alignItems: "center",
 ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 1.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 345
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
            fontSize: "var(--font-size-2xl)",
            marginBottom: "var(--space-md)",
       ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

## Phase 2: utils/dom-factory.ts

### Gate 2.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 513
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    position: "fixed",
                    inset: "0",
                    backgro...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 2.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 513
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    position: "fixed",
                    inset: "0",
                    backgro...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.3: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 526
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                position: "absolute",
                width: `${width}px`,
                height:...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.4: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 546
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                justifyCont...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.5: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 561
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                fontSize: "var(--font-size-sm)",
                fontWeight: "500",
              ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.6: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 585
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                padding: "var(--space...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.7: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 638
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                position: "relative",
                width: collapsed ? `${collapsedWidth}px` : `...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.8: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 660
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                opacity: collapsed ? ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.9: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 681
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                position: "absolute",
                top: "var(--space-sm)",
                [pos...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.10: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 743
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                gap: "var(-...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 2.11: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width,
                height,
                borderRadius,
            }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

## Phase 3: terminal/components/terminal-sidebar.ts

### Gate 3.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 24
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<svg viewBox="0 0 24 24" fill="currentColor">
        <rect x="2" y="4" width="20" height="16" rx="2...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 3.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 24
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<svg viewBox="0 0 24 24" fill="currentColor">
        <rect x="2" y="4" width="20" height="16" rx="2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 4: terminal/components/terminal-header.ts

### Gate 4.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 83
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<svg viewBox="0 0 24 24" fill="currentColor">
        <rect x="2" y="4" width="20" height="16" rx="2...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 4.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 83
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<svg viewBox="0 0 24 24" fill="currentColor">
        <rect x="2" y="4" width="20" height="16" rx="2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 5: terminal/components/terminal-chat.ts

### Gate 5.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 191
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.container.style.cssText = `
            display: flex;
            flex-direction: column;
    ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.2: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 191
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
this.container.style.cssText = `
            display: flex;
            flex-direction: column;
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 191
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            height: 100%;
          ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.4: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 191
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            height: 100%;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.5: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.entriesEl.style.cssText = `
            flex: 1;
            min-height: 0;
            overflo...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.6: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-height: 0;
            overflow-y: auto;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
footerArea.style.cssText = `
            flex-shrink: 0;
            border-top: 1px solid #3c3c3c;
...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
footerArea.style.cssText = `
            flex-shrink: 0;
            border-top: 1px solid #3c3c3c;
...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex-shrink: 0;
            border-top: 1px solid #3c3c3c;
            background: #252...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.10: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 215
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex-shrink: 0;
            border-top: 1px solid #3c3c3c;
            background: #252...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 226
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
pathContainer.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.12: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 226
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
pathContainer.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 226
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            padding: 4px 8px;
         ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.14: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 226
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            padding: 4px 8px;
         ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 235
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.pathEl.style.cssText = `
            flex: 1;
            font-size: 11px;
            color: #...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.16: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 235
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
this.pathEl.style.cssText = `
            flex: 1;
            font-size: 11px;
            color: #...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.17: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 235
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex: 1;
            font-size: 11px;
            color: #888;
            white-space:...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.18: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 235
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            flex: 1;
            font-size: 11px;
            color: #888;
            white-space:...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.19: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.22: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.23: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.24: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.25: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.cssText = `
            display: none;
            align-items: center;
    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.26: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.28: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.29: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.30: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.31: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.32: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: none;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.33: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 275
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.35: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 280
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#3c3c3c"
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.36: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 281
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.37: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.38: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.39: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.40: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.41: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.42: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.todoPanelEl.style.cssText = `
            display: none;
            position: absolute;
      ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.43: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.44: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.45: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.46: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.47: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.48: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 292
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: none;
            position: absolute;
            bottom: 100%;
            ri...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.49: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.optionsEl.style.cssText = `
            display: none;
            flex-wrap: wrap;
           ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.50: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.optionsEl.style.cssText = `
            display: none;
            flex-wrap: wrap;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.51: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.optionsEl.style.cssText = `
            display: none;
            flex-wrap: wrap;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.52: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.optionsEl.style.cssText = `
            display: none;
            flex-wrap: wrap;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.53: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.54: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.55: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.56: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 314
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.57: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 329
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
inputArea.style.cssText = `
            display: flex;
            align-items: center;
            ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.58: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 329
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
inputArea.style.cssText = `
            display: flex;
            align-items: center;
            ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.59: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 329
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.60: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 329
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.61: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.promptEl.style.cssText = `
            color: #c9a227;
            font-weight: bold;
         ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.62: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            color: #c9a227;
            font-weight: bold;
            user-select: none;
        
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.63: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.64: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.65: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.66: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.67: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
this.inputEl.style.cssText = `
            flex: 1;
            min-width: 0;
            padding: 6...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.68: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-width: 0;
            padding: 6px 8px;
            border: 1p...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.69: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-width: 0;
            padding: 6px 8px;
            border: 1p...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.70: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-width: 0;
            padding: 6px 8px;
            border: 1p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.71: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-width: 0;
            padding: 6px 8px;
            border: 1p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.72: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 350
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            flex: 1;
            min-width: 0;
            padding: 6px 8px;
            border: 1p...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.73: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 414
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.promptEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.74: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 417
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.promptEl.style.color = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.75: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 481
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
display: flex; align-items: flex-start; gap: 8px; color: #ccc;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.76: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 481
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; align-items: flex-start; gap: 8px; color: #ccc;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.77: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 484
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
prompt.style.cssText = `color: #c9a227; font-weight: bold; user-select: none;`
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.78: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 484
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #c9a227; font-weight: bold; user-select: none;
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.79: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 494
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
padding-left: 16px; color: #4a9c6d;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.80: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 513
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
padding-left: 16px; color: #888;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.81: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.82: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.83: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.84: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.85: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.86: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.87: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.88: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.89: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 616
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.90: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 636
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `display: flex; align-items: center; color: ${toolCall.isError ? "#f14c4c" ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.91: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 636
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `display: flex; align-items: center; color: ${toolCall.isError ? "#f14c4c" ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.92: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 644
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
nameSpan.style.cssText = `color: #9cdcfe; font-weight: 500;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.93: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 644
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #9cdcfe; font-weight: 500;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.94: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.95: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.96: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.97: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.98: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.99: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 654
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.100: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 892
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4; padding-left: 16px; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.101: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 892
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
margin-bottom: 4px; line-height: 1.4; padding-left: 16px; color: #4a9c6d;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.102: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.103: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.104: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.105: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.106: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.107: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.108: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.109: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
el.style.cssText = `
            display: flex;
            align-items: center;
            gap: 8p...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.110: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.111: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.112: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.113: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.114: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.115: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.116: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.117: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 943
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.118: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 962
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
iconSpan.style.cssText = `display: flex; align-items: center; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.119: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 962
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
display: flex; align-items: center; color: #4a9c6d;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.120: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 969
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
nameSpan.style.cssText = `color: #9cdcfe; font-weight: 500;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.121: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 969
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #9cdcfe; font-weight: 500;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.122: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.123: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.124: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
targetSpan.style.cssText = `color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; ov...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.125: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.126: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.127: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 979
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: #ce9178; font-family: 'Consolas', monospace; max-width: 400px; overflow: hidden; text-overflo...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.128: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 991
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
spinner.style.cssText = `display: flex; align-items: center; margin-left: auto; color: #4a9c6d;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.129: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 991
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
display: flex; align-items: center; margin-left: auto; color: #4a9c6d;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.130: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1000
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                @keyframes spin { to { transform: rotate(360deg); } }
                @keyframes ai...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.131: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1000
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                @keyframes spin { to { transform: rotate(360deg); } }
                @keyframes ai...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.132: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1000
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                @keyframes spin { to { transform: rotate(360deg); } }
                @keyframes ai...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.133: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1032
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.borderLeftColor = isError ? "#f14c4c" : "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.134: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1032
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.borderLeftColor = isError ? "#f14c4c" : "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.135: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
questionContainer.style.cssText = `
            display: flex;
            flex-direction: column;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.136: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
questionContainer.style.cssText = `
            display: flex;
            flex-direction: column;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.137: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
questionContainer.style.cssText = `
            display: flex;
            flex-direction: column;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.138: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
questionContainer.style.cssText = `
            display: flex;
            flex-direction: column;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.139: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.140: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.141: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.142: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1101
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.143: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1116
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
questionEl.style.cssText = `display: flex; flex-direction: column; gap: 6px;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.144: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1116
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; flex-direction: column; gap: 6px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.145: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1122
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
questionRow.style.cssText = `display: flex; align-items: center; gap: 8px; flex-wrap: wrap;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.146: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1122
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; align-items: center; gap: 8px; flex-wrap: wrap;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.147: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.148: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.149: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.150: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.151: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
headerEl.style.cssText = `
                    display: inline-block;
                    padding: 2...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.152: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: inline-block;
                    padding: 2px 6px;
                  ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.153: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: inline-block;
                    padding: 2px 6px;
                  ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.154: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: inline-block;
                    padding: 2px 6px;
                  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.155: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                    display: inline-block;
                    padding: 2px 6px;
                  ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.156: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1129
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    display: inline-block;
                    padding: 2px 6px;
                  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.157: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1147
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
questionText.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.158: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1147
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
questionText.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.159: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1147
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.160: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1147
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.161: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1156
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
hintEl.style.cssText = `font-size: 10px; color: #888;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.162: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1156
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
hintEl.style.cssText = `font-size: 10px; color: #888;`
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.163: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1156
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
font-size: 10px; color: #888;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.164: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1156
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
font-size: 10px; color: #888;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.165: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1167
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
optionsEl.style.cssText = `display: flex; flex-wrap: wrap; gap: 6px;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.166: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1167
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; flex-wrap: wrap; gap: 6px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.167: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.168: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.169: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.170: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.171: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
submitBtn.style.cssText = `
                align-self: flex-end;
                padding: 4px 12px;...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.172: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                align-self: flex-end;
                padding: 4px 12px;
                background...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.173: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                align-self: flex-end;
                padding: 4px 12px;
                background...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.174: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                align-self: flex-end;
                padding: 4px 12px;
                background...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.175: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                align-self: flex-end;
                padding: 4px 12px;
                background...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.176: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1185
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                align-self: flex-end;
                padding: 4px 12px;
                background...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.177: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1199
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
submitBtn.style.background = "#5ab87d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.178: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1202
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
submitBtn.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.179: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.180: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.181: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.182: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.183: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.184: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.185: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.186: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.187: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.188: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.189: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.190: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.191: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.192: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1230
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.193: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.194: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.195: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.196: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.197: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.198: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.199: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
badge.style.cssText = `
            display: inline-block;
            min-width: 16px;
            ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.200: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.201: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.202: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.203: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.204: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.205: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.206: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1249
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 16px;
            padding: 1px 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.207: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1281
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.208: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1282
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.209: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1283
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
badge.style.background = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.210: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1286
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.211: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1287
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#2d3d30"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.212: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1288
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
badge.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.213: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1300
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.214: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1301
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.215: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1306
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.216: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1307
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.217: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1375
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4; display: flex; align-items: flex-start; ga...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.218: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1375
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
el.style.cssText = `margin-bottom: 4px; line-height: 1.4; display: flex; align-items: flex-start; ga...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.219: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1375
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
margin-bottom: 4px; line-height: 1.4; display: flex; align-items: flex-start; gap: 8px; color: #ccc;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.220: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1375
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
margin-bottom: 4px; line-height: 1.4; display: flex; align-items: flex-start; gap: 8px; color: #ccc;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.221: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1380
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
prompt.style.cssText = `color: #c9a227; font-weight: bold; user-select: none;`
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.222: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1380
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #c9a227; font-weight: bold; user-select: none;
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.223: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
permissionContainer.style.cssText = `
            display: flex;
            flex-direction: column;...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.224: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
permissionContainer.style.cssText = `
            display: flex;
            flex-direction: column;...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.225: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
permissionContainer.style.cssText = `
            display: flex;
            flex-direction: column;...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.226: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
permissionContainer.style.cssText = `
            display: flex;
            flex-direction: column;...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.227: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.228: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.229: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.230: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1428
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            flex-direction: column;
            gap: 6px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.231: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1441
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
permissionRow.style.cssText = `display: flex; align-items: center; gap: 8px; flex-wrap: wrap;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.232: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1441
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; align-items: center; gap: 8px; flex-wrap: wrap;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.233: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1457
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
questionSpan.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.234: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1457
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
questionSpan.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.235: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1457
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.236: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1457
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.237: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.238: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.239: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.240: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.241: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
toolBadge.style.cssText = `
            display: inline-block;
            padding: 2px 8px;
       ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.242: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            padding: 2px 8px;
            background: #c9a227;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.243: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            padding: 2px 8px;
            background: #c9a227;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.244: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            padding: 2px 8px;
            background: #c9a227;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.245: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            padding: 2px 8px;
            background: #c9a227;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.246: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1465
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            padding: 2px 8px;
            background: #c9a227;
 ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.247: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1480
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
questionMark.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.248: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1480
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
questionMark.style.cssText = `color: #ccc; font-size: 12px;`
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.249: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1480
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.250: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1480
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
color: #ccc; font-size: 12px;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.251: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.252: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.253: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.254: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.255: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.256: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
targetRow.style.cssText = `
                padding: 4px 8px;
                margin-left: 24px;
   ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.257: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.258: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.259: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.260: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.261: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.262: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1492
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                padding: 4px 8px;
                margin-left: 24px;
                background: #2...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.263: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1515
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
actionsRow.style.cssText = `display: flex; gap: 8px; margin-top: 4px;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.264: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1515
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; gap: 8px; margin-top: 4px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.265: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.266: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.267: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.268: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.269: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.270: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
allowBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
      ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.271: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.272: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.273: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.274: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.275: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.276: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1521
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.277: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1542
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
allowBtn.style.background = "#5ab87d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.278: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1545
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
allowBtn.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.279: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.280: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.281: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.282: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.283: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.284: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.285: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
denyBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
       ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.286: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.287: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.288: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.289: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.290: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.291: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.292: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1554
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.293: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1576
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.borderColor = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.294: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1577
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.color = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.295: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1580
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.296: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1581
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyBtn.style.color = "#ccc"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.297: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.298: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.299: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.300: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.301: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
denyExpandBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
 ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.302: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            padding: 6px 8px;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.303: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            padding: 6px 8px;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.304: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            padding: 6px 8px;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.305: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            padding: 6px 8px;
  ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.306: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1590
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            padding: 6px 8px;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.307: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonRow.style.cssText = `
            display: none;
            flex-direction: column;
     ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.308: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1616
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyReasonRow.style.cssText = `
            display: none;
            flex-direction: column;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.309: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1616
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-direction: column;
            gap: 6px;
            ma...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.310: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1616
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-direction: column;
            gap: 6px;
            ma...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.311: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1628
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonLabel.style.cssText = `color: #888; font-size: 11px;`
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.312: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1628
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
denyReasonLabel.style.cssText = `color: #888; font-size: 11px;`
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.313: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1628
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: #888; font-size: 11px;
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.314: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1628
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
color: #888; font-size: 11px;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.315: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.316: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.317: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.318: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.319: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.320: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.cssText = `
            padding: 6px 10px;
            background: #252526;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.321: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 5.322: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.323: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.324: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.325: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.326: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1637
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            padding: 6px 10px;
            background: #252526;
            border: 1px solid #4a4a...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.327: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1647
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.borderColor = "#f14c4c"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.328: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1650
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyReasonInput.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.329: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1657
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyReasonActions.style.cssText = `display: flex; gap: 8px;`
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.330: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1657
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.331: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.332: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.333: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.334: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.335: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.336: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
denyWithMsgBtn.style.cssText = `
            display: inline-flex;
            align-items: center;
...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.337: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.338: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.339: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.340: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.341: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.342: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1662
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.343: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.344: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.345: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.346: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.347: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
cancelBtn.style.cssText = `
            padding: 6px 12px;
            background: transparent;
    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.348: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: transparent;
            border: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.349: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: transparent;
            border: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.350: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: transparent;
            border: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.351: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: transparent;
            border: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.352: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1685
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: transparent;
            border: 1px solid #...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.353: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2025
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.354: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2026
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.355: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2028
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.356: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2029
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.357: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2031
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#3c3c3c"
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 5.358: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2032
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.359: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2070
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.borderColor = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.360: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2071
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.color = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.361: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2072
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.362: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2074
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.todoButtonEl.style.background = "#2d2d30"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.363: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.364: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.365: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.366: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.367: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.368: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.369: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.370: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 2232
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: inline-flex;
                align-items: center;
   ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.371: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.372: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.373: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.374: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 5.375: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 5.376: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 5.377: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 5.378: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.379: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 2254
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
keyBadge.style.cssText = `
                display: inline-block;
                min-width: 18px;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 5.380: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2294
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 5.381: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2295
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.382: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2298
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = option.isDefault ? "#4a9c6d" : "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.383: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2298
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = option.isDefault ? "#4a9c6d" : "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.384: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2299
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = option.isDefault ? "#3d3d3d" : "#333"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 5.385: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 2299
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = option.isDefault ? "#3d3d3d" : "#333"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 6: terminal/components/prompt-overlay.ts

### Gate 6.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
overlay.style.cssText = `
            display: none;
            flex-wrap: wrap;
            gap: 6...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 6.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
overlay.style.cssText = `
            display: none;
            flex-wrap: wrap;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.3: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
overlay.style.cssText = `
            display: none;
            flex-wrap: wrap;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.4: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
overlay.style.cssText = `
            display: none;
            flex-wrap: wrap;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 6.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.7: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.8: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 47
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: none;
            flex-wrap: wrap;
            gap: 6px;
            padding: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 79
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
label.style.cssText = `
                width: 100%;
                margin-bottom: 4px;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.10: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 79
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
label.style.cssText = `
                width: 100%;
                margin-bottom: 4px;
           ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 79
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                width: 100%;
                margin-bottom: 4px;
                font-size: 11px;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.12: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 79
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                width: 100%;
                margin-bottom: 4px;
                font-size: 11px;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.16: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.17: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.18: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.19: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 6.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.22: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.23: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.24: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.25: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.26: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 110
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            gap: 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 6.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 6.28: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 6.29: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.30: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.31: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 6.32: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 6.33: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
keyBadge.style.cssText = `
            display: inline-block;
            min-width: 18px;
         ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 6.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 6.35: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 6.36: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 6.37: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 6.38: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 6.39: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 6.40: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 130
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-block;
            min-width: 18px;
            padding: 2px 5px;
     ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 6.41: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 173
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 6.42: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 174
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.43: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 176
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
keyBadge.style.background = "#4a9c6d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.44: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 181
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#c9a227"
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 6.45: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 182
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.46: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 186
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4a4a4a"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 6.47: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 187
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#333"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 7: terminal/components/ai-provider-dialog.ts

### Gate 7.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.overlay.style.cssText = `
            position: fixed;
            inset: 0;
            backgr...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            position: fixed;
            inset: 0;
            background: rgba(0, 0, 0, 0.6);
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 7.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.6: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 7.7: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.8: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.9: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.10: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.11: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
this.dialog.style.cssText = `
            background: #1e1e1e;
            border: 1px solid #444;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.12: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 7.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.15: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 7.16: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.17: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.18: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.19: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.20: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 144
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            background: #1e1e1e;
            border: 1px solid #444;
            border-radius: 8px...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 323
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.22: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 323
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.23: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 323
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.24: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 323
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.25: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 7.26: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.27: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.28: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.29: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 370
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
                width: 24px;
                height: 24px;
                pad...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.30: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
customColor.style.cssText = `
            width: 32px;
            height: 24px;
            padding...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.31: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
customColor.style.cssText = `
            width: 32px;
            height: 24px;
            padding...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.32: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
customColor.style.cssText = `
            width: 32px;
            height: 24px;
            padding...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.33: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
customColor.style.cssText = `
            width: 32px;
            height: 24px;
            padding...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            width: 32px;
            height: 24px;
            padding: 0;
            border: 1px ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.35: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            width: 32px;
            height: 24px;
            padding: 0;
            border: 1px ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 7.36: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            width: 32px;
            height: 24px;
            padding: 0;
            border: 1px ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.37: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 397
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            width: 32px;
            height: 24px;
            padding: 0;
            border: 1px ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 7.38: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 419
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
colorLabel.style.cssText = `
            font-size: 11px;
            color: #888;
            margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.39: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 419
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
colorLabel.style.cssText = `
            font-size: 11px;
            color: #888;
            margi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 7.40: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 419
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            font-size: 11px;
            color: #888;
            margin-left: 8px;
            fon...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 7.41: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 419
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            font-size: 11px;
            color: #888;
            margin-left: 8px;
            fon...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

## Phase 8: terminal/components/ai-footer.ts

### Gate 8.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.footerEl.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
this.footerEl.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.3: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.footerEl.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.4: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.footerEl.style.cssText = `
            display: flex;
            align-items: center;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.7: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.8: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sep.style.cssText = `
            width: 1px;
            height: 20px;
            background: #444...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.10: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
sep.style.cssText = `
            width: 1px;
            height: 20px;
            background: #444...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.11: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
sep.style.cssText = `
            width: 1px;
            height: 20px;
            background: #444...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.12: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            width: 1px;
            height: 20px;
            background: #444;
            margin:...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.13: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            width: 1px;
            height: 20px;
            background: #444;
            margin:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.14: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 145
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            width: 1px;
            height: 20px;
            background: #444;
            margin:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.16: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.17: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.18: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.19: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
addBtn.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.22: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.23: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.24: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 166
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.25: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 184
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.26: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 185
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.color = "#aaa"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 188
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.borderColor = "#555"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.28: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 189
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addBtn.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.29: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.30: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.31: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.32: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.33: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.34: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.35: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.36: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.37: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.38: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.39: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.40: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.41: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.42: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 218
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.43: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.44: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.45: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.46: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.47: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.48: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.cssText = `
                    display: flex;
                    align-item...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.49: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.50: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.51: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.52: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.53: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.54: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 233
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    align-items: center;
                    gap...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.55: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 269
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.56: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 270
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.color = "#aaa"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.57: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 273
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.borderColor = "#666"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.58: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sessionIndicator.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.59: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
activeLabel.style.cssText = `
            margin-left: ${showedSessionIndicator ? "8px" : "auto"};
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.60: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
activeLabel.style.cssText = `
            margin-left: ${showedSessionIndicator ? "8px" : "auto"};
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.61: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 8.62: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 8.63: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.64: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.65: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.66: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.67: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.68: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.69: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.70: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
panel.style.cssText = `
            position: absolute;
            bottom: 100%;
            right:...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.71: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 8.72: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 8.73: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.74: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.75: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.76: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.77: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.78: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.79: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.80: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 342
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            position: absolute;
            bottom: 100%;
            right: 8px;
            margi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.81: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 8.82: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 8.83: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
header.style.cssText = `
            display: flex;
            align-items: center;
            jus...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.84: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: space-betw...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 8.85: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: space-betw...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 8.86: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 364
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: space-betw...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.87: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
title.style.cssText = `
            font-size: 10px;
            font-weight: 600;
            color...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.88: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
title.style.cssText = `
            font-size: 10px;
            font-weight: 600;
            color...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.89: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            font-size: 10px;
            font-weight: 600;
            color: var(--color-text-seco...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.90: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            font-size: 10px;
            font-weight: 600;
            color: var(--color-text-seco...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.91: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.92: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.93: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.94: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.95: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.96: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
newBtn.style.cssText = `
            display: flex;
            align-items: center;
            gap...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.97: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.98: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.99: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.100: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.101: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.102: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 390
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 4px;
            paddi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.103: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 406
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.borderColor = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.104: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 407
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.color = "#ccc"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.105: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 410
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.borderColor = "#555"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.106: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 411
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newBtn.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.107: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
            display: flex;
            align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.108: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
            display: flex;
            align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.109: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
            display: flex;
            align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.110: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
            display: flex;
            align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.111: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.112: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.113: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.114: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.115: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 478
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.background = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.116: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 483
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.117: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 508
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
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
Use --space-* variable
```

### Gate 8.118: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 508
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            flex: 1;
            overflow-y: auto;
            padding: 4px;
        
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.119: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                        padding: 16px 8px;
                        text-alig...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.120: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                        padding: 16px 8px;
                        text-alig...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.121: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                        padding: 16px 8px;
                        text-alig...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.122: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                        padding: 16px 8px;
                        text-align: center;
            ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.123: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                        padding: 16px 8px;
                        text-align: center;
            ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.124: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 524
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                        padding: 16px 8px;
                        text-align: center;
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.125: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                padding: 16px 8px;
                text-align: center;
     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.126: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                padding: 16px 8px;
                text-align: center;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.127: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
empty.style.cssText = `
                padding: 16px 8px;
                text-align: center;
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.128: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                padding: 16px 8px;
                text-align: center;
                color: var(-...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.129: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                padding: 16px 8px;
                text-align: center;
                color: var(-...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.130: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 547
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                padding: 16px 8px;
                text-align: center;
                color: var(-...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.131: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 599
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.132: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 599
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.133: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 599
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.134: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 611
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
item.style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.135: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 631
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
content.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.136: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 631
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 6px;
            flex:...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.137: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
icon.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.138: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
icon.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.139: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
icon.style.cssText = `
            display: flex;
            align-items: center;
            justi...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.140: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 671
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
nameRow.style.cssText = `
            font-size: 10px;
            color: var(--color-text-primary, ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 8.141: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 671
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
nameRow.style.cssText = `
            font-size: 10px;
            color: var(--color-text-primary, ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.142: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 671
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            font-size: 10px;
            color: var(--color-text-primary, #ffffff);
            whi...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 8.143: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 671
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            font-size: 10px;
            color: var(--color-text-primary, #ffffff);
            whi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.144: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.145: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.146: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.147: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.148: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
statusBadge.style.cssText = `
            margin-left: 4px;
            font-size: 7px;
            ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.149: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
boundBadge.style.cssText = `
                margin-left: 4px;
                font-size: 7px;
     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.150: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
boundBadge.style.cssText = `
                margin-left: 4px;
                font-size: 7px;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.151: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
boundBadge.style.cssText = `
                margin-left: 4px;
                font-size: 7px;
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.152: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
boundBadge.style.cssText = `
                margin-left: 4px;
                font-size: 7px;
     ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.153: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                margin-left: 4px;
                font-size: 7px;
                padding: 1px 3px;...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.154: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                margin-left: 4px;
                font-size: 7px;
                padding: 1px 3px;...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.155: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                margin-left: 4px;
                font-size: 7px;
                padding: 1px 3px;...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.156: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 703
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                margin-left: 4px;
                font-size: 7px;
                padding: 1px 3px;...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.157: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 723
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
meta.style.cssText = `
            font-size: 8px;
            color: var(--color-text-muted, #80808...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.158: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 723
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
meta.style.cssText = `
            font-size: 8px;
            color: var(--color-text-muted, #80808...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.159: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 723
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            font-size: 8px;
            color: var(--color-text-muted, #808080);
        
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.160: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 723
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            font-size: 8px;
            color: var(--color-text-muted, #808080);
        
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 8.161: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 739
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
actions.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.162: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 739
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            gap: 2px;
            opaci...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 8.163: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.164: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.165: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.166: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.167: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.168: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.169: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.170: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 790
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.171: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 815
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.172: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 815
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 8.173: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 822
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.color = "var(--color-text-muted, #808080)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.174: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1002
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: flex;
            align-items: center;
            justif...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.175: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 1002
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: flex;
            align-items: center;
            justif...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 8.176: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1002
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: flex;
            align-items: center;
            justif...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.177: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 1002
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: flex;
            align-items: center;
            justif...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 8.178: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1037
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#444"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 8.179: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 1038
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.color = "#888"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 9: components/tool-permission-editor.ts

### Gate 9.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 9.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 9.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 9.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 9.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 9.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 9.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 9.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 9.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.10: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 9.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.12: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 9.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 9.16: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 9.17: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.18: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 9.19: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 9.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 9.22: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 9.23: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 9.24: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.25: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.26: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.27: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.28: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.29: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.30: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.31: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 9.32: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.33: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.34: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.35: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.36: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.37: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.38: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.39: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 9.40: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 9.41: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 9.42: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 9.43: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 9.44: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 69
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        .tool-perm-editor {
            display: flex;
            flex-direction: column;
        ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 9.45: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 251
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", flexDirection: "column", gap: "6px" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 9.46: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 263
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
<span style="font-size:13px;">+</span> Add Rule
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 9.47: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 442
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", flexDirection: "column", gap: "6px" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 9.48: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 454
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
<span style="font-size:13px;">+</span> Add Directory
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

## Phase 10: components/settings-history-modal.ts

### Gate 10.1: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 95
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.tabsContainer.style.cssText = `
            display: flex;
            gap: 4px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.2: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 95
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.tabsContainer.style.cssText = `
            display: flex;
            gap: 4px;
            pa...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.3: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 95
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            gap: 4px;
            padding: 12px 16px 0;
            bord...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.4: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 95
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            gap: 4px;
            padding: 12px 16px 0;
            bord...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.5: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 113
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
this.contentContainer.style.cssText = `
            flex: 1;
            overflow-y: auto;
         ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.6: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 113
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            flex: 1;
            overflow-y: auto;
            padding: 16px;
        
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.7: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 129
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            padding: 8px 16px;
            background: transparent;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.8: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 129
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            padding: 8px 16px;
            background: transparent;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.9: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
actions.style.cssText = "display: flex; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.10: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 202
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.11: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 210
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
clearBtn.style.cssText = "display: flex; align-items: center; gap: 4px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.12: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 210
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; align-items: center; gap: 4px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.13: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 225
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
empty.style.cssText = "text-align: center; color: var(--color-text-secondary); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.14: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 225
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
text-align: center; color: var(--color-text-secondary); padding: 32px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.15: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 234
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.16: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 234
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; flex-direction: column; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.17: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            background: var(--color-bg-secondary);
            border: 1px so...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.18: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            background: var(--color-bg-secondary);
            border: 1px so...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.19: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            background: var(--color-bg-secondary);
            border: 1px solid var(--color-border...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.20: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 248
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            background: var(--color-bg-secondary);
            border: 1px solid var(--color-border...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.21: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 276
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
meta.style.cssText = "font-size: 12px; color: var(--color-text-muted);"
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.22: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 276
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
font-size: 12px; color: var(--color-text-muted);
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.23: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 288
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
diffContainer.style.cssText = `
            font-size: 12px;
            font-family: monospace;
   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.24: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 288
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            font-size: 12px;
            font-family: monospace;
        
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.25: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
diffLine.style.cssText = `
                    display: flex;
                    gap: 8px;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.26: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
diffLine.style.cssText = `
                    display: flex;
                    gap: 8px;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.27: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
diffLine.style.cssText = `
                    display: flex;
                    gap: 8px;
        ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.28: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    gap: 8px;
                    padding: 4px 8...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.29: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    gap: 8px;
                    padding: 4px 8...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.30: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 308
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    display: flex;
                    gap: 8px;
                    padding: 4px 8...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.31: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
pathSpan.style.cssText = "color: var(--color-text-secondary); min-width: 150px;"
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 10.32: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
pathSpan.style.cssText = "color: var(--color-text-secondary); min-width: 150px;"
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 10.33: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: var(--color-text-secondary); min-width: 150px;
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 10.34: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 321
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
color: var(--color-text-secondary); min-width: 150px;
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 10.35: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
oldSpan.style.cssText = `
                    background: rgba(255, 100, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.36: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
oldSpan.style.cssText = `
                    background: rgba(255, 100, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.37: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
oldSpan.style.cssText = `
                    background: rgba(255, 100, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.38: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    background: rgba(255, 100, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.39: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    background: rgba(255, 100, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.40: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 326
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    background: rgba(255, 100, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.41: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
newSpan.style.cssText = `
                    background: rgba(100, 255, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.42: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
newSpan.style.cssText = `
                    background: rgba(100, 255, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.43: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
newSpan.style.cssText = `
                    background: rgba(100, 255, 100, 0.15);
               ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.44: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                    background: rgba(100, 255, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.45: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                    background: rgba(100, 255, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.46: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 344
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                    background: rgba(100, 255, 100, 0.15);
                    padding: 2px 6px;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.47: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 476
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
empty.style.cssText = "text-align: center; color: var(--color-text-secondary); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.48: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 476
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
text-align: center; color: var(--color-text-secondary); padding: 32px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.49: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 485
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.50: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 485
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; flex-direction: column; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.51: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 499
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            justify-content: space-between;
      ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.52: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 499
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
            display: flex;
            justify-content: space-between;
      ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.53: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 499
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            justify-content: space-between;
            align-items: cen...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.54: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 499
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: flex;
            justify-content: space-between;
            align-items: cen...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.55: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 523
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
meta.style.cssText = "font-size: 12px; color: var(--color-text-muted); margin-top: 2px;"
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.56: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 523
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
font-size: 12px; color: var(--color-text-muted); margin-top: 2px;
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.57: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 534
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
actions.style.cssText = "display: flex; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.58: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 534
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.59: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 693
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
noDiff.style.cssText = "text-align: center; color: var(--color-text-muted); padding: 32px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.60: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 693
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
text-align: center; color: var(--color-text-muted); padding: 32px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.61: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 704
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
legend.style.cssText = `
            display: flex;
            gap: 16px;
            font-size: 12...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.62: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 704
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
legend.style.cssText = `
            display: flex;
            gap: 16px;
            font-size: 12...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.63: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 704
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            display: flex;
            gap: 16px;
            font-size: 12px;
            margin-b...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.64: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 704
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            display: flex;
            gap: 16px;
            font-size: 12px;
            margin-b...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.65: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 715
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<span style="color: rgba(255, 100, 100, 0.8);">Current</span>
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.66: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 720
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
<span style="color: rgba(100, 255, 100, 0.8);">Preset</span>
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.67: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 731
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
list.style.cssText = "display: flex; flex-direction: column; gap: 8px;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.68: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 731
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; flex-direction: column; gap: 8px;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.69: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
                background: var(--color-bg-secondary);
                border...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.70: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
                background: var(--color-bg-secondary);
                border...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.71: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
item.style.cssText = `
                background: var(--color-bg-secondary);
                border...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.72: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                background: var(--color-bg-secondary);
                border: 1px solid var(--colo...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.73: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                background: var(--color-bg-secondary);
                border: 1px solid var(--colo...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 10.74: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 738
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                background: var(--color-bg-secondary);
                border: 1px solid var(--colo...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.75: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 756
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
diffRow.style.cssText = "display: flex; gap: 8px; align-items: center;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.76: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 756
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
display: flex; gap: 8px; align-items: center;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.77: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
currentVal.style.cssText = `
                background: rgba(255, 100, 100, 0.15);
                ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.78: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
currentVal.style.cssText = `
                background: rgba(255, 100, 100, 0.15);
                ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.79: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
currentVal.style.cssText = `
                background: rgba(255, 100, 100, 0.15);
                ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.80: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                background: rgba(255, 100, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.81: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                background: rgba(255, 100, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.82: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 761
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                background: rgba(255, 100, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.83: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
presetVal.style.cssText = `
                background: rgba(100, 255, 100, 0.15);
                p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.84: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
presetVal.style.cssText = `
                background: rgba(100, 255, 100, 0.15);
                p...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.85: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
presetVal.style.cssText = `
                background: rgba(100, 255, 100, 0.15);
                p...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 10.86: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                background: rgba(100, 255, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 10.87: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                background: rgba(100, 255, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 10.88: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 779
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                background: rgba(100, 255, 100, 0.15);
                padding: 2px 6px;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

## Phase 11: components/popover.ts

### Gate 11.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 11.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 11.3: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 11.4: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 11.5: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 11.6: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 11.7: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 48
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    /* Slow spin animation for settings icon only */
    .popover-anchor-settings.popover-anchor-ac...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

## Phase 12: components/modal.ts

### Gate 12.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 12.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 12.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 12.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 12.10: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-secondary)
```

### Gate 12.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.12: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 12.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 12.16: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.17: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.18: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.19: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 12.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.22: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.23: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.24: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 12.25: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.26: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.27: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.28: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.29: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.30: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.31: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.32: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.33: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.34: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.35: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.36: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.37: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.38: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.39: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.40: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.41: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 12.42: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 12.43: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 12.44: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 12.45: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 12.46: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 12.47: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 125
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        @keyframes modalOverlayFadeIn {
            from { opacity: 0; }
            to { opacity: ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 12.48: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 786
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
message.style.cssText = "padding: 4px 0; white-space: pre-wrap;"
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 12.49: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 786
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
padding: 4px 0; white-space: pre-wrap;
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

## Phase 13: components/form-controls.ts

### Gate 13.1: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 209
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
input.style.cssText =
        "position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 13.2: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 209
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
input.style.cssText =
        "position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 13.3: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 210
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);bord...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 13.4: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 210
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);bord...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

## Phase 14: components/environment-modal.ts

### Gate 14.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 116
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "4px",
                borderBottom: "1px so...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 14.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 116
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "4px",
                borderBottom: "1px so...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 14.3: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                display: "flex",
                flexDirection: "column...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 14.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 14.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.8: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.9: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 154
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            padding: 8px 16px;
            background: ${isActive ? "var(--col...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.10: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 187
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                justifyContent: "space-between",
                ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 14.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 196
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-secondary, #cccccc)",
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.12: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 196
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-secondary, #cccccc)",
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 14.15: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.16: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.17: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
addButton.style.cssText = `
            padding: 6px 12px;
            background: var(--color-butto...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 14.18: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: var(--color-button-primary, #0e639c);
      ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.19: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: var(--color-button-primary, #0e639c);
      ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 14.20: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: var(--color-button-primary, #0e639c);
      ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.21: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: var(--color-button-primary, #0e639c);
      ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.22: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 212
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            padding: 6px 12px;
            background: var(--color-button-primary, #0e639c);
      ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 14.23: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 229
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                border: "1px solid va...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 14.24: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 229
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                border: "1px solid va...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 14.25: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 229
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                border: "1px solid va...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 14.26: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 240
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
table.style.cssText = `
            width: 100%;
            border-collapse: collapse;
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.27: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 240
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
      ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.28: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bottom: 1px solid var(...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 14.29: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bottom: 1px solid var(...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 14.30: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bottom: 1px solid var(...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.31: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bottom: 1px solid var(...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.32: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

            <tr style="background: var(--color-bg-tertiary, #1e1e1e); border-bottom: 1px solid var(...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.33: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 283
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                <td colspan="3" style="padding: 24px; text-align: center; color: var(--color-text-s...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.34: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 283
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                <td colspan="3" style="padding: 24px; text-align: center; color: var(--color-text-s...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.35: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
row.style.borderBottom = "1px solid var(--color-border, #3c3c3c)"
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 14.36: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 330
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    gap: "8px",
                    justifyCo...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 14.37: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 14.38: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 14.39: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 14.40: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 14.41: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 14.42: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 361
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
button.style.cssText = `
            padding: 4px 8px;
            background: ${isDanger ? "var(--c...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

## Phase 15: components/env-import-wizard.ts

### Gate 15.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 52
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .env-wizard-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 15.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 52
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .env-wizard-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 15.3: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 52
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .env-wizard-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

## Phase 16: components/env-export-wizard.ts

### Gate 16.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 37
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .env-export-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 16.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 37
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .env-export-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 16.3: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 37
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .env-export-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0....
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

## Phase 17: components/debug-panel.ts

### Gate 17.1: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 36
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        .debug-panel {
            position: fixed;
            bottom: 40px;
            right: 16...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 17.2: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 36
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        .debug-panel {
            position: fixed;
            bottom: 40px;
            right: 16...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 17.3: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 36
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

        .debug-panel {
            position: fixed;
            bottom: 40px;
            right: 16...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

## Phase 18: components/context-menu.ts

### Gate 18.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 18.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 18.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 18.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 18.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 18.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 18.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 18.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 18.9: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 18.10: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 18.11: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 18.12: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 18.13: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 18.14: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 18.15: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 18.16: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.17: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.18: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.19: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.20: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.21: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.22: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.23: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.24: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 58
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

    .context-menu-overlay {
      position: fixed;
      inset: 0;
      background: transparent;
 ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 18.25: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 386
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
items[this.focusedIndex].style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 19: components/claude-settings-dialog.ts

### Gate 19.1: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 288
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", flexDirection: "column", gap: "6px" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 293
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", flexDirection: "column", gap: "6px" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 19.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 19.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 19.6: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.7: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 19.8: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 302
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    padding: "12px",
                    textAlign: "center",
                    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.9: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 320
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", gap: "6px", alignItems: "center" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.10: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
keyInput.style.cssText = `
                width: 120px; padding: 6px 8px; font-size: 11px;
        ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 19.11: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
keyInput.style.cssText = `
                width: 120px; padding: 6px 8px; font-size: 11px;
        ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.12: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
keyInput.style.cssText = `
                width: 120px; padding: 6px 8px; font-size: 11px;
        ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 19.13: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                width: 120px; padding: 6px 8px; font-size: 11px;
                font-family: monos...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 19.14: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                width: 120px; padding: 6px 8px; font-size: 11px;
                font-family: monos...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.15: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 331
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

                width: 120px; padding: 6px 8px; font-size: 11px;
                font-family: monos...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 19.16: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{ color: "#666", fontSize: "12px" }
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 19.17: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 340
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{ color: "#666", fontSize: "12px" }
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.18: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
valueInput.style.cssText = `
                flex: 1; padding: 6px 8px; font-size: 11px;
           ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 19.19: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
valueInput.style.cssText = `
                flex: 1; padding: 6px 8px; font-size: 11px;
           ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.20: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                flex: 1; padding: 6px 8px; font-size: 11px;
                font-family: monospace;...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 19.21: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 353
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                flex: 1; padding: 6px 8px; font-size: 11px;
                font-family: monospace;...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.22: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    padding: "4px 8px",
                    fontSize: "11px",
                    ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 19.23: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    padding: "4px 8px",
                    fontSize: "11px",
                    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 19.24: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    padding: "4px 8px",
                    fontSize: "11px",
                    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.25: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                    padding: "4px 8px",
                    fontSize: "11px",
                    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 19.26: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 362
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    padding: "4px 8px",
                    fontSize: "11px",
                    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 19.28: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 19.29: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 19.30: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 19.31: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 19.32: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 389
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
            display: "inline-flex",
            alignItems: "center",
            gap: "4px",
    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 19.33: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 404
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
<span style="font-size:13px;">+</span> Add Variable
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

## Phase 20: components/claude-form-builder.ts

### Gate 20.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 100
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "14px",
                    fontWeight: "bold",
                    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.2: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 100
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "14px",
                    fontWeight: "bold",
                    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.3: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 100
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    fontSize: "14px",
                    fontWeight: "bold",
                    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "block",
                fontSize: "12px",
                fontWeight: "5...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.5: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                display: "block",
                fontSize: "12px",
                fontWeight: "5...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.6: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 133
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "block",
                fontSize: "12px",
                fontWeight: "5...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 141
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
 <span style="color:#ff6b6b">*</span>
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 148
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.9: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 148
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.10: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 148
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.11: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 190
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "10px",
                color: "#ff6b6b",
                marginTop: "4p...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.12: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 190
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "10px",
                color: "#ff6b6b",
                marginTop: "4p...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.13: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 190
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                fontSize: "10px",
                color: "#ff6b6b",
                marginTop: "4p...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.14: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.15: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.16: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.17: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.18: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.19: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 214
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.21: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.22: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 250
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    fontSize: "10px",
                    color: "#888",
                    margi...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.23: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 270
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                gap: "8px",...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.24: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 280
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "16px",
                height: "16px",
                cursor: "pointer",
...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.25: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{ fontSize: "12px", color: "#e0e0e0" }
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.26: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{ fontSize: "12px", color: "#e0e0e0" }
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 310
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.28: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 310
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.29: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 310
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.30: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 330
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.31: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 330
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.32: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 330
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.33: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 330
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 341
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.35: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 341
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.36: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 341
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.37: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 352
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.38: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 352
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.39: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 352
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.40: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.41: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.42: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 376
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.43: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 394
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                padding: "8px",
                background: "#1e1...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 20.44: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 394
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                padding: "8px",
                background: "#1e1...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.45: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.46: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.47: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.48: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.49: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.50: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 408
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.51: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 422
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.52: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 422
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.53: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 422
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.54: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 422
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.55: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 464
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.56: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 464
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.57: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 464
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                border: "1px solid #444",
                borderRadius: "4px",
                ove...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.58: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 486
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.59: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 486
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.60: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 486
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.61: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 486
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        display: "flex",
                        alignItems: "center",
           ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.62: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 498
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        fontSize: "11px",
                        fontFamily: "monospace",
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.63: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 498
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        fontSize: "11px",
                        fontFamily: "monospace",
       ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.64: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 498
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        fontSize: "11px",
                        fontFamily: "monospace",
       ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.65: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 509
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.66: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 509
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.67: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 509
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        flex: "1",
                        fontSize: "11px",
                     ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.68: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 520
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.69: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 520
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.70: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 520
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        background: "transparent",
                        border: "none",
       ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.71: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 545
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.72: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 545
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.73: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 545
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        padding: "12px",
                        textAlign: "center",
            ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.74: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 563
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                padding: "8px",
                background: "#1e1...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 20.75: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 563
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                padding: "8px",
                background: "#1e1...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.76: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.77: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.78: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.79: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.80: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.81: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 574
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "120px",
                padding: "6px 8px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.82: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.83: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.84: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.85: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.86: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.87: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 589
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                padding: "6px 8px",
                background: "#2d2d2...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.88: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 603
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.89: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 603
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.90: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 603
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.91: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 603
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                padding: "6px 12px",
                background: "#4caf50",
                border...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.92: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 20.93: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.94: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.95: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.96: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.97: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 649
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "100%",
                minHeight: "120px",
                padding: "10px"...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.98: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.99: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.100: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.101: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.102: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.103: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.104: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.105: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.106: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.107: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.108: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.109: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 718
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                width: "100%",
                padding: "8px 12px",
                background: "#...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.110: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 744
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "12px",
                background: "#252525",
                borderRadi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.111: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 744
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "12px",
                background: "#252525",
                borderRadi...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.112: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 744
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                padding: "12px",
                background: "#252525",
                borderRadi...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.113: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 744
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                padding: "12px",
                background: "#252525",
                borderRadi...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.114: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 900
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
            display: "flex",
            gap: "8px",
            flexWrap: "wrap",
            mar...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.115: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.116: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.117: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.118: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 20.119: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 20.120: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 911
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                padding: "8px 16px",
                background: "#2d2d2d",
                border...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 20.121: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 926
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#3d3d3d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.122: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 927
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#4fc3f7"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.123: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 930
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = "#2d2d2d"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 20.124: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 931
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.borderColor = "#444"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 21: components/ai-metrics-panel.ts

### Gate 21.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 242
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                textAlign: "center",
                padding: "32px 16px",
                color: ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 242
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                textAlign: "center",
                padding: "32px 16px",
                color: ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 260
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "4px",
                padding: "8px 0",
   ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 21.4: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 260
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "4px",
                padding: "8px 0",
   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.5: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.8: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.9: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 21.10: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 21.11: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 21.12: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 277
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
                display: flex;
                align-items: center;
          ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 21.13: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
iconSpan.style.cssText = "display: flex; width: 14px; height: 14px;"
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 21.14: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
iconSpan.style.cssText = "display: flex; width: 14px; height: 14px;"
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 21.15: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
display: flex; width: 14px; height: 14px;
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 21.16: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
display: flex; width: 14px; height: 14px;
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 21.17: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 313
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
indicator.style.cssText = "color: var(--color-accent-success, #4caf50);"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.18: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 313
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
color: var(--color-accent-success, #4caf50);
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.19: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 452
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "flex-end",
                gap: "2px...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.20: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 470
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    flex: "1",
                    height: `${Math.max(heightPercent, 2)}%`,
     ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 21.21: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 470
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                    flex: "1",
                    height: `${Math.max(heightPercent, 2)}%`,
     ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 21.22: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 470
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    flex: "1",
                    height: `${Math.max(heightPercent, 2)}%`,
     ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.23: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 532
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                flexDirection: "column",
                gap: "4p...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.24: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 544
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 21.25: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 544
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.26: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 556
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    width: "6px",
                    height: "6px",
                    borderRad...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.27: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 556
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    width: "6px",
                    height: "6px",
                    borderRad...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.28: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 556
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    width: "6px",
                    height: "6px",
                    borderRad...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.29: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 569
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                        display: "flex",
                        width: "12px",
                  ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.30: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 583
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    flex: "1",
                    overflow: "hidden",
                    textOve...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 21.31: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 583
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    flex: "1",
                    overflow: "hidden",
                    textOve...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.32: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 597
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    color: "var(--color-text-muted, #808080)",
                }
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 21.33: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 624
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{ display: "flex", alignItems: "center", gap: "4px" }
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 21.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 629
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{ display: "flex", color: "var(--color-text-muted, #808080)" }
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 22: components/ai-history-modal.ts

### Gate 22.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 242
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "2px",
                padding: "8px 12px",
...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.2: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 242
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                gap: "2px",
                padding: "8px 12px",
...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.3: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 258
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.4: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 258
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 22.5: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 258
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.6: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.7: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.8: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.9: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.10: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.11: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.cssText = `
                display: flex;
                align-items: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.12: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.13: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.14: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.15: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.16: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.17: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 274
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

                display: flex;
                align-items: center;
                gap: 4px;
     ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.18: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 289
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.background = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.19: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 294
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
clearAllBtn.style.color = "var(--color-accent-error, #ef4444)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.20: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 305
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: "1",
                overflow: "auto",
                padding: "12px",
    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.21: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 318
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.22: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 318
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 22.23: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 318
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.24: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.25: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.26: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.27: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.28: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.29: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 351
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
tab.style.cssText = `
            display: flex;
            align-items: center;
            gap: 6...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.30: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
iconSpan.style.cssText = `
            display: flex;
            align-items: center;
            j...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.31: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
iconSpan.style.cssText = `
            display: flex;
            align-items: center;
            j...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.32: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.33: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 371
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: flex;
            align-items: center;
            justify-content: center;
  ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.34: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.35: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.36: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.37: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.38: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 392
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
countBadge.style.cssText = `
                font-size: 10px;
                padding: 1px 5px;
    ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.39: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 406
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.background = "var(--color-bg-hover, #2a2a2a)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.40: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 415
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
tab.style.color = "var(--color-text-secondary, #cccccc)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.41: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 441
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    textAlign: "center",
                    padding: "40px 20px",
               ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.42: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 441
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    textAlign: "center",
                    padding: "40px 20px",
               ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.43: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 455
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                flexDirection: "column",
                gap: "8p...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.44: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 477
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                flex: isExpanded ? "1" : "0 0 auto",
                minHeight: isExpanded ? "120p...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.45: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 477
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                flex: isExpanded ? "1" : "0 0 auto",
                minHeight: isExpanded ? "120p...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.46: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 477
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                flex: isExpanded ? "1" : "0 0 auto",
                minHeight: isExpanded ? "120p...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.47: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 491
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                gap: "8px",...
```
- **Suggested Fix**:
```typescript
var(--color-bg-primary)
```

### Gate 22.48: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 491
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                gap: "8px",...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.49: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 505
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                color: "var...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.50: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 505
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                color: "var...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.51: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 522
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                gap: "8px",...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.52: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 532
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontWeight: "500",
                color: "var(--color-text-primary, #ffffff)",
  ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 22.53: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 543
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "9px",
                padding: "1px 4px",
                borderRadius:...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.54: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 543
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "9px",
                padding: "1px 4px",
                borderRadius:...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.55: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 543
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "9px",
                padding: "1px 4px",
                borderRadius:...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.56: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 543
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
{
                fontSize: "9px",
                padding: "1px 4px",
                borderRadius:...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.57: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 543
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                fontSize: "9px",
                padding: "1px 4px",
                borderRadius:...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.58: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 559
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "11px",
                color: "var(--color-text-muted, #808080)",
     ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.59: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 559
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "11px",
                color: "var(--color-text-muted, #808080)",
     ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.60: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 627
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    flex: "1",
                    minHeight: "0",
                    borderTop: ...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.61: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 627
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    flex: "1",
                    minHeight: "0",
                    borderTop: ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.62: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 645
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                            padding: "12px",
                            textAlign: "center",
    ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.63: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 645
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                            padding: "12px",
                            textAlign: "center",
    ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.64: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 645
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                            padding: "12px",
                            textAlign: "center",
    ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.65: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 675
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "flex-start",
                gap: "8...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.66: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 675
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "flex-start",
                gap: "8...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.67: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 675
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "flex-start",
                gap: "8...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.68: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 698
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
container.style.background = checked ? "var(--color-bg-hover, #2a2a2a)" : "transparent"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.69: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 708
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                justifyCont...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.70: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 708
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "flex",
                alignItems: "center",
                justifyCont...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.71: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 734
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "10px",
                fontWeight: "600",
                color: isUser...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.72: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 734
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "10px",
                fontWeight: "600",
                color: isUser...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.73: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 734
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                fontSize: "10px",
                fontWeight: "600",
                color: isUser...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.74: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 746
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-primary, #ffffff)",
   ...
```
- **Suggested Fix**:
```typescript
var(--color-text-primary)
```

### Gate 22.75: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 746
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-primary, #ffffff)",
   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.76: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 746
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-primary, #ffffff)",
   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.77: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 764
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    gap: "2px",
                    flexShrin...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 22.78: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 812
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-secondary, #cccccc)",
 ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.79: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 812
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                fontSize: "12px",
                color: "var(--color-text-secondary, #cccccc)",
 ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.80: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 22.81: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
var(--color-border)
```

### Gate 22.82: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.83: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.84: HARDCODED_SPACING
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: warning
- **Conditions**:
  - [ ] Spacing uses CSS variables
- **Failure Reason**: Hardcoded spacing inconsistent with design system
- **Remediation**: Replace with CSS variable from --space-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --space-* variable
```

### Gate 22.85: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 22.86: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 825
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
sendBtn.style.cssText = `
            display: flex;
            align-items: center;
            ga...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.87: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.88: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.89: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.90: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript
btn.style.cssText = `
            display: inline-flex;
            align-items: center;
           ...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.91: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.92: HARDCODED_RADIUS
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: warning
- **Conditions**:
  - [ ] Border radius uses CSS variables
- **Failure Reason**: Hardcoded border-radius inconsistent with design system
- **Remediation**: Replace with CSS variable from --radius-* tokens
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --radius-* variable
```

### Gate 22.93: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.94: FIXED_DIMENSION
- **Status**: ❌ FAILED
- **Line**: 871
- **Severity**: info
- **Conditions**:
  - [ ] Dimensions are responsive
- **Failure Reason**: Fixed pixel dimension may not be responsive
- **Remediation**: Consider using rem, %, or flex for responsive sizing
- **Current Code**:
```typescript

            display: inline-flex;
            align-items: center;
            justify-content: cen...
```
- **Suggested Fix**:
```typescript
Use --size-* variable
```

### Gate 22.95: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 889
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 22.96: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 889
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.background = isDanger
                ? "var(--color-accent-error, #ef4444)"
             ...
```
- **Suggested Fix**:
```typescript
var(--color-accent-primary)
```

### Gate 22.97: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 896
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
btn.style.color = "var(--color-text-muted, #808080)"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Phase 23: components/claude-hub/tabs/runtime/sessions-tab.ts

### Gate 23.1: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 66
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                display: "grid",
                gridTemplateColumns: "300px 1fr",
               ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

### Gate 23.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 176
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 23.3: HARDCODED_FONT_SIZE
- **Status**: ❌ FAILED
- **Line**: 176
- **Severity**: warning
- **Conditions**:
  - [ ] Font sizes use CSS variables
- **Failure Reason**: Hardcoded font-size bypasses typography system
- **Remediation**: Replace with CSS variable from --font-size-* tokens
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Use --font-size-* variable
```

### Gate 23.4: STYLE_OBJECT
- **Status**: ❌ FAILED
- **Line**: 176
- **Severity**: warning
- **Conditions**:
  - [ ] Styling uses CSS classes
- **Failure Reason**: Inline style object should use CSS class
- **Remediation**: Create CSS class and use className instead
- **Current Code**:
```typescript
{
                    display: "flex",
                    alignItems: "center",
                   ...
```
- **Suggested Fix**:
```typescript
Move styles to CSS class and use className
```

## Phase 24: components/claude-hub/tabs/config/local-tab.ts

### Gate 24.1: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 124
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
content.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

### Gate 24.2: HARDCODED_COLOR
- **Status**: ❌ FAILED
- **Line**: 167
- **Severity**: error
- **Conditions**:
  - [ ] Colors use CSS variables
- **Failure Reason**: Hardcoded color bypasses theme system
- **Remediation**: Replace with CSS variable from --color-* tokens
- **Current Code**:
```typescript
empty.style.color = "#666"
```
- **Suggested Fix**:
```typescript
Use --color-* variable
```

## Recommendations

1. Fix 534 hardcoded color values - use --color-* variables
2. Fix 226 hardcoded spacing values - use --space-* variables
3. Fix 154 hardcoded font sizes - use --font-size-* variables
4. Review 87 inline style objects - consider CSS classes
5. Review 116 fixed dimensions for responsiveness
6. Consider creating utility CSS classes for common patterns

## Migration Phases

### Phase 1: Colors
- **Count**: 534 violations
- **Factory Method**: --color-* tokens
- **Priority**: 1

### Phase 2: Spacing
- **Count**: 226 violations
- **Factory Method**: --space-* tokens
- **Priority**: 2

### Phase 3: Typography
- **Count**: 154 violations
- **Factory Method**: --font-size-* tokens
- **Priority**: 3

### Phase 4: Borders
- **Count**: 118 violations
- **Factory Method**: --radius-* tokens
- **Priority**: 4

### Phase 5: Style Objects
- **Count**: 87 violations
- **Factory Method**: CSS classes
- **Priority**: 5

ALWAYS use DOM factory methods for element creation
ALWAYS use addManagedListener for event binding
ALWAYS use CSS tokens instead of hardcoded values
NEVER use document.createElement directly
NEVER use innerHTML for icon assignment
NEVER add event listeners without managed cleanup
