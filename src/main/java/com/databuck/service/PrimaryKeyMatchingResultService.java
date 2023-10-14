package com.databuck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.impl.MatchingResultDaoImpl;
import org.apache.log4j.Logger;

@Service
public class PrimaryKeyMatchingResultService {

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    MatchingResultDaoImpl matchingresultdaompl;

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;
    
    private static final Logger LOG = Logger.getLogger(PrimaryKeyMatchingResultService.class);

    public Map<String, List<Map<String, Object>>> getTableDataByDateFilter(String idApp, String fromDate,
	    String toDate) {

	Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
	SqlRowSet tableNameNResulCat = jdbcTemplate1.queryForRowSet(
		"select table_name as tableName, Result_Category2 as resultCat from result_master_table where appID="
			+ idApp);

	List<String> tableNameArr = new ArrayList<String>();
	while (tableNameNResulCat.next()) {
	    if (tableNameNResulCat.getString("resultCat") != null
		    && !tableNameNResulCat.getString("resultCat").equalsIgnoreCase("summary")) {
		tableNameArr.add(tableNameNResulCat.getString("tableName"));
	    }
	}
	for (String tableName : tableNameArr) {

	    // check whether the table is present in database or not
	    if (isTablePresentInDB(tableName)) {
		break;
	    }

	    // Get column names
	    String[] columnNames = getColumnNames(tableName);

	    // Get transaction table data
	    resultMap.put(tableName,
		    getTranDetailAllDetails(tableName, columnNames, getDateFilter(fromDate, toDate, "Date")));
	}
	return resultMap;
    }

    /*
     * Create date filter condition
     */
    public String getDateFilter(String fromDate, String toDate, String columnName) {
	String dateFilter = "";
	// Query compatibility changes for both POSTGRES and MYSQL
	if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
	    dateFilter = "to_date(" + columnName + "::text, 'YYYY-MM-DD') between to_date('" + fromDate
		    + "'::text, 'YYYY-MM-DD') AND  to_date('" + toDate + "'::text, 'YYYY-MM-DD')";
	} else {
	    dateFilter = "STR_TO_DATE(" + columnName + ", '%Y-%m-%d') between STR_TO_DATE('" + fromDate
		    + "', '%Y-%m-%d') AND  STR_TO_DATE('" + toDate + "', '%Y-%m-%d')";
	}
	return dateFilter;
    }

    public String[] getColumnNames(String tableName) {
	SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet("select * from " + tableName);
	String[] columnNames = queryForRowSet.getMetaData().getColumnNames();

	columnNames = ArrayUtils.removeElement(columnNames, "Id");
	columnNames = ArrayUtils.removeElement(columnNames, "idApp");
	return columnNames;
    }

    private boolean isTablePresentInDB(String tableName) {
	SqlRowSet resultSet = jdbcTemplate1.queryForRowSet("SELECT "
		+ "COUNT(*) as count from information_schema.TABLES where table_schema = 'result_db' and table_name = '"
		+ tableName + "'");

	if (resultSet.next() && resultSet.getInt("count") == 0) {
	    return false;
	} else {
	    return true;
	}

    }

    public List<Map<String, Object>> getTranDetailAllDetails(String tableName, String[] columnNames,
	    String dateFilter) {
	List<Map<String, Object>> resultArray = new ArrayList<Map<String, Object>>();
	try {

	    String columns_Str = "";
	    int pos = 0;
	    for (String col : columnNames) {
		if (col != null && !col.trim().isEmpty()) {
		    columns_Str = (pos == 0) ? col : columns_Str + "," + col;

		    ++pos;
		}
	    }
	    String sql = "SELECT " + columns_Str + " FROM " + tableName;
	    if (dateFilter != null)
		sql = sql + " where (" + dateFilter + ")";
	    
	    LOG.debug("SQL Query  "+sql);	    

	    SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
	    while (rs.next()) {

		int colIndex = 1;
		JSONObject dmRowObject = new JSONObject();

		// Read Row Data
		for (String columnName : columnNames) {
		    String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

		    // Read column value
		    String columnValue = "";
		    if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
			    || columnDataType.equalsIgnoreCase("float")) {

			columnValue = rs.getString(columnName);
		    } else {
			columnValue = rs.getString(columnName);
		    }

		    if (columnValue == null)
			columnValue = "";

		    if (columnName.startsWith("fe") && (columnName.endsWith("Valdiff") || columnName.endsWith("ValDiff")
			    || columnName.endsWith("valDiff") || columnName.endsWith("val_Diff")
			    || columnName.endsWith("Val_diff"))) {
			columnName = columnName.replace("fe", "").replace("Valdiff", "Val Diff")
				.replace("ValDiff", "Val Diff").replace("val_diff", "Val Diff")
				.replace("val_Diff", "Val Diff").replace("Val_diff", "Val Diff")
				.replace("valDiff", "Val Diff");
		    }

		    columnName = columnName.replace("fe_Right", "Right ");
		    columnName = columnName.replace("fe_Left", "Left ");
		    columnName = columnName.replace("fe_right", "Right ");
		    columnName = columnName.replace("fe_left", "Left ");
		    columnName = columnName.replace("fe_R_", "Right ");
		    columnName = columnName.replace("fe_R", "Right ");
		    columnName = columnName.replace("fe_L_", "");
		    columnName = columnName.replace("fe_L", "");
		    columnName = columnName.replace("fe_r_", "Right ");
		    columnName = columnName.replace("fe_r", "Right ");
		    columnName = columnName.replace("fe_l_", "");
		    columnName = columnName.replace("fe_l", "");
		    columnName = columnName.replace("fe", "");
		    columnName = columnName.replaceAll("MeasurementSum", "Measurement Sum").replaceAll("measurementsum",
			    "Measurement Sum");
		    columnName = columnName.replaceAll("RecordCount", "Record Count").replaceAll("recordcount",
			    "Record Count");
		    columnName = columnName.replace("_", " ");
		    columnName.trim();
		    
		    dmRowObject.put(columnName.trim(), columnValue.trim());
		    ++colIndex;
		}
		
		resultArray.add(dmRowObject.toMap());
		
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    throw e;
	}

	return resultArray;
    }

    public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingDashboard(String domainId, String projectId,
	    String fromDate, String toDate) {
	List<KeyMeasurementMatchingDashboard> masterDashboard = new ArrayList<>();
	try {
	    String sql = "select distinct dm.* from data_matching_dashboard dm, result_master_table rm where rm.AppType='PKM' and rm.appID=dm.idapp and dm.idapp in ("
		    + getStringOfAppIdsForProjectNDomainByDateFilter(projectId, domainId, fromDate, toDate) + ")";
	    masterDashboard = matchingresultdaompl.getMasterDashboardDetails(sql);
	} catch (Exception e) {
		LOG.error(e.getMessage());
	}
	return masterDashboard;
    }

    public String getStringOfAppIdsForProjectNDomainByDateFilter(String projectId, String domainId, String fromDate,
	    String toDate) {
	String sql1 = "select idApp as appId from listApplications where project_id = " + projectId
		+ " and domain_id = " + domainId + " and  " + getDateFilter(fromDate, toDate, "createdAt");
	SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql1);
	StringBuilder stringBuilder = new StringBuilder();
	while (resultSet.next()) {
	    stringBuilder.append(resultSet.getString("appId"));
	    stringBuilder.append(",");
	}
	stringBuilder.deleteCharAt(stringBuilder.length() - 1);
	return stringBuilder.toString();
    }

    public Boolean validateToken(String token) {
	return executiveSummaryService.validateToken(token).getString("status") == "success" ? true : false;
    }
}
