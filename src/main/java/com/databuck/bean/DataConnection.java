package com.databuck.bean;

public class DataConnection {
	private String schemaName = "";
	private String schemaType = "";
	private String uri = "";
	private String database = "";
	private String username = "";
	private String password = "";
	private String port = "";
	private String domain = "";
	private String serviceName = "";
	private String krb5conf = "";
	private String autoGenerateId = "";
	private String hivejdbchost = "";
	private String hivejdbcport = "";
	private String suffix = "";
	private String prefix = "";
	private String sslEnb = "";
	private String sslTrustStorePath = "";
	private String trustPassword = "";
	private String gatewayPath = "";
	private String jksPath = "";
	private String zookeeperUrl = "";
	private String folderPath = "";
	private String fileNamePattern = "";
	private String fileDataFormat = "";
	private String headerPresent = "";
	private String partitionedFolders = "N";
	private String headerFilePath = "";
	private String headerFileNamePattern = "";
	private String headerFileDataFormat = "";
	private String accessKey = "";
	private String secretKey = "";
	private String bucketName = "";
	private String bigQueryProjectName = "";
	private String privatekeyId = "";
	private String privatekey = "";
	private String clientId = "";
	private String clientEmail = "";
	private String datasetName = "";
	private String azureClientId = "";
	private String azureClientSecret = "";
	private String azureTenantId = "";
	private String azureServiceURI = "";
	private String azureFilePath = "";
	private String enableFileMonitoring = "N";
	private String multiPattern = "N";
	private Integer startingUniqueCharCount = 0;
	private Integer endingUniqueCharCount = 0;
	private Integer maxFolderDepth = 0;
	private String fileEncrypted = "N";
	private String singleFile = "N";
	private String externalFileNamePatternId = "N";
	private String externalFileName = "";
	private String patternColumn = "";
	private String headerColumn = "";
	private String localDirectoryColumnIndex = "";
	private String xsltFolderPath = "";
	private String kmsAuthDisabled = "";
	private String readLatestPartition = "N";
	private String alationIntegrationEnabled = "N";
	private String incrementalDataReadEnabled = "N";
	private String createdBy = "";
	private Integer domainId = 0;
	private Long projectId = 0l;

	private String pushDownQueryEnabled = "";

	public String getPushDownQueryEnabled() {
		return pushDownQueryEnabled;
	}

	public void setPushDownQueryEnabled(String pushDownQueryEnabled) {
		this.pushDownQueryEnabled = pushDownQueryEnabled;
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getKrb5conf() {
		return krb5conf;
	}

	public void setKrb5conf(String krb5conf) {
		this.krb5conf = krb5conf;
	}

	public String getAutoGenerateId() {
		return autoGenerateId;
	}

	public void setAutoGenerateId(String autoGenerateId) {
		this.autoGenerateId = autoGenerateId;
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

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSslEnb() {
		return sslEnb;
	}

	public void setSslEnb(String sslEnb) {
		this.sslEnb = sslEnb;
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

	public String getPartitionedFolders() {
		return partitionedFolders;
	}

	public void setPartitionedFolders(String partitionedFolders) {
		this.partitionedFolders = partitionedFolders;
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

	public Integer getStartingUniqueCharCount() {
		return startingUniqueCharCount;
	}

	public void setStartingUniqueCharCount(Integer startingUniqueCharCount) {
		this.startingUniqueCharCount = startingUniqueCharCount;
	}

	public Integer getEndingUniqueCharCount() {
		return endingUniqueCharCount;
	}

	public void setEndingUniqueCharCount(Integer endingUniqueCharCount) {
		this.endingUniqueCharCount = endingUniqueCharCount;
	}

	public Integer getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public void setMaxFolderDepth(Integer maxFolderDepth) {
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

	public String getExternalFileNamePatternId() {
		return externalFileNamePatternId;
	}

	public void setExternalFileNamePatternId(String externalFileNamePatternId) {
		this.externalFileNamePatternId = externalFileNamePatternId;
	}

	public String getExternalFileName() {
		return externalFileName;
	}

	public void setExternalFileName(String externalFileName) {
		this.externalFileName = externalFileName;
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

	public String getReadLatestPartition() {
		return readLatestPartition;
	}

	public void setReadLatestPartition(String readLatestPartition) {
		this.readLatestPartition = readLatestPartition;
	}

	public String getAlationIntegrationEnabled() {
		return alationIntegrationEnabled;
	}

	public void setAlationIntegrationEnabled(String alationIntegrationEnabled) {
		this.alationIntegrationEnabled = alationIntegrationEnabled;
	}

	public String getIncrementalDataReadEnabled() {
		return incrementalDataReadEnabled;
	}

	public void setIncrementalDataReadEnabled(String incrementalDataReadEnabled) {
		this.incrementalDataReadEnabled = incrementalDataReadEnabled;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}
