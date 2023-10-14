package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class VerticaConnection {
	public Object[] verticaconnection(String uri, String databaseAndSchema, String username, String password,
			String tableName, String port,String queryString) {
		List<String> primaryKeyColumns = new ArrayList<String>();
		LinkedHashMap<String, String> tableList = new LinkedHashMap<String, String>();
		// System.out.println("username="+username);
		try {
			int dotPosition = databaseAndSchema.indexOf(".");
			String database = databaseAndSchema;
			if (dotPosition != -1) {
				database = databaseAndSchema.substring(0, dotPosition);
			}
			String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
			String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
			Connection con = null;
			Class.forName("com.vertica.jdbc.Driver");
			// System.out.println(url+" "+username+" "+password);
			con = DriverManager.getConnection(url, username, password);
			// System.out.println("connection executed");
			Statement stmt = con.createStatement();
			// String schema=database.split(".")[1];
			String query = null;
			if(queryString.equals("")) {
			 query = "select column_name,data_type from columns where table_name='" + tableName
					+ "' and table_schema='" + schema + "'";
			System.out.println("query performed :" + query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String column_name = rs.getString("column_name");
				String data_type = rs.getString("data_type");
				System.out.println("column_name=" + column_name);
				System.out.println("data_type=" + data_type);
				tableList.put(column_name, data_type);
			}
			}else if(queryString!= null && !queryString.equals("")) {
			
				query = queryString + " limit 1";
				
			System.out.println("query performed :" + query);
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				System.out.println("columnName=" + columnName);
				System.out.println("columnType=" + columnType);
				tableList.put(columnName, columnType);
			}
			}
			

			System.out.println("tableList=" + tableList);
			String primaryKeySql = "SELECT column_name  FROM primary_keys WHERE table_name='" + tableName
					+ "' and table_schema='" + schema + "'";

			System.out.println(primaryKeySql);
			ResultSet primarykey_rs = stmt.executeQuery(primaryKeySql);

			while (primarykey_rs.next()) {
				primaryKeyColumns.add(primarykey_rs.getString(1));
				System.out.println("primaryKey col :" + primarykey_rs.getString(1));
			}
			stmt.close();
			//rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[] { tableList, primaryKeyColumns };
	}

	public List<String> getPrimaryKeysForVertica(String uri, String databaseAndSchema, String username, String password,
			String tableName, String port) {
		List<String> primaryKeyColumns = new ArrayList<String>();
		try {
			int dotPosition = databaseAndSchema.indexOf(".");
			String database = databaseAndSchema;
			if (dotPosition != -1) {
				database = databaseAndSchema.substring(0, dotPosition);
			}
			String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
			String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
			Connection con = null;
			Class.forName("com.vertica.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();

			String primaryKeySql = "SELECT column_name  FROM primary_keys WHERE table_name='" + tableName
					+ "' and table_schema='" + schema + "'";

			System.out.println(primaryKeySql);
			ResultSet primarykey_rs = stmt.executeQuery(primaryKeySql);

			while (primarykey_rs.next()) {
				primaryKeyColumns.add(primarykey_rs.getString(1));
				System.out.println("primaryKey col :" + primarykey_rs.getString(1));
			}
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKeyColumns;
	}

	public List<String> getListOfTableNamesFromVertica(String uri, String username, String password, String port,
			String databaseAndSchema) {
		try {
			int dotPosition = databaseAndSchema.indexOf(".");
			String database = databaseAndSchema;
			if (dotPosition != -1) {
				database = databaseAndSchema.substring(0, dotPosition);
			}
			String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
			String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
			Connection con = null;
			List<String> tableNameData = new ArrayList<String>();
			Class.forName("com.vertica.jdbc.Driver");

			// System.out.println(url+" "+username+" "+password);
			con = DriverManager.getConnection(url, username, password);
			// System.out.println("connection executed");
			Statement stmt = con.createStatement();
			// String schema=database.split(".")[1];
			String query = "select table_name from tables where table_schema='" + schema + "'";
			System.out.println("query:" + query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String table_name = rs.getString("table_name");
				System.out.println("table_name=" + table_name);
				tableNameData.add(table_name);
			}
			
			stmt.close();
			con.close();
			
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getTableDataFromVertica(String uri, String username, String password, String port,
			String tablename, String databaseAndSchema) {
		try {
			int dotPosition = databaseAndSchema.indexOf(".");
			String database = databaseAndSchema;
			if (dotPosition != -1) {
				database = databaseAndSchema.substring(0, dotPosition);
			}
			String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
			String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
			System.out.println("url=" + url);
			Connection con = null;
			Class.forName("com.vertica.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			String query = "select * from " + tablename + " a order by RANDOM() LIMIT 10000";
			System.out.println("Query for fetching table data:" + query);
			ResultSet executeQuery = stmt.executeQuery(query);

			System.out.println(executeQuery.next());
			return stmt.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getOneDateRecordForDateFormat(String uri, String databaseAndSchema, String username,
			String password, String tablename, String port, String dateColumnName, String domain) {
		try {
			int dotPosition = databaseAndSchema.indexOf(".");
			String database = databaseAndSchema;
			if (dotPosition != -1) {
				database = databaseAndSchema.substring(0, dotPosition);
			}
			String schema = databaseAndSchema.substring(dotPosition + 1, databaseAndSchema.length());
			String url = "jdbc:vertica://" + uri + ":" + port + "/" + database;
			System.out.println("url=" + url);
			Connection con = null;
			Class.forName("com.vertica.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			String query = "select * from " + tablename + " a order by RANDOM() LIMIT 1";
			System.out.println("Query for fetching table data:" + query);
			ResultSet executeQuery = stmt.executeQuery(query);

			System.out.println(executeQuery.next());
			ResultSet executeQuery2 = stmt.executeQuery(query);
			while(executeQuery2.next()){
				return executeQuery2.getString(dateColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
