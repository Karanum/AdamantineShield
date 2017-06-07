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
		String columnName = "";
		switch (lookupType) {
			case BLOCK_LOOKUP:
				columnName = "block";
			case ITEM_LOOKUP:
				columnName = "item";
		}
		
		Iterator<CatalogType> iter = exclude.iterator();
		String result = "NOT (" + columnName + " = '" + iter.next().getId() + "'";
		while (iter.hasNext()) {
			result += " OR " + columnName + " = '" + iter.next().getId() + "'";
		}
		result += ")";
		return result;
	}

}
