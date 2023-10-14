package com.databuck.aver.bean;

public class TrendCheckMicrosegmentReport {
	private String date;
	private double sumOfNumstat;
	private double upperLimit;
	private double lowerLimit;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getSumOfNumstat() {
		return sumOfNumstat;
	}

	public void setSumOfNumstat(double sumOfNumstat) {
		this.sumOfNumstat = sumOfNumstat;
	}

}
