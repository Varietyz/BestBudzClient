# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BestBudzClient is a **RuneScape Private Server (RSPS) client** built on a deobfuscated RS2/317 engine. It is a Java Swing desktop application with an OpenGL GPU rendering pipeline (LWJGL 3), a custom side-docked UI system, and a custom game engine loop. The client connects to a remote game server and renders a 3D tile-based game world.

## Build & Run

```bash
# Build the project (fat JAR)
./gradlew shadowJar

# Run the client
./gradlew run

# Build Windows .exe (depends on shadowJar)
./gradlew createExe
```

- **Java 11** toolchain (set in build.gradle)
- JVM args: `-Xms512m -Xmx4096m -XX:+UseG1GC`
- Entry point: `com.bestbudz.engine.ClientLauncher`
- No test suite exists in this project
- Local JAR dependencies live in `src/libs/`

## Architecture

### Startup Pipeline

`ClientLauncher.main()` runs a phased initialization:
1. Show loading screen (FlatDarkLaf theme)
2. Initialize core systems (Signlink, SettingHandler, ItemBonusManager)
3. Create JFrame + GameCanvas, initialize input handlers
4. Start `GameEngine` on a dedicated thread ("GameLoop")
5. On load complete → show main window, attach `UIDockFrame` as side panel

### Engine Core (`com.bestbudz.engine`)

- **`Client`** — the massive central class (extends `ClientEngine`). Holds all game state: sprites, interfaces, entities, map data, network stream. Most game logic lives here or in classes that operate on its static fields.
- **`ClientEngine`** — base class handling AWT focus/window events
- **`GameEngine`** — the game loop (`Runnable`). Runs logic ticks at `LOGIC_TICK_MS` (18ms) and renders at `TARGET_FPS` (90). Handles canvas resize and buffer strategy.
- **`GameState`** (extends `Client`) — scene rendering, GPU state management, and `resetGameState()` which handles map region loading with embedded map cache support
- **`GameCanvas`** — the AWT Canvas for rendering
- **`EngineConfig`** — central constants: `ENABLE_GPU`, `TARGET_FPS`, `BUFFERS`, `VIEW_DISTANCE`, `REGION_RENDER`, etc.
- **`NetworkConfig`** — server IPs and ports. `LOCALHOST` flag for dev.

### Inheritance Chain

`ClientEngine` → `Client` → `GameState` → `PacketParser`. The `Client` class is the god object — most subsystems reference its static fields directly.

### GPU Rendering (`com.bestbudz.engine.gpu`)

GPU rendering uses LWJGL 3 / OpenGL and is **disabled by default** (`EngineConfig.ENABLE_GPU = false`). Falls back to CPU software rasterizer. Key classes: `GPURenderingEngine`, `GPUContextManager` (context token acquire/release pattern), `GPUMonitor`, `RS317GPUInterface`.

### Dock UI System (`com.bestbudz.dock`)

A **companion JDialog** (`UIDockFrame`) anchored to the right of the main game window. Uses a split-pane layout with top/bottom card stacks and toggle bars.

**Adding a new dock panel:**
1. Create a package under `com.bestbudz.dock.ui.panel.yourpanel/`
2. Implement the `UIPanel` interface (methods: `getPanelID()`, `getComponent()`, `updateText()`, `onActivate()`, `onDeactivate()`, `updateDockText()`)
3. Register in `RegisteredPanels.PANEL_SUPPLIERS` list
4. Add to `RegisteredPanels.DefaultLayout.TOP_PANELS` or `BOTTOM_PANELS`
5. See `CreationGuide` in `dock/ui/panel/example/` for the full template pattern

**Panel convention:** Each panel package typically has `*Panel.java`, `*Config.java`, `*Style.java`, `*Utils.java`.

**Key dock utilities:**
- `ButtonHandler.sendClick(interfaceId)` — sends interface click packet (opcode 185) to the server. The ID is offset by -85560.
- `DockBlocker` — registers interface IDs that should be rendered in the dock instead of the in-game interface system
- `InteractiveButtonUtil` — creates buttons with rainbow hover effects
- `DockSync` — synchronizes dock frame height with game window

### Network (`com.bestbudz.network`)

- `Stream` — binary packet buffer (read/write primitives, encrypted opcodes via ISAAC)
- `PacketParser` (extends `Client`) — handles all inbound server packets
- `PacketSender` — outbound packet construction
- `CacheManager` / `OnDemandFetcher` — cache file streaming from server
- Server port: 42000

### Rendering Pipeline (`com.bestbudz.rendering`, `com.bestbudz.graphics`, `com.bestbudz.engine.core.gamerender`)

- `Rasterizer` — software 3D rasterizer (triangles, texture mapping)
- `DrawingArea` — 2D pixel buffer operations
- `WorldController` — manages the 3D scene graph (tiles, objects, entities)
- `ObjectManager` — loads map regions and objects from cache data
- `Model` — 3D model with vertex/face data, animation support
- `ImageProducer` — bridges pixel arrays to AWT for display
- `Camera` — camera position, rotation, and cutscene control

### Entity System (`com.bestbudz.entity`)

- `Entity` — base entity with position, animation, chat
- `Stoner` — player entity (RSPS-themed rename of "Player")
- `Npc` — NPC entity
- `EntityDef` — NPC definitions loaded from cache
- Pet variant system in `entity/pets/` — `PetVariantManager` maps variant IDs (starting at 10000) to modified `EntityDef`s

### World (`com.bestbudz.world`)

- `ObjectDef` — game object definitions from cache
- `CollisionMap` (`Class11`) — pathfinding collision data
- `VarBit` / `Varp` — game variable/config system
- `Ground` / `GroundItem` — tile and ground item data
- Map coordinate system: tiles are 64x64 per region, camera positions use `>> 7` for tile conversion

### Game Data (`com.bestbudz.data`)

- `ItemDef` — item definitions (loaded from cache with bonus data from XML)
- `Skills` — skill IDs and level data
- `AccountData` / `AccountManager` — local account persistence

### UI / Interfaces (`com.bestbudz.ui`)

- `RSInterface` — the RS interface widget system (interfaceCache array)
- `BuildInterface` — constructs custom interfaces programmatically
- `InterfaceManagement` — manages open/close interface state
- `ActionHandler` — processes button/interface click actions

## Key Conventions

- **Deobfuscation is ongoing** — many classes still have original obfuscated names (`Class11`, `Class18`, `Class29`, etc.) and fields (`anInt1097`, `aByteArrayArray1183`). Newer code uses descriptive names. When modifying old code, prefer renaming to descriptive names.
- **"Stoner"** = Player entity throughout the codebase (thematic rename)
- **Static field access** — most game state is accessed through `Client.*` static fields. This is inherited from the RS2 architecture.
- **Interface IDs** — dock panels use assigned ranges to avoid conflicts (e.g., 29000-29099 for template, 30000+ for others). Server-side interfaces use IDs in the RSInterface cache.
- **Resources** are in `src/main/resources/`: game cache files in `caches/bestbudz/`, sprites in `sprites/`, loading screen assets in `loading/`.
- **`.bak` files** (e.g., `DrawingAreaCPU.bak`, `ImageProducerCPU.bak`) are preserved CPU-only fallback implementations.
