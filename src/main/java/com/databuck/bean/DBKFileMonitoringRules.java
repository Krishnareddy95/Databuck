package com.databuck.bean;

public class DBKFileMonitoringRules {
    private Long id;
    private Long connectionId;
    private Long validationId;
    private String schemaName;
    private String tableName;
    private String fileIndicator;
    private String dayOfWeek;
    private Integer hourOfDay;
    private Integer expectedTime;
    private Integer expectedFileCount;
    private Integer startHour;
    private Integer endHour;
    private Integer frequency;
    private String ruleDeltaType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public long getValidationId() {
        return validationId;
    }

    public void setValidationId(long validationId) {
        this.validationId = validationId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFileIndicator() {
        return fileIndicator;
    }

    public void setFileIndicator(String fileIndicator) {
        this.fileIndicator = fileIndicator;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(Integer hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public Integer getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(Integer expectedTime) {
        this.expectedTime = expectedTime;
    }

    public Integer getExpectedFileCount() {
        return expectedFileCount;
    }

    public void setExpectedFileCount(Integer expectedFileCount) {
        this.expectedFileCount = expectedFileCount;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

	public String getRuleDeltaType() {
		return ruleDeltaType;
	}

	public void setRuleDeltaType(String ruleDeltaType) {
		this.ruleDeltaType = ruleDeltaType;
	}
    
}
