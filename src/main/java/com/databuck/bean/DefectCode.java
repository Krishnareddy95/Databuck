package com.databuck.bean;

import java.io.Serializable;

public class DefectCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	  
       private String defectCode;
       private String defectDescription;
       private int dimensionId;
       private String dimensionName;
	public String getDefectCode() {
		return defectCode;
	}
	public void setDefectCode(String defectCode) {
		this.defectCode = defectCode;
	}
	public String getDefectDescription() {
		return defectDescription;
	}
	public void setDefectDescription(String defectDescription) {
		this.defectDescription = defectDescription;
	}
	public int getDimensionId() {
		return dimensionId;
	}
	public void setDimensionId(int dimensionId) {
		this.dimensionId = dimensionId;
	}
	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	@Override
	public String toString() {
		return "DefectCode [defectCode=" + defectCode + ", defectDescription=" + defectDescription + ", dimensionId="
				+ dimensionId + ", dimensionName=" + dimensionName + "]";
	}
	

}
