package com.databuck.bean;

import java.io.Serializable;

public class Idapp implements Serializable {

	private long idApp;
	
	public Idapp() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public Idapp(long idApp) {
		super();
		this.idApp = idApp;
	}
	

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}

	
	@Override
	public String toString() {
		return "" + idApp + "";
	}

	
	
}
