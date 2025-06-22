package com.bestbudz.data.items;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.entity.pets.PetItemCreator;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.MRUNodes;
import java.util.Objects;

public final class ItemDef {

	public static MRUNodes mruNodes1 = new MRUNodes(100);
	public static MRUNodes mruNodes2 = new MRUNodes(50);
	public static boolean isMembers = true;
	public static int totalItems;
	public static ItemDef[] cache;
	public static int cacheIndex;
	public static Stream stream;
	public static int[] streamIndices;
	public int value;
	public int[] modifiedModelColors;
	public int id;
	public int[] originalModelColors;
	public boolean membersObject;
	public int certTemplateID;
	public int anInt164;
	public int anInt165;
	public String[] groundActions;
	public int modelOffset1;
	public String name;
	public int modelID;
	public int anInt175;
	public boolean stackable;
	public byte[] description;
	public int certID;
	public int modelZoom;
	public int anInt188;
	public String[] itemActions;
	public String[] equipActions;
	public int modelRotationY;
	public int[] stackIDs;
	public int modelOffset2;
	public int anInt197;
	public int modelRotationX;
	public int anInt200;
	public int[] stackAmounts;
	public int team;
	public int anInt204;
	private byte aByte154;
	private int anInt162;
	private int anInt166;
	private int anInt167;
	private int anInt173;
	private int anInt184;
	private int anInt185;
	private int anInt191;
	private int anInt192;
	private int anInt196;
	private byte aByte205;
	public ItemDef() {
		id = -1;
	}



	public static void nullLoader() {
		mruNodes2 = null;
		mruNodes1 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void unpackConfig(StreamLoader archive) {
		stream = new Stream(archive.getDataForName("obj.dat"));
		Stream stream = new Stream(archive.getDataForName("obj.idx"));
		totalItems = stream.readUnsignedWord() + 21;
		System.out.println("Items Loaded: " + totalItems);
		streamIndices = new int[totalItems + 50000];
		int i = 2;
		for (int j = 0; j < totalItems - 21; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}

		cache = new ItemDef[10];
		for (int k = 0; k < 10; k++)
			cache[k] = new ItemDef();
	}

	public static Sprite getSprite(int id, int size, int color, int zoom) {
		if (id == 0) {
			return Client.cacheSprite[326];
		}
		ItemDef item = getItemDefinition(id);
		if (item.stackIDs == null) {
			size = -1;
		}
		if (size > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++) {
				if (size >= item.stackAmounts[j1] && item.stackAmounts[j1] != 0) {
					i1 = item.stackIDs[j1];
				}
			}
			if (i1 != -1) {
				item = getItemDefinition(i1);
			}
		}
		Model model = item.getStackedModel(1);
		if (model == null)
			return null;
		Sprite image = new Sprite(32, 32);
		int k1 = Rasterizer.viewportCenterX;
		int l1 = Rasterizer.viewportCenterY;
		int[] ai = Rasterizer.scanlineOffsets;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.enableDepthBuffer = false;
		DrawingArea.initDrawingArea(32, 32, image.myPixels, new float[32 * 32]);
		DrawingArea.drawPixels(32, 0, 0, 0, 32);
		Rasterizer.initializeViewport();
		int itemZoom = item.modelZoom * zoom - 500;
		int l3 = Rasterizer.sinTable[item.modelRotationY] * itemZoom >> 16;
		int i4 = Rasterizer.cosTable[item.modelRotationY] * itemZoom >> 16;
		model.method482(item.modelRotationX, item.anInt204, item.modelRotationY, item.modelOffset1,
				l3 + model.modelHeight / 2 + item.modelOffset2, i4 + item.modelOffset2);
		if (color == 0) {
			for (int index = 31; index >= 0; index--) {
				for (int index2 = 31; index2 >= 0; index2--)
					if (image.myPixels[index + index2 * 32] == 0 && index > 0 && index2 > 0
							&& image.myPixels[(index - 1) + (index2 - 1) * 32] > 0)
						image.myPixels[index + index2 * 32] = 0x302020;
			}
		}
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.viewportCenterX = k1;
		Rasterizer.viewportCenterY = l1;
		Rasterizer.scanlineOffsets = ai;
		Rasterizer.enableDepthBuffer = true;
		if (item.stackable) {
			image.cropWidth = 33;
		} else {
			image.cropWidth = 32;
		}
		image.anInt1445 = size;

		return image;
	}



