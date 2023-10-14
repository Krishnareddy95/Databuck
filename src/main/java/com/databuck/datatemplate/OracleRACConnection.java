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

import org.springframework.stereotype.Repository;

import com.databuck.bean.listDataAccess;

@Repository
public class OracleRACConnection {
	public Map<String, String> readTablesFromOracleRAC(String uri, String databaseAndSchema, String username,
			String password, String tablename, String port, String serviceName, String queryString) {
		Map<String, String> tableData = new java.util.LinkedHashMap<String, String>();
		/*
		 * String url =
		 * "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = " + uri +
		 * ")(PORT = " + port + "))(CONNECT_DATA =(SERVER = DEDICATED)" +
		 * "(SERVICE_NAME = " + serviceName + ")))";
		 */
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(uri, username, password);

			Statement stmt = con.createStatement();
			//String query = "select * from " + tablename + " where ROWNUM <= 1";
			
			String query = null;
			if(queryString.equals("")){
				query = "select * from " + tablename + " where ROWNUM <= 1";
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString;
			}

			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				System.out.println("columnName=" + columnName);
				System.out.println("columnType=" + columnType);
				tableData.put(columnName, columnType);
			}
			con.close();
			return tableData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableData;
	}

	public List<String> readPrimaryKeyColumnsFromOracleRAC(String uri, String databaseAndSchema, String username,
			String password, String tablename, String port, String serviceName) {
		String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" + "(HOST = " + uri + ")(PORT = "
				+ port + "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName + ")))";
		Connection con = null;
		List<String> primaryKeyCols = new ArrayList<String>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);
			// System.out.println("connection executed");
			Statement stmt = con.createStatement();
			ResultSet rs_p = stmt.executeQuery("SELECT column_name FROM all_cons_columns WHERE constraint_name = "
					+ "(  SELECT constraint_name FROM user_constraints   WHERE TABLE_NAME = " + " UPPER('" + tablename
					+ "') AND CONSTRAINT_TYPE = 'P' )");

			while (rs_p.next()) {
				primaryKeyCols.add(rs_p.getString(1));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKeyCols;
	}

	public List<String> getListOfTableNamesFromOracleRAC(String uri, String userlogin, String password, String port,
			String database, String domain, String serviceName) {
		List<String> tableNameData = new ArrayList<String>();
		/*
		 * String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" +
		 * "(HOST = " + uri + ")(PORT = " + port +
		 * "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName +
		 * ")))";
		 */
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(uri, userlogin, password);

			Statement stmt = con.createStatement();
			String query = "select table_name from user_tables";
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("table_name"));
				System.out.println("table data:" + data.getString("table_name"));
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public ResultSet getTableDataFromOracleRAC(String uri, String username, String password, String port,
			String databaseSchema, String domain, String serviceName, String selTableName) {
		String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" + "(HOST = " + uri + ")(PORT = "
				+ port + "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName + ")))";
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "Select * from (select *  from " + selTableName
					+ " order by dbms_random.value) where rownum < 10000";

			System.out.println("Query for fetching table data:" + query);
			return stmt.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getOneDateRecordForDateFormat(String hostURI, String database, String username, String password,
			String tableName, String port, String dateColumnName, String serviceName) {
		String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" + "(HOST = " + hostURI + ")(PORT = "
				+ port + "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName + ")))";
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "select * from " + tableName + " where ROWNUM <= 1";
			
			ResultSet executeQuery = stmt.executeQuery(query);
			while(executeQuery.next()){
				return executeQuery.getString(dateColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
