package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.engine.Client;

import com.bestbudz.ui.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;
import com.bestbudz.util.TextClass;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StonersPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final Timer refreshTimer;

	private final DefaultListModel<StonerEntry> listModel = new DefaultListModel<>();
	private final JList<StonerEntry> stonerList;

	public StonersPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		// Title
		JLabel title = new JLabel("Stoners");
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		title.setForeground(new Color(0xFF9933));
		title.setBorder(new EmptyBorder(0, 0, 6, 0));
		add(title, BorderLayout.NORTH);

		// List
		stonerList = new JList<>(listModel);
		stonerList.setOpaque(false);
		stonerList.setCellRenderer(new StonerCellRenderer());
		stonerList.setFixedCellHeight(22);
		stonerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stonerList.setVisibleRowCount(20);

		// Left-click -> Show message popup
		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int index = stonerList.locationToIndex(e.getPoint());
					if (index >= 0 && index < listModel.size()) {
						StonerEntry selected = listModel.get(index);
						if (selected != null && selected.isHigh) { // Only allow messaging if they're high
							showMessageDialog(selected.name);
						}
					}
				}
			}
		});

		// Hover highlight
		stonerList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = stonerList.locationToIndex(e.getPoint());
				stonerList.setSelectedIndex(index);
			}
		});

		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				stonerList.clearSelection();
			}
		});

		JScrollPane scroll = new JScrollPane(stonerList);
		scroll.setOpaque(false);
		scroll.setBorder(null);
		scroll.getViewport().setOpaque(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scroll, BorderLayout.CENTER);

		refreshTimer = new Timer(3000, e -> {
			if (isShowing() && Client.loggedIn) {
				onActivate(); // safely refresh list
			}
		});
		refreshTimer.setRepeats(true);

		ToolTipManager.sharedInstance().setInitialDelay(300);
		ToolTipManager.sharedInstance().setDismissDelay(4000);

		stonerList.setToolTipText(""); // activate tooltip system

		stonerList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = stonerList.locationToIndex(e.getPoint());
				if (index >= 0 && index < listModel.size()) {
					StonerEntry entry = listModel.get(index);

					if (entry.isHigh) {
						stonerList.setToolTipText(
							"<html><font color='#5FD17A'>Send message to " + entry.name + "</font></html>"
						);
					} else {
						stonerList.setToolTipText(null); // no tooltip if asleep
					}
				} else {
					stonerList.setToolTipText(null);
				}
			}
		});
	}

	private void showMessageDialog(String recipientName) {
		if (!Client.loggedIn || recipientName == null || recipientName.isEmpty()) {
			return;
		}

		// Create custom dialog
		JDialog messageDialog = new JDialog();
		messageDialog.setTitle("Send Message to " + recipientName);
		messageDialog.setModal(true);
		messageDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		messageDialog.setSize(400, 150);
		messageDialog.setLocationRelativeTo(this);
		messageDialog.setResizable(false);

		// Style the dialog
		messageDialog.getContentPane().setBackground(new Color(0x2D2D2D));
		messageDialog.setLayout(new BorderLayout(10, 10));

		// Message input area
		JTextArea messageArea = new JTextArea(3, 30);
		messageArea.setBackground(new Color(0x393939));
		messageArea.setForeground(Color.WHITE);
		messageArea.setCaretColor(Color.WHITE);
		messageArea.setBorder(new EmptyBorder(8, 8, 8, 8));
		messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.requestFocusInWindow();

		JScrollPane scrollPane = new JScrollPane(messageArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x5FD17A), 1));
		scrollPane.setBackground(new Color(0x393939));

		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(new Color(0x2D2D2D));

		JButton sendButton = new JButton("Send");
		JButton cancelButton = new JButton("Cancel");

		// Style buttons
		styleButton(sendButton, new Color(0x5FD17A));
		styleButton(cancelButton, new Color(0xE06666));

		buttonPanel.add(cancelButton);
		buttonPanel.add(sendButton);

		// Add components to dialog
		JLabel instructionLabel = new JLabel("Enter your message:");
		instructionLabel.setForeground(Color.WHITE);
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
					sendPrivateMessage(recipientName, message);
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

	private void styleButton(JButton button, Color backgroundColor) {
		button.setBackground(backgroundColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(new Font("SansSerif", Font.BOLD, 11));
		button.setPreferredSize(new Dimension(80, 30));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(backgroundColor.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(backgroundColor);
			}
		});
	}

	private void sendPrivateMessage(String recipientName, String message) {
		if (!Client.loggedIn || recipientName == null || recipientName.isEmpty() || message.trim().isEmpty()) {
			return;
		}

		try {
			// Convert recipient name to hash
			long recipientHash = TextClass.longForName(recipientName);

			// Find the recipient in the stoners list
			int recipientIndex = -1;
			for (int i = 0; i < Client.stonersCount; i++) {
				if (Client.stonersListAsLongs[i] == recipientHash) {
					recipientIndex = i;
					break;
				}
			}

			// Verify recipient is online and reachable
			if (recipientIndex == -1 || Client.stonersNodeIDs[recipientIndex] <= 0) {
				JOptionPane.showMessageDialog(this,
					recipientName + " is not available for messaging.",
					"Cannot Send Message",
					JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Send the message using the game's protocol (frame 126)
			Client.stream.createFrame(126);
			Client.stream.writeWordBigEndian(0);
			int frameStart = Client.stream.currentOffset;
			Client.stream.writeQWord(recipientHash);
			TextInput.method526(message, Client.stream);
			Client.stream.writeBytes(Client.stream.currentOffset - frameStart);

			// Process and display the message locally using the correct method
			String processedMessage = TextInput.processText(message);
			Chatbox.pushMessage(processedMessage, 6, TextClass.fixName(TextClass.nameForLong(recipientHash)));

			// Handle private chat mode if needed
			if (Chatbox.privateChatMode == 2) {
				Chatbox.privateChatMode = 1;
				Client.stream.createFrame(95);
				Client.stream.writeWordBigEndian(Chatbox.privateChatMode);
			}

		} catch (Exception e) {
			System.err.println("Failed to send private message: " + e.getMessage());
			e.printStackTrace();

			// Show error dialog
			JOptionPane.showMessageDialog(this,
				"Failed to send message to " + recipientName,
				"Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void updateText() {
		// Implementation remains the same
	}

	@Override
	public String getPanelID() {
		return "Stoners";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn || Client.stonersList == null) return;

		listModel.clear();
		int count = Math.min(Client.stonersCount, Client.stonersList.length);
		for (int i = 0; i < count; i++) {
			String name = Client.stonersList[i];
			int node = Client.stonersNodeIDs[i];
			boolean high = node == Client.nodeID;
			listModel.addElement(new StonerEntry(name, high));
		}

		refreshTimer.start(); // 🔄 begin auto-refresh
	}

	@Override
	public void onDeactivate() {
		refreshTimer.stop(); // 🛑 disable while hidden
	}

	@Override
	public void updateDockText(int index, String text) {
		if (index < 0 || index >= listModel.size()) return;
		StonerEntry entry = listModel.get(index);
		listModel.set(index, new StonerEntry(text, entry.isHigh));
	}

	static class StonerEntry {
		final String name;
		final boolean isHigh;

		StonerEntry(String name, boolean isHigh) {
			this.name = name;
			this.isHigh = isHigh;
		}
	}

	static class StonerCellRenderer extends JPanel implements ListCellRenderer<StonerEntry> {
		private final JLabel nameLabel = new JLabel();
		private final JLabel statusLabel = new JLabel();

		private static final Color GREEN = new Color(0x5FD17A);
		private static final Color RED = new Color(0xE06666);
		private static final Color BG_HOVER = new Color(0x393939);
		private static final Color BG_SELECTED = new Color(0x2D2D2D);

		public StonerCellRenderer() {
			setLayout(new BorderLayout());
			setOpaque(true);
			setBorder(new EmptyBorder(2, 8, 2, 8));
			setMaximumSize(new Dimension(280, 22));

			nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			nameLabel.setForeground(Color.WHITE);

			statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
			statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			add(nameLabel, BorderLayout.WEST);
			add(statusLabel, BorderLayout.EAST);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends StonerEntry> list, StonerEntry value, int index,
													  boolean isSelected, boolean cellHasFocus) {

			nameLabel.setText(value.name);
			statusLabel.setText(value.isHigh ? "● High" : "Asleep");
			statusLabel.setForeground(value.isHigh ? GREEN : RED);

			if (isSelected) {
				setBackground(BG_SELECTED);
			} else {
				setBackground(new Color(0, 0, 0, 0));
			}

			// Only show hand cursor for users who are high
			setCursor(value.isHigh ?
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
				Cursor.getDefaultCursor());
			return this;
		}
	}
}