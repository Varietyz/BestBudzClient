package com.bestbudz.ui.interfaces;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class OptionsTab extends RSInterface {

  public static void optionTab(TextDrawingArea[] bestbudz) {
	  RSInterface tab = addTabInterface(904);
	  RSInterface energy = interfaceCache[149];
	  energy.textColor = 0xff9933;

	  addSprite(905, 86);
	  addSprite(907, 87);
	  addSprite(909, 88);
	  addSprite(951, 89);
	  addSprite(953, 90);
	  addSprite(955, 91);
	  addSprite(947, 102);
	  addSprite(949, 103);
	  addSprite(36001, 38);
	  addSprite(36002, 38);
	  addSprite(36017, 38);

	  addConfigButton(152, 904, 92, 93, 40, 40, "Haste Yo' Ass", 1, 5, 173);
	  addConfigButton(913, 904, 92, 93, 40, 40, "Pointless Button", 0, 5, 170);
	  addConfigButton(957, 904, 92, 93, 40, 40, "Split PM", 1, 5, 287);
	  addConfigButton(12464, 904, 92, 93, 40, 40, "Too Stoned...", 0, 5, 427);

	  addConfigButton(906, 904, 94, 95, 32, 16, "Shit Its Dark", 1, 5, 166);
	  addConfigButton(908, 904, 96, 97, 32, 16, "Ait, Can See", 2, 5, 166);
	  addConfigButton(910, 904, 98, 99, 32, 16, "Tis Sunny", 3, 5, 166);
	  addConfigButton(912, 904, 100, 101, 32, 16, "AAAHH MY EYES", 4, 5, 166);

	  addHoverButton(36004, 470, 40, 40, "Transparent side panel", -1, 36005, 1);
	  addHoveredButton(36005, 471, 40, 40, 36006);

	  addHoverButton(36007, 468, 40, 40, "Transparent chatbox", -1, 36008, 1);
	  addHoveredButton(36008, 469, 40, 40, 36009);

	  addHoverButton(36010, 466, 40, 40, "Side-stones arrangement", -1, 36011, 1);
	  addHoveredButton(36011, 467, 40, 40, 36012);

	  addHoverButton(36026, 104, 40, 40, "Custom", -1, 36027, 1);
	  addHoveredButton(36027, 105, 40, 40, 36028);
	  addHoverButton(36029, 69, 40, 40, "Chat Coloring", -1, 36030, 1);
	  addHoveredButton(36030, 70, 40, 40, 36031);

	  addText(36003, "Hmmm, Where's the button?", bestbudz, 1, 0xff9933, true, true);
	  addText(36013, "", bestbudz, 0, 0xff9933, true, true);
	  addText(36014, "Fuck Fixed Or Fullscreen!", bestbudz, 0, 0xff9933, true, true);
	  addText(36015, "", bestbudz, 0, 0xff9933, true, true);
	  addText(36016, "", bestbudz, 1, 0xff9933, true, true);
	  addHoverText(36025, "Buggy Options", "Go Ahead", bestbudz, 0, 0xff981f, true, true, 60);

    tab.totalChildren(34);

    tab.child(0, 913, 15, 153);
    tab.child(1, 955, 19, 159);
    tab.child(2, 36029, 75, 153);
    tab.child(3, 953, 79, 160);
    tab.child(4, 957, 135, 153);
    tab.child(5, 951, 139, 159);
    tab.child(6, 12464, 15, 208);
    tab.child(7, 949, 20, 213);
    tab.child(8, 152, 75, 208);
    tab.child(9, 947, 86, 212);
    tab.child(10, 149, 80, 230);
    tab.child(11, 36001, 0, 18);
    tab.child(12, 36002, 0, -100);
    tab.child(13, 36003, 93, 2);
    tab.child(14, 36004, 15, 26);
    tab.child(15, 36005, 15, 26);
    tab.child(16, 36007, 75, 26);
    tab.child(17, 36008, 75, 26);
    tab.child(18, 36010, 135, 26);
    tab.child(19, 36011, 135, 26);
    tab.child(20, 36013, 37, 72);
    tab.child(21, 36014, 97, 72);
    tab.child(22, 36015, 157, 72);
    tab.child(23, 36016, 93, 88);
    tab.child(24, 36017, 0, 106);
    tab.child(25, 905, 13, 115);
    tab.child(26, 906, 48, 123);
    tab.child(27, 908, 80, 123);
    tab.child(28, 910, 112, 123);
    tab.child(29, 912, 144, 123);
    tab.child(30, 36025, 69, 89);
    tab.child(31, 36026, 135, 208);
    tab.child(32, 36027, 135, 208);
    tab.child(33, 36030, 75, 153);
  }
}
