package com.databuck.dao.impl;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.SynonymLibrary;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IImportExportUtilityDAO;

@Repository
public class ImportExportUtilityDAOImpl implements IImportExportUtilityDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	Map<String, String> importMap = new HashMap<String, String>();
	String sql = null;
	
	private static final Logger LOG = Logger.getLogger(ImportExportUtilityDAOImpl.class);

	public ListApplications getdatafromlistapplications(Long idApp) {
		try {
			ListApplications la = new ListApplications();
			String sql = "select * from listApplications" + " where idApp=" + idApp;
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			queryForRowSet.next();
			la.setIdApp(queryForRowSet.getLong("idApp"));
			la.setName(queryForRowSet.getString("name"));
			la.setDescription(queryForRowSet.getString("description"));
			la.setAppType(queryForRowSet.getString("appType"));
			la.setIdData(queryForRowSet.getLong("idData"));
			la.setIdRightData(queryForRowSet.getInt("idRightData"));
			la.setCreatedBy(queryForRowSet.getString("createdBy"));
			la.setCreatedAt(queryForRowSet.getString("createdAt"));
			la.setUpdatedAt(queryForRowSet.getString("updatedAt"));
			la.setUpdatedBy(queryForRowSet.getString("updatedBy"));
			la.setFileNameValidation(queryForRowSet.getString("fileNameValidation"));
			la.setEntityColumn(queryForRowSet.getString("entityColumn"));
			la.setColOrderValidation(queryForRowSet.getString("colOrderValidation"));
			la.setMatchingThreshold(queryForRowSet.getDouble("matchingThreshold"));
			la.setNonNullCheck(queryForRowSet.getString("nonNullCheck"));
			la.setNumericalStatCheck(queryForRowSet.getString("numericalStatCheck"));
			la.setStringStatCheck(queryForRowSet.getString("stringStatCheck"));
			la.setRecordAnomalyCheck(queryForRowSet.getString("recordAnomalyCheck"));
			la.setIncrementalMatching(queryForRowSet.getString("incrementalMatching"));
			la.setIncrementalTimestamp(queryForRowSet.getDate("incrementalTimestamp"));
			la.setDataDriftCheck(queryForRowSet.getString("dataDriftCheck"));
			la.setUpdateFrequency(queryForRowSet.getString("updateFrequency"));
			la.setFrequencyDays(queryForRowSet.getInt("frequencyDays"));
			la.setRecordCountAnomaly(queryForRowSet.getString("recordCountAnomaly"));
			la.setRecordCountAnomalyThreshold(queryForRowSet.getDouble("recordCountAnomalyThreshold"));
			la.setTimeSeries(queryForRowSet.getString("timeSeries"));
			la.setKeyGroupRecordCountAnomaly(queryForRowSet.getString("keyGroupRecordCountAnomaly"));
			la.setOutOfNormCheck(queryForRowSet.getString("outOfNormCheck"));
			la.setApplyRules(queryForRowSet.getString("applyRules"));
			la.setApplyDerivedColumns(queryForRowSet.getString("applyDerivedColumns"));
			la.setCsvDir(queryForRowSet.getString("csvDir"));
			la.setGroupEquality(queryForRowSet.getString("groupEquality"));
			la.setGroupEqualityThreshold(queryForRowSet.getDouble("groupEqualityThreshold"));
			la.setBuildHistoricFingerPrint(queryForRowSet.getString("buildHistoricFingerPrint"));
			la.setHistoricStartDate(queryForRowSet.getString("historicStartDate"));
			la.setHistoricEndDate(queryForRowSet.getString("historicEndDate"));
			la.setHistoricDateFormat(queryForRowSet.getString("historicDateFormat"));
			la.setActive(queryForRowSet.getString("active"));
			la.setCorrelationcheck(queryForRowSet.getString("correlationcheck"));
			la.setProjectId(queryForRowSet.getLong("project_id"));
			la.setlengthCheck(queryForRowSet.getString("lengthCheck"));
			la.setMaxLengthCheck(queryForRowSet.getString("maxLengthCheck"));
			la.setTimelinessKeyChk(queryForRowSet.getString("timelinessKeyCheck"));
			la.setDefaultCheck(queryForRowSet.getString("defaultCheck"));
			la.setDefaultValues(queryForRowSet.getString("defaultValues"));
			la.setPatternCheck(queryForRowSet.getString("patternCheck"));
			la.setDateRuleChk(queryForRowSet.getString("dateRuleCheck"));
			la.setBadData(queryForRowSet.getString("badData"));
			la.setIdLeftData(queryForRowSet.getLong("idLeftData"));
			la.setPrefix1(queryForRowSet.getString("prefix1"));
			la.setPrefix2(queryForRowSet.getString("prefix2"));
			la.setdGroupNullCheck(queryForRowSet.getString("dGroupNullCheck"));
			la.setdGroupDateRuleCheck(queryForRowSet.getString("dGroupDateRuleCheck"));
			la.setFileMonitoringType(queryForRowSet.getString("fileMonitoringType"));
			la.setCreatedByUser(queryForRowSet.getString("createdByUser"));

			la.setValidityThreshold(queryForRowSet.getDouble("validityThreshold"));
			la.setdGroupDataDriftCheck(queryForRowSet.getString("dGroupDataDriftCheck"));
			la.setRollTargetSchemaId(queryForRowSet.getLong("rollTargetSchemaId"));
			la.setThresholdsApplyOption(queryForRowSet.getInt("thresholdsApplyOption"));
			la.setContinuousFileMonitoring(queryForRowSet.getString("continuousFileMonitoring"));
			la.setRollType(queryForRowSet.getString("rollType"));

			return la;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public ListDataSource getDataFromListDataSources(Long idData) {

		String sql = "SELECT * from listDataSources where idData=" + idData;

		LOG.debug("----- getDataFromListDataSources ---" + sql);

		return jdbcTemplate.query(sql, new ResultSetExtractor<ListDataSource>() {

			public ListDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListDataSource listdatasource = new ListDataSource();
					listdatasource.setIdData(rs.getInt("idData"));
					listdatasource.setName(rs.getString("name"));
					listdatasource.setDescription(rs.getString("description"));
					listdatasource.setDataLocation(rs.getString("dataLocation"));
					listdatasource.setDataSource(rs.getString("dataSource"));
					listdatasource.setCreatedBy(rs.getLong("createdBy"));
					listdatasource.setIdDataBlend(rs.getInt("idDataBlend"));
					listdatasource.setCreatedAt(rs.getDate("createdAt"));
					listdatasource.setUpdatedAt(rs.getDate("updatedAt"));
					listdatasource.setUpdatedBy(rs.getLong("updatedBy"));
					listdatasource.setSchemaName(rs.getString("schemaName"));
					listdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
					listdatasource.setIgnoreRowsCount(rs.getLong("ignoreRowsCount"));
					listdatasource.setActive(rs.getString("active"));
					listdatasource.setProjectId(rs.getInt("project_id"));
					listdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
					listdatasource.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
					listdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
					listdatasource.setCreatedByUser(rs.getString("createdByUser"));
					listdatasource.setDomain(rs.getInt("domain_id"));

					return listdatasource;
				}
				return null;
			}
		});
	}

	public listDataAccess getListDataAccess(Long iData) {
		RowMapper<listDataAccess> rowMapper = (rs, i) -> {
			listDataAccess lda = new listDataAccess();
			lda.setIdlistDataAccess(rs.getLong("idlistDataAccess"));
			lda.setIdData(rs.getLong("idData"));
			lda.setHostName(rs.getString("hostName"));
			lda.setPortName(rs.getString("portName"));
			lda.setUserName(rs.getString("userName"));
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String decryptedText = decryptor.decrypt(rs.getString("pwd"));
			// lda.setPwd(rs.getString("pwd"));
			lda.setPwd(decryptedText);
			// lda.setSchemaName(rs.getString("schemaName"));
			String str = rs.getString("schemaName").replaceAll(",", "#");
			lda.setSchemaName(str);
			lda.setFolderName(rs.getString("folderName"));
			lda.setQueryString(rs.getString("queryString"));
			lda.setQuery(rs.getString("query"));
			lda.setIncrementalType(rs.getString("incrementalType"));
			lda.setDateFormat(rs.getString("dateFormat"));
			lda.setSliceStart(rs.getString("sliceStart"));
			lda.setSliceEnd(rs.getString("sliceEnd"));
			lda.setIdDataSchema(rs.getLong("idDataSchema"));
			lda.setWhereCondition(rs.getString("whereCondition"));
			lda.setDomain(rs.getString("domain"));
			lda.setFileHeader(rs.getString("fileHeader"));
			// lda.setQueryString(rs.getString("queryString"));
			lda.setMetaData(rs.getString("metaData"));
			lda.setIsRawData(rs.getString("isRawData"));
			lda.setSslEnb(rs.getString("sslEnb"));
			lda.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
			lda.setTrustPassword(rs.getString("trustPassword"));
			lda.setHivejdbchost(rs.getString("hivejdbchost"));
			lda.setHivejdbcport(rs.getString("hivejdbcport"));
			lda.setGatewayPath(rs.getString("gatewayPath"));
			lda.setJksPath(rs.getString("jksPath"));
			lda.setZookeeperUrl(rs.getString("zookeeperUrl"));
			lda.setRollingHeader(rs.getString("rollingHeader"));
			lda.setRollingColumn(rs.getString("rollingColumn"));
//			lda.setRollTargetSchemaId(rs.getInt("rollTargetSchemaId"));

			return lda;
		};
		String sql = "select * from listDataAccess where  idData=?";
		return jdbcTemplate.query(sql, rowMapper, iData).get(0);
	}

	public List<ListDataDefinition> getListDataDefinitionData(Long idData) {
		RowMapper<ListDataDefinition> rowMapper = (rs, i) -> {
			ListDataDefinition listDataDefinition = new ListDataDefinition();
			listDataDefinition.setIdColumn(rs.getLong("idColumn"));
			listDataDefinition.setIdData(rs.getInt("idData"));
			listDataDefinition.setColumnName(rs.getString("columnName"));
			listDataDefinition.setDisplayName(rs.getString("displayName"));
			listDataDefinition.setFormat(rs.getString("format"));
			listDataDefinition.setHashValue(rs.getString("hashValue"));
			listDataDefinition.setNumericalStat(rs.getString("numericalStat"));
			listDataDefinition.setStringStat(rs.getString("stringStat"));
			listDataDefinition.setNullCountThreshold(rs.getDouble("nullCountThreshold"));
			listDataDefinition.setNumericalThreshold(rs.getDouble("numericalThreshold"));
			listDataDefinition.setStringStatThreshold(rs.getDouble("stringStatThreshold"));
			listDataDefinition.setKBE(rs.getString("KBE"));
			listDataDefinition.setDgroup(rs.getString("dgroup"));
			listDataDefinition.setDupkey(rs.getString("dupKey"));
			listDataDefinition.setMeasurement(rs.getString("measurement"));
			listDataDefinition.setBlend(rs.getString("blend"));
			listDataDefinition.setIdCol(rs.getInt("idCol"));
			listDataDefinition.setIncrementalCol(rs.getString("incrementalCol"));
			listDataDefinition.setIdDataSchema(rs.getLong("idDataSchema"));
			listDataDefinition.setNonNull(rs.getString("nonNull"));
			listDataDefinition.setPrimaryKey(rs.getString("primaryKey"));
			listDataDefinition.setRecordAnomaly(rs.getString("recordAnomaly"));
			listDataDefinition.setRecordAnomalyThreshold(rs.getDouble("recordAnomalyThreshold"));
			listDataDefinition.setDataDrift(rs.getString("dataDrift"));
			listDataDefinition.setDataDriftThreshold(rs.getDouble("dataDriftThreshold"));
			listDataDefinition.setOutOfNormStat(rs.getString("outOfNormStat"));
			listDataDefinition.setOutOfNormStatThreshold(rs.getDouble("outOfNormStatThreshold"));
			listDataDefinition.setIsMasked(rs.getString("isMasked"));
			listDataDefinition.setPartitionBy(rs.getString("partitionBy"));
			listDataDefinition.setLengthCheck(rs.getString("lengthCheck"));
			listDataDefinition.setMaxLengthCheck(rs.getString("maxLengthCheck"));
			listDataDefinition.setLengthValue(rs.getString("lengthValue"));
			listDataDefinition.setApplyRule(rs.getString("applyrule"));
			listDataDefinition.setStartDate(rs.getString("startDate"));
			listDataDefinition.setTimelinessKey(rs.getString("timelinessKey"));
			listDataDefinition.setEndDate(rs.getString("endDate"));
			listDataDefinition.setDefaultCheck(rs.getString("defaultCheck"));
			listDataDefinition.setDefaultValues(rs.getString("defaultValues"));
			listDataDefinition.setPatternCheck(rs.getString("patternCheck"));
			listDataDefinition.setPatterns(rs.getString("patterns"));
			listDataDefinition.setDateRule(rs.getString("dateRule"));
			listDataDefinition.setbadData(rs.getString("badData"));
			listDataDefinition.setDateFormat(rs.getString("dateFormat"));
			listDataDefinition.setCorrelationcolumn(rs.getString("correlationcolumn"));
			listDataDefinition.setLengthThreshold(rs.getDouble("lengthCheckThreshold"));
			listDataDefinition.setBadDataThreshold(rs.getDouble("badDataCheckThreshold"));
			listDataDefinition.setPatternCheckThreshold(rs.getDouble("patternCheckThreshold"));

			return listDataDefinition;
		};
		List<ListDataDefinition> ListOfDataDefinition = jdbcTemplate
				.query("select * from listDataDefinition where idData=?", rowMapper, idData);
		return ListOfDataDefinition;
	}

	public List<ListColRules> getListColRulesData(Long idData) {
		RowMapper<ListColRules> rowMapper = (rs, i) -> {
			ListColRules ListColRules = new ListColRules();
			ListColRules.setIdData(rs.getInt("idData"));
			ListColRules.setIdCol(rs.getInt("idCol"));
			ListColRules.setRuleName(rs.getString("ruleName"));
			ListColRules.setRuleType(rs.getString("ruleType"));
			ListColRules.setExpression(rs.getString("expression"));
			ListColRules.setExternal(rs.getString("external"));
			ListColRules.setExternalDatasetName(rs.getString("externalDatasetName"));
			ListColRules.setIdRightData(rs.getInt("idRightData"));
			ListColRules.setMatchingRules(rs.getString("matchingRules"));
			ListColRules.setMatchType(rs.getString("matchType"));
			ListColRules.setSourcetemplateone(rs.getString("sourcetemplateone"));
			ListColRules.setSourcetemplatesecond(rs.getString("sourcetemplatesecond"));
			ListColRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
			ListColRules.setCreatedByUser(rs.getString("createdByUser"));
			ListColRules.setProjectId(rs.getLong("project_id"));
			return ListColRules;
		};
		List<ListColRules> ListOfColRules = jdbcTemplate
				.query("select * from listColRules where activeFlag='Y' and idData=?", rowMapper, idData);
		return ListOfColRules;
	}

	public List<ListColGlobalRules> getListColGlobalRulesData(Long idData) {
		RowMapper<ListColGlobalRules> rowMapper = (rs, i) -> {
			ListColGlobalRules ListColGlobalRules = new ListColGlobalRules();
			ListColGlobalRules.setIdListColrules(rs.getLong("idListColrules"));
			ListColGlobalRules.setRuleName(rs.getString("ruleName"));
			ListColGlobalRules.setDescription(rs.getString("description"));
			ListColGlobalRules.setExpression(rs.getString("expression"));
			ListColGlobalRules.setDomain_id(rs.getInt("domain_id"));
			ListColGlobalRules.setProjectId(rs.getLong("project_id"));
			ListColGlobalRules.setRuleType(rs.getString("ruleType"));
			ListColGlobalRules.setExternalDatasetName(rs.getString("externalDatasetName"));
			ListColGlobalRules.setIdRightData(rs.getLong("idRightData"));
			ListColGlobalRules.setMatchingRules(rs.getString("matchingRules"));
			ListColGlobalRules.setCreatedByUser(rs.getString("createdByUser"));
			ListColGlobalRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
			return ListColGlobalRules;
		};
		List<ListColGlobalRules> ListOfColGlobalRules = jdbcTemplate.query(
				"select * from listColGlobalRules where idListColrules in (select ruleId from rule_Template_Mapping where templateid=?)",
				rowMapper, idData);
		return ListOfColGlobalRules;
	}

	public List<SynonymLibrary> getListSynonymLibraryData(Long idData) {
		RowMapper<SynonymLibrary> rowMapper = (rs, i) -> {
			SynonymLibrary SynonymLibrary = new SynonymLibrary();
			SynonymLibrary.setSynonymsId(rs.getInt("synonyms_Id"));
			SynonymLibrary.setDomainId(rs.getInt("domain_Id"));
			SynonymLibrary.setTableColumn(rs.getString("tableColumn"));
			SynonymLibrary.setPossibleNames(rs.getString("possiblenames"));
			return SynonymLibrary;
		};
		List<SynonymLibrary> ListOfSynonymLibrary = jdbcTemplate.query(
				"select * from SynonymLibrary where synonyms_Id in (select synonym_id from ruleTosynonym where rule_id in (select ruleId from rule_Template_Mapping where templateid=? ))",
				rowMapper, idData);
		return ListOfSynonymLibrary;
	}

	public List<ListDataSchema> getListDataSchema(long idDataSchema) {
		String sql = "SELECT * from listDataSchema where idDataSchema=" + idDataSchema;
		LOG.debug("sql:" + sql);
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				// alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				String str = rs.getString("databaseSchema").replaceAll(",", "#");
				alistdataschema.setDatabaseSchema(str);
				alistdataschema.setUsername(rs.getString("username"));
				// alistdataschema.setPassword(rs.getString("password"));
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
				decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String decryptedText = decryptor.decrypt(rs.getString("password"));
				alistdataschema.setPassword(decryptedText);
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setGss_jaas(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setAutoGenerate(rs.getString("autoGenerate"));
				alistdataschema.setSuffixes(rs.getString("autoGenerate"));
				alistdataschema.setPrefixes(rs.getString("autoGenerate"));
				alistdataschema.setCreatedAt(rs.getDate("createdAt"));
				alistdataschema.setUpdatedAt(rs.getDate("updatedAt"));
				alistdataschema.setCreatedBy(rs.getLong("createdBy"));
				alistdataschema.setUpdatedBy(rs.getLong("updatedBy"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				/*
				 * alistdataschema.setsslTrustStorePath(rs.getString("sslTrustStorePath"));
				 * alistdataschema.settrustPassword(rs.getString("trustPassword"));
				 */
				alistdataschema.setHivejdbchost(rs.getString("hivejdbchost"));
				alistdataschema.setHivejdbcport(rs.getString("hivejdbcport"));
				alistdataschema.setAction(rs.getString("Action"));
				alistdataschema.setGatewayPath(rs.getString("gatewayPath"));
				alistdataschema.setJksPath(rs.getString("jksPath"));
				alistdataschema.setZookeeperUrl(rs.getString("zookeeperUrl"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setFolderPath(rs.getString("folderPath"));
				alistdataschema.setFileNamePattern(rs.getString("fileNamePattern"));
				alistdataschema.setFileDataFormat(rs.getString("fileDataFormat"));
				alistdataschema.setHeaderPresent(rs.getString("headerPresent"));
				alistdataschema.setHeaderFilePath(rs.getString("headerFilePath"));
				alistdataschema.setHeaderFileNamePattern(rs.getString("headerFileNamePattern"));
				alistdataschema.setHeaderFileDataFormat(rs.getString("headerFileDataFormat"));
				alistdataschema.setBucketName(rs.getString("bucketName"));
				alistdataschema.setAccessKey(rs.getString("accessKey"));
				alistdataschema.setSecretKey(rs.getString("secretKey"));
				alistdataschema.setIdSORs(rs.getLong("idSORs"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	public String insertIntoListApplications(String readLine, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listApplications (name, description, "
				+ "appType, idData, idRightData, createdBy, createdAt, updatedAt, updatedBy, "
				+ "fileNameValidation, entityColumn, colOrderValidation, matchingThreshold, "
				+ "nonNullCheck, numericalStatCheck, stringStatCheck, recordAnomalyCheck, "
				+ "incrementalMatching, incrementalTimestamp, dataDriftCheck, updateFrequency, "
				+ "frequencyDays, recordCountAnomaly, recordCountAnomalyThreshold, timeSeries, "
				+ "keyGroupRecordCountAnomaly, outOfNormCheck, applyRules, applyDerivedColumns, "
				+ "csvDir, groupEquality, groupEqualityThreshold, buildHistoricFingerPrint, "
				+ "historicStartDate, historicEndDate, historicDateFormat, active, correlationcheck, "
				+ "project_id, lengthCheck, maxLengthCheck,timelinessKeyCheck, defaultCheck, defaultValues, "
				+ "patternCheck, dateRuleCheck, badData) values(" + importMap.get("name") + ", "
				+ importMap.get("description") + ", " + importMap.get("appType") + ", " + idData + ", "
				+ importMap.get("idRightData") + "," + importMap.get("createdBy") + ", " + importMap.get("createdAt")
				+ ", " + importMap.get("updatedAt") + ", " + importMap.get("updatedBy") + ", "
				+ importMap.get("fileNameValidation") + ", " + importMap.get("entityColumn") + ", "
				+ importMap.get("colOrderValidation") + ", " + importMap.get("matchingThreshold") + ", "
				+ importMap.get("nonNullCheck") + ", " + importMap.get("numericalStatCheck") + ", "
				+ importMap.get("stringStatCheck") + ", " + importMap.get("recordAnomalyCheck") + ", "
				+ importMap.get("incrementalMatching") + ", " + importMap.get("incrementalTimestamp") + ", "
				+ importMap.get("dataDriftCheck") + ", " + importMap.get("updateFrequency") + ", "
				+ importMap.get("frequencyDays") + ", " + importMap.get("recordCountAnomaly") + ", "
				+ importMap.get("recordCountAnomalyThreshold") + ", " + importMap.get("timeSeries") + ", "
				+ importMap.get("keyGroupRecordCountAnomaly") + ", " + importMap.get("outOfNormCheck") + ", "
				+ importMap.get("applyRules") + ", " + importMap.get("applyDerivedColumns") + ", "
				+ importMap.get("csvDir") + ", " + importMap.get("groupEquality") + ", "
				+ importMap.get("groupEqualityThreshold") + ", " + importMap.get("buildHistoricFingerPrint") + ", "
				+ importMap.get("historicStartDate") + ", " + importMap.get("historicEndDate") + ", "
				+ importMap.get("historicDateFormat") + ", " + importMap.get("active") + ", "
				+ importMap.get("correlationcheck") + ", " + importMap.get("project_id") + ", "
				+ importMap.get("lengthCheck") + "," + importMap.get("maxLengthCheck") + " ,"
				+ importMap.get("timelinessKeyCheck") + ", " + importMap.get("defaultCheck") + ", "
				+ importMap.get("defaultValues") + ", " + importMap.get("patternCheck") + ", "
				+ importMap.get("dateRuleCheck") + ", " + importMap.get("badData") + ")";

		return sql;
	}

	public String insertIntoListDataSources(String readLine, Long idDataSchema) {
		importMap.putAll(getMap(readLine));
		sql = "Insert into listDataSources(name, description, dataLocation, dataSource, createdBy, idDataBlend, "
				+ "createdAt, updatedAt, updatedBy, schemaName, idDataSchema, ignoreRowsCount, active, project_id) "
				+ "values(" + importMap.get("name") + ", " + importMap.get("description") + ", "
				+ importMap.get("dataLocation") + ", " + importMap.get("dataSource") + ", " + importMap.get("createdBy")
				+ "," + null + "," + importMap.get("createdAt") + "," + importMap.get("updatedAt") + ","
				+ importMap.get("updatedBy") + ", " + importMap.get("schemaName") + ", " + idDataSchema + ", "
				+ importMap.get("ignoreRowsCount") + ", " + importMap.get("active") + ", " + importMap.get("project_id")
				+ ")";
		return sql;
	}

	public String insertIntoListDataAccess(String readLine, Long idDataSchema, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataAccess (idData,hostName, portName, userName, pwd, schemaName, folderName, query, incrementalType, idDataSchema, "
				+ "whereCondition, domain, fileHeader, dateFormat, sliceStart, sliceEnd, queryString, "
				+ "metaData, isRawData, hivejdbchost, hivejdbcport, sslEnb, sslTrustStorePath, "
				+ "trustPassword) values(" + idData + "," + importMap.get("hostName") + ", " + importMap.get("portName")
				+ ", " + importMap.get("userName") + ", " + importMap.get("pwd") + ", " + importMap.get("schemaName")
				+ ", " + importMap.get("folderName") + ", " + importMap.get("query") + ", "
				+ importMap.get("incrementalType") + ", " + idDataSchema + ", " + importMap.get("whereCondition") + ", "
				+ importMap.get("domain") + ", " + importMap.get("fileHeader") + ", " + importMap.get("dateFormat")
				+ ", " + importMap.get("sliceStart") + ", " + importMap.get("sliceEnd") + ", "
				+ importMap.get("queryString") + ", " + importMap.get("metaData") + ", " + importMap.get("isRawData")
				+ ", " + importMap.get("hivejdbchost") + ", " + importMap.get("hivejdbcport") + ", "
				+ importMap.get("sslEnb") + ", " + importMap.get("sslTrustStorePath") + ", "
				+ importMap.get("trustPassword") + ")";
		return sql;
	}

	public String insertIntoListDataDefination(String readLine, Long idDataSchema, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataDefinition (idData, columnName, "
				+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
				+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
				+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
				+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
				+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
				+ "lengthCheck, maxLengthCheck, lengthValue, applyrule, startDate, timelinessKey, endDate, "
				+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, " + "dateFormat) values("
				+ idData + "," + importMap.get("columnName") + ", " + importMap.get("displayName") + ", "
				+ importMap.get("format") + ", " + importMap.get("hashValue") + ", " + importMap.get("numericalStat")
				+ ", " + importMap.get("stringStat") + ", " + importMap.get("nullCountThreshold") + ", "
				+ importMap.get("numericalThreshold") + ", " + importMap.get("stringStatThreshold") + ", "
				+ importMap.get("KBE") + ", " + importMap.get("dgroup") + ", " + importMap.get("dupkey") + ", "
				+ importMap.get("measurement") + ", " + importMap.get("blend") + ", " + importMap.get("idCol") + ", "
				+ importMap.get("incrementalCol") + ", " + idDataSchema + ", " + importMap.get("nonNull") + ", "
				+ importMap.get("primaryKey") + ", " + importMap.get("recordAnomaly") + ", CAST("
				+ importMap.get("recordAnomalyThreshold") + " AS DECIMAL(9,2)), " + importMap.get("dataDrift")
				+ ", CAST(" + importMap.get("dataDriftThreshold") + " AS DECIMAL(9,2)), "
				+ importMap.get("outOfNormStat") + ", CAST(" + importMap.get("outOfNormStatThreshold")
				+ " AS DECIMAL(9,2)), " + importMap.get("isMasked") + ", " + importMap.get("correlationcolumn") + ", "
				+ importMap.get("partitionBy") + ", " + importMap.get("lengthCheck") + ", "
				+ importMap.get("maxLengthCheck") + "," + importMap.get("lengthValue") + ", "
				+ importMap.get("applyrule") + ", " + importMap.get("startDate") + ", " + importMap.get("timelinessKey")
				+ ", " + importMap.get("endDate") + ", " + importMap.get("defaultCheck") + ", "
				+ importMap.get("defaultValues") + ", " + importMap.get("patternCheck") + ", "
				+ importMap.get("patterns") + ", " + importMap.get("dateRule") + ", " + importMap.get("badData") + ", "
				+ importMap.get("dateFormat") + ")";
		return sql;
	}

	public String insertIntoConnection(String readLine) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataSchema(schemaName, schemaType, ipAddress, databaseSchema, username, password, port, project_id, domain, "
				+ "gss_jaas, krb5conf, autoGenerate, suffixes, prefixes, createdAt, updatedAt, createdBy, updatedBy, hivejdbchost, hivejdbcport, "
				+ "sslEnb, sslTrustStorePath, trustPassword, Action) values(" + importMap.get("schemaName") + ", "
				+ importMap.get("schemaType") + ", " + importMap.get("ipAddress") + ", "
				+ importMap.get("databaseSchema") + ", " + importMap.get("username") + ", " + importMap.get("password")
				+ ", " + importMap.get("port") + ", " + importMap.get("project_id") + ", " + importMap.get("domain")
				+ ", " + importMap.get("gss_jaas") + ", " + importMap.get("krb5conf") + ", "
				+ importMap.get("autoGenerate") + ", " + importMap.get("suffixes") + ", " + importMap.get("prefixes")
				+ "," + importMap.get("createdAt") + ", " + importMap.get("updatedAt") + ", "
				+ importMap.get("createdBy") + ", " + importMap.get("updatedBy") + ", " + importMap.get("hivejdbchost")
				+ ", " + importMap.get("hivejdbcport") + ", " + importMap.get("sslEnb") + ", "
				+ importMap.get("sslTrustStorePath") + ", " + importMap.get("trustPassword") + ", 'Yes')";
		return sql;
	}

	public Map<String, String> getMap(String readLine) {
		// TODO Auto-generated method stub
		Map<String, String> myMap = new HashMap<String, String>();
		String[] pairs = readLine.split("\\|");
		for (int i = 0; i < pairs.length; i++) {
			String pair = pairs[i];
			String[] keyValue = pair.split(":=");
			if (keyValue.length == 2) { // password=password@12345akshay
				LOG.debug(keyValue[0] + " " + keyValue[1]);
				if (keyValue[0].equalsIgnoreCase("password") || keyValue[0].equalsIgnoreCase("pwd")) {// decrypt
																										// password
					StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
					encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
					String encryptedText = encryptor.encrypt(keyValue[1]);
					myMap.put(keyValue[0], "'" + encryptedText + "'");
				} else if (keyValue[0].equalsIgnoreCase("databaseSchema")
						|| keyValue[0].equalsIgnoreCase("schemaName")) {// databaseSchema=TEST#TEST1
					String str = keyValue[1].replaceAll("#", ",");
					LOG.debug("keyValue[1]--" + str);
					myMap.put(keyValue[0], "'" + str + "'");
				} else {
					myMap.put(keyValue[0], "'" + keyValue[1] + "'");
				}
			} else {
				if (keyValue[0].equalsIgnoreCase("password") || keyValue[0].equalsIgnoreCase("pwd")) {// do here
																										// filesystem
																										// encryp of
																										// password''
																										// then insert
					StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
					encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
					String encryptedText = encryptor.encrypt("");
					LOG.debug(keyValue[0] + " " + encryptedText);
					myMap.put(keyValue[0], "'" + encryptedText + "'");

				} else {
					LOG.debug(keyValue[0] + " " + "NULL");
					myMap.put(keyValue[0], "''");
				}
			}

		}
		return myMap;
	}

	public Long exportDataForSpecificType(String type, String nextIdField, Long id, PrintWriter pw) {
		Long nextId = -1L;
		Object obj = new Object();
		List<String> fldListWithValues = new ArrayList<String>();
		if (type.equals("VC")) {
			pw.println("#VC:");
			obj = new ListApplications();
			obj = getdatafromlistapplications(id);
			fldListWithValues = getFieldListWithValues(obj);
			String fieldString = String.join(",", fldListWithValues);
			pw.println(fieldString);

			if (nextIdField != null) {
				nextId = getNextIdForObj(obj, nextIdField);
			}
		} else if (type.equals("DT")) {
			pw.println("#DT:");
			pw.println("#LDS:");
			obj = new ListDataSource();
			obj = getDataFromListDataSources(id);
			fldListWithValues = getFieldListWithValues(obj);
			String fieldString = String.join(",", fldListWithValues);
			pw.println(fieldString);
			obj = new listDataAccess();
			pw.println("#LDA:");
			obj = getListDataAccess(id);
			fldListWithValues = getFieldListWithValues(obj);
			fieldString = String.join(",", fldListWithValues);
			pw.println(fieldString);
			if (nextIdField != null) {
				nextId = getNextIdForObj(obj, nextIdField);
			}
			obj = new ListDataDefinition();

			ListIterator<ListDataDefinition> li = getListDataDefinitionData(id).listIterator();
			while (li.hasNext()) {
				pw.println("#LDD:");
				obj = li.next();
				fldListWithValues = getFieldListWithValues(obj);
				fieldString = String.join(",", fldListWithValues);
				pw.println(fieldString);
			}

		} else if (type.equals("CN")) {
			pw.println("#CN:");
			obj = new ListDataSchema();
			obj = getListDataSchema(id).get(0);
			fldListWithValues = getFieldListWithValues(obj);
			String fieldString = String.join(",", fldListWithValues);
			pw.println(fieldString);

			if (nextIdField != null) {
				nextId = getNextIdForObj(obj, nextIdField);
			}
		}
		return nextId;
	}

	private List<String> getFieldListWithValues(Object obj) {
		Class objClass = obj.getClass();
		LOG.debug(objClass.getName());
		Field[] fieldList = objClass.getDeclaredFields();
		// Field[] fieldList = objClass.getFields();
		List<String> fldListWithValues = new ArrayList<String>();
		for (int i = 0; i < fieldList.length; i++) {
			String fldString = "";
			try {
				fieldList[i].setAccessible(true);
				fldString = fieldList[i].getName() + "=" + fieldList[i].get(obj);
			} catch (Exception e) {
				LOG.error("Exception while retrieving field value:" + e.getMessage());
			}
			if (fldString.length() > 0) {
				fldListWithValues.add(fldString);
			}
		}

		return fldListWithValues;
	}

	private Long getNextIdForObj(Object obj, String nextIdField) {
		Class objClass = obj.getClass();
		Field[] fieldList = objClass.getDeclaredFields();
		Long nextId = -1L;
		for (int i = 0; i < fieldList.length; i++) {
			try {
				fieldList[i].setAccessible(true);
				if (fieldList[i].getName().equalsIgnoreCase(nextIdField)) {
					nextId = Long.valueOf(fieldList[i].get(obj).toString());
				}
			} catch (Exception e) {
				LOG.error("Exception while retrieving field value:" + fieldList[i] + ":" + e.getMessage());
			}

		}
		return nextId;
	}

	// import utility

	public String insertIntoListApplicationsForImport(String readLine, Long idData, Long projectId) throws Exception {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String sDate1="2019-02-07 15:38:22";
		Date date1 = new Date();
		LOG.debug("Date1 =>" + date1);

		LOG.debug("importMap.get(\"historicStartDate\")" + importMap.get("historicStartDate"));
		LOG.debug("importMap.get(\"incrementalTimestamp\")" + importMap.get("incrementalTimestamp"));
		LOG.debug("importMap.get(\"historicEndDate\")" + importMap.get("historicEndDate"));

		sql = "insert into listApplications (name, description, "
				+ "appType, idData, idRightData, createdBy, createdAt, updatedAt, updatedBy, "
				+ "fileNameValidation, entityColumn, colOrderValidation, matchingThreshold, "
				+ "nonNullCheck, numericalStatCheck, stringStatCheck, recordAnomalyCheck, "
				+ "incrementalMatching, incrementalTimestamp, dataDriftCheck, updateFrequency, "
				+ "frequencyDays, recordCountAnomaly, recordCountAnomalyThreshold, timeSeries, "
				+ "keyGroupRecordCountAnomaly, outOfNormCheck, applyRules, applyDerivedColumns, "
				+ "csvDir, groupEquality, groupEqualityThreshold, buildHistoricFingerPrint, "
				+ "historicStartDate, historicEndDate, historicDateFormat, active, correlationcheck, "
				+ "project_id, lengthCheck, maxLengthCheck,timelinessKeyCheck, defaultCheck, defaultValues, "
				+ "patternCheck, dateRuleCheck, badData) values(" + importMap.get("name") + ", "
				+ importMap.get("description") + ", " + importMap.get("appType") + ", " + idData + ", "
				+ importMap.get("idRightData") + "," + importMap.get("createdBy") + ", " + importMap.get("createdAt")
				+ ", " + importMap.get("updatedAt") + ", " + importMap.get("updatedBy") + ", "
				+ importMap.get("fileNameValidation") + ", " + importMap.get("entityColumn") + ", "
				+ importMap.get("colOrderValidation") + ", " + importMap.get("matchingThreshold") + ", "
				+ importMap.get("nonNullCheck") + ", " + importMap.get("numericalStatCheck") + ", "
				+ importMap.get("stringStatCheck") + ", " + importMap.get("recordAnomalyCheck") + ", "
				+ importMap.get("incrementalMatching") + ", " + null + ", " + importMap.get("dataDriftCheck") + ", "
				+ importMap.get("updateFrequency") + ", " + importMap.get("frequencyDays") + ", "
				+ importMap.get("recordCountAnomaly") + ", " + importMap.get("recordCountAnomalyThreshold") + ", "
				+ importMap.get("timeSeries") + ", " + importMap.get("keyGroupRecordCountAnomaly") + ", "
				+ importMap.get("outOfNormCheck") + ", " + importMap.get("applyRules") + ", "
				+ importMap.get("applyDerivedColumns") + ", " + importMap.get("csvDir") + ", "
				+ importMap.get("groupEquality") + ", " + importMap.get("groupEqualityThreshold") + ", "
				+ importMap.get("buildHistoricFingerPrint") + ", " + null + ", " + null + ", "
				+ importMap.get("historicDateFormat") + ", " + importMap.get("active") + ", "
				+ importMap.get("correlationcheck") + ", " + projectId + ", " + importMap.get("lengthCheck") + ", "
				+ importMap.get("maxLengthCheck") + "," + importMap.get("timelinessKeyCheck") + ", "
				+ importMap.get("defaultCheck") + ", " + importMap.get("defaultValues") + ", "
				+ importMap.get("patternCheck") + ", " + importMap.get("dateRuleCheck") + ", "
				+ importMap.get("badData") + ")";

		return sql;

	}

	public String insertIntoListApplicationsForImportdirect(String readLine, Long projectId, long idData)
			throws Exception {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String sDate1="2019-02-07 15:38:22";
		Date date1 = new Date();
		/*
		 * LOG.debug("Date1 =>" + date1);
		 * 
		 * LOG.debug("importMap.get(\"historicStartDate\")" +
		 * importMap.get("historicStartDate"));
		 * LOG.debug("importMap.get(\"incrementalTimestamp\")" +
		 * importMap.get("incrementalTimestamp"));
		 * LOG.debug("importMap.get(\"historicEndDate\")" +
		 * importMap.get("historicEndDate"));
		 */

		/*
		 * schema=null,,idApp=958,,, =0,,
		 * =N,garbageRows=0,=,=N,=N,nonNullThreshold=null,
		 * =Y,numericalStatThreshold=null,=N,stringStatThreshold=null,=Y,
		 * recordAnomalyThreshold=null,=Y,dataDriftThreshold=null,=Y,=3.0,
		 * =N,=Y,=N,=N,=Never, =null,=0,=N,=null,=null, =,=N,=,=N,=0.0,=None,
		 * =0.0,=null,dupRowAll=null,dupRowIdentity=null,dupRowAllThreshold=null,
		 * dupRowIdentityThreshold=null,duplicateCheck=null,startDateChk=null,endDateChk
		 * =null,=N,=Y, =Y,=N,=Y,=null,=null,=null,=N,
		 * =N,=yes,windowTime=0,startTime=null,endTime=null,=local,=1.0,
		 * =N,=0,=2,=N,=null, =N,=15,=Y
		 */
		sql = "insert into listApplications (name, description, "
				+ "appType, idData, idRightData, idLeftData	,createdBy, createdAt, updatedAt, updatedBy, "
				+ "fileNameValidation, entityColumn, colOrderValidation, matchingThreshold, "
				+ "nonNullCheck, numericalStatCheck, stringStatCheck, recordAnomalyCheck, "
				+ "incrementalMatching, incrementalTimestamp, dataDriftCheck, updateFrequency, "
				+ "frequencyDays, recordCountAnomaly, recordCountAnomalyThreshold, timeSeries, "
				+ "keyGroupRecordCountAnomaly, outOfNormCheck, applyRules, applyDerivedColumns, "
				+ "csvDir, groupEquality, groupEqualityThreshold, buildHistoricFingerPrint, "
				+ "historicStartDate, historicEndDate, historicDateFormat, active, correlationcheck, "
				+ "project_id, lengthCheck,maxLengthCheck , timelinessKeyCheck, defaultCheck, defaultValues, "
				+ "patternCheck, dateRuleCheck, badData,prefix1,prefix2,dGroupNullCheck,dGroupDateRuleCheck,fileMonitoringType,"
				+ "validityThreshold,dGroupDataDriftCheck,rollTargetSchemaId,thresholdsApplyOption,continuousFileMonitoring,rollType) values("
				+ importMap.get("name") + ", " + importMap.get("description") + ", " + importMap.get("appType") + ", "
				+ idData + ", " + importMap.get("idRightData") + "," + importMap.get("idLeftData") + ","
				+ importMap.get("createdBy") + ", " + importMap.get("createdAt") + ", " + importMap.get("updatedAt")
				+ ", " + importMap.get("updatedBy") + ", " + importMap.get("fileNameValidation") + ", "
				+ importMap.get("entityColumn") + ", " + importMap.get("colOrderValidation") + ", "
				+ importMap.get("matchingThreshold") + ", " + importMap.get("nonNullCheck") + ", "
				+ importMap.get("numericalStatCheck") + ", " + importMap.get("stringStatCheck") + ", "
				+ importMap.get("recordAnomalyCheck") + ", " + importMap.get("incrementalMatching") + ", " + null + ", "
				+ importMap.get("dataDriftCheck") + ", " + importMap.get("updateFrequency") + ", "
				+ importMap.get("frequencyDays") + ", " + importMap.get("recordCountAnomaly") + ", "
				+ importMap.get("recordCountAnomalyThreshold") + ", " + importMap.get("timeSeries") + ", "
				+ importMap.get("keyGroupRecordCountAnomaly") + ", " + importMap.get("outOfNormCheck") + ", "
				+ importMap.get("applyRules") + ", " + importMap.get("applyDerivedColumns") + ", "
				+ importMap.get("csvDir") + ", " + importMap.get("groupEquality") + ", "
				+ importMap.get("groupEqualityThreshold") + ", " + importMap.get("buildHistoricFingerPrint") + ", "
				+ null + ", " + null + ", " + importMap.get("historicDateFormat") + ", " + importMap.get("active")
				+ ", " + importMap.get("correlationcheck") + ", " + projectId + ", " + importMap.get("lengthCheck")
				+ ", " + importMap.get("maxLengthCheck") + "," + importMap.get("timelinessKeyCheck") + ", "
				+ importMap.get("defaultCheck") + ", " + importMap.get("defaultValues") + ", "
				+ importMap.get("patternCheck") + ", " + importMap.get("dateRuleCheck") + ", "
				+ importMap.get("badData") + ", " + importMap.get("prefix1") + ", " + importMap.get("prefix2") + ", "
				+ importMap.get("dGroupNullCheck") + ", " + importMap.get("dGroupDateRuleCheck") + ", "
				+ importMap.get("fileMonitoringType") + ", " + importMap.get("validityThreshold") + ", "
				+ importMap.get("dGroupDataDriftCheck") + ", " + importMap.get("rollTargetSchemaId") + ", "
				+ importMap.get("thresholdsApplyOption") + ", " + importMap.get("continuousFileMonitoring") + ", "
				+ importMap.get("rollType") + ")";

		return sql;

	}

	public String insertIntoListDataSourcesForImport(String readLine, Long idDataSchema, Long projectId) {
		importMap.putAll(getMap(readLine));
		sql = "Insert into listDataSources(name, description, dataLocation, dataSource, createdBy, idDataBlend, "
				+ "createdAt, updatedAt, updatedBy, schemaName, idDataSchema, ignoreRowsCount, active, project_id,domain_id) "
				+ "values(" + importMap.get("name") + ", " + importMap.get("description") + ", "
				+ importMap.get("dataLocation") + ", " + importMap.get("dataSource") + ", " + importMap.get("createdBy")
				+ "," + null + "," + importMap.get("createdAt") + "," + importMap.get("updatedAt") + ","
				+ importMap.get("updatedBy") + ", " + importMap.get("schemaName") + ", " + idDataSchema + ", "
				+ importMap.get("ignoreRowsCount") + ", " + importMap.get("active") + ", " + projectId + ", "
				+ importMap.get("domain") + ")";
		return sql;
	}

	/*
	 * idData=716,name=bug_fix_01_empdata,description=bug_fix_01,dataLocation=
	 * Postgres,dataSource=SQL,createdBy=1,
	 * idDataBlend=0,createdAt=2020-07-30,updatedAt=2020-07-30,updatedBy=1,
	 * idDataSchema=44,garbageRows=null,tableName=null,
	 * ignoreRowsCount=0,domain=3,srcBrokerUri=null,srcTopicName=null,tarBrokerUri=
	 * null,tarTopicName=null,profilingEnabled=N,
	 * advancedRulesEnabled=N,schemaName=null,active=yes,createdByUser=Admin
	 * User,projectId=15
	 * profilingEnabled,advancedRulesEnabled,createdByUser,domain_id
	 */
	public String insertIntoListDataSourcesForImportdirect(String readLine, Long projectId, Long idDataSchema) {
		importMap.putAll(getMap(readLine));
		String idDataSchemaValue = "";
		if (idDataSchema != 0) {
			idDataSchemaValue = String.valueOf(idDataSchema);
		} else {
			idDataSchemaValue = importMap.get("idDataSchema");
		}
		sql = "Insert into listDataSources(name, description, dataLocation, dataSource, createdBy, idDataBlend, "
				+ "createdAt, updatedAt, updatedBy, schemaName, idDataSchema, ignoreRowsCount, active, project_id,"
				+ "profilingEnabled,advancedRulesEnabled,createdByUser,domain_id) " + "values(" + importMap.get("name")
				+ ", " + importMap.get("description") + ", " + importMap.get("dataLocation") + ", "
				+ importMap.get("dataSource") + ", " + importMap.get("createdBy") + "," + null + ","
				+ importMap.get("createdAt") + "," + importMap.get("updatedAt") + "," + importMap.get("updatedBy")
				+ ", " + importMap.get("schemaName") + ", " + idDataSchemaValue + ", "
				+ importMap.get("ignoreRowsCount") + ", " + importMap.get("active") + ", " + projectId + ", "
				+ importMap.get("profilingEnabled") + ", " + importMap.get("advancedRulesEnabled") + ", "
				+ importMap.get("createdByUser") + ", " + importMap.get("domain") + ")";
		return sql;
	}

	public String insertIntoListDataAccessForImport(String readLine, Long idDataSchema, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataAccess (idData,hostName, portName, userName, pwd, schemaName, folderName, query, incrementalType, idDataSchema, "
				+ "whereCondition, domain, fileHeader, dateFormat, sliceStart, sliceEnd, queryString, "
				+ "metaData, isRawData, hivejdbchost, hivejdbcport, sslEnb, sslTrustStorePath, "
				+ "trustPassword) values(" + idData + "," + importMap.get("hostName") + ", " + importMap.get("portName")
				+ ", " + importMap.get("userName") + ", " + importMap.get("pwd") + ", " + importMap.get("schemaName")
				+ ", " + importMap.get("folderName") + ", " + importMap.get("query") + ", "
				+ importMap.get("incrementalType") + ", " + idDataSchema + ", " + importMap.get("whereCondition") + ", "
				+ importMap.get("domain") + ", " + importMap.get("fileHeader") + ", " + importMap.get("dateFormat")
				+ ", " + importMap.get("sliceStart") + ", " + importMap.get("sliceEnd") + ", "
				+ importMap.get("queryString") + ", " + importMap.get("metaData") + ", " + importMap.get("isRawData")
				+ ", " + importMap.get("hivejdbchost") + ", " + importMap.get("hivejdbcport") + ", "
				+ importMap.get("sslEnb") + ", " + importMap.get("sslTrustStorePath") + ", "
				+ importMap.get("trustPassword") + ")";
		return sql;
	}

	/*
	 * idlistDataAccess=687,idData=687,hostName=vs90372.us-east-1.snowflakecomputing
	 * .com,portName=443,
	 * userName=angsuman,pwd=A8hoy6ZIf8qH93pezwddBzeNe9dDGvKU,schemaName=TEST,TEST1,
	 * folderName=MS,
	 * queryString=,query=N,idDataSchema=109,whereCondition=,domain=,incrementalType
	 * =null,dateFormat=,
	 * sliceStart=,sliceEnd=,fileHeader=Y,isRawData=null,hivejdbcport=null,
	 * hivejdbchost=null,sslEnb=null,
	 * sslTrustStorePath=null,trustPassword=null,metaData=null,gatewayPath=null,
	 * jksPath=null,
	 * zookeeperUrl=null,rollingHeader=N,rollingColumn=,rollTargetSchemaId=0
	 * gatewayPath,jksPath,zookeeperUrl,rollingHeader,rollingColumn,
	 * rollTargetSchemaId
	 */
	public String insertIntoListDataAccessForImportdirect(String readLine, Long idData, Long idDataSchema) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		String idDataSchemaValue = "";
		String hostName = "";
		if (idDataSchema != 0) {
			idDataSchemaValue = String.valueOf(idDataSchema);
			hostName = jdbcTemplate.queryForObject(
					"select hostName from listDataAccess where idDataSchema =" + idDataSchemaValue + " limit 1",
					String.class);
			hostName = "'" + hostName + "'";
		} else {
			idDataSchemaValue = importMap.get("idDataSchema");
			hostName = importMap.get("hostName");
		}
		String Query = importMap.get("queryString");
		Query = Query.substring(1, Query.length());
		Query = Query.substring(0, Query.length() - 1);
		Query = Query.replace("'", "''");
		LOG.debug("Query--" + Query);
		sql = "insert into listDataAccess (idData,hostName, portName, userName, pwd, schemaName, folderName, query, incrementalType, idDataSchema, "
				+ "whereCondition, domain, fileHeader, dateFormat, sliceStart, sliceEnd, queryString, "
				+ "metaData, isRawData, hivejdbchost, hivejdbcport, sslEnb, sslTrustStorePath, "
				+ "trustPassword,gatewayPath,jksPath,zookeeperUrl,rollingHeader,rollingColumn,rollTargetSchemaId) values("
				+ idData + "," + hostName + ", " + importMap.get("portName") + ", " + importMap.get("userName") + ", "
				+ importMap.get("pwd") + ", " + importMap.get("schemaName") + ", " + importMap.get("folderName") + ", "
				+ importMap.get("query") + ", " + importMap.get("incrementalType") + ", " + idDataSchemaValue + ", "
				+ importMap.get("whereCondition") + ", " + importMap.get("domain") + ", " + importMap.get("fileHeader")
				+ ", " + importMap.get("dateFormat") + ", " + importMap.get("sliceStart") + ", "
				+ importMap.get("sliceEnd") + ", '" + Query + "', " + importMap.get("metaData") + ", "
				+ importMap.get("isRawData") + ", " + importMap.get("hivejdbchost") + ", "
				+ importMap.get("hivejdbcport") + ", " + importMap.get("sslEnb") + ", "
				+ importMap.get("sslTrustStorePath") + ", " + importMap.get("trustPassword") + ", "
				+ importMap.get("gatewayPath") + ", " + importMap.get("jksPath") + ", " + importMap.get("zookeeperUrl")
				+ ", " + importMap.get("rollingHeader") + ", " + importMap.get("rollingColumn") + ", "
				+ importMap.get("rollTargetSchemaId") + ")";
		return sql;
	}

	public String insertIntoListDataDefinationForImport(String readLine, Long idDataSchema, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataDefinition (idData, columnName, "
				+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
				+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
				+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
				+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
				+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
				+ "lengthCheck, lengthValue, applyrule, startDate, timelinessKey, endDate, "
				+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, " + "dateFormat) values("
				+ idData + "," + importMap.get("columnName") + ", " + importMap.get("displayName") + ", "
				+ importMap.get("format") + ", " + importMap.get("hashValue") + ", " + importMap.get("numericalStat")
				+ ", " + importMap.get("stringStat") + ", " + importMap.get("nullCountThreshold") + ", "
				+ importMap.get("numericalThreshold") + ", " + importMap.get("stringStatThreshold") + ", "
				+ importMap.get("KBE") + ", " + importMap.get("dgroup") + ", " + importMap.get("dupkey") + ", "
				+ importMap.get("measurement") + ", " + importMap.get("blend") + ", " + importMap.get("idCol") + ", "
				+ importMap.get("incrementalCol") + ", " + idDataSchema + ", " + importMap.get("nonNull") + ", "
				+ importMap.get("primaryKey") + ", " + importMap.get("recordAnomaly") + ", CAST("
				+ importMap.get("recordAnomalyThreshold") + " AS DECIMAL(9,2)), " + importMap.get("dataDrift")
				+ ", CAST(" + importMap.get("dataDriftThreshold") + " AS DECIMAL(9,2)), "
				+ importMap.get("outOfNormStat") + ", CAST(" + importMap.get("outOfNormStatThreshold")
				+ " AS DECIMAL(9,2)), " + importMap.get("isMasked") + ", " + importMap.get("correlationcolumn") + ", "
				+ importMap.get("partitionBy") + ", " + importMap.get("lengthCheck") + ", "
				+ importMap.get("lengthValue") + ", " + importMap.get("applyrule") + ", " + importMap.get("startDate")
				+ ", " + importMap.get("timelinessKey") + ", " + importMap.get("endDate") + ", "
				+ importMap.get("defaultCheck") + ", " + importMap.get("defaultValues") + ", "
				+ importMap.get("patternCheck") + ", " + importMap.get("patterns") + ", " + importMap.get("dateRule")
				+ ", " + importMap.get("badData") + ", " + importMap.get("dateFormat") + ")";
		return sql;
	}

	/*
	 * itemid=null,idColumn=10592,idData=652,columnName=,displayName=revenueamount,
	 * format=numeric,hashValue=N,
	 * numericalStat=Y,stringStat=N,KBE=Y,dgroup=N,dupkey=N,measurement=N,blend=,
	 * idCol=0,numericalThreshold=0.0,
	 * stringStatThreshold=0.0,nullCountThreshold=3.0,idDataSchema=0,incrementalCol=
	 * N,primaryKey=N,nonNull=Y,
	 * recordAnomaly=Y,startDate=N,endDate=N,timelinessKey=N,defaultCheck=N,
	 * defaultValues=N,recordAnomalyThreshold=2.0,
	 * dataDrift=Y,dataDriftThreshold=0.0,outOfNormStat=N,outOfNormStatThreshold=0.0
	 * ,isMasked=N,partitionBy=N,patternCheck=N,
	 * patterns=null,dateRule=N,badData=N,dateFormat=null,correlationcolumn=null,
	 * lengthThreshold=0.0,badDataThreshold=0.0,
	 * patternCheckThreshold=0.0,applyrule=N,lengthCheck=N,lengthValue=0
	 * lengthCheckThreshold,badDataCheckThreshold,patternCheckThreshold
	 */
	public String insertIntoListDataDefinationForImportdirect(String readLine, Long idData) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));
		sql = "insert into listDataDefinition (idData, columnName, "
				+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
				+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
				+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
				+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
				+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
				+ "lengthCheck, lengthValue, applyrule, startDate, timelinessKey, endDate, "
				+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
				+ "dateFormat,lengthCheckThreshold" + ",badDataCheckThreshold,patternCheckThreshold) values(" + idData
				+ "," + importMap.get("columnName") + ", " + importMap.get("displayName") + ", "
				+ importMap.get("format") + ", " + importMap.get("hashValue") + ", " + importMap.get("numericalStat")
				+ ", " + importMap.get("stringStat") + ", " + importMap.get("nullCountThreshold") + ", "
				+ importMap.get("numericalThreshold") + ", " + importMap.get("stringStatThreshold") + ", "
				+ importMap.get("KBE") + ", " + importMap.get("dgroup") + ", " + importMap.get("dupkey") + ", "
				+ importMap.get("measurement") + ", " + importMap.get("blend") + ", " + importMap.get("idCol") + ", "
				+ importMap.get("incrementalCol") + ", " + importMap.get("idDataSchema") + ", "
				+ importMap.get("nonNull") + ", " + importMap.get("primaryKey") + ", " + importMap.get("recordAnomaly")
				+ ", CAST(" + importMap.get("recordAnomalyThreshold") + " AS DECIMAL(9,2)), "
				+ importMap.get("dataDrift") + ", CAST(" + importMap.get("dataDriftThreshold") + " AS DECIMAL(9,2)), "
				+ importMap.get("outOfNormStat") + ", CAST(" + importMap.get("outOfNormStatThreshold")
				+ " AS DECIMAL(9,2)), " + importMap.get("isMasked") + ", " + importMap.get("correlationcolumn") + ", "
				+ importMap.get("partitionBy") + ", " + importMap.get("lengthCheck") + ", "
				+ importMap.get("lengthValue") + ", " + importMap.get("applyrule") + ", " + importMap.get("startDate")
				+ ", " + importMap.get("timelinessKey") + ", " + importMap.get("endDate") + ", "
				+ importMap.get("defaultCheck") + ", " + importMap.get("defaultValues") + ", "
				+ importMap.get("patternCheck") + ", " + importMap.get("patterns") + ", " + importMap.get("dateRule")
				+ ", " + importMap.get("badData") + ", " + importMap.get("dateFormat") + ", "
				+ importMap.get("lengthThreshold") + ", " + importMap.get("badDataCheckThreshold") + ", "
				+ importMap.get("patternCheckThreshold") + ")";
		/*
		 * ",CAST(" + importMap.get("lengthThreshold")+ "AS DECIMAL(9,2)),  CAST(" +
		 * importMap.get("badDataCheckThreshold") +"AS DECIMAL(9,2)),  CAST(" +
		 * importMap.get("patternCheckThreshold") +"AS DECIMAL(9,2)),)";
		 */
		return sql;
	}

	public String insertIntoConnectionForImport(String readLine, Long projectId, Integer domainId) {
		// TODO Auto-generated method stub
		importMap.putAll(getMap(readLine));

		LOG.debug("=====insertIntoConnection====" + importMap.values());

		/*
		 * LOG.debug("importMap.get(\"schemaName\") =>" +
		 * importMap.get("schemaName"));
		 * LOG.debug("importMap.get(\"schemaType\") =>" +
		 * importMap.get("schemaType"));
		 * LOG.debug("importMap.get(\"ipAddress\") =>" +
		 * importMap.get("ipAddress"));
		 * LOG.debug("importMap.get(\"projectId\") =>" +
		 * importMap.get("projectId"));
		 */

		/*
		 * exportnames:
		 * idDataSchema=110,ipAddress=vs90372.us-east-1.snowflakecomputing.com,
		 * databaseSchema=TEST1,TEST2,username=angsuman,
		 * password=9HpWePblEzh/jPa+xI2AcVwC7PlyH4wj,port=443,createdAt=2020-06-05,
		 * updatedAt=2020-06-05,schemaName=Snowflake_Akshay,
		 * domain=,keytab=null,krb5conf=,gss_jaas=,createdBy=1,updatedBy=1,serviceName=
		 * null,hivejdbchost=,hivejdbcport=,sslEnb=,
		 * sslTrustStorePath=,trustPassword=,suffixes=N,prefixes=N,autoGenerate=N,
		 * projectId=15,gatewayPath=,jksPath=,zookeeperUrl=, createdByUser=Admin
		 * User,folderPath=,fileNamePattern=,fileDataFormat=PSV,headerPresent=Y,
		 * headerFilePath=,headerFileNamePattern=,
		 * headerFileDataFormat=PSV,accessKey=,secretKey=,bucketName=,action=Yes,idSORs=
		 * 0,schemaType=SnowFlake
		 */

		/*
		 * tablenames(schemaName,schemaType,ipAddress,databaseSchema,username,password,
		 * port,domain,gss_jaas,krb5conf,autoGenerate,suffixes,prefixes,createdAt,
		 * updatedAt,createdBy,updatedBy,project_id,sslEnb,sslTrustStorePath,
		 * trustPassword,hivejdbcport,hivejdbchost,Action,gatewayPath,jksPath,
		 * zookeeperUrl,createdByUser,folderPath,fileNamePattern,fileDataFormat,
		 * headerPresent,headerFilePath,headerFileNamePattern,headerFileDataFormat,
		 * bucketName,accessKey,secretKey,idSORs)
		 */

		sql = "insert into listDataSchema(schemaName,schemaType,ipAddress,databaseSchema,username,password,port,domain,gss_jaas,krb5conf,autoGenerate,suffixes,prefixes,createdAt,\r\n"
				+ "updatedAt,createdBy,updatedBy,project_id,sslEnb,sslTrustStorePath,trustPassword,hivejdbcport,hivejdbchost,Action,gatewayPath,jksPath,\r\n"
				+ "zookeeperUrl,createdByUser,folderPath,fileNamePattern,fileDataFormat,headerPresent,headerFilePath,headerFileNamePattern,headerFileDataFormat,\r\n"
				+ "bucketName,accessKey,secretKey,idSORs, domain_id) values(" + importMap.get("schemaName") + ", "
				+ importMap.get("schemaType") + ", " + importMap.get("ipAddress") + ", "
				+ importMap.get("databaseSchema") + ", " + importMap.get("username") + ", " + importMap.get("password")
				+ ", " + importMap.get("port") + ", " + importMap.get("domain") + ", " + importMap.get("gss_jaas")
				+ ", " + importMap.get("krb5conf") + ", " + importMap.get("autoGenerate") + ", "
				+ importMap.get("suffixes") + ", " + importMap.get("prefixes") + ", " + importMap.get("createdAt") + ","
				+ importMap.get("updatedAt") + ", " + importMap.get("createdBy") + ", " + importMap.get("updatedBy")
				+ ", " + projectId + " ," + importMap.get("sslEnb") + ", " + importMap.get("sslTrustStorePath") + ", "
				+ importMap.get("trustPassword") + ", " + importMap.get("hivejdbcport") + ", "
				+ importMap.get("hivejdbchost") + ", 'Yes'  ," + importMap.get("gatewayPath") + ", "
				+ importMap.get("jksPath") + ", " + importMap.get("zookeeperUrl") + ", "
				+ importMap.get("createdByUser") + ", " + importMap.get("folderPath") + ", "
				+ importMap.get("fileNamePattern") + ", " + importMap.get("fileDataFormat") + ", "
				+ importMap.get("headerPresent") + ", " + importMap.get("headerFilePath") + ", "
				+ importMap.get("headerFileNamePattern") + ", " + importMap.get("headerFileDataFormat") + ", "
				+ importMap.get("bucketName") + ", " + importMap.get("accessKey") + ", " + importMap.get("secretKey")
				+ ", " + importMap.get("idSORs") + ", " + domainId + ")";

		return sql;
	}

	public String insertIntoListColRulesForImportdirect(String readLine, long idData, Long projectId) {
		importMap.putAll(getMap(readLine));
		String idRightData = importMap.get("idRightData").replace("'", "");
		String ruleThreshold = importMap.get("ruleThreshold").replace("'", "");
		String expression = importMap.get("expression");
		expression = expression.substring(1, expression.length());
		expression = expression.substring(0, expression.length() - 1);
		expression = expression.replace("'", "''");
		LOG.debug("Query--" + expression);
		sql = "insert into listColRules(idData,idCol,ruleName,createdAt,ruleType,expression,external,externalDatasetName,idRightData,matchingRules,matchType,"
				+ "sourcetemplateone,sourcetemplatesecond,ruleThreshold,createdByUser,project_id) values(" + idData
				+ "," + importMap.get("idCol") + "," + importMap.get("ruleName") + ",now()," + importMap.get("ruleType")
				+ ",'" + expression + "'," + importMap.get("external") + "," + importMap.get("externalDatasetName")
				+ "," + idRightData + "," + importMap.get("matchingRules") + "," + importMap.get("matchType") + ","
				+ importMap.get("sourcetemplateone") + "," + importMap.get("sourcetemplatesecond") + "," + ruleThreshold
				+ "," + importMap.get("createdByUser") + "," + projectId + ")";
		return sql;

		/*
		 * sql = "insert into listColRules" +
		 * "(idData,ruleName,ruleType,expression,external," +
		 * "externalDatasetName,idRightData,matchingRules,matchType,sourcetemplateone,sourcetemplatesecond,ruleThreshold,createdByUser,project_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
		 * ; int update = jdbcTemplate.update(sql, new Object[] { idData,
		 * importMap.get("ruleName"), importMap.get("ruleType"),
		 * importMap.get("expression"), importMap.get("external"),
		 * importMap.get("externalDatasetName"), idRightData,
		 * importMap.get("matchingRules"), importMap.get("matchType"),
		 * importMap.get("sourcetemplateone"), importMap.get("sourcetemplatesecond"),
		 * ruleThreshold, importMap.get("createdByUser"), projectId }); return sql;
		 */
	}

	public String insertIntoListColGlobalRulesForImportdirect(String readLine, long idData, Long projectId) {
		importMap.putAll(getMap(readLine));
		String idRightData = importMap.get("idRightData").replace("'", "");
		String ruleThreshold = importMap.get("ruleThreshold").replace("'", "");
		String expression = importMap.get("expression");
		expression = expression.substring(1, expression.length());
		expression = expression.substring(0, expression.length() - 1);
		expression = expression.replace("'", "''");
		LOG.debug("Query--" + expression);
		/*
		 * idListColrules = jdbcTemplate.
		 * queryForObject("select idListColrules from listColGlobalRules where ruleName="
		 * +importMap.get("ruleName")+" and domain_id ="+importMap.get("domain_id")
		 * +" and expression='"+expression+"'", Long.class);
		 */

		/*
		 * String query =
		 * "select idListColrules from listColGlobalRules where ruleName=? and domain_id =? and expression=?"
		 * ;
		 */
		String query = "select idListColrules from listColGlobalRules where ruleName=" + importMap.get("ruleName")
				+ " and domain_id =" + importMap.get("domain_id") + " and expression='" + expression + "'";
		/*
		 * SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query,
		 * importMap.get("ruleName"), importMap.get("domain_id"),newExpression);
		 */
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);

		if (queryForRowSet.next()) {
			// insert into rule_template_mapping
			sql = "insert into rule_Template_Mapping(templateid,ruleId,ruleName,ruleExpression,ruleType) values("
					+ idData + "," + queryForRowSet.getInt(1) + "," + importMap.get("ruleName") + ",'" + expression
					+ "'," + importMap.get("ruleType") + ")";

		} else {
			// insert into listGLobalRule
			// insert rule_template_mapping
			String newGlobalRulesql = "insert into listColGlobalRules(ruleName,description,expression,domain_id,project_id,ruleType,externalDatasetName,idRightData,matchingRules,"
					+ "createdByUser,ruleThreshold) values(" + importMap.get("ruleName") + ","
					+ importMap.get("description") + ",'" + expression + "'," + importMap.get("domain_id") + ","
					+ projectId + "," + importMap.get("ruleType") + "," + importMap.get("externalDatasetName") + ","
					+ idRightData + "," + importMap.get("matchingRules") + "," + importMap.get("createdByUser") + ","
					+ ruleThreshold + ")";
			jdbcTemplate.execute(newGlobalRulesql);
			SqlRowSet queryNewForRowSet = jdbcTemplate.queryForRowSet(query);
			if (queryNewForRowSet.next()) {
				LOG.debug("result1:" + queryNewForRowSet.getInt(1));
				sql = "insert into rule_Template_Mapping(templateid,ruleId,ruleName,ruleExpression,ruleType) values("
						+ idData + "," + queryNewForRowSet.getInt(1) + "," + importMap.get("ruleName") + ",'"
						+ expression + "'," + importMap.get("ruleType") + ")";
			}

		}

		return sql;
	}

	public long readGlobalRuleId(String readLine, long idData) {
		importMap.putAll(getMap(readLine));
		long idListColrules = 0l;
		String expression = importMap.get("expression");
		expression = expression.substring(1, expression.length());
		expression = expression.substring(0, expression.length() - 1);
		expression = expression.replace("'", "''");
		LOG.debug("Query--" + expression);
		idListColrules = jdbcTemplate.queryForObject(
				"select idListColrules from listColGlobalRules where ruleName=" + importMap.get("ruleName")
						+ " and domain_id =" + importMap.get("domain_id") + " and expression='" + expression + "'",
				Long.class);
		return idListColrules;
	}

	public void insertIntoSynonymLibraryImportdirect(String readLine, long idData, Long projectId,
			long idDataColGlobalRule) {
		importMap.putAll(getMap(readLine));
		try {
			String querySym = "select synonyms_Id from SynonymLibrary where tableColumn=" + importMap.get("tableColumn")
					+ " and domain_id =" + importMap.get("domainId") + " and possiblenames="
					+ importMap.get("possibleNames");
			SqlRowSet queryForRowSetSym = jdbcTemplate.queryForRowSet(querySym);

			if (queryForRowSetSym.next()) {
				String query = "SELECT * from ruleTosynonym where rule_id=" + idDataColGlobalRule + " and  synonym_id="
						+ queryForRowSetSym.getInt(1);
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);
				if (!queryForRowSet.next()) {
					String mapSynonymsToGlobalRule = "insert into ruleTosynonym(rule_id,synonym_id) values("
							+ idDataColGlobalRule + "," + queryForRowSetSym.getInt(1) + ")";
					jdbcTemplate.execute(mapSynonymsToGlobalRule);
				} else {
					LOG.debug("Synonyms and Global rules are alread Exist");

				}

			} else {
				LOG.debug("idDataColGlobalRule.................................=>" + idDataColGlobalRule);
				String insertIntoSynonymLibrary = "insert into SynonymLibrary(domain_Id,tableColumn,possiblenames) values("
						+ importMap.get("domainId") + "," + importMap.get("tableColumn") + ","
						+ importMap.get("possibleNames") + ")";
				jdbcTemplate.execute(insertIntoSynonymLibrary);
				SqlRowSet queryNewForRowSetSym = jdbcTemplate.queryForRowSet(querySym);
				if (queryNewForRowSetSym.next()) {
					LOG.debug("idDataColGlobalRule..........................=>" + idDataColGlobalRule);
					String mapSynonymsToGlobalRule = "insert into ruleTosynonym(rule_id,synonym_id) values("
							+ idDataColGlobalRule + "," + queryNewForRowSetSym.getInt(1) + ")";
					jdbcTemplate.execute(mapSynonymsToGlobalRule);
				}

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

	}

}