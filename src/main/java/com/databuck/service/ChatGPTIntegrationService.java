package com.databuck.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.restcontroller.DataTemplateRestController;

@Service
public class ChatGPTIntegrationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    MSSQLConnection mSSQLConnection;

    @Autowired
    private IListDataSourceDAO listDataSourceDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    private static final Logger LOG = Logger.getLogger(ChatGPTIntegrationService.class);

    public Map<String, String> getRuleExpression(String text) throws IOException {
	Map<String, String> result = new HashMap<>();
	// String url = "https://api.openai.com/v1/completions";
	String instructions = "Given an input question, respond with syntactically correct PostgreSQL. Be creative but the SQL must be correct.";

	String tableName = getResultFromOpenAI("Respond with only table name from given sentence, " + text);

	// List<String> tableList = getTablesNamesFromExp(text);

	List<String> tableList = new ArrayList<>();
	tableList.add(tableName);

	String question = text; // getQuestionFromExp(text);

	String tableColumnExp = "";
	String onlyTableExp = "";
	Set<String> columnNames = new HashSet<>();
	if (!tableList.isEmpty()) {
	    onlyTableExp = "Only use tables called ";
	    for (String table : tableList) {
		onlyTableExp += "\\\"" + table + "\\\",";
		tableColumnExp += getColumnsForTable(table) + ".";
		String sql = "select distinct(displayName), format from listDataDefinition a "
			+ "left join listDataAccess l on a.idData=l.idData where l.folderName='" + table + "'";
		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
		String tableColumExp = "The \\\"" + table + "\\\"table has columns: ";
		while (sqlRowSet.next()) {
		    columnNames.add(sqlRowSet.getString("displayName"));
		}
		if (!tableColumExp.isEmpty()) {
		    tableColumExp = tableColumExp.substring(0, tableColumExp.length() - 1);
		    tableColumExp += ". ";
		}
	    }
	    onlyTableExp = onlyTableExp.substring(0, onlyTableExp.length() - 1);
	}

	String ruleNameInstruction = "Given an input question, Respond with single word name for, " + question;

	instructions = instructions + onlyTableExp + tableColumnExp + question;

	System.out.println("User instructions : " + instructions);

	result.put("ruleDescription", question);

	String ruleExpression = getResultFromOpenAI(instructions);

	result.put("tableName", tableList.get(0));
	result.put("ruleExpression", ruleExpression.replaceAll(";", " "));
	result.put("ruleName", getResultFromOpenAI(ruleNameInstruction));

	return result;
    }

    private String getColumnsForTable(String tableName) {
	String sql = "select distinct(displayName), format from listDataDefinition a "
		+ "left join listDataAccess l on a.idData=l.idData where l.folderName='" + tableName + "'";
	SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
	String tableColumExp = "The \\\"" + tableName + "\\\"table has columns: ";
	while (sqlRowSet.next()) {
	    tableColumExp += sqlRowSet.getString("displayName") + " (" + sqlRowSet.getString("format") + "),";
	}
	if (!tableColumExp.isEmpty()) {
	    return tableColumExp.substring(0, tableColumExp.length() - 1);
	}
	return "";
    }

    private String getResultFromOpenAI(String instruction) throws MalformedURLException, IOException {
	String url = "https://api.openai.com/v1/completions";
	HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

	con.setRequestMethod("POST");
	con.setRequestProperty("Content-Type", "application/json");
	con.setRequestProperty("Authorization", "Bearer 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");

	JSONObject data = new JSONObject();
	data.put("model", "text-davinci-003");
	data.put("prompt", instruction);
	data.put("max_tokens", 2048);
	data.put("temperature", 0.2);

	con.setDoOutput(true);
	con.getOutputStream().write(data.toString().getBytes());

	@SuppressWarnings("resource")
	String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines().reduce((a, b) -> a + b)
		.get();
	con.disconnect();
	return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text").replaceAll("\n", " ")
		.trim();
    }

    public Map<String, Object> assessTable(String question) {
	Map<String, Object> tableConnMap = null;
	try {

	    String tableName = getResultFromOpenAI(
		    "Respond with only table name from given sentence '" + question + "'");
	    tableName = tableName.replace(".", "");

	    tableConnMap = getValidationsFromTableName(tableName);
	    if (tableConnMap == null || !tableConnMap.containsKey("validation")) {
		tableConnMap = checkTableIsExistOrNot(tableName.trim());
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return tableConnMap;
    }

    private Map<String, Object> checkTableIsExistOrNot(String tableName) {
	Map<String, Object> result = new HashMap<>();
	String hostURI, userlogin, password, port, database;
	// = null;
//	String sql = "SELECT * FROM listDataSchema WHERE ipAddress in "
//		+ "     (SELECT ipAddress FROM listDataSchema GROUP BY ipAddress,schematype HAVING COUNT(ipAddress)>=0 and schemaType='MSSQL' and action='Yes')";

	String sql = "select * from listDataSchema where schemaType='MSSQL' and action='Yes' order by idDataSchema DESC";
	SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);

	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
	Set<String> connectionSet = new HashSet<>();
	List<Map<String, Object>> connList = new ArrayList<>();

	while (rs.next()) {
	    hostURI = rs.getString("ipAddress");
	    port = rs.getString("port");
	    userlogin = rs.getString("username");
	    password = rs.getString("password");
	    database = rs.getString("databaseSchema");
	    if (!connectionSet.contains(hostURI.trim() + "-" + database)) {
		LOG.debug("Getting details for " + hostURI + " " + database);
		connectionSet.add(hostURI.trim() + "-" + database);

		String decryptedPsw = encryptor.decrypt(password);

		if (rs.getString("schemaType").equals("MSSQL")) {
		    try {
			List<String> tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSql(hostURI, userlogin,
				decryptedPsw, port, database);
			LOG.debug("tables" + tableListFrom.toString());
			if (tableListFrom.contains(tableName)) {
			    Map<String, Object> connTableMap = new HashMap<>();
			    connTableMap.put("connectionName", rs.getString("schemaName"));
			    connTableMap.put("connectionId", rs.getLong("idDataSchema"));
			    connTableMap.put("tableName", tableName);
			    connTableMap.put("host", hostURI);
			    connTableMap.put("databaseSchema", database);
			    connTableMap.put("schemaType", rs.getString("schemaType"));
			    connList.add(connTableMap);
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		result.put("connections", connList);
	    }
	}
	return result;
    }

    private Map<String, Object> getValidationsFromTableName(String tableName) {

	Map<String, String> validation = new HashMap<>();
	Map<String, Object> result = new HashMap<>();

	String sql = "select t1.*, t2.test_run,t3.recordCount, t4.folderName from data_quality_dashboard t1  "
		+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
		+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
		+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
		+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run left join (Select idApp, folderName from processData group by idApp, folderName) t4 on t1.IdApp=t4.idApp where folderName = '"
		+ tableName + "' order by idApp desc";
	LOG.debug("Getting validation result for query : " + sql);
	SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
	if (queryForRowSet.next()) {
	    // DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
	    Long idApp = queryForRowSet.getLong("idApp");
	    validation.put("idApp", String.valueOf(queryForRowSet.getLong("idApp")));
	    validation.put("folderName", queryForRowSet.getString("folderName"));
	    validation.put("date", queryForRowSet.getString("date"));
	    validation.put("run", String.valueOf(queryForRowSet.getLong("run")));
	    validation.put("test_run", queryForRowSet.getString("test_run"));
	    validation.put("validationCheckName", queryForRowSet.getString("validationCheckName"));

	    try {
		String templateSql = "select la.idData, ls.schemaName as connectionName, ls.fileNamePattern as fileName, applyRules, lds.profilingEnabled from listApplications la join listDataSources lds on la.idData=lds.idData "
			+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp=" + idApp;
		Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);

		validation.put("idData", la_data.get("idData").toString());
		validation.put("connectionName", (String.valueOf(la_data.get("connectionName"))));
		validation.put("applyRules", (String.valueOf(la_data.get("applyRules"))));
		validation.put("profilingEnabled", (String.valueOf(la_data.get("profilingEnabled"))));
		ListDerivedDataSource listDerivedDataSource = listDataSourceDAO
			.getDataFromListDerivedDataSourcesOfIdData(Long.valueOf(la_data.get("idData").toString()));
		String isDerivedTemplate = listDerivedDataSource != null ? "Y" : "N";
		validation.put("isDerivedTemplate", isDerivedTemplate);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    validation.put("sourceName", queryForRowSet.getString("sourceName"));
	    validation.put("recordCountStatus", queryForRowSet.getString("recordCountStatus"));
	    validation.put("nullCountStatus", queryForRowSet.getString("nullCountStatus"));
	    validation.put("stringFieldStatus", queryForRowSet.getString("stringFieldStatus"));
	    validation.put("numericalFieldStatus", queryForRowSet.getString("numericalFieldStatus"));
	    validation.put("recordAnomalyStatus", queryForRowSet.getString("recordAnomalyStatus"));
	    validation.put("userSelectedFieldStatus", queryForRowSet.getString("userSelectedFieldStatus"));
	    validation.put("primaryKeyStatus", queryForRowSet.getString("primaryKeyStatus"));
	    validation.put("dataDriftStatus", queryForRowSet.getString("dataDriftStatus"));
	    validation.put("projectName", getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
	    validation.put("aggregateDQI", String.valueOf(queryForRowSet.getDouble("aggregateDQI"))); // added
	    validation.put("recordCount", String.valueOf(queryForRowSet.getLong("recordCount")));

	}
	if (!validation.isEmpty())
	    result.put("validation", validation);

	return result;
    }

    public String getProjectNameOfIdapp(Long idApp) {
	String projname = "";
	try {
	    String sql = "select a.projectName  from project a , listApplications b"
		    + " where b.project_id = a.idProject and b.idApp = ?";
	    projname = (String) jdbcTemplate.queryForObject(sql, new Object[] { idApp }, String.class);
	} catch (Exception e) {
	    LOG.error("Failed to get project name : " + e.getMessage());
	    e.printStackTrace();
	}
	return projname;
    }

    public Map<String, Object> getValidationDetails(int idData) {
	String sql = "select idApp, idData, name from listApplications where idData=" + idData
		+ " order by idApp DESC limit 1";
	SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
	Map<String, Object> valData = new HashMap<>();
	while (sqlRowSet.next()) {
	    valData.put("validationName", sqlRowSet.getString("name"));
	    valData.put("idApp", sqlRowSet.getInt("idApp"));
	    valData.put("idData", sqlRowSet.getInt("idData"));
	    String sql2 = "select * from runScheduledTasks where idApp=" + sqlRowSet.getInt("idApp")
		    + " order by id DESC limit 1";
	    SqlRowSet sqlRowSet2 = jdbcTemplate.queryForRowSet(sql2);
	    while (sqlRowSet2.next()) {
		valData.put("status", sqlRowSet2.getString("status"));
		valData.put("uniqueId", sqlRowSet2.getString("uniqueId"));
	    }
	}
	return valData;
    }

    public Map<String, Object> getValidationFromIdApp(Integer idApp) {
	Map<String, Object> validation = new HashMap<>();
	Map<String, Object> result = new HashMap<>();

	String sql = "select * from data_quality_dashboard where idApp="+idApp;
	LOG.debug("Getting validation result for query : " + sql);
	SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
	List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
	if (queryForRowSet.next()) {
	    // DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
	    validation.put("idApp", String.valueOf(queryForRowSet.getLong("idApp")));
	    validation.put("validationCheckName", queryForRowSet.getString("validationCheckName"));
	    validation.put("aggregateDQI", queryForRowSet.getDouble("aggregateDQI"));
	}
	if (!validation.isEmpty())
	    result.put("validation", validation);

	return result;

    }

}
