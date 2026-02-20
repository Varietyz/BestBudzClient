# Agent & Context Forking in Claude Code v2.1.0+

## Overview

Context forking is a powerful feature introduced in Claude Code v2.1.0 that allows skills, agents, and slash commands to run in isolated sub-agent contexts with their own conversation history. This prevents main conversation pollution and enables parallel execution of complex operations.

## What is Context Forking?

Context forking creates an **isolated execution environment** where:

- **Separate Conversation History** - Forked context has its own message history
- **No Main Conversation Pollution** - Research/exploration doesn't clutter primary work
- **Fresh Perspective** - Clean slate without anchoring bias from main conversation
- **Parallel Execution** - Can run alongside main conversation
- **Independent State** - Forked context has isolated state heap

## Why Use Context Forking?

### Problem: Context Pollution

```
Main Conversation:
User: Help me build a React dashboard
Claude: [Designs architecture]
User: /research best-practices-for-charts
Claude: [Loads 50KB of chart library docs]
User: Now implement the dashboard
Claude: [Confused by mixing dashboard context + chart docs]
```

### Solution: Forked Context

```
Main Conversation:
User: Help me build a React dashboard
Claude: [Designs architecture]
User: /research best-practices-for-charts   # <-- Runs in fork
Claude: [Fork: Researches charts separately]
[Fork completes, returns summary]
User: Now implement the dashboard
Claude: [Clean context, focused on dashboard]
```

## Enabling Context Forking

### In Skills (skills.template)

```yaml
---
name: deep-research
description: Research topic in depth without main conversation pollution
context: fork
agent: general-purpose
allowed-tools: Read, Grep, WebSearch, WebFetch
---
```

### In Agents (agents.template)

```yaml
---
name: code-analyzer
description: Analyze code quality with fresh perspective
context: fork
tools: Read, Grep, Glob
model: sonnet
---
```

### In Slash Commands (commands.template)

```yaml
---
description: Investigate bug with clean context
context: fork
agent: general-purpose
allowed-tools: Read, Grep, Bash(git:*)
---
```

## Context Values

| Value    | Description                                    | Use Case                     |
| -------- | ---------------------------------------------- | ---------------------------- |
| `normal` | Default - runs in main conversation context   | Most skills/agents/commands  |
| `fork`   | Isolated sub-agent with separate conversation | Research, analysis, auditing |

## When to Use Fork

### ✅ Use `context: fork` when:

1. **Research & Exploration**

   - Deep code investigation
   - Best practices research
   - Documentation analysis
   - Comparative analysis

2. **Complex Multi-Step Operations**

   - Long-running analysis tasks
   - Multi-file refactoring audits
   - Compliance checking
   - Code quality assessment

3. **Fresh Perspective Needed**

   - Code review without anchoring bias
   - Bug investigation from scratch
   - Security audit with clean slate

4. **Parallel Execution**
   - Background research while main work continues
   - Multiple independent analyses
   - Concurrent validation checks

### ❌ Avoid `context: fork` when:

1. **Needs Main Conversation Context**

   - User has explained requirements in main conversation
   - Relies on previous code decisions
   - Needs access to conversation history

2. **Simple Operations**

   - Single file edits
   - Quick lookups
   - Trivial validation

3. **Incremental Work**
   - Building on previous conversation
   - Iterative refinement
   - Context-dependent decisions

## Fork Behavior

### What Gets Forked

✅ **Copied to Fork**:

- System prompt
- Available tools (from `allowed-tools`)
- Agent capabilities
- File system access

❌ **NOT Copied to Fork**:

- Main conversation history
- User's previous messages
- Claude's previous responses
- Accumulated context

### Fork Lifecycle

```
1. User invokes skill/agent/command with context: fork
   ↓
2. Claude creates isolated sub-agent
   ↓
3. Sub-agent executes with clean slate
   ↓
4. Sub-agent completes and returns results
   ↓
5. Results returned to main conversation
   ↓
6. Fork context discarded
```

## Integration with Hooks

Forked contexts support agent-scoped hooks (v2.1.0+):

