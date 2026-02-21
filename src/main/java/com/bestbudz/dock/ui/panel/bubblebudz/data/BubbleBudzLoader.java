package com.bestbudz.dock.ui.panel.bubblebudz.data;

import com.bestbudz.cache.Signlink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class BubbleBudzLoader
{
	private static final Gson GSON = new Gson();

	public static int loadBestScore() {
		try {
			Path scoreFile = Paths.get(Signlink.findCacheDir(), "bubblebudz.json");

			if (Files.exists(scoreFile)) {
				String content = new String(Files.readAllBytes(scoreFile), StandardCharsets.UTF_8).trim();
				if (!content.isEmpty()) {
					JsonObject obj = GSON.fromJson(content, JsonObject.class);
					return obj.has("bestScore") ? obj.get("bestScore").getAsInt() : 0;
				}
			}
			return 0;
		} catch (Exception e) {
			System.err.println("Failed to load best score: " + e.getMessage());
			return 0;
		}
	}
}
