# algorithm-maintainer

Execute algorithm-maintainer agent with verification discipline to maintain and expand the algorithm repository.

## Usage

$ARGUMENTS

## Modes

**DISCOVER** - Discover algorithm patterns from codebase and web sources
```
algorithm-maintainer DISCOVER [domain]
```

**SYNTHESIZE** - Synthesize new algorithms by combining existing patterns
```
algorithm-maintainer SYNTHESIZE
```

**MAINTAIN** - Check repository structure and create missing categories
```
algorithm-maintainer MAINTAIN
```

**SEARCH** - Search for algorithms by pattern or domain
```
algorithm-maintainer SEARCH [query]
```

## Examples

Discover algorithms from codebase:
```
algorithm-maintainer DISCOVER
```

Discover NLP algorithms from web:
```
algorithm-maintainer DISCOVER "NLP parsing"
```

Maintain repository structure:
```
algorithm-maintainer MAINTAIN
```

Search for semantic algorithms:
```
algorithm-maintainer SEARCH "semantic analysis"
```

## Inherited Skepticism

This agent includes:
- Tool calibration phase
- Claim verification tracking (verified_claims, refuted_claims)
- Verification rate threshold (0.7)
- Safe iteration loop patterns (max_iterations: 20, goal_achieved)
- Web source verification (academic/technical sources only)
- Algorithm uniqueness verification
- Verb/preposition ontology validation

## Algorithm Sources

The agent discovers algorithms from:
1. **Codebase patterns** - Function chains, class hierarchies, processing pipelines
2. **Web research** - Academic papers (arxiv.org, acm.org, ieee.org)
3. **Documentation** - Markdown files, code comments
4. **Synthesis** - Combining existing algorithm patterns

## Verification Discipline

Every discovered algorithm is:
- Verified for uniqueness against existing repository
- Verified for correct verb usage against VERBAL ONTOLOGY
- Verified for correct preposition usage against PREPOSITION SLOTS
- Classified into correct loop class (Perceptual, Cognitive, Decision, Construction, Adaptive, Meta, Hypothesis-Validation)
- Formatted according to VERB-CHAIN template

## Philosophical Foundation

Algorithms follow 1960s-1980s principles fused with modern best practices:
- **Symbolic Compression** - Store rules/generators, not raw data
- **Homoiconicity** - Code, data, state interchangeable
- **Deterministic Correctness** - Fail loudly, halt on inconsistency
- **Human-Machine Co-Reasoning** - Human is part of computation
- **Constraint-First** - Begin with invariants, not features

## Repository Structure

Maintains category files:
- `_PERCEPTUAL-LOOP-ALGORITHMS.md` - Signal detection → Analysis → Response
- `_COGNITIVE-LOOP-ALGORITHMS.md` - Information processing → Understanding
- `_DECISION-LOOP-ALGORITHMS.md` - Option generation → Evaluation → Selection
- `_CONSTRUCTION-LOOP-ALGORITHMS.md` - Component assembly → Validation
- `_ADAPTIVE-LOOP-ALGORITHMS.md` - Environment sensing → Strategy adjustment
- `_META-LOOP-ALGORITHMS.md` - Self-monitoring → Strategy refinement
- `_HYPOTHESIS-VALIDATION-LOOP-ALGORITHMS.md` - Hypothesis → Constraint testing
- `_UNIFIED-COGNITIVE-ARCHITECTURE-ALGORITHMS.md` - Cross-loop integration
- `_1960s-1980s-THEORETICAL-FOUNDATIONS.md` - Historical principles
- `_ARCHITECTURAL-WORKFLOW-ALGORITHMS.md` - System-level workflows
