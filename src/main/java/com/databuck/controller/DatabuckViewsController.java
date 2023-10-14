package com.databuck.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.ViewRule;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IViewRuleDAO;
import com.databuck.service.RBACController;

@Controller
public class DatabuckViewsController {
	@Autowired
	private RBACController rbacController;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	IViewRuleDAO iViewRuleDAO;

	@Autowired
	GlobalRuleDAO globalRuleDAO;

	@Autowired
	IValidationCheckDAO validationcheckdao;
	

	@RequestMapping(value = "/dashboardViewRules")
	public ModelAndView dashboardView(HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		ModelAndView oModelViewPage = null;
		boolean oRbac = rbacController.rbac("Dashboard", "R", oSession);
		List<ViewRule> listViewRules = iViewRuleDAO.getViewRuleList();
		if ((oUser == null) || (!oUser.equals("validUser")) || (!oRbac)) {
			oModelViewPage = new ModelAndView("loginPage");

		} else {

			oModelViewPage = new ModelAndView("dashboardView");
			oModelViewPage.addObject("currentSection", "Dashboard View");
			oModelViewPage.addObject("currentLink", "DashboardViews");
			oModelViewPage.addObject("listDashboardViews", listViewRules);
		}
		return oModelViewPage;
	}

	@RequestMapping(value = "/myViews")
	public ModelAndView myViewsScreen(HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		ModelAndView oModelViewPage = null;
		boolean oRbac = rbacController.rbac("Results", "R", oSession);
		List<ViewRule> listViewRules = iViewRuleDAO.getViewRuleList();
		if ((oUser == null) || (!oUser.equals("validUser")) || (!oRbac)) {
			oModelViewPage = new ModelAndView("loginPage");

		} else {

			oModelViewPage = new ModelAndView("myViews");

			oModelViewPage.addObject("listViewRules", listViewRules);
			oModelViewPage.addObject("currentSection", "Dashboard");
			oModelViewPage.addObject("currentLink", "View");
		}
		return oModelViewPage;
	}

	@RequestMapping(value = "/viewRuleDetails")
	public ModelAndView viewRuleDetails(HttpSession session, @RequestParam long idruleMap) {
		Object oUser = session.getAttribute("user");
		ModelAndView oModelViewPage = null;
		boolean oRbac = rbacController.rbac("Dashboard", "R", session);
		if ((oUser == null) || (!oUser.equals("validUser")) || (!oRbac)) {
			oModelViewPage = new ModelAndView("loginPage");

		} else {
			List<ViewRule> viewRuleObj = iViewRuleDAO.getViewRuleById(idruleMap);
			String num1 = viewRuleObj.get(0).getIdListColrules().replace("[", "").replace("]", "");

			List<RuleCatalog> listRules = new ArrayList<RuleCatalog>();
			listRules = iViewRuleDAO.getRuleListByRuleId(num1);
			String num = viewRuleObj.get(0).getIdData().replace("[", "").replace("]", "");

			Long projectId = (Long) session.getAttribute("projectId");
			List<ListDataSource> listDataSource = new ArrayList<ListDataSource>();
			listDataSource = iViewRuleDAO.getListDataSources(projectId, num);
			System.out.println("listDataSource: " + listDataSource.size());

			oModelViewPage = new ModelAndView("viewRuleDetails");
			oModelViewPage.addObject("listOfRules", listRules);
			oModelViewPage.addObject("listDataSource", listDataSource);
			oModelViewPage.addObject("currentSection", "Dashboard View");
			oModelViewPage.addObject("currentLink", "DashboardViews");
		}
		return oModelViewPage;
	}

	@RequestMapping(value = "/createView")
	public ModelAndView createDashboardView(HttpSession session) {
		Object user = session.getAttribute("user");
		ModelAndView oModelViewPage = null;
		boolean oRbac = rbacController.rbac("Dashboard", "C", session);

		if ((user == null) || (!user.equals("validUser")) || (!oRbac)) {
			oModelViewPage = new ModelAndView("loginPage");

		} else {
			Long projectId = (Long) session.getAttribute("projectId");
			List listappslistds = iViewRuleDAO.getDataFromListDataSources(projectId);
			oModelViewPage = new ModelAndView("addNewView");
			oModelViewPage.addObject("getlistdatasourcesname", listappslistds);
			oModelViewPage.addObject("currentSection", "Dashboard View");
			oModelViewPage.addObject("currentLink", "Add New View");
		}
		return oModelViewPage;
	}

