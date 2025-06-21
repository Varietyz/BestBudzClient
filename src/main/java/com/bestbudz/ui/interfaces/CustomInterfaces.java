package com.bestbudz.ui.interfaces;

import static com.bestbudz.ui.interfaces.Bank.bank;
import static com.bestbudz.ui.interfaces.Bank.bankSettings;
import static com.bestbudz.ui.interfaces.OptionsTab.optionTab;
import static com.bestbudz.ui.interfaces.Prestiging.advance;
import static com.bestbudz.ui.interfaces.Shop.loyaltyShop;
import static com.bestbudz.ui.interfaces.Shop.shop;
import static com.bestbudz.ui.interfaces.Starter.starter;
import static com.bestbudz.ui.interfaces.minigames.PestControl.pestControlBoat;
import static com.bestbudz.ui.interfaces.minigames.PestControl.pestControlGame;
import static com.bestbudz.ui.interfaces.minigames.WarriorGuild.warriorGuild;

import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.ui.RSInterface;

public class CustomInterfaces extends RSInterface
{
	public static final String[][] shopCategories = new String[][]{{"Strain Titles", "413"}, {"Weed Titles", "414"},
		{"Colors", "407"}};
	public static final String[][][] shopContent = new String[][][]{
		{
			{"Amnesia", "10"},
			{"Haze", "10"},
			{"Kush", "10"},
			{"Cheese", "10"},
			{"Diesel", "10"},
			{"Jack The Ripper", "20"},
			{"Durban", "20"},
			{"Master Kush", "20"},
			{"Super Silver", "20"},
			{"OG Kush", "20"},
			{"Jack Herer", "20"},
			{"Banana Kush", "20"},
			{"Purple Haze", "20"},
			{"Chocolope", "20"},
			{"PowerPlant", "20"},
			{"Bubble Gum", "20"},
			{"Bubba Kush", "20"},
			{"Cheese Haze", "20"},
			{"Zkittlez", "20"},
			{"Gorilla Glue", "20"},
			{"Best Bud", "420"},
			{"Waldo", "420"},
			{"420", "420"},

		},

		{
			{"Skeletal", "250 skeletal"},
			{"Blood", "1500 blood"},
			{"Multi-task", "100 tasks"},
			{"Pet", "10 pets"},
			{"Tztok", "50 firecape"},
			{"The Game", "Win WG 10"},
			{"Big Bear", "100 callisto"},
		},

		{
			{"<col=C22323>Red", "@whi@500"},
			{"<col=0FA80F>Green", "@whi@500"},
			{"<col=2AA4C9>Blue", "@whi@500"},
			{"<col=C9BC28>Yellow", "@whi@500"},
			{"<col=F58D16>Orange", "@whi@500"},
			{"<col=C931E8>Purple", "@whi@500"},
			{"<col=F52CD7>Pink", "@whi@500"},
			{"<col=FFFFFF>White", "@whi@500"},
		}
	};

	public static void unpackInterfaces(TextDrawingArea[] BestBudz)
	{
		optionTab(BestBudz);
		shop(BestBudz);
		bank(BestBudz);
		bankSettings(BestBudz);
		priceChecker(BestBudz);
		genie(BestBudz);
		dropTable(BestBudz);
		itemDetails(BestBudz);
		advance(BestBudz);
		warriorGuild(BestBudz);
		pestControlBoat(BestBudz);
		pestControlGame(BestBudz);
		loyaltyShop(BestBudz);
		starter(BestBudz);
		boltEnchanting(BestBudz);
	}

