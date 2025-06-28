package com.bestbudz.ui;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.dock.util.DockBlocker;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.buildStonersListMenu;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.InterfaceManagement.drawInterfaceRecursive;

public class BuildInterface extends Client {

	public static void buildInterfaceMenu(int interfaceX, RSInterface parentInterface, int interfaceY, int scrollPosition) {
		// Early validation and setup
		if (!validateInterfaceInput(parentInterface, interfaceX, interfaceY)) {
			return;
		}

		// Process all child interfaces
		processChildInterfaces(interfaceX, parentInterface, interfaceY, scrollPosition);
	}

	private static boolean validateInterfaceInput(RSInterface parentInterface, int interfaceX, int interfaceY) {
		// Early null check and fallback
		if (parentInterface == null) {
			parentInterface = RSInterface.interfaceCache[21356];
			// Additional safety check for the fallback interface
			if (parentInterface == null) {
				return false; // Exit if both original and fallback are null
			}
		}

		if (DockBlocker.isDocked(parentInterface.id) || DockBlocker.isDocked(parentInterface.parentID)) {
			return false;
		}

		if (parentInterface.type != 0 || parentInterface.children == null || parentInterface.isMouseoverTriggered) {
			return false;
		}

		if (MouseState.x < interfaceX || MouseState.y < interfaceY ||
			MouseState.x > interfaceX + parentInterface.width ||
			MouseState.y > interfaceY + parentInterface.height) {
			return false;
		}

		return true;
	}

	private static void processChildInterfaces(int interfaceX, RSInterface parentInterface, int interfaceY, int scrollPosition) {
		for (int childIndex = 0; childIndex < parentInterface.children.length; childIndex++) {
			RSInterface childInterface = RSInterface.interfaceCache[parentInterface.children[childIndex]];

			// Add null check for child interface
			if (childInterface == null) {
				continue; // Skip this child if it's null
			}

			int childX = calculateChildX(parentInterface, childInterface, interfaceX, childIndex);
			int childY = calculateChildY(parentInterface, childInterface, interfaceY, childIndex, scrollPosition);

			// Handle interface interactions
			handleHoverEffects(childInterface, childX, childY);
			handleSpecialHoverTypes(childInterface, childX, childY);

			// Process interface based on type
			if (childInterface.type == 0) {
				handleContainerInterface(childX, childInterface, childY);
			} else {
				handleClickableInterface(childInterface, childX, childY, parentInterface);
			}
		}
	}

	private static int calculateChildX(RSInterface parentInterface, RSInterface childInterface, int interfaceX, int childIndex) {
		int childX = parentInterface.childX[childIndex] + interfaceX;
		childX += childInterface.anInt263; // Additional X offset
		return childX;
	}

	private static int calculateChildY(RSInterface parentInterface, RSInterface childInterface, int interfaceY, int childIndex, int scrollPosition) {
		int childY = (parentInterface.childY[childIndex] + interfaceY) - scrollPosition;
		childY += childInterface.positionScroll; // Additional Y offset
		return childY;
	}

	private static void handleHoverEffects(RSInterface childInterface, int childX, int childY) {
		if ((childInterface.hoverType >= 0 || childInterface.textHoverColor != 0) &&
			isMouseOverInterface(childX, childY, childInterface.width, childInterface.height)) {
			if (childInterface.hoverType >= 0)
				hoveredInterfaceId = childInterface.hoverType;
			else
				hoveredInterfaceId = childInterface.id;
		}
	}

	private static void handleSpecialHoverTypes(RSInterface childInterface, int childX, int childY) {
		if (childInterface.type == 8 &&
			isMouseOverInterface(childX, childY, childInterface.width, childInterface.height)) {
			specialHoverInterfaceId = childInterface.id;
		}
	}

	private static void handleContainerInterface(int childX, RSInterface childInterface, int childY) {
		buildInterfaceMenu(childX, childInterface, childY, childInterface.scrollPosition);
		if (childInterface.scrollMax > childInterface.height)
			drawInterfaceRecursive(childX + childInterface.width, childInterface.height,
				MouseState.x, MouseState.y, childInterface, childY, true, childInterface.scrollMax);
	}

