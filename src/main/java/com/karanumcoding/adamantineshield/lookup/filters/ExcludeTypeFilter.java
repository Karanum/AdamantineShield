package com.karanumcoding.adamantineshield.lookup.filters;

import java.util.Iterator;
import java.util.Set;

import org.spongepowered.api.CatalogType;

import com.google.common.collect.Sets;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class ExcludeTypeFilter implements FilterBase {

	private Set<CatalogType> exclude;
	
	public ExcludeTypeFilter() {
		exclude = Sets.newHashSet();
	}
	
	public void addType(CatalogType type) {
		exclude.add(type);
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return !exclude.contains(line.getTarget());
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		if (lookupType == LookupType.CHAT_LOOKUP)
			return "";
		
		Iterator<CatalogType> iter = exclude.iterator();
		String result = "NOT (AS_Id.value = '" + iter.next().getId() + "'";
		while (iter.hasNext()) {
			result += " OR AS_Id.value = '" + iter.next().getId() + "'";
		}
		result += ")";
		return result;
	}

}
