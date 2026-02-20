       █████╗ ██████╗  ██████╗██╗  ██╗██╗      █████╗ ██████╗
      ██╔══██╗██╔══██╗██╔════╝██║  ██║██║     ██╔══██╗██╔══██╗
      ███████║██████╔╝██║     ███████║██║     ███████║██████╔╝
      ██╔══██║██╔══██╗██║     ██╔══██║██║     ██╔══██║██╔══██╗
      ██║  ██║██║  ██║╚██████╗██║  ██║███████╗██║  ██║██████╔╝
      ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═════╝
                    "Architecture Laboratory"

=======================================================================

THE DOCS ARE FOUND IN "root\archlab-ide\docs" , These docs specify not just documentation, but architectural vision and patterns to actively enforce.

THIS DOCUMENTATION IS PROJECT PATTERNS NEEDING TO BE ENFORCED BY YOU (CLAUDE) , ITS NOT SOMETHING TO ANALYZE, ITS SOMETHING TO ENFORCE ACTIVELY.

# Key Rule: [backend_infrastructure_<->_Database_<->_UI]

- Universal UUID-based entity tracking system that serves as the backbone for the entire engine. Every "thing" in the app (elements, API connections, settings, actions, etc.) gets a UUID enabling DB tracking, state visualization, logging correlation, history/replay, and anomaly detection.
- on-demand/virtualized rendering - like games render only what's in the frustum

### File Naming Rules

2 Deep: <parent>/<identifier>-<parent>.<ext>
modals/draggable/
├── header-modal.css ✓ (not "draggable-header-modal.css")
├── overlay-modal.css ✓
└── resize-modal.css ✓

3 Deep: <parent>/<identifier>-<parent>/<identifier>-<scope>-<parent>.<ext>
terminal/chat/
├── header-chat.css ✓ (not "terminal-chat-header.css")
├── footer-chat.css ✓
└── container-chat.css ✓

4 Deep: <parent>/<identifier>-<parent>/<identifier>-<category>-<parent>/<identifier>-<scope>-<parent>.<ext>
terminal/chat/containers/
├── main-container.css ✓ (not "terminal-chat-container.css")
└── entry-container.css ✓

terminal/chat/entries/
├── list-entry.css ✓
├── base-entry.css ✓
└── ai-entry.css ✓

Key Rules:

