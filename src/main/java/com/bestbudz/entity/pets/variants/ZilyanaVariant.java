package com.bestbudz.entity.pets.variants;

import com.bestbudz.entity.pets.PetVariantManager;

public class ZilyanaVariant extends PetVariantManager {
	public static int zilyanaPet = 4009; // Pet Commander Zilyana
	public static void createZilyanaVariants() {
		int startNpcId = 10031; // Ends 10039
		String[] names = {
			"Toy Zilyana",
			"Young Acolyte Zilyana",
			"Disciple Zilyana",
			"Templar Zilyana",
			"Crusader Zilyana",
			"Seraph Zilyana",
			"High Priestess Zilyana",
			"Champion Zilyana",
			"Commander Zilyana"
		};

		int[] scales = {
			3, 6, 9, 12, 15, 18, 21, 24, 27 // Pet scales to 30%
		};

		for (int i = 0; i < names.length; i++) {
			variants.put(startNpcId + i, new VariantConfig(zilyanaPet)
				.setName(names[i])
				.setModelScale(scales[i], scales[i])
				.setActions(new String[]{"Pick-up", "Command", null, null, null}));
		}
	}
}
