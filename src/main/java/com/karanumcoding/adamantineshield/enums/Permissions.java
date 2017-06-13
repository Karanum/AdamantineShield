package com.karanumcoding.adamantineshield.enums;

public enum Permissions {
	ROLLBACK("use.rollback.base"),
	UNDO("use.rollback.undo"),
	REDO("use.rollback.redo"),
	LOOKUP("use.lookup.base"),
	RELOAD("admin.reload"),
	PURGE("admin.purge"),
	FILTER("use.lookup.filter"),
	TARGET_GLOBAL("target.global");
	
	
	private String permission;
	
	private Permissions(String permission) {
		this.permission = permission;
	}
	
	public String get() {
		return "adamantineshield." + permission;
	}
}