1. Deepest folder type determines suffix (containers → -container, entries → -entry)
2. Don't repeat parent folder name (folder IS "chat", so no "chat-" prefix)
3. Identifier describes WHAT it is semantically (header, footer, main, list)
4. For swaps: toast-container-toast.css → container-toast.css (remove redundant prefix)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  UNIVERSAL PATTERN: FOLDER TYPE → FILE SUFFIX                           │
│  ─────────────────────────────────────────────────────────────────────  │
│  ANY folder using a plural noun derives its file suffix from the        │
│  singular form. The examples below are illustrative, not exhaustive.    │
│                                                                         │
│  Pattern: /{plural-noun}/ → *-{singular-noun}.ts                        │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE F1: ALL FILES USE KEBAB-CASE                                      │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ file-tree-panel.ts                                                   │
│  ✓ base-component.ts                                                    │
│  ✓ editor-service.ts                                                    │
│  ✗ FileTreePanel.ts                                                     │
│  ✗ baseComponent.ts                                                     │
│  ✗ editor_service.ts                                                    │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE F2: FILES INHERIT SUFFIX FROM PARENT FOLDER                       │
│  ─────────────────────────────────────────────────────────────────────  │
|  PATTERN: /{plural}/ → *-{singular}.ts                                  |
│  Folder name (singular) becomes file suffix.                            │
│                                                                         │
│    {plural}         {singular}              (example)                   │
│  /components/    → *-component.ts    (file-tree-component.ts)           │
│  /services/      → *-service.ts      (editor-service.ts)                │
│  /panels/        → *-panel.ts        (terminal-panel.ts)                │
│  /engines/       → *-engine.ts       (governance-engine.ts)             │
│  /validators/    → *-validator.ts    (line-limit-validator.ts)          │
│  /repositories/  → *-repository.ts   (file-repository.ts)               │
│  /utilities/     → *-util.ts         (string-util.ts)                   │
│  /helpers/       → *-helper.ts       (dom-helper.ts)                    │
│  /clients/       → *-client.ts       (api-client.ts)                    │
│  /handlers/      → *-handler.ts      (error-handler.ts)                 │
│  /factories/     → *-factory.ts      (component-factory.ts)             │
│  /parsers/       → *-parser.ts       (typescript-parser.ts)             │
│  /scanners/      → *-scanner.ts      (file-scanner.ts)                  │
│  /watchers/      → *-watcher.ts      (file-watcher.ts)                  │
│  /commands/      → *-command.ts      (save-file-command.ts)             │
│  /plugins/       → *-plugin.ts       (git-plugin.ts)                    │
│  /pages/         → *-page.ts         (editor-page.ts)                   │
│  /modals/        → *-modal.ts        (settings-modal.ts)                │
│  /widgets/       → *-widget.ts       (clock-widget.ts)                  │
|  ...                                                                    |
|  ⟹ DERIVE: Any /{X}s/ folder → *-{X}.ts                                |
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE F3: BASE CLASSES USE PREFIX, NOT SUFFIX                           │
│  ─────────────────────────────────────────────────────────────────────  │
│  /base/          → base-*.ts         (base-component.ts)                │
│  /core/          → core-*.ts         (core-event-bus.ts)                │
│  /abstract/      → abstract-*.ts     (abstract-panel.ts)                │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE F4: AGGREGATE FILES USE PLURAL NOUN                               │
│  ─────────────────────────────────────────────────────────────────────  │
│  Files containing multiple related exports use plural form.             │
│                                                                         │
│  /constants/     → *-constants.ts    (editor-constants.ts)              │
│  /types/         → *-types.ts        (editor-types.ts)                  │
│  /interfaces/    → *-interfaces.ts   (api-interfaces.ts)                │
│  /events/        → *-events.ts       (file-events.ts)                   │
│  /intents/       → *-intents.ts      (editor-intents.ts)                │
│  /keys/          → *-keys.ts         (shortcut-keys.ts)                 │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE F5: INDEX FILES ARE BARREL EXPORTS ONLY                           │
│  ─────────────────────────────────────────────────────────────────────  │
│  index.ts files ONLY contain export statements.                         │
│  No logic, no classes, no functions - only re-exports.                  │
│                                                                         │
│  ✓ export { FileService } from './file-service';                        │
│  ✓ export * from './types';                                             │
│  ✗ export class FileService { }  // NO - logic in index                 │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### Folder Taxonomy Rules

