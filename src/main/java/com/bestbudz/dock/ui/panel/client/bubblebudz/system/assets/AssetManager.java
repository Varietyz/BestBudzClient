package com.bestbudz.dock.ui.panel.client.bubblebudz.system.assets;

import com.bestbudz.dock.util.SpriteUtil;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashMap;

public class AssetManager {
	private Map<String, BufferedImage> images = new HashMap<>();

	public BufferedImage getImage(String path, int size) {
		String key = path + "_" + size;
		return images.computeIfAbsent(key, k -> loadImage(path, size));
	}

	private BufferedImage loadImage(String path, int size) {
		try {
			ImageIcon icon = SpriteUtil.loadIconScaled(path, size);
			Image img = icon.getImage();
			BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bufferedImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			return bufferedImage;
		} catch (Exception e) {
			return null;
		}
	}

	public void dispose() {
		for (BufferedImage image : images.values()) {
			if (image != null) {
				image.flush();
			}
		}
		images.clear();
	}
}