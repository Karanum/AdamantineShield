package com.karanumcoding.adamantineshield.enums;

public enum LookupType {
	BLOCK_LOOKUP("AS_Block", null),
	ITEM_LOOKUP("AS_Container", "count, slot"),
	CHAT_LOOKUP("AS_Chat", null);
	
	private String table;
	private String column;
	
	private LookupType(String table, String column) {
		this.table = table;
		this.column = column;
	}
	
	public String getTable() {
		return table;
	}
	
	public String getRelevantColumns() {
		return column;
	}
	
}
