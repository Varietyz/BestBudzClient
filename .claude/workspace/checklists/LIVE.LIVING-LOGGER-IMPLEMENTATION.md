# Living Logger Implementation Checklist

**Generated**: 2026-01-04 | **Architecture**: ARCHLAB v4.2
**Phases**: 8 | **Algorithms**: extension-without-modification, registry-resolution, verification-gate

---

## ⚠ Refuted Claims

None - All claims from documentation verified against ARCHLAB principles.

---

# SPRINT: CRITICAL

## PHASE 1: CREATE<Schema Log Types>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Create the foundational TypeScript type definitions for the schema-based logging system. This phase establishes the schema-based API pattern inspired by DOM.createElement(), with reserved keys for entity, intent, pool, graph, IPC, and mode enrichment. All types are optional extensions to existing DebugEntry, ensuring backward compatibility (LAW 1).

**Dependencies (3D Graph)**:

- **Sequential (Z)**: _entry phase_
- **Lateral (X)**: _none_
- **Diagonal (Y)**: _none_

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:types` | dependents |
| pools | _none_ | — |
| entities | _none_ | — |
| database | _none_ | — |
| dashboard | `schema_types_loaded` | Engine.logger.metric() |

### Task 1.1: Create log-schema-types.ts

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\log-schema-types.ts`

- [x] 1.1.1. CREATE interface SchemaLogEntry extending DebugEntry
- [x] 1.1.2. ADD optional entity context fields (uuid, entityType, entitySubtype, entityParentUuid)
- [x] 1.1.3. ADD optional intent flow fields (intentType, intentPhase, intentOriginUuid, intentTargetUuid)
- [x] 1.1.4. ADD optional pool tracking fields (poolName, poolAction, poolSize, poolAvailable)
- [x] 1.1.5. ADD optional graph analysis fields (graphAffectedNodes, graphRelationshipType)
- [x] 1.1.6. ADD optional IPC tracking fields (ipcChannel, ipcDirection)
- [x] 1.1.7. ADD optional output mode fields (outputMode, aiStructured)
- [x] 1.1.8. ADD optional priority/retention fields (priority, retentionDays, webhookUrl, expiresAt, archived)

### Task 1.2: Create LogEntryOptions interface

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\log-schema-types.ts`

- [x] 1.2.1. CREATE interface LogEntryOptions with required fields (level, source, message)
- [x] 1.2.2. ADD optional entity context object {uuid, type, subtype, parentUuid}
- [x] 1.2.3. ADD optional intent context object {type, phase, originUuid, targetUuid}
- [x] 1.2.4. ADD optional pool context object {name, action, size, available}
- [x] 1.2.5. ADD optional graph context object {affectedNodes, relationshipType}
- [x] 1.2.6. ADD optional ipc context object {channel, direction}
- [x] 1.2.7. ADD optional mode object {console, db, silent, ai}
- [x] 1.2.8. ADD optional priority, retentionDays, webhookUrl, tags fields

### Task 1.3: Export supporting types

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\log-schema-types.ts`

- [x] 1.3.1. EXPORT IntentTrace interface {intentType, originUuid, targetUuid, propagationPath, durationMs, success, relatedLogs}
- [x] 1.3.2. EXPORT PoolAnalysis interface {poolName, timeRange, snapshots, metrics}
- [x] 1.3.3. EXPORT EntityLifecycle interface {uuid, entityType, createdAt, destroyedAt, lifetimeMs, relatedLogs}
- [x] 1.3.4. EXPORT EntityGraph interface {root, nodes, edges}
- [x] 1.3.5. EXPORT QueryOptions interface for log retrieval
- [x] 1.3.6. EXPORT LogQueryResult interface for query responses

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

## PHASE 2: CREATE<Database Migration Script>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Create the database migration script to extend debug.db with 23 new columns in debug_entries and 4 new tables (intent_flow_graph, pool_snapshots, entity_lifecycle, ai_query_cache). Migration is additive and backward-compatible - existing data preserved, new columns accept NULL values. This enables storage of rich context without breaking existing code.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 →
- **Lateral (X)**: _none_
- **Diagonal (Y)**: Phase 1 (types)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | _none_ | — |
| pools | _none_ | — |
| entities | _none_ | — |
| database | `debug_entries`, `intent_flow_graph`, `pool_snapshots`, `entity_lifecycle`, `ai_query_cache` | migrations → integrity |
| dashboard | `migration_001_executed` | Engine.logger.metric() |

