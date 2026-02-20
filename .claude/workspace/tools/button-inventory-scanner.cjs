/**
 * Button Inventory Scanner
 * Forensically traces all button implementations in archlab-ide
 */

const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, '../../../root/archlab-ide/src');
const outputPath = path.join(__dirname, '../../../root/archlab-ide/button-inventory.md');

const results = {
  htmlButtons: [],
  createElementButtons: [],
  cssClasses: [],
  clickHandlers: [],
  inlineStyles: [],
  components: []
};

function walkDir(dir, callback) {
  const files = fs.readdirSync(dir);
  files.forEach(file => {
    const filepath = path.join(dir, file);
    const stat = fs.statSync(filepath);
    if (stat.isDirectory()) {
      walkDir(filepath, callback);
    } else {
      callback(filepath);
    }
  });
}

function analyzeFile(filepath) {
  const ext = path.extname(filepath);
  if (!['.ts', '.tsx', '.js', '.jsx', '.css'].includes(ext)) return;

  const content = fs.readFileSync(filepath, 'utf8');
  const lines = content.split('\n');
  const relPath = path.relative(srcDir, filepath).replace(/\\/g, '/');

  if (ext === '.css') {
    analyzeCSSFile(content, lines, relPath);
  } else {
    analyzeCodeFile(content, lines, relPath);
  }
}

function analyzeCSSFile(content, lines, filepath) {
  // Find button-related class definitions
  const buttonClassPattern = /\.(btn|button|icon-button|toggle|action-btn|close-btn|minimize-btn|maximize-btn|toolbar-btn|taskbar-btn|header-btn)[^\s{]*/g;

  lines.forEach((line, idx) => {
    const matches = line.matchAll(buttonClassPattern);
    for (const match of matches) {
      results.cssClasses.push({
        file: filepath,
        line: idx + 1,
        className: match[0],
        context: line.trim()
      });
    }
  });
}

function analyzeCodeFile(content, lines, filepath) {
  lines.forEach((line, idx) => {
    const lineNum = idx + 1;

    // Find HTML <button> tags
    const buttonTagMatch = /<button([^>]*)>/g;
    const buttonMatches = line.matchAll(buttonTagMatch);
    for (const match of buttonMatches) {
      const attributes = match[1];
      const classMatch = /class[Name]*=["']([^"']+)["']/i.exec(attributes);
      const onClickMatch = /onClick=\{([^}]+)\}/i.exec(attributes);

      results.htmlButtons.push({
        file: filepath,
        line: lineNum,
        attributes: attributes.trim(),
        classes: classMatch ? classMatch[1] : '',
        onClick: onClickMatch ? onClickMatch[1] : '',
        context: line.trim()
      });
    }

    // Find createElement('button')
    if (line.includes('createElement') && line.includes('button')) {
      results.createElementButtons.push({
        file: filepath,
        line: lineNum,
        context: line.trim()
      });
    }

    // Find addEventListener('click')
    if (line.includes('addEventListener') && line.includes('click')) {
      results.clickHandlers.push({
        file: filepath,
        line: lineNum,
        context: line.trim()
      });
    }

    // Find .style.xxx = patterns (inline styling)
    if (line.match(/\.style\.\w+\s*=/)) {
      results.inlineStyles.push({
        file: filepath,
        line: lineNum,
        context: line.trim()
      });
    }
  });
}

// Scan all files
try {
  console.log('Scanning:', srcDir);
  walkDir(srcDir, analyzeFile);

  // Generate markdown report
  let report = '# Button Inventory Report\n\n';
  report += `**Generated:** ${new Date().toISOString()}\n\n`;
  report += '---\n\n';

  // Summary
  report += '## Summary\n\n';
  report += `- **HTML Button Elements:** ${results.htmlButtons.length}\n`;
  report += `- **createElement Buttons:** ${results.createElementButtons.length}\n`;
  report += `- **CSS Button Classes:** ${results.cssClasses.length}\n`;
  report += `- **Click Handlers:** ${results.clickHandlers.length}\n`;
  report += `- **Inline Styles:** ${results.inlineStyles.length}\n\n`;
  report += '---\n\n';

  // HTML Buttons
  report += '## 1. HTML Button Elements\n\n';
  if (results.htmlButtons.length === 0) {
    report += '*No HTML button elements found.*\n\n';
  } else {
    results.htmlButtons.forEach(btn => {
      report += `### ${btn.file}:${btn.line}\n`;
      report += `- **Classes:** \`${btn.classes || 'none'}\`\n`;
      report += `- **onClick:** \`${btn.onClick || 'none'}\`\n`;
      report += `- **Attributes:** \`${btn.attributes}\`\n`;
      report += `\`\`\`html\n${btn.context}\n\`\`\`\n\n`;
    });
  }

  // createElement Buttons
  report += '## 2. createElement("button") Calls\n\n';
  if (results.createElementButtons.length === 0) {
    report += '*No createElement button calls found.*\n\n';
  } else {
    results.createElementButtons.forEach(btn => {
      report += `### ${btn.file}:${btn.line}\n`;
      report += `\`\`\`typescript\n${btn.context}\n\`\`\`\n\n`;
    });
  }

  // CSS Classes
  report += '## 3. Button CSS Classes\n\n';
  const classesByFile = {};
  results.cssClasses.forEach(cls => {
    if (!classesByFile[cls.file]) classesByFile[cls.file] = [];
    classesByFile[cls.file].push(cls);
  });

  Object.keys(classesByFile).sort().forEach(file => {
    report += `### ${file}\n\n`;
    classesByFile[file].forEach(cls => {
      report += `- Line ${cls.line}: \`${cls.className}\`\n`;
      report += `  \`\`\`css\n  ${cls.context}\n  \`\`\`\n`;
    });
    report += '\n';
  });

  // Click Handlers
  report += '## 4. Click Event Handlers\n\n';
  if (results.clickHandlers.length === 0) {
    report += '*No addEventListener click handlers found.*\n\n';
  } else {
    const handlersByFile = {};
    results.clickHandlers.forEach(handler => {
      if (!handlersByFile[handler.file]) handlersByFile[handler.file] = [];
      handlersByFile[handler.file].push(handler);
    });

    Object.keys(handlersByFile).sort().forEach(file => {
      report += `### ${file}\n\n`;
      handlersByFile[file].forEach(handler => {
        report += `- Line ${handler.line}:\n`;
        report += `  \`\`\`typescript\n  ${handler.context}\n  \`\`\`\n`;
      });
      report += '\n';
    });
  }

  // Inline Styles
  report += '## 5. Inline Styles (Potential Issues)\n\n';
  if (results.inlineStyles.length === 0) {
    report += '*No inline styles found.*\n\n';
  } else {
    const stylesByFile = {};
    results.inlineStyles.forEach(style => {
      if (!stylesByFile[style.file]) stylesByFile[style.file] = [];
      stylesByFile[style.file].push(style);
    });

    Object.keys(stylesByFile).sort().forEach(file => {
      report += `### ${file}\n\n`;
      stylesByFile[file].forEach(style => {
        report += `- Line ${style.line}:\n`;
        report += `  \`\`\`typescript\n  ${style.context}\n  \`\`\`\n`;
      });
      report += '\n';
    });
  }

  // Recommendations
  report += '## 6. Recommendations\n\n';
  report += '### Consistency Issues\n\n';

  const uniqueClasses = new Set(results.cssClasses.map(c => c.className));
  report += `- **${uniqueClasses.size} unique button class names** found across ${results.cssClasses.length} usages\n`;

  if (results.inlineStyles.length > 0) {
    report += `- **${results.inlineStyles.length} inline styles** detected - should be moved to CSS classes\n`;
  }

  report += '\n### Suggested Unification Strategy\n\n';
  report += '1. **Consolidate CSS classes** - Create a single button component system\n';
  report += '2. **Remove inline styles** - Move all styling to CSS files\n';
  report += '3. **Standardize event handlers** - Use consistent onClick/addEventListener pattern\n';
  report += '4. **Create button variants** - primary, secondary, ghost, danger, icon-only\n';
  report += '5. **Document button usage** - Create style guide with examples\n\n';

  // Write report
  fs.writeFileSync(outputPath, report, 'utf8');

  console.log('\n=== BUTTON INVENTORY COMPLETE ===');
  console.log('HTML Buttons:', results.htmlButtons.length);
  console.log('createElement Buttons:', results.createElementButtons.length);
  console.log('CSS Classes:', results.cssClasses.length);
  console.log('Click Handlers:', results.clickHandlers.length);
  console.log('Inline Styles:', results.inlineStyles.length);
  console.log('\nReport written to:', outputPath);

} catch (error) {
  console.error('Error during scan:', error);
  process.exit(1);
}
