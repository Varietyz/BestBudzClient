package com.bestbudz.dock.ui.panel.social.stoners;

import com.bestbudz.engine.core.Client;
import com.bestbudz.graphics.text.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;
import com.bestbudz.graphics.text.TextClass;

import javax.swing.*;
import java.awt.*;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.ChatProto.*;
import com.google.protobuf.ByteString;

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

			long recipientHash = TextClass.longForName(recipientName);

			int recipientIndex = -1;
			for (int i = 0; i < Client.stonersCount; i++) {
				if (Client.stonersListAsLongs[i] == recipientHash) {
					recipientIndex = i;
					break;
				}
			}

			if (recipientIndex == -1 || Client.stonersNodeIDs[recipientIndex] <= 0) {
				JOptionPane.showMessageDialog(parentComponent,
					recipientName + " is not available for messaging.",
					"Cannot Send Message",
					JOptionPane.WARNING_MESSAGE);
				return;
			}

			Client.sendProto(GamePacket.newBuilder().setPrivateMessageSend(PrivateMessageSend.newBuilder().setRecipientHash(recipientHash).setMessage(ByteString.copyFrom(message.getBytes()))).build());

			String processedMessage = TextInput.processText(message);
			Chatbox.pushMessage(processedMessage, 6, TextClass.fixName(TextClass.nameForLong(recipientHash)));

			if (Chatbox.privateChatMode == 2) {
				Chatbox.privateChatMode = 1;
				Client.sendProto(GamePacket.newBuilder().setChatModeUpdate(ChatModeUpdate.newBuilder().setPublicMode(Chatbox.publicChatMode).setPrivateMode(Chatbox.privateChatMode).setTradeMode(0)).build());
			}

		} catch (Exception e) {
			System.err.println("Failed to send private message: " + e.getMessage());
			e.printStackTrace();

			JOptionPane.showMessageDialog(parentComponent,
				"Failed to send message to " + recipientName,
				"Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}
}
