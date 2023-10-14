package com.databuck.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDfTranRule;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.JsonDaoI;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.service.RuleCatalogService;

@Controller
public class JSONController {

	@Autowired
	private ITemplateViewDAO templateviewdao;

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private JsonDaoI jsonDaoI;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@RequestMapping(value = "/downloadJSONObject")
	public void downloadJSONObject(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam Long idApp) {
		try {
			System.out.println("idApp=" + idApp);
			String fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/dqscore" + idApp + ".json";
			// WriteToJSON
			JSONObject obj = prepareJSONData(idApp, null, 0, true);

			// try-with-resources statement based on post comment below :)
			try (FileWriter file = new FileWriter(
					System.getenv("DATABUCK_HOME") + "/csvFiles/dqscore" + idApp + ".json")) {
				file.write(obj.toString());
				System.out.println("Successfully Copied JSON Object to File...");
				System.out.println("\nJSON Object: " + obj);
			}

			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");
			System.out.println("appPath = " + appPath);

			// construct the complete absolute path of the file
			File downloadFile = new File(fileFullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fileFullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			System.out.println("MIME type: " + mimeType);

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", "dqscore" + idApp + ".json");
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[1024 * 1000];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public JSONObject prepareJSONData(Long idApp, String date, int currentRun, boolean flag) {

		JSONObject feDataQuality = new JSONObject();
		JSONObject jsonObj = new JSONObject();

		try {
			// Check it is test run by execution date and run
			boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, date, currentRun);

			// Check if the validation staging is enabled
			boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);

			// Fetching listApplications to add project, domain and other details
			ListApplications listApplications = null;
			if (isTestRun && validationStatingEnabled)
				listApplications = ruleCatalogDao.getDataFromStagingListapplications(idApp);
			else
				listApplications = validationcheckdao.getdatafromlistapplications(idApp);

			long idData = listApplications.getIdData();
			ListDataSource listDataSource = validationcheckdao.getdatafromlistdatasource(idData).get(0);
			listDataAccess listDataAccess = templateviewdao.getDataFromListDataAccess(idData).get(0);

			DecimalFormat df = new DecimalFormat("#0.0");

			// Get the test type
			String testType = "";
			if (listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")
					&& listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
				testType = "Historic";
			} else if (listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("N")
					&& listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
				testType = "Incremental";
			} else {
				testType = "Bulk Load";
			}

			String run = "";
			if (flag) {
				Map<String, String> map = jsonDaoI.getDateAndRunForSummaryOfLastRun(idApp);
				date = map.get("Date");
				run = map.get("Run");
			} else {
				run = String.valueOf(currentRun);
			}

			jsonObj.put("idApp", idApp);
			jsonObj.put("validationName", listApplications.getName());
			jsonObj.put("testType", testType);
			jsonObj.put("date", date);
			jsonObj.put("run", run);
			jsonObj.put("templateId", idData);
			jsonObj.put("templateName", listDataSource.getName());
			jsonObj.put("tableOrFileName", listDataAccess.getFolderName());

			Long connectionId = null;
			String connectionName = null;

			if (listDataSource != null && listDataSource.getIdDataSchema() != null) {
				List<ListDataSchema> listDataSchemas = listDataSourceDao
						.getListDataSchemaForIdDataSchema(listDataSource.getIdDataSchema());

				if (listDataSchemas != null && listDataSchemas.size() > 0) {
					connectionId = listDataSchemas.get(0).getIdDataSchema();
					connectionName = listDataSchemas.get(0).getSchemaName();
				}
			}
			jsonObj.put("connectionId", connectionId);
			jsonObj.put("connectionName", connectionName);

			JSONArray feRowDQArray = new JSONArray();
			JSONObject rca_feRowDQ = new JSONObject();

			// Record count Reasonability
			rca_feRowDQ.put("validationTest", "Record Count Fingerprint");
			rca_feRowDQ.put("definition", "Record count reasonability based on historical trends");
			double recordAnomalyScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Record Count Fingerprint");
			rca_feRowDQ.put("dqScore", df.format(recordAnomalyScore));
			feRowDQArray.put(rca_feRowDQ);

			// Null Check
			if (listApplications.getNonNullCheck() != null
					&& listApplications.getNonNullCheck().equalsIgnoreCase("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Null Count");
				feRowDQObj.put("definition", "Percentage of null fields in the selected group of columns");
				double nullCountScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Completeness");
				feRowDQObj.put("dqScore", df.format(nullCountScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Duplicate Check
			List<ListDfTranRule> listDfTranRule = jsonDaoI.getDataFromListDfTranRule(idApp);
			for (ListDfTranRule dups : listDfTranRule) {
				if (dups.getDupRow().equalsIgnoreCase("Y")) {

					// Identify the Duplicate check type - Identity
					if (dups.getType().equalsIgnoreCase("ALL")) {
						JSONObject feRowDQObj = new JSONObject();
						feRowDQObj.put("validationTest", "User Selected Fields Duplicate");
						feRowDQObj.put("definition",
								"Percentage of duplicates records based on user-selected non-primary keys");
						double allFieldsScore = jsonDaoI.getDQIByCheckName(idApp, date, run,
								"DQ_Uniqueness -Seleted Fields");
						feRowDQObj.put("dqScore", df.format(allFieldsScore));
						feRowDQArray.put(feRowDQObj);
					}

					// Identify the Duplicate check type -Identity
					else if (dups.getType().equalsIgnoreCase("IDENTITY")) {
						JSONObject feRowDQObj = new JSONObject();
						feRowDQObj.put("validationTest", "Primary Key Duplicate");
						feRowDQObj.put("definition", "Percentage of duplicates records based on primary keys");
						double identityFieldsScore = jsonDaoI.getDQIByCheckName(idApp, date, run,
								"DQ_Uniqueness -Primary Keys");
						feRowDQObj.put("dqScore", df.format(identityFieldsScore));
						feRowDQArray.put(feRowDQObj);
					}
				}
			}

			// Numerical Check
			if (listApplications.getNumericalStatCheck() != null
					&& listApplications.getNumericalStatCheck().equalsIgnoreCase("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Numerical Field Fingerprint");
				feRowDQObj.put("definition", "Reasonability of selected numerical fields based on historical trends");
				double numericalFieldScore = jsonDaoI.getDQIByCheckName(idApp, date, run,
						"DQ_Numerical Field Fingerprint");
				feRowDQObj.put("dqScore", df.format(numericalFieldScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Record Anomaly
			if (listApplications.getRecordAnomalyCheck() != null
					&& listApplications.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Record Anomaly Fingerprint");
				feRowDQObj.put("definition", "Percentage of records that are outliers");
				double recordFieldScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Record Anomaly");
				feRowDQObj.put("dqScore", df.format(recordFieldScore));
				feRowDQArray.put(feRowDQObj);
			}

			if (listApplications.getApplyRules() != null && listApplications.getApplyRules().equalsIgnoreCase("Y")) {

				// Rules
				JSONObject rules_feRowDQObj = new JSONObject();
				rules_feRowDQObj.put("validationTest", "Custom or Referential Rules");
				rules_feRowDQObj.put("definition", "Percentage of records failed the rules");
				double rulesScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Rules");
				rules_feRowDQObj.put("dqScore", df.format(rulesScore));
				feRowDQArray.put(rules_feRowDQObj);

				// Sql Rules
				JSONObject sql_feRowDQObj = new JSONObject();
				sql_feRowDQObj.put("validationTest", "SQL Rules");
				sql_feRowDQObj.put("definition", "Percentage of records failed the rules");
				double sqlRulesScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Sql_Rule");
				sql_feRowDQObj.put("dqScore", df.format(sqlRulesScore));
				feRowDQArray.put(sql_feRowDQObj);

				// Global rules
				JSONObject global_feRowDQObj = new JSONObject();
				global_feRowDQObj.put("validationTest", "Global Rules");
				global_feRowDQObj.put("definition", "Percentage of records failed the global rules");
				double globalRulesScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_GlobalRules");
				global_feRowDQObj.put("dqScore", df.format(globalRulesScore));
				feRowDQArray.put(global_feRowDQObj);
			}

			// Default check
			if (listApplications.getDefaultCheck() != null
					&& listApplications.getDefaultCheck().equalsIgnoreCase("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Default value check");
				feRowDQObj.put("definition", "Percentage of records that fail the default value check");
				double defaultCheckScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_DefaultCheck");
				feRowDQObj.put("dqScore", df.format(defaultCheckScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Length check
			if (listApplications.getlengthCheck() != null && listApplications.getlengthCheck().equals("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Length Check");
				feRowDQObj.put("definition", "Percentage of records failed the length validation");
				double lengthCheckScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_LengthCheck");
				feRowDQObj.put("dqScore", df.format(lengthCheckScore));
				feRowDQArray.put(feRowDQObj);
			}

			//Max Length Check
						if (listApplications.getMaxLengthCheck() != null && listApplications.getMaxLengthCheck().equals("Y")) {
							JSONObject feRowDQObj = new JSONObject();
							feRowDQObj.put("validationTest", "Max Length Check");
							feRowDQObj.put("definition", "Percentage of records failed the Max length validation");
							double maxLengthCheckScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_MaxLengthCheck");
							System.out.println("!!!!!!!!!!!!!!!!!!!!!!!maxLengthCheckScore ====="+maxLengthCheckScore);
							feRowDQObj.put("dqScore", df.format(maxLengthCheckScore));
							feRowDQArray.put(feRowDQObj);
						}
			
			// Pattern check
			if (listApplications.getPatternCheck() != null
					&& listApplications.getPatternCheck().equalsIgnoreCase("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Pattern Check");
				feRowDQObj.put("definition", "Percentage of records failed the regex pattern validation");
				double patternCheckScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Pattern_Data");
				feRowDQObj.put("dqScore", df.format(patternCheckScore));
				feRowDQArray.put(feRowDQObj);
			}

			// BadData Check
			if (listApplications.getBadData() != null && listApplications.getBadData().equals("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "BadData Check");
				feRowDQObj.put("definition", "Percentage of invalid data present in Numeric and String fields");
				double badDataScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Bad_Data");
				feRowDQObj.put("dqScore", df.format(badDataScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Timeliness check
			if (listApplications.getTimelinessKeyChk() != null && listApplications.getTimelinessKeyChk().equals("Y")) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Timeliness Check");
				feRowDQObj.put("definition", "Percentage of records failed in Timeliness check");
				double timelinessScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Timeliness");
				feRowDQObj.put("dqScore", df.format(timelinessScore));
				feRowDQArray.put(feRowDQObj);
			}

			// DateRule Check
			if ((listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y"))
					|| (listApplications.getdGroupDateRuleCheck() != null
							&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y"))) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "DateRule Check");
				feRowDQObj.put("definition", "Percentage of records failed in DateRule check");
				double dateRuleScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_DateRuleCheck");
				feRowDQObj.put("dqScore", df.format(dateRuleScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Data Drift
			if (listApplications.getDataDriftCheck() != null
					&& (listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
							|| listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y"))) {
				JSONObject feRowDQObj = new JSONObject();
				feRowDQObj.put("validationTest", "Data Drift");
				feRowDQObj.put("definition", "Changes in unique values of the selected string fields");
				double dataDriftScore = jsonDaoI.getDQIByCheckName(idApp, date, run, "DQ_Data Drift");
				feRowDQObj.put("dqScore", df.format(dataDriftScore));
				feRowDQArray.put(feRowDQObj);
			}

			// Aggregate DQI
			double aggregateDqi = jsonDaoI.getAggregateDQIForDataQualityDashboard(idApp, date, run);
			jsonObj.put("aggregateDqi", df.format(aggregateDqi));
			jsonObj.put("feRowDQ", feRowDQArray);

			feDataQuality.put("feDataQuality", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feDataQuality;
	}

	public Map<String, String> getSecureAPI() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM secure_API");
			while (queryForRowSet.next()) {
				map.put("accessTokenId", encryptor.decrypt(queryForRowSet.getString("accessTokenId")));
				map.put("secretAccessToken", encryptor.decrypt(queryForRowSet.getString("secretAccessToken")));
			}
		} catch (Exception e) {
		}
		return map;

	}

	public boolean checkDateAndRun(Long idApp, String date, int run) {
		try {
			ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
			String sql = "";
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				sql = "SELECT  * FROM  DATA_QUALITY_Transactionset_sum_A1 " + " WHERE idApp=" + idApp + " and DATE ='"
						+ date + "' and run=" + run + " LIMIT 1";
			} else {
				sql = "SELECT  * FROM  DATA_QUALITY_Transactionset_sum_dgroup " + " WHERE idApp=" + idApp
						+ " and DATE ='" + date + "' and run=" + run + " LIMIT 1";
			}
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
}