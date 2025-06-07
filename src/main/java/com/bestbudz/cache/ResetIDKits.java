package com.bestbudz.cache;

import com.bestbudz.engine.core.Client;

public class ResetIDKits extends Client
{
	public static void resetIdentityKits()
	{
		aBoolean1031 = true;
		for (int j = 0; j < 7; j++)
		{
			anIntArray1065[j] = -1;
			for (int k = 0; k < IdentityKit.length; k++)
			{
				if (IdentityKit.cache[k].aBoolean662 || IdentityKit.cache[k].anInt657 != j + (aBoolean1047 ? 0 : 7))
					continue;
				anIntArray1065[j] = k;
				break;
			}
		}
	}
}
