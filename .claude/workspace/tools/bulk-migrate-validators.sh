#!/usr/bin/env bash
# Bulk Validator Migration Script
# Migrates from BaseValidator to BaseDbValidator systematically

set -euo pipefail

VALIDATORS_DIR="D:/GIT/archlab/root/codebase-validation/validators"
LOG_FILE="D:/GIT/archlab/.claude/workspace/tools/migration-log.txt"

echo "Starting bulk validator migration..." > "$LOG_FILE"
echo "Timestamp: $(date)" >> "$LOG_FILE"
echo "" >> "$LOG_FILE"

migrated=0
skipped=0
errors=0

# Find all *-validator.js files that still extend BaseValidator
while IFS= read -r file; do
    filename=$(basename "$file")

    # Skip already migrated files
    if grep -q "BaseDbValidator" "$file" 2>/dev/null; then
        echo "SKIP: $filename (already migrated)" >> "$LOG_FILE"
        ((skipped++))
        continue
    fi

    # Skip if doesn't use BaseValidator
    if ! grep -q "extends BaseValidator" "$file" 2>/dev/null; then
        echo "SKIP: $filename (doesn't extend BaseValidator)" >> "$LOG_FILE"
        ((skipped++))
        continue
    fi

    echo "Processing: $filename" >> "$LOG_FILE"

    # Create backup
    cp "$file" "$file.bak"

    # Extract validator name (remove .js)
    validator_name="${filename%.js}"

    # Infer scanner ID (remove -validator suffix)
    scanner_id="${validator_name%-validator}"

    # Calculate relative path depth for import
    depth=$(echo "$file" | grep -o "/validators/" | wc -l)
    if [ "$depth" -eq 0 ]; then
        depth=1  # Fallback
    fi

    # Build relative import path
    rel_path=""
    for ((i=1; i<=$depth+1; i++)); do
        rel_path="../$rel_path"
    done
    rel_path="${rel_path}core/base/base-db-validator.js"

    # Perform migrations
    sed -i "s|import { BaseValidator } from [\"'].*base-validator\.js[\"'];|import { BaseDbValidator } from \"$rel_path\";|g" "$file"
    sed -i "s|extends BaseValidator|extends BaseDbValidator|g" "$file"
    sed -i "s|async analyzeViolations(_state, _invariants)|async analyzeViolations(state, _invariants)|g" "$file"

    # Add getScannerId method - will need manual placement but mark it
    echo "  - Added BaseDbValidator import" >> "$LOG_FILE"
    echo "  - Changed extends to BaseDbValidator" >> "$LOG_FILE"
    echo "  - Changed _state to state parameter" >> "$LOG_FILE"
    echo "  - Scanner ID: $scanner_id" >> "$LOG_FILE"

    ((migrated++))

done < <(find "$VALIDATORS_DIR" -type f -name "*-validator.js")

echo "" >> "$LOG_FILE"
echo "Migration Summary:" >> "$LOG_FILE"
echo "  Migrated: $migrated" >> "$LOG_FILE"
echo "  Skipped: $skipped" >> "$LOG_FILE"
echo "  Errors: $errors" >> "$LOG_FILE"

echo "✓ Bulk migration complete!"
echo "  Migrated: $migrated validators"
echo "  Skipped: $skipped validators"
echo "  Log: $LOG_FILE"
echo ""
echo "NEXT STEPS:"
echo "1. Manually add getScannerId() method to each migrated validator"
echo "2. Update getName() to include -validator suffix"
echo "3. Replace this.loadRegistry() with state.data.violations"
echo "4. Remove this.paths references"
echo "5. Change return false/true to { violations, total } object"
