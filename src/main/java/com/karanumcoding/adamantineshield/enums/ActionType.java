package com.karanumcoding.adamantineshield.enums;

public enum ActionType {
	PLACE(true),
	DESTROY(false),
	MOB_PLACE(true),
	MOB_DESTROY(false),
	FLOW(true),
	CONTAINER_ADD(true),
	CONTAINER_REMOVE(false),
	ENTITY_CONTAINER_ADD(true),
	ENTITY_CONTAINER_REMOVE(false);
	
	private boolean isAddition;
	
	private ActionType(boolean isAddition) {
		this.isAddition = isAddition;
	}
	
	public boolean isAddition() {
		return isAddition;
	}
	
	public final static ActionType[] valueCache = values();
}
