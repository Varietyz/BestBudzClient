package com.bestbudz.ui;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import bestbudz.config.InterfaceConfig;
import bestbudz.config.InterfaceEntry;
import bestbudz.config.SpriteSlot;
import com.bestbudz.cache.FlatBufferConfigLoader;
import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.ui.interfaces.CustomInterfaces;
import com.bestbudz.util.LRUCache;
import com.bestbudz.graphics.text.TextClass;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;

public class RSInterface {

	private static final LRUCache<Model> aMRUNodes_264 = new LRUCache<>(30);
	public static RSInterface[] interfaceCache;
	public static RSInterface currentInputField = null;
	public static LRUCache<Sprite> aMRUNodes_238;
	public boolean drawsTransparent;
	public Sprite disabledSprite;
	public int anInt208;
	public boolean boxhover;
	public Sprite[] sprites;
	public int[] anIntArray212;
	public int contentType;
	public int[] spritesX;
	public int textHoverColor;
	public int atActionType;
	public String spellName;
	public int anInt219;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centerText;
	public int scrollPosition;
	public String[] actions;
	public int[][] valueIndexArray;
	public boolean aBoolean227;
	public String enabledMessage;
	public int hoverType;
	public int invSpritePadX;
	public int textColor;
	public int modelType;
	public int mediaID;
	public boolean aBoolean235;
	public int parentID;
	public int spellUsableOn;
	public int anInt239;
	public int[] children;
	public int[] childX;
	public boolean usableItemInterface;
	public TextDrawingArea textDrawingAreas;
	public int invSpritePadY;
	public int[] anIntArray245;
	public int anInt246;
	public int[] spritesY;
	public String disabledMessage;
	public boolean displayAsterisks;
	public boolean onlyNumbers;
	public boolean isBoxInterface;
	public int id;
	public int[] invStackSizes;
	public int[] inv;
	public byte opacity;
	public int verticalOffset;
	public int anInt258;
	public boolean aBoolean259;
	public Sprite enabledSprite;
	public int scrollMax;
	public int type;
	public int anInt263;
	public int positionScroll;
	public boolean isMouseoverTriggered;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int[] childY;
	public Sprite disabledHover;
	public String popupString;
	public String hoverText;
	public int anInt255;
	public int anInt256;

	public RSInterface() {

	}

	public static void unpack(TextDrawingArea[] textDrawingAreas) {
		aMRUNodes_238 = new LRUCache<>(50000);
		interfaceCache = new RSInterface[70_000];

		// Try FlatBuffer first
		ByteBuffer buf = FlatBufferConfigLoader.load("interfaces.fb");
		if (buf != null) {
			unpackFromFlatBuffer(buf, textDrawingAreas);
		} else {
			unpackFromJson(textDrawingAreas);
		}

		try { constructLunar(); } catch (Exception e) { System.err.println("Error in constructLunar: " + e.getMessage()); }
		try { configureLunar(textDrawingAreas); } catch (Exception e) { System.err.println("Error in configureLunar: " + e.getMessage()); }
		try { homeTeleport(textDrawingAreas); } catch (Exception e) { System.err.println("Error in homeTeleport: " + e.getMessage()); }
		try { itemsOnDeathDATA(textDrawingAreas); } catch (Exception e) { System.err.println("Error in itemsOnDeathDATA: " + e.getMessage()); }
		try { CustomInterfaces.unpackInterfaces(textDrawingAreas); } catch (Exception e) { System.err.println("Error in CustomInterfaces: " + e.getMessage()); }

		aMRUNodes_238 = null;
	}

