/**
 * DOM.createElement Async Migration Script (AST-based)
 *
 * Uses TypeScript AST to properly transform DOM.createElement calls to async/await pattern.
 *
 * TRANSFORMATIONS:
 * 1. DOM.createElement(...) → await DOM.createElement(...)
 * 2. Containing functions/methods → async
 * 3. Arrow functions → async arrow functions
 *
 * Usage:
 *   npx ts-node migrate-dom-async-ast.ts --analyze
 *   npx ts-node migrate-dom-async-ast.ts --apply
 *   npx ts-node migrate-dom-async-ast.ts --file=<pattern>
 *
 * @module tools/migrate-dom-async-ast
 */

import * as ts from "typescript";
import * as fs from "fs";
import * as path from "path";

const ROOT = path.resolve(__dirname, "../../../root/archlab-ide/src/renderer");
const REPORT_PATH = path.resolve(__dirname, "async-migration-ast-report.json");

// =============================================================================
// Types
// =============================================================================

interface CreateElementCall {
    line: number;
    column: number;
    text: string;
    containingFunction: string;
    functionLine: number;
    needsAsyncFunction: boolean;
}

interface FileAnalysis {
    file: string;
    calls: CreateElementCall[];
    functionsToMakeAsync: Set<string>;
    totalCalls: number;
}

interface TransformResult {
    transformed: boolean;
    newContent: string;
    changesMade: number;
}

// =============================================================================
// AST Analysis
// =============================================================================

/**
 * Find all DOM.createElement calls and their containing functions
 */
function analyzeFile(filePath: string): FileAnalysis | null {
    const fullPath = path.join(ROOT, filePath);
    if (!fs.existsSync(fullPath)) {
        return null;
    }

    const sourceText = fs.readFileSync(fullPath, "utf-8");
    const sourceFile = ts.createSourceFile(
        fullPath,
        sourceText,
        ts.ScriptTarget.Latest,
        true,
        ts.ScriptKind.TS
    );

    const calls: CreateElementCall[] = [];
    const functionsToMakeAsync = new Set<string>();

    /**
     * Check if node is DOM.createElement call
     */
    function isDOMCreateElement(node: ts.Node): boolean {
        if (!ts.isCallExpression(node)) return false;

        const expr = node.expression;
        if (!ts.isPropertyAccessExpression(expr)) return false;

        return (
            ts.isIdentifier(expr.expression) &&
            expr.expression.text === "DOM" &&
            ts.isIdentifier(expr.name) &&
            expr.name.text === "createElement"
        );
    }

    /**
     * Find containing function/method
     */
    function findContainingFunction(node: ts.Node): { name: string; line: number; isAsync: boolean; kind: string } | null {
        let current: ts.Node | undefined = node.parent;

        while (current) {
            // Arrow function
            if (ts.isArrowFunction(current)) {
                const parent = current.parent;
                let name = "anonymous";
                let line = sourceFile.getLineAndCharacterOfPosition(current.getStart()).line + 1;

                // Check if it's a variable declaration
                if (ts.isVariableDeclaration(parent) && ts.isIdentifier(parent.name)) {
                    name = parent.name.text;
                }
                // Check if it's a property assignment
                else if (ts.isPropertyAssignment(parent) && ts.isIdentifier(parent.name)) {
                    name = parent.name.text;
                }

                const isAsync = !!(current.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword));
                return { name, line, isAsync, kind: "arrow" };
            }

            // Function declaration
            if (ts.isFunctionDeclaration(current)) {
                const name = current.name?.text || "anonymous";
                const line = sourceFile.getLineAndCharacterOfPosition(current.getStart()).line + 1;
                const isAsync = !!(current.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword));
                return { name, line, isAsync, kind: "function" };
            }

            // Method declaration
            if (ts.isMethodDeclaration(current)) {
                const name = ts.isIdentifier(current.name) ? current.name.text : "method";
                const line = sourceFile.getLineAndCharacterOfPosition(current.getStart()).line + 1;
                const isAsync = !!(current.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword));
                return { name, line, isAsync, kind: "method" };
            }

            // Constructor
            if (ts.isConstructorDeclaration(current)) {
                const line = sourceFile.getLineAndCharacterOfPosition(current.getStart()).line + 1;
                // Constructors cannot be async
                return { name: "constructor", line, isAsync: false, kind: "constructor" };
            }

            // Function expression
            if (ts.isFunctionExpression(current)) {
                const name = current.name?.text || "anonymous";
                const line = sourceFile.getLineAndCharacterOfPosition(current.getStart()).line + 1;
                const isAsync = !!(current.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword));
                return { name, line, isAsync, kind: "function-expr" };
            }

            current = current.parent;
        }

        return null;
    }

    /**
     * Check if call is already awaited
     */
    function isAwaited(node: ts.CallExpression): boolean {
        const parent = node.parent;
        return ts.isAwaitExpression(parent);
    }

    /**
     * Visit all nodes
     */
    function visit(node: ts.Node): void {
        if (isDOMCreateElement(node) && ts.isCallExpression(node)) {
            const { line, character } = sourceFile.getLineAndCharacterOfPosition(node.getStart());
            const containingFn = findContainingFunction(node);
            const awaited = isAwaited(node);

            const callInfo: CreateElementCall = {
                line: line + 1,
                column: character,
                text: node.getText().substring(0, 60),
                containingFunction: containingFn?.name || "top-level",
                functionLine: containingFn?.line || 0,
                needsAsyncFunction: containingFn !== null && !containingFn.isAsync && containingFn.kind !== "constructor",
            };

            calls.push(callInfo);

            if (!awaited && containingFn && !containingFn.isAsync && containingFn.kind !== "constructor") {
                functionsToMakeAsync.add(`${containingFn.name}:${containingFn.line}`);
            }
        }

        ts.forEachChild(node, visit);
    }

    visit(sourceFile);

    return {
        file: filePath,
        calls,
        functionsToMakeAsync,
        totalCalls: calls.length,
    };
}

