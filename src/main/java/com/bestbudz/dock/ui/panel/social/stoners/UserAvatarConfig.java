package com.bestbudz.dock.ui.panel.social.stoners;

import com.bestbudz.dock.util.SpriteUtil;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;

public class UserAvatarConfig {

	private static final Map<String, String> userAvatars = new HashMap<>();
	private static final int AVATAR_SIZE = 16;

	static {

		userAvatars.put("Jaybane", "icons/potleaf.png");
		userAvatars.put("Bestbud", "icons/discord.png");
		userAvatars.put("VIPUser", "icons/vip_star.png");

	}

	public static ImageIcon getAvatarForUser(String username) {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}

		String cleanUsername = username.trim();
		String avatarPath = userAvatars.get(cleanUsername);

		if (avatarPath != null) {
			return SpriteUtil.loadIconScaled(avatarPath, AVATAR_SIZE);
		}

		return null;
	}

	public static boolean hasCustomAvatar(String username) {
		return username != null && userAvatars.containsKey(username.trim());
	}
}
