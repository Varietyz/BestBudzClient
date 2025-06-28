package com.bestbudz.world;

import com.bestbudz.network.ArchiveLoader;
import com.bestbudz.network.Stream;

public final class  Varp {

    public static Varp[] cache;
    private static int clientScriptCount;
    private static int[] clientScriptIds;
    public int configType;
    private Varp()
    {
	}

    public static void unpackConfig(ArchiveLoader archiveLoader)
    {
        Stream stream = new Stream(archiveLoader.extractFile("varp.dat"));
        clientScriptCount = 0;
        int cacheSize = stream.readUnsignedWord();
        if(cache == null)
            cache = new Varp[cacheSize];
        if(clientScriptIds == null)
            clientScriptIds = new int[cacheSize];
        for(int j = 0; j < cacheSize; j++)
        {
            if(cache[j] == null)
                cache[j] = new Varp();
            cache[j].readValues(stream, j);
        }
        if(stream.position != stream.buffer.length)
            System.out.println("varptype load mismatch");
    }

    private void readValues(Stream stream, int i)
    {
        do
        {
            int j = stream.readUnsignedByte();
            if(j == 0)
                return;
            if(j == 1)
                 stream.readUnsignedByte();
            else
            if(j == 2)
                stream.readUnsignedByte();
            else
            if(j == 3)
                clientScriptIds[clientScriptCount++] = i;
            else
            if(j == 4) {
			} else
            if(j == 5)
                configType = stream.readUnsignedWord();
            else
            if(j == 6) {
			} else
            if(j == 7)
                stream.readDWord();
            else
            if(j == 8)
			{
			}
             else
            if(j == 10)
                 stream.readString();
            else
            if(j == 11)
			{
			}
            else
            if(j == 12)
                stream.readDWord();
            else
            if(j == 13) {
			} else
                System.out.println("Error unrecognised config code: " + j);
        } while(true);
    }

}
