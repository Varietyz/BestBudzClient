package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.types.score;

import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.Effect;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.EffectData;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.EffectFactory;

public class ScoreFactory implements EffectFactory
{
	@Override
	public Effect create(EffectData data) {
		if (data instanceof ScoreEffectData) {
			ScoreEffectData scoreData = (ScoreEffectData) data;
			return new ScoreEffect(data, scoreData.score);
		}
		// Fallback for regular EffectData
		return new ScoreEffect(data, 10); // Default score
	}
}