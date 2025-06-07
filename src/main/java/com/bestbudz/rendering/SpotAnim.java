package com.bestbudz.rendering;

import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;

public final class SpotAnim {

	public static SpotAnim[] cache;
	private final int[] originalColors;
	private final int[] replacementColors;
	public Animation aAnimation_407;
	public int anInt410;
	public int anInt411;
	public int anInt412;
	public int anInt413;
	public int anInt414;
	private int anInt404;
	private int anInt405;
	private int anInt406;

	private SpotAnim() {
		anInt406 = -1;
		originalColors = new int[6];
		replacementColors = new int[6];
		anInt410 = 128;
		anInt411 = 128;
	}

	public static void unpackConfig(StreamLoader streamLoader) {
		Stream stream = new Stream(streamLoader.getDataForName("spotanim.dat"));
		int length = stream.readUnsignedWord();
		System.out.println("Graphics Loaded: " + length);

		if (cache == null) {
			cache = new SpotAnim[length + 50000];
		}

		for (int j = 0; j < length; j++) {
			if (cache[j] == null) {
				cache[j] = new SpotAnim();
			}
			cache[j].anInt404 = j;
			cache[j].readValues(stream);
		}
	}

	public void readValues(Stream stream) {
		int opcode;
		while ((opcode = stream.readUnsignedByte()) != 0) {
			switch (opcode) {
				case 1:
					anInt405 = stream.readUnsignedWord();
					break;
				case 2:
					anInt406 = stream.readUnsignedWord();
					if (Animation.anims != null) {
						aAnimation_407 = Animation.anims[anInt406];
					}
					break;
				case 4:
					anInt410 = stream.readUnsignedWord();
					break;
				case 5:
					anInt411 = stream.readUnsignedWord();
					break;
				case 6:
					anInt412 = stream.readUnsignedWord();
					break;
				case 7:
					anInt413 = stream.readUnsignedWord();
					break;
				case 8:
					anInt414 = stream.readUnsignedWord();
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
		Model model = Model.loadModelFromCache(anInt405);
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