package com.bestbudz.engine.core.error.panels;

import com.bestbudz.engine.core.error.ErrorUtilities;
import static com.bestbudz.ui.handling.input.MouseActions.mouseInRegion;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.bestbudz.engine.core.error.ErrorEnums.*;

/**
 * Actions panel containing buttons for error handling and self-healing
 */
public class ErrorActionsPanel {
	private final ErrorUtilities.ErrorFontManager fontManager;
	private final Map<ActionType, Rect> buttonBounds;
	private boolean showSelfHealOptions;
	private String lastAction;
	private long lastActionTime;

	public ErrorActionsPanel(ErrorUtilities.ErrorFontManager fontManager) {
		this.fontManager = fontManager;
		this.buttonBounds = new HashMap<>();
		this.showSelfHealOptions = false;
		this.lastAction = "";
		this.lastActionTime = 0;
	}

	/**
	 * Render the actions panel
	 */
	public void render(Graphics2D g, Rect bounds) {
		// Draw panel background
		ErrorUtilities.drawStyledPanel(g, bounds, "Actions", ACCENT_COLOR, fontManager.headerFont);

		// Calculate button positions
		calculateButtonPositions(bounds);

		// Draw main action buttons
		drawMainActions(g);

		// Draw self-healing options if enabled
		if (showSelfHealOptions) {
			drawSelfHealingActions(g);
		}

		// Draw status message if recent action
		drawStatusMessage(g, bounds);
	}

	/**
	 * Calculate button positions within the panel
	 */
	private void calculateButtonPositions(Rect bounds) {
		buttonBounds.clear();

		int btnW = 180;
		int btnH = 35;
		int btnSpacing = 10;
		int startY = bounds.y + 50;

		// Main buttons
		buttonBounds.put(ActionType.TOGGLE_CONSOLE,
			new Rect(bounds.x + 10, startY, btnW, btnH));
		startY += btnH + btnSpacing;

		buttonBounds.put(ActionType.CLEAR_CACHE,
			new Rect(bounds.x + 10, startY, btnW, btnH));
		startY += btnH + btnSpacing;

		buttonBounds.put(ActionType.RESTART,
			new Rect(bounds.x + 10, startY, btnW, btnH));
		startY += btnH + btnSpacing;

		buttonBounds.put(ActionType.EXIT,
			new Rect(bounds.x + 10, startY, btnW, btnH));
		startY += btnH + btnSpacing * 2;

		// Self-healing buttons (if enabled)
		if (showSelfHealOptions) {
			buttonBounds.put(ActionType.COPY_LOGS,
				new Rect(bounds.x + 10, startY, btnW, btnH));
			startY += btnH + btnSpacing;

			buttonBounds.put(ActionType.DELETE_CACHE,
				new Rect(bounds.x + 10, startY, btnW, btnH));
		}
	}

	/**
	 * Draw main action buttons
	 */
	private void drawMainActions(Graphics2D g) {
		// Toggle Console button
		Rect toggleBtn = buttonBounds.get(ActionType.TOGGLE_CONSOLE);
		if (toggleBtn != null) {
			boolean hover = mouseInRegion(toggleBtn.x, toggleBtn.y, toggleBtn.x + toggleBtn.w, toggleBtn.y + toggleBtn.h);
			ErrorUtilities.drawStyledButton(g, toggleBtn, ActionType.TOGGLE_CONSOLE.label,
				ActionType.TOGGLE_CONSOLE.color, hover, fontManager.buttonFont);
		}

		// Clear Cache button
		Rect clearBtn = buttonBounds.get(ActionType.CLEAR_CACHE);
		if (clearBtn != null) {
			boolean hover = mouseInRegion(clearBtn.x, clearBtn.y, clearBtn.x + clearBtn.w, clearBtn.y + clearBtn.h);
			ErrorUtilities.drawStyledButton(g, clearBtn, ActionType.CLEAR_CACHE.label,
				ActionType.CLEAR_CACHE.color, hover, fontManager.buttonFont);
		}

		// Restart button
		Rect restartBtn = buttonBounds.get(ActionType.RESTART);
		if (restartBtn != null) {
			boolean hover = mouseInRegion(restartBtn.x, restartBtn.y, restartBtn.x + restartBtn.w, restartBtn.y + restartBtn.h);
			ErrorUtilities.drawStyledButton(g, restartBtn, ActionType.RESTART.label,
				ActionType.RESTART.color, hover, fontManager.buttonFont);
		}

		// Exit button
		Rect exitBtn = buttonBounds.get(ActionType.EXIT);
		if (exitBtn != null) {
			boolean hover = mouseInRegion(exitBtn.x, exitBtn.y, exitBtn.x + exitBtn.w, exitBtn.y + exitBtn.h);
			ErrorUtilities.drawStyledButton(g, exitBtn, ActionType.EXIT.label,
				ActionType.EXIT.color, hover, fontManager.buttonFont);
		}
	}

