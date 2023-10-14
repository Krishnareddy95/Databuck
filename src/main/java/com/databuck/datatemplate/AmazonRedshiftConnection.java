package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Repository;

@Repository
public class AmazonRedshiftConnection {
	public Map<String, String> readTablesFromAmazonRedshift(String uri, String databaseAndSchema, String username,
			String password, String tablename, String port, String queryString) {
		Map<String, String> tableData = new LinkedHashMap<String, String>();
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("com.amazon.redshift.jdbc42.Driver");
			// System.out.println("Connecting to database...");
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + databaseAndSchema, props);
			stmt = con.createStatement();
			String query = "";
			
			if(queryString!= null && !queryString.equals(""))
				query = queryString + " limit 1";
			else 
				query = "select * from " + tablename + " limit 1";
			
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				System.out.println("columnName=" + columnName);
				System.out.println("columnType=" + columnType);
				tableData.put(columnName, columnType);
			}
			con.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return tableData;
	}

	public ResultSet getTableDataFromAmazonRedshift(String uri, String username, String password, String port,
			String selTableName, String databaseAndSchema, String domain) {
		try {
			Class.forName("com.amazon.redshift.jdbc42.Driver");
			// System.out.println("Connecting to database...");
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			Connection con = DriverManager
					.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + databaseAndSchema, props);
			Statement stmt = con.createStatement();
			String query = "SELECT *  FROM " + selTableName + " order by random() limit 10000";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);
			return data;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public List<String> getListOfTableNamesFromAmazonRedshift(String uri, String username, String password, String port,
			String domain, String database) {
		List<String> tableNameData = new ArrayList<String>();
		try {
			Class.forName("com.amazon.redshift.jdbc42.Driver");
			// System.out.println("Connecting to database...");
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			Connection con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database, props);
			Statement stmt = con.createStatement();
			String query = "select distinct(tablename) from pg_table_def where schemaname = 'public';";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);
			while (data.next()) {
				tableNameData.add(data.getString(1));
				System.out.println("table data:" + data.getString(1));
			}
			
			con.close();
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}
}
