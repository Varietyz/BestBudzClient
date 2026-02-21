package com.bestbudz.rendering;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.rendering.animation.SkinList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class SequenceFrame {

	private static final Logger LOGGER = Logger.getLogger(SequenceFrame.class.getName());

	private static final int ROTATION_DEFAULT = 128;
	private static final int POSITION_DEFAULT = 0;
	private static final int FRAME_MULTIPLIER = 3;

	public static SequenceFrame[][] animationlist;
	public final int version;
	public final SkinList skinList;
	public final int transformCount;
	public final int[] transformIndices;
	public final int[] xTransforms;
	public final int[] yTransforms;
	public final int[] zTransforms;

	public SequenceFrame(SkinList skinList, int frameCount, int[] indices,
						 int[] xValues, int[] yValues, int[] zValues) {
		this.version = 0;
		this.skinList = skinList;
		this.transformCount = frameCount;
		this.transformIndices = indices;
		this.xTransforms = xValues;
		this.yTransforms = yValues;
		this.zTransforms = zValues;
	}

	public SequenceFrame() {
		this.version = 0;
		this.skinList = null;
		this.transformCount = 0;
		this.transformIndices = null;
		this.xTransforms = null;
		this.yTransforms = null;
		this.zTransforms = null;
	}

	public static void load(int fileId, byte[] data) {
		loadFromJson(fileId);
	}

	private static boolean loadFromJson(int fileId) {
		JsonObject json = JsonCacheLoader.loadAnimFrameJson(fileId + ".json");
		if (json == null) return false;

		try {
			JsonObject skinJson = json.getAsJsonObject("skinList");
			SkinList skinList = new SkinList(skinJson);

			int frameCount = json.get("frameCount").getAsInt();
			if (frameCount < 0 || frameCount > 10000) {
				LOGGER.warning("Invalid JSON frame count " + frameCount + " for file " + fileId);
				return false;
			}

			animationlist[fileId] = new SequenceFrame[frameCount * FRAME_MULTIPLIER];

			int rawTransformCount = json.has("transformCount") ? json.get("transformCount").getAsInt() : 0;

			JsonArray frames = json.getAsJsonArray("frames");
			int loaded = 0;
			for (int i = 0; i < frames.size(); i++) {
				try {
					JsonObject frame = frames.get(i).getAsJsonObject();
					int fid = frame.get("frameId").getAsInt();
					if (fid < 0 || fid >= animationlist[fileId].length) continue;

					JsonArray transforms = frame.getAsJsonArray("transforms");
					int jsonTransformCount = frame.has("transformCount") ? frame.get("transformCount").getAsInt() : transforms.size();

					// Build a lookup of bone -> transform data from JSON
					// Max possible entries = json transforms + gap fills
					int maxEntries = jsonTransformCount + transforms.size();
					int[] indices = new int[maxEntries];
					int[] xValues = new int[maxEntries];
					int[] yValues = new int[maxEntries];
					int[] zValues = new int[maxEntries];
					int actualCount = 0;

					// Index the JSON transforms by bone for quick lookup
					int[][] boneData = new int[jsonTransformCount][];
					boolean[] hasBone = new boolean[jsonTransformCount];
					for (int j = 0; j < transforms.size(); j++) {
						JsonObject t = transforms.get(j).getAsJsonObject();
						int bone = t.get("bone").getAsInt();
						if (bone >= 0 && bone < jsonTransformCount) {
							hasBone[bone] = true;
							boneData[bone] = new int[] { t.get("x").getAsInt(), t.get("y").getAsInt(), t.get("z").getAsInt() };
						}
					}

					// Replay the gap-filling logic from binary loadSingleFrame
					int lastProcessedIndex = -1;
					for (int boneIdx = 0; boneIdx < jsonTransformCount; boneIdx++) {
						if (!hasBone[boneIdx]) continue;

						// Gap-fill: if this bone is NOT type 0, insert a default position
						// for the nearest preceding type-0 bone that hasn't been processed
						if (boneIdx < skinList.transformTypes.length && skinList.transformTypes[boneIdx] != 0) {
							for (int gapIndex = boneIdx - 1; gapIndex > lastProcessedIndex; gapIndex--) {
								if (gapIndex >= 0 && gapIndex < skinList.transformTypes.length
									&& skinList.transformTypes[gapIndex] == 0) {
									if (actualCount < maxEntries) {
										indices[actualCount] = gapIndex;
										xValues[actualCount] = POSITION_DEFAULT;
										yValues[actualCount] = POSITION_DEFAULT;
										zValues[actualCount] = POSITION_DEFAULT;
										actualCount++;
									}
									break;
								}
							}
						}

						if (actualCount < maxEntries) {
							indices[actualCount] = boneIdx;
							xValues[actualCount] = boneData[boneIdx][0];
							yValues[actualCount] = boneData[boneIdx][1];
							zValues[actualCount] = boneData[boneIdx][2];
							actualCount++;
						}
						lastProcessedIndex = boneIdx;
					}

					if (actualCount == 0) continue;

					// Trim arrays to actual size
					int[] finalIndices = new int[actualCount];
					int[] finalX = new int[actualCount];
					int[] finalY = new int[actualCount];
					int[] finalZ = new int[actualCount];
					System.arraycopy(indices, 0, finalIndices, 0, actualCount);
					System.arraycopy(xValues, 0, finalX, 0, actualCount);
					System.arraycopy(yValues, 0, finalY, 0, actualCount);
					System.arraycopy(zValues, 0, finalZ, 0, actualCount);

					animationlist[fileId][fid] = new SequenceFrame(skinList, actualCount, finalIndices,
						finalX, finalY, finalZ);
					loaded++;
				} catch (Exception e) {
					LOGGER.warning("Error loading JSON frame " + i + " in file " + fileId + ": " + e.getMessage());
				}
			}

			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to load JSON anim frames for file " + fileId, e);
			return false;
		}
	}


	public static void loader(int file, byte[] data) {
		load(file, data);
	}

	public static void nullLoader() {
		animationlist = null;
	}

	public static SequenceFrame getFrame(int encodedId) {
		try {
			int fileIndex, frameIndex;

			String hexString = Integer.toHexString(encodedId);
			if (hexString.length() < 4) {
				fileIndex = 0;
				frameIndex = encodedId;
			} else {
				int splitPoint = hexString.length() - 4;
				fileIndex = Integer.parseInt(hexString.substring(0, splitPoint), 16);
				frameIndex = Integer.parseInt(hexString.substring(splitPoint), 16);
			}

			if (fileIndex < 0 || frameIndex < 0 || fileIndex >= animationlist.length) {
				return null;
			}

			SequenceFrame[] frameArray = animationlist[fileIndex];
			if (frameArray == null || frameArray.length == 0) {
				if (loadFromJson(fileIndex)) {
					frameArray = animationlist[fileIndex];
				}
				if (frameArray == null || frameArray.length == 0) {
					return null;
				}
			}

			if (frameIndex >= frameArray.length) {
				return null;
			}

			return frameArray[frameIndex];

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error retrieving frame for ID: " + encodedId, e);
			return null;
		}
	}

	public static boolean isInvalidFrame(int frameId) {
		return frameId == -1;
	}

}
