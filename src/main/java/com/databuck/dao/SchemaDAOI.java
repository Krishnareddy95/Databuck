package com.databuck.dao;

import java.util.HashMap;
import java.util.List;

import com.databuck.bean.DataConnection;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import org.json.JSONArray;

public interface SchemaDAOI {

	Long saveDataIntoListDataSchema(String uri, String database, String username, String password, String port,
			String schemaName, String schemaType, String domain, String serviceName, String krb5conf,
			String autoGenerateId, String suffix, String prefix, String hivejdbchost, String hivejdbcport,
			String sslEnb, String sslTrustStorePath, String trustPassword, String gatewayPath, String jksPath,
			String zookeeperUrl, String folderPath, String fileNamePattern, String fileDataFormat, String headerPresent,
			String headerFilePath, String headerFileNamePattern, String headerFileDataFormat, Long projectid,
			String createdByUser, String accessKey, String secretKey, String bucketName, String bigQueryprojectName,
			String privatekeyId, String privatekey, String clientId, String clientEmail, String datasetName,
			String azureClientId, String azureClientSecret, String azureTenantId, String azureServiceURI,
			String azureFilePath, String partitionedFolders, String enableFileMonitoring, String multiPattern,
			int startingUniqueCharCount, int endingUniqueCharCount, int maxFolderDepth, String fileEncrypted,
			Integer domainId, String singleFile, String externalFileNamePatternId, String externalFileName,
			String patternColumn, String headerColumn,String localDirectoryColumnIndex,String xsltFolderPath,
			String kmsAuthDisabled, String readLatestPartition,String alation_integration_enabled,
			String incrementalDataReadEnabled,String clusterPropertyCategory, String multiFolderEnabled, 
			String pushDownQueryEnabled, String httpPath,String clusterPolicyId,String azureAuthenticationType);

	Object[] readTablesFromVertica(String uri, String database, String username, String password, String port,
			Long idDataSchema, Long idUser);

	public long insertintolds(String tableName, Long idDataSchema, Long idUser);

	public String duplicateSchemaName(String schemaName,long projectId, int domainId);

	public List<ListDataSchema> readdatafromlistdataschema(Long idDataSchema);

	public List<Long> insertDataInListApplications(List tableList, Long[] listDataSourcesIds, String dataBase,
			String strDate, Long idUser);

	public Long[] insertListDFSetRule(List<Long> listApplicationsIdList);

	void insertIntoTranRule(List<Long> listApplicationsIdList);

	public void insertListDFSetComparisonRule(Long[] listDfSetRuleidDfDfSetArray, Long[] listDataSourcesIds);

	Long updateDataIntoListDataSchema(String uri, String database, String username, String password, String port,
			String schemaName, String schemaType, String domain, long idDataSchema, String serviceName, String krb5conf,
			String hivejdbchost, String hivejdbcport, String sslTrustStorePath, String trustPassword,
			String gatewayPath, String jksPath, String zookeeperUrl, String folderPath, String fileNamePattern,
			String fileDataFormat, String headerPresent, String headerFilePath, String headerFileNamePattern,
			String headerFileDataFormat, String accessKey, String secretKey, String bucketName,
			String bigQueryprojectName, String privatekeyId, String privatekey, String clientId, String clientEmail,
			String datasetName, String azureClientId, String azureClientSecret, String azureTenantId,
			String azureServiceURI, String azureFilePath, String partitionedFolders, String multiPattern,
			int startingUniqueCharCount, int endingUniqueCharCount, int maxFolderDepth, String fileEncrypted,
			String singleFile,String externalFileNamePatternId, String externalFileName, String patternColumn, 
			String headerColumn, String localDirectoryColumnIndex,String xsltFolderPath, String kmsAuthDisabled,
			String readLatestPartition,String alation_integration_enabled, String enableFileMonitoring,
		    String incrementalDataReadEnabled, String multiFolderEnabled,String sslEnabled,String pushDownQueryEnabled,String httpPath,String clusterPropertyCategory,String azureAuthenticationType, String clusterPolicyId);

	String getPasswordForIdDataSchema(long idDataSchema);
	List<Long> getIdAppFromFmConnectionDetails(long idDataSchema);
	Long updateDataIntoListDataDefinition(Long idColumn, String columnName, String columnValue);
	void updateKBEIntoListDataDefinition(Long idColumn, String columnName, String columnValue);

	String getStatusForHiveKerberos(Long idDataSchema);

	void deleteEntryFromListDataSchemaAndHiveSource(Long idDataSchema);

	public void processAutoGenerateTemplate_oracleRAC(String uri, String database, String username, String password,
			String port, String schemaName, String schemaType, String domain, String serviceName, String krb5conf,
			String autoGenerateId, long idUser, Long idDataSchema, Long projectId, Integer domainId);

	void insertDataIntoHiveSourceForAutoGenerateStatus(Long idDataSchema);

	Long getStatusForAutoDT();

	Long insertDataIntoListBatchSchema(String schemaBatchName, String schemaType, String originalFilename);

	void updateidDataSchemasInListBatchSchema(Long idBatchSchema, String idDataSchemas);

	public ListDataSchema getSchemaDetailsForConnectionUtil(Long idDataSchema);

	Long updateIntoListGlobalThreshold(String idColumn, String columnName, Double columnThresholdValue);

	String inserIntoListGlobalThreshold(int domainId, String columnName);

	public List<String> getSchemaNames(Long projectId);

	public boolean updateEnableFileMonitoringFlagForSchema(Long idDataSchema, String status);

	public List<Long> getFileMonitoringValidationsForSchema(Long idDataSchema);

	public boolean activateOrDeactivateFMValidations(List<Long> fmAppIdList, String activeFlagStatus);

	public HashMap<String, Long> getDomainProjectFromSchema(long nIdDataSchema);

	public long updateDataIntoStagingListDataDefinition(long idColumn, String columnName, String columnValue);

	public void updateKBEIntoStagingListDataDefinition(Long idColumn, String columnName, String columnValue);

	public Long getConnectionIdForFMValidation(long idApp);

	public String getSecretKeyForIdDataSchema(long idDataSchema);

	public Long getConnectionIdByDetails(String connectionType, String hostUri, String port, String schema, String userlogin,
			String password);
	
	Long getSchemaId(String schemaName,long projectId, int domainId);

	long saveConnection(DataConnection connection);
	
	public List<ListDataSchema> getListDataConnections(String projectIds, String fromDate, String toDate);
	
	public List<ListDataSchema> getListDataConnectionsbyDomain(String projectIds, String fromDate, String toDate, String domainId);

	public boolean updateClusterPropertyCategoryByIdDataSchema(long idDataSchema, String clusterPropertyCategory);

	public List<ListDataSchema> readDatabricksFileMonitoringListDataSchema();

	public boolean isLengthCheckEnabled(long idColumn);

	public boolean isMaxLengthCheckEnabled(long idColumn);

	public List<Long> getValidationsForFMConnection(long idDataSchema);

	public JSONArray checkDuplicateSchemaConnection(ListDataSchema listDataSchema);

}