	@RequestMapping(value = "/editView", method = RequestMethod.GET)
	public ModelAndView editView(HttpServletRequest request, HttpSession session, @RequestParam long idruleMap,
			ModelAndView model) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Dashboard", "C", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<ListApplicationsandListDataSources> listappslistds = iViewRuleDAO
					.getDataFromListDataSources(projectId);
			List<ViewRule> viewRuleObj = iViewRuleDAO.getViewRuleById(idruleMap);
			ModelAndView modelandview = new ModelAndView("editView");

			String num = viewRuleObj.get(0).getIdData().replace("[", "").replace("]", "");
			String str[] = num.split(",");
			List<String> al = new ArrayList<String>();
			al = Arrays.asList(str);

			String num1 = viewRuleObj.get(0).getIdListColrules().replace("[", "").replace("]", "");
			String str1[] = num1.split(",");
			List<String> al1 = new ArrayList<String>();
			al1 = Arrays.asList(str1);

			String num2 = viewRuleObj.get(0).getIdApp().replace("[", "").replace("]", "");
			String str2[] = num2.split(",");
			List<String> al2 = new ArrayList<String>();
			al2 = Arrays.asList(str2);

			ListApplicationsandListDataSources obj = new ListApplicationsandListDataSources();

			try {
				for (ListApplicationsandListDataSources yourInt : listappslistds) {
					if (yourInt.getIdData().equals(Long.parseLong(al.get(0)))) {
						obj.setLsName(yourInt.getLsName());
					}
				}
			} catch (IndexOutOfBoundsException error) {
			} catch (Exception | Error exception) {
			}

			modelandview.addObject("getlistdatasourcesname", listappslistds);
			modelandview.addObject("idruleMap", viewRuleObj.get(0).getIdruleMap());
			modelandview.addObject("viewName", viewRuleObj.get(0).getViewName());
			modelandview.addObject("description", viewRuleObj.get(0).getDescription());
			modelandview.addObject("idData", al);
			modelandview.addObject("idListColrules", al1);
			modelandview.addObject("listIdApps", al2);
			modelandview.addObject("lsName", obj.getLsName());
			modelandview.addObject("currentSection", "Dashboard View");
			modelandview.addObject("currentLink", "Edit View");
			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/rulesByRuleId", method = RequestMethod.GET)
	public @ResponseBody List<RuleCatalog> rulesByRuleId(HttpServletResponse response, HttpSession session,
			@RequestParam long idruleMap) throws IOException, ParseException {

		List<ViewRule> viewRuleObj = iViewRuleDAO.getViewRuleById(idruleMap);
		String num1 = viewRuleObj.get(0).getIdListColrules().replace("[", "").replace("]", "");
		String str1[] = num1.split(",");
		List<String> al1 = new ArrayList<String>();
		al1 = Arrays.asList(str1);
		List<RuleCatalog> listColRulesData = new ArrayList<RuleCatalog>();
		listColRulesData = iViewRuleDAO.getRuleListByRuleId(num1);
		return listColRulesData;
	}

	@RequestMapping(value = "/getListOfIdApp", method = RequestMethod.GET)
	public @ResponseBody List<ListApplicationsandListDataSources> getListOfIdApp(HttpServletResponse response,
			HttpSession session, @RequestParam String idData) throws IOException, ParseException {
		Long projectId = (Long) session.getAttribute("projectId");
		List<ListApplicationsandListDataSources> listColRulesData = iViewRuleDAO.getlistOfValidation(projectId, idData);
		return listColRulesData;
	}

	@RequestMapping(value = "/getRulesByIdApp", method = RequestMethod.GET)
	public @ResponseBody List<RuleCatalog> getRulesByIdApp(HttpServletResponse response, HttpSession session,
			@RequestParam Long idApp) throws IOException, ParseException {
		List<RuleCatalog> listColRulesData = iViewRuleDAO.getRulesByIdApp(idApp);
		return listColRulesData;
	}

