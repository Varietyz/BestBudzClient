
public final class ItemDef {

	public static ItemDef forID(int i) {
		for (int j = 0; j < 10; j++)
			if (cache[j].id == i)
				return cache[j];

		cacheIndex = (cacheIndex + 1) % 10;
		ItemDef itemDef = cache[cacheIndex];
		stream.currentOffset = streamIndices[i];
		itemDef.id = i;
		itemDef.setDefaults();
		itemDef.readValues(stream);
		if (!isMembers && itemDef.membersObject) {
			itemDef.name = "Members Object";
			itemDef.description = "Login to a members' server to use this object.".getBytes();
			itemDef.groundActions = null;
			itemDef.itemActions = null;
			itemDef.team = 0;

		}

		/* Customs items */
		switch (i) {

			case 13190:
				itemDef.name = "1,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13191:
				itemDef.name = "3,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13192:
				itemDef.name = "5,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13193:
				itemDef.name = "8,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

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
				break;// JayBane

			case 13195:
				itemDef.name = "20,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13196:
				itemDef.name = "50,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13197:
				itemDef.name = "100,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			case 13198:
				itemDef.name = "200,000 CannaCredits voucher";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Redeem";
				itemDef.modelID = 29210;
				itemDef.modelZoom = 2300;
				itemDef.modelRotationY = 512;
				itemDef.modelOffset1 = 3;
				itemDef.modelOffset2 = 1;
				break;// JayBane

			//// TOOL RING
			case 6575:
				itemDef.name = "Tool Ring";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A ring that transforms into any tool when worn.".getBytes(); // Description
				break;// JayBane

			//// FISH NECKLACE
			case 6577:
				itemDef.name = "Fisher Necklace";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A necklace to passively attract and feed all kinds of fish."
						.getBytes(); // Description
				break;// JayBane

			//// FISH BONES
			case 6904:
				itemDef.name = "Fish bones";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Feed";
				itemDef.description = "The best part of the fish!"
						.getBytes(); // Description
				break;// JayBane

			//// Robin hood boots
			case 2577:
				itemDef.name = "Robin hood boots";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Yep, robin's boots."
						.getBytes(); // Description
				break;// JayBane

			case 9005: // Rainbow socks
				itemDef.name = "Rainbow socks";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Try on";
				itemDef.description = "Yep, robin's boots."
						.getBytes(); // Description
				break;// JayBane

			/// MASTERCAPES BY JAYBANE
			case 13200:
				itemDef.name = "Assault master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65299; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65298;// maleWieldModel
				itemDef.anInt200 = 65298;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Assault master cape.".getBytes(); // Description
				break;// JayBane

			case 13201:
				itemDef.name = "Aegis master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65301; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65300;// maleWieldModel
				itemDef.anInt200 = 65300;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Aegis master cape.".getBytes(); // Description
				break;// JayBane

			case 13202:
				itemDef.name = "Vigour master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65303; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65302;// maleWieldModel
				itemDef.anInt200 = 65302;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Stength master cape.".getBytes(); // Description
				break;// JayBane

			case 13203:
				itemDef.name = "Life master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[2] = "Drop";
				itemDef.modelID = 65305; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65304;// maleWieldModel
				itemDef.anInt200 = 65304;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Life master cape.".getBytes(); // Description
				break;// JayBane

			case 13204:
				itemDef.name = "Sagittarius master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65307; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65306;// maleWieldModel
				itemDef.anInt200 = 65306;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Sagittarius master cape.".getBytes(); // Description
				break;// JayBane

			case 13205:
				itemDef.name = "Necromance master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65309; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65308;// maleWieldModel
				itemDef.anInt200 = 65308;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Necromance master cape.".getBytes(); // Description
				break;// JayBane

			case 13206:
				itemDef.name = "Mage master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65311; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65310;// maleWieldModel
				itemDef.anInt200 = 65310;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Mage master cape.".getBytes(); // Description
				break;// JayBane

			case 13207:
				itemDef.name = "Foodie master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65313; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65312;// maleWieldModel
				itemDef.anInt200 = 65312;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Foodie master cape.".getBytes(); // Description
				break;// JayBane

			case 13208:
				itemDef.name = "Lumbering master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65315; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65314;// maleWieldModel
				itemDef.anInt200 = 65314;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Lumbering master cape.".getBytes(); // Description
				break;// JayBane

			case 13209:
				itemDef.name = "Woodcarving master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65317; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65316;// maleWieldModel
				itemDef.anInt200 = 65316;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Woodcarving master cape.".getBytes(); // Description
				break;// JayBane

			case 13210:
				itemDef.name = "Fisher master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65319; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65318;// maleWieldModel
				itemDef.anInt200 = 65318;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Fisher master cape.".getBytes(); // Description
				break;// JayBane

			case 13211:
				itemDef.name = "Pyromaniac master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65321; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65320;// maleWieldModel
				itemDef.anInt200 = 65320;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Pyromaniac master cape.".getBytes(); // Description
				break;// JayBane

			case 13212:
				itemDef.name = "Handiness master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65323; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65322;// maleWieldModel
				itemDef.anInt200 = 65322;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Handiness master cape.".getBytes(); // Description
				break;// JayBane

			case 13213:
				itemDef.name = "Forging master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65325; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65324;// maleWieldModel
				itemDef.anInt200 = 65324;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Forging master cape.".getBytes(); // Description
				break;// JayBane

			case 13214:
				itemDef.name = "Quarrying master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65327; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65326;// maleWieldModel
				itemDef.anInt200 = 65326;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Quarrying master cape.".getBytes(); // Description
				break;// JayBane

			case 13215:
				itemDef.name = "THC-hempistry master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65329; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65328;// maleWieldModel
				itemDef.anInt200 = 65328;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a THC-hempistry master cape.".getBytes(); // Description
				break;// JayBane

			case 13216:
				itemDef.name = "Weedsmoking master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65331; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65330;// maleWieldModel
				itemDef.anInt200 = 65330;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Weedsmoking master cape.".getBytes(); // Description
				break;// JayBane

			case 13217:
				itemDef.name = "Accomplisher master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65333; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65332;// maleWieldModel
				itemDef.anInt200 = 65332;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Accomplisher master cape.".getBytes(); // Description
				break;// JayBane

			case 13218:
				itemDef.name = "Mercenary master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65335; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65334;// maleWieldModel
				itemDef.anInt200 = 65334;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Mercenary master cape.".getBytes(); // Description
				break;// JayBane

			case 13219:
				itemDef.name = "Cultivation master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65337; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65336;// maleWieldModel
				itemDef.anInt200 = 65336;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Cultivation master cape.".getBytes(); // Description
				break;// JayBane

			case 13220:
				itemDef.name = "Consumer master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65339; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65338;// maleWieldModel
				itemDef.anInt200 = 65338;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Consumer master cape.".getBytes(); // Description
				break;// JayBane

			case 13221:
				itemDef.name = "Safe haven master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65341; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65340;// maleWieldModel
				itemDef.anInt200 = 65340;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Safe haven master cape.".getBytes(); // Description
				break;// JayBane

			case 13222:
				itemDef.name = "Bear grills master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65343; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65342;// maleWieldModel
				itemDef.anInt200 = 65342;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Bear grills master cape.".getBytes(); // Description
				break;// JayBane

			case 13223:
				itemDef.name = "Pet master cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.itemActions[4] = "Drop";
				itemDef.modelID = 65345; // groundid
				itemDef.modelZoom = 2479; // 2479
				itemDef.modelRotationY = 479; // 279 579
				itemDef.modelRotationX = 1024; // 948
				itemDef.modelOffset1 = 3; // -3
				itemDef.modelOffset2 = 12; // 24
				itemDef.anInt165 = 65344;// maleWieldModel
				itemDef.anInt200 = 65344;// femWieldModel
				itemDef.stackable = false;
				itemDef.description = "It's a Pet master cape.".getBytes(); // Description
				break;// JayBane

			////// END OF MASTERCAPES

			case 11941:// Lootbag
				itemDef.name = "Mysterious Bag";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[4] = "Throw-away";
				itemDef.description = "It's a mysterious bag, magical powers can turn it to gold.".getBytes(); // Description
				break;// JayBane

			case 995:// Money
				itemDef.name = "BestBucks";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[4] = "Throw-away";
				itemDef.itemActions[3] = "Add-to-debit-card";
				break;// JayBane

			case 4155:// Gem
				itemDef.name = "Mercenary gem";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Check-task";
				break;// JayBane

			case 2568:// Ring of forging
				itemDef.itemActions[2] = "Check charges";
				break;// JayBane

			case 13188:// Dragon claws
				itemDef.name = "Dragon claws";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's a pair of D-claws.".getBytes(); // Description
				break;// JayBane

			case 5698:// Dragon Dagger P++
				itemDef.name = "DDS";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's a famous DDS.".getBytes(); // Description
				break;// JayBane

			case 9747:// Att. Cape
				itemDef.name = "Assault cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Assault.".getBytes(); // Description
				break;// JayBane

			case 9748:// Att. Cape (t)
				itemDef.name = "Assault cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Assault.".getBytes(); // Description
				break;// JayBane

			case 9749:// Att. hood
				itemDef.name = "Assault hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Assault.".getBytes(); // Description
				break;// JayBane

			case 9750:// str. Cape
				itemDef.name = "Vigour cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Vigour.".getBytes(); // Description
				break;// JayBane

			case 9751:// str. Cape (t)
				itemDef.name = "Vigour cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Vigour.".getBytes(); // Description
				break;// JayBane

			case 9752:// str. hood
				itemDef.name = "Vigour hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Vigour.".getBytes(); // Description
				break;// JayBane

			case 9753:// def. Cape
				itemDef.name = "Aegis cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Aegis.".getBytes(); // Description
				break;// JayBane

			case 9754:// def. Cape (t)
				itemDef.name = "Aegis cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Aegis.".getBytes(); // Description
				break;// JayBane

			case 9755:// def. hood
				itemDef.name = "Aegis hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Aegis.".getBytes(); // Description
				break;// JayBane

			case 9756:// ranged Cape
				itemDef.name = "Sagittarius cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Sagittarius.".getBytes(); // Description
				break;// JayBane

			case 9757:// ranged Cape (t)
				itemDef.name = "Sagittarius cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Sagittarius.".getBytes(); // Description
				break;// JayBane

			case 9758:// ranged hood
				itemDef.name = "Sagittarius hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Sagittarius.".getBytes(); // Description
				break;// JayBane

			case 9759:// Pray. Cape
				itemDef.name = "Necromance cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Necromance.".getBytes(); // Description
				break;// JayBane

			case 9760:// Pray. Cape (t)
				itemDef.name = "Necromance cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Necromance.".getBytes(); // Description
				break;// JayBane

			case 9761:// Pray. hood
				itemDef.name = "Necromance hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Necromance.".getBytes(); // Description
				break;// JayBane

			case 9762:// Magic Cape
				itemDef.name = "Mage cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Mage.".getBytes(); // Description
				break;// JayBane

			case 9763:// Magic Cape (t)
				itemDef.name = "Mage cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Mage.".getBytes(); // Description
				break;// JayBane

			case 9764:// Magic hood
				itemDef.name = "Mage hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Mage.".getBytes(); // Description
				break;// JayBane

			case 9765:// Runecraft. cape
				itemDef.name = "Consumer cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Consumer.".getBytes(); // Description
				break;// JayBane

			case 9766:// Runecraft. Cape (t)
				itemDef.name = "Consumer cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Consumer.".getBytes(); // Description
				break;// JayBane

			case 9767:// Runecraft. hood
				itemDef.name = "Consumer hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Consumer.".getBytes(); // Description
				break;// JayBane

			case 9768:// Hitp. cape
				itemDef.name = "Life cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Life.".getBytes(); // Description
				break;// JayBane

			case 9769:// Hitp. Cape (t)
				itemDef.name = "Life cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Life.".getBytes(); // Description
				break;// JayBane

			case 9770:// Hitp. hood
				itemDef.name = "Life hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Life.".getBytes(); // Description
				break;// JayBane

			case 9771:// Agil. cape
				itemDef.name = "Weedsmoking cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Weedsmoking.".getBytes(); // Description
				break;// JayBane

			case 9772:// Agil. Cape (t)
				itemDef.name = "Weedsmoking cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Weedsmoking.".getBytes(); // Description
				break;// JayBane

			case 9773:// Agil. hood
				itemDef.name = "Weedsmoking hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Weedsmoking.".getBytes(); // Description
				break;// JayBane

			case 9774:// Herb. cape
				itemDef.name = "THC-hempistry cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of THC-hempistry.".getBytes(); // Description
				break;// JayBane

			case 9775:// Herb. Cape (t)
				itemDef.name = "THC-hempistry cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of THC-hempistry.".getBytes(); // Description
				break;// JayBane

			case 9776:// Herb. hood
				itemDef.name = "THC-hempistry hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of THC-hempistry.".getBytes(); // Description
				break;// JayBane

			case 9777:// Thief. cape
				itemDef.name = "Accomplisher cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Accomplisher.".getBytes(); // Description
				break;// JayBane

			case 9778:// Thief. Cape (t)
				itemDef.name = "Accomplisher cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Accomplisher.".getBytes(); // Description
				break;// JayBane

			case 9779:// Thief. hood
				itemDef.name = "Accomplisher hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Accomplisher.".getBytes(); // Description
				break;// JayBane

			case 9780:// Craft. cape
				itemDef.name = "Handiness cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Handiness.".getBytes(); // Description
				break;// JayBane

			case 9781:// Craft. Cape (t)
				itemDef.name = "Handiness cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Handiness.".getBytes(); // Description
				break;// JayBane

			case 9782:// Craft. hood
				itemDef.name = "Handiness hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Handiness.".getBytes(); // Description
				break;// JayBane

			case 9783:// fletch. cape
				itemDef.name = "Woodcarving cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Woodcarving.".getBytes(); // Description
				break;// JayBane

			case 9784:// fletch. Cape (t)
				itemDef.name = "Woodcarving cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Woodcarving.".getBytes(); // Description
				break;// JayBane

			case 9785:// fletch. hood
				itemDef.name = "Woodcarving hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Woodcarving.".getBytes(); // Description
				break;// JayBane

			case 9786:// Slay. cape
				itemDef.name = "Mercenary cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Mercenary.".getBytes(); // Description
				break;// JayBane

			case 9787:// Slay. Cape (t)
				itemDef.name = "Mercenary cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Mercenary.".getBytes(); // Description
				break;// JayBane

			case 9788:// Slay. hood
				itemDef.name = "Mercenary hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Mercenary.".getBytes(); // Description
				break;// JayBane

			case 9789:// Cons. cape
				itemDef.name = "Safehaven cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Safehaven.".getBytes(); // Description
				break;// JayBane

			case 9790:// cons. Cape (t)
				itemDef.name = "Safehaven cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Safehaven.".getBytes(); // Description
				break;// JayBane

			case 9791:// cons. hood
				itemDef.name = "Safehaven hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Safehaven.".getBytes(); // Description
				break;// JayBane

			case 9792:// Mine. cape
				itemDef.name = "Quarry cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Quarrying.".getBytes(); // Description
				break;// JayBane

			case 9793:// Mine. Cape (t)
				itemDef.name = "Quarry cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Quarrying.".getBytes(); // Description
				break;// JayBane

			case 9794:// Mine. hood
				itemDef.name = "Quarry hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Quarrying.".getBytes(); // Description
				break;// JayBane

			case 9795:// Smith. cape
				itemDef.name = "Forging cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Forging.".getBytes(); // Description
				break;// JayBane

			case 9796:// Smith. Cape (t)
				itemDef.name = "Forging cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Forging.".getBytes(); // Description
				break;// JayBane

			case 9797:// Smith. hood
				itemDef.name = "Forging hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Forging.".getBytes(); // Description
				break;// JayBane

			case 9798:// Fish. cape
				itemDef.name = "Fisher cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Fish.".getBytes(); // Description
				break;// JayBane

			case 9799:// Fish. Cape (t)
				itemDef.name = "Fisher cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Fish.".getBytes(); // Description
				break;// JayBane

			case 9800:// Fish. hood
				itemDef.name = "Fisher hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Fish.".getBytes(); // Description
				break;// JayBane

			case 9801:// Cook. cape
				itemDef.name = "Foodie cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Food.".getBytes(); // Description
				break;// JayBane

			case 9802:// Cook. Cape (t)
				itemDef.name = "Foodie cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Food.".getBytes(); // Description
				break;// JayBane

			case 9803:// Cook. hood
				itemDef.name = "Foodie hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Food.".getBytes(); // Description
				break;// JayBane

			case 9804:// Firem. cape
				itemDef.name = "Pyromaniac cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Pyromaniac.".getBytes(); // Description
				break;// JayBane

			case 9805:// Firem. Cape (t)
				itemDef.name = "Pyromaniac cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Pyromaniac.".getBytes(); // Description
				break;// JayBane

			case 9806:// Firem. hood
				itemDef.name = "Pyromaniac hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Pyromaniac.".getBytes(); // Description
				break;// JayBane

			case 9807:// woodcut. cape
				itemDef.name = "Lumbering cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Lumbering.".getBytes(); // Description
				break;// JayBane

			case 9808:// woodcut. Cape (t)
				itemDef.name = "Lumbering cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Lumbering.".getBytes(); // Description
				break;// JayBane

			case 9809:// woodcut. hood
				itemDef.name = "Lumbering hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Lumbering.".getBytes(); // Description
				break;// JayBane

			case 9810:// farm. cape
				itemDef.name = "Cultivation cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist cape of Cultivation.".getBytes(); // Description
				break;// JayBane

			case 9811:// farm. Cape (t)
				itemDef.name = "Cultivation cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist silk cape of Cultivation.".getBytes(); // Description
				break;// JayBane

			case 9812:// farm. hood
				itemDef.name = "Cultivation hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "A professionist hood of Cultivation.".getBytes(); // Description
				break;// JayBane

			case 9813:// quest. cape
				itemDef.name = "Jah cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Jah jah cape.".getBytes(); // Description
				break;// JayBane

			case 9814:// quest. hood
				itemDef.name = "Jah hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Jah jah hood.".getBytes(); // Description
				break;// JayBane

			case 9948:// hunt. cape
				itemDef.name = "Bear grills cape";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes(); // Description
				break;// JayBane

			case 9949:// hunt. Cape (t)
				itemDef.name = "Bear grills cape (s)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes(); // Description
				break;// JayBane

			case 9950:// hunt. hood
				itemDef.name = "Bear grills hood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "Bear grills cape.".getBytes(); // Description
				break;// JayBane

			case 199:// Untrimmed Guam
				itemDef.name = "Untrimmed Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 249:// Guam
				itemDef.name = "Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Kush, 17% THC.".getBytes(); // Description
				break;// JayBane

			case 91:// Guam Unf potion
				itemDef.name = "Kush mix (x)";
				itemDef.description = "It's a 17% THC kush mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5291:// Guam seed
				itemDef.name = "Kush seed";
				itemDef.description = "It's Kush seed with 17% THC.".getBytes(); // Description
				break;// JayBane

			case 201:// Untrimmed MARRENTILL
				itemDef.name = "Untrimmed Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 251:// MARRENTILL
				itemDef.name = "Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Haze, 20% THC.".getBytes(); // Description
				break;// JayBane

			case 93:// MARRENTILL Unf potion
				itemDef.name = "Haze mix (x)";
				itemDef.description = "It's a 20% THC Haze mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5292:// MARRENTILL seed
				itemDef.name = "Haze seed";
				itemDef.description = "It's Haze seed with 20% THC.".getBytes(); // Description
				break;// JayBane

			case 203:// Untrimmed TARROMIN
				itemDef.name = "Untrimmed OG Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 253:// TARROMIN
				itemDef.name = "OG Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling OG Kush, 19% THC.".getBytes(); // Description
				break;// JayBane

			case 95:// TARROMIN Unf potion
				itemDef.name = "OG Kush mix (x)";
				itemDef.description = "It's a 19% THC OG Kush mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5293:// TARROMIN seed
				itemDef.name = "OG Kush seed";
				itemDef.description = "It's OG Kush seed with 19% THC.".getBytes(); // Description
				break;// JayBane

			case 205:// Untrimmed HARRALANDER
				itemDef.name = "Untrimmed Powerplant";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 255:// HARRALANDER
				itemDef.name = "Powerplant";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Powerplant, 17% THC.".getBytes(); // Description
				break;// JayBane

			case 97:// HARRALANDER Unf potion
				itemDef.name = "Powerplant mix (x)";
				itemDef.description = "It's a 17% THC Powerplant mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5294:// HARRALANDER seed
				itemDef.name = "Powerplant seed";
				itemDef.description = "It's Powerplant seed with 17% THC.".getBytes(); // Description
				break;// JayBane

			case 207:// Untrimmed RANARR
				itemDef.name = "Untrimmed Cheese Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 257:// RANARR
				itemDef.name = "Cheese Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Cheese Haze, 24% THC.".getBytes(); // Description
				break;// JayBane

			case 99:// RANARR Unf potion
				itemDef.name = "Cheese Haze mix (x)";
				itemDef.description = "It's a 24% THC Cheese Haze mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5295:// RANARR seed
				itemDef.name = "Cheese Haze seed";
				itemDef.description = "It's Cheese Haze seed with 24% THC.".getBytes(); // Description
				break;// JayBane

			case 3049:// Untrimmed TOADFLAX
				itemDef.name = "Untrimmed Bubba Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 2998:// TOADFLAX
				itemDef.name = "Bubba Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Bubba Kush, 20% THC.".getBytes(); // Description
				break;// JayBane

			case 3002:// TOADFLAX Unf potion
				itemDef.name = "Bubba Kush mix (x)";
				itemDef.description = "It's a 20% THC Bubba Kush mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5296:// TOADFLAX seed
				itemDef.name = "Bubba Kush seed";
				itemDef.description = "It's Bubba Kush seed with 20% THC.".getBytes(); // Description
				break;// JayBane

			case 209:// Untrimmed IRIT
				itemDef.name = "Untrimmed Chocolope";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 259:// IRIT
				itemDef.name = "Chocolope";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Chocolope, 22% THC.".getBytes(); // Description
				break;// JayBane

			case 101:// IRIT Unf potion
				itemDef.name = "Chocolope mix (x)";
				itemDef.description = "It's a 22% THC Chocolope mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5297:// IRIT seed
				itemDef.name = "Chocolope seed";
				itemDef.description = "It's Chocolope seed with 22% THC.".getBytes(); // Description
				break;// JayBane

			case 211:// Untrimmed AVANTOE
				itemDef.name = "Untrimmed Gorilla Glue";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 261:// AVANTOE
				itemDef.name = "Gorilla Glue";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Gorilla Glue, 26% THC.".getBytes(); // Description
				break;// JayBane

			case 103:// AVANTOE Unf potion
				itemDef.name = "Gorilla Glue mix (x)";
				itemDef.description = "It's a 26% THC Gorilla Glue mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5298:// AVANTOE seed
				itemDef.name = "Gorilla Glue seed";
				itemDef.description = "It's Gorilla Glue seed with 26% THC.".getBytes(); // Description
				break;// JayBane

			case 213:// Untrimmed KWUARM
				itemDef.name = "Untrimmed Jack Herer";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 263:// KWUARM
				itemDef.name = "Jack Herer";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Jack Herer, 17% THC.".getBytes(); // Description
				break;// JayBane

			case 105:// KWUARM Unf potion
				itemDef.name = "Jack Herer mix (x)";
				itemDef.description = "It's a 24% THC Jack Herer mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5299:// KWUARM seed
				itemDef.name = "Jack Herer seed";
				itemDef.description = "It's Jack Herer seed with 24% THC.".getBytes(); // Description
				break;// JayBane

			case 3051:// Untrimmed SNAPDRAGON
				itemDef.name = "Untrimmed Durban Poison";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 3000:// SNAPDRAGON
				itemDef.name = "Durban Poison";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Durban Poison, 23% THC.".getBytes(); // Description
				break;// JayBane

			case 3004:// SNAPDRAGON Unf potion
				itemDef.name = "Durban Poison mix (x)";
				itemDef.description = "It's a 23% THC Durban Poison mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5300:// SNAPDRAGON seed
				itemDef.name = "Durban Poison seed";
				itemDef.description = "It's Durban Poison seed with 23% THC.".getBytes(); // Description
				break;// JayBane

			case 215:// Untrimmed CADANTINE
				itemDef.name = "Untrimmed Amnesia";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 265:// CADANTINE
				itemDef.name = "Amnesia";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Amnesia, 21% THC.".getBytes(); // Description
				break;// JayBane

			case 107:// CADANTINE Unf potion
				itemDef.name = "Amnesia mix (x)";
				itemDef.description = "It's a 21% THC Amnesia mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5301:// CADANTINE seed
				itemDef.name = "Amnesia seed";
				itemDef.description = "It's Amnesia seed with 21% THC.".getBytes(); // Description
				break;// JayBane

			case 2485:// Untrimmed LANTADYME
				itemDef.name = "Untrimmed Super Silver Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 2481:// LANTADYME
				itemDef.name = "Super Silver Haze";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Super Silver Haze, 27% THC.".getBytes(); // Description
				break;// JayBane

			case 2483:// LANTADYME Unf potion
				itemDef.name = "Super Silver Haze mix (x)";
				itemDef.description = "It's a 27% THC Super Silver Haze mix, waiting for its final component."
						.getBytes(); // Description
				break;// JayBane

			case 5302:// LANTADYME seed
				itemDef.name = "Super Silver Haze seed";
				itemDef.description = "It's Super Silver Haze seed with 27% THC.".getBytes(); // Description
				break;// JayBane

			case 217:// Untrimmed DWARFWEED
				itemDef.name = "Untrimmed Girl Scout Cookies";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 267:// DWARFWEED
				itemDef.name = "Girl Scout Cookies";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Girl Scout Cookies, 29.5% THC.".getBytes(); // Description
				break;// JayBane

			case 109:// DWARFWEED Unf potion
				itemDef.name = "Girl Scout Cookies mix (x)";
				itemDef.description = "It's a 29.5% THC Girl Scout Cookies mix, waiting for its final component."
						.getBytes(); // Description
				break;// JayBane

			case 5303:// DWARFWEED seed
				itemDef.name = "Girl Scout Cookies seed";
				itemDef.description = "It's Girl Scout Cookies seed with 29.5% THC.".getBytes(); // Description
				break;// JayBane

			case 219:// Untrimmed TORSTOL
				itemDef.name = "Untrimmed Khalifa Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Trim";
				itemDef.description = "These are some wild looking buds.".getBytes(); // Description
				break;// JayBane

			case 269:// TORSTOL
				itemDef.name = "Khalifa Kush";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Smoke";
				itemDef.description = "It's some bombass smelling Khalifa Kush, 31% THC.".getBytes(); // Description
				break;// JayBane

			case 111:// TORSTOL Unf potion
				itemDef.name = "Khalifa Kush mix (x)";
				itemDef.description = "It's a 31% THC Khalifa Kush mix, waiting for its final component.".getBytes(); // Description
				break;// JayBane

			case 5304:// TORSTOL seed
				itemDef.name = "Khalifa Kush seed";
				itemDef.description = "It's Khalifa Kush seed with 31% THC.".getBytes(); // Description
				break;// JayBane

			case 6036:// Plant cure
				itemDef.name = "PK 13-14";
				itemDef.description = "Powerful flowering nutritions.".getBytes(); // Description
				break;// JayBane

			case 6037:// Plant cure
				itemDef.name = "PK 13-14";
				itemDef.description = "Powerful flowering nutritions.".getBytes(); // Description
				break;// JayBane

			case 1785://glass blowing pipe
				itemDef.name = "Weed pipe";
				itemDef.description = "A pipe used to smoke cannabis.".getBytes(); // Description
				break;// JayBane

			case 1511:// Logs
				itemDef.name = "Wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.itemActions[1] = "Hold";
				itemDef.description = "It's wood.".getBytes(); // Description
				break;// JayBane

			case 1512:// Logs
				itemDef.name = "Wood";
				itemDef.description = "It's wood.".getBytes(); // Description
				break;// JayBane

			case 1513:// Mage Logs
				itemDef.name = "Mages wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Mages wood.".getBytes(); // Description
				break;// JayBane

			case 1514:// Mage Logs
				itemDef.name = "Mages wood";
				itemDef.description = "It's Mages wood.".getBytes(); // Description
				break;// JayBane

			case 1515:// Yew Logs
				itemDef.name = "Yew wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Yew wood.".getBytes(); // Description
				break;// JayBane

			case 1516:// Yew Logs
				itemDef.name = "Yew wood";
				itemDef.description = "It's Yew wood.".getBytes(); // Description
				break;// JayBane

			case 1517:// Maple Logs
				itemDef.name = "Maple wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Maple wood.".getBytes(); // Description
				break;// JayBane

			case 1518:// Maple Logs
				itemDef.name = "Maple wood";
			
				itemDef.description = "It's Maple wood.".getBytes(); // Description
				break;// JayBane

			case 1519:// Willow Logs
				itemDef.name = "Willow wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Willow wood.".getBytes(); // Description
				break;// JayBane

			case 1521:// Willow Logs
				itemDef.name = "Oak wood";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Burn";
				itemDef.description = "It's Oak wood.".getBytes(); // Description
				break;// JayBane

			case 1522:// Maple Logs
				itemDef.name = "Oak wood";
				itemDef.description = "It's Oak wood.".getBytes(); // Description
				break;// JayBane

			case 1725:// Amulet of strength
				itemDef.name = "Amulet of vigour";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[1] = "Wear";
				itemDef.description = "It's an amulet of vigour.".getBytes(); // Description
				break; // JayBane

			case 1726:// Amulet of strength
				itemDef.name = "Amulet of vigour";
				itemDef.description = "It's an amulet of vigour.".getBytes(); // Description
				break; // JayBane

			case 167:// Super defence (1)
				itemDef.name = "Power of the aegis (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 168:// Super defence (1)
				itemDef.name = "Power of the aegis (1)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 165:// Super defence (2)
				itemDef.name = "Power of the aegis (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 166:// Super defence (2)
				itemDef.name = "Power of the aegis (2)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 163:// Super defence (3)
				itemDef.name = "Power of the aegis (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 164:// Super defence (3)
				itemDef.name = "Power of the aegis (3)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 2442:// Super defence (4)
				itemDef.name = "Power of the aegis (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 2443:// Super defence (4)
				itemDef.name = "Power of the aegis (4)";
				itemDef.description = "A powerfull potion imbued by the Aegis.".getBytes(); // Description
				break; // JayBane

			case 161:// Super strength (1)
				itemDef.name = "Power of vigour(1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 162:// Super strength (1)
				itemDef.name = "Power of vigour(1)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 159:// Super strength (2)
				itemDef.name = "Power of vigour(2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 160:// Super strength (2)
				itemDef.name = "Power of vigour (2)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 157:// Super strength (3)
				itemDef.name = "Power of vigour(3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 158:// Super strength (3)
				itemDef.name = "Power of vigour (3)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 2440:// Super strength (4)
				itemDef.name = "Power of vigour(4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 2441:// Super strength (4)
				itemDef.name = "Power of vigour (4)";
				itemDef.description = "A powerfull potion imbued to channel Vigour.".getBytes(); // Description
				break; // Drenth

			case 149:// Super attack (1)
				itemDef.name = "Power of assault (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 150:// Super attack (1)
				itemDef.name = "Power of assault (1)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 147:// Super attack (2)
				itemDef.name = "Power of assault(2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 148:// Super attack (2)
				itemDef.name = "Power of assault (2)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 145:// Super attack (3)
				itemDef.name = "Power of assault(3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 146:// Super attack (3)
				itemDef.name = "Power of assault (3)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 2436:// Super attack (4)
				itemDef.name = "Power of assault(4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 2437:// Super attack (4)
				itemDef.name = "Power of assault (4)";
				itemDef.description = "A powerfull potion imbued to make u more Assaulting.".getBytes(); // Description
				break; // Drenth

			case 3046:// Magic potion (1)
				itemDef.name = "Mages elixir (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3047:// Magic potion (1)
				itemDef.name = "Mages elixir (1)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3044:// Magic potion (2)
				itemDef.name = "Mages elixir (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3045:// Magic potion (2)
				itemDef.name = "Mages elixir (2)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3042:// Magic potion (3)
				itemDef.name = "Mages elixir (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3043:// Magic potion (3)
				itemDef.name = "Mages elixir (3)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3040:// Magic potion (4)
				itemDef.name = "Mages elixir (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 3041:// Magic potion (4)
				itemDef.name = "Mages elixir (4)";
				itemDef.description = "Elixir to boost the magical powers in your veins.".getBytes(); // Description
				break; // Drenth

			case 173:// Power of the sagittarius (1)
				itemDef.name = "Power of the sagittarius (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 174:// Power of the sagittarius (1)
				itemDef.name = "Power of the sagittarius (1)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 171:// Power of the sagittarius (2)
				itemDef.name = "Power of the sagittarius (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 172:// Power of the sagittarius (2)
				itemDef.name = "Power of the sagittarius (2)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 169:// Power of the sagittarius (3)
				itemDef.name = "Power of the sagittarius (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 170:// Power of the sagittarius (3)
				itemDef.name = "Power of the sagittarius (3)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 2444:// Power of the sagittarius (4)
				itemDef.name = "Power of the sagittarius (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 2445:// Power of the sagittarius (4)
				itemDef.name = "Power of the sagittarius (4)";
				itemDef.description = "It's a potion made by the sagittarius.".getBytes(); // Description
				break; // Drenth

			case 143:// Prayer potion potion (1)
				itemDef.name = "Power of the necromancer (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 144:// Prayer potion (1)
				itemDef.name = "Power of the necromancer (1)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 141:// Prayer potion (2)
				itemDef.name = "Power of the necromancer (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 142:// Prayer potion (2)
				itemDef.name = "Power of the necromancer (2)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 139:// Prayer potion (3)
				itemDef.name = "Power of the necromancer (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 140:// Prayer potion (3)
				itemDef.name = "Power of the necromancer (3)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 2434:// Prayer potion (4)
				itemDef.name = "Power of the necromancer (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 2435:// Prayer potion (4)
				itemDef.name = "Power of the necromancer (4)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 125:// Attack potion (1)
				itemDef.name = "Power of assault (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 126:// Attack potion (1)
				itemDef.name = "Power of assault (1)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 123:// Attack potion (2)
				itemDef.name = "Power of assault (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 124:// Attack potion (2)
				itemDef.name = "Power of assault (2)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 121:// Attack potion (3)
				itemDef.name = "Power of assault (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 122:// Attack potion (3)
				itemDef.name = "Power of assault (3)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 2428:// Attack potion (4)
				itemDef.name = "Power of assault (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 2429:// Attack potion (4)
				itemDef.name = "Power of assault (4)";
				itemDef.description = "It's a potion made by the necromancers.".getBytes(); // Description
				break; // Drenth

			case 179:// Cannabidiol Concentrate  (1)
				itemDef.name = "Cannabidiol Concentrate (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(1).".getBytes(); // Description
				break; // Drenth

			case 180:// Cannabidiol Concentrate  (1)
				itemDef.name = "Cannabidiol Concentrate (1)";
				itemDef.description = "It's an Cannabidiol Concentrate(1).".getBytes(); // Description
				break; // Drenth

			case 177:// Cannabidiol Concentrate  (2)
				itemDef.name = "Cannabidiol Concentrate (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(2).".getBytes(); // Description
				break; // Drenth

			case 178:// Cannabidiol Concentrate  (2)
				itemDef.name = "Cannabidiol Concentrate (2)";
				itemDef.description = "It's an Cannabidiol Concentrate(2).".getBytes(); // Description
				break; // Drenth

			case 175:// Cannabidiol Concentrate  (3)
				itemDef.name = "Cannabidiol Concentrate (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(3).".getBytes(); // Description
				break; // Drenth

			case 176:// Cannabidiol Concentrate  (3)
				itemDef.name = "Cannabidiol Concentrate (3)";
				itemDef.description = "It's an Cannabidiol Concentrate(3).".getBytes(); // Description
				break; // Drenth

			case 2446:// Cannabidiol Concentrate  (4)
				itemDef.name = "Cannabidiol Concentrate (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Cure with";
				itemDef.description = "it's an Cannabidiol Concentrate(4).".getBytes(); // Description
				break; // Drenth

			case 2447:// Cannabidiol Concentrate  (4)
				itemDef.name = "Cannabidiol Concentrate (4)";
				itemDef.description = "It's an Cannabidiol Concentrate(4).".getBytes(); // Description
				break; // Drenth

			case 119:// Strength potion (1)
				itemDef.name = "Vigour potion (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 120:// Strength potion (1)
				itemDef.name = "Vigour potion (1)";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 117:// Strength potion (2)
				itemDef.name = "Vigour potion (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 118:// Strength potion (2)
				itemDef.name = "Vigour potion (2)";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 115:// Strength potion (3)
				itemDef.name = "Vigour potion (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 116:// Strength potion (3)
				itemDef.name = "Vigour potion (3)";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 113:// Strength potion (4)
				itemDef.name = "Vigour potion (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 114:// Strength potion (4)
				itemDef.name = "Vigour potion (4)";
				itemDef.description = "It's a potion to channel vigour.".getBytes(); // Description
				break; // Drenth

			case 131:// Restore potion (1)
				itemDef.name = "Steroid (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 132:// Restore potion (1)
				itemDef.name = "Steroid (1)";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 129:// Restore potion (2)
				itemDef.name = "Steroid (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 130:// Restore potion (2)
				itemDef.name = "Steroid (2)";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 127:// Restore potion (3)
				itemDef.name = "Steroid (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 128:// Restore potion (3)
				itemDef.name = "Steroid (3)";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 2430:// Restore potion (4)
				itemDef.name = "Steroid (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 2431:// Restore potion (4)
				itemDef.name = "Steroid (4)";
				itemDef.description = "Fucking Frank White...".getBytes(); // Description
				break; // Drenth

			case 3014:// Energy potion (1)
				itemDef.name = "Usain Bolt's Doping (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3015:// Energy potion (1)
				itemDef.name = "Usain Bolt's Doping (1)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3012:// Energy potion (2)
				itemDef.name = "Usain Bolt's Doping (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3013:// Energy potion (2)
				itemDef.name = "Usain Bolt's Doping (2)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3010:// Energy potion (3)
				itemDef.name = "Usain Bolt's Doping (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3011:// Energy potion (3)
				itemDef.name = "Usain Bolt's Doping (3)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 3008:// Energy potion (4)
				itemDef.name = "Usain Bolt's Doping (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drent

			case 3009:// Energy potion (4)
				itemDef.name = "Usain Bolt's Doping (4)";
				itemDef.description = "Run as long as Usain Bolt!.".getBytes(); // Description
				break; // Drenth

			case 12701:// Jah Powa(1)
				itemDef.name = "Jah Powa (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12702:// Jah Powa (1)
				itemDef.name = "Jah Powa (1)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12699:// Jah Powa(2)
				itemDef.name = "Jah Powa (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12700:// Jah Powa (2)
				itemDef.name = "Jah Powa (2)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12697:// Jah Powa(3)
				itemDef.name = "Jah Powa (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12698:// Jah Powa (3)
				itemDef.name = "Jah Powa (3)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12695:// Jah Powa(4)
				itemDef.name = "Jah Powa (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 12696:// Jah Powa (4)
				itemDef.name = "Jah Powa (4)";
				itemDef.description = "Inheret the King Selassie Jah Jah powers.".getBytes(); // Description
				break; // Drenth/Jaybane

			case 3030:// Steroid v2 (1)
				itemDef.name = "Steroid v2 (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3031:// Steroid v2(1)
				itemDef.name = "Steroid v2 (1)";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3028:// Steroid v2 (2)
				itemDef.name = "Steroid v2 (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3029:// Steroid v2(2)
				itemDef.name = "Steroid v2 (2)";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3026:// Steroid v2 (3)
				itemDef.name = "Steroid v2 (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3027:// Steroid v2(3)
				itemDef.name = "Steroid v2 (3)";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3024:// Steroid v2 (4)
				itemDef.name = "Steroid v2 (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 3025:// Steroid v2(4)
				itemDef.name = "Steroid v2 (4)";
				itemDef.description = "Bloody Russia.".getBytes(); // Description
				break; // Drenth

			case 155:// Fishing potion (1)
				itemDef.name = "Fish oil extract (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 156:// Fishing potion(1)
				itemDef.name = "Fish oil extract (1)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 153:// Fishing potion (2)
				itemDef.name = "Fish oil extract (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 154:// Fishing potion(2)
				itemDef.name = "Fish oil extract (2)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 151:// Fishing potion (3)
				itemDef.name = "Fish oil extract (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 152:// Fishing potion(3)
				itemDef.name = "Fish oil extract (3)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 2438:// Fishing potion (4)
				itemDef.name = "Fish oil extract (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 2439:// Fishing potion(4)
				itemDef.name = "Fish oil extract (4)";
				itemDef.description = "This extract buffs your fishing skills.".getBytes(); // Description
				break; // Drenth

			case 2458:// Resist Brisingr (1)
				itemDef.name = "Resist Brisingr (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2459:// Resist Brisingr(1)
				itemDef.name = "Resist Brisingr (1)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2456:// Resist Brisingr (2)
				itemDef.name = "Resist Brisingr (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2457:// Resist Brisingr(2)
				itemDef.name = "Resist Brisingr (2)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2454:// Resist Brisingr (3)
				itemDef.name = "Resist Brisingr (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2455:// Resist Brisingr(3)
				itemDef.name = "Resist Brisingr (3)";
				itemDef.description = "This This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2452:// Resist Brisingr (4)
				itemDef.name = "Resist Brisingr (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 2453:// Resist Brisingr(4)
				itemDef.name = "Resist Brisingr (4)";
				itemDef.description = "This drink makes you brisingr resistant.".getBytes(); // Description
				break; // Drenth

			case 137:// Defence potion (1)
				itemDef.name = "Aegis call (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 138:// Defence potion(1)
				itemDef.name = "Aegis call (1)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 135:// Defence potion (2)
				itemDef.name = "Aegis call (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 136:// Defence potion(2)
				itemDef.name = "Aegis call (2)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 133:// Defence potion (3)
				itemDef.name = "Aegis call (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 134:// Defence potion(3)
				itemDef.name = "Aegis call (3)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 2432:// Defence potion (4)
				itemDef.name = "Aegis call (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 2433:// Defence potion(4)
				itemDef.name = "Aegis call (4)";
				itemDef.description = "It's a light potion made by the aegis.".getBytes(); // Description
				break; // Drenth

			case 6691:// Healing ale of the gods (1)
				itemDef.name = "Healing ale of the gods (1)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6692:// Healing ale of the gods(1)
				itemDef.name = "Healing ale of the gods (1)";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6689:// Healing ale of the gods (2)
				itemDef.name = "Healing ale of the gods (2)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6690:// Healing ale of the gods(2)
				itemDef.name = "Healing ale of the gods (2)";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6687:// Healing ale of the gods (3)
				itemDef.name = "Healing ale of the gods (3)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6688:// Healing ale of the gods(3)
				itemDef.name = "Healing ale of the gods (3)";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6685:// Healing ale of the gods (4)
				itemDef.name = "Healing ale of the gods (4)";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Gulp";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 6686:// Healing ale of the gods(4)
				itemDef.name = "Healing ale of the gods (4)";
				itemDef.description = "Godlike ale made for healing.".getBytes(); // Description
				break; // Drenth

			case 12690:// Armour sets
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

	public boolean method192(int j) {
		int k = anInt175;
		int l = anInt166;
		if (j == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return true;
		boolean flag = true;
		if (!Model.method463(k))
			flag = false;
		if (l != -1 && !Model.method463(l))
			flag = false;
		return flag;
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

	public Model method194(int j) {
		int k = anInt175;
		int l = anInt166;
		if (j == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return null;
		Model model = Model.method462(k);
		if (l != -1) {
			Model model_1 = Model.method462(l);
			Model aclass30_sub2_sub4_sub6s[] = { model, model_1 };
			model = new Model(2, aclass30_sub2_sub4_sub6s);
		}
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				model.method476(modifiedModelColors[i1],
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
		boolean flag = true;
		if (!Model.method463(k))
			flag = false;
		if (l != -1 && !Model.method463(l))
			flag = false;
		if (i1 != -1 && !Model.method463(i1))
			flag = false;
		return flag;
	}

	public Model method196(int i) {
		int j = anInt165;
		int k = anInt188;
		int l = anInt185;
		if (i == 1) {
			j = anInt200;
			k = anInt164;
			l = anInt162;
		}
		if (j == -1)
			return null;
		Model model = Model.method462(j);
		if (k != -1)
			if (l != -1) {
				Model model_1 = Model.method462(k);
				Model model_3 = Model.method462(l);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_1, model_3 };
				model = new Model(3, aclass30_sub2_sub4_sub6_1s);
			} else {
				Model model_2 = Model.method462(k);
				Model aclass30_sub2_sub4_sub6s[] = { model, model_2 };
				model = new Model(2, aclass30_sub2_sub4_sub6s);
			}
		if (i == 0 && aByte205 != 0)
			model.method475(0, aByte205, 0);
		if (i == 1 && aByte154 != 0)
			model.method475(0, aByte154, 0);
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				model.method476(modifiedModelColors[i1], originalModelColors[i1]);

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
		ItemDef itemDef = forID(certTemplateID);
		modelID = itemDef.modelID;
		modelZoom = itemDef.modelZoom;
		modelRotationY = itemDef.modelRotationY;
		modelRotationX = itemDef.modelRotationX;

		anInt204 = itemDef.anInt204;
		modelOffset1 = itemDef.modelOffset1;
		modelOffset2 = itemDef.modelOffset2;
		modifiedModelColors = itemDef.modifiedModelColors;
		originalModelColors = itemDef.originalModelColors;
		ItemDef itemDef_1 = forID(certID);
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

	public static Sprite getSprite(int id, int size, int color, int zoom) {
		if (id == 0) {
			return Client.cacheSprite[326];
		}
		ItemDef item = forID(id);
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
				item = forID(i1);
			}
		}
		Model model = item.method201(1);
		if (model == null)
			return null;
		Sprite image = new Sprite(32, 32);
		int k1 = Rasterizer.centerX;
		int l1 = Rasterizer.centerY;
		int ai[] = Rasterizer.anIntArray1472;
		int ai1[] = DrawingArea.pixels;
		float depthBuffer[] = DrawingArea.depthBuffer;
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
		ItemDef itemDef = forID(i);
		if (itemDef.stackIDs == null)
			j = -1;
		if (j > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++)
				if (j >= itemDef.stackAmounts[j1]
						&& itemDef.stackAmounts[j1] != 0)
					i1 = itemDef.stackIDs[j1];

			if (i1 != -1)
				itemDef = forID(i1);
		}
		Model model = itemDef.method201(1);
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
		int ai[] = Rasterizer.anIntArray1472;
		int ai1[] = DrawingArea.pixels;
		float depthBuffer[] = DrawingArea.depthBuffer;
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

	public Model method201(int i) {
		if (stackIDs != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (i >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return forID(j).method201(1);
		}
		Model model = (Model) mruNodes2.insertFromCache(id);
		if (model != null)
			return model;
		model = Model.method462(modelID);
		if (model == null)
			return null;
		if (anInt167 != 128 || anInt192 != 128 || anInt191 != 128)
			model.method478(anInt167, anInt191, anInt192);
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.method476(modifiedModelColors[l], originalModelColors[l]);

		}
		model.method479(64 + anInt196, 768 + anInt184, -50, -10, -50, true);
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
				return forID(j).method202(1);
		}
		Model model = Model.method462(modelID);
		if (model == null)
			return null;
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.method476(modifiedModelColors[l], originalModelColors[l]);

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

	private ItemDef() {
		id = -1;
	}

	private byte aByte154;
	public int value;// anInt155
	public int[] modifiedModelColors;// newModelColor
	public int id;// anInt157
	static MRUNodes mruNodes1 = new MRUNodes(100);
	public static MRUNodes mruNodes2 = new MRUNodes(50);
	public int[] originalModelColors;
	public boolean membersObject;// aBoolean161
	private int anInt162;
	public int certTemplateID;
	public int anInt164;// femArmModel
	public int anInt165;// maleWieldModel
	private int anInt166;
	private int anInt167;
	public String groundActions[];
	public int modelOffset1;
	public String name;// itemName
	private static ItemDef[] cache;
	private int anInt173;
	public int modelID;// dropModel
	public int anInt175;
	public boolean stackable;// itemStackable
	public byte[] description;// iteminspect
	public int certID;
	private static int cacheIndex;
	public int modelZoom;
	public static boolean isMembers = true;
	private static Stream stream;
	private int anInt184;
	private int anInt185;
	public int anInt188;// maleArmModel
	public String itemActions[];// itemMenuOption
	public String equipActions[];// equipItemOption
	public int modelRotationY;// modelRotateUp
	private int anInt191;
	private int anInt192;
	public int[] stackIDs;// modelStack
	public int modelOffset2;//
	private static int[] streamIndices;
	private int anInt196;
	public int anInt197;
	public int modelRotationX;// modelRotateRight
	public int anInt200;// femWieldModel
	public int[] stackAmounts;// itemAmount
	public int team;
	public static int totalItems;
	public int anInt204;// modelPositionUp
	private byte aByte205;

}
