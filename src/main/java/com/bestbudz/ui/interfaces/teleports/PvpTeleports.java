package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class PvpTeleports extends RSInterface {
  public static void pvpTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(63000);
    addSprite(63001, 65);
    addHoverButton(63002, 17, 21, 21, "Close", 250, 63003, 3);
    addHoveredButton(63003, 18, 21, 21, 63004);
    addHoverButton(63005, 59, 123, 30, "Training", 0, 63006, 1);
    addHoveredButton(63006, 60, 123, 30, 63007);
    addHoverButton(63008, 59, 123, 30, "Skilling", 0, 63009, 1);
    addHoveredButton(63009, 60, 123, 30, 63010);
    addHoverButton(63011, 59, 123, 30, "Stoner Vs Stoner", 0, 63012, 1);
    addHoveredButton(63012, 60, 123, 30, 63013);
    addHoverButton(63014, 59, 123, 30, "Boss", 0, 63015, 1);
    addHoveredButton(63015, 60, 123, 30, 63016);
    addHoverButton(63017, 59, 123, 30, "Minigame", 0, 63018, 1);
    addHoveredButton(63018, 60, 123, 30, 63019);
    addHoverButton(63020, 59, 123, 30, "Other", 0, 63021, 1);
    addHoveredButton(63021, 60, 123, 30, 63022);
    addText(
        63023,
        "@bla@Teleportation Menu (@red@Stoner Vs Stoner@bla@)",
        0x20CF2F,
        true,
        true,
        -1,
        bestbudz,
        2);
    addText(63024, "Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63025, "Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63026, "@bla@Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63027, "Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63028, "Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63029, "Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(63030, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(63031, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(63032, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(63033, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(63034, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(63035, 61, 123, 30, "Confirm Teleport", 0, 63036, 1);
    addHoveredButton(63036, 62, 123, 30, 63037);
    addText(63038, " ", 0x20CF2F, true, true, -1, bestbudz, 3);
    tab.totalChildren(31);
    tab.child(0, 63001, 11, 2);
    tab.child(1, 63002, 475, 8);
    tab.child(2, 63003, 475, 8);
    tab.child(3, 63005, 16, 35);
    tab.child(4, 63006, 16, 35);
    tab.child(5, 63008, 136, 35);
    tab.child(6, 63009, 136, 35);
    tab.child(7, 63011, 256, 35);
    tab.child(8, 63012, 256, 35);
    tab.child(9, 63014, 376, 35);
    tab.child(10, 63015, 376, 35);
    tab.child(11, 63017, 136, 61);
    tab.child(12, 63018, 136, 61);
    tab.child(13, 63020, 256, 61);
    tab.child(14, 63021, 256, 61);
    tab.child(15, 63023, 255, 9);
    tab.child(16, 63024, 75, 39);
    tab.child(17, 63025, 195, 39);
    tab.child(18, 63026, 315, 39);
    tab.child(19, 63027, 435, 39);
    tab.child(20, 63028, 195, 65);
    tab.child(21, 63029, 315, 65);
    tab.child(22, 63030, 110, 117);
    tab.child(23, 63031, 40, 140);
    tab.child(24, 63032, 40, 160);
    tab.child(25, 63033, 40, 180);
    tab.child(26, 63034, 40, 200);
    tab.child(27, 63035, 70, 250);
    tab.child(28, 63036, 70, 250);
    tab.child(29, 63038, 110, 257);
    tab.child(30, 63050, -70, 109);
    RSInterface scrollInterface = addTabInterface(63050);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 270;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(63051 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(63051 + i, 260, y, i, scrollInterface);
      y += 30;
    }
  }
}
