package com.bestbudz.world;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.network.OnDemandFetcher;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.MRUNodes;
import java.util.Objects;

public final class ObjectDef {
	Client client;
	public static final Model[] aModelArray741s = new Model[4];
	public static boolean lowMem;
	public static Stream stream;
	public static int[] streamIndices;
	public static Client clientInstance;
	public static int cacheIndex;
	public static MRUNodes mruNodes2 = new MRUNodes(64); // Increased from 30
	public static ObjectDef[] cache;
	public static MRUNodes mruNodes1 = new MRUNodes(1024); // Increased from 500

	public boolean aBoolean736;
	public byte aByte737;
	public int anInt738;
	public String name;
	public int anInt740;
	public byte aByte742;
	public int anInt744;
	public int anInt745;
	public int anInt746;
	public int[] originalModelColors;
	public int anInt748;
	public int anInt749;
	public boolean aBoolean751;
	public int type;
	public boolean aBoolean757;
	public int anInt758;
	public int[] childrenIDs;
	public int anInt760;
	public int anInt761;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean766;
	public boolean aBoolean767;
	public int anInt768;
	public boolean aBoolean769;
	public int anInt772;
	public int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	public int[] anIntArray776;
	public byte[] description;
	public boolean hasActions;
	public boolean aBoolean779;
	public int anInt781;
	public int anInt783;
	public int[] modifiedModelColors;
	public String[] actions;

	public ObjectDef() {
		type = -1;
	}

