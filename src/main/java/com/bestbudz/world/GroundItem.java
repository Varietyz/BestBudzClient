package com.bestbudz.world;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.Item;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.Animable;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

import java.util.ArrayDeque;
import java.util.Iterator;

public class GroundItem extends Client
{
	public static void spawnGroundItem(int i, int j)
	{
		ArrayDeque<Item> class19 = groundArray[plane][i][j];
		if (class19 == null || class19.isEmpty())
		{
			worldController.removeRoofDecoration(plane, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Item bestItem = null;
		for (Item item : class19)
		{
			ItemDef itemDef = getItemDefinition(item.ID);
			int l = itemDef.value;
			if (itemDef.stackable)
			{
				l *= item.stackSize + 1;
			}
			if (l > k)
			{
				k = l;
				bestItem = item;
			}
		}
		// Move best item to last position (highest value rendered on top)
		if (bestItem != null) {
			class19.remove(bestItem);
			class19.addLast(bestItem);
		}
		Item obj1 = null;
		Item obj2 = null;
		for (Item class30_sub2_sub4_sub2_1 : class19)
		{
			if (class30_sub2_sub4_sub2_1.ID != bestItem.ID && obj1 == null)
			{
				obj1 = class30_sub2_sub4_sub2_1;
			}
			if (class30_sub2_sub4_sub2_1.ID != bestItem.ID && (obj1 == null || class30_sub2_sub4_sub2_1.ID != obj1.ID)
				&& obj2 == null)
			{
				obj2 = class30_sub2_sub4_sub2_1;
			}
		}
		int i1 = i + (j << 7) + 0x60000000;
		worldController.addRoofDecoration(i, i1, ((Animable) (obj1)), getTerrainHeight(plane, j * 128 + 64, i * 128 + 64),
			((Animable) (obj2)), ((Animable) (bestItem)), plane, j);
	}
}
