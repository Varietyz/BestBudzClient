package com.bestbudz.ui.interfaces.teleports;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class OtherTeleports extends RSInterface {
  public static void otherTeleport(TextDrawingArea[] bestbudz) {
    RSInterface tab = addInterface(61500);
    addSprite(61501, 68);
    addHoverButton(61502, 17, 21, 21, "Close", 250, 61503, 3);
    addHoveredButton(61503, 18, 21, 21, 61504);
    addHoverButton(61505, 59, 123, 30, "Training", 0, 61506, 1);
    addHoveredButton(61506, 60, 123, 30, 61507);
    addHoverButton(61508, 59, 123, 30, "Skilling", 0, 61509, 1);
    addHoveredButton(61509, 60, 123, 30, 61510);
    addHoverButton(61511, 59, 123, 30, "Stoner Vs Stoner", 0, 61512, 1);
    addHoveredButton(61512, 60, 123, 30, 61513);
    addHoverButton(61514, 59, 123, 30, "Boss", 0, 61515, 1);
    addHoveredButton(61515, 60, 123, 30, 61516);
    addHoverButton(61517, 59, 123, 30, "Minigame", 0, 61518, 1);
    addHoveredButton(61518, 60, 123, 30, 61519);
    addHoverButton(61520, 59, 123, 30, "Other", 0, 61521, 1);
    addHoveredButton(61521, 60, 123, 30, 61522);
    addText(
        61523, "@bla@Teleportation Menu (@red@Other@bla@)", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(61524, "Training", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61525, "Skilling", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61526, "Stoner Vs Stoner", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61527, "Stoner Vs Monster", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61528, "Minigame", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61529, "@bla@Other", 0x1FEA39, true, true, -1, bestbudz, 1);
    addText(61530, "Teleport Information:", 0x20CF2F, true, true, -1, bestbudz, 2);
    addText(61531, "Selected: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61532, "Cost: @red@Free", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61533, "Requirement: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addText(61534, "Other: @red@None", 0x20CF2F, false, true, -1, bestbudz, 0);
    addHoverButton(61535, 61, 123, 30, "Confirm Teleport", 0, 61536, 1);
    addHoveredButton(61536, 62, 123, 30, 61537);
    addText(61538, " ", 0x20CF2F, true, true, -1, bestbudz, 3);
    tab.totalChildren(31);
    tab.child(0, 61501, 11, 2);
    tab.child(1, 61502, 475, 8);
    tab.child(2, 61503, 475, 8);
    tab.child(3, 61505, 16, 35);
    tab.child(4, 61506, 16, 35);
    tab.child(5, 61508, 136, 35);
    tab.child(6, 61509, 136, 35);
    tab.child(7, 61511, 256, 35);
    tab.child(8, 61512, 256, 35);
    tab.child(9, 61514, 376, 35);
    tab.child(10, 61515, 376, 35);
    tab.child(11, 61517, 136, 61);
    tab.child(12, 61518, 136, 61);
    tab.child(13, 61520, 256, 61);
    tab.child(14, 61521, 256, 61);
    tab.child(15, 61523, 255, 9);
    tab.child(16, 61524, 75, 39);
    tab.child(17, 61525, 195, 39);
    tab.child(18, 61526, 315, 39);
    tab.child(19, 61527, 435, 39);
    tab.child(20, 61528, 195, 65);
    tab.child(21, 61529, 315, 65);
    tab.child(22, 61530, 110, 117);
    tab.child(23, 61531, 40, 140);
    tab.child(24, 61532, 40, 160);
    tab.child(25, 61533, 40, 180);
    tab.child(26, 61534, 40, 200);
    tab.child(27, 61535, 70, 250);
    tab.child(28, 61536, 70, 250);
    tab.child(29, 61538, 110, 257);
    tab.child(30, 61550, -70, 109);
    RSInterface scrollInterface = addTabInterface(61550);
    scrollInterface.width = 538;
    scrollInterface.height = 204;
    scrollInterface.scrollMax = 205;
    setChildren(40, scrollInterface);
    int y = 0;
    for (int i = 0; i < 40; i++) {
      addHoverText(61551 + i, "", "Select", bestbudz, 1, 0xff9633, true, true, 300);
      setBounds(61551 + i, 260, y, i, scrollInterface);
      y += 30;
    }
  }
}
