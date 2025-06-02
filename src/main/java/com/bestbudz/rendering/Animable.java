package com.bestbudz.rendering;

import com.bestbudz.rendering.model.Class33;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.NodeSub;

public class Animable extends NodeSub {

	public int modelHeight;
	public Class33[] aClass33Array1425;

	// FLICKERING FIX: Add model caching and stability tracking
	private Model lastValidModel = null;
	private int lastValidModelHeight = 1000;
	private long lastModelUpdateTime = 0;
	private static final long MODEL_CACHE_DURATION = 100; // ms to cache model during rapid changes

	protected Animable() {
		modelHeight = 1000;
	}

	public void method443(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
		// FLICKERING FIX: Use cached model during rapid state changes
		Model model = getFinalRenderedModelSafe();
		if (model != null) {
			modelHeight = model.modelHeight;
			model.method443(i, j, k, l, i1, j1, k1, l1, i2);
		}
	}

	/**
	 * FLICKERING FIX: Safe model retrieval with caching during combat/animation transitions
	 */
	public Model getFinalRenderedModelSafe() {
		long currentTime = System.currentTimeMillis();
		Model model = getFinalRenderedModel();

		if (model != null) {
			// Valid model - update cache
			lastValidModel = model;
			lastValidModelHeight = model.modelHeight;
			lastModelUpdateTime = currentTime;
			return model;
		} else {
			// Null model - check if we should use cached version
			if (lastValidModel != null &&
				(currentTime - lastModelUpdateTime) < MODEL_CACHE_DURATION) {

				// Use cached model during brief transitions
				modelHeight = lastValidModelHeight;
				return lastValidModel;
			}

			// Cache expired or no valid cache - return null
			return null;
		}
	}

	public Model getFinalRenderedModel() {
		return null;
	}
}