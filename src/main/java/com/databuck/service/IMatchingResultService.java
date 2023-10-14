package com.databuck.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.PrimaryMatchingSummary;


public interface IMatchingResultService {
	public Map<Long,String> getMatchingResultTable();
	public SqlRowSet getDataMatchingResultsTableNames(Long appId);
	public Object[] getDataFromResultTable(String tableName,Long appId);
	public Map<String,String> getDataFromDataMatchingSummary(String tableName);
	public SqlRowSet getThresholdFromListApplication(Long idApp);
	public List<DataMatchingSummary>  getDataFromDataMatchingSummaryGroupByDate(String tableName);
	public SqlRowSet getUnmatchedGroupbyTableData(String string);
	public String getAppNameFromListApplication(Long idApp);
	SqlRowSet getDataForPrimary(String tableName, Long appId);
	public Object[] getZeroStatusAbdCount(String tableName,Long appId);
	public Map<Long, String> getRollDataMatchingResultTable();
	public SqlRowSet getRollDataMatchingResultsTableNames(Long appId);
	public SqlRowSet getRollDataSummary(String tableName, long rollTargetSchemaId);
	public long getRollDataSummaryCount(String tableName, long rollTargetSchemaId);
	public SqlRowSet downloadRollDataSummary(String tableName, long rollTargetSchemaId);
	
	// for primary key matching 
		public Map<Long, String> getPrimaryKeyMatchingResultTable();
		public SqlRowSet getPrimaryKeyMatchingResultsTableNames(Long appId);
		public List<PrimaryMatchingSummary> getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(String tableName);
		public Object[] getZeroStatusAbdCountForPrimaryKeyMatching(String tableName, Long appId);
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummary(String tableName);
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate(String tableName);
		public List<PrimaryMatchingSummary> getLeftGraphForPrimaryKeyMatching(String tableName);
		public List<PrimaryMatchingSummary> getUnmatchedGraphForPrimaryKeyMatching(String tableName);
		public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingDashboard();
		
		public String getPrimaryKeyMatchingResultTableColumns(Long nAppId);
}