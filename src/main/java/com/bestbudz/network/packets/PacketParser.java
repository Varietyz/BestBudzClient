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
import static com.bestbudz.engine.core.gamerender.GroundItems.handleGroundItemUpdate;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.data.Skills;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.PacketSender.setInterfaceText;
import static com.bestbudz.network.packets.PacketSender.updateInterfaceText;
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
import com.bestbudz.world.SpotAnimationNode;
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
					inStream.position = 0;
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

			inStream.position = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			idleTimeout = 0;
			regionY = regionX;
			regionX = regionUpdateCount;
			regionUpdateCount = pktType;

			// Reordered switch by packet frequency for better branch prediction
			switch (pktType)
			{
				// HIGH FREQUENCY PACKETS FIRST
				case 81: // updateStoners - very frequent in multiplayer
					updateStoners(pktSize, inStream);
					friendsListVisible = false;
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
					cutsceneActive = false;
					Arrays.fill(cameraShakeEnabled, 0, 5, false);
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
					lastActionTime = inStream.readUnsignedByte();
					pktType = -1;
					return true;

				case 75: // interface NPC head
					handleInterfaceNPCHeadOptimized();
					pktType = -1;
					return true;

				case 114: // system update
					systemMessageTimer = inStream.readWordLittleEndian() * 30;
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
					localRegionY = inStream.readByteNegated();
					localRegionX = inStream.readByteNegated();
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
					friendsListAction = inStream.readUnsignedByte();
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
					tabHoverTime = inStream.readUnsignedByte();
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
					tabID = inStream.readByteNegated();
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
			final int frame = inStream.readWordMixed();
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
					setInterfaceText(text, frame);
					updateInterfaceText(text, frame);
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
		final int pExp = inStream.readDWordMixed();
		final int pGrade = inStream.readDWordMixed();

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
			if (!isIgnoredUser(l17) && publicChatFilter == 0) {
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
			if (!isIgnoredUser(l18) && publicChatFilter == 0) {
				pushMessage("wishes to duel with you.", 8, s4);
			}
		}
		else if (s.indexOf(CHALLENGE_SUFFIX) == s.length() - CHALLENGE_SUFFIX.length()) {
			final String s5 = s.substring(0, s.indexOf(":"));
			final long l19 = TextClass.longForName(s5);
			if (!isIgnoredUser(l19) && publicChatFilter == 0) {
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

		if (!shouldIgnore && publicChatFilter == 0) {
			try {
				ignoreListIds[friendsListCount] = j18;
				friendsListCount = (friendsListCount + 1) % 100;
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
		daysSinceRecovChange = inStream.readByteNegated();
		unreadMessages = inStream.readWordMixed();
		membersInt = inStream.readUnsignedByte();
		minimapRegionX = inStream.readDWordMixed2();
		daysSinceLastLogin = inStream.readUnsignedWord();

		if (minimapRegionX != 0 && openInterfaceID == -1) {
			Signlink.dnslookup(TextClass.method586(minimapRegionX));
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
		final int startX = inStream.readByteNegated();
		final int startY = inStream.readByte128Minus();
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

		for (SpotAnimationNode item = (SpotAnimationNode) spotAnimationQueue.reverseGetFirst();
			 item != null;
			 item = (SpotAnimationNode) spotAnimationQueue.reverseGetNext()) {

			if (item.x >= startX && item.x < endX &&
				item.y >= startY && item.y < endY &&
				item.plane == plane) {
				item.delay = 0;
			}
		}
	}

	private static void handleInterfaceModelOptimized() {
		final int k = inStream.readWordMixedLE();
		final RSInterface rsInterface = INTERFACE_CACHE[k];
		rsInterface.modelType = 3;

		if (myStoner.desc == null) {
			rsInterface.mediaID = (myStoner.bodyColors[0] << 25) +
				(myStoner.bodyColors[4] << 20) +
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
		final int i1 = inStream.readWordLittleEndian();
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
		cutsceneActive = true;
		cameraOffsetX = inStream.readUnsignedByte();
		cameraOffsetY = inStream.readUnsignedByte();
		cameraOffsetZ = inStream.readUnsignedWord();
		cameraRotationX = inStream.readUnsignedByte();
		cameraRotationY = inStream.readUnsignedByte();

		if (cameraRotationY >= 100) {
			xCameraPos = cameraOffsetX * 128 + 64;
			yCameraPos = cameraOffsetY * 128 + 64;
			zCameraPos = getTerrainHeight(plane, yCameraPos, xCameraPos) - cameraOffsetZ;
		}
	}

	private static void handleTabInterfaceOptimized() {
		int l1 = inStream.readUnsignedWord();
		final int j10 = inStream.readByteSubtract128();
		if (l1 == 65535) l1 = -1;
		tabInterfaceIDs[j10] = l1;
		tabAreaAltered = true;
	}

	private static void handleMusicChangeOptimized() {
		int i2 = inStream.readWordLittleEndian();
		if (i2 == 65535) i2 = -1;

		if (i2 != currentSong && musicEnabled && !lowMem && prevSong == 0) {
			nextSong = i2;
			songChanging = true;
			cacheManager.enqueueRequest(2, nextSong);
		}
		currentSong = i2;
	}

	private static void handleMusicQueueOptimized() {
		final int j2 = inStream.readWordMixedLE();
		final int k10 = inStream.readWordMixed();

		if (musicEnabled && !lowMem) {
			nextSong = j2;
			songChanging = false;
			cacheManager.enqueueRequest(2, nextSong);
			prevSong = k10;
		}
	}

	private static void handleInterfaceScrollOptimized() {
		final int l10 = inStream.readSignedWordLE();
		final int i16 = inStream.readWordLittleEndian();
		INTERFACE_CACHE[i16].positionScroll = l10;
	}

	private static void handleRegionChangeOptimized() {
		int l2 = inventoryOffsetX;
		int i11 = inventoryOffsetY;

		if (pktType == 73) {
			l2 = inStream.readWordMixed();
			i11 = inStream.readUnsignedWord();
			musicPlaying = false;
		}

		if (pktType == 241) { // REGION LOADER
			i11 = inStream.readWordMixed();
			inStream.initBitAccess();

			for (int j16 = 0; j16 < 4; j16++) {
				for (int l20 = 0; l20 < 13; l20++) {
					for (int j23 = 0; j23 < 13; j23++) {
						final int i26 = inStream.readBits(1);
						dynamicRegionData[j16][l20][j23] = (i26 == 1) ? inStream.readBits(26) : -1;
					}
				}
			}
			inStream.finishBitAccess();
			l2 = inStream.readUnsignedWord();
			musicPlaying = true;
		}

		int playerRegionX = l2;
		int playerRegionY = i11;

// Only reload if player has actually moved to a different region
		if (inventoryOffsetX == playerRegionX && inventoryOffsetY == playerRegionY && loadingStage == 2) {
			// Player is still in same region - no need to reload
			return;
		}

// ✅ GOOD: Region loading triggered by player movement only
		inventoryOffsetX = playerRegionX;
		inventoryOffsetY = playerRegionY;
		baseX = (playerRegionX - 6) * 8;
		baseY = (playerRegionY - 6) * 8;

// Calculate region boundaries for special areas
		selectedSpell = (playerRegionX / 8 == 48 || playerRegionX / 8 == 49) && playerRegionY / 8 == 48;
		if (playerRegionX / 8 == 48 && playerRegionY / 8 == 148) selectedSpell = true;

		loadingStage = 1;
		lastConnectionTime = System.currentTimeMillis();

		System.out.println("🗺️ [RegionChange] Player moved to region: " + playerRegionX + "," + playerRegionY +
			" (camera position does NOT affect this)");

		inventoryOffsetX = l2;
		inventoryOffsetY = i11;
		baseX = (l2 - 6) * 8;
		baseY = (i11 - 6) * 8;
		selectedSpell = (l2 / 8 == 48 || l2 / 8 == 49) && i11 / 8 == 48;
		if (l2 / 8 == 48 && i11 / 8 == 148) selectedSpell = true;

		loadingStage = 1;
		lastConnectionTime = System.currentTimeMillis();

		if (pktType == 73) {
			handleRegionType73Optimized(l2, i11);
		} else if (pktType == 241) {
			handleRegionType241Optimized();
		}

		// Update entity positions
		final int deltaX = baseX - gameDrawingMode;
		final int deltaY = baseY - gameDrawingFlags;

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

		friendsListVisible = true;

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

		for (SpotAnimationNode class30_sub1_1 = (SpotAnimationNode) spotAnimationQueue.reverseGetFirst();
			 class30_sub1_1 != null;
			 class30_sub1_1 = (SpotAnimationNode) spotAnimationQueue.reverseGetNext()) {

			class30_sub1_1.x -= deltaX;
			class30_sub1_1.y -= deltaY;
			if (class30_sub1_1.x < 0 || class30_sub1_1.y < 0 ||
				class30_sub1_1.x >= 208 || class30_sub1_1.y >= 208) {
				class30_sub1_1.unlink();
			}
		}

		if (destX != 0) {
			destX -= deltaX;
			destY -= deltaY;
		}

		gameDrawingMode = baseX;
		gameDrawingFlags = baseY;
		cutsceneActive = false;
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

		terrainData = new byte[k16][];
		objectData = new byte[k16][];
		mapRegionIds = new int[k16];
		terrainIndices = new int[k16];
		objectIndices = new int[k16];

		k16 = 0;
		int embeddedLoaded = 0;
		int onDemandQueued = 0;

		for (int l23 = (l2 - 6) / 8; l23 <= (l2 + 6) / 8; l23++) {
			for (int j26 = (i11 - 6) / 8; j26 <= (i11 + 6) / 8; j26++) {
				mapRegionIds[k16] = (l23 << 8) + j26;

				if (selectedSpell && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || (l23 == 49 && j26 == 47))) {
					// Special regions with no data
					terrainIndices[k16] = -1;
					objectIndices[k16] = -1;
					System.out.println("✅ [RegionChange] Region " + mapRegionIds[k16] + " - special region (no data)");
				} else {
					// TRY EMBEDDED CACHE FIRST
					if (loadRegionFromEmbeddedCache(k16, l23, j26)) {
						embeddedLoaded++;
						System.out.println("✅ [RegionChange] Region " + mapRegionIds[k16] + " loaded from EMBEDDED CACHE");
					} else {
						// FALLBACK TO ONDEMAND FETCHER (causes lag)
						final int k28 = terrainIndices[k16] = cacheManager.getMapIndex(0, j26, l23);
						if (k28 != -1) {
							cacheManager.enqueueRequest(3, k28);
							onDemandQueued++;
						}

						final int j30 = objectIndices[k16] = cacheManager.getMapIndex(1, j26, l23);
						if (j30 != -1) {
							cacheManager.enqueueRequest(3, j30);
							onDemandQueued++;
						}

						System.err.println("⚠️ [RegionChange] Region " + mapRegionIds[k16] +
							" using cacheManager (files: " + k28 + "/" + j30 + ") - WILL CAUSE LAG");
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
			System.err.println("❌ [RegionChange] " + onDemandQueued + " files still use cacheManager - LAG EXPECTED");
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
					final int k30 = dynamicRegionData[i24][k26][l28];
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

		terrainData = new byte[l16][];
		objectData = new byte[l16][];
		mapRegionIds = new int[l16];
		terrainIndices = new int[l16];
		objectIndices = new int[l16];

		int embeddedLoaded = 0;
		int onDemandQueued = 0;

		for (int l26 = 0; l26 < l16; l26++) {
			final int i29 = mapRegionIds[l26] = ai[l26];
			final int l30 = i29 >> 8 & 0xff;
			final int l31 = i29 & 0xff;

			// TRY EMBEDDED CACHE FIRST
			if (loadRegionFromEmbeddedCache(l26, l30, l31)) {
				embeddedLoaded++;
				System.out.println("✅ [RegionChange] Region " + i29 + " loaded from EMBEDDED CACHE");
			} else {
				// FALLBACK TO ONDEMAND FETCHER (causes lag)
				final int j32 = terrainIndices[l26] = cacheManager.getMapIndex(0, l31, l30);
				if (j32 != -1) {
					cacheManager.enqueueRequest(3, j32);
					onDemandQueued++;
				}

				final int i33 = objectIndices[l26] = cacheManager.getMapIndex(1, l31, l30);
				if (i33 != -1) {
					cacheManager.enqueueRequest(3, i33);
					onDemandQueued++;
				}

				System.err.println("⚠️ [RegionChange] Region " + i29 +
					" using cacheManager (files: " + j32 + "/" + i33 + ") - WILL CAUSE LAG");
			}
		}

		System.out.println("📊 [RegionChange] Summary: " + embeddedLoaded + " embedded, " +
			onDemandQueued + " onDemand requests");

		if (onDemandQueued == 0) {
			System.out.println("🎉 [RegionChange] ALL REGIONS FROM EMBEDDED CACHE - NO LAG!");
		} else {
			System.err.println("❌ [RegionChange] " + onDemandQueued + " files still use cacheManager - LAG EXPECTED");
		}
	}

	/**
	 * NEW: Try to load region from embedded cache
	 * Returns true if successful, false if should use cacheManager
	 */
	private static boolean loadRegionFromEmbeddedCache(int arrayIndex, int regionX, int regionY) {
		try {
			// Calculate region ID
			int regionId = (regionX << 8) + regionY;

			// Try to get file IDs from cacheManager (but don't queue them)
			int mapFileId = cacheManager.getMapIndex(0, regionY, regionX);
			int landscapeFileId = cacheManager.getMapIndex(1, regionY, regionX);

			// Set file IDs in arrays
			terrainIndices[arrayIndex] = mapFileId;
			objectIndices[arrayIndex] = landscapeFileId;

			// Handle -1 file IDs (no data regions)
			if (mapFileId == -1 && landscapeFileId == -1) {
				// No data needed - mark as loaded
				terrainData[arrayIndex] = null;
				objectData[arrayIndex] = null;
				return true;
			}

			if (mapFileId == -1 || landscapeFileId == -1) {
				// Mixed -1 and valid IDs - let cacheManager handle
				return false;
			}

			// Try to get data from embedded cache
			byte[] mapData = com.bestbudz.cache.EmbeddedMapCache.getEmbeddedFileData(mapFileId);
			byte[] landscapeData = com.bestbudz.cache.EmbeddedMapCache.getEmbeddedFileData(landscapeFileId);

			if (mapData != null && landscapeData != null) {
				// SUCCESS - set data directly
				terrainData[arrayIndex] = mapData;
				objectData[arrayIndex] = landscapeData;
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

		terrainData = new byte[k16][];
		objectData = new byte[k16][];
		mapRegionIds = new int[k16];
		terrainIndices = new int[k16];
		objectIndices = new int[k16];

		k16 = 0;
		for (int l23 = (l2 - 6) / 8; l23 <= (l2 + 6) / 8; l23++) {
			for (int j26 = (i11 - 6) / 8; j26 <= (i11 + 6) / 8; j26++) {
				mapRegionIds[k16] = (l23 << 8) + j26;

				if (selectedSpell && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || (l23 == 49 && j26 == 47))) {
					terrainIndices[k16] = -1;
					objectIndices[k16] = -1;
				} else {
					final int k28 = terrainIndices[k16] = cacheManager.getMapIndex(0, j26, l23);
					if (k28 != -1) cacheManager.enqueueRequest(3, k28);

					final int j30 = objectIndices[k16] = cacheManager.getMapIndex(1, j26, l23);
					if (j30 != -1) cacheManager.enqueueRequest(3, j30);
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
					final int k30 = dynamicRegionData[i24][k26][l28];
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

		terrainData = new byte[l16][];
		objectData = new byte[l16][];
		mapRegionIds = new int[l16];
		terrainIndices = new int[l16];
		objectIndices = new int[l16];

		for (int l26 = 0; l26 < l16; l26++) {
			final int i29 = mapRegionIds[l26] = ai[l26];
			final int l30 = i29 >> 8 & 0xff;
			final int l31 = i29 & 0xff;

			final int j32 = terrainIndices[l26] = cacheManager.getMapIndex(0, l31, l30);
			if (j32 != -1) cacheManager.enqueueRequest(3, j32);

			final int i33 = objectIndices[l26] = cacheManager.getMapIndex(1, l31, l30);
			if (i33 != -1) cacheManager.enqueueRequest(3, i33);
		}
	}

	private static void handleWalkableInterfaceOptimized() {
		final int i3 = inStream.readSignedWordLE();
		if (i3 >= 0) {
			clearInterfaceAnimations(i3);
			walkableInterfaceMode = true;
		} else {
			walkableInterfaceMode = false;
		}
		mouseClickState = i3;
	}

	private static void handleInterfaceNPCHeadOptimized() {
		final int j3 = inStream.readWordMixedLE();
		final int j11 = inStream.readWordMixedLE();

		System.out.println("🎭 RAW STRING PACKET: j3 & j11 = " + j3 + " ' " + j11 + " '");

		final RSInterface rsInterface = INTERFACE_CACHE[j11];
		rsInterface.modelType = 2;
		rsInterface.mediaID = j3;
	}

	private static void handleGroundItemUpdateOptimized() {
		localRegionY = inStream.readUnsignedByte();
		localRegionX = inStream.readByteNegated();

		while (inStream.position < pktSize) {
			final int k3 = inStream.readUnsignedByte();
			handleGroundItemUpdate(inStream, k3);
		}
	}

	private static void handleSkillLevelOptimized() {
		final int skillId = inStream.readUnsignedByte();
		final int k11 = inStream.readUnsignedByte();
		final int j17 = inStream.readUnsignedByte();
		final int k21 = inStream.readUnsignedByte();

		cameraShakeEnabled[skillId] = true;
		cameraShakeAmplitude[skillId] = k11;
		cameraShakeMagnitude[skillId] = j17;
		cameraShakeSpeed[skillId] = k21;
		cameraShakeCounters[skillId] = 0;
	}

	private static void handleSoundEffectOptimized() {
		final int soundId = inStream.readUnsignedWord();
		final int volume = inStream.readUnsignedByte();
		final int delay = inStream.readUnsignedWord();

		if (soundProduction && !lowMem && soundEffectCount < 50) {
			final int index = soundEffectCount++;
			soundIds[index] = soundId;
			soundTypes[index] = volume;
			soundDelays[index] = delay;
		}
	}

	private static void handlePlayerOptionOptimized() {
		final int optionIndex = inStream.readByteNegated();
		final int enabled = inStream.readByteSubtract128();
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
		crosshairType = inStream.readUnsignedByte();

		if (crosshairType == 1) {
			targetNpcIndex = inStream.readUnsignedWord();
		} else if (crosshairType >= 2 && crosshairType <= 6) {
			switch (crosshairType) {
				case 2: selectedRegionTileY = 64; regionTileAction = 64; break;
				case 3: selectedRegionTileY = 0; regionTileAction = 64; break;
				case 4: selectedRegionTileY = 128; regionTileAction = 64; break;
				case 5: selectedRegionTileY = 64; regionTileAction = 0; break;
				case 6: selectedRegionTileY = 64; regionTileAction = 128; break;
			}
			crosshairType = 2;
			hoveredRegionTileX = inStream.readUnsignedWord();
			hoveredRegionTileY = inStream.readUnsignedWord();
			selectedRegionTileX = inStream.readUnsignedByte();
		} else if (crosshairType == 10) {
			targetPlayerIndex = inStream.readUnsignedWord();
		}
	}

	private static void handleInterfaceInventoryOptimized() {
		final int interfaceId = inStream.readWordMixed();
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
		isPlayerBusy = false;
	}

	private static void handleInterfaceScrollPositionOptimized() {
		final int interfaceId = inStream.readWordLittleEndian();
		int scrollPos = inStream.readWordMixed();
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
			if (variousSettings[k5] != experienceDrops[k5]) {
				variousSettings[k5] = experienceDrops[k5];
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
		selectedTabIndex = inStream.readByte128Minus();
		if (selectedTabIndex == tabID) {
			tabID = (selectedTabIndex == 3) ? 1 : 3;
		}
	}

	private static void handleInterfaceItemModelOptimized() {
		final int interfaceId = inStream.readWordLittleEndian();
		final int zoom = inStream.readUnsignedWord();
		final int itemId = inStream.readUnsignedWord();

		System.out.println("🎭 RAW STRING PACKET: ItemModel InterfaceID" + interfaceId + " & zoom'" + zoom + "&& item id" + itemId);

		UIDockFrame instance = UIDockFrame.getInstance();
		if (instance != null && instance.getModalManager() != null) {
			instance.getModalManager().feedItemDisplay(interfaceId, itemId, 1);
		}

		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (itemId == 65535) {
			rsInterface.modelType = 0;
		} else {
			final ItemDef itemDef = getItemDefinition(itemId);
			rsInterface.modelType = 4;
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
		final int interfaceId = inStream.readWordLittleEndian();

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
		isPlayerBusy = false;
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
			npcDisplay.size = npcDisplay.desc.size;
			npcDisplay.mapIconScale = npcDisplay.desc.mapIconScale;
			npcDisplay.desc.modelWidth = size;
			npcDisplay.desc.modelHeight = size;
			npcDisplay.standAnimation = npcDisplay.desc.standAnim;
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
		final int interfaceId = inStream.readWordMixedLE();
		final int animationId = inStream.readUnsignedWord();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];
		rsInterface.modelType = 1;
		rsInterface.mediaID = animationId;
	}

	private static void handleInterfaceColorOptimized() {
		final int interfaceId = inStream.readWordMixedLE();
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
		final int safeItemCount = Math.min(itemCount, rsInterface.inv.length);

		// Standard inventory processing for ALL interfaces (including bank)
		for (int i = 0; i < safeItemCount; i++) {
			int stackSize = inStream.readUnsignedByte();
			if (stackSize == 255) stackSize = inStream.readDWordMixed2();
			rsInterface.inv[i] = inStream.readWordMixedLE();
			rsInterface.invStackSizes[i] = stackSize;
		}

		// Clear remaining slots
		for (int j25 = safeItemCount; j25 < rsInterface.inv.length; j25++) {
			rsInterface.inv[j25] = 0;
			rsInterface.invStackSizes[j25] = 0;
		}

		// Skip excess items if any
		if (itemCount > rsInterface.inv.length) {
			for (int i = rsInterface.inv.length; i < itemCount; i++) {
				int stackSize = inStream.readUnsignedByte();
				if (stackSize == 255) stackSize = inStream.readDWordMixed2();
				inStream.readWordMixedLE();
			}
		}

		// SIMPLIFIED: Only read bank tab data but don't do special processing
		if (rsInterface.contentType == 206) {
			// Read tab amounts to keep protocol compatible but don't use them for special logic
			for (int tab = 0; tab < 10; tab++) {
				int amount = inStream.readSignedByte() << 8 | inStream.readUnsignedWord();
				tabAmounts[tab] = amount; // Store but don't use for special bank behavior
			}

			// Force bank to behave as box interface
			rsInterface.isBoxInterface = true;

			// Skip search functionality - treat all items as visible
			// No special bankInvTemp processing

			System.out.println("📦 Bank interface " + interfaceId + " updated with " +
				safeItemCount + " items (treated as regular inventory)");
		}

		notifyDockPanelUpdates(interfaceId);
	}

	private static void handleInterfaceModelRotationOptimized() {
		final int zoom = inStream.readWordMixed();
		final int interfaceId = inStream.readUnsignedWord();
		final int rotation1 = inStream.readUnsignedWord();
		final int rotation2 = inStream.readWordMixedLE();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		rsInterface.modelRotation1 = rotation1;
		rsInterface.modelRotation2 = rotation2;
		rsInterface.modelZoom = zoom;
	}

	private static void handleCameraAngleOptimized() {
		cutsceneActive = true;
		interfaceScrollX = inStream.readUnsignedByte();
		interfaceScrollY = inStream.readUnsignedByte();
		scrollableAreaHeight = inStream.readUnsignedWord();
		scrollableAreaWidth = inStream.readUnsignedByte();
		scrollPosition = inStream.readUnsignedByte();

		if (scrollPosition >= 100) {
			final int x = interfaceScrollX * 128 + 64;
			final int y = interfaceScrollY * 128 + 64;
			final int height = getTerrainHeight(plane, y, x) - scrollableAreaHeight;
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
		inputType = inStream.readByteSubtract128();
		unknownInt10 = inStream.readWordMixedLE();
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
		isPlayerBusy = false;
	}

	private static void handleDialogWindowOptimized() {
		dialogID = inStream.readSignedWordMixed();
		inputTaken = true;
	}

	private static void handleConfigLargeOptimized() {
		final int configId = inStream.readWordLittleEndian();
		final int value = inStream.readDWordMixed();
		experienceDrops[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleConfigByteOptimized() {
		final int configId = inStream.readWordLittleEndian();
		final byte value = inStream.readSignedByte();
		experienceDrops[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleInterfaceOffsetOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		final int offset = inStream.readSignedWord();
		INTERFACE_CACHE[interfaceId].verticalOffset = offset;
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
		isPlayerBusy = false;
	}

	private static void handleInventoryItemUpdateOptimized() {
		final int interfaceId = inStream.readUnsignedWord();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		// Debug logging for bank updates
		if (interfaceId == 5382) {
			System.out.println("🏦 Bank item update - Interface: " + interfaceId);
		}

		while (inStream.position < pktSize) {
			final int slot = inStream.readSmartUnsigned();
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
		final int interfaceId = inStream.readWordLittleEndian();
		clearInterfaceAnimations(interfaceId);

		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}

		UIDockFrame.handleDialogueInterface(interfaceId, INTERFACE_CACHE[interfaceId]);

		backDialogID = interfaceId;
		inputTaken = true;
		openInterfaceID = -1;
		isPlayerBusy = false;
		System.out.println("🎭 PACKET 164: Chat box interface " + interfaceId);
	}

	// Error handling optimized for performance
	private static void handleUnknownPacket() {
		final StringBuilder error = STRING_BUILDER_CACHE.get();
		error.setLength(0);
		error.append("T1 - ").append(pktType).append(",").append(pktSize)
			.append(" - ").append(regionX).append(",").append(regionY);
		Signlink.reporterror(error.toString());
		resetLogout();
	}

	private static void handlePacketException(final Exception exception) {
		exception.printStackTrace();
		final StringBuilder errorBuilder = STRING_BUILDER_CACHE.get();
		errorBuilder.setLength(0);

		errorBuilder.append("T2 - ").append(pktType).append(",").append(regionX).append(",")
			.append(regionY).append(" - ").append(pktSize).append(",")
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
				stream.writeEncryptedOpcode(103);
				stream.writeByte(inputString.length() - 1);
				stream.writeString(inputString.substring(2));
				inputString = "";
				promptInput = "";
				break;

			case 1003:
				stream.writeEncryptedOpcode(103);
				inputString = "::" + inputString;
				stream.writeByte(inputString.length() - 1);
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