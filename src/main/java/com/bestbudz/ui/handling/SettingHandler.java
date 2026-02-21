package com.bestbudz.ui.handling;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingHandler
{

	public final static String[] strings = {
		"Fog", "Mip Mapping", "Moving Textures", "Status Orbs",
		"Roofs", "Debit Card", "Kill Feed", "Hover Menus", "Entity Feed", "HP Bars",
		"Hitmarkers",
		"x10 Damage", "Attack Priority", "Time Stamps", "Ground Decorations", "Flat Shading",
		"GPU Rendering"
	};
	private final static String DIR = Signlink.findCacheDir();
	private final static String PATH = DIR + "settings.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void defaultSettings()
	{
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
		SettingsConfig.enableGroundDecorations = true;
		SettingsConfig.enableFlatShading = true;
		SettingsConfig.enableGPU = false;
		Client.loadingStage = 1;
	}

	public static void save()
	{
		try
		{
			JsonObject root = new JsonObject();
			root.addProperty("username", Client.myUsername);
			root.addProperty("chatColorHex", Client.chatColorHex);
			root.addProperty("rememberMe", Client.rememberMe);

			JsonObject toggles = new JsonObject();
			toggles.addProperty("fog", SettingsConfig.enableDistanceFog);
			toggles.addProperty("mipMapping", SettingsConfig.enableMipMapping);
			toggles.addProperty("movingTextures", SettingsConfig.enableMovingTextures);
			toggles.addProperty("statusOrbs", SettingsConfig.enableStatusOrbs);
			toggles.addProperty("roofs", SettingsConfig.enableRoofs);
			toggles.addProperty("pouch", SettingsConfig.enablePouch);
			toggles.addProperty("killFeed", SettingsConfig.showKillFeed);
			toggles.addProperty("hoverMenus", SettingsConfig.menuHovers);
			toggles.addProperty("entityFeed", SettingsConfig.drawEntityFeed);
			toggles.addProperty("hpBars", SettingsConfig.enableNewHpBars);
			toggles.addProperty("hitmarkers", SettingsConfig.enableNewHitmarks);
			toggles.addProperty("x10Damage", SettingsConfig.enable10xDamage);
			toggles.addProperty("attackPriority", SettingsConfig.entityAttackPriority);
			toggles.addProperty("timeStamps", SettingsConfig.enableTimeStamps);
			toggles.addProperty("groundDecorations", SettingsConfig.enableGroundDecorations);
			toggles.addProperty("flatShading", SettingsConfig.enableFlatShading);
			toggles.addProperty("gpuRendering", SettingsConfig.enableGPU);
			root.add("toggles", toggles);

			JsonObject dock = new JsonObject();
			dock.addProperty("panels", SettingsConfig.uiDockPanels);
			dock.addProperty("dividerRatio", SettingsConfig.uiDockDividerRatio);
			dock.addProperty("lastActive", SettingsConfig.uiDockLastActive);
			root.add("dock", dock);

			Files.write(Paths.get(PATH), GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
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
			File jsonFile = new File(PATH);

			if (!jsonFile.exists())
			{
				return;
			}

			String content = new String(Files.readAllBytes(Paths.get(PATH)), StandardCharsets.UTF_8);
			JsonObject root = GSON.fromJson(content, JsonObject.class);

			Client.myUsername = getStr(root, "username", "");
			Client.chatColorHex = getStr(root, "chatColorHex", "");
			Client.rememberMe = getBool(root, "rememberMe", false);

			if (root.has("toggles"))
			{
				JsonObject t = root.getAsJsonObject("toggles");
				SettingsConfig.enableDistanceFog = getBool(t, "fog", true);
				SettingsConfig.enableMipMapping = getBool(t, "mipMapping", true);
				SettingsConfig.enableMovingTextures = getBool(t, "movingTextures", true);
				SettingsConfig.enableStatusOrbs = getBool(t, "statusOrbs", true);
				SettingsConfig.enableRoofs = getBool(t, "roofs", false);
				SettingsConfig.enablePouch = getBool(t, "pouch", true);
				SettingsConfig.showKillFeed = getBool(t, "killFeed", false);
				SettingsConfig.menuHovers = getBool(t, "hoverMenus", true);
				SettingsConfig.drawEntityFeed = getBool(t, "entityFeed", false);
				SettingsConfig.enableNewHpBars = getBool(t, "hpBars", false);
				SettingsConfig.enableNewHitmarks = getBool(t, "hitmarkers", false);
				SettingsConfig.enable10xDamage = getBool(t, "x10Damage", false);
				SettingsConfig.entityAttackPriority = getBool(t, "attackPriority", false);
				SettingsConfig.enableTimeStamps = getBool(t, "timeStamps", false);
				SettingsConfig.enableGroundDecorations = getBool(t, "groundDecorations", true);
				SettingsConfig.enableFlatShading = getBool(t, "flatShading", true);
				SettingsConfig.enableGPU = getBool(t, "gpuRendering", false);
			}

			if (root.has("dock"))
			{
				JsonObject d = root.getAsJsonObject("dock");
				SettingsConfig.uiDockPanels = getStr(d, "panels", "");
				SettingsConfig.uiDockDividerRatio = d.has("dividerRatio") ? d.get("dividerRatio").getAsFloat() : 0.5f;
				SettingsConfig.uiDockLastActive = getStr(d, "lastActive", "");
			}

			System.out.println("Settings loaded: " + strings.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private static String getStr(JsonObject obj, String key, String def)
	{
		return obj.has(key) ? obj.get(key).getAsString() : def;
	}

	private static boolean getBool(JsonObject obj, String key, boolean def)
	{
		return obj.has(key) ? obj.get(key).getAsBoolean() : def;
	}

}
