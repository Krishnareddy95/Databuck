package com.databuck.config;

import java.util.Map;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.constants.DatabuckConstants;
import com.databuck.security.KeyVaultAuthService;
import com.databuck.security.LogonManager;

@Service
public class DatabuckPropertyInitializer {

	@Autowired
	private LogonManager logonManager;

	@Autowired
	private KeyVaultAuthService keyVaultAuthService;

	public static String azureSecretsEnabled = null;
	public static String azureSecretsADUser = "";
	public static String azureSecretsADPwd = "";
	private static String azureSecretsDBUser = "";
	private static String azureSecretsDBPwd = "";

	private static String logonManagerEnabled = null;
	private static String logonManagerAppDbCmd = "";
	private static String logonManagerResultsDbCmd = "";

	public Properties readAppDBProperties(Properties propFile) {

		// Read database type
		String db_type = propFile.getProperty("db.type");
		DatabuckEnv.DB_TYPE = (db_type != null && db_type.trim().equalsIgnoreCase("postgres"))
				? DatabuckConstants.DB_TYPE_POSTGRES
				: DatabuckConstants.DB_TYPE_MYSQL;
		System.out.println("\n====> Application started with db type: " + DatabuckEnv.DB_TYPE);
		
		// azure secrets variables
		azureSecretsEnabled = propFile.getProperty("azure.secrets.enabled");
		String azureSecretsDBUserKey = propFile.getProperty("azure.secrets.db.user.key");
		String azureSecretsDBPwdKey = propFile.getProperty("azure.secrets.db.pwd.key");
		String azureSecretsADUserKey = propFile.getProperty("azure.secrets.activedir.user.key");
		String azureSecretsADPwdKey = propFile.getProperty("azure.secrets.activedir.pwd.key");

		// LogonManager variables
		logonManagerEnabled = propFile.getProperty("logon_manager.enabled");
		logonManagerAppDbCmd = propFile.getProperty("logon_manager.appdb.key");
		logonManagerResultsDbCmd = propFile.getProperty("logon_manager.resultdb.key");

		if (azureSecretsEnabled != null && azureSecretsEnabled.equalsIgnoreCase("Y")) {

			// Retrieve DB Username from KeyVault
			System.out.println("\n=====> Retrieving DB username from KeyVault ..");
			azureSecretsDBUser = getSecretValueFromKeyVault(azureSecretsDBUserKey);

			// Set the Username
			propFile.setProperty("db.user", azureSecretsDBUser);

			// Retrieve DB password from KeyVault
			System.out.println("\n=====> Retrieving DB password from KeyVault ..");
			azureSecretsDBPwd = getSecretValueFromKeyVault(azureSecretsDBPwdKey);

			// Set the password
			propFile.setProperty("db.pwd", azureSecretsDBPwd);

			// Retrieve AD Username from KeyVault
			System.out.println("\n=====> Retrieving AD username from KeyVault ..");
			azureSecretsADUser = getSecretValueFromKeyVault(azureSecretsADUserKey);

			// Retrieve AD password from KeyVault
			System.out.println("\n=====> Retrieving AD password from KeyVault ..");
			azureSecretsADPwd = getSecretValueFromKeyVault(azureSecretsADPwdKey);

		} else if (logonManagerEnabled != null && logonManagerEnabled.equalsIgnoreCase("Y")) {

			// Get the credentials from logon manager
			Map<String, String> dbDetailsMap = logonManager.getCredentialsFromLogonCmd(logonManagerAppDbCmd);

			// Validate the logon manager response for key
			boolean responseStatus = logonManager.validateLogonManagerResponseForDB(dbDetailsMap);

			String url = "";
			String dbUser = "";
			String dbPwd = "";

			// Fetch the details if the response is valid
			if (responseStatus) {
				String databaseName = propFile.getProperty("db.schema.name");
				String host = dbDetailsMap.get("hostname");
				String port = dbDetailsMap.get("port");
				
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					String pg_databaseSchemaName = propFile.getProperty("db.postgres.databaseschema.name");
					pg_databaseSchemaName = (pg_databaseSchemaName != null && !pg_databaseSchemaName.trim().isEmpty())
							? pg_databaseSchemaName.trim()
							: "public";
					propFile.setProperty("db.postgres.databaseschema.name", pg_databaseSchemaName);
					
					url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName + "?currentSchema="
							+ pg_databaseSchemaName;
				} else {
					url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
				}
				dbUser = dbDetailsMap.get("username");
				dbPwd = dbDetailsMap.get("password");
			}
			propFile.setProperty("db.url", url);
			propFile.setProperty("db.user", dbUser);
			propFile.setProperty("db.pwd", dbPwd);
		} else {
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
			String decryptedText = decryptor.decrypt(propFile.getProperty("db.pwd"));
			propFile.setProperty("db.pwd", decryptedText);
		}
		
