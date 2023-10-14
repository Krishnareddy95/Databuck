package com.databuck.controller;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.TaskTypes;
import com.databuck.integration.IntegrationMasterService;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.multipart.*;

import com.databuck.bean.DataDomain;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.listModelGovernance;
import com.databuck.bean.listStatisticalMatchingConfig;
import com.databuck.bean.ListDmCriteria;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.RBACController;
import com.databuck.service.RuleCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.service.IProjectService;
import com.databuck.service.LoginService;

@Controller
public class ValidationCheckController {

	private final String CURRENT_MODULE_NAME = "Validation Check";
	
	@Autowired
	public LoginService loginService;
	
	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	private IProjectService IProjectservice;

	@Autowired
	IValidationCheckDAO validationcheckdao;
	
	@Autowired
	public IProjectDAO iProjectDAO;
	
	@Autowired
	private RBACController rbacController;
	
	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;
	
	@Autowired
	private SchemaDAOI schemaDao;
	
	@Autowired
	GlobalRuleDAO globalRuleDAO;

	@Autowired
	private IntegrationMasterService integrationMasterService;

	@RequestMapping(value = "/saveDataIntoListDMCriteria", method = RequestMethod.POST)
	public void saveDataIntoListDMCriteria(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam Long idApp, @RequestParam Long idData, @RequestParam Long rightSourceId,
			@RequestParam String expression, @RequestParam String matchingRuleAutomatic,
			@RequestParam Long leftSourceId, @RequestParam String matchType, @RequestParam double absoluteThresholdId,
			@RequestParam double unMatchedAnomalyThreshold,
			@RequestParam String groupbyid, @RequestParam String measurementid, @RequestParam String dateFormat,
			@RequestParam String incrementalMatching, @RequestParam String rightSliceEnd,
			@RequestParam String recordCount, @RequestParam String primaryKey) {
		expression = expression.replaceAll("\\s", "").trim();
		System.out.println("expression=" + expression);
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		boolean flag = true;
		List<String> leftMatchValueCols = validationcheckdao.getMatchValueColumns(leftSourceId);
		List<String> rightMatchValueCols = validationcheckdao.getMatchValueColumns(rightSourceId);

		List<String> leftTemplatecolumns = validationcheckdao.getDisplayName(leftSourceId);
		List<String> rightTemplatecolumns = validationcheckdao.getDisplayName(rightSourceId);

		List<String> leftMatchingRulesColumn = new ArrayList<String>();
		List<String> rightMatchingRulesColumn = new ArrayList<String>();
		String[] matchingColumnRules = expression.split("&&");
		if(!matchingRuleAutomatic.equalsIgnoreCase("Y")) {
		for(int i=0;i < matchingColumnRules.length;i++) {
			String[] displayName = matchingColumnRules[i].split("=");
			leftMatchingRulesColumn.add(displayName[0]);
			rightMatchingRulesColumn.add(displayName[1]);

		}
		}

		if (measurementid.equalsIgnoreCase("Y")) {
			boolean leftTemplateflag = false;
			if (leftMatchValueCols != null && leftMatchValueCols.size() >= 1) {
				leftTemplateflag = true;
			}
			System.out.println("leftTemplateflag=" + leftTemplateflag);

			if (!leftTemplateflag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("leftsource",
							"Please select atleast one match value column for your Validation Check in First Data Template.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (flag) {
				boolean rightTemplateflag = false;
				if (rightMatchValueCols != null && rightMatchValueCols.size() >= 1) {
					rightTemplateflag = true;
				}
				System.out.println("rightTemplateflag=" + rightTemplateflag);

				if (!rightTemplateflag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("rightsource",
								"Please select atleast one match value column for your Validation Check in Second Data Template.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (flag) {
				// Check if the size of the match values column is same on left and right
				if (leftMatchValueCols.size() != rightMatchValueCols.size()) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("rightsource", "Please select same number of match value column in both templates.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}


			if (flag) {
				if(leftMatchValueCols.size() > 1 && rightMatchValueCols.size() > 1){
					Collections.sort(leftMatchValueCols);
					Collections.sort(rightMatchValueCols);
					List<String> leftMatchValueCols_1 = leftMatchValueCols.stream().map(String::toUpperCase).collect(Collectors.toList());
					List<String> rightMatchValueCols_1 = rightMatchValueCols.stream().map(String::toUpperCase).collect(Collectors.toList());
					// Check if the match value columns are matching or not
					if (!leftMatchValueCols_1.equals(rightMatchValueCols_1)) {
						List<String> leftMatchValueCols_temp = new ArrayList<String>(leftMatchValueCols_1);
						leftMatchValueCols_temp.removeAll(rightMatchValueCols_1);
						flag = false;
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object.
							json.put("rightsource",
									"Please make sure match value columns names match in both templates. "
											+ Arrays.toString(leftMatchValueCols_temp.toArray())
											+ " columns are missing in second template.");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		if (flag) {
			// checking matching Rule columns are present
			if(leftMatchingRulesColumn.size() >= 1 && leftMatchingRulesColumn != null){
				List<String> leftTemplatecolumns_1 = new ArrayList<>(leftTemplatecolumns);
				List<String> missingColumsList = new ArrayList<String>();
				for(String lcol: leftMatchingRulesColumn) {
					if(!leftTemplatecolumns_1.contains(lcol)) {
						missingColumsList.add(lcol);
					}
				}
					
				if (missingColumsList != null && missingColumsList.size() > 0) {
					expression = null;
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("leftsource",
								"Please make sure match Rule columns names match in templates. "
										+ Arrays.toString(missingColumsList.toArray())
										+ " columns are missing in First template.");
						json.put("match",
								"Yes");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		if (flag) {
			// checking matching Rule columns are present
			if(rightMatchingRulesColumn.size() >= 1 && rightMatchingRulesColumn != null){
				List<String> rightTemplatecolumns_1 = new ArrayList<>(rightTemplatecolumns);

				List<String> missingColumsList = new ArrayList<String>();
				for(String rcol: rightMatchingRulesColumn) {
					if(!rightTemplatecolumns_1.contains(rcol)) {
						missingColumsList.add(rcol);
					}
				}

				if (missingColumsList != null && missingColumsList.size() > 0) {
					expression = null;
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("rightsource",
								"Please make sure match Rule columns names match in templates. "
										+ Arrays.toString(missingColumsList.toArray())
										+ " columns are missing in Second template.");
						json.put("match",
								"Yes");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		//////
		if (flag) {
			if (groupbyid.equalsIgnoreCase("Y")) {
				boolean leftTemplateflag = validationcheckdao
						.checkWhetherTheSameDgroupAreSelectedInDataTemplate(expression.trim(), leftSourceId);
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				System.out.println("leftTemplateflag=" + leftTemplateflag);
				if (!leftTemplateflag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("leftsource",
								"Subsegment(s) should be from the key matching column(s). Please configure again.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("incrementalMatching=" + incrementalMatching);
		if (flag) {
			if (incrementalMatching.equalsIgnoreCase("Y")) {
				boolean incrementalLeft = validationcheckdao.checkWhetherIncrementalForLeftTemplate(leftSourceId);
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				System.out.println("incrementalLeft=" + incrementalLeft);
				if (!incrementalLeft) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("leftsource",
								"Please select atleast one Last Read Time column for First Source Template.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (flag) {
			if (incrementalMatching.equalsIgnoreCase("Y")) {
				boolean incrementalLeft = validationcheckdao.checkWhetherIncrementalForLeftTemplate(rightSourceId);
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				System.out.println("incrementalLeft=" + incrementalLeft);
				if (!incrementalLeft) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("rightsource",
								"Please select atleast one Last Read Time column for Second Source Template.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		validationcheckdao.deleteEntryFromListDMRules(idApp, "Primary Key Matching");
		if (flag) {
			// primaryKeyMatching
			if (primaryKey.equalsIgnoreCase("Y")) {
				Long primaryKeyidDM = validationcheckdao.getIddmFromListDMRules(idApp, "Primary Key Matching");
				if (primaryKeyidDM == 0) {
					primaryKeyidDM = validationcheckdao.insertIntoListDMRules(idApp, "Primary Key Matching",
							"One to One");
				}
				System.out.println("primaryKeyidDM=" + primaryKeyidDM);
				Long primaryKeyMatchingUpdate = validationcheckdao
						.updateListDMRulesForPrimaryKeyMatching(primaryKeyidDM, idApp, rightSourceId, leftSourceId);
				if (primaryKeyMatchingUpdate <= 0) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("leftsource",
								"Please select atleast one Primary Key column for First Source Template.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		/*
		 * if(flag){ boolean rightTemplateflag =
		 * validationcheckdao.checkDgroupColumnForRightTemplate(rightSourceId);
		 * System.out.println("rightTemplateflag="+rightTemplateflag);
		 * if(!rightTemplateflag){ flag=false; try { JSONObject json = new
		 * JSONObject(); // put some value pairs into the JSON object .
		 * json.put("rightsource",
		 * "Please select one dgroup column for your Validation Check in Second Data Template."
		 * ); response.getWriter().println(json); } catch (Exception e) {
		 * e.printStackTrace(); } } }
		 */
		if (flag) {
			System.out.println("rightSliceEnd=" + rightSliceEnd);
			System.out.println("measurementid=" + measurementid);
			System.out.println("idApp=" + idApp);
			// System.out.println("idDM=" + idDM);
			System.out.println("rightSourceId=" + rightSourceId);
			System.out.println("leftSourceId=" + rightSourceId);
			System.out.println("expression=" + expression);
			System.out.println("matchingRuleAutomatic=" + matchingRuleAutomatic);
			validationcheckdao.deleteEntryFromListDMRules(idApp, "Measurements Match");
			Long idDM = validationcheckdao.getIddmFromListDMRules(idApp, "Key Fields Match");
			if (idDM == 0) {
				idDM = validationcheckdao.insertIntoListDMRules(idApp, matchType, "One to One");
				System.out.println("idDM=" + idDM);
			}

			if (measurementid.equalsIgnoreCase("Y")) {
				Long measurementIdDM = validationcheckdao.insertIntoListDMRules(idApp, "Measurements Match",
						"One to One");
				validationcheckdao.insertDataIntoListDMCriteriaForMeasurementMatch(measurementIdDM, leftMatchValueCols,
						rightMatchValueCols);
			}
			/*
			 * ListApplications listApplicationsData =
			 * validationcheckdao.getdatafromlistapplications(idApp);
			 * if(listApplicationsData.getOutOfNormCheck().equalsIgnoreCase("Y")
			 * ){ matchingRuleAutomatic="N"; }
			 */
			validationcheckdao.updateDataIntoListApplications(idApp, rightSourceId, absoluteThresholdId, groupbyid,
					measurementid, dateFormat, rightSliceEnd, expression, matchingRuleAutomatic, recordCount,
					primaryKey, unMatchedAnomalyThreshold);
			if (matchingRuleAutomatic.equalsIgnoreCase("Y")) {
				// validationcheckdao.deleteEntryFromListDMRules(idApp,"Key
				// Fields Match");
				// Long idDM1 = validationcheckdao.insertIntoListDMRules(idApp,
				// "Key Fields Match","One to One");
				List<String> rightSourceColumnNames = validationcheckdao
						.getDisplayNamesFromListDataDefinition(rightSourceId);
				List<String> LeftSourceColumnNames = validationcheckdao
						.getDisplayNamesFromListDataDefinition(leftSourceId);
				int updateMatchingRuleAutomatic = validationcheckdao.updateMatchingRuleAutomaticIntoListDMCriteria(
						rightSourceColumnNames, LeftSourceColumnNames, idDM, idApp);
				System.out.println("updateMatchingRuleAutomatic=" + updateMatchingRuleAutomatic);

				if (updateMatchingRuleAutomatic > 0) {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("success", "Match Key created successfully");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "No Matching key found");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (expression != "") {
				int insertDataIntoListDMCriteria = validationcheckdao.insertDataIntoListDMCriteria(expression.trim(),
						idDM, idApp);
				System.out.println("insertDataIntoListDMCriteria=" + insertDataIntoListDMCriteria);

				if (insertDataIntoListDMCriteria >= 1) {
					try {
						JSONObject json = new JSONObject();
						json.put("success", "Match Key created successfully");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (insertDataIntoListDMCriteria == -1) {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail1", "Matching key already exists");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "No Matching key found");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "No Matching key found");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		/*
		 * try { response.getWriter().println("success"); } catch (IOException
		 * e) { e.printStackTrace(); }
		 */

	}

	@RequestMapping(value = "/saveRollDataIntoListDMCriteria", method = RequestMethod.POST)
	public void saveRollDataIntoListDMCriteria(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam Long idApp, @RequestParam Long idData, @RequestParam Long rightSourceId,
			@RequestParam String expression, @RequestParam Long leftSourceId, @RequestParam String matchType,
			@RequestParam String recordCount, @RequestParam String primaryKey, @RequestParam long targetSchemaId,
			@RequestParam String rollType) {

		System.out.println("\n*********** Saving RollingData Rules to ListDMCriteria ***********");

		expression = expression.replaceAll("\\s", "").trim();
		System.out.println("expression=" + expression);

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		boolean flag = true;
		List<String> leftMatchValueCols = validationcheckdao.getMatchValueColumns(leftSourceId);
		List<String> rightMatchValueCols = validationcheckdao.getMatchValueColumns(rightSourceId);

		boolean leftTemplateflag = false;
		if (leftMatchValueCols != null && leftMatchValueCols.size() >= 1 && leftMatchValueCols.size() <= 25) {
			leftTemplateflag = true;
		}
		System.out.println("leftTemplateflag=" + leftTemplateflag);

		if (!leftTemplateflag) {
			flag = false;
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("leftsource",
						"Please select atleast one or max 25 match value column for your Validation Check in First Data Template.");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (flag) {
			boolean rightTemplateflag = false;
			if (rightMatchValueCols != null && rightMatchValueCols.size() >= 1 && rightMatchValueCols.size() <= 25) {
				rightTemplateflag = true;
			}
			System.out.println("rightTemplateflag=" + rightTemplateflag);

			if (!rightTemplateflag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object.
					json.put("rightsource",
							"Please select atleast one or max 25 match value column for your Validation Check in Second Data Template.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (flag) {
			// Check if the size of the match values column is same on left and right
			if (leftMatchValueCols.size() != rightMatchValueCols.size()) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object.
					json.put("rightsource", "Please select same number of match value column in both templates.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		if (flag) {
			Collections.sort(leftMatchValueCols);
			Collections.sort(rightMatchValueCols);
			List<String> leftMatchValueCols_1 = leftMatchValueCols.stream().map(String::toUpperCase).collect(Collectors.toList());
			List<String> rightMatchValueCols_1 = rightMatchValueCols.stream().map(String::toUpperCase).collect(Collectors.toList());
			// Check if the match value columns are matching or not
			if (!leftMatchValueCols_1.equals(rightMatchValueCols_1)) {
				List<String> leftMatchValueCols_temp = new ArrayList<String>(leftMatchValueCols_1);
				leftMatchValueCols_temp.removeAll(rightMatchValueCols_1);
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object.
					json.put("rightsource",
							"Please make sure match value columns names match in both templates. "
									+ Arrays.toString(leftMatchValueCols_temp.toArray())
									+ " columns are missing in second template.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (flag) {

			validationcheckdao.deleteEntryFromListDMRules(idApp, "Key Fields Match");
			validationcheckdao.deleteEntryFromListDMRules(idApp, "Measurements Match");
			validationcheckdao.deleteEntryFromListDMRules(idApp, "Primary Key Matching");

			Long measurementIdDM = validationcheckdao.insertIntoListDMRules(idApp, "Measurements Match", "One to One");
			validationcheckdao.insertDataIntoListDMCriteriaForRollMatch(measurementIdDM, leftMatchValueCols,
					rightMatchValueCols);

			validationcheckdao.updateDataIntoListApplications(idApp, rightSourceId, 0.0, "N", "Y", "",
					"N", expression, "N", recordCount, primaryKey, 0.0);

			System.out.println("targetSchemaId:"+targetSchemaId);
			validationcheckdao.updateRollTargetSchemaIdForIdApp(idApp, targetSchemaId);

			if( rollType== null || rollType.trim().isEmpty()) {
				rollType = "current_previous";
			}
			System.out.println("rollType:"+rollType);
			validationcheckdao.updateRollTypeForIdApp(idApp, rollType);

			// primaryKeyMatching
			if (primaryKey.equalsIgnoreCase("Y")) {
				List<String> leftPrimaryCols = validationcheckdao.getPrimaryColumns(leftSourceId);
				List<String> rightPrimaryCols = validationcheckdao.getPrimaryColumns(rightSourceId);

				if (leftPrimaryCols == null || leftPrimaryCols.size() == 0) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object.
						json.put("leftsource",
								"Please select atleast one Primary Key column for First Source Template.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (flag) {
					if (rightPrimaryCols == null || rightPrimaryCols.size() == 0) {
						flag = false;
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object.
							json.put("rightsource",
									"Please select atleast one Primary Key column for Second Source Template.");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Long primaryKeyidDM = validationcheckdao.getIddmFromListDMRules(idApp, "Primary Key Matching");
				if (primaryKeyidDM == 0) {
					primaryKeyidDM = validationcheckdao.insertIntoListDMRules(idApp, "Primary Key Matching",
							"One to One");
				}
				System.out.println("primaryKeyidDM=" + primaryKeyidDM);
				Long primaryKeyMatchingUpdate = validationcheckdao
						.updateListDMRulesForPrimaryKeyMatching(primaryKeyidDM, idApp, rightSourceId, leftSourceId);
				if (primaryKeyMatchingUpdate <= 0){
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "No Matching key found");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("success", "Match Key created successfully");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("idApp=" + idApp);
				System.out.println("expression=" + expression);

				Long idDM = validationcheckdao.getIddmFromListDMRules(idApp, "Key Fields Match");

				if (idDM == 0) {
					idDM = validationcheckdao.insertIntoListDMRules(idApp, matchType, "One to One");
					System.out.println("idDM=" + idDM);
				}

				if (expression != "") {
					int insertDataIntoListDMCriteria = validationcheckdao.insertDataIntoListDMCriteria(expression.trim(),
							idDM, idApp);
					System.out.println("insertDataIntoListDMCriteria=" + insertDataIntoListDMCriteria);

					if (insertDataIntoListDMCriteria >= 1) {
						try {
							JSONObject json = new JSONObject();
							json.put("success", "Match Key created successfully");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (insertDataIntoListDMCriteria == -1) {
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object .
							json.put("fail1", "Matching key already exists");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object .
							json.put("fail", "No Matching key found");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "No Matching key found");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		    }
		}
	}
	
	@RequestMapping(value = "/updateSchemaMatching", method = RequestMethod.POST)
	public ModelAndView updateSchemaMatching(HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {

			Long schemaid1 = Long.parseLong("64");
			Long schemaid2 = Long.parseLong("65");
			String prefix1 = request.getParameter("prefix1");
			String prefix2 = request.getParameter("prefix2");
			Long appId = Long.parseLong("547");
		
			String schematypename = request.getParameter("schematypename");

			if (schematypename.equalsIgnoreCase("metadata")) {
				System.out.println("Going to update listApplication for schema Matching");
				templateviewdao.updatelistApplicationsForSchemamatching(appId,
						schemaid1, schemaid2, schematypename, request.getParameter("dataset"), null, null, prefix1,
						prefix2);

				System.out.println("Done update listApplication for schema Matching");
			} else if (schematypename.equalsIgnoreCase("RC")) {

				System.out.println("Going to update listApplication for RC Matching");
				String threasholdType = request.getParameter("schema_thresholdtype");
				String rcThreshold = request.getParameter("schema_rc");

				System.out.println("threasholdType:" + threasholdType);
				System.out.println("rcThreshold:" + rcThreshold);
				templateviewdao.updatelistApplicationsForSchemamatching(Long.parseLong(request.getParameter("idApp")),
						schemaid1, schemaid2, schematypename, request.getParameter("dataset"), threasholdType,
						rcThreshold, prefix1, prefix2);
				System.out.println("Done update listApplication for RC Matching");
			}
			
			ModelAndView model = new ModelAndView("ValidationSuccess");
			model.addObject("currentSection", "Validation Check");
			model.addObject("currentLink", "Add New");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/updateDataIntolistStatisticalMatchingConfig", method = RequestMethod.POST)
	public void updateDataIntolistStatisticalMatchingConfig(
			@RequestBody listStatisticalMatchingConfig listStatisticalMatchingConfig, HttpServletResponse response,
			HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + listStatisticalMatchingConfig.getIdApp());
		System.out.println("rightSourceId=" + listStatisticalMatchingConfig.getRightSourceId());
		System.out.println("leftSourceId=" + listStatisticalMatchingConfig.getLeftSourceId());
		System.out.println("expression=" + listStatisticalMatchingConfig.getExpression());
		System.out.println("measurementSum=" + listStatisticalMatchingConfig.getMeasurementSum());
		System.out.println("getMeasurementSumThreshold=" + listStatisticalMatchingConfig.getMeasurementSumThreshold());
		System.out.println("getMeasurementSumType=" + listStatisticalMatchingConfig.getMeasurementSumType());
		validationcheckdao.updateDataIntoListApplication(listStatisticalMatchingConfig.getIdApp(),
				listStatisticalMatchingConfig.getRightSourceId());
		int update = validationcheckdao.updateintolistStatisticalMatchingConfig(listStatisticalMatchingConfig);
		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/saveDataIntolistStatisticalMatchingConfig", method = RequestMethod.POST)
	public void saveDataIntolistStatisticalMatchingConfig(
			@RequestBody listStatisticalMatchingConfig listStatisticalMatchingConfig, HttpServletResponse response,
			HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + listStatisticalMatchingConfig.getIdApp());
		System.out.println("rightSourceId=" + listStatisticalMatchingConfig.getRightSourceId());
		System.out.println("leftSourceId=" + listStatisticalMatchingConfig.getLeftSourceId());
		System.out.println("expression=" + listStatisticalMatchingConfig.getExpression());
		System.out.println("measurementSum=" + listStatisticalMatchingConfig.getMeasurementSum());
		System.out.println("getMeasurementSumThreshold=" + listStatisticalMatchingConfig.getMeasurementSumThreshold());
		System.out.println("getMeasurementSumType=" + listStatisticalMatchingConfig.getMeasurementSumType());

		boolean flag = true;
		List<ListDataDefinition> listdatadefinition = templateviewdao
				.view(listStatisticalMatchingConfig.getLeftSourceId());

		if (listStatisticalMatchingConfig.getGroupBy().equalsIgnoreCase("Y")) {
			if (flag) {
				boolean checkDataTemplateForDGroup = validationcheckdao.checkDataTemplateForDGroup(listdatadefinition);
				System.out.println("checkDataTemplateForDGroup=" + checkDataTemplateForDGroup);
				if (!checkDataTemplateForDGroup) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("firstSource", "Please select atleast one dgroup from first source template");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
			if (flag) {
				List<ListDataDefinition> secondlistdatadefinition = templateviewdao
						.view(listStatisticalMatchingConfig.getRightSourceId());
				boolean checkDataTemplateForDGroup = validationcheckdao
						.checkDataTemplateForDGroup(secondlistdatadefinition);
				System.out.println("checkDataTemplateForDGroup=" + checkDataTemplateForDGroup);
				if (!checkDataTemplateForDGroup) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("secondSource", "Please select atleast one dgroup from second source template");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		if (flag) {
			validationcheckdao.updateDataIntoListApplication(listStatisticalMatchingConfig.getIdApp(),
					listStatisticalMatchingConfig.getRightSourceId());
			int update = validationcheckdao.insertintolistStatisticalMatchingConfig(listStatisticalMatchingConfig);
			System.out.println("update=" + update);
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@RequestMapping(value = "/saveDataIntolistApplicationForDataMatchingGroup", method = RequestMethod.POST)
	public void saveDataIntolistApplicationForDataMatchingGroup(@RequestParam Long idApp, @RequestParam Long idData,
			@RequestParam Long secondSourceId, @RequestParam double absoluteThresholdId, HttpServletResponse response,
			HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("saveDataIntolistApplicationForDataMatchingGroup");
		System.out.println("idApp=" + idApp);
		System.out.println("idData=" + idData);
		System.out.println("secondSourceId=" + secondSourceId);
		System.out.println("absoluteThresholdId=" + absoluteThresholdId);
		boolean flag = true;
		List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);

		if (flag) {
			boolean checkDataTemplateForDGroup = validationcheckdao.checkDataTemplateForDGroup(listdatadefinition);
			System.out.println("checkDataTemplateForDGroup=" + checkDataTemplateForDGroup);
			if (!checkDataTemplateForDGroup) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("firstSource", "Please select atleast one dgroup from first source template");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			List<ListDataDefinition> secondlistdatadefinition = templateviewdao.view(secondSourceId);
			boolean checkDataTemplateForDGroup = validationcheckdao
					.checkDataTemplateForDGroup(secondlistdatadefinition);
			System.out.println("checkDataTemplateForDGroup=" + checkDataTemplateForDGroup);
			if (!checkDataTemplateForDGroup) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("secondSource", "Please select atleast one dgroup from second source template");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			int update = validationcheckdao.saveDataIntolistApplicationForDataMatchingGroup(idApp, secondSourceId,
					absoluteThresholdId);
			System.out.println("update=" + update);
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@RequestMapping(value = "/changeDataColumnAjax", method = RequestMethod.POST)
	public void changeDataColumnAjax(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("idData=" + idData);
		List<String> listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(listDataDefinitionColumnNames);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject displayName = new JSONObject();
			displayName.put("success", jsonInString);
			System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/matchingKeys")
	public ModelAndView matchingKeys(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {

			String matchCategory = request.getParameter("mcardinality");
			System.out.println("matchCategory=" + matchCategory);
			String matchtype = request.getParameter("matchtype");
			System.out.println("matchtype=" + matchtype);

			Long idApp = Long.parseLong(request.getParameter("idApp"));
			System.out.println("idApp=" + idApp);
			Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("idData=" + idData);
			String apptype = request.getParameter("apptype");
			System.out.println("apptype=" + apptype);
			String applicationName = request.getParameter("applicationName");
			System.out.println("applicationName=" + applicationName);
			String description = request.getParameter("description");
			System.out.println("description=" + description);
			String name = request.getParameter("name");
			System.out.println("name=" + name);
			Long idDM = validationcheckdao.insertIntoListDMRules(idApp, matchtype, matchCategory);
			System.out.println("idDM=" + idDM);

			Long projectId= (Long)session.getAttribute("projectId");
			List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);


			List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();

			List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

			List matchingRules = validationcheckdao.getMatchingRules(idApp);
			System.out.println("matchingRulesSize=" + matchingRules.size());

			ModelAndView model = new ModelAndView("matchKeyCreateView");
			if (matchingRules.size() >= 1) {
				model.addObject("matchingRulesTrue", true);
			}

			model.addObject("matchingRules", matchingRules);

			model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
			model.addObject("listRefFunctionsname", listRefFunctionsname);
			model.addObject("getlistdatasourcesname", getlistdatasourcesname);
			model.addObject("currentSection", "Validation Check");
			model.addObject("currentLink", "VCView");
			model.setViewName("matchKeyCreateView");
			model.addObject("idApp", idApp);
			model.addObject("idData", idData);
			model.addObject("apptype", apptype);
			model.addObject("idDM", idDM);
			model.addObject("applicationName", applicationName);
			model.addObject("description", description);
			model.addObject("name", name);

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/updateDataIntoListDfTranrule", method = RequestMethod.POST)
	public void updateDataIntoListDfTranrule(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam String numericalStats, @RequestParam String recordAnomaly,
			@RequestParam Long idApp, @RequestParam String nullCount, @RequestParam String stringStat) {
		Object user = session.getAttribute("user");
		System.out.println("user:updateDataIntoListDfTranrule" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("idApp=" + idApp);
		// System.out.println("thresholdAll="+thresholdAll);
		// System.out.println("thresholdIdentity="+thresholdIdentity);
		System.out.println("nullCount=" + nullCount);
		// int update =
		// validationcheckdao.updatedataintolistdftranrule(idApp,thresholdAll,thresholdIdentity);
		// System.out.println("update="+update);

		int updateintolistapplications = validationcheckdao.updateintolistapplications(idApp, numericalStats,
				recordAnomaly, nullCount, stringStat);
		System.out.println("updateintolistapplications=" + updateintolistapplications);

		if (1 > 0) {
			try {
				response.getWriter().println("Data Connection customized successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} /*
			 * else { try { response.getWriter().println("There is a problem");
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } }
			 */
	}

	@RequestMapping(value = "/createValidationCheckCustomize")
	public ModelAndView createValidationCheckCustomize(HttpServletRequest request, HttpSession session, String defaultValues) {
		ModelAndView modelAndView = new ModelAndView("ValidationSuccess");

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {
			try {
				Long idApp = Long.parseLong(request.getParameter("idApp"));
				System.out.println("idApp=" + idApp);
				Long idData = Long.parseLong(request.getParameter("idData"));
				System.out.println("idData=" + idData);

				String recordCount = request.getParameter("recordCount");

				System.out.println("recordCount=" + recordCount);

				/*
				 * String
				 * recordCountText=request.getParameter("recordCountText");
				 *
				 * Long record=0l;
				 * if(!(recordCountText==null||recordCountText.equals(""))){
				 * record=Long.parseLong(recordCountText); }
				 * System.out.println("record="+record);
				 */

				String nullCount = request.getParameter("nullCount");
				System.out.println("nullCount=" + nullCount);
				String nullCountText = request.getParameter("nullCountText");
				Double nullCountTextNull = 0.0;
				if (!(nullCountText == null || nullCountText.equals(""))) {
					nullCountTextNull = Double.parseDouble(nullCountText);
				}
				System.out.println("nullCountTextNull=" + nullCountTextNull);

				String duplicateCount = request.getParameter("duplicateCount");
				System.out.println("duplicateCount=" + duplicateCount);

				String duplicateCountText = request.getParameter("duplicateCountText");
				Double duplicateCountTextNull = 0.0;
				if (!(duplicateCountText == null || duplicateCountText.equals(""))) {
					duplicateCountTextNull = Double.parseDouble(duplicateCountText);
				}
				System.out.println("duplicateCountTextNull=" + duplicateCountTextNull);

				String duplicateCountAll = request.getParameter("duplicateCountAll");
				System.out.println("duplicateCountAll=" + duplicateCountAll);
				String duplicateCountAllText = request.getParameter("duplicateCountAllText");
				Double duplicateCountAllTextNull = 0.0;
				if (!(duplicateCountAllText == null || duplicateCountAllText.equals(""))) {
					duplicateCountAllTextNull = Double.parseDouble(duplicateCountAllText);
				}
				System.out.println("duplicateCountAllTextNull=" + duplicateCountAllTextNull);

				String incrementalCheck = request.getParameter("incrementalCheck");
				System.out.println("incrementalCheck=" + incrementalCheck);
				String incrementalCheckText = request.getParameter("incrementalCheckText");
				System.out.println("incrementalCheckText=" + incrementalCheckText);

				String numericalStats = request.getParameter("numericalStats");
				System.out.println("numericalStats=" + numericalStats);
				String numericalStatsText = request.getParameter("numericalStatsText");
				Double numericalStatsTextNull = 0.0;
				if (!(numericalStatsText == null || numericalStatsText.equals(""))) {
					numericalStatsTextNull = Double.parseDouble(numericalStatsText);
				}
				System.out.println("numericalStatsTextNull=" + numericalStatsTextNull);

				String stringStat = request.getParameter("stringStat");
				System.out.println("stringStat=" + stringStat);
				String stringStatText = request.getParameter("stringStatText");
				Double stringStatTextNull = 0.0;
				if (!(stringStatText == null || stringStatText.equals(""))) {
					stringStatTextNull = Double.parseDouble(stringStatText);
				}
				System.out.println("stringStatTextNull=" + stringStatTextNull);

				String columnOrderVal = request.getParameter("columnOrderVal");
				System.out.println("columnOrderVal=" + columnOrderVal);

				String fileNameVal = request.getParameter("fileNameVal");
				System.out.println("fileNameVal=" + fileNameVal);
				String numberofRows = request.getParameter("numberofRows");
				Double rows = 0.0;
				if (!(numberofRows == null || numberofRows.equals("")))
					rows = Double.parseDouble(numberofRows);
				System.out.println("numberofRows=" + numberofRows);

				//
				String recordAnomaly = request.getParameter("recordAnomalyid");
				System.out.println("recordAnomaly=" + recordAnomaly);

				String recordAnomalyThreshold = request.getParameter("recordAnomalyThresholdId");
				Double recordAnomalyThresholdnull = 3.0;
				if (!(recordAnomalyThreshold == null || recordAnomalyThreshold.equals("")))
					recordAnomalyThresholdnull = Double.parseDouble(recordAnomalyThreshold);
				System.out.println("recordAnomalyThresholdnull=" + recordAnomalyThresholdnull);

				String DFSetComparison = request.getParameter("DFSetComparisonId");
				Double DFSetComparisonnull = 0.0;
				if (!(DFSetComparison == null || DFSetComparison.equals("")))
					DFSetComparisonnull = Double.parseDouble(DFSetComparison);
				System.out.println("DFSetComparisonnull=" + DFSetComparisonnull);
				String timeSeries = "";
				String[] timeSeriesCheckbox = request.getParameterValues("check_id");

				String recordCountAnomalyType = request.getParameter("recordCountAnomalyType");
				System.out.println("recordCountAnomalyType=" + recordCountAnomalyType);

				String applyRules = request.getParameter("applyRules");
				System.out.println("applyRules=" + applyRules);

				String applyDerivedColumns = request.getParameter("applyDerivedColumns");
				System.out.println("applyDerivedColumns=" + applyDerivedColumns);

				String nameofEntityColumn = request.getParameter("nameofEntityColumn");
				System.out.println("nameofEntityColumn=" + nameofEntityColumn);
				String csvDirectory = request.getParameter("csvDirectory");
				System.out.println("csvDirectory=" + csvDirectory);

				String groupEquality = request.getParameter("groupEquality");
				System.out.println("groupEquality=" + groupEquality);

				String groupEqualityText = request.getParameter("groupEqualityText");
				Double groupEqualityThreshold = 0.0;
				if (!(groupEqualityText == null || groupEqualityText.equals("")))
					groupEqualityThreshold = Double.parseDouble(groupEqualityText);
				System.out.println("groupEqualityThreshold=" + groupEqualityThreshold);
				System.out.println("groupEqualityText=" + groupEqualityText);
				// System.out.println("timeSeriesCheckbox="+timeSeriesCheckbox);
				if (timeSeriesCheckbox != null) {
					for (int i = 0; i < timeSeriesCheckbox.length; i++) {
						timeSeries = timeSeries + timeSeriesCheckbox[i] + ",";
						/*
						 * if (timeSeriesCheckbox[i].equalsIgnoreCase("month"))
						 * { String monthdropdown =
						 * request.getParameter("monthdropdown");
						 * System.out.println("monthdropdown=" + monthdropdown);
						 * timeSeries = timeSeries + monthdropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("dayOfWeek"))
						 * { String dowdropdown =
						 * request.getParameter("dowdropdown");
						 * System.out.println("dowdropdown=" + dowdropdown);
						 * timeSeries = timeSeries + dowdropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("hourOfDay"))
						 * { String hoddropdown =
						 * request.getParameter("hoddropdown");
						 * System.out.println("hoddropdown=" + hoddropdown);
						 * timeSeries = timeSeries + hoddropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("dayOfMonth")
						 * ) { String domdropdown =
						 * request.getParameter("domdropdown");
						 * System.out.println("domdropdown=" + domdropdown);
						 * timeSeries = timeSeries + domdropdown; }
						 */
						System.out.println("timeSeries=" + timeSeriesCheckbox[i]);
						// timeSeries = timeSeries + "), ";
					}
					timeSeries = timeSeries.toString().substring(0, timeSeries.length() - 1);
				}
				/*
				 * if (timeSeries.contains("None")) { timeSeries =
				 * timeSeries.toString().substring(0, timeSeries.length() - 1);
				 * }
				 */
				System.out.println("timeSeries=" + timeSeries.trim());

				String dataDriftCheck = request.getParameter("dataDriftCheck");
				System.out.println("dataDriftCheck=" + dataDriftCheck);

				String dataDriftCheckText = request.getParameter("dataDriftCheckText");
				// System.out.println("dataDriftCheckText="+dataDriftCheckText);
				Double dataDriftCheckTextnull = 0.0;
				if (!(dataDriftCheckText == null || dataDriftCheckText.equals("")))
					dataDriftCheckTextnull = Double.parseDouble(dataDriftCheckText);
				System.out.println("dataDriftCheckTextnull=" + dataDriftCheckTextnull);

				String updateFrequency = request.getParameter("frequencyid");
				System.out.println("updateFrequency=" + updateFrequency);
				String frequencyDays = request.getParameter("EveryDay");
				Double frequencyDaysnull = 0.0;
				if (!(frequencyDays == null || frequencyDays.equals("")))
					frequencyDaysnull = Double.parseDouble(frequencyDays);
				System.out.println("frequencyDaysnull=" + frequencyDaysnull);

				String outofNorm = request.getParameter("outofNorm");
				System.out.println("outofNorm=" + outofNorm);
				String outofNormThreshold = request.getParameter("outofNormThreshold");
				Double outofNormThresholdnull = 0.0;
				if (!(outofNormThreshold == null || outofNormThreshold.equals("")))
					outofNormThresholdnull = Double.parseDouble(outofNormThreshold);
				System.out.println("outofNormThresholdnull=" + outofNormThresholdnull);

				String incrementalMatching = request.getParameter("incrementalTypeId");
				System.out.println("incrementalMatching=" + incrementalMatching);
				String buildHistoricFingerPrint = request.getParameter("buildHistoricId");
				System.out.println("buildHistoricFingerPrint=" + buildHistoricFingerPrint);
				String historicStartDate = null;
				String historicEndDate = null;
				String historicDateFormat = null;
				if (buildHistoricFingerPrint == null)
					buildHistoricFingerPrint = "N";
				if (buildHistoricFingerPrint.equalsIgnoreCase("Y")) {
					historicStartDate = request.getParameter("startdateid");
					System.out.println("historicStartDate=" + historicStartDate);
					historicEndDate = request.getParameter("enddateid");
					System.out.println("historicEndDate=" + historicEndDate);
					historicDateFormat = request.getParameter("dateformatid");
					System.out.println("historicDateFormat=" + historicDateFormat);
				}
				ListApplications listApplication = new ListApplications();
				listApplication.setIncrementalMatching(incrementalMatching);
				listApplication.setBuildHistoricFingerPrint(buildHistoricFingerPrint);
				listApplication.setHistoricStartDate(historicStartDate);
				listApplication.setHistoricEndDate(historicEndDate);
				listApplication.setHistoricDateFormat(historicDateFormat);

				// validationcheckdao.insertintolistdfsetruleandtranrule(idApp,
				// DFSetComparisonnull);
				String dGroupNullCheck = request.getParameter("dGroupNullCheck");
				String dGroupDateRuleCheck = request.getParameter("dGroupDateRuleCheck");

				int updateintolistapplication = validationcheckdao.updateintolistapplication(outofNorm, columnOrderVal,
						fileNameVal, rows, nameofEntityColumn, idApp, nullCount, recordAnomaly, numericalStats,
						stringStat, dataDriftCheck, updateFrequency, frequencyDaysnull, recordCount,
						DFSetComparisonnull, timeSeries, recordCountAnomalyType, applyRules, applyDerivedColumns,
						csvDirectory, groupEquality, groupEqualityThreshold, dGroupNullCheck, dGroupDateRuleCheck, listApplication);
				System.out.println("updateintolistapplication=" + updateintolistapplication);

				int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(idData,
						recordCount, nullCountTextNull, incrementalCheck, numericalStats, numericalStatsTextNull,
						stringStat, stringStatTextNull, recordAnomaly, recordAnomalyThresholdnull,
						dataDriftCheckTextnull, outofNormThresholdnull);
				System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

				int insertintolistdftranrule = validationcheckdao.insertintolistdftranrule(idApp, duplicateCount,
						duplicateCountTextNull, duplicateCountAll, duplicateCountAllTextNull);
				System.out.println("insertintolistdftranrule=" + insertintolistdftranrule);

				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "Add New");
				/*
				 * List<String> warningmsgs =
				 * validationcheckdao.validationCheckprerequisite(idApp, idData,
				 * duplicateCountAll
				 * ,nullCount,duplicateCount,recordAnomaly,stringStat,
				 * numericalStats ); if(warningmsgs.size()!=0) {
				 * modelAndView.setViewName("showWarnings");
				 * modelAndView.addObject("warningmsgs", warningmsgs); }
				 */

			} catch (Exception e) {
				e.printStackTrace();
				return modelAndView;
			}

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	private static void doFeatureFlagsSanitizaton(ListApplications oListApplication) {
        BeanInfo oBeanInfo = null;
        PropertyDescriptor aPropertyDescriptor[] = null;
        Method oSetterMethod = null;
    	String sPropName, sPropValue;
    	String sColumnNames = "nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,lengthCheck,maxLengthCheck,correlationcheck,timelinessKeyCheck,defaultCheck,patternCheck,dGroupDateRuleCheck,badData,dGroupDataDriftCheck,continuousFileMonitoring";

    	DateUtility.DebugLog("doFeatureFlagsSanitizaton 01", "Begin");

		try {
			oBeanInfo = Introspector.getBeanInfo(oListApplication.getClass());
			aPropertyDescriptor = oBeanInfo.getPropertyDescriptors();

	        for(PropertyDescriptor oPropertyDescriptor : aPropertyDescriptor) {
	        	sPropName = oPropertyDescriptor.getName();
	        	if (sColumnNames.indexOf(sPropName) > -1) {
		        	sPropValue = (String)oPropertyDescriptor.getReadMethod().invoke(oListApplication);
		        	oSetterMethod = oPropertyDescriptor.getWriteMethod();

		        	if ( (sPropValue == null) || (sPropValue.isEmpty()) ) {
		        		oSetterMethod.invoke(oListApplication, "N");
		        		DateUtility.DebugLog("doFeatureFlagsSanitizaton 02", String.format("Replacement done for Property Name = %1$s, Original Value = %2$s, Replaced to = %3$s", sPropName, sPropValue, "N"));
	        		} else {
	        			DateUtility.DebugLog("doFeatureFlagsSanitizaton 02", String.format("No replacement done for Property Name = %1$s, as Original Value = %2$s", sPropName, sPropValue));
	        		}
	        	}
	        }

		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/updateValidationCheckAjax", method = RequestMethod.POST)
	public void ajax(@RequestBody ListApplications listApplications, HttpServletResponse response,
			HttpSession session) {

		boolean flag = true;
		Object user = session.getAttribute("user");
		System.out.println("data:" + listApplications);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
			Pradeep 8-Mar-2020 Global threshold changes: Debug log only, no code change to this method at all
			just View log for new field 'thresholdsApplyOption' reach from UI to 'listApplications' variable or not.
			so this object carry new field via updateintolistapplicationForAjaxRequest() parameter to dao save code
		*/
		try {
			ObjectMapper oMapper = new ObjectMapper();
			String sListApplication = oMapper.writeValueAsString(listApplications);
			DateUtility.DebugLog("updateValidationCheckAjax 01",
						String.format("thresholdsApplyOption = %1$s \n full submitted data = %2$s",
							listApplications.getThresholdsApplyOption(), sListApplication));
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
		doFeatureFlagsSanitizaton(listApplications);

		int dataDomainId = validationcheckdao.getCurrentDataDomainForIdApp(listApplications.getIdApp());
		System.out.println("@@@@@@@@2 getCurrentDataDomainForIdApp =>"+dataDomainId);
		listApplications.setData_domain(dataDomainId);
		
		if (flag) {
			boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
					listApplications);
			System.out.println("applyRulesFlag=" + applyRulesFlag);
			if (!applyRulesFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("rules", "The rules are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean applyDerivedColumnsFlag = validationcheckdao
					.checkTheConfigurationForapplyDerivedColumns(listdatadefinition, listApplications);
			System.out.println("applyDerivedColumnsFlag=" + applyDerivedColumnsFlag);
			if (!applyDerivedColumnsFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("derivedcols", "The Derived Columns are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			if(listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")){
				if(listApplications.getHistoricStartDate()==null || listApplications.getHistoricStartDate().trim().isEmpty()
						|| listApplications.getHistoricEndDate() == null || listApplications.getHistoricEndDate().isEmpty()){
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "Start Date or End Date is missing");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (flag) {
			boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForBuildHistoric(listdatadefinition,
					listApplications);
			System.out.println("buildHistoricFlag=" + buildHistoricFlag);
			if (!buildHistoricFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The last read time check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {

			boolean identityFlag = validationcheckdao.checkTheConfigurationForDupRowIdentity(listdatadefinition,
					listApplications);
			System.out.println("identityFlag=" + identityFlag);
			if (!identityFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate identity check (primary key) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean AllFlag = validationcheckdao.checkTheConfigurationForDupRowAll(listdatadefinition,
					listApplications);
			System.out.println("AllFlag=" + AllFlag);
			if (!AllFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate all check (duplicate key) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean dataDriftFlag = validationcheckdao.checkTheConfigurationForDataDrift(listdatadefinition,
					listApplications);
			System.out.println("dataDriftFlag=" + dataDriftFlag);
			if (!dataDriftFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The data drift check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean numFieldFlag = validationcheckdao.checkTheConfigurationForNumField(listdatadefinition,
					listApplications);
			System.out.println("numFieldFlag=" + numFieldFlag);
			if (!numFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The numerical fingerprint check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean stringFieldFlag = validationcheckdao.checkTheConfigurationForstringField(listdatadefinition,
					listApplications);
			System.out.println("stringFieldFlag=" + stringFieldFlag);
			if (!stringFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The string fingerprint check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			// System.out.println("recordcountanomaly="+listApplications.getRecordCountAnomaly());
			// System.out.println("getkeyBasedRecordCountAnomaly="+listApplications.getkeyBasedRecordCountAnomaly());
			boolean dGroupFlag = validationcheckdao.checkTheConfigurationFordGroup(listdatadefinition,
					listApplications);
			System.out.println("dGroupFlag=" + dGroupFlag);
			if (!dGroupFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The subsegment check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean recordAnomalyFlag = validationcheckdao.checkTheConfigurationForRecordAnomaly(listdatadefinition,
					listApplications);
			System.out.println("recordAnomalyFlag=" + recordAnomalyFlag);
			if (!recordAnomalyFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The record anomaly check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean nonNullFlag = validationcheckdao.checkTheConfigurationForNonNullField(listdatadefinition,
					listApplications);
			System.out.println("nonNullFlag=" + nonNullFlag);
			if (!nonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Null check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		// incremental check in VC source should not have incremental select in
		// DT pre check
		if (flag) {
			if (listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
				Long idData = listApplications.getIdData();
				boolean incrementalCheck = validationcheckdao.checkForIncrementalInListDataAccess(idData);
				if (!incrementalCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("incremental",
								"Cannot use data slices in source with Historic and Incremental Type of Application");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		//
		if (flag) {
			if (listApplications.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
			boolean timelinessKeyFlag = validationcheckdao.checkTheConfigurationForTimelinessKeyField(listdatadefinition,
					listApplications);
				System.out.println("timelinessKeyFlag=" + timelinessKeyFlag);
				if (!timelinessKeyFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The TimelinessKey check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.checkTheConfigurationDefaultCheck(listdatadefinition,
					listApplications);
			System.out.println("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean patternCheckFlag = validationcheckdao.checkTheConfigurationPatternCheck(listdatadefinition,
					listApplications);
			System.out.println("patternCheckFlag=" + patternCheckFlag);
			if (!patternCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Pattern check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		
		if (flag) {
			boolean defaultPatternCheckFlag = validationcheckdao.checkTheConfigurationDefaultPatternCheck(listdatadefinition,
					listApplications);
			System.out.println("defaultPatternCheckFlag=" + defaultPatternCheckFlag);
			if (!defaultPatternCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default Pattern check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// Changes to allow user to select either DateRuleCheck or MicrosegmentDateRuleCheck
		// only at one time
		if (flag) {
			if (listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDateRuleCheck() != null
					&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "Choose either DateRuleCheck or MicrosegmentDateRuleCheck, both can't be enabled together.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (flag) {
			boolean dateRuleCheckFlag = validationcheckdao.checkTheConfigurationDateRuleCheck(listdatadefinition,
					listApplications);
			System.out.println("dateRuleCheckFlag=" + dateRuleCheckFlag);
			if (!dateRuleCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Date Rule check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean dgroupNonNullFlag = validationcheckdao.checkConfigurationForDGroupNullCheck(listdatadefinition,
					listApplications);
			System.out.println("MicrosegmentNonNullFlag=" + dgroupNonNullFlag);
			if (!dgroupNonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Microsegment Null check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean dGroupDateRuleCheckFlag = validationcheckdao.checkConfigurationDgroupDateRuleCheck(listdatadefinition,
					listApplications);
			System.out.println("dGroupDateRuleCheckFlag=" + dGroupDateRuleCheckFlag);
			if (!dGroupDateRuleCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Microsegment DateRule check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// sumeet---create
		if (flag) {
			if (listApplications.getBadData().equalsIgnoreCase("Y")) {
			boolean badDataCheckFlag = validationcheckdao.checkTheBadData(listdatadefinition,
					listApplications);
				System.out.println("badDataCheckFlag=" + badDataCheckFlag);
				if (!badDataCheckFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Bad Data check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		// ---------- [priyanka 25-12-2018] --

		// chnges for length Check



		if (flag) {
			if (listApplications.getlengthCheck().equalsIgnoreCase("Y")) {

				System.out.println("ValidationCheckController if ( uncom listApplications.getlengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean lengthCheck = validationcheckdao.checkForLengthCheck(listdatadefinition,
						listApplications);
				System.out.println("lengthCheck=" + lengthCheck);
				if (!lengthCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Length check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		// -------------------
		
		// Max Length Check

		if (flag) {
			if (listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {

				System.out.println("ValidationCheckController if ( uncom listApplications.getMaxLengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean maxLengthCheck = validationcheckdao.checkForMaxLengthCheck(listdatadefinition,
						listApplications);
				System.out.println("maxLengthCheck=" + maxLengthCheck);
				if (!maxLengthCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The MAX Length check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		//End of Max Length Check
		
		

		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.defaultCheckFlag(listdatadefinition,
					listApplications);
			System.out.println("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// Changes to allow user to select either DataDrift or Microsegment Based DataDrift
		// only at one time
		if (flag) {
			if (listApplications.getDataDriftCheck() != null && listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDataDriftCheck() != null
					&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "Choose either DataDrift or Microsegment Based DataDrift, both can't be enabled together.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}



		if (flag) {
			if(listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
				boolean microsegmentFlag = validationcheckdao.checkTheConfigurationForMicrosegment(listdatadefinition,
						listApplications);
				System.out.println("microsegmentFlag=" + microsegmentFlag);
				if (!microsegmentFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Microsegment check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		if (flag) {

			Long idApp = listApplications.getIdApp();
			validationcheckdao.insertintolistdfsetruleandtranrule(idApp,
					listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());

			/*if(listApplications.getHistoricDateFormat()==""){
				String automaticDateFormat = validationcheckdao.getAutomaticDateFormat(listApplications);
				listApplications.setHistoricDateFormat(automaticDateFormat);
			}*/
			int updateintolistapplication = validationcheckdao
					.updateintolistapplicationForAjaxRequest(listApplications);
			System.out.println("updateintolistapplication=" + updateintolistapplication);
			System.out.println("datadriftthreshold=" + listApplications.getDataDriftThreshold());
			// Sumeet_21_08_2018
			int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
					listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
					listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(), "N",
					listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(), 0.0);
			System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

			int insertintolistdftranrule = validationcheckdao.insertintolistdftranrule(idApp,
					listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
					listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());

			// Create rule catalog
			boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
			if (isRuleCatalogDiscovery)
				ruleCatalogService.createRuleCatalog(idApp);
			
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/customizeUpdateValidationCheckAjax", method = RequestMethod.POST)
	public void customizeUpdateValidationCheckAjax(@RequestBody ListApplications listApplications,
			HttpServletResponse response, HttpSession session) {

		boolean flag = true;
		Object user = session.getAttribute("user");
		System.out.println("data:" + listApplications);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("###############33 listApplications data domain ="+listApplications.getData_domain());

		/*
			Pradeep 8-Mar-2020 Global threshold changes: Debug log only, no code change to this method at all
			just View log for new field 'thresholdsApplyOption' reach from UI to 'listApplications' variable or not.
			so this object carry new field via updateintolistapplicationForAjaxRequest() parameter to dao save code
		*/
		try {
			ObjectMapper oMapper = new ObjectMapper();
			String sListApplication = oMapper.writeValueAsString(listApplications);
			DateUtility.DebugLog("customizeUpdateValidationCheckAjax 01",
						String.format("thresholdsApplyOption = %1$s \n full submitted data = %2$s",
							listApplications.getThresholdsApplyOption(), sListApplication));
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());

		if (flag) {
			boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForPatternCheckTab(listdatadefinition,
					listApplications);
			System.out.println("buildHistoricFlag=" + buildHistoricFlag);
			if (!buildHistoricFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Pattern check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean defaultPatternFlag = validationcheckdao.checkTheConfigurationForDefaultPatternCheckTab(listdatadefinition,
					listApplications);
			System.out.println("defaultPatternFlag=" + defaultPatternFlag);
			if (!defaultPatternFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default Pattern check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		//////////////////////////////////////////////////////
		if (flag) {
			if (listApplications.getBadData().equalsIgnoreCase("Y")) {
			boolean badDataCheckFlag = validationcheckdao.checkTheBadData(listdatadefinition,
					listApplications);
				System.out.println("badDataCheckFlag=" + badDataCheckFlag);
				if (!badDataCheckFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Bad Data check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		/////////////////////////////////////////////////////
		if (flag) {
			if(listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")){
				if(listApplications.getHistoricStartDate()==null || listApplications.getHistoricStartDate().trim().isEmpty()
						|| listApplications.getHistoricEndDate() == null || listApplications.getHistoricEndDate().isEmpty()){
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "Start Date or End Date is missing");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (flag) {
			boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForBuildHistoric(listdatadefinition,
					listApplications);
			System.out.println("buildHistoricFlag=" + buildHistoricFlag);
			if (!buildHistoricFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The last read time check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {

			boolean identityFlag = validationcheckdao.checkTheConfigurationForDupRowIdentity(listdatadefinition,
					listApplications);
			System.out.println("identityFlag=" + identityFlag);
			if (!identityFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate identity check (primary key) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
					listApplications);
			System.out.println("applyRulesFlag=" + applyRulesFlag);
			if (!applyRulesFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("rules", "The rules are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean applyDerivedColumnsFlag = validationcheckdao
					.checkTheConfigurationForapplyDerivedColumns(listdatadefinition, listApplications);
			System.out.println("applyDerivedColumnsFlag=" + applyDerivedColumnsFlag);
			if (!applyDerivedColumnsFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("derivedcols", "The Derived Columns are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean AllFlag = validationcheckdao.checkTheConfigurationForDupRowAll(listdatadefinition,
					listApplications);
			System.out.println("AllFlag=" + AllFlag);
			if (!AllFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate all check (dupkey) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean dataDriftFlag = validationcheckdao.checkTheConfigurationForDataDrift(listdatadefinition,
					listApplications);
			System.out.println("dataDriftFlag=" + dataDriftFlag);
			if (!dataDriftFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The data drift check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean numFieldFlag = validationcheckdao.checkTheConfigurationForNumField(listdatadefinition,
					listApplications);
			System.out.println("numFieldFlag=" + numFieldFlag);
			if (!numFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The numerical stat check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean stringFieldFlag = validationcheckdao.checkTheConfigurationForstringField(listdatadefinition,
					listApplications);
			System.out.println("stringFieldFlag=" + stringFieldFlag);
			if (!stringFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The string stat check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			// System.out.println("recordcountanomaly="+listApplications.getRecordCountAnomaly());
			// System.out.println("getkeyBasedRecordCountAnomaly="+listApplications.getkeyBasedRecordCountAnomaly());
			boolean dGroupFlag = validationcheckdao.checkTheConfigurationFordGroup(listdatadefinition,
					listApplications);
			System.out.println("dGroupFlag=" + dGroupFlag);
			if (!dGroupFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The subsegment check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean recordAnomalyFlag = validationcheckdao.checkTheConfigurationForRecordAnomaly(listdatadefinition,
					listApplications);
			System.out.println("recordAnomalyFlag=" + recordAnomalyFlag);
			if (!recordAnomalyFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The record anomaly check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean nonNullFlag = validationcheckdao.checkTheConfigurationForNonNullField(listdatadefinition,
					listApplications);
			System.out.println("nonNullFlag=" + nonNullFlag);
			if (!nonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The nonnull check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			if (listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
				Long idData = listApplications.getIdData();
				boolean incrementalCheck = validationcheckdao.checkForIncrementalInListDataAccess(idData);
				System.out.println("incrementalCheck=" + incrementalCheck);
				if (!incrementalCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("incremental",
								"Cannot use data slices in source with Historic and Incremental Type of Application");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		// Changes to allow user to select either DateRuleCheck or MicrosegmentDateRuleCheck
		// only at one time
		if (flag) {
			if (listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDateRuleCheck() != null
					&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "Choose either DateRuleCheck or MicrosegmentDateRuleCheck, both can't be enabled together.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (flag) {
			if (listApplications.getDateRuleChk().equalsIgnoreCase("Y")) {
				System.out.println("-----------in validationCheckControllerD getDateRuleChk");
				Long idData = listApplications.getIdData();
				System.out.println("validationCheckControllerD idData=>" + idData);

				// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
				boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(listdatadefinition, listApplications);
				System.out.println("validationCheckControllerD dateRuleCheck=>" + dateRuleCheck);

				if (!dateRuleCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail",
								"The Date Rule check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		if (flag) {
			boolean dGroupDateRuleCheckFlag = validationcheckdao
					.checkConfigurationDgroupDateRuleCheck(listdatadefinition, listApplications);
			System.out.println("MicrosegmentDateRuleCheckFlag=" + dGroupDateRuleCheckFlag);
			if (!dGroupDateRuleCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Microsegment DateRule check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean dgroupNonNullFlag = validationcheckdao.checkConfigurationForDGroupNullCheck(listdatadefinition,
					listApplications);
			System.out.println("MicrosegmentNonNullFlag=" + dgroupNonNullFlag);
			if (!dgroupNonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Microsegment Null check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}


		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.checkTheConfigurationDefaultCheck(listdatadefinition,
					listApplications);
			System.out.println("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		//////////////////// --AKSHAY 4/3/2019--////////////////////////////
		if (flag) {
			if(listApplications.getTimelinessKeyChk().equalsIgnoreCase("Y"))
			{
			boolean time = validationcheckdao.checkTheConfigurationForTimelinessKeyField(listdatadefinition, listApplications);
				System.out.println("time=" + time);
				if (!time) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The TimelinessKey check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
		}}
		/////////////////////////////////////////////////////////////////
		// changes for lengthCheck 31Dec18
		if (flag) {
			if (listApplications.getlengthCheck().equalsIgnoreCase("Y")) {
				System.out.println("validationCheckController if (flag) listApplications.getlengthCheck().equalsIgnoreCase(\"Y\") -> ");
				Long idData = listApplications.getIdData();
				// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
				boolean lengthCheck = validationcheckdao.checkForLengthCheck(listdatadefinition, listApplications);

				System.out.println("lengthCheck=" + lengthCheck);
				if (!lengthCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Length check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
			// Max Length Check
						if (flag) {
							if (listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {
								System.out.println("validationCheckController if (flag) listApplications.getMaxLengthCheck().equalsIgnoreCase(\"Y\") -> ");
								Long idData = listApplications.getIdData();
								// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
								boolean maxLengthCheck = validationcheckdao.checkForMaxLengthCheck(listdatadefinition, listApplications);

								System.out.println("MaxlengthCheck=" + maxLengthCheck);
								if (!maxLengthCheck) {
									flag = false;
									try {
										JSONObject json = new JSONObject();
										// put some value pairs into the JSON object .
										json.put("fail", "The Max Length check is configured incorrectly");
										response.getWriter().println(json);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// return true;
								}
							}
						}

			// Changes to allow user to select either DataDrift or Microsegment Based DataDrift
			// only at one time
			if (flag) {
				if (listApplications.getDataDriftCheck() != null && listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
						&& listApplications.getdGroupDataDriftCheck() != null
						&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "Choose either DataDrift or Microsegment Based DataDrift, both can't be enabled together.");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}



			if (flag) {
				if(listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
					boolean microsegmentFlag = validationcheckdao.checkTheConfigurationForMicrosegment(listdatadefinition,
							listApplications);
					System.out.println("microsegmentFlag=" + microsegmentFlag);
					if (!microsegmentFlag) {
						flag = false;
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object .
							json.put("fail", "The Microsegment check is configured incorrectly");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// return true;
					}
				}
			}

			if (flag) {
				Long idApp = listApplications.getIdApp();
				boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
				
				// If RuleCatalog is enabled and staging is activated updating the details in
				// staging_listApplications table
				boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);
				if (isRuleCatalogEnabled && validationStatingEnabled) {

					ruleCatalogDao.updateIntoStagingListapplication(listApplications);

					ruleCatalogDao.updateStagingListDfTranRule(idApp, listApplications.getDupRowIdentity(),
							listApplications.getDupRowIdentityThreshold(), listApplications.getDupRowAll(),
							listApplications.getDupRowAllThreshold());
					
					// Update Foundation checks details to actual listApplications
					// As these are related to only Application Mode, Historic and Incremental details, Frequency
					validationcheckdao.updateFoundationChecksDetailsToListApplications(listApplications);
				} else {
					validationcheckdao.updateintolistdfsetruleandtranrule(idApp,
							listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());

					int updateintolistapplication = validationcheckdao
							.updateintolistapplicationForAjaxRequest(listApplications);
					System.out.println("updateintolistapplication=" + updateintolistapplication);
					System.out.println("datadriftthreshold=" + listApplications.getDataDriftThreshold());

					// Sumeet_21_08_2018
					int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
							listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
							listApplications.getNumericalStatThreshold(), "N",
							listApplications.getStringStatThreshold(), "N",
							listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(),
							0.0);
					System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

					int insertintolistdftranrule = validationcheckdao.updateintolistdftranrule(idApp,
							listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
							listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());
					System.out.println("insertintolistdftranrule=" + insertintolistdftranrule);
				}

				// If RuleCatalog is enabled, update Rule catalog
				if (isRuleCatalogEnabled) {
					System.out.println(
							"\n====> Updating the rule changes related to validation customization into Rule Catalog");
					ruleCatalogService.updateRuleCatalog(idApp);
				}

				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("success", "Validation Check Customized Successfully");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	@RequestMapping(value = "/saveValidationCheckCustomizedData")
	public ModelAndView saveValidationCheckCustomizedData(HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("demo");
		modelAndView.addObject("msg", "Validation Check	Customized Successfully");
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {
			try {
				Long idApp = Long.parseLong(request.getParameter("idApp"));
				System.out.println("idApp=" + idApp);
				Long idData = Long.parseLong(request.getParameter("idData"));
				System.out.println("idData=" + idData);

				String recordCount = request.getParameter("recordCount");

				System.out.println("recordCount=" + recordCount);

				/*
				 * String
				 * recordCountText=request.getParameter("recordCountText");
				 *
				 * Long record=0l;
				 * if(!(recordCountText==null||recordCountText.equals(""))){
				 * record=Long.parseLong(recordCountText); }
				 * System.out.println("record="+record);
				 */

				String nullCount = request.getParameter("nullCount");
				System.out.println("nullCount=" + nullCount);

				String duplicateCount = request.getParameter("duplicateCount");
				System.out.println("duplicateCount=" + duplicateCount);

				String duplicateCountText = request.getParameter("duplicateCountText");
				Double duplicateCountTextNull = 0.0;
				if (!(duplicateCountText == null || duplicateCountText.equals(""))) {
					duplicateCountTextNull = Double.parseDouble(duplicateCountText);
				}
				System.out.println("duplicateCountTextNull=" + duplicateCountTextNull);

				String duplicateCountAll = request.getParameter("duplicateCountAll");
				System.out.println("duplicateCountAll=" + duplicateCountAll);
				String duplicateCountAllText = request.getParameter("duplicateCountAllText");
				Double duplicateCountAllTextNull = 0.0;
				if (!(duplicateCountAllText == null || duplicateCountAllText.equals(""))) {
					duplicateCountAllTextNull = Double.parseDouble(duplicateCountAllText);
				}
				System.out.println("duplicateCountAllTextNull=" + duplicateCountAllTextNull);

				String numericalStats = request.getParameter("numericalStats");
				System.out.println("numericalStats=" + numericalStats);

				String stringStat = request.getParameter("stringStat");
				System.out.println("stringStat=" + stringStat);

				String columnOrderVal = request.getParameter("columnOrderVal");
				System.out.println("columnOrderVal=" + columnOrderVal);

				String fileNameVal = request.getParameter("fileNameVal");
				System.out.println("fileNameVal=" + fileNameVal);

				//
				String recordAnomaly = request.getParameter("recordAnomalyid");
				System.out.println("recordAnomaly=" + recordAnomaly);

				String DFSetComparison = request.getParameter("DFSetComparisonId");
				Double DFSetComparisonnull = 0.0;
				if (!(DFSetComparison == null || DFSetComparison.equals("")))
					DFSetComparisonnull = Double.parseDouble(DFSetComparison);
				System.out.println("DFSetComparisonnull=" + DFSetComparisonnull);
				String timeSeries = "";
				String[] timeSeriesCheckbox = request.getParameterValues("check_id");

				String recordCountAnomalyType = request.getParameter("recordCountAnomalyType");
				System.out.println("recordCountAnomalyType=" + recordCountAnomalyType);

				String applyRules = request.getParameter("applyRules");
				System.out.println("applyRules=" + applyRules);

				String applyDerivedColumns = request.getParameter("applyDerivedColumns");
				System.out.println("applyDerivedColumns=" + applyDerivedColumns);

				String startDate = request.getParameter("startdateid");
				System.out.println("startDate=" + startDate);
				String endDate = request.getParameter("enddateid");
				System.out.println("endDate=" + endDate);
				String dateformat = request.getParameter("dateformatid");
				System.out.println("dateformatid=" + dateformat);
				// System.out.println("timeSeriesCheckbox="+timeSeriesCheckbox);
				if (timeSeriesCheckbox != null) {
					for (int i = 0; i < timeSeriesCheckbox.length; i++) {
						timeSeries = timeSeries + timeSeriesCheckbox[i] + ",";
						/*
						 * if (timeSeriesCheckbox[i].equalsIgnoreCase("month"))
						 * { String monthdropdown =
						 * request.getParameter("monthdropdown");
						 * System.out.println("monthdropdown=" + monthdropdown);
						 * timeSeries = timeSeries + monthdropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("dayOfWeek"))
						 * { String dowdropdown =
						 * request.getParameter("dowdropdown");
						 * System.out.println("dowdropdown=" + dowdropdown);
						 * timeSeries = timeSeries + dowdropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("hourOfDay"))
						 * { String hoddropdown =
						 * request.getParameter("hoddropdown");
						 * System.out.println("hoddropdown=" + hoddropdown);
						 * timeSeries = timeSeries + hoddropdown; } if
						 * (timeSeriesCheckbox[i].equalsIgnoreCase("dayOfMonth")
						 * ) { String domdropdown =
						 * request.getParameter("domdropdown");
						 * System.out.println("domdropdown=" + domdropdown);
						 * timeSeries = timeSeries + domdropdown; }
						 */
						System.out.println("timeSeries=" + timeSeriesCheckbox[i]);
						// timeSeries = timeSeries + "), ";
					}
					timeSeries = timeSeries.toString().substring(0, timeSeries.length() - 1);
				}
				System.out.println("timeSeries=" + timeSeries.trim());

				String dataDriftCheck = request.getParameter("dataDriftCheck");
				System.out.println("dataDriftCheck=" + dataDriftCheck);
				String updateFrequency = request.getParameter("frequencyid");
				System.out.println("updateFrequency=" + updateFrequency);
				String frequencyDays = request.getParameter("EveryDay");
				int frequencyDaysnull = 0;
				if (!(frequencyDays == null || frequencyDays.equals("")))
					frequencyDaysnull = Integer.parseInt(frequencyDays);
				System.out.println("frequencyDaysnull=" + frequencyDaysnull);
				String outofNorm = request.getParameter("outofNorm");
				System.out.println("outofNorm=" + outofNorm);
				String incrementalMatching = request.getParameter("incrementalTypeId");
				System.out.println("incrementalMatching=" + incrementalMatching);
				String buildHistoricFingerPrint = request.getParameter("buildHistoricId");
				System.out.println("buildHistoricFingerPrint=" + buildHistoricFingerPrint);
				String historicStartDate = null;
				String historicEndDate = null;
				String historicDateFormat = null;
				if (buildHistoricFingerPrint == null)
					buildHistoricFingerPrint = "N";
				// if (buildHistoricFingerPrint.equalsIgnoreCase("Y")) {
				historicStartDate = request.getParameter("startdateid");
				System.out.println("historicStartDate=" + historicStartDate);
				historicEndDate = request.getParameter("enddateid");
				System.out.println("historicEndDate=" + historicEndDate);
				historicDateFormat = request.getParameter("dateformatid");
				System.out.println("historicDateFormat=" + historicDateFormat);
				// }
				String nameofEntityColumn = request.getParameter("nameofEntityColumn");
				System.out.println("nameofEntityColumn=" + nameofEntityColumn);
				String csvDirectory = request.getParameter("csvDirectory");
				System.out.println("csvDirectory=" + csvDirectory);

				String groupEquality = request.getParameter("groupEquality");
				System.out.println("groupEquality=" + groupEquality);

				String groupEqualityText = request.getParameter("groupEqualityText");
				Double groupEqualityThreshold = 0.0;
				if (!(groupEqualityText == null || groupEqualityText.equals("")))
					groupEqualityThreshold = Double.parseDouble(groupEqualityText);
				System.out.println("groupEqualityThreshold=" + groupEqualityThreshold);
				// sumeet
				String badData = request.getParameter("badData");
				System.out.println("badData=" + badData);

				// 24_DEC_2018 (12.43pm) Priyanka
				String lengthCheck = request.getParameter("lengthCheck");
				System.out.println("lengthCheck =" + lengthCheck);

				String maxLengthCheck = request.getParameter("maxLengthCheck");
				System.out.println("maxLengthCheck =" + maxLengthCheck);

				// changes for DateRuleChk 8jan2019 priyanka
				String dateRuleCheck = request.getParameter("dateRuleCheck");
				System.out.println("dateRuleCheck =>" + dateRuleCheck);

				ListApplications listApplication = new ListApplications();
				listApplication.setIncrementalMatching(incrementalMatching);
				listApplication.setBuildHistoricFingerPrint(buildHistoricFingerPrint);
				listApplication.setHistoricStartDate(historicStartDate);
				listApplication.setHistoricEndDate(historicEndDate);
				listApplication.setHistoricDateFormat(historicDateFormat);
				listApplication.setCsvDir(csvDirectory);
				listApplication.setGroupEquality(groupEquality);
				listApplication.setGroupEqualityThreshold(groupEqualityThreshold);
				listApplication.setOutOfNormCheck(outofNorm);
				listApplication.setColOrderValidation(columnOrderVal);
				listApplication.setFileNameValidation(fileNameVal);
				listApplication.setEntityColumn(nameofEntityColumn);
				listApplication.setIdApp(idApp);
				listApplication.setNonNullCheck(nullCount);
				listApplication.setRecordAnomalyCheck(recordAnomaly);
				listApplication.setNumericalStatCheck(numericalStats);
				listApplication.setStringStatCheck(stringStat);
				listApplication.setDataDriftCheck(dataDriftCheck);
				listApplication.setUpdateFrequency(updateFrequency);
				listApplication.setFrequencyDays(frequencyDaysnull);
				listApplication.setRecordCountAnomaly(recordCount);
				listApplication.setRecordCountAnomalyThreshold(DFSetComparisonnull);
				listApplication.setTimeSeries(timeSeries);
				listApplication.setApplyRules(applyRules);
				listApplication.setApplyDerivedColumns(applyDerivedColumns);
				listApplication.setBadData(badData);
				listApplication.setlengthCheck(lengthCheck);
				listApplication.setMaxLengthCheck(maxLengthCheck);

				// Date Rule Change
				listApplication.setDateRuleChk(dateRuleCheck);

				int updateintolistapplication = validationcheckdao.updateintolistapplicationforCustomize(outofNorm,
						columnOrderVal, fileNameVal, 0.0, nameofEntityColumn, idApp, nullCount, recordAnomaly,
						numericalStats, stringStat, dataDriftCheck, updateFrequency, frequencyDaysnull, recordCount,
						DFSetComparisonnull, timeSeries, recordCountAnomalyType, applyRules, applyDerivedColumns,
						badData, lengthCheck,maxLengthCheck, dateRuleCheck, listApplication);
				System.out.println("updateintolistapplication=" + updateintolistapplication);

				int insertintolistdftranrule = validationcheckdao.updateintolistdftranrule(idApp, duplicateCount,
						duplicateCountTextNull, duplicateCountAll, duplicateCountAllTextNull);
				System.out.println("insertintolistdftranrule=" + insertintolistdftranrule);

				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
				/*
				 * List<String> warningmsgs =
				 * validationcheckdao.validationCheckprerequisite(idApp, idData,
				 * duplicateCountAll
				 * ,nullCount,duplicateCount,recordAnomaly,stringStat,
				 * numericalStats ); if(warningmsgs.size()!=0) {
				 * modelAndView.setViewName("showWarnings");
				 * modelAndView.addObject("warningmsgs", warningmsgs); }
				 */

			} catch (Exception e) {
				e.printStackTrace();
				return modelAndView;
			}

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteValidationCheckajax", method = RequestMethod.POST)
	public void deleteValidationCheckajax(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idApp) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("idApp=" + idApp);
		// Integer countofRec = validationcheckdao.getCountFromResult_masterById(idApp);
		int delete = -1;
		/*if (countofRec == 0) {
			delete = validationcheckdao.deletefromlistapplications(idApp, "delete");
		} else {*/
		delete = validationcheckdao.deletefromlistapplications(idApp, "updateActive");
		// }
		System.out.println("delete=" + delete);
		if (delete > 0) {
			try {
				response.getWriter().println("Validation Check deleted successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				response.getWriter().println("There is a problem");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/dataApplicationDeleteView")
	public ModelAndView dataApplicationDeleteView(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "D", session);
		if (rbac) {

			Long idApp = Long.parseLong(request.getParameter("idApp"));
			System.out.println("idApp=" + idApp);
			String listappName = request.getParameter("laName");
			System.out.println("listappName=" + listappName);
			String listsourceName = request.getParameter("lsName");
			System.out.println("listsourceName=" + listsourceName);

			ModelAndView modelAndView = new ModelAndView("dataApplicationDeleteView");
			modelAndView.addObject("listsourceName", listsourceName);
			modelAndView.addObject("listappName", listappName);
			modelAndView.addObject("idApp", idApp);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/dataSourceDisplayAllView")
	public ModelAndView dataSourceDisplayAllView(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {
			try{
				Long idData = Long.parseLong(request.getParameter("idData"));
				System.out.println("idData=" + idData);

				List<ListDataSource> listdatasource = validationcheckdao.getdatafromlistdatasource(idData);

				ModelAndView modelAndView = new ModelAndView("dataSourceDisplayAllView");
				modelAndView.addObject("listdatasource", listdatasource);
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
				return modelAndView;

			}catch (Exception e){
				e.printStackTrace();
			}

		}

		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/finishSchemaCustomization")
	public ModelAndView finishSchemaCustomization(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("finishSchemaCustomization");
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;
	}


	 @RequestMapping(value = "/copyConnection")
	    public void copyConnection(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam String newConnectionName, @RequestParam int idDataSchema) {

		System.out.println("newConnectionName is " + newConnectionName);
		System.out.println("idDataSchema is " + idDataSchema);

		JSONObject json = new JSONObject();

		try {
			long idUser = (Long) session.getAttribute("idUser");

			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

			String createdByUser = "";
			if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
				createdByUser = (String) session.getAttribute("createdByUser");
				System.out.println("======= createdByUser ===>" + createdByUser);
			} else {
				// getting createdBy username from createdBy userId
				System.out.println("======= idUser ===>" + idUser);

				createdByUser = userDAO.getUserNameByUserId(idUser);

				System.out.println("======= createdByUser ===>" + createdByUser);
			}

			// Get the details of connection
			List<ListDataSchema> listDataSchema_list = listdatasourcedao.getListDataSchemaForIdDataSchema(idDataSchema);

			if (listDataSchema_list != null && listDataSchema_list.size() > 0) {

				ListDataSchema listDataSchema = listDataSchema_list.get(0);

				// Get projectId
				long projectId = listDataSchema.getProjectId();

				// Get domainId
				int domainId = listDataSchema.getDomainId();

				// Check if the new connection name already exists
				String duplicateName = schemaDao.duplicateSchemaName(newConnectionName, projectId, domainId);
				if (duplicateName != null && !duplicateName.trim().isEmpty()) {

					System.out.println("\n====> A connection with name [] already exists in Domain:[" + domainId
							+ "] and Project:[" + projectId + "] combination !!");
					// Duplicate name exists
					json.put("failure", "Connection copy failed, name already exits");
				} else {
					String updateListDatasSchema = ("insert into listDataSchema (schemaName, schemaType, "
							+ "ipAddress, databaseSchema, username, password, port, project_id, domain, "
							+ "gss_jaas, krb5conf, autoGenerate, suffixes, "
							+ "prefixes, createdAt, updatedAt, createdBy, "
							+ "updatedBy, hivejdbchost, hivejdbcport, sslEnb, "
							+ "sslTrustStorePath, trustPassword, Action, createdByUser, folderPath, fileNamePattern, fileDataFormat, "
							+ "headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,"
							+ "bigQueryProjectName, privateKey, privateKeyId, clientId, clientEmail, datasetName,"
							+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath,partitionedFolders,"
							+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount, maxFolderDepth, fileEncrypted, domain_id, readLatestPartition,alation_integration_enabled,push_down_query_enabled"
							+ ") (select '" + newConnectionName + "' as schemaName, schemaType, "
							+ "ipAddress, databaseSchema, username, password, port, project_id, domain, "
							+ "gss_jaas, krb5conf, autoGenerate, suffixes, "
							+ "prefixes, createdAt, updatedAt, createdBy, "
							+ "updatedBy, hivejdbchost, hivejdbcport, sslEnb, "
							+ "sslTrustStorePath, trustPassword, 'Yes', '" + createdByUser
							+ "' as createdByUser, folderPath, fileNamePattern, fileDataFormat, "
							+ "headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName, "
							+ "bigQueryProjectName, privateKey, privateKeyId, clientId, clientEmail, datasetName,"
							+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath,partitionedFolders,"
							+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount, maxFolderDepth, fileEncrypted, domain_id, readLatestPartition,alation_integration_enabled,push_down_query_enabled "
							+ " from listDataSchema where " + "idDataSchema = " + idDataSchema + ")");
					jdbcTemplate.execute(updateListDatasSchema);

					System.out.println("listdataschema updated");

					json.put("success", "Connection Copied Successfully");
				}
			} else {
				System.out.println("\n====> Failed to get details of connection with Id[" + idDataSchema
						+ "], hence copy failed !!");
				json.put("failure", "Connection copy failed");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response.getWriter().println(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 @RequestMapping(value = "/copyRules")
	    public void copyRules(HttpServletRequest request, HttpSession session, HttpServletResponse response,
	    		@RequestParam String newRuleName, @RequestParam int idListColrules){

			 System.out.println("newRuleName is " + newRuleName );
			 System.out.println("idListColrules is " + idListColrules );
			 Long idUser = (Long) session.getAttribute("idUser");
			// activedirectory flag check
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



			  String updateListColRules = ("insert into listColRules (idData, idCol, ruleName, "
			  		+ "description, createdAt, ruleType, expression, external, "
			  		+ "externalDatasetName, idRightData, matchingRules, matchType, createdByUser,ruleThreshold ,project_id ,domain_id, domensionId) "
			  		+ "(select idData, idCol, '" + newRuleName + "' as ruleName, description, createdAt, ruleType, expression,"
			  		+ " external, "
			  		+ "externalDatasetName, idRightData, matchingRules, matchType, '"+ createdByUser +"' as createdByUser , ruleThreshold ,project_id ,domain_id, domensionId "
			  		+ "from listColRules where "
			     		+ "idListColrules = " + idListColrules + ")");
			     jdbcTemplate.execute(updateListColRules);
			     System.out.println("ListColRules updated");


			     JSONObject json = new JSONObject();
				 json.put("success", "Rule Coppyed Successfully");
	 }

	 @RequestMapping(value = "/copyGlobalRules")
	 public void copyGlobalRules(HttpServletRequest request, HttpSession session, HttpServletResponse response,
	 @RequestParam String newRuleName, @RequestParam Long idListColrules){

	 System.out.println("newRuleName is " + newRuleName );
	 System.out.println("idListColrules is " + idListColrules );

     globalRuleDAO.copyGlobalRulesData(newRuleName, idListColrules);

	 JSONObject json = new JSONObject();
	 json.put("success", "Rule Copied Successfully");
	 }


	    @RequestMapping(value = "/copyValidation")
	    public void copyValidation(HttpServletRequest request, HttpSession session, HttpServletResponse response,
	    		@RequestParam String newValidationName, @RequestParam long idApp){

			 System.out.println("newTemplateName is " + newValidationName );
			 System.out.println("idApp is " + idApp );
			 long idUser = (Long) session.getAttribute("idUser");
			// activedirectory flag check
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

				Long newIdApp = validationcheckdao.copyValidation(idApp, newValidationName, createdByUser);

				if (newIdApp != null && newIdApp > 0l) {
					System.out.println("\n====> Validation Check Copied Successfully");
		
					// Create rule catalog
					boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
					if (isRuleCatalogDiscovery)
						ruleCatalogService.createRuleCatalog(newIdApp);
		
				} else
					System.out.println("\n====> Validation Check Copied failed");
	}

	@RequestMapping(value = "/inactivateValidation")
	public void inactivateValidation(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			 @RequestParam int idApp) {

		String activeStatusSql = "select active from listApplications where idApp = " + idApp;


		SqlRowSet rs1 = jdbcTemplate.queryForRowSet(activeStatusSql);
		String activeStatus = "";
		while (rs1.next()) {
			activeStatus = rs1.getString(1);
		}

		if(activeStatus.equals("yes")) {
			String inacctivateSql = "update listApplications set active = 'no' where idApp = " + idApp;
			jdbcTemplate.execute(inacctivateSql);
		}
		if(activeStatus.equals("no")) {
			String acctivateSql = "update listApplications set active = 'yes' where idApp = " + idApp;
			jdbcTemplate.execute(acctivateSql);
		}

		JSONObject json = new JSONObject();
		json.put("success", "Active status changed Successfully");
	}


	@RequestMapping(value = "/validationCheck_View")
	public ModelAndView validationCheckView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {
			DateUtility.DebugLog("validationCheck_View 01","Start of controller");
			
			ModelAndView modelAndView = new ModelAndView("validationView");
			boolean isRuleCatalogDiscovery = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N").equalsIgnoreCase("Y") ? true : false;
			modelAndView.addObject("isRuleCatalogDiscovery", isRuleCatalogDiscovery);
			modelAndView.addObject("SelectedProjectId", (Long)session.getAttribute("projectId"));
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			
			DateUtility.DebugLog("validationCheck_View 02","End of controller");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
}
	
	@RequestMapping(value = "/getPaginatedValidationsList", method = RequestMethod.POST, produces = "application/json")
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
			oJsonResponse.put("FromMapping", "validationView");
			

			for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds", "SearchText" }) {
				oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
			}

			oJsonResponse.put("ViewPageDataList", validationcheckdao.getPaginatedValidationsJsonData(oPaginationParms));
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

	

	@RequestMapping(value = "/dataApplicationCreateView")
	public ModelAndView listValidation(ModelAndView modelAndView, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		Long projectId= (Long)session.getAttribute("projectId");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {
			System.out.println("dataApplicationCreateView");
			//Long projectId= (Long)session.getAttribute("projectId");
			///List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);

			int idData = 0;

		/*	for (ListDataSource listDataSource : getlistdatasourcesname) {
				idData = listDataSource.getIdData();
			}*/

			System.out.println("idData dataApplicationCreateView=>" + idData);


			Long idDataLong = (long) idData;

			// fOR kafka
			String dataLocation = templateviewdao.getDataLocationByidData(idDataLong);

			System.out.println("dataApplicationCreateView dataLocation = " + dataLocation);

			modelAndView.addObject("dataLocationName", dataLocation);
			//Long projectId= (Long)session.getAttribute("projectId");
			List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId,projList,fromDate,toDate);
			//modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
			
			//added Data_Domain for executive summary
			
			List<DataDomain> lstDataDomain = validationcheckdao.getAllDataDomainNames();
			Integer domainId = (Integer) session.getAttribute("domainId");

			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add New");
			modelAndView.setViewName("dataApplicationCreateView");
			modelAndView.addObject("listdataschema", listdataschema);
			modelAndView.addObject("lstDataDomain",lstDataDomain);
			
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createValidationCheckAjax", method = RequestMethod.POST)
	public ModelAndView createValidationCheckAjax(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Object user = session.getAttribute("user");
		Long projectId= (Long)session.getAttribute("projectId");
		Integer domainId = (Integer)session.getAttribute("domainId");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {

			Long idUser = (Long) session.getAttribute("idUser");
			System.out.println("idUser=" + idUser);
			System.out.println("createValidationCheckAjax");
			
			System.out.println(request.getHeader("token"));

			String name = request.getParameter("dataset");
			System.out.println("name=" + name);
			String description = request.getParameter("description");
			System.out.println("description=" + description);

			String getlistName = request.getParameter("getlistName");
			System.out.println("getlistName=" + getlistName);

			long idData=0l;

			String apptype = request.getParameter("apptype");
			System.out.println("apptype=" + apptype);

			System.out.println("************ DATA DOMAIN **********");
			System.out.println("Data Domain ->"+request.getParameter("data_domainId"));

			int data_Domain_id = Integer.parseInt(request.getParameter("data_domainId"));
			String validationJobSize = request.getParameter("validationJobSize");
			String fm_connectionId=request.getParameter("fm_connectionId");
			System.out.println("fm_connectionId: "+fm_connectionId);


			//String templatename=request.getParameter("selectedTables");
			//String str=templatename.toString().replace("[","").replace("]","").toString().replace("\"", "").trim();
			//System.out.println("templatename :: "+templatename+"  valuess :: "+str);

		//	idData = Long.parseLong(request.getParameter("selectedTables").replace("\"", "").trim());
			//idData = validationcheckdao.getListDataSourceIdData(str);
			//System.out.println("templatename :: "+templatename+"  valuess ::"+idData);

			/*Long idData = 0l;
			 if (apptype.equals("Schema Matching")) {
				idData = 0l;
			 }
			 else {
			System.out.println("==============test======"+request.getParameter("selectedTables").replace("\"", "").trim());
			 idData = Long.parseLong(request.getParameter("selectedTables").replace("\"", "").trim());*/
			 idData = 0l;
			 if (apptype.equals("Schema Matching") || apptype.equals("File Monitoring") || apptype.equals("File Management")) {

				 idData = validationcheckdao.getlatestListDataSourceIdData();
				 String templatename=request.getParameter("selectedTables");
				String str=templatename.toString().replace("[","").replace("]","").toString().replace("\"", "").trim();
				System.out.println("templatename :: "+templatename+"  valuess :: "+str+" @@@ "+idData);
			 }
			 else {
			String templatename=request.getParameter("selectedTables");
			String str=templatename.toString().replace("[","").replace("]","").toString().replace("\"", "").trim();
			System.out.println("templatename :: "+templatename+"  valuess :: "+str);
			idData=validationcheckdao.getListDataSourceIdData(str);
			System.out.println("templatename :: "+templatename+"  valuess :: "+str+" @@@ "+idData);
			 }


			if(apptype.equalsIgnoreCase("Data Forensics")) {
				idData = Long.parseLong(request.getParameter("selectedTables").replace("\"", "").trim());
				System.out.println("********Data Forensics idData=" + idData);
			}

			String stringStatCheck="N";
			if (apptype.equalsIgnoreCase("Matching")) {
				apptype = request.getParameter("matchapptype");
				System.out.println("apptype=" + apptype);
				System.out.println("%%%%%%%%%%%%% First Validation Check Error ============="+request.getParameter("selectedTables"));
				idData = Long.parseLong(request.getParameter("selectedTables").replace("\"", "").trim());
				System.out.println("********Matcing idData=" + idData);
				stringStatCheck="Y";
			}

			if (apptype.equalsIgnoreCase("Rolling DataMatching")) {
				idData = Long.parseLong(request.getParameter("selectedTables").replace("\"", "").trim());
				System.out.println("********Matcing idData=" + idData);


			}
			Long idData1 = -1l;
			try {
				System.out.println("idData=" + idData1);
				idData1 = Long.parseLong(request.getParameter("sourceid"));

			} catch (Exception e) {

			}

			String threshold_id = request.getParameter("threshold_id");
			Double matchingThreshold = 0.0;
			if (!(threshold_id == null || threshold_id.equals(""))) {
				matchingThreshold = Double.parseDouble(threshold_id);
			}
			System.out.println("matchingThreshold=" + matchingThreshold);

			String incrementalMatching = request.getParameter("incremental_Matching_Id");
			if (incrementalMatching == null) {
				incrementalMatching = "N";
			}
			System.out.println("incrementalMatching=" + incrementalMatching);
			String dateFormat = request.getParameter("dateformatid");
			System.out.println("dateFormat=" + dateFormat);
			String leftSliceEnd = request.getParameter("leftsliceend");
			System.out.println("leftSliceEnd=" + leftSliceEnd);

			String fileMonitoringType = request.getParameter("fileMonitoringType");
			System.out.println("fileMonitoringType=" + fileMonitoringType);

			String continuousFileMonitoring = request.getParameter("enableContinuousMonitoring");
			System.out.println("enableContinuousMonitoring=" + continuousFileMonitoring);
			if (continuousFileMonitoring == null || !continuousFileMonitoring.trim().equalsIgnoreCase("Y")) {
				continuousFileMonitoring = "N";
			}

			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
			String createdByUser="";
			if(activeDirectoryFlag.equalsIgnoreCase("Y"))
			{
				createdByUser=(String) session.getAttribute("createdByUser");
				System.out.println("======= createdByUser in Validation ===>"+createdByUser);
			}else {
				// getting createdBy username from createdBy userId
				System.out.println("======= idUser in validation===>"+idUser);

				createdByUser = userDAO.getUserNameByUserId(idUser);

				System.out.println("======= createdByUser in Validation ===>"+createdByUser);
			}

			ListDataSource ld = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			Integer domain_Id = domainId;
			Long project_Id = projectId;
			if(ld != null) {
				domain_Id = (Integer)ld.getDomain();
				project_Id = Long.valueOf(ld.getProjectId());
			} 

			if(apptype.equals("File Monitoring") || apptype.equals("File Management")){
				// if file monitoring getting credentials from session
				domain_Id = domainId;
				project_Id = projectId;
			}

			long idApp = validationcheckdao.insertintolistapplications(name, description, apptype, idData, idUser,
					matchingThreshold, incrementalMatching, dateFormat, leftSliceEnd,fileMonitoringType,continuousFileMonitoring,project_Id,createdByUser,domain_Id,data_Domain_id,stringStatCheck,validationJobSize);
			
			// Save validation creation status to alert_log
			if(idApp > 0l) {
				integrationMasterService.saveAlertEventLog("", 1l, project_Id, idApp,
						name,TaskTypes.validation, AlertManagement.Validation_Creation_Success, "success",
						createdByUser, null);
			} else {
				JSONArray mappingErros = new JSONArray();
				mappingErros.put("Exception occured while inserting data into listApplications");
				integrationMasterService.saveAlertEventLog("", 1l, project_Id, 0l,
						name,TaskTypes.validation, AlertManagement.Validation_Creation_Failure, "failed",
						createdByUser, mappingErros);
			}
			
			int updateApplicationNameWithIdApp = validationcheckdao.updateApplicationNameWithIdApp(name, idApp);
			System.out.println("updateApplicationNameWithIdApp=" + updateApplicationNameWithIdApp);

			System.out.println("idApp=" + idApp);
			boolean applyRulesEnabled = false;
			if(idApp > 0){
				int count = validationcheckdao.getGlobalRulesCountByTemplateId(idData);
				if(count > 0){
					System.out.println("\n====>Enabling Apply rules for validation:"+idApp);
					applyRulesEnabled= validationcheckdao.enableApplyRules(idApp);
				}
			}

			String applicationName = validationcheckdao.getNameFromListDataSources(idData);

			System.out.println("applicationName=" + applicationName);

			if(apptype.equals("File Monitoring") && (fileMonitoringType.equalsIgnoreCase("snowflake") || fileMonitoringType.equalsIgnoreCase("azuredatalakestoragegen2batch") || fileMonitoringType.equalsIgnoreCase("S3 Batch"))) {
				if(fm_connectionId!=null) {
					long schemaId = Long.parseLong(fm_connectionId);
					int checkdata = validationcheckdao.checkFMConnectionDetailsForConnection(schemaId);
					System.out.println("checkdata=" + checkdata);

					if (checkdata > 1)
						validationcheckdao.updateFMConnectionDetails(idApp, schemaId);
					else
						validationcheckdao.insertFMConnectionDetails(idApp, schemaId);
				}
			}
			if (apptype.equals("Data Forensics")) {
				ModelAndView model = new ModelAndView();

				String dataLocation = validationcheckdao.getDataLocationInListDataSources(idData);
				System.out.println("dataLocation=" + dataLocation);
				if (dataLocation.equalsIgnoreCase("HDFS") || dataLocation.equalsIgnoreCase("FILESYSTEM")) {
					System.out.println("finishSchemaCustomization");
					model.setViewName("finishSchemaCustomization");
					model.addObject("validationName", name);
					model.addObject("filesystem", true);
				} else {
					System.out.println("finishSchemaCustomize");
					model.setViewName("finishSchemaCustomization");
					model.addObject("validationName", name);
					model.addObject("filesystem", false);
				}

				if (dataLocation.equalsIgnoreCase("Kafka")) {

					int windowTime = Integer.parseInt(request.getParameter("windowTime"));
					String startTime = request.getParameter("startTime");
					String endTime = request.getParameter("endTime");

					System.out.println("idApp =>" + idApp);
					System.out.println("windowTime =>" + windowTime);
					System.out.println("startTime =>" + startTime);
					System.out.println("endTime =>" + endTime);

					templateviewdao.updatelistApplicationsForKafka(idApp, windowTime, startTime, endTime);

					System.out.println("updated LA for KAFKA........");

				}

				/*	Pradeep 8-Mar-2020 Global threshold changes: drop down list JSON data is send to load in select control on page */
				Map<String, JSONArray> oThresholdOptions = JwfSpaInfra.getAppListsOptionsMap(jdbcTemplate, "GLOBAL_THRESHOLDS_OPTION");
				JSONArray aThresholdOptions = oThresholdOptions.get("GLOBAL_THRESHOLDS_OPTION");

				DateUtility.DebugLog("createValidationCheckAjax 01", aThresholdOptions.toString());

				model.addObject("ThresholdOptions", aThresholdOptions.toString());
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);

				if(applyRulesEnabled)
					model.addObject("applyRulesEnabled", "Y");
				else
					model.addObject("applyRulesEnabled", "N");
				
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");

				return model;
			} else if (apptype.equals("Schema Matching")) {
				ModelAndView model = new ModelAndView("ValidationSuccess");
				Long schemaid1 = Long.parseLong(request.getParameter("schemaid1"));
				Long schemaid2 = Long.parseLong(request.getParameter("schemaid2"));
				String prefix1 = request.getParameter("prefix1");
				String prefix2 = request.getParameter("prefix2");

				String schematypename = request.getParameter("schematypename");

				if (schematypename.equalsIgnoreCase("metadata")) {
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, null, null, prefix1, prefix2);
				} else if (schematypename.equalsIgnoreCase("RC")) {
					String threasholdType = request.getParameter("schema_thresholdtype");
					String rcThreshold = request.getParameter("schema_rc");

					System.out.println("threasholdType:" + threasholdType);
					System.out.println("rcThreshold:" + rcThreshold);
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, threasholdType, rcThreshold, prefix1, prefix2);

				} else if (schematypename.equalsIgnoreCase("both")) {
					String threasholdType = request.getParameter("schema_thresholdtype");
					String rcThreshold = request.getParameter("schema_rc");
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, threasholdType, rcThreshold, prefix1, prefix2);

					long idApp1 = validationcheckdao.insertintolistapplications(name, description, apptype, idData,
							idUser, matchingThreshold, incrementalMatching, dateFormat, leftSliceEnd,fileMonitoringType,continuousFileMonitoring,project_Id,createdByUser,domain_Id,data_Domain_id,"N",validationJobSize);

					templateviewdao.updatelistApplicationsForSchemamatchingForBoth_RC(idApp1, schemaid1, schemaid2,
							schematypename, name, threasholdType, rcThreshold);
				}

				// update listapplications

				System.out.println("schema ids :" + schemaid1 + "   " + schemaid2);

				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("Data Matching")) {
				ModelAndView model = new ModelAndView("matchKeyCreateView");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				if (incrementalMatching.equalsIgnoreCase("Y")) {
					model.addObject("incrementalMatching", incrementalMatching);
					model.addObject("Source2DateFormat", true);
				} else {
					model.addObject("incrementalMatching", "N");
				}
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				System.out.println("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					model.addObject("matchingRulesTrue", true);
				}
				model.addObject("matchingRules", matchingRules);
				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("listRefFunctionsname", listRefFunctionsname);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("currentSection", "Validation Check");
				// model.addObject("currentLink", "VCView");
				model.setViewName("matchKeyCreateView");

				return model;
			} else if (apptype.equals("Rolling DataMatching")) {
				ModelAndView model = new ModelAndView("createRollingDataMatching");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				System.out.println("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					model.addObject("matchingRulesTrue", true);
				}
				model.addObject("matchingRules", matchingRules);
				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("listRefFunctionsname", listRefFunctionsname);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("currentSection", "Validation Check");
				return model;
			}else if (apptype.equals("Data Matching Group")) {
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				ModelAndView model = new ModelAndView("DataMatchingGroupCreateView");
				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("File Management")) {
				System.out.println("File Management");
				ModelAndView model = new ModelAndView("fileManagementCreate");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("Statistical Matching")) {
				System.out.println("Statistical Matching");
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				ModelAndView model = new ModelAndView("statisticalMatchingCreateView");

				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("listRefFunctionsname", listRefFunctionsname);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("currentSection", "Validation Check");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("Primary Key Matching")) {

				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List primaryId=validationcheckdao.getPrimaryKeyListDataDefinition(idData);
				System.out.println("=======================>primaryId="+primaryId);
				ModelAndView model = new ModelAndView("primaryMatchingView");
				ObjectMapper mapper = new ObjectMapper();
				String jsonprimaryId="";
				try {
					jsonprimaryId=mapper.writeValueAsString(primaryId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				model.addObject("jsonprimaryId", jsonprimaryId);
				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("listRefFunctionsname", listRefFunctionsname);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("currentSection", "Validation Check");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("Model Governance")) {
				System.out.println("Model Governance");
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				ModelAndView model = new ModelAndView("modelGovernanceCreateView");

				model.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				model.addObject("listRefFunctionsname", listRefFunctionsname);
				model.addObject("getlistdatasourcesname", getlistdatasourcesname);
				model.addObject("currentSection", "Validation Check");
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("Model Governance Dashboard")) {
				System.out.println("Model Governance Dashboard");
				ModelAndView model = new ModelAndView("modelGovernanceDashboardCreateView");
				Map<Long, String> decileEqualityAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Equality");
				Map<Long, String> decileConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Consistency");
				Map<Long, String> scoreConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Score Consistency");
				model.addObject("decileEqualityAppType", decileEqualityAppType);
				model.addObject("decileConsistencyAppType", decileConsistencyAppType);
				model.addObject("scoreConsistencyAppType", scoreConsistencyAppType);
				model.addObject("idApp", idApp);
				model.addObject("idData", idData);
				model.addObject("apptype", apptype);
				model.addObject("applicationName", applicationName);
				model.addObject("description", description);
				model.addObject("name", name);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				return model;
			} else if (apptype.equals("File Monitoring")) {
				System.out.println("File Monitoring......");
				System.out.println("in Val Check idApp->" + idApp);
				ModelAndView model = new ModelAndView("fileMonitoringUploadCSV");
				model.addObject("idApp", idApp);
				model.addObject("currentSection", "Validation Check");
				model.addObject("currentLink", "Add New");
				model.addObject("fileMonitoringType", fileMonitoringType);
				if(fm_connectionId==null || fm_connectionId.trim().isEmpty())
					fm_connectionId="-1";
				model.addObject("connectionId", fm_connectionId);
				return model;
			}

			return null;
		} else
			return new ModelAndView("loginPage");
	}

	@SuppressWarnings("unlikely-arg-type")
	@RequestMapping(value = "/customizeValidation", method = RequestMethod.GET)
	public ModelAndView customizeValidation(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Validation Check", "U", session);
		if (rbac) {
			System.out.println("idApp =====>>>> " + request.getParameter("idApp"));
			Long idApp = Long.parseLong(request.getParameter("idApp"));
			String name = request.getParameter("laName");
			System.out.println("applicationName=" + name);

			String applicationName = request.getParameter("lsName");
			System.out.println("applicationName=.........................." + applicationName);
			Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("idData=" + idData);
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			/*
			 * List<ListDataDefinition> ListDataDefinitiondata =
			 * validationcheckdao.getDataFromListDataDefinition(idData);
			 * System.out.println("ListDataDefinitiondata="+
			 * ListDataDefinitiondata);
			 * System.out.println("getStringStat="+ListDataDefinitiondata.get(0)
			 * . getStringStat());
			 */

			// Map getdatafromlistdftranrule =
			// validationcheckdao.getdatafromlistdftranrule(idApp);
			// System.out.println("getdatafromlistdftranrule="+getdatafromlistdftranrule);

			String appType = validationcheckdao.getAppTypeFromListApplication(idApp);
			
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
			boolean validationStatingEnabled =  ruleCatalogService.isValidationStagingActivated(idApp);
			
			// If RuleCatalog is enabled and staging is activated fetch the details from
			// staging_listApplications table else from listApplications table
			ListApplications listApplicationsData = null;
			Multimap<String, Double> map = null;
			if(isRuleCatalogEnabled && validationStatingEnabled) {
				listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
				map = ruleCatalogDao.getDataFromStagingListDfTranRule(idApp);
			} else {
				listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
				map = validationcheckdao.getDataFromListDfTranRule(idApp);
			}
			
			//added Data_Domain for executive summary
			
			List<DataDomain> lstDataDomain = validationcheckdao.getAllDataDomainNames(); 
			System.out.println("$$$$$$$$$$$$ Test Data Domain =>"+lstDataDomain);

			
			int data_domain = listApplicationsData.getData_domain();
			
			System.out.println("$$$$$$$$$$$$ Test Data Domain =>"+data_domain);
		
			
			System.out.println("appType=" + appType);
			ModelAndView modelAndView = new ModelAndView();
			if (appType.equals("Schema Matching")) {

				// for editing schema matching validation by pravin
				modelAndView.setViewName("editSchemaMatchingValidation");
				// List<ListDataSchema> listdataschema1 =
				// listdatasourcedao.getListDataSchemaForIdDataSchema(idDataSchema);
				// modelAndView.addObject("applicationName", applicaFtionName);
				modelAndView.addObject("NameValidation", listApplicationsData.getFileNameValidation());
				modelAndView.addObject("name", name);
				modelAndView.addObject("prefix1", listApplicationsData.getPrefix1());
				modelAndView.addObject("prefix2", listApplicationsData.getPrefix2());
				modelAndView.addObject("description", listApplicationsData.getDescription());
				modelAndView.addObject("RecordCountThreshold", listApplicationsData.getRecordCountAnomalyThreshold());

				modelAndView.addObject("entityColumn", listApplicationsData.getEntityColumn());
				modelAndView.addObject("recordCountAnomaly", listApplicationsData.getRecordCountAnomaly());
				modelAndView.addObject("description", listApplicationsData.getDescription());

				List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId,projList,fromDate,toDate);



				long iddataSchemaLeft = listApplicationsData.getIdLeftData();
				long iddataSchemaRight = listApplicationsData.getIdRightData();

				System.out.println("iddataSchema" + iddataSchemaLeft);
				List<ListDataSchema> listdataschema3 = listdatasourcedao.getListDataSchema(projectId,projList,fromDate,toDate);
				String idDataSchemaNmeLeft = listdatasourcedao.getSchemaNameByIdData(iddataSchemaLeft);
				String idDataSchemaNmeRight = listdatasourcedao.getSchemaNameByIdData(iddataSchemaRight);
				listdataschema.removeIf(e -> e.getSchemaName().equals(idDataSchemaNmeLeft));
				listdataschema.removeIf(f -> f.getSchemaName().equals(idDataSchemaNmeRight));





				modelAndView.addObject("idDataSchemaNmeLeft", idDataSchemaNmeLeft);
				modelAndView.addObject("idDataSchemaNmeRight", idDataSchemaNmeRight);
				modelAndView.addObject("listdataschema1", listdataschema);

				System.out.println("idDataSchemaNmeVal =>" + idDataSchemaNmeLeft);


			} else if (appType.equals("Data Matching")) {

				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				modelAndView.addObject("matchingRules", matchingRules);
				System.out.println("matchingRulesSize=" + matchingRules.size());
				// ModelAndView model=new ModelAndView("matchTypeCreateView");
				if (matchingRules.size() >= 1) {
					modelAndView.addObject("matchingRulesTrue", true);
				}
				System.out.println("matchKeyCustomizeView");
				modelAndView.setViewName("matchKeyCustomizeView");
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				// List matchingRules =
				// validationcheckdao.getMatchingRules(idApp);
				System.out.println("matchingRulesSize=" + matchingRules.size());
				// ModelAndView model = new ModelAndView("matchKeyCreateView");
				if (matchingRules.size() >= 1) {
					modelAndView.addObject("matchingRulesTrue", true);
				}
				modelAndView.addObject("matchingRules", matchingRules);
				modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				modelAndView.addObject("listRefFunctionsname", listRefFunctionsname);
				modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
				modelAndView.addObject("dateFormat", listApplicationsData.getUpdateFrequency());
				modelAndView.addObject("matchByValue", listApplicationsData.getFileNameValidation());
				modelAndView.addObject("matchBySubsegment", listApplicationsData.getColOrderValidation());
				modelAndView.addObject("idRightData", listApplicationsData.getIdRightData());
				modelAndView.addObject("absoluteThreshold", listApplicationsData.getRecordCountAnomalyThreshold());
				modelAndView.addObject("UnMatchedAnomalyThreshold", listApplicationsData.getGroupEqualityThreshold());
				// String
				// matchkeyformula=validationcheckdao.getMatchingExpressionFromListDMRules(idApp);
				modelAndView.addObject("matchkeyformula", listApplicationsData.getKeyGroupRecordCountAnomaly());
				modelAndView.addObject("setMatchingAutomatic", listApplicationsData.getOutOfNormCheck());
				modelAndView.addObject("recordCount", listApplicationsData.getNumericalStatCheck());
				modelAndView.addObject("primaryKey", listApplicationsData.getStringStatCheck());
				// modelAndView.addObject("matchkeyformula",matchkeyformula);
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					modelAndView.addObject("Source2DateFormat", true);
				}
			} else if(appType.equals("Rolling DataMatching")) {

				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				modelAndView.addObject("matchingRules", matchingRules);
				System.out.println("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					modelAndView.addObject("matchingRulesTrue", true);
				}
				System.out.println("editRollingDataMatching");
				modelAndView.setViewName("editRollingDataMatching");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				if (matchingRules.size() >= 1) {
					modelAndView.addObject("matchingRulesTrue", true);
				}
				modelAndView.addObject("matchingRules", matchingRules);
				modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				modelAndView.addObject("listRefFunctionsname", listRefFunctionsname);
				modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
				modelAndView.addObject("idRightData", listApplicationsData.getIdRightData());
				modelAndView.addObject("matchkeyformula", listApplicationsData.getKeyGroupRecordCountAnomaly());
				modelAndView.addObject("recordCount", listApplicationsData.getNumericalStatCheck());
				modelAndView.addObject("primaryKey", listApplicationsData.getStringStatCheck());
				modelAndView.addObject("targetSchemaId", listApplicationsData.getRollTargetSchemaId());
				modelAndView.addObject("rollType", listApplicationsData.getRollType());

			}else if (appType.equals("Data Forensics")) {
				System.out.println("dataApplicationCustomizeView");
				modelAndView.setViewName("dataApplicationCustomizeView");

				List<ListDataDefinition> ListDataDefinitiondata = validationcheckdao
						.getDataFromListDataDefinition(idData);
				System.out.println("ListDataDefinitiondata=" + ListDataDefinitiondata);
				System.out.println("getStringStat=" + ListDataDefinitiondata.get(0).getStringStat());
				System.out.println("StringThreshold" + ListDataDefinitiondata.get(0).getStringStatThreshold());
				modelAndView.addObject("listDataDefinitionData", ListDataDefinitiondata);

				Set keySet = map.keySet();
				Iterator keyIterator = keySet.iterator();
				int i = 0;
				while (keyIterator.hasNext()) {
					Object key = keyIterator.next();

					Collection values = (Collection) map.get((String) key);
					Iterator valuesIterator = values.iterator();
					while (valuesIterator.hasNext()) {
						System.out.print("Key: " + key + ", ");
						// System.out.print( "Value: " + valuesIterator.next() +
						// ".
						// " );

						if (i == 0) {
							System.out.println("allDupRow=" + key);
							modelAndView.addObject("allDupRow", key);
							modelAndView.addObject("allThreshold", valuesIterator.next());
							i++;
						} else {
							System.out.println("identityDupRow=" + key);
							modelAndView.addObject("identityDupRow", key);
							modelAndView.addObject("identityThreshold", valuesIterator.next());
						}
					}
					System.out.print("\n");
				}
				String timeSeries = validationcheckdao.getTimeSeriesForIdApp(idApp);
				if (!timeSeries.equals("None")) {
					String[] split = timeSeries.split(",");
					for (int i1 = 0; i1 < split.length; i1++) {
						System.out.println(split[i1]);
						if (split[i1].equalsIgnoreCase("month")) {
							System.out.println("month condition");
							modelAndView.addObject("month", split[i1]);
						}
						if (split[i1].equalsIgnoreCase("dayOfWeek")) {
							modelAndView.addObject("dayOfWeek", split[i1]);
						}
						if (split[i1].equalsIgnoreCase("hourOfDay")) {
							modelAndView.addObject("hourOfDay", split[i1]);
							System.out.println("hourOfDay");
						}
						if (split[i1].equalsIgnoreCase("dayOfMonth")) {
							modelAndView.addObject("dayOfMonth", split[i1]);
						}
					}
				} else {
					modelAndView.addObject("None", timeSeries);
				}

				System.out.println("listApplicationsData=" + listApplicationsData);
				modelAndView.addObject("listApplicationsData", listApplicationsData);
				// updatefrequency
				String updateFrequency = listApplicationsData.getUpdateFrequency();
				System.out.println("listApplicationsData.getUpdateFrequency();=" + updateFrequency);
				if (updateFrequency.equalsIgnoreCase("Daily") || updateFrequency.equalsIgnoreCase("Never")) {
					System.out.println("daily r never");
					modelAndView.addObject("updateFrequency", updateFrequency);
				} else {
					int frequencyDays = listApplicationsData.getFrequencyDays();
					System.out.println("frequencyDays=" + frequencyDays);
					modelAndView.addObject("updateFrequency", updateFrequency);
					modelAndView.addObject("frequencyDays", frequencyDays);
				}

				modelAndView.addObject("stringStatStatus", listApplicationsData.getStringStatCheck());
				modelAndView.addObject("nullCountStatus", listApplicationsData.getNonNullCheck());
				modelAndView.addObject("numericalStatsStatus", listApplicationsData.getNumericalStatCheck());
				modelAndView.addObject("recordAnomalyStatus", listApplicationsData.getRecordAnomalyCheck());
				modelAndView.addObject("dataDriftStatus", listApplicationsData.getDataDriftCheck());
				modelAndView.addObject("outofNormStatus", listApplicationsData.getOutOfNormCheck());
				modelAndView.addObject("recordCountAnomalyTypeStatus", listApplicationsData.getRecordCountAnomaly());
				modelAndView.addObject("recordCountAnomalyThresholdStatus",
						listApplicationsData.getRecordCountAnomalyThreshold());
				modelAndView.addObject("applyRulesStatus", listApplicationsData.getApplyRules());
				modelAndView.addObject("applyDerivedColumnsStatus", listApplicationsData.getApplyDerivedColumns());
				modelAndView.addObject("fileNameValStatus", listApplicationsData.getFileNameValidation());
				modelAndView.addObject("columnOrderValStatus", listApplicationsData.getColOrderValidation());
				modelAndView.addObject("entityColumn", listApplicationsData.getEntityColumn());
				modelAndView.addObject("incrementalMatching", listApplicationsData.getIncrementalMatching());
				modelAndView.addObject("buildHistoricFingerPrint", listApplicationsData.getBuildHistoricFingerPrint());
				modelAndView.addObject("defaultCheckStatus", listApplicationsData.getDefaultCheck());
				modelAndView.addObject("patternCheckStatus", listApplicationsData.getPatternCheck());
				modelAndView.addObject("badDataStatus", listApplicationsData.getBadData());

				// priyanka 25-12-2018

				modelAndView.addObject("lengthCheckStatus", listApplicationsData.getlengthCheck());
				
				modelAndView.addObject("maxLengthCheckStatus", listApplicationsData.getMaxLengthCheck());

				modelAndView.addObject("reprofilingStatus", listApplicationsData.getReprofiling());

				modelAndView.addObject("dateRuleChkStatus", listApplicationsData.getDateRuleChk());

				// date rule changes 8jan2019 priyanka

				modelAndView.addObject("timelinessKeyStatus", listApplicationsData.getTimelinessKeyChk());

				modelAndView.addObject("dGroupNullCheckStatus", listApplicationsData.getdGroupNullCheck());

				System.out.println("dGroupDateRuleCheckStatus****:" + listApplicationsData.getdGroupDateRuleCheck());
				modelAndView.addObject("dGroupDateRuleCheckStatus", listApplicationsData.getdGroupDateRuleCheck());
				modelAndView.addObject("defaultPatternCheckStatus", listApplicationsData.getDefaultPatternCheck());

				if (listApplicationsData.getHistoricStartDate() != null
						&& listApplicationsData.getHistoricEndDate() != null) {
					String[] StartDate = listApplicationsData.getHistoricStartDate().split(" ");
					String[] EndDate = listApplicationsData.getHistoricEndDate().split(" ");
					// System.out.println(split[0].length());
					modelAndView.addObject("historicStartDate", StartDate[0]);
					modelAndView.addObject("historicEndDate", EndDate[0]);
				} else {
					modelAndView.addObject("historicStartDate", listApplicationsData.getHistoricStartDate());
					modelAndView.addObject("historicEndDate", listApplicationsData.getHistoricEndDate());
				}

				modelAndView.addObject("historicDateFormat", listApplicationsData.getHistoricDateFormat());
				modelAndView.addObject("csvDir", listApplicationsData.getCsvDir());
				modelAndView.addObject("groupEquality", listApplicationsData.getGroupEquality());
				modelAndView.addObject("groupEqualityThreshold", listApplicationsData.getGroupEqualityThreshold());
				modelAndView.addObject("dGroupDataDriftCheckStatus", listApplicationsData.getdGroupDataDriftCheck());

				String dataLocation = validationcheckdao.getDataLocationFromListDataSources(idData);
				System.out.println("dataLocation=" + dataLocation);
				if (dataLocation.equalsIgnoreCase("FILESYSTEM")) {
					modelAndView.addObject("dataLocation", dataLocation);
				}
				String duplicateCheck = validationcheckdao.getDuplicateCheckFromListDFSetRule(idApp);
				modelAndView.addObject("duplicateCheck", duplicateCheck);


				/* Pradeep 8-Mar-2020 Global threshold changes: Debug log + pass object to JSP page	*/
				String sThresholdOptions = "[]";
				String sThresholdsApplyOption = "0";

				try {
					Map<String, JSONArray> oThresholdOptions = JwfSpaInfra.getAppListsOptionsMap(jdbcTemplate, "GLOBAL_THRESHOLDS_OPTION");

					ObjectMapper oMapper = new ObjectMapper();
					String sListApplication = oMapper.writeValueAsString(listApplicationsData);

					JSONArray aThresholdOptions = oThresholdOptions.get("GLOBAL_THRESHOLDS_OPTION");
					sThresholdOptions = aThresholdOptions.toString();
					sThresholdsApplyOption = Integer.toString(listApplicationsData.getThresholdsApplyOption());

					DateUtility.DebugLog("customizeValidation 01",
								String.format("thresholdsApplyOption = %1$s \n full retrieved data from DB = %2$s \n List Options = %3$s",
									sThresholdsApplyOption, sListApplication, sThresholdOptions));
				} catch (Exception oException) {
					oException.printStackTrace();
				}
				modelAndView.addObject("ThresholdOptions", sThresholdOptions);
				modelAndView.addObject("ThresholdsApplyOption", sThresholdsApplyOption);

			} else if (appType.equals("Statistical Matching")) {

				modelAndView.setViewName("statisticalMatchingCustomizeView");
				System.out.println("statisticalMatchingCustomizeView");

				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

				modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				modelAndView.addObject("listRefFunctionsname", listRefFunctionsname);
				modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("idData", idData);
				modelAndView.addObject("applicationName", applicationName);
				modelAndView.addObject("name", name);
				long secondSourceIdData = validationcheckdao.getNamefromlistDataSources(idApp);
				modelAndView.addObject("secondSourceIdData", secondSourceIdData);
				System.out.println("secondSourceIdData=" + secondSourceIdData);
				listStatisticalMatchingConfig listStatisticalMatchingData = validationcheckdao
						.getDataFromlistStatisticalMatchingConfig(idApp);
				System.out.println(
						"listStatisticalMatchingData=" + listStatisticalMatchingData.getMeasurementMeanThreshold());
				String expression = listStatisticalMatchingData.getLeftSideExp() + "="
						+ listStatisticalMatchingData.getRightSideExp();
				modelAndView.addObject("expression", expression);
				modelAndView.addObject("leftSideExp", listStatisticalMatchingData.getLeftSideExp());
				modelAndView.addObject("rightSideExp", listStatisticalMatchingData.getRightSideExp());

				modelAndView.addObject("RCType", listStatisticalMatchingData.getRecordCountType());
				modelAndView.addObject("RCThreshold", listStatisticalMatchingData.getRecordCountThreshold());
				modelAndView.addObject("MSum", listStatisticalMatchingData.getMeasurementSum());
				modelAndView.addObject("MSType", listStatisticalMatchingData.getMeasurementSumType());
				modelAndView.addObject("MSThreshold", listStatisticalMatchingData.getMeasurementSumThreshold());
				modelAndView.addObject("RCthreshold", listStatisticalMatchingData.getRecordCountThreshold());
				modelAndView.addObject("MMean", listStatisticalMatchingData.getMeasurementMean());
				modelAndView.addObject("MMType", listStatisticalMatchingData.getMeasurementMeanType());
				modelAndView.addObject("MMThreshold", listStatisticalMatchingData.getMeasurementMeanThreshold());
				modelAndView.addObject("MSD", listStatisticalMatchingData.getMeasurementStdDev());
				modelAndView.addObject("MSDType", listStatisticalMatchingData.getMeasurementStdDevType());
				modelAndView.addObject("MSDThreshold", listStatisticalMatchingData.getMeasurementStdDevThreshold());
				modelAndView.addObject("GroupBy", listStatisticalMatchingData.getGroupBy());
			} else if (appType.equals("Data Matching Group")) {
				modelAndView.setViewName("DataMatchingGroupCreateView");
				//Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("DataMatchingGroupCustomize", "DataMatchingGroupCustomize");
				modelAndView.addObject("idRightData", listApplicationsData.getIdRightData());
				modelAndView.addObject("matchingThreshold", listApplicationsData.getMatchingThreshold());

			} else if (appType.equals("Model Governance")) {
				modelAndView.setViewName("modelGovernanceCustomizeView");
				listModelGovernance listModelGovernanceObject = validationcheckdao
						.getDataFromListModelGovernance(idApp);
				System.out.println("listModelGovernanceObject.getModelGovernanceType()="
						+ listModelGovernanceObject.getModelGovernanceType());
				if (listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Decile Equality")
						|| listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Decile Consistency")) {
					modelAndView.addObject("modelGovernanceType", listModelGovernanceObject.getModelGovernanceType());
					modelAndView.addObject("modelIdCol", listModelGovernanceObject.getModelIdCol());
					modelAndView.addObject("decileCol", listModelGovernanceObject.getDecileCol());
					modelAndView.addObject("expectedPercentage", listModelGovernanceObject.getExpectedPercentage());
					modelAndView.addObject("thresholdPercentage", listModelGovernanceObject.getThresholdPercentage());
					modelAndView.addObject("incrementalMatching", listApplicationsData.getIncrementalMatching());
					modelAndView.addObject("buildHistoricFingerPrint",
							listApplicationsData.getBuildHistoricFingerPrint());
					modelAndView.addObject("updateFrequency", listApplicationsData.getUpdateFrequency());
					modelAndView.addObject("frequencyDays", listApplicationsData.getFrequencyDays());
					modelAndView.addObject("historicDateFormat", listApplicationsData.getHistoricDateFormat());
					if (listApplicationsData.getHistoricStartDate() != null
							&& listApplicationsData.getHistoricEndDate() != null) {
						String[] StartDate = listApplicationsData.getHistoricStartDate().split(" ");
						String[] EndDate = listApplicationsData.getHistoricEndDate().split(" ");
						// System.out.println(split[0].length());
						modelAndView.addObject("historicStartDate", StartDate[0]);
						modelAndView.addObject("historicEndDate", EndDate[0]);
					} else {
						modelAndView.addObject("historicStartDate", listApplicationsData.getHistoricStartDate());
						modelAndView.addObject("historicEndDate", listApplicationsData.getHistoricEndDate());
					}
					String timeSeries = listApplicationsData.getTimeSeries();
					if (!timeSeries.equals("None")) {
						String[] split = timeSeries.split(",");
						for (int i1 = 0; i1 < split.length; i1++) {
							System.out.println(split[i1]);
							if (split[i1].equalsIgnoreCase("month")) {
								System.out.println("month condition");
								modelAndView.addObject("month", split[i1]);
							}
							if (split[i1].equalsIgnoreCase("dayOfWeek")) {
								modelAndView.addObject("dayOfWeek", split[i1]);
							}
							if (split[i1].equalsIgnoreCase("hourOfDay")) {
								modelAndView.addObject("hourOfDay", split[i1]);
								System.out.println("hourOfDay");
							}
							if (split[i1].equalsIgnoreCase("dayOfMonth")) {
								modelAndView.addObject("dayOfMonth", split[i1]);
							}
						}
					} else {
						modelAndView.addObject("None", timeSeries);
					}
					List listDataDefinitionColumnNames = validationcheckdao
							.getDisplayNamesFromListDataDefinition(idData);
					modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				} else if (listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Score Consistency")) {
					modelAndView.addObject("modelGovernanceType", listModelGovernanceObject.getModelGovernanceType());
					modelAndView.addObject("leftSourceSliceStart", listModelGovernanceObject.getLeftSourceSliceStart());
					modelAndView.addObject("leftSourceSliceEnd", listModelGovernanceObject.getLeftSourceSliceEnd());
					modelAndView.addObject("rightSourceSliceStart",
							listModelGovernanceObject.getRightSourceSliceStart());
					modelAndView.addObject("rightSourceSliceEnd", listModelGovernanceObject.getRightSourceSliceEnd());
					modelAndView.addObject("matchingExpression", listModelGovernanceObject.getMatchingExpression());
					modelAndView.addObject("measurementExpression",
							listModelGovernanceObject.getMeasurementExpression());
					modelAndView.addObject("threshold", listModelGovernanceObject.getThresholdPercentage());
					modelAndView.addObject("modelIdCol", listModelGovernanceObject.getModelIdCol());
					modelAndView.addObject("decileCol", listModelGovernanceObject.getDecileCol());
					List listDataDefinitionColumnNames = validationcheckdao
							.getDisplayNamesFromListDataDefinition(idData);
					modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
					modelAndView.addObject("sourcedateformat", listApplicationsData.getHistoricDateFormat());
				}
			} else if (appType.equals("Model Governance Dashboard")) {
				modelAndView.setViewName("modelGovernanceDashboardCreateView");
				String mgDashboard = listApplicationsData.getCsvDir();
				String[] split = mgDashboard.split("-");
				/*
				 * for (int i = 0; i < split.length; i++) {
				 * System.out.println("id="+split[i]); }
				 */
				modelAndView.addObject("decileEquality", split[0]);
				modelAndView.addObject("decileConsistency", split[1]);
				modelAndView.addObject("scoreConsistency", split[2]);
				modelAndView.addObject("mgCustomizeView", "mgCustomizeView");
				Map<Long, String> decileEqualityAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Equality");
				Map<Long, String> decileConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Consistency");
				Map<Long, String> scoreConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Score Consistency");
				modelAndView.addObject("decileEqualityAppType", decileEqualityAppType);
				modelAndView.addObject("decileConsistencyAppType", decileConsistencyAppType);
				modelAndView.addObject("scoreConsistencyAppType", scoreConsistencyAppType);
			} else if (appType.equals("File Management")) {
				validationcheckdao.deleteFileManagementFromListFMRules(idApp);
				modelAndView.setViewName("fileManagementCreate");
				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("idData", idData);
				modelAndView.addObject("apptype", appType);
				modelAndView.addObject("applicationName", applicationName);
				// model.addObject("description", description);
				modelAndView.addObject("name", name);

			} else if (appType.equals("File Monitoring")) { // changes for File Monitoring
				// validationcheckdao.deleteFileManagementFromListFMRules(idApp);

				System.out.println("In File Monitoring..................");

				String fileMonitorType= fileMonitorDao.getFileMonitorTypeByIdApp(idApp);

				if(fileMonitorType.equalsIgnoreCase("snowflake") || fileMonitorType.trim().equalsIgnoreCase("azuredatalakestoragegen2batch") || fileMonitorType.equalsIgnoreCase("aws s3")){

					// Check if there is data in staging table
					Long stagingDataCount = validationcheckdao.getDbkFileMonitorRulesCountInStaging(idApp);
					
					// Get connection details from fm_connection_details table
					long connectionId = schemaDao.getConnectionIdForFMValidation(idApp);
					
					modelAndView.addObject("connectionId", connectionId);
					
					if(stagingDataCount > 0l){
						modelAndView.setViewName("fileMonitoringApproval");
						
						List<DBKFileMonitoringRules> stagingFMRulesList = validationcheckdao.getStagingDbkFileMonitorRules(idApp);
						modelAndView.addObject("dbkFileMonitoringRules", stagingFMRulesList);
					} else {
	
						modelAndView.setViewName("dbkFileMonitoringEdit");
	
						List<DBKFileMonitoringRules> fileMonitoringRulesList= fileMonitorDao.getDBKFileMonitorDetailsByIdApp(idApp);
	
						modelAndView.addObject("dbkFileMonitoringRules", fileMonitoringRulesList);
					}

				}else{

					// int idApp1=0;
					List<FileMonitorRules> arrListFileMonitorRule = fileMonitorDao
							.getAllFileMonitorRuleDetailsByIdApp(idApp);

					System.out.println("arrListFileMonitorRule =>" + arrListFileMonitorRule);

					modelAndView.setViewName("editFileMonitoring");

					modelAndView.addObject("arrListFileMonitorRule", arrListFileMonitorRule);
				}
				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("apptype", appType);
				modelAndView.addObject("applicationName", applicationName);
				// model.addObject("description", description);
				modelAndView.addObject("name", name);

				System.out.println("File Monitoring -------- idApp ->" + idApp);
				System.out.println("File Monitoring -------- apptype -> " + appType);
				System.out.println("File Monitoring -------- applicationName->" + applicationName);
				System.out.println("File Monitoring -name------- name->" + name);


			} else if(appType.equals("Primary Key Matching")){
				modelAndView.setViewName("primaryKeyMatchingCustomizeView");
                //Long projectId= (Long)session.getAttribute("projectId");
				modelAndView.addObject("applicationName", applicationName);
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List<ListDmCriteria> getMatchingRules=validationcheckdao.getMatchingRules(idApp);
				ObjectMapper mapper = new ObjectMapper();
				String jsongetMatchingRules="";
				try {
					jsongetMatchingRules=mapper.writeValueAsString(getMatchingRules);
				} catch (Exception e) {
					e.printStackTrace();
				}
				modelAndView.addObject("jsongetMatchingRules", jsongetMatchingRules);
				long secondSourceIdData = validationcheckdao.getNamefromlistDataSources(idApp);
				modelAndView.addObject("secondSourceIdData", secondSourceIdData);
				String secondSourceName = validationcheckdao.getNameFromListDataSources(secondSourceIdData);
				modelAndView.addObject("secondSourceName", secondSourceName);
				modelAndView.addObject("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				modelAndView.addObject("listRefFunctionsname", listRefFunctionsname);
				modelAndView.addObject("getlistdatasourcesname", getlistdatasourcesname);
				modelAndView.addObject("idApp", idApp);

			}

			// ListApplications listapplications =
			// validationcheckdao.getdatafromlistapplications(idApp);
			// System.out.println(listapplications.getGarbageRows());
			// ModelAndView modelAndView=new
			// ModelAndView("dataApplicationCustomizeView");
			// ModelAndView modelAndView=new
			// ModelAndView("matchTypeCreateView");
			// modelAndView.addObject("getdatafromlistdftranrule",getdatafromlistdftranrule);
			System.out.println("After if condition");
			modelAndView.addObject("idApp", idApp);
			modelAndView.addObject("idData", idData);
			modelAndView.addObject("apptype", appType);
			modelAndView.addObject("name", name);
			modelAndView.addObject("description", listApplicationsData.getDescription());
			modelAndView.addObject("listappName", name);
			modelAndView.addObject("applicationName", applicationName);
			
			// Data_Domain
			modelAndView.addObject("selectedDataDomain", data_domain);
			modelAndView.addObject("lstDataDomain",lstDataDomain);
			
			System.out.println("idApp ::" + idApp);
			System.out.println("idData ::" + idData);
			System.out.println("apptype ::" + appType);
			System.out.println("name ::" + name);
			System.out.println("listappName ::" + name);
			System.out.println("applicationName :: " + applicationName);
			// modelAndView.addObject("listapplications",listapplications);
			modelAndView.addObject("idApp", idApp);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteMatchingRule", method = RequestMethod.POST)
	public void deleteIdListColRulesData(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam long idDm) {
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
		System.out.println("idDm=" + idDm);
		long idDMCriteria = idDm;
		
		// Get details of DM criteria
		ListDmCriteria listDMCriteria = validationcheckdao.getlistDMCriteriaDetailsByID(idDMCriteria);
		
		String leftColumn = "";
		String rightColumn = "";
		Long leftTemplateId = null;
		Integer rightTemplateId = null;
		Long idDM = null;
		Long validationId = null;
		String matchType = "";
		
		// For Measurement Match get the left or right columns
		if(listDMCriteria != null) {
			idDM = listDMCriteria.getIdDm();
			rightColumn = listDMCriteria.getRightSideExp();
			leftColumn = listDMCriteria.getLeftSideExp();
		}
		 System.out.println("idDM: "+idDM);
		 System.out.println("leftColumn: "+leftColumn);
		 System.out.println("rightColumn: "+rightColumn);
		 
		// Get DM rule details
		 Map<String,Object> dmDetails = validationcheckdao.getListDMRulesByIdDM(idDM);
		 if(dmDetails != null && dmDetails.size()>0) {
			 if(dmDetails.get("idApp") != null) {
				 validationId = Long.parseLong(dmDetails.get("idApp").toString());
			 }
			
			 if(dmDetails.get("matchType2") != null) {
				 matchType = dmDetails.get("matchType2").toString();
			 }
		 }
		 System.out.println("validationId: "+validationId);
		 System.out.println("matchType: "+matchType);

		// Get the validation details and template details
		 if(validationId != null) {
			 ListApplications listApplications= validationcheckdao.getdatafromlistapplications(validationId);
			 leftTemplateId = listApplications.getIdData();
			 rightTemplateId = listApplications.getIdRightData();
		 }
		 System.out.println("leftTemplateId: "+leftTemplateId);
		 System.out.println("rightTemplateId: "+rightTemplateId); 

		// Deleting DMRules 
		int result=validationcheckdao.deleteEntryFromListDMRulesWithIdDm1(idDMCriteria);
						
		if(result>0) {
			
			// For Measurement Match Disable and Approve Measurement fields in Template Metadata
			if (matchType != null && matchType.trim().equalsIgnoreCase("Measurements Match") && leftTemplateId != null
					&& rightTemplateId != null && rightColumn != null && leftColumn != null) {
				
				// Disable in Left Template
				templateviewdao.updateCheckValueIntoListDatadefinition(leftTemplateId, "measurement", leftColumn.trim(), "N");
				templateviewdao.updateCheckValueIntoStagingListDatadefinition(leftTemplateId, "measurement", leftColumn.trim(), "N");
				
				// Disable in right Template
				templateviewdao.updateCheckValueIntoListDatadefinition(rightTemplateId, "measurement", rightColumn.trim(), "N");
				templateviewdao.updateCheckValueIntoStagingListDatadefinition(rightTemplateId, "measurement", rightColumn.trim(), "N");

			}
		}
		try {
			response.getWriter().println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/duplicateValidationCheckName", method = RequestMethod.POST)
	public void duplicateValidationCheckName(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String validationCheckName = req.getParameter("val");
		
		//Eliminates spaces and special characters - by mamta
		  
		  if (validationCheckName == null || validationCheckName.trim().isEmpty()) {
		       
			  try {
					res.getWriter().println("Please Enter Validation Check Name");
					return;
				
				} catch (IOException e) {
					e.printStackTrace();
				} 
		     }
		/*  Pattern pattern = Pattern.compile("[^A-Za-z0-9_]");
	      Matcher match = pattern.matcher(validationCheckName);
	      boolean val = match.find();
	      if (val == true)
	      {
				try {
					res.getWriter().println("Please Enter Validation Check Name without spaces and special characters");
					return;
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}*/
		  Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");//"[^A-Za-z0-9_]"
	      Matcher match = pattern.matcher(validationCheckName);
	      boolean val = match.find();
	      System.out.println("val"+val);
	      if (val == false)
	      {
				try {
					res.getWriter().println("Name must begin with a letter and cannot contain spaces,special characters");
					return;
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
		  }else {
			  Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_]*$");
			  Matcher match2 = pattern2.matcher(validationCheckName);
		      boolean val2 = match2.find();
		      if (val2 == false)
		      {
					try {
						res.getWriter().println("Name must begin with a letter and cannot contain spaces,special characters");
						return;
						
					} catch (IOException e) {
						e.printStackTrace();
					} 
			  }
		      
		  }
		//Eliminates only spaces
		//if(validationCheckName.matches(".*\\s.*"))
		//{
			//try {
				//res.getWriter().println("Please Enter Validation Check Name without spaces.");
			//} catch (IOException e) {
				//e.printStackTrace();
			//} 
		//}
		
		//System.out.println("validationCheckName=" + validationCheckName);
		String name = validationcheckdao.duplicateValidationCheckName(validationCheckName);
		//System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter().println("This Validation Check name is in use. Please choose another name.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/fileManagementSave", method = RequestMethod.POST)
	public void fileManagementSave(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idApp, @RequestParam String dupCheck) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("idApp=" + idApp);
		System.out.println("dupCheck=" + dupCheck);
		int insertIntoListFmRules = validationcheckdao.insertIntoListFmRules(idApp, dupCheck);
		System.out.println("insertIntoListFmRules=" + insertIntoListFmRules);
		if (insertIntoListFmRules > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "File Management Rule created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "Sorry, There was a Problem");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/saveDataIntolistModelGovernance", method = RequestMethod.POST)
	public void saveDataIntolistModelGovernance(HttpServletResponse response, HttpSession session,
			@RequestParam Long idApp, @RequestParam String modelGovernanceType, @RequestParam String modelIdCol,
			@RequestParam String decileCol, @RequestParam double expectedPercentage,
			@RequestParam double thresholdPercentage, @RequestParam String buildHistoric,
			@RequestParam String incrementalType, @RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String dateFormat, @RequestParam String timeSeries, @RequestParam String updateFrequency,
			@RequestParam int frequencyDays) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + idApp);
		System.out.println("modelGovernanceType=" + modelGovernanceType);
		System.out.println("modelIdCol=" + modelIdCol);
		System.out.println("decileCol=" + decileCol);
		System.out.println("startDate=" + startDate);
		System.out.println("endDate=" + endDate);
		ListApplications la = new ListApplications();
		la.setIdApp(idApp);
		la.setBuildHistoricFingerPrint(buildHistoric);
		la.setIncrementalMatching(incrementalType);
		la.setHistoricStartDate(startDate);
		la.setHistoricEndDate(endDate);
		la.setHistoricDateFormat(dateFormat);
		la.setTimeSeries(timeSeries);
		la.setUpdateFrequency(updateFrequency);
		la.setFrequencyDays(frequencyDays);

		int update = validationcheckdao.insertintolistModelGovernance(idApp, modelGovernanceType, modelIdCol, decileCol,
				expectedPercentage, thresholdPercentage);
		validationcheckdao.updateIntoListApplicationForModelGovernance(la);
		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/updateDataIntolistModelGovernance", method = RequestMethod.POST)
	public void updateDataIntolistModelGovernance(HttpServletResponse response, HttpSession session,
			@RequestParam Long idApp, @RequestParam String modelGovernanceType, @RequestParam String modelIdCol,
			@RequestParam String decileCol, @RequestParam double expectedPercentage,
			@RequestParam double thresholdPercentage, @RequestParam String buildHistoric,
			@RequestParam String incrementalType, @RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String dateFormat, @RequestParam String timeSeries, @RequestParam String updateFrequency,
			@RequestParam int frequencyDays) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + idApp);
		System.out.println("modelGovernanceType=" + modelGovernanceType);
		System.out.println("modelIdCol=" + modelIdCol);
		System.out.println("decileCol=" + decileCol);
		System.out.println("expectedPercentage=" + expectedPercentage);
		System.out.println("thresholdPercentage=" + thresholdPercentage);
		ListApplications la = new ListApplications();
		la.setIdApp(idApp);
		la.setBuildHistoricFingerPrint(buildHistoric);
		la.setIncrementalMatching(incrementalType);
		la.setHistoricStartDate(startDate);
		la.setHistoricEndDate(endDate);
		la.setHistoricDateFormat(dateFormat);
		la.setTimeSeries(timeSeries);
		la.setUpdateFrequency(updateFrequency);
		la.setFrequencyDays(frequencyDays);

		int update = validationcheckdao.updateintolistModelGovernance(idApp, modelGovernanceType, modelIdCol, decileCol,
				expectedPercentage, thresholdPercentage);
		System.out.println(update);
		validationcheckdao.updateIntoListApplicationForModelGovernance(la);
		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/saveDataIntolistModelGovernanceForScoreConsistency", method = RequestMethod.POST)
	public void saveDataIntolistModelGovernanceForScoreConsistency(HttpServletResponse response, HttpSession session,
			@RequestParam Long idApp, @RequestParam String modelGovernanceType,
			@RequestParam String leftSourceSliceStart, @RequestParam String leftSourceSliceEnd,
			@RequestParam String rightSourceSliceStart, @RequestParam String rightSourceSliceEnd,
			@RequestParam String matchingExpression, @RequestParam String measurementExpression,
			@RequestParam double scThreshold, @RequestParam String sourceDateFormat, @RequestParam String modelIdCol,
			@RequestParam String decileCol) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + idApp);
		System.out.println("modelGovernanceType=" + modelGovernanceType);
		System.out.println("leftSourceSliceStart=" + leftSourceSliceStart);
		System.out.println("leftSourceSliceEnd=" + leftSourceSliceEnd);
		System.out.println("rightSourceSliceStart=" + rightSourceSliceStart);
		System.out.println("rightSourceSliceEnd=" + rightSourceSliceEnd);
		ListApplications la = new ListApplications();
		la.setIdApp(idApp);

		int update = validationcheckdao.insertintolistModelGovernanceForScoreConsistency(idApp, modelGovernanceType,
				leftSourceSliceStart, leftSourceSliceEnd, rightSourceSliceStart, rightSourceSliceEnd,
				matchingExpression, measurementExpression, scThreshold, sourceDateFormat, modelIdCol, decileCol);

		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/updateDataIntolistModelGovernanceForScoreConsistency", method = RequestMethod.POST)
	public void updateDataIntolistModelGovernanceForScoreConsistency(HttpServletResponse response, HttpSession session,
			@RequestParam Long idApp, @RequestParam String modelGovernanceType,
			@RequestParam String leftSourceSliceStart, @RequestParam String leftSourceSliceEnd,
			@RequestParam String rightSourceSliceStart, @RequestParam String rightSourceSliceEnd,
			@RequestParam String matchingExpression, @RequestParam String measurementExpression,
			@RequestParam double scThreshold, @RequestParam String sourceDateFormat, @RequestParam String modelIdCol,
			@RequestParam String decileCol) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + idApp);
		System.out.println("modelGovernanceType=" + modelGovernanceType);
		System.out.println("leftSourceSliceStart=" + leftSourceSliceStart);
		System.out.println("leftSourceSliceEnd=" + leftSourceSliceEnd);
		System.out.println("rightSourceSliceStart=" + rightSourceSliceStart);
		System.out.println("rightSourceSliceEnd=" + rightSourceSliceEnd);
		ListApplications la = new ListApplications();
		la.setIdApp(idApp);

		int update = validationcheckdao.updateintolistModelGovernanceForScoreConsistency(idApp, modelGovernanceType,
				leftSourceSliceStart, leftSourceSliceEnd, rightSourceSliceStart, rightSourceSliceEnd,
				matchingExpression, measurementExpression, scThreshold, sourceDateFormat, modelIdCol, decileCol);
		System.out.println("update=" + update);
		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/saveDataIntolistModelGovernanceForModelGovernanceDashboard", method = RequestMethod.POST)
	public void saveDataIntolistModelGovernanceForModelGovernanceDashboard(HttpServletResponse response,
			HttpSession session, @RequestParam Long idApp, @RequestParam String modelGovernanceDashboard) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("leftSourceId="+leftSourceId);
		System.out.println("idApp=" + idApp);
		// System.out.println("modelGovernanceType=" + modelGovernanceType);
		System.out.println("modelGovernanceDashboard=" + modelGovernanceDashboard);

		int update = validationcheckdao.updateModelGovernanceDashboardIntoListApplications(modelGovernanceDashboard,
				idApp);
		System.out.println("update=" + update);
		if (update > 0) {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Match Key created successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("fail", "No Matching key found");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/checkSchemaType", method = RequestMethod.POST)
	public void checkSchemaType(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					res.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String idDataSchema = req.getParameter("idDataSchema");
			System.out.println("idDataSchema=" + idDataSchema);
			String schemaType = validationcheckdao.getSchemaTypeFromListDataSchema(idDataSchema);
			System.out.println("schemaType:" + schemaType);
			if (schemaType.equalsIgnoreCase("Oracle RAC") || schemaType.equalsIgnoreCase("Oracle")
					|| schemaType.equalsIgnoreCase("Hive Kerberos") || schemaType.equalsIgnoreCase("Postgres")  || schemaType.equalsIgnoreCase("Teradata")) {
				// res.getWriter().println("");
			} else {
				res.getWriter().println("Please select only hive-kerberos or oracle type schema or PostgreSQL or Teradata");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get template name
	@RequestMapping(value = "/templatename", method = RequestMethod.GET)
	public @ResponseBody List<String> templatename(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		Long projectId= (Long)session.getAttribute("projectId");
		System.out.println("=== projectId ==>"+projectId);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<String> tamplateListFrom = validationcheckdao.getTemplateListDataSource(projectId);
		// List<String>str= taplateListFrom.toString();
		// String str=tamplateListFrom.toString().replace("[","").replace("]","");
		List<String> list1 = new ArrayList<>();
		for (String list : tamplateListFrom) {
			String str = list.toString().replace("[", "").replace("]", "");
			list1.add(str);
		}

		// System.out.println("::: "+str);
		//System.out.println("::!@@@@@@ " + list1);
		//System.out.println(" ToString ::::::::: " + tamplateListFrom);
		return list1;

	}

	// get non derived template name
	@RequestMapping(value = "/getNonDerivedtemplatename", method = RequestMethod.GET)
	public @ResponseBody List<String> getNonDerivedtemplatename(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		Long projectId = (Long) session.getAttribute("projectId");
		System.out.println("=== projectId ==>" + projectId);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<String> templateListFrom = validationcheckdao.getNonDerivedTemplateList(projectId);
		List<String> templateNameList = new ArrayList<>();
		if (templateListFrom != null && templateListFrom.size() > 0) {
			for (String list : templateListFrom) {
				String str = list.toString().replace("[", "").replace("]", "");
				templateNameList.add(str);
			}
		}
		return templateNameList;
	}

	@RequestMapping(value = "/createBatchValidation", method = RequestMethod.POST, produces = "application/json")
	public void createBatchValidation(
				HttpSession oSession,
				HttpServletResponse oResponse,
				@RequestParam("configFile") MultipartFile oConfigFile,
				@RequestParam("type") String sType,
				@RequestParam("qualityValidation") String sValidation
			) throws IOException {

		Object oUser = oSession.getAttribute("user");
		boolean lAccessRights = rbacController.rbac("Validation Check", "C", oSession);
		JSONObject oJsonResponse = new JSONObject();

		System.out.println("==============batchValidation");

		System.out.println("Path ======>" + oConfigFile.getName());
		System.out.println("type ======>" + sType);
		System.out.println("isQuality ======>" + sValidation);

		String databuckHome = "/opt/databuck";
		if (System.getenv("DATABUCK_HOME") != null) {
			databuckHome = System.getenv("DATABUCK_HOME");
		} else if (System.getProperty("DATABUCK_HOME") != null) {
			databuckHome = System.getProperty("DATABUCK_HOME");
		}
		System.out.println("========= databuckHome ========"+databuckHome);

		if ( (oUser == null) || (!oUser.equals("validUser")) || (!lAccessRights) ) {
			oResponse.sendRedirect("./databuck");
		}

		DebugLog("Got parameters from UI: ", String.format("[configFile: %1$s/%2$s] [type: %3$s] [qualityValidation: %4$s]",
			oConfigFile.getOriginalFilename(), oConfigFile.getSize(), sType, sValidation));
		if (oConfigFile.isEmpty()) {
			DebugLog("File not uploaded to server:", "File not selected or selected file is empty");
			oJsonResponse.put("status", "-1");
			oJsonResponse.put("msg", "File not selected or selected file is empty");
		} else {
			byte[] aBytes = oConfigFile.getBytes();
			File oServerFile = new File(databuckHome + "//csvFiles1//"+ oConfigFile.getOriginalFilename());
			BufferedOutputStream oOutputStream = new BufferedOutputStream(new FileOutputStream(oServerFile));
			oOutputStream.write(aBytes);
			oOutputStream.close();

			DebugLog("File uploaded to server:", String.format("Filename '%1$s' path '%2$s'", oConfigFile.getOriginalFilename(), databuckHome));
			oJsonResponse.put("status", "1");
			oJsonResponse.put("msg", String.format("File '%1$s' Successfully uploaded to server",  oConfigFile.getOriginalFilename()));
		}

		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();

		String scriptLocation = "";

		// calling jar file..........

		Process process = null;

		scriptLocation = databuckHome + "/scripts/batchValidation.sh";

		String serverCsvLocation = databuckHome + "//csvFiles1//" + oConfigFile.getOriginalFilename() ;
		System.out.println("csvLocation ==>"+serverCsvLocation);

		System.out.println("**** script location: " + scriptLocation);

		// path => server file path
		String cmd = scriptLocation + "  " + serverCsvLocation + "  " + sType + "  " + sValidation;

		System.out.println("**** Command : " + cmd);

		try {
			process = Runtime.getRuntime().exec(cmd);
		} catch (Exception e1) {
			System.out.println("\n====>Exception occurred when triggering script !!!");
			e1.printStackTrace();

		}

	}


/*	@RequestMapping(value = "/createBatchValidation")
	public ModelAndView createBatchValidation(ModelAndView modelAndView, HttpSession session, @RequestParam File path,
			@RequestParam String type, @RequestParam String isQuality) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("Amar ravi");
		System.out.println("path:" + path);
		System.out.println("type:" + type);
		System.out.println("isQuality:" + isQuality);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {
			System.out.println("batchValidation");

			System.out.println("Path ======>" + path.getPath());
			System.out.println("type ======>" + type);
			System.out.println("isQuality ======>" + isQuality);

			// calling jar file..........

			Process process = null;

			String databuckHome = "/opt/databuck";
			if (System.getenv("DATABUCK_HOME") != null) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			System.out.println("========= databuckHome ========"+databuckHome);

			try {
		//	File dir = new File("C:/Pravin_Godhane/Priyanka/csvFiles1/");
		//		System.out.println("Dir path =>"+dir);

				//C:\Pravin_Godhane\csvFiles1
				File file1 = new File(databuckHome + "//csvFiles1//"+path.getName());
				System.out.println("======File1 path =>"+file1);

				if(!file1.exists()) {
				//	dir.mkdir();
					file1.createNewFile();
					System.out.println("File,Folder created");
				}

			}catch(Exception e) {
				e.printStackTrace();
			}



			//for csvFile location
			String serverCsvLocation = databuckHome + "//csvFiles1//" + path.getName() ;
			System.out.println("csvLocation ==>"+serverCsvLocation);

//			String csvFile = "E:/CISCO/csvs/DMatchingConfig.csv";
			String csvFile = path.getPath();
			System.out.println("CSVFile path for reading csv:============>"+csvFile);

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			try{

			// String filePath = "E:/CISCO/csvs/AMAR-Demo.csv";
			String filePath = serverCsvLocation;

			 System.out.println("filePath for writting on server ============>"+filePath);

			 File file = new File(filePath);
			 FileWriter outputfile = new FileWriter(file);
			 CSVWriter writer = new CSVWriter(outputfile, ',',
	                 CSVWriter.NO_QUOTE_CHARACTER,
	                 CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                 CSVWriter.DEFAULT_LINE_END);
			try {
				br = new BufferedReader(new FileReader(csvFile));
				while ((line = br.readLine()) != null) {

					// use comma as separator
					String[] country = line.split(cvsSplitBy);
					String x = line.toString();
					writer.writeNext(country);

				}
			} catch (Exception E) {
				E.printStackTrace();
			}
			writer.close();
			}
			catch(Exception E){
				E.printStackTrace();
			}


			String scriptLocation = "";

			scriptLocation = databuckHome + "/scripts/batchValidation.sh";

			System.out.println("**** script location: " + scriptLocation);

			// path => server file path
			String cmd = scriptLocation + "  " + serverCsvLocation + "  " + type + "  " + isQuality;

			System.out.println("**** Command : " + cmd);

			try {
				process = Runtime.getRuntime().exec(cmd);
			} catch (Exception e1) {
				System.out.println("\n====>Exception occurred when triggering script !!!");
				e1.printStackTrace();

			}

			// --------------------------

			return modelAndView;
		} else
			return new ModelAndView("loginPage");

	}*/

	@RequestMapping(value = "/batchValidation")
	public ModelAndView batchValidation(ModelAndView modelAndView, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "C", session);
		if (rbac) {
			System.out.println("batchValidation");
			modelAndView.setViewName("batchValidation");

			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add Batch Validation");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	// get second source template names for matching [/getlistdatasourcesname]
	@RequestMapping(value = "/secondSourceTemplateName", method = RequestMethod.GET)
	public @ResponseBody List<String> secondSourceTemplateName(ModelAndView model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Long projectId = (Long) session.getAttribute("projectId");
		System.out.println("@@@@@@@@@@@@@ProjectID=>" + projectId);

		// List<ListDataSource> getlistdatasourcesname =
		// templateviewdao.getlistdatasourcesname(projectId);
		// List<String>

		// List<Object> reqs = (List<Object>) responseObjinside3.values();
		List<String> lstSecondSrcTemp = templateviewdao.getSecondSourceTemplateNames(projectId);



		List<String> list1 = new ArrayList<>();
		for (String list : lstSecondSrcTemp) {
			String str = list.toString().replace("[", "").replace("]", "");
			;
			list1.add(str);
		}

		//System.out.println("lstSecondSrcTemp ::!@@@@@@ " + list1);
		//System.out.println("lstSecondSrcTemp ToString ::::::::: " + lstSecondSrcTemp);
		return list1;

	}

	/* Pradeep 2-April-2021 below method is fully rewritten version of method below */
	@RequestMapping(value = "/validationCheckName", method = RequestMethod.GET)
	public @ResponseBody List<String> validationCheckName(ModelAndView model, HttpSession oSession,
			HttpServletRequest oRequest, HttpServletResponse oResponse) throws IOException, URISyntaxException {

		Object oUser = oSession.getAttribute("user");
		boolean lValidUser = ((oUser == null) || (!oUser.equals("validUser"))) ? false : true;

		Long nProjectId = 0l;
		List<Project> aProjectList = null;
		String sListProjectIds = "";
		List<ListApplications> aListApplications = null;

		boolean lIsRuleCatalogUsed = false;
		String sApplicationListQry = "";
		List<String> aListAppIdsAndNames = new ArrayList<>();
		String sRunnableStatusRowIds = "";		

		if (!lValidUser) {
			oResponse.sendRedirect("loginPage.jsp");

		} else {
			lIsRuleCatalogUsed = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N").equalsIgnoreCase("Y") ? true : false;
			
			nProjectId = (Long)oSession.getAttribute("projectId");
			aProjectList = (List<Project>)oSession.getAttribute("userProjectList");
			sListProjectIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(aProjectList);
			sRunnableStatusRowIds = validationcheckdao.getRunnableStatusRowIds();

			if (lIsRuleCatalogUsed) {
				sApplicationListQry= "select name, idapp from listApplications where active ='yes' "+
									 "and project_id in ("+sListProjectIds+") and ((approve_status in ("+sRunnableStatusRowIds+")) or (appType !='Data Forensics')) order by idapp desc;";
			} else {
				sApplicationListQry = String.format("select name, idApp from listApplications where active ='yes' and project_id in (%1$s) order by idapp desc",sListProjectIds);
			}
			System.out.println("validationCheckName 01 " + sApplicationListQry);

			aListApplications = jdbcTemplate.query(sApplicationListQry, new RowMapper<ListApplications>() {
				public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
					ListApplications oListApplication = new ListApplications();

					oListApplication.setName(rs.getString("name"));
					oListApplication.setIdApp(rs.getLong("idApp"));
					return oListApplication;
				}
			});			

			for (ListApplications oListApplication : aListApplications) {
				String[] aNameParts = oListApplication.getName().split("_"); 
				String sIdApp = "";
				String sIdAppToString = "";
				
				/* If application name contains leading idApp in name itself then ignore appending again else append it */
				if (aNameParts.length > 1) {								
					sIdAppToString = String.format("%1$s", oListApplication.getIdApp());
					sIdApp = aNameParts[0];					
					sIdApp = (sIdAppToString.equalsIgnoreCase(sIdApp)) ? "" : String.format("%1$s_", oListApplication.getIdApp()); 
				} else {
					sIdApp = String.format("%1$s_", oListApplication.getIdApp());
				}
				
				aListAppIdsAndNames.add(String.format("%1$s%2$s", sIdApp, oListApplication.getName()));
			}
			
			System.out.println("validationCheckName 02 " + aListApplications.size());
		}		
		return aListAppIdsAndNames;
	}

	/* Pradeep 2-April-2021 Removed from active code flow Do Not Use, rewrtten same method fully */
	@RequestMapping(value = "/validationCheckName_Do_Not_Use", method = RequestMethod.GET)
	public @ResponseBody List<String> validationCheckName_Do_Not_Use(ModelAndView model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Long projectId = (Long) session.getAttribute("projectId");
		System.out.println("@@@@@@@@@@@ validationCheckName =>" + projectId);

		List<String> validationListFrom = validationcheckdao.getValidationNames(projectId);
		// List<String>str= taplateListFrom.toString();
		System.out.println("::::: " + validationListFrom);
		// String str=tamplateListFrom.toString().replace("[","").replace("]","");
		List<String> list1 = new ArrayList<>();
		for (String list : validationListFrom) {
			String str = list.toString().replace("[", "").replace("]", "");

			// System.out.println("str =>"+str);

			list1.add(str);
		}

		// System.out.println("::: "+str);
		//System.out.println("::!@@@@@@ " + list1);
		//System.out.println(" ToString ::::::::: " + validationListFrom);
		return list1;

	}

	@RequestMapping(value = "/showValidationLog", method = RequestMethod.POST, produces = "application/json")
	public void showValidationLog(HttpSession oSession, HttpServletResponse oResponse, HttpServletRequest request,
			@RequestParam String sWhichLog) throws Exception {
		JSONObject oJsonResponse = new JSONObject();
		List<String> aRetArray = new ArrayList<String>();

		String databuckHome = "/opt/databuck";
		if (System.getenv("DATABUCK_HOME") != null) {
			databuckHome = System.getenv("DATABUCK_HOME");
		} else if (System.getProperty("DATABUCK_HOME") != null) {
			databuckHome = System.getProperty("DATABUCK_HOME");
		}

		if (sWhichLog.equals("batchExecution")) {
			System.out.println("File path is: " + databuckHome + "/showio.txt");
			aRetArray = GetLogFileLines(databuckHome + "/showio.txt", 3000, true);
			oJsonResponse.put("msg",
					String.format("Displaying (%1$s) lines from 'Batch Execution log' log file", aRetArray.size()));
		}
		if (sWhichLog.equals("timeExecution")) {
			System.out.println("File path is: " + databuckHome + "/showio1.txt");
			aRetArray = GetLogFileLines(databuckHome + "/showio1.txt", 3000, true);
			oJsonResponse.put("msg",
					String.format("Displaying (%1$s) lines from 'Execution Time logs' log file", aRetArray.size()));
		}
		if (sWhichLog.equals("uiLogs")) {
			System.out.println("File path is:/opt/apache-tomcat-9.0.1/logs/catalina.out");
			aRetArray = GetLogFileLines("/opt/apache-tomcat-9.0.1/logs/catalina.out", 3000, true);
			oJsonResponse.put("msg",
					String.format("Displaying (%1$s) lines from 'UI logs' log file", aRetArray.size()));
		}

		DebugLog("/showValidationLog", String.format(
				"Called with parameter sWhichLog=(%1$s) so Amar please add if statements to return different logs",
				sWhichLog));

		oJsonResponse.put("status", "1");

		oJsonResponse.put("loglines", aRetArray.toString().replace("[", "").replace("]", ""));
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	@RequestMapping(value = "/downloadLogFile", method = RequestMethod.POST, produces = "application/json")
	public void downloadLogFile(HttpSession oSession, HttpServletResponse oResponse, HttpServletRequest request,
			@RequestParam String sWhichLog) throws Exception {
		JSONObject oJsonResponse = new JSONObject();
		List<String> aRetArray = new ArrayList<String>();

		String databuckHome = "/opt/databuck";
		if (System.getenv("DATABUCK_HOME") != null) {
			databuckHome = System.getenv("DATABUCK_HOME");
		} else if (System.getProperty("DATABUCK_HOME") != null) {
			databuckHome = System.getProperty("DATABUCK_HOME");
		}

		if (sWhichLog.equals("batchExecution")) {
			System.out.println("File path is: " + databuckHome + "/showio.txt");
			downloadLogs(oResponse, request, sWhichLog);
		}
		if (sWhichLog.equals("timeExecution")) {
			System.out.println("File path is: " + databuckHome + "/showio1.txt");
			downloadLogs(oResponse, request, sWhichLog);
		}
		if (sWhichLog.equals("uiLogs")) {
			System.out.println("File path is: /opt/dfadmrpm/apache-tomcat-9.0.1/logs/catalina.out");
			downloadLogs(oResponse, request, sWhichLog);
		}

		DebugLog("/showValidationLog", String.format(
				"Called with parameter sWhichLog=(%1$s) so Amar please add if statements to return different logs",
				sWhichLog));

		oJsonResponse.put("status", "1");

		oJsonResponse.put("loglines", aRetArray.toString().replace("[", "").replace("]", ""));
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	public void downloadLogs(HttpServletResponse response, HttpServletRequest request, String logType) {

		try {

			String databuckHome = "/opt/databuck";
			if (System.getenv("DATABUCK_HOME") != null) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			String fileFullPath = "";
			if (logType.equals("batchExecution")) {
				fileFullPath = databuckHome + "/show1.txt";
			}
			if (logType.equals("timeExecution")) {
				fileFullPath = databuckHome + "/show2.txt";
			}
			if (logType.equals("uiLogs")) {
				fileFullPath = "/opt/dfadmrpm/apache-tomcat-9.0.1/logs/catalina.out";
			}

			System.out.println("fileFullPath ........+" + fileFullPath);
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");
			System.out.println("downloadCsvLocal ........ appPath = " + appPath);

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
			System.out.println("downloadFile.length() =>" + downloadFile.length());

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";

			String headerValue = String.format("attachment; filename=\"%s\"", logType + ".csv");
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[1024 * 1000];
			int bytesRead = -1;

			// write bytes read from the input stream into the
			// output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<String> GetLogFileLines(String sFielName, int nNoOfLines, boolean lLineEndsByHtmlTag)
			throws Exception {
		List<String> aRetArray = new ArrayList<String>();
		File oFile = null;
		Scanner oScanner = null;
		String sLineEndTag = (lLineEndsByHtmlTag) ? "<br>" : "\n";

		try {
			oFile = new File(sFielName);
			oScanner = new Scanner(oFile);
			String sLine = "";
			int nFirstLineNo = 0;

			while (oScanner.hasNextLine()) {
				sLine = oScanner.nextLine() + sLineEndTag;
				aRetArray.add(sLine);
			}
			nFirstLineNo = (aRetArray.size() <= nNoOfLines) ? 0 : (aRetArray.size() - nNoOfLines);
			if (nFirstLineNo > 0) {
				aRetArray = aRetArray.subList(nFirstLineNo, aRetArray.size());
			}

		} catch (Exception oException) {
			oException.printStackTrace();
			System.out.println("Exception in GetLogFileLines: " + oException.getMessage());
		}
		return aRetArray;
	}

	private void DebugLog(String sDebugContext, String sDebugText) {
		DateTimeFormatter oDtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime oNow = LocalDateTime.now();
		System.out.println(oDtf.format(oNow) + " " + sDebugContext + " " + sDebugText);
	}

	@RequestMapping(value = "/createValidationEssentialChecksTab")
	public ModelAndView createValidationEssentialChecksTab(HttpSession session,@RequestParam long idApp,@RequestParam long idData) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

		ModelAndView model = new ModelAndView("essentialChecks");
		System.out.println("In EssentialChecksTab controller");
		model.addObject("idApp", idApp);
		model.addObject("idData", idData);
		model.addObject("currentSection", "Validation Check");
		model.addObject("currentLink", "Add New");

		return model;
		}

		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createValidationAdvancedChecksTab")
	public ModelAndView createValidationAdvancedChecksTab(HttpSession session,@RequestParam long idApp,@RequestParam long idData) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

		ModelAndView model = new ModelAndView("advancedChecks");
		System.out.println("In AdavancedChecksTab controller");
		model.addObject("idApp", idApp);
		model.addObject("idData", idData);
		model.addObject("currentSection", "Validation Check");
		model.addObject("currentLink", "Add New");

		return model;
		}

		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createValidationFoundationChecksTab")
	public ModelAndView createValidationFoundationChecksTab(HttpSession session,@RequestParam long idApp,@RequestParam long idData) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

		ModelAndView model = new ModelAndView("finishSchemaCustomization");
		System.out.println("In FoundationChecksTab controller");
		model.addObject("idApp", idApp);
		model.addObject("idData", idData);
		model.addObject("currentSection", "Validation Check");
		model.addObject("currentLink", "Add New");

		return model;
		}

		return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/saveValidationFoundationChecksAjax", method = RequestMethod.POST)
	public void saveValidationFoundationChecksAjax(@RequestBody ListApplications listApplications, HttpServletResponse response,
			HttpSession session) {

		boolean flag = true;
		Object user = session.getAttribute("user");
		System.out.println("data:" + listApplications);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
		if (flag) {
			// System.out.println("recordcountanomaly="+listApplications.getRecordCountAnomaly());
			// System.out.println("getkeyBasedRecordCountAnomaly="+listApplications.getkeyBasedRecordCountAnomaly());
			boolean dGroupFlag = validationcheckdao.checkTheConfigurationFordGroup(listdatadefinition,
					listApplications);
			System.out.println("dGroupFlag=" + dGroupFlag);
			if (!dGroupFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The subsegment check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForBuildHistoric(listdatadefinition,
					listApplications);
			System.out.println("buildHistoricFlag=" + buildHistoricFlag);
			if (!buildHistoricFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The last read time check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}


		if(flag) {



			Long idApp = listApplications.getIdApp();
			validationcheckdao.insertintolistdfsetruleandtranrule(idApp,
					listApplications.getRecordCountAnomalyThreshold(),"N");

			int updateintolistapplication = validationcheckdao.updateintolistapplicationForFoundationChecksAjaxRequest(listApplications);
			System.out.println("updateintolistapplication in Foundation checks tab =" + updateintolistapplication);
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



	}

	@RequestMapping(value = "/saveValidationEssentialChecksAjax", method = RequestMethod.POST)
	public void saveValidationEssentialChecksAjax(@RequestBody ListApplications listApplications, HttpServletResponse response,
			HttpSession session) {

		boolean flag = true;
		Object user = session.getAttribute("user");
		System.out.println("data:" + listApplications);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
		if (flag) {

			boolean identityFlag = validationcheckdao.checkTheConfigurationForDupRowIdentity(listdatadefinition,
					listApplications);
			System.out.println("identityFlag=" + identityFlag);
			if (!identityFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate identity check (primary key) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean AllFlag = validationcheckdao.checkTheConfigurationForDupRowAll(listdatadefinition,
					listApplications);
			System.out.println("AllFlag=" + AllFlag);
			if (!AllFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The duplicate all check (duplicate key) is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean dataDriftFlag = validationcheckdao.checkTheConfigurationForDataDrift(listdatadefinition,
					listApplications);
			System.out.println("dataDriftFlag=" + dataDriftFlag);
			if (!dataDriftFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The data drift check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.checkTheConfigurationDefaultCheck(listdatadefinition,
					listApplications);
			System.out.println("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean patternCheckFlag = validationcheckdao.checkTheConfigurationPatternCheck(listdatadefinition,
					listApplications);
			System.out.println("patternCheckFlag=" + patternCheckFlag);
			if (!patternCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Pattern check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// Changes to allow user to select either DateRuleCheck or DGroupDateRuleCheck
		// only at one time
		if (flag) {
			if (listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDateRuleCheck() != null
					&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "Choose either DateRuleCheck or DGroupDateRuleCheck, both can't be enabled together.");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (flag) {
			boolean dateRuleCheckFlag = validationcheckdao.checkTheConfigurationDateRuleCheck(listdatadefinition,
					listApplications);
			System.out.println("dateRuleCheckFlag=" + dateRuleCheckFlag);
			if (!dateRuleCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Date Rule check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean dgroupNonNullFlag = validationcheckdao.checkConfigurationForDGroupNullCheck(listdatadefinition,
					listApplications);
			System.out.println("dgroupNonNullFlag=" + dgroupNonNullFlag);
			if (!dgroupNonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The DGroup Null check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean dGroupDateRuleCheckFlag = validationcheckdao.checkConfigurationDgroupDateRuleCheck(listdatadefinition,
					listApplications);
			System.out.println("dGroupDateRuleCheckFlag=" + dGroupDateRuleCheckFlag);
			if (!dGroupDateRuleCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The DGroup DateRule check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// sumeet---create
		if (flag) {
			if (listApplications.getBadData().equalsIgnoreCase("Y")) {
			boolean badDataCheckFlag = validationcheckdao.checkTheBadData(listdatadefinition,
					listApplications);
				System.out.println("badDataCheckFlag=" + badDataCheckFlag);
				if (!badDataCheckFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Bad Data check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}


		// ---------- [priyanka 25-12-2018] --

		// chnges for length Check



		if (flag) {
			if (listApplications.getlengthCheck().equalsIgnoreCase("Y")) {

				System.out.println("ValidationCheckController if ( uncom listApplications.getlengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean lengthCheck = validationcheckdao.checkForLengthCheck(listdatadefinition,
						listApplications);
				System.out.println("lengthCheck=" + lengthCheck);
				if (!lengthCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The Length check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		// -------------------
		
		//Max Length Check
		
		if (flag) {
			if (listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {

				System.out.println("ValidationCheckController if ( uncom listApplications.getMaxLengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean maxLengthCheck = validationcheckdao.checkForMaxLengthCheck(listdatadefinition,
						listApplications);
				System.out.println("MaxLengthCheck=" + maxLengthCheck);
				if (!maxLengthCheck) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The MAX Length check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		// End of Max Length Check

		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.defaultCheckFlag(listdatadefinition,
					listApplications);
			System.out.println("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The Default check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean stringFieldFlag = validationcheckdao.checkTheConfigurationForstringField(listdatadefinition,
					listApplications);
			System.out.println("stringFieldFlag=" + stringFieldFlag);
			if (!stringFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The string fingerprint check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean nonNullFlag = validationcheckdao.checkTheConfigurationForNonNullField(listdatadefinition,
					listApplications);
			System.out.println("nonNullFlag=" + nonNullFlag);
			if (!nonNullFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The nonnull check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		// Changes to allow user to select either DataDrift or Microsegment Based DataDrift
				// only at one time
				if (flag) {
					if (listApplications.getDataDriftCheck() != null && listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
							&& listApplications.getdGroupDataDriftCheck() != null
							&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
						flag = false;
						try {
							JSONObject json = new JSONObject();
							// put some value pairs into the JSON object .
							json.put("fail", "Choose either DataDrift or Microsegment Based DataDrift, both can't be enabled together.");
							response.getWriter().println(json);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}



				if (flag) {
					if(listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {

						ListApplications liForMicroCheck =  validationcheckdao.getdatafromlistapplications(listApplications.getIdApp());
						boolean microsegmentFlag = validationcheckdao.checkTheConfigurationForMicrosegment(listdatadefinition,
								liForMicroCheck);
						System.out.println("microsegmentFlag=" + microsegmentFlag);
						if (!microsegmentFlag) {
							flag = false;
							try {
								JSONObject json = new JSONObject();
								// put some value pairs into the JSON object .
								json.put("fail", "The Microsegment check is configured incorrectly");
								response.getWriter().println(json);
							} catch (Exception e) {
								e.printStackTrace();
							}
							// return true;
						}
					}
				}



		if(flag) {



			validationcheckdao.updateintolistDFSetComparisonRule(listApplications.getIdApp(),listApplications.getDuplicateCheck());
			int updateintolistapplication = validationcheckdao.updateintolistapplicationForEssentialChecksAjaxRequest(listApplications);
			System.out.println("updateintolistapplication in Essential checks tab =" + updateintolistapplication);
			int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
					listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
					listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(), "N",
					listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(), 0.0);
			System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



	}

	@RequestMapping(value = "/saveValidationAdvancedChecksAjax", method = RequestMethod.POST)
	public void saveValidationAdvancedChecksAjax(@RequestBody ListApplications listApplications, HttpServletResponse response,
			HttpSession session) {

		boolean flag = true;
		Object user = session.getAttribute("user");
		System.out.println("data:" + listApplications);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
		if (flag) {
			boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
					listApplications);
			System.out.println("applyRulesFlag=" + applyRulesFlag);
			if (!applyRulesFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("rules", "The rules are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}
		if (flag) {
			boolean applyDerivedColumnsFlag = validationcheckdao
					.checkTheConfigurationForapplyDerivedColumns(listdatadefinition, listApplications);
			System.out.println("applyDerivedColumnsFlag=" + applyDerivedColumnsFlag);
			if (!applyDerivedColumnsFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("derivedcols", "The Derived Columns are configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			if (listApplications.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
			boolean timelinessKeyFlag = validationcheckdao.checkTheConfigurationForTimelinessKeyField(listdatadefinition,
					listApplications);
				System.out.println("timelinessKeyFlag=" + timelinessKeyFlag);
				if (!timelinessKeyFlag) {
					flag = false;
					try {
						JSONObject json = new JSONObject();
						// put some value pairs into the JSON object .
						json.put("fail", "The TimelinessKey check is configured incorrectly");
						response.getWriter().println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}

		if (flag) {
			boolean numFieldFlag = validationcheckdao.checkTheConfigurationForNumField(listdatadefinition,
					listApplications);
			System.out.println("numFieldFlag=" + numFieldFlag);
			if (!numFieldFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The numerical stat check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}

		if (flag) {
			boolean recordAnomalyFlag = validationcheckdao.checkTheConfigurationForRecordAnomaly(listdatadefinition,
					listApplications);
			System.out.println("recordAnomalyFlag=" + recordAnomalyFlag);
			if (!recordAnomalyFlag) {
				flag = false;
				try {
					JSONObject json = new JSONObject();
					// put some value pairs into the JSON object .
					json.put("fail", "The record anomaly check is configured incorrectly");
					response.getWriter().println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// return true;
			}
		}


		if(flag) {



			int updateintolistapplication = validationcheckdao.updateintolistapplicationForAdvancedChecksAjaxRequest(listApplications);
			/*
			System.out.println("updateintolistapplication in Advanced checks tab =" + updateintolistapplication);
			int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitionsAdCheckTab(
					listApplications.getIdData(), listApplications.getNumericalStatCheck(),
					listApplications.getNumericalStatThreshold(), listApplications.getRecordAnomalyCheck(),
					listApplications.getRecordAnomalyThreshold()
					);
			System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);
			*/
			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



	}

}
