package com.bestbudz.util;

import java.awt.Toolkit;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResourceLoader {

	public static BufferedImage loadBufferedImage(String fullPath) {
		try {
			return ImageIO.read(ResourceLoader.class.getResource(fullPath));
		} catch (IOException | IllegalArgumentException e) {
			System.err.println("[ERROR] Failed to load: " + fullPath);
			e.printStackTrace();
			return null;
		}
	}

	public static Image loadIcon(String fullPath) {
		try {
			return Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource(fullPath));
		} catch (Exception e) {
			System.err.println("[ERROR] Failed to load icon: " + fullPath);
			return null;
		}
	}
}
