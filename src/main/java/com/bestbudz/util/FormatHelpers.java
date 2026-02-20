package com.bestbudz.util;

import static com.bestbudz.engine.config.ColorConfig.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FormatHelpers {
	public static String intToKOrMilLongName(int i)
	{
		String s = String.valueOf(i);
		for (int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);
		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	public static String intToKOrMil(int j)
	{
		if (j < 0x186a0)
			return String.valueOf(j);
		if (j < 0x989680)
			return j / 1000 + "K";
		else
			return j / 0xf4240 + "M";
	}

	public static final String formatKMValue(long j)
	{
		if (j >= 0 && j < 10000)
			return String.valueOf(j);
		if (j >= 10000 && j < 10000000)
			return j / 1000 + "K";
		if (j >= 10000000 && j < 999999999)
			return j / 1000000 + "M";
		if (j >= 999999999)
			return "*";
		else
			return "?";
	}
	public static String getTime()
	{
		Calendar calendar = new GregorianCalendar();
		String meridiem;
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		if (calendar.get(Calendar.AM_PM) == 0)
		{
			meridiem = "AM";
		}
		else
		{
			meridiem = "PM";
		}
		return "[" + hour + ":" + minute + " " + meridiem + "]";
	}

	private static final class ColorRange {
		final int minDiffInclusive;
		final int maxDiffInclusive;
		final String hexColor;

		ColorRange(int minDiffInclusive, int maxDiffInclusive, String hexColor) {
			this.minDiffInclusive = minDiffInclusive;
			this.maxDiffInclusive = maxDiffInclusive;
			this.hexColor = hexColor;
		}

		boolean matches(int diff) {
			return diff >= minDiffInclusive && diff <= maxDiffInclusive;
		}
	}

	private static final ColorRange[] colorRanges = {
		new ColorRange(Integer.MIN_VALUE, -421, "<col=" + Integer.toHexString(COMBATGRADE_SOFT_MAGENTA) + ">"),
		new ColorRange(-420, -361, "<col=" + Integer.toHexString(COMBATGRADE_LAVENDER_PINK) + ">"),
		new ColorRange(-360, -301, "<col=" + Integer.toHexString(COMBATGRADE_BABY_PURPLE) + ">"),
		new ColorRange(-300, -241, "<col=" + Integer.toHexString(COMBATGRADE_SOFT_VIOLET) + ">"),
		new ColorRange(-240, -181, "<col=" + Integer.toHexString(COMBATGRADE_VERY_PALE_ROSE) + ">"),
		new ColorRange(-180, -121, "<col=" + Integer.toHexString(COMBATGRADE_PASTEL_PINK) + ">"),
		new ColorRange(-120, -61,  "<col=" + Integer.toHexString(COMBATGRADE_PINK_MAGENTA) + ">"),
		new ColorRange(-60, -1,    "<col=" + Integer.toHexString(COMBATGRADE_PALE_BLUSH) + ">"),
		new ColorRange(0, 0,       "<col=" + Integer.toHexString(COMBATGRADE_WHITE) + ">"),
		new ColorRange(1, 60,      "<col=" + Integer.toHexString(COMBATGRADE_MINT_CREAM) + ">"),
		new ColorRange(61, 120,    "<col=" + Integer.toHexString(COMBATGRADE_LIGHT_TEAL) + ">"),
		new ColorRange(121, 180,   "<col=" + Integer.toHexString(COMBATGRADE_SOFT_AQUA) + ">"),
		new ColorRange(181, 240,   "<col=" + Integer.toHexString(COMBATGRADE_LIGHT_CYAN) + ">"),
		new ColorRange(241, 300,   "<col=" + Integer.toHexString(COMBATGRADE_PASTEL_BLUE) + ">"),
		new ColorRange(301, 360,   "<col=" + Integer.toHexString(COMBATGRADE_SKY_BLUE) + ">"),
		new ColorRange(361, 420,   "<col=" + Integer.toHexString(COMBATGRADE_FADED_BLUE) + ">"),
		new ColorRange(421, Integer.MAX_VALUE, "<col=" + Integer.toHexString(COMBATGRADE_LIGHT_AZURE) + ">")
	};

	public static String combatDiffColor(int myLevel, int targetLevel) {
		int diff = myLevel - targetLevel;
		for (ColorRange range : colorRanges) {
			if (range.matches(diff)) {
				return range.hexColor;
			}
		}
		return "<col=FFFF00>";
	}

	public static String formatBestBucks(long amount)
	{
		if (amount >= 1_000 && amount < 1_000_000)
		{
			return (amount / 1_000) + "K";
		}

		if (amount >= 1_000_000 && amount < 1_000_000_000)
		{
			return (amount / 1_000_000) + "M";
		}

		if (amount >= 1_000_000_000 && amount < 1_000_000_000_000L)
		{
			return (amount / 1_000_000_000) + "B";
		}

		if (amount >= 1_000_000_000_000L && amount < 1_000_000_000_000_000L)
		{
			return (amount / 1_000_000_000_000L) + "T";
		}

		if (amount >= 1_000_000_000_000_000L && amount < 1_000_000_000_000_000_000L)
		{
			return (amount / 1_000_000_000_000_000L) + "F";
		}

		if (amount >= 1_000_000_000_000_000_000L)
		{
			return (amount / 1_000_000_000_000_000_000L) + "A";
		}
		return "" + amount;
	}

}
