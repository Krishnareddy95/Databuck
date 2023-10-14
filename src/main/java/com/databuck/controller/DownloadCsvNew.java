package com.databuck.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.StringJoiner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.databuck.dao.SchemaDAOI;

@Controller
public class DownloadCsvNew {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	@Autowired
	public SchemaDAOI SchemaDAOI;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1) {
		this.jdbcTemplate1 = jdbcTemplate1;
		this.jdbcTemplate = jdbcTemplate;
	}

	@RequestMapping(value = "/statusBarCsvDownloadnew", method = RequestMethod.POST, produces = "application/json")
	public void startStatuspoll(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam String tableName) {
		System.out.println("tableName=" + tableName);
		JSONObject json = new JSONObject();
		try {
			double currentCount = (Long) session.getAttribute(tableName + "_CurrentCount");
			double totalCount = (Long) session.getAttribute(tableName + "_TotalCount");
			System.out.println(currentCount + "" + totalCount);
			System.out.println(((currentCount / totalCount) * 100));
			json.put("percentage", (long) ((currentCount / totalCount) * 100));
			System.out.println(json);
			// json.put("appName", appName);

			response.getWriter().println(json);
		} catch (Exception e) {
			json.put("percentage", 100);
			try {
				response.getWriter().println(json);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	@RequestMapping(value = "/downloadCsvnew")
	public void doDownload(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String tableName = request.getParameter("tableName");
		System.out.println("tableName::" + tableName);
		session.setAttribute(tableName + "_CurrentCount", 0l);
		session.setAttribute(tableName + "_TotalCount", 0l);

		try {
			if (!tableName.trim().equals("")) {
				String fileFullPath = null;
				if (tableName.toUpperCase().contains("DATA_QUALITY")) {
					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv";
				} else {
					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv";
				}
				PrintWriter pw = new PrintWriter(
						new FileWriter(new File(System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv")));
				String sql = "select * from " + tableName;
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				// set count
				queryForRowSet.last();
				session.setAttribute(tableName + "_TotalCount", (long) queryForRowSet.getRow());
				queryForRowSet.first();
				SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
				StringJoiner csvData = new StringJoiner(",");
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					csvData.add(metaData.getColumnName(i));
				}
				pw.println(csvData);
				long j = 0;
				do {
					j++;
					csvData = new StringJoiner(",");
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						csvData.add(queryForRowSet.getString(i));
					}
					pw.println(csvData);
					// if(j%100==0){
					session.setAttribute(tableName + "_CurrentCount", j);
				} while (queryForRowSet.next());

				/*
				 * while (queryForRowSet.next()) { j++; csvData = new
				 * StringJoiner(","); for (int i = 1; i <=
				 * metaData.getColumnCount(); i++) {
				 * csvData.add(queryForRowSet.getString(i)); }
				 * pw.println(csvData); // if(j%100==0){
				 * session.setAttribute(tableName + "_CurrentCount", j); // } }
				 */
				pw.flush();
				pw.close();
				System.out.println("CSV File is created successfully.");
				// String
				// fileFullPath="C:\\Users\\appzop6\\Downloads\\"+tableName+".csv";
				// String fileFullPath="E:\\backup\\Tuesday\\databuck.zip";
				System.out.println("table for csv+" + tableName);
				// get absolute path of the application
				ServletContext context = request.getSession().getServletContext();
				String appPath = context.getRealPath("");
				System.out.println("appPath = " + appPath);

				// construct the complete absolute path of the file
				File downloadFile = new File(fileFullPath);
				FileInputStream inputStream = new FileInputStream(downloadFile);

				// get MIME type of the file
				String mimeType = context.getMimeType(fileFullPath);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}
				System.out.println("MIME type: " + mimeType);

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", tableName + ".csv");
				response.setHeader(headerKey, headerValue);

				// get output stream of the response
				OutputStream outStream = response.getOutputStream();

				byte[] buffer = new byte[1024 * 1000];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}

				inputStream.close();
				outStream.close();
				session.removeAttribute(tableName + "_CurrentCount");
				session.removeAttribute(tableName + "_TotalCount");
			}
		} catch (Exception e) {
			/*
			 * try { response.sendRedirect("errorPage"); } catch (IOException
			 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
			 */
			e.printStackTrace();
		}
	}
}