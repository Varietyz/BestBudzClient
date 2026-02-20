package com.bestbudz.dock.ui.panel.bubblebudz.system.effects;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EffectManager {
	private List<Effect> effects = new CopyOnWriteArrayList<>();
	private Map<String, EffectFactory> factories = new HashMap<>();

	public void registerEffectType(String name, EffectFactory factory) {
		factories.put(name, factory);
	}

	public void addEffect(String type, EffectData data) {
		EffectFactory factory = factories.get(type);
		if (factory != null) {
			effects.add(factory.create(data));
		}
	}

	public void updateEffects() {
		effects.removeIf(effect -> {
			effect.update();
			return effect.isExpired();
		});
	}

	public void renderEffects(Graphics2D g2d) {
		for (Effect effect : effects) {
			effect.render(g2d);
		}
	}

	public void clearEffects() {
		effects.clear();
	}
}