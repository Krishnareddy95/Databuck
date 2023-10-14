package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class OracleConnection {
	public Map<String, String> readTablesFromOracle(String uri, String databaseAndSchema, String username,
			String password, String tablename, String queryString, String port) {
		Map<String, String> tableData = new java.util.LinkedHashMap<String, String>();
		String url = "jdbc:oracle:thin:@" + uri + ":" + port;

		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			if(queryString!= null && !queryString.equals("")){
				if(!queryString.toLowerCase().contains("where")){
					query = queryString+" where rownum <= 1";
				}else{
					query = queryString+" and rownum <= 1";
				}
			} else {
				query = "select * from " + databaseAndSchema + "." + tablename + " where ROWNUM <= 1";
			}
			
			System.out.println("\n====> Query to fetch metadata: "+ query);
			
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				System.out.println("columnName=" + columnName);
				System.out.println("columnType=" + columnType);
				tableData.put(columnName, columnType);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableData;
	}

	public List<String> readPrimaryKeyColumnsFromOracle(String uri, String databaseAndSchema, String username,
			String password, String tablename, String port) {
		String url = "jdbc:oracle:thin:@" + uri + ":" + port;
		Connection con = null;
		List<String> primaryKeyCols = new ArrayList<String>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			// read primary key columns
			ResultSet rs_p = stmt.executeQuery("SELECT column_name FROM all_cons_columns WHERE constraint_name = "
					+ "(  SELECT constraint_name FROM user_constraints   WHERE UPPER('" + tablename + "') = "
					+ " UPPER('" + tablename + "') AND CONSTRAINT_TYPE = 'P' )");

			while (rs_p.next()) {
				primaryKeyCols.add(rs_p.getString(1));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKeyCols;
	}

	public List<String> getListOfTableNamesFromOracle(String uri, String username, String password, String port, String schema) {
		List<String> tableNameData = new ArrayList<String>();
		String url = "jdbc:oracle:thin:@" + uri + ":" + port;
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);
			
			Statement stmt = con.createStatement();
			String query = "select table_name from all_tables where owner='"+ schema +"'";
			
			System.out.println("\n=====> Query to fetch table list: "+ query);
			
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("table_name"));
				System.out.println("table data:" + data.getString("table_name"));
			}
			
			//Append view names also
			query = "select view_name from all_views where owner='"+ schema +"'";
			
			System.out.println("\n=====> Query to fetch view list: "+ query);
			
			data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("view_name"));
				System.out.println("view data:" + data.getString("view_name"));
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public ResultSet getTableDataFromOracle(String uri, String username, String password, String port, String tablename, String schema)
			throws Exception {
		String url = "jdbc:oracle:thin:@" + uri + ":" + port;

		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "Select * from (select *  from " + schema+"."+tablename
					+ " order by dbms_random.value) where rownum < 10000";

			System.out.println("Query for fetching table data:" + query);
			return stmt.executeQuery(query);
		} catch (Exception e) {
			throw new Exception("SQL Exception: Not able to load driver class.");
		}
	}
	
	public ResultSet getQueryResultsFromOracle(String uri, String username, String password, String port, String queryString)
			throws Exception {
		String url = "jdbc:oracle:thin:@" + uri + ":" + port;

		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			if(!queryString.toLowerCase().contains("where")){
				query = queryString+" where rownum < 10000";
			}else{
				query = queryString+" and rownum < 10000";
			}

			System.out.println("Query for fetching table data:" + query);
			return stmt.executeQuery(query);
		} catch (Exception e) {
			throw new Exception("SQL Exception: Not able to load driver class.");
		}
	}

	public String getOneDateRecordForDateFormat(String uri, String database, String username, String password,
			String tablename, String port, String columnName) {
		try {
			String url = "jdbc:oracle:thin:@" + uri + ":" + port;

			Connection con = null;

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "select * from " + database + "." + tablename + " where ROWNUM <= 1";
			ResultSet executeQuery = stmt.executeQuery(query);
			while (executeQuery.next()) {
				return executeQuery.getString(columnName);
			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}