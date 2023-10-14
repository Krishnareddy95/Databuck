package com.databuck.bean;

import java.io.Serializable;

public class SynonymLibrary implements Serializable{
	private int synonymsId;
	private int domainId;
	private String domainName;
	private String tableColumn;
	private String possibleNames;

	public int getSynonymsId() {
		return synonymsId;
	}

	public void setSynonymsId(int synonymsId) {
		this.synonymsId = synonymsId;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getTableColumn() {
		return tableColumn;
	}

	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}

	public String getPossibleNames() {
		return possibleNames;
	}

	public void setPossibleNames(String possibleNames) {
		this.possibleNames = possibleNames;
	}

}
