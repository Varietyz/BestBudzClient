package com.bestbudz.dock.ui.panel.social.stoners;


import com.bestbudz.dock.util.RainbowHoverUtil;
import static com.bestbudz.engine.config.ColorConfig.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MessageDialog {

	public static void show(Component parentComponent, String recipientName, MessageSender messageSender) {
		if (recipientName == null || recipientName.isEmpty()) {
			return;
		}

		// Create custom dialog
		JDialog messageDialog = new JDialog();
		messageDialog.setTitle("Send Message to " + recipientName);
		messageDialog.setModal(true);
		messageDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		messageDialog.setSize(400, 150);
		messageDialog.setLocationRelativeTo(parentComponent);
		messageDialog.setResizable(false);

		// Style the dialog
		messageDialog.getContentPane().setBackground(GRAPHITE_COLOR);
		messageDialog.setLayout(new BorderLayout(10, 10));

		// Message input area
		JTextArea messageArea = new JTextArea(3, 30);
		messageArea.setBackground(GRAY_UI_COLOR);
		messageArea.setForeground(WHITE_UI_COLOR);
		messageArea.setCaretColor(WHITE_UI_COLOR);
		messageArea.setBorder(new EmptyBorder(8, 8, 8, 8));
		messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.requestFocusInWindow();

		JScrollPane scrollPane = new JScrollPane(messageArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
		scrollPane.setBackground(GRAY_UI_COLOR);

		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(GRAY_UI_COLOR);

		JButton sendButton = new JButton("Send");
		JButton cancelButton = new JButton("Cancel");

		// Style buttons
		styleButton(sendButton, ONLINE_COLOR);
		styleButton(cancelButton, OFFLINE_COLOR);

		buttonPanel.add(cancelButton);
		buttonPanel.add(sendButton);

		// Add components to dialog
		JLabel instructionLabel = new JLabel("Enter your message:");
		instructionLabel.setForeground(WHITE_UI_COLOR);
		instructionLabel.setBorder(new EmptyBorder(10, 10, 0, 10));

		messageDialog.add(instructionLabel, BorderLayout.NORTH);
		messageDialog.add(scrollPane, BorderLayout.CENTER);
		messageDialog.add(buttonPanel, BorderLayout.SOUTH);

		// Button actions
		ActionListener sendAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = messageArea.getText().trim();
				if (!message.isEmpty()) {
					messageSender.sendPrivateMessage(recipientName, message);
					messageDialog.dispose();
				}
			}
		};

		sendButton.addActionListener(sendAction);
		cancelButton.addActionListener(e -> messageDialog.dispose());

		// Enter key sends message, Escape cancels
		messageArea.getInputMap(JComponent.WHEN_FOCUSED).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "send");
		messageArea.getActionMap().put("send", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendAction.actionPerformed(e);
			}
		});

		messageDialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		messageDialog.getRootPane().getActionMap().put("cancel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messageDialog.dispose();
			}
		});

		// Show dialog
		SwingUtilities.invokeLater(() -> {
			messageDialog.setVisible(true);
			messageArea.requestFocusInWindow();
		});
	}

	private static void styleButton(JButton button, Color backgroundColor) {
		button.setBackground(backgroundColor);
		button.setForeground(WHITE_UI_COLOR);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(new Font("SansSerif", Font.BOLD, 11));
		button.setPreferredSize(new Dimension(80, 30));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		RainbowHoverUtil.applyRainbowHover(button);
	}
}