	public static ObjectDef forID(int i) {
		if (i > streamIndices.length)
			i = streamIndices.length - 1;
		for (int j = 0; j < 20; j++)
			if (cache[j].type == i)
				return cache[j];
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef class46 = cache[cacheIndex];
		stream.currentOffset = streamIndices[i];
		class46.type = i;
		class46.setDefaults();
		class46.readValues(stream);

		if (i == 11407 || i == 11408) {
			class46 = forID(11404);
			class46.type = i;
			class46.modifiedModelColors = new int[]{7105, 8137, 7130, 5043, 7082};
			if (i == 11407)
				class46.originalModelColors = new int[]{51126, 51149, 51149, 51113, 51113};
			else
				class46.originalModelColors = new int[]{117, 127, 127, 96, 96};
			return class46;
		}

		switch (i) {
			case 26621:
			case 26620:
			case 26619:
			case 26618:
			case 26561:
			case 26562:
			case 7836:
			case 7837:
			case 7838:
			case 7839:
			case 7848:
			case 7847:
			case 7849:
			case 7850:
			case 8150:
			case 8151:
			case 8152:
			case 8153:
			case 8550:
			case 8551:
			case 8552:
			case 8553:
			case 8554:
			case 8555:
			case 8556:
			case 8557:
			case 20973:
			case 26606:
			case 26611:
			case 26613:
			case 26626:
			case 26608:
			case 26607:
			case 26603:
			case 26580:
			case 26604:
			case 26609:
			case 26600:
			case 26601:
			case 26610:
			case 26616:
			case 26612:
			case 26605:
			case 26602:
				class46.hasActions = true;
				break;
			case 22472:
				class46.name = "Tablet";
				class46.actions = new String[5];
				class46.actions[0] = "Create";
				break;

			case 8720:
			case 26492:
			case 26796:
			case 26797:
			case 26798:
			case 26799:
			case 26800:
			case 26801:
			case 26802:
			case 26803:
			case 26804:
			case 26805:
			case 26806:
			case 26807:
			case 26808:
			case 26809:
			case 26810:
			case 26811:
			case 26812:
			case 26813:
			case 26814:
			case 26815:
			case 26816:
			case 26817:
			case 26818:
			case 26819:
			case 26820:
			case 26821:
				class46.name = "Chill Booth";
				class46.actions = new String[5];
				class46.actions[1] = "Open shop";
				class46.hasActions = true;
				break;

			case 2097:
				class46.name = "Anvil";
				class46.description = "It's a solid iron block hammer thingy.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Hammer";
				class46.hasActions = true;
				break;

			case 2030:
				class46.name = "Forge";
				class46.description = "Wondering if i could get my blunt lit here..".getBytes();
				class46.hasActions = true;
				break;

			case 26181:
				class46.name = "Stove";
				class46.description = "Looks like someone attempted to cook meth on this stove.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Cook";
				class46.hasActions = true;
				break;

			case 10377:
				class46.name = "Giant's Glory Hole";
				class46.description = "HOLY FUCK! There is a lot a semen in there.".getBytes();
				break;

			case 10379:
				class46.name = "Weed Stash";
				class46.description = "You sniff the boxes & It's Cheese Haze!".getBytes();
				break;

			case 10380:
				class46.name = "Trim Waste";
				class46.description = "Boxes filled with trimmed cannabis leaves and branches.".getBytes();
				break;

			case 14854:
			case 14855:
			case 14856:
				class46.name = "Fancy Rocks";
				class46.description = "There are tons of gems in these rocks.".getBytes();
				class46.hasActions = true;
				break;

			case 14901:
				class46.name = "Fire Essence";
				class46.description = "It compels you to keep burning wood, but it also pays you!.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Offer to";
				class46.hasActions = true;
				break;

			case 10376:
				class46.name = "Cocaïne Stall";
				class46.description = "You look and wonder.. Where are the topless women?".getBytes();
				break;

			case 26149:
				class46.name = "Budz Core";
				class46.description = "The BestBudz core, essence of all magic in the game.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Offer to";
				class46.hasActions = true;
				break;

			case 2072:
				class46.name = "Game Crate";
				class46.actions = new String[5];
				class46.actions[0] = "Search";
				break;

			case 9472:
			case 9371:
				class46.name = "Stoner Market";
				class46.actions = new String[5];
				class46.actions[0] = "Info";
				class46.actions[1] = "Edit Shop";
				class46.actions[2] = "Explore Shops";
				break;

			case 18772:
				class46.name = "Misery Box";
				class46.actions = new String[5];
				class46.actions[0] = "Open";
				break;

			case 5249:
				class46.name = "Fyah";
				class46.description = "Bun dem!".getBytes();
				break;

			case 13712:
				class46.name = "Tin";
				class46.description = "It's a Tin ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 13709:
				class46.name = "Copper";
				class46.description = "It's a Copper ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 13711:
				class46.name = "Iron";
				class46.description = "It's a Iron ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 13714:
				class46.name = "Coal";
				class46.description = "It's a Coal ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 13718:
				class46.name = "Mithril";
				class46.description = "It's a Mithril ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 14168:
				class46.name = "Adamantite";
				class46.description = "It's a Adamantite ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 13707:
				class46.name = "Gold";
				class46.description = "It's a Gold ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 14175:
				class46.name = "Runite";
				class46.description = "It's a Runite ore.".getBytes();
				class46.actions = new String[5];
				class46.actions[0] = "Collect";
				break;

			case 574:
				class46.name = "Mage Spells";
				class46.actions = new String[5];
				class46.actions[0] = "Change";
				break;

			case 575:
				class46.name = "Altar";
				class46.actions = new String[5];
				class46.actions[0] = "Restore";
				break;

			case 576:
				class46.name = "Highscores";
				class46.actions = new String[5];
				class46.actions[0] = "View";
				break;

			case 13618:
				class46.name = "Wyverns";
				class46.actions = new String[5];
				class46.actions[0] = "Teleport-to";
				break;

			case 13619:
				class46.name = "Fountain of Rune";
				class46.actions = new String[5];
				class46.actions[0] = "Teleport-to";
				break;

			case 4090:
				class46.name = "Blood altar";
				class46.actions = new String[5];
				class46.actions[0] = "Craft";
				break;
		}

		return class46;
	}

