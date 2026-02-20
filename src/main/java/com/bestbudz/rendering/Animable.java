package com.bestbudz.rendering;

import static com.bestbudz.engine.core.Client.queueSpotAnimation;
import static com.bestbudz.engine.core.Client.loopCycle;
import static com.bestbudz.engine.core.Client.plane;
import static com.bestbudz.engine.core.Client.gameTickCounter;
import static com.bestbudz.engine.core.Client.worldController;
import com.bestbudz.engine.gpu.RS317GPUInterface;
import com.bestbudz.rendering.model.Point3D;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.NodeSub;

public class Animable extends NodeSub {

	public int modelHeight;
	public Point3D[] vertices;

	protected Animable() {
		modelHeight = 1000;
	}

	public void render(int rotation, int sinVertical, int cosVertical, int sinHorizontal, int cosHorizontal, int worldX, int worldY, int worldZ, int id) {
		Model model = getModel();
		if (model != null) {
			modelHeight = model.modelHeight;

			if (RS317GPUInterface.isActive()) {

				System.out.println("[GPU DEBUG] Rendering model via GPU: " + worldX + "," + worldY + "," + worldZ);

				RS317GPUInterface.renderModel(model, worldX, worldY, worldZ, rotation, 0, 0, 64);
				return;
			}

			model.render(rotation, sinVertical, cosVertical, sinHorizontal, cosHorizontal, worldX, worldY, worldZ, id);
		}
	}

	public Model getModel() {
		return null;
	}

	public static void processGraphicEffects()
	{
		GraphicEffect class30_sub2_sub4_sub3 = (GraphicEffect) queueSpotAnimation.reverseGetFirst();
		for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (GraphicEffect) queueSpotAnimation.reverseGetNext())
			if (class30_sub2_sub4_sub3.plane != plane || class30_sub2_sub4_sub3.finished)
				class30_sub2_sub4_sub3.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub3.endCycle)
			{
				class30_sub2_sub4_sub3.update(gameTickCounter);
				if (class30_sub2_sub4_sub3.finished)
					class30_sub2_sub4_sub3.unlink();
				else
					worldController.addLargeObject(class30_sub2_sub4_sub3.plane, 0, class30_sub2_sub4_sub3.z, -1,
						class30_sub2_sub4_sub3.y, 60, class30_sub2_sub4_sub3.x,
						class30_sub2_sub4_sub3, false);
			}

	}
}
