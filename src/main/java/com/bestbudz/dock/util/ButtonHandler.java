package com.bestbudz.dock.util;

import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Simple handler for sending interface click actions to the game client
 */
public class ButtonHandler {

	/**
	 * Sends an interface click packet to the game client
	 * @param interfaceId The interface ID to activate
	 */
	public static void sendClick(int interfaceId) {
		try {
			interfaceId = interfaceId - 85560;
			if (Client.stream != null) {
				Client.stream.createFrame(185);
				Client.stream.writeWord(interfaceId);
				System.out.println("Sent click for interface ID: " + interfaceId);
			} else {
				System.err.println("Client stream is null - cannot send click");
			}
		} catch (Exception ex) {
			System.err.println("Failed to send click for ID " + interfaceId);
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a click listener that sends the specified interface ID
	 * @param interfaceId The ID to send when clicked
	 * @return MouseListener for the click action
	 */
	public static MouseAdapter createClickListener(int interfaceId) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendClick(interfaceId);
			}
		};
	}

	/**
	 * Creates an ActionListener for buttons that sends the specified interface ID
	 * @param interfaceId The ID to send when clicked
	 * @return ActionListener for the click action
	 */
	public static ActionListener createButtonListener(int interfaceId) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendClick(interfaceId);
			}
		};
	}
}