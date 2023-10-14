package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ValidateQuery;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IDataTemplateAddNewDAO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SnowflakeConnection {
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;
	public Map<String, String> readTablesFromSnowflake(String uri, String databaseAndSchema, String username,
			String password, String tablename, String queryString, String port) {
		Map<String, String> tableData = new java.util.LinkedHashMap<String, String>();
		//String url = "jdbc:snowflake://" + uri;
		
		String[] dbAndSchema = databaseAndSchema.split(",");
		String schemaName = "public";
		
		String url = "jdbc:snowflake://"+ uri+"/?db="+dbAndSchema[1]+"&warehouse="+dbAndSchema[0]+"&schema="+dbAndSchema[2];
	

		Connection con = null;
		try {
			Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = null;
			
			if(queryString.equals("")){
				query = "select * from " + dbAndSchema[1]+"."+dbAndSchema[2]+"."+tablename + " limit 1";
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString + " limit 1";
			}
			/*if(!tablename.equals("")){
				query = "select * from " + dbAndSchema[0]+"."+dbAndSchema[1]+"."+tablename + " limit 1";
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

	public List<String> getListOfTableNamesFromSnowflake(String uri, String username, String password, String port,
			String database) {
		List<String> tableNameData = new ArrayList<String>();
		System.out.println("Database============>"+database);
		//String[] dbAndSchema = database.split("\\.");
		String[] dbAndSchema = database.split(",");
		System.out.println("warehouse ========>"+dbAndSchema[0]);
		System.out.println("db ========>"+dbAndSchema[1]);
		System.out.println("Schema ========>"+dbAndSchema[2]);
		
		//String url = "jdbc:snowflake://" + uri+"/?db="+"TEST"+"&schema=TEST1";
		String url = "jdbc:snowflake://" + uri+"/?db="+dbAndSchema[1]+"&warehouse="+dbAndSchema[0]+"&schema="+dbAndSchema[2];
		System.out.println("url =====>>>>>>>>>>"+url);
		Connection con = null;
		try {
			Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			
			String database1 = dbAndSchema[1];
			String schema = dbAndSchema[2];
			
			System.out.println("database :"+ database1);
			System.out.println("schema :"+ schema);
			
			String query = "SELECT TABLE_NAME FROM "+database1+".INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE IN ('BASE TABLE','VIEW') AND TABLE_SCHEMA='"+schema+"'";
			System.out.println("query :"+ query);
			
			ResultSet data = stmt.executeQuery(query);

			while (data.next()) {
				tableNameData.add(data.getString("TABLE_NAME"));
				System.out.println("table data:" + data.getString("TABLE_NAME"));
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNameData;
	}

	public JSONObject getProfileData(ListDataSource listDataSource, listDataAccess listDataAccess,long idDataSchema, int limit) {
		Connection con = null;
		List<Map<String, Object>> tableData =new ArrayList<>();
		List<ListDataSchema> listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
		String hostURI = listDataSchema.get(0).getIpAddress();
		String username = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String portName = listDataSchema.get(0).getPort();
		String tableName = listDataAccess.getFolderName();
		String queryString = listDataAccess.getQueryString();
		String databaseAndSchema = listDataSchema.get(0).getDatabaseSchema();
		JSONObject json = new JSONObject();
		try {
			String[] dbAndSchema = databaseAndSchema.split(",");
			String url = "jdbc:snowflake://" + hostURI + "/?db=" + dbAndSchema[1] + "&warehouse=" + dbAndSchema[0] + "&schema=" + dbAndSchema[2];
			Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
			con = DriverManager.getConnection(url, username, password);

			Statement stmt = con.createStatement();
			String query = null;

			if(queryString.equals("")){
				query = "select * from " + dbAndSchema[1]+"."+dbAndSchema[2]+"."+tableName + " limit "+limit;
			}else if(queryString!= null && !queryString.equals("")){
				query = queryString + " limit "+limit;
			}

			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			List<String> headers = new ArrayList<>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				headers.add(columnName);
			}
			json.put("header", headers);

			ResultSet resultSet = stmt.executeQuery(query);
			List<Map<String, Object>> actualData = new ArrayList<>();
			while(resultSet.next()){
				Map<String, Object> objectMap = new HashMap<>();
				for(String header : headers){
					String data = "";
					if(resultSet.getObject(header) != null){
						data = resultSet.getObject(header).toString();
					}
					objectMap.put(header, data);
				}
				actualData.add(objectMap);
			}
			json.put("data", actualData);


			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public JSONObject validateQuery(ValidateQuery validateQuery, ListDataSchema listDSchema) {
		Connection con = null;
		JSONObject json = new JSONObject();
		boolean isQueryValid = false;
		String errorMessage = "";
		List<Map<String, Object>> tableData = new ArrayList<>();
		List<ListDataSchema> listDataSchema = dataTemplateAddNewDAO.getListDataSchema(listDSchema.getIdDataSchema());
		String hostURI = listDataSchema.get(0).getIpAddress();
		String username = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String portName = listDataSchema.get(0).getPort();
		String databaseSchema = listDataSchema.get(0).getDatabaseSchema();
		String databaseAndSchema = listDataSchema.get(0).getDatabaseSchema();
		String isQueryEnabled = validateQuery.getIsQueryEnabled();
		String queryString = validateQuery.getQueryString();
		String tableName = validateQuery.getTableName();
		String whereCondition = validateQuery.getWhereCondition();
		try {
			String[] dbAndSchema = databaseAndSchema.split(",");
			String url = "jdbc:snowflake://" + hostURI + "/?db=" + dbAndSchema[1] + "&warehouse=" + dbAndSchema[0] + "&schema=" + dbAndSchema[2];
			Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			String query = "";
			if (isQueryEnabled.equalsIgnoreCase("N") && !whereCondition.isEmpty()) {
				query = "select * from " + dbAndSchema[1] + "." + dbAndSchema[2] + "." + tableName + " where " + whereCondition + " limit " + 1;
			} else if (queryString != null && !queryString.equals("")) {
				query = "select * from (" + queryString + ") A limit " + 1;
			}
			System.out.println("query=" + query);
			ResultSet rs = stmt.executeQuery(query);

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
			System.out.println("Exception :: " + e.getMessage());
			isQueryValid = false;
			errorMessage = e.getMessage();
		}
		json.put("isQueryValid", isQueryValid);
		json.put("errorMessage", errorMessage);
		return json;
	}

}
