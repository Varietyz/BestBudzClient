package com.bestbudz.ui;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.dock.util.DockBlocker;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.buildStonersListMenu;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.InterfaceManagement.drawInterfaceRecursive;

public class BuildInterface extends Client
{
	public static void buildInterfaceMenu(int y, RSInterface rsinterface, int x, int scrollPosition) {
		// Early null check and fallback
		if (rsinterface == null) {
			rsinterface = RSInterface.interfaceCache[21356];
			// Additional safety check for the fallback interface
			if (rsinterface == null) {
				return; // Exit if both original and fallback are null
			}
		}

		if (DockBlocker.isDocked(rsinterface.id) || DockBlocker.isDocked(rsinterface.parentID)) {
			return;
		}

		if (rsinterface.type != 0 || rsinterface.children == null || rsinterface.isMouseoverTriggered)
			return;
		if (MouseState.x < y || MouseState.y < x || MouseState.x > y + rsinterface.width || MouseState.y > x + rsinterface.height)
			return;

		for (int childIndex = 0; childIndex < rsinterface.children.length; childIndex++) {
			int childX = rsinterface.childX[childIndex] + y;
			int childY = (rsinterface.childY[childIndex] + x) - scrollPosition;
			RSInterface child = RSInterface.interfaceCache[rsinterface.children[childIndex]];

			// Add null check for child interface
			if (child == null) {
				continue; // Skip this child if it's null
			}

			childX += child.anInt263;
			childY += child.anInt265;

			if ((child.hoverType >= 0 || child.textHoverColor != 0) && MouseState.x >= childX && MouseState.y >= childY
				&& MouseState.x < childX + child.width && MouseState.y < childY + child.height)
				if (child.hoverType >= 0)
					anInt886 = child.hoverType;
				else
					anInt886 = child.id;
			if (child.type == 8 && MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + child.width
				&& MouseState.y < childY + child.height) {
				anInt1315 = child.id;
			}
			if (child.type == 0) {
				buildInterfaceMenu(childX, child, childY, child.scrollPosition);
				if (child.scrollMax > child.height)
					drawInterfaceRecursive(childX + child.width, child.height, MouseState.x, MouseState.y, child, childY, true, child.scrollMax);
			} else {
				if (child.atActionType == 1 && MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + child.width
					&& MouseState.y < childY + child.height) {
					boolean flag = false;
					if (child.contentType != 0)
						flag = buildStonersListMenu(child);
					if (!flag) {
						menuActionName[menuActionRow] = child.tooltip;
						menuActionID[menuActionRow] = 315;
						menuActionCmd3[menuActionRow] = child.id;
						menuActionRow++;
					}
				}
				if (child.atActionType == 2 && spellSelected == 0 && MouseState.x >= childX && MouseState.y >= childY
					&& MouseState.x < childX + child.width && MouseState.y < childY + child.height) {
					String s = child.selectedActionName;
					if (s != null && s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					menuActionName[menuActionRow] = s + " @gre@" + child.spellName;
					menuActionID[menuActionRow] = 626;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 3 && MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + child.width
					&& MouseState.y < childY + child.height) {
					menuActionName[menuActionRow] = "Shut";
					menuActionID[menuActionRow] = 200;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 4 && MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + child.width
					&& MouseState.y < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip;
					menuActionID[menuActionRow] = 169;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
					if (child.hoverText != null) {
						// Handle hover text if needed
					}
				}
				if (child.atActionType == 5 && MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + child.width
					&& MouseState.y < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip;
					menuActionID[menuActionRow] = 646;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 6 && !aBoolean1149 && MouseState.x >= childX && MouseState.y >= childY
					&& MouseState.x < childX + child.width && MouseState.y < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip;
					menuActionID[menuActionRow] = 679;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 8 && !aBoolean1149 && MouseState.x >= childX && MouseState.y >= childY
					&& MouseState.x < childX + child.width && MouseState.y < childY + child.height) {
					if (child.actions != null) {
						for (int i = 0; i < child.actions.length; i++) {
							menuActionName[menuActionRow] = child.actions[i];
							menuActionID[menuActionRow] = 1700 + i;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
						}
					}
				}
				if (MouseState.x >= childX && MouseState.y >= childY && MouseState.x < childX + (child.type == 4 ? 100 : child.width)
					&& MouseState.y < childY + child.height) {
					if (child.actions != null) {
						if ((child.type == 4 && child.disabledMessage != null && child.disabledMessage.length() > 0) || child.type == 5) {
							for (int action = child.actions.length - 1; action >= 0; action--) {
								if (child.actions[action] != null) {
									menuActionName[menuActionRow] = child.actions[action]
										+ (child.type == 4 ? " " + child.disabledMessage : "");
									menuActionID[menuActionRow] = 647;
									menuActionCmd2[menuActionRow] = action;
									menuActionCmd3[menuActionRow] = child.id;
									menuActionRow++;
								}
							}
						}
					}
				}
				if (child.type == 2) {
					int k2 = 0;
					int tabAm = tabAmounts[0];
					int tabSlot = 0;
					int hh = 0;

					int newSlot = 0;
					if (child.contentType == 206 && variousSettings[1000] != 0 && variousSettings[1012] == 0) {
						for (int tab = 0; tab < tabAmounts.length; tab++) {
							if (tab == variousSettings[1000]) {
								break;
							}
							newSlot += tabAmounts[tab];
						}
						k2 = newSlot;
					}

					heightLoop:
					for (int l2 = 0; l2 < child.height; l2++) {
						for (int i3 = 0; i3 < child.width; i3++) {
							int j3 = childX + i3 * (32 + child.invSpritePadX);
							int k3 = childY + l2 * (32 + child.invSpritePadY) + hh;
							if (child.contentType == 206 && variousSettings[1012] == 0) {
								if (variousSettings[1000] == 0) {
									if (k2 >= tabAm) {
										if (tabSlot + 1 < tabAmounts.length) {
											tabAm += tabAmounts[++tabSlot];
											if (tabSlot > 0 && tabAmounts[tabSlot - 1] % 8 == 0) {
												l2--;
											}
											hh += 8;
										}
										break;
									}
								} else if (variousSettings[1000] <= 9) {
									if (k2 >= tabAmounts[variousSettings[1000]] + newSlot) {
										break heightLoop;
									}
								}
							}
							if (k2 < 20 && child.spritesX != null && child.spritesY != null) {
								j3 += child.spritesX[k2];
								k3 += child.spritesY[k2];
							}
							if (MouseState.x >= j3 && MouseState.y >= k3 && MouseState.x < j3 + 32 && MouseState.y < k3 + 32) {
								mouseInvInterfaceIndex = k2;
								lastActiveInvInterface = child.id;

								int itemId = -1;
								if (child.inv != null && k2 < child.inv.length) {
									itemId = child.inv[k2] - 1;
								}
								if (variousSettings[1012] == 1 && child.contentType == 206 && bankInvTemp != null && k2 < bankInvTemp.length) {
									itemId = bankInvTemp[k2] - 1;
								}
								if (itemId >= 0) {
									ItemDef itemDef = getItemDefinition(itemId);
									if (itemDef != null) {
										if (itemSelected == 1 && child.isBoxInterface) {
											if (child.id != anInt1284 || k2 != anInt1283) {
												menuActionName[menuActionRow] = "Select " + selectedItemName
													+ ", combine with @lre@"
													+ itemDef.name;
												menuActionID[menuActionRow] = 870;
												menuActionCmd1[menuActionRow] = itemDef.id;
												menuActionCmd2[menuActionRow] = k2;
												menuActionCmd3[menuActionRow] = child.id;
												menuActionRow++;
											}
										} else if (spellSelected == 1 && child.isBoxInterface) {
											if ((spellUsableOn & 0x10) == 16) {
												menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
												menuActionID[menuActionRow] = 543;
												menuActionCmd1[menuActionRow] = itemDef.id;
												menuActionCmd2[menuActionRow] = k2;
												menuActionCmd3[menuActionRow] = child.id;
												menuActionRow++;
											}
										} else {
											if (child.isBoxInterface) {
												for (int l3 = 4; l3 >= 3; l3--)
													if (itemDef.itemActions != null && itemDef.itemActions[l3] != null) {
														menuActionName[menuActionRow] = itemDef.itemActions[l3] + " @lre@"
															+ itemDef.name;
														if (l3 == 3)
															menuActionID[menuActionRow] = 493;
														if (l3 == 4)
															menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.id;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													} else if (l3 == 4) {
														menuActionName[menuActionRow] = "Throw away @lre@" + itemDef.name;
														menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.id;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
											}
											if (child.usableItemInterface) {
												menuActionName[menuActionRow] = "Select @lre@" + itemDef.name;
												menuActionID[menuActionRow] = 447;
												menuActionCmd1[menuActionRow] = itemDef.id;
												menuActionCmd2[menuActionRow] = k2;
												menuActionCmd3[menuActionRow] = child.id;
												menuActionRow++;
											}
											if (child.isBoxInterface && itemDef.itemActions != null) {
												for (int i4 = 2; i4 >= 0; i4--)
													if (itemDef.itemActions[i4] != null) {
														menuActionName[menuActionRow] = itemDef.itemActions[i4] + " @lre@"
															+ itemDef.name;
														if (i4 == 0)
															menuActionID[menuActionRow] = 74;
														if (i4 == 1)
															menuActionID[menuActionRow] = 454;
														if (i4 == 2)
															menuActionID[menuActionRow] = 539;
														menuActionCmd1[menuActionRow] = itemDef.id;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
											}
											if (child.actions != null) {
												if (rsinterface.parentID == 5292) {
													if (modifiableXValue > 0) {
														if (child.actions.length > 5) {
															child.actions[5] = "Take out " + modifiableXValue;
														}
													} else {
														if (child.actions.length > 5) {
															child.actions[5] = null;
														}
													}
												}

												for (int j4 = 6; j4 >= 0; j4--) {
													if (j4 > child.actions.length - 1)
														continue;
													if (child.actions[j4] != null) {
														menuActionName[menuActionRow] = child.actions[j4] + " @lre@"
															+ itemDef.name;
														if (j4 == 0)
															menuActionID[menuActionRow] = 632;
														if (j4 == 1)
															menuActionID[menuActionRow] = 78;
														if (j4 == 2)
															menuActionID[menuActionRow] = 867;
														if (j4 == 3)
															menuActionID[menuActionRow] = 431;
														if (j4 == 4)
															menuActionID[menuActionRow] = 53;
														if (rsinterface.parentID == 5292) {
															if (child.actions[j4] == null) {
																if (j4 == 5)
																	menuActionID[menuActionRow] = 291;
															} else {
																if (j4 == 5)
																	menuActionID[menuActionRow] = 300;
																if (j4 == 6)
																	menuActionID[menuActionRow] = 291;
															}
														}

														menuActionCmd1[menuActionRow] = itemDef.id;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												}
											}
											if (EngineConfig.DEBUG_MODE) {
												menuActionName[menuActionRow] = "Inspect @lre@" + itemDef.name
													+ " @gre@(@whi@" + itemDef.id + "@gre@)";
											} else {
												menuActionName[menuActionRow] = "Inspect @lre@" + itemDef.name;
											}
											menuActionID[menuActionRow] = 1125;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									}
								}
							}
							k2++;
						}
					}
				}
			}
		}
	}

}
