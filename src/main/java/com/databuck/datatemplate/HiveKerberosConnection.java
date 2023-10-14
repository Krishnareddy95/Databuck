package com.databuck.datatemplate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.stereotype.Repository;

@Repository
public class HiveKerberosConnection {
	public Map readTablesFromHiveKerberos(String internalIP, String databaseAndSchema, String userlogin,
			String password, String tableName, String port, String principle, String keytab, String krb5conf) {
		Map<String, String> tableData = new LinkedHashMap<String, String>();
		try {
			org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
			conf.set("hadoop.security.authentication", "Kerberos");
			UserGroupInformation.setConfiguration(conf);
			System.setProperty("java.security.auth.login.config", keytab);
			System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
			System.setProperty("java.security.krb5.conf", krb5conf);
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection con = DriverManager.getConnection(
					"jdbc:hive2://" + internalIP + ":" + port + "/" + databaseAndSchema + ";principal=" + principle);
			System.out.println("Connected...." + con);
			Statement stmt = con.createStatement();
			ResultSet executeQuery = stmt.executeQuery("select * from " + tableName + " limit 1");
			ResultSetMetaData metaData = executeQuery.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);

				if (columnName.contains(".")) {
					String[] split = columnName.split("\\.");
					columnName = split[1];
				}
				System.out
						.println(columnName + "   " + metaData.getColumnTypeName(i) + "   " + columnName.contains("."));

				tableData.put(columnName, metaData.getColumnTypeName(i));
			}
			con.close();
			return tableData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableData;
	}

}
