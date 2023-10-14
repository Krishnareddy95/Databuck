package com.databuck.dao.impl;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.PrimaryKeyMatchingResultService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

@Repository
public class MatchingResultDaoImpl implements MatchingResultDao {
    @Autowired
    public JdbcTemplate jdbcTemplate1;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public IListDataSourceDAO listDataSourceDAO;
    @Autowired
    PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

    private static final Logger LOG = Logger.getLogger(MatchingResultDaoImpl.class);

    public Map<Long, String> getMatchingResultTable() {
	SqlRowSet queryForRowSet = null;
	Map<Long, String> map = new LinkedHashMap<Long, String>();
	try {
	    // select distinct rm.appID , rm.AppName from result_master_table rm ,
	    // data_matching_dashboard dm where rm.AppType != 'PKM' and rm.appID=dm.idapp;

	    queryForRowSet = jdbcTemplate1.queryForRowSet(
		    "select distinct  appID,AppName from result_master_table where AppType='DM' ORDER BY appID DESC");
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	    return map;
	} catch (Exception e) {
	    queryForRowSet = jdbcTemplate1
		    .queryForRowSet("select distinct  appID,AppName from result_master_table where AppType='DM'");
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	}
	return map;
    }

    public Map<Long, String> getRollDataMatchingResultTable() {
	SqlRowSet queryForRowSet = null;
	Map<Long, String> map = new LinkedHashMap<Long, String>();
	try {
	    queryForRowSet = jdbcTemplate1.queryForRowSet(
		    "select distinct  appID,AppName from result_master_table where AppType='RDM' ORDER BY appID DESC");
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	    return map;
	} catch (Exception e) {
	    queryForRowSet = jdbcTemplate1
		    .queryForRowSet("select distinct  appID,AppName from result_master_table where AppType='RDM'");
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	}
	return map;
    }