```yaml
---
name: research-agent
context: fork
hooks:
  - type: PreToolUse
    command: echo "Fork starting research"
  - type: PostToolUse
    command: echo "Tool completed in fork"
  - type: Stop
    command: echo "Fork complete, returning to main"
---
```

### Hook Execution in Forks

- **PreToolUse** - Runs before each tool in forked context
- **PostToolUse** - Runs after each tool in forked context
- **Stop** - Runs when forked context completes
- **SubagentStart** - Runs when fork is created (if defined)
- **SubagentStop** - Runs when fork terminates (if defined)

## Session Forking (CLI)

Command-line session forking with custom IDs:

```bash
# Fork current session with custom ID
claude --resume my-session --fork-session --session-id forked-work-1

# Fork and continue
claude --continue --fork-session --session-id parallel-task

# Multiple parallel forks
claude --resume project-x --fork-session --session-id worker-1 &
claude --resume project-x --fork-session --session-id worker-2 &
claude --resume project-x --fork-session --session-id worker-3 &
```

### SDK Session Forking

Claude Code SDK v2.0.1+ supports `forkSession` for 10-20x speedups:

```typescript
import { ClaudeAgent } from '@anthropic-ai/claude-code-sdk';

const agent = new ClaudeAgent({
  forkSession: true, // Enable session forking
  parallelWorkers: 5 // Spawn 5 parallel forks
});

// Each fork has isolated context but shares global state
```

## Architectural Integration

### With LAW 2 (No Child-to-Parent Callbacks)

Forked contexts **emit intents**, never call back:

```
Main Conversation (Parent)
    ↓ spawns
Forked Context (Child)
    ↓ emits intent
EventBus → Main Conversation observes
```

Forks communicate results via:

- Return values
- Intent emission to EventBus
- Summary messages

### With LAW 7 (Single Import Boundary)

Forked contexts resolve dependencies via registry:

```
Fork → Registry.resolve("token") → Module
```

Not direct imports. This ensures forks don't break dependency isolation.

## Best Practices

### 1. Descriptive Names

```yaml
# ✅ Good - Clear purpose
name: deep-codebase-analysis
context: fork

# ❌ Bad - Vague
name: analyzer
context: fork
```

### 2. Tool Restrictions

```yaml
# ✅ Restrict to needed tools only
name: readonly-research
context: fork
allowed-tools: Read, Grep, WebSearch

# ❌ Don't give unnecessary permissions
context: fork
# (inherits all tools)
```

### 3. Model Selection

```yaml
# ✅ Use appropriate model for fork
name: quick-scan
context: fork
model: haiku # Fast for simple tasks

name: deep-analysis
context: fork
model: opus # Thorough for complex tasks
```

### 4. Clear Descriptions

```yaml
# ✅ Claude knows when to use fork
description: Investigate security vulnerabilities with fresh perspective, no main conversation bias

# ❌ Claude won't know to fork
description: Security check
```

## Debugging Forked Contexts

### Check Fork Status

```bash
# View active forks
claude /tasks

# Monitor fork output
claude /logs --filter fork
```

### Fork Didn't Run?

1. **Check frontmatter syntax** - YAML must be valid
2. **Verify `context: fork`** - Exact spelling, lowercase
3. **Ensure skill/agent is loaded** - Run `/context` to verify
4. **Check description** - Must trigger invocation

### Fork Seems Slow?

1. **Model choice** - Using Opus? Consider Sonnet/Haiku
2. **Tool access** - Restrict to needed tools only
3. **Parallel execution** - Multiple forks compete for resources

## Examples

### Example 1: Research Skill

```yaml
---
name: architecture-research
description: Research architectural patterns for given problem domain with clean context
context: fork
agent: general-purpose
allowed-tools: Read, Grep, WebSearch, WebFetch
model: sonnet
---

THIS SKILL RESEARCHES architectural patterns without polluting main conversation

# PHASE 1: INVESTIGATE

READ relevant documentation
GREP for pattern examples
SEARCH web for best practices

# PHASE 2: SYNTHESIZE

ANALYZE findings
CREATE summary
RETURN recommendations
```

### Example 2: Code Review Agent

