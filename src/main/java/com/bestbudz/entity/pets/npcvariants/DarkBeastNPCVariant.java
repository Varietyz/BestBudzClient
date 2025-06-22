package com.bestbudz.entity.pets.npcvariants;

import com.bestbudz.entity.pets.PetVariantManager;

public class DarkBeastNPCVariant extends PetVariantManager
{
	public static int darkbeastPet = 10050;
	public static void createDarkBeastVariants() {
		int darkBeastNPC = 4005; // Original NPC
		int startingNpcId = 10050; // ends at 10050
		String[] darkbeastStages = {
			"Dark Beast"
		};

		int[] scaleSteps = {
			30 // Pet scales to 30%
		};

		for (int i = 0; i < darkbeastStages.length; i++) {
			int scale = scaleSteps[i];

			variants.put(startingNpcId + i, new VariantConfig(darkBeastNPC)
				.setName(darkbeastStages[i])
				.setModelScale(scale, scale)
				.setActions(new String[]{"Pick-up", "Examine", null, null, null}));
		}
	}


}