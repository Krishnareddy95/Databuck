package com.databuck.bean;

import java.io.Serializable;

public class Dimension implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6665326375201426392L;
	private int idDimension;
	private String dimensionName;
	
	public int getIdDimension() {
		return idDimension;
	}
	public void setIdDimension(int idDimension) {
		this.idDimension = idDimension;
	}
	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	
	@Override
	public String toString() {
		return "Dimension [idDimension=" + idDimension + ", dimensionName=" + dimensionName + "]";
	}
	
	
	
}
