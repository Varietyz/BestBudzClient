package com.bestbudz.dock.net;

import com.bestbudz.engine.core.Client;

/**
 * Utility for sending structured dock panel interaction commands to the server.
 * Used by modern UIDockFrame panels instead of legacy RSInterface frame triggers.
 */
public class DockNetworkUtil {

	/**
	 * Sends a raw dock interaction string to the server.
	 *
	 * @param command Dock command (e.g. "bank:withdraw:995:10")
	 */
	public static void send(String command) {
		if (Client.stream == null || !Client.loggedIn) return;
		Client.stream.createFrame(222); // Opcode must match registration
		Client.stream.writeString(command);
	}

	// ====== BANK COMMAND HELPERS ======

	public static void openBank() {
		send("bank:open");
	}

	public static void updateBank() {
		send("bank:update");
	}

	public static void withdraw(int itemId, int amount) {
		send("bank:withdraw:" + itemId + ":" + amount);
	}

	public static void deposit(int itemId, int amount, int slot) {
		send("bank:deposit:" + itemId + ":" + amount + ":" + slot);
	}

	public static void setBankTab(int tab) {
		send("bank:tab:" + tab);
	}

	public static void setBankSearching(boolean search) {
		send("bank:search:" + search);
	}

	public static void setWithdrawMode(boolean noteMode) {
		send("bank:withdrawtype:" + (noteMode ? "note" : "item"));
	}

	public static void setRearrangeMode(boolean insertMode) {
		send("bank:rearrange:" + (insertMode ? "insert" : "swap"));
	}
}