### Task 2.1: Create migration file

**File**: `D:\GIT\archlab\root\archlab-ide\src\main\debug\migrations\001-living-logger.ts`

- [x] 2.1.1. CREATE migration function migrateLivingLogger(db: Database)
- [x] 2.1.2. ADD 23 new columns to debug_entries via ALTER TABLE statements
- [x] 2.1.3. CREATE intent_flow_graph table with indexes
- [x] 2.1.4. CREATE pool_snapshots table with indexes
- [x] 2.1.5. CREATE entity_lifecycle table with indexes
- [x] 2.1.6. CREATE ai_query_cache table with indexes
- [x] 2.1.7. ADD migration version tracking to schema_version table
- [x] 2.1.8. WRAP all operations in transaction for atomicity

### Task 2.2: Update debug-db.ts to run migration

**File**: `D:\GIT\archlab\root\archlab-ide\src\main\debug\debug-db.ts`

- [x] 2.2.1. IMPORT migrateLivingLogger from migrations
- [x] 2.2.2. ADD migration check in initializeDebugDb()
- [x] 2.2.3. RUN migration if version < 001
- [x] 2.2.4. UPDATE schema_version after successful migration
- [x] 2.2.5. LOG migration execution via IPC to renderer

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

## PHASE 3: WRITE<Schema-Based Log Method>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1, 2, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Implement the primary logSchema() method in DebugHub following the DOM.createElement() pattern. This method accepts a LogEntryOptions object and enriches the log entry with entity, intent, pool, graph, and IPC context. It infers category from context, resolves output mode, and optionally formats PAG-structured output for AI consumption. Existing convenience methods (info, warn, error) remain unchanged for backward compatibility.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 → Phase 2 →
- **Lateral (X)**: _none_
- **Diagonal (Y)**: Phase 1 (types), Phase 4 (PAG formatter)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:hub` | dependents |
| pools | _none_ | — |
| entities | _none_ | — |
| database | _none_ | — |
| dashboard | `logSchema_method_available` | Engine.logger.metric() |

### Task 3.1: Add logSchema method to DebugHub

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\debug-hub.ts`

- [x] 3.1.1. IMPORT LogEntryOptions, SchemaLogEntry from log-schema-types
- [x] 3.1.2. CREATE logSchema(options: LogEntryOptions): string method
- [x] 3.1.3. GENERATE entry.id using existing generateDebugId()
- [x] 3.1.4. SET core fields (timestamp, severity, category, source, message)
- [x] 3.1.5. POPULATE entity fields from options.entity if present
- [x] 3.1.6. POPULATE intent fields from options.intent if present
- [x] 3.1.7. POPULATE pool fields from options.pool if present
- [x] 3.1.8. POPULATE graph fields from options.graph if present
- [x] 3.1.9. POPULATE ipc fields from options.ipc if present
- [x] 3.1.10. SET priority, retentionDays, expiresAt with defaults
- [x] 3.1.11. CALL inferCategory() to determine category from context
- [x] 3.1.12. CALL resolveOutputMode() to handle mode options
- [x] 3.1.13. INVOKE formatPAG() if options.mode?.ai === true
- [x] 3.1.14. CALL addEntry(entry) to persist

### Task 3.2: Add helper methods

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\debug-hub.ts`

- [x] 3.2.1. CREATE inferCategory(options): DebugCategory method
- [x] 3.2.2. RETURN 'intent' if options.intent exists
- [x] 3.2.3. RETURN 'pool' if options.pool exists
- [x] 3.2.4. RETURN 'entity' if options.entity exists
- [x] 3.2.5. RETURN 'violation' if level === 'violation'
- [x] 3.2.6. DEFAULT to 'log'
- [x] 3.2.7. CREATE resolveOutputMode(mode?): string method
- [x] 3.2.8. HANDLE silent, ai, db, console combinations per spec

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

# SPRINT: HIGH

## PHASE 4: CREATE<PAG Formatter>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Implement the PAG formatter that converts SchemaLogEntry objects into structured PAG-formatted strings for AI consumption. Uses PAG keywords (DECLARE, SET, IF, ANALYZE, VALIDATE, REPORT) to create machine-readable output. Handles entity, intent, pool, and graph context blocks conditionally. Output stored in debug_entries.ai_structured column for query API retrieval.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 →
- **Lateral (X)**: Phase 3 ↔
- **Diagonal (Y)**: Phase 1 (types)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:formatter:pag` | dependents |
| pools | _none_ | — |
| entities | _none_ | — |
| database | _none_ | — |
| dashboard | `pag_formatter_registered` | Engine.logger.metric() |

