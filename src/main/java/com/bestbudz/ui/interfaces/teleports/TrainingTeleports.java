package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class TrainingTeleports extends RSInterface {
  public static void trainingTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(61000);
    addSprite(61001, 63);
    addHoverButton(61002, 17, 21, 21, "Close", 250, 61003, 3);
    addHoveredButton(61003, 18, 21, 21, 61004);
    addHoverButton(61005, 59, 123, 30, "Training", 0, 61006, 1);
    addHoveredButton(61006, 60, 123, 30, 61007);
    addHoverButton(61008, 59, 123, 30, "Skilling", 0, 61009, 1);
    addHoveredButton(61009, 60, 123, 30, 61010);
    addHoverButton(61011, 59, 123, 30, "Stoner Vs Stoner", 0, 61012, 1);
    addHoveredButton(61012, 60, 123, 30, 61013);
    addHoverButton(61014, 59, 123, 30, "Boss", 0, 61015, 1);
    addHoveredButton(61015, 60, 123, 30, 61016);
    addHoverButton(61017, 59, 123, 30, "Minigame", 0, 61018, 1);
    addHoveredButton(61018, 60, 123, 30, 61019);
    addHoverButton(61020, 59, 123, 30, "Other", 0, 61021, 1);
    addHoveredButton(61021, 60, 123, 30, 61022);
    addText(
        61023,
        "@bla@Teleportation Menu (@red@Training@bla@)",
        0x20CF2F,
        true,
        true,
        -1,
        bestbudz,
        2);
    addText(61024, "@bla@Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61025, "Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61026, "Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61027, "Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61028, "Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61029, "Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61030, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(61031, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61032, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61033, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61034, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(61035, 61, 123, 30, "Confirm Teleport", 0, 61036, 1);
    addHoveredButton(61036, 62, 123, 30, 61037);
    addText(61038, " ", 0x20CF2F, true, true, -1, bestbudz, 3);
    tab.totalChildren(31);
    tab.child(0, 61001, 11, 2);
    tab.child(1, 61002, 475, 8);
    tab.child(2, 61003, 475, 8);
    tab.child(3, 61005, 16, 35);
    tab.child(4, 61006, 16, 35);
    tab.child(5, 61008, 136, 35);
    tab.child(6, 61009, 136, 35);
    tab.child(7, 61011, 256, 35);
    tab.child(8, 61012, 256, 35);
    tab.child(9, 61014, 376, 35);
    tab.child(10, 61015, 376, 35);
    tab.child(11, 61017, 136, 61);
    tab.child(12, 61018, 136, 61);
    tab.child(13, 61020, 256, 61);
    tab.child(14, 61021, 256, 61);
    tab.child(15, 61023, 255, 9);
    tab.child(16, 61024, 75, 39);
    tab.child(17, 61025, 195, 39);
    tab.child(18, 61026, 315, 39);
    tab.child(19, 61027, 435, 39);
    tab.child(20, 61028, 195, 65);
    tab.child(21, 61029, 315, 65);
    tab.child(22, 61030, 110, 117);
    tab.child(23, 61031, 40, 140);
    tab.child(24, 61032, 40, 160);
    tab.child(25, 61033, 40, 180);
    tab.child(26, 61034, 40, 200);
    tab.child(27, 61035, 70, 250);
    tab.child(28, 61036, 70, 250);
    tab.child(29, 61038, 110, 257);
    tab.child(30, 61050, -70, 109);
    RSInterface scrollInterface = addTabInterface(61050);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 250;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(61051 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(61051 + i, 260, y, i, scrollInterface);
      y += 25;
    }
  }
}
