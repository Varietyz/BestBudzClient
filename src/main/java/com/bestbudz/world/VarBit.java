package com.bestbudz.world;

import com.bestbudz.network.Stream;
import com.bestbudz.network.ArchiveLoader;

public final class VarBit
{

	public static VarBit[] cache;
	public int baseVar;
	public int startBit;
	public int endBit;
	private VarBit()
	{
		boolean aBoolean651 = false;
	}

	public static void unpackConfig(ArchiveLoader archiveLoader)
	{
		Stream stream = new Stream(archiveLoader.extractFile("varbit.dat"));
		int cacheSize = stream.readUnsignedWord();
		if (cache == null)
			cache = new VarBit[cacheSize];
		for (int j = 0; j < cacheSize; j++)
		{
			if (cache[j] == null)
				cache[j] = new VarBit();
			cache[j].readValues(stream);
		}

		if (stream.position != stream.buffer.length)
			System.out.println("varbit load mismatch");
	}

	private void readValues(Stream stream)
	{
		baseVar = stream.readUnsignedWord();
		startBit = stream.readUnsignedByte();
		endBit = stream.readUnsignedByte();

	}
}
