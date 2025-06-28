package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.entity.ParseAndUpdateEntities.calcEntityScreenPos;

public class HeadIcon extends Client
{
	public static void drawHeadIcon()
{
	if (crosshairType != 2)
	{
		return;
	}
	calcEntityScreenPos((hoveredRegionTileX - baseX << 7) + selectedRegionTileY, selectedRegionTileX * 2, (hoveredRegionTileY - baseY << 7) + regionTileAction);
	if (spriteDrawX > -1 && loopCycle % 20 < 10)
	{
		headIconsHint[1].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}
}}
