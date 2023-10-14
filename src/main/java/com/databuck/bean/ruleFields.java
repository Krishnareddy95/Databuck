package com.databuck.bean;

public class ruleFields {
	private int rule_id;
		
		public int getRule_id() {
		return rule_id;
	}

	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}

		private int domain_id;
	private String domain_name;
	private int synonyms_Id;
	private String usercolumns;
	private String possiblenames;

	
	
	
	public int getDomain_id() {
		return domain_id;
	}

	public void setDomain_id(int domain_id) {
		this.domain_id = domain_id;
	}
	
	public String getColumn() {
		return usercolumns;
	}

	
	public int getSynonyms_Id() {
		return synonyms_Id;
	}

	public void setSynonyms_Id(int synonyms_Id) {
		this.synonyms_Id = synonyms_Id;
	}

	public String getUsercolumns() {
		return usercolumns;
	}

	public void setUsercolumns(String usercolumns) {
		this.usercolumns = usercolumns;
	}

	public String getPossiblenames() {
		return possiblenames;
	}

	public void setPossiblenames(String possiblenames) {
		this.possiblenames = possiblenames;
	}

	
	public String getDomain_name() {
		return domain_name;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	@Override
	public String toString() {
		return "ruleFields [rule_id=" + rule_id + ", domain_id=" + domain_id + ", domain_name=" + domain_name
				+ ", synonyms_Id=" + synonyms_Id + ", usercolumns=" + usercolumns + ", possiblenames=" + possiblenames
				+ "]";
	}

	

	
	

	

}
