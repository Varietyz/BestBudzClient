/**
 * DOM.createElement Async Migration Script
 *
 * Migrates synchronous DOM.createElement calls to async/await pattern
 * for Phase 2 code splitting with lazy manager loading.
 *
 * PATTERNS HANDLED:
 * 1. `const x = DOM.createElement(` → `const x = await DOM.createElement(`
 * 2. `return DOM.createElement(` → `return await DOM.createElement(`
 * 3. Containing functions made async if not already
 *
 * Usage:
 *   node migrate-dom-async.cjs --analyze          # Report only (default)
 *   node migrate-dom-async.cjs --apply            # Apply migrations
 *   node migrate-dom-async.cjs --file=<pattern>   # Filter to specific files
 *
 * @module tools/migrate-dom-async
 */

const fs = require("fs");
const path = require("path");

const ROOT = path.resolve(__dirname, "../../../root/archlab-ide/src/renderer");
const REPORT_PATH = path.resolve(__dirname, "async-migration-report.json");

// =============================================================================
// Pattern Detection
// =============================================================================

/**
 * Find all DOM.createElement calls and their containing functions
 */
function analyzeFile(filePath) {
    const fullPath = path.join(ROOT, filePath);
    if (!fs.existsSync(fullPath)) {
        return null;
    }

    const content = fs.readFileSync(fullPath, "utf-8");
    const lines = content.split("\n");

    const findings = {
        file: filePath,
        createElementCalls: [],
        functionsToMakeAsync: [],
        alreadyAsync: [],
        returnStatements: [],
        totalCalls: 0,
    };

    // Track function/method boundaries for async conversion
    const functionStack = [];

    lines.forEach((line, idx) => {
        const lineNum = idx + 1;

        // Detect function/method starts
        const funcMatch = line.match(/^(\s*)(async\s+)?(?:function\s+(\w+)|(\w+)\s*\([^)]*\)\s*(?::\s*[^{]+)?{|(\w+)\s*=\s*(?:async\s+)?\([^)]*\)\s*(?::\s*[^=]+)?=>)/);
        if (funcMatch) {
            const isAsync = !!funcMatch[2] || line.includes("async ");
            const indent = funcMatch[1].length;
            const name = funcMatch[3] || funcMatch[4] || funcMatch[5] || "anonymous";
            functionStack.push({ name, lineNum, isAsync, indent, hasCreateElement: false });
        }

        // Detect render() method specifically
        const renderMatch = line.match(/^(\s*)render\(\)(\s*:\s*[^{]+)?\s*{/);
        if (renderMatch) {
            const isAsync = line.includes("async ");
            const indent = renderMatch[1].length;
            functionStack.push({ name: "render", lineNum, isAsync, indent, hasCreateElement: false });
        }

        // Detect arrow functions with bodies
        const arrowMatch = line.match(/^(\s*)(?:const|let)\s+(\w+)\s*=\s*(async\s+)?\([^)]*\)\s*(?::\s*[^=]+)?=>\s*{/);
        if (arrowMatch) {
            const isAsync = !!arrowMatch[3];
            const indent = arrowMatch[1].length;
            const name = arrowMatch[2];
            functionStack.push({ name, lineNum, isAsync, indent, hasCreateElement: false });
        }

        // Detect DOM.createElement calls
        const createMatch = line.match(/DOM\.createElement\s*\(/);
        if (createMatch) {
            findings.totalCalls++;

            // Mark current function as having createElement
            if (functionStack.length > 0) {
                functionStack[functionStack.length - 1].hasCreateElement = true;
            }

            // Pattern 1: const x = DOM.createElement(
            const constMatch = line.match(/^(\s*)(const|let)\s+(\w+)\s*=\s*DOM\.createElement\s*\(/);
            if (constMatch) {
                const hasAwait = line.includes("await ");
                findings.createElementCalls.push({
                    line: lineNum,
                    pattern: "const_assignment",
                    original: line.trim(),
                    variable: constMatch[3],
                    hasAwait,
                    migration: hasAwait ? null : {
                        search: `${constMatch[2]} ${constMatch[3]} = DOM.createElement`,
                        replace: `${constMatch[2]} ${constMatch[3]} = await DOM.createElement`,
                    },
                });
            }

            // Pattern 2: return DOM.createElement(
            const returnMatch = line.match(/^(\s*)return\s+DOM\.createElement\s*\(/);
            if (returnMatch) {
                const hasAwait = line.includes("await ");
                findings.returnStatements.push({
                    line: lineNum,
                    pattern: "return_statement",
                    original: line.trim(),
                    hasAwait,
                    migration: hasAwait ? null : {
                        search: "return DOM.createElement",
                        replace: "return await DOM.createElement",
                    },
                });
            }

            // Pattern 3: inline/other patterns (nested, chained, etc.)
            if (!constMatch && !returnMatch) {
                const hasAwait = line.includes("await ");
                findings.createElementCalls.push({
                    line: lineNum,
                    pattern: "inline",
                    original: line.trim(),
                    hasAwait,
                    migration: hasAwait ? null : {
                        note: "MANUAL: Add await before DOM.createElement",
                    },
                });
            }
        }

        // Detect function/block endings (simple heuristic based on closing braces at indent)
        if (functionStack.length > 0) {
            const closingMatch = line.match(/^(\s*)}[,;]?\s*$/);
            if (closingMatch) {
                const currentIndent = closingMatch[1].length;
                const topFunc = functionStack[functionStack.length - 1];

                if (currentIndent <= topFunc.indent) {
                    const poppedFunc = functionStack.pop();
                    if (poppedFunc.hasCreateElement && !poppedFunc.isAsync) {
                        findings.functionsToMakeAsync.push({
                            name: poppedFunc.name,
                            startLine: poppedFunc.lineNum,
                            endLine: lineNum,
                        });
                    } else if (poppedFunc.hasCreateElement && poppedFunc.isAsync) {
                        findings.alreadyAsync.push({
                            name: poppedFunc.name,
                            startLine: poppedFunc.lineNum,
                        });
                    }
                }
            }
        }
    });

    return findings;
}

/**
 * Apply migrations to a file
 */
function applyMigrations(findings) {
    const fullPath = path.join(ROOT, findings.file);
    let content = fs.readFileSync(fullPath, "utf-8");
    let changes = 0;

    // Apply await insertions for const assignments
    for (const call of findings.createElementCalls) {
        if (call.migration && call.migration.search) {
            const regex = new RegExp(call.migration.search.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "g");
            const newContent = content.replace(regex, call.migration.replace);
            if (newContent !== content) {
                content = newContent;
                changes++;
            }
        }
    }

    // Apply await insertions for return statements
    for (const ret of findings.returnStatements) {
        if (ret.migration && ret.migration.search) {
            const regex = new RegExp(ret.migration.search.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "g");
            const newContent = content.replace(regex, ret.migration.replace);
            if (newContent !== content) {
                content = newContent;
                changes++;
            }
        }
    }

    // Apply async keyword to functions
    for (const func of findings.functionsToMakeAsync) {
        const lines = content.split("\n");
        const lineIdx = func.startLine - 1;
        const line = lines[lineIdx];

        // Pattern: render() { → async render() {
        if (func.name === "render" && !line.includes("async ")) {
            lines[lineIdx] = line.replace(/render\s*\(/, "async render(");
            content = lines.join("\n");
            changes++;
        }
        // Pattern: methodName() { → async methodName() {
        else if (!line.includes("async ")) {
            const methodRegex = new RegExp(`(${func.name})\\s*\\(`);
            if (methodRegex.test(line)) {
                lines[lineIdx] = line.replace(methodRegex, `async $1(`);
                content = lines.join("\n");
                changes++;
            }
        }
    }

    if (changes > 0) {
        fs.writeFileSync(fullPath, content);
    }

    return changes;
}

/**
 * Recursively find all TypeScript files
 */
function findTsFiles(dir, files = []) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            findTsFiles(fullPath, files);
        } else if (entry.name.endsWith(".ts") && !entry.name.endsWith(".d.ts")) {
            files.push(path.relative(ROOT, fullPath));
        }
    }
    return files;
}

// =============================================================================
// Main
// =============================================================================

function main() {
    const args = process.argv.slice(2);
    const shouldApply = args.includes("--apply");
    const filePattern = args.find(a => a.startsWith("--file="))?.replace("--file=", "") || null;

    console.log("DOM.createElement Async Migration Tool");
    console.log("=".repeat(50));
    console.log(`Mode: ${shouldApply ? "APPLY MIGRATIONS" : "ANALYZE ONLY"}`);
    if (filePattern) {
        console.log(`Filter: ${filePattern}`);
    }
    console.log("");

    // Find all TypeScript files
    let files = findTsFiles(ROOT);
    if (filePattern) {
        files = files.filter(f => f.includes(filePattern));
    }

    console.log(`Scanning ${files.length} TypeScript files...\n`);

    const allFindings = [];
    let totalCalls = 0;
    let totalNeedingAwait = 0;
    let totalFunctionsNeedingAsync = 0;
    let totalAlreadyAsync = 0;

    for (const file of files) {
        const findings = analyzeFile(file);
        if (!findings || findings.totalCalls === 0) continue;

        allFindings.push(findings);
        totalCalls += findings.totalCalls;

        const needingAwait = [
            ...findings.createElementCalls.filter(c => c.migration),
            ...findings.returnStatements.filter(r => r.migration),
        ];
        totalNeedingAwait += needingAwait.length;
        totalFunctionsNeedingAsync += findings.functionsToMakeAsync.length;
        totalAlreadyAsync += findings.alreadyAsync.length;

        // Print file summary
        if (needingAwait.length > 0 || findings.functionsToMakeAsync.length > 0) {
            console.log(`\n${"=".repeat(70)}`);
            console.log(`FILE: ${file}`);
            console.log(`${"=".repeat(70)}`);
            console.log(`Total createElement calls: ${findings.totalCalls}`);
            console.log(`Calls needing await: ${needingAwait.length}`);
            console.log(`Functions needing async: ${findings.functionsToMakeAsync.length}`);
            console.log(`Already async: ${findings.alreadyAsync.length}`);

            if (findings.functionsToMakeAsync.length > 0) {
                console.log("\nFunctions to make async:");
                for (const func of findings.functionsToMakeAsync) {
                    console.log(`  - ${func.name}() at line ${func.startLine}`);
                }
            }

            if (needingAwait.length > 0) {
                console.log("\nCalls needing await:");
                for (const call of needingAwait.slice(0, 5)) {
                    console.log(`  Line ${call.line}: ${call.original.substring(0, 60)}...`);
                }
                if (needingAwait.length > 5) {
                    console.log(`  ... and ${needingAwait.length - 5} more`);
                }
            }
        }
    }

    // Summary
    console.log("\n" + "=".repeat(70));
    console.log("SUMMARY");
    console.log("=".repeat(70));
    console.log(`Files scanned: ${files.length}`);
    console.log(`Files with createElement: ${allFindings.length}`);
    console.log(`Total createElement calls: ${totalCalls}`);
    console.log(`Calls needing await: ${totalNeedingAwait}`);
    console.log(`Functions needing async keyword: ${totalFunctionsNeedingAsync}`);
    console.log(`Functions already async: ${totalAlreadyAsync}`);
    console.log(`\nMigration complexity: ${totalNeedingAwait + totalFunctionsNeedingAsync} changes`);

    // Pattern breakdown
    const constAssignments = allFindings.reduce((sum, f) =>
        sum + f.createElementCalls.filter(c => c.pattern === "const_assignment" && c.migration).length, 0);
    const returnStmts = allFindings.reduce((sum, f) =>
        sum + f.returnStatements.filter(r => r.migration).length, 0);
    const inlineCalls = allFindings.reduce((sum, f) =>
        sum + f.createElementCalls.filter(c => c.pattern === "inline" && c.migration).length, 0);

    console.log("\nPattern breakdown:");
    console.log(`  const x = DOM.createElement: ${constAssignments} (auto-migrate)`);
    console.log(`  return DOM.createElement: ${returnStmts} (auto-migrate)`);
    console.log(`  inline/other: ${inlineCalls} (manual review)`);

    // Save report
    const report = {
        generatedAt: new Date().toISOString(),
        summary: {
            filesScanned: files.length,
            filesWithCreateElement: allFindings.length,
            totalCalls,
            callsNeedingAwait: totalNeedingAwait,
            functionsNeedingAsync: totalFunctionsNeedingAsync,
            alreadyAsync: totalAlreadyAsync,
            patterns: { constAssignments, returnStmts, inlineCalls },
        },
        findings: allFindings,
    };

    fs.writeFileSync(REPORT_PATH, JSON.stringify(report, null, 2));
    console.log(`\nDetailed report saved to: ${REPORT_PATH}`);

    // Apply if requested
    if (shouldApply) {
        console.log("\n" + "=".repeat(70));
        console.log("APPLYING MIGRATIONS");
        console.log("=".repeat(70));

        let totalChanges = 0;
        for (const findings of allFindings) {
            const changes = applyMigrations(findings);
            if (changes > 0) {
                console.log(`  ${findings.file}: ${changes} changes`);
                totalChanges += changes;
            }
        }

        console.log(`\nTotal changes applied: ${totalChanges}`);
        console.log("\nIMPORTANT: Run 'npm run verify-codebase' to check for any issues.");
        console.log("Some inline patterns may need manual review.");
    } else {
        console.log("\nTo apply migrations, run with --apply flag");
    }
}

main();