	private static void handleClickableInterface(RSInterface childInterface, int childX, int childY, RSInterface parentInterface) {
		// Handle specific action types
		handleActionTypes(childInterface, childX, childY);

		// Handle general interface actions
		handleGeneralInterfaceActions(childInterface, childX, childY);

		// Handle inventory interfaces (type 2)
		if (childInterface.type == 2) {
			handleInventoryInterface(childInterface, childX, childY, parentInterface);
		}
	}

	private static void handleActionTypes(RSInterface childInterface, int childX, int childY) {
		if (!isMouseOverInterface(childX, childY, childInterface.width, childInterface.height)) {
			return;
		}

		switch (childInterface.atActionType) {
			case 1:
				handleActionType1(childInterface);
				break;
			case 2:
				handleActionType2(childInterface);
				break;
			case 3:
				handleActionType3(childInterface);
				break;
			case 4:
				handleActionType4(childInterface);
				break;
			case 5:
				handleActionType5(childInterface);
				break;
			case 6:
				handleActionType6(childInterface);
				break;
			case 8:
				handleActionType8(childInterface);
				break;
		}
	}

	private static void handleActionType1(RSInterface childInterface) {
		boolean handledBySpecialMenu = false;
		if (childInterface.contentType != 0)
			handledBySpecialMenu = buildStonersListMenu(childInterface);
		if (!handledBySpecialMenu) {
			addMenuAction(childInterface.tooltip, 315, childInterface.id);
		}
	}

	private static void handleActionType2(RSInterface childInterface) {
		if (spellSelected == 0) {
			String spellActionName = childInterface.selectedActionName;
			if (spellActionName != null && spellActionName.indexOf(" ") != -1)
				spellActionName = spellActionName.substring(0, spellActionName.indexOf(" "));
			addMenuAction(spellActionName + " @gre@" + childInterface.spellName, 626, childInterface.id);
		}
	}

	private static void handleActionType3(RSInterface childInterface) {
		addMenuAction("Shut", 200, childInterface.id);
	}

	private static void handleActionType4(RSInterface childInterface) {
		addMenuAction(childInterface.tooltip, 169, childInterface.id);
		if (childInterface.hoverText != null) {
			// Handle hover text if needed
		}
	}

	private static void handleActionType5(RSInterface childInterface) {
		addMenuAction(childInterface.tooltip, 646, childInterface.id);
	}

	private static void handleActionType6(RSInterface childInterface) {
		if (!isPlayerBusy) {
			addMenuAction(childInterface.tooltip, 679, childInterface.id);
		}
	}

	private static void handleActionType8(RSInterface childInterface) {
		if (!isPlayerBusy && childInterface.actions != null) {
			for (int actionIndex = 0; actionIndex < childInterface.actions.length; actionIndex++) {
				addMenuAction(childInterface.actions[actionIndex], 1700 + actionIndex, childInterface.id);
			}
		}
	}

	private static void handleGeneralInterfaceActions(RSInterface childInterface, int childX, int childY) {
		int effectiveWidth = childInterface.type == 4 ? 100 : childInterface.width;

		if (isMouseOverInterface(childX, childY, effectiveWidth, childInterface.height)) {
			if (childInterface.actions != null) {
				if ((childInterface.type == 4 && childInterface.disabledMessage != null &&
					childInterface.disabledMessage.length() > 0) || childInterface.type == 5) {

					for (int actionIndex = childInterface.actions.length - 1; actionIndex >= 0; actionIndex--) {
						if (childInterface.actions[actionIndex] != null) {
							String actionText = childInterface.actions[actionIndex] +
								(childInterface.type == 4 ? " " + childInterface.disabledMessage : "");
							addMenuActionWithSlot(actionText, 647, actionIndex, childInterface.id);
						}
					}
				}
			}
		}
	}

	private static void handleInventoryInterface(RSInterface childInterface, int childX, int childY, RSInterface parentInterface) {
		InventoryProcessor processor = new InventoryProcessor(childInterface, childX, childY, parentInterface);
		processor.processInventorySlots();
	}

	private static class InventoryProcessor {
		private final RSInterface childInterface;
		private final int childX;
		private final int childY;
		private final RSInterface parentInterface;
		private int inventorySlot = 0;
		private int currentTabAmount;
		private int currentTabSlot = 0;
		private int heightOffset = 0;
		private int baseSlotOffset = 0;

