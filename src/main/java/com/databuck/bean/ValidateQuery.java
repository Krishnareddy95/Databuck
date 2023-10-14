package com.databuck.bean;

public class ValidateQuery {

	private long idDataSchema;
	private String tableName;
	private String isQueryEnabled;
	private String queryString;
	private String whereCondition;

	public long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIsQueryEnabled() {
		return isQueryEnabled;
	}

	public void setIsQueryEnabled(String isQueryEnabled) {
		this.isQueryEnabled = isQueryEnabled;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	@Override
	public String toString() {
		return "====> ValidateQuery{" +
				"idDataSchema=" + idDataSchema +
				", tableName='" + tableName + '\'' +
				", isQueryEnabled='" + isQueryEnabled + '\'' +
				", queryString='" + queryString + '\'' +
				", whereCondition='" + whereCondition + '\'' +
				'}';
	}
}
