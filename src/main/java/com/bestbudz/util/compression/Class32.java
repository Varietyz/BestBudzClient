package com.bestbudz.util.compression;

final class Class32 {
	// Static shared buffer - optimize for single-threaded game usage
	public static int[] anIntArray587;

	// Pre-allocated arrays to avoid GC pressure
	final int[] anIntArray583 = new int[256];
	final int[] anIntArray585 = new int[257];
	final boolean[] aBooleanArray589 = new boolean[256];
	final boolean[] aBooleanArray590 = new boolean[16];
	final byte[] aByteArray591 = new byte[256];
	final byte[] aByteArray592 = new byte[4096];
	final int[] anIntArray593 = new int[16];
	final byte[] aByteArray594 = new byte[18002];
	final byte[] aByteArray595 = new byte[18002];
	final byte[][] aByteArrayArray596 = new byte[6][258];
	final int[][] anIntArrayArray597 = new int[6][258];
	final int[][] anIntArrayArray598 = new int[6][258];
	final int[][] anIntArrayArray599 = new int[6][258];
	final int[] anIntArray600 = new int[6];

	// I/O buffers
	byte[] aByteArray563;  // input buffer
	int anInt564;          // input position
	int anInt565;          // input remaining
	int anInt566;          // block counter
	int anInt567;          // block overflow

	byte[] aByteArray568;  // output buffer
	int anInt569;          // output position
	int anInt570;          // output remaining

	// State variables
	int anInt571;          // crc low
	int anInt572;          // crc high
	byte aByte573;         // current byte
	int anInt574;          // repeat count

	// Bit buffer for efficient bit reading
	int anInt576;          // bit buffer
	int anInt577;          // bits available

	// Algorithm state
	int anInt578;          // block size
	int anInt579;          // block number
	int anInt580;          // origPtr
	int anInt581;          // current index
	int anInt582;          // current byte value
	int anInt584;          // decoder position
	int anInt588;          // symbol count
	int anInt601;          // total symbols

	Class32() {
		// Constructor is empty - arrays initialized inline for better performance
	}

	// Optimized method to clear state arrays without creating new objects
	final void resetState() {
		java.util.Arrays.fill(anIntArray583, 0);
		java.util.Arrays.fill(aBooleanArray589, false);
		java.util.Arrays.fill(aBooleanArray590, false);
		anInt577 = 0;
		anInt576 = 0;
		anInt588 = 0;
	}
}