	public static void nullLoader() {
		mruNodes1 = null;
		mruNodes2 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void unpackConfig(StreamLoader streamLoader) {
		stream = new Stream(streamLoader.getDataForName("loc.dat"));
		Stream stream = new Stream(streamLoader.getDataForName("loc.idx"));
		int totalObjects = stream.readUnsignedWord();
		streamIndices = new int[totalObjects];
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new ObjectDef();
	}

	public void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		anInt744 = 1;
		anInt761 = 1;
		aBoolean767 = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public void method574(OnDemandFetcher class42_sub1) {
		if (anIntArray773 == null)
			return;
		for (int i : anIntArray773) class42_sub1.method560(i & 0xffff, 0);
	}

	public boolean method577(int i) {
		if (anIntArray776 == null) {
			if (anIntArray773 == null)
				return true;
			if (i != 10)
				return true;
			for (int j : anIntArray773) {
				if (!Model.method463(j & 0xffff))
					return false;
			}
			return true;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.method463(anIntArray773[j] & 0xffff);
		return true;
	}

	public Model method578(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null)
			return null;
		if (aBoolean762 || aBoolean769)
			model = new Model(aBoolean762, aBoolean769, model);
		if (aBoolean762) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.anInt1626; i2++) {
				int j2 = model.anIntArray1627[i2];
				int k2 = model.anIntArray1629[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.anIntArray1628[i2] += j3 - l1;
			}
			model.method467();
		}
		return model;
	}

	public boolean method579() {
		if (anIntArray773 == null)
			return true;
		for (int j : anIntArray773) {
			if (!Model.method463(j & 0xffff))
				return false;
		}
		return true;
	}

	public ObjectDef method580() {
		int i = -1;
		if (anInt774 != -1) {
			VarBit varBit = VarBit.cache[anInt774];
			int j = varBit.anInt648;
			int k = varBit.anInt649;
			int l = varBit.anInt650;
			int i1 = Client.anIntArray1232[l - k];
			i = client.variousSettings[j] >> k & i1;
		} else if (anInt749 != -1)
			i = client.variousSettings[anInt749];
		if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
			return null;
		else
			return forID(childrenIDs[i]);
	}

