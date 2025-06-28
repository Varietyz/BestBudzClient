package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.types.pop;

import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.EffectData;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import java.awt.Color;

public class PopEffectData extends EffectData {
	public final GeometricShape.ShapeType shapeType;
	public final boolean isGeometric;
	public final float rotation;

	// Constructor for geometric shapes with rotation
	public PopEffectData(float x, float y, float radius, Color color, GeometricShape.ShapeType shapeType, float rotation) {
		super(x, y, radius, color);
		this.shapeType = shapeType;
		this.isGeometric = true;
		this.rotation = rotation;
	}

	// Constructor for regular circular bubbles
	public PopEffectData(float x, float y, float radius, Color color) {
		super(x, y, radius, color);
		this.shapeType = null;
		this.isGeometric = false;
		this.rotation = 0f;
	}
}