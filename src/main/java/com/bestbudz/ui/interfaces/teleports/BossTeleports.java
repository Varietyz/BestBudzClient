package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class BossTeleports extends RSInterface {
  public static void bossTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(64000);
    addSprite(64001, 66);
    addHoverButton(64002, 17, 21, 21, "Close", 250, 64003, 3);
    addHoveredButton(64003, 18, 21, 21, 64004);
    addHoverButton(64005, 59, 123, 30, "Training", 0, 64006, 1);
    addHoveredButton(64006, 60, 123, 30, 64007);
    addHoverButton(64008, 59, 123, 30, "Skilling", 0, 64009, 1);
    addHoveredButton(64009, 60, 123, 30, 64010);
    addHoverButton(64011, 59, 123, 30, "Stoner Vs Stoner", 0, 64012, 1);
    addHoveredButton(64012, 60, 123, 30, 64013);
    addHoverButton(64014, 59, 123, 30, "Boss", 0, 64015, 1);
    addHoveredButton(64015, 60, 123, 30, 64016);
    addHoverButton(64017, 59, 123, 30, "Minigame", 0, 64018, 1);
    addHoveredButton(64018, 60, 123, 30, 64019);
    addHoverButton(64020, 59, 123, 30, "Other", 0, 64021, 1);
    addHoveredButton(64021, 60, 123, 30, 64022);
    addText(
        64023,
        "@bla@Teleportation Menu (@red@Stoner Vs Monster@bla@)",
        0x20CF2F,
        true,
        true,
        -1,
        bestbudz,
        2);
    addText(64024, "Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64025, "Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64026, "Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64027, "@bla@Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64028, "Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64029, "Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(64030, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(64031, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(64039, "Grade: @red@0", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(64032, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(64033, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(64034, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(64035, 61, 123, 30, "Confirm Teleport", 0, 64036, 1);
    addHoveredButton(64036, 62, 123, 30, 64037);
    addText(64038, " ", 0x20CF2F, true, true, -1, bestbudz, 3);
    tab.totalChildren(32);
    tab.child(0, 64001, 11, 2);
    tab.child(1, 64002, 475, 8);
    tab.child(2, 64003, 475, 8);
    tab.child(3, 64005, 16, 35);
    tab.child(4, 64006, 16, 35);
    tab.child(5, 64008, 136, 35);
    tab.child(6, 64009, 136, 35);
    tab.child(7, 64011, 256, 35);
    tab.child(8, 64012, 256, 35);
    tab.child(9, 64014, 376, 35);
    tab.child(10, 64015, 376, 35);
    tab.child(11, 64017, 136, 61);
    tab.child(12, 64018, 136, 61);
    tab.child(13, 64020, 256, 61);
    tab.child(14, 64021, 256, 61);
    tab.child(15, 64023, 255, 9);
    tab.child(16, 64024, 75, 39);
    tab.child(17, 64025, 195, 39);
    tab.child(18, 64026, 315, 39);
    tab.child(19, 64027, 435, 39);
    tab.child(20, 64028, 195, 65);
    tab.child(21, 64029, 315, 65);
    tab.child(22, 64030, 110, 117);
    tab.child(23, 64031, 40, 140);

    tab.child(24, 64032, 40, 180);
    tab.child(25, 64033, 40, 200);
    tab.child(26, 64034, 40, 220);
    tab.child(27, 64035, 70, 250);
    tab.child(28, 64036, 70, 250);
    tab.child(29, 64038, 110, 257);
    tab.child(30, 64050, -70, 109);
    tab.child(31, 64039, 40, 160);
    RSInterface scrollInterface = addTabInterface(64050);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 500;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(64051 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(64051 + i, 260, y, i, scrollInterface);
      y += 30;
    }
  }
}
