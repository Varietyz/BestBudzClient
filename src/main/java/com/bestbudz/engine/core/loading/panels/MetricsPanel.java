package com.bestbudz.engine.core.loading.panels;

import com.bestbudz.engine.core.loading.LoadingUtilities;
import javax.swing.*;
import java.awt.*;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

/**
 * Metrics panel displaying current phase and memory usage
 */
public class MetricsPanel extends JPanel {
	private JLabel currentPhaseLabel;
	private JLabel memoryUsageLabel;
	private final LoadingUtilities.FontManager fontManager;
	private LoadingPhase currentPhase = LoadingPhase.INITIALIZING;
	private double memoryUsage = 0.0;

	public MetricsPanel(LoadingUtilities.FontManager fontManager) {
		this.fontManager = fontManager;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(new Color(22, 22, 26));
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(50, 50, 54), 1),
			BorderFactory.createEmptyBorder(12, 16, 12, 16)
		));

		createComponents();
		layoutComponents();
	}

	private void createComponents() {
		currentPhaseLabel = new JLabel("Phase: Getting Ready");
		currentPhaseLabel.setFont(fontManager.detailFont);
		currentPhaseLabel.setForeground(SECONDARY_TEXT);

		memoryUsageLabel = new JLabel("Memory usage: 0%");
		memoryUsageLabel.setFont(fontManager.detailFont);
		memoryUsageLabel.setForeground(SECONDARY_TEXT);
	}

	private void layoutComponents() {
		JPanel metricsRow = new JPanel(new BorderLayout());
		metricsRow.setOpaque(false);
		metricsRow.add(currentPhaseLabel, BorderLayout.WEST);
		metricsRow.add(memoryUsageLabel, BorderLayout.EAST);

		add(Box.createVerticalStrut(8));
		add(metricsRow);
	}

	/**
	 * Update current phase
	 */
	public void updatePhase(LoadingPhase phase) {
		this.currentPhase = phase;
		SwingUtilities.invokeLater(() -> {
			currentPhaseLabel.setText("Phase: " + phase.displayName);
		});
	}

	/**
	 * Update memory usage
	 */
	public void updateMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
		SwingUtilities.invokeLater(() -> {
			memoryUsageLabel.setText(String.format("Memory usage: %.0f%%", memoryUsage));
		});
	}

	/**
	 * Get current phase
	 */
	public LoadingPhase getCurrentPhase() {
		return currentPhase;
	}

	/**
	 * Get current memory usage
	 */
	public double getMemoryUsage() {
		return memoryUsage;
	}
}