/**
 * Forensic verification tool: Test preset APIs
 */

import { listPresets, getPresetInfo, loadPreset } from '../../../root/codebase-validation/core/settings/settings-core.js';

async function testPresetAPIs() {
    const results = {
        listPresets: { status: 'pending', data: null },
        getPresetInfo: { status: 'pending', data: null },
        loadPreset: { status: 'pending', data: null }
    };

    try {
        // Test listPresets()
        const presets = await listPresets();
        results.listPresets = {
            status: 'verified',
            data: presets,
            check: Array.isArray(presets) && presets.length === 3
        };

        // Test getPresetInfo()
        const info = await getPresetInfo('enterprise');
        results.getPresetInfo = {
            status: 'verified',
            data: info,
            check: info && info.name === 'Enterprise'
        };

        // Test loadPreset()
        const merged = await loadPreset('startup');
        results.loadPreset = {
            status: 'verified',
            data: { type: typeof merged, hasLimits: !!merged.limits },
            check: typeof merged === 'object' && merged.limits && merged.limits.maxLinesPerFile === 300
        };

    } catch (error) {
        console.error('Verification failed:', error.message);
        results.error = error.message;
    }

    console.log(JSON.stringify(results, null, 2));
}

testPresetAPIs();
