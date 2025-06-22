package com.bestbudz.entity.pets.petvariants;

import com.bestbudz.entity.pets.PetVariantManager;

public class GraardorVariant extends PetVariantManager {
	public static int graardorPet = 4001; // Pet General Graardor
	public static void createGraardorVariants() {
		int startNpcId = 10011; // Ends 10029

		String[] names = {
			"Toy Graardor",
			"Club-Wielding Newborn",
			"Tiny Brute",
			"Juvenile Graardor",
			"Youngblood Graardor",
			"Warborn Graardor",
			"Rampaging Graardor",
			"Juggernaut Graardor",
			"Berserker Graardor"
		};

		int[] scales = {
			3, 6, 9, 12, 15, 18, 21, 24, 27 // Pet scales to 30%
		};

		for (int i = 0; i < names.length; i++) {
			variants.put(startNpcId + i, new VariantConfig(graardorPet)
				.setName(names[i])
				.setModelScale(scales[i], scales[i])
				.setActions(new String[]{"Pick-up", "Rage", "Smash", null, null}));
		}
	}
}
