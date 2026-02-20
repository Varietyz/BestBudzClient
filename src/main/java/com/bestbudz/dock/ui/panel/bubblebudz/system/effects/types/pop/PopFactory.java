package com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types.pop;

import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.Effect;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectData;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectFactory;

public class PopFactory implements EffectFactory {
	@Override
	public Effect create(EffectData data) {
		return new PopEffect(data);
	}
}