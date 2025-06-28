package com.bestbudz.engine.core.loading.panels;

import com.bestbudz.engine.core.loading.LoadingUtilities;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

/**
 * Progress panel containing status, progress bars, and details
 */
public class ProgressPanel extends JPanel {
	private JLabel statusLabel;
	private JLabel detailLabel;
	private JProgressBar mainProgressBar;
	private JProgressBar detailProgressBar;
	private final AtomicInteger currentProgress;
	private final LoadingUtilities.FontManager fontManager;

	public ProgressPanel(LoadingUtilities.FontManager fontManager, AtomicInteger currentProgress) {
		this.fontManager = fontManager;
		this.currentProgress = currentProgress;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		createComponents();
		layoutComponents();
	}

	private void createComponents() {
		statusLabel = new JLabel("Sparking a fat spliff...", JLabel.CENTER);
		statusLabel.setFont(fontManager.statusFont);
		statusLabel.setForeground(SECONDARY_TEXT);

		detailLabel = new JLabel("Looking for lighter to spark spliff", JLabel.CENTER);
		detailLabel.setFont(fontManager.detailFont);
		detailLabel.setForeground(SECONDARY_TEXT);

		mainProgressBar = LoadingUtilities.createStyledProgressBar();
		mainProgressBar.setPreferredSize(new Dimension(500, 8));

		detailProgressBar = LoadingUtilities.createStyledProgressBar();
		detailProgressBar.setPreferredSize(new Dimension(500, 4));
	}

	private void layoutComponents() {
		statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		detailProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		add(Box.createVerticalStrut(16));
		add(statusLabel);
		add(Box.createVerticalStrut(8));
		add(mainProgressBar);
		add(Box.createVerticalStrut(8));
		add(detailLabel);
		add(Box.createVerticalStrut(4));
		add(detailProgressBar);
	}

	/**
	 * Update main progress
	 */
	public void updateProgress(int progress) {
		SwingUtilities.invokeLater(() -> {
			mainProgressBar.setValue(Math.max(0, Math.min(100, progress)));
		});
	}

	/**
	 * Update status text
	 */
	public void updateStatus(String status) {
		SwingUtilities.invokeLater(() -> {
			statusLabel.setText(status);
			statusLabel.setForeground(SECONDARY_TEXT); // Reset color
		});
	}

	/**
	 * Update detail text
	 */
	public void updateDetail(String detail) {
		SwingUtilities.invokeLater(() -> detailLabel.setText(detail));
	}

	/**
	 * Update detail progress bar
	 */
	public void updateDetailProgress(int progress) {
		SwingUtilities.invokeLater(() -> {
			detailProgressBar.setValue(Math.max(0, Math.min(100, progress)));
		});
	}

	/**
	 * Set error state
	 */
	public void setErrorState(String message) {
		SwingUtilities.invokeLater(() -> {
			statusLabel.setText("❌ " + message);
			statusLabel.setForeground(ERROR_COLOR);
		});
	}

	/**
	 * Get current progress value
	 */
	public int getCurrentProgress() {
		return currentProgress.get();
	}
}