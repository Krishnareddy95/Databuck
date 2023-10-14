package com.databuck.bean;

public class listDataAccess {
	private long idlistDataAccess;
	private long idData;
	private String hostName;
	private String portName;
	private String userName;
	private String pwd;
	private String schemaName;
	private String folderName;
	private String queryString;
	private String query;
	private long idDataSchema;
	private String whereCondition;
	private String 	domain;
	private String incrementalType;
	private String dateFormat;
	private String sliceStart;
	private String sliceEnd;
	private String fileHeader;
	private String isRawData;
	private String hivejdbcport;
	private String hivejdbchost;
	private String sslEnb;
	private String sslTrustStorePath;
	private String trustPassword;
	private String metaData;
	// Adding fields for Hive(Knox)
	
	private String gatewayPath;
	private String jksPath;
	private String zookeeperUrl;

	// Adding rolling header fields
	private String rollingHeader;
	private String rollingColumn;
	private int rollTargetSchemaId;
	
	private String historicDateTable;
	
	
	public int getRollTargetSchemaId() {
		return rollTargetSchemaId;
	}
	public void setRollTargetSchemaId(int rollTargetSchemaId) {
		this.rollTargetSchemaId = rollTargetSchemaId;
	}
	public String getZookeeperUrl() {
		return zookeeperUrl;
	}
	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}
		
	public String getMetaData() {
		return metaData;
	}
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	public String getIsRawData() {
		return isRawData;
	}
	public void setIsRawData(String isRawData) {
		this.isRawData = isRawData;
	}
	public long getIdlistDataAccess() {
		return idlistDataAccess;
	}
	public void setIdlistDataAccess(long idlistDataAccess) {
		this.idlistDataAccess = idlistDataAccess;
	}
	public long getIdData() {
		return idData;
	}
	public void setIdData(long idData) {
		this.idData = idData;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}
	public String getWhereCondition() {
		return whereCondition;
	}
	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getIncrementalType() {
		return incrementalType;
	}
	public void setIncrementalType(String incrementalType) {
		this.incrementalType = incrementalType;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public String getSliceStart() {
		return sliceStart;
	}
	public void setSliceStart(String sliceStart) {
		this.sliceStart = sliceStart;
	}
	public String getSliceEnd() {
		return sliceEnd;
	}
	public void setSliceEnd(String sliceEnd) {
		this.sliceEnd = sliceEnd;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getFileHeader() {
		return fileHeader;
	}
	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
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

	/*
	 * public String getsslTrustStorePath() { return sslTrustStorePath; } public
	 * void setsslTrustStorePath(String sslTrustStorePath) { this.sslTrustStorePath
	 * = sslTrustStorePath; }
	 */
	/*
	 * public String gettrustPassword(){ return trustPassword; } public void
	 * settrustPassword(String trustPassword){ this.trustPassword=trustPassword; }
	 */
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
	public String getRollingHeader() {
		return rollingHeader;
	}
	public void setRollingHeader(String rollingHeader) {
		this.rollingHeader = rollingHeader;
	}
	public String getRollingColumn() {
		return rollingColumn;
	}
	public void setRollingColumn(String rollingColumn) {
		this.rollingColumn = rollingColumn;
	}
	public String getHistoricDateTable() {
		return historicDateTable;
	}
	public void setHistoricDateTable(String historicDateTable) {
		this.historicDateTable = historicDateTable;
	}
	
}