package com.bestbudz.dock.ui.panel.skills;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.SpriteUtil;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.data.Skills;
import com.bestbudz.engine.core.Client;

import static com.bestbudz.engine.config.ColorConfig.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

public class SkillsPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final SkillComponent[] skills = new SkillComponent[Skills.SKILLS_COUNT];
	private static final NumberFormat formatter = NumberFormat.getInstance();
	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel totalGradesLabel;
	private JLabel totalXpLabel;
	private JButton totalOverlayButton;

	private boolean truncateXP = true;
	private boolean autoSort = true;
	private long[] lastUpdateTimes = new long[Skills.SKILLS_COUNT];
	private long[] lastXpValues = new long[Skills.SKILLS_COUNT];
	private int[] lastAdvanceValues = new int[Skills.SKILLS_COUNT];
	private JSlider colorSlider;

	public SkillsPanel() {
		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(MAIN_FRAME_COLOR);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(0, 2, 6, 6));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(3, 8, 3, 8));

		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		SkillUIFactory.styleScrollBar(scrollPane);

		add(scrollPane, BorderLayout.CENTER);

		ToolTipManager.sharedInstance().setInitialDelay(1000);
		ToolTipManager.sharedInstance().setDismissDelay(5000);
		ToolTipManager.sharedInstance().setReshowDelay(500);

		populateSkills();
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		controlsPanel.setOpaque(false);

		JLabel colorLabel = new JLabel("Color XP:");
		colorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		colorLabel.setForeground(TEXT_SECONDARY_COLOR);

		colorSlider = SkillUIFactory.createColorSlider();

		colorSlider.addChangeListener(e -> {

			if (!colorSlider.getValueIsAdjusting()) {
				int hue = colorSlider.getValue();
				XP_TEXT_COLOR = Color.getHSBColor(hue / 360.0f, 0.7f, 0.9f);

				for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
					if (Skills.SKILL_ENABLED[i] && skills[i] != null) {
						skills[i].xpLabel.setForeground(XP_TEXT_COLOR);
					}
				}
			}
		});

		JButton sortToggleButton = SkillUIFactory.createToggleButton(autoSort ? "Fixed" : "Auto-sort");
		sortToggleButton.addActionListener(e -> {
			autoSort = !autoSort;
			sortToggleButton.setText(autoSort ? "Fixed" : "Auto-sort");
			refreshSkillOrder();
		});

		JButton xpToggleButton = SkillUIFactory.createToggleButton(truncateXP ? "Full XP" : "Short XP");
		xpToggleButton.addActionListener(e -> {
			truncateXP = !truncateXP;
			xpToggleButton.setText(truncateXP ? "Full XP" : "Short XP");
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (Skills.SKILL_ENABLED[i] && skills[i] != null) {
					long xp = Client.currentExp[i] & 0xFFFFFFFFL;
					skills[i].xpLabel.setText(formatXP(xp));
				}
			}
		});

		controlsPanel.add(colorLabel);
		controlsPanel.add(colorSlider);
		controlsPanel.add(sortToggleButton);
		controlsPanel.add(xpToggleButton);

		headerPanel.add(controlsPanel, BorderLayout.EAST);

		return headerPanel;
	}

	private void populateSkills() {
		contentPanel.removeAll();

		contentPanel.add(createTotalStatsButton());

		if (autoSort) {
			java.util.List<Integer> enabledSkills = new java.util.ArrayList<>();
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (Skills.SKILL_ENABLED[i]) {
					enabledSkills.add(i);
				}
			}

			enabledSkills.sort((a, b) -> Long.compare(lastUpdateTimes[b], lastUpdateTimes[a]));

			for (int skillId : enabledSkills) {
				contentPanel.add(createSkillTile(skillId));
			}
		} else {

			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (!Skills.SKILL_ENABLED[i]) continue;
				contentPanel.add(createSkillTile(i));
			}
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private JPanel createTotalStatsButton() {

		int totalGrades = 0;
		long totalExp = 0;
		int totalAdvances = 0;

		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {

				int baseGrade = Client.currentStats[i];

				int advanceLevel = (Client.currentAdvances != null) ? Client.currentAdvances[i] : 0;

				int effectiveGrade = baseGrade + (advanceLevel * 420);

				totalGrades += effectiveGrade;
				totalExp += Client.currentExp[i] & 0xFFFFFFFFL;
				totalAdvances += advanceLevel;
			}
		}

		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setPreferredSize(new Dimension(132, 50));
		wrapperPanel.setOpaque(false);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(132, 50));

		JPanel tile = SkillUIFactory.createGoldenTile();
		tile.setBounds(0, 0, 132, 50);

		String totalTooltip = "<html>Total Statistics<br>" +
			"Combined Grades: " + totalGrades + "<br>" +
			"Combined Experience: " + formatter.format(totalExp);

		if (totalAdvances > 0) {
			totalTooltip += "<br>Combined Advances: " + totalAdvances;
		}
		totalTooltip += "</html>";

		tile.setToolTipText(totalTooltip);

		ImageIcon totalIcon = SpriteUtil.loadIconScaled("sprites/skills/total.png", 24);
		if (totalIcon == null) {

			JLabel iconLabel = new JLabel("Σ");
			iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
			iconLabel.setForeground(GOLD_ACCENT_COLOR);
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconLabel.setBounds(32, 10, 24, 24);
			layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		} else {
			JLabel iconLabel = new JLabel(totalIcon);
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconLabel.setBounds(32, 10, 24, 24);
			layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		}

		totalGradesLabel = new JLabel(String.valueOf(totalGrades));
		totalGradesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		totalGradesLabel.setForeground(GOLD_ACCENT_COLOR);
		totalGradesLabel.setBounds(60, 10, 50, 24);
		totalGradesLabel.setToolTipText(totalTooltip);

		totalXpLabel = new JLabel(formatXP(totalExp));
		totalXpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		totalXpLabel.setForeground(GOLD_ACCENT_COLOR);
		totalXpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalXpLabel.setBounds(0, 34, 132, 12);
		totalXpLabel.setToolTipText(totalTooltip);

		totalOverlayButton = new JButton();
		totalOverlayButton.setOpaque(false);
		totalOverlayButton.setContentAreaFilled(false);
		totalOverlayButton.setBorderPainted(false);
		totalOverlayButton.setFocusPainted(false);
		totalOverlayButton.setBounds(0, 0, 132, 50);
		totalOverlayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		totalOverlayButton.setToolTipText(totalTooltip);

		totalOverlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tile.setBackground(new Color(90, 75, 45));
				tile.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tile.setBackground(new Color(75, 60, 30));
				tile.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				tile.setBackground(new Color(60, 45, 15));
				tile.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tile.setBackground(new Color(90, 75, 45));
				tile.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				SkillClickHandler.handleTotalStatsClick();
			}
		});

		layeredPane.add(tile, JLayeredPane.DEFAULT_LAYER);

		layeredPane.add(totalGradesLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(totalXpLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(totalOverlayButton, JLayeredPane.MODAL_LAYER);

		if (totalAdvances > 0) {
			JLabel advanceLabel = AdvancementManager.createAdvancementBadge(totalAdvances);
			advanceLabel.setBounds(114, -2, 18, 16);
			layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
		}

		wrapperPanel.add(layeredPane, BorderLayout.CENTER);
		return wrapperPanel;
	}

	private String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private JPanel createSkillTile(int id) {
		ImageIcon icon = SpriteUtil.loadIconScaled("sprites/skills/" + Skills.SKILL_NAMES[id] + ".png", 24);
		if (icon == null) {
			icon = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
		}

		int grade = Client.currentStats[id];
		long xp = Client.currentExp[id] & 0xFFFFFFFFL;
		String name = capitalize(Skills.SKILL_NAMES[id]);
		int advance = (Client.currentAdvances != null) ? Client.currentAdvances[id] : 0;

		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setPreferredSize(new Dimension(132, 50));
		wrapperPanel.setOpaque(false);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(132, 50));

		JPanel tile = SkillUIFactory.createModernTile();
		tile.setBounds(0, 0, 132, 50);

		String progressionInfo = SkillCalculator.getXpToNextLevel(xp, grade);
		String multiLineTooltip = "<html>" + name + "<br>" +
			"Grade: " + grade + "<br>" +
			"Experience: " + formatter.format(xp) + "<br>" +
			"Progress: " + progressionInfo;

		if (advance > 0) {
			multiLineTooltip += "<br>Advance Level: " + advance;
		}
		multiLineTooltip += "</html>";

		tile.setToolTipText(multiLineTooltip);

		JLabel iconLabel = new JLabel(icon);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setBounds(32, 10, 24, 24);

		JLabel gradeLabel = new JLabel(String.valueOf(grade));
		gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		gradeLabel.setForeground(TEXT_PRIMARY_COLOR);
		gradeLabel.setBounds(60, 10, 50, 24);

		JLabel xpLabel = new JLabel(formatXP(xp));
		xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		xpLabel.setForeground(XP_TEXT_COLOR);
		xpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		xpLabel.setBounds(0, 34, 132, 12);

		JButton overlayButton = new JButton();
		overlayButton.setOpaque(false);
		overlayButton.setContentAreaFilled(false);
		overlayButton.setBorderPainted(false);
		overlayButton.setFocusPainted(false);
		overlayButton.setBounds(0, 0, 132, 50);
		overlayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		overlayButton.setToolTipText(multiLineTooltip);

		overlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tile.setBackground(TILE_HOVER_COLOR);
				tile.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tile.setBackground(GRAPHITE_COLOR);
				tile.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				tile.setBackground(TILE_PRESSED_COLOR);
				tile.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tile.setBackground(TILE_HOVER_COLOR);
				tile.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				SkillClickHandler.handleSkillClick(id);
			}
		});

		layeredPane.add(tile, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(gradeLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(xpLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(overlayButton, JLayeredPane.MODAL_LAYER);

		JLabel advanceLabel = null;
		if (advance > 0) {
			advanceLabel = AdvancementManager.createAdvancementBadge(advance);
			layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
		}

		wrapperPanel.add(layeredPane, BorderLayout.CENTER);

		skills[id] = new SkillComponent(tile, iconLabel, gradeLabel, xpLabel, overlayButton, advanceLabel);
		lastXpValues[id] = xp;
		lastAdvanceValues[id] = advance;

		return wrapperPanel;
	}

	private boolean isInTopThree(int skillId) {
		if (!autoSort) return false;

		java.util.List<Integer> sortedSkills = new java.util.ArrayList<>();
		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {
				sortedSkills.add(i);
			}
		}
		sortedSkills.sort((a, b) -> Long.compare(lastUpdateTimes[b], lastUpdateTimes[a]));

		return sortedSkills.size() >= 3 && sortedSkills.subList(0, 3).contains(skillId);
	}

	private void refreshSkillOrder() {
		SwingUtilities.invokeLater(() -> {
			populateSkills();
		});
	}

	private void updateTotalStats() {

		int totalGrades = 0;
		long totalExp = 0;
		int totalAdvances = 0;

		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {

				int baseGrade = Client.currentStats[i];

				int advanceLevel = (Client.currentAdvances != null) ? Client.currentAdvances[i] : 0;

				int effectiveGrade = baseGrade + (advanceLevel * 420);

				totalGrades += effectiveGrade;
				totalExp += Client.currentExp[i] & 0xFFFFFFFFL;
				totalAdvances += advanceLevel;
			}
		}

		if (totalGradesLabel != null) {
			totalGradesLabel.setText(String.valueOf(totalGrades));
		}

		if (totalXpLabel != null) {
			totalXpLabel.setText(formatXP(totalExp));
		}

		if (totalOverlayButton != null) {
			String updatedTooltip = "<html>Total Statistics<br>" +
				"Combined Grades: " + totalGrades + "<br>" +
				"Combined Experience: " + formatter.format(totalExp);

			if (totalAdvances > 0) {
				updatedTooltip += "<br>Combined Advances: " + totalAdvances;
			}
			updatedTooltip += "</html>";

			totalOverlayButton.setToolTipText(updatedTooltip);

			if (totalGradesLabel != null) {
				totalGradesLabel.setToolTipText(updatedTooltip);
			}

			if (totalXpLabel != null) {
				totalXpLabel.setToolTipText(updatedTooltip);
			}
		}
	}

	private String formatXP(long xp) {
		return SkillCalculator.formatXP(xp, truncateXP);
	}

	@Override
	public void updateText() {
		if (!Client.loggedIn) return;

		SwingUtilities.invokeLater(() -> {
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (!Skills.SKILL_ENABLED[i]) continue;
				updateSkill(i);
			}
		});
	}

	public void updateSkill(int id) {
		if (!Client.loggedIn) return;
		if (id < 0 || id >= skills.length || skills[id] == null || !Skills.SKILL_ENABLED[id]) return;

		SwingUtilities.invokeLater(() -> {
			int grade = Client.currentStats[id];
			long xp = Client.currentExp[id] & 0xFFFFFFFFL;
			String name = capitalize(Skills.SKILL_NAMES[id]);
			int advance = (Client.currentAdvances != null) ? Client.currentAdvances[id] : 0;

			boolean xpChanged = lastXpValues[id] != xp;
			boolean advanceChanged = lastAdvanceValues[id] != advance;
			lastXpValues[id] = xp;
			lastAdvanceValues[id] = advance;

			skills[id].gradeLabel.setText(String.valueOf(grade));
			skills[id].xpLabel.setText(formatXP(xp));

			if (skills[id].advanceLabel != null) {
				if (advance > 0) {

					JLabel newAdvanceLabel = AdvancementManager.createAdvancementBadge(advance);

					Component parent = skills[id].tile.getParent();
					if (parent instanceof JLayeredPane) {
						JLayeredPane layeredPane = (JLayeredPane) parent;
						layeredPane.remove(skills[id].advanceLabel);
						layeredPane.add(newAdvanceLabel, JLayeredPane.PALETTE_LAYER);
						skills[id].advanceLabel = newAdvanceLabel;
						layeredPane.revalidate();
						layeredPane.repaint();
					}
				} else {
					skills[id].advanceLabel.setVisible(false);
				}
			} else if (advance > 0) {

				JLabel advanceLabel = AdvancementManager.createAdvancementBadge(advance);

				Component parent = skills[id].tile.getParent();
				if (parent instanceof JLayeredPane) {
					JLayeredPane layeredPane = (JLayeredPane) parent;
					layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
					skills[id].advanceLabel = advanceLabel;
				}
			}

			String progressionInfo = SkillCalculator.getXpToNextLevel(xp, grade);
			String updatedTooltip = "<html>" + name + "<br>" +
				"Grade: " + grade + "<br>" +
				"Experience: " + formatter.format(xp) + "<br>" +
				"Progress: " + progressionInfo;

			if (advance > 0) {
				updatedTooltip += "<br>Advance Level: " + advance;
			}
			updatedTooltip += "</html>";

			skills[id].gradeLabel.setToolTipText(updatedTooltip);
			skills[id].iconLabel.setToolTipText(updatedTooltip);
			skills[id].xpLabel.setToolTipText(updatedTooltip);
			skills[id].tile.setToolTipText(updatedTooltip);
			skills[id].overlayButton.setToolTipText(updatedTooltip);

			if ((xpChanged || advanceChanged) && autoSort) {
				boolean wasInTopThree = isInTopThree(id);
				lastUpdateTimes[id] = System.currentTimeMillis();

				if (!wasInTopThree) {
					refreshSkillOrder();
				}
			}

			updateTotalStats();
		});
	}

	@Override
	public String getPanelID() {
		return "Skills";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		updateText();
	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void updateDockText(int index, String text) {

	}
}
