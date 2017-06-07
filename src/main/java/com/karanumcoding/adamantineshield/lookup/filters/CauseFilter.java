package com.karanumcoding.adamantineshield.lookup.filters;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class CauseFilter implements FilterBase {

	private Set<String> causes;
	
	public CauseFilter() {
		causes = Sets.newHashSet();
	}
	
	public void addCause(String cause) {
		causes.add(cause);
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return causes.contains(line.getNamedCause());
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		String tableName = lookupType.getTable();
		Iterator<String> iter = causes.iterator();
		String result = "(AS_Cause.cause = '" + iter.next() + "'";
		while (iter.hasNext()) {
			result += " OR AS_Cause.cause = '" + iter.next() + "'";
		}
		result += ") AND " + tableName + ".cause = AS_Cause.id";
		return result;
	}

}