	@RequestMapping(value = "/saveDashboardView", method = RequestMethod.POST)
	public void saveDashboardView(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam String viewName, @RequestParam String description, @RequestParam String idDatas,
			@RequestParam String ruleList, @RequestParam String idApps) throws IOException {
		ViewRule viewRuleObj = new ViewRule();
		viewRuleObj.setViewName(viewName);
		viewRuleObj.setDescription(description);
		String idDataList = idDatas.replace("[", "").replace("]", "");
		viewRuleObj.setIdData(idDataList);
		String ruleIdList = ruleList.replace("[", "").replace("]", "");
		viewRuleObj.setIdListColrules(ruleIdList);
		String idAppList = idApps.replace("[", "").replace("]", "");
		viewRuleObj.setIdApp(idAppList);
		boolean flag = iViewRuleDAO.saveDashboardRule(viewRuleObj);
		JSONObject json = new JSONObject();
		if (flag) {

			json.put("success", "Item has been saved successfully.");
		} else {
			json.put("failed", "Unable to saved record.");
		}
		response.getWriter().println(json);
	}

	@RequestMapping(value = "/updateDashboardView", method = RequestMethod.POST)
	public void updateDashboardView(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam Long id, @RequestParam String viewName, @RequestParam String description,
			@RequestParam String idDatas, @RequestParam String ruleList, @RequestParam String idApps)
			throws IOException {
		ViewRule viewRuleObj = new ViewRule();
		viewRuleObj.setViewName(viewName);
		viewRuleObj.setIdruleMap(id);
		viewRuleObj.setDescription(description);
		String idDataList = idDatas.replace("[", "").replace("]", "");
		viewRuleObj.setIdData(idDataList);
		String ruleIdList = ruleList.replace("[", "").replace("]", "");
		viewRuleObj.setIdListColrules(ruleIdList);
		String idAppList = idApps.replace("[", "").replace("]", "");
		viewRuleObj.setIdApp(idAppList);
		boolean flag = iViewRuleDAO.updateDashboardRule(viewRuleObj);
		JSONObject json = new JSONObject();
		if (flag) {
			json.put("success", "Item has been updated successfully.");
		} else {
			json.put("failed", "Unable to update record.");
		}
		response.getWriter().println(json);
	}

	@RequestMapping(value = "/deleteView", method = RequestMethod.GET)
	public void deleteView(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam long idruleMap) throws IOException {

		boolean flag = iViewRuleDAO.deleteView(idruleMap);
		JSONObject json = new JSONObject();
		if (flag) {
			json.put("success", "Item has been deleted successfully.");
		} else {
			json.put("failed", "Unable to delete record.");
		}
		response.getWriter().println(json);
	}

	@RequestMapping(value = "/getRulesByViewId", method = RequestMethod.GET)
	public @ResponseBody List<RuleCatalog> getRulesByViewId(HttpServletResponse response, HttpSession session,
			@RequestParam Long idruleMap) throws IOException, ParseException {
		List<ViewRule> viewRuleObj = iViewRuleDAO.getViewRuleById(idruleMap);
		String num1 = viewRuleObj.get(0).getIdListColrules().replace("[", "").replace("]", "");
		List<RuleCatalog> listRules = new ArrayList<RuleCatalog>();
		listRules = iViewRuleDAO.getRuleListByRuleId(num1);
		return listRules;
	}

	@RequestMapping(value = "/getDataForIdApps", method = RequestMethod.GET)
	public @ResponseBody ViewRule getDataForIdApps(HttpServletResponse response, HttpSession session,
			@RequestParam Long idruleMap) throws IOException, ParseException {
		List<ViewRule> viewRuleObj = iViewRuleDAO.getViewRuleById(idruleMap);
		String num1 = viewRuleObj.get(0).getIdApp().replace("[", "").replace("]", "");
		ViewRule listRules = new ViewRule();
		listRules.setIdApp(num1);
		return listRules;
	}
}
