package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.animation.SkinList;
import com.bestbudz.network.Stream;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simplified SequenceFrame class for handling animation frame data.
 */
public final class SequenceFrame {

	private static final Logger LOGGER = Logger.getLogger(SequenceFrame.class.getName());

	// Constants
	private static final int ROTATION_DEFAULT = 128;
	private static final int POSITION_DEFAULT = 0;
	private static final int FRAME_MULTIPLIER = 3;

	// Frame data
	public static SequenceFrame[][] animationlist;
	public final int version;
	public final SkinList skinList;
	public final int transformCount;
	public final int[] transformIndices;
	public final int[] xTransforms;
	public final int[] yTransforms;
	public final int[] zTransforms;

	/**
	 * Main constructor for creating frame instances.
	 */
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

	/**
	 * Default constructor for compatibility.
	 */
	public SequenceFrame() {
		this.version = 0;
		this.skinList = null;
		this.transformCount = 0;
		this.transformIndices = null;
		this.xTransforms = null;
		this.yTransforms = null;
		this.zTransforms = null;
	}

	/**
	 * Loads animation frames from byte data.
	 */
	public static void load(int fileId, byte[] data) {
		if (data == null || data.length == 0) {
			LOGGER.warning("Cannot load sequence frame: empty or null data for file " + fileId);
			return;
		}

		try {
			Stream stream = new Stream(data);
			SkinList skinList = new SkinList(stream);

			// Check if we have enough data to read frame count
			if (stream.position + 2 > data.length) {
				LOGGER.warning("Insufficient data to read frame count for file " + fileId);
				return;
			}

			int frameCount = stream.readUnsignedWord();

			// Validate frame count is reasonable
			if (frameCount < 0 || frameCount > 10000) {
				LOGGER.warning("Invalid frame count " + frameCount + " for file " + fileId);
				return;
			}

			// Initialize animation list for this file
			animationlist[fileId] = new SequenceFrame[frameCount * FRAME_MULTIPLIER];

			int successfulFrames = 0;
			for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
				try {
					// Check if we have enough remaining data before attempting to load
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
					// Stop processing if we hit buffer overflow errors
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

	/**
	 * Loads a single frame from the stream.
	 */
	private static void loadSingleFrame(Stream stream, SkinList skinList, SequenceFrame[] frameArray) {
		// Check if we have enough data to read frame header
		if (stream.position + 3 > stream.buffer.length) {
			throw new ArrayIndexOutOfBoundsException("Not enough data to read frame header");
		}

		int frameId = stream.readUnsignedWord();
		int transformCount = stream.readUnsignedByte();

		// Validate frame ID
		if (frameId < 0 || frameId >= frameArray.length) {
			LOGGER.warning("Invalid frame ID " + frameId + ", skipping frame");
			return;
		}

		// Validate transform count
		if (transformCount < 0 || transformCount > 1000) {
			LOGGER.warning("Invalid transform count " + transformCount + " for frame " + frameId + ", skipping frame");
			return;
		}

		// Collect transformation data
		int[] indices = new int[transformCount];
		int[] xValues = new int[transformCount];
		int[] yValues = new int[transformCount];
		int[] zValues = new int[transformCount];
		int actualCount = 0;

		int lastProcessedIndex = -1;

		for (int i = 0; i < transformCount; i++) {
			// Check if we have data to read the transform flags
			if (stream.position >= stream.buffer.length) {
				LOGGER.warning("Reached end of buffer at transform " + i + " of " + transformCount);
				break;
			}

			int transformFlags = stream.readUnsignedByte();

			// Validate transform flags (should be 0-7 for 3 bits)
			if (transformFlags < 0 || transformFlags > 7) {
				LOGGER.warning("Invalid transform flags " + transformFlags + " at index " + i + ", skipping");
				continue;
			}

			if (transformFlags > 0) {
				// Validate skinList bounds
				if (i >= skinList.transformTypes.length) {
					LOGGER.warning("Transform index " + i + " exceeds skinList bounds, skipping");
					continue;
				}

				// Fill gaps with default values if needed
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

				// Determine default value based on transformation type
				int defaultValue = (skinList.transformTypes[i] == 3) ? ROTATION_DEFAULT : POSITION_DEFAULT;

				// Check if we have enough data to read the transformation values
				int bytesNeeded = 0;
				if ((transformFlags & 0x1) != 0) bytesNeeded += 2; // x value
				if ((transformFlags & 0x2) != 0) bytesNeeded += 2; // y value
				if ((transformFlags & 0x4) != 0) bytesNeeded += 2; // z value

				if (stream.position + bytesNeeded > stream.buffer.length) {
					LOGGER.warning("Not enough data to read transformation values");
					break;
				}

				// Read transformation values based on flags
				int xValue = (transformFlags & 0x1) != 0 ? stream.readSignedWordBE() : defaultValue;
				int yValue = (transformFlags & 0x2) != 0 ? stream.readSignedWordBE() : defaultValue;
				int zValue = (transformFlags & 0x4) != 0 ? stream.readSignedWordBE() : defaultValue;

				// Validate and clean transformation values
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

		// Skip creating frame if no valid transformations were found
		if (actualCount == 0) {
			LOGGER.warning("No valid transformations found for frame " + frameId + ", skipping");
			return;
		}

		// Create properly sized arrays for the actual data
		int[] finalIndices = new int[actualCount];
		int[] finalXValues = new int[actualCount];
		int[] finalYValues = new int[actualCount];
		int[] finalZValues = new int[actualCount];

		System.arraycopy(indices, 0, finalIndices, 0, actualCount);
		System.arraycopy(xValues, 0, finalXValues, 0, actualCount);
		System.arraycopy(yValues, 0, finalYValues, 0, actualCount);
		System.arraycopy(zValues, 0, finalZValues, 0, actualCount);

		// Check if the entire frame data appears corrupted
		if (isFrameDataCorrupted(finalIndices, finalXValues, finalYValues, finalZValues, actualCount)) {
			LOGGER.warning("Frame " + frameId + " appears to contain corrupted data, skipping");
			return;
		}

		// Create the frame only if we have valid data
		frameArray[frameId] = new SequenceFrame(skinList, actualCount, finalIndices,
			finalXValues, finalYValues, finalZValues);
	}

	/**
	 * Legacy loader method for backward compatibility.
	 */
	public static void loader(int file, byte[] data) {
		load(file, data);
	}

	/**
	 * Clears all animation data.
	 */
	public static void nullLoader() {
		animationlist = null;
	}

	/**
	 * Retrieves a sequence frame by its encoded identifier.
	 */
	public static SequenceFrame getFrame(int encodedId) {
		try {
			int fileIndex, frameIndex;

			// Decode the frame ID
			String hexString = Integer.toHexString(encodedId);
			if (hexString.length() < 4) {
				fileIndex = 0;
				frameIndex = encodedId;
			} else {
				int splitPoint = hexString.length() - 4;
				fileIndex = Integer.parseInt(hexString.substring(0, splitPoint), 16);
				frameIndex = Integer.parseInt(hexString.substring(splitPoint), 16);
			}

			// Validate bounds
			if (fileIndex < 0 || frameIndex < 0 || fileIndex >= animationlist.length) {
				return null;
			}

			SequenceFrame[] frameArray = animationlist[fileIndex];
			if (frameArray == null || frameArray.length == 0) {
				// Request loading if not available
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

	/**
	 * Legacy method for checking if frame ID is invalid.
	 */
	public static boolean isInvalidFrame(int frameId) {
		return frameId == -1;
	}

	/**
	 * Cleans and validates transformation values, removing corrupted data.
	 */
	private static int cleanTransformValue(int value, String axis, int frameId, int transformIndex) {
		// Check for extreme values that indicate corruption
		if (value < -32768 || value > 32767) {
			LOGGER.warning("Extreme " + axis + " value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", clamping to valid range");
			return Math.max(-32768, Math.min(32767, value));
		}

		// Check for suspicious patterns that might indicate corruption
		// Values like 0xFFFF, 0x0000 repeated might be corruption
		if (value == 0xFFFF || value == -1) {
			LOGGER.fine("Suspicious " + axis + " value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", replacing with default");
			return axis.equals("X") || axis.equals("Y") || axis.equals("Z") ?
				POSITION_DEFAULT : ROTATION_DEFAULT;
		}

		// For rotation values, ensure they're within reasonable range (0-255 typically)
		if (axis.equals("Rotation") && (value < 0 || value > 255)) {
			LOGGER.fine("Invalid rotation value " + value + " in frame " + frameId +
				" transform " + transformIndex + ", wrapping to valid range");
			return ((value % 256) + 256) % 256;
		}

		return value;
	}

	/**
	 * Additional validation to clean entire frames of obviously corrupted data.
	 */
	private static boolean isFrameDataCorrupted(int[] indices, int[] xValues, int[] yValues, int[] zValues, int count) {
		if (count == 0) return true;

		int zeroCount = 0;
		int extremeCount = 0;

		for (int i = 0; i < count; i++) {
			// Count zero values
			if (xValues[i] == 0 && yValues[i] == 0 && zValues[i] == 0) {
				zeroCount++;
			}

			// Count extreme values
			if (Math.abs(xValues[i]) > 10000 || Math.abs(yValues[i]) > 10000 || Math.abs(zValues[i]) > 10000) {
				extremeCount++;
			}
		}

		// If more than 80% of values are zero, likely corrupted
		if ((double)zeroCount / count > 0.8) {
			return true;
		}

		// If more than 50% of values are extreme, likely corrupted
		if ((double)extremeCount / count > 0.5) {
			return true;
		}

		return false;
	}
}