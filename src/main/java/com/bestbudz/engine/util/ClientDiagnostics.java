package com.bestbudz.engine.util;

import com.bestbudz.engine.config.EngineConfig;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import java.lang.management.*;
import java.lang.reflect.Field;
import java.io.File;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.GameLoader;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.entity.Stoner;
import com.bestbudz.cache.Signlink;
import java.util.ArrayList;
import java.util.List;

public class ClientDiagnostics extends Client {

	/* Dynamic sizing constants */
	private static final int PANEL_WIDTH = 130;
	private static final int PANEL_MARGIN = 4;
	private static final int LINE_HEIGHT = 14;
	private static final int HEADER_HEIGHT = 16;
	private static final int SECTION_SPACING = 6;
	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 5;
	private static final int TEXT_PADDING = 3;
	private static final int MAX_DISPLAY_HEIGHT = 720;

	/* Enhanced color palette */
	private static final int COL_HEADER = ColorConfig.COLOR_CYAN_TURQUOISE;
	private static final int COL_TEXT = ColorConfig.WHITE_COLOR;
	private static final int COL_VALUE = 0xFFFF99;
	private static final int COL_GOOD = 0x00FF00;
	private static final int COL_WARNING = 0xFFFF00;
	private static final int COL_CRITICAL = 0xFF0000;
	private static final int COL_NETWORK = 0x00CCFF;
	private static final int COL_CACHE = 0xFF9900;
	private static final int BACKGROUND_COLOR = 0x151515;
	private static final int BACKGROUND_OPACITY = 180;

	/* ------------------------------------------------------------ */
	public static void drawClientDiagnostics() {
		if (!clientData) return;

		/* Collect diagnostic sections */
		List<DiagnosticSection> sections = new ArrayList<>();
		sections.add(createHeaderSection());
		sections.add(createPerformanceSection());
		sections.add(createMemorySection());
		sections.add(createGameWorldSection());
		sections.add(createEntitiesSection());
		sections.add(createGraphicsSection());
		sections.add(createSignlinkSection());
		sections.add(createOnDemandSection());

		// Render sections with height management
		int currentY = TOP_MARGIN;
		int totalHeight = calculateTotalHeight(sections);

		if (totalHeight > MAX_DISPLAY_HEIGHT) {
			renderInTwoColumns(sections);
		} else {
			for (DiagnosticSection section : sections) {
				currentY = renderCompactSection(section, LEFT_MARGIN, currentY);
				currentY += SECTION_SPACING;
			}
		}
	}

	/* --------------------------------------------------------- Section Creators */

	private static DiagnosticSection createHeaderSection() {
		DiagnosticSection section = new DiagnosticSection("[BestBudz]", COL_HEADER);
		if (worldSelected == 3){
			section.addLine("Version", String.valueOf(EngineConfig.DEV_ENGINE_VERSION), COL_VALUE);
		}else{
			section.addLine("Version", String.valueOf(EngineConfig.ENGINE_VERSION), COL_VALUE);
		}

		return section;
	}

	private static DiagnosticSection createPerformanceSection() {
		DiagnosticSection section = new DiagnosticSection("[Performance]", COL_HEADER);
		section.addLine("FPS", String.valueOf(fps), getFpsColor());
		section.addLine("GC Runs", String.valueOf(getGcRuns()), COL_VALUE);
		section.addLine("GC Time", getGcTime() + " ms", COL_VALUE);

		ThreadMXBean tm = ManagementFactory.getThreadMXBean();
		section.addLine("Threads", String.valueOf(tm.getThreadCount()), COL_VALUE);

		long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
		section.addLine("Uptime", formatTime(uptime), COL_VALUE);
		return section;
	}

	private static DiagnosticSection createMemorySection() {
		DiagnosticSection section = new DiagnosticSection("[Memory]", COL_HEADER);

		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		long total = rt.totalMemory();
		long max = rt.maxMemory();

		section.addLine("Used", formatBytes(used), getMemoryColor(used, max));
		section.addLine("Total", formatBytes(total), COL_VALUE);
		section.addLine("Max", formatBytes(max), COL_VALUE);
		section.addLine("Usage", String.format("%.1f%%", (used * 100.0 / max)), getMemoryColor(used, max));

		return section;
	}

