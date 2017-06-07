package com.karanumcoding.adamantineshield.lookup;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.entity.living.player.Player;

import com.google.common.collect.Maps;
import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.filters.ActionFilter;
import com.karanumcoding.adamantineshield.lookup.filters.FilterBase;
import com.karanumcoding.adamantineshield.lookup.filters.PositionFilter;

public class FilterSet {

	private Map<Class<? extends FilterBase>, FilterBase> filters;
	
	public FilterSet(AdamantineShield plugin, Player p) {
		filters = Maps.newHashMap();
		filters.put(PositionFilter.class, new PositionFilter(p.getLocation().getBlockPosition(), 
				plugin.getConfig().getInt("lookup", "default-radius")));
	}
	
	public <T extends FilterBase> Optional<FilterBase> getFilter(Class<T> filter) {
		if (filters.containsKey(filter))
			return Optional.of(filters.get(filter));
		return Optional.empty();
	}
	
	public <T extends FilterBase> void addFilter(T filter) {
		filters.put(filter.getClass(), filter);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FilterBase> T getOrCreate(T alt) {
		if (filters.containsKey(alt.getClass()))
			return (T) filters.get(alt.getClass());
		filters.put(alt.getClass(), alt);
		return alt;
	}
	
	public LookupType getLookupType() {
		if (!filters.containsKey(ActionFilter.class))
			return LookupType.BLOCK_LOOKUP;
		ActionFilter filter = (ActionFilter) filters.get(ActionFilter.class);
		if (filter.isItemLookup())
			return LookupType.ITEM_LOOKUP;
		return LookupType.BLOCK_LOOKUP;
	}
	
	public List<LookupLine> apply(List<LookupLine> lines) {
		return lines.stream().filter(item -> {
			Iterator<FilterBase> iter = filters.values().iterator();
			while (iter.hasNext()) {
				if (!iter.next().matches(item))
					return false;
			}
			return true;
		}).collect(Collectors.toList());
	}
	
	public String getQueryConditions(Player p) {
		if (!filters.containsKey(ActionFilter.class)) {
			filters.put(ActionFilter.class, new ActionFilter());
		}
		
		LookupType lookupType = LookupType.BLOCK_LOOKUP;
		if (((ActionFilter) filters.get(ActionFilter.class)).isItemLookup())
			lookupType = LookupType.ITEM_LOOKUP;
		
		Iterator<FilterBase> iter = filters.values().iterator();
		String result = iter.next().getQueryCondition(lookupType);
		while (iter.hasNext()) {
			result += " AND " + iter.next().getQueryCondition(lookupType);
		}
		return result;
	}
	
}
