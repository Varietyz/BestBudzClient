package com.bestbudz.rendering;

import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class OverlayFloor
{

	public static OverlayFloor[] overlayFloor;
	public boolean aBoolean393;
	public int textureId = -1;
	public int rgb;
	public boolean boolean_5;
	public int int_7;
	public int int_9;
	public boolean boolean_10;
	public int int_11;
	public boolean boolean_12;
	public int int_13;
	public int int_14;
	public int int_15;
	public int int_16;

	public int anInt394;
	public int anInt395;
	public int anInt396;
	public int anInt397;
	public int anInt398;
	public int anInt399;

	public static void unpackConfig()
	{
		JsonObject json = JsonCacheLoader.loadJsonObject("floors_overlay.json");
		if (json == null) {
			System.err.println("Failed to load floors_overlay.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		int count = maxId + 1;
		overlayFloor = new OverlayFloor[count];

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			JsonObject def = entry.getValue().getAsJsonObject();

			if (overlayFloor[id] == null) {
				overlayFloor[id] = new OverlayFloor();
			}
			overlayFloor[id].readFromJson(def);
			loaded++;
		}

		System.out.println("Overlays Loaded (JSON): " + loaded);
	}

	private void readFromJson(JsonObject json) {
		if (json.has("rgb")) {
			rgb = json.get("rgb").getAsInt();
			method262(rgb);
		}
		if (json.has("textureId")) {
			textureId = json.get("textureId").getAsInt();
			if (textureId == 65535) textureId = -1;
		}
		if (json.has("hideUnderlay")) {
			boolean_5 = false;
		}
		if (json.has("secondaryRgb")) {
			int_7 = json.get("secondaryRgb").getAsInt();
		}
		if (json.has("blendMode")) {
			int_9 = json.get("blendMode").getAsInt();
		}
		if (json.has("boolean_10") && !json.get("boolean_10").getAsBoolean()) {
			boolean_10 = false;
		}
		if (json.has("int_11")) {
			int_11 = json.get("int_11").getAsInt();
		}
		if (json.has("boolean_12")) {
			boolean_12 = json.get("boolean_12").getAsBoolean();
		}
		if (json.has("tertiaryRgb")) {
			int_13 = json.get("tertiaryRgb").getAsInt();
		}
		if (json.has("int_14")) {
			int_14 = json.get("int_14").getAsInt();
		}
		if (json.has("int_15")) {
			int_15 = json.get("int_15").getAsInt();
			if (int_15 == 65535) int_15 = -1;
		}
		if (json.has("int_16")) {
			int_16 = json.get("int_16").getAsInt();
		}
	}

	private void method262(int arg0)
	{
		double d = (arg0 >> 16 & 0xff) / 256.0;
		double d_5_ = (arg0 >> 8 & 0xff) / 256.0;
		double d_6_ = (arg0 & 0xff) / 256.0;
		double d_7_ = d;
		if (d_5_ < d_7_)
		{
			d_7_ = d_5_;
		}
		if (d_6_ < d_7_)
		{
			d_7_ = d_6_;
		}
		double d_8_ = d;
		if (d_5_ > d_8_)
		{
			d_8_ = d_5_;
		}
		if (d_6_ > d_8_)
		{
			d_8_ = d_6_;
		}
		double d_9_ = 0.0;
		double d_10_ = 0.0;
		double d_11_ = (d_7_ + d_8_) / 2.0;
		if (d_7_ != d_8_)
		{
			if (d_11_ < 0.5)
			{
				d_10_ = (d_8_ - d_7_) / (d_8_ + d_7_);
			}
			if (d_11_ >= 0.5)
			{
				d_10_ = (d_8_ - d_7_) / (2.0 - d_8_ - d_7_);
			}
			if (d == d_8_)
			{
				d_9_ = (d_5_ - d_6_) / (d_8_ - d_7_);
			}
			else if (d_5_ == d_8_)
			{
				d_9_ = 2.0 + (d_6_ - d) / (d_8_ - d_7_);
			}
			else if (d_6_ == d_8_)
			{
				d_9_ = 4.0 + (d - d_5_) / (d_8_ - d_7_);
			}
		}
		d_9_ /= 6.0;
		anInt394 = (int) (d_9_ * 256.0);
		anInt395 = (int) (d_10_ * 256.0);
		anInt396 = (int) (d_11_ * 256.0);
		if (anInt395 < 0)
		{
			anInt395 = 0;
		}
		else if (anInt395 > 255)
		{
			anInt395 = 255;
		}
		if (anInt396 < 0)
		{
			anInt396 = 0;
		}
		else if (anInt396 > 255)
		{
			anInt396 = 255;
		}
		if (d_11_ > 0.5)
		{
			anInt398 = (int) ((1.0 - d_11_) * d_10_ * 512.0);
		}
		else
		{
			anInt398 = (int) (d_11_ * d_10_ * 512.0);
		}
		if (anInt398 < 1)
		{
			anInt398 = 1;
		}
		anInt397 = (int) (d_9_ * anInt398);
		anInt399 = method263(anInt394, anInt395, anInt396);
	}

	private int method263(int arg0, int arg1, int arg2)
	{
		if (arg2 > 179)
		{
			arg1 /= 2;
		}
		if (arg2 > 192)
		{
			arg1 /= 2;
		}
		if (arg2 > 217)
		{
			arg1 /= 2;
		}
		if (arg2 > 243)
		{
			arg1 /= 2;
		}
		return (arg0 / 4 << 10) + (arg1 / 32 << 7) + arg2 / 2;
	}

}
