package com.bestbudz.ui.interfaces;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;
import static com.bestbudz.ui.interfaces.CustomInterfaces.shopCategories;
import static com.bestbudz.ui.interfaces.CustomInterfaces.shopContent;

public class Shop extends RSInterface {
  static void shop(TextDrawingArea[] bestbudz) {
    RSInterface rsinterface = addInterface(3824);
    setChildren(8 + 36, rsinterface);
    addSprite(3825, 75);

    addHoverButton(3902, 17, 21, 21, "Close store", 0, 3826, 1);
    addHoveredButton(3826, 18, 21, 21, 3827);

    addText(19679, "", 0xff981f, false, true, 52, bestbudz, 1);
    addText(19680, "", 0xbf751d, false, true, 52, bestbudz, 1);
    addButton(19681, 2, "Interfaces/Shop/SHOP", 0, 0, "", 1);
    addSprite(19687, 1, "Interfaces/Shop/ITEMBG");
    for (int i = 0; i < 36; i++) {
      addText(28000 + i, "-1,0", 0xffff00, false, false, 52, bestbudz, 0);
    }
    setBounds(3825, 6, 8, 0, rsinterface);
    setBounds(3902, 479, 15, 1, rsinterface);
    setBounds(3826, 479, 15, 2, rsinterface);
    setBounds(3900, 32, 50, 3, rsinterface);
    setBounds(3901, 240, 17, 4, rsinterface);
    setBounds(19679, 42, 54, 5, rsinterface);
    setBounds(19680, 150, 54, 6, rsinterface);
    setBounds(19681, 129, 50, 7, rsinterface);
    for (int i = 0; i < 36; i++) {
      int x = i % 9;
      int y = i / 9;
      x = 52 * x + 32;
      y = 65 * y + 84;
      setBounds(28000 + i, x, y, 8 + i, rsinterface);
    }
    rsinterface = interfaceCache[3900];
    setChildren(1, rsinterface);
    setBounds(19687, 6, 15, 0, rsinterface);
    rsinterface.invSpritePadX = 20;
    rsinterface.width = 9;
    rsinterface.height = 4;
    rsinterface.invSpritePadY = 33;
    rsinterface.spritesX = new int[36];
    rsinterface.spritesY = new int[36];
    rsinterface.inv = new int[36];
    rsinterface.invStackSizes = new int[36];
    rsinterface = addInterface(19682);
    addSprite(19683, 1, "Interfaces/Shop/SHOP");
    addText(19684, "Main Stock", 0xbf751d, false, true, 52, bestbudz, 1);
    addText(19685, "Store Info", 0xff981f, false, true, 52, bestbudz, 1);
    addButton(19686, 2, "Interfaces/Shop/SHOP", 95, 19, "Main Stock", 1);
    setChildren(7, rsinterface);
    setBounds(19683, 12, 12, 0, rsinterface);
    setBounds(3901, 240, 21, 1, rsinterface);
    setBounds(19684, 42, 54, 2, rsinterface);
    setBounds(19685, 150, 54, 3, rsinterface);
    setBounds(19686, 23, 50, 4, rsinterface);
    setBounds(3902, 471, 22, 5, rsinterface);
    setBounds(3826, 60, 85, 6, rsinterface);
  }

  public static void loyaltyShop(TextDrawingArea[] tda) {
    int id = 55000;
    RSInterface shop = addTabInterface(id);
    addSprite(id + 1, 372);
    addHoverButton(id + 2, 17, 21, 21, "Close", 250, id + 3, 3);
    addHoveredButton(id + 3, 18, 21, 21, id + 4);
    addText(id + 5, "Weed Title Shop", tda, 0, 0xe6be78, true, true);
    addSprite(id + 6, 407);
    addText(id + 7, "CannaCredits", tda, 0, 0xe6be78, false, true);
    addText(id + 8, "The Weed Title shop.", tda, 0, 0xe6be78, false, true);

    shop.totalChildren(8 + shopCategories.length * 3);
    for (int index = 0, frame = 0, configOffset = 0;
        index < shopCategories.length * 3;
        index += 3) {
      addHoverButton(
          id + frame + 9, 380, 120, 28, shopCategories[index / 3][0], 0xBABE, id + frame + 10, 1);
      addHoveredButton(id + frame + 10, 381, 120, 28, id + frame + 11);
      addText(id + frame + 12, shopCategories[index / 3][0], tda, 0, 0xE6BE78, false, true);

      loyaltySubShop(
          id + frame + 13,
          id,
          configOffset,
          tda,
          shopContent[index / 3],
          Integer.parseInt(shopCategories[index / 3][1]));

      shop.child(index + 7, id + frame + 9, 9, 100 + (index / 3) * 30);
      shop.child(index + 8, id + frame + 10, 9, 100 + (index / 3) * 30);
      shop.child(index + 9, id + frame + 12, 38, 108 + (index / 3) * 30);
      frame += shopContent[index / 3].length * 8 + 5;
      configOffset += shopContent[index / 3].length;
    }

    shop.child(0, id + 1, 1, 15);
    shop.child(1, id + 2, 484, 22);
    shop.child(2, id + 3, 484, 22);
    shop.child(3, id + 5, 65, 55);
    shop.child(4, id + 6, 17, 78);
    shop.child(5, id + 7, 35, 78);
    shop.child(6, id + 8, 223, 53);
    shop.child(8 + shopCategories.length * 3 - 1, id + 13, 131, 70);
  }

  public static void loyaltySubShop(
      int id,
      int parent,
      int configOffset,
      TextDrawingArea[] tda,
      String[][] titles,
      int currency) {
    RSInterface shop = addTabInterface(id);
    shop.totalChildren(titles.length * 7);
    int frame = 0;
    for (int index = 0; index < titles.length * 7; index += 7) {
      String title = titles[(index / 7)][0];
      String price = titles[(index / 7)][1];
      int x = ((index / 7) % 3) * 120;
      int y = ((index / 7) / 3) * 50;
      addSprite(id + frame + 1, 377);

      addHoverConfigButton(
          id + frame + 2,
          id + frame + 3,
          374,
          375,
          56,
          11,
          "Buy",
          new int[] {0},
          new int[] {0},
          new int[][] {{1040 + index / 7 + configOffset}});
      addHoveredConfigButton(
          interfaceCache[id + frame + 2], id + frame + 3, id + frame + 4, 373, 376);

      addText(id + frame + 5, title, tda, 0, 0xE6BE78, true, true);
      addSprite(id + frame + 6, currency);
      addText(id + frame + 7, price, tda, 0, 0xDB9000, false, true);
      addText(id + frame + 8, "Buy", tda, 0, 0xE6BE78, false, true);

      shop.child(index, id + frame + 1, x, y);
      shop.child(index + 1, id + frame + 2, x + 48, y + 32);
      shop.child(index + 2, id + frame + 3, x + 48, y + 32);
      shop.child(index + 3, id + frame + 5, x + 73, y + 8);
      shop.child(index + 4, id + frame + 6, x + 39, y + 21);
      shop.child(index + 5, id + frame + 7, x + 51, y + 21);
      shop.child(index + 6, id + frame + 8, x + 67, y + 32);

      frame += 8;
    }
    shop.width = 355;
    shop.height = 236;
    shop.scrollMax = (titles.length / 3 + (titles.length % 3 > 0 ? 1 : 0)) * 50;
    shop.parentID = parent;
  }
}
