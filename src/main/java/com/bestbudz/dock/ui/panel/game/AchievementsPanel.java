package com.bestbudz.dock.ui.panel.game;

import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AchievementsPanel implements UIPanel, DockTextUpdatable
{
	private Timer achievementRequestTimer;

	private final JPanel panel;
	private final DefaultListModel<AchievementEntry> listModel;
	private final JList<AchievementEntry> achievementList;

	private int hoverIndex = -1;
	private int clickedIndex = -1;
	private Timer clickFadeTimer;

	private static class AchievementEntry {
		final String text;
		final Color color;

		AchievementEntry(String text, Color color) {
			this.text = text;
			this.color = color;
		}
	}

	public AchievementsPanel() {
		panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBackground(MAIN_FRAME_COLOR);
panel.setPreferredSize(null);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.setMinimumSize(new Dimension(0, 0));

		listModel = new DefaultListModel<>();
		for (int i = 0; i < 80; i++) listModel.addElement(new AchievementEntry("", BLUE_ACCENT_COLOR));

		achievementList = new JList<>(listModel);
		achievementList.setFont(new Font("Monospaced", Font.PLAIN, 12));
		achievementList.setBackground(new Color(30, 30, 30));
		achievementList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Monospaced", Font.PLAIN, 12));
				label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

				if (value instanceof AchievementEntry) {
					AchievementEntry entry = (AchievementEntry) value;
					label.setText(entry.text);
					label.setForeground(entry.color);
				}

				if (index == hoverIndex) {

					Color rainbowColor = RainbowHoverUtil.getNextHoverColor();
					label.setBackground(rainbowColor);
				}

				return label;
			}
		});

		achievementList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = achievementList.locationToIndex(e.getPoint());
				if (index != hoverIndex) {
					hoverIndex = index;
					achievementList.repaint();
				}
			}
		});

		achievementList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				hoverIndex = -1;
				achievementList.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = achievementList.locationToIndex(e.getPoint());
				clickedIndex = index;
				achievementList.repaint();

				if (clickFadeTimer != null) clickFadeTimer.stop();
				clickFadeTimer = new Timer(500, evt -> {
					clickedIndex = -1;
					achievementList.repaint();
				});
				clickFadeTimer.setRepeats(false);
				clickFadeTimer.start();

				int frameId = 31006 + index;
				Client.stream.writeEncryptedOpcode(185);
				Client.stream.writeWord(frameId);
			}
		});

		JScrollPane scrollPane = new JScrollPane(achievementList);
		scrollPane.setPreferredSize(new Dimension(200, 300));
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		content.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonBar.setOpaque(false);

		panel.add(content, BorderLayout.CENTER);
		panel.add(buttonBar, BorderLayout.SOUTH);
	}

	@Override
	public void updateText()
	{

	}

	@Override
	public String getPanelID() {
		return "Achievements";
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {

		requestAchievements();

		if (achievementRequestTimer == null) {
			achievementRequestTimer = new Timer(5000, e -> requestAchievements());
			achievementRequestTimer.setRepeats(true);
			achievementRequestTimer.start();
		}
	}

	@Override
	public void onDeactivate() {
		if (achievementRequestTimer != null) {
			achievementRequestTimer.stop();
			achievementRequestTimer = null;
		}
	}

	@Override
	public String getPanelIconPath() {
		return "sprites/achievements-tab.png";
	}

	@Override
	public void updateDockText(int index, String text) {
		updateAchievement(index, text);
	}

	private void requestAchievements() {
		if (!Client.loggedIn || Client.stream == null) return;
		try {
			Client.stream.writeEncryptedOpcode(185);
			Client.stream.writeWord(29404);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateAchievement(int index, String rawText) {
		if (index < 0) return;

		Color color;
		if (rawText.contains("@gre@")) color = COMPLETED_COLOR;
		else if (rawText.contains("@yel@")) color = IN_PROGRESS_COLOR;
		else if (rawText.contains("@red@")) color = UNAVAILABLE_COLOR;
		else color = BLUE_ACCENT_COLOR;

		String cleaned = rawText.replaceAll("@[a-z]{3}@", "").trim();

		while (listModel.size() <= index)
			listModel.addElement(new AchievementEntry("", color));

		listModel.set(index, new AchievementEntry(cleaned, color));
	}

	@Override
	public int[] getBlockedInterfaces() {
		return new int[] { 29404 };
	}

}
