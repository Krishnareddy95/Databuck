package com.databuck.bean;

public class DbkFMSummaryDetails {
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
    private Integer expected_minute;
    private Integer actual_file_count;
    private Integer expected_file_count;
    private String status;

    public String getTable_or_subfolder_name() {
        return table_or_subfolder_name;
    }

    public void setTable_or_subfolder_name(String table_or_subfolder_name) {
        this.table_or_subfolder_name = table_or_subfolder_name;
    }

    public Long getConnection_id() {return connection_id;}

    public Long getValidation_id() {return validation_id;}

    public String getSchema_name() {return schema_name;}

    public String getFile_indicator() {return file_indicator;}

    public String getDayOfWeek() {return dayOfWeek;}

    public String getLoad_date() {return load_date;}

    public Integer getLoaded_hour() {return loaded_hour;}

    public Integer getExpected_minute() {  return expected_minute;    }

    public String getConnectionType() {return connectionType;}

    public void setConnectionType(String connectionType) {this.connectionType = connectionType;}

    public Integer getActual_file_count() {return actual_file_count;}

    public Integer getExpected_file_count() {return expected_file_count;}

    public String getStatus() {return status;}

    public void setConnection_id(Long connection_id) {this.connection_id = connection_id;}

    public void setValidation_id(Long validation_id) {this.validation_id = validation_id;}

    public void setSchema_name(String schema_name) {this.schema_name = schema_name;}

    public void setFile_indicator(String file_indicator) {this.file_indicator = file_indicator;}

    public void setDayOfWeek(String dayOfWeek) {this.dayOfWeek = dayOfWeek;}

    public void setLoad_date(String load_date) {this.load_date = load_date;}

    public void setLoaded_hour(Integer loaded_hour) {this.loaded_hour = loaded_hour;}

    public void setExpected_minute(Integer expected_minute) {        this.expected_minute = expected_minute;    }

    public void setActual_file_count(Integer actual_file_count) {this.actual_file_count = actual_file_count;}

    public void setExpected_file_count(Integer expected_file_count) {this.expected_file_count = expected_file_count;}

    public void setStatus(String status) {this.status = status;}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
    
}