```yaml
---
name: fresh-code-reviewer
description: Review code with fresh perspective, no anchoring from previous discussions
context: fork
tools: Read, Grep, Glob
model: opus
hooks:
  - type: Stop
    command: echo "Code review complete, findings attached"
---

THIS AGENT REVIEWS code with clean slate

# PHASE 1: READ CODE

GLOB target files
READ each file
EXTRACT patterns

# PHASE 2: ANALYZE

CHECK compliance
FIND issues
VERIFY best practices

# PHASE 3: REPORT

SUMMARIZE findings
RATE severity
PROVIDE recommendations
```

### Example 3: Background Research Command

```yaml
---
description: Research topic in background while main work continues
context: fork
agent: Explore
allowed-tools: Read, Grep, WebSearch
---

Background research command that doesn't interrupt main conversation flow.

SEARCH topic: $ARGUMENTS
ANALYZE results
SUMMARIZE key points
RETURN findings
```

## Comparison: Normal vs Fork

| Aspect               | Normal Context              | Fork Context                       |
| -------------------- | --------------------------- | ---------------------------------- |
| Conversation History | Shared with main            | Isolated, starts fresh             |
| State                | Accumulated state           | Clean state                        |
| Execution            | Sequential with main        | Can run in parallel                |
| Main Conversation    | Polluted with all messages  | Protected from fork messages       |
| Use Case             | Incremental work            | Research, audits, exploration      |
| Performance          | Fast (no spawn overhead)    | Slight spawn overhead              |
| Context Clarity      | Can become muddled          | Always clean                       |
| Anchoring Bias       | May influence decisions     | Fresh perspective                  |
| Tool Access          | Inherits from conversation  | Explicit in `allowed-tools`        |
| Model                | Main conversation model     | Can specify different model        |
| Hooks                | Shared hook scope           | Isolated hook scope                |
| Results              | Inline in conversation      | Summary returned to main           |
| Debugging            | Easy to follow in /logs     | Separate fork logs                 |
| Cost                 | Single context cost         | Additional context for fork        |
| Complexity           | Simple, straightforward     | Requires understanding fork model  |

## Performance Considerations

### Fork Overhead

- **Spawn Time**: ~200-500ms per fork
- **Memory**: Each fork has separate context heap
- **Tokens**: Forked system prompt counts toward usage

### Optimization Tips

1. **Reuse Forks** - Don't spawn new fork for each call
2. **Haiku for Simple Tasks** - Fast model for lightweight forks
3. **Limit Parallel Forks** - Too many compete for resources
4. **Tool Restrictions** - Only give needed tools to fork

## Advanced: SDK Integration

### Custom Fork Control

```typescript
import { ClaudeAgent } from '@anthropic-ai/claude-code-sdk';

const agent = new ClaudeAgent();

// Manual fork creation
const fork = await agent.fork({
  sessionId: 'custom-fork-1',
  systemPrompt: 'Custom fork prompt',
  tools: ['Read', 'Grep'],
  model: 'haiku'
});

// Execute in fork
const result = await fork.execute('Analyze codebase');

// Fork auto-discards after execution
```

### Parallel Fork Workers

```typescript
const results = await Promise.all([
  agent.fork({ sessionId: 'worker-1' }).execute('Task 1'),
  agent.fork({ sessionId: 'worker-2' }).execute('Task 2'),
  agent.fork({ sessionId: 'worker-3' }).execute('Task 3'),
]);

// 3x speedup with parallel forks
```

## References

- **Agent Skills**: https://code.claude.com/docs/en/skills
- **Subagents**: https://code.claude.com/docs/en/subagents
- **Context Forking Announcement**: https://x.com/EricBuess/status/2009073718450889209
- **Feature Request**: https://github.com/anthropics/claude-code/issues/16153
- **Fork Workflows**: https://kau.sh/blog/agent-forking/

## Summary

Context forking (`context: fork`) is a powerful isolation mechanism for:

- ✅ Research and exploration without main conversation pollution
- ✅ Fresh perspective for reviews and audits
- ✅ Parallel execution of independent tasks
- ✅ Complex multi-step operations in clean state

Use it wisely for operations that benefit from isolation, but avoid it for work that needs main conversation context.
