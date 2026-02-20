const fs = require('fs');
const path = require('path');

const srcDir = 'D:\\GIT\\archlab\\root\\archlab-ide\\src';
const results = {
  fileLineCounts: [],
  oversizedFiles: [],
  importAnalysis: [],
  classAnalysis: []
};

function countLines(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');
  return content.split('\n').length;
}

function analyzeImports(filePath, content) {
  const imports = [];
  const parentImportPattern = /from\s+['"]\.\.\//g;
  const matches = content.matchAll(parentImportPattern);

  for (const match of matches) {
    imports.push({
      file: filePath,
      type: 'parent-import',
      line: content.substring(0, match.index).split('\n').length
    });
  }

  return imports;
}

function analyzeClasses(filePath, content) {
  const classes = [];
  const classPattern = /class\s+(\w+)(?:\s+extends\s+(\w+))?/g;
  const matches = content.matchAll(classPattern);

  for (const match of matches) {
    classes.push({
      file: filePath,
      className: match[1],
      extends: match[2] || null
    });
  }

  return classes;
}

function walkDirectory(dir) {
  const files = fs.readdirSync(dir);

  for (const file of files) {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);

    if (stat.isDirectory()) {
      walkDirectory(filePath);
    } else if (file.endsWith('.ts') || file.endsWith('.js')) {
      const lineCount = countLines(filePath);
      const relativePath = path.relative('D:\\GIT\\archlab\\root\\archlab-ide', filePath);

      results.fileLineCounts.push({
        path: relativePath,
        lines: lineCount
      });

      if (lineCount > 150) {
        results.oversizedFiles.push({
          path: relativePath,
          lines: lineCount,
          excess: lineCount - 150
        });
      }

      const content = fs.readFileSync(filePath, 'utf-8');
      const imports = analyzeImports(filePath, content);
      const classes = analyzeClasses(filePath, content);

      if (imports.length > 0) {
        results.importAnalysis.push(...imports);
      }

      if (classes.length > 0) {
        results.classAnalysis.push(...classes);
      }
    }
  }
}

walkDirectory(srcDir);

console.log(JSON.stringify(results, null, 2));
