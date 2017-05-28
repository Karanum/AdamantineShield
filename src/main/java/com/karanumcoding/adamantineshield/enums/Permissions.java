package com.karanumcoding.adamantineshield.enums;

public enum Permissions {
	ROLLBACK("use.rollback"),
	UNDO("use.rollback.undo"),
	REDO("use.rollback.redo"),
	LOOKUP("use.lookup"),
	RELOAD("admin.reload"),
	PURGE("admin.purge"),
	FILTER("use.lookup.filter");
	
	
	private String permission;
	
	private Permissions(String permission) {
		this.permission = permission;
	}
	
	public String get() {
		return "adamantineshield." + permission;
	}
}
