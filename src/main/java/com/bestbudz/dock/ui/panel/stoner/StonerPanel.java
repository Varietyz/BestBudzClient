package com.bestbudz.dock.ui.panel.stoner;

import com.bestbudz.dock.ui.panel.stoner.sections.*;
import com.bestbudz.dock.ui.panel.stoner.sections.equipment.EquipmentSection;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class StonerPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private StonerControlsManager controlsManager;
	private JScrollPane scrollPane;
	private JPanel sectionsPanel;

	// Section components
	private EquipmentSection equipmentSection;
	private KillcountSection killcountSection;
	private PetsSection petsSection;
	private SpellbookSection spellbookSection;
	private PointsSection valueSection;
	private AttackStyleSection attackStyleSection;

	private static final int PANEL_WIDTH = 300;

	public StonerPanel() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setPreferredSize(new Dimension(PANEL_WIDTH, 400));
		setMinimumSize(new Dimension(PANEL_WIDTH, 200));

		initializeComponents();
		setupLayout();
	}

	private void initializeComponents() {
		controlsManager = new StonerControlsManager();

		// Initialize sections
		equipmentSection = new EquipmentSection();
		killcountSection = new KillcountSection();
		petsSection = new PetsSection();
		spellbookSection = new SpellbookSection();
		valueSection = new PointsSection();
		attackStyleSection = new AttackStyleSection();
	}

	private void setupLayout() {
		// Fixed header with controls
		JPanel headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);

		// Scrollable sections
		createScrollableSections();
		add(scrollPane, BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				updateAllSections();
			}
		});
	}

	private JPanel createHeaderPanel() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(new Color(45, 45, 45));
		header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		header.add(controlsManager.getControlsPanel(), BorderLayout.EAST);
		return header;
	}

	private void createScrollableSections() {
		sectionsPanel = new JPanel();
		sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
		sectionsPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		sectionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// Add sections
		addSection(equipmentSection.createPanel(PANEL_WIDTH));
		addSection(attackStyleSection.createPanel(PANEL_WIDTH));
		addSection(killcountSection.createPanel(PANEL_WIDTH));
		addSection(petsSection.createPanel(PANEL_WIDTH));
		addSection(spellbookSection.createPanel(PANEL_WIDTH));
		addSection(valueSection.createPanel(PANEL_WIDTH));

		scrollPane = new JScrollPane(sectionsPanel);
		StonerUIFactory.styleScrollPane(scrollPane);
	}

	private void addSection(JPanel section) {
		if (section != null) {
			sectionsPanel.add(section);
			sectionsPanel.add(Box.createVerticalStrut(8));
		}
	}

	private void updateAllSections() {
		if (!Client.loggedIn || Client.myStoner == null) return;

		SwingUtilities.invokeLater(() -> {
			equipmentSection.updateData();
			attackStyleSection.updateData();
			killcountSection.updateData();
			petsSection.updateData();
			spellbookSection.updateData();
			valueSection.updateData();
			repaint();
		});
	}

	// Extension API
	public void addCustomControl(JComponent control) {
		controlsManager.addCustomControl(control);
	}

	public void addCustomButton(String text, int buttonId) {
		controlsManager.addCustomButton(text, buttonId);
	}

	public boolean isAutoCombatEnabled() {
		return controlsManager.isAutoCombatEnabled();
	}

	public void setAutoCombatState(boolean enabled) {
		controlsManager.setAutoCombatState(enabled);
	}

	// UIPanel interface
	@Override
	public String getPanelID() {
		return "My Info";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		equipmentSection.onPanelActivated();
		SwingUtilities.invokeLater(() -> {
			updateAllSections();

			setVisible(true);
			if (getParent() != null) {
				getParent().setComponentZOrder(this, 0);
			}
			revalidate();
			repaint();
		});
	}

	@Override
	public void onDeactivate() {
		equipmentSection.onPanelDeactivated();
		SwingUtilities.invokeLater(() -> {
			if (getParent() != null) {
				getParent().setComponentZOrder(this, getParent().getComponentCount() - 1);
			}
		});
	}

	@Override
	public void updateText() {
		updateAllSections();
	}

	@Override
	public void updateDockText(int index, String text) {
		controlsManager.handleDockTextUpdate(index, text);
	}
}