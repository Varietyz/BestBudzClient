package com.bestbudz.dock.ui.panel.bubblebudz.data;

import com.bestbudz.cache.Signlink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class BubbleBudzSaving
{
	private static final Gson GSON = new Gson();

	public static void saveBestScore(int bestScore) {
		try {
			int filebestScore = 0;
			Path scoreFile = Paths.get(Signlink.findCacheDir(), "bubblebudz.json");

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
			}
		} catch (Exception e) {
			System.err.println("Failed to save best score: " + e.getMessage());
		}
	}
}
