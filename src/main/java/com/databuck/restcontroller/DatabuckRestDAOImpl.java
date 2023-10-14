package com.databuck.restcontroller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.databuck.bean.DataQualityHistoricDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.controller.DataTemplateController;
import com.databuck.controller.JSONController;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.impl.ValidationCheckDAOImpl;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.MsSqlActiveDirectoryConnection;
import com.databuck.datatemplate.OracleConnection;
import com.databuck.datatemplate.OracleRACConnection;
import com.databuck.datatemplate.VerticaConnection;
import com.databuck.service.ITaskService;

@Component
public class DatabuckRestDAOImpl {
	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;
	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	OracleRACConnection OracleRACConnection;
	@Autowired
	ValidationCheckDAOImpl validationCheckDAOImpl;
	@Autowired
	CassandraConnection cassandraconnection;
	@Autowired
	public ITaskService iTaskService;
	@Autowired
	HiveConnection hiveconnection;
	@Autowired
	public IResultsDAO iResultsDAO;
	@Autowired
	DataTemplateController dataTemplateController;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;
	@Autowired
	ITemplateViewDAO templateviewdao;
	@Autowired
	MSSQLConnection mSSQLConnection;
	@Autowired
	JSONController jsoncontroller;
	@Autowired
	IValidationCheckDAO validationcheckdao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;

