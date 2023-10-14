package com.databuck.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.ListDataSchema;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.GloabalRule;
import com.databuck.bean.ListAdvancedRules;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IExtendTemplateRuleDAO;
import com.databuck.dao.IImportExportUtilityDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.econstants.RuleActionTypes;
import com.databuck.econstants.TemplateRunTypes;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.ITaskService;
import com.databuck.service.RBACController;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.DateUtility;

@Controller
public class DataProfilingTemplateController {

	@Autowired
	DataProfilingTemplateService dataProilingTemplateService;

	@Autowired
	private RBACController rbacController;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private IExtendTemplateRuleDAO extendTemplateDao;

	@Autowired
	private ITemplateViewDAO templateViewDAO;

	@Autowired
	public ITaskService iTaskService;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private Properties resultDBConnectionProperties;
	
	@Autowired
	private IImportExportUtilityDAO importExportDao;
	
	@Autowired
	private ITaskDAO iTaskDAO;
	
	@Autowired
	IValidationCheckDAO validationcheckdao;
	
	@Autowired
	ITemplateViewDAO templateviewdao;
	
	@Autowired
	SchemaDAOI schemaDao;
	
	@Autowired
	private RuleCatalogService ruleCatalogService;
	
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@RequestMapping(value = "/createDataTemplate", method = RequestMethod.POST)
	public @ResponseBody ModelAndView uploadFileHandler(@RequestParam("dataupload") MultipartFile file,
			HttpSession session, HttpServletRequest request, HttpServletResponse res)
			throws IOException, URISyntaxException {
		System.out.println("***** Entered DataProfilingTemplateController ******");

		Object user = session.getAttribute("user");
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		System.out.println("user:" + user);

		
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		DateUtility.DebugLog("createDataTemplate 01", String.format("activeDirectoryFlag = %1$s", activeDirectoryFlag));
		String createdByUser = (activeDirectoryFlag.equalsIgnoreCase("Y"))? (String) session.getAttribute("createdByUser") : userDAO.getUserNameByUserId(idUser);
		DateUtility.DebugLog("createDataTemplate 02", String.format("createdByUser = %1$s", createdByUser));

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		// changes regarding kafka
		String datalocation = request.getParameter("location");
		String HostURI = null;
		String folder = null;

		if (datalocation.equals("File Management") || datalocation.equals("File System") || datalocation.equals("HDFS")
				|| datalocation.equals("S3") || datalocation.equals("MapR FS") || datalocation.equals("MapR DB")
				|| datalocation.equals("AzureDataLakeStorageGen2")) {

			HostURI = request.getParameter("hostName");
			folder = request.getParameter("schemaName");

		} else if (datalocation.equals("Kafka")) {
			HostURI = request.getParameter("src_brokerUri");
			folder = request.getParameter("src_topicName");
		}

		String dataFormat = request.getParameter("source");
		String userlogin = request.getParameter("userName");
		String password = request.getParameter("pwd");
		String schemaName = request.getParameter("schemaName");
		String tar_brokerUri = request.getParameter("tar_brokerUri");
		String tar_topicName = request.getParameter("tar_topicName");
		System.out.println("tar_brokerUri ->" + tar_brokerUri);
		System.out.println("tar_topicName ->" + tar_topicName);

		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {
			System.out.println("hello create form");
			String DataTemplateName = request.getParameter("dataset");
			System.out.println("DataTemplateName=" + DataTemplateName);
			String description = request.getParameter("description");
			System.out.println("description=" + description);

			String schema = request.getParameter("schemaId1");
			Long idDataSchema = Long.valueOf(0L);
			if ((schema != null) && (!schema.equals("")))
				idDataSchema = Long.valueOf(Long.parseLong(schema));
			System.out.println("idDataSchema=" + idDataSchema);

			String headerId = request.getParameter("headerId");
			System.out.println("headerId=" + headerId);
			if (headerId == null)
				headerId = "N";
			String rowsId = request.getParameter("rowsId");
			System.out.println("rowsId=" + rowsId);

			// tablename for query

			String tableName = request.getParameter("tableNameid");
			System.out.println("tableName=" + tableName);

			if(tableName!=null && tableName.equals("") && session.getAttribute("FirstEleFromTablesList")!=null) {

				//tableName = request.getParameter("");
				System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tableName"+session.getAttribute("FirstEleFromTablesList"));

				tableName = session.getAttribute("FirstEleFromTablesList").toString();
			}

			System.out.println(".................After IF tableName=" + tableName);
			System.out.println("datalocation=" + datalocation);

			String whereCondition = request.getParameter("whereId");
			System.out.println("whereCondition=" + whereCondition);

			String queryCheckbox = request.getParameter("querycheckboxid");
			System.out.println("queryCheckbox=" + queryCheckbox);
			if (queryCheckbox == null)
				queryCheckbox = "N";

			String historicDateTable = request.getParameter("historicDateTable");
			System.out.println("historicDateTable=" + historicDateTable);
			
			if(queryCheckbox != null && queryCheckbox.equalsIgnoreCase("Y")) {
				historicDateTable = (historicDateTable != null)? historicDateTable.trim():"";
			} else {
				historicDateTable = "";
			}
			
			String lstTable = request.getParameter("selectedTables");
			System.out.println("Selected Tables=" + lstTable);
			String queryTextbox = request.getParameter("querytextboxid");
			System.out.println("queryTextbox=" + queryTextbox);

			System.out.println("@@@@@@@@@@@@@@@@@ lstTable =>" + lstTable);

			// Property to check if query have to be applied on File
			String fileQueryCheckBoxId = request.getParameter("filequerycheckboxid");
			System.out.println("fileQueryCheckBoxId=" + fileQueryCheckBoxId);
			if (fileQueryCheckBoxId == null)
				fileQueryCheckBoxId = "N";

			// Property to get the query which have to be applied on File
			String fileQueryTextbox = request.getParameter("filequerytextboxid");
			System.out.println("filequerytextboxid=" + fileQueryTextbox);

			/*
			 * When datalocation is "File System and query is enabled on it
			 */
			if(datalocation.equals("File System") && fileQueryCheckBoxId.equalsIgnoreCase("Y")
					&& fileQueryTextbox!=null && !fileQueryTextbox.trim().isEmpty()) {
				queryCheckbox = "Y";
				queryTextbox = fileQueryTextbox;
				System.out.println("queryCheckbox=" + queryCheckbox);
				System.out.println("queryTextbox=" + queryTextbox);
			}

			String incrementalType = request.getParameter("incrementalsourceid");
			System.out.println("incrementalType=" + incrementalType);
			String dateFormat = request.getParameter("dateformatid");
			System.out.println("dateFormat=" + dateFormat);
			String sliceStart = request.getParameter("slicestartid");
			System.out.println("sliceStart=" + sliceStart);
			String sliceEnd = request.getParameter("sliceendid");
			System.out.println("sliceEnd=" + sliceEnd);
			String profilingEnabled = request.getParameter("profilingEnabled");
			System.out.println("profilingEnabled=" + profilingEnabled);

			/* Pradeep 3/3/2020 Global threshold CR List<GloabalRule> selected_list no longer used so set as null to nullify effect */
			//List<GloabalRule> selected_list = getSelectedRuleDetails(session);
			List<GloabalRule> selected_list = null;
			String sDomainOptionSelected = request.getParameter("domainfunction");

			/* Pradeep 18-Jan-2021 Domain and Project to be picked up from connection, domain dropdown removed from UI */
			HashMap<String, Long> oDomainProject = schemaDao.getDomainProjectFromSchema(idDataSchema);
			Long projectId = oDomainProject.get("ProjectId");
			int nDomainId = oDomainProject.get("DomainId").intValue();
			
			// When idDataSchema is null then take the current project domain
			if(idDataSchema == null || idDataSchema <= 0l) {
				projectId = (Long) session.getAttribute("projectId");
				nDomainId = (Integer) session.getAttribute("domainId");
			}

			String advancedRulesEnabled = request.getParameter("advancedRulesEnabled");
			System.out.println("advancedRulesEnabled=" + advancedRulesEnabled);

			// Rolling Header Info
			String rollingHeaderPresent = request.getParameter("rollingHeaderPresent");
			System.out.println("rollingHeaderPresent=" + rollingHeaderPresent);

			String rollingColumn = request.getParameter("rollingColumn");
			System.out.println("rollingColumn=" + rollingColumn);

			if(rollingHeaderPresent == null || !rollingHeaderPresent.equalsIgnoreCase("Y")) {
				rollingHeaderPresent = "N";
			}

			ModelAndView modelAndView = new ModelAndView("dataTemplateStatus");
			String message = "Data Template creation is in progress, status will be notified to registered email !!";

			if (datalocation.equals("File Management") || datalocation.equals("Kafka")
					|| (queryCheckbox.equalsIgnoreCase("N") && lstTable != null && lstTable.split(",").length > 1)) {
				modelAndView.setViewName("Data Template Created Successfully");
			}

			// Create multiple template
			if (!(datalocation.equals("File Management") || datalocation.equals("File System")
					|| datalocation.equals("HDFS") || datalocation.equals("S3") || datalocation.equals("Kafka")
					|| datalocation.equals("MapR FS") || datalocation.equals("AzureDataLakeStorageGen2")) 
					&& queryCheckbox.equalsIgnoreCase("N") && lstTable != null
					&& !lstTable.trim().isEmpty() && lstTable.split(",").length > 1) {

				dataProilingTemplateService.createTemplatesAsync(session, idDataSchema.longValue(), datalocation,
						tableName, DataTemplateName, description, schema, headerId, rowsId, whereCondition,
						queryCheckbox, lstTable, queryTextbox, incrementalType, dateFormat, sliceStart, sliceEnd,
						idUser, HostURI, folder, dataFormat, userlogin, password, schemaName, file, tar_brokerUri,
						tar_topicName, profilingEnabled, advancedRulesEnabled, selected_list,rollingHeaderPresent,
						rollingColumn, historicDateTable, createdByUser, projectId);

			}

			// Create single template
			else {

				if (!(datalocation.equals("File Management") || datalocation.equals("File System")
						|| datalocation.equals("HDFS") || datalocation.equals("S3") || datalocation.equals("Kafka")
						|| datalocation.equals("MapR FS") || datalocation.equals("AzureDataLakeStorageGen2")) 
						&& lstTable != null && !lstTable.trim().isEmpty()
						&& lstTable.split(",").length == 1) {

					String tablesList = lstTable.trim().replace("[", "").replace("]", "");
					tableName = tablesList.split(",")[0];
					tableName = tableName.replace("\"", "");
				}

				CompletableFuture<Long> result = dataProilingTemplateService.createDataTemplate(session, idDataSchema,
						datalocation, tableName, DataTemplateName, description, schema, headerId, rowsId,
						whereCondition, queryCheckbox, lstTable, queryTextbox, incrementalType, dateFormat, sliceStart,
						sliceEnd, idUser, HostURI, folder, dataFormat, userlogin, password, schemaName, file,
						tar_brokerUri, tar_topicName, profilingEnabled, selected_list, projectId, advancedRulesEnabled,createdByUser,
						rollingHeaderPresent,rollingColumn,historicDateTable,null,null);

				Long idData = 0l;
				try {
					idData = result.get();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if(idData!=null && idData != 0l) {
					dataTemplateAddNewDAO.populateColumnProfileMasterTable(idData,nDomainId);
	
					DateUtility.DebugLog("Rules Auto Discovery 03", "End processing/linking done");
	
					String uniqueId = "";
					try {
						uniqueId = dataProilingTemplateService.triggerDataTemplate(idData, datalocation, profilingEnabled,
								advancedRulesEnabled).get();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
							+ " is in queue for execution !!");
					
					modelAndView.addObject("templateId", String.valueOf(idData));
					modelAndView.addObject("uniqueId", uniqueId);
			  } else {
				  modelAndView.setViewName("Data Template Created Successfully");
				  message = "Data Template not Created Successfully";
			  }
			}

			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTAdd New");
			modelAndView.addObject("templateName", DataTemplateName);
			modelAndView.addObject("message",message);
			return modelAndView;
		}

		return new ModelAndView("loginPage");
	}
	/*
	 * Create derived data template to capture form data
	 */
	
	@RequestMapping(value = "/createDerivedDataTemplate", method = RequestMethod.POST)	
	public @ResponseBody ModelAndView derivedDataTemplated(	
			HttpSession session, HttpServletRequest request, HttpServletResponse res)	
			throws IOException, URISyntaxException {	
		System.out.println("***** Entered DerivedTemplateController ******");	
		Object user = session.getAttribute("user");	
		long idUser = (Long) session.getAttribute("idUser");	
		System.out.println("idUser=" + idUser);	
		System.out.println("user:" + user);	
						String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");	
				System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);	
				String createdByUser="";	
				if(activeDirectoryFlag.equalsIgnoreCase("Y"))	
				{	
				 	createdByUser=(String) session.getAttribute("createdByUser");	
					System.out.println("======= createdByUser ===>"+createdByUser);	
				}else {	
					// getting createdBy username from createdBy userId	
					System.out.println("======= idUser ===>"+idUser);	
					createdByUser = userDAO.getUserNameByUserId(idUser);	
					System.out.println("======= createdByUser ===>"+createdByUser);	
				}	
		Long projectId= (Long)session.getAttribute("projectId");
		Integer domainId = (Integer)session.getAttribute("domainId");
		if ((user == null) || (!user.equals("validUser"))) {	
			return new ModelAndView("loginPage");	
		}	
		String datalocation = "Derived";	
		
			
		boolean rbac = rbacController.rbac("Data Template", "C", session);	
		if (rbac) {	
			System.out.println("hello create form");	
			String DataTemplateName = request.getParameter("dataset");	
			System.out.println("DataTemplateName=" + DataTemplateName);	
			String description = request.getParameter("description");	
			System.out.println("description=" + description);	
			String template1Value = request.getParameter("template1id");	
			System.out.println("template1Value=" + template1Value);	
			String template2Value = request.getParameter("template2id");	
			System.out.println("template2Value=" + template2Value);	
			String template1Name = request.getParameter("template1name");	
			System.out.println("template1=" + template1Name);	
			String template2Name = request.getParameter("template2name");	
			System.out.println("template2=" + template2Name);	
			String aliasNameTemplate1 = request.getParameter("aliasname1");	
			System.out.println("aliasNameTemplate1=" + aliasNameTemplate1);	
			String aliasNameTemplate2 = request.getParameter("aliasname2");	
			System.out.println("aliasNameTemplate1=" + aliasNameTemplate2);	
			String queryText = request.getParameter("querytext");	
			System.out.println("queryText=" + queryText);	
			ModelAndView modelAndView = new ModelAndView("dataTemplateStatus");
			String message = "Data Template creation is in progress, status will be notified to registered email !!";
			CompletableFuture<Long> result = dataProilingTemplateService.createDerivedDataTemplate(session, 	
						datalocation,  DataTemplateName, description, idUser,  projectId,template1Value, template2Value,template1Name, template2Name, 	
						aliasNameTemplate1,aliasNameTemplate2,queryText,createdByUser,domainId);	
			
			long idData = 0l;	
			try {	
				
				idData = result.get();	
			} catch (Exception e) {	
				e.printStackTrace();	
			}	
			if(idData > 0 && idData != 0l) {
				
				String uniqueId = "";
				try {
					uniqueId = dataProilingTemplateService.triggerDataTemplate(idData, datalocation, "N","N").get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("\n====> uniqueId: "+ uniqueId);

				modelAndView.addObject("templateId", String.valueOf(idData));
				modelAndView.addObject("uniqueId", uniqueId);
		  }
			 else {
			  modelAndView.setViewName("Data Template Created Successfully");
			  message = "Data Template not Created Successfully";
		  }
		

			modelAndView.addObject("currentSection", "Data Template");	
			modelAndView.addObject("currentLink", "DerivedDTAdd New");	
			modelAndView.addObject("templateName", DataTemplateName);	
			modelAndView.addObject("message",message);
			return modelAndView;	
		}	
		return new ModelAndView("loginPage");	
	}	

	private List<GloabalRule> getSelectedRuleDetails(HttpSession session){
		List<GloabalRule> selected_list = new ArrayList<GloabalRule>();

		List<String> selected_rule_ids = (List<String>) session.getAttribute("selected_rule_ids");
		System.out.println(selected_rule_ids);

		if (selected_rule_ids != null) {

			for (int i = 0; i < selected_rule_ids.size(); i++) {
				List<GloabalRule> selected_rules_details = (List<GloabalRule>) session
						.getAttribute("rulesForSelection");
				System.out.println("selected_rules_details +++++++++" + selected_rules_details);
				// List<ListColRules2>
				// selected_rules_details=getMainRuleExpression(Integer.parseInt(selected_rule_ids.get(i)));
				for (int z1 = 0; z1 < selected_rules_details.size(); z1++) {
					System.out.println(selected_rule_ids.get(i));
					System.out.println(selected_rules_details.get(z1).getGloabal_rule_id());
					if (Integer.parseInt(
							selected_rule_ids.get(i)) == (selected_rules_details.get(z1).getGloabal_rule_id())) {
						System.out.println("ruleid  is ::" + selected_rule_ids.get(i) + "rule name::"
								+ selected_rules_details.get(z1).getRule_name() + " rule expression:: "
								+ selected_rules_details.get(z1).getRule_expression()+" rule type:: "+selected_rules_details.get(z1).getRule_Type() );
						// System.out.println("modified rule expression "+
						// eligible_rules_formula.get(z1).getExpression().replaceAll(regex,
						// replacement));
						GloabalRule g = new GloabalRule();
						g.setGloabal_rule_id(Integer.parseInt(selected_rule_ids.get(i)));
						g.setRule_name(selected_rules_details.get(z1).getRule_name());
						g.setRule_expression(selected_rules_details.get(z1).getRule_expression());
						g.setRule_Type(selected_rules_details.get(z1).getRule_Type());
						selected_list.add(g);
					} else {
						System.out.println("No rules");
					}

				}
			}
		}

		System.out.println("selected rules and details " + selected_list);
		return selected_list;
}

	@RequestMapping(value = "/advancedRulesTemplateView", method = RequestMethod.GET)
	public ModelAndView getListDataSource(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForAdvancedRules(projectId,projList);
			model.addObject("listdatasource", listdatasource);
			model.setViewName("advancedRulesTemplateView");
			model.addObject("currentSection", "Dashboard");
			model.addObject("currentLink", "AdvRulesView");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/advancedRulesListView")
	public ModelAndView dataProfilingView(HttpServletRequest request, ModelAndView model, HttpSession session,
			HttpServletResponse response) throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);

		long idData = Long.parseLong(request.getParameter("idData"));

		String templateName = request.getParameter("templateName");
		String createdByUser = request.getParameter("createdByUser");

		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("advancedRulesListView");
			List<ListAdvancedRules> advancedRulesList = listdatasourcedao.getAdvancedRulesForId(idData);
			modelAndView.addObject("idData", idData);
			modelAndView.addObject("templateName", templateName);
			modelAndView.addObject("advancedRulesList", advancedRulesList);
			modelAndView.addObject("createdByUser", createdByUser);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "AdvRulesView");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/downloadAdvancedRules")
	public void downloadAdvancedRulesForId(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		try {
			long idData = Long.parseLong(request.getParameter("idData"));

			String fileName = request.getParameter("tableNickName");

			List<ListAdvancedRules> advancedRulesList = listdatasourcedao.getAdvancedRulesForId(idData);
			if (advancedRulesList != null && advancedRulesList.size() > 0) {

				OutputStream outStream = response.getOutputStream();

				String headerKey = "Content-Disposition";

				String headerValue = String.format("attachment; filename=\"%s\"", fileName + ".csv");

				response.setHeader(headerKey, headerValue);

				// Writing file header
				String fileHeader = "Rule_Id,Rule_Type,Column_Name,Rule_Expression,Rule_sql";
				outStream.write(fileHeader.getBytes());
				outStream.write("\n".getBytes());

				// Writing data to file
				for (ListAdvancedRules advRule : advancedRulesList) {
					outStream.write(advRule.toString().getBytes());
					outStream.write("\n".getBytes());
				}

			} else {
				String message = "No Advanced Rules available to download for Template Id [" + idData + "] !!";
				session.setAttribute("errormsg", message);

				JSONObject json = new JSONObject();
				json.put("fail", message);

				response.getWriter().println(json);
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while downloading advancedRule !!");
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/activateAdvancedRuleById")
	public void activateAdvancedRuleById(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();

		try {
			long idData = Long.parseLong(request.getParameter("idData"));

			long ruleId = Long.parseLong(request.getParameter("ruleId"));

			String ruleSql = request.getParameter("ruleSql");

			String columnName = request.getParameter("columnName");

			String ruleType = request.getParameter("ruleType");
			String createdByUser = request.getParameter("createdByUser");

			// Get project id & domain of left DataTemplate from listDataSources
			ListDataSource lds = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			Integer domainId = lds.getDomain();
			Long projectId = (long) lds.getProjectId();
			
			// Create a custom rule
			ListColRules listColRules = new ListColRules();

			String ruleName = ruleId + "_" + ruleType + "_" + columnName;

			System.out.println("\n===>Creating new custom rule :" + ruleName);

			System.out.println("ruleSql: " + ruleSql);

			listColRules.setIdData(idData);
			listColRules.setIdRightData(idData);
			listColRules.setRuleName(ruleName);
			listColRules.setExpression(ruleSql);
			listColRules.setMatchingRules("");
			listColRules.setRuleType("Referential");
			listColRules.setExternal("N");
			listColRules.setCreatedByUser(createdByUser);
			listColRules.setProjectId(projectId);
			listColRules.setDomainId(domainId);
			listColRules.setIdDimension(0l);
			listColRules.setAnchorColumns(columnName);
			extendTemplateDao.insertintolistColRules(listColRules);

			long idListColrules = extendTemplateDao.getCustomRuleByName(ruleName);

			if (idListColrules > 0l) {
				// Update the status to Y and set the custom rule id to Advanced rule
				listdatasourcedao.updateAdvancedRulesActiveStatus(ruleId, "Y", idListColrules);

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
				
				//json.put("success", "Extended Template Rule for AdvancedRule is created successfully");
				json.put("success", "Auto Discovered Rule is Deactivated successfully and Rule is created in Extend Template and Rule");

			} else {
				System.out.println("\n====>Failed to create custom rule for advancedRule with Id:" + ruleId);

				json.put("fail", "Extended Template Rule creation for AdvancedRule is failed");

			}

			response.getWriter().println(json);
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while activating advancedRule !!");
			e.printStackTrace();

			try {
				json.put("fail", "Extended Template Rule creation for AdvancedRule is failed");
				response.getWriter().println(json);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	@RequestMapping(value = "/deactivateAdvancedRuleById", method = RequestMethod.POST)
	public void deactivateAdvancedRuleById(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			long ruleId = Long.parseLong(request.getParameter("ruleId"));

			String customRuleId = request.getParameter("idListColrules");

			if (customRuleId != null && !customRuleId.trim().isEmpty()) {

				long idListColrules = Long.parseLong(customRuleId);

				if (idListColrules > 0l) {
					
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
						// Delete the custom rule
						templateViewDAO.deleteIdListColRulesData(idListColrules);
					}
					
					// Update the status to Y and set the custom rule id to Advanced rule
					listdatasourcedao.updateAdvancedRulesActiveStatus(ruleId, "N", null);

					//json.put("success", "AdvancedRule is deactivated successfully");
					json.put("success", "Auto Discovered Rule Activated Successfully");
				}

				response.getWriter().println(json);
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while deactivating advancedRule !!");
			e.printStackTrace();

			try {
				json.put("fail", "Deactivation of AdvancedRule is failed");
				response.getWriter().println(json);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	@RequestMapping(value = "/templateStatusPoll", method = RequestMethod.POST, produces = "application/json")
	public void startStatuspoll(HttpServletRequest request, HttpSession session, Long idData, String uniqueId,
			HttpServletResponse response) {

		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		request.setAttribute("currentSection", "Data Template");
		request.setAttribute("currentLink", "DTAdd New");

		String templateStatus = iTaskService.getTemplateCreationJobStatusById(idData, uniqueId);
		String status = "Task " + templateStatus;

		// for showing task % status
		int percentage = 5;
		percentage = iTaskService.getStatusOfDfReadTaskForTemplate(idData);
		if (percentage >= 30) {
			double count = iTaskService.getCountOfTasksEnabledForTemplate(idData);
			double passedStatus = iTaskService.getCountOfTasksPassedForTemplate(idData);
			percentage = percentage + (int) ((passedStatus / count) * 70);
			if (percentage > 100) {
				percentage = 100;
			}
		}

		// Check if job is completed, killed or failed and log it
		if (templateStatus != null && (templateStatus.equalsIgnoreCase("completed")
				|| templateStatus.equalsIgnoreCase("failed") || templateStatus.equalsIgnoreCase("killed"))) {
			System.out.println("\n====>Template with Id [" + idData + "] and uniqueId [" + uniqueId + "] is "
					+ templateStatus + "!!");
		}

		JSONObject json = new JSONObject();
		json.put("percentage", percentage);
		json.put("success", status);
		try {
			response.getWriter().println(json);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/createReferencesTemplate", method = RequestMethod.POST)
	public ModelAndView createReferencesTemplate(HttpSession session, HttpServletResponse oResponse,
			@RequestParam("configFile") MultipartFile file, @RequestParam("referencename") String newTemplateName
	) {
		JSONObject oJsonResponse = new JSONObject();
		String status = "failed";
		String fileDownloadStatus = "failed";
		String fileDownloadMessage = "";
		String uniqueId = "";
		Long templateId = 0l;
		String newdataTemplateName = "";
	   try {

		System.out.println("\n==========> referencesfile <==========");
		System.out.println("\n==========> referencesfile <==========" + newTemplateName);

		String filename = file.getOriginalFilename();

		String hostUri = System.getenv("DATABUCK_HOME") + "/referencesfile";

		String path = hostUri + "/" + filename;

		newdataTemplateName = "ref_" + newTemplateName;
		String dataTemplateName = filename.split("\\.")[0];

		if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

			oJsonResponse.put("msg", "successfully");
			
			// Check if the path exists
			File downloaded_file_dir = new File(hostUri);
			if(!downloaded_file_dir.exists()){
				downloaded_file_dir.mkdir();
			}
			
			// Download file to DATABUCK_HOME directory
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path)));) {
				byte[] bytes = file.getBytes();
				stream.write(bytes);
				fileDownloadStatus = "success";
				oJsonResponse.put("msg", "successfully");

			} catch (Exception e) {
				fileDownloadMessage = "\n====>Exception occurred while saving file to DATABUCK_HOME !!";
				System.out.println("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
				e.printStackTrace();
			}

			// When file download is success create template
			if (fileDownloadStatus.equalsIgnoreCase("success")) {
				oJsonResponse.put("msg", "successfully");
				
				// Getting createdByUser
				long idUser = (Long) session.getAttribute("idUser");
				
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				String createdByUser = (activeDirectoryFlag !=null && activeDirectoryFlag.trim().equalsIgnoreCase("Y"))
						? (String) session.getAttribute("createdByUser") 
						: userDAO.getUserNameByUserId(idUser);
				
				System.out.println("=== Created by in createTemplateForQuickStart ==>" + createdByUser);
				// -------------------------file read --------------------------------//
				Map<String, String> metadata = new LinkedHashMap<String, String>();
				List<String> columnNamesList = new ArrayList<String>();
				List<String> datatypes = new ArrayList<String>();
				datatypes.add("int");
				datatypes.add("char");
				datatypes.add("long");
				datatypes.add("float");
				datatypes.add("double");
				datatypes.add("varchar");
				datatypes.add("text");
				datatypes.add("string");
				datatypes.add("date");
				datatypes.add("number");

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
					oJsonResponse.put("msg", "successfully");
				} else {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate").split(",");
				String createColumns = "";
				String insertColumns = "";
				String insertValues = "";
				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
					dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
					InputStream inputStream = file.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					String line = br.readLine();
					String dataLine = br.readLine();
					oJsonResponse.put("msg", "successfully");
					if (line != null) {
						boolean isJSON = false;
						String[] dataValues = {};
						String[] headerValues = {};
						Map<String, String> jsonHM = new HashMap<String, String>();

						String splitBy = "\\,";

						if (!isJSON) {
							oJsonResponse.put("msg", "successfully");
							// This needs to be sorted
							if (line != null && dataLine != null) {
								dataValues = dataLine.split(splitBy, -1);
								headerValues = line.split(splitBy);
							}

							// if dataline is not provided, initiallize to string
							if (line != null && dataLine == null) {
								headerValues = line.split(splitBy);
								dataValues = new String[headerValues.length];
								for (int i = 0; i < headerValues.length; i++) {
									dataValues[i] = "";
								}
							}
						} else {
							headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
							dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
						}

						for (int i = 0; i < headerValues.length; i++) {
							String word = headerValues[i];
							String columnName = headerValues[i];
							String dataValue = dataValues[i];
							String columnType = "String";

							int first = word.lastIndexOf("(");
							int last = word.lastIndexOf(")");

							boolean isColumnTypePresent = false;
							if (first != -1 && last != -1) {
								isColumnTypePresent = true;
								if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
									columnType = word.substring(first + 1, last).trim();
									columnName = word.substring(0, first);
								} else {
									columnName = word.substring(0, last);
								}
							} else {
								columnName = word;
							}

							// Determine column type based on first row. This will
							// be done only if
							// column type is not already specified in csv.
							if (!isColumnTypePresent) {
								boolean isColumnTypeIdentified = false;
								if (dataValue.matches(numberRegex)) {
									isColumnTypeIdentified = true;
									columnType = "number";
								} else {
									for (int j = 0; j < dateRegexFormate.size(); j++) {
										String regex = dateRegexFormate.get(j).trim();
										if (dataValue.matches(regex) || (dataValue.length() >= 10
												&& dataValue.substring(0, 10).trim().matches(regex))) {
											columnType = "Date";
											isColumnTypeIdentified = true;
										}
									}
								}

								if (!isColumnTypeIdentified) {
									columnType = "varchar";
								}
							}

							columnName = columnName.trim();
							String modifiedColumn = "";
							String[] charArray = columnName.split("(?!^)");
							for (int j = 0; j < charArray.length; j++) {
								if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

									modifiedColumn = modifiedColumn + charArray[j];
								}
							}

							modifiedColumn = modifiedColumn.replace("-", "_");
							modifiedColumn = modifiedColumn.replace(".", "_");
							modifiedColumn = modifiedColumn.replace(" ", "_");

							System.out.println("columnName=" + modifiedColumn);
							System.out.println("columnType=" + columnType);
							metadata.put(modifiedColumn, columnType);
							columnNamesList.add(modifiedColumn);
						}

						for (Map.Entry m : metadata.entrySet()) {
							String Type;
							if (m.getValue() == "varchar") {
								Type = "varchar(500)";
							} else if (m.getValue() == "number") {
								// Query compatibility changes for both POSTGRES and MYSQL
								Type = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
										? "bigint"
										: "bigint(20)";
							} else {
								Type = "Date";
							}
							String createColumnslocal = m.getKey() + " " + Type + " NULL ,";
							createColumns = createColumns + createColumnslocal;
							String insertColumnslocal =  "" + m.getKey() + ",";
							insertColumns = insertColumns + insertColumnslocal;
							String insertValueslocal = "?,";
							insertValues = insertValues + insertValueslocal;
						}
					}

					createColumns = createColumns.substring(0, createColumns.length() - 1);
					insertColumns = insertColumns.substring(0, insertColumns.length() - 1);
					insertValues = insertValues.substring(0, insertValues.length() - 1);
					// ---------------------------End-----------------------------------//
					System.out.println("--->" + createColumns);
					
					String createQuery = "";
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
						createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
						+ "(dbk_row_Id serial PRIMARY KEY ," + createColumns +")";

					else 
					    createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
								+ "(dbk_row_Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT," + createColumns +")";
					
					jdbcTemplate1.execute(createQuery);
					InputStream inputStreams = file.getInputStream();
					BufferedReader bra = new BufferedReader(new InputStreamReader(inputStreams));
					String dataLine_1 = "";

					CSVReader csvReader = new CSVReaderBuilder(bra).withSkipLines(1).build();
					List<String[]> allData = csvReader.readAll();
					for(String[] data:allData){
						String insertvalues = "";
						for (int i = 0; i <= data.length - 1; i++) {
							String colName = columnNamesList.get(i);
							String colDataType = metadata.get(colName);
							String colValue = data[i];

							if (colDataType.equalsIgnoreCase("date")
									|| colDataType.toLowerCase().startsWith("varchar")) {
								colValue = colValue.replace("'", "''");
								String val = "'" + colValue + "',";
								insertvalues = insertvalues + val;

							} else {
								if (colValue == null || colValue.trim().isEmpty())
									colValue = null;
								insertvalues = insertvalues + colValue + ",";
							}

						}
						insertvalues = insertvalues.substring(0, insertvalues.length() - 1);

						String sql = "INSERT INTO " + dataTemplateName + "(" + insertColumns
								+ ") VALUES (" + insertvalues + ")";

						jdbcTemplate1.execute(sql);
					}

					Long projectId = (Long) session.getAttribute("projectId");
					List<GloabalRule> selected_list = null;
					String schema = resultDBConnectionProperties.getProperty("db1.schema.name");
					String pg_databaseSchemaName = resultDBConnectionProperties.getProperty("db1.postgres.databaseschema.name");
					String HostUri = resultDBConnectionProperties.getProperty("db1.url");
					String connUri= HostUri;

					int index = HostUri.indexOf("/");
					String ResultHost = HostUri.substring(index+2);
					String resultHostUri = ResultHost.substring(0, ResultHost.indexOf(":"));
					HostUri=resultHostUri;

					int index1 = ResultHost.indexOf(":");
					String ResultPort = ResultHost.substring(index1+1);
					String resultPort = ResultPort.substring(0, ResultPort.indexOf("/"));
					String port=resultPort;

					String userlogin = resultDBConnectionProperties.getProperty("db1.user");
					String password = resultDBConnectionProperties.getProperty("db1.pwd");

					String connectionName = dataTemplateName + "_con";
					int nDomainId = (Integer) session.getAttribute("domainId");
					
					// Get the connection available for the connection details else create new connection
					String connectionType ="";
					String dataLocation = "";
					// Query compatibility changes for both POSTGRES and MYSQL
					connectionType = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
							?"Postgres"
							:"MYSQL";
					dataLocation = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
							?"Postgres"
							:"MYSQL";
					schema = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
							? schema+","+pg_databaseSchemaName
							:schema;
					
					Long idDataSchema = schemaDao.getConnectionIdByDetails(connectionType, HostUri, port, schema, userlogin, password);

					String sslEnabled= "N";
					if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_MYSQL) &&  connUri.toLowerCase().contains("usessl=true")) {
						sslEnabled = "Y";
						System.out.println("\n====>SSL is enabled in connection Uri");
					}else
						System.out.println("\n====>SSL is not enabled in connection Uri");

					if(idDataSchema == null || idDataSchema <= 0l) {
						
						// Create schema
						idDataSchema = schemaDao.saveDataIntoListDataSchema(HostUri, schema, userlogin, password,
								port, connectionName, dataLocation, "", "", "", "N", "", "", "", "", sslEnabled, "",
								"", "", "", "", "", "", "", "", "", "", "", projectId, createdByUser, "", "", "",
								Long.toString(projectId), "", "", "", "", "", "", "", "", "",
								"", "N", "N", "N", 0, 0, 2, "N", nDomainId, "N","","","","","","","Y","N","N","N","local","N","N","","","");

					}else if(sslEnabled.equalsIgnoreCase("Y")){
						//update connection to ssl if not enabled
						List<ListDataSchema> listdataschema= listdatasourcedao.getListDataSchemaForIdDataSchema(idDataSchema);

						if(!listdataschema.get(0).getSslEnb().equalsIgnoreCase("Y")){
							boolean sslEnableStatus= listdatasourcedao.enableSSLForConnectionById(idDataSchema);

							if(!sslEnableStatus)
								System.out.println("\n====>Could not enable SSL for connection Id["+idDataSchema+"]");
							else
								System.out.println("\n====>SSL is enabled for connection Id["+idDataSchema+"]");
						}
					}

					if (idDataSchema != null && idDataSchema > 0l) {
						// update clusterpropertyCategory to local
						schemaDao.updateClusterPropertyCategoryByIdDataSchema(idDataSchema,"local");

						// Create a template
						CompletableFuture<Long> result = dataProilingTemplateService.createDataTemplate(session,
								idDataSchema, dataLocation, dataTemplateName, newdataTemplateName, "", schema, "", "", "",
								"N", "", "", "", "", "", "", idUser, HostUri, schema, "", "", "", schema, file, "", "",
								"N", selected_list, projectId, "N", createdByUser, "N", null, "", null, null);

						templateId = result.get();

						System.out.println("TemplateId: " + templateId);

						if (templateId != null && templateId > 0l) {
							System.out.println("\n===> Placing template in queue ... ");

							iTaskService.insertTaskListForTemplate(templateId, "N", "N");

							uniqueId = iTaskDAO.placeTemplateJobInQueue(templateId,
									TemplateRunTypes.newtemplate.toString());
							status = "success";
							fileDownloadMessage = "Reference Template Creation is in progress!!";

							String message = "Data Template creation is in progress, status will be notified to registered email !!";

							ModelAndView modelAndView = new ModelAndView("dataTemplateStatus");
							modelAndView.addObject("currentSection", "Global Rule");
							modelAndView.addObject("currentLink", "References View");
							modelAndView.addObject("templateId", templateId);
							modelAndView.addObject("uniqueId", uniqueId);
							modelAndView.addObject("templateName", newdataTemplateName);
							modelAndView.addObject("message", message);
							return modelAndView;

						} else {
							System.out.println("\n====> Failed to create connection, Reference Template Creation failed !");
							fileDownloadMessage = "Reference Template Creation failed!!";
						}

					} else {
						fileDownloadMessage = "Reference Template Creation failed!!";
					}
					

				} catch (Exception e) {
					fileDownloadMessage = "Reference Template Creation failed!!";
					e.printStackTrace();
				}

				
			} else {
				System.out.println("\n====>Failed to download the file to DATABUCK_HOME!!");
				fileDownloadMessage = "Exception occurred while saving file to DATABUCK_HOME !!";
			}
		} else {
			System.out.println("\n====>Unable to process this file!! Currently this feature supports only csv files!!");
			fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
		}
	  } catch(Exception e) {
			fileDownloadMessage = "Error occurred while creating reference template!!";
			e.printStackTrace();
	  }
	  
	  ModelAndView modelAndView = new ModelAndView("Data Template Created Successfully");
	  modelAndView.addObject("currentSection", "Global Rule");
	  modelAndView.addObject("currentLink", "Add Internal References");
	  modelAndView.addObject("message", fileDownloadMessage); 
	  return modelAndView;
			  
	}
	
	@RequestMapping(value = "/rerunTemplateProfiling", method = RequestMethod.GET)
	public ModelAndView rerunTemplateProfiling(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long idData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Data Template", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("dataTemplateStatus");
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTView");

			ListDataSource listDataSource = importExportDao.getDataFromListDataSources(idData);
			if (listDataSource != null) {

				// Check Current status of template
				boolean isTemplateInProgress = iTaskDAO.isTemplateJobQueuedOrInProgress(idData);

				if (!isTemplateInProgress) {

					// Place template to queue
					String uniqueId = iTaskDAO.placeTemplateJobInQueue(idData,
							TemplateRunTypes.profilingrerun.toString());
					iTaskService.insertTaskListForTemplate(idData, listDataSource.getProfilingEnabled(),
							listDataSource.getAdvancedRulesEnabled());

					modelAndView.addObject("templateId", String.valueOf(idData));
					modelAndView.addObject("uniqueId", uniqueId);
					modelAndView.addObject("templateName", listDataSource.getName());
					modelAndView.addObject("message",
							"Profiling ReRun is in progress, status will be notified to registered email !!");
				} else {
					System.out.println("\n====> rerunTemplateProfiling - Template is queued or already in progress !!");
					modelAndView.setViewName("Data Template Created Successfully");
					modelAndView.addObject("message", "Template is queued or already in progress !!");
				}
			} else {
				System.out.println("\n====> rerunTemplateProfiling - Failed to get Template details !!");
				modelAndView.setViewName("Data Template Created Successfully");
				modelAndView.addObject("message", "Failed to reRun Profiling !!");
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/rerunTemplate", method = RequestMethod.GET)
	public ModelAndView rerunTemplate(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long idData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Data Template", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("dataTemplateStatus");
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTView");

			ListDataSource listDataSource = importExportDao.getDataFromListDataSources(idData);
			if (listDataSource != null) {

				// Check Current status of template
				boolean isTemplateInProgress = iTaskDAO.isTemplateJobQueuedOrInProgress(idData);
				
				if (!isTemplateInProgress) {

					// Place template to queue
					String uniqueId = "";
					if (listDataSource.getTemplateCreateSuccess() != null
							&& listDataSource.getTemplateCreateSuccess().equalsIgnoreCase("N")) {
						uniqueId = iTaskDAO.placeTemplateJobInQueue(idData, TemplateRunTypes.newtemplate.toString());
					} else {
						uniqueId = iTaskDAO.placeTemplateJobInQueue(idData, TemplateRunTypes.templatererun.toString());
					}

					iTaskService.insertTaskListForTemplate(idData, listDataSource.getProfilingEnabled(),
							listDataSource.getAdvancedRulesEnabled());

					modelAndView.addObject("templateId", String.valueOf(idData));
					modelAndView.addObject("uniqueId", uniqueId);
					modelAndView.addObject("templateName", listDataSource.getName());
					modelAndView.addObject("message",
							"Template rerun is in progress, status will be notified to registered email !!");
				} else {
					System.out.println("\n====> rerunTemplate - Template is queued or already in progress !!");
					modelAndView.setViewName("Data Template Created Successfully");
					modelAndView.addObject("message", "Template is queued or already in progress !!");
				}
			} else {
				System.out.println("\n====> rerunTemplate - Failed to get Template details !!");
				modelAndView.setViewName("Data Template Created Successfully");
				modelAndView.addObject("message", "Failed to reRun Template !!");
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

}
