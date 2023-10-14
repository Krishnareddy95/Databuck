package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AzureConnection {

	public List<String> getListOfTableNamesFromAzure(String Uri, String port, String datasetname, String username,
			String password) {
		String url = "jdbc:sqlserver://" + Uri + ":" + port + ";DatabaseName=" + datasetname;
		List<String> tableNameData = new ArrayList<String>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(url, username, password);

			DatabaseMetaData metadata = con.getMetaData();
			String[] types = { "TABLE" };

			ResultSet resultSet = metadata.getTables(null, null, "%", types);

			while (resultSet.next()) {
				String tableName = resultSet.getString(3);
				String tableCatalog = resultSet.getString(1);
				String tableSchema = resultSet.getString(2);
				String tableNames = tableSchema + "." + tableName;
				tableNameData.add(tableNames);
				System.out.println("Table : " + tableName + "nCatalog : " + tableCatalog + "nSchema : " + tableSchema);
			}

			System.out.println("getListOfTableNamesFromAzure Tables list: " + tableNameData);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableNameData;
	}

	public LinkedHashMap<String, String> readTablesFromAzureSynapse(String Uri, String port, String datasetname,
			String username, String password, String tableName) {

		LinkedHashMap<String, String> tableData = new LinkedHashMap<String, String>();
		try {
			String url = "jdbc:sqlserver://" + Uri + ":" + port + ";DatabaseName=" + datasetname;

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(url, username, password);

			String table_columns_details = "select * from " + tableName + "";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(table_columns_details);
			ResultSetMetaData rsmd = rs.getMetaData();
			int NumOfCol = rsmd.getColumnCount();
			for (int i = 1; i <= NumOfCol; i++) {
				System.out.println(
						" Column Name and data type is =" + rsmd.getColumnName(i) + "==" + rsmd.getColumnTypeName(i));
				tableData.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableData;
	}

}
