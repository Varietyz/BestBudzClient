package com.bestbudz.engine.core.gamerender;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.network.CacheManager;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.InteractiveObject;
import com.bestbudz.rendering.OverlayFloor;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.world.CollisionMap;
import com.bestbudz.world.CoordinateTransform;
import com.bestbudz.world.Floor;
import com.bestbudz.world.ObjectDef;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;

public final class ObjectManager {

	private static final int[] directionDeltaX = { 1, 0, -1, 0 };
	private static final int[] wallDirections = { 16, 32, 64, 128 };
	private static final int[] directionDeltaY = { 0, -1, 0, 1 };
	private static final int[] wallFlags = { 1, 2, 4, 8 };
	public static int baseLevel;
	public static int minPlane = 99;
	public static boolean lowMem = true;
	public final ArrayList<Integer> colors = new ArrayList<>();
	private final int[] redAccumulator;
	private final int[] greenAccumulator;
	private final int[] blueAccumulator;
	private final int[] saturationAccumulator;
	private final int[] colorCount;
	private final int[][][] heightMap;
	private final byte[][][] overlayIds;
	private final byte[][][] shadowMap;
	private final int[][][] occlusionFlags;
	private final byte[][][] overlayRotations;
	private final int[][] lightMap;
	private final byte[][][] underlay;
	private final int mapWidth;
	private final int mapHeight;
	private final byte[][][] overlayShapes;
	private final byte[][][] tileFlags;
	private static final int SAFE_BOUNDS_BUFFER = 1;
	private static final boolean ENABLE_BOUNDS_LOGGING = true;

	public ObjectManager(byte[][][] abyte0, int[][][] ai) {
		minPlane = 99;
		mapWidth = 104;
		mapHeight = 104;
		heightMap = ai;
		tileFlags = abyte0;
		underlay = new byte[4][mapWidth][mapHeight];
		overlayIds = new byte[4][mapWidth][mapHeight];
		overlayRotations = new byte[4][mapWidth][mapHeight];
		overlayShapes = new byte[4][mapWidth][mapHeight];
		occlusionFlags = new int[4][mapWidth + 1][mapHeight + 1];
		shadowMap = new byte[4][mapWidth + 1][mapHeight + 1];
		lightMap = new int[mapWidth + 1][mapHeight + 1];
		redAccumulator = new int[mapHeight];
		greenAccumulator = new int[mapHeight];
		blueAccumulator = new int[mapHeight];
		saturationAccumulator = new int[mapHeight];
		colorCount = new int[mapHeight];
	}

	private static int generateNoise(int i, int j) {
		int k = i + j * 57;
		k = k << 13 ^ k;
		int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return l >> 19 & 0xff;
	}

	private static int calculateTerrainHeight(int i, int j) {
		int k = (interpolateNoise(i + 45365, j + 0x16713, 4) - 128) + (interpolateNoise(i + 10294, j + 37821, 2) - 128 >> 1) + (interpolateNoise(i, j, 1) - 128 >> 2);
		k = (int) ((double) k * 0.29999999999999999D) + 35;
		if (k < 10)
			k = 10;
		else if (k > 60)
			k = 60;
		return k;
	}

	public static void loadObjectRequirements(Stream stream, CacheManager class42_sub1) {
		label0: {
			int i = -1;
			do {
				int j = stream.readLargeSmart();
				if (j == 0)
					break label0;
				i += j;
				ObjectDef objectDef = ObjectDef.forID(i);
				objectDef.loadRequiredModels(class42_sub1);
				do {
					int k = stream.readSmartUnsigned();
					if (k == 0)
						break;
					stream.readUnsignedByte();
				} while (true);
			} while (true);
		}
	}

	private static int interpolateNoise(int i, int j, int k) {
		int l = i / k;
		int i1 = i & k - 1;
		int j1 = j / k;
		int k1 = j & k - 1;
		int l1 = smoothNoise(l, j1);
		int i2 = smoothNoise(l + 1, j1);
		int j2 = smoothNoise(l, j1 + 1);
		int k2 = smoothNoise(l + 1, j1 + 1);
		int l2 = interpolateValue(l1, i2, i1, k);
		int i3 = interpolateValue(j2, k2, i1, k);
		return interpolateValue(l2, i3, k1, k);
	}

	public static boolean isValidObject(int i, int j) {
		ObjectDef objectDef = ObjectDef.forID(i);
		if (j == 11)
			j = 10;
		if (j >= 5 && j <= 8)
			j = 4;
		return objectDef.hasType(j);
	}

	private static int interpolateValue(int i, int j, int k, int l) {
		int i1 = 0x10000 - Rasterizer.cosTable[(k * 1024) / l] >> 1;
		return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
	}

	private static int smoothNoise(int i, int j) {
		int k = generateNoise(i - 1, j - 1) + generateNoise(i + 1, j - 1) + generateNoise(i - 1, j + 1) + generateNoise(i + 1, j + 1);
		int l = generateNoise(i - 1, j) + generateNoise(i + 1, j) + generateNoise(i, j - 1) + generateNoise(i, j + 1);
		int i1 = generateNoise(i, j);
		return k / 16 + l / 8 + i1 / 4;
	}

	private static int adjustBrightness(int i, int j) {
		if (i == -1)
			return 0xbc614e;
		j = (j * (i & 0x7f)) / 128;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	private static boolean isWithinBounds(int[][][] array, int plane, int x, int y, String methodName) {
		if (array == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": array is null");
			}
			return false;
		}

