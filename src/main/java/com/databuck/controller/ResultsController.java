/*package com.databuck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DATA_QUALITY_Column_Summary;
import com.databuck.dao.IResultsDAO;

@Controller
public class ResultsController {

	@Autowired
	public IResultsDAO resultsDAOImpl;
	
	@RequestMapping(value = "/forensicsresults", method = RequestMethod.GET)
	public ModelAndView forensicsresults(HttpSession session)
	{
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		System.out.println("hii");
		//Map<Long, String> map = resultsDAOImpl.getQualityApplicatinNamesAndId();
		Map<Long, String> resultMasterTableAppNamesAndAppId = resultsDAOImpl.getResultMasterTableAppNamesAndAppId();
		ModelAndView modelAndView = new ModelAndView("forensicsresults");
		modelAndView.addObject("currentSection","Results");
		modelAndView.addObject("currentLink","View");
		modelAndView.addObject("map", resultMasterTableAppNamesAndAppId);
		return modelAndView;
	}
	
	@RequestMapping(value = "/sendResultAppId", method = RequestMethod.GET)
	public ModelAndView getResultAppId(HttpServletRequest request,HttpSession session)
	{
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		System.out.println("hii");
		int appId = Integer.parseInt(request.getParameter("subject"));
		System.out.println("appId="+appId);
		return null;
		
	}
	
	
	@RequestMapping(value = "/getdataqualitytables", method = RequestMethod.GET)
	public ModelAndView getdataqualitytables(HttpServletRequest req,HttpSession session)
	{
		System.out.println("hii getdataqualitytables");
		Long appId=Long.parseLong(req.getParameter("subject"));  
		System.out.println("appId="+appId);
		for(Object dc :readColumn_SummaryTable)
		{
			DATA_QUALITY_Column_Summary dcc =(DATA_QUALITY_Column_Summary)dc;
			System.out.println(dcc.getDate()+ "   "+dcc.getRun());
		}
		List readTransactionset_sum_A1Table = resultsDAOImpl.readTransactionset_sum_A1Table(appId);
		System.out.println("readTransactionset_sum_A1Table");
		List readTransaction_SummaryTable = resultsDAOImpl.readTransaction_SummaryTable(appId);
		System.out.println("readTransaction_SummaryTable");
		SqlRowSet readTransaction_DetailTable = resultsDAOImpl.readTransaction_DetailTable(appId);
		System.out.println("readTransaction_DetailTable");
		 SqlRowSet readColumn_SummaryTable = resultsDAOImpl.readColumn_SummaryTable(appId);
		System.out.println("readColumn_SummaryTable");
		//String[] columnNames = resultsDAOImpl.columnNames(appId);
		String dynamiccolumnName = resultsDAOImpl.dynamiccolumnNameforTransactionset(appId);
		SqlRowSet readTransaction_Detail_IdentityTable = resultsDAOImpl.readTransaction_Detail_IdentityTable(appId);
		
		
		
		
		
		ModelAndView modelAndView = new ModelAndView("forensicsresults1jsp");
		modelAndView.addObject("appId", appId);
		modelAndView.addObject("readTransactionset_sum_A1Table", readTransactionset_sum_A1Table);
		modelAndView.addObject("readTransaction_SummaryTable", readTransaction_SummaryTable);
		modelAndView.addObject("readTransaction_DetailTable", readTransaction_DetailTable);
		modelAndView.addObject("readColumn_SummaryTable", readColumn_SummaryTable);
		modelAndView.addObject("readTransaction_Detail_IdentityTable",readTransaction_Detail_IdentityTable);
		//modelAndView.addObject("columnNames", columnNames);
		modelAndView.addObject("dynamiccolumnName", dynamiccolumnName);
		modelAndView.addObject("currentSection","Results");
		modelAndView.addObject("currentLink","View");
		
		
		
		return modelAndView;
	}
	
	
}
*/