		System.out.println("\n==== AppDB Details =====>");
		System.out.println("\n====>db url: " + propFile.getProperty("db.url"));
		System.out.println("\n====>db user: " + propFile.getProperty("db.user"));

		return propFile;
	}

	public Properties readResultDBProperties(Properties propFile) {
		String dbUser = "";
		String dbPwd = "";
		String url = "";

		if (azureSecretsEnabled != null && azureSecretsEnabled.equalsIgnoreCase("Y")) {
			url = propFile.getProperty("db1.url");

			// Set the Username
			dbUser = azureSecretsDBUser;
			propFile.setProperty("db1.user", dbUser);

			// Set the password
			dbPwd = azureSecretsDBPwd;
			propFile.setProperty("db1.pwd", dbPwd);

		} else if (logonManagerEnabled != null && logonManagerEnabled.equalsIgnoreCase("Y")) {

			// Get the credentials from logon manager
			Map<String, String> dbDetailsMap = logonManager.getCredentialsFromLogonCmd(logonManagerResultsDbCmd);

			// Validate the logon manager response for key
			boolean responseStatus = logonManager.validateLogonManagerResponseForDB(dbDetailsMap);

			// Fetch the details if the response is valid
			if (responseStatus) {
				String databaseName = propFile.getProperty("db1.schema.name");
				String host = dbDetailsMap.get("hostname");
				String port = dbDetailsMap.get("port");
				dbUser = dbDetailsMap.get("username");
				dbPwd = dbDetailsMap.get("password");

				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					String pg_databaseSchemaName = propFile.getProperty("db1.postgres.databaseschema.name");
					pg_databaseSchemaName = (pg_databaseSchemaName != null && !pg_databaseSchemaName.trim().isEmpty())
							? pg_databaseSchemaName.trim()
							: "public";
					propFile.setProperty("db1.postgres.databaseschema.name", pg_databaseSchemaName);
					
					url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName + "?currentSchema="
							+ pg_databaseSchemaName;
				} else {
					url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
				}
			}
			propFile.setProperty("db1.url", url);
			propFile.setProperty("db1.user", dbUser);
			propFile.setProperty("db1.pwd", dbPwd);
		} else {
			url = propFile.getProperty("db1.url");
			dbUser = propFile.getProperty("db1.user");

			// Decrypt password
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
			dbPwd = decryptor.decrypt(propFile.getProperty("db1.pwd"));
			propFile.setProperty("db1.pwd", dbPwd);
		}

		propFile.setProperty("url", url);
		propFile.setProperty("user", dbUser);
		propFile.setProperty("password", dbPwd);
		propFile.setProperty("driver", propFile.getProperty("db1.driver"));
		
		System.out.println("\n==== ResultsDB Details =====>");
		System.out.println("\n====>db url: " + propFile.getProperty("db1.url"));
		System.out.println("\n====>db user: " + propFile.getProperty("db1.user"));

		return propFile;
	}

	private String getSecretValueFromKeyVault(String secretKey) {
		String output = "";
		try {
			if (secretKey != null && !secretKey.trim().isEmpty()) {
				String secretValue = keyVaultAuthService.getSecretValueFromKeyVault(secretKey);

				if (secretValue != null && !secretValue.trim().isEmpty()) {
					StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
					decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
					output = decryptor.decrypt(secretValue);
				}
			} else {
				System.out.println("\n====> secret key is missing!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

}