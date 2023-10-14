package com.databuck.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "file_tracking_history")
public class FileTrackingHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private long idApp;
	@Temporal(TemporalType.DATE)
	private Date date;
	private int run;
	private int dayOfYear;
	private String month;
	private int dayOfMonth;
	private Integer dayOfWeek;
	private String hourOfDay;
	private long fileMonitorRuleId;
	private String requestId;
	private String bucketName;
	private String folderPath;
	private String fileName;
	private long fileSize;
	private Date fileArrivalDate;
	private String fileArrivalTime;
	private String status;
	private String statusMessage;
	private String fileFormat;
	private String zeroSizeFileCheck;
	private String recordLengthCheck;
	private String recordMaxLengthCheck;
	private String columnCountCheck;
	private String columnSequenceCheck;
	private Long idData;
	private String fileExecutionStatus;
	private String fileExecutionStatusMsg;
	
	
	
	public String getRecordMaxLengthCheck() {
		return recordMaxLengthCheck;
	}
	public void setRecordMaxLengthCheck(String recordMaxLengthCheck) {
		this.recordMaxLengthCheck = recordMaxLengthCheck;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public Date getFileArrivalDate() {
		return fileArrivalDate;
	}
	public void setFileArrivalDate(Date fileArrivalDate) {
		this.fileArrivalDate = fileArrivalDate;
	}
	public String getFileArrivalTime() {
		return fileArrivalTime;
	}
	public void setFileArrivalTime(String fileArrivalTime) {
		this.fileArrivalTime = fileArrivalTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public long getFileMonitorRuleId() {
		return fileMonitorRuleId;
	}
	public void setFileMonitorRuleId(long fileMonitorRuleId) {
		this.fileMonitorRuleId = fileMonitorRuleId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getRun() {
		return run;
	}
	public void setRun(int run) {
		this.run = run;
	}
	public int getDayOfYear() {
		return dayOfYear;
	}
	public void setDayOfYear(int dayOfYear) {
		this.dayOfYear = dayOfYear;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public int getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(String hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getZeroSizeFileCheck() {
		return zeroSizeFileCheck;
	}
	public void setZeroSizeFileCheck(String zeroSizeFileCheck) {
		this.zeroSizeFileCheck = zeroSizeFileCheck;
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
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public String getFileExecutionStatus() {
		return fileExecutionStatus;
	}
	public void setFileExecutionStatus(String fileExecutionStatus) {
		this.fileExecutionStatus = fileExecutionStatus;
	}
	public String getFileExecutionStatusMsg() {
		return fileExecutionStatusMsg;
	}
	public void setFileExecutionStatusMsg(String fileExecutionStatusMsg) {
		this.fileExecutionStatusMsg = fileExecutionStatusMsg;
	}
	
}
