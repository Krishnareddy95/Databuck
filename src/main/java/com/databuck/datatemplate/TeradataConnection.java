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
public class TeradataConnection {
	public Map<String, String> readTablesFromTeradata(String uri, String databaseAndSchema, String username,
			String password, String tablename, String queryString, String port) {
		Map<String, String> tableData = new java.util.LinkedHashMap<String, String>();
		String url = "jdbc:teradata://" + uri +"/TMODE=ANSI,CHARSET=UTF8,TYPE=FASTEXPORT,COLUMN_NAME=ON,MAYBENULL=ON";

		Connection con = null;
		try {
			Class.forName("com.teradata.jdbc.TeraDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			
			if(queryString.equals("")){
				query = "select top 1 * from " +databaseAndSchema+"."+ tablename;
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString;
			}
			/*if(!tablename.equals("")){
				query = "select top 1 * from " +databaseAndSchema+"."+ tablename;
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString;
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

	
	public List<String> getListOfTableNamesFromTeradata(String uri, String username, String password, String port, String database) {
		List<String> tableNameData = new ArrayList<String>();
		String url = "jdbc:teradata://" + uri +"/TMODE=ANSI,CHARSET=UTF8,TYPE=FASTEXPORT,COLUMN_NAME=ON,MAYBENULL=ON";;
		Connection con = null;
		try {
			Class.forName("com.teradata.jdbc.TeraDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "";
			
			query = "select TableName from DBC.TablesV where DatabaseName='" +database+"'";			 
			
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("TableName"));
				System.out.println("table data:" + data.getString("TableName"));
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	/*public ResultSet getTableDataFromTeradata(String uri, String username, String password, String port, String database, String tablename)
			throws Exception {
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
	}	*/
}
