package com.databuck.bean;

public class AdhocRollRequest {
	private String salesForce;
	private String market;
	private String sellingPeriod;
	private String curDataMonth;
	private String otherDataMonth;
	private String curMonthFilePath;
	private String curMonthFileName;
	private String otherMonthFilePath;
	private String otherMonthfileName;

	public String getSalesForce() {
		return salesForce;
	}

	public void setSalesForce(String salesForce) {
		this.salesForce = salesForce;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getSellingPeriod() {
		return sellingPeriod;
	}

	public void setSellingPeriod(String sellingPeriod) {
		this.sellingPeriod = sellingPeriod;
	}

	public String getCurDataMonth() {
		return curDataMonth;
	}

	public void setCurDataMonth(String curDataMonth) {
		this.curDataMonth = curDataMonth;
	}

	public String getOtherDataMonth() {
		return otherDataMonth;
	}

	public void setOtherDataMonth(String otherDataMonth) {
		this.otherDataMonth = otherDataMonth;
	}

	public String getCurMonthFilePath() {
		return curMonthFilePath;
	}

	public void setCurMonthFilePath(String curMonthFilePath) {
		this.curMonthFilePath = curMonthFilePath;
	}

	public String getCurMonthFileName() {
		return curMonthFileName;
	}

	public void setCurMonthFileName(String curMonthFileName) {
		this.curMonthFileName = curMonthFileName;
	}

	public String getOtherMonthFilePath() {
		return otherMonthFilePath;
	}

	public void setOtherMonthFilePath(String otherMonthFilePath) {
		this.otherMonthFilePath = otherMonthFilePath;
	}

	public String getOtherMonthfileName() {
		return otherMonthfileName;
	}

	public void setOtherMonthfileName(String otherMonthfileName) {
		this.otherMonthfileName = otherMonthfileName;
	}

}
