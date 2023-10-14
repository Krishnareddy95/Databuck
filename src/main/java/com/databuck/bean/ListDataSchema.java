package com.databuck.bean;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class ListDataSchema {
	
	@ApiModelProperty(notes = "Id Data Schema")
	private long idDataSchema;
	@ApiModelProperty(notes = "Ip Address")
	private String ipAddress;
	@ApiModelProperty(notes = "Database Schema")
	private String databaseSchema;
	@ApiModelProperty(notes = "Username")
	private String username;
	@ApiModelProperty(notes = "Password")
	private String password;
	@ApiModelProperty(notes = "Port")
	private String port;
	@ApiModelProperty(notes = "Created At")
	private Date createdAt;
	@ApiModelProperty(notes = "Updated At")
	private Date updatedAt;
	@ApiModelProperty(notes = "Created At String")
	private String createdAtStr;
	@ApiModelProperty(notes = "Updated At String")
	private String updatedAtStr;
	@ApiModelProperty(notes = "Schema Name")
	private String schemaName;
	@ApiModelProperty(notes = "Domain")
	private String domain;
	@ApiModelProperty(notes = "Key Tab")
	private String keytab;
	@ApiModelProperty(notes = "KRB Configuration")
	private String krb5conf;
	@ApiModelProperty(notes = "GSS JAAS")
	private String gss_jaas;
	@ApiModelProperty(notes = "Created By")
	private Long createdBy;
	@ApiModelProperty(notes = "Updated By")
	private Long updatedBy;
	@ApiModelProperty(notes = "Service Na,e")
	private String serviceName;
	@ApiModelProperty(notes = "Hive JDBC Host")
	private String hivejdbchost;
	@ApiModelProperty(notes = "Hive JDBC Port")
	private String hivejdbcport;
	@ApiModelProperty(notes = "SSL ENB")
	private String sslEnb;
	@ApiModelProperty(notes = "SSL Trust Store Path")
	private String sslTrustStorePath;
	@ApiModelProperty(notes = "Trust Password")
	private String trustPassword;
	@ApiModelProperty(notes = "Suffixes")
	private String suffixes;
	@ApiModelProperty(notes = "Prefixes")
	private String prefixes;
	@ApiModelProperty(notes = "Auto Generate")
	private String autoGenerate;
	@ApiModelProperty(notes = "Project Id")
	private Long projectId;

	// domainId and domainName properties hold the Id and Name of the domain
	// under which this connection is present
	@ApiModelProperty(notes = "Domain Id")
	private Integer domainId;
	@ApiModelProperty(notes = "Domain Name")
	private String domainName;

	// Adding fields for Hive(Knox)
	@ApiModelProperty(notes = "Gateway Path")
	private String gatewayPath;
	@ApiModelProperty(notes = "JKS Path")
	private String jksPath;
	@ApiModelProperty(notes = "Zookeeper Url")
	private String zookeeperUrl;
	@ApiModelProperty(notes = "Created By User")
	private String createdByUser;

	// Adding fields for FileSystem Batch
	@ApiModelProperty(notes = "Folder Path")
	private String folderPath;
	@ApiModelProperty(notes = "File Name Pattern")
	private String fileNamePattern;
	@ApiModelProperty(notes = "File Data Format")
	private String fileDataFormat;
	@ApiModelProperty(notes = "Header Present")
	private String headerPresent;
	@ApiModelProperty(notes = "Header File Path")
	private String headerFilePath;
	@ApiModelProperty(notes = "Header File Name Pattern")
	private String headerFileNamePattern;
	@ApiModelProperty(notes = "Header File Data Format")
	private String headerFileDataFormat;

	// Adding fields for S3 Batch
	@ApiModelProperty(notes = "Access Key")
	private String accessKey;
	@ApiModelProperty(notes = "Secret Key")
	private String secretKey;
	@ApiModelProperty(notes = "Bucket Name")
	private String bucketName;
	@ApiModelProperty(notes = "Action")
	private String action;
	@ApiModelProperty(notes = "Id SORs")
	private Long idSORs;

	// Adding fields for Big Query Batch
	@ApiModelProperty(notes = "Big Query Project Name")
	private String bigQueryProjectName;
	@ApiModelProperty(notes = "Private Key Id")
	private String privatekeyId;
	@ApiModelProperty(notes = "Private Key")
	private String privatekey;
	@ApiModelProperty(notes = "Client Id")
	private String clientId;
	@ApiModelProperty(notes = "Client Email")
	private String clientEmail;
	@ApiModelProperty(notes = "Dataset Name")
	private String datasetName;

	// Adding fields for Azure Data Lake gen1
	@ApiModelProperty(notes = "Azure Client Id")
	private String azureClientId;
	@ApiModelProperty(notes = "Azure Client Secret")
	private String azureClientSecret;
	@ApiModelProperty(notes = "Azure Tenant Id")
	private String azureTenantId;
	@ApiModelProperty(notes = "Azure Service URI")
	private String azureServiceURI;
	@ApiModelProperty(notes = "Azure File Path")
	private String azureFilePath;

	// Adding fields for S3 IAMRole - partitioned folders
	@ApiModelProperty(notes = "Partitioned Folders")
	private String partitionedFolders;
	@ApiModelProperty(notes = "Enable File Monitoring")
	private String enableFileMonitoring;
	@ApiModelProperty(notes = "Multi Folder Enabled")
	private String multiFolderEnabled;

	// Adding fields for S3 IAMRole - MultiPattern
	@ApiModelProperty(notes = "Multi Pattern")
	private String multiPattern;
	@ApiModelProperty(notes = "Starting Unique Char Count")
	private int startingUniqueCharCount;
	@ApiModelProperty(notes = "Ending Unique Char Count")
	private int endingUniqueCharCount;
	@ApiModelProperty(notes = "Max Folder Depth")
	private int maxFolderDepth;
	@ApiModelProperty(notes = "File Encrypted")
	private String fileEncrypted;
	@ApiModelProperty(notes = "Project Name")
	private String projectName;
	@ApiModelProperty(notes = "Single File")
	private String singleFile;

	// Adding fields for External File
	@ApiModelProperty(notes = "External File Name Pattern")
	private String extenalFileNamePattern;
	@ApiModelProperty(notes = "External File Name")
	private String extenalFileName;
	@ApiModelProperty(notes = "Pattern Column")
	private String patternColumn;
	@ApiModelProperty(notes = "Header Column")
	private String headerColumn;
	@ApiModelProperty(notes = "Local Directory Column Index")
	private String localDirectoryColumnIndex;
	@ApiModelProperty(notes = "XSLT Folder Path")
	private String xsltFolderPath;

	// Adding field to enable/disable KMS Auth for a connection
	@ApiModelProperty(notes = "KMS Auth Disabled")
	private String kmsAuthDisabled;

	// Adding field readLatestPartition
	@ApiModelProperty(notes = "Read Latest Patition")
	private String readLatestPartition;

	// Adding field alation_integration_enabled
	@ApiModelProperty(notes = "Alation Integration Enabled")
	private String alation_integration_enabled;
	@ApiModelProperty(notes = "Incremental Data Read Enabled")
	private String incrementalDataReadEnabled;
	@ApiModelProperty(notes = "Alation Integration Enabled")
	private String alationIntegrationEnabled;

	//Adding fileds for remote machine connections

	// Adding field to identify cluster
	@ApiModelProperty(notes = "Cluster Property Category")
	private String clusterPropertyCategory;

	@ApiModelProperty(notes = "Push Down Query Enabled")
	private String pushDownQueryEnabled;

	@ApiModelProperty(notes = "Http Path")
	private String httpPath;
	
	@ApiModelProperty(notes = "Azure Authentication Type")
	private String azureAuthenticationType;
	
	@ApiModelProperty(notes = "Cluster Policy Id")
	private String clusterPolicyId;

	public String getHttpPath() {
		return httpPath;
	}

	public String getClusterPolicyId() {
		return clusterPolicyId;
	}

	public void setClusterPolicyId(String clusterPolicyId) {
		this.clusterPolicyId = clusterPolicyId;
	}

	public void setHttpPath(String httpPath) {
		this.httpPath = httpPath;
	}

	public String getPushDownQueryEnabled() {
		return pushDownQueryEnabled;
	}

	public void setPushDownQueryEnabled(String pushDownQueryEnabled) {
		this.pushDownQueryEnabled = pushDownQueryEnabled;
	}

	public String getClusterPropertyCategory() {
		return clusterPropertyCategory;
	}

	public void setClusterPropertyCategory(String clusterPropertyCategory) {
		this.clusterPropertyCategory = clusterPropertyCategory;
	}

	public String getAlationIntegrationEnabled() {
		return alationIntegrationEnabled;
	}

	public void setAlationIntegrationEnabled(String alationIntegrationEnabled) {
		this.alationIntegrationEnabled = alationIntegrationEnabled;
	}

	public String getAlation_integration_enabled() {
		return alation_integration_enabled;
	}

	public void setAlation_integration_enabled(String alation_integration_enabled) {
		this.alation_integration_enabled = alation_integration_enabled;
	}

	public String getReadLatestPartition() {
		return readLatestPartition;
	}

	public void setReadLatestPartition(String readLatestPartition) {
		this.readLatestPartition = readLatestPartition;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getBigQueryProjectName() {
		return bigQueryProjectName;
	}

	public void setBigQueryProjectName(String bigQueryProjectName) {
		this.bigQueryProjectName = bigQueryProjectName;
	}

	public String getPrivatekeyId() {
		return privatekeyId;
	}

	public void setPrivatekeyId(String privatekeyId) {
		this.privatekeyId = privatekeyId;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getIdSORs() {
		return idSORs;
	}

	public void setIdSORs(Long idSORs) {
		this.idSORs = idSORs;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getSslTrustStorePath() {
		return sslTrustStorePath;
	}

	public void setSslTrustStorePath(String sslTrustStorePath) {
		this.sslTrustStorePath = sslTrustStorePath;
	}

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		this.trustPassword = trustPassword;
	}

	public String getGatewayPath() {
		return gatewayPath;
	}

	public void setGatewayPath(String gatewayPath) {
		this.gatewayPath = gatewayPath;
	}

	public String getJksPath() {
		return jksPath;
	}

	public void setJksPath(String jksPath) {
		this.jksPath = jksPath;
	}

	public String getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(String suffixes) {
		this.suffixes = suffixes;
	}

	public String getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(String prefixes) {
		this.prefixes = prefixes;
	}

	public String getAutoGenerate() {
		return autoGenerate;
	}

	public void setAutoGenerate(String autoGenerate) {
		this.autoGenerate = autoGenerate;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getGss_jaas() {
		return gss_jaas;
	}

	public void setGss_jaas(String gss_jaas) {
		this.gss_jaas = gss_jaas;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}

	private String schemaType;

	public long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getKeytab() {
		return keytab;
	}

	public void setKeytab(String keytab) {
		this.keytab = keytab;
	}

	public String getKrb5conf() {
		return krb5conf;
	}

	public void setKrb5conf(String krb5conf) {
		this.krb5conf = krb5conf;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getHivejdbchost() {
		return hivejdbchost;
	}

	public void setHivejdbchost(String hivejdbchost) {
		this.hivejdbchost = hivejdbchost;
	}

	public String getHivejdbcport() {
		return hivejdbcport;
	}

	public void setHivejdbcport(String hivejdbcport) {
		this.hivejdbcport = hivejdbcport;
	}

	public String getSslEnb() {
		return sslEnb;
	}

	public void setSslEnb(String sslEnb) {
		this.sslEnb = sslEnb;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public String getFileDataFormat() {
		return fileDataFormat;
	}

	public void setFileDataFormat(String fileDataFormat) {
		this.fileDataFormat = fileDataFormat;
	}

	public String getHeaderPresent() {
		return headerPresent;
	}

	public void setHeaderPresent(String headerPresent) {
		this.headerPresent = headerPresent;
	}

	public String getHeaderFilePath() {
		return headerFilePath;
	}

	public void setHeaderFilePath(String headerFilePath) {
		this.headerFilePath = headerFilePath;
	}

	public String getHeaderFileNamePattern() {
		return headerFileNamePattern;
	}

	public void setHeaderFileNamePattern(String headerFileNamePattern) {
		this.headerFileNamePattern = headerFileNamePattern;
	}

	public String getHeaderFileDataFormat() {
		return headerFileDataFormat;
	}

	public void setHeaderFileDataFormat(String headerFileDataFormat) {
		this.headerFileDataFormat = headerFileDataFormat;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAzureClientId() {
		return azureClientId;
	}

	public void setAzureClientId(String azureClientId) {
		this.azureClientId = azureClientId;
	}

	public String getAzureClientSecret() {
		return azureClientSecret;
	}

	public void setAzureClientSecret(String azureClientSecret) {
		this.azureClientSecret = azureClientSecret;
	}

	public String getAzureTenantId() {
		return azureTenantId;
	}

	public void setAzureTenantId(String azureTenantId) {
		this.azureTenantId = azureTenantId;
	}

	public String getAzureServiceURI() {
		return azureServiceURI;
	}

	public void setAzureServiceURI(String azureServiceURI) {
		this.azureServiceURI = azureServiceURI;
	}

	public String getAzureFilePath() {
		return azureFilePath;
	}

	public void setAzureFilePath(String azureFilePath) {
		this.azureFilePath = azureFilePath;
	}

	public String getPartitionedFolders() {
		return partitionedFolders;
	}

	public void setPartitionedFolders(String partitionedFolders) {
		this.partitionedFolders = partitionedFolders;
	}

	public String getEnableFileMonitoring() {
		return enableFileMonitoring;
	}

	public void setEnableFileMonitoring(String enableFileMonitoring) {
		this.enableFileMonitoring = enableFileMonitoring;
	}

	public String getMultiPattern() {
		return multiPattern;
	}

	public void setMultiPattern(String multiPattern) {
		this.multiPattern = multiPattern;
	}

	public int getStartingUniqueCharCount() {
		return startingUniqueCharCount;
	}

	public void setStartingUniqueCharCount(int startingUniqueCharCount) {
		this.startingUniqueCharCount = startingUniqueCharCount;
	}

	public int getEndingUniqueCharCount() {
		return endingUniqueCharCount;
	}

	public void setEndingUniqueCharCount(int endingUniqueCharCount) {
		this.endingUniqueCharCount = endingUniqueCharCount;
	}

	public int getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public void setMaxFolderDepth(int maxFolderDepth) {
		this.maxFolderDepth = maxFolderDepth;
	}

	public String getFileEncrypted() {
		return fileEncrypted;
	}

	public void setFileEncrypted(String fileEncrypted) {
		this.fileEncrypted = fileEncrypted;
	}

	public String getSingleFile() {
		return singleFile;
	}

	public void setSingleFile(String singleFile) {
		this.singleFile = singleFile;
	}

	public String getExtenalFileNamePattern() {
		return extenalFileNamePattern;
	}

	public void setExtenalFileNamePattern(String extenalFileNamePattern) {
		this.extenalFileNamePattern = extenalFileNamePattern;
	}

	public String getExtenalFileName() {
		return extenalFileName;
	}

	public void setExtenalFileName(String extenalFileName) {
		this.extenalFileName = extenalFileName;
	}

	public String getPatternColumn() {
		return patternColumn;
	}

	public void setPatternColumn(String patternColumn) {
		this.patternColumn = patternColumn;
	}

	public String getHeaderColumn() {
		return headerColumn;
	}

	public void setHeaderColumn(String headerColumn) {
		this.headerColumn = headerColumn;
	}

	public String getLocalDirectoryColumnIndex() {
		return localDirectoryColumnIndex;
	}

	public void setLocalDirectoryColumnIndex(String localDirectoryColumnIndex) {
		this.localDirectoryColumnIndex = localDirectoryColumnIndex;
	}

	public String getXsltFolderPath() {
		return xsltFolderPath;
	}

	public void setXsltFolderPath(String xsltFolderPath) {
		this.xsltFolderPath = xsltFolderPath;
	}

	public String getKmsAuthDisabled() {
		return kmsAuthDisabled;
	}

	public void setKmsAuthDisabled(String kmsAuthDisabled) {
		this.kmsAuthDisabled = kmsAuthDisabled;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getIncrementalDataReadEnabled() {
		return incrementalDataReadEnabled;
	}

	public void setIncrementalDataReadEnabled(String incrementalDataReadEnabled) {
		this.incrementalDataReadEnabled = incrementalDataReadEnabled;
	}

	public String getMultiFolderEnabled() {
		return multiFolderEnabled;
	}

	public void setMultiFolderEnabled(String multiFolderEnabled) {
		this.multiFolderEnabled = multiFolderEnabled;
	}

	public String getAzureAuthenticationType() {
		return azureAuthenticationType;
	}

	public void setAzureAuthenticationType(String azureAuthenticationType) {
		this.azureAuthenticationType = azureAuthenticationType;
	}

	public String getCreatedAtStr() {
		return createdAtStr;
	}

	public void setCreatedAtStr(String createdAtStr) {
		this.createdAtStr = createdAtStr;
	}

	public String getUpdatedAtStr() {
		return updatedAtStr;
	}

	public void setUpdatedAtStr(String updatedAtStr) {
		this.updatedAtStr = updatedAtStr;
	}
	
	
	
}