	private static DiagnosticSection createGameWorldSection() {
		DiagnosticSection section = new DiagnosticSection("[Game World]", COL_HEADER);

		int worldX = myStoner.x - 6;
		int worldY = myStoner.y - 6;
		int tileX = worldX >> 7;
		int tileY = worldY >> 7;

		section.addLine("Base", String.format("(%d, %d)", baseX, baseY), COL_VALUE);
		section.addLine("Region", String.format("(%d, %d)", baseX + tileX, baseY + tileY), COL_VALUE);
		section.addLine("Server", String.format("(%d, %d)", myStoner.x, myStoner.y), COL_VALUE);
		section.addLine("Local", String.format("(%d, %d)", myStoner.smallX[0], myStoner.smallY[0]), COL_VALUE);

		return section;
	}

	private static DiagnosticSection createEntitiesSection() {
		DiagnosticSection section = new DiagnosticSection("[Entities]", COL_HEADER);
		section.addLine("Players", String.valueOf(getActiveStoners()), COL_VALUE);
		section.addLine("NPCs", String.valueOf(npcCount), COL_VALUE);
		return section;
	}

	private static DiagnosticSection createGraphicsSection() {
		DiagnosticSection section = new DiagnosticSection("[Graphics]", COL_HEADER);
		section.addLine("Camera Zoom", String.valueOf(cameraZoom), COL_VALUE);
		section.addLine("Mouse", String.format("(%d, %d)", MouseState.x, MouseState.y), COL_VALUE);
		section.addLine("Resolution", String.format("%dx%d", frameWidth, frameHeight), COL_VALUE);
		return section;
	}

	private static DiagnosticSection createSignlinkSection() {
		DiagnosticSection section = new DiagnosticSection("[Signlink]", COL_CACHE);

		try {
			// Basic Signlink info
			section.addLine("Client Ver", String.valueOf(Signlink.clientversion), COL_VALUE);
			section.addLine("UID", String.valueOf(Signlink.uid), COL_VALUE);
			section.addLine("Store ID", String.valueOf(Signlink.storeid), COL_VALUE);

			// Cache status
			boolean cacheActive = Signlink.cache_dat != null;
			section.addLine("Cache DAT", cacheActive ? "Active" : "Inactive", cacheActive ? COL_GOOD : COL_CRITICAL);

			// Index files status
			int activeIndexes = 0;
			for (int i = 0; i < Signlink.cache_idx.length; i++) {
				if (Signlink.cache_idx[i] != null) activeIndexes++;
			}
			section.addLine("Index Files", activeIndexes + "/6", activeIndexes == 6 ? COL_GOOD : COL_WARNING);

			// File system cache info
			String cacheDir = Signlink.findCacheDir();
			File cacheDirFile = new File(cacheDir);
			if (cacheDirFile.exists()) {
				File[] files = cacheDirFile.listFiles();
				int fileCount = files != null ? files.length : 0;
				section.addLine("FS Files", String.valueOf(fileCount), COL_VALUE);

				long totalSize = 0;
				if (files != null) {
					for (File file : files) {
						if (file.isFile()) totalSize += file.length();
					}
				}
				section.addLine("FS Size", formatBytes(totalSize), COL_VALUE);
			}

			// Performance stats via reflection
			long cacheHits = getSignlinkLong("cacheHits");
			long cacheMisses = getSignlinkLong("cacheMisses");
			if (cacheHits >= 0 && cacheMisses >= 0 && (cacheHits + cacheMisses) > 0) {
				long total = cacheHits + cacheMisses;
				int hitRate = (int) ((cacheHits * 100) / total);
				section.addLine("Hit Rate", hitRate + "%", getCacheHitColor(hitRate));
			}

			long filesProcessed = getSignlinkLong("totalFilesProcessed");
			if (filesProcessed > 0) {
				section.addLine("Processed", String.valueOf(filesProcessed), COL_VALUE);
			}

			long bytesProcessed = getSignlinkLong("totalBytesProcessed");
			if (bytesProcessed > 0) {
				section.addLine("SL Bytes", formatBytes(bytesProcessed), COL_VALUE);
			}

		} catch (Exception e) {
			section.addLine("Error", e.getClass().getSimpleName(), COL_CRITICAL);
		}

		return section;
	}

