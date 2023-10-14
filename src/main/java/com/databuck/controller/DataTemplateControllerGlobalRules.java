package com.databuck.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.Domain;
import com.databuck.bean.ImportExportGlobalRuleDTO;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.RuleToSynonym;
import com.databuck.bean.ruleFields;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.GlobalRuleService;
import com.databuck.service.IDataAlgorithService;
import com.databuck.service.RBACController;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Controller
public class DataTemplateControllerGlobalRules {

	@Autowired
	private GlobalRuleDAO globalRuleDAO;

	@Autowired
	IDataAlgorithService dataAlgorithService;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	SchemaDAOI schemaDao;

	@Autowired
	IListDataSourceDAO oListDataSourceDAO;

	@Autowired
	private GlobalRuleService globalRuleService;

	@RequestMapping(value = "/importExportGlobalRules")
	public ModelAndView getRuleForDomain(HttpServletRequest request, ModelAndView model, HttpServletResponse response,
			HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Data Template", "C", session);
		if (rbac) {
			List<Domain> domainList = globalRuleDAO.getDomainList();
			model.setViewName("importExportGlobalRules");
			model.addObject("currentSection", "Global Rule");
			model.addObject("currentLink", "importExportGlobalRules");
			model.addObject("domainList", domainList);
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getGlobalRulesImportSampleFile")
	public void getGlobalRulesImportSampleFile(HttpServletResponse response, HttpSession session) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "R", session);
			if (rbac) {
				// Prepare file for download
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", "GlobalRules_sample.csv");

				OutputStream outStream = response.getOutputStream();
				response.setHeader(headerKey, headerValue);

				String headerLine = "ruleName,columnName,ruleExpression,ruleThreshold";
				String dataLine = "Invalid_Gender,gender,gender not like '^(?:m|M|male|Male|f|F|female|Female)$',1.0";
				outStream.write(headerLine.toString().getBytes());
				outStream.write("\n".getBytes());
				outStream.write(dataLine.toString().getBytes());
				outStream.write("\n".getBytes());
				outStream.flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@RequestMapping(value = "/exportGlobalRulesToCSV")
	public void exportGlobalRulesToCSV(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam int exportDomainId, @RequestParam String exportDomainName) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {
				Long projectId = (Long) session.getAttribute("projectId");
				String projectName = (String) session.getAttribute("projectName");

				// Get the list of global rules associated with the domain and project
				List<ImportExportGlobalRuleDTO> rulesList = globalRuleDAO
						.getGlobalRulesOfDomainForExport(exportDomainId, projectId);

				// Prepare file for download
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"",
						"GlobalRules_" + exportDomainName + "_" + projectName + ".csv");

				OutputStream outStream = response.getOutputStream();
				response.setHeader(headerKey, headerValue);

				String headerLine = "ruleName,columnName,ruleExpression,ruleThreshold";
				outStream.write(headerLine.toString().getBytes());
				outStream.write("\n".getBytes());

				if (rulesList != null) {
					for (ImportExportGlobalRuleDTO rule : rulesList) {
						outStream.write(rule.toString().getBytes());
						outStream.write("\n".getBytes());
					}
				}
				outStream.flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@RequestMapping(value = "/importGlobalRulesFromCSV")
	public ModelAndView importGlobalRulesFromCSV(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, ModelAndView model, @RequestParam("dataupload") MultipartFile file) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage");
			}
			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {
				String domainId = request.getParameter("domainId");
				System.out.println("domainId: " + domainId);

				Long projectId = (Long) session.getAttribute("projectId");
				long idUser = (Long) session.getAttribute("idUser");

				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				String createdByUser = "";
				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
					createdByUser = (String) session.getAttribute("createdByUser");
				} else {
					// Get createdBy username from createdBy userId
					createdByUser = userDAO.getUserNameByUserId(idUser);
				}

				System.out.println("======= createdByUser ===>" + createdByUser);

				String resMessage = importGlobalRules(Integer.parseInt(domainId), file, projectId, createdByUser);

				model.setViewName("message");
				model.addObject("currentSection", "Global Rule");
				model.addObject("currentLink", "importExportGlobalRules");
				model.addObject("msg", resMessage);
				return model;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("loginPage");
	}

