package com.bestbudz.ui.interfaces.minigames;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class WarriorGuild extends RSInterface {
  public static void warriorGuild(TextDrawingArea[] jaybane) {
    RSInterface tab = addInterface(51200);
    addText(51202, "Warrior's Guild", jaybane, 2, 0xff981f, true, true);
    itemDisplay(51203, 30, 30, 4, 5);
    addText(51204, "Dropping:", jaybane, 0, 0xff981f, true, true);
    addText(51205, "Tokens Used:", jaybane, 0, 0xff981f, true, true);
    addText(51206, "Cyclops Killed:", jaybane, 0, 0xff981f, true, true);
    tab.totalChildren(5);
    tab.child(0, 51202, 460, 215);
    tab.child(1, 51203, 450, 260);
    tab.child(2, 51204, 460, 235);
    tab.child(3, 51205, 460, 300);
    tab.child(4, 51206, 460, 320);
  }
}
