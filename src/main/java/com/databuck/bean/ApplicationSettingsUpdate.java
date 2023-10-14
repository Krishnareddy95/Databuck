package com.databuck.bean;

import java.io.Serializable;

/*
 * Bean created for property file values updated through UI which will be written into new property file generated
 */
public class ApplicationSettingsUpdate implements Serializable {

	private String propKeys;
	private String propValues;
	private boolean propEncrypt;
	private String propReqRestart;
	private String propName;

	public String getPropKeys() {
		return propKeys;
	}

	public void setPropKeys(String propKeys) {
		this.propKeys = propKeys;
	}

	public String getPropValues() {
		return propValues;
	}

	public void setPropValues(String propValues) {
		this.propValues = propValues;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public boolean propEncrypt() {
		return propEncrypt;
	}

	public void setPropEncrypt(boolean propEncrypt) {
		this.propEncrypt = propEncrypt;
	}

	public String getPropReqRestart() {
		return propReqRestart;
	}

	public void setPropReqRestart(String propReqRestart) {
		this.propReqRestart = propReqRestart;
	}

}
