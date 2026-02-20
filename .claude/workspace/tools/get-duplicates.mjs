// Temporary script to extract code duplication details
import path from "path";
import { fileURLToPath } from "url";
import { PROJECT_PATHS } from "../../../root/codebase-validation/constants/index.js";
import { ExclusionManager } from "../../../root/codebase-validation/core/exclusion-manager.js";
import { BoundaryScanner } from "../../../root/codebase-validation/helpers/code-duplication/boundary-helper.js";
import { ResultAggregator } from "../../../root/codebase-validation/helpers/code-duplication/result-aggregator.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const codebaseValidationDir = path.resolve(__dirname, "../../../root/codebase-validation");

// Mock scanner interface
const mockScanner = {
    getName: () => "temp-duplication-extractor",
};

async function extractDuplicates() {
    const boundaries = [{ name: "root", dirs: [PROJECT_PATHS.root] }];

    const exclusionsDir = path.join(codebaseValidationDir, "exclusions");
    const exclusionManager = new ExclusionManager();
    await exclusionManager.discoverRules(exclusionsDir);

    const boundaryScanner = new BoundaryScanner(mockScanner, exclusionManager);
    const boundaryResults = [];

    for (const boundary of boundaries) {
        const result = await boundaryScanner.scanBoundary(boundary.name, boundary.dirs);
        boundaryResults.push(result);
    }

    const aggregated = ResultAggregator.aggregate(boundaryResults);

    // Output top 50 duplicates with details
    console.log(JSON.stringify({
        total_duplicates: aggregated.total_duplicates,
        total_instances: aggregated.total_instances,
        top_duplicates: aggregated.duplicates.slice(0, 50).map((dup, idx) => ({
            id: idx + 1,
            count: dup.count,
            hash: dup.hash,
            content_preview: dup.content?.substring(0, 200) || "",
            instances: dup.instances?.map(inst => ({
                file: inst.file || inst.path,
                line: inst.line || 1,
                end_line: inst.end_line || (inst.line || 1) + (dup.lines || 0)
            })) || []
        }))
    }, null, 2));
}

extractDuplicates().catch(console.error);
