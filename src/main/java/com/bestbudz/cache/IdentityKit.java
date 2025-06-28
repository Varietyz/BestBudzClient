package com.bestbudz.cache;

import com.bestbudz.network.Stream;
import com.bestbudz.network.ArchiveLoader;
import com.bestbudz.rendering.model.Model;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class IdentityKit
{
	public static int length;
	public static IdentityKit[] cache;
	private static Set<Integer> originalColors = new HashSet<>();
	private static Set<Integer> replacementColors = new HashSet<>();
	private static Set<Integer> additionalColors = new HashSet<>();

	private final int[] anIntArray659;
	private final int[] anIntArray660;
	private final int[] anIntArray661 = {
		-1, -1, -1, -1, -1
	};
	public int anInt657;
	public boolean aBoolean662;
	private int[] anIntArray658;

	private IdentityKit()
	{
		anInt657 = -1;
		anIntArray659 = new int[6];
		anIntArray660 = new int[6];
		aBoolean662 = false;
	}

	public static void unpackConfig(ArchiveLoader archiveLoader)
	{
		Stream stream = new Stream(archiveLoader.extractFile("idk.dat"));
		length = stream.readUnsignedWord();
		if (cache == null)
			cache = new IdentityKit[length];
		for (int j = 0; j < length; j++)
		{
			if (cache[j] == null)
				cache[j] = new IdentityKit();
			cache[j].readValues(stream);
		}

		// Save colors to cache after all identities are loaded
		//saveColorsToCache();
	}

	private void readValues(Stream stream)
	{
		do
		{
			int i = stream.readUnsignedByte();
			if (i == 0)
			{
				// Collect colors when this identity kit is fully loaded
				collectColors();
				return;
			}
			if (i == 1)
				anInt657 = stream.readUnsignedByte();
			else if (i == 2)
			{
				int j = stream.readUnsignedByte();
				anIntArray658 = new int[j];
				for (int k = 0; k < j; k++)
					anIntArray658[k] = stream.readUnsignedWord();

			}
			else if (i == 3)
				aBoolean662 = true;
			else if (i >= 40 && i < 50)
				anIntArray659[i - 40] = stream.readUnsignedWord();
			else if (i >= 50 && i < 60)
				anIntArray660[i - 50] = stream.readUnsignedWord();
			else if (i >= 60 && i < 70)
				anIntArray661[i - 60] = stream.readUnsignedWord();
			else
				System.out.println("Error unrecognised config code: " + i);
		} while (true);
	}

	private void collectColors()
	{
		// Collect original colors (anIntArray659)
		for (int color : anIntArray659) {
			if (color != 0) {
				originalColors.add(color);
			}
		}

		// Collect replacement colors (anIntArray660)
		for (int color : anIntArray660) {
			if (color != 0) {
				replacementColors.add(color);
			}
		}

		// Collect additional colors (anIntArray661)
		for (int color : anIntArray661) {
			if (color != -1) {
				additionalColors.add(color);
			}
		}
	}
/*
	// Add this debug method to your IdentityKit class
	private static void saveColorsToCache()
	{
		int totalKits = 0;
		int kitsWithOriginalColors = 0;
		int kitsWithReplacementColors = 0;
		int kitsWithAdditionalColors = 0;

		try (FileWriter writer = new FileWriter("cache_colors.txt")) {
			writer.write("// IdentityKit Colors Cache - Organized\n");
			writer.write("// Generated automatically during IdentityKit loading\n");
			writer.write("// Total IdentityKit instances loaded: " + length + "\n\n");

			// Debug: Count how many kits actually have colors
			if (cache != null) {
				for (IdentityKit kit : cache) {
					if (kit != null) {
						totalKits++;

						boolean hasOriginal = false, hasReplacement = false, hasAdditional = false;

						for (int color : kit.anIntArray659) {
							if (color != 0) hasOriginal = true;
						}
						for (int color : kit.anIntArray660) {
							if (color != 0) hasReplacement = true;
						}
						for (int color : kit.anIntArray661) {
							if (color != -1) hasAdditional = true;
						}

						if (hasOriginal) kitsWithOriginalColors++;
						if (hasReplacement) kitsWithReplacementColors++;
						if (hasAdditional) kitsWithAdditionalColors++;
					}
				}
			}

			writer.write("// Debug Info:\n");
			writer.write("// - Total kits loaded: " + totalKits + "\n");
			writer.write("// - Kits with original colors: " + kitsWithOriginalColors + "\n");
			writer.write("// - Kits with replacement colors: " + kitsWithReplacementColors + "\n");
			writer.write("// - Kits with additional colors: " + kitsWithAdditionalColors + "\n\n");

			writer.write("// Original Colors (anIntArray659 - colors to be replaced)\n");
			writer.write("// Count: " + originalColors.size() + "\n");
			for (Integer color : originalColors) {
				writer.write(color + "\n");
			}

			writer.write("\n// Replacement Colors (anIntArray660 - new colors)\n");
			writer.write("// Count: " + replacementColors.size() + "\n");
			for (Integer color : replacementColors) {
				writer.write(color + "\n");
			}

			writer.write("\n// Additional Colors (anIntArray661 - extra model colors)\n");
			writer.write("// Count: " + additionalColors.size() + "\n");
			for (Integer color : additionalColors) {
				writer.write(color + "\n");
			}

			// Combine all unique colors
			Set<Integer> allColors = new HashSet<>();
			allColors.addAll(originalColors);
			allColors.addAll(replacementColors);
			allColors.addAll(additionalColors);

			writer.write("\n// All Unique Colors Combined\n");
			writer.write("// Total Count: " + allColors.size() + "\n");
			for (Integer color : allColors) {
				writer.write(color + "\n");
			}

			System.out.println("IdentityKit Debug:");
			System.out.println("  - Total kits loaded: " + totalKits + "/" + length);
			System.out.println("  - Kits with original colors: " + kitsWithOriginalColors);
			System.out.println("  - Kits with replacement colors: " + kitsWithReplacementColors);
			System.out.println("  - Kits with additional colors: " + kitsWithAdditionalColors);
			System.out.println("  - Unique colors saved: " + allColors.size());

		} catch (IOException e) {
			System.err.println("IdentityKit: Error writing colors to cache_colors.txt: " + e.getMessage());
		}
	}
*/
	public boolean method537()
	{
		if (anIntArray658 == null)
			return false;
		boolean flag = true;
		for (int i : anIntArray658)
			if (!Model.isModelCached(i))
				flag = false;

		return !flag;
	}

	public Model method538()
	{
		if (anIntArray658 == null)
			return null;
		Model[] aclass30_sub2_sub4_sub6s = new Model[anIntArray658.length];
		for (int i = 0; i < anIntArray658.length; i++)
			aclass30_sub2_sub4_sub6s[i] = Model.loadModelFromCache(anIntArray658[i]);

		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
		for (int j = 0; j < 6; j++)
		{
			if (anIntArray659[j] == 0)
				break;
			Objects.requireNonNull(model).replaceColor(anIntArray659[j], anIntArray660[j]);
		}

		return model;
	}

	public boolean method539()
	{
		boolean flag1 = true;
		for (int i = 0; i < 5; i++)
			if (anIntArray661[i] != -1 && !Model.isModelCached(anIntArray661[i]))
				flag1 = false;

		return flag1;
	}

	public Model method540()
	{
		Model[] aclass30_sub2_sub4_sub6s = new Model[5];
		int j = 0;
		for (int k = 0; k < 5; k++)
			if (anIntArray661[k] != -1)
				aclass30_sub2_sub4_sub6s[j++] = Model.loadModelFromCache(anIntArray661[k]);

		Model model = new Model(j, aclass30_sub2_sub4_sub6s);
		for (int l = 0; l < 6; l++)
		{
			if (anIntArray659[l] == 0)
				break;
			model.replaceColor(anIntArray659[l], anIntArray660[l]);
		}

		return model;
	}
}