// =============================================================================
// AST Transformation
// =============================================================================

/**
 * Transform a file to add await and async keywords
 */
function transformFile(filePath: string): TransformResult {
    const fullPath = path.join(ROOT, filePath);
    const sourceText = fs.readFileSync(fullPath, "utf-8");
    const sourceFile = ts.createSourceFile(
        fullPath,
        sourceText,
        ts.ScriptTarget.Latest,
        true,
        ts.ScriptKind.TS
    );

    let changesMade = 0;

    // Create transformer factory
    const transformer: ts.TransformerFactory<ts.SourceFile> = (context) => {
        const visit: ts.Visitor = (node) => {
            // Transform DOM.createElement calls to await DOM.createElement
            if (ts.isCallExpression(node)) {
                const expr = node.expression;
                if (
                    ts.isPropertyAccessExpression(expr) &&
                    ts.isIdentifier(expr.expression) &&
                    expr.expression.text === "DOM" &&
                    ts.isIdentifier(expr.name) &&
                    expr.name.text === "createElement"
                ) {
                    // Check if already awaited
                    if (!ts.isAwaitExpression(node.parent)) {
                        changesMade++;
                        return context.factory.createAwaitExpression(
                            ts.visitEachChild(node, visit, context)
                        );
                    }
                }
            }

            // Transform method declarations to async
            if (ts.isMethodDeclaration(node) && node.name) {
                const hasCreateElement = containsCreateElement(node);
                const isAsync = node.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword);

                if (hasCreateElement && !isAsync) {
                    changesMade++;
                    // Place async AFTER access modifiers (private, public, protected) but BEFORE others
                    const modifiers = [
                        ...(node.modifiers || []),
                        context.factory.createModifier(ts.SyntaxKind.AsyncKeyword),
                    ];

                    // Wrap return type in Promise<T>
                    let returnType = node.type;
                    if (returnType) {
                        returnType = context.factory.createTypeReferenceNode(
                            context.factory.createIdentifier("Promise"),
                            [returnType]
                        );
                    }

                    return context.factory.updateMethodDeclaration(
                        node,
                        modifiers,
                        node.asteriskToken,
                        node.name,
                        node.questionToken,
                        node.typeParameters,
                        node.parameters,
                        returnType,
                        ts.visitEachChild(node.body, visit, context) as ts.Block | undefined
                    );
                }
            }

            // Transform arrow functions to async
            if (ts.isArrowFunction(node)) {
                const hasCreateElement = containsCreateElement(node);
                const isAsync = node.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword);

                if (hasCreateElement && !isAsync) {
                    changesMade++;
                    const modifiers = [
                        ...(node.modifiers || []),
                        context.factory.createModifier(ts.SyntaxKind.AsyncKeyword),
                    ];

                    // Wrap return type in Promise<T>
                    let returnType = node.type;
                    if (returnType) {
                        returnType = context.factory.createTypeReferenceNode(
                            context.factory.createIdentifier("Promise"),
                            [returnType]
                        );
                    }

                    return context.factory.updateArrowFunction(
                        node,
                        modifiers,
                        node.typeParameters,
                        node.parameters,
                        returnType,
                        node.equalsGreaterThanToken,
                        ts.visitEachChild(node.body, visit, context) as ts.ConciseBody
                    );
                }
            }

            // Transform function declarations to async
            if (ts.isFunctionDeclaration(node)) {
                const hasCreateElement = containsCreateElement(node);
                const isAsync = node.modifiers?.some(m => m.kind === ts.SyntaxKind.AsyncKeyword);

                if (hasCreateElement && !isAsync) {
                    changesMade++;
                    const modifiers = [
                        ...(node.modifiers || []),
                        context.factory.createModifier(ts.SyntaxKind.AsyncKeyword),
                    ];

                    // Wrap return type in Promise<T>
                    let returnType = node.type;
                    if (returnType) {
                        returnType = context.factory.createTypeReferenceNode(
                            context.factory.createIdentifier("Promise"),
                            [returnType]
                        );
                    }

                    return context.factory.updateFunctionDeclaration(
                        node,
                        modifiers,
                        node.asteriskToken,
                        node.name,
                        node.typeParameters,
                        node.parameters,
                        returnType,
                        ts.visitEachChild(node.body, visit, context) as ts.Block | undefined
                    );
                }
            }

            return ts.visitEachChild(node, visit, context);
        };

        return (sf) => ts.visitNode(sf, visit) as ts.SourceFile;
    };

    // Apply transformation
    const result = ts.transform(sourceFile, [transformer]);
    const printer = ts.createPrinter({ newLine: ts.NewLineKind.LineFeed });
    const newContent = printer.printFile(result.transformed[0]);
    result.dispose();

    return {
        transformed: changesMade > 0,
        newContent,
        changesMade,
    };
}

