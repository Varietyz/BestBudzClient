package com.bestbudz.data;

public final class Skills {

	public static final int SKILLS_COUNT = 25;

	public static final int ASSAULT = 0;
	public static final int AEGIS = 1;
	public static final int VIGOUR = 2;
	public static final int LIFE = 3;
	public static final int SAGITTARIUS = 4;
	public static final int NECROMANCE = 5;
	public static final int MAGE = 6;
	public static final int FOODIE = 7;
	public static final int LUMBERING = 8;
	public static final int WOODCARVING = 9;
	public static final int FISHER = 10;
	public static final int PYROMANIAC = 11;
	public static final int HANDINESS = 12;
	public static final int FORGING = 13;
	public static final int QUARRYING = 14;
	public static final int THCHEMPISTRY = 15;
	public static final int WEEDSMOKING = 16;
	public static final int ACCOMPLISHER = 17;
	public static final int MERCENARY = 18;
	public static final int CULTIVATION = 19;
	public static final int CONSUMER = 20;
	public static final int SUMMONING = 22;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 23;
	public static final int DUNGEONEERING = 24;

	public static final int[][] STAT_IDS = new int[][] { { 4004, 4005 }, { 4008, 4009 }, { 4006, 4007 }, { 4016, 4017 },
			{ 4010, 4011 }, { 4012, 4013 }, { 4014, 4015 }, { 4034, 4035 }, { 4038, 4039 }, { 4026, 4027 },
			{ 4032, 4033 }, { 4036, 4037 }, { 4024, 4025 }, { 4030, 4031 }, { 4028, 4029 }, { 4020, 4021 },
			{ 4018, 4019 }, { 4022, 4023 }, { 12166, 12167 }, { 13926, 13927 }, { 4152, 4153 }, { -1, -1 },
			{ 24134, 24135 },

			{ -1, -1 }, { -1, -1 } };

	public static final String[] SKILL_NAMES = { "assault", "aegis", "vigour", "life", "sagittarius", "necromance",
			"mage", "foodie", "lumbering", "woodcarving", "fisher", "pyromaniac", "handiness", "forging",
			"quarrying",
			"thc-hempistry", "weedsmoking", "accomplisher", "mercenary", "cultivation", "consumer", "hunter", "summoning",
			"construction", "dungeoneering", "grades total" };

	public static final boolean[] SKILL_ENABLED = new boolean[SKILLS_COUNT];

	public static final int[] EXP_FOR_LEVEL = new int[99];

	public static final int getIdByName(String skill) {
		for (int i = 0; i < SKILL_NAMES.length; i++) {
			if (SKILL_NAMES[i].equalsIgnoreCase(skill)) {
				return i;
			}
		}
		return 0;
	}

	static {
		int points = 0;
		for (int lvl = 1; lvl < 99; lvl++) {
			points += lvl + 300 * Math.pow(2, lvl / 7.0);
			EXP_FOR_LEVEL[lvl - 1] = points / 4;
		}

		SKILL_ENABLED[ASSAULT] = true;
		SKILL_ENABLED[AEGIS] = true;
		SKILL_ENABLED[VIGOUR] = true;
		SKILL_ENABLED[LIFE] = true;
		SKILL_ENABLED[SAGITTARIUS] = true;
		SKILL_ENABLED[NECROMANCE] = true;
		SKILL_ENABLED[MAGE] = true;
		SKILL_ENABLED[FOODIE] = true;
		SKILL_ENABLED[LUMBERING] = true;
		SKILL_ENABLED[WOODCARVING] = true;
		SKILL_ENABLED[FISHER] = true;
		SKILL_ENABLED[PYROMANIAC] = true;
		SKILL_ENABLED[HANDINESS] = true;
		SKILL_ENABLED[FORGING] = true;
		SKILL_ENABLED[QUARRYING] = true;
		SKILL_ENABLED[THCHEMPISTRY] = true;
		SKILL_ENABLED[WEEDSMOKING] = true;
		SKILL_ENABLED[ACCOMPLISHER] = true;
		SKILL_ENABLED[MERCENARY] = true;
		SKILL_ENABLED[CULTIVATION] = true;
		SKILL_ENABLED[CONSUMER] = true;
		SKILL_ENABLED[HUNTER] = false;
		SKILL_ENABLED[SUMMONING] = false;
		SKILL_ENABLED[CONSTRUCTION] = false;
		SKILL_ENABLED[DUNGEONEERING] = false;
	}

	public static String getAdvanceColor(int advance) {
		if (advance == 1) {
			return "005EFF";
		} else if (advance == 2) {
			return "336600";
		} else if (advance == 3) {
			return "A300CC";
		} else if (advance == 4) {
			return "E6E600";
		} else if (advance == 5) {
			return "B80000";
		}
		return "070707";
	}
}