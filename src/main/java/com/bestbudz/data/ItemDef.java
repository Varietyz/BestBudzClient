package com.bestbudz.data;

import com.bestbudz.client.Client;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.Rasterizer;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.MRUNodes;

public final class ItemDef {

	public static MRUNodes mruNodes1 = new MRUNodes(100);
	public static MRUNodes mruNodes2 = new MRUNodes(50);
	public static boolean isMembers = true;
	public static int totalItems;
	private static ItemDef[] cache;
	private static int cacheIndex;
	private static Stream stream;
	private static int[] streamIndices;
	public int value;
	public int[] modifiedModelColors;
	public int id;
	public int[] originalModelColors;
	public boolean membersObject;
	public int certTemplateID;
	public int anInt164;
	public int anInt165;
	public String[] groundActions;
	public int modelOffset1;
	public String name;
	public int modelID;
	public int anInt175;
	public boolean stackable;
	public byte[] description;
	public int certID;
	public int modelZoom;
	public int anInt188;
	public String[] itemActions;
	public String[] equipActions;
	public int modelRotationY;
	public int[] stackIDs;
	public int modelOffset2;
	public int anInt197;
	public int modelRotationX;
	public int anInt200;
	public int[] stackAmounts;
	public int team;
	public int anInt204;
	private byte aByte154;
	private int anInt162;
	private int anInt166;
	private int anInt167;
	private int anInt173;
	private int anInt184;
	private int anInt185;
	private int anInt191;
	private int anInt192;
	private int anInt196;
	private byte aByte205;
	private ItemDef() {
		id = -1;
	}

