package com.bestbudz.ui;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.Item;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.handling.RightClickMenu.buildAtNPCMenu;
import static com.bestbudz.ui.handling.RightClickMenu.buildAtStonerMenu;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.rendering.model.Model;
import java.util.ArrayDeque;
import com.bestbudz.world.ObjectDef;

public class BuildScreenMenu extends Client
{
	public static void build3dScreenMenu()
	{
		if (itemSelected == 0 && spellSelected == 0)
		{
			menuActionName[menuActionRow] = "Shuffle over here";
			menuActionID[menuActionRow] = 519;
			menuActionCmd2[menuActionRow] = MouseState.x;
			menuActionCmd3[menuActionRow] = MouseState.y;
			menuActionRow++;
		}
		int j = -1;
		for (int k = 0; k < Model.modelCount; k++)
		{
			int l = Model.modelHashes[k];
			int i1 = l & 0x7f;
			int j1 = l >> 7 & 0x7f;
			int k1 = l >> 29 & 3;
			int l1 = l >> 14 & 0x7fff;
			if (l == j)
				continue;
			j = l;
			if (k1 == 2)
			{
			}
			if (k1 == 2 && worldController.getObjectConfig(plane, i1, j1, l) >= 0)
			{
				ObjectDef objectDef = ObjectDef.forID(l1);
				if (objectDef.childIds != null)
					objectDef = objectDef.getChildObject();
				if (objectDef == null)
				{
					continue;
				}
				if (itemSelected == 1)
				{
					menuActionName[menuActionRow] = "Select " + selectedItemName + ", combine with @cya@"
						+ objectDef.name;
					menuActionID[menuActionRow] = 62;
					menuActionCmd1[menuActionRow] = l;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				}
				else if (spellSelected == 1)
				{
					if ((spellUsableOn & 4) == 4)
					{
						menuActionName[menuActionRow] = spellTooltip + " @cya@" + objectDef.name;
						menuActionID[menuActionRow] = 956;
						menuActionCmd1[menuActionRow] = l;
						menuActionCmd2[menuActionRow] = i1;
						menuActionCmd3[menuActionRow] = j1;
						menuActionRow++;
					}
				}
				else
				{
					if (objectDef.actions != null)
					{
						for (int i2 = 4; i2 >= 0; i2--)
							if (objectDef.actions[i2] != null)
							{
								menuActionName[menuActionRow] = objectDef.actions[i2] + " @cya@" + objectDef.name;
								if (i2 == 0)
									menuActionID[menuActionRow] = 502;
								if (i2 == 1)
									menuActionID[menuActionRow] = 900;
								if (i2 == 2)
									menuActionID[menuActionRow] = 113;
								if (i2 == 3)
									menuActionID[menuActionRow] = 872;
								if (i2 == 4)
									menuActionID[menuActionRow] = 1062;
								menuActionCmd1[menuActionRow] = l;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}

					}
					if (EngineConfig.DEBUG_MODE)
					{
						menuActionName[menuActionRow] = "@cya@" + objectDef.name + " @gre@(@whi@" + l1
							+ "@gre@) (@whi@" + (i1 + baseX) + "," + (j1 + baseY) + "@gre@)";
					}
					else
					{
						menuActionName[menuActionRow] = "@cya@" + objectDef.name;
					}
					menuActionID[menuActionRow] = 1226;
					menuActionCmd1[menuActionRow] = objectDef.type << 14;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				}
			}
			if (k1 == 1)
			{
				Npc npc = npcArray[l1];
				try
				{
					if (npc.desc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64)
					{
						for (int j2 = 0; j2 < npcCount; j2++)
						{
							Npc npc2 = npcArray[npcIndices[j2]];
							if (npc2 != null && npc2 != npc && npc2.desc.size == 1 && npc2.x == npc.x
								&& npc2.y == npc.y)
								buildAtNPCMenu(npc2.desc, npcIndices[j2], j1, i1);
						}
						for (int l2 = 0; l2 < stonerCount; l2++)
						{
							Stoner stoner = stonerArray[stonerIndices[l2]];
							if (stoner != null && stoner.x == npc.x && stoner.y == npc.y)
								buildAtStonerMenu(i1, stonerIndices[l2], stoner, j1);
						}
					}
					buildAtNPCMenu(npc.desc, l1, j1, i1);
				}
				catch (Exception e)
				{
				}
			}
			if (k1 == 0)
			{
				Stoner stoner = stonerArray[l1];
				if ((stoner.x & 0x7f) == 64 && (stoner.y & 0x7f) == 64)
				{
					for (int k2 = 0; k2 < npcCount; k2++)
					{
						Npc npc = npcArray[npcIndices[k2]];
						if (npc != null && npc.desc.size == 1
							&& npc.x == stoner.x
							&& npc.y == stoner.y)
							buildAtNPCMenu(npc.desc, npcIndices[k2], j1, i1);
					}

					for (int i3 = 0; i3 < stonerCount; i3++)
					{
						Stoner otherStoner = stonerArray[stonerIndices[i3]];
						if (otherStoner != null && otherStoner != stoner
							&& otherStoner.x == stoner.x
							&& otherStoner.y == stoner.y)
							buildAtStonerMenu(i1, stonerIndices[i3], otherStoner, j1);
					}

				}
				buildAtStonerMenu(i1, l1, stoner, j1);
			}
			if (k1 == 3)
			{
				ArrayDeque<Item> groundItemList = groundArray[plane][i1][j1];
				if (groundItemList != null)
				{
					for (Item item : groundItemList)
					{
						ItemDef itemDef = getItemDefinition(item.ID);
						if (itemSelected == 1)
						{
							menuActionName[menuActionRow] = "Select " + selectedItemName + ",combine with @lre@"
								+ itemDef.name;
							menuActionID[menuActionRow] = 511;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						}
						else if (spellSelected == 1)
						{
							if ((spellUsableOn & 1) == 1)
							{
								menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
								menuActionID[menuActionRow] = 94;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}
						}
						else
						{
							for (int j3 = 4; j3 >= 0; j3--)
								if (itemDef.groundActions != null && itemDef.groundActions[j3] != null)
								{
									menuActionName[menuActionRow] = itemDef.groundActions[j3] + " @lre@" + itemDef.name;
									if (j3 == 0)
										menuActionID[menuActionRow] = 652;
									if (j3 == 1)
										menuActionID[menuActionRow] = 567;
									if (j3 == 2)
										menuActionID[menuActionRow] = 234;
									if (j3 == 3)
										menuActionID[menuActionRow] = 244;
									if (j3 == 4)
										menuActionID[menuActionRow] = 213;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								}
								else if (j3 == 2)
								{
									menuActionName[menuActionRow] = "Take @lre@" + itemDef.name;
									menuActionID[menuActionRow] = 234;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								}
							menuActionName[menuActionRow] = "@lre@" + itemDef.name;
							menuActionID[menuActionRow] = 1448;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						}
					}

				}
			}
		}
	}
}
