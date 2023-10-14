package com.databuck.filemonitoring;

public class FileCheckRules {

	private String fileName;
	private String filePath;
	private String fileFormat;
	private String zeroSizeFile;
	private String recordLengthCheck;
	private String recordMaxLengthCheck;
	private String columnCountCheck;
	private String columnSequenceCheck;
	private String isFileValid;
	
	

	public String getRecordMaxLengthCheck() {
		return recordMaxLengthCheck;
	}

	public void setRecordMaxLengthCheck(String recordMaxLengthCheck) {
		this.recordMaxLengthCheck = recordMaxLengthCheck;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getZeroSizeFile() {
		return zeroSizeFile;
	}

	public void setZeroSizeFile(String zeroSizeFile) {
		this.zeroSizeFile = zeroSizeFile;
	}

	public String getRecordLengthCheck() {
		return recordLengthCheck;
	}

	public void setRecordLengthCheck(String recordLengthCheck) {
		this.recordLengthCheck = recordLengthCheck;
	}

	public String getColumnCountCheck() {
		return columnCountCheck;
	}

	public void setColumnCountCheck(String columnCountCheck) {
		this.columnCountCheck = columnCountCheck;
	}

	public String getColumnSequenceCheck() {
		return columnSequenceCheck;
	}

	public void setColumnSequenceCheck(String columnSequenceCheck) {
		this.columnSequenceCheck = columnSequenceCheck;
	}

	public String getIsFileValid() {
		return isFileValid;
	}

	public void setIsFileValid(String isFileValid) {
		this.isFileValid = isFileValid;
	}

	@Override
	public String toString() {
		return "FileCheckRules [\n fileName=" + fileName + "\n filePath=" + filePath + "\n fileFormat=" + fileFormat
				+ "\n zeroSizeFile=" + zeroSizeFile + "\n recordLengthCheck=" + recordLengthCheck + "\n recordMaxLengthCheck=" +recordMaxLengthCheck 
				+ "\n columnCountCheck=" + columnCountCheck + "\n columnSequenceCheck=" + columnSequenceCheck
				+ "\n isFileValid=" + isFileValid + "\n]";
	}

}
