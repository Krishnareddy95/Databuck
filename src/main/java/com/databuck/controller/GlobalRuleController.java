package com.databuck.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.GlobalFilters;
import com.databuck.constants.DatabuckConstants;
import com.databuck.service.GlobalRuleService;
import com.databuck.service.RuleCatalogService;
import org.apache.commons.collections.map.MultiValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListColRules;
import com.databuck.bean.Dimension;
import com.databuck.bean.Domain;
import com.databuck.bean.GlobalRuleView;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.RuleToSynonym;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.ruleFields;
import com.databuck.dao.DatabuckTagsDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;

@Controller
public class GlobalRuleController {

	@Autowired
	GlobalRuleDAO GlobalRuleDAO;

	@Autowired
	ITemplateViewDAO templateviewdao;
	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	private RBACController rbacController;

	@Autowired
	private RuleCatalogService ruleCatalogService;
	
    @Autowired
	private DatabuckTagsDao databuckTagsDao;

	@Autowired
	private GlobalRuleService globalRuleService;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	public void setGlobalRuleDAO(GlobalRuleDAO GlobalRuleDAO) {
		this.GlobalRuleDAO = GlobalRuleDAO;
	}

	@RequestMapping(value = "/AddNewRuleGlobal", method = RequestMethod.GET)
	public ModelAndView addNewRule(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		String domain = request.getParameter("rulenameid");
		System.out.println("Rule Name Id======== = " + domain);
		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
			List<Domain> listdomain = GlobalRuleDAO.getDomainList();
			List<Dimension> dimensionList = templateviewdao.getlistdimensionname();

			ModelAndView modelandview = new ModelAndView("GlobalAddNewRule");
			modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
			modelandview.addObject("listdomain", listdomain);
			modelandview.addObject("dimensionList", dimensionList);
			modelandview.addObject("currentSection", "Global Rule");
			modelandview.addObject("currentLink", "Add New Global Rule");
			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/addNewGlobalFilter", method = RequestMethod.GET)
	public ModelAndView addNewGlobalFilter(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		String domain = request.getParameter("domain");
		System.out.println("Domain Name ======== = " + domain);
		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		if (rbac) {

			List<Domain> listdomain = GlobalRuleDAO.getDomainList();

			ModelAndView modelandview = new ModelAndView("addNewGlobalFilter");
			modelandview.addObject("listdomain", listdomain);
			modelandview.addObject("currentSection", "Global Rule");
			modelandview.addObject("currentLink", "Add New Global Filter");
			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

	// for get list of synonyms
	@RequestMapping(value = "/getsynonymslist", method = RequestMethod.POST)
	public void getsynonymslist(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		System.out.println("Inside /getsynonymslist:Started");
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String fieldName = req.getParameter("val");
		String domain = req.getParameter("domain");
		System.out.println("domain = " + domain);

		// String fieldName1="id";
		System.out.println("FieldName=" + fieldName);
		String name = GlobalRuleDAO.getListPossibleName(GlobalRuleDAO.getDomainId(domain), fieldName);
		System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter().println(" " + name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/createGlobalRule", method = RequestMethod.POST)
	public void createExtendTemplateRule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name, @RequestParam String description, @RequestParam String ruleCategory,
			@RequestParam String expression, @RequestParam String matchingRules,
			@RequestParam String externalDatasetName, @RequestParam String domain, @RequestParam long dataSource2,
			@RequestParam Double ruleThreshold, @RequestParam long dimension, @RequestParam Integer filterId,
			@RequestParam Integer rightTemplateFilterId, @RequestParam String aggregateResultsEnabled) {
		Map<String, String> tempmap = new HashMap<String, String>();
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");

		System.out.println("idUser=" + idUser);
		// activedirectory flag check
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
		String createdByUser = "";
		if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
			createdByUser = (String) session.getAttribute("createdByUser");
			System.out.println("======= createdByUser in GlobalRuleController ===>" + createdByUser);
		} else {
			// getting createdBy username from createdBy userId
			System.out.println("======= idUser ===>" + idUser);

			createdByUser = userDAO.getUserNameByUserId(idUser);

			System.out.println("======= createdByUser in GlobalRuleController ===>" + createdByUser);
		}

		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (matchingRules == null)
			matchingRules = "";

		if (expression == null)
			expression = "";

		System.out.println("name=" + name);
		System.out.println("domain=" + domain);
		System.out.println("description=" + description);
		System.out.println("ruleCategory=" + ruleCategory);
		System.out.println("dataSource2=" + dataSource2);
		System.out.println("expression=" + expression);
		System.out.println("matchingRules=" + matchingRules);
		System.out.println("externalDatasetName=" + externalDatasetName);
		System.out.println("dimension = " + dimension);
		System.out.println("filterId = " + filterId);
		System.out.println("rightTemplateFilterId = " + rightTemplateFilterId);
		System.out.println("aggregateResultsEnabled = " + aggregateResultsEnabled);

		// Eliminates spaces and special characters - by mamta
		String globalRuleName = request.getParameter("name");

		if (globalRuleName == null || globalRuleName.trim().isEmpty()) {

			try {
				JSONObject json = new JSONObject();
				json.put("fail", "Please Enter Global Rule Name ");
				response.getWriter().println(json);
				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Pattern pattern = Pattern.compile("[^A-Za-z0-9_]");
		Matcher match = pattern.matcher(globalRuleName);
		boolean val = match.find();
		if (val == true) {

			try {

				JSONObject json = new JSONObject();
				json.put("fail", "Please Enter Global Rule Name without spaces and special character");
				response.getWriter().println(json);
				// response.getWriter().println("Please Enter Global Rule Name without spaces
				// and special characters");
				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// for negative Threshold -Mamta 19/5/2022
		String rulethr = request.getParameter("ruleThreshold");
		double value = Double.parseDouble(rulethr);

		if (value < 0)

		{
			System.out.println(value + "Threshold is negative");
			JSONObject json = new JSONObject();
			json.put("fail", "Please enter positive value for Rule Threshold");
			try {
				response.getWriter().println(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		} else {
			System.out.println(value + "Threshold is positive");
		}

		int domainid = 0;
		ListColGlobalRules lcr = new ListColGlobalRules();
		lcr.setRuleName(name);
		lcr.setDescription(description);
		lcr.setExpression(expression);

		lcr.setIdRightData(dataSource2);
		lcr.setMatchingRules(matchingRules);
		lcr.setCreatedByUser(createdByUser);

		lcr.setRuleThreshold(ruleThreshold);
		lcr.setFilterId(filterId);

		Long projectId = (Long) session.getAttribute("projectId");
		lcr.setProjectId(projectId);
		lcr.setDimensionId(dimension);
		lcr.setAggregateResultsEnabled(aggregateResultsEnabled);
		lcr.setRightTemplateFilterId(0);

		if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)) {
			lcr.setRuleType(DatabuckConstants.REFERENTIAL_RULE);
			lcr.setMatchingRules("");
			lcr.setFilterId(0);

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
			lcr.setRuleType(DatabuckConstants.ORPHAN_RULE);
			lcr.setExpression("");
			lcr.setExternalDatasetName(externalDatasetName);
			lcr.setFilterId(0);

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
			lcr.setRuleType(DatabuckConstants.CROSSREFERENTIAL_RULE);
			lcr.setExternalDatasetName(externalDatasetName);
			lcr.setFilterId(0);

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {
			lcr.setRuleType(DatabuckConstants.CONDITIONAL_ORPHAN_RULE);
			lcr.setExternalDatasetName(externalDatasetName);
			lcr.setExpression("");
			if(rightTemplateFilterId != null && rightTemplateFilterId > 0)
				lcr.setRightTemplateFilterId(rightTemplateFilterId);

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)) {
			lcr.setRuleType(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE);
			lcr.setMatchingRules("");

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)) {
			lcr.setRuleType(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE);
			lcr.setMatchingRules("");

		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)) {
			lcr.setRuleType(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK);
			lcr.setMatchingRules("");
			
		} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
			lcr.setRuleType(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK);
			lcr.setMatchingRules("");
		}

		// String domain_Name = GlobalRuleDAO.getdomainName(domain);
		// System.out.println("for check duplicate domain_name :: " + domain_Name);
		if (domain != null) {
			try {
				domainid = GlobalRuleDAO.getDomainId(domain);
				lcr.setDomain_id(domainid);
				System.out.println("get domain_Id  from db ::   " + domainid);
			} catch (Exception e) {
				System.out.println("probem occured while getting domain_Id  from db ");
			}
		}

		boolean duplicateName = GlobalRuleDAO.checkIfDuplicateGlobalRuleName(lcr);

		if (!duplicateName) {

			String validityStatus = "fail";
			String message = "";
			JSONObject match_expr_status = null;
			JSONObject rule_expr_status = null;

			// Validate Matching condition
			if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
				match_expr_status = globalRuleService.validateSynonymExpression(matchingRules, domainid);
			}

			// Validate Rule Expression
			if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
					|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
				rule_expr_status = globalRuleService.validateSynonymExpression(expression, domainid);
			}

			if (match_expr_status != null && match_expr_status.getString("status").equalsIgnoreCase("failed")) {
				message = match_expr_status.getString("message");

			} else if (rule_expr_status != null && rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
				message = rule_expr_status.getString("message");

			} else
				validityStatus = "success";

			if (validityStatus.equalsIgnoreCase("success")) {

				int checkIfDuplicateSqlRowSet = GlobalRuleDAO.checkIfDuplicateGlobalRule(lcr);
				try {
					if (checkIfDuplicateSqlRowSet >= 1) {
						try {
							JSONObject json = new JSONObject();
							json.put("fail", "The Rule Expression already exists");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						GlobalRuleDAO.insertintolistColRules(lcr);
						try {
							JSONObject json = new JSONObject();
							json.put("success", "Global Rule created successfully");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					GlobalRuleDAO.insertintolistColRules(lcr);
					try {
						JSONObject json = new JSONObject();
						json.put("success", "Global Rule created successfully");
						response.getWriter().println(json);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				if (checkIfDuplicateSqlRowSet < 1) {
					int global_ruleid = GlobalRuleDAO.getruleId(name, domainid);
					if (session.getAttribute("userrule") != null) {
						tempmap = (Map<String, String>) session.getAttribute("userrule");
					}
					String text = null;
					String original_Possible = null;
					int tableColumn_Count = 0;
					for (Map.Entry<String, String> entry : tempmap.entrySet()) {

						ruleFields rc = new ruleFields();
						if (entry.getKey().equals("null")) {
							continue;
						}
						rc.setUsercolumns(entry.getKey());
						if (entry.getValue().contains("\n") || entry.getValue().contains("\\n")) {
							text = entry.getValue().replace("\\n", "").trim();

							rc.setPossiblenames(text);
						} else {
							rc.setPossiblenames(entry.getValue());
						}
						rc.setDomain_id(domainid);

						tableColumn_Count = GlobalRuleDAO.getCountOfTableColumn(rc);
						original_Possible = GlobalRuleDAO.getPossibleName(rc);
						int syn_id = GlobalRuleDAO.getsynosymId(rc);// synonym table syn id

						RuleToSynonym rulesyn = new RuleToSynonym();

						rulesyn.setGlobalRuleId(global_ruleid);
						rulesyn.setSynonymId(syn_id);

						rc.setSynonyms_Id(syn_id);
						if (tableColumn_Count >= 1) {
							if (original_Possible.equals(text)) {
								System.out.println("  both are equal " + original_Possible + " ::  " + text);
								// insert into rule syn mapping in case of resuing existing synonym

								System.out
										.println("inserting into rule syn mapping in case of resuing existing synonym");
								String query = "insert into ruleTosynonym" + "(rule_id,synonym_id) values(?,?)";

								int update = jdbcTemplate.update(query,
										new Object[] { rulesyn.getGlobalRuleId(), rulesyn.getSynonymId() });
								System.out.println("insert into ruleTosynonym=" + update);

							} else {
								System.out.println("not equql :: " + text);

								// insert into rule syn mapping in case of

								System.out.println(
										"inserting into rule syn mapping in case of  updating existing synonym");
								String query = "insert into ruleTosynonym" + "(rule_id,synonym_id) values(?,?)";

								int update = jdbcTemplate.update(query,
										new Object[] { rulesyn.getGlobalRuleId(), rulesyn.getSynonymId() });
								System.out.println("insert into ruleTosynonym=" + update);
								int update2 = GlobalRuleDAO.updateIntoSynonymsLibrary(rc);
								System.out.println("data updated :: " + update);
							}

						} else {

							// insert into rule syn mapping in case of creating new synonym

							// insert new synonymns into synonym lib

							System.out.println("insert sysnoyms into  synonym lib ");
							syn_id = GlobalRuleDAO.insertintoSynonymsLibrary(rc);

							System.out.println("inserting into rule syn mapping in case of  creating new  synonym");
							String query = "insert into ruleTosynonym" + "(rule_id,synonym_id) values(?,?)";

							int update = jdbcTemplate.update(query, new Object[] { rulesyn.getGlobalRuleId(), syn_id });// here
																														// global
																														// rule
																														// id
																														// ==
																														// syn
																														// id
							System.out.println("insert into ruleTosynonym=" + update);

						}

					}
				}

			} else {
				try {
					JSONObject json = new JSONObject();
					json.put(validityStatus, message);
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("\nThe Rule Name already exists!!");
			try {
				JSONObject json = new JSONObject();
				json.put("fail", "The Rule Name already exists ");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/viewGlobalRules")
	public ModelAndView getListDataSource(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "R", session);
		if (rbac) {

			List<GlobalRuleView> listColRulesData = GlobalRuleDAO.getListColRulesForViewRules();
			JSONArray databuckTag = databuckTagsDao.getAllDatabuckTags();

			model.addObject("listColRulesData", listColRulesData);
			model.addObject("currentSection", "Global Rule");
			model.addObject("tags",databuckTag.toList());
			model.addObject("currentLink", "View Global Rules");
			model.setViewName("GlobalViewRules");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/viewGlobalFilters")
	public ModelAndView viewGlobalFilters(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "R", session);
		if (rbac) {

			List<GlobalFilters> globalFiltersData = GlobalRuleDAO.getListGlobalFilters();

			model.addObject("globalFiltersData", globalFiltersData);
			model.addObject("currentSection", "Global Rule");
			model.addObject("currentLink", "View Global Filter");
			model.setViewName("viewGlobalFilters");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteGlobalRulesData", method = RequestMethod.POST)
	public void deleteIdListColRulesData(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam long globalRuleId) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("idListColrules=" + globalRuleId);
		GlobalRuleDAO.deleteGlobalRulesData(globalRuleId);
		try {
			response.getWriter().println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/editGlobalRule", method = RequestMethod.GET)
	public ModelAndView editExtendTemplate(HttpServletRequest request, HttpSession session,
			@RequestParam long idListColrules, @RequestParam String domain, ModelAndView model) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		ModelAndView modelandview = new ModelAndView("editGlobalRule");
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			int domain_id = GlobalRuleDAO.getDomainId(domain);
			List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
			// ListColGlobalRules listDataSource =
			// GlobalRuleDAO.getDataFromListDataSourcesOfIdData(idListColrules);
			ListColGlobalRules listColGlobalRules = GlobalRuleDAO.getDataFromListDataSourcesruledomain(idListColrules,
					domain_id);

			System.out.println("############# domain_id =>" + domain_id);
			System.out.println("############# ruleID =>" + idListColrules);

			GlobalFilters globalFilter = null;
			if (listColGlobalRules.getFilterId() > 0) {
				globalFilter = GlobalRuleDAO.getDataFromGlobalFilters(listColGlobalRules.getFilterId(), domain_id);
				listColGlobalRules.setFilterCondition(globalFilter.getFilterCondition());
			}
			
			Integer rightTemplateGlobalFilterId = listColGlobalRules.getRightTemplateFilterId();
			GlobalFilters rightTemplateGlobalFilter = null;
			if (rightTemplateGlobalFilterId!=null && rightTemplateGlobalFilterId > 0) {
				rightTemplateGlobalFilter = GlobalRuleDAO.getDataFromGlobalFilters(rightTemplateGlobalFilterId, domain_id);
				if(rightTemplateGlobalFilter != null)
					listColGlobalRules.setRightTemplateFilterCondition(rightTemplateGlobalFilter.getFilterCondition());
			}

			// get synonym_id for rule_id(idListColrules) from ruleToSynonym TABLE

			// List<Integer> lstSysnonym_id =
			// GlobalRuleDAO.getSynonymIdByRuleId(idListColrules);

			List<ruleFields> lstSynonyms = GlobalRuleDAO.getSynonymIdByRuleId(idListColrules);
			System.out.println("number  of synonyms for ruleId :" + idListColrules + " are :" + lstSynonyms.size());

			/*
			 * Iterator itr = lstSysnonym_id.iterator(); while (itr.hasNext()) {
			 * 
			 * List<ruleFields> synforruledomain =
			 * GlobalRuleDAO.getsynforruledomain(domain_id,
			 * Long.parseLong(itr.next().toString()));
			 * 
			 */

			System.out.println(":::: " + lstSynonyms);
			List<Domain> listdomain = GlobalRuleDAO.getDomainList();
			List<Dimension> dimensionList = templateviewdao.getlistdimensionname();

			List<GlobalFilters> globalFiltersList = GlobalRuleDAO.getAllGlobalFiltersByDomain(domain_id);

			modelandview.addObject("globalFilter", globalFilter);
			modelandview.addObject("rightTemplateGlobalFilter", rightTemplateGlobalFilter);
			modelandview.addObject("dimensionList", dimensionList);
			modelandview.addObject("listFilterNames", globalFiltersList);
			modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
			modelandview.addObject("listDataSource", listColGlobalRules);
			// modelandview.addObject("rulefields", rulefields);
			modelandview.addObject("listdomain", listdomain);
			modelandview.addObject("synforruledomain", lstSynonyms);
			modelandview.addObject("currentSection", "Global Rule");
			modelandview.addObject("currentLink", "View Global Rules");

			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/editGlobalFilter", method = RequestMethod.GET)
	public ModelAndView editGlobalFilter(HttpServletRequest request, HttpSession session, @RequestParam long filterId,
			@RequestParam String domain, ModelAndView model) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		ModelAndView modelandview = new ModelAndView("editGlobalFilter");
		if (rbac) {

			int domain_id = GlobalRuleDAO.getDomainId(domain);

			GlobalFilters globalFilters = GlobalRuleDAO.getDataFromGlobalFilters(filterId, domain_id);

			System.out.println("############# domain_id =>" + domain_id);

			modelandview.addObject("globalFilters", globalFilters);
			modelandview.addObject("currentSection", "Global Rule");
			modelandview.addObject("currentLink", "View Global Filter");

			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createGlobalFilter", method = RequestMethod.POST)
	public void createGlobalFilter(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String filterName, @RequestParam String description, @RequestParam String domain,
			@RequestParam String filterCondition) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("domain:" + domain);

		GlobalFilters globalFilters = new GlobalFilters();
		System.out.println(domain);
		int domain_id = GlobalRuleDAO.getDomainId(domain);

		globalFilters.setDomainId(domain_id);
		globalFilters.setFilterCondition(filterCondition);
		globalFilters.setFilterName(filterName);
		globalFilters.setDescription(description);

		String message = "";
		String status = "failed";

		// Check if the filter condition has valid synonym names
		JSONObject synonymStatusObj = globalRuleService.validateSynonymExpression(filterCondition, domain_id);

		String synonymStatus = synonymStatusObj.getString("status");

		if (!synonymStatus.equalsIgnoreCase("failed")) {
			message = "Could not create Global Filter";
			try {
				long filterId = GlobalRuleDAO.insertIntoGlobalFilters(globalFilters);
				System.out.println("filterId=" + filterId);
				if (filterId > 0) {
					message = "Global Filter created successfully";
					status = "success";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			message = synonymStatusObj.getString("message");

		try {
			JSONObject json = new JSONObject();
			json.put("status", status);
			json.put("message", message);
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/updateGlobalRule", method = RequestMethod.POST)
	public void updateExtendTemplateRule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name, @RequestParam long idListColrules, @RequestParam String description,
			@RequestParam String ruleExpression, @RequestParam String domain, @RequestParam String ruleCategory,
			@RequestParam long dataSource2, @RequestParam String matchingExpression,
			@RequestParam String externalDatasetName, @RequestParam Double ruleThreshold, @RequestParam long dimension,
			@RequestParam Integer filterId, @RequestParam Integer rightTemplateFilterId, @RequestParam String aggregateResultsEnabled) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// for negative Threshold -Mamta 19/5/2022
		String rulethr = request.getParameter("ruleThreshold");
		double value = Double.parseDouble(rulethr);

		if (value < 0)

		{
			System.out.println(value + "Threshold is negative");
			JSONObject json = new JSONObject();
			json.put("fail", "Please enter positive value for Rule Threshold");
			try {
				response.getWriter().println(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		} else {
			System.out.println(value + "Threshold is positive");
		}

		Map<String, String> map = new HashMap<String, String>();
		ListColGlobalRules lcr = new ListColGlobalRules();

		System.out.println(domain);
		int domain_id = GlobalRuleDAO.getDomainId(domain);
		System.out.println(domain_id);

		lcr.setIdListColrules(idListColrules);
		lcr.setRuleName(name);
		lcr.setRuleType(ruleCategory);
		lcr.setDomain_id(domain_id);
		lcr.setExternalDatasetName(externalDatasetName);
		lcr.setMatchingRules(matchingExpression);
		lcr.setIdRightData(dataSource2);
		lcr.setDescription(description);
		lcr.setExpression(ruleExpression);
		lcr.setRuleThreshold(ruleThreshold);
		lcr.setDimensionId(dimension);
		lcr.setFilterId(filterId);
		lcr.setAggregateResultsEnabled(aggregateResultsEnabled);
		
		if(ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE) && rightTemplateFilterId != null && rightTemplateFilterId > 0)
			lcr.setRightTemplateFilterId(rightTemplateFilterId);
		else 
			lcr.setRightTemplateFilterId(0);
		
		System.out.println("name " + name);
		// update for synonymus
		ruleFields rc = null;
		int idListColrule = (int) idListColrules;
		// int rule_id = GlobalRuleDAO.getruleId(name, domain);
		if (session.getAttribute("editsynonyum") != null) {
			map = (Map<String, String>) session.getAttribute("editsynonyum");
		}
		/*
		 * try { int delete = GlobalRuleDAO.deleteGlobalRulesField(idListColrule);
		 * System.out.println("updating synonyms by deleting current syns ::: " +
		 * delete); } catch (Exception e) { e.printStackTrace(); }
		 */

		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());

			rc = new ruleFields();

			rc.setUsercolumns(entry.getKey());
			rc.setPossiblenames(entry.getValue());
			rc.setDomain_id(domain_id);

			int syn_id = GlobalRuleDAO.getsynosymId(rc);
			rc.setSynonyms_Id(syn_id);

			if (syn_id > 0) {
				GlobalRuleDAO.updateIntoSynonymsLibrary(rc);
			} else {
				System.out.println("insert synonyms into  synonym lib ");
				syn_id = GlobalRuleDAO.insertintoSynonymsLibrary(rc);

				System.out.println("inserting into rule syn mapping in case of  creating new  synonym");
				String query = "insert into ruleTosynonym" + "(rule_id,synonym_id) values(?,?)";
				int update = jdbcTemplate.update(query, new Object[] { idListColrule, syn_id });// here global rule
																								// id == syn id
				System.out.println("insert into ruleTosynonym=" + update);
			}
		}

		String finalStatus = "failed";
		String message = "Failed to update Global rule";
		JSONArray mappingErrors = null;

		JSONObject match_expr_status = null;
		JSONObject rule_expr_status = null;

		// Validate Matching condition
		if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
			match_expr_status = globalRuleService.validateSynonymExpression(matchingExpression, domain_id);
		}

		// Validate Rule Expression
		if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
				|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
			rule_expr_status = globalRuleService.validateSynonymExpression(ruleExpression, domain_id);
		}

		if (match_expr_status != null && match_expr_status.getString("status").equalsIgnoreCase("failed")) {
			message = match_expr_status.getString("message");

		} else if (rule_expr_status != null && rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
			message = rule_expr_status.getString("message");

		} else {
			try {
				int checkIfDuplicateSqlRowSet = GlobalRuleDAO.checkIfDuplicateGlobalRule(lcr);
				System.out.println("checkIfDuplicateSqlRowSet=" + checkIfDuplicateSqlRowSet);
				if (checkIfDuplicateSqlRowSet >= 1)
					message = "Rule already exists";
				else {
					// Save the rule
					GlobalRuleDAO.updateintolistColRules(lcr);
					finalStatus = "success";
					message = "Global Rule updated successfully";

					// Updating the rule Mapping in all associated templates
					System.out.println("\n====> Updating rule Mapping in all associated templates");
					JSONObject templateMappingStatusObj = globalRuleService
							.updateGlobalRuleMappingInAssociatedTemplates(lcr);

					finalStatus = templateMappingStatusObj.getString("status");

					if (finalStatus.equalsIgnoreCase("failed")) {
						message = templateMappingStatusObj.getString("message");
						mappingErrors = templateMappingStatusObj.getJSONArray("mappingErrors");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Send the response
		try {
			JSONObject json = new JSONObject();
			json.put("status", finalStatus);
			json.put("message", message);
			json.put("mappingErrors", mappingErrors);
			response.getWriter().println(json);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/updateGlobalFilter", method = RequestMethod.POST)
	public void updateGlobalFilter(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String filterName, @RequestParam long filterId, @RequestParam String description,
			@RequestParam String domain, @RequestParam String filterCondition) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String status = "failed";

		System.out.println("domain:" + domain);

		GlobalFilters globalFilter = new GlobalFilters();
		int domain_id = GlobalRuleDAO.getDomainId(domain);

		globalFilter.setDomainId(domain_id);
		globalFilter.setFilterCondition(filterCondition);
		globalFilter.setFilterName(filterName);
		globalFilter.setFilterId(filterId);
		globalFilter.setDescription(description);

		String message = "";
		JSONArray mappingErrors = null;

		// Check if the filter condition has valid synonym columns
		JSONObject synonymStatusObj = globalRuleService.validateSynonymExpression(filterCondition, domain_id);

		String synonymStatus = synonymStatusObj.getString("status");

		if (!synonymStatus.equalsIgnoreCase("failed")) {
			message = "Failed to update Global Filter";

			try {
				int updateStatus = GlobalRuleDAO.updateIntoGlobalFilters(globalFilter);

				if (updateStatus > 0) {

					message = "Global Filter updated successfully";
					System.out.println("\n====>" + message);
					status = "success";

					// Updating the filter condition rule Mapping in all associated templates
					System.out
							.println("\n====> Updating the filter conditiona rule Mapping in all associated templates");
					JSONObject templateMappingStatusObj = globalRuleService
							.updateGlobalFilterInAssociatedTemplates(globalFilter);

					status = templateMappingStatusObj.getString("status");

					if (status.equalsIgnoreCase("failed")) {
						message = templateMappingStatusObj.getString("message");
						mappingErrors = templateMappingStatusObj.getJSONArray("mappingErrors");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			message = synonymStatusObj.getString("message");

		try {
			JSONObject json = new JSONObject();
			json.put("status", status);
			json.put("message", message);
			json.put("mappingErrors", mappingErrors);
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/populateFilters", method = RequestMethod.GET)
	public @ResponseBody List populateFilters(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, @RequestParam String domain) throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("domain:" + domain);

		int domainId = GlobalRuleDAO.getDomainId(domain);

		List<GlobalFilters> globalFiltersList = GlobalRuleDAO.getAllGlobalFiltersByDomain(domainId);

		String firstElement = null;

		if (globalFiltersList != null && globalFiltersList.size() > 0) {
			firstElement = globalFiltersList.get(0).getFilterName();
		} else {
			System.out.println("\n====> Zero tables fetched");
		}

		System.out.println("\n====> filter name in the list: " + firstElement);

		return globalFiltersList;
	}

	@RequestMapping(value = "/getFilterConditionByName", method = RequestMethod.GET)
	public void getFilterConditionMapByName(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, @RequestParam String filterName, @RequestParam String domain)
			throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int domainId = GlobalRuleDAO.getDomainId(domain);

		System.out.println("domainL" + domain);
		String status = "failed";
		JSONObject globalFilterConditionObj = null;
		try {
			globalFilterConditionObj = GlobalRuleDAO.getGlobalFilterConditionByName(filterName, domainId);
			if (globalFilterConditionObj.length() > 0)
				status = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		globalFilterConditionObj.put("status", status);
		response.getWriter().println(globalFilterConditionObj);
		response.getWriter().flush();
	}

	@RequestMapping(value = "/userrule", method = RequestMethod.POST)
	public @ResponseBody MultiValueMap userruleinfo(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam String singleRowData) {
		Map<String, String> tempmap = new HashMap<String, String>();
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		MultiValueMap map = new MultiValueMap();
		System.out.println("data=" + singleRowData);

		String s1 = singleRowData.replaceFirst("\\|\\|", "").substring(1, singleRowData.length() - 3);
		System.out.println(s1);
		String[] s2 = s1.trim().split("\\|\\|");
		System.out.println("s2 length " + s2.length);
		int i = 0;
		while (i < s2.length) {
			int j = 0;
			String key = s2[i + j];
			j++;
			String value = s2[i + j];
			tempmap.put(key, value);
			i = i + 2;
			System.out.println("given List :: " + tempmap);
		}
		session.setAttribute("userrule", tempmap);
		return map;
	}
	// editRuleSynonym by pravin

	@RequestMapping(value = "/editRuleSynonym", method = RequestMethod.POST)
	public @ResponseBody MultiValueMap editRuleSynonym(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam String singleRowData) {
		Map<String, String> tempmap = new HashMap<String, String>();
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		MultiValueMap map = new MultiValueMap();
		System.out.println("data=" + singleRowData);

		String s1 = singleRowData.replaceFirst("\\|\\|", "").substring(1, singleRowData.length() - 3);
		System.out.println(s1);
		String[] s2 = s1.trim().split("\\|\\|");
		System.out.println("s2 length " + s2.length);
		int i = 0;
		while (i < s2.length) {
			int j = 0;
			String key = s2[i + j];
			j++;
			String value = s2[i + j];
			tempmap.put(key, value);
			i = i + 2;
		}
		session.setAttribute("editsynonyum", tempmap);
		return map;
	}

	@RequestMapping(value = "/viewsynonyms_not_used")
	public ModelAndView getListSynonyms_not_used(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "R", session);
		if (rbac) {

			List<ruleFields> rulefields = GlobalRuleDAO.getListGlobalRulesSynonyms();
			for (ruleFields l : rulefields) {
				System.out.println(l.getDomain_id());
			}

			model.addObject("rulefields", rulefields);
			model.addObject("currentSection", "Global Rule");
			model.addObject("currentLink", "Synonym Library");
			model.setViewName("synonyms");

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/SaveSynonymsFromViewList", method = RequestMethod.POST, produces = "application/json")
	public void SaveSynonymsFromViewList(@RequestParam String SynonymsRecordToSave, HttpSession oSession,
			HttpServletResponse oResponse) {
		JSONObject oSynonymsRecordToSave = new JSONObject();
		JSONObject oJsonResponse = new JSONObject();

		DateUtility.DebugLog("SaveSynonymsFromViewList 01", "Begin save process");

		try {
			/* Get all data entry form entered by user (add or edit) */
			oSynonymsRecordToSave = new JSONObject(SynonymsRecordToSave);
			DateUtility.DebugLog("SaveSynonymsFromViewList 02",
					String.format("Read input form values from UI as \n%1$s\n", oSynonymsRecordToSave.toString()));

			oJsonResponse = globalRuleService.saveSynonymsFormFromViewList(oSynonymsRecordToSave);

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();

			DateUtility.DebugLog("SaveSynonymsFromViewList 03",
					String.format("Pushed response to UI as \n%1$s\n", oJsonResponse.toString()));
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/loadSynonymsViewList", method = RequestMethod.POST, produces = "application/json")
	public void loadSynonymsViewList(@RequestParam int LoadContext, HttpSession oSession,
			HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadSynonymsViewList 01",
					String.format("Begin controller processing with get data context as '%1$s'", LoadContext));

			oJsonResponse = globalRuleService.getSynonymsPageData(LoadContext);

			DateUtility.DebugLog("loadSynonymsViewList 02", "Got data sending to client");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private void DemoOfGettingAllPageOptionsLists() {
		HashMap<String, ArrayList<HashMap<String, String>>> aDroDownLists = JwfSpaInfra.getAppOptionsListsMap(
				jdbcTemplate,
				"GLOBAL_THRESHOLDS_OPTION, DQ_REVIEW_STATUS, DQ_APPROVAL_PROCESS_STATUS, DQ_APPROVE_STATUS");

		try {
			ObjectMapper oMapper = new ObjectMapper();
			String sList = oMapper.writeValueAsString(aDroDownLists);
			DateUtility.DebugLog("DemoOfGettingAllPageOptionsLists ()", sList);
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/viewsynonyms")
	public ModelAndView getSynonymsManagement(ModelAndView model, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lRbac = rbacController.rbac("Global Rule", "R", oSession);
		ModelAndView oModelView = new ModelAndView("loginPage");

		System.out.println("Hit new controller for sending new view");

		if ((oUser != null) || (oUser.equals("validUser")) && (lRbac)) {
			oModelView = new ModelAndView("synonymsManagement");
			oModelView.addObject("currentSection", "Global Rule");
			oModelView.addObject("currentLink", "Synonym Library");
		}

		return oModelView;
	}

	@RequestMapping(value = "/editsynonym", method = RequestMethod.GET)
	public ModelAndView editsynonyms(HttpServletRequest request, HttpSession session, @RequestParam long rule_id,
			ModelAndView model) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		if (rbac) {
			int domain_id = 1;
			Long projectId = (Long) session.getAttribute("projectId");
			List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
			ListColGlobalRules listDataSource = GlobalRuleDAO.getDataFromListDataSourcesOfIdData(rule_id);

			ModelAndView modelandview = new ModelAndView("editSynonym");
			modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
			modelandview.addObject("listDataSource", listDataSource);
			modelandview.addObject("currentSection", "Global Rule");
			modelandview.addObject("currentLink", "Edit Rule");
			return modelandview;
		} else
			return new ModelAndView("loginPage");
	}

}
