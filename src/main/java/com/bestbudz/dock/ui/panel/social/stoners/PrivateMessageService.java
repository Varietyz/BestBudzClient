package com.bestbudz.dock.ui.panel.social.stoners;


import com.bestbudz.engine.core.Client;
import com.bestbudz.graphics.text.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;
import com.bestbudz.graphics.text.TextClass;

import javax.swing.*;
import java.awt.*;

public class PrivateMessageService implements MessageSender {
	private final Component parentComponent;

	public PrivateMessageService(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	@Override
	public void sendPrivateMessage(String recipientName, String message) {
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
				JOptionPane.showMessageDialog(parentComponent,
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
			JOptionPane.showMessageDialog(parentComponent,
				"Failed to send message to " + recipientName,
				"Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}
}