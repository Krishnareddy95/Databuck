package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ColumnNameDataTypeISNullableForVertica;
import com.databuck.bean.DataConnection;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.listDataAccess;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.SchemaDAOI;

@Repository
public class SchemaDAOImpl implements SchemaDAOI {

	String encryptedData = null;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;
	
	private static final Logger LOG = Logger.getLogger(SchemaDAOImpl.class);

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String duplicateSchemaName(String schemaName, long projectId, int domainId) {
		String Name = null;
		String sql = "SELECT schemaName FROM listDataSchema WHERE schemaName='" + schemaName + "'"
				+ " and domain_id=? and project_id=?";
		try {
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, domainId, projectId);
			while (queryForRowSet.next()) {
				Name = queryForRowSet.getString(1);
			}
			return Name;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return Name;
		}
	}

	@Override
	public Long updateDataIntoListDataSchema(String uri, String database, String username, String password, String port,
			String schemaName, String schemaType, String domain, long idDataSchema, String serviceName, String krb5conf,
			String hivejdbchost, String hivejdbcport, String sslTrustStorePath, String trustPassword,
			String gatewayPath, String jksPath, String zookeeperUrl, String folderPath, String fileNamePattern,
			String fileDataFormat, String headerPresent, String headerFilePath, String headerFileNamePattern,
			String headerFileDataFormat, String accessKey, String secretKey, String bucketName,
			String bigQueryprojectName, String privatekeyId, String privatekey, String clientId, String clientEmail,
			String datasetName, String azureClientId, String azureClientSecret, String azureTenantId,
			String azureServiceURI, String azureFilePath, String partitionedFolders, String multiPattern,
			int startingUniqueCharCount, int endingUniqueCharCount, int maxFolderDepth, String fileEncrypted,
			String singleFile, String externalFileNamePatternId, String externalFileName, String patternColumn,
			String headerColumn, String localDirectoryColumnIndex, String xsltFolderPath, String kmsAuthDisabled,
			String readLatestPartition, String enableFileMonitoring, String alation_integration_enabled,
			String incrementalDataReadEnabled, String multiFolderEnabled,String sslEnabled,String pushDownQueryEnabled,String httpPath,String clusterPropertyCategory,String azureAuthenticationType, String clusterPolicyId) {
	
		long update = 0;
		if(clusterPropertyCategory==null)
			clusterPropertyCategory="cluster";

		LOG.debug("httpPath="+httpPath);

		// Encrypt and update secret key only when it is modified

		// if allation is null, making it 'N'
		if (alation_integration_enabled == null || alation_integration_enabled.trim().isEmpty())
			alation_integration_enabled = "N";

		if (secretKey != null && !secretKey.trim().isEmpty()) {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String encryptedSecretKey = encryptor.encrypt(secretKey);

			String sql = "update listDataSchema set secretKey=?  where idDataSchema=?";
			jdbcTemplate.update(sql, encryptedSecretKey, idDataSchema);
		}
		if(sslEnabled==null || !sslEnabled.trim().equalsIgnoreCase("Y"))
			sslEnabled="N";

		if (password.trim().length() > 1) {
			String sql = "update listDataSchema set schemaType=?,ipAddress=?,databaseSchema=?,username=?,port=?,domain=?,password=?,gss_jaas=?,krb5conf=?,hivejdbchost='"
					+ hivejdbchost + "',hivejdbcport='" + hivejdbcport + "',sslTrustStorePath='" + sslTrustStorePath
					+ "',trustPassword='" + trustPassword + "',gatewayPath='" + gatewayPath + "',jksPath='" + jksPath
					+ "', zookeeperUrl='" + zookeeperUrl + "', folderPath='" + folderPath + "', fileNamePattern='"
					+ fileNamePattern + "', fileDataFormat='" + fileDataFormat + "', headerPresent='" + headerPresent
					+ "', headerFilePath='" + headerFilePath + "', headerFileNamePattern='" + headerFileNamePattern
					+ "', headerFileDataFormat='" + headerFileDataFormat + "', accessKey='" + accessKey
					+ "', bucketName='" + bucketName + "'" + ",bigQueryProjectName='" + bigQueryprojectName
					+ "',privateKeyId='" + privatekeyId + "',privateKey='" + privatekey + "',clientId='" + clientId
					+ "',clientEmail='" + clientEmail + "',datasetName='" + datasetName + "'" + ",azureClientId='"
					+ azureClientId + "',azureClientSecret='" + azureClientSecret + "',azureTenantId='" + azureTenantId
					+ "',azureServiceURI='" + azureServiceURI + "',azureFilePath='" + azureFilePath
					+ "', partitionedFolders='" + partitionedFolders + "'," + "multiPattern='" + multiPattern
					+ "', startingUniqueCharCount=" + startingUniqueCharCount + ",endingUniqueCharCount="
					+ endingUniqueCharCount + ",maxFolderDepth=" + maxFolderDepth + ", fileEncrypted='" + fileEncrypted
					+ "',singleFile='" + singleFile + "', externalfileNamePattern='" + externalFileNamePatternId
					+ "',externalfileName='" + externalFileName + "', patternColumn='" + patternColumn
					+ "', headerColumn='" + headerColumn + "',localDirectoryColumnIndex='" + localDirectoryColumnIndex
					+ "',xsltFolderPath='" + xsltFolderPath + "', kmsAuthDisabled='" + kmsAuthDisabled
					+ "', readLatestPartition='" + readLatestPartition + "',enableFileMonitoring='"
					+ enableFileMonitoring + "',alation_integration_enabled='" + alation_integration_enabled
					+ "',incremental_dataread_enabled='" + incrementalDataReadEnabled + "', multiFolderEnabled='"+multiFolderEnabled+"', push_down_query_enabled='"+pushDownQueryEnabled+"',sslEnb='"+sslEnabled+"' ,http_path=?, cluster_property_category=?,azure_authentication_type='"+azureAuthenticationType+"' where idDataSchema=?";
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String encryptedText = encryptor.encrypt(password);
			LOG.debug(sql);
			update = jdbcTemplate.update(sql, schemaType, uri, database, username, port, domain, encryptedText,
					serviceName, krb5conf,httpPath,clusterPropertyCategory, idDataSchema);

			// Update username, password and databaseSchema in listDataAccess
			sql = "update listDataAccess set userName=?, pwd=?, schemaName=? where idDataSchema=?";
			jdbcTemplate.update(sql, username, encryptedText, database, idDataSchema);

			return update;
		} else {
			String sql = "update listDataSchema set schemaType=?,ipAddress=?,databaseSchema=?,username=?,port=?,domain=?,gss_jaas=?,krb5conf=?,hivejdbchost='"
					+ hivejdbchost + "',hivejdbcport='" + hivejdbcport + "',sslTrustStorePath='" + sslTrustStorePath
					+ "',trustPassword='" + trustPassword + "',gatewayPath='" + gatewayPath + "',jksPath='" + jksPath
					+ "',zookeeperUrl='" + zookeeperUrl + "', folderPath='" + folderPath + "', fileNamePattern='"
					+ fileNamePattern + "', fileDataFormat='" + fileDataFormat + "', headerPresent='" + headerPresent
					+ "', headerFilePath='" + headerFilePath + "', headerFileNamePattern='" + headerFileNamePattern
					+ "', headerFileDataFormat='" + headerFileDataFormat + "', accessKey='" + accessKey
					+ "', bucketName='" + bucketName + "'" + ",bigQueryProjectName='" + bigQueryprojectName
					+ "',privateKeyId='" + privatekeyId + "',privateKey='" + privatekey + "',clientId='" + clientId
					+ "',clientEmail='" + clientEmail + "',datasetName='" + datasetName + "'" + ",azureClientId='"
					+ azureClientId + "',azureClientSecret='" + azureClientSecret + "',azureTenantId='" + azureTenantId
					+ "',azureServiceURI='" + azureServiceURI + "',azureFilePath='" + azureFilePath
					+ "', partitionedFolders='" + partitionedFolders + "'," + "multiPattern='" + multiPattern
					+ "', startingUniqueCharCount=" + startingUniqueCharCount + ",endingUniqueCharCount="
					+ endingUniqueCharCount + ",maxFolderDepth=" + maxFolderDepth + ", fileEncrypted='" + fileEncrypted
					+ "',singleFile='" + singleFile + "', externalfileNamePattern='" + externalFileNamePatternId
					+ "',externalfileName='" + externalFileName + "', patternColumn='" + patternColumn
					+ "', headerColumn='" + headerColumn + "',localDirectoryColumnIndex='" + localDirectoryColumnIndex
					+ "',xsltFolderPath='" + xsltFolderPath + "', kmsAuthDisabled='" + kmsAuthDisabled
					+ "', readLatestPartition='" + readLatestPartition + "',alation_integration_enabled='"
					+ alation_integration_enabled + "',enableFileMonitoring='" + enableFileMonitoring
					+ "',incremental_dataread_enabled='" + incrementalDataReadEnabled + "', multiFolderEnabled='"+multiFolderEnabled+"', push_down_query_enabled='"+pushDownQueryEnabled+"',sslEnb='"+sslEnabled+"' ,http_path=?,cluster_property_category=?,azure_authentication_type='"+azureAuthenticationType+"',cluster_policy_id='"+clusterPolicyId+"' where idDataSchema=?";

			LOG.debug(sql);
			update = jdbcTemplate.update(sql, schemaType, uri, database, username, port, domain, serviceName, krb5conf, httpPath,clusterPropertyCategory, idDataSchema);

			// Update username and databaseSchema in listDataAccess
			sql = "update listDataAccess set userName=?, schemaName=? where idDataSchema=?";
			jdbcTemplate.update(sql, username, database, idDataSchema);

			return update;
		}

	}

	public void updateKBEIntoListDataDefinition(Long idColumn, String columnName, String columnValue) {
		// String KBE="Y";
		if ((columnName.equalsIgnoreCase("numericalStat") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("stringStat") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("nonNull") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("recordAnomaly") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("datadrift") && columnValue.equalsIgnoreCase("N"))) {
			String kbeQuery = "SELECT KBE FROM listDataDefinition WHERE idColumn=" + idColumn
					+ " AND dataDrift='N' AND stringStat='N' AND numericalStat='N' AND nonNull='N' AND recordAnomaly='N'";
			LOG.debug(kbeQuery);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(kbeQuery);
			// LOG.debug(queryForRowSet.next());
			while (queryForRowSet.next()) {
				String sql = "update listDataDefinition set KBE=? where idColumn=?";
				jdbcTemplate.update(sql, "N", idColumn);
			}
		}
	}

	public Long updateDataIntoListDataDefinition(Long idColumn, String columnName, String columnValue) {
		try {
			String sql = "";
			String format = "";
			String isColMasked = "";
			String isLengthCheckEnabled = "";
			String isMaxLengthCheckEnabled = "";
			String isPrimaryKeyCheckEnabled = "";
			String isDuplicateKeyCheckEnabled = "";
			String getFormat = "select format,isMasked,lengthCheck,maxLengthCheck,primaryKey,dupkey from listDataDefinition where idColumn = "
					+ idColumn;
			SqlRowSet idColumnRowSet = jdbcTemplate.queryForRowSet(getFormat);

			if (idColumnRowSet.next()) {
				format = idColumnRowSet.getString("format");
				isColMasked = idColumnRowSet.getString("isMasked");
				isLengthCheckEnabled = idColumnRowSet.getString("lengthCheck");
				isMaxLengthCheckEnabled = idColumnRowSet.getString("maxLengthCheck");
				isPrimaryKeyCheckEnabled = idColumnRowSet.getString("primaryKey");
				isDuplicateKeyCheckEnabled = idColumnRowSet.getString("dupkey");
			}
			LOG.debug("idColumn =>" + idColumn + " columnName=> " + columnName + " columnValue=>" + columnValue
					+ " format col=>" + format + " isMasked=>" + isColMasked);

			// If the column is enabled for 'DoNotShow' , it is not eligible for
			// DataDrift, NumericalStat,RecordAnomaly and SubSegment
			if (columnValue != null && columnValue.equalsIgnoreCase("Y") && isColMasked != null
					&& isColMasked.equalsIgnoreCase("Y")
					&& (columnName.equalsIgnoreCase("datadrift") || columnName.equalsIgnoreCase("numericalStat")
					|| columnName.equalsIgnoreCase("recordAnomaly") || columnName.equalsIgnoreCase("dgroup"))) {
				LOG.debug("\n====> This column is 'DoNotShow' enabled, not eligible for " + columnName);
				return 0l;
			}

			if (columnName.equals("incrementalCol")) {
				if (format.equalsIgnoreCase("Date") || format.equalsIgnoreCase("date")
						|| format.equalsIgnoreCase("datetime2") || format.equalsIgnoreCase("datetime")) {

					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;

				} else {
					return (long) -1;
				}
			}
			// Changes related to isMasked
			else if (columnName.equals("isMasked")) {
				// Masking column is not allowed for following checks
				// DataDrift
				// Numerical stat
				// Record Anomaly
				// SubSegment
				String ldd_details = "select dataDrift,numericalStat,recordAnomaly,dgroup from listDataDefinition where idColumn = "
						+ idColumn;
				Map<String, Object> colDetailMap = jdbcTemplate.queryForMap(ldd_details);
				String dataDrift = (String) colDetailMap.get("dataDrift");
				String numericalStat = (String) colDetailMap.get("numericalStat");
				String recordAnomaly = (String) colDetailMap.get("recordAnomaly");
				String dgroup = (String) colDetailMap.get("dgroup");

				if (columnValue.equalsIgnoreCase("Y") && ((dataDrift != null && dataDrift.equalsIgnoreCase("Y"))
						|| (numericalStat != null && numericalStat.equalsIgnoreCase("Y"))
						|| (recordAnomaly != null && recordAnomaly.equalsIgnoreCase("Y"))
						|| (dgroup != null && dgroup.equalsIgnoreCase("Y")))) {

					return 0l;
				} else {
					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				}
			} else if (columnName.equals("lengthCheck") || (columnName.equals("lengthValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {

					if (columnValue.equalsIgnoreCase("Y") && isMaxLengthCheckEnabled != null
							&& isMaxLengthCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for Length check, already MaxLengthCheck is enabled!!");
						return 0l;
					} else {
						sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}

				} else {
					return (long) -1;
				}
			} else if (columnName.equals("maxLengthCheck") || (columnName.equals("lengthValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {

					if (columnValue.equalsIgnoreCase("Y") && isLengthCheckEnabled != null
							&& isLengthCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for MaxLengthCheck, already LengthCheck is enabled!!");
						return 0l;
					} else {
						sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("primaryKey") || columnName.equals("primaryKeyValue")) {
				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {

					if (columnValue.equalsIgnoreCase("Y") && isDuplicateKeyCheckEnabled != null
							&& isDuplicateKeyCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for PrimaryKey check, already Duplicate Key Check is enabled!!");
						return 0l;
					} else {
						sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else if (columnValue.equalsIgnoreCase("N")) {
					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				}else {
					return (long) -1;
				}
			} else if (columnName.equals("dupkey") || (columnName.equals("dupkeyValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {

					if (columnValue.equalsIgnoreCase("Y") && isPrimaryKeyCheckEnabled != null
							&& isPrimaryKeyCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for Duplicate Check, already Primary Key Check is enabled!!");
						return 0l;
					} else {
						sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				}else if (columnValue.equalsIgnoreCase("N")) {
					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("dateRule")) {

				if (format.toLowerCase().contains("date")) {

					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				} else {
					return (long) -1;
				}

				// Changes regarding numericalStat
			} else if ((columnName.equalsIgnoreCase("numericalStat"))) {

				if (columnValue.equalsIgnoreCase("Y") && (format.equalsIgnoreCase("NUMBER")
						|| format.equalsIgnoreCase("int") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC") || format.equalsIgnoreCase("BIGINT")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("FLOAT")
						|| format.equalsIgnoreCase("DOUBLE") || format.equalsIgnoreCase("DECIMAL"))) {

					sql = "update listDataDefinition set " + columnName + "=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", idColumn);

				} else if (columnValue.equalsIgnoreCase("N")) {

					sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, idColumn);

				} else {
					return (long) -1;
				}
			} else {

				if (columnName.equalsIgnoreCase("datadrift") && columnValue.equalsIgnoreCase("Y")) {

					sql = "update listDataDefinition set " + columnName + "=?,stringStat=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", "Y", idColumn);

				} else if ((columnName.equalsIgnoreCase("stringStat") && columnValue.equalsIgnoreCase("Y"))
						|| (columnName.equalsIgnoreCase("nonNull") && columnValue.equalsIgnoreCase("Y"))
						|| (columnName.equalsIgnoreCase("recordAnomaly") && columnValue.equalsIgnoreCase("Y"))) {

					sql = "update listDataDefinition set " + columnName + "=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", idColumn);

				} else {

					// Query compatibility changes for both POSTGRES and MYSQL
					// For threshold columns
					List<String> threshold_columns_list = new ArrayList<String>();
					threshold_columns_list.add("nullcountthreshold");
					threshold_columns_list.add("numericalthreshold");
					threshold_columns_list.add("stringstatthreshold");
					threshold_columns_list.add("datadriftthreshold");
					threshold_columns_list.add("recordanomalythreshold");
					threshold_columns_list.add("outofnormstatthreshold");
					threshold_columns_list.add("lengthcheckthreshold");
					threshold_columns_list.add("baddatacheckthreshold");
					threshold_columns_list.add("patterncheckthreshold");

					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
							&& (threshold_columns_list.contains(columnName.toLowerCase()))) {
						double threshold_value = 0.0;
						if (columnValue != null) {
							threshold_value = Double.parseDouble(columnValue);
						}

						sql = "update listDataDefinition set " + columnName + "=" + threshold_value + " where idColumn=?";
						return (long) jdbcTemplate.update(sql, idColumn);
					} else {
						sql = "update listDataDefinition set " + columnName + "=? where idColumn=?";
						return (long) jdbcTemplate.update(sql, columnValue, idColumn);
					}

				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public Long saveDataIntoListDataSchema(String uri, String database, String username, String password, String port,
			String schemaName, String schemaType, String domain, String serviceName, String krb5conf,
			String autoGenerateId, String suffix, String prefix, String hivejdbchost, String hivejdbcport,
			String sslEnb, String sslTrustStorePath, String trustPassword, String gatewayPath, String jksPath,
			String zookeeperUrl, String folderPath, String fileNamePattern, String fileDataFormat, String headerPresent,
			String headerFilePath, String headerFileNamePattern, String headerFileDataFormat, Long projectid,
			String createdByUser, String accessKey, String secretKey, String bucketName, String bigQueryProjectName,
			String privatekeyId, String privatekey, String clientId, String clientEmail, String datasetName,
			String azureClientId, String azureClientSecret, String azureTenantId, String azureServiceURI,
			String azureFilePath, String partitionedFolders, String enableFileMonitoring, String multiPattern,
			int startingUniqueCharCount, int endingUniqueCharCount, int maxFolderDepth, String fileEncrypted,
			Integer domainId, String singleFile, String externalFileNamePatternId, String externalFileName,
			String patternColumn, String headerColumn, String localDirectoryColumnIndex, String xsltFolderPath,
			String kmsAuthDisabled, String readLatestPartition, String alation_integration_enabled,
			String incrementalDataReadEnabled, String clusterPropertyCategory, String multiFolderEnabled, 
			String pushDownQueryEnabled,String httpPath, String clusterPolicyId,String azureAuthenticationType) {

		String sql = "insert into listDataSchema(ipAddress,databaseSchema,username,password,port,createdAt, updatedAt,"
				+ "createdBy,updatedBy,schemaName,schemaType,domain,gss_jaas,krb5conf,autoGenerate,suffixes,prefixes,"
				+ "hivejdbchost,hivejdbcport,sslEnb,sslTrustStorePath,trustPassword, Action, gatewayPath, jksPath, "
				+ "zookeeperUrl, folderPath, fileNamePattern, fileDataFormat, headerPresent, headerFilePath, "
				+ "headerFileNamePattern, headerFileDataFormat,project_id,createdByUser,accessKey, secretKey, bucketName,"
				+ "privateKeyId,privatekey,clientId,clientEmail,datasetName,bigQueryprojectName,"
				+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath, partitionedFolders, enableFileMonitoring,"
				+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount,maxFolderDepth,fileEncrypted,domain_id,singleFile,"
				+ "externalfileNamePattern, externalfileName,patternColumn,headerColumn, localDirectoryColumnIndex,xsltFolderPath, "
				+ "kmsAuthDisabled,readLatestPartition,alation_integration_enabled,incremental_dataread_enabled,cluster_property_category, multiFolderEnabled, push_down_query_enabled,http_path,cluster_policy_id,azure_authentication_type)"
				+ " VALUES (?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		LOG.debug("==============>sql" + sql);
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddataschema"
				: "idDataSchema";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, uri);
				pst.setString(2, database);
				pst.setString(3, username);
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String encryptedText = encryptor.encrypt(password);
				// LOG.debug("Encrypted text is: " + encryptedText);
				pst.setString(4, encryptedText);
				pst.setString(5, port);
				pst.setInt(6, 1);
				pst.setInt(7, 1);
				pst.setString(8, schemaName);
				pst.setString(9, schemaType);
				pst.setString(10, domain);
				pst.setString(11, serviceName);
				pst.setString(12, krb5conf);
				pst.setString(13, autoGenerateId);
				pst.setString(14, suffix);
				pst.setString(15, prefix);
				pst.setString(16, hivejdbchost);
				pst.setString(17, hivejdbcport);
				pst.setString(18, sslEnb);
				pst.setString(19, sslTrustStorePath);
				pst.setString(20, trustPassword);
				pst.setString(21, "Yes");
				pst.setString(22, gatewayPath);
				pst.setString(23, jksPath);
				pst.setString(24, zookeeperUrl);
				pst.setString(25, folderPath);
				pst.setString(26, fileNamePattern);
				pst.setString(27, fileDataFormat);
				pst.setString(28, headerPresent);
				pst.setString(29, headerFilePath);
				pst.setString(30, headerFileNamePattern);
				pst.setString(31, headerFileDataFormat);
				pst.setLong(32, projectid);
				pst.setString(33, createdByUser);
				pst.setString(34, accessKey);
				String encryptedSecretKey = "";
				if (secretKey != null && !secretKey.trim().isEmpty())
					encryptedSecretKey = encryptor.encrypt(secretKey);
				pst.setString(35, encryptedSecretKey);
				pst.setString(36, bucketName);
				pst.setString(37, privatekeyId);
				pst.setString(38, privatekey);
				pst.setString(39, clientId);
				pst.setString(40, clientEmail);
				pst.setString(41, datasetName);
				pst.setString(42, bigQueryProjectName);
				pst.setString(43, azureClientId);
				pst.setString(44, azureClientSecret);
				pst.setString(45, azureTenantId);
				pst.setString(46, azureServiceURI);
				pst.setString(47, azureFilePath);
				pst.setString(48, partitionedFolders);
				pst.setString(49, enableFileMonitoring);
				pst.setString(50, multiPattern);
				pst.setInt(51, startingUniqueCharCount);
				pst.setInt(52, endingUniqueCharCount);
				pst.setInt(53, maxFolderDepth);
				pst.setString(54, fileEncrypted);
				pst.setInt(55, domainId);
				pst.setString(56, singleFile);
				pst.setString(57, externalFileNamePatternId);
				pst.setString(58, externalFileName);
				pst.setString(59, patternColumn);
				pst.setString(60, headerColumn);
				pst.setString(61, localDirectoryColumnIndex);
				pst.setString(62, xsltFolderPath);
				pst.setString(63, kmsAuthDisabled);
				pst.setString(64, readLatestPartition);
				pst.setString(65, alation_integration_enabled);
				pst.setString(66, incrementalDataReadEnabled);
				pst.setString(67, clusterPropertyCategory);
				pst.setString(68, multiFolderEnabled);
				pst.setString(69, pushDownQueryEnabled);
				pst.setString(70, httpPath);
				pst.setString(71, clusterPolicyId);
				pst.setString(72, clusterPolicyId);
				return pst;
			}
		}, keyHolder);
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
		Long idDataSchema = keyHolder.getKey().longValue();
		// LOG.debug("idDataSchema"+idDataSchema);
		return idDataSchema;
	}

	@Override
	public long saveConnection(DataConnection connection) {
		String sql = "insert into listDataSchema(ipAddress,databaseSchema,username,password,port,createdAt, updatedAt,"
				+ "createdBy,updatedBy,schemaName,schemaType,domain,gss_jaas,krb5conf,autoGenerate,suffixes,prefixes,"
				+ "hivejdbchost,hivejdbcport,sslEnb,sslTrustStorePath,trustPassword, Action, gatewayPath, jksPath, "
				+ "zookeeperUrl, folderPath, fileNamePattern, fileDataFormat, headerPresent, headerFilePath, "
				+ "headerFileNamePattern, headerFileDataFormat,project_id,createdByUser,accessKey, secretKey, bucketName,"
				+ "privateKeyId,privatekey,clientId,clientEmail,datasetName,bigQueryprojectName,"
				+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath, partitionedFolders, enableFileMonitoring,"
				+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount,maxFolderDepth,fileEncrypted,domain_id,singleFile,"
				+ "externalfileNamePattern, externalfileName,patternColumn,headerColumn, localDirectoryColumnIndex,xsltFolderPath, kmsAuthDisabled,readLatestPartition,alation_integration_enabled,incremental_dataread_enabled, push_down_query_enabled) "
				+ " VALUES (?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		LOG.debug("==============>sql" + sql);
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddataschema"
				: "idDataSchema";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, connection.getUri());
				pst.setString(2, connection.getDatabase());
				pst.setString(3, connection.getUsername());
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String encryptedText = encryptor.encrypt(connection.getPassword());
				// LOG.debug("Encrypted text is: " + encryptedText);
				pst.setString(4, encryptedText);
				pst.setString(5, connection.getPort());
				pst.setInt(6, 1);
				pst.setInt(7, 1);
				pst.setString(8, connection.getSchemaName());
				pst.setString(9, connection.getSchemaType());
				pst.setString(10, connection.getDomain());
				pst.setString(11, connection.getServiceName());
				pst.setString(12, connection.getKrb5conf());
				pst.setString(13, connection.getAutoGenerateId());
				pst.setString(14, connection.getSuffix());
				pst.setString(15, connection.getPrefix());
				pst.setString(16, connection.getHivejdbchost());
				pst.setString(17, connection.getHivejdbcport());
				pst.setString(18, connection.getSslEnb());
				pst.setString(19, connection.getSslTrustStorePath());
				pst.setString(20, connection.getTrustPassword());
				pst.setString(21, "Yes");
				pst.setString(22, connection.getGatewayPath());
				pst.setString(23, connection.getJksPath());
				pst.setString(24, connection.getZookeeperUrl());
				pst.setString(25, connection.getFolderPath());
				pst.setString(26, connection.getFileNamePattern());
				pst.setString(27, connection.getFileDataFormat());
				pst.setString(28, connection.getHeaderPresent());
				pst.setString(29, connection.getHeaderFilePath());
				pst.setString(30, connection.getHeaderFileNamePattern());
				pst.setString(31, connection.getHeaderFileDataFormat());
				pst.setLong(32, connection.getProjectId());
				pst.setString(33, connection.getCreatedBy());
				pst.setString(34, connection.getAccessKey());
				String encryptedSecretKey = "";
				if (connection.getSecretKey() != null && !connection.getSecretKey().trim().isEmpty())
					encryptedSecretKey = encryptor.encrypt(connection.getSecretKey());
				pst.setString(35, encryptedSecretKey);
				pst.setString(36, connection.getBucketName());
				pst.setString(37, connection.getPrivatekeyId());
				pst.setString(38, connection.getPrivatekey());
				pst.setString(39, connection.getClientId());
				pst.setString(40, connection.getClientEmail());
				pst.setString(41, connection.getDatasetName());
				pst.setString(42, connection.getBigQueryProjectName());
				pst.setString(43, connection.getAzureClientId());
				pst.setString(44, connection.getAzureClientSecret());
				pst.setString(45, connection.getAzureTenantId());
				pst.setString(46, connection.getAzureServiceURI());
				pst.setString(47, connection.getAzureFilePath());
				pst.setString(48, connection.getPartitionedFolders());
				pst.setString(49, connection.getEnableFileMonitoring());
				pst.setString(50, connection.getMultiPattern());
				pst.setInt(51, connection.getStartingUniqueCharCount());
				pst.setInt(52, connection.getEndingUniqueCharCount());
				pst.setInt(53, connection.getMaxFolderDepth());
				pst.setString(54, connection.getFileEncrypted());
				pst.setInt(55, connection.getDomainId());
				pst.setString(56, connection.getSingleFile());
				pst.setString(57, connection.getExternalFileNamePatternId());
				pst.setString(58, connection.getExternalFileName());
				pst.setString(59, connection.getPatternColumn());
				pst.setString(60, connection.getHeaderColumn());
				pst.setString(61, connection.getLocalDirectoryColumnIndex());
				pst.setString(62, connection.getXsltFolderPath());
				pst.setString(63, connection.getKmsAuthDisabled());
				pst.setString(64, connection.getReadLatestPartition());
				pst.setString(65, connection.getAlationIntegrationEnabled());
				pst.setString(66, connection.getIncrementalDataReadEnabled());
				return pst;
			}
		}, keyHolder);
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
		Long idDataSchema = keyHolder.getKey().longValue();
		// LOG.debug("idDataSchema"+idDataSchema);
		return idDataSchema;
	}

	// LOG.debug("Port"+port);
	/*
	 * int dotPosition = database.indexOf("."); String databasecontest="";
	 * if(dotPosition!=-1){ databasecontest = database.substring(0,dotPosition); }
	 * else databasecontest=database;
	 */

	/*
	 * String sql1=
	 * "insert into listDataSchema(ipAddress,databaseScheme,username,password,port,createdAt,updatedAt,createdBy,updatedBy)"
	 * + " VALUES (?,?,?,?,?,now(),now(),now(),now())"; int count1 =
	 * jdbcTemplate.update(sql1,uri,database,username,password,port);
	 * LOG.debug("listDataSchema"+count1);
	 */

	/*
	 * try { Class.forName("com.vertica.jdbc.Driver"); Connection
	 * con=DriverManager.getConnection("jdbc:vertica://"+uri+":"+port+"/"+
	 * databasecontest,username,password);
	 *
	 * } catch (ClassNotFoundException | SQLException e) { //LOG.debug(
	 * "exception raised in test con"); e.printStackTrace(); return null; }
	 */

	// encrypting the password

	/*
	 * try { encryptedData = encryptionDecryption.encrypt(password); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * LOG.debug("encryptedData="+encryptedData);
	 */

	public List<ListDataSchema> readdatafromlistdataschema(Long idDataSchema) {

		String sqlSelect = "SELECT * FROM listDataSchema where idDataSchema=" + idDataSchema;
		List<ListDataSchema> listDataSchemaList = jdbcTemplate.query(sqlSelect, new RowMapper<ListDataSchema>() {

			public ListDataSchema mapRow(ResultSet result, int rowNum) throws SQLException {
				ListDataSchema listDataSchema = new ListDataSchema();
				listDataSchema.setIpAddress(result.getString("ipAddress"));
				listDataSchema.setDatabaseSchema(result.getString("databaseSchema"));
				listDataSchema.setUsername(result.getString("username"));
				listDataSchema.setPassword(result.getString("password"));
				listDataSchema.setPort(result.getString("port"));
				listDataSchema.setProjectId(result.getLong("project_id"));
				listDataSchema.setEnableFileMonitoring(result.getString("enableFileMonitoring"));
				listDataSchema.setAction(result.getString("Action"));
				return listDataSchema;
			}
		});
		for (ListDataSchema aContact : listDataSchemaList) {
			// LOG.debug(aContact.getIpAddress());
		}

		return listDataSchemaList;

	}

	public Object[] readTablesFromVertica(String uri, String databaseAndSchema, String username, String password,
			String port, Long idDataSchema, Long idUser) {

		//// LOG.debug("databaseAndSchema="+databaseAndSchema);
		int dotPosition = databaseAndSchema.indexOf(".");
		//// LOG.debug("dotPosition"+dotPosition);
		String database = databaseAndSchema;
		if (dotPosition != -1) {
			database = databaseAndSchema.substring(0, dotPosition);
		}
		List tableList = null;
		//// LOG.debug("database:"+database);
		String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
		// LOG.debug("schema:"+schema);
		String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
		Connection con = null;
		ResultSet metaDataResultSet = null;
		try {
			Class.forName("com.vertica.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			// LOG.debug("connection executed");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select table_name from tables where table_schema='" + schema + "'");
			tableList = new ArrayList();
			while (rs.next()) {
				String tableName = rs.getString("table_name");
				tableList.add(tableName);
			}
			rs.close();
			Long[] listDataSourcesIds = new Long[tableList.size()];
			String[] ldaInsertQueries = new String[tableList.size()];
			for (int i = 0; i < tableList.size(); i++) {
				List<ColumnNameDataTypeISNullableForVertica> columnNameDataTypeISNullable = new ArrayList<ColumnNameDataTypeISNullableForVertica>();
				String tableName = (String) tableList.get(i);
				// if(tableName.equals("employee")||tableName.equals("employee1")){
				metaDataResultSet = stmt.executeQuery(
						"select column_name,data_type,is_nullable from columns where table_name='" + tableName + "'");
				while (metaDataResultSet.next()) {
					ColumnNameDataTypeISNullableForVertica columnNameDataTypeISNullableObj = new ColumnNameDataTypeISNullableForVertica();
					columnNameDataTypeISNullableObj.setColumnName(metaDataResultSet.getString("column_name"));
					columnNameDataTypeISNullableObj.setDataType(metaDataResultSet.getString("data_type"));
					columnNameDataTypeISNullableObj.setIsNullable(metaDataResultSet.getString("is_nullable"));
					// LOG.debug(tableName+" "+columnName+"
					// "+dataType);
					columnNameDataTypeISNullable.add(columnNameDataTypeISNullableObj);
				}
				String s = databaseAndSchema;
				int indexOf = s.indexOf(".");
				String data = s.substring(indexOf + 1, s.length());
				// LOG.debug(data);
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strDate = sdf.format(cal.getTime());
				// LOG.debug("Current date in String Format: " +
				// strDate);
				String newcolumnname = data + "_" + tableName + "_" + strDate;
				// LOG.debug("newcolumnname="+newcolumnname);
				Long id = (Long) insertintolds(newcolumnname, idDataSchema, idUser);
				listDataSourcesIds[i] = id;
				LOG.debug("tableName:" + tableName);
				String ldaQuery = insertintolda(id, uri, port, username, password, idDataSchema, databaseAndSchema,
						tableName);
				ldaInsertQueries[i] = ldaQuery;

				String lddQuery = insertintoldd(id, idDataSchema, tableName, con, columnNameDataTypeISNullable);
			}
			jdbcTemplate.batchUpdate(ldaInsertQueries);
			return new Object[] { true, tableList, listDataSourcesIds };
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return new Object[] { false, tableList, null };
		}
	}

	private String insertintoldd(Long id, Long idDataSchema, String tableName, Connection con,
			List<ColumnNameDataTypeISNullableForVertica> columnNameDataTypeISNullable) {
		LOG.debug("tableNAmein ldd:" + tableName);
		LOG.debug("columnNameDataTypeISNullable:" + columnNameDataTypeISNullable.size());
		String[] lddQueries = new String[columnNameDataTypeISNullable.size()];
		try {
			int i = 0;
			for (ColumnNameDataTypeISNullableForVertica columnNameDataTypeISNullableObj : columnNameDataTypeISNullable) {
				String name = columnNameDataTypeISNullableObj.getDataType().toLowerCase();
				String numericalStat, stringStat;
				LOG.debug("numeric type:" + name);
				if (name.contains("int") || name.contains("float") || name.contains("numeric")) {

					numericalStat = "Y";
					stringStat = "N";
				} else if (name.contains("varchar") || name.contains("char")) {
					numericalStat = "N";
					stringStat = "Y";
				} else {
					numericalStat = "N";
					stringStat = "N";
				}
				String primaryKey = "N";
				try {
					DatabaseMetaData metaData = con.getMetaData();
					ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
					while (primaryKeysResultSet.next()) {
						if (primaryKeysResultSet.getString("PK_NAME").contains("C_PRIMARY")) {
							if (primaryKeysResultSet.getString("COLUMN_NAME")
									.equals(columnNameDataTypeISNullableObj.getColumnName().toString())) {
								primaryKey = "Y";
							}
						}
					}
				} catch (SQLException e) {
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
				String null_countAndNot_Null = "Y";
				if (columnNameDataTypeISNullableObj.getIsNullable().equalsIgnoreCase("t"))
					null_countAndNot_Null = "N";
				// LOG.debug("primaryKey= "+primaryKey+" "+tableName);

				// String sql2="insert into
				// listDataDefinition(idData,columnName,displayName,nonNull,primaryKey,format,hashValue,numericalStat,stringStat,KBE,dgroup,dupkey,measurement,blend,idDataSchema,incrementalCol)"+
				// " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'N')";
				String sql3 = "insert into listDataDefinition(idData,columnName,displayName,nonNull,primaryKey,format,hashValue,numericalStat,stringStat,KBE,dgroup,dupkey,measurement,blend,idDataSchema,incrementalCol)"
						+ " VALUES (" + id + "," + "''" + ",'" + columnNameDataTypeISNullableObj.getColumnName() + "','"
						+ null_countAndNot_Null + "','" + primaryKey + "','"
						+ columnNameDataTypeISNullableObj.getDataType() + "','N','" + numericalStat + "','" + stringStat
						+ "','Y','N','Y'," + "''" + "," + "''" + "," + idDataSchema + ",'N')";
				// int count2 = jdbcTemplate.update(sql2,id,"
				// ",columnNameDataTypeISNullableObj.getColumnName(),null_countAndNot_Null,primaryKey,
				// columnNameDataTypeISNullableObj.getDataType(),"N",numericalStat,stringStat,"Y","N","Y","","",idDataSchema);

				// LOG.debug("listDataDefinition"+count2);
				lddQueries[i] = sql3;
				i++;
			}
			jdbcTemplate.batchUpdate(lddQueries);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private String insertintolda(Long id, String uri, String port, String username, String password, Long idDataSchema,
			String databaseAndSchema, String tableName) {
		// LOG.debug("id:"+id);
		String sql1 = "insert into listDataAccess(idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,incrementalType)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		String sql2 = "insert into listDataAccess(idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,incrementalType)"
				+ " VALUES (" + id + ",'" + uri + "','" + port + "','" + username + "','" + password + "','"
				+ databaseAndSchema + "','" + tableName + "','N','" + idDataSchema + "','N')";
		// int count1 =
		// jdbcTemplate.update(sql1,id,uri,port,username,password,databaseAndSchema,tableName,"N",idDataSchema,"N");
		// LOG.debug("listDataAccess"+count1);
		return sql2;
	}

	public long insertintolds(String newcolumnname, Long idDataSchema, Long idUSer) {
		// LOG.debug("idDataSchema:"+idDataSchema);
		String sql = "insert into listDataSources(idDataSchema,name,description,dataLocation,createdAt,updatedAt,createdBy,updatedBy,dataSource)"
				+ " VALUES (?,?,?,?,now(),now(),?,?,?)";
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
						: "idData";
				
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setLong(1, idDataSchema);
				pst.setString(2, newcolumnname);
				pst.setString(3, newcolumnname);
				pst.setString(4, "Vertica");
				pst.setLong(5, idUSer);
				pst.setLong(6, idUSer);
				pst.setString(7, "-");
				return pst;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	public List<Long> insertDataInListApplications(List tableList, Long[] listDataSourcesIds, String dataBase,
			String strDate, Long idUser) {

		int dotPosition = dataBase.indexOf(".");
		// LOG.debug("dotPosition"+dotPosition);
		String schema = "";
		if (dotPosition != -1) {
			schema = dataBase.substring(dotPosition + 1, dataBase.length());
		}

		List<Long> listApplicationIdAppList = new ArrayList<Long>();
		// for(Entry<Long ,String> entry:idAndTableNameMap.entrySet())
		// for(Object o:tableList)
		for (int count = 0; count < tableList.size(); count++) {

			String table = (String) tableList.get(count);
			String name = "VC_" + schema + "_" + table + "_" + strDate;
			Long idData = listDataSourcesIds[count];
			String sql = "insert into listApplications(name,description,appType,idData,idRightData,createdBy,createdAt,updatedAt,updatedBy)"
					+ " VALUES (?,?,?,?,?,1,now(),now(),?)";
			
			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
							: "idData";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setString(1, name);
					pst.setString(2, name);
					pst.setString(3, "DATA_QUALITY");
					pst.setLong(4, idData);
					pst.setString(5, null);
					pst.setLong(6, idUser);
					return pst;
				}
			}, keyHolder);
			listApplicationIdAppList.add(keyHolder.getKey().longValue());
		}
		return listApplicationIdAppList;
	}

	public Long[] insertListDFSetRule(List<Long> listApplicationsIdList) {
		Long[] listDfSetRuleidDfDfSetArray = new Long[listApplicationsIdList.size()];
		try {
			int i = 0;
			for (Long id : listApplicationsIdList) {
				String sql = "insert into listDFSetRule(idApp,count,sum,correlation,statisticalParam,duplicateFile) values(?,?,?,?,?,?)";
				
				// Query compatibility changes for both POSTGRES and MYSQL
				String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
								: "idData";
				
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
						pst.setLong(1, id);
						pst.setString(2, "Y");
						pst.setString(3, "N");
						pst.setString(4, "N");
						pst.setString(5, "N");
						pst.setString(6, "N");
						return pst;
					}
				}, keyHolder);
				listDfSetRuleidDfDfSetArray[i] = keyHolder.getKey().longValue();
				i++;
			}
			return listDfSetRuleidDfDfSetArray;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return listDfSetRuleidDfDfSetArray;
	}

	public void insertIntoTranRule(List<Long> listApplicationsIdList) {
		try {
			String[] queries = new String[listApplicationsIdList.size() * 2];
			List<String> queriesList = new ArrayList<String>();
			for (Long idApp : listApplicationsIdList) {
				Long seqIdCol = jdbcTemplate.queryForObject(
						"select max(idColumn) from listDataDefinition where idData= (select idData from listApplications where idApp="
								+ idApp + ")",
						Long.class);
				if (seqIdCol == null) {
					seqIdCol = 0l;
				}
				String sql1 = "insert into listDFTranRule (idApp,dupRow,seqRow,seqIdCol,threshold,type) values ("
						+ idApp + ",'Y','N'," + seqIdCol + "," + 0 + ",'all')";
				queriesList.add(sql1);
				String sql2 = "insert into listDFTranRule (idApp,dupRow,seqRow,seqIdCol,threshold,type) values ("
						+ idApp + ",'Y','N'," + seqIdCol + "," + 0 + ",'identity')";
				queriesList.add(sql2);
			}

			String[] array = queriesList.toArray(new String[queriesList.size()]);
			jdbcTemplate.batchUpdate(array);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	public void insertListDFSetComparisonRule(Long[] listDfSetRuleidDfDfSetArray, Long[] listDataSourcesIds) {
		try {
			String[] listDFSetComparisonRuleQueries = new String[listDfSetRuleidDfDfSetArray.length];
			int i = 0;
			for (Long idDFSet : listDfSetRuleidDfDfSetArray) {
				String thresholdSql = "select max(numericalThreshold) from listDataDefinition where idData="
						+ listDataSourcesIds[i];
				LOG.debug("thresholdSql:" + thresholdSql);
				Long threshold = jdbcTemplate.queryForObject(thresholdSql, Long.class);
				if (threshold == null)
					threshold = 0l;
				String query = "insert into listDFSetComparisonRule (idDFSet,comparisonType,comparisonMethod,comparisonDuration,threshold) values("
						+ idDFSet + ",'History','Std Deviation',0," + threshold + ")";
				LOG.debug("queryfor lisDfsetCompariosionRule:" + query);
				listDFSetComparisonRuleQueries[i] = query;
			}
			jdbcTemplate.batchUpdate(listDFSetComparisonRuleQueries);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String getPasswordForIdDataSchema(long idDataSchema) {
		try {
			String sql = "select password from listDataSchema where idDataSchema=" + idDataSchema;
			String password = jdbcTemplate.queryForObject(sql, String.class);
			if(password!=null && !password.trim().isEmpty()){
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
				decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String decryptedText = decryptor.decrypt(password);
				return decryptedText;
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public String getStatusForHiveKerberos(Long idDataSchema) {
		try {
			String sql = "select description from hiveSource where idDataSchema=" + idDataSchema
					+ " order by idHiveSource desc limit 1";
			return jdbcTemplate.queryForObject(sql, String.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public void insertDataIntoHiveSourceForAutoGenerateStatus(Long idDataSchema) {
		try {
			String query = "insert into hiveSource(name,description,idDataSchema,tableName) " + "values('','',"
					+ idDataSchema + ",'')";
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteEntryFromListDataSchemaAndHiveSource(Long idDataSchema) {
		try {
			String sql = "delete from hiveSource where idDataSchema=" + idDataSchema;
			String sql1 = "delete from listDataSchema where idDataSchema=" + idDataSchema;
			jdbcTemplate.batchUpdate(sql, sql1);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	public void processAutoGenerateTemplate_oracleRAC(String uri, String database, String username, String password,
			String port, String schemaName, String schemaType, String domain, String serviceName, String krb5conf,
			String autoGenerateId, long idUser, Long idDataSchema, Long projectId, Integer domainId) {
		try {
			String url = "";
			url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" + "(HOST = " + uri + ")(PORT = " + port
					+ "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName + ")))";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = null;
			try {
				con = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				LOG.error("Connection Failed");
				jdbcTemplate.update("delete from listDataSchema where idDataSchema=" + idDataSchema);
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
				return;
			}
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select TABLE_NAME from all_tables where owner='" + database + "'");
			ArrayList<String> tables = new ArrayList<String>();
			while (rs.next()) {
				tables.add(rs.getString(1));
			}
			// total tables
			LOG.debug("total tables=" + tables.size());
			jdbcTemplate.update(
					"update hiveSource set totalTables=" + tables.size() + " where idDataSchema=" + idDataSchema);
			// for (String tableName : tables) {
			for (int count = 0; count < tables.size(); count++) {
				String tableName = tables.get(count);
				try {
					String query = "select * from " + database + "." + tableName + " where ROWNUM <= 1";
					ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
					Map<String, String> tableData = new LinkedHashMap<String, String>();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						String columnName = metaData.getColumnName(i);
						String columnType = metaData.getColumnTypeName(i);
						// LOG.debug("columnName=" + columnName);
						// LOG.debug("columnType=" + columnType);
						tableData.put(columnName, columnType);
					}
					ListDataSource listDataSource = new ListDataSource();
					listDataSource.setName(schemaName + "_" + idDataSchema + "_" + tableName);
					listDataSource.setDescription(schemaName + "_" + idDataSchema + "_" + tableName);
					listDataSource.setDataLocation("Oracle RAC");
					listDataSource.setDataSource("SQL");
					listDataSource.setCreatedAt(new Date());
					listDataSource.setCreatedBy(idUser);
					listDataSource.setIdDataSchema(idDataSchema);
					listDataSource.setGarbageRows(0l);
					listDataSource.setDomain(domainId);

					listDataAccess listdataAccess = new listDataAccess();
					listdataAccess.setHostName(uri);
					listdataAccess.setPortName(port);
					listdataAccess.setUserName(username);
					listdataAccess.setPwd(password);
					listdataAccess.setSchemaName(serviceName);
					listdataAccess.setFolderName(tableName);
					listdataAccess.setIdDataSchema(idDataSchema);
					listdataAccess.setWhereCondition("");
					listdataAccess.setQuery("N");
					listdataAccess.setHistoricDateTable("");
					List<String> primarykeyCols = new ArrayList<String>();
					Long i = dataTemplateAddNewDAO.addintolistdatasourceForOracle(listDataSource, tableData,
							listdataAccess, primarykeyCols, projectId);
					// LOG.debug("i:" + i);
				} catch (Exception e) {
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
				jdbcTemplate.update(
						"update hiveSource set completedTables=" + (count + 1) + " where idDataSchema=" + idDataSchema);
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	public Long getStatusForAutoDT() {
		try {
			String sql = "select * from hiveSource order by idHiveSource desc limit 1";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				double totalTables = queryForRowSet.getDouble("totalTables");
				double completedTables = queryForRowSet.getDouble("completedTables");
				LOG.debug("totalTables=" + totalTables + "completedTables=" + completedTables);
				LOG.debug(((completedTables / totalTables) * 100));
				return (long) ((completedTables / totalTables) * 100);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public Long insertDataIntoListBatchSchema(String schemaBatchName, String schemaType, String originalFilename) {
		String sql = "insert into list_batch_schema(schemaBatchName,schemaBatchType,batchFileLocation)"
				+ " VALUES (?,?,?)";
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddataschema"
						: "idDataSchema";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, schemaBatchName);
				pst.setString(2, schemaType);
				pst.setString(3, originalFilename);
				return pst;
			}
		}, keyHolder);
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
		Long idBatchSchema = keyHolder.getKey().longValue();
		// LOG.debug("idDataSchema"+idDataSchema);
		return idBatchSchema;
	}

	@Override
	public void updateidDataSchemasInListBatchSchema(Long idBatchSchema, String idDataSchemas) {
		try {
			jdbcTemplate.update("update list_batch_schema set idDataSchemas='" + idDataSchemas
					+ "' where idBatchSchema=" + idBatchSchema);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public ListDataSchema getSchemaDetailsForConnectionUtil(Long idDataSchema) {

		String sqlSelect = "SELECT schemaName,schemaType,ipAddress,userName,password,project_id,domain_id FROM listDataSchema where idDataSchema="
				+ idDataSchema;
		ListDataSchema listDataSchema = (ListDataSchema) jdbcTemplate.queryForObject(sqlSelect,
				new RowMapper<ListDataSchema>() {

					public ListDataSchema mapRow(ResultSet result, int rowNum) throws SQLException {
						ListDataSchema listDataSchema = new ListDataSchema();
						listDataSchema.setSchemaName(result.getString("schemaName"));
						listDataSchema.setSchemaType(result.getString("schemaType"));
						listDataSchema.setIpAddress(result.getString("ipAddress"));
						listDataSchema.setUsername(result.getString("userName"));
						listDataSchema.setPassword(result.getString("password"));
						listDataSchema.setProjectId(result.getLong("project_id"));
						listDataSchema.setDomainId(result.getInt("domain_id"));
						return listDataSchema;

					}
				});

		return listDataSchema;
	}

	public Long updateIntoListGlobalThreshold(String idColumn, String columnName, Double columnValue) {
		try {
			String sql = "";

			sql = "update listGlobalThresholds set " + columnName + "=? where globalColumnName=?";
			return (long) jdbcTemplate.update(sql, columnValue, idColumn);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public String inserIntoListGlobalThreshold(int domainId, String columnName) {
		String sRetResult = "";

		try {
			String sql = "";

			sql = "INSERT INTO listGlobalThresholds(domainId, globalColumnName) VALUES (?,?)";
			jdbcTemplate.update(sql, domainId, columnName);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			sRetResult = e.getMessage();
		}
		return sRetResult;
	}

	@Override
	public List<String> getSchemaNames(Long projectId) {
		List<String> schemaNameList = null;
		try {

			String sql = "Select idDataSchema, schemaName from listDataSchema where project_id=? and Action = 'Yes' order by idDataSchema desc";
			schemaNameList = jdbcTemplate.query(sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					String schemaData = rs.getLong("idDataSchema") + "-" + rs.getString("schemaName");
					return schemaData;
				}
			}, projectId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return schemaNameList;
	}

	@Override
	public boolean updateEnableFileMonitoringFlagForSchema(Long idDataSchema, String status) {
		boolean result = false;
		try {
			String sql = "update listDataSchema set enableFileMonitoring=? where idDataSchema=?";
			jdbcTemplate.update(sql, status, idDataSchema);
			result = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Long> getFileMonitoringValidationsForSchema(Long idDataSchema) {
		List<Long> fmAppIdList = null;
		try {
			String sql = "select idApp from file_monitor_rules where idDataSchema=? and idApp != 0";
			fmAppIdList = jdbcTemplate.queryForList(sql, Long.class, idDataSchema);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return fmAppIdList;
	}

	@Override
	public List<Long> getValidationsForFMConnection(long idDataSchema) {
		List<Long> validationAppIdList = null;
		try {
			String sql = "select la.idApp from listApplications la join fm_connection_details fmc on la.idApp = fmc.idApp where fmc.idDataSchema=? and " +
					"la.appType='File Monitoring' and la.continuousFileMonitoring='Y';";

			System.out.println("sql:"+sql.replace("?",""+idDataSchema));
			validationAppIdList = jdbcTemplate.queryForList(sql, Long.class, idDataSchema);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationAppIdList;
	}

	@Override
	public List<Long> getIdAppFromFmConnectionDetails(long idDataSchema) {
		List<Long> validationAppId = null;
		try {
			String sql = "select fmc.idApp from fm_connection_details fmc join listApplications la on fmc.idApp=la.idApp where fmc.idDataSchema=? order by idApp desc limit 1";
			validationAppId = jdbcTemplate.queryForList(sql, Long.class, idDataSchema);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationAppId;
	}

	@Override
	public Long getConnectionIdForFMValidation(long idApp) {
		Long connectionId = 0l;
		try {
			String sql = "select idDataSchema from fm_connection_details where  idApp=? limit 1";
			connectionId = jdbcTemplate.queryForObject(sql, Long.class, idApp);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return connectionId;
	}

	@Override
	public boolean activateOrDeactivateFMValidations(List<Long> fmAppIdList, String activeFlagStatus) {
		boolean result = false;
		try {
			if (fmAppIdList != null && fmAppIdList.size() > 0) {
				String idAppStr = "";
				for (long idApp : fmAppIdList) {
					idAppStr = idAppStr + idApp + ",";
				}
				idAppStr = idAppStr.substring(0, idAppStr.length() - 1);
				String sql = "update listApplications set active=? where idApp in (" + idAppStr + ")";
				System.out.println("sql:"+sql);
				jdbcTemplate.update(sql, activeFlagStatus);
				result = true;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public HashMap<String, Long> getDomainProjectFromSchema(long nIdDataSchema) {
		HashMap<String, Long> oRetValue = new HashMap<String, Long>() {
			{
				put("ProjectId", -1l);
				put("DomainId", -1l);
			}
		};

		String sDomainProjectSql = "select project_id as ProjectId, domain_id as DomainId from listDataSchema where idDataSchema = %1$s;";
		SqlRowSet oSqlRowSet = null;

		try {
			sDomainProjectSql = String.format(sDomainProjectSql, nIdDataSchema);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sDomainProjectSql);

			if (oSqlRowSet.next()) {
				oRetValue.put("ProjectId", oSqlRowSet.getLong("ProjectId"));
				oRetValue.put("DomainId", oSqlRowSet.getLong("DomainId"));
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return oRetValue;
	}

	@Override
	public void updateKBEIntoStagingListDataDefinition(Long idColumn, String columnName, String columnValue) {
		// String KBE="Y";
		if ((columnName.equalsIgnoreCase("numericalStat") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("stringStat") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("nonNull") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("recordAnomaly") && columnValue.equalsIgnoreCase("N"))
				|| (columnName.equalsIgnoreCase("datadrift") && columnValue.equalsIgnoreCase("N"))) {
			String kbeQuery = "SELECT KBE FROM staging_listDataDefinition WHERE idColumn=" + idColumn
					+ " AND dataDrift='N' AND stringStat='N' AND numericalStat='N' AND nonNull='N' AND recordAnomaly='N'";
			LOG.debug(kbeQuery);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(kbeQuery);
			// LOG.debug(queryForRowSet.next());
			while (queryForRowSet.next()) {
				String sql = "update staging_listDataDefinition set KBE=? where idColumn=?";
				jdbcTemplate.update(sql, "N", idColumn);
			}
		}
	}

	@Override
	public long updateDataIntoStagingListDataDefinition(long idColumn, String columnName, String columnValue) {
		try {
			String sql = "";
			String format = "";
			String isColMasked = "";
			String isLengthCheckEnabled = "";
			String isMaxLengthCheckEnabled = "";
			String isPrimaryKeyCheckEnabled = "";
			String isDuplicateKeyCheckEnabled = "";
			String patternCheckEnabled = "";
			String getFormat = "select format,isMasked,lengthCheck,maxLengthCheck,primaryKey,dupkey,patternCheck from staging_listDataDefinition where idColumn = "
					+ idColumn;
			SqlRowSet idColumnRowSet = jdbcTemplate.queryForRowSet(getFormat);

			if (idColumnRowSet.next()) {
				format = idColumnRowSet.getString("format");
				isColMasked = idColumnRowSet.getString("isMasked");
				isLengthCheckEnabled = idColumnRowSet.getString("lengthCheck");
				isMaxLengthCheckEnabled = idColumnRowSet.getString("maxLengthCheck");
				isPrimaryKeyCheckEnabled = idColumnRowSet.getString("primaryKey");
				isDuplicateKeyCheckEnabled = idColumnRowSet.getString("dupkey");
				patternCheckEnabled = idColumnRowSet.getString("patternCheck");
			}
			LOG.debug("idColumn =>" + idColumn + " columnName=> " + columnName + " columnValue=>" + columnValue
					+ " format col=>" + format + " isMasked=>" + isColMasked);

			// If the column is enabled for 'DoNotShow' , it is not eligible for
			// DataDrift, NumericalStat,RecordAnomaly and SubSegment
			if (columnValue.equalsIgnoreCase("Y") && isColMasked != null && isColMasked.equalsIgnoreCase("Y")
					&& (columnName.equalsIgnoreCase("datadrift") || columnName.equalsIgnoreCase("numericalStat")
					|| columnName.equalsIgnoreCase("recordAnomaly") || columnName.equalsIgnoreCase("dgroup"))) {
				LOG.debug("\n====> This column is 'DoNotShow' enabled, not eligible for " + columnName);
				return 0l;
			}

			if (columnName.equals("incrementalCol")) {
				if (format.equalsIgnoreCase("Date") || format.equalsIgnoreCase("date")
						|| format.equalsIgnoreCase("datetime2") || format.equalsIgnoreCase("datetime")) {

					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				} else {
					return (long) -1;
				}
			}
			// Changes related to isMasked
			else if (columnName.equals("timelinessKey")) {
				sql = "select count(timelinessKey) as timelinessCount from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ") and timelinessKey='Y' and idColumn!=" + idColumn;
				SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

				while (sqlRowSet.next()) {
					int timelinessCount = sqlRowSet.getInt("timelinessCount");
					if (timelinessCount == 0) {
						if (format.equalsIgnoreCase("Date") || format.equalsIgnoreCase("date")
								|| format.equalsIgnoreCase("datetime2") || format.equalsIgnoreCase("datetime")) {
							sql = "select  sum( CASE WHEN startDate='Y' then 1 ELSE 0 END) as startDate, sum( CASE WHEN endDate='Y' then 1 ELSE 0 END) as endDate from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ")";
							SqlRowSet rSet = jdbcTemplate.queryForRowSet(sql);
							long startCount = 0l;
							long endCount = 0l;
							while (rSet.next()) {
								startCount = rSet.getLong("startDate");
								endCount = rSet.getLong("endDate");
							}
							if (columnValue.equalsIgnoreCase("Y") && (startCount == 0 || endCount == 0)) {
								return -3l;
							} else {
								sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
								jdbcTemplate.update(sql, columnValue, idColumn);
								return (long) 1;
							}
						}
						return (long) -2;
					} else {
						return (long) -1;
					}
				}
			} else if (columnName.equals("startDate")) {
				sql = "select count(startDate) as startDateCount from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ") and startDate='Y' and idColumn!=" + idColumn;
				SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
				while (sqlRowSet.next()) {
					int startDateCount = sqlRowSet.getInt("startDateCount");
					if (startDateCount == 0) {
						if (format.equalsIgnoreCase("Date") || format.equalsIgnoreCase("date") || format.equalsIgnoreCase("datetime2") || format.equalsIgnoreCase("datetime")) {
							if (columnValue.equalsIgnoreCase("N")) {
								sql = "select count(timelinessKey) as timelinessCount from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ") and timelinessKey='Y'";
								SqlRowSet sqlRowSet1 = jdbcTemplate.queryForRowSet(sql);
								while (sqlRowSet1.next()) {
									int timelinessCount = sqlRowSet1.getInt("timelinessCount");
									if (timelinessCount > 0) {
										return (long) -3;
									}
								}
							}
							sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
							jdbcTemplate.update(sql, columnValue, idColumn);
							return (long) 1;
						}
						return (long) -2;
					} else {
						return (long) -1;
					}
				}

			} else if (columnName.equals("endDate")) {
				sql = "select count(endDate) as endDateCount from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ") and endDate='Y' and idColumn!=" + idColumn;
				SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
				while (sqlRowSet.next()) {
					int endDateCount = sqlRowSet.getInt("endDateCount");
					if (endDateCount == 0) {
						if (format.equalsIgnoreCase("Date") || format.equalsIgnoreCase("date") || format.equalsIgnoreCase("datetime2") || format.equalsIgnoreCase("datetime")) {
							if (columnValue.equalsIgnoreCase("N")) {
								sql = "select count(timelinessKey) as timelinessCount from staging_listDataDefinition where idData = (select idData from staging_listDataDefinition where idColumn=" + idColumn + ") and timelinessKey='Y'";
								SqlRowSet sqlRowSet1 = jdbcTemplate.queryForRowSet(sql);
								while (sqlRowSet1.next()) {
									int timelinessCount = sqlRowSet1.getInt("timelinessCount");
									if (timelinessCount > 0) {
										return (long) -3;
									}
								}
							}
							sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
							jdbcTemplate.update(sql, columnValue, idColumn);
							return (long) 1;
						}
						return (long) -2;
					} else {
						return (long) -1;
					}
				}
			} else if (columnName.equals("isMasked")) {
				// Masking column is not allowed for following checks
				// DataDrift
				// Numerical stat
				// Record Anomaly
				// SubSegment
				String ldd_details = "select dataDrift,numericalStat,recordAnomaly,dgroup from staging_listDataDefinition where idColumn = "
						+ idColumn;
				Map<String, Object> colDetailMap = jdbcTemplate.queryForMap(ldd_details);
				String dataDrift = (String) colDetailMap.get("dataDrift");
				String numericalStat = (String) colDetailMap.get("numericalStat");
				String recordAnomaly = (String) colDetailMap.get("recordAnomaly");
				String dgroup = (String) colDetailMap.get("dgroup");

				if (columnValue.equalsIgnoreCase("Y") && ((dataDrift != null && dataDrift.equalsIgnoreCase("Y"))
						|| (numericalStat != null && numericalStat.equalsIgnoreCase("Y"))
						|| (recordAnomaly != null && recordAnomaly.equalsIgnoreCase("Y"))
						|| (dgroup != null && dgroup.equalsIgnoreCase("Y")))) {

					return 0l;
				} else {
					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				}
			} else if (columnName.equals("lengthCheck") || (columnName.equals("lengthValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {
					if (columnValue.equalsIgnoreCase("Y") && isMaxLengthCheckEnabled != null
							&& isMaxLengthCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for Length check, already MaxLengthCheck is enabled!!");
						return  (long)-4;
					} else if(columnName.equals("lengthValue") && isMaxLengthCheckEnabled.trim().equalsIgnoreCase("N")
							&& isLengthCheckEnabled.trim().equalsIgnoreCase("N")){
						LOG.debug("\n====> The column[" + columnName + "] is not enabled for Length check or MaxLengthCheck!!");
						return  (long)-6;
					} else {
						sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("maxLengthCheck") || (columnName.equals("lengthValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {
					if (columnValue.equalsIgnoreCase("Y") && isLengthCheckEnabled != null
							&& isLengthCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for MaxLengthCheck, already LengthCheck is enabled!!");
						return  (long)-5;
					} else if(columnName.equals("lengthValue") && isMaxLengthCheckEnabled.trim().equalsIgnoreCase("N")
							&& isLengthCheckEnabled.trim().equalsIgnoreCase("N")){
						LOG.debug("\n====> The column[" + columnName + "] is not enabled for Length check or MaxLengthCheck!!");
						return  (long) -6;
					}  else {
						sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("primaryKey") || (columnName.equals("primaryKeyValue"))) {

				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {
					if (columnValue.equalsIgnoreCase("Y") && isDuplicateKeyCheckEnabled != null
							&& isDuplicateKeyCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for Primary Key Check, already Duplicate Check is enabled!!");
						return 0l;
					} else {
						sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";

						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("dupkey") || (columnName.equals("dupkeyValue"))) {
				if (format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
						|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
						|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC")) {
					if (columnValue.equalsIgnoreCase("Y") && isPrimaryKeyCheckEnabled != null
							&& isPrimaryKeyCheckEnabled.trim().equalsIgnoreCase("Y")) {

						LOG.debug("\n====> The column[" + columnName
								+ "] is not eligible for Duplicate Key Check, already primary key Check is enabled!!");
						return 0l;
					} else {
						sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
						jdbcTemplate.update(sql, columnValue, idColumn);
						return (long) 1;
					}
				} else if (columnValue.equalsIgnoreCase("N")) {
					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				} else {
					return (long) -1;
				}
			} else if (columnName.equals("patterns")) {
				if (columnName.equals("patterns") && patternCheckEnabled.trim().equalsIgnoreCase("N")) {
					LOG.debug("\n====> The column[" + columnName + "] is not enabled for patternCheck!!");
					return (long) -7;
				} else {
					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";

					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				}
			} else if (columnName.equals("dateRule")) {

				if (format.toLowerCase().contains("date")) {

					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
					jdbcTemplate.update(sql, columnValue, idColumn);
					return (long) 1;
				} else {
					return (long) -1;
				}
				// Changes regarding numericalStat
				// Changes regarding recordAnomaly - Mamta 8/Aug/2022
			} else if ((columnName.equalsIgnoreCase("numericalStat")) || (columnName.equalsIgnoreCase("recordAnomaly"))) {

				if (columnValue.equalsIgnoreCase("Y") && (format.equalsIgnoreCase("NUMBER")
						|| format.equalsIgnoreCase("int") || format.equalsIgnoreCase("INTEGER")
						|| format.equalsIgnoreCase("NUMERIC") || format.equalsIgnoreCase("BIGINT")
						|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("FLOAT")
						|| format.equalsIgnoreCase("DOUBLE") || format.equalsIgnoreCase("DECIMAL"))) {

					sql = "update staging_listDataDefinition set " + columnName + "=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", idColumn);

				} else if (columnValue.equalsIgnoreCase("N")) {

					sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, idColumn);

				} else {
					return (long) -1;
				}
			}else {

				if (columnName.equalsIgnoreCase("datadrift") && columnValue.equalsIgnoreCase("Y")) {

					sql = "update staging_listDataDefinition set " + columnName
							+ "=?,stringStat=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", "Y", idColumn);

				} else if ((columnName.equalsIgnoreCase("stringStat") && columnValue.equalsIgnoreCase("Y"))
						|| (columnName.equalsIgnoreCase("nonNull") && columnValue.equalsIgnoreCase("Y"))) {
					//|| (columnName.equalsIgnoreCase("recordAnomaly") && columnValue.equalsIgnoreCase("Y"))) {

					sql = "update staging_listDataDefinition set " + columnName + "=?,KBE=? where idColumn=?";
					return (long) jdbcTemplate.update(sql, columnValue, "Y", idColumn);

				} else {

					// Query compatibility changes for both POSTGRES and MYSQL
					// For threshold columns
					List<String> threshold_columns_list = new ArrayList<String>();
					threshold_columns_list.add("nullcountthreshold");
					threshold_columns_list.add("numericalthreshold");
					threshold_columns_list.add("stringstatthreshold");
					threshold_columns_list.add("datadriftthreshold");
					threshold_columns_list.add("recordanomalythreshold");
					threshold_columns_list.add("outofnormstatthreshold");
					threshold_columns_list.add("lengthcheckthreshold");
					threshold_columns_list.add("baddatacheckthreshold");
					threshold_columns_list.add("patterncheckthreshold");

					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
							&& (threshold_columns_list.contains(columnName.toLowerCase()))) {
						double threshold_value = 0.0;
						if (columnValue != null) {
							threshold_value = Double.parseDouble(columnValue);
						}

						sql = "update staging_listDataDefinition set " + columnName + "=" + threshold_value + " where idColumn=?";
						return (long) jdbcTemplate.update(sql, idColumn);
					} else {
						sql = "update staging_listDataDefinition set " + columnName + "=? where idColumn=?";
						return (long) jdbcTemplate.update(sql, columnValue, idColumn);
					}

				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public String getSecretKeyForIdDataSchema(long idDataSchema) {
		try {
			String sql = "select secretKey from listDataSchema where idDataSchema=" + idDataSchema;
			String secretKey = jdbcTemplate.queryForObject(sql, String.class);

			if(secretKey!=null && !secretKey.trim().isEmpty()){
				// Decrypt it
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
				decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String decryptedText = decryptor.decrypt(secretKey);

				return decryptedText;
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Long getConnectionIdByDetails(String connectionType, String hostUri, String port, String schema,
			String userlogin, String password) {
		Long idDataSchema = null;
		try {
			String sql = "select idDataSchema,password from listDataSchema where Action='Yes' and schemaType=? and ipAddress=? and port=? and databaseSchema=? and username=? order by idDataSchema desc";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, connectionType, hostUri, port, schema, userlogin);

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					String db_password = sqlRowSet.getString("password");
					String decryptedPwd = "";
					try {
						StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
						decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
						decryptedPwd = decryptor.decrypt(db_password);
					} catch (Exception e) {
						LOG.error("exception "+e.getMessage());
					}

					if (decryptedPwd.trim().equalsIgnoreCase(password.trim())) {
						idDataSchema = sqlRowSet.getLong("idDataSchema");
						break;
					}
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idDataSchema;
	}

	@Override
	public Long getSchemaId(String schemaName, long projectId, int domainId) {
		Long idData = 0l;
		String sql = "SELECT idDataSchema FROM listDataSchema WHERE schemaName='" + schemaName + "'"
				+ " and domain_id=? and project_id=?";
		try {
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, domainId, projectId);
			while (queryForRowSet.next()) {
				idData = queryForRowSet.getLong(1);
			}
			return idData;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return idData;
		}
	}

	@Override
	public List<ListDataSchema> getListDataConnections(String projectIds, String fromDate, String toDate) {
		String sql = "SELECT t1.*,t2.projectName,t3.domainName from listDataSchema t1, project t2, domain t3 where "
				+ "t1.project_id = t2.idProject and t1.domain_id = t3.domainId and ";
		if (fromDate != null && !fromDate.trim().isEmpty() && toDate != null && !toDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "t1.createdAt >= '" + fromDate + "' and t1.createdAt <= ('" + toDate
						+ "'::DATE + '1 day'::INTERVAL) and t1.project_id in (  " + projectIds
						+ " ) order by t1.idDataSchema DESC";
			} else {
				sql = sql + "t1.createdAt >= '" + fromDate + "' and t1.createdAt <= DATE_ADD('" + toDate
						+ "', INTERVAL 1 DAY) and t1.project_id in (  " + projectIds
						+ " ) order by t1.idDataSchema DESC";
			}
		} else {
			sql = sql + "project_id in (  " + projectIds + " ) order by t1.idDataSchema DESC";
		}
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setGss_jaas(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setProjectName(rs.getString("projectName"));
				alistdataschema.setAction(rs.getString("Action"));
				alistdataschema.setDomainName(rs.getString("domainName"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				alistdataschema.setReadLatestPartition(rs.getString("readLatestPartition"));
				alistdataschema.setAlationIntegrationEnabled(rs.getString("alation_integration_enabled"));
				alistdataschema.setKmsAuthDisabled(rs.getString("kmsAuthDisabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setFolderPath(rs.getString("folderPath"));
				alistdataschema.setFileNamePattern(rs.getString("fileNamePattern"));
				alistdataschema.setFileEncrypted(rs.getString("fileEncrypted"));
				alistdataschema.setFileDataFormat(rs.getString("fileDataFormat"));
				alistdataschema.setHeaderPresent(rs.getString("headerPresent"));
				alistdataschema.setSingleFile(rs.getString("singleFile"));
				alistdataschema.setExtenalFileNamePattern(rs.getString("externalfileNamePattern"));
				alistdataschema.setExtenalFileName(rs.getString("externalfileName"));
				alistdataschema.setPartitionedFolders(rs.getString("partitionedFolders"));
				alistdataschema.setHeaderColumn(rs.getString("headerColumn"));
				alistdataschema.setLocalDirectoryColumnIndex(rs.getString("localDirectoryColumnIndex"));
				alistdataschema.setPatternColumn(rs.getString("patternColumn"));
				alistdataschema.setXsltFolderPath(rs.getString("xsltFolderPath"));
				alistdataschema.setHeaderFileDataFormat(rs.getString("headerFileDataFormat"));
				alistdataschema.setHeaderFileNamePattern(rs.getString("headerFileNamePattern"));
				alistdataschema.setHeaderFilePath(rs.getString("headerFilePath"));
				alistdataschema.setAccessKey(rs.getString("accessKey"));
				alistdataschema.setBucketName(rs.getString("bucketName"));
				alistdataschema.setSecretKey(rs.getString("secretKey"));
				alistdataschema.setJksPath(rs.getString("jksPath"));
				alistdataschema.setAzureClientId(rs.getString("azureClientId"));
				alistdataschema.setAzureClientSecret(rs.getString("azureClientSecret"));
				alistdataschema.setAzureTenantId(rs.getString("azureTenantId"));
				alistdataschema.setAzureServiceURI(rs.getString("azureServiceURI"));
				alistdataschema.setAzureFilePath(rs.getString("azureFilePath"));
				alistdataschema.setClusterPropertyCategory(rs.getString("cluster_property_category"));
				alistdataschema.setMultiFolderEnabled(rs.getString("multiFolderEnabled"));
				alistdataschema.setIncrementalDataReadEnabled(rs.getString("incremental_dataread_enabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setHttpPath(rs.getString("http_path"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}
	
	@Override
	public List<ListDataSchema> getListDataConnectionsbyDomain(String projectIds, String fromDate, String toDate, String domainId ) {
		String sql = "SELECT t1.*,cast(date_format(t1.createdAt, '%y-%m-%d') as date) as CreatedOn,"
				+ "cast(date_format(t1.updatedAt, '%y-%m-%d') as date) as UpdatedOn,"
				+ " t2.projectName,t3.domainName from listDataSchema t1, project t2, domain t3 where "
				+ "t1.project_id = t2.idProject and t1.domain_id = t3.domainId and ";

        if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
            sql = "SELECT t1.*,TO_CHAR(t1.createdAt, 'yyyy-MM-dd') as CreatedOn,TO_CHAR(t1.updatedAt, 'yyyy-MM-dd') as UpdatedOn,"
                    + " t2.projectName,t3.domainName from listDataSchema t1, project t2, domain t3 where "
                    + "t1.project_id = t2.idProject and t1.domain_id = t3.domainId and ";
        }

		if (fromDate != null && !fromDate.trim().isEmpty() && toDate != null && !toDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "t1.createdAt >= '" + fromDate + "' and t1.createdAt <= ('" + toDate
						+ "'::DATE + '1 day'::INTERVAL) and t1.project_id in (  " + projectIds
						+ " ) and t1.domain_id in ( " + domainId + ") order by t1.idDataSchema DESC";
			} else {
				sql = sql + "t1.createdAt >= '" + fromDate + "' and t1.createdAt <= DATE_ADD('" + toDate
						+ "', INTERVAL 1 DAY) and t1.project_id in (  " + projectIds
						+ " ) and t1.domain_id in ( " + domainId + ") order by t1.idDataSchema DESC";
			}
		} else {
			sql = sql + "project_id in (  " + projectIds + " ) and domain_id in ( " + domainId + " )order by t1.idDataSchema DESC";
		}
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setGss_jaas(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setProjectName(rs.getString("projectName"));
				alistdataschema.setAction(rs.getString("Action"));
				alistdataschema.setDomainName(rs.getString("domainName"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				alistdataschema.setReadLatestPartition(rs.getString("readLatestPartition"));
				alistdataschema.setAlationIntegrationEnabled(rs.getString("alation_integration_enabled"));
				alistdataschema.setKmsAuthDisabled(rs.getString("kmsAuthDisabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setFolderPath(rs.getString("folderPath"));
				alistdataschema.setFileNamePattern(rs.getString("fileNamePattern"));
				alistdataschema.setFileEncrypted(rs.getString("fileEncrypted"));
				alistdataschema.setFileDataFormat(rs.getString("fileDataFormat"));
				alistdataschema.setHeaderPresent(rs.getString("headerPresent"));
				alistdataschema.setClusterPolicyId(rs.getString("cluster_policy_id"));
				alistdataschema.setSingleFile(rs.getString("singleFile"));
				alistdataschema.setExtenalFileNamePattern(rs.getString("externalfileNamePattern"));
				alistdataschema.setExtenalFileName(rs.getString("externalfileName"));
				alistdataschema.setPartitionedFolders(rs.getString("partitionedFolders"));
				alistdataschema.setHeaderColumn(rs.getString("headerColumn"));
				alistdataschema.setLocalDirectoryColumnIndex(rs.getString("localDirectoryColumnIndex"));
				alistdataschema.setPatternColumn(rs.getString("patternColumn"));
				alistdataschema.setXsltFolderPath(rs.getString("xsltFolderPath"));
				alistdataschema.setHeaderFileDataFormat(rs.getString("headerFileDataFormat"));
				alistdataschema.setHeaderFileNamePattern(rs.getString("headerFileNamePattern"));
				alistdataschema.setHeaderFilePath(rs.getString("headerFilePath"));
				alistdataschema.setAccessKey(rs.getString("accessKey"));
				alistdataschema.setBucketName(rs.getString("bucketName"));
				alistdataschema.setSecretKey(rs.getString("secretKey"));
				alistdataschema.setJksPath(rs.getString("jksPath"));
				alistdataschema.setAzureClientId(rs.getString("azureClientId"));
				alistdataschema.setAzureClientSecret(rs.getString("azureClientSecret"));
				alistdataschema.setAzureTenantId(rs.getString("azureTenantId"));
				alistdataschema.setAzureServiceURI(rs.getString("azureServiceURI"));
				alistdataschema.setAzureFilePath(rs.getString("azureFilePath"));
				alistdataschema.setClusterPropertyCategory(rs.getString("cluster_property_category"));
				alistdataschema.setMultiFolderEnabled(rs.getString("multiFolderEnabled"));
				alistdataschema.setIncrementalDataReadEnabled(rs.getString("incremental_dataread_enabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setHttpPath(rs.getString("http_path"));
				alistdataschema.setAzureAuthenticationType(rs.getString("azure_authentication_type"));
				alistdataschema.setMaxFolderDepth(rs.getInt("maxFolderDepth"));
				alistdataschema.setCreatedAt(rs.getDate("CreatedOn"));
				alistdataschema.setUpdatedAt(rs.getDate("UpdatedOn"));
				alistdataschema.setCreatedAtStr(rs.getString("CreatedOn"));
				alistdataschema.setUpdatedAtStr(rs.getString("UpdatedOn"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	@Override
	public boolean updateClusterPropertyCategoryByIdDataSchema(long idDataSchema, String clusterPropertyCategory) {
		boolean result = false;
		try {
			String sql = "update listDataSchema set cluster_property_category=? where idDataSchema=?";
			LOG.debug("\n====>idDataSchema="+idDataSchema+" and cluster_property_category="+clusterPropertyCategory);
			LOG.debug(sql);
			jdbcTemplate.update(sql, clusterPropertyCategory, idDataSchema);
			result = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public List<ListDataSchema> readDatabricksFileMonitoringListDataSchema() {

		String sql = "select idDataSchema,databaseSchema,schemaName,schemaType,ipAddress,username,password," +
				"port,folderPath,http_path from listDataSchema where schemaType='DatabricksDeltaLake' and enableFileMonitoring='Y' and Action='Yes'";

		System.out.println("sql:"+sql);

		List<ListDataSchema> listDataSchemaList = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {

			public ListDataSchema mapRow(ResultSet result, int rowNum) throws SQLException {
				ListDataSchema listDataSchema = new ListDataSchema();
				listDataSchema.setIdDataSchema(result.getLong("idDataSchema"));
				listDataSchema.setSchemaName(result.getString("schemaName"));
				listDataSchema.setIpAddress(result.getString("ipAddress"));
				listDataSchema.setDatabaseSchema(result.getString("databaseSchema"));
				listDataSchema.setUsername(result.getString("username"));
				listDataSchema.setPassword(result.getString("password"));
				listDataSchema.setPort(result.getString("port"));
				listDataSchema.setHttpPath(result.getString("http_path"));
				return listDataSchema;
			}
		});

		return listDataSchemaList;
	}
	@Override
	public boolean isLengthCheckEnabled(long idColumn){
		boolean result = false;
		try {
			String isLengthCheckEnabled="";
			String sql="select lengthCheck from staging_listDataDefinition where idColumn ="+idColumn;
			SqlRowSet idColumnRowSet = jdbcTemplate.queryForRowSet(sql);

			if (idColumnRowSet.next()) {
				isLengthCheckEnabled = idColumnRowSet.getString("lengthCheck");
				if(isLengthCheckEnabled.equalsIgnoreCase("Y")){
					result=true;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public boolean isMaxLengthCheckEnabled(long idColumn){
		boolean result = false;
		try {
			String isMaxLengthCheckEnabled="";
			String sql="select maxLengthCheck from staging_listDataDefinition where idColumn ="+idColumn;
			SqlRowSet idColumnRowSet = jdbcTemplate.queryForRowSet(sql);

			if (idColumnRowSet.next()) {
				isMaxLengthCheckEnabled = idColumnRowSet.getString("maxLengthCheck");
				if(isMaxLengthCheckEnabled.equalsIgnoreCase("Y")){
					result=true;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JSONArray checkDuplicateSchemaConnection(ListDataSchema listDataSchema){
		JSONArray connectionDetails = new JSONArray();
		try{
			String schemaType= listDataSchema.getSchemaType();
			String sql="";
			SqlRowSet sqlRowSet=null;

			if (schemaType.equalsIgnoreCase("oracle")||
					schemaType.equalsIgnoreCase("MSSQL")||
					schemaType.equalsIgnoreCase("AzureSynapseMSSQL")||
					schemaType.equalsIgnoreCase("Vertica")||
					schemaType.equalsIgnoreCase("Hive")||
					schemaType.equalsIgnoreCase("Amazon Redshift")||
					schemaType.equalsIgnoreCase("Oracle RAC")||
					schemaType.equalsIgnoreCase("MySQL")||
					schemaType.equalsIgnoreCase("MapR DB")||
					schemaType.equalsIgnoreCase("Hive knox")||
					schemaType.equalsIgnoreCase("SnowFlake")||
					schemaType.equalsIgnoreCase("AzureCosmosDB")||
					schemaType.equalsIgnoreCase("DatabricksDeltaLake")||
					schemaType.equalsIgnoreCase("MapR Hive")||
					schemaType.equalsIgnoreCase("MSSQLActiveDirectory")||
					schemaType.equalsIgnoreCase("MSSQLAD")||
					schemaType.equalsIgnoreCase("Postgres")||
					schemaType.equalsIgnoreCase("Teradata")||
					schemaType.equalsIgnoreCase("Hive Kerberos")) {

				sql="select idDataSchema,schemaName,createdAt from listDataSchema where domain_id=? and project_id=? and schemaType=? and ipAddress=? and databaseSchema=? and port=? order by idDataSchema desc limit 1";

				System.out.println("sql:"+sql);
				sqlRowSet = jdbcTemplate.queryForRowSet(sql,listDataSchema.getDomainId(),listDataSchema.getProjectId(),schemaType,listDataSchema.getIpAddress(),listDataSchema.getDatabaseSchema(), listDataSchema.getPort());

			}else if(schemaType.equalsIgnoreCase("S3 Batch")||
					schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")||
					schemaType.equalsIgnoreCase("S3 IAMRole Batch")){

				sql="select idDataSchema,schemaName,createdAt from listDataSchema where domain_id=? and project_id=? and schemaType=? and folderPath=? and bucketName=? order by idDataSchema desc limit 1";

				System.out.println("sql:"+sql);
				sqlRowSet = jdbcTemplate.queryForRowSet(sql,listDataSchema.getDomainId(),listDataSchema.getProjectId(),schemaType,listDataSchema.getFolderPath(),listDataSchema.getBucketName());

			}else if(schemaType.equalsIgnoreCase("FileSystem Batch")){

				sql="select idDataSchema,schemaName,createdAt from listDataSchema where domain_id=? and project_id=? and schemaType=? and folderPath=? order by idDataSchema desc limit 1";

				System.out.println("sql:"+sql);
				sqlRowSet = jdbcTemplate.queryForRowSet(sql,listDataSchema.getDomainId(),listDataSchema.getProjectId(),schemaType,listDataSchema.getFolderPath());

			}else if(schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")){
				sql="select idDataSchema,schemaName,createdAt from listDataSchema where domain_id=? and project_id=? and schemaType=? and azureServiceURI=? and azureFilePath=? order by idDataSchema desc limit 1";

				System.out.println("sql:"+sql);
				sqlRowSet = jdbcTemplate.queryForRowSet(sql,listDataSchema.getDomainId(),listDataSchema.getProjectId(),schemaType,listDataSchema.getAzureServiceURI(),listDataSchema.getAzureFilePath());
			}

			if(sqlRowSet!=null){

				while(sqlRowSet.next()){
					JSONObject connectionDetailsObj= new JSONObject();
					connectionDetailsObj.put("schemaName",sqlRowSet.getString("schemaName"));
					connectionDetailsObj.put("createdAt",sqlRowSet.getString("createdAt"));
					connectionDetailsObj.put("idDataSchema",sqlRowSet.getLong("idDataSchema"));

					connectionDetails.put(connectionDetailsObj);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return connectionDetails;
	}

}