package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render;

import java.awt.Graphics2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;

public class RenderManager {
	private List<RenderComponent> components = new CopyOnWriteArrayList<>();
	private boolean needsSorting = false;

	public void addComponent(RenderComponent component) {
		components.add(component);
		needsSorting = true;
	}

	public void removeComponent(RenderComponent component) {
		components.remove(component);
	}

	public void render(Graphics2D g2d, RenderContext context) {
		if (needsSorting) {
			components.sort(Comparator.comparingInt(RenderComponent::getRenderOrder));
			needsSorting = false;
		}

		for (RenderComponent component : components) {
			component.render(g2d, context);
		}
	}

	public void clearComponents() {
		components.clear();
	}
}