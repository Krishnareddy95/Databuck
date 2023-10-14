package com.databuck.bean;
import java.sql.*;
import java.util.Properties;

public class RedShiftJdbc {

    public static void main(String[] args) {
        Connection con = null;
        Statement stmt = null;
        try {
            Class.forName("com.amazon.redshift.jdbc42.Driver");
           // System.out.println("Connecting to database...");
            Properties props = new Properties();
            props.setProperty("user", "testuser");
            props.setProperty("password", "Password1");
            con = DriverManager.getConnection("jdbc:redshift://redshiftinstance.cofohltbswbh.us-west-2.redshift.amazonaws.com:5439/redshiftdb", props);
            //Statement stmt1=conn.createStatement();
           // stmt1.executeQuery("select * from historicdatav1")
           // DatabaseMetaData metaData = con.getMetaData();
            stmt = con.createStatement();
			String query="select * from historicDataV3 limit 1";
			ResultSetMetaData metaData = stmt.executeQuery(query).getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				String columnName=metaData.getColumnName(i);
				String columnType=metaData.getColumnTypeName(i);
				System.out.println("columnName="+columnName);
				System.out.println("columnType="+columnType);
				//tableData.put(columnName, columnType);
			}
			con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        System.out.println("Finished connectivity test.");
    }
}
}
