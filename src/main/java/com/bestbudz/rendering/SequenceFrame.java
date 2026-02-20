package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.animation.SkinList;
import com.bestbudz.network.Stream;

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
		if (data == null || data.length == 0) {
			LOGGER.warning("Cannot load sequence frame: empty or null data for file " + fileId);
			return;
		}

		try {
			Stream stream = new Stream(data);
			SkinList skinList = new SkinList(stream);

			if (stream.position + 2 > data.length) {
				LOGGER.warning("Insufficient data to read frame count for file " + fileId);
				return;
			}

			int frameCount = stream.readUnsignedWord();

			if (frameCount < 0 || frameCount > 10000) {
				LOGGER.warning("Invalid frame count " + frameCount + " for file " + fileId);
				return;
			}

			animationlist[fileId] = new SequenceFrame[frameCount * FRAME_MULTIPLIER];

			int successfulFrames = 0;
			for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
				try {

					if (stream.position >= data.length) {
						LOGGER.warning("Reached end of data at frame " + frameIndex + " of " + frameCount +
							" for file " + fileId + ". Successfully loaded " + successfulFrames + " frames.");
						break;
					}

					loadSingleFrame(stream, skinList, animationlist[fileId]);
					successfulFrames++;
				} catch (Exception e) {
					LOGGER.log(Level.WARNING,
						"Skipping corrupt sequence frame in file " + fileId +
							" at index " + frameIndex + ": " + e.getMessage(), e);

					if (e instanceof ArrayIndexOutOfBoundsException) {
						LOGGER.warning("Buffer overflow detected, stopping frame loading for file " + fileId);
						break;
					}
				}
			}

			LOGGER.info("Loaded " + successfulFrames + " of " + frameCount + " frames for file " + fileId);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load sequence file " + fileId, e);
			if (animationlist[fileId] != null) {
				animationlist[fileId] = new SequenceFrame[0];
			}
		}
	}

	private static void loadSingleFrame(Stream stream, SkinList skinList, SequenceFrame[] frameArray) {

		if (stream.position + 3 > stream.buffer.length) {
			throw new ArrayIndexOutOfBoundsException("Not enough data to read frame header");
		}

		int frameId = stream.readUnsignedWord();
		int transformCount = stream.readUnsignedByte();

		if (frameId < 0 || frameId >= frameArray.length) {
			LOGGER.warning("Invalid frame ID " + frameId + ", skipping frame");
			return;
		}

		if (transformCount < 0 || transformCount > 1000) {
			LOGGER.warning("Invalid transform count " + transformCount + " for frame " + frameId + ", skipping frame");
			return;
		}

		int[] indices = new int[transformCount];
		int[] xValues = new int[transformCount];
		int[] yValues = new int[transformCount];
		int[] zValues = new int[transformCount];
		int actualCount = 0;

		int lastProcessedIndex = -1;

		for (int i = 0; i < transformCount; i++) {

			if (stream.position >= stream.buffer.length) {
				LOGGER.warning("Reached end of buffer at transform " + i + " of " + transformCount);
				break;
			}

			int transformFlags = stream.readUnsignedByte();

			if (transformFlags < 0 || transformFlags > 7) {
				LOGGER.warning("Invalid transform flags " + transformFlags + " at index " + i + ", skipping");
				continue;
			}

			if (transformFlags > 0) {

				if (i >= skinList.transformTypes.length) {
					LOGGER.warning("Transform index " + i + " exceeds skinList bounds, skipping");
					continue;
				}

				if (skinList.transformTypes[i] != 0) {
					for (int gapIndex = i - 1; gapIndex > lastProcessedIndex; gapIndex--) {
						if (gapIndex >= 0 && gapIndex < skinList.transformTypes.length &&
							skinList.transformTypes[gapIndex] == 0) {
							if (actualCount < transformCount) {
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

				int defaultValue = (skinList.transformTypes[i] == 3) ? ROTATION_DEFAULT : POSITION_DEFAULT;

				int bytesNeeded = 0;
				if ((transformFlags & 0x1) != 0) bytesNeeded += 2;
				if ((transformFlags & 0x2) != 0) bytesNeeded += 2;
				if ((transformFlags & 0x4) != 0) bytesNeeded += 2;

				if (stream.position + bytesNeeded > stream.buffer.length) {
					LOGGER.warning("Not enough data to read transformation values");
					break;
				}

				int xValue = (transformFlags & 0x1) != 0 ? stream.readSignedWordBE() : defaultValue;
				int yValue = (transformFlags & 0x2) != 0 ? stream.readSignedWordBE() : defaultValue;
				int zValue = (transformFlags & 0x4) != 0 ? stream.readSignedWordBE() : defaultValue;

				xValue = cleanTransformValue(xValue, "X", frameId, i);
				yValue = cleanTransformValue(yValue, "Y", frameId, i);
				zValue = cleanTransformValue(zValue, "Z", frameId, i);

				if (actualCount < transformCount) {
					indices[actualCount] = i;
					xValues[actualCount] = xValue;
					yValues[actualCount] = yValue;
					zValues[actualCount] = zValue;
					actualCount++;
				}

				lastProcessedIndex = i;
			}
		}

		if (actualCount == 0) {
			LOGGER.warning("No valid transformations found for frame " + frameId + ", skipping");
			return;
		}

		int[] finalIndices = new int[actualCount];
		int[] finalXValues = new int[actualCount];
		int[] finalYValues = new int[actualCount];
		int[] finalZValues = new int[actualCount];

		System.arraycopy(indices, 0, finalIndices, 0, actualCount);
		System.arraycopy(xValues, 0, finalXValues, 0, actualCount);
		System.arraycopy(yValues, 0, finalYValues, 0, actualCount);
		System.arraycopy(zValues, 0, finalZValues, 0, actualCount);

		if (isFrameDataCorrupted(finalIndices, finalXValues, finalYValues, finalZValues, actualCount)) {
			LOGGER.warning("Frame " + frameId + " appears to contain corrupted data, skipping");
			return;
		}

		frameArray[frameId] = new SequenceFrame(skinList, actualCount, finalIndices,
			finalXValues, finalYValues, finalZValues);
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

				Client.instance.cacheManager.enqueueRequest(1, fileIndex);
				return null;
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

	private static int cleanTransformValue(int value, String axis, int frameId, int transformIndex) {

		if (value < -32768 || value > 32767) {
			LOGGER.warning("Extreme " + axis + " value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", clamping to valid range");
			return Math.max(-32768, Math.min(32767, value));
		}

		if (value == 0xFFFF || value == -1) {
			LOGGER.fine("Suspicious " + axis + " value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", replacing with default");
			return axis.equals("X") || axis.equals("Y") || axis.equals("Z") ?
				POSITION_DEFAULT : ROTATION_DEFAULT;
		}

		if (axis.equals("Rotation") && (value < 0 || value > 255)) {
			LOGGER.fine("Invalid rotation value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", wrapping to valid range");
			return ((value % 256) + 256) % 256;
		}

		return value;
	}

	private static boolean isFrameDataCorrupted(int[] indices, int[] xValues, int[] yValues, int[] zValues, int count) {
		if (count == 0) return true;

		int zeroCount = 0;
		int extremeCount = 0;

		for (int i = 0; i < count; i++) {

			if (xValues[i] == 0 && yValues[i] == 0 && zValues[i] == 0) {
				zeroCount++;
			}

			if (Math.abs(xValues[i]) > 10000 || Math.abs(yValues[i]) > 10000 || Math.abs(zValues[i]) > 10000) {
				extremeCount++;
			}
		}

		if ((double)zeroCount / count > 0.8) {
			return true;
		}

		if ((double)extremeCount / count > 0.5) {
			return true;
		}

		return false;
	}
}
