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
				int k = background.backgroundWidth * background.backgroundHeight - 1;
				int j1 = background.backgroundWidth * gameTickCounter * 2;
				byte[] abyte0 = background.textureData;
				byte[] abyte3 = aByteArray912;
				for (int i2 = 0; i2 <= k; i2++)
					abyte3[i2] = abyte0[i2 - j1 & k];

				background.textureData = abyte3;
				aByteArray912 = abyte0;
				Rasterizer.applyTexture(17);
				packetCounter++;
				if (packetCounter > 1235)
				{
					packetCounter = 0;
					stream.writeEncryptedOpcode(226);
					stream.writeByte(0);
					int l2 = stream.position;
					stream.writeWord(58722);
					stream.writeByte(240);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeByte((int) (Math.random() * 256D));
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(51825);
					stream.writeByte((int) (Math.random() * 256D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(7130);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(61657);
					stream.writePacketLength(stream.position - l2);
				}
			}
			if (Rasterizer.textureLastUsed[24] >= j)
			{
				Background background_1 = Rasterizer.backgroundTextures[24];
				int l = background_1.backgroundWidth * background_1.backgroundHeight - 1;
				int k1 = background_1.backgroundWidth * gameTickCounter * 2;
				byte[] abyte1 = background_1.textureData;
				byte[] abyte4 = aByteArray912;
				for (int j2 = 0; j2 <= l; j2++)
					abyte4[j2] = abyte1[j2 - k1 & l];

				background_1.textureData = abyte4;
				aByteArray912 = abyte1;
				Rasterizer.applyTexture(24);
			}
			if (Rasterizer.textureLastUsed[34] >= j)
			{
				Background background_2 = Rasterizer.backgroundTextures[34];
				int i1 = background_2.backgroundWidth * background_2.backgroundHeight - 1;
				int l1 = background_2.backgroundWidth * gameTickCounter * 2;
				byte[] abyte2 = background_2.textureData;
				byte[] abyte5 = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.textureData = abyte5;
				aByteArray912 = abyte2;
				Rasterizer.applyTexture(34);
			}
			if (Rasterizer.textureLastUsed[40] >= j)
			{
				Background background_2 = Rasterizer.backgroundTextures[40];
				int i1 = background_2.backgroundWidth * background_2.backgroundHeight - 1;
				int l1 = background_2.backgroundWidth * gameTickCounter * 2;
				byte[] abyte2 = background_2.textureData;
				byte[] abyte5 = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.textureData = abyte5;
				aByteArray912 = abyte2;
				Rasterizer.applyTexture(40);
			}
		}
	}
}