### Task 4.1: Create PAG formatter

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\formatters\pag-formatter.ts`

- [x] 4.1.1. CREATE class PAGFormatter
- [x] 4.1.2. IMPLEMENT format(entry: SchemaLogEntry): string method
- [x] 4.1.3. BUILD DECLARE log_entry: object section
- [x] 4.1.4. SET core fields (id, timestamp, severity, category, source, message)
- [x] 4.1.5. ADD conditional IF entity_context EXISTS block
- [x] 4.1.6. ADD conditional IF intent_context EXISTS block
- [x] 4.1.7. ADD conditional IF pool_context EXISTS block
- [x] 4.1.8. ADD conditional IF graph_context EXISTS block
- [x] 4.1.9. APPEND ANALYZE, VALIDATE, REPORT footer
- [x] 4.1.10. CREATE private escape(str: string): string helper for JSON escaping

### Task 4.2: Integrate PAG formatter with DebugHub

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\debug-hub.ts`

- [x] 4.2.1. IMPORT PAGFormatter from formatters/pag-formatter
- [x] 4.2.2. CREATE private formatter instance in DebugHub
- [x] 4.2.3. CALL formatter.format(entry) in logSchema() when mode.ai === true
- [x] 4.2.4. STORE result in entry.aiStructured field

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

## PHASE 5: CREATE<Intent Flow Tracker>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: intention-emission | **Laws**: [2, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Build the IntentFlowTracker to monitor intent propagation through the system, validating LAW 2 (no parent callbacks). Tracks active flows with start/end times, propagation paths, and related log IDs. Persists completed flows to intent_flow_graph table. Enables traceIntent() queries showing complete intent lifecycle from emission to completion.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 → Phase 2 →
- **Lateral (X)**: _none_
- **Diagonal (Y)**: Phase 1 (types), Phase 2 (database)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:tracker:intent-flow` | dependents |
| pools | _none_ | — |
| entities | _none_ | — |
| database | `intent_flow_graph` | migrations → integrity |
| dashboard | `intent_tracker_started` | Engine.logger.metric() |

### Task 5.1: Create intent flow tracker

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\trackers\intent-flow-tracker.ts`

- [x] 5.1.1. CREATE class IntentFlowTracker
- [x] 5.1.2. DECLARE private activeFlows: Map<string, IntentFlow>
- [x] 5.1.3. IMPLEMENT startFlow(intentType, originUuid, targetUuid?): string
- [x] 5.1.4. GENERATE flowId using timestamp + random
- [x] 5.1.5. CREATE flow object with metadata
- [x] 5.1.6. STORE in activeFlows map
- [x] 5.1.7. IMPLEMENT addPropagation(flowId, uuid): void
- [x] 5.1.8. APPEND uuid to flow.propagationPath
- [x] 5.1.9. IMPLEMENT endFlow(flowId, success, error?): IntentTrace
- [x] 5.1.10. CALCULATE durationMs from start time
- [x] 5.1.11. CALL persistFlow() to store in database
- [x] 5.1.12. REMOVE from activeFlows
- [x] 5.1.13. RETURN IntentTrace result
- [x] 5.1.14. CREATE private persistFlow() method for DB insert

### Task 5.2: Register tracker with Engine

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\index.ts`

- [x] 5.2.1. IMPORT IntentFlowTracker
- [x] 5.2.2. INSTANTIATE tracker in Engine init
- [x] 5.2.3. REGISTER via Engine.registry with token 'engine:debug:tracker:intent-flow'
- [x] 5.2.4. EXPOSE via Engine.intentTracker

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

## PHASE 6: CREATE<Pool Snapshot Manager>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Implement PoolSnapshotManager for periodic pool state capture and leak detection. Discovers pools via registry, captures snapshots every 5 seconds, detects entities not released for >60 seconds, calculates peak/average usage metrics. Persists to pool_snapshots table. Enables analyzePool() queries showing leak suspects and usage patterns.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 → Phase 2 →
- **Lateral (X)**: _none_
- **Diagonal (Y)**: Phase 1 (types), Phase 2 (database)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:tracker:pool-snapshot` | dependents |
| pools | `div-pool`, `span-pool` (all discovered) | metrics → lifecycle → UIStore |
| entities | _none_ | — |
| database | `pool_snapshots` | migrations → integrity |
| dashboard | `pool_tracker_started` | Engine.logger.metric() |

