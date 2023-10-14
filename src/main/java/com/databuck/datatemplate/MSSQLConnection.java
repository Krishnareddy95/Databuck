package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ValidateQuery;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IDataTemplateAddNewDAO;

@Component
public class MSSQLConnection {
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	public Object[] readTablesFromMSSQL(String uri, String databaseAndSchema, String username, String password,
			String tablename, String queryString, String port) {
		Map<String, String> tableData = new LinkedHashMap<String, String>();
		List<String> primaryCols = new ArrayList<String>();
		try {
			String url = "jdbc:sqlserver://" + uri + ":" + port + ";encrypt=true;trustServerCertificate=true;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			// System.out.println("tableNameas12 :" + databaseAndSchema + "." + tablename);
			String query = "";
			if (queryString.equals("") && (databaseAndSchema != null && databaseAndSchema.length() != 0)) {
				tablename = databaseAndSchema + "." + tablename;
				query = "select TOP 0  * from  " + tablename;
			} else if (queryString != null && !queryString.equals("")) {
				// query = "select TOP 1 * from ("+ queryString +")";
				query = queryString;
			}

			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				tableData.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
			}

			if (queryString == null || queryString.equals("")) {
				String schema = databaseAndSchema.split("\\.")[1];
				String database = databaseAndSchema.split("\\.")[0];
				String tableName_p = tablename.split("\\.")[2];
				String primaryKeySql = "SELECT Col.Column_Name from  INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col"
						+ " WHERE Col.Constraint_Name = Tab.Constraint_Name AND Col.Table_Name = Tab.Table_Name AND Constraint_Type = 'PRIMARY KEY' AND "
						+ "Col.TABLE_SCHEMA='" + schema + "' AND Col.TABLE_CATALOG='" + database
						+ "' AND Col.Table_Name = '" + tableName_p + "'";

				System.out.println(primaryKeySql);

				ResultSet rs_p = stmt.executeQuery(primaryKeySql);
				while (rs_p.next()) {
					System.out.println("primaryKeySql in while");
					primaryCols.add(rs_p.getString(1));
				}
				System.out.println("primaryCols -mssql" + primaryCols);
				/*
				 * while(resultSet.next()) {
				 * tableData.put(resultSet.getString("COLUMN_NAME"),resultSet.
				 * getString("DATA_TYPE")); }
				 */
				stmt.close();
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[] { tableData, primaryCols };
	}

	public List<String> readPrimaryKeysFromMSSQL(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port) {
		List<String> primaryCols = new ArrayList<String>();
		try {
			String url = "jdbc:sqlserver://" + uri + ":" + port + ";encrypt=true;trustServerCertificate=true;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			System.out.println("tableName   :" + databaseAndSchema + "." + tablename);
			if (databaseAndSchema != null && databaseAndSchema.length() != 0) {
				tablename = databaseAndSchema + "." + tablename;
			}
			String schema = databaseAndSchema.split("\\.")[1];
			String database = databaseAndSchema.split("\\.")[0];
			String tableName_p = tablename.split("\\.")[2];
			String primaryKeySql = "SELECT Col.Column_Name from  INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col"
					+ " WHERE Col.Constraint_Name = Tab.Constraint_Name AND Col.Table_Name = Tab.Table_Name AND Constraint_Type = 'PRIMARY KEY' AND "
					+ "Col.TABLE_SCHEMA='" + schema + "' AND Col.TABLE_CATALOG='" + database
					+ "' AND Col.Table_Name = '" + tableName_p + "'";

			System.out.println(primaryKeySql);

			ResultSet rs_p = stmt.executeQuery(primaryKeySql);
			while (rs_p.next()) {
				System.out.println("primaryKeySql in while");
				primaryCols.add(rs_p.getString(1));
			}
			System.out.println("primaryCols -mssql" + primaryCols);
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryCols;
	}

	public List<String> getListOfTableNamesFromMsSql(String uri, String username, String password, String port,
			String databaseAndSchema) {
		List<String> tableNameData = new ArrayList<String>();
		String url = "jdbc:sqlserver://" + uri + ":" + port + ";encrypt=true;trustServerCertificate=true;";

		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String database = databaseAndSchema.split("\\.")[0];
			String schema = databaseAndSchema.split("\\.")[1];
			/*
			 * String query =
			 * "SELECT TABLE_NAME FROM "+database+".INFORMATION_SCHEMA.TABLES " +
			 * "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='"+schema+"'";
			 */
			String query = "SELECT TABLE_NAME FROM " + database + ".INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE in ('BASE TABLE','VIEW') AND TABLE_SCHEMA='" + schema + "'";
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("TABLE_NAME"));
				// System.out.println("table data:" + data.getString("TABLE_NAME"));
			}
			con.close();
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}
	
	
	public List<String> getListOfTableNamesFromMsSqlAzure(String uri, String username, String password, String port,
			String databaseAndSchema,String azureAuthenticationType) {
		List<String> tableNameData = new ArrayList<String>();
		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String database = databaseAndSchema.split("\\.")[0];
			String schema = databaseAndSchema.split("\\.")[1];
			String db_connect_string = "";
			if(azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryPassword")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+database+";user="+username+";password="+password+";"
					+ "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"
					+ "loginTimeout=30;authentication=ActiveDirectoryPassword";
			}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryIntegrated")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+database+";encrypt=true;trustServerCertificate=false;"
						+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
						+ "authentication=ActiveDirectoryIntegrated";
			}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryDefault")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+database+";user="+username+";password="+password+";"//CloudSA8d3b0977@databuck
						+ "encrypt=true;trustServerCertificate=false;"
						+ "hostNameInCertificate=*.database.windows.net;"
						+ "loginTimeout=30;";
			}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryManagedIdentity")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";authentication=ActiveDirectoryManagedIdentity;database="+database;
			}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryServicePrinicipal")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+database+";user="+username+";password="+password+";Authentication=ActiveDirectoryServicePrincipal";
			}else if (azureAuthenticationType.equalsIgnoreCase("NotSpecified")) {
				db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+database+";user="+username+";password="+password+";"
						+ "integratedSecurity=true;trustServerCertificate=true;authenticationScheme=NTLM;authentication=NotSpecified";
			}
			con = DriverManager.getConnection(db_connect_string);

			Statement stmt = con.createStatement();
			/*
			 * String query =
			 * "SELECT TABLE_NAME FROM "+database+".INFORMATION_SCHEMA.TABLES " +
			 * "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='"+schema+"'";
			 */
			String query = "SELECT TABLE_NAME FROM " + database + ".INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE in ('BASE TABLE','VIEW') AND TABLE_SCHEMA='" + schema + "'";
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("TABLE_NAME"));
				// System.out.println("table data:" + data.getString("TABLE_NAME"));
			}
			con.close();
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public ResultSet getTableDataFromMSSQL(String hostURI, String username, String password, String port,
			String selTableName, String databaseSchema) {
		String url = "jdbc:sqlserver://" + hostURI + ":" + port + ";encrypt=true;trustServerCertificate=true;";

		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "SELECT TOP 10000 *  FROM " + databaseSchema + "." + selTableName + " ORDER BY NEWID()";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getOneDateRecordForDateFormat(String hostURI, String database, String username, String password,
			String tableName, String port, String dateColumnName) {
		String url = "jdbc:sqlserver://" + hostURI + ":" + port + ";encrypt=true;trustServerCertificate=true;";

		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "SELECT TOP 1 *  FROM " + database + "." + tableName + " ORDER BY NEWID()";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);
			while (data.next()) {
				return data.getString(dateColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getProfileData(ListDataSource listDataSource, listDataAccess listDataAccess, long idDataSchema,
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
		String databaseSchema = listDataSchema.get(0).getDatabaseSchema();
		String url = "jdbc:sqlserver://" + hostURI + ":" + port + ";encrypt=true;trustServerCertificate=true;";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			if (queryString.equals("")) {
				query = "select TOP " + limit + " * from " + databaseSchema + "." + tableName;
			} else if (queryString != null && !queryString.equals("")) {
				query = "select TOP " + limit + " * from (" + queryString + ") AS a";
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

	public JSONObject validateQuery(ValidateQuery validateQuery, ListDataSchema listDataSchema) {
		Connection con = null;
		JSONObject json = new JSONObject();
		String hostURI = listDataSchema.getIpAddress();
		String username = listDataSchema.getUsername();
		String password = listDataSchema.getPassword();
		String port = listDataSchema.getPort();
		String databaseSchema = listDataSchema.getDatabaseSchema();
		String url = "jdbc:sqlserver://" + hostURI + ":" + port + ";encrypt=true;trustServerCertificate=true;";
		String isQueryEnabled = validateQuery.getIsQueryEnabled();
		String queryString = validateQuery.getQueryString();
		String whereCondition = validateQuery.getWhereCondition();
		boolean isQueryValid = false;
		String errorMessage = "";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			if(isQueryEnabled.equalsIgnoreCase("N") && !whereCondition.isEmpty()){
				query = " select TOP 1 * from " + listDataSchema.getDatabaseSchema() + "."
						+ validateQuery.getTableName() + " where " + whereCondition + " ";
			}else if(queryString!= null && !queryString.equals("")){
				//query = "select TOP 1 * from ("+queryString + ") AS a ";
				query = queryString;
			}
			System.out.println("query="+query);
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

}