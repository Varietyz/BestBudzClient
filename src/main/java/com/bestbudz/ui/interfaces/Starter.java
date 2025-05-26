package com.bestbudz.ui.interfaces;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class Starter extends RSInterface {
  public static void starter(TextDrawingArea[] jaybane) {
    RSInterface Interface = addInterface(51750);

    addSprite(51751, 431);
    addText(51752, " ", 0xff9933, true, true, 52, jaybane, 2);
    addHoverButton(51753, 17, 21, 21, "Close", 250, 51754, 3);
    addHoveredButton(51754, 18, 21, 21, 51755);
    addText(51756, "", 0xff9933, true, true, 52, jaybane, 1);
    addText(51757, "", 0xff9933, false, true, 52, jaybane, 1);
    itemDisplay(51758, 7, 15, 8, 3);
    addText(51759, "", 0xff9933, true, true, 52, jaybane, 2);
    addText(51760, "", 0xff9933, true, true, 52, jaybane, 0);
    addText(51761, "", 0xff9933, true, true, 52, jaybane, 0);
    addText(51762, "", 0xff9933, true, true, 52, jaybane, 0);

    addConfigButton(51763, 51750, 425, 426, 14, 15, "Select Stoner", 0, 5, 1085);
    addConfigButton(51764, 51750, 425, 426, 14, 15, "Select Dealer", 1, 5, 1085);
    addConfigButton(51765, 51750, 425, 426, 14, 15, "Select Grower", 2, 5, 1085);

    addHoverText(51766, "Stoner", "Select Stoner", jaybane, 0, 0xF7AA25, false, true, 250);
    addHoverText(51767, "Dealer", "Select Dealer", jaybane, 0, 0xF7AA25, false, true, 250);
    addHoverText(51768, "Grower", "Select Grower", jaybane, 0, 0xF7AA25, false, true, 250);

    addHoverButton(51769, -1, 123, 30, "Confirm selection", -1, 51770, 1);
    addHoveredButton(51770, 432, 123, 30, 51771);

    Interface.totalChildren(19);

    Interface.child(0, 51751, 7, 8);
    Interface.child(1, 51752, 250, 16);
    Interface.child(2, 51753, 480, 500);
    Interface.child(3, 51754, 480, 500);
    Interface.child(4, 51756, 415, 45);
    Interface.child(5, 51757, 25, 48);
    Interface.child(6, 51758, 24, 70);
    Interface.child(7, 51759, 250, 220);
    Interface.child(8, 51760, 250, 250);
    Interface.child(9, 51761, 250, 270);
    Interface.child(10, 51762, 250, 290);
    Interface.child(11, 51763, 366, 75);
    Interface.child(12, 51764, 366, 115);
    Interface.child(13, 51765, 366, 153);
    Interface.child(14, 51766, 388, 78);
    Interface.child(15, 51767, 388, 117);
    Interface.child(16, 51768, 388, 154);

    Interface.child(17, 51769, 364, 180);
    Interface.child(18, 51770, 364, 180);
  }
}
