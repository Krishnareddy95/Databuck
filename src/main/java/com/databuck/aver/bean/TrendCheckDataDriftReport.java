package com.databuck.aver.bean;

import java.util.List;

public class TrendCheckDataDriftReport {
	private List<String> newUniqueValues;
	private List<String> missingUniqueValues;

	public List<String> getNewUniqueValues() {
		return newUniqueValues;
	}

	public void setNewUniqueValues(List<String> newUniqueValues) {
		this.newUniqueValues = newUniqueValues;
	}

	public List<String> getMissingUniqueValues() {
		return missingUniqueValues;
	}

	public void setMissingUniqueValues(List<String> missingUniqueValues) {
		this.missingUniqueValues = missingUniqueValues;
	}
}
