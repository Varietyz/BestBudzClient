#!/usr/bin/env node

/**
 * AST-Based Logging Violation Detection Script
 *
 * Uses proper AST parsing for accurate detection of:
 * - Functions without Engine.logger calls
 * - Empty catch blocks
 * - Silent passes (functions that should log but don't)
 * - console.* usage with context
 * - Aggregated count of Engine.logger calls
 *
 * Generates manifest.json with saturated/unsaturated files
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// =============================================================================
// AST Parser Setup
// =============================================================================

let parser;
let traverse;

try {
    // Try @babel/parser first (best for TypeScript)
    parser = require('@babel/parser');
    traverse = require('@babel/traverse').default;
} catch (e) {
    try {
        // Fallback to acorn + acorn-walk
        const acorn = require('acorn');
        const walk = require('acorn-walk');
        parser = {
            parse: (code) => acorn.parse(code, {
                ecmaVersion: 2020,
                sourceType: 'module',
                locations: true,
            })
        };
        traverse = (ast, visitors) => {
            walk.full(ast, (node, state, type) => {
                if (visitors[type]) {
                    visitors[type](node);
                }
            });
        };
    } catch (e2) {
        console.error('❌ No AST parser available. Install @babel/parser or acorn:');
        console.error('   npm install @babel/parser @babel/traverse');
        console.error('   OR');
        console.error('   npm install acorn acorn-walk');
        process.exit(1);
    }
}

// =============================================================================
// Configuration
// =============================================================================

const ROOT = path.join(__dirname, '../../..');
const MANIFEST_PATH = path.join(__dirname, '../logging-migration-manifest.json');

const SCAN_PATTERNS = [
    'root/archlab-ide/src/**/*.ts',
    'root/archlab-ide/src/**/*.js',
    '!**/*.d.ts',
    '!**/node_modules/**',
    '!**/dist/**',
    '!**/build/**',
];

// Minimum function complexity requiring logging (lines)
const COMPLEXITY_THRESHOLD = 10;

// =============================================================================
// AST-Based Analysis Functions
// =============================================================================

/**
 * Parse file to AST
 */
function parseToAST(content, filePath) {
    try {
        // Determine parser based on file extension
        const isTypeScript = filePath.endsWith('.ts') || filePath.endsWith('.tsx');

        if (parser.parse) {
            // Babel parser
            return parser.parse(content, {
                sourceType: 'module',
                plugins: [
                    'typescript',
                    'jsx',
                    'classProperties',
                    'decorators-legacy',
                    'asyncGenerators',
                    'objectRestSpread',
                    'optionalChaining',
                    'nullishCoalescingOperator',
                ],
                errorRecovery: true,
            });
        } else {
            // Acorn parser
            return parser.parse(content);
        }
    } catch (error) {
        // If parsing fails, return null (will be handled gracefully)
        return null;
    }
}

/**
 * Extract function information from AST
 */
