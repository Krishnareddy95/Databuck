package com.databuck.bean;

import java.io.Serializable;

import com.databuck.econstants.DeltaType;

public class ListDataDefinitionDelta implements Serializable {

	private static final long serialVersionUID = 1L;

	private long idData;
	private DeltaType deltaType;
	private ListDataDefinition curListDataDefinition;
	private ListDataDefinition stgListDataDefinition;
	private long missingColId;

	public DeltaType getDeltaType() {
		return deltaType;
	}

	public void setDeltaType(DeltaType deltaType) {
		this.deltaType = deltaType;
	}

	public ListDataDefinition getCurListDataDefinition() {
		return curListDataDefinition;
	}

	public void setCurListDataDefinition(ListDataDefinition curListDataDefinition) {
		this.curListDataDefinition = curListDataDefinition;
	}

	public ListDataDefinition getStgListDataDefinition() {
		return stgListDataDefinition;
	}

	public void setStgListDataDefinition(ListDataDefinition stgListDataDefinition) {
		this.stgListDataDefinition = stgListDataDefinition;
	}

	public long getIdData() {
		return idData;
	}

	public void setIdData(long idData) {
		this.idData = idData;
	}

	public long getMissingColId() {
		return missingColId;
	}

	public void setMissingColId(long missingColId) {
		this.missingColId = missingColId;
	}

	@Override
	public String toString() {
		return "{\"idData\": " + idData + ",\"deltaType\":\"" + deltaType + "\", \"curListDataDefinition\":"
				+ curListDataDefinition + ", \"stgListDataDefinition\":" + stgListDataDefinition + ", \"missingColId\":"
				+ missingColId + "}";
	}

}