```
┌─────────────────────────────────────────────────────────────────────────┐
│  RULE T1: FOLDERS USE PLURAL NOUNS                                      │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ /components/                                                         │
│  ✓ /services/                                                           │
│  ✓ /utilities/                                                          │
│  ✗ /component/                                                          │
│  ✗ /service/                                                            │
│  ✗ /utility/                                                            │
│                                                                         │
│  Exception: /base/, /core/, /shared/ (these are categories, not types) │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE T2: MAXIMUM 2 LEVELS OF DOMAIN NESTING                            │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ /services/file-service.ts                                            │
│  ✓ /services/editor/editor-service.ts                                   │
│  ✗ /services/editor/tabs/tab-service.ts  // Too deep                    │
│                                                                         │
│  If you need deeper nesting, create a new top-level domain.             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE T3: DOMAIN SUBFOLDERS INHERIT PARENT SUFFIX                       │
│  ─────────────────────────────────────────────────────────────────────  │
│  /services/editor/       → *-service.ts  (tab-service.ts)               │
│  /components/panels/     → *-panel.ts    (file-tree-panel.ts)           │
│  /validators/typescript/ → *-validator.ts (no-any-validator.ts)         │
│                                                                         │
│  The deepest type-folder determines the suffix.                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE T4: SPECIAL FOLDERS HAVE FIXED STRUCTURES                         │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                         │
│  /styles/                                                               │
│  ├── /tokens/      → *-tokens.css      (color-tokens.css)               │
│  ├── /base/        → *-base.css        (reset-base.css)                 │
│  ├── /shared/      → *-shared.css      (button-shared.css)              │
│  ├── /components/  → *-component.css   (modal-component.css)            │
│  └── /pages/       → *-page.css        (editor-page.css)                │
│                                                                         │
│  /constants/                                                            │
│  ├── /channels/    → *-channels.ts     (ipc-channels.ts)                │
│  ├── /events/      → *-events.ts       (dom-events.ts)                  │
│  ├── /keys/        → *-keys.ts         (keyboard-keys.ts)               │
│  └── /intents/     → *-intents.ts      (file-intents.ts)                │
│                                                                         │
│  /types/                                                                │
│  ├── /models/      → *-model.ts        (file-model.ts)                  │
│  ├── /dtos/        → *-dto.ts          (save-file-dto.ts)               │
│  └── /interfaces/  → *-interface.ts    (parser-interface.ts)            │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### Class/Function Naming Rules

```
┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C1: CLASS NAME = PASCALCASE(FILENAME)                             │
│  ─────────────────────────────────────────────────────────────────────  │
│  file-tree-panel.ts    → class FileTreePanel                            │
│  base-component.ts     → class BaseComponent                            │
│  editor-service.ts     → class EditorService                            │
│  string-util.ts        → class StringUtil                               │
│                                                                         │
│  ONE class per file. File name = class name (kebab vs Pascal).          │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C2: ONE PRIMARY EXPORT PER FILE                                   │
│  ─────────────────────────────────────────────────────────────────────  │
│  Each file has ONE primary export (class, function, or const).          │
│  Supporting types/interfaces may be co-located but not primary.         │
│                                                                         │
│  ✓ file-service.ts exports FileService (primary)                        │
│  ✓ file-service.ts may export FileServiceOptions (supporting type)      │
│  ✗ file-service.ts exports FileService AND DirectoryService             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C3: METHODS USE VERB-FIRST CAMELCASE                              │
│  ─────────────────────────────────────────────────────────────────────  │
│  Public methods: verbNoun()                                             │
│  ✓ getFile(), saveDocument(), parseContent(), validateRule()            │
│  ✗ fileGet(), documentSave(), contentParse()                            │
│                                                                         │
│  Private methods: _verbNoun() or verbNoun() (consistent within class)   │
│  Lifecycle methods: onVerb() (onInit, onMount, onDestroy)               │
│  Event handlers: handleNoun() (handleClick, handleChange)               │
│  Boolean getters: isAdjective() or hasNoun() (isValid, hasChildren)     │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C4: CONSTANTS USE SCREAMING_SNAKE_CASE                            │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ MAX_FILE_SIZE                                                        │
│  ✓ DEFAULT_TIMEOUT_MS                                                   │
│  ✓ FILE_SAVE_REQUESTED                                                  │
│  ✗ maxFileSize                                                          │
│  ✗ DefaultTimeoutMs                                                     │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C5: INTERFACES USE I-PREFIX ONLY FOR CONTRACTS                    │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ interface PanelOptions { }     // Options/config - no prefix         │
│  ✓ interface FileData { }         // Data shape - no prefix             │
│  ✓ interface IDisposable { }      // Contract/capability - I prefix     │
│  ✓ interface ISerializable { }    // Contract/capability - I prefix     │
│                                                                         │
│  I-prefix = "this is a capability contract to implement"                │
│  No prefix = "this is a data shape or configuration"                    │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE C6: TYPE ALIASES USE DESCRIPTIVE SUFFIXES                         │
│  ─────────────────────────────────────────────────────────────────────  │
│  ✓ type FileId = string;                                                │
│  ✓ type SaveResult = { success: boolean };                              │
│  ✓ type PanelPosition = 'left' | 'right' | 'bottom';                    │
│  ✓ type IntentHandler<T> = (intent: Intent<T>) => void;                 │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### File Structure Rules

```
┌─────────────────────────────────────────────────────────────────────────┐
│  RULE S1: MAXIMUM 150 LINES PER FILE                                    │
│  ─────────────────────────────────────────────────────────────────────  │
│  If a file exceeds 150 lines, it must be split.                         │
│  This forces modularization and single responsibility.                  │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE S2: MAXIMUM 6 FILES PER FOLDER                                    │
│  ─────────────────────────────────────────────────────────────────────  │
│  If a folder exceeds 6 files (excluding index.ts), create subfolders.   │
│  This forces organization and prevents flat folder sprawl.              │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE S3: STANDARD FILE SECTION ORDER                                   │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                         │
│  /**                                                                    │
│   * File description (1-3 lines)                                        │
│   */                                                                    │
│                                                                         │
│  // =======================================================================│
│  // Imports                                                             │
│  // =======================================================================│
│                                                                         │
│  // =======================================================================│
│  // Types (local to this file)                                          │
│  // =======================================================================│
│                                                                         │
│  // =======================================================================│
│  // Constants                                                           │
│  // =======================================================================│
│                                                                         │
│  // =======================================================================│
│  // Main Export (class/function)                                        │
│  // =======================================================================│
│                                                                         │
│  // =======================================================================│
│  // Helpers (private to this file)                                      │
│  // =======================================================================│
│                                                                         │
│  Order within class:                                                    │
│  1. Static properties                                                   │
│  2. Instance properties                                                 │
│  3. Constructor                                                         │
│  4. Lifecycle methods (onInit, onMount, etc.)                           │
│  5. Public methods                                                      │
│  6. Protected methods                                                   │
│  7. Private methods                                                     │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  RULE S4: NO CIRCULAR DEPENDENCIES                                      │
│  ─────────────────────────────────────────────────────────────────────  │
│  Dependency flow is strictly hierarchical:                              │
│                                                                         │
│  core/ ← base/ ← services/ ← components/ ← pages/                       │
│                                                                         │
│  Lower layers NEVER import from higher layers.                          │
│  Use events/intents for upward communication.                           │
│  Dependency graph is queryable for architectural verification.          │
└─────────────────────────────────────────────────────────────────────────┘
```

