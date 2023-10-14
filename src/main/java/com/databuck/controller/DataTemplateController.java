package com.databuck.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.config.DatabuckEnv;
import com.databuck.service.RemoteClusterAPIService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ApproveMicroSegmentsDTO;
import com.databuck.bean.DateAnalysisData;
import com.databuck.bean.Domain;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataDefinitionDelta;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.NumericalAnalysisData;
import com.databuck.bean.Project;
import com.databuck.bean.StringAnalysisData;
import com.databuck.bean.TemplateDeltaResponse;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.listGlobalThresholds;
import com.databuck.bean.ValidateQuery;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IGlobalThresholdDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.AzureConnection;
import com.databuck.datatemplate.AzureDataLakeConnection;
import com.databuck.datatemplate.BatchFileSystem;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.HiveKerberosConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.MYSQLConnection;
import com.databuck.datatemplate.MsSqlActiveDirectoryConnection;
import com.databuck.datatemplate.OracleConnection;
import com.databuck.datatemplate.OracleRACConnection;
import com.databuck.datatemplate.PostgresConnection;
import com.databuck.datatemplate.S3BatchConnection;
import com.databuck.datatemplate.SnowflakeConnection;
import com.databuck.datatemplate.TeradataConnection;
import com.databuck.datatemplate.VerticaConnection;
import com.databuck.econstants.DeltaType;
import com.databuck.security.LogonManager;
import com.databuck.service.DataTemplateDeltaCheckService;
import com.databuck.service.IDataAlgorithService;
import com.databuck.service.IProjectService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.databuck.datatemplate.AzureDataLakeGen2Connection;

@Controller
public class DataTemplateController {

	private final String CURRENT_MODULE_NAME = "Data Template";

	@Autowired
	public LoginService loginService;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	GlobalRuleDAO globalruledao;

	@Autowired
	IGlobalThresholdDAO globalthresholddao;

	@Autowired
	IDataAlgorithService dataAlgorithService;

	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private IProjectService IProjectservice;

	@Autowired
	private LogonManager logonManager;

	@Autowired
	AzureDataLakeGen2Connection azureDataLakeGen2Connection;

	@Autowired
	private IProjectDAO projectdAO;

	static ArrayList<String> al = null;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	static {
		al = new ArrayList<String>();
		al.add("nonNull");
		al.add("primaryKey");
		al.add("hashValue");
		al.add("numericalStat");
		al.add("stringStat");
		al.add("KBE");
		al.add("dgroup");
		al.add("dupkey");
		al.add("measurement");
		al.add("incrementalCol");
		al.add("recordAnomaly");
		al.add("dataDrift");
		al.add("isMasked");
		al.add("partitionBy");
		al.add("badData");
		al.add("lengthCheck");

		al.add("maxLengthCheck");

		al.add("lengthValue");

	}

	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	MYSQLConnection mysqlconnection;

	@Autowired
	PostgresConnection postgresConnection;

	@Autowired
	TeradataConnection teradataConnection;

	@Autowired
	OracleRACConnection OracleRACConnection;

	@Autowired
	CassandraConnection cassandraconnection;

	@Autowired
	HiveConnection hiveconnection;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;

	@Autowired
	MSSQLConnection mSSQLConnection;

	@Autowired
	BigQueryConnection bigQueryConnection;

	@Autowired
	AzureConnection azureConnection;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private RBACController rbacController;

	@Autowired
	public SchemaDAOI SchemaDAOI;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	HiveKerberosConnection hiveKerberosConnection;

	@Autowired
	SnowflakeConnection snowFlakeConnection;

	@Autowired
	BatchFileSystem batchFileSystem;

	@Autowired
	S3BatchConnection s3BatchConnection;

	@Autowired
	AzureDataLakeConnection azureDataLakeConnection;

	@Autowired
	DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ITaskDAO iTaskDAO;

