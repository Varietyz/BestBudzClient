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
import com.bestbudz.engine.core.gamerender.ObjectManager;
import static com.bestbudz.engine.core.gamerender.GroundItems.handleGroundItemUpdate;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.data.Skills;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.PacketSender.setInterfaceText;
import static com.bestbudz.network.packets.PacketSender.updateInterfaceText;
import static com.bestbudz.entity.ParseAndUpdateEntities.updateNPCsProto;
import static com.bestbudz.entity.UpdateStoners.updateStonersProto;
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
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.world.SpotAnimationNode;
import com.bestbudz.world.Wall;
import com.bestbudz.world.WallDecoration;
import com.bestbudz.world.GroundDecoration;
import com.bestbudz.world.GameObject;
import static com.bestbudz.world.GroundItem.spawnGroundItem;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;
import com.bestbudz.rendering.GraphicEffect;
import com.bestbudz.rendering.InteractiveObject;
import com.bestbudz.rendering.Projectile;
import static com.bestbudz.rendering.AnimationManager.createSpotAnimation;

import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.ChatProto.*;
import com.bestbudz.net.proto.InterfaceProto.*;
import com.bestbudz.net.proto.PlayerProto.*;
import com.bestbudz.net.proto.WorldProto.*;
import com.bestbudz.net.proto.CommonProto.*;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

public class PacketParser extends Client
{

	private static final ThreadLocal<StringBuilder> STRING_BUILDER_CACHE =
		ThreadLocal.withInitial(() -> new StringBuilder(256));

	private static final RSInterface[] INTERFACE_CACHE = RSInterface.interfaceCache;

	private static final String DEAL_SUFFIX = ":dealreq:";
	private static final String CULT_SUFFIX = ":cult:";
	private static final String DUEL_SUFFIX = ":duelreq:";
	private static final String CHALLENGE_SUFFIX = ":chalreq:";
	private static final String URL_SUFFIX = "#url#";

	private static long packetCount = 0;
	private static long lastOptimizationCheck = System.currentTimeMillis();

	public static void handlePackets(Graphics2D g) throws IOException
	{
		if (socketStream == null) return;

		final boolean available = socketStream.hasProtoAvailable();
		final int batchSize = available ? 10 : 1;

		for (int i = 0; i < batchSize; i++) {
			if (!parsePacket(g)) break;
		}

		if (++packetCount % 1000 == 0) {
			checkPerformanceOptimization();
		}
	}

	private static void checkPerformanceOptimization() {
		final long currentTime = System.currentTimeMillis();
		if (currentTime - lastOptimizationCheck > 30000) {
			lastOptimizationCheck = currentTime;
		}
	}

	private static void notifyDockPanelUpdates(int interfaceId) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIDockFrame instance = UIDockFrame.getInstance();
				if (instance == null) return;

