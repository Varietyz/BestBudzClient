package com.bestbudz.ui.interfaces;

import static com.bestbudz.ui.interfaces.Bank.bank;
import static com.bestbudz.ui.interfaces.Bank.bankSettings;
import static com.bestbudz.ui.interfaces.BongBase.myProfile;
import static com.bestbudz.ui.interfaces.BongBase.profileLeaderboards;
import static com.bestbudz.ui.interfaces.BongBase.profileTab;
import static com.bestbudz.ui.interfaces.BongBase.stonerProfiler;
import static com.bestbudz.ui.interfaces.EquipmentTab.equipmentScreen;
import static com.bestbudz.ui.interfaces.EquipmentTab.equipmentTab;
import static com.bestbudz.ui.interfaces.OptionsTab.optionTab;
import static com.bestbudz.ui.interfaces.Prestiging.advance;
import static com.bestbudz.ui.interfaces.QuickPrayers.quickCurses;
import static com.bestbudz.ui.interfaces.QuickPrayers.quickPrayers;
import static com.bestbudz.ui.interfaces.Shop.loyaltyShop;
import static com.bestbudz.ui.interfaces.Shop.shop;
import static com.bestbudz.ui.interfaces.Starter.starter;
import static com.bestbudz.ui.interfaces.minigames.PestControl.pestControlBoat;
import static com.bestbudz.ui.interfaces.minigames.PestControl.pestControlGame;
import static com.bestbudz.ui.interfaces.minigames.WarriorGuild.warriorGuild;
import static com.bestbudz.ui.interfaces.teleports.BossTeleports.bossTeleport;
import static com.bestbudz.ui.interfaces.teleports.MinigameTeleports.minigameTeleport;
import static com.bestbudz.ui.interfaces.teleports.OtherTeleports.otherTeleport;
import static com.bestbudz.ui.interfaces.teleports.PvpTeleports.pvpTeleport;
import static com.bestbudz.ui.interfaces.teleports.SkillingTeleports.skillingTeleport;
import static com.bestbudz.ui.interfaces.teleports.TrainingTeleports.trainingTeleport;

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
		godWars(BestBudz);

		questTab(BestBudz);
		equipmentTab();
		equipmentScreen(BestBudz);
		quickCurses(BestBudz);
		quickPrayers();
		optionTab(BestBudz);
		bounty(BestBudz);
		trainingTeleport(BestBudz);
		skillingTeleport(BestBudz);
		pvpTeleport(BestBudz);
		bossTeleport(BestBudz);
		minigameTeleport(BestBudz);
		otherTeleport(BestBudz);
		shop(BestBudz);
		settings(BestBudz);
		screenOptions(BestBudz);
		achievementsTab(BestBudz);
		shopExchange(BestBudz);
		creditsTab(BestBudz);
		itemsOnDeath(BestBudz);
		bank(BestBudz);
		bankSettings(BestBudz);
		priceChecker(BestBudz);
		genie(BestBudz);
		mysteryBox(BestBudz);
		dropTable(BestBudz);
		itemDetails(BestBudz);
		chatColor(BestBudz);
		stonersTab(BestBudz);
		ignoreTab(BestBudz);
		advance(BestBudz);
		warriorGuild(BestBudz);
		pestControlBoat(BestBudz);
		pestControlGame(BestBudz);
		stonerProfiler(BestBudz);
		myProfile(BestBudz);
		profileTab(BestBudz);
		profileLeaderboards(BestBudz);
		loyaltyShop(BestBudz);
		questInterface(BestBudz);
		fireColor(BestBudz);
		clanChatTab(BestBudz);
		clanChatSetup(BestBudz);
		inPvP(BestBudz);
		inSafe();
		inTimer(BestBudz);
		PKSkillTab(BestBudz);
		starter(BestBudz);
		tabCreation(BestBudz);
		reportAbuse(BestBudz);
		boltEnchanting(BestBudz);
		weaponLobby(BestBudz);
		weaponGame(BestBudz);
		weaponStore(BestBudz);
		ticketInterface(BestBudz);
		staffTab(BestBudz);
	}

	public static void screenOptions(TextDrawingArea[] tda) {
		// Create the parent container interface
		RSInterface tab = RSInterface.addInterface(28200);

		// === Background and Title ===

		RSInterface.addSprite(28201, 472); // Background box (sprite index 472)
		RSInterface.addText(28202, "Screen Options", tda, 2, 0xFF9933, true, true); // Tab Title

		// === Close Button (with hover effect) ===

		RSInterface.addHoverButton(28203, 17, 21, 21, "Close", 250, 28204, 3);  // Close X (normal)
		RSInterface.addHoveredButton(28204, 18, 21, 21, 28205);                 // Close X (hover)

		// === Config Toggles (checkboxes with linked config values) ===

		RSInterface.addConfigButton(28206, 28200, 470, 471, 40, 40,
			"Transparent side panel", 0, 1, 700); // Toggle transparent side panels

		RSInterface.addConfigButton(28207, 28200, 468, 469, 40, 40,
			"Transparent chatbox", 0, 1, 701); // Toggle transparent chatbox

		RSInterface.addConfigButton(28208, 28200, 466, 467, 40, 40,
			"Side-stones arrangement", 0, 1, 702); // Toggle side-stone layout

		// === Labels and Descriptions ===

		// Side panel toggle label
		RSInterface.addText(28209, "Transparent side panel", tda, 1, 0xFF9933, false, true);
		RSInterface.addText(28210, "Make each side panel transparent", tda, 0, 0xFF9933, false, true);

		// Chatbox toggle label
		RSInterface.addText(28211, "Transparent chatbox", tda, 1, 0xFF9933, false, true);
		RSInterface.addText(28212, "Make chatbox transparent", tda, 0, 0xFF9933, false, true);

		// Side-stones toggle label
		RSInterface.addText(28213, "Side-stones arrangement", tda, 1, 0xFF9933, false, true);
		RSInterface.addText(28214, "Change the side-stones arrangement", tda, 0, 0xFF9933, false, true);

		// Fixed mode warning (centered)
		RSInterface.addText(28215, "Settings are not applicable in fixed mode!", tda, 0, 0xFF9933, true, true);

		// === Layout: Bind elements to parent tab ===
		tab.totalChildren(14); // must match number of .child(...) calls

		tab.child(0,  28201, 145,  70);  // background
		tab.child(1,  28202, 270,  80);  // title text
		tab.child(2,  28203, 365,  77);  // close button
		tab.child(3,  28204, 365,  77);  // close button hover

		tab.child(4,  28206, 155, 110);  // transparent side panel toggle
		tab.child(5,  28207, 155, 165);  // transparent chatbox toggle
		tab.child(6,  28208, 155, 220);  // side-stones arrangement toggle

		tab.child(7,  28209, 200, 115);  // side panel label
		tab.child(8,  28210, 200, 130);  // side panel desc

		tab.child(9,  28211, 200, 170);  // chatbox label
		tab.child(10, 28212, 200, 185);  // chatbox desc

		tab.child(11, 28213, 200, 225);  // side-stones label
		tab.child(12, 28214, 200, 240);  // side-stones desc

		tab.child(13, 28215, 270, 261);  // fixed mode warning
	}


	public static void weaponStore(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(56500);
		addSprite(56501, 462);
		addText(56502, "Weapon Game Reward Store", jaybane, 2, 0xff9933, true, true);
		itemContainer(56503, 28, 8, 6, 50, false, "Select", "Buy");
		addText(56504, "Welcome to the Weapon Game Rewards store!", jaybane, 0, 0xff9933, true, true);
		addText(56505, "", jaybane, 0, 0xff9933, true, true);
		addText(56506, "", jaybane, 0, 0xff9933, false, true);
		addText(56507, "", jaybane, 0, 0xff9933, false, true);
		itemContainer(56508, 28, 8, 20, 50, false);
		addText(56509, "", jaybane, 0, 0xff9933, false, true);
		addHoverButton(56510, 17, 21, 21, "Close", 250, 56511, 3);
		addHoveredButton(56511, 18, 21, 21, 56512);
		tab.totalChildren(11);
		tab.child(0, 56501, 50, 20);
		tab.child(1, 56502, 255, 27);
		tab.child(2, 56503, 87, 105);
		tab.child(3, 56504, 192, 62);
		tab.child(4, 56505, 191, 77);
		tab.child(5, 56506, 325, 62);
		tab.child(6, 56508, 400, 60);
		tab.child(7, 56507, 325, 77);
		tab.child(8, 56509, 340, 69);
		tab.child(9, 56510, 440, 24);
		tab.child(10, 56511, 440, 24);
	}

	public static void ticketInterface(TextDrawingArea[] jaybane)
	{
		RSInterface boob = addInterface(51250);
		addSprite(51251, 463);
		addText(51252, "Ticket Manager", jaybane, 2, 0xff9933, true, true);
		addHoverButton(51253, 17, 21, 21, "Close", 250, 51254, 3);
		addHoveredButton(51254, 18, 21, 21, 51255);

		addInputField(51250, 51255, 20, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 175, 30, false, false, "");
		addText(51256, "Title:", jaybane, 3, 0xff9933, true, true);
		addText(51257, "Issue:", jaybane, 3, 0xff9933, true, true);
		addInputField(51250, 51258, 40, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 310, 25, false, false, "");
		addInputField(51250, 51259, 40, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 310, 25, false, false, "");

		addHoverButton(51260, 61, 75, 30, "Send ticket", -1, 51261, 1);
		addHoveredButton(51261, 62, 75, 30, 51262);

		addText(51263, "Send", jaybane, 3, 0xff9933, true, true);

		boob.totalChildren(12);

		boob.child(0, 51251, 75, 50);
		boob.child(1, 51252, 245, 62);
		boob.child(2, 51253, 390, 59);
		boob.child(3, 51254, 390, 59);
		boob.child(4, 51255, 158, 115);
		boob.child(5, 51256, 245, 98);
		boob.child(6, 51257, 245, 150);
		boob.child(7, 51258, 93, 170);
		boob.child(8, 51259, 93, 200);

		boob.child(9, 51260, 205, 235);
		boob.child(10, 51261, 205, 235);
		boob.child(11, 51263, 245, 242);
	}

	public static void weaponLobby(TextDrawingArea[] jaybane)
	{
		RSInterface boob = addInterface(41250);
		addSprite(41251, 453);
		addText(41252, "Stoners ready: 0", jaybane, 0, 0x33cc00, false, true);
		addText(41253, "(Need 5 to 20)", jaybane, 0, 0xFFcc33, false, true);
		addText(41254, "Next Departure: 60 sec", jaybane, 0, 0x33ccff, false, true);
		boob.totalChildren(4);
		boob.child(0, 41251, 378, 290);
		boob.child(1, 41252, 385, 292);
		boob.child(2, 41253, 385, 305);
		boob.child(3, 41254, 385, 318);
	}

	public static void weaponGame(TextDrawingArea[] jaybane)
	{
		RSInterface boob = addInterface(41270);
		addTransparentSprite(41271, 452);
		addText(41272, "Weapon Game", jaybane, 2, 0xff9933, true, true);
		addText(41273, "Time Left:", jaybane, 0, 0xff9933, false, true);
		addText(41274, "5:30", jaybane, 0, 0xFFFFFF, false, true);
		addText(41275, "Stoners:", jaybane, 0, 0xff9933, false, true);
		addText(41276, "7", jaybane, 0, 0xFFFFFF, false, true);
		addText(41277, "Leader:", jaybane, 0, 0xff9933, false, true);
		addText(41278, "JayBane", jaybane, 0, 0xFFFFFF, false, true);
		addText(41279, "Crates:", jaybane, 0, 0xff9933, false, true);
		addText(41280, "0:14", jaybane, 0, 0xFFFFFF, false, true);
		addText(41281, "Tier:", jaybane, 0, 0xff9933, false, true);
		addText(41282, "5", jaybane, 0, 0xFFFFFF, false, true);
		itemContainer(41283, 45, 8, 20, 50, true);

		addText(41284, "Current", jaybane, 0, 0xff9933, false, true);
		addText(41285, "Next", jaybane, 0, 0xff9933, false, true);

		boob.totalChildren(15);
		boob.child(0, 41271, 355, 2);
		boob.child(1, 41272, 433, 7);

		boob.child(2, 41273, 375, 25);
		boob.child(3, 41274, 455, 25);

		boob.child(4, 41275, 375, 40);
		boob.child(5, 41276, 455, 40);

		boob.child(6, 41277, 375, 55);
		boob.child(7, 41278, 455, 55);

		boob.child(8, 41279, 375, 70);
		boob.child(9, 41280, 455, 70);

		boob.child(10, 41281, 375, 85);
		boob.child(11, 41282, 455, 85);

		boob.child(12, 41283, 378, 103);

		boob.child(13, 41284, 373, 130);
		boob.child(14, 41285, 458, 130);
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

	public static void reportAbuse(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(41750);
		addSprite(41751, 449);

		addHoverButton(41752, 17, 21, 21, "Get out", 250, 41753, 3);
		addHoveredButton(41753, 18, 21, 21, 41754);

		addText(41755, "Snitch Form", jaybane, 2, 0xFF99CC, true, true);

		addText(41756, "Use this form to narc on stoners doing shady stuff.", jaybane, 1, 0xFF99CC, true, true);
		addText(41757, "It sends us your best selfie, possibly feet pics.", jaybane, 1, 0xFF99CC, true, true);
		addText(41758, "False reports will get you bonked. Possibly by a duck.", jaybane, 1, 0xDB0D0D, true, true);
		addText(41759, "Drop the username of the sketchy stoner below, we might review it.", jaybane, 1, 0xFF99CC, true, true);

		addInputField(41750, 41760, 25, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 180, 23, false, false, "");

		addText(41761, "Naah, not really, we dont give a fuck, but go ahead tho:", jaybane, 1, 0xFF99CC, true, true);

		String[] rules = {
			"Being way too high in public chat",
			"Claimed they had a GF in another server",
			"Scammed a leaf and ran off laughing",
			"Found a bug and called it a feature",
			"Wore a mod crown in roleplay",
			"Shared their Netflix account",
			"Gave me Monkey Nuts",
			"Logged in twice, called it 'duo mode'",
			"Spammed links to their mixtape",
			"Sold imaginary items for real hugs",
			"Asked mod help to open a jar",
			"Told others to type ::crashme"
		};

		for (int i = 0; i < rules.length; i++) {
			addHoverText(41762 + i, (i + 1) + ") " + rules[i], "Roast", jaybane, 0, 0xF7AA25, false, true, 250);
		}

		addHoverButton(41774, 446, 135, 30, "Snitch them", -1, 41775, 1);
		addHoveredButton(41775, 447, 135, 30, 41776);


		tab.totalChildren(24);
		tab.child(0, 41751, 11, 17);
		tab.child(1, 41752, 471, 24);
		tab.child(2, 41753, 471, 24);
		tab.child(3, 41755, 257, 26);
		tab.child(4, 41756, 257, 55);
		tab.child(5, 41757, 257, 70);
		tab.child(6, 41758, 257, 85);
		tab.child(7, 41759, 257, 105);
		tab.child(8, 41760, 170, 125);
		tab.child(9, 41761, 257, 160);

		tab.child(10, 41762, 65, 190);
		tab.child(11, 41763, 65, 205);
		tab.child(12, 41764, 65, 220);
		tab.child(13, 41765, 65, 235);
		tab.child(14, 41766, 65, 250);
		tab.child(15, 41767, 65, 265);

		tab.child(16, 41768, 285, 190);
		tab.child(17, 41769, 285, 205);
		tab.child(18, 41770, 285, 220);
		tab.child(19, 41771, 285, 235);
		tab.child(20, 41772, 285, 250);
		tab.child(21, 41773, 285, 265);

		tab.child(22, 41774, 160, 280);
		tab.child(23, 41775, 160, 280);
	}

	public static void tabCreation(TextDrawingArea[] TDA)
	{
		RSInterface tab = addTabInterface(26700);
		addSprite(26701, 448);
		addHoverButton(26702, 17, 21, 21, "Close", 250, 26703, 3);
		addHoveredButton(26703, 18, 21, 21, 26704);
		addText(26705, "Tablet Creation", TDA, 2, 0xff9933, true, true);
		itemDisplay(26706, 50, 12, 3, 5, "Make", "Info");
		addText(26707, "Please click on a tab to create it", TDA, 0, 0xff9933, true, true);
		addText(26708, "Click on 'info' to get requirements.", TDA, 0, 0xff9933, true, true);
		tab.totalChildren(7);
		tab.child(0, 26701, 100, 70);
		tab.child(1, 26702, 475, 10);
		tab.child(2, 26703, 475, 10);
		tab.child(3, 26705, 257, 80);
		tab.child(4, 26706, 158, 144);
		tab.child(5, 26707, 257, 112);
		tab.child(6, 26708, 257, 122);
	}

	public static void bounty(TextDrawingArea[] TDA)
	{
		RSInterface tab = addTabInterface(23300);
		addAdvancedSprite(23301, 434);
		addAdvancedSprite(23302, 433);
		addConfigSprite(23303, 435, -1, 0, 876);
		addSprite(23304, 441);
		addText(23305, "---", TDA, 0, 0xffff00, true, true);
		addText(23306, "Target:", TDA, 0, 0xffff00, true, true);
		addText(23307, "None", TDA, 1, 0xffffff, true, true);
		addText(23308, "Grade: ------", TDA, 0, 0xffff00, true, true);
		addText(23309, "Current  Record", TDA, 0, 0xffff00, true, true);
		addText(23310, "0", TDA, 0, 0xffff00, true, true);
		addText(23311, "0", TDA, 0, 0xffff00, true, true);
		addText(23312, "0", TDA, 0, 0xffff00, true, true);
		addText(23313, "0", TDA, 0, 0xffff00, true, true);
		addText(23314, "Rogue:", TDA, 0, 0xffff00, true, true);
		addText(23315, "Hunter:", TDA, 0, 0xffff00, true, true);
		addConfigSprite(23316, -1, 436, 0, 877);
		addConfigSprite(23317, -1, 437, 0, 878);
		addConfigSprite(23318, -1, 438, 0, 879);
		addConfigSprite(23319, -1, 439, 0, 880);
		addConfigSprite(23320, -1, 440, 0, 881);
		tab.totalChildren(21);
		tab.child(0, 23301, 319, 8);
		tab.child(1, 23302, 339, 56);
		tab.child(2, 23303, 345, 18);
		tab.child(3, 23304, 348, 73);
		tab.child(4, 23305, 358, 41);
		tab.child(5, 23306, 455, 12);
		tab.child(6, 23307, 456, 25);
		tab.child(7, 23308, 457, 41);
		tab.child(8, 23309, 460, 59);
		tab.child(9, 23310, 438, 72);
		tab.child(10, 23311, 481, 72);
		tab.child(11, 23312, 438, 85);
		tab.child(12, 23313, 481, 85);
		tab.child(13, 23314, 393, 72);
		tab.child(14, 23315, 394, 85);
		tab.child(15, 23316, 345, 18);
		tab.child(16, 23317, 345, 18);
		tab.child(17, 23318, 345, 18);
		tab.child(18, 23319, 345, 18);
		tab.child(19, 23320, 345, 18);
		tab.child(20, 197, 2, 2);
	}

	public static void fireColor(TextDrawingArea[] bestbudz)
	{
		RSInterface tab = addInterface(49750);
		addSprite(49751, 400);
		addHoverButton(49752, 21, 21, 21, "Close", 250, 49753, 3);
		addHoveredButton(49753, 22, 21, 21, 49754);
		addText(49755, "Fire Color Changer", 0xff9933, true, true, -1, bestbudz, 2);
		addText(49756, "By paying 10 CannaCredits you can change all fire colors.", 0xff9933, true, true, -1, bestbudz,
			0);
		addText(49757, "You will earn double foodie & pyromaniac exp for 30mins.", 0xff9933, true, true, -1, bestbudz,
			0);
		addText(49758, "Current Color: Orange", 0xff9933, true, true, -1, bestbudz, 0);
		addText(49759, "Changed by: Jaybane", 0xff9933, true, true, -1, bestbudz, 0);
		addText(49760, "JayBane's Cult: BestBudz", 0xff9933, true, true, -1, bestbudz, 0);
		addText(49761, "Change availability: 45minutes", 0xff9933, true, true, -1, bestbudz, 0);
		addHoverButton(49762, "", 0, 65, 95, "Select Orange", 0, 49763, 1);
		addHoveredButton(49763, 401, 65, 95, 49764);
		addHoverButton(49765, "", 0, 65, 95, "Select Green", 0, 49766, 1);
		addHoveredButton(49766, 402, 65, 95, 49767);
		addHoverButton(49768, "", 0, 65, 95, "Select Blue", 0, 49769, 1);
		addHoveredButton(49769, 403, 65, 95, 49770);
		addHoverButton(49771, "", 0, 65, 95, "Select Red", 0, 49772, 1);
		addHoveredButton(49772, 404, 65, 95, 49773);
		tab.totalChildren(18);
		tab.child(0, 49751, 65, 55);
		tab.child(1, 49752, 420, 63);
		tab.child(2, 49753, 420, 63);
		tab.child(3, 49755, 257, 63);
		tab.child(4, 49756, 257, 100);
		tab.child(5, 49757, 257, 112);
		tab.child(6, 49758, 257, 233);
		tab.child(7, 49759, 257, 245);
		tab.child(8, 49760, 257, 257);
		tab.child(9, 49761, 257, 269);
		tab.child(10, 49762, 92, 133);
		tab.child(11, 49763, 92, 133);
		tab.child(12, 49765, 181, 133);
		tab.child(13, 49766, 181, 133);
		tab.child(14, 49768, 270, 133);
		tab.child(15, 49769, 270, 133);
		tab.child(16, 49771, 359, 133);
		tab.child(17, 49772, 359, 133);
	}

	public static void inPvP(TextDrawingArea[] jaybane)
	{
		RSInterface Interface = addInterface(60250);
		addSprite(60251, 422);

		addTooltipBox(60252, "You are currently standing in a @red@SvS@bla@ Zone.");
		interfaceCache[60252].width = 30;
		interfaceCache[60252].height = 35;

		addText(60253, "@or1@116 - 126", 0x000000, true, true, 52, jaybane, 0);
		addText(60254, "@or1@- - -", 0x000000, true, true, 52, jaybane, 0);

		Interface.totalChildren(4);
		Interface.child(0, 60251, 455, 275);
		Interface.child(1, 60252, 460, 295);
		Interface.child(2, 60253, 474, 310);
		Interface.child(3, 60254, 474, 320);
	}

	public static void godWars(TextDrawingArea[] jaybane)
	{
		int ID = 61750;
		RSInterface Interface = addInterface(ID);
		addText(ID + 1, "NPC Killcount", 0xEDE026, false, true, 52, jaybane, 0);
		String[] TEXTS = {"Armadyl", "Bandos", "Saradomin", "Zamorak"};
		for (int index = 0; index < 4; index++)
		{
			addText(ID + (2 + index), TEXTS[index] + " kills", 0xff9040, false, true, 52, jaybane, 0);
		}
		for (int index = 0; index < 4; index++)
		{
			addText(ID + (6 + index), "0", 0x54BFE3, false, true, 52, jaybane, 0);
		}
		Interface.totalChildren(9);
		Interface.child(0, ID + 1, 390, 7);
		for (int index = 0; index < 4; index++)
		{
			Interface.child(1 + index, ID + (2 + index), 390, 30 + (index * 15));
		}
		for (int index = 0; index < 4; index++)
		{
			Interface.child(5 + index, ID + (6 + index), 480, 30 + (index * 15));
		}
	}

	public static void inSafe()
	{
		RSInterface Interface = addInterface(60350);
		addSprite(60351, 423);

		addTooltipBox(60352, "You are currently standing in a @blu@Safe@bla@ Zone.");
		interfaceCache[60352].width = 30;
		interfaceCache[60352].height = 35;

		Interface.totalChildren(2);
		Interface.child(0, 60351, 460, 295);
		Interface.child(1, 60352, 460, 295);
	}

	public static void inTimer(TextDrawingArea[] jaybane)
	{
		RSInterface Interface = addInterface(60450);
		addSprite(60451, 424);
		addTooltipBox(60452, "You currently have a SvS timer.");
		addText(60453, "@whi@10", 0x000000, true, true, 52, jaybane, 3);
		interfaceCache[60452].width = 30;
		interfaceCache[60452].height = 35;

		Interface.totalChildren(3);
		Interface.child(0, 60451, 460, 295);
		Interface.child(1, 60452, 460, 295);
		Interface.child(2, 60453, 476, 303);
	}

	public static void PKSkillTab(TextDrawingArea[] jaybane)
	{
		RSInterface Interface = addInterface(63700);
		addSprite(63701, 71);
		String[] grades = {"Assault", "Aegis", "Vigour", "Life", "Sagittarius", "Necromance", "Mage", "Bounty"};
		for (int i = 0; i < 8; i++)
		{
			addText(63702 + i, "@or1@" + grades[i], 0x000000, false, true, 52, jaybane, 2);
		}
		for (int i = 0; i < 8; i++)
		{
			addText(63720 + i, "@or2@420/420", 0x000000, false, true, 52, jaybane, 1);
		}
		addHoverButton(64102, -1, 300, 25, "Set assault grade", -1, 64103, 1);
		addHoveredButton(64103, 386, 300, 25, 64104);
		addHoverButton(64105, -1, 300, 25, "Set aegis grade", -1, 64106, 1);
		addHoveredButton(64106, 388, 300, 25, 64107);
		addHoverButton(64108, -1, 300, 25, "Set vigour grade", -1, 64109, 1);
		addHoveredButton(64109, 387, 300, 25, 64110);
		addHoverButton(64111, -1, 300, 25, "Set life grade", -1, 64112, 1);
		addHoveredButton(64112, 392, 300, 25, 64113);
		addHoverButton(64114, -1, 300, 25, "Set sagittarius grade", -1, 64115, 1);
		addHoveredButton(64115, 389, 300, 25, 64116);
		addHoverButton(64117, -1, 300, 25, "Set necromance grade", -1, 64118, 1);
		addHoveredButton(64118, 391, 300, 25, 64119);
		addHoverButton(64120, -1, 300, 25, "Set mage grade", -1, 64121, 1);
		addHoveredButton(64121, 390, 300, 25, 64122);
		Interface.totalChildren(31);
		Interface.child(0, 63701, 1, 8);
		Interface.child(1, 64102, 2, 10);
		Interface.child(2, 64103, 2, 10);
		Interface.child(3, 64105, 2, 40);
		Interface.child(4, 64106, 2, 40);
		Interface.child(5, 64108, 2, 70);
		Interface.child(6, 64109, 2, 70);
		Interface.child(7, 64111, 2, 100);
		Interface.child(8, 64112, 2, 100);
		Interface.child(9, 64114, 2, 130);
		Interface.child(10, 64115, 2, 130);
		Interface.child(11, 64117, 2, 160);
		Interface.child(12, 64118, 2, 160);
		Interface.child(13, 64120, 2, 190);
		Interface.child(14, 64121, 2, 190);
		for (int i = 0; i < 8; i++)
		{
			Interface.child(15 + i, 63702 + i, 30, 16 + (i * 30));
		}
		for (int i = 0; i < 8; i++)
		{
			Interface.child(23 + i, 63720 + i, 125, 17 + (i * 30));
		}
	}

	public static void questInterface(TextDrawingArea[] TDA)
	{
		RSInterface Interface = addInterface(8134);
		setChildren(0, Interface);
	}


	public static void settings(TextDrawingArea[] jaybane) {
		RSInterface tab = addInterface(28400);

		addHoverButton(28405, 446, 130, 24, "Confirm Selection", -1, 28406, 1);
		addHoveredButton(28406, 447, 130, 24, 28407);

		tab.totalChildren(2);
		tab.child(0, 28405, -10, 233);
		tab.child(1, 28406, -10, 233);
	}

	public static void staffTab(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(49700);
		addSprite(49701, 360);
		addSprite(49702, 38);
		addText(49703, "Staff Tab", jaybane, 2, 0xF7AA25, true, true);
		addText(49704, "You have access to all commands!", jaybane, 0, 0xF7AA25, true, true);
		addText(49705, "</col>Rank: @red@<img=2> Owner", jaybane, 0, 0xF7AA25, true, true);
		addHoverText(49706, "> Back to BestBudz Tab <", "Go back", jaybane, 0, 0xF7AA25, true, true, 60);

		tab.totalChildren(8);
		tab.child(0, 49701, -4, 34);
		tab.child(1, 49702, -0, 34);
		tab.child(2, 49702, -0, 229);
		tab.child(3, 49703, 93, 4);
		tab.child(4, 49704, 93, 235);
		tab.child(5, 49710, 5, 36);
		tab.child(6, 49705, 93, 20);
		tab.child(7, 49706, 61, 247);

		String[] titles = {
			"@mbl@Check bank user", "@mbl@Kick user", "@mbl@Mute user", "@mbl@Unmute user", "@mbl@Ban user",
			"@mbl@Unban user", "@mbl@Jail user", "@mbl@Unjail user", "@mbl@Move home user", "@mye@Copy user",
			"@mye@Freeze user", "@mbl@Get info user", "@mye@Demote user", "@mye@Give mod user", "@mre@Kill user",
			"@mbl@Tele to user", "@mbl@Tele to me user", "@mre@Boo user", "@mre@Random NPC user", "@mye@Refresh"
		};

		int amount = titles.length;

		RSInterface scrollInterface = addTabInterface(49710);
		scrollInterface.width = 170;
		scrollInterface.height = 192;
		scrollInterface.scrollMax = 665;
		setChildren(amount, scrollInterface);
		int y = 0;
		for (int i = 0; i < amount; i++)
		{
			addInputField(49700, 49720 + i, 15, 0x332E24, 0x4D4636, 0x383631, 0x474540, "", 136, 25, false, false,
				titles[i]);
			setBounds(49720 + i, 15, y + 5, i, scrollInterface);
			y += 35;
		}

	}



	public static void stonersTab(TextDrawingArea[] tda)
	{
		RSInterface tab = addTabInterface(5065);
		tab.totalChildren(0);
	}

	public static void ignoreTab(TextDrawingArea[] tda)
	{
		RSInterface tab = addTabInterface(5715);
		tab.totalChildren(0);
	}


	public static void chatColor(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(37500);
		tab.totalChildren(0);
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

	public static void mysteryBox(TextDrawingArea[] jaybane)
	{
		RSInterface rsinterface = addTabInterface(17000);
		addSprite(17001, 257);
		lotteryItem(17002);
		mysteryWonItem(17007);
		addText(17003, "Misery Box ", jaybane, 2, 0xF7AA25, true, true);
		addHoverButton(17004, 258, 146, 37, "Bet CannaCredits", -200, 17005, 1);
		addHoveredButton(17005, 259, 146, 37, 9454);
		addText(17006, "</col>CannaCredits: @gre@1,000", jaybane, 0, 0xF7AA25, true, true);
		addText(17015, "", jaybane, 0, 0xF7AA25, true, true);
		addHoverButton(47267, 21, 16, 16, "Close", -1, 47268, 1);
		addHoveredButton(47268, 22, 16, 16, 47269);
		addText(17008, "Misery Box is a gambling game where you bet CannaCredits.", jaybane, 0, 0xF7AA25,
			true,
			true);
		addText(17009, "Rewards can vary from items worth @gre@1,000</col> - @gre@100,000,000</col> bestbucks!", jaybane, 0,
			0xF7AA25, true, true);
		addText(17010, "Good luck wasting those credits. (Cost = 200 CC)", jaybane, 0, 0xF7AA25, true, true);

		addText(17012, "Play", jaybane, 2, 0xF7AA25, true, true);
		rsinterface.totalChildren(15);

		rsinterface.child(0, 17001, 14, 5);
		rsinterface.child(1, 17002, 240, 190);
		rsinterface.child(2, 17003, 260, 10);
		rsinterface.child(3, 17004, 44, 123);
		rsinterface.child(4, 17005, 44, 123);
		rsinterface.child(5, 47267, 474, 8);
		rsinterface.child(6, 47268, 474, 8);
		rsinterface.child(7, 17008, 260, 55);
		rsinterface.child(8, 17009, 260, 70);
		rsinterface.child(9, 17010, 260, 85);
		rsinterface.child(10, 17011, 800, 800);
		rsinterface.child(11, 17006, 407, 128);
		rsinterface.child(12, 17007, 397, 127);
		rsinterface.child(13, 17015, 255, 300);
		rsinterface.child(14, 17012, 120, 130);
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
		addHoverButton(59521, "", 0, buttonWidth, buttonHeight, "Reset Necromance", 0, 59522, 1);
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

	public static void itemsOnDeath(TextDrawingArea[] jaybane)
	{
		RSInterface rsinterface = addInterface(17100);
		addSprite(17101, 242);
		addHoverButton(17102, 21, 15, 15, "Close", 250, 10601, 3);
		addHoveredButton(10601, 22, 15, 15, 10602);
		addText(17103, "Items Kept on Death", jaybane, 2, 0xff981f);
		addText(17104, "Items you keep on death if not skulled:", jaybane, 0, 0xff981f);
		addText(17105, "Items you loose on death if not skulled:", jaybane, 0, 0xff981f);
		addText(17106, "Information:", jaybane, 1, 0xff981f);
		addText(17107, "Max Items Kept on death:", jaybane, 1, 0xff981f);
		addText(17108, "~ 3 ~", jaybane, 1, 0xffcc33);
		rsinterface.scrollMax = 0;
		rsinterface.children = new int[12];
		rsinterface.childX = new int[12];
		rsinterface.childY = new int[12];
		rsinterface.children[0] = 17101;
		rsinterface.childX[0] = 7;
		rsinterface.childY[0] = 8;
		rsinterface.children[1] = 17102;
		rsinterface.childX[1] = 480;
		rsinterface.childY[1] = 17;
		rsinterface.children[2] = 17103;
		rsinterface.childX[2] = 185;
		rsinterface.childY[2] = 18;
		rsinterface.children[3] = 17104;
		rsinterface.childX[3] = 22;
		rsinterface.childY[3] = 50;
		rsinterface.children[4] = 17105;
		rsinterface.childX[4] = 22;
		rsinterface.childY[4] = 110;
		rsinterface.children[5] = 17106;
		rsinterface.childX[5] = 347;
		rsinterface.childY[5] = 47;
		rsinterface.children[6] = 17107;
		rsinterface.childX[6] = 349;
		rsinterface.childY[6] = 270;
		rsinterface.children[7] = 17108;
		rsinterface.childX[7] = 398;
		rsinterface.childY[7] = 298;
		rsinterface.children[8] = 17115;
		rsinterface.childX[8] = 348;
		rsinterface.childY[8] = 64;
		rsinterface.children[9] = 10494;
		rsinterface.childX[9] = 26;
		rsinterface.childY[9] = 74;
		rsinterface.children[10] = 10600;
		rsinterface.childX[10] = 26;
		rsinterface.childY[10] = 133;
		rsinterface.children[11] = 10601;
		rsinterface.childX[11] = 480;
		rsinterface.childY[11] = 17;
		rsinterface = interfaceCache[10494];
		rsinterface.invSpritePadX = 6;
		rsinterface.invSpritePadY = 5;
		rsinterface = interfaceCache[10600];
		rsinterface.invSpritePadX = 6;
		rsinterface.invSpritePadY = 5;
	}

	public static void creditsTab(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(52500);
		addSprite(52501, 38);
		addText(52502, "CannaCredits", 0xff9933, true, true, -1, jaybane, 2);
		addSprite(52503, 55);
		addText(52504, "Current CannaCredits:", 0xff9933, true, true, -1, jaybane, 0);
		addHoverText(52505, " ", " ", jaybane, 0, 0xF7AA25, true, true, 168);
		addSprite(52506, 56);
		tab.totalChildren(9);
		tab.child(0, 52501, 0, 25);
		tab.child(1, 52502, 95, 5);
		tab.child(2, 52503, -5, 28);
		tab.child(3, 52504, 95, 35);
		tab.child(4, 52505, 12, 50);
		tab.child(5, 52506, 2, 79);
		tab.child(6, 52501, 0, 70);
		tab.child(7, 52501, 0, 254);
		tab.child(8, 52530, 5, 79);
		RSInterface scrollInterface = addTabInterface(52530);
		scrollInterface.parentID = 52500;
		scrollInterface.scrollPosition = 0;
		scrollInterface.atActionType = 0;
		scrollInterface.contentType = 0;
		scrollInterface.width = 169;
		scrollInterface.height = 167;
		scrollInterface.scrollMax = 150;
		int x = 7, y = 9;
		scrollInterface.totalChildren(30);
		for (int i = 0; i < 30; i++)
		{
			addHoverText(52531 + i, "", "Spend CannaCredit", jaybane, 0, 0xE3A724, true, true, 168, 0xFFFFFF);
			scrollInterface.child(i, 52531 + i, x, y);
			y += 18;
		}
	}

	public static void shopExchange(TextDrawingArea[] jaybane)
	{
		RSInterface tab = addInterface(53500);
		addSprite(53501, 43);
		addHoverButton(53502, 17, 21, 21, "Close", 250, 53503, 3);
		addHoveredButton(53503, 18, 21, 21, 53504);
		addText(53505, "Shops Exchange", 0xff9933, true, true, -1, jaybane, 2);
		addHoverButton(53506, 44, 45, 45, "Search", 0, 53507, 1);
		addHoveredButton(53507, 45, 45, 45, 53508);
		addText(53509, "Shop Owner:", 0xff9933, true, true, -1, jaybane, 2);
		addText(53510, "Shop Description:", 0xff9933, true, true, -1, jaybane, 2);
		tab.totalChildren(9);
		tab.child(0, 53501, 11, 2);
		tab.child(1, 53502, 475, 8);
		tab.child(2, 53503, 475, 8);
		tab.child(3, 53505, 255, 12);
		tab.child(4, 53506, 456, 36);
		tab.child(5, 53507, 456, 36);
		tab.child(6, 53509, 109, 65);
		tab.child(7, 53510, 330, 65);
		tab.child(8, 53515, 45, 84);
		RSInterface scrollInterface = addTabInterface(53515);
		scrollInterface.width = 412;
		scrollInterface.height = 225;
		scrollInterface.scrollMax = 3000;
		setChildren(400, scrollInterface);
		int y = 0;
		for (int i = 0; i < 200; i++)
		{
			addHoverText(53516 + i, "Name: " + i, "View", jaybane, 0, 0xcc8000, false, true, 100, 0xFFFFFF);
			setBounds(53516 + i, 0, y, i, scrollInterface);
			y += 20;
		}
		y = 0;
		for (int i = 0; i < 200; i++)
		{
			addHoverText(53516 + 200 + i, "Motto: " + i, "View", jaybane, 0, 0xcc8000, false, true, 100, 0xFFFFFF);
			setBounds(53516 + 200 + i, 150, y, i + 200, scrollInterface);
			y += 20;
		}
	}

	private static void achievementsTab(TextDrawingArea[] jaybane) // JFRAMED
	{
		RSInterface tab = addTabInterface(31000);
	//	addSprite(31001, 15);
	//	addText(31002, "Achievements", 0xff9040, false, true, 52, jaybane, 2);
		setChildren(0, tab);
	/*	setBounds(31001, 0, -3, 0, tab);
		setBounds(31002, 45, 5, 1, tab);
		setBounds(31003, -6, 25, 2, tab);
		addHoverButton(31004, 13, 25, 25, "Back to Quest tab", -1, 31005, 1);
		addHoveredButton(31005, 14, 25, 25, 31096);
		RSInterface scrollInterface = addTabInterface(31003);
		scrollInterface.width = 179;
		scrollInterface.height = 236;
		scrollInterface.scrollMax = 5 + 15 * 80;
		setChildren(80, scrollInterface);
		int y = 5;
		for (int i = 0; i < 80; i++)
		{
			addHoverText(31006 + i, "", "View", jaybane, 0, 0xff0000, false, true, 300);
			setBounds(31006 + i, 11, y, i, scrollInterface);
			if (i == 4)
			{
				y += 15;
			}
			y += 15;
		}
		tab.child(3, 31004, 160, 0);
		tab.child(4, 31005, 160, 0);

	 */
	}

	public static void clanChatTab(TextDrawingArea[] bestbudz)
	{
		RSInterface tab = addTabInterface(18128);
		addHoverButton(18129, 40, 72, 32, "Join Cult", 550, 18130, 5);
		addHoveredButton(18130, 41, 72, 32, 18131);
		addHoverButton(18132, 40, 72, 32, "Cult Setup", -1, 18133, 5);
		addHoveredButton(18133, 41, 72, 32, 18134);

		addButtons(18250, 31, "Share with cult", 18253, 1);
		drawTooltip(18253, "Share with cult");

		addText(18135, "Join/Leave", bestbudz, 0, 0xff9b00, true, true);
		addText(18136, "Cult Setup", bestbudz, 0, 0xff9b00, true, true);
		addSprite(18137, 42);
		addText(18138, "CULT", bestbudz, 2, 0xff9b00, true, true);
		addText(18139, "Loyalty: Not in cult", bestbudz, 0, 0xff9b00, false, true);
		addText(18140, "High Priest: None", bestbudz, 0, 0xff9b00, false, true);

		addText(18252, "(0/100)", bestbudz, 0, 0xff9b00, false, true);
		tab.totalChildren(14);
		tab.child(0, 18137, 3, 57);
		tab.child(1, 18143, 7, 62);
		tab.child(2, 18129, 15, 227);
		tab.child(3, 18130, 15, 227);
		tab.child(4, 18132, 96, 227);
		tab.child(5, 18133, 96, 227);
		tab.child(6, 18135, 51, 236);
		tab.child(7, 18136, 132, 236);
		tab.child(8, 18138, 95, 3);
		tab.child(9, 18139, 10, 23);
		tab.child(10, 18140, 25, 38);
		tab.child(11, 18250, 148, 24);
		tab.child(12, 18253, 74, 60);
		tab.child(13, 18252, 140, 5);
		RSInterface list = addTabInterface(18143);
		list.totalChildren(100);
		for (int i = 18144; i <= 18244; i++)
		{
			addText(i, "", bestbudz, 0, 0xffffff, false, true);
		}
		for (int id = 18144, i = 0; id <= 18243 && i <= 99; id++, i++)
		{
			list.children[i] = id;
			list.childX[i] = 5;
			for (int id2 = 18144, i2 = 1; id2 <= 18243 && i2 <= 99; id2++, i2++)
			{
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}
		for (int id = 18144, i = 0; id <= 18243 && i <= 99; id++, i++)
		{
			interfaceCache[id].actions = new String[]{"Edit Rank", "Kick", "Ban"};
			list.children[i] = id;
			list.childX[i] = 5;
			for (int id2 = 18144, i2 = 1; id2 <= 18243 && i2 <= 99; id2++, i2++)
			{
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}
		list.height = 152;
		list.width = 164;
		list.scrollMax = 1405;
	}

	public static void clanChatSetup(TextDrawingArea[] bestbudz)
	{
		RSInterface rsi = addInterface(43700);
		rsi.totalChildren(12 + 15);
		int count = 0;
		addSprite(43701, 35);
		rsi.child(count++, 43701, 14, 18);
		addButton(43702, 21, "Close");
		interfaceCache[43702].atActionType = 3;
		rsi.child(count++, 43702, 475, 26);
		addText(43703, "Cult Setup", bestbudz, 2, 0xFF981F, true, true);
		rsi.child(count++, 43703, 256, 26);
		String[] titles = {"Cult:", "Who can enter cult?", "Who can talk in the cult?", "Who can kick in cult?",
			"Who can ban in cult?"};
		String[] defaults = {"Chat Disabled", "Initiate", "Initiate", "Initiate", "Initiate"};
		String[] whoCan = {"Initiate", "Disciple", "Mendicant", "Priest", "Lector", "Arch Lector", "High Priest",
			"Only Me"};
		for (int index = 0, id = 43704, y = 50; index < titles.length; index++, id += 3, y += 40)
		{
			addButton(id, 36, "");
			interfaceCache[id].atActionType = 0;
			if (index > 0)
			{
				interfaceCache[id].actions = whoCan;
			}
			else
			{
				interfaceCache[id].actions = new String[]{"Change title", "Delete cult"};
			}
			addText(id + 1, titles[index], bestbudz, 0, 0xFF981F, true, true);
			addText(id + 2, defaults[index], bestbudz, 1, 0xFFFFFF, true, true);
			rsi.child(count++, id, 25, y);
			rsi.child(count++, id + 1, 100, y + 4);
			rsi.child(count++, id + 2, 100, y + 17);
		}
		addSprite(43719, 39);
		rsi.child(count++, 43719, 197, 70);
		int id = 43720;
		int y = 74;
		addText(id, "Ranked Cultists", bestbudz, 2, 0xFF981F, false, true);
		rsi.child(count++, id++, 202, y);
		addText(id, "Banned Cultists", bestbudz, 2, 0xFF981F, false, true);
		rsi.child(count++, id++, 339, y);
		RSInterface list = addInterface(id++);
		int lines = 100;
		list.totalChildren(lines);
		String[] ranks = {"Demote", "Initiate", "Disciple", "Mendicant", "Priest", "Lector", "Arch Lector",
			"High Priest"};
		list.childY[0] = 2;
		for (int index = id; index < id + lines; index++)
		{
			addText(index, "", bestbudz, 1, 0xffffff, false, true);
			interfaceCache[index].actions = ranks;
			list.children[index - id] = index;
			list.childX[index - id] = 2;
			list.childY[index - id] = (index - id > 0 ? list.childY[index - id - 1] + 14 : 0);
		}
		id += lines;
		list.width = 119;
		list.height = 210;
		list.scrollMax = (lines * 14) + 2;
		rsi.child(count++, list.id, 199, 92);
		list = addInterface(id++);
		list.totalChildren(lines);
		list.childY[0] = 2;
		for (int index = id; index < id + lines; index++)
		{
			addText(index, "", bestbudz, 1, 0xffffff, false, true);
			interfaceCache[index].actions = new String[]{"Unban"};
			list.children[index - id] = index;
			list.childX[index - id] = 0;
			list.childY[index - id] = (index - id > 0 ? list.childY[index - id - 1] + 14 : 0);
		}
		id += lines;
		list.width = 119;
		list.height = 210;
		list.scrollMax = (lines * 14) + 2;
		rsi.child(count++, list.id, 339, 92);
		y = 47;
		addText(id, "Manage both ranked and banned Cultists here.", bestbudz, 0, 0xFF981F, true, true);
		rsi.child(count++, id++, 337, y);
		addText(id, "Right click on a Cultist to edit.", bestbudz, 0, 0xFF981F, true, true);
		rsi.child(count++, id++, 337, y + 11);
		y = 75;
		addButton(id, 33, "Add ranked Cultist");
		interfaceCache[id].atActionType = 5;
		rsi.child(count++, id++, 319, y);
		addButton(id, 33, "Add banned Cultist");
		interfaceCache[id].atActionType = 5;
		rsi.child(count++, id++, 459, y);
		int[] clanSetup = {43702, 43704, 43707, 43710, 43713, 43716, 43826, 43827};
		String[] names = {"close", "sprite", "sprite", "sprite", "sprite", "sprite", "plus", "plus"};
		int[] ids = {1, 3, 3, 3, 3, 3, 1, 1};
		for (int index = 0; index < clanSetup.length; index++)
		{
			rsi = interfaceCache[clanSetup[index]];
			rsi.disabledHover = imageLoader(ids[index], "Interfaces/Clan Chat/" + names[index]);
		}
	}

	public static void questTab(TextDrawingArea[] bestbudz) // JFRAMED
	{
		RSInterface tab = addTabInterface(29400);
		/*addSprite(29401, 82);
		addSprite(29402, 38);
		addText(29403, "BestBudz", bestbudz, 2, 0xF7AA25, true, true);
		addText(29407, "Stay high!", bestbudz, 2, 0xF7AA25, true, true);
		addHoverButton(29404, 84, 25, 25, "Achievements", -1, 29405, 1);
		addHoveredButton(29405, 85, 25, 25, 29406);
		addHoverButton(29410, 351, 15, 15, "Refresh", -1, 29411, 1);
		addHoveredButton(29411, 352, 15, 15, 29412);
		addHoverText(29413, "", "", bestbudz, 0, 0x47B320, false, true, 50);
		tab.scrollMax = 0;*/
		tab.totalChildren(0);
		/*
		tab.child(0, 29401, -4, 34);
		tab.child(1, 29402, -0, 34);
		tab.child(2, 29402, -0, 229);
		tab.child(3, 29403, 92, 9);
		tab.child(4, 29404, 160, 4);
		tab.child(5, 29405, 160, 4);
		tab.child(6, 29407, 92, 239);
		tab.child(7, 29500, 10, 36);
		tab.child(8, 29410, 160, 36);
		tab.child(9, 29411, 160, 36);
		tab.child(10, 29413, 8, 22);
		RSInterface scrollInterface = addTabInterface(29500);
		scrollInterface.parentID = 29400;
		scrollInterface.scrollPosition = 0;
		scrollInterface.atActionType = 0;
		scrollInterface.contentType = 0;
		scrollInterface.width = 165;
		scrollInterface.height = 193;
		scrollInterface.scrollMax = 1520;
		int x = 7, y = 9;
		int amountOfLines = 100;
		scrollInterface.totalChildren(amountOfLines);
		for (int i = 0; i < amountOfLines; i++)
		{
			addHoverText(29501 + i, "", "", bestbudz, 0, 0x47B320, false, true, 168);
			scrollInterface.child(i, 29501 + i, x, y);
			y += 18;
		}

		 */
	}

}