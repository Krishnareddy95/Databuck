package com.databuck.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.databuck.bean.Dimension;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.dao.IExtendTemplateRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.econstants.RuleActionTypes;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;

@Controller
public class ExtendTemplateRuleController {

	private final String CURRENT_MODULE_NAME = "Extend Template & Rule";

	@Autowired
	public LoginService loginService;
	
	@Autowired
	IExtendTemplateRuleDAO iExtendTemplateRuleDAO;

	@Autowired
	ITemplateViewDAO templateviewdao;
	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	private RBACController rbacController;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	public void setiExtendTemplateRuleDAO(IExtendTemplateRuleDAO iExtendTemplateRuleDAO) {
		this.iExtendTemplateRuleDAO = iExtendTemplateRuleDAO;
	}

	@RequestMapping(value = "/addNewRule", method = RequestMethod.GET)
	public ModelAndView addNewRule(HttpServletRequest request, HttpSession session,
			@RequestParam Optional<String> selectedLeftTemplateId) {
		
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		if(rbac){
			Long projectId= (Long)session.getAttribute("projectId");
		List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
		List<Dimension> getlistdimensionname = templateviewdao.getlistdimensionname();
		ModelAndView modelandview = new ModelAndView("extendTemplateAddNewRule");

		SqlRowSet rowset1 = iExtendTemplateRuleDAO.getOperatorsDataFromSymbol();

		Map<Long, String> operatorsdata = new LinkedHashMap<Long, String>();
		System.out.println("operators info start");
		while (rowset1.next()) {
			operatorsdata.put(rowset1.getLong(1), rowset1.getString(2));
		}
		System.out.println("operators info end");
		SqlRowSet rowset2 = iExtendTemplateRuleDAO.getFunctionssDataFromSymbol();

		Map<Long, String> functionsdata = new LinkedHashMap<Long, String>();
		System.out.println("functions info start");
		while (rowset2.next()) {
			functionsdata.put(rowset2.getLong(1), rowset2.getString(2));
		}
		System.out.println("functions info end");
		modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
		modelandview.addObject("getlistdimensionname", getlistdimensionname);
		modelandview.addObject("operatorsdata", operatorsdata);
		modelandview.addObject("functionsdata", functionsdata);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "Add New Rule");
		
		/*
		 * This optional parameter is used by Rule Catalog, to create a custom rule for
		 * the template of a specific validation
		 * 
		 * Since this is optional it works normally without this parameter.
		 */
		if (selectedLeftTemplateId.isPresent()) {
			modelandview.addObject("selectedLeftTemplateId", selectedLeftTemplateId.get());
		} else {
			modelandview.addObject("selectedLeftTemplateId", 0);
		}
		
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createExtendTemplateRule", method = RequestMethod.POST)
	public void createExtendTemplateRule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name, @RequestParam String description, @RequestParam String ruleCategory,
			@RequestParam long dataSource1, @RequestParam long dataSource2, @RequestParam String ruleExpression,
			@RequestParam String sSqlRuleQuery,
			@RequestParam String matchingExpression, @RequestParam String externalDatasetName,
			@RequestParam String matchType, @RequestParam String regularExprColumnName,@RequestParam double ruleThreshold,
			@RequestParam long domension, @RequestParam String anchorColumns) {
		boolean lIsDuplicateRule = false;
		JSONObject json = new JSONObject();
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);

		/* getting project id & domain of left DataTemplate from listDataSources */
		ListDataSource lds = listdatasourcedao.getDataFromListDataSourcesOfIdData(dataSource1);
		Integer domainId = lds.getDomain();
		Long projectId = (long) lds.getProjectId();

		// activedirectory flag check
					String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
					System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
					String createdByUser="";
					if(activeDirectoryFlag.equalsIgnoreCase("Y"))
					{
					 	createdByUser=(String) session.getAttribute("createdByUser");
						System.out.println("======= createdByUser in extendTemplateRule ===>"+createdByUser);
					}else {
						// getting createdBy username from createdBy userId
						System.out.println("======= idUser ===>"+idUser);

						createdByUser = userDAO.getUserNameByUserId(idUser);

						System.out.println("======= createdByUser in extendTemplateRule ===>"+createdByUser);
					}


		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("name=" + name);
		System.out.println("description=" + description);
		System.out.println("ruleCategory=" + ruleCategory);
		System.out.println("dataSource1=" + dataSource1);
		System.out.println("dataSource2=" + dataSource2);
		System.out.println("ruleExpression=" + ruleExpression);
		System.out.println("matchingExpression=" + matchingExpression);
		System.out.println("externalDatasetName=" + externalDatasetName);
		System.out.println("externalDatasetName=" + externalDatasetName);
		System.out.println("domensionid=" + domension);
		
