package com.bestbudz.dock.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RainbowHoverUtil {

	private static float globalHue = 0.6f;
	private static final float HUE_SHIFT_AMOUNT = 0.08f;
	private static final float SATURATION = 0.3f;
	private static final float BRIGHTNESS = 0.4f;

	private static final int FADE_OUT_DURATION = 500;
	private static final int COLOR_ANIMATION_FPS = 60;

	public interface HoverOverlayRenderer {
		void setHoverOverlay(Color overlayColor);
		void repaintComponent();
	}

	private static class HoverState {
		Color currentHoverOverlay = null;
		Timer colorTimer = null;
		boolean isHovering = false;

		void cleanup() {
			if (colorTimer != null && colorTimer.isRunning()) {
				colorTimer.stop();
			}
		}
	}

	public static synchronized Color getNextHoverColor() {
		Color color = Color.getHSBColor(globalHue, SATURATION, BRIGHTNESS);

		globalHue += HUE_SHIFT_AMOUNT;
		if (globalHue >= 1.0f) {
			globalHue -= 1.0f;
		}

		return color;
	}

	public static void applyRainbowHover(JComponent component, HoverOverlayRenderer renderer) {
		HoverState state = new HoverState();

		component.putClientProperty("RainbowHoverState", state);

		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				state.isHovering = true;

				if (state.colorTimer != null && state.colorTimer.isRunning()) {
					state.colorTimer.stop();
				}

				state.currentHoverOverlay = getNextHoverColor();
				renderer.setHoverOverlay(state.currentHoverOverlay);
				renderer.repaintComponent();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				state.isHovering = false;
				animateHoverFadeOut(state, renderer);
			}
		});
	}

	public static void applyRainbowHover(JButton button) {
		Color originalBackground = button.getBackground();
		HoverState state = new HoverState();

		button.putClientProperty("RainbowHoverState", state);
		button.putClientProperty("OriginalBackground", originalBackground);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				state.isHovering = true;

				if (state.colorTimer != null && state.colorTimer.isRunning()) {
					state.colorTimer.stop();
				}

				state.currentHoverOverlay = getNextHoverColor();
				button.setBackground(blendColors(originalBackground, state.currentHoverOverlay, 0.6f));
				button.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				state.isHovering = false;
				animateButtonFadeOut(button, state, originalBackground);
			}
		});
	}

	public static void applyRainbowHover(JComponent component, Color originalBackground) {
		HoverState state = new HoverState();

		component.putClientProperty("RainbowHoverState", state);
		component.putClientProperty("OriginalBackground", originalBackground);

		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				state.isHovering = true;

				if (state.colorTimer != null && state.colorTimer.isRunning()) {
					state.colorTimer.stop();
				}

				state.currentHoverOverlay = getNextHoverColor();
				component.setBackground(blendColors(originalBackground, state.currentHoverOverlay, 0.6f));
				component.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				state.isHovering = false;
				animateComponentFadeOut(component, state, originalBackground);
			}
		});
	}

	public static void applyRainbowHoverToPanel(JPanel panel, Color originalBackground, Color hoverBackground) {
		HoverState state = new HoverState();

		panel.putClientProperty("RainbowHoverState", state);
		panel.putClientProperty("OriginalBackground", originalBackground);
		panel.putClientProperty("HoverBackground", hoverBackground);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				state.isHovering = true;

				if (state.colorTimer != null && state.colorTimer.isRunning()) {
					state.colorTimer.stop();
				}

				state.currentHoverOverlay = getNextHoverColor();

				Color blendedColor = blendColors(hoverBackground, state.currentHoverOverlay, 0.4f);
				panel.setBackground(blendedColor);
				panel.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				state.isHovering = false;
				animatePanelFadeOut(panel, state, originalBackground, hoverBackground);
			}
		});
	}

	private static void animateHoverFadeOut(HoverState state, HoverOverlayRenderer renderer) {
		if (state.colorTimer != null && state.colorTimer.isRunning()) {
			state.colorTimer.stop();
		}

		if (state.currentHoverOverlay == null) {
			return;
		}

		Color startColor = state.currentHoverOverlay;
		long startTime = System.currentTimeMillis();

		state.colorTimer = new Timer(1000 / COLOR_ANIMATION_FPS, null);
		state.colorTimer.addActionListener(e -> {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);

			progress = (float) (1 - Math.pow(1 - progress, 3));

			int alpha = (int) ((1.0f - progress) * 255);
			if (alpha <= 0) {
				state.currentHoverOverlay = null;
				renderer.setHoverOverlay(null);
				state.colorTimer.stop();
			} else {

				state.currentHoverOverlay = new Color(
					startColor.getRed(),
					startColor.getGreen(),
					startColor.getBlue(),
					alpha
				);
				renderer.setHoverOverlay(state.currentHoverOverlay);
			}
			renderer.repaintComponent();
		});

		state.colorTimer.start();
	}

	private static void animateButtonFadeOut(JButton button, HoverState state, Color originalBackground) {
		if (state.colorTimer != null && state.colorTimer.isRunning()) {
			state.colorTimer.stop();
		}

		if (state.currentHoverOverlay == null) {
			button.setBackground(originalBackground);
			return;
		}

		Color startBlended = button.getBackground();
		long startTime = System.currentTimeMillis();

		state.colorTimer = new Timer(1000 / COLOR_ANIMATION_FPS, null);
		state.colorTimer.addActionListener(e -> {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);

			progress = (float) (1 - Math.pow(1 - progress, 3));

			if (progress >= 1.0f) {
				button.setBackground(originalBackground);
				state.currentHoverOverlay = null;
				state.colorTimer.stop();
			} else {

				Color interpolated = interpolateColors(startBlended, originalBackground, progress);
				button.setBackground(interpolated);
			}
			button.repaint();
		});

		state.colorTimer.start();
	}

	private static void animateComponentFadeOut(JComponent component, HoverState state, Color originalBackground) {
		if (state.colorTimer != null && state.colorTimer.isRunning()) {
			state.colorTimer.stop();
		}

		if (state.currentHoverOverlay == null) {
			component.setBackground(originalBackground);
			return;
		}

		Color startBlended = component.getBackground();
		long startTime = System.currentTimeMillis();

		state.colorTimer = new Timer(1000 / COLOR_ANIMATION_FPS, null);
		state.colorTimer.addActionListener(e -> {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);

			progress = (float) (1 - Math.pow(1 - progress, 3));

			if (progress >= 1.0f) {
				component.setBackground(originalBackground);
				state.currentHoverOverlay = null;
				state.colorTimer.stop();
			} else {
				Color interpolated = interpolateColors(startBlended, originalBackground, progress);
				component.setBackground(interpolated);
			}
			component.repaint();
		});

		state.colorTimer.start();
	}

	private static void animatePanelFadeOut(JPanel panel, HoverState state, Color originalBackground, Color hoverBackground) {
		if (state.colorTimer != null && state.colorTimer.isRunning()) {
			state.colorTimer.stop();
		}

		if (state.currentHoverOverlay == null) {
			panel.setBackground(originalBackground);
			return;
		}

		Color startBlended = panel.getBackground();
		long startTime = System.currentTimeMillis();

		state.colorTimer = new Timer(1000 / COLOR_ANIMATION_FPS, null);
		state.colorTimer.addActionListener(e -> {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);

			progress = (float) (1 - Math.pow(1 - progress, 3));

			if (progress >= 1.0f) {
				panel.setBackground(originalBackground);
				state.currentHoverOverlay = null;
				state.colorTimer.stop();
			} else {
				Color interpolated = interpolateColors(startBlended, originalBackground, progress);
				panel.setBackground(interpolated);
			}
			panel.repaint();
		});

		state.colorTimer.start();
	}

	private static Color blendColors(Color base, Color overlay, float overlayRatio) {
		float baseRatio = 1.0f - overlayRatio;

		int red = (int) (base.getRed() * baseRatio + overlay.getRed() * overlayRatio);
		int green = (int) (base.getGreen() * baseRatio + overlay.getGreen() * overlayRatio);
		int blue = (int) (base.getBlue() * baseRatio + overlay.getBlue() * overlayRatio);
		int alpha = Math.max(base.getAlpha(), overlay.getAlpha());

		return new Color(
			Math.min(255, Math.max(0, red)),
			Math.min(255, Math.max(0, green)),
			Math.min(255, Math.max(0, blue)),
			Math.min(255, Math.max(0, alpha))
		);
	}

	private static Color interpolateColors(Color start, Color end, float progress) {
		int red = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
		int green = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
		int blue = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
		int alpha = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * progress);

		return new Color(
			Math.min(255, Math.max(0, red)),
			Math.min(255, Math.max(0, green)),
			Math.min(255, Math.max(0, blue)),
			Math.min(255, Math.max(0, alpha))
		);
	}

	public static void removeRainbowHover(JComponent component) {
		HoverState state = (HoverState) component.getClientProperty("RainbowHoverState");
		if (state != null) {
			state.cleanup();
			component.putClientProperty("RainbowHoverState", null);
		}

		Color originalBackground = (Color) component.getClientProperty("OriginalBackground");
		if (originalBackground != null) {
			component.setBackground(originalBackground);
			component.putClientProperty("OriginalBackground", null);
		}
	}

	public static boolean hasRainbowHover(JComponent component) {
		return component.getClientProperty("RainbowHoverState") != null;
	}
}
