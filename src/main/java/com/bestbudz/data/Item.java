package com.bestbudz.data;

import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.model.Model;

public final class Item extends Animable {

  public int ID;
  public int x;
  public int y;
  public int anInt1559;
  public Item() {}

  public Model getFinalRenderedModel() {
    ItemDef itemDef = ItemDef.getItemDefinition(ID);
    return itemDef.getStackedModel(anInt1559);
  }
}
