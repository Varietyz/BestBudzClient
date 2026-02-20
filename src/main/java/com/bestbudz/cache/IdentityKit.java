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

	}

	private void readValues(Stream stream)
	{
		do
		{
			int i = stream.readUnsignedByte();
			if (i == 0)
			{

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

		for (int color : anIntArray659) {
			if (color != 0) {
				originalColors.add(color);
			}
		}

		for (int color : anIntArray660) {
			if (color != 0) {
				replacementColors.add(color);
			}
		}

		for (int color : anIntArray661) {
			if (color != -1) {
				additionalColors.add(color);
			}
		}
	}

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
