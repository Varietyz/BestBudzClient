package com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types.score;// Keep ScoreEffectData as extension

import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectData;
import java.awt.Color;

public class ScoreEffectData extends EffectData
{
	public final int score;

	public ScoreEffectData(float x, float y, float radius, Color color, int score) {
		super(x, y, radius, color);  // Don't pass score to parent
		this.score = score;
	}
}