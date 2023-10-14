package com.databuck.bean;

import java.io.Serializable;

public class DeleteTempView implements Serializable {

	private String lename;
	private String ledescription;
	private String lsdescription;
	private int idDataBlend;
	private String lsdcname;
	private String lsdccolExpression;
	private int idData;
	private String ldbfdname;
	private String ldbfdfilteringExp;

	public String getLename() {
		return lename;
	}

	public void setLename(String lename) {
		this.lename = lename;
	}

	public String getLedescription() {
		return ledescription;
	}

	public void setLedescription(String ledescription) {
		this.ledescription = ledescription;
	}

	public String getLsdescription() {
		return lsdescription;
	}

	public void setLsdescription(String lsdescription) {
		this.lsdescription = lsdescription;
	}

	public int getIdDataBlend() {
		return idDataBlend;
	}

	public void setIdDataBlend(int idDataBlend) {
		this.idDataBlend = idDataBlend;
	}

	public String getLsdcname() {
		return lsdcname;
	}

	public void setLsdcname(String lsdcname) {
		this.lsdcname = lsdcname;
	}

	public String getLsdccolExpression() {
		return lsdccolExpression;
	}

	public void setLsdccolExpression(String lsdccolExpression) {
		this.lsdccolExpression = lsdccolExpression;
	}

	public int getIdData() {
		return idData;
	}

	public void setIdData(int idData) {
		this.idData = idData;
	}

	public String getLdbfdname() {
		return ldbfdname;
	}

	public void setLdbfdname(String ldbfdname) {
		this.ldbfdname = ldbfdname;
	}

	public String getLdbfdfilteringExp() {
		return ldbfdfilteringExp;
	}

	public void setLdbfdfilteringExp(String ldbfdfilteringExp) {
		this.ldbfdfilteringExp = ldbfdfilteringExp;
	}

}
