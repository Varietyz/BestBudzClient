package com.bestbudz.util;

import java.awt.Image;
import java.awt.Toolkit;

public class ResourceLoader {

	public static Image loadIcon(String fullPath) {
		try {
			return Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource(fullPath));
		} catch (Exception e) {
			System.err.println("[ERROR] Failed to load icon: " + fullPath);
			return null;
		}
	}
}
