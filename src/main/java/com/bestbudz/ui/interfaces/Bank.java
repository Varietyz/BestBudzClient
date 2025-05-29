package com.bestbudz.ui.interfaces;

import com.bestbudz.engine.core.Client;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class Bank extends RSInterface {
  public static void bank(TextDrawingArea[] wid) {
    RSInterface bank = addInterface(5292);

    setChildren(57, bank);

    int id = 50_000;

    Sprite disabled = Client.cacheSprite[137];
    Sprite enabled = Client.cacheSprite[138];
    Sprite button1 = method207(0, aClass44, "miscgraphics");
    Sprite button2 = method207(9, aClass44, "miscgraphics");

	  RSInterface background = addInterface(id);
	  background.type = 3; // filled rectangle
	  background.aBoolean227 = true; // draw filled
	  background.width = 670;  // adjust to match original sprite size
	  background.height = 340;
	  background.textColor = 0x000000; // black
	  background.opacity = (byte) 50;

	  addHoverButton(
        id + 1, method207(2, aClass44, "miscgraphics2"), 21, 21, "Close", 250, id + 2, 3);
    addHoveredButton(id + 2, method207(3, aClass44, "miscgraphics2"), 21, 21, id + 3);
    addContainer(
        5382,
        109,
        8,
        44,
        "Withdraw-1",
        "Withdraw-5",
        "Withdraw-10",
        "Withdraw-All",
        "Withdraw-X",
        null,
        "Withdraw-All but one");
    addConfigButton(id + 4, id + 4, button1, button2, 36, 36, "Search", 1, 5, 1012);
    interfaceCache[id + 4].contentType = 555;
    addButton(id + 5, button2, button1, "Deposit box", 36, 36);
    addButton(id + 6, button2, button1, "Deposit worn equipment", 36, 36);
    addButton(
        id + 7,
        method207(0, aClass44, "miscgraphics3"),
        method207(0, aClass44, "miscgraphics3"),
        "Show menu",
        25,
        25);
    addSprite(id + 8, 118);
    addSprite(id + 9, 116);
    addSprite(id + 10, 117);
    addSprite(id + 11, 115);

    addSprite(id + 12, 113);

    addText(id + 53, "%1", wid, 0, 0xFE9624, true);
    RSInterface line = addInterface(id + 54);
    line.type = 3;
    line.aBoolean227 = true;
    line.width = 14;
    line.height = 1;
    line.textColor = 0xFE9624;
    addText(id + 55, "350", wid, 0, 0xFE9624, true);

    interfaceCache[id + 53].valueIndexArray = new int[][] {{22, 5382, 0}};

    int child = 0;

    bank.child(child++, id, 12, 2);
    bank.child(child++, id + 1, 472, 9);
    bank.child(child++, id + 2, 472, 9);

    bank.child(child++, id + 53, 30, 8);
    bank.child(child++, id + 54, 24, 19);
    bank.child(child++, id + 55, 30, 20);

    for (int tab = 0; tab < 40; tab += 4) {
      addButton(
          id + 13 + tab,
          Client.cacheSprite[111],
          Client.cacheSprite[111],
          "Collapse " + (tab == 0 ? "@or2@all tabs" : "tab @or2@" + (tab / 4)),
          39,
          40);
      int[] array = {21, (tab / 4), 0};
      if (tab / 4 == 0) {
        array = new int[] {5, 1000, 0};
      }
      addHoverConfigButton(
          id + 14 + tab,
          id + 15 + tab,
          109,
          111,
          39,
          40,
          tab == 0 ? "View all" : "New tab",
          new int[] {1, tab / 4 == 0 ? 1 : 3},
          new int[] {(tab / 4), 0},
          new int[][] {{5, 1000, 0}, array});
      addHoveredConfigButton(interfaceCache[id + 14 + tab], id + 15 + tab, id + 16 + tab, 110, 111);
      interfaceCache[id + 14 + tab].parentID = id;
      interfaceCache[id + 15 + tab].parentID = id;
      interfaceCache[id + 16 + tab].parentID = id;
      bank.child(child++, id + 13 + tab, 57 + 40 * (tab / 4), 37);
      bank.child(child++, id + 14 + tab, 57 + 40 * (tab / 4), 37);
      bank.child(child++, id + 15 + tab, 57 + 40 * (tab / 4), 37);
    }

    interfaceCache[5385].width += 20;
    interfaceCache[5385].height -= 18;
    interfaceCache[5382].contentType = 206;

    int[] interfaces = new int[] {5386, 5387, 8130, 8131};

    for (int rsint : interfaces) {
      interfaceCache[rsint].disabledSprite = disabled;
      interfaceCache[rsint].enabledSprite = enabled;
      interfaceCache[rsint].width = enabled.myWidth;
      interfaceCache[rsint].height = enabled.myHeight;
    }

    bank.child(child++, id + 12, 59, 41);

    bank.child(child++, 5383, 180, 12);
    bank.child(child++, 5385, 31, 77);

    bank.child(child++, 8131, 102, 306);
    bank.child(child++, 8130, 17, 306);

    bank.child(child++, 5386, 282, 306);
    bank.child(child++, 5387, 197, 306);
    bank.child(child++, 8132, 127, 309);
    bank.child(child++, 8133, 45, 309);
    bank.child(child++, 5390, 54, 291);

    bank.child(child++, 5389, 227, 309);
    bank.child(child++, 5391, 311, 309);
    bank.child(child++, 5388, 248, 291);
    bank.child(child++, id + 4, 376, 291);
    bank.child(child++, id + 5, 417, 291);
    bank.child(child++, id + 6, 458, 291);
    bank.child(child++, id + 7, 463, 44);
    bank.child(child++, id + 8, 379, 298);
    bank.child(child++, id + 9, 420, 298);
    bank.child(child++, id + 10, 461, 298);
    bank.child(child++, id + 11, 463, 44);
  }

  public static void bankSettings(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(32500);
    addSprite(32501, 368);
    addText(32502, "BestBudz Bank Settings", 0xff9933, true, true, -1, bestbudz, 2);
    addHoverButton(32503, 17, 21, 21, "Close", 250, 32504, 3);
    addHoveredButton(32504, 18, 21, 21, 32505);
    addConfigButton(32506, 32500, 289, 290, 14, 15, "Select", 0, 5, 1011);
    addConfigButton(32507, 32500, 289, 290, 14, 15, "Select", 1, 5, 1011);
    addConfigButton(32508, 32500, 289, 290, 14, 15, "Select", 2, 5, 1011);
    addText(32509, "First item in tab", 0xff9933, true, true, -1, bestbudz, 1);
    addText(32510, "Digit (1, 2, 3)", 0xff9933, true, true, -1, bestbudz, 1);
    addText(32511, "Roman numeral (I, II, III)", 0xff9933, true, true, -1, bestbudz, 1);
    addHoverText(32512, "Back to bank", "View", bestbudz, 1, 0xcc8000, true, true, 100, 0xFFFFFF);
    tab.totalChildren(11);
    tab.child(0, 32501, 115, 35);
    tab.child(1, 32502, 263, 44);
    tab.child(2, 32503, 373, 42);
    tab.child(3, 32504, 373, 42);
    tab.child(4, 32506, 150, 65 + 30);
    tab.child(5, 32507, 150, 65 + 60);
    tab.child(6, 32508, 150, 65 + 90);
    tab.child(7, 32509, 218, 65 + 30);
    tab.child(8, 32510, 210, 65 + 60);
    tab.child(9, 32511, 239, 65 + 90);
    tab.child(10, 32512, 275, 265);
  }
}