	private static void unpackFromFlatBuffer(ByteBuffer buf, TextDrawingArea[] textDrawingAreas) {
		InterfaceConfig config = InterfaceConfig.getRootAsInterfaceConfig(buf);
		int loaded = 0;

		for (int i = 0; i < config.entriesLength(); i++) {
			try {
				InterfaceEntry fb = config.entries(i);
				int k = fb.id();
				if (k < 0 || k >= interfaceCache.length) continue;

				RSInterface rsInterface = interfaceCache[k] = new RSInterface();
				rsInterface.id = k;
				rsInterface.parentID = fb.parentId();
				rsInterface.type = fb.type();
				rsInterface.atActionType = fb.atActionType();
				rsInterface.contentType = fb.contentType();
				rsInterface.width = fb.width();
				rsInterface.height = fb.height();
				rsInterface.opacity = (byte) fb.opacity();
				rsInterface.hoverType = fb.hoverType();

				// Comparator ops + values
				if (fb.comparatorOpsLength() > 0) {
					int count = fb.comparatorOpsLength();
					rsInterface.anIntArray245 = new int[count];
					rsInterface.anIntArray212 = new int[count];
					for (int j = 0; j < count; j++) {
						rsInterface.anIntArray245[j] = fb.comparatorOps(j);
						rsInterface.anIntArray212[j] = j < fb.comparatorValuesLength() ? fb.comparatorValues(j) : 0;
					}
				}

				// Value index arrays (stored as comma-separated strings)
				if (fb.valueIndexesLength() > 0) {
					rsInterface.valueIndexArray = new int[fb.valueIndexesLength()][];
					for (int j = 0; j < fb.valueIndexesLength(); j++) {
						String csv = fb.valueIndexes(j);
						if (csv != null && !csv.isEmpty()) {
							String[] parts = csv.split(",");
							rsInterface.valueIndexArray[j] = new int[parts.length];
							for (int m = 0; m < parts.length; m++)
								rsInterface.valueIndexArray[j][m] = Integer.parseInt(parts[m]);
						} else {
							rsInterface.valueIndexArray[j] = new int[0];
						}
					}
				}

				// Type 0: Container
				if (rsInterface.type == 0) {
					rsInterface.drawsTransparent = false;
					rsInterface.scrollMax = fb.scrollMax();
					rsInterface.isMouseoverTriggered = fb.mouseoverTriggered();
					if (fb.childrenLength() > 0) {
						int childCount = fb.childrenLength();
						rsInterface.children = new int[childCount];
						rsInterface.childX = new int[childCount];
						rsInterface.childY = new int[childCount];
						for (int j = 0; j < childCount; j++) {
							rsInterface.children[j] = fb.children(j);
							rsInterface.childX[j] = j < fb.childXLength() ? fb.childX(j) : 0;
							rsInterface.childY[j] = j < fb.childYLength() ? fb.childY(j) : 0;
						}
					}
				}

				// Type 2: Inventory
				if (rsInterface.type == 2) {
					int slotCount = rsInterface.width * rsInterface.height;
					if (slotCount > 0 && slotCount < 10000) {
						rsInterface.inv = new int[slotCount];
						rsInterface.invStackSizes = new int[slotCount];
					}
					rsInterface.aBoolean259 = fb.replaceItems();
					rsInterface.isBoxInterface = fb.isBank();
					rsInterface.usableItemInterface = fb.usableItems();
					rsInterface.aBoolean235 = fb.aBoolean235();
					rsInterface.invSpritePadX = fb.invSpritePadX();
					rsInterface.invSpritePadY = fb.invSpritePadY();

					// Sprite slots
					rsInterface.spritesX = new int[20];
					rsInterface.spritesY = new int[20];
					rsInterface.sprites = new Sprite[20];
					for (int j = 0; j < fb.spriteSlotsLength(); j++) {
						SpriteSlot slot = fb.spriteSlots(j);
						int slotIdx = slot.slot();
						if (slotIdx >= 0 && slotIdx < 20) {
							rsInterface.spritesX[slotIdx] = slot.x();
							rsInterface.spritesY[slotIdx] = slot.y();
							String spriteRef = slot.sprite();
							if (spriteRef != null && !spriteRef.isEmpty()) {
								rsInterface.sprites[slotIdx] = loadSpriteFromRef(spriteRef);
							}
						}
					}

					// Actions
					rsInterface.actions = new String[5];
					for (int j = 0; j < 5 && j < fb.actionsLength(); j++) {
						String act = fb.actions(j);
						if (act != null && !act.isEmpty())
							rsInterface.actions[j] = act;
					}
					// Shop/equip overrides
					if (rsInterface.parentID == 3824 && rsInterface.actions.length > 4)
						rsInterface.actions[4] = "Buy X";
					if (rsInterface.parentID == 3822 && rsInterface.actions.length > 4)
						rsInterface.actions[4] = "Sell X";
					if (rsInterface.parentID == 1644 && rsInterface.actions.length > 2)
						rsInterface.actions[2] = "Operate";
				}

				// Type 3: Rectangle
				if (rsInterface.type == 3) {
					rsInterface.aBoolean227 = fb.filled();
				}

				// Type 4 / Type 1: Text properties
				if (rsInterface.type == 4 || rsInterface.type == 1) {
					rsInterface.centerText = fb.centerText();
					int fontIdx = fb.fontId();
					if (fontIdx >= 0 && textDrawingAreas != null && fontIdx < textDrawingAreas.length)
						rsInterface.textDrawingAreas = textDrawingAreas[fontIdx];
					rsInterface.textShadow = fb.textShadow();
				}

				// Type 4: Text messages
				if (rsInterface.type == 4) {
					String dm = fb.disabledMessage();
					rsInterface.disabledMessage = dm != null ? dm.replaceAll("RuneScape", EngineConfig.TITLE) : "";
					String em = fb.enabledMessage();
					rsInterface.enabledMessage = em != null ? em : "";
				}

				// Types 1, 3, 4: textColor
				if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4) {
					rsInterface.textColor = fb.textColor();
				}

				// Types 3, 4: color variants
				if (rsInterface.type == 3 || rsInterface.type == 4) {
					rsInterface.anInt219 = fb.enabledColor();
					rsInterface.textHoverColor = fb.textHoverColor();
					rsInterface.anInt239 = fb.enabledHoverColor();
				}

				// Type 5: Sprite
				if (rsInterface.type == 5) {
					rsInterface.drawsTransparent = false;
					String disRef = fb.disabledSprite();
					if (disRef != null && !disRef.isEmpty()) {
						rsInterface.disabledSprite = loadSpriteFromRef(disRef);
					}
					String enRef = fb.enabledSprite();
					if (enRef != null && !enRef.isEmpty()) {
						rsInterface.enabledSprite = loadSpriteFromRef(enRef);
					}
				}

				// Type 6: Model
				if (rsInterface.type == 6) {
					if (fb.modelType() != 0) {
						rsInterface.modelType = fb.modelType();
						rsInterface.mediaID = fb.mediaId();
					}
					if (fb.enabledModelType() != 0) {
						rsInterface.anInt255 = fb.enabledModelType();
						rsInterface.anInt256 = fb.enabledMediaId();
					}
					rsInterface.verticalOffset = fb.verticalOffset();
					rsInterface.anInt258 = fb.enabledVerticalOffset();
					rsInterface.modelZoom = fb.modelZoom();
					rsInterface.modelRotation1 = fb.modelRotation1();
					rsInterface.modelRotation2 = fb.modelRotation2();
				}

				// Type 7: Inventory text
				if (rsInterface.type == 7) {
					int slotCount = rsInterface.width * rsInterface.height;
					if (slotCount > 0 && slotCount < 10000) {
						rsInterface.inv = new int[slotCount];
						rsInterface.invStackSizes = new int[slotCount];
					}
					rsInterface.centerText = fb.centerText();
					int fontIdx = fb.fontId();
					if (fontIdx >= 0 && textDrawingAreas != null && fontIdx < textDrawingAreas.length)
						rsInterface.textDrawingAreas = textDrawingAreas[fontIdx];
					rsInterface.textShadow = fb.textShadow();
					rsInterface.textColor = fb.textColor();
					rsInterface.invSpritePadX = fb.invSpritePadX();
					rsInterface.invSpritePadY = fb.invSpritePadY();
					rsInterface.isBoxInterface = fb.isBank();
					rsInterface.actions = new String[5];
					for (int j = 0; j < 5 && j < fb.actionsLength(); j++) {
						String act = fb.actions(j);
						if (act != null && !act.isEmpty())
							rsInterface.actions[j] = act;
					}
				}

				// Spell / usable on (atActionType == 2 || type == 2)
				if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
					String san = fb.selectedActionName();
					rsInterface.selectedActionName = san != null ? san : "";
					String sn = fb.spellName();
					rsInterface.spellName = sn != null ? sn : "";
					rsInterface.spellUsableOn = fb.spellUsableOn();
				}

				// Type 8: Tooltip text
				if (rsInterface.type == 8) {
					String dm = fb.disabledMessage();
					rsInterface.disabledMessage = dm != null ? dm : "";
				}

				// Tooltip for action types 1, 4, 5, 6
				if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4
					|| rsInterface.atActionType == 5 || rsInterface.atActionType == 6) {
					String tt = fb.tooltip();
					rsInterface.tooltip = tt != null ? tt : "";
					if (rsInterface.tooltip.isEmpty()) {
						if (rsInterface.atActionType == 1) rsInterface.tooltip = "Ok";
						if (rsInterface.atActionType == 4) rsInterface.tooltip = "Select";
						if (rsInterface.atActionType == 5) rsInterface.tooltip = "Select";
						if (rsInterface.atActionType == 6) rsInterface.tooltip = "Continue";
					}
				}

