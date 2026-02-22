package com.bestbudz.dock.ui.panel.skills;

import com.bestbudz.data.Skills;
import com.bestbudz.engine.core.Client;
import java.util.HashMap;
import java.util.Map;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.InterfaceProto.*;

public class SkillClickHandler {
	private static long lastClickTime = 0;

	// Mapping of skill names to server button IDs for profession chat
	private static final Map<String, Integer> SKILL_BUTTON_MAP = new HashMap<>();
	static {
		SKILL_BUTTON_MAP.put("assault", 94147);
		SKILL_BUTTON_MAP.put("aegis", 94153);
		SKILL_BUTTON_MAP.put("vigour", 94150);
		SKILL_BUTTON_MAP.put("life", 94148);
		SKILL_BUTTON_MAP.put("sagittarius", 94156);
		SKILL_BUTTON_MAP.put("resonance", 94159);
		SKILL_BUTTON_MAP.put("mage", 94162);
		SKILL_BUTTON_MAP.put("foodie", 94158);
		SKILL_BUTTON_MAP.put("lumbering", 94164);
		SKILL_BUTTON_MAP.put("woodcarving", 94163);
		SKILL_BUTTON_MAP.put("fisher", 94155);
		SKILL_BUTTON_MAP.put("pyromaniac", 94161);
		SKILL_BUTTON_MAP.put("handiness", 94160);
		SKILL_BUTTON_MAP.put("forging", 94152);
		SKILL_BUTTON_MAP.put("quarrying", 94149);
		SKILL_BUTTON_MAP.put("thc-hempistry", 94154);
		SKILL_BUTTON_MAP.put("weedsmoking", 94151);
		SKILL_BUTTON_MAP.put("pet master", 94157);
		SKILL_BUTTON_MAP.put("mercenary", 94166);
		SKILL_BUTTON_MAP.put("bankstanding", 94167);
		SKILL_BUTTON_MAP.put("consumer", 94165);
	}

	public static void handleSkillClick(int id) {
		long currentTime = System.currentTimeMillis();
		String skillName = Skills.SKILL_NAMES[id].toLowerCase();

		if (currentTime - lastClickTime < 500) return;
		lastClickTime = currentTime;

		Integer serverButtonId = SKILL_BUTTON_MAP.get(skillName);

		if (serverButtonId != null) {
			int clientButtonId = calculateClientButtonId(serverButtonId);

			try {
				Client.sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(clientButtonId)).build());
				System.out.println("Skill " + skillName + " clicked - sent ID: " + clientButtonId);
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	public static void handleTotalStatsClick() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClickTime < 500) return;
		lastClickTime = currentTime;

		try {
			int clientButtonId = calculateClientButtonId(94144);
			Client.sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(clientButtonId)).build());
			System.out.println("Total Stats clicked - sent button ID: " + clientButtonId);
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}

	private static int calculateClientButtonId(int serverButtonId) {
		int byte1 = serverButtonId / 1000;
		int byte2 = serverButtonId % 1000;

		if (byte1 > 255 || byte2 > 255) {
			return -1;
		}

		return (byte1 << 8) | byte2;
	}
}