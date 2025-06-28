package com.bestbudz.world;

import com.bestbudz.engine.core.Client;

public class InLocation extends Client
{
	public static final boolean inBarrows(int x, int y)
	{
		return (x > 3539 && x < 3582 && y >= 9675 && y < 9722) || x > 3532 && x < 3546 && y > 9698 && y < 9709;
	}

	public static final boolean inCyclops(int x, int y, int z)
	{
		return (x >= 2847 && x <= 2876 && y >= 3534 && y <= 3556 && z == 2
			|| x >= 2838 && x <= 2847 && y >= 3543 && y <= 3556 && z == 2);
	}

	public static final boolean inWGGame(int x, int y)
	{
		return (x > 2136 && x < 2166 && y > 5089 && y < 5108);
	}

	public static final boolean inWGLobby(int x, int y)
	{
		return (x > 1859 && x < 1868 && y > 5316 && y < 5323);
	}

	public static final boolean inGWD(int x, int y)
	{
		return (x > 2816 && x < 2960 && y >= 5243 && y < 5400);
	}

	public static boolean inPcBoat(int x, int y)
	{
		return (x >= 2660 && x <= 2663 && y >= 2638 && y <= 2643);
	}

	public static boolean inPcGame(int x, int y)
	{
		return (x >= 2624 && x <= 2690 && y >= 2550 && y <= 2619);
	}

//	public static boolean inWilderness(int x, int y)
//	{
//		return (x > 2941 && x < 3392 && y > 3521 && y < 3966) || (x > 2941 && x < 3392 && y > 9918 && y < 10366);
//	}

	public static boolean inMaze(int x)
	{
		return (x > 1);
	}

	public static boolean inSafe(int x, int y)
	{
		return (x >= 3180 && x <= 3190 && y >= 3433 && y <= 3447) || (x >= 3091 && x <= 3098 && y >= 3488 && y <= 3499);
	}

	public static boolean inPvP(int x, int y)
	{
		return (!inSafe(x, y));
	}
}
