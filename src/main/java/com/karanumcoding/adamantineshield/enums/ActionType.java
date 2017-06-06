package com.karanumcoding.adamantineshield.enums;

public enum ActionType {
	PLACE,
	DESTROY,
	MOB_PLACE,
	MOB_DESTROY,
	FLOW,
	CONTAINER_ADD,
	CONTAINER_REMOVE;
	
	public final static ActionType[] valueCache = values();
}
