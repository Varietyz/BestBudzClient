package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class SkillingTeleports extends RSInterface {
  public static void skillingTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(62000);
    addSprite(62001, 64);
    addHoverButton(62002, 17, 21, 21, "Close", 250, 62003, 3);
    addHoveredButton(62003, 18, 21, 21, 62004);
    addHoverButton(62005, 59, 123, 30, "Training", 0, 62006, 1);
    addHoveredButton(62006, 60, 123, 30, 62007);
    addHoverButton(62008, 59, 123, 30, "Skilling", 0, 62009, 1);
    addHoveredButton(62009, 60, 123, 30, 62010);
    addHoverButton(62011, 59, 123, 30, "Stoner Vs Stoner", 0, 62012, 1);
    addHoveredButton(62012, 60, 123, 30, 62013);
    addHoverButton(62014, 59, 123, 30, "Boss", 0, 62015, 1);
    addHoveredButton(62015, 60, 123, 30, 62016);
    addHoverButton(62017, 59, 123, 30, "Minigame", 0, 62018, 1);
    addHoveredButton(62018, 60, 123, 30, 62019);
    addHoverButton(62020, 59, 123, 30, "Other", 0, 62021, 1);
    addHoveredButton(62021, 60, 123, 30, 62022);
    addText(
        62023,
        "@bla@Teleportation Menu (@red@Skilling@bla@)",
        0x20CF2F,
        true,
        true,
        -1,
        bestbudz,
        2);
    addText(62024, "Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62025, "@bla@Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62026, "Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62027, "Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62028, "Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62029, "Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(62030, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(62031, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(62032, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(62033, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(62034, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(62035, 61, 123, 30, "Confirm Teleport", 0, 62036, 1);
    addHoveredButton(62036, 62, 123, 30, 62037);
    addText(62038, " ", 0x1FEA39, true, true, -1, bestbudz, 3);
    tab.totalChildren(31);
    tab.child(0, 62001, 11, 2);
    tab.child(1, 62002, 475, 8);
    tab.child(2, 62003, 475, 8);
    tab.child(3, 62005, 16, 35);
    tab.child(4, 62006, 16, 35);
    tab.child(5, 62008, 136, 35);
    tab.child(6, 62009, 136, 35);
    tab.child(7, 62011, 256, 35);
    tab.child(8, 62012, 256, 35);
    tab.child(9, 62014, 376, 35);
    tab.child(10, 62015, 376, 35);
    tab.child(11, 62017, 136, 61);
    tab.child(12, 62018, 136, 61);
    tab.child(13, 62020, 256, 61);
    tab.child(14, 62021, 256, 61);
    tab.child(15, 62023, 255, 9);
    tab.child(16, 62024, 75, 39);
    tab.child(17, 62025, 195, 39);
    tab.child(18, 62026, 315, 39);
    tab.child(19, 62027, 435, 39);
    tab.child(20, 62028, 195, 65);
    tab.child(21, 62029, 315, 65);
    tab.child(22, 62030, 110, 117);
    tab.child(23, 62031, 40, 140);
    tab.child(24, 62032, 40, 160);
    tab.child(25, 62033, 40, 180);
    tab.child(26, 62034, 40, 200);
    tab.child(27, 62035, 70, 250);
    tab.child(28, 62036, 70, 250);
    tab.child(29, 62038, 110, 257);
    tab.child(30, 62050, -70, 109);
    RSInterface scrollInterface = addTabInterface(62050);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 300;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(62051 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(62051 + i, 260, y, i, scrollInterface);
      y += 30;
    }
  }
}
