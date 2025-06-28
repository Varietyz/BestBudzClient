package com.bestbudz.engine.core.loading;

import com.bestbudz.engine.core.loading.panels.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

/**
 * Refactored user-friendly loading screen with clean progress display
 * and helpful status information. Now properly separated into components.
 */
public class LoadingVisual extends JFrame {

	// Core components
	private final LoadingUtilities.FontManager fontManager;
	private HeaderPanel headerPanel;
	private ProgressPanel progressPanel;
	private MetricsPanel metricsPanel;
	private LogPanel logPanel;
	private JPanel mainPanel;

	// State management
	private final AtomicInteger currentProgress = new AtomicInteger(0);
	private final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

	// User-friendly metrics
	private final Map<String, Integer> itemCounts = new ConcurrentHashMap<>();
	private int totalItemsLoaded = 0;

	// Animation and performance
	private Timer updateTimer;
	private Timer performanceTimer;
	private double memoryUsage = 0.0;
	private int frameCount = 0;

	// Current loading state
	private LoadingPhase currentPhase = LoadingPhase.INITIALIZING;

	public LoadingVisual() {
		this.fontManager = new LoadingUtilities.FontManager();

		setupWindow();
		createComponents();
		layoutComponents();
		startTimers();

		// Add initial log entry
		logPanel.addLogEntry("🚀 Starting game initialization...", LogLevel.INFO);
	}

	private void setupWindow() {
		setTitle("BestBudz - Loading Game");
		setSize(650, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);

		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
		getContentPane().setBackground(BACKGROUND);
	}

	private void createComponents() {
		// Create all panel components
		headerPanel = new HeaderPanel(fontManager, startTime);
		progressPanel = new ProgressPanel(fontManager, currentProgress);
		metricsPanel = new MetricsPanel(fontManager);
		logPanel = new LogPanel(fontManager, startTime);
	}

