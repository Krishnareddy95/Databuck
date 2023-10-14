package com.databuck.bean;

public class DerivedTemplateValidateQuery {

	private String queryString;
	private String isJoinCondition;
	private long template1IdData;
	private long idDataSchema1;
	private long template2IdData;
	private long idDataSchema2;

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getIsJoinCondition() {
		return isJoinCondition;
	}

	public void setIsJoinCondition(String isJoinCondition) {
		this.isJoinCondition = isJoinCondition;
	}

	public long getTemplate1IdData() {
		return template1IdData;
	}

	public void setTemplate1IdData(long template1IdData) {
		this.template1IdData = template1IdData;
	}

	public long getIdDataSchema1() {
		return idDataSchema1;
	}

	public void setIdDataSchema1(long idDataSchema1) {
		this.idDataSchema1 = idDataSchema1;
	}

	public long getTemplate2IdData() {
		return template2IdData;
	}

	public void setTemplate2IdData(long template2IdData) {
		this.template2IdData = template2IdData;
	}

	public long getIdDataSchema2() {
		return idDataSchema2;
	}

	public void setIdDataSchema2(long idDataSchema2) {
		this.idDataSchema2 = idDataSchema2;
	}

	@Override
	public String toString() {
		return "DerivedTemplateValidateQuery{" +
				"queryString='" + queryString + '\'' +
				", isJoinCondition='" + isJoinCondition + '\'' +
				", template1IdData=" + template1IdData +
				", idDataSchema1=" + idDataSchema1 +
				", template2IdData=" + template2IdData +
				", idDataSchema2=" + idDataSchema2 +
				'}';
	}
}
