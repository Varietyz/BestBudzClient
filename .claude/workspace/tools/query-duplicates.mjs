// Query database for code duplication results
import Database from "better-sqlite3";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const dbPath = path.resolve(__dirname, "../../../root/codebase-validation/data/cv.db");
const db = new Database(dbPath);

// Query all scan results
const results = db.prepare("SELECT * FROM scan_results WHERE scanner_id LIKE '%dup%' OR category LIKE '%dup%' LIMIT 100").all();

if (results.length === 0) {
    console.log("No duplication results found in database");
    console.log("\nChecking all scanner_ids:");
    const scanners = db.prepare("SELECT DISTINCT scanner_id FROM scan_results LIMIT 50").all();
    console.log(JSON.stringify(scanners, null, 2));
} else {
    console.log(JSON.stringify(results.slice(0, 20), null, 2));
}

db.close();
