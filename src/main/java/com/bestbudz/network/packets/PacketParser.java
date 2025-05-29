package com.bestbudz.network.packets;

import com.bestbudz.cache.Signlink;
import static com.bestbudz.data.XP.addXP;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.frame.UIDockHelper;
import com.bestbudz.dock.ui.panel.game.SkillsPanel;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.ui.panel.DockPanelMapping;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.ItemDef;
import com.bestbudz.data.Skills;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.SendFrames.sendFrame126;
import static com.bestbudz.network.packets.SendFrames.updateStrings;
import static com.bestbudz.entity.ParseAndUpdateEntities.updateNPCs;
import static com.bestbudz.entity.UpdateStoners.updateStoners;
import static com.bestbudz.ui.InterfaceManagement.clearInterfaceAnimations;
import static com.bestbudz.ui.InterfaceManagement.clearTopInterfaces;
import static com.bestbudz.engine.core.login.logout.Logout.resetLogout;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import static com.bestbudz.ui.NotificationMessages.pushKill;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.ui.TextInput;
import static com.bestbudz.ui.interfaces.Chatbox.privateChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.publicChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import static com.bestbudz.ui.interfaces.Chatbox.reportAbuseInput;
import com.bestbudz.ui.interfaces.StatusOrbs;
import com.bestbudz.util.SizeConstants;
import com.bestbudz.util.TextClass;
import com.bestbudz.world.Class30_Sub1;
import static com.bestbudz.world.GroundItem.spawnGroundItem;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public class PacketParser extends Client
{
	public static void handlePackets(Graphics2D g) {
		for (int i = 0; i < 5; i++) {
			if (!parsePacket(g)) break;
		}
	}

	public static boolean parsePacket(Graphics2D g)
	{
		if (socketStream == null)
			return false;
		try
		{
			int i = socketStream.available();
			if (i == 0)
				return false;
			if (pktType == -1)
			{
				socketStream.flushInputStream(inStream.buffer, 1);
				pktType = inStream.buffer[0] & 0xff;
				if (encryption != null)
					pktType = pktType - encryption.getNextKey() & 0xff;
				pktSize = SizeConstants.packetSizes[pktType];
				i--;
			}
			if (pktSize == -1)
				if (i > 0)
				{
					socketStream.flushInputStream(inStream.buffer, 1);
					pktSize = inStream.buffer[0] & 0xff;
					i--;
				}
				else
				{
					return false;
				}
			if (pktSize == -2)
				if (i > 1)
				{
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					pktSize = inStream.readUnsignedWord();
					i -= 2;
				}
				else
				{
					return false;
				}
			if (i < pktSize)
				return false;
			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			anInt1009 = 0;
			anInt843 = anInt842;
			anInt842 = anInt841;
			anInt841 = pktType;
			switch (pktType)
			{
				case 81:
					updateStoners(pktSize, inStream);
					aBoolean1080 = false;
					pktType = -1;
					return true;

				case 176:
					daysSinceRecovChange = inStream.method427();
					unreadMessages = inStream.method435();
					membersInt = inStream.readUnsignedByte();
					anInt1193 = inStream.method440();
					daysSinceLastLogin = inStream.readUnsignedWord();
					if (anInt1193 != 0 && openInterfaceID == -1)
					{
						Signlink.dnslookup(TextClass.method586(anInt1193));
						clearTopInterfaces();
						char c = '\u028A';
						if (daysSinceRecovChange != 201 || membersInt == 1)
							c = '\u028F';
						reportAbuseInput = "";
						canMute = false;
						for (int k9 = 0; k9 < RSInterface.interfaceCache.length; k9++)
						{
							if (RSInterface.interfaceCache[k9] == null
								|| RSInterface.interfaceCache[k9].contentType != c)
								continue;
							openInterfaceID = RSInterface.interfaceCache[k9].parentID;

						}
					}
					pktType = -1;
					return true;

				case 64:
					anInt1268 = inStream.method427();
					anInt1269 = inStream.method428();
					for (int j = anInt1268; j < anInt1268 + 8; j++)
					{
						for (int l9 = anInt1269; l9 < anInt1269 + 8; l9++)
							if (groundArray[plane][j][l9] != null)
							{
								groundArray[plane][j][l9] = null;
								spawnGroundItem(j, l9);
							}
					}
					for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
						.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
						.reverseGetNext())
						if (class30_sub1.anInt1297 >= anInt1268 && class30_sub1.anInt1297 < anInt1268 + 8
							&& class30_sub1.anInt1298 >= anInt1269 && class30_sub1.anInt1298 < anInt1269 + 8
							&& class30_sub1.anInt1295 == plane)
							class30_sub1.anInt1294 = 0;
					pktType = -1;
					return true;

				case 185:
					int k = inStream.method436();
					RSInterface.interfaceCache[k].anInt233 = 3;
					if (myStoner.desc == null)
						RSInterface.interfaceCache[k].mediaID = (myStoner.anIntArray1700[0] << 25)
							+ (myStoner.anIntArray1700[4] << 20) + (myStoner.equipment[0] << 15)
							+ (myStoner.equipment[8] << 10) + (myStoner.equipment[11] << 5) + myStoner.equipment[1];
					else
						RSInterface.interfaceCache[k].mediaID = (int) (0x12345678L + myStoner.desc.interfaceType);
					pktType = -1;
					return true;
				case 217:
					try
					{
						clanUsername = inStream.readString();
						clanMessage = TextInput.processText(inStream.readString());
						clanTitle = inStream.readString();
						channelRights = inStream.readUnsignedWord();
						pushMessage(clanMessage, 16, clanUsername);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					pktType = -1;
					return true;

				case 107:
					aBoolean1160 = false;
					for (int l = 0; l < 5; l++)
						aBooleanArray876[l] = false;
					StatusOrbs.xpCounter = 0;
					pktType = -1;
					return true;

				case 72:
					int i1 = inStream.method434();
					RSInterface class9 = RSInterface.interfaceCache[i1];
					for (int k15 = 0; k15 < class9.inv.length; k15++)
					{
						class9.inv[k15] = -1;
						class9.inv[k15] = 0;
					}
					pktType = -1;
					return true;

				case 214:
					ignoreCount = pktSize / 8;
					for (int j1 = 0; j1 < ignoreCount; j1++)
						ignoreListAsLongs[j1] = inStream.readQWord();
					pktType = -1;
					return true;

				case 166:
					aBoolean1160 = true;
					anInt1098 = inStream.readUnsignedByte();
					anInt1099 = inStream.readUnsignedByte();
					anInt1100 = inStream.readUnsignedWord();
					anInt1101 = inStream.readUnsignedByte();
					anInt1102 = inStream.readUnsignedByte();
					if (anInt1102 >= 100)
					{
						xCameraPos = anInt1098 * 128 + 64;
						yCameraPos = anInt1099 * 128 + 64;
						zCameraPos = getTerrainHeight(plane, yCameraPos, xCameraPos) - anInt1100;
					}
					pktType = -1;
					return true;

				case 134: {                                    // isolate scope
					int pid        = inStream.readUnsignedByte();  // profession id
					int pAdvance   = inStream.readUnsignedByte();  // advances
					int pExp       = inStream.method439();         // exp   (4 bytes, middle-endian)
					int pGrade     = inStream.method439();         // grade (4 bytes, middle-endian)

					currentExp[pid]   = pExp;
					currentStats[pid] = pGrade;
					currentAdvances[pid] = pAdvance;

					maxStats[pid] = 1;
					for (int lvl = 0; lvl < Skills.EXP_FOR_LEVEL.length; lvl++)
						if (pExp >= Skills.EXP_FOR_LEVEL[lvl])
							maxStats[pid] = lvl;

					DockTextUpdatable updatable = UIDockHelper.getUpdatablePanel("Skills");
					if (UIDockHelper.isPanelVisible("Skills") && updatable instanceof SkillsPanel) {
						((SkillsPanel) updatable).updateSkill(pid);
					}

					pktType = -1;
					return true;
				}


				case 71:
					int l1 = inStream.readUnsignedWord();
					int j10 = inStream.method426();
					if (l1 == 65535)
						l1 = -1;
					tabInterfaceIDs[j10] = l1;
					tabAreaAltered = true;
					pktType = -1;
					return true;

				case 74:
					int i2 = inStream.method434();
					if (i2 == 65535)
						i2 = -1;
					if (i2 != currentSong && musicEnabled && !lowMem && prevSong == 0)
					{
						nextSong = i2;
						songChanging = true;
						onDemandFetcher.method558(2, nextSong);
					}
					currentSong = i2;
					pktType = -1;
					return true;

				case 121:
					int j2 = inStream.method436();
					int k10 = inStream.method435();
					if (musicEnabled && !lowMem)
					{
						nextSong = j2;
						songChanging = false;
						onDemandFetcher.method558(2, nextSong);
						prevSong = k10;
					}
					pktType = -1;
					return true;

				case 109:
					resetLogout();
					pktType = -1;
					return false;

				case 70:
					int l10 = inStream.method437();
					int i16 = inStream.method434();
					RSInterface class9_5 = RSInterface.interfaceCache[i16];
					class9_5.anInt265 = l10;
					pktType = -1;
					return true;

				case 73:
				case 241:
					int l2 = anInt1069;
					int i11 = anInt1070;
					if (pktType == 73)
					{
						l2 = inStream.method435();
						i11 = inStream.readUnsignedWord();
						aBoolean1159 = false;
					}
					if (pktType == 241) // REGION LOADER
					{
						i11 = inStream.method435();
						inStream.initBitAccess();
						for (int j16 = 0; j16 < 4; j16++)
						{
							for (int l20 = 0; l20 < 13; l20++)
							{
								for (int j23 = 0; j23 < 13; j23++)
								{
									int i26 = inStream.readBits(1);
									if (i26 == 1)
										anIntArrayArrayArray1129[j16][l20][j23] = inStream.readBits(26);
									else
										anIntArrayArrayArray1129[j16][l20][j23] = -1;
								}
							}
						}
						inStream.finishBitAccess();
						l2 = inStream.readUnsignedWord();
						aBoolean1159 = true;
					}
					if (anInt1069 == l2 && anInt1070 == i11 && loadingStage == 2)
					{
						pktType = -1;
						return true;
					}
					anInt1069 = l2;
					anInt1070 = i11;
					baseX = (anInt1069 - 6) * 8;
					baseY = (anInt1070 - 6) * 8;
					aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49) && anInt1070 / 8 == 48;
					if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
						aBoolean1141 = true;
					loadingStage = 1;
					aLong824 = System.currentTimeMillis();
					if (pktType == 73)
					{
						int k16 = 0;
						for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++)
						{
							for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
								k16++;
						}
						aByteArrayArray1183 = new byte[k16][];
						aByteArrayArray1247 = new byte[k16][];
						anIntArray1234 = new int[k16];
						anIntArray1235 = new int[k16];
						anIntArray1236 = new int[k16];
						k16 = 0;
						for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++)
						{
							for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++)
							{
								anIntArray1234[k16] = (l23 << 8) + j26;
								if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50
									|| l23 == 49 && j26 == 47))
								{
									anIntArray1235[k16] = -1;
									anIntArray1236[k16] = -1;
									k16++;
								}
								else
								{
									int k28 = anIntArray1235[k16] = onDemandFetcher.method562(0, j26, l23);
									if (k28 != -1)
										onDemandFetcher.method558(3, k28);
									int j30 = anIntArray1236[k16] = onDemandFetcher.method562(1, j26, l23);
									if (j30 != -1)
										onDemandFetcher.method558(3, j30);
									k16++;
								}
							}
						}
					}
					if (pktType == 241)
					{
						int l16 = 0;
						int[] ai = new int[676];
						for (int i24 = 0; i24 < 4; i24++)
						{
							for (int k26 = 0; k26 < 13; k26++)
							{
								for (int l28 = 0; l28 < 13; l28++)
								{
									int k30 = anIntArrayArrayArray1129[i24][k26][l28];
									if (k30 != -1)
									{
										int k31 = k30 >> 14 & 0x3ff;
										int i32 = k30 >> 3 & 0x7ff;
										int k32 = (k31 / 8 << 8) + i32 / 8;
										for (int j33 = 0; j33 < l16; j33++)
										{
											if (ai[j33] != k32)
												continue;
											k32 = -1;

										}
										if (k32 != -1)
											ai[l16++] = k32;
									}
								}
							}
						}
						aByteArrayArray1183 = new byte[l16][];
						aByteArrayArray1247 = new byte[l16][];
						anIntArray1234 = new int[l16];
						anIntArray1235 = new int[l16];
						anIntArray1236 = new int[l16];
						for (int l26 = 0; l26 < l16; l26++)
						{
							int i29 = anIntArray1234[l26] = ai[l26];
							int l30 = i29 >> 8 & 0xff;
							int l31 = i29 & 0xff;
							int j32 = anIntArray1235[l26] = onDemandFetcher.method562(0, l31, l30);
							if (j32 != -1)
								onDemandFetcher.method558(3, j32);
							int i33 = anIntArray1236[l26] = onDemandFetcher.method562(1, l31, l30);
							if (i33 != -1)
								onDemandFetcher.method558(3, i33);
						}
					}
					int i17 = baseX - anInt1036;
					int j21 = baseY - anInt1037;
					anInt1036 = baseX;
					anInt1037 = baseY;
					for (int j24 = 0; j24 < 16384; j24++)
					{
						Npc npc = npcArray[j24];
						if (npc != null)
						{
							for (int j29 = 0; j29 < 10; j29++)
							{
								npc.smallX[j29] -= i17;
								npc.smallY[j29] -= j21;
							}
							npc.x -= i17 * 128;
							npc.y -= j21 * 128;
						}
					}
					for (int i27 = 0; i27 < maxStoners; i27++)
					{
						Stoner stoner = stonerArray[i27];
						if (stoner != null)
						{
							for (int i31 = 0; i31 < 10; i31++)
							{
								stoner.smallX[i31] -= i17;
								stoner.smallY[i31] -= j21;
							}
							stoner.x -= i17 * 128;
							stoner.y -= j21 * 128;
						}
					}
					aBoolean1080 = true;
					byte byte1 = 0;
					byte byte2 = 104;
					byte byte3 = 1;
					if (i17 < 0)
					{
						byte1 = 103;
						byte2 = -1;
						byte3 = -1;
					}
					byte byte4 = 0;
					byte byte5 = 104;
					byte byte6 = 1;
					if (j21 < 0)
					{
						byte4 = 103;
						byte5 = -1;
						byte6 = -1;
					}
					for (int k33 = byte1; k33 != byte2; k33 += byte3)
					{
						for (int l33 = byte4; l33 != byte5; l33 += byte6)
						{
							int i34 = k33 + i17;
							int j34 = l33 + j21;
							for (int k34 = 0; k34 < 4; k34++)
								if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
									groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
								else
									groundArray[k34][k33][l33] = null;
						}
					}
					for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.reverseGetNext())
					{
						class30_sub1_1.anInt1297 -= i17;
						class30_sub1_1.anInt1298 -= j21;
						if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0
							|| class30_sub1_1.anInt1297 >= 208 || class30_sub1_1.anInt1298 >= 208)
							class30_sub1_1.unlink();
					}
					if (destX != 0)
					{
						destX -= i17;
						destY -= j21;
					}
					aBoolean1160 = false;
					pktType = -1;

					return true;

				case 208:
					int i3 = inStream.method437();
					if (i3 >= 0)
					{
						clearInterfaceAnimations(i3);
						walkableInterfaceMode = true;
					}
					else
						walkableInterfaceMode = false;
					anInt1018 = i3;
					pktType = -1;
					return true;

				case 99:
					anInt1021 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 75:
					int j3 = inStream.method436();
					int j11 = inStream.method436();
					RSInterface.interfaceCache[j11].anInt233 = 2;
					RSInterface.interfaceCache[j11].mediaID = j3;
					pktType = -1;
					return true;

				case 114:
					anInt1104 = inStream.method434() * 30;
					pktType = -1;
					return true;

				case 60:
					anInt1269 = inStream.readUnsignedByte();
					anInt1268 = inStream.method427();
					while (inStream.currentOffset < pktSize)
					{
						int k3 = inStream.readUnsignedByte();
						handleGroundItemUpdate(inStream, k3);
					}
					pktType = -1;
					return true;

				case 35:
					int l3 = inStream.readUnsignedByte();
					int k11 = inStream.readUnsignedByte();
					int j17 = inStream.readUnsignedByte();
					int k21 = inStream.readUnsignedByte();
					aBooleanArray876[l3] = true;
					anIntArray873[l3] = k11;
					anIntArray1203[l3] = j17;
					anIntArray928[l3] = k21;
					anIntArray1030[l3] = 0;
					pktType = -1;
					return true;

				case 174:
					int i4 = inStream.readUnsignedWord();
					int l11 = inStream.readUnsignedByte();
					int k17 = inStream.readUnsignedWord();
					if (soundProduction && !lowMem && anInt1062 < 50)
					{
						anIntArray1207[anInt1062] = i4;
						anIntArray1241[anInt1062] = l11;
						anIntArray1250[anInt1062] = k17;
						anInt1062++;
					}
					pktType = -1;
					return true;

				case 104:
					int j4 = inStream.method427();
					int i12 = inStream.method426();
					String s6 = inStream.readString();
					if (j4 >= 1 && j4 <= 5)
					{
						if (s6.equalsIgnoreCase("null"))
							s6 = null;
						atStonerActions[j4 - 1] = s6;
						atStonerArray[j4 - 1] = i12 == 0;
					}
					pktType = -1;
					return true;

				case 78:
					destX = 0;
					pktType = -1;
					return true;

				case 253:
					String s = inStream.readString();
					if (s.endsWith(" wishes to deal with you."))
					{
						String s3 = s.substring(0, s.indexOf(":"));
						long l17 = TextClass.longForName(s3);
						boolean flag2 = false;
						for (int j27 = 0; j27 < ignoreCount; j27++)
						{
							if (ignoreListAsLongs[j27] != l17)
								continue;
							flag2 = true;

						}
						if (!flag2 && anInt1251 == 0)
							pushMessage("wishes to deal with you.", 4, s3);

					}
					else if (s.startsWith(":defaultSettings:"))
					{
						SettingHandler.defaultSettings();
					}
					else if (s.startsWith(":saveSettings:"))
					{
						SettingHandler.save();
					}
					else if (s.startsWith(":transparentTab:"))
					{

						transparentTabArea = !transparentTabArea;

					}
					else if (s.startsWith(":transparentChatbox:"))
					{

						changeChatArea = !changeChatArea;

					}
					else if (s.startsWith(":sideStones:"))
					{

						changeTabArea = !changeTabArea;

					}
					else if (s.endsWith(":cult:"))
					{
						String s4 = s.substring(0, s.indexOf(":"));
						TextClass.longForName(s4);
						pushMessage("Cult: ", 8, s4);
					}
					else if (s.endsWith("#url#"))
					{
						String link = s.substring(0, s.indexOf("#"));
						pushMessage("Join us at: ", 9, link);
					}
					else if (s.endsWith(":duelreq:"))
					{
						String s4 = s.substring(0, s.indexOf(":"));
						long l18 = TextClass.longForName(s4);
						boolean flag3 = false;
						for (int k27 = 0; k27 < ignoreCount; k27++)
						{
							if (ignoreListAsLongs[k27] != l18)
								continue;
							flag3 = true;

						}
						if (!flag3 && anInt1251 == 0)
							pushMessage("wishes to duel with you.", 8, s4);
					}
					else if (s.endsWith(":chalreq:"))
					{
						String s5 = s.substring(0, s.indexOf(":"));
						long l19 = TextClass.longForName(s5);
						boolean flag4 = false;
						for (int l27 = 0; l27 < ignoreCount; l27++)
						{
							if (ignoreListAsLongs[l27] != l19)
								continue;
							flag4 = true;

						}
						if (!flag4 && anInt1251 == 0)
						{
							String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
							pushMessage(s8, 8, s5);
						}
					}
					else
					{
						pushMessage(s, 0, "");
					}
					pktType = -1;
					return true;

				case 1:
					for (int k4 = 0; k4 < stonerArray.length; k4++)
						if (stonerArray[k4] != null)
							stonerArray[k4].anim = -1;
					for (int j12 = 0; j12 < npcArray.length; j12++)
						if (npcArray[j12] != null)
							npcArray[j12].anim = -1;
					pktType = -1;
					return true;

				case 50:
					long l4 = inStream.readQWord();
					int i18 = inStream.readUnsignedByte();
					String s7 = TextClass.fixName(TextClass.nameForLong(l4));
					for (int k24 = 0; k24 < stonersCount; k24++)
					{
						if (l4 != stonersListAsLongs[k24])
							continue;
						if (stonersNodeIDs[k24] != i18)
						{
							stonersNodeIDs[k24] = i18;
							if (i18 >= 2)
							{
								pushMessage(s7 + " came to chill.", 5, "");
							}
							if (i18 <= 1)
							{
								pushMessage(s7 + " is too stoned.", 5, "");
							}
						}
						s7 = null;

					}
					if (s7 != null && stonersCount < 200)
					{
						stonersListAsLongs[stonersCount] = l4;
						stonersList[stonersCount] = s7;
						stonersNodeIDs[stonersCount] = i18;
						stonersCount++;
					}
					for (boolean flag6 = false; !flag6; )
					{
						flag6 = true;
						for (int k29 = 0; k29 < stonersCount - 1; k29++)
							if (stonersNodeIDs[k29] != nodeID && stonersNodeIDs[k29 + 1] == nodeID
								|| stonersNodeIDs[k29] == 0 && stonersNodeIDs[k29 + 1] != 0)
							{
								int j31 = stonersNodeIDs[k29];
								stonersNodeIDs[k29] = stonersNodeIDs[k29 + 1];
								stonersNodeIDs[k29 + 1] = j31;
								String s10 = stonersList[k29];
								stonersList[k29] = stonersList[k29 + 1];
								stonersList[k29 + 1] = s10;
								long l32 = stonersListAsLongs[k29];
								stonersListAsLongs[k29] = stonersListAsLongs[k29 + 1];
								stonersListAsLongs[k29 + 1] = l32;
								flag6 = false;
							}
					}
					pktType = -1;
					return true;

				case 110:
					if (tabID == 12)
					{
					}
					energy = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 254:
					anInt855 = inStream.readUnsignedByte();
					if (anInt855 == 1)
						anInt1222 = inStream.readUnsignedWord();
					if (anInt855 >= 2 && anInt855 <= 6)
					{
						if (anInt855 == 2)
						{
							anInt937 = 64;
							anInt938 = 64;
						}
						if (anInt855 == 3)
						{
							anInt937 = 0;
							anInt938 = 64;
						}
						if (anInt855 == 4)
						{
							anInt937 = 128;
							anInt938 = 64;
						}
						if (anInt855 == 5)
						{
							anInt937 = 64;
							anInt938 = 0;
						}
						if (anInt855 == 6)
						{
							anInt937 = 64;
							anInt938 = 128;
						}
						anInt855 = 2;
						anInt934 = inStream.readUnsignedWord();
						anInt935 = inStream.readUnsignedWord();
						anInt936 = inStream.readUnsignedByte();
					}
					if (anInt855 == 10)
						anInt933 = inStream.readUnsignedWord();
					pktType = -1;
					return true;

				case 248:
					int i5 = inStream.method435();
					int k12 = inStream.readUnsignedWord();
					if (backDialogID != -1)
					{
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0)
					{
						inputDialogState = 0;
						inputTaken = true;
					}
					openInterfaceID = i5;
					invOverlayInterfaceID = k12;
					tabAreaAltered = true;
					aBoolean1149 = false;
					pktType = -1;
					return true;

				case 79:
					int j5 = inStream.method434();
					int l12 = inStream.method435();
					RSInterface class9_3 = RSInterface.interfaceCache[j5];
					if (class9_3 != null && class9_3.type == 0)
					{
						if (l12 < 0)
							l12 = 0;
						if (l12 > class9_3.scrollMax - class9_3.height)
							l12 = class9_3.scrollMax - class9_3.height;
						class9_3.scrollPosition = l12;
					}
					pktType = -1;
					return true;

				case 68:
					for (int k5 = 0; k5 < variousSettings.length; k5++)
						if (variousSettings[k5] != anIntArray1045[k5])
						{
							variousSettings[k5] = anIntArray1045[k5];
							updateConfigValues(k5);
						}
					pktType = -1;
					return true;

				case 173:
					try
					{
						pushKill(inStream.readString(), inStream.readString(), inStream.readUnsignedWord(),
							inStream.readUnsignedByte() == 1);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					pktType = -1;
					return true;


				case 196:
					final long usernameHash = inStream.readQWord();
					int j18 = inStream.readDWord();
					int rights = inStream.readUnsignedByte();
					boolean flag5 = false;
					if (rights <= 1)
					{
						for (int l29 = 0; l29 < ignoreCount; l29++)
						{
							if (ignoreListAsLongs[l29] != usernameHash)
								continue;
							flag5 = true;

						}
					}
					if (!flag5 && anInt1251 == 0)
						try
						{
							anIntArray1240[anInt1169] = j18;
							anInt1169 = (anInt1169 + 1) % 100;
							String s9 = TextInput.method525(pktSize - 13, inStream);
							if (rights >= 1)
							{
								pushMessage(s9, 7,
									"@cr" + rights + "@" + TextClass.fixName(TextClass.nameForLong(usernameHash)));
							}
							else
							{
								pushMessage(s9, 3, TextClass.fixName(TextClass.nameForLong(usernameHash)));
							}
						}
						catch (Exception exception1)
						{
							Signlink.reporterror("cde1");
						}
					pktType = -1;
					return true;

				case 85:
					anInt1269 = inStream.method427();
					anInt1268 = inStream.method427();
					pktType = -1;
					return true;

				case 24:
					anInt1054 = inStream.method428();
					if (anInt1054 == tabID)
					{
						if (anInt1054 == 3)
							tabID = 1;
						else
							tabID = 3;
					}
					pktType = -1;
					return true;

				case 246:
					int i6 = inStream.method434();
					int i13 = inStream.readUnsignedWord();
					int k18 = inStream.readUnsignedWord();
					if (k18 == 65535)
					{
						RSInterface.interfaceCache[i6].anInt233 = 0;
						pktType = -1;
						return true;
					}
					else
					{
						ItemDef itemDef = ItemDef.getItemDefinition(k18);
						RSInterface.interfaceCache[i6].anInt233 = 4;
						RSInterface.interfaceCache[i6].mediaID = k18;
						RSInterface.interfaceCache[i6].modelRotation1 = itemDef.modelRotationY;
						RSInterface.interfaceCache[i6].modelRotation2 = itemDef.modelRotationX;
						RSInterface.interfaceCache[i6].modelZoom = (itemDef.modelZoom * 100) / i13;
						pktType = -1;
						return true;
					}

				case 171:
					boolean flag1 = inStream.readUnsignedByte() == 1;
					int j13 = inStream.readUnsignedWord();
					RSInterface.interfaceCache[j13].isMouseoverTriggered = flag1;
					pktType = -1;
					return true;

				case 142:
					int j6 = inStream.method434();
					clearInterfaceAnimations(j6);
					if (backDialogID != -1)
					{
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0)
					{
						inputDialogState = 0;
						inputTaken = true;
					}
					invOverlayInterfaceID = j6;
					tabAreaAltered = true;
					openInterfaceID = -1;
					aBoolean1149 = false;
					pktType = -1;
					return true;

				case 126:
					try {
						String text = inStream.readString();
						int frame = inStream.method435();

						DockPanelMapping mapping = DockPanelMapping.fromFrame(frame);
						if (mapping != null) {
							int index = mapping.indexFor(frame);
							SwingUtilities.invokeLater(() -> {
								UIPanel panel = UIDockFrame.getInstance().getPanel(mapping.panelID);
								if (panel instanceof DockTextUpdatable) {
									((DockTextUpdatable) panel).updateDockText(index, text);
								}
							});
						}


						// Text commands
						if (text.startsWith(":quicks:"))
							StatusOrbs.clickedQuickPrayers = text.substring(8).equalsIgnoreCase("on");
						if (text.startsWith(":prayer:"))
							prayerBook = text.substring(8);

						// Only call sendFrame126 if interface exists
						if (RSInterface.interfaceCache != null && frame >= 0 && frame < RSInterface.interfaceCache.length) {
							if (RSInterface.interfaceCache[frame] != null) {
								sendFrame126(text, frame);
								updateStrings(text, frame);
							}
						}

						// Other frame cases
						if (frame >= 18144 && frame <= 18244) {
							clanList[frame - 18144] = text;
						}
						if (frame == 8135) {
							if (RSInterface.interfaceCache[8135] != null)
								RSInterface.interfaceCache[8135].opacity = 108;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					pktType = -1;
					return true;


				case 124:
					try
					{
						int npc = inStream.readUnsignedByte();
						int size = inStream.readUnsignedByte();
						if (npc <= 0)
						{
							npcDisplay = null;
							pktType = -1;
							return true;
						}
						npcDisplay = new Npc();
						npcDisplay.desc = EntityDef.forID(npc, false);
						npcDisplay.anInt1540 = npcDisplay.desc.aByte68;
						npcDisplay.anInt1504 = npcDisplay.desc.anInt79;
						npcDisplay.desc.modelWidth = size;
						npcDisplay.desc.modelHeight = size;
						npcDisplay.anInt1511 = npcDisplay.desc.standAnim;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					pktType = -1;
					return true;

				case 127:
					try
					{
						int skill = inStream.readSignedByte();
						int exp = inStream.readDWord();
						StatusOrbs.xpCounter = inStream.readDWord();
						addXP(skill, exp);
					}
					catch (Exception e)
					{
					}
					pktType = -1;
					return true;

				case 125:
					try
					{
						int skill = inStream.readSignedByte();
						statsSkillGoal[skill][2] = inStream.readDWord();
						statsSkillGoal[skill][0] = inStream.readDWord();
						statsSkillGoal[skill][1] = inStream.readSignedByte();
					}
					catch (Exception e)
					{
					}
					pktType = -1;
					return true;

				case 202:
					// FREE AND OPEN PROTOCOL
					pktType = -1;
					return true;

				case 201:
					try
					{
						stonerIndex = inStream.readUnsignedWord();
					}
					catch (Exception e)
					{
					}
					pktType = -1;
					return true;

				case 206:
					publicChatMode = inStream.readUnsignedByte();
					privateChatMode = inStream.readUnsignedByte();
					tradeMode = inStream.readUnsignedByte();
					inputTaken = true;
					pktType = -1;
					return true;

				case 240:
					if (tabID == 12)
					{
					}
					weight = inStream.readSignedWord();
					pktType = -1;
					return true;

				case 8:
					int k6 = inStream.method436();
					int l13 = inStream.readUnsignedWord();
					RSInterface.interfaceCache[k6].anInt233 = 1;
					RSInterface.interfaceCache[k6].mediaID = l13;
					pktType = -1;
					return true;

				case 122:
					int intId = inStream.method436();
					int r = inStream.readUnsignedByte();
					int gByte = inStream.readUnsignedByte();
					int b = inStream.readUnsignedByte();
					if (RSInterface.interfaceCache[intId] != null)
					{
						RSInterface.interfaceCache[intId].textColor = r << 16 | gByte << 8 | b;
					}
					pktType = -1;
					return true;

				case 53:
					int i7 = inStream.readUnsignedWord();
					RSInterface class9_1 = RSInterface.interfaceCache[i7];
					int j19 = inStream.readUnsignedWord();
					for (int j22 = 0; j22 < j19; j22++)
					{
						int i25 = inStream.readUnsignedByte();
						if (i25 == 255)
							i25 = inStream.method440();
						class9_1.inv[j22] = inStream.method436();
						class9_1.invStackSizes[j22] = i25;
					}
					for (int j25 = j19; j25 < class9_1.inv.length; j25++)
					{
						class9_1.inv[j25] = 0;
						class9_1.invStackSizes[j25] = 0;
					}
					if (class9_1.contentType == 206)
					{
						for (int tab = 0; tab < 10; tab++)
						{
							int amount = inStream.readSignedByte() << 8 | inStream.readUnsignedWord();
							tabAmounts[tab] = amount;
						}

						if (variousSettings[1012] == 1)
						{
							RSInterface bank = RSInterface.interfaceCache[5382];
							Arrays.fill(bankInvTemp, 0);
							Arrays.fill(bankStackTemp, 0);
							for (int slot = 0, bankSlot = 0; slot < bank.inv.length; slot++)
							{
								if (bank.inv[slot] - 1 > 0)
								{
									if (ItemDef.getItemDefinition(bank.inv[slot] - 1).name.toLowerCase()
										.contains(promptInput.toLowerCase()))
									{
										bankInvTemp[bankSlot] = bank.inv[slot];
										bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
									}
								}
							}
						}
					}
					pktType = -1;
					return true;

				case 230:
					int j7 = inStream.method435();
					int j14 = inStream.readUnsignedWord();
					int k19 = inStream.readUnsignedWord();
					int k22 = inStream.method436();
					RSInterface.interfaceCache[j14].modelRotation1 = k19;
					RSInterface.interfaceCache[j14].modelRotation2 = k22;
					RSInterface.interfaceCache[j14].modelZoom = j7;
					pktType = -1;
					return true;

				case 221:
					anInt900 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 177:
					aBoolean1160 = true;
					anInt995 = inStream.readUnsignedByte();
					anInt996 = inStream.readUnsignedByte();
					anInt997 = inStream.readUnsignedWord();
					anInt998 = inStream.readUnsignedByte();
					anInt999 = inStream.readUnsignedByte();
					if (anInt999 >= 100)
					{
						int k7 = anInt995 * 128 + 64;
						int k14 = anInt996 * 128 + 64;
						int i20 = getTerrainHeight(plane, k14, k7) - anInt997;
						int l22 = k7 - xCameraPos;
						int k25 = i20 - zCameraPos;
						int j28 = k14 - yCameraPos;
						int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
						yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
						xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
						if (yCameraCurve < 128)
							yCameraCurve = 128;
						if (yCameraCurve > 383)
							yCameraCurve = 383;
					}
					pktType = -1;
					return true;

				case 249:
					anInt1046 = inStream.method426();
					unknownInt10 = inStream.method436();
					pktType = -1;
					return true;

				case 65:
					updateNPCs(inStream, pktSize);
					pktType = -1;
					return true;

				case 27:
					messagePromptRaised = false;
					inputDialogState = 1;
					amountOrNameInput = "";
					inputTaken = true;
					pktType = -1;
					return true;

				case 187:
					messagePromptRaised = false;
					inputDialogState = 2;
					amountOrNameInput = "";
					inputTaken = true;
					pktType = -1;
					return true;

				case 97:
					int l7 = inStream.readUnsignedWord();
					clearInterfaceAnimations(l7);
					if (invOverlayInterfaceID != -1)
					{
						invOverlayInterfaceID = -1;
						tabAreaAltered = true;
					}
					if (backDialogID != -1)
					{
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0)
					{
						inputDialogState = 0;
						inputTaken = true;
					}
					openInterfaceID = l7;
					aBoolean1149 = false;
					pktType = -1;
					return true;

				case 218:
					int i8 = inStream.method438();
					dialogID = i8;
					inputTaken = true;
					pktType = -1;
					return true;

				case 87:
					int j8 = inStream.method434();
					int l14 = inStream.method439();
					anIntArray1045[j8] = l14;
					if (variousSettings[j8] != l14)
					{
						variousSettings[j8] = l14;
						updateConfigValues(j8);
						if (dialogID != -1)
							inputTaken = true;
					}
					pktType = -1;
					return true;

				case 36:
					int k8 = inStream.method434();
					byte byte0 = inStream.readSignedByte();
					anIntArray1045[k8] = byte0;
					if (variousSettings[k8] != byte0)
					{
						variousSettings[k8] = byte0;
						updateConfigValues(k8);
						if (dialogID != -1)
							inputTaken = true;
					}
					pktType = -1;
					return true;

				case 61:
					anInt1055 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 200:
					int l8 = inStream.readUnsignedWord();
					int i15 = inStream.readSignedWord();
					RSInterface class9_4 = RSInterface.interfaceCache[l8];
					class9_4.anInt257 = i15;
					pktType = -1;
					return true;

				case 219:
					if (invOverlayInterfaceID != -1)
					{
						invOverlayInterfaceID = -1;
						tabAreaAltered = true;
					}
					if (backDialogID != -1)
					{
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0)
					{
						inputDialogState = 0;
						inputTaken = true;
					}
					openInterfaceID = -1;
					aBoolean1149 = false;
					pktType = -1;
					return true;

				case 34:
					int i9 = inStream.readUnsignedWord();
					RSInterface class9_2 = RSInterface.interfaceCache[i9];
					while (inStream.currentOffset < pktSize)
					{
						int j20 = inStream.method422();
						int i23 = inStream.readUnsignedWord();
						int l25 = inStream.readUnsignedByte();
						if (l25 == 255)
							l25 = inStream.readDWord();
						if (j20 >= 0 && j20 < class9_2.inv.length)
						{
							class9_2.inv[j20] = i23;
							class9_2.invStackSizes[j20] = l25;
						}
					}
					pktType = -1;
					return true;

				case 4:
				case 44:
				case 84:
				case 101:
				case 105:
				case 117:
				case 147:
				case 151:
				case 156:
				case 160:
				case 215:
					handleGroundItemUpdate(inStream, pktType);
					pktType = -1;
					return true;

				case 106:
					tabID = inStream.method427();
					tabAreaAltered = true;
					pktType = -1;
					return true;

				case 164:
					int j9 = inStream.method434();
					clearInterfaceAnimations(j9);
					if (invOverlayInterfaceID != -1)
					{
						invOverlayInterfaceID = -1;
						tabAreaAltered = true;
					}
					backDialogID = j9;
					inputTaken = true;
					openInterfaceID = -1;
					aBoolean1149 = false;
					pktType = -1;
					return true;

			}
			Signlink.reporterror("T1 - " + pktType + "," + pktSize + " - " + anInt842 + "," + anInt843);
			resetLogout();
		}
		catch (IOException _ex)
		{
			//no-op
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			String s2 = "T2 - " + pktType + "," + anInt842 + "," + anInt843 + " - " + pktSize + ","
				+ (baseX + myStoner.smallX[0]) + "," + (baseY + myStoner.smallY[0]) + " - ";
			for (int j15 = 0; j15 < pktSize && j15 < 50; j15++)
				s2 = s2 + inStream.buffer[j15] + ",";
			Signlink.reporterror(s2);
			resetLogout();
		}
		pktType = -1;
		return true;
	}

	public static void sendPacket(int packet)
	{
		if (packet == 103)
		{
			stream.createFrame(103);
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
		}

		if (packet == 1003)
		{
			stream.createFrame(103);
			inputString = "::" + inputString;
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
		}
	}
}