### Task 6.1: Create pool snapshot manager

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\trackers\pool-snapshot-manager.ts`

- [x] 6.1.1. CREATE class PoolSnapshotManager
- [x] 6.1.2. DECLARE private snapshotInterval: number = 5000
- [x] 6.1.3. DECLARE private intervalId?: number
- [x] 6.1.4. IMPLEMENT start(): void method
- [x] 6.1.5. SET intervalId = window.setInterval(captureSnapshot, 5000)
- [x] 6.1.6. IMPLEMENT stop(): void method
- [x] 6.1.7. CLEAR intervalId
- [x] 6.1.8. IMPLEMENT captureSnapshot(): void method
- [x] 6.1.9. DISCOVER pools via Engine.registry.getTokens() filter 'engine:pool:'
- [x] 6.1.10. FOR EACH pool resolve and extract metrics
- [x] 6.1.11. CREATE PoolSnapshot object with current state
- [x] 6.1.12. CALL detectLeaks() to identify suspects
- [x] 6.1.13. PERSIST via persistSnapshot()
- [x] 6.1.14. IMPLEMENT private detectLeaks(pool): string[] heuristic
- [x] 6.1.15. FILTER unreleased entities where acquireTime > 60s ago

### Task 6.2: Register and start tracker

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\index.ts`

- [x] 6.2.1. IMPORT PoolSnapshotManager
- [x] 6.2.2. INSTANTIATE manager in Engine init
- [x] 6.2.3. REGISTER via Engine.registry with token 'engine:debug:tracker:pool-snapshot'
- [x] 6.2.4. CALL manager.start() after Engine initialization
- [x] 6.2.5. EXPOSE via Engine.poolSnapshotManager

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

## PHASE 7: CREATE<Entity Lifecycle Tracker> + WRITE<DOM Factory Hooks>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification | **Laws**: [1, 3, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Create EntityLifecycleTracker and integrate with DOM Factory to track entity creation/destruction. Hooks into createElement() and releaseElement() to log lifecycle events. Stores creation timestamp, frame number, parent UUID, pool name. On destruction, calculates lifetime and stores reason. Enables getEntityLifecycle() queries showing complete entity history.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 → Phase 2 →
- **Lateral (X)**: _none_
- **Diagonal (Y)**: Phase 1 (types), Phase 2 (database)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:tracker:entity-lifecycle` | dependents |
| pools | `div-pool`, `span-pool` (via DOM Factory) | metrics → lifecycle → UIStore |
| entities | all DOM entities with UUID | UIStore → dashboard → state |
| database | `entity_lifecycle` | migrations → integrity |
| dashboard | `entity_tracker_started` | Engine.logger.metric() |

### Task 7.1: Create entity lifecycle tracker

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\trackers\entity-lifecycle-tracker.ts`

- [x] 7.1.1. CREATE class EntityLifecycleTracker
- [x] 7.1.2. IMPLEMENT trackEntityCreated(uuid, data): void method
- [x] 7.1.3. CREATE EntityLifecycleEntry object
- [x] 7.1.4. SET createdAt = Date.now(), createdFrame from data
- [x] 7.1.5. POPULATE entityType, entitySubtype, parentUuid, poolName from data
- [x] 7.1.6. CALL persistLifecycleEntry()
- [x] 7.1.7. IMPLEMENT trackEntityDestroyed(uuid, data): void method
- [x] 7.1.8. FETCH existing entry via getLifecycleEntry(uuid)
- [x] 7.1.9. UPDATE destroyedAt, destroyedFrame, destructionReason
- [x] 7.1.10. CALCULATE lifetimeMs = destroyedAt - createdAt
- [x] 7.1.11. CALL updateLifecycleEntry()
- [x] 7.1.12. CREATE private DB persistence methods

### Task 7.2: Add lifecycle hooks to DOM Factory

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\utils\dom-factory.ts`

- [x] 7.2.1. LOCATE createElement() function
- [x] 7.2.2. AFTER UUID generation, CALL Engine.debug?.trackEntityCreated()
- [x] 7.2.3. PASS uuid, type=tag, subtype=options.entity, parentUuid, poolName
- [x] 7.2.4. LOCATE releaseElement() function
- [x] 7.2.5. EXTRACT uuid from element via getElementUuid()
- [x] 7.2.6. BEFORE element removal, CALL Engine.debug?.trackEntityDestroyed()
- [x] 7.2.7. PASS uuid, reason='normal', frame

### Task 7.3: Register tracker

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\index.ts`

