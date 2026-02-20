package com.bestbudz.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class Decompressor {

	private final RandomAccessFile dataFile;
	private final RandomAccessFile indexFile;
	private final int fileId;

	private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[520]);

	public Decompressor(RandomAccessFile dataFile, RandomAccessFile indexFile, int fileId) {
		this.fileId = fileId;
		this.dataFile = dataFile;
		this.indexFile = indexFile;
	}

	public byte[] decompress(int entryId) {
		byte[] buffer = BUFFER.get();

		try {

			indexFile.seek(entryId * 6);
			if (indexFile.read(buffer, 0, 6) != 6) {
				return null;
			}

			int dataSize = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int firstBlock = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);

			if (firstBlock <= 0 || firstBlock > dataFile.length() / 520) {
				return null;
			}

			byte[] result = new byte[dataSize];
			int bytesRead = 0;
			int currentBlock = firstBlock;
			int blockIndex = 0;

			while (bytesRead < dataSize) {
				if (currentBlock == 0) {
					return null;
				}

				dataFile.seek(currentBlock * 520);
				int chunkSize = Math.min(512, dataSize - bytesRead);
				int totalToRead = chunkSize + 8;

				if (dataFile.read(buffer, 0, totalToRead) != totalToRead) {
					return null;
				}

				int blockEntryId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int blockNumber = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int nextBlock = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int blockFileId = buffer[7] & 0xff;

				if (blockEntryId != entryId || blockNumber != blockIndex || blockFileId != fileId) {
					return null;
				}

				if (nextBlock > dataFile.length() / 520) {
					return null;
				}

				System.arraycopy(buffer, 8, result, bytesRead, chunkSize);
				bytesRead += chunkSize;
				currentBlock = nextBlock;
				blockIndex++;
			}

			return result;
		} catch (IOException e) {
			return null;
		}
	}

	public void method234(int dataSize, byte[] data, int entryId) {
		if (!writeEntry(true, entryId, dataSize, data)) {
			writeEntry(false, entryId, dataSize, data);
		}
	}

	private boolean writeEntry(boolean overwrite, int entryId, int dataSize, byte[] data) {
		byte[] buffer = BUFFER.get();

		try {
			int currentBlock;

			if (overwrite) {

				indexFile.seek(entryId * 6);
				if (indexFile.read(buffer, 0, 6) != 6) {
					return false;
				}

				currentBlock = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
				if (currentBlock <= 0 || currentBlock > dataFile.length() / 520) {
					return false;
				}
			} else {

				currentBlock = (int) ((dataFile.length() + 519) / 520);
				if (currentBlock == 0) {
					currentBlock = 1;
				}
			}

			buffer[0] = (byte) (dataSize >> 16);
			buffer[1] = (byte) (dataSize >> 8);
			buffer[2] = (byte) dataSize;
			buffer[3] = (byte) (currentBlock >> 16);
			buffer[4] = (byte) (currentBlock >> 8);
			buffer[5] = (byte) currentBlock;

			indexFile.seek(entryId * 6);
			indexFile.write(buffer, 0, 6);

			int bytesWritten = 0;
			int blockIndex = 0;

			while (bytesWritten < dataSize) {
				int nextBlock = 0;

				if (overwrite) {

					dataFile.seek(currentBlock * 520);
					if (dataFile.read(buffer, 0, 8) == 8) {
						int blockEntryId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int blockNumber = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						nextBlock = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
						int blockFileId = buffer[7] & 0xff;

						if (blockEntryId != entryId || blockNumber != blockIndex || blockFileId != fileId ||
							nextBlock > dataFile.length() / 520) {
							return false;
						}
					}
				}

				if (nextBlock == 0) {

					overwrite = false;
					nextBlock = (int) ((dataFile.length() + 519) / 520);
					if (nextBlock == 0) {
						nextBlock++;
					}
					if (nextBlock == currentBlock) {
						nextBlock++;
					}
				}

				if (dataSize - bytesWritten <= 512) {
					nextBlock = 0;
				}

				buffer[0] = (byte) (entryId >> 8);
				buffer[1] = (byte) entryId;
				buffer[2] = (byte) (blockIndex >> 8);
				buffer[3] = (byte) blockIndex;
				buffer[4] = (byte) (nextBlock >> 16);
				buffer[5] = (byte) (nextBlock >> 8);
				buffer[6] = (byte) nextBlock;
				buffer[7] = (byte) fileId;

				dataFile.seek(currentBlock * 520);
				dataFile.write(buffer, 0, 8);

				int chunkSize = Math.min(512, dataSize - bytesWritten);
				dataFile.write(data, bytesWritten, chunkSize);

				bytesWritten += chunkSize;
				currentBlock = nextBlock;
				blockIndex++;
			}

			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
