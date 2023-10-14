package com.databuck.bean;

import java.io.Serializable;
import java.util.Date;

public class ValidationView implements Serializable {

	private String ldname;
	private String lddescription;
	private String ldappType;
	private String lsdescription;
	private Date lscreatedAt;
	private int idApp;
	private int idData;
	private int idRightData;
	private int idDataBlend;
	private String lsdataLocation;
	private String lsdataSource;

	public int getIdApp() {
		return idApp;
	}

	public void setIdApp(int idApp) {
		this.idApp = idApp;
	}

	public String getLdname() {
		return ldname;
	}

	public void setLdname(String ldname) {
		this.ldname = ldname;
	}

	public String getLddescription() {
		return lddescription;
	}

	public void setLddescription(String lddescription) {
		this.lddescription = lddescription;
	}

	public String getLdappType() {
		return ldappType;
	}

	public void setLdappType(String ldappType) {
		this.ldappType = ldappType;
	}

	public int getIdData() {
		return idData;
	}

	public void setIdData(int idData) {
		this.idData = idData;
	}

	public int getIdRightData() {
		return idRightData;
	}

	public void setIdRightData(int idRightData) {
		this.idRightData = idRightData;
	}

	public int getIdDataBlend() {
		return idDataBlend;
	}

	public void setIdDataBlend(int idDataBlend) {
		this.idDataBlend = idDataBlend;
	}

	public Date getLscreatedAt() {
		return lscreatedAt;
	}

	public void setLscreatedAt(Date lscreatedAt) {
		this.lscreatedAt = lscreatedAt;
	}

	public String getLsdescription() {
		return lsdescription;
	}

	public void setLsdescription(String lsdescription) {
		this.lsdescription = lsdescription;
	}

	public String getLsdataLocation() {
		return lsdataLocation;
	}

	public void setLsdataLocation(String lsdataLocation) {
		this.lsdataLocation = lsdataLocation;
	}

	public String getLsdataSource() {
		return lsdataSource;
	}

	public void setLsdataSource(String lsdataSource) {
		this.lsdataSource = lsdataSource;
	}

	@Override
	public String toString() {
		return "ValidationView [idApp=" + idApp + ", ldname=" + ldname + ", lddescription=" + lddescription
				+ ", ldappType=" + ldappType + ", idData=" + idData + ", idRightData=" + idRightData + ", idDataBlend="
				+ idDataBlend + ", lscreatedAt=" + lscreatedAt + ", lsdescription=" + lsdescription
				+ ", lsdataLocation=" + lsdataLocation + ", lsdataSource=" + lsdataSource + "]";
	}

	public ValidationView() {

		this.idApp = idApp;
		this.ldname = ldname;
		this.lddescription = lddescription;
		this.ldappType = ldappType;
		this.idData = idData;
		this.idRightData = idRightData;
		this.idDataBlend = idDataBlend;
		this.lscreatedAt = lscreatedAt;
		this.lsdescription = lsdescription;
		this.lsdataLocation = lsdataLocation;
		this.lsdataSource = lsdataSource;
	}

}
