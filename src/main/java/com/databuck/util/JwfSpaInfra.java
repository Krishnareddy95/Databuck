package com.databuck.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.apache.log4j.Logger;

import org.json.JSONObject;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;

import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.JsonDaoI;
import com.databuck.util.DateUtility;

import java.lang.Runtime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Process;

@Service
public class JwfSpaInfra {

	@Autowired
	private Properties activeDirectoryConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(JwfSpaInfra.class);

	public interface CustomizeDataTableColumn { String getOuterHtml(HashMap<String,String> oDataRow); }

	public static ArrayList<ArrayList<String>> getSqlResultsAsStringArrayList(JdbcTemplate oJdbcConn, String sSqlQuery, String[] aColumnList, String sHtmlTable)
	{
		String sColumnValue = "";
		SqlRowSet oSqlRowSet = null;
		ArrayList<ArrayList<String>> aRetValue = new ArrayList<ArrayList<String> >();
		String sCheckBoxTmpl = "<input type='checkbox' id='%1$s' value='%2$s' data-tablename='%3$s'>";

		DateUtility.DebugLog("getSqlResultsAsStringArrayList 01",sHtmlTable);

		try {
			oSqlRowSet = oJdbcConn.queryForRowSet(sSqlQuery);
			while (oSqlRowSet.next()) {
				ArrayList<String> aRow = new ArrayList<String>();

				for (String sColumn: aColumnList) {
					String[] aColumnParts = sColumn.split(":");
					sColumnValue = oSqlRowSet.getString(aColumnParts[0]);
					sColumnValue = (aColumnParts.length > 1) ? String.format(sCheckBoxTmpl, sColumnValue, sColumnValue, sHtmlTable) : sColumnValue;
					aRow.add(sColumnValue);
				}
				aRetValue.add(aRow);
			}

			DateUtility.DebugLog("getSqlResultsAsStringArrayList 03",String.format("%1$s",aRetValue));
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return aRetValue;
	}

	/* Generic highly reusable function to return data set as array of string maps / data rows (all SQL column values as string type) so all data tables in system can use it easily */

	public static List<HashMap<String, String>> getDataRowsAsListOfMaps(JdbcTemplate oJdbcConn, String sSqlQuery, String[] aColumnList, String sHtmlTable, HashMap<String, CustomizeDataTableColumn> oCustomizeDataTable)
	{
		List<HashMap<String, String>> aListOfDataRows = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> oDataRow = new HashMap<String, String>();

		String[] aColumnNames = null;
		String sKeyName, sColumnValue, sTmplTag;
		SqlRowSet oSqlRowSet = null;
		boolean lIsLambdaTag = false;

		Map<String, String> aTmplLibrary = new HashMap<String, String>() {{
		    put("namedcheckbox", "<input class='datatable-checkbox' type='checkbox' id='%1$s-check-%2$s' value='%3$s'>");
		    put("checkbox", "<input class='datatable-checkbox' type='checkbox' id='check-%1$s' value='%2$s' data-tablename='%3$s'>");
		    put("edit", "<a class='datatable-anker' id='edit-%1$s'><i class='fa fa-edit'></i></a>");
		    put("copy", "<a class='datatable-anker' id='copy-%1$s'><i class='fa fa-copy'></i></a>");
		    put("review", "<a class='datatable-anker' id='review-%1$s'><i class='fa fa-check-square-o'></i></a>");
		    put("delete", "<a class='datatable-anker' id='delete-%1$s'><i class='fa fa-trash'></i></a>");
		    put("view", "<a class='datatable-anker' id='view-%1$s'><img src='./assets/img/view_icon_02.jpg'></a>");
		}};

		//DateUtility.DebugLog("getDataRowsAsListOfMaps 01",String.format("SQL Query\n%1$s\n", sSqlQuery));

		try {
			oSqlRowSet = oJdbcConn.queryForRowSet(sSqlQuery);
			//aColumnNames = (aColumnList == null || aColumnList.length < 1) ? oSqlRowSet.getMetaData().getColumnNames() : aColumnList;
			aColumnNames = (aColumnList == null || aColumnList.length < 1) ? getSqlRowSetColumnLabels(oSqlRowSet) : aColumnList;

			sKeyName = "";

			DateUtility.DebugLog("getDataRowsAsListOfMaps 01",String.format("No of columns in SQL Query or/and extended for formatting '%1$s'", aColumnNames.length));

			while (oSqlRowSet.next()) {
				oDataRow = new HashMap<String, String>();

				for (String sColumn: aColumnNames) {
					String[] aColumnParts = sColumn.split(":");

					if (aColumnParts.length > 1) { sTmplTag = aColumnParts[1];	} else {	sTmplTag = "None"; }

					sColumnValue = sTmplTag.equalsIgnoreCase("lambda") ? "" : oSqlRowSet.getString(aColumnParts[0]);

					 /* Check regular column value or special template based values */
					 switch (sTmplTag) {
					 	case "None":
					 		sKeyName = sColumn;
					 		break;

					 	case "namedcheckbox":
					 		sColumnValue = String.format(aTmplLibrary.get("namedcheckbox"), aColumnParts[0], sColumnValue, sColumnValue);
					 		sKeyName = aColumnParts[0] + "-check";
					 		break;

					 	case "checkbox":
					 		sColumnValue = String.format(aTmplLibrary.get("checkbox"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-check";
					 		break;

					 	case "edit":
					 		sColumnValue = String.format(aTmplLibrary.get("edit"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-edit";
					 		break;

					 	case "copy":
					 		sColumnValue = String.format(aTmplLibrary.get("copy"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-copy";
					 		break;

					 	case "review":
					 		sColumnValue = String.format(aTmplLibrary.get("review"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-review";
					 		break;

					 	case "delete":
					 		sColumnValue = String.format(aTmplLibrary.get("delete"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-delete";
					 		break;

					 	case "view":
					 		sColumnValue = String.format(aTmplLibrary.get("view"), sColumnValue, sColumnValue, sHtmlTable);
					 		sKeyName = aColumnParts[0] + "-view";
					 		break;

					 	case "lambda":
					 		sKeyName = aColumnParts[0];	  // in case of lambda column specification first part should be lambda function reference key and kind of dummy column name
					 		sColumnValue = oCustomizeDataTable.get(sKeyName).getOuterHtml(oDataRow);
					 		break;
					 }
					 oDataRow.put(sKeyName, sColumnValue);
				}
				aListOfDataRows.add(oDataRow);

			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}

		DateUtility.DebugLog("getDataRowsAsListOfMaps 99",String.format("Got no of data rows = '%1$s'", aListOfDataRows.size()));
		return aListOfDataRows;
	}

	private static String[] getSqlRowSetColumnLabels(SqlRowSet oSqlRowSet) {
		int nNoOfColumns = oSqlRowSet.getMetaData().getColumnCount();
		String[] aColumnLabels = new String[nNoOfColumns];

		for (int nIndex = 1;  nIndex <= nNoOfColumns; nIndex++) {
			aColumnLabels[nIndex-1] = oSqlRowSet.getMetaData().getColumnLabel(nIndex);
		}
		return aColumnLabels;
	}

	public static Map<String, String> getExpandedSynonyms(JdbcTemplate oJdbcConn, String sDomainInList) {
		String sQry = "select trim(a.tableColumn) as SynonymName, trim(a.possiblenames) as TemplateColumns\n";
		sQry = sQry + "from SynonymLibrary a, domain b\n";
		sQry = sQry + "where a.domain_Id = b.domainId\n";
		sQry = sQry + String.format("and   b.domainId in (%1$s);\n", sDomainInList);

		Map<String, String> oRawMap = new HashMap<String, String>();
		Map<String, String> oRetMap = new HashMap<String, String>();
		SqlRowSet oSqlRowSet = oJdbcConn.queryForRowSet(sQry);

		while (oSqlRowSet.next()) {
			oRawMap.put(oSqlRowSet.getString("SynonymName").toString(), oSqlRowSet.getString("TemplateColumns").toString());
		}

		for (String sKey : oRawMap.keySet()) {
			String sFieldList = oRawMap.get(sKey);
			String[] aFieldList = sFieldList.split(",", 0);
			for (String sField : aFieldList) {
				oRetMap.put(sField.toLowerCase(), sKey);
			}
	    }
		DateUtility.DebugLog("getExpandedSynonyms 01",String.format("%1$s", oRetMap));
		return oRetMap;
	}

	/* Pradeep (19-Apr-2020 generic utility function) - To conver string to SQL in clause list including start and ending brackets */
	public static String getStringToSqlInClause(String sAppLists, String sSeparator) {
		String sRetValue = "";
		String[] aInStringArray = null;

		sAppLists = (sAppLists == null) ? "" : sAppLists;

		if (sAppLists.isEmpty()) {
			aInStringArray = new String[] {};
		} else {
			aInStringArray = sAppLists.split(sSeparator);

			for (String sElement : aInStringArray) {
				sRetValue = sRetValue + "'" + sElement.trim() + "',";
			}
			sRetValue = sRetValue.substring(0, sRetValue.length()-1);
		}

		return sRetValue;
	}

	/* Pradeep Version 2 (19-Apr-2020 more generic) - To get all application options list needed by page. Version 1 will be removed soon */
	public static HashMap<String, ArrayList<HashMap<String,String>>> getAppOptionsListsMap(JdbcTemplate oJdbcConn, String sListNames) {
		HashMap<String, ArrayList<HashMap<String,String>>> aRetValue = new HashMap<String, ArrayList<HashMap<String,String>>>();
		ArrayList<HashMap<String,String>> aAppListOptions = new ArrayList<HashMap<String,String>>();

		String sAppOptListSql = "";
		SqlRowSet oSqlRowSet = null;
		String[] aSqlInList = null;

		try {
			aSqlInList = sListNames.split(",");
			DateUtility.DebugLog("getAppOptionsListsMap 01"," Length of list names array = " + String.valueOf(aSqlInList.length));

			for (String sListName : aSqlInList) {
				aAppListOptions = new ArrayList<HashMap<String,String>>();

				sAppOptListSql = "";
				sAppOptListSql = sAppOptListSql + "select upper(a.list_reference) as list_reference, b.*\n";
				sAppOptListSql = sAppOptListSql + "from app_option_list a, app_option_list_elements b\n";
				sAppOptListSql = sAppOptListSql + "where b.elements2app_list = a.row_id\n";
				sAppOptListSql = sAppOptListSql + String.format("and   a.list_reference = '%1$s'", sListName.trim());
				sAppOptListSql = sAppOptListSql + "and   b.active > 0\n";
				sAppOptListSql = sAppOptListSql + "order by b.position;";

				oSqlRowSet = oJdbcConn.queryForRowSet(sAppOptListSql);
				while (oSqlRowSet.next()) {
					HashMap<String,String> oElementData = new HashMap<String,String>();

					oElementData.put("element_reference", oSqlRowSet.getString("element_reference"));
					oElementData.put("row_id", oSqlRowSet.getString("row_id"));
					oElementData.put("element_text", oSqlRowSet.getString("element_text"));
					oElementData.put("is_default", (oSqlRowSet.getBoolean("is_default") ? "true" : "false")  );
					oElementData.put("position", oSqlRowSet.getString("position"));

					aAppListOptions.add(oElementData);
				}
				aRetValue.put(sListName.trim(), aAppListOptions);
				DateUtility.DebugLog("getAppOptionsListsMap 02 ",  sListName + "," + String.valueOf(aAppListOptions.size()));
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return aRetValue;
	}

	public static String getPropertyValue(Properties oPropFile, String sWhichProperty, String sDefaultValue) {
		String sRetValue = oPropFile.getProperty(sWhichProperty);

		if ( (sRetValue == null) || sRetValue.isEmpty() )  {
			sRetValue = sDefaultValue;
		} else {
			sRetValue = sRetValue.trim();
		}

		return sRetValue;
	}


	public static boolean isLDAPUser(JdbcTemplate oJdbcConn, long idUser) {
		boolean isApprover = false;
		Optional<String> User;

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		
		String Sql = "select  a.idUser as idUser from " + user_table + " a , UserRole b "
				+ "where a.idUser = b.idUser and b.idRole = 1 and a.idUser = " + idUser;
		// User = oJdbcConn.queryForObject(Sql, String.class);
		User = oJdbcConn.queryForList(Sql, String.class).stream().findFirst();
		if (User.isPresent())
			isApprover = true;
		return isApprover;
	}

	public static boolean isURLSecurityRequired(JdbcTemplate oJdbcConn) {
		SqlRowSet oSqlRowSet = null;
		String  sQrySql = "", sFlagValue = "";
		boolean lRetValue = false;

		try {
			sQrySql = sQrySql + "select b.property_value as PropertyValue \n";
			sQrySql = sQrySql + "from databuck_properties_master a, databuck_property_details b \n";
			sQrySql = sQrySql + "where a.property_category_id = b.property_category_id \n";
			sQrySql = sQrySql + "and   lower(a.property_category_name) = 'appdb' \n";
			sQrySql = sQrySql + "and   lower(b.property_name) = 'isurlsecurityrequired' \n";

			oSqlRowSet = oJdbcConn.queryForRowSet(sQrySql);
			while (oSqlRowSet.next()) {
				sFlagValue = oSqlRowSet.getString("PropertyValue");
				lRetValue = (sFlagValue.equalsIgnoreCase("Y")) ? true : false;
				
				//DateUtility.DebugLog("isURLSecurityRequired 01", String.format("%1$s, %2$s", sFlagValue,lRetValue));
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			lRetValue = false;
		}
		//DateUtility.DebugLog("isURLSecurityRequired 02", String.format("%1$s", lRetValue));
		return lRetValue;
	}

	public static String getEmailofApprover(JdbcTemplate oJdbcConn, long idApp) {
		String email = "";

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		String Sql = "select a.email as email from " + user_table + " a,listApplications b\n"
				+ " where a.idUser = b.approve_by\n" + " and b.idApp= " + idApp;
		email = oJdbcConn.queryForObject(Sql, String.class);
		return email;
	}

	public static void updateValidationApprovalStatus(JdbcTemplate oJdbcConn,long  idApp) {
		String Sql = " update listApplications \n" +
								"set approve_status = (select a.row_id from app_option_list_elements a,\n" +
														" app_option_list b \n" +
														" where a.elements2app_list=b.row_id \n" +
														" and a.element_reference='NOT APPROVED' \n" +
														" and b.list_reference='DQ_RULE_CATALOG_STATUS')\n" +
														" where  idApp= ?";


		Integer rowUpdates = oJdbcConn.execute(new PreparedStatementCreator() {

			@Override
			public java.sql.PreparedStatement createPreparedStatement(Connection con) throws SQLException {

				java.sql.PreparedStatement ps = con.prepareStatement(Sql);
				ps.setLong(1, idApp);
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(java.sql.PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});

	}

	/* Pradeep Version 1 (5-Mar-2020 quick dirty) - To get application options list needed by global thresholds. Version 1 will be removed, as of now it is used by validation add, edit JSP pages */
	public static Map<String, JSONArray> getAppListsOptionsMap(JdbcTemplate oJdbcConn, String sListNames) {
		Map<String, JSONArray> oRetValue = new HashMap<String, JSONArray>();
		JSONArray aAppListOptions = new JSONArray();
		String sAppOptListSql = "";
		SqlRowSet oSqlRowSet = null;

		try {
			sAppOptListSql = sAppOptListSql + "select a.list_reference, b.*\n";
			sAppOptListSql = sAppOptListSql + "from app_option_list a, app_option_list_elements b\n";
			sAppOptListSql = sAppOptListSql + "where b.elements2app_list = a.row_id\n";
			sAppOptListSql = sAppOptListSql + "and   a.list_reference = 'GLOBAL_THRESHOLDS_OPTION'\n";
			sAppOptListSql = sAppOptListSql + "and   b.active > 0\n";
			sAppOptListSql = sAppOptListSql + "order by a.list_reference, b.position;";

			oSqlRowSet = oJdbcConn.queryForRowSet(sAppOptListSql);
			while (oSqlRowSet.next()) {
				JSONObject oElementData = new JSONObject();

				oElementData.put("element_reference", oSqlRowSet.getString("element_reference"));
				oElementData.put("row_id", oSqlRowSet.getInt("row_id"));
				oElementData.put("element_text", oSqlRowSet.getString("element_text"));
				oElementData.put("is_default", oSqlRowSet.getBoolean("is_default"));
				oElementData.put("position", oSqlRowSet.getInt("position"));

				aAppListOptions.put(oElementData);
			}
			oRetValue.put("GLOBAL_THRESHOLDS_OPTION", aAppListOptions);
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return oRetValue;
	}

	/* Added next two functions to run external program or OS commands, capture output and return program result, std out and std err output */
	public static HashMap<String, String> runProgramAndSearchPatternInOutput(String[] cmd_agrs, String[] aPatternsToSearch) {
		HashMap<String, String> oRetValue = new HashMap<String, String>(){{
			put("Program Std Out", "Default");
			put("Program Std Err", "");
			put("Program Result", "0");
		}};
		BufferedReader oStdOut = null;
		BufferedReader oStdError = null;
    	String sOutput = "";
    	String sLine = "";

		Process oCmdProcess = null;
		int nExitCode = -1;

	    try {
	    	DateUtility.DebugLog("runProgramAndSearchPatternInOutput 01",	"Begin");

	    	/* Execute program and capture both outputs */
	    	ProcessBuilder processBuilder = new ProcessBuilder().command(cmd_agrs);
			oCmdProcess = processBuilder.start();
	    	nExitCode = oCmdProcess.waitFor();

	    	oRetValue.put("Program ExitCode", String.valueOf(nExitCode));

	    	oStdOut = new BufferedReader(new InputStreamReader(oCmdProcess.getInputStream()));
	    	oStdError = new BufferedReader(new InputStreamReader(oCmdProcess.getErrorStream()));

			while ((sLine = oStdOut.readLine()) != null) {
				sOutput = sOutput + ( (sLine == null) ? "" : sLine ) + "\n";
			}
			oRetValue.put("Program Std Out", sOutput);

			sLine = "";
			sOutput = "";
			while ((sLine = oStdError.readLine()) != null) {
				sOutput = sOutput + ( (sLine.isEmpty() || sLine == null) ? "" : sLine ) + "\n";
			}
			oRetValue.put("Program Std Err", sOutput);

			/* Now search pattern in std output lines of program being executed */
			sOutput = oRetValue.get("Program Std Out");
			if ( (sOutput.length() > 0) && (oRetValue.get("Program Std Err").length() < 1)) {
				oRetValue.put("Program Result", (isPatternsExists(aPatternsToSearch, sOutput) ? "1" : "-1"));
			} else {
				oRetValue.put("Program Result", "-1");
			}
			DateUtility.DebugLog("runProgramAndSearchPatternInOutput 02",	"Normal Exit");
	    } catch (Exception oException) {
	    	LOG.error(oException.getMessage());
	    	oException.printStackTrace();
	    	oRetValue.put("Program Result", "-1");
	    	DateUtility.DebugLog("runProgramAndSearchPatternInOutput 03",	"Exception Exit");
	    }
	    return oRetValue;
	}

	private static boolean isPatternsExists(String[] aPatterntoSearch, String sStringToSearch) {
		boolean lRetValue = false;
		int nMatchCtr = 0;
		boolean lMatchFound = false;
		String sSearchString = sStringToSearch.toLowerCase();

		for (String sPattern : aPatterntoSearch) {
			lMatchFound = (sSearchString.indexOf(sPattern.toLowerCase()) > -1) ? true : false;
			if (lMatchFound) { ++nMatchCtr; }
		}
		lRetValue = (nMatchCtr > 0) ? true : false;
		return lRetValue;
	}

	public boolean IsLoginLdapGroupExistsOnLdap(String sGroupName) {
		Properties propFile = activeDirectoryConnectionProperties;
		String sAdminPassword = propFile.getProperty("credentials");

		HashMap<String, String> oProgramOutput = null;
		String databuckHome = getDatabuckHome();
		String sCmdLine = databuckHome + "/scripts/ldap_login.sh %1$s %2$s %3$s";

    	try {
			String principal = activeDirectoryConnectionProperties.getProperty("principal");
    		sCmdLine = String.format(sCmdLine,sGroupName,sAdminPassword, principal);
    		System.out.print(sCmdLine);
    		oProgramOutput = runProgramAndSearchPatternInOutputExistingGroup(sCmdLine, new String[] { "# numEntries: 1" });
    		LOG.debug("loadComponentAccessControlViewList 02 "+oProgramOutput);
    		if(oProgramOutput.get("Program Result").equalsIgnoreCase("1")) {
	    		  LOG.debug("Group [" + sGroupName + "] is present on ldap server.");
	    		  return true;
    		}
    		else
    		{
    			 LOG.debug("Group [" + sGroupName + "] is not present on ldap server.");
    			 return false;
    		}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
    		LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;


	}
	public static HashMap<String, String> runProgramAndSearchPatternInOutputExistingGroup(String sFullCmdLine,  String[] aPatternsToSearch) {
		HashMap<String, String> oRetValue = new HashMap<String, String>(){{
			put("Program Std Out", "Default");
			put("Program Std Err", "");
			put("Program Result", "0");
		}};
		BufferedReader oStdOut = null;
		BufferedReader oStdError = null;
    	String sOutput = "";
    	String sLine = "";

		Runtime oRuntimeObject = Runtime.getRuntime();
		Process oCmdProcess = null;
		int nExitCode = -1;

	    try {
	    	DateUtility.DebugLog("runProgramAndSearchPatternInOutput 01",	"Begin");

	    	/* Execute program and capture both outputs */
	    	oCmdProcess = oRuntimeObject.exec(sFullCmdLine);


	    	oRetValue.put("Program ExitCode", String.valueOf(nExitCode));

	    	oStdOut = new BufferedReader(new InputStreamReader(oCmdProcess.getInputStream()));
	    	oStdError = new BufferedReader(new InputStreamReader(oCmdProcess.getErrorStream()));

			while ((sLine = oStdOut.readLine()) != null) {
				sOutput = sOutput + ( (sLine == null) ? "" : sLine ) + "\n";
			}
			oRetValue.put("Program Std Out", sOutput);

			sLine = "";
			sOutput = "";
			while ((sLine = oStdError.readLine()) != null) {
				sOutput = sOutput + ( (sLine.isEmpty() || sLine == null) ? "" : sLine ) + "\n";
			}
			oRetValue.put("Program Std Err", sOutput);

			/* Now search pattern in std output lines of program being executed */
			sOutput = oRetValue.get("Program Std Out");
			if ( (sOutput.length() > 0) && (oRetValue.get("Program Std Err").length() < 1)) {
				oRetValue.put("Program Result", (isPatternsExists(aPatternsToSearch, sOutput) ? "1" : "-1"));
			} else {
				oRetValue.put("Program Result", "-1");
			}
			DateUtility.DebugLog("runProgramAndSearchPatternInOutput 02",	"Normal Exit");
	    } catch (Exception oException) {
	    	LOG.error(oException.getMessage());
	    	oException.printStackTrace();
	    	oRetValue.put("Program Result", "-1");
	    	DateUtility.DebugLog("runProgramAndSearchPatternInOutput 03",	"Exception Exit");
	    }
	    return oRetValue;
	}


	private static String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		LOG.debug("DATABUCK_HOME:" + databuckHome);
		return databuckHome;
	}

}
