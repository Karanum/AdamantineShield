package com.karanumcoding.adamantineshield.util;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public final class PlayerUtils {
	
	private PlayerUtils() {}
	
	public static String getName(UUID id) {
		Player p = Sponge.getServer().getPlayer(id).orElse(null);
		if (p == null)
			return null;
		return p.getName();
	}
	
}
