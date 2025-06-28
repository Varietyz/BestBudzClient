package com.bestbudz.network;

import com.bestbudz.util.compression.BZip2Compression;

public final class ArchiveLoader
{

  private final byte[] archiveData;
  private final int fileCount;
  private final int[] fileHashes;
  private final int[] fileSizes;
  public ArchiveLoader(byte[] abyte0) {
    Stream stream = new Stream(abyte0);
    int i = stream.read3Bytes();
    int j = stream.read3Bytes();
    if (j != i) {
      byte[] abyte1 = new byte[i];
      BZip2Compression.decompress(abyte1, i, abyte0, j, 6);
      archiveData = abyte1;
      stream = new Stream(archiveData);
      isDecompressed = true;
    } else {
      archiveData = abyte0;
		isDecompressed = false;
    }
    fileCount = stream.readUnsignedWord();
    fileHashes = new int[fileCount];
    fileSizes = new int[fileCount];
	  compressedSizes = new int[fileCount];
	  fileOffsets = new int[fileCount];
    int k = stream.position + fileCount * 10;
    for (int l = 0; l < fileCount; l++) {
      fileHashes[l] = stream.readDWord();
      fileSizes[l] = stream.read3Bytes();
      compressedSizes[l] = stream.read3Bytes();
      fileOffsets[l] = k;
      k += compressedSizes[l];
    }
  }

  public byte[] extractFile(String s) {
    byte[] abyte0 = null;
    int i = 0;
    s = s.toUpperCase();
    for (int j = 0; j < s.length(); j++) i = (i * 61 + s.charAt(j)) - 32;

    for (int k = 0; k < fileCount; k++)
      if (fileHashes[k] == i) {
		  abyte0 = new byte[fileSizes[k]];
        if (!isDecompressed) {
          BZip2Compression.decompress(
              abyte0, fileSizes[k], archiveData, compressedSizes[k], fileOffsets[k]);
        } else {
          System.arraycopy(archiveData, fileOffsets[k], abyte0, 0, fileSizes[k]);
        }
        return abyte0;
      }

    return null;
  }
  private final int[] compressedSizes;
  private final int[] fileOffsets;
  private final boolean isDecompressed;
}
