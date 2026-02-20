# meta-progressive-calculator

Execute META-progressive-calculator agent with adaptive complexity analysis, progressive refinement, and hierarchical checklist generation.

## Usage

Provide domain (path or conceptual) and analysis target:

```
meta-progressive-calculator <domain-path-or-concept> <analysis-description>
```

### Examples

**Filesystem domain:**
```
meta-progressive-calculator "root/archlab-ide/src/renderer/**/*.ts" "architecture compliance analysis"
```

**Conceptual domain:**
```
meta-progressive-calculator "ARCHLAB 7 Laws" "cross-law dependency analysis"
```

**Specific subsystem:**
```
meta-progressive-calculator "root/archlab-ide/src/renderer/engine" "pool lifecycle and observation patterns"
```

## What This Agent Does

### 1. Progressive META Coordinate Calculation
- Calculates X/Y/Z/W axes with iterative refinement
- Adapts depth based on domain complexity
- Progressive density accumulation (shallow → deep)
- Multi-pass refinement until goal achieved or max iterations
- Adaptive threshold adjustment (simple domains: lower thresholds, complex domains: higher thresholds)

### 2. Hierarchical Checklist Generation
Based on embedded META-CHECKLIST.md and META-ADAPTIVE-CHECKLIST.md patterns:
- **Hierarchical Numbering**: 1.0 → 1.1 → 1.1.1 → 1.1.2 → 1.2 → 2.0
- **3D Validation Graphs**: Progress bars showing X/Y/Z/W axis completion
- **Ripple Chains**: Cross-references showing propagation (1.1 → 2.3 → 5.1)
- **Adaptive Depth**: Checklist detail scales with complexity
- **Progressive Expansion**: Starts high-level, expands based on findings

### 3. Adaptive Complexity Response
- Detects domain complexity (simple, moderate, complex, extremely complex)
- Adjusts analysis depth dynamically
- Scales checklist granularity to match complexity
- Progressive refinement loops until goal achieved
- Adaptive stopping conditions (avoids over-analyzing simple domains)

### 4. Ripple Chain Propagation
- Maps dependencies between checklist items
- Detects cross-axis ripples (X-axis → Y-axis → Z-axis → W-axis)
- Traces propagation paths with magnitude (HIGH/MEDIUM/LOW)
- Calculates ripple impact across axes
- Identifies amplification points

### 5. 3D Validation Graph Generation
Generates progress tracking per phase:
```
╔═══════════════════════════════════════════════════════════╗
║ GATE VALIDATION PROGRESS (3D Graph)                      ║
╠═══════════════════════════════════════════════════════════╣
║ X-Axis (WHAT)    : [████████░░] 80% ✓ Threshold Met     ║
║ Y-Axis (HOW)     : [██████░░░░] 60% ⚠ Below Threshold    ║
║ Z-Axis (WHY)     : [█████████░] 90% ✓ Threshold Met     ║
║ W-Axis (OBSERVER): [████░░░░░░] 40% ⚠ Below Threshold    ║
╠═══════════════════════════════════════════════════════════╣
║ Overall Progress : [███████░░░] 68%                      ║
║ Implosion Risk   : MEDIUM (Y-axis, W-axis below 70%)     ║
╚═══════════════════════════════════════════════════════════╝
```

## Output Artifacts

The agent generates:

1. **META-PROGRESSIVE-CHECKLIST-{timestamp}.md** - Hierarchical checklist with:
   - META coordinate summary table
   - Numbered checklist items (N.N.N structure)
   - 3D validation graphs per phase
   - Ripple chain annotations
   - Iteration history table

2. **Embedded in checklist**:
   - Ripple chain analysis (top 10 chains)
   - Cross-axis propagation paths
   - Verification summary

## Progressive Refinement Features

### Iteration Tracking
- Each refinement iteration logged with improvements
- Density evolution tracked across iterations
- Goal achievement conditions per iteration

### Threshold Adaptation
- Simple domains: lower thresholds (x: 0.5, y: 0.4, z: 0.6, w: 0.3)
- Moderate domains: standard thresholds (x: 0.6, y: 0.5, z: 0.7, w: 0.4)
- Complex domains: higher thresholds (x: 0.7, y: 0.6, z: 0.8, w: 0.5)
- Extremely complex: maximum thresholds (x: 0.8, y: 0.7, z: 0.9, w: 0.6)

### Checklist Expansion
- Iteration 1: 1.0 level items only
- Iteration 2+: Expands to 1.1 level
- Iteration 3+: Expands to 1.1.1 level as needed

### Ripple Discovery
- Finds new ripple chains each iteration
- Accumulates cross-axis dependencies
- Calculates propagation magnitude

### Goal Achievement
- Stops when all axes meet adaptive thresholds
- Or stops at max_iterations (adaptive: 3-10 based on complexity)
- Reports goal achievement status

## Adaptive Stopping Conditions

- **Simple domains**: 1-3 iterations sufficient
- **Moderate domains**: 3-5 iterations
- **Complex domains**: 5-8 iterations
- **Extremely complex domains**: 8-10 iterations (max bound)
- **Early stop**: If goal achieved before max iterations
- **Progressive expansion**: Continues until no new patterns found within iteration limit

## Embedded Patterns

This agent embeds the following META checklist patterns internally:
- Hierarchical numbering system (from META-CHECKLIST.md)
- 3D validation graph structure (from META-ADAPTIVE-CHECKLIST.md)
- Ripple chain format (from META-ADAPTIVE-CHECKLIST.md)
- Progressive refinement algorithm (from META-ADAPTIVE-README.md)

No external template dependencies - all patterns are embedded in the agent logic.

## Validation

The agent verifies:
- ✅ Hierarchical numbering correctness (1.0 → 1.1 → 1.1.1)
- ✅ Ripple chains are bidirectional (forward and backward references)
- ✅ 3D graph percentages match calculated densities
- ✅ Adaptive thresholds adjust correctly based on complexity
- ✅ Progressive refinement converges (bounded by max_iterations)
- ✅ No unbounded loops (all loops have max_iterations guard)

## Inherited Skepticism

This agent includes:
- Tool calibration phase (verifies grep accuracy before use)
- Claim verification tracking (verified_claims and refuted_claims arrays)
- Verification rate threshold (proceeds only if >= 0.8)
- Safe iteration loop patterns (max_iterations bound, goal_achieved condition)
- Skeptical ALWAYS/NEVER directives
- Empirical evidence requirement (no assumptions without verification)

## Safety Features

- **Bounded loops**: All iteration loops have max_iterations limit
- **Goal conditions**: Loops terminate when goal_achieved = true
- **Iteration guards**: iteration_count checked before each loop continuation
- **Adaptive limits**: max_iterations scales with complexity (3-10)
- **Abort handling**: Graceful abort with diagnostic report if failures occur

---

**Example invocation:**

```
meta-progressive-calculator "root/archlab-ide/src/renderer" "DOM factory compliance and entity lifecycle analysis"
```

This will:
1. Assess renderer directory complexity
2. Calculate X/Y/Z/W coordinates progressively
3. Generate hierarchical checklist with 3D graphs
4. Map ripple chains across DOM factory patterns
5. Output comprehensive META analysis report

The checklist will guide systematic analysis of DOM factory usage, entity tracking, lifecycle management, and observation patterns across the renderer codebase.
