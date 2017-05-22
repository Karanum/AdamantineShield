package com.karanumcoding.adamantineshield.lookup;

import java.util.Map;

import org.spongepowered.api.entity.living.player.Player;

import com.google.common.collect.Maps;

public class LookupResultManager {

	private static LookupResultManager instance = new LookupResultManager();
	public static LookupResultManager instance() {
		return instance;
	}
	
	private Map<Player, LookupResult> lookupMap;
	
	private LookupResultManager() {
		lookupMap = Maps.newHashMap();
	}
	
	public void setLookupResult(Player p, LookupResult res) {
		lookupMap.put(p, res);
	}
	
	public void getLookupResult(Player p) {
		lookupMap.get(p);
	}
	
	public void clearLookupResult(Player p) {
		lookupMap.remove(p);
	}
	
}
