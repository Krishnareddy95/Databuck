package com.databuck.bean;

import java.io.Serializable;

public class listGlobalThresholds implements Serializable{
	private int idGlobalThreshold;
	private int domainId;
	private String domainName;
	private String globalColumnName;
	private String description;
	private Double nullCountThreshold;
	private Double numericalThreshold;
	private Double stringstatThreshold;
	private Double dataDriftThreshold;
	private Double recordAnomalyThreshold;
	private Double lengthCheckThreshold;
	
	
	public Double getLengthCheckThreshold() {
		return lengthCheckThreshold;
	}
	public void setLengthCheckThreshold(Double lengthCheckThreshold) {
		this.lengthCheckThreshold = lengthCheckThreshold;
	}
	public int getIdGlobalThreshold() {
		return idGlobalThreshold;
	}
	public void setIdGlobalThreshold(int idGlobalThreshold) {
		this.idGlobalThreshold = idGlobalThreshold;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}	
	public String getGlobalColumnName() {
		return globalColumnName;
	}
	public void setGlobalColumnName(String globalColumnName) {
		this.globalColumnName = globalColumnName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getNullCountThreshold() {
		return nullCountThreshold;
	}
	public void setNullCountThreshold(Double nullCountThreshold) {
		this.nullCountThreshold = nullCountThreshold;
	}
	public Double getNumericalThreshold() {
		return numericalThreshold;
	}
	public void setNumericalThreshold(Double numericalThreshold) {
		this.numericalThreshold = numericalThreshold;
	}
	public Double getStringstatThreshold() {
		return stringstatThreshold;
	}
	public void setStringstatThreshold(Double stringstatThreshold) {
		this.stringstatThreshold = stringstatThreshold;
	}
	/**
	 * @return the dataDriftThreshold
	 */
	public Double getDataDriftThreshold() {
		return dataDriftThreshold;
	}
	/**
	 * @param dataDriftThreshold the dataDriftThreshold to set
	 */
	public void setDataDriftThreshold(Double dataDriftThreshold) {
		this.dataDriftThreshold = dataDriftThreshold;
	}
	/**
	 * @return the recordAnomalyThreshold
	 */
	public Double getRecordAnomalyThreshold() {
		return recordAnomalyThreshold;
	}
	/**
	 * @param recordAnomalyThreshold the recordAnomalyThreshold to set
	 */
	public void setRecordAnomalyThreshold(Double recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
	}

}