	@RequestMapping(value = "/copyTemplate", method = RequestMethod.GET)
	public void copyTemplate(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam String newTemplateName, @RequestParam long idData) {

		System.out.println("newTemplateName is " + newTemplateName);
		System.out.println("idData is " + idData);

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

		Long newTemplateId = templateviewdao.copyTemplate(idData, newTemplateName, createdByUser);
		//check it its derived template
		ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(newTemplateId);
		if(listDataSource != null && listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
			// Copy the listDerivedDataSource
			templateviewdao.copyDerivedTemplate(idData, newTemplateId, createdByUser, newTemplateName);
		}
		//DC-152 amol 12/04/22
		System.out.println("======= newTemplateId ===>" + newTemplateId);
		JSONObject json = new JSONObject();
		try {
			if (newTemplateId != null && newTemplateId != 0l) {
				json.put("success", "Template Created Successfully");
				response.getWriter().println(json);
				response.getWriter().flush();
			} else {
				json.put("failure", "Template Creation failed");
				response.getWriter().println(json);
				response.getWriter().flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/datatemplateview", method = RequestMethod.GET)
	public ModelAndView getListDataSource(HttpServletRequest request, ModelAndView model, HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Data Template", "R", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");

			Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");

			// When project is not selected in search filter, default selected project will
			// be considered for search filter
			Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
					? searchfilter_projectId
					: projectId;
			Project selectedProject = projectdAO.getSelectedProject(selected_projectId);
			// List<ListDataSource> listdatasource =
			// listdatasourcedao.getListDataSourceTable(selected_projectId, projList,
			// fromDate, toDate);

			// Added by Tapesh on '19-July-2021'
			String objName = (String) session.getAttribute("ObjectName");

			try {

				// objName=(String) session.getAttribute("ObjectName");

				if (objName != null) {
					List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTable(selected_projectId,
							projList, fromDate, toDate, objName);
					model.addObject("projectList", projList);
					model.addObject("selectedProject", selectedProject);
					model.addObject("listdatasource", listdatasource);
					model.setViewName("dataTemplateView");
					model.addObject("currentSection", "Data Template");
					model.addObject("currentLink", "DTView");
					session.setAttribute("ObjectName", null);
				} else {

					List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTable(selected_projectId,
							projList, fromDate, toDate);
					model.addObject("projectList", projList);
					model.addObject("selectedProject", selectedProject);
					model.addObject("listdatasource", listdatasource);
					model.setViewName("dataTemplateView");
					model.addObject("currentSection", "Data Template");
					model.addObject("currentLink", "DTView");
				}

				model.addObject("SelectedProjectId", (Long) session.getAttribute("projectId"));

			} catch (Exception e) {
				e.printStackTrace();
			}

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getPaginatedDataTemplateList", method = RequestMethod.POST, produces = "application/json")
	public void getPaginatedDataTemplateList(HttpSession oSession, HttpServletRequest oRequest,
			HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		ObjectMapper oObjectMapper = new ObjectMapper();

		Long nProjectId = 0l;
		Project oSelectedProject = null;
		List<Project> aProjectList = null;

		try {
			DateUtility.DebugLog("serverSideDataTableTest 01", "Start of controller");

			nProjectId = (Long) oSession.getAttribute("projectId");
			oSelectedProject = iProjectDAO.getSelectedProject(nProjectId);
			// aProjectList = (List<Project>)oSession.getAttribute("userProjectList");
			aProjectList = loginService.getAllDistinctProjectListForUser(oSession);

			oJsonResponse.put("SelectedProjectId", nProjectId);
			oJsonResponse.put("AllProjectList", new JSONArray(oObjectMapper.writeValueAsString(aProjectList)));
			oJsonResponse.put("SecurityFlags", getSecurityFlags(CURRENT_MODULE_NAME, oSession));

			for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds",
					"SearchText" }) {
				oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
			}

			oJsonResponse.put("ViewPageDataList", listdatasourcedao.getPaginatedDataTemplateJsonData(oPaginationParms));
			DateUtility.DebugLog("serverSideDataTableTest 02", String.format(
					"End of controller, got data sending to client \n%1$s\n", oJsonResponse.get("SecurityFlags")));

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject getSecurityFlags(String sModuleName, HttpSession oSession) {
		String sSecurityAccessFlags = "{ 'Create': %1$s, 'Update': %2$s, 'Delete': %3$s }";

		sSecurityAccessFlags = String.format(sSecurityAccessFlags, rbacController.rbac(sModuleName, "C", oSession),
				rbacController.rbac(sModuleName, "U", oSession), rbacController.rbac(sModuleName, "D", oSession));
		return new JSONObject(sSecurityAccessFlags);
	}

	@RequestMapping(value = "/listdataview", method = RequestMethod.GET)
	public ModelAndView editTask(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "R", session);
		if (rbac) {
			System.out.println("idData=" + request.getParameter("idData"));
			Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("view listDataView  id " + idData);
			String dataLocation = request.getParameter("dataLocation");
			System.out.println("dataLocation=" + dataLocation);
			if (dataLocation.equals("File Management")) {
				String name = request.getParameter("name");
				System.out.println("name=" + name);
				String description = request.getParameter("description");
				System.out.println("description=" + description);
				List<listDataAccess> dataFromListDataAccess = templateviewdao.getDataFromListDataAccess(idData);
				String referenceFiles = templateviewdao.getReferenceFilesFromListDataFiles(idData);
				ModelAndView model = new ModelAndView("fileManagementDataTemplateView");
				model.addObject("name", name);
				model.addObject("description", description);
				model.addObject("listDataAccessData", dataFromListDataAccess);
				model.addObject("referenceFiles", referenceFiles);
				model.addObject("currentSection", "Data Template");
				model.addObject("currentLink", "DTView");
				model.addObject("name", request.getParameter("name"));
				return model;
			} else {
				List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
				System.out.println(listdatadefinition.size());
				ModelAndView model = new ModelAndView("listDataView");

				try {
					model.addObject("idDataSchema", listdatadefinition.get(0).getIdDataSchema());
				} catch (Exception e) {
					// e.printStackTrace();
					model.setViewName("message");
					model.addObject("currentSection", "Data Template");
					model.addObject("currentLink", "DTView");
					model.addObject("msg", "Data Template not Created Successfully");
					return model;

				}
				// Delta changes of listDatadefinition
				TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService
						.getTemplateDeltaChanges(idData);

				model.addObject("currentSection", "Data Template");
				model.addObject("currentLink", "DTView");
				model.addObject("idData", idData);
				model.addObject("name", request.getParameter("name"));
				model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
				model.addObject("templateDeltaResponse", templateDeltaResponse);

				return model;
			}
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/updateTemplateDeltaApprovalStatus", method = RequestMethod.POST)
	public void updateTemplateDeltaApprovalStatus(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		try {
			System.out.println("\n====> updateTemplateDeltaApprovalStatus - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			String idData = request.getParameter("idData");
			Long l_idData = Long.parseLong(idData);
			String approvalStatus = request.getParameter("approvalStatus");

			System.out.println("idData: " + idData);
			System.out.println("approvalStatus: " + approvalStatus);

			boolean updateStatus = listdatasourcedao.updateTemplateDeltaApprovalStatus(l_idData, approvalStatus);
			System.out.println("====> updateStatus: " + updateStatus);
			if (updateStatus) {
				response.getWriter().println("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/rejectMicroSegments", method = RequestMethod.POST)
	public void rejectMicroSegments(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestBody List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		try {
			System.out.println("\n====> rejectMicroSegments - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			boolean status = dataTemplateDeltaCheckService.rejectMicrosegments(listDataDefinitionDelta);
			if (status) {
				response.getWriter().println("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/approveMicroSegments", method = RequestMethod.GET)
	public @ResponseBody String approveMicroSegments(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {
		try {
			System.out.println("\n====> approveMicroSegments - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Long idUser = (Long) session.getAttribute("idUser");
			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

			// getting createdBy username from createdBy userId
			String createdByUser = "";
			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
				createdByUser = (String) session.getAttribute("createdByUser");
			} else {
				createdByUser = userDAO.getUserNameByUserId(idUser);
			}

			ApproveMicroSegmentsDTO approveMicroSegmentsDTO = dataTemplateDeltaCheckService.approveMicrosegments(idData,
					createdByUser);

			System.out.println("\n====> approveMicroSegments - END ");
			return approveMicroSegmentsDTO.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/rejectColumnAnalysisChanges")
	public void rejectColumnAnalysisChanges(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {
		try {
			System.out.println("\n====> rejectColumnAnalysisChanges - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Clear the staging
			listdatasourcedao.clearListDataDefinitonStagingForIdData(idData);

			response.getWriter().println("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/approveColumnAnalysisChanges", method = RequestMethod.POST)
	public void approveColumnAnalysisChanges(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestBody List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		try {
			System.out.println("\n====> approveColumnAnalysisChanges - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			for (ListDataDefinitionDelta lddDelta : listDataDefinitionDelta) {
				if (lddDelta.getDeltaType().equals(DeltaType.MISSING)) {
					System.out.println("missingcolId: " + lddDelta.getMissingColId());
				}
			}
			boolean status = dataTemplateDeltaCheckService.approveColumnAnalysisChanges(listDataDefinitionDelta);
			if (status) {
				response.getWriter().println("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deletedatasource", method = RequestMethod.GET)
	public ModelAndView deletedatasource(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "D", session);
		if (rbac) {

			int idData = Integer.parseInt(request.getParameter("idData"));
			System.out.println("delete datasource id " + idData);

			ListDataSource listdatasource = listdatasourcedao.delete(idData);

			ModelAndView modelAndView = new ModelAndView("deleteTemplate");
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTView");
			modelAndView.addObject("listdatasource", listdatasource);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/delete_Template", method = RequestMethod.GET)
	public ModelAndView DeleteDataDource(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "D", session);
		if (rbac) {
			try {
				int idData = Integer.parseInt(request.getParameter("idData"));
				System.out.println("/delete_Template idData" + idData);
				int deleteDataSource = listdatasourcedao.deleteDataSource(idData);
				System.out.println("deleteDataSource=" + deleteDataSource);
				ModelAndView modelAndView = new ModelAndView("deleteDataSourceSuccess");
				modelAndView.addObject("currentSection", "Data Template");
				modelAndView.addObject("currentLink", "DTView");
				return modelAndView;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/changeLocationForDataTemplateAjax", method = RequestMethod.POST)
	public void changeLocationForDataTemplateAjax(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam String location) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("idData=" + idData);
		// System.out.println("helllo.........");
		Long projectId = (Long) session.getAttribute("projectId");
		Integer domainId = (Integer) session.getAttribute("domainId");
		Map<Long, String> dataTemplateForRequiredLocation = listdatasourcedao
				.getDataTemplateForRequiredLocation(location, projectId, domainId);
		System.out.println("dataTemplateForRequiredLocation=" + dataTemplateForRequiredLocation);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(dataTemplateForRequiredLocation);
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

	@RequestMapping(value = "/getRollDataTargetSchemaList", method = RequestMethod.GET)
	public void getRollDataTargetSchemaList(HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {
		System.out.println("\n=====> getRollDataTargetSchemaList ");
		Long projectId = (Long) session.getAttribute("projectId");
		Map<Long, String> targetSchemaList = listdatasourcedao.getRollDataTargetSchemaList(projectId);
		System.out.println("targetSchemaList=" + targetSchemaList);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(targetSchemaList);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject displayName = new JSONObject();
			displayName.put("success", jsonInString);
			System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/dataTemplateAddNew", method = RequestMethod.GET)
	public ModelAndView getCreateTemplateView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId, projList, fromDate,
					toDate);
			System.out.println("listdataschema=" + listdataschema);

			List<Domain> listdomain = globalruledao.getDomainList();
			System.out.println("listdomain=" + listdomain);

			boolean isRuleCatalogDiscovery = JwfSpaInfra
					.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N").equalsIgnoreCase("Y")
							? true
							: false;

			ModelAndView modelAndView = new ModelAndView("createDataTemplate");
			modelAndView.addObject("listdataschema", listdataschema);
			modelAndView.addObject("listdomain", listdomain);
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTAdd New");
			modelAndView.addObject("isRuleCatalogDiscovery", isRuleCatalogDiscovery);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	/*
	 * Added as part of derived template changes
	 */

	@RequestMapping(value = "/derivedTemplateAddNew", method = RequestMethod.GET)
	public ModelAndView getCreateDerivedTemplateView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("createDerivedDataTemplate");
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DerivedDT Add New");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	/*
	 * Added as part of derived template changes
	 */

	@RequestMapping(value = "/editDerivedDataTemplate", method = RequestMethod.GET)
	public ModelAndView editDerivedDataTemplate(HttpServletRequest request, HttpSession session,
			@RequestParam Long idData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("editDerivedDataTemplate");
			ListDerivedDataSource listDerivedDataSource = listdatasourcedao
					.getDataFromListDerivedDataSourcesOfIdData(idData);

			modelAndView.addObject("listDerivedDataSource", listDerivedDataSource);
			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DerivedDTAdd New");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/editDataTemplate", method = RequestMethod.GET)
	public ModelAndView editDataTemplate(HttpServletRequest request, HttpSession session, @RequestParam Long idData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("editDataTemplate");
			ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(idData);
			if (!(listDataSource.getDataLocation().equalsIgnoreCase("FILESYSTEM")
					|| listDataSource.getDataLocation().equalsIgnoreCase("HDFS")
					|| listDataSource.getDataLocation().equalsIgnoreCase("File Management")
					|| listDataSource.getDataLocation().equalsIgnoreCase("MapR FS")
					|| listDataSource.getDataLocation().equalsIgnoreCase("MapR DB")
					|| listDataSource.getDataLocation().equalsIgnoreCase("S3")
					|| listDataSource.getDataLocation().equalsIgnoreCase("AzureDataLakeStorageGen2"))) {
				ListDataSchema listDataSchema = listdatasourcedao
						.getListDataSchemaForIdDataSchema(listDataAccess.getIdDataSchema()).get(0);
				modelAndView.addObject("listDataSchema", listDataSchema);
			}

			List<Domain> listdomain = globalruledao.getDomainList();
			System.out.println("listdomain=" + listdomain);
			modelAndView.addObject("listdomain", listdomain);
			modelAndView.addObject("listDataSource", listDataSource);
			modelAndView.addObject("listDataAccess", listDataAccess);

			modelAndView.addObject("currentSection", "Data Template");
			modelAndView.addObject("currentLink", "DTAdd New");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	public Long createValidationCheck(HttpSession session, String validationCheckName, Long idData, Long idUser,
			List<ListDataDefinition> lstDataDefinition, Long project_id) throws Exception {

		long idApp = -99;
		ListDataSource ld = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
		Integer domainId = (Integer) ld.getDomain();
		Long projectId = Long.valueOf(ld.getProjectId());
		// activedirectory flag check
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
		String createdByUser = "";
		if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
			createdByUser = (String) session.getAttribute("email");
			System.out.println("======= createdByName ===>" + createdByUser);
		} else {
			// getting createdBy username from createdBy userId
			System.out.println("======= idUser ===>" + idUser);

			createdByUser = userDAO.getUserNameByUserId(idUser);

			System.out.println("======= createdByName ===>" + createdByUser);
		}

		try {
			idApp = validationcheckdao.insertintolistapplications(validationCheckName, "A default validation check",
					"Data Forensics", idData, idUser, 0.0, "N", null, null, null, "N", projectId, createdByUser,
					domainId);

			System.out.println("validation idApp is " + idApp + "" + idData);
			int updateApplicationNameWithIdApp = validationcheckdao.updateApplicationNameWithIdApp(validationCheckName,
					idApp);
		} catch (Exception e) {
			System.out.println("Exception encountered while creating validation check.");
			e.printStackTrace();
			throw e;
		}

		// Create an object of type listApplications by using default values
		// from listdatadefinition
		ListApplications listApplications = new ListApplications();
		System.out.println("validation1 idApp is " + idApp);
		listApplications.setIdApp(idApp);
		listApplications.setColOrderValidation("N");
		listApplications.setFileNameValidation("N");
		listApplications.setEntityColumn("N");
		listApplications.setNumericalStatCheck("N");
		listApplications.setStringStatCheck("N");
		listApplications.setDataDriftCheck("N");
		listApplications.setRecordAnomalyCheck("N");
		listApplications.setNonNullCheck("N");
		listApplications.setDupRowIdentity("N");
		listApplications.setDupRowIdentityThreshold(0.0);
		listApplications.setDupRowAll("N");
		listApplications.setDupRowAllThreshold(0.0);
		listApplications.setDuplicateCheck("N");
		listApplications.setUpdateFrequency("Never");
		listApplications.setRecordCountAnomaly("Y");
		listApplications.setKeyGroupRecordCountAnomaly("N");
		listApplications.setkeyBasedRecordCountAnomaly("N");
		listApplications.setApplyDerivedColumns("N");
		listApplications.setApplyRules("N");
		listApplications.setRecordCountAnomalyThreshold(0.0);
		listApplications.setNumericalStatThreshold(3.0);
		listApplications.setStringStatThreshold(0.0);
		listApplications.setLengthCheckThreshold(0.0);
		listApplications.setDataDriftThreshold(0.0);
		listApplications.setNonNullThreshold(0.0);
		listApplications.setTimeSeries("None");
		listApplications.setOutOfNormCheck("N");
		listApplications.setGroupEquality("N");
		listApplications.setGroupEqualityThreshold(0.0);
		listApplications.setBuildHistoricFingerPrint("N");
		listApplications.setIncrementalMatching("N");
		listApplications.setTimelinessKeyChk("N");
		listApplications.setDefaultCheck("N");
		listApplications.setlengthCheck("N");
		listApplications.setMaxLengthCheck("N");
		listApplications.setdGroupNullCheck("N");
		listApplications.setdGroupDateRuleCheck("N");

		if (lstDataDefinition != null) {
			for (Iterator lstIterator = lstDataDefinition.iterator(); lstIterator.hasNext();) {
				ListDataDefinition dataDefinition = (ListDataDefinition) lstIterator.next();
				if (dataDefinition.getNumericalStat().equalsIgnoreCase("column_name")) {
					listApplications.setNumericalStatCheck("column_name");
					listApplications.setNumericalStatThreshold(dataDefinition.getNumericalThreshold());
				}
				if (dataDefinition.getStringStat().equalsIgnoreCase("column_name")) {
					listApplications.setStringStatCheck("column_name");
					listApplications.setStringStatThreshold(dataDefinition.getStringStatThreshold());
				}
				if (dataDefinition.getDataDrift().equalsIgnoreCase("column_name")) {
					listApplications.setDataDriftCheck("column_name");
					listApplications.setDataDriftThreshold(dataDefinition.getDataDriftThreshold());
				}
				if (dataDefinition.getRecordAnomaly().equalsIgnoreCase("column_name")) {
					listApplications.setRecordAnomalyCheck("column_name");
				}
				if (dataDefinition.getNonNull().equalsIgnoreCase("column_name")) {
					listApplications.setNonNullCheck("column_name");
					listApplications.setNonNullThreshold(dataDefinition.getNullCountThreshold());
				}
				if (dataDefinition.getPrimaryKey().equalsIgnoreCase("column_name")) {
					listApplications.setDupRowIdentity("column_name");
					// listApplications.setDupRowIdentityThreshold(dataDefinition.);
				}
				if (dataDefinition.getDupkey().equalsIgnoreCase("column_name")) {
					listApplications.setDuplicateCheck("column_name");
					listApplications.setDupRowAll("column_name");
					// listApplications.setDupRowAllThreshold(dataDefinition.);
				}
				if (dataDefinition.getDgroup().equalsIgnoreCase("column_name")) {
					listApplications.setRecordCountAnomaly("N");
					listApplications.setkeyBasedRecordCountAnomaly("column_name");
				}
				if (dataDefinition.getDefaultCheck() != null
						&& dataDefinition.getDefaultCheck().equalsIgnoreCase("column_name")) {
					listApplications.setDefaultCheck("column_name");

				}
				if (dataDefinition.getPatternCheck() != null
						&& dataDefinition.getPatternCheck().equalsIgnoreCase("column_name")) {
					listApplications.setPatternCheck("column_name");
				}
				if (dataDefinition.getLengthCheck().equalsIgnoreCase("column_name")) {
					listApplications.setlengthCheck("column_name");
				}
				if (dataDefinition.getMaxLengthCheck().equalsIgnoreCase("column_name")) {
					listApplications.setMaxLengthCheck("column_name");
				}
			}
		}

		try {
			validationcheckdao.insertintolistdfsetruleandtranrule(idApp,
					listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());

			if (listApplications.getHistoricDateFormat() == "") {
				String automaticDateFormat = validationcheckdao.getAutomaticDateFormat(listApplications);
				listApplications.setHistoricDateFormat(automaticDateFormat);
			}
			int updateintolistapplication = validationcheckdao
					.updateintolistapplicationForAjaxRequest(listApplications);
			System.out.println("updateintolistapplication=" + updateintolistapplication);
			System.out.println("datadriftthreshold=" + listApplications.getDataDriftThreshold());

			int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
					listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
					listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(), "N",
					listApplications.getRecordCountAnomalyThreshold(), listApplications.getDataDriftThreshold(), 0.0);
			System.out.println("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

			int insertintolistdftranrule = validationcheckdao.insertintolistdftranrule(idApp,
					listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
					listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());
		} catch (Exception e) {
			System.out.println("Exception encountered while creating validation check.");
			e.printStackTrace();
			throw e;
		}
		return idApp;
	}

	@RequestMapping(value = "/duplicatedatatemplatename", method = RequestMethod.POST)
	public void duplicatedatatemplatename(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String dataTemplateName = req.getParameter("val");
		// System.out.println("dataTemplateName=" + dataTemplateName);

		// Eliminates spaces and special characters -changed by mamta

		if (dataTemplateName == null || dataTemplateName.trim().isEmpty()) {

			try {
				res.getWriter().println("Please Enter Data Template Name");
				return;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	/*	Pattern pattern = Pattern.compile("[^A-Za-z0-9_]");				
		if (pattern.matcher(dataTemplateName).find()) {
			try {
				res.getWriter().println("Please Enter Data Template Name without spaces and special characters");
				return;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		 Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");//"[^A-Za-z0-9_]"
	      Matcher match = pattern.matcher(dataTemplateName);
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
			  Matcher match2 = pattern2.matcher(dataTemplateName);
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
		String name = DataTemplateAddNewDAO.duplicatedatatemplatename(dataTemplateName);
		// System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter().println(
						"<span style=\"color:red;\"><b>This Data Template name is in use. Please choose another name.</b></span>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/updateDataTemplate", method = RequestMethod.GET)
	public ModelAndView updateDataTemplate(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ModelAndView mav = new ModelAndView("message");
		mav.addObject("currentSection", "Data Template");
		mav.addObject("currentLink", "DTView");
		String dataLocation = req.getParameter("dataLocation");
		String idData = req.getParameter("idData");
		System.out.println("dataLocation:" + dataLocation);

		String idDataSchema = req.getParameter("idDataSchema");

		StringBuffer sb_lds = new StringBuffer(
				"UPDATE listDataSources SET name= '" + req.getParameter("dataset") + "',description='"
						+ req.getParameter("description") + "',dataLocation='" + dataLocation + "',updatedAt=now(), ");

		StringBuffer sb_lda = new StringBuffer("UPDATE listDataAccess SET  ");

		if (dataLocation.equalsIgnoreCase("Oracle RAC") || dataLocation.equalsIgnoreCase("Oracle")
				|| dataLocation.equalsIgnoreCase("ClouderaHive") || dataLocation.equalsIgnoreCase("Hive")
				|| dataLocation.equalsIgnoreCase("Hive Kerberos") || dataLocation.equalsIgnoreCase("MSSQL")
				|| dataLocation.equalsIgnoreCase("MSSQLActiveDirectory") || dataLocation.equalsIgnoreCase("Vertica")
				|| dataLocation.equalsIgnoreCase("Teradata") || dataLocation.equalsIgnoreCase("Postgres")
				|| dataLocation.equalsIgnoreCase("Amazon Redshift") || dataLocation.equalsIgnoreCase("FileSystem Batch")
				|| dataLocation.equalsIgnoreCase("SnowFlake") || dataLocation.equalsIgnoreCase("S3 Batch")
				|| dataLocation.equalsIgnoreCase("BigQuery") || dataLocation.equalsIgnoreCase("AzureSynapseMSSQL")
				|| dataLocation.equalsIgnoreCase("S3 IAMRole Batch")
				|| dataLocation.equalsIgnoreCase("AzureDataLakeStorageGen1") || dataLocation.equalsIgnoreCase("MYSQL")
				|| dataLocation.equalsIgnoreCase("MapR Hive") || dataLocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch") ) {

			// handle query

			String querycheckboxid = req.getParameter("querycheckbox");
			System.out.println("in handle query update method-->" + querycheckboxid);

			if (querycheckboxid == null) {
				querycheckboxid = "N";
			}

			String historicDateTable = req.getParameter("historicDateTable");
			System.out.println("historicDateTable=" + historicDateTable);

			if (querycheckboxid != null && querycheckboxid.equalsIgnoreCase("Y")) {
				historicDateTable = (historicDateTable != null) ? historicDateTable.trim() : "";
			} else {
				historicDateTable = "";
			}

			if (querycheckboxid != null && querycheckboxid.equalsIgnoreCase("Y")) {
				String querytextboxid = req.getParameter("querytextboxid");
				sb_lda.append("query='Y',");
				sb_lda.append("queryString=\"" + querytextboxid + "\",");
				sb_lda.append("wherecondition='',");
				sb_lda.append("folderName=\'" + req.getParameter("tableNameid") + "\',");
				sb_lda.append("historicDateTable='" + historicDateTable + "',");
			} else {
				sb_lda.append("query='N',");
				// handle where condtition
				String whereId = req.getParameter("whereId");
				sb_lda.append("folderName=\'" + req.getParameter("tableNameid") + "\',");
				if (whereId != null && whereId.length() != 0) {
					sb_lda.append("wherecondition=\"" + whereId + "\",");
				} else {
					sb_lda.append("wherecondition='',");
				}
			}
			String querytextboxid = req.getParameter("querytextboxid");
			// handle incremental matching source
			String incrementalSourceId = req.getParameter("incrementalsourceid");
			if (incrementalSourceId != null && incrementalSourceId.equalsIgnoreCase("column_name")) {
				sb_lda.append(" incrementalType='" + req.getParameter("incrementalsourceid") + "',");
				sb_lda.append(" dateFormat='" + req.getParameter("dateformatid") + "',");
				sb_lda.append(" sliceStart='" + req.getParameter("slicestartid") + "',");
				sb_lda.append(" sliceEnd='" + req.getParameter("sliceendid") + "',");
			} else {
				sb_lda.append(" incrementalType='N' ,");
				sb_lda.append(" dateFormat='',");
				sb_lda.append(" sliceStart='',");
				sb_lda.append(" sliceEnd='',");
			}

		} else if (dataLocation.equalsIgnoreCase("FILESYSTEM") || dataLocation.equalsIgnoreCase("HDFS")
				|| dataLocation.equalsIgnoreCase("MapR FS") || dataLocation.equalsIgnoreCase("MapR DB")
				|| dataLocation.equalsIgnoreCase("S3")) {

			String fileQueryCheckBoxId = req.getParameter("filequerycheckbox");
			if (fileQueryCheckBoxId == null) {
				fileQueryCheckBoxId = "N";
			}

			if (dataLocation.equalsIgnoreCase("FILESYSTEM") && fileQueryCheckBoxId != null
					&& fileQueryCheckBoxId.equalsIgnoreCase("Y")) {
				String fileQueryTextbox = req.getParameter("filequerytextboxid");
				sb_lda.append("query='Y',");
				sb_lda.append("queryString=\"" + fileQueryTextbox + "\",");
				sb_lda.append("wherecondition='',");
				sb_lda.append("folderName=\"" + req.getParameter("tableNameid") + "\",");
			}
			sb_lds.append(" ignoreRowsCount=" + req.getParameter("rowsId"));
			sb_lda.append(" hostName='" + req.getParameter("hostName") + "', ");
			sb_lda.append(" folderName='" + req.getParameter("schemaName") + "', ");
			sb_lda.append(" userName='" + req.getParameter("userName") + "' ,");
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String encryptedText = encryptor.encrypt(req.getParameter("pwd"));
			sb_lda.append(" pwd='" + encryptedText + "',");

		} else if (dataLocation.equalsIgnoreCase("File Management")) {
			sb_lda.append(" hostName='" + req.getParameter("hostName") + "', ");
			sb_lda.append(" folderName='" + req.getParameter("schemaName") + "', ");
			sb_lda.append(" userName='" + req.getParameter("userName") + "' ,");

			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String encryptedText = encryptor.encrypt(req.getParameter("pwd"));
			sb_lda.append(" pwd='" + encryptedText + "' ,");

		}

		String sb_lds_str = sb_lds.toString().trim();
		sb_lds_str = sb_lds_str.substring(0, sb_lds_str.lastIndexOf(","));

		String sb_lda_str = sb_lda.toString().trim();
		sb_lda_str = sb_lda_str.substring(0, sb_lda_str.lastIndexOf(","));

		sb_lda_str = sb_lda_str.concat(" WHERE idData=" + idData + "");
		sb_lds_str = sb_lds_str.concat(" WHERE idData=" + idData + "");

		System.out.println("sb_lds_str: " + sb_lds_str);
		System.out.println("sb_lda_str: " + sb_lda_str);
		boolean updateStatus = listdatasourcedao.updateDataTemplate(sb_lds_str, sb_lda_str);

		if (updateStatus) {
			mav.addObject("msg", "Data Template Customized Successfully");
		} else {
			mav.addObject("msg", "Unable to edit Data Template, Please check your configuration and try again.");
		}
		return mav;
	}

	/*
	 * Added as part of derived template changes
	 */

	@RequestMapping(value = "/updateDerivedDataTemplate", method = RequestMethod.POST)
	public ModelAndView updateDerivedDataTemplate(HttpServletRequest req, HttpServletResponse res,
			HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ModelAndView mav = new ModelAndView("message");
		mav.addObject("currentSection", "Data Template");
		mav.addObject("currentLink", "DerivedDT View");

		String idDerivedData = req.getParameter("idDerivedData");
		System.out.println("idDerivedData:" + idDerivedData);

		String queryText = req.getParameter("querytext");
		String derivedDescription = req.getParameter("description");
		// Query compatibility changes for both POSTGRES and MYSQL
		String lddsUpdate = "UPDATE listDerivedDataSources SET description='" + derivedDescription + "',queryText='"+ queryText.replace("'","''") + "',updatedAt=now()";
		lddsUpdate = lddsUpdate.concat(" WHERE idDerivedData=" + idDerivedData);

		boolean updateStatus = listdatasourcedao.updateDerivedDataTemplate(lddsUpdate);

		long idData = listdatasourcedao.getIdDataByDerivedId(Long.valueOf(idDerivedData));
		String ldsUpdate = "UPDATE listDataSources SET description='" + derivedDescription + "',updatedAt=now() WHERE idData = " + idData;
		boolean ldsUpdateStatus = listdatasourcedao.updateDerivedDataTemplate(ldsUpdate);

		if (updateStatus && ldsUpdateStatus) {
			mav.addObject("msg", "Data Template Customized Successfully");
		} else {
			mav.addObject("msg", "Unable to edit Data Template, Please check your configuration and try again.");
		}
		return mav;
	}

	@RequestMapping(value = "/populateTables", method = RequestMethod.GET)
	public @ResponseBody List populateTables(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, @RequestParam Long schemaId, @RequestParam String locationName)
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

		String selSchemaId = request.getParameter("schemaId");
		String selLocationName = request.getParameter("locationName");
		System.out.println("Schema id:" + selSchemaId);
		System.out.println("Location name:" + selLocationName);

		String projectName = projectdAO.getSelectedProject((Long) session.getAttribute("projectId")).getProjectName();
		List<String> tableListFrom = getTableListForSchema(selSchemaId, selLocationName, projectName);

		String firstElement = null;

		if (tableListFrom != null && tableListFrom.size() > 0) {
			firstElement = tableListFrom.get(0);
		} else {
			System.out.println("\n====> Zero tables fetched");
		}

		System.out.println("\n====> First table name in the list: " + firstElement);

		session.setAttribute("FirstEleFromTablesList", firstElement);

		return tableListFrom;

	}

	public List<String> getTableListForSchema(String selSchemaId, String selLocationName, String projectName) {

		System.out.println("\n====> Fetching table list for Connection Id: [" + selSchemaId + "]");
		System.out.println("Schema id:" + selSchemaId);
		System.out.println("Location name:" + selLocationName);

		List<String> tableListFrom = new ArrayList<String>();
		List<ListDataSchema> listDataSchema = DataTemplateAddNewDAO.getListDataSchema(Long.parseLong(selSchemaId));
		String hostURI = listDataSchema.get(0).getIpAddress();
		String database = listDataSchema.get(0).getDatabaseSchema();
		String userlogin = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String port = listDataSchema.get(0).getPort();
		String domain = listDataSchema.get(0).getDomain();
		String serviceName = listDataSchema.get(0).getKeytab();
		String sslEnb = listDataSchema.get(0).getSslEnb();
		String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
		String trustPassword = listDataSchema.get(0).getTrustPassword();
		String principle = listDataSchema.get(0).getDomain();
		String keytab = listDataSchema.get(0).getKeytab();
		String krb5conf = listDataSchema.get(0).getKrb5conf();
		String gatewayPath = listDataSchema.get(0).getGatewayPath();
		String jksPath = listDataSchema.get(0).getJksPath();
		String folderPath = listDataSchema.get(0).getFolderPath();
		String fileNamePattern = listDataSchema.get(0).getFileNamePattern();
		String accessKey = listDataSchema.get(0).getAccessKey();
		String secretKey = listDataSchema.get(0).getSecretKey();
		String bucketName = listDataSchema.get(0).getBucketName();
		String partitionedFolders = listDataSchema.get(0).getPartitionedFolders();
		int maxFolderDepth = listDataSchema.get(0).getMaxFolderDepth();
		String multiFolderEnabled = listDataSchema.get(0).getMultiFolderEnabled();

		String bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
		String privatekeyId = listDataSchema.get(0).getPrivatekeyId();
		String privatekey = listDataSchema.get(0).getPrivatekey();
		String clientId = listDataSchema.get(0).getClientId();
		String clientEmail = listDataSchema.get(0).getClientEmail();
		String datasetName = listDataSchema.get(0).getDatasetName();

		String azureClientId = listDataSchema.get(0).getAzureClientId();
		String azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
		String azureTenantId = listDataSchema.get(0).getAzureTenantId();
		String azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
		String azureFilePath = listDataSchema.get(0).getAzureFilePath();

		String schemaType = listDataSchema.get(0).getSchemaType();
		String schemaName = listDataSchema.get(0).getSchemaName();
		String kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();

		/*
		 * When KMS Auth is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(schemaType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(schemaName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					hostURI = conn_user_details.get("hostname");
					port = (schemaType.equalsIgnoreCase("Oracle")) ? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				System.out.println("\n====> KMS Authentication is not supported for [" + schemaType + "] !!");

			if (!flag) {
				System.out.println(
						"\n====> Unable to fetch table list, failed to get connection details from logon manager !!");
				return tableListFrom;
			}
		}

		if (selLocationName.equals("Oracle")) {
			tableListFrom = oracleconnection.getListOfTableNamesFromOracle(hostURI, userlogin, password, port,
					database);
			// System.out.println("Table name:"+tableListFromOracle.get(0));
		}
		if (selLocationName.equals("Postgres")) {
			tableListFrom = postgresConnection.getListOfTableNamesFromPostgres(hostURI, userlogin, password, port,
					database, sslEnb);
			// System.out.println("Table name:"+tableListFromOracle.get(0));
		} else if (selLocationName.equals("MYSQL")) {
			tableListFrom = mysqlconnection.getListOfTableNamesFromMYSql(hostURI, userlogin, password, port, database, sslEnb);
		} else if (selLocationName.equals("MSSQL")) {
			tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSql(hostURI, userlogin, password, port, database);
		} else if (selLocationName.equals("Teradata")) {
			tableListFrom = teradataConnection.getListOfTableNamesFromTeradata(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equals("Vertica")) {
			tableListFrom = verticaconnection.getListOfTableNamesFromVertica(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equalsIgnoreCase("MSSQLActiveDirectory")) {
			tableListFrom = msSqlActiveDirectoryConnectionObject.getListOfTableNamesFromMsSqlActiveDirectory(hostURI,
					userlogin, password, port, domain, database);
		} else if (selLocationName.equalsIgnoreCase("Amazon Redshift")) {
			tableListFrom = amazonRedshiftConnection.getListOfTableNamesFromAmazonRedshift(hostURI, userlogin, password,
					port, domain, database);
		} else if (selLocationName.equalsIgnoreCase("Cassandra")) {
			tableListFrom = cassandraconnection.getListOfTableNamesFromCassandra(hostURI, userlogin, password, port,
					database, domain);
		} else if (selLocationName.equalsIgnoreCase("Oracle RAC")) {
			tableListFrom = OracleRACConnection.getListOfTableNamesFromOracleRAC(hostURI, userlogin, password, port,
					database, domain, serviceName);
		} else if (selLocationName.equalsIgnoreCase("Hive") || selLocationName.equalsIgnoreCase("Hive Kerberos")
				|| selLocationName.equalsIgnoreCase("ClouderaHive") || selLocationName.equalsIgnoreCase("MapR Hive")) {
			boolean isKerberosEnabled = selLocationName.equals("Hive Kerberos") ? true : false;
			// Add special condition for MapR hive
			if (selLocationName.equalsIgnoreCase("MapR Hive")) {
				// Get DomainId and Domain Name of the connection, under which it is created.
				Integer domainId = listDataSchema.get(0).getDomainId();
				String domainName = iTaskDAO.getDomainNameById(domainId.longValue());

				ListDataSchema ldd= listDataSchema.get(0);
				String clusterPropertyCategory= ldd.getClusterPropertyCategory();
				System.out.println("\n====>clusterPropertyCategory:"+clusterPropertyCategory);
				long idDataSchema = ldd.getIdDataSchema();

				if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
						&& !clusterPropertyCategory.equalsIgnoreCase("cluster")){
					String token= remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

					StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
					encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

					String encryptedToken = encryptor.encrypt(token);
					ldd.setDomainName(domainName);
					tableListFrom = remoteClusterAPIService.getListOfTableNamesByRemoteCluster(ldd,encryptedToken);
				}

				else
					tableListFrom = hiveconnection.getListOfTableNamesFromMapRHive(hostURI, database, userlogin, password,
							port, domainName);
			} else {
				tableListFrom = hiveconnection.getListOfTableNamesFromHive(selLocationName, hostURI, database,
						userlogin, password, port, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
						krb5conf, principle, jksPath);
			}
		} else if (selLocationName.equalsIgnoreCase("Hive knox")) {
			tableListFrom = hiveconnection.getListOfTableNamesFromHiveKnox(hostURI, database, userlogin, password, port,
					jksPath, gatewayPath);

		} else if (selLocationName.equals("SnowFlake")) {
			tableListFrom = snowFlakeConnection.getListOfTableNamesFromSnowflake(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equalsIgnoreCase("FileSystem Batch")) {
			tableListFrom = batchFileSystem.getListOfTableNamesFromFolder(folderPath, fileNamePattern);
		} else if (selLocationName.equalsIgnoreCase("S3 Batch")) {
			tableListFrom = s3BatchConnection.getListOfTableNamesFromFolder(accessKey, secretKey, bucketName,
					folderPath, fileNamePattern, multiFolderEnabled);
		} else if (selLocationName.equalsIgnoreCase("S3 IAMRole Batch")) {
			tableListFrom = s3BatchConnection.getListOfTableNamesInFolderForS3IAMRole(bucketName, folderPath,
					fileNamePattern, partitionedFolders, maxFolderDepth);
		} else if (selLocationName.equalsIgnoreCase("BigQuery")) {
			tableListFrom = bigQueryConnection.getListOfTableNamesFromBigQuery(bigQueryProjectName, privatekeyId,
					privatekey, clientId, clientEmail, datasetName);
		} else if (selLocationName.equalsIgnoreCase("AzureSynapseMSSQL")) {
			tableListFrom = azureConnection.getListOfTableNamesFromAzure(hostURI, port, database, userlogin, password);
		} else if (selLocationName.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
			tableListFrom = azureDataLakeConnection.getListOfFilesFromDataLake(azureClientId, azureClientSecret,
					azureTenantId, azureServiceURI, azureFilePath);
		}else if (selLocationName.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			tableListFrom = azureDataLakeGen2Connection.getFolderListFromDataLake(accessKey,secretKey,bucketName,
					folderPath) ;
		}
		/*
		 * else if (selLocationName.equalsIgnoreCase("C_Hive")) { tableListFrom =
		 * hiveconnection.getListOfTableNamesFromHive(hostURI, database, userlogin,
		 * password, port); }
		 */
		return tableListFrom;
	}

	public String generateDataDefinitionDataForS3(Map<String, String> columnMetaData, HttpSession session,
			Map<String, NumericalAnalysisData> numericalData, Map<String, StringAnalysisData> stringData,
			Map<String, DateAnalysisData> dateData, String selTableName) throws Exception {

		List<ListDataDefinition> lstDataDefinition = new ArrayList<ListDataDefinition>();
		Map<String, List<ListDataDefinition>> mapDataDefinition = new HashMap<String, List<ListDataDefinition>>();
		String htmlData = null;
		try {

			// Based on metadata, identify string, date and numerical columns.
			int i = 0;
			Map<String, Double> partitionKeyMap = new HashMap<String, Double>();
			for (Entry<String, String> e : columnMetaData.entrySet()) {
				String columnName = e.getKey();
				String columnType = e.getValue();
				ListDataDefinition dataDefinition = new ListDataDefinition();

				lstDataDefinition.add(dataDefinition);

				dataDefinition.setColumnName(columnName);
				dataDefinition.setDisplayName(columnName);
				dataDefinition.setFormat(columnType);
				dataDefinition.setPartitionBy("N");

				dataDefinition.setNumericalThreshold(3.0);
				dataDefinition.setStringStatThreshold(3.0);
				dataDefinition.setNullCountThreshold(3.0);

				dataDefinition.setKBE("Y");
				dataDefinition.setDupkey("N");
				dataDefinition.setMeasurement("N");
				dataDefinition.setBlend("");
				dataDefinition.setIdDataSchema(0);
				dataDefinition.setHashValue("N");
				dataDefinition.setNumericalStat("N");
				dataDefinition.setRecordAnomaly("N");
				dataDefinition.setDgroup("N");
				dataDefinition.setDataDrift("N");
				dataDefinition.setStringStat("N");
				dataDefinition.setTimelinessKey("N");
				dataDefinition.setDefaultCheck("N");
				dataDefinition.setDefaultValues("");
				dataDefinition.setPatternCheck("N");
				dataDefinition.setPatterns("");
				dataDefinition.setbadData("N");

				// changes for length check & length val
				dataDefinition.setLengthCheck("N");
				dataDefinition.setLengthValue("0");

				dataDefinition.setMaxLengthCheck("N");

				dataDefinition.setDateFormat("");
				// dataDefinition.setLengthCheck("N");

				boolean isNumeric = false;
				boolean isString = false;
				boolean isDate = false;
				boolean isPrimaryKey = false;

				boolean isGroupBy = false;
				boolean isNullCheck = false;
				boolean isStringStat = false;

				if (numericalData.containsKey(columnName)) {
					isNumeric = true;
				} else if (stringData.containsKey(columnName)) {
					isString = true;
				} else if (dateData.containsKey(columnName)) {
					isDate = true;
				}

				if (isDate) {
					dataDefinition.setDupkey("N");
				}
				ArrayList<String> RegexList = null;
				if (isString) {
					dataDefinition.setDupkey("Y");
					StringAnalysisData stringAnalysisData = stringData.get(columnName);
					// List<String> strList = stringDataMap.get(columnName);

					// RegexList = PatternMatchUtility.patternGeneration(strList);

					/*
					 * if(RegexList.size() > 0){ dataDefinition.setPatternCheck("Y"); //StringJoiner
					 * patternsdupSj = new StringJoiner(","); StringBuilder sb = new
					 * StringBuilder(); for (String Regexs : RegexList) {
					 * System.out.println("pattern>"+Regexs); sb.append(Regexs); sb.append(",");
					 * 
					 * //patternsdupSj.add(Regexs); //dataDefinition.setPatterns(Regexs); }
					 * 
					 * String patternsdupSj = sb.deleteCharAt(sb.length() - 1).toString();
					 * 
					 * System.out.println("Pattrns:"+patternsdupSj);
					 * dataDefinition.setPatterns(patternsdupSj); }
					 */
					// 3
					partitionKeyMap.put(columnName, Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()));
					if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) < 5) {
						isGroupBy = true;
						// 1
						if (stringData.containsKey(columnName)) {
							dataDefinition.setDataDrift("Y");
						}
					} else if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) < 20) {
						isStringStat = true;
					} else if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) == 100) {
						isPrimaryKey = true;
					}
					if (Double.parseDouble(stringAnalysisData.getPercentOfNullValues()) < 25) {
						isNullCheck = true;
					}

					// ---length Check--
					double min = Double.parseDouble(stringAnalysisData.getMinLength());
					double max = Double.parseDouble(stringAnalysisData.getMaxLength());
					if (min == max) {
						dataDefinition.setLengthCheck("Y");
						dataDefinition.setMaxLengthCheck("Y");
					}
					// -----------------

				}

				if (isNumeric) {
					NumericalAnalysisData numAnalysisData = numericalData.get(columnName);

					// D2
					if (columnType.equalsIgnoreCase("decimal") || columnType.equalsIgnoreCase("numeric")
							|| columnType.equalsIgnoreCase("REAL") || columnType.equalsIgnoreCase("float")
							|| columnType.equalsIgnoreCase("double")) {
						dataDefinition.setDupkey("Y");
						dataDefinition.setNumericalStat("Y");
						isNumeric = true;
					} else if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100) {
						dataDefinition.setDupkey("N");
					} else {
						dataDefinition.setDupkey("Y");
					}
					if (!(Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100)
							&& !(Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) < 20)) {
						dataDefinition.setNumericalStat("Y");
						isNumeric = true;
					} else {
						dataDefinition.setNumericalStat("N");
						isNumeric = true;
					}
					// 3
					partitionKeyMap.put(columnName, Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()));
					if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) <= 5) {
						isGroupBy = true;
					}
					if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100) {
						isPrimaryKey = true;
					}
					// ---length Check--

					if (columnType.equalsIgnoreCase("number") || columnType.equalsIgnoreCase("integer")
							|| columnType.equalsIgnoreCase("int") || columnType.equalsIgnoreCase("bigint")
							|| columnType.equalsIgnoreCase("smallint") || columnType.equalsIgnoreCase("int4")) {
						double min = Double.parseDouble(numAnalysisData.getMinLength());
						double max = Double.parseDouble(numAnalysisData.getMaxLength());
						if (min == max) {
							dataDefinition.setLengthCheck("Y");
							dataDefinition.setMaxLengthCheck("Y");
						}
					}

					// -----------------
				}

				if (isDate) {
					DateAnalysisData dateAnalysisData = dateData.get(columnName);
					if (Double.parseDouble(dateAnalysisData.getPercentOfNullValues()) < 25) {
						isNullCheck = true;
					}
				}

				dataDefinition.setIdColumn(i);
				if (columnName.contains(".")) {
					String[] split = columnName.split("\\.");
					columnName = split[1];
				}
				if (isNumeric && !isGroupBy && !isPrimaryKey) {
					dataDefinition.setRecordAnomaly("Y");
					dataDefinition.setDgroup("N");
					dataDefinition.setDataDrift("N");
				} else if (isNumeric && isGroupBy) {
					dataDefinition.setNumericalStat("N");
					dataDefinition.setRecordAnomaly("N");
					dataDefinition.setDgroup("Y");
				}
				dataDefinition.setIncrementalCol("N");
				/*
				 * if (isDate) { dataDefinition.setIncrementalCol("Y"); }
				 */
				if (isNullCheck) {
					dataDefinition.setNonNull("Y");
				} else {
					dataDefinition.setNonNull("N");
				}

				if ((isNumeric || isString) && isPrimaryKey) {
					dataDefinition.setPrimaryKey("Y");
				} else {
					dataDefinition.setPrimaryKey("N");
				}

				dataDefinition.setRecordAnomalyThreshold(3.0);

				if (isString && isGroupBy) {
					dataDefinition.setDgroup("Y");
					dataDefinition.setDataDrift("Y");
					dataDefinition.setStringStat("N");
				} else if (isString && isStringStat) {
					dataDefinition.setStringStat("Y");
				}
				dataDefinition.setDataDriftThreshold(3.0);
				dataDefinition.setIsMasked("N");
			}

			System.out.println("Data Definition size:" + lstDataDefinition.size());
			// logic PartitionKey
			String partitionColumn = getPartitionValueFromMap(partitionKeyMap);
			for (ListDataDefinition listDataDefinition : lstDataDefinition) {
				if (listDataDefinition.getColumnName().equalsIgnoreCase(partitionColumn)) {
					listDataDefinition.setPartitionBy("Y");
				} else {
					listDataDefinition.setPartitionBy("N");
				}
			}

			dataAlgorithService.refineGroupByColumns(numericalData, stringData, lstDataDefinition);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Problem reading table metadata..");
		}

		if (session.getAttribute("dataDefinition") != null) {
			mapDataDefinition = (Map<String, List<ListDataDefinition>>) session.getAttribute("dataDefinition");
		}
		mapDataDefinition.put(selTableName, lstDataDefinition);
		session.setAttribute("dataDefinition", mapDataDefinition);
		Gson gson = new Gson();
		htmlData = gson.toJson(lstDataDefinition);
		return htmlData;
	}

	// condition For PartitionKey
	public static String getMaxValueFromMap(Map<String, Double> partitionKeyMap) {
		HashMap<String, Double> subsegMap = new HashMap<String, Double>();
		String maxGroupBy = null;
		Double maxValue = 0.0;
		for (Map.Entry<String, Double> entry : partitionKeyMap.entrySet()) {
			if (entry.getValue() <= 0.05) {
				subsegMap.put(entry.getKey(), entry.getValue());
			}
		}
		Double maxValue2 = 0.0;
		if (!subsegMap.isEmpty()) {
			for (Map.Entry<String, Double> entry : subsegMap.entrySet()) {
				if (entry.getValue() > maxValue2) {
					maxGroupBy = entry.getKey();
					maxValue2 = entry.getValue();
				}
			}
			return maxGroupBy;
		} else {
			Entry<String, Double> min = null;
			for (Entry<String, Double> entry : partitionKeyMap.entrySet()) {
				if (min == null || min.getValue() > entry.getValue()) {
					min = entry;
				}
			}
			if (min != null) {
				maxValue = min.getValue();
				if (maxValue > 0.05 && maxValue <= 0.3) {
					return min.getKey();
				}
			}
		}
		return maxGroupBy;
	}

	public String generateDataDefinitionData(ResultSet resultSetFromDb, List<String> primaryKeys, HttpSession session,
			Map<String, NumericalAnalysisData> numericalData, Map<String, StringAnalysisData> stringData,
			Map<String, DateAnalysisData> dateData, String selTableName, Map<String, List<String>> stringDataMap)
			throws Exception {

		List<ListDataDefinition> lstDataDefinition = new ArrayList<ListDataDefinition>();
		Map<String, List<ListDataDefinition>> mapDataDefinition = new HashMap<String, List<ListDataDefinition>>();
		String htmlData = null;
		System.out.println("Inside generateDataDefinitionData");
		try {
			ResultSetMetaData metaData = resultSetFromDb.getMetaData();
			Map<String, Double> partitionKeyMap = new HashMap<String, Double>();
			// Based on metadata, identify string, date and numerical columns.
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				ListDataDefinition dataDefinition = new ListDataDefinition();
				lstDataDefinition.add(dataDefinition);
				if (columnName.contains(".")) {
					String[] split = columnName.split("\\.");
					columnName = split[1];
				}
				// String columnName = metaData.getColumnName(i);
				// String columnType = metaData.getColumnTypeName(i);
				dataDefinition.setColumnName(columnName);
				dataDefinition.setDisplayName(columnName);
				dataDefinition.setFormat(columnType);
				dataDefinition.setPartitionBy("N");

				dataDefinition.setNumericalThreshold(3.0);
				dataDefinition.setStringStatThreshold(3.0);
				dataDefinition.setNullCountThreshold(3.0);

				dataDefinition.setKBE("Y");
				dataDefinition.setDupkey("N");
				dataDefinition.setMeasurement("N");
				dataDefinition.setBlend("");
				dataDefinition.setIdDataSchema(0);
				dataDefinition.setHashValue("N");
				dataDefinition.setNumericalStat("N");
				dataDefinition.setRecordAnomaly("N");
				dataDefinition.setDgroup("N");
				dataDefinition.setDataDrift("N");
				dataDefinition.setStringStat("N");
				dataDefinition.setTimelinessKey("N");
				dataDefinition.setDefaultCheck("N");
				dataDefinition.setDefaultValues("");
				dataDefinition.setPatternCheck("N");
				dataDefinition.setPatterns("");
				dataDefinition.setbadData("N");

				// changes for length check & length val
				dataDefinition.setLengthCheck("N");
				dataDefinition.setLengthValue("0");

				dataDefinition.setMaxLengthCheck("N");

				dataDefinition.setDateFormat("");
				// dataDefinition.setLengthCheck("N");

				boolean isNumeric = false;
				boolean isString = false;
				boolean isDate = false;
				boolean isPrimaryKey = false;

				boolean isGroupBy = false;
				boolean isNullCheck = false;
				boolean isStringStat = false;

				if (numericalData.containsKey(columnName)) {
					isNumeric = true;
				} else if (stringData.containsKey(columnName)) {
					isString = true;
				} else if (dateData.containsKey(columnName)) {
					isDate = true;
				}

				if (primaryKeys.contains(columnName)) {
					isPrimaryKey = true;
				}
				if (isDate) {
					dataDefinition.setDupkey("N");
				}
				ArrayList<String> RegexList = null;
				if (isString) {
					dataDefinition.setDupkey("Y");
					StringAnalysisData stringAnalysisData = stringData.get(columnName);
					// List<String> strList = stringDataMap.get(columnName);

					// RegexList = PatternMatchUtility.patternGeneration(strList);

					/*
					 * if(RegexList.size() > 0){ dataDefinition.setPatternCheck("Y"); //StringJoiner
					 * patternsdupSj = new StringJoiner(","); StringBuilder sb = new
					 * StringBuilder(); for (String Regexs : RegexList) {
					 * System.out.println("pattern>"+Regexs); sb.append(Regexs); sb.append(",");
					 * 
					 * //patternsdupSj.add(Regexs); //dataDefinition.setPatterns(Regexs); }
					 * 
					 * String patternsdupSj = sb.deleteCharAt(sb.length() - 1).toString();
					 * 
					 * System.out.println("Pattrns:"+patternsdupSj);
					 * dataDefinition.setPatterns(patternsdupSj); }
					 */
					// 3
					partitionKeyMap.put(columnName, Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()));
					if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) < 5) {
						isGroupBy = true;
						// 1
						if (stringData.containsKey(columnName)) {
							dataDefinition.setDataDrift("Y");
						}
					} else if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) < 20) {
						isStringStat = true;
					} else if (Double.parseDouble(stringAnalysisData.getPercentOfUniqueValues()) == 100) {
						isPrimaryKey = true;
					}
					if (Double.parseDouble(stringAnalysisData.getPercentOfNullValues()) < 25) {
						isNullCheck = true;
					}

					// ---length Check--
					double min = Double.parseDouble(stringAnalysisData.getMinLength());
					double max = Double.parseDouble(stringAnalysisData.getMaxLength());
					if (min == max) {
						dataDefinition.setLengthCheck("Y");
						dataDefinition.setMaxLengthCheck("Y");
					}
					// -----------------

				}

				if (isNumeric) {
					NumericalAnalysisData numAnalysisData = numericalData.get(columnName);

					// D2
					if (columnType.equalsIgnoreCase("decimal") || columnType.equalsIgnoreCase("numeric")
							|| columnType.equalsIgnoreCase("REAL") || columnType.equalsIgnoreCase("float")
							|| columnType.equalsIgnoreCase("double")) {
						dataDefinition.setDupkey("Y");
						dataDefinition.setNumericalStat("Y");
						isNumeric = true;
					} else if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100) {
						dataDefinition.setDupkey("N");
					} else {
						dataDefinition.setDupkey("Y");
					}
					if (!(Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100)
							&& !(Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) < 20)) {
						dataDefinition.setNumericalStat("Y");
						isNumeric = true;
					} else {
						dataDefinition.setNumericalStat("N");
						isNumeric = true;
					}
					// 3
					partitionKeyMap.put(columnName, Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()));
					if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) <= 5) {
						isGroupBy = true;
					}
					if (Double.parseDouble(numAnalysisData.getPercentOfUniqueValues()) == 100) {
						isPrimaryKey = true;
					}
					// ---length Check--

					if (columnType.equalsIgnoreCase("number") || columnType.equalsIgnoreCase("integer")
							|| columnType.equalsIgnoreCase("int") || columnType.equalsIgnoreCase("bigint")
							|| columnType.equalsIgnoreCase("smallint") || columnType.equalsIgnoreCase("int4")) {
						double min = Double.parseDouble(numAnalysisData.getMinLength());
						double max = Double.parseDouble(numAnalysisData.getMaxLength());
						if (min == max) {
							dataDefinition.setLengthCheck("Y");
							dataDefinition.setMaxLengthCheck("Y");
						}
					}

					// -----------------
				}

				if (isDate) {
					DateAnalysisData dateAnalysisData = dateData.get(columnName);
					if (Double.parseDouble(dateAnalysisData.getPercentOfNullValues()) < 25) {
						isNullCheck = true;
					}
				}

				dataDefinition.setIdColumn(i);
				if (columnName.contains(".")) {
					String[] split = columnName.split("\\.");
					columnName = split[1];
				}
				if (isNumeric && !isGroupBy && !isPrimaryKey) {
					dataDefinition.setRecordAnomaly("Y");
					dataDefinition.setDgroup("N");
					dataDefinition.setDataDrift("N");
				} else if (isNumeric && isGroupBy) {
					dataDefinition.setNumericalStat("N");
					dataDefinition.setRecordAnomaly("N");
					dataDefinition.setDgroup("Y");
				}
				dataDefinition.setIncrementalCol("N");
				/*
				 * if (isDate) { dataDefinition.setIncrementalCol("Y"); }
				 */
				if (isNullCheck) {
					dataDefinition.setNonNull("Y");
				} else {
					dataDefinition.setNonNull("N");
				}

				if ((isNumeric || isString) && isPrimaryKey) {
					dataDefinition.setPrimaryKey("Y");
				} else {
					dataDefinition.setPrimaryKey("N");
				}

				dataDefinition.setRecordAnomalyThreshold(3.0);

				if (isString && isGroupBy) {
					dataDefinition.setDgroup("Y");
					dataDefinition.setDataDrift("Y");
					dataDefinition.setStringStat("N");
				} else if (isString && isStringStat) {
					dataDefinition.setStringStat("Y");
				}
				dataDefinition.setDataDriftThreshold(3.0);
				dataDefinition.setIsMasked("N");
			}

			System.out.println("Data Definition size:" + lstDataDefinition.size());
			// logic PartitionKey
			String partitionColumn = getPartitionValueFromMap(partitionKeyMap);
			for (ListDataDefinition listDataDefinition : lstDataDefinition) {
				if (listDataDefinition.getColumnName().equalsIgnoreCase(partitionColumn)) {
					listDataDefinition.setPartitionBy("Y");
				} else {
					listDataDefinition.setPartitionBy("N");
				}
			}

		} catch (SQLException e) {
			throw new Exception("Problem reading table metadata..");
		}

		dataAlgorithService.refineGroupByColumns(numericalData, stringData, lstDataDefinition);

		if (session.getAttribute("dataDefinition") != null) {
			mapDataDefinition = (Map<String, List<ListDataDefinition>>) session.getAttribute("dataDefinition");
		}
		mapDataDefinition.put(selTableName, lstDataDefinition);
		session.setAttribute("dataDefinition", mapDataDefinition);
		Gson gson = new Gson();
		htmlData = gson.toJson(lstDataDefinition);
		System.out.println("Generated data definition:" + htmlData);
		return htmlData;
	}

	// condition For PartitionKey
	public static String getPartitionValueFromMap(Map<String, Double> partitionKeyMap) {
		HashMap<String, Double> subsegMap = new HashMap<String, Double>();
		String maxGroupBy = null;
		Double minValue = 0.0;
		for (Map.Entry<String, Double> entry : partitionKeyMap.entrySet()) {
			if (entry.getValue() <= 5) {
				subsegMap.put(entry.getKey(), entry.getValue());
			}
		}
		Double maxValue2 = 0.0;
		if (!subsegMap.isEmpty()) {
			for (Map.Entry<String, Double> entry : subsegMap.entrySet()) {
				if (entry.getValue() > maxValue2) {
					maxGroupBy = entry.getKey();
					maxValue2 = entry.getValue();
				}
			}
			return maxGroupBy;
		} else {
			Entry<String, Double> min = null;
			for (Entry<String, Double> entry : partitionKeyMap.entrySet()) {
				if (min == null || min.getValue() > entry.getValue()) {
					min = entry;
				}
			}
			if (min != null) {
				minValue = min.getValue();
				if (minValue > 0.05 && minValue <= 0.3) {
					return min.getKey();
				}
			}
		}
		return null;
	}

	@RequestMapping(value = "/saveDataDefinitionData", method = RequestMethod.POST)
	public void saveDataDefinitionData(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam Long idColumn, @RequestParam String columnName, @RequestParam String columnValue,
			@RequestParam String tableName) throws IOException {
		/*
		 * Object user = session.getAttribute("user"); JSONObject json = new
		 * JSONObject(); System.out.println("user:" + user); if ((user == null) ||
		 * (!user.equals("validUser"))) { try { response.sendRedirect("loginPage.jsp");
		 * } catch (IOException e) { e.printStackTrace(); } }
		 * 
		 * if (columnValue.equalsIgnoreCase("y")) { columnValue = "Y"; } else if
		 * (columnValue.equalsIgnoreCase("n")) { columnValue = "N"; }
		 * 
		 * boolean flag = true; if (!al.contains(columnName)) { flag = false; }
		 * System.out.println("idColumn=" + idColumn); System.out.println("columnName="
		 * + columnName); System.out.println("columnValue=" + columnValue); try { if
		 * (flag) {
		 * 
		 * Long update = SchemaDAOI.updateDataIntoListDataDefinition(idColumn,
		 * columnName, columnValue);
		 * SchemaDAOI.updateKBEIntoListDataDefinition(idColumn, columnName,
		 * columnValue); System.out.println("update=" + update);
		 * 
		 * 
		 * if (update == -1) { // JSONObject json = new JSONObject();
		 * 
		 * json.put("success", "Not Allowed For This Data Type");
		 * 
		 * if (columnName.equals("lengthValue")) {
		 * System.out.println("If ...........lengthValue =>" + columnName);
		 * json.put("columnValue", "0"); } else {
		 * System.out.println("Else...........lengthValue =>" + columnName);
		 * json.put("columnValue", "N"); }
		 * 
		 * response.getWriter().println(json); } else if (update > 0) { //JSONObject
		 * json = new JSONObject(); System.out.println("update > 0 ................ =>"
		 * + columnName); json.put("success", "Item Updated Successfully");
		 * json.put("columnValue", columnValue); response.getWriter().println(json); }
		 * else {
		 * 
		 * //JSONObject json = new JSONObject();
		 * 
		 * json.put("success", "Item failed to Update");
		 * response.getWriter().println(json); } } else { //JSONObject json = new
		 * JSONObject(); json.put("fail", "Please write Y for Yes and N for No");
		 * response.getWriter().println(json); }
		 */

		Object user = session.getAttribute("user");
		JSONObject json = new JSONObject();
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (columnValue.equalsIgnoreCase("y")) {
			columnValue = "Y";
		} else if (columnValue.equalsIgnoreCase("n")) {
			columnValue = "N";
		}

		boolean flag = true;
		if (!al.contains(columnName)) {
			flag = false;
		}
		System.out.println("idColumn=" + idColumn);
		System.out.println("columnName=" + columnName);
		System.out.println("columnValue=" + columnValue);
		try {
			if (flag) {
				int update = 0;
				Map<String, List<ListDataDefinition>> mapDataDefinition = (Map<String, List<ListDataDefinition>>) session
						.getAttribute("dataDefinition");
				List<ListDataDefinition> lstDataDefinition = mapDataDefinition.get(tableName);
				ListDataDefinition dataDefinition = lstDataDefinition.get((int) (idColumn - 1));
				try {
					Class<?> aClass = Class.forName("com.databuck.bean.ListDataDefinition");
					columnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					String methodName = "set" + columnName;
					Method method = aClass.getMethod(methodName, String.class);
					System.out.println("dataDefinition=" + dataDefinition);
					System.out.println("columnValue=" + columnValue);
					method.invoke(dataDefinition, columnValue);
					update = 1;
				} catch (Exception e) {
					json.put("success", "Item failed to Update");
					response.getWriter().println(json);
				}
				System.out.println("update=" + update);
				if (update > 0) {
					json.put("success", "Item Updated Successfully");
					json.put("columnValue", columnValue);
					response.getWriter().println(json);
				} else {
					json.put("fail", "Item failed to Update");
					response.getWriter().println(json);
				}
			} else {
				json.put("fail", "This value is not editable.");
				response.getWriter().println(json);
			}
		} catch (IOException e) {
			json.put("fail", "Problem updating value.");
			response.getWriter().println(json);
		}
	}

	/**
	 * get table columns from json Object
	 * 
	 * @param file
	 * @return
	 */
	Map<String, String> getTableColumns(MultipartFile file) {
		Map<String, String> columns = new HashMap();

		// parse json objet file
		JSONObject obj;
		try {

			JSONTokener tokener = new JSONTokener(new InputStreamReader(file.getInputStream(), "UTF-8"));
			// parse JSON object
			obj = new JSONObject(tokener);

			obj.keySet().forEach(keyStr -> {
				String keyvalue = obj.get(keyStr).toString();
				System.out.println("key: " + keyStr + " value: " + keyvalue);
				columns.put(keyStr, keyvalue);

				// for nested objects iteration if required
				// if (keyvalue instanceof JSONObject)
				// printJsonObject((JSONObject)keyvalue);
			});

			System.out.println(" columns " + columns);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columns;
	}

	@RequestMapping(value = "/globalThreshold", method = RequestMethod.GET)
	public ModelAndView globalThreshold(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Global Rule", "C", session);
		if (rbac) {
			List<listGlobalThresholds> listGlobalThresholds = globalthresholddao.view();
			List<Domain> aListDomains = globalruledao.getDomainList();

			/*
			 * Pradeep 18-Mar-2020 View data going to browser so changes in HTML can be
			 * managed
			 */
			try {
				ObjectMapper oMapper = new ObjectMapper();
				String slistGlobalThresholds = oMapper.writeValueAsString(listGlobalThresholds);
				DateUtility.DebugLog("globalThreshold 01",
						String.format("globalThreshold json list\n%1$s\n", slistGlobalThresholds));
			} catch (Exception oException) {
				oException.printStackTrace();
			}

			ModelAndView modelAndView = new ModelAndView("globalThresholdView");
			modelAndView.addObject("listGlobalThresholds", listGlobalThresholds);
			modelAndView.addObject("aListDomains", aListDomains);
			modelAndView.addObject("currentSection", "Global Rule");
			modelAndView.addObject("currentLink", "Global Thresholds");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/addReferences")
	public ModelAndView addReferences(HttpServletRequest request, HttpSession session) throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		request.setAttribute("currentSection", "Global Rule");
		request.setAttribute("currentLink", "Referencesfile");
		return new ModelAndView("addReferencesFile");
	}

	@RequestMapping(value = "/refdatatemplateview", method = RequestMethod.GET)
	public ModelAndView refdatatemplateview(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		System.out.println("In dataTemplate view###############################");
		String projectId = request.getParameter("projectId");
		String domainId = request.getParameter("domainId");
		List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Global Rule", "R", session);
		if (rbac) {
			List<ListDataSource> listDataSource = listdatasourcedao.getListDataSourceTableForRef(projectId,domainId);
			//List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForRef(projList);
			// listdatasourcedao.getTableNameFromListDataAccess(listdatasource.get(0).getIdData());
			model.addObject("listdatasource", listDataSource);
			model.setViewName("referenceData");
			model.addObject("currentSection", "Global Rule");
			model.addObject("currentLink", "References View");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/refData", method = RequestMethod.GET)
	public ModelAndView refData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "R", session);
		if (rbac) {
			System.out.println("idData=" + request.getParameter("idData"));
			Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("view listDataView  id " + idData);
			String dataLocation = request.getParameter("dataLocation");
			System.out.println("dataLocation=" + dataLocation);

			List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
			System.out.println(listdatadefinition.size());
			ModelAndView model = new ModelAndView("refDataView");
			model.addObject("idData", idData);
			model.addObject("currentSection", "Extend Template & Rule");
			model.addObject("currentLink", "References View");
			model.addObject("listDataDefinitionData", listdatadefinition);
			// model.addObject("sumeet",listdatadefinition.get(0).getPattern());
			String tableName = request.getParameter("name");
			model.addObject("name", tableName);
			return model;

		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/updateColumnCheckThreshold", method = RequestMethod.POST)
	public void updateColumnCheckThreshold(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long idApp, @RequestParam String checkName,
			@RequestParam String column_or_rule_name, @RequestParam double failed_Threshold, @RequestParam String defaultPattern) {
		System.out.println("\n====> updateColumnCheckThreshold - START ");
		System.out.println("idApp => " + idApp + ", checkName => " + checkName + ", columnName => " + column_or_rule_name
				+ ", failed_Threshold => " + failed_Threshold);
		String status = "failed";
		String message = "";
		JSONObject json = new JSONObject();
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			json = dataTemplateDeltaCheckService.updateColumnCheckThreshold(idApp, checkName, column_or_rule_name,
					failed_Threshold, defaultPattern);
			status = json.getString("status");
			message = json.getString("message");

		} catch (Exception e) {
			message = "Error Occurred, failed to update Pattern";
			e.printStackTrace();
		}

		try {
			json.put("status", status);
			json.put("message", message);
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n====> updateDataPatternCheckPattern - END ");
	}


@RequestMapping(value = "/updateRecordCountAnomalyThreshold", method = RequestMethod.POST)
public void updateRecordCountAnomalyThreshold(HttpServletRequest request, HttpServletResponse response,
		HttpSession session, @RequestParam long idApp, @RequestParam String checkName, @RequestParam double failed_Threshold) {
	System.out.println("\n====> updateRecordCountAnomalyThreshold - START ");
	System.out.println("idApp => " + idApp + ", checkName => " + checkName + ", failed_Threshold => " + failed_Threshold);
	String status = "failed";
	String message = "";
	JSONObject json = new JSONObject();
	try {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		json = dataTemplateDeltaCheckService.updateRecordCountAnomalyThreshold(idApp, checkName,
				failed_Threshold);
		status = json.getString("status");
		message = json.getString("message");

	} catch (Exception e) {
		message = "Error Occurred, failed to update Threshold";
		e.printStackTrace();
	}

	try {
		json.put("status", status);
		json.put("message", message);
		response.getWriter().println(json);
	} catch (Exception e) {
		e.printStackTrace();
	}
	System.out.println("\n====> updateDataCheckThreshold - END ");
}
	@RequestMapping(value = "/getProfileDataForTemplate", method = RequestMethod.POST)
	public void getProfileDataForTemplate(@RequestHeader HttpHeaders headers, @RequestParam long idData, HttpServletResponse oResponse) {
		JSONObject result = new JSONObject();
		String status = "failed";
		System.out.println("\n======> getProfileDataForTemplate - START <======");

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			JSONObject sourceResults = dataTemplateDeltaCheckService.getProfileDataForTemplate(idData);
			result.put("status", sourceResults.getString("status"));
			result.put("message", sourceResults.getString("message"));
			result.put("result", sourceResults.getJSONObject("result"));

		} catch (Exception e) {
			result.put("message", "Request failed");
			result.put("status", status);
			e.printStackTrace();
		}

		try {
			oResponse.getWriter().println(result);
			oResponse.getWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/validateQueryForTemplate", method = RequestMethod.POST)
	public void getProfileDataForTemplate(@RequestHeader HttpHeaders headers, @RequestBody ValidateQuery validateQuery, HttpServletResponse oResponse) {
		JSONObject result = new JSONObject();
		String status = "failed";
		System.out.println("\n======> getProfileDataForTemplate - START <======");

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			JSONObject sourceResults = dataTemplateDeltaCheckService.validateQueryForTemplate(validateQuery);
			result.put("status", sourceResults.getString("status"));
			result.put("message", sourceResults.getString("message"));
			result.put("result", sourceResults.getJSONObject("result"));

		} catch (Exception e) {
			result.put("message", "Request failed");
			result.put("status", status);
			e.printStackTrace();
		}

		try {
			oResponse.getWriter().println(result);
			oResponse.getWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

