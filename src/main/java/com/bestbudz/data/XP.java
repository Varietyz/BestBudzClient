package com.bestbudz.data;

import com.bestbudz.engine.core.Client;
import java.util.LinkedList;

public class XP extends Client
{
	public static final LinkedList<XPGain> gains = new LinkedList<XPGain>();

	public static void addXP(int skillID, int xp)
	{
		if (xp != 0 && canGainXP)
		{
			gains.add(new XPGain(skillID, xp));
		}
	}

	public static class XPGain
	{
		public final int skill;
		public int xp;
		private int y;
		private int alpha = 0;

		public XPGain(int skill, int xp)
		{
			this.skill = skill;
			this.xp = xp;
		}

		public void increaseY()
		{
			y++;
		}

		public int getSkill()
		{
			return skill;
		}

		public int getXP()
		{
			return xp;
		}

		public int getY()
		{
			return y;
		}

		public int getAlpha()
		{
			return alpha;
		}

		public void increaseAlpha()
		{
			alpha += alpha < 256 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}

		public void decreaseAlpha()
		{
			alpha -= alpha > 0 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}
	}
}