	public static void boltEnchanting(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(42750);
		addSprite(42751, 451);
		itemDisplay(42752, 66, 100, 5, 5, "Make");
		addHoverButton(42753, 17, 21, 21, "Close", 250, 42754, 3);
		addHoveredButton(42754, 18, 21, 21, 42755);
		addText(42756, "Mage 4", jaybane, 1, 0xff9933, true, true);
		addText(42757, "Mage 7", jaybane, 1, 0xff9933, true, true);
		addText(42758, "Mage 14", jaybane, 1, 0xff9933, true, true);
		addText(42759, "Mage 24", jaybane, 1, 0xff9933, true, true);
		addText(42760, "Mage 27", jaybane, 1, 0xff9933, true, true);
		addText(42761, "Mage 29", jaybane, 1, 0xff9933, true, true);
		addText(42762, "Mage 49", jaybane, 1, 0xff9933, true, true);
		addText(42763, "Mage 57", jaybane, 1, 0xff9933, true, true);
		addText(42764, "Mage 68", jaybane, 1, 0xff9933, true, true);
		addText(42765, "Mage 87", jaybane, 1, 0xff9933, true, true);
		addText(42766, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42767, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42768, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42769, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42770, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42771, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42772, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42773, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42774, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42775, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42776, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42777, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42778, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42779, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42780, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42781, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42782, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42783, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42784, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42785, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42786, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42787, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42788, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42789, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42790, "0/0", jaybane, 0, 0xFA0A0A, true, true);
		addText(42791, "0/0", jaybane, 0, 0xFA0A0A, true, true);

		tab.totalChildren(40);
		tab.child(0, 42751, 11, 15);
		tab.child(1, 42752, 40, 100);
		tab.child(2, 42753, 471, 22);
		tab.child(3, 42754, 471, 22);
		tab.child(4, 42756, 56, 70);
		tab.child(5, 42757, 156, 70);
		tab.child(6, 42758, 256, 70);
		tab.child(7, 42759, 352, 70);
		tab.child(8, 42760, 443, 70);
		tab.child(9, 42761, 65, 200);
		tab.child(10, 42762, 152, 200);
		tab.child(11, 42763, 245, 200);
		tab.child(12, 42764, 342, 200);
		tab.child(13, 42765, 438, 200);
		tab.child(14, 42766, 41, 162);
		tab.child(15, 42767, 74, 162);
		tab.child(16, 42768, 124, 162);
		tab.child(17, 42769, 155, 162);
		tab.child(18, 42770, 185, 162);
		tab.child(19, 42771, 233, 162);
		tab.child(20, 42772, 265, 162);
		tab.child(21, 42773, 331, 162);
		tab.child(22, 42774, 364, 162);
		tab.child(23, 42775, 415, 162);
		tab.child(24, 42776, 447, 162);
		tab.child(25, 42777, 476, 162);
		tab.child(26, 42778, 40, 290);
		tab.child(27, 42779, 72, 290);
		tab.child(28, 42780, 126, 290);
		tab.child(29, 42781, 156, 290);
		tab.child(30, 42782, 185, 290);
		tab.child(31, 42783, 221, 290);
		tab.child(32, 42784, 250, 290);
		tab.child(33, 42785, 280, 290);
		tab.child(34, 42786, 314, 290);
		tab.child(35, 42787, 344, 290);
		tab.child(36, 42788, 372, 290);
		tab.child(37, 42789, 413, 290);
		tab.child(38, 42790, 445, 290);
		tab.child(39, 42791, 475, 290);
	}

	public static void itemDetails(TextDrawingArea[] bestbudz)
	{
		RSInterface tab = addInterface(59750);
		addSprite(59751, 456);
		addText(59752, "Item Details:", 0xff9933, true, true, -1, bestbudz, 2);
		addText(59753, "</col>Item: @gre@Armadyl Godsword", 0xff9933, true, true, -1, bestbudz, 0);
		addText(59754, "</col>General Price: @gre@85M", 0xff9933, true, true, -1, bestbudz, 0);
		addText(59755, "</col>Dealable: @gre@True", 0xff9933, true, true, -1, bestbudz, 0);
		addText(59756, "</col>Noted: @gre@False", 0xff9933, true, true, -1, bestbudz, 0);
		itemDisplay(59757, 30, 10, 5, 5, "Complain");
		addHoverText(59758, "-> Back <-", "Go back to drop table", bestbudz, 0, 0xff9933, true, true, 300);
		tab.totalChildren(8);
		tab.child(0, 59751, 130, 100);
		tab.child(1, 59752, 295, 115);
		tab.child(2, 59753, 295, 140);
		tab.child(3, 59754, 295, 155);
		tab.child(4, 59755, 295, 170);
		tab.child(5, 59756, 295, 185);
		tab.child(6, 59757, 150, 143);
		tab.child(7, 59758, 145, 205);
	}

