package com.databuck.databasemigration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;

@Service
public class MigrationManagement {

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private Properties resultDBConnectionProperties;

	@Autowired
	private MigrationFlyway oMigrationFlyway;

	public enum DbMigrationContext { FreshDatabase, InconsistentDatabase, ExistingDatabaseWithChanges, ExistingDatabaseNoChanges, ErrorOccured };
	public enum DbMigrationScripts { Baseline, ConfigDb, ResultsDb };

	protected static LinkedHashMap<String, String> BaseDbProperties = null;

	protected static final int OPTIMAL_DB_OBJECTS = 60;
	protected static final String ADDITIONAL_MYSQL_PROPERTIES = "allowMultiQueries=true&max_allowed_packet=16777216";
	protected static final String MIGRATION_BASELINE_PATH = "baseline";
	protected static final String MIGRATION_CONFIGDB_PATH = "configdb";
	protected static final String MIGRATION_RESULTSDB_PATH = "resultsdb";

	/* Top level function to check overall DB migration context call from any controller */
	public DbMigrationContext getDbMigrationContext(ServletContext oServletContext) {
		DbMigrationContext oRetValue = null;

		try {
			Class.forName(appDbConnectionProperties.getProperty("db.driver")).newInstance();  // loads mysql driver for all code in this module

			MigrationManagement.BaseDbProperties = getDerivedDbProperties(oServletContext);
			DateUtility.DebugLog("getDbMigrationContext 01","Got derived properties, saved to global static hashmap, now checking db migration context ..");

			oRetValue = getFreshDbMigrationContext();
			DateUtility.DebugLog("getDbMigrationContext 02",String.format("Got initial DB migration context as value as '%1$s'",oRetValue));

			if (oRetValue != DbMigrationContext.FreshDatabase) { oRetValue = getExistingDbMigrationContext(); }
			DateUtility.DebugLog("getDbMigrationContext 03",String.format("Got final DB migration context as value as '%1$s'",oRetValue));

		} catch (Exception oException) {
			oException.printStackTrace();
			DateUtility.DebugLog("getDbMigrationContext 99",String.format("Error while checking DB migration context '%1$s'", oException.getMessage()));
			oRetValue = DbMigrationContext.ErrorOccured;
		}
		return oRetValue;
	}

	public String getByPassMigrationDisplableMsg(DbMigrationContext oMigrationType) throws Exception {
		String sConsolidateMsg = "Configuration database schema version:CRLF%1$sCRLFCRLFResults database schema version:CRLF%2$sCRLFCRLF%3$s";
		String sConfigDbMsg = "Current Config Database Version => '%1$s', Need to be updgraded to '%2$s'";
		String sResultDbMsg = "Current Result Database Version => '%1$s', Need to be updgraded to '%2$s'";
		String sManualMsg = "";

		double dAppDbVersion,dResultsDbVersion,dAppScriptsVersion,dResultsScriptsVersion;

		dAppDbVersion = getDbLastestVersion(DbMigrationScripts.ConfigDb);
		dResultsDbVersion = getDbLastestVersion(DbMigrationScripts.ResultsDb);

		dAppScriptsVersion = getScriptsLastestVersion(DbMigrationScripts.ConfigDb);
		dResultsScriptsVersion = getScriptsLastestVersion(DbMigrationScripts.ResultsDb);

		sConfigDbMsg = (dAppDbVersion != dAppScriptsVersion) ? String.format(sConfigDbMsg, dAppDbVersion, dAppScriptsVersion) : "Version is up to Date, No upgrade needed";
		sResultDbMsg = (dResultsDbVersion != dResultsScriptsVersion) ? String.format(sResultDbMsg, dResultsDbVersion, dResultsScriptsVersion) : "Version is up to Date, No upgrade needed";
		
		sManualMsg = sManualMsg + "Kindly do below manual actions before click proceedCRLF";
		sManualMsg = sManualMsg + "(a) Run all SQL scripts which are above current versionCRLF";
		sManualMsg = sManualMsg + "(b) For each script run insert each row in 'schema_version' in respective databasesCRLF";
		
		sConsolidateMsg = String.format(sConsolidateMsg, sConfigDbMsg, sResultDbMsg, sManualMsg);
		
		DateUtility.DebugLog("getByPassMigrationDisplableMsg",  sConsolidateMsg);		
		
		return sConsolidateMsg;
	}
	
