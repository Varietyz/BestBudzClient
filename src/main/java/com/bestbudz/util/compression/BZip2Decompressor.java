package com.bestbudz.util.compression;

final class BZip2Decompressor
{

	public static int[] sharedWorkspace;

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

	byte[] inputBuffer;
	int inputPosition;
	int inputRemaining;
	int bytesRead;
	int overflow;

	byte[] outputBuffer;
	int outputPosition;
	int outputRemaining;

	int crcLow;
	int crcHigh;
	byte currentByte;
	int repeatCount;

	int bitBuffer;
	int bitsAvailable;

	int blockSize;
	int blockNumber;
	int origPtr;
	int currentIndex;
	int currentByteValue;
	int decoderPosition;
	int symbolCount;
	int totalSymbols;

	BZip2Decompressor() {

	}

	final void resetState() {
		java.util.Arrays.fill(symbolFrequencies, 0);
		java.util.Arrays.fill(symbolInUse, false);
		java.util.Arrays.fill(groupInUse, false);
		bitsAvailable = 0;
		bitBuffer = 0;
		symbolCount = 0;
	}
}
