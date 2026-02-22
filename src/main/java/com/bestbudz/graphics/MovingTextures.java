package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.Rasterizer;

public class MovingTextures extends Client
{
	private static void ensureBufferSize(int required) {
		if (aByteArray912 == null || aByteArray912.length < required) {
			aByteArray912 = new byte[required];
		}
	}

	public static void updateMovingTextures(int j)
	{
		{
			if (Rasterizer.textureLastUsed[17] >= j)
			{
				Background background = Rasterizer.backgroundTextures[17];
				int k = background.backgroundWidth * background.backgroundHeight - 1;
				int j1 = background.backgroundWidth * gameTickCounter * 2;
				byte[] abyte0 = background.textureData;
				ensureBufferSize(k + 1);
				byte[] abyte3 = aByteArray912;
				for (int i2 = 0; i2 <= k; i2++)
					abyte3[i2] = abyte0[i2 - j1 & k];

				background.textureData = abyte3;
				aByteArray912 = abyte0;
				Rasterizer.applyTexture(17);
				}
			if (Rasterizer.textureLastUsed[24] >= j)
			{
				Background background_1 = Rasterizer.backgroundTextures[24];
				int l = background_1.backgroundWidth * background_1.backgroundHeight - 1;
				int k1 = background_1.backgroundWidth * gameTickCounter * 2;
				byte[] abyte1 = background_1.textureData;
				ensureBufferSize(l + 1);
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
				ensureBufferSize(i1 + 1);
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
				ensureBufferSize(i1 + 1);
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