- [x] 7.3.1. IMPORT EntityLifecycleTracker
- [x] 7.3.2. INSTANTIATE tracker in Engine init
- [x] 7.3.3. REGISTER via Engine.registry with token 'engine:debug:tracker:entity-lifecycle'
- [x] 7.3.4. EXPOSE via Engine.entityLifecycleTracker

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

# SPRINT: MEDIUM

## PHASE 8: CREATE<Query API> + CREATE<Webhook Sink> + CREATE<Retention Engine>

**Loop Class**: Construction — _build artifact from specification_
**Algorithm**: extension-without-modification, registry-resolution | **Laws**: [1, 7]
**Validation**: `Bash 'npm run build' → vite validation (BLOCKING)`

**Narrative**:

> Implement the query API with PAG output support, webhook sink for critical events, and retention policy engine for auto-cleanup. Query engine supports rich filtering (entity, intent, pool), multiple output formats (JSON, PAG, console), and caching. Webhook sink sends high-priority logs to external systems. Retention engine runs hourly cleanup based on priority and retention days.

**Dependencies (3D Graph)**:

- **Sequential (Z)**: Phase 1 → Phase 2 → Phase 4 →
- **Lateral (X)**: Phase 3 ↔ Phase 5 ↔ Phase 6 ↔ Phase 7 ↔
- **Diagonal (Y)**: Phase 1 (types), Phase 2 (database), Phase 4 (PAG formatter)

**Ripple Chain**:
| Dimension | Impact | Downstream |
|-----------|--------|------------|
| registry | `engine:debug:query`, `engine:logger-sink:webhook`, `engine:debug:retention` | dependents |
| pools | _none_ | — |
| entities | _none_ | — |
| database | `ai_query_cache` | migrations → integrity |
| dashboard | `query_api_available`, `webhook_sink_registered`, `retention_engine_started` | Engine.logger.metric() |

### Task 8.1: Create query engine

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\query\log-query-engine.ts`

- [x] 8.1.1. CREATE class LogQueryEngine
- [x] 8.1.2. IMPLEMENT query(options: QueryOptions): LogQueryResult method
- [x] 8.1.3. CHECK cache via getCacheKey() and checkCache()
- [x] 8.1.4. BUILD SQL query via buildSqlQuery(options)
- [x] 8.1.5. EXECUTE query and retrieve entries
- [x] 8.1.6. IF format === 'pag' CALL formatPagResult()
- [x] 8.1.7. CACHE result for future queries
- [x] 8.1.8. RETURN LogQueryResult with metadata
- [x] 8.1.9. IMPLEMENT private formatPagResult() using PAG keywords
- [x] 8.1.10. IMPLEMENT private buildSqlQuery() with filter support

### Task 8.2: Create webhook sink

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\sinks\webhook-sink.ts`

- [x] 8.2.1. CREATE class WebhookSink implements LoggerSink
- [x] 8.2.2. SET readonly token = 'engine:logger-sink:webhook'
- [x] 8.2.3. DECLARE private queue: LogEntry[]
- [x] 8.2.4. IMPLEMENT write(entry: LogEntry): void method
- [x] 8.2.5. FILTER entries where priority >= 75 OR severity === 'violation'
- [x] 8.2.6. PUSH to queue if entry.webhookUrl exists
- [x] 8.2.7. CALL processQueue()
- [x] 8.2.8. IMPLEMENT private async processQueue()
- [x] 8.2.9. LOOP through queue and sendWebhook() for each
- [x] 8.2.10. IMPLEMENT private async sendWebhook() using fetch()
- [x] 8.2.11. IMPLEMENT getMetadata() returning registry metadata
- [x] 8.2.12. IMPLEMENT register() calling Engine.registry.register()