	public static void dropTable(TextDrawingArea[] bestbudz)
	{
		RSInterface tab = addInterface(59800);
		addSprite(59801, 16);
		addHoverButton(59802, 454, 17, 17, "Close", 250, 59803, 3);
		addHoveredButton(59803, 455, 17, 17, 59804);
		addText(59805, "Monster Drop Guide", 0xff9933, true, true, -1, bestbudz, 2);
		addText(59806, "Name:", 0xff9933, true, false, -1, bestbudz, 0);
		addText(59807, "Grade:", 0xff9933, true, false, -1, bestbudz, 0);
		addText(59818, "", 0xff9933, true, false, -1, bestbudz, 0);
		String[] table = {"Always", "Common", "Uncommon", "Rare", "Very Rare"};
		for (int i = 0; i < table.length; i++)
		{
			addText(59808 + i, table[i], 0x000000, true, false, -1, bestbudz, 0);
		}
		itemContainer(59813, 30, 8, 5, 50, false, "Details");
		addInputField(59800, 59814, 15, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 136, 25, false, false,
			"Search for item");
		addInputField(59800, 59815, 15, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 136, 25, false, false,
			"Search for Npc ");

		tab.totalChildren(16);
		tab.child(0, 59801, 15, 2);
		tab.child(1, 59802, 475, 8);
		tab.child(2, 59803, 475, 8);
		tab.child(3, 59805, 265, 9);
		tab.child(4, 59806, 334, 43);
		tab.child(5, 59807, 334, 60);
		tab.child(6, 59808, 204, 91);
		tab.child(7, 59809, 262, 91);
		tab.child(8, 59810, 324, 91);
		tab.child(9, 59811, 385, 91);
		tab.child(10, 59812, 446, 91);
		tab.child(11, 59814, 24, 37);
		tab.child(12, 59817, 171, 119);
		tab.child(13, 59900, -230, 97);
		tab.child(14, 59818, 334, 63);
		tab.child(15, 59815, 24, 65);

		RSInterface scrollInterface = addTabInterface(59817);
		scrollInterface.width = 305;
		scrollInterface.height = 208;
		scrollInterface.scrollMax = 41 * 50;
		setChildren(2, scrollInterface);
		addSprite(59819, 458);
		setBounds(59819, 0, 0, 0, scrollInterface);
		setBounds(59813, 13, 4, 1, scrollInterface);

		scrollInterface = addTabInterface(59900);
		scrollInterface.width = 377;
		scrollInterface.height = 231;
		scrollInterface.scrollMax = 1000;
		setChildren(50, scrollInterface);
		int y = 0;
		for (int i = 0; i < 50; i++)
		{
			addHoverText(59901 + i, "", "", bestbudz, 0, 0xCF4F0A, false, true, 300);
			setBounds(59901 + i, 260, y + 4, i, scrollInterface);
			y += 20;
		}
	}

