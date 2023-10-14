package com.databuck.datatemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ValidateQuery;
import com.databuck.bean.DerivedTemplateValidateQuery;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.service.RemoteClusterAPIService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HiveConnection {

	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	ITaskDAO iTaskDAO;

	public Map<String, String> readTablesFromHive(String hiveConType, String uri, String databaseAndSchema,
			String username, String password, String tablename, String port, String sslEnb, String sslTrustStorePath,
			String trustPassword, boolean kerberosEnabled, String keytab, String krb5conf, String principle,
			boolean isKnoxEnabled, String gatewayPath, String jksPath, String zookeeperUrl, String queryString) {
		Map<String, String> tableData = new LinkedHashMap<String, String>();
		try {
			Connection con = null;
			if (isKnoxEnabled) {
				con = getHiveKnoxConnection(uri, port, databaseAndSchema, gatewayPath, jksPath, username, password);
			} else {
				con = getHiveConnection(hiveConType, uri, databaseAndSchema, username, password, port, sslEnb,
						sslTrustStorePath, trustPassword, kerberosEnabled, keytab, krb5conf, principle, jksPath);
			}

			if (con != null) {
				Statement createStatement1 = con.createStatement();
				String query = "";

				if (queryString != null && !queryString.equals("")) {
					query = queryString;
				} else {
					query = "select * from " + tablename + " limit 1";
				}

				ResultSet executeQuery = createStatement1.executeQuery(query);
				ResultSetMetaData metaData = executeQuery.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnName = metaData.getColumnName(i);

					if (columnName.contains(".")) {
						String[] split = columnName.split("\\.");
						columnName = split[1];
					}
					System.out.println(
							columnName + "   " + metaData.getColumnTypeName(i) + "   " + columnName.contains("."));
					tableData.put(columnName, metaData.getColumnTypeName(i));
				}
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableData;
	}

	public List<String> getListOfTableNamesFromHive(String hiveConType, String hostURI, String database,
			String userlogin, String password, String port, String sslEnb, String sslTrustStorePath,
			String trustPassword, boolean kerberosEnabled, String keytab, String krb5conf, String principle, String jksPath) {
		List<String> tableNameData = new ArrayList<String>();
		try {
			Connection con = getHiveConnection(hiveConType, hostURI, database, userlogin, password, port, sslEnb,
					sslTrustStorePath, trustPassword, kerberosEnabled, keytab, krb5conf, principle, jksPath);
			if (con != null) {
				Statement stmt = con.createStatement();
				String query = "show tables";
				System.out.println(query);
				ResultSet data = stmt.executeQuery(query);

				while (data.next()) {
					tableNameData.add(data.getString(1));
					System.out.println("table data:" + data.getString(1));
				}
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public ResultSet getTableDataFromHive(String hiveConType, String uri, String username, String password,
			String portName, String tablename, String databaseAndSchema, String sslEnb, String sslTrustStorePath,
			String trustPassword, boolean kerberosEnabled, String keytab, String krb5conf, String principle, String jksPath) {
		try {
			Connection con = getHiveConnection(hiveConType, uri, databaseAndSchema, username, password, portName,
					sslEnb, sslTrustStorePath, trustPassword, kerberosEnabled, keytab, krb5conf, principle, jksPath);
			if (con != null) {
				Statement stmt = con.createStatement();
				String query = "Select * from " + tablename + " limit 10000";
				System.out.println(query);
				ResultSet executeQuery = stmt.executeQuery(query);
				return executeQuery;
			}
		} catch (Exception e) {
			System.out.println("Failed to get table data from Hive !!");
			e.printStackTrace();
		}
		return null;

	}

	public Connection getHiveConnection(String hiveConType, String hostURI, String database, String username,
			String password, String port, String sslEnb, String sslTrustStorePath, String trustPassword,
			boolean kerberosEnabled, String keytab, String krb5conf, String principal, String jksPath) throws Exception {
		Connection con = null;
		String dbURL2 = "";

		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");

			if (hiveConType.equalsIgnoreCase("MapR Hive")) {
				dbURL2 = "jdbc:hive2://" + hostURI + ":" + port + "/" + database
						+ ";auth=maprsasl;saslQop=auth-int;ssl=true";
				con = DriverManager.getConnection(dbURL2);
			} else if (kerberosEnabled) {
				System.setProperty("java.security.auth.login.config", keytab);
				// System.setProperty("sun.security.jgss.debug", "true");
				System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
				System.setProperty("java.security.krb5.conf", krb5conf);

				/*
				 String zookeeperURL = hiveConnectionURL(hostURI, port, database);
				if (zookeeperURL != null)
					dbURL2 = "jdbc:hive2://" + zookeeperURL;
				 */
				
				if(hostURI.contains("2181") && jksPath !=null && !jksPath.trim().isEmpty()) {
					dbURL2 = "jdbc:hive2://" + hostURI + "/" + database
							+";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal=" + principal + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
							+ password;
				} else if(jksPath !=null && !jksPath.trim().isEmpty()) {
					dbURL2 = "jdbc:hive2://" + hostURI + ":" + port + "/" + database
							+";principal=" + principal + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
							+ password;
				}
				else
				{
					String zookeeperURL = hiveConnectionURL(hostURI, port, database);
					if (zookeeperURL != null)
						dbURL2 = "jdbc:hive2://" + zookeeperURL;
					else
						dbURL2 = "jdbc:hive2://" + hostURI + ":" + port + "/" + database + ";principal=" + principal;
				}
				con = DriverManager.getConnection(dbURL2);
			} else {
				if (sslEnb != null && sslEnb.equalsIgnoreCase("Y")) {
					dbURL2 = "jdbc:hive2://" + hostURI + ":" + port + "/" + database + ";ssl=true;sslTrustStore="
							+ sslTrustStorePath + ";trustStorePassword=" + trustPassword;
				} else {
					dbURL2 = "jdbc:hive2://" + hostURI + ":" + port + "/" + database;
				}
				con = DriverManager.getConnection(dbURL2, username, password);
			}
		} catch (Exception e) {
			System.out.println("Failed to get Hive connection !!!");
			e.printStackTrace();
		}
		return con;
	}

	private String hiveConnectionURL(String hosts, String port, String domain) {
		String zookeeperURL = null;

		// Check if the server has commad seperated values to identify if it is a
		// zookeeper connection
		if (hosts.contains(",") || port.equalsIgnoreCase("2181")) {
			String parts[] = hosts.split("\\,");
			System.out.print(parts);
			for (String part : parts) {
				System.out.println(" part");
				System.out.println(part);
				if (zookeeperURL != null)
					zookeeperURL = zookeeperURL + "," + part + ":2181";
				else
					zookeeperURL = part + ":2181";
			}
		}

		if (zookeeperURL != null)
			zookeeperURL = zookeeperURL + "/" + domain
					+ ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";

		System.out.println(" zookeeperURL");
		System.out.println(zookeeperURL);
		return zookeeperURL;
	}

	public List getListOfTableNamesFromHiveKnox(String hostURI, String database, String userlogin, String password,
			String port, String jksPath, String gatewayPath) {
		List<String> tableNameData = new ArrayList<String>();
		try {
			Connection con = getHiveKnoxConnection(hostURI, port, database, gatewayPath, jksPath, userlogin, password);
			if (con != null) {
				Statement stmt = con.createStatement();
				String query = "show tables";
				System.out.println(query);
				ResultSet data = stmt.executeQuery(query);

				while (data.next()) {
					tableNameData.add(data.getString(1));
					System.out.println("table data:" + data.getString(1));
				}
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	private Connection getHiveKnoxConnection(String hosts, String port, String database, String gatewayPath,
			String jksPath, String username, String password) {

		Connection con = null;
		try {
			System.setProperty("javax.net.ssl.trustStore", jksPath);

			String url = "jdbc:hive2://" + hosts + ":" + port + "/" + database
					+ ";ssl=true;transportMode=http;httpPath=" + gatewayPath;

			System.out.println("In Hive Knox =>" + url);

			// load Hive JDBC Driver
			Class.forName("org.apache.hive.jdbc.HiveDriver");

			// configure JDBC connection
			con = DriverManager.getConnection(url, username, password);

		} catch (Exception e) {
			System.out.println("Failed to get Hive Knox connection !!!");
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * Get list of tables from MapR Hive This is required for special treatment for
	 * Mapr Tickets Check if these are done via PAM user name and password this
	 * needs to be done from UI
	 * 
	 * @param hostURI
	 * @param database
	 * @param userlogin
	 * @param password
	 * @param port
	 * @return
	 */
	public List<String> getListOfTableNamesFromMapRHive(String hostURI, String database, String userlogin,
			String password, String port, String domainName) {
		List<String> tableNameData = new ArrayList<String>();
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();

			String databuckHome = "/opt/databuck";

			if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null
					&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			System.out.println("DATABUCK_HOME:" + databuckHome);

			String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
					+ " com.databuck.mapr.hive.ListHiveTables " + hostURI + ":" + port + " " + database;
			System.out.println(" shellCommand - " + shellCommand);
			processBuilder.command("bash", "-c", shellCommand);

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			boolean resultStart = false;

			while ((line = reader.readLine()) != null) {

				output.append(line + "\n");
				// Table list has started
				if (resultStart) {
					tableNameData.add(line);
				}

				// Start of Table list
				if (line.contains("tab_name")) {
					resultStart = true;
				}
			}

			int exitVal = process.waitFor();

			System.out.println(output);

			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(tableNameData);
			} else {
				// abnormal...
				System.out.println("Failure!");
				throw new Exception("Failed to get table name using shell");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	/**
	 *
	 * @param hostURI
	 * @param database
	 * @param userlogin
	 * @param password
	 * @param port
	 * @param domainName
	 * @param tablename
	 * @return
	 */
	public Map<String, String> getTableMetaDataFromMapRHive(String hostURI, String database, String userlogin,
			String password, String port, String domainName, String tablename, String queryString) {
		Map<String, String> tableMetadata = new HashMap<String, String>();
		try {
			String databuckHome = "/opt/databuck";

			if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null
					&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			System.out.println("DATABUCK_HOME:" + databuckHome);

			// Check if query enabled
			String queryOrTable = "";
			if (queryString != null && !queryString.trim().isEmpty()) {
				queryOrTable = "'" + queryString + "'";
			} else {
				queryOrTable = tablename;
			}

			ProcessBuilder processBuilder = new ProcessBuilder();
			String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
					+ " com.databuck.mapr.hive.ReadMetadata " + hostURI + ":" + port + " " + database + " "
					+ queryOrTable;
			System.out.println(" shellCommand - " + shellCommand);
			processBuilder.command("bash", "-c", shellCommand);

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			boolean resultStart = false;

			while ((line = reader.readLine()) != null) {

				output.append(line + "\n");
				// Table list has started
				if (resultStart && !line.contains("*Metadata End*")) {
					String[] meta = line.split(":");
					tableMetadata.put(meta[0], meta[1]);
				}

				// Start of Table list
				if (line.contains("*Metadata Start*")) {
					resultStart = true;
				}
			}

			int exitVal = process.waitFor();

			System.out.println(output);

			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(tableMetadata);
			} else {
				// abnormal...
				System.out.println("Failed to get table name using shell!");
				throw new Exception("Failed to get table name using shell");
			}
		} catch (Exception e) {
			System.out.println("Failed to get table data from Hive !!");
			e.printStackTrace();
		}
		return tableMetadata;

	}

	public JSONObject getProfileData(ListDataSource listDataSource, listDataAccess listDataAccess, long idDataSchema, int limit) {
		JSONObject json = new JSONObject();
		List<ListDataSchema> listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
		String hostURI = listDataSchema.get(0).getIpAddress();
		String port = listDataSchema.get(0).getPort();
		String tablename = listDataAccess.getFolderName();
		String queryString = listDataAccess.getQueryString();
		String database = listDataSchema.get(0).getDatabaseSchema();
		String domainName = "";
		String userlogin = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();

		Set<String> headers = new HashSet<>();
		JSONArray actualData = new JSONArray();

		try {

			domainName = iTaskDAO.getDomainNameById(listDataSchema.get(0).getDomainId().longValue());

			String databuckHome = "/opt/databuck";
			if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null && !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			System.out.println("DATABUCK_HOME:" + databuckHome);

			// Check if query enabled
			String queryOrTable = "";
			if (queryString != null && !queryString.trim().isEmpty()) {
				queryOrTable = "'select * from (" + queryString + ") t1 limit "+limit+" '";
			} else {
				queryOrTable = "'select * from "+tablename+" limit "+limit+ " '";
			}

			String clusterPropertyCategory = listDataSchema.get(0).getClusterPropertyCategory();
			System.out.println("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

			if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& (!clusterPropertyCategory.equalsIgnoreCase("cluster")
					&& !clusterPropertyCategory.equalsIgnoreCase("local"))) {

				String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);

				List<String> rawData = remoteClusterAPIService.getProfileData(hostURI, database, userlogin,
						password, port, domainName, queryOrTable, encryptedToken, idDataSchema);

				boolean dataStart=false;
				for(String data: rawData){
					if(data.contains("*ProfileData End*")){
						dataStart = false;
					}
					if(dataStart){
						JSONObject jsonObject = new JSONObject("{"+data+"}");
						headers.addAll(jsonObject.keySet());
						actualData.put(jsonObject);
					}

					if(data.contains("*ProfileData Start*")){
						dataStart = true;
					}
				}

			} else {

				System.out.println("Executing script to get data ::");
				ProcessBuilder processBuilder = new ProcessBuilder();
				String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
						+ " com.databuck.mapr.hive.ReadProfileData " + hostURI + ":" + port + " " + database + " " + queryOrTable;

				System.out.println("ShellCommand for Hive DataSet- " + shellCommand);
				processBuilder.command("bash", "-c", shellCommand);

				Process process = processBuilder.start();
				StringBuilder output = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				boolean resultStart = false;
				List<String> rawData = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
					// Start adding data to raw data
					if (resultStart && !line.contains("*ProfileData End*")) {
						rawData.add(line);
					}
					// Start when ProfileData printing start
					if (line.contains("*ProfileData Start*")) {
						resultStart = true;
						rawData.add(line);
					} else if(line.contains("*ProfileData End*")){
						resultStart = false;
						rawData.add(line);
						break;
					}
				}

				int exitVal = process.waitFor();
				System.out.println("exitVal :: "+exitVal);
				System.out.println(output);
				if (exitVal == 0) {
					System.out.println("Success!");
					boolean dataStart=false;
					for(String data: rawData){
						if(data.contains("*ProfileData End*")){
							dataStart = false;
						}
						if(dataStart){
							JSONObject jsonObject = new JSONObject("{"+data+"}");
							headers.addAll(jsonObject.keySet());
							actualData.put(jsonObject);
						}

						if(data.contains("*ProfileData Start*")){
							dataStart = true;
						}
					}
				} else {
					// abnormal...
					System.out.println("Failure!");
					throw new Exception("Failed to get data using shell");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		json.put("header", headers);
		json.put("data", actualData);
		return json;
	}

	public JSONObject validateQuery(ValidateQuery validateQuery, ListDataSchema listDataSchema) {
		JSONObject json = new JSONObject();
		String hostURI = listDataSchema.getIpAddress();
		String port = listDataSchema.getPort();
		String tablename = validateQuery.getTableName();
		String queryString = validateQuery.getQueryString();
		String database = listDataSchema.getDatabaseSchema();
		String domainName = "";
		String isQueryEnabled = validateQuery.getIsQueryEnabled();
		String whereCondition = validateQuery.getWhereCondition();
		String userlogin = listDataSchema.getUsername();
		String password = listDataSchema.getPassword();
		boolean isQueryValid=false;
		String errorMessage="";
		try {

			domainName = iTaskDAO.getDomainNameById(listDataSchema.getDomainId().longValue());

			String databuckHome = "/opt/databuck";
			if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null && !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			System.out.println("DATABUCK_HOME:" + databuckHome);

			// Check if query enabled
			String queryOrTable = "";
			if(isQueryEnabled.equalsIgnoreCase("N") && !whereCondition.isEmpty()){
				queryOrTable = " 'select * from " + listDataSchema.getDatabaseSchema() + "."
						+ validateQuery.getTableName() + " where " + whereCondition + " limit 1' ";
			}else if(queryString!= null && !queryString.equals("")){
				queryOrTable = " 'select * from ("+queryString + ") AS a limit 1' ";
			}
			String clusterPropertyCategory = listDataSchema.getClusterPropertyCategory();
			System.out.println("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

			if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& (!clusterPropertyCategory.equalsIgnoreCase("cluster")
					&& !clusterPropertyCategory.equalsIgnoreCase("local"))) {

				String token = remoteClusterAPIService.generateRemoteClusterAPIToken(listDataSchema.getIdDataSchema());

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);

				List<String> rawData = remoteClusterAPIService.getProfileData(hostURI, database, userlogin,
						password, port, domainName, queryOrTable, encryptedToken, listDataSchema.getIdDataSchema());
				boolean exceptionStart = false;
				if(rawData != null){
					for (String data : rawData) {
						if (exceptionStart) {
							isQueryValid = false;
							errorMessage = data;
							break;
						}
						if (data.contains("*Exception Start*")) {
							exceptionStart = true;
						}
					}
				}
				if(!exceptionStart){
					isQueryValid = true;
				}

			} else {
				System.out.println("Executing script to get data ::");
				ProcessBuilder processBuilder = new ProcessBuilder();
				String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
						+ " com.databuck.mapr.hive.ReadProfileData " + hostURI + ":" + port + " " + database + " "
						+ queryOrTable;
				System.out.println("ShellCommand for Hive DataSet- " + shellCommand);
				processBuilder.command("bash", "-c", shellCommand);

				Process process = processBuilder.start();
				StringBuilder output = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				boolean resultStart = false;
				List<String> rawData = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
					// Start adding data to raw data
					rawData.add(line);
				}

				int exitVal = process.waitFor();
				System.out.println("exitVal :: " + exitVal);
				System.out.println(output);
				if (exitVal == 0) {
					System.out.println("Success!");
					boolean exceptionStart = false;
					for (String data : rawData) {
						if (exceptionStart) {
							isQueryValid = false;
							errorMessage = data;
							break;
						}

						if (data.contains("*Exception Start*")) {
							exceptionStart = true;
						}
					}
					if(!exceptionStart){
						isQueryValid = true;
					}
				} else {
					// abnormal...
					System.out.println("Failure!");
					throw new Exception("Failed to get table name using shell");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.put("isQueryValid", isQueryValid);
		json.put("errorMessage", dataTemplateAddNewDAO.getUserMessageFromExp(errorMessage));
		return json;
	}

	public JSONObject getProfileDataForHive(ListDataSource listDataSource, listDataAccess listDataAccess, long idDataSchema,
									 int limit) {
		Connection con = null;
		JSONObject json = new JSONObject();
		List<ListDataSchema> listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
		String hostURI = listDataSchema.get(0).getIpAddress();
		String username = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String port = listDataSchema.get(0).getPort();
		String tableName = listDataAccess.getFolderName();
		String queryString = listDataAccess.getQueryString();
		String database = listDataSchema.get(0).getDatabaseSchema();
		String schemaType = listDataSchema.get(0).getSchemaType().trim();
		String principle = listDataSchema.get(0).getDomain();
		String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
		String trustPassword = listDataSchema.get(0).getTrustPassword();
		String keytab = listDataSchema.get(0).getKeytab();
		String krb5conf = listDataSchema.get(0).getKrb5conf();
		String jksPath = listDataSchema.get(0).getJksPath();
		String sslEnb = listDataSchema.get(0).getSslEnb();
		try {
			con = getHiveConnection(schemaType, hostURI, database, username, password, port, sslEnb,
					sslTrustStorePath, trustPassword, true, keytab, krb5conf, principle, jksPath);

			Statement stmt = con.createStatement();
			String query = "";
			if(queryString.equals("")){
				query = "select * from " + database+"."+tableName + " limit "+limit;
			}else if(queryString!= null && !queryString.equals("")){
				query = "select * from (" + queryString + ") AS a limit "+limit;
			}
			System.out.println("query=" + query);
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			List<String> headers = new ArrayList<>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				headers.add(columnName);
			}
			json.put("header", headers);
			ResultSet resultSet = stmt.executeQuery(query);
			List<Map<String, Object>> actualData = new ArrayList<>();
			while (resultSet.next()) {
				Map<String, Object> objectMap = new HashMap<>();
				for (String header : headers) {
					String data = "";
					if (resultSet.getObject(header) != null) {
						data = resultSet.getObject(header).toString();
					}
					objectMap.put(header, data);
				}
				actualData.add(objectMap);
			}
			json.put("data", actualData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public JSONObject validateQueryForHive(ValidateQuery validateQuery, ListDataSchema listDataSchema) {
		Connection con = null;
		JSONObject json = new JSONObject();
		String hostURI = listDataSchema.getIpAddress();
		String port = listDataSchema.getPort();
		String queryString = validateQuery.getQueryString();
		String database = listDataSchema.getDatabaseSchema();
		String isQueryEnabled = validateQuery.getIsQueryEnabled();
		String whereCondition = validateQuery.getWhereCondition();
		String username = listDataSchema.getUsername();
		String password = listDataSchema.getPassword();
		String schemaType = listDataSchema.getSchemaType().trim();
		boolean isQueryValid=false;
		String errorMessage="";
		String principle = listDataSchema.getDomain();
		String sslTrustStorePath = listDataSchema.getSslTrustStorePath();
		String trustPassword = listDataSchema.getTrustPassword();
		String keytab = listDataSchema.getKeytab();
		String krb5conf = listDataSchema.getKrb5conf();
		String jksPath = listDataSchema.getJksPath();
		String sslEnb = listDataSchema.getSslEnb();

		try {
			con = getHiveConnection(schemaType, hostURI, database, username, password, port, sslEnb,
					sslTrustStorePath, trustPassword, true, keytab, krb5conf, principle, jksPath);

			Statement stmt = con.createStatement();
			String query = "";
			if (isQueryEnabled.equalsIgnoreCase("N") && !whereCondition.isEmpty()) {
				query = "select * from " + listDataSchema.getDatabaseSchema() + "."
						+ validateQuery.getTableName() + " where " + whereCondition + " limit 1";
			} else if (queryString != null && !queryString.equals("")) {
				query = "select * from (" + queryString + ") AS a limit 1";
			}
			System.out.println("query=" + query);
			ResultSet rs=stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int column_count = rsmd.getColumnCount();

			Set<String> columnNames = new HashSet<>();
			Set<String> duplicateColumns = new HashSet<>();
			int duplicateCount = 0;
			for (int i = 1; i <= column_count; i++) {
				String columnName = rsmd.getColumnName(i);

				if (columnNames.contains(columnName)) {
					duplicateCount++;
					duplicateColumns.add(columnName);
				} else {
					columnNames.add(columnName);
				}
			}

			if(column_count>1 && duplicateCount == 0){
				isQueryValid = true;
				errorMessage="";
			} else if (duplicateCount > 0) {
				isQueryValid = false;
				errorMessage="Duplicate columns ("+String.join(",", duplicateColumns)+") found. Please provide distinct column names.";
			} else {
				isQueryValid = false;
				errorMessage="The statement did not return a result set";
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception :: "+e.getMessage());
			isQueryValid = false;
			errorMessage = e.getMessage();
		}
		json.put("isQueryValid", isQueryValid);
		json.put("errorMessage", errorMessage);
		return json;
	}

	public JSONObject validateQueryForHiveDerivedTemplate(DerivedTemplateValidateQuery validateQuery, ListDataSchema listDataSchema) {
		Connection con = null;
		JSONObject json = new JSONObject();
		String hostURI = listDataSchema.getIpAddress();
		String port = listDataSchema.getPort();
		String queryString = validateQuery.getQueryString();
		String database = listDataSchema.getDatabaseSchema();
		String username = listDataSchema.getUsername();
		String password = listDataSchema.getPassword();
		String schemaType = listDataSchema.getSchemaType().trim();
		boolean isQueryValid=false;
		String errorMessage="";
		String principle = listDataSchema.getDomain();
		String sslTrustStorePath = listDataSchema.getSslTrustStorePath();
		String trustPassword = listDataSchema.getTrustPassword();
		String keytab = listDataSchema.getKeytab();
		String krb5conf = listDataSchema.getKrb5conf();
		String jksPath = listDataSchema.getJksPath();
		String sslEnb = listDataSchema.getSslEnb();

		try {
			con = getHiveConnection(schemaType, hostURI, database, username, password, port, sslEnb,
					sslTrustStorePath, trustPassword, true, keytab, krb5conf, principle, jksPath);

			Statement stmt = con.createStatement();
			String query = "";
			if (queryString != null && !queryString.equals("")) {
				query = "select * from (" + queryString + ") AS a limit 1";
				System.out.println("query=" + query);
				ResultSet rs = stmt.executeQuery(query);
				isQueryValid = true;
				errorMessage = "";
			} else{
				errorMessage = "Please provide query.";
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception :: "+e.getMessage());
			errorMessage = e.getMessage();
		}
		json.put("isQueryValid", isQueryValid);
		json.put("errorMessage", errorMessage);
		return json;
	}
	
	

}