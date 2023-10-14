package com.databuck.bean;

public class ListDfTranRule {
	private Long idDFT;
	private Long idApp;
	private String dupRow;
	private String seqRow;
	private Long seqIDcol;
	private Double threshold;
	private String type;

	public Long getIdDFT() {
		return idDFT;
	}

	public void setIdDFT(Long idDFT) {
		this.idDFT = idDFT;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getDupRow() {
		return dupRow;
	}

	public void setDupRow(String dupRow) {
		this.dupRow = dupRow;
	}

	public String getSeqRow() {
		return seqRow;
	}

	public void setSeqRow(String seqRow) {
		this.seqRow = seqRow;
	}

	public Long getSeqIDcol() {
		return seqIDcol;
	}

	public void setSeqIDcol(Long seqIDcol) {
		this.seqIDcol = seqIDcol;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