	public static ItemDef getItemDefinition(int id)
	{
		for (int j = 0; j < 10; j++)
			if (cache[j].id == id)
				return cache[j];

		cacheIndex = (cacheIndex + 1) % 10;
		ItemDef itemDef = cache[cacheIndex];
		stream.currentOffset = streamIndices[id];
		itemDef.id = id;
		itemDef.setDefaults();
		itemDef.readValues(stream);
		if (!isMembers && itemDef.membersObject)
		{
			itemDef.name = "Members Object";
			itemDef.description = "Login to a members' server to use this object.".getBytes();
			itemDef.groundActions = null;
			itemDef.itemActions = null;
			itemDef.team = 0;

		}
		switch (id)
		{

			case 13190:
				itemDef.name = "1,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13191:
				itemDef.name = "3,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13192:
				itemDef.name = "5,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13193:
				itemDef.name = "8,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13194:
				itemDef.name = "10,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				itemDef.stackable = false;
				break;

			case 13195:
				itemDef.name = "20,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13196:
				itemDef.name = "50,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13197:
				itemDef.name = "100,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;

			case 13198:
				itemDef.name = "200,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;
			case 6575:
				itemDef.name = "Tool Ring";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A ring that transforms into any tool when worn.".getBytes();
				break;
			case 6577:
				itemDef.name = "Fisher Necklace";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A necklace to passively attract and feed all kinds of fish."
					.getBytes();
				break;
			case 6904:
				itemDef.name = "Fish bones";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Feed";
				itemDef.description = "The best part of the fish!"
					.getBytes();
				break;
			case 2577:
				itemDef.name = "Robin hood boots";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Yep, robin's boots."
					.getBytes();
				break;

			case 9005:
				itemDef.name = "Rainbow socks";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Try on";
				itemDef.description = "Yep, robin's socks."
					.getBytes();
				break;
			case 13200:
				itemDef.name = "Assault master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65299;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65298;
				itemDef.anInt200 = 65298;
				itemDef.stackable = false;
				itemDef.description = "It's a Assault master cape.".getBytes();
				break;

			case 13201:
				itemDef.name = "Aegis master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65301;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65300;
				itemDef.anInt200 = 65300;
				itemDef.stackable = false;
				itemDef.description = "It's a Aegis master cape.".getBytes();
				break;

			case 13202:
				itemDef.name = "Vigour master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65303;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65302;
				itemDef.anInt200 = 65302;
				itemDef.stackable = false;
				itemDef.description = "It's a Stength master cape.".getBytes();
				break;

			case 13203:
				itemDef.name = "Life master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65305;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65304;
				itemDef.anInt200 = 65304;
				itemDef.stackable = false;
				itemDef.description = "It's a Life master cape.".getBytes();
				break;

			case 13204:
				itemDef.name = "Sagittarius master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65307;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65306;
				itemDef.anInt200 = 65306;
				itemDef.stackable = false;
				itemDef.description = "It's a Sagittarius master cape.".getBytes();
				break;

			case 13205:
				itemDef.name = "Necromance master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65309;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65308;
				itemDef.anInt200 = 65308;
				itemDef.stackable = false;
				itemDef.description = "It's a Necromance master cape.".getBytes();
				break;

			case 13206:
				itemDef.name = "Mage master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65311;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65310;
				itemDef.anInt200 = 65310;
				itemDef.stackable = false;
				itemDef.description = "It's a Mage master cape.".getBytes();
				break;

			case 13207:
				itemDef.name = "Foodie master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65313;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65312;
				itemDef.anInt200 = 65312;
				itemDef.stackable = false;
				itemDef.description = "It's a Foodie master cape.".getBytes();
				break;

			case 13208:
				itemDef.name = "Lumbering master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65315;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65314;
				itemDef.anInt200 = 65314;
				itemDef.stackable = false;
				itemDef.description = "It's a Lumbering master cape.".getBytes();
				break;

			case 13209:
				itemDef.name = "Woodcarving master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65317;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65316;
				itemDef.anInt200 = 65316;
				itemDef.stackable = false;
				itemDef.description = "It's a Woodcarving master cape.".getBytes();
				break;

			case 13210:
				itemDef.name = "Fisher master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65319;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65318;
				itemDef.anInt200 = 65318;
				itemDef.stackable = false;
				itemDef.description = "It's a Fisher master cape.".getBytes();
				break;

			case 13211:
				itemDef.name = "Pyromaniac master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65321;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65320;
				itemDef.anInt200 = 65320;
				itemDef.stackable = false;
				itemDef.description = "It's a Pyromaniac master cape.".getBytes();
				break;

			case 13212:
				itemDef.name = "Handiness master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65323;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65322;
				itemDef.anInt200 = 65322;
				itemDef.stackable = false;
				itemDef.description = "It's a Handiness master cape.".getBytes();
				break;

			case 13213:
				itemDef.name = "Forging master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65325;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65324;
				itemDef.anInt200 = 65324;
				itemDef.stackable = false;
				itemDef.description = "It's a Forging master cape.".getBytes();
				break;

			case 13214:
				itemDef.name = "Quarrying master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65327;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65326;
				itemDef.anInt200 = 65326;
				itemDef.stackable = false;
				itemDef.description = "It's a Quarrying master cape.".getBytes();
				break;

			case 13215:
				itemDef.name = "THC-hempistry master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65329;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65328;
				itemDef.anInt200 = 65328;
				itemDef.stackable = false;
				itemDef.description = "It's a THC-hempistry master cape.".getBytes();
				break;

			case 13216:
				itemDef.name = "Weedsmoking master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65331;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65330;
				itemDef.anInt200 = 65330;
				itemDef.stackable = false;
				itemDef.description = "It's a Weedsmoking master cape.".getBytes();
				break;

			case 13217:
				itemDef.name = "Accomplisher master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65333;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65332;
				itemDef.anInt200 = 65332;
				itemDef.stackable = false;
				itemDef.description = "It's a Accomplisher master cape.".getBytes();
				break;

			case 13218:
				itemDef.name = "Mercenary master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65335;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65334;
				itemDef.anInt200 = 65334;
				itemDef.stackable = false;
				itemDef.description = "It's a Mercenary master cape.".getBytes();
				break;

			case 13219:
				itemDef.name = "Cultivation master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65337;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65336;
				itemDef.anInt200 = 65336;
				itemDef.stackable = false;
				itemDef.description = "It's a Cultivation master cape.".getBytes();
				break;

			case 13220:
				itemDef.name = "Consumer master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65339;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65338;
				itemDef.anInt200 = 65338;
				itemDef.stackable = false;
				itemDef.description = "It's a Consumer master cape.".getBytes();
				break;

			case 13221:
				itemDef.name = "Safe haven master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65341;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65340;
				itemDef.anInt200 = 65340;
				itemDef.stackable = false;
				itemDef.description = "It's a Safe haven master cape.".getBytes();
				break;

			case 13222:
				itemDef.name = "Bear grills master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65343;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65342;
				itemDef.anInt200 = 65342;
				itemDef.stackable = false;
				itemDef.description = "It's a Bear grills master cape.".getBytes();
				break;

			case 13223:
				itemDef.name = "Pet master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65345;
				itemDef.modelZoom = 2479;
				itemDef.modelRotationY = 479;
				itemDef.modelRotationX = 1024;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 12;
				itemDef.anInt165 = 65344;
				itemDef.anInt200 = 65344;
				itemDef.stackable = false;
				itemDef.description = "It's a Pet master cape.".getBytes();
				break;

			case 11941:
				itemDef.name = "Mysterious Bag";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[4] = "Throw-away";
				itemDef.description = "It's a mysterious bag, magical powers can turn it to gold. But you dont have them..".getBytes();
				break;

			case 995:
				itemDef.name = "BestBucks";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[4] = "Throw-away";
				itemDef.itemActions[3] = "Add-to-debit-card";
				break;

			case 4155:
				itemDef.name = "Mercenary gem";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Check-task";
				break;

			case 2568:
				itemDef.itemActions[2] = "Check charges";
				break;

			case 13188:
				itemDef.name = "D claws";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's a pair of D-claws.".getBytes();
				break;

			case 5698:
				itemDef.name = "DDS";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's a famous DDS.".getBytes();
				break;

			case 9747:
				itemDef.name = "Assault cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Assault.".getBytes();
				break;

			case 9748:
				itemDef.name = "Assault cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Assault.".getBytes();
				break;

			case 9749:
				itemDef.name = "Assault hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Assault.".getBytes();
				break;

			case 9750:
				itemDef.name = "Vigour cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Vigour.".getBytes();
				break;

			case 9751:
				itemDef.name = "Vigour cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Vigour.".getBytes();
				break;

			case 9752:
				itemDef.name = "Vigour hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Vigour.".getBytes();
				break;

			case 9753:
				itemDef.name = "Aegis cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Aegis.".getBytes();
				break;

			case 9754:
				itemDef.name = "Aegis cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Aegis.".getBytes();
				break;

			case 9755:
				itemDef.name = "Aegis hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Aegis.".getBytes();
				break;

			case 9756:
				itemDef.name = "Sagittarius cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Sagittarius.".getBytes();
				break;

			case 9757:
				itemDef.name = "Sagittarius cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Sagittarius.".getBytes();
				break;

			case 9758:
				itemDef.name = "Sagittarius hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Sagittarius.".getBytes();
				break;

			case 9759:
				itemDef.name = "Necromance cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Necromance.".getBytes();
				break;

			case 9760:
				itemDef.name = "Necromance cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Necromance.".getBytes();
				break;

			case 9761:
				itemDef.name = "Necromance hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Necromance.".getBytes();
				break;

			case 9762:
				itemDef.name = "Mage cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Mage.".getBytes();
				break;

			case 9763:
				itemDef.name = "Mage cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Mage.".getBytes();
				break;

			case 9764:
				itemDef.name = "Mage hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Mage.".getBytes();
				break;

			case 9765:
				itemDef.name = "Consumer cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Consumer.".getBytes();
				break;

			case 9766:
				itemDef.name = "Consumer cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Consumer.".getBytes();
				break;

			case 9767:
				itemDef.name = "Consumer hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Consumer.".getBytes();
				break;

			case 9768:
				itemDef.name = "Life cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Life.".getBytes();
				break;

			case 9769:
				itemDef.name = "Life cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Life.".getBytes();
				break;

			case 9770:
				itemDef.name = "Life hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Life.".getBytes();
				break;

			case 9771:
				itemDef.name = "Weedsmoking cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Weedsmoking.".getBytes();
				break;

			case 9772:
				itemDef.name = "Weedsmoking cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Weedsmoking.".getBytes();
				break;

			case 9773:
				itemDef.name = "Weedsmoking hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Weedsmoking.".getBytes();
				break;

			case 9774:
				itemDef.name = "THC-hempistry cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of THC-hempistry.".getBytes();
				break;

			case 9775:
				itemDef.name = "THC-hempistry cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of THC-hempistry.".getBytes();
				break;

			case 9776:
				itemDef.name = "THC-hempistry hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of THC-hempistry.".getBytes();
				break;

			case 9777:
				itemDef.name = "Accomplisher cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Accomplisher.".getBytes();
				break;

			case 9778:
				itemDef.name = "Accomplisher cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Accomplisher.".getBytes();
				break;

			case 9779:
				itemDef.name = "Accomplisher hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Accomplisher.".getBytes();
				break;

			case 9780:
				itemDef.name = "Handiness cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Handiness.".getBytes();
				break;

			case 9781:
				itemDef.name = "Handiness cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Handiness.".getBytes();
				break;

			case 9782:
				itemDef.name = "Handiness hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Handiness.".getBytes();
				break;

			case 9783:
				itemDef.name = "Woodcarving cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Woodcarving.".getBytes();
				break;

			case 9784:
				itemDef.name = "Woodcarving cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Woodcarving.".getBytes();
				break;

			case 9785:
				itemDef.name = "Woodcarving hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Woodcarving.".getBytes();
				break;

			case 9786:
				itemDef.name = "Mercenary cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Mercenary.".getBytes();
				break;

			case 9787:
				itemDef.name = "Mercenary cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Mercenary.".getBytes();
				break;

			case 9788:
				itemDef.name = "Mercenary hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Mercenary.".getBytes();
				break;

			case 9789:
				itemDef.name = "Safehaven cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Safehaven.".getBytes();
				break;

			case 9790:
				itemDef.name = "Safehaven cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Safehaven.".getBytes();
				break;

			case 9791:
				itemDef.name = "Safehaven hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Safehaven.".getBytes();
				break;

			case 9792:
				itemDef.name = "Quarry cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Quarrying.".getBytes();
				break;

			case 9793:
				itemDef.name = "Quarry cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Quarrying.".getBytes();
				break;

			case 9794:
				itemDef.name = "Quarry hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Quarrying.".getBytes();
				break;

			case 9795:
				itemDef.name = "Forging cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Forging.".getBytes();
				break;

			case 9796:
				itemDef.name = "Forging cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Forging.".getBytes();
				break;

			case 9797:
				itemDef.name = "Forging hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Forging.".getBytes();
				break;

			case 9798:
				itemDef.name = "Fisher cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Fish.".getBytes();
				break;

			case 9799:
				itemDef.name = "Fisher cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Fish.".getBytes();
				break;

			case 9800:
				itemDef.name = "Fisher hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Fish.".getBytes();
				break;

			case 9801:
				itemDef.name = "Foodie cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Food.".getBytes();
				break;

			case 9802:
				itemDef.name = "Foodie cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Food.".getBytes();
				break;

			case 9803:
				itemDef.name = "Foodie hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Food.".getBytes();
				break;

			case 9804:
				itemDef.name = "Pyromaniac cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Pyromaniac.".getBytes();
				break;

			case 9805:
				itemDef.name = "Pyromaniac cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Pyromaniac.".getBytes();
				break;

			case 9806:
				itemDef.name = "Pyromaniac hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Pyromaniac.".getBytes();
				break;

			case 9807:
				itemDef.name = "Lumbering cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Lumbering.".getBytes();
				break;

			case 9808:
				itemDef.name = "Lumbering cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Lumbering.".getBytes();
				break;

			case 9809:
				itemDef.name = "Lumbering hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Lumbering.".getBytes();
				break;

			case 9810:
				itemDef.name = "Cultivation cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Cultivation.".getBytes();
				break;

			case 9811:
				itemDef.name = "Cultivation cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Cultivation.".getBytes();
				break;

			case 9812:
				itemDef.name = "Cultivation hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Cultivation.".getBytes();
				break;

			case 9813:
				itemDef.name = "Jah cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Jah jah cape.".getBytes();
				break;

			case 9814:
				itemDef.name = "Jah hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Jah jah hood.".getBytes();
				break;

			case 9948:
				itemDef.name = "Bear grills cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes();
				break;

			case 9949:
				itemDef.name = "Bear grills cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes();
				break;

			case 9950:
				itemDef.name = "Bear grills hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes();
				break;

			case 199:
				itemDef.name = "Untrimmed Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 249:
				itemDef.name = "Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Kush, 17% THC.".getBytes();
				break;

			case 91:
				itemDef.name = "Kush mix (x)";
				itemDef.description = "It's a 17% THC kush mix, waiting for its final component.".getBytes();
				break;

			case 5291:
				itemDef.name = "Kush seed";
				itemDef.description = "It's Kush seed with 17% THC.".getBytes();
				break;

			case 201:
				itemDef.name = "Untrimmed Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 251:
				itemDef.name = "Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Haze, 20% THC.".getBytes();
				break;

			case 93:
				itemDef.name = "Haze mix (x)";
				itemDef.description = "It's a 20% THC Haze mix, waiting for its final component.".getBytes();
				break;

			case 5292:
				itemDef.name = "Haze seed";
				itemDef.description = "It's Haze seed with 20% THC.".getBytes();
				break;

			case 203:
				itemDef.name = "Untrimmed OG Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 253:
				itemDef.name = "OG Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling OG Kush, 19% THC.".getBytes();
				break;

			case 95:
				itemDef.name = "OG Kush mix (x)";
				itemDef.description = "It's a 19% THC OG Kush mix, waiting for its final component.".getBytes();
				break;

			case 5293:
				itemDef.name = "OG Kush seed";
				itemDef.description = "It's OG Kush seed with 19% THC.".getBytes();
				break;

			case 205:
				itemDef.name = "Untrimmed Powerplant";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 255:
				itemDef.name = "Powerplant";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Powerplant, 17% THC.".getBytes();
				break;

			case 97:
				itemDef.name = "Powerplant mix (x)";
				itemDef.description = "It's a 17% THC Powerplant mix, waiting for its final component.".getBytes();
				break;

			case 5294:
				itemDef.name = "Powerplant seed";
				itemDef.description = "It's Powerplant seed with 17% THC.".getBytes();
				break;

			case 207:
				itemDef.name = "Untrimmed Cheese Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 257:
				itemDef.name = "Cheese Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Cheese Haze, 24% THC.".getBytes();
				break;

			case 99:
				itemDef.name = "Cheese Haze mix (x)";
				itemDef.description = "It's a 24% THC Cheese Haze mix, waiting for its final component.".getBytes();
				break;

			case 5295:
				itemDef.name = "Cheese Haze seed";
				itemDef.description = "It's Cheese Haze seed with 24% THC.".getBytes();
				break;

			case 3049:
				itemDef.name = "Untrimmed Bubba Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 2998:
				itemDef.name = "Bubba Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Bubba Kush, 20% THC.".getBytes();
				break;

			case 3002:
				itemDef.name = "Bubba Kush mix (x)";
				itemDef.description = "It's a 20% THC Bubba Kush mix, waiting for its final component.".getBytes();
				break;

			case 5296:
				itemDef.name = "Bubba Kush seed";
				itemDef.description = "It's Bubba Kush seed with 20% THC.".getBytes();
				break;

			case 209:
				itemDef.name = "Untrimmed Chocolope";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 259:
				itemDef.name = "Chocolope";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Chocolope, 22% THC.".getBytes();
				break;

			case 101:
				itemDef.name = "Chocolope mix (x)";
				itemDef.description = "It's a 22% THC Chocolope mix, waiting for its final component.".getBytes();
				break;

			case 5297:
				itemDef.name = "Chocolope seed";
				itemDef.description = "It's Chocolope seed with 22% THC.".getBytes();
				break;

			case 211:
				itemDef.name = "Untrimmed Gorilla Glue";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 261:
				itemDef.name = "Gorilla Glue";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Gorilla Glue, 26% THC.".getBytes();
				break;

			case 103:
				itemDef.name = "Gorilla Glue mix (x)";
				itemDef.description = "It's a 26% THC Gorilla Glue mix, waiting for its final component.".getBytes();
				break;

			case 5298:
				itemDef.name = "Gorilla Glue seed";
				itemDef.description = "It's Gorilla Glue seed with 26% THC.".getBytes();
				break;

			case 213:
				itemDef.name = "Untrimmed Jack Herer";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 263:
				itemDef.name = "Jack Herer";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Jack Herer, 17% THC.".getBytes();
				break;

			case 105:
				itemDef.name = "Jack Herer mix (x)";
				itemDef.description = "It's a 24% THC Jack Herer mix, waiting for its final component.".getBytes();
				break;

			case 5299:
				itemDef.name = "Jack Herer seed";
				itemDef.description = "It's Jack Herer seed with 24% THC.".getBytes();
				break;

			case 3051:
				itemDef.name = "Untrimmed Durban Poison";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 3000:
				itemDef.name = "Durban Poison";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Durban Poison, 23% THC.".getBytes();
				break;

			case 3004:
				itemDef.name = "Durban Poison mix (x)";
				itemDef.description = "It's a 23% THC Durban Poison mix, waiting for its final component.".getBytes();
				break;

			case 5300:
				itemDef.name = "Durban Poison seed";
				itemDef.description = "It's Durban Poison seed with 23% THC.".getBytes();
				break;

			case 215:
				itemDef.name = "Untrimmed Amnesia";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 265:
				itemDef.name = "Amnesia";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Amnesia, 21% THC.".getBytes();
				break;

			case 107:
				itemDef.name = "Amnesia mix (x)";
				itemDef.description = "It's a 21% THC Amnesia mix, waiting for its final component.".getBytes();
				break;

			case 5301:
				itemDef.name = "Amnesia seed";
				itemDef.description = "It's Amnesia seed with 21% THC.".getBytes();
				break;

			case 2485:
				itemDef.name = "Untrimmed Super Silver Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 2481:
				itemDef.name = "Super Silver Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Super Silver Haze, 27% THC.".getBytes();
				break;

			case 2483:
				itemDef.name = "Super Silver Haze mix (x)";
				itemDef.description = "It's a 27% THC Super Silver Haze mix, waiting for its final component."
					.getBytes();
				break;

			case 5302:
				itemDef.name = "Super Silver Haze seed";
				itemDef.description = "It's Super Silver Haze seed with 27% THC.".getBytes();
				break;

			case 217:
				itemDef.name = "Untrimmed Girl Scout Cookies";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 267:
				itemDef.name = "Girl Scout Cookies";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Girl Scout Cookies, 29.5% THC.".getBytes();
				break;

			case 109:
				itemDef.name = "Girl Scout Cookies mix (x)";
				itemDef.description = "It's a 29.5% THC Girl Scout Cookies mix, waiting for its final component."
					.getBytes();
				break;

			case 5303:
				itemDef.name = "Girl Scout Cookies seed";
				itemDef.description = "It's Girl Scout Cookies seed with 29.5% THC.".getBytes();
				break;

			case 219:
				itemDef.name = "Untrimmed Khalifa Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes();
				break;

			case 269:
				itemDef.name = "Khalifa Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Khalifa Kush, 31% THC.".getBytes();
				break;

			case 111:
				itemDef.name = "Khalifa Kush mix (x)";
				itemDef.description = "It's a 31% THC Khalifa Kush mix, waiting for its final component.".getBytes();
				break;

			case 5304:
				itemDef.name = "Khalifa Kush seed";
				itemDef.description = "It's Khalifa Kush seed with 31% THC.".getBytes();
				break;

			case 6036:
				itemDef.name = "PK 13-14";
				itemDef.description = "Powerful flowering nutritions.".getBytes();
				break;

			case 6037:
				itemDef.name = "PK 13-14";
				itemDef.description = "Powerful flowering nutritions.".getBytes();
				break;

			case 1785:
				itemDef.name = "Weed pipe";
				itemDef.description = "A pipe used to smoke cannabis.".getBytes();
				break;

			case 1511:
				itemDef.name = "Wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.itemActions[1] = "Hold";
				itemDef.description = "It's wood.".getBytes();
				break;

			case 1512:
				itemDef.name = "Wood";
				itemDef.description = "It's wood.".getBytes();
				break;

			case 1513:
				itemDef.name = "Mages wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Mages wood.".getBytes();
				break;

			case 1514:
				itemDef.name = "Mages wood";
				itemDef.description = "It's Mages wood.".getBytes();
				break;

			case 1515:
				itemDef.name = "Yew wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Yew wood.".getBytes();
				break;

			case 1516:
				itemDef.name = "Yew wood";
				itemDef.description = "It's Yew wood.".getBytes();
				break;

			case 1517:
				itemDef.name = "Maple wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Maple wood.".getBytes();
				break;

			case 1518:
				itemDef.name = "Maple wood";

				itemDef.description = "It's Maple wood.".getBytes();
				break;

			case 1519:
				itemDef.name = "Willow wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Willow wood.".getBytes();
				break;

			case 1521:
				itemDef.name = "Oak wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Oak wood.".getBytes();
				break;

			case 1522:
				itemDef.name = "Oak wood";
				itemDef.description = "It's Oak wood.".getBytes();
				break;

			case 1725:
				itemDef.name = "Amulet of vigour";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's an amulet of vigour.".getBytes();
				break;

			case 1726:
				itemDef.name = "Amulet of vigour";
				itemDef.description = "It's an amulet of vigour.".getBytes();
				break;

			case 167:
				itemDef.name = "Power of the aegis (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 168:
				itemDef.name = "Power of the aegis (1)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 165:
				itemDef.name = "Power of the aegis (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 166:
				itemDef.name = "Power of the aegis (2)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 163:
				itemDef.name = "Power of the aegis (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 164:
				itemDef.name = "Power of the aegis (3)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 2442:
				itemDef.name = "Power of the aegis (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 2443:
				itemDef.name = "Power of the aegis (4)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes();
				break;

			case 161:
				itemDef.name = "Power of vigour(1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 162:
				itemDef.name = "Power of vigour(1)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 159:
				itemDef.name = "Power of vigour(2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 160:
				itemDef.name = "Power of vigour (2)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 157:
				itemDef.name = "Power of vigour(3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 158:
				itemDef.name = "Power of vigour (3)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 2440:
				itemDef.name = "Power of vigour(4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 2441:
				itemDef.name = "Power of vigour (4)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes();
				break;

			case 149:
				itemDef.name = "Power of assault (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 150:
				itemDef.name = "Power of assault (1)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 147:
				itemDef.name = "Power of assault(2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 148:
				itemDef.name = "Power of assault (2)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 145:
				itemDef.name = "Power of assault(3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 146:
				itemDef.name = "Power of assault (3)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 2436:
				itemDef.name = "Power of assault(4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 2437:
				itemDef.name = "Power of assault (4)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes();
				break;

			case 3046:
				itemDef.name = "Mages elixir (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3047:
				itemDef.name = "Mages elixir (1)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3044:
				itemDef.name = "Mages elixir (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3045:
				itemDef.name = "Mages elixir (2)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3042:
				itemDef.name = "Mages elixir (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3043:
				itemDef.name = "Mages elixir (3)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3040:
				itemDef.name = "Mages elixir (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 3041:
				itemDef.name = "Mages elixir (4)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes();
				break;

			case 173:
				itemDef.name = "Power of the sagittarius (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 174:
				itemDef.name = "Power of the sagittarius (1)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 171:
				itemDef.name = "Power of the sagittarius (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 172:
				itemDef.name = "Power of the sagittarius (2)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 169:
				itemDef.name = "Power of the sagittarius (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 170:
				itemDef.name = "Power of the sagittarius (3)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 2444:
				itemDef.name = "Power of the sagittarius (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 2445:
				itemDef.name = "Power of the sagittarius (4)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes();
				break;

			case 143:
				itemDef.name = "Power of the necromancer (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 144:
				itemDef.name = "Power of the necromancer (1)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 141:
				itemDef.name = "Power of the necromancer (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 142:
				itemDef.name = "Power of the necromancer (2)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 139:
				itemDef.name = "Power of the necromancer (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 140:
				itemDef.name = "Power of the necromancer (3)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 2434:
				itemDef.name = "Power of the necromancer (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 2435:
				itemDef.name = "Power of the necromancer (4)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 125:
				itemDef.name = "Power of assault (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 126:
				itemDef.name = "Power of assault (1)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 123:
				itemDef.name = "Power of assault (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 124:
				itemDef.name = "Power of assault (2)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 121:
				itemDef.name = "Power of assault (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 122:
				itemDef.name = "Power of assault (3)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 2428:
				itemDef.name = "Power of assault (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 2429:
				itemDef.name = "Power of assault (4)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes();
				break;

			case 179:
				itemDef.name = "Cannabidiol Concentrate (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(1).".getBytes();
				break;

			case 180:
				itemDef.name = "Cannabidiol Concentrate (1)";
				itemDef.description = "It's an Cannabidiol Concentrate(1).".getBytes();
				break;

			case 177:
				itemDef.name = "Cannabidiol Concentrate (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(2).".getBytes();
				break;

			case 178:
				itemDef.name = "Cannabidiol Concentrate (2)";
				itemDef.description = "It's an Cannabidiol Concentrate(2).".getBytes();
				break;

			case 175:
				itemDef.name = "Cannabidiol Concentrate (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(3).".getBytes();
				break;

			case 176:
				itemDef.name = "Cannabidiol Concentrate (3)";
				itemDef.description = "It's an Cannabidiol Concentrate(3).".getBytes();
				break;

			case 2446:
				itemDef.name = "Cannabidiol Concentrate (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(4).".getBytes();
				break;

			case 2447:
				itemDef.name = "Cannabidiol Concentrate (4)";
				itemDef.description = "It's an Cannabidiol Concentrate(4).".getBytes();
				break;

			case 119:
				itemDef.name = "Vigour potion (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 120:
				itemDef.name = "Vigour potion (1)";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 117:
				itemDef.name = "Vigour potion (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 118:
				itemDef.name = "Vigour potion (2)";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 115:
				itemDef.name = "Vigour potion (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 116:
				itemDef.name = "Vigour potion (3)";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 113:
				itemDef.name = "Vigour potion (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 114:
				itemDef.name = "Vigour potion (4)";
				itemDef.description = "It's a potion to channel vigour.".getBytes();
				break;

			case 131:
				itemDef.name = "Steroid (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 132:
				itemDef.name = "Steroid (1)";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 129:
				itemDef.name = "Steroid (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 130:
				itemDef.name = "Steroid (2)";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 127:
				itemDef.name = "Steroid (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 128:
				itemDef.name = "Steroid (3)";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 2430:
				itemDef.name = "Steroid (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 2431:
				itemDef.name = "Steroid (4)";
				itemDef.description = "Fucking Frank White...".getBytes();
				break;

			case 3014:
				itemDef.name = "Usain Bolt's Doping (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3015:
				itemDef.name = "Usain Bolt's Doping (1)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3012:
				itemDef.name = "Usain Bolt's Doping (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3013:
				itemDef.name = "Usain Bolt's Doping (2)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3010:
				itemDef.name = "Usain Bolt's Doping (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3011:
				itemDef.name = "Usain Bolt's Doping (3)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3008:
				itemDef.name = "Usain Bolt's Doping (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 3009:
				itemDef.name = "Usain Bolt's Doping (4)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes();
				break;

			case 12701:
				itemDef.name = "Jah Powa (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12702:
				itemDef.name = "Jah Powa (1)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12699:
				itemDef.name = "Jah Powa (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12700:
				itemDef.name = "Jah Powa (2)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12697:
				itemDef.name = "Jah Powa (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12698:
				itemDef.name = "Jah Powa (3)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12695:
				itemDef.name = "Jah Powa (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 12696:
				itemDef.name = "Jah Powa (4)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes();
				break;

			case 3030:
				itemDef.name = "Steroid v2 (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3031:
				itemDef.name = "Steroid v2 (1)";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3028:
				itemDef.name = "Steroid v2 (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3029:
				itemDef.name = "Steroid v2 (2)";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3026:
				itemDef.name = "Steroid v2 (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3027:
				itemDef.name = "Steroid v2 (3)";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3024:
				itemDef.name = "Steroid v2 (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 3025:
				itemDef.name = "Steroid v2 (4)";
				itemDef.description = "Bloody Russia.".getBytes();
				break;

			case 155:
				itemDef.name = "Fish oil extract (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 156:
				itemDef.name = "Fish oil extract (1)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 153:
				itemDef.name = "Fish oil extract (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 154:
				itemDef.name = "Fish oil extract (2)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 151:
				itemDef.name = "Fish oil extract (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 152:
				itemDef.name = "Fish oil extract (3)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 2438:
				itemDef.name = "Fish oil extract (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 2439:
				itemDef.name = "Fish oil extract (4)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes();
				break;

			case 2458:
				itemDef.name = "Resist Brisingr (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2459:
				itemDef.name = "Resist Brisingr (1)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2456:
				itemDef.name = "Resist Brisingr (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2457:
				itemDef.name = "Resist Brisingr (2)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2454:
				itemDef.name = "Resist Brisingr (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2455:
				itemDef.name = "Resist Brisingr (3)";
				itemDef.description = "This This drink makes you brisingr resistant.".getBytes();
				break;

			case 2452:
				itemDef.name = "Resist Brisingr (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 2453:
				itemDef.name = "Resist Brisingr (4)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes();
				break;

			case 137:
				itemDef.name = "Aegis call (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 138:
				itemDef.name = "Aegis call (1)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 135:
				itemDef.name = "Aegis call (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 136:
				itemDef.name = "Aegis call (2)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 133:
				itemDef.name = "Aegis call (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 134:
				itemDef.name = "Aegis call (3)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 2432:
				itemDef.name = "Aegis call (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 2433:
				itemDef.name = "Aegis call (4)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes();
				break;

			case 6691:
				itemDef.name = "Healing ale of the gods (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6692:
				itemDef.name = "Healing ale of the gods (1)";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6689:
				itemDef.name = "Healing ale of the gods (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6690:
				itemDef.name = "Healing ale of the gods (2)";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6687:
				itemDef.name = "Healing ale of the gods (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6688:
				itemDef.name = "Healing ale of the gods (3)";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6685:
				itemDef.name = "Healing ale of the gods (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 6686:
				itemDef.name = "Healing ale of the gods (4)";
				itemDef.description = "Godlike ale made for healing.".getBytes();
				break;

			case 12690:
			case 12962:
			case 12972:
			case 12974:
			case 12984:
			case 12986:
			case 12988:
			case 12990:
			case 13000:
			case 13002:
			case 13012:
			case 13014:
			case 13024:
			case 13026:
			case 9666:
			case 9670:
			case 12865:
			case 12867:
			case 12869:
			case 12871:
			case 12966:
			case 12964:
			case 12968:
			case 12970:
			case 12976:
			case 12978:
			case 12980:
			case 12982:
			case 12992:
			case 12994:
			case 12996:
			case 12998:
			case 13004:
			case 13006:
			case 13008:
			case 13010:
			case 13016:
			case 13018:
			case 13020:
			case 13022:
			case 13028:
			case 13030:
			case 13032:
			case 13034:
			case 13036:
			case 13038:
			case 12960:
			case 13173:
			case 13175:
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6828:
				itemDef.name = "Armour set 1";
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6829:
				itemDef.name = "Armour set 2";
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6830:
				itemDef.name = "Armour set 3";
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6831:
				itemDef.name = "Armour set 4";
				itemDef.itemActions[0] = "Unpack";
				break;
		}

		if (itemDef.certTemplateID != -1)
			itemDef.toNote();
		return itemDef;
	}