function extractFunctions(ast, lines) {
    const functions = [];

    if (!ast) return functions;

    const visitors = {
        FunctionDeclaration(path) {
            const node = path.node || path;
            if (node.id) {
                functions.push({
                    name: node.id.name,
                    line: node.loc ? node.loc.start.line : 0,
                    startLine: node.loc ? node.loc.start.line : 0,
                    endLine: node.loc ? node.loc.end.line : 0,
                    bodyLines: node.loc ? (node.loc.end.line - node.loc.start.line) : 0,
                    node: node,
                    type: 'function',
                    async: node.async || false,
                });
            }
        },
        FunctionExpression(path) {
            const node = path.node || path;
            const name = node.id ? node.id.name : 'anonymous';
            functions.push({
                name,
                line: node.loc ? node.loc.start.line : 0,
                startLine: node.loc ? node.loc.start.line : 0,
                endLine: node.loc ? node.loc.end.line : 0,
                bodyLines: node.loc ? (node.loc.end.line - node.loc.start.line) : 0,
                node: node,
                type: 'function-expression',
                async: node.async || false,
            });
        },
        ArrowFunctionExpression(path) {
            const node = path.node || path;
            functions.push({
                name: 'arrow',
                line: node.loc ? node.loc.start.line : 0,
                startLine: node.loc ? node.loc.start.line : 0,
                endLine: node.loc ? node.loc.end.line : 0,
                bodyLines: node.loc ? (node.loc.end.line - node.loc.start.line) : 0,
                node: node,
                type: 'arrow',
                async: node.async || false,
            });
        },
        ClassMethod(path) {
            const node = path.node || path;
            if (node.key) {
                const name = node.key.name || node.key.value || 'method';
                functions.push({
                    name,
                    line: node.loc ? node.loc.start.line : 0,
                    startLine: node.loc ? node.loc.start.line : 0,
                    endLine: node.loc ? node.loc.end.line : 0,
                    bodyLines: node.loc ? (node.loc.end.line - node.loc.start.line) : 0,
                    node: node,
                    type: 'class-method',
                    async: node.async || false,
                });
            }
        },
        ObjectMethod(path) {
            const node = path.node || path;
            if (node.key) {
                const name = node.key.name || node.key.value || 'method';
                functions.push({
                    name,
                    line: node.loc ? node.loc.start.line : 0,
                    startLine: node.loc ? node.loc.start.line : 0,
                    endLine: node.loc ? node.loc.end.line : 0,
                    bodyLines: node.loc ? (node.loc.end.line - node.loc.start.line) : 0,
                    node: node,
                    type: 'object-method',
                    async: node.async || false,
                });
            }
        },
    };

    try {
        traverse(ast, visitors);
    } catch (error) {
        // Ignore traversal errors
    }

    return functions;
}

/**
 * Find empty catch blocks in AST
 */
function findEmptyCatches(ast) {
    const emptyCatches = [];

    if (!ast) return emptyCatches;

    const visitors = {
        CatchClause(path) {
            const node = path.node || path;
            const body = node.body;

            // Check if catch body is empty or only has whitespace/comments
            if (body && body.type === 'BlockStatement') {
                if (body.body.length === 0) {
                    emptyCatches.push({
                        line: node.loc ? node.loc.start.line : 0,
                        param: node.param ? (node.param.name || 'unknown') : 'none',
                    });
                }
            }
        },
    };

    try {
        traverse(ast, visitors);
    } catch (error) {
        // Ignore traversal errors
    }

    return emptyCatches;
}

/**
 * Find console.* calls in AST
 */
function findConsoleCalls(ast) {
    const consoleCalls = [];

    if (!ast) return consoleCalls;

    const visitors = {
        CallExpression(path) {
            const node = path.node || path;
            if (
                node.callee &&
                node.callee.type === 'MemberExpression' &&
                node.callee.object &&
                node.callee.object.name === 'console' &&
                node.callee.property
            ) {
                const method = node.callee.property.name || node.callee.property.value;
                consoleCalls.push({
                    line: node.loc ? node.loc.start.line : 0,
                    method,
                });
            }
        },
    };

    try {
        traverse(ast, visitors);
    } catch (error) {
        // Ignore traversal errors
    }

    return consoleCalls;
}

/**
 * Count Engine.logger calls in AST
 */
function countEngineLoggerCalls(ast) {
    let count = 0;

    if (!ast) return count;

    const visitors = {
        CallExpression(path) {
            const node = path.node || path;
            if (
                node.callee &&
                node.callee.type === 'MemberExpression' &&
                node.callee.object &&
                node.callee.object.name === 'Engine' &&
                node.callee.property &&
                node.callee.property.name === 'logger'
            ) {
                count++;
            }
        },
    };

    try {
        traverse(ast, visitors);
    } catch (error) {
        // Ignore traversal errors
    }

    return count;
}

/**
 * Classify function by architectural domain
 * Based on .claude/_DEV-RULES.md COMPUTATION/RESOURCE/EXECUTION domains
 */