/**
 * Check if a node contains DOM.createElement calls
 */
function containsCreateElement(node: ts.Node): boolean {
    let found = false;

    function visit(n: ts.Node): void {
        if (found) return;

        if (ts.isCallExpression(n)) {
            const expr = n.expression;
            if (
                ts.isPropertyAccessExpression(expr) &&
                ts.isIdentifier(expr.expression) &&
                expr.expression.text === "DOM" &&
                ts.isIdentifier(expr.name) &&
                expr.name.text === "createElement"
            ) {
                found = true;
                return;
            }
        }

        ts.forEachChild(n, visit);
    }

    visit(node);
    return found;
}

// =============================================================================
// File Discovery
// =============================================================================

function findTsFiles(dir: string, files: string[] = []): string[] {
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

async function main(): Promise<void> {
    const args = process.argv.slice(2);
    const shouldApply = args.includes("--apply");
    const filePattern = args.find(a => a.startsWith("--file="))?.replace("--file=", "") || null;

    console.log("DOM.createElement Async Migration Tool (AST-based)");
    console.log("=".repeat(55));
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

    const allAnalyses: FileAnalysis[] = [];
    let totalCalls = 0;
    let totalFunctionsNeedingAsync = 0;

    for (const file of files) {
        const analysis = analyzeFile(file);
        if (!analysis || analysis.totalCalls === 0) continue;

        allAnalyses.push(analysis);
        totalCalls += analysis.totalCalls;
        totalFunctionsNeedingAsync += analysis.functionsToMakeAsync.size;

        // Print file summary
        if (analysis.totalCalls > 0) {
            console.log(`\n${"=".repeat(70)}`);
            console.log(`FILE: ${file}`);
            console.log(`${"=".repeat(70)}`);
            console.log(`createElement calls: ${analysis.totalCalls}`);
            console.log(`Functions needing async: ${analysis.functionsToMakeAsync.size}`);

            if (analysis.functionsToMakeAsync.size > 0) {
                console.log("\nFunctions to make async:");
                for (const fn of analysis.functionsToMakeAsync) {
                    const [name, line] = fn.split(":");
                    console.log(`  - ${name}() at line ${line}`);
                }
            }

            console.log("\nCalls:");
            for (const call of analysis.calls.slice(0, 5)) {
                console.log(`  Line ${call.line}: ${call.text}...`);
            }
            if (analysis.calls.length > 5) {
                console.log(`  ... and ${analysis.calls.length - 5} more`);
            }
        }
    }

    // Summary
    console.log("\n" + "=".repeat(70));
    console.log("SUMMARY");
    console.log("=".repeat(70));
    console.log(`Files scanned: ${files.length}`);
    console.log(`Files with createElement: ${allAnalyses.length}`);
    console.log(`Total createElement calls: ${totalCalls}`);
    console.log(`Functions needing async: ${totalFunctionsNeedingAsync}`);

    // Save report
    const report = {
        generatedAt: new Date().toISOString(),
        summary: {
            filesScanned: files.length,
            filesWithCreateElement: allAnalyses.length,
            totalCalls,
            functionsNeedingAsync: totalFunctionsNeedingAsync,
        },
        analyses: allAnalyses.map(a => ({
            ...a,
            functionsToMakeAsync: Array.from(a.functionsToMakeAsync),
        })),
    };

    fs.writeFileSync(REPORT_PATH, JSON.stringify(report, null, 2));
    console.log(`\nDetailed report saved to: ${REPORT_PATH}`);

    // Apply if requested
    if (shouldApply) {
        console.log("\n" + "=".repeat(70));
        console.log("APPLYING TRANSFORMATIONS");
        console.log("=".repeat(70));

        let totalChanges = 0;
        for (const analysis of allAnalyses) {
            const fullPath = path.join(ROOT, analysis.file);
            try {
                const result = transformFile(analysis.file);
                if (result.transformed) {
                    fs.writeFileSync(fullPath, result.newContent);
                    console.log(`  ${analysis.file}: ${result.changesMade} changes`);
                    totalChanges += result.changesMade;
                }
            } catch (err) {
                console.error(`  ERROR in ${analysis.file}: ${err}`);
            }
        }

        console.log(`\nTotal changes applied: ${totalChanges}`);
        console.log("\nIMPORTANT: Run 'npm run verify-codebase' to check for issues.");
        console.log("Note: Constructors cannot be async - manual refactoring needed.");
    } else {
        console.log("\nTo apply transformations, run with --apply flag");
    }
}

main().catch(console.error);
