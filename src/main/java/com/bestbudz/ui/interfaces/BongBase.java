package com.bestbudz.ui.interfaces;

import com.bestbudz.data.Skills;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class BongBase extends RSInterface {

  public static void profileTab(TextDrawingArea[] jaybane) {
    RSInterface tab = addInterface(51500);
    addSprite(51501, 360);
    addSprite(51502, 38);
    addText(51503, "", jaybane, 2, 0xF7AA25, true, true);
    addHoverButton(51504, 44, 45, 45, "Search", 51504, 51505, 1);
    addHoveredButton(51505, 45, 45, 45, 51506);
    addConfigButton(51507, 51500, 289, 290, 14, 15, "Select", 1, 5, 1032);
    addConfigButton(51508, 51500, 289, 290, 14, 15, "Select", 2, 5, 1032);
    addHoverText(
        51509, "Hide in the bushes", "Hide in bush", jaybane, 0, 0xF7AA25, false, true, 60);
    addHoverText(
        51510, "Be prominent", "Stoners can see you", jaybane, 0, 0xF7AA25, false, true, 60);
    addHoverButton(51511, 36, 150, 35, "View my Bongbase", 0, 51512, 1);
    addHoveredButton(51512, 37, 150, 35, 51513);
    addText(51514, "View self", jaybane, 2, 0xF7AA25, true, true);
    addHoverButton(51515, 36, 150, 35, "View stoner ratings", 0, 51516, 1);
    addHoveredButton(51516, 37, 150, 35, 51517);
    addText(51518, "Rates", jaybane, 2, 0xF7AA25, true, true);
    addHoverButton(51519, 362, 50, 20, "Settings", 0, 51520, 1);
    addHoveredButton(51520, 361, 50, 20, 51521);
    tab.totalChildren(18);
    tab.child(0, 51501, -4, 34);
    tab.child(1, 51502, -0, 34);
    tab.child(2, 51502, -0, 229);
    tab.child(3, 51503, 92, 9);
    tab.child(4, 51504, 0, 37);
    tab.child(5, 51505, 0, 37);
    tab.child(6, 51507, 50, 37);
    tab.child(7, 51508, 50, 56);
    tab.child(8, 51509, 70, 39);
    tab.child(9, 51510, 70, 58);
    tab.child(10, 51511, 20, 95);
    tab.child(11, 51512, 20, 95);
    tab.child(12, 51514, 92, 103);
    tab.child(13, 51515, 20, 155);
    tab.child(14, 51516, 20, 155);
    tab.child(15, 51518, 92, 163);
    tab.child(16, 51519, 1000, 1000);
    tab.child(17, 51520, 1000, 1000);
  }

  public static void myProfile(TextDrawingArea[] jaybane) {
    RSInterface tab = addInterface(51600);
    addSprite(51601, 359);
    addText(51602, "My Profile", jaybane, 2, 0xff981f, true, true);
    addHoverButton(51603, 17, 21, 21, "Close", 250, 51604, 3);
    addHoveredButton(51604, 18, 21, 21, 51605);
    addChar(51606, 700);
    addText(51607, "</col>Name: @gre@Jaybane", jaybane, 1, 0xff981f, true, true);
    addText(51608, "</col>Rank: @cr2@@gre@  Owner", jaybane, 1, 0xff981f, true, true);
    addText(51609, "</col>Grade: @gre@126", jaybane, 1, 0xff981f, true, true);
    for (int i = 0; i < 21; i++) {
      addSprite(51610 + i, 324 + i);
    }
    tab.totalChildren(51);
    tab.child(8, 51680, 303, 49);
    RSInterface scrollInterface = addTabInterface(51680);
    scrollInterface.width = 170;
    scrollInterface.height = 267;
    scrollInterface.scrollMax = 450;
    setChildren(35, scrollInterface);
    int y = 0;
    for (int i = 0; i < 35; i++) {
      addHoverText(51681 + i, "", "", jaybane, 0, 0xff981f, true, true, 160, 0xff981f);
      setBounds(51681 + i, 0, y, i, scrollInterface);
      y += 20;
    }
    tab.child(0, 51601, 10, 5);
    tab.child(1, 51602, 255, 13);
    tab.child(2, 51603, 478, 11);
    tab.child(3, 51604, 478, 11);
    tab.child(4, 51606, 35, 210);
    tab.child(5, 51607, 105, 55);
    tab.child(6, 51608, 105, 70);
    tab.child(7, 51609, 105, 85);
    for (int i = 0; i < 20; i++) {
      tab.child(9 + i, 51610 + i, 205 + (i / 10) * 36, 50 + (i % 10) * 25);
      addTooltipBox(
          51632 + i,
          Skills.SKILL_NAMES[i].substring(0, 1).toUpperCase()
              + Skills.SKILL_NAMES[i].substring(1)
              + " grade: 1/1\\nAdvance grade: 1");
      interfaceCache[51632 + i].width = 25;
      interfaceCache[51632 + i].height = 25;
      tab.child(30 + i, 51632 + i, 205 + (i / 10) * 36, 50 + (i % 10) * 25);
    }
    tab.child(29, 51610 + 20, 222, 290);
    addTooltipBox(51632 + 20, "Consumer grade: 1/1\\nAdvance grade: 1");
    interfaceCache[51632 + 20].width = 25;
    interfaceCache[51632 + 20].height = 25;
    tab.child(50, 51632 + 20, 222, 290);
  }

  public static void stonerProfiler(TextDrawingArea[] jaybane) {
    RSInterface tab = addInterface(51800);
    addSprite(51801, 359);
    addText(51802, "", jaybane, 2, 0xff981f, true, true);
    addHoverButton(51803, 17, 21, 21, "Close", 250, 51804, 3);
    addHoveredButton(51804, 18, 21, 21, 51805);
    addOtherChar(51806, 700);
    addText(51807, "</col>Name: @gre@Jaybane", jaybane, 1, 0xff981f, true, true);
    addText(51808, "</col>Rank: @cr2@@gre@  Owner", jaybane, 1, 0xff981f, true, true);
    addText(51809, "</col>Grade: @gre@126", jaybane, 1, 0xff981f, true, true);
    for (int i = 0; i < 21; i++) {
      addSprite(51810 + i, 324 + i);
    }

    addHoverButton(51990, 363, 30, 30, "Like", 0, 51991, 1);
    addHoveredButton(51991, 364, 30, 30, 51992);

    addHoverButton(51993, 365, 30, 30, "Dislike", 0, 51994, 1);
    addHoveredButton(51994, 366, 30, 30, 51995);

    tab.totalChildren(55);
    tab.child(8, 51880, 303, 49);
    RSInterface scrollInterface = addTabInterface(51880);
    scrollInterface.width = 170;
    scrollInterface.height = 267;
    scrollInterface.scrollMax = 450;
    setChildren(35, scrollInterface);
    int y = 0;
    for (int i = 0; i < 35; i++) {
      addHoverText(51881 + i, "", "", jaybane, 0, 0xff981f, true, true, 160, 0xff981f);
      setBounds(51881 + i, 0, y, i, scrollInterface);
      y += 20;
    }
    tab.child(0, 51801, 10, 5);
    tab.child(1, 51802, 255, 13);
    tab.child(2, 51803, 478, 11);
    tab.child(3, 51804, 478, 11);
    tab.child(4, 51806, 35, 210);
    tab.child(5, 51807, 105, 55);
    tab.child(6, 51808, 105, 70);
    tab.child(7, 51809, 105, 85);
    for (int i = 0; i < 20; i++) {
      tab.child(9 + i, 51810 + i, 205 + (i / 10) * 36, 50 + (i % 10) * 25);
      addTooltipBox(
          51832 + i,
          Skills.SKILL_NAMES[i].substring(0, 1).toUpperCase()
              + Skills.SKILL_NAMES[i].substring(1)
              + " grade: 1/1\\nAdvance grade: 1");
      interfaceCache[51832 + i].width = 25;
      interfaceCache[51832 + i].height = 25;
      tab.child(30 + i, 51832 + i, 205 + (i / 10) * 36, 50 + (i % 10) * 25);
    }
    tab.child(29, 51810 + 20, 222, 290);
    addTooltipBox(51832 + 20, "Consumer grade: 1/1\\nAdvance grade: 1");
    interfaceCache[51832 + 20].width = 25;
    interfaceCache[51832 + 20].height = 25;
    tab.child(50, 51832 + 20, 222, 290);
    tab.child(51, 51990, 185, 10);
    tab.child(52, 51991, 185, 10);
    tab.child(53, 51993, 307, 10);
    tab.child(54, 51994, 307, 10);
  }

  public static void profileLeaderboards(TextDrawingArea[] jaybane) {
    RSInterface tab = addInterface(47400);
    addSprite(47401, 367);
    addHoverButton(47402, 17, 21, 21, "Close", 250, 47403, 3);
    addHoveredButton(47403, 18, 21, 21, 47404);
    addText(47405, " ", jaybane, 2, 0xF7AA25, true, true);
    addHoverButton(47406, 59, 123, 30, "Views", 0, 47407, 1);
    addHoveredButton(47407, 60, 123, 30, 47408);
    addHoverButton(47409, 59, 123, 30, "Tab 2", 0, 47410, 1);
    addHoveredButton(47410, 60, 123, 30, 47411);
    addHoverButton(47412, 59, 123, 30, "Tab 3", 0, 47413, 1);
    addHoveredButton(47413, 60, 123, 30, 47414);
    addHoverButton(47415, 59, 123, 30, "Tab 4", 0, 47416, 1);
    addHoveredButton(47416, 60, 123, 30, 47417);
    addText(47418, "Views", jaybane, 2, 0xF7AA25, true, true);
    addText(47419, "Likes", jaybane, 2, 0xF7AA25, true, true);
    addText(47420, "Dislikes", jaybane, 2, 0xF7AA25, true, true);
    addText(47421, "Ratio", jaybane, 2, 0xF7AA25, true, true);
    tab.totalChildren(17);
    tab.child(0, 47401, 11, 17);
    tab.child(1, 47402, 475, 23);
    tab.child(2, 47403, 475, 23);
    tab.child(3, 47405, 258, 26);
    tab.child(4, 47406, 16, 51);
    tab.child(5, 47407, 16, 51);
    tab.child(6, 47409, 136, 51);
    tab.child(7, 47410, 136, 51);
    tab.child(8, 47412, 256, 51);
    tab.child(9, 47413, 256, 51);
    tab.child(10, 47415, 376, 51);
    tab.child(11, 47416, 376, 51);
    tab.child(12, 47418, 75, 56);
    tab.child(13, 47419, 195, 56);
    tab.child(14, 47420, 315, 56);
    tab.child(15, 47421, 435, 56);
    tab.child(16, 51550, 206, 97);
    RSInterface scrollInterface = addTabInterface(51550);
    scrollInterface.width = 175;
    scrollInterface.height = 201;
    scrollInterface.scrollMax = 500;
    setChildren(25, scrollInterface);
    int y = 0;
    for (int i = 0; i < 25; i++) {
      addHoverText(51551 + i, "Username", "View", jaybane, 0, 0xcc8000, true, true, 100, 0xFFFFFF);
      setBounds(51551 + i, 0, y, i, scrollInterface);
      y += 20;
    }
  }
}
