package com.databuck.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "file_monitor_rules")
public class FileMonitorRules {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String bucketName;
	private String folderPath;
	private String filePattern;
	private String frequency;
	private Integer dayOfCheck;
	private String timeOfCheck;
	private Integer fileCount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastProcessedDate;
	private Integer fileSizeThreshold;
	private int idApp;
	@Column(name = "idDataSchema", nullable = true)
	private Long idDataSchema;
	@Column(name = "partitionedFolders", nullable = false, columnDefinition = "varchar(10) default 'N'")
	private String partitionedFolders;
	@Column(name = "maxFolderDepth", nullable = false, columnDefinition = "int default 2")
	private int maxFolderDepth;

	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public int getIdApp() {
		return idApp;
	}

	public void setIdApp(int idApp) {
		this.idApp = idApp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public Integer getDayOfCheck() {
		return dayOfCheck;
	}

	public void setDayOfCheck(Integer dayOfCheck) {
		this.dayOfCheck = dayOfCheck;
	}

	public String getTimeOfCheck() {
		return timeOfCheck;
	}

	public void setTimeOfCheck(String timeOfCheck) {
		this.timeOfCheck = timeOfCheck;
	}

	public Integer getFileCount() {
		return fileCount;
	}

	public void setFileCount(Integer fileCount) {
		this.fileCount = fileCount;
	}

	public Date getLastProcessedDate() {
		return lastProcessedDate;
	}

	public void setLastProcessedDate(Date lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public FileMonitorRules() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileMonitorRules(String bucketName, String folderPath, String filePattern, String frequency,
			Integer dayOfCheck, String timeOfCheck, Integer fileCount, Date lastProcessedDate, int idApp,
			int fileSizeThreshold, String partitionedFolders, int maxFolderDepth) {
		super();

		this.bucketName = bucketName;
		this.folderPath = folderPath;
		this.filePattern = filePattern;
		this.frequency = frequency;
		this.dayOfCheck = dayOfCheck;
		this.timeOfCheck = timeOfCheck;
		this.fileCount = fileCount;
		this.lastProcessedDate = lastProcessedDate;
		this.idApp = idApp;
		this.fileSizeThreshold = fileSizeThreshold;
		this.partitionedFolders = partitionedFolders;
		this.maxFolderDepth = maxFolderDepth;
	}

	@Override
	public String toString() {
		return "FileMonitorRules [id=" + id + ", bucketName=" + bucketName + ", folderPath=" + folderPath
				+ ", filePattern=" + filePattern + ", frequency=" + frequency + ", dayOfCheck=" + dayOfCheck
				+ ", timeOfCheck=" + timeOfCheck + ", fileCount=" + fileCount + ", lastProcessedDate="
				+ lastProcessedDate + ", idApp=" + idApp + ", fileSizeThreshold=" + fileSizeThreshold
				+ ", partitionedFolders=" + partitionedFolders + ", maxFolderDepth=" + maxFolderDepth + "]";
	}

	public Integer getFileSizeThreshold() {
		return fileSizeThreshold;
	}

	public void setFileSizeThreshold(Integer fileSizeThreshold) {
		this.fileSizeThreshold = fileSizeThreshold;
	}

	public String getPartitionedFolders() {
		return partitionedFolders;
	}

	public void setPartitionedFolders(String partitionedFolders) {
		this.partitionedFolders = partitionedFolders;
	}

	public int getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public void setMaxFolderDepth(int maxFolderDepth) {
		this.maxFolderDepth = maxFolderDepth;
	}

}