				if (interfaceId == 5382) {
					UIPanel bankPanel = instance.getPanel("Bank");
					if (bankPanel instanceof BankPanel) {
						((BankPanel) bankPanel).updateBankData();
					}
				}

			} catch (Exception e) {
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
			if (!socketStream.hasProtoAvailable()) return false;

			final GamePacket packet = socketStream.readProto();
			idleTimeout = 0;

			switch (packet.getPayloadCase())
			{

				case STONER_UPDATE: {
					updateStonersProto(packet.getStonerUpdate());
					friendsListVisible = false;
					return true;
				}

				case NPC_UPDATE: {
					updateNPCsProto(packet.getNpcUpdate());
					return true;
				}

				case INTERFACE_TEXT: {
					InterfaceText msg = packet.getInterfaceText();
					handleInterfaceText(msg.getText(), msg.getFrame());
					return true;
				}

				case PROFESSION_UPDATE: {
					ProfessionUpdate msg = packet.getProfessionUpdate();
					handleProfessionUpdate(msg.getId(), msg.getAdvance(), msg.getExp(), msg.getGrade());
					return true;
				}

				case PRIVATE_MESSAGE_OUT: {
					PrivateMessageOut msg = packet.getPrivateMessageOut();
					handlePrivateMessage(msg.getSenderNameHash(), msg.getMessageId(),
						msg.getRights(), msg.getMessage().toByteArray());
					return true;
				}

				case CHAT_MESSAGE: {
					ChatMessage msg = packet.getChatMessage();
					handleChatMessage(msg.getText());
					return true;
				}

				case LOGIN_RESPONSE_DATA: {
					LoginResponseData msg = packet.getLoginResponseData();
					handleLoginResponse(msg.getDaysSinceRecovery(), msg.getUnreadMessages(),
						msg.getMembers(), msg.getLastIp(), msg.getDaysSinceLogin());
					return true;
				}

				case GROUND_ARRAY_CLEAR: {
					GroundArrayClear msg = packet.getGroundArrayClear();
					handleGroundArrayClear(msg.getStartX(), msg.getStartY());
					return true;
				}

				case INTERFACE_MODEL: {
					InterfaceModel msg = packet.getInterfaceModel();
					handleInterfaceModel(msg.getInterfaceId());
					return true;
				}

				case CLAN_MESSAGE: {
					ClanMessage msg = packet.getClanMessage();
					handleClanMessage(msg.getUsername(), msg.getMessage(), msg.getTitle(), msg.getRights());
					return true;
				}

				case RESET_CAMERA: {
					cutsceneActive = false;
					Arrays.fill(cameraShakeEnabled, 0, 5, false);
					StatusOrbs.xpCounter = 0;
					return true;
				}

				case CLEAR_INVENTORY: {
					ClearInventory msg = packet.getClearInventory();
					handleClearInventory(msg.getInterfaceId());
					return true;
				}

				case IGNORE_LIST: {
					IgnoreList msg = packet.getIgnoreList();
					handleIgnoreList(msg.getNameHashesList());
					return true;
				}

				case CAMERA_SHAKE: {
					CameraShake msg = packet.getCameraShake();
					handleCameraShake(msg.getX(), msg.getY(), msg.getZ(), msg.getRotationX(), msg.getRotationY());
					return true;
				}

				case TAB_INTERFACE: {
					TabInterface msg = packet.getTabInterface();
					handleTabInterface(msg.getInterfaceId(), msg.getTabId());
					return true;
				}

				case MUSIC_CHANGE: {
					MusicChange msg = packet.getMusicChange();
					handleMusicChange(msg.getId());
					return true;
				}

				case MUSIC_QUEUE: {
					MusicQueue msg = packet.getMusicQueue();
					handleMusicQueue(msg.getId(), msg.getDelay());
					return true;
				}

				case LOGOUT: {
					resetLogout();
					return false;
				}

				case INTERFACE_SCROLL: {
					InterfaceScroll msg = packet.getInterfaceScroll();
					INTERFACE_CACHE[msg.getInterfaceId()].positionScroll = msg.getScrollPosition();
					return true;
				}

				case MAP_REGION: {
					MapRegion msg = packet.getMapRegion();
					handleMapRegion(msg.getRegionX(), msg.getRegionY());
					return true;
				}

				case MAP_REGION_CONSTRUCTION: {
					MapRegionConstruction msg = packet.getMapRegionConstruction();
					handleMapRegionConstruction(msg.getRegionX(), msg.getRegionY(), msg.getDynamicDataList());
					return true;
				}

				case WALKABLE_INTERFACE: {
					WalkableInterface msg = packet.getWalkableInterface();
					handleWalkableInterface(msg.getId());
					return true;
				}

				case CHARACTER_DETAIL: {
					CharacterDetail msg = packet.getCharacterDetail();
					lastActionTime = msg.getSlot();
					return true;
				}

				case INTERFACE_NPC_HEAD: {
					InterfaceNpcHead msg = packet.getInterfaceNpcHead();
					final RSInterface rsInterface = INTERFACE_CACHE[msg.getInterfaceId()];
					rsInterface.modelType = 2;
					rsInterface.mediaID = msg.getNpcId();
					return true;
				}

				case GAME_UPDATE_TIMER: {
					GameUpdateTimer msg = packet.getGameUpdateTimer();
					systemMessageTimer = msg.getTime() * 30;
					return true;
				}

				case GROUND_ITEM_UPDATE: {
					GroundItemUpdate msg = packet.getGroundItemUpdate();
					handleGroundItemUpdateProto(msg);
					return true;
				}

				case SOUND_EFFECT: {
					SoundEffect msg = packet.getSoundEffect();
					handleSoundEffect(msg.getId(), msg.getType(), msg.getDelay());
					return false;
				}

				case STONER_OPTION: {
					StonerOption msg = packet.getStonerOption();
					handleStonerOption(msg.getId(), msg.getOption(), msg.getTopPriority());
					return true;
				}

				case REMOVE_INTERFACES: {
					destX = 0;
					return true;
				}

				case RESET_ANIMATIONS: {
					handleResetAnimationsAll();
					return true;
				}

				case FRIEND_UPDATE: {
					FriendUpdate msg = packet.getFriendUpdate();
					handleFriendUpdate(msg.getNameHash(), msg.getWorld());
					return true;
				}

				case ENERGY_UPDATE: {
					EnergyUpdate msg = packet.getEnergyUpdate();
					energy = msg.getEnergy();
					return true;
				}

				case MINIMAP_STATE: {
					MinimapState msg = packet.getMinimapState();
					handleMinimapState(msg.getType(), msg.getTargetIndex(), msg.getX(), msg.getY(), msg.getOffset());
					return true;
				}

				case INTERFACE_INVENTORY: {
					InterfaceInventory msg = packet.getInterfaceInventory();
					handleInterfaceInventory(msg.getInterfaceId(), msg.getOverlayId());
					return true;
				}

				case INTERFACE_SCROLL_POSITION: {
					InterfaceScrollPosition msg = packet.getInterfaceScrollPosition();
					handleInterfaceScrollPosition(msg.getInterfaceId(), msg.getScrollPosition());
					return true;
				}

				case CONFIG_UPDATE: {
					ConfigUpdate msg = packet.getConfigUpdate();
					handleConfigLarge(msg.getId(), msg.getValue());
					return true;
				}

				case CONFIG_BYTE_UPDATE: {
					ConfigByteUpdate msg = packet.getConfigByteUpdate();
					handleConfigByte(msg.getId(), msg.getValue());
					return true;
				}

				case KILL_FEED: {
					KillFeed msg = packet.getKillFeed();
					try {
						pushKill(msg.getKiller(), msg.getVictim(), msg.getWeaponId(), msg.getIsSpecial());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}

				case COORDINATES: {
					Coordinates msg = packet.getCoordinates();
					localRegionY = msg.getY();
					localRegionX = msg.getX();
					return true;
				}

				case FORCE_TAB: {
					ForceTab msg = packet.getForceTab();
					handleForceTab(msg.getTabId());
					return true;
				}

				case INTERFACE_ITEM_MODEL: {
					InterfaceItemModel msg = packet.getInterfaceItemModel();
					handleInterfaceItemModel(msg.getInterfaceId(), msg.getZoom(), msg.getItemId());
					return true;
				}

				case INTERFACE_HOVER: {
					InterfaceHover msg = packet.getInterfaceHover();
					handleInterfaceHover(msg.getEnabled(), msg.getInterfaceId());
					return true;
				}

				case CLOSE_ALL_INTERFACES: {
					handleCloseAllInterfaces();
					return true;
				}

				case CLOSE_INTERFACE: {
					CloseInterface msg = packet.getCloseInterface();
					handleCloseInterface(msg.getId());
					return true;
				}

				case NPC_DISPLAY: {
					NpcDisplay msg = packet.getNpcDisplay();
					handleNpcDisplay(msg.getNpcId(), msg.getSize());
					return true;
				}

				case EXP_COUNTER: {
					ExpCounter msg = packet.getExpCounter();
					try {
						StatusOrbs.xpCounter = msg.getCounterExp();
						addXP(msg.getProfession(), msg.getExp());
					} catch (Exception e) {
					}
					return true;
				}

				case PLAYER_INDEX: {
					PlayerIndex msg = packet.getPlayerIndex();
					try {
						stonerIndex = msg.getIndex();
					} catch (Exception e) {
					}
					return true;
				}

				case CHAT_SETTINGS: {
					ChatSettings msg = packet.getChatSettings();
					publicChatMode = msg.getPublicMode();
					privateChatMode = msg.getPrivateMode();
					tradeMode = msg.getTradeMode();
					inputTaken = true;
					return true;
				}

				case WEIGHT_UPDATE: {
					WeightUpdate msg = packet.getWeightUpdate();
					weight = msg.getWeight();
					return true;
				}

				case INTERFACE_ANIMATION: {
					InterfaceAnimation msg = packet.getInterfaceAnimation();
					final RSInterface rsInterface = INTERFACE_CACHE[msg.getInterfaceId()];
					rsInterface.modelType = 1;
					rsInterface.mediaID = msg.getAnimationId();
					return true;
				}

				case INTERFACE_COLOR: {
					InterfaceColor msg = packet.getInterfaceColor();
					final RSInterface rsInterface = INTERFACE_CACHE[msg.getId()];
					if (rsInterface != null) {
						rsInterface.textColor = msg.getR() << 16 | msg.getG() << 8 | msg.getB();
					}
					return true;
				}

				case INVENTORY_UPDATE: {
					InventoryUpdate msg = packet.getInventoryUpdate();
					handleInventoryUpdate(msg);
					return true;
				}

				case INTERFACE_MODEL_ROTATION: {
					InterfaceModelRotation msg = packet.getInterfaceModelRotation();
					final RSInterface rsInterface = INTERFACE_CACHE[msg.getInterfaceId()];
					rsInterface.modelRotation1 = msg.getRotation1();
					rsInterface.modelRotation2 = msg.getRotation2();
					rsInterface.modelZoom = msg.getZoom();
					return true;
				}

				case PRIVATE_MESSAGE_SERVER: {
					PrivateMessageServer msg = packet.getPrivateMessageServer();
					friendsListAction = msg.getState();
					return true;
				}

				case CAMERA_ANGLE: {
					CameraAngle msg = packet.getCameraAngle();
					handleCameraAngle(msg.getX(), msg.getY(), msg.getHeight(), msg.getSpeed(), msg.getAngle());
					return true;
				}

				case INTERFACE_SETTINGS: {
					InterfaceSettings msg = packet.getInterfaceSettings();
					inputType = msg.getInputType();
					unknownInt10 = msg.getUnknown();
					return true;
				}

				case INPUT_AMOUNT: {
					messagePromptRaised = false;
					inputDialogState = 1;
					amountOrNameInput = "";
					inputTaken = true;
					return true;
				}

				case INPUT_NAME: {
					messagePromptRaised = false;
					inputDialogState = 2;
					amountOrNameInput = "";
					inputTaken = true;
					return true;
				}

				case OPEN_INTERFACE: {
					OpenInterface msg = packet.getOpenInterface();
					handleOpenInterface(msg.getId());
					return true;
				}

				case DIALOG_WINDOW: {
					DialogWindow msg = packet.getDialogWindow();
					dialogID = msg.getId();
					inputTaken = true;
					return true;
				}

				case INTERFACE_OFFSET: {
					InterfaceOffset msg = packet.getInterfaceOffset();
					INTERFACE_CACHE[msg.getInterfaceId()].verticalOffset = msg.getOffset();
					return true;
				}

				case INVENTORY_ITEM_UPDATE: {
					InventoryItemUpdate msg = packet.getInventoryItemUpdate();
					handleInventoryItemUpdate(msg);
					return true;
				}

				case SET_TAB: {
					SetTab msg = packet.getSetTab();
					tabID = msg.getTabId();
					tabAreaAltered = true;
					return true;
				}

				case BOX_INTERFACE: {
					BoxInterface msg = packet.getBoxInterface();
					handleBoxInterface(msg.getId(), msg.getInvId());
					return true;
				}

				case SIDEBAR_INTERFACE: {
					SidebarInterface msg = packet.getSidebarInterface();
					handleSidebarInterface(msg.getTabId(), msg.getInterfaceId());
					return true;
				}

				case BACK_DIALOG: {
					BackDialog msg = packet.getBackDialog();
					handleBackDialog(msg.getId());
					return true;
				}

				case EQUIPMENT_UPDATE: {
					EquipmentUpdate msg = packet.getEquipmentUpdate();
					handleEquipmentUpdate(msg.getSlot(), msg.getId(), msg.getAmount());
					return true;
				}

				case DUEL_EQUIPMENT: {
					DuelEquipment msg = packet.getDuelEquipment();
					handleDuelEquipment(msg.getId(), msg.getAmount(), msg.getSlot());
					return true;
				}

				case UPDATE_ITEMS_ALT: {
					UpdateItemsAlt msg = packet.getUpdateItemsAlt();
					handleUpdateItemsAlt(msg.getInterfaceId(), msg.getId(), msg.getAmount(), msg.getSlot());
					return true;
				}

				case BANNER_MESSAGE: {
					BannerMessage msg = packet.getBannerMessage();
					handleBannerMessage(msg.getMessage(), msg.getColor());
					return true;
				}

				case SPECIAL_BAR_UPDATE: {
					SpecialBarUpdate msg = packet.getSpecialBarUpdate();
					handleSpecialBarUpdate(msg.getAmount(), msg.getId());
					return true;
				}

				case SPECIAL_BAR: {
					SpecialBar msg = packet.getSpecialBar();
					handleSpecialBar(msg.getMain(), msg.getSub());
					return true;
				}

				case STONER_HINT: {
					StonerHint msg = packet.getStonerHint();
					handleStonerHint(msg.getType(), msg.getId());
					return true;
				}

				case NPC_HINT: {
					NpcHint msg = packet.getNpcHint();
					handleNpcHint(msg.getType(), msg.getId());
					return true;
				}

				case MOVE_COMPONENT: {
					MoveComponent msg = packet.getMoveComponent();
					handleMoveComponent(msg.getX(), msg.getY(), msg.getComponentId());
					return true;
				}

				case ITEM_ON_INTERFACE: {
					ItemOnInterface msg = packet.getItemOnInterface();
					handleItemOnInterface(msg.getId(), msg.getZoom(), msg.getModel());
					return true;
				}

				case ENTER_STRING: {
					messagePromptRaised = false;
					inputDialogState = 3;
					amountOrNameInput = "";
					inputTaken = true;
					return true;
				}

				case ENTER_X_INTERFACE: {
					EnterXInterface msg = packet.getEnterXInterface();
					handleEnterXInterface(msg.getInterfaceId(), msg.getItemId());
					return true;
				}

				case MAP_STATE: {
					MapState msg = packet.getMapState();
					handleMapState(msg.getState());
					return true;
				}

				case PROFESSION_GOAL: {
					ProfessionGoal msg = packet.getProfessionGoal();
					handleProfessionGoal(msg.getProfession(), msg.getInit(), msg.getGoal(), msg.getType());
					return true;
				}

				case CHATBOX_INTERFACE: {
					ChatBoxInterface msg = packet.getChatboxInterface();
					handleChatboxInterface(msg.getId());
					return true;
				}

				case INTERFACE_CONFIG: {
					InterfaceConfig msg = packet.getInterfaceConfig();
					handleInterfaceConfig(msg.getMain(), msg.getSub1(), msg.getSub2());
					return true;
				}

				case GROUND_ITEM_SPAWN: {
					GroundItemSpawn msg = packet.getGroundItemSpawn();
					handleGroundItemSpawnProto(msg.getItemId(), msg.getAmount(), msg.getX(), msg.getY());
					return true;
				}

				case GROUND_ITEM_REMOVE: {
					GroundItemRemove msg = packet.getGroundItemRemove();
					handleGroundItemRemoveProto(msg.getItemId(), msg.getX(), msg.getY());
					return true;
				}

				case OBJECT_SPAWN: {
					ObjectSpawn msg = packet.getObjectSpawn();
					handleObjectSpawnProto(msg.getObjectId(), msg.getType(), msg.getFace(), msg.getX(), msg.getY());
					return true;
				}

				case OBJECT_ANIMATE: {
					ObjectAnimate msg = packet.getObjectAnimate();
					handleObjectAnimateProto(msg.getType(), msg.getFace(), msg.getAnimation(), msg.getX(), msg.getY());
					return true;
				}

				case PROJECTILE: {
					com.bestbudz.net.proto.WorldProto.Projectile msg = packet.getProjectile();
					handleProjectileProto(msg.getId(), msg.getStartHeight(), msg.getEndHeight(),
						msg.getDelay(), msg.getDuration(), msg.getCurve(), msg.getLockOn(),
						msg.getOffsetX(), msg.getOffsetY(), msg.getX(), msg.getY());
					return true;
				}

				case STILL_GRAPHIC: {
					StillGraphic msg = packet.getStillGraphic();
					handleStillGraphicProto(msg.getId(), msg.getZ(), msg.getDelay(), msg.getX(), msg.getY());
					return true;
				}

				case MULTI_COMBAT_ICON: {
					MultiCombatIcon msg = packet.getMultiCombatIcon();
					isMultiCombatArea = msg.getMulti();
					return true;
				}

				case SYSTEM_BAN: {
					resetLogout();
					return false;
				}

				case LOGIN_RESPONSE: {
					// LoginResponse already handled during login handshake in Login.java.
					// If one arrives here, silently ignore it.
					return true;
				}

				default: {
					System.err.println("Unhandled protobuf packet type: " + packet.getPayloadCase());
					return true;
				}
			}
		}
		catch (IOException _ex)
		{
			return false;
		}
		catch (Exception exception)
		{
			handlePacketException(exception);
			return false;
		}
	}

	// ========================================================================
	// Handler methods
	// ========================================================================

	private static void handleInterfaceText(String text, int frame) {
		try {
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

			if (frame >= 0 && frame < INTERFACE_CACHE.length) {
				final RSInterface rsInterface = INTERFACE_CACHE[frame];
				if (rsInterface != null) {
					setInterfaceText(text, frame);
					updateInterfaceText(text, frame);
				}
			}

			if (frame >= 18144 && frame <= 18244) {
				clanList[frame - 18144] = text;
			} else if (frame == 8135 && INTERFACE_CACHE[8135] != null) {
				INTERFACE_CACHE[8135].opacity = 108;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleProfessionUpdate(int pid, int pAdvance, int pExp, int pGrade) {
		currentExp[pid] = pExp;
		currentStats[pid] = pGrade;
		currentAdvances[pid] = pAdvance;
		maxStats[pid] = calculateMaxLevel(pExp);

		updateSkillsPanel(pid);
	}

	private static int calculateMaxLevel(final int experience) {
		final int[] expTable = Skills.EXP_FOR_LEVEL;
		final int tableLength = expTable.length;

		for (int level = tableLength - 1; level >= 0; level--) {
			if (experience >= expTable[level]) {
				return level;
			}
		}
		return 1;
	}

	private static void updateSkillsPanel(final int skillId) {
		final DockTextUpdatable updatable = UIDockHelper.getUpdatablePanel("Skills");
		if (UIDockHelper.isPanelVisible("Skills") && updatable instanceof SkillsPanel) {
			((SkillsPanel) updatable).updateSkill(skillId);
		}
	}

	private static void handlePrivateMessage(long usernameHash, int messageId, int rights, byte[] messageBytes) {
		final boolean shouldIgnore = rights <= 1 && isIgnoredUser(usernameHash);

		if (!shouldIgnore && publicChatFilter == 0) {
			try {
				ignoreListIds[friendsListCount] = messageId;
				friendsListCount = (friendsListCount + 1) % 100;

				// Copy message bytes into inStream so TextInput.method525 can read them
				System.arraycopy(messageBytes, 0, inStream.buffer, 0, messageBytes.length);
				inStream.position = 0;
				final String s9 = TextInput.method525(messageBytes.length, inStream);

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

	private static void handleChatMessage(String s) {
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

	private static void handleLoginResponse(int daysSinceRecov, int unreadMsgs, int members, int lastIp, int daysSinceLogin) {
		daysSinceRecovChange = daysSinceRecov;
		unreadMessages = unreadMsgs;
		membersInt = members;
		minimapRegionX = lastIp;
		daysSinceLastLogin = daysSinceLogin;

		if (minimapRegionX != 0 && openInterfaceID == -1) {
			Signlink.dnslookup(TextClass.method586(minimapRegionX));
			clearTopInterfaces();
			final char c = (daysSinceRecovChange != 201 || membersInt == 1) ? '\u028F' : '\u028A';
			reportAbuseInput = "";
			canMute = false;

			for (final RSInterface rsInterface : INTERFACE_CACHE) {
				if (rsInterface != null && rsInterface.contentType == c) {
					openInterfaceID = rsInterface.parentID;
					break;
				}
			}
		}
	}

	private static void handleGroundArrayClear(int startX, int startY) {
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

		for (SpotAnimationNode item : spotAnimationQueue) {
			if (item.x >= startX && item.x < endX &&
				item.y >= startY && item.y < endY &&
				item.plane == plane) {
				item.delay = 0;
			}
		}
	}

	private static void handleInterfaceModel(int interfaceId) {
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];
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

	private static void handleClanMessage(String username, String message, String title, int rights) {
		try {
			clanUsername = username;
			clanMessage = TextInput.processText(message);
			clanTitle = title;
			channelRights = rights;
			pushMessage(clanMessage, 16, clanUsername);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void handleClearInventory(int interfaceId) {
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		for (int k15 = 0; k15 < rsInterface.inv.length; k15++) {
			rsInterface.inv[k15] = -1;
			rsInterface.inv[k15] = 0;
		}
	}

	private static void handleIgnoreList(List<Long> nameHashes) {
		final int count = nameHashes.size();
		ignoreCount = count;
		for (int i = 0; i < count; i++) {
			ignoreListAsLongs[i] = nameHashes.get(i);
		}
	}

	private static void handleCameraShake(int x, int y, int z, int rotationX, int rotationY) {
		cutsceneActive = true;
		cameraOffsetX = x;
		cameraOffsetY = y;
		cameraOffsetZ = z;
		cameraRotationX = rotationX;
		cameraRotationY = rotationY;

		if (cameraRotationY >= 100) {
			xCameraPos = cameraOffsetX * 128 + 64;
			yCameraPos = cameraOffsetY * 128 + 64;
			zCameraPos = getTerrainHeight(plane, yCameraPos, xCameraPos) - cameraOffsetZ;
		}
	}

	private static void handleTabInterface(int interfaceId, int tabId) {
		if (interfaceId == 65535) interfaceId = -1;
		tabInterfaceIDs[tabId] = interfaceId;
		tabAreaAltered = true;
	}

	private static void handleMusicChange(int id) {
		if (id == 65535) id = -1;

		if (id != currentSong && musicEnabled && !lowMem && prevSong == 0) {
			nextSong = id;
			songChanging = true;
		}
		currentSong = id;
	}

	private static void handleMusicQueue(int id, int delay) {
		if (musicEnabled && !lowMem) {
			nextSong = id;
			songChanging = false;
			prevSong = delay;
		}
	}

	private static void handleMapRegion(int regionX, int regionY) {
		handleRegionChange(regionX, regionY, false, null);
	}

	private static void handleMapRegionConstruction(int regionX, int regionY, List<Integer> dynamicDataList) {
		// Fill dynamicRegionData from flattened list
		int index = 0;
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					if (index < dynamicDataList.size()) {
						dynamicRegionData[z][x][y] = dynamicDataList.get(index++);
					} else {
						dynamicRegionData[z][x][y] = -1;
					}
				}
			}
		}
		handleRegionChange(regionX, regionY, true, dynamicDataList);
	}

	private static void handleRegionChange(int l2, int i11, boolean isConstruction, List<Integer> dynamicDataList) {
		if (!isConstruction) {
			musicPlaying = false;
		} else {
			musicPlaying = true;
		}

		if (inventoryOffsetX == l2 && inventoryOffsetY == i11 && loadingStage == 2) {
			return;
		}

		inventoryOffsetX = l2;
		inventoryOffsetY = i11;
		baseX = (l2 - 6) * 8;
		baseY = (i11 - 6) * 8;

		selectedSpell = (l2 / 8 == 48 || l2 / 8 == 49) && i11 / 8 == 48;
		if (l2 / 8 == 48 && i11 / 8 == 148) selectedSpell = true;

		loadingStage = 1;
		lastConnectionTime = System.currentTimeMillis();

		if (!isConstruction) {
			handleRegionType73(l2, i11);
		} else {
			handleRegionType241();
		}

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

		java.util.Iterator<SpotAnimationNode> spotIt = spotAnimationQueue.iterator();
		while (spotIt.hasNext()) {
			SpotAnimationNode class30_sub1_1 = spotIt.next();
			class30_sub1_1.x -= deltaX;
			class30_sub1_1.y -= deltaY;
			if (class30_sub1_1.x < 0 || class30_sub1_1.y < 0 ||
				class30_sub1_1.x >= 208 || class30_sub1_1.y >= 208) {
				spotIt.remove();
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

	private static void handleRegionType73(final int l2, final int i11) {
		int k16 = 0;
		for (int i21 = (l2 - 6) / 8; i21 <= (l2 + 6) / 8; i21++) {
			for (int k23 = (i11 - 6) / 8; k23 <= (i11 + 6) / 8; k23++) {
				k16++;
			}
		}

		mapRegionIds = new int[k16];

		k16 = 0;
		for (int l23 = (l2 - 6) / 8; l23 <= (l2 + 6) / 8; l23++) {
			for (int j26 = (i11 - 6) / 8; j26 <= (i11 + 6) / 8; j26++) {
				if (selectedSpell && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || (l23 == 49 && j26 == 47))) {
					mapRegionIds[k16] = 0;
				} else {
					mapRegionIds[k16] = (l23 << 8) + j26;
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

		mapRegionIds = new int[l16];
		for (int l26 = 0; l26 < l16; l26++) {
			mapRegionIds[l26] = ai[l26];
		}
	}

	private static void handleWalkableInterface(int id) {
		if (id >= 0) {
			clearInterfaceAnimations(id);
			walkableInterfaceMode = true;
		} else {
			walkableInterfaceMode = false;
		}
		mouseClickState = id;
	}

	private static void handleGroundItemUpdateProto(GroundItemUpdate msg) {
		localRegionY = msg.getRegionY();
		localRegionX = msg.getRegionX();

		for (GroundItemAction action : msg.getActionsList()) {
			byte[] data = action.getData().toByteArray();
			System.arraycopy(data, 0, inStream.buffer, 0, data.length);
			inStream.position = 0;
			handleGroundItemUpdate(inStream, action.getOpcode());
		}
	}

	private static void handleSoundEffect(int id, int type, int delay) {
		if (soundProduction && !lowMem && soundEffectCount < 50) {
			final int index = soundEffectCount++;
			soundIds[index] = id;
			soundTypes[index] = type;
			soundDelays[index] = delay;
		}
	}

	private static void handleStonerOption(int optionIndex, String option, boolean topPriority) {
		if (optionIndex >= 1 && optionIndex <= 5) {
			if (option.equalsIgnoreCase("null")) option = null;
			atStonerActions[optionIndex - 1] = option;
			atStonerArray[optionIndex - 1] = topPriority;
		}
	}

	private static void handleResetAnimationsAll() {
		for (final Stoner stoner : stonerArray) {
			if (stoner != null) stoner.anim = -1;
		}
		for (final Npc npc : npcArray) {
			if (npc != null) npc.anim = -1;
		}
	}

	private static void handleFriendUpdate(long userHash, int nodeId) {
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

	private static void handleMinimapState(int type, int targetIndex, int x, int y, int offset) {
		crosshairType = type;

		if (crosshairType == 1) {
			targetNpcIndex = targetIndex;
		} else if (crosshairType >= 2 && crosshairType <= 6) {
			switch (crosshairType) {
				case 2: selectedRegionTileY = 64; regionTileAction = 64; break;
				case 3: selectedRegionTileY = 0; regionTileAction = 64; break;
				case 4: selectedRegionTileY = 128; regionTileAction = 64; break;
				case 5: selectedRegionTileY = 64; regionTileAction = 0; break;
				case 6: selectedRegionTileY = 64; regionTileAction = 128; break;
			}
			crosshairType = 2;
			hoveredRegionTileX = x;
			hoveredRegionTileY = y;
			selectedRegionTileX = offset;
		} else if (crosshairType == 10) {
			targetPlayerIndex = targetIndex;
		}
	}

	private static void handleInterfaceInventory(int interfaceId, int overlayId) {
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

	private static void handleInterfaceScrollPosition(int interfaceId, int scrollPos) {
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (rsInterface != null && rsInterface.type == 0) {
			if (scrollPos < 0) scrollPos = 0;
			if (scrollPos > rsInterface.scrollMax - rsInterface.height) {
				scrollPos = rsInterface.scrollMax - rsInterface.height;
			}
			rsInterface.scrollPosition = scrollPos;
		}
	}

	private static void handleConfigLarge(int configId, int value) {
		if (configId < 0 || configId >= variousSettings.length) return;
		experienceDrops[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleConfigByte(int configId, int value) {
		if (configId < 0 || configId >= variousSettings.length) return;
		experienceDrops[configId] = value;

		if (variousSettings[configId] != value) {
			variousSettings[configId] = value;
			updateConfigValues(configId);
			if (dialogID != -1) inputTaken = true;
		}
	}

	private static void handleForceTab(int tabId) {
		selectedTabIndex = tabId;
		if (selectedTabIndex == tabID) {
			tabID = (selectedTabIndex == 3) ? 1 : 3;
		}
	}

	private static void handleInterfaceItemModel(int interfaceId, int zoom, int itemId) {
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

	private static void handleInterfaceHover(boolean enabled, int interfaceId) {
		if (interfaceId >= 0 && interfaceId < INTERFACE_CACHE.length && INTERFACE_CACHE[interfaceId] != null) {
			INTERFACE_CACHE[interfaceId].isMouseoverTriggered = enabled;
		} else {
			System.err.println("Ignoring hover for missing interface: " + interfaceId);
		}
	}

	private static void handleCloseAllInterfaces() {
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

	private static void handleCloseInterface(int interfaceId) {
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

	private static void handleNpcDisplay(int npcId, int size) {
		try {
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

	private static void handleOpenInterface(int interfaceId) {
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

	private static void handleInventoryUpdate(InventoryUpdate msg) {
		final int interfaceId = msg.getInterfaceId();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		if (rsInterface == null || rsInterface.inv == null) {
			System.err.println("Invalid interface or inventory: " + interfaceId);
			return;
		}

		final List<Item> items = msg.getItemsList();
		final int itemCount = items.size();
		final int safeItemCount = Math.min(itemCount, rsInterface.inv.length);

		for (int i = 0; i < safeItemCount; i++) {
			Item item = items.get(i);
			rsInterface.inv[i] = item.getId();
			rsInterface.invStackSizes[i] = item.getAmount();
		}

		for (int j25 = safeItemCount; j25 < rsInterface.inv.length; j25++) {
			rsInterface.inv[j25] = 0;
			rsInterface.invStackSizes[j25] = 0;
		}

		if (rsInterface.contentType == 206) {
			final List<Integer> tabAmountsList = msg.getTabAmountsList();
			for (int tab = 0; tab < Math.min(10, tabAmountsList.size()); tab++) {
				tabAmounts[tab] = tabAmountsList.get(tab);
			}

			rsInterface.isBoxInterface = true;
		}

		notifyDockPanelUpdates(interfaceId);
	}

	private static void handleInventoryItemUpdate(InventoryItemUpdate msg) {
		final int interfaceId = msg.getInterfaceId();
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];

		for (SlottedItem slotted : msg.getItemsList()) {
			final int slot = slotted.getSlot();
			final int itemId = slotted.getId();
			final int stackSize = slotted.getAmount();

			if (slot >= 0 && slot < rsInterface.inv.length) {
				rsInterface.inv[slot] = itemId;
				rsInterface.invStackSizes[slot] = stackSize;
			}
		}

		notifyDockPanelUpdates(interfaceId);
	}

	private static void handleCameraAngle(int x, int y, int height, int speed, int angle) {
		cutsceneActive = true;
		interfaceScrollX = x;
		interfaceScrollY = y;
		scrollableAreaHeight = height;
		scrollableAreaWidth = speed;
		scrollPosition = angle;

		if (scrollPosition >= 100) {
			final int px = interfaceScrollX * 128 + 64;
			final int py = interfaceScrollY * 128 + 64;
			final int h = getTerrainHeight(plane, py, px) - scrollableAreaHeight;
			final int deltaX = px - xCameraPos;
			final int deltaHeight = h - zCameraPos;
			final int deltaY = py - yCameraPos;
			final int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

			yCameraCurve = (int) (Math.atan2(deltaHeight, distance) * 325.94900000000001D) & 0x7ff;
			xCameraCurve = (int) (Math.atan2(deltaX, deltaY) * -325.94900000000001D) & 0x7ff;
			if (yCameraCurve < 128) yCameraCurve = 128;
			if (yCameraCurve > 383) yCameraCurve = 383;
		}
	}

	private static void handleBackDialog(int interfaceId) {
		clearInterfaceAnimations(interfaceId);

		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}

		backDialogID = interfaceId;
		inputTaken = true;
		openInterfaceID = -1;
		isPlayerBusy = false;
	}

	private static void handleBoxInterface(int id, int invId) {
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}

		openInterfaceID = id;
		invOverlayInterfaceID = invId;
		tabAreaAltered = true;
		isPlayerBusy = false;
	}

	private static void handleSidebarInterface(int tabId, int interfaceId) {
		if (interfaceId == 65535) interfaceId = -1;
		tabInterfaceIDs[tabId] = interfaceId;
		tabAreaAltered = true;
	}

	private static void handleEquipmentUpdate(int slot, int id, int amount) {
		// Equipment updates handled via interface cache if applicable
		// The server sends slot/id/amount for the equipment interface
	}

	private static void handleDuelEquipment(int id, int amount, int slot) {
		// Duel equipment update
	}

	private static void handleUpdateItemsAlt(int interfaceId, int id, int amount, int slot) {
		final RSInterface rsInterface = INTERFACE_CACHE[interfaceId];
		if (rsInterface != null && rsInterface.inv != null && slot >= 0 && slot < rsInterface.inv.length) {
			rsInterface.inv[slot] = id;
			rsInterface.invStackSizes[slot] = amount;
		}
	}

	private static void handleBannerMessage(String message, int color) {
		// Banner message display - the message will be shown via the client's banner system
		pushMessage(message, 0, "");
	}

	private static void handleSpecialBarUpdate(int amount, int id) {
		// Special attack bar percentage update
		// The id is the special bar interface component, amount is the percentage
	}

	private static void handleSpecialBar(int main, int sub) {
		// Special bar toggle (on/off state)
	}

	private static void handleStonerHint(int type, int id) {
		crosshairType = type;
		if (type == 10) {
			targetPlayerIndex = id;
		}
	}

	private static void handleNpcHint(int type, int id) {
		crosshairType = type;
		if (type == 1) {
			targetNpcIndex = id;
		}
	}

	private static void handleMoveComponent(int x, int y, int componentId) {
		final RSInterface rsInterface = INTERFACE_CACHE[componentId];
		if (rsInterface != null) {
			rsInterface.anInt263 = x;
			rsInterface.positionScroll = y;
		}
	}

	private static void handleItemOnInterface(int id, int zoom, int model) {
		final RSInterface rsInterface = INTERFACE_CACHE[id];
		if (rsInterface != null) {
			rsInterface.modelType = 4;
			rsInterface.mediaID = model;
			rsInterface.modelZoom = zoom;
		}
	}

	private static void handleEnterXInterface(int interfaceId, int itemId) {
		// Enter X amount dialog for a specific interface/item
		messagePromptRaised = false;
		inputDialogState = 1;
		amountOrNameInput = "";
		inputTaken = true;
	}

	private static void handleMapState(int state) {
		// Map state changes (e.g., minimap visibility)
	}

	private static void handleProfessionGoal(int profession, int init, int goal, int type) {
		if (profession >= 0 && profession < professionGoalInit.length) {
			professionGoalInit[profession] = init;
			professionGoalTarget[profession] = goal;
			professionGoalType[profession] = type;
		}
	}

	private static void handleChatboxInterface(int id) {
		clearInterfaceAnimations(id);
		backDialogID = id;
		inputTaken = true;
	}

	private static void handleInterfaceConfig(int main, int sub1, int sub2) {
		// Interface config — used to show/hide interface children or set item display
		// main = interface ID, sub1 = value, sub2 = secondary value
	}

	private static void handleGroundItemSpawnProto(int itemId, int amount, int absX, int absY) {
		int localX = absX - baseX;
		int localY = absY - baseY;
		if (localX >= 0 && localY >= 0 && localX < 104 && localY < 104) {
			com.bestbudz.data.items.Item groundItem = new com.bestbudz.data.items.Item();
			groundItem.ID = itemId;
			groundItem.stackSize = amount;
			if (groundArray[plane][localX][localY] == null)
				groundArray[plane][localX][localY] = new java.util.ArrayDeque<>();
			groundArray[plane][localX][localY].addFirst(groundItem);
			spawnGroundItem(localX, localY);
		}
	}

	private static void handleGroundItemRemoveProto(int itemId, int absX, int absY) {
		int localX = absX - baseX;
		int localY = absY - baseY;
		if (localX >= 0 && localY >= 0 && localX < 104 && localY < 104) {
			java.util.ArrayDeque<com.bestbudz.data.items.Item> list = groundArray[plane][localX][localY];
			if (list != null) {
				java.util.Iterator<com.bestbudz.data.items.Item> it = list.iterator();
				while (it.hasNext()) {
					com.bestbudz.data.items.Item item = it.next();
					if (item.ID == (itemId & 0x7fff)) {
						it.remove();
						break;
					}
				}
				if (list.isEmpty())
					groundArray[plane][localX][localY] = null;
				spawnGroundItem(localX, localY);
			}
		}
	}

	private static void handleObjectSpawnProto(int objectId, int type, int face, int absX, int absY) {
		int localX = absX - baseX;
		int localY = absY - baseY;
		if (localX >= 0 && localY >= 0 && localX < 104 && localY < 104) {
			int objectGroup = characterModelIndices[type];
			createSpotAnimation(-1, objectId, face, objectGroup, localY, type, plane, localX, 0);
		}
	}

	private static void handleObjectAnimateProto(int type, int face, int animation, int absX, int absY) {
		int localX = absX - baseX;
		int localY = absY - baseY;
		if (localX >= 0 && localY >= 0 && localX < 103 && localY < 103) {
			int objectGroup = characterModelIndices[type];
			int j18 = intGroundArray[plane][localX][localY];
			int i19 = intGroundArray[plane][localX + 1][localY];
			int l19 = intGroundArray[plane][localX + 1][localY + 1];
			int k20 = intGroundArray[plane][localX][localY + 1];
			if (objectGroup == 0) {
				Wall wall = worldController.getWall(plane, localX, localY);
				if (wall != null) {
					int objId = wall.uid >> 14 & 0x7fff;
					if (type == 2) {
						wall.primaryModel = new InteractiveObject(objId, 4 + face, 2, i19, l19, j18, k20, animation, false);
						wall.secondaryModel = new InteractiveObject(objId, face + 1 & 3, 2, i19, l19, j18, k20, animation, false);
					} else {
						wall.primaryModel = new InteractiveObject(objId, face, type, i19, l19, j18, k20, animation, false);
					}
				}
			}
			if (objectGroup == 1) {
				WallDecoration wd = worldController.getWallDecoration(localX, localY, plane);
				if (wd != null)
					wd.model = new InteractiveObject(wd.uid >> 14 & 0x7fff, 0, 4, i19, l19, j18, k20, animation, false);
			}
			if (objectGroup == 2) {
				GameObject go = worldController.getGameObject(localX, localY, plane);
				int usedType = type;
				if (usedType == 11) usedType = 10;
				if (go != null)
					go.model = new InteractiveObject(go.uid >> 14 & 0x7fff, face, usedType, i19, l19, j18, k20, animation, false);
			}
			if (objectGroup == 3) {
				com.bestbudz.world.GroundDecoration gd = worldController.getGroundDecoration(localY, localX, plane);
				if (gd != null)
					gd.model = new InteractiveObject(gd.uid >> 14 & 0x7fff, face, 22, i19, l19, j18, k20, animation, false);
			}
		}
	}

	private static void handleProjectileProto(int id, int startHeight, int endHeight,
			int delay, int duration, int curve, int lockOn, int offsetX, int offsetY,
			int absX, int absY) {
		int srcLocalX = absX - baseX;
		int srcLocalY = absY - baseY;
		int dstLocalX = srcLocalX + offsetX;
		int dstLocalY = srcLocalY + offsetY;
		if (srcLocalX >= 0 && srcLocalY >= 0 && srcLocalX < 104 && srcLocalY < 104
			&& dstLocalX >= 0 && dstLocalY >= 0 && dstLocalX < 104 && dstLocalY < 104
			&& id != 65535) {
			int srcPixelX = srcLocalX * 128 + 64;
			int srcPixelY = srcLocalY * 128 + 64;
			int dstPixelX = dstLocalX * 128 + 64;
			int dstPixelY = dstLocalY * 128 + 64;
			Projectile projectile = new Projectile(curve, endHeight,
				delay + loopCycle, duration + loopCycle,
				startHeight, plane,
				getTerrainHeight(plane, srcPixelY, srcPixelX) - startHeight * 4,
				srcPixelY, srcPixelX, lockOn, id);
			projectile.calculateTrajectory(delay + loopCycle, dstPixelY,
				getTerrainHeight(plane, dstPixelY, dstPixelX) - endHeight * 4, dstPixelX);
			nodeList.addFirst(projectile);
		}
	}

	private static void handleStillGraphicProto(int id, int z, int delay, int absX, int absY) {
		int localX = absX - baseX;
		int localY = absY - baseY;
		if (localX >= 0 && localY >= 0 && localX < 104 && localY < 104) {
			int pixelX = localX * 128 + 64;
			int pixelY = localY * 128 + 64;
			GraphicEffect graphicEffect = new GraphicEffect(plane, loopCycle, delay, id,
				getTerrainHeight(plane, pixelY, pixelX) - z, pixelY, pixelX);
			queueSpotAnimation.addFirst(graphicEffect);
		}
	}

	private static void handlePacketException(final Exception exception) {
		exception.printStackTrace();
		final StringBuilder errorBuilder = STRING_BUILDER_CACHE.get();
		errorBuilder.setLength(0);

		errorBuilder.append("T2 - proto exception - ");
		if (myStoner != null) {
			errorBuilder.append(baseX + myStoner.smallX[0]).append(",").append(baseY + myStoner.smallY[0]);
		}

		Signlink.reporterror(errorBuilder.toString());
		resetLogout();
	}

	public static void sendPacket(final int packet) {
		switch (packet) {
			case 103:
				sendProto(GamePacket.newBuilder().setCommandMessage(CommandMessage.newBuilder().setCommand(inputString.substring(2))).build());
				inputString = "";
				promptInput = "";
				break;

			case 1003:
				sendProto(GamePacket.newBuilder().setCommandMessage(CommandMessage.newBuilder().setCommand(inputString.substring(2))).build());
				promptInput = "";
				break;

			default:
				break;
		}
	}

	public static long getPacketCount() {
		return packetCount;
	}

	public static void resetPacketCount() {
		packetCount = 0;
		lastOptimizationCheck = System.currentTimeMillis();
	}
}
