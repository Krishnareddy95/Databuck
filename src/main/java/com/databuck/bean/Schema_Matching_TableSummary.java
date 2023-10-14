package com.databuck.bean;

public class Schema_Matching_TableSummary {

	private Long id;
	private String date;
	private String tablesinschemaleftSource;
	private String extratablesinrightsource;
	private String missingtablesinrightsource;

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

	public String getTablesinschemaleftSource() {
		return tablesinschemaleftSource;
	}

	public void setTablesinschemaleftSource(String tablesinschemaleftSource) {
		this.tablesinschemaleftSource = tablesinschemaleftSource;
	}

	public String getExtratablesinrightsource() {
		return extratablesinrightsource;
	}

	public void setExtratablesinrightsource(String extratablesinrightsource) {
		this.extratablesinrightsource = extratablesinrightsource;
	}

	public String getMissingtablesinrightsource() {
		return missingtablesinrightsource;
	}

	public void setMissingtablesinrightsource(String missingtablesinrightsource) {
		this.missingtablesinrightsource = missingtablesinrightsource;
	}

}
