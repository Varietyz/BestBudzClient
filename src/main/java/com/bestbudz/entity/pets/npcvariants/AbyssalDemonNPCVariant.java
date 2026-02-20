package com.bestbudz.entity.pets.npcvariants;

import com.bestbudz.entity.pets.PetVariantManager;

public class AbyssalDemonNPCVariant extends PetVariantManager
{
	public static int abyssalDemonPet = 10050;
	public static void createAbyssalDemonVariants() {
		int abyssalDemonNPC = 415; // Original NPC
		int startingNpcId = 10050; // ends at 10050
		String[] abyssalDemonStages = {
			"Abyssal Demon"
		};

		int[] scaleSteps = {
			50 // Pet scales to 30%
		};

		for (int i = 0; i < abyssalDemonStages.length; i++) {
			int scale = scaleSteps[i];

			variants.put(startingNpcId + i, new VariantConfig(abyssalDemonNPC)
				.setName(abyssalDemonStages[i])
				.setModelScale(scale, scale)
				.setActions(new String[]{"Grab", null, null, null, null}));
		}
	}


}