	private void layoutComponents() {
		setLayout(new BorderLayout());

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(CARD_BACKGROUND);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(40, 40, 44), 1),
			BorderFactory.createEmptyBorder(20, 24, 20, 24)
		));

		// Assembly
		mainPanel.add(headerPanel);
		mainPanel.add(progressPanel);
		mainPanel.add(Box.createVerticalStrut(16));
		mainPanel.add(metricsPanel);
		mainPanel.add(Box.createVerticalStrut(16));
		mainPanel.add(logPanel);

		add(mainPanel, BorderLayout.CENTER);
	}

	private void startTimers() {
		// Main update timer
		updateTimer = new Timer(50, e -> updateDisplay());
		updateTimer.start();

		// Performance monitoring timer
		performanceTimer = new Timer(1000, e -> updatePerformanceMetrics());
		performanceTimer.start();
	}

	private void updateDisplay() {
		SwingUtilities.invokeLater(() -> {
			// Update time in header
			headerPanel.updateElapsedTime();

			// Update progress
			progressPanel.updateProgress(currentProgress.get());

			// Process log queue
			logPanel.processLogQueue();

			// Update metrics
			metricsPanel.updatePhase(currentPhase);
			metricsPanel.updateMemoryUsage(memoryUsage);

			frameCount++;
		});
	}

	private void updatePerformanceMetrics() {
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maxMemory = runtime.maxMemory();
		memoryUsage = ((double) usedMemory / maxMemory) * 100;
	}

	// Public API methods
	public void updateProgress(int progress) {
		currentProgress.set(Math.max(0, Math.min(100, progress)));
	}

	public void updateProgress(int progress, String status) {
		updateProgress(progress);
		updateStatus(status);
	}

	public void updateStatus(String status) {
		progressPanel.updateStatus(status);
	}

	public void updateDetail(String detail) {
		progressPanel.updateDetail(detail);
	}

	public void updateDetailProgress(int progress) {
		progressPanel.updateDetailProgress(progress);
	}

	public void setPhase(LoadingPhase phase) {
		this.currentPhase = phase;
		progressPanel.updateStatus(phase.displayName);
		updateProgress(phase.startProgress);
		logPanel.addLogEntry("🎯 " + phase.displayName, LogLevel.INFO);
	}

	public void addLogEntry(String message, LogLevel level) {
		logPanel.addLogEntry(message, level);
	}

	// Simple metrics tracking methods
	public void updateItemCount(String itemType, int count) {
		itemCounts.put(itemType, count);
	}

	public void incrementItemCount(String itemType) {
		itemCounts.put(itemType, itemCounts.getOrDefault(itemType, 0) + 1);
		totalItemsLoaded++;
	}

	// Simplified methods (keeping interface compatibility)
	public void updateBytesProcessed(long bytes) {
		// For compatibility, but not displayed in user-friendly version
	}

	public void updateFilesProcessed(int files) {
		// For compatibility, but not displayed in user-friendly version
	}

	public void updateCacheStats(int hits, int misses) {
		// For compatibility, but not displayed in user-friendly version
	}

	public void incrementCacheHits() {
		// For compatibility, but not displayed in user-friendly version
	}

	public void incrementCacheMisses() {
		// For compatibility, but not displayed in user-friendly version
	}

	public void reportOperationTime(String operation, long timeMs) {
		// Only show significant operations to users
		if (timeMs > 100) { // Only show operations taking more than 100ms
			logPanel.addLogEntry(String.format("%s completed (%.1fs)", operation, timeMs / 1000.0), LogLevel.INFO);
		}
	}

	public void showLoader() {
		SwingUtilities.invokeLater(() -> {
			setVisible(true);
			toFront();
			requestFocus();
			logPanel.addLogEntry("🎮 Starting game...", LogLevel.INFO);
		});
	}

	public void closeLoader() {
		SwingUtilities.invokeLater(() -> {
			// Final friendly message
			logPanel.addLogEntry("🎉 Game loaded successfully!", LogLevel.SUCCESS);
			logPanel.addLogEntry(String.format("📦 Loaded %d items total", totalItemsLoaded), LogLevel.INFO);

			if (updateTimer != null) updateTimer.stop();
			if (performanceTimer != null) performanceTimer.stop();

			// Fade out effect
			Timer fadeTimer = new Timer(50, null);
			fadeTimer.addActionListener(new ActionListener() {
				float alpha = 1.0f;

				@Override
				public void actionPerformed(ActionEvent e) {
					alpha -= 0.05f;
					if (alpha <= 0) {
						fadeTimer.stop();
						setVisible(false);
						dispose();
					} else {
						setOpacity(Math.max(0.0f, alpha));
					}
				}
			});
			fadeTimer.start();
		});
	}

	// Utility methods for GameLoader integration
	public void reportError(String error) {
		logPanel.addLogEntry(error, LogLevel.ERROR);
		progressPanel.setErrorState("Something went wrong");
	}

	public void reportSuccess(String message) {
		logPanel.addLogEntry(message, LogLevel.SUCCESS);
	}

	public void reportWarning(String message) {
		logPanel.addLogEntry(message, LogLevel.WARNING);
	}

	public void reportMetric(String metric) {
		// For compatibility, but metrics are simplified for users
	}

	public void reportTiming(String operation, long timeMs) {
		reportOperationTime(operation, timeMs);
	}

	public void reportErrorWithContext(String operation, Exception e, Map<String, Object> context) {
		logPanel.addLogEntry(String.format("Error: %s failed", operation), LogLevel.ERROR);
	}

	// Getters for compatibility
	public LoadingPhase getCurrentPhase() {
		return currentPhase;
	}

	public double getMemoryUsage() {
		return memoryUsage;
	}

	public int getFPS() {
		return 0; // Not displayed in user-friendly version
	}

	public long getTotalBytesProcessed() {
		return 0; // Not displayed in user-friendly version
	}

	public long getTotalFilesProcessed() {
		return 0; // Not displayed in user-friendly version
	}

	public Map<String, Integer> getItemCounts() {
		return new HashMap<>(itemCounts);
	}

	public Map<String, Long> getPhaseTimes() {
		return new HashMap<>(); // Simplified for user-friendly version
	}

	public String getDetailedMetricsReport() {
		return String.format("Game loaded successfully!\nTotal items: %d\nLoading time: %s",
			totalItemsLoaded, LoadingUtilities.formatElapsedTime(startTime.get()));
	}
}