package com.databuck.dao;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.databuck.bean.DbkFMConnectionSummary;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMSummaryDetails;
import com.databuck.bean.ReportUIOverallDQIIndex;

public interface DBKFileMonitoringDao {
    public List<DbkFMSummaryDetails> getDBKFMSummaryDetails(Long idApp);

	public List<DbkFMFileArrivalDetails> getDBKFMFileArrivalDetails(Long idApp);

    public List<DbkFMSummaryDetails> getDBKFMSummaryDetailsForConnection(Long idApp,Long ConnectionId,String fromDate,String toDate);

    public List<DbkFMFileArrivalDetails> getDBKFMFileArrivalDetailsForTable(Long idApp, Long connectionId, String from_date, String to_date, String tableOrFileName);

	public List<DbkFMConnectionSummary> getFileMonitoringValidationsList(long domainId, long projectId);

	public JSONArray getfailedFileCount(String fromDate, String toDate, String validationList);
	
	public String getIdAppsForFMconnectiondetails(long domainId, long projectId);
	
	public JSONArray getAdditionalFileCount(String fromDate, String toDate, String validationList);
		
	public JSONArray getMissingDelayedFileCount(String fromDate, String toDate, String validationList);

}