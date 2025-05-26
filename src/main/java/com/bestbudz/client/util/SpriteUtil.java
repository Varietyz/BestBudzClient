package com.bestbudz.client.util;

import com.bestbudz.data.ItemDef;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.sprite.SpriteLoader;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpriteUtil {

	private static final Map<String, ImageIcon> iconCache = new ConcurrentHashMap<>();

	public static ImageIcon loadIconScaled(String path, int size) {
		return loadIconScaled(path, size, 1f);
	}

	public static ImageIcon loadIconScaled(String path, int size, float alpha) {
		String key = path + "#" + size + "@" + alpha;
		if (iconCache.containsKey(key)) {
			return iconCache.get(key);
		}

		try {
			URL imageUrl = SpriteUtil.class.getClassLoader().getResource(path);
			if (imageUrl == null) throw new IllegalArgumentException("Sprite not found: " + path);

			BufferedImage raw = ImageIO.read(imageUrl);
			Image scaled = raw.getScaledInstance(size, size, Image.SCALE_SMOOTH);

			ImageIcon icon;
			if (alpha < 1f) {
				BufferedImage faded = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = faded.createGraphics();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g2d.drawImage(scaled, 0, 0, null);
				g2d.dispose();
				icon = new ImageIcon(faded);
			} else {
				icon = new ImageIcon(scaled);
			}

			iconCache.put(key, icon);
			return icon;
		} catch (Exception e) {
			System.err.println("Failed to load or cache icon: " + e.getMessage());
			return null;
		}
	}

}
