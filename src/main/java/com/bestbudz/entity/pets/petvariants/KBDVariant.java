package com.bestbudz.entity.pets.petvariants;

import com.bestbudz.entity.pets.PetVariantManager;

public class KBDVariant extends PetVariantManager {
public static int kbdPet = 4000;
	public static void createDragonVariants() {
		 // Base KBD NPC ID (Prince Black Dragon)
		int startNpcId = 10021; // Ends At 10019
		String[] names = {
			"Toy KBD",
			"Hatchling KBD",
			"Youthful KBD",
			"Smoldering KBD",
			"Adolescent KBD",
			"Vicious KBD",
			"Darkscale KBD",
			"Nightmare KBD",
			"Ancient KBD"
		};

		int[] scales = {
			3, 6, 9, 12, 15, 18, 21, 24, 27 // Pet scales to 30%
		};

		for (int i = 0; i < names.length; i++) {
			variants.put(startNpcId + i, new VariantConfig(kbdPet)
				.setName(names[i])
				.setModelScale(scales[i], scales[i])
				.setActions(new String[]{"Pick-up", "Freeze", null, null, null}));
		}
	}
}
