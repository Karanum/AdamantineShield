package com.karanumcoding.adamantineshield.lookup.filters;

import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class RolledBackFilter implements FilterBase {

	private boolean rolledBack;
	
	public RolledBackFilter(boolean rolledBack) {
		this.rolledBack = rolledBack;
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return line.getRolledBack() == rolledBack;
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		if (lookupType == LookupType.CHAT_LOOKUP)
			return "";
		
		return "rolled_back = " + (rolledBack ? "1" : "0");
	}

}
