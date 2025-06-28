package com.bestbudz.data.items;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.model.Model;

public final class Item extends Animable {

  public int ID;
  public int x;
  public int y;
  public int stackSize;
  public Item() {}

  public Model getModel() {
    ItemDef itemDef = getItemDefinition(ID);
    return itemDef.getStackedModel(stackSize);
  }
}
