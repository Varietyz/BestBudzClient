---
name: listener-pattern-remediation
type: checklist
version: 1.0.0
generated: 2026-01-03T13:05:53.868Z
---

THIS CHECKLIST TRACKS Event listener pattern violations requiring remediation

%% META %%:
intent: "Ensure all event listeners have managed lifecycle cleanup"
objective: "Zero unmanaged listeners, proper render patterns"

## Validation Summary

**Total Gates**: 17
**Passed**: 0
**Failed**: 17
**Pass Rate**: 0%

## Phase 1: terminal/components/terminal-sidebar.ts

### Gate 1.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 146
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class TerminalSidebar extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class TerminalSidebar extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 2: terminal/components/terminal-header.ts

### Gate 2.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 141
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class TerminalHeader extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class TerminalHeader extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 3: terminal/components/terminal-chat.ts

### Gate 3.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 96
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class TerminalChat extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class TerminalChat extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 4: terminal/components/ai-sidebar.ts

### Gate 4.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 149
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class AISidebar extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class AISidebar extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 5: terminal/components/ai-provider-dialog.ts

### Gate 5.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 89
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class AIProviderDialog extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class AIProviderDialog extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 6: terminal/components/ai-footer.ts

### Gate 6.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 53
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class AIFooter extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class AIFooter extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 7: engine/base/base-event-component.ts

### Gate 7.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 296
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class BaseRenderComponent extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class BaseRenderComponent extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 8: components/tool-permission-editor.ts

### Gate 8.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 211
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class ToolPermissionEditor extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class ToolPermissionEditor extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 9: components/toast.ts

### Gate 9.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 52
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class ToastManager extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class ToastManager extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 10: components/taskbar.ts

### Gate 10.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 105
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class Taskbar extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class Taskbar extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 11: components/settings-history-modal.ts

### Gate 11.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 31
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class SettingsHistoryModal extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class SettingsHistoryModal extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 12: components/popover.ts

### Gate 12.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 685
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class Popover extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class Popover extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 13: components/header-bar.ts

### Gate 13.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 66
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class HeaderBar extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class HeaderBar extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 14: components/env-import-wizard.ts

### Gate 14.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 249
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class EnvImportWizard extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class EnvImportWizard extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 15: components/env-export-wizard.ts

### Gate 15.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 169
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class EnvExportWizard extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class EnvExportWizard extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 16: components/debug-panel.ts

### Gate 16.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 189
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class DebugPanel extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class DebugPanel extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Phase 17: components/context-menu.ts

### Gate 17.1: SHOULD_EXTEND_BASE_RENDER
- **Status**: ❌ FAILED
- **Line**: 153
- **Severity**: warning
- **Conditions**:
  - [ ] Component uses BaseRenderComponent for render lifecycle
- **Failure Reason**: Component has render pattern but extends BaseEventComponent
- **Remediation**: Migrate to BaseRenderComponent for automatic lifecycle management
- **Current Code**:
```typescript
class ContextMenu extends BaseEventComponent
```
- **Suggested Fix**:
```typescript
class ContextMenu extends BaseRenderComponent {
    protected createContent(): HTMLElement {
        // Move render() logic here
    }
}
```

## Recommendations

1. Fix 0 unmanaged addEventListener calls
2. Add clearManagedListeners() to 0 render methods
3. Fix 0 cleanup() misuses in hide() methods
4. Consider migrating 17 components to BaseRenderComponent
5. Run render-pattern-scanner.cjs for detailed pattern analysis

## Migration Phases

### Phase 1: Unmanaged Listeners
- **Count**: 0 violations
- **Factory Method**: addManagedListener()
- **Priority**: 1

### Phase 2: Render Pattern Fix
- **Count**: 0 violations
- **Factory Method**: clearManagedListeners()
- **Priority**: 2

### Phase 3: Hide Pattern Fix
- **Count**: 0 violations
- **Factory Method**: reset()
- **Priority**: 3

### Phase 4: Base Class Migration
- **Count**: 17 violations
- **Factory Method**: BaseRenderComponent
- **Priority**: 4

ALWAYS use DOM factory methods for element creation
ALWAYS use addManagedListener for event binding
ALWAYS use CSS tokens instead of hardcoded values
NEVER use document.createElement directly
NEVER use innerHTML for icon assignment
NEVER add event listeners without managed cleanup
