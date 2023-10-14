package com.databuck.Migration;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Migration {
	Flyway flyway_appDB = null;
	Flyway flyway_resultDB = null;

	String appDB_sql = "com.appdb.migration";
	String resultDB_sql = "com.resultdb.migration";
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private DataSource dataSource1;
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private Properties resultDBConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(Migration.class);

	public int getMigration() {
		
		// App db migration
		String pg_app_databaseSchemaName = appDbConnectionProperties.getProperty("db.postgres.databaseschema.name");

		Map<String, String> appdbPlaceHolders = new HashMap<String,String>();
		appdbPlaceHolders.put("appdbSchemaName", pg_app_databaseSchemaName);
		
		flyway_appDB = Flyway.configure().dataSource(dataSource).table("schema_version").placeholders(appdbPlaceHolders).locations(appDB_sql).load();
		flyway_appDB.repair();

		int migrate =flyway_appDB.migrate();
				
		// Results db migration
		String pg_results_databaseSchemaName = resultDBConnectionProperties.getProperty("db1.postgres.databaseschema.name");

		Map<String, String> resultsdbPlaceHolders = new HashMap<String,String>();
		resultsdbPlaceHolders.put("resultsdbSchemaName", pg_results_databaseSchemaName);
		
		
		flyway_resultDB = Flyway.configure().dataSource(dataSource1).table("schema_version").placeholders(resultsdbPlaceHolders).locations(resultDB_sql)
				.load();
		flyway_resultDB.repair();

		int migrate2 =flyway_resultDB.migrate();
		return migrate + migrate2;

	}

	public void isVersionAvailable_for_AppDB() {

		flyway_appDB = Flyway.configure().dataSource(dataSource).table("schema_version").locations(appDB_sql).load();
		
		MigrationInfoService m = flyway_appDB.info();
		MigrationInfo current = m.current();
		MigrationVersion getversion1 = current.getVersion();
		String description1 = current.getDescription();

		LOG.debug("Current migrated Version for AddDB is::" + getversion1 + "=> " + description1);

		MigrationInfo[] info = m.pending();
		LOG.info("Pending SQL versions for AppDB migrations Are::");
		if (info.length != 0) {
			for (int i = 0; i < info.length; i++) {
				MigrationVersion getversion = info[i].getVersion();
				String getver = getversion.getVersion();
				String description = info[i].getDescription();
				LOG.debug("Version::" + getver + " => " + description);
			}
		} else {
			LOG.info("There is no Sql file for Migraton");
		}
	}

	public void isVersionAvailable_for_ResultDB() {
		flyway_resultDB = Flyway.configure().dataSource(dataSource1).table("schema_version").locations(resultDB_sql).load();

		MigrationInfoService m = flyway_resultDB.info();
		MigrationInfo current = m.current();
		MigrationVersion getversion1 = current.getVersion();
		String description1 = current.getDescription();

		LOG.debug("Current migrated Version for ResultDB is::" + getversion1 + "=> " + description1);

		MigrationInfo[] info = m.pending();
		LOG.info("Pending SQL versions for resultDB migrations Are::");
		if (info.length != 0) {
			for (int i = 0; i < info.length; i++) {
				MigrationVersion getversion = info[i].getVersion();
				String getver = getversion.getVersion();
				String description = info[i].getDescription();
				LOG.debug("Version::" + getver + " => " + description);
			}
		} else {
			LOG.info("There is no Sql file for Migraton");
		}
	}

}