	/**
	 * Draw self-healing action buttons
	 */
	private void drawSelfHealingActions(Graphics2D g) {
		// Copy Logs button
		Rect copyBtn = buttonBounds.get(ActionType.COPY_LOGS);
		if (copyBtn != null) {
			boolean hover = mouseInRegion(copyBtn.x, copyBtn.y, copyBtn.x + copyBtn.w, copyBtn.y + copyBtn.h);
			ErrorUtilities.drawStyledButton(g, copyBtn, ActionType.COPY_LOGS.label,
				ActionType.COPY_LOGS.color, hover, fontManager.buttonFont);
		}

		// Delete Cache Files button
		Rect deleteBtn = buttonBounds.get(ActionType.DELETE_CACHE);
		if (deleteBtn != null) {
			boolean hover = mouseInRegion(deleteBtn.x, deleteBtn.y, deleteBtn.x + deleteBtn.w, deleteBtn.y + deleteBtn.h);
			ErrorUtilities.drawStyledButton(g, deleteBtn, ActionType.DELETE_CACHE.label,
				ActionType.DELETE_CACHE.color, hover, fontManager.buttonFont);
		}
	}

	/**
	 * Draw status message for recent actions
	 */
	private void drawStatusMessage(Graphics2D g, Rect bounds) {
		if (!lastAction.isEmpty() && System.currentTimeMillis() - lastActionTime < 5000) {
			g.setColor(SUCCESS_COLOR);
			g.setFont(fontManager.detailFont);

			String message = "✓ " + lastAction;
			FontMetrics fm = g.getFontMetrics();
			int msgX = bounds.x + (bounds.w - fm.stringWidth(message)) / 2;
			int msgY = bounds.y + bounds.h - 20;

			// Draw background for better readability
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRoundRect(msgX - 5, msgY - fm.getAscent() - 2,
				fm.stringWidth(message) + 10, fm.getHeight() + 4, 4, 4);

			g.setColor(SUCCESS_COLOR);
			g.drawString(message, msgX, msgY);
		}
	}

	/**
	 * Handle button click
	 */
	public ActionType handleClick(int x, int y) {
		for (Map.Entry<ActionType, Rect> entry : buttonBounds.entrySet()) {
			Rect rect = entry.getValue();
			if (rect != null && rect.contains(x, y)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Set whether to show self-healing options
	 */
	public void setShowSelfHealOptions(boolean show) {
		this.showSelfHealOptions = show;
	}

	/**
	 * Update last action status
	 */
	public void setLastAction(String action) {
		this.lastAction = action;
		this.lastActionTime = System.currentTimeMillis();
	}

	/**
	 * Get button bounds for external click handling
	 */
	public Map<ActionType, Rect> getButtonBounds() {
		return new HashMap<>(buttonBounds);
	}

	/**
	 * Get preferred size for this panel
	 */
	public Dimension getPreferredSize() {
		int height = showSelfHealOptions ? 400 : 250;
		return new Dimension(200, height);
	}
}