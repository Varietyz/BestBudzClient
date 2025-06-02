package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.animation.Class18;
import com.bestbudz.network.Stream;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Optimized SequenceFrame class for handling animation frame data.
 * Provides better performance, reliability, and maintainability.
 */
public final class SequenceFrame {

	private static final Logger LOGGER = Logger.getLogger(SequenceFrame.class.getName());

	// Constants for better readability
	private static final int TEMP_ARRAY_SIZE = 500;
	private static final int ROTATION_DEFAULT = 128;
	private static final int POSITION_DEFAULT = 0;
	private static final int FRAME_MULTIPLIER = 3;
	private static final int HEX_FRAME_ID_LENGTH = 4;
	private static final int HEX_RADIX = 16;

	// Frame data - made final where possible
	public static SequenceFrame[][] animationlist;
	public final int anInt636;
	public final Class18 aClass18_637;
	public final int anInt638;
	public final int[] anIntArray639;
	public final int[] anIntArray640;
	public final int[] anIntArray641;
	public final int[] anIntArray642;

	/**
	 * Private constructor for creating frame instances during loading.
	 */
	private SequenceFrame(Class18 skinList, int frameCount, int[] indices,
						  int[] xValues, int[] yValues, int[] zValues) {
		this.anInt636 = 0; // Default value, maintain compatibility
		this.aClass18_637 = skinList;
		this.anInt638 = frameCount;
		this.anIntArray639 = indices;
		this.anIntArray640 = xValues;
		this.anIntArray641 = yValues;
		this.anIntArray642 = zValues;
	}

	/**
	 * Legacy constructor for compatibility.
	 */
	public SequenceFrame() {
		this.anInt636 = 0;
		this.aClass18_637 = null;
		this.anInt638 = 0;
		this.anIntArray639 = null;
		this.anIntArray640 = null;
		this.anIntArray641 = null;
		this.anIntArray642 = null;
	}

