package com.bestbudz.engine.core.gamerender;

import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.animation.FloorDecoration;
import com.bestbudz.rendering.model.Point3D;
import com.bestbudz.rendering.model.SimpleTile;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.NodeList;
import com.bestbudz.world.Ground;
import com.bestbudz.world.Wall;
import com.bestbudz.world.WallDecoration;
import com.bestbudz.world.GroundDecoration;
import com.bestbudz.world.RoofDecoration;
import com.bestbudz.world.GameObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class WorldController {

	private static final int[] wallOffsetX1 = { 53, -53, -53, 53 };
	private static final int[] wallOffsetZ1 = { -53, -53, 53, 53 };
	private static final int[] wallOffsetX2 = { -45, 45, 45, -45 };
	private static final int[] wallOffsetZ2 = { 45, 45, -45, -45 };
	private static final int planeCount;
	private static final Occluder[] A_OCCLUDER_ARRAY_476 = new Occluder[500];
	private static final int[] directionLookup = { 19, 55, 38, 155, 255, 110, 137, 205, 76 };
	private static final int[] visibilityMask = { 160, 192, 80, 96, 0, 144, 80, 48, 160 };
	private static final int[] visibilityFlags = { 76, 8, 137, 4, 0, 1, 38, 2, 19 };
	private static final int[] wallPattern1 = { 0, 0, 2, 0, 0, 2, 1, 1, 0 };
	private static final int[] wallPattern2 = { 2, 0, 0, 2, 0, 0, 0, 4, 4 };
	private static final int[] wallPattern3 = { 0, 4, 4, 8, 0, 0, 8, 0, 0 };
	private static final int[] wallPattern4 = { 1, 1, 0, 0, 0, 8, 0, 0, 8 };
	private static final int[] textureBrightness = { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086,
			41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41,
			41, 41, 41, 41, 41, 3131, 41, 41, 41 };
	public static boolean lowMemoryMode = true;
	public static int selectedTileX = -1;
	public static int selectedTileY = -1;
	public static int viewDistance = 10;

	private static int visibleTileCount;
	private static int currentPlane;
	private static int renderCycle;
	private static int minTileX;
	private static int maxTileX;
	private static int minTileY;
	private static int maxTileY;
	private static int cameraTileX;
	private static int cameraTileY;
	private static int cameraX;
	private static int cameraY;
	private static int cameraZ;
	private static int sinVertical;
	private static int cosVertical;
	private static int sinHorizontal;
	private static int cosHorizontal;
	private static GameObject[] aClass28Array462 = new GameObject[100];
	private static boolean mouseSelection;
	private static int mouseX;
	private static int mouseY;
	private static int[] anIntArray473;
	private static Occluder[][] aOccluderArrayArray474;
	private static int occluderCount;
	private static NodeList aClass19_477 = new NodeList();
	private static boolean[][][][] aBooleanArrayArrayArrayArray491 = new boolean[8][32][51][51];
	private static boolean[][] aBooleanArrayArray492;
	private static int viewportCenterX;
	private static int viewportCenterY;
	private static int viewportMinX;
	private static int viewportMinY;
	private static int viewportMaxX;
	private static int viewportMaxY;

	static {
		planeCount = 4;
		anIntArray473 = new int[planeCount];
		aOccluderArrayArray474 = new Occluder[planeCount][500];
	}

	private final int levelCount;
	private final int mapWidth;
	private final int mapHeight;
	private final int[][][] heightMap;
	public final Ground[][][] tiles;
	private final GameObject[] gameObjectCache;
	private final int[][][] visibilityCache;
	private final int[] lightingMarker1;
	private final int[] lightingMarker2;
	private boolean lowMemory;
	private int currentLevel;
	private int gameObjectCachePosition;
	private int lightingCycle;
	public WorldController(int[][][] ai) {
		int i = EngineConfig.REGION_RENDER;
		int j = EngineConfig.REGION_RENDER;
		int k = 8;
		lowMemory = true;
		gameObjectCache = new GameObject[5000];
		lightingMarker1 = new int[10000];
		lightingMarker2 = new int[10000];
		levelCount = k;
		mapWidth = j;
		mapHeight = i;
		tiles = new Ground[k][j][i];
		visibilityCache = new int[k][j + 1][i + 1];
		heightMap = ai;
		initToNull();
	}

	public static void nullLoader() {
		aClass28Array462 = null;
		anIntArray473 = null;
		aOccluderArrayArray474 = null;
		aClass19_477 = null;
		aBooleanArrayArrayArrayArray491 = null;
		aBooleanArrayArray492 = null;
	}

	public static void method277(int i, int j, int k, int l, int i1, int j1, int l1, int i2) {
		Occluder occluder = new Occluder();
		occluder.tileX = j / 128;
		occluder.tileX2 = l / 128;
		occluder.tileY = l1 / 128;
		occluder.tileY2 = i1 / 128;
		occluder.occluderType = i2;
		occluder.worldX = j;
		occluder.worldX2 = l;
		occluder.worldZ = l1;
		occluder.worldZ2 = i1;
		occluder.worldY = j1;
		occluder.worldY2 = k;
		aOccluderArrayArray474[i][anIntArray473[i]++] = occluder;
	}

	public static void calculateVisibility(int i, int j, int k, int l, int[] ai) {
		viewportMinX = 0;
		viewportMinY = 0;
		viewportMaxX = k;
		viewportMaxY = l;
		viewportCenterX = k / 2;
		viewportCenterY = l / 2;
		boolean[][][][] aflag = new boolean[9][32][53][53];

		for (int i1 = 128; i1 <= 384; i1 += 32) {
			sinVertical = Model.modelIntArray1[i1];
			cosVertical = Model.modelIntArray2[i1];
			int l1 = (i1 - 128) / 32;

			for (int j1 = 0; j1 < 2048; j1 += 64) {
				sinHorizontal = Model.modelIntArray1[j1];
				cosHorizontal = Model.modelIntArray2[j1];
				int j2 = j1 / 64;

				for (int l2 = -26; l2 <= 26; l2++) {
					int k3 = l2 * 128;

					for (int j3 = -26; j3 <= 26; j3++) {
						int i4 = j3 * 128;
						boolean flag2 = false;

						for (int k4 = -i; k4 <= j; k4 += 128) {
							if (isVisible(ai[l1] + k4, i4, k3)) {
								flag2 = true;
								break;
							}
						}

						aflag[l1][j2][l2 + 26][j3 + 26] = flag2;
					}
				}
			}
		}

		for (int k1 = 0; k1 < 8; k1++) {
			for (int i2 = 0; i2 < 32; i2++) {
				for (int k2 = -25; k2 < 25; k2++) {
					int k2_offset = k2 + 26;

					for (int i3 = -25; i3 < 25; i3++) {
						int i3_offset = i3 + 26;
						boolean flag1 = false;

						// Check 3x3 neighborhood
						for (int l3 = -1; l3 <= 1 && !flag1; l3++) {
							for (int j4 = -1; j4 <= 1; j4++) {
								if (aflag[k1][i2][k2_offset + l3][i3_offset + j4] ||
									aflag[k1][(i2 + 1) % 31][k2_offset + l3][i3_offset + j4] ||
									aflag[k1 + 1][i2][k2_offset + l3][i3_offset + j4] ||
									aflag[k1 + 1][(i2 + 1) % 31][k2_offset + l3][i3_offset + j4]) {
									flag1 = true;
									break;
								}
							}
						}

						aBooleanArrayArrayArrayArray491[k1][i2][k2 + 25][i3 + 25] = flag1;
					}
				}
			}
		}
	}

	private static boolean isVisible(int i, int j, int k) {
		int l = j * sinHorizontal + k * cosHorizontal >> 16;
		int i1 = j * cosHorizontal - k * sinHorizontal >> 16;
		int j1 = i * sinVertical + i1 * cosVertical >> 16;
		int k1 = i * cosVertical - i1 * sinVertical >> 16;

		// CRITICAL: Check if point is behind camera FIRST
		if (j1 < 50 || j1 > 3500)
			return false;

		// FIX: Use FIXED view distance to match rendering
		// This prevents view frustum from flipping during camera rotation
		final int PROJECTION_SHIFT = 10; // Must match the shift used in rendering!

		int l1 = viewportCenterX + (l << PROJECTION_SHIFT) / j1;
		int i2 = viewportCenterY + (k1 << PROJECTION_SHIFT) / j1;

		return l1 >= viewportMinX && l1 <= viewportMaxX && i2 >= viewportMinY && i2 <= viewportMaxY;
	}

	public void initToNull() {
		for (int j = 0; j < levelCount; j++) {
			for (int k = 0; k < mapWidth; k++) {
				for (int i1 = 0; i1 < mapHeight; i1++)
					tiles[j][k][i1] = null;

			}

		}
		for (int l = 0; l < planeCount; l++) {
			for (int j1 = 0; j1 < anIntArray473[l]; j1++)
				aOccluderArrayArray474[l][j1] = null;

			anIntArray473[l] = 0;
		}

		for (int k1 = 0; k1 < gameObjectCachePosition; k1++)
			gameObjectCache[k1] = null;

		gameObjectCachePosition = 0;
		for (int l1 = 0; l1 < aClass28Array462.length; l1++)
			aClass28Array462[l1] = null;

	}

	public void initializeLevel(int i) {
		currentLevel = i;
		for (int k = 0; k < mapWidth; k++) {
			for (int l = 0; l < mapHeight; l++)
				if (tiles[i][k][l] == null)
					tiles[i][k][l] = new Ground(i, k, l);

		}

	}

	public void removeTopLevel(int i, int j) {
		Ground class30_sub3 = tiles[0][j][i];
		for (int l = 0; l < 3; l++) {
			Ground class30_sub3_1 = tiles[l][j][i] = tiles[l + 1][j][i];
			if (class30_sub3_1 != null) {
				class30_sub3_1.anInt1307--;
				for (int j1 = 0; j1 < class30_sub3_1.anInt1317; j1++) {
					GameObject class28 = class30_sub3_1.obj5Array[j1];
					if ((class28.uid >> 29 & 3) == 2 && class28.anInt523 == j && class28.anInt525 == i)
						class28.anInt517--;
				}

			}
		}
		if (tiles[0][j][i] == null)
			tiles[0][j][i] = new Ground(0, j, i);
		tiles[0][j][i].bridgeTile = class30_sub3;
		tiles[3][j][i] = null;
	}

	public void setTileHeight(int i, int j, int k, int l) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 != null) {
			tiles[i][j][k].anInt1321 = l;
		}
	}

	void addFloorTile(int i, int j, int k, int l, int i1, int overlaytex,
					  int k1, int l1, int i2, int j2, int k2, int l2,
					  int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4,
					  boolean tex) {
		if (l == 0) {
			SimpleTile simpleTile = new SimpleTile(k2, l2, i3, j3, 154, k4,
					false, tex);

			for (int i5 = i; i5 >= 0; i5--) {
				if (tiles[i5][j][k] == null) {
					tiles[i5][j][k] = new Ground(i5, j, k);
				}
			}

			tiles[i][j][k].simpleTile = simpleTile;
			return;
		}

		if (l == 1) {
			SimpleTile simpleTile_1 = new SimpleTile(k3, l3, i4, j4, overlaytex, l4,
					k1 == l1 && k1 == i2 && k1 == j2, tex);

			for (int j5 = i; j5 >= 0; j5--) {
				if (tiles[j5][j][k] == null) {
					tiles[j5][j][k] = new Ground(j5, j, k);
				}
			}

			tiles[i][j][k].simpleTile = simpleTile_1;
			return;
		}

		FloorDecoration floorDecoration = new FloorDecoration(k, k3, j3, i2, overlaytex, 154,
				i4, i1, k2, k4, i3, j2, l1, k1, l, j4, l3, l2, j, l4, tex);

		for (int k5 = i; k5 >= 0; k5--) {
			if (tiles[k5][j][k] == null) {
				tiles[k5][j][k] = new Ground(k5, j, k);
			}
		}

		tiles[i][j][k].floorDecoration = floorDecoration;
	}

	public void addGroundDecoration(int i, int j, int k, Animable class30_sub2_sub4, byte byte0, int i1, int j1) {
		if (class30_sub2_sub4 == null)
			return;
		GroundDecoration class49 = new GroundDecoration();
		class49.model = class30_sub2_sub4;
		class49.anInt812 = j1 * 128 + 64;
		class49.anInt813 = k * 128 + 64;
		class49.anInt811 = j;
		class49.uid = i1;
		class49.aByte816 = byte0;
		if (tiles[i][j1][k] == null)
			tiles[i][j1][k] = new Ground(i, j1, k);
		tiles[i][j1][k].obj3 = class49;
	}

	public void addRoofDecoration(int i, int j, Animable class30_sub2_sub4, int k, Animable class30_sub2_sub4_1,
								  Animable class30_sub2_sub4_2, int l, int i1) {
		RoofDecoration roofDecoration = new RoofDecoration();
		roofDecoration.aClass30_Sub2_Sub4_48 = class30_sub2_sub4_2;
		roofDecoration.anInt46 = i * 128 + 64;
		roofDecoration.anInt47 = i1 * 128 + 64;
		roofDecoration.anInt45 = k;
		roofDecoration.uid = j;
		roofDecoration.aClass30_Sub2_Sub4_49 = class30_sub2_sub4;
		roofDecoration.aClass30_Sub2_Sub4_50 = class30_sub2_sub4_1;
		int j1 = 0;
		Ground class30_sub3 = tiles[l][i][i1];
		if (class30_sub3 != null) {
			for (int k1 = 0; k1 < class30_sub3.anInt1317; k1++)
				if (class30_sub3.obj5Array[k1].model instanceof Model) {
					int l1 = ((Model) class30_sub3.obj5Array[k1].model).anInt1654;
					if (l1 > j1)
						j1 = l1;
				}

		}
		roofDecoration.anInt52 = j1;
		if (tiles[l][i][i1] == null)
			tiles[l][i][i1] = new Ground(l, i, i1);
		tiles[l][i][i1].obj4 = roofDecoration;
	}

	public void addWall(int i, Animable class30_sub2_sub4, int j, int k, byte byte0, int l,
						Animable class30_sub2_sub4_1, int i1, int j1, int k1) {
		if (class30_sub2_sub4 == null && class30_sub2_sub4_1 == null)
			return;
		Wall wall = new Wall();
		wall.uid = j;
		wall.aByte281 = byte0;
		wall.anInt274 = l * 128 + 64;
		wall.anInt275 = k * 128 + 64;
		wall.anInt273 = i1;
		wall.primaryModel = class30_sub2_sub4;
		wall.secondaryModel = class30_sub2_sub4_1;
		wall.rotation = i;
		wall.secondaryRotation = j1;
		for (int l1 = k1; l1 >= 0; l1--)
			if (tiles[l1][l][k] == null)
				tiles[l1][l][k] = new Ground(l1, l, k);

		tiles[k1][l][k].obj1 = wall;
	}

	public void addWallDecoration(int i, int j, int k, int i1, int j1, int k1, Animable class30_sub2_sub4, int l1, byte byte0,
								  int i2, int j2) {
		if (class30_sub2_sub4 == null)
			return;
		WallDecoration class26 = new WallDecoration();
		class26.uid = i;
		class26.aByte506 = byte0;
		class26.anInt500 = l1 * 128 + 64 + j1;
		class26.anInt501 = j * 128 + 64 + i2;
		class26.anInt499 = k1;
		class26.model = class30_sub2_sub4;
		class26.anInt502 = j2;
		class26.anInt503 = k;
		for (int k2 = i1; k2 >= 0; k2--)
			if (tiles[k2][l1][j] == null)
				tiles[k2][l1][j] = new Ground(k2, l1, j);

		tiles[i1][l1][j].obj2 = class26;
	}

	public boolean canPlaceObject(int i, byte byte0, int j, int k, Animable class30_sub2_sub4, int l, int i1, int j1, int k1,
								  int l1) {
		if (class30_sub2_sub4 == null) {
			return true;
		} else {
			int i2 = l1 * 128 + 64 * l;
			int j2 = k1 * 128 + 64 * k;
			return placeObject(i1, l1, k1, l, k, i2, j2, j, class30_sub2_sub4, j1, false, i, byte0);
		}
	}

	public void addLargeObject(int i, int j, int k, int l, int i1, int j1, int k1, Animable class30_sub2_sub4,
							   boolean flag) {
		if (class30_sub2_sub4 == null)
			return;
		int l1 = k1 - j1;
		int i2 = i1 - j1;
		int j2 = k1 + j1;
		int k2 = i1 + j1;
		if (flag) {
			if (j > 640 && j < 1408)
				k2 += 128;
			if (j > 1152 && j < 1920)
				j2 += 128;
			if (j > 1664 || j < 384)
				i2 -= 128;
			if (j > 128 && j < 896)
				l1 -= 128;
		}
		l1 /= 128;
		i2 /= 128;
		j2 /= 128;
		k2 /= 128;
		placeObject(i, l1, i2, (j2 - l1) + 1, (k2 - i2) + 1, k1, i1, k, class30_sub2_sub4, j, true, l, (byte) 0);
	}

	public void addMultiTileObject(int j, int k, Animable class30_sub2_sub4, int l, int i1, int j1, int k1, int l1, int i2,
								   int j2, int k2) {
		if (class30_sub2_sub4 != null)
		{
			placeObject(j, l1, k2, (i2 - l1) + 1, (i1 - k2) + 1, j1, k, k1,
				class30_sub2_sub4, l, true, j2, (byte) 0);
		}
	}

	private boolean placeObject(int i, int j, int k, int l, int i1, int j1, int k1, int l1, Animable class30_sub2_sub4,
								int i2, boolean flag, int j2, byte byte0) {
		for (int k2 = j; k2 < j + l; k2++) {
			for (int l2 = k; l2 < k + i1; l2++) {
				if (k2 < 0 || l2 < 0 || k2 >= mapWidth || l2 >= mapHeight)
					return false;
				Ground class30_sub3 = tiles[i][k2][l2];
				if (class30_sub3 != null && class30_sub3.anInt1317 >= 5)
					return false;
			}

		}

		GameObject class28 = new GameObject();
		class28.uid = j2;
		class28.aByte530 = byte0;
		class28.anInt517 = i;
		class28.anInt519 = j1;
		class28.anInt520 = k1;
		class28.anInt518 = l1;
		class28.model = class30_sub2_sub4;
		class28.anInt522 = i2;
		class28.anInt523 = j;
		class28.anInt525 = k;
		class28.anInt524 = (j + l) - 1;
		class28.anInt526 = (k + i1) - 1;
		for (int i3 = j; i3 < j + l; i3++) {
			for (int j3 = k; j3 < k + i1; j3++) {
				int k3 = 0;
				if (i3 > j)
					k3++;
				if (i3 < (j + l) - 1)
					k3 += 4;
				if (j3 > k)
					k3 += 8;
				if (j3 < (k + i1) - 1)
					k3 += 2;
				for (int l3 = i; l3 >= 0; l3--)
					if (tiles[l3][i3][j3] == null)
						tiles[l3][i3][j3] = new Ground(l3, i3, j3);

				Ground class30_sub3_1 = tiles[i][i3][j3];
				class30_sub3_1.obj5Array[class30_sub3_1.anInt1317] = class28;
				class30_sub3_1.anIntArray1319[class30_sub3_1.anInt1317] = k3;
				class30_sub3_1.anInt1320 |= k3;
				class30_sub3_1.anInt1317++;
			}

		}

		if (flag)
			gameObjectCache[gameObjectCachePosition++] = class28;
		return true;
	}

	public void clearObj5Cache() {
		for (int i = 0; i < gameObjectCachePosition; i++) {
			GameObject gameObject = gameObjectCache[i];
			removeObject(gameObject);
			gameObjectCache[i] = null;
		}

		gameObjectCachePosition = 0;
	}

	private void removeObject(GameObject class28) {
		for (int j = class28.anInt523; j <= class28.anInt524; j++) {
			for (int k = class28.anInt525; k <= class28.anInt526; k++) {
				Ground class30_sub3 = tiles[class28.anInt517][j][k];
				if (class30_sub3 != null) {
					for (int l = 0; l < class30_sub3.anInt1317; l++) {
						if (class30_sub3.obj5Array[l] != class28)
							continue;
						class30_sub3.anInt1317--;
						for (int i1 = l; i1 < class30_sub3.anInt1317; i1++) {
							class30_sub3.obj5Array[i1] = class30_sub3.obj5Array[i1 + 1];
							class30_sub3.anIntArray1319[i1] = class30_sub3.anIntArray1319[i1 + 1];
						}

						class30_sub3.obj5Array[class30_sub3.anInt1317] = null;
						break;
					}

					class30_sub3.anInt1320 = 0;
					for (int j1 = 0; j1 < class30_sub3.anInt1317; j1++)
						class30_sub3.anInt1320 |= class30_sub3.anIntArray1319[j1];

				}
			}

		}

	}

	public void animateWallDecoration(int i, int k, int l, int i1) {
		Ground class30_sub3 = tiles[i1][l][i];
		if (class30_sub3 == null)
			return;
		WallDecoration class26 = class30_sub3.obj2;
		if (class26 != null) {
			int j1 = l * 128 + 64;
			int k1 = i * 128 + 64;
			class26.anInt500 = j1 + ((class26.anInt500 - j1) * k) / 16;
			class26.anInt501 = k1 + ((class26.anInt501 - k1) * k) / 16;
		}
	}

	public void removeWall(int i, int j, int k, byte byte0) {
		Ground class30_sub3 = tiles[j][i][k];
		if (byte0 != -119)
			lowMemory = !lowMemory;
		if (class30_sub3 != null) {
			class30_sub3.obj1 = null;
		}
	}

	public void removeWallDecoration(int j, int k, int l) {
		Ground class30_sub3 = tiles[k][l][j];
		if (class30_sub3 != null) {
			class30_sub3.obj2 = null;
		}
	}

	public void removeGameObject(int i, int k, int l) {
		Ground class30_sub3 = tiles[i][k][l];
		if (class30_sub3 == null)
			return;
		for (int j1 = 0; j1 < class30_sub3.anInt1317; j1++) {
			GameObject class28 = class30_sub3.obj5Array[j1];
			if ((class28.uid >> 29 & 3) == 2 && class28.anInt523 == k && class28.anInt525 == l) {
				removeObject(class28);
				return;
			}
		}

	}

	public void removeGroundDecoration(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][k][j];
		if (class30_sub3 == null)
			return;
		class30_sub3.obj3 = null;
	}

	public void removeRoofDecoration(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 != null) {
			class30_sub3.obj4 = null;
		}
	}

	public Wall getWall(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 == null)
			return null;
		else
			return class30_sub3.obj1;
	}

	public WallDecoration getWallDecoration(int i, int k, int l) {
		Ground class30_sub3 = tiles[l][i][k];
		if (class30_sub3 == null)
			return null;
		else
			return class30_sub3.obj2;
	}

	public GameObject getGameObject(int i, int j, int k) {
		Ground class30_sub3 = tiles[k][i][j];
		if (class30_sub3 == null)
			return null;
		for (int l = 0; l < class30_sub3.anInt1317; l++) {
			GameObject class28 = class30_sub3.obj5Array[l];
			if ((class28.uid >> 29 & 3) == 2 && class28.anInt523 == i && class28.anInt525 == j)
				return class28;
		}
		return null;
	}

	public GroundDecoration getGroundDecoration(int i, int j, int k) {
		Ground class30_sub3 = tiles[k][j][i];
		if (class30_sub3 == null || class30_sub3.obj3 == null)
			return null;
		else
			return class30_sub3.obj3;
	}

	public int getWallId(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 == null || class30_sub3.obj1 == null)
			return 0;
		else
			return class30_sub3.obj1.uid;
	}

	public int getWallDecorationId(int i, int j, int l) {
		Ground class30_sub3 = tiles[i][j][l];
		if (class30_sub3 == null || class30_sub3.obj2 == null)
			return 0;
		else
			return class30_sub3.obj2.uid;
	}

	public int getGameObjectId(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 == null)
			return 0;
		for (int l = 0; l < class30_sub3.anInt1317; l++) {
			GameObject class28 = class30_sub3.obj5Array[l];
			if ((class28.uid >> 29 & 3) == 2 && class28.anInt523 == j && class28.anInt525 == k)
				return class28.uid;
		}

		return 0;
	}

	public int getGroundDecorationId(int i, int j, int k) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 == null || class30_sub3.obj3 == null)
			return 0;
		else
			return class30_sub3.obj3.uid;
	}

	public int getObjectConfig(int i, int j, int k, int l) {
		Ground class30_sub3 = tiles[i][j][k];
		if (class30_sub3 == null)
			return -1;
		if (class30_sub3.obj1 != null && class30_sub3.obj1.uid == l)
			return class30_sub3.obj1.aByte281 & 0xff;
		if (class30_sub3.obj2 != null && class30_sub3.obj2.uid == l)
			return class30_sub3.obj2.aByte506 & 0xff;
		if (class30_sub3.obj3 != null && class30_sub3.obj3.uid == l)
			return class30_sub3.obj3.aByte816 & 0xff;
		for (int i1 = 0; i1 < class30_sub3.anInt1317; i1++)
			if (class30_sub3.obj5Array[i1].uid == l)
				return class30_sub3.obj5Array[i1].aByte530 & 0xff;

		return -1;
	}

	public void applyLighting(int i, int k, int i1) {
		int j = 64;
		int l = 768;
		int j1 = (int) Math.sqrt(k * k + i * i + i1 * i1);
		int k1 = l * j1 >> 8;
		for (int l1 = 0; l1 < levelCount; l1++) {
			for (int i2 = 0; i2 < mapWidth; i2++) {
				for (int j2 = 0; j2 < mapHeight; j2++) {
					Ground class30_sub3 = tiles[l1][i2][j2];
					if (class30_sub3 != null) {
						Wall class10 = class30_sub3.obj1;
						if (class10 != null && class10.primaryModel != null
								&& class10.primaryModel.vertices != null) {
							method307(l1, 1, 1, i2, j2, (Model) class10.primaryModel);
							if (class10.secondaryModel != null
									&& class10.secondaryModel.vertices != null) {
								method307(l1, 1, 1, i2, j2, (Model) class10.secondaryModel);
								method308((Model) class10.primaryModel,
										(Model) class10.secondaryModel, 0, 0, 0, false);
								((Model) class10.secondaryModel).calculateVertexLighting(j, k1, k, i, i1);
							}
							((Model) class10.primaryModel).calculateVertexLighting(j, k1, k, i, i1);
						}
						for (int k2 = 0; k2 < class30_sub3.anInt1317; k2++) {
							GameObject class28 = class30_sub3.obj5Array[k2];
							if (class28 != null && class28.model != null
									&& class28.model.vertices != null) {
								method307(l1, (class28.anInt524 - class28.anInt523) + 1,
										(class28.anInt526 - class28.anInt525) + 1, i2, j2,
										(Model) class28.model);
								((Model) class28.model).calculateVertexLighting(j, k1, k, i, i1);
							}
						}

						GroundDecoration class49 = class30_sub3.obj3;
						if (class49 != null && class49.model.vertices != null) {
							method306(i2, l1, (Model) class49.model, j2);
							((Model) class49.model).calculateVertexLighting(j, k1, k, i, i1);
						}
					}
				}

			}

		}

	}

	private void method306(int i, int j, Model model, int k) {
		if (i < mapWidth) {
			Ground class30_sub3 = tiles[j][i + 1][k];
			if (class30_sub3 != null && class30_sub3.obj3 != null
					&& class30_sub3.obj3.model.vertices != null)
				method308(model, (Model) class30_sub3.obj3.model, 128, 0, 0, true);
		}
		if (k < mapWidth) {
			Ground class30_sub3_1 = tiles[j][i][k + 1];
			if (class30_sub3_1 != null && class30_sub3_1.obj3 != null
					&& class30_sub3_1.obj3.model.vertices != null)
				method308(model, (Model) class30_sub3_1.obj3.model, 0, 0, 128, true);
		}
		if (i < mapWidth && k < mapHeight) {
			Ground class30_sub3_2 = tiles[j][i + 1][k + 1];
			if (class30_sub3_2 != null && class30_sub3_2.obj3 != null
					&& class30_sub3_2.obj3.model.vertices != null)
				method308(model, (Model) class30_sub3_2.obj3.model, 128, 0, 128, true);
		}
		if (i < mapWidth && k > 0) {
			Ground class30_sub3_3 = tiles[j][i + 1][k - 1];
			if (class30_sub3_3 != null && class30_sub3_3.obj3 != null
					&& class30_sub3_3.obj3.model.vertices != null)
				method308(model, (Model) class30_sub3_3.obj3.model, 128, 0, -128, true);
		}
	}

	private void method307(int i, int j, int k, int l, int i1, Model model) {
		boolean flag = true;
		int j1 = l;
		int k1 = l + j;
		int l1 = i1 - 1;
		int i2 = i1 + k;
		for (int j2 = i; j2 <= i + 1; j2++)
			if (j2 != levelCount) {
				for (int k2 = j1; k2 <= k1; k2++)
					if (k2 >= 0 && k2 < mapWidth) {
						for (int l2 = l1; l2 <= i2; l2++)
							if (l2 >= 0 && l2 < mapHeight && (!flag || k2 >= k1 || l2 >= i2 || l2 < i1 && k2 != l)) {
								Ground class30_sub3 = tiles[j2][k2][l2];
								if (class30_sub3 != null) {
									int i3 = (heightMap[j2][k2][l2]
											+ heightMap[j2][k2 + 1][l2]
											+ heightMap[j2][k2][l2 + 1]
											+ heightMap[j2][k2 + 1][l2 + 1]) / 4
											- (heightMap[i][l][i1] + heightMap[i][l + 1][i1]
													+ heightMap[i][l][i1 + 1]
													+ heightMap[i][l + 1][i1 + 1]) / 4;
									Wall class10 = class30_sub3.obj1;
									if (class10 != null && class10.primaryModel != null
											&& class10.primaryModel.vertices != null)
										method308(model, (Model) class10.primaryModel,
												(k2 - l) * 128 + (1 - j) * 64, i3, (l2 - i1) * 128 + (1 - k) * 64,
												flag);
									if (class10 != null && class10.secondaryModel != null
											&& class10.secondaryModel.vertices != null)
										method308(model, (Model) class10.secondaryModel,
												(k2 - l) * 128 + (1 - j) * 64, i3, (l2 - i1) * 128 + (1 - k) * 64,
												flag);
									for (int j3 = 0; j3 < class30_sub3.anInt1317; j3++) {
										GameObject class28 = class30_sub3.obj5Array[j3];
										if (class28 != null && class28.model != null
												&& class28.model.vertices != null) {
											int k3 = (class28.anInt524 - class28.anInt523) + 1;
											int l3 = (class28.anInt526 - class28.anInt525) + 1;
											method308(model, (Model) class28.model,
													(class28.anInt523 - l) * 128 + (k3 - j) * 64, i3,
													(class28.anInt525 - i1) * 128 + (l3 - k) * 64, flag);
										}
									}

								}
							}

					}

				j1--;
				flag = false;
			}

	}

	private void method308(Model model, Model model_1, int i, int j, int k, boolean flag) {
		lightingCycle++;
		int l = 0;
		int[] ai = model_1.verticesX;
		int i1 = model_1.vertexCount;
		for (int j1 = 0; j1 < model.vertexCount; j1++) {
			Point3D point3D = model.vertices[j1];
			Point3D point3D_1 = model.aPoint3DArray1660[j1];
			if (point3D_1.vertexCount != 0) {
				int i2 = model.verticesY[j1] - j;
				if (i2 <= model_1.maxY) {
					int j2 = model.verticesX[j1] - i;
					if (j2 >= model_1.anInt1646 && j2 <= model_1.anInt1647) {
						int k2 = model.verticesZ[j1] - k;
						if (k2 >= model_1.anInt1649 && k2 <= model_1.anInt1648) {
							for (int l2 = 0; l2 < i1; l2++) {
								Point3D point3D_2 = model_1.vertices[l2];
								Point3D point3D_3 = model_1.aPoint3DArray1660[l2];
								if (j2 == ai[l2] && k2 == model_1.verticesZ[l2] && i2 == model_1.verticesY[l2]
										&& point3D_3.vertexCount != 0) {
									point3D.normalX += point3D_3.normalX;
									point3D.normalY += point3D_3.normalY;
									point3D.normalZ += point3D_3.normalZ;
									point3D.vertexCount += point3D_3.vertexCount;
									point3D_2.normalX += point3D_1.normalX;
									point3D_2.normalY += point3D_1.normalY;
									point3D_2.normalZ += point3D_1.normalZ;
									point3D_2.vertexCount += point3D_1.vertexCount;
									l++;
									lightingMarker1[j1] = lightingCycle;
									lightingMarker2[l2] = lightingCycle;
								}
							}

						}
					}
				}
			}
		}

		if (l < 3 || !flag)
			return;
		for (int k1 = 0; k1 < model.triangleCount; k1++)
			if (lightingMarker1[model.triangleVertexA[k1]] == lightingCycle
					&& lightingMarker1[model.triangleVertexB[k1]] == lightingCycle
					&& lightingMarker1[model.triangleVertexC[k1]] == lightingCycle)
				model.triangleInfo[k1] = -1;

		for (int l1 = 0; l1 < model_1.triangleCount; l1++)
			if (lightingMarker2[model_1.triangleVertexA[l1]] == lightingCycle
					&& lightingMarker2[model_1.triangleVertexB[l1]] == lightingCycle
					&& lightingMarker2[model_1.triangleVertexC[l1]] == lightingCycle)
				model_1.triangleInfo[l1] = -1;

	}
