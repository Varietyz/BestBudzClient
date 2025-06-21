package com.bestbudz.dock.ui.panel.social.stoners;
import com.bestbudz.dock.util.RainbowHoverUtil;
import static com.bestbudz.engine.config.ColorConfig.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StonerCellRenderer extends JPanel implements ListCellRenderer<StonerEntry> {
	private final JLabel avatarLabel = new JLabel();
	private final JLabel titleLabel = new JLabel();
	private final JLabel nameLabel = new JLabel();
	private final JLabel statusLabel = new JLabel();

	public StonerCellRenderer() {
		setLayout(new BorderLayout());
		setOpaque(true);
		setBorder(new EmptyBorder(2, 8, 2, 8));
		setMaximumSize(new Dimension(280, 22));

		// Avatar setup - no fixed size, will be hidden when empty
		avatarLabel.setHorizontalAlignment(SwingConstants.LEFT);

		// Title setup
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
		titleLabel.setForeground(WHITE_UI_COLOR);

		nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		nameLabel.setForeground(WHITE_UI_COLOR);

		statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		// Create a panel for avatar + title + name to keep them together
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
		leftPanel.setOpaque(false);
		leftPanel.add(avatarLabel);
		leftPanel.add(titleLabel);
		leftPanel.add(nameLabel);

		add(leftPanel, BorderLayout.WEST);
		add(statusLabel, BorderLayout.EAST);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends StonerEntry> list, StonerEntry value, int index,
												  boolean isSelected, boolean cellHasFocus) {

		// Set avatar and hide/show based on availability
		ImageIcon avatar = UserAvatarConfig.getAvatarForUser(value.name);
		avatarLabel.setIcon(avatar);
		avatarLabel.setVisible(avatar != null);

		// Set title if available
		if (value.title != null && !value.title.trim().isEmpty()) {
			String cleanTitle = UserTitleUtil.processTitleText(value.title);
			titleLabel.setText("[" + cleanTitle + "]");

			Color titleColor = UserTitleUtil.parseTitleColor(value.titleColor);
			titleLabel.setForeground(titleColor);
			titleLabel.setVisible(true);
		} else {
			titleLabel.setText("");
			titleLabel.setVisible(false);
		}

		nameLabel.setText(value.name);
		statusLabel.setText(value.isHigh ? "● High" : "Asleep");
		statusLabel.setForeground(value.isHigh ? ONLINE_COLOR : OFFLINE_COLOR);

		if (isSelected) {
			Color rainbowColor = RainbowHoverUtil.getNextHoverColor(); // You'd need to make this public
			setBackground(rainbowColor);
		} else {
			setBackground(MAIN_FRAME_COLOR);
		}

		// Only show hand cursor for users who are high
		setCursor(value.isHigh ?
			Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
			Cursor.getDefaultCursor());
		return this;
	}
}