	public static void nullLoader() {
		mruNodes2 = null;
		mruNodes1 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void unpackConfig(StreamLoader archive) {
		stream = new Stream(archive.getDataForName("obj.dat"));
		Stream stream = new Stream(archive.getDataForName("obj.idx"));
		totalItems = stream.readUnsignedWord() + 21;
		System.out.println("Items Loaded: " + totalItems);
		streamIndices = new int[totalItems + 50000];
		int i = 2;
		for (int j = 0; j < totalItems - 21; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}

		cache = new ItemDef[10];
		for (int k = 0; k < 10; k++)
			cache[k] = new ItemDef();

	}

	public static Sprite getSprite(int id, int size, int color, int zoom) {
		if (id == 0) {
			return Client.cacheSprite[326];
		}
		ItemDef item = getItemDefinition(id);
		if (item.stackIDs == null) {
			size = -1;
		}
		if (size > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++) {
				if (size >= item.stackAmounts[j1] && item.stackAmounts[j1] != 0) {
					i1 = item.stackIDs[j1];
				}
			}
			if (i1 != -1) {
				item = getItemDefinition(i1);
			}
		}
		Model model = item.getStackedModel(1);
		if (model == null)
			return null;
		Sprite image = new Sprite(32, 32);
		int k1 = Rasterizer.centerX;
		int l1 = Rasterizer.centerY;
		int[] ai = Rasterizer.anIntArray1472;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.aBoolean1464 = false;
		DrawingArea.initDrawingArea(32, 32, image.myPixels, new float[32 * 32]);
		DrawingArea.drawPixels(32, 0, 0, 0, 32);
		Rasterizer.method364();
		int itemZoom = item.modelZoom * zoom - 500;
		int l3 = Rasterizer.anIntArray1470[item.modelRotationY] * itemZoom >> 16;
		int i4 = Rasterizer.anIntArray1471[item.modelRotationY] * itemZoom >> 16;
		model.method482(item.modelRotationX, item.anInt204, item.modelRotationY, item.modelOffset1,
				l3 + model.modelHeight / 2 + item.modelOffset2, i4 + item.modelOffset2);
		if (color == 0) {
			for (int index = 31; index >= 0; index--) {
				for (int index2 = 31; index2 >= 0; index2--)
					if (image.myPixels[index + index2 * 32] == 0 && index > 0 && index2 > 0
							&& image.myPixels[(index - 1) + (index2 - 1) * 32] > 0)
						image.myPixels[index + index2 * 32] = 0x302020;
			}
		}
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.centerX = k1;
		Rasterizer.centerY = l1;
		Rasterizer.anIntArray1472 = ai;
		Rasterizer.aBoolean1464 = true;
		if (item.stackable) {
			image.cropWidth = 33;
		} else {
			image.cropWidth = 32;
		}
		image.anInt1445 = size;

		return image;
	}

