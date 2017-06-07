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
		String columnName = "";
		switch (lookupType) {
			case BLOCK_LOOKUP:
				columnName = "block";
				break;
			case ITEM_LOOKUP:
				columnName = "item";
				break;
		}
		
		Iterator<CatalogType> iter = include.iterator();
		String result = "(" + columnName + " = '" + iter.next().getId() + "'";
		while (iter.hasNext()) {
			result += " OR " + columnName + " = '" + iter.next().getId() + "'";
		}
		result += ")";
		return result;
	}

}
