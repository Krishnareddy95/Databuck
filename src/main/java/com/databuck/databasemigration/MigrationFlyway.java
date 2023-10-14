package com.databuck.databasemigration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.util.DateUtility;

@Service
public class MigrationFlyway {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private DataSource dataSource1;	
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private Properties resultDBConnectionProperties;

	public void doBackloggedAndOnGoingSchemaChanges(MigrationManagement.DbMigrationContext oMigrationContext) {
		Flyway oFlywayAppDB = null;
		Flyway oFlywayResultDB = null;

		String sAppDBSql = "com.databuck.databasemigration.configdb";
		String sResultsDBSql = "com.databuck.databasemigration.resultsdb";	
		
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sAppDBSql = "com.databuck.databasemigration."+DatabuckConstants.DB_TYPE_POSTGRES+".configdb";
			sResultsDBSql = "com.databuck.databasemigration."+DatabuckConstants.DB_TYPE_POSTGRES+".resultsdb";	
		}
		
		String pg_app_databaseSchemaName = appDbConnectionProperties.getProperty("db.postgres.databaseschema.name");
		String pg_results_databaseSchemaName = resultDBConnectionProperties.getProperty("db1.postgres.databaseschema.name");
		
		Map<String, String> appdbPlaceHolders = new HashMap<String,String>();
		appdbPlaceHolders.put("appdbSchemaName", pg_app_databaseSchemaName);
		
		Map<String, String> resultsdbPlaceHolders = new HashMap<String,String>();
		resultsdbPlaceHolders.put("resultsdbSchemaName", pg_results_databaseSchemaName);
		
		try {
			oFlywayAppDB = Flyway.configure().dataSource(dataSource).table("schema_version").locations(sAppDBSql).placeholders(appdbPlaceHolders).load();
			
			DateUtility.DebugLog("doBackloggedAndOnGoingSchemaChanges 01", "Begin");
			oFlywayAppDB.repair();
			oFlywayAppDB.migrate();
			DateUtility.DebugLog("doBackloggedAndOnGoingSchemaChanges 02", "App DB done");
			
			oFlywayResultDB = Flyway.configure().dataSource(dataSource1).table("schema_version").locations(sResultsDBSql).placeholders(resultsdbPlaceHolders).load();
			oFlywayResultDB.repair();
			oFlywayResultDB.migrate();
			
			DateUtility.DebugLog("doBackloggedAndOnGoingSchemaChanges 03", "End");
		} catch (Exception oException) {
			DateUtility.DebugLog("doBackloggedAndOnGoingSchemaChanges 99", oException.getMessage());			
			throw oException;
		}
	}		
}