		public InventoryProcessor(RSInterface childInterface, int childX, int childY, RSInterface parentInterface) {
			this.childInterface = childInterface;
			this.childX = childX;
			this.childY = childY;
			this.parentInterface = parentInterface;
			this.currentTabAmount = tabAmounts[0];
			initializeBankTabs();
		}

		private void initializeBankTabs() {
			// Handle bank tab selection
			if (childInterface.contentType == 206 && variousSettings[1000] != 0 && variousSettings[1012] == 0) {
				for (int tabIndex = 0; tabIndex < tabAmounts.length; tabIndex++) {
					if (tabIndex == variousSettings[1000]) {
						break;
					}
					baseSlotOffset += tabAmounts[tabIndex];
				}
				inventorySlot = baseSlotOffset;
			}
		}

		public void processInventorySlots() {
			heightLoop:
			for (int heightIndex = 0; heightIndex < childInterface.height; heightIndex++) {
				for (int widthIndex = 0; widthIndex < childInterface.width; widthIndex++) {

					SlotCoordinates coords = calculateSlotCoordinates(heightIndex, widthIndex);

					if (!handleBankTabLogic(heightIndex)) {
						break heightLoop;
					}

					if (isMouseOverSlot(coords)) {
						processSlotInteraction(coords);
					}

					inventorySlot++;
				}
			}
		}

		private SlotCoordinates calculateSlotCoordinates(int heightIndex, int widthIndex) {
			int slotX = childX + widthIndex * (32 + childInterface.invSpritePadX);
			int slotY = childY + heightIndex * (32 + childInterface.invSpritePadY) + heightOffset;

			// Apply sprite position offsets for small slot numbers
			if (inventorySlot < 20 && childInterface.spritesX != null && childInterface.spritesY != null) {
				slotX += childInterface.spritesX[inventorySlot];
				slotY += childInterface.spritesY[inventorySlot];
			}

			return new SlotCoordinates(slotX, slotY);
		}

		private boolean handleBankTabLogic(int heightIndex) {
			// Handle bank tab logic
			if (childInterface.contentType == 206 && variousSettings[1012] == 0) {
				if (variousSettings[1000] == 0) { // All tabs view
					if (inventorySlot >= currentTabAmount) {
						if (currentTabSlot + 1 < tabAmounts.length) {
							currentTabAmount += tabAmounts[++currentTabSlot];
							if (currentTabSlot > 0 && tabAmounts[currentTabSlot - 1] % 8 == 0) {
								return false; // Break heightIndex loop
							}
							heightOffset += 8;
						}
						return false;
					}
				} else if (variousSettings[1000] <= 9) { // Specific tab view
					if (inventorySlot >= tabAmounts[variousSettings[1000]] + baseSlotOffset) {
						return false;
					}
				}
			}
			return true;
		}

		private boolean isMouseOverSlot(SlotCoordinates coords) {
			return MouseState.x >= coords.x && MouseState.y >= coords.y &&
				MouseState.x < coords.x + 32 && MouseState.y < coords.y + 32;
		}

		private void processSlotInteraction(SlotCoordinates coords) {
			mouseInvInterfaceIndex = inventorySlot;
			lastActiveInvInterface = childInterface.id;

			int itemId = getItemIdFromSlot();
			if (itemId >= 0) {
				ItemDef itemDefinition = getItemDefinition(itemId);
				if (itemDefinition != null) {
					processItemActions(itemDefinition);
				}
			}
		}

		private int getItemIdFromSlot() {
			int itemId = -1;
			// Get item ID from regular inventory
			if (childInterface.inv != null && inventorySlot < childInterface.inv.length) {
				itemId = childInterface.inv[inventorySlot] - 1;
			}
			// Get item ID from bank temporary array
			if (variousSettings[1012] == 1 && childInterface.contentType == 206 &&
				bankInvTemp != null && inventorySlot < bankInvTemp.length) {
				itemId = bankInvTemp[inventorySlot] - 1;
			}
			return itemId;
		}

		private void processItemActions(ItemDef itemDefinition) {
			// Force bank items to behave exactly like box interface items
			boolean isBoxOrBank = childInterface.isBoxInterface || childInterface.contentType == 206;

			// Handle item combination when another item is selected
			if (itemSelected == 1 && isBoxOrBank) {
				handleItemCombination(itemDefinition);
			}
			// Handle spell casting on items
			else if (spellSelected == 1 && isBoxOrBank) {
				handleSpellCasting(itemDefinition);
			}
			// Handle normal item actions
			else {
				handleNormalItemActions(itemDefinition);
			}
		}