	/**
	 * Optimized loader with better error handling and performance.
	 *
	 * @param fileId the file identifier
	 * @param data the byte array containing animation data
	 */
	public static void load(int fileId, byte[] data) {
		if (data == null || data.length == 0) {
			LOGGER.warning("Cannot load sequence frame: empty or null data for file " + fileId);
			return;
		}

		try {
			final Stream stream = new Stream(data);
			final Class18 skinList = new Class18(stream);
			final int frameCount = stream.readUnsignedWord();

			// Initialize animation list for this file
			animationlist[fileId] = new SequenceFrame[frameCount * FRAME_MULTIPLIER];

			// Pre-allocate temporary arrays once
			final FrameDataBuilder builder = new FrameDataBuilder();

			for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
				try {
					loadSingleFrame(stream, skinList, animationlist[fileId], builder);
				} catch (Exception e) {
					LOGGER.log(Level.WARNING,
						"Skipping corrupt sequence frame in file " + fileId +
							" at index " + frameIndex + ": " + e.getMessage(), e);
				}
			}

			LOGGER.info("Successfully loaded " + frameCount + " sequence frames for file " + fileId);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load sequence file " + fileId, e);
			// Ensure we don't leave a partially loaded state
			if (animationlist[fileId] != null) {
				animationlist[fileId] = new SequenceFrame[0];
			}
		}
	}

	/**
	 * Loads a single frame from the stream.
	 */
	private static void loadSingleFrame(Stream stream, Class18 skinList,
										SequenceFrame[] frameArray, FrameDataBuilder builder) {
		final int frameId = stream.readUnsignedWord();
		final int transformCount = stream.readUnsignedByte();

		builder.reset();
		int lastProcessedIndex = -1;

		// Process each transformation in the frame
		for (int i = 0; i < transformCount; i++) {
			final int transformFlags = stream.readUnsignedByte();

			if (transformFlags > 0) {
				processTransformation(stream, skinList, builder, i, transformFlags, lastProcessedIndex);
				lastProcessedIndex = i;
			}
		}

		// Create the frame with the collected data
		frameArray[frameId] = builder.createFrame(skinList);
	}

	/**
	 * Processes a single transformation within a frame.
	 */
	private static void processTransformation(Stream stream, Class18 skinList,
											  FrameDataBuilder builder, int currentIndex,
											  int transformFlags, int lastProcessedIndex) {

		// Fill gaps with default values if needed
		if (skinList.anIntArray342[currentIndex] != 0) {
			fillTransformationGaps(skinList, builder, currentIndex, lastProcessedIndex);
		}

		// Determine default value based on transformation type
		final int defaultValue = (skinList.anIntArray342[currentIndex] == 3) ?
			ROTATION_DEFAULT : POSITION_DEFAULT;

		// Read transformation values based on flags
		final int xValue = (transformFlags & 0x1) != 0 ? stream.readShort2() : defaultValue;
		final int yValue = (transformFlags & 0x2) != 0 ? stream.readShort2() : defaultValue;
		final int zValue = (transformFlags & 0x4) != 0 ? stream.readShort2() : defaultValue;

		builder.addTransformation(currentIndex, xValue, yValue, zValue);
	}

	/**
	 * Fills gaps in transformation data with default values.
	 */
	private static void fillTransformationGaps(Class18 skinList, FrameDataBuilder builder,
											   int currentIndex, int lastProcessedIndex) {
		for (int gapIndex = currentIndex - 1; gapIndex > lastProcessedIndex; gapIndex--) {
			if (skinList.anIntArray342[gapIndex] == 0) {
				builder.addTransformation(gapIndex, POSITION_DEFAULT, POSITION_DEFAULT, POSITION_DEFAULT);
				break;
			}
		}
	}

	/**
	 * Legacy loader method for backward compatibility.
	 * Delegates to the optimized load method.
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
	 * Optimized with better error handling and caching.
	 *
	 * @param encodedId the encoded frame identifier
	 * @return the sequence frame, or null if not found/available
	 */
	public static SequenceFrame method531(int encodedId) {
		try {
			final FrameIdentifier frameId = decodeFrameId(encodedId);

			if (frameId == null || !isValidFrameId(frameId)) {
				return null;
			}

			final SequenceFrame[] frameArray = animationlist[frameId.fileIndex];
			if (frameArray == null || frameArray.length == 0) {
				// Request loading if not available
				Client.instance.onDemandFetcher.method558(1, frameId.fileIndex);
				return null;
			}

			if (frameId.frameIndex >= frameArray.length) {
				LOGGER.warning("Frame index " + frameId.frameIndex +
					" exceeds array length " + frameArray.length +
					" for file " + frameId.fileIndex);
				return null;
			}

			return frameArray[frameId.frameIndex];

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error retrieving frame for ID: " + encodedId, e);
			return null;
		}
	}

	/**
	 * Decodes a frame identifier from its encoded form.
	 */
	private static FrameIdentifier decodeFrameId(int encodedId) {
		try {
			final String hexString = Integer.toHexString(encodedId);

			if (hexString.length() < HEX_FRAME_ID_LENGTH) {
				return new FrameIdentifier(0, encodedId);
			}

			final int splitPoint = hexString.length() - HEX_FRAME_ID_LENGTH;
			final int fileIndex = Integer.parseInt(hexString.substring(0, splitPoint), HEX_RADIX);
			final int frameIndex = Integer.parseInt(hexString.substring(splitPoint), HEX_RADIX);

			return new FrameIdentifier(fileIndex, frameIndex);

		} catch (NumberFormatException e) {
			LOGGER.warning("Invalid frame ID format: " + encodedId);
			return null;
		}
	}

	/**
	 * Validates frame identifier bounds.
	 */
	private static boolean isValidFrameId(FrameIdentifier frameId) {
		return frameId.fileIndex >= 0 &&
			frameId.frameIndex >= 0 &&
			frameId.fileIndex < animationlist.length;
	}

	/**
	 * Legacy method for checking if frame ID is invalid.
	 *
	 * @param frameId the frame identifier
	 * @return true if frame ID is invalid (-1)
	 */
	public static boolean method532(int frameId) {
		return frameId == -1;
	}

	/**
	 * Helper class for building frame data efficiently.
	 */
	private static final class FrameDataBuilder {
		private final int[] tempIndices = new int[TEMP_ARRAY_SIZE];
		private final int[] tempXValues = new int[TEMP_ARRAY_SIZE];
		private final int[] tempYValues = new int[TEMP_ARRAY_SIZE];
		private final int[] tempZValues = new int[TEMP_ARRAY_SIZE];
		private int count = 0;

		void reset() {
			count = 0;
		}

		void addTransformation(int index, int x, int y, int z) {
			if (count < TEMP_ARRAY_SIZE) {
				tempIndices[count] = index;
				tempXValues[count] = x;
				tempYValues[count] = y;
				tempZValues[count] = z;
				count++;
			}
		}

		SequenceFrame createFrame(Class18 skinList) {
			// Create properly sized arrays
			final int[] indices = new int[count];
			final int[] xValues = new int[count];
			final int[] yValues = new int[count];
			final int[] zValues = new int[count];

			// Copy data efficiently
			System.arraycopy(tempIndices, 0, indices, 0, count);
			System.arraycopy(tempXValues, 0, xValues, 0, count);
			System.arraycopy(tempYValues, 0, yValues, 0, count);
			System.arraycopy(tempZValues, 0, zValues, 0, count);

			return new SequenceFrame(skinList, count, indices, xValues, yValues, zValues);
		}
	}

	/**
	 * Helper class for frame identification.
	 */
	private static final class FrameIdentifier {
		final int fileIndex;
		final int frameIndex;

		FrameIdentifier(int fileIndex, int frameIndex) {
			this.fileIndex = fileIndex;
			this.frameIndex = frameIndex;
		}
	}
}