	public boolean isByPassMigrationEnabled() {
		String sMigrationSecurityLevel = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "manualDBMigrationEnabled", "N");
		boolean lRetValue = sMigrationSecurityLevel.equalsIgnoreCase("Y") ? true : false;		
		return lRetValue;
	}
	
	/* Top level function to do DB migration, called from migration page when user click "Proceed" button */
	public JSONObject doDatabaseMigration(DbMigrationContext oMigrationType) {
		JSONObject oMigrationStatus = new JSONObject();

		try {
			/* if blank My SQL DB created DataBuck databases with names picked up from properties file */
			if (oMigrationType == DbMigrationContext.FreshDatabase) {
				if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_MYSQL)) {
					createDataBuckDatabases();
				}
				importDataBuckBaseSchema();		// Imports baseline schema, done only once for fresh DB
			}

			/* if not exists create Flyway DB version table, unconditional for fresh or existing DB first run */
			createMigrationSystemSchema();

			/* For fresh DB flyway library will automatically do backlogged DB changes,for existing only latest */
			oMigrationFlyway.doBackloggedAndOnGoingSchemaChanges(oMigrationType);

			oMigrationStatus.put("PageContext", 2);
			oMigrationStatus.put("Msg", "Database schema imported/upgraded successfully, kindly login to DataBuck");
		} catch (Exception oException) {
			oMigrationStatus.put("PageContext", -1);
			oMigrationStatus.put("Msg", oException.getMessage());
			oException.printStackTrace();
		}

		return oMigrationStatus;
	}

	/* Get count of DataBuck DB objects to check is it fresh mysql database? MigrationManagement.BaseDbProperties variable must be initialize with values before call. */
	private DbMigrationContext getFreshDbMigrationContext() throws Exception {
		Connection oAppDbConn = null;
		Connection oResultDbConn = null;
		Statement oQuery = null;
		ResultSet oResultSet = null;
		DbMigrationContext oRetValue = null;
		int nObjectsCount = -1;
		String sCheckQuery = "";

		try {

			/* Check databuck objects count in app config db */

			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				oAppDbConn = DriverManager.getConnection(appDbConnectionProperties.getProperty("db.url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));
			
				String pg_app_databaseSchemaName = appDbConnectionProperties.getProperty("db.postgres.databaseschema.name");
				pg_app_databaseSchemaName = (pg_app_databaseSchemaName != null && !pg_app_databaseSchemaName.trim().isEmpty())?pg_app_databaseSchemaName.trim():"public";
				
				sCheckQuery = "select count(*) from information_schema.tables where table_schema='"+pg_app_databaseSchemaName+"' and table_type='BASE TABLE' and table_catalog='configdb'";
			} else {
				oAppDbConn = DriverManager.getConnection(MigrationManagement.BaseDbProperties.get("raw_app_db_url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));
				
				sCheckQuery = "select count(*) as count from information_schema.tables where lower(table_schema) = lower('configdb')";
			}

			sCheckQuery = sCheckQuery.replaceAll("configdb", MigrationManagement.BaseDbProperties.get("app_db_name"));

			System.out.println("sCheckQuery: " + sCheckQuery);
			oQuery = oAppDbConn.createStatement();
			oResultSet = oQuery.executeQuery(sCheckQuery);
			if (oResultSet.next()) { nObjectsCount = oResultSet.getInt("count"); 
			System.out.println("nObjectsCount: " + nObjectsCount);}
			oAppDbConn.close();

			/* Check databuck objects count in results db */
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				oResultDbConn = DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));
			
				String pg_results_databaseSchemaName = resultDBConnectionProperties.getProperty("db1.postgres.databaseschema.name");
				pg_results_databaseSchemaName = (pg_results_databaseSchemaName != null && !pg_results_databaseSchemaName.trim().isEmpty())?pg_results_databaseSchemaName.trim():"public";

				sCheckQuery = "select count(*) from information_schema.tables where table_schema='"+pg_results_databaseSchemaName+"' and table_type='BASE TABLE' and table_catalog='resultsdb'";

			} else {
				oResultDbConn = DriverManager.getConnection(MigrationManagement.BaseDbProperties.get("raw_result_db_url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));
			
				sCheckQuery = "select count(*) as count from information_schema.tables where lower(table_schema) = lower('resultsdb')";
			}

			

			sCheckQuery = sCheckQuery.replaceAll("resultsdb", MigrationManagement.BaseDbProperties.get("result_db_name"));

			System.out.println("sCheckQuery: " + sCheckQuery);

			oQuery = oResultDbConn.createStatement();
			oResultSet = oQuery.executeQuery(sCheckQuery);
			if (oResultSet.next()) { nObjectsCount = nObjectsCount + oResultSet.getInt("count");
			System.out.println("nObjectsCount: " + nObjectsCount);}
			oResultDbConn.close();

			DateUtility.DebugLog("getFreshDbMigrationContext 01",String.format("Got databuck DB objects count as %1$s", nObjectsCount));

			if (nObjectsCount == 0) {
				oRetValue = DbMigrationContext.FreshDatabase;
			} else if (nObjectsCount < OPTIMAL_DB_OBJECTS) {
				oRetValue = DbMigrationContext.InconsistentDatabase;
			} else if (nObjectsCount >= OPTIMAL_DB_OBJECTS) {
				oRetValue = DbMigrationContext.ExistingDatabaseNoChanges;
			}

		} catch (Exception oException) {
			throw oException;
		}
		return oRetValue;
	}

	/* Campare schema_version to last DB changes files version available in config db and results db, send return value. */
	private DbMigrationContext getExistingDbMigrationContext() throws Exception {
		DbMigrationContext oRetValue = DbMigrationContext.ExistingDatabaseNoChanges;

		double dAppDbVersion,dResultsDbVersion,dAppScriptsVersion,dResultsScriptsVersion;

		if(!isByPassMigrationEnabled()) {
			/* 1st call for existing DB, if not exists create Flyway DB version 'schema_version' table, as this function need to query this table */
			createMigrationSystemSchema();
		}

		dAppDbVersion = getDbLastestVersion(DbMigrationScripts.ConfigDb);
		dResultsDbVersion = getDbLastestVersion(DbMigrationScripts.ResultsDb);

		dAppScriptsVersion = getScriptsLastestVersion(DbMigrationScripts.ConfigDb);
		dResultsScriptsVersion = getScriptsLastestVersion(DbMigrationScripts.ResultsDb);

		DateUtility.DebugLog("getExistingDbMigrationContext 01",  String.format("versions app db, results db, app scripts, results scripts %1$s, %2$s, %3$s, %4$s", dAppDbVersion,dResultsDbVersion, dAppScriptsVersion, dResultsScriptsVersion));

		oRetValue = ( (dAppScriptsVersion >= 0 && dAppDbVersion != dAppScriptsVersion) || (dResultsScriptsVersion >= 0 && dResultsDbVersion != dResultsScriptsVersion) ) ? DbMigrationContext.ExistingDatabaseWithChanges : oRetValue;

		return oRetValue;
	}

	/* Creats databuck databases, will get called for fresh customer with blank mysql DB. */
	private void createDataBuckDatabases() throws Exception {
		Connection oAppDbConn = null;
		Connection oResultDbConn = null;
		Statement oQuery = null;
		String sCreateDbQueryTmpl = "create database if not exists %1$s default character set latin1;";
		String sCreateDbQuery = "";

		try {
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				oAppDbConn = DriverManager.getConnection(appDbConnectionProperties.getProperty("db.url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));
			} else {
				oAppDbConn = DriverManager.getConnection(MigrationManagement.BaseDbProperties.get("raw_app_db_url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));
			}
			
			sCreateDbQuery = String.format(sCreateDbQueryTmpl,MigrationManagement.BaseDbProperties.get("app_db_name"));
			oQuery = oAppDbConn.createStatement();
			oQuery.addBatch(sCreateDbQuery);
			oQuery.executeBatch();
			oAppDbConn.close();

			/* Check databuck objects count in results db */
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				oResultDbConn = DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));
			} else {
				oResultDbConn = DriverManager.getConnection(MigrationManagement.BaseDbProperties.get("raw_result_db_url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));
			}

			sCreateDbQuery = String.format(sCreateDbQueryTmpl,MigrationManagement.BaseDbProperties.get("result_db_name"));
			oQuery = oResultDbConn.createStatement();
			oQuery.addBatch(sCreateDbQuery);
			oQuery.executeBatch();
			oResultDbConn.close();

			DateUtility.DebugLog("createDataBuckDatabases 01", String.format("Databuck databases app / results DB are created in respective instances as '%1$s','%2$s'",MigrationManagement.BaseDbProperties.get("app_db_name"), MigrationManagement.BaseDbProperties.get("result_db_name")));

		} catch (Exception oException) {
			throw oException;
		}
	}

	/* Creats flyway db schema_version table in both databuck databases, will get called for fresh or existing customer when schema_version is not available. */
	private void createMigrationSystemSchema() throws Exception {
		Connection oAppDbConn = null;
		Statement oQuery = null;
		String sCreateTableQuery = "";
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			
			sCreateTableQuery = sCreateTableQuery + "create table if not exists schema_version (\n";
			sCreateTableQuery = sCreateTableQuery + "installed_rank	int not null,\n";
			sCreateTableQuery = sCreateTableQuery + "version			varchar(50) default null,\n";
			sCreateTableQuery = sCreateTableQuery + "description		varchar(200) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "type				varchar(20) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "script				varchar(1000) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "checksum			int default null,\n";
			sCreateTableQuery = sCreateTableQuery + "installed_by		varchar(100) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "installed_on		timestamp(0) not null default current_timestamp,\n";
			sCreateTableQuery = sCreateTableQuery + "execution_time	int not null,\n";
			sCreateTableQuery = sCreateTableQuery + "success			BOOLEAN not null,\n";
			sCreateTableQuery = sCreateTableQuery + "primary key		(installed_rank)\n";
			sCreateTableQuery = sCreateTableQuery + ");";
			
		} else {
			sCreateTableQuery = sCreateTableQuery + "create table if not exists schema_version (\n";
			sCreateTableQuery = sCreateTableQuery + "installed_rank	int(11) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "version			varchar(50) default null,\n";
			sCreateTableQuery = sCreateTableQuery + "description		varchar(200) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "type				varchar(20) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "script				varchar(1000) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "checksum			int(11) default null,\n";
			sCreateTableQuery = sCreateTableQuery + "installed_by		varchar(100) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "installed_on		timestamp not null default current_timestamp,\n";
			sCreateTableQuery = sCreateTableQuery + "execution_time	int(11) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "success			tinyint(1) not null,\n";
			sCreateTableQuery = sCreateTableQuery + "primary key		(installed_rank),\n";
			sCreateTableQuery = sCreateTableQuery + "key					schema_version_s_idx (success)\n";
			sCreateTableQuery = sCreateTableQuery + ") engine=innodb default charset=latin1;";
		}

		try {
			System.out.println("\n====>db.url: "+appDbConnectionProperties.getProperty("db.url"));
			System.out.println("\n====>db1.url: "+resultDBConnectionProperties.getProperty("db1.url"));
			
			oAppDbConn = DriverManager.getConnection(appDbConnectionProperties.getProperty("db.url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));

			oQuery = oAppDbConn.createStatement();
			oQuery.addBatch(sCreateTableQuery);
			oQuery.executeBatch();
			oAppDbConn.close();

			oAppDbConn = DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));

			oQuery = oAppDbConn.createStatement();
			oQuery.addBatch(sCreateTableQuery);
			oQuery.executeBatch();
			oAppDbConn.close();

			DateUtility.DebugLog("createMigrationSystemSchema 01", "Created (if not exists) flyway db schema_version table in both databuck databases");

		} catch (Exception oException) {
			throw oException;
		}
	}

	/* Imports base schema objects, along with seed data to allow user to do first login to databuck, will get called for fresh customer only */
	private void importDataBuckBaseSchema() throws Exception {
		Connection oAppDbConn = null;
		List<String> aBaseScripts = new ArrayList<String>();
		ScriptRunner oScriptRunner = null;
		Reader oReader = null;

		try {
			aBaseScripts = getDatabaseScripts(DbMigrationScripts.Baseline, false);
			if (aBaseScripts.size() != 2) { throw new Exception("DataBuck baseline schema scripts to be imported are invalid or not found"); }

			DateUtility.DebugLog("importDataBuckBaseSchema 01", String.format("Found no of scripts files '%1$s'", aBaseScripts.size()));

			for (String sBaselineScript : aBaseScripts) {
				oReader = new BufferedReader(new FileReader(sBaselineScript));
				oAppDbConn = getBaselineDbConnection(sBaselineScript);
				oScriptRunner = new ScriptRunner(oAppDbConn);

				oScriptRunner.setLogWriter(null);
				oScriptRunner.setErrorLogWriter(null);
				oScriptRunner.setSendFullScript(true);

				oScriptRunner.runScript(oReader);
				oScriptRunner.closeConnection();
				oAppDbConn.close();
				DateUtility.DebugLog("importDataBuckBaseSchema 02", String.format("Imported script %1$s=%2$s", sBaselineScript,"Success"));
			}

		} catch (Exception oException) {
			DateUtility.DebugLog("importDataBuckBaseSchema 99", String.format("Exception occured '%1$s'", oException.getMessage()));
			oException.printStackTrace();
			throw oException;
		}
	}

	private Connection getBaselineDbConnection(String sBaselineScript) throws SQLException {
		Connection oAppDbConn = null;
		String sConnUrl = "";

		if (sBaselineScript.toLowerCase().indexOf("config_db") > -1) {

			String db_url = appDbConnectionProperties.getProperty("db.url");
			
			if(db_url.contains("?"))
				sConnUrl = String.format("%1$s&%2$s",db_url, ADDITIONAL_MYSQL_PROPERTIES);
			else
				sConnUrl = String.format("%1$s?%2$s",db_url, ADDITIONAL_MYSQL_PROPERTIES);
			oAppDbConn = DriverManager.getConnection(sConnUrl, appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"));

		} else if (sBaselineScript.toLowerCase().indexOf("results_db") > -1) {

			String db1_url = resultDBConnectionProperties.getProperty("db1.url");
			
			if(db1_url.contains("?"))
				sConnUrl = String.format("%1$s&%2$s",db1_url, ADDITIONAL_MYSQL_PROPERTIES);
			else
				sConnUrl = String.format("%1$s?%2$s",db1_url, ADDITIONAL_MYSQL_PROPERTIES);

			oAppDbConn = DriverManager.getConnection(sConnUrl, resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));

		}
		return oAppDbConn;
	}

	private List<String> getDatabaseScripts(DbMigrationScripts oWhichScripts, boolean lIsReverseOrder) {
		URL oScriptsUrl = null;
		File oScriptsFolder = null;
		File[] aScriptFiles = null;
		String sClassPath = "";
		List<String> aScriptsFileList = new ArrayList<String>();

		try {
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				if (oWhichScripts == DbMigrationScripts.Baseline) {
					sClassPath = DatabuckConstants.DB_TYPE_POSTGRES+"/baseline";
				} else if (oWhichScripts == DbMigrationScripts.ConfigDb) {
					sClassPath = DatabuckConstants.DB_TYPE_POSTGRES+"/configdb";
				} else if (oWhichScripts == DbMigrationScripts.ResultsDb) {
					sClassPath = DatabuckConstants.DB_TYPE_POSTGRES+"/resultsdb";
				}
			} else {
				if (oWhichScripts == DbMigrationScripts.Baseline) {
					sClassPath = MIGRATION_BASELINE_PATH;
				} else if (oWhichScripts == DbMigrationScripts.ConfigDb) {
					sClassPath = MIGRATION_CONFIGDB_PATH;
				} else if (oWhichScripts == DbMigrationScripts.ResultsDb) {
					sClassPath = MIGRATION_RESULTSDB_PATH;
				}
			}
			System.out.println("sClassPath: " +sClassPath);

			oScriptsUrl = this.getClass().getResource(sClassPath);
			oScriptsFolder = new File(oScriptsUrl.toURI());
			DateUtility.DebugLog("getDatabaseScripts 01",String.format("Got file pointer to scripts folder yes/physical path? '%1$s'/'%2$s'", oScriptsFolder.isDirectory(), oScriptsFolder.getAbsolutePath()));

			aScriptFiles = oScriptsFolder.listFiles((dir, name) -> name.endsWith(".sql"));
			DateUtility.DebugLog("getDatabaseScripts 02",String.format("Got no of sql files '%1$s'", aScriptFiles.length));

			/* Please note for reverse order sort, script files list returned with just file name without path, vice versa  */
			for (File oEachSsript : aScriptFiles) {
				aScriptsFileList.add( (lIsReverseOrder) ? oEachSsript.getName() : oEachSsript.getAbsolutePath());
			}
			if (lIsReverseOrder) {
				Collections.sort(aScriptsFileList, Collections.reverseOrder());
			} else {
				Collections.sort(aScriptsFileList);
			}
		} catch (Exception oException) {
			DateUtility.DebugLog("getDatabaseScripts 99", String.format("Exception occured '%1$s'", oException.getMessage()));
			oException.printStackTrace();
		}
		return aScriptsFileList;
	}

	private double getDbLastestVersion(DbMigrationScripts oWhichDb) throws Exception {
		Connection oMySqlConn = null;
		Statement oQuery = null;
		ResultSet oResultSet = null;
		String sVersionQuery = "";
		String sDbVersion = "0.0";

		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sVersionQuery = "select cast(final_qry.sortable_version as decimal(7,5)) as sortable_version, final_qry.version from "
					+ "(select concat( lpad(core_qry.major_version,2,'0'), '.', lpad(core_qry.minor_version,5,'0') ) as sortable_version, core_qry.version "
					+ "from (select trim(split_part(version, '.', 1)) as major_version, trim(split_part(version, '.', 2)) as minor_version, version from schema_version) "
					+ "core_qry ) final_qry order by final_qry.sortable_version desc limit 1";
			 
		} else {
			sVersionQuery = sVersionQuery + "select cast(final_qry.sortable_version as decimal(7,5)) as sortable_version, final_qry.version \n";
			sVersionQuery = sVersionQuery + "from \n";
			sVersionQuery = sVersionQuery + "( \n";
			sVersionQuery = sVersionQuery + "	select concat( lpad(core_qry.major_version,2,'0'), '.', lpad(core_qry.minor_version,5,'0') ) as sortable_version, core_qry.version \n";
			sVersionQuery = sVersionQuery + "	from  \n";
			sVersionQuery = sVersionQuery + "	( \n";
			sVersionQuery = sVersionQuery + "		select trim(substring_index(substring_index(version, '.', 1), '.',-1)) as major_version, trim(substring_index(substring_index(version, '.', 2), '.',-1)) as minor_version, version from schema_version \n";
			sVersionQuery = sVersionQuery + "	) core_qry \n";
			sVersionQuery = sVersionQuery + ") final_qry \n";
			sVersionQuery = sVersionQuery + "order by final_qry.sortable_version desc \n";
			sVersionQuery = sVersionQuery + "limit 1;";
		}

		System.out.println("sVersionQuery: "+sVersionQuery);
		
		oMySqlConn = (oWhichDb == DbMigrationScripts.ConfigDb)
				?
			DriverManager.getConnection(appDbConnectionProperties.getProperty("db.url"), appDbConnectionProperties.getProperty("db.user"), appDbConnectionProperties.getProperty("db.pwd"))
				:
			DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"), resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd"));

		oQuery = oMySqlConn.createStatement();
		oResultSet = oQuery.executeQuery(sVersionQuery);
		if (oResultSet.next()) { sDbVersion = oResultSet.getString("sortable_version"); }
		oMySqlConn.close();

		return getSortableVersion(sDbVersion);    // new Double(sDbVersion);
	}

	private double getScriptsLastestVersion(DbMigrationScripts oWhichScripts) {
		List<String> aScriptsFileList = getDatabaseScripts(oWhichScripts, true);
		String sFileVersion, sFileName;
		String[] aFileParts = null;
		double dMaxVersion = -1.0;
		double dFileVersion = 0.0;

		for (int nFileNo = 0; nFileNo < aScriptsFileList.size(); nFileNo++) {
			sFileName = aScriptsFileList.get(nFileNo).substring(1);
			aFileParts = sFileName.split("_",3);

			sFileVersion = String.format("%1$s.%2$s", aFileParts[0].trim(),aFileParts[1].trim());
			dFileVersion = getSortableVersion(sFileVersion);
			dMaxVersion = (dMaxVersion <= dFileVersion) ? dFileVersion : dMaxVersion;
		}
		DateUtility.DebugLog("getScriptsLastestVersion 01", String.format("Latest version of '%1$s' scripts = '%2$s'",  oWhichScripts, dMaxVersion));
		return dMaxVersion;
	}

	private double getSortableVersion(String sVersion) {
		String[] aVersionParts = null;
		String sSortableVersion = "0.0";
		double dRetValue = 0.0;

		try {
			aVersionParts = sVersion.trim().split("\\.");
			if (aVersionParts.length == 2) {
				sSortableVersion = String.format("%02d", Integer.parseInt(aVersionParts[0])) + "." + String.format("%05d", Integer.parseInt(aVersionParts[1]));
			}
		} catch (Exception oException) {
			sSortableVersion = "0.0";
		}
		dRetValue = new Double(sSortableVersion);
		return dRetValue;
	}

	/* Consolidating derived properties needed during migration into one function, those not directly available. */
	private LinkedHashMap<String, String> getDerivedDbProperties(ServletContext oServletContext) {
		LinkedHashMap<String, String> oRetValue = new LinkedHashMap<String, String>();
		String sPropMySqlUrl, sMySqlUrlWithoutDb, sDbName;
		String sWebsiteRootFolder = oServletContext.getRealPath(File.separator);

		sPropMySqlUrl = appDbConnectionProperties.getProperty("db.url");
		sDbName = appDbConnectionProperties.getProperty("db.schema.name").trim();
		
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sMySqlUrlWithoutDb = sPropMySqlUrl;
		} else {
			sMySqlUrlWithoutDb = sPropMySqlUrl.replace(sDbName,"");
		}

		if(sMySqlUrlWithoutDb.contains("?")) 
			oRetValue.put("raw_app_db_url", String.format("%1$s&%2$s",sMySqlUrlWithoutDb,ADDITIONAL_MYSQL_PROPERTIES));
		else
			oRetValue.put("raw_app_db_url", String.format("%1$s?%2$s",sMySqlUrlWithoutDb,ADDITIONAL_MYSQL_PROPERTIES));
		
		oRetValue.put("app_db_name", sDbName);

		sPropMySqlUrl = resultDBConnectionProperties.getProperty("db1.url");
		sDbName = resultDBConnectionProperties.getProperty("db1.schema.name").trim();
		
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sMySqlUrlWithoutDb = sPropMySqlUrl;
		} else {
			sMySqlUrlWithoutDb = sPropMySqlUrl.replace(sDbName,"");
		}

		if(sMySqlUrlWithoutDb.contains("?"))
			oRetValue.put("raw_result_db_url", String.format("%1$s&%2$s",sMySqlUrlWithoutDb,ADDITIONAL_MYSQL_PROPERTIES));
		else
			oRetValue.put("raw_result_db_url", String.format("%1$s?%2$s",sMySqlUrlWithoutDb,ADDITIONAL_MYSQL_PROPERTIES));
		
		oRetValue.put("result_db_name", sDbName);

		oRetValue.put("website_root_folder", sWebsiteRootFolder);

		DateUtility.DebugLog("getDerivedDbProperties 01", "Listing of Derived Db Properties used by migration program:");
		for (Map.Entry<String, String> oEntry : oRetValue.entrySet()) {
			DateUtility.DebugLog("key=value =>", String.format(" '%1$s'='%2$s' ", oEntry.getKey(), oEntry.getValue()));
		}

		return oRetValue;
	}
}