	public static void genie(TextDrawingArea[] jaybane)
	{
		int buttonWidth = 130;
		int buttonHeight = 30;

		RSInterface tab = addInterface(59500);
		addSprite(59501, 249);
		addText(59502, "", 0xff9933, true, true, -1, jaybane, 2);
		addHoverButton(59503, 21, 21, 21, "Close", 250, 59504, 3);
		addHoveredButton(59504, 22, 21, 21, 59505);
		addHoverButton(59506, "", 0, buttonWidth, buttonHeight, "Reset Assault", 0, 59507, 1);
		addHoveredButton(59507, 250, buttonWidth, buttonHeight, 59508);
		addHoverButton(59509, "", 0, buttonWidth, buttonHeight, "Reset Vigour", 0, 59510, 1);
		addHoveredButton(59510, 251, buttonWidth, buttonHeight, 59511);
		addHoverButton(59512, "", 0, buttonWidth, buttonHeight, "Reset Aegis", 0, 59513, 1);
		addHoveredButton(59513, 252, buttonWidth, buttonHeight, 59514);
		addHoverButton(59515, "", 0, buttonWidth, buttonHeight, "Reset Range", 0, 59516, 1);
		addHoveredButton(59516, 253, buttonWidth, buttonHeight, 59517);
		addHoverButton(59518, "", 0, buttonWidth, buttonHeight, "Reset Mage", 0, 59519, 1);
		addHoveredButton(59519, 254, buttonWidth, buttonHeight, 59520);
		addHoverButton(59521, "", 0, buttonWidth, buttonHeight, "Reset Resonance", 0, 59522, 1);
		addHoveredButton(59522, 255, buttonWidth, buttonHeight, 59523);
		addHoverButton(59524, "", 0, buttonWidth, buttonHeight, "Reset Life", 0, 59525, 1);
		addHoveredButton(59525, 256, buttonWidth, buttonHeight, 59526);
		addText(59527, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59528, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59529, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59530, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59531, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59532, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59533, "", 0xff9933, true, true, -1, jaybane, 0);
		addText(59534, "", 0xff9953, true, true, -1, jaybane, 0);
		tab.totalChildren(26);
		tab.child(0, 59501, 90, 90);
		tab.child(1, 59502, 250, 97);
		tab.child(2, 59503, 398, 96);
		tab.child(3, 59504, 398, 96);
		tab.child(4, 59506, 101, 145);
		tab.child(5, 59507, 101, 145);
		tab.child(6, 59509, 209, 145);
		tab.child(7, 59510, 209, 145);
		tab.child(8, 59512, 317, 145);
		tab.child(9, 59513, 317, 145);
		tab.child(10, 59515, 101, 179);
		tab.child(11, 59516, 101, 179);
		tab.child(12, 59518, 209, 179);
		tab.child(13, 59519, 209, 179);
		tab.child(14, 59521, 317, 179);
		tab.child(15, 59522, 317, 179);
		tab.child(16, 59524, 209, 213);
		tab.child(17, 59525, 209, 213);
		tab.child(18, 59527, 155, 155);
		tab.child(19, 59528, 265, 155);
		tab.child(20, 59529, 375, 155);
		tab.child(21, 59530, 155, 189);
		tab.child(22, 59531, 265, 189);
		tab.child(23, 59532, 375, 189);
		tab.child(24, 59533, 265, 223);
		tab.child(25, 59534, 255, 122);
	}

