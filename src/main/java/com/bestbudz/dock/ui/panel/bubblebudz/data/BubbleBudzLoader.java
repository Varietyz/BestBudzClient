package com.bestbudz.dock.ui.panel.bubblebudz.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class BubbleBudzLoader
{
	private static final Gson GSON = new Gson();

	public static int loadBestScore() {
		try {
			String cacheDir = BubbleBudzSaving.findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.json");

			// Check for old .dat file to migrate
			Path oldFile = Paths.get(cacheDir, "bubblebudz.dat");
			if (!Files.exists(scoreFile) && Files.exists(oldFile)) {
				try {
					String content = new String(Files.readAllBytes(oldFile)).trim();
					if (!content.isEmpty()) {
						int score = Integer.parseInt(content);
						// Write as JSON
						JsonObject obj = new JsonObject();
						obj.addProperty("bestScore", score);
						Files.write(scoreFile, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
						Files.delete(oldFile);
						System.out.println("Migrated bubblebudz.dat -> bubblebudz.json, score: " + score);
						return score;
					}
				} catch (Exception e) {
					System.err.println("Error migrating bubblebudz.dat: " + e.getMessage());
				}
			}

			if (Files.exists(scoreFile)) {
				String content = new String(Files.readAllBytes(scoreFile), StandardCharsets.UTF_8).trim();
				if (!content.isEmpty()) {
					JsonObject obj = GSON.fromJson(content, JsonObject.class);
					int score = obj.has("bestScore") ? obj.get("bestScore").getAsInt() : 0;
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
