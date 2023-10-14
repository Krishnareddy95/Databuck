package com.databuck.datatemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
@Repository
public class MYSQLDataLocation {
	public Map readTablesFromMYSQL(String uri, String databaseAndSchema, String username, String password,String tablename, String port, String sslEnb)
	{
		Map<String,String> tableData=new LinkedHashMap<String,String>(); 
		String url = "jdbc:mysql://"+uri+":"+port+"/"+databaseAndSchema;

		if(sslEnb!=null && !sslEnb.trim().equalsIgnoreCase("Y"))
			url= url+"?verifyServerCertificate=false&useSSL=true";

		Connection con=null;
		//ResultSet metaDataResultSet=null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			//System.out.println("connection executed");
			Statement stmt = con.createStatement();
			String query="select COLUMN_NAME,DATA_TYPE from INFORMATION_SCHEMA.COLUMNS  where TABLE_NAME='"+tablename+"' and  TABLE_SCHEMA='"+databaseAndSchema+"'";

			
	//String query="select COLUMN_NAME,DATA_TYPE from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='"+tablename+"'";
	System.out.println("query="+query);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("query="+query);
			//tableList=new Map<>() {
			//};
			
			while (rs.next()) {
				String COLUMN_NAME = rs.getString("COLUMN_NAME");
				System.out.println("COLUMN_NAME="+COLUMN_NAME);
				String DATA_TYPE=rs.getString("DATA_TYPE");
				System.out.println("DATA_TYPE="+DATA_TYPE);
				tableData.put(COLUMN_NAME, DATA_TYPE);
			}
			rs.close();
			for(Map.Entry m:tableData.entrySet()){  
				   System.out.println(m.getKey()+"        "+m.getValue());  
				  }  
			stmt.close();
			con.close();
			
		}catch(Exception e)
		{
			//System.out.println("vertica");
			e.printStackTrace();
		}
		return tableData;
	}

}