function classifyFunctionDomain(funcName, funcBody) {
    const bodyCode = funcBody || '';
    const name = funcName || '';

    // RESOURCE domain (File I/O, DB, Network, Lifecycle)
    if (bodyCode.includes('fs.') || bodyCode.includes('readFile') || bodyCode.includes('writeFile')) {
        return { domain: 'RESOURCE', reason: 'File I/O detected' };
    }
    if (bodyCode.includes('db.') || bodyCode.includes('query(') || bodyCode.includes('execute(')) {
        return { domain: 'RESOURCE', reason: 'Database operation' };
    }
    if (bodyCode.includes('fetch(') || bodyCode.includes('axios') || bodyCode.includes('http.')) {
        return { domain: 'RESOURCE', reason: 'Network I/O' };
    }
    if (name.match(/(init|start|stop|shutdown|destroy|acquire|release)/i)) {
        return { domain: 'RESOURCE', reason: 'Lifecycle method' };
    }

    // EXECUTION domain (Events, IPC, Intents, Error handling)
    if (bodyCode.includes('addEventListener') || bodyCode.includes('.on(') || bodyCode.includes('once(')) {
        return { domain: 'EXECUTION', reason: 'Event handler' };
    }
    if (bodyCode.includes('ipcRenderer') || bodyCode.includes('ipcMain') || bodyCode.includes('window.api')) {
        return { domain: 'EXECUTION', reason: 'IPC communication' };
    }
    if (bodyCode.includes('.emit(') || bodyCode.includes('Intent')) {
        return { domain: 'EXECUTION', reason: 'Intent flow' };
    }

    // COMPUTATION domain (Pure functions, type guards, formatters)
    // Type guards and predicates
    if (name.match(/^(is|has|should|can)/i)) {
        if (!bodyCode.includes('await') && !bodyCode.includes('fs.') && !bodyCode.includes('db.')) {
            return { domain: 'COMPUTATION', reason: 'Pure predicate/type guard' };
        }
    }

    // Path/data transformers
    if (name.match(/^(to|from).*(Path|String|Number|Date|JSON)/i)) {
        return { domain: 'COMPUTATION', reason: 'Pure data transformer' };
    }
    if (name.match(/(format|parse|normalize|sanitize|convert)/i)) {
        if (!bodyCode.includes('await') && !bodyCode.includes('fs.')) {
            return { domain: 'COMPUTATION', reason: 'Pure formatter' };
        }
    }

    // Simple getters (1-2 line return)
    const lines = bodyCode.split('\n').filter(line => line.trim().length > 0);
    if (lines.length <= 2 && bodyCode.trim().startsWith('return')) {
        return { domain: 'COMPUTATION', reason: 'Simple getter' };
    }

    // Default: unknown (analyze by complexity)
    return { domain: 'UNKNOWN', reason: 'Requires complexity analysis' };
}

/**
 * Analyze function context to detect if it should have logging
 * Uses architectural domain classification
 */
function shouldFunctionLog(funcNode, funcInfo) {
    const funcName = funcInfo.name;
    const funcBody = funcInfo.body || '';

    // Classify by architectural domain
    const classification = classifyFunctionDomain(funcName, funcBody);

    // COMPUTATION domain (pure utilities) - SKIP
    if (classification.domain === 'COMPUTATION') {
        return { should: false, reason: `SKIP: ${classification.reason}` };
    }

    // RESOURCE/EXECUTION domain - REQUIRE logging
    if (classification.domain === 'RESOURCE' || classification.domain === 'EXECUTION') {
        return { should: true, reason: `${classification.domain}: ${classification.reason}` };
    }

    // UNKNOWN domain - use complexity heuristics

    // Complex functions should log
    if (funcInfo.bodyLines >= COMPLEXITY_THRESHOLD) {
        return { should: true, reason: `Complex (${funcInfo.bodyLines} lines)` };
    }

    // Async functions should log (likely I/O)
    if (funcInfo.async) {
        return { should: true, reason: 'Async function (likely I/O)' };
    }

    // Functions with try/catch should log
    let hasTryCatch = false;
    let hasThrow = false;

    const checkVisitors = {
        TryStatement() {
            hasTryCatch = true;
        },
        ThrowStatement() {
            hasThrow = true;
        },
    };

    try {
        traverse(funcNode, checkVisitors);
    } catch (error) {
        // Ignore traversal errors
    }

    if (hasTryCatch) {
        return { should: true, reason: 'Has try/catch (error handling)' };
    }

    if (hasThrow) {
        return { should: true, reason: 'Throws error' };
    }

    // Default: simple function, skip logging
    return { should: false, reason: 'Simple utility (<10 lines, no I/O)' };
}

