package com.bestbudz.rendering;

import com.bestbudz.network.ArchiveLoader;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;

public final class SpotAnim {

	public static SpotAnim[] cache;
	private final int[] originalColors;
	private final int[] replacementColors;
	public Animation animation;
	public int resizeX;
	public int resizeY;
	public int rotation;
	public int ambient;
	public int contrast;
	private int id;
	private int modelId;
	private int animationId;

	private SpotAnim() {
		animationId = -1;
		originalColors = new int[6];
		replacementColors = new int[6];
		resizeX = 128;
		resizeY = 128;
	}

	public static void loadConfigurations(ArchiveLoader archiveLoader) {
		Stream stream = new Stream(archiveLoader.extractFile("spotanim.dat"));
		int length = stream.readUnsignedWord();
		System.out.println("Graphics Loaded: " + length);

		if (cache == null) {
			cache = new SpotAnim[length + 50000];
		}

		for (int j = 0; j < length; j++) {
			if (cache[j] == null) {
				cache[j] = new SpotAnim();
			}
			cache[j].id = j;
			cache[j].decode(stream);
		}
	}

	public void decode(Stream stream) {
		int opcode;
		while ((opcode = stream.readUnsignedByte()) != 0) {
			switch (opcode) {
				case 1:
					modelId = stream.readUnsignedWord();
					break;
				case 2:
					animationId = stream.readUnsignedWord();
					if (Animation.anims != null) {
						animation = Animation.anims[animationId];
					}
					break;
				case 4:
					resizeX = stream.readUnsignedWord();
					break;
				case 5:
					resizeY = stream.readUnsignedWord();
					break;
				case 6:
					rotation = stream.readUnsignedWord();
					break;
				case 7:
					ambient = stream.readUnsignedWord();
					break;
				case 8:
					contrast = stream.readUnsignedWord();
					break;
				case 40:
					int colorCount = stream.readUnsignedByte();
					for (int i = 0; i < colorCount; i++) {
						originalColors[i] = stream.readUnsignedWord();
						replacementColors[i] = stream.readUnsignedWord();
					}
					break;
				default:
					System.out.println("Error unrecognised spotanim config code: " + opcode);
					break;
			}
		}
	}

	public Model getModel() {
		Model model = Model.loadModelFromCache(modelId);
		if (model == null) {
			return null;
		}

		// Apply color replacements
		for (int i = 0; i < 6; i++) {
			if (originalColors[i] != 0) {
				model.replaceColor(originalColors[i], replacementColors[i]);
			}
		}

		return model;
	}
}