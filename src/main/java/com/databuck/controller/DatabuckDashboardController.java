package com.databuck.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.IDashBoardService;
import com.databuck.service.IValidationService;
import com.databuck.service.RBACController;

@Controller
public class DatabuckDashboardController {
	@Autowired
	public IValidationService validationService;

	@Autowired
	public IDashBoardService dashBoardService;

	@Autowired
	public IResultsDAO iResultsDAO;
	
	@Autowired
	MatchingResultDao matchingresultdao;
	
	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	public IValidationDAO ivalidationdao;
	@Autowired
	private RBACController rbacController;
	
	@RequestMapping(value = "/dataQualityDashboard")
	public ModelAndView dashboardView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R",session);
		if(rbac){
		
		System.out.println("dashboard controller");
		ModelAndView modelAndView = new ModelAndView("resultDashboard");

		SqlRowSet getdatafromresultmaster = ivalidationdao.getdatafromresultmaster();
		Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
		
		//Map resultmasterdata = new HashMap<>();
		while (getdatafromresultmaster.next()) {
			resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
			System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
		}

		List<ListApplications> listapplicationsData = matchingresultdao.getdatafromlistapplications();
		List<DataQualityMasterDashboard> dashboardTable = iResultsDAO.getMasterDashboardForDataQuality(resultmasterdata,listapplicationsData);
		modelAndView.addObject("dashboardTable", dashboardTable);
		modelAndView.addObject("resultmasterdata", resultmasterdata);
		modelAndView.addObject("currentSection", "Dashboard");
		modelAndView.addObject("currentLink", "View");
		return modelAndView;
		}else
		return new ModelAndView("loginPage");
	}
}
