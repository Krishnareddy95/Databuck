package com.databuck.bean;


public class FileMonitorRequest {

	private String requestId;
	private String bucketName;
	private String folderPath;
	private String fileName;
	private String trackingDate;
	private String fileArrivalDateTime;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTrackingDate() {
		return trackingDate;
	}
	public void setTrackingDate(String trackingDate) {
		this.trackingDate = trackingDate;
	}
	public String getFileArrivalDateTime() {
		return fileArrivalDateTime;
	}
	public void setFileArrivalDateTime(String fileArrivalDateTime) {
		this.fileArrivalDateTime = fileArrivalDateTime;
	}

}
