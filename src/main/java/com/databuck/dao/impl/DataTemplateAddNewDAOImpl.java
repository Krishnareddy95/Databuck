package com.databuck.dao.impl;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DateRuleMap;
import com.databuck.bean.GloabalRule;
import com.databuck.bean.HiveSource;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.TemplateRuleMapping;
import com.databuck.bean.listDataAccess;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.restcontroller.ListDataDefinitionBean;
import com.databuck.util.DateUtility;
import org.apache.log4j.Logger;

@Repository
public class DataTemplateAddNewDAOImpl implements IDataTemplateAddNewDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private Properties resultDBConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(DataTemplateAddNewDAOImpl.class);

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int insertDataIntoHiveSource(HiveSource hivesource) {
		SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from hiveSource");
		if (rs.next()) {
			String updateSql = "update hiveSource set name=?,description=?,idDataSchema=?,tableName=?";
			return jdbcTemplate.update(updateSql, hivesource.getName(), hivesource.getDescription(),
					hivesource.getIdDataSchema(), hivesource.getTableName());
		} else {
			String sql1 = "insert into hiveSource(name,description,idDataSchema,tableName)" + " VALUES (?,?,?,?)";
			int count1 = jdbcTemplate.update(sql1, hivesource.getName(), hivesource.getDescription(),
					hivesource.getIdDataSchema(), hivesource.getTableName());
			LOG.debug("insertDataIntoHiveSource" + count1);
			return count1;
		}
	}
	
	public List<ListDataSchema> getuniqueUrlfromAllListDataSchema(){

		String sql = "SELECT Min(idDataSchema) AS idDataSchema,ipAddress,databaseSchema,username,port FROM listDataSchema group by ipAddress, databaseSchema,username,port";
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {

			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPort(rs.getString("port"));
				return alistdataschema;
			}
		});

		return listdataschema;
	}

	public List<ListDataSchema> getListDataSchema(Long idDataSchema) {

		String sql = "SELECT * " + "from listDataSchema where  idDataSchema=" + idDataSchema;
		//LOG.debug("sql:" + sql);
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {

			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setHivejdbchost(rs.getString("hivejdbchost"));
				alistdataschema.setHivejdbcport(rs.getString("hivejdbcport"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				String encryptedPassword = rs.getString("password");
				String decryptedText = "";
				if(encryptedPassword != null &&! encryptedPassword.trim().isEmpty()) {
					try {
						StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
						decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
						decryptedText = decryptor.decrypt(encryptedPassword);
					} catch(Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					
				}
				// LOG.debug("Decrypted text is: " + decryptedText);
				alistdataschema.setPassword(decryptedText);
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setGatewayPath(rs.getString("gatewayPath"));
				alistdataschema.setJksPath(rs.getString("jksPath"));
				alistdataschema.setZookeeperUrl(rs.getString("zookeeperUrl"));
				
				alistdataschema.setFolderPath(rs.getString("folderPath"));
				alistdataschema.setFileNamePattern(rs.getString("fileNamePattern"));
				alistdataschema.setFileDataFormat(rs.getString("fileDataFormat"));
				alistdataschema.setHeaderPresent(rs.getString("headerPresent"));
				alistdataschema.setHeaderFilePath(rs.getString("headerFilePath"));
				alistdataschema.setHeaderFileNamePattern(rs.getString("headerFileNamePattern"));
				alistdataschema.setHeaderFileDataFormat(rs.getString("headerFileDataFormat"));
				alistdataschema.setAccessKey(rs.getString("accessKey"));
				String encryptedSecretKey = rs.getString("secretKey");
				String decryptedSecretKey = "";
				if(encryptedSecretKey != null &&! encryptedSecretKey.trim().isEmpty()) {
					try {
						StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
						decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
						decryptedSecretKey = decryptor.decrypt(encryptedSecretKey);
					} catch(Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
				}
				alistdataschema.setSecretKey(decryptedSecretKey);
				alistdataschema.setBucketName(rs.getString("bucketName"));
				alistdataschema.setBigQueryProjectName(rs.getString("bigQueryProjectName"));
				alistdataschema.setPrivatekeyId(rs.getString("privateKeyId"));
				alistdataschema.setPrivatekey(rs.getString("privateKey"));
				alistdataschema.setClientId(rs.getString("clientId"));
				alistdataschema.setClientEmail(rs.getString("clientEmail"));
				alistdataschema.setDatasetName(rs.getString("datasetName"));
				alistdataschema.setAzureClientId(rs.getString("azureClientId"));
				alistdataschema.setAzureClientSecret(rs.getString("azureClientSecret"));
				alistdataschema.setAzureTenantId(rs.getString("azureTenantId"));
				alistdataschema.setAzureServiceURI(rs.getString("azureServiceURI"));
				alistdataschema.setAzureFilePath(rs.getString("azureFilePath"));
				alistdataschema.setPartitionedFolders(rs.getString("partitionedFolders"));
				alistdataschema.setMaxFolderDepth(rs.getInt("maxFolderDepth"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setMultiPattern(rs.getString("multiPattern"));
				alistdataschema.setStartingUniqueCharCount(rs.getInt("startingUniqueCharCount"));
				alistdataschema.setEndingUniqueCharCount(rs.getInt("endingUniqueCharCount"));
				alistdataschema.setFileEncrypted(rs.getString("fileEncrypted"));
				alistdataschema.setCreatedBy(rs.getLong("createdBy"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				alistdataschema.setExtenalFileNamePattern(rs.getString("externalfileNamePattern"));
				alistdataschema.setExtenalFileName(rs.getString("externalfileName"));
				alistdataschema.setPatternColumn(rs.getString("patternColumn"));
				alistdataschema.setHeaderColumn(rs.getString("headerColumn"));
				alistdataschema.setLocalDirectoryColumnIndex(rs.getString("localDirectoryColumnIndex"));
				alistdataschema.setKmsAuthDisabled(rs.getString("kmsAuthDisabled"));
				alistdataschema.setClusterPropertyCategory(rs.getString("cluster_property_category"));
				alistdataschema.setMultiFolderEnabled(rs.getString("multiFolderEnabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setHttpPath(rs.getString("http_path"));

				alistdataschema.setAutoGenerate(rs.getString("autoGenerate"));
				alistdataschema.setSuffixes(rs.getString("suffixes"));
				alistdataschema.setPrefixes(rs.getString("prefixes"));
				alistdataschema.setCreatedAt(rs.getDate("createdAt"));
				alistdataschema.setUpdatedAt(rs.getDate("updatedAt"));
				alistdataschema.setUpdatedBy(rs.getLong("updatedBy"));
				alistdataschema.setAction(rs.getString("Action"));
				alistdataschema.setIdSORs(rs.getLong("idSORs"));
				alistdataschema.setPrivatekeyId(rs.getString("privateKeyId"));
				alistdataschema.setPrivatekey(rs.getString("privateKey"));
				alistdataschema.setSingleFile(rs.getString("singleFile"));
				alistdataschema.setXsltFolderPath(rs.getString("xsltFolderPath"));
				alistdataschema.setReadLatestPartition(rs.getString("readLatestPartition"));
				alistdataschema.setAlation_integration_enabled(rs.getString("alation_integration_enabled"));
				alistdataschema.setIncrementalDataReadEnabled(rs.getString("incremental_dataread_enabled"));

				return alistdataschema;
			}
		});

		return listdataschema;
	}

	public JSONObject createTemplateForRestAPI(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primaryKeyCols, List<ListDataDefinition> lstDataDefinition,
			List<ListDataDefinitionBean> metaData, HttpServletRequest req,Long project_id) {
		List<ListDataDefinitionBean> lddMain = new ArrayList<ListDataDefinitionBean>();
		String isUpdate = "N";
		Long idData = 0L;
		JSONObject jsonObj = new JSONObject();
		
		//LOG.info("createTemplateForRestAPI For =>");
		if (lstDataDefinition == null) {
			for (Map.Entry m : hm.entrySet()) {
				String isPrimaryKey = "N";

				ListDataDefinitionBean ldb = new ListDataDefinitionBean();
				ldb.setColumnName(m.getKey().toString());
				ldb.setColumnType(m.getValue().toString());
				ldb.setSubsegmentCheck("N");
				ldb.setDuplicateCheck("N");
				ldb.setMatchValueCheck("N");
				ldb.setNumericalCheck("N");
				ldb.setTextCheck("N");
				ldb.setLastReadTimeCheck("N");
				ldb.setNullCheck("N");
				ldb.setPrimaryCheck(isPrimaryKey);
				ldb.setRecordAnomalyCheck("N");
				ldb.setRecordAnomalyThreshold(Double.valueOf(3.0D));
				ldb.setDoNotDisplayCheck("N");
				ldb.setDataDriftCheck("N");
				ldb.setDoNotDisplayCheck("N");
				ldb.setPartitionCheck("N");
				ldb.setDefaultCheck("N");
				ldb.setDefaultValues("N");
				ldb.setbadData("N");
				ldb.setDateFormat("");
				
				/*//24_DEC_2018 (12.43pm)
				
				ldb.setStringSizeCheck("N");
				ldb.setStringSizeValue("N");*/
				
				if (primaryKeyCols.contains(m.getKey())) {
					ldb.setPrimaryCheck("Y");
				}
				
				
				/*
				 * ldb.setSubsegmentCheck("N"); ldb.setDuplicateCheck("N");
				 * ldb.setMatchValueCheck("N"); ldb.setNumericalCheck("N");
				 * ldb.setTextCheck("N"); ldb.setLastReadTimeCheck("N");
				 * ldb.setNullCheck("N"); ldb.setPrimaryCheck(isPrimaryKey);
				 * ldb.setRecordAnomalyCheck("N");
				 * ldb.setRecordAnomalyThreshold(Double.valueOf(1.5D));
				 * ldb.setDoNotDisplayCheck("N"); ldb.setDataDriftCheck("N");
				 * ldb.setDoNotDisplayCheck("N"); ldb.setPartitionCheck("N");
				 */
				for (ListDataDefinitionBean ldd : metaData) {
					if (ldd.getColumnName().equalsIgnoreCase(m.getKey().toString())) {
						LOG.debug("ldd.getColumnName()=" + ldd.getColumnName());
						if (ldd.getSubsegmentCheck().equalsIgnoreCase("Y")) {
							ldb.setSubsegmentCheck("Y");
						}
						if (ldd.getDuplicateCheck().equalsIgnoreCase("Y")) {
							ldb.setDuplicateCheck("Y");
						}
						if (ldd.getMatchValueCheck().equalsIgnoreCase("Y")) {
							ldb.setMatchValueCheck("Y");
						}
						if (ldd.getNumericalCheck().equalsIgnoreCase("Y")) {
							ldb.setNumericalCheck("Y");
						}
						if (ldd.getTextCheck().equalsIgnoreCase("Y")) {
							ldb.setTextCheck("Y");
						}
						if (ldd.getLastReadTimeCheck().equalsIgnoreCase("Y")) {
							ldb.setLastReadTimeCheck("Y");
						}
						if (ldd.getNullCheck().equalsIgnoreCase("Y")) {
							ldb.setNullCheck("Y");
						}
						if (ldd.getPrimaryCheck().equalsIgnoreCase("Y")) {
							ldb.setPrimaryCheck("Y");
						}
						if (ldd.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
							ldb.setRecordAnomalyCheck("Y");
						}
						if (ldd.getRecordAnomalyThreshold() == 0) {
							ldb.setRecordAnomalyThreshold(Double.valueOf(3.0D));
						}
						if (ldd.getDataDriftCheck().equalsIgnoreCase("Y")) {
							ldb.setDataDriftCheck("Y");
						}
						if (ldd.getDoNotDisplayCheck().equalsIgnoreCase("Y")) {
							ldb.setDoNotDisplayCheck("Y");
						}
						if (ldd.getPartitionCheck().equalsIgnoreCase("Y")) {
							ldb.setPartitionCheck("Y");
						}
						if (ldd.getDefaultCheck().equalsIgnoreCase("Y")) {
							ldb.setDefaultCheck("Y");
						}						
					/*	if (ldd.getStringSizeCheck().equalsIgnoreCase("Y")) {
							ldb.setStringSizeCheck("Y");
						}	*/
					}
				}
				lddMain.add(ldb);
			}
		}
		
		try {
			String metaDataRow = "";
			String matchedMetaData = "";
			SqlRowSet metaDataListFromSamePath = jdbcTemplate.queryForRowSet(
					"select metaData from listDataAccess where metaData is not null and hostName=?", lda.getHostName());

			boolean checkMetaDataUpdation = true;
			boolean isTemplateUpdate = false;
			
			String isTemplateUpdationSupported = appDbConnectionProperties.getProperty("isTemplateUpdationSupported");

			if (metaData == null || metaData.size() == 0 || (isTemplateUpdationSupported != null && isTemplateUpdationSupported.equalsIgnoreCase("N"))) {
				checkMetaDataUpdation = false;
			}
			
			String metaDataList = null;
			ArrayList<String> mataDataArrayList = new ArrayList<String>();
			if(metaData!=null) {
				for (ListDataDefinitionBean ldd : metaData) {
					metaDataList = ldd.getColumnName() + "_" + ldd.getColumnType();
					mataDataArrayList.add(metaDataList);
				}
				metaDataList = String.join(",", mataDataArrayList);
			}
			if (checkMetaDataUpdation) {				
				List<String> metaDataFromUi = Arrays.asList(metaDataList.split("\\s*,\\s*"));
				while (metaDataListFromSamePath.next()) {
					metaDataRow = metaDataListFromSamePath.getString("metaData");
					List<String> metaDataInTable = Arrays.asList(metaDataRow.split("\\s*,\\s*"));
					Iterator<String> iterator = metaDataFromUi.iterator();
					int numberOfMatches = 0;
					int totalBits = 0;
					while (iterator.hasNext()) {
						Boolean flag = metaDataInTable.contains(iterator.next());
						if (flag == true) {
							numberOfMatches++;
						}
						totalBits++;
					}
					if (numberOfMatches == metaDataFromUi.size() && totalBits == metaDataFromUi.size()) {

						String queryListDataAccess = "SELECT idData, folderName from listDataAccess where metaData = '"
								+ metaDataList + "' AND hostName = '" + lda.getHostName() + "'";

						String repeatFolderName = "";
						Long repeatIdData = null;
						SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(queryListDataAccess);
						while (queryForRowSet.next()) {
							repeatIdData = queryForRowSet.getLong(1);
							repeatFolderName = queryForRowSet.getString(2);
						}
						String updatedFolderName = repeatFolderName + "," + lda.getFolderName();

						String updateFieldName = "update listDataAccess set folderName=? where idData=? ";
						int update = jdbcTemplate.update(updateFieldName, updatedFolderName, repeatIdData);
						isTemplateUpdate = true;
						isUpdate = "Y";
						idData = repeatIdData;
					}
				}
			}
			//TO BE REMOVED LATER STARTS
			//isTemplateUpdate = false;
			//TO BE REMOVED LATER ENDS
			if (!isTemplateUpdate) {
				
				URI contextUrl = URI.create(req.getRequestURL().toString()).resolve(req.getContextPath());
				String resultUrlLink = contextUrl.toString();
				
				idData = insertIntoListDataSources(lds,project_id);
				LOG.debug("listDataSources idData:" + idData);
				String sql1 = "insert into listDataAccess(idData,hostName,portName,userName,pwd,schemaName,"
						+ "folderName,query,idDataSchema,whereCondition,domain,incrementalType,dateFormat,"
						+ "sliceStart,sliceEnd,queryString,isRawData) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String encryptedText = encryptor.encrypt(lda.getPwd());

				if (lda.getQuery() == null) {
					lda.setQuery("N");
				}
				int count1 = jdbcTemplate.update(sql1,
						new Object[] { idData, lda.getHostName(), lda.getPortName(), lda.getUserName(), encryptedText,
								resultUrlLink, lda.getFolderName(), lda.getQuery(),
								Long.valueOf(lda.getIdDataSchema()), lda.getWhereCondition(), lda.getDomain(),
								lda.getIncrementalType(), lda.getDateFormat(), lda.getSliceStart(), lda.getSliceEnd(),
								lda.getQueryString(), lda.getIsRawData()});
				int count2 = 0;
				
				sql1 = "update listDataAccess set metaData='"+ metaDataList+"' where idData="+idData;				
				jdbcTemplate.update(sql1);
			

			String dataDrift = "N";
			String name = null;
			if (lstDataDefinition == null) {
				for (ListDataDefinitionBean ldd : lddMain) {
					String sql2 = "insert into listDataDefinition(idData,columnName,displayName,format,KBE,dgroup,dupkey,"
							+ "measurement,blend,idDataSchema,hashValue,defaultCheck,defaultValues,numericalStat,stringStat,incrementalCol,nonNull"
							+ "primarykey,recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,"
							+ "partitionBy,badData,lengthCheck,maxLengthCheck,lengthValue) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					jdbcTemplate.update(sql2,
							new Object[] { idData, "", ldd.getColumnName(), ldd.getColumnType(), "N",
									ldd.getSubsegmentCheck(), ldd.getDuplicateCheck(), ldd.getMatchValueCheck(), "",
									Integer.valueOf(0), "N", ldd.getDefaultCheck(),ldd.getDefaultValues(),ldd.getNumericalCheck(), ldd.getTextCheck(),
									ldd.getLastReadTimeCheck(), ldd.getNullCheck(), ldd.getPrimaryCheck(),
									ldd.getRecordAnomalyCheck(), ldd.getRecordAnomalyThreshold(),
									ldd.getDataDriftCheck(), Integer.valueOf(0), ldd.getDoNotDisplayCheck(),
									ldd.getPartitionCheck(),ldd.getBadData()/*,ldd.getStringSizeCheck(),ldd.getStringSizeValue()*/ });

				}
			} else {
				for (Map.Entry m : hm.entrySet()) {
					String isPrimaryKey = "N";
					if (primaryKeyCols.contains(m.getKey())) {
						isPrimaryKey = "Y";
					}
					name = m.getValue().toString().toLowerCase();

					String sql2 = "insert into listDataDefinition(idData,columnName,displayName,format,KBE,dgroup,dupkey,"
							+ "measurement,blend,idDataSchema,hashValue,defaultCheck,defaultValues,numericalStat,stringStat,"
							+ "incrementalCol,nonNull,"
							+ "primarykey,recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,"
							+ "partitionBy,badData,lengthCheck,maxLengthCheck,lengthValue) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					if (lstDataDefinition == null) {

					} else {
						ListDataDefinition colDataDefinition = null;
						for (Iterator<ListDataDefinition> iter = lstDataDefinition.iterator(); iter.hasNext();) {
							ListDataDefinition dataDefinition = (ListDataDefinition) iter.next();
							if (dataDefinition.getColumnName().equals(m.getKey())) {
								colDataDefinition = dataDefinition;
								break;
							}
						}

						if (colDataDefinition != null) {
							if ((isPrimaryKey.equalsIgnoreCase("n"))
									&& (colDataDefinition.getPrimaryKey().equalsIgnoreCase("y"))) {
								isPrimaryKey = "Y";
							}
							jdbcTemplate.update(sql2,
									new Object[] { idData, "", m.getKey(), m.getValue(), colDataDefinition.getKBE(),
											colDataDefinition.getDgroup(), colDataDefinition.getDupkey(),
											colDataDefinition.getMeasurement(), "", Integer.valueOf(0),
											colDataDefinition.getHashValue(), colDataDefinition.getDefaultCheck(),
											colDataDefinition.getDefaultValues(),colDataDefinition.getNumericalStat(),
											colDataDefinition.getStringStat(), colDataDefinition.getIncrementalCol(),
											colDataDefinition.getNonNull(), isPrimaryKey,
											colDataDefinition.getRecordAnomaly(), Double.valueOf(3.0D), dataDrift,
											Integer.valueOf(0), colDataDefinition.getIsMasked(),
											colDataDefinition.getPartitionBy(),colDataDefinition.getBadData(),colDataDefinition.getLengthCheck(),colDataDefinition.getMaxLengthCheck(),colDataDefinition.getLengthValue()});
														
							//remains
						}
					}

				}
			}
		}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		jsonObj.put("idData", idData);
		jsonObj.put("isUpdate", isUpdate);
		return jsonObj;
	}

	public Long addintolistdatasource(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primaryKeyCols, List<ListDataDefinition> lstDataDefinition,List<GloabalRule> selected_list,Long projectId) {

		Long idData = insertIntoListDataSources(lds,projectId);
		/*
		 * for(int i=0;i<rules_selected.size();i++) { TemplateRuleMapping trm=new
		 * TemplateRuleMapping(); LOG.debug(idData); trm.setTemplateId(idData);
		 * //get rule id from the selected list trm.setRuleId(rules_selected.get(i));
		 * 
		 * LOG.debug("##############");
		 * 
		 * insertintorule_Template_Mapping(trm);
		 * LOG.debug("******************"); }
		 */
		if(selected_list != null){
			for(int i=0;i<selected_list.size();i++)
			{
			TemplateRuleMapping trm=new TemplateRuleMapping();
			LOG.debug(idData);
			trm.setTemplateId(idData);
			//get rule id from the selected list 
			trm.setRuleId(selected_list.get(i).getGloabal_rule_id());
			trm.setRuleName(selected_list.get(i).getRule_name());
			trm.setRuleExpression(selected_list.get(i).getRule_expression());
			trm.setRuleType(selected_list.get(i).getRule_Type());
			
			LOG.debug(trm);
			
			try {
			insertintorule_Template_Mapping(trm);
					
			}catch(Exception e)
			{
				LOG.error(e.getMessage());
				e.printStackTrace();
				}
			}
		}		
		
		
		LOG.debug("listDataSources idData:" + idData);
		String sql1 = "insert into listDataAccess("
				+ "idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,whereCondition,domain,incrementalType,"+
		"dateFormat,sliceStart,sliceEnd,queryString,hivejdbchost,hivejdbcport,sslEnb,sslTrustStorePath,trustPassword,gatewayPath,jksPath,zookeeperUrl,fileHeader,rollingHeader,rollingColumn,historicDateTable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		String encryptedText = encryptor.encrypt(lda.getPwd());

		if (lda.getQuery() == null) {
			lda.setQuery("N");
		} else if (lda.getQuery().equals("Y") && !(lds.getDataLocation().equalsIgnoreCase("FileSystem Batch")
				|| lds.getDataLocation().equalsIgnoreCase("S3 Batch") || lds.getDataLocation().equalsIgnoreCase("FileSystem")
				|| lds.getDataLocation().equalsIgnoreCase("S3 IAMRole Batch"))) {
			lda.setFolderName("Query");
		}
		int count1 = jdbcTemplate.update(sql1,
				new Object[] { idData, lda.getHostName(), lda.getPortName(), lda.getUserName(), encryptedText,
						lda.getSchemaName(), lda.getFolderName(), lda.getQuery(), Long.valueOf(lda.getIdDataSchema()),
						lda.getWhereCondition(), lda.getDomain(), lda.getIncrementalType(), lda.getDateFormat(),
						lda.getSliceStart(), lda.getSliceEnd(), lda.getQueryString(),lda.getHivejdbchost(),
						lda.getHivejdbcport(),lda.getSslEnb(),lda.getSslTrustStorePath(),lda.getTrustPassword(),
						lda.getGatewayPath(),lda.getJksPath(),lda.getZookeeperUrl(),lda.getFileHeader(),
						lda.getRollingHeader(), lda.getRollingColumn(), lda.getHistoricDateTable()});
		LOG.debug("listDataAccess" + count1);
		int count2 = 0;

		String dataDrift = "N";
		String name = null;

		if(hm!=null && hm.size()>0) {
		for (Map.Entry m : hm.entrySet()) {
			String isPrimaryKey = "N";

			LOG.debug("m.getValue()=            " + m.getValue());
			LOG.debug("m.getKey()=            " + m.getKey());
			if (primaryKeyCols!=null && primaryKeyCols.contains(m.getKey())) {
				isPrimaryKey = "Y";
			}
			name = m.getValue().toString().toLowerCase();
			LOG.debug("name=" + name);
			if ((!name.toLowerCase().contains("varchar")) && (!name.toLowerCase().contains("char"))
					&& (!name.toLowerCase().contains("string"))) {
				name.toLowerCase().contains("text");
			}


			String sql2 = "insert into listDataDefinition("
					+ "idData,columnName,displayName,format,KBE,dgroup,dupkey,measurement,blend,"
					+ "idDataSchema,hashValue,numericalStat"
					+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
					+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
					+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			if (lstDataDefinition == null) {
				count2 = jdbcTemplate.update(sql2,
						new Object[] { idData, "", m.getKey(), m.getValue(), "N", "N", "N", "N", "", Integer.valueOf(0),

								"N", "N", "N", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3),
								"N", "N", isPrimaryKey, "N", Double.valueOf(3.0D), dataDrift,
								Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N" ,"N", null, "N",null, "N" ,"N","N",0});

			} else {
				ListDataDefinition colDataDefinition = null;
				for (Iterator<ListDataDefinition> iter = lstDataDefinition.iterator(); iter.hasNext();) {
					ListDataDefinition dataDefinition = (ListDataDefinition) iter.next();
					LOG.debug("ntest colum -->"+dataDefinition.getColumnName());
					if (dataDefinition.getColumnName().equals(m.getKey())) {
						colDataDefinition = dataDefinition;
						break;
					}
				}

				if (colDataDefinition != null) {
					if ((isPrimaryKey.equalsIgnoreCase("n"))
							&& (colDataDefinition.getPrimaryKey().equalsIgnoreCase("y"))) {
						isPrimaryKey = "Y";
					}
					count2 = jdbcTemplate.update(sql2,
							new Object[] { idData, "", m.getKey(), m.getValue(), colDataDefinition.getKBE(),
									colDataDefinition.getDgroup(), colDataDefinition.getDupkey(),
									colDataDefinition.getMeasurement(), "", Integer.valueOf(0),
									colDataDefinition.getHashValue(), colDataDefinition.getNumericalStat(),
									colDataDefinition.getStringStat(), Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), colDataDefinition.getIncrementalCol(),
									colDataDefinition.getNonNull(), isPrimaryKey, colDataDefinition.getRecordAnomaly(),
									Double.valueOf(3.0D), colDataDefinition.getDataDrift(), Integer.valueOf(3),
									colDataDefinition.getIsMasked(), colDataDefinition.getPartitionBy(),
									"N", "N", "N", "N", "N", colDataDefinition.getPatternCheck(), colDataDefinition.getPatterns(), 
									colDataDefinition.getBadData(), colDataDefinition.getDateFormat(), "N"  ,colDataDefinition.getLengthCheck(),colDataDefinition.getMaxLengthCheck(),0});
					LOG.debug(count2);
					//}
				/*	catch(Exception e)
					{
						e.printStackTrace();
					}*/
					//remains
				}
			}

			LOG.debug("listDataDefinition" + count2);
		}
		}
		return idData;
	}

// Added as part of derived template changes
public Long insertIntoListDerivedDataSources(final ListDerivedDataSource lds,Long projectId) {
		
		final String sql = "insert into listDerivedDataSources(name,description,template1Name,template2Name,createdAt,"
				+ "updatedAt,createdBy,updatedBy,template1IdData,template2IdData,template1AliasName,template2AliasName,"
				+ "project_id,queryText,createdByUser,idData)"
				+ " VALUES (?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?)";

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idderiveddata"
				: "idDerivedData";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, lds.getName());
				pst.setString(2, lds.getDescription());
				pst.setString(3, lds.getTemplate1Name());
				pst.setString(4, lds.getTemplate2Name());
				pst.setLong(5, lds.getCreatedBy());
				pst.setLong(6, lds.getCreatedBy());
				pst.setLong(7, lds.getTemplate1IdData());
				pst.setLong(8, lds.getTemplate2IdData());
				pst.setString(9, lds.getTemplate1AliasName());
				pst.setString(10, lds.getTemplate2AliasName());
				pst.setLong(11, projectId);
				pst.setString(12, lds.getQueryText());
				pst.setString(13, lds.getCreatedByUser());
				pst.setLong(14, lds.getIdData());
				return pst;
			}
		}, keyHolder);
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());

		return keyHolder.getKey().longValue();
	}


	
	public Long addintolistdatasourceupdate(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primaryKeyCols, List<ListDataDefinition> lstDataDefinition,Long idData1) {

		//Long idData = insertIntoListDataSources(lds);
		Long idData = idData1;
		LOG.debug("listDataSources idData:" + idData);
		
		deleteListDataDefinition(idData);
		/*String sql1 = "insert into listDataAccess("
				+ "idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,whereCondition,domain,incrementalType,"+
		"dateFormat,sliceStart,sliceEnd,queryString,hivejdbchost,hivejdbcport,sslEnb,sslTrustStorePath,trustPassword,gatewayPath,jksPath,zookeeperUrl) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
*/
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		String encryptedText = encryptor.encrypt(lda.getPwd());

		if (lda.getQuery() == null) {
			lda.setQuery("N");
		}else if (lda.getQuery().equals("Y") && !lds.getDataLocation().equalsIgnoreCase("FileSystem Batch")){
			lda.setFolderName("Query");
		}
		/*int count1 = jdbcTemplate.update(sql1,
				new Object[] { idData, lda.getHostName(), lda.getPortName(), lda.getUserName(), encryptedText,
						lda.getSchemaName(), lda.getFolderName(), lda.getQuery(), Long.valueOf(lda.getIdDataSchema()),
						lda.getWhereCondition(), lda.getDomain(), lda.getIncrementalType(), lda.getDateFormat(),
						lda.getSliceStart(), lda.getSliceEnd(), lda.getQueryString(),lda.getHivejdbchost(),
						lda.getHivejdbcport(),lda.getSslEnb(),lda.getsslTrustStorePath(),lda.gettrustPassword(),
						lda.getGatewayPath(),lda.getJksPath(),lda.getZookeeperUrl()});
		LOG.debug("listDataAccess" + count1);*/
		int count2 = 0;

		String dataDrift = "N";
		String name = null;

		if(hm!=null && hm.size()>0) {
		for (Map.Entry m : hm.entrySet()) {
			String isPrimaryKey = "N";

			LOG.debug("m.getValue()=            " + m.getValue());
			LOG.debug("m.getKey()=            " + m.getKey());
			if (primaryKeyCols.contains(m.getKey())) {
				isPrimaryKey = "Y";
			}
			name = m.getValue().toString().toLowerCase();
			LOG.debug("name=" + name);
			if ((!name.toLowerCase().contains("varchar")) && (!name.toLowerCase().contains("char"))
					&& (!name.toLowerCase().contains("string"))) {
				name.toLowerCase().contains("text");
			}


			String sql2 = "insert into listDataDefinition("
					+ "idData,columnName,displayName,format,KBE,dgroup,dupkey,measurement,blend,"
					+ "idDataSchema,hashValue,numericalStat"
					+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
					+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
					+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			if (lstDataDefinition == null) {
				count2 = jdbcTemplate.update(sql2,
						new Object[] { idData, "", m.getKey(), m.getValue(), "N", "N", "N", "N", "", Integer.valueOf(0),

								"N", "N", "N", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3),
								"N", "N", isPrimaryKey, "N", Double.valueOf(3.0D), dataDrift,
								Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N" ,"N", null, "N",null, "N" ,"N",0});

			} else {
				ListDataDefinition colDataDefinition = null;
				for (Iterator<ListDataDefinition> iter = lstDataDefinition.iterator(); iter.hasNext();) {
					ListDataDefinition dataDefinition = (ListDataDefinition) iter.next();
					LOG.debug("ntest colum -->"+dataDefinition.getColumnName());
					if (dataDefinition.getColumnName().equals(m.getKey())) {
						colDataDefinition = dataDefinition;
						break;
					}
				}

				if (colDataDefinition != null) {
					if ((isPrimaryKey.equalsIgnoreCase("n"))
							&& (colDataDefinition.getPrimaryKey().equalsIgnoreCase("y"))) {
						isPrimaryKey = "Y";
					}
					count2 = jdbcTemplate.update(sql2,
							new Object[] { idData, "", m.getKey(), m.getValue(), colDataDefinition.getKBE(),
									colDataDefinition.getDgroup(), colDataDefinition.getDupkey(),
									colDataDefinition.getMeasurement(), "", Integer.valueOf(0),
									colDataDefinition.getHashValue(), colDataDefinition.getNumericalStat(),
									colDataDefinition.getStringStat(), Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), colDataDefinition.getIncrementalCol(),
									colDataDefinition.getNonNull(), isPrimaryKey, colDataDefinition.getRecordAnomaly(),
									Double.valueOf(3.0D), colDataDefinition.getDataDrift(), Integer.valueOf(3),
									colDataDefinition.getIsMasked(), colDataDefinition.getPartitionBy(),
									"N", "N", "N", "N", "N", colDataDefinition.getPatternCheck(), colDataDefinition.getPatterns(), 
									colDataDefinition.getBadData(), colDataDefinition.getDateFormat(), "N"  ,colDataDefinition.getLengthCheck(),colDataDefinition.getMaxLengthCheck(),0});
					//remains
				}
			}

			LOG.debug("listDataDefinition" + count2);
		}
		}
		return idData;
	}

	public int insertIntoListDataAccess(listDataAccess lda) {
		String sql1 = "insert into listDataAccess(idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,"
				+ "whereCondition,domain,queryString)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		String encryptedText = encryptor.encrypt(lda.getPwd());
		int count = jdbcTemplate.update(sql1, lda.getIdData(), lda.getHostName(), lda.getPortName(), lda.getUserName(),
				encryptedText, lda.getSchemaName(), lda.getFolderName(), "Y", lda.getIdDataSchema(),
				lda.getWhereCondition(), lda.getDomain(), lda.getQueryString());
		// LOG.debug("listDataAccess" + count);
		return count;
	}

	public void insertIntoListDataFiles(Long idData, ArrayList<String> al) {
		int count = 0;
		String sql1 = "insert into listDataFiles(idData,fileName)" + " VALUES (?,?)";
		for (String fileName : al) {
			jdbcTemplate.update(sql1, idData, fileName);
			count++;
		}
		LOG.debug("listDataFiles" + count);
	}

	public Long insertIntoListDataSources(final ListDataSource lds,Long projectId) {
		// LOG.debug("inserting into listdatasource");
		final String sql = "insert into listDataSources(name,description,dataLocation,dataSource,createdAt,"
				+ "updatedAt,createdBy,updatedBy,idDataSchema,ignoreRowsCount,profilingEnabled,project_id,advancedRulesEnabled,createdByUser,domain_id)"
				+ " VALUES (?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?)";

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
				: "idData";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, lds.getName());
				pst.setString(2, lds.getDescription());
				pst.setString(3, lds.getDataLocation());
				pst.setString(4, lds.getDataSource());
				pst.setLong(5, lds.getCreatedBy());
				pst.setLong(6, lds.getCreatedBy());
				pst.setLong(7, lds.getIdDataSchema());
				pst.setLong(8, lds.getGarbageRows());
				pst.setString(9, lds.getProfilingEnabled());
				pst.setLong(10, projectId);
				pst.setString(11, lds.getAdvancedRulesEnabled());
				pst.setString(12, lds.getCreatedByUser());
				pst.setInt(13, lds.getDomain());
				return pst;
			}
		}, keyHolder);
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());

		return keyHolder.getKey().longValue();
	}

	@Override
	public String duplicatedatatemplatename(String dataTemplateName) {
		String Name = null;
		if(dataTemplateName != null) 
			dataTemplateName = dataTemplateName.trim();
		String q = "SELECT name FROM listDataSources WHERE name = ? limit 1";
		Object[] inputs = new Object[] { dataTemplateName};
		try {
			Name = jdbcTemplate.queryForObject(q, inputs, String.class);
			LOG.debug("Name=" + Name);
			return Name;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return Name;
		}
	}
	
	@Override
	public String duplicateCustomrulename(String customRuleName) {
		String Name = null;
		if(customRuleName != null) 
			customRuleName = customRuleName.trim();
		String q = "SELECT ruleName FROM listColGlobalRules WHERE ruleName = ? limit 1";
		Object[] inputs = new Object[] { customRuleName};
		try {
			Name = jdbcTemplate.queryForObject(q, inputs, String.class);
			LOG.debug("Name=" + Name);
			return Name;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return Name;
		}
	}

	public void deleteListDataDefinition(Long idData) {
		try {
			String deleteQuery = "DELETE FROM listDataDefinition where idData=" + idData;
			jdbcTemplate.execute(deleteQuery);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Long addintolistdatasourceForOracle(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primaryKeyCols,Long project_id) {
		Long idData = insertIntoListDataSources(lds,project_id);
		// LOG.debug("listDataSources idData:" + idData);
		String sql1 = "insert into listDataAccess(idData,hostName,portName,userName,pwd,"
				+ "schemaName,folderName,query,idDataSchema,whereCondition,domain,"
				+ "incrementalType,dateFormat,sliceStart,sliceEnd,queryString,fileHeader)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		String encryptedText = encryptor.encrypt(lda.getPwd());
		// LOG.debug("Encrypted text is: " + encryptedText);
		if (lda.getQuery() == null) {
			lda.setQuery("N");
		}
		int count1 = jdbcTemplate.update(sql1, idData, lda.getHostName(), lda.getPortName(), lda.getUserName(),
				encryptedText, lda.getSchemaName(), lda.getFolderName(), lda.getQuery(), lda.getIdDataSchema(),
				lda.getWhereCondition(), lda.getDomain(), lda.getIncrementalType(), lda.getDateFormat(),
				lda.getSliceStart(), lda.getSliceEnd(), lda.getQueryString(), lda.getFileHeader());
		// LOG.debug("listDataAccess" + count1);
		int count2 = 0;
		String name = null;
		if (hm.isEmpty()) {
		} else {

			for (Map.Entry m : hm.entrySet()) {
				String isPrimaryKey = "N";

				// LOG.debug("m.getValue()= " + m.getValue());
				if (primaryKeyCols.contains(m.getKey())) {
					isPrimaryKey = "Y";
				}
				name = m.getValue().toString().toLowerCase();
				// LOG.debug("name=" + name);
				String sql2 = "insert into listDataDefinition(idData,columnName,displayName,"
						+ "format,KBE,dgroup,dupkey,measurement,blend,idDataSchema,hashValue,"
						+ "numericalStat,stringStat,incrementalCol,nonNull,primarykey,recordanomaly,"
						+ "recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,badData)"
						+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				count2 = jdbcTemplate.update(sql2, idData, "", m.getKey(), m.getValue(), "N", "N", "N", "N", "",
						lds.getIdDataSchema(), "N", "N", "N", "N", "N", isPrimaryKey, "N", 2, "N", 0, "N","N");
				// LOG.debug("listDataDefinition" + count2);
			}
		}
		return idData;
	}
	public Long addIntoResultDb(long idApp, HttpSession session) {
		
		 
	      try{
	    	  
	    	  //String connectionUrl= "jdbc:mysql://172.28.25.111:3306"; 
	 	     
		      Connection con = null;  
		      Statement stmt = null;  
		      ResultSet rs = null; 
	    	  Class.forName(resultDBConnectionProperties.getProperty("db1.driver")).newInstance ();  
		         con = DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd1"));  

		         // Create and execute an SQL statement that returns some data.  
		         String SQL = "CREATE TABLE Date_Rule_" + idApp + "(ColumnName nvarchar(500), NAAcceptable nvarchar(100), MinAcceptable nvarchar(500), MaxAcceptable nvarchar(500), NullColumn nvarchar(500))";
		         stmt = con.createStatement();  
		         int create = stmt.executeUpdate(SQL); 
		         DateRuleMap DateRuleMap1 = new DateRuleMap();
		         Map<String, Object> dataRuleMapListFinal = new HashMap<String, Object>();
		         Object dataRuleMapListObject = session.getAttribute("dataRuleMap");
		         dataRuleMapListFinal = (HashMap<String, Object>)dataRuleMapListObject;
		         for (Map.Entry<String, Object> entry : dataRuleMapListFinal.entrySet()) {
		        	  DateRuleMap1 = (DateRuleMap) entry.getValue();
		        	  String insertQuery = "INSERT INTO Date_Rule_" + idApp + " (ColumnName, NAAcceptable, "
		        	  		+ "MinAcceptable, MaxAcceptable, NullColumn) VALUES ('" + entry.getKey() + "', '" + 
		        			  DateRuleMap1.getNAAcceptable() + "', '" + DateRuleMap1.getMinAcceptable() + "', '" 
		        	  		+ DateRuleMap1.getMaxAcceptable() + "', '" + DateRuleMap1.getNullColumn() + "')";
		 			  int insert = stmt.executeUpdate(insertQuery); 
		        }
		         return (long) 1;
	      }
	      catch(Exception e){
	    	  LOG.error(e.getMessage());
	    	  e.printStackTrace();
	      }
	      return (long) 0;
	   	
	}
	
	public void insertintorule_Template_Mapping(TemplateRuleMapping rc){
		String query = "insert into rule_Template_Mapping" + "(templateid,ruleId,ruleName,ruleExpression,ruleType) values(?,?,?,?,?)";
		int sql = jdbcTemplate.update(query, new Object[] {rc.getTemplateId(),rc.getRuleId(),rc.getRuleName(),rc.getRuleExpression(),rc.getRuleType()});
		LOG.debug("insert into rule_Template_Mapping=" + sql);
	}
	
	public void populateColumnProfileMasterTable(long nDataTmplId, int nDomainId) {
		String sInsertQuery = "";
		String sUpdateQuery = String.format("update listDataSources set domain_id = %1$s where idData = %2$s and domain_id is null;", nDomainId, nDataTmplId);
		int aInsertedRow[] = null;

		jdbcTemplate.update(sUpdateQuery);

		sInsertQuery = sInsertQuery + "insert into column_profile_master_table\n";
		sInsertQuery = sInsertQuery + "(domain_id, idData, schemaName, template_name, table_name, column_name)\n";
		sInsertQuery = sInsertQuery + "select a.domain_id, a.idData, c.schemaName, a.name, d.folderName, b.displayName\n";
		sInsertQuery = sInsertQuery + "from listDataSources a, listDataDefinition b, listDataSchema c, listDataAccess d\n";
		sInsertQuery = sInsertQuery + "where a.idData = b.idData\n";
		sInsertQuery = sInsertQuery + "and   a.idDataSchema = c.idDataSchema\n";
		sInsertQuery = sInsertQuery + "and   a.idData = d.idData\n";
		sInsertQuery = sInsertQuery + String.format("and   a.idData = %1$s\n", nDataTmplId);
		sInsertQuery = sInsertQuery + "and   a.domain_id is not null;";

		aInsertedRow = jdbcTemplate.batchUpdate(sInsertQuery);

		DateUtility.DebugLog("populateColumnProfileMasterTable", String.format("Rows inserted are '%1$s' by SQL statement as below\n%2$s", aInsertedRow[0], sInsertQuery));
	}
	
	public String getUserMessageFromExp(String ExpMsg) {
		String errmsg = "";
		LOG.debug("ExpMsg " +ExpMsg);
		if (ExpMsg.toLowerCase().contains("line")) {
			Pattern pattern = Pattern.compile("Line \\d{1,3}:\\d{1,3} (.*?)(:|$)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(ExpMsg);
			if (matcher.find()) {
				errmsg = matcher.group(1);
			} 
		}else if (ExpMsg.toLowerCase().contains("exception")) {
			errmsg = "Please check proper syntax of query";
		}else {		
			errmsg = ExpMsg;
		}
		return errmsg;
	}
	

	

}