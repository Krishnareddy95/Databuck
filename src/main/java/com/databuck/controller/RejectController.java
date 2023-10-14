package com.databuck.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;

@Controller
public class RejectController {
	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	public IResultsDAO iResultsDAO;

 @RequestMapping(value = "/rejectInd", method = RequestMethod.GET)
    public void rejectInd(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
	String colName = request.getParameter("colName");
	String tableName = request.getParameter("tableName");
	String uniqueValues = request.getParameter("uniqueValues");
	String dGroupVal = request.getParameter("dGroupVal");
	String dGroupCol = request.getParameter("dGroupCol");
	String Run = request.getParameter("Run");
	String idApp = request.getParameter("idApp");
	String tab = request.getParameter("tab");
	String Date = request.getParameter("Date");
	
	System.out.println("uniqueValues ");
	System.out.println("===============> In rejectInd, dGroupVal = " + dGroupVal);
	System.out.println("===============> In rejectInd, dGroupCol = " + dGroupCol);

	try {
		
	
		String userName = (String) session.getAttribute("firstName");

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();	
		if(tab.equals("drift")){
			String sql=iResultsDAO.updaterejectIndDrift(tableName,userName,Run,uniqueValues,colName, dGroupVal,dGroupCol);
			response.sendRedirect("stringstats?idApp=" + idApp);
		}
		if(tab.equals("Validity")){
			String sql=iResultsDAO.updaterejectIndValidity(tableName,dGroupCol,Run,dGroupVal,Date,userName,idApp);
			response.sendRedirect("validity?idApp=" + idApp);
		}
		if(tab.equals("GBRCA")){
			String sql=iResultsDAO.updaterejectIndGBRCA(tableName,dGroupCol,Run,dGroupVal,Date,userName,idApp);
			response.sendRedirect("dashboard_table?idApp=" + idApp);
		}
			

	} catch (Exception e) {
		e.printStackTrace();
	}
	
  }
 
 	@RequestMapping(value = "/undoRejectInd", method = RequestMethod.GET)
	public void undoRejectInd(HttpServletRequest request, HttpSession session, HttpServletResponse response) {

		try {
			String colName = request.getParameter("colName");
			String tableName = request.getParameter("tableName");
			String uniqueValues = request.getParameter("uniqueValues");
			String dGroupVal = request.getParameter("dGroupVal");
			String dGroupCol = request.getParameter("dGroupCol");
			String Run = request.getParameter("Run");
			String idApp = request.getParameter("idApp");
			String tab = request.getParameter("tab");
			String userName = (String) session.getAttribute("firstName");

			if (tab.equals("drift")) {
				iResultsDAO.undoDataDriftRejectInd(tableName, userName, Run, uniqueValues, colName, dGroupVal,
						dGroupCol);
				response.sendRedirect("stringstats?idApp=" + idApp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
 
 @RequestMapping(value = "/rejectAll", method = RequestMethod.GET)
	public void rejectAll(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		String rejectAll = request.getParameter("rejectAll");
		String tableName = request.getParameter("tableName");
		String idApp = request.getParameter("idApp");
		String tab = request.getParameter("tab");
		String columnname = request.getParameter("columnname");

		try {
			
			//Connection con = null;
			//Statement stmt = null;
			//ResultSet rs = null;
			//Class.forName(resultDBConnectionProperties.getProperty("db1.driver")).newInstance();
			//con = DriverManager.getConnection(resultDBConnectionProperties.getProperty("db1.url"),
					//resultDBConnectionProperties.getProperty("db1.user"), resultDBConnectionProperties.getProperty("db1.pwd1"));
			int maxValue = 0;
			String dateUpdate = "";
			//stmt = con.createStatement();
			

			String userName = (String) session.getAttribute("firstName");
			
			if(tab.equals("drift")){
				String maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="+idApp 
													+" and Date=(select max(Date) from "+tableName+" where idApp="+idApp+")";
				SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
				if (max.next()) {
					maxValue = max.getInt("maxRun");
					dateUpdate = max.getString("Date");
				}
			String SQL = iResultsDAO.updaterejectAllDrift(tableName,userName,maxValue,dateUpdate, idApp,columnname);			
			response.sendRedirect("stringstats?idApp=" + idApp);
			}
			if(tab.equals("GBRCA")){
				String maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="+idApp
													+" and Date=(select max(Date) from "+tableName+" where idApp="+idApp+")";
				SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
				if (max.next()) {
					maxValue = max.getInt("maxRun");
					dateUpdate = max.getString("Date");
				}
				String SQL = iResultsDAO.updaterejectAllGBRCA(tableName,userName,maxValue,dateUpdate, idApp);			
				response.sendRedirect("dashboard_table?idApp=" + idApp);
				}
			if(tab.equals("Validity")){
				String maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="+idApp
														+" and Date=(select max(Date) from "+tableName+" where idApp="+idApp+")";
				SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
				if (max.next()) {
					maxValue = max.getInt("maxRun");
					dateUpdate = max.getString("Date");
				}
				String SQL = iResultsDAO.updaterejectAllValidity(tableName,userName,maxValue,dateUpdate, idApp);			
				response.sendRedirect("validity?idApp=" + idApp);
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
 //for Date Filter
 
 @RequestMapping(value = "/dateFilter", method = RequestMethod.GET)
	public void dateFilter(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String toDate, @RequestParam String fromDate) {
	 
	 HttpSession session = request.getSession();
	 session.setAttribute("toDate", toDate);
 	 session.setAttribute("fromDate", fromDate);
 	 String dateFilter = " Date >= '" + toDate + "' and Date <= '" + fromDate + "'";
 	 session.setAttribute("dateFilter", dateFilter);
 	 System.out.println("date Query" + dateFilter);
 	JSONObject json = new JSONObject();
 	json.put("success", "");
 }
 @RequestMapping(value = "/filterProject", method = RequestMethod.GET)
	public void filterProject(HttpServletRequest request, HttpServletResponse response,
			 @RequestParam String ObjectName) {
	 
	 HttpSession session = request.getSession();
	
 	 session.setAttribute("ObjectName", ObjectName);
  	JSONObject json = new JSONObject();
 	json.put("success", "");
 }
 @RequestMapping(value = "/runFilter", method = RequestMethod.GET)
	public void runFilter(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int RunFilter) {
	 
	 HttpSession session = request.getSession();
	 JSONObject json = new JSONObject();
	 
	 
		 session.setAttribute("RunFilter", RunFilter);
	     json.put("success", "");
 }
 
 
 @RequestMapping(value = "/dateAndProjectFilter", method = RequestMethod.GET)
	public void dateAndProjectFilter(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String toDate, @RequestParam String fromDate, @RequestParam String projectid) {
	 
	 HttpSession session = request.getSession();
	 session.setAttribute("toDate", toDate);
	 session.setAttribute("fromDate", fromDate);
	 session.setAttribute("searchfilter_projectId", Long.valueOf(projectid));
	String dateFilter = " Date >= '" + toDate + "' and Date <= '" + fromDate + "'";
	session.setAttribute("dateFilter", dateFilter);
	 System.out.println("Test====1");
	 System.out.println("date Query" + dateFilter);
	JSONObject json = new JSONObject();
	json.put("success", "");
}
 
}
