package com.bestbudz.graphics.sprite;

import com.bestbudz.cache.Signlink;
import com.bestbudz.network.Stream;
import com.bestbudz.util.FileUtility;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class SpriteLoader {

	public static void loadSprites()
	{
		try
		{
			Stream index = new Stream(FileUtility.readFile(Signlink.findCacheDir() + "sprites.idx"));
			Stream data = new Stream(FileUtility.readFile(Signlink.findCacheDir() + "sprites.dat"));
			DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.buffer)));
			DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.buffer)));
			int totalSprites = indexFile.readInt();
			System.out.println("Sprites Loaded: " + totalSprites);
			if (cache == null)
			{
				cache = new SpriteLoader[totalSprites];
				sprites = new Sprite[totalSprites];
			}
			for (int i = 0; i < totalSprites; i++)
			{
				int id = indexFile.readInt();
				if (cache[id] == null)
				{
					cache[id] = new SpriteLoader();
				}
				cache[id].readValues(indexFile, dataFile);
				createSprite(cache[id]);
			}
			indexFile.close();
			dataFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void readValues(DataInputStream index, DataInputStream data) throws IOException
	{
		do
		{
			int opCode = data.readByte();
			if (opCode == 0)
			{
				break;
			}
			if (opCode == 1)
			{
				id = data.readShort();
			}
			else if (opCode == 2)
			{
				name = data.readUTF();
			}
			else if (opCode == 3)
			{
				drawOffsetX = data.readShort();
			}
			else if (opCode == 4)
			{
				drawOffsetY = data.readShort();
			}
			else if (opCode == 5)
			{
				int indexLength = index.readInt();
				byte[] dataread = new byte[indexLength];
				data.readFully(dataread);
				spriteData = dataread;
			}
		} while (true);
	}

	public static void createSprite(SpriteLoader sprite)
	{
		sprites[sprite.id] = new Sprite(sprite.spriteData);
		sprites[sprite.id].drawOffsetX = sprite.drawOffsetX;
		sprites[sprite.id].drawOffsetY = sprite.drawOffsetY;
	}

	public SpriteLoader()
	{
		name = "name";
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}

	public static SpriteLoader[] cache;
	public static Sprite[] sprites = null;
	public String name;
	public int id;
	public int drawOffsetX;
	public int drawOffsetY;
	public byte[] spriteData;
}