## THE SEVEN LAWS

```
┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 1: FORWARD-ONLY PROGRAMMING (ExtendViaComposablePrimitives)       │
│  ─────────────────────────────────────────────────────────────────────  │
│  Adding new functionality NEVER requires modifying existing files.      │
│  New files self-register by inheriting from base classes.               │
│  Discovery happens at runtime, not compile-time registration.           │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - ExtendViaComposablePrimitives: New features via extension            │
│  - ScaleMonotonically: Add without redesign                            │
│  - ExtendWithoutOverspecifying: Leave room for evolution               │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 2: NO CHILD-TO-PARENT CALLBACKS (ForbidCallbacksAllowNegotiation) │
│  ─────────────────────────────────────────────────────────────────────  │
│  Children emit intentions. Parents observe and decide.                  │
│  Execution may counter-propose (negotiation pattern).                   │
│  Data flows down, intentions flow up via EventBus.                      │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - ForbidCallbacksAllowNegotiation: No upward calls                    │
│  - EmitEventsAllowInterruption: Event-based communication              │
│  - LayerWithLogicalTime: Unidirectional flow                           │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 3: INHERITANCE OVER CONFIGURATION (DeriveResponsibilityFromInvs)  │
│  ─────────────────────────────────────────────────────────────────────  │
│  Behavior comes from base class extension, not config objects.          │
│  Base classes provide capabilities; children declare usage.             │
│  Modification at base level propagates to all inheritors.               │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - DeriveResponsibilityFromInvariants: Behavior from constraints       │
│  - InvertToInterchangeableAbstractions: Depend on abstractions         │
│  - EnsureSubstitutabilityAndConfluence: Replaceable components         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 4: SECURITY AS BASE-INHERITED CAPABILITY 🔒                        │
│  ─────────────────────────────────────────────────────────────────────  │
│  All input sanitization, validation, and resource constraints are       │
│  inherited from base classes. Components cannot bypass security.        │
│  Trust boundaries enforced architecturally, not by developer discipline.│
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - EncapsulateButShipInspector: Hide internals, audit paths visible    │
│  - FailImmediatelyHaltOnInconsistency: Security violations halt        │
│  - IsolateModulesProtectState: Sandboxed units                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 5: AUTHENTICATED STATE RECOVERY 🎮🔐                               │
│  ─────────────────────────────────────────────────────────────────────  │
│  IDE requires passkey authentication before accessing ANY state.        │
│  Full viewport checkpoints (like game saves) + individual persistence.  │
│  Atomic rollback to any checkpoint. No state access without auth.       │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - EnsureRepeatableWithQuickRecovery: Crash-safe recovery              │
│  - SnapshotMeaningBeforeMutation: Pre-action state capture             │
│  - SeparateConcernsRestoreBySnapshot: Atomic restoration               │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 6: HUMAN-CONTROLLED AUTHORITY BOUNDARY 👤⚖️                        │
│  ─────────────────────────────────────────────────────────────────────  │
│  Humans hold ultimate authority via passkey authentication.             │
│  AI is an untrusted subprocess with ZERO authority.                     │
│  Overrides require human justification (min 10 chars).                  │
│  Authority tiers: KERNEL > USER > SYSTEM > AI (none)                   │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - IncludeHumanInComputation: Human is part of execution               │
│  - AttachJustificationToValues: Decisions carry why                    │
│  - DeclareIntentExplainAtRuntime: System explains itself               │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  LAW 7: SINGLE IMPORT BOUNDARY (ANTENNA HUB) 📡 🆕 NEW v2.4             │
│  ─────────────────────────────────────────────────────────────────────  │
│  All module communication flows through a centralized registry.         │
│  Only ONE import is allowed: the registry itself.                       │
│  Modules self-register; consumers resolve by token.                     │
│  No direct imports between feature modules.                             │
│                                                                         │
│  TOPOLOGY:                                                              │
│       ┌─────────┐                                                       │
│       │ Module A│──register──┐                                          │
│       └─────────┘            │                                          │
│       ┌─────────┐            ▼                                          │
│       │ Module B│──register──┬──────────┐                               │
│       └─────────┘            │ REGISTRY │                               │
│       ┌─────────┐            │  (Hub)   │                               │
│       │ Module C│──resolve───┴──────────┘                               │
│       └─────────┘            ▲                                          │
│       ┌─────────┐            │                                          │
│       │ Module D│──resolve───┘                                          │
│       └─────────┘                                                       │
│                                                                         │
│  BENEFITS:                                                              │
│  - Single point for capability checking                                 │
│  - Complete dependency graph at startup                                 │
│  - Hot-reloadable without import chain breakage                        │
│  - AI can understand entire system from registry scan                  │
│  - Forward-only: Register new, never modify existing                   │
│  - No callbacks: Resolve via registry, emit via EventBus               │
│  - Security: Registry enforces capabilities                             │
│  - Checkpoints: Registry state is checkpointable                        │
│  - Discovery: Registry IS discovery                                     │
│  - Observability: Registry state queryable as first-class data          │
│                                                                         │
│  DEV-RULES MAPPING:                                                     │
│  - CentralizeAsVersionedSymbols: Registry IS the symbol table          │
│  - SegregateByMeaning: Tokens are semantic addresses                   │
│  - MinimizeDependenciesDiagnoseSymbolically: Single dep point          │
│  - ExtendViaComposablePrimitives: register() + resolve() only          │
│                                                                         │
│  VIOLATION DETECTION:                                                   │
│  - AST query: import statements not from registry                       │
│  - Startup: unresolved tokens                                          │
│  - Runtime: resolution without registration                            │
└─────────────────────────────────────────────────────────────────────────┘
```

