package com.karanumcoding.adamantineshield.util;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

public final class PlayerUtils {
	
	private PlayerUtils() {}
	
	public static String getName(UUID id) {		
		Optional<GameProfile> profile = Sponge.getServer().getGameProfileManager().getCache().getById(id);
		if (!profile.isPresent())
			return null;
		return profile.get().getName().get();
	}
	
}
