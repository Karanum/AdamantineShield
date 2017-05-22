package com.karanumcoding.adamantineshield;

public enum ActionType {
	PLACE("place"),
	DESTROY("destroy"),
	MOB_PLACE("mobplace"),
	MOB_DESTROY("mobdestroy"),
	FLOW("flow");
	
	
	private String type;
	
	private ActionType(String type) {
		this.type = type;
	}
	
	public static ActionType fromString(String type) {
		for (ActionType v : ActionType.values()) {
			if (v.toString().equals(type))
				return v;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