				loaded++;
			} catch (Exception e) {
				System.err.println("Error loading interface from FB index " + i + ": " + e.getMessage());
			}
		}

		System.out.println("[RSInterface] Loaded " + loaded + " widgets from FlatBuffer");
	}

	private static void unpackFromJson(TextDrawingArea[] textDrawingAreas) {
		JsonObject allWidgets = JsonCacheLoader.loadJsonObject("interfaces.json");
		if (allWidgets == null) {
			System.err.println("[RSInterface] Failed to load interfaces.json");
			return;
		}

		int loaded = 0;
		for (java.util.Map.Entry<String, JsonElement> entry : allWidgets.entrySet()) {
			try {
				int k = Integer.parseInt(entry.getKey());
				if (k < 0 || k >= interfaceCache.length) {
					System.err.println("Interface ID out of bounds: " + k);
					continue;
				}

				JsonObject json = entry.getValue().getAsJsonObject();
				RSInterface rsInterface = interfaceCache[k] = new RSInterface();
				rsInterface.id = k;
				rsInterface.parentID = json.has("parentID") ? json.get("parentID").getAsInt() : -1;
				rsInterface.type = json.has("type") ? json.get("type").getAsInt() : 0;
				rsInterface.atActionType = json.has("atActionType") ? json.get("atActionType").getAsInt() : 0;
				rsInterface.contentType = json.has("contentType") ? json.get("contentType").getAsInt() : 0;
				rsInterface.width = json.has("width") ? json.get("width").getAsInt() : 0;
				rsInterface.height = json.has("height") ? json.get("height").getAsInt() : 0;
				rsInterface.opacity = json.has("opacity") ? (byte) json.get("opacity").getAsInt() : 0;

				if (json.has("hoverType") && json.get("hoverType").getAsInt() > 0) {
					rsInterface.hoverType = json.get("hoverType").getAsInt();
				} else {
					rsInterface.hoverType = -1;
				}

				// Comparator ops + values
				if (json.has("comparatorOps") && json.get("comparatorOps").isJsonArray()) {
					JsonArray opsArr = json.getAsJsonArray("comparatorOps");
					JsonArray valsArr = json.has("comparatorValues") && json.get("comparatorValues").isJsonArray() ? json.getAsJsonArray("comparatorValues") : null;
					int count = opsArr.size();
					rsInterface.anIntArray245 = new int[count];
					rsInterface.anIntArray212 = new int[count];
					for (int j = 0; j < count; j++) {
						rsInterface.anIntArray245[j] = opsArr.get(j).getAsInt();
						rsInterface.anIntArray212[j] = (valsArr != null && j < valsArr.size()) ? valsArr.get(j).getAsInt() : 0;
					}
				}

				// Value index arrays
				if (json.has("valueIndexes") && json.get("valueIndexes").isJsonArray()) {
					JsonArray outerArr = json.getAsJsonArray("valueIndexes");
					rsInterface.valueIndexArray = new int[outerArr.size()][];
					for (int j = 0; j < outerArr.size(); j++) {
						JsonArray innerArr = outerArr.get(j).getAsJsonArray();
						rsInterface.valueIndexArray[j] = new int[innerArr.size()];
						for (int m = 0; m < innerArr.size(); m++) {
							rsInterface.valueIndexArray[j][m] = innerArr.get(m).getAsInt();
						}
					}
				}

				// Type 0: Container
				if (rsInterface.type == 0) {
					rsInterface.drawsTransparent = false;
					rsInterface.scrollMax = json.has("scrollMax") ? json.get("scrollMax").getAsInt() : 0;
					rsInterface.isMouseoverTriggered = json.has("mouseoverTriggered") && json.get("mouseoverTriggered").getAsBoolean();
					if (json.has("children") && json.get("children").isJsonArray()) {
						JsonArray childArr = json.getAsJsonArray("children");
						JsonArray cxArr = json.has("childX") && json.get("childX").isJsonArray() ? json.getAsJsonArray("childX") : null;
						JsonArray cyArr = json.has("childY") && json.get("childY").isJsonArray() ? json.getAsJsonArray("childY") : null;
						int childCount = childArr.size();
						rsInterface.children = new int[childCount];
						rsInterface.childX = new int[childCount];
						rsInterface.childY = new int[childCount];
						for (int j = 0; j < childCount; j++) {
							rsInterface.children[j] = childArr.get(j).getAsInt();
							rsInterface.childX[j] = (cxArr != null && j < cxArr.size()) ? cxArr.get(j).getAsInt() : 0;
							rsInterface.childY[j] = (cyArr != null && j < cyArr.size()) ? cyArr.get(j).getAsInt() : 0;
						}
					}
				}

				// Type 2: Inventory
				if (rsInterface.type == 2) {
					int slotCount = rsInterface.width * rsInterface.height;
					if (slotCount > 0 && slotCount < 10000) {
						rsInterface.inv = new int[slotCount];
						rsInterface.invStackSizes = new int[slotCount];
					}
					rsInterface.aBoolean259 = json.has("replaceItems") && json.get("replaceItems").getAsBoolean();
					rsInterface.isBoxInterface = json.has("isBank") && json.get("isBank").getAsBoolean();
					rsInterface.usableItemInterface = json.has("usableItems") && json.get("usableItems").getAsBoolean();
					rsInterface.aBoolean235 = json.has("aBoolean235") && json.get("aBoolean235").getAsBoolean();
					rsInterface.invSpritePadX = json.has("invSpritePadX") ? json.get("invSpritePadX").getAsInt() : 0;
					rsInterface.invSpritePadY = json.has("invSpritePadY") ? json.get("invSpritePadY").getAsInt() : 0;

					// Sprite slots
					rsInterface.spritesX = new int[20];
					rsInterface.spritesY = new int[20];
					rsInterface.sprites = new Sprite[20];
					if (json.has("spriteSlots") && json.get("spriteSlots").isJsonArray()) {
						JsonArray slots = json.getAsJsonArray("spriteSlots");
						for (int j = 0; j < slots.size(); j++) {
							JsonObject slot = slots.get(j).getAsJsonObject();
							int slotIdx = slot.get("slot").getAsInt();
							if (slotIdx >= 0 && slotIdx < 20) {
								rsInterface.spritesX[slotIdx] = slot.has("x") ? slot.get("x").getAsInt() : 0;
								rsInterface.spritesY[slotIdx] = slot.has("y") ? slot.get("y").getAsInt() : 0;
								String spriteRef = slot.has("sprite") ? slot.get("sprite").getAsString() : "";
								if (!spriteRef.isEmpty()) {
									rsInterface.sprites[slotIdx] = loadSpriteFromRef(spriteRef);
								}
							}
						}
					}

					// Actions
					rsInterface.actions = new String[5];
					if (json.has("actions") && json.get("actions").isJsonArray()) {
						JsonArray actArr = json.getAsJsonArray("actions");
						for (int j = 0; j < 5 && j < actArr.size(); j++) {
							if (!actArr.get(j).isJsonNull()) {
								String act = actArr.get(j).getAsString();
								rsInterface.actions[j] = act.isEmpty() ? null : act;
							}
						}
					}
					// Shop/equip overrides
					if (rsInterface.parentID == 3824 && rsInterface.actions.length > 4)
						rsInterface.actions[4] = "Buy X";
					if (rsInterface.parentID == 3822 && rsInterface.actions.length > 4)
						rsInterface.actions[4] = "Sell X";
					if (rsInterface.parentID == 1644 && rsInterface.actions.length > 2)
						rsInterface.actions[2] = "Operate";
				}

				// Type 3: Rectangle
				if (rsInterface.type == 3) {
					rsInterface.aBoolean227 = json.has("filled") && json.get("filled").getAsBoolean();
				}

				// Type 4 / Type 1: Text properties
				if (rsInterface.type == 4 || rsInterface.type == 1) {
					rsInterface.centerText = json.has("centerText") && json.get("centerText").getAsBoolean();
					if (json.has("fontId")) {
						int fontIdx = json.get("fontId").getAsInt();
						if (textDrawingAreas != null && fontIdx >= 0 && fontIdx < textDrawingAreas.length)
							rsInterface.textDrawingAreas = textDrawingAreas[fontIdx];
					}
					rsInterface.textShadow = json.has("textShadow") && json.get("textShadow").getAsBoolean();
				}

				// Type 4: Text messages
				if (rsInterface.type == 4) {
					rsInterface.disabledMessage = json.has("disabledMessage")
						? json.get("disabledMessage").getAsString().replaceAll("RuneScape", EngineConfig.TITLE) : "";
					rsInterface.enabledMessage = json.has("enabledMessage") ? json.get("enabledMessage").getAsString() : "";
				}

				// Types 1, 3, 4: textColor
				if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4) {
					rsInterface.textColor = json.has("textColor") ? json.get("textColor").getAsInt() : 0;
				}

				// Types 3, 4: color variants
				if (rsInterface.type == 3 || rsInterface.type == 4) {
					rsInterface.anInt219 = json.has("enabledColor") ? json.get("enabledColor").getAsInt() : 0;
					rsInterface.textHoverColor = json.has("textHoverColor") ? json.get("textHoverColor").getAsInt() : 0;
					rsInterface.anInt239 = json.has("enabledHoverColor") ? json.get("enabledHoverColor").getAsInt() : 0;
				}

				// Type 5: Sprite
				if (rsInterface.type == 5) {
					rsInterface.drawsTransparent = false;
					if (json.has("disabledSprite")) {
						String ref = json.get("disabledSprite").getAsString();
						if (!ref.isEmpty()) {
							rsInterface.disabledSprite = loadSpriteFromRef(ref);
						}
					}
					if (json.has("enabledSprite")) {
						String ref = json.get("enabledSprite").getAsString();
						if (!ref.isEmpty()) {
							rsInterface.enabledSprite = loadSpriteFromRef(ref);
						}
					}
				}

				// Type 6: Model
				if (rsInterface.type == 6) {
					if (json.has("modelType")) {
						rsInterface.modelType = json.get("modelType").getAsInt();
						rsInterface.mediaID = json.has("mediaID") ? json.get("mediaID").getAsInt() : 0;
					}
					if (json.has("enabledModelType")) {
						rsInterface.anInt255 = json.get("enabledModelType").getAsInt();
						rsInterface.anInt256 = json.has("enabledMediaID") ? json.get("enabledMediaID").getAsInt() : 0;
					}
					rsInterface.verticalOffset = json.has("verticalOffset") ? json.get("verticalOffset").getAsInt() : -1;
					rsInterface.anInt258 = json.has("enabledVerticalOffset") ? json.get("enabledVerticalOffset").getAsInt() : -1;
					rsInterface.modelZoom = json.has("modelZoom") ? json.get("modelZoom").getAsInt() : 0;
					rsInterface.modelRotation1 = json.has("modelRotation1") ? json.get("modelRotation1").getAsInt() : 0;
					rsInterface.modelRotation2 = json.has("modelRotation2") ? json.get("modelRotation2").getAsInt() : 0;
				}

				// Type 7: Inventory text
				if (rsInterface.type == 7) {
					int slotCount = rsInterface.width * rsInterface.height;
					if (slotCount > 0 && slotCount < 10000) {
						rsInterface.inv = new int[slotCount];
						rsInterface.invStackSizes = new int[slotCount];
					}
					rsInterface.centerText = json.has("centerText") && json.get("centerText").getAsBoolean();
					if (json.has("fontId")) {
						int fontIdx = json.get("fontId").getAsInt();
						if (textDrawingAreas != null && fontIdx >= 0 && fontIdx < textDrawingAreas.length)
							rsInterface.textDrawingAreas = textDrawingAreas[fontIdx];
					}
					rsInterface.textShadow = json.has("textShadow") && json.get("textShadow").getAsBoolean();
					rsInterface.textColor = json.has("textColor") ? json.get("textColor").getAsInt() : 0;
					rsInterface.invSpritePadX = json.has("invSpritePadX") ? json.get("invSpritePadX").getAsInt() : 0;
					rsInterface.invSpritePadY = json.has("invSpritePadY") ? json.get("invSpritePadY").getAsInt() : 0;
					rsInterface.isBoxInterface = json.has("isBank") && json.get("isBank").getAsBoolean();
					rsInterface.actions = new String[5];
					if (json.has("actions") && json.get("actions").isJsonArray()) {
						JsonArray actArr = json.getAsJsonArray("actions");
						for (int j = 0; j < 5 && j < actArr.size(); j++) {
							if (!actArr.get(j).isJsonNull()) {
								String act = actArr.get(j).getAsString();
								rsInterface.actions[j] = act.isEmpty() ? null : act;
							}
						}
					}
				}

				// Spell / usable on (atActionType == 2 || type == 2)
				if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
					rsInterface.selectedActionName = json.has("selectedActionName") ? json.get("selectedActionName").getAsString() : "";
					rsInterface.spellName = json.has("spellName") ? json.get("spellName").getAsString() : "";
					rsInterface.spellUsableOn = json.has("spellUsableOn") ? json.get("spellUsableOn").getAsInt() : 0;
				}

				// Type 8: Tooltip text
				if (rsInterface.type == 8) {
					rsInterface.disabledMessage = json.has("disabledMessage") ? json.get("disabledMessage").getAsString() : "";
				}

				// Tooltip for action types 1, 4, 5, 6
				if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4
					|| rsInterface.atActionType == 5 || rsInterface.atActionType == 6) {
					rsInterface.tooltip = json.has("tooltip") ? json.get("tooltip").getAsString() : "";
					if (rsInterface.tooltip.isEmpty()) {
						if (rsInterface.atActionType == 1) rsInterface.tooltip = "Ok";
						if (rsInterface.atActionType == 4) rsInterface.tooltip = "Select";
						if (rsInterface.atActionType == 5) rsInterface.tooltip = "Select";
						if (rsInterface.atActionType == 6) rsInterface.tooltip = "Continue";
					}
				}

				loaded++;
			} catch (Exception e) {
				System.err.println("Error loading interface " + entry.getKey() + ": " + e.getMessage());
			}
		}

		System.out.println("[RSInterface] Loaded " + loaded + " widgets from JSON");
	}

	private static Sprite loadSpriteFromRef(String ref) {
		try {
			int commaIdx = ref.lastIndexOf(",");
			if (commaIdx > 0 && commaIdx < ref.length() - 1) {
				int spriteIndex = Integer.parseInt(ref.substring(commaIdx + 1));
				String spriteName = ref.substring(0, commaIdx);
				return method207(spriteIndex, spriteName);
			}
		} catch (Exception e) {
			System.err.println("Error loading sprite from ref: " + ref);
		}
		return null;
	}

	public static void setChildren(int total, RSInterface i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}

	public static Sprite imageLoader(int i, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = aMRUNodes_238.get(l);
		if (sprite != null)
			return sprite;
		return null;
	}

	public static void homeTeleport(TextDrawingArea[] TDA) {
		RSInterface rsi = interfaceCache[21741];
		rsi.atActionType = 1;
		rsi.tooltip = "Home teleport";
	}

	public static void addButton(int id, int sid, String tooltip) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[sid];
		tab.enabledSprite = Client.cacheSprite[sid];
		tab.width = tab.disabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight;
		tab.tooltip = tooltip;
	}

	public static void addTooltipBox(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = id;
		rsi.parentID = id;
		rsi.type = 8;
		rsi.popupString = text;
		rsi.disabledMessage = text;
	}

	public static void addText(int id, String text, TextDrawingArea[] tda, int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		if (centered)
			rsi.centerText = true;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.disabledMessage = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void addText(int id, String text, TextDrawingArea[] wid, int idx, int color) {
		RSInterface rsinterface = addTab(id);
		rsinterface.id = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 0;
		rsinterface.width = 174;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = false;
		rsinterface.textShadow = true;
		rsinterface.textDrawingAreas = wid[idx];
		rsinterface.disabledMessage = text;
		rsinterface.enabledMessage = "";
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.textHoverColor = 0;
		rsinterface.anInt239 = 0;
	}

	public static void addPriceChecker(int index) {
		RSInterface rsi = interfaceCache[index] = new RSInterface();
		rsi.actions = new String[10];
		rsi.spritesX = new int[20];
		rsi.invStackSizes = new int[28];
		rsi.inv = new int[28];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		rsi.actions[0] = "Grab 1";
		rsi.actions[1] = "Grab 5";
		rsi.actions[2] = "Grab 10";
		rsi.actions[3] = "Grab All";
		rsi.actions[4] = "Grab X";
		rsi.centerText = true;
		rsi.aBoolean227 = false;
		rsi.aBoolean235 = false;
		rsi.usableItemInterface = false;
		rsi.isBoxInterface = false;
		rsi.aBoolean259 = true;
		rsi.textShadow = false;
		rsi.invSpritePadX = 35;
		rsi.invSpritePadY = 30;
		rsi.height = 4;
		rsi.width = 7;
		rsi.parentID = 48542;
		rsi.id = 4393;
		rsi.type = 2;
	}

	public static void addText(int id, String text, TextDrawingArea[] tda, int idx, int color, boolean center,
							   boolean shadow) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.disabledMessage = text;
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textHoverColor = 0;
		tab.anInt239 = 0;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m, int a, TextDrawingArea[] TDA, int j) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.parentID = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = a;
		RSInterface.centerText = l;
		RSInterface.textShadow = m;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.disabledMessage = s;
		RSInterface.enabledMessage = "";
		RSInterface.textColor = k;
	}

	public static void addConfigButton(int ID, int pID, int bID, int bID2, int width, int height, String tT,
			int configID, int aT, int configFrame) {
		RSInterface Tab = addTabInterface(ID);
		Tab.parentID = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.anIntArray245 = new int[] { 1 };
		Tab.anIntArray212 = new int[] { configID };
		Tab.valueIndexArray = new int[][] { { 5, configFrame, configID } };
		Tab.disabledSprite = Client.cacheSprite[bID];
		Tab.enabledSprite = Client.cacheSprite[bID2];
		Tab.tooltip = tT;
	}

	public static void addSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(spriteId, spriteName);
		tab.enabledSprite = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text,
			int contentType, int hoverOver, int aT)
	{
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = imageLoader(j, imageName);
		tab.enabledSprite = imageLoader(j, imageName);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID)
	{
		RSInterface tab = addTabInterface(i);
		tab.parentID = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.isMouseoverTriggered = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[j];
		tab.enabledSprite = Client.cacheSprite[k];
	}

	protected static Sprite method207(int i, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = aMRUNodes_238.get(l);
		if (sprite != null)
			return sprite;
		try {
			String key = s + "_" + i;
			JsonObject mediaSpriteIndex = JsonCacheLoader.loadJsonObject("media_sprites/_index.json");
			if (mediaSpriteIndex != null && mediaSpriteIndex.has(key)) {
				JsonObject meta = mediaSpriteIndex.getAsJsonObject(key);
				String file = meta.get("file").getAsString();
				int width = meta.get("width").getAsInt();
				int height = meta.get("height").getAsInt();
				int offsetX = meta.has("offsetX") ? meta.get("offsetX").getAsInt() : 0;
				int offsetY = meta.has("offsetY") ? meta.get("offsetY").getAsInt() : 0;
				int fullWidth = meta.has("fullWidth") ? meta.get("fullWidth").getAsInt() : width;
				int fullHeight = meta.has("fullHeight") ? meta.get("fullHeight").getAsInt() : height;

				byte[] pngData = JsonCacheLoader.loadFileBytes("media_sprites/" + file);
				if (pngData != null) {
					java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(pngData);
					javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
					sprite = new Sprite(icon.getIconWidth(), icon.getIconHeight());
					sprite.drawOffsetX = offsetX;
					sprite.drawOffsetY = offsetY;
					sprite.originalWidth = fullWidth;
					sprite.originalHeight = fullHeight;
					java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(
						image, 0, 0, sprite.myWidth, sprite.myHeight, sprite.myPixels, 0, sprite.myWidth);
					pg.grabPixels();
					sprite.setTransparency(255, 0, 255);
				} else {
					sprite = createEmptySprite();
				}
			} else {
				sprite = createEmptySprite();
			}
			aMRUNodes_238.put(l, sprite);
		} catch (Exception _ex) {
			System.err.println("Failed to load sprite " + s + " index " + i + ": " + _ex.getMessage());
			sprite = createEmptySprite();
		}
		return sprite;
	}

	public static Sprite createEmptySprite() {
		return new Sprite();
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;
		int j = 5;
		if (flag)
			return;
		aMRUNodes_264.clear();
		if (model != null)
			aMRUNodes_264.put(((long) j << 16) + i, model);
	}

	public static void addLunarSprite(int i, int j) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.cacheSprite[j];
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}

	public static void drawRune(int i, int id) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.cacheSprite[id];
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	public static void addRuneText(int ID, int runeAmount, int RuneID, TextDrawingArea[] font) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 4;
		rsInterface.atActionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.anIntArray245 = new int[1];
		rsInterface.anIntArray212 = new int[1];
		rsInterface.anIntArray245[0] = 3;
		rsInterface.anIntArray212[0] = runeAmount;
		rsInterface.valueIndexArray = new int[1][4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = RuneID;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.centerText = true;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.disabledMessage = "%1/" + runeAmount;
		rsInterface.enabledMessage = "";
		rsInterface.textColor = 12582912;
		rsInterface.anInt219 = 49152;
	}

	public static void homeTeleport() {
		RSInterface RSInterface = addInterface(30000);
		RSInterface.tooltip = "Invoke @gre@Lunar Home Teleport";
		RSInterface.id = 30000;
		RSInterface.parentID = 30000;
		RSInterface.type = 5;
		RSInterface.atActionType = 5;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 30001;
		RSInterface.disabledSprite = Client.cacheSprite[141];
		RSInterface.width = 20;
		RSInterface.height = 20;
		RSInterface Int = addInterface(30001);
		Int.isMouseoverTriggered = true;
		Int.hoverType = -1;
		setChildren(1, Int);
		addLunarSprite(30002, 142);
		setBounds(30002, 0, 0, 0, Int);
	}

	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name,
			String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Invoke On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Invoke @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.anIntArray245 = new int[3];
		rsInterface.anIntArray212 = new int[3];
		rsInterface.anIntArray245[0] = 3;
		rsInterface.anIntArray212[0] = ra1;
		rsInterface.anIntArray245[1] = 3;
		rsInterface.anIntArray212[1] = ra2;
		rsInterface.anIntArray245[2] = 3;
		rsInterface.anIntArray212[2] = lvl;
		rsInterface.valueIndexArray = new int[3][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[3];
		rsInterface.valueIndexArray[2][0] = 1;
		rsInterface.valueIndexArray[2][1] = 6;
		rsInterface.valueIndexArray[2][2] = 0;
		rsInterface.disabledSprite = Client.cacheSprite[sid];
		rsInterface.enabledSprite = Client.cacheSprite[sid + 39];
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(7, INT);
		addLunarSprite(ID + 2, 143);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Grade " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 37, 35, 3, INT);
		setBounds(rune1, 112, 35, 4, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, INT);
	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Invoke on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Invoke @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.anIntArray245 = new int[4];
		rsInterface.anIntArray212 = new int[4];
		rsInterface.anIntArray245[0] = 3;
		rsInterface.anIntArray212[0] = ra1;
		rsInterface.anIntArray245[1] = 3;
		rsInterface.anIntArray212[1] = ra2;
		rsInterface.anIntArray245[2] = 3;
		rsInterface.anIntArray212[2] = ra3;
		rsInterface.anIntArray245[3] = 3;
		rsInterface.anIntArray212[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.disabledSprite = Client.cacheSprite[sid];
		rsInterface.enabledSprite = Client.cacheSprite[sid + 39];
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 144);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Grade " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 14, 35, 3, INT);
		setBounds(rune1, 74, 35, 4, INT);
		setBounds(rune2, 130, 35, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, INT);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Invoke on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Invoke @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.anIntArray245 = new int[4];
		rsInterface.anIntArray212 = new int[4];
		rsInterface.anIntArray245[0] = 3;
		rsInterface.anIntArray212[0] = ra1;
		rsInterface.anIntArray245[1] = 3;
		rsInterface.anIntArray212[1] = ra2;
		rsInterface.anIntArray245[2] = 3;
		rsInterface.anIntArray212[2] = ra3;
		rsInterface.anIntArray245[3] = 3;
		rsInterface.anIntArray212[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.disabledSprite = Client.cacheSprite[sid];
		rsInterface.enabledSprite = Client.cacheSprite[sid + 39];
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 145);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Grade " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, INT);
		setBounds(30016, 14, 48, 3, INT);
		setBounds(rune1, 74, 48, 4, INT);
		setBounds(rune2, 130, 48, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, INT);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Invoke on";
		rsInterface.height = 20;
		rsInterface.tooltip = "Invoke @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.anIntArray245 = new int[4];
		rsInterface.anIntArray212 = new int[4];
		rsInterface.anIntArray245[0] = 3;
		rsInterface.anIntArray212[0] = ra1;
		rsInterface.anIntArray245[1] = 3;
		rsInterface.anIntArray212[1] = ra2;
		rsInterface.anIntArray245[2] = 3;
		rsInterface.anIntArray212[2] = ra3;
		rsInterface.anIntArray245[3] = 3;
		rsInterface.anIntArray212[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.disabledSprite = Client.cacheSprite[sid];
		rsInterface.enabledSprite = Client.cacheSprite[sid + 39];
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 145);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Grade " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 34, 2, INT);
		setBounds(30016, 14, 61, 3, INT);
		setBounds(rune1, 74, 61, 4, INT);
		setBounds(rune2, 130, 61, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, INT);
	}

	public static void configureLunar(TextDrawingArea[] TDA) {
		homeTeleport();
		constructLunar();
		drawRune(30003, 146);
		drawRune(30004, 147);
		drawRune(30005, 148);
		drawRune(30006, 149);
		drawRune(30007, 150);
		drawRune(30008, 151);
		drawRune(30009, 152);
		drawRune(30010, 153);
		drawRune(30011, 154);
		drawRune(30012, 155);
		drawRune(30013, 156);
		drawRune(30014, 157);
		drawRune(30015, 158);
		drawRune(30016, 159);
		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Cook Pie",
				"Cook pies without a stove", TDA, 160, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 189, 7, 30006, 65, "Cure Weed", "Cure your harvest.",
				TDA,
				161, 4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster identifier",
				"Detect the combat statistics of a\\nmonster", TDA, 162, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "Schizophrenic",
				"Speak with varied voices in ur head", TDA, 163, 0, 2);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned stoners",
				TDA, 164, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Rain Dance",
				"Fills certain vessels with water", TDA, 165, 0, 5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Moonclan Teleport",
				"Teleports you to moonclan island", TDA, 166, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Tele Group Moonclan",
				"Teleports stoners to Moonclan\\nisland", TDA, 167, 0, 5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Ourania Teleport",
				"Teleports you to ourania rune altar", TDA, 168, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", TDA, 169, 0,
				5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", TDA, 170,
				0, 5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "Waterbirth Teleport",
				"Teleports you to Waterbirth island", TDA, 171, 0, 5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth",
				"Teleports stoners to Waterbirth\\nisland", TDA, 172, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group",
				"Cures Poison on stoners", TDA, 173, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy",
				"Invoke on another stoner to see their\\nskill grades", TDA, 174, 8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport",
				"Teleports you to the Barbarian\\noutpost", TDA, 175, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian",
				"Teleports stoners to the Barbarian\\noutpost", TDA, 176, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make",
				"Make glass without a furnace", TDA, 177, 16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport",
				"Teleports you to Port khazard", TDA, 178, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard",
				"Teleports stoners to Port khazard", TDA, 179, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Sleep",
				"Take a rest and restore life 3\\n times faster", TDA, 180, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery",
				"String amulets without wool", TDA, 181, 0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot\\nShare",
				"Share a potion with up to 4 nearby\\nstoners", TDA, 182, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Mage Imbue",
				"Combine runes without a talisman", TDA, 183, 0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil",
				"Fertilise a bankstanding patch with super\\ncompost", TDA, 184, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share",
				"Shares a potion with up to 4 nearby\\nstoners", TDA, 185, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fisher Guild Teleport",
				"Teleports you to the fisher guild", TDA, 186, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fisher\\nGuild",
				"Teleports stoners to the Fisher\\nGuild", TDA, 187, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks",
				TDA, 188, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catweedy Teleport",
				"Teleports you to Catweedy", TDA, 189, 0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catweedy",
				"Teleports stoners to Catweedy", TDA, 190, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport",
				"Teleports you to Ice Plateau", TDA, 191, 0, 5);
		addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau",
				"Teleports stoners to Ice Plateau", TDA, 192, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer",
				"Spend HP and SA energy to\\n give another SA and run energy", TDA, 193, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other",
				"Transfer up to 75% of life\\n to another stoner", TDA, 194, 8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other",
				"Allows another stoner to rebound\\ndamage to an opponent", TDA, 195, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance",
				"Rebound damage to an opponent", TDA, 196, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group",
				"Transfer up to 75% of life\\n to a group", TDA, 197, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap",
				"Change to another spellbook for 1\\nspell cast", TDA, 198, 0, 5);
	}

	public static void constructLunar() {
		RSInterface Interface = addTabInterface(29999);
		setChildren(80, Interface);
		setBounds(30000, 11, 10, 0, Interface);
		setBounds(30017, 40, 9, 1, Interface);
		setBounds(30025, 71, 12, 2, Interface);
		setBounds(30032, 103, 10, 3, Interface);
		setBounds(30040, 135, 12, 4, Interface);
		setBounds(30048, 165, 10, 5, Interface);
		setBounds(30056, 8, 38, 6, Interface);
		setBounds(30064, 39, 39, 7, Interface);
		setBounds(30075, 71, 39, 8, Interface);
		setBounds(30083, 103, 39, 9, Interface);
		setBounds(30091, 135, 39, 10, Interface);
		setBounds(30099, 165, 37, 11, Interface);
		setBounds(30106, 12, 68, 12, Interface);
		setBounds(30114, 42, 68, 13, Interface);
		setBounds(30122, 71, 68, 14, Interface);
		setBounds(30130, 103, 68, 15, Interface);
		setBounds(30138, 135, 68, 16, Interface);
		setBounds(30146, 165, 68, 17, Interface);
		setBounds(30154, 14, 97, 18, Interface);
		setBounds(30162, 42, 97, 19, Interface);
		setBounds(30170, 71, 97, 20, Interface);
		setBounds(30178, 101, 97, 21, Interface);
		setBounds(30186, 135, 98, 22, Interface);
		setBounds(30194, 168, 98, 23, Interface);
		setBounds(30202, 11, 125, 24, Interface);
		setBounds(30210, 42, 124, 25, Interface);
		setBounds(30218, 74, 125, 26, Interface);
		setBounds(30226, 103, 125, 27, Interface);
		setBounds(30234, 135, 125, 28, Interface);
		setBounds(30242, 164, 126, 29, Interface);
		setBounds(30250, 10, 155, 30, Interface);
		setBounds(30258, 42, 155, 31, Interface);
		setBounds(30266, 71, 155, 32, Interface);
		setBounds(30274, 103, 155, 33, Interface);
		setBounds(30282, 136, 155, 34, Interface);
		setBounds(30290, 165, 155, 35, Interface);
		setBounds(30298, 13, 185, 36, Interface);
		setBounds(30306, 42, 185, 37, Interface);
		setBounds(30314, 71, 184, 38, Interface);
		setBounds(30322, 104, 184, 39, Interface);
		setBounds(30001, 6, 184, 40, Interface);
		setBounds(30018, 5, 176, 41, Interface);
		setBounds(30026, 5, 176, 42, Interface);
		setBounds(30033, 5, 163, 43, Interface);
		setBounds(30041, 5, 176, 44, Interface);
		setBounds(30049, 5, 176, 45, Interface);
		setBounds(30057, 5, 176, 46, Interface);
		setBounds(30065, 5, 176, 47, Interface);
		setBounds(30076, 5, 163, 48, Interface);
		setBounds(30084, 5, 176, 49, Interface);
		setBounds(30092, 5, 176, 50, Interface);
		setBounds(30100, 5, 176, 51, Interface);
		setBounds(30107, 5, 176, 52, Interface);
		setBounds(30115, 5, 163, 53, Interface);
		setBounds(30123, 5, 176, 54, Interface);
		setBounds(30131, 5, 163, 55, Interface);
		setBounds(30139, 5, 163, 56, Interface);
		setBounds(30147, 5, 163, 57, Interface);
		setBounds(30155, 5, 176, 58, Interface);
		setBounds(30163, 5, 176, 59, Interface);
		setBounds(30171, 5, 176, 60, Interface);
		setBounds(30179, 5, 163, 61, Interface);
		setBounds(30187, 5, 176, 62, Interface);
		setBounds(30195, 5, 149, 63, Interface);
		setBounds(30203, 5, 176, 64, Interface);
		setBounds(30211, 5, 163, 65, Interface);
		setBounds(30219, 5, 163, 66, Interface);
		setBounds(30227, 5, 176, 67, Interface);
		setBounds(30235, 5, 149, 68, Interface);
		setBounds(30243, 5, 176, 69, Interface);
		setBounds(30251, 5, 5, 70, Interface);
		setBounds(30259, 5, 5, 71, Interface);
		setBounds(30267, 5, 5, 72, Interface);
		setBounds(30275, 5, 5, 73, Interface);
		setBounds(30283, 5, 5, 74, Interface);
		setBounds(30291, 5, 5, 75, Interface);
		setBounds(30299, 5, 5, 76, Interface);
		setBounds(30307, 5, 5, 77, Interface);
		setBounds(30323, 5, 5, 78, Interface);
		setBounds(30315, 5, 5, 79, Interface);
	}

	public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
		RSinterface.children[frame] = ID;
		RSinterface.childX[frame] = X;
		RSinterface.childY[frame] = Y;
	}

	public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = imageLoader(j, name);
		RSInterface.enabledSprite = imageLoader(j, name);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;
	}

	public static void addHoverText(int id, String text, String tooltip, TextDrawingArea[] tda, int idx, int color,
									boolean center, boolean textShadow, int width) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.disabledMessage = text;
		rsinterface.enabledMessage = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.textHoverColor = 0xFFFFFF;
		rsinterface.anInt239 = 0;
	}

	public static void addText(int id, String text, TextDrawingArea[] tda, int idx, int color, boolean center,
							   boolean shadow, int contentType, int actionType) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = actionType;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.disabledMessage = text;
		tab.enabledMessage = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textHoverColor = 0;
		tab.anInt239 = 0;
	}

	public static void addHoverButton(int i, Sprite sprite, int width, int height, String text, int contentType,
			int hoverOver, int aT) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = sprite;
		tab.enabledSprite = sprite;
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	protected static void addHoverButton(int id, int j, int width, int height, String text, int anInt214, int hoverOver,
			int aT) {
		RSInterface component = addTabInterface(id);
		component.id = id;
		component.parentID = id;
		component.type = 5;
		component.atActionType = aT;
		component.contentType = anInt214;
		component.opacity = 0;
		component.hoverType = hoverOver;

		if (j >= 0) {
			component.disabledSprite = Client.cacheSprite[j];
			component.enabledSprite = Client.cacheSprite[j];
		}

		component.width = width;
		component.height = height;
		component.tooltip = text;
	}

	public static void addHoverButton(int i, Sprite sprite, int j, int width, int height, String text, int contentType,
			int hoverOver, int aT) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = sprite;
		tab.enabledSprite = sprite;
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addContainer(int id, int contentType, int width, int height, String... actions) {
		RSInterface container = addInterface(id);
		container.parentID = id;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.invSpritePadX = 16;
		container.invSpritePadY = 4;
		container.inv = new int[width * height];
		container.invStackSizes = new int[width * height];
		container.aBoolean259 = true;
		container.actions = actions;
	}

	public static void addButton(int id, Sprite enabled, Sprite disabled, String tooltip, int w, int h) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = disabled;
		tab.enabledSprite = enabled;
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addConfigButton(int ID, int pID, Sprite disabled, Sprite enabled, int width, int height,
			String tT, int configID, int aT, int configFrame) {
		RSInterface Tab = addTabInterface(ID);
		Tab.parentID = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.anIntArray245 = new int[1];
		Tab.anIntArray212 = new int[1];
		Tab.anIntArray245[0] = 1;
		Tab.anIntArray212[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite = disabled;
		Tab.enabledSprite = enabled;
		Tab.tooltip = tT;
	}

	public static void addHoveredConfigButton(RSInterface original, int ID, int IMAGEID, int disabledID,
			int enabledID) {
		RSInterface rsint = addTabInterface(ID);
		rsint.parentID = original.id;
		rsint.id = ID;
		rsint.type = 0;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.width = original.width;
		rsint.height = original.height;
		rsint.opacity = 0;
		rsint.hoverType = -1;
		RSInterface hover = addInterface(IMAGEID);
		hover.type = 5;
		hover.width = original.width;
		hover.height = original.height;
		hover.anIntArray245 = original.anIntArray245;
		hover.anIntArray212 = original.anIntArray212;
		hover.valueIndexArray = original.valueIndexArray;
		hover.disabledSprite = Client.cacheSprite[disabledID];
		hover.enabledSprite = Client.cacheSprite[enabledID];
		rsint.totalChildren(1);
		setBounds(IMAGEID, 0, 0, 0, rsint);
		rsint.tooltip = original.tooltip;
		rsint.isMouseoverTriggered = true;
	}

	public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height,
			String tooltip, int[] anIntArray245, int[] anIntArray212, int[][] valueIndexArray) {
		RSInterface rsint = addTabInterface(id);
		rsint.parentID = id;
		rsint.id = id;
		rsint.type = 5;
		rsint.atActionType = 5;
		rsint.contentType = 206;
		rsint.width = width;
		rsint.height = height;
		rsint.opacity = 0;
		rsint.hoverType = hoverOver;
		rsint.anIntArray245 = anIntArray245;
		rsint.anIntArray212 = anIntArray212;
		rsint.valueIndexArray = valueIndexArray;
		rsint.disabledSprite = Client.cacheSprite[disabledID];
		rsint.enabledSprite = Client.cacheSprite[enabledID];
		rsint.tooltip = tooltip;
	}

	public static void addSprite(int id, int spriteId) {
		RSInterface rsint = interfaceCache[id] = new RSInterface();
		rsint.id = id;
		rsint.parentID = id;
		rsint.type = 5;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.opacity = 0;
		rsint.hoverType = 0;

		if (spriteId != -1) {
			rsint.disabledSprite = Client.cacheSprite[spriteId];
			rsint.enabledSprite = Client.cacheSprite[spriteId];
		}

		rsint.width = 0;
		rsint.height = 0;
	}

	public static void addSprite(int id, Sprite sprite) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = sprite;
		tab.enabledSprite = sprite;
		tab.width = sprite.myWidth;
		tab.height = sprite.myHeight;
	}

	public static void drawTooltip(int id, String text) {
		RSInterface rsinterface = addTabInterface(id);
		rsinterface.parentID = id;
		rsinterface.type = 0;
		rsinterface.isMouseoverTriggered = true;
		rsinterface.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsinterface.totalChildren(1);
		rsinterface.child(0, id + 1, 0, 0);
	}

	public static void addHoveredButton(int i, Sprite sprite, int w, int h, int IMAGEID)
	{
		RSInterface tab = addTabInterface(i);
		tab.parentID = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.isMouseoverTriggered = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, sprite, sprite);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	protected static void addHoveredButton(int i, int j, int w, int h, int IMAGEID) {
		RSInterface component = addTabInterface(i);
		component.parentID = i;
		component.id = i;
		component.type = 0;
		component.atActionType = 0;
		component.width = w;
		component.height = h;
		component.isMouseoverTriggered = true;
		component.opacity = 0;
		component.contentType = -1;
		component.scrollMax = 0;
		addHoverImage(IMAGEID, j, j);
		component.totalChildren(1);
		component.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, Sprite disabled, Sprite enabled) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = disabled;
		tab.enabledSprite = enabled;
	}

	public static void addChar(int ID, int zoom) {
		RSInterface t = interfaceCache[ID] = new RSInterface();
		t.id = ID;
		t.parentID = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 328;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.hoverType = 0;
		t.modelZoom = zoom;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.verticalOffset = -1;
		t.anInt258 = -1;
	}

	public static void addHoverText(int id, String text, String tooltip, TextDrawingArea[] tda, int idx, int color,
									boolean center, boolean textShadow, int width, int hoveredColor) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.disabledMessage = text;
		rsinterface.enabledMessage = "";
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.textHoverColor = hoveredColor;
		rsinterface.anInt239 = 0;
		if (EngineConfig.DEBUG_MODE) {
			rsinterface.tooltip = tooltip + ", " + rsinterface.id;
		} else {
			rsinterface.tooltip = tooltip;
		}
	}

	public static void itemsOnDeathDATA(TextDrawingArea[] wid)
	{
		RSInterface rsinterface = addInterface(17115);
		addText(17109, "7", wid, 0, 0xff981f);
		addText(17110, "b", wid, 0, 0xff981f);
		addText(17111, "c", wid, 0, 0xff981f);
		addText(17112, "8", wid, 0, 0xff981f);
		addText(17113, "e", wid, 0, 0xff981f);
		addText(17114, "f", wid, 0, 0xff981f);
		addText(17117, "9", wid, 0, 0xff981f);
		addText(17118, "h", wid, 0, 0xff981f);
		addText(17119, "i", wid, 0, 0xff981f);
		addText(17120, "10", wid, 0, 0xff981f);
		addText(17121, "k", wid, 0, 0xff981f);
		addText(17122, "l", wid, 0, 0xff981f);
		addText(17123, "11", wid, 0, 0xff981f);
		addText(17124, "n", wid, 0, 0xff981f);
		addText(17125, "o", wid, 0, 0xff981f);
		addText(17126, "12", wid, 0, 0xff981f);
		addText(17127, "q", wid, 0, 0xff981f);
		addText(17128, "r", wid, 0, 0xff981f);
		addText(17129, "13", wid, 0, 0xff981f);
		addText(17130, "t", wid, 0, 0xff981f);
		rsinterface.parentID = 17115;
		rsinterface.id = 17115;
		rsinterface.atActionType = 0;
		rsinterface.contentType = 0;
		rsinterface.width = 130;
		rsinterface.height = 197;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.scrollMax = 280;
		rsinterface.children = new int[20];
		rsinterface.childX = new int[20];
		rsinterface.childY = new int[20];
		rsinterface.children[0] = 17109;
		rsinterface.childX[0] = 0;
		rsinterface.childY[0] = 0;
		rsinterface.children[1] = 17110;
		rsinterface.childX[1] = 0;
		rsinterface.childY[1] = 12;
		rsinterface.children[2] = 17111;
		rsinterface.childX[2] = 0;
		rsinterface.childY[2] = 24;
		rsinterface.children[3] = 17112;
		rsinterface.childX[3] = 0;
		rsinterface.childY[3] = 36;
		rsinterface.children[4] = 17113;
		rsinterface.childX[4] = 0;
		rsinterface.childY[4] = 48;
		rsinterface.children[5] = 17114;
		rsinterface.childX[5] = 0;
		rsinterface.childY[5] = 60;
		rsinterface.children[6] = 17117;
		rsinterface.childX[6] = 0;
		rsinterface.childY[6] = 72;
		rsinterface.children[7] = 17118;
		rsinterface.childX[7] = 0;
		rsinterface.childY[7] = 84;
		rsinterface.children[8] = 17119;
		rsinterface.childX[8] = 0;
		rsinterface.childY[8] = 96;
		rsinterface.children[9] = 17120;
		rsinterface.childX[9] = 0;
		rsinterface.childY[9] = 108;
		rsinterface.children[10] = 17121;
		rsinterface.childX[10] = 0;
		rsinterface.childY[10] = 120;
		rsinterface.children[11] = 17122;
		rsinterface.childX[11] = 0;
		rsinterface.childY[11] = 132;
		rsinterface.children[12] = 17123;
		rsinterface.childX[12] = 0;
		rsinterface.childY[12] = 144;
		rsinterface.children[13] = 17124;
		rsinterface.childX[13] = 0;
		rsinterface.childY[13] = 156;
		rsinterface.children[14] = 17125;
		rsinterface.childX[14] = 0;
		rsinterface.childY[14] = 168;
		rsinterface.children[15] = 17126;
		rsinterface.childX[15] = 0;
		rsinterface.childY[15] = 180;
		rsinterface.children[16] = 17127;
		rsinterface.childX[16] = 0;
		rsinterface.childY[16] = 192;
		rsinterface.children[17] = 17128;
		rsinterface.childX[17] = 0;
		rsinterface.childY[17] = 204;
		rsinterface.children[18] = 17129;
		rsinterface.childX[18] = 0;
		rsinterface.childY[18] = 216;
		rsinterface.children[19] = 17130;
		rsinterface.childX[19] = 0;
		rsinterface.childY[19] = 228;
	}
	public static RSInterface addInterface(int id) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.id = id;
		rsi.parentID = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static RSInterface addTab(int id) {
		RSInterface Tab = interfaceCache[id] = new RSInterface();
		Tab.id = id;
		Tab.parentID = id;
		Tab.type = 0;
		Tab.atActionType = 0;
		Tab.contentType = 0;
		Tab.width = 512;
		Tab.height = 334;
		Tab.opacity = (byte) 0;
		Tab.textColor = 0;
		return Tab;
	}

	public static RSInterface addTabInterface(int id) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		if (tab.id == 3917) id = -1;
		tab.id = id;
		tab.parentID = id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 700;
		tab.opacity = (byte) 0;
		tab.hoverType = -1;
		return tab;
	}

	public static void itemDisplay(int index, int itemSpaceX, int itemSpaceY, int itemX, int itemY, String... options) {
		RSInterface rsi = interfaceCache[index] = new RSInterface();
		rsi.actions = new String[options.length];
		System.arraycopy(options, 0, rsi.actions, 0, options.length);
		rsi.spritesX = new int[20];
		rsi.invStackSizes = new int[30];
		rsi.inv = new int[30];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		rsi.centerText = true;
		rsi.aBoolean227 = false;
		rsi.aBoolean235 = false;
		rsi.usableItemInterface = false;
		rsi.isBoxInterface = false;
		rsi.aBoolean259 = true;
		rsi.textShadow = false;
		rsi.invSpritePadX = itemSpaceX;
		rsi.invSpritePadY = itemSpaceY;
		rsi.height = itemY;
		rsi.width = itemX;
		rsi.parentID = index;
		rsi.id = index;
		rsi.type = 2;
	}

	public static void itemContainer(int id, int itemSpaceX, int itemSpaceY, int width, int height, boolean transparent,
			String... options) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.actions = new String[options.length];
		System.arraycopy(options, 0, rsi.actions, 0, options.length);
		rsi.spritesX = new int[20];
		rsi.invStackSizes = new int[width * height];
		rsi.inv = new int[width * height];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		rsi.centerText = true;
		rsi.aBoolean227 = false;
		rsi.aBoolean235 = false;
		rsi.usableItemInterface = false;
		rsi.isBoxInterface = false;
		rsi.aBoolean259 = true;
		rsi.textShadow = false;
		rsi.drawsTransparent = transparent;
		if (transparent) {
			rsi.opacity = (byte) 0;
		}
		rsi.invSpritePadX = itemSpaceX;
		rsi.invSpritePadY = itemSpaceY;
		rsi.height = height;
		rsi.width = width;
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 2;
	}

	public static void addInputField(int parentId, int id, int characterLimit, int defaultColor, int defaultHoverColor,
			int selectedColor, int selectedHoverColor, String text, int width, int height, boolean onlyNumbers,
			boolean asterisks, String defaultText) {
		RSInterface field = addInterface(id);
		field.id = id;
		field.parentID = parentId;
		field.type = 16;
		field.atActionType = 8;
		field.disabledMessage = text;
		field.enabledMessage = defaultText;
		field.width = width;
		field.height = height;
		field.textColor = defaultColor;
		field.textHoverColor = defaultHoverColor;
		field.anInt219 = selectedColor;
		field.anInt239 = selectedHoverColor;
		field.onlyNumbers = onlyNumbers;
		field.displayAsterisks = asterisks;
		field.actions = new String[] { "Clear", "Edit" };
	}

	public void setText(String text) {
		disabledMessage = text;
	}

	public void swapBoxItems(int i, int j) {
		int k = inv[i];
		inv[i] = inv[j];
		inv[j] = k;
		k = invStackSizes[i];
		invStackSizes[i] = invStackSizes[j];
		invStackSizes[j] = k;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	private Model method206(int i, int j) {
		long cacheKey = ((long) i << 16) + j;
		Model model = aMRUNodes_264.get(cacheKey);
		if (model != null)
			return model;
		if (i == 1)
			model = Model.loadModelFromCache(j);
		if (i == 2)
			model = EntityDef.forID(j).getModelForInterface();
		if (i == 3)
			model = Client.myStoner.getUnanimatedModel();
		if (i == 4)
			model = getItemDefinition(j).method202(50);
		if (i == 5)
		{
		}
		if (model != null)
			aMRUNodes_264.put(cacheKey, model);
		return model;
	}

	public Model method209(int j, int k, boolean flag) {
		Model model;
		if (flag)
			model = method206(anInt255, anInt256);
		else
			model = method206(modelType, mediaID);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.triangleColors == null)
			return model;
		Model model_1 = new Model(true, SequenceFrame.isInvalidFrame(k) & SequenceFrame.isInvalidFrame(j), false, model);
		if (k != -1 || j != -1)
			model_1.calculateNormals();
		if (k != -1)
			model_1.applyTransformation(k);
		if (j != -1)
			model_1.applyTransformation(j);
		model_1.applyLighting(84, 1000, -90, -580, -90, true);
		return model_1;
	}

}
