package com.databuck.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbcp.BasicDataSource;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.AdhocRollRequest;
import com.databuck.bean.ListDataSchema;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AdhocRollMatchController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ITaskDAO iTaskDao;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private IListDataSourceDAO listDataSourceDAO;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@RequestMapping(value = "/processAdhocRollMatch", method = RequestMethod.POST)
	public String createAndExecuteAdhocRollMatchValidation(HttpServletRequest request,
			@RequestBody AdhocRollRequest adhocRollRequest) {

		System.out.println("\n=======> processAdhocRollMatch - Start <=======");

		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		String result = "";
		String status = "failed";
		Long newIdApp = 0l;
		try {
			// Identify the latest Adhoc Roll Match validation
			// Get the left and Right template Id's
			Long old_leftTemplateId = 0l;
			Long old_rightTemplateId = 0l;
			Long old_idApp = 0l;
			Long targetSchemaId = 0l;

			System.out.println("\n=====> Get the Adhoc Roll Match validation details ....");

			String sql = "select idApp,idData,idRightData,rollTargetSchemaId from listApplications where appType='Rolling DataMatching' and rollType='adhoc_match' order by idApp desc limit 1";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					old_idApp = sqlRowSet.getLong("idApp");
					old_leftTemplateId = sqlRowSet.getLong("idData");
					old_rightTemplateId = sqlRowSet.getLong("idRightData");
					targetSchemaId = sqlRowSet.getLong("rollTargetSchemaId");
				}
			}

			System.out.println("old_idApp: " + old_idApp);
			System.out.println("old_leftTemplateId: " + old_leftTemplateId);
			System.out.println("old_rightTemplateId: " + old_rightTemplateId);

			if (old_leftTemplateId != null && old_leftTemplateId != 0l && old_rightTemplateId != null
					&& old_rightTemplateId != 0l && old_idApp != null && old_idApp != 0l) {

				// Copy the left template and update filePath and fileName
				String leftTemplateName = "Adhoc_Roll_LeftTemplate_" + adhocRollRequest.getCurDataMonth() + "_1";
				System.out.println("\n=====> Creating the left template: " + leftTemplateName);
				System.out.println("FilePath: " + adhocRollRequest.getCurMonthFilePath());
				System.out.println("FileName: " + adhocRollRequest.getCurMonthFileName());

				Long new_leftTemplateId = copyTemplate(old_leftTemplateId, leftTemplateName,
						adhocRollRequest.getCurMonthFilePath(), adhocRollRequest.getCurMonthFileName());

				System.out.println("\n=====> new_leftTemplateId: " + new_leftTemplateId);

				// Copy the right template and update filePath and fileName
				String rightTemplateName = "Adhoc_Roll_RightTemplate_" + adhocRollRequest.getOtherDataMonth() + "_1";
				System.out.println("\n=====> Creating the right template: " + leftTemplateName);
				System.out.println("FilePath: " + adhocRollRequest.getOtherMonthFilePath());
				System.out.println("FileName: " + adhocRollRequest.getOtherMonthfileName());

				Long new_rightTemplateId = copyTemplate(old_rightTemplateId, rightTemplateName,
						adhocRollRequest.getOtherMonthFilePath(), adhocRollRequest.getOtherMonthfileName());

				System.out.println("\n=====> new_rightTemplateId: " + new_rightTemplateId);

				if (new_rightTemplateId != null && new_leftTemplateId != null) {

					// Copy the validation and update the right and left template Id's
					String newValidationName = "Adhoc" + adhocRollRequest.getCurDataMonth() + "_"
							+ adhocRollRequest.getOtherDataMonth() + "_val";
					System.out.println("\n=====> Creating the new validation: " + newValidationName);

					newIdApp = copyValidation(newValidationName, old_idApp, new_leftTemplateId, new_rightTemplateId);
					System.out.println("\n=====>newIdApp: " + newIdApp);

					if (newIdApp != null && newIdApp != 0l) {
						status = "passed";
						// Get deployMode
						String deployMode = clusterProperties.getProperty("deploymode");

						if (deployMode.trim().equalsIgnoreCase("2")) {
							deployMode = "local";
						} else {
							deployMode = "cluster";
						}
						// Place the job in queue
						System.out.println("\n=====> Job placed in queue ....");
						iTaskDao.insertRunScheduledTask(newIdApp, "queued", deployMode, null, null);

						// Insert status in adhoc_rollmatch_master_table with status 'initiated'
						System.out.println("\n=====> Insert status in adhoc_rollmatch_master_table ....");
						JdbcTemplate targetJdbcTemplate = getTargetSchemaConnection(targetSchemaId);
						boolean updateStatus = insertStatusIntoAdhocMaster(targetJdbcTemplate, newIdApp,
								adhocRollRequest);
						System.out.println("\n=====> Insert status: " + updateStatus);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Prepare final response and convert to json string
		try {
			responseMap.put("Status", status);
			if (status != null && status.equalsIgnoreCase("passed")) {
				responseMap.put("AppId", newIdApp);
			} else {
				System.out.println("\n=====> Adhoc validation creation failed !!");
			}

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("\n=======> processAdhocRollMatch - End <=======");

		return result;
	}

	private Long copyTemplate(long idData, String newTemplateName, String filePath, String fileName) {

		String datasourceSql = ("insert into listDataSources(name ,description ,dataLocation ,dataSource ,createdBy ,idDataBlend ,createdAt ,"
				+ "updatedAt ,updatedBy ,schemaName ,idDataSchema ,ignoreRowsCount ,active ,project_id ,profilingEnabled ,advancedRulesEnabled ,"
				+ "createdByUser) (select name ,description ,dataLocation ,dataSource ,createdBy ,idDataBlend ,createdAt ,"
				+ "updatedAt ,updatedBy ,schemaName ,idDataSchema ,ignoreRowsCount ,active ,project_id ,profilingEnabled ,advancedRulesEnabled ,"
				+ "createdByUser from listDataSources where idData=" + idData + ")");

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
				: "idData";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(datasourceSql, new String[] { key_name });
				return pst;
			}
		}, keyHolder);

		Long newTemplateId = keyHolder.getKey().longValue();

		System.out.println("\n=====> Inserted into listDataSource");

		String updateListDataDefinationQuery = "insert into listDataDefinition (idData, columnName, "
				+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
				+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
				+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
				+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
				+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
				+ "lengthcheck , maxLengthCheck , lengthvalue, applyrule, startDate, timelinessKey, endDate, "
				+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, " + "dateFormat, defaultPatternCheck, defaultPatterns) (select "
				+ newTemplateId + " as idData, columnName, "
				+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
				+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
				+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
				+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
				+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
				+ "lengthcheck, maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
				+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
				+ "dateFormat, defaultPatternCheck, defaultPatterns from listDataDefinition where idData=" + idData + ")";
		jdbcTemplate.execute(updateListDataDefinationQuery);

		System.out.println("\n=====> Inserted into listDataDefinition");

		String updateListDataAccess = "insert into listDataAccess (idData ,hostName ,portName ,userName ,pwd ,"
				+ "schemaName ,folderName ,queryString ,query ,incrementalType ,idDataSchema ,whereCondition ,"
				+ "domain ,fileHeader ,dateFormat ,sliceStart ,sliceEnd ,metaData ,isRawData ,sslEnb ,"
				+ "sslTrustStorePath ,trustPassword ,hivejdbcport ,hivejdbchost ,gatewayPath ,jksPath ,"
				+ "zookeeperUrl ,rollingHeader ,rollingColumn ,rollTargetSchemaId) (select  " + newTemplateId
				+ " as idData ,'" + filePath + "' as hostName ,portName ,userName ,pwd ," + "schemaName ,'" + fileName
				+ "' as folderName ,queryString ,query ,incrementalType ,idDataSchema ,whereCondition ,"
				+ "domain ,fileHeader ,dateFormat ,sliceStart ,sliceEnd ,metaData ,isRawData ,sslEnb ,"
				+ "sslTrustStorePath ,trustPassword ,hivejdbcport ,hivejdbchost ,gatewayPath ,jksPath ,"
				+ "zookeeperUrl ,rollingHeader ,rollingColumn ,rollTargetSchemaId from listDataAccess where idData="
				+ idData + ")";
		jdbcTemplate.execute(updateListDataAccess);

		System.out.println("\n=====> Inserted into listDataAccess");

		return newTemplateId;
	}

	private Long copyValidation(String newValidationName, long idApp, long leftTemplateId, long rightTemplateId) {

		String updateListApplications = ("insert into listApplications (name ,description ,appType ,idData ,"
				+ "idRightData ,createdBy ,createdAt ,updatedAt ,updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,"
				+ "matchingThreshold ,nonNullCheck ,numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,incrementalMatching ,"
				+ "incrementalTimestamp ,dataDriftCheck ,updateFrequency ,frequencyDays ,recordCountAnomaly ,"
				+ "recordCountAnomalyThreshold ,timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,applyRules ,"
				+ "applyDerivedColumns ,csvDir ,groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,historicStartDate ,"
				+ "historicEndDate ,historicDateFormat ,active ,lengthCheck , maxLengthCheck ,correlationcheck ,project_id ,timelinessKeyCheck ,"
				+ "defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,idLeftData ,prefix1 ,prefix2 ,"
				+ "dGroupNullCheck ,dGroupDateRuleCheck ,fileMonitoringType ,createdByUser ,validityThreshold ,dGroupDataDriftCheck ,"
				+ "rollTargetSchemaId ,thresholdsApplyOption ,continuousFileMonitoring ,rollType, domain_id, subcribed_email_id, data_domain_id) " + "(select '"
				+ newValidationName + "' as name, description, appType, " + leftTemplateId + " as idData, "
				+ rightTemplateId
				+ " as idRightData,createdBy ,createdAt ,updatedAt ,updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,"
				+ "matchingThreshold ,nonNullCheck ,numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,incrementalMatching ,"
				+ "incrementalTimestamp ,dataDriftCheck ,updateFrequency ,frequencyDays ,recordCountAnomaly ,"
				+ "recordCountAnomalyThreshold ,timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,applyRules ,"
				+ "applyDerivedColumns ,csvDir ,groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,historicStartDate ,"
				+ "historicEndDate ,historicDateFormat ,active ,lengthCheck , maxLengthCheck ,correlationcheck ,project_id ,timelinessKeyCheck ,"
				+ "defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,idLeftData ,prefix1 ,prefix2 ,"
				+ "dGroupNullCheck ,dGroupDateRuleCheck ,fileMonitoringType ,createdByUser ,validityThreshold ,dGroupDataDriftCheck ,"
				+ "rollTargetSchemaId ,thresholdsApplyOption ,continuousFileMonitoring ,rollType, domain_id, subcribed_email_id, data_domain_id"
				+ " from listApplications where " + "idApp = " + idApp + ")");

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idapp"
				: "idApp";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(updateListApplications, new String[] { key_name });
				return pst;
			}
		}, keyHolder);

		Long new_idApp = keyHolder.getKey().longValue();

		newValidationName = (new_idApp) + "_" + newValidationName;

		String updateNameSql = "update listApplications set name='" + newValidationName + "' where idApp=" + new_idApp;
		jdbcTemplate.execute(updateNameSql);

		System.out.println("\n=====> listApplication updated");

		validationcheckdao.deleteEntryFromListDMRules(new_idApp, "Key Fields Match");
		validationcheckdao.deleteEntryFromListDMRules(new_idApp, "Measurements Match");

		Long idDM = validationcheckdao.getIddmFromListDMRules(new_idApp, "Key Fields Match");
		if (idDM == 0) {
			idDM = validationcheckdao.insertIntoListDMRules(new_idApp, "Key Fields Match", "One to One");
			System.out.println("idDM=" + idDM);
		}

		String dmSql = "Select leftSideExp,rightSideExp from listDMCriteria where idDM = (select idDM from listDMRules where matchType2='Key Fields Match' and matchType='One to One' and idApp="
				+ idApp + ")";
		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(dmSql);

		while (sqlRowSet.next()) {
			String leftSideExp = sqlRowSet.getString("leftSideExp");
			String rightSideExp = sqlRowSet.getString("rightSideExp");

			String expressionSql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
			jdbcTemplate.update(expressionSql, idDM, leftSideExp, rightSideExp);
		}

		List<String> leftMatchValueCols = validationcheckdao.getMatchValueColumns(leftTemplateId);
		List<String> rightMatchValueCols = validationcheckdao.getMatchValueColumns(rightTemplateId);

		Long measurementIdDM = validationcheckdao.insertIntoListDMRules(new_idApp, "Measurements Match", "One to One");
		validationcheckdao.insertDataIntoListDMCriteriaForRollMatch(measurementIdDM, leftMatchValueCols,
				rightMatchValueCols);

		return new_idApp;
	}

	private JdbcTemplate getTargetSchemaConnection(long targetSchemaId) {

		JdbcTemplate jdbcTemplate = null;
		try {
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
						System.out.println("****Url:" + url);
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
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate;
	}

	private boolean insertStatusIntoAdhocMaster(JdbcTemplate targetJdbcTemplate, long idApp,
			AdhocRollRequest adhocRollRequest) {
		boolean result = false;
		try {
			String dataType = "";
			String metric = "";
			String dataSrc = "";
			String actualCur_fileName = adhocRollRequest.getCurMonthFileName();

			if (actualCur_fileName != null && !actualCur_fileName.trim().isEmpty()
					&& actualCur_fileName.split("_").length > 5) {
				String[] fileNameParts = actualCur_fileName.split("_");
				dataType = fileNameParts[3];
				metric = fileNameParts[4];
				dataSrc = fileNameParts[5];
			}

			Date runDate = new Date();
			String runDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(runDate);

			String adhoc_id_sql = "INSERT INTO adhoc_rollmatch_master_table (idApp, Run_Date, SALES_FORCE_CODE, MARKET_NAME, SELLING_PERIOD, CUR_DATA_MONTH, ANOTHER_DATA_MONTH, METRIC, DATATYPE, DATASRC, STATUS) values("
					+ idApp + ",'" + runDateStr + "','" + adhocRollRequest.getSalesForce() + "','"
					+ adhocRollRequest.getMarket() + "','" + adhocRollRequest.getSellingPeriod() + "','"
					+ adhocRollRequest.getCurDataMonth() + "','" + adhocRollRequest.getOtherDataMonth() + "','" + metric
					+ "','" + dataType + "','" + dataSrc + "','initiated')";

			int updateCount = targetJdbcTemplate.update(adhoc_id_sql);
			if (updateCount > 0) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
