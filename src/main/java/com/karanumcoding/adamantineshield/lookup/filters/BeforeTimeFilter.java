package com.karanumcoding.adamantineshield.lookup.filters;

import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class BeforeTimeFilter implements FilterBase {

	private long timestamp;
	
	public BeforeTimeFilter(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return line.getTime() < timestamp;
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		return "time < " + timestamp;
	}

}
