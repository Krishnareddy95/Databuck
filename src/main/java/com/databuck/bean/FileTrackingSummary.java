package com.databuck.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "file_tracking_summary")
public class FileTrackingSummary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private FileMonitorRules fileMonitorRules;
	@Temporal(TemporalType.DATE)
	private Date date;
	private int run;
	private int dayOfYear;
	private String month;
	private int dayOfMonth;
	private Integer dayOfWeek;
	private String hourOfDay;
	private Integer fileCount;
	private String countStatus;
	private String fileSizeStatus;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTimeStamp;
	private long idApp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public FileMonitorRules getFileMonitorRules() {
		return fileMonitorRules;
	}

	public void setFileMonitorRules(FileMonitorRules fileMonitorRules) {
		this.fileMonitorRules = fileMonitorRules;
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

	public Integer getFileCount() {
		return fileCount;
	}

	public void setFileCount(Integer fileCount) {
		this.fileCount = fileCount;
	}

	public String getCountStatus() {
		return countStatus;
	}

	public void setCountStatus(String countStatus) {
		this.countStatus = countStatus;
	}

	public Date getLastUpdateTimeStamp() {
		return lastUpdateTimeStamp;
	}

	public void setLastUpdateTimeStamp(Date lastUpdateTimeStamp) {
		this.lastUpdateTimeStamp = lastUpdateTimeStamp;
	}

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
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

	public String getFileSizeStatus() {
		return fileSizeStatus;
	}

	public void setFileSizeStatus(String fileSizeStatus) {
		this.fileSizeStatus = fileSizeStatus;
	}

	@Override
	public String toString() {
		return "FileTrackingSummary [id=" + id + ", fileMonitorRules=" + fileMonitorRules + ", Date=" + date
				+ ", dayOfWeek=" + dayOfWeek + ", hourOfDay=" + hourOfDay + ", fileCount=" + fileCount
				+ ", countStatus=" + countStatus + ", lastUpdateTimeStamp=" + lastUpdateTimeStamp + "]";
	}

}