/**
 * Check if function has Engine.logger call
 */
function hasEngineLogger(funcNode) {
    let hasLogger = false;

    const visitors = {
        CallExpression(path) {
            const node = path.node || path;
            if (
                node.callee &&
                node.callee.type === 'MemberExpression' &&
                node.callee.object &&
                node.callee.object.name === 'Engine' &&
                node.callee.property &&
                node.callee.property.name === 'logger'
            ) {
                hasLogger = true;
            }
        },
    };

    try {
        traverse(funcNode, visitors);
    } catch (error) {
        // Ignore traversal errors
    }

    return hasLogger;
}

/**
 * Analyze a single file for logging violations (AST-based)
 */
function analyzeFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const lines = content.split('\n');

    const violations = {
        file: path.relative(ROOT, filePath),
        emptyCatches: [],
        consoleLogs: [],
        functionsWithoutLogging: [],
        engineLoggerCalls: 0,
        totalFunctions: 0,
        score: 0,
    };

    // Parse to AST
    const ast = parseToAST(content, filePath);

    if (!ast) {
        // Parsing failed - skip this file
        return violations;
    }

    // Count Engine.logger calls
    violations.engineLoggerCalls = countEngineLoggerCalls(ast);

    // Find console.* usage
    violations.consoleLogs = findConsoleCalls(ast);

    // Find empty catch blocks
    violations.emptyCatches = findEmptyCatches(ast);

    // Extract functions
    const functions = extractFunctions(ast, lines);
    violations.totalFunctions = functions.length;

    // Analyze each function
    functions.forEach(fn => {
        const shouldLog = shouldFunctionLog(fn.node, fn);

        if (shouldLog.should) {
            const hasLogger = hasEngineLogger(fn.node);

            if (!hasLogger) {
                violations.functionsWithoutLogging.push({
                    name: fn.name,
                    line: fn.line,
                    bodyLines: fn.bodyLines,
                    reason: shouldLog.reason,
                    async: fn.async,
                });
            }
        }
    });

    // Calculate saturation score
    const totalIssues =
        violations.emptyCatches.length +
        violations.consoleLogs.length +
        violations.functionsWithoutLogging.length;

    const expectedLogs = violations.totalFunctions > 0 ? violations.totalFunctions : 1;
    violations.score = Math.round(
        (violations.engineLoggerCalls / (violations.engineLoggerCalls + totalIssues)) * 100
    );

    // Cap score at 100
    violations.score = Math.min(violations.score, 100);
    violations.score = Math.max(violations.score, 0);

    return violations;
}

/**
 * Scan all files matching patterns
 */