	public static void priceChecker(TextDrawingArea[] jaybane)
	{
		RSInterface rsi = addInterface(48500);
		addSprite(48501, 243);
		addPriceChecker(48542);
		addHoverButton(48502, 17, 16, 21, "Close", -1, 48503, 3);
		addHoveredButton(48503, 18, 21, 21, 48504);
		addHoverButton(48505, 244, 36, 36, "Add all", -1, 48506, 1);
		addHoveredButton(48506, 245, 36, 36, 48507);
		addHoverButton(48578, 246, 36, 36, "Withdraw all", -1, 48579, 1);
		addHoveredButton(48579, 247, 36, 36, 48580);
		addHoverButton(48508, 44, 36, 36, "Search for item", -1, 48509, 1);
		addHoveredButton(48509, 45, 36, 36, 48510);
		addText(48511, "Price Checker", jaybane, 2, 0xFF981F, true, true);
		addText(48512, "Total price:", jaybane, 1, 0xFF981F, true, true);
		addText(48513, "115,424,152", jaybane, 0, 0xffffff, true, true);
		addText(48550, "", jaybane, 0, 0xffffff, true, true);
		addText(48551, "", jaybane, 0, 0xffffff, true, true);
		addText(48552, "", jaybane, 0, 0xffffff, true, true);
		addText(48553, "", jaybane, 0, 0xffffff, true, true);
		addText(48554, "", jaybane, 0, 0xffffff, true, true);
		addText(48555, "", jaybane, 0, 0xffffff, true, true);
		addText(48556, "", jaybane, 0, 0xffffff, true, true);
		addText(48557, "", jaybane, 0, 0xffffff, true, true);
		addText(48558, "", jaybane, 0, 0xffffff, true, true);
		addText(48559, "", jaybane, 0, 0xffffff, true, true);
		addText(48560, "", jaybane, 0, 0xffffff, true, true);
		addText(48561, "", jaybane, 0, 0xffffff, true, true);
		addText(48562, "", jaybane, 0, 0xffffff, true, true);
		addText(48563, "", jaybane, 0, 0xffffff, true, true);
		addText(48564, "", jaybane, 0, 0xffffff, true, true);
		addText(48565, "", jaybane, 0, 0xffffff, true, true);
		addText(48566, "", jaybane, 0, 0xffffff, true, true);
		addText(48567, "", jaybane, 0, 0xffffff, true, true);
		addText(48568, "", jaybane, 0, 0xffffff, true, true);
		addText(48569, "", jaybane, 0, 0xffffff, true, true);
		addText(48570, "", jaybane, 0, 0xffffff, true, true);
		addText(48571, "", jaybane, 0, 0xffffff, true, true);
		addText(48572, "", jaybane, 0, 0xffffff, true, true);
		addText(48573, "", jaybane, 0, 0xffffff, true, true);
		addText(48574, "", jaybane, 0, 0xffffff, true, true);
		addText(48575, "", jaybane, 0, 0xffffff, true, true);
		addText(48576, "", jaybane, 0, 0xffffff, true, true);
		addText(48577, "", jaybane, 0, 0xffffff, true, true);
		rsi.totalChildren(41);
		rsi.child(0, 48501, 0, 0);
		rsi.child(1, 48502, 485, 7);
		rsi.child(2, 48503, 485, 7);
		rsi.child(3, 48505, 434, 290);
		rsi.child(4, 48506, 434, 290);
		rsi.child(5, 48508, 8, 290);
		rsi.child(6, 48509, 8, 290);
		rsi.child(7, 48511, 255, 10);
		rsi.child(8, 48512, 255, 290);
		rsi.child(9, 48513, 255, 310);
		rsi.child(10, 48542, 40, 45);
		rsi.child(11, 48550, 57, 80);
		rsi.child(12, 48551, 123, 80);
		rsi.child(13, 48552, 189, 80);
		rsi.child(14, 48553, 256, 80);
		rsi.child(15, 48554, 323, 80);
		rsi.child(16, 48555, 390, 80);
		rsi.child(17, 48556, 458, 80);
		rsi.child(18, 48557, 57, 142);
		rsi.child(19, 48558, 123, 142);
		rsi.child(20, 48559, 189, 142);
		rsi.child(21, 48560, 256, 142);
		rsi.child(22, 48561, 323, 142);
		rsi.child(23, 48562, 390, 142);
		rsi.child(24, 48563, 458, 142);
		rsi.child(25, 48564, 57, 204);
		rsi.child(26, 48565, 123, 204);
		rsi.child(27, 48566, 189, 204);
		rsi.child(28, 48567, 256, 204);
		rsi.child(29, 48568, 323, 204);
		rsi.child(30, 48569, 390, 204);
		rsi.child(31, 48570, 458, 204);
		rsi.child(32, 48571, 57, 266);
		rsi.child(33, 48572, 123, 266);
		rsi.child(34, 48573, 189, 266);
		rsi.child(35, 48574, 256, 266);
		rsi.child(36, 48575, 323, 266);
		rsi.child(37, 48576, 390, 266);
		rsi.child(38, 48577, 458, 266);
		rsi.child(39, 48578, 470, 290);
		rsi.child(40, 48579, 470, 290);
	}
}