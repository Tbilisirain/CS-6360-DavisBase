package com.magenta.persistance;

public class TableColumnSetting {
	
	private String columnName;
	private String dataType;
	private boolean isPrimaryKey;
	private boolean isNotNull;
	private boolean isUnique;
	
	public TableColumnSetting(String columnName, String dataType, boolean isPrimaryKey, boolean isNotNull,
			boolean isUnique) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.isPrimaryKey = isPrimaryKey;
		this.isNotNull = isNotNull;
		this.isUnique = isUnique;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	
}