function scanFiles() {
    console.log('🔍 Scanning for logging violations (AST-based)...\n');

    const files = [];

    // Use glob to find files
    SCAN_PATTERNS.forEach(pattern => {
        if (pattern.startsWith('!')) return;

        try {
            const globPath = path.join(ROOT, pattern);
            const command = `npx glob "${globPath.replace(/\\/g, '/')}" --ignore "**/node_modules/**" --ignore "**/dist/**"`;
            const output = execSync(command, { encoding: 'utf-8', cwd: ROOT });
            const foundFiles = output
                .trim()
                .split('\n')
                .filter(Boolean)
                .map(f => f.trim());
            files.push(...foundFiles);
        } catch (error) {
            // Ignore glob errors
        }
    });

    console.log(`📂 Found ${files.length} files to analyze\n`);

    const results = {
        timestamp: new Date().toISOString(),
        totalFiles: files.length,
        saturated: [],
        unsaturated: [],
        summary: {
            totalEmptyCatches: 0,
            totalConsoleLogs: 0,
            totalFunctionsWithoutLogging: 0,
            totalEngineLoggerCalls: 0,
            totalFunctions: 0,
            averageScore: 0,
        },
    };

    files.forEach((file, idx) => {
        process.stdout.write(`\r⚙️  Analyzing: ${idx + 1}/${files.length} files...`);

        try {
            const violations = analyzeFile(file);

            // Update summary
            results.summary.totalEmptyCatches += violations.emptyCatches.length;
            results.summary.totalConsoleLogs += violations.consoleLogs.length;
            results.summary.totalFunctionsWithoutLogging += violations.functionsWithoutLogging.length;
            results.summary.totalEngineLoggerCalls += violations.engineLoggerCalls;
            results.summary.totalFunctions += violations.totalFunctions;

            // Categorize by saturation (>= 80% = saturated)
            if (violations.score >= 80) {
                results.saturated.push(violations);
            } else {
                results.unsaturated.push(violations);
            }
        } catch (error) {
            console.error(`\n❌ Error analyzing ${file}:`, error.message);
        }
    });

    console.log('\n');

    // Calculate average score
    results.summary.averageScore = Math.round(
        results.saturated.length + results.unsaturated.length > 0
            ? (results.saturated.length / (results.saturated.length + results.unsaturated.length)) * 100
            : 0
    );

    return results;
}

/**
 * Generate manifest file
 */
function generateManifest(results) {
    // Sort unsaturated by score (lowest first - needs most work)
    results.unsaturated.sort((a, b) => a.score - b.score);

    // Write manifest
    fs.writeFileSync(MANIFEST_PATH, JSON.stringify(results, null, 2));
    console.log(`📋 Manifest written to: ${path.relative(ROOT, MANIFEST_PATH)}`);
}

/**
 * Print summary report
 */
function printSummary(results) {
    console.log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('📊 LOGGING MIGRATION SUMMARY (AST-BASED)');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n');

    console.log(`Total Files Analyzed: ${results.totalFiles}`);
    console.log(`✅ Saturated (≥80%):   ${results.saturated.length} files`);
    console.log(`⚠️  Unsaturated (<80%): ${results.unsaturated.length} files`);
    console.log(`📈 Average Score:      ${results.summary.averageScore}%\n`);

    console.log('Violation Breakdown:');
    console.log(`  Empty catch blocks:    ${results.summary.totalEmptyCatches}`);
    console.log(`  console.* calls:       ${results.summary.totalConsoleLogs}`);
    console.log(`  Functions w/o logging: ${results.summary.totalFunctionsWithoutLogging}\n`);

    console.log('Logging Stats:');
    console.log(`  Engine.logger calls:   ${results.summary.totalEngineLoggerCalls}`);
    console.log(`  Total functions:       ${results.summary.totalFunctions}`);
    const coverage =
        results.summary.totalFunctions > 0
            ? Math.round((results.summary.totalEngineLoggerCalls / results.summary.totalFunctions) * 100)
            : 0;
    console.log(`  Coverage:              ${coverage}%\n`);

    // Top 10 files needing attention
    console.log('🔥 TOP 10 FILES NEEDING ATTENTION:\n');
    results.unsaturated.slice(0, 10).forEach((file, idx) => {
        console.log(`${idx + 1}. ${file.file} (Score: ${file.score}%)`);
        console.log(`   - Empty catches: ${file.emptyCatches.length}`);
        console.log(`   - console.*: ${file.consoleLogs.length}`);
        console.log(`   - Functions w/o logging: ${file.functionsWithoutLogging.length}`);
        console.log(`   - Engine.logger calls: ${file.engineLoggerCalls}\n`);
    });

    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n');
}

// =============================================================================
// Main Execution
// =============================================================================

if (require.main === module) {
    try {
        const results = scanFiles();
        generateManifest(results);
        printSummary(results);

        console.log(`✅ AST-based detection complete! Manifest saved to:`);
        console.log(`   ${path.relative(ROOT, MANIFEST_PATH)}\n`);

        process.exit(0);
    } catch (error) {
        console.error('\n❌ Fatal error:', error);
        process.exit(1);
    }
}

module.exports = { analyzeFile, scanFiles, generateManifest };
