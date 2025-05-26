package com.bestbudz.ui.interfaces.minigames;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class PestControl extends RSInterface {
  public static void pestControlBoat(TextDrawingArea[] tda) {
    RSInterface RSinterface = addInterface(21119);
    addText(21120, "", 0x999999, false, true, 52, tda, 1);
    addText(21121, "", 0x33cc00, false, true, 52, tda, 1);
    addText(21122, "", 0xFFcc33, false, true, 52, tda, 1);
    addText(21123, "", 0x33ccff, false, true, 52, tda, 1);
    int last = 4;
    RSinterface.children = new int[last];
    RSinterface.childX = new int[last];
    RSinterface.childY = new int[last];
    setBounds(21120, 15, 12, 0, RSinterface);
    setBounds(21121, 15, 30, 1, RSinterface);
    setBounds(21122, 15, 48, 2, RSinterface);
    setBounds(21123, 15, 66, 3, RSinterface);
  }

  public static void pestControlGame(TextDrawingArea[] tda) {
    RSInterface RSinterface = addInterface(21100);
    addSprite(21101, 353);
    addSprite(21102, 354);
    addSprite(21103, 355);
    addSprite(21104, 356);
    addSprite(21105, 357);
    addSprite(21106, 358);
    addText(21107, "", 0xCC00CC, false, true, 52, tda, 1);
    addText(21108, "", 0xf7dd45, false, true, 52, tda, 1);
    addText(21109, "", 0xFFFF44, false, true, 52, tda, 1);
    addText(21110, "", 0xCC0000, false, true, 52, tda, 1);
    addText(21111, "250", 0x99FF33, false, true, 52, tda, 1);
    addText(21112, "250", 0x99FF33, false, true, 52, tda, 1);
    addText(21113, "250", 0x99FF33, false, true, 52, tda, 1);
    addText(21114, "250", 0x99FF33, false, true, 52, tda, 1);
    addText(21115, "200", 0x99FF33, false, true, 52, tda, 1);
    addText(21116, "0", 0x99FF33, false, true, 52, tda, 1);
    addText(21117, "Time Remaining:", 0xFFFFFF, false, true, 52, tda, 0);
    addText(21118, "", 0xFFFFFF, false, true, 52, tda, 0);
    int last = 18;
    RSinterface.children = new int[last];
    RSinterface.childX = new int[last];
    RSinterface.childY = new int[last];
    setBounds(21101, 361, 26, 0, RSinterface);
    setBounds(21102, 396, 26, 1, RSinterface);
    setBounds(21103, 436, 26, 2, RSinterface);
    setBounds(21104, 474, 26, 3, RSinterface);
    setBounds(21105, 3, 21, 4, RSinterface);
    setBounds(21106, 3, 50, 5, RSinterface);
    setBounds(21107, 371, 60, 6, RSinterface);
    setBounds(21108, 409, 60, 7, RSinterface);
    setBounds(21109, 443, 60, 8, RSinterface);
    setBounds(21110, 479, 60, 9, RSinterface);
    setBounds(21111, 362, 10, 10, RSinterface);
    setBounds(21112, 398, 10, 11, RSinterface);
    setBounds(21113, 436, 10, 12, RSinterface);
    setBounds(21114, 475, 10, 13, RSinterface);
    setBounds(21115, 32, 32, 14, RSinterface);
    setBounds(21116, 32, 62, 15, RSinterface);
    setBounds(21117, 8, 88, 16, RSinterface);
    setBounds(21118, 87, 88, 17, RSinterface);
  }
}