	private static final Logger LOG = Logger.getLogger(DatabuckRestDAOImpl.class);
	public JSONObject insertRecordInListAplication(ValidationCheckBean validationCheckBean,
			HttpServletResponse response) {
		LOG.info("insertRecordInListAplication - START");
		LOG.debug("Getting  parameters for validationCheckBean  " + validationCheckBean);
		JSONObject json = new JSONObject();
		try {
			Long idApp = 0l;
			String validationCheckName = validationCheckBean.getValidationCheckName();
			String validationCheckType = validationCheckBean.getValidationCheckType();
			String description = validationCheckBean.getDescription();
			String idData = validationCheckBean.getIdData();
			String fileNameValidation = validationCheckBean.getFileNameValidation();
			String colOrderValidation = validationCheckBean.getColOrderValidation();
			String nonNullCheck = validationCheckBean.getNonNullCheck();
			String numericalStatCheck = validationCheckBean.getNumericalStatCheck();
			String stringStatCheck = validationCheckBean.getStringStatCheck();
			String recordAnomalyCheck = validationCheckBean.getRecordAnomalyCheck();
			String incrementalColCheck = "N";
			String dataDriftCheck = validationCheckBean.getDataDriftCheck();
			String dataDriftThreshold = validationCheckBean.getDataDriftThreshold();
			String updateFrequency = validationCheckBean.getUpdateFrequency();
			String timeSeries = validationCheckBean.getTimeSeries();
			String recordCountAnomalyCheck = validationCheckBean.getRecordCountAnomalyCheck();
			String recordCountAnomalyThreshold = validationCheckBean.getRecordCountAnomalyThreshold();
			String keyGroupRecordCountAnomalyCheck = validationCheckBean.getKeyGroupRecordCountAnomalyCheck();
			String keyGroupRecordCountAnomalyThreshold = validationCheckBean.getKeyGroupRecordCountAnomalyThreshold();
			String outOfNormCheck = validationCheckBean.getOutOfNormCheck();
			String applyRules = validationCheckBean.getApplyRules();
			String applyDerivedColumns = validationCheckBean.getApplyDerivedColumns();
			String groupEqualityCheck = validationCheckBean.getGroupEqualityCheck();
			String groupEqualityThreshold = validationCheckBean.getGroupEqualityThreshold();
			String buildHistoricFingerPrint = "N";
			String historicStartDate = validationCheckBean.getHistoricStartDate();
			String historicEndDate = validationCheckBean.getHistoricEndDate();
			String historicDateFormat = validationCheckBean.getHistoricDateFormat();
			String nonNullThreshold = validationCheckBean.getNonNullThreshold();
			String numericalStatThreshold = validationCheckBean.getNumericalStatThreshold();
			String stringStatThreshold = validationCheckBean.getStringStatThreshold();
			String recordAnomalyThreshold = validationCheckBean.getRecordAnomalyThreshold();
			String duplicateRowId = validationCheckBean.getDuplicateRowId();
			String duplicateRowIdThreshold = validationCheckBean.getDuplicateRowIdThreshold();
			String duplicateRowAll = validationCheckBean.getDuplicateRowAll();
			String duplicateRowAllThreshold = validationCheckBean.getDuplicateRowAllThreshold();
			String duplicateFileCheck = validationCheckBean.getDuplicateFileCheck();
			SimpleDateFormat DFt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat iDFt = new SimpleDateFormat("yyyy-MM-dd");

			if ((keyGroupRecordCountAnomalyCheck != null && recordCountAnomalyCheck != null)) {
				if (keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("N")
						&& recordCountAnomalyCheck.trim().equalsIgnoreCase("N")) {
					recordCountAnomalyCheck = "Y";
					if (recordCountAnomalyThreshold == null) {
						recordCountAnomalyThreshold = "0.0";
					}
				}
				if (keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("Y")
						&& recordCountAnomalyCheck.trim().equalsIgnoreCase("Y")) {
					recordCountAnomalyCheck = "Y";
					keyGroupRecordCountAnomalyCheck = "N";
				}
			} else {
				recordCountAnomalyCheck = "Y";
				if (recordCountAnomalyThreshold == null) {
					recordCountAnomalyThreshold = "0.0";
				}
			}
			if (validationCheckName == null) {
				json.put("fail", "validationCheckName parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			if (validationCheckType == null) {
				json.put("fail", "validationCheckType parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			} else if (validationCheckType.trim().equalsIgnoreCase("Bulk Load")) {
				incrementalColCheck = "N";
				buildHistoricFingerPrint = "N";
				historicEndDate = "NULL";
				historicStartDate = "NULL";
				historicDateFormat = "''";
			} else if (validationCheckType.trim().equalsIgnoreCase("Historic")) {
				incrementalColCheck = "Y";
				buildHistoricFingerPrint = "Y";
				if (historicStartDate == null) {
					json.put("fail", "historic Start Date is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicStartDate = "'" + DFt.format(iDFt.parse(historicStartDate)) + "'";
				}
				if (historicEndDate == null) {
					json.put("fail", "historic End Date is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicEndDate = "'" + DFt.format(iDFt.parse(historicEndDate)) + "'";
				}
				if (historicDateFormat == null) {
					json.put("fail", "historic Date Format is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicDateFormat = "'" + historicDateFormat + "'";
				}
			} else if (validationCheckType.trim().equalsIgnoreCase("Incremental")) {
				incrementalColCheck = "Y";
				buildHistoricFingerPrint = "N";
				historicEndDate = "NULL";
				historicStartDate = "NULL";
				historicDateFormat = "''";
			} else {
				json.put("fail", "ValidationCheckType is invalid");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}

			if (idData == null) {
				json.put("fail", "idData parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}

			if (description == null) {
				description = "";
			}

			if (fileNameValidation == null) {
				fileNameValidation = "N";
			} else if (!(fileNameValidation.trim().equalsIgnoreCase("Y"))) {
				fileNameValidation = "N";
			}

			if (colOrderValidation == null) {
				colOrderValidation = "N";
			} else if (!(colOrderValidation.trim().equalsIgnoreCase("Y"))) {
				colOrderValidation = "N";
			}

			if (nonNullCheck == null) {
				nonNullCheck = "N";
				nonNullThreshold = "0.0";
			} else if (!(nonNullCheck.trim().equalsIgnoreCase("Y"))) {
				nonNullCheck = "N";
				nonNullThreshold = "0.0";
			} else if (nonNullThreshold == null) {
				nonNullThreshold = "0.0";
			}

			if (numericalStatCheck == null) {
				numericalStatCheck = "N";
				numericalStatThreshold = "0.0";
			} else if (!(numericalStatCheck.trim().equalsIgnoreCase("Y"))) {
				numericalStatCheck = "N";
				numericalStatThreshold = "0.0";
			} else if (numericalStatThreshold == null) {
				numericalStatThreshold = "0.0";
			}

			if (stringStatCheck == null) {
				stringStatCheck = "N";
				stringStatThreshold = "0.0";
			} else if (!(stringStatCheck.trim().equalsIgnoreCase("Y"))) {
				stringStatCheck = "N";
				stringStatThreshold = "0.0";
			} else if (stringStatThreshold == null) {
				stringStatThreshold = "0.0";
			}

			if (recordAnomalyCheck == null) {
				recordAnomalyCheck = "N";
				recordAnomalyThreshold = "3.0";
			} else if (!(recordAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				recordAnomalyCheck = "N";
				recordAnomalyThreshold = "3.0";
			} else if (recordAnomalyThreshold == null) {
				recordAnomalyThreshold = "3.0";
			}

			if (dataDriftCheck == null) {
				dataDriftCheck = "N";
				dataDriftThreshold = "0.0";
			} else if (!(dataDriftCheck.trim().equalsIgnoreCase("Y"))) {
				dataDriftCheck = "N";
				dataDriftThreshold = "0.0";
			} else if (dataDriftThreshold == null) {
				dataDriftThreshold = "0.0";
			}

			if (duplicateFileCheck == null) {
				duplicateFileCheck = "N";
			} else if (!(duplicateFileCheck.trim().equalsIgnoreCase("Y"))) {
				duplicateFileCheck = "N";
			}
			if (updateFrequency == null) {
				updateFrequency = "Never";
				timeSeries = "None";
			} else if (!(updateFrequency.trim().equalsIgnoreCase("Daily"))) {
				updateFrequency = "Never";
				timeSeries = "None";
			} else if (timeSeries == null) {
				json.put("fail", "timeSeries parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}

			if (recordCountAnomalyCheck == null) {
				recordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (!(recordCountAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				recordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (recordCountAnomalyThreshold == null) {
				recordCountAnomalyThreshold = "0.0";
			}

			if (keyGroupRecordCountAnomalyCheck == null) {
				keyGroupRecordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (!(keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				keyGroupRecordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (keyGroupRecordCountAnomalyThreshold == null) {
				recordCountAnomalyThreshold = "0.0";
			} else {
				recordCountAnomalyThreshold = keyGroupRecordCountAnomalyThreshold;
			}
			/*
			 * //pre check for dgroup
			 * if(keyGroupRecordCountAnomalyCheck.equalsIgnoreCase("Y")){
			 * boolean flag=true; List<ListDataDefinition> listdatadefinition =
			 * templateviewdao.view(Long.valueOf(validationCheckBean.getIdData()
			 * )); for (ListDataDefinition ldd : listdatadefinition) { if
			 * (ldd.getDgroup().equalsIgnoreCase("Y")) { flag=false; break; } }
			 * if(flag){ json.put("fail",
			 * "The subsegment check is configured incorrectly"); return json; }
			 * }
			 */
			if (outOfNormCheck == null) {
				outOfNormCheck = "N";
			} else if (!(outOfNormCheck.trim().equalsIgnoreCase("Y"))) {
				outOfNormCheck = "N";
			}

			if (applyRules == null) {
				applyRules = "N";
			} else if (!(applyRules.trim().equalsIgnoreCase("Y"))) {
				applyRules = "N";
			}

			if (applyDerivedColumns == null) {
				applyDerivedColumns = "N";
			} else if (!(applyDerivedColumns.trim().equalsIgnoreCase("Y"))) {
				applyDerivedColumns = "N";
			}

			if (duplicateRowId == null) {
				duplicateRowId = "N";
				duplicateRowIdThreshold = "0.0";
			} else if (!(duplicateRowId.trim().equalsIgnoreCase("Y"))) {
				duplicateRowId = "N";
				duplicateRowIdThreshold = "0.0";
			} else if (duplicateRowIdThreshold == null) {
				duplicateRowIdThreshold = "0.0";
			}

			if (duplicateRowAll == null) {
				duplicateRowAll = "N";
				duplicateRowAllThreshold = "0.0";
			} else if (!(duplicateRowAll.trim().equalsIgnoreCase("Y"))) {
				duplicateRowAll = "N";
				duplicateRowAllThreshold = "0.0";
			} else if (duplicateRowAllThreshold == null) {
				duplicateRowAllThreshold = "0.0";
			}

			if (groupEqualityCheck == null) {
				groupEqualityCheck = "N";
				groupEqualityThreshold = "0.0";
			} else if (!(groupEqualityCheck.trim().equalsIgnoreCase("Y"))) {
				groupEqualityCheck = "N";
				groupEqualityThreshold = "0.0";
			} else if (groupEqualityThreshold == null) {
				groupEqualityThreshold = "0.0";
			}
			try {
				String sql = "SELECT MAX(idApp) FROM listApplications";
				idApp = jdbcTemplate.queryForObject(sql, Long.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sql = "INSERT INTO listApplications (name, description, appType, idData, idRightData, createdBy, createdAt, "
					+ "updatedAt, updatedBy, fileNameValidation, entityColumn, colOrderValidation, matchingThreshold, nonNullCheck,"
					+ " numericalStatCheck, stringStatCheck, recordAnomalyCheck, incrementalMatching, incrementalTimestamp, dataDriftCheck,"
					+ " updateFrequency, frequencyDays, recordCountAnomaly, recordCountAnomalyThreshold, timeSeries, keyGroupRecordCountAnomaly,"
					+ " outOfNormCheck, applyRules, applyDerivedColumns, csvDir, groupEquality, groupEqualityThreshold, buildHistoricFingerPrint,"
					+ " historicStartDate, historicEndDate, historicDateFormat, active) VALUES" + "('"
					+ (idApp + 1) + "_" + validationCheckName + "', '" + description + "', 'Data Forensics', " + idData
					+ ",NULL, 1, '" + DFt.format(new Date()) + "', '" + DFt.format(new Date()) + "',1,'"
					+ fileNameValidation + "', '', '" + colOrderValidation + "', 0, '" + nonNullCheck + "', '"
					+ numericalStatCheck + "', '" + stringStatCheck + "', '" + recordAnomalyCheck + "'," + " '"
					+ incrementalColCheck + "', NULL,'" + dataDriftCheck + "', '" + updateFrequency + "', 0, '"
					+ recordCountAnomalyCheck + "', " + recordCountAnomalyThreshold + ", " + "'" + timeSeries + "', '"
					+ keyGroupRecordCountAnomalyCheck + "', '" + outOfNormCheck + "', '" + applyRules + "', '"
					+ applyDerivedColumns + "', '', '" + groupEqualityCheck + "', " + groupEqualityThreshold + ", "
					+ "'" + buildHistoricFingerPrint + "', " + historicStartDate + ", " + historicEndDate + ", "
					+ historicDateFormat + ", 'yes')";
			int laStatus = jdbcTemplate.update(sql);
			int dupIdStatus = 0;
			int dupAllStatus = 0;
			int setRuleStatus = 0;

			if (duplicateRowAll.trim().equalsIgnoreCase("Y")) {
				sql = "INSERT INTO listDFTranRule ( idApp, dupRow, seqRow, seqIDcol, threshold, type) VALUES"
						+ "( " + (idApp + 1) + ", 'Y', 'N', 0, " + duplicateRowAllThreshold + ", 'all')";
				dupIdStatus = jdbcTemplate.update(sql);
			} else {
				sql = "INSERT INTO listDFTranRule ( idApp, dupRow, seqRow, seqIDcol, threshold, type) VALUES"
						+ "( " + (idApp + 1) + ", 'N', 'N', 0, " + duplicateRowAllThreshold + ", 'all')";
				dupIdStatus = jdbcTemplate.update(sql);
			}
			if (duplicateRowId.trim().equalsIgnoreCase("Y")) {
				sql = "INSERT INTO listDFTranRule ( idApp, dupRow, seqRow, seqIDcol, threshold, type) VALUES"
						+ "( " + (idApp + 1) + ", 'Y', 'N', 0, " + duplicateRowIdThreshold + ", 'identity')";
				dupAllStatus = jdbcTemplate.update(sql);
			} else {
				sql = "INSERT INTO listDFTranRule ( idApp, dupRow, seqRow, seqIDcol, threshold, type) VALUES"
						+ "( " + (idApp + 1) + ", 'N', 'N', 0, " + duplicateRowIdThreshold + ", 'identity')";
				dupAllStatus = jdbcTemplate.update(sql);
			}
			if (duplicateFileCheck.trim().equalsIgnoreCase("Y")) {
				sql = "INSERT INTO listDFSetRule (idApp, count, sum, correlation, statisticalParam, duplicateFile) VALUES"
						+ "( " + (idApp + 1) + ",'Y', 'Y', 'Y', 'Y', 'Y')";
				setRuleStatus = jdbcTemplate.update(sql);
			} else {
				sql = "INSERT INTO listDFSetRule (idApp, count, sum, correlation, statisticalParam, duplicateFile) VALUES"
						+ "( " + (idApp + 1) + ",'Y', 'Y', 'Y', 'Y', 'N')";
				setRuleStatus = jdbcTemplate.update(sql);
			}

			// update threshold in listdatadefinition..
			sql = "UPDATE listDataDefinition SET nullCountThreshold=" + nonNullThreshold + ", numericalThreshold="
					+ numericalStatThreshold + ", stringStatThreshold=" + stringStatThreshold + ", "
					+ "recordAnomalyThreshold=" + recordAnomalyThreshold + ",dataDriftThreshold=" + dataDriftThreshold
					+ " WHERE idData=" + idData;
			int lddStatus = jdbcTemplate.update(sql);

			if ((setRuleStatus != 0) && (laStatus != 0) && (dupIdStatus != 0) && (dupAllStatus != 0)
					&& (lddStatus != 0)) {
				json.put("idData", idData);
				json.put("idApp", idApp + 1);
				json.put("success", "Validation Check Created Successfully");
			} else {
				json.put("fail", "Validation Check failed,Please check Configuration");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Validation Check failed,Please check Configuration");
			return json;
		}
		LOG.info("insertRecordInListAplication - END");
		return json;
	}

	/*public JSONObject createTemplate(String dataTemplateName, String dataLocation, String description,
			Long idDataSchema, String tableName, HttpServletResponse response, HttpSession session,
			List<ListDataDefinitionBean> metaData, String dataFormat, String hostURI2, String folder, String userLogin2,
			String password2) {
		JSONObject json = new JSONObject();
		try {
			Map<String, List<ListDataDefinition>> mapDataDefinition = (Map) session.getAttribute("dataDefinition");
			String HostURI = "", databaseSchema = "", userlogin = "", password = "", portName = "", domain = "",
					serviceName = "";
			if (dataLocation.equals("File System") || dataLocation.equals("HDFS") || dataLocation.equals("S3")
					|| dataLocation.equals("MapR FS") || dataLocation.equals("MapR DB")) {
			} else {
				List<ListDataSchema> listDataSchema = DataTemplateAddNewDAO.getListDataSchema(idDataSchema);
				HostURI = listDataSchema.get(0).getIpAddress();
				databaseSchema = listDataSchema.get(0).getDatabaseSchema();
				userlogin = listDataSchema.get(0).getUsername();
				password = listDataSchema.get(0).getPassword();
				portName = listDataSchema.get(0).getPort();
				domain = listDataSchema.get(0).getDomain();
				serviceName = ((ListDataSchema) listDataSchema.get(0)).getKeytab();
			}
			ListDataSource listDataSource = new ListDataSource();
			listDataSource.setDescription(description);
			listDataSource.setDataLocation(dataLocation);
			listDataSource.setDataSource("SQL");
			listDataSource.setCreatedAt(new Date());
			listDataSource.setCreatedBy(1l);
			listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
			listDataSource.setGarbageRows(0l);
			listDataSource.setName(dataTemplateName + "_" + tableName);
			listDataSource.setTableName(tableName);
			listDataAccess listdataAccess = new listDataAccess();
			listdataAccess.setHostName(HostURI);
			listdataAccess.setPortName(portName);
			listdataAccess.setUserName(userlogin);
			listdataAccess.setPwd(password);
			listdataAccess.setSchemaName(databaseSchema);
			listdataAccess.setQueryString("");
			listdataAccess.setFolderName(tableName);
			listdataAccess.setIdDataSchema(idDataSchema);
			listdataAccess.setWhereCondition("");
			listdataAccess.setDomain(domain);
			listdataAccess.setQuery("");
			listdataAccess.setIncrementalType("");
			listdataAccess.setDateFormat("");
			listdataAccess.setSliceStart("");
			listdataAccess.setSliceEnd("");
			listdataAccess.setFileHeader("");
			listdataAccess.setFolderName(tableName);
			listDataSource.setName(dataTemplateName + "_" + tableName);
			if (dataLocation.equals("MSSQLActiveDirectory")) {
				Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, databaseSchema,
						userlogin, password, tableName, portName, domain);
				LinkedHashMap readTablesFromMSSQLActiveDirectory = (LinkedHashMap) arr[0];
				List<String> primarykeyCols = (ArrayList<String>) arr[1];
				if (readTablesFromMSSQLActiveDirectory.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource,
						readTablesFromMSSQLActiveDirectory, listdataAccess, primarykeyCols, lstDataDefinition,
						metaData, );
				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			} else if (dataLocation.equals("Vertica")) {

				Object[] obj = verticaconnection.verticaconnection(HostURI, databaseSchema, userlogin, password,
						tableName, portName);
				LinkedHashMap readTablesFromVertica = (LinkedHashMap) obj[0];
				List<String> primarykeyCols = (List<String>) obj[1];
				if (readTablesFromVertica.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromVertica,
						listdataAccess, primarykeyCols, lstDataDefinition, metaData);
				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;

			} else if (dataLocation.equals("MSSQL")) {

				Object[] arr = mSSQLConnection.readTablesFromMSSQL(HostURI, databaseSchema, userlogin, password,
						tableName, portName);
				LinkedHashMap readTablesFromMYSQL = (LinkedHashMap) arr[0];
				List<String> primarykeyCols = (List<String>) arr[1];
				if (readTablesFromMYSQL.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromMYSQL,
						listdataAccess, primarykeyCols, lstDataDefinition, metaData);
				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;

			} else if (dataLocation.equals("Cassandra")) {

				Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, databaseSchema, userlogin, password,
						tableName, portName);
				Map<String, String> readTablesFromCassandra = (Map<String, String>) arr[0];
				List<String> primarykeyCols = (List<String>) arr[1];
				if (readTablesFromCassandra.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromCassandra,
						listdataAccess, primarykeyCols, lstDataDefinition, metaData);

				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;

			} else if (dataLocation.equals("Oracle")) {

				Map readTablesFromOracle = oracleconnection.readTablesFromOracle(HostURI, databaseSchema, userlogin,
						password, tableName, portName);
				if (readTablesFromOracle.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<String> primaryKeyCols = oracleconnection.readPrimaryKeyColumnsFromOracle(HostURI, databaseSchema,
						userlogin, password, tableName, portName);

				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromOracle,
						listdataAccess, primaryKeyCols, lstDataDefinition, metaData);

				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			}

			else if (dataLocation.equals("Oracle RAC")) {
				Map readTablesFromOracleRAC = OracleRACConnection.readTablesFromOracleRAC(HostURI, databaseSchema,
						userlogin, password, tableName, portName, serviceName);
				if (readTablesFromOracleRAC.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<String> primaryKeyCols = OracleRACConnection.readPrimaryKeyColumnsFromOracleRAC(HostURI,
						databaseSchema, userlogin, password, tableName, portName, serviceName);

				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromOracleRAC,
						listdataAccess, primaryKeyCols, lstDataDefinition, metaData);

				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			}

			else if (dataLocation.equals("Hive")) {
				System.out.println(" Hive ");
				HiveSource hivesource = new HiveSource();
				hivesource.setName(dataTemplateName);
				hivesource.setDescription(description);
				hivesource.setIdDataSchema(idDataSchema);
				hivesource.setTableName(tableName);
				int insertDataIntoHiveSource = DataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

				Map readTablesFromHive = hiveconnection.readTablesFromHive(HostURI, databaseSchema, userlogin, password,
						tableName, portName);
				if (readTablesFromHive.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<String> primarykeyCols = new ArrayList<String>();
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}
				System.out.println("tableName=" + tableName);
				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromHive,
						listdataAccess, primarykeyCols, lstDataDefinition, metaData);

				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			} else if (dataLocation.equalsIgnoreCase("Amazon Redshift")) {
				System.out.println("Amazon Redshift ");
				HiveSource hivesource = new HiveSource();
				hivesource.setName(dataTemplateName);
				hivesource.setDescription(description);
				hivesource.setIdDataSchema(idDataSchema);
				hivesource.setTableName(tableName);
				int insertDataIntoHiveSource = DataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

				Map readTablesFromAmazon = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI,
						databaseSchema, userlogin, password, tableName, portName);

				if (readTablesFromAmazon.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				List<String> primarykeyCols = new ArrayList();
				List<ListDataDefinition> lstDataDefinition = null;
				try {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromAmazon,
						listdataAccess, primarykeyCols, lstDataDefinition, metaData);

				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			}
			String principle;
			String keytab;
			String krb5conf;
			if (dataLocation.equalsIgnoreCase("Hive Kerberos")) {
				System.out.println("Hive Kerberos");
				HiveSource hivesource = new HiveSource();
				hivesource.setName(dataTemplateName);
				hivesource.setDescription(description);
				hivesource.setIdDataSchema(idDataSchema);
				hivesource.setTableName(tableName);
				Map readTablesFromHive = new HashMap();
				List<String> primarykeyCols = new ArrayList<String>();

				Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromHive,
						listdataAccess, primarykeyCols, null);
				System.out.println("idData=" + idData);
				try {
					System.out
							.println("java -jar " + System.getenv("DATABUCK_HOME") + "/hive-kerberos-dt.jar " + idData);
					Process proc = Runtime.getRuntime()
							.exec("java -jar " + System.getenv("DATABUCK_HOME") + "/hive-kerberos-dt.jar " + idData);
					proc.waitFor();

					InputStream in = proc.getInputStream();
					InputStream err = proc.getErrorStream();

					byte[] b = new byte[in.available()];
					in.read(b, 0, b.length);
					System.out.println(new String(b));

					byte[] c = new byte[err.available()];
					err.read(c, 0, c.length);
					System.out.println(new String(c));

				}

				catch (Exception e) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				json.put("idData", idData);
				json.put("success", "Data Template Created Successfully");
				return json;
			} else if (dataLocation.equals("File System") || dataLocation.equals("HDFS") || dataLocation.equals("S3")
					|| dataLocation.equals("MapR FS") || dataLocation.equals("MapR DB")) {
				listDataSource.setName(dataTemplateName);
				listDataSource.setDescription(description);
				if (dataLocation.equals("File System"))
					listDataSource.setDataLocation("FILESYSTEM");
				else
					listDataSource.setDataLocation(dataLocation);
				listDataSource.setDataSource(dataFormat);
				listDataSource.setCreatedAt(new Date());
				listDataSource.setCreatedBy(1l);
				listDataSource.setIdDataSchema(0l);
				listDataSource.setGarbageRows(0l);

				listdataAccess.setHostName(hostURI2);
				listdataAccess.setPortName("");
				listdataAccess.setUserName(userLogin2);
				listdataAccess.setPwd(password2);
				listdataAccess.setSchemaName("");
				listdataAccess.setFolderName(folder);
				listdataAccess.setIdDataSchema(0);
				listdataAccess.setFileHeader("");

				List<String> datatypes = new ArrayList<String>();
				datatypes.add("int");
				datatypes.add("char");
				datatypes.add("long");
				datatypes.add("float");
				datatypes.add("double");
				datatypes.add("varchar");
				datatypes.add("text");
				datatypes.add("string");
				datatypes.add("date");
				datatypes.add("number");
				Map<String, String> hm = new LinkedHashMap<String, String>();
				List<String> primarykeyCols = new ArrayList<String>();
				for (ListDataDefinitionBean ldd : metaData) {
					System.out.println();
					hm.put(ldd.getColumnName(), ldd.getColumnType());
				}
				if (hm.isEmpty()) {
					json.put("fail", "Data Template failed,Please check Configuration");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {

					List<ListDataDefinition> lstDataDefinition = null;
					try {
						lstDataDefinition = (List) mapDataDefinition.get(folder);
					} catch (Exception e) {
						// TODO: handle exception
					}
					Long idData = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, hm, listdataAccess,
							primarykeyCols, lstDataDefinition, metaData);
					
					 * Long idData =
					 * DataTemplateAddNewDAO.addintolistdatasource(
					 * listDataSource, hm, listdataAccess, primarykeyCols,
					 * null);
					 
					json.put("idData", idData);
					json.put("success", "Data Template Created Successfully");
					return json;
				}
			}
			json.put("fail", "Data Template failed,Please check Configuration");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (Exception e) {
			json.put("fail", "Data Template failed,Please check Configuration");
			return json;
		}
		return json;
	}*/

	public ValidationCheckBean createAutoMaticValidationCheckObject(JSONObject json, String dataTemplateName) {
		LOG.info("createAutoMaticValidationCheckObject - START");
		LOG.debug("Getting  parameters for dataTemplateName  " + dataTemplateName+"   "+json);
		ValidationCheckBean validationCheckBean = new ValidationCheckBean();
		try {
			String sql = "";
			Long idData = (Long) json.get("idData");
			
			LOG.debug("idData=" + idData);
			sql = "SELECT max(idApp+1) FROM listApplications";
			validationCheckBean
					.setValidationCheckName(jdbcTemplate.queryForObject(sql, Long.class) + "_" + dataTemplateName);
			validationCheckBean.setIdData(idData.toString());
			validationCheckBean.setValidationCheckType("Bulk load");
			validationCheckBean.setRecordCountAnomalyCheck("Y");

			// PreChecks
			List<ListDataDefinition> listdatadefinition = templateviewdao
					.view(Long.valueOf(validationCheckBean.getIdData()));
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDgroup().equalsIgnoreCase("Y")) {
					validationCheckBean.setRecordCountAnomalyCheck("N");
					validationCheckBean.setKeyGroupRecordCountAnomalyCheck("Y");
					break;
				}
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND primaryKey='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setDuplicateRowId("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND nonNull='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setNonNullCheck("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND dupkey='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setDuplicateRowAll("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND numericalStat='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setNumericalStatCheck("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND stringStat='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setStringStatCheck("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND dataDrift='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setDataDriftCheck("Y");
			}
			sql = "SELECT count(*) FROM listDataDefinition WHERE idData=" + idData + " AND recordAnomaly='Y'";
			if (jdbcTemplate.queryForObject(sql, Long.class) != 0) {
				validationCheckBean.setRecordAnomalyCheck("Y");
			}
			
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();

		}
		LOG.info("createAutoMaticValidationCheckObject - END");
		return validationCheckBean;
	}

	public JSONObject updateRecordInListAplication(ValidationCheckBean validationCheckBean,
			HttpServletResponse response) {
		LOG.info("updateRecordInListAplication - START");
		LOG.debug("Getting  parameters for validationCheckBean  " + validationCheckBean );

		JSONObject json = new JSONObject();
		try {
			String idApp = validationCheckBean.getIdApp();
			String validationCheckName = validationCheckBean.getValidationCheckName();
			String validationCheckType = validationCheckBean.getValidationCheckType();
			String description = validationCheckBean.getDescription();
			String fileNameValidation = validationCheckBean.getFileNameValidation();
			String colOrderValidation = validationCheckBean.getColOrderValidation();
			String nonNullCheck = validationCheckBean.getNonNullCheck();
			String numericalStatCheck = validationCheckBean.getNumericalStatCheck();
			String stringStatCheck = validationCheckBean.getStringStatCheck();
			String recordAnomalyCheck = validationCheckBean.getRecordAnomalyCheck();
			String incrementalColCheck = "N";
			String dataDriftCheck = validationCheckBean.getDataDriftCheck();
			String dataDriftThreshold = validationCheckBean.getDataDriftThreshold();
			String updateFrequency = validationCheckBean.getUpdateFrequency();
			String timeSeries = validationCheckBean.getTimeSeries();
			String recordCountAnomalyCheck = validationCheckBean.getRecordCountAnomalyCheck();
			String recordCountAnomalyThreshold = validationCheckBean.getRecordCountAnomalyThreshold();
			String keyGroupRecordCountAnomalyCheck = validationCheckBean.getKeyGroupRecordCountAnomalyCheck();
			String keyGroupRecordCountAnomalyThreshold = validationCheckBean.getKeyGroupRecordCountAnomalyThreshold();
			String outOfNormCheck = validationCheckBean.getOutOfNormCheck();
			String applyRules = validationCheckBean.getApplyRules();
			String applyDerivedColumns = validationCheckBean.getApplyDerivedColumns();
			String groupEqualityCheck = validationCheckBean.getGroupEqualityCheck();
			String groupEqualityThreshold = validationCheckBean.getGroupEqualityThreshold();
			String buildHistoricFingerPrint = "N";
			String historicStartDate = validationCheckBean.getHistoricStartDate();
			String historicEndDate = validationCheckBean.getHistoricEndDate();
			String historicDateFormat = validationCheckBean.getHistoricDateFormat();
			String nonNullThreshold = validationCheckBean.getNonNullThreshold();
			String numericalStatThreshold = validationCheckBean.getNumericalStatThreshold();
			String stringStatThreshold = validationCheckBean.getStringStatThreshold();
			String recordAnomalyThreshold = validationCheckBean.getRecordAnomalyThreshold();
			String duplicateRowId = validationCheckBean.getDuplicateRowId();
			String duplicateRowIdThreshold = validationCheckBean.getDuplicateRowIdThreshold();
			String duplicateRowAll = validationCheckBean.getDuplicateRowAll();
			String duplicateRowAllThreshold = validationCheckBean.getDuplicateRowAllThreshold();
			String duplicateFileCheck = validationCheckBean.getDuplicateFileCheck();
			SimpleDateFormat DFt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat iDFt = new SimpleDateFormat("yyyy-MM-dd");

			if (idApp == null) {
				json.put("fail", "idApp parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			if ((keyGroupRecordCountAnomalyCheck != null && recordCountAnomalyCheck != null)) {
				if (keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("N")
						&& recordCountAnomalyCheck.trim().equalsIgnoreCase("N")) {
					recordCountAnomalyCheck = "Y";
					if (recordCountAnomalyThreshold == null) {
						recordCountAnomalyThreshold = "0.0";
					}
				}
				if (keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("Y")
						&& recordCountAnomalyCheck.trim().equalsIgnoreCase("Y")) {
					recordCountAnomalyCheck = "Y";
					keyGroupRecordCountAnomalyCheck = "N";
				}
			} else {
				recordCountAnomalyCheck = "Y";
				if (recordCountAnomalyThreshold == null) {
					recordCountAnomalyThreshold = "0.0";
				}
			}
			if (validationCheckType == null) {
				json.put("fail", "validationCheckType parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			} else if (validationCheckType.trim().equalsIgnoreCase("Bulk Load")) {
				incrementalColCheck = "N";
				buildHistoricFingerPrint = "N";
				historicEndDate = null;
				historicStartDate = null;
				historicDateFormat = "''";
			} else if (validationCheckType.trim().equalsIgnoreCase("Historic")) {
				incrementalColCheck = "Y";
				buildHistoricFingerPrint = "Y";
				if (historicStartDate == null) {
					json.put("fail", "historic Start Date is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicStartDate = DFt.format(iDFt.parse(historicStartDate));
				}
				if (historicEndDate == null) {
					json.put("fail", "historic End Date is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicEndDate = DFt.format(iDFt.parse(historicEndDate));
				}
				if (historicDateFormat == null) {
					json.put("fail", "historic Date Format is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				} else {
					historicDateFormat = historicDateFormat;
				}
			} else if (validationCheckType.trim().equalsIgnoreCase("Incremental")) {
				incrementalColCheck = "Y";
				buildHistoricFingerPrint = "N";
				historicEndDate = null;
				historicStartDate = null;
				historicDateFormat = "''";
			} else {
				json.put("fail", "ValidationCheckType is invalid");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			if (fileNameValidation == null) {
				fileNameValidation = "N";
			} else if (!(fileNameValidation.trim().equalsIgnoreCase("Y"))) {
				fileNameValidation = "N";
			}

			if (colOrderValidation == null) {
				colOrderValidation = "N";
			} else if (!(colOrderValidation.trim().equalsIgnoreCase("Y"))) {
				colOrderValidation = "N";
			}

			if (nonNullCheck == null) {
				nonNullCheck = "N";
				nonNullThreshold = "0.0";
			} else if (!(nonNullCheck.trim().equalsIgnoreCase("Y"))) {
				nonNullCheck = "N";
				nonNullThreshold = "0.0";
			} else if (nonNullThreshold == null) {
				nonNullThreshold = "0.0";
			}

			if (numericalStatCheck == null) {
				numericalStatCheck = "N";
				numericalStatThreshold = "0.0";
			} else if (!(numericalStatCheck.trim().equalsIgnoreCase("Y"))) {
				numericalStatCheck = "N";
				numericalStatThreshold = "0.0";
			} else if (numericalStatThreshold == null) {
				numericalStatThreshold = "0.0";
			}

			if (stringStatCheck == null) {
				stringStatCheck = "N";
				stringStatThreshold = "0.0";
			} else if (!(stringStatCheck.trim().equalsIgnoreCase("Y"))) {
				stringStatCheck = "N";
				stringStatThreshold = "0.0";
			} else if (stringStatThreshold == null) {
				stringStatThreshold = "0.0";
			}

			if (recordAnomalyCheck == null) {
				recordAnomalyCheck = "N";
				recordAnomalyThreshold = "3.0";
			} else if (!(recordAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				recordAnomalyCheck = "N";
				recordAnomalyThreshold = "3.0";
			} else if (recordAnomalyThreshold == null) {
				recordAnomalyThreshold = "3.0";
			}

			if (dataDriftCheck == null) {
				dataDriftCheck = "N";
				dataDriftThreshold = "0.0";
			} else if (!(dataDriftCheck.trim().equalsIgnoreCase("Y"))) {
				dataDriftCheck = "N";
				dataDriftThreshold = "0.0";
			} else if (dataDriftThreshold == null) {
				dataDriftThreshold = "0.0";
			}

			if (duplicateFileCheck == null) {
				duplicateFileCheck = "N";
			} else if (!(duplicateFileCheck.trim().equalsIgnoreCase("Y"))) {
				duplicateFileCheck = "N";
			}
			if (updateFrequency == null) {
				updateFrequency = "Never";
				timeSeries = "None";
			} else if (!(updateFrequency.trim().equalsIgnoreCase("Daily"))) {
				updateFrequency = "Never";
				timeSeries = "None";
			} else if (timeSeries == null) {
				json.put("fail", "timeSeries parameter is missing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}

			if (recordCountAnomalyCheck == null) {
				recordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (!(recordCountAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				recordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (recordCountAnomalyThreshold == null) {
				recordCountAnomalyThreshold = "0.0";
			}

			if (keyGroupRecordCountAnomalyCheck == null) {
				keyGroupRecordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (!(keyGroupRecordCountAnomalyCheck.trim().equalsIgnoreCase("Y"))) {
				keyGroupRecordCountAnomalyCheck = "N";
				recordCountAnomalyThreshold = "0.0";
			} else if (keyGroupRecordCountAnomalyThreshold == null) {
				recordCountAnomalyThreshold = "0.0";
			} else {
				recordCountAnomalyThreshold = keyGroupRecordCountAnomalyThreshold;
			}

			if (outOfNormCheck == null) {
				outOfNormCheck = "N";
			} else if (!(outOfNormCheck.trim().equalsIgnoreCase("Y"))) {
				outOfNormCheck = "N";
			}

			if (applyRules == null) {
				applyRules = "N";
			} else if (!(applyRules.trim().equalsIgnoreCase("Y"))) {
				applyRules = "N";
			}

			if (applyDerivedColumns == null) {
				applyDerivedColumns = "N";
			} else if (!(applyDerivedColumns.trim().equalsIgnoreCase("Y"))) {
				applyDerivedColumns = "N";
			}

			if (duplicateRowId == null) {
				duplicateRowId = "N";
				duplicateRowIdThreshold = "0.0";
			} else if (!(duplicateRowId.trim().equalsIgnoreCase("Y"))) {
				duplicateRowId = "N";
				duplicateRowIdThreshold = "0.0";
			} else if (duplicateRowIdThreshold == null) {
				duplicateRowIdThreshold = "0.0";
			}

			if (duplicateRowAll == null) {
				duplicateRowAll = "N";
				duplicateRowAllThreshold = "0.0";
			} else if (!(duplicateRowAll.trim().equalsIgnoreCase("Y"))) {
				duplicateRowAll = "N";
				duplicateRowAllThreshold = "0.0";
			} else if (duplicateRowAllThreshold == null) {
				duplicateRowAllThreshold = "0.0";
			}

			if (groupEqualityCheck == null) {
				groupEqualityCheck = "N";
				groupEqualityThreshold = "0.0";
			} else if (!(groupEqualityCheck.trim().equalsIgnoreCase("Y"))) {
				groupEqualityCheck = "N";
				groupEqualityThreshold = "0.0";
			} else if (groupEqualityThreshold == null) {
				groupEqualityThreshold = "0.0";
			}

			String sql = "update listApplications set fileNameValidation=?, colOrderValidation=?,"
					+ "  nonNullCheck=?,"
					+ " numericalStatCheck=?, stringStatCheck=?, recordAnomalyCheck=?, incrementalMatching=?, "
					+ " dataDriftCheck=?,"
					+ " updateFrequency=?, recordCountAnomaly=?, recordCountAnomalyThreshold=?, "
					+ "timeSeries=?, keyGroupRecordCountAnomaly=?,"
					+ " outOfNormCheck=?, applyRules=?, applyDerivedColumns=?, groupEquality=?, "
					+ "groupEqualityThreshold=?, buildHistoricFingerPrint=?,"
					+ " historicStartDate=?, historicEndDate=?, historicDateFormat=? where idApp=" + idApp;
			int laStatus = jdbcTemplate.update(sql, fileNameValidation, colOrderValidation, nonNullCheck,
					numericalStatCheck, stringStatCheck, recordAnomalyCheck, incrementalColCheck, dataDriftCheck,
					updateFrequency, recordCountAnomalyCheck, recordCountAnomalyThreshold, timeSeries,
					keyGroupRecordCountAnomalyCheck, outOfNormCheck, applyRules, applyDerivedColumns,
					groupEqualityCheck, groupEqualityThreshold, buildHistoricFingerPrint, historicStartDate,
					historicEndDate, historicDateFormat);
			int dupIdStatus = 0;
			int dupAllStatus = 0;
			int setRuleStatus = 0;

			if (duplicateRowAll.trim().equalsIgnoreCase("Y")) {
				sql = "update listDFTranRule set threshold=" + duplicateRowAllThreshold + " , dupRow='Y' "
						+ "where idApp=" + idApp + " and  type='all'";
				dupIdStatus = jdbcTemplate.update(sql);
			} else {
				sql = "update listDFTranRule set threshold=" + duplicateRowAllThreshold + " , dupRow='N' "
						+ "where idApp=" + idApp + " and  type='all'";
				dupIdStatus = jdbcTemplate.update(sql);
			}
			if (duplicateRowId.trim().equalsIgnoreCase("Y")) {
				sql = "update listDFTranRule set threshold=" + duplicateRowIdThreshold + " , dupRow='Y' "
						+ "where idApp=" + idApp + " and  type='identity'";
				dupAllStatus = jdbcTemplate.update(sql);
			} else {
				sql = "update listDFTranRule set threshold=" + duplicateRowIdThreshold + " , dupRow='N' "
						+ "where idApp=" + idApp + " and  type='identity'";
				dupAllStatus = jdbcTemplate.update(sql);
			}
			if (duplicateFileCheck.trim().equalsIgnoreCase("Y")) {
				sql = "update listDFSetRule set duplicateFile='Y' where idApp=" + idApp;
				setRuleStatus = jdbcTemplate.update(sql);
			} else {
				sql = "update listDFSetRule set duplicateFile='N' where idApp=" + idApp;
				setRuleStatus = jdbcTemplate.update(sql);
			}
			Long idData = jdbcTemplate.queryForObject("select idData from listApplications where idApp=" + idApp,
					Long.class);

			// update threshold in listdatadefinition..
			sql = "UPDATE listDataDefinition SET nullCountThreshold=" + nonNullThreshold + ", numericalThreshold="
					+ numericalStatThreshold + ", stringStatThreshold=" + stringStatThreshold + ", "
					+ "recordAnomalyThreshold=" + recordAnomalyThreshold + ",dataDriftThreshold=" + dataDriftThreshold
					+ " WHERE idData=" + idData;
			int lddStatus = jdbcTemplate.update(sql);

			
			LOG.debug(setRuleStatus + "  " + laStatus + "  " + dupIdStatus + "  " + dupAllStatus + "  " + lddStatus);

			if ((setRuleStatus != 0) && (laStatus != 0) && (dupIdStatus != 0) && (dupAllStatus != 0)
					&& (lddStatus != 0)) {
				json.put("idData", idData);
				json.put("idApp", idApp);
				json.put("success", "Validation Check Updated Successfully");
			} else {
				json.put("fail", "Validation Check failed,Please check Configuration");
			}

		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
			json.put("fail", "Validation Check failed,Please check Configuration");
			return json;
		}
		LOG.info("updateRecordInListAplication - END");
		return json;

	}

	public JSONObject viewValidationCheck(long idApp) {
		LOG.info("viewValidationCheck - START");
		LOG.debug("Getting  parameters for idApp  " + idApp);
		ListApplications la = validationcheckdao.getdatafromlistapplications(idApp);
		JSONObject obj = new JSONObject();
		obj.put("idApp", la.getIdApp());
		obj.put("validationCheckName", la.getName());
		if (la.getIncrementalMatching().trim().equalsIgnoreCase("Y")) {
			if (la.getBuildHistoricFingerPrint().trim().equalsIgnoreCase("Y")) {
				obj.put("validationCheckType", "Historic");
				obj.put("historicStartDate", la.getHistoricStartDate());
				obj.put("historicEndDate", la.getHistoricEndDate());
				obj.put("historicDateFormat", la.getHistoricDateFormat());
			} else {
				obj.put("validationCheckType", "Incremental");
			}
		} else {
			obj.put("validationCheckType", "Bulk Load");
		}
		obj.put("idData", la.getIdData());
		obj.put("fileNameValidation", la.getFileNameValidation());
		obj.put("colOrderValidation", la.getColOrderValidation());
		obj.put("nonNullCheck", la.getNonNullCheck());
		obj.put("numericalStatCheck", la.getNumericalStatCheck());
		obj.put("stringStatCheck", la.getStringStatCheck());
		obj.put("recordAnomalyCheck", la.getRecordAnomalyCheck());
		obj.put("dataDriftCheck", la.getDataDriftCheck());
		obj.put("timeSeries", la.getTimeSeries());
		if (la.getRecordCountAnomaly().trim().equalsIgnoreCase("Y")) {
			obj.put("recordCountAnomalyCheck", "Y");
		} else {
			obj.put("keyGroupRecordCountAnomalyCheck", "Y");
		}
		obj.put("applyRules", la.getApplyRules());
		obj.put("applyDerivedColumns", la.getApplyDerivedColumns());

		obj.put("duplicateRowAll", jdbcTemplate
				.queryForObject("select dupRow from listDFTranRule where type='all' and idApp=" + idApp, String.class));

		obj.put("duplicateRowId", jdbcTemplate.queryForObject(
				"select dupRow from listDFTranRule where type='identity' and idApp=" + idApp, String.class));
		obj.put("colOrderValidation", la.getColOrderValidation());
		obj.put("appType", la.getAppType());
		obj.put("createdAt", la.getCreatedAt());
		LOG.info("viewValidationCheck - END");
		return obj;

	}

	public String taskstatus(Long idApp) {
		LOG.info("taskstatus - START");
		LOG.debug("Getting  parameters for idApp  " + idApp);
		JSONObject json = new JSONObject();
		try {
			String status = "Task " + iTaskService.getTaskStatusFromRunScheduledTasks(idApp,null);
			SqlRowSet appTypeFromListApplications = iTaskService.getAppTypeFromListApplications(idApp);
			String appType = "", appName = "";
			while (appTypeFromListApplications.next()) {
				appType = appTypeFromListApplications.getString(1);
				appName = appTypeFromListApplications.getString(2);
			}
			// for showing task % status
			int percentage = 5;
			
			LOG.debug("appType=" + appType);
			if (appType.equalsIgnoreCase("Data Forensics")) {
				percentage = iTaskService.getStatusOfDfReadFromTaskProgressStatus(idApp);
				if (percentage >= 30) {
					double count = iTaskService.getColumnCountYesInListApplicationsAndListDFTranrule(idApp);
					double passedStatus = iTaskService.getTaskStatusForPassed(idApp);
					percentage = percentage + (int) ((passedStatus / count) * 70);
					if (percentage > 100) {
						percentage = 100;
					}
				}
			} else if (appType.equalsIgnoreCase("Data Matching") || appType.equalsIgnoreCase("Data Matching")
					|| appType.equalsIgnoreCase("Data Matching Group")
					|| appType.equalsIgnoreCase("Statistical Matching")) {
				percentage = iTaskService.getStatusForMatching(idApp);
			
			} else if (appType.equalsIgnoreCase("Schema Matching")) {
				percentage = iTaskService.getStatusForSchemaMatching(idApp);
				if (percentage > 100) {
					percentage = 100;
				}
			}
			json.put("percentage", percentage);
			json.put("success", status);
		} catch (Exception e) {
			json.put("fail", "Unable to process the request");
			LOG.error("Exception  "+e.getMessage());
			return json.toString();
		}
		LOG.info("taskstatus - END");
		return json.toString();
	}

	public JSONObject deleteResultFromDB(Long idApp) {
		LOG.info("deleteResultFromDB - START");
		LOG.debug("Getting  parameters for idApp  " + idApp);
		JSONObject json = new JSONObject();
		try {
			String sql = "select Table_Name from result_master_table where appID=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				try {
					//sql = "DROP TABLE " + queryForRowSet.getString(1);
					sql = "DELETE FROM "+ queryForRowSet.getString(1) +"  where idApp = " + idApp;
					jdbcTemplate1.update(sql);
				} catch (Exception e) {
					LOG.error("Exception  "+e.getMessage());
				}
			}
			// result_master_table
			try {
				sql = "DELETE FROM result_master_table where appID=" + idApp;
				jdbcTemplate1.update(sql);
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
			}
			// data_quality_dashboard
			try {
				sql = "DELETE FROM data_quality_dashboard where idApp=" + idApp;
				jdbcTemplate1.update(sql);
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
			}
			//data_quality_historic_dashboard
			try {
				sql = "DELETE FROM data_quality_historic_dashboard where idApp=" + idApp;
				jdbcTemplate1.update(sql);
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				
			}
			json.put("idApp", idApp);
			json.put("success", "Table Data Deleted Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Unable to process the request");
			return json;
		}
		LOG.info("deleteResultFromDB - END");
		return json;
	}

	public DataQualityHistoricDashboard getDataFromDataQualityHistoricDashboard(long idApp) {
		LOG.info("getDataFromDataQualityHistoricDashboard - START");
		LOG.debug("Getting  parameters for idApp  " + idApp);
		DataQualityHistoricDashboard dqhd = new DataQualityHistoricDashboard();
		try {
			String sql = "select * from data_quality_historic_dashboard where idApp=" + idApp
					+ " order by id desc limit 1";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				dqhd.setId(queryForRowSet.getLong("id"));
				dqhd.setIdApp(queryForRowSet.getLong("idApp"));
				dqhd.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
				dqhd.setDate(queryForRowSet.getDate("date"));
				dqhd.setRun(queryForRowSet.getLong("run"));
				dqhd.setAggregateDQI(queryForRowSet.getDouble("aggregateDQI"));
				dqhd.setFileContentValidationStatus(queryForRowSet.getString("fileContentValidationStatus"));
				dqhd.setColumnOrderValidationStatus(queryForRowSet.getString("columnOrderValidationStatus"));
				dqhd.setAbsoluteRCDQI(queryForRowSet.getDouble("absoluteRCDQI"));
				dqhd.setAbsoluteRCStatus(queryForRowSet.getString("absoluteRCStatus"));
				dqhd.setAbsoluteRCRecordCount(queryForRowSet.getLong("absoluteRCRecordCount"));
				dqhd.setAbsoluteRCAverageRecordCount(queryForRowSet.getLong("absoluteRCAverageRecordCount"));
				dqhd.setAggregateRCDQI(queryForRowSet.getDouble("aggregateRCDQI"));
				dqhd.setAggregateRCStatus(queryForRowSet.getString("aggregateRCStatus"));
				dqhd.setAggregateRCRecordCount(queryForRowSet.getLong("aggregateRCRecordCount"));
				dqhd.setNullCountDQI(queryForRowSet.getDouble("nullCountDQI"));
				dqhd.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
				dqhd.setNullCountColumns(queryForRowSet.getLong("nullCountColumns"));
				dqhd.setNullCountColumnsFailed(queryForRowSet.getLong("nullCountColumnsFailed"));
				dqhd.setPrimaryKeyDQI(queryForRowSet.getDouble("primaryKeyDQI"));
				dqhd.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
				dqhd.setPrimaryKeyDuplicates(queryForRowSet.getLong("primaryKeyDuplicates"));
				dqhd.setUserSelectedDQI(queryForRowSet.getDouble("userSelectedDQI"));
				dqhd.setUserSelectedStatus(queryForRowSet.getString("userSelectedStatus"));
				dqhd.setUserSelectedDuplicates(queryForRowSet.getLong("userSelectedDuplicates"));
				dqhd.setNumericalDQI(queryForRowSet.getDouble("numericalDQI"));
				dqhd.setNumericalStatus(queryForRowSet.getString("numericalStatus"));
				dqhd.setNumericalColumns(queryForRowSet.getLong("numericalColumns"));
				dqhd.setNumericalRecordsFailed(queryForRowSet.getLong("numericalRecordsFailed"));
				dqhd.setStringDQI(queryForRowSet.getDouble("stringDQI"));
				dqhd.setStringStatus(queryForRowSet.getString("stringStatus"));
				dqhd.setStringColumns(queryForRowSet.getLong("stringColumns"));
				dqhd.setStringRecordsFailed(queryForRowSet.getLong("stringRecordsFailed"));
				dqhd.setRecordAnomalyDQI(queryForRowSet.getDouble("recordAnomalyDQI"));
				dqhd.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
				dqhd.setRecordAnomalyRecords(queryForRowSet.getLong("recordAnomalyRecords"));
				dqhd.setRecordAnomalyRecordsFailed(queryForRowSet.getLong("recordAnomalyRecordsFailed"));
				dqhd.setRuleType(queryForRowSet.getString("ruleType"));
				dqhd.setRuleDQI(queryForRowSet.getDouble("ruleDQI"));
				dqhd.setDataDriftDQI(queryForRowSet.getDouble("dataDriftDQI"));
				dqhd.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("getDataFromDataQualityHistoricDashboard - END");
		return dqhd;
	}

	String getSourceNameFromListDataSource(Long idData) {
		LOG.info("getSourceNameFromListDataSource - START");
		LOG.debug("Getting  parameters for idData  " + idData);
		try {
			return jdbcTemplate.queryForObject("SELECT name FROM listDataSources WHERE idData=" + idData,
					String.class);
		} catch (Exception e) {
			// //e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		LOG.info("getSourceNameFromListDataSource - END");
		return "";
	}

	public JSONObject prepareJSONDataForDashboardFast(Long idApp) {
		LOG.info("prepareJSONDataForDashboardFast - START");
		LOG.debug("Getting  parameters for idApp  " + idApp);
		DataQualityHistoricDashboard dqhd = getDataFromDataQualityHistoricDashboard(idApp);
		JSONObject FEDataQuality = new JSONObject();
		try {
			ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
			JSONObject obj = new JSONObject();
			obj.put("DataSetName", getSourceNameFromListDataSource(listApplicationsData.getIdData()));
			obj.put("ValidationTestName", listApplicationsData.getName());
			if (listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")
					&& listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				obj.put("testtype", "Historic");
			} else if (listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")
					&& listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				obj.put("testtype", "Incremental");
			} else {
				obj.put("testtype", "Bulk Load");
			}

			obj.put("Date", dqhd.getDate());
			obj.put("Run", dqhd.getRun().toString());
			obj.put("idApp", idApp);
			JSONArray FERowDQ = new JSONArray();
			JSONObject FERowDQ1 = new JSONObject();
			//
			FERowDQ1.put("ValidationTest", "Record Count Fingerprint");
			FERowDQ1.put("Definition", "Record count reasonability based on historical trends");
			FERowDQ1.put("DQScore", dqhd.getAbsoluteRCDQI().toString());

			FERowDQ.put(FERowDQ1);

			if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
				JSONObject FERowDQ11 = new JSONObject();
				FERowDQ11.put("ValidationTest", "Null Count");
				FERowDQ11.put("Definition", "Percentage of null fields in the selected group of columns");
				FERowDQ11.put("DQScore", dqhd.getNullCountDQI().toString());

				FERowDQ.put(FERowDQ11);
			}

			Map<String, String> listdftranruleData = iResultsDAO.getdatafromlistdftranrule(idApp);
			for (Map.Entry<String, String> entry : listdftranruleData.entrySet()) {
				if (entry.getKey().equalsIgnoreCase("Y")) {
					JSONObject FERowDQ11 = new JSONObject();
					FERowDQ11.put("ValidationTest", "Primary Key Duplicate");
					FERowDQ11.put("Definition", "Percentage of duplicates records based on primary keys");
					FERowDQ11.put("DQScore", dqhd.getPrimaryKeyDQI().toString());

					FERowDQ.put(FERowDQ11);
				}
				if (entry.getValue().equalsIgnoreCase("Y")) {
					JSONObject FERowDQ11 = new JSONObject();
					FERowDQ11.put("ValidationTest", "User Selected Fields Duplicate");
					FERowDQ11.put("Definition",
							"Percentage of duplicates records based on user-selected non-primary keys");
					FERowDQ11.put("DQScore", dqhd.getUserSelectedDQI().toString());

					FERowDQ.put(FERowDQ11);
				}
			}
			if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
				JSONObject FERowDQ11 = new JSONObject();
				FERowDQ11.put("ValidationTest", "Numerical Field Fingerprint");
				FERowDQ11.put("Definition", "Reasonability of selected numerical fields based on historical trends");
				FERowDQ11.put("DQScore", dqhd.getNumericalDQI().toString());

				FERowDQ.put(FERowDQ11);
			}
			if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
				JSONObject FERowDQ11 = new JSONObject();
				FERowDQ11.put("ValidationTest", "String Field Fingerprint");
				FERowDQ11.put("Definition", "Reasonability of selected String fields based on historical trends");
				FERowDQ11.put("DQScore", dqhd.getStringDQI().toString());

				FERowDQ.put(FERowDQ11);
			}
			if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
				JSONObject FERowDQ11 = new JSONObject();
				FERowDQ11.put("ValidationTest", "Record Anomaly Fingerprint");
				FERowDQ11.put("Definition", "Percentage of records that are outliers");
				FERowDQ11.put("DQScore", dqhd.getRecordAnomalyDQI().toString());

				FERowDQ.put(FERowDQ11);
			}
			if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y")) {
				if (dqhd.getRuleDQI() != null && dqhd.getRuleType() != null) {
					JSONObject FERowDQ11 = new JSONObject();
					FERowDQ11.put("ValidationTest", dqhd.getRuleType());
					FERowDQ11.put("DQScore", dqhd.getRuleDQI().toString());
					FERowDQ.put(FERowDQ11);

				}
			}
			if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) {
				JSONObject FERowDQ11 = new JSONObject();
				FERowDQ11.put("ValidationTest", "Data Drift");
				FERowDQ11.put("Definition", "Changes in unique values of the selected string fields");
				FERowDQ11.put("DQScore", dqhd.getDataDriftDQI().toString());
				FERowDQ.put(FERowDQ11);
			}
			DecimalFormat df = new DecimalFormat("#0.0");

			obj.put("Aggregate DQI", dqhd.getAggregateDQI().toString());
			obj.put("FERowDQ", FERowDQ);

			FEDataQuality.put("FEDataQuality", obj);
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		LOG.info("prepareJSONDataForDashboardFast - END");
		return FEDataQuality;
	}
}