	public static Sprite getSprite(int i, int j, int k) {
		if (k == 0) {
			Sprite sprite = (Sprite) mruNodes1.insertFromCache(i);
			if (sprite != null && sprite.anInt1445 != j && sprite.anInt1445 != -1) {

				sprite.unlink();
				sprite = null;
			}
			if (sprite != null) {
				return sprite;
			}
		}
		ItemDef itemDef = getItemDefinition(i);
		if (itemDef.stackIDs == null)
			j = -1;
		if (j > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++)
				if (j >= itemDef.stackAmounts[j1]
						&& itemDef.stackAmounts[j1] != 0)
					i1 = itemDef.stackIDs[j1];

			if (i1 != -1)
				itemDef = getItemDefinition(i1);
		}
		Model model = itemDef.getStackedModel(1);
		if (model == null)
			return null;
		Sprite sprite = null;
		if (itemDef.certTemplateID != -1) {
			sprite = getSprite(itemDef.certID, 10, -1);
			if (sprite == null)
				return null;
		}
		Sprite enabledSprite = new Sprite(32, 32);
		int k1 = Rasterizer.centerX;
		int l1 = Rasterizer.centerY;
		int[] ai = Rasterizer.anIntArray1472;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.aBoolean1464 = false;
		DrawingArea.initDrawingArea(32, 32, enabledSprite.myPixels, new float[32 * 32]);
		DrawingArea.method336(32, 0, 0, 0, 32);
		Rasterizer.method364();
		int k3 = itemDef.modelZoom;
		if (k == -1)
			k3 = (int) ((double) k3 * 1.5D);
		if (k > 0)
			k3 = (int) ((double) k3 * 1.04D);
		int l3 = Rasterizer.anIntArray1470[itemDef.modelRotationY] * k3 >> 16;
		int i4 = Rasterizer.anIntArray1471[itemDef.modelRotationY] * k3 >> 16;
		model.method482(itemDef.modelRotationX, itemDef.anInt204,
				itemDef.modelRotationY, itemDef.modelOffset1, l3
						+ model.modelHeight / 2 + itemDef.modelOffset2,
				i4
						+ itemDef.modelOffset2);
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--)
				if (enabledSprite.myPixels[i5 + j4 * 32] == 0)
					if (i5 > 0
							&& enabledSprite.myPixels[(i5 - 1) + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 > 0
							&& enabledSprite.myPixels[i5 + (j4 - 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (i5 < 31
							&& enabledSprite.myPixels[i5 + 1 + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 < 31
							&& enabledSprite.myPixels[i5 + (j4 + 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;

		}

		if (k > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--)
					if (enabledSprite.myPixels[j5 + k4 * 32] == 0)
						if (j5 > 0
								&& enabledSprite.myPixels[(j5 - 1) + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 > 0
								&& enabledSprite.myPixels[j5 + (k4 - 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (j5 < 31
								&& enabledSprite.myPixels[j5 + 1 + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 < 31
								&& enabledSprite.myPixels[j5 + (k4 + 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;

			}

		} else if (k == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--)
					if (enabledSprite.myPixels[k5 + l4 * 32] == 0
							&& k5 > 0
							&& l4 > 0
							&& enabledSprite.myPixels[(k5 - 1) + (l4 - 1) * 32] > 0)
						enabledSprite.myPixels[k5 + l4 * 32] = 0x302020;

			}

		}
		if (itemDef.certTemplateID != -1) {
			int l5 = sprite.cropWidth;
			int j6 = sprite.anInt1445;
			sprite.cropWidth = 32;
			sprite.anInt1445 = 32;
			sprite.drawSprite(0, 0);
			sprite.cropWidth = l5;
			sprite.anInt1445 = j6;
		}
		if (k == 0)
			mruNodes1.removeFromCache(enabledSprite, i);
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.centerX = k1;
		Rasterizer.centerY = l1;
		Rasterizer.anIntArray1472 = ai;
		Rasterizer.aBoolean1464 = true;
		if (itemDef.stackable)
			enabledSprite.cropWidth = 33;
		else
			enabledSprite.cropWidth = 32;
		enabledSprite.anInt1445 = j;
		return enabledSprite;
	}

	public boolean method192(int j) {
		int k = anInt175;
		int l = anInt166;
		if (j == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return true;
		boolean flag = Model.method463(k);
		if (l != -1 && !Model.method463(l))
			flag = false;
		return flag;
	}

	public Model getWornModel(int gender) {
		int k = anInt175;
		int l = anInt166;
		if (gender == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return null;
		Model model = Model.loadModelFromCache(k);
		if (l != -1) {
			Model model_1 = Model.loadModelFromCache(l);
			Model[] aclass30_sub2_sub4_sub6s = { model, model_1 };
			model = new Model(2, aclass30_sub2_sub4_sub6s);
		}
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				model.replaceColor(modifiedModelColors[i1],
						originalModelColors[i1]);

		}
		return model;
	}

	public boolean method195(int j) {
		int k = anInt165;
		int l = anInt188;
		int i1 = anInt185;
		if (j == 1) {
			k = anInt200;
			l = anInt164;
			i1 = anInt162;
		}
		if (k == -1)
			return true;
		boolean flag = Model.method463(k);
		if (l != -1 && !Model.method463(l))
			flag = false;
		if (i1 != -1 && !Model.method463(i1))
			flag = false;
		return flag;
	}

	public Model getInventoryModel(int gender) {
		int j = anInt165;
		int k = anInt188;
		int l = anInt185;
		if (gender == 1) {
			j = anInt200;
			k = anInt164;
			l = anInt162;
		}
		if (j == -1)
			return null;
		Model model = Model.loadModelFromCache(j);
		if (k != -1)
			if (l != -1) {
				Model model_1 = Model.loadModelFromCache(k);
				Model model_3 = Model.loadModelFromCache(l);
				Model[] aclass30_sub2_sub4_sub6_1s = { model, model_1, model_3 };
				model = new Model(3, aclass30_sub2_sub4_sub6_1s);
			} else {
				Model model_2 = Model.loadModelFromCache(k);
				Model[] aclass30_sub2_sub4_sub6s = { model, model_2 };
				model = new Model(2, aclass30_sub2_sub4_sub6s);
			}
		if (gender == 0 && aByte205 != 0)
			model.translateCoords(0, aByte205, 0);
		if (gender == 1 && aByte154 != 0)
			model.translateCoords(0, aByte154, 0);
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				model.replaceColor(modifiedModelColors[i1], originalModelColors[i1]);

		}
		return model;
	}

	private void setDefaults() {
		modelID = 0;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		modelZoom = 2000;
		modelRotationY = 0;
		modelRotationX = 0;
		anInt204 = 0;
		modelOffset1 = 0;
		modelOffset2 = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundActions = null;
		itemActions = null;
		equipActions = new String[6];
		anInt165 = -1;
		anInt188 = -1;
		aByte205 = 0;
		anInt200 = -1;
		anInt164 = -1;
		aByte154 = 0;
		anInt185 = -1;
		anInt162 = -1;
		anInt175 = -1;
		anInt166 = -1;
		anInt197 = -1;
		anInt173 = -1;
		stackIDs = null;
		stackAmounts = null;
		certID = -1;
		certTemplateID = -1;
		anInt167 = 128;
		anInt192 = 128;
		anInt191 = 128;
		anInt196 = 0;
		anInt184 = 0;
		team = 0;
	}

	private void toNote() {
		ItemDef itemDef = getItemDefinition(certTemplateID);
		modelID = itemDef.modelID;
		modelZoom = itemDef.modelZoom;
		modelRotationY = itemDef.modelRotationY;
		modelRotationX = itemDef.modelRotationX;

		anInt204 = itemDef.anInt204;
		modelOffset1 = itemDef.modelOffset1;
		modelOffset2 = itemDef.modelOffset2;
		modifiedModelColors = itemDef.modifiedModelColors;
		originalModelColors = itemDef.originalModelColors;
		ItemDef itemDef_1 = getItemDefinition(certID);
		name = itemDef_1.name;
		membersObject = itemDef_1.membersObject;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
			s = "an";
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".").getBytes();
		stackable = true;
	}

	public Model getStackedModel(int stackSize) {
		if (stackIDs != null && stackSize > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (stackSize >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).getStackedModel(1);
		}
		Model model = (Model) mruNodes2.insertFromCache(id);
		if (model != null)
			return model;
		model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;
		if (anInt167 != 128 || anInt192 != 128 || anInt191 != 128)
			model.modelScale(anInt167, anInt191, anInt192);
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);

		}
		model.applyLighting(64 + anInt196, 768 + anInt184, -50, -10, -50, true);
		model.aBoolean1659 = true;
		mruNodes2.removeFromCache(model, id);
		return model;
	}

	public Model method202(int i) {
		if (stackIDs != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (i >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).method202(1);
		}
		Model model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);

		}
		return model;
	}

	private void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1)
				modelID = stream.readUnsignedWord();
			else if (i == 2)
				name = stream.readString();
			else if (i == 3)
				description = stream.readBytes();
			else if (i == 4)
				modelZoom = stream.readUnsignedWord();
			else if (i == 5)
				modelRotationY = stream.readUnsignedWord();
			else if (i == 6)
				modelRotationX = stream.readUnsignedWord();
			else if (i == 7) {
				modelOffset1 = stream.readUnsignedWord();
				if (modelOffset1 > 32767)
					modelOffset1 -= 0x10000;
			} else if (i == 8) {
				modelOffset2 = stream.readUnsignedWord();
				if (modelOffset2 > 32767)
					modelOffset2 -= 0x10000;
			} else if (i == 10)
				stream.readUnsignedWord();
			else if (i == 11)
				stackable = true;
			else if (i == 12)
				value = stream.readDWord();
			else if (i == 16)
				membersObject = true;
			else if (i == 23) {
				anInt165 = stream.readUnsignedWord();
				aByte205 = stream.readSignedByte();
			} else if (i == 24)
				anInt188 = stream.readUnsignedWord();
			else if (i == 25) {
				anInt200 = stream.readUnsignedWord();
				aByte154 = stream.readSignedByte();
			} else if (i == 26)
				anInt164 = stream.readUnsignedWord();
			else if (i >= 30 && i < 35) {
				if (groundActions == null)
					groundActions = new String[5];
				groundActions[i - 30] = stream.readString();
				if (groundActions[i - 30].equalsIgnoreCase("hidden"))
					groundActions[i - 30] = null;
			} else if (i >= 35 && i < 40) {
				if (itemActions == null)
					itemActions = new String[5];
				itemActions[i - 35] = stream.readString();
			} else if (i == 40) {
				int j = stream.readUnsignedByte();
				originalModelColors = new int[j];
				modifiedModelColors = new int[j];
				for (int k = 0; k < j; k++) {
					originalModelColors[k] = stream.readUnsignedWord();
					modifiedModelColors[k] = stream.readUnsignedWord();
				}

			} else if (i == 78)
				anInt185 = stream.readUnsignedWord();
			else if (i == 79)
				anInt162 = stream.readUnsignedWord();
			else if (i == 90)
				anInt175 = stream.readUnsignedWord();
			else if (i == 91)
				anInt197 = stream.readUnsignedWord();
			else if (i == 92)
				anInt166 = stream.readUnsignedWord();
			else if (i == 93)
				anInt173 = stream.readUnsignedWord();
			else if (i == 95)
				anInt204 = stream.readUnsignedWord();
			else if (i == 97)
				certID = stream.readUnsignedWord();
			else if (i == 98)
				certTemplateID = stream.readUnsignedWord();
			else if (i == 100) {
				int length = stream.readUnsignedByte();
				stackIDs = new int[length];
				stackAmounts = new int[length];
				for (int i2 = 0; i2 < length; i2++) {
					stackIDs[i2] = stream.readUnsignedWord();
					stackAmounts[i2] = stream.readUnsignedWord();
				}
			} else if (i == 110)
				anInt167 = stream.readUnsignedWord();
			else if (i == 111)
				anInt192 = stream.readUnsignedWord();
			else if (i == 112)
				anInt191 = stream.readUnsignedWord();
			else if (i == 113)
				anInt196 = stream.readSignedByte();
			else if (i == 114)
				anInt184 = stream.readSignedByte() * 5;
			else if (i == 115)
				team = stream.readUnsignedByte();
		} while (true);
	}

}