	private String importGlobalRules(int domainId, MultipartFile file, Long projectId, String createdByUser) {
		String response = "Import Global Rules from CSV is failed !!";
		try {
			System.out.println("\n=======> Importing Global Rules from CSV - START <=======");

			InputStream inputStream = file.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			CSVReader csvReader = new CSVReaderBuilder(br).withSkipLines(1).build();
			List<String[]> allData = csvReader.readAll();

			List<ImportExportGlobalRuleDTO> rulesList = new ArrayList<ImportExportGlobalRuleDTO>();
			if (allData != null) {
				for (String[] data : allData) {
					ImportExportGlobalRuleDTO rule = new ImportExportGlobalRuleDTO();
					rule.setRuleName(data[0]);
					rule.setColumnName(data[1]);
					rule.setRuleExpression(data[2]);
					rule.setRuleThreshold(Double.parseDouble(data[3]));
					rulesList.add(rule);
				}
			}

			// Add all the Global rules
			if (rulesList != null && rulesList.size() > 0) {
				long totalRuleCount = rulesList.size();
				long duplicateRuleCount = 0l;

				for (ImportExportGlobalRuleDTO rule : rulesList) {

					String columnName = rule.getColumnName();

					ruleFields rc = new ruleFields();
					rc.setDomain_id(domainId);
					rc.setUsercolumns(columnName);
					rc.setPossiblenames(columnName);

					// Check if synonym is present else add
					int syn_id = globalRuleDAO.getsynosymId(rc);

					if (syn_id <= 0) {
						syn_id = globalRuleDAO.insertintoSynonymsLibrary(rc);
					}

					// Insert rule
					ListColGlobalRules lcr = new ListColGlobalRules();
					lcr.setRuleName(rule.getRuleName());
					lcr.setRuleType("referential");
					lcr.setDescription("");
					lcr.setIdRightData(-1);
					lcr.setExpression(rule.getRuleExpression());
					lcr.setRuleThreshold(rule.getRuleThreshold());
					lcr.setDimensionId(1l);
					lcr.setCreatedByUser(createdByUser);
					lcr.setProjectId(projectId);
					lcr.setDomain_id(domainId);

					// Check if the rule already exists
					int duplicateCount = globalRuleDAO.checkIfDuplicateGlobalRule(lcr);
					if (duplicateCount <= 0) {
						Long globalRuleId = globalRuleDAO.insertintolistColRules(lcr);

						RuleToSynonym rulesyn = new RuleToSynonym();
						rulesyn.setGlobalRuleId(globalRuleId.intValue());
						rulesyn.setSynonymId(syn_id);

						// Insert ruleToSynonym mapping
						globalRuleDAO.insertRuleSynonymMapping(rulesyn);
					} else {
						System.out.println("Rule: [" + lcr.getRuleName()
								+ "] is duplicate, a rule with same expression already exists");
						++duplicateRuleCount;
					}
				}

				if (duplicateRuleCount == totalRuleCount) {
					response = "Import Global Rules is failed, all the rule expressions already exists in domain-project !!";
				} else {
					response = "Import Global Rules from CSV is successful !!";
				}
			} else {
				response = "No rules found to import !!";
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while importing the rules !!");
			e.printStackTrace();
		}
		System.out.println("\n=======> Importing Global Rules from CSV - END   <=======");

		return response;
	}

	@RequestMapping(value = "/loadGlobalRuleDiscoveryForValidation", method = RequestMethod.GET)
	public ModelAndView loadGlobalRuleDiscoveryForValidation(HttpServletRequest request, HttpSession session,
			@RequestParam long idApp, @RequestParam String validationName, @RequestParam String pageSource) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if (user != null && user.equals("validUser")) {
				boolean rbac = RBACController.rbac("Validation Check", "C", session);

				if (rbac) {

					// Get template Id
					long idData = validationcheckdao.getIdDataTFromListApplication(idApp);

					// Get template column names list
					List<String> columnNamesList = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

					ModelAndView modelandview = new ModelAndView("globalRuleDiscovery");
					modelandview.addObject("idApp", idApp);
					modelandview.addObject("appName", validationName);
					modelandview.addObject("idData", idData);
					modelandview.addObject("templateColumnList", columnNamesList);

					if (pageSource != null && pageSource.trim().equalsIgnoreCase("results"))
						modelandview.addObject("fromMapping", "dashboard_View");
					else
						modelandview.addObject("fromMapping", "validationView");
					modelandview.addObject("currentSection", "Validation Check");
					modelandview.addObject("currentLink", "VCView");
					return modelandview;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/loadGlobalRuleDiscoveryForTemplate", method = RequestMethod.GET)
	public ModelAndView loadGlobalRuleDiscoveryForTemplate(HttpServletRequest request, HttpSession session,
			@RequestParam long idData, @RequestParam String templateName) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if (user != null && user.equals("validUser")) {
				boolean rbac = RBACController.rbac("Data Template", "C", session);

				if (rbac) {
					// Get template column names list
					List<String> columnNamesList = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

					ModelAndView modelandview = new ModelAndView("globalRuleDiscovery");
					modelandview.addObject("idData", idData);
					modelandview.addObject("templateColumnList", columnNamesList);
					modelandview.addObject("appName", templateName);
					modelandview.addObject("fromMapping", "datatemplateview");
					modelandview.addObject("currentSection", "Data Template");
					modelandview.addObject("currentLink", "DTView");
					return modelandview;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getEligibleGlobalRulesForTemplate", method = RequestMethod.POST)
	public void getEligibleGlobalRulesForTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {

		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {

				// Get eligible global rules list
				JSONObject eligibleGlobalRulesJson = globalRuleService.getEligibleGlobalRulesForTemplate(idData);

				// Get already linked global rules list
				String linkedGlobalRules = oListDataSourceDAO.getAlreadyLinkedGlobalRulesToDataTemplate(idData);

				JSONObject oJsonResponse = new JSONObject();

				oJsonResponse.put("Rules", eligibleGlobalRulesJson.get("DataSet"));
				oJsonResponse.put("AlreadyLinkedGlobalRulesToDataTemplate", linkedGlobalRules);
				response.getWriter().println(oJsonResponse);
				response.getWriter().flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getNonEligibleGlobalRulesForTemplate", method = RequestMethod.POST)
	public void getEligibleOtherGlobalRulesForTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {

		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {

				// Get eligible global rules list
				JSONObject eligibleGlobalRulesJson = globalRuleService.getNonEligibleGlobalRulesForTemplate(idData);

				JSONObject oJsonResponse = new JSONObject();
				oJsonResponse.put("Rules", eligibleGlobalRulesJson.get("DataSet"));
				response.getWriter().println(oJsonResponse);
				response.getWriter().flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getEligibleReferenceRulesForTemplate", method = RequestMethod.POST)
	public void getEligibleReferenceRulesForTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {

		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {

				// Get eligible global rules list
				JSONObject eligibleGlobalRulesJson = globalRuleService.getEligibleReferenceRulesForTemplate(idData);

				JSONObject oJsonResponse = new JSONObject();
				oJsonResponse.put("ReferenceRules", eligibleGlobalRulesJson.get("DataSet"));
				response.getWriter().println(oJsonResponse);
				response.getWriter().flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getEligibleGlobalThresholdsForTemplate", method = RequestMethod.POST)
	public void getEligibleGlobalThresholdsForTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {

		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "C", session);
			if (rbac) {

				// Get eligible global rules list
				JSONObject eligibleThresholdsJson = globalRuleService.getEligibleGlobalThresholdsForTemplate(idData);

				JSONObject oJsonResponse = new JSONObject();
				oJsonResponse.put("Thresholds", eligibleThresholdsJson.get("DataSet"));
				response.getWriter().println(oJsonResponse);
				response.getWriter().flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/linkSelectedGlobalRulesToTemplate", method = RequestMethod.POST)
	public void linkSelectedGlobalRulesToTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData, @RequestParam String selectedGlobalRules) {

		System.out.println("****** linkSelectedGlobalRulesToTemplate - START *******");

		JSONObject resJson = new JSONObject();

		try {
			System.out.println("\n====>idData: " + idData);
			System.out.println("\n====>selectedGlobalRules: " + selectedGlobalRules);

			JSONObject selectedRulesJson = new JSONObject(selectedGlobalRules);
			JSONArray selectedRules = selectedRulesJson.getJSONArray("Rules");
			JSONArray delinkedRules = selectedRulesJson.getJSONArray("DelinkedRules");

			// Link selected Global rules and Delink unchecked global rules
			resJson = globalRuleService.linkSelectedGlobalRulesToTemplate(selectedRules, delinkedRules, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalRules to Template");
			e.printStackTrace();
		}

		try {
			response.getWriter().println(resJson);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n****** linkSelectedGlobalRulesToTemplate - END *******");
	}

	@RequestMapping(value = "/linkSelectedOtherGlobalRulesToTemplate", method = RequestMethod.POST)
	public void linkSelectedOtherGlobalRulesToTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData, @RequestParam String selectedGlobalRules) {

		System.out.println("****** linkSelectedOtherGlobalRulesToTemplate - START *******");

		JSONObject resJson = new JSONObject();

		try {
			System.out.println("\n====>idData: " + idData);
			System.out.println("\n====>selectedGlobalRules: " + selectedGlobalRules);

			JSONObject selectedRulesJson = new JSONObject(selectedGlobalRules);
			JSONArray selectedRules = selectedRulesJson.getJSONArray("Rules");

			// Link selected Global rules and Delink unchecked global rules
			resJson = globalRuleService.linkSelectedOtherGlobalRulesToTemplate(selectedRules, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalRules to Template");
			e.printStackTrace();
		}

		try {
			response.getWriter().println(resJson);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n****** linkSelectedOtherGlobalRulesToTemplate - END *******");
	}

	@RequestMapping(value = "/linkSelectedReferenceRulesToTemplate", method = RequestMethod.POST)
	public void linkSelectedReferenceRulesToTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData, @RequestParam String selectedReferenceRules) {

		System.out.println("****** linkSelectedReferenceRulesToTemplate - START *******");

		JSONObject resJson = new JSONObject();

		try {
			System.out.println("\n====>idData: " + idData);
			System.out.println("\n====>selectedRefereneRules: " + selectedReferenceRules);

			JSONObject selectedReferenceRulesJson = new JSONObject(selectedReferenceRules);
			JSONArray refRules = selectedReferenceRulesJson.getJSONArray("ReferenceRules");

			resJson = globalRuleService.linkSelectedReferenceRulesToTemplate(refRules, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking ReferenceRules to Template");
			e.printStackTrace();
		}

		try {
			response.getWriter().println(resJson);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n****** linkSelectedReferenceRulesToTemplate - END *******");
	}

	@RequestMapping(value = "/linkSelectedGlobalThresholdsToTemplate", method = RequestMethod.POST)
	public void linkSelectedGlobalThresholdsToTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData, @RequestParam String selectedThresholds) {

		System.out.println("\n****** linkSelectedGlobalThresholdsToTemplate - START *******");

		JSONObject resJson = new JSONObject();

		try {
			System.out.println("\n====>idData: " + idData);
			System.out.println("\n====>selectedRules: " + selectedThresholds);

			JSONObject selectedThresholdsJson = new JSONObject(selectedThresholds);
			JSONArray aThresholds = selectedThresholdsJson.getJSONArray("Thresholds");

			resJson = globalRuleService.linkGlobalThresholdToDataTemplate(aThresholds, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalThresholds to Template");
			e.printStackTrace();
		}

		try {
			response.getWriter().println(resJson);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n****** linkSelectedGlobalThresholdsToTemplate - END *******");

	}

	@RequestMapping(value = "/saveSynonymMappings", method = RequestMethod.POST)
	public void saveSynonymMappings(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam long idData, @RequestParam String synonymMappings) {

		System.out.println("\n****** saveSynonymMappings - START *******");

		JSONObject resJson = new JSONObject();

		try {
			System.out.println("\n====>idData: " + idData);
			System.out.println("\n====>synonymMappings: " + synonymMappings);

			JSONObject synonymMappingsJson = new JSONObject(synonymMappings);

			resJson = globalRuleService.saveSynonymMappings(idData, synonymMappingsJson);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while saving synonym mappings");
			e.printStackTrace();
		}

		try {
			response.getWriter().println(resJson);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n****** saveSynonymMappings - END *******");

	}

}
