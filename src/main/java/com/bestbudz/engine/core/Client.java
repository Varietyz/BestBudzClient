package com.bestbudz.engine.core;

import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.ui.manager.UIModalManager;
import static com.bestbudz.engine.ClientLauncher.gpuContextManager;
import static com.bestbudz.engine.ClientLauncher.gpuInitialized;
import static com.bestbudz.engine.ClientLauncher.initializeGPUAfterGraphicsLoad;
import static com.bestbudz.engine.core.GameState.runSceneRendering;
import static com.bestbudz.engine.core.GameState.validateGPUStateIfNeeded;
import static com.bestbudz.engine.core.LoadingErrorScreen.showErrorScreen;
import static com.bestbudz.engine.core.LoadingScreen.showBootScreen;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import static com.bestbudz.engine.gpu.GPURenderingEngine.initialized;
import com.bestbudz.engine.gpu.GPUShaders;
import com.bestbudz.graphics.text.TextController;
import static com.bestbudz.engine.core.gamerender.Camera.setCameraPos;
import static com.bestbudz.ui.handling.input.Keyboard.console;
import static com.bestbudz.ui.handling.input.Keyboard.keyArray;
import static com.bestbudz.ui.handling.input.MouseActions.handleClickPacket;
import static com.bestbudz.ui.handling.input.MouseActions.handleDragAndDrop;
import static com.bestbudz.ui.handling.RightClickMenu.processMenuClick;
import static com.bestbudz.engine.core.login.Login.updateEntityTextAndCamera;
import static com.bestbudz.engine.core.login.logout.Logout.resetLogout;
import static com.bestbudz.network.packets.PacketParser.handlePackets;
import static com.bestbudz.graphics.ClearExpiredProjectiles.clearExpiredProjectiles;
import static com.bestbudz.entity.EntityMovement.updateAllNpcs;
import static com.bestbudz.entity.EntityMovement.updateAllStoners;
import static com.bestbudz.entity.ParseAndUpdateEntities.parseStoners;
import static com.bestbudz.entity.ParseAndUpdateEntities.renderNPCs;
import static com.bestbudz.entity.UpdateEntities.updateEntities;
import static com.bestbudz.graphics.HeadIcon.drawHeadIcon;
import static com.bestbudz.graphics.MovingTextures.updateMovingTextures;
import static com.bestbudz.rendering.Roofing.getRoofPlane;
import static com.bestbudz.rendering.Roofing.selectRoofPlane;
import static com.bestbudz.rendering.SpotAnim2.updateSpotAnimationTimers;
import static com.bestbudz.ui.DialogHandling.handleBoxDialogue;
import static com.bestbudz.ui.InterfaceManagement.draw3dScreen;
import static com.bestbudz.ui.InterfaceManagement.drawGameScreen;
import static com.bestbudz.ui.NotificationMessages.displayKillFeed;
import static com.bestbudz.ui.TabArea.drawTabArea;
import static com.bestbudz.ui.interfaces.StatusOrbs.drawGameUIorbs;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents2;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents3;
import static com.bestbudz.world.GroundItem.spawnGroundItem;
import static com.bestbudz.world.InLocation.inMaze;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;
import static com.bestbudz.world.WalkTo.handleWalkToObject;