		//for negative Threshold -Mamta 19/5/2022
		String rulethr = request.getParameter("ruleThreshold");
		double value = Double.parseDouble(rulethr);
				
	    if(value<0)
	    	
	    {
	       System.out.println(value + "Threshold is negative");
	        //JSONObject json = new JSONObject();
	        json.put("fail", "Please enter positive value for Rule Threshold");
          	try {
				response.getWriter().println(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	return;
	       
	    }
	    else {
		       System.out.println(value + "Threshold is positive");
		     }

		ListColRules lcr = new ListColRules();
		lcr.setIdData(dataSource1);
		lcr.setIdRightData(dataSource2);
		lcr.setRuleName(name);
		lcr.setDescription(description);
		lcr.setExpression(ruleExpression);
		lcr.setMatchingRules(matchingExpression);
		lcr.setCreatedByUser(createdByUser);
		lcr.setRuleType(ruleCategory);
		lcr.setRuleThreshold(ruleThreshold);
		lcr.setProjectId(projectId);
		lcr.setDomainId(domainId);
		lcr.setIdDimension(domension);
		lcr.setAnchorColumns(anchorColumns.trim());

		if (ruleCategory.equalsIgnoreCase("sql rule") || ruleCategory.equalsIgnoreCase("sql Internal rule")) {
			System.out.println("Sql Query = " + sSqlRuleQuery);
			lcr.setExpression(sSqlRuleQuery);
			lcr.setExternal("N");
		} else if (ruleCategory.equalsIgnoreCase("referential")) {
			//lcr.setRuleType("referential");
			lcr.setExternal("N");
		} else if (ruleCategory.equalsIgnoreCase("cross referential")) {
			//lcr.setRuleType("referential");
			lcr.setExternal("Y");
			lcr.setExternalDatasetName(externalDatasetName);
		} else if (ruleCategory.equalsIgnoreCase("orphan")) {
			//lcr.setRuleType("orphanreferential");
			lcr.setExternal("Y");
			lcr.setExpression("");
			lcr.setExternalDatasetName(externalDatasetName);
		} else {
			//lcr.setRuleType(ruleCategory);
			//need to change later
			lcr.setMatchType("Pattern");
			lcr.setExternalDatasetName(regularExprColumnName);
		}
		try {
			lIsDuplicateRule = iExtendTemplateRuleDAO.isExtendTemplateRuleAlreadyExists(lcr);

			if (lIsDuplicateRule) {
				json.put("fail", String.format("Rule with name '%1$s' already exists for selected data source. Kindly use different name.",name));
			} else {
				long idListColrules = iExtendTemplateRuleDAO.insertintolistColRules(lcr);
				System.out.println("\n====> idListColrules: " + idListColrules);

				if (idListColrules > 0l) {
					// When RuleCatalog is enabled, We need to add this new rule in all the
					// associated rule catalog
					boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

					if (isRuleCatalogEnabled) {
						System.out.println(
								"\n====> Adding the new custom rule in all the Associated validations RuleCatalogs");
						CompletableFuture.runAsync(() -> {
							ruleCatalogService.updateAssociatedRuleCatalogsForCustomRuleChange(idListColrules,
									RuleActionTypes.CREATE);
						});
					}

					json.put("success", String.format("Extended Template Rule '%1$s' created successfully", name));

				} else {
					json.put("fail", String.format("Extended Template Rule '%1$s' create failed", name));
				}
			}
			response.getWriter().println(json);
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/viewRules")
	public ModelAndView getListDataSource(HttpServletRequest oRequest, HttpSession oSession) {
		ModelAndView oModelAndView = null;
		Object oUser = oSession.getAttribute("user");

		boolean lModuleAccess = rbacController.rbac(CURRENT_MODULE_NAME, "R", oSession);
		lModuleAccess = (lModuleAccess) ? ( (oUser != null) && (oUser.equals("validUser")) ) : false;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("extendTemplateViewRules");

			oModelAndView.addObject("SelectedProjectId", (Long)oSession.getAttribute("projectId"));
			oModelAndView.addObject("currentSection", "Extend Template");
			oModelAndView.addObject("currentLink", "View Rules");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/getPaginatedCustomRulesList", method = RequestMethod.POST, produces = "application/json")
	public void getPaginatedCustomRulesList(HttpSession oSession, HttpServletRequest oRequest, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		ObjectMapper oObjectMapper = new ObjectMapper();

		Long nProjectId = 0l;
		Project oSelectedProject = null;
		List<Project> aProjectList = null;

		try {
			DateUtility.DebugLog("serverSideDataTableTest 01","Start of controller");

			nProjectId = (Long)oSession.getAttribute("projectId");
			oSelectedProject = iProjectDAO.getSelectedProject(nProjectId);
			//aProjectList = (List<Project>)oSession.getAttribute("userProjectList");
			aProjectList = loginService.getAllDistinctProjectListForUser(oSession);

			oJsonResponse.put("SelectedProjectId", nProjectId);
			oJsonResponse.put("AllProjectList", new JSONArray(oObjectMapper.writeValueAsString(aProjectList)));
			oJsonResponse.put("SecurityFlags", getSecurityFlags(CURRENT_MODULE_NAME, oSession));

			for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds", "SearchText" }) {
				oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
			}

			oJsonResponse.put("ViewPageDataList", getPaginatedCustomRulesJsonData(oPaginationParms));
			DateUtility.DebugLog("serverSideDataTableTest 02",String.format("End of controller, got data sending to client \n%1$s\n", oJsonResponse.get("SecurityFlags")));

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject getSecurityFlags(String sModuleName, HttpSession oSession) {
		String sSecurityAccessFlags = "{ 'Create': %1$s, 'Update': %2$s, 'Delete': %3$s }";

		sSecurityAccessFlags = String.format(sSecurityAccessFlags,
				rbacController.rbac(sModuleName, "C", oSession),
				rbacController.rbac(sModuleName, "U", oSession),
				rbacController.rbac(sModuleName, "D", oSession)
			);
		return new JSONObject(sSecurityAccessFlags);
	}

	//Mychanges from here
	
	@RequestMapping(value = "/getPaginatedExtendedList", method = RequestMethod.POST, produces = "application/json")
	public void getExtendList(HttpSession oSession, HttpServletRequest oRequest, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		ObjectMapper oObjectMapper = new ObjectMapper();

		Long nProjectId = 0l;
		Project oSelectedProject = null;
		List<Project> aProjectList = null;

		try {
			DateUtility.DebugLog("serverSideDataTableTest 01","Start of controller");

			nProjectId = (Long)oSession.getAttribute("projectId");
			oSelectedProject = iProjectDAO.getSelectedProject(nProjectId);
			//aProjectList = (List<Project>)oSession.getAttribute("userProjectList");
			aProjectList = loginService.getAllDistinctProjectListForUser(oSession);

			oJsonResponse.put("SelectedProjectId", nProjectId);
			oJsonResponse.put("AllProjectList", new JSONArray(oObjectMapper.writeValueAsString(aProjectList)));
			oJsonResponse.put("SecurityFlags", getSecurityFlags(CURRENT_MODULE_NAME, oSession));

			for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds", "SearchText" }) {
				oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
			}

			oJsonResponse.put("ViewPageDataList", getPaginatedExtendedJsonData(oPaginationParms));  //question
			DateUtility.DebugLog("serverSideDataTableTest 02",String.format("End of controller, got data sending to client \n%1$s\n", oJsonResponse.get("SecurityFlags")));

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject getSecurityFlag(String sModuleName, HttpSession oSession) {
		String sSecurityAccessFlags = "{ 'Create': %1$s, 'Update': %2$s, 'Delete': %3$s }";

		sSecurityAccessFlags = String.format(sSecurityAccessFlags,
				rbacController.rbac(sModuleName, "C", oSession),
				rbacController.rbac(sModuleName, "U", oSession),
				rbacController.rbac(sModuleName, "D", oSession)
			);
		return new JSONObject(sSecurityAccessFlags);
	}
	
	
	

	private JSONArray getPaginatedExtendedJsonData(HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] {
				"ExtendedTemplateName", "Description", "DataTemplate", "ProjectName", "Created_On", "CreatedBy", "IdDataBlend", "TemplateId"  
			};
		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		DateUtility.DebugLog("getPaginatedCustomRulesJsonData 01", String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			sOption1Sql = String.format("and (DATE(le.createdAt) between '%1$s' and '%2$s') \n", oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

			sOption2Sql = "and 1 = case when le.name like 'LIKE-TEXT' then 1 when le.createdByUser like 'LIKE-TEXT' then 1 when ls.name like 'LIKE-TEXT' then 1 else 0 end \n".replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			sDataSql = "";
			/*sDataSql = sDataSql + "select \n";
			sDataSql = sDataSql + "	la.idListColrules as RuleId, la.ruleName as RuleName, ls.idData as TemplateId, ls.name as TemplateName, \n";
			sDataSql = sDataSql + "	la.ruleType as RuleType, \n";
			sDataSql = sDataSql + "	case when lower(la.ruleType) in ('orphan','cross referential') then la.matchingRules else la.expression end as Expression, \n";
			sDataSql = sDataSql + "	case when ds.dimensionName IS NULL THEN '' ELSE ds.dimensionName END AS Dimension, \n";
			sDataSql = sDataSql + "	p.projectName as ProjectName, la.createdAt as CreatedAt, la.createdByUser as CreatedBy \n";
			sDataSql = sDataSql + "from listColRules la \n";
			sDataSql = sDataSql + "	left outer join listDataSources ls ON ls.idData = la.idData \n";
			sDataSql = sDataSql + "	left outer join dimension ds ON la.domensionId = ds.idDimension \n";
			sDataSql = sDataSql + "	left outer join project p ON ls.project_id = p.idProject \n";
			sDataSql = sDataSql + String.format(" where la.activeFlag='Y' and ls.project_id in (%1$s) \n", oPaginationParms.get("ProjectIds"));*/
			
			sDataSql = sDataSql + "select le.name as ExtendedTemplateName, le.description as Description, ls.name as DataTemplate,p.projectName as ProjectName ,le.createdAt as Created_On,le.createdByUser as CreatedBy,le.idDataBlend as IdDataBlend,le.idData as TemplateId \n";
			sDataSql = sDataSql + "from listDataBlend le \n";
			sDataSql = sDataSql + "left outer join listDataSources ls ON ls.idData = le.idData \n";
			sDataSql = sDataSql + "left outer join project p ON ls.project_id = p.idProject \n"; 
			sDataSql = sDataSql + String.format(" where ls.active='yes' and ls.project_id in (%1$s) \n", oPaginationParms.get("ProjectIds"));

			
			if ( oPaginationParms.get("SearchByOption").equalsIgnoreCase("1") ) {
				sDataSql = sDataSql + sOption1Sql + "limit 1000;";
			} else if ( oPaginationParms.get("SearchByOption").equalsIgnoreCase("2") ) {
					sDataSql = sDataSql + sOption2Sql + "limit 1000;";
			} else {
				sDataSql = sDataSql + sOption1Sql + sOption2Sql + "limit 1000;";
			}

			
			DateUtility.DebugLog("getPaginatedCustomRulesJsonData 02",String.format("Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql ));

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);
			aRetValue = new JSONArray(sDataViewList);

			DateUtility.DebugLog("getPaginatedCustomRulesJsonData 03",String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return aRetValue;
	}

	
	//heree..............
	
	
	
	
	
	
	private JSONArray getPaginatedCustomRulesJsonData(HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] {
				"RuleId", "RuleName", "TemplateId", "TemplateName", "RuleType", "Expression", "Dimension", "ProjectName", "CreatedAt", "CreatedBy"
			};

		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		DateUtility.DebugLog("getPaginatedCustomRulesJsonData 01", String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			sOption1Sql = String.format("and (la.createdAt between '%1$s' and '%2$s') \n", oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

			sOption2Sql = "and 1 = case when la.ruleName like 'LIKE-TEXT' then 1 when ls.name like 'LIKE-TEXT' then 1 when la.expression like 'LIKE-TEXT' then 1 else 0 end \n".replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			sDataSql = "";
			sDataSql = sDataSql + "select \n";
			sDataSql = sDataSql + "	la.idListColrules as RuleId, la.ruleName as RuleName, ls.idData as TemplateId, ls.name as TemplateName, \n";
			sDataSql = sDataSql + "	la.ruleType as RuleType, \n";
			sDataSql = sDataSql + "	case when lower(la.ruleType) in ('orphan','cross referential') then la.matchingRules else la.expression end as Expression, \n";
			sDataSql = sDataSql + "	case when ds.dimensionName IS NULL THEN '' ELSE ds.dimensionName END AS Dimension, \n";
			sDataSql = sDataSql + "	p.projectName as ProjectName, la.createdAt as CreatedAt, la.createdByUser as CreatedBy \n";
			sDataSql = sDataSql + " from listColRules la \n";
			sDataSql = sDataSql + "	left outer join listDataSources ls ON ls.idData = la.idData \n";
			sDataSql = sDataSql + "	left outer join dimension ds ON la.domensionId = ds.idDimension \n";
			sDataSql = sDataSql + "	left outer join project p ON ls.project_id = p.idProject \n";
			sDataSql = sDataSql + String.format(" where la.activeFlag='Y' and ls.project_id in (%1$s) \n", oPaginationParms.get("ProjectIds"));
			
			if ( oPaginationParms.get("SearchByOption").equalsIgnoreCase("1") ) {
				sDataSql = sDataSql + sOption1Sql + " ORDER BY la.createdAt desc limit 1000;";
			} else if ( oPaginationParms.get("SearchByOption").equalsIgnoreCase("2") ) {
					sDataSql = sDataSql + sOption2Sql + " ORDER BY la.createdAt desc limit 1000;";
			} else {
				sDataSql = sDataSql + sOption1Sql + sOption2Sql + " ORDER BY la.createdAt desc limit 1000;";
			}

			DateUtility.DebugLog("getPaginatedCustomRulesJsonData 02",String.format("Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql ));

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);
			aRetValue = new JSONArray(sDataViewList);

			DateUtility.DebugLog("getPaginatedCustomRulesJsonData 03",String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return aRetValue;
	}

	private JSONArray getPageSlicedCustomRulesJsonData(JSONArray aJsonFullDataList, HashMap<String, String> oPaginationParms) {
		int nTotalDataRecords = aJsonFullDataList.length();
		int nStartIndex, nEndIndex, nUiStartIndex, nUiPageSize = 0;
		JSONArray aRetValue = new JSONArray();

		try {
			nUiStartIndex = Integer.parseInt(oPaginationParms.get("start"));
			nUiPageSize = Integer.parseInt(oPaginationParms.get("length"));

			if (nTotalDataRecords > 0) {
				if (nTotalDataRecords <= nUiPageSize) {
					aRetValue = aJsonFullDataList;
				} else {
					nStartIndex = nUiStartIndex;
					nEndIndex = nStartIndex + nUiPageSize;

					nEndIndex = (nTotalDataRecords <= nEndIndex) ? nTotalDataRecords : nEndIndex;

					for (int nIndex = nStartIndex; nIndex < nEndIndex; nIndex++) {
						aRetValue.put(aJsonFullDataList.get(nIndex));
					}
				}
			}
		} catch (Exception oException) {
			aRetValue = new JSONArray();
			oException.printStackTrace();
		}
		return aRetValue;
	}

	@RequestMapping(value = "/deleteIdListColRulesData", method = RequestMethod.POST)
	public void deleteIdListColRulesData(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam long idListColrules) {
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
		System.out.println("idListColrules=" + idListColrules);

		/*
		 * When rule catalog is enabled, deactivate the rule and delete it from all the
		 * associated rule catalogs
		 *
		 * When rule catalog is disabled, delete it from table
		 */
		boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

		if (isRuleCatalogEnabled) {
			CompletableFuture.runAsync(() -> {
				ruleCatalogService.updateAssociatedRuleCatalogsForCustomRuleChange(idListColrules,
						RuleActionTypes.DELETE);
			});
		} else {
			System.out.println("\n====> Deleting the rule from the table ..");
			templateviewdao.deleteIdListColRulesData(idListColrules);
		}

		try {
			response.getWriter().println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/editExtendTemplateRule", method = RequestMethod.GET)
	public ModelAndView editExtendTemplate(HttpServletRequest request, HttpSession session,
			@RequestParam long idListColrules, ModelAndView model) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		if(rbac){
			Long projectId= (Long)session.getAttribute("projectId");
		List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
		ListColRules listColRules = iExtendTemplateRuleDAO.getListColRulesById(idListColrules);
		System.out.println("Match expression " + listColRules.getMatchingRules());
		ModelAndView modelandview = new ModelAndView("editExtendTemplateRule");

		SqlRowSet rowset1 = iExtendTemplateRuleDAO.getOperatorsDataFromSymbol();

		Map<Long, String> operatorsdata = new LinkedHashMap<Long, String>();
		System.out.println("operators info start");
		while (rowset1.next()) {
			operatorsdata.put(rowset1.getLong(1), rowset1.getString(2));
		}
		System.out.println("operators info end");
		SqlRowSet rowset2 = iExtendTemplateRuleDAO.getFunctionssDataFromSymbol();

		Map<Long, String> functionsdata = new LinkedHashMap<Long, String>();
		System.out.println("functions info start");
		while (rowset2.next()) {
			functionsdata.put(rowset2.getLong(1), rowset2.getString(2));
		}
		System.out.println("functions info end");
		List<Dimension> getlistdimensionname = templateviewdao.getlistdimensionname();

		modelandview.addObject("getlistdimensionname", getlistdimensionname);
		modelandview.addObject("operatorsdata", operatorsdata);
		modelandview.addObject("functionsdata", functionsdata);
		modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
		modelandview.addObject("listDataSource", listColRules);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "Edit Rule");
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	//Sumeet_08_08_2018
	@RequestMapping(value = "/updateExtendTemplateRule", method = RequestMethod.POST)
	public void updateExtendTemplateRule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name,@RequestParam long idListColrules, @RequestParam String description, @RequestParam String ruleCategory,
			@RequestParam long dataSource1, @RequestParam long dataSource2, @RequestParam String ruleExpression,
			@RequestParam String sSqlRuleQuery,
			@RequestParam String matchingExpression, @RequestParam String externalDatasetName,
			@RequestParam String matchType, @RequestParam String regularExprColumnName, @RequestParam double ruleThreshold, 
			@RequestParam long dimension, @RequestParam String anchorColumns) {
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
		//for negative Threshold -Mamta 19/5/2022
		String rulethr = request.getParameter("ruleThreshold");
		double value = Double.parseDouble(rulethr);
				
	    if(value<0)
	    	
	    {
	       System.out.println(value + "Threshold is negative");
	        JSONObject json = new JSONObject();
	        json.put("failed", "Please enter positive value for Rule Threshold");
          	try {
				response.getWriter().println(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	return;
	       
	    }
	    else {
		       System.out.println(value + "Threshold is positive");
		     }
	    
		String sExpression = (ruleCategory.equalsIgnoreCase("SQL Internal Rule")
				|| ruleCategory.equalsIgnoreCase("sql rule")) ? sSqlRuleQuery : ruleExpression;

		/* Pradeep fix for special treatment of ' character in custom rule edit save */
		System.out.println(String.format("Custom rule fields coming from UI %1$s,%2$s,%3$s", idListColrules, ruleExpression, matchingExpression));
		if (sExpression != null) {
			sExpression = sExpression.trim().replace("'", "\\'");
			// Query compatibility changes for POSTGRES
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sExpression = sExpression.trim().replaceAll("'", "''");
			}
		}
		if (matchingExpression != null) {
			matchingExpression = matchingExpression.trim().replace("'", "\\'");
			// Query compatibility changes for POSTGRES
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				matchingExpression = matchingExpression.trim().replaceAll("'", "''");
			}
		}

		ListColRules lcr = new ListColRules();
		lcr.setIdData(dataSource1);
		lcr.setIdListColrules(idListColrules);
		lcr.setIdRightData(dataSource2);
		lcr.setRuleName(name);
		lcr.setDescription(description);
		lcr.setExpression(sExpression);
		lcr.setMatchingRules(matchingExpression);
		lcr.setRuleThreshold(ruleThreshold);
		lcr.setIdDimension(dimension);
		lcr.setAnchorColumns(anchorColumns.trim());

		try{
			//Sumeet_10_08_2018
			iExtendTemplateRuleDAO.updateintolistColRules(lcr);

			// When RuleCatalog is enabled, We need to update modified rule details in all
			// the associated validations
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

			if (isRuleCatalogEnabled) {
				System.out.println(
						"\n====> Updating the custom rule changes in all the Associated validations RuleCatalogs");
				CompletableFuture.runAsync(() -> {
					ruleCatalogService.updateAssociatedRuleCatalogsForCustomRuleChange(idListColrules,
							RuleActionTypes.MODIFIED);
				});
			}

			try {
				JSONObject json = new JSONObject();
				 json.put("success", "Extended Template Rule updated successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}catch (Exception e) {
				e.printStackTrace();
				iExtendTemplateRuleDAO.updateintolistColRules(lcr);
				try {
					JSONObject json = new JSONObject();
					 json.put("success", "Extended Template Rule updated successfully");
					response.getWriter().println(json);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
}
