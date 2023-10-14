package com.databuck.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.JwfSpaInfra.CustomizeDataTableColumn;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PrimaryKeyMatchingController
{
	@Autowired
	private RBACController rbacController;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	public LoginService loginService;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	IValidationCheckDAO validationcheckdao;


	protected static final String PRIMARY_KEY_MATCH_JOIN_FIELD = "PRIMARY_KEY_MATCH_JOIN_FIELD";
	protected static final String PRIMARY_KEY_MATCH_VALUE_FIELD = "PRIMARY_KEY_MATCH_VALUE_FIELD";

	@RequestMapping(value = "/primaryKeyMatchView")
	public ModelAndView notificationView(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("primaryMatchingView");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "notificationView");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/mainPrimaryMatchingHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainPrimaryMatchingHandler(HttpSession oSession,  @RequestParam String sWrapperData, HttpServletResponse oResponse) throws Exception {
		JSONObject oWrapperData = new JSONObject();
		JSONObject oPrimaryKeyMatchingData = new JSONObject();
		JSONArray aPrimaryKeyMatchingData = new JSONArray();

		JSONObject oJsonResponse = new JSONObject();

		String sContext;
		String sMsg = "";
		boolean lStatus = true;

		long nLeftTmplId = 0l;
		long nRightTmplId = 0l;

		try {
			oWrapperData = new JSONObject(sWrapperData);
			sContext = oWrapperData.getString("Context");

			DateUtility.DebugLog("mainPrimaryMatchingHandler 01", "Server call context = " + sContext);

			switch (sContext) {

				case "LoadMatchConfiguration":
					oPrimaryKeyMatchingData = oWrapperData.getJSONObject("Data");
					nLeftTmplId = Long.parseUnsignedLong(oPrimaryKeyMatchingData.getString("LeftDataTmplId"));
					nRightTmplId = Long.parseUnsignedLong(oPrimaryKeyMatchingData.getString("RightDataTmplId"));

					oJsonResponse = loadPrimaryMatchingDataList(nLeftTmplId, nRightTmplId);
					break;

				case "SaveMatchConfiguration":
					aPrimaryKeyMatchingData = oWrapperData.getJSONArray("Data");
					if (isRuleOrCriteriaExists(oWrapperData.getString("IdApp")) < 1) {
						oJsonResponse = saveMatchConfiguration(aPrimaryKeyMatchingData, oWrapperData.getString("IdApp"), oWrapperData.getString("RightDataTmplId"));
						if (oJsonResponse.getBoolean("Status")) {
							DateUtility.DebugLog("mainPrimaryMatchingHandler", "Redirect to validation view page");
						}
					} else {
						oJsonResponse.put("Status", false);
						oJsonResponse.put("Msg", "Data already exist for this List Application");
					}
					break;

				default:
			}

			DateUtility.DebugLog("mainPrimaryMatchingHandler 03", "End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}

		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}
	@RequestMapping(value = "/editPrimaryMatchingHandler", method = RequestMethod.POST, produces = "application/json")
	public void editPrimaryMatchingHandler(HttpSession oSession,  @RequestParam String sWrapperData,@RequestParam String idApp,HttpServletResponse oResponse) throws Exception {
		JSONObject oWrapperData = new JSONObject();
		JSONObject oPrimaryKeyMatchingData = new JSONObject();
		JSONArray aPrimaryKeyMatchingData = new JSONArray();

		JSONObject oJsonResponse = new JSONObject();
		String sContext="";
		String sMsg = "";
		boolean lStatus = true;

		long nLeftTmplId = 0l;
		long nRightTmplId = 0l;
		try {
			oWrapperData = new JSONObject(sWrapperData);
			sContext = oWrapperData.getString("Context");

			DateUtility.DebugLog("editPrimaryMatchingHandler 01", "Server call context = " + sContext);

			switch (sContext) {

				case "LoadMatchConfiguration":
					oPrimaryKeyMatchingData = oWrapperData.getJSONObject("Data");
					nLeftTmplId = Long.parseUnsignedLong(oPrimaryKeyMatchingData.getString("LeftDataTmplId"));
					nRightTmplId = Long.parseUnsignedLong(oPrimaryKeyMatchingData.getString("RightDataTmplId"));

					oJsonResponse = loadPrimaryMatchingDataList(nLeftTmplId, nRightTmplId);
					break;

				case "SaveMatchConfiguration":
					 aPrimaryKeyMatchingData = oWrapperData.getJSONArray("Data");
					 validationcheckdao.deleteEntryFromListDMRulesWithIdApp(Long.parseLong(idApp));
					 oJsonResponse = saveMatchConfigurationChanges(aPrimaryKeyMatchingData, oWrapperData.getString("IdApp"), oWrapperData.getString("RightDataTmplId"));
					break;

				default:
			}
			DateUtility.DebugLog("editPrimaryMatchingHandler 03", "End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}

		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	private JSONObject loadPrimaryMatchingDataList(long nLeftTmplId, long nRightTmplId) throws Exception {
		List<HashMap<String, String>> aLeftDataList = null;
		List<HashMap<String, String>> aRightDataList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] {};

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		CustomizeDataTableColumn oRightSelectControl = (oDataRow) -> {
			String sSelectControlTmpl = "<select id='RightColumnNameAndType-%1$s'></select>";
			return String.format(sSelectControlTmpl, oDataRow.get("LeftPrimaryColumnId"));
		 };

		CustomizeDataTableColumn oLeftExprTextBox = (oDataRow) -> {
			String sLeftExprTextBox = "<input type='text' id='LeftExprTextBox-%1$s'>";
			return String.format(sLeftExprTextBox, oDataRow.get("LeftPrimaryColumnId"));
		 };

		CustomizeDataTableColumn oRightExprTextBox = (oDataRow) -> {
			String sRightExprTextBox = "<input type='text' id='RightExprTextBox-%1$s'>";
			return String.format(sRightExprTextBox, oDataRow.get("LeftPrimaryColumnId"));
		 };

		HashMap<String, CustomizeDataTableColumn> oCustomizeDataTable = new HashMap<String, CustomizeDataTableColumn>() {{
		    	put("RightColumnNameAndType", oRightSelectControl);
		    	put("LeftColumnExpr", oLeftExprTextBox);
		    	put("RightColumnExpr", oRightExprTextBox);
		  }};

		try {

			aColumnSpec = new String[] {
				"LeftPrimaryColumnId", "LeftColumnName", "LeftColumnType",  "LeftValueColumnId",
				"IsLeftColumnPrimaryField", "IsLeftColumnValueField", "IsLeftColumnCustomized", "LeftColumnCustomizedExpr",
				"LeftPrimaryColumnId:namedcheckbox", "LeftValueColumnId:namedcheckbox", "LeftCustomize:namedcheckbox","LeftColumnExpr:lambda",
				"LeftPrimaryColumnId:namedcheckbox", "LeftValueColumnId:namedcheckbox", "LeftCustomize:namedcheckbox",
				"RightColumnIdAndName", "IsRightColumnCustomized", "RightColumnCustomizedExpr",
				"RightCustomize:namedcheckbox", "RightColumnNameAndType:lambda", "RightColumnExpr:lambda"
			};

			sDataSql = "";
			sDataSql = sDataSql + "select idColumn as LeftPrimaryColumnId, displayName as LeftColumnName, format as LeftColumnType, idColumn as LeftValueColumnId, idColumn as LeftCustomize, \n";
			sDataSql = sDataSql + "'false' as IsLeftColumnPrimaryField, 'false' as IsLeftColumnValueField, 'false' as IsLeftColumnCustomized, '' as LeftColumnCustomizedExpr, \n";
			sDataSql = sDataSql + "idColumn as RightCustomize, '' as RightColumnIdAndName, 'false' as IsRightColumnCustomized, '' as RightColumnCustomizedExpr \n";
			sDataSql = sDataSql + String.format("from listDataDefinition where idData = %1$s;", nLeftTmplId);

			aLeftDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", oCustomizeDataTable);
			sDataViewList = oMapper.writeValueAsString(aLeftDataList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("LeftDataSet", aJsonDataList);

			sDataSql = String.format("select idColumn as RightColumnId, displayName as RightColumnName, format as RightColumnType from listDataDefinition where idData = %1$s", nRightTmplId);

			aColumnSpec = new String[]{"RightColumnId", "RightColumnName", "RightColumnType"};
			aRightDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", null);
			sDataViewList = oMapper.writeValueAsString(aRightDataList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("RightDataSet", aJsonDataList);

			DateUtility.DebugLog("loadPrimaryMatchingDataList", String.format("Return JSON = \n%1$s\n", oJsonRetValue));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	/* IsLeftColumnPrimaryField,IsLeftColumnValueField,IsLeftColumnCustomized,IsRightColumnCustomized, LeftPrimaryColumnId,LeftColumnName, LeftValueColumnId,
	 * listDMRules (idApp, matchType, matchType2)
	 * listDMCriteria (idlistDMCriteria,idDM , leftSideExp , rightSideExp    ,idLeftColumn    ,leftSideColumn  ,idRightColumn   ,rightSideColumn)
	 * RightColumnIdAndName, LeftColumnCustomizedExpr,RightColumnCustomizedExpr
	 *
	 * */
	private JSONObject saveMatchConfiguration(JSONArray aPrimaryKeyMatchingData, String sIdApp, String RightDataTmplId) throws Exception {
		String sRuleInsertSqlTmpl = "insert into listDMRules (idApp, matchType, matchType2) values (%1$s, '%2$s', '%3$s');";
		String sRuleInsertSql = "";
		String sUpdateListApplicationsSql = "update listApplications set idRightData = %1$s where idApp = %2$s";
		long nCriteriaRuleId = 0;
		JSONObject oJsonRetValue = new JSONObject();

		oJsonRetValue.put("Status", false);
		oJsonRetValue.put("Msg","Default failure status");

		try {
			DateUtility.DebugLog("saveMatchConfiguration", String.format("Input data sIdApp = %1$s \n%2$s\n", sIdApp, aPrimaryKeyMatchingData));

			// update validation for right data id
			sUpdateListApplicationsSql = String.format(sUpdateListApplicationsSql, RightDataTmplId, sIdApp);
			jdbcTemplate.update(sUpdateListApplicationsSql);

			sRuleInsertSql = String.format(sRuleInsertSqlTmpl, sIdApp, "One to One", PRIMARY_KEY_MATCH_JOIN_FIELD);
			jdbcTemplate.update(sRuleInsertSql);

			sRuleInsertSql = String.format(sRuleInsertSqlTmpl, sIdApp, "One to One", PRIMARY_KEY_MATCH_VALUE_FIELD);
			jdbcTemplate.update(sRuleInsertSql);

			for(int nIndex = 0; nIndex < aPrimaryKeyMatchingData.length(); nIndex++)
			{
			  JSONObject oPrimaryKeyMatchingData = aPrimaryKeyMatchingData.getJSONObject(nIndex);

				  /* One field cannot be primary key and value field at same time */
				  if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnPrimaryField")==true){

					  nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_JOIN_FIELD, sIdApp);
					  insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				  } else
				  if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnValueField")==true) {
					  nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_VALUE_FIELD, sIdApp);
					  insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				  }

			}

			oJsonRetValue.put("Status", true);
			oJsonRetValue.put("Msg","Match criteria or configuration saved successfully");

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}
	private JSONObject saveMatchConfigurationChanges(JSONArray aPrimaryKeyMatchingData, String sIdApp, String RightDataTmplId) throws Exception {
		long nCriteriaRuleId = 0;
		JSONObject oJsonRetValue = new JSONObject();

		oJsonRetValue.put("Status", false);
		oJsonRetValue.put("Msg","Default failure status");

		try {
			DateUtility.DebugLog("saveMatchConfiguration", String.format("Input data sIdApp = %1$s \n%2$s\n", sIdApp, aPrimaryKeyMatchingData));

			// update validation for right data id
			for(int nIndex = 0; nIndex < aPrimaryKeyMatchingData.length(); nIndex++)
			{
				JSONObject oPrimaryKeyMatchingData = aPrimaryKeyMatchingData.getJSONObject(nIndex);
				/* One field cannot be primary key and value field at same time */
				if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnPrimaryField")==true){

					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_JOIN_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				} else if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnValueField")==true) {
					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_VALUE_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);
				}
			}

			oJsonRetValue.put("Status", true);
			oJsonRetValue.put("Msg","Match criteria or configuration saved successfully");
		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	private void insertMatchCriteria(long nRuleId, JSONObject oPrimaryKeyMatchingData) {
		String sCriteriaInsertSqlTmpl = "";
		String sCriteriaInsertSql = "";
		String sRightColumnIdName = oPrimaryKeyMatchingData.getString("RightColumnIdAndName");

		long nLeftColumnId = Long.parseLong(oPrimaryKeyMatchingData.getString("LeftPrimaryColumnId"));
		long nRightColumnId = Long.parseLong(sRightColumnIdName.split("-")[0]);
		String sLeftColumnName = oPrimaryKeyMatchingData.getString("LeftColumnName");
		String sLeftColumnExpr = oPrimaryKeyMatchingData.getString("LeftColumnCustomizedExpr");
		String sRightColumnName = sRightColumnIdName.split("-")[1];
		String sRightColumnExpr = oPrimaryKeyMatchingData.getString("RightColumnCustomizedExpr");

		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "insert into listDMCriteria \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "(idDM, idLeftColumn, leftSideColumn, leftSideExp, idRightColumn, rightSideColumn, rightSideExp) \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "values \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "(%1$s, %2$s, '%3$s', '%4$s', %5$s, '%6$s', '%7$s') \n";

		sCriteriaInsertSql = String.format(sCriteriaInsertSqlTmpl, nRuleId, nLeftColumnId, sLeftColumnName, sLeftColumnExpr, nRightColumnId, sRightColumnName, sRightColumnExpr);
		jdbcTemplate.update(sCriteriaInsertSql);
	}


	private long isRuleOrCriteriaExists(String sIdApp) {
		long nRetValue = 0l;
		String sSelectSql = String.format("select count(*) as Count from listDMRules where idApp = %1$s;", sIdApp);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nRetValue = oSqlRowSet.next() ? oSqlRowSet.getLong("Count") : -1l;

		return nRetValue;
	}

	private long getRuleId(String sWhichRule, String sIdApp) {
		long nRetValue = 0l;
		String sSelectSql = String.format("select idDM as RuleId from listDMRules where idApp = %1$s and matchType2 = '%2$s';", sIdApp, sWhichRule);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nRetValue = oSqlRowSet.next() ? oSqlRowSet.getLong("RuleId") : -1l;

		return nRetValue;
	}

}