	public static Sprite getSprite(int i, int j, int k) {
		if (k == 0) {
			Sprite sprite = (Sprite) mruNodes1.insertFromCache(i);
			if (sprite != null && sprite.anInt1445 != j && sprite.anInt1445 != -1) {

				sprite.unlink();
				sprite = null;
			}
			if (sprite != null) {
				return sprite;
			}
		}
		ItemDef itemDef = getItemDefinition(i);
		if (itemDef.stackIDs == null)
			j = -1;
		if (j > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++)
				if (j >= itemDef.stackAmounts[j1]
						&& itemDef.stackAmounts[j1] != 0)
					i1 = itemDef.stackIDs[j1];

			if (i1 != -1)
				itemDef = getItemDefinition(i1);
		}
		Model model = itemDef.getStackedModel(1);
		if (model == null)
			return null;
		Sprite sprite = null;
		if (itemDef.certTemplateID != -1) {
			sprite = getSprite(itemDef.certID, 10, -1);
			if (sprite == null)
				return null;
		}

		int spriteSize = 32;
		Sprite enabledSprite = new Sprite(spriteSize, spriteSize);

		int k1 = Rasterizer.viewportCenterX;
		int l1 = Rasterizer.viewportCenterY;
		int[] ai = Rasterizer.scanlineOffsets;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.enableDepthBuffer = false;
		DrawingArea.initDrawingArea(spriteSize, spriteSize, enabledSprite.myPixels, new float[32 * 32]);
		DrawingArea.method336(spriteSize, 0, 0, 0, spriteSize);
		Rasterizer.initializeViewport();
		int k3 = itemDef.modelZoom;
		if (k == -1)
			k3 = (int) ((double) k3 * 1.5D);
		if (k > 0)
			k3 = (int) ((double) k3 * 1.04D);
		int l3 = Rasterizer.sinTable[itemDef.modelRotationY] * k3 >> 16;
		int i4 = Rasterizer.cosTable[itemDef.modelRotationY] * k3 >> 16;
		Rasterizer.isRenderingItem = true;
		model.method482(itemDef.modelRotationX, itemDef.anInt204,
				itemDef.modelRotationY, itemDef.modelOffset1, l3
						+ model.modelHeight / 2 + itemDef.modelOffset2,
				i4
						+ itemDef.modelOffset2);
		Rasterizer.isRenderingItem = false;
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--)
				if (enabledSprite.myPixels[i5 + j4 * 32] == 0)
					if (i5 > 0
							&& enabledSprite.myPixels[(i5 - 1) + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 > 0
							&& enabledSprite.myPixels[i5 + (j4 - 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (i5 < 31
							&& enabledSprite.myPixels[i5 + 1 + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 < 31
							&& enabledSprite.myPixels[i5 + (j4 + 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;

		}

		if (k > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--)
					if (enabledSprite.myPixels[j5 + k4 * 32] == 0)
						if (j5 > 0
								&& enabledSprite.myPixels[(j5 - 1) + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 > 0
								&& enabledSprite.myPixels[j5 + (k4 - 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (j5 < 31
								&& enabledSprite.myPixels[j5 + 1 + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 < 31
								&& enabledSprite.myPixels[j5 + (k4 + 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;

			}

		} else if (k == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--)
					if (enabledSprite.myPixels[k5 + l4 * 32] == 0
							&& k5 > 0
							&& l4 > 0
							&& enabledSprite.myPixels[(k5 - 1) + (l4 - 1) * 32] > 0)
						enabledSprite.myPixels[k5 + l4 * 32] = 0x302020;

			}

		}
		if (itemDef.certTemplateID != -1) {
			int l5 = Objects.requireNonNull(sprite).cropWidth;
			int j6 = sprite.anInt1445;
			sprite.cropWidth = 32;
			sprite.anInt1445 = 32;
			sprite.drawSprite(0, 0);
			sprite.cropWidth = l5;
			sprite.anInt1445 = j6;
		}
		if (k == 0)
			mruNodes1.removeFromCache(enabledSprite, i);
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.viewportCenterX = k1;
		Rasterizer.viewportCenterY = l1;
		Rasterizer.scanlineOffsets = ai;
		Rasterizer.enableDepthBuffer = true;
		if (itemDef.stackable)
			enabledSprite.cropWidth = 33;
		else
			enabledSprite.cropWidth = 32;
		enabledSprite.anInt1445 = j;
		return enabledSprite;
	}

	public boolean method192(int j) {
		int k = anInt175;
		int l = anInt166;
		if (j == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return true;
		boolean flag = Model.method463(k);
		if (l != -1 && !Model.method463(l))
			flag = false;
		return flag;
	}

	public Model getWornModel(int gender) {
		int k = anInt175;
		int l = anInt166;
		if (gender == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return null;
		Model model = Model.loadModelFromCache(k);
		if (l != -1) {
			Model model_1 = Model.loadModelFromCache(l);
			Model[] aclass30_sub2_sub4_sub6s = { model, model_1 };
			model = new Model(2, aclass30_sub2_sub4_sub6s);
		}
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				Objects.requireNonNull(model).replaceColor(modifiedModelColors[i1],
						originalModelColors[i1]);

		}
		return model;
	}

	public boolean method195(int j) {
		int k = anInt165;
		int l = anInt188;
		int i1 = anInt185;
		if (j == 1) {
			k = anInt200;
			l = anInt164;
			i1 = anInt162;
		}
		if (k == -1)
			return true;
		boolean flag = Model.method463(k);
		if (l != -1 && !Model.method463(l))
			flag = false;
		if (i1 != -1 && !Model.method463(i1))
			flag = false;
		return flag;
	}

	public Model getInventoryModel(int gender) {
		int j = anInt165;
		int k = anInt188;
		int l = anInt185;
		if (gender == 1) {
			j = anInt200;
			k = anInt164;
			l = anInt162;
		}
		if (j == -1)
			return null;
		Model model = Model.loadModelFromCache(j);
		if (k != -1)
			if (l != -1) {
				Model model_1 = Model.loadModelFromCache(k);
				Model model_3 = Model.loadModelFromCache(l);
				Model[] aclass30_sub2_sub4_sub6_1s = { model, model_1, model_3 };
				model = new Model(3, aclass30_sub2_sub4_sub6_1s);
			} else {
				Model model_2 = Model.loadModelFromCache(k);
				Model[] aclass30_sub2_sub4_sub6s = { model, model_2 };
				model = new Model(2, aclass30_sub2_sub4_sub6s);
			}
		if (gender == 0 && aByte205 != 0)
			Objects.requireNonNull(model).translateCoords(0, aByte205, 0);
		if (gender == 1 && aByte154 != 0)
			Objects.requireNonNull(model).translateCoords(0, aByte154, 0);
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				Objects.requireNonNull(model).replaceColor(modifiedModelColors[i1], originalModelColors[i1]);

		}
		return model;
	}

	public void setDefaults() {
		modelID = 0;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		modelZoom = 2000;
		modelRotationY = 0;
		modelRotationX = 0;
		anInt204 = 0;
		modelOffset1 = 0;
		modelOffset2 = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundActions = null;
		itemActions = null;
		equipActions = new String[6];
		anInt165 = -1;
		anInt188 = -1;
		aByte205 = 0;
		anInt200 = -1;
		anInt164 = -1;
		aByte154 = 0;
		anInt185 = -1;
		anInt162 = -1;
		anInt175 = -1;
		anInt166 = -1;
		anInt197 = -1;
		anInt173 = -1;
		stackIDs = null;
		stackAmounts = null;
		certID = -1;
		certTemplateID = -1;
		anInt167 = 128;
		anInt192 = 128;
		anInt191 = 128;
		anInt196 = 0;
		anInt184 = 0;
		team = 0;
	}

	public void toNote() {
		ItemDef itemDef = getItemDefinition(certTemplateID);
		modelID = itemDef.modelID;
		modelZoom = itemDef.modelZoom;
		modelRotationY = itemDef.modelRotationY;
		modelRotationX = itemDef.modelRotationX;

		anInt204 = itemDef.anInt204;
		modelOffset1 = itemDef.modelOffset1;
		modelOffset2 = itemDef.modelOffset2;
		modifiedModelColors = itemDef.modifiedModelColors;
		originalModelColors = itemDef.originalModelColors;
		ItemDef itemDef_1 = getItemDefinition(certID);
		name = itemDef_1.name;
		membersObject = itemDef_1.membersObject;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
			s = "an";
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".").getBytes();
		stackable = true;
	}

	// Add this method to your ItemDef class to ensure variant items render properly

	public Model getStackedModel(int stackSize) {
		if (stackIDs != null && stackSize > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (stackSize >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).getStackedModel(1);
		}

		Model model = (Model) mruNodes2.insertFromCache(id);
		if (model != null)
			return model;

		model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;

		// Scale the model if needed
		if (anInt167 != 128 || anInt192 != 128 || anInt191 != 128)
			model.modelScale(anInt167, anInt191, anInt192);

		// CRITICAL: Apply color replacements for variant items
		if (modifiedModelColors != null && originalModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++) {
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);
				// Debug output to verify colors are being applied
				if (PetItemCreator.isVariantPetItem(id)) {
					System.out.println("Applying item color: " + modifiedModelColors[l] + " -> " + originalModelColors[l] + " for item " + id);
				}
			}
		}

		model.applyLighting(64 + anInt196, 768 + anInt184, -50, -10, -50, true);
		model.aBoolean1659 = true;
		mruNodes2.removeFromCache(model, id);
		return model;
	}

	public Model method202(int i) {
		if (stackIDs != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (i >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).method202(1);
		}
		Model model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);

		}
		return model;
	}

	public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1)
				modelID = stream.readUnsignedWord();
			else if (i == 2)
				name = stream.readString();
			else if (i == 3)
				description = stream.readBytes();
			else if (i == 4)
				modelZoom = stream.readUnsignedWord();
			else if (i == 5)
				modelRotationY = stream.readUnsignedWord();
			else if (i == 6)
				modelRotationX = stream.readUnsignedWord();
			else if (i == 7) {
				modelOffset1 = stream.readUnsignedWord();
				if (modelOffset1 > 32767)
					modelOffset1 -= 0x10000;
			} else if (i == 8) {
				modelOffset2 = stream.readUnsignedWord();
				if (modelOffset2 > 32767)
					modelOffset2 -= 0x10000;
			} else if (i == 10)
				stream.readUnsignedWord();
			else if (i == 11)
				stackable = true;
			else if (i == 12)
				value = stream.readDWord();
			else if (i == 16)
				membersObject = true;
			else if (i == 23) {
				anInt165 = stream.readUnsignedWord();
				aByte205 = stream.readSignedByte();
			} else if (i == 24)
				anInt188 = stream.readUnsignedWord();
			else if (i == 25) {
				anInt200 = stream.readUnsignedWord();
				aByte154 = stream.readSignedByte();
			} else if (i == 26)
				anInt164 = stream.readUnsignedWord();
			else if (i >= 30 && i < 35) {
				if (groundActions == null)
					groundActions = new String[5];
				groundActions[i - 30] = stream.readString();
				if (groundActions[i - 30].equalsIgnoreCase("hidden"))
					groundActions[i - 30] = null;
			} else if (i >= 35 && i < 40) {
				if (itemActions == null)
					itemActions = new String[5];
				itemActions[i - 35] = stream.readString();
			} else if (i == 40) {
				int j = stream.readUnsignedByte();
				originalModelColors = new int[j];
				modifiedModelColors = new int[j];
				for (int k = 0; k < j; k++) {
					originalModelColors[k] = stream.readUnsignedWord();
					modifiedModelColors[k] = stream.readUnsignedWord();
				}

			} else if (i == 78)
				anInt185 = stream.readUnsignedWord();
			else if (i == 79)
				anInt162 = stream.readUnsignedWord();
			else if (i == 90)
				anInt175 = stream.readUnsignedWord();
			else if (i == 91)
				anInt197 = stream.readUnsignedWord();
			else if (i == 92)
				anInt166 = stream.readUnsignedWord();
			else if (i == 93)
				anInt173 = stream.readUnsignedWord();
			else if (i == 95)
				anInt204 = stream.readUnsignedWord();
			else if (i == 97)
				certID = stream.readUnsignedWord();
			else if (i == 98)
				certTemplateID = stream.readUnsignedWord();
			else if (i == 100) {
				int length = stream.readUnsignedByte();
				stackIDs = new int[length];
				stackAmounts = new int[length];
				for (int i2 = 0; i2 < length; i2++) {
					stackIDs[i2] = stream.readUnsignedWord();
					stackAmounts[i2] = stream.readUnsignedWord();
				}
			} else if (i == 110)
				anInt167 = stream.readUnsignedWord();
			else if (i == 111)
				anInt192 = stream.readUnsignedWord();
			else if (i == 112)
				anInt191 = stream.readUnsignedWord();
			else if (i == 113)
				anInt196 = stream.readSignedByte();
			else if (i == 114)
				anInt184 = stream.readSignedByte() * 5;
			else if (i == 115)
				team = stream.readUnsignedByte();
		} while (true);
	}

	public static boolean isValid(int id) {
		return id > 0 && id < totalItems;
	}

	public static boolean isRenderable(int id) {
		try {
			if (!isValid(id)) return false;
			ItemDef def = getItemDefinition(id);
			return def != null && def.modelID > 0;
		} catch (Exception e) {
			return false;
		}
	}


}