public void setMousePosition(int i, int j) {
		mouseSelection = true;
		mouseX = j;
		mouseY = i;
		selectedTileX = -1;
		selectedTileY = -1;
	}

	public void render(int i, int j, int k, int l, int i1, int j1) {
		if (i < 0)
			i = 0;
		else if (i >= mapWidth * 128)
			i = mapWidth * 128 - 1;
		if (j < 0)
			j = 0;
		else if (j >= mapHeight * 128)
			j = mapHeight * 128 - 1;
		renderCycle++;
		sinVertical = Model.modelIntArray1[j1];
		cosVertical = Model.modelIntArray2[j1];
		sinHorizontal = Model.modelIntArray1[k];
		cosHorizontal = Model.modelIntArray2[k];
		aBooleanArrayArray492 = aBooleanArrayArrayArrayArray491[(j1 - 128) / 32][k / 64];
		cameraX = i;
		cameraY = l;
		cameraZ = j;
		cameraTileX = i / 128;
		cameraTileY = j / 128;
		currentPlane = i1;
		minTileX = cameraTileX - 25;
		if (minTileX < 0)
			minTileX = 0;
		minTileY = cameraTileY - 25;
		if (minTileY < 0)
			minTileY = 0;
		maxTileX = cameraTileX + 25;
		if (maxTileX > mapWidth)
			maxTileX = mapWidth;
		maxTileY = cameraTileY + 25;
		if (maxTileY > mapHeight)
			maxTileY = mapHeight;
		processOccluders();
		visibleTileCount = 0;
		for (int k1 = currentLevel; k1 < levelCount; k1++) {
			Ground[][] aclass30_sub3 = tiles[k1];
			for (int i2 = minTileX; i2 < maxTileX; i2++) {
				for (int k2 = minTileY; k2 < maxTileY; k2++) {
					Ground class30_sub3 = aclass30_sub3[i2][k2];
					if (class30_sub3 != null)
						if (class30_sub3.anInt1321 > i1
								|| !aBooleanArrayArray492[(i2 - cameraTileX) + 25][(k2 - cameraTileY) + 25]
										&& heightMap[k1][i2][k2] - l < 2000) {
							class30_sub3.aBoolean1322 = false;
							class30_sub3.aBoolean1323 = false;
							class30_sub3.anInt1325 = 0;
						} else {
							class30_sub3.aBoolean1322 = true;
							class30_sub3.aBoolean1323 = true;
							class30_sub3.aBoolean1324 = class30_sub3.anInt1317 > 0;
							visibleTileCount++;
						}
				}

			}

		}

		for (int l1 = currentLevel; l1 < levelCount; l1++) {
			Ground[][] aclass30_sub3_1 = tiles[l1];
			for (int l2 = -25; l2 <= 0; l2++) {
				int i3 = cameraTileX + l2;
				int k3 = cameraTileX - l2;
				if (i3 >= minTileX || k3 < maxTileX) {
					for (int i4 = -25; i4 <= 0; i4++) {
						int k4 = cameraTileY + i4;
						int i5 = cameraTileY - i4;
						if (i3 >= minTileX) {
							if (k4 >= minTileY) {
								Ground class30_sub3_1 = aclass30_sub3_1[i3][k4];
								if (class30_sub3_1 != null && class30_sub3_1.aBoolean1322)
									renderTile(class30_sub3_1, true);
							}
							if (i5 < maxTileY) {
								Ground class30_sub3_2 = aclass30_sub3_1[i3][i5];
								if (class30_sub3_2 != null && class30_sub3_2.aBoolean1322)
									renderTile(class30_sub3_2, true);
							}
						}
						if (k3 < maxTileX) {
							if (k4 >= minTileY) {
								Ground class30_sub3_3 = aclass30_sub3_1[k3][k4];
								if (class30_sub3_3 != null && class30_sub3_3.aBoolean1322)
									renderTile(class30_sub3_3, true);
							}
							if (i5 < maxTileY) {
								Ground class30_sub3_4 = aclass30_sub3_1[k3][i5];
								if (class30_sub3_4 != null && class30_sub3_4.aBoolean1322)
									renderTile(class30_sub3_4, true);
							}
						}
						if (visibleTileCount == 0) {
							mouseSelection = false;
							return;
						}
					}

				}
			}

		}

		for (int j2 = currentLevel; j2 < levelCount; j2++) {
			Ground[][] aclass30_sub3_2 = tiles[j2];
			for (int j3 = -25; j3 <= 0; j3++) {
				int l3 = cameraTileX + j3;
				int j4 = cameraTileX - j3;
				if (l3 >= minTileX || j4 < maxTileX) {
					for (int l4 = -25; l4 <= 0; l4++) {
						int j5 = cameraTileY + l4;
						int k5 = cameraTileY - l4;
						if (l3 >= minTileX) {
							if (j5 >= minTileY) {
								Ground class30_sub3_5 = aclass30_sub3_2[l3][j5];
								if (class30_sub3_5 != null && class30_sub3_5.aBoolean1322)
									renderTile(class30_sub3_5, false);
							}
							if (k5 < maxTileY) {
								Ground class30_sub3_6 = aclass30_sub3_2[l3][k5];
								if (class30_sub3_6 != null && class30_sub3_6.aBoolean1322)
									renderTile(class30_sub3_6, false);
							}
						}
						if (j4 < maxTileX) {
							if (j5 >= minTileY) {
								Ground class30_sub3_7 = aclass30_sub3_2[j4][j5];
								if (class30_sub3_7 != null && class30_sub3_7.aBoolean1322)
									renderTile(class30_sub3_7, false);
							}
							if (k5 < maxTileY) {
								Ground class30_sub3_8 = aclass30_sub3_2[j4][k5];
								if (class30_sub3_8 != null && class30_sub3_8.aBoolean1322)
									renderTile(class30_sub3_8, false);
							}
						}
						if (visibleTileCount == 0) {
							mouseSelection = false;
							return;
						}
					}

				}
			}

		}

		mouseSelection = false;
	}

	// Add these fields to WorldController class
	private Ground[] tileQueue = new Ground[2048];
	private int queueHead = 0;
	private int queueTail = 0;

	private static final ExecutorService worldRenderPool =
		Executors.newFixedThreadPool(Math.min(4, Runtime.getRuntime().availableProcessors()));
	private static final AtomicInteger activeRegions = new AtomicInteger(0);

	private void renderTile(Ground startTile, boolean flag) {
		if (!flag) {
			// Use parallel processing for large render jobs
			if (visibleTileCount > 100) { // Only parallelize for substantial work
				renderTileParallel(startTile, flag);
				return;
			}
		}

		// Fall back to original for small jobs
		renderTileOriginal(startTile, flag);
	}

	private void renderTileParallel(Ground startTile, boolean flag) {
		// Split rendering into 4 spatial regions
		int regionWidth = (maxTileX - minTileX) / 4;
		List<Future<Void>> futures = new ArrayList<>();

		for (int r = 0; r < 4; r++) {
			final int startX = minTileX + (r * regionWidth);
			final int endX = (r == 3) ? maxTileX : startX + regionWidth;

			futures.add(worldRenderPool.submit(() -> {
				activeRegions.incrementAndGet();
				try {
					renderTileRegion(startX, endX, minTileY, maxTileY);
				} finally {
					activeRegions.decrementAndGet();
				}
				return null;
			}));
		}

		// Wait for all regions with timeout
		for (Future<Void> future : futures) {
			try {
				future.get(5, TimeUnit.MILLISECONDS); // 5ms max wait
			} catch (Exception e) {
				// If threading fails, fall back immediately
				renderTileOriginal(startTile, flag);
				return;
			}
		}
	}

	private void renderTileRegion(int startX, int endX, int startY, int endY) {
		Ground[] localQueue = new Ground[512]; // Smaller local queue
		int queueHead = 0, queueTail = 0;

		// Process only tiles in this region
		for (int k = currentLevel; k < levelCount; k++) {
			Ground[][] aclass30_sub3 = tiles[k];
			for (int i = startX; i < endX; i++) {
				for (int j = startY; j < endY; j++) {
					Ground tile = aclass30_sub3[i][j];
					if (tile != null && tile.aBoolean1322) {
						// Quick local tile processing - simplified from original
						processLocalTile(tile, k, i, j);
					}
				}
			}
		}
	}

	private void processLocalTile(Ground tile, int k, int i, int j) {
		// Simplified tile rendering - core logic only
		tile.aBoolean1322 = false;

		// Render objects immediately without complex queuing
		if (tile.aBoolean1324) {
			for (int objIdx = 0; objIdx < tile.anInt1317; objIdx++) {
				GameObject obj5 = tile.obj5Array[objIdx];
				if (obj5 != null && obj5.lastRenderCycle != renderCycle) {
					obj5.model.render(obj5.anInt522, sinVertical, cosVertical, sinHorizontal,
						cosHorizontal, obj5.anInt519 - cameraX, obj5.anInt518 - cameraY,
						obj5.anInt520 - cameraZ, obj5.uid);
					obj5.lastRenderCycle = renderCycle;
				}
			}
			tile.aBoolean1324 = false;
		}
	}

	// Keep original as fallback
	private void renderTileOriginal(Ground startTile, boolean flag) {
		// Reset queue
		queueHead = queueTail = 0;

		// Add starting tile to queue
		tileQueue[queueTail] = startTile;
		queueTail = (queueTail + 1) % tileQueue.length;

		// Cache frequently accessed values
		final Ground[][][] localGroundArray = tiles;
		final int[] lookup478 = directionLookup;
		final int[] lookup479 = visibilityMask;
		final int[] lookup480 = visibilityFlags;
		final int[] lookup481 = wallPattern1;
		final int[] lookup482 = wallPattern2;
		final int[] lookup483 = wallPattern3;
		final int[] lookup484 = wallPattern4;
		final int[] lookup463 = wallOffsetX1;
		final int[] lookup464 = wallOffsetZ1;
		final int[] lookup465 = wallOffsetX2;
		final int[] lookup466 = wallOffsetZ2;

		while (queueHead != queueTail) {
			// Dequeue tile
			Ground currentTile = tileQueue[queueHead];
			queueHead = (queueHead + 1) % tileQueue.length;

			if (currentTile == null || !currentTile.aBoolean1323)
				continue;

			// Cache coordinates
			final int i = currentTile.anInt1308;
			final int j = currentTile.anInt1309;
			final int k = currentTile.anInt1307;
			final int l = currentTile.anInt1310;
			final Ground[][] aclass30_sub3 = localGroundArray[k];

			if (currentTile.aBoolean1322) {
				if (flag) {
					// Combined neighbor visibility check
					boolean shouldContinue = false;
					if (k > 0) {
						Ground neighbor = localGroundArray[k - 1][i][j];
						if (neighbor != null && neighbor.aBoolean1323) {
							shouldContinue = true;
						}
					}
					if (!shouldContinue && i <= cameraTileX && i > minTileX) {
						Ground neighbor = aclass30_sub3[i - 1][j];
						if (neighbor != null && neighbor.aBoolean1323 &&
							(neighbor.aBoolean1322 || (currentTile.anInt1320 & 1) == 0)) {
							shouldContinue = true;
						}
					}
					if (!shouldContinue && i >= cameraTileX && i < maxTileX - 1) {
						Ground neighbor = aclass30_sub3[i + 1][j];
						if (neighbor != null && neighbor.aBoolean1323 &&
							(neighbor.aBoolean1322 || (currentTile.anInt1320 & 4) == 0)) {
							shouldContinue = true;
						}
					}
					if (!shouldContinue && j <= cameraTileY && j > minTileY) {
						Ground neighbor = aclass30_sub3[i][j - 1];
						if (neighbor != null && neighbor.aBoolean1323 &&
							(neighbor.aBoolean1322 || (currentTile.anInt1320 & 8) == 0)) {
							shouldContinue = true;
						}
					}
					if (!shouldContinue && j >= cameraTileY && j < maxTileY - 1) {
						Ground neighbor = aclass30_sub3[i][j + 1];
						if (neighbor != null && neighbor.aBoolean1323 &&
							(neighbor.aBoolean1322 || (currentTile.anInt1320 & 2) == 0)) {
							shouldContinue = true;
						}
					}
					if (shouldContinue) continue;
				} else {
					flag = true;
				}

				currentTile.aBoolean1322 = false;

				// Render lower level
				if (currentTile.bridgeTile != null) {
					Ground lowerTile = currentTile.bridgeTile;
					if (lowerTile.simpleTile != null) {
						if (isTileVisible(0, i, j))
							renderPlainTile(lowerTile.simpleTile, 0, sinVertical, cosVertical, sinHorizontal, cosHorizontal, i, j);
					} else if (lowerTile.floorDecoration != null && isTileVisible(0, i, j)) {
						renderComplexTile(i, sinVertical, sinHorizontal, lowerTile.floorDecoration, cosVertical, j, cosHorizontal);
					}

					Wall lowerObj1 = lowerTile.obj1;
					if (lowerObj1 != null) {
						lowerObj1.primaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							lowerObj1.anInt274 - cameraX, lowerObj1.anInt273 - cameraY,
							lowerObj1.anInt275 - cameraZ, lowerObj1.uid);
					}

					for (int i2 = 0; i2 < lowerTile.anInt1317; i2++) {
						GameObject obj5 = lowerTile.obj5Array[i2];
						if (obj5 != null) {
							obj5.model.render(obj5.anInt522, sinVertical, cosVertical, sinHorizontal,
								cosHorizontal, obj5.anInt519 - cameraX, obj5.anInt518 - cameraY,
								obj5.anInt520 - cameraZ, obj5.uid);
						}
					}
				}

				// Render current level
				boolean flag1 = false;
				if (currentTile.simpleTile != null) {
					if (isTileVisible(l, i, j)) {
						flag1 = true;
						renderPlainTile(currentTile.simpleTile, l, sinVertical, cosVertical, sinHorizontal, cosHorizontal, i, j);
					}
				} else if (currentTile.floorDecoration != null && isTileVisible(l, i, j)) {
					flag1 = true;
					renderComplexTile(i, sinVertical, sinHorizontal, currentTile.floorDecoration, cosVertical, j, cosHorizontal);
				}

				// Object rendering with cached direction calculation
				int j1 = 0;
				int j2 = 0;
				Wall obj1 = currentTile.obj1;
				WallDecoration obj2 = currentTile.obj2;

				if (obj1 != null || obj2 != null) {
					if (cameraTileX == i) j1++;
					else if (cameraTileX < i) j1 += 2;
					if (cameraTileY == j) j1 += 3;
					else if (cameraTileY > j) j1 += 6;
					j2 = lookup478[j1];
					currentTile.anInt1328 = lookup480[j1];
				}

				if (obj1 != null) {
					if ((obj1.rotation & lookup479[j1]) != 0) {
						switch (obj1.rotation) {
							case 16:
								currentTile.anInt1325 = 3;
								currentTile.anInt1326 = lookup481[j1];
								currentTile.anInt1327 = 3 - currentTile.anInt1326;
								break;
							case 32:
								currentTile.anInt1325 = 6;
								currentTile.anInt1326 = lookup482[j1];
								currentTile.anInt1327 = 6 - currentTile.anInt1326;
								break;
							case 64:
								currentTile.anInt1325 = 12;
								currentTile.anInt1326 = lookup483[j1];
								currentTile.anInt1327 = 12 - currentTile.anInt1326;
								break;
							default:
								currentTile.anInt1325 = 9;
								currentTile.anInt1326 = lookup484[j1];
								currentTile.anInt1327 = 9 - currentTile.anInt1326;
								break;
						}
					} else {
						currentTile.anInt1325 = 0;
					}

					if ((obj1.rotation & j2) != 0 && isWallVisible(l, i, j, obj1.rotation)) {
						obj1.primaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							obj1.anInt274 - cameraX, obj1.anInt273 - cameraY,
							obj1.anInt275 - cameraZ, obj1.uid);
					}
					if ((obj1.secondaryRotation & j2) != 0 && isWallVisible(l, i, j, obj1.secondaryRotation)) {
						obj1.secondaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							obj1.anInt274 - cameraX, obj1.anInt273 - cameraY,
							obj1.anInt275 - cameraZ, obj1.uid);
					}
				}

				if (obj2 != null && isDecorationVisible(l, i, j, obj2.model.modelHeight)) {
					if ((obj2.anInt502 & j2) != 0) {
						obj2.model.render(obj2.anInt503, sinVertical, cosVertical, sinHorizontal,
							cosHorizontal, obj2.anInt500 - cameraX, obj2.anInt499 - cameraY,
							obj2.anInt501 - cameraZ, obj2.uid);
					} else if ((obj2.anInt502 & 0x300) != 0) {
						int deltaX = obj2.anInt500 - cameraX;
						int deltaY = obj2.anInt499 - cameraY;
						int deltaZ = obj2.anInt501 - cameraZ;
						int orientation = obj2.anInt503;

						int xSign = (orientation == 1 || orientation == 2) ? -deltaX : deltaX;
						int zSign = (orientation == 2 || orientation == 3) ? -deltaZ : deltaZ;

						if ((obj2.anInt502 & 0x100) != 0 && zSign < xSign) {
							int renderX = deltaX + lookup463[orientation];
							int renderZ = deltaZ + lookup464[orientation];
							obj2.model.render(orientation * 512 + 256, sinVertical, cosVertical,
								sinHorizontal, cosHorizontal, renderX, deltaY, renderZ, obj2.uid);
						}
						if ((obj2.anInt502 & 0x200) != 0 && zSign > xSign) {
							int renderX = deltaX + lookup465[orientation];
							int renderZ = deltaZ + lookup466[orientation];
							obj2.model.render(orientation * 512 + 1280 & 0x7ff, sinVertical,
								cosVertical, sinHorizontal, cosHorizontal, renderX, deltaY, renderZ, obj2.uid);
						}
					}
				}

				if (flag1) {
					GroundDecoration obj3 = currentTile.obj3;
					if (obj3 != null) {
						obj3.model.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							obj3.anInt812 - cameraX, obj3.anInt811 - cameraY, obj3.anInt813 - cameraZ,
							obj3.uid);
					}

					RoofDecoration obj4 = currentTile.obj4;
					if (obj4 != null && obj4.anInt52 == 0) {
						if (obj4.aClass30_Sub2_Sub4_49 != null) {
							obj4.aClass30_Sub2_Sub4_49.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
								obj4.anInt46 - cameraX, obj4.anInt45 - cameraY,
								obj4.anInt47 - cameraZ, obj4.uid);
						}
						if (obj4.aClass30_Sub2_Sub4_50 != null) {
							obj4.aClass30_Sub2_Sub4_50.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
								obj4.anInt46 - cameraX, obj4.anInt45 - cameraY,
								obj4.anInt47 - cameraZ, obj4.uid);
						}
						if (obj4.aClass30_Sub2_Sub4_48 != null) {
							obj4.aClass30_Sub2_Sub4_48.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
								obj4.anInt46 - cameraX, obj4.anInt45 - cameraY,
								obj4.anInt47 - cameraZ, obj4.uid);
						}
					}
				}

				// Add connected neighbors to queue
				int connectionMask = currentTile.anInt1320;
				if (connectionMask != 0) {
					if (i < cameraTileX && (connectionMask & 4) != 0) {
						Ground neighbor = aclass30_sub3[i + 1][j];
						if (neighbor != null && neighbor.aBoolean1323) {
							tileQueue[queueTail] = neighbor;
							queueTail = (queueTail + 1) % tileQueue.length;
						}
					}
					if (j < cameraTileY && (connectionMask & 2) != 0) {
						Ground neighbor = aclass30_sub3[i][j + 1];
						if (neighbor != null && neighbor.aBoolean1323) {
							tileQueue[queueTail] = neighbor;
							queueTail = (queueTail + 1) % tileQueue.length;
						}
					}
					if (i > cameraTileX && (connectionMask & 1) != 0) {
						Ground neighbor = aclass30_sub3[i - 1][j];
						if (neighbor != null && neighbor.aBoolean1323) {
							tileQueue[queueTail] = neighbor;
							queueTail = (queueTail + 1) % tileQueue.length;
						}
					}
					if (j > cameraTileY && (connectionMask & 8) != 0) {
						Ground neighbor = aclass30_sub3[i][j - 1];
						if (neighbor != null && neighbor.aBoolean1323) {
							tileQueue[queueTail] = neighbor;
							queueTail = (queueTail + 1) % tileQueue.length;
						}
					}
				}
			}

			// Wall occlusion check
			if (currentTile.anInt1325 != 0) {
				boolean shouldRenderWall = true;
				for (int objIdx = 0; objIdx < currentTile.anInt1317; objIdx++) {
					GameObject obj5 = currentTile.obj5Array[objIdx];
					if (obj5.lastRenderCycle != renderCycle &&
						(currentTile.anIntArray1319[objIdx] & currentTile.anInt1325) == currentTile.anInt1326) {
						shouldRenderWall = false;
						break;
					}
				}

				if (shouldRenderWall) {
					Wall wallObj = currentTile.obj1;
					if (isWallVisible(l, i, j, wallObj.rotation)) {
						wallObj.primaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							wallObj.anInt274 - cameraX, wallObj.anInt273 - cameraY,
							wallObj.anInt275 - cameraZ, wallObj.uid);
					}
					currentTile.anInt1325 = 0;
				}
			}

			// Object sorting and rendering
			if (currentTile.aBoolean1324) {
				try {
					int objectCount = currentTile.anInt1317;
					currentTile.aBoolean1324 = false;
					int validObjects = 0;

					// First pass: check visibility and collect valid objects
					for (int objIdx = 0; objIdx < objectCount; objIdx++) {
						GameObject obj5 = currentTile.obj5Array[objIdx];
						if (obj5.lastRenderCycle == renderCycle) continue;

						boolean isOccluded = false;
						for (int tileX = obj5.anInt523; tileX <= obj5.anInt524 && !isOccluded; tileX++) {
							for (int tileY = obj5.anInt525; tileY <= obj5.anInt526 && !isOccluded; tileY++) {
								Ground checkTile = aclass30_sub3[tileX][tileY];
								if (checkTile.aBoolean1322) {
									currentTile.aBoolean1324 = true;
									isOccluded = true;
								} else if (checkTile.anInt1325 != 0) {
									int edgeMask = 0;
									if (tileX > obj5.anInt523) edgeMask++;
									if (tileX < obj5.anInt524) edgeMask += 4;
									if (tileY > obj5.anInt525) edgeMask += 8;
									if (tileY < obj5.anInt526) edgeMask += 2;
									if ((edgeMask & checkTile.anInt1325) == currentTile.anInt1327) {
										currentTile.aBoolean1324 = true;
										isOccluded = true;
									}
								}
							}
						}

						if (!isOccluded) {
							aClass28Array462[validObjects++] = obj5;
							// Calculate distance for sorting
							int distX = Math.max(Math.abs(cameraTileX - obj5.anInt523), Math.abs(obj5.anInt524 - cameraTileX));
							int distY = Math.max(Math.abs(cameraTileY - obj5.anInt525), Math.abs(obj5.anInt526 - cameraTileY));
							obj5.renderDistance = distX + distY;
						}
					}

					// Render objects by distance (furthest first)
					while (validObjects > 0) {
						int maxDistance = -50;
						int selectedIdx = -1;

						for (int objIdx = 0; objIdx < validObjects; objIdx++) {
							GameObject obj5 = aClass28Array462[objIdx];
							if (obj5.lastRenderCycle != renderCycle) {
								if (obj5.renderDistance > maxDistance) {
									maxDistance = obj5.renderDistance;
									selectedIdx = objIdx;
								} else if (obj5.renderDistance == maxDistance) {
									// Distance tie-breaker
									int dist1 = (obj5.anInt519 - cameraX) * (obj5.anInt519 - cameraX) +
										(obj5.anInt520 - cameraZ) * (obj5.anInt520 - cameraZ);
									int dist2 = (aClass28Array462[selectedIdx].anInt519 - cameraX) *
										(aClass28Array462[selectedIdx].anInt519 - cameraX) +
										(aClass28Array462[selectedIdx].anInt520 - cameraZ) *
											(aClass28Array462[selectedIdx].anInt520 - cameraZ);
									if (dist1 > dist2) {
										selectedIdx = objIdx;
									}
								}
							}
						}

						if (selectedIdx == -1) break;

						GameObject selectedObj = aClass28Array462[selectedIdx];
						selectedObj.lastRenderCycle = renderCycle;

						if (!isObjectVisible(l, selectedObj.anInt523, selectedObj.anInt524, selectedObj.anInt525,
							selectedObj.anInt526, selectedObj.model.modelHeight)) {
							selectedObj.model.render(selectedObj.anInt522, sinVertical, cosVertical,
								sinHorizontal, cosHorizontal, selectedObj.anInt519 - cameraX, selectedObj.anInt518 - cameraY,
								selectedObj.anInt520 - cameraZ, selectedObj.uid);
						}

						// Add affected tiles back to queue
						for (int tileX = selectedObj.anInt523; tileX <= selectedObj.anInt524; tileX++) {
							for (int tileY = selectedObj.anInt525; tileY <= selectedObj.anInt526; tileY++) {
								Ground affectedTile = aclass30_sub3[tileX][tileY];
								if (affectedTile.anInt1325 != 0) {
									tileQueue[queueTail] = affectedTile;
									queueTail = (queueTail + 1) % tileQueue.length;
								} else if ((tileX != i || tileY != j) && affectedTile.aBoolean1323) {
									tileQueue[queueTail] = affectedTile;
									queueTail = (queueTail + 1) % tileQueue.length;
								}
							}
						}
					}

					if (currentTile.aBoolean1324) continue;
				} catch (Exception ex) {
					currentTile.aBoolean1324 = false;
				}
			}

			// Final neighbor visibility check and cleanup
			if (!currentTile.aBoolean1323 || currentTile.anInt1325 != 0) continue;

			boolean hasVisibleNeighbor = false;
			if (i <= cameraTileX && i > minTileX) {
				Ground neighbor = aclass30_sub3[i - 1][j];
				if (neighbor != null && neighbor.aBoolean1323) hasVisibleNeighbor = true;
			}
			if (!hasVisibleNeighbor && i >= cameraTileX && i < maxTileX - 1) {
				Ground neighbor = aclass30_sub3[i + 1][j];
				if (neighbor != null && neighbor.aBoolean1323) hasVisibleNeighbor = true;
			}
			if (!hasVisibleNeighbor && j <= cameraTileY && j > minTileY) {
				Ground neighbor = aclass30_sub3[i][j - 1];
				if (neighbor != null && neighbor.aBoolean1323) hasVisibleNeighbor = true;
			}
			if (!hasVisibleNeighbor && j >= cameraTileY && j < maxTileY - 1) {
				Ground neighbor = aclass30_sub3[i][j + 1];
				if (neighbor != null && neighbor.aBoolean1323) hasVisibleNeighbor = true;
			}

			if (hasVisibleNeighbor) continue;

			currentTile.aBoolean1323 = false;
			visibleTileCount--;

			// Render elevated objects
			RoofDecoration obj4 = currentTile.obj4;
			if (obj4 != null && obj4.anInt52 != 0) {
				if (obj4.aClass30_Sub2_Sub4_49 != null) {
					obj4.aClass30_Sub2_Sub4_49.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
						obj4.anInt46 - cameraX, obj4.anInt45 - cameraY - obj4.anInt52,
						obj4.anInt47 - cameraZ, obj4.uid);
				}
				if (obj4.aClass30_Sub2_Sub4_50 != null) {
					obj4.aClass30_Sub2_Sub4_50.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
						obj4.anInt46 - cameraX, obj4.anInt45 - cameraY - obj4.anInt52,
						obj4.anInt47 - cameraZ, obj4.uid);
				}
				if (obj4.aClass30_Sub2_Sub4_48 != null) {
					obj4.aClass30_Sub2_Sub4_48.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
						obj4.anInt46 - cameraX, obj4.anInt45 - cameraY - obj4.anInt52,
						obj4.anInt47 - cameraZ, obj4.uid);
				}
			}

			// Render walls based on visibility
			if (currentTile.anInt1328 != 0) {
				WallDecoration wallObj2 = currentTile.obj2;
				if (wallObj2 != null && isDecorationVisible(l, i, j, wallObj2.model.modelHeight)) {
					if ((wallObj2.anInt502 & currentTile.anInt1328) != 0) {
						wallObj2.model.render(wallObj2.anInt503, sinVertical, cosVertical, sinHorizontal,
							cosHorizontal, wallObj2.anInt500 - cameraX, wallObj2.anInt499 - cameraY,
							wallObj2.anInt501 - cameraZ, wallObj2.uid);
					} else if ((wallObj2.anInt502 & 0x300) != 0) {
						int deltaX = wallObj2.anInt500 - cameraX;
						int deltaY = wallObj2.anInt499 - cameraY;
						int deltaZ = wallObj2.anInt501 - cameraZ;
						int orientation = wallObj2.anInt503;

						int xSign = (orientation == 1 || orientation == 2) ? -deltaX : deltaX;
						int zSign = (orientation == 2 || orientation == 3) ? -deltaZ : deltaZ;

						if ((wallObj2.anInt502 & 0x100) != 0 && zSign >= xSign) {
							int renderX = deltaX + lookup463[orientation];
							int renderZ = deltaZ + lookup464[orientation];
							wallObj2.model.render(orientation * 512 + 256, sinVertical, cosVertical,
								sinHorizontal, cosHorizontal, renderX, deltaY, renderZ, wallObj2.uid);
						}
						if ((wallObj2.anInt502 & 0x200) != 0 && zSign <= xSign) {
							int renderX = deltaX + lookup465[orientation];
							int renderZ = deltaZ + lookup466[orientation];
							wallObj2.model.render(orientation * 512 + 1280 & 0x7ff, sinVertical,
								cosVertical, sinHorizontal, cosHorizontal, renderX, deltaY, renderZ, wallObj2.uid);
						}
					}
				}

				Wall wallObj1 = currentTile.obj1;
				if (wallObj1 != null) {
					if ((wallObj1.secondaryRotation & currentTile.anInt1328) != 0 &&
						isWallVisible(l, i, j, wallObj1.secondaryRotation)) {
						wallObj1.secondaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							wallObj1.anInt274 - cameraX, wallObj1.anInt273 - cameraY,
							wallObj1.anInt275 - cameraZ, wallObj1.uid);
					}
					if ((wallObj1.rotation & currentTile.anInt1328) != 0 &&
						isWallVisible(l, i, j, wallObj1.rotation)) {
						wallObj1.primaryModel.render(0, sinVertical, cosVertical, sinHorizontal, cosHorizontal,
							wallObj1.anInt274 - cameraX, wallObj1.anInt273 - cameraY,
							wallObj1.anInt275 - cameraZ, wallObj1.uid);
					}
				}
			}

			// Add neighboring levels and tiles to queue
			if (k < levelCount - 1) {
				Ground upperLevel = localGroundArray[k + 1][i][j];
				if (upperLevel != null && upperLevel.aBoolean1323) {
					tileQueue[queueTail] = upperLevel;
					queueTail = (queueTail + 1) % tileQueue.length;
				}
			}
			if (i < cameraTileX) {
				Ground neighbor = aclass30_sub3[i + 1][j];
				if (neighbor != null && neighbor.aBoolean1323) {
					tileQueue[queueTail] = neighbor;
					queueTail = (queueTail + 1) % tileQueue.length;
				}
			}
			if (j < cameraTileY) {
				Ground neighbor = aclass30_sub3[i][j + 1];
				if (neighbor != null && neighbor.aBoolean1323) {
					tileQueue[queueTail] = neighbor;
					queueTail = (queueTail + 1) % tileQueue.length;
				}
			}
			if (i > cameraTileX) {
				Ground neighbor = aclass30_sub3[i - 1][j];
				if (neighbor != null && neighbor.aBoolean1323) {
					tileQueue[queueTail] = neighbor;
					queueTail = (queueTail + 1) % tileQueue.length;
				}
			}
			if (j > cameraTileY) {
				Ground neighbor = aclass30_sub3[i][j - 1];
				if (neighbor != null && neighbor.aBoolean1323) {
					tileQueue[queueTail] = neighbor;
					queueTail = (queueTail + 1) % tileQueue.length;
				}
			}
		}
	}

	private void renderPlainTile(SimpleTile simpleTile, int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1;
		int i2 = l1 = (j1 << 7) - cameraX;
		int j2;
		int k2 = j2 = (k1 << 7) - cameraZ;
		int l2;
		int i3 = l2 = i2 + 128;
		int j3;
		int k3 = j3 = k2 + 128;
		int l3 = heightMap[i][j1][k1] - cameraY;
		int i4 = heightMap[i][j1 + 1][k1] - cameraY;
		int j4 = heightMap[i][j1 + 1][k1 + 1] - cameraY;
		int k4 = heightMap[i][j1][k1 + 1] - cameraY;

		// Transform first vertex
		int l4 = k2 * l + i2 * i1 >> 16;
		k2 = k2 * i1 - i2 * l >> 16;
		i2 = l4;
		l4 = l3 * k - k2 * j >> 16;
		k2 = l3 * j + k2 * k >> 16;
		l3 = l4;
		if (k2 < 50) return;

		// Transform second vertex
		l4 = j2 * l + i3 * i1 >> 16;
		j2 = j2 * i1 - i3 * l >> 16;
		i3 = l4;
		l4 = i4 * k - j2 * j >> 16;
		j2 = i4 * j + j2 * k >> 16;
		i4 = l4;
		if (j2 < 50) return;

		// Transform third vertex
		l4 = k3 * l + l2 * i1 >> 16;
		k3 = k3 * i1 - l2 * l >> 16;
		l2 = l4;
		l4 = j4 * k - k3 * j >> 16;
		k3 = j4 * j + k3 * k >> 16;
		j4 = l4;
		if (k3 < 50) return;

		// Transform fourth vertex
		l4 = j3 * l + l1 * i1 >> 16;
		j3 = j3 * i1 - l1 * l >> 16;
		l1 = l4;
		l4 = k4 * k - j3 * j >> 16;
		j3 = k4 * j + j3 * k >> 16;
		k4 = l4;
		if (j3 < 50) return;

		// Project to screen coordinates
		int i5 = Rasterizer.viewportCenterX + (i2 << 10) / k2;
		int j5 = Rasterizer.viewportCenterY + (l3 << 10) / k2;
		int k5 = Rasterizer.viewportCenterX + (i3 << 10) / j2;
		int l5 = Rasterizer.viewportCenterY + (i4 << 10) / j2;
		int i6 = Rasterizer.viewportCenterX + (l2 << 10) / k3;
		int j6 = Rasterizer.viewportCenterY + (j4 << 10) / k3;
		int k6 = Rasterizer.viewportCenterX + (l1 << 10) / j3;
		int l6 = Rasterizer.viewportCenterY + (k4 << 10) / j3;

		Rasterizer.alphaBlendValue = 0;

		// First triangle
		if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
			Rasterizer.enableClipping = i6 < 0 || k6 < 0 || k5 < 0 || i6 > DrawingArea.centerX || k6 > DrawingArea.centerX || k5 > DrawingArea.centerX;

			if (mouseSelection && isPointInTriangle(mouseX, mouseY, j6, l6, l5, i6, k6, k5)) {
				selectedTileX = j1;
				selectedTileY = k1;
			}

			if (simpleTile.anInt720 == -1 || simpleTile.anInt720 > 50) {
				if (simpleTile.anInt718 != 0xbc614e) {
					Rasterizer.renderTriangle(j6, l6, l5, i6, k6, k5, simpleTile.anInt718, simpleTile.anInt719, simpleTile.anInt717, k3, j3, j2);
				}
			} else if (!lowMemoryMode) {
				if (simpleTile.aBoolean721) {
					Rasterizer.renderTexturedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.anInt718, simpleTile.anInt719, simpleTile.anInt717,
						i2, i3, l1, l3, i4, k4, k2, j2, j3, simpleTile.anInt720, k3, j3, j2);
				} else {
					Rasterizer.renderTexturedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.anInt718, simpleTile.anInt719, simpleTile.anInt717,
						l2, l1, i3, j4, k4, i4, k3, j3, j2, simpleTile.anInt720, k3, j3, j2);
				}
			} else {
				int i7 = textureBrightness[simpleTile.anInt720];
				Rasterizer.renderTriangle(j6, l6, l5, i6, k6, k5, adjustBrightness(i7, simpleTile.anInt718),
					adjustBrightness(i7, simpleTile.anInt719), adjustBrightness(i7, simpleTile.anInt717), k3, j3, j2);
			}
		}

		// Second triangle
		if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
			Rasterizer.enableClipping = i5 < 0 || k5 < 0 || k6 < 0 || i5 > DrawingArea.centerX || k5 > DrawingArea.centerX || k6 > DrawingArea.centerX;

			if (mouseSelection && isPointInTriangle(mouseX, mouseY, j5, l5, l6, i5, k5, k6)) {
				selectedTileX = j1;
				selectedTileY = k1;
			}

			if (simpleTile.anInt720 == -1 || simpleTile.anInt720 > 50) {
				if (simpleTile.anInt716 != 0xbc614e) {
					Rasterizer.renderTriangle(j5, l5, l6, i5, k5, k6, simpleTile.anInt716, simpleTile.anInt717, simpleTile.anInt719, k2, j2, j3);
				}
			} else {
				if (!lowMemoryMode) {
					Rasterizer.renderTexturedTriangle(j5, l5, l6, i5, k5, k6, simpleTile.anInt716, simpleTile.anInt717, simpleTile.anInt719,
						i2, i3, l1, l3, i4, k4, k2, j2, j3, simpleTile.anInt720, k2, j2, j3);
					return;
				}
				int j7 = textureBrightness[simpleTile.anInt720];
				Rasterizer.renderTriangle(j5, l5, l6, i5, k5, k6, adjustBrightness(j7, simpleTile.anInt716),
					adjustBrightness(j7, simpleTile.anInt717), adjustBrightness(j7, simpleTile.anInt719), k2, j2, j3);
			}
		}
	}

	private void renderComplexTile(int i, int j, int k, FloorDecoration floorDecoration, int l, int i1, int j1) {
		int k1 = floorDecoration.anIntArray673.length;

		// Transform vertices - early exit if any vertex is behind near plane
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = floorDecoration.anIntArray673[l1] - cameraX;
			int k2 = floorDecoration.anIntArray674[l1] - cameraY;
			int i3 = floorDecoration.anIntArray675[l1] - cameraZ;
			int k3 = i3 * k + i2 * j1 >> 16;
			i3 = i3 * j1 - i2 * k >> 16;
			i2 = k3;
			k3 = k2 * l - i3 * j >> 16;
			i3 = k2 * j + i3 * l >> 16;
			k2 = k3;
			if (i3 < 50) {
				return;
			}
			FloorDecoration.anIntArray688[l1] = Rasterizer.viewportCenterX + (i2 << 10) / i3;
			FloorDecoration.anIntArray689[l1] = Rasterizer.viewportCenterY + (k2 << 10) / i3;
			FloorDecoration.depthPoint[l1] = i3;
		}

		Rasterizer.alphaBlendValue = 0;
		k1 = floorDecoration.anIntArray679.length;

		// Render triangles
		for (int j2 = 0; j2 < k1; j2++) {
			int l2 = floorDecoration.anIntArray679[j2];
			int j3 = floorDecoration.anIntArray680[j2];
			int l3 = floorDecoration.anIntArray681[j2];
			int i4 = FloorDecoration.anIntArray688[l2];
			int j4 = FloorDecoration.anIntArray688[j3];
			int k4 = FloorDecoration.anIntArray688[l3];
			int l4 = FloorDecoration.anIntArray689[l2];
			int i5 = FloorDecoration.anIntArray689[j3];
			int j5 = FloorDecoration.anIntArray689[l3];

			// Backface culling
			if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
				Rasterizer.enableClipping = i4 < 0 || j4 < 0 || k4 < 0 || i4 > DrawingArea.centerX
					|| j4 > DrawingArea.centerX || k4 > DrawingArea.centerX;

				if (mouseSelection && isPointInTriangle(mouseX, mouseY, l4, i5, j5, i4, j4, k4)) {
					selectedTileX = i;
					selectedTileY = i1;
				}

				// Texture/color rendering decision
				if (floorDecoration.anIntArray682 == null || floorDecoration.anIntArray682[j2] == -1 || floorDecoration.anIntArray682[j2] > 50) {
					if (floorDecoration.anIntArray676[j2] != 0xbc614e) {
						Rasterizer.renderTriangle(l4, i5, j5, i4, j4, k4, floorDecoration.anIntArray676[j2],
							floorDecoration.anIntArray677[j2], floorDecoration.anIntArray678[j2], FloorDecoration.depthPoint[l2],
							FloorDecoration.depthPoint[j3], FloorDecoration.depthPoint[l3]);
					}
				} else if (!lowMemoryMode) {
					if (floorDecoration.aBoolean683) {
						Rasterizer.renderTexturedTriangle(l4, i5, j5, i4, j4, k4, floorDecoration.anIntArray676[j2],
							floorDecoration.anIntArray677[j2], floorDecoration.anIntArray678[j2], FloorDecoration.anIntArray690[0],
							FloorDecoration.anIntArray690[1], FloorDecoration.anIntArray690[3], FloorDecoration.anIntArray691[0],
							FloorDecoration.anIntArray691[1], FloorDecoration.anIntArray691[3], FloorDecoration.anIntArray692[0],
							FloorDecoration.anIntArray692[1], FloorDecoration.anIntArray692[3], floorDecoration.anIntArray682[j2],
							FloorDecoration.depthPoint[l2], FloorDecoration.depthPoint[j3], FloorDecoration.depthPoint[l3]);
					} else {
						Rasterizer.renderTexturedTriangle(l4, i5, j5, i4, j4, k4, floorDecoration.anIntArray676[j2],
							floorDecoration.anIntArray677[j2], floorDecoration.anIntArray678[j2], FloorDecoration.anIntArray690[l2],
							FloorDecoration.anIntArray690[j3], FloorDecoration.anIntArray690[l3], FloorDecoration.anIntArray691[l2],
							FloorDecoration.anIntArray691[j3], FloorDecoration.anIntArray691[l3], FloorDecoration.anIntArray692[l2],
							FloorDecoration.anIntArray692[j3], FloorDecoration.anIntArray692[l3], floorDecoration.anIntArray682[j2],
							FloorDecoration.depthPoint[l2], FloorDecoration.depthPoint[j3], FloorDecoration.depthPoint[l3]);
					}
				} else {
					int k5 = textureBrightness[floorDecoration.anIntArray682[j2]];
					Rasterizer.renderTriangle(l4, i5, j5, i4, j4, k4, adjustBrightness(k5, floorDecoration.anIntArray676[j2]),
						adjustBrightness(k5, floorDecoration.anIntArray677[j2]), adjustBrightness(k5, floorDecoration.anIntArray678[j2]),
						FloorDecoration.depthPoint[l2], FloorDecoration.depthPoint[j3], FloorDecoration.depthPoint[l3]);
				}
			}
		}
	}

	private int adjustBrightness(int j, int k) {
		k = 127 - k;
		k = (k * (j & 0x7f)) / 160;
		if (k < 2)
			k = 2;
		else if (k > 126)
			k = 126;
		return (j & 0xff80) + k;
	}

	private boolean isPointInTriangle(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1)
			return false;
		if (j > k && j > l && j > i1)
			return false;
		if (i < j1 && i < k1 && i < l1)
			return false;
		if (i > j1 && i > k1 && i > l1)
			return false;
		int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
		int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
		int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
		return i2 * k2 > 0 && k2 * j2 > 0;
	}

	private void processOccluders() {
		int j = anIntArray473[currentPlane];
		Occluder[] aclass47 = aOccluderArrayArray474[currentPlane];
		occluderCount = 0;
		for (int k = 0; k < j; k++) {
			Occluder occluder = aclass47[k];
			if (occluder.occluderType == 1) {
				int l = (occluder.tileX - cameraTileX) + 25;
				if (l < 0 || l > 50)
					continue;
				int k1 = (occluder.tileY - cameraTileY) + 25;
				if (k1 < 0)
					k1 = 0;
				int j2 = (occluder.tileY2 - cameraTileY) + 25;
				if (j2 > 50)
					j2 = 50;
				boolean flag = false;
				while (k1 <= j2)
					if (aBooleanArrayArray492[l][k1++]) {
						flag = true;
						break;
					}
				if (!flag)
					continue;
				int j3 = cameraX - occluder.worldX;
				if (j3 > 32) {
					occluder.clipType = 1;
				} else {
					if (j3 >= -32)
						continue;
					occluder.clipType = 2;
					j3 = -j3;
				}
				occluder.deltaZ = (occluder.worldZ - cameraZ << 8) / j3;
				occluder.deltaZ2 = (occluder.worldZ2 - cameraZ << 8) / j3;
				occluder.deltaY = (occluder.worldY - cameraY << 8) / j3;
				occluder.deltaY2 = (occluder.worldY2 - cameraY << 8) / j3;
				A_OCCLUDER_ARRAY_476[occluderCount++] = occluder;
				continue;
			}
			if (occluder.occluderType == 2) {
				int i1 = (occluder.tileY - cameraTileY) + 25;
				if (i1 < 0 || i1 > 50)
					continue;
				int l1 = (occluder.tileX - cameraTileX) + 25;
				if (l1 < 0)
					l1 = 0;
				int k2 = (occluder.tileX2 - cameraTileX) + 25;
				if (k2 > 50)
					k2 = 50;
				boolean flag1 = false;
				while (l1 <= k2)
					if (aBooleanArrayArray492[l1++][i1]) {
						flag1 = true;
						break;
					}
				if (!flag1)
					continue;
				int k3 = cameraZ - occluder.worldZ;
				if (k3 > 32) {
					occluder.clipType = 3;
				} else {
					if (k3 >= -32)
						continue;
					occluder.clipType = 4;
					k3 = -k3;
				}
				occluder.deltaX = (occluder.worldX - cameraX << 8) / k3;
				occluder.deltaX2 = (occluder.worldX2 - cameraX << 8) / k3;
				occluder.deltaY = (occluder.worldY - cameraY << 8) / k3;
				occluder.deltaY2 = (occluder.worldY2 - cameraY << 8) / k3;
				A_OCCLUDER_ARRAY_476[occluderCount++] = occluder;
			} else if (occluder.occluderType == 4) {
				int j1 = occluder.worldY - cameraY;
				if (j1 > 128) {
					int i2 = (occluder.tileY - cameraTileY) + 25;
					if (i2 < 0)
						i2 = 0;
					int l2 = (occluder.tileY2 - cameraTileY) + 25;
					if (l2 > 50)
						l2 = 50;
					if (i2 <= l2) {
						int i3 = (occluder.tileX - cameraTileX) + 25;
						if (i3 < 0)
							i3 = 0;
						int l3 = (occluder.tileX2 - cameraTileX) + 25;
						if (l3 > 50)
							l3 = 50;
						boolean flag2 = false;
						label0: for (int i4 = i3; i4 <= l3; i4++) {
							for (int j4 = i2; j4 <= l2; j4++) {
								if (!aBooleanArrayArray492[i4][j4])
									continue;
								flag2 = true;
								break label0;
							}

						}

						if (flag2) {
							occluder.clipType = 5;
							occluder.deltaX = (occluder.worldX - cameraX << 8) / j1;
							occluder.deltaX2 = (occluder.worldX2 - cameraX << 8) / j1;
							occluder.deltaZ = (occluder.worldZ - cameraZ << 8) / j1;
							occluder.deltaZ2 = (occluder.worldZ2 - cameraZ << 8) / j1;
							A_OCCLUDER_ARRAY_476[occluderCount++] = occluder;
						}
					}
				}
			}
		}

	}

	private boolean isTileVisible(int i, int j, int k) {
		int l = visibilityCache[i][j][k];
		if (l == -renderCycle)
			return true;
		if (l == renderCycle)
			return false;
		int i1 = j << 7;
		int j1 = k << 7;
		if (isOccluded(i1 + 1, heightMap[i][j][k], j1 + 1)
				&& isOccluded((i1 + 128) - 1, heightMap[i][j + 1][k], j1 + 1)
				&& isOccluded((i1 + 128) - 1, heightMap[i][j + 1][k + 1], (j1 + 128) - 1)
				&& isOccluded(i1 + 1, heightMap[i][j][k + 1], (j1 + 128) - 1)) {
			visibilityCache[i][j][k] = renderCycle;
			return false;
		} else {
			visibilityCache[i][j][k] = -renderCycle;
			return true;
		}
	}

	private boolean isWallVisible(int i, int j, int k, int l) {
		if (isTileVisible(i, j, k))
			return true;
		int i1 = j << 7;
		int j1 = k << 7;
		int k1 = heightMap[i][j][k] - 1;
		int l1 = k1 - 120;
		int i2 = k1 - 230;
		int j2 = k1 - 238;
		if (l < 16) {
			if (l == 1) {
				if (i1 > cameraX) {
					if (!isOccluded(i1, k1, j1))
						return true;
					if (!isOccluded(i1, k1, j1 + 128))
						return true;
				}
				if (i > 0) {
					if (!isOccluded(i1, l1, j1))
						return true;
					if (!isOccluded(i1, l1, j1 + 128))
						return true;
				}
				return !isOccluded(i1, i2, j1) || !isOccluded(i1, i2, j1 + 128);
			}
			if (l == 2) {
				if (j1 < cameraZ) {
					if (!isOccluded(i1, k1, j1 + 128))
						return true;
					if (!isOccluded(i1 + 128, k1, j1 + 128))
						return true;
				}
				if (i > 0) {
					if (!isOccluded(i1, l1, j1 + 128))
						return true;
					if (!isOccluded(i1 + 128, l1, j1 + 128))
						return true;
				}
				return !isOccluded(i1, i2, j1 + 128) || !isOccluded(i1 + 128, i2, j1 + 128);
			}
			if (l == 4) {
				if (i1 < cameraX) {
					if (!isOccluded(i1 + 128, k1, j1))
						return true;
					if (!isOccluded(i1 + 128, k1, j1 + 128))
						return true;
				}
				if (i > 0) {
					if (!isOccluded(i1 + 128, l1, j1))
						return true;
					if (!isOccluded(i1 + 128, l1, j1 + 128))
						return true;
				}
				return !isOccluded(i1 + 128, i2, j1) || !isOccluded(i1 + 128, i2, j1 + 128);
			}
			if (l == 8) {
				if (j1 > cameraZ) {
					if (!isOccluded(i1, k1, j1))
						return true;
					if (!isOccluded(i1 + 128, k1, j1))
						return true;
				}
				if (i > 0) {
					if (!isOccluded(i1, l1, j1))
						return true;
					if (!isOccluded(i1 + 128, l1, j1))
						return true;
				}
				return !isOccluded(i1, i2, j1) || !isOccluded(i1 + 128, i2, j1);
			}
		}
		if (!isOccluded(i1 + 64, j2, j1 + 64))
			return true;
		if (l == 16)
			return !isOccluded(i1, i2, j1 + 128);
		if (l == 32)
			return !isOccluded(i1 + 128, i2, j1 + 128);
		if (l == 64)
			return !isOccluded(i1 + 128, i2, j1);
		if (l == 128) {
			return !isOccluded(i1, i2, j1);
		} else {
			System.out.println("Warning unsupported wall type");
			return false;
		}
	}

	private boolean isDecorationVisible(int plane, int tileX, int tileY, int modelHeight) {
		if (isTileVisible(plane, tileX, tileY))
			return true;                                        // already unclipped

		final int worldX = tileX << 7;
		final int worldY = tileY << 7;

    /* bring the sample heights on the *target* plane down into the same
       reference frame as the occluder on plane-0 by cancelling the vertical
       128-unit offset for every plane above 0                                */
		final int planeOffset = plane * EngineConfig.PLANE_HEIGHT;

		/* four corners of the tile (00, 10, 11, 01) */
		int h00 = heightMap[plane][tileX    ][tileY    ] - planeOffset - modelHeight;
		int h10 = heightMap[plane][tileX + 1][tileY    ] - planeOffset - modelHeight;
		int h11 = heightMap[plane][tileX + 1][tileY + 1] - planeOffset - modelHeight;
		int h01 = heightMap[plane][tileX    ][tileY + 1] - planeOffset - modelHeight;

		/* if *all four* points fall inside the same occluder → tile is hidden   */
		return !(  isOccluded(worldX + 1,          h00, worldY + 1)
			|| isOccluded(worldX + 127,        h10, worldY + 1)
			|| isOccluded(worldX + 127,        h11, worldY + 127)
			|| isOccluded(worldX + 1,          h01, worldY + 127) );
	}

	private boolean isObjectVisible(int i, int j, int k, int l, int i1, int j1) {
		if (j == k && l == i1) {
			if (isTileVisible(i, j, l))
				return false;
			int k1 = j << 7;
			int i2 = l << 7;
			return isOccluded(k1 + 1, heightMap[i][j][l] - j1, i2 + 1)
					&& isOccluded((k1 + 128) - 1, heightMap[i][j + 1][l] - j1, i2 + 1)
					&& isOccluded((k1 + 128) - 1, heightMap[i][j + 1][l + 1] - j1, (i2 + 128) - 1)
					&& isOccluded(k1 + 1, heightMap[i][j][l + 1] - j1, (i2 + 128) - 1);
		}
		for (int l1 = j; l1 <= k; l1++) {
			for (int j2 = l; j2 <= i1; j2++)
				if (visibilityCache[i][l1][j2] == -renderCycle)
					return false;

		}

		int k2 = (j << 7) + 1;
		int l2 = (l << 7) + 2;
		int i3 = heightMap[i][j][l] - j1;
		if (!isOccluded(k2, i3, l2))
			return false;
		int j3 = (k << 7) - 1;
		if (!isOccluded(j3, i3, l2))
			return false;
		int k3 = (i1 << 7) - 1;
		return isOccluded(k2, i3, k3) && isOccluded(j3, i3, k3);
	}

	private boolean isOccluded(int i, int j, int k) {
		for (int l = 0; l < occluderCount; l++) {
			Occluder occluder = A_OCCLUDER_ARRAY_476[l];
			int distance;

			switch (occluder.clipType) {
				case 1:
					distance = occluder.worldX - i;
					if (distance > 0) {
						int j2 = occluder.worldZ + (occluder.deltaZ * distance >> 8);
						int k3 = occluder.worldZ2 + (occluder.deltaZ2 * distance >> 8);
						int l4 = occluder.worldY + (occluder.deltaY * distance >> 8);
						int i6 = occluder.worldY2 + (occluder.deltaY2 * distance >> 8);
						if (k >= j2 && k <= k3 && j >= l4 && j <= i6)
							return true;
					}
					break;

				case 2:
					distance = i - occluder.worldX;
					if (distance > 0) {
						int k2 = occluder.worldZ + (occluder.deltaZ * distance >> 8);
						int l3 = occluder.worldZ2 + (occluder.deltaZ2 * distance >> 8);
						int i5 = occluder.worldY + (occluder.deltaY * distance >> 8);
						int j6 = occluder.worldY2 + (occluder.deltaY2 * distance >> 8);
						if (k >= k2 && k <= l3 && j >= i5 && j <= j6)
							return true;
					}
					break;

				case 3:
					distance = occluder.worldZ - k;
					if (distance > 0) {
						int l2 = occluder.worldX + (occluder.deltaX * distance >> 8);
						int i4 = occluder.worldX2 + (occluder.deltaX2 * distance >> 8);
						int j5 = occluder.worldY + (occluder.deltaY * distance >> 8);
						int k6 = occluder.worldY2 + (occluder.deltaY2 * distance >> 8);
						if (i >= l2 && i <= i4 && j >= j5 && j <= k6)
							return true;
					}
					break;

				case 4:
					distance = k - occluder.worldZ;
					if (distance > 0) {
						int i3 = occluder.worldX + (occluder.deltaX * distance >> 8);
						int j4 = occluder.worldX2 + (occluder.deltaX2 * distance >> 8);
						int k5 = occluder.worldY + (occluder.deltaY * distance >> 8);
						int l6 = occluder.worldY2 + (occluder.deltaY2 * distance >> 8);
						if (i >= i3 && i <= j4 && j >= k5 && j <= l6)
							return true;
					}
					break;

				case 5:
					distance = j - occluder.worldY;
					if (distance > 0) {
						int j3 = occluder.worldX + (occluder.deltaX * distance >> 8);
						int k4 = occluder.worldX2 + (occluder.deltaX2 * distance >> 8);
						int l5 = occluder.worldZ + (occluder.deltaZ * distance >> 8);
						int i7 = occluder.worldZ2 + (occluder.deltaZ2 * distance >> 8);
						if (i >= j3 && i <= k4 && k >= l5 && k <= i7)
							return true;
					}
					break;
			}
		}
		return false;
	}
}
