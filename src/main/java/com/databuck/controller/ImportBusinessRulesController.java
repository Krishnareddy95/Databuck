package com.databuck.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.Domain;
import com.databuck.bean.ListDataSource;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IimportBusinessRulesDAO;
import com.databuck.service.RBACController;

@Controller
public class ImportBusinessRulesController {
	@Autowired
	ITemplateViewDAO templateviewdao;
	@Autowired
	private RBACController rbacController;
	@Autowired
	IUserDAO userDAO;
	
	@Autowired
	GlobalRuleDAO globalruledao;
	
	@Autowired
	IimportBusinessRulesDAO importBusinessRulesDao;
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@RequestMapping(value = "/importBusinessRules", method = RequestMethod.GET)
	public ModelAndView addNewExtendTemplate(HttpServletResponse response,HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		System.out.println("importBusinessRules 01 ok");
		
		if (rbac) {			
			int deleteImpBRules = importBusinessRulesDao.deleteBusinessRules(1918l, 1919l);
			System.out.println("importBusinessRules 02 deleteImpBRules=" + deleteImpBRules);
			
			try {
				Process proc = null;
				proc = Runtime.getRuntime().exec(System.getenv("DATABUCK_HOME") + "/scripts/runscript.sh" );
				proc.waitFor(); // Then retreive the process output
				InputStream in = proc.getInputStream();
				InputStream err = proc.getErrorStream();

				byte b[] = new byte[in.available()];
				in.read(b, 0, b.length);
				System.out.println(new String(b));

				byte c[] = new byte[err.available()];
				err.read(c, 0, c.length);
				
				System.out.println("importBusinessRules 03 ok");

			} catch (Exception oException) {
				System.out.println("importBusinessRules 04 " + oException.getMessage());				
			}
			
			try {
				response.sendRedirect("viewRules");
			} catch (Exception oException) {
				oException.getMessage();
			}
		} else
			return new ModelAndView("loginPage");
		
		return new ModelAndView("loginPage");
	}

}