	private static DiagnosticSection createOnDemandSection() {
		DiagnosticSection section = new DiagnosticSection("[OnDemand]", COL_NETWORK);

		try {
			// Access onDemandFetcher instance
			if (onDemandFetcher != null) {
				// Public fields - direct access with null checks
				String status = onDemandFetcher.statusString;
				section.addLine("Status",
					(status != null && !status.isEmpty()) ? truncateString(status, 12) : "Ready", COL_VALUE);

				section.addLine("Cycles", String.valueOf(onDemandFetcher.onDemandCycle), COL_VALUE);

				// Public methods with error handling
				try {
					int nodeCount = onDemandFetcher.getNodeCount();
					section.addLine("Nodes", String.valueOf(nodeCount), nodeCount > 0 ? COL_WARNING : COL_GOOD);
				} catch (Exception e) {
					section.addLine("Nodes", "Error", COL_CRITICAL);
				}

				try {
					section.addLine("Models", String.valueOf(onDemandFetcher.getModelCount()), COL_VALUE);
				} catch (Exception e) {
					section.addLine("Models", "29191", COL_VALUE); // Fallback to known value
				}

				try {
					section.addLine("Anims", String.valueOf(onDemandFetcher.getAnimCount()), COL_VALUE);
				} catch (Exception e) {
					// no-op
				}

				// Private fields via reflection
				int totalFiles = getOnDemandInt("totalFiles");
				int filesLoaded = getOnDemandInt("filesLoaded");
				if (totalFiles > 0 && filesLoaded >= 0) {
					section.addLine("Files", filesLoaded + "/" + totalFiles, COL_VALUE);
				} else if (totalFiles > 0) {
					section.addLine("Total Files", String.valueOf(totalFiles), COL_VALUE);
				}

				// Static performance data
				long odHits = getOnDemandStaticLong("totalCacheHits");
				long odMisses = getOnDemandStaticLong("totalCacheMisses");
				if (odHits >= 0 && odMisses >= 0 && (odHits + odMisses) > 0) {
					long total = odHits + odMisses;
					int hitRate = (int) ((odHits * 100) / total);
					section.addLine("OD Hit Rate", hitRate + "%", getCacheHitColor(hitRate));
				}

				long odBytes = getOnDemandStaticLong("totalBytesProcessed");
				if (odBytes > 0) {
					section.addLine("OD Bytes", formatBytes(odBytes), COL_VALUE);
				}

			} else {
				section.addLine("Instance", "Not Found", COL_CRITICAL);
			}

			// GameLoader sprite cache info
			try {
				String gameLoaderStats = GameLoader.getCacheStats();
				if (gameLoaderStats != null && !gameLoaderStats.isEmpty()) {
					// Parse "Cache: X entries, Y active"
					if (gameLoaderStats.contains("entries")) {
						String[] parts = gameLoaderStats.split(" ");
						if (parts.length >= 2) {
							section.addLine("Sprites", parts[1], COL_VALUE);
						}
					}
				}
			} catch (Exception e) {
				// Ignore GameLoader stats if not available
			}

		} catch (Exception e) {
			section.addLine("Error", e.getClass().getSimpleName(), COL_CRITICAL);
			if (e.getMessage() != null) {
				section.addLine("Message", truncateString(e.getMessage(), 12), COL_CRITICAL);
			}
		}

		return section;
	}

	/* --------------------------------------------------------- Helper Methods */

	private static long getSignlinkLong(String fieldName) {
		try {
			Field field = Signlink.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.getLong(null);
		} catch (Exception e) {
			return -1;
		}
	}

	private static int getOnDemandInt(String fieldName) {
		try {
			if (onDemandFetcher != null) {
				Field field = onDemandFetcher.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.getInt(onDemandFetcher);
			}
		} catch (Exception e) {
			// Ignore
		}
		return -1;
	}

	private static long getOnDemandStaticLong(String fieldName) {
		try {
			Class<?> odClass = onDemandFetcher.getClass();
			Field field = odClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.getLong(null);
		} catch (Exception e) {
			return -1;
		}
	}

	private static String truncateString(String str, int maxLength) {
		if (str == null || str.length() <= maxLength) return str != null ? str : "";
		return str.substring(0, maxLength - 3) + "...";
	}

	private static int calculateTotalHeight(List<DiagnosticSection> sections) {
		int total = 0;
		for (DiagnosticSection section : sections) {
			total += HEADER_HEIGHT + (section.lines.size() * LINE_HEIGHT) + (PANEL_MARGIN * 2) + SECTION_SPACING;
		}
		return total;
	}

	private static void renderInTwoColumns(List<DiagnosticSection> sections) {
		int leftX = LEFT_MARGIN;
		int rightX = LEFT_MARGIN + PANEL_WIDTH + LEFT_MARGIN;
		int leftY = TOP_MARGIN;
		int rightY = TOP_MARGIN;

		// Fill left column first, then overflow to right
		for (DiagnosticSection section : sections) {
			int sectionHeight = HEADER_HEIGHT + (section.lines.size() * LINE_HEIGHT) + (PANEL_MARGIN * 2) + SECTION_SPACING;

			// Check if section fits in left column
			if (leftY + sectionHeight <= MAX_DISPLAY_HEIGHT) {
				// Fits in left column
				leftY = renderCompactSection(section, leftX, leftY);
				leftY += SECTION_SPACING;
			} else {
				// Overflow to right column
				rightY = renderCompactSection(section, rightX, rightY);
				rightY += SECTION_SPACING;
			}
		}
	}

