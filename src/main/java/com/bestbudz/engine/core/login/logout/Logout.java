package com.bestbudz.engine.core.login.logout;

import com.bestbudz.data.AccountManager;
import static com.bestbudz.engine.ClientLauncher.gpuContextManager;
import static com.bestbudz.engine.ClientLauncher.gpuInitialized;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.ui.handling.SettingHandler;
import static com.bestbudz.ui.handling.input.Keyboard.console;

public class Logout extends Client
{
	public static void resetLogout()
	{
		try
		{
			if (socketStream != null)
				socketStream.close();
		}
		catch (Exception _ex)
		{
		}
		if (rememberMe)
		{
			AccountManager.saveAccount();
		}
		setBounds();
		socketStream = null;
		loggedIn = false;
		loginComplete = false;
		loginInProgress = false;
		loginScreenState = 0;
		loginMessage1 = "Welcome to BestBudz";
		loginMessage2 = "Where the weed flows, where the tree grows!";
		if (!rememberMe)
		{
			myUsername = "";
			myPassword = "";
		}
		unlinkMRUNodes();
		worldController.initToNull();
		for (int i = 0; i < 4; i++)
			aClass11Array1230[i].method210();
		System.gc();
		stopMidi();
		currentSong = -1;
		nextSong = -1;
		prevSong = 0;
		SettingHandler.save();
		console.openConsole = false;
		if (gpuInitialized) {
			System.out.println("[ClientLauncher] Cleaning up GPU resources...");
			try {
				GPURenderingEngine.cleanup();
				if (gpuContextManager != null) {
					gpuContextManager.shutdown();
				}
				System.out.println("[ClientLauncher] ✅ GPU resources cleaned up successfully");
			} catch (Exception e) {
				System.err.println("[ClientLauncher] ⚠️ Error during GPU cleanup: " + e.getMessage());
			}
		}
	}
}
