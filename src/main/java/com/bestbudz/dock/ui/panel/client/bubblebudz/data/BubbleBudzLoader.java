package com.bestbudz.dock.ui.panel.client.bubblebudz.data;

import java.nio.file.*;

public class BubbleBudzLoader
{
	public static int loadBestScore() {
		try {
			String cacheDir = BubbleBudzSaving.findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.dat");

			if (Files.exists(scoreFile)) {
				String content = new String(Files.readAllBytes(scoreFile)).trim();
				if (!content.isEmpty()) {
					int score = Integer.parseInt(content);
					System.out.println("Loaded best score: " + score);
					return score;
				} else {
					return 0;
				}
			} else {
				System.out.println("No existing score file found, starting with best score: 0");
				return 0;
			}
		} catch (Exception e) {
			System.err.println("Failed to load best score: " + e.getMessage());
			return 0;
		}
	}
}