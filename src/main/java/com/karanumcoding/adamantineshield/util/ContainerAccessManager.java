package com.karanumcoding.adamantineshield.util;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;

import com.google.common.collect.Maps;

public class ContainerAccessManager {
	
	private class AccessData {
		public int tick;
		public TileEntityCarrier entity;
		public boolean active;
	}
	
	private Map<UUID, AccessData> accessMap;
	
	public ContainerAccessManager() {
		accessMap = Maps.newHashMap();
	}
	
	public void addPlayer(UUID id, int tick, TileEntityCarrier entity) {
		AccessData data = new AccessData();
		data.tick = tick;
		data.entity = entity;
		accessMap.put(id, data);
	}
	
	public void removePlayer(UUID id) {
		accessMap.remove(id);
	}
	
	public void setActive(UUID id) {
		if (!accessMap.containsKey(id))
			return;
		accessMap.get(id).active = true;
	}
	
	public boolean isActive(UUID id) {
		if (!accessMap.containsKey(id))
			return false;
		return accessMap.get(id).active;
	}
	
	public boolean matchesTick(UUID id, int tick) {
		if (!accessMap.containsKey(id))
			return false;
		return (accessMap.get(id).tick == tick);
	}
	
	public Optional<TileEntityCarrier> getEntity(UUID id) {
		if (!accessMap.containsKey(id))
			return Optional.empty();
		return Optional.of(accessMap.get(id).entity);
	}
	
}
