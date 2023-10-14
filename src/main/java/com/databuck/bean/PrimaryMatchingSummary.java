package com.databuck.bean;

public class PrimaryMatchingSummary {
	
	
/*	Run	int(11)
	Date	text
	LeftTotalCount	int(11)
	RightTotalCount	int(11)
	TotalMatchedCount	int(11)
	LeftMatchedCount	int(11)
	RightMatchedCount	int(11)
	UnMatchedCount	int(11)
	UnMatchedPercentage	decimal(17,2)
	UnMatchedStatus	text
	LeftOnlyCount	int(11)
	LeftOnlyPercentage	text
	LeftOnlyStatus	text
	RightOnlyCount	int(11)
	RightOnlyPercentage	text
	RightOnlyStatus	text
	LeftNullCount	int(11)
	RightNullCount	int(11)*/
	
	private Long id;
	
	private Long run;
	private String date;
	private Long leftTotalCount;
	private Long rightTotalCount;
	private Long totalMatchedCount;
	private Long leftMatchedCount;
	private Long rightMatchedCount;
	private Long UnMatchedCount;
	private double UnMatchedPercentage;
	private String UnMatchedStatus;
	private Long leftOnlyCount;
	private Double  leftOnlyPercentage;
	private String LeftOnlyStatus;
	private  Long rightOnlyCount;
	private Double rightOnlyPercentage;
	private String RightOnlyStatus;
	private Long leftNullCount;
	private Long rightNullCount;
//
//	private long unMatchedCount;
//
//	public void setUnMatchedCount(long unMatchedCount) {
//		this.unMatchedCount = unMatchedCount;
//	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getRun() {
		return run;
	}
	public void setRun(Long run) {
		this.run = run;
	}
	public Long getLeftTotalCount() {
		return leftTotalCount;
	}
	public void setLeftTotalCount(Long leftTotalCount) {
		this.leftTotalCount = leftTotalCount;
	}
	public Long getRightTotalCount() {
		return rightTotalCount;
	}
	public void setRightTotalCount(Long rightTotalCount) {
		this.rightTotalCount = rightTotalCount;
	}
	public Long getTotalMatchedCount() {
		return totalMatchedCount;
	}
	public void setTotalMatchedCount(Long totalMatchedCount) {
		this.totalMatchedCount = totalMatchedCount;
	}
	public Long getLeftMatchedCount() {
		return leftMatchedCount;
	}
	public void setLeftMatchedCount(Long leftMatchedCount) {
		this.leftMatchedCount = leftMatchedCount;
	}
	public Long getRightMatchedCount() {
		return rightMatchedCount;
	}
	public void setRightMatchedCount(Long rightMatchedCount) {
		this.rightMatchedCount = rightMatchedCount;
	}
	public Long getLeftOnlyCount() {
		return leftOnlyCount;
	}
	public void setLeftOnlyCount(Long leftOnlyCount) {
		this.leftOnlyCount = leftOnlyCount;
	}
	public Double getLeftOnlyPercentage() {
		return leftOnlyPercentage;
	}
	public void setLeftOnlyPercentage(Double leftOnlyPercentage) {
		this.leftOnlyPercentage = leftOnlyPercentage;
	}
	public Long getRightOnlyCount() {
		return rightOnlyCount;
	}
	public void setRightOnlyCount(Long rightOnlyCount) {
		this.rightOnlyCount = rightOnlyCount;
	}
	public Double getRightOnlyPercentage() {
		return rightOnlyPercentage;
	}
	public void setRightOnlyPercentage(Double rightOnlyPercentage) {
		this.rightOnlyPercentage = rightOnlyPercentage;
	}
	public Long getLeftNullCount() {
		return leftNullCount;
	}
	public void setLeftNullCount(Long leftNullCount) {
		this.leftNullCount = leftNullCount;
	}
	public Long getRightNullCount() {
		return rightNullCount;
	}
	public void setRightNullCount(Long rightNullCount) {
		this.rightNullCount = rightNullCount;
	}
//	public Long getUnMatchedCount() {
//		return UnMatchedCount;
//	}
//	public void setUnMatchedCount(Long unMatchedCount) {
//		UnMatchedCount = unMatchedCount;
//	}
	public double getUnMatchedPercentage() {
		return UnMatchedPercentage;
	}
	public void setUnMatchedPercentage(double unMatchedPercentage) {
		UnMatchedPercentage = unMatchedPercentage;
	}
	public String getUnMatchedStatus() {
		return UnMatchedStatus;
	}
	public void setUnMatchedStatus(String unMatchedStatus) {
		UnMatchedStatus = unMatchedStatus;
	}
	public String getLeftOnlyStatus() {
		return LeftOnlyStatus;
	}
	public void setLeftOnlyStatus(String leftOnlyStatus) {
		LeftOnlyStatus = leftOnlyStatus;
	}
	public String getRightOnlyStatus() {
		return RightOnlyStatus;
	}
	public void setRightOnlyStatus(String rightOnlyStatus) {
		RightOnlyStatus = rightOnlyStatus;
	}
	public Long getUnMatchedCount() {
		return UnMatchedCount;
	}
	public void setUnMatchedCount(Long unMatchedCount) {
		UnMatchedCount = unMatchedCount;
	}
	
	

	

}
