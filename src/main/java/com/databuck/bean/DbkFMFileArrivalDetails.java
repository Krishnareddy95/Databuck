package com.databuck.bean;

public class DbkFMFileArrivalDetails {

    private Long connection_id;
    private Long validation_id;
    private String connectionName;
    private String connectionType;
    private String schema_name;
    private String table_or_subfolder_name;
    private String file_indicator;
    private String dayOfWeek;
    private String load_date;
    private Integer loaded_hour;
    private Integer loaded_time;
    private Integer expected_hour;
    private Integer expected_time;
    private Integer record_count;
    private String record_count_check;
    private String column_metadata_check;
    private String file_validity_status;
    private String file_arrival_status;
    private String fileName;

    public String getTable_or_subfolder_name() {
        return table_or_subfolder_name;
    }

    public void setTable_or_subfolder_name(String table_or_subfolder_name) {
        this.table_or_subfolder_name = table_or_subfolder_name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getExpected_time() {
        return expected_time;
    }

    public void setExpected_time(Integer expected_time) {
        this.expected_time = expected_time;
    }

    public Long getConnection_id() {
        return connection_id;
    }

    public Long getValidation_id() {
        return validation_id;
    }

    public String getSchema_name() {
        return schema_name;
    }

    public String getFile_indicator() {
        return file_indicator;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getLoad_date() {
        return load_date;
    }

    public Integer getLoaded_hour() {
        return loaded_hour;
    }

    public Integer getLoaded_time() {
        return loaded_time;
    }

    public Integer getRecord_count() {
        return record_count;
    }

    public String getRecord_count_check() {
        return record_count_check;
    }

    public String getColumn_metadata_check() {
        return column_metadata_check;
    }

    public String getFile_validity_status() {
        return file_validity_status;
    }

    public String getFile_arrival_status() {
        return file_arrival_status;
    }

    public void setConnection_id(Long connection_id) {
        this.connection_id = connection_id;
    }

    public void setValidation_id(Long validation_id) {
        this.validation_id = validation_id;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    public void setFile_indicator(String file_indicator) {
        this.file_indicator = file_indicator;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setLoad_date(String load_date) {
        this.load_date = load_date;
    }

    public void setLoaded_hour(Integer loaded_hour) {
        this.loaded_hour = loaded_hour;
    }

    public void setLoaded_time(Integer loaded_time) {
        this.loaded_time = loaded_time;
    }

    public void setRecord_count(Integer record_count) {
        this.record_count = record_count;
    }

    public void setRecord_count_check(String record_count_check) {
        this.record_count_check = record_count_check;
    }

    public void setColumn_metadata_check(String column_metadata_check) {this.column_metadata_check = column_metadata_check;}

    public void setFile_validity_status(String file_validity_status) {this.file_validity_status = file_validity_status;}

    public void setFile_arrival_status(String file_arrival_status) {
        this.file_arrival_status = file_arrival_status;
    }

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public Integer getExpected_hour() {
        return expected_hour;
    }

    public void setExpected_hour(Integer expected_hour) {
        this.expected_hour = expected_hour;
    }
}