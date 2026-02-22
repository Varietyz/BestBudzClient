package com.bestbudz.engine.core;

import static com.bestbudz.engine.ClientLauncher.gpuContextManager;
import static com.bestbudz.engine.ClientLauncher.gpuInitialized;
import static com.bestbudz.engine.ClientLauncher.initializeGPUAfterGraphicsLoad;
import com.bestbudz.engine.config.RenderSettings;
import static com.bestbudz.engine.core.GameFrame.drawGameScreen;
import static com.bestbudz.engine.core.GameState.runSceneRendering;
import static com.bestbudz.engine.core.GameState.validateGPUStateIfNeeded;
import static com.bestbudz.engine.core.login.logout.Reset.nullLoader;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.GPUToggleHandler;
import static com.bestbudz.engine.gpu.GPURenderingEngine.initialized;
import com.bestbudz.engine.gpu.RS317GPUInterface;
import com.bestbudz.graphics.text.TextController;
import com.bestbudz.network.CacheManager;
import static com.bestbudz.ui.handling.input.Keyboard.console;
import static com.bestbudz.ui.handling.input.Keyboard.keyArray;
import static com.bestbudz.ui.handling.input.MouseActions.handleClickPacket;
import static com.bestbudz.ui.handling.input.MouseActions.handleDragAndDrop;
import static com.bestbudz.ui.handling.RightClickMenu.processMenuClick;
import static com.bestbudz.engine.core.login.Login.updateEntityTextAndCamera;
import static com.bestbudz.engine.core.login.logout.Logout.resetLogout;
import static com.bestbudz.network.packets.PacketParser.handlePackets;
import static com.bestbudz.entity.EntityMovement.updateAllNpcs;
import static com.bestbudz.entity.EntityMovement.updateAllStoners;
import static com.bestbudz.rendering.AnimationManager.updateSpotAnimations;
import static com.bestbudz.ui.DialogHandling.handleBoxDialogue;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents2;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents3;
import com.bestbudz.world.CollisionMap;
import static com.bestbudz.world.WalkTo.handleWalkToObject;