		private void handleBankItemActions(ItemDef itemDefinition) {
			// Only add inspect option in debug mode for bank items
			if (EngineConfig.DEBUG_MODE) {
				String inspectText = "@lre@" + itemDefinition.name + " @gre@(@whi@" + itemDefinition.id + "@gre@)";
				addItemMenuAction(inspectText, 1125, itemDefinition.id, inventorySlot, childInterface.id);
			}
			// All other actions are skipped for bank items
		}

		private void handleItemCombination(ItemDef itemDefinition) {
			// Force bank items to use box interface logic for item combination
			boolean isBoxOrBank = childInterface.isBoxInterface || childInterface.contentType == 206;

			if (isBoxOrBank && (childInterface.id != selectedItemInterfaceId || inventorySlot != selectedItemSlot)) {
				String combineText = "Select " + selectedItemName + ", combine with @lre@" + itemDefinition.name;
				addItemMenuAction(combineText, 870, itemDefinition.id, inventorySlot, childInterface.id);
			}
		}

		private void handleSpellCasting(ItemDef itemDefinition) {
			// Force bank items to use box interface logic for spell casting
			boolean isBoxOrBank = childInterface.isBoxInterface || childInterface.contentType == 206;

			if (isBoxOrBank && (spellUsableOn & 0x10) == 16) {
				String spellText = spellTooltip + " @lre@" + itemDefinition.name;
				addItemMenuAction(spellText, 543, itemDefinition.id, inventorySlot, childInterface.id);
			}
		}

		private void handleNormalItemActions(ItemDef itemDefinition) {
			// Add high-priority item actions (indices 4 and 3)
			addHighPriorityItemActions(itemDefinition);

			// Add "Select" action for usable items
			addSelectAction(itemDefinition);

			// Add remaining item actions (indices 2, 1, 0)
			addRemainingItemActions(itemDefinition);

			// Add interface-specific actions
			addInterfaceSpecificActions(itemDefinition);

			// Add "Inspect" action (always available in debug mode)
			addInspectAction(itemDefinition);
		}

		private void addHighPriorityItemActions(ItemDef itemDefinition) {
			// Force bank items to use box interface logic
			boolean isBoxOrBank = childInterface.isBoxInterface || childInterface.contentType == 206;

			if (isBoxOrBank) {
				for (int actionIndex = 4; actionIndex >= 3; actionIndex--) {
					if (itemDefinition.itemActions != null && itemDefinition.itemActions[actionIndex] != null) {
						String actionText = itemDefinition.itemActions[actionIndex] + " @lre@" + itemDefinition.name;
						int actionId = actionIndex == 3 ? 493 : 847;
						addItemMenuAction(actionText, actionId, itemDefinition.id, inventorySlot, childInterface.id);
					} else if (actionIndex == 4) {
						// Default "Throw away" action for action slot 4
						String throwAwayText = "Throw away @lre@" + itemDefinition.name;
						addItemMenuAction(throwAwayText, 847, itemDefinition.id, inventorySlot, childInterface.id);
					}
				}
			}
		}

		private void addSelectAction(ItemDef itemDefinition) {
			// Force bank items to use box interface logic for usable items
			boolean isUsableBoxOrBank = childInterface.usableItemInterface ||
				(childInterface.contentType == 206 && childInterface.usableItemInterface);

			if (isUsableBoxOrBank) {
				String selectText = "Select @lre@" + itemDefinition.name;
				addItemMenuAction(selectText, 447, itemDefinition.id, inventorySlot, childInterface.id);
			}
		}

		private void addRemainingItemActions(ItemDef itemDefinition) {
			// Force bank items to use box interface logic
			boolean isBoxOrBank = childInterface.isBoxInterface || childInterface.contentType == 206;

			if (isBoxOrBank && itemDefinition.itemActions != null) {
				for (int actionIndex = 2; actionIndex >= 0; actionIndex--) {
					if (itemDefinition.itemActions[actionIndex] != null) {
						String actionText = itemDefinition.itemActions[actionIndex] + " @lre@" + itemDefinition.name;
						int actionId = getActionIdForIndex(actionIndex);
						addItemMenuAction(actionText, actionId, itemDefinition.id, inventorySlot, childInterface.id);
					}
				}
			}
		}

