package com.bestbudz.ui.interfaces;

import com.bestbudz.engine.core.Client;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class Bank extends RSInterface {
  public static void bank(TextDrawingArea[] wid) {
    RSInterface bank = addInterface(5292);

    setChildren(27, bank);

    int id = 50_000;

    Sprite button1 = method207(0, aClass44, "miscgraphics");
    Sprite button2 = method207(9, aClass44, "miscgraphics");

	  RSInterface background = addInterface(id);
	  background.type = 3; // filled rectangle
	  background.aBoolean227 = true; // draw filled
	  background.width = 670;
	  background.height = 340;
	  background.textColor = 0x000000; // black
	  background.opacity = (byte) 50;

	  addHoverButton(
        id + 1, method207(2, aClass44, "miscgraphics2"), 21, 21, "Close", 250, id + 2, 3);
    addHoveredButton(id + 2, method207(3, aClass44, "miscgraphics2"), 21, 21, id + 3);
    addContainer(
        5382,
        109,
        14,
        44,
		null,
		null,
		null,
		null,
		null,
        null,
		null);
   addConfigButton(id + 4, id + 4, button1, button2, 36, 36, "Search", 1, 5, 1012);

    interfaceCache[id + 4].contentType = 670;
    addSprite(id + 8, 118);
    addSprite(id + 9, 116);
    addSprite(id + 10, 117);
    addSprite(id + 11, 115);

    addSprite(id + 12, 113);

    int child = 0;

	  bank.child(child++, id, 12, 2);                // 50000 - Main bank background (rect fill)

	  bank.child(child++, id + 1, 632, 9);           // 50001 - Close button (hoverable)
	  bank.child(child++, id + 2, 632, 9);           // 50002 - Close button (hovered)

    interfaceCache[5385].width = 606; // SCROLLBAR
    interfaceCache[5385].height -= 18;
    interfaceCache[5382].contentType = 206;

	  bank.child(child++, 5385, 31, 47);             // 5385 - Bank item container scroll area

  }
}
