package com.bestbudz.entity.pets.variants;

import com.bestbudz.entity.pets.PetVariantManager;

public class CorpVariant extends PetVariantManager
{
	public static int corpPet = 4002; // Original Pet
	public static void createCorpVariants() {
		int startingNpcId = 10041; // ends at 10049

		String[] corpStages = {
			"Toy Corp",
			"Infant Corp",
			"Hatchling Corp",
			"Cub Corp",
			"Youth Corp",
			"Teen Corp",
			"Young Adult Corp",
			"Adult Corp",
			"Prime Corp"
		};

		int[] scaleSteps = {
			9, 11, 13, 15, 17, 19, 21, 24, 27 // Pet scales to 30%
		};

		for (int i = 0; i < corpStages.length; i++) {
			int scale = scaleSteps[i];
			variants.put(startingNpcId + i, new VariantConfig(corpPet)
				.setName(corpStages[i])
				.setModelScale(scale, scale)
				.setActions(new String[]{"Pick-up", "Examine", null, null, null}));
		}
	}


}