		if (plane < 0 || plane >= array.length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": plane " + plane + " out of bounds [0," + (array.length-1) + "]");
			}
			return false;
		}

		if (array[plane] == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": array[" + plane + "] is null");
			}
			return false;
		}

		if (x < 0 || x >= array[plane].length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": x " + x + " out of bounds [0," + (array[plane].length-1) + "]");
			}
			return false;
		}

		if (array[plane][x] == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": array[" + plane + "][" + x + "] is null");
			}
			return false;
		}

		if (y < 0 || y >= array[plane][x].length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": y " + y + " out of bounds [0," + (array[plane][x].length-1) + "]");
			}
			return false;
		}

		return true;
	}

	private static boolean isWithinBounds(byte[][][] array, int plane, int x, int y, String methodName) {
		if (array == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": byte array is null");
			}
			return false;
		}

		if (plane < 0 || plane >= array.length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": plane " + plane + " out of bounds [0," + (array.length-1) + "]");
			}
			return false;
		}

		if (array[plane] == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": byte array[" + plane + "] is null");
			}
			return false;
		}

		if (x < 0 || x >= array[plane].length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": x " + x + " out of bounds [0," + (array[plane].length-1) + "]");
			}
			return false;
		}

		if (array[plane][x] == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": byte array[" + plane + "][" + x + "] is null");
			}
			return false;
		}

		if (y < 0 || y >= array[plane][x].length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": y " + y + " out of bounds [0," + (array[plane][x].length-1) + "]");
			}
			return false;
		}

		return true;
	}

	public static void placeWorldObject(WorldController worldController, int i, int j, int k, int l, CollisionMap collisionMap,
										int[][][] ai, int i1, int j1, int k1) {

		final String methodName = "placeWorldObject";

		if (ai == null) {
			System.err.println("[ERROR] " + methodName + ": height array (ai) is null");
			return;
		}

		if (i1 < 0 || j < 0 || l < 0 || l >= ai.length) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": Invalid coordinates - i1:" + i1 + ", j:" + j + ", l:" + l + ", ai.length:" + ai.length);
			}
			return;
		}

		if (!isWithinBounds(ai, l, i1, j, methodName) ||
			!isWithinBounds(ai, l, i1 + 1, j, methodName) ||
			!isWithinBounds(ai, l, i1 + 1, j + 1, methodName) ||
			!isWithinBounds(ai, l, i1, j + 1, methodName)) {

			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[BOUNDS] " + methodName + ": Height array access would be out of bounds");
				System.err.println("  Coordinates: i1=" + i1 + ", j=" + j + ", l=" + l);
				System.err.println("  Required access: [" + l + "][" + i1 + "-" + (i1+1) + "][" + j + "-" + (j+1) + "]");
				if (ai[l] != null) {
					System.err.println("  Array dimensions: [" + ai.length + "][" + ai[l].length + "][" +
						(ai[l].length > 0 && ai[l][0] != null ? ai[l][0].length : "null") + "]");
				}
			}
			return;
		}

		final int l1 = ai[l][i1][j];
		final int i2 = ai[l][i1 + 1][j];
		final int j2 = ai[l][i1 + 1][j + 1];
		final int k2 = ai[l][i1][j + 1];
		final int l2 = (l1 + i2 + j2 + k2) >> 2;

		final ObjectDef objectDef = ObjectDef.forID(j1);
		if (objectDef == null) {
			if (ENABLE_BOUNDS_LOGGING) {
				System.err.println("[ERROR] " + methodName + ": ObjectDef.forID(" + j1 + ") returned null");
			}
			return;
		}

		final int i3 = calculateI3(i1, j, j1, objectDef.hasActions);
		final byte byte1 = (byte)((i << 6) + k);

		try {
			switch (k) {
				case 0:
				case 1:
				case 3:
					handleWallObjects(worldController, collisionMap, i, j, k, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 2:
					handleCornerWall(worldController, collisionMap, i, j, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 4:
				case 6:
				case 7:
				case 8:
					handleDiagonalWalls(worldController, i, j, k, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 5:
					handleSlopedWall(worldController, i, j, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 9:
					handleDecorativeWall(worldController, collisionMap, i, j, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 10:
				case 11:
					handleFloorDecorations(worldController, collisionMap, i, j, k, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				case 22:
					handleGroundObject(worldController, collisionMap, i, j, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					break;

				default:
					if (k >= 12) {
						handleLargeObjects(worldController, collisionMap, i, j, k, l2, objectDef, i3, byte1, i1, j1, l1, i2, j2, k2, k1);
					}
					break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("[CRITICAL] " + methodName + ": Bounds error in handler for k=" + k);
			System.err.println("  Parameters: i=" + i + ", j=" + j + ", k=" + k + ", l=" + l + ", i1=" + i1 + ", j1=" + j1 + ", k1=" + k1);
			System.err.println("  Heights: l1=" + l1 + ", i2=" + i2 + ", j2=" + j2 + ", k2=" + k2 + ", l2=" + l2);
			throw e;
		}
	}

	private static int calculateI3(int i1, int j, int j1, boolean hasActions) {
		int i3 = i1 + (j << 7) + (j1 << 14) + 0x40000000;
		if (!hasActions) {
			i3 += 0x80000000;
		}
		return i3;
	}

	private static Animable createAnimable(ObjectDef objectDef, int j1, int i, int k, int l1, int i2, int j2, int k2) {
		if (objectDef.animationId == -1 && objectDef.childIds == null) {
			return objectDef.getModel(k, i, l1, i2, j2, k2, -1);
		} else {
			return new InteractiveObject(j1, i, k, i2, j2, l1, k2, objectDef.animationId, true);
		}
	}

	private static void handleWallObjects(WorldController worldController, CollisionMap collisionMap, int i, int j, int k,
										  int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
										  int l1, int i2, int j2, int k2, int k1) {
		final Animable obj = createAnimable(objectDef, j1, i, k, l1, i2, j2, k2);
		final int direction = (k == 0 || k == 3) ? wallFlags[i] : wallDirections[i];

		worldController.addWall(direction, obj, i3, j, byte1, i1, null, l2, 0, k1);

		if (objectDef.clipFlag) {
			collisionMap.addWall(j, i, i1, k, objectDef.blocksProjectiles);
		}
	}

	private static void handleCornerWall(WorldController worldController, CollisionMap collisionMap, int i, int j,
										 int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
										 int l1, int i2, int j2, int k2, int k1) {
		final int j3 = (i + 1) & 3;
		final Animable obj11 = createAnimable(objectDef, j1, 4 + i, 2, l1, i2, j2, k2);
		final Animable obj12 = createAnimable(objectDef, j1, j3, 2, l1, i2, j2, k2);

		worldController.addWall(wallFlags[i], obj11, i3, j, byte1, i1, obj12, l2, wallFlags[j3], k1);

		if (objectDef.clipFlag) {
			collisionMap.addWall(j, i, i1, 2, objectDef.blocksProjectiles);
		}
	}

	private static void handleDiagonalWalls(WorldController worldController, int i, int j, int k,
											int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
											int l1, int i2, int j2, int k2, int k1) {

		final int[] heights = objectDef.rotateHeights ?
			transformHeights(new int[]{l1, i2, j2, k2}, i) :
			new int[]{l1, i2, j2, k2};

		final Animable obj = createAnimable(objectDef, j1, 0, 4, heights[0], heights[1], heights[2], heights[3]);

		final int rotation, offsetX, offsetZ, direction;
		switch (k) {
			case 4:
				rotation = i * 512;
				offsetX = offsetZ = 0;
				direction = wallFlags[i];
				break;
			case 6:
				rotation = i;
				offsetX = offsetZ = 0;
				direction = 256;
				break;
			case 7:
				rotation = i;
				offsetX = offsetZ = 0;
				direction = 512;
				break;
			case 8:
				rotation = i;
				offsetX = offsetZ = 0;
				direction = 768;
				break;
			default:
				throw new IllegalArgumentException("Invalid k value for diagonal wall: " + k);
		}

		worldController.addWallDecoration(i3, j, rotation, k1, offsetX, l2, obj, i1, byte1, offsetZ, direction);
	}

	private static void handleSlopedWall(WorldController worldController, int i, int j,
										 int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
										 int l1, int i2, int j2, int k2, int k1) {
		final int j4 = 16;
		final int l4 = worldController.getWallId(k1, i1, j);
		final int actualJ4 = (l4 > 0) ? ObjectDef.forID((l4 >> 14) & 0x7fff).wallThickness : j4;

		final Animable obj = createAnimable(objectDef, j1, 0, 4, l1, i2, j2, k2);

		worldController.addWallDecoration(i3, j, i * 512, k1, directionDeltaX[i] * actualJ4, l2, obj, i1, byte1,
			directionDeltaY[i] * actualJ4, wallFlags[i]);
	}

	private static void handleDecorativeWall(WorldController worldController, CollisionMap collisionMap, int i, int j,
											 int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
											 int l1, int i2, int j2, int k2, int k1) {
		final Animable obj = createAnimable(objectDef, j1, i, 9, l1, i2, j2, k2);

		worldController.canPlaceObject(i3, byte1, l2, 1, obj, 1, k1, 0, j, i1);

		if (objectDef.clipFlag) {
			collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, i1, j, i);
		}
	}

	private static void handleFloorDecorations(WorldController worldController, CollisionMap collisionMap, int i, int j, int k,
											   int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
											   int l1, int i2, int j2, int k2, int k1) {
		final Animable obj = createAnimable(objectDef, j1, i, 10, l1, i2, j2, k2);

		if (obj != null) {
			final int j5 = (k == 11) ? 256 : 0;
			final boolean isRotated = (i == 1 || i == 3);
			final int k4 = isRotated ? objectDef.sizeY : objectDef.sizeX;
			final int i5 = isRotated ? objectDef.sizeX : objectDef.sizeY;

			worldController.canPlaceObject(i3, byte1, l2, i5, obj, k4, k1, j5, j, i1);
		}

		if (objectDef.clipFlag) {
			collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, i1, j, i);
		}
	}

	private static void handleGroundObject(WorldController worldController, CollisionMap collisionMap, int i, int j,
										   int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
										   int l1, int i2, int j2, int k2, int k1) {
		final Animable obj = createAnimable(objectDef, j1, i, 22, l1, i2, j2, k2);

		worldController.addGroundDecoration(k1, l2, j, obj, byte1, i3, i1);

		if (objectDef.clipFlag && objectDef.hasActions) {
			collisionMap.addFloorDecoration(j, i1);
		}
	}

	private static void handleLargeObjects(WorldController worldController, CollisionMap collisionMap, int i, int j, int k,
										   int l2, ObjectDef objectDef, int i3, byte byte1, int i1, int j1,
										   int l1, int i2, int j2, int k2, int k1) {
		final Animable obj = createAnimable(objectDef, j1, i, k, l1, i2, j2, k2);

		worldController.canPlaceObject(i3, byte1, l2, 1, obj, 1, k1, 0, j, i1);

		if (objectDef.clipFlag) {
			collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, i1, j, i);
		}
	}

	private static int[] transformHeights(int[] heights, int i) {
		final int[] result = heights.clone();

		switch (i) {
			case 1:

				final int temp1 = result[3];
				result[3] = result[2];
				result[2] = result[1];
				result[1] = result[0];
				result[0] = temp1;
				break;

			case 2:

				final int temp2a = result[3];
				result[3] = result[1];
				result[1] = temp2a;
				final int temp2b = result[2];
				result[2] = result[0];
				result[0] = temp2b;
				break;

			case 3:

				final int temp3 = result[3];
				result[3] = result[0];
				result[0] = result[1];
				result[1] = result[2];
				result[2] = temp3;
				break;
		}

		return result;
	}

	public static boolean validateObjectRequirements(int i, byte[] is, int i_250_)
	{
		boolean bool = true;
		Stream stream = new Stream(is);
		int i_252_ = -1;
		for (; ; )
		{
			int i_253_ = stream.readSmartUnsigned();
			if (i_253_ == 0)
				break;
			i_252_ += i_253_;
			int i_254_ = 0;
			boolean bool_255_ = false;
			for (; ; )
			{
				if (bool_255_)
				{
					int i_256_ = stream.readSmartUnsigned();
					if (i_256_ == 0)
						break;
					stream.readUnsignedByte();
				}
				else
				{
					int i_257_ = stream.readSmartUnsigned();
					if (i_257_ == 0)
						break;
					i_254_ += i_257_ - 1;
					int i_258_ = i_254_ & 0x3f;
					int i_259_ = i_254_ >> 6 & 0x3f;
					int i_260_ = stream.readUnsignedByte() >> 2;
					int i_261_ = i_259_ + i;
					int i_262_ = i_258_ + i_250_;
					if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103)
					{
						ObjectDef objectDef = ObjectDef.forID(i_252_);
						if (i_260_ != 22 || SettingsConfig.enableGroundDecorations || objectDef.hasActions || objectDef.aBoolean736) {
							bool &= objectDef.isModelLoaded();
							bool_255_ = true;
						}
					}
				}
			}
		}
		return bool;
	}

	public void renderScene(CollisionMap[] aclass11, WorldController worldController) {

		for (int j = 0; j < 4; j++) {

			if (tileFlags == null || j >= tileFlags.length ||
				tileFlags[j] == null) {
				continue;
			}

			for (int k = 0; k < 104; k++) {

				if (k >= tileFlags[j].length || tileFlags[j][k] == null) {
					continue;
				}

				for (int i1 = 0; i1 < 104; i1++) {

					if (i1 >= tileFlags[j][k].length) {
						continue;
					}

					try {
						if ((tileFlags[j][k][i1] & 1) == 1) {
							int k1 = j;

							if (j >= 1 && tileFlags.length > 1 &&
								tileFlags[1] != null &&
								k < tileFlags[1].length &&
								tileFlags[1][k] != null &&
								i1 < tileFlags[1][k].length &&
								(tileFlags[1][k][i1] & 2) == 2) {
								k1--;
							}

							if (k1 >= 0 && k1 < aclass11.length && aclass11[k1] != null) {
								aclass11[k1].addFloorDecoration(i1, k);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("[ERROR] renderScene Phase1: Bounds error at j=" + j + ", k=" + k + ", i1=" + i1);
						continue;
					}
				}
			}
		}

		for (int l = 0; l < 4; l++) {

			if (shadowMap == null || l >= shadowMap.length ||
				shadowMap[l] == null) {
				System.err.println("[ERROR] renderScene: aByteArrayArrayArray134[" + l + "] is null");
				continue;
			}

			if (heightMap == null || l >= heightMap.length ||
				heightMap[l] == null) {
				System.err.println("[ERROR] renderScene: anIntArrayArrayArray129[" + l + "] is null");
				continue;
			}

			byte[][] shadowIntensity = shadowMap[l];
			byte directional_light_initial_intensity = 96;
			char specular_distribution_factor = '̀';
			byte directional_light_x = -50;
			byte directional_light_z = -10;
			byte directional_light_y = -50;
			int directional_light_length = (int) Math.sqrt(directional_light_x * directional_light_x +
				directional_light_z * directional_light_z +
				directional_light_y * directional_light_y);
			int specular_distribution = specular_distribution_factor * directional_light_length >> 8;

			int maxY = Math.min(mapHeight - 1, heightMap[l].length - 1);
			int maxX = Math.min(mapWidth - 1,
				heightMap[l].length > 0 && heightMap[l][0] != null ?
					heightMap[l][0].length - 1 : 0);

			for (int y = 1; y < maxY; y++) {
				if (heightMap[l][y] == null) continue;

				for (int x = 1; x < maxX; x++) {
					try {

						int x_height_difference = 0;
						int y_height_difference = 0;

						if (x + 1 < heightMap[l].length &&
							heightMap[l][x + 1] != null &&
							y < heightMap[l][x + 1].length &&
							x - 1 >= 0 && heightMap[l][x - 1] != null &&
							y < heightMap[l][x - 1].length) {
							x_height_difference = heightMap[l][x + 1][y] - heightMap[l][x - 1][y];
						}

						if (heightMap[l][x] != null &&
							y + 1 < heightMap[l][x].length &&
							y - 1 >= 0) {
							y_height_difference = heightMap[l][x][y + 1] - heightMap[l][x][y - 1];
						}

						int normal_length = (int) Math.sqrt(x_height_difference * x_height_difference +
							0x10000 + y_height_difference * y_height_difference);

						if (normal_length == 0) normal_length = 1;

						int normalized_normal_x = (x_height_difference << 8) / normal_length;
						int normalized_normal_z = 0x10000 / normal_length;
						int normalized_normal_y = (y_height_difference << 8) / normal_length;

						int directional_light_intensity = directional_light_initial_intensity +
							(directional_light_x * normalized_normal_x + directional_light_z * normalized_normal_z +
								directional_light_y * normalized_normal_y) /
								(specular_distribution == 0 ? 1 : specular_distribution);

						int weighted_shadow_intensity = 0;
						if (shadowIntensity != null) {
							if (x - 1 >= 0 && x - 1 < shadowIntensity.length &&
								shadowIntensity[x - 1] != null && y < shadowIntensity[x - 1].length) {
								weighted_shadow_intensity += (shadowIntensity[x - 1][y] >> 2);
							}
							if (x + 1 < shadowIntensity.length &&
								shadowIntensity[x + 1] != null && y < shadowIntensity[x + 1].length) {
								weighted_shadow_intensity += (shadowIntensity[x + 1][y] >> 3);
							}
							if (x < shadowIntensity.length && shadowIntensity[x] != null) {
								if (y - 1 >= 0 && y - 1 < shadowIntensity[x].length) {
									weighted_shadow_intensity += (shadowIntensity[x][y - 1] >> 2);
								}
								if (y + 1 < shadowIntensity[x].length) {
									weighted_shadow_intensity += (shadowIntensity[x][y + 1] >> 3);
								}
								if (y < shadowIntensity[x].length) {
									weighted_shadow_intensity += (shadowIntensity[x][y] >> 1);
								}
							}
						}

						if (lightMap != null && x < lightMap.length &&
							lightMap[x] != null && y < lightMap[x].length) {
							lightMap[x][y] = directional_light_intensity - weighted_shadow_intensity;
						}

					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("[ERROR] renderScene Lighting: Bounds error at l=" + l + ", x=" + x + ", y=" + y);
						continue;
					}
				}
			}

			if (redAccumulator != null && greenAccumulator != null && blueAccumulator != null &&
				saturationAccumulator != null && colorCount != null) {
				int arrayLength = Math.min(mapHeight, Math.min(redAccumulator.length,
					Math.min(greenAccumulator.length, Math.min(blueAccumulator.length,
						Math.min(saturationAccumulator.length, colorCount.length)))));

				for (int k5 = 0; k5 < arrayLength; k5++) {
					redAccumulator[k5] = 0;
					greenAccumulator[k5] = 0;
					blueAccumulator[k5] = 0;
					saturationAccumulator[k5] = 0;
					colorCount[k5] = 0;
				}
			}

			for (int l6 = -5; l6 < mapWidth + 5; l6++) {
				for (int i8 = 0; i8 < mapHeight; i8++) {

					if (redAccumulator == null || i8 >= redAccumulator.length ||
						greenAccumulator == null || i8 >= greenAccumulator.length ||
						blueAccumulator == null || i8 >= blueAccumulator.length ||
						saturationAccumulator == null || i8 >= saturationAccumulator.length ||
						colorCount == null || i8 >= colorCount.length) {
						continue;
					}

					int k9 = l6 + 5;
					if (k9 >= 0 && k9 < mapWidth) {
						try {
							if (underlay != null && l < underlay.length &&
								underlay[l] != null && k9 < underlay[l].length &&
								underlay[l][k9] != null && i8 < underlay[l][k9].length) {

								int l12 = underlay[l][k9][i8] & 0xff;
								if (l12 > 0 && Floor.cache != null && l12 - 1 < Floor.cache.length) {
									Floor flo = Floor.cache[l12 - 1];
									if (flo != null) {
										redAccumulator[i8] += flo.anInt397;
										greenAccumulator[i8] += flo.anInt395;
										blueAccumulator[i8] += flo.anInt396;
										saturationAccumulator[i8] += flo.anInt398;
										colorCount[i8]++;
									}
								}
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							System.err.println("[ERROR] renderScene Color+: Bounds error at l6=" + l6 + ", i8=" + i8 + ", k9=" + k9);
						}
					}

					int i13 = l6 - 5;
					if (i13 >= 0 && i13 < mapWidth) {
						try {
							if (underlay != null && l < underlay.length &&
								underlay[l] != null && i13 < underlay[l].length &&
								underlay[l][i13] != null && i8 < underlay[l][i13].length) {

								int i14 = underlay[l][i13][i8] & 0xff;
								if (i14 > 0 && Floor.cache != null && i14 - 1 < Floor.cache.length) {
									Floor flo_1 = Floor.cache[i14 - 1];
									if (flo_1 != null) {
										redAccumulator[i8] -= flo_1.anInt397;
										greenAccumulator[i8] -= flo_1.anInt395;
										blueAccumulator[i8] -= flo_1.anInt396;
										saturationAccumulator[i8] -= flo_1.anInt398;
										colorCount[i8]--;
									}
								}
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							System.err.println("[ERROR] renderScene Color-: Bounds error at l6=" + l6 + ", i8=" + i8 + ", i13=" + i13);
						}
					}
				}

				if (l6 >= 1 && l6 < mapWidth - 1) {
					int l9 = 0;
					int j13 = 0;
					int j14 = 0;
					int k15 = 0;
					int k16 = 0;

					for (int k17 = -5; k17 < mapHeight + 5; k17++) {
						int j18 = k17 + 5;
						if (j18 >= 0 && j18 < mapHeight) {
							if (redAccumulator != null && j18 < redAccumulator.length &&
								greenAccumulator != null && j18 < greenAccumulator.length &&
								blueAccumulator != null && j18 < blueAccumulator.length &&
								saturationAccumulator != null && j18 < saturationAccumulator.length &&
								colorCount != null && j18 < colorCount.length) {
								l9 += redAccumulator[j18];
								j13 += greenAccumulator[j18];
								j14 += blueAccumulator[j18];
								k15 += saturationAccumulator[j18];
								k16 += colorCount[j18];
							}
						}

						int k18 = k17 - 5;
						if (k18 >= 0 && k18 < mapHeight) {
							if (redAccumulator != null && k18 < redAccumulator.length &&
								greenAccumulator != null && k18 < greenAccumulator.length &&
								blueAccumulator != null && k18 < blueAccumulator.length &&
								saturationAccumulator != null && k18 < saturationAccumulator.length &&
								colorCount != null && k18 < colorCount.length) {
								l9 -= redAccumulator[k18];
								j13 -= greenAccumulator[k18];
								j14 -= blueAccumulator[k18];
								k15 -= saturationAccumulator[k18];
								k16 -= colorCount[k18];
							}
						}

						if (k17 >= 1 && k17 < mapHeight - 1 &&
							(!lowMem || checkLowMemConditions(l, l6, k17))) {

							if (l < minPlane) {
								minPlane = l;
							}

							try {
								renderTileIfValid(worldController, l, l6, k17, l9, j13, j14, k15, k16);
							} catch (ArrayIndexOutOfBoundsException e) {
								System.err.println("[ERROR] renderScene Render: Bounds error at l=" + l + ", l6=" + l6 + ", k17=" + k17);
								continue;
							}
						}
					}
				}
			}

			for (int j8 = 1; j8 < mapHeight - 1; j8++) {
				for (int i10 = 1; i10 < mapWidth - 1; i10++) {
					try {
						worldController.setTileHeight(l, i10, j8, getEffectiveLevel(j8, l, i10));
					} catch (Exception e) {
						System.err.println("[ERROR] renderScene Setup: Error at j8=" + j8 + ", i10=" + i10);
					}
				}
			}
		}

		if (worldController != null) {
			worldController.applyLighting(-10, -50, -50);

			for (int j1 = 0; j1 < mapWidth; j1++) {
				for (int l1 = 0; l1 < mapHeight; l1++) {
					try {
						if (tileFlags != null && tileFlags.length > 1 &&
							tileFlags[1] != null && j1 < tileFlags[1].length &&
							tileFlags[1][j1] != null && l1 < tileFlags[1][j1].length &&
							(tileFlags[1][j1][l1] & 2) == 2) {
							worldController.removeTopLevel(l1, j1);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("[ERROR] renderScene Bridge: Bounds error at j1=" + j1 + ", l1=" + l1);
					}
				}
			}
		}

		int i2 = 1;
		int j2 = 2;
		int k2 = 4;

		for (int l2 = 0; l2 < 4; l2++) {
			if (l2 > 0) {
				i2 <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}

			for (int i3 = 0; i3 <= l2; i3++) {
				for (int k3 = 0; k3 <= mapHeight; k3++) {
					for (int i4 = 0; i4 <= mapWidth; i4++) {
						try {
							processOcclusionCulling(i2, j2, k2, l2, i3, k3, i4);
						} catch (ArrayIndexOutOfBoundsException e) {
							System.err.println("[ERROR] renderScene Occlusion: Bounds error at i3=" + i3 + ", k3=" + k3 + ", i4=" + i4);
							continue;
						}
					}
				}
			}
		}
	}

	private boolean checkLowMemConditions(int l, int l6, int k17) {
		try {
			if (tileFlags == null || tileFlags.length == 0) return true;

			if (tileFlags[0] != null && l6 < tileFlags[0].length &&
				tileFlags[0][l6] != null && k17 < tileFlags[0][l6].length &&
				(tileFlags[0][l6][k17] & 2) != 0) {
				return true;
			}

			if (l < tileFlags.length && tileFlags[l] != null &&
				l6 < tileFlags[l].length && tileFlags[l][l6] != null &&
				k17 < tileFlags[l][l6].length &&
				(tileFlags[l][l6][k17] & 0x10) == 0 &&
				getEffectiveLevel(k17, l, l6) == baseLevel) {
				return true;
			}

			return false;
		} catch (Exception e) {
			return true;
		}
	}

	private void renderTileIfValid(WorldController worldController, int l, int l6, int k17,
								   int l9, int j13, int j14, int k15, int k16) {

		if (underlay == null || l >= underlay.length ||
			underlay[l] == null || l6 >= underlay[l].length ||
			underlay[l][l6] == null || k17 >= underlay[l][l6].length) {
			return;
		}

		if (overlayIds == null || l >= overlayIds.length ||
			overlayIds[l] == null || l6 >= overlayIds[l].length ||
			overlayIds[l][l6] == null || k17 >= overlayIds[l][l6].length) {
			return;
		}

		int l18 = underlay[l][l6][k17] & 0xff;
		int i19 = overlayIds[l][l6][k17] & 0xff;

		if (l18 > 0 || i19 > 0) {

			int j19 = safeGetHeight(heightMap, l, l6, k17, 0);
			int k19 = safeGetHeight(heightMap, l, l6 + 1, k17, 0);
			int l19 = safeGetHeight(heightMap, l, l6 + 1, k17 + 1, 0);
			int i20 = safeGetHeight(heightMap, l, l6, k17 + 1, 0);

			int j20 = safeGetLighting(lightMap, l6, k17, 0);
			int k20 = safeGetLighting(lightMap, l6 + 1, k17, 0);
			int l20 = safeGetLighting(lightMap, l6 + 1, k17 + 1, 0);
			int i21 = safeGetLighting(lightMap, l6, k17 + 1, 0);

			int j21 = -1;
			int k21 = -1;

			if (l18 > 0) {
				int l21 = k15 != 0 ? (l9 * 256) / k15 : 0;
				int j22 = k16 != 0 ? j13 / k16 : 0;
				int l22 = k16 != 0 ? j14 / k16 : 0;
				j21 = packRGB(l21, j22, l22);

				if (l22 < 0) l22 = 0;
				else if (l22 > 255) l22 = 255;
				k21 = packRGB(l21, j22, l22);
			}

			if (l > 0) {
				boolean flag = l18 != 0 || safeGetByte(overlayRotations, l, l6, k17, (byte)0) == 0;
				if (i19 > 0 && OverlayFloor.overlayFloor != null && i19 - 1 < OverlayFloor.overlayFloor.length &&
					!OverlayFloor.overlayFloor[i19 - 1].aBoolean393) {
					flag = false;
				}
				if (flag && j19 == k19 && j19 == l19 && j19 == i20) {
					if (occlusionFlags != null && l < occlusionFlags.length &&
						occlusionFlags[l] != null && l6 < occlusionFlags[l].length &&
						occlusionFlags[l][l6] != null && k17 < occlusionFlags[l][l6].length) {
						occlusionFlags[l][l6][k17] |= 0x924;
					}
				}
			}

			int i22 = 0;
			if (j21 != -1) {
				i22 = Rasterizer.colorPalette[adjustBrightness(k21, 96)];
			}

			if (i19 == 0) {
				worldController.addFloorTile(l, l6, k17, 0, 0, -1, j19, k19, l19, i20,
					adjustBrightness(j21, j20), adjustBrightness(j21, k20), adjustBrightness(j21, l20),
					adjustBrightness(j21, i21), 0, 0, 0, 0, i22, 0, false);
			} else {
				renderOverlayFloor(worldController, l, l6, k17, i19, j19, k19, l19, i20,
					j20, k20, l20, i21, j21, i22);
			}
		}
	}

	private void renderOverlayFloor(WorldController worldController, int l, int l6, int k17, int i19,
									int j19, int k19, int l19, int i20, int j20, int k20, int l20, int i21,
									int j21, int i22) {
		try {
			int k22 = safeGetByte(overlayRotations, l, l6, k17, (byte)0) + 1;
			byte byte4 = safeGetByte(overlayShapes, l, l6, k17, (byte)0);

			if (i19 - 1 >= OverlayFloor.overlayFloor.length) {
				i19 = OverlayFloor.overlayFloor.length;
			}

			if (OverlayFloor.overlayFloor == null || i19 - 1 < 0 || i19 - 1 >= OverlayFloor.overlayFloor.length) {
				return;
			}

			OverlayFloor overlay_flo = OverlayFloor.overlayFloor[i19 - 1];
			if (overlay_flo == null) return;

			int textureId = overlay_flo.textureId;
			int j23;
			int k23;

			if (textureId > 50) {
				textureId = -1;
			}

			if (textureId >= 0) {
				k23 = Rasterizer.getTextureAverageColor(textureId);
				j23 = -1;
			} else if (overlay_flo.rgb == 0xff00ff) {
				k23 = 0;
				j23 = -2;
				textureId = -1;
			} else if (overlay_flo.rgb == 0x333333) {
				k23 = Rasterizer.colorPalette[adjustColor(overlay_flo.anInt399, 96)];
				j23 = -2;
				textureId = -1;
			} else if ((i19 - 1) == 63) {
				k23 = overlay_flo.rgb = 0x767676;
				j23 = -2;
				textureId = -1;
			} else {
				j23 = packRGB(overlay_flo.anInt394, overlay_flo.anInt395, overlay_flo.anInt396);
				k23 = Rasterizer.colorPalette[adjustColor(overlay_flo.anInt399, 96)];
			}

			if ((i19 - 1) == 111) {
				k23 = Rasterizer.getTextureAverageColor(1);
				j23 = -1;
				textureId = 1;
			} else if (j23 == 6363) {
				k23 = 0x483B21;
				j23 = packRGB(25, 146, 24);
			} else if ((i19 - 1) == 64) {
				k23 = overlay_flo.rgb;
				j23 = -2;
				textureId = -1;
			} else if ((i19 - 1) == 63) {
				k23 = overlay_flo.rgb = 0x767676;
				j23 = -2;
				textureId = -1;
			} else if ((i19 - 1) == 54 || (i19 - 1) == 15) {
				k23 = overlay_flo.rgb;
				j23 = -2;
				textureId = -1;
			} else if ((i19 - 1) == 151) {
				k23 = 0xfad83d;
			}

			if (overlay_flo.rgb == 0x000000 || (i19 - 1) == 28 || (i19 - 1) == 113 || (i19 - 1) == 6) {
				textureId = 25;
				k23 = Rasterizer.getTextureAverageColor(25);
				j23 = -1;
			}

			if (colors != null && colors.size() < 256 && !colors.contains(k23)) {
				colors.add(k23);
			}

			worldController.addFloorTile(l, l6, k17, k22, byte4, textureId, j19, k19, l19, i20,
				adjustBrightness(j21, j20), adjustBrightness(j21, k20), adjustBrightness(j21, l20), adjustBrightness(j21, i21),
				adjustColor(j23, j20), adjustColor(j23, k20), adjustColor(j23, l20), adjustColor(j23, i21),
				i22, k23, textureId >= 0 && textureId <= 50);

		} catch (Exception e) {
			System.err.println("[ERROR] renderOverlayFloor: Error rendering overlay at l=" + l + ", l6=" + l6 + ", k17=" + k17);
		}
	}

	private void processOcclusionCulling(int i2, int j2, int k2, int l2, int i3, int k3, int i4) {
		if (occlusionFlags == null || i3 >= occlusionFlags.length ||
			occlusionFlags[i3] == null || i4 >= occlusionFlags[i3].length ||
			occlusionFlags[i3][i4] == null || k3 >= occlusionFlags[i3][i4].length) {
			return;
		}

		if ((occlusionFlags[i3][i4][k3] & i2) != 0) {
			processXAxisOcclusion(i2, l2, i3, k3, i4);
		}

		if ((occlusionFlags[i3][i4][k3] & j2) != 0) {
			processYAxisOcclusion(j2, l2, i3, k3, i4);
		}

		if ((occlusionFlags[i3][i4][k3] & k2) != 0) {
			processZAxisOcclusion(k2, l2, i3, k3, i4);
		}
	}

	private void processXAxisOcclusion(int i2, int l2, int i3, int k3, int i4) {
		try {
			int k4 = k3;
			int l5 = k3;
			int i7 = i3;
			int k8 = i3;

			for (; k4 > 0 && safeCheckOcclusionFlag(occlusionFlags, i3, i4, k4 - 1, i2); k4--);
			for (; l5 < mapHeight && safeCheckOcclusionFlag(occlusionFlags, i3, i4, l5 + 1, i2); l5++);

			label0: for (; i7 > 0; i7--) {
				for (int j10 = k4; j10 <= l5; j10++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, i7 - 1, i4, j10, i2)) {
						break label0;
					}
				}
			}

			label1: for (; k8 < l2; k8++) {
				for (int k10 = k4; k10 <= l5; k10++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, k8 + 1, i4, k10, i2)) {
						break label1;
					}
				}
			}

			int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
			if (l10 >= 8) {
				char c1 = '\360';
				int k14 = safeGetHeight(heightMap, k8, i4, k4, 0) - c1;
				int l15 = safeGetHeight(heightMap, i7, i4, k4, 0);

				WorldController.method277(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);

				for (int l16 = i7; l16 <= k8; l16++) {
					for (int l17 = k4; l17 <= l5; l17++) {
						safeClearOcclusionFlag(occlusionFlags, l16, i4, l17, i2);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[ERROR] processXAxisOcclusion: Error at i3=" + i3 + ", k3=" + k3 + ", i4=" + i4);
		}
	}

	private void processYAxisOcclusion(int j2, int l2, int i3, int k3, int i4) {
		try {
			int l4 = i4;
			int i6 = i4;
			int j7 = i3;
			int l8 = i3;

			for (; l4 > 0 && safeCheckOcclusionFlag(occlusionFlags, i3, l4 - 1, k3, j2); l4--);
			for (; i6 < mapWidth && safeCheckOcclusionFlag(occlusionFlags, i3, i6 + 1, k3, j2); i6++);

			label2: for (; j7 > 0; j7--) {
				for (int i11 = l4; i11 <= i6; i11++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, j7 - 1, i11, k3, j2)) {
						break label2;
					}
				}
			}

			label3: for (; l8 < l2; l8++) {
				for (int j11 = l4; j11 <= i6; j11++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, l8 + 1, j11, k3, j2)) {
						break label3;
					}
				}
			}

			int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
			if (k11 >= 8) {
				char c2 = '\360';
				int l14 = safeGetHeight(heightMap, l8, l4, k3, 0) - c2;
				int i16 = safeGetHeight(heightMap, j7, l4, k3, 0);

				WorldController.method277(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);

				for (int i17 = j7; i17 <= l8; i17++) {
					for (int i18 = l4; i18 <= i6; i18++) {
						safeClearOcclusionFlag(occlusionFlags, i17, i18, k3, j2);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[ERROR] processYAxisOcclusion: Error at i3=" + i3 + ", k3=" + k3 + ", i4=" + i4);
		}
	}

	private void processZAxisOcclusion(int k2, int l2, int i3, int k3, int i4) {
		try {
			int i5 = i4;
			int j6 = i4;
			int k7 = k3;
			int i9 = k3;

			for (; k7 > 0 && safeCheckOcclusionFlag(occlusionFlags, i3, i4, k7 - 1, k2); k7--);
			for (; i9 < mapHeight && safeCheckOcclusionFlag(occlusionFlags, i3, i4, i9 + 1, k2); i9++);

			label4: for (; i5 > 0; i5--) {
				for (int l11 = k7; l11 <= i9; l11++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, i3, i5 - 1, l11, k2)) {
						break label4;
					}
				}
			}

			label5: for (; j6 < mapWidth; j6++) {
				for (int i12 = k7; i12 <= i9; i12++) {
					if (!safeCheckOcclusionFlag(occlusionFlags, i3, j6 + 1, i12, k2)) {
						break label5;
					}
				}
			}

			if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
				int j12 = safeGetHeight(heightMap, i3, i5, k7, 0);

				WorldController.method277(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);

				for (int k13 = i5; k13 <= j6; k13++) {
					for (int i15 = k7; i15 <= i9; i15++) {
						safeClearOcclusionFlag(occlusionFlags, i3, k13, i15, k2);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[ERROR] processZAxisOcclusion: Error at i3=" + i3 + ", k3=" + k3 + ", i4=" + i4);
		}
	}

	private static int safeGetHeight(int[][][] heightArray, int plane, int x, int y, int defaultValue) {
		try {
			if (heightArray != null &&
				plane >= 0 && plane < heightArray.length &&
				heightArray[plane] != null &&
				x >= 0 && x < heightArray[plane].length &&
				heightArray[plane][x] != null &&
				y >= 0 && y < heightArray[plane][x].length) {
				return heightArray[plane][x][y];
			}
		} catch (Exception e) {

		}
		return defaultValue;
	}

	private static byte safeGetByte(byte[][][] byteArray, int plane, int x, int y, byte defaultValue) {
		try {
			if (byteArray != null &&
				plane >= 0 && plane < byteArray.length &&
				byteArray[plane] != null &&
				x >= 0 && x < byteArray[plane].length &&
				byteArray[plane][x] != null &&
				y >= 0 && y < byteArray[plane][x].length) {
				return byteArray[plane][x][y];
			}
		} catch (Exception e) {

		}
		return defaultValue;
	}

	private static int safeGetLighting(int[][] lightArray, int x, int y, int defaultValue) {
		try {
			if (lightArray != null &&
				x >= 0 && x < lightArray.length &&
				lightArray[x] != null &&
				y >= 0 && y < lightArray[x].length) {
				return lightArray[x][y];
			}
		} catch (Exception e) {

		}
		return defaultValue;
	}

	private static boolean safeCheckOcclusionFlag(int[][][] occlusionArray, int plane, int x, int y, int flag) {
		try {
			if (occlusionArray != null &&
				plane >= 0 && plane < occlusionArray.length &&
				occlusionArray[plane] != null &&
				x >= 0 && x < occlusionArray[plane].length &&
				occlusionArray[plane][x] != null &&
				y >= 0 && y < occlusionArray[plane][x].length) {
				return (occlusionArray[plane][x][y] & flag) != 0;
			}
		} catch (Exception e) {

		}
		return false;
	}

	private static void safeClearOcclusionFlag(int[][][] occlusionArray, int plane, int x, int y, int flag) {
		try {
			if (occlusionArray != null &&
				plane >= 0 && plane < occlusionArray.length &&
				occlusionArray[plane] != null &&
				x >= 0 && x < occlusionArray[plane].length &&
				occlusionArray[plane][x] != null &&
				y >= 0 && y < occlusionArray[plane][x].length) {
				occlusionArray[plane][x][y] &= ~flag;
			}
		} catch (Exception e) {

		}
	}

	public void markWaterArea(int i, int j, int l, int i1) {
		for (int j1 = i; j1 <= i + j; j1++) {
			for (int k1 = i1; k1 <= i1 + l; k1++)
				if (k1 >= 0 && k1 < mapWidth && j1 >= 0 && j1 < mapHeight) {
					shadowMap[0][k1][j1] = 127;
					if (k1 == i1 && k1 > 0)
						heightMap[0][k1][j1] = heightMap[0][k1 - 1][j1];
					if (k1 == i1 + l && k1 < mapWidth - 1)
						heightMap[0][k1][j1] = heightMap[0][k1 + 1][j1];
					if (j1 == i && j1 > 0)
						heightMap[0][k1][j1] = heightMap[0][k1][j1 - 1];
					if (j1 == i + j && j1 < mapHeight - 1)
						heightMap[0][k1][j1] = heightMap[0][k1][j1 + 1];
				}

		}
	}

	private void placeObject(int i, WorldController worldController, CollisionMap collisionMap, int j, int k, int l, int i1, int j1) {
		if (lowMem && (tileFlags[0][l][i] & 2) == 0) {
			if ((tileFlags[k][l][i] & 0x10) != 0)
				return;
			if (getEffectiveLevel(i, k, l) != baseLevel)
				return;
		}
		if (k < minPlane)
			minPlane = k;
		int k1 = heightMap[k][l][i];
		int l1 = heightMap[k][l + 1][i];
		int i2 = heightMap[k][l + 1][i + 1];
		int j2 = heightMap[k][l][i + 1];
		int k2 = k1 + l1 + i2 + j2 >> 2;
		ObjectDef objectDef = ObjectDef.forID(i1);
		int l2 = l + (i << 7) + (i1 << 14) + 0x40000000;
		if (!objectDef.hasActions)
			l2 += 0x80000000;
		byte byte0 = (byte) ((j1 << 6) + j);
		if (j == 22) {
			if (!SettingsConfig.enableGroundDecorations && !objectDef.hasActions && !objectDef.aBoolean736)
				return;
			Object obj;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj = objectDef.getModel(22, j1, k1, l1, i2, j2, -1);
			else
				obj = new InteractiveObject(i1, j1, 22, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addGroundDecoration(k, k2, i, ((Animable) (obj)), byte0, l2, l);
			if (objectDef.clipFlag && objectDef.hasActions && collisionMap != null)
				collisionMap.addFloorDecoration(i, l);
			return;
		}
		if (j == 10 || j == 11) {
			Animable obj1;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj1 = objectDef.getModel(10, j1, k1, l1, i2, j2, -1);
			else
				obj1 = new InteractiveObject(i1, j1, 10, l1, i2, k1, j2, objectDef.animationId, true);
			if (obj1 != null) {
				int i5 = 0;
				if (j == 11)
					i5 += 256;
				int j4;
				int l4;
				if (j1 == 1 || j1 == 3) {
					j4 = objectDef.sizeY;
					l4 = objectDef.sizeX;
				} else {
					j4 = objectDef.sizeX;
					l4 = objectDef.sizeY;
				}
				if (worldController.canPlaceObject(l2, byte0, k2, l4, obj1, j4, k, i5, i, l) && objectDef.castsShadow) {
					Model model;
					if (obj1 instanceof Model)
						model = (Model) obj1;
					else
						model = objectDef.getModel(10, j1, k1, l1, i2, j2, -1);
					if (model != null) {
						for (int j5 = 0; j5 <= j4; j5++) {
							for (int k5 = 0; k5 <= l4; k5++) {
								int l5 = model.diameter / 4;
								if (l5 > 30)
									l5 = 30;
								if (l5 > shadowMap[k][l + j5][i + k5])
									shadowMap[k][l + j5][i + k5] = (byte) l5;
							}

						}

					}
				}
			}
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, l, i, j1);
			return;
		}
		if (j >= 12) {
			Animable obj2;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj2 = objectDef.getModel(j, j1, k1, l1, i2, j2, -1);
			else
				obj2 = new InteractiveObject(i1, j1, j, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.canPlaceObject(l2, byte0, k2, 1, obj2, 1, k, 0, i, l);
			if (j <= 17 && j != 13 && k > 0)
				occlusionFlags[k][l][i] |= 0x924;
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, l, i, j1);
			return;
		}
		if (j == 0) {
			Animable obj3;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj3 = objectDef.getModel(0, j1, k1, l1, i2, j2, -1);
			else
				obj3 = new InteractiveObject(i1, j1, 0, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWall(wallFlags[j1], obj3, l2, i, byte0, l, null, k2, 0, k);
			if (j1 == 0) {
				if (objectDef.castsShadow) {
					shadowMap[k][l][i] = 50;
					shadowMap[k][l][i + 1] = 50;
				}
				if (objectDef.blocksMovement)
					occlusionFlags[k][l][i] |= 0x249;
			} else if (j1 == 1) {
				if (objectDef.castsShadow) {
					shadowMap[k][l][i + 1] = 50;
					shadowMap[k][l + 1][i + 1] = 50;
				}
				if (objectDef.blocksMovement)
					occlusionFlags[k][l][i + 1] |= 0x492;
			} else if (j1 == 2) {
				if (objectDef.castsShadow) {
					shadowMap[k][l + 1][i] = 50;
					shadowMap[k][l + 1][i + 1] = 50;
				}
				if (objectDef.blocksMovement)
					occlusionFlags[k][l + 1][i] |= 0x249;
			} else
			{
				if (objectDef.castsShadow) {
					shadowMap[k][l][i] = 50;
					shadowMap[k][l + 1][i] = 50;
				}
				if (objectDef.blocksMovement)
					occlusionFlags[k][l][i] |= 0x492;
			}
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addWall(i, j1, l, j, objectDef.blocksProjectiles);
			if (objectDef.wallThickness != 16)
				worldController.animateWallDecoration(i, objectDef.wallThickness, l, k);
			return;
		}
		if (j == 1) {
			Animable obj4;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj4 = objectDef.getModel(1, j1, k1, l1, i2, j2, -1);
			else
				obj4 = new InteractiveObject(i1, j1, 1, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWall(wallDirections[j1], obj4, l2, i, byte0, l, null, k2, 0, k);
			if (objectDef.castsShadow)
				if (j1 == 0)
					shadowMap[k][l][i + 1] = 50;
				else if (j1 == 1)
					shadowMap[k][l + 1][i + 1] = 50;
				else if (j1 == 2)
					shadowMap[k][l + 1][i] = 50;
				else shadowMap[k][l][i] = 50;
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addWall(i, j1, l, j, objectDef.blocksProjectiles);
			return;
		}
		if (j == 2) {
			int i3 = j1 + 1 & 3;
			Animable obj11;
			Animable obj12;
			if (objectDef.animationId == -1 && objectDef.childIds == null) {
				obj11 = objectDef.getModel(2, 4 + j1, k1, l1, i2, j2, -1);
				obj12 = objectDef.getModel(2, i3, k1, l1, i2, j2, -1);
			} else {
				obj11 = new InteractiveObject(i1, 4 + j1, 2, l1, i2, k1, j2, objectDef.animationId, true);
				obj12 = new InteractiveObject(i1, i3, 2, l1, i2, k1, j2, objectDef.animationId, true);
			}
			worldController.addWall(wallFlags[j1], obj11, l2, i, byte0, l, obj12, k2, wallFlags[i3], k);
			if (objectDef.blocksMovement)
				if (j1 == 0) {
					occlusionFlags[k][l][i] |= 0x249;
					occlusionFlags[k][l][i + 1] |= 0x492;
				} else if (j1 == 1) {
					occlusionFlags[k][l][i + 1] |= 0x492;
					occlusionFlags[k][l + 1][i] |= 0x249;
				} else if (j1 == 2) {
					occlusionFlags[k][l + 1][i] |= 0x249;
					occlusionFlags[k][l][i] |= 0x492;
				} else
				{
					occlusionFlags[k][l][i] |= 0x492;
					occlusionFlags[k][l][i] |= 0x249;
				}
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addWall(i, j1, l, j, objectDef.blocksProjectiles);
			if (objectDef.wallThickness != 16)
				worldController.animateWallDecoration(i, objectDef.wallThickness, l, k);
			return;
		}
		if (j == 3) {
			Animable obj5;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj5 = objectDef.getModel(3, j1, k1, l1, i2, j2, -1);
			else
				obj5 = new InteractiveObject(i1, j1, 3, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWall(wallDirections[j1], obj5, l2, i, byte0, l, null, k2, 0, k);
			if (objectDef.castsShadow)
				if (j1 == 0)
					shadowMap[k][l][i + 1] = 50;
				else if (j1 == 1)
					shadowMap[k][l + 1][i + 1] = 50;
				else if (j1 == 2)
					shadowMap[k][l + 1][i] = 50;
				else shadowMap[k][l][i] = 50;
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addWall(i, j1, l, j, objectDef.blocksProjectiles);
			return;
		}
		if (j == 9) {
			Animable obj6;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj6 = objectDef.getModel(j, j1, k1, l1, i2, j2, -1);
			else
				obj6 = new InteractiveObject(i1, j1, j, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.canPlaceObject(l2, byte0, k2, 1, obj6, 1, k, 0, i, l);
			if (objectDef.clipFlag && collisionMap != null)
				collisionMap.addObject(objectDef.blocksProjectiles, objectDef.sizeX, objectDef.sizeY, l, i, j1);
			return;
		}
		if (objectDef.rotateHeights)
			if (j1 == 1) {
				int j3 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k1;
				k1 = j3;
			} else if (j1 == 2) {
				int k3 = j2;
				j2 = l1;
				l1 = k3;
				k3 = i2;
				i2 = k1;
				k1 = k3;
			} else if (j1 == 3) {
				int l3 = j2;
				j2 = k1;
				k1 = l1;
				l1 = i2;
				i2 = l3;
			}
		if (j == 4) {
			Animable obj7;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj7 = objectDef.getModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj7 = new InteractiveObject(i1, 0, 4, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWallDecoration(l2, i, j1 * 512, k, 0, k2, obj7, l, byte0, 0, wallFlags[j1]);
			return;
		}
		if (j == 5) {
			int i4 = 16;
			int k4 = worldController.getWallId(k, l, i);
			if (k4 > 0)
				i4 = ObjectDef.forID(k4 >> 14 & 0x7fff).wallThickness;
			Animable obj13;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj13 = objectDef.getModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj13 = new InteractiveObject(i1, 0, 4, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWallDecoration(l2, i, j1 * 512, k, directionDeltaX[j1] * i4, k2, obj13, l, byte0, directionDeltaY[j1] * i4, wallFlags[j1]);
			return;
		}
		if (j == 6) {
			Animable obj8;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj8 = objectDef.getModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj8 = new InteractiveObject(i1, 0, 4, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWallDecoration(l2, i, j1, k, 0, k2, obj8, l, byte0, 0, 256);
			return;
		}
		if (j == 7) {
			Animable obj9;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj9 = objectDef.getModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj9 = new InteractiveObject(i1, 0, 4, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWallDecoration(l2, i, j1, k, 0, k2, obj9, l, byte0, 0, 512);
			return;
		}
		if (j == 8) {
			Animable obj10;
			if (objectDef.animationId == -1 && objectDef.childIds == null)
				obj10 = objectDef.getModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj10 = new InteractiveObject(i1, 0, 4, l1, i2, k1, j2, objectDef.animationId, true);
			worldController.addWallDecoration(l2, i, j1, k, 0, k2, obj10, l, byte0, 0, 768);
		}
	}

	private int packRGB(int i, int j, int k) {
		if (k > 179)
			j /= 2;
		if (k > 192)
			j /= 2;
		if (k > 217)
			j /= 2;
		if (k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	public void loadMapChunk(int i, int j, CollisionMap[] aclass11, int l, int i1, byte[] abyte0, int j1, int k1, int l1) {
		for (int i2 = 0; i2 < 8; i2++) {
			for (int j2 = 0; j2 < 8; j2++)
				if (l + i2 > 0 && l + i2 < 103 && l1 + j2 > 0 && l1 + j2 < 103)
					aclass11[k1].collisionFlags[l + i2][l1 + j2] &= 0xfeffffff;

		}
		Stream stream = new Stream(abyte0);
		for (int l2 = 0; l2 < 4; l2++) {
			for (int i3 = 0; i3 < 64; i3++) {
				for (int j3 = 0; j3 < 64; j3++)
					if (l2 == i && i3 >= i1 && i3 < i1 + 8 && j3 >= j1 && j3 < j1 + 8)
						parseMapTile(l1 + CoordinateTransform.transformY(j3 & 7, j, i3 & 7), 0, stream, l + CoordinateTransform.transformX(j, j3 & 7, i3 & 7), k1, j, 0);
					else
						parseMapTile(-1, 0, stream, -1, 0, 0, 0);

			}

		}

	}

	public void loadMapRegion(byte[] abyte0, int i, int j, int k, int l, CollisionMap[] aclass11) {
		for (int i1 = 0; i1 < 4; i1++) {
			for (int j1 = 0; j1 < 64; j1++) {
				for (int k1 = 0; k1 < 64; k1++)
					if (j + j1 > 0 && j + j1 < 103 && i + k1 > 0 && i + k1 < 103)
						aclass11[i1].collisionFlags[j + j1][i + k1] &= 0xfeffffff;

			}

		}

		Stream stream = new Stream(abyte0);
		for (int l1 = 0; l1 < 4; l1++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int j2 = 0; j2 < 64; j2++)
					parseMapTile(j2 + i, l, stream, i2 + j, l1, 0, k);

			}

		}
	}

	private void parseMapTile(int i, int j, Stream stream, int k, int l, int i1, int k1) {
		try {
			if (k >= 0 && k < 104 && i >= 0 && i < 104) {
				tileFlags[l][k][i] = 0;
				do {
					int l1 = stream.readUnsignedByte();
					if (l1 == 0)
						if (l == 0) {
							heightMap[0][k][i] = -calculateTerrainHeight(0xe3b7b + k + k1, 0x87cce + i + j) * 8;
							return;
						} else {
							heightMap[l][k][i] = heightMap[l - 1][k][i] - 240;
							return;
						}
					if (l1 == 1) {
						int j2 = stream.readUnsignedByte();
						if (j2 == 1)
							j2 = 0;
						if (l == 0) {
							heightMap[0][k][i] = -j2 * 8;
							return;
						} else {
							heightMap[l][k][i] = heightMap[l - 1][k][i] - j2 * 8;
							return;
						}
					}
					if (l1 <= 49) {
						overlayIds[l][k][i] = stream.readSignedByte();
						overlayRotations[l][k][i] = (byte) ((l1 - 2) / 4);
						overlayShapes[l][k][i] = (byte) ((l1 - 2) + i1 & 3);
					} else if (l1 <= 81)
						tileFlags[l][k][i] = (byte) (l1 - 49);
					else
						underlay[l][k][i] = (byte) (l1 - 81);
				} while (true);
			}
			do {
				int i2 = stream.readUnsignedByte();
				if (i2 == 0)
					break;
				if (i2 == 1) {
					stream.readUnsignedByte();
					return;
				}
				if (i2 <= 49)
					stream.readUnsignedByte();
			} while (true);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private int getEffectiveLevel(int i, int j, int k) {
		if ((tileFlags[j][k][i] & 8) != 0)
			return 0;
		if (j > 0 && (tileFlags[1][k][i] & 2) != 0)
			return j - 1;
		else
			return j;
	}

	public void loadObjectChunk(CollisionMap[] aclass11, WorldController worldController, int i, int j, int k, int l, byte[] abyte0, int i1, int j1, int k1) {
		label0: {
			Stream stream = new Stream(abyte0);
			int l1 = -1;
			do {
				int i2 = stream.readLargeSmart();
				if (i2 == 0)
					break label0;
				l1 += i2;
				int j2 = 0;
				do {
					int k2 = stream.readSmartUnsigned();
					if (k2 == 0)
						break;
					j2 += k2 - 1;
					int l2 = j2 & 0x3f;
					int i3 = j2 >> 6 & 0x3f;
					int j3 = j2 >> 12;
					int k3 = stream.readUnsignedByte();
					int l3 = k3 >> 2;
					int i4 = k3 & 3;
					if (j3 == i && i3 >= i1 && i3 < i1 + 8 && l2 >= k && l2 < k + 8) {
						ObjectDef objectDef = ObjectDef.forID(l1);
						int j4 = j + CoordinateTransform.transformObjectX(j1, objectDef.sizeY, i3 & 7, l2 & 7, objectDef.sizeX);
						int k4 = k1 + CoordinateTransform.transformObjectY(l2 & 7, objectDef.sizeY, j1, objectDef.sizeX, i3 & 7);
						if (j4 > 0 && k4 > 0 && j4 < 103 && k4 < 103) {
							int l4 = j3;
							if ((tileFlags[1][j4][k4] & 2) == 2)
								l4--;
							CollisionMap collisionMap = null;
							if (l4 >= 0)
								collisionMap = aclass11[l4];
							placeObject(k4, worldController, collisionMap, l3, l, j4, l1, i4 + j1 & 3);
						}
					}
				} while (true);
			} while (true);
		}
	}

	private int adjustColor(int i, int j) {
		if (i == -2)
			return 0xbc614e;
		if (i == -1) {
			if (j < 0)
				j = 0;
			else if (j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}
		j = (j * (i & 0x7f)) / 128;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public void loadObjectRegion(int i, CollisionMap[] aclass11, int j, WorldController worldController, byte[] abyte0) {
		label0: {
			Stream stream = new Stream(abyte0);
			int l = -1;
			do {
				int i1 = stream.readSmartUnsigned();
				if (i1 == 0)
					break label0;
				l += i1;
				int j1 = 0;
				do {
					int k1 = stream.readSmartUnsigned();
					if (k1 == 0)
						break;
					j1 += k1 - 1;
					int l1 = j1 & 0x3f;
					int i2 = j1 >> 6 & 0x3f;
					int j2 = j1 >> 12;
					int k2 = stream.readUnsignedByte();
					int l2 = k2 >> 2;
					int i3 = k2 & 3;
					int j3 = i2 + i;
					int k3 = l1 + j;
					if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103 && j2 >= 0 && j2 < 4) {
						int l3 = j2;
						if ((tileFlags[1][j3][k3] & 2) == 2)
							l3--;
						CollisionMap collisionMap = null;
						if (l3 >= 0)
							collisionMap = aclass11[l3];
						placeObject(k3, worldController, collisionMap, l2, j2, j3, l, i3);
					}
				} while (true);
			} while (true);
		}
	}

	// --- JSON-based map loading methods ---

	public boolean loadMapRegionJson(int fileId, int offsetY, int offsetX, int originX, int originY, CollisionMap[] collisionMaps) {
		JsonObject json = JsonCacheLoader.loadMapJson("landscape_" + fileId + ".json");
		if (json == null) return false;

		// Clear collision flags for this region
		for (int plane = 0; plane < 4; plane++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++) {
					if (offsetX + x > 0 && offsetX + x < 103 && offsetY + y > 0 && offsetY + y < 103)
						collisionMaps[plane].collisionFlags[offsetX + x][offsetY + y] &= 0xfeffffff;
				}
			}
		}

		// Set defaults for all tiles (noise height, no overlay/underlay/flags)
		for (int plane = 0; plane < 4; plane++) {
			for (int lx = 0; lx < 64; lx++) {
				for (int ly = 0; ly < 64; ly++) {
					int absX = lx + offsetX;
					int absY = ly + offsetY;
					if (absX >= 0 && absX < 104 && absY >= 0 && absY < 104) {
						tileFlags[plane][absX][absY] = 0;
						if (plane == 0) {
							heightMap[0][absX][absY] = -calculateTerrainHeight(
								0xe3b7b + absX + originX, 0x87cce + absY + originY) * 8;
						} else {
							heightMap[plane][absX][absY] = heightMap[plane - 1][absX][absY] - 240;
						}
					}
				}
			}
		}

		// Apply JSON tile data (sparse - only non-default tiles stored)
		JsonArray planes = json.getAsJsonArray("planes");
		for (int p = 0; p < planes.size(); p++) {
			JsonObject planeObj = planes.get(p).getAsJsonObject();
			int plane = planeObj.get("plane").getAsInt();
			if (plane < 0 || plane >= 4) continue;

			JsonArray tiles = planeObj.getAsJsonArray("tiles");
			for (int t = 0; t < tiles.size(); t++) {
				JsonObject tile = tiles.get(t).getAsJsonObject();
				int lx = tile.get("x").getAsInt();
				int ly = tile.get("y").getAsInt();
				int absX = lx + offsetX;
				int absY = ly + offsetY;

				if (absX < 0 || absX >= 104 || absY < 0 || absY >= 104) continue;

				if (tile.has("height")) {
					int h = tile.get("height").getAsInt();
					if (h == 1) h = 0;
					if (plane == 0) {
						heightMap[0][absX][absY] = -h * 8;
					} else {
						heightMap[plane][absX][absY] = heightMap[plane - 1][absX][absY] - h * 8;
					}
				}

				if (tile.has("overlayId")) {
					overlayIds[plane][absX][absY] = (byte) tile.get("overlayId").getAsInt();
				}
				if (tile.has("overlayRotation")) {
					overlayRotations[plane][absX][absY] = (byte) tile.get("overlayRotation").getAsInt();
				}
				if (tile.has("overlayShape")) {
					overlayShapes[plane][absX][absY] = (byte) tile.get("overlayShape").getAsInt();
				}
				if (tile.has("underlayId")) {
					underlay[plane][absX][absY] = (byte) tile.get("underlayId").getAsInt();
				}
				if (tile.has("flags")) {
					tileFlags[plane][absX][absY] = (byte) tile.get("flags").getAsInt();
				}
			}
		}

		return true;
	}

	public boolean loadObjectRegionJson(int fileId, int offsetX, CollisionMap[] collisionMaps,
										int offsetY, WorldController worldController) {
		JsonObject json = JsonCacheLoader.loadMapJson("objects_" + fileId + ".json");
		if (json == null) return false;

		JsonArray objects = json.getAsJsonArray("objects");
		for (int i = 0; i < objects.size(); i++) {
			JsonObject obj = objects.get(i).getAsJsonObject();
			int objectId = obj.get("objectId").getAsInt();
			int localX = obj.get("localX").getAsInt();
			int localY = obj.get("localY").getAsInt();
			int plane = obj.get("plane").getAsInt();
			int type = obj.get("type").getAsInt();
			int rotation = obj.get("rotation").getAsInt();

			int absX = localX + offsetX;
			int absY = localY + offsetY;

			if (absX > 0 && absY > 0 && absX < 103 && absY < 103 && plane >= 0 && plane < 4) {
				int effectivePlane = plane;
				if ((tileFlags[1][absX][absY] & 2) == 2)
					effectivePlane--;
				CollisionMap collisionMap = null;
				if (effectivePlane >= 0)
					collisionMap = collisionMaps[effectivePlane];
				placeObject(absY, worldController, collisionMap, type, plane, absX, objectId, rotation);
			}
		}

		return true;
	}

	public static boolean isJsonMapAvailable(int fileId) {
		return JsonCacheLoader.isAvailable("maps_json/landscape_" + fileId + ".json");
	}

	public static boolean isJsonObjectsAvailable(int fileId) {
		return JsonCacheLoader.isAvailable("maps_json/objects_" + fileId + ".json");
	}
}
