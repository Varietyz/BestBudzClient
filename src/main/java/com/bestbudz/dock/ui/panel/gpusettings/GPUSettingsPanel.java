package com.bestbudz.dock.ui.panel.gpusettings;

import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.gpu.postprocess.EnvironmentConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * GPU Settings dock panel for real-time ENB-like tweaking of all rendering parameters.
 * All sliders write directly to EnvironmentConfig static fields -- changes are instant/live.
 *
 * Follows dock panel conventions: implements UIPanel, returns JPanel from getComponent().
 */
public class GPUSettingsPanel implements UIPanel {

	private final JPanel panel;
	private final List<Runnable> refreshCallbacks = new ArrayList<>();

	public GPUSettingsPanel() {
		panel = new JPanel(new BorderLayout());
		panel.setBackground(GPUSettingsStyle.PANEL_BG);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(GPUSettingsStyle.PANEL_BG);

		// ---- Presets section ----
		content.add(createPresetsSection());
		content.add(Box.createVerticalStrut(GPUSettingsStyle.SECTION_GAP));

		// ---- Lighting section ----
		content.add(createSectionHeader(GPUSettingsConfig.SECTION_LIGHTING));
		content.add(createSliderRow("Sun Elevation", GPUSettingsConfig.SUN_ELEVATION_MIN, GPUSettingsConfig.SUN_ELEVATION_MAX,
			() -> EnvironmentConfig.sunElevation, v -> EnvironmentConfig.sunElevation = v));
		content.add(createSliderRow("Sun Azimuth", GPUSettingsConfig.SUN_AZIMUTH_MIN, GPUSettingsConfig.SUN_AZIMUTH_MAX,
			() -> EnvironmentConfig.sunAzimuth, v -> EnvironmentConfig.sunAzimuth = v));
		content.add(createSliderRow("Sun Strength", GPUSettingsConfig.STRENGTH_MIN, GPUSettingsConfig.STRENGTH_MAX,
			() -> EnvironmentConfig.sunStrength, v -> EnvironmentConfig.sunStrength = v));
		content.add(createColorRow("Sun Color",
			() -> EnvironmentConfig.sunColorR, v -> EnvironmentConfig.sunColorR = v,
			() -> EnvironmentConfig.sunColorG, v -> EnvironmentConfig.sunColorG = v,
			() -> EnvironmentConfig.sunColorB, v -> EnvironmentConfig.sunColorB = v));
		content.add(createSliderRow("Ambient Strength", GPUSettingsConfig.STRENGTH_MIN, GPUSettingsConfig.AMBIENT_STRENGTH_MAX,
			() -> EnvironmentConfig.ambientStrength, v -> EnvironmentConfig.ambientStrength = v));
		content.add(createColorRow("Ambient Color",
			() -> EnvironmentConfig.ambientColorR, v -> EnvironmentConfig.ambientColorR = v,
			() -> EnvironmentConfig.ambientColorG, v -> EnvironmentConfig.ambientColorG = v,
			() -> EnvironmentConfig.ambientColorB, v -> EnvironmentConfig.ambientColorB = v));

		content.add(Box.createVerticalStrut(GPUSettingsStyle.SECTION_GAP));

		// ---- Atmosphere section ----
		content.add(createSectionHeader(GPUSettingsConfig.SECTION_ATMOSPHERE));
		content.add(createColorRow("Sky Zenith",
			() -> EnvironmentConfig.skyZenithR, v -> EnvironmentConfig.skyZenithR = v,
			() -> EnvironmentConfig.skyZenithG, v -> EnvironmentConfig.skyZenithG = v,
			() -> EnvironmentConfig.skyZenithB, v -> EnvironmentConfig.skyZenithB = v));
		content.add(createColorRow("Sky Horizon",
			() -> EnvironmentConfig.skyHorizonR, v -> EnvironmentConfig.skyHorizonR = v,
			() -> EnvironmentConfig.skyHorizonG, v -> EnvironmentConfig.skyHorizonG = v,
			() -> EnvironmentConfig.skyHorizonB, v -> EnvironmentConfig.skyHorizonB = v));
		content.add(createSliderRow("Sun Size", GPUSettingsConfig.SUN_SIZE_MIN, GPUSettingsConfig.SUN_SIZE_MAX,
			() -> EnvironmentConfig.sunSize, v -> EnvironmentConfig.sunSize = v));
		content.add(createSliderRow("Fog Start", GPUSettingsConfig.FOG_START_MIN, GPUSettingsConfig.FOG_START_MAX,
			() -> EnvironmentConfig.fogStart, v -> EnvironmentConfig.fogStart = v));
		content.add(createSliderRow("Fog End", GPUSettingsConfig.FOG_END_MIN, GPUSettingsConfig.FOG_END_MAX,
			() -> EnvironmentConfig.fogEnd, v -> EnvironmentConfig.fogEnd = v));
		content.add(createColorRow("Fog Color",
			() -> EnvironmentConfig.fogColorR, v -> EnvironmentConfig.fogColorR = v,
			() -> EnvironmentConfig.fogColorG, v -> EnvironmentConfig.fogColorG = v,
			() -> EnvironmentConfig.fogColorB, v -> EnvironmentConfig.fogColorB = v));

		content.add(Box.createVerticalStrut(GPUSettingsStyle.SECTION_GAP));

		// ---- Rendering section ----
		content.add(createSectionHeader("Rendering"));
		content.add(createToggleRow("Animated Textures", () -> EnvironmentConfig.enableAnimatedTextures, v -> EnvironmentConfig.enableAnimatedTextures = v));

		content.add(Box.createVerticalStrut(GPUSettingsStyle.SECTION_GAP));

		// ---- Post-Processing section ----
		content.add(createSectionHeader(GPUSettingsConfig.SECTION_POST_PROCESSING));
		content.add(createToggleRow("Bloom", () -> EnvironmentConfig.bloomEnabled, v -> EnvironmentConfig.bloomEnabled = v));
		content.add(createSliderRow("Bloom Threshold", GPUSettingsConfig.BLOOM_THRESHOLD_MIN, GPUSettingsConfig.BLOOM_THRESHOLD_MAX,
			() -> EnvironmentConfig.bloomThreshold, v -> EnvironmentConfig.bloomThreshold = v));
		content.add(createSliderRow("Bloom Intensity", GPUSettingsConfig.BLOOM_INTENSITY_MIN, GPUSettingsConfig.BLOOM_INTENSITY_MAX,
			() -> EnvironmentConfig.bloomIntensity, v -> EnvironmentConfig.bloomIntensity = v));
		content.add(createToggleRow("SSAO", () -> EnvironmentConfig.ssaoEnabled, v -> EnvironmentConfig.ssaoEnabled = v));
		content.add(createSliderRow("SSAO Radius", GPUSettingsConfig.SSAO_RADIUS_MIN, GPUSettingsConfig.SSAO_RADIUS_MAX,
			() -> EnvironmentConfig.ssaoRadius, v -> EnvironmentConfig.ssaoRadius = v));
		content.add(createSliderRow("SSAO Strength", GPUSettingsConfig.SSAO_STRENGTH_MIN, GPUSettingsConfig.SSAO_STRENGTH_MAX,
			() -> EnvironmentConfig.ssaoStrength, v -> EnvironmentConfig.ssaoStrength = v));
		content.add(createSliderRow("Exposure", GPUSettingsConfig.EXPOSURE_MIN, GPUSettingsConfig.EXPOSURE_MAX,
			() -> EnvironmentConfig.exposure, v -> EnvironmentConfig.exposure = v));
		content.add(createSliderRow("Gamma", GPUSettingsConfig.GAMMA_MIN, GPUSettingsConfig.GAMMA_MAX,
			() -> EnvironmentConfig.gamma, v -> EnvironmentConfig.gamma = v));
		content.add(createSliderRow("Color Grading", GPUSettingsConfig.COLOR_GRADING_MIN, GPUSettingsConfig.COLOR_GRADING_MAX,
			() -> EnvironmentConfig.colorGradingStrength, v -> EnvironmentConfig.colorGradingStrength = v));

		// Bottom spacer
		content.add(Box.createVerticalStrut(20));

		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setBackground(GPUSettingsStyle.PANEL_BG);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// Slim scrollbar
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

		panel.add(scrollPane, BorderLayout.CENTER);
	}

