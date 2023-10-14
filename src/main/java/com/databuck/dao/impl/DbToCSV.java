package com.databuck.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



public class DbToCSV {
	private static final Logger LOG = Logger.getLogger(DbToCSV.class);
	public static void main(String[] args) {
		
		List<String> list=new ArrayList<String>();
		list.add("amount=AMOUNT");
		list.add("id=ID");
		String exp="id=ID1";
		if(!list.contains(exp)){
			LOG.debug("insert");
		}else{
			LOG.debug("no");
		}
		
		
		/*String filename = "/home/appzop1/Desktop/listApp1s.csv";
		try {

			System.out.println(new File(filename).exists());
			
			PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_db_v1", "root", "root");
			String query = "select * from appConfig";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData metaData = rs.getMetaData();
			StringJoiner csvData = new StringJoiner(",");
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				csvData.add(metaData.getColumnName(i));
			}
			pw.println(csvData);
			while (rs.next()) {
				csvData = new StringJoiner(",");
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					csvData.add(rs.getString(i));

				}
				pw.println(csvData);
			}
			pw.flush();
			pw.close();
			conn.close();
			System.out.println("CSV File is created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
}