import com.bestbudz.cache.Signlink;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.data.Skills;
import com.bestbudz.dock.util.DockSync;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.config.NetworkConfig;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.engine.core.login.Login;
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
import com.bestbudz.network.Socket;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.OverlayFloor;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.SpotAnim;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.ui.interfaces.StatusOrbs;
import com.bestbudz.util.ISAACRandomGen;
import com.bestbudz.rendering.Projectile;
import com.bestbudz.rendering.GraphicEffect;
import com.bestbudz.world.SpotAnimationNode;
import java.util.LinkedList;
import com.bestbudz.world.Floor;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import com.bestbudz.world.Varp;
import com.bestbudz.engine.core.gamerender.WorldController;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Client extends ClientEngine
{
	public static final int[][] validInterfaceRegions = {
		{6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131},
		{8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
		{25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
		{4626, 11146, 6439, 12, 4758, 10270, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131},
		{4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131}};
	public static final int[] tabInterfaceIDs = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1};
	public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public static final int[] chatSystemColors = {9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486};
	public static final int MENU_Y_SHIFT = 15;
	public static final RSInterface defaultInterface = new RSInterface();
	public static final int currencies = 11;
	public static final Sprite[] currencyImage = new Sprite[currencies];
	public static final int[] tabAmounts = new int[]{420, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final int[] bankInvTemp = new int[420];
	public static final int[] bankStackTemp = new int[420];
	public static final int[] sideIconsTab = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	public static final long[] currentExp = new long[Skills.SKILLS_COUNT];
	public static final int[] cameraShakeAmplitude = new int[5];
	public static final boolean[] cameraShakeEnabled = new boolean[5];
	public static final int maxStoners = 2048;
	public static final int myStonerIndex = 2047;
	public static final int maxNpcs = 16384;
	public static final int MAX_CHAT_MESSAGES = 500;
	public static final int MAX_FRIENDS = 200;
	public static final int MAX_IGNORES = 100;
	public static final int MAX_SOUND_QUEUE = 50;
	public static final int[] currentStats = new int[Skills.SKILLS_COUNT];
	public static final long[] ignoreListAsLongs = new long[MAX_IGNORES];
	public static final int[] cameraShakeSpeed = new int[5];
	public static String[] chatMessages = new String[MAX_CHAT_MESSAGES];
	public static final String[] clanList = new String[100];
	public static final int[] configuredChatColors = {
		ColorConfig.CHAT_COLOR,
		ColorConfig.CHAT_SOFT_PINK,
		ColorConfig.CHAT_BABY_BLUE,
		ColorConfig.CHAT_MINT_AQUA,
		ColorConfig.CHAT_LIGHT_MAGENTA,
		ColorConfig.WHITE_COLOR
	};
	public static final int[] skillExperienceTable = new int[33];
	public static final int maxChatMessages = 50;
	public static final int[] chatMessageX = new int[maxChatMessages];
	public static final int[] chatMessageY = new int[maxChatMessages];
	public static final int[] chatMessageHeight = new int[maxChatMessages];
	public static final int[] chatMessageWidth = new int[maxChatMessages];
	public static final int[] chatMessageTypes = new int[maxChatMessages];
	public static final int[] chatMessageEffects = new int[maxChatMessages];
	public static final int[] chatMessageCycles = new int[maxChatMessages];
	public static final String[] chatMessageTexts = new String[maxChatMessages];
	public static final Sprite[] hitMark = new Sprite[20];
	public static final Sprite[] hitIcon = new Sprite[20];
	public static final int[] cameraShakeOffsets = new int[5];
	public static final boolean aBoolean994 = false;
	public static final int[] cameraShakeCounters = new int[5];
	public static final int[] maxStats = new int[Skills.SKILLS_COUNT];
	public static int[] experienceDrops;
	public static final int[] professionGrades = new int[152];
	public static final int[] questStates = new int[33];
	public static final int[] musicTrackIds = new int[7];
	public static final int[] expectedCRCs = new int[9];
	public static final String[] atStonerActions = new String[5];
	public static final boolean[] atStonerArray = new boolean[5];
	public static final int[][][] dynamicRegionData = new int[4][13][13];
	public static final boolean genericLoadingError = false;
	public static final int[] cameraShakeMagnitude = new int[5];
	public static final int[] soundIds = new int[MAX_SOUND_QUEUE];
	public static final int[] friendsListIds = new int[MAX_FRIENDS];
	public static final int[] ignoreListIds = new int[MAX_IGNORES];
	public static final int[] soundTypes = new int[MAX_SOUND_QUEUE];
	public static final int[] soundDelays = new int[MAX_SOUND_QUEUE];
	public static final boolean gameAlreadyLoaded = false;
	public static final boolean canGainXP = true;
public static final int[] characterModelIndices = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
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
	public static int loadingProgress;
	public static int spellID = 0;
	public static ImageProducer gameScreenBuffer;
	public static Stoner myStoner;
	public static boolean loggedIn;
	public static int loopCycle;
	public static String myUsername;
	public static String myPassword;
	public static Stream stream;
	public static boolean inputTaken;
	public static int[] bitMasks;

	public static void resizeConfigArrays(int size) {
		if (size > variousSettings.length) {
			variousSettings = new int[size];
		}
		if (experienceDrops == null || size > experienceDrops.length) {
			experienceDrops = new int[size];
		}
	}

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
	public static int gameSubState;
	public static int nodeID = 10;
	public static boolean isMembers = true;
	public static boolean lowMem;
	public static int systemUpdateTimer;
	public static boolean tabAreaAltered;
	public static int interfaceDrawX;
	public static int interfaceDrawY;
	public static int interfaceDrawZ;
	public static ImageProducer mainGameRendering;
	public static int renderDistance;
	public static int[] viewportPixels1;
	public static int[] viewportPixels2;
	public static int[] viewportPixels3;
	public static int viewportIndex;
	public static boolean flagged;
	public static int lastMouseX;
	public static int lastMouseY;

	public static boolean clientData = true;
	public static int channelRights;
	public static int rights;
	public static String message;
	public static int chatTypeView;
	public static int clanChatMode;
	public static int[] skillLevels;
	public static int[] skillExperiences;
	public static int loginScreenState;

	public static int[] configValues;
		public static Background loginBackground1;
	public static Background loginBackground2;
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
	public static CacheManager cacheManager;
	public static int focusedDragWidget;
	public static ImageProducer inventoryImageProducer;
	public static ImageProducer tabImageProducer;
	public static ImageProducer sidebarImageProducer;
	public static ImageProducer mapBackImageProducer;
	public static ImageProducer minimapBackImageProducer;
	public static String loginMessage1;
	public static String loginMessage2;
	public static int drawCount;
	public static int fullscreenInterfaceID;
	public static int anInt1044;
	public static int anInt1129;
	public static int specialHoverInterfaceId;
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
	public static long lastConnectionTime;
	public static int[] stonersNodeIDs;
	@SuppressWarnings("unchecked")
	public static java.util.ArrayDeque<com.bestbudz.data.items.Item>[][][] groundArray;
	public static volatile boolean regionLoaded;
	public static Stream incomingPacketBuffer;
	public static Npc[] npcArray;
	public static int npcCount;
	public static int[] npcIndices;
	public static int regionUpdateCount;
	public static int regionX;
	public static int regionY;
	public static String inputPromptText;
	public static Stream networkBuffer;
	public static boolean soundProduction;
	public static int[] regionUpdateIndices;
	public static int[] regionUpdateX;
	public static int[] regionUpdateY;
	public static int crosshairType;
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
	public static int hoveredInterfaceId;
	public static String inputString;
	public static Stoner[] stonerArray;
	public static int stonerCount;
	public static int[] stonerIndices;
	public static Stream[] playerUpdateBuffers;
	public static int cameraRotation;
	public static int stonersCount;
	public static int friendsListAction;
	public static int crossX;
	public static int crossY;
	public static int crossIndex;
	public static int crossType;
	public static int plane;
	public static boolean loadingError;
	public static Sprite aClass30_Sub2_Sub1_Sub1_931;
	public static Sprite aClass30_Sub2_Sub1_Sub1_932;
	public static int targetPlayerIndex;
	public static int hoveredRegionTileX;
	public static int hoveredRegionTileY;
	public static int selectedRegionTileX;
	public static int selectedRegionTileY;
	public static int regionTileAction;
	public static int gameTickCounter;
	public static WorldController worldController;
	public static Sprite[] sideIcons;
	public static int menuScreenArea;
	public static int menuOffsetX;
	public static int menuOffsetY;
	public static int menuWidth;
	public static int menuHeight;
	public static long menuOpenTime;
	public static boolean windowFocused;
	public static long[] stonersListAsLongs;
	public static int currentSong;
	public static int spriteDrawX;
	public static int spriteDrawY;
	public static boolean menuVisible;
	public static int draggedInterfaceId;
	public static Sprite[] hitMarks;
	public static int interfaceScrollX;
	public static int interfaceScrollY;
	public static int scrollableAreaHeight;
	public static int scrollableAreaWidth;
	public static int scrollPosition;
	public static ISAACRandomGen encryption;
	public static Sprite multiOverlay;
	public static String amountOrNameInput;
	public static int daysSinceLastLogin;
	public static int pktSize;
	public static int pktType;
	public static int idleTimeout;
	public static int connectionTimeout;
	public static LinkedList<Projectile> nodeList;
	public static int mouseClickState;
	public static int lastActionTime;
	public static int mouseDragTime;
	public static Sprite scrollBar1;
	public static Sprite scrollBar2;
	public static int interfaceHoverTime;
	public static boolean dragModeActive;
	public static Sprite[] mapFunctions;
	public static int baseX;
	public static int baseY;
	public static int gameDrawingMode;
	public static int gameDrawingFlags;
	public static int gameRenderState;
	public static int dialogID;
	public static int inputType;
	public static boolean welcomeScreenVisible;
	public static int inputLength;
	public static String inputBuffer;
	public static int selectedTabIndex;
	public static int tabHoverTime;
	public static LinkedList<GraphicEffect> queueSpotAnimation;
	public static Background[] mapScenes;
	public static int soundEffectCount;
	public static int stonersListAction;
	public static int mouseInvInterfaceIndex;
	public static int lastActiveInvInterface;
	public static int inventoryOffsetX;
	public static int inventoryOffsetY;
	public static Sprite mapDotItem;
	public static Sprite mapDotNPC;
	public static Sprite mapDotStoner;
	public static Sprite mapDotTeam;
	public static int minimapState;
	public static boolean friendsListVisible;
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
	public static int cameraOffsetX;
	public static int cameraOffsetY;
	public static int cameraOffsetZ;
	public static int cameraRotationX;
	public static int cameraRotationY;
	public static int systemMessageTimer;
	public static ImageProducer gameImageProducer;
	public static ImageProducer chatImageProducer;
	public static int membersInt;
	public static String aString1121;
	public static ImageProducer loginImageProducer;
	public static int rightClickMenuOption;
	public static int menuActionRow;
	public static int spellSelected;
	public static int spellTargetMask;
	public static int spellUsableOn;
	public static String spellTooltip;
	public static boolean selectedSpell;
	public static int energy;
	public static boolean isPlayerBusy;
	public static Sprite[] crosses;
	public static boolean musicEnabled;
	public static Background[] aBackgroundArray1152s;
	public static int unreadMessages;
	public static boolean canMute;
	public static boolean musicPlaying;
	public static boolean cutsceneActive;
	public static int daysSinceRecovChange;
	public static Socket socketStream;
	public static int friendsListCount;
	public static int minimapInt3;
	public static int reportAbuseInterfaceID;
	public static LinkedList<SpotAnimationNode> spotAnimationQueue;
	public static int minimapRotation;
	public static int invOverlayInterfaceID;
	public static int[] anIntArray1190;
	public static int[] anIntArray1191;
	public static int minimapRegionX;
	public static Background mapBack;
	public static String[] menuActionName;
	public static Sprite aClass30_Sub2_Sub1_Sub1_1201;
	public static Sprite aClass30_Sub2_Sub1_Sub1_1202;
	public static int minimapInt2;
	public static String promptInput;
	public static int inputClickCount;
	public static int[][][] intGroundArray;
	public static long lastInventoryTime;
	public static long lastChatTime;
	public static int targetNpcIndex;
	public static int inputDialogState;
	public static int nextSong;
	public static boolean songChanging;
	public static CollisionMap[] collisionMaps;
	public static int[] mapRegionIds;
	public static boolean messageWaiting;
	public static int atBoxLoopCycle;
	public static int atBoxInterface;
	public static int atBoxIndex;
	public static int atBoxInterfaceType;
	public static int tradeMode;
	public static int chatDisplayMode;
	public static int publicChatFilter;
	public static boolean welcomeScreenRaised;
	public static boolean messagePromptRaised;
	public static byte[][][] byteGroundArray;
	public static int prevSong;
	public static int destX;
	public static int destY;
	public static int lastTickCount;
	public static int currentTick;
	public static int localRegionX;
	public static int localRegionY;
	public static int selectedSpellIndex;
	public static int itemSelected;
	public static int selectedItemSlot;
	public static int selectedItemInterfaceId;
	public static int selectedItemIndex;
	public static String selectedItemName;
	public static int modifiableXValue = 0;
	public static GameCanvas canvas;
	public static boolean filterGrayScale = false;
	public static int packetCounter;
	public static int packetTimer;
	public static int anInt1051;
	public static int anInt1097;
	public static boolean fpsOn = true;
	public static int[][] walkDistance;
	public static int removedNpcCount;
	public static int[] removedNpcIndices;
	public static int updatedNpcCount;
	public static int[] updatedNpcIndices;
	public static int[][] walkDirection;
	public static byte[] aByteArray912;
	public static int anInt913;
	public static int[][] renderGrid;
	public static int stonerHeight;
	public static int cameraX;
	public static int cameraZ;
	public static int minCameraHeight;
	public static int legacyClickInt;
	public static int[] walkQueueX;
	public static int[] walkQueueY;
	public static int[][] walkVisitGen;
	public static int walkGeneration;
	public static int cameraYawVelocity;
	public static int cameraPitchVelocity;
	public static ImageProducer minimapImageProducer;
	public static ImageProducer gameAreaImageProducer;
	public static ImageProducer fullscreenImageProducer;
	public static ImageProducer overlayImageProducer;
	public static final FogHandler fogHandler = new FogHandler();
	private static java.net.Socket aSocket832;
	private static boolean connectionError;
	private static int[] renderQueueX;
	private static int[] renderQueueY;
	private static Sprite[] spriteCache;
	public final int[] anIntArray969;
	private final String[] chatNames;
	public String name;
	public int loginScreenCursorPos;
	private int frameCount = 0;
	private long lastFpsTime = System.currentTimeMillis();
	private int anInt1010;
	private int anInt1016;
	private boolean aBoolean1017;

	static {
		try {
			LOGIN_BACKGROUND = ImageIO.read(Client.class.getResource("/loading/login_background.png"));
			System.out.println("[DEBUG] BG_COLOR: " + LOGIN_BACKGROUND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		bitMasks = new int[32];
		int i = 2;
		for (int k = 0; k < 32; k++) {
			bitMasks[k] = i - 1;
			i += i;
		}
	}

	public Client()
	{
		EngineConfig.worldSelected = 1;
		fullscreenInterfaceID = -1;
		chatTypeView = 0;
		clanChatMode = 0;
		server = NetworkConfig.SERVER_IPS[EngineConfig.worldSelected - 1];
		walkDistance = new int[104][104];
		stonersNodeIDs = new int[200];
		groundArray = new java.util.ArrayDeque[4][104][104];
		regionLoaded = false;
		incomingPacketBuffer = new Stream(new byte[5000]);
		npcArray = new Npc[maxNpcs];
		npcIndices = new int[maxNpcs];
		removedNpcIndices = new int[1000];
		networkBuffer = Stream.getPooledStream();
		soundProduction = true;
		openInterfaceID = -1;
		connectionError = false;
		unknownInt10 = -1;
		menuOpen = false;
		inputString = "";
		stonerArray = new Stoner[maxStoners];
		stonerIndices = new int[maxStoners];
		updatedNpcIndices = new int[maxStoners];
		playerUpdateBuffers = new Stream[maxStoners];
		walkDirection = new int[104][104];
		aByteArray912 = new byte[maxNpcs];
		loadingError = false;
		renderGrid = new int[104][104];
		chatNames = new String[MAX_CHAT_MESSAGES];
		sideIcons = new Sprite[17];
		windowFocused = true;
		stonersListAsLongs = new long[200];
		currentSong = -1;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray969 = new int[256];
		variousSettings = new int[2000];
		experienceDrops = new int[2000];
		menuVisible = false;

		draggedInterfaceId = -1;
		hitMarks = new Sprite[0];
		amountOrNameInput = "";
		nodeList = new LinkedList<>();
		aBoolean1017 = false;
		mouseClickState = -1;
		dragModeActive = false;
		mapFunctions = new Sprite[0];
		dialogID = -1;
		welcomeScreenVisible = true;
		selectedTabIndex = -1;
		queueSpotAnimation = new LinkedList<>();

		mapScenes = new Background[0];
		renderQueueX = new int[1000];
		renderQueueY = new int[1000];
		friendsListVisible = false;
		stonersList = new String[200];
		inStream = Stream.getPooledStream();
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[0];
		skullIcons = new Sprite[0];
		headIconsHint = new Sprite[0];
		tabAreaAltered = false;
		aString1121 = "";
		spriteCache = new Sprite[1000];
		selectedSpell = false;
		isPlayerBusy = false;
		crosses = new Sprite[8];
		musicEnabled = true;
		loggedIn = false;
		canMute = false;
		musicPlaying = false;
		cutsceneActive = false;
		myUsername = "";
		myPassword = "";
		reportAbuseInterfaceID = -1;
		spotAnimationQueue = new LinkedList<>();
		minCameraHeight = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.getPooledStream();
		menuActionName = new String[500];
		anInt1211 = 78;
		promptInput = "";
		tabID = 3;
		inputTaken = false;
		songChanging = true;
		collisionMaps = new CollisionMap[4];
		messageWaiting = false;
		welcomeScreenRaised = false;
		messagePromptRaised = false;
		loginMessage1 = "Best Budz - Where weed is legal.";
		loginMessage2 = "The weed flows, where the tree grows!";
		backDialogID = -1;
		walkQueueX = new int[4000];
		walkQueueY = new int[4000];
		walkVisitGen = new int[104][104];
	}

	public static void setCanvas(GameCanvas canvas) {
	}

	public static void setBounds()
	{
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		fullScreenTextureArray = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		viewportPixels1 = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		viewportPixels2 = Rasterizer.scanlineOffsets;
		Rasterizer.setViewportSize(frameWidth, frameHeight);
		viewportPixels3 = Rasterizer.scanlineOffsets;
		int[] ai = new int[9];
		for (int i8 = 0; i8 < 9; i8++)
		{
			int k8 = 128 + i8 * 32 + 15;
			int l8 = 600 + k8 * 3;
			int i9 = Rasterizer.sinTable[k8];
			ai[i8] = l8 * i9 >> 16;
		}

		WorldController.viewDistance = RenderSettings.WORLD_VIEW_DISTANCE;

		cameraZoom = EngineConfig.CAMERA_ZOOM;

		if (extendChatArea > frameHeight - 170) {
			extendChatArea = frameHeight - 170;
		}

		WorldController.calculateVisibility(500, 800, frameWidth, frameHeight, ai);
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
		WorldController.lowMemoryMode = false;
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

	public static java.net.Socket openSocket(int port) throws IOException
	{
		return new java.net.Socket(InetAddress.getByName(NetworkConfig.LOCALHOST ? "localhost" : server), port);
	}

	public static boolean inBounds(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	public static void renderChatIfInvalidated() {
		if (inputTaken) {
			inputTaken = false;
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

	public void refreshFrameSize(GameCanvas canvas, int lockedW, int lockedH) throws InterruptedException {
		if (lockedW <= 0 || lockedH <= 0) return;

		System.out.println("[Client] === STARTING RESIZE: " + lockedW + "x" + lockedH + " ===");

		boolean wasGPUEnabled = false;
		GPUContextManager.ContextToken contextToken = null;

		try {

			wasGPUEnabled = GPURenderingEngine.isEnabled() && gpuInitialized;

			if (wasGPUEnabled) {
				System.out.println("[Client] GPU active - performing safe resize (NO SHUTDOWN)");

				try {

					contextToken = gpuContextManager.acquireContext("Resize Operation", 5000);
					if (contextToken != null && contextToken.isValid()) {

						GPURenderingEngine.resize(lockedW, lockedH);
						System.out.println("[Client] ✅ GPU framebuffer resized to " + lockedW + "x" + lockedH);
					} else {
						System.err.println("[Client] ⚠️ Failed to acquire context for GPU resize");

						wasGPUEnabled = false;
					}
				} catch (Exception e) {
					System.err.println("[Client] ⚠️ Error during GPU resize: " + e.getMessage());
					wasGPUEnabled = false;
				} finally {
					if (contextToken != null) {
						try {
							contextToken.close();
							contextToken = null;
						} catch (Exception e) {
							System.err.println("[Client] Error releasing resize context: " + e.getMessage());
						}
					}
				}
			}

			System.out.println("[Client] Performing CPU buffer resize to " + lockedW + "x" + lockedH);

			int[] newPixels = new int[lockedW * lockedH];
			float[] newDepthBuffer = new float[lockedW * lockedH];
			java.util.Arrays.fill(newPixels, 0);
			java.util.Arrays.fill(newDepthBuffer, Float.MAX_VALUE);

			DrawingArea.initDrawingArea(lockedH, lockedW, newPixels, newDepthBuffer);

			frameWidth = lockedW;
			frameHeight = lockedH;
			screenAreaWidth = lockedW;
			screenAreaHeight = lockedH;

			setBounds();

			Client.gameScreenBuffer = new ImageProducer(lockedW, lockedH);
			DrawingArea.setCurrentImageProducer(Client.gameScreenBuffer);

			DockSync.refreshHeight(lockedH);

			System.out.println("[Client] ✅ CPU resize complete");

			if (wasGPUEnabled) {

				if (GPURenderingEngine.isEnabled()) {
					System.out.println("[Client] ✅ GPU resize complete - engine still enabled");

					try {
						RS317GPUInterface.setScreenSize(lockedW, lockedH);
					} catch (Exception e) {
						System.err.println("[Client] Error updating RS317 screen size: " + e.getMessage());
					}
				} else {
					System.err.println("[Client] ⚠️ GPU became disabled during resize - this shouldn't happen");
					gpuInitialized = false;
				}
			} else if (GPURenderingEngine.isEnabled()) {

				gpuInitialized = true;
				System.out.println("[Client] ✅ GPU discovered to be enabled after resize");
			}

			System.out.println("[Client] === RESIZE COMPLETE: " + lockedW + "x" + lockedH + " ===");

		} catch (Exception e) {
			System.err.println("[Client] ❌ Resize failed: " + e.getMessage());
			e.printStackTrace();

			try {
				if (contextToken != null) {
					contextToken.close();
				}
			} catch (Exception fallbackError) {
				System.err.println("[Client] Error in emergency cleanup: " + fallbackError.getMessage());
			}

			throw e;
		}
	}

	public void processDrawing(Graphics2D g, GameCanvas canvas) {

		int expected = screenAreaWidth * screenAreaHeight;
		if (DrawingArea.pixels == null || DrawingArea.pixels.length != expected) {
			System.err.println("❌ Buffer mismatch detected - expected: " + expected +
				", actual: " + (DrawingArea.pixels != null ? DrawingArea.pixels.length : "null"));

			DrawingArea.pixels = new int[expected];
			DrawingArea.depthBuffer = new float[expected];
			DrawingArea.width = screenAreaWidth;
			DrawingArea.height = screenAreaHeight;
			java.util.Arrays.fill(DrawingArea.pixels, 0);
			java.util.Arrays.fill(DrawingArea.depthBuffer, Float.MAX_VALUE);

			System.err.println("🔧 Emergency buffer fix applied");
		}

		if (gameAlreadyLoaded || loadingError || genericLoadingError) {
			LoadingErrorScreen.showErrorScreen(g, canvas);
			return;
		}
		if (!loggedIn)
			loginRenderer.displayLoginScreen(g, canvas);
		else
			drawGameScreen(g);
		inputClickCount = 0;
		frameCount++;
		long now = System.currentTimeMillis();
		if (now - lastFpsTime >= 1000) {
			fps = frameCount;
			frameCount = 0;
			lastFpsTime = now;
		}
	}

	public void processGameLoop(Graphics2D g, GameCanvas canvas) throws IOException
	{
		if (gameAlreadyLoaded || loadingError || genericLoadingError)
			return;

		loopCycle++;
		if (!loggedIn)
			loginRenderer.processLoginScreen(g, canvas);
		else

			mainGameProcessor(g, canvas);
	}

	private void mainGameProcessor(Graphics2D g, GameCanvas canvas) throws IOException
	{
		boolean leftClick = MouseState.leftClicked;
		boolean rightClick = MouseState.rightClicked;

		handleDecrements();
		handlePackets(g);

		if (!loggedIn) return;

		validateGPUStateIfNeeded();

		handleClickPacket(leftClick, rightClick);
		handleMovementKeys();
		handleFocusPacket();
		runCorePhases(g, canvas);
		handleCrossCursor();
		handleBoxDialogue();
		handleDragAndDrop();
		handleWalkToObject();
		handleInputClearOnClick(leftClick, rightClick);
		processMenuClick(leftClick, rightClick);
		handleInputTick(leftClick, rightClick);

		GPUToggleHandler.processPendingToggle();

		if (EngineConfig.ENABLE_GPU){
			if (!initialized) {
				System.out.println("[Client] GPU not initialized, calling initializeGPUAfterGraphicsLoad()");
				initializeGPUAfterGraphicsLoad();
			}
		}

		runSceneRendering(g);

		handleIdle();
		tryFlushStream(g, canvas);

		MouseState.leftClicked = false;
		MouseState.rightClicked = false;
	}

	private void handleDecrements() {
		if (systemMessageTimer > 1) systemMessageTimer--;
		if (connectionTimeout > 0) connectionTimeout--;
		if (anInt1016 > 0) anInt1016--;
	}

	private void handleMovementKeys() {
		if (keyArray[1] == 1 || keyArray[2] == 1 || keyArray[3] == 1 || keyArray[4] == 1) {
			aBoolean1017 = true;
		}
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.writeEncryptedOpcode(86);
			stream.writeWord(minCameraHeight);
			stream.writeWordMixed(minimapRotation);
		}
	}

	private void handleFocusPacket() {
		if (awtFocus && !windowFocused) {
			windowFocused = true;
			stream.writeEncryptedOpcode(3);
			stream.writeByte(1);
		}
		if (!awtFocus && windowFocused) {
			windowFocused = false;
			stream.writeEncryptedOpcode(3);
			stream.writeByte(0);
		}
	}

	private void runCorePhases(Graphics2D g, GameCanvas canvas) {
		Login.loginToGameworld(g);
		updateSpotAnimations();
		idleTimeout++;
		if (idleTimeout > 750) dropClient(g, canvas);
		updateAllStoners();
		updateAllNpcs();
		updateEntityTextAndCamera();
		gameTickCounter++;
	}

	private void handleCrossCursor() {
		if (crossType == 0) return;
		crossIndex += 20;
		if (crossIndex >= 400) crossType = 0;
	}

	private void handleInputClearOnClick(boolean leftClick, boolean rightClick) {
		if ((leftClick || rightClick) && inputPromptText != null) {
			inputPromptText = null;
			inputTaken = true;
		}
	}

	private void handleInputTick(boolean leftClick, boolean rightClick) {
		if (leftClick || rightClick)
			inputClickCount++;

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
			connectionTimeout = 250;
			idleTime -= 500;
			stream.writeEncryptedOpcode(202);
		}
		anInt1010++;
		if (anInt1010 > 50) {
			stream.writeEncryptedOpcode(0);
		}
	}

	private void tryFlushStream(Graphics2D g, GameCanvas canvas) {
		try {
			if (socketStream != null && stream.position > 0) {
				socketStream.queueBytes(stream.position, stream.buffer);
				stream.position = 0;
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
		if (connectionTimeout > 0)
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
		lastActionTime = 0;
		destX = 0;
		if (rememberMe)
		{
			AccountManager.saveAccount();
		}
		Socket socket = socketStream;
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
			socket.close();
		}
		catch (Exception _ex)
		{
		}
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
		cacheManager = null;
		incomingPacketBuffer = null;
		stream = null;
		networkBuffer = null;
		inStream = null;
		mapRegionIds = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		collisionMaps = null;
		walkDirection = null;
		walkDistance = null;
		walkQueueX = null;
		walkQueueY = null;
		walkVisitGen = null;
		aByteArray912 = null;
		gameAreaImageProducer = null;
		leftFrame = null;
		topFrame = null;
		overlayImageProducer = null;
		mainGameRendering = null;
		fullscreenImageProducer = null;
		loginImageProducer = null;
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
		renderGrid = null;
		stonerArray = null;
		stonerIndices = null;
		updatedNpcIndices = null;
		playerUpdateBuffers = null;
		removedNpcIndices = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		spotAnimationQueue = null;
		nodeList = null;
		queueSpotAnimation = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		renderQueueX = null;
		renderQueueY = null;
		spriteCache = null;
		minimapImage = null;
		stonersList = null;
		stonersListAsLongs = null;
		stonersNodeIDs = null;
		gameImageProducer = null;
		chatImageProducer = null;
		minimapImageProducer = null;
		inventoryImageProducer = null;
		gameScreenBuffer = null;
		tabImageProducer = null;
		sidebarImageProducer = null;
		mapBackImageProducer = null;
		minimapBackImageProducer = null;
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

		Varp.cache = null;
		Stoner.mruNodes = null;
		Rasterizer.nullLoader();
		WorldController.nullLoader();
		Model.clearCache();
		SequenceFrame.nullLoader();
		Texture.clearCache();
		System.gc();
	}

}
