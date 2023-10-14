package com.databuck.dao;

import java.io.File;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.dbcp.BasicDataSource;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.bean.RollDataMatchingDashboard;

public interface MatchingResultDao {
	public Map<Long, String> getMatchingResultTable();

	public SqlRowSet getDataMatchingResultsTableNames(Long appId);

	public Object[] getDataFromResultTable(String tableName, Long appId);

	public Map<String, String> getDataFromDataMatchingSummary(String tableName);

	public SqlRowSet getThresholdFromListApplication(Long idApp);

	public List<DataMatchingSummary> getDataFromDataMatchingSummaryGroupByDate(String tableName);

	public List<DataMatchingSummary> getUnmatchedGraph(String tableName);

	public List<DataMatchingSummary> getLeftGraph(String tableName);
	public List<DataMatchingSummary> getRightGraph(String tableName);

	public SqlRowSet getUnmatchedGroupbyTableData(String tableName);

	public List<KeyMeasurementMatchingDashboard> getDashboardStatusForKeyMeasurementMatching(
			Map<Long, String> matchingResultTableData);

	public String getAppNameFromListApplication(Long idApp);

	// public List<DataQualityMasterDashboard>
	// getMasterDashboardForDataQuality(Map<Long, String> resultmasterdata,
	// List<ListApplications> listapplicationsData);
	public List<ListApplications> getdatafromlistapplications();
	public SqlRowSet getDataForPrimary(String tableName, Long appId);
	public void createMatchingCsv(String tableName);
	public List<KeyMeasurementMatchingDashboard> getmatchingDashboard();
	public Object[] getZeroStatusAbdCount(String tableName, Long appId);
	public List<RollDataMatchingDashboard> getRollmatchingDashboard();
	public Map<Long, String> getRollDataMatchingResultTable();
	public SqlRowSet getRollDataMatchingResultsTableNames(Long appId);
	public SqlRowSet getRollDataSummary(String tableName, long rollTargetSchemaId);
	public long getRollDataSummaryCount(String tableName, long rollTargetSchemaId);
	public SqlRowSet downloadRollDataSummary(String tableName, long rollTargetSchemaId);
	public RollDataMatchingDashboard getRollmatchingDashboardById(long idApp);
	
	// for Primary key matching
		public Map<Long, String> getPrimaryKeyMatchingResultTable();
		public SqlRowSet getPrimaryKeyMatchingResultsTableNames(Long appId);
		public List<PrimaryMatchingSummary> getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(String tableName);
		public Object[] getZeroStatusAbdCountForPrimaryKeyMatching(String tableName, Long appId);
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummary(String tableName);
		public List<PrimaryMatchingSummary> getLeftGraphForPrimaryKeyMatching(String tableName);
		public List<PrimaryMatchingSummary> getUnmatchedGraphForPrimaryKeyMatching(String tableName);
		public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingDashboard();
		public String getPrimaryKeyMatchingResultTableColumns(Long nAppId);
		public List<Map<String, Object>> getPrimaryKeyMatchingTableResult(String tableName, long appId);
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate(String tableName);

		public List<KeyMeasurementMatchingDashboard> getKeyMeasurementMatchingDashboardByProjectNDateFilter(
				String domainId, String projectId, String fromaDate, String toDate);
	public List<Map<String, Object>> getPrimaryKeyMatchingTableResult(String tableName, JSONArray ignoreColumnArr);

	public Map<String, String> getPrimaryKeyMatchingColumnsMetaData(Long nAppId);


}