## AVOID → ENFORCE Mapping

| AVOID                       | Reason                 | ENFORCE                  | Reason                  |
| --------------------------- | ---------------------- | ------------------------ | ----------------------- |
| shortcuts                   | debt                   | constraints              | leverage                |
| backward_compatibility      | debt                   | forward_compatibility    | compounding             |
| fallback                    | debt                   | fail-fast                | clarity                 |
| deprecation                 | debt                   | explicit-removal         | coherence               |
| legacy                      | debt                   | greenfield               | clarity                 |
| dual-path                   | confusion              | single-path              | determinism             |
| deffering                   | forgetting             | immediacy                | continuity              |
| optional                    | user-orientation       | mandatory                | system-orientation      |
| for_now                     | deffering → forgetting | now                      | immediacy → continuity  |
| unobserved-execution        | missed-learning        | observed-execution       | learning                |
| pattern-without-compression | inefficiency           | pattern-with-compression | efficiency              |
| evolution-without-approval  | architectural-drift    | evolution-with-approval  | architectural-integrity |
| feedback-ignored            | stagnation             | feedback-enforced        | adaptation              |
| shared-ownership            | ambiguity              | single-owner             | accountability          |
| unbounded-lifetime          | leaks                  | bounded-lifetime         | deterministic-release   |
| asymmetric-lifecycle        | resource-leaks         | enforced-symmetry        | guaranteed-cleanup      |
| implicit-retention          | hidden-leaks           | explicit-retention       | observable-ownership    |
| discipline-release          | human-error            | structural-release       | automatic-enforcement   |
| mutable-state               | unpredictability       | immutable-data           | reproducibility         |
| silent-errors               | unknown-failure        | errors-as-language       | machine-processable     |
| hidden-invalidity           | false-consistency      | explicit-invalidity      | honest-uncertainty      |
| parent-callbacks            | tight-coupling         | event-emission           | decoupling              |
| retraction                  | complexity             | monotonic-growth         | append-only-scalability |
| location-addressing         | brittleness            | semantic-addressing      | meaning-based-reference |
| timestamp-ordering          | wall-clock-dependency  | ordinal-time             | logical-sequencing      |
| separation                  | duplication            | homoiconicity            | code-data-uniformity    |
| unlimited-complexity        | cognitive-overload     | bounded-complexity       | human-comprehension     |
| metric-health               | symptom-tracking       | computed-health          | symbolic-diagnosis      |
