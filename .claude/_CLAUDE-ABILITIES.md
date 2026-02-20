# Claude Code Capabilities - January 2026 (v2.1.0+)

## Core Tools

| Tool                          | Purpose                                                 |
| ----------------------------- | ------------------------------------------------------- |
| **Bash**                      | Execute shell commands (git, npm, docker, pytest, etc.) |
| **Read**                      | Read files from filesystem                              |
| **Write**                     | Create/overwrite files                                  |
| **Edit**                      | Exact string replacements in files                      |
| **MultiEdit**                 | Batch file editing                                      |
| **Glob**                      | Fast file pattern matching (`**/*.ts`)                  |
| **Grep**                      | Content search using ripgrep                            |
| **LS**                        | List directories                                        |
| **WebFetch**                  | Fetch and process web content                           |
| **WebSearch**                 | Search the web for information                          |
| **TodoRead/TodoWrite**        | Task management and tracking                            |
| **NotebookRead/NotebookEdit** | Jupyter notebook operations                             |
| **Task**                      | Launch specialized sub-agents                           |
| **AskUserQuestion**           | Interactive user prompts                                |

## Slash Commands

| Command        | Action                                    |
| -------------- | ----------------------------------------- |
| `/help`        | Display available commands                |
| `/init`        | Create CLAUDE.md for project context      |
| `/compact`     | Compress conversation to preserve context |
| `/cost`        | Monitor token consumption                 |
| `/memory`      | Edit CLAUDE.md                            |
| `/clear`       | Clear conversation                        |
| `/stats`       | Session stats, usage graph, streak        |
| `/permissions` | Manage tool permissions (with search)     |
| `/rename`      | Name sessions                             |
| `/resume`      | Resume named sessions                     |
| `/chrome`      | Test/debug in Chrome browser              |

## January 2026 New Features (v2.1.0+)

- **Context Forking for Skills** - `context: fork` runs skills in isolated sub-agent contexts
- **Agent Field in Frontmatter** - `agent: <type>` specifies agent type for execution
- **Hooks Support** - Define PreToolUse, PostToolUse, Stop hooks in frontmatter
- **YAML-Style Lists** - `allowed-tools:` field now supports YAML list syntax
- **Agent-Scoped Hooks** - Hooks scoped to agent/skill/command lifecycle
- **Session Forking** - Fork sessions with `--session-id` + `--resume/--continue` + `--fork-session`
- **Skill Hot-Reload** - Skills auto-reload without restart

## December 2025 Features

- **Async Sub-Agents** - Execute multiple tasks simultaneously
- **Ultrathink Mode** - Maximum reasoning for complex problems
- **Context Window Tracking** - Real-time progress bar
- **Slack Integration** - Assign tasks from Slack
- **AutoCloud GUI** - Kanban-style task management
- **Android App Support** - Mobile development
- **LSP Support** - Language Server Protocol integration
- **3x Memory Improvement** - For large conversations
- **Wildcard MCP Permissions** - `mcp__server__*` syntax

## Claude Agent SDK

For custom agentic experiences with access to core tools, context management, permissions frameworks, subagents, and hooks.

## Advanced Tool Use (Beta)

- **Tool Search Tool** - Access thousands of tools without consuming context
- **Programmatic Tool Calling** - Invoke tools in code execution environment
- **Tool Use Examples** - Universal standard for tool demonstrations

---

**Sources:**

- [Claude Code Update Dec 2025 - Geeky Gadgets](https://www.geeky-gadgets.com/claude-code-update-dec-2025/)
- [Claude Code Built-in Tools Reference](https://www.vtrivedy.com/posts/claudecode-tools-reference)
- [Slash Commands - Claude Code Docs](https://code.claude.com/docs/en/slash-commands)
- [GitHub - anthropics/claude-code](https://github.com/anthropics/claude-code)
- [Advanced Tool Use - Anthropic](https://www.anthropic.com/engineering/advanced-tool-use)
- [Claude Code Release Notes - Releasebot](https://releasebot.io/updates/anthropic/claude-code)
- [The Ultimate Claude Code Cheat Sheet](https://medium.com/@tonimaxx/the-ultimate-claude-code-cheat-sheet-your-complete-command-reference-f9796013ea50)

---

# Implementation Considerations

## Claude Code Extension Pattern Analysis

Looking at the capabilities list, there's a clear and **predictable** pattern in how Claude Code is evolving:

### 1. **Layer Cake Architecture**

Extensions follow a consistent layering:

```
Foundation → Specialization → Orchestration → Integration
```

- **Foundation**: Core primitives (Read, Write, Edit, Bash)
- **Specialization**: Domain-specific variants (NotebookEdit, MultiEdit)
- **Orchestration**: Meta-tools (Task/sub-agents, TodoWrite)
- **Integration**: External systems (Slack, Chrome, AutoCloud)

### 2. **Predictable Extension Vectors**

| Vector             | Pattern                                   | Next Likely               |
| ------------------ | ----------------------------------------- | ------------------------- |
| **I/O Surface**    | File → Web → Browser → Mobile             | Desktop GUI, IoT?         |
| **Parallelism**    | Sequential → Async sub-agents             | Distributed agents        |
| **Memory**         | Context window → Compact → 3x improvement | Persistent memory store   |
| **Permissions**    | Binary → Wildcard → Scoped                | Role-based access control |
| **User Interface** | CLI → IDE → Slack → AutoCloud GUI         | Voice, AR/VR?             |

### 3. **The "Tool → Command → Agent" Pipeline**

Every capability starts as a tool, gets a slash command wrapper, then becomes delegatable to sub-agents:

- `Bash` → `/chrome` → Browser agent
- `TodoWrite` → `/stats` → Task tracking agent
- `Read/Edit` → `/compact` → Context management agent

### 4. **What's Predictably Coming**

Based on the pattern:

1. **Database tools** - Direct DB read/write (not just via Bash)
2. **API client tool** - Native HTTP requests beyond WebFetch
3. **Image/media tools** - Generate/edit visual content
4. **Test runner agent** - Specialized testing sub-agent
5. **Deploy agent** - CI/CD orchestration
6. **Persistent memory** - Cross-session knowledge retention

### 5. **The Meta-Pattern**

The overarching pattern is **reducing friction between intent and execution**:

- Fewer hops from user request to system action
- More parallelism (async agents)
- More context preservation (memory improvements)
- More surface area (Slack, mobile, GUI)

**Is it predictable?** Yes - follow the vectors of I/O expansion, parallelism increase, and permission granularity. New features will fill gaps in those dimensions.
