package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.gamerender.Rasterizer;

public class MovingTextures extends Client
{
	public static void updateMovingTextures(int j)
	{
		if (SettingsConfig.enableMovingTextures)
		{
			if (Rasterizer.textureLastUsed[17] >= j)
			{
				Background background = Rasterizer.backgroundTextures[17];
				int k = background.anInt1452 * background.anInt1453 - 1;
				int j1 = background.anInt1452 * anInt945 * 2;
				byte[] abyte0 = background.aByteArray1450;
				byte[] abyte3 = aByteArray912;
				for (int i2 = 0; i2 <= k; i2++)
					abyte3[i2] = abyte0[i2 - j1 & k];

				background.aByteArray1450 = abyte3;
				aByteArray912 = abyte0;
				Rasterizer.applyTexture(17);
				anInt854++;
				if (anInt854 > 1235)
				{
					anInt854 = 0;
					stream.createFrame(226);
					stream.writeWordBigEndian(0);
					int l2 = stream.currentOffset;
					stream.writeWord(58722);
					stream.writeWordBigEndian(240);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(51825);
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(7130);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(61657);
					stream.writeBytes(stream.currentOffset - l2);
				}
			}
			if (Rasterizer.textureLastUsed[24] >= j)
			{
				Background background_1 = Rasterizer.backgroundTextures[24];
				int l = background_1.anInt1452 * background_1.anInt1453 - 1;
				int k1 = background_1.anInt1452 * anInt945 * 2;
				byte[] abyte1 = background_1.aByteArray1450;
				byte[] abyte4 = aByteArray912;
				for (int j2 = 0; j2 <= l; j2++)
					abyte4[j2] = abyte1[j2 - k1 & l];

				background_1.aByteArray1450 = abyte4;
				aByteArray912 = abyte1;
				Rasterizer.applyTexture(24);
			}
			if (Rasterizer.textureLastUsed[34] >= j)
			{
				Background background_2 = Rasterizer.backgroundTextures[34];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte[] abyte2 = background_2.aByteArray1450;
				byte[] abyte5 = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Rasterizer.applyTexture(34);
			}
			if (Rasterizer.textureLastUsed[40] >= j)
			{
				Background background_2 = Rasterizer.backgroundTextures[40];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte[] abyte2 = background_2.aByteArray1450;
				byte[] abyte5 = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Rasterizer.applyTexture(40);
			}
		}
	}
}
