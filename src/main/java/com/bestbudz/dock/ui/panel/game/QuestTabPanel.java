package com.bestbudz.dock.ui.panel.game;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;

public class QuestTabPanel implements UIPanel, DockTextUpdatable
{

	private final DefaultListModel<String> listModel = new DefaultListModel<>();
	private final JList<String> questList = new JList<>(listModel);

	private final JPanel panel;
	private final JLabel placeholderLabel = new JLabel("Navigation panel will be available once you log in.");
	public static int hoverIndex = -1;
	public static int clickedIndex = -1;
	public static Timer clickFadeTimer;

	public QuestTabPanel() {
		panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBackground(MAIN_FRAME_COLOR);
		panel.setPreferredSize(null);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.setMinimumSize(new Dimension(0, 0));

		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonBar.setOpaque(false);
		
		questList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);

				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Monospaced", Font.PLAIN, 12));
				label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

				if (index == clickedIndex) {
					label.setBackground(SELECTED_COLOR); // flash green
					label.setForeground(WHITE_UI_COLOR);
				} else if (index == hoverIndex) {
					label.setBackground(HOVER_COLOR); // hover color
					label.setForeground(WHITE_UI_COLOR);
				} else {
					label.setBackground(new Color(30, 30, 30));
					label.setForeground(TITLE_COLOR);
				}

				return label;
			}
		});


		questList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = questList.locationToIndex(e.getPoint());
				if (index != hoverIndex) {
					hoverIndex = index;
					questList.repaint();
				}
			}
		});

		questList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				hoverIndex = -1;
				questList.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = questList.locationToIndex(e.getPoint());
				clickedIndex = index;
				questList.repaint();

				if (clickFadeTimer != null) {
					clickFadeTimer.stop();
				}
				clickFadeTimer = new Timer(500, evt -> {
					clickedIndex = -1;
					questList.repaint();
				});
				clickFadeTimer.setRepeats(false);
				clickFadeTimer.start();
			}
		});



		JScrollPane scrollPane = new JScrollPane(questList);
		scrollPane.setPreferredSize(new Dimension(200, 300));
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);

		placeholderLabel.setForeground(Color.GRAY);
		placeholderLabel.setFont(new Font("Monospaced", Font.ITALIC, 12));
		placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);

		content.add(scrollPane, BorderLayout.CENTER);

		panel.add(content, BorderLayout.CENTER);
		panel.add(buttonBar, BorderLayout.SOUTH);

		questList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && !e.isConsumed()) {
					int index = questList.locationToIndex(e.getPoint());
					int childId = 29501 + index; // Matches original RSInterface child ID

					System.out.println("Clicked info line " + index + " (ID " + childId + ")");

					// Send interaction
					try {
						Client.stream.createFrame(185);
						Client.stream.writeWord(childId);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			});

// Auto-refresh every 60 seconds
		new Timer(60_000, e -> {
			if (Client.loggedIn) {
				try {
					Client.stream.createFrame(185);
					Client.stream.writeWord(29410);
					System.out.println("Auto-refreshing info tab.");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();


	}

	public void updateQuestLine(int index, String text) {
		while (listModel.size() <= index) {
			listModel.addElement(""); // add blanks until we reach desired index
		}
		listModel.set(index, text);
	}


	public void showQuestList() {
		if (placeholderLabel.getParent() != null) {
			placeholderLabel.getParent().remove(placeholderLabel);
			placeholderLabel.getParent().revalidate();
			placeholderLabel.getParent().repaint();
		}
		questList.setVisible(true);
	}


	@Override
	public void updateText()
	{

	}

	@Override
	public String getPanelID() {
		return "Info Tab";
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {
		// Optional: focus or reload logic
	}

	@Override
	public void onDeactivate() {
		// Optional: save or cleanup logic
	}

	@Override
	public String getPanelIconPath() {
		return "sprites/quest-tab.png";
	}
	@Override
	public void updateDockText(int index, String text) {
		updateQuestLine(index, text);
		showQuestList(); // Optional
	}

	@Override
	public int[] getBlockedInterfaces() {
		return new int[] { 29500 };
	}

}