import com.bestbudz.cache.Signlink;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.items.Item;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.data.Skills;
import com.bestbudz.dock.util.DockSync;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.config.NetworkConfig;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.engine.core.login.Login;
import com.bestbudz.entity.Entity;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.cache.IdentityKit;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.graphics.Background;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.FogHandler;
import com.bestbudz.engine.core.gamerender.Texture;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.network.OnDemandData;
import com.bestbudz.network.OnDemandFetcher;
import com.bestbudz.network.RSSocket;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.Animable_Sub3;
import com.bestbudz.rendering.Animable_Sub4;
import com.bestbudz.rendering.Animable_Sub5;
import com.bestbudz.rendering.OverlayFloor;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.SpotAnim;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.ui.interfaces.StatusOrbs;
import com.bestbudz.util.ColorUtility;
import com.bestbudz.util.Decompressor;
import com.bestbudz.util.ISAACRandomGen;
import com.bestbudz.util.NodeList;
import com.bestbudz.world.Class11;
import com.bestbudz.world.Class30_Sub1;
import com.bestbudz.world.Floor;
import com.bestbudz.world.Object1;
import com.bestbudz.world.Object2;
import com.bestbudz.world.Object3;
import com.bestbudz.world.Object5;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import com.bestbudz.world.Varp;
import com.bestbudz.engine.core.gamerender.WorldController;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Client extends ClientEngine
{
	public static final int[][] anIntArrayArray1003 = {
		{6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131},
		{8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
		{25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
		{4626, 11146, 6439, 12, 4758, 10270, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131},
		{4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131}};
	public static final int[] tabInterfaceIDs = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1};
	public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public static final int[] anIntArray1204 = {9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486};
	public static final int MENU_Y_SHIFT = 15;
	public static final Decompressor[] decompressors = new Decompressor[6];
	public static final RSInterface aClass9_1059 = new RSInterface();
	public static final int currencies = 11;
	public static final Sprite[] currencyImage = new Sprite[currencies];
	public static final int[] tabAmounts = new int[]{420, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final int[] bankInvTemp = new int[420];
	public static final int[] bankStackTemp = new int[420];
	public static final int[] sideIconsX = {17, 49, 83, 114, 146, 180, 214, 16, 49, 82, 116, 148, 184, 216};
	public static final int[] sideIconsY = {9, 7, 6, 5, 2, 3, 7, 306, 306, 306, 302, 305, 303, 303, 303};
	public static final int[] sideIconsId = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, -1};
	public static final int[] sideIconsTab = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	public static final int[] redStonesX = {6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209};
	public static final int[] redStonesY = {0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298};
	public static final int[] redStonesId = {0, 4, 4, 4, 4, 4, 1, 2, 4, 4, 4, 4, 4, 3};
	public static final int[] tabClickX = {38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38};
	public static final int[] tabClickStart = {522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724};
	public static final int[] tabClickY = {169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466};
	public static final long[] currentExp = new long[Skills.SKILLS_COUNT];
	public static final int[] anIntArray873 = new int[5];
	public static final boolean[] aBooleanArray876 = new boolean[5];
	public static final int maxStoners = 2048;
	public static final int myStonerIndex = 2047;
	public static final int[] currentStats = new int[Skills.SKILLS_COUNT];
	public static final long[] ignoreListAsLongs = new long[100];
	public static final int[] anIntArray928 = new int[5];
	public static String[] chatMessages = new String[500];
	public static final String[] clanList = new String[100];
	public static final int[] anIntArray965 = {
		ColorConfig.CHAT_COLOR, // pastel yellow
		ColorConfig.CHAT_SOFT_PINK, // soft pink
		ColorConfig.CHAT_BABY_BLUE, // baby blue
		ColorConfig.CHAT_MINT_AQUA, // mint aqua
		ColorConfig.CHAT_LIGHT_MAGENTA, // light magenta
		ColorConfig.WHITE_COLOR  // white (kept for contrast pulse)
	};
	public static final int[] anIntArray968 = new int[33];
	public static final int anInt975 = 50;
	public static final int[] anIntArray976 = new int[anInt975];
	public static final int[] anIntArray977 = new int[anInt975];
	public static final int[] anIntArray978 = new int[anInt975];
	public static final int[] anIntArray979 = new int[anInt975];
	public static final int[] anIntArray980 = new int[anInt975];
	public static final int[] anIntArray981 = new int[anInt975];
	public static final int[] anIntArray982 = new int[anInt975];
	public static final String[] aStringArray983 = new String[anInt975];
	public static final Sprite[] hitMark = new Sprite[20];
	public static final Sprite[] hitIcon = new Sprite[20];
	public static final int[] anIntArray990 = new int[5];
	public static final boolean aBoolean994 = false;
	public static final int[] anIntArray1030 = new int[5];
	public static final int[] maxStats = new int[Skills.SKILLS_COUNT];
	public static final int[] anIntArray1045 = new int[2000];
	public static final int[] anIntArray1052 = new int[152];
	public static final int[] anIntArray1057 = new int[33];
	public static final int barFillColor = 0x4d4233;
	public static final int[] anIntArray1065 = new int[7];
	public static final int[] expectedCRCs = new int[9];
	public static final String[] atStonerActions = new String[5];
	public static final boolean[] atStonerArray = new boolean[5];
	public static final int[][][] anIntArrayArrayArray1129 = new int[4][13][13];
	public static final boolean genericLoadingError = false;
	public static final int[] anIntArray1203 = new int[5];
	public static final int[] anIntArray1207 = new int[50];
	public static final int[] anIntArray1229 = new int[152];
public static final int[] anIntArray1240 = new int[100];
	public static final int[] anIntArray1241 = new int[50];
	public static final int[] anIntArray1250 = new int[50];
	public static final boolean rsAlreadyLoaded = false;
	public static final boolean canGainXP = true;
	private static final FogHandler fogHandler = new FogHandler();
	private static final int[] anIntArray1177 = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
	public static int fps = 0;
	public static boolean loginInProgress = false;
	public static boolean loginComplete = false;
	public static int[] currentAdvances = new int[Skills.SKILLS_COUNT];
	public static boolean controlIsDown = false;
	public static String chatColorHex = "00FFFF";
	public static int frameWidth = 1280;
	public static int frameHeight = 720;
	public static int screenAreaWidth = 1280;
	public static int screenAreaHeight = 720;
	public static int cameraZoom = EngineConfig.CAMERA_ZOOM;
	public static boolean showTabComponents = false;
	public static boolean changeChatArea = true;
	public static boolean transparentTabArea = true;
	public static int npcBits = 16;
	public static int extendChatArea = 0;
	public static Client instance;
	public static BufferedImage LOGIN_BACKGROUND;
	public static BufferedImage BACKGROUND;
	public static BufferedImage LOADING_BAR;
	public static Sprite[] cacheSprite;
	public static int portOff;
	public static int loadingStage;
	public static int anInt1089;
	public static int spellID = 0;
	public static ImageProducer aRSImageProducer_1109;
	public static Stoner myStoner;
	public static boolean loggedIn;
	public static int loopCycle;
	public static String myUsername;
	public static String myPassword;
	public static Stream stream;
	public static boolean inputTaken;
	public static int[] anIntArray1232;
	public static Sprite minimapImage;
	public static int[] fullScreenTextureArray;
	public static TextDrawingArea smallText;
	public static TextDrawingArea regularText;
	public static TextDrawingArea boldText;
	public static TextController newRegularFont;
	public static TextController newBoldFont;
	public static TextController newFancyFont;
	public static int backDialogID;
	public static String server = "";
	public static boolean rememberMe = false;
	public static int openInterfaceID;
	public static int myPrivilege;
	public static int anInt1211;
	public static int tabID;
	public static int anInt849;
	public static int anInt924;
	public static int nodeID = 10;
	public static boolean isMembers = true;
	public static boolean lowMem;
	public static int anInt986;
	public static boolean tabAreaAltered;
	public static int anInt1117;
	public static int anInt1134;
	public static int anInt1155;
	public static ImageProducer mainGameRendering;
	public static int anInt1175;
	public static int[] anIntArray1180;
	public static int[] anIntArray1181;
	public static int[] anIntArray1182;
	public static int anInt1188;
	public static boolean flagged;
	public static int anInt1226;
	public static int anInt1288;
	// config
	public static boolean clientData = true;
	public static int channelRights;
	public static int rights;
	public static String message;
	public static int chatTypeView;
	public static int clanChatMode;
	public static int[] anIntArray828;
	public static int[] anIntArray829;
	public static int loginScreenState;
	//public static String prayerBook;
	public static int[] anIntArray850;
		public static Background aBackground_966;
	public static Background aBackground_967;
	public static int[] variousSettings;
	public static int dragCycle;
	public static int loginFailures;
	public static Sprite[] skillIcons = new Sprite[22];
	public static Sprite[] newHitMarks = new Sprite[4];
	public static Sprite[] channelButtons = new Sprite[4];
	public static Sprite[] fixedGameComponents = new Sprite[4];
	public static Sprite[] gameComponents = new Sprite[5];
	public static Sprite[] redStones = new Sprite[6];
	public static Sprite[] hpBars = new Sprite[2];
	public static OnDemandFetcher onDemandFetcher;
	public static int focusedDragWidget;
	public static ImageProducer aRSImageProducer_1108;
	public static ImageProducer aRSImageProducer_1112;
	public static ImageProducer aRSImageProducer_1113;
	public static ImageProducer aRSImageProducer_1114;
	public static ImageProducer aRSImageProducer_1115;
	public static String loginMessage1;
	public static String loginMessage2;
	public static int drawCount;
	public static int fullscreenInterfaceID;
	public static int anInt1044;
	public static int anInt1129;
	public static int anInt1315;
	public static int anInt1500;
	public static int anInt1501;
	public static TextController newSmallFont;
	public static String[] feedKiller = new String[5];
	public static String[] feedVictim = new String[5];
	public static int[] feedWeapon = new int[5];
	public static boolean[] feedPoison = new boolean[5];
	public static Sprite[] feedImage = new Sprite[5];
	public static int[] feedAlpha = new int[5];
	public static int[] feedYPos = new int[5];
	public static int killsDisplayed = 5;
	public static int stonerIndex = 0;
	public static Sprite stock;
	public static String clanUsername;
	public static String clanMessage;
	public static String clanTitle;
	public static AccountData currentAccount;
	public static Npc npcDisplay;
	public static boolean walkableInterfaceMode = false;
	public static boolean prayClicked;
	public static ImageProducer leftFrame;
	public static ImageProducer topFrame;
	public static int ignoreCount;
	public static long aLong824;
	public static int[] stonersNodeIDs;
	public static NodeList[][][] groundArray;
	public static volatile boolean aBoolean831;
	public static Stream aStream_834;
	public static Npc[] npcArray;
	public static int npcCount;
	public static int[] npcIndices;
	public static int anInt841;
	public static int anInt842;
	public static int anInt843;
	public static String aString844;
	public static Stream aStream_847;
	public static boolean soundProduction;
	public static int[] anIntArray851;
	public static int[] anIntArray852;
	public static int[] anIntArray853;
	public static int anInt855;
	public static int xCameraPos;
	public static int zCameraPos;
	public static int yCameraPos;
	public static int yCameraCurve;
	public static int xCameraCurve;
	public static Sprite mapFlag;
	public static Sprite mapMarker;
	public static int weight;
	public static int unknownInt10;
	public static boolean menuOpen;
	public static int anInt886;
	public static String inputString;
	public static Stoner[] stonerArray;
	public static int stonerCount;
	public static int[] stonerIndices;
	public static Stream[] aStreamArray895s;
	public static int anInt896;
	public static int stonersCount;
	public static int anInt900;
	public static int crossX;
	public static int crossY;
	public static int crossIndex;
	public static int crossType;
	public static int plane;
	public static boolean loadingError;
	public static Sprite aClass30_Sub2_Sub1_Sub1_931;
	public static Sprite aClass30_Sub2_Sub1_Sub1_932;
	public static int anInt933;
	public static int anInt934;
	public static int anInt935;
	public static int anInt936;
	public static int anInt937;
	public static int anInt938;
	public static int anInt945;
	public static WorldController worldController;
	public static Sprite[] sideIcons;
	public static int menuScreenArea;
	public static int menuOffsetX;
	public static int menuOffsetY;
	public static int menuWidth;
	public static int menuHeight;
	public static long aLong953;
	public static boolean aBoolean954;
	public static long[] stonersListAsLongs;
	public static int currentSong;
	public static int spriteDrawX;
	public static int spriteDrawY;
	public static boolean aBoolean972;
	public static int anInt985;
	public static Sprite[] hitMarks;
	public static int anInt995;
	public static int anInt996;
	public static int anInt997;
	public static int anInt998;
	public static int anInt999;
	public static ISAACRandomGen encryption;
	public static Sprite multiOverlay;
	public static String amountOrNameInput;
	public static int daysSinceLastLogin;
	public static int pktSize;
	public static int pktType;
	public static int anInt1009;
	public static int anInt1011;
	public static NodeList aClass19_1013;
	public static int anInt1018;
	public static int anInt1021;
	public static int anInt1022;
	public static Sprite scrollBar1;
	public static Sprite scrollBar2;
	public static int anInt1026;
	public static boolean aBoolean1031;
	public static Sprite[] mapFunctions;
	public static int baseX;
	public static int baseY;
	public static int anInt1036;
	public static int anInt1037;
	public static int anInt1039;
	public static int dialogID;
	public static int anInt1046;
	public static boolean aBoolean1047;
	public static int anInt1048;
	public static String aString1049;
	public static StreamLoader titleStreamLoader;
	public static int anInt1054;
	public static int anInt1055;
	public static NodeList aClass19_1056;
	public static Background[] mapScenes;
	public static int anInt1062;
	public static int stonersListAction;
	public static int mouseInvInterfaceIndex;
	public static int lastActiveInvInterface;
	public static int anInt1069;
	public static int anInt1070;
	public static Sprite mapDotItem;
	public static Sprite mapDotNPC;
	public static Sprite mapDotStoner;
	public static Sprite mapDotTeam;
	public static Sprite mapDotClan;
	public static int anInt1079;
	public static boolean aBoolean1080;
	public static String[] stonersList;
	public static Stream inStream;
	public static int dragFromSlot;
	public static int activeInterfaceType;
	public static int pressX;
	public static int pressY;
	public static int[] menuActionCmd2;
	public static int[] menuActionCmd3;
	public static int[] menuActionID;
	public static int[] menuActionCmd1;
	public static Sprite[] headIcons;
	public static Sprite[] skullIcons;
	public static Sprite[] headIconsHint;
	public static int anInt1098;
	public static int anInt1099;
	public static int anInt1100;
	public static int anInt1101;
	public static int anInt1102;
	public static int anInt1104;
	public static ImageProducer aRSImageProducer_1110;
	public static ImageProducer aRSImageProducer_1111;
	public static int membersInt;
	public static String aString1121;
	public static ImageProducer aRSImageProducer_1125;
	public static int anInt1131;
	public static int menuActionRow;
	public static int spellSelected;
	public static int anInt1137;
	public static int spellUsableOn;
	public static String spellTooltip;
	public static boolean aBoolean1141;
	public static int energy;
	public static boolean aBoolean1149;
	public static Sprite[] crosses;
	public static boolean musicEnabled;
	public static Background[] aBackgroundArray1152s;
	public static int unreadMessages;
	public static boolean canMute;
	public static boolean aBoolean1159;
	public static boolean aBoolean1160;
	public static int daysSinceRecovChange;
	public static RSSocket socketStream;
	public static int anInt1169;
	public static int minimapInt3;
	public static int reportAbuseInterfaceID;
	public static NodeList aClass19_1179;
	public static byte[][] aByteArrayArray1183;
	public static int minimapInt1;
	public static int invOverlayInterfaceID;
	public static int[] anIntArray1190;
	public static int[] anIntArray1191;
	public static int anInt1193;
	public static Background mapBack;
	public static String[] menuActionName;
	public static Sprite aClass30_Sub2_Sub1_Sub1_1201;
	public static Sprite aClass30_Sub2_Sub1_Sub1_1202;
	public static int minimapInt2;
	public static String promptInput;
	public static int anInt1213;
	public static int[][][] intGroundArray;
	public static long aLong1215;
	public static long aLong1220;
	public static int anInt1222;
	public static int inputDialogState;
	public static int nextSong;
	public static boolean songChanging;
	public static Class11[] aClass11Array1230;
	public static int[] anIntArray1234;
	public static int[] anIntArray1235;
	public static int[] anIntArray1236;
	public static boolean aBoolean1242;
	public static int atBoxLoopCycle;
	public static int atBoxInterface;
	public static int atBoxIndex;
	public static int atBoxInterfaceType;
	public static byte[][] aByteArrayArray1247;
	public static int tradeMode;
	public static int anInt1249;
	public static int anInt1251;
	public static boolean welcomeScreenRaised;
	public static boolean messagePromptRaised;
	public static byte[][][] byteGroundArray;
	public static int prevSong;
	public static int destX;
	public static int destY;
	public static int anInt1264;
	public static int anInt1265;
	public static int anInt1268;
	public static int anInt1269;
	public static int anInt1278;
	public static int itemSelected;
	public static int anInt1283;
	public static int anInt1284;
	public static int anInt1285;
	public static String selectedItemName;
	public static int modifiableXValue = 0;
	public static GameCanvas canvas;
	public static boolean filterGrayScale = false;
	public static int anInt854;
	public static int anInt1005;
	public static int anInt1051;
	public static int anInt1097;
	public static boolean fpsOn = true;
	public static int[][] anIntArrayArray825;
	public static int anInt839;
	public static int[] anIntArray840;
	public static int anInt893;
	public static int[] anIntArray894;
	public static int[][] anIntArrayArray901;
	public static byte[] aByteArray912;
	public static int anInt913;
	public static int[][] anIntArrayArray929;
	public static int anInt984;
	public static int anInt1014;
	public static int anInt1015;
	public static int anInt1184;
	public static int legacyClickInt;
	public static int[] bigX;
	public static int[] bigY;
	public static int anInt1186;
	public static int anInt1187;
	private static ImageProducer aRSImageProducer_1163;
	private static ImageProducer aRSImageProducer_1166;
	private static Socket aSocket832;
	private static boolean aBoolean872;
	private static int[] anIntArray1072;
	private static int[] anIntArray1073;
	private static ImageProducer aRSImageProducer_1107;
	private static Sprite[] aClass30_Sub2_Sub1_Sub1Array1140;
	private static ImageProducer aRSImageProducer_1164;
	private UIModalManager uiModalManager;

	static {
		try {
			LOGIN_BACKGROUND = ImageIO.read(Client.class.getResource("/loading/login_background.png"));
			System.out.println("[DEBUG] BG_COLOR: " + LOGIN_BACKGROUND);
			BACKGROUND = ImageIO.read(Client.class.getResource("/loading/login_background.png"));
			System.out.println("[DEBUG] BG_COLOR: " + BACKGROUND);
			LOADING_BAR = ImageIO.read(Client.class.getResource("/loading/bar.png"));
			System.out.println("[DEBUG] BAR: " + LOADING_BAR);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		anIntArray1232 = new int[32];
		int i = 2;
		for (int k = 0; k < 32; k++) {
			anIntArray1232[k] = i - 1;
			i += i;
		}
	}

	public final int[] anIntArray969;
	private final String[] chatNames;
	public String name;
	public int loginScreenCursorPos;
	private int frameCount = 0;
	private long lastFpsTime = System.currentTimeMillis();
	private int anInt1010;
	private int anInt1016;
	private boolean aBoolean1017;

	public Client()
	{
		EngineConfig.worldSelected = 1;
		fullscreenInterfaceID = -1;
		chatTypeView = 0;
		clanChatMode = 0;
		server = NetworkConfig.SERVER_IPS[EngineConfig.worldSelected - 1];
		anIntArrayArray825 = new int[104][104];
		stonersNodeIDs = new int[200];
		groundArray = new NodeList[4][104][104];
		aBoolean831 = false;
		aStream_834 = new Stream(new byte[5000]);
		npcArray = new Npc[16384];
		npcIndices = new int[16384];
		anIntArray840 = new int[1000];
		aStream_847 = Stream.create();
		soundProduction = true;
		openInterfaceID = -1;
		aBoolean872 = false;
		unknownInt10 = -1;
		menuOpen = false;
		inputString = "";
		stonerArray = new Stoner[maxStoners];
		stonerIndices = new int[maxStoners];
		anIntArray894 = new int[maxStoners];
		aStreamArray895s = new Stream[maxStoners];
		anIntArrayArray901 = new int[104][104];
		aByteArray912 = new byte[16384];
		loadingError = false;
		anIntArrayArray929 = new int[104][104];
		chatNames = new String[500];
		sideIcons = new Sprite[17];
		aBoolean954 = true;
		stonersListAsLongs = new long[200];
		currentSong = -1;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray969 = new int[256];
		variousSettings = new int[2000];
		aBoolean972 = false;

		anInt985 = -1;
		hitMarks = new Sprite[20];
		amountOrNameInput = "";
		aClass19_1013 = new NodeList();
		aBoolean1017 = false;
		anInt1018 = -1;
		aBoolean1031 = false;
		mapFunctions = new Sprite[100];
		dialogID = -1;
		aBoolean1047 = true;
		anInt1054 = -1;
		aClass19_1056 = new NodeList();

		mapScenes = new Background[100];
		anIntArray1072 = new int[1000];
		anIntArray1073 = new int[1000];
		aBoolean1080 = false;
		stonersList = new String[200];
		inStream = Stream.create();
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[20];
		skullIcons = new Sprite[20];
		headIconsHint = new Sprite[20];
		tabAreaAltered = false;
		aString1121 = "";
		aClass30_Sub2_Sub1_Sub1Array1140 = new Sprite[1000];
		aBoolean1141 = false;
		aBoolean1149 = false;
		crosses = new Sprite[8];
		musicEnabled = true;
		loggedIn = false;
		canMute = false;
		aBoolean1159 = false;
		aBoolean1160 = false;
		myUsername = "";
		myPassword = "";
		reportAbuseInterfaceID = -1;
		aClass19_1179 = new NodeList();
		anInt1184 = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.create();
		menuActionName = new String[500];
		anInt1211 = 78;
		promptInput = "";
		tabID = 3;
		inputTaken = false;
		songChanging = true;
		aClass11Array1230 = new Class11[4];
		aBoolean1242 = false;
		welcomeScreenRaised = false;
		messagePromptRaised = false;
		loginMessage1 = "Best Budz - Where weed is legal.";
		loginMessage2 = "The weed flows, where the tree grows!";
		backDialogID = -1;
		bigX = new int[4000];
		bigY = new int[4000];
	}


	public UIModalManager getUIModalManager() {
		UIDockFrame dockFrame = UIDockFrame.getInstance();
		return dockFrame != null ? dockFrame.getModalManager() : null;
	}


	public static void setCanvas(GameCanvas canvas) {
	}


	public static void setBounds()
	{
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		fullScreenTextureArray = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		anIntArray1180 = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		anIntArray1181 = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		anIntArray1182 = Rasterizer.scanlineOffsets;
		int[] ai = new int[9];
		for (int i8 = 0; i8 < 9; i8++)
		{
			int k8 = 128 + i8 * 32 + 15;
			int l8 = 600 + k8 * 3;
			int i9 = Rasterizer.sinTable[k8];
			ai[i8] = l8 * i9 >> 16;
		}

			WorldController.viewDistance = 10;
			cameraZoom = EngineConfig.CAMERA_ZOOM;

		if (extendChatArea > frameHeight - 170)
		{
			extendChatArea = frameHeight - 170;
		}
		WorldController.method310(500, 800, frameWidth, frameHeight, ai);
		if (loggedIn)
		{
			mainGameRendering = new ImageProducer(frameWidth, frameHeight);
		}
	}


	public static AbstractMap.SimpleEntry<Integer, Integer> getNextInteger(ArrayList<Integer> values)
	{
		ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> frequencies = new ArrayList<>();
		int maxIndex = 0;
		main:
		for (int i = 0; i < values.size(); ++i)
		{
			int value = values.get(i);
			for (int j = 0; j < frequencies.size(); ++j)
			{
				if (frequencies.get(j).getKey() == value)
				{
					frequencies.get(j).setValue(frequencies.get(j).getValue() + 1);
					if (frequencies.get(maxIndex).getValue() < frequencies.get(j).getValue())
					{
						maxIndex = j;
					}
					continue main;
				}
			}
			frequencies.add(new AbstractMap.SimpleEntry<Integer, Integer>(value, 1));
		}
		return frequencies.get(maxIndex);
	}


	public static void setHighMem()
	{
		WorldController.lowMem = false;
		Rasterizer.lowMemoryMode = false;
		lowMem = false;
		ObjectManager.lowMem = false;
		ObjectDef.lowMem = false;
	}

	public static void stopMidi()
	{
		Signlink.midifade = 0;
		Signlink.midi = "stop";
	}

	public static Socket openSocket(int port) throws IOException
	{
		return new Socket(InetAddress.getByName(NetworkConfig.LOCALHOST ? "localhost" : server), port);
	}

	public static void unlinkMRUNodes()
	{
		ObjectDef.mruNodes1.unlinkAll();
		ObjectDef.mruNodes2.unlinkAll();
		EntityDef.mruNodes.unlinkAll();
		ItemDef.mruNodes2.unlinkAll();
		ItemDef.mruNodes1.unlinkAll();
		Stoner.mruNodes.unlinkAll();
		//SpotAnim.aMRUNodes_415.unlinkAll();
	}

	public static StreamLoader streamLoaderForName(int i, String s, String s1, int j, int k, Graphics2D g)
	{
		byte[] abyte0 = null;
		int l = 5;
		try
		{
			if (decompressors[0] != null)
				abyte0 = decompressors[0].decompress(i);
		}
		catch (Exception _ex)
		{
		}
		if (abyte0 != null)
		{
		}
		if (abyte0 != null)
		{
			StreamLoader streamLoader = new StreamLoader(abyte0);
			return streamLoader;
		}
		int j1 = 0;
		while (abyte0 == null)
		{
			String s2 = "Unknown error";
			try
			{
				int k1 = 0;
				DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
				byte[] abyte1 = new byte[6];
				datainputstream.readFully(abyte1, 0, 6);
				Stream stream = new Stream(abyte1);
				stream.currentOffset = 3;
				int i2 = stream.read3Bytes() + 6;
				int j2 = 6;
				abyte0 = new byte[i2];
				System.arraycopy(abyte1, 0, abyte0, 0, 6);

				while (j2 < i2)
				{
					int l2 = i2 - j2;
					if (l2 > 1000)
						l2 = 1000;
					int j3 = datainputstream.read(abyte0, j2, l2);
					if (j3 < 0)
					{
						s2 = "Length error: " + j2 + "/" + i2;
						throw new IOException("EOF");
					}
					j2 += j3;
					int k3 = (j2 * 100) / i2;
					if (k3 != k1)
					k1 = k3;
				}
				datainputstream.close();
				try
				{
					if (decompressors[0] != null)
						decompressors[0].method234(abyte0.length, abyte0, i);
				}
				catch (Exception _ex)
				{
					decompressors[0] = null;
				}
			}
			catch (IOException ioexception)
			{
				if (s2.equals("Unknown error"))
					s2 = "Connection error";
				abyte0 = null;
			}
			catch (NullPointerException _ex)
			{
				s2 = "Null error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			}
			catch (ArrayIndexOutOfBoundsException _ex)
			{
				s2 = "Bounds error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			}
			catch (Exception _ex)
			{
				s2 = "Unexpected error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			}
			if (abyte0 == null)
			{
				for (int l1 = l; l1 > 0; l1--)
				{
					if (j1 >= 3)
					{
						l1 = 10;
					}
					else
					{
					}
					try
					{
						Thread.sleep(1000L);
					}
					catch (Exception _ex)
					{
					}
				}

				l *= 2;
				if (l > 60)
					l = 60;
				aBoolean872 = !aBoolean872;
			}

		}

		StreamLoader streamLoader_1 = new StreamLoader(abyte0);
		return streamLoader_1;
	}

	public static boolean inBounds(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	public static void resetImageProducers2()
	{
		if (aRSImageProducer_1166 != null)
			return;
		nullLoader();
		gameWorldScreen = null;
		aRSImageProducer_1107 = null;
		aRSImageProducer_1108 = null;
		aRSImageProducer_1109 = null;
		aRSImageProducer_1110 = null;
		aRSImageProducer_1111 = null;
		aRSImageProducer_1112 = null;
		aRSImageProducer_1113 = null;
		aRSImageProducer_1114 = null;
		aRSImageProducer_1115 = null;
		aRSImageProducer_1166 = new ImageProducer(519, 165);
		aRSImageProducer_1164 = new ImageProducer(249, 168);
		DrawingArea.setAllPixelsToZero();
		fixedGameComponents[0].drawSprite(0, 0);
		aRSImageProducer_1163 = new ImageProducer(249, 335);
		mainGameRendering = new ImageProducer(frameWidth, frameHeight);
		DrawingArea.setAllPixelsToZero();
		aRSImageProducer_1125 = new ImageProducer(249, 45);
		welcomeScreenRaised = true;
	}

	public static void parseNpcUpdateMasks(Stream stream)
	{
		for (int j = 0; j < anInt893; j++)
		{
			int k = anIntArray894[j];
			Npc npc = npcArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x10) != 0)
			{
				int i1 = stream.method434();
				if (i1 == 65535)
					i1 = -1;
				int i2 = stream.readUnsignedByte();
				if (i1 == npc.anim && i1 != -1)
				{
					int l2 = Animation.anims[i1].anInt365;
					if (l2 == 1)
					{
						npc.anInt1527 = 0;
						npc.anInt1528 = 0;
						npc.anInt1529 = i2;
						npc.anInt1530 = 0;
					}
					if (l2 == 2)
						npc.anInt1530 = 0;
				}
				else if (i1 == -1 || npc.anim == -1
					|| Animation.anims[i1].anInt359 >= Animation.anims[npc.anim].anInt359)
				{
					npc.anim = i1;
					npc.anInt1527 = 0;
					npc.anInt1528 = 0;
					npc.anInt1529 = i2;
					npc.anInt1530 = 0;
					npc.anInt1542 = npc.smallXYIndex;
				}
			}
			if ((l & 8) != 0)
			{
				int j1 = stream.method426();
				int j2 = stream.method427();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(j2, j1, loopCycle, icon);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.method426();
				npc.maxHealth = stream.readUnsignedByte();
			}
			if ((l & 0x80) != 0)
			{
				npc.anInt1520 = stream.readUnsignedWord();
				int k1 = stream.readDWord();
				npc.anInt1524 = k1 >> 16;
				npc.anInt1523 = loopCycle + (k1 & 0xffff);
				npc.anInt1521 = 0;
				npc.anInt1522 = 0;
				if (npc.anInt1523 > loopCycle)
					npc.anInt1521 = -1;
				if (npc.anInt1520 == 65535)
					npc.anInt1520 = -1;
			}
			if ((l & 0x20) != 0)
			{
				npc.interactingEntity = stream.readUnsignedWord();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((l & 1) != 0)
			{
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;
			}
			if ((l & 0x40) != 0)
			{
				int l1 = stream.method427();
				int k2 = stream.method428();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(k2, l1, loopCycle, icon);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.method428();
				npc.maxHealth = stream.method427();
			}
			if ((l & 2) != 0)
			{
				npc.desc = EntityDef.forID(stream.method436());
				npc.anInt1540 = npc.desc.aByte68;
				npc.anInt1504 = npc.desc.anInt79;
				npc.anInt1554 = npc.desc.walkAnim;
				npc.anInt1555 = npc.desc.anInt58;
				npc.anInt1556 = npc.desc.anInt83;
				npc.anInt1557 = npc.desc.anInt55;
				npc.anInt1511 = npc.desc.standAnim;
			}
			if ((l & 4) != 0)
			{
				npc.anInt1538 = stream.method434();
				npc.anInt1539 = stream.method434();
			}
		}
	}

	public static void processSpotAnimations(Class30_Sub1 class30_sub1)
	{
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (class30_sub1.anInt1296 == 0)
			i = worldController.method300(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 1)
			i = worldController.method301(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 2)
			i = worldController.method302(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 3)
			i = worldController.method303(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (i != 0)
		{
			int i1 = worldController.method304(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298,
				i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		class30_sub1.anInt1299 = j;
		class30_sub1.anInt1301 = k;
		class30_sub1.anInt1300 = l;
	}

	public static void parseProjectiles(Stream stream, int i)
	{
		while (stream.bitPosition + 10 < i * 8)
		{
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (stonerArray[j] == null)
			{
				stonerArray[j] = new Stoner();
				if (aStreamArray895s[j] != null)
					stonerArray[j].updateStoner(aStreamArray895s[j]);
			}
			stonerIndices[stonerCount++] = j;
			Stoner stoner = stonerArray[j];
			stoner.anInt1537 = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				anIntArray894[anInt893++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			stoner.setPos(myStoner.smallX[0] + j1, myStoner.smallY[0] + i1, l == 1);
		}
		stream.finishBitAccess();
	}

	public static void renderChatIfInvalidated() {
		if (inputTaken) {
			inputTaken = false;
		}
	}

	private static void processAnimableGraphics()
	{
		Animable_Sub3 class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetFirst();
		for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetNext())
			if (class30_sub2_sub4_sub3.anInt1560 != plane || class30_sub2_sub4_sub3.aBoolean1567)
				class30_sub2_sub4_sub3.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub3.anInt1564)
			{
				class30_sub2_sub4_sub3.method454(anInt945);
				if (class30_sub2_sub4_sub3.aBoolean1567)
					class30_sub2_sub4_sub3.unlink();
				else
					worldController.method285(class30_sub2_sub4_sub3.anInt1560, 0, class30_sub2_sub4_sub3.anInt1563, -1,
						class30_sub2_sub4_sub3.anInt1562, 60, class30_sub2_sub4_sub3.anInt1561,
						class30_sub2_sub4_sub3, false);
			}

	}

	public static boolean isStonerOrSelf(String s)
	{
		if (s == null)
			return false;
		for (int i = 0; i < stonersCount; i++)
			if (s.equalsIgnoreCase(stonersList[i]))
				return true;
		return s.equalsIgnoreCase(myStoner.name);
	}

	private static void nullLoader()
	{
		aBoolean831 = false;
		aBackground_966 = null;
		aBackground_967 = null;
		aBackgroundArray1152s = null;
		anIntArray850 = null;
		anIntArray851 = null;
		anIntArray852 = null;
		anIntArray853 = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
		anIntArray828 = null;
		anIntArray829 = null;
		aClass30_Sub2_Sub1_Sub1_1201 = null;
		aClass30_Sub2_Sub1_Sub1_1202 = null;
	}

	public static void npcScreenPos(Entity entity, int i)
	{
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	public static void calcEntityScreenPos(int i, int j, int l)
	{
		if (i < 128 || l < 128 || i > 13056 || l > 13056)
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		int i1 = getTerrainHeight(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;
		int j1 = Model.modelIntArray1[yCameraCurve];
		int k1 = Model.modelIntArray2[yCameraCurve];
		int l1 = Model.modelIntArray1[xCameraCurve];
		int i2 = Model.modelIntArray2[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50)
		{
			spriteDrawX = Rasterizer.viewportCenterX + (i << WorldController.viewDistance) / l;
			spriteDrawY = Rasterizer.viewportCenterY + (i1 << WorldController.viewDistance) / l;
		}
		else
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	private static void getOrCreateSpotAnimation(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2)
	{
		Class30_Sub1 class30_sub1 = null;
		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
			.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
			.reverseGetNext())
		{
			if (class30_sub1_1.anInt1295 != l1 || class30_sub1_1.anInt1297 != i2 || class30_sub1_1.anInt1298 != j1
				|| class30_sub1_1.anInt1296 != i1)
				continue;
			class30_sub1 = class30_sub1_1;
			break;
		}

		if (class30_sub1 == null)
		{
			class30_sub1 = new Class30_Sub1();
			class30_sub1.anInt1295 = l1;
			class30_sub1.anInt1296 = i1;
			class30_sub1.anInt1297 = i2;
			class30_sub1.anInt1298 = j1;
			processSpotAnimations(class30_sub1);
			aClass19_1179.insertHead(class30_sub1);
		}
		class30_sub1.anInt1291 = k;
		class30_sub1.anInt1293 = k1;
		class30_sub1.anInt1292 = l;
		class30_sub1.anInt1302 = j2;
		class30_sub1.anInt1294 = j;
	}

	private static DataInputStream openJagGrabInputStream(String s) throws IOException
	{
		if (aSocket832 != null) {
			try {
				aSocket832.close();
			} catch (Exception _ex) {
			}
			aSocket832 = null;
		}
		aSocket832 = openSocket(NetworkConfig.CLIENT_PORT);
		aSocket832.setSoTimeout(10000);
		InputStream inputstream = aSocket832.getInputStream();
		OutputStream outputstream = aSocket832.getOutputStream();
		outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	public static void handleGroundItemUpdate(Stream stream, int j)
	{
		if (j == 84)
		{
			int k = stream.readUnsignedByte();
			int j3 = anInt1268 + (k >> 4 & 7);
			int i6 = anInt1269 + (k & 7);
			int l8 = stream.readUnsignedWord();
			int k11 = stream.readUnsignedWord();
			int l13 = stream.readUnsignedWord();
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104)
			{
				NodeList class19_1 = groundArray[plane][j3][i6];
				if (class19_1 != null)
				{
					for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1
						.reverseGetFirst(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1
						.reverseGetNext())
					{
						if (class30_sub2_sub4_sub2_3.ID != (l8 & 0x7fff) || class30_sub2_sub4_sub2_3.anInt1559 != k11)
							continue;
						class30_sub2_sub4_sub2_3.anInt1559 = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105)
		{
			int l = stream.readUnsignedByte();
			int k3 = anInt1268 + (l >> 4 & 7);
			int j6 = anInt1269 + (l & 7);
			int i9 = stream.readUnsignedWord();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
			int i16 = l11 & 7;
			if (myStoner.smallX[0] >= k3 - i14 && myStoner.smallX[0] <= k3 + i14 && myStoner.smallY[0] >= j6 - i14
				&& myStoner.smallY[0] <= j6 + i14 && soundProduction && !lowMem && anInt1062 < 50)
			{
				anIntArray1207[anInt1062] = i9;
				anIntArray1241[anInt1062] = i16;
				anIntArray1250[anInt1062] = i9;
				anInt1062++;
			}
		}
		if (j == 215)
		{
			int i1 = stream.method435();
			int l3 = stream.method428();
			int k6 = anInt1268 + (l3 >> 4 & 7);
			int j9 = anInt1269 + (l3 & 7);
			int i12 = stream.method435();
			int j14 = stream.readUnsignedWord();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != unknownInt10)
			{
				Item class30_sub2_sub4_sub2_2 = new Item();
				class30_sub2_sub4_sub2_2.ID = i1;
				class30_sub2_sub4_sub2_2.anInt1559 = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new NodeList();
				groundArray[plane][k6][j9].insertHead(class30_sub2_sub4_sub2_2);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156)
		{
			int j1 = stream.method426();
			int i4 = anInt1268 + (j1 >> 4 & 7);
			int l6 = anInt1269 + (j1 & 7);
			int k9 = stream.readUnsignedWord();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104)
			{
				NodeList class19 = groundArray[plane][i4][l6];
				if (class19 != null)
				{
					for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19
						.reverseGetNext())
					{
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (class19.reverseGetFirst() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160)
		{
			int k1 = stream.method428();
			int j4 = anInt1268 + (k1 >> 4 & 7);
			int i7 = anInt1269 + (k1 & 7);
			int l9 = stream.method428();
			int j12 = l9 >> 2;
			int k14 = l9 & 3;
			int j16 = anIntArray1177[j12];
			int j17 = stream.method435();
			if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103)
			{
				int j18 = intGroundArray[plane][j4][i7];
				int i19 = intGroundArray[plane][j4 + 1][i7];
				int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
				int k20 = intGroundArray[plane][j4][i7 + 1];
				if (j16 == 0)
				{
					Object1 class10 = worldController.method296(plane, j4, i7);
					if (class10 != null)
					{
						int k21 = class10.uid >> 14 & 0x7fff;
						if (j12 == 2)
						{
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, 4 + k14, 2, i19, l19, j18, k20, j17,
								false);
							class10.aClass30_Sub2_Sub4_279 = new Animable_Sub5(k21, k14 + 1 & 3, 2, i19, l19, j18, k20,
								j17, false);
						}
						else
						{
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, k14, j12, i19, l19, j18, k20, j17,
								false);
						}
					}
				}
				if (j16 == 1)
				{
					Object2 class26 = worldController.method297(j4, i7, plane);
					if (class26 != null)
						class26.aClass30_Sub2_Sub4_504 = new Animable_Sub5(class26.uid >> 14 & 0x7fff, 0, 4, i19, l19,
							j18, k20, j17, false);
				}
				if (j16 == 2)
				{
					Object5 class28 = worldController.method298(j4, i7, plane);
					if (j12 == 11)
						j12 = 10;
					if (class28 != null)
						class28.aClass30_Sub2_Sub4_521 = new Animable_Sub5(class28.uid >> 14 & 0x7fff, k14, j12, i19,
							l19, j18, k20, j17, false);
				}
				if (j16 == 3)
				{
					Object3 class49 = worldController.method299(i7, j4, plane);
					if (class49 != null)
						class49.aClass30_Sub2_Sub4_814 = new Animable_Sub5(class49.uid >> 14 & 0x7fff, k14, 22, i19,
							l19, j18, k20, j17, false);
				}
			}
			return;
		}
		if (j == 147)
		{
			int l1 = stream.method428();
			int k4 = anInt1268 + (l1 >> 4 & 7);
			int j7 = anInt1269 + (l1 & 7);
			int i10 = stream.readUnsignedWord();
			byte byte0 = stream.method430();
			int l14 = stream.method434();
			byte byte1 = stream.method429();
			int k17 = stream.readUnsignedWord();
			int k18 = stream.method428();
			int j19 = k18 >> 2;
			int i20 = k18 & 3;
			int l20 = anIntArray1177[j19];
			byte byte2 = stream.readSignedByte();
			int l21 = stream.readUnsignedWord();
			byte byte3 = stream.method429();
			Stoner stoner;
			if (i10 == unknownInt10)
				stoner = myStoner;
			else
				stoner = stonerArray[i10];
			if (stoner != null)
			{
				ObjectDef class46 = ObjectDef.forID(l21);
				int i22 = intGroundArray[plane][k4][j7];
				int j22 = intGroundArray[plane][k4 + 1][j7];
				int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
				int l22 = intGroundArray[plane][k4][j7 + 1];
				Model model = class46.method578(j19, i20, i22, j22, k22, l22, -1);
				if (model != null)
				{
					getOrCreateSpotAnimation(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
					stoner.spotAnimStartCycle = l14 + loopCycle;
					stoner.spotAnimEndCycle = k17 + loopCycle;
					stoner.aModel_1714 = model;
					int i23 = class46.anInt744;
					int j23 = class46.anInt761;
					if (i20 == 1 || i20 == 3)
					{
						i23 = class46.anInt761;
						j23 = class46.anInt744;
					}
					stoner.spotAnimX = k4 * 128 + i23 * 64;
					stoner.spotAnimDestY = j7 * 128 + j23 * 64;
					stoner.spotAnimZ = getTerrainHeight(plane, stoner.spotAnimDestY, stoner.spotAnimX);
					if (byte2 > byte0)
					{
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1)
					{
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					stoner.anInt1719 = k4 + byte2;
					stoner.anInt1721 = k4 + byte0;
					stoner.anInt1720 = j7 + byte3;
					stoner.anInt1722 = j7 + byte1;
				}
			}
		}
		if (j == 151)
		{
			int i2 = stream.method426();
			int l4 = anInt1268 + (i2 >> 4 & 7);
			int k7 = anInt1269 + (i2 & 7);
			int j10 = stream.method434();
			int k12 = stream.method428();
			int i15 = k12 >> 2;
			int k16 = k12 & 3;
			int l17 = anIntArray1177[i15];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				getOrCreateSpotAnimation(-1, j10, k16, l17, k7, i15, plane, l4, 0);
			return;
		}
		if (j == 4)
		{
			int j2 = stream.readUnsignedByte();
			int i5 = anInt1268 + (j2 >> 4 & 7);
			int l7 = anInt1269 + (j2 & 7);
			int k10 = stream.readUnsignedWord();
			int l12 = stream.readUnsignedByte();
			int j15 = stream.readUnsignedWord();
			if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104)
			{
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				Animable_Sub3 class30_sub2_sub4_sub3 = new Animable_Sub3(plane, loopCycle, j15, k10,
					getTerrainHeight(plane, l7, i5) - l12, l7, i5);
				aClass19_1056.insertHead(class30_sub2_sub4_sub3);
			}
			return;
		}
		if (j == 44)
		{
			int k2 = stream.method436();
			int j5 = stream.readUnsignedWord();
			int i8 = stream.readUnsignedByte();
			int l10 = anInt1268 + (i8 >> 4 & 7);
			int i13 = anInt1269 + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104)
			{
				Item class30_sub2_sub4_sub2_1 = new Item();
				class30_sub2_sub4_sub2_1.ID = k2;
				class30_sub2_sub4_sub2_1.anInt1559 = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new NodeList();
				groundArray[plane][l10][i13].insertHead(class30_sub2_sub4_sub2_1);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101)
		{
			int l2 = stream.method427();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			int i11 = anIntArray1177[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = anInt1268 + (j13 >> 4 & 7);
			int l16 = anInt1269 + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				getOrCreateSpotAnimation(-1, -1, j8, i11, l16, k5, plane, k15, 0);
			return;
		}
		if (j == 117)
		{
			int i3 = stream.readUnsignedByte();
			int l5 = anInt1268 + (i3 >> 4 & 7);
			int k8 = anInt1269 + (i3 & 7);
			int j11 = l5 + stream.readSignedByte();
			int k13 = k8 + stream.readSignedByte();
			int l15 = stream.readSignedWord();
			int i17 = stream.readUnsignedWord();
			int i18 = stream.readUnsignedByte() * 4;
			int l18 = stream.readUnsignedByte() * 4;
			int k19 = stream.readUnsignedWord();
			int j20 = stream.readUnsignedWord();
			int i21 = stream.readUnsignedByte();
			int j21 = stream.readUnsignedByte();
			if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104
				&& i17 != 65535)
			{
				l5 = l5 * 128 + 64;
				k8 = k8 * 128 + 64;
				j11 = j11 * 128 + 64;
				k13 = k13 * 128 + 64;
				Animable_Sub4 class30_sub2_sub4_sub4 = new Animable_Sub4(i21, l18, k19 + loopCycle, j20 + loopCycle,
					j21, plane, getTerrainHeight(plane, k8, l5) - i18, k8, l5, l15, i17);
				class30_sub2_sub4_sub4.method455(k19 + loopCycle, k13, getTerrainHeight(plane, k13, j11) - l18, j11);
				aClass19_1013.insertHead(class30_sub2_sub4_sub4);
			}
		}
	}

	public static void parseServerUpdate(Stream stream)
	{
		stream.initBitAccess();
		int k = stream.readBits(8);
		if (k < npcCount)
		{
			for (int l = k; l < npcCount; l++)
				anIntArray840[anInt839++] = npcIndices[l];

		}
		if (k > npcCount)
		{
			Signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int i1 = 0; i1 < k; i1++)
		{
			int j1 = npcIndices[i1];
			Npc npc = npcArray[j1];
			int k1 = stream.readBits(1);
			if (k1 == 0)
			{
				npcIndices[npcCount++] = j1;
				npc.anInt1537 = loopCycle;
			}
			else
			{
				int l1 = stream.readBits(2);
				if (l1 == 0)
				{
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = j1;
				}
				else if (l1 == 1)
				{
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						anIntArray894[anInt893++] = j1;
				}
				else if (l1 == 2)
				{
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					int i3 = stream.readBits(1);
					if (i3 == 1)
						anIntArray894[anInt893++] = j1;
				}
				else if (l1 == 3)
					anIntArray840[anInt839++] = j1;
			}
		}
	}

	public static void renderGameFrame(Graphics2D g)
	{
		anInt1265++;
		parseStoners(true);
		renderNPCs(true);
		parseStoners(false);
		renderNPCs(false);
		clearExpiredProjectiles();
		processAnimableGraphics();
		if (!aBoolean1160)
		{
			int i = anInt1184;
			if (anInt984 / 256 > i)
			{
				i = anInt984 / 256;
			}
			if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
			{
				i = anIntArray1203[4] + 128;
			}
			int calc = minimapInt1 + anInt896 & 0x7ff;
// Calculate pure camera distance (not view area)
			int cameraDistance = cameraZoom + i;

// Apply screen size compensation without affecting view area
			if (frameWidth >= 1024) {
				cameraDistance += (cameraZoom - frameHeight / 200);
			}

			setCameraPos(
				cameraDistance,  // Just camera distance - no view multiplier
				i, anInt1014, getTerrainHeight(plane, myStoner.y, myStoner.x) - 50, calc, anInt1015);
		}
		int j;
		if (!aBoolean1160)
			j = getRoofPlane();
		else
			j = selectRoofPlane();
		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;
		int k2 = Rasterizer.textureAccessCounter;
		for (int i2 = 0; i2 < 5; i2++)
			if (aBooleanArray876[i2])
			{
				int j2 = (int) ((Math.random() * (double) (anIntArray873[i2] * 2 + 1) - (double) anIntArray873[i2])
					+ Math.sin((double) anIntArray1030[i2] * ((double) anIntArray928[i2] / 100D))
					* (double) anIntArray1203[i2]);
				if (i2 == 0)
					xCameraPos += j2;
				if (i2 == 1)
					zCameraPos += j2;
				if (i2 == 2)
					yCameraPos += j2;
				if (i2 == 3)
					xCameraCurve = xCameraCurve + j2 & 0x7ff;
				if (i2 == 4)
				{
					yCameraCurve += j2;
					if (yCameraCurve < 128)
						yCameraCurve = 128;
					if (yCameraCurve > 383)
						yCameraCurve = 383;
				}
			}
		Model.aBoolean1684 = true;
		Model.anInt1687 = 0;
		Model.anInt1685 = MouseState.x;
		Model.anInt1686 = MouseState.y;
		DrawingArea.setAllPixelsToZero();
		if (SettingsConfig.enableDistanceFog)
		{
			DrawingArea.drawPixels(frameHeight, 0, 0, ColorUtility.fadingToColor, frameWidth);
		}
		GameState.safeRenderWorld(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
		worldController.clearObj5Cache();
		if (SettingsConfig.enableDistanceFog) {
			fogHandler.renderFog(mainGameRendering.canvasRaster, mainGameRendering.depthBuffer);
		}

		if (inMaze(baseX + (myStoner.x - 6 >> 7)) && filterGrayScale)
		{
			DrawingArea.filterGrayscale(0, 0, frameWidth, frameHeight, 1);
		}
		updateEntities();
		drawHeadIcon();
		updateMovingTextures(k2);

		if (SettingsConfig.showKillFeed)
		{
			displayKillFeed();
		}

		drawGameUIorbs();
		drawTabArea();

		draw3dScreen();
		if (console.openConsole)
		{
			console.drawConsole(frameWidth, 334);
		}
		mainGameRendering.drawGraphics(0, g,
			0);


		xCameraPos = l;
		zCameraPos = i1;
		yCameraPos = j1;
		yCameraCurve = k1;
		xCameraCurve = l1;


	}

	public static void resetAllImageProducers()
	{
		if (gameWorldScreen != null)
		{
			return;
		}
		aRSImageProducer_1166 = null;
		aRSImageProducer_1164 = null;
		aRSImageProducer_1163 = null;
		mainGameRendering = null;
		aRSImageProducer_1125 = null;
		aRSImageProducer_1107 = null;
		aRSImageProducer_1108 = null;
		aRSImageProducer_1109 = null;
		aRSImageProducer_1110 = null;
		aRSImageProducer_1111 = null;
		aRSImageProducer_1112 = null;
		aRSImageProducer_1113 = null;
		aRSImageProducer_1114 = null;
		aRSImageProducer_1115 = null;
		gameWorldScreen = new ImageProducer(canvas.getWidth(), canvas.getHeight());
		welcomeScreenRaised = true;
	}

	public void refreshFrameSize(GameCanvas canvas, int lockedW, int lockedH) throws InterruptedException
	{
		if (lockedW <= 0 || lockedH <= 0) return;

		System.out.println("[Client] === STARTING RESIZE: " + lockedW + "x" + lockedH + " ===");

		boolean wasGPUEnabled = false;
		GPUContextManager.ContextToken contextToken = null;

		try {
			// ===== STEP 1: SMART GPU STATE CHECK =====
			wasGPUEnabled = GPURenderingEngine.isEnabled() && gpuInitialized;

			if (wasGPUEnabled) {
				System.out.println("[Client] GPU active - performing safe shutdown for resize");

				try {
					// Acquire context for cleanup
					if (gpuContextManager != null) {
						contextToken = gpuContextManager.acquireContext("Resize Cleanup", 2000);
						if (contextToken != null && contextToken.isValid()) {
							// Clean up GPU resources
							GPURenderingEngine.cleanup();
							System.out.println("[Client] ✅ GPU cleaned up for resize");
						}
					}
				} catch (Exception e) {
					System.err.println("[Client] ⚠️ Error during GPU cleanup: " + e.getMessage());
					wasGPUEnabled = false; // Don't try to restore if cleanup failed
				} finally {
					if (contextToken != null) {
						try {
							contextToken.close();
							contextToken = null;
						} catch (Exception e) {
							System.err.println("[Client] Error releasing cleanup context: " + e.getMessage());
						}
					}
				}
			}

			// ===== STEP 2: CPU-ONLY RESIZE =====
			System.out.println("[Client] Performing resize to " + lockedW + "x" + lockedH);

			// Create new buffers
			int[] newPixels = new int[lockedW * lockedH];
			float[] newDepthBuffer = new float[lockedW * lockedH];
			java.util.Arrays.fill(newPixels, 0);
			java.util.Arrays.fill(newDepthBuffer, Float.MAX_VALUE);

			// Update DrawingArea with new dimensions - this will handle GPU/CPU mode internally
			DrawingArea.initDrawingArea(lockedH, lockedW, newPixels, newDepthBuffer);

			// Update global size variables
			frameWidth = lockedW;
			frameHeight = lockedH;
			screenAreaWidth = lockedW;
			screenAreaHeight = lockedH;

			// Update other systems that depend on screen size
			setBounds();

			// Create new ImageProducers with correct dimensions
			Client.aRSImageProducer_1109 = new ImageProducer(lockedW, lockedH);
			DrawingArea.setCurrentImageProducer(Client.aRSImageProducer_1109);

			// Update dock system
			DockSync.refreshHeight(lockedH);

			System.out.println("[Client] ✅ Core resize complete");

			// ===== STEP 3: GPU RESTORATION =====
			if (wasGPUEnabled) {
				System.out.println("[Client] Restoring GPU with new dimensions...");

				// Small delay to ensure buffers are stable
				Thread.sleep(100);

				try {
					// Re-acquire context for restoration
					contextToken = gpuContextManager.acquireContext("Resize Restore", 3000);

					if (contextToken != null && contextToken.isValid()) {
						// Re-initialize GPU with new dimensions
						GPURenderingEngine.initialize();

						if (GPURenderingEngine.isEnabled()) {
							// Update GPU shaders with new screen size
							GPUShaders.setScreenSize(lockedW, lockedH);

							// Update global GPU state
							gpuInitialized = true;

							System.out.println("[Client] ✅ GPU restored successfully with new dimensions");
						} else {
							System.err.println("[Client] ⚠️ GPU failed to restore, continuing with CPU rendering");
							gpuInitialized = false;
						}
					} else {
						System.err.println("[Client] ⚠️ Failed to acquire GPU context for restore");
						gpuInitialized = false;
					}

				} catch (Exception e) {
					System.err.println("[Client] ❌ GPU restoration failed: " + e.getMessage());
					e.printStackTrace();
					gpuInitialized = false;

					System.err.println("[Client] 🔧 Continuing with CPU rendering after GPU failure");

				} finally {
					if (contextToken != null) {
						try {
							contextToken.close();
						} catch (Exception e) {
							System.err.println("[Client] Error releasing restore context: " + e.getMessage());
						}
					}
				}
			}

			System.out.println("[Client] === RESIZE COMPLETE: " + lockedW + "x" + lockedH + " ===");

		} catch (Exception e) {
			System.err.println("[Client] ❌ Resize failed: " + e.getMessage());
			e.printStackTrace();

			// Emergency fallback - ensure we're in a working state
			try {
				if (contextToken != null) {
					contextToken.close();
				}

				// Disable GPU as emergency fallback
				gpuInitialized = false;

				System.err.println("[Client] 🔧 Emergency fallback - GPU disabled");

			} catch (Exception fallbackError) {
				System.err.println("[Client] ❌ Emergency fallback failed: " + fallbackError.getMessage());
				throw fallbackError;
			}

			throw e;
		}
	}

	// Helper method to validate resize dimensions
	private void validateResizeParameters(int width, int height) {
		if (width < 256 || height < 256) {
			throw new IllegalArgumentException("Minimum resize dimensions are 256x256");
		}
		if (width > 4096 || height > 4096) {
			throw new IllegalArgumentException("Maximum resize dimensions are 4096x4096");
		}
	}

	// Helper method to safely check GPU state during resize
	private boolean isGPUSafeForResize() {
		try {
			return GPURenderingEngine.isEnabled() &&
				gpuContextManager != null &&
				gpuInitialized;
		} catch (Exception e) {
			System.err.println("[Client] Error checking GPU state: " + e.getMessage());
			return false;
		}
	}

	private void saveMidi(boolean flag, byte[] abyte0)
	{
		Signlink.midifade = flag ? 1 : 0;
		Signlink.midisave(abyte0, abyte0.length);
	}


	private void processOnDemandQueue()
	{
		do
		{
			OnDemandData onDemandData;
			do
			{
				onDemandData = onDemandFetcher.getNextNode();
				if (onDemandData == null)
					return;
				if (onDemandData.dataType == 0)
				{
					Model.method460(onDemandData.buffer, onDemandData.ID);
					if (backDialogID != -1)
						inputTaken = true;
				}
				if (onDemandData.dataType == 1)
				{
					SequenceFrame.load(onDemandData.ID, onDemandData.buffer);
				}
				if (onDemandData.dataType == 2 && onDemandData.ID == nextSong && onDemandData.buffer != null)
					saveMidi(songChanging, onDemandData.buffer);

				if (onDemandData.dataType == 4)
				{
					Texture.decode(onDemandData.ID, onDemandData.buffer);
				}

				if (onDemandData.dataType == 3 && loadingStage == 1)
				{
					for (int i = 0; i < aByteArrayArray1183.length; i++)
					{
						if (anIntArray1235[i] == onDemandData.ID)
						{
							aByteArrayArray1183[i] = onDemandData.buffer;
							if (onDemandData.buffer == null)
								anIntArray1235[i] = -1;
							break;
						}
						if (anIntArray1236[i] != onDemandData.ID)
							continue;
						aByteArrayArray1247[i] = onDemandData.buffer;
						if (onDemandData.buffer == null)
							anIntArray1236[i] = -1;
						break;
					}

				}
			} while (onDemandData.dataType != 93 || !onDemandFetcher.method564(onDemandData.ID));
			ObjectManager.method173(new Stream(onDemandData.buffer), onDemandFetcher);
		} while (true);
	}

	private void mainGameProcessor(Graphics2D g, GameCanvas canvas) throws IOException
	{
		boolean leftClick = MouseState.leftClicked;
		boolean rightClick = MouseState.rightClicked;

		handleDecrements();
		handlePackets(g);

		if (!loggedIn) return;

		validateGPUStateIfNeeded();

		handleClickPacket(leftClick, rightClick);       // 🔄 Needs update to accept args
		handleMovementKeys();
		handleFocusPacket();
		runCorePhases(g, canvas);
		handleCrossCursor();
		handleBoxDialogue();
		handleDragAndDrop();
		handleWalkToObject();
		handleInputClearOnClick(leftClick, rightClick); // 🔄 Also needs update
		processMenuClick(leftClick, rightClick);        // 🔄 Also needs update
		handleInputTick(leftClick, rightClick);

		if (EngineConfig.ENABLE_GPU){
			if (!initialized) {
				initializeGPUAfterGraphicsLoad();
			}
		}

		runSceneRendering(g);
		if (GPURenderingEngine.isEnabled()) {
			GPUShaders.flushAllBatches();
		}

		handleIdle();
		tryFlushStream(g, canvas);

		// ⬇️ Reset click flags at end of frame
		MouseState.leftClicked = false;
		MouseState.rightClicked = false;
	}

	private void handleDecrements() {
		if (anInt1104 > 1) anInt1104--;
		if (anInt1011 > 0) anInt1011--;
		if (anInt1016 > 0) anInt1016--;
	}

	private void handleMovementKeys() {
		if (keyArray[1] == 1 || keyArray[2] == 1 || keyArray[3] == 1 || keyArray[4] == 1) {
			aBoolean1017 = true;
		}
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.createFrame(86);
			stream.writeWord(anInt1184);
			stream.method432(minimapInt1);
		}
	}

	private void handleFocusPacket() {
		if (awtFocus && !aBoolean954) {
			aBoolean954 = true;
			stream.createFrame(3);
			stream.writeWordBigEndian(1);
		}
		if (!awtFocus && aBoolean954) {
			aBoolean954 = false;
			stream.createFrame(3);
			stream.writeWordBigEndian(0);
		}
	}

	private void runCorePhases(Graphics2D g, GameCanvas canvas) {
		Login.loginToGameworld(g);
		updateSpotAnimationTimers();
		anInt1009++;
		if (anInt1009 > 750) dropClient(g, canvas);
		updateAllStoners();
		updateAllNpcs();
		updateEntityTextAndCamera();
		anInt945++;
	}

	private void handleCrossCursor() {
		if (crossType == 0) return;
		crossIndex += 20;
		if (crossIndex >= 400) crossType = 0;
	}

	private void handleInputClearOnClick(boolean leftClick, boolean rightClick) {
		if ((leftClick || rightClick) && aString844 != null) {
			aString844 = null;
			inputTaken = true;
		}
	}

	private void handleInputTick(boolean leftClick, boolean rightClick) {
		if (leftClick || rightClick)
			anInt1213++;

		if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if (anInt1501 < 0 && !menuOpen) {
				anInt1501++;
				if (anInt1501 == 0 && anInt1500 != 0) {
					inputTaken = true;
				}
			}
		} else if (anInt1501 > 0) {
			anInt1501--;
		}
	}

	private void handleIdle() {
		idleTime++;
		if (idleTime > 4500) {
			anInt1011 = 250;
			idleTime -= 500;
			stream.createFrame(202);
		}
		anInt1010++;
		if (anInt1010 > 50) {
			stream.createFrame(0);
		}
	}

	private void tryFlushStream(Graphics2D g, GameCanvas canvas) {
		try {
			if (socketStream != null && stream.currentOffset > 0) {
				socketStream.queueBytes(stream.currentOffset, stream.buffer);
				stream.currentOffset = 0;
				anInt1010 = 0;
			}
		} catch (IOException ex) {
			dropClient(g, canvas);
		} catch (Exception e) {
			resetLogout();
		}
	}

	public void dropClient(Graphics2D g, GameCanvas canvas)
	{
		if (anInt1011 > 0)
		{
			resetLogout();
			return;
		}
		DrawingArea.fillPixels(2, 229, 39, ColorConfig.WHITE_COLOR, 2);
		DrawingArea.drawPixels(37, 3, 3, 0, 227);
		regularText.drawText(0, "BestBudz is out of weed!", 19, 120);
		regularText.drawText(ColorConfig.WHITE_COLOR, "BestBudz is out of weed!", 18, 119);
		regularText.drawText(0, "Attempting to add fertilizer.", 34, 117);
		regularText.drawText(ColorConfig.WHITE_COLOR, "Attempting to add fertilizer.", 34, 116);
		mainGameRendering.drawGraphics(0, g, 0);
		anInt1021 = 0;
		destX = 0;
		if (rememberMe)
		{
			AccountManager.saveAccount();
		}
		RSSocket rsSocket = socketStream;
		loggedIn = false;
		loginComplete = false;
		loginInProgress = false;

		loginFailures = 0;
		setBounds();
		Login.login(myUsername, myPassword, true,g, canvas, this);

		SettingHandler.save();
		console.openConsole = false;
		if (!loggedIn)
			resetLogout();
		try
		{
			rsSocket.close();
		}
		catch (Exception _ex)
		{
		}
	}
	public void processGameLoop(Graphics2D g, GameCanvas canvas) throws IOException
	{
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
			return;

		loopCycle++;
		if (!loggedIn)
			loginRenderer.processLoginScreen(g, canvas);
		else

			mainGameProcessor(g, canvas);
		processOnDemandQueue();
	}

	public void cleanUpForQuit()
	{
		Signlink.reporterror = false;
		try
		{
			if (socketStream != null)
			{
				socketStream.close();
			}
		}
		catch (Exception _ex)
		{
		}
		socketStream = null;
		stopMidi();
		onDemandFetcher.disable();
		onDemandFetcher = null;
		aStream_834 = null;
		stream = null;
		aStream_847 = null;
		inStream = null;
		anIntArray1234 = null;
		aByteArrayArray1183 = null;
		aByteArrayArray1247 = null;
		anIntArray1235 = null;
		anIntArray1236 = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		aClass11Array1230 = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		bigX = null;
		bigY = null;
		aByteArray912 = null;
		aRSImageProducer_1163 = null;
		leftFrame = null;
		topFrame = null;
		aRSImageProducer_1164 = null;
		mainGameRendering = null;
		aRSImageProducer_1166 = null;
		aRSImageProducer_1125 = null;
		cacheSprite = null;
		mapBack = null;
		sideIcons = null;
		StatusOrbs.compass = null;
		hitMarks = null;
		skillIcons = null;
		newHitMarks = null;
		channelButtons = null;
		fixedGameComponents = null;
		skillIcons = null;
		gameComponents = null;
		orbComponents = null;
		orbComponents2 = null;
		orbComponents3 = null;
		redStones = null;
		hpBars = null;
		headIcons = null;
		skullIcons = null;
		headIconsHint = null;
		crosses = null;
		mapDotItem = null;
		mapDotNPC = null;
		mapDotStoner = null;
		mapDotStoner = null;
		mapDotTeam = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		stonerArray = null;
		stonerIndices = null;
		anIntArray894 = null;
		aStreamArray895s = null;
		anIntArray840 = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		aClass19_1179 = null;
		aClass19_1013 = null;
		aClass19_1056 = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		anIntArray1072 = null;
		anIntArray1073 = null;
		aClass30_Sub2_Sub1_Sub1Array1140 = null;
		minimapImage = null;
		stonersList = null;
		stonersListAsLongs = null;
		stonersNodeIDs = null;
		aRSImageProducer_1110 = null;
		aRSImageProducer_1111 = null;
		aRSImageProducer_1107 = null;
		aRSImageProducer_1108 = null;
		aRSImageProducer_1109 = null;
		aRSImageProducer_1112 = null;
		aRSImageProducer_1113 = null;
		aRSImageProducer_1114 = null;
		aRSImageProducer_1115 = null;
		multiOverlay = null;
		nullLoader();
		ObjectDef.nullLoader();
		EntityDef.nullLoader();
		ItemDef.nullLoader();
		Floor.cache = null;
		OverlayFloor.overlayFloor = null;
		IdentityKit.cache = null;
		RSInterface.interfaceCache = null;
		Animation.anims = null;
		SpotAnim.cache = null;
		//SpotAnim.aMRUNodes_415 = null;
		Varp.cache = null;
		Stoner.mruNodes = null;
		Rasterizer.nullLoader();
		WorldController.nullLoader();
		Model.nullLoader();
		SequenceFrame.nullLoader();
		Texture.reset();
		System.gc();
	}

	public void processDrawing(Graphics2D g, GameCanvas canvas) {
		// Quick validation before rendering
		int expected = screenAreaWidth * screenAreaHeight;
		if (DrawingArea.pixels == null || DrawingArea.pixels.length != expected) {
			System.err.println("❌ Buffer mismatch detected - expected: " + expected +
				", actual: " + (DrawingArea.pixels != null ? DrawingArea.pixels.length : "null"));

			// Simple fix: recreate buffers with correct size
			DrawingArea.pixels = new int[expected];
			DrawingArea.depthBuffer = new float[expected];
			DrawingArea.width = screenAreaWidth;
			DrawingArea.height = screenAreaHeight;
			java.util.Arrays.fill(DrawingArea.pixels, 0);
			java.util.Arrays.fill(DrawingArea.depthBuffer, Float.MAX_VALUE);

			System.err.println("🔧 Emergency buffer fix applied");
		}

		// Rest of method unchanged...
		if (rsAlreadyLoaded || loadingError || genericLoadingError) {
			LoadingErrorScreen.showErrorScreen(g, canvas); // Keep original error screen
			return;
		}
		if (!loggedIn)
			loginRenderer.displayLoginScreen(g, canvas);
		else
			drawGameScreen(g);
		anInt1213 = 0;
		frameCount++;
		long now = System.currentTimeMillis();
		if (now - lastFpsTime >= 1000) {
			fps = frameCount;
			frameCount = 0;
			lastFpsTime = now;
		}
	}
}