package com.karanumcoding.adamantineshield.enums;

public enum LookupType {
	BLOCK_LOOKUP("AS_Block", "AS_Id.value"),
	ITEM_LOOKUP("AS_Container", "AS_Id.value");
	
	private String table;
	private String column;
	
	private LookupType(String table, String column) {
		this.table = table;
		this.column = column;
	}
	
	public String getTable() {
		return table;
	}
	
	public String getRelevantColumn() {
		return column;
	}
	
}
