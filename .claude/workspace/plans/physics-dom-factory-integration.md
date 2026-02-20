# Physics-Enabled DOM Factory & Chaos Panel

## Overview

Integrate physics simulation into DOM Factory, enabling any element to participate in chaos mode physics. Create a dedicated Chaos Panel for real-time physics configuration with entity-type presets.

**Vision**: Work hard / Play hard environment where chaos mode transforms the app into a playful, game-driven experience.

---

## File Structure

```
src/.archtypes/types/
├── physics-manager-types.ts          # NEW: PhysicsOptions, PhysicsPreset, PhysicsConstants
└── physics-settings-types.ts         # NEW: PhysicsSettings for AppSettings

src/renderer/managers/
├── physics-manager.ts                # NEW: DOM Factory PhysicsManager (extends BaseManager)
└── physics-shape-manager.ts          # MODIFY: Extract constants, add composition hooks

src/renderer/helpers/
└── physics-manager-helper.ts         # NEW: Singleton accessor

src/renderer/components/
├── chaos-panel-component.ts          # NEW: Physics configuration panel
└── settings-panel-component.ts       # MODIFY: Add Chaos Panel access button

src/renderer/factories/
└── dom-factory.ts                    # MODIFY: Add physics reserved key

src/renderer/services/
└── manager-tier-service.ts           # MODIFY: Add physics to lazy managers

src/renderer/styles/components/chaos-panel/
├── chaos-panel.css                   # NEW: Panel styling
└── index.css                         # NEW: Barrel export
```

---

## Type Definitions

### `physics-manager-types.ts`

```typescript
export interface PhysicsConstants {
  friction: number;           // 0.992 default
  angularFriction: number;    // 0.988 default
  impulseStrength: number;    // 0.18 default
  pushRadius: number;         // 20 default
  massMultiplier: number;     // 2.5 default
  restitution: number;        // 0.6 default
  torqueEdgeThreshold: number; // 0.6 default
}

export interface PhysicsPreset {
  id: string;
  entityType: string;         // "window", "button", "panel", etc.
  enabled: boolean;
  mass: number;
  radius: number;
  depthLayer: "far" | "mid" | "near" | "deep";  // Consistent with geometric background
  constants?: Partial<PhysicsConstants>;
}

export interface PhysicsOptions {
  enabled?: boolean;          // Must be explicitly true (disabled by default)
  mass?: number;
  radius?: number;
  depthLayer?: "far" | "mid" | "near" | "deep";
  constants?: Partial<PhysicsConstants>;
  chaosMode?: boolean;        // Override global
  // Collision events emitted via EventBus (LAW 2 compliant)
  // Listen for: "physics:collision" intent with payload { element, other, force }
}

export const DEFAULT_PHYSICS_PRESETS: PhysicsPreset[] = [
  { id: "window", entityType: "window", enabled: false, mass: 10, radius: 50, depthLayer: "near" },
  { id: "panel", entityType: "panel", enabled: false, mass: 5, radius: 30, depthLayer: "mid" },
  { id: "card", entityType: "card", enabled: false, mass: 3, radius: 20, depthLayer: "mid" },
  { id: "button", entityType: "button", enabled: false, mass: 0.5, radius: 10, depthLayer: "far" },
  { id: "sidebar", entityType: "sidebar", enabled: false, mass: 15, radius: 60, depthLayer: "near" },
];
```

### `physics-settings-types.ts`

```typescript
export interface PhysicsSettings {
  enabled: boolean;
  chaosMode: boolean;
  globalConstants: PhysicsConstants;
  presets: PhysicsPreset[];
}
```

---

## PhysicsManager (extends BaseManager)

```typescript
export class PhysicsManager extends BaseManager {
  private readonly physicsStates = new WeakMap<HTMLElement, PhysicsState>();
  private presets: Map<string, PhysicsPreset> = new Map();

  constructor(uiStore: UIStore, elementRegistry: ElementRegistry) {
    super(uiStore, elementRegistry);
    ManagerRegistry.register("physics", this);
  }

  apply(element: HTMLElement, options: PhysicsOptions | boolean): void {
    // Resolve preset from entity type
    // Register with PhysicsShapeManager
  }

  cleanup(element: HTMLElement): void {
    // Unregister from PhysicsShapeManager
  }

  updateGlobalConstants(constants: Partial<PhysicsConstants>): void;
  updatePreset(entityType: string, preset: Partial<PhysicsPreset>): void;
}
```

