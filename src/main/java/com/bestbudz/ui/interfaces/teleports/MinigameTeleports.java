package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class MinigameTeleports extends RSInterface {
  public static void minigameTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(65000);
    addSprite(65001, 67);
    addHoverButton(65002, 17, 21, 21, "Close", 250, 65003, 3);
    addHoveredButton(65003, 18, 21, 21, 65004);
    addHoverButton(65005, 59, 123, 30, "Training", 0, 65006, 1);
    addHoveredButton(65006, 60, 123, 30, 65007);
    addHoverButton(65008, 59, 123, 30, "Skilling", 0, 65009, 1);
    addHoveredButton(65009, 60, 123, 30, 65010);
    addHoverButton(65011, 59, 123, 30, "Stoner Vs Stoner", 0, 65012, 1);
    addHoveredButton(65012, 60, 123, 30, 65013);
    addHoverButton(65014, 59, 123, 30, "Boss", 0, 65015, 1);
    addHoveredButton(65015, 60, 123, 30, 65016);
    addHoverButton(65017, 59, 123, 30, "Minigame", 0, 65018, 1);
    addHoveredButton(65018, 60, 123, 30, 65019);
    addHoverButton(65020, 59, 123, 30, "Other", 0, 65021, 1);
    addHoveredButton(65021, 60, 123, 30, 65022);
    addText(
        65023,
        "@bla@Teleportation Menu (@red@Minigame@bla@)",
        0x20CF2F,
        true,
        true,
        -1,
        bestbudz,
        2);
    addText(65024, "Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65025, "Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65026, "Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65027, "Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65028, "@bla@Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65029, "Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(65030, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(65031, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(65032, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(65033, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(65034, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(65035, 61, 123, 30, "Confirm Teleport", 0, 65036, 1);
    addHoveredButton(65036, 62, 123, 30, 65037);
    addText(65038, " ", 0x20CF2F, true, true, -1, bestbudz, 3);
    tab.totalChildren(31);
    tab.child(0, 65001, 11, 2);
    tab.child(1, 65002, 475, 8);
    tab.child(2, 65003, 475, 8);
    tab.child(3, 65005, 16, 35);
    tab.child(4, 65006, 16, 35);
    tab.child(5, 65008, 136, 35);
    tab.child(6, 65009, 136, 35);
    tab.child(7, 65011, 256, 35);
    tab.child(8, 65012, 256, 35);
    tab.child(9, 65014, 376, 35);
    tab.child(10, 65015, 376, 35);
    tab.child(11, 65017, 136, 61);
    tab.child(12, 65018, 136, 61);
    tab.child(13, 65020, 256, 61);
    tab.child(14, 65021, 256, 61);
    tab.child(15, 65023, 255, 9);
    tab.child(16, 65024, 75, 39);
    tab.child(17, 65025, 195, 39);
    tab.child(18, 65026, 315, 39);
    tab.child(19, 65027, 435, 39);
    tab.child(20, 65028, 195, 65);
    tab.child(21, 65029, 315, 65);
    tab.child(22, 65030, 110, 117);
    tab.child(23, 65031, 40, 140);
    tab.child(24, 65032, 40, 160);
    tab.child(25, 65033, 40, 180);
    tab.child(26, 65034, 40, 200);
    tab.child(27, 65035, 70, 250);
    tab.child(28, 65036, 70, 250);
    tab.child(29, 65038, 110, 257);
    tab.child(30, 65050, -70, 109);
    RSInterface scrollInterface = addTabInterface(65050);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 270;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(65051 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(65051 + i, 260, y, i, scrollInterface);
      y += 30;
    }
  }
}
