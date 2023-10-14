package com.databuck.dto;

import java.util.Map;

public class ValidationListRequest {

	private Integer domainId;
	private Integer projectId;
	private Integer pageNo;
	private Integer pageSize;
	private String fromDate;
	private String toDate;
	private String sort;
	private String globalSearchOption;
	private Boolean countRequired;
	private Map<String, Object> filterCondtionMap;
	private String[] menuFilter; 

	public Integer getDomainId() {
		return domainId;
	}

	public String[] getMenuFilter() {
		return menuFilter;
	}

	public void setMenuFilter(String[] menuFilter) {
		this.menuFilter = menuFilter;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
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

	public String getGlobalSearchOption() {
		return globalSearchOption;
	}

	public void setGlobalSearchOption(String globalSearchOption) {
		this.globalSearchOption = globalSearchOption;
	}

	public Map<String, Object> getFilterCondtionMap() {
		return filterCondtionMap;
	}

	public void setFilterCondtionMap(Map<String, Object> filterCondtionMap) {
		this.filterCondtionMap = filterCondtionMap;
	}

}