	public Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = ((long) type << 8) + l + ((long) (k + 1) << 32);
			Model model_1 = (Model) mruNodes2.insertFromCache(l1);
			if (model_1 != null)
				return model_1;
			if (anIntArray773 == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = anIntArray773.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = anIntArray773[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) mruNodes1.insertFromCache(l2);
				if (model == null) {
					model = Model.loadModelFromCache(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.method477();
					mruNodes1.removeFromCache(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}

			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}

			if (i1 == -1)
				return null;
			l1 = ((long) type << 8) + ((long) i1 << 3) + l + ((long) (k + 1) << 32);
			Model model_2 = (Model) mruNodes2.insertFromCache(l1);
			if (model_2 != null)
				return model_2;
			int j2 = anIntArray773[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = (Model) mruNodes1.insertFromCache(j2);
			if (model == null) {
				model = Model.loadModelFromCache(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.method477();
				mruNodes1.removeFromCache(model, j2);
			}
		}

		// Optimize flag calculations - do them once
		boolean needsScale = anInt748 != 128 || anInt772 != 128 || anInt740 != 128;
		boolean needsTranslate = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;

		Model model_3 = new Model(modifiedModelColors == null, SequenceFrame.method532(k),
			l == 0 && k == -1 && !needsScale && !needsTranslate, Objects.requireNonNull(model));
		if (k != -1) {
			model_3.calculateNormals();
			model_3.method470(k);
			model_3.anIntArrayArray1658 = null;
			model_3.anIntArrayArray1657 = null;
		}

		// Optimize rotation loop
		for (int rot = 0; rot < l; rot++)
			model_3.method473();

		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.replaceColor(modifiedModelColors[k2], originalModelColors[k2]);
		}
		if (needsScale)
			model_3.modelScale(anInt748, anInt740, anInt772);
		if (needsTranslate)
			model_3.translateCoords(anInt738, anInt745, anInt783);
		model_3.applyLighting(74, 1000, -90, -580, -90, !aBoolean769);
		if (anInt760 == 1)
			model_3.anInt1654 = model_3.modelHeight;
		mruNodes2.removeFromCache(model_3, l1);
		return model_3;
	}

	public void readValues(Stream stream) {
		do {
			int type = stream.readUnsignedByte();
			if (type == 0)
				break;
			if (type == 1) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = new int[len];
						anIntArray773 = new int[len];
						for (int k1 = 0; k1 < len; k1++) {
							anIntArray773[k1] = stream.readUnsignedWord();
							anIntArray776[k1] = stream.readUnsignedByte();
						}
					} else {
						stream.currentOffset += len * 3;
					}
				}
			} else if (type == 2)
				name = stream.readString();
			else if (type == 3)
				description = stream.readBytes();
			else if (type == 5) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = null;
						anIntArray773 = new int[len];
						for (int l1 = 0; l1 < len; l1++)
							anIntArray773[l1] = stream.readUnsignedWord();
					} else {
						stream.currentOffset += len * 2;
					}
				}
			} else if (type == 14)
				anInt744 = stream.readUnsignedByte();
			else if (type == 15)
				anInt761 = stream.readUnsignedByte();
			else if (type == 17)
				aBoolean767 = false;
			else if (type == 18)
				aBoolean757 = false;
			else if (type == 19)
				hasActions = (stream.readUnsignedByte() == 1);
			else if (type == 21)
				aBoolean762 = true;
			else if (type == 22)
				aBoolean769 = false;
			else if (type == 23)
				aBoolean764 = true;
			else if (type == 24) {
				anInt781 = stream.readUnsignedWord();
				if (anInt781 == 65535)
					anInt781 = -1;
			} else if (type == 28)
				anInt775 = stream.readUnsignedByte();
			else if (type == 29)
				aByte737 = stream.readSignedByte();
			else if (type == 39)
				aByte742 = stream.readSignedByte();
			else if (type >= 30 && type < 39) {
				if (actions == null)
					actions = new String[5];
				actions[type - 30] = stream.readString();
				if (actions[type - 30].equalsIgnoreCase("hidden"))
					actions[type - 30] = null;
			} else if (type == 40) {
				int i1 = stream.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = stream.readUnsignedWord();
					originalModelColors[i2] = stream.readUnsignedWord();
				}
			} else if (type == 60)
				anInt746 = stream.readUnsignedWord();
			else if (type == 62)
				aBoolean751 = true;
			else if (type == 64)
				aBoolean779 = false;
			else if (type == 65)
				anInt748 = stream.readUnsignedWord();
			else if (type == 66)
				anInt772 = stream.readUnsignedWord();
			else if (type == 67)
				anInt740 = stream.readUnsignedWord();
			else if (type == 68)
				anInt758 = stream.readUnsignedWord();
			else if (type == 69)
				anInt768 = stream.readUnsignedByte();
			else if (type == 70)
				anInt738 = stream.readSignedWord();
			else if (type == 71)
				anInt745 = stream.readSignedWord();
			else if (type == 72)
				anInt783 = stream.readSignedWord();
			else if (type == 73)
				aBoolean736 = true;
			else if (type == 74)
				aBoolean766 = true;
			else if (type == 75)
				anInt760 = stream.readUnsignedByte();
			else if (type == 77) {
				anInt774 = stream.readUnsignedWord();
				if (anInt774 == 65535)
					anInt774 = -1;
				anInt749 = stream.readUnsignedWord();
				if (anInt749 == 65535)
					anInt749 = -1;
				int j1 = stream.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = stream.readUnsignedWord();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			}
		} while (true);
		if (!Objects.equals(name, "null") && name != null) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}
}