
package com.bestbudz.entity.pets;

import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.pets.npcvariants.AbyssalDemonNPCVariant;
import com.bestbudz.entity.pets.petvariants.*;
import java.util.HashMap;
import java.util.Map;

public class PetVariantManager
{

	protected static final Map<Integer, VariantConfig> variants = new HashMap<>();
	private static final Map<Integer, EntityDef> variantCache = new HashMap<>();

	private static final int VARIANT_START_ID = 10000;

	static {

		initializeVariants();
		prebuildVariantCache();
	}

	private static void initializeVariants() {

		JadVariant.createJadVariants();
		KBDVariant.createDragonVariants();
		GraardorVariant.createGraardorVariants();
		ZilyanaVariant.createZilyanaVariants();
		CorpVariant.createCorpVariants();
		AbyssalDemonNPCVariant.createAbyssalDemonVariants();
	}

	private static void prebuildVariantCache() {
		for (Integer variantId : variants.keySet()) {
			EntityDef entityDef = new EntityDef();
			entityDef.interfaceType = variantId;
			applyVariant(variantId, entityDef);
			variantCache.put(variantId, entityDef);
		}
	}

	public static EntityDef getVariant(int variantId) {
		return variantCache.get(variantId);
	}

	public static boolean isVariant(int npcId) {
		return variants.containsKey(npcId);
	}

	public static void applyVariant(int variantId, EntityDef entityDef) {
		VariantConfig config = variants.get(variantId);
		if (config == null) return;

		EntityDef baseEntity = EntityDef.forID(config.baseId, false);
		if (baseEntity == null) return;

		copyBaseProperties(baseEntity, entityDef);

		if (config.name != null) {
			entityDef.name = config.name;
		}

		if (config.description != null) {
			entityDef.description = config.description.getBytes();
		}

		if (config.actions != null) {
			entityDef.actions = config.actions.clone();
		}

		if (config.modelWidth != -1) {
			entityDef.modelWidth = config.modelWidth;
		}

		if (config.modelHeight != -1) {
			entityDef.modelHeight = config.modelHeight;
		}
	}

	private static void copyBaseProperties(EntityDef source, EntityDef target) {

		if (source.models != null) {
			target.models = source.models.clone();
		}
		if (source.newColors != null) {
			target.newColors = source.newColors.clone();
		}
		if (source.originalColors != null) {
			target.originalColors = source.originalColors.clone();
		}

		target.walkAnim = source.walkAnim;
		target.standAnim = source.standAnim;
		target.combatGrade = source.combatGrade;
		target.modelWidth = source.modelWidth;
		target.modelHeight = source.modelHeight;
		target.size = source.size;

		if (source.actions != null) {
			target.actions = source.actions.clone();
		}

		if (source.name != null) {
			target.name = source.name;
		}
		if (source.description != null) {
			target.description = source.description.clone();
		}
	}

	protected static class VariantConfig {
		int baseId;
		String name;
		String description;
		String[] actions;
		int modelWidth = -1;
		int modelHeight = -1;

		public VariantConfig(int baseId) {
			this.baseId = baseId;
		}

		public VariantConfig setName(String name) {
			this.name = name;
			return this;
		}

		public VariantConfig setDescription(String description) {
			this.description = description;
			return this;
		}

		public VariantConfig setActions(String[] actions) {
			this.actions = actions;
			return this;
		}

		public VariantConfig setModelScale(int width, int height) {
			this.modelWidth = width;
			this.modelHeight = height;
			return this;
		}
	}
}
