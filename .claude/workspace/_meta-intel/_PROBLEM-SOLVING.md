**INTAKE → ABSTRACT → DEFINE → CONSTRAIN → EXPLORE → PREDICT → CHOOSE → EXECUTE → VALIDATE → REFINE**

This chain operates identically across domains:

| Step      | Puzzle                    | Software            | Algorithm            |
| --------- | ------------------------- | ------------------- | -------------------- |
| INTAKE    | Read board state          | Parse system state  | Read input           |
| ABSTRACT  | Identify threats/patterns | Model components    | Build representation |
| DEFINE    | Checkmate/solved state    | Requirements/spec   | Target output        |
| CONSTRAIN | Legal moves               | Validation rules    | Domain bounds        |
| EXPLORE   | Generate candidate moves  | Design alternatives | Search space         |
| PREDICT   | Simulate outcomes         | Test scenarios      | Evaluate branches    |
| CHOOSE    | Select best move          | Pick implementation | Select path          |
| EXECUTE   | Make move                 | Deploy code         | Apply operation      |
| VALIDATE  | Check position            | Monitor metrics     | Verify output        |
| REFINE    | Adjust strategy           | Iterate/rollback    | Update heuristics    |

## Critical Observations

**ABSTRACT determines efficiency**. Poor representation creates expensive EXPLORE/PREDICT phases. Chess piece values compress evaluation. Rubik's cube group theory reduces search space. Software data structures determine algorithmic complexity.

**CONSTRAIN reduces combinatorial explosion**. Without bounds, EXPLORE becomes intractable. Type systems, validation rules, and move legality all serve the same function: prune invalid paths before computation.

**PREDICT prevents commitment to bad paths**. Testing, simulation, mental lookahead all implement the same lookahead search. Cost: computation time. Benefit: avoid expensive backtracking.

**VALIDATE triggers REFINE**. Mismatch between expected and actual triggers either local correction (regenerate at EXPLORE) or global reset (return to ABSTRACT with new model).

The chain runs recursively. EXPLORE spawns sub-chains. REFINE may reset to any prior step depending on failure severity.

**The pattern is invariant. Only the representation changes.**