		private void addInterfaceSpecificActions(ItemDef itemDefinition) {
			if (childInterface.actions != null) {
				for (int actionIndex = 6; actionIndex >= 0; actionIndex--) {
					if (actionIndex > childInterface.actions.length - 1) continue;
					if (childInterface.actions[actionIndex] != null) {
						String actionText = childInterface.actions[actionIndex] + " @lre@" + itemDefinition.name;
						int actionId = getInterfaceActionId(actionIndex);

						// For bank items, use standard inventory action IDs instead of bank-specific ones
						if (childInterface.contentType == 206) {
							actionId = getInventoryActionId(actionIndex); // Use inventory IDs for bank items
						} else if (parentInterface.parentID == 5292) {
							// Only apply bank-specific handling for non-206 content types
							actionId = getBankSpecificActionId(actionIndex);
						}

						addItemMenuAction(actionText, actionId, itemDefinition.id, inventorySlot, childInterface.id);
					}
				}
			}
		}

		private void addInspectAction(ItemDef itemDefinition) {
			if (EngineConfig.DEBUG_MODE) {
				String inspectText = "@lre@" + itemDefinition.name + " @gre@(@whi@" + itemDefinition.id + "@gre@)";
				addItemMenuAction(inspectText, 1125, itemDefinition.id, inventorySlot, childInterface.id);
			}
		}

		private int getActionIdForIndex(int actionIndex) {
			switch (actionIndex) {
				case 0: return 74;
				case 1: return 454;
				case 2: return 539;
				default: return 0;
			}
		}

		private int getInterfaceActionId(int actionIndex) {
			switch (actionIndex) {
				case 0: return 632;
				case 1: return 78;
				case 2: return 867;
				case 3: return 431;
				case 4: return 53;
				default: return 0;
			}
		}

		private int getInventoryActionId(int actionIndex) {
			// Return standard inventory action IDs for bank items to trick the system
			switch (actionIndex) {
				case 0: return 632; // Same as regular inventory
				case 1: return 78;  // Same as regular inventory
				case 2: return 867; // Same as regular inventory
				case 3: return 431; // Same as regular inventory
				case 4: return 53;  // Same as regular inventory
				case 5: return 632; // Force to inventory action instead of bank
				case 6: return 78;  // Force to inventory action instead of bank
				default: return getInterfaceActionId(actionIndex);
			}
		}

		private int getBankSpecificActionId(int actionIndex) {
			if (childInterface.actions[actionIndex] == null) {
				if (actionIndex == 5) return 291;
			} else {
				if (actionIndex == 5) return 300;
				if (actionIndex == 6) return 291;
			}
			return getInterfaceActionId(actionIndex);
		}

		private static class SlotCoordinates {
			final int x;
			final int y;

			SlotCoordinates(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
	}

	// Utility methods
	private static boolean isMouseOverInterface(int x, int y, int width, int height) {
		return MouseState.x >= x && MouseState.y >= y &&
			MouseState.x < x + width && MouseState.y < y + height;
	}

	private static void addMenuAction(String actionName, int actionId, int interfaceId) {
		menuActionName[menuActionRow] = actionName;
		menuActionID[menuActionRow] = actionId;
		menuActionCmd3[menuActionRow] = interfaceId;
		menuActionRow++;
	}

	private static void addMenuActionWithSlot(String actionName, int actionId, int slot, int interfaceId) {
		menuActionName[menuActionRow] = actionName;
		menuActionID[menuActionRow] = actionId;
		menuActionCmd2[menuActionRow] = slot;
		menuActionCmd3[menuActionRow] = interfaceId;
		menuActionRow++;
	}

	private static void addItemMenuAction(String actionName, int actionId, int itemId, int slot, int interfaceId) {
		menuActionName[menuActionRow] = actionName;
		menuActionID[menuActionRow] = actionId;
		menuActionCmd1[menuActionRow] = itemId;
		menuActionCmd2[menuActionRow] = slot;
		menuActionCmd3[menuActionRow] = interfaceId;
		menuActionRow++;
	}
}