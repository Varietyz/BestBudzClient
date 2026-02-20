#!/usr/bin/env node

function patternToRegex(pattern) {
    const escaped = pattern.replace(/[.+?^${}()|[\]\\]/g, '\\$&');
    const regex = escaped.replace(/\\\*/g, '.*');
    return new RegExp(`^${regex}$`);
}

const pattern = "limits.*";
const regex = patternToRegex(pattern);
const testPath = "limits.maxLinesPerFile";

console.log("Pattern:", pattern);
console.log("Regex:", regex);
console.log("Test path:", testPath);
console.log("Match:", regex.test(testPath));

// Test the correct regex
const correctRegex = /^limits\..*$/;
console.log("\nCorrect regex:", correctRegex);
console.log("Correct match:", correctRegex.test(testPath));
