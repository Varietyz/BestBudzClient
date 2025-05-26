package com.bestbudz.config;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.Client;
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
		Configuration.enableTweening = true;
		Configuration.enableDistanceFog = true;
		Configuration.enableMipMapping = true;
		Configuration.enableMovingTextures = true;
		Configuration.enableStatusOrbs = true;
		Configuration.enableRoofs = false;
		Configuration.enablePouch = true;
		Configuration.showKillFeed = false;
		Configuration.menuHovers = true;
		Configuration.drawEntityFeed = false;
		Configuration.enableNewHpBars = false;
		Configuration.enableNewHitmarks = false;
		Configuration.enable10xDamage = false;
		Configuration.entityAttackPriority = false;
		Configuration.enableTimeStamps = false;
		Configuration.enableGroundDecorations = true;
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
			out.writeBoolean(Configuration.enableTweening);
			out.writeBoolean(Configuration.enableDistanceFog);
			out.writeBoolean(Configuration.enableMipMapping);
			out.writeBoolean(Configuration.enableMovingTextures);
			out.writeBoolean(Configuration.enableStatusOrbs);
			out.writeBoolean(Configuration.enableRoofs);
			out.writeBoolean(Configuration.enablePouch);
			out.writeBoolean(Configuration.showKillFeed);
			out.writeBoolean(Configuration.menuHovers);
			out.writeBoolean(Configuration.drawEntityFeed);
			out.writeBoolean(Configuration.enableNewHpBars);
			out.writeBoolean(Configuration.enableNewHitmarks);
			out.writeBoolean(Configuration.enable10xDamage);
			out.writeBoolean(Configuration.entityAttackPriority);
			out.writeBoolean(Configuration.enableTimeStamps);
			out.writeBoolean(Configuration.enableGroundDecorations);
			out.writeUTF(Configuration.uiDockPanels);
			out.writeFloat(Configuration.uiDockDividerRatio); // ✅ persist ratio
			out.writeUTF(Configuration.uiDockLastActive);
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
			Configuration.enableTweening = in.readBoolean();
			Configuration.enableDistanceFog = in.readBoolean();
			Configuration.enableMipMapping = in.readBoolean();
			Configuration.enableMovingTextures = in.readBoolean();
			Configuration.enableStatusOrbs = in.readBoolean();
			Configuration.enableRoofs = in.readBoolean();
			Configuration.enablePouch = in.readBoolean();
			Configuration.showKillFeed = in.readBoolean();
			Configuration.menuHovers = in.readBoolean();
			Configuration.drawEntityFeed = in.readBoolean();
			Configuration.enableNewHpBars = in.readBoolean();
			Configuration.enableNewHitmarks = in.readBoolean();
			Configuration.enable10xDamage = in.readBoolean();
			Configuration.entityAttackPriority = in.readBoolean();
			Configuration.enableTimeStamps = in.readBoolean();
			Configuration.enableGroundDecorations = in.readBoolean();
			try {
				Configuration.uiDockPanels = in.readUTF();
			} catch (Exception ignored) {
				Configuration.uiDockPanels = "";
			}

			try {
				Configuration.uiDockDividerRatio = in.readFloat();
			} catch (Exception ignored) {
				Configuration.uiDockDividerRatio = 0.5f; // sensible fallback
			}

			try {
				Configuration.uiDockLastActive = in.readUTF();
			} catch (Exception ignored) {
				Configuration.uiDockLastActive = "";
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