---

## DOM Factory Integration

```typescript
// In createElement(), after drag handling:
if (options.physics) {
  const manager = await this.getLazyManager("physics");
  manager.apply(el, options.physics);
}
```

**Usage:**
```typescript
const panel = await DOM.createElement("div", {
  entity: "panel",
  physics: true  // Uses "panel" preset
});

const window = await DOM.createElement("div", {
  entity: "window",
  physics: { mass: 15, chaosMode: true }  // Override preset
});
```

---

## Chaos Panel Component

**Features:**
- Global chaos mode toggle
- Physics constant sliders (friction, restitution, impulse, push radius, mass scale)
- Entity-type preset editor (enable/disable, mass, radius per type)
- Real-time preview area

**Access:** Settings Panel > Graphics > "Chaos Panel" button

---

## Implementation Steps

### Phase 1: Types & Foundation
1. Create `physics-manager-types.ts` with interfaces and defaults
2. Create `physics-settings-types.ts` with PhysicsSettings
3. Export types via Antenna registry

### Phase 2: PhysicsManager
4. Create `physics-manager.ts` extending BaseManager
5. Create `physics-manager-helper.ts` singleton accessor
6. Add "physics" to `manager-tier-service.ts` lazy managers

### Phase 3: DOM Factory Integration
7. Add PhysicsOptions to ReservedKeyOptions in `manager-types.ts`
8. Add physics handling in `dom-factory.ts` createElement
9. Register types in Antenna exports

### Phase 4: Chaos Panel
10. Create `chaos-panel-component.ts` with sliders and preset editor
11. Create `chaos-panel.css` styling
12. Add "Chaos Panel" button to settings-panel graphics section

### Phase 5: PhysicsShapeManager Refactor
13. Extract hardcoded constants to use PhysicsSettings
14. Add hooks for PhysicsManager composition
15. Sync chaos mode between both managers

---

## Verification

1. **Type Check**: `npm run ts-check` - no type errors
2. **Lint**: `npm run lint` - passes
3. **Codebase Validation**: `npm run verify-codebase 2>&1 | tail -100` - no new violations
4. **Manual Testing**:
   - Create element with `physics: true` - should register with physics engine
   - Toggle chaos mode - all physics-enabled elements affected
   - Adjust sliders in Chaos Panel - real-time physics changes
   - Save preset - persists across restart

---

## Critical Files

| File | Action | Purpose |
|------|--------|---------|
| `src/.archtypes/types/physics-manager-types.ts` | CREATE | Type definitions |
| `src/.archtypes/types/physics-settings-types.ts` | CREATE | Settings types |
| `src/renderer/managers/physics-manager.ts` | CREATE | DOM Factory manager |
| `src/renderer/helpers/physics-manager-helper.ts` | CREATE | Singleton accessor |
| `src/renderer/factories/dom-factory.ts` | MODIFY | Add physics key |
| `src/renderer/services/manager-tier-service.ts` | MODIFY | Add lazy loader |
| `src/renderer/components/chaos-panel-component.ts` | CREATE | Config panel |
| `src/renderer/components/settings-panel-component.ts` | MODIFY | Add panel access |
| `src/.archtypes/types/manager-types.ts` | MODIFY | Add PhysicsOptions |

---

## Open Questions

1. **Preset inheritance**: Should entity subtypes inherit from parent types? (e.g., "modal" inherits from "window")
2. **Collision callbacks**: Should we support collision events via EventBus (LAW 2 compliant) or direct callbacks?
3. **Performance**: Should physics be disabled by default for non-chaos elements to reduce overhead?
4. **Depth layers**: Keep "front/mid/back" or align with existing "far/mid/near/deep" from geometric background?
