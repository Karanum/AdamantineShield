package com.karanumcoding.adamantineshield.lookup.filters;

import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public interface FilterBase {
	
	public boolean matches(LookupLine line);
	public String getQueryCondition(LookupType lookupType);
	
}
