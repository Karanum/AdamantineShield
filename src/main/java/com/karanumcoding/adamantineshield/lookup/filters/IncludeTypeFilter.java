package com.karanumcoding.adamantineshield.lookup.filters;

import java.util.Iterator;
import java.util.Set;

import org.spongepowered.api.CatalogType;

import com.google.common.collect.Sets;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class IncludeTypeFilter implements FilterBase {

	private Set<CatalogType> include;
	
	public IncludeTypeFilter() {
		include = Sets.newHashSet();
	}
	
	public void addType(CatalogType type) {
		include.add(type);
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return include.contains(line.getTarget());
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		Iterator<CatalogType> iter = include.iterator();
		String result = "(AS_Id.value = '" + iter.next().getId() + "'";
		while (iter.hasNext()) {
			result += " OR AS_Id.value = '" + iter.next().getId() + "'";
		}
		result += ")";
		return result;
	}

}
