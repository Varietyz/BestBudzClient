package com.bestbudz.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JsonCacheLoader {

	private static String basePath;

	static {
		basePath = System.getProperty("cache.json.path", "extracted_cache");
	}

	public static void setBasePath(String path) {
		basePath = path;
	}

	public static String getBasePath() {
		return basePath;
	}

	public static JsonObject loadJsonObject(String filename) {
		String content = loadFileContent(filename);
		if (content == null) return null;
		return JsonParser.parseString(content).getAsJsonObject();
	}

	public static JsonArray loadJsonArray(String filename) {
		String content = loadFileContent(filename);
		if (content == null) return null;
		JsonElement element = JsonParser.parseString(content);
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}
		return null;
	}

	public static JsonObject loadMapJson(String subpath) {
		String content = loadFileContentQuiet("maps_json/" + subpath);
		if (content == null) return null;
		return JsonParser.parseString(content).getAsJsonObject();
	}

	public static JsonObject loadAnimFrameJson(String subpath) {
		String content = loadFileContentQuiet("anim_frames_json/" + subpath);
		if (content == null) return null;
		return JsonParser.parseString(content).getAsJsonObject();
	}

	private static String loadFileContent(String relativePath) {
		return loadFileContent(relativePath, true);
	}

	private static String loadFileContentQuiet(String relativePath) {
		return loadFileContent(relativePath, false);
	}

	private static String loadFileContent(String relativePath, boolean logMissing) {
		// Try filesystem first
		Path fsPath = Paths.get(basePath, relativePath);
		if (Files.exists(fsPath)) {
			try {
				return new String(Files.readAllBytes(fsPath), StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.err.println("[JsonCacheLoader] Failed to read file: " + fsPath + " - " + e.getMessage());
			}
		}

		// Try classpath fallback
		String classpathResource = "/" + basePath + "/" + relativePath;
		try (InputStream is = JsonCacheLoader.class.getResourceAsStream(classpathResource)) {
			if (is != null) {
				return readStream(is);
			}
		} catch (IOException e) {
			System.err.println("[JsonCacheLoader] Failed to read classpath resource: " + classpathResource);
		}

		// Try classpath without base path prefix
		try (InputStream is = JsonCacheLoader.class.getResourceAsStream("/" + relativePath)) {
			if (is != null) {
				return readStream(is);
			}
		} catch (IOException e) {
			// ignore
		}

		if (logMissing) {
			System.err.println("[JsonCacheLoader] File not found: " + relativePath + " (checked: " + fsPath + " and classpath)");
		}
		return null;
	}

	private static String readStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int len;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		return baos.toString("UTF-8");
	}

	public static byte[] loadFileBytes(String relativePath) {
		Path fsPath = Paths.get(basePath, relativePath);
		if (Files.exists(fsPath)) {
			try {
				return Files.readAllBytes(fsPath);
			} catch (IOException e) {
				System.err.println("[JsonCacheLoader] Failed to read binary file: " + fsPath + " - " + e.getMessage());
			}
		}
		try (InputStream is = JsonCacheLoader.class.getResourceAsStream("/" + basePath + "/" + relativePath)) {
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[8192];
				int len;
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return baos.toByteArray();
			}
		} catch (IOException e) {
			// ignore
		}
		return null;
	}

	public static boolean isAvailable(String filename) {
		Path fsPath = Paths.get(basePath, filename);
		if (Files.exists(fsPath)) return true;
		return JsonCacheLoader.class.getResourceAsStream("/" + basePath + "/" + filename) != null;
	}
}
