package com.databuck.filemonitoring;

import java.util.List;

public class FileMonitorExternalFile {

	private String filePattern;
	private String fieldSeparator;
	private String subFolderName;
	private String xsltFileName;
	
	public String getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	public String getFieldSeparator() {
		return fieldSeparator;
	}
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}
	public String getSubFolderName() {
		return subFolderName;
	}
	public void setSubFolderName(String subFolderName) {
		this.subFolderName = subFolderName;
	}
	public String getXsltFileName() {
		return xsltFileName;
	}
	public void setXsltFileName(String xsltFileName) {
		this.xsltFileName = xsltFileName;
	}
	
	
}
