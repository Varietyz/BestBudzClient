package com.bestbudz.dock.util;

import com.bestbudz.engine.core.Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.InterfaceProto.*;

public class ButtonHandler {

	public static void sendClick(int interfaceId) {
		try {
			interfaceId = interfaceId - 85560;
			if (Client.stream != null) {
				Client.sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(interfaceId)).build());
				System.out.println("Sent click for interface ID: " + interfaceId);
			} else {
				System.err.println("Client stream is null - cannot send click");
			}
		} catch (Exception ex) {
			System.err.println("Failed to send click for ID " + interfaceId);
			ex.printStackTrace();
		}
	}

	public static MouseAdapter createClickListener(int interfaceId) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendClick(interfaceId);
			}
		};
	}

	public static ActionListener createButtonListener(int interfaceId) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendClick(interfaceId);
			}
		};
	}
}
