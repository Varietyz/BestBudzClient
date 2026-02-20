package com.bestbudz.dock.ui.panel.stoner;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.engine.config.ColorConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StonerControlsManager {

	private static final int AUTO_COMBAT_BUTTON_ID = 115116;

	private static final Color TEXT_SECONDARY = new Color(170, 170, 170);
	private static final Color BORDER = new Color(60, 60, 60);
	private static final Color COMBAT_ACTIVE = new Color(220, 53, 69);

	private JPanel controlsPanel;
	private JButton autoCombatToggle;
	private boolean autoCombatEnabled = false;

	public StonerControlsManager() {
		createControlsPanel();
	}

	private void createControlsPanel() {
		controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		controlsPanel.setOpaque(false);

		autoCombatToggle = createAutoCombatToggle();
		controlsPanel.add(autoCombatToggle);
	}

	private JButton createAutoCombatToggle() {
		JButton toggle = new JButton("Auto-Combat");
		toggle.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		toggle.setPreferredSize(new Dimension(75, 20));
		toggle.setFocusPainted(false);
		toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		toggle.setOpaque(true);
		toggle.setContentAreaFilled(true);
		toggle.setBorderPainted(true);

		updateAutoCombatButtonState(toggle);

		toggle.addActionListener(e -> {

			if (!isPanelActiveAndVisible()) {
				return;
			}

			autoCombatEnabled = !autoCombatEnabled;
			updateAutoCombatButtonState(toggle);
			ButtonHandler.createButtonListener(AUTO_COMBAT_BUTTON_ID).actionPerformed(e);
		});

		toggle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!autoCombatEnabled && isPanelActiveAndVisible()) {
					toggle.setBackground(ColorConfig.TILE_HOVER_COLOR);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (isPanelActiveAndVisible()) {
					updateAutoCombatButtonState(toggle);
				}
			}
		});

		return toggle;
	}

	private void updateAutoCombatButtonState(JButton button) {
		if (autoCombatEnabled) {
			button.setBackground(COMBAT_ACTIVE);
			button.setForeground(Color.WHITE);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COMBAT_ACTIVE.brighter(), 1),
				new EmptyBorder(2, 4, 2, 4)
			));
		} else {
			button.setBackground(ColorConfig.GRAPHITE_COLOR);
			button.setForeground(TEXT_SECONDARY);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				new EmptyBorder(2, 4, 2, 4)
			));
		}
	}

	public void addCustomButton(String text, int buttonId) {
		JButton button = StonerUIFactory.createControlButton(text);
		button.addActionListener(e -> {
			if (isPanelActiveAndVisible()) {
				ButtonHandler.sendClick(buttonId);
			}
		});
		controlsPanel.add(button);
		controlsPanel.revalidate();
	}

	private boolean isPanelActiveAndVisible() {

		Container parent = controlsPanel.getParent();
		while (parent != null && !(parent instanceof StonerPanel)) {
			if (!parent.isVisible() || !parent.isDisplayable()) {
				return false;
			}
			parent = parent.getParent();
		}

		if (parent instanceof StonerPanel) {
			StonerPanel stonerPanel = (StonerPanel) parent;
			return stonerPanel.isVisible() &&
				stonerPanel.isDisplayable() &&
				stonerPanel.isShowing();
		}

		return false;
	}

	public void addCustomControl(JComponent control) {
		controlsPanel.add(control);
		controlsPanel.revalidate();
	}

	public void setAutoCombatState(boolean enabled) {
		if (autoCombatEnabled != enabled) {
			autoCombatEnabled = enabled;
			updateAutoCombatButtonState(autoCombatToggle);
		}
	}

	public boolean isAutoCombatEnabled() {
		return autoCombatEnabled;
	}

	public JPanel getControlsPanel() {
		return controlsPanel;
	}

	public void handleDockTextUpdate(int index, String text) {

		if (text != null && text.contains("auto-combat")) {

		}
	}
}
