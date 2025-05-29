package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;

public class HeadIcon extends Client
{
	public static void drawHeadIcon()
{
	if (anInt855 != 2)
	{
		return;
	}
	calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2, (anInt935 - baseY << 7) + anInt938);
	if (spriteDrawX > -1 && loopCycle % 20 < 10)
	{
		headIconsHint[1].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}
}}
