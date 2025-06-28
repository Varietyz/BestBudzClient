package com.bestbudz.util.compression;

public final class BZip2Compression
{
	// Only safe optimization: bit mask lookup table
	private static final int[] BIT_MASKS = {
		0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535
	};

	// EXACT same decompress as original
	public static void decompress(byte[] abyte0, int i, byte[] abyte1, int j, int k) {
		synchronized (decompressor) {
			decompressor.inputBuffer = abyte1;
			decompressor.inputPosition = k;
			decompressor.outputBuffer = abyte0;
			decompressor.outputPosition = 0;
			decompressor.inputRemaining = j;
			decompressor.outputRemaining = i;
			decompressor.bitsAvailable = 0;
			decompressor.bitBuffer = 0;
			decompressor.bytesRead = 0;
			decompressor.overflow = 0;
			decompressor.crcLow = 0;
			decompressor.crcHigh = 0;
			decompressor.blockNumber = 0;
			processStream();
			i -= decompressor.outputRemaining;
		}
	}

	// EXACT same decodeBlock as original
	private static void decodeBlock() {
		byte byte4 = BZip2Compression.decompressor.currentByte;
		int i = BZip2Compression.decompressor.repeatCount;
		int j = BZip2Compression.decompressor.decoderPosition;
		int k = BZip2Compression.decompressor.currentByteValue;
		int[] ai = BZip2Decompressor.sharedWorkspace;
		int l = BZip2Compression.decompressor.currentIndex;
		byte[] abyte0 = BZip2Compression.decompressor.outputBuffer;
		int i1 = BZip2Compression.decompressor.outputPosition;
		int j1 = BZip2Compression.decompressor.outputRemaining;
		int k1 = j1;
		int l1 = BZip2Compression.decompressor.totalSymbols + 1;
		label0:
		do {
			if (i > 0) {
				do {
					if (j1 == 0) break label0;
					if (i == 1) break;
					abyte0[i1] = byte4;
					i--;
					i1++;
					j1--;
				} while (true);
				abyte0[i1] = byte4;
				i1++;
				j1--;
			}
			boolean flag = true;
			while (flag) {
				flag = false;
				if (j == l1) {
					i = 0;
					break label0;
				}
				byte4 = (byte) k;
				l = ai[l];
				byte byte0 = (byte) (l & 0xff);
				l >>= 8;
				j++;
				if (byte0 != k) {
					k = byte0;
					if (j1 == 0) {
						i = 1;
					} else {
						abyte0[i1] = byte4;
						i1++;
						j1--;
						flag = true;
						continue;
					}
					break label0;
				}
				if (j != l1) continue;
				if (j1 == 0) {
					i = 1;
					break label0;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
				flag = true;
			}
			i = 2;
			l = ai[l];
			byte byte1 = (byte) (l & 0xff);
			l >>= 8;
			if (++j != l1)
				if (byte1 != k) {
					k = byte1;
				} else {
					i = 3;
					l = ai[l];
					byte byte2 = (byte) (l & 0xff);
					l >>= 8;
					if (++j != l1)
						if (byte2 != k) {
							k = byte2;
						} else {
							l = ai[l];
							byte byte3 = (byte) (l & 0xff);
							l >>= 8;
							j++;
							i = (byte3 & 0xff) + 4;
							l = ai[l];
							k = (byte) (l & 0xff);
							l >>= 8;
							j++;
						}
				}
		} while (true);
		int i2 = BZip2Compression.decompressor.crcLow;
		BZip2Compression.decompressor.crcLow += k1 - j1;
		if (BZip2Compression.decompressor.crcLow < i2) BZip2Compression.decompressor.crcHigh++;
		BZip2Compression.decompressor.currentByte = byte4;
		BZip2Compression.decompressor.repeatCount = i;
		BZip2Compression.decompressor.decoderPosition = j;
		BZip2Compression.decompressor.currentByteValue = k;
		BZip2Decompressor.sharedWorkspace = ai;
		BZip2Compression.decompressor.currentIndex = l;
		BZip2Compression.decompressor.outputPosition = i1;
		BZip2Compression.decompressor.outputRemaining = j1;
	}

	// EXACT same processStream as original
	private static void processStream() {
		int k8 = 0;
		int[] ai = null;
		int[] ai1 = null;
		int[] ai2 = null;
		BZip2Compression.decompressor.blockSize = 1;
		if (BZip2Decompressor.sharedWorkspace == null) BZip2Decompressor.sharedWorkspace = new int[BZip2Compression.decompressor.blockSize * 0x186a0];
		boolean flag19 = true;
		while (flag19) {
			byte byte0 = readByte();
			if (byte0 == 23) return;
			readByte();
			readByte();
			readByte();
			readByte();
			readByte();
			BZip2Compression.decompressor.blockNumber++;
			readByte();
			readByte();
			readByte();
			readByte();
			readBit();
			BZip2Compression.decompressor.origPtr = 0;
			byte0 = readByte();
			BZip2Compression.decompressor.origPtr = BZip2Compression.decompressor.origPtr << 8 | byte0 & 0xff;
			byte0 = readByte();
			BZip2Compression.decompressor.origPtr = BZip2Compression.decompressor.origPtr << 8 | byte0 & 0xff;
			byte0 = readByte();
			BZip2Compression.decompressor.origPtr = BZip2Compression.decompressor.origPtr << 8 | byte0 & 0xff;
			for (int j = 0; j < 16; j++) {
				byte byte1 = readBit();
				BZip2Compression.decompressor.groupInUse[j] = byte1 == 1;
			}

			for (int k = 0; k < 256; k++) BZip2Compression.decompressor.symbolInUse[k] = false;

			for (int l = 0; l < 16; l++)
				if (BZip2Compression.decompressor.groupInUse[l]) {
					for (int i3 = 0; i3 < 16; i3++) {
						byte byte2 = readBit();
						if (byte2 == 1) BZip2Compression.decompressor.symbolInUse[l * 16 + i3] = true;
					}
				}

			buildSymbolMap();
			int i4 = BZip2Compression.decompressor.symbolCount + 2;
			int j4 = readBits(3, BZip2Compression.decompressor);
			int k4 = readBits(15, BZip2Compression.decompressor);
			for (int i1 = 0; i1 < k4; i1++) {
				int j3 = 0;
				do {
					byte byte3 = readBit();
					if (byte3 == 0) break;
					j3++;
				} while (true);
				BZip2Compression.decompressor.selectorMtfList[i1] = (byte) j3;
			}

			byte[] abyte0 = new byte[6];
			for (byte byte16 = 0; byte16 < j4; byte16++) abyte0[byte16] = byte16;

			for (int j1 = 0; j1 < k4; j1++) {
				byte byte17 = BZip2Compression.decompressor.selectorMtfList[j1];
				byte byte15 = abyte0[byte17];
				for (; byte17 > 0; byte17--) abyte0[byte17] = abyte0[byte17 - 1];

				abyte0[0] = byte15;
				BZip2Compression.decompressor.selectorList[j1] = byte15;
			}

			for (int k3 = 0; k3 < j4; k3++) {
				int l6 = readBits(5, BZip2Compression.decompressor);
				for (int k1 = 0; k1 < i4; k1++) {
					do {
						byte byte4 = readBit();
						if (byte4 == 0) break;
						byte4 = readBit();
						if (byte4 == 0) l6++;
						else l6--;
					} while (true);
					BZip2Compression.decompressor.huffmanCodeLengths[k3][k1] = (byte) l6;
				}
			}

			for (int l3 = 0; l3 < j4; l3++) {
				byte byte8 = 32;
				int i = 0;
				for (int l1 = 0; l1 < i4; l1++) {
					if (BZip2Compression.decompressor.huffmanCodeLengths[l3][l1] > i) i = BZip2Compression.decompressor.huffmanCodeLengths[l3][l1];
					if (BZip2Compression.decompressor.huffmanCodeLengths[l3][l1] < byte8)
						byte8 = BZip2Compression.decompressor.huffmanCodeLengths[l3][l1];
				}

				buildHuffmanTables(
					BZip2Compression.decompressor.huffmanCodes[l3],
					BZip2Compression.decompressor.huffmanBases[l3],
					BZip2Compression.decompressor.huffmanPermutation[l3],
					BZip2Compression.decompressor.huffmanCodeLengths[l3],
					byte8,
					i,
					i4);
				BZip2Compression.decompressor.huffmanMinLengths[l3] = byte8;
			}

			int l4 = BZip2Compression.decompressor.symbolCount + 1;
			int i5 = -1;
			int j5 = 0;
			for (int i2 = 0; i2 <= 255; i2++) BZip2Compression.decompressor.symbolFrequencies[i2] = 0;

			int j9 = 4095;
			for (int l8 = 15; l8 >= 0; l8--) {
				for (int i9 = 15; i9 >= 0; i9--) {
					BZip2Compression.decompressor.moveToFrontBuffer[j9] = (byte) (l8 * 16 + i9);
					j9--;
				}

				BZip2Compression.decompressor.groupPointers[l8] = j9 + 1;
			}

			int i6 = 0;
			i5++;
			j5 = 50;
			byte byte12 = BZip2Compression.decompressor.selectorList[i5];
			k8 = BZip2Compression.decompressor.huffmanMinLengths[byte12];
			ai = BZip2Compression.decompressor.huffmanCodes[byte12];
			ai2 = BZip2Compression.decompressor.huffmanPermutation[byte12];
			ai1 = BZip2Compression.decompressor.huffmanBases[byte12];
			j5--;
			int i7 = k8;
			int l7;
			byte byte9;
			for (l7 = readBits(i7, BZip2Compression.decompressor); l7 > ai[i7]; l7 = l7 << 1 | byte9) {
				i7++;
				byte9 = readBit();
			}

			for (int k5 = ai2[l7 - ai1[i7]]; k5 != l4; )
				if (k5 == 0 || k5 == 1) {
					int j6 = -1;
					int k6 = 1;
					do {
						if (k5 == 0) j6 += k6;
						else j6 += 2 * k6;
						k6 *= 2;
						if (j5 == 0) {
							i5++;
							j5 = 50;
							byte byte13 = BZip2Compression.decompressor.selectorList[i5];
							k8 = BZip2Compression.decompressor.huffmanMinLengths[byte13];
							ai = BZip2Compression.decompressor.huffmanCodes[byte13];
							ai2 = BZip2Compression.decompressor.huffmanPermutation[byte13];
							ai1 = BZip2Compression.decompressor.huffmanBases[byte13];
						}
						j5--;
						int j7 = k8;
						int i8;
						byte byte10;
						for (i8 = readBits(j7, BZip2Compression.decompressor); i8 > ai[j7]; i8 = i8 << 1 | byte10) {
							j7++;
							byte10 = readBit();
						}

						k5 = ai2[i8 - ai1[j7]];
					} while (k5 == 0 || k5 == 1);
					j6++;
					byte byte5 =
						BZip2Compression.decompressor.symbolMap[BZip2Compression.decompressor.moveToFrontBuffer[BZip2Compression.decompressor.groupPointers[0]] & 0xff];
					BZip2Compression.decompressor.symbolFrequencies[byte5 & 0xff] += j6;
					for (; j6 > 0; j6--) {
						BZip2Decompressor.sharedWorkspace[i6] = byte5 & 0xff;
						i6++;
					}

				} else {
					int j11 = k5 - 1;
					byte byte6;
					if (j11 < 16) {
						int j10 = BZip2Compression.decompressor.groupPointers[0];
						byte6 = BZip2Compression.decompressor.moveToFrontBuffer[j10 + j11];
						for (; j11 > 3; j11 -= 4) {
							int k11 = j10 + j11;
							BZip2Compression.decompressor.moveToFrontBuffer[k11] = BZip2Compression.decompressor.moveToFrontBuffer[k11 - 1];
							BZip2Compression.decompressor.moveToFrontBuffer[k11 - 1] = BZip2Compression.decompressor.moveToFrontBuffer[k11 - 2];
							BZip2Compression.decompressor.moveToFrontBuffer[k11 - 2] = BZip2Compression.decompressor.moveToFrontBuffer[k11 - 3];
							BZip2Compression.decompressor.moveToFrontBuffer[k11 - 3] = BZip2Compression.decompressor.moveToFrontBuffer[k11 - 4];
						}

						for (; j11 > 0; j11--)
							BZip2Compression.decompressor.moveToFrontBuffer[j10 + j11] = BZip2Compression.decompressor.moveToFrontBuffer[(j10 + j11) - 1];

						BZip2Compression.decompressor.moveToFrontBuffer[j10] = byte6;
					} else {
						int l10 = j11 / 16;
						int i11 = j11 % 16;
						int k10 = BZip2Compression.decompressor.groupPointers[l10] + i11;
						byte6 = BZip2Compression.decompressor.moveToFrontBuffer[k10];
						for (; k10 > BZip2Compression.decompressor.groupPointers[l10]; k10--)
							BZip2Compression.decompressor.moveToFrontBuffer[k10] = BZip2Compression.decompressor.moveToFrontBuffer[k10 - 1];

						BZip2Compression.decompressor.groupPointers[l10]++;
						for (; l10 > 0; l10--) {
							BZip2Compression.decompressor.groupPointers[l10]--;
							BZip2Compression.decompressor.moveToFrontBuffer[BZip2Compression.decompressor.groupPointers[l10]] =
								BZip2Compression.decompressor.moveToFrontBuffer[(BZip2Compression.decompressor.groupPointers[l10 - 1] + 16) - 1];
						}

						BZip2Compression.decompressor.groupPointers[0]--;
						BZip2Compression.decompressor.moveToFrontBuffer[BZip2Compression.decompressor.groupPointers[0]] = byte6;
						if (BZip2Compression.decompressor.groupPointers[0] == 0) {
							int i10 = 4095;
							for (int k9 = 15; k9 >= 0; k9--) {
								for (int l9 = 15; l9 >= 0; l9--) {
									BZip2Compression.decompressor.moveToFrontBuffer[i10] =
										BZip2Compression.decompressor.moveToFrontBuffer[BZip2Compression.decompressor.groupPointers[k9] + l9];
									i10--;
								}

								BZip2Compression.decompressor.groupPointers[k9] = i10 + 1;
							}
						}
					}
					BZip2Compression.decompressor.symbolFrequencies[BZip2Compression.decompressor.symbolMap[byte6 & 0xff] & 0xff]++;
					BZip2Decompressor.sharedWorkspace[i6] = BZip2Compression.decompressor.symbolMap[byte6 & 0xff] & 0xff;
					i6++;
					if (j5 == 0) {
						i5++;
						j5 = 50;
						byte byte14 = BZip2Compression.decompressor.selectorList[i5];
						k8 = BZip2Compression.decompressor.huffmanMinLengths[byte14];
						ai = BZip2Compression.decompressor.huffmanCodes[byte14];
						ai2 = BZip2Compression.decompressor.huffmanPermutation[byte14];
						ai1 = BZip2Compression.decompressor.huffmanBases[byte14];
					}
					j5--;
					int k7 = k8;
					int j8;
					byte byte11;
					for (j8 = readBits(k7, BZip2Compression.decompressor); j8 > ai[k7]; j8 = j8 << 1 | byte11) {
						k7++;
						byte11 = readBit();
					}

					k5 = ai2[j8 - ai1[k7]];
				}

			BZip2Compression.decompressor.repeatCount = 0;
			BZip2Compression.decompressor.currentByte = 0;
			BZip2Compression.decompressor.cumulativeFrequencies[0] = 0;
			System.arraycopy(BZip2Compression.decompressor.symbolFrequencies, 0, BZip2Compression.decompressor.cumulativeFrequencies, 1, 256);

			for (int k2 = 1; k2 <= 256; k2++) BZip2Compression.decompressor.cumulativeFrequencies[k2] += BZip2Compression.decompressor.cumulativeFrequencies[k2 - 1];

			for (int l2 = 0; l2 < i6; l2++) {
				byte byte7 = (byte) (BZip2Decompressor.sharedWorkspace[l2] & 0xff);
				BZip2Decompressor.sharedWorkspace[BZip2Compression.decompressor.cumulativeFrequencies[byte7 & 0xff]] |= l2 << 8;
				BZip2Compression.decompressor.cumulativeFrequencies[byte7 & 0xff]++;
			}

			BZip2Compression.decompressor.currentIndex = BZip2Decompressor.sharedWorkspace[BZip2Compression.decompressor.origPtr] >> 8;
			BZip2Compression.decompressor.decoderPosition = 0;
			BZip2Compression.decompressor.currentIndex = BZip2Decompressor.sharedWorkspace[BZip2Compression.decompressor.currentIndex];
			BZip2Compression.decompressor.currentByteValue = (byte) (BZip2Compression.decompressor.currentIndex & 0xff);
			BZip2Compression.decompressor.currentIndex >>= 8;
			BZip2Compression.decompressor.decoderPosition++;
			BZip2Compression.decompressor.totalSymbols = i6;
			decodeBlock();
			flag19 = BZip2Compression.decompressor.decoderPosition == BZip2Compression.decompressor.totalSymbols + 1 && BZip2Compression.decompressor.repeatCount == 0;
		}
	}

	// EXACT same readByte as original
	private static byte readByte() {
		return (byte) readBits(8, BZip2Compression.decompressor);
	}

	// EXACT same readBit as original
	private static byte readBit() {
		return (byte) readBits(1, BZip2Compression.decompressor);
	}

	// ONLY optimization: use BIT_MASKS lookup instead of (1 << i) - 1
	private static int readBits(int i, BZip2Decompressor BZip2Decompressor) {
		int j;
		do {
			if (BZip2Decompressor.bitsAvailable >= i) {
				int k = BZip2Decompressor.bitBuffer >> BZip2Decompressor.bitsAvailable - i & BIT_MASKS[i];
				BZip2Decompressor.bitsAvailable -= i;
				j = k;
				break;
			}
			BZip2Decompressor.bitBuffer = BZip2Decompressor.bitBuffer << 8 | BZip2Decompressor.inputBuffer[BZip2Decompressor.inputPosition] & 0xff;
			BZip2Decompressor.bitsAvailable += 8;
			BZip2Decompressor.inputPosition++;
			BZip2Decompressor.inputRemaining--;
			BZip2Decompressor.bytesRead++;
			if (BZip2Decompressor.bytesRead == 0) BZip2Decompressor.overflow++;
		} while (true);
		return j;
	}

	// EXACT same buildSymbolMap as original
	private static void buildSymbolMap() {
		BZip2Compression.decompressor.symbolCount = 0;
		for (int i = 0; i < 256; i++)
			if (BZip2Compression.decompressor.symbolInUse[i]) {
				BZip2Compression.decompressor.symbolMap[BZip2Compression.decompressor.symbolCount] = (byte) i;
				BZip2Compression.decompressor.symbolCount++;
			}
	}

	// EXACT same buildHuffmanTables as original
	private static void buildHuffmanTables(int[] ai, int[] ai1, int[] ai2, byte[] abyte0, int i, int j, int k) {
		int l = 0;
		for (int i1 = i; i1 <= j; i1++) {
			for (int l2 = 0; l2 < k; l2++)
				if (abyte0[l2] == i1) {
					ai2[l] = l2;
					l++;
				}
		}

		for (int j1 = 0; j1 < 23; j1++) ai1[j1] = 0;

		for (int k1 = 0; k1 < k; k1++) ai1[abyte0[k1] + 1]++;

		for (int l1 = 1; l1 < 23; l1++) ai1[l1] += ai1[l1 - 1];

		for (int i2 = 0; i2 < 23; i2++) ai[i2] = 0;

		int i3 = 0;
		for (int j2 = i; j2 <= j; j2++) {
			i3 += ai1[j2 + 1] - ai1[j2];
			ai[j2] = i3 - 1;
			i3 <<= 1;
		}

		for (int k2 = i + 1; k2 <= j; k2++) ai1[k2] = (ai[k2 - 1] + 1 << 1) - ai1[k2];
	}

	// EXACT same static field as original
	private static final BZip2Decompressor decompressor = new BZip2Decompressor();
}