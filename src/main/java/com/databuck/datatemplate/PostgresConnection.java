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

@Repository
public class PostgresConnection {
	public Map<String, String> readTablesFromPostgres(String uri, String databaseAndSchema, String username,
			String password, String tablename, String queryString, String port, String sslEnb) {
		Map<String, String> tableData = new java.util.LinkedHashMap<String, String>();
		String[] dbAndSchema = databaseAndSchema.split(",");
		String schemaName = "public";

		String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0]+ "?currentSchema=" + schemaName;

		if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
			url = url + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		}

		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = null;
			if(queryString.equals("")){
				query = "select * from " + schemaName+"."+tablename + " limit 1";
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString + " limit 1";
			}
			/*if(!tablename.equals("")){
				query = "select * from " + schemaName+"."+tablename + " limit 1";
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString + " limit 1";
			}*/
			
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

	public List<String> readPrimaryKeyColumnsFromPostgres(String uri, String databaseAndSchema, String username,
			String password, String database, String tablename, String port) {
		
		String[] dbAndSchema = database.split(",");
		String schemaName = "public";
		
		String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0];
		if(dbAndSchema.length > 1 && dbAndSchema[1].length() > 0 ){
			url = url+"?currentSchema="+dbAndSchema[1]+"&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
			schemaName = dbAndSchema[1];
		}else{			
			url = url + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		}
		Connection con = null;
		List<String> primaryKeyCols = new ArrayList<String>();
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			// read primary key columns
			ResultSet rs_p = stmt.executeQuery("SELECT c.column_name, c.ordinal_position"+
				" FROM information_schema.key_column_usage AS c "+
				" LEFT JOIN information_schema.table_constraints AS t "+
				" ON t.constraint_name = c.constraint_name "+
				" WHERE t.table_name = '"+schemaName+"."+tablename+"' AND t.constraint_type = 'PRIMARY KEY';");

			while (rs_p.next()) {
				primaryKeyCols.add(rs_p.getString(1));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKeyCols;
	}

	public List<String> getListOfTableNamesFromPostgres(String uri, String username, String password, String port, String database, String sslEnb) {
		List<String> tableNameData = new ArrayList<String>();
		String[] dbAndSchema = database.split(",");
		String schemaName = "public";

		if(dbAndSchema.length > 1 && dbAndSchema[1].length() > 0 ){
			schemaName = dbAndSchema[1];
		}
		String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0]+ "?currentSchema=" + schemaName;

		if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
			url = url + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		}

		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "SELECT table_name FROM information_schema.tables "+
						"WHERE table_schema='"+schemaName+"' AND table_type='BASE TABLE'";
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("table_name"));
				System.out.println("table data:" + data.getString("table_name"));
			}
			
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public ResultSet getTableDataFromPostgres(String uri, String username, String password, String port, String database, String tablename, String sslEnb)
			throws Exception {
		String[] dbAndSchema = database.split(",");
		String schemaName = "public";

		if(dbAndSchema.length > 1 && dbAndSchema[1].length() > 0 ){
			schemaName = dbAndSchema[1];
		}
		String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0]+ "?currentSchema=" + schemaName;

		if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
			url = url + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		}

		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "select * from "+ schemaName+"."+tablename + " order by random() limit 10000";

			System.out.println("Query for fetching table data:" + query);
			return stmt.executeQuery(query);
		} catch (Exception e) {
			throw new Exception("SQL Exception: Not able to load driver class.");
		}
	}

	/*public String getOneDateRecordForDateFormat(String uri, String database, String username, String password,
			String tablename, String port, String columnName) {
		try {
			String url = "jdbc:oracle:thin:@" + uri + ":" + port;

			Connection con = null;

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "select * from " + tablename + " where ROWNUM <= 1";
			ResultSet executeQuery = stmt.executeQuery(query);
			while (executeQuery.next()) {
				return executeQuery.getString(columnName);
			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}*/
}
