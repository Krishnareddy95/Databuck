package com.databuck.dto;

public class HistoricalColumnValues {
    private Integer id;
    private String colName;
    private String dGroupVal;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getColName() {
	return colName;
    }

    public void setColName(String colName) {
	this.colName = colName;
    }

    public String getDGroupVal() {
	return dGroupVal;
    }

    public void setDGroupVal(String dGroupVal) {
	this.dGroupVal = dGroupVal;
    }

}
