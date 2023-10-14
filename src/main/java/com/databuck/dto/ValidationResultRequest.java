package com.databuck.dto;

import java.util.Map;

public class ValidationResultRequest {

    private Integer idApp;
    private Integer pageNo;
    private Integer pageSize;
    private String fromDate;
    private String toDate;
    private String sort;
    private Boolean countRequired;
    private Boolean historicalData;
    private Map<String, Object> filterCondtionMap;
    private HistoricalColumnValues columnValueMap;

    public Boolean getHistoricalData() {
	return historicalData;
    }

    public void setHistoricalData(Boolean historicalData) {
	this.historicalData = historicalData;
    }

    public Integer getIdApp() {
	return idApp;
    }

    public void setIdApp(Integer idApp) {
	this.idApp = idApp;
    }

    public Integer getPageNo() {
	return pageNo;
    }

    public void setPageNo(Integer pageNo) {
	this.pageNo = pageNo;
    }

    public Integer getPageSize() {
	return pageSize;
    }

    public void setPageSize(Integer pageSize) {
	this.pageSize = pageSize;
    }

    public String getFromDate() {
	return fromDate;
    }

    public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
    }

    public String getToDate() {
	return toDate;
    }

    public void setToDate(String toDate) {
	this.toDate = toDate;
    }

    public String getSort() {
	return sort;
    }

    public void setSort(String sort) {
	this.sort = sort;
    }

    public Boolean getCountRequired() {
	return countRequired;
    }

    public void setCountRequired(Boolean countRequired) {
	this.countRequired = countRequired;
    }

    public Map<String, Object> getFilterCondtionMap() {
	return filterCondtionMap;
    }

    public void setFilterCondtionMap(Map<String, Object> filterCondtionMap) {
	this.filterCondtionMap = filterCondtionMap;
    }

    public HistoricalColumnValues getColumnValueMap() {
	return columnValueMap;
    }

    public void setColumnValueMap(HistoricalColumnValues columnValueMap) {
	this.columnValueMap = columnValueMap;
    }

}