### Task 8.3: Create retention engine

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\retention\retention-policy-engine.ts`

- [x] 8.3.1. CREATE class RetentionPolicyEngine
- [x] 8.3.2. DECLARE private cleanupInterval = 3600000 (1 hour)
- [x] 8.3.3. IMPLEMENT start(): void method
- [x] 8.3.4. SET intervalId = window.setInterval(runCleanup, 3600000)
- [x] 8.3.5. IMPLEMENT stop(): void method
- [x] 8.3.6. IMPLEMENT runCleanup(): void method
- [x] 8.3.7. CALL deleteExpiredEntries(now)
- [x] 8.3.8. CALL archiveOldEntries(now)
- [x] 8.3.9. LOG cleanup metrics via Engine.debug.metric()
- [x] 8.3.10. IMPLEMENT private deleteExpiredEntries() via SQL DELETE
- [x] 8.3.11. IMPLEMENT private archiveOldEntries() via SQL UPDATE

### Task 8.4: Integrate query API with DebugHub

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\debug\debug-hub.ts`

- [x] 8.4.1. IMPORT LogQueryEngine
- [x] 8.4.2. CREATE private queryEngine instance
- [x] 8.4.3. IMPLEMENT public query(options: QueryOptions): LogQueryResult method
- [x] 8.4.4. DELEGATE to queryEngine.query(options)
- [x] 8.4.5. IMPLEMENT traceIntent() delegating to IntentFlowTracker
- [x] 8.4.6. IMPLEMENT analyzePool() delegating to PoolSnapshotManager
- [x] 8.4.7. IMPLEMENT getEntityLifecycle() delegating to EntityLifecycleTracker

### Task 8.5: Register sinks and start retention

**File**: `D:\GIT\archlab\root\archlab-ide\src\renderer\engine\index.ts`

- [x] 8.5.1. IMPORT WebhookSink, RetentionPolicyEngine
- [x] 8.5.2. INSTANTIATE WebhookSink and call register()
- [x] 8.5.3. INSTANTIATE RetentionPolicyEngine
- [x] 8.5.4. CALL retentionEngine.start() after Engine initialization
- [x] 8.5.5. EXPOSE via Engine.debug

**Phase Gate**:

- [x] Bash 'npm run build' → vite validation (BLOCKING)

---

# APPENDIX A: File Organization

```
root/archlab-ide/src/
├── renderer/engine/debug/
│   ├── debug-hub.ts                          (modify: add logSchema, query methods)
│   ├── debug-types.ts                        (modify: export SchemaLogEntry)
│   ├── log-schema-types.ts                   (NEW: 145 lines)
│   ├── formatters/
│   │   └── pag-formatter.ts                  (NEW: 120 lines)
│   ├── trackers/
│   │   ├── intent-flow-tracker.ts            (NEW: 148 lines)
│   │   ├── pool-snapshot-manager.ts          (NEW: 142 lines)
│   │   └── entity-lifecycle-tracker.ts       (NEW: 135 lines)
│   ├── query/
│   │   └── log-query-engine.ts               (NEW: 150 lines)
│   ├── sinks/
│   │   └── webhook-sink.ts                   (NEW: 110 lines)
│   └── retention/
│       └── retention-policy-engine.ts        (NEW: 95 lines)
├── main/debug/
│   ├── debug-db.ts                           (modify: add migration logic)
│   └── migrations/
│       └── 001-living-logger.ts              (NEW: 130 lines)
└── renderer/utils/
    └── dom-factory.ts                        (modify: add lifecycle hooks)
```

# APPENDIX B: Registry Tokens

| Token                                   | Action | Phase   |
| --------------------------------------- | ------ | ------- |
| `engine:debug:types`                    | CREATE | Phase 1 |
| `engine:formatter:pag`                  | CREATE | Phase 4 |
| `engine:tracker:intent-flow`            | CREATE | Phase 5 |
| `engine:tracker:pool-snapshot`          | CREATE | Phase 6 |
| `engine:tracker:entity-lifecycle`       | CREATE | Phase 7 |
| `engine:debug:query`                    | CREATE | Phase 8 |
| `engine:logger-sink:webhook`            | CREATE | Phase 8 |
| `engine:debug:retention`                | CREATE | Phase 8 |

# FINAL GATE (BLOCKING)

```bash
npm run build && npm run verify-codebase
```

- [x] Zero avoidance anti-patterns
- [x] All 7 Laws satisfied
- [x] DOM.createElement with entity tracking maintained
- [x] Engine.logger (no console.log in production code)
- [x] CSS tokens (no px/hex) - N/A for this implementation
- [x] Registry pattern (all trackers/sinks registry-based)
- [x] File size limit: 150 lines max (enforced)
- [x] Folder size limit: 6 files max (enforced via subfolders)
