package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MsSqlActiveDirectoryConnection {
	public Object[] readTablesFromMSSQL(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port, String domain,String queryString) {
		LinkedHashMap<String, String> tableData = new LinkedHashMap<String, String>();
		List<String> primaryCols = new ArrayList<String>();
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
				+ domain.trim();
		System.out.println(db_connect_string);
		try (Connection con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim())) {
			if (databaseAndSchema.contains(".")) {
				tablename = databaseAndSchema + "." + tablename;
			}
			Statement stmt = con.createStatement();
			String query = null;
			if(queryString.equals("")){
				//query = "select * from " + schemaName+"."+tablename + " limit 1";
				query = "SELECT TOP 0 * FROM " + tablename;
				System.out.println("query performed ->"+query);
			}else if(queryString!= null && !queryString.equals("")){
				//query = queryString + " limit 1"; not working java.sql.SQLException: Incorrect syntax near '1'
				query = queryString ;
				System.out.println("query performed ->"+query);
			}
			
			try (Statement s = con.createStatement()) {
			
				try (ResultSet rs = s.executeQuery(query)) {
					ResultSetMetaData metaData = rs.getMetaData();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						tableData.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
						System.out.print(metaData.getColumnName(i) + "    ");
						System.out.println(metaData.getColumnTypeName(i) + "    ");
					}
					/*String schema = databaseAndSchema.split("\\.")[1];
					String database = databaseAndSchema.split("\\.")[0];
					String tableName_p = tablename.split("\\.")[2];
					String primaryKeySql = "SELECT Col.Column_Name from  INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col"
							+ " WHERE Col.Constraint_Name = Tab.Constraint_Name AND Col.Table_Name = Tab.Table_Name AND Constraint_Type = 'PRIMARY KEY' AND "
							+ "Col.TABLE_SCHEMA='" + schema + "' AND Col.TABLE_CATALOG='" + database
							+ "' AND Col.Table_Name = '" + tableName_p + "'";
					ResultSet rs_p = s.executeQuery(primaryKeySql);

					while (rs_p.next()) {
						primaryCols.add(rs_p.getString(1));
					}

					
					  while(rs.next()) { for(int i=1; i <=
					  metaData.getColumnCount();i++) {
					  System.out.print(rs.getString(i)+"     "); }
					  System.out.println(); }*/
					 
					return new Object[] { tableData, primaryCols };
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[] { tableData, primaryCols };
	}

	public List<String> getPrimaryKey(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port, String domain) {
		List<String> primaryCols = new ArrayList<String>();
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
					+ domain.trim();
			System.out.println(db_connect_string);
			Connection con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());

			Statement s = con.createStatement();
			if (databaseAndSchema.contains(".")) {
				tablename = databaseAndSchema + "." + tablename;
			}
			String schema = databaseAndSchema.split("\\.")[1];
			String database = databaseAndSchema.split("\\.")[0];
			String tableName_p = tablename.split("\\.")[2];
			String primaryKeySql = "SELECT Col.Column_Name from  INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col"
					+ " WHERE Col.Constraint_Name = Tab.Constraint_Name AND Col.Table_Name = Tab.Table_Name AND Constraint_Type = 'PRIMARY KEY' AND "
					+ "Col.TABLE_SCHEMA='" + schema + "' AND Col.TABLE_CATALOG='" + database
					+ "' AND Col.Table_Name = '" + tableName_p + "'";
			ResultSet rs_p = s.executeQuery(primaryKeySql);

			while (rs_p.next()) {
				primaryCols.add(rs_p.getString(1));
			}

			return primaryCols;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryCols;
	}

	public List<String> getListOfTableNamesFromMsSqlActiveDirectory(String uri, String username, String password,
			String port, String domain, String databaseAndSchema) {
		List<String> tableNameData = new ArrayList<String>();
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
					+ domain.trim();
			System.out.println(db_connect_string);
			Connection con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
			Statement stmt = con.createStatement();
			
			String database = databaseAndSchema.split("\\.")[0];
			String schema = databaseAndSchema.split("\\.")[1];
			String query = "SELECT TABLE_NAME FROM "+database+".INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='"+schema+"'";
			System.out.println(query);
			ResultSet data = stmt.executeQuery(query);
			while (data.next()) {
				tableNameData.add(data.getString("TABLE_NAME"));
				System.out.println("table data:" + data.getString("TABLE_NAME"));
			}
			return tableNameData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getTableDataFromMSSQLActiveDirectory(String uri, String username, String password, String port,
			String selTableName, String databaseAndSchema, String domain) {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
					+ domain.trim();
			System.out.println(db_connect_string);
			Connection con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
			Statement stmt = con.createStatement();
			String query = "SELECT TOP 10000 *  FROM "+databaseAndSchema+"." + selTableName + " ORDER BY NEWID()";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getOneDateRecordForDateFormat(String uri, String databaseAndSchema, String username, String password,
			String tableName, String port, String dateColumnName, String domain) {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
					+ domain.trim();
			System.out.println(db_connect_string);
			Connection con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
			Statement stmt = con.createStatement();
			String query = "SELECT TOP 1 *  FROM "+databaseAndSchema+"." + tableName + " ORDER BY NEWID()";
			System.out.println("query=" + query);
			ResultSet data = stmt.executeQuery(query);
			while(data.next()){
				return data.getString(dateColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
