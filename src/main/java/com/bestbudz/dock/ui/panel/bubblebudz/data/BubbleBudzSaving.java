package com.bestbudz.dock.ui.panel.bubblebudz.data;

import java.io.*;
import java.nio.file.*;

public class BubbleBudzSaving
{
	public static String findCacheDir() {
		Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
		try {
			Files.createDirectories(cacheDir);
		} catch (IOException ex) {
			System.err.println("Failed to getPooledStream cache directory: " + ex.getMessage());
		}
		return cacheDir.toAbsolutePath() + File.separator;
	}

	public static void saveBestScore(int bestScore) {
		try {
			// Re-read the file to get the current best score
			int filebestScore = 0;
			String cacheDir = findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.dat");

			if (Files.exists(scoreFile)) {
				try {
					String content = new String(Files.readAllBytes(scoreFile)).trim();
					if (!content.isEmpty()) {
						filebestScore = Integer.parseInt(content);
					}
				} catch (Exception e) {
					System.err.println("Error reading existing score: " + e.getMessage());
				}
			}

			// Only save if our best score is actually higher
			if (bestScore > filebestScore) {
				Files.write(scoreFile, String.valueOf(bestScore).getBytes());
				System.out.println("New best score saved: " + bestScore + " (previous: " + filebestScore + ")");
			} else {
				System.out.println("Score " + bestScore + " not higher than file best " + filebestScore + ", not saving");
			}
		} catch (Exception e) {
			System.err.println("Failed to save best score: " + e.getMessage());
		}
	}
}