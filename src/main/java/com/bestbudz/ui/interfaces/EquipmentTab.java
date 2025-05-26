package com.bestbudz.ui.interfaces;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class EquipmentTab extends RSInterface {
  public static void equipmentScreen(TextDrawingArea[] bestbudz) {
    RSInterface tab = addTabInterface(15106);
    addSprite(15107, 54);
    addHoverButton(15210, 21, 21, 21, "Close", 250, 15211, 3);
    addHoveredButton(15211, 22, 21, 21, 15212);
    addText(15111, "", bestbudz, 2, 0xe4a146, false, true);
    addText(15112, "Assault bonus", bestbudz, 2, 0xFF981F, false, true);
    addText(15113, "Aegis bonus", bestbudz, 2, 0xFF981F, false, true);
    addText(15114, "Other bonuses", bestbudz, 2, 0xFF981F, false, true);
    addText(15115, "Equipment Bonuses", bestbudz, 2, 0xFF981F, false, true);
    addText(15116, "", bestbudz, 0, 0xFF981F, true, true);
    addText(15117, "", bestbudz, 0, 0xFF981F, true, true);
    addText(15118, "", bestbudz, 0, 0xFF981F, true, true);
    for (int i = 1675; i <= 1684; i++) {
      textSize(i, bestbudz, 1);
    }
    textSize(1686, bestbudz, 1);
    textSize(1687, bestbudz, 1);
    addChar(15125, 560);
    tab.totalChildren(48);
    tab.child(0, 15107, 15, 5);
    tab.child(1, 15210, 480, 9);
    tab.child(2, 15211, 480, 9);
    tab.child(3, 15111, 14, 30);
    int shift = 3;
    int rofl = 2;
    int Child = 4;
    int Y = 44 + shift;
    tab.child(16, 15112, 23, 30 + shift - rofl);
    for (int i = 1675; i <= 1679; i++) {
      tab.child(Child, i, 27, Y - rofl);
      Child++;
      Y += 14;
    }
	  tab.child(17, 15113, 24, 122 - rofl + shift);
    tab.child(9, 1680, 27, 137 - rofl - 1 + shift);
    tab.child(10, 1681, 27, 153 - rofl - 3 + shift);
    tab.child(11, 1682, 27, 168 - rofl - 4 + shift);
    tab.child(12, 1683, 27, 183 - rofl - 5 + shift);
    tab.child(13, 1684, 27, 197 - rofl - 6 + shift);
    tab.child(18, 15114, 24, 211 + shift);
    tab.child(14, 1686, 27, 250 - 24 + shift);
    tab.child(15, 15125, 188, 185);
    tab.child(19, 1645, 104 + 295, 149 - 52);
    tab.child(20, 1646, 399, 163);
    tab.child(21, 1647, 399, 163);
    tab.child(22, 1648, 399, 58 + 146);
    tab.child(23, 1649, 26 + 22 + 297 - 2, 110 - 44 + 118 - 13 + 5);
    tab.child(24, 1650, 321 + 22, 58 + 154);
    tab.child(25, 1651, 321 + 134, 58 + 118);
    tab.child(26, 1652, 321 + 134, 58 + 154);
    tab.child(27, 1653, 321 + 48, 58 + 81);
    tab.child(28, 1654, 321 + 107, 58 + 81);
    tab.child(29, 1655, 321 + 58, 58 + 42);
    tab.child(30, 1656, 321 + 112, 58 + 41);
    tab.child(31, 1657, 321 + 78, 58 + 4);
    tab.child(32, 1658, 321 + 37, 58 + 43);
    tab.child(33, 1659, 321 + 78, 58 + 43);
    tab.child(34, 1660, 321 + 119, 58 + 43);
    tab.child(35, 1661, 321 + 22, 58 + 82);
    tab.child(36, 1662, 321 + 78, 58 + 82);
    tab.child(37, 1663, 321 + 134, 58 + 82);
    tab.child(38, 1664, 321 + 78, 58 + 122);
    tab.child(39, 1665, 321 + 78, 58 + 162);
    tab.child(40, 1666, 321 + 22, 58 + 162);
    tab.child(41, 1667, 321 + 134, 58 + 162);
    tab.child(42, 1688, 50 + 297 - 2, 110 - 13 + 5);
    tab.child(43, 1687, 27, 250 - 24 + 14 + shift);
    tab.child(44, 15115, 195, 9);
    tab.child(45, 15116, 420, 275);
    tab.child(46, 15117, 420, 290);
    tab.child(47, 15118, 420, 305);
    for (int i = 1675; i <= 1684; i++) {
      RSInterface rsi = interfaceCache[i];
      rsi.textColor = 0xFF9200;
      rsi.centerText = false;
    }
    for (int i = 1686; i <= 1687; i++) {
      RSInterface rsi = interfaceCache[i];
      rsi.textColor = 0xFF9200;
      rsi.centerText = false;
    }
  }

  public static void equipmentTab() {
    RSInterface Interface = interfaceCache[1644];
    removeSomething(21338);
    removeSomething(21344);
    removeSomething(21342);
    removeSomething(21341);
    removeSomething(21340);
    removeSomething(15103);
    removeSomething(15104);
    Interface.children[26] = 27650;
    Interface.childX[26] = 0;
    Interface.childY[26] = 0;
    Interface = addInterface(27650);
    addHoverButton(15201, 46, 40, 40, "Show Equipment Screen", 0, 15202, 1);
    addHoveredButton(15202, 47, 40, 40, 15203);
    addHoverButton(15204, 48, 40, 40, "Items Kept on Death", 0, 15205, 1);
    addHoveredButton(15205, 49, 40, 40, 15206);
    addHoverButton(15207, 50, 40, 40, "Price Checker", 0, 15208, 1);
    addHoveredButton(15208, 51, 40, 40, 15209);
    addHoverButton(15310, 52, 40, 40, "Experience Toggle", 0, 15311, 1);
    addHoveredButton(15311, 53, 40, 40, 15312);
    setChildren(8, Interface);
    setBounds(15201, 21, 210, 0, Interface);
    setBounds(15202, 21, 210, 1, Interface);
    setBounds(15204, 132, 210, 2, Interface);
    setBounds(15205, 132, 210, 3, Interface);
    setBounds(15207, 76, 210, 4, Interface);
    setBounds(15208, 76, 210, 5, Interface);
    setBounds(15310, 5, 2, 6, Interface);
    setBounds(15311, 5, 2, 7, Interface);
  }
}
