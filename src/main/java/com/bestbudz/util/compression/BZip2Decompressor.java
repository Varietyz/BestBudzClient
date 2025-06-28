package com.bestbudz.util.compression;

final class BZip2Decompressor
{
	// Static shared buffer - optimize for single-threaded game usage
	public static int[] sharedWorkspace;

	// Pre-allocated arrays to avoid GC pressure
	final int[] symbolFrequencies = new int[256];
	final int[] cumulativeFrequencies = new int[257];
	final boolean[] symbolInUse = new boolean[256];
	final boolean[] groupInUse = new boolean[16];
	final byte[] symbolMap = new byte[256];
	final byte[] moveToFrontBuffer = new byte[4096];
	final int[] groupPointers = new int[16];
	final byte[] selectorList = new byte[18002];
	final byte[] selectorMtfList = new byte[18002];
	final byte[][] huffmanCodeLengths = new byte[6][258];
	final int[][] huffmanCodes = new int[6][258];
	final int[][] huffmanBases = new int[6][258];
	final int[][] huffmanPermutation = new int[6][258];
	final int[] huffmanMinLengths = new int[6];

	// I/O buffers
	byte[] inputBuffer;  // input buffer
	int inputPosition;          // input position
	int inputRemaining;          // input remaining
	int bytesRead;          // block counter
	int overflow;          // block overflow

	byte[] outputBuffer;  // output buffer
	int outputPosition;          // output position
	int outputRemaining;          // output remaining

	// State variables
	int crcLow;          // crc low
	int crcHigh;          // crc high
	byte currentByte;         // current byte
	int repeatCount;          // repeat count

	// Bit buffer for efficient bit reading
	int bitBuffer;          // bit buffer
	int bitsAvailable;          // bits available

	// Algorithm state
	int blockSize;          // block size
	int blockNumber;          // block number
	int origPtr;          // origPtr
	int currentIndex;          // current index
	int currentByteValue;          // current byte value
	int decoderPosition;          // decoder position
	int symbolCount;          // symbol count
	int totalSymbols;          // total symbols

	BZip2Decompressor() {
		// Constructor is empty - arrays initialized inline for better performance
	}

	// Optimized method to clear state arrays without creating new objects
	final void resetState() {
		java.util.Arrays.fill(symbolFrequencies, 0);
		java.util.Arrays.fill(symbolInUse, false);
		java.util.Arrays.fill(groupInUse, false);
		bitsAvailable = 0;
		bitBuffer = 0;
		symbolCount = 0;
	}
}