    public SqlRowSet getDataMatchingResultsTableNames(Long appId) {
	try {
	    return jdbcTemplate1.queryForRowSet(
		    "select * from result_master_table where appID=? and (AppType='DM' OR AppType='DMG')", appId);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public SqlRowSet getRollDataMatchingResultsTableNames(Long appId) {
	try {
	    return jdbcTemplate1.queryForRowSet("select * from result_master_table where appID=? and AppType='RDM'",
		    appId);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public Object[] getDataFromResultTable(String tableName, Long appId) {
	try {
	    /*
	     * SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet("select * from " +
	     * tableName + " where Run = (SELECT MAX(Run) from " + tableName + ")");
	     */
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet("select * from " + tableName);
	    queryForRowSet.last();
	    Long count = (long) queryForRowSet.getRow();
	    queryForRowSet.beforeFirst();
	    return new Object[] { queryForRowSet, count };
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public Object[] getZeroStatusAbdCount(String tableName, Long appId) {
	try {
	    String sql = "SELECT * FROM " + tableName + " where Run = " + "(select max(Run) from " + tableName
		    + " where Date = (" + "select max(Date) from " + tableName + "))" + " and Date = "
		    + "(select max(Date) from " + tableName + ")";

	    LOG.debug("getZeroStatusAbdCount =>" + sql);
	    SqlRowSet rs1 = jdbcTemplate1.queryForRowSet(sql);
	    String leftStatus = "";
	    String rightStatus = "";
	    BigInteger source1TotalRecords = BigInteger.valueOf(0L);
	    BigInteger source2TotalRecords = BigInteger.valueOf(0L);
	    while (rs1.next()) {
		leftStatus = rs1.getString(11);
		rightStatus = rs1.getString(14);
		source1TotalRecords = BigInteger.valueOf(rs1.getLong(15));
		source2TotalRecords = BigInteger.valueOf(rs1.getLong(16));

		/*
		 * source1TotalRecords = BigInteger.valueOf(300L); source2TotalRecords =
		 * BigInteger.valueOf(100L);
		 */
	    }

	    return new Object[] { leftStatus, rightStatus, source1TotalRecords, source2TotalRecords };
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public SqlRowSet getDataForPrimary(String tableName, Long appId) {
	String sql = "select * from " + tableName;
	sql += " where Date=(select max(date) from " + tableName + " ) " + "And Run=(select max(run) from " + tableName
		+ " where date=" + "(select max(date) from " + tableName + ")) ";
	LOG.debug("getDataForPrimary : " + sql);
	try {
	    return jdbcTemplate1.queryForRowSet(sql);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public Map<String, String> getDataFromDataMatchingSummary(String tableName) {
	try {
	    Map<String, String> dmSummaryMap = new IdentityHashMap<String, String>();
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(
		    "select * from " + tableName + " where id=(select max(id) from  " + tableName + ")");

	    DecimalFormat numberFormat = new DecimalFormat("#0.00");
	    while (queryForRowSet.next()) {
		dmSummaryMap.put("totalRecordsInSource1", queryForRowSet.getString("totalRecordsInSource1"));
		dmSummaryMap.put("totalRecordsInSource2", queryForRowSet.getString("totalRecordsInSource2"));
		dmSummaryMap.put("unmatchedRecords", queryForRowSet.getString("unmatchedRecords"));
		dmSummaryMap.put("source1OnlyRecords", queryForRowSet.getString("source1OnlyRecords"));
		dmSummaryMap.put("source2OnlyRecords", queryForRowSet.getString("source2OnlyRecords"));
		// numberFormat.format(queryForRowSet.getString("source2OnlyPercentage"));
		dmSummaryMap.put("source1OnlyPercentage",
			numberFormat.format(queryForRowSet.getDouble("source1OnlyPercentage")));
		dmSummaryMap.put("source2OnlyPercentage",
			numberFormat.format(queryForRowSet.getDouble("source2OnlyPercentage")));
		dmSummaryMap.put("unmatchedPercentage",
			numberFormat.format(queryForRowSet.getDouble("unmatchedPercentage")));
		dmSummaryMap.put("unmatchedStatus", queryForRowSet.getString("unmatchedStatus"));
	    }
	    return dmSummaryMap;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public SqlRowSet getThresholdFromListApplication(Long idApp) {
	try {
	    String sql = "select matchingThreshold,fileNameValidation, recordCountAnomalyThreshold from listApplications where idApp=?";
	    return jdbcTemplate.queryForRowSet(sql, idApp);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<DataMatchingSummary> getDataFromDataMatchingSummaryGroupByDate(String tableName) {
	List<DataMatchingSummary> al = new ArrayList<>();
	try {
	    DecimalFormat numberFormat = new DecimalFormat("#.00");
	    RowMapper<DataMatchingSummary> rowMapper = (resultSet, i) -> {
		DataMatchingSummary datamatchingSummaryObj = new DataMatchingSummary();
		datamatchingSummaryObj.setId(resultSet.getInt("id"));
		datamatchingSummaryObj.setDate(resultSet.getString("Date"));
		datamatchingSummaryObj.setRun(resultSet.getLong("Run"));
		datamatchingSummaryObj.setTotalRecordsInSource1(resultSet.getLong("totalRecordsInSource1"));
		datamatchingSummaryObj.setTotalRecordsInSource2(resultSet.getLong("totalRecordsInSource2"));
		datamatchingSummaryObj.setUnmatchedRecords(resultSet.getLong("unmatchedRecords"));
		datamatchingSummaryObj.setSource1OnlyRecords(resultSet.getLong("source1OnlyRecords"));
		datamatchingSummaryObj.setStatus(resultSet.getString("unmatchedStatus").toLowerCase());
		datamatchingSummaryObj.setSoure1OnlyPercenage(
			Double.parseDouble(numberFormat.format(resultSet.getDouble("source1OnlyPercentage"))));
		datamatchingSummaryObj.setSource2OnlyRecods(resultSet.getLong("source2OnlyRecords"));
		datamatchingSummaryObj.setSoure2OnlyPercenage(
			Double.parseDouble(numberFormat.format(resultSet.getDouble("source2OnlyPercentage"))));
		datamatchingSummaryObj.setSource1OnlyStatus(resultSet.getString("source1OnlyStatus").toLowerCase());
		datamatchingSummaryObj.setSource2OnlyStatus(resultSet.getString("source2OnlyStatus").toLowerCase());
		datamatchingSummaryObj.setRcDifference(Math
			.abs(resultSet.getLong("totalRecordsInSource1") - resultSet.getLong("totalRecordsInSource2")));
		return datamatchingSummaryObj;
	    };
	    String sql = "select * from " + tableName + "  ORDER BY id DESC";
	    return jdbcTemplate1.query(sql, rowMapper);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return al;
    }

    public List<DataMatchingSummary> getUnmatchedGraph(String tableName) {
	try {
	    String Query = "select  Date,Run,unmatchedStatus from " + tableName + " group by Date,unmatchedStatus,Run";
	    LOG.debug("UnMatched Query:" + Query);
	    RowMapper<DataMatchingSummary> rowMapper = (rs, i) -> {
		DataMatchingSummary dms = new DataMatchingSummary();
		LOG.debug("Date=" + rs.getString("Date"));
		LOG.debug("unmatchedStatus=" + rs.getString("unmatchedStatus"));
		String date = rs.getString("Date");
		Long run = rs.getLong("Run");
		String date_run = date + "(" + StringUtils.leftPad(run.toString(), 2, "0") + ")";
		dms.setDate(date_run);
		if (rs.getString("unmatchedStatus").equalsIgnoreCase("passed"))
		    dms.setStatus("1");
		else
		    dms.setStatus("0");
		return dms;
	    };
	    List<DataMatchingSummary> unmatchedGraph = jdbcTemplate1.query(Query, rowMapper);
	    return unmatchedGraph;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<DataMatchingSummary> getLeftGraph(String tableName) {
	try {
	    String Query = "select Date,Run,source1OnlyStatus from " + tableName
		    + "   group by Date,Run,source1OnlyStatus";

	    RowMapper<DataMatchingSummary> rowMapper = (rs, i) -> {
		DataMatchingSummary dms = new DataMatchingSummary();
		// LOG.debug("Date="+rs.getString("Date"));
		String date = rs.getString("Date");
		Long run = rs.getLong("Run");
		String date_run = date + "(" + StringUtils.leftPad(run.toString(), 2, "0") + ")";
		dms.setDate(date_run);
		if (rs.getString("source1OnlyStatus").equalsIgnoreCase("passed"))
		    dms.setStatus("1");
		else
		    dms.setStatus("0");
		return dms;
	    };
	    List<DataMatchingSummary> leftGraph = jdbcTemplate1.query(Query, rowMapper);
	    return leftGraph;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<DataMatchingSummary> getRightGraph(String tableName) {
	try {
	    String Query = "select Date,Run,source2OnlyStatus from " + tableName
		    + "   group by Date,Run,source2OnlyStatus";

	    RowMapper<DataMatchingSummary> rowMapper = (rs, i) -> {
		DataMatchingSummary dms = new DataMatchingSummary();
		// LOG.debug("Date="+rs.getString("Date"));
		String date = rs.getString("Date");
		Long run = rs.getLong("Run");
		String date_run = date + "(" + StringUtils.leftPad(run.toString(), 2, "0") + ")";
		dms.setDate(date_run);
		if (rs.getString("source2OnlyStatus").equalsIgnoreCase("passed"))
		    dms.setStatus("1");
		else
		    dms.setStatus("0");
		return dms;
	    };
	    List<DataMatchingSummary> rightGraph = jdbcTemplate1.query(Query, rowMapper);
	    return rightGraph;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public SqlRowSet getUnmatchedGroupbyTableData(String tableName) {
	try {
	    String sql = "select * from " + tableName;
	    SqlRowSet UnmatchedGroupbyTableData = jdbcTemplate1.queryForRowSet(sql);
	    return UnmatchedGroupbyTableData;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List getdatafromlistappsandlistdatasources() {
	try {
	    String sql = "select la.name, la.createdAt ,ls.name,ls.idData,la.idApp,la.appType from listApplications la,listDataSources ls where ls.idData = la.idData and la.active='yes'";
	    List<ListApplicationsandListDataSources> listDataSchema = jdbcTemplate.query(sql,
		    new RowMapper<ListApplicationsandListDataSources>() {
			public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
			    ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();
			    listappslistds.setLaName(rs.getString("name"));
			    listappslistds.setLsName(rs.getString(3));
			    listappslistds.setCreatedAt(rs.getDate("createdAt"));
			    listappslistds.setIdData(rs.getLong("idData"));
			    listappslistds.setIdApp(rs.getLong("idApp"));
			    listappslistds.setAppType(rs.getString("appType"));
			    return listappslistds;
			}
		    });
	    return listDataSchema;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<KeyMeasurementMatchingDashboard> getmatchingDashboard() {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    // select distinct rm.appID , rm.AppName,rm.AppType from result_master_table rm
	    // , data_matching_dashboard dm where rm.AppType = 'DM' and rm.appID=dm.idapp
	    // ORDER BY rm.appID DESC;
	    // String sql = "select * from data_matching_dashboard order by idapp";

	    String sql = "select distinct dm.* , rm.AppType from result_master_table rm , data_matching_dashboard dm where rm.AppType = 'DM' and rm.appID=dm.idapp ORDER BY dm.idapp DESC";
	    masterDashboard = getKeyMeasurementMatchingDashboardDetails(sql);
	} catch (Exception e) {
	}
	return masterDashboard;
    }

    private List<KeyMeasurementMatchingDashboard> getKeyMeasurementMatchingDashboardDetails(String sql) {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		KeyMeasurementMatchingDashboard dashboard = new KeyMeasurementMatchingDashboard();
		dashboard.setIdApp(queryForRowSet.getLong("idapp"));
		dashboard.setDate(queryForRowSet.getString("date"));
		dashboard.setRun(queryForRowSet.getLong("run"));
		dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
		dashboard.setSource1(queryForRowSet.getString("source1Name"));
		dashboard.setSource2(queryForRowSet.getString("source2Name"));
		dashboard.setSource1Count(queryForRowSet.getLong("source1Count"));
		dashboard.setSource1Records(queryForRowSet.getLong("source1OnlyRecords"));
		dashboard.setSource2Count(queryForRowSet.getLong("source2Count"));
		dashboard.setSource2Records(queryForRowSet.getLong("source2OnlyRecords"));
		dashboard.setSource1OnlyStatus(queryForRowSet.getString("source1Status"));
		dashboard.setSource2OnlyStatus(queryForRowSet.getString("source2Status"));

		if (queryForRowSet.getString("unMatchedStatus").equalsIgnoreCase("NA")) {
		    dashboard.setUnmatchedStatus("NA");
		    ;
		} else {
		    dashboard.setUnmatchedStatus(queryForRowSet.getString("unMatchedStatus"));
		}
		if (queryForRowSet.getString("unMatchedRecords") == null) {
		    dashboard.setUnmatchedRecords("NA");
		} else {
		    dashboard.setUnmatchedRecords(queryForRowSet.getString("unMatchedRecords"));
		}
		masterDashboard.add(dashboard);
	    }
	} catch (Exception e) {
	    throw e;
	}
	return masterDashboard;
    }

    public List<RollDataMatchingDashboard> getRollmatchingDashboard() {
	List<RollDataMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    String sql = "select * from roll_data_matching_dashboard order by idapp";
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		RollDataMatchingDashboard dashboard = new RollDataMatchingDashboard();
		dashboard.setIdApp(queryForRowSet.getLong("idapp"));
		dashboard.setDate(queryForRowSet.getString("date"));
		dashboard.setRun(queryForRowSet.getLong("run"));
		dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
		dashboard.setSource1(queryForRowSet.getString("source1Name"));
		dashboard.setSource2(queryForRowSet.getString("source2Name"));
		dashboard.setSource1Count(queryForRowSet.getLong("source1Count"));
		dashboard.setSource1Records(queryForRowSet.getLong("source1OnlyRecords"));
		dashboard.setSource2Count(queryForRowSet.getLong("source2Count"));
		dashboard.setSource2Records(queryForRowSet.getLong("source2OnlyRecords"));
		dashboard.setSource1OnlyStatus(queryForRowSet.getString("source1Status"));
		dashboard.setSource2OnlyStatus(queryForRowSet.getString("source2Status"));
		dashboard.setUnmatchedStatus(queryForRowSet.getString("unMatchedStatus"));
		if (queryForRowSet.getString("unMatchedRecords") == null) {
		    dashboard.setUnmatchedRecords("NA");
		} else {
		    dashboard.setUnmatchedRecords(queryForRowSet.getString("unMatchedRecords"));
		}
		masterDashboard.add(dashboard);
	    }
	} catch (Exception e) {
	}
	return masterDashboard;
    }

    @Override
    public RollDataMatchingDashboard getRollmatchingDashboardById(long idApp) {
	RollDataMatchingDashboard result = new RollDataMatchingDashboard();
	List<RollDataMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    String sql = "select * from roll_data_matching_dashboard where idapp = ?";
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);
	    while (queryForRowSet.next()) {
		RollDataMatchingDashboard dashboard = new RollDataMatchingDashboard();
		dashboard.setIdApp(queryForRowSet.getLong("idapp"));
		dashboard.setDate(queryForRowSet.getString("date"));
		dashboard.setRun(queryForRowSet.getLong("run"));
		dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
		dashboard.setSource1(queryForRowSet.getString("source1Name"));
		dashboard.setSource2(queryForRowSet.getString("source2Name"));
		dashboard.setSource1Count(queryForRowSet.getLong("source1Count"));
		dashboard.setSource1Records(queryForRowSet.getLong("source1OnlyRecords"));
		dashboard.setSource2Count(queryForRowSet.getLong("source2Count"));
		dashboard.setSource2Records(queryForRowSet.getLong("source2OnlyRecords"));
		dashboard.setSource1OnlyStatus(queryForRowSet.getString("source1Status"));
		dashboard.setSource2OnlyStatus(queryForRowSet.getString("source2Status"));
		dashboard.setUnmatchedStatus(queryForRowSet.getString("unMatchedStatus"));
		if (queryForRowSet.getString("unMatchedRecords") == null) {
		    dashboard.setUnmatchedRecords("NA");
		} else {
		    dashboard.setUnmatchedRecords(queryForRowSet.getString("unMatchedRecords"));
		}
		masterDashboard.add(dashboard);
	    }
	    if (masterDashboard != null && masterDashboard.size() > 0) {
		result = masterDashboard.get(0);
	    }
	} catch (Exception e) {
	}
	return result;
    }

    @Override
    public List<KeyMeasurementMatchingDashboard> getDashboardStatusForKeyMeasurementMatching(Map<Long, String> map) {

	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {

	    for (Entry<Long, String> matchingResultTableData : map.entrySet()) {
		try {
		    KeyMeasurementMatchingDashboard dashboard = new KeyMeasurementMatchingDashboard();
		    dashboard.setIdApp(matchingResultTableData.getKey());
		    dashboard.setValidationCheckName(matchingResultTableData.getValue());

		    String matchingTableName = "DATA_MATCHING_" + matchingResultTableData.getKey() + "_SUMMARY";
		    List<DataMatchingSummary> summaryData = getDataFromDataMatchingSummaryGroupByDate(
			    matchingTableName);

		    dashboard.setSource1Count(summaryData.get(0).getTotalRecordsInSource1());
		    dashboard.setSource1Records(summaryData.get(0).getSource1OnlyRecords());
		    dashboard.setSource2Count(summaryData.get(0).getTotalRecordsInSource2());
		    dashboard.setSource2Records(summaryData.get(0).getSource2OnlyRecods());
		    dashboard.setSource1OnlyStatus(summaryData.get(0).getSource1OnlyStatus());
		    dashboard.setSource2OnlyStatus(summaryData.get(0).getSource2OnlyStatus());
		    dashboard.setDate(summaryData.get(0).getDate());
		    dashboard.setRun(summaryData.get(0).getRun());
		    // check if unmatched or Not
		    SqlRowSet checkUnMatch = getThresholdFromListApplication(matchingResultTableData.getKey());
		    while (checkUnMatch.next()) {
			if (checkUnMatch.getString(2).equalsIgnoreCase("Y")) {
			    dashboard.setUnmatchedStatus(summaryData.get(0).getStatus());
			    dashboard.setUnmatchedRecords(summaryData.get(0).getUnmatchedRecords().toString());
			} else {
			    dashboard.setUnmatchedStatus("NA");
			    dashboard.setUnmatchedRecords("NA");
			}
		    }
		    // Source1 &Source2 Names
		    Map<Long, String> source1NameFromAppDB = getSource1NameFromAppDB(matchingResultTableData.getKey());
		    for (Entry<Long, String> source1NameFromAppDBData : source1NameFromAppDB.entrySet()) {
			dashboard.setSource1(source1NameFromAppDBData.getValue());
			dashboard.setSource2(getSource2NameFromAppDB(source1NameFromAppDBData.getKey()));
			masterDashboard.add(dashboard);
		    }
		} catch (Exception e) {
		    // TODO: handle exception
		}
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	// createMatchingCsv(masterDashboard);
	return masterDashboard;
    }

    public void createMatchingCsv(String tableName) {
	List<KeyMeasurementMatchingDashboard> matchingDashboardList = new ArrayList<KeyMeasurementMatchingDashboard>();
	if (tableName.equalsIgnoreCase("dm_dashboard")) {
	    matchingDashboardList = getmatchingDashboard();
	} else if (tableName.equalsIgnoreCase("pkm_dashboard")) {
	    matchingDashboardList = getPrimaryKeyMatchingDashboard();
	}
	try {
	    Collections.reverse(matchingDashboardList);
	    String filePath = "";
	    if (tableName.equalsIgnoreCase("pkm_dashboard")) {
		filePath = System.getenv("DATABUCK_HOME") + "/csvFiles/pkm_dashboard.csv";
	    } else {
		filePath = System.getenv("DATABUCK_HOME") + "/csvFiles/dm_dashboard.csv";
	    }
	    PrintWriter pw = new PrintWriter(new FileWriter(new File(filePath)));
	    String csvHeader = "idApp,Date,Run,Validation Check Name,Source1 Name,Source2 Name,Source1Count,Source1OnlyRecords,Source1Status,Source2Count,Source2OnlyRecords,Source2Status,UnMatchedRecords,UnMatchedStatus";
	    pw.println(csvHeader);
	    for (KeyMeasurementMatchingDashboard kmmd : matchingDashboardList) {
		StringJoiner csvData = new StringJoiner(",");
		csvData.add(kmmd.getIdApp().toString());
		csvData.add(kmmd.getDate());
		csvData.add(kmmd.getRun().toString());
		csvData.add(kmmd.getValidationCheckName());
		csvData.add(kmmd.getSource1());
		csvData.add(kmmd.getSource2());
		csvData.add(kmmd.getSource1Count().toString());
		csvData.add(kmmd.getSource1Records().toString());
		csvData.add(kmmd.getSource1OnlyStatus());
		csvData.add(kmmd.getSource2Count().toString());
		csvData.add(kmmd.getSource2Records().toString());
		csvData.add(kmmd.getSource2OnlyStatus());
		csvData.add(kmmd.getUnmatchedRecords().toString());
		csvData.add(kmmd.getUnmatchedStatus());
		pw.println(csvData);
	    }
	    pw.flush();
	    pw.close();
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
    }

    public List<ListApplications> getdatafromlistapplications() {
	List<ListApplications> listApplication = new ArrayList<>();
	try {
	    String sql = "select * from listApplications where appType='Data Forensics' AND active='yes'";
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		ListApplications la = new ListApplications();
		la.setIdApp(queryForRowSet.getLong("idApp"));
		la.setFileNameValidation(queryForRowSet.getString("fileNameValidation"));
		// la.setGarbageRows(queryForRowSet.getInt("garbageRows"));
		la.setEntityColumn(queryForRowSet.getString("entityColumn"));
		la.setColOrderValidation(queryForRowSet.getString("colOrderValidation"));
		la.setNumericalStatCheck(queryForRowSet.getString("numericalStatCheck"));
		la.setStringStatCheck(queryForRowSet.getString("stringStatCheck"));
		la.setRecordAnomalyCheck(queryForRowSet.getString("recordAnomalyCheck"));
		la.setNonNullCheck(queryForRowSet.getString("nonNullCheck"));
		la.setDataDriftCheck(queryForRowSet.getString("dataDriftCheck"));
		la.setRecordCountAnomaly(queryForRowSet.getString("recordCountAnomaly"));
		la.setRecordCountAnomalyThreshold(queryForRowSet.getDouble("recordCountAnomalyThreshold"));
		la.setOutOfNormCheck(queryForRowSet.getString("outOfNormCheck"));
		la.setApplyRules(queryForRowSet.getString("applyRules"));
		la.setApplyDerivedColumns(queryForRowSet.getString("applyDerivedColumns"));
		la.setKeyGroupRecordCountAnomaly(queryForRowSet.getString("keyGroupRecordCountAnomaly"));
		la.setUpdateFrequency(queryForRowSet.getString("updateFrequency"));
		la.setFrequencyDays(queryForRowSet.getInt("frequencyDays"));
		la.setIncrementalMatching(queryForRowSet.getString("incrementalMatching"));
		la.setBuildHistoricFingerPrint(queryForRowSet.getString("buildHistoricFingerPrint"));
		la.setHistoricStartDate(queryForRowSet.getString("historicStartDate"));
		la.setHistoricEndDate(queryForRowSet.getString("historicEndDate"));
		la.setHistoricDateFormat(queryForRowSet.getString("historicDateFormat"));
		la.setCsvDir(queryForRowSet.getString("csvDir"));
		la.setGroupEquality(queryForRowSet.getString("groupEquality"));
		la.setGroupEqualityThreshold(queryForRowSet.getDouble("groupEqualityThreshold"));
		la.setIdData(queryForRowSet.getLong("idData"));
		la.setTimeSeries(queryForRowSet.getString("timeSeries"));
		la.setIdRightData(queryForRowSet.getInt("idRightData"));
		la.setMatchingThreshold(queryForRowSet.getDouble("matchingThreshold"));
		la.setName(queryForRowSet.getString("name"));

		listApplication.add(la);
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return listApplication;
    }

    public Map<Long, String> getSource1NameFromAppDB(Long idApp) {
	Map<Long, String> map = new LinkedHashMap<Long, String>();
	try {
	    String sql = "select la.idRightData,ls.name from listApplications la,listDataSources ls "
		    + "where ls.idData = la.idData and idApp=" + idApp;
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		map.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
	    }
	    return map;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return map;
    }

    public String getSource2NameFromAppDB(Long idRightData) {
	try {
	    return jdbcTemplate.queryForObject("select name from listDataSources where idData=" + idRightData,
		    String.class);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return "";
    }

    public String getAppNameFromListApplication(Long idApp) {
	return jdbcTemplate.queryForObject("SELECT NAME FROM listApplications  WHERE idApp=?", String.class, idApp);
    }

    @Override
    public SqlRowSet getRollDataSummary(String tableName, long SchemaId) {
	try {
	    String sql = "select t.* from " + tableName + " t where t.\"Date\" = (SELECT MAX(t2.\"Date\") from "
		    + tableName + " t2) and t.\"Run\"=(select Max(t1.\"Run\") from " + tableName
		    + " t1 where t1.\"Date\" = (SELECT MAX(t2.\"Date\") from " + tableName + " t2)) limit 20";
	    JdbcTemplate trgt_jdbcTemplate = getTargetSchemaConnection(SchemaId);
	    if (trgt_jdbcTemplate != null) {
		return trgt_jdbcTemplate.queryForRowSet(sql);
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public long getRollDataSummaryCount(String tableName, long rollTargetSchemaId) {
	long count = 0;
	try {
	    String sql = "select count(t.*) from " + tableName + " t where t.\"Date\" = (SELECT MAX(t2.\"Date\") from "
		    + tableName + " t2) and t.\"Run\"=(select Max(t1.\"Run\") from " + tableName
		    + " t1 where t1.\"Date\" = (SELECT MAX(t2.\"Date\") from " + tableName + " t2))";
	    JdbcTemplate trgt_jdbcTemplate = getTargetSchemaConnection(rollTargetSchemaId);
	    if (trgt_jdbcTemplate != null) {
		count = trgt_jdbcTemplate.queryForObject(sql, Long.class);
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return count;
    }

    @Override
    public SqlRowSet downloadRollDataSummary(String tableName, long rollTargetSchemaId) {
	try {
	    String sql = "select t.* from " + tableName + " t where t.\"Date\" = (SELECT MAX(t2.\"Date\") from "
		    + tableName + " t2) and t.\"Run\"=(select Max(t1.\"Run\") from " + tableName
		    + " t1 where t1.\"Date\" = (SELECT MAX(t2.\"Date\") from " + tableName + " t2))";
	    JdbcTemplate trgt_jdbcTemplate = getTargetSchemaConnection(rollTargetSchemaId);
	    if (trgt_jdbcTemplate != null) {
		return trgt_jdbcTemplate.queryForRowSet(sql);
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    private JdbcTemplate getTargetSchemaConnection(long targetSchemaId) {
	JdbcTemplate jdbcTemplate = null;
	try {
	    // Get the schema details
	    List<ListDataSchema> list_schema = listDataSourceDAO.getListDataSchemaForIdDataSchema(targetSchemaId);

	    if (list_schema != null && list_schema.size() > 0) {
		ListDataSchema listDataSchema = list_schema.get(0);

		if (listDataSchema.getSchemaType().equalsIgnoreCase("Postgres")) {
		    String[] dbAndSchema = listDataSchema.getDatabaseSchema().split(",");

		    String url = "jdbc:postgresql://" + listDataSchema.getIpAddress() + ":" + listDataSchema.getPort()
			    + "/" + dbAndSchema[0];
		    if (dbAndSchema.length > 1 && dbAndSchema[1].length() > 0) {
			url = url + "?currentSchema=" + dbAndSchema[1]
				+ "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		    } else {
			url = url + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		    }
		    try {
			LOG.debug("****Url:" + url);
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("org.postgresql.Driver");
			dataSource.setUrl(url);
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String decryptedText = decryptor.decrypt(listDataSchema.getPassword());
			dataSource.setUsername(listDataSchema.getUsername());
			dataSource.setPassword(decryptedText);

			jdbcTemplate = new JdbcTemplate(dataSource);
		    } catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		    }
		}
	    }
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return jdbcTemplate;
    }

    // changes for primary key matching
    public Map<Long, String> getPrimaryKeyMatchingResultTable() {
	SqlRowSet queryForRowSet = null;
	Map<Long, String> map = new LinkedHashMap<Long, String>();
	try {
	    queryForRowSet = jdbcTemplate1.queryForRowSet(
		    "select distinct  appID,AppName from result_master_table where AppType='PKM' ORDER BY appID DESC");

	    LOG.debug("if getPrimaryKeyMatchingResultTable : " + queryForRowSet);
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	    return map;
	} catch (Exception e) {
	    queryForRowSet = jdbcTemplate1
		    .queryForRowSet("select distinct  appID,AppName from result_master_table where AppType='PKM'");

	    LOG.debug(" else getPrimaryKeyMatchingResultTable : " + queryForRowSet);
	    while (queryForRowSet.next()) {
		long appID = queryForRowSet.getLong("appID");
		String appName = queryForRowSet.getString("AppName");
		map.put(appID, appName);
	    }
	}
	return map;
    }

    // added for primary key matching

    public SqlRowSet getPrimaryKeyMatchingResultsTableNames(Long appId) {
	try {
	    LOG.debug("getPrimaryKeyMatchingResultsTableNames ==>" + appId);
	    return jdbcTemplate1.queryForRowSet("select * from result_master_table where appID=? and AppType='PKM'",
		    appId);

	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    // primary key matching
    public List<PrimaryMatchingSummary> getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(String tableName) {
	List<PrimaryMatchingSummary> al = new ArrayList<>();
	try {
	    DecimalFormat numberFormat = new DecimalFormat("#0.00");
	    RowMapper<PrimaryMatchingSummary> rowMapper = (resultSet, i) -> {
		PrimaryMatchingSummary primaryMatchSummaryObj = new PrimaryMatchingSummary();
		primaryMatchSummaryObj.setId(resultSet.getLong("id"));
		primaryMatchSummaryObj.setRun(resultSet.getLong("Run"));
		primaryMatchSummaryObj.setDate(resultSet.getString("Date"));

		primaryMatchSummaryObj.setLeftTotalCount(resultSet.getLong("leftTotalCount"));
		primaryMatchSummaryObj.setRightTotalCount(resultSet.getLong("rightTotalCount"));
		primaryMatchSummaryObj.setTotalMatchedCount(resultSet.getLong("totalMatchedCount"));
		primaryMatchSummaryObj.setUnMatchedCount(resultSet.getLong("unMatchedCount"));
		primaryMatchSummaryObj.setLeftOnlyCount(resultSet.getLong("leftOnlyCount"));
		primaryMatchSummaryObj.setLeftOnlyPercentage(
			Double.parseDouble(numberFormat.format(resultSet.getDouble("leftOnlyPercentage"))));
		primaryMatchSummaryObj.setRightOnlyCount(resultSet.getLong("rightOnlyCount"));
		primaryMatchSummaryObj.setRightOnlyPercentage(
			Double.parseDouble(numberFormat.format(resultSet.getDouble("rightOnlyPercentage"))));
		primaryMatchSummaryObj.setLeftNullCount(resultSet.getLong("leftNullCount"));
		primaryMatchSummaryObj.setRightNullCount(resultSet.getLong("rightNullCount"));

		primaryMatchSummaryObj.setLeftOnlyStatus(resultSet.getString("leftOnlyStatus"));
		primaryMatchSummaryObj.setRightOnlyStatus(resultSet.getString("rightOnlyStatus"));
		primaryMatchSummaryObj.setLeftMatchedCount(resultSet.getLong("leftMatchedCount"));
		primaryMatchSummaryObj.setRightMatchedCount(resultSet.getLong("rightMatchedCount"));
		primaryMatchSummaryObj.setUnMatchedCount(resultSet.getLong("unMatchedCount"));
		primaryMatchSummaryObj.setUnMatchedStatus(resultSet.getString("unMatchedStatus"));
		return primaryMatchSummaryObj;
	    };
	    String sql = "select * from " + tableName + "  ORDER BY id DESC";
	    LOG.debug("getDataFromPrimaryKeyDataMatchingSummaryGroupByDate : " + sql);
	    return jdbcTemplate1.query(sql, rowMapper);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return al;
    }

    public Object[] getZeroStatusAbdCountForPrimaryKeyMatching(String tableName, Long appId) {
	try {
	    String sql = "SELECT * FROM " + tableName + " where Run = " + "(select max(Run) from " + tableName
		    + " where Date = (" + "select max(Date) from " + tableName + "))" + " and Date = "
		    + "(select max(Date) from " + tableName + ")";

	    LOG.debug("getZeroStatusAbdCountForPrimaryKeyMatching : " + sql);
	    SqlRowSet rs1 = jdbcTemplate1.queryForRowSet(sql);
	    String leftOnlyStatus = "";
	    String rightOnlyStatus = "";
	    // int leftTotalCount = 0;
	    // int rightTotalCount = 0;

	    BigInteger leftTotalCount = BigInteger.valueOf(0L);
	    BigInteger rightTotalCount = BigInteger.valueOf(0L);

	    while (rs1.next()) {
		leftTotalCount = BigInteger.valueOf(rs1.getLong("leftTotalCount"));
		rightTotalCount = BigInteger.valueOf(rs1.getLong("rightTotalCount"));

		leftOnlyStatus = rs1.getString("leftOnlyStatus");
		rightOnlyStatus = rs1.getString("rightOnlyStatus");

	    }
	    LOG.debug("leftTotalCount = " + leftTotalCount + "|| rightTotalCount=" + rightTotalCount
		    + "|| leftOnlyStatus=" + leftOnlyStatus + "|| rightOnlyStatus=" + rightOnlyStatus);
	    return new Object[] { leftOnlyStatus, rightOnlyStatus, leftTotalCount, rightTotalCount };
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public Map<String, String> getDataFromPrimaryKeyDataMatchingSummary(String tableName) {
	try {
	    String sql = "select * from " + tableName + " where Date=(select max(Date) from " + tableName
		    + ")  and Run=(select max(Run) from  " + tableName + " where Date=(select max(Date) from "
		    + tableName + "))";
	    return getDMSummaryMap(sql);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    private Map<String, String> getDMSummaryMap(String sql) {
	try {
	    Map<String, String> dmSummaryMap = new IdentityHashMap<String, String>();
	    SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	    DecimalFormat numberFormat = new DecimalFormat("#0.00");
	    LOG.debug("getDataFromPrimaryKeyDataMatchingSummary : " + queryForRowSet);

	    while (queryForRowSet.next()) {
		dmSummaryMap.put("leftTotalCount", queryForRowSet.getString("leftTotalCount"));
		dmSummaryMap.put("rightTotalCount", queryForRowSet.getString("rightTotalCount"));
		dmSummaryMap.put("unMatchedCount", queryForRowSet.getString("unMatchedCount"));
		dmSummaryMap.put("totalMatchedCount", queryForRowSet.getString("totalMatchedCount"));
		dmSummaryMap.put("totalMatchedPercentage", queryForRowSet.getString("totalMatchedPercentage"));
		dmSummaryMap.put("leftOnlyCount", queryForRowSet.getString("leftOnlyCount"));
		dmSummaryMap.put("rightOnlyCount", queryForRowSet.getString("rightOnlyCount"));
		// numberFormat.format(queryForRowSet.getString("source2OnlyPercentage"));
		dmSummaryMap.put("rightOnlyPercentage",
			numberFormat.format(queryForRowSet.getDouble("rightOnlyPercentage")));
		dmSummaryMap.put("leftOnlyPercentage",
			numberFormat.format(queryForRowSet.getDouble("leftOnlyPercentage")));
		dmSummaryMap.put("unMatchedPercentage",
			numberFormat.format(queryForRowSet.getDouble("unMatchedPercentage")));
		dmSummaryMap.put("leftOnlyStatus", queryForRowSet.getString("leftOnlyStatus"));
		dmSummaryMap.put("rightOnlyStatus", queryForRowSet.getString("rightOnlyStatus"));
		dmSummaryMap.put("unMatchedStatus", queryForRowSet.getString("unMatchedStatus"));
		dmSummaryMap.put("Date", queryForRowSet.getString("Date"));
		dmSummaryMap.put("Run", queryForRowSet.getString("Run"));
	    }
	    LOG.debug("dmSummaryMap : " + dmSummaryMap.toString());
	    return dmSummaryMap;
	} catch (Exception e) {
	    throw e;
	}
    }

    public List<PrimaryMatchingSummary> getLeftGraphForPrimaryKeyMatching(String tableName) {
	try {
	    String Query = "select Date,leftOnlyStatus from " + tableName + " group by Date,leftOnlyStatus";
	    LOG.debug("getLeftGraphForPrimaryKeyMatching  : " + Query);
	    RowMapper<PrimaryMatchingSummary> rowMapper = (rs, i) -> {
		PrimaryMatchingSummary dms = new PrimaryMatchingSummary();
		// LOG.debug("Date="+rs.getString("Date"));
		dms.setDate(rs.getString("Date"));
		if (rs.getString("leftOnlyStatus").equalsIgnoreCase("passed"))
		    dms.setLeftOnlyStatus("1");
		else
		    dms.setLeftOnlyStatus("0");
		return dms;
	    };
	    List<PrimaryMatchingSummary> leftGraph = jdbcTemplate1.query(Query, rowMapper);
	    return leftGraph;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<PrimaryMatchingSummary> getUnmatchedGraphForPrimaryKeyMatching(String tableName) {
	try {
	    String Query = "select Date,unMatchedStatus from " + tableName + " group by Date,unMatchedStatus";

	    LOG.debug("### getUnmatchedGraphForPrimaryKeyMatching =>" + Query);

	    RowMapper<PrimaryMatchingSummary> rowMapper = (rs, i) -> {
		PrimaryMatchingSummary dms = new PrimaryMatchingSummary();
		LOG.debug("Date=" + rs.getString("Date"));
		LOG.debug("unMatchedStatus=" + rs.getString("unMatchedStatus"));
		dms.setDate(rs.getString("Date"));
		if (rs.getString("unMatchedStatus").equalsIgnoreCase("passed"))
		    dms.setUnMatchedStatus("1");
		else
		    dms.setUnMatchedStatus("0");
		return dms;
	    };
	    List<PrimaryMatchingSummary> unmatchedGraph = jdbcTemplate1.query(Query, rowMapper);
	    return unmatchedGraph;
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingDashboard() {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    String sql = "select distinct dm.* from data_matching_dashboard dm, result_master_table rm where rm.AppType='PKM' and rm.appID=dm.idapp;";
	    masterDashboard = getMasterDashboardDetails(sql);
	} catch (Exception e) {
	}
	return masterDashboard;
    }

    public List<KeyMeasurementMatchingDashboard> getMasterDashboardDetails(String sql) {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	while (queryForRowSet.next()) {
	    KeyMeasurementMatchingDashboard dashboard = new KeyMeasurementMatchingDashboard();
	    dashboard.setIdApp(queryForRowSet.getLong("idapp"));
	    dashboard.setDate(queryForRowSet.getString("date"));
	    dashboard.setRun(queryForRowSet.getLong("run"));
	    dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
	    dashboard.setSource1(queryForRowSet.getString("source1Name"));
	    dashboard.setSource2(queryForRowSet.getString("source2Name"));
	    dashboard.setSource1Count(queryForRowSet.getLong("source1Count"));
	    dashboard.setSource1Records(queryForRowSet.getLong("source1OnlyRecords"));
	    dashboard.setSource2Count(queryForRowSet.getLong("source2Count"));
	    dashboard.setSource2Records(queryForRowSet.getLong("source2OnlyRecords"));
	    dashboard.setSource1OnlyStatus(queryForRowSet.getString("source1Status"));
	    dashboard.setSource2OnlyStatus(queryForRowSet.getString("source2Status"));

	    if (queryForRowSet.getString("unMatchedStatus").equalsIgnoreCase("NA")) {
		dashboard.setUnmatchedStatus("NA");
		;
	    } else {
		dashboard.setUnmatchedStatus(queryForRowSet.getString("unMatchedStatus"));
	    }
	    if (queryForRowSet.getString("unMatchedRecords") == null) {
		dashboard.setUnmatchedRecords("NA");
	    } else {
		dashboard.setUnmatchedRecords(queryForRowSet.getString("unMatchedRecords"));
	    }
	    masterDashboard.add(dashboard);
	}
	return masterDashboard;
    }

    public String getPrimaryKeyMatchingResultTableColumns(Long nAppId) {
	String sGetDmCriteria = "";
	String sReturnTmpl = "{ PrimaryColCount: %1$s, ResultColumns: '%2$s' }";
	String sRetValue = "";

	int nPrimaryColCount = 0;

	SqlRowSet oSqlRowSet = null;
	List<String> aMatchResultColumns = new ArrayList<String>();
	List<String> aValueResultColumns = new ArrayList<String>();

	List<String> aAllResultColumns = new ArrayList<String>();

	sGetDmCriteria = sGetDmCriteria + "select \n";
	sGetDmCriteria = sGetDmCriteria + "c.idlistDMCriteria as RowId, b.matchType2 as MatchConfigType, \n";
	sGetDmCriteria = sGetDmCriteria + "c.leftSideColumn as LeftColumnName, c.rightSideColumn as RightColumnName \n";
	sGetDmCriteria = sGetDmCriteria + "from listApplications a, listDMRules b, listDMCriteria c \n";
	sGetDmCriteria = sGetDmCriteria + String.format("where b.idApp = a.idApp \n", nAppId);
	sGetDmCriteria = sGetDmCriteria + "and   b.idDM = c.idDM \n";
	sGetDmCriteria = sGetDmCriteria + String.format("and   a.idApp = %1$s \n", nAppId);
	sGetDmCriteria = sGetDmCriteria + "and   a.appType = 'Primary Key Matching' \n";
	sGetDmCriteria = sGetDmCriteria + "order by c.idListDMCriteria;";

	oSqlRowSet = jdbcTemplate.queryForRowSet(sGetDmCriteria);
	while (oSqlRowSet.next()) {
	    if (oSqlRowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")) {
		aMatchResultColumns.add("leftM_" + oSqlRowSet.getString("LeftColumnName"));
		aMatchResultColumns.add("rightM_" + oSqlRowSet.getString("RightColumnName"));

		++nPrimaryColCount;

	    } else if (oSqlRowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")) {
		aValueResultColumns.add("leftV_" + oSqlRowSet.getString("LeftColumnName"));
		aValueResultColumns.add("rightV_" + oSqlRowSet.getString("RightColumnName"));
	    }
	}

	aAllResultColumns.addAll(aMatchResultColumns);
	aAllResultColumns.addAll(aValueResultColumns);

	sRetValue = String.format(sReturnTmpl, nPrimaryColCount, String.join(",", aAllResultColumns));

	return sRetValue;
    }

    @Override
    public List<Map<String, Object>> getPrimaryKeyMatchingTableResult(String tableName, long appId) {
	List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
	try {
//			Get columns mapping
	    Map<String, String> columnsMapping = getPrimaryKeyMatchingColumnsMapping(appId);
	    Map<String, String> columnsMetaDataMap = new HashMap<>();

	    String sql = "select * from " + tableName + " where Date=(SELECT max(Date) from " + tableName + ") "
		    + " and Run=(SELECT max(Run) from " + tableName + " where Date=(SELECT max(Date) from " + tableName
		    + ")) ";

	    SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
	    SqlRowSetMetaData metaData = sqlRowSet.getMetaData();

	    // Create map with columns name and column type
	    for (int i = 1; i <= metaData.getColumnNames().length; ++i) {
		String column_name = metaData.getColumnName(i);
		String column_datatype = metaData.getColumnTypeName(i);
		columnsMetaDataMap.put(column_name, column_datatype);
	    }

	    while (sqlRowSet.next()) {
		Map<String, Object> dataMap = new HashMap<>();
		for (int i = 1; i <= metaData.getColumnNames().length; ++i) {
		    String column_name = metaData.getColumnName(i);
		    String column_datatype = metaData.getColumnTypeName(i);

		    Object columnValue = sqlRowSet.getObject(column_name);
		    if (column_datatype.equalsIgnoreCase("date")) {
			columnValue = sqlRowSet.getString(column_name);
		    }
		    if (column_datatype.equalsIgnoreCase("VARCHAR") || column_datatype.equalsIgnoreCase("TEXT")
			    || column_datatype.equalsIgnoreCase("nvarchar")) {
			if (columnsMapping.containsKey(column_name)) {
			    String mappingCol = columnsMapping.get(column_name);
			    String mappingColDataType = columnsMetaDataMap.get(mappingCol);
			    if (mappingColDataType.equalsIgnoreCase("Decimal")
				    || mappingColDataType.equalsIgnoreCase("double")
				    || mappingColDataType.equalsIgnoreCase("float")) {
				try {
				    columnValue = sqlRowSet.getDouble(column_name);
				} catch (Exception ex) {
				    ex.printStackTrace();
				}
			    } else if (mappingColDataType.equalsIgnoreCase("Integer")
				    || mappingColDataType.equalsIgnoreCase("int")
				    || mappingColDataType.equalsIgnoreCase("bigint")) {
				try {
				    columnValue = sqlRowSet.getLong(column_name);
				} catch (Exception ex) {
				    ex.printStackTrace();
				}
			    }
			}
		    }
		    if(column_name.trim().equalsIgnoreCase("date"))
				dataMap.put("Date", columnValue);
		    else if(column_name.trim().equalsIgnoreCase("run"))
				dataMap.put("Run", columnValue);
		    else
		    	dataMap.put(column_name, columnValue);
		}

		resultMap.add(dataMap);
	    }

	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return resultMap;
    }

    public List<String> getColumnNamesByMetadata(String tableName) {

	String sql = "SELECT * from " + tableName + " limit 1";

	LOG.debug("SQL : " + sql);

	return jdbcTemplate1.query(sql, new ResultSetExtractor<List<String>>() {

	    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		List<String> columnNames = new ArrayList<>();
		for (int i = 1; i <= columnCount; i++) {
			if(rsmd.getColumnName(i).equalsIgnoreCase("dbk_row_id")){
				columnNames.add("dbk_row_Id");
			}else{
				columnNames.add(rsmd.getColumnName(i));
			}
		}
		return columnNames;
	    }
	});
    }

    public List<String> getColumnNamesByMetadataWithType(String tableName) {

	String sql = "SELECT * from " + tableName + " limit 1";

	LOG.debug("----- sql ---" + sql);

	return jdbcTemplate1.query(sql, new ResultSetExtractor<List<String>>() {

	    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		List<String> columnNames = new ArrayList<>();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = rsmd.getColumnName(i);
			if(columnName.equalsIgnoreCase("dbk_row_Id"))
				columnName = "dbk_row_Id";
		    String colNameWithType = (columnName + ":" + rsmd.getColumnTypeName(i))
			    .replaceAll(":BIGINT", ":Integer").replace(":VARCHAR", ":String");
		    columnNames.add(colNameWithType);
		}
		return columnNames;
	    }
	});
    }

    // This method is used to prepare sql by ignoring columns from the result
    private String getIgnoreColumnSqlForMatching(JSONArray ignoreColumnArr, String tableName) {

	String ignoreMacthingColumnStr = "";
	try {
	    if (ignoreColumnArr.length() > 0) {

		ignoreMacthingColumnStr = "select ";

		List<String> columnNamesList = getColumnNamesByMetadata(tableName);

		// Create a new list for ignore columns
		List<String> ignoreColumnList = new ArrayList<>();

		// Adding ignore columns to list
		for (int i = 0; i < ignoreColumnArr.length(); i++) {
		    JSONObject ignoreColumnObj = (JSONObject) ignoreColumnArr.get(i);
		    String leftColumn = "leftV_" + ignoreColumnObj.getString("left").replace(" ", "\\ ");
		    String rightColumn = "rightV_" + ignoreColumnObj.getString("right").replace(" ", "\\ ");
		    ignoreColumnList.add(leftColumn);
		    ignoreColumnList.add(rightColumn);
		}
		// preparing sql for column names
		for (String colName : columnNamesList) {
		    if (ignoreColumnList.contains(colName)) {
			// LOG.debug("continued with column:"+colName);
			continue;
		    }
		    ignoreMacthingColumnStr = ignoreMacthingColumnStr + colName + ",";
		}

		ignoreMacthingColumnStr = ignoreMacthingColumnStr.substring(0, ignoreMacthingColumnStr.lastIndexOf(","))
			+ " from " + tableName + " where ";
		// Query compatibility changes for both POSTGRES and MYSQL
		String binary_key = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? ""
			: "BINARY";
		// preparing sql to consider only those values where data is not unmatched for
		// ignore columns
		for (int i = 0; i < ignoreColumnArr.length(); i++) {
		    JSONObject ignoreColumnObj = (JSONObject) ignoreColumnArr.get(i);
		    String leftColumn = ignoreColumnObj.getString("left");
		    String rightColumn = ignoreColumnObj.getString("right");
		    String ignoreSql = " " + binary_key + " trim(leftV_" + leftColumn + ") = trim(rightV_" + rightColumn
			    + ") AND ";

		    ignoreMacthingColumnStr = ignoreMacthingColumnStr + ignoreSql;
		}

		ignoreMacthingColumnStr = ignoreMacthingColumnStr.substring(0,
			ignoreMacthingColumnStr.lastIndexOf("AND"));
	    }

	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}

	return ignoreMacthingColumnStr;
    }

    @Override
    public List<Map<String, Object>> getPrimaryKeyMatchingTableResult(String tableName, JSONArray ignoreColumnArr) {
	List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
	try {
	    String sql = "select * from " + tableName;
	    String ignoreMacthingColumnStr = getIgnoreColumnSqlForMatching(ignoreColumnArr, tableName);

	    if (ignoreMacthingColumnStr != null && !ignoreMacthingColumnStr.trim().isEmpty())
		sql = ignoreMacthingColumnStr;
	    else
		LOG.debug("Input is missing ignore columns");

	    LOG.debug(sql);

	    SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
	    SqlRowSetMetaData metaData = sqlRowSet.getMetaData();

	    while (sqlRowSet.next()) {
		Map<String, Object> dataMap = new HashMap<>();
		for (int i = 1; i <= metaData.getColumnNames().length; ++i) {
		    String column_name = metaData.getColumnName(i);
		    String column_datatype = metaData.getColumnTypeName(i);

		    Object columnValue = sqlRowSet.getObject(column_name);
		    if (column_datatype.equalsIgnoreCase("date")) {
			columnValue = sqlRowSet.getString(column_name);
		    }
		    dataMap.put(column_name, columnValue);
		}

		resultMap.add(dataMap);
	    }

	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return resultMap;
    }

    @Override
    public Map<String, String> getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate(String tableName) {
	try {
	    String sql = "select * from " + tableName + " order by id desc limit 1";
	    return getDMSummaryMap(sql);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public List<KeyMeasurementMatchingDashboard> getKeyMeasurementMatchingDashboardByProjectNDateFilter(String domainId,
	    String projectId, String fromDate, String toDate) {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    String sql = "select distinct dm.* , rm.AppType from result_master_table rm , data_matching_dashboard dm where rm.AppType = 'DM' and rm.appID=dm.idapp and dm.idapp in ("
		    + primaryKeyMatchingResultService.getStringOfAppIdsForProjectNDomainByDateFilter(projectId,
			    domainId, fromDate, toDate)
		    + ") ORDER BY dm.idapp DESC";
	    masterDashboard = getKeyMeasurementMatchingDashboardDetails(sql);
	} catch (Exception e) {
	    LOG.error(e.getMessage() + " " + e.getCause());
	    e.printStackTrace();
	}
	return masterDashboard;
    }

    /*
     * Method to get columns data type for left and right mapping columns
     */
    @Override
    public Map<String, String> getPrimaryKeyMatchingColumnsMetaData(Long nAppId) {
	String sGetDmCriteria = "";
	SqlRowSet rowSet = null;
	Map<String, String> aMatchResultColumns = new HashMap<>();

	List<String> aAllResultColumns = new ArrayList<String>();

	sGetDmCriteria = "SELECT ldm.leftSideColumn,lldd.format AS leftFormat, "
		+ "ldm.rightSideColumn, rldd.format AS rightFormat, ldmr.matchType2 AS MatchConfigType\n"
		+ "FROM listDMCriteria ldm\n" + "INNER JOIN listDMRules ldmr ON ldmr.idDM=ldm.idDM\n"
		+ "INNER JOIN listDataDefinition lldd ON lldd.idColumn=ldm.idLeftColumn\n"
		+ "INNER JOIN listDataDefinition rldd ON rldd.idColumn=ldm.idRightColumn\n" + "WHERE ldmr.idApp="
		+ nAppId;

	rowSet = jdbcTemplate.queryForRowSet(sGetDmCriteria);
	while (rowSet.next()) {
	    if (rowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")) {
		aMatchResultColumns.put("leftM_" + rowSet.getString("leftSideColumn"), rowSet.getString("leftFormat"));
		aMatchResultColumns.put("rightM_" + rowSet.getString("rightSideColumn"),
			rowSet.getString("rightFormat"));
	    } else if (rowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")) {
		aMatchResultColumns.put("leftV_" + rowSet.getString("leftSideColumn"), rowSet.getString("leftFormat"));
		aMatchResultColumns.put("rightV_" + rowSet.getString("rightSideColumn"),
			rowSet.getString("rightFormat"));
	    }
	}
	return aMatchResultColumns;
    }

    public Map<String, String> getPrimaryKeyMatchingColumnsMapping(Long nAppId) {
	String sGetDmCriteria = "";
	SqlRowSet rowSet = null;
	Map<String, String> aMatchResultColumns = new HashMap<>();

	List<String> aAllResultColumns = new ArrayList<String>();

	sGetDmCriteria = "SELECT ldm.leftSideColumn, ldm.rightSideColumn, ldmr.matchType2 AS MatchConfigType\n"
		+ "FROM listDMCriteria ldm\n" + "INNER JOIN listDMRules ldmr ON ldmr.idDM=ldm.idDM\n"
		+ "WHERE ldmr.idApp=" + nAppId;

	rowSet = jdbcTemplate.queryForRowSet(sGetDmCriteria);
	while (rowSet.next()) {
	    String leftColumn = rowSet.getString("leftSideColumn");
	    String rightColumn = rowSet.getString("rightSideColumn");

	    if (rowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")) {
		aMatchResultColumns.put("leftM_" + leftColumn, "rightM_" + rightColumn);
		aMatchResultColumns.put("rightM_" + rightColumn, "leftM_" + leftColumn);
	    } else if (rowSet.getString("MatchConfigType").equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")) {
		aMatchResultColumns.put("leftV_" + leftColumn, "rightV_" + rightColumn);
		aMatchResultColumns.put("rightV_" + rightColumn, "leftV_" + leftColumn);
	    }
	}
	return aMatchResultColumns;
    }
}