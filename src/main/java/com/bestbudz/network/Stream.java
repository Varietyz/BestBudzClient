package com.bestbudz.network;

import com.bestbudz.cache.Signlink;
import com.bestbudz.util.ISAACRandomGen;
import java.math.BigInteger;

public final class Stream
{

	private static final int[] BIT_MASKS = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };
	public byte[] buffer;
	public int position;
	public int bitOffset;
	public ISAACRandomGen encryption;

	private Stream() {
	}

	public Stream(byte[] abyte0) {
		buffer = abyte0;
		position = 0;
	}

	public static Stream getPooledStream() {
		Stream stream = new Stream();
		stream.position = 0;
		stream.buffer = new byte[5000];
		return stream;
	}

	public int readSignedWordBE() {
		position += 2;
		int i = ((buffer[position - 2] & 0xff) << 8) + (buffer[position - 1] & 0xff);
		if (i > 60000)
			i = -65535 + i;
		return i;

	}

	public int read3BytesBE(int i) {
		position += 3;
		return (0xff & buffer[position - 3] << 16) + (0xff & buffer[position - 2] << 8) + (0xff & buffer[position - 1]);
	}

	public int readLargeSmart() {
		int baseVal = 0;
		int lastVal;
		while ((lastVal = readSmartUnsigned()) == 32767) {
			baseVal += 32767;
		}
		return baseVal + lastVal;
	}

	public void writeEncryptedOpcode(int i) {
		 //System.out.println("Frame: " + i);
		buffer[position++] = (byte) (i + encryption.getNextKey());
	}

	public void writeByte(int i) {
		buffer[position++] = (byte) i;
	}

	public void writeWord(int i) {
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
	}

	public void writeDWordBigEndian(int i) {
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
	}

	public void writeDWord(int i) {
		buffer[position++] = (byte) (i >> 24);
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
	}

	public void writeQWord(long l) {
		try {
			buffer[position++] = (byte) (int) (l >> 56);
			buffer[position++] = (byte) (int) (l >> 48);
			buffer[position++] = (byte) (int) (l >> 40);
			buffer[position++] = (byte) (int) (l >> 32);
			buffer[position++] = (byte) (int) (l >> 24);
			buffer[position++] = (byte) (int) (l >> 16);
			buffer[position++] = (byte) (int) (l >> 8);
			buffer[position++] = (byte) (int) l;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("14395, " + 5 + ", " + l + ", " + runtimeexception);
			throw new RuntimeException();
		}
	}

	public void writeString(String s) {
		s.getBytes();
		System.arraycopy(s.getBytes(), 0, buffer, position, s.length());
		position += s.length();
		buffer[position++] = 10;
	}

	public void writePacketLength(byte[] abyte0, int i, int j) {
		for (int k = j; k < j + i; k++)
			buffer[position++] = abyte0[k];
	}

	public void writePacketLength(int i) {
		buffer[position - i - 1] = (byte) i;
	}

	public int readUnsignedByte() {
		if (position >= buffer.length) {
			System.err.println("Warning: readUnsignedByte overflow at offset=" + position + ", buffer length=" + buffer.length);
			return 0;
		}
		return buffer[position++] & 0xff;
	}

	public byte readSignedByte() {
		if (position >= buffer.length) {
			System.err.println("Warning: readSignedByte overflow at offset=" + position + ", buffer length=" + buffer.length);
			return 0;
		}
		return buffer[position++];
	}

	public int readUnsignedWord() {
		if (position + 1 >= buffer.length) {
			System.err.println("Warning: readUnsignedWord overflow at offset=" + position + ", buffer length=" + buffer.length);
			return 0;
		}
		int value = ((buffer[position] & 0xff) << 8) + (buffer[position + 1] & 0xff);
		position += 2;
		return value;
	}

	public int readSignedWord() {
		if (position + 1 >= buffer.length) {
			System.err.println("Warning: readSignedWord overflow at offset=" + position + ", buffer length=" + buffer.length);
			return -1;
		}
		int i = ((buffer[position] & 0xff) << 8) + (buffer[position + 1] & 0xff);
		position += 2;
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int read3Bytes() {
		if (position + 2 >= buffer.length) {
			System.err.println("Warning: read3Bytes overflow at offset=" + position + ", buffer length=" + buffer.length);
			return 0;
		}

		position += 3;
		return ((buffer[position - 3] & 0xff) << 16) +
			((buffer[position - 2] & 0xff) << 8) +
			(buffer[position - 1] & 0xff);
	}

	public int readDWord() {
		if (position + 3 >= buffer.length) {
			System.err.println("Warning: readDWord overflow at offset=" + position + ", buffer length=" + buffer.length);
			return 0;
		}

		position += 4;
		return ((buffer[position - 4] & 0xff) << 24) +
			((buffer[position - 3] & 0xff) << 16) +
			((buffer[position - 2] & 0xff) << 8) +
			(buffer[position - 1] & 0xff);
	}

	public long readQWord() {
		long l = (long) readDWord() & 0xffffffffL;
		long l1 = (long) readDWord() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String readString() {
		int i = position;
		while (buffer[position++] != 10)
			;
		return new String(buffer, i, position - i - 1);
	}

	public byte[] readBytes() {
		int i = position;
		while (buffer[position++] != 10);
		byte[] abyte0 = new byte[position - i - 1];
		System.arraycopy(buffer, i, abyte0, 0, position - 1 - i);
		return abyte0;
	}

	public void readBytes(int i, int j, byte[] abyte0) {
		for (int l = j; l < j + i; l++)
			abyte0[l] = buffer[position++];
	}

	public void initBitAccess() {
		bitOffset = position * 8;
	}

	public int readBits(int i) {
		int k = bitOffset >> 3;
		int l = 8 - (bitOffset & 7);
		int i1 = 0;
		bitOffset += i;
		for (; i > l; l = 8) {
			i1 += (buffer[k++] & BIT_MASKS[l]) << i - l;
			i -= l;
		}
		if (i == l)
			i1 += buffer[k] & BIT_MASKS[l];
		else
			i1 += buffer[k] >> l - i & BIT_MASKS[i];
		return i1;
	}

	public void finishBitAccess() {
		position = (bitOffset + 7) / 8;
	}

	public int readSmartSigned() {
		int i = buffer[position] & 0xff;
		if (i < 128)
			return readUnsignedByte() - 64;
		else
			return readUnsignedWord() - 49152;
	}

	public int readSmartUnsigned() {
		int i = buffer[position] & 0xff;
		if (i < 128)
			return readUnsignedByte();
		else
			return readUnsignedWord() - 32768;
	}

	public void applyRSAEncryption() {
		int i = position;
		position = 0;
		byte[] abyte0 = new byte[i];
		readBytes(i, 0, abyte0);
		byte[] abyte1 = new BigInteger(abyte0).toByteArray();
		position = 0;
		writeByte(abyte1.length);
		writePacketLength(abyte1, abyte1.length, 0);
	}

	public void writeByteNegated(int i) {
		buffer[position++] = (byte) (-i);
	}

	public void writeByte128Minus(int j) {
		buffer[position++] = (byte) (128 - j);
	}

	public int readByteSubtract128() {
		return buffer[position++] - 128 & 0xff;
	}

	public int readByteNegated() {
		return -buffer[position++] & 0xff;
	}

	public int readByte128Minus() {
		return 128 - buffer[position++] & 0xff;
	}

	public byte readSignedByteNegated() {
		return (byte) (-buffer[position++]);
	}

	public byte readSignedByte128Minus() {
		return (byte) (128 - buffer[position++]);
	}

	public void writeWordLittleEndian(int i) {
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 8);
	}

	public void writeWordMixed(int j) {
		buffer[position++] = (byte) (j >> 8);
		buffer[position++] = (byte) (j + 128);
	}

	public void writeWordMixedLE(int j) {
		buffer[position++] = (byte) (j + 128);
		buffer[position++] = (byte) (j >> 8);
	}

	public int readWordLittleEndian() {
		position += 2;
		return ((buffer[position - 1] & 0xff) << 8) + (buffer[position - 2] & 0xff);
	}

	public int readWordMixed() {
		position += 2;
		return ((buffer[position - 2] & 0xff) << 8) + (buffer[position - 1] - 128 & 0xff);
	}

	public int readWordMixedLE() {
		position += 2;
		return ((buffer[position - 1] & 0xff) << 8) + (buffer[position - 2] - 128 & 0xff);
	}

	public int readSignedWordLE() {
		position += 2;
		int j = ((buffer[position - 1] & 0xff) << 8) + (buffer[position - 2] & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int readSignedWordMixed() {
		position += 2;
		int j = ((buffer[position - 1] & 0xff) << 8) + (buffer[position - 2] - 128 & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int readDWordMixed() {
		position += 4;
		return ((buffer[position - 2] & 0xff) << 24) + ((buffer[position - 1] & 0xff) << 16) + ((buffer[position - 4] & 0xff) << 8) + (buffer[position - 3] & 0xff);
	}

	public int readDWordMixed2() {
		position += 4;
		return ((buffer[position - 3] & 0xff) << 24) + ((buffer[position - 4] & 0xff) << 16) + ((buffer[position - 1] & 0xff) << 8) + (buffer[position - 2] & 0xff);
	}

	public void writeBytesReversed128(int i, byte[] abyte0, int j) {
		for (int k = (i + j) - 1; k >= i; k--)
			buffer[position++] = (byte) (abyte0[k] + 128);

	}

	public void readBytesReversed(int i, int j, byte[] abyte0) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[position++];

	}
}
