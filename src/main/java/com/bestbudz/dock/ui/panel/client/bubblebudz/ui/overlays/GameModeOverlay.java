package com.bestbudz.dock.ui.panel.client.bubblebudz.ui.overlays;

import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.BubbleBudzStyle;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.modes.GameMode;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.modes.ClassicGameMode;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.modes.GeometricGameMode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Overlay for selecting game modes
 */
public class GameModeOverlay extends MouseAdapter {
	private boolean isVisible = false;
	private Rectangle overlayBounds;
	private List<GameModeOption> modeOptions;
	private GameModeOverlayListener listener;
	private int hoveredIndex = -1;
	private int panelWidth, panelHeight; // Store panel dimensions

	public interface GameModeOverlayListener {
		void onGameModeSelected(GameMode gameMode, String modeName);
		void onOverlayClosed();
	}

	public static class GameModeOption {
		public final GameMode gameMode;
		public final String name;
		public final String description;
		public final Color accentColor;

		public GameModeOption(GameMode gameMode, String name, String description, Color accentColor) {
			this.gameMode = gameMode;
			this.name = name;
			this.description = description;
			this.accentColor = accentColor;
		}
	}

	public GameModeOverlay(GameModeOverlayListener listener) {
		this.listener = listener;
		initializeGameModes();
	}

	private void initializeGameModes() {
		modeOptions = new ArrayList<>();
		modeOptions.add(new GameModeOption(
			new ClassicGameMode(),
			"Classic",
			"Traditional circular (Normal Score)",
			BubbleBudzStyle.TEXT_NEON_GREEN
		));
		modeOptions.add(new GameModeOption(
			new GeometricGameMode(),
			"Geometric",
			"Various shapes (Score Multipliers)",
			BubbleBudzStyle.TEXT_NEON_CYAN
		));
	}

	public void show(int panelWidth, int panelHeight) {
		isVisible = true;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;

		// FUCK IT - HARDCODE THE DIMENSIONS
		int overlayWidth = 250;  // FIXED WIDTH
		int overlayHeight = 160; // FIXED HEIGHT

		// Center it manually
		int x = (panelWidth - overlayWidth) / 3;
		int y = (panelHeight - overlayHeight);

		overlayBounds = new Rectangle(x - 25, y, overlayWidth, overlayHeight);
	}

	public void hide() {
		isVisible = false;
		hoveredIndex = -1;
		if (listener != null) {
			listener.onOverlayClosed();
		}
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void render(Graphics2D g2d, String currentModeName) {
		if (!isVisible || overlayBounds == null) return;

		// Overlay background
		g2d.setColor(new Color(15, 15, 25, 240));
		g2d.fill(new RoundRectangle2D.Float(overlayBounds.x, overlayBounds.y,
			overlayBounds.width, overlayBounds.height, 12, 12));

		// Border
		g2d.setColor(BubbleBudzStyle.TEXT_NEON_CYAN);
		g2d.setStroke(new BasicStroke(2f));
		g2d.draw(new RoundRectangle2D.Float(overlayBounds.x, overlayBounds.y,
			overlayBounds.width, overlayBounds.height, 12, 12));

		// Title
		g2d.setFont(BubbleBudzStyle.FONT_BRAND);
		g2d.setColor(BubbleBudzStyle.TEXT_NEON_CYAN);
		String title = "Select Mode";
		FontMetrics fm = g2d.getFontMetrics();
		int titleX = overlayBounds.x + (overlayBounds.width - fm.stringWidth(title)) / 2;
		g2d.drawString(title, titleX, overlayBounds.y + 25);

		// Mode options
		int optionY = overlayBounds.y + 45;
		int optionHeight = 35;
		int optionSpacing = 8;

		for (int i = 0; i < modeOptions.size(); i++) {
			GameModeOption option = modeOptions.get(i);
			Rectangle optionBounds = new Rectangle(
				overlayBounds.x + 15,
				optionY + i * (optionHeight + optionSpacing),
				overlayBounds.width - 30,
				optionHeight
			);

			renderModeOption(g2d, option, optionBounds, i == hoveredIndex,
				option.name.equals(currentModeName));
		}

		// Close instruction
		g2d.setFont(BubbleBudzStyle.FONT_INSTRUCTION);
		g2d.setColor(BubbleBudzStyle.TEXT_SECONDARY);
		String closeText = "Click outside to close";
		fm = g2d.getFontMetrics();
		int closeX = overlayBounds.x + (overlayBounds.width - fm.stringWidth(closeText)) / 2;
		g2d.drawString(closeText, closeX, overlayBounds.y + overlayBounds.height - 10);
	}

	private void renderModeOption(Graphics2D g2d, GameModeOption option, Rectangle bounds,
								  boolean isHovered, boolean isSelected) {
		// Option background
		Color bgColor;
		if (isSelected) {
			bgColor = new Color(option.accentColor.getRed(), option.accentColor.getGreen(),
				option.accentColor.getBlue(), 80);
		} else if (isHovered) {
			bgColor = new Color(40, 40, 60, 120);
		} else {
			bgColor = new Color(20, 20, 35, 100);
		}

		g2d.setColor(bgColor);
		g2d.fill(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6));

		// Border
		Color borderColor = isSelected ? option.accentColor :
			isHovered ? BubbleBudzStyle.TEXT_SECONDARY :
				new Color(60, 60, 80);
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(isSelected ? 2f : 1f));
		g2d.draw(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6));

		// Mode name
		g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
		g2d.setColor(isSelected ? option.accentColor : BubbleBudzStyle.TEXT_PRIMARY);
		g2d.drawString(option.name, bounds.x + 12, bounds.y + 16);

		// Description
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
		g2d.setColor(BubbleBudzStyle.TEXT_SECONDARY);
		g2d.drawString(option.description, bounds.x + 12, bounds.y + 28);

		// Selected indicator
		if (isSelected) {
			g2d.setColor(option.accentColor);
			g2d.fillOval(bounds.x + bounds.width - 20, bounds.y + 10, 8, 8);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!isVisible) return;

		if (overlayBounds.contains(e.getX(), e.getY())) {
			// Check if clicking on a mode option
			int optionY = overlayBounds.y + 45;
			int optionHeight = 35;
			int optionSpacing = 8;

			for (int i = 0; i < modeOptions.size(); i++) {
				Rectangle optionBounds = new Rectangle(
					overlayBounds.x + 15,
					optionY + i * (optionHeight + optionSpacing),
					overlayBounds.width - 30,
					optionHeight
				);

				if (optionBounds.contains(e.getX(), e.getY())) {
					GameModeOption selected = modeOptions.get(i);
					if (listener != null) {
						listener.onGameModeSelected(selected.gameMode, selected.name);
					}
					hide();
					return;
				}
			}
		} else {
			// Clicked outside overlay - close it
			hide();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!isVisible) return;

		hoveredIndex = -1;

		if (overlayBounds.contains(e.getX(), e.getY())) {
			int optionY = overlayBounds.y + 45;
			int optionHeight = 35;
			int optionSpacing = 8;

			for (int i = 0; i < modeOptions.size(); i++) {
				Rectangle optionBounds = new Rectangle(
					overlayBounds.x + 15,
					optionY + i * (optionHeight + optionSpacing),
					overlayBounds.width - 30,
					optionHeight
				);

				if (optionBounds.contains(e.getX(), e.getY())) {
					hoveredIndex = i;
					break;
				}
			}
		}
	}

	public void updateHover(int x, int y) {
		MouseEvent fakeEvent = new MouseEvent(
			new Component(){}, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
			0, x, y, 0, false
		);
		mouseMoved(fakeEvent);
	}
}