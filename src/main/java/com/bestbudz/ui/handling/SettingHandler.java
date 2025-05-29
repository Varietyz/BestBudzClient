package com.bestbudz.ui.handling;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;

public class SettingHandler
{

	public final static String[] strings = {
		"Tweening", "Fog", "Mip Mapping", "Moving Textures", "Status Orbs",
		"Roofs", "Debit Card", "Kill Feed", "Hover Menus", "Entity Feed", "HP Bars",
		"Hitmarkers",
		"x10 Damage", "Attack Priority", "Time Stamps", "Ground Decorations"
	};
	private final static String PATH = Signlink.findCacheDir() + "/settings.dat";

	public static void defaultSettings()
	{
		SettingsConfig.enableTweening = true;
		SettingsConfig.enableDistanceFog = true;
		SettingsConfig.enableMipMapping = true;
		SettingsConfig.enableMovingTextures = true;
		SettingsConfig.enableStatusOrbs = true;
		SettingsConfig.enableRoofs = false;
		SettingsConfig.enablePouch = true;
		SettingsConfig.showKillFeed = false;
		SettingsConfig.menuHovers = true;
		SettingsConfig.drawEntityFeed = false;
		SettingsConfig.enableNewHpBars = false;
		SettingsConfig.enableNewHitmarks = false;
		SettingsConfig.enable10xDamage = false;
		SettingsConfig.entityAttackPriority = false;
		SettingsConfig.enableTimeStamps = false;
		Client.loadingStage = 1;
		Client.minimapImage.method343();
	}

	public static void save()
	{
		try
		{
			File file = new File(PATH);
			DataOutputStream out = new DataOutputStream(Files.newOutputStream(file.toPath()));
			out.writeUTF(Client.myUsername);
			out.writeUTF(Client.myPassword);
			out.writeUTF(Client.chatColorHex);
			out.writeBoolean(Client.rememberMe);
			out.writeBoolean(SettingsConfig.enableTweening);
			out.writeBoolean(SettingsConfig.enableDistanceFog);
			out.writeBoolean(SettingsConfig.enableMipMapping);
			out.writeBoolean(SettingsConfig.enableMovingTextures);
			out.writeBoolean(SettingsConfig.enableStatusOrbs);
			out.writeBoolean(SettingsConfig.enableRoofs);
			out.writeBoolean(SettingsConfig.enablePouch);
			out.writeBoolean(SettingsConfig.showKillFeed);
			out.writeBoolean(SettingsConfig.menuHovers);
			out.writeBoolean(SettingsConfig.drawEntityFeed);
			out.writeBoolean(SettingsConfig.enableNewHpBars);
			out.writeBoolean(SettingsConfig.enableNewHitmarks);
			out.writeBoolean(SettingsConfig.enable10xDamage);
			out.writeBoolean(SettingsConfig.entityAttackPriority);
			out.writeBoolean(SettingsConfig.enableTimeStamps);
			out.writeUTF(SettingsConfig.uiDockPanels);
			out.writeFloat(SettingsConfig.uiDockDividerRatio); // ✅ persist ratio
			out.writeUTF(SettingsConfig.uiDockLastActive);
			out.close();
			System.out.println("Successfully saved " + strings.length + " settings.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void load()
	{
		try
		{
			File file = new File(PATH);
			if (!file.exists())
			{
				return;
			}
			DataInputStream in = new DataInputStream(Files.newInputStream(file.toPath()));
			Client.myUsername = in.readUTF();
			Client.myPassword = in.readUTF();
			Client.chatColorHex = in.readUTF();
			Client.rememberMe = in.readBoolean();
			SettingsConfig.enableTweening = in.readBoolean();
			SettingsConfig.enableDistanceFog = in.readBoolean();
			SettingsConfig.enableMipMapping = in.readBoolean();
			SettingsConfig.enableMovingTextures = in.readBoolean();
			SettingsConfig.enableStatusOrbs = in.readBoolean();
			SettingsConfig.enableRoofs = in.readBoolean();
			SettingsConfig.enablePouch = in.readBoolean();
			SettingsConfig.showKillFeed = in.readBoolean();
			SettingsConfig.menuHovers = in.readBoolean();
			SettingsConfig.drawEntityFeed = in.readBoolean();
			SettingsConfig.enableNewHpBars = in.readBoolean();
			SettingsConfig.enableNewHitmarks = in.readBoolean();
			SettingsConfig.enable10xDamage = in.readBoolean();
			SettingsConfig.entityAttackPriority = in.readBoolean();
			SettingsConfig.enableTimeStamps = in.readBoolean();
			try {
				SettingsConfig.uiDockPanels = in.readUTF();
			} catch (Exception ignored) {
				SettingsConfig.uiDockPanels = "";
			}

			try {
				SettingsConfig.uiDockDividerRatio = in.readFloat();
			} catch (Exception ignored) {
				SettingsConfig.uiDockDividerRatio = 0.5f; // sensible fallback
			}

			try {
				SettingsConfig.uiDockLastActive = in.readUTF();
			} catch (Exception ignored) {
				SettingsConfig.uiDockLastActive = "";
			}

			in.close();
			System.out.println("Settings loaded: " + strings.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
