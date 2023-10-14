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

import org.springframework.stereotype.Component;

@Component
public class MYSQLConnection {

	public Object[] readTablesFromMYSQL(String uri, String databaseAndSchema, String username, String password,
			String tablename, String queryString, String portName,String sslEnb) {
		
		Map<String,String> tableData=new LinkedHashMap<String,String>(); 
		List<String> primaryCols = new ArrayList<String>();
		
		try {
			String url = "jdbc:mysql://" + uri + ":" + portName;
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;

			if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y")){
				url = "jdbc:mysql://" + uri + ":" + portName +"/"+databaseAndSchema+"?verifyServerCertificate=false&useSSL=true";
			}

			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			System.out.println("tableName   :" + databaseAndSchema + "." + tablename);
			String query = "";
			if(queryString.equals("") && (databaseAndSchema != null && databaseAndSchema.length() != 0)){	
				tablename = databaseAndSchema + "." + tablename;
				query = "select * from  " + tablename+" limit 1";
			}else if(queryString!= null && !queryString.equals("")){				
				//query = "select TOP 1  * from ("+ queryString +")";
				query = queryString+" limit 1";
			}
			
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				tableData.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
			}
			
			if(queryString == null || queryString.equals("")){
				/*String schema = databaseAndSchema.split("\\.")[1];
				String database = databaseAndSchema.split("\\.")[0];*/
				String tableName_p = tablename.split("\\.")[1];
				String primaryKeySql = "SELECT k.`COLUMN_NAME` FROM `information_schema`.`TABLE_CONSTRAINTS` t "
						+ "JOIN `information_schema`.`KEY_COLUMN_USAGE` k USING (`CONSTRAINT_NAME`, `TABLE_SCHEMA`, `TABLE_NAME`) "
						+ "WHERE t.`CONSTRAINT_TYPE` = 'PRIMARY KEY' "
						+ "AND t.`TABLE_SCHEMA` = '"+databaseAndSchema+"' AND t.`TABLE_NAME` = '"+tableName_p+"'";
	
				System.out.println(primaryKeySql);
	
				ResultSet rs_p = stmt.executeQuery(primaryKeySql);
				while (rs_p.next()) {
					System.out.println("primaryKeySql in while");
					primaryCols.add(rs_p.getString(1));
				}
				System.out.println("primaryCols -MYSQL" + primaryCols);
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
	
	// Created by -- Abhijeet For Read without Primary Key Field 
	public Object[] readTablesFromMYSQLNOPrimaryField(String uri, String databaseAndSchema, String username, String password,
			String tablename, String queryString, String portName,String sslEnb) {
		
		Map<String,String> tableData=new LinkedHashMap<String,String>(); 
		List<String> primaryCols = new ArrayList<String>();
		
		try {
			String url = "jdbc:mysql://" + uri + ":" + portName;
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;

			if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y")){
				url = "jdbc:mysql://" + uri + ":" + portName +"/"+databaseAndSchema+"?verifyServerCertificate=false&useSSL=true";
			}

			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			System.out.println("tableName   :" + databaseAndSchema + "." + tablename);
			String query = "";
			if(queryString.equals("") && (databaseAndSchema != null && databaseAndSchema.length() != 0)){	
				tablename = databaseAndSchema + "." + tablename;
				query = "select * from  " + tablename+" limit 1";
			}else if(queryString!= null && !queryString.equals("")){				
				//query = "select TOP 1  * from ("+ queryString +")";
				query = queryString+" limit 1";
			}
			
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for (int i = 2; i <= metaData.getColumnCount(); i++) {
				tableData.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
			}
			
			if(queryString == null || queryString.equals("")){
				/*String schema = databaseAndSchema.split("\\.")[1];
				String database = databaseAndSchema.split("\\.")[0];*/
				String tableName_p = tablename.split("\\.")[1];
				String primaryKeySql = "SELECT k.`COLUMN_NAME` FROM `information_schema`.`TABLE_CONSTRAINTS` t "
						+ "JOIN `information_schema`.`KEY_COLUMN_USAGE` k USING (`CONSTRAINT_NAME`, `TABLE_SCHEMA`, `TABLE_NAME`) "
						+ "WHERE t.`CONSTRAINT_TYPE` = 'PRIMARY KEY' "
						+ "AND t.`TABLE_SCHEMA` = '"+databaseAndSchema+"' AND t.`TABLE_NAME` = '"+tableName_p+"'";
	
				System.out.println(primaryKeySql);
	
				ResultSet rs_p = stmt.executeQuery(primaryKeySql);
				while (rs_p.next()) {
					System.out.println("primaryKeySql in while");
					primaryCols.add(rs_p.getString(1));
				}
				System.out.println("primaryCols -MYSQL" + primaryCols);
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
	
	
	
	/*public static void main(String[] args) {
		new MYSQLConnection().readTablesFromMYSQL("172.28.25.111", "databuck_app_db_project", "root", "root", "listdataschema", "3306");
	}*/
	public ResultSet getTableDataFromMYSQL(String hostURI, String username, String password, String port,
			String selTableName, String databaseSchema,String sslEnb) {
		String url = "jdbc:mysql://" + hostURI+ ":" + port+"/"+databaseSchema ;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y"))
				url= url+"?verifyServerCertificate=false&useSSL=true";

			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = "SELECT *  FROM "+selTableName+ " limit 10000";
			System.out.println("query="+query);
			ResultSet data = stmt.executeQuery(query);

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<String> readPrimaryKeysFromMYSQL(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port,String sslEnb) {
		List<String> primaryCols = new ArrayList<String>();
		try {
			String url = "jdbc:mysql://" + uri + ":" + port;
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;

			if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y")){
				url = "jdbc:mysql://" + uri + ":" + port +"/"+databaseAndSchema +"?verifyServerCertificate=false&useSSL=true";
			}

			con = DriverManager.getConnection(url, username, password);
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
			System.out.println("primaryCols -mysql" + primaryCols);
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryCols ;
	}
	public List<String> getListOfTableNamesFromMYSql(String uri, String username, String password, String port, String databaseAndSchema, String sslEnb) {
		List<String> tableNameData = new ArrayList<String>();
		String url = "jdbc:mysql://" + uri + ":" + port;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y")){
				url = "jdbc:mysql://" + uri + ":" + port +"/"+databaseAndSchema +"?verifyServerCertificate=false&useSSL=true";
			}
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			/*String database = databaseAndSchema.split("\\.")[0];
			
			System.out.println("databaseAndSchema =>"+databaseAndSchema);
			
			System.out.println("database =>"+database);
			
			String schema = databaseAndSchema.split("\\.")[1];
			
			System.out.println("schema =>"+schema);*/
			
			String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_SCHEMA='"+databaseAndSchema+"'";
			
			System.out.println("query ->"+query);
			
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("TABLE_NAME"));
				System.out.println("table data:" + data.getString("TABLE_NAME"));
			}
			
			con.close();
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}
}
