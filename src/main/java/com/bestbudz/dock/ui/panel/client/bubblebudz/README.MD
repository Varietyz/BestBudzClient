# 🫧 Bubble Budz - Interactive Loading Panel Mini-Game

**A modular, extensible bubble-popping mini-game with plugin architecture for easy customization and feature additions.**

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Folder Structure](#folder-structure)
4. [Core Systems](#core-systems)
5. [Key Classes](#key-classes)
6. [Dependencies](#dependencies)
7. [Adding New Features](#adding-new-features)
8. [Classes to Focus On](#classes-to-focus-on)
9. [Classes to Ignore](#classes-to-ignore)
10. [Development Guidelines](#development-guidelines)
11. [Examples](#examples)

---

## 🎯 Overview

Bubble Budz is a **skill-based mini-game** where players click bubbles for points within timed rounds. The game features:

- **Dynamic bubble spawning** with physics-based animations
- **Skill-based scoring** (smaller bubbles = more points)
- **Visual effects system** with modular pop effects
- **Persistent high scores** across sessions
- **Extensible architecture** for easy feature additions

### Game Mechanics
- **30-second rounds** with continuous scoring
- **Bubble lifecycle**: Spawn → Grow → Shrink → Fade → Despawn
- **Generous click detection** for better user experience
- **Viewport scaling bonus** for larger screens
- **Automatic round transitions** with high score persistence

---

## 🏗️ Architecture

### Design Principles

**1. Event-Driven Architecture**
- Components communicate through events rather than direct coupling
- Easy to add new systems that react to game events
- Clean separation of concerns

**2. Plugin-Based Systems**
- Game modes, effects, input handlers, and renderers are pluggable
- Add new features without modifying existing code
- Factory pattern for dynamic loading

**3. Modular Rendering Pipeline**
- Composable render components with defined order
- Easy to add visual effects, UI elements, or debug overlays
- Clean separation of rendering logic

**4. Clear Dependency Hierarchy**
```
ui/ (Top Level - Orchestration)
  ↓
game/ (Game Logic)
  ↓
systems/ (Input, Effects, Rendering, Assets)
  ↓
core/ (Events, State, Base Game Logic)
```

---

## 📁 Folder Structure

```
📁 bubblebudz/
├── 📁 core/                           # Core game systems (FOUNDATION)
│   ├── 🔵 BubbleBudzGame.java         # Main game logic & bubble management
│   ├── 🔵 GameEventManager.java        # Event system for loose coupling
│   └── 🔵 GameStateManager.java        # State transitions & management
│
├── 📁 game/                           # Game-specific logic (GAMEPLAY)
│   ├── 📁 modes/                      # Game mode implementations
│   │   ├── 🟡 GameMode.java           # Abstract game mode base class
│   │   └── 🔵 ClassicGameMode.java    # 30-second timed rounds
│   ├── 📁 entities/                   # Game object definitions
│   │   └── 🔵 Bubble.java             # Bubble entity (extracted from BubbleBudzGame)
│   └── 📁 scoring/                    # Scoring system
│       ├── 🔵 BubbleBudzScore.java    # Score calculation algorithms
│       └── 🔵 BubbleBudzScoreManager.java # High score persistence
│
├── 📁 system/                        # Major subsystems (INFRASTRUCTURE)
│   ├── 📁 input/                      # Input handling abstraction
│   │   ├── 🟡 InputHandler.java       # Input interface
│   │   └── 🔵 BubbleInputHandler.java # Mouse click → bubble interaction
│   ├── 📁 effects/                    # Visual effects system
│   │   ├── 🟡 Effect.java             # Effect interface
│   │   ├── 🔵 EffectData.java         # Effect parameters
│   │   ├── 🟡 EffectFactory.java      # Effect creation interface
│   │   ├── 🔵 EffectLoader.java       # Dynamic effect discovery
│   │   ├── 🔵 EffectManager.java      # Effect lifecycle management
│   │   └── 📁 types/                  # Effect implementations
│   │       └── 📁 pop/                # Pop effect on bubble click
│   │           ├── 🔵 PopEffect.java
│   │           └── 🔵 PopFactory.java
│   ├── 📁 render/                     # Modular rendering pipeline
│   │   ├── 🔵 RenderManager.java      # Render component coordination
│   │   ├── 🔵 RenderContext.java      # Rendering state & data
│   │   ├── 🟡 RenderComponent.java    # Render component interface
│   │   └── 📁 components/             # Specific renderers
│   │       ├── 🔵 BackgroundRenderer.java  # Game background
│   │       ├── 🔵 BubbleRenderer.java      # Bubble drawing
│   │       ├── 🔵 EffectRenderer.java      # Visual effects overlay
│   │       ├── 🔵 HeaderRenderer.java      # Score, timer, logo
│   │       ├── 🔵 FooterRenderer.java      # Instructions & info
│   │       └── 🔵 LoadingRenderer.java     # Loading animations
│   └── 📁 assets/                     # Resource management
│       └── 🔵 AssetManager.java       # Image loading & caching
│
├── 📁 data/                           # Data persistence (I/O)
│   ├── 🔵 BubbleBudzLoader.java       # Load game data from disk
│   └── 🔵 BubbleBudzSaving.java       # Save game data to disk
│
├── 📁 ui/                             # User interface (PRESENTATION)
│   ├── 🔵 BubbleBudzPanel.java        # Main Swing panel (ENTRY POINT)
│   ├── 🔵 BubbleBudzStyle.java        # Visual styling constants
│   └── 🔵 BubbleBudzRenderer.java     # Legacy static rendering methods
│
└── 📁 config/                         # Configuration (CONSTANTS)
    └── 🔵 BubbleBudzConfig.java       # Game tuning parameters
```

**Legend:**
- 🔵 Concrete implementations
- 🟡 Interfaces/Abstract classes

---

## ⚙️ Core Systems

### 1. Event System (`GameEventManager`)
**Purpose**: Decoupled communication between game components

**Key Events:**
- `BubblePoppedEvent` - Fired when bubble is clicked
- `ScoreChangedEvent` - Fired when score updates
- `RoundCompleteEvent` - Fired when round ends

**Usage:**
```java
// Subscribe to events
eventManager.subscribe(BubblePoppedEvent.class, this);

// Publish events
eventManager.publish(new BubblePoppedEvent(x, y, radius, score, color));
```

### 2. Game Mode System (`GameMode`)
**Purpose**: Pluggable gameplay variations

**Current Modes:**
- `ClassicGameMode` - 30-second timed rounds

**Interface:**
```java
public abstract class GameMode {
    public abstract boolean isRoundComplete(long currentTime);
    public abstract int calculateScore(Bubble bubble, int panelHeight);
    public abstract void onRoundComplete();
}
```

### 3. Rendering Pipeline (`RenderManager`)
**Purpose**: Modular, ordered rendering system

**Render Order:**
1. `BackgroundRenderer` (0)
2. `HeaderRenderer` (100)
3. `BubbleRenderer` (200)
4. `EffectRenderer` (300)
5. `FooterRenderer` (400)
6. `LoadingRenderer` (500)

### 4. Effects System (`EffectManager`)
**Purpose**: Dynamic visual effects with factory pattern

**Features:**
- Automatic effect discovery via reflection
- Plugin-style effect types
- Lifecycle management (update/expire/render)

---

## 🎯 Key Classes

### 🔴 CRITICAL CLASSES (Modify these for major changes)

#### `BubbleBudzPanel.java`
**Role**: Main orchestrator and entry point
- **Purpose**: Coordinates all systems, handles Swing integration
- **Key Responsibilities**: System initialization, game loop, event coordination
- **When to modify**: Adding new core systems, changing game flow
- **Size**: ~120 lines (was 200+ before refactor)

#### `BubbleBudzGame.java`
**Role**: Core game logic engine
- **Purpose**: Bubble management, spawning logic, collision detection
- **Key Responsibilities**: Game state, bubble lifecycle, spawn algorithms
- **When to modify**: Changing core game mechanics, bubble behavior
- **Size**: ~80 lines (was 180+ before Bubble extraction)

#### `Bubble.java`
**Role**: Game entity with complete behavior
- **Purpose**: Individual bubble state, physics, and rendering
- **Key Responsibilities**: Animation phases, collision detection, visual effects
- **When to modify**: Changing bubble behavior, adding bubble types
- **Size**: ~90 lines

### 🟡 IMPORTANT CLASSES (Extend these for new features)

#### `GameMode.java` / `ClassicGameMode.java`
**Role**: Gameplay rule definitions
- **Purpose**: Define scoring, timing, and completion rules
- **Extension Point**: Create new modes (`TimeAttackMode`, `SurvivalMode`)
- **When to extend**: Adding new gameplay variations

#### `RenderComponent.java` + implementations
**Role**: Modular rendering system
- **Purpose**: Composable visual components
- **Extension Point**: Add new visual elements (`ParticleRenderer`, `ComboRenderer`)
- **When to extend**: Adding new visual effects or UI elements

#### `InputHandler.java` / `BubbleInputHandler.java`
**Role**: Input abstraction layer
- **Purpose**: Convert user input to game actions
- **Extension Point**: Add new input methods (`KeyboardHandler`, `TouchHandler`)
- **When to extend**: Supporting new input devices

#### `Effect.java` + effect implementations
**Role**: Visual effects system
- **Purpose**: Animated visual feedback
- **Extension Point**: Add new effects (`ExplosionEffect`, `TrailEffect`)
- **When to extend**: Adding new visual feedback

### 🟢 UTILITY CLASSES (Usually don't modify)

#### `BubbleBudzConfig.java`
**Role**: Game configuration constants
- **Purpose**: Centralized tuning parameters
- **When to modify**: Balancing game difficulty, timing adjustments

#### `GameEventManager.java`
**Role**: Event system infrastructure
- **Purpose**: Decoupled component communication
- **When to modify**: Adding new event types (rare)

#### `AssetManager.java`
**Role**: Resource management
- **Purpose**: Image loading and caching
- **When to modify**: Adding new asset types or optimization

---

## 📦 Dependencies

### External Dependencies
```
javax.swing.*           # Swing UI components
java.awt.*             # Graphics and rendering
java.awt.event.*       # Input event handling
java.awt.geom.*        # 2D geometric shapes
java.awt.image.*       # Image processing
java.util.*            # Collections and utilities
java.util.concurrent.* # Thread-safe collections
java.nio.file.*        # File I/O operations
```

### Internal Dependencies
```
com.bestbudz.dock.util.SpriteUtil       # Sprite loading utilities
com.bestbudz.engine.core.Client         # Main application client
```

### Dependency Flow
```
BubbleBudzPanel
    ↓
BubbleBudzGame + GameMode + RenderManager + EffectManager
    ↓
Bubble + InputHandler + RenderComponents + Effects
    ↓
BubbleBudzConfig + AssetManager + GameEventManager
```

---

## 🚀 Adding New Features

### Adding a New Game Mode

**1. Create Mode Class:**
```java
// game/modes/TimeAttackMode.java
public class TimeAttackMode extends GameMode {
    private static final int TIME_ATTACK_DURATION = 60000; // 60 seconds
    
    @Override
    public boolean isRoundComplete(long currentTime) {
        return (currentTime - getRoundStartTime()) >= TIME_ATTACK_DURATION;
    }
    
    @Override
    public int calculateScore(Bubble bubble, int panelHeight) {
        // Double points for time attack mode
        return BubbleBudzScore.calculateScore(bubble.currentRadius, panelHeight) * 2;
    }
    
    @Override
    public void onRoundComplete() {
        // Time attack specific completion logic
    }
}
```

**2. Use in Panel:**
```java
// In BubbleBudzPanel constructor
this.gameMode = new TimeAttackMode(); // Instead of ClassicGameMode
```

### Adding a New Visual Effect

**1. Create Effect:**
```java
// systems/effects/types/explosion/ExplosionEffect.java
public class ExplosionEffect implements Effect {
    // Implementation here
}

// systems/effects/types/explosion/ExplosionFactory.java  
public class ExplosionFactory implements EffectFactory {
    public Effect create(EffectData data) {
        return new ExplosionEffect(data);
    }
}
```

**2. Use Effect:**
```java
// Effect is automatically discovered by EffectLoader
effectManager.addEffect("explosion", effectData);
```

### Adding a New UI Component

**1. Create Renderer:**
```java
// systems/render/components/ComboRenderer.java
public class ComboRenderer implements RenderComponent {
    @Override
    public void render(Graphics2D g2d, RenderContext context) {
        // Draw combo counter
    }
    
    @Override
    public int getRenderOrder() { return 250; } // Between bubbles and effects
}
```

**2. Register Component:**
```java
// In BubbleBudzPanel.setupRenderComponents()
renderManager.addComponent(new ComboRenderer());
```

### Adding Mode-Specific UI

**1. Create Mode UI:**
```java
// systems/render/components/TimeAttackUI.java
public class TimeAttackUI implements RenderComponent {
    @Override
    public void render(Graphics2D g2d, RenderContext context) {
        if (currentGameMode instanceof TimeAttackMode) {
            drawSpeedometer(g2d);
            drawMultiplier(g2d);
        }
    }
}
```

**2. Register in Mode:**
```java
// In TimeAttackMode.initialize()
renderManager.addComponent(new TimeAttackUI());
```

### Adding New Input Support

**1. Create Handler:**
```java
// systems/input/KeyboardInputHandler.java
public class KeyboardInputHandler implements InputHandler {
    @Override
    public boolean handleClick(int x, int y, MouseEvent event) {
        // Keyboard-specific logic
        return false;
    }
}
```

**2. Use Handler:**
```java
// In BubbleBudzPanel
this.inputHandler = new KeyboardInputHandler(...);
```

---

## 🎯 Classes to Focus On

### For Game Logic Changes:
1. **`BubbleBudzGame.java`** - Spawning, collision, bubble management
2. **`Bubble.java`** - Individual bubble behavior and physics
3. **`ClassicGameMode.java`** - Current game rules and scoring

### For New Features:
1. **`GameMode.java`** - Extend for new gameplay modes
2. **`RenderComponent.java`** - Extend for new visual elements
3. **`Effect.java`** - Extend for new visual effects
4. **`InputHandler.java`** - Extend for new input methods

### For UI/Visual Changes:
1. **`BubbleBudzStyle.java`** - Colors, fonts, visual constants
2. **`BubbleBudzRenderer.java`** - Static rendering utilities
3. **Render components** - Specific visual elements

### For Configuration:
1. **`BubbleBudzConfig.java`** - Game timing, physics, constants

---

## ❌ Classes to Ignore

### Infrastructure (Don't modify unless you know what you're doing):
- **`GameEventManager.java`** - Event system infrastructure
- **`GameStateManager.java`** - State management (currently minimal)
- **`RenderManager.java`** - Render coordination
- **`EffectManager.java`** - Effect lifecycle management
- **`EffectLoader.java`** - Dynamic effect discovery
- **`AssetManager.java`** - Resource management

### Data Layer (Rarely need changes):
- **`BubbleBudzLoader.java`** - File loading
- **`BubbleBudzSaving.java`** - File saving
- **`BubbleBudzScoreManager.java`** - High score persistence

### Legacy (Stable, rarely modified):
- **`BubbleBudzScore.java`** - Core scoring algorithm
- **`PopEffect.java` / `PopFactory.java`** - Basic pop effect

---

## 📋 Development Guidelines

### Code Organization
- **One responsibility per class** - Single purpose, clear role
- **Interfaces for extension points** - Easy to add new implementations
- **Events for communication** - Avoid direct coupling between systems
- **Factory pattern for plugins** - Dynamic loading of features

### Naming Conventions
- **Classes**: PascalCase (`BubbleBudzGame`)
- **Interfaces**: PascalCase, descriptive (`RenderComponent`)
- **Methods**: camelCase, verb-based (`updateBubbles`)
- **Constants**: UPPER_SNAKE_CASE (`BUBBLE_LIFESPAN`)

### Performance Considerations
- **Use `CopyOnWriteArrayList`** for collections modified during iteration
- **Minimize object creation** in game loop (use object pooling if needed)
- **Cache expensive calculations** (like distances, graphics objects)
- **Dispose resources** properly (images, graphics contexts)

### Testing Approach
- **Unit test entities** (`Bubble` behavior in isolation)
- **Test game modes** (scoring, completion logic)
- **Test effects** (lifecycle, rendering)
- **Integration test** main game loop

### Extension Patterns
```java
// ✅ Good: Extend abstract classes
public class MyGameMode extends GameMode { ... }

// ✅ Good: Implement interfaces  
public class MyRenderer implements RenderComponent { ... }

// ✅ Good: Use events for communication
eventManager.publish(new MyCustomEvent(...));

// ❌ Avoid: Modifying existing concrete classes
// Don't modify BubbleBudzGame unless changing core mechanics
```

---

## 📝 Examples

### Complete New Game Mode Example

```java
// game/modes/SurvivalMode.java
public class SurvivalMode extends GameMode {
    private int lives = 3;
    private int wave = 1;
    
    @Override
    public void initialize() {
        // Add survival-specific UI
        renderManager.addComponent(new SurvivalUI());
        renderManager.addComponent(new LivesRenderer());
    }
    
    @Override
    public boolean isRoundComplete(long currentTime) {
        return lives <= 0; // Game over when lives exhausted
    }
    
    @Override
    public int calculateScore(Bubble bubble, int panelHeight) {
        // Increase score with wave multiplier
        int baseScore = BubbleBudzScore.calculateScore(bubble.currentRadius, panelHeight);
        return baseScore * wave;
    }
    
    @Override
    public void onRoundComplete() {
        // Handle game over
        if (lives <= 0) {
            gameOver();
        }
    }
    
    public void loseLife() {
        lives--;
        if (lives > 0) {
            // Continue with reduced lives
        }
    }
    
    public void nextWave() {
        wave++;
        // Increase difficulty
    }
}
```

### Complete New Effect Example

```java
// systems/effects/types/shockwave/ShockwaveEffect.java
public class ShockwaveEffect implements Effect {
    private float x, y;
    private float radius = 0;
    private float maxRadius;
    private long startTime;
    private Color color;
    
    public ShockwaveEffect(EffectData data) {
        this.x = data.x;
        this.y = data.y;
        this.maxRadius = data.radius * 3; // Expand to 3x bubble size
        this.color = data.color;
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = elapsed / 800f; // 0.8 second effect
        
        if (progress <= 1.0f) {
            radius = maxRadius * easeOut(progress);
        }
    }
    
    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > 800;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (radius <= 0) return;
        
        float alpha = 1.0f - (radius / maxRadius);
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                              (int)(255 * alpha * 0.6f)));
        g2d.setStroke(new BasicStroke(4f));
        
        float diameter = radius * 2;
        g2d.drawOval((int)(x - radius), (int)(y - radius), 
                     (int)diameter, (int)diameter);
    }
    
    private float easeOut(float t) {
        return 1 - (1 - t) * (1 - t);
    }
}

// systems/effects/types/shockwave/ShockwaveFactory.java
public class ShockwaveFactory implements EffectFactory {
    @Override
    public Effect create(EffectData data) {
        return new ShockwaveEffect(data);
    }
}
```

---

## 🏁 Getting Started

1. **Understand the flow**: `BubbleBudzPanel` → `BubbleBudzGame` → `Bubble`
2. **Follow the events**: See how clicking bubbles creates events that update scores
3. **Explore the rendering**: See how `RenderManager` coordinates visual components
4. **Try small changes**: Modify `BubbleBudzConfig` constants to see immediate effects
5. **Add a simple effect**: Create a new effect type to understand the plugin system
6. **Create a game mode**: Extend `GameMode` to see how gameplay can be customized

The architecture is designed to be **discoverable** - you can understand how things work by following the code from `BubbleBudzPanel` downward through the dependency hierarchy.

**Happy coding! 🫧**