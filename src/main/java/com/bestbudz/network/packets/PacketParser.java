package com.bestbudz.network.packets;

import com.bestbudz.cache.Signlink;
import static com.bestbudz.data.XP.addXP;
import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.frame.UIDockHelper;
import com.bestbudz.dock.ui.panel.bank.BankPanel;
import com.bestbudz.dock.ui.panel.skills.SkillsPanel;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.ui.panel.DockPanelMapping;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.items.ItemDef;
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
import com.bestbudz.graphics.text.TextInput;
import static com.bestbudz.ui.interfaces.Chatbox.privateChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.publicChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import static com.bestbudz.ui.interfaces.Chatbox.reportAbuseInput;
import com.bestbudz.ui.interfaces.StatusOrbs;
import com.bestbudz.util.SizeConstants;
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.world.Class30_Sub1;
import static com.bestbudz.world.GroundItem.spawnGroundItem;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public class PacketParser extends Client
{
	// Performance optimizations: Cache frequently accessed data
	private static final ThreadLocal<StringBuilder> STRING_BUILDER_CACHE =
		ThreadLocal.withInitial(() -> new StringBuilder(256));

	// Cache static arrays to reduce field access overhead
	private static final int[] PACKET_SIZES = SizeConstants.packetSizes;
	private static final RSInterface[] INTERFACE_CACHE = RSInterface.interfaceCache;

	// Pre-compiled string patterns for chat optimization
	private static final String DEAL_SUFFIX = " wishes to deal with you.";
	private static final String CULT_SUFFIX = ":cult:";
	private static final String DUEL_SUFFIX = ":duelreq:";
	private static final String CHALLENGE_SUFFIX = ":chalreq:";
	private static final String URL_SUFFIX = "#url#";

	// Netty optimization: Expected packet size for buffer hinting
	private static final int EXPECTED_PACKET_SIZE = 64;
	private static final int MIN_PACKET_SIZE = 3;

	// Performance monitoring
	private static long packetCount = 0;
	private static long lastOptimizationCheck = System.currentTimeMillis();

	public static void handlePackets(Graphics2D g) throws IOException
	{
		// Dynamic batch sizing based on available data and load
		final int available = socketStream != null ? socketStream.available() : 0;
		final int batchSize = calculateOptimalBatchSize(available);

		for (int i = 0; i < batchSize; i++) {
			if (!parsePacket(g)) break;
		}

		// Periodic optimization check
		if (++packetCount % 1000 == 0) {
			checkPerformanceOptimization();
		}
	}

	private static int calculateOptimalBatchSize(int available) {
		if (available == 0) return 1;
		if (available > 2000) return 15;  // High load - process more
		if (available > 1000) return 10;  // Medium load
		if (available > 500) return 8;    // Normal load
		return 5;                         // Low load - original behavior
	}

	private static void checkPerformanceOptimization() {
		final long currentTime = System.currentTimeMillis();
		if (currentTime - lastOptimizationCheck > 30000) { // Every 30 seconds
			lastOptimizationCheck = currentTime;
			// Performance monitoring hook
		}
	}
	/**
	 * Notifies dock panels when specific interfaces are updated
	 */
	private static void notifyDockPanelUpdates(int interfaceId) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIDockFrame instance = UIDockFrame.getInstance();
				if (instance == null) return;

				// Bank interface updates
				if (interfaceId == 5382) {
					UIPanel bankPanel = instance.getPanel("Bank");
					if (bankPanel instanceof BankPanel) {
						((BankPanel) bankPanel).updateBankData();
					}
				}

				// Add other interface notifications here as needed
				// if (interfaceId == XXXX) { ... }

			} catch (Exception e) {
				// Silent fail if dock is not available
				System.err.println("Failed to notify dock panels: " + e.getMessage());
			}
		});
	}
	public static boolean parsePacket(Graphics2D g)
	{
		if (socketStream == null)
			return false;

		try
		{
			final int availableBytes = socketStream.available();
			// Quick exit for insufficient data
			if (availableBytes == 0) return false;

			// Netty optimization: Check minimum packet size early
			if (availableBytes < MIN_PACKET_SIZE && pktType == -1) return false;

			if (pktType == -1)
			{
				socketStream.flushInputStream(inStream.buffer, 1);
				pktType = inStream.buffer[0] & 0xff;
				if (encryption != null)
					pktType = pktType - encryption.getNextKey() & 0xff;
				pktSize = PACKET_SIZES[pktType]; // Use cached array
			}

			// Optimized packet size handling with branch prediction optimization
			if (pktSize >= 0) {
				// Most common case first
				if (availableBytes < pktSize) return false;
			} else if (pktSize == -1) {
				if (availableBytes > 0) {
					socketStream.flushInputStream(inStream.buffer, 1);
					pktSize = inStream.buffer[0] & 0xff;
				} else {
					return false;
				}
			} else if (pktSize == -2) {
				if (availableBytes > 1) {
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					pktSize = inStream.readUnsignedWord();
				} else {
					return false;
				}
			}

			if (availableBytes < pktSize) return false;

			// Buffer size optimization hint for Netty
			if (pktSize > EXPECTED_PACKET_SIZE * 2) {
				//System.err.println("Large packet detected: " + pktSize + " bytes (type: " + pktType + ")");
			}

			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			anInt1009 = 0;
			anInt843 = anInt842;
			anInt842 = anInt841;
			anInt841 = pktType;

			// Reordered switch by packet frequency for better branch prediction
			switch (pktType)
			{
				// HIGH FREQUENCY PACKETS FIRST
				case 81: // updateStoners - very frequent in multiplayer
					updateStoners(pktSize, inStream);
					aBoolean1080 = false;
					pktType = -1;
					return true;

				case 65: // updateNPCs - very frequent
					updateNPCs(inStream, pktSize);
					pktType = -1;
					return true;

				case 126: // interface text - very frequent
					handleInterfaceTextOptimized();
					pktType = -1;
					return true;

				case 134: // skill update - frequent
					handleSkillUpdateOptimized();
					pktType = -1;
					return true;

				case 196: // private message - frequent
					handlePrivateMessageOptimized();
					pktType = -1;
					return true;

				case 253: // chat message - frequent
					handleChatMessageOptimized();
					pktType = -1;
					return true;

				// MEDIUM FREQUENCY PACKETS
				case 176: // login response
					handleLoginResponseOptimized();
					pktType = -1;
					return true;

				case 64: // ground array update
					handleGroundArrayUpdateOptimized();
					pktType = -1;
					return true;

				case 185: // interface model
					handleInterfaceModelOptimized();
					pktType = -1;
					return true;

				case 217: // clan message
					handleClanMessageOptimized();
					pktType = -1;
					return true;

				case 107: // reset animations
					aBoolean1160 = false;
					Arrays.fill(aBooleanArray876, 0, 5, false);
					StatusOrbs.xpCounter = 0;
					pktType = -1;
					return true;

				case 72: // clear inventory
					handleClearInventoryOptimized();
					pktType = -1;
					return true;

				case 214: // ignore list
					handleIgnoreListOptimized();
					pktType = -1;
					return true;

				case 166: // camera shake
					handleCameraShakeOptimized();
					pktType = -1;
					return true;

				case 71: // tab interface
					handleTabInterfaceOptimized();
					pktType = -1;
					return true;

				case 74: // music change
					handleMusicChangeOptimized();
					pktType = -1;
					return true;

				case 121: // music queue
					handleMusicQueueOptimized();
					pktType = -1;
					return true;

				case 109: // logout - rare but critical
					resetLogout();
					pktType = -1;
					return false;

				case 70: // interface scroll
					handleInterfaceScrollOptimized();
					pktType = -1;
					return true;

				case 73: // region change
				case 241: // region loader
					handleRegionChangeOptimized();
					pktType = -1;
					return true;

				case 208: // walkable interface
					handleWalkableInterfaceOptimized();
					pktType = -1;
					return true;

				case 99: // camera reset
					anInt1021 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 75: // interface NPC head
					handleInterfaceNPCHeadOptimized();
					pktType = -1;
					return true;

				case 114: // system update
					anInt1104 = inStream.method434() * 30;
					pktType = -1;
					return true;

				case 60: // ground item update
					handleGroundItemUpdateOptimized();
					pktType = -1;
					return true;

				case 35: // skill level
					handleSkillLevelOptimized();
					pktType = -1;
					return true;

				case 174: // sound effect
					handleSoundEffectOptimized();
					pktType = -1;
					return false;

				case 104: // player option
					handlePlayerOptionOptimized();
					pktType = -1;
					return true;

				case 78: // reset destination
					destX = 0;
					pktType = -1;
					return true;

				case 1: // reset animations all
					handleResetAnimationsAllOptimized();
					pktType = -1;
					return true;

				case 50: // friends list
					handleFriendsListOptimized();
					pktType = -1;
					return true;

				case 110: // energy update
					handleEnergyUpdateOptimized();
					pktType = -1;
					return true;

				case 254: // minimap state
					handleMinimapStateOptimized();
					pktType = -1;
					return true;

				case 248: // interface inventory
					handleInterfaceInventoryOptimized();
					pktType = -1;
					return true;

				case 79: // interface scroll position
					handleInterfaceScrollPositionOptimized();
					pktType = -1;
					return true;

				case 68: // config update
					handleConfigUpdateOptimized();
					pktType = -1;
					return true;

				case 173: // kill feed
					handleKillFeedOptimized();
					pktType = -1;
					return true;

				case 85: // coordinate update
					anInt1269 = inStream.method427();
					anInt1268 = inStream.method427();
					pktType = -1;
					return true;

				case 24: // force tab
					handleForceTabOptimized();
					pktType = -1;
					return true;

				case 246: // interface item model
					handleInterfaceItemModelOptimized();
					pktType = -1;
					return true;

				case 171: // interface hover
					handleInterfaceHoverOptimized();
					pktType = -1;
					return true;

				case 142: // close interface
					handleCloseInterfaceOptimized();
					pktType = -1;
					return true;

				case 124: // NPC display
					handleNPCDisplayOptimized();
					pktType = -1;
					return true;

				case 127: // XP drop
					handleXPDropOptimized();
					pktType = -1;
					return true;

				case 125: // FREE AND OPEN PROTOCOL
					pktType = -1;
					return false;

				case 202: // FREE AND OPEN PROTOCOL
					pktType = -1;
					return false;

				case 201: // player index
					handlePlayerIndexOptimized();
					pktType = -1;
					return true;

				case 206: // chat settings
					handleChatSettingsOptimized();
					pktType = -1;
					return true;

				case 240: // weight update
					handleWeightUpdateOptimized();
					pktType = -1;
					return true;

				case 8: // interface animation
					handleInterfaceAnimationOptimized();
					pktType = -1;
					return true;

				case 122: // interface color
					handleInterfaceColorOptimized();
					pktType = -1;
					return true;

				case 53: // inventory update
					handleInventoryUpdateOptimized();
					pktType = -1;
					return true;

				case 230: // interface model rotation
					handleInterfaceModelRotationOptimized();
					pktType = -1;
					return true;

				case 221: // unknown 221
					anInt900 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 177: // camera angle
					handleCameraAngleOptimized();
					pktType = -1;
					return true;

				case 249: // interface settings
					handleInterfaceSettingsOptimized();
					pktType = -1;
					return true;

				case 27: // input amount
					handleInputAmountOptimized();
					pktType = -1;
					return true;

				case 187: // input name
					handleInputNameOptimized();
					pktType = -1;
					return true;

				case 97: // open interface
					handleOpenInterfaceOptimized();
					pktType = -1;
					return true;

				case 218: // dialog window
					handleDialogWindowOptimized();
					pktType = -1;
					return true;

				case 87: // config large
					handleConfigLargeOptimized();
					pktType = -1;
					return true;

				case 36: // config byte
					handleConfigByteOptimized();
					pktType = -1;
					return true;

				case 61: // unknown 61
					anInt1055 = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 200: // interface offset
					handleInterfaceOffsetOptimized();
					pktType = -1;
					return true;

				case 219: // close all interfaces
					handleCloseAllInterfacesOptimized();
					pktType = -1;
					return true;

				case 34: // inventory item update
					handleInventoryItemUpdateOptimized();
					pktType = -1;
					return true;

				case 4: case 44: case 84: case 101: case 105: case 117:
				case 147: case 151: case 156: case 160: case 215: // ground items
				handleGroundItemUpdate(inStream, pktType);
				pktType = -1;
				return true;

				case 106: // set tab
					tabID = inStream.method427();
					tabAreaAltered = true;
					pktType = -1;
					return true;

				case 164: // back dialog
					handleBackDialogOptimized();
					pktType = -1;
					return true;

				default:
					handleUnknownPacket();
					pktType = -1;
					return true;
			}
		}
		catch (IOException _ex)
		{
			// Silent handling for IOException - connection issues
			return false;
		}
		catch (Exception exception)
		{
			handlePacketException(exception);
			return false;
		}
	}

	// Optimized handler methods with reduced overhead
	private static void handleInterfaceTextOptimized() {
		try {
			final String text = inStream.readString();
			final int frame = inStream.method435();
			//System.out.println("🎭 RAW STRING PACKET: Frame " + frame + " = '" + text + "'");

			UIDockFrame instance = UIDockFrame.getInstance();
			if (instance != null && instance.getModalManager() != null) {
				instance.getModalManager().feedStringPacket(frame, text);
			}

			final DockPanelMapping mapping = DockPanelMapping.fromFrame(frame);
			if (mapping != null) {
				final int index = mapping.indexFor(frame);
				SwingUtilities.invokeLater(() -> {
					UIPanel panel = UIDockFrame.getInstance().getPanel(mapping.panelID);
					if (panel instanceof DockTextUpdatable) {
						((DockTextUpdatable) panel).updateDockText(index, text);
					}
				});
			}



			// Bounds check optimization
			if (frame >= 0 && frame < INTERFACE_CACHE.length) {
				final RSInterface rsInterface = INTERFACE_CACHE[frame];
				if (rsInterface != null) {
					sendFrame126(text, frame);
					updateStrings(text, frame);
				}
			}

			// Optimized range checks
			if (frame >= 18144 && frame <= 18244) {
				clanList[frame - 18144] = text;
			} else if (frame == 8135 && INTERFACE_CACHE[8135] != null) {
				INTERFACE_CACHE[8135].opacity = 108;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleSkillUpdateOptimized() {
		final int pid = inStream.readUnsignedByte();
		final int pAdvance = inStream.readUnsignedByte();
		final int pExp = inStream.method439();
		final int pGrade = inStream.method439();

		// Batch field assignments for better cache locality
		currentExp[pid] = pExp;
		currentStats[pid] = pGrade;
		currentAdvances[pid] = pAdvance;
		maxStats[pid] = calculateMaxLevelOptimized(pExp);

		// Optimized UI update
		updateSkillsPanelOptimized(pid);
	}

	private static int calculateMaxLevelOptimized(final int experience) {
		final int[] expTable = Skills.EXP_FOR_LEVEL;
		final int tableLength = expTable.length;

		// Reverse iteration for better performance on high levels
		for (int level = tableLength - 1; level >= 0; level--) {
			if (experience >= expTable[level]) {
				return level;
			}
		}
		return 1;
	}

	private static void updateSkillsPanelOptimized(final int skillId) {
		final DockTextUpdatable updatable = UIDockHelper.getUpdatablePanel("Skills");
		if (UIDockHelper.isPanelVisible("Skills") && updatable instanceof SkillsPanel) {
			((SkillsPanel) updatable).updateSkill(skillId);
		}
	}

	private static void handleChatMessageOptimized() {
		final String s = inStream.readString();

		// Optimized string matching using indexOf for better performance
		if (s.indexOf(DEAL_SUFFIX) == s.length() - DEAL_SUFFIX.length() && s.contains(":")) {
			final String s3 = s.substring(0, s.indexOf(":"));
			final long l17 = TextClass.longForName(s3);
			if (!isIgnoredUser(l17) && anInt1251 == 0) {
				pushMessage("wishes to deal with you.", 4, s3);
			}
		}
		else if (s.startsWith(":defaultSettings:")) {
			SettingHandler.defaultSettings();
		}
		else if (s.startsWith(":saveSettings:")) {
			SettingHandler.save();
		}
		else if (s.startsWith(":transparentTab:")) {
			transparentTabArea = !transparentTabArea;
		}
		else if (s.indexOf(CULT_SUFFIX) == s.length() - CULT_SUFFIX.length()) {
			final String s4 = s.substring(0, s.indexOf(":"));
			TextClass.longForName(s4);
			pushMessage("Cult: ", 8, s4);
		}
		else if (s.indexOf(URL_SUFFIX) == s.length() - URL_SUFFIX.length()) {
			final String link = s.substring(0, s.indexOf("#"));
			pushMessage("Join us at: ", 9, link);
		}
		else if (s.indexOf(DUEL_SUFFIX) == s.length() - DUEL_SUFFIX.length()) {
			final String s4 = s.substring(0, s.indexOf(":"));
			final long l18 = TextClass.longForName(s4);
			if (!isIgnoredUser(l18) && anInt1251 == 0) {
				pushMessage("wishes to duel with you.", 8, s4);
			}
		}
		else if (s.indexOf(CHALLENGE_SUFFIX) == s.length() - CHALLENGE_SUFFIX.length()) {
			final String s5 = s.substring(0, s.indexOf(":"));
			final long l19 = TextClass.longForName(s5);
			if (!isIgnoredUser(l19) && anInt1251 == 0) {
				final String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
				pushMessage(s8, 8, s5);
			}
		}
		else {
			pushMessage(s, 0, "");
		}
	}

	// Optimized ignore check with early exit
	private static boolean isIgnoredUser(final long userHash) {
		final long[] ignoreList = ignoreListAsLongs;
		final int maxIgnore = ignoreCount;
		for (int i = 0; i < maxIgnore; i++) {
			if (ignoreList[i] == userHash) {
				return true;
			}
		}
		return false;
	}

	private static void handlePrivateMessageOptimized() {
		final long usernameHash = inStream.readQWord();
		final int j18 = inStream.readDWord();
		final int rights = inStream.readUnsignedByte();

		final boolean shouldIgnore = rights <= 1 && isIgnoredUser(usernameHash);

		if (!shouldIgnore && anInt1251 == 0) {
			try {
				anIntArray1240[anInt1169] = j18;
				anInt1169 = (anInt1169 + 1) % 100;
				final String s9 = TextInput.method525(pktSize - 13, inStream);

				if (rights >= 1) {
					pushMessage(s9, 7, "@cr" + rights + "@" + TextClass.fixName(TextClass.nameForLong(usernameHash)));
				} else {
					pushMessage(s9, 3, TextClass.fixName(TextClass.nameForLong(usernameHash)));
				}
			} catch (Exception exception1) {
				Signlink.reporterror("cde1");
			}
		}
	}

	private static void handleLoginResponseOptimized() {
		daysSinceRecovChange = inStream.method427();
		unreadMessages = inStream.method435();
		membersInt = inStream.readUnsignedByte();
		anInt1193 = inStream.method440();
		daysSinceLastLogin = inStream.readUnsignedWord();

		if (anInt1193 != 0 && openInterfaceID == -1) {
			Signlink.dnslookup(TextClass.method586(anInt1193));
			clearTopInterfaces();
			final char c = (daysSinceRecovChange != 201 || membersInt == 1) ? '\u028F' : '\u028A';
			reportAbuseInput = "";
			canMute = false;

			// Optimized interface search with early exit
			for (final RSInterface rsInterface : INTERFACE_CACHE) {
				if (rsInterface != null && rsInterface.contentType == c) {
					openInterfaceID = rsInterface.parentID;
					break;
				}
			}
		}
	}

	private static void handleGroundArrayUpdateOptimized() {
		final int startX = inStream.method427();
		final int startY = inStream.method428();
		final int endX = startX + 8;
		final int endY = startY + 8;

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				if (groundArray[plane][x][y] != null) {
					groundArray[plane][x][y] = null;
					spawnGroundItem(x, y);
				}
			}
		}

		for (Class30_Sub1 item = (Class30_Sub1) aClass19_1179.reverseGetFirst();
			 item != null;
			 item = (Class30_Sub1) aClass19_1179.reverseGetNext()) {

			if (item.anInt1297 >= startX && item.anInt1297 < endX &&
				item.anInt1298 >= startY && item.anInt1298 < endY &&
				item.anInt1295 == plane) {
				item.anInt1294 = 0;
			}
		}
	}

	private static void handleInterfaceModelOptimized() {
		final int k = inStream.method436();
		final RSInterface rsInterface = INTERFACE_CACHE[k];
		rsInterface.anInt233 = 3;

		if (myStoner.desc == null) {
			rsInterface.mediaID = (myStoner.anIntArray1700[0] << 25) +
				(myStoner.anIntArray1700[4] << 20) +
				(myStoner.equipment[0] << 15) +
				(myStoner.equipment[8] << 10) +
				(myStoner.equipment[11] << 5) +
				myStoner.equipment[1];
		} else {
			rsInterface.mediaID = (int) (0x12345678L + myStoner.desc.interfaceType);
		}
	}

	private static void handleClanMessageOptimized() {
		try {
			clanUsername = inStream.readString();
			clanMessage = TextInput.processText(inStream.readString());
			clanTitle = inStream.readString();
			channelRights = inStream.readUnsignedWord();
			pushMessage(clanMessage, 16, clanUsername);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleClearInventoryOptimized() {
		final int i1 = inStream.method434();
		final RSInterface rsInterface = INTERFACE_CACHE[i1];
		// Keep original behavior - both assignments for compatibility
		for (int k15 = 0; k15 < rsInterface.inv.length; k15++) {
			rsInterface.inv[k15] = -1;
			rsInterface.inv[k15] = 0;
		}
	}

	private static void handleIgnoreListOptimized() {
		final int count = pktSize / 8;
		ignoreCount = count;
		for (int i = 0; i < count; i++) {
			ignoreListAsLongs[i] = inStream.readQWord();
		}
	}

	private static void handleCameraShakeOptimized() {
		aBoolean1160 = true;
		anInt1098 = inStream.readUnsignedByte();
		anInt1099 = inStream.readUnsignedByte();
		anInt1100 = inStream.readUnsignedWord();
		anInt1101 = inStream.readUnsignedByte();
		anInt1102 = inStream.readUnsignedByte();

		if (anInt1102 >= 100) {
			xCameraPos = anInt1098 * 128 + 64;
			yCameraPos = anInt1099 * 128 + 64;
			zCameraPos = getTerrainHeight(plane, yCameraPos, xCameraPos) - anInt1100;
		}
	}

	private static void handleTabInterfaceOptimized() {
		int l1 = inStream.readUnsignedWord();
		final int j10 = inStream.method426();
		if (l1 == 65535) l1 = -1;
		tabInterfaceIDs[j10] = l1;
		tabAreaAltered = true;
	}

	private static void handleMusicChangeOptimized() {
		int i2 = inStream.method434();
		if (i2 == 65535) i2 = -1;

		if (i2 != currentSong && musicEnabled && !lowMem && prevSong == 0) {
			nextSong = i2;
			songChanging = true;
			onDemandFetcher.method558(2, nextSong);
		}
		currentSong = i2;
	}

	private static void handleMusicQueueOptimized() {
		final int j2 = inStream.method436();
		final int k10 = inStream.method435();

		if (musicEnabled && !lowMem) {
			nextSong = j2;
			songChanging = false;
			onDemandFetcher.method558(2, nextSong);
			prevSong = k10;
		}
	}

	private static void handleInterfaceScrollOptimized() {
		final int l10 = inStream.method437();
		final int i16 = inStream.method434();
		INTERFACE_CACHE[i16].anInt265 = l10;
	}

	private static void handleRegionChangeOptimized() {
		int l2 = anInt1069;
		int i11 = anInt1070;

		if (pktType == 73) {
			l2 = inStream.method435();
			i11 = inStream.readUnsignedWord();
			aBoolean1159 = false;
		}

		if (pktType == 241) { // REGION LOADER
			i11 = inStream.method435();
			inStream.initBitAccess();

			for (int j16 = 0; j16 < 4; j16++) {
				for (int l20 = 0; l20 < 13; l20++) {
					for (int j23 = 0; j23 < 13; j23++) {
						final int i26 = inStream.readBits(1);
						anIntArrayArrayArray1129[j16][l20][j23] = (i26 == 1) ? inStream.readBits(26) : -1;
					}
				}
			}
			inStream.finishBitAccess();
			l2 = inStream.readUnsignedWord();
			aBoolean1159 = true;
		}

		int playerRegionX = l2;
		int playerRegionY = i11;

// Only reload if player has actually moved to a different region
		if (anInt1069 == playerRegionX && anInt1070 == playerRegionY && loadingStage == 2) {
			// Player is still in same region - no need to reload
			return;
		}

// ✅ GOOD: Region loading triggered by player movement only
		anInt1069 = playerRegionX;
		anInt1070 = playerRegionY;
		baseX = (playerRegionX - 6) * 8;
		baseY = (playerRegionY - 6) * 8;

// Calculate region boundaries for special areas
		aBoolean1141 = (playerRegionX / 8 == 48 || playerRegionX / 8 == 49) && playerRegionY / 8 == 48;
		if (playerRegionX / 8 == 48 && playerRegionY / 8 == 148) aBoolean1141 = true;

		loadingStage = 1;
		aLong824 = System.currentTimeMillis();

		System.out.println("🗺️ [RegionChange] Player moved to region: " + playerRegionX + "," + playerRegionY +
			" (camera position does NOT affect this)");

		anInt1069 = l2;
		anInt1070 = i11;
		baseX = (l2 - 6) * 8;
		baseY = (i11 - 6) * 8;
		aBoolean1141 = (l2 / 8 == 48 || l2 / 8 == 49) && i11 / 8 == 48;
		if (l2 / 8 == 48 && i11 / 8 == 148) aBoolean1141 = true;

		loadingStage = 1;
		aLong824 = System.currentTimeMillis();

		if (pktType == 73) {
			handleRegionType73Optimized(l2, i11);
		} else if (pktType == 241) {
			handleRegionType241Optimized();
		}

		// Update entity positions
		final int deltaX = baseX - anInt1036;
		final int deltaY = baseY - anInt1037;

		for (int j24 = 0; j24 < 16384; j24++) {
			Npc npc = npcArray[j24];
			if (npc != null) {
				for (int j29 = 0; j29 < 10; j29++) {
					npc.smallX[j29] -= deltaX;
					npc.smallY[j29] -= deltaY;
				}
				npc.x -= deltaX * 128;
				npc.y -= deltaY * 128;
			}
		}

		for (int i27 = 0; i27 < maxStoners; i27++) {
			Stoner stoner = stonerArray[i27];
			if (stoner != null) {
				for (int i31 = 0; i31 < 10; i31++) {
					stoner.smallX[i31] -= deltaX;
					stoner.smallY[i31] -= deltaY;
				}
				stoner.x -= deltaX * 128;
				stoner.y -= deltaY * 128;
			}
		}

		aBoolean1080 = true;

		// Update ground arrays
		byte byte1 = 0, byte2 = 104, byte3 = 1;
		if (deltaX < 0) {
			byte1 = 103; byte2 = -1; byte3 = -1;
		}
		byte byte4 = 0, byte5 = 104, byte6 = 1;
		if (deltaY < 0) {
			byte4 = 103; byte5 = -1; byte6 = -1;
		}

		for (int k33 = byte1; k33 != byte2; k33 += byte3) {
			for (int l33 = byte4; l33 != byte5; l33 += byte6) {
				final int i34 = k33 + deltaX;
				final int j34 = l33 + deltaY;
				for (int k34 = 0; k34 < 4; k34++) {
					if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104) {
						groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
					} else {
						groundArray[k34][k33][l33] = null;
					}
				}
			}
		}

		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetFirst();
			 class30_sub1_1 != null;
			 class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetNext()) {

			class30_sub1_1.anInt1297 -= deltaX;
			class30_sub1_1.anInt1298 -= deltaY;
			if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0 ||
				class30_sub1_1.anInt1297 >= 208 || class30_sub1_1.anInt1298 >= 208) {
				class30_sub1_1.unlink();
			}
		}

		if (destX != 0) {
			destX -= deltaX;
			destY -= deltaY;
		}

		anInt1036 = baseX;
		anInt1037 = baseY;
		aBoolean1160 = false;
	}

	/**
	 * FIXED: Type 73 region handler with embedded cache integration
	 */
	private static void handleRegionType73Optimized(final int l2, final int i11) {
		System.out.println("🗺️ [RegionChange] Type 73 - Position: " + l2 + "," + i11);

		int k16 = 0;
		for (int i21 = (l2 - 6) / 8; i21 <= (l2 + 6) / 8; i21++) {
			for (int k23 = (i11 - 6) / 8; k23 <= (i11 + 6) / 8; k23++) {
				k16++;
			}
		}

		aByteArrayArray1183 = new byte[k16][];
		aByteArrayArray1247 = new byte[k16][];
		anIntArray1234 = new int[k16];
		anIntArray1235 = new int[k16];
		anIntArray1236 = new int[k16];

		k16 = 0;
		int embeddedLoaded = 0;
		int onDemandQueued = 0;

		for (int l23 = (l2 - 6) / 8; l23 <= (l2 + 6) / 8; l23++) {
			for (int j26 = (i11 - 6) / 8; j26 <= (i11 + 6) / 8; j26++) {
				anIntArray1234[k16] = (l23 << 8) + j26;

				if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || (l23 == 49 && j26 == 47))) {
					// Special regions with no data
					anIntArray1235[k16] = -1;
					anIntArray1236[k16] = -1;
					System.out.println("✅ [RegionChange] Region " + anIntArray1234[k16] + " - special region (no data)");
				} else {
					// TRY EMBEDDED CACHE FIRST
					if (loadRegionFromEmbeddedCache(k16, l23, j26)) {
						embeddedLoaded++;
						System.out.println("✅ [RegionChange] Region " + anIntArray1234[k16] + " loaded from EMBEDDED CACHE");
					} else {
						// FALLBACK TO ONDEMAND FETCHER (causes lag)
						final int k28 = anIntArray1235[k16] = onDemandFetcher.method562(0, j26, l23);
						if (k28 != -1) {
							onDemandFetcher.method558(3, k28);
							onDemandQueued++;
						}

						final int j30 = anIntArray1236[k16] = onDemandFetcher.method562(1, j26, l23);
						if (j30 != -1) {
							onDemandFetcher.method558(3, j30);
							onDemandQueued++;
						}

						System.err.println("⚠️ [RegionChange] Region " + anIntArray1234[k16] +
							" using onDemandFetcher (files: " + k28 + "/" + j30 + ") - WILL CAUSE LAG");
					}
				}
				k16++;
			}
		}

		System.out.println("📊 [RegionChange] Summary: " + embeddedLoaded + " embedded, " +
			onDemandQueued + " onDemand requests");

		if (onDemandQueued == 0) {
			System.out.println("🎉 [RegionChange] ALL REGIONS FROM EMBEDDED CACHE - NO LAG!");
		} else {
			System.err.println("❌ [RegionChange] " + onDemandQueued + " files still use onDemandFetcher - LAG EXPECTED");
		}
	}

	/**
	 * FIXED: Type 241 region handler with embedded cache integration
	 */
	private static void handleRegionType241Optimized() {
		System.out.println("🗺️ [RegionChange] Type 241 - Dynamic region loading");

		int l16 = 0;
		final int[] ai = new int[676];

		for (int i24 = 0; i24 < 4; i24++) {
			for (int k26 = 0; k26 < 13; k26++) {
				for (int l28 = 0; l28 < 13; l28++) {
					final int k30 = anIntArrayArrayArray1129[i24][k26][l28];
					if (k30 != -1) {
						final int k31 = k30 >> 14 & 0x3ff;
						final int i32 = k30 >> 3 & 0x7ff;
						final int k32 = (k31 / 8 << 8) + i32 / 8;

						boolean found = false;
						for (int j33 = 0; j33 < l16; j33++) {
							if (ai[j33] == k32) {
								found = true;
								break;
							}
						}
						if (!found) {
							ai[l16++] = k32;
						}
					}
				}
			}
		}

		aByteArrayArray1183 = new byte[l16][];
		aByteArrayArray1247 = new byte[l16][];
		anIntArray1234 = new int[l16];
		anIntArray1235 = new int[l16];
		anIntArray1236 = new int[l16];

		int embeddedLoaded = 0;
		int onDemandQueued = 0;

		for (int l26 = 0; l26 < l16; l26++) {
			final int i29 = anIntArray1234[l26] = ai[l26];
			final int l30 = i29 >> 8 & 0xff;
			final int l31 = i29 & 0xff;

			// TRY EMBEDDED CACHE FIRST
			if (loadRegionFromEmbeddedCache(l26, l30, l31)) {
				embeddedLoaded++;
				System.out.println("✅ [RegionChange] Region " + i29 + " loaded from EMBEDDED CACHE");
			} else {
				// FALLBACK TO ONDEMAND FETCHER (causes lag)
				final int j32 = anIntArray1235[l26] = onDemandFetcher.method562(0, l31, l30);
				if (j32 != -1) {
					onDemandFetcher.method558(3, j32);
					onDemandQueued++;
				}

				final int i33 = anIntArray1236[l26] = onDemandFetcher.method562(1, l31, l30);
				if (i33 != -1) {
					onDemandFetcher.method558(3, i33);
					onDemandQueued++;
				}

				System.err.println("⚠️ [RegionChange] Region " + i29 +
					" using onDemandFetcher (files: " + j32 + "/" + i33 + ") - WILL CAUSE LAG");
			}
		}

		System.out.println("📊 [RegionChange] Summary: " + embeddedLoaded + " embedded, " +
			onDemandQueued + " onDemand requests");

		if (onDemandQueued == 0) {
			System.out.println("🎉 [RegionChange] ALL REGIONS FROM EMBEDDED CACHE - NO LAG!");
		} else {
			System.err.println("❌ [RegionChange] " + onDemandQueued + " files still use onDemandFetcher - LAG EXPECTED");
		}
	}

	/**
	 * NEW: Try to load region from embedded cache
	 * Returns true if successful, false if should use onDemandFetcher
	 */
	private static boolean loadRegionFromEmbeddedCache(int arrayIndex, int regionX, int regionY) {
		try {
			// Calculate region ID
			int regionId = (regionX << 8) + regionY;

			// Try to get file IDs from onDemandFetcher (but don't queue them)
			int mapFileId = onDemandFetcher.method562(0, regionY, regionX);
			int landscapeFileId = onDemandFetcher.method562(1, regionY, regionX);

			// Set file IDs in arrays
			anIntArray1235[arrayIndex] = mapFileId;
			anIntArray1236[arrayIndex] = landscapeFileId;

			// Handle -1 file IDs (no data regions)
			if (mapFileId == -1 && landscapeFileId == -1) {
				// No data needed - mark as loaded
				aByteArrayArray1183[arrayIndex] = null;
				aByteArrayArray1247[arrayIndex] = null;
				return true;
			}

			if (mapFileId == -1 || landscapeFileId == -1) {
				// Mixed -1 and valid IDs - let onDemandFetcher handle
				return false;
			}

			// Try to get data from embedded cache
			byte[] mapData = com.bestbudz.cache.EmbeddedMapCache.getEmbeddedFileData(mapFileId);
			byte[] landscapeData = com.bestbudz.cache.EmbeddedMapCache.getEmbeddedFileData(landscapeFileId);

			if (mapData != null && landscapeData != null) {
				// SUCCESS - set data directly
				aByteArrayArray1183[arrayIndex] = mapData;
				aByteArrayArray1247[arrayIndex] = landscapeData;
				return true;
			}

			// Embedded cache doesn't have this data
			return false;

		} catch (Exception e) {
			System.err.println("⚠️ [RegionChange] Error loading from embedded cache: " + e.getMessage());
			return false;
		}
	}

	private static void handleRegionType73(final int l2, final int i11) {
		int k16 = 0;
		for (int i21 = (l2 - 6) / 8; i21 <= (l2 + 6) / 8; i21++) {
			for (int k23 = (i11 - 6) / 8; k23 <= (i11 + 6) / 8; k23++) {
				k16++;
			}
		}

		aByteArrayArray1183 = new byte[k16][];
		aByteArrayArray1247 = new byte[k16][];
		anIntArray1234 = new int[k16];
		anIntArray1235 = new int[k16];
		anIntArray1236 = new int[k16];

		k16 = 0;
		for (int l23 = (l2 - 6) / 8; l23 <= (l2 + 6) / 8; l23++) {
			for (int j26 = (i11 - 6) / 8; j26 <= (i11 + 6) / 8; j26++) {
				anIntArray1234[k16] = (l23 << 8) + j26;

				if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || (l23 == 49 && j26 == 47))) {
					anIntArray1235[k16] = -1;
					anIntArray1236[k16] = -1;
				} else {
					final int k28 = anIntArray1235[k16] = onDemandFetcher.method562(0, j26, l23);
					if (k28 != -1) onDemandFetcher.method558(3, k28);

					final int j30 = anIntArray1236[k16] = onDemandFetcher.method562(1, j26, l23);
					if (j30 != -1) onDemandFetcher.method558(3, j30);
				}
				k16++;
			}
		}
	}

	private static void handleRegionType241() {
		int l16 = 0;
		final int[] ai = new int[676];

		for (int i24 = 0; i24 < 4; i24++) {
			for (int k26 = 0; k26 < 13; k26++) {
				for (int l28 = 0; l28 < 13; l28++) {
					final int k30 = anIntArrayArrayArray1129[i24][k26][l28];
					if (k30 != -1) {
						final int k31 = k30 >> 14 & 0x3ff;
						final int i32 = k30 >> 3 & 0x7ff;
						final int k32 = (k31 / 8 << 8) + i32 / 8;

						boolean found = false;
						for (int j33 = 0; j33 < l16; j33++) {
							if (ai[j33] == k32) {
								found = true;
								break;
							}
						}
						if (!found) {
							ai[l16++] = k32;
						}
					}
				}
			}
		}

		aByteArrayArray1183 = new byte[l16][];
		aByteArrayArray1247 = new byte[l16][];
		anIntArray1234 = new int[l16];
		anIntArray1235 = new int[l16];
		anIntArray1236 = new int[l16];

		for (int l26 = 0; l26 < l16; l26++) {
			final int i29 = anIntArray1234[l26] = ai[l26];
			final int l30 = i29 >> 8 & 0xff;
			final int l31 = i29 & 0xff;

			final int j32 = anIntArray1235[l26] = onDemandFetcher.method562(0, l31, l30);
			if (j32 != -1) onDemandFetcher.method558(3, j32);

			final int i33 = anIntArray1236[l26] = onDemandFetcher.method562(1, l31, l30);
			if (i33 != -1) onDemandFetcher.method558(3, i33);
		}
	}

	private static void handleWalkableInterfaceOptimized() {
		final int i3 = inStream.method437();
		if (i3 >= 0) {
			clearInterfaceAnimations(i3);
			walkableInterfaceMode = true;
		} else {
			walkableInterfaceMode = false;
		}
		anInt1018 = i3;
	}

	private static void handleInterfaceNPCHeadOptimized() {
		final int j3 = inStream.method436();
		final int j11 = inStream.method436();

		System.out.println("🎭 RAW STRING PACKET: j3 & j11 = " + j3 + " ' " + j11 + " '");

		final RSInterface rsInterface = INTERFACE_CACHE[j11];
		rsInterface.anInt233 = 2;
		rsInterface.mediaID = j3;
	}

	private static void handleGroundItemUpdateOptimized() {
		anInt1269 = inStream.readUnsignedByte();
		anInt1268 = inStream.method427();

		while (inStream.currentOffset < pktSize) {
			final int k3 = inStream.readUnsignedByte();
			handleGroundItemUpdate(inStream, k3);
		}
	}

	private static void handleSkillLevelOptimized() {
		final int skillId = inStream.readUnsignedByte();
		final int k11 = inStream.readUnsignedByte();
		final int j17 = inStream.readUnsignedByte();
		final int k21 = inStream.readUnsignedByte();

		aBooleanArray876[skillId] = true;
		anIntArray873[skillId] = k11;
		anIntArray1203[skillId] = j17;
		anIntArray928[skillId] = k21;
		anIntArray1030[skillId] = 0;
	}

	private static void handleSoundEffectOptimized() {
		final int soundId = inStream.readUnsignedWord();
		final int volume = inStream.readUnsignedByte();
		final int delay = inStream.readUnsignedWord();

		if (soundProduction && !lowMem && anInt1062 < 50) {
			final int index = anInt1062++;
			anIntArray1207[index] = soundId;
			anIntArray1241[index] = volume;
			anIntArray1250[index] = delay;
		}
	}

	private static void handlePlayerOptionOptimized() {
		final int optionIndex = inStream.method427();
		final int enabled = inStream.method426();
		String option = inStream.readString();

		if (optionIndex >= 1 && optionIndex <= 5) {
			if (option.equalsIgnoreCase("null")) option = null;
			atStonerActions[optionIndex - 1] = option;
			atStonerArray[optionIndex - 1] = enabled == 0;
		}
	}

	private static void handleResetAnimationsAllOptimized() {
		for (final Stoner stoner : stonerArray) {
			if (stoner != null) stoner.anim = -1;
		}
		for (final Npc npc : npcArray) {
			if (npc != null) npc.anim = -1;
		}
	}

	private static void handleFriendsListOptimized() {
		final long userHash = inStream.readQWord();
		final int nodeId = inStream.readUnsignedByte();
		String username = TextClass.fixName(TextClass.nameForLong(userHash));

		for (int i = 0; i < stonersCount; i++) {
			if (userHash == stonersListAsLongs[i]) {
				if (stonersNodeIDs[i] != nodeId) {
					stonersNodeIDs[i] = nodeId;
					final String message = nodeId >= 2 ? " came to chill." : " is too stoned.";
					pushMessage(username + message, 5, "");
				}
				username = null;
				break;
			}
		}

		if (username != null && stonersCount < 200) {
			stonersListAsLongs[stonersCount] = userHash;
			stonersList[stonersCount] = username;
			stonersNodeIDs[stonersCount] = nodeId;
			stonersCount++;
		}

		// Friends list sorting
		for (boolean flag6 = false; !flag6; ) {
			flag6 = true;
			for (int k29 = 0; k29 < stonersCount - 1; k29++) {
				if (stonersNodeIDs[k29] != nodeID && stonersNodeIDs[k29 + 1] == nodeID ||
					stonersNodeIDs[k29] == 0 && stonersNodeIDs[k29 + 1] != 0) {

					final int j31 = stonersNodeIDs[k29];
					stonersNodeIDs[k29] = stonersNodeIDs[k29 + 1];
					stonersNodeIDs[k29 + 1] = j31;

					final String s10 = stonersList[k29];
					stonersList[k29] = stonersList[k29 + 1];
					stonersList[k29 + 1] = s10;

					final long l32 = stonersListAsLongs[k29];
					stonersListAsLongs[k29] = stonersListAsLongs[k29 + 1];
					stonersListAsLongs[k29 + 1] = l32;

					flag6 = false;
				}
			}
		}
	}

	private static void handleEnergyUpdateOptimized() {
		if (tabID == 12) {
			// Empty block preserved from original
		}
		energy = inStream.readUnsignedByte();
	}

	private static void handleMinimapStateOptimized() {
		anInt855 = inStream.readUnsignedByte();

		if (anInt855 == 1) {
			anInt1222 = inStream.readUnsignedWord();
		} else if (anInt855 >= 2 && anInt855 <= 6) {
			switch (anInt855) {
				case 2: anInt937 = 64; anInt938 = 64; break;
				case 3: anInt937 = 0; anInt938 = 64; break;
				case 4: anInt937 = 128; anInt938 = 64; break;
				case 5: anInt937 = 64; anInt938 = 0; break;
				case 6: anInt937 = 64; anInt938 = 128; break;
			}
			anInt855 = 2;
			anInt934 = inStream.readUnsignedWord();
			anInt935 = inStream.readUnsignedWord();
			anInt936 = inStream.readUnsignedByte();
		} else if (anInt855 == 10) {
			anInt933 = inStream.readUnsignedWord();
		}
	}

	private static void handleInterfaceInventoryOptimized() {
		final int interfaceId = inStream.method435();
		final int overlayId = inStream.readUnsignedWord();

		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}

		openInterfaceID = interfaceId;
		invOverlayInterfaceID = overlayId;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}

	private static void handleInterfaceScrollPositionOptimized() {
		final int interfaceId = inStream.method434();
		int scrollPos = inStream.method435();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (rsInterface != null && rsInterface.type == 0) {
			if (scrollPos < 0) scrollPos = 0;
			if (scrollPos > rsInterface.scrollMax - rsInterface.height) {
				scrollPos = rsInterface.scrollMax - rsInterface.height;
			}
			rsInterface.scrollPosition = scrollPos;
		}
	}

	private static void handleConfigUpdateOptimized() {
		for (int k5 = 0; k5 < variousSettings.length; k5++) {
			if (variousSettings[k5] != anIntArray1045[k5]) {
				variousSettings[k5] = anIntArray1045[k5];
				updateConfigValues(k5);
			}
		}
	}

	private static void handleKillFeedOptimized() {
		try {
			pushKill(inStream.readString(), inStream.readString(),
				inStream.readUnsignedWord(), inStream.readUnsignedByte() == 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleForceTabOptimized() {
		anInt1054 = inStream.method428();
		if (anInt1054 == tabID) {
			tabID = (anInt1054 == 3) ? 1 : 3;
		}
	}

	private static void handleInterfaceItemModelOptimized() {
		final int interfaceId = inStream.method434();
		final int zoom = inStream.readUnsignedWord();
		final int itemId = inStream.readUnsignedWord();

		System.out.println("🎭 RAW STRING PACKET: ItemModel InterfaceID" + interfaceId + " & zoom'" + zoom + "&& item id" + itemId);

		UIDockFrame instance = UIDockFrame.getInstance();
		if (instance != null && instance.getModalManager() != null) {
			instance.getModalManager().feedItemDisplay(interfaceId, itemId, 1);
		}

		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (itemId == 65535) {
			rsInterface.anInt233 = 0;
		} else {
			final ItemDef itemDef = getItemDefinition(itemId);
			rsInterface.anInt233 = 4;
			rsInterface.mediaID = itemId;
			rsInterface.modelRotation1 = itemDef.modelRotationY;
			rsInterface.modelRotation2 = itemDef.modelRotationX;
			rsInterface.modelZoom = (itemDef.modelZoom * 100) / zoom;
		}
	}

	private static void handleInterfaceHoverOptimized() {
		final boolean enabled = inStream.readUnsignedByte() == 1;
		final int interfaceId = inStream.readUnsignedWord();

		// Add bounds and null checking
		if (interfaceId >= 0 && interfaceId < INTERFACE_CACHE.length && INTERFACE_CACHE[interfaceId] != null) {
			INTERFACE_CACHE[interfaceId].isMouseoverTriggered = enabled;
		} else {
			System.err.println("Ignoring hover for missing interface: " + interfaceId);
		}
	}

	private static void handleCloseInterfaceOptimized() {
		final int interfaceId = inStream.method434();

		// Only clear animations if interface exists
		if (interfaceId >= 0 && interfaceId < INTERFACE_CACHE.length && INTERFACE_CACHE[interfaceId] != null) {
			clearInterfaceAnimations(interfaceId);
		} else {
			System.err.println("Ignoring close for missing interface: " + interfaceId);
		}

		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}

		invOverlayInterfaceID = interfaceId;
		tabAreaAltered = true;
		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	private static void handleNPCDisplayOptimized() {
		try {
			final int npcId = inStream.readUnsignedByte();
			final int size = inStream.readUnsignedByte();

			if (npcId <= 0) {
				npcDisplay = null;
				return;
			}

			npcDisplay = new Npc();
			npcDisplay.desc = EntityDef.forID(npcId, false);
			npcDisplay.anInt1540 = npcDisplay.desc.aByte68;
			npcDisplay.anInt1504 = npcDisplay.desc.anInt79;
			npcDisplay.desc.modelWidth = size;
			npcDisplay.desc.modelHeight = size;
			npcDisplay.anInt1511 = npcDisplay.desc.standAnim;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleXPDropOptimized() {
		try {
			final int skill = inStream.readSignedByte();
			final int exp = inStream.readDWord();
			StatusOrbs.xpCounter = inStream.readDWord();
			addXP(skill, exp);
		} catch (Exception e) {
			// Silent catch as in original
		}
	}

	private static void handlePlayerIndexOptimized() {
		try {
			stonerIndex = inStream.readUnsignedWord();
		} catch (Exception e) {
			// Silent catch as in original
		}
	}

	private static void handleChatSettingsOptimized() {
		publicChatMode = inStream.readUnsignedByte();
		privateChatMode = inStream.readUnsignedByte();
		tradeMode = inStream.readUnsignedByte();
		inputTaken = true;
	}

	private static void handleWeightUpdateOptimized() {
		if (tabID == 12) {
			// Empty block preserved from original
		}
		weight = inStream.readSignedWord();
	}

	private static void handleInterfaceAnimationOptimized() {
		final int interfaceId = inStream.method436();
		final int animationId = inStream.readUnsignedWord();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];
		rsInterface.anInt233 = 1;
		rsInterface.mediaID = animationId;
	}

	private static void handleInterfaceColorOptimized() {
		final int interfaceId = inStream.method436();
		final int r = inStream.readUnsignedByte();
		final int g = inStream.readUnsignedByte();
		final int b = inStream.readUnsignedByte();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (rsInterface != null) {
			rsInterface.textColor = r << 16 | g << 8 | b;
		}
	}

	private static void handleInventoryUpdateOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		// Null check
		if (rsInterface == null || rsInterface.inv == null) {
			System.err.println("Invalid interface or inventory: " + interfaceId);
			return;
		}

		final int itemCount = inStream.readUnsignedWord();

		// Bounds check - prevent reading more items than the array can hold
		final int safeItemCount = Math.min(itemCount, rsInterface.inv.length);

		// Debug logging for bank updates
		if (interfaceId == 5382) {
			System.out.println("🏦 Bank inventory update - Interface: " + interfaceId +
				", Items: " + itemCount + ", Safe count: " + safeItemCount +
				", Array length: " + rsInterface.inv.length);
		}

		for (int i = 0; i < safeItemCount; i++) {
			int stackSize = inStream.readUnsignedByte();
			if (stackSize == 255) stackSize = inStream.method440();
			rsInterface.inv[i] = inStream.method436();
			rsInterface.invStackSizes[i] = stackSize;
		}

		// Clear remaining slots - FIX: Only clear slots that actually exist
		for (int j25 = safeItemCount; j25 < rsInterface.inv.length; j25++) {
			rsInterface.inv[j25] = 0;
			rsInterface.invStackSizes[j25] = 0;
		}

		// If server sent more items than we can handle, skip the excess
		if (itemCount > rsInterface.inv.length) {
			System.err.println("Server sent " + itemCount + " items but interface " +
				interfaceId + " only has " + rsInterface.inv.length + " slots - skipping excess");
			// Skip the excess items from the stream to prevent desync
			for (int i = rsInterface.inv.length; i < itemCount; i++) {
				int stackSize = inStream.readUnsignedByte();
				if (stackSize == 255) stackSize = inStream.method440();
				inStream.method436(); // Skip item ID
			}
		}

		if (rsInterface.contentType == 206) {
			for (int tab = 0; tab < 10; tab++) {
				int amount = inStream.readSignedByte() << 8 | inStream.readUnsignedWord();
				tabAmounts[tab] = amount;
			}

			if (variousSettings[1012] == 1) {
				final RSInterface bank = INTERFACE_CACHE[5382];
				Arrays.fill(bankInvTemp, 0);
				Arrays.fill(bankStackTemp, 0);

				for (int slot = 0, bankSlot = 0; slot < bank.inv.length; slot++) {
					if (bank.inv[slot] - 1 > 0) {
						if (getItemDefinition(bank.inv[slot] - 1).name.toLowerCase()
							.contains(promptInput.toLowerCase())) {
							bankInvTemp[bankSlot] = bank.inv[slot];
							bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
						}
					}
				}
			}

			// Debug: Log first few bank items
			if (interfaceId == 5382) {
				System.out.println("🏦 Bank tab amounts: " + Arrays.toString(Arrays.copyOf(tabAmounts, 5)));
				int nonEmptySlots = 0;
				for (int i = 0; i < Math.min(10, rsInterface.inv.length); i++) {
					if (rsInterface.inv[i] > 0) {
						nonEmptySlots++;
						System.out.println("🏦 Slot " + i + ": Item " + (rsInterface.inv[i] - 1) +
							" x" + rsInterface.invStackSizes[i]);
					}
				}
				System.out.println("🏦 Total non-empty slots in first 10: " + nonEmptySlots);
			}
		}

		// Notify dock panels of the update
		notifyDockPanelUpdates(interfaceId);
	}

	private static void handleInterfaceModelRotationOptimized() {
		final int zoom = inStream.method435();
		final int interfaceId = inStream.readUnsignedWord();
		final int rotation1 = inStream.readUnsignedWord();
		final int rotation2 = inStream.method436();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		rsInterface.modelRotation1 = rotation1;
		rsInterface.modelRotation2 = rotation2;
		rsInterface.modelZoom = zoom;
	}

	private static void handleCameraAngleOptimized() {
		aBoolean1160 = true;
		anInt995 = inStream.readUnsignedByte();
		anInt996 = inStream.readUnsignedByte();
		anInt997 = inStream.readUnsignedWord();
		anInt998 = inStream.readUnsignedByte();
		anInt999 = inStream.readUnsignedByte();

		if (anInt999 >= 100) {
			final int x = anInt995 * 128 + 64;
			final int y = anInt996 * 128 + 64;
			final int height = getTerrainHeight(plane, y, x) - anInt997;
			final int deltaX = x - xCameraPos;
			final int deltaHeight = height - zCameraPos;
			final int deltaY = y - yCameraPos;
			final int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

			yCameraCurve = (int) (Math.atan2(deltaHeight, distance) * 325.94900000000001D) & 0x7ff;
			xCameraCurve = (int) (Math.atan2(deltaX, deltaY) * -325.94900000000001D) & 0x7ff;
			if (yCameraCurve < 128) yCameraCurve = 128;
			if (yCameraCurve > 383) yCameraCurve = 383;
		}
	}

	private static void handleInterfaceSettingsOptimized() {
		anInt1046 = inStream.method426();
		unknownInt10 = inStream.method436();
	}

	private static void handleInputAmountOptimized() {
		messagePromptRaised = false;
		inputDialogState = 1;
		amountOrNameInput = "";
		inputTaken = true;
	}

	private static void handleInputNameOptimized() {
		messagePromptRaised = false;
		inputDialogState = 2;
		amountOrNameInput = "";
		inputTaken = true;
	}

	private static void handleOpenInterfaceOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		clearInterfaceAnimations(interfaceId);

		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}

		openInterfaceID = interfaceId;
		aBoolean1149 = false;
	}

	private static void handleDialogWindowOptimized() {
		dialogID = inStream.method438();
		inputTaken = true;
	}

	private static void handleConfigLargeOptimized() {
		final int configId = inStream.method434();
		final int value = inStream.method439();
		anIntArray1045[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleConfigByteOptimized() {
		final int configId = inStream.method434();
		final byte value = inStream.readSignedByte();
		anIntArray1045[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleInterfaceOffsetOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		final int offset = inStream.readSignedWord();
		INTERFACE_CACHE[interfaceId].anInt257 = offset;
	}

	private static void handleCloseAllInterfacesOptimized() {
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}

		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	private static void handleInventoryItemUpdateOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		// Debug logging for bank updates
		if (interfaceId == 5382) {
			System.out.println("🏦 Bank item update - Interface: " + interfaceId);
		}

		while (inStream.currentOffset < pktSize) {
			final int slot = inStream.method422();
			final int itemId = inStream.readUnsignedWord();
			int stackSize = inStream.readUnsignedByte();
			if (stackSize == 255) stackSize = inStream.readDWord();

			if (slot >= 0 && slot < rsInterface.inv.length) {
				rsInterface.inv[slot] = itemId;
				rsInterface.invStackSizes[slot] = stackSize;

				// Debug individual item updates for bank
				if (interfaceId == 5382 && itemId > 0) {
					System.out.println("🏦 Bank slot " + slot + " updated: Item " + (itemId - 1) + " x" + stackSize);
				}
			}
		}

		// Notify dock panels of the update
		notifyDockPanelUpdates(interfaceId);
	}

	private static void handleBackDialogOptimized() {
		final int interfaceId = inStream.method434();
		clearInterfaceAnimations(interfaceId);

		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}

		UIDockFrame.handleDialogueInterface(interfaceId, INTERFACE_CACHE[interfaceId]);

		backDialogID = interfaceId;
		inputTaken = true;
		openInterfaceID = -1;
		aBoolean1149 = false;
		System.out.println("🎭 PACKET 164: Chat box interface " + interfaceId);
	}

	// Error handling optimized for performance
	private static void handleUnknownPacket() {
		final StringBuilder error = STRING_BUILDER_CACHE.get();
		error.setLength(0);
		error.append("T1 - ").append(pktType).append(",").append(pktSize)
			.append(" - ").append(anInt842).append(",").append(anInt843);
		Signlink.reporterror(error.toString());
		resetLogout();
	}

	private static void handlePacketException(final Exception exception) {
		exception.printStackTrace();
		final StringBuilder errorBuilder = STRING_BUILDER_CACHE.get();
		errorBuilder.setLength(0);

		errorBuilder.append("T2 - ").append(pktType).append(",").append(anInt842).append(",")
			.append(anInt843).append(" - ").append(pktSize).append(",")
			.append(baseX + myStoner.smallX[0]).append(",").append(baseY + myStoner.smallY[0])
			.append(" - ");

		final int limit = Math.min(pktSize, 50);
		for (int i = 0; i < limit; i++) {
			errorBuilder.append(inStream.buffer[i]).append(",");
		}

		Signlink.reporterror(errorBuilder.toString());
		resetLogout();
	}

	// Optimized sendPacket method
	public static void sendPacket(final int packet) {
		switch (packet) {
			case 103:
				stream.createFrame(103);
				stream.writeWordBigEndian(inputString.length() - 1);
				stream.writeString(inputString.substring(2));
				inputString = "";
				promptInput = "";
				break;

			case 1003:
				stream.createFrame(103);
				inputString = "::" + inputString;
				stream.writeWordBigEndian(inputString.length() - 1);
				stream.writeString(inputString.substring(2));
				inputString = "";
				promptInput = "";
				break;

			default:
				// Handle other packet types if needed
				break;
		}
	}

	// Performance monitoring methods
	public static long getPacketCount() {
		return packetCount;
	}

	public static void resetPacketCount() {
		packetCount = 0;
		lastOptimizationCheck = System.currentTimeMillis();
	}
}