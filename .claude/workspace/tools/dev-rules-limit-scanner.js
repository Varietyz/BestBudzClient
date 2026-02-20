/**
 * DEV-RULES Limit Scanner
 *
 * Scans for violations of DEV-RULES.md limits:
 * - maxLinesPerFile: 150 (default)
 * - maxFilesPerFolder: 6 (default)
 *
 * Usage: node dev-rules-limit-scanner.js [targetDir] [--lines=N] [--files=N]
 */

import { promises as fs } from "fs";
import path from "path";

const DEFAULTS = { maxLines: 150, maxFiles: 6 };
const EXCLUDE = ["node_modules", ".git", "dist", "build", ".registry", ".chain"];

async function getFiles(dir, ext, exclude) {
    const results = [];
    async function walk(d) {
        const entries = await fs.readdir(d, { withFileTypes: true });
        for (const e of entries) {
            const full = path.join(d, e.name);
            if (e.isDirectory() && !exclude.includes(e.name)) await walk(full);
            else if (e.isFile() && ext.some(x => e.name.endsWith(x))) results.push(full);
        }
    }
    await walk(dir);
    return results;
}

async function countLines(filePath) {
    const content = await fs.readFile(filePath, "utf-8");
    return content.split("\n").length;
}

async function getFolderFileCounts(dir, ext, exclude) {
    const counts = new Map();
    async function walk(d) {
        const entries = await fs.readdir(d, { withFileTypes: true });
        let fileCount = 0;
        for (const e of entries) {
            const full = path.join(d, e.name);
            if (e.isDirectory() && !exclude.includes(e.name)) await walk(full);
            else if (e.isFile() && ext.some(x => e.name.endsWith(x))) fileCount++;
        }
        if (fileCount > 0) counts.set(d, fileCount);
    }
    await walk(dir);
    return counts;
}

async function scan(targetDir, options = {}) {
    const { maxLines = DEFAULTS.maxLines, maxFiles = DEFAULTS.maxFiles, extensions = [".js", ".ts"] } = options;
    const rootDir = path.resolve(targetDir);

    // Scan line counts
    const files = await getFiles(rootDir, extensions, EXCLUDE);
    const lineViolations = [];

    for (const file of files) {
        const lines = await countLines(file);
        if (lines > maxLines) {
            lineViolations.push({
                path: path.relative(rootDir, file).replace(/\\/g, "/"),
                absolutePath: file,
                lines,
                limit: maxLines,
                excess: lines - maxLines,
                severity: lines > maxLines * 2 ? "critical" : lines > maxLines * 1.5 ? "high" : "medium"
            });
        }
    }

    // Scan folder file counts
    const folderCounts = await getFolderFileCounts(rootDir, extensions, EXCLUDE);
    const folderViolations = [];

    for (const [folder, count] of folderCounts) {
        if (count > maxFiles) {
            folderViolations.push({
                path: path.relative(rootDir, folder).replace(/\\/g, "/") || ".",
                absolutePath: folder,
                fileCount: count,
                limit: maxFiles,
                excess: count - maxFiles,
                severity: count > maxFiles * 2 ? "critical" : count > maxFiles * 1.5 ? "high" : "medium"
            });
        }
    }

    // Sort by severity then excess
    const severityOrder = { critical: 0, high: 1, medium: 2 };
    lineViolations.sort((a, b) => severityOrder[a.severity] - severityOrder[b.severity] || b.excess - a.excess);
    folderViolations.sort((a, b) => severityOrder[a.severity] - severityOrder[b.severity] || b.excess - a.excess);

    return {
        _meta: {
            scannedAt: new Date().toISOString(),
            targetDir: rootDir,
            limits: { maxLines, maxFiles },
            totalFilesScanned: files.length,
            totalFoldersScanned: folderCounts.size
        },
        lineViolations: {
            total: lineViolations.length,
            bySeverity: {
                critical: lineViolations.filter(v => v.severity === "critical").length,
                high: lineViolations.filter(v => v.severity === "high").length,
                medium: lineViolations.filter(v => v.severity === "medium").length
            },
            violations: lineViolations
        },
        folderViolations: {
            total: folderViolations.length,
            bySeverity: {
                critical: folderViolations.filter(v => v.severity === "critical").length,
                high: folderViolations.filter(v => v.severity === "high").length,
                medium: folderViolations.filter(v => v.severity === "medium").length
            },
            violations: folderViolations
        }
    };
}

// CLI execution
async function main() {
    const args = process.argv.slice(2);
    const targetDir = args.find(a => !a.startsWith("--")) || ".";
    const maxLines = parseInt(args.find(a => a.startsWith("--lines="))?.split("=")[1]) || DEFAULTS.maxLines;
    const maxFiles = parseInt(args.find(a => a.startsWith("--files="))?.split("=")[1]) || DEFAULTS.maxFiles;

    console.log(`\nScanning: ${path.resolve(targetDir)}`);
    console.log(`Limits: ${maxLines} lines/file, ${maxFiles} files/folder\n`);

    const report = await scan(targetDir, { maxLines, maxFiles });

    // Console summary
    console.log("LINE VIOLATIONS:", report.lineViolations.total);
    for (const v of report.lineViolations.violations.slice(0, 10)) {
        console.log(`  [${v.severity.toUpperCase()}] ${v.path}: ${v.lines} lines (+${v.excess})`);
    }
    if (report.lineViolations.total > 10) console.log(`  ... and ${report.lineViolations.total - 10} more`);

    console.log("\nFOLDER VIOLATIONS:", report.folderViolations.total);
    for (const v of report.folderViolations.violations.slice(0, 10)) {
        console.log(`  [${v.severity.toUpperCase()}] ${v.path}: ${v.fileCount} files (+${v.excess})`);
    }
    if (report.folderViolations.total > 10) console.log(`  ... and ${report.folderViolations.total - 10} more`);

    // Write JSON output
    const outputPath = path.join(targetDir, ".registry", "quality", "dev-rules-limits.json");
    await fs.mkdir(path.dirname(outputPath), { recursive: true });
    await fs.writeFile(outputPath, JSON.stringify(report, null, 2));
    console.log(`\nJSON report: ${outputPath}`);

    return report;
}

export { scan };
main().catch(console.error);
