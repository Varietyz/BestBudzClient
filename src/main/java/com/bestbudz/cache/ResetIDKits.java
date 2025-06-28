package com.bestbudz.cache;

import com.bestbudz.engine.core.Client;

public class ResetIDKits extends Client
{
	public static void resetIdentityKits()
	{
		dragModeActive = true;
		for (int j = 0; j < 7; j++)
		{
			musicTrackIds[j] = -1;
			for (int k = 0; k < IdentityKit.length; k++)
			{
				if (IdentityKit.cache[k].aBoolean662 || IdentityKit.cache[k].anInt657 != j + (welcomeScreenVisible ? 0 : 7))
					continue;
				musicTrackIds[j] = k;
				break;
			}
		}
	}
}