	// ========== Section Header ==========

	private JPanel createSectionHeader(String title) {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(GPUSettingsStyle.SECTION_HEADER_BG);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
		header.setPreferredSize(new Dimension(0, 26));
		header.setBorder(new EmptyBorder(2, 8, 2, 8));

		JLabel label = new JLabel(title);
		label.setFont(GPUSettingsStyle.SECTION_HEADER_FONT);
		label.setForeground(GPUSettingsStyle.TEXT_ACCENT);
		header.add(label, BorderLayout.WEST);

		return header;
	}

	// ========== Slider Row ==========

	private JPanel createSliderRow(String name, float min, float max,
								   Supplier<Float> getter, Consumer<Float> setter) {
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(GPUSettingsStyle.SECTION_BG);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, GPUSettingsStyle.ROW_HEIGHT));
		row.setPreferredSize(new Dimension(0, GPUSettingsStyle.ROW_HEIGHT));
		row.setBorder(new EmptyBorder(2, 8, 2, 8));

		JLabel label = new JLabel(name);
		label.setFont(GPUSettingsStyle.LABEL_FONT);
		label.setForeground(GPUSettingsStyle.TEXT_PRIMARY);
		label.setPreferredSize(new Dimension(GPUSettingsStyle.LABEL_WIDTH, 0));
		row.add(label, BorderLayout.WEST);

		// Slider: 0-1000 internal range, mapped to min-max
		int sliderSteps = 1000;
		JSlider slider = new JSlider(0, sliderSteps);
		slider.setBackground(GPUSettingsStyle.SECTION_BG);
		slider.setForeground(GPUSettingsStyle.SLIDER_FILL);
		slider.setFocusable(false);
		slider.setOpaque(false);

		// Value display label
		JLabel valueLabel = new JLabel();
		valueLabel.setFont(GPUSettingsStyle.VALUE_FONT);
		valueLabel.setForeground(GPUSettingsStyle.TEXT_SECONDARY);
		valueLabel.setPreferredSize(new Dimension(40, 0));
		valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		// Set initial value
		float currentVal = getter.get();
		int sliderVal = Math.round((currentVal - min) / (max - min) * sliderSteps);
		slider.setValue(Math.max(0, Math.min(sliderSteps, sliderVal)));
		valueLabel.setText(formatValue(currentVal, min, max));

		slider.addChangeListener(e -> {
			float val = min + (float) slider.getValue() / sliderSteps * (max - min);
			setter.accept(val);
			valueLabel.setText(formatValue(val, min, max));
		});

		// Register refresh callback
		refreshCallbacks.add(() -> {
			float val = getter.get();
			int sv = Math.round((val - min) / (max - min) * sliderSteps);
			slider.setValue(Math.max(0, Math.min(sliderSteps, sv)));
			valueLabel.setText(formatValue(val, min, max));
		});

		row.add(slider, BorderLayout.CENTER);
		row.add(valueLabel, BorderLayout.EAST);
		return row;
	}

	private String formatValue(float val, float min, float max) {
		if (max - min > 100) {
			return String.valueOf(Math.round(val));
		}
		return String.format("%.2f", val);
	}

	// ========== Color Row (3 RGB sliders compact) ==========

	private JPanel createColorRow(String name,
								  Supplier<Float> getR, Consumer<Float> setR,
								  Supplier<Float> getG, Consumer<Float> setG,
								  Supplier<Float> getB, Consumer<Float> setB) {
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(GPUSettingsStyle.SECTION_BG);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, GPUSettingsStyle.ROW_HEIGHT));
		row.setPreferredSize(new Dimension(0, GPUSettingsStyle.ROW_HEIGHT));
		row.setBorder(new EmptyBorder(2, 8, 2, 8));

		JLabel label = new JLabel(name);
		label.setFont(GPUSettingsStyle.LABEL_FONT);
		label.setForeground(GPUSettingsStyle.TEXT_PRIMARY);
		label.setPreferredSize(new Dimension(GPUSettingsStyle.LABEL_WIDTH, 0));
		row.add(label, BorderLayout.WEST);

		// 3 mini sliders for R, G, B
		JPanel slidersPanel = new JPanel(new GridLayout(1, 3, 2, 0));
		slidersPanel.setBackground(GPUSettingsStyle.SECTION_BG);
		slidersPanel.setOpaque(false);

		slidersPanel.add(createMiniSlider(new Color(255, 100, 100), getR, setR));
		slidersPanel.add(createMiniSlider(new Color(100, 255, 100), getG, setG));
		slidersPanel.add(createMiniSlider(new Color(100, 130, 255), getB, setB));

		// Color preview swatch
		JPanel swatch = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				float r = Math.max(0, Math.min(1, getR.get()));
				float gv = Math.max(0, Math.min(1, getG.get()));
				float b = Math.max(0, Math.min(1, getB.get()));
				g.setColor(new Color(r, gv, b));
				g.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 4, 4);
			}
		};
		swatch.setPreferredSize(new Dimension(20, 0));
		swatch.setBackground(GPUSettingsStyle.SECTION_BG);
		swatch.setOpaque(false);

		// Refresh swatch when sliders change
		refreshCallbacks.add(swatch::repaint);

		row.add(slidersPanel, BorderLayout.CENTER);
		row.add(swatch, BorderLayout.EAST);
		return row;
	}

	private JSlider createMiniSlider(Color trackColor, Supplier<Float> getter, Consumer<Float> setter) {
		JSlider slider = new JSlider(0, 255);
		slider.setBackground(GPUSettingsStyle.SECTION_BG);
		slider.setForeground(trackColor);
		slider.setFocusable(false);
		slider.setOpaque(false);

		int initVal = Math.round(getter.get() * 255);
		slider.setValue(Math.max(0, Math.min(255, initVal)));

		slider.addChangeListener(e -> {
			float val = slider.getValue() / 255.0f;
			setter.accept(val);
			// Trigger swatch repaints
			for (Runnable cb : refreshCallbacks) {
				if (cb instanceof SwatchRefresh) {
					cb.run();
				}
			}
			panel.repaint();
		});

		refreshCallbacks.add(() -> {
			int v = Math.round(getter.get() * 255);
			slider.setValue(Math.max(0, Math.min(255, v)));
		});

		return slider;
	}

	// ========== Toggle Row ==========

	private JPanel createToggleRow(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(GPUSettingsStyle.SECTION_BG);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, GPUSettingsStyle.ROW_HEIGHT));
		row.setPreferredSize(new Dimension(0, GPUSettingsStyle.ROW_HEIGHT));
		row.setBorder(new EmptyBorder(2, 8, 2, 8));

		JLabel label = new JLabel(name);
		label.setFont(GPUSettingsStyle.LABEL_FONT);
		label.setForeground(GPUSettingsStyle.TEXT_PRIMARY);
		row.add(label, BorderLayout.WEST);

		JToggleButton toggle = new JToggleButton() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int w = getWidth();
				int h = getHeight();
				int trackW = 36;
				int trackH = 18;
				int trackX = (w - trackW) / 2;
				int trackY = (h - trackH) / 2;

				// Track
				g2.setColor(isSelected() ? GPUSettingsStyle.TOGGLE_ON : GPUSettingsStyle.TOGGLE_OFF);
				g2.fillRoundRect(trackX, trackY, trackW, trackH, trackH, trackH);

				// Thumb
				int thumbD = trackH - 4;
				int thumbX = isSelected() ? trackX + trackW - thumbD - 2 : trackX + 2;
				int thumbY = trackY + 2;
				g2.setColor(Color.WHITE);
				g2.fillOval(thumbX, thumbY, thumbD, thumbD);

				g2.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(50, 24);
			}
		};

		toggle.setSelected(getter.get());
		toggle.setOpaque(false);
		toggle.setBorderPainted(false);
		toggle.setContentAreaFilled(false);
		toggle.setFocusPainted(false);
		toggle.addActionListener(e -> setter.accept(toggle.isSelected()));

		refreshCallbacks.add(() -> toggle.setSelected(getter.get()));

		JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		toggleContainer.setOpaque(false);
		toggleContainer.add(toggle);
		row.add(toggleContainer, BorderLayout.EAST);

		return row;
	}

	// ========== Presets Section ==========

	private JPanel createPresetsSection() {
		JPanel section = new JPanel();
		section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
		section.setBackground(GPUSettingsStyle.SECTION_BG);

		section.add(createSectionHeader(GPUSettingsConfig.SECTION_PRESETS));

		JPanel btnRow = new JPanel(new GridLayout(1, 4, 3, 0));
		btnRow.setBackground(GPUSettingsStyle.SECTION_BG);
		btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		btnRow.setPreferredSize(new Dimension(0, 32));
		btnRow.setBorder(new EmptyBorder(4, 8, 4, 8));

		btnRow.add(createPresetButton("Default", e -> {
			EnvironmentConfig.presetDefault();
			refreshAll();
		}));
		btnRow.add(createPresetButton("Cinematic", e -> {
			EnvironmentConfig.presetCinematic();
			refreshAll();
		}));
		btnRow.add(createPresetButton("Vivid", e -> {
			EnvironmentConfig.presetVivid();
			refreshAll();
		}));
		btnRow.add(createPresetButton("Dark", e -> {
			EnvironmentConfig.presetDarkFantasy();
			refreshAll();
		}));

		section.add(btnRow);
		return section;
	}

	private JButton createPresetButton(String name, ActionListener action) {
		JButton btn = new JButton(name) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color bg = getModel().isRollover()
					? GPUSettingsStyle.PRESET_BTN_HOVER
					: GPUSettingsStyle.PRESET_BTN_BG;
				if (getModel().isPressed()) {
					bg = GPUSettingsStyle.PRESET_BTN_ACTIVE;
				}

				g2.setColor(bg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

				g2.setFont(GPUSettingsStyle.PRESET_FONT);
				g2.setColor(GPUSettingsStyle.TEXT_PRIMARY);
				FontMetrics fm = g2.getFontMetrics();
				int textX = (getWidth() - fm.stringWidth(getText())) / 2;
				int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
				g2.drawString(getText(), textX, textY);

				g2.dispose();
			}
		};

		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.addActionListener(action);

		return btn;
	}

	// ========== Refresh ==========

	private void refreshAll() {
		SwingUtilities.invokeLater(() -> {
			for (Runnable cb : refreshCallbacks) {
				try {
					cb.run();
				} catch (Exception e) {
					// Ignore refresh errors
				}
			}
			panel.repaint();
		});
	}

	// ========== UIPanel Interface ==========

	@Override
	public String getPanelID() {
		return GPUSettingsConfig.PANEL_ID;
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {
		refreshAll();
	}

	@Override
	public void onDeactivate() {
		// No background updates to stop
	}

	@Override
	public void updateText() {
		// Nothing to poll -- all updates are push-based via sliders
	}

	@Override
	public void updateDockText(int index, String text) {
		// Not used by this panel
	}

	// Marker interface for swatch refresh identification
	private interface SwatchRefresh extends Runnable {}
}
