package com.bestbudz.dock.ui.panel.bubblebudz.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class BubbleBudzSaving
{
	private static final Gson GSON = new Gson();

	public static String findCacheDir() {
		Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
		try {
			Files.createDirectories(cacheDir);
		} catch (IOException ex) {
			System.err.println("Failed to create cache directory: " + ex.getMessage());
		}
		return cacheDir.toAbsolutePath() + File.separator;
	}

	public static void saveBestScore(int bestScore) {
		try {
			int filebestScore = 0;
			String cacheDir = findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.json");

			// Migrate old .dat if needed
			Path oldFile = Paths.get(cacheDir, "bubblebudz.dat");
			if (Files.exists(oldFile) && !Files.exists(scoreFile)) {
				try {
					String content = new String(Files.readAllBytes(oldFile)).trim();
					if (!content.isEmpty()) {
						filebestScore = Integer.parseInt(content);
					}
					Files.delete(oldFile);
					System.out.println("Migrated bubblebudz.dat -> bubblebudz.json");
				} catch (Exception e) {
					System.err.println("Error migrating bubblebudz.dat: " + e.getMessage());
				}
			}

			if (Files.exists(scoreFile)) {
				try {
					String content = new String(Files.readAllBytes(scoreFile), StandardCharsets.UTF_8).trim();
					if (!content.isEmpty()) {
						JsonObject obj = GSON.fromJson(content, JsonObject.class);
						filebestScore = obj.has("bestScore") ? obj.get("bestScore").getAsInt() : 0;
					}
				} catch (Exception e) {
					System.err.println("Error reading existing score: " + e.getMessage());
				}
			}

			if (bestScore > filebestScore) {
				JsonObject obj = new JsonObject();
				obj.addProperty("bestScore", bestScore);
				Files.write(scoreFile, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
				System.out.println("New best score saved: " + bestScore + " (previous: " + filebestScore + ")");
			} else {
				System.out.println("Score " + bestScore + " not higher than file best " + filebestScore + ", not saving");
			}
		} catch (Exception e) {
			System.err.println("Failed to save best score: " + e.getMessage());
		}
	}
}
