package com.databuck.bean;

public class RuleToSynonym {

	private int id;
	private int globalRuleId;
	private int synonymId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGlobalRuleId() {
		return globalRuleId;
	}
	public void setGlobalRuleId(int globalRuleId) {
		this.globalRuleId = globalRuleId;
	}
	public int getSynonymId() {
		return synonymId;
	}
	public void setSynonymId(int synonymId) {
		this.synonymId = synonymId;
	}
	@Override
	public String toString() {
		return "RuleToSynonym [id=" + id + ", globalRuleId=" + globalRuleId + ", synonymId=" + synonymId + "]";
	}
	
	
	
	
	
	
}
