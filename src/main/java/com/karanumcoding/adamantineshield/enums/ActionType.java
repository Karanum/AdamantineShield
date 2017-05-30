package com.karanumcoding.adamantineshield.enums;

public enum ActionType {
	PLACE,
	DESTROY,
	MOB_PLACE,
	MOB_DESTROY,
	FLOW;
	
	public final static ActionType[] valueCache = values();
}
