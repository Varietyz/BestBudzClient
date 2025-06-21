package com.bestbudz.entity.pets.variants;

import com.bestbudz.entity.pets.PetVariantManager;

public class JadVariant extends PetVariantManager
{
	public static int jadPet = 4010; // Original Pet
	public static void createJadVariants() {
		int startingNpcId = 10001; // ends at 10009

		String[] jadStages = {
			"Toy Jad",
			"Infant Jad",
			"Hatchling Jad",
			"Cub Jad",
			"Youth Jad",
			"Teen Jad",
			"Young Adult Jad",
			"Adult Jad",
			"Prime Jad"
		};

		int[] scaleSteps = {
			9, 11, 13, 15, 17, 19, 21, 24, 27 // Pet scales to 30%
		};

		for (int i = 0; i < jadStages.length; i++) {
			int scale = scaleSteps[i];
			variants.put(startingNpcId + i, new VariantConfig(jadPet)
				.setName(jadStages[i])
				.setModelScale(scale, scale)
				.setActions(new String[]{"Pick-up", "Examine", null, null, null}));
		}
	}


}