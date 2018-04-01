package com.karanumcoding.adamantineshield.util;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

public final class PlayerUtils {
	
	private static UserStorageService service = null;
	
	private PlayerUtils() {}
	
	public static String getName(UUID id) {				
		Optional<Player> p = Sponge.getServer().getPlayer(id);
		if (p.isPresent())
			return p.get().getName();
		
		if (service == null)
			service = Sponge.getServiceManager().provide(UserStorageService.class).get();
		
		Optional<User> u = service.get(id);
		if (u.isPresent())
			return u.get().getName();
		return null;
	}
	
	public static UUID getUuidFromName(String name) {
		Optional<Player> p = Sponge.getServer().getPlayer(name);
		if (p.isPresent())
			return p.get().getUniqueId();
		
		if (service == null)
			service = Sponge.getServiceManager().provide(UserStorageService.class).get();
		
		Optional<User> u = service.get(name);
		if (u.isPresent())
			return u.get().getUniqueId();
		return null;
	}
	
}