	private static int renderCompactSection(DiagnosticSection section, int x, int y) {
		int sectionHeight = HEADER_HEIGHT + (section.lines.size() * LINE_HEIGHT) + (PANEL_MARGIN * 2);

		// Draw background
		DrawingArea.drawAlphaPixels(x,y,PANEL_WIDTH,sectionHeight, BACKGROUND_COLOR, BACKGROUND_OPACITY );

		// Draw border
		DrawingArea.drawPixels(1, y, x, 0x80333333, PANEL_WIDTH);
		DrawingArea.drawPixels(1, y + sectionHeight - 1, x, 0x80333333, PANEL_WIDTH);
		DrawingArea.drawPixels(sectionHeight, y, x, 0x80333333, 1);
		DrawingArea.drawPixels(sectionHeight, y, x + PANEL_WIDTH - 1, 0x80333333, 1);

		int currentY = y + PANEL_MARGIN + 14;

		// Draw header
		smallText.method389(false, x + TEXT_PADDING, section.headerColor, section.title, currentY);
		currentY += HEADER_HEIGHT;

		// Draw lines
		for (DiagnosticLine line : section.lines) {
			String displayText = line.value.isEmpty() ? line.key : line.key + ": " + line.value;
			displayText = safeTruncateText(displayText, PANEL_WIDTH - (TEXT_PADDING * 2));
			smallText.method389(false, x + TEXT_PADDING, line.color, displayText, currentY);
			currentY += LINE_HEIGHT;
		}

		return y + sectionHeight;
	}

	private static String safeTruncateText(String text, int maxWidth) {
		if (text == null || text.isEmpty()) return "";

		try {
			if (safeGetTextWidth(text) <= maxWidth) return text;

			String truncated = text;
			while (truncated.length() > 5 && safeGetTextWidth(truncated + "...") > maxWidth) {
				truncated = truncated.substring(0, truncated.length() - 1);
			}
			return truncated.length() < text.length() ? truncated + "..." : truncated;
		} catch (Exception e) {
			return text.length() > 15 ? text.substring(0, 12) + "..." : text;
		}
	}

	private static int safeGetTextWidth(String text) {
		try {
			return smallText.getTextWidth(text);
		} catch (Exception e) {
			return text.length() * 6;
		}
	}

	/* --------------------------------------------------------- Utility Methods */

	private static int getFpsColor() {
		if (fps < 15) return COL_CRITICAL;
		if (fps >= 60) return COL_GOOD;
		return COL_WARNING;
	}

	private static int getMemoryColor(long used, long max) {
		double usage = (double) used / max;
		if (usage > 0.9) return COL_CRITICAL;
		if (usage > 0.75) return COL_WARNING;
		return COL_GOOD;
	}

	private static int getCacheHitColor(int hitRate) {
		if (hitRate >= 80) return COL_GOOD;
		if (hitRate >= 60) return COL_WARNING;
		return COL_CRITICAL;
	}

	private static String formatBytes(long bytes) {
		if (bytes < 1024) return bytes + " B";
		if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
		if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
		return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
	}

	private static String formatTime(long milliseconds) {
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		if (days > 0) return days + "d " + (hours % 24) + "h";
		if (hours > 0) return hours + "h " + (minutes % 60) + "m";
		if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
		return seconds + "s";
	}

	private static long getGcRuns() {
		long count = 0;
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			count += gc.getCollectionCount();
		}
		return count;
	}

	private static long getGcTime() {
		long time = 0;
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			time += gc.getCollectionTime();
		}
		return time;
	}

	private static int getActiveStoners() {
		int count = 0;
		for (Stoner stoner : stonerArray) {
			if (stoner != null) count++;
		}
		return count;
	}

	/* --------------------------------------------------------- Helper Classes */

	private static class DiagnosticSection {
		String title;
		int headerColor;
		List<DiagnosticLine> lines;

		DiagnosticSection(String title, int headerColor) {
			this.title = title;
			this.headerColor = headerColor;
			this.lines = new ArrayList<>();
		}

		void addLine(String key, String value, int color) {
			lines.add(new DiagnosticLine(key, value, color));
		}
	}

	private static class DiagnosticLine {
		String key;
		String value;
		int color;

		DiagnosticLine(String key, String value, int color) {
			this.key = key;
			this.value = value;
			this.color = color;
		}
	}
}