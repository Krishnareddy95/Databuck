package com.databuck.bean;

import java.io.Serializable;

public class GloabalRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int gloabal_rule_id;
	String rule_name;
	String rule_expression;
	String rule_Type;

	

	public String getRule_Type() {
		return rule_Type;
	}

	public void setRule_Type(String rule_Type) {
		this.rule_Type = rule_Type;
	}

	public int getGloabal_rule_id() {
		return gloabal_rule_id;
	}

	public void setGloabal_rule_id(int gloabal_rule_id) {
		this.gloabal_rule_id = gloabal_rule_id;
	}

	public String getRule_name() {
		return rule_name;
	}

	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}

	public String getRule_expression() {
		return rule_expression;
	}

	public void setRule_expression(String rule_expression) {
		this.rule_expression = rule_expression;
	}

	@Override
	public String toString() {
		return "GloabalRule [gloabal_rule_id=" + gloabal_rule_id + ", rule_name=" + rule_name + ", rule_expression="
				+ rule_expression + ", rule_Type=" + rule_Type + "]";
	}

	

	

}
