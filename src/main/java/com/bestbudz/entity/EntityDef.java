package com.bestbudz.entity;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.pets.PetVariantManager;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.MRUNodes;
import com.bestbudz.world.VarBit;
import java.util.Objects;

public final class EntityDef {
	Client client;

  public static EntityDef forID(int i, boolean cached) {
	  // *** CHECK FOR VARIANTS FIRST - BEFORE ANYTHING ELSE ***
	  if (PetVariantManager.isVariant(i)) {
		  if (cached) {
			  // Check cache for variants too
			  for (int j = 0; j < 20; j++) {
				  if (cache[j].interfaceType == (long) i) {
					  return cache[j];
				  }
			  }
		  }

		  // Create and cache variant
		  anInt56 = (anInt56 + 1) % 20;
		  EntityDef entityDef = cached ? cache[anInt56] : new EntityDef();
		  entityDef.interfaceType = i;

		  PetVariantManager.applyVariant(i, entityDef);

		  return entityDef;
	  }

	  // Original forID logic for non-variant NPCs
	  if (cached) {
		  for (int j = 0; j < 20; j++) {
			  if (cache[j].interfaceType == (long) i) {
				  return cache[j];
			  }
		  }
	  }

	  anInt56 = (anInt56 + 1) % 20;
	  EntityDef entityDef = new EntityDef();
	  if (cached) {
		  cache[anInt56] = entityDef;
	  }

	  // Only read from stream if it's NOT a variant
	  stream.currentOffset = streamIndices[i];
	  entityDef.interfaceType = i;
	  entityDef.readValues(stream);
    switch (i) {
      case 6144:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.anIntArray94 = new int[2];
        entityDef.anIntArray94[0] = 14533;
        entityDef.anIntArray94[1] = 14522;
        entityDef.anIntArray70 = new int[5];
        entityDef.anIntArray70[0] = 10355;
        entityDef.anIntArray70[1] = 10471;
        entityDef.anIntArray70[2] = 10941;
        entityDef.anIntArray70[3] = 11200;
        entityDef.anIntArray70[4] = 11187;
        entityDef.anIntArray76 = new int[5];
        entityDef.anIntArray76[0] = 54387;
        entityDef.anIntArray76[1] = 54503;
        entityDef.anIntArray76[2] = 54744;
        entityDef.anIntArray76[3] = 55219;
        entityDef.anIntArray76[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.aByte68 = 3;
        break;

      case 6145:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.anIntArray94 = new int[4];
        entityDef.anIntArray94[0] = 14533;
        entityDef.anIntArray94[1] = 14523;
        entityDef.anIntArray94[2] = 14524;
        entityDef.anIntArray94[3] = 14525;
        entityDef.anIntArray70 = new int[5];
        entityDef.anIntArray70[0] = 63411;
        entityDef.anIntArray70[1] = 63287;
        entityDef.anIntArray70[2] = 63163;
        entityDef.anIntArray70[3] = 63046;
        entityDef.anIntArray70[4] = 63046;
        entityDef.anIntArray76 = new int[5];
        entityDef.anIntArray76[0] = 54387;
        entityDef.anIntArray76[1] = 54503;
        entityDef.anIntArray76[2] = 54744;
        entityDef.anIntArray76[3] = 55219;
        entityDef.anIntArray76[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.aByte68 = 3;
        break;

      case 6146:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.anIntArray94 = new int[2];
        entityDef.anIntArray94[0] = 14533;
        entityDef.anIntArray94[1] = 14522;
        entityDef.anIntArray70 = new int[5];
        entityDef.anIntArray70[0] = 10355;
        entityDef.anIntArray70[1] = 10471;
        entityDef.anIntArray70[2] = 10941;
        entityDef.anIntArray70[3] = 11200;
        entityDef.anIntArray70[4] = 11187;
        entityDef.anIntArray76 = new int[5];
        entityDef.anIntArray76[0] = 54387;
        entityDef.anIntArray76[1] = 54503;
        entityDef.anIntArray76[2] = 54744;
        entityDef.anIntArray76[3] = 55219;
        entityDef.anIntArray76[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.aByte68 = 3;
        break;

      case 6147:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.anIntArray94 = new int[4];
        entityDef.anIntArray94[0] = 14533;
        entityDef.anIntArray94[1] = 14523;
        entityDef.anIntArray94[2] = 14524;
        entityDef.anIntArray94[3] = 14525;
        entityDef.anIntArray70 = new int[5];
        entityDef.anIntArray70[0] = 63411;
        entityDef.anIntArray70[1] = 63287;
        entityDef.anIntArray70[2] = 63163;
        entityDef.anIntArray70[3] = 63046;
        entityDef.anIntArray70[4] = 63046;
        entityDef.anIntArray76 = new int[5];
        entityDef.anIntArray76[0] = 54387;
        entityDef.anIntArray76[1] = 54503;
        entityDef.anIntArray76[2] = 54744;
        entityDef.anIntArray76[3] = 55219;
        entityDef.anIntArray76[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.aByte68 = 3;
        break;

      case 1526:
      case 1520:
      case 1534:
      case 1536:
      case 1521:
      case 1519:
      case 1518:
        entityDef.name = "Mystery Fishes";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Splash";
        entityDef.description =
            "All kinds of fish are present, what else could be lurking in the water?".getBytes();
        break;

      case 1103:
        entityDef.actions = new String[5];
        entityDef.name = "Game Instructor";
        entityDef.actions[0] = "Speak with";
        entityDef.actions[3] = "Deal";
        entityDef.description = "It's a game instructor.".getBytes();
        break;

      case 13178:
      case 495:
      case 5907:
      case 318:
      case 497:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Naptime";
        break;

      case 3936:
        entityDef.name = "Travel Agency";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Book";
        entityDef.description = "It's a travel agency.".getBytes();
        break;

      case 3231:
        entityDef.name = "Tanner";
        entityDef.description = "It's a tanner.".getBytes();
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Open";
        break;

      case 732:
        entityDef.name = "BankStanding/THC-hempistry";
        entityDef.description = "It's a bankstanding and thc-hempistry store.".getBytes();
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        break;

      case 394:
      case 2182:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Bank";
        break;

      case 311:
        entityDef.name = "Status Manager";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.description = "It's the status manager.".getBytes();
        break;

      case 6749:
        entityDef.name = "Deluxe Title Manager";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.description = "Set your title color + name.".getBytes();
        break;

      case 1325:
        entityDef.name = "Drop Tables";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[3] = "Open";
        entityDef.description = "View drop tables.".getBytes();
        break;

      case 1306:
        entityDef.name = "Plastic Surgeon";
        entityDef.description = "It's the Best Budz Plastic Surgeon.".getBytes();
        break;

      case 5527:
        entityDef.name = "Achievements";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.description = "It's the achievement store.".getBytes();
        break;

      case 6524:
        entityDef.name = "Decantor";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[3] = "Decant";
        entityDef.description = "Decants your potions.".getBytes();
        break;

      case 5523:
        entityDef.name = "Cannabud";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.actions[3] = "Teleport";
        entityDef.description = "It's your local weed supplier.".getBytes();
        break;

      case 1558:
        entityDef.name = "Wah Gwaan";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's Best Budz bredren, Wah Gwaan!".getBytes();
        break;

      case 1603:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        break;

      case 4000:
        entityDef.name = "Prince black dragon";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {17414, 17415, 17429, 17422};
        entityDef.walkAnim = 4635;
        entityDef.standAnim = 90;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4001:
        entityDef.name = "Pet general graardor";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {27660, 27665};
        entityDef.walkAnim = 7016;
        entityDef.standAnim = 7017;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4002:
        entityDef.name = "Pet corporeal beast";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {11056};
        entityDef.walkAnim = 1684;
        entityDef.standAnim = 1678;
        entityDef.modelWidth = 65;
        entityDef.modelHeight = 65;
        break;

      case 4003:
        entityDef.name = "Pet kree'Arra";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {28019, 28021, 28020};
        entityDef.walkAnim = 6977;
        entityDef.standAnim = 6976;
        entityDef.modelWidth = 20;
        entityDef.modelHeight = 20;
        break;

      case 4004:
        entityDef.name = "Pet k'ril tsutsaroth";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {27683, 27681, 27692, 27682, 27690};
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 4070;
        entityDef.standAnim = 6935;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4009:
        entityDef.name = "Pet commander zilyana";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {27989, 27937, 27985, 27968, 27990};
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 6965;
        entityDef.standAnim = 6966;
        entityDef.modelWidth = 50;
        entityDef.modelHeight = 50;
        break;

      case 4006:
        entityDef.name = "Pet dagannoth supreme";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {9941, 9943};
        entityDef.standAnim = 2850;
        entityDef.walkAnim = 2849;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4007:
        entityDef.name = "Pet dagannoth prime";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {9940, 994, 9942};
        entityDef.walkAnim = 2849;
        entityDef.standAnim = 2850;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4008:
        entityDef.name = "Pet dagannoth rex";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {9941};
        entityDef.walkAnim = 2849;
        entityDef.standAnim = 2850;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4010:
        entityDef.name = "Pet Jad";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.anIntArray94 = new int[] {9319};
        entityDef.walkAnim = 2651;
        entityDef.standAnim = 2650;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 963:
        entityDef.anIntArray94 = new int[] {24602, 24605, 24606};
        entityDef.walkAnim = 6236;
        entityDef.standAnim = 6236;
        entityDef.modelWidth = 128;
        entityDef.modelHeight = 128;
        break;

      case 964:
        entityDef.anIntArray94 = new int[] {24597, 24598};
        entityDef.walkAnim = 6238;
        entityDef.standAnim = 6239;
        entityDef.modelWidth = 128;
        entityDef.modelHeight = 128;
        break;

      case 5547:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
		  entityDef.modelWidth = 30;
		  entityDef.modelHeight = 30;
        break;

      case 5559:
      case 5560:
      case 6637:
      case 6638:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.actions[1] = "Metamorphosis";
		  entityDef.modelWidth = 30;
		  entityDef.modelHeight = 30;
        break;

      case 2130: //Snakelings (Zulrah)
      case 2131:
      case 2132:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.actions[1] = "Metamorphosis";
		  entityDef.modelWidth = 90;
		  entityDef.modelHeight = 90;
        break;

      case 518:
      case 519:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        break;

      case 6525:
        entityDef.name = "Decantor";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.description = "Decants your potions.".getBytes();
        break;

      case 3984:
        entityDef.name = "Packs";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Store";
        entityDef.description = "It's a pack seller.".getBytes();
        break;

      case 403:
        entityDef.name = "Mercenary Tasks";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.description = "It's a mercenary master.".getBytes();
        break;

      case 1758:
        entityDef.name = "Void Shop";
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's the void knight's shop manager.".getBytes();
        break;

      case 1756:
        entityDef.name = "Void knight";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.description = "It's a void knight.".getBytes();
        break;

      case 606:
        entityDef.name = "Advance Master";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.actions[3] = "Advance";
        entityDef.description = "It's the advance master.".getBytes();
        break;

      case 490:
        entityDef.name = "Boss Mercenary Tasks";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.description = "It's a mercenary master.".getBytes();
        break;

      case 326:
        entityDef.name = "Combat Reset";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[2] = "Reset";
        entityDef.description = "It's a combat skill(s) manager.".getBytes();
        break;

      case 4936:
        entityDef.name = "Consumer Mage";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.actions[3] = "To Distant Land";
        entityDef.description = "It's a artificeer.".getBytes();
        break;

      case 22:
        entityDef.name = "Pawnshop";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a pawnshop.".getBytes();
        break;

      case 505:
        entityDef.name = "Skilling";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a skilling shop.".getBytes();
        break;

      case 225:
        entityDef.name = "Consumables";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a consumables shop.".getBytes();
        break;

      case 527:
        entityDef.name = "Weapons";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a weapons shop.".getBytes();
        break;

      case 528:
        entityDef.name = "Armour";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a armour shop.".getBytes();
        break;

      case 536:
        entityDef.name = "Range";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a range shop.".getBytes();
        break;

      case 5314:
        entityDef.name = "Mage";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a mage shop.".getBytes();
        break;

      case 603:
        entityDef.name = "Accessories";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a accessories shop.".getBytes();
        break;

      case 534:
        entityDef.name = "Clothing";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a clothing shop.".getBytes();
        break;
      case 532:
        entityDef.name = "Master Capes";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a mastercape shop.".getBytes();
        break;

      case 4306:
        entityDef.name = "Skillcapes";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's a skillcape shop.".getBytes();
        break;

      case 315:
        entityDef.name = "Bounty";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.actions[3] = "Get Skulled";
        entityDef.description = "It's a bounty hunter manager.".getBytes();
        break;
    }

    return entityDef;
  }

  public static EntityDef forID(int i) {
    return forID(i, true);
  }

  public static void unpackConfig(StreamLoader streamLoader) {
    stream = new Stream(streamLoader.getDataForName("npc.dat"));
    Stream stream2 = new Stream(streamLoader.getDataForName("npc.idx"));
    int totalNPCs = stream2.readUnsignedWord();
    System.out.println("Npcs Loaded: " + totalNPCs);
    streamIndices = new int[totalNPCs + 50000];
    int i = 2;
    for (int j = 0; j < totalNPCs; j++) {
      streamIndices[j] = i;
      i += stream2.readUnsignedWord();
    }

    cache = new EntityDef[20];
    for (int k = 0; k < 20; k++) cache[k] = new EntityDef();
    for (int index = 0; index < totalNPCs; index++) {
      EntityDef ed = forID(index);
      if (ed == null) continue;
      if (ed.name == null)
	  {
	  }
    }
    boolean dump = false;
  }

  public static void nullLoader() {
    mruNodes = null;
    streamIndices = null;
    cache = null;
    stream = null;
  }

  public Model method160() {
    if (childrenIDs != null) {
      EntityDef entityDef = method161();
      if (entityDef == null) return null;
      else return entityDef.method160();
    }
    if (anIntArray73 == null) return null;
    boolean flag1 = false;
	  for (int value : anIntArray73) if (!Model.method463(value)) flag1 = true;

    if (flag1) return null;
    Model[] aclass30_sub2_sub4_sub6s = new Model[anIntArray73.length];
    for (int j = 0; j < anIntArray73.length; j++)
      aclass30_sub2_sub4_sub6s[j] = Model.loadModelFromCache(anIntArray73[j]);

    Model model;
    if (aclass30_sub2_sub4_sub6s.length == 1) model = aclass30_sub2_sub4_sub6s[0];
    else model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
    if (anIntArray76 != null) {
      for (int k = 0; k < anIntArray76.length; k++)
        Objects.requireNonNull(model).replaceColor(anIntArray76[k], anIntArray70[k]);
    }
    return model;
  }

  public EntityDef method161() {
    int j = -1;
    if (anInt57 != -1) {
      VarBit varBit = VarBit.cache[anInt57];
      int k = varBit.anInt648;
      int l = varBit.anInt649;
      int i1 = varBit.anInt650;
      int j1 = Client.anIntArray1232[i1 - l];
      j = client.variousSettings[k] >> l & j1;
    } else if (anInt59 != -1) j = client.variousSettings[anInt59];
    if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1) return null;
    else return forID(childrenIDs[j]);
  }

  public Model method164(
      int j, int currAnim, int nextAnim, int currCycle, int nextCycle, int[] ai) {
    if (childrenIDs != null) {
      final EntityDef type = method161();
      if (type == null) {
        return null;
      } else {
        return type.method164(j, currAnim, ai);
      }
    }
    Model model = (Model) mruNodes.insertFromCache(interfaceType);
    if (model == null) {
      boolean flag = false;
		for (int i : anIntArray94)
		{
			if (!Model.method463(i))
			{
				flag = true;
			}
		}
      if (flag) {
        return null;
      }
      final Model[] parts = new Model[anIntArray94.length];
      for (int j1 = 0; j1 < anIntArray94.length; j1++) {
        parts[j1] = Model.loadModelFromCache(anIntArray94[j1]);
      }
      if (parts.length == 1) {
        model = parts[0];
      } else {
        model = new Model(parts.length, parts);
      }
      if (anIntArray70 != null) {
        for (int k1 = 0; k1 < anIntArray70.length; k1++)
          Objects.requireNonNull(model).replaceColor(anIntArray70[k1], anIntArray76[k1]);
      }
      Objects.requireNonNull(model).calculateNormals();
      model.applyLighting(64 + anInt85, 1500 + anInt92, -30, -50, -30, true);
      mruNodes.removeFromCache(model, interfaceType);
    }
    final Model model_1 = Model.aModel_1621;
    model_1.method464(model, SequenceFrame.method532(currAnim) & SequenceFrame.method532(j));
    if (currAnim != -1 && j != -1) {
      model_1.modelScale(modelHeight, modelHeight, modelWidth);
    } else if (currAnim != -1) {
      model_1.interpolateFrames(currAnim, nextAnim, nextCycle, currCycle);
    }
    if (modelHeight != 128 || modelWidth != 128) {
      model_1.modelScale(modelHeight, modelWidth, modelHeight);
    }
    model_1.method466();
    model_1.anIntArrayArray1658 = null;
    model_1.anIntArrayArray1657 = null;
    if (aByte68 == 1) model_1.aBoolean1659 = true;
    return model_1;
  }

  public Model method164(int j, int k, int[] ai) {
    if (childrenIDs != null) {
      EntityDef entityDef = method161();
      if (entityDef == null) return null;
      else return entityDef.method164(j, k, ai);
    }
    Model model = (Model) mruNodes.insertFromCache(interfaceType);
    if (model == null) {
      boolean flag = false;
		for (int i : anIntArray94) if (!Model.method463(i)) flag = true;

      if (flag) return null;
      Model[] aclass30_sub2_sub4_sub6s = new Model[anIntArray94.length];
      for (int j1 = 0; j1 < anIntArray94.length; j1++)
        aclass30_sub2_sub4_sub6s[j1] = Model.loadModelFromCache(anIntArray94[j1]);

      if (aclass30_sub2_sub4_sub6s.length == 1) model = aclass30_sub2_sub4_sub6s[0];
      else model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
      if (anIntArray76 != null) {
        for (int k1 = 0; k1 < anIntArray76.length; k1++)
          Objects.requireNonNull(model).replaceColor(anIntArray76[k1], anIntArray70[k1]);
      }
      Objects.requireNonNull(model).calculateNormals();
      model.applyLighting(64 + anInt85, 1500 + anInt92, -30, -50, -30, true);
      mruNodes.removeFromCache(model, interfaceType);
    }
    Model model_1 = Model.aModel_1621;
    model_1.method464(model, SequenceFrame.method532(k) & SequenceFrame.method532(j));
    if (k != -1 && j != -1) model_1.method471(ai, j, k);
    else if (k != -1) model_1.method470(k);
    if (modelHeight != 128 || modelWidth != 128) model_1.modelScale(modelHeight, modelHeight, modelWidth);
    model_1.method466();
    model_1.anIntArrayArray1658 = null;
    model_1.anIntArrayArray1657 = null;
    if (aByte68 == 1) model_1.aBoolean1659 = true;
    return model_1;
  }

  public void readValues(Stream stream) {
    do {
      int i = stream.readUnsignedByte();
      if (i == 0) return;
      if (i == 1) {
        int j = stream.readUnsignedByte();
        anIntArray94 = new int[j];
        for (int j1 = 0; j1 < j; j1++) anIntArray94[j1] = stream.readUnsignedWord();

      } else if (i == 2) name = stream.readString();
      else if (i == 3) description = stream.readBytes();
      else if (i == 12) aByte68 = stream.readSignedByte();
      else if (i == 13) standAnim = stream.readUnsignedWord();
      else if (i == 14) walkAnim = stream.readUnsignedWord();
      else if (i == 17) {
        walkAnim = stream.readUnsignedWord();
        anInt58 = stream.readUnsignedWord();
        anInt83 = stream.readUnsignedWord();
        anInt55 = stream.readUnsignedWord();
      } else if (i >= 30 && i < 40) {
        if (actions == null) actions = new String[5];
        actions[i - 30] = stream.readString();
        if (actions[i - 30].equalsIgnoreCase("hidden")) actions[i - 30] = null;
      } else if (i == 40) {
        int k = stream.readUnsignedByte();
        anIntArray76 = new int[k];
        anIntArray70 = new int[k];
        for (int k1 = 0; k1 < k; k1++) {
          anIntArray76[k1] = stream.readUnsignedWord();
          anIntArray70[k1] = stream.readUnsignedWord();
        }

      } else if (i == 60) {
        int l = stream.readUnsignedByte();
        anIntArray73 = new int[l];
        for (int l1 = 0; l1 < l; l1++) anIntArray73[l1] = stream.readUnsignedWord();

      } else if (i == 90) stream.readUnsignedWord();
      else if (i == 91) stream.readUnsignedWord();
      else if (i == 92) stream.readUnsignedWord();
      else if (i == 93) aBoolean87 = false;
      else if (i == 95) combatGrade = stream.readUnsignedWord();
      else if (i == 97) modelHeight = stream.readUnsignedWord();
      else if (i == 98) modelWidth = stream.readUnsignedWord();
      else if (i == 99) aBoolean93 = true;
      else if (i == 100) anInt85 = stream.readSignedByte();
      else if (i == 101) anInt92 = stream.readSignedByte() * 5;
      else if (i == 102) anInt75 = stream.readUnsignedWord();
      else if (i == 103) anInt79 = stream.readUnsignedWord();
      else if (i == 106) {
        anInt57 = stream.readUnsignedWord();
        if (anInt57 == 65535) anInt57 = -1;
        anInt59 = stream.readUnsignedWord();
        if (anInt59 == 65535) anInt59 = -1;
        int i1 = stream.readUnsignedByte();
        childrenIDs = new int[i1 + 1];
        for (int i2 = 0; i2 <= i1; i2++) {
          childrenIDs[i2] = stream.readUnsignedWord();
          if (childrenIDs[i2] == 65535) childrenIDs[i2] = -1;
        }

      } else if (i == 107) aBoolean84 = false;
    } while (true);
  }

  public EntityDef() {
    anInt55 = -1;
    anInt57 = -1;
    anInt58 = -1;
    anInt59 = -1;
    combatGrade = -1;
    anInt64 = 1834;
    walkAnim = -1;
    aByte68 = 1;
    anInt75 = -1;
    standAnim = -1;
    interfaceType = -1L;
    anInt79 = 32;
    anInt83 = -1;
    aBoolean84 = true;
    modelWidth = 128;
    aBoolean87 = true;
    modelHeight = 128;
    aBoolean93 = false;
  }

  public int anInt55;
  public static int anInt56;
  public int anInt57;
  public int anInt58;
  public int anInt59;
  public static Stream stream;
  public int combatGrade;
  public final int anInt64;
  public String name;
  public String[] actions;
  public int walkAnim;
  public byte aByte68;
  public int[] anIntArray70;
  public static int[] streamIndices;
  public int[] anIntArray73;
  public int anInt75;
  public int[] anIntArray76;
  public int standAnim;
  public long interfaceType;
  public int anInt79;
  public static EntityDef[] cache;
  public static Client clientInstance;
  public int anInt83;
  public boolean aBoolean84;
  public int anInt85;
  public int modelWidth;
  public boolean aBoolean87;
  public int[] childrenIDs;
  public byte[] description;
  public int modelHeight;
  public int anInt92;
  public boolean aBoolean93;
  public int[] anIntArray94;
  public static MRUNodes mruNodes = new MRUNodes(30);
}
