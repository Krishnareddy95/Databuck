package com.databuck.restcontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.AgingIssuesForValidationReq;
import com.databuck.bean.AppGroupJobDTO;
import com.databuck.bean.AppGroupMapping;
import com.databuck.bean.ColumnAggregateRequest;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainJobDTO;
import com.databuck.bean.DomainLiteJobDTO;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.bean.ListAdvancedRules;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.MappingDetail;
import com.databuck.bean.Project;
import com.databuck.bean.ProjectJobDTO;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobDTO;
import com.databuck.bean.ValidationRunDTO;
import com.databuck.bean.listDataAccess;
import com.databuck.constants.DatabuckConstants;
import com.databuck.controller.JSONController;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IExtendTemplateRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.MappingDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.impl.ValidationCheckDAOImpl;
import com.databuck.dto.CheckProjectJobStatusByIdResponse;
import com.databuck.dto.ConnectionDto;
import com.databuck.dto.ConnectionInfoDto;
import com.databuck.dto.ConnectionforProjectDto;
import com.databuck.dto.DQmetricdto;
import com.databuck.dto.DashboardResultByIdDto;
import com.databuck.dto.DashboardResultByIdResponse;
import com.databuck.dto.DataTemplateforSchemaDto;
import com.databuck.dto.GenericResponse;
import com.databuck.dto.GenericRestResponse;
import com.databuck.dto.GetAdvancedRulesDto;
import com.databuck.dto.GetAdvancedRulesResponse;
import com.databuck.dto.GetAgingIssuesForValidationDto;
import com.databuck.dto.GetAllProjectsResponseDto;
import com.databuck.dto.GetAllValidationChecksDto;
import com.databuck.dto.GetAllValidationChecksResponse;
import com.databuck.dto.GetDataConnectionsForProjectResponse;
import com.databuck.dto.GetDataTemplateByIdDto;
import com.databuck.dto.GetDataTemplateByIdResponse;
import com.databuck.dto.GetDataTemplatesForSchemaResponse;
import com.databuck.dto.GetDomainToProjectMappingResponse;
import com.databuck.dto.GetMetadataResponse;
import com.databuck.dto.GetMetadataResponseDto;
import com.databuck.dto.GetProjectJobHistoryResponse;
import com.databuck.dto.GetTemplatesColumnChangesForProjectDto;
import com.databuck.dto.GetTemplatesColumnChangesForProjectResponse;
import com.databuck.dto.GetValidationCheckByIdResponse;
import com.databuck.dto.ProjectDto;
import com.databuck.dto.ProjectJobHistoryDto;
import com.databuck.dto.ProjectResponse;
import com.databuck.dto.RunAppGroupByNameResponse;
import com.databuck.dto.RunDomainDto;
import com.databuck.dto.RunProjectByNameResponse;
import com.databuck.dto.RunProjectResponse;
import com.databuck.dto.RunSchemaResponse;
import com.databuck.dto.RunValidationResponse;
import com.databuck.dto.TemplateDto;
import com.databuck.dto.TemplateInfoDto;
import com.databuck.dto.ValidationCheckByIdDto;
import com.databuck.econstants.TemplateRunTypes;
import com.databuck.service.AuthorizationService;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.ITaskService;
import com.databuck.service.RuleCatalogService;
import com.databuck.service.RunningJobService;
import com.databuck.util.DatabuckFileUtility;
import com.databuck.util.TokenValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckRestAPIController {

	@Autowired
	DatabuckRestDAOImpl databuckRestDAOImpl;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	IExtendTemplateRuleDAO iExtendTemplateRuleDAO;

	@Autowired
	JSONController Jsoncontroller;

	@Autowired
	IListDataSourceDAO listdatasourcedao;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	ValidationCheckDAOImpl validationCheckDAOImpl;

	@Autowired
	IProjectDAO projectDAO;

	@Autowired
	IResultsDAO iResultsDAO;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	public ITaskService iTaskService;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private RunningJobService runningJobService;

	@Autowired
	private IProjectDAO iprojectDao;

	@Autowired
	private GlobalRuleDAO globalRuleDao;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private SchemaDAOI schemaDao;

	@Autowired
	private DataProfilingTemplateService dataProilingTemplateService;

	@Autowired
	private TokenValidator tokenValidator;

	@Autowired
	private MappingDAO mappingDAO;

	private static final Logger LOG = Logger.getLogger(DatabuckRestAPIController.class);

	@ApiOperation(value = "Get all products", notes = "This API is used to get the list of all the projects created in Databuck.", tags = "Project")
	@RequestMapping(value = "restapi/project/getAllProjects", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public GetAllProjectsResponseDto getAllProjects(HttpServletResponse response, HttpServletRequest request) {
		LOG.info("restapi/project/getAllProjects - START");
		GetAllProjectsResponseDto getAllProjectsResponseDto = new GetAllProjectsResponseDto();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				List<Project> projectList = projectDAO.getAllProjects();

				if (projectList != null && projectList.size() > 0) {

					// Convert result to json string
					ArrayList<ProjectResponse> newProjectList = new ArrayList<ProjectResponse>();
					for (Project project : projectList) {
						ProjectResponse obj = new ProjectResponse();
						obj.setIdProject(project.getIdProject());
						obj.setProjectName(project.getProjectName());
						obj.setProjectDescription(project.getProjectDescription());
						newProjectList.add(obj);
					}
					getAllProjectsResponseDto.setResult(newProjectList);
					return getAllProjectsResponseDto;
				} else {
					LOG.error("No projects found ");
					getAllProjectsResponseDto.setFail("No projects found ");
				}
			} else {
				LOG.error("Invalid Authorization");
				getAllProjectsResponseDto.setFail("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			getAllProjectsResponseDto.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/project/getAllProjects - END");
		return getAllProjectsResponseDto;
	}

	@ApiOperation(value = "Get domain to project mappings", notes = "The API is to get the list of Domains and its associated Project "
			+ "details", tags = "Domain")
	@ResponseBody
	@RequestMapping(value = "restapi/project/getDomainToProjectMapping", method = RequestMethod.GET, produces = "application/json")
	public GenericRestResponse<ArrayList<GetDomainToProjectMappingResponse>> getDomainToProjectMapping(
			HttpServletResponse response, HttpServletRequest request) {
		LOG.info(" restapi/project/getDomainToProjectMapping - START");
		GenericRestResponse<ArrayList<GetDomainToProjectMappingResponse>> json = new GenericRestResponse<ArrayList<GetDomainToProjectMappingResponse>>();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {

				// Get list of Domains
				List<Domain> domainsList = projectDAO.getAllDomain();
				ArrayList<GetDomainToProjectMappingResponse> outputjsonArray = new ArrayList<GetDomainToProjectMappingResponse>();

				if (domainsList != null && domainsList.size() > 0) {

					for (Domain domain : domainsList) {
						GetDomainToProjectMappingResponse dmn_json_obj = new GetDomainToProjectMappingResponse();
						dmn_json_obj.setDomainId(domain.getDomainId());
						dmn_json_obj.setDomainName(domain.getDomainName());

						// Get list of Projects for each domain
						List<Project> projectList = projectDAO.getAllProjectsForDomain(domain.getDomainId());

						ArrayList<ProjectDto> prj_json_list = new ArrayList<ProjectDto>();
						if (projectList != null && projectList.size() > 0) {

							for (Project project : projectList) {
								ProjectDto prj_json = new ProjectDto();
								prj_json.setProjectId(project.getIdProject());
								prj_json.setProjectName(project.getProjectName());
								prj_json.setProjectDescription(project.getProjectDescription());
								prj_json_list.add(prj_json);
							}
						}
						dmn_json_obj.setAssociatedProjects(prj_json_list);
						outputjsonArray.add(dmn_json_obj);
					}
				}
				json.setResult(outputjsonArray);
				return json;
			}
			json.setFail("Invalid Authorization");
			LOG.error("Invalid Authorization ");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			json.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info(" restapi/project/getDomainToProjectMapping - END ");
		return json;
	}

	@ApiOperation(value = "Get data connection by project id", notes = "This API is used to get the list of all the connections that are created under any particular project created in Databuck. "
			+ "New field \"isActive\" with values (yes/no) is added in the response. ", tags = "Connection")
	@ResponseBody
	@RequestMapping(value = "restapi/dataconnection/getDataConnectionsForProject", method = RequestMethod.GET, produces = "application/json")
	public GetDataConnectionsForProjectResponse getDataConnectionsForProject(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam @ApiParam(name = "projectId", value = "Project Id", example = "1") long projectId) {
		LOG.info("restapi/dataconnection/getDataConnectionsForProject - START");

		GetDataConnectionsForProjectResponse getDataConnectionsForProjectResponse = new GetDataConnectionsForProjectResponse();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {

				Project project = projectDAO.getSelectedProject(projectId);

				if (project != null) {
					List<Project> projectList = new ArrayList<Project>();
					projectList.add(project);

					List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId, projectList,
							"", "");

					if (listdataschema != null && listdataschema.size() > 0) {
						ArrayList<ConnectionforProjectDto> arrayList = new ArrayList<ConnectionforProjectDto>();
						for (ListDataSchema lds : listdataschema) {
							ConnectionforProjectDto obj = new ConnectionforProjectDto();
							obj.setIdDataSchema(lds.getIdDataSchema());
							obj.setSchemaName(lds.getSchemaName());
							obj.setSchemaType(lds.getSchemaType());
							obj.setIpAddress(lds.getIpAddress());
							obj.setDatabaseSchema(lds.getDatabaseSchema());
							obj.setPort(lds.getPort());
							obj.setIsActive(lds.getAction());
							arrayList.add(obj);
						}
						getDataConnectionsForProjectResponse.setResult(arrayList);
					} else {
						LOG.error("No DataConnections found for this Project");
						getDataConnectionsForProjectResponse.setFail("No DataConnections found for this Project");
					}
				} else {
					LOG.error("Invalid ProjectId");
					getDataConnectionsForProjectResponse.setFail("Invalid ProjectId");
				}
			} else {
				LOG.error("Invalid Authorization");
				getDataConnectionsForProjectResponse.setFail("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			getDataConnectionsForProjectResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/dataconnection/getDataConnectionsForProject - END");
		return getDataConnectionsForProjectResponse;
	}

	@ApiOperation(value = "Activates data connection by schema id", notes = "This API is used to activate a disabled or deactivated connection.", tags = "Connection")
	@ResponseBody
	@RequestMapping(value = "restapi/dataconnection/activateConnection", method = RequestMethod.GET, produces = "application/json")
	public GenericResponse<String> activateConnection(HttpServletResponse response, HttpServletRequest request,
			HttpSession session,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema) {
		LOG.info("restapi/dataconnection/activateConnection - START");

		String status = "failed";
		String message = "";

		GenericResponse<String> json = new GenericResponse<String>();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				LOG.debug(" idDataSchema : " + idDataSchema);
				// Fetch Details of idDataSchema
				List<ListDataSchema> schemaList = listdatasourcedao.getListDataSchemaId(idDataSchema);

				if (schemaList != null && !schemaList.isEmpty()) {

					ListDataSchema listDataSchema = schemaList.get(0);

					// Check if the connection is already active
					if (listDataSchema != null) {

						if (listDataSchema.getAction() != null
								&& listDataSchema.getAction().trim().equalsIgnoreCase("YES")) {
							message = "Connection is already active";
							LOG.info(message);
						} else {
							boolean activationStatus = listdatasourcedao.activateSchema(idDataSchema);
							if (activationStatus) {
								status = "success";
								message = "Connection activated successfully";
								LOG.info(message);
							} else
								message = "Connection activation failed";
							LOG.error(message);
						}

					} else {
						message = "Invalid SchemaId";
						LOG.error(message);
					}

				} else {
					message = "Invalid SchemaId";
					LOG.error(message);
				}
			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "Request failed";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/dataconnection/activateConnection - END");
		return json;
	}

	@ApiOperation(value = "Deactivate Connection by schema id", notes = "This API is used to deactivate a active connection.", tags = "Connection")
	@ResponseBody
	@RequestMapping(value = "restapi/dataconnection/deactivateConnection", method = RequestMethod.GET, produces = "application/json")
	public GenericResponse<String> deactivateConnection(HttpServletResponse response, HttpServletRequest request,
			HttpSession session,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema) {
		LOG.info("restapi/dataconnection/deactivateConnection - START");

		String status = "failed";
		String message = "";

		GenericResponse<String> json = new GenericResponse<String>();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {

				// Fetch Details of idDataSchema
				List<ListDataSchema> schemaList = listdatasourcedao.getListDataSchemaId(idDataSchema);

				if (schemaList != null && !schemaList.isEmpty()) {

					ListDataSchema listDataSchema = schemaList.get(0);

					// Check if the connection is already active
					if (listDataSchema != null) {

						if (listDataSchema.getAction() != null
								&& listDataSchema.getAction().trim().equalsIgnoreCase("YES")) {
							boolean deactivationStatus = listdatasourcedao.deactivateSchema(idDataSchema);
							if (deactivationStatus) {
								status = "success";
								message = "Connection deactivated successfully";
								LOG.info(message);
							} else {
								message = "Connection deactivation failed";
								LOG.error(message);
							}

						} else {
							message = "Connection is already deactive";
							LOG.info(message);
						}

					} else {
						message = "Invalid SchemaId";
						LOG.error(message);
					}

				} else {
					message = "Invalid SchemaId";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				message = "Connection deactivation failed";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "Request failed";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/dataconnection/deactivateConnection - END");
		return json;
	}

	@ApiOperation(value = "Get data Template by schema id", notes = "This API is used to get the list of all the templates that are\r\n"
			+ "associated with connection created in Databuck.", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/datatemplate/getDataTemplatesForSchema", method = RequestMethod.GET, produces = "application/json")
	public GetDataTemplatesForSchemaResponse getDataTemplatesForSchema(HttpServletResponse response,
			HttpServletRequest request, HttpSession session,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema) {

		LOG.info("restapi/datatemplate/getDataTemplatesForSchema - START");
		GetDataTemplatesForSchemaResponse getDataTemplatesForSchemaResponse = new GetDataTemplatesForSchemaResponse();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			LOG.debug("idDataSchema   " + idDataSchema);
			if (isUserValid) {

				// Fetch Details of idDataSchema
				List<ListDataSchema> schemaList = listdatasourcedao.getListDataSchemaId(idDataSchema);

				if (schemaList != null && !schemaList.isEmpty()) {
					List<ListDataSource> listdatasource = listdatasourcedao.getListDataSource(idDataSchema);

					ArrayList<DataTemplateforSchemaDto> jsonArray = new ArrayList<DataTemplateforSchemaDto>();
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

					if (listdatasource != null && listdatasource.size() > 0) {
						for (ListDataSource lds : listdatasource) {
							DataTemplateforSchemaDto obj = new DataTemplateforSchemaDto();
							obj.setIdData(lds.getIdData());
							obj.setName(lds.getName());
							obj.setDataLocation(lds.getDataLocation());
							obj.setTableName(schemaList.get(0).getSchemaName());
							obj.setCreatedAt(lds.getCreatedAt() != null ? dateFormat.format(lds.getCreatedAt()) : "");
							obj.setProfilingEnabled(lds.getProfilingEnabled());
							obj.setAdvancedRulesEnabled(lds.getAdvancedRulesEnabled());
							obj.setTemplateCreateSuccess(lds.getTemplateCreateSuccess());
							obj.setDeltaApprovalStatus(lds.getDeltaApprovalStatus());
							jsonArray.add(obj);
						}
						getDataTemplatesForSchemaResponse.setResult(jsonArray);
						return getDataTemplatesForSchemaResponse;
					} else {
						getDataTemplatesForSchemaResponse.setFail("No Templates found for this connection");
						LOG.error("No Templates found for this connection");
					}
				} else
					getDataTemplatesForSchemaResponse.setFail("Invalid SchemaId");
				LOG.error("Invalid SchemaId");
			} else {
				getDataTemplatesForSchemaResponse.setFail("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			e.printStackTrace();
			getDataTemplatesForSchemaResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/datatemplate/getDataTemplatesForSchema - END");
		return getDataTemplatesForSchemaResponse;
	}

	@ApiOperation(value = "Create Template & Validation", notes = "This API is used to get the dashboard summary of a particular"
			+ "Validation Check using Unique id", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/createTemplateValidationJob", method = RequestMethod.GET)
	private String createTemplateAPI(
			@RequestParam @ApiParam(name = "connectionName", value = "connectionName", example = "Connection") String connectionName,
			@RequestParam @ApiParam(name = "fileOrTableName", value = "fileOrTableName", example = "Table") String fileOrTableName,
			HttpServletRequest req, HttpServletResponse response,
			@RequestParam @ApiParam(name = "projectName", value = "projectName", example = "Project") String projectName,
			@RequestParam @ApiParam(name = "domainName", value = "domainName", example = "Domain") String domainName,
			@RequestParam @ApiParam(name = "userName", value = "userName", example = "User") String userName) {
		LOG.info("restapi/createTemplateValidationJob - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		Long idData = 0l;
		Boolean runValidation = false;
		String uniqueId = "";
		try {
			Integer projectId = iprojectDao.getProjectIdByProjectName(projectName);
			Integer domainId = globalRuleDao.getDomainId(domainName);
			if (projectId != 0 && domainId != 0) {
				String dataTemplateName = connectionName + fileOrTableName.replaceAll("[^a-zA-Z0-9]+", "");
				String profilingEnabled = "N";
				String advancedRuleEnabled = "N";
				long idDataSchema = schemaDao.getSchemaId(connectionName, projectId, domainId);

				List<ListDataSchema> listDataSchema_list = listDataSourceDao
						.getListDataSchemaForIdDataSchema(idDataSchema);
				ListDataSchema listDataSchema = listDataSchema_list.get(0);
				String headerPresent = listDataSchema.getHeaderPresent();
				long idUser = listDataSchema.getCreatedBy();
				String dataFormat = listDataSchema.getFileDataFormat();
				String dataLocation = listDataSchema.getSchemaType();
				String schemaName = listDataSchema.getDatabaseSchema();
				List<ListDataSource> listDataSource_list = listDataSourceDao.getListDataSourceBySource(idDataSchema,
						dataTemplateName);
				if (listDataSource_list != null && listDataSource_list.size() > 0) {
					ListDataSource listDataSource = listDataSource_list.get(0);

					boolean isActive = listDataSource.getActive().trim().equalsIgnoreCase("yes") ? true : false;

					idData = (long) listDataSource.getIdData();
					if (isActive) {
						// Check Current status of template
						boolean isTemplateInProgress = iTaskDAO.isTemplateJobQueuedOrInProgress(idData);

						if (!isTemplateInProgress) {
							if (listDataSource.getTemplateCreateSuccess() != null
									&& listDataSource.getTemplateCreateSuccess().equalsIgnoreCase("Y")) {
								uniqueId = iTaskDAO.placeTemplateJobInQueue(idData,
										TemplateRunTypes.templatererun.toString());

								runValidation = true;
							} else if (listDataSource.getTemplateCreateSuccess() != null
									&& listDataSource.getTemplateCreateSuccess().equalsIgnoreCase("N")) {
								uniqueId = iTaskDAO.placeTemplateJobInQueue(idData,
										TemplateRunTypes.newtemplate.toString());
							}

						} else {
							message = "Template run is already in progress";
							LOG.info(message);
						}

					} else {
						message = "Template is InActive";
						LOG.info(message);
					}

				} else {
					// Create new template
					CompletableFuture<Long> trgtResult = dataProilingTemplateService.createDataTemplate(null,
							idDataSchema, dataLocation, fileOrTableName, dataTemplateName, "Rest Api Template",
							schemaName, headerPresent, "", "", "N", "", "", "", "", "", "", idUser, "", "", dataFormat,
							"", "", "", null, "", "", profilingEnabled, null, Long.valueOf(projectId + ""),
							advancedRuleEnabled, "Rest Api User", "N", "N", "", "", "");

					idData = (trgtResult != null && trgtResult.get() != null && trgtResult.get() != 0l)
							? trgtResult.get()
							: 0l;

					LOG.debug(" Template creation is in progress idData .." + idData);

					// Trigger the template
					CompletableFuture<String> uniqueIdTgt = dataProilingTemplateService.triggerDataTemplate(idData,
							dataLocation, "N", "N");

					uniqueId = (uniqueIdTgt != null && uniqueIdTgt.get() != null) ? uniqueIdTgt.get() : "";

				}

				LOG.debug(" Template uniqueId .." + uniqueId);
				// check status template uniqueid

				String appRunStatus = "inprogress";
				int i = 0;
				Boolean endLoop = true;
				while (endLoop) {
					appRunStatus = iTaskDAO.getJobTemplateUniqueIdApi(uniqueId);
					LOG.info(" Template creation is in progress ..");

					if (appRunStatus.equalsIgnoreCase("completed") || appRunStatus.equalsIgnoreCase("failed")
							|| i == 30) {
						endLoop = false;
						LOG.info(" break here ..");
						break;
					}
					i++;
					Thread.sleep(20000);

				}

				// Check if RuleCatalog is enabled
				boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

				String uniqueValidId = "";
				Long idApp = 0l;

				if (runValidation) {
					idApp = fileMonitorDao.getDQApplicationsForIdData(idData);
					LOG.debug("Validation idApp .." + idApp);
					if (idApp > 0l) {

						// Get validation details
						ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

						// Check if validation is active or not
						boolean isValidationActive = (listApplications != null && listApplications.getActive() != null
								&& listApplications.getActive().equalsIgnoreCase("yes")) ? true : false;

						if (isValidationActive) {

							boolean isValidationEligibleToRun = true;
							String approvalStatusName = "";

							if (isRuleCatalogEnabled) {
								// Get the RuleCatalog Approval status of validation
								Map<String, String> valAppStatus = validationcheckdao
										.getRuleCatalogApprovalStatus(idApp);

								String approvalStatusCode = valAppStatus.get("approvalStatusCode");
								approvalStatusName = ruleCatalogDao
										.getApproveStatusNameById(Integer.parseInt(approvalStatusCode));

								// Check if validation is approved for Production
								isValidationEligibleToRun = (approvalStatusName != null && (approvalStatusName
										.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1)
										|| approvalStatusName
												.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)))
														? true
														: false;
							}

							if (isValidationEligibleToRun) {
								// Get deployMode
								String deployMode = clusterProperties.getProperty("deploymode");
								deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local"
										: "cluster";

								uniqueValidId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null,
										null);
								LOG.debug("Validation uniqueId .." + uniqueValidId);

								status = "success";
								message = "Request successful";
								LOG.info(message);
							} else {
								message = "Validation Approval status[" + approvalStatusName
										+ "] is not eligible for execution";
								LOG.error(message);
							}
						} else
							message = "Validation is inactive, cannot be executed";
						LOG.error(message);
					}

					else
						message = "Validation id is not found";
					LOG.error(message);
				} else {

					idApp = fileMonitorDao.getDQApplicationsForIdData(idData);
					LOG.debug(" Validation idApp .." + idApp);
					/*
					 * while ((idApp == null || idApp == 0) && idAppLoop &&
					 * appRunStatus.equalsIgnoreCase("completed")) { idApp =
					 * fileMonitorDao.getDQApplicationsForIdData(idData);
					 * System.out.println("\n=====> Get idApp details ..");
					 * 
					 * if (idApp > 0 || cnt == 30 ) { idAppLoop=false;
					 * System.out.println("\n=====> idApp break here .."); break; } cnt++;
					 * Thread.sleep(20000);
					 * 
					 * }
					 */

					uniqueValidId = iTaskDAO.getJobStatusByIdapp(idApp);
					LOG.debug("\n=====> Validation false flag uniqueId .." + uniqueValidId);

					int retryCount = 4;

					while (uniqueValidId == null && retryCount > 0) {
						Thread.sleep(20000);
						LOG.info("\n=====> Retrying after 20 seconds ....");

						--retryCount;
						uniqueValidId = iTaskDAO.getJobStatusByIdapp(idApp);
						LOG.debug(" Validation false flag uniqueId .." + uniqueValidId);
					}

					if (uniqueValidId == null) {

						// Get deployMode
						String deployMode = clusterProperties.getProperty("deploymode");
						deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local"
								: "cluster";

						// Place job in queue
						uniqueValidId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null, null);
						LOG.debug(" Validation uniqueId .." + uniqueValidId);

					}
				}

				String appValidRunStatus = "inprogress";
				int j = 0;
				Boolean endLoop2 = true;
				while (endLoop2 && uniqueValidId.length() > 0) {
					appValidRunStatus = iTaskDAO.getJobStatusByUniqueIdApi(uniqueValidId);
					LOG.info(" Validation creation is in progress ..");

					if (appValidRunStatus.equalsIgnoreCase("completed") || appValidRunStatus.equalsIgnoreCase("failed")
							|| j == 30) {
						endLoop2 = false;
						message = "Validation is completed";
						LOG.info(" Validation break here ..");
						break;
					}
					j++;
					Thread.sleep(20000);

				}
				status = "success";
				if (appValidRunStatus.equalsIgnoreCase("failed") || appRunStatus.equalsIgnoreCase("failed"))
					status = "failed";

				json.put("validation_uniqueId", uniqueValidId);
				json.put("template_uniqueId", uniqueId);
				json.put("template_id", idData);
				json.put("validation_id", idApp);
				json.put("Message", message);
				json.put("status", status);
			} else {
				status = "failed";
				message = "Project or Domain not found";
				json.put("Message", message);
				json.put("status", status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		LOG.info("restapi/createTemplateValidationJob - END");
		return json.toString();

	}

	@ApiOperation(value = "Get data template by project id", notes = "This API is used to get the details of a template using template Id", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/datatemplate/getDataTemplateById", method = RequestMethod.GET, produces = "application/json")
	public GetDataTemplateByIdResponse viewIdDataTemplate(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "idData", value = "Id", example = "1") long idData) {

		LOG.info("restapi/datatemplate/getDataTemplateById - START");
		GetDataTemplateByIdResponse getDataTemplateByIdResponse = new GetDataTemplateByIdResponse();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableId(idData);
				listDataAccess lda = listdatasourcedao.getListDataAccess(idData);

				ArrayList<GetDataTemplateByIdDto> jsonArray = new ArrayList<GetDataTemplateByIdDto>();

				for (ListDataSource lds : listdatasource) {
					GetDataTemplateByIdDto obj = new GetDataTemplateByIdDto();
					obj.setIdData(lds.getIdData());
					obj.setIdDataSchema(lda.getIdDataSchema());
					obj.setName(lds.getName());
					obj.setDataLocation(lds.getDataLocation());
					obj.setTableName(lds.getTableName());
					obj.setHostName(lda.getHostName());
					obj.setPort(lda.getPortName());
					obj.setUserName(lda.getUserName());
					obj.setSchemaName(lda.getSchemaName());
					obj.setFolderName(lda.getFolderName());
					obj.setQuery(lda.getQuery());
					obj.setQueryString(lda.getQueryString());
					obj.setIncrementalType(lda.getIncrementalType());
					obj.setWhereCondition(lda.getWhereCondition());
					obj.setDomain(lda.getDomain());
					obj.setFileHeader(lda.getFileHeader());
					obj.setRollingHeader(lda.getRollingHeader());
					obj.setRollingColumn(lda.getRollingColumn());
					obj.setDateFormat(lda.getDateFormat());
					obj.setSliceStart(lda.getSliceStart());
					obj.setSliceEnd(lda.getSliceEnd());
					obj.setProfilingEnabled(lds.getProfilingEnabled());
					obj.setAdvancedRulesEnabled(lds.getAdvancedRulesEnabled());
					obj.setTemplateCreateSuccess(lds.getTemplateCreateSuccess());
					obj.setDeltaApprovalStatus(lds.getDeltaApprovalStatus());
					jsonArray.add(obj);
				}
				getDataTemplateByIdResponse.setResult(jsonArray);
				return getDataTemplateByIdResponse;
			}
			LOG.error("Invalid Authorization");
			getDataTemplateByIdResponse.setFail("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			e.printStackTrace();
			getDataTemplateByIdResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/datatemplate/getDataTemplateById - END");
		return getDataTemplateByIdResponse;
	}

	@ApiOperation(value = "Get data metadata by id", notes = "This API is used to get the details of all the columns present in "
			+ "template and the flags set for different checks on that column", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/datatemplate/getMetadata", method = RequestMethod.GET, produces = "application/json")
	public GetMetadataResponse getMetadata(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "idData", value = "Id", example = "1") long idData) {
		GetMetadataResponse getMetadataResponse = new GetMetadataResponse();
		LOG.info("restapi/datatemplate/getMetadata - START");

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				LOG.debug("idData " + idData);
				// Get the Template details
				ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);

				// Get the listDataAccess for the template
				listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(idData);

				ConnectionInfoDto conInfoJson = new ConnectionInfoDto();
				TemplateInfoDto templateInfoJson = new TemplateInfoDto();

				if (listDataSource != null && listDataAccess != null) {
					templateInfoJson.setIdData(listDataSource.getIdData());
					templateInfoJson.setName(listDataSource.getName());
					templateInfoJson.setDescription(listDataSource.getDescription());
					templateInfoJson.setDataLocation(listDataSource.getDataLocation());
					templateInfoJson.setHostOrFolder(listDataAccess.getHostName());
					templateInfoJson.setTableOrFile(listDataAccess.getFolderName());
					templateInfoJson.setQuery(listDataAccess.getQuery());
					templateInfoJson.setQueryString(listDataAccess.getQueryString());
					templateInfoJson.setWhereCondition(listDataAccess.getWhereCondition());
					templateInfoJson.setProject_id(listDataSource.getProjectId());

					Date date = listDataSource.getCreatedAt();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					templateInfoJson.setCreatedAt(sdf.format(date));

					// Get the connection details
					List<ListDataSchema> listDataSchemaList = listdatasourcedao
							.getListDataSchemaForIdDataSchema(listDataSource.getIdDataSchema());
					ListDataSchema listDataSchema = (listDataSchemaList != null && listDataSchemaList.size() > 0)
							? listDataSchemaList.get(0)
							: null;

					if (listDataSchema != null) {
						conInfoJson.setConnectionId(listDataSchema.getIdDataSchema());
						conInfoJson.setConnectionName(listDataSchema.getSchemaName());
						conInfoJson.setConnectionType(listDataSchema.getSchemaType());
						conInfoJson.setIpAddress(listDataSchema.getIpAddress());
						conInfoJson.setPort(listDataSchema.getPort());
						conInfoJson.setDatabaseSchema(listDataSchema.getDatabaseSchema());
					}

					// Get the metadata details
					List<ListDataDefinition> listDataDefinition = templateviewdao.view(idData);

					ArrayList<GetMetadataResponseDto> jsonArray = new ArrayList<GetMetadataResponseDto>();

					for (ListDataDefinition ldd : listDataDefinition) {
						GetMetadataResponseDto obj = new GetMetadataResponseDto();
						obj.setIdData(ldd.getIdData());
						obj.setIdColumn(ldd.getIdColumn());
						obj.setColumnName(ldd.getColumnName());
						obj.setDisplayName(ldd.getDisplayName());
						obj.setPrimaryKey(ldd.getPrimaryKey());
						obj.setNonNull(ldd.getNonNull());
						obj.setKBE(ldd.getKBE());
						obj.setDgroup(ldd.getDgroup());
						obj.setNullCountThreshold(ldd.getNullCountThreshold());
						obj.setFormat(ldd.getFormat());
						obj.setHashValue(ldd.getHashValue());
						obj.setNumericalStat(ldd.getNumericalStat());
						obj.setNumericalThreshold(ldd.getNumericalThreshold());
						obj.setStringStat(ldd.getStringStat());
						obj.setStringStatThreshold(ldd.getStringStatThreshold());
						obj.setDupkey(ldd.getDupkey());
						obj.setMeasurement(ldd.getMeasurement());
						obj.setIncrementalCol(ldd.getIncrementalCol());
						obj.setRecordAnomaly(ldd.getRecordAnomaly());
						obj.setRecordAnomalyThreshold(ldd.getRecordAnomalyThreshold());
						obj.setStartDate(ldd.getStartDate());
						obj.setEndDate(ldd.getEndDate());
						obj.setTimelinessKey(ldd.getTimelinessKey());
						obj.setDefaultCheck(ldd.getDefaultCheck());
						obj.setDefaultValues(ldd.getDefaultValues());
						obj.setBlend(ldd.getBlend());
						obj.setDataDrift(ldd.getDataDrift());
						obj.setDataDriftThreshold(ldd.getDataDriftThreshold());
						obj.setOutOfNormStat(ldd.getOutOfNormStat());
						obj.setOutOfNormStatThreshold(ldd.getOutOfNormStatThreshold());
						obj.setIsMasked(ldd.getIsMasked());
						obj.setPartitionBy(ldd.getPartitionBy());
						obj.setPatterns(ldd.getPatterns());
						obj.setPatternCheck(ldd.getPatternCheck());
						obj.setPatternCheckThreshold(ldd.getPatternCheckThreshold());
						obj.setDefaultPatternCheck(ldd.getDefaultPatternCheck());
						obj.setDateRule(ldd.getDateRule());
						obj.setDateFormat(ldd.getDateFormat());
						obj.setLengthCheck(ldd.getLengthCheck());
						obj.setMaxLengthCheck(ldd.getMaxLengthCheck());
						obj.setLengthValue(ldd.getLengthValue());
						obj.setLengthThreshold(ldd.getLengthThreshold());
						obj.setBadData(ldd.getBadData());
						obj.setBadDataThreshold(ldd.getBadDataThreshold());
						jsonArray.add(obj);
					}

					getMetadataResponse.setConnectionInfo(conInfoJson);
					getMetadataResponse.setTemplateInfo(templateInfoJson);
					getMetadataResponse.setMetadata(jsonArray);

				} else {
					getMetadataResponse.setFail("Invalid data template");
					LOG.error("Invalid data template");
				}
				return getMetadataResponse;
			} else {
				getMetadataResponse.setFail("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			getMetadataResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/datatemplate/getMetadata - END");
		return getMetadataResponse;
	}

	@ApiOperation(value = "Get advanced rules by id", notes = "This API is used to get the list of all the advance rules generated for\r\n"
			+ "a template during profiling.", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/datatemplate/getAdvancedRules", method = RequestMethod.GET, produces = "application/json")
	public GetAdvancedRulesResponse getAdvancedRules(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "idData", value = "Id", example = "1") long idData) {
		LOG.info("restapi/datatemplate/getAdvancedRules - START");
		GetAdvancedRulesResponse getAdvancedRulesResponse = new GetAdvancedRulesResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				LOG.debug("idData " + idData);
				List<ListAdvancedRules> advancedRulesList = listdatasourcedao.getAdvancedRulesForId(idData);

				ArrayList<GetAdvancedRulesDto> jsonArray = new ArrayList<GetAdvancedRulesDto>();

				for (ListAdvancedRules listAdvancedRules : advancedRulesList) {
					GetAdvancedRulesDto obj = new GetAdvancedRulesDto();
					obj.setRuleId(listAdvancedRules.getRuleId());
					obj.setRuleType(listAdvancedRules.getRuleType());
					obj.setColumnName(listAdvancedRules.getColumnName());
					obj.setRuleExpression(listAdvancedRules.getRuleExpr());
					obj.setRuleSql(listAdvancedRules.getRuleSql());
					obj.setIsRuleActive(listAdvancedRules.getIsRuleActive());
					obj.setIsCustomRuleEligible(listAdvancedRules.getIsCustomRuleEligible());

					jsonArray.add(obj);
				}
				getAdvancedRulesResponse.setResult(jsonArray);
				return getAdvancedRulesResponse;
			}
			LOG.error("Invalid Authorization");
			getAdvancedRulesResponse.setFail("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			getAdvancedRulesResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/datatemplate/getAdvancedRules - END");
		return getAdvancedRulesResponse;
	}

	@ApiOperation(value = "Get validation checks by id", notes = "This API is used to get the list of all the validations for a particular "
			+ "template", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/validation/getAllValidationChecks", method = RequestMethod.GET, produces = "application/json")
	public GetAllValidationChecksResponse getAllValidationChecks(HttpServletResponse response,
			HttpServletRequest request, HttpSession session,
			@RequestParam @ApiParam(name = "idData", value = "Id", example = "1") long idData) {
		GetAllValidationChecksResponse getAllValidationChecksResponse = new GetAllValidationChecksResponse();
		LOG.info("restapi/validation/getAllValidationChecks - START");

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				LOG.debug("idData " + idData);
				List<ListApplicationsandListDataSources> listappslistds = validationcheckdao
						.getListApplicationsByIdData(idData);
				ArrayList<GetAllValidationChecksDto> jsonArray = new ArrayList<GetAllValidationChecksDto>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				for (ListApplicationsandListDataSources lads : listappslistds) {
					GetAllValidationChecksDto obj = new GetAllValidationChecksDto();
					obj.setIdApp(lads.getIdApp());
					obj.setValidationCheckName(lads.getLaName());
					obj.setIdData(lads.getIdData());
					obj.setDataTemplateName(lads.getLsName());
					obj.setCreatedAt(lads.getCreatedAt() != null ? sdf.format(lads.getCreatedAt()) : "");
					obj.setAppType(lads.getAppType());
					obj.setCreatedByUser(lads.getCreatedByUser());
					obj.setActive(lads.getActive());
					jsonArray.add(obj);
				}
				getAllValidationChecksResponse.setResult(jsonArray);
				return getAllValidationChecksResponse;
			}
			LOG.error("Invalid Authorization");
			getAllValidationChecksResponse.setFail("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			getAllValidationChecksResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/validation/getAllValidationChecks - END");
		return getAllValidationChecksResponse;
	}

	@RequestMapping(value = "restapi/validation/runS3ValidationForFolderData", method = RequestMethod.GET)
	public String runS3ValidationForFolderData(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, long idApp, String dataFolder) {

		LOG.info("restapi/validation/runS3ValidationForFolderData - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				// Validate idApp
				LOG.debug("token   " + request.getHeader("token"));
				ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplications != null) {

					if (dataFolder != null && !dataFolder.trim().isEmpty()) {
						ListDataSource listDataSource = listDataSourceDao
								.getDataFromListDataSourcesOfIdData(listApplications.getIdData());
						List<ListDataSchema> schemaList = listdatasourcedao
								.getListDataSchemaForIdDataSchema(listDataSource.getIdDataSchema());
						ListDataSchema listDataSchema = schemaList.get(0);

						String fileNamePattern = listDataSchema.getFileNamePattern();
						String incrementalFileName = dataFolder + "/" + fileNamePattern;
						String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(idApp, incrementalFileName);
						status = "passed";
						message = "Validation placed in queue successfully";
						LOG.info(message);
						json.put("uniqueId", uniqueId);
					} else {
						message = "Invalid dataFolder";
						LOG.error(message);
					}

				} else {
					message = "Invalid ValidationId";
					LOG.error(message);
				}

			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				message = "Invalid Authorization";
				LOG.error(message);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			message = "Request failed";
			LOG.error("Exception  " + e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/validation/runS3ValidationForFolderData - END");
		return json.toString();
	}

	@ApiOperation(value = "Get template column changes for project by id", notes = "Provides the following change column information given a project", tags="Template")
	@ResponseBody
	@RequestMapping(value = "restapi/datatemplate/getTemplatesColumnChangesForProject", method = RequestMethod.GET,produces = "application/json")
	public GetTemplatesColumnChangesForProjectResponse getChangedColumnsData(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam @ApiParam(name = "id", value = "Id", example = "1") long id) {
		
		LOG.info("restapi/datatemplate/getTemplatesColumnChangesForProject - START");
																				 
		String status = "failed";
		String message = "";
		GetTemplatesColumnChangesForProjectResponse responseEntity = new GetTemplatesColumnChangesForProjectResponse();

		try {
			LOG.debug("token   " + request.getHeader("token") );
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization!=null?authorization:"");

			if (isUserValid) {
				LOG.debug("id "+id);
				Project project = projectDAO.getSelectedProject(id);
				if (project != null) {

					SqlRowSet sqlRowSet = listdatasourcedao.getTemplatesColumnChangesForProject(id);
					ArrayList<GetTemplatesColumnChangesForProjectDto> jsonArray = new ArrayList<GetTemplatesColumnChangesForProjectDto>();

					if (sqlRowSet != null) {
						while (sqlRowSet.next()) {
							GetTemplatesColumnChangesForProjectDto tempJson = new GetTemplatesColumnChangesForProjectDto();
							tempJson.setTemplateId(sqlRowSet.getString("templateId"));
							tempJson.setTemplateName( sqlRowSet.getString("templateName"));
							tempJson.setConnectionId( sqlRowSet.getString("connectionId"));
							tempJson.setConnectionName( sqlRowSet.getString("connectionName"));
							tempJson.setConnectionType( sqlRowSet.getString("dataLocation"));
							tempJson.setTableOrFile( sqlRowSet.getString("tableOrFile"));
							tempJson.setAddedColumns( sqlRowSet.getString("addedColumns"));
							tempJson.setMissingColumns( sqlRowSet.getString("missingColumns"));
							Date date = sqlRowSet.getTimestamp("changeDetectedTime");
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							tempJson.setChangeDetectedTime(sdf.format(date));

							jsonArray.add(tempJson);

						}
						if (jsonArray.size() > 0) {
							status = "passed";
							responseEntity.setResult( jsonArray);
						} else {
							status = "passed";
							message = "No Template Column Changes Found";
							LOG.error(message);
						}
					}
				} else {
					message = "Invalid ProjectId";
					LOG.error(message);
				}

		   } else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			message = "Request failed";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		responseEntity.setStatus(status);
		responseEntity.setMessage(message);
		LOG.info("restapi/datatemplate/getTemplatesColumnChangesForProject - END");
		return responseEntity;
	}


	
	@RequestMapping(value = "restapi/validation/getColumnAggregateDQDetails", method = RequestMethod.POST)
	public String getColumnAggregateDQDetails(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ColumnAggregateRequest columnAggregateRequest) {

		LOG.info("restapi/validation/getColumnAggregateDQDetails - START");

		JSONObject outputJson = new JSONObject();
		JSONArray all_columnAggregateDataArray = new JSONArray();
		String status = "failed";
		String message = "";
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("columnAggregateRequest " + columnAggregateRequest);
			if (isUserValid) {
				if (columnAggregateRequest != null) {
					// Get requestDetails
					String idApp = columnAggregateRequest.getIdApp();
					String microsegmentCols = columnAggregateRequest.getMicrosegmentCols();
					String type = columnAggregateRequest.getType();
					String filterColumn = columnAggregateRequest.getFilterColumn();
					String filterValues = columnAggregateRequest.getFilterValues();
					String saveDataToCsvFile = columnAggregateRequest.getSaveDataToCsvFile();
					String fileLocation = columnAggregateRequest.getFileLocation();
					List<Long> validationIds = new ArrayList<Long>();

					boolean isRequestValid = false;
					// Validate request
					if (idApp == null || idApp.trim().isEmpty() || idApp.trim().split(",").length <= 0) {
						message = "Data is missing for field : idApp";
						LOG.error(message);
					} else if (microsegmentCols != null && !microsegmentCols.trim().isEmpty()
							&& microsegmentCols.trim().split(",").length <= 0) {
						message = "Invalid Data in request fields : microsegmentCols";
						LOG.error(message);
					} else if (type == null || type.trim().isEmpty() || !(type.trim().equalsIgnoreCase("aggregate")
							|| type.trim().equalsIgnoreCase("individual"))) {
						message = "Invalid Data in request fields : type";
						LOG.error(message);
					}

					// Filter column is present and filter values are missing, then it is invalid
					// request
					else if (filterColumn != null && !filterColumn.trim().isEmpty() && (filterValues == null
							|| filterValues.trim().isEmpty() || filterValues.trim().split(",").length <= 0)) {
						message = "Data is missing for field - filterValues";
						LOG.error(message);
					}
					// Filter Values are present and filter column is missing, then it is invalid
					// request
					else if (filterValues != null && !filterValues.trim().isEmpty()
							&& (filterColumn == null || filterColumn.trim().isEmpty())) {
						message = "Data is missing for field - filterColumn";
						LOG.error(message);
					}
					// saveDataToCsvFile -- Valid data should be Y or N
					else if (saveDataToCsvFile != null && !saveDataToCsvFile.trim().isEmpty()
							&& !(saveDataToCsvFile.trim().equalsIgnoreCase("Y")
									|| saveDataToCsvFile.trim().equalsIgnoreCase("N"))) {
						message = "Invalid data in field - saveDataToCsvFile, Valid values are : Y, N";
						LOG.error(message);
					}
					// saveDataToCsvFile -- Value is N and fileLocation is blank, it is invalid
					// request
					else if (type.equalsIgnoreCase("aggregate") && saveDataToCsvFile != null
							&& !saveDataToCsvFile.trim().isEmpty() && saveDataToCsvFile.trim().equalsIgnoreCase("Y")
							&& (fileLocation == null || fileLocation.trim().isEmpty())) {
						message = "Data is missing for field - fileLocation";
						LOG.error(message);
					}
					// fileLocation is present and saveDataToCsvFile is blank, it is invalid request
					else if (type.equalsIgnoreCase("aggregate") && fileLocation != null
							&& !fileLocation.trim().isEmpty()
							&& (saveDataToCsvFile == null || saveDataToCsvFile.trim().isEmpty())) {
						message = "Data is missing for field - saveDataToCsvFile";
						LOG.error(message);
					} else {
						List<String> invalid_IdApps = new ArrayList<String>();

						// Validate Validation Ids
						for (String val_Id_str : idApp.trim().split(",")) {
							try {
								val_Id_str = val_Id_str.trim();
								ListApplications listApp = validationcheckdao
										.getdatafromlistapplications(Long.parseLong(val_Id_str));

								if (listApp != null) {
									validationIds.add(Long.parseLong(val_Id_str));
								} else {
									invalid_IdApps.add(val_Id_str);
								}
							} catch (Exception e) {
								invalid_IdApps.add(val_Id_str);
								e.printStackTrace();
								LOG.error("Exception  " + e.getMessage());
							}

						}

						if (invalid_IdApps.size() > 0) {
							message = "Invalid Validation Ids: " + String.join(",", invalid_IdApps);
							LOG.error(message);
						} else {
							// Request is valid
							isRequestValid = true;
						}
					}

					if (isRequestValid) {
						boolean fileStatus = true;
						BufferedWriter bw = null;
						String fullFilePath = "";

						for (long validationId : validationIds) {

							// Get validation details
							ListApplications listApplications = validationcheckdao
									.getdatafromlistapplications(validationId);

							// Get Template details
							long templateId = listApplications.getIdData();
							String templateName = "";
							String tableName = "";

							ListDataSource listDataSource = listdatasourcedao
									.getDataFromListDataSourcesOfIdData(templateId);
							if (listDataSource != null)
								templateName = listDataSource.getName();

							// Get the listDataAccess for the template
							listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(templateId);
							if (listDataAccess != null)
								tableName = listDataAccess.getFolderName();

							// Update validationId in request
							columnAggregateRequest.setIdApp(String.valueOf(validationId));

							// Get the data
							JSONArray columnAggregateDataArray = prepareColumnAggregateDataForChecks(listApplications,
									columnAggregateRequest);

							status = "success";

							JSONObject val_output_Json = new JSONObject();
							val_output_Json.put("validationId", validationId);
							val_output_Json.put("validationName", listApplications.getName());
							val_output_Json.put("templateId", templateId);
							val_output_Json.put("templateName", templateName);
							val_output_Json.put("tableName", tableName);
							val_output_Json.put("result", columnAggregateDataArray);
							all_columnAggregateDataArray.put(val_output_Json);

						}

						// If Type='aggregate' and saveDataToCsvFile='Y' then save data to csv file
						if (type.equalsIgnoreCase("aggregate") && saveDataToCsvFile != null
								&& saveDataToCsvFile.trim().equalsIgnoreCase("Y")) {

							try {
								String fileName = idApp.replaceAll(",", "_") + "_aggregate_DQ_details.csv";
								fullFilePath = fileLocation + "/" + fileName;
								LOG.debug(" Saving Aggregate DQ to location: " + fullFilePath);

								// Create BufferReader
								bw = new BufferedWriter(new FileWriter(new File(fullFilePath)));

								// Save Header
								String header = "idApp,Date,Run,ruleName,dimension_name,totalRecords,failedCount,passPercentage,failedPercentage,templateId,templateName,tableName,dataset,businessAttribute,dqId,systemName,row_identifer_type,batch_id";
								bw.write(header);
								bw.newLine();

								// Save Data
								for (int k = 0; k < all_columnAggregateDataArray.length(); ++k) {
									JSONObject validation_result_json = all_columnAggregateDataArray.getJSONObject(k);

									JSONArray columnAggregateDataArray = validation_result_json.getJSONArray("result");
									long validationId = validation_result_json.getLong("validationId");
									long templateId = validation_result_json.getLong("templateId");
									String templateName = validation_result_json.getString("templateName");
									String tableName = validation_result_json.getString("tableName");

									for (int i = 0; i < columnAggregateDataArray.length(); ++i) {

										JSONObject check_dq_obj = columnAggregateDataArray.getJSONObject(i);

										JSONArray dqAggregateObj = check_dq_obj.getJSONArray("dqAggregate");
										String checkName = check_dq_obj.getString("checkName");

										for (int j = 0; j < dqAggregateObj.length(); ++j) {
											JSONObject aggObj = dqAggregateObj.getJSONObject(j);

											// Fetch details
											String exec_date = aggObj.getString("executionDate");
											long run = aggObj.getLong("run");

											// For Global Rules read rule name else column name
											String ruleName = "";
											if (checkName.equalsIgnoreCase("Global Rules")) {
												ruleName = aggObj.getString("ruleName");
											} else {
												ruleName = aggObj.getString("columnName");
											}
											String dimension_name = aggObj.getString("dimensionName");
											long totalRecords = aggObj.getLong("totalRecords");
											long failedCount = aggObj.getLong("failedCount");
											double failedPercentage = aggObj.getDouble("failPercentage");
											double passPercentage = aggObj.getDouble("passPercentage");
											String dataset = aggObj.getString("dataset");
											String businessAttribute = aggObj.getString("businessAttribute");
											String dqId = aggObj.getString("dqId");
											String systemName = aggObj.getString("systemName");
											String row_identifer_type = aggObj.getString("row_identifer_type");
											String batch_id = aggObj.getString("batch_id");

											// Prepare data line
											StringBuffer data_line = new StringBuffer();
											data_line.append(validationId + ",");
											data_line.append(exec_date + ",");
											data_line.append(run + ",");
											data_line.append(ruleName + ",");
											data_line.append(dimension_name + ",");
											data_line.append(totalRecords + ",");
											data_line.append(failedCount + ",");
											data_line.append(passPercentage + ",");
											data_line.append(failedPercentage + ",");
											data_line.append(templateId + ",");
											data_line.append(templateName + ",");
											data_line.append(tableName + ",");
											data_line.append(dataset + ",");
											data_line.append(businessAttribute + ",");
											data_line.append(dqId + ",");
											data_line.append(systemName + ",");
											data_line.append(row_identifer_type + ",");
											data_line.append(batch_id);
											// Save data
											bw.write(data_line.toString());
											bw.newLine();
										}

										bw.flush();
									}
								}
							} catch (Exception e) {
								fileStatus = false;
								LOG.error(" Exception occurred while saving ColumnAggregateDQ response to file!!");
								LOG.error("Exception  " + e.getMessage());
								e.printStackTrace();
							}

							outputJson.put("fileLocation", fullFilePath);
							if (fileStatus) {
								outputJson.put("fileStatus", "Data saved to file successfully");
							} else {
								outputJson.put("fileStatus", "Failed to save data to file");
							}

						}

					}

				} else {
					message = "Invalid Request";
					LOG.error(message);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		outputJson.put("status", status);
		outputJson.put("message", message);
		outputJson.put("validationResults", all_columnAggregateDataArray);

		LOG.info("restapi/validation/getColumnAggregateDQDetails - END");

		return outputJson.toString();
	}

	@RequestMapping(value = "restapi/validation/getColumnNonAggregateDQDetails", method = RequestMethod.POST)
	public String getColumnNonAggregateDQDetails(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ColumnAggregateRequest columnAggregateRequest) {

		LOG.info("restapi/validation/getColumnNonAggregateDQDetails - START");

		JSONObject outputJson = new JSONObject();
		JSONArray all_columnAggregateDataArray = new JSONArray();
		JSONArray all_columnCountSummaryArray = new JSONArray();
		String status = "failed";
		String message = "";
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("columnAggregateRequest   " + columnAggregateRequest);
				if (columnAggregateRequest != null) {
					// Get requestDetails
					String idApp = columnAggregateRequest.getIdApp();
					String saveDataToCsvFile = columnAggregateRequest.getSaveDataToCsvFile();
					String fileLocation = columnAggregateRequest.getFileLocation();
					List<Long> validationIds = new ArrayList<Long>();

					boolean isRequestValid = false;
					// Validate request
					if (idApp == null || idApp.trim().isEmpty() || idApp.trim().split(",").length <= 0) {
						message = "Data is missing for field : idApp";
						LOG.error(message);
					}
					// saveDataToCsvFile -- Valid data should be Y or N
					else if (saveDataToCsvFile != null && !saveDataToCsvFile.trim().isEmpty()
							&& !(saveDataToCsvFile.trim().equalsIgnoreCase("Y")
									|| saveDataToCsvFile.trim().equalsIgnoreCase("N"))) {
						message = "Invalid data in field - saveDataToCsvFile, Valid values are : Y, N";
						LOG.error(message);
					}
					// saveDataToCsvFile -- Value is N and fileLocation is blank, it is invalid
					// request
					else if (saveDataToCsvFile != null && !saveDataToCsvFile.trim().isEmpty()
							&& saveDataToCsvFile.trim().equalsIgnoreCase("Y")
							&& (fileLocation == null || fileLocation.trim().isEmpty())) {
						message = "Data is missing for field - fileLocation";
						LOG.error(message);
					}
					// fileLocation is present and saveDataToCsvFile is blank, it is invalid request
					else if (fileLocation != null && !fileLocation.trim().isEmpty()
							&& (saveDataToCsvFile == null || saveDataToCsvFile.trim().isEmpty())) {
						message = "Data is missing for field - saveDataToCsvFile";
						LOG.error(message);
					} else {
						List<String> invalid_IdApps = new ArrayList<String>();

						// Validate Validation Ids
						for (String val_Id_str : idApp.trim().split(",")) {
							try {
								val_Id_str = val_Id_str.trim();
								ListApplications listApp = validationcheckdao
										.getdatafromlistapplications(Long.parseLong(val_Id_str));

								if (listApp != null) {
									validationIds.add(Long.parseLong(val_Id_str));
								} else {
									invalid_IdApps.add(val_Id_str);
								}
							} catch (Exception e) {
								invalid_IdApps.add(val_Id_str);
								e.printStackTrace();
								LOG.error("Exception  " + e.getMessage());
							}

						}

						if (invalid_IdApps.size() > 0) {
							message = "Invalid Validation Ids: " + String.join(",", invalid_IdApps);
							LOG.error(message);
						} else {
							// Request is valid
							isRequestValid = true;
						}
					}

					if (isRequestValid) {
						boolean fileStatus = true;
						boolean summaryFileStatus = true;
						BufferedWriter bw = null;
						String fullFilePath = "";
						String summaryFilePath = "";

						for (long validationId : validationIds) {

							// Get validation details
							ListApplications listApplications = validationcheckdao
									.getdatafromlistapplications(validationId);

							// Get Template details
							long templateId = listApplications.getIdData();
							String templateName = "";
							String tableName = "";

							ListDataSource listDataSource = listdatasourcedao
									.getDataFromListDataSourcesOfIdData(templateId);
							if (listDataSource != null)
								templateName = listDataSource.getName();

							// Get the listDataAccess for the template
							listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(templateId);
							if (listDataAccess != null)
								tableName = listDataAccess.getFolderName();

							// Update validationId in request
							columnAggregateRequest.setIdApp(String.valueOf(validationId));

							// Get the data
							JSONArray columnAggregateDataArray = prepareColumnNonAggregateDataForChecks(
									listApplications);

							// Get the count summary data
							JSONArray columnCountSummaryDataArray = prepareColumnNonAggregateCountSummaryDataForChecks(
									listApplications);

							status = "success";

							JSONObject val_output_Json = new JSONObject();
							val_output_Json.put("validationId", validationId);
							val_output_Json.put("validationName", listApplications.getName());
							val_output_Json.put("templateId", templateId);
							val_output_Json.put("templateName", templateName);
							val_output_Json.put("tableName", tableName);
							val_output_Json.put("result", columnAggregateDataArray);
							all_columnAggregateDataArray.put(val_output_Json);

							JSONObject val_count_Json = new JSONObject();
							val_count_Json.put("validationId", validationId);
							val_count_Json.put("validationName", listApplications.getName());
							val_count_Json.put("templateId", templateId);
							val_count_Json.put("templateName", templateName);
							val_count_Json.put("tableName", tableName);
							val_count_Json.put("result", columnCountSummaryDataArray);
							all_columnCountSummaryArray.put(val_count_Json);

						}

						// If saveDataToCsvFile='Y' then save data to csv file
						if (saveDataToCsvFile != null && saveDataToCsvFile.trim().equalsIgnoreCase("Y")) {

							try {
								String fileName = idApp.replaceAll(",", "_") + "_non_aggregate_DQ_details.csv";
								fullFilePath = fileLocation + "/" + fileName;
								LOG.debug("Saving Non Aggregate DQ to location: " + fullFilePath);

								// Identify microsegment columns list
								Set<String> microsegCols = new HashSet<String>();

								List<String> static_cols_list = new ArrayList<String>();
								static_cols_list.add("executionDate");
								static_cols_list.add("run");
								static_cols_list.add("ruleName");
								static_cols_list.add("columnName");
								static_cols_list.add("dimensionName");
								static_cols_list.add("totalRecords");
								static_cols_list.add("failedCount");
								static_cols_list.add("failPercentage");
								static_cols_list.add("passPercentage");
								static_cols_list.add("dataset");
								static_cols_list.add("businessAttribute");
								static_cols_list.add("dqId");
								static_cols_list.add("systemName");
								static_cols_list.add("row_identifer_type");
								static_cols_list.add("batch_id");

								LOG.debug("static_cols_list: " + static_cols_list);

								LOG.debug("Reading microsegment column names: ");

								for (int m = 0; m < all_columnAggregateDataArray.length(); ++m) {
									JSONObject validation_result_json = all_columnAggregateDataArray.getJSONObject(m);
									JSONArray columnAggregateDataArray = validation_result_json.getJSONArray("result");

									long validationId = validation_result_json.getLong("validationId");
									LOG.debug("Reading microsegment columns for validation Id: " + validationId);

									if (columnAggregateDataArray != null && columnAggregateDataArray.length() > 0) {

										for (int i = 0; i < columnAggregateDataArray.length(); ++i) {
											JSONObject check_dq_obj = columnAggregateDataArray.getJSONObject(i);
											JSONArray dqAggregateObj = check_dq_obj.getJSONArray("dqAggregate");

											if (dqAggregateObj != null && dqAggregateObj.length() > 0) {
												JSONObject aggObj = dqAggregateObj.getJSONObject(0);

												Set<String> keysList = aggObj.keySet();
												LOG.debug("keysList:" + keysList);

												for (String key : keysList) {
													if (!static_cols_list.contains(key)) {
														microsegCols.add(key);
													}
												}

												LOG.debug("microsegCols:" + microsegCols);
												break;
											}
										}
									}
								}

								// Create BufferReader
								bw = new BufferedWriter(new FileWriter(new File(fullFilePath)));

								String microsegColsStr = "";
								if (microsegCols.size() > 0) {
									microsegColsStr = "," + String.join(",", microsegCols);
								}

								LOG.debug("microsegColsStr:" + microsegColsStr);

								// Save Header
								String header = "idApp,Date,Run,checkName,ruleName,dimension_name" + microsegColsStr
										+ ",totalRecords,failedCount,passPercentage,failedPercentage,templateId,templateName,tableName,dataset,businessAttribute,dqId,systemName,row_identifer_type,batch_id";
								bw.write(header);
								bw.newLine();

								// Save Data
								for (int k = 0; k < all_columnAggregateDataArray.length(); ++k) {
									JSONObject validation_result_json = all_columnAggregateDataArray.getJSONObject(k);

									JSONArray columnAggregateDataArray = validation_result_json.getJSONArray("result");
									long validationId = validation_result_json.getLong("validationId");
									long templateId = validation_result_json.getLong("templateId");
									String templateName = validation_result_json.getString("templateName");
									String tableName = validation_result_json.getString("tableName");

									for (int i = 0; i < columnAggregateDataArray.length(); ++i) {

										JSONObject check_dq_obj = columnAggregateDataArray.getJSONObject(i);

										JSONArray dqAggregateObj = check_dq_obj.getJSONArray("dqAggregate");
										String checkName = check_dq_obj.getString("checkName");

										for (int j = 0; j < dqAggregateObj.length(); ++j) {
											JSONObject aggObj = dqAggregateObj.getJSONObject(j);

											// Fetch details
											String exec_date = aggObj.getString("executionDate");
											long run = aggObj.getLong("run");

											// For Global Rules read rule name else column name
											String ruleName = "";
											if (checkName.equalsIgnoreCase("Global Rules")) {
												ruleName = aggObj.getString("ruleName");
											} else {
												ruleName = aggObj.getString("columnName");
											}
											String dimension_name = aggObj.getString("dimensionName");
											long totalRecords = aggObj.getLong("totalRecords");
											long failedCount = aggObj.getLong("failedCount");
											double failedPercentage = aggObj.getDouble("failPercentage");
											double passPercentage = aggObj.getDouble("passPercentage");
											String dataset = aggObj.getString("dataset");
											String businessAttribute = aggObj.getString("businessAttribute");
											String dqId = aggObj.getString("dqId");
											String systemName = aggObj.getString("systemName");
											String row_identifer_type = aggObj.getString("row_identifer_type");
											String batch_id = aggObj.getString("batch_id");

											// Prepare data line
											StringBuffer data_line = new StringBuffer();
											data_line.append(validationId + ",");
											data_line.append(exec_date + ",");
											data_line.append(run + ",");
											data_line.append(checkName + ",");
											data_line.append("\"" + ruleName + "\",");
											data_line.append(dimension_name + ",");
											for (String micro_seg : microsegCols) {
												String micro_seg_val_str = "";
												if (aggObj.has(micro_seg)) {
													Object micro_seg_val = aggObj.get(micro_seg);

													if (micro_seg_val != null)
														micro_seg_val_str = micro_seg_val.toString();
												}

												data_line.append("\"" + micro_seg_val_str + "\"" + ",");
											}
											data_line.append(totalRecords + ",");
											data_line.append(failedCount + ",");
											data_line.append(passPercentage + ",");
											data_line.append(failedPercentage + ",");
											data_line.append(templateId + ",");
											data_line.append(templateName + ",");
											data_line.append(tableName + ",");
											data_line.append(dataset + ",");
											data_line.append("\"" + businessAttribute + "\",");
											data_line.append(dqId + ",");
											data_line.append(systemName + ",");
											data_line.append(row_identifer_type + ",");
											data_line.append(batch_id);

											// Save data
											bw.write(data_line.toString());
											bw.newLine();
										}

										bw.flush();
									}
								}
							} catch (Exception e) {
								fileStatus = false;
								LOG.error("Exception occurred while saving ColumnNonAggregateDQ response to file!!");
								e.printStackTrace();
							}

							outputJson.put("fileLocation", fullFilePath);
							if (fileStatus) {
								outputJson.put("fileStatus", "Data saved to file successfully");
							} else {
								outputJson.put("fileStatus", "Failed to save data to file");
							}

							try {
								String fileName = idApp.replaceAll(",", "_") + "_non_aggregate_summary_details.csv";
								summaryFilePath = fileLocation + "/" + fileName;
								LOG.debug(" Saving Non Aggregate Summary DQ to location: " + summaryFilePath);

								// Identify microsegment columns list
								Set<String> microsegCols = new HashSet<String>();

								List<String> static_cols_list = new ArrayList<String>();
								static_cols_list.add("executionDate");
								static_cols_list.add("run");
								static_cols_list.add("ruleName");
								static_cols_list.add("columnName");
								static_cols_list.add("dimensionName");
								static_cols_list.add("count");
								static_cols_list.add("quality");
								static_cols_list.add("dataset");
								static_cols_list.add("businessAttribute");
								static_cols_list.add("dqId");
								static_cols_list.add("systemName");
								static_cols_list.add("row_identifer_type");
								static_cols_list.add("batch_id");

								LOG.debug("static_cols_list: " + static_cols_list);

								LOG.info("\n====>Reading microsegment column names: ");

								for (int m = 0; m < all_columnCountSummaryArray.length(); ++m) {
									JSONObject validation_result_json = all_columnCountSummaryArray.getJSONObject(m);
									JSONArray columnAggregateDataArray = validation_result_json.getJSONArray("result");

									long validationId = validation_result_json.getLong("validationId");
									LOG.debug("\n====>Reading microsegment columns for validation Id: " + validationId);

									if (columnAggregateDataArray != null && columnAggregateDataArray.length() > 0) {

										for (int i = 0; i < columnAggregateDataArray.length(); ++i) {
											JSONObject check_dq_obj = columnAggregateDataArray.getJSONObject(i);
											JSONArray dqAggregateObj = check_dq_obj.getJSONArray("dqAggregate");

											if (dqAggregateObj != null && dqAggregateObj.length() > 0) {
												JSONObject aggObj = dqAggregateObj.getJSONObject(0);

												Set<String> keysList = aggObj.keySet();
												LOG.debug("keysList:" + keysList);

												for (String key : keysList) {
													if (!static_cols_list.contains(key)) {
														microsegCols.add(key);
													}
												}
												LOG.debug("microsegCols:" + microsegCols);
												break;
											}
										}
									}
								}

								// Create BufferReader
								bw = new BufferedWriter(new FileWriter(new File(summaryFilePath)));

								// Get table name for Mapping
								String refMappingTableName = "NonAggregateMappingTable";
								String refMappingTemplateName = "ref_non_aggregate_mapping_table";
								LOG.info("\n====>> Mapping Details :: ");
								LOG.debug("\n====>> Mapping Template Name :: " + refMappingTemplateName);
								ListDataSource mappingListDataSource = listdatasourcedao
										.getDataFromListDataSourcesByName(refMappingTemplateName);
								// Get the listDataAccess for the template
								if (mappingListDataSource != null) {
									listDataAccess listDataAccess = listdatasourcedao
											.getListDataAccess((long) mappingListDataSource.getIdData());
									if (listDataAccess != null && listDataAccess.getFolderName() != null
											&& !listDataAccess.getFolderName().isEmpty()) {
										refMappingTableName = listDataAccess.getFolderName();
									} else {
										LOG.error("Template details not found, so using default table name.");
									}
								} else {
									LOG.error("Reference template not found, so using default table name.");
								}
								LOG.debug("\n====>> Mapping Table Name :: " + refMappingTableName);

								long template_id = 0l;
								for (int k = 0; k < all_columnCountSummaryArray.length(); ++k) {
									JSONObject validation_result_json = all_columnCountSummaryArray.getJSONObject(k);
									template_id = validation_result_json.getLong("templateId");
									if (template_id > 0l)
										break;
								}

								String data_table_name = "";
								// Derived template
								ListDataSource lds_template = listdatasourcedao
										.getDataFromListDataSourcesOfIdData(template_id);

								if (lds_template != null
										&& lds_template.getDataLocation().trim().equalsIgnoreCase("Derived")) {
									ListDerivedDataSource lds_derived_template = listdatasourcedao
											.getDataFromListDerivedDataSourcesOfIdData(template_id);
									data_table_name = lds_derived_template.getName();
								} else {
									data_table_name = lds_template.getName();
								}
								LOG.debug("\n====> Data Table name: " + data_table_name);

								List<String> headers = mappingDAO.getHeadersForMappingDetails(refMappingTableName,
										data_table_name);
								LOG.debug("\n====>> Headers list :: " + headers);

								// Create header string for csv.
								String microsegColsStr = "";
								microsegColsStr = "";
								if (headers != null && !headers.isEmpty()) {
									microsegColsStr = "," + String.join(",", headers);
								}
								LOG.debug("\n====>> Final Microsegment Headers :: " + microsegColsStr);

								// Save Header
								String smy_header = "idApp,Date,Run,checkName,ruleName,dimension_name,count,quality,templateId,templateName,"
										+ "tableName,dataset,businessAttribute,dqId,systemName,row_identifer_type,batch_id"
										+ microsegColsStr;
								bw.write(smy_header);
								bw.newLine();

								// Save Data
								for (int k = 0; k < all_columnCountSummaryArray.length(); ++k) {
									JSONObject validation_result_json = all_columnCountSummaryArray.getJSONObject(k);

									JSONArray columnAggregateDataArray = validation_result_json.getJSONArray("result");
									long validationId = validation_result_json.getLong("validationId");
									long templateId = validation_result_json.getLong("templateId");
									String templateName = validation_result_json.getString("templateName");
									String tableName = validation_result_json.getString("tableName");
									LOG.info("\n====>> Getting Mapping details :: ");
									List<MappingDetail> mappingDetailList = mappingDAO
											.getMappingEntries(refMappingTableName, templateName);
									for (int i = 0; i < columnAggregateDataArray.length(); ++i) {

										JSONObject check_dq_obj = columnAggregateDataArray.getJSONObject(i);

										JSONArray dqAggregateObj = check_dq_obj.getJSONArray("dqAggregate");
										String checkName = check_dq_obj.getString("checkName");

										for (int j = 0; j < dqAggregateObj.length(); ++j) {
											JSONObject aggObj = dqAggregateObj.getJSONObject(j);

											// Fetch details
											String exec_date = aggObj.getString("executionDate");
											long run = aggObj.getLong("run");

											// For Global Rules read rule name else column name
											String ruleName = "";
											if (checkName.equalsIgnoreCase("Global Rules")) {
												ruleName = aggObj.getString("ruleName");
											} else {
												ruleName = aggObj.getString("columnName");
											}
											String dimension_name = aggObj.getString("dimensionName");
											long count = aggObj.getLong("count");
											String quality = aggObj.getString("quality");
											String dataset = aggObj.getString("dataset");
											String businessAttribute = aggObj.getString("businessAttribute");
											String dqId = aggObj.getString("dqId");
											String systemName = aggObj.getString("systemName");
											String row_identifer_type = aggObj.getString("row_identifer_type");
											String batch_id = aggObj.getString("batch_id");

											// Prepare data line
											String dataLines[] = businessAttribute.split(",");

												StringBuffer data_line = new StringBuffer();
												data_line.append(validationId + ",");
												data_line.append(exec_date + ",");
												data_line.append(run + ",");
												data_line.append(checkName + ",");
												data_line.append("\"" + ruleName + "\",");
												data_line.append(dimension_name + ",");
												data_line.append(count + ",");
												data_line.append(quality + ",");
												data_line.append(templateId + ",");
												data_line.append(templateName + ",");
												data_line.append(tableName + ",");
												data_line.append(dataset + ",");
												data_line.append("\"" + businessAttribute + "\",");
												data_line.append(dqId + ",");
												data_line.append(systemName + ",");
												data_line.append(row_identifer_type + ",");
												data_line.append(batch_id);
												// Prepare data as per columns position
												for (String header : headers) {
													String micro_seg_val_str = "";
													MappingDetail mapping = mappingDetailList.stream()
															.filter(mappingDetail -> mappingDetail.getRightColumn()
																	.equalsIgnoreCase(header))
															.collect(Collectors.toList()).get(0);
													String leftColumn = mapping.getLeftColumn();
													// If left column key is present then get data from response else add
													// default value from mapping details.
													if (leftColumn != null && !leftColumn.isEmpty()
															&& aggObj.has(leftColumn)) {
														Object micro_seg_val = aggObj.get(leftColumn);
														if (micro_seg_val != null) {
															micro_seg_val_str = micro_seg_val.toString();
														}
													} else {
														micro_seg_val_str = mapping.getDefaultValue();
													}
													data_line.append("," + "\"" + micro_seg_val_str + "\"");
												}

												// Save data
												bw.write(data_line.toString());
												bw.newLine();
										}

										bw.flush();
									}
								}
							} catch (Exception e) {
								summaryFileStatus = false;
								LOG.error("\n====> Exception occurred while saving Column summary response to file!!");
								LOG.error("Exception  " + e.getMessage());
								e.printStackTrace();
							}

							outputJson.put("summaryFileLocation", summaryFilePath);
							if (summaryFileStatus) {
								outputJson.put("summaryFileStatus", "Data saved to file successfully");
							} else {
								outputJson.put("summaryFileStatus", "Failed to save data to file");
							}

						}

					}

				} else {
					message = "Invalid Request";
					LOG.error(message);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		outputJson.put("status", status);
		outputJson.put("message", message);
//		outputJson.put("validationResults", all_columnAggregateDataArray);

		LOG.info("restapi/validation/getColumnNonAggregateDQDetails - END");

		return outputJson.toString();
	}

	@RequestMapping(value = "restapi/datatemplate/getColumnProfileDetails", method = RequestMethod.GET)
	public String getColumnProfileDetails(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Long templateId) {
		LOG.info("restapi/datatemplate/getColumnProfileDetails - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			JSONArray columnProfileDataArray = new JSONArray();
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("templateId " + templateId);
				if (templateId != null) {
					ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(templateId);

					if (listDataSource != null) {
						List<ColumnProfile_DP> columnProfileList = listdatasourcedao
								.readColumnProfileForTemplate(templateId);

						ObjectMapper mapper = new ObjectMapper();

						if (columnProfileList != null && columnProfileList.size() > 0) {

							for (ColumnProfile_DP columnProfile_dp : columnProfileList) {
								// Converting the Object to JSONString
								String jsonString = mapper.writeValueAsString(columnProfile_dp);
								JSONTokener jsonTokener = new JSONTokener(jsonString);
								JSONObject profileObj = new JSONObject(jsonTokener);
								profileObj.remove("projectName");
								columnProfileDataArray.put(profileObj);
							}
						}
						status = "success";
						json.put("result", columnProfileDataArray);
					} else {
						message = "Invalid templateId";
						LOG.error(message);
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
				} else {
					message = "Invalid templateId";
					LOG.error(message);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/datatemplate/getColumnProfileDetails - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/datatemplate/createExtendTemplateRule", method = RequestMethod.POST, produces = "application/json")
	public String createExtendTemplateRule(HttpServletResponse response, HttpServletRequest request,
			@RequestBody ListColRules listColRules) {

		LOG.info("restapi/datatemplate/createExtendTemplateRule - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("listColRules   " + listColRules);
			if (isUserValid) {

				if (listColRules != null) {
					Long idData = listColRules.getIdData();
					ListDataSource lds = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);

					if (lds != null) {
						if (listColRules.getIdDimension() == null) {
							listColRules.setIdDimension(1l);
						}
						listColRules.setDomainId(lds.getDomain());
						listColRules.setProjectId((long) lds.getProjectId());
						listColRules.setCreatedByUser(lds.getCreatedByUser());

						String ruleType = listColRules.getRuleType();

						// Check if rule type is supported or not
						if (ruleType != null && (ruleType.equalsIgnoreCase("Referential")
								|| ruleType.equalsIgnoreCase("Regular Expression"))) {

							if (ruleType.equalsIgnoreCase("Referential"))
								ruleType = "Referential";
							else if (ruleType.equalsIgnoreCase("Regular Expression"))
								ruleType = "Regular Expression";

							// Check if rule expression is empty
							if (listColRules.getExpression() != null
									&& !listColRules.getExpression().trim().isEmpty()) {

								// Check if the RuleName is duplicate
								boolean isRuleNameDuplicated = iExtendTemplateRuleDAO
										.isExtendTemplateRuleAlreadyExists(listColRules);

								if (!isRuleNameDuplicated) {
									listColRules.setExternal("N");

									// Save the custom Rules
									iExtendTemplateRuleDAO.insertintolistColRules(listColRules);
									status = "success";
									message = "CustomRule created successfully";
									LOG.info(message);
								} else {
									message = "Rule name already exists";
									LOG.error(message);
								}

							} else {
								message = "Rule expression is empty";
								LOG.error(message);
							}

						} else {
							message = "Rule Type is not supported";
							LOG.error(message);
						}

					} else {
						message = "Invalid TemplateId";
						LOG.error(message);
					}

				} else {
					message = "Invalid Request";
					LOG.error(message);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				LOG.error("\n===> Invalid username or password !!");
				message = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/datatemplate/createExtendTemplateRule - END");
		return json.toString();
	}

	@ApiOperation(value = "Get validation check by id", notes = "This API is used to get the details of a particular validation", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/validation/getValidationCheckById", method = RequestMethod.GET, produces = "application/json")
	public GetValidationCheckByIdResponse getValidationCheckById(HttpServletResponse response,
			HttpServletRequest request, HttpSession session,
			@RequestParam @ApiParam(name = "idApp", value = "App Id", example = "1") long idApp) {
		LOG.info("restapi/validation/getValidationCheckById - START");
		GetValidationCheckByIdResponse getValidationCheckByIdResponse = new GetValidationCheckByIdResponse();

		try {
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);
				ArrayList<ValidationCheckByIdDto> jsonArray = new ArrayList<ValidationCheckByIdDto>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (listApplication != null) {
					ValidationCheckByIdDto obj = new ValidationCheckByIdDto();
					obj.setIdApp(listApplication.getIdApp());
					obj.setName(listApplication.getName());
					obj.setAppType(listApplication.getAppType());
					obj.setIdData(listApplication.getIdData());
					obj.setIdRightData(listApplication.getIdRightData());
					obj.setIdLeftData(listApplication.getIdLeftData());
					obj.setFileNameValidation(listApplication.getFileNameValidation());
					obj.setEntityColumn(listApplication.getEntityColumn());
					obj.setColOrderValidation(listApplication.getColOrderValidation());
					obj.setNumericalStatCheck(listApplication.getNumericalStatCheck());
					obj.setStringStatCheck(listApplication.getStringStatCheck());
					obj.setTimelinessKeyCheck(listApplication.getTimelinessKeyChk());
					obj.setRecordAnomalyCheck(listApplication.getRecordAnomalyCheck());
					obj.setNonNullCheck(listApplication.getNonNullCheck());
					obj.setDataDriftCheck(listApplication.getDataDriftCheck());
					obj.setRecordCountAnomaly(listApplication.getRecordCountAnomaly());
					obj.setRecordCountAnomalyThreshold(listApplication.getRecordCountAnomalyThreshold());
					obj.setOutOfNormCheck(listApplication.getOutOfNormCheck());
					obj.setApplyRules(listApplication.getApplyRules());
					obj.setApplyDerivedColumns(listApplication.getApplyDerivedColumns());
					obj.setKeyGroupRecordCountAnomaly(listApplication.getKeyGroupRecordCountAnomaly());
					obj.setUpdateFrequency(listApplication.getUpdateFrequency());
					obj.setFrequencyDays(listApplication.getFrequencyDays());
					obj.setIncrementalMatching(listApplication.getIncrementalMatching());
					obj.setBuildHistoricFingerPrint(listApplication.getBuildHistoricFingerPrint());
					obj.setHistoricStartDate(listApplication.getHistoricStartDate());
					obj.setHistoricEndDate(listApplication.getHistoricEndDate());
					obj.setHistoricDateFormat(listApplication.getHistoricDateFormat());
					obj.setCsvDir(listApplication.getCsvDir());
					obj.setGroupEquality(listApplication.getGroupEquality());
					obj.setGroupEqualityThreshold(listApplication.getGroupEqualityThreshold());
					obj.setPrefix1(listApplication.getPrefix1());
					obj.setPrefix2(listApplication.getPrefix2());
					obj.setTimeSeries(listApplication.getTimeSeries());
					obj.setMatchingThreshold(listApplication.getMatchingThreshold());
					obj.setIncrementalTimestamp(listApplication.getIncrementalTimestamp() != null
							? sdf.format(listApplication.getIncrementalTimestamp())
							: "");
					obj.setDefaultCheck(listApplication.getDefaultCheck());
					obj.setPatternCheck(listApplication.getPatternCheck());
					obj.setBadData(listApplication.getBadData());
					obj.setLengthCheck(listApplication.getlengthCheck());
					obj.setMaxLengthCheck(listApplication.getMaxLengthCheck());
					obj.setDateRuleCheck(listApplication.getDateRuleChk());
					obj.setdGroupNullCheck(listApplication.getdGroupNullCheck());
					obj.setdGroupDateRuleCheck(listApplication.getdGroupDateRuleCheck());
					obj.setFileMonitoringType(listApplication.getFileMonitoringType());
					obj.setdGroupDataDriftCheck(listApplication.getdGroupDataDriftCheck());
					obj.setRollTargetSchemaId(listApplication.getRollTargetSchemaId());
					obj.setThresholdsApplyOption(listApplication.getThresholdsApplyOption());
					jsonArray.add(obj);
				}
				getValidationCheckByIdResponse.setResult(jsonArray);
				return getValidationCheckByIdResponse;
			}
			getValidationCheckByIdResponse.setFail("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();

			getValidationCheckByIdResponse.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/validation/getValidationCheckById - STOP");
		return getValidationCheckByIdResponse;
	}

	@ApiOperation(value = "Run validation by app id", notes = "This API is used to get the status, unique id of a particular "
			+ "Validation check", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/validation/runValidation", method = RequestMethod.GET, produces = "application/json")
	public RunValidationResponse runtaskresult(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "idApp", value = "App Id", example = "1") long idApp) {

		LOG.info("restapi/validation/runValidation - START");

		RunValidationResponse runValidationResponse = new RunValidationResponse();
		String status = "failed";
		String reason = "";
		String uniqueId = "";
		String approvalStatus="";
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				// Check if already validation with IdApp is in 'started' or 'in progress' or
				// 'queued'
				LOG.debug("idApp " + idApp);
				boolean isAppInProgress = iTaskDAO.isApplicationInProgress(idApp);

				if (isAppInProgress) {
					reason = "Application with [" + idApp + "] is already queued or in-progress";
				} else {

					// Get validation details
					ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

					// Check if validation is active or not
					boolean isValidationActive = (listApplications != null && listApplications.getActive() != null
							&& listApplications.getActive().equalsIgnoreCase("yes")) ? true : false;

					if (isValidationActive) {

						boolean isValidationEligibleToRun = true;
						String approvalStatusName = "";

						// Check if RuleCatalog is enabled
						boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

						if (isRuleCatalogEnabled) {
							// Get the RuleCatalog Approval status of validation
							Map<String, String> valAppStatus = validationcheckdao.getRuleCatalogApprovalStatus(idApp);

							String approvalStatusCode = valAppStatus.get("approvalStatusCode");
							approvalStatus = valAppStatus.get("approvalStatus");
							approvalStatusName = ruleCatalogDao
									.getApproveStatusNameById(Integer.parseInt(approvalStatusCode));

							// Check if validation is approved for Production
							isValidationEligibleToRun = (approvalStatusName != null && (approvalStatusName
									.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1)
									|| approvalStatusName
											.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2))) ? true
													: false;
						}

						if (isValidationEligibleToRun) {
							// Get deployMode
							String deployMode = clusterProperties.getProperty("deploymode");
							deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local"
									: "cluster";

							uniqueId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null, null);

							status = "success";
							reason = "Request successful";
							
							LOG.info(reason);
						} else {
							reason = "Validation Approval status[" + approvalStatusName
									+ "] is not eligible for execution";
							LOG.error(reason);
						}
					} else {
						reason = "Validation is inactive, cannot be executed";
						LOG.error(reason);
					}

				}
			} else {
				LOG.error(" Invalid username or password ");

				reason = "Invalid Authorization";
				LOG.error(reason);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			reason = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

			e.printStackTrace();
		}

		runValidationResponse.setStatus(status);
		runValidationResponse.setReason(reason);
		runValidationResponse.setUniqueId(uniqueId);
		runValidationResponse.setAppRunStatus(approvalStatus);
		LOG.info("restapi/validation/runValidation - END");
		return runValidationResponse;
	}

	@RequestMapping(value = "restapi/validation/runValidationUnitTesting", method = RequestMethod.GET, produces = "application/json")
	public String runValidationUnitTesting(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long idApp) {
		LOG.info("restapi/validation/runValidationUnitTesting - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String reason = "";
		String uniqueId = "";
		try {
			LOG.debug("token   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {

				LOG.debug("idApp " + idApp);
				// Check if already validation with IdApp is in 'started' or 'in progress' or
				// 'queued'
				boolean isAppInProgress = iTaskDAO.isApplicationInProgress(idApp);

				if (isAppInProgress) {
					reason = "Application with [" + idApp + "] is already queued or in-progress";
					LOG.info(reason);
				} else {

					// Get validation details
					ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

					// Check if validation is active or not
					boolean isValidationActive = (listApplications != null && listApplications.getActive() != null
							&& listApplications.getActive().equalsIgnoreCase("yes")) ? true : false;

					if (isValidationActive) {

						/*
						 * When rule catalog is enabled, get the approval status of validation Only when
						 * it has UNIT_TEST_READY or APPROVED_FOR_PRODUCTION the validation is eligible
						 * for Run.
						 * 
						 * But currently this logic is removed as per Wells requirement changes. In
						 * future if we need to add this please refer to git history of this file and
						 * get that code piece
						 */

						// Get deployMode
						String deployMode = clusterProperties.getProperty("deploymode");
						deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local"
								: "cluster";

						uniqueId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null,
								DatabuckConstants.VAL_RUN_TYPE_UNIT_TESTING);

						status = "success";
						reason = "Request successful";
						LOG.info(reason);

					} else {
						reason = "Validation is inactive, cannot be executed";
						LOG.error(reason);
					}

				}
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.put("status", "failed");
				json.put("reason", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("status", "failed");
			json.put("reason", "unexpected exception occurred");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

			e.printStackTrace();
		}
		json.put("status", status);
		json.put("reason", reason);
		json.put("uniqueId", uniqueId);
		LOG.info("restapi/validation/runValidationUnitTesting - END");
		return json.toString();
	}

	@ApiOperation(value = "Get validation status by id", notes = "This API is used to get the execution status of a particular Unique Id", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/validation/checkStatus", method = RequestMethod.GET, produces = "application/json")
	public RunValidationResponse checkStatusByUniqueId(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "id", value = "Id", example = "1") String id) {
		LOG.info("restapi/validation/checkStatus - START");

		RunValidationResponse json = new RunValidationResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				LOG.debug("id " + id);

				String appRunStatus = iTaskDAO.getJobStatusByUniqueId(id);
				if (appRunStatus.equals("")) {
					json.setStatus("failed");
					json.setUniqueId(id);
					json.setAppRunStatus(appRunStatus);
					json.setReason("Invalid ID");
				} else {
					json.setStatus("success");
					json.setUniqueId(id);
					json.setAppRunStatus(appRunStatus);
					json.setReason("");
				}

			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.setStatus("failed");
				json.setReason("Invalid Authorization");
				json.setUniqueId(id);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			json.setStatus("failed");
			json.setReason("unexpected exception occurred");
			json.setUniqueId(id);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/validation/checkStatus - END");
		return json;
	}

	@RequestMapping(value = "restapi/validation/getValidationRunHistory", method = RequestMethod.GET, produces = "application/json")
	public String getValidationRunHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam Long idApp) {

		LOG.info("restapi/validation/getValidationRunHistory - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("token   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("idApp " + idApp);
				// Check if appGroup is valid
				if (idApp != null && idApp != 0l) {

					List<ValidationRunDTO> valHistoryList = iTaskDAO.getValidationRunHistory(idApp);

					JSONArray childArray = new JSONArray();
					if (valHistoryList != null && valHistoryList.size() > 0) {
						for (ValidationRunDTO validationRunDTO : valHistoryList) {
							JSONObject childObj = new JSONObject();
							childObj.put("idApp", validationRunDTO.getIdApp());
							childObj.put("uniqueId", validationRunDTO.getUniqueId());
							childObj.put("execDate", validationRunDTO.getExecDate());
							childObj.put("run", validationRunDTO.getRun());
							childObj.put("testRun", validationRunDTO.getTestRun());
							childObj.put("status", validationRunDTO.getStatus());
							childArray.put(childObj);
						}
					}
					json.put("history", childArray);

				} else {
					json.put("status", "failed");
					json.put("message", "Invalid Validation Id");
					LOG.error("Invalid Validation Id");
				}
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.put("status", "failed");
				json.put("message", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			json.put("status", "failed");
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/validation/getValidationRunHistory - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/validation/getRWIFileLocationForRule", method = RequestMethod.GET)
	public String getRWIFileLocationForRule(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long idApp, @RequestParam long dqrId) {

		LOG.info("restapi/validation/getRWIFileLocationForRule - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			String errorMsg = "";

			if (isUserValid) {

				LOG.debug("ValidationId " + idApp);

				ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplication != null) {

					// Fetch project details
					Long projectId = listApplication.getProjectId();
					String projectName = projectDAO.getProjectNameByProjectid(projectId);

					// Fetch domain details
					Long domainId = listApplication.getDomainId();
					String domainName = iTaskDAO.getDomainNameById(domainId);

					if (projectName != null && !projectName.trim().isEmpty()) {

						if (domainName != null && !domainName.trim().isEmpty()) {

							// Fetching dqrId details
							Map<String, Object> dqrIdDetailsMap = iTaskDAO.getRuleCheckDetails(idApp, dqrId);

							// Validating dqrId
							if (dqrIdDetailsMap != null && dqrIdDetailsMap.size() > 0) {

								// extracting dqrId details
								String rule_type = (String) dqrIdDetailsMap.get("rule_type");
								String columnName = (String) dqrIdDetailsMap.get("column_name");

								// getting fail check data folder name
								Map<String, String> exceptionFolderMap = getCheckToFileNameMap();
								String exceptionFolder = exceptionFolderMap.get(rule_type);
								exceptionFolder = exceptionFolder + "/" + columnName;

								// extracting max date and max run
								Map<String, Object> maxDateAndRunMap = getMaxDateAndRun(idApp);

								if (maxDateAndRunMap != null && maxDateAndRunMap.size() > 0) {

									String maxDate = maxDateAndRunMap.get("maxDate").toString();
									Integer maxRun = (Integer) maxDateAndRunMap.get("maxRun");

									// getting failDataFile path
									String exceptionDataFilePath = getExceptionDataFileLocation(projectName, domainName,
											idApp, maxDate, maxRun, exceptionFolder);

									if (exceptionDataFilePath != null && !exceptionDataFilePath.isEmpty()) {

										// Find the file type
										String resultFileType = appDbConnectionProperties
												.getProperty("databuck.result.fileType");

										String fileType = (resultFileType != null
												&& resultFileType.trim().equalsIgnoreCase("parquet")) ? "parquet"
														: "csv";

										json.put("status", "success");
										json.put("validationId", idApp);
										json.put("dqrId", dqrId);
										json.put("maxDate", maxDate);
										json.put("maxRun", maxRun);
										json.put("checkName", rule_type);
										json.put("checkColumnName", columnName);
										json.put("filePath", exceptionDataFilePath);
										json.put("fileType", fileType);
										return json.toString();

									} else {
										errorMsg = "Failed to locate Exception data file location";

									}

								} else {
									errorMsg = "No run is performed for this validation";

								}

							} else {
								errorMsg = "Invalid dqrId";

							}

						} else {
							errorMsg = "Failed to get Domain details of the validation";

						}

					} else {
						errorMsg = "Failed to get Project details of the validation";

					}

				} else {
					errorMsg = "Invalid ValidationId";

				}

				LOG.error(errorMsg);
				json.put("status", "failed");
				json.put("message", errorMsg);

			} else {
				LOG.error("Invalid Authorization");
				json.put("status", "failed");
				json.put("message", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			json.put("status", "failed");
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/validation/getRWIFileLocationForRule - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/dataquality/dashboardSummaryForSchema", method = RequestMethod.GET)
	public String dashboardSummaryForConnection(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, long idDataSchema) {

		LOG.info("restapi/dataquality/dashboardSummaryForSchema - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("idDataSchema " + idDataSchema);
				// Fetch Details of idDataSchema
				List<ListDataSchema> schemaList = listdatasourcedao.getListDataSchemaId(idDataSchema);

				// Check if idDataSchema is valid
				if (schemaList != null && schemaList.size() > 0) {

					// Get the list of validation Id's associated with idDataSchema
					List<Long> validationIdList = validationcheckdao.getValidationIdListForSchema(idDataSchema);

					if (validationIdList != null && !validationIdList.isEmpty()) {

						String valIdsStr = "";
						for (Long idApp : validationIdList) {
							if (idApp != null && idApp > 0l)
								valIdsStr = valIdsStr + idApp + ",";
						}
						valIdsStr = valIdsStr.substring(0, valIdsStr.length() - 1);

						// Calculate aggregate values
						JSONObject respJson = prepareDashboardSummaryForConnection(schemaList.get(0), valIdsStr);
						status = "success";
						json.put("result", respJson);
					} else
						message = "No validations found for this schema";

				} else
					message = "Invalid SchemaId";

			} else {
				message = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			LOG.error(message);

		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/dataquality/dashboardSummaryForSchema - END");
		return json.toString();
	}

	@ApiOperation(value = "Get dashboard result by Unique id", notes = "This API is used to get the dashboard summary of a particular"
			+ "Validation Check using Unique id", tags = "DataQuality")
	@ResponseBody
	@RequestMapping(value = "restapi/dataquality/dashboardResult", method = RequestMethod.GET)
	public String dashboardResultByUniqueId(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "id", value = "Unique Id", example = "1") String id) {

		LOG.info("restapi/dataquality/dashboardResult - START");

		JSONObject json = new JSONObject();
		try {
			String uniqueId = id;
			LOG.debug("Authorization   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				if (uniqueId != null && !uniqueId.trim().isEmpty() && uniqueId.split("_").length == 2) {
					long idApp = 0;
					idApp = Long.parseLong(uniqueId.split("_")[0]);
					ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

					JSONArray jsonArray = new JSONArray();

					if (listApplication != null) {
						// Check if the status of the job is completed or not
						String appRunStatus = iTaskDAO.getJobStatusByUniqueId(uniqueId);
						if (appRunStatus != null && !appRunStatus.trim().isEmpty()) {
							if (appRunStatus.equalsIgnoreCase("completed")) {
								// Fetch the execDate and Run
								Long run = 0l;
								String execDate = "";
								Map<String, Object> outputMap = validationcheckdao.getDateRunForUniqueId(uniqueId);

								if (outputMap != null && outputMap.size() == 2) {
									Date e_Date = (Date) outputMap.get("execution_date");
									run = (Long) outputMap.get("run");

									if (e_Date != null && run != null && run != 0l) {
										execDate = new SimpleDateFormat("yyyy-MM-dd").format(e_Date);
										LOG.debug("\n====> execDate: " + execDate);
										LOG.debug("\n====> run: " + run);

										try {
											JSONObject obj = new JSONObject();
											obj = prepareDQDashBoardResultForUniqueId(uniqueId, idApp, execDate, run);
											jsonArray.put(obj);
											json.put("result", jsonArray);
										} catch (Exception e) {
											e.printStackTrace();
											json.put("status", "failed");
											json.put("reason",
													"Unable to fetch dashboard result, unexpected error occurred.");
											LOG.error("Exception  " + e.getMessage());
										}
									} else {
										json.put("status", "failed");
										json.put("reason", "Unable to fetch dashboard result, failed to Date and run.");
										LOG.error("Unable to fetch dashboard result, failed to Date and run.");
									}
								} else {
									json.put("status", "failed");
									json.put("reason", "Unable to fetch dashboard result, failed to Date and run.");
									LOG.error("Unable to fetch dashboard result, failed to Date and run.");
								}

							} else if (appRunStatus.equalsIgnoreCase("queued")
									|| appRunStatus.equalsIgnoreCase("started")
									|| appRunStatus.equalsIgnoreCase("in progress")
									|| appRunStatus.equalsIgnoreCase("killed")
									|| appRunStatus.equalsIgnoreCase("failed")) {
								json.put("status", "failed");
								json.put("reason",
										"Unable to fetch dashboard result,Job is in " + appRunStatus + " state.");
								LOG.error("Unable to fetch dashboard result,Job is in " + appRunStatus + " state.");
							}
						} else {
							json.put("status", "failed");
							json.put("reason",
									"Unable to fetch dashboard result, Job status is not available. Please enter valid Unique Id");
							LOG.error(
									"Unable to fetch dashboard result, Job status is not available. Please enter valid Unique Id");
						}
					} else {
						json.put("status", "failed");
						json.put("reason", "Application List Not Found");
						LOG.error("Application List Not Found");
					}
					return json.toString();
				} else {
					json.put("status", "failed");
					json.put("reason", "Invalid UniqueId. This Id is not valid for Validation");
					LOG.error("Invalid UniqueId");
				}
			} else {
				json.put("status", "failed");
				json.put("reason", "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", "failed");
			json.put("reason", "Request failed");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/dataquality/dashboardResult - STOP");
		return json.toString();
	}

	@ApiOperation(value = "Get dashboard result by app id", notes = "This API is used to get the dashboard summary of a particular"
			+ "Validation Check", tags = "DataQuality")
	@ResponseBody
	@RequestMapping(value = "restapi/dataquality/dashboardResultById", method = RequestMethod.GET, produces = "application/json")
	public DashboardResultByIdResponse getDQDashboardResult(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "idApp", value = "App Id", example = "1") long idApp) {

		LOG.info("restapi/dataquality/dashboardResultById - START");
		DashboardResultByIdResponse json = new DashboardResultByIdResponse();
		try {
			LOG.debug("Authorization   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplication != null) {
					DashboardResultByIdDto obj = prepareDQDashBoardResult(idApp);
					json.setResult(obj);
					return json;
				} else {
					LOG.error("Invalid ValidationId");
					json.setFail("Invalid ValidationId");
				}
			}
			json.setFail("Invalid Authorization");
			LOG.error("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			json.setFail("Request failed");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		LOG.info("restapi/dataquality/dashboardResultById - END");
		return json;
	}

	@ApiOperation(value = "Get dashboard result details by app id", notes = "This API is used to get the dashboard details of a particular"
			+ "Validation Check", tags = "DataQuality")
	@ResponseBody
	@RequestMapping(value = "restapi/dataquality/dashboardResultDetailsById", method = RequestMethod.GET)
	public String dashboardResultDetailsForApp(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam @ApiParam(name = "idApp", value = "App Id", example = "1") long idApp) {

		LOG.info("restapi/dataquality/dashboardResultDetailsById - START");
		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			LOG.debug("idApp " + idApp);
			if (isUserValid) {
				ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplication != null) {
					JSONObject obj = prepareDQDashBoardDetails(idApp);
					json.put("result", obj);
					return json.toString();
				} else {
					json.put("fail", "Invalid ValidationId");
					LOG.error("Invalid ValidationId");
				}

			} else {
				LOG.error("Invalid Authorization");
				json.put("fail", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		LOG.info("restapi/dataquality/dashboardResultDetailsById - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/appgroup/getAppGroupsForProject", method = RequestMethod.GET)
	public String getAppGroupsForProject(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long projectId) {

		LOG.info("restapi/appgroup/getAppGroupsForProject - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("projectId " + projectId);
			if (isUserValid) {
				List<Project> projectList = new ArrayList<Project>();
				Project project = new Project();
				project.setIdProject(projectId);
				projectList.add(project);
				List<ListAppGroup> appgroupList = iTaskDAO.getAppGroupsForProject(projectId, projectList);

				// Convert result to json string
				JSONArray jsonArray = new JSONArray();
				for (ListAppGroup listAppGroup : appgroupList) {
					JSONObject obj = new JSONObject();
					obj.put("idAppGroup", listAppGroup.getIdAppGroup());
					obj.put("appGroupName", listAppGroup.getName());

					JSONArray childArray = new JSONArray();

					// Get the validations mapped to the AppGroup
					List<AppGroupMapping> appGroupMappingList = iTaskDAO
							.getApplicationMappingForGroup(listAppGroup.getIdAppGroup());
					if (appGroupMappingList != null && appGroupMappingList.size() > 0) {
						for (AppGroupMapping appGroupMapping : appGroupMappingList) {
							JSONObject childObj = new JSONObject();
							childObj.put("appId", appGroupMapping.getAppId());
							childObj.put("appName", appGroupMapping.getAppName());
							childArray.put(childObj);
						}
					}
					obj.put("validationsList", childArray);

					jsonArray.put(obj);
				}
				json.put("result", jsonArray);
				LOG.info("restapi/appgroup/getAppGroupsForProject - STOP");
				return json.toString();
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/appgroup/getAppGroupsForProject - STOP");
		return json.toString();
	}

	@RequestMapping(value = "restapi/appgroup/runAppGroupById", method = RequestMethod.GET)
	public RunAppGroupByNameResponse runAppGroupById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long idAppGroup) {
		LOG.info("restapi/appgroup/runAppGroupById - START");

		RunAppGroupByNameResponse json = new RunAppGroupByNameResponse();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			LOG.debug("idAppGroup " + idAppGroup);
			if (isUserValid) {
				return runAppGroup("" + idAppGroup, "id");
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.setIdAppGroup(idAppGroup);
				json.setStatus("failed");
				json.setMessage("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.setIdAppGroup(idAppGroup);
			json.setStatus("failed");
			LOG.error("Exception  " + e.getMessage());
			json.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		LOG.info("restapi/appgroup/runAppGroupById - END");
		return json;

	}

	@ApiOperation(value = "Run AppGroup by name", notes = "New API to run bunch of validations as clubbed via app group", tags = "AppGroup")
	@ResponseBody
	@RequestMapping(value = "restapi/appgroup/runAppGroupByName", method = RequestMethod.GET, produces = "application/json")
	public RunAppGroupByNameResponse runAppGroupByName(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "appGroupName", value = "AppGroup Name", example = "name") String appGroupName) {

		LOG.info("restapi/appgroup/runAppGroupByName - START");

		RunAppGroupByNameResponse json = new RunAppGroupByNameResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			LOG.debug("appGroupName " + appGroupName);
			if (isUserValid) {
				return runAppGroup(appGroupName, "name");
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.setIdAppGroup(Long.parseLong(appGroupName));
				json.setStatus("failed");
				json.setMessage("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			json.setIdAppGroup(Long.parseLong(appGroupName));
			json.setStatus("failed");
			json.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		LOG.info("restapi/appgroup/runAppGroupByName - END");
		return json;
	}

	private RunAppGroupByNameResponse runAppGroup(String idAppGroupStr, String parameterType) {

		LOG.info("runAppGroup - START");
		RunAppGroupByNameResponse json = new RunAppGroupByNameResponse();
		Long idAppGroup = 0l;

		LOG.debug("idAppGroupStr  " + idAppGroupStr + "  parameterType " + parameterType);
		// If ParameterType is "name" get the appGroup Id using name
		if (parameterType.equalsIgnoreCase("name")) {
			ListAppGroup listAppGroup = iTaskDAO.getListAppGroupByName(idAppGroupStr);
			if (listAppGroup != null)
				idAppGroup = listAppGroup.getIdAppGroup();
		} else {
			idAppGroup = Long.parseLong(idAppGroupStr);
		}

		if (idAppGroup != null && idAppGroup > 0l) {
			// Check if the same Id is in progress
			boolean isJobInProgress = iTaskDAO.checkIfAppGroupJobInProgress(idAppGroup);

			// Check if the same Id is Queued
			boolean isJobQueued = iTaskDAO.checkIfAppGroupJobInQueue(idAppGroup);

			// If same Id is in progress do not trigger again
			if (isJobInProgress || isJobQueued) {
				LOG.info("\n====> AppGroup Job with Id: " + idAppGroup
						+ " is already in progress!! So the job cannot be triggered !!");
				json.setIdAppGroup(idAppGroup);
				json.setStatus("failed");
				json.setMessage("AppGroup is already queued or in-progress");
			} else {
				// Trigger appGroup job
				LOG.info("\n====> Placing AppGroup with id:[" + idAppGroup + "] to Queue !!");

				// Place the Job in queue
				String uniqueId = iTaskDAO.addAppGroupJobToQueue(idAppGroup);
				LOG.info("\n====> AppGroup with id:[" + idAppGroup + "] placed to queue successfully with UniqueId : "
						+ uniqueId + " !!");

				json.setIdAppGroup(idAppGroup);
				json.setStatus("success");
				json.setMessage("AppGroup Placed in queue successfully");
				LOG.info("AppGroup Placed in queue successfully");
				json.setUniqueId(uniqueId);
			}
		} else {
			json.setIdAppGroup(idAppGroup);
			json.setStatus("failed");
			json.setMessage("Invalid AppGroup");
			LOG.error("Invalid AppGroup");
		}
		LOG.info("runAppGroup - END");
		return json;
	}

	@RequestMapping(value = "restapi/appgroup/checkAppGroupStatusById", method = RequestMethod.GET, produces = "application/json")
	public String checkAppGroupStatusById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam String id) {

		LOG.info("restapi/appgroup/checkAppGroupStatusById - START");
		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("id " + id);
			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = false;
				Long idAppGroup = null;

				if (id != null && !id.trim().isEmpty() && id.split("_").length == 3) {
					try {
						idAppGroup = Long.parseLong(id.split("_")[1]);
						isUniqueIdValid = true;
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Exception  " + e.getMessage());
					}
				}

				if (isUniqueIdValid && idAppGroup != null) {
					String appRunStatus = iTaskDAO.getAppGroupJobStatusByUniqueId(id);
					json.put("id", id);
					json.put("idAppGroup", idAppGroup);
					json.put("appGroupStatus", appRunStatus);

					JSONArray childArray = new JSONArray();

					// Get Associated validations status
					List<RunningTaskDTO> validationStatusList = iTaskDAO.getAppGroupJobValidationsStatus(id,
							idAppGroup);
					if (validationStatusList != null && validationStatusList.size() > 0) {
						for (RunningTaskDTO runningTaskDTO : validationStatusList) {
							JSONObject childObj = new JSONObject();
							childObj.put("appId", runningTaskDTO.getApplicationId());
							childObj.put("uniqueId", runningTaskDTO.getUniqueId());
							childObj.put("appStatus", runningTaskDTO.getStatus());
							childArray.put(childObj);
						}
					}
					json.put("validationStatusList", childArray);
				} else {
					json.put("id", id);
					json.put("message", "Invalid UniqueId");
					LOG.error("Invalid UniqueId");
				}
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.put("id", id);
				json.put("message", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("id", id);
			LOG.error("Exception  " + e.getMessage());
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/appgroup/checkAppGroupStatusById - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/appgroup/getAppGroupHistory", method = RequestMethod.GET, produces = "application/json")
	public String getAppGroupHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam Long idAppGroup) {

		LOG.info("restapi/appgroup/getAppGroupHistory - START");
		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("idAppGroup " + idAppGroup);
				// Check if appGroup is valid
				ListAppGroup appGroup = iTaskDAO.getListAppGroupById(idAppGroup);

				if (appGroup != null) {

					List<AppGroupJobDTO> appGroupHistoryList = iTaskDAO.getAppGroupHistoryById(idAppGroup);

					JSONArray childArray = new JSONArray();
					if (appGroupHistoryList != null && appGroupHistoryList.size() > 0) {
						for (AppGroupJobDTO appGroupJobDTO : appGroupHistoryList) {
							JSONObject childObj = new JSONObject();
							childObj.put("queueId", appGroupJobDTO.getQueueId());
							childObj.put("idAppGroup", appGroupJobDTO.getIdAppGroup());
							childObj.put("appGroupName", appGroupJobDTO.getAppGroupName());
							childObj.put("uniqueId", appGroupJobDTO.getUniqueId());
							childObj.put("status", appGroupJobDTO.getStatus());
							childObj.put("createdAt", appGroupJobDTO.getCreatedAt());
							childObj.put("sparkApplicationId", appGroupJobDTO.getSparkAppId());
							childObj.put("deployMode", appGroupJobDTO.getDeployMode());
							childObj.put("processId", appGroupJobDTO.getProcessId());
							childObj.put("startTime", appGroupJobDTO.getStartTime());
							childObj.put("endTime", appGroupJobDTO.getEndTime());
							childArray.put(childObj);
						}
					}
					json.put("history", childArray);

				} else {
					json.put("status", "failed");
					json.put("message", "Invalid AppGroupId");
					LOG.error("Invalid AppGroupId");
				}
			} else {
				LOG.error("\n===> Invalid username or password !!");
				json.put("status", "failed");
				json.put("message", "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("status", "failed");
			LOG.error("Exception  " + e.getMessage());
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/appgroup/getAppGroupHistory - END");
		return json.toString();
	}

	@ApiOperation(value = "Run schema by id", notes = "This API is used to create templates for all tables present for a "
			+ "schema", tags = "Schema")
	@RequestMapping(value = "restapi/schema/runSchema", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody RunSchemaResponse runSchema(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema) {

		LOG.info("restapi/schema/runSchema - START");
		RunSchemaResponse json = new RunSchemaResponse();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			LOG.debug("idDataSchema " + idDataSchema);
			if (isUserValid) {
				// Check if the same Id is in progress
				boolean isJobInProgress = iTaskDAO.checkIfSchemaJobInProgress(idDataSchema);

				// Check if the same Id is queued
				boolean isJobQueued = iTaskDAO.checkIfSchemaJobInQueue(idDataSchema);

				// If same Id is in progress do not trigger again
				if (isJobInProgress || isJobQueued) {
					LOG.info("\n====> Schema Job with Id: " + idDataSchema
							+ " is already in progress!! So the job cannot be triggered !!");
					json.setIdDataSchema(idDataSchema);
					json.setStatus("failed");
					json.setMessage("Schema is already queued or in-progress");
				} else {
					// Trigger appGroup job
					if (fileMonitorDao.isListDataSchemaActive(idDataSchema)) {
						LOG.info("\n====> Placing Schema with id:[" + idDataSchema + "] to Queue !!");

						// Place the Job in queue and put N for health check flag
						String uniqueId = iTaskDAO.addSchemaJobToQueue(idDataSchema, "N");
						LOG.info("\n====> Schema with id:[" + idDataSchema
								+ "] placed to queue successfully with UniqueId : " + uniqueId + " !!");

						json.setIdDataSchema(idDataSchema);
						json.setStatus("success");
						LOG.info("Schema Placed in queue successfully");
						json.setMessage("Schema Placed in queue successfully");
						json.setUniqueId(uniqueId);
					} else {
						json.setIdDataSchema(idDataSchema);
						json.setStatus("failed");
						LOG.error("Connection is inactive, cannot be executed");
						json.setMessage("Connection is inactive, cannot be executed");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}

				}

				return json;
			} else {
				json.setIdDataSchema(idDataSchema);
				json.setStatus("failed");
				LOG.error("Invalid Authorization");
				json.setMessage("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.setIdDataSchema(idDataSchema);
			json.setStatus("failed");
			LOG.error("Exception  " + e.getMessage());
			json.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		LOG.info("restapi/schema/runSchema - END");
		return json;
	}

	@RequestMapping(value = "restapi/schema/checkSchemaJobStatusById", method = RequestMethod.GET, produces = "application/json")
	public String checkSchemaJobStatusById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam String id) {

		LOG.info("restapi/schema/checkSchemaJobStatusById - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("id  " + id);
			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = false;
				Long idDataSchema = null;

				if (id != null && !id.trim().isEmpty() && id.split("_").length == 3) {
					try {
						idDataSchema = Long.parseLong(id.split("_")[1]);
						isUniqueIdValid = true;
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Exception  " + e.getMessage());
					}
				}

				if (isUniqueIdValid && idDataSchema != null) {
					String schemaJobStatus = iTaskDAO.getSchemaJobStatusByUniqueId(id);
					json.put("id", id);
					json.put("idDataSchema", idDataSchema);
					json.put("SchemaJobStatus", schemaJobStatus);

					JSONArray childArray = new JSONArray();

					// Get Associated Template status
					List<RunningTaskDTO> templateStatusList = iTaskDAO.getSchemaJobTemplatesStatus(id, idDataSchema);
					if (templateStatusList != null && templateStatusList.size() > 0) {
						for (RunningTaskDTO runningTaskDTO : templateStatusList) {
							JSONObject childObj = new JSONObject();
							childObj.put("idData", runningTaskDTO.getApplicationId());
							childObj.put("uniqueId", runningTaskDTO.getUniqueId());
							childObj.put("templateStatus", runningTaskDTO.getStatus());
							childArray.put(childObj);
						}
					}
					json.put("associatedTemplates", childArray);
				} else {
					json.put("id", id);
					json.put("message", "Invalid UniqueId");
					LOG.error("Invalid UniqueId");
				}
			} else {
				json.put("id", id);
				json.put("message", "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("id", id);
			LOG.error("Exception  " + e.getMessage());
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/schema/checkSchemaJobStatusById - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/schema/getSchemaJobHistory", method = RequestMethod.GET, produces = "application/json")
	public String getSchemaJobHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam Long idDataSchema) {

		LOG.info("restapi/schema/getSchemaJobHistory - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				// Check if idDataSchema is valid
				List<ListDataSchema> conList = listdatasourcedao.getListDataSchemaId(idDataSchema);

				// get list of dataSchema
				if (conList != null && conList.size() == 1) {

					List<SchemaJobDTO> schemaJobHistoryList = iTaskDAO.getSchemaJobHistoryById(idDataSchema);

					JSONArray childArray = new JSONArray();
					if (schemaJobHistoryList != null && schemaJobHistoryList.size() > 0) {
						for (SchemaJobDTO schemaJobDTO : schemaJobHistoryList) {
							JSONObject childObj = new JSONObject();
							childObj.put("queueId", schemaJobDTO.getQueueId());
							childObj.put("idDataSchema", schemaJobDTO.getIdDataSchema());
							childObj.put("schemaName", schemaJobDTO.getSchemaName());
							childObj.put("uniqueId", schemaJobDTO.getUniqueId());
							childObj.put("status", schemaJobDTO.getStatus());
							childObj.put("createdAt", schemaJobDTO.getCreatedAt());
							childObj.put("sparkApplicationId", schemaJobDTO.getSparkAppId());
							childObj.put("deployMode", schemaJobDTO.getDeployMode());
							childObj.put("processId", schemaJobDTO.getProcessId());
							childObj.put("startTime", schemaJobDTO.getStartTime());
							childObj.put("endTime", schemaJobDTO.getEndTime());
							childArray.put(childObj);
						}
					}
					json.put("history", childArray);

				} else {
					json.put("status", "failed");
					json.put("message", "Invalid Schema Id");
					LOG.error("Invalid Schema Id");
				}
			} else {
				json.put("status", "failed");
				json.put("message", "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("status", "failed");
			LOG.error("Exception  " + e.getMessage());
			json.put("message", "unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/schema/getSchemaJobHistory - END");
		return json.toString();
	}

	@ApiOperation(value = "Run project by project id", notes = "Runs all connections under a project for rules discovery - input "
			+ "projectID " + "Run Project to run all the connections in it, it place the job in queue "
			+ "and returns a unique Id", tags = "Project")
	@ResponseBody
	@RequestMapping(value = "restapi/project/runProject", method = RequestMethod.GET, produces = "application/json")
	public RunProjectResponse runProject(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "projectId", value = "Project Id", example = "1") long projectId) {

		LOG.info("restapi/project/runProject - START");

		RunProjectResponse json = new RunProjectResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			LOG.debug("projectId " + projectId);
			json.setProjectId(projectId);

			if (isUserValid) {

				Project project = projectDAO.getSelectedProject(projectId);
				if (project != null) {

					// Check if the same Id is in progress
					boolean isJobInProgress = iTaskDAO.checkIfProjectJobInProgress(projectId);

					// Check if the same Id is queued
					boolean isJobQueued = iTaskDAO.checkIfProjectJobInQueue(projectId);

					// If same Id is in progress do not trigger again
					if (isJobInProgress || isJobQueued) {
						LOG.info("\n====> Project Job with Id: " + projectId
								+ " is already in progress!! So the job cannot be triggered !!");
						json.setStatus("failed");
						json.setMessage("Project is already queued or in-progress");
					} else {
						// Trigger Project job
						LOG.info("\n====> Placing Project with id:[" + projectId + "] to Queue !!");

						// Place the Job in queue
						String uniqueId = iTaskDAO.addProjectJobToQueue(projectId);
						LOG.info("\n====> Project with id:[" + projectId
								+ "] placed to queue successfully with UniqueId : " + uniqueId + " !!");

						json.setStatus("success");
						json.setMessage("Project Placed in queue successfully");
						json.setUniqueId(uniqueId);
					}

					return json;
				} else {
					json.setStatus("failed");
					json.setMessage("Invalid Project");
					LOG.error("Invalid Project");
				}
			} else {
				json.setStatus("failed");
				json.setMessage("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.setStatus("failed");
			LOG.error("Exception  " + e.getMessage());
			json.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		LOG.info("restapi/project/runProject - END");
		return json;
	}

	@ApiOperation(value = "Run project by name", notes = "This API is used to run a project by passing its name as argument", tags = "Project")
	@ResponseBody
	@RequestMapping(value = "restapi/project/runProjectByName", method = RequestMethod.GET, produces = "application/json")
	public RunProjectByNameResponse runProjectByName(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "projectName", value = "Project Name", example = "name") String projectName) {

		LOG.info("restapi/project/runProjectByName - START");
		RunProjectByNameResponse json = new RunProjectByNameResponse();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			json.setProjectName(projectName);
			LOG.debug("projectName " + projectName);
			if (isUserValid) {

				// check if the project name is valid
				int projectId = projectDAO.getProjectIdByProjectName(projectName);

				if (projectId > 0) {

					// Check if the same Id is in progress
					boolean isJobInProgress = iTaskDAO.checkIfProjectJobInProgress(Long.valueOf(projectId));

					// Check if the same Id is queued
					boolean isJobQueued = iTaskDAO.checkIfProjectJobInQueue(Long.valueOf(projectId));

					// If same Id is in progress do not trigger again
					if (isJobInProgress || isJobQueued) {
						LOG.info("\n====> Project Job with Id: " + projectId
								+ " is already in progress!! So the job cannot be triggered !!");
						message = "Project Job is already queued or in-progress";
					} else {
						// Trigger Project job
						LOG.info("\n====> Placing Project with id:[" + projectId + "] to Queue !!");

						// Place the Job in queue
						String uniqueId = iTaskDAO.addProjectJobToQueue(projectId);
						LOG.info("\n====> Project with id:[" + projectId
								+ "] placed to queue successfully with UniqueId : " + uniqueId + " !!");

						status = "success";
						message = "Project Placed in queue successfully";
						json.setUniqueId(uniqueId);
					}

				} else {
					message = "Invalid Project Name";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error(message);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/project/runProjectByName - END");
		return json;
	}

	@ApiOperation(value = "Run domain by id", notes = "This API is used to run domain as per the id", tags = "Domain")
	@ResponseBody
	@RequestMapping(value = "restapi/domain/runDomain", method = RequestMethod.GET, produces = "application/json")
	public RunDomainDto runDomain(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "domainId", value = "Domain Id", example = "1") long domainId) {

		LOG.info("restapi/domain/runDomain - START");

		RunDomainDto json = new RunDomainDto();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			json.setDomainId(domainId);
			LOG.debug("domainId " + domainId);
			if (isUserValid) {

				Domain domain = iTaskDAO.getDomainDetailsById(domainId);
				if (domain != null) {

					// Check if the same Id is in progress
					boolean isJobInProgress = iTaskDAO.checkIfDomainJobInProgress(domainId);

					// Check if the same Id is queued
					boolean isJobQueued = iTaskDAO.checkIfDomainJobInQueue(domainId);

					// If same Id is in progress do not trigger again
					if (isJobInProgress || isJobQueued) {
						LOG.info("\n====> Domain Job with Id: " + domainId
								+ " is already in progress!! So the job cannot be triggered !!");
						json.setStatus("failed");
						json.setMessage("Domain is already queued or in-progress");
					} else {
						// Trigger Domain job
						LOG.info("\n====> Placing Domain with id:[" + domainId + "] to Queue !!");

						// Place the Job in queue
						String uniqueId = iTaskDAO.addDomainJobToQueue(domainId);
						LOG.info("\n====> Domain with id:[" + domainId
								+ "] placed to queue successfully with UniqueId : " + uniqueId + " !!");

						json.setStatus("success");
						json.setMessage("Domain Placed in queue successfully");
						json.setUniqueId(uniqueId);
					}

					return json;
				} else {
					json.setStatus("failed");
					json.setMessage("Invalid Domain");
					LOG.error("Invalid Domain");
				}
			} else {
				json.setStatus("failed");
				json.setMessage("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.setStatus("failed");
			json.setMessage("unexpected exception occurred");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();

		}
		LOG.info("restapi/domain/runDomain - END");
		return json;
	}

	@RequestMapping(value = "restapi/domain/checkDomainJobStatusById", method = RequestMethod.GET, produces = "application/json")
	public String checkDomainJobStatusById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam String id) {
		LOG.info("restapi/domain/checkDomainJobStatusById - START");

		JSONObject domainJson = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = false;
				Long domainId = null;

				if (id != null && !id.trim().isEmpty() && id.split("_").length == 3) {
					try {
						isUniqueIdValid = iTaskDAO.isDomainUniqueIdValid(id);
						if (isUniqueIdValid) {
							domainId = Long.parseLong(id.split("_")[1]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Exception  " + e.getMessage());
					}
				}

				if (isUniqueIdValid && domainId != null) {
					String domainJobStatus = iTaskDAO.getDomainJobStatusByUniqueId(id);
					domainJson.put("id", id);
					domainJson.put("domainId", domainId);
					domainJson.put("domainJobStatus", domainJobStatus);

					JSONArray projectArray = new JSONArray();

					// Get Associated project list
					List<ProjectJobDTO> domainProjectList = iTaskDAO.getDomainJobAssociatedProjects(id, domainId);

					for (ProjectJobDTO projectJobDTO : domainProjectList) {
						String project_uniqueId = projectJobDTO.getUniqueId();
						long projectId = projectJobDTO.getProjectId();

						JSONObject projectObj = new JSONObject();
						projectObj.put("projectId", projectId);
						projectObj.put("projectName", projectJobDTO.getProjectName());
						projectObj.put("project_uniqueId", project_uniqueId);
						projectObj.put("projectJobStatus", projectJobDTO.getStatus());

						List<RunningTaskDTO> schemaStatusList = iTaskDAO.getProjectJobSchemaStatus(project_uniqueId,
								projectId);

						if (schemaStatusList != null && schemaStatusList.size() > 0) {
							JSONArray connectionArray = new JSONArray();
							for (RunningTaskDTO runningTaskDTO : schemaStatusList) {
								JSONObject templateObj = new JSONObject();
								templateObj.put("connectionId", runningTaskDTO.getApplicationId());
								templateObj.put("connection_uniqueId", runningTaskDTO.getUniqueId());
								templateObj.put("connectionStatus", runningTaskDTO.getStatus());
								connectionArray.put(templateObj);
							}

							projectObj.put("associatedConnections", connectionArray);
						}

						projectArray.put(projectObj);
					}
					domainJson.put("associatedProjects", projectArray);

				} else {
					domainJson.put("id", id);
					domainJson.put("message", "Invalid UniqueId");
					LOG.error("Invalid UniqueId");
				}
			} else {
				domainJson.put("id", id);
				domainJson.put("message", "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			domainJson.put("id", id);
			domainJson.put("message", "unexpected exception occurred");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/domain/checkDomainJobStatusById - END");
		return domainJson.toString();
	}

	// API - For domain lite
	@RequestMapping(value = "restapi/domain/runDomainLite", method = RequestMethod.GET, produces = "application/json")
	public String performDomainLite(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long domainId) {

		LOG.info("restapi/domain/runDomainLite - START");
		JSONObject json = new JSONObject();
		String message = "";
		String status = "failed";
		try {
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			// validate user
			if (isUserValid) {

				// check if domainId is valid
				Domain domain = iTaskDAO.getDomainDetailsById(domainId);
				if (domain != null) {

					// Check if the same Id is in progress
					boolean isJobInProgress = iTaskDAO.checkIfDomainLiteJobInProgress(domainId);

					// Check if the same Id is queued
					boolean isJobQueued = iTaskDAO.checkIfDomainLiteJobInQueue(domainId);

					// If same Id is in progress do not trigger again
					if (isJobInProgress || isJobQueued) {
						LOG.info("\n====> DomainLite Job with Id: " + domainId
								+ " is already in progress!! So the job cannot be triggered !!");

						message = "DomainLite is already queued or in-progress";
						LOG.info(message);
					} else {

						// Trigger Domain Lite job
						LOG.info("\n====> Placing Domain job with id:[" + domainId + "] to Queue !!");

						// Place the Domain Lite Job into the queue
						String uniqueId = iTaskDAO.addDomainLiteJobToQueue(domainId);
						LOG.info("\n====> Domain with id:[" + domainId
								+ "] placed to queue successfully with UniqueId : " + uniqueId + " !!");

						if (uniqueId != null && !uniqueId.isEmpty()) {
							status = "success";
							message = "Domain Lite job with Id:[" + domainId + "] is placed in queue successfully";
							json.put("uniqueId", uniqueId);
							LOG.info(message);
						} else {
							message = "Failed to put Domain Lite job with Id[" + domainId + "] into queue";
							LOG.error(message);
						}

					}
				} else {
					message = "Invalid Domain Id";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/domain/runDomainLite - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/domain/checkDomainLiteJobStatusById", method = RequestMethod.GET, produces = "application/json")
	public String checkDomainLiteJobStatusById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam String id) {

		LOG.info("restapi/domain/checkDomainLiteJobStatusById - START");

		JSONObject domainJson = new JSONObject();
		String message = "";
		String status = "failed";
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			LOG.debug("id " + id);
			// Set the uniqueId to response
			domainJson.put("id", id);

			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = false;
				Long domainId = null;

				if (id != null && !id.trim().isEmpty() && id.split("_").length == 3) {
					try {
						isUniqueIdValid = iTaskDAO.isDomainLiteUniqueIdValid(id);
						if (isUniqueIdValid) {
							domainId = Long.parseLong(id.split("_")[1]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Exception  " + e.getMessage());
					}
				}

				if (isUniqueIdValid && domainId != null) {
					String domainLiteJobStatus = iTaskDAO.getDomainLiteJobStatusByUniqueId(id);

					domainJson.put("domainId", domainId);
					status = domainLiteJobStatus;

				} else
					message = "Invalid UniqueId";

			} else {
				message = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			LOG.error(message);
		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		domainJson.put("status", status);
		domainJson.put("message", message);
		LOG.info("restapi/domain/checkDomainLiteJobStatusById - END");
		return domainJson.toString();
	}

	@RequestMapping(value = "restapi/domain/getDomainLiteJobResultById", method = RequestMethod.GET, produces = "application/json")
	public String getDomainLiteJobResultById(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(name = "id") String id) {
		LOG.info("restapi/domain/getDomainLiteJobResultById - START");

		String status = "failed";
		String message = "";

		JSONObject domainJson = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("id " + id);
			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = iTaskDAO.isDomainLiteUniqueIdValid(id);

				if (isUniqueIdValid) {

					String domainLiteJobResult = iTaskDAO.getDomainLiteJobResultByUniqueId(id);
					if (domainLiteJobResult != null && !domainLiteJobResult.isEmpty()) {
						JSONObject resultObj = new JSONObject(domainLiteJobResult);
						status = "success";
						domainJson.put("result", resultObj);
					} else
						message = "Failed to get the result";

				} else
					message = "Invalid Unique Id";

			} else {
				message = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.error(message);
		domainJson.put("status", status);
		domainJson.put("message", message);
		LOG.info("restapi/domain/getDomainLiteJobResultById - END");
		return domainJson.toString();
	}

	@RequestMapping(value = "restapi/domain/getDomainLiteJobHistory", method = RequestMethod.GET)
	public String getDomainLiteJobHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(name = "domainId") long domainId) {
		LOG.info("restapi/domain/getDomainLiteJobHistory - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			LOG.debug("domainId " + domainId);
			if (isUserValid) {
				// Check if domainId is valid
				Domain domain = iTaskDAO.getDomainDetailsById(domainId);

				if (domain != null) {

					// get List of history for given domain Id
					List<DomainLiteJobDTO> domainLiteJobHistoryList = iTaskDAO.getDomainLiteJobHistoryById(domainId);

					JSONArray domainLiteJobHistoryArray = new JSONArray();

					if (domainLiteJobHistoryList != null && domainLiteJobHistoryList.size() > 0) {

						ObjectMapper mapper = new ObjectMapper();

						// converting domain lite history pojo object to json
						for (DomainLiteJobDTO domainLiteJobDTO : domainLiteJobHistoryList) {
							JSONObject childObj = new JSONObject(mapper.writeValueAsString(domainLiteJobDTO));
							domainLiteJobHistoryArray.put(childObj);
						}
					}
					json.put("history", domainLiteJobHistoryArray);

				} else
					message = "Invalid DomainId";
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				message = "Invalid Authorization";
			}
		} catch (Exception e) {
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.error(message);
		json.put("status", status);
		json.put("message", message);
		LOG.info("restapi/domain/getDomainLiteJobHistory - END");
		return json.toString();
	}

	@ApiOperation(value = "Get project job status by id", notes = "Check the status of the project job using UniqueId -- it shows the"
			+ "details of the connections running and templates running under it" + "and its status", tags = "Project")
	@ResponseBody
	@RequestMapping(value = "restapi/project/checkProjectJobStatusById", method = RequestMethod.GET, produces = "application/json")
	public CheckProjectJobStatusByIdResponse checkProjectJobStatusById(HttpServletResponse response,
			HttpServletRequest request, @RequestParam @ApiParam(name = "id", value = "Id", example = "1") String id) {
		LOG.info("restapi/project/checkProjectJobStatusById - START");

		CheckProjectJobStatusByIdResponse projectJson = new CheckProjectJobStatusByIdResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			LOG.debug("id " + id);
			if (isUserValid) {
				// Check if uniqueId is valid
				boolean isUniqueIdValid = false;
				Long projectId = null;

				if (id != null && !id.trim().isEmpty() && id.split("_").length == 3) {
					try {
						isUniqueIdValid = iTaskDAO.isProjectUniqueIdValid(id);
						if (isUniqueIdValid) {
							projectId = Long.parseLong(id.split("_")[1]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Exception  " + e.getMessage());
					}
				}

				if (isUniqueIdValid && projectId != null) {
					String projectJobStatus = iTaskDAO.getProjectJobStatusByUniqueId(id);
					projectJson.setId(id);
					projectJson.setProjectId(projectId);
					projectJson.setProjectJobStatus(projectJobStatus);

					ArrayList<ConnectionDto> connArray = new ArrayList<ConnectionDto>();

					// Get Associated connections and list
					List<SchemaJobDTO> projectConList = iTaskDAO.getProjectJobAssociatedConnections(id, projectId);

					for (SchemaJobDTO schemaJobDTO : projectConList) {
						String connection_uniqueId = schemaJobDTO.getUniqueId();
						long connectionId = schemaJobDTO.getIdDataSchema();

						ConnectionDto conObj = new ConnectionDto();
						conObj.setConnectionId(connectionId);
						conObj.setConnectionName(schemaJobDTO.getSchemaName());
						conObj.setConnection_uniqueId(connection_uniqueId);
						conObj.setConnectionJobStatus(schemaJobDTO.getStatus());

						List<RunningTaskDTO> templateStatusList = iTaskDAO
								.getSchemaJobTemplatesStatus(connection_uniqueId, connectionId);

						if (templateStatusList != null && templateStatusList.size() > 0) {
							ArrayList<TemplateDto> templateArray = new ArrayList<TemplateDto>();
							for (RunningTaskDTO runningTaskDTO : templateStatusList) {
								TemplateDto templateObj = new TemplateDto();
								templateObj.setTemplateId(runningTaskDTO.getApplicationId());
								templateObj.setTemplate_uniqueId(runningTaskDTO.getUniqueId());
								templateObj.setTemplateStatus(runningTaskDTO.getStatus());
								templateArray.add(templateObj);
							}

							conObj.setAssociatedTemplates(templateArray);
						}

						connArray.add(conObj);
					}
					projectJson.setAssociatedConnections(connArray);

				} else {
					projectJson.setId(id);
					projectJson.setMessage("Invalid UniqueId");
					LOG.error("Invalid UniqueId");
				}
			} else {
				projectJson.setId(id);
				projectJson.setMessage("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			projectJson.setId(id);
			LOG.error("Exception  " + e.getMessage());
			projectJson.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/project/checkProjectJobStatusById - END");
		return projectJson;
	}

	@RequestMapping(value = "restapi/domain/getDomainJobHistory", method = RequestMethod.GET)
	public String getDomainJobHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam long domainId) {

		LOG.info("restapi/domain/getDomainJobHistory - START");

		JSONObject json = new JSONObject();
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("domainId " + domainId);
			if (isUserValid) {
				// Check if domainId is valid
				Domain domain = iTaskDAO.getDomainDetailsById(domainId);

				if (domain != null) {
					List<DomainJobDTO> domainJobHistoryList = iTaskDAO.getDomainJobHistoryById(domainId);

					JSONArray childArray = new JSONArray();

					if (domainJobHistoryList != null && domainJobHistoryList.size() > 0) {

						for (DomainJobDTO domainJobDTO : domainJobHistoryList) {
							JSONObject childObj = new JSONObject();
							childObj.put("queueId", domainJobDTO.getQueueId());
							childObj.put("domainId", domainJobDTO.getDomainId());
							childObj.put("domainName", domainJobDTO.getDomainName());
							childObj.put("domain_uniqueId", domainJobDTO.getUniqueId());
							childObj.put("status", domainJobDTO.getStatus());
							childObj.put("createdAt", domainJobDTO.getCreatedAt());
							childObj.put("sparkApplicationId", domainJobDTO.getSparkAppId());
							childObj.put("deployMode", domainJobDTO.getDeployMode());
							childObj.put("processId", domainJobDTO.getProcessId());
							childObj.put("startTime", domainJobDTO.getStartTime());
							childObj.put("endTime", domainJobDTO.getEndTime());
							childArray.put(childObj);
						}
					}
					json.put("history", childArray);

				} else {
					json.put("status", "failed");
					json.put("message", "Invalid DomainId");
					LOG.error("Invalid DomainId");
				}
			} else {
				json.put("status", "failed");
				json.put("message", "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			json.put("status", "failed");
			json.put("message", "unexpected exception occurred");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/domain/getDomainJobHistory - END");
		return json.toString();
	}

	@ApiOperation(value = "Get project job history by project id", notes = "Get the project Job execution history", tags = "Project")
	@ResponseBody
	@RequestMapping(value = "restapi/project/getProjectJobHistory", method = RequestMethod.GET, produces = "application/json")
	public GetProjectJobHistoryResponse getProjectJobHistory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam @ApiParam(name = "projectId", value = "Project Id", example = "1") long projectId) {
		LOG.info("restapi/project/getProjectJobHistory - START");

		GetProjectJobHistoryResponse getProjectJobHistoryResponse = new GetProjectJobHistoryResponse();
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			LOG.debug("projectId " + projectId);
			if (isUserValid) {
				// Check if projectId is valid
				Project project = projectDAO.getSelectedProject(projectId);

				if (project != null) {
					List<ProjectJobDTO> projectJobHistoryList = iTaskDAO.getProjectJobHistoryById(projectId);

					ArrayList<ProjectJobHistoryDto> childArray = new ArrayList<ProjectJobHistoryDto>();

					if (projectJobHistoryList != null && projectJobHistoryList.size() > 0) {
						SimpleDateFormat createDateFormat = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
						for (ProjectJobDTO projectJobDTO : projectJobHistoryList) {
							ProjectJobHistoryDto childObj = new ProjectJobHistoryDto();
							childObj.setQueueId(projectJobDTO.getQueueId());
							childObj.setProjectId(projectJobDTO.getProjectId());
							childObj.setProjectName(projectJobDTO.getProjectName());
							childObj.setProject_uniqueId(projectJobDTO.getUniqueId());
							childObj.setStatus(projectJobDTO.getStatus());
							childObj.setCreatedAt(projectJobDTO.getCreatedAt() != null
									? createDateFormat.format(projectJobDTO.getCreatedAt())
									: "");
							childObj.setSparkApplicationId(projectJobDTO.getSparkAppId());
							childObj.setDeployMode(projectJobDTO.getDeployMode());
							childObj.setProcessId(projectJobDTO.getProcessId());
							childObj.setStartTime(projectJobDTO.getStartTime() != null
									? timeFormat.format(projectJobDTO.getStartTime())
									: "");
							childObj.setEndTime(
									projectJobDTO.getEndTime() != null ? timeFormat.format(projectJobDTO.getEndTime())
											: "");
							childArray.add(childObj);
						}
					}
					getProjectJobHistoryResponse.setHistory(childArray);

				} else {
					getProjectJobHistoryResponse.setStatus("failed");
					getProjectJobHistoryResponse.setMessage("Invalid ProjectId");
					LOG.error("Invalid ProjectId");
				}
			} else {
				getProjectJobHistoryResponse.setStatus("failed");
				getProjectJobHistoryResponse.setMessage("Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			getProjectJobHistoryResponse.setStatus("failed");
			getProjectJobHistoryResponse.setMessage("unexpected exception occurred");
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/project/getProjectJobHistory - END");
		return getProjectJobHistoryResponse;
	}

	// API - For AGING check
	@ApiOperation(value = "Get aging issues for validations", notes = "API to return issue details of aging for a specific validation and dqrId", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/aging/getAgingIssuesForValidation", method = RequestMethod.POST, produces = "application/json")
	public GenericResponse<ArrayList<GetAgingIssuesForValidationDto>> getAgingIssuesForValidation(
			HttpServletResponse response, HttpServletRequest request,
			@RequestBody AgingIssuesForValidationReq agingIssuesForValReq) {

		LOG.info("restapi/aging/getAgingIssuesForValidation - START");
		GenericResponse<ArrayList<GetAgingIssuesForValidationDto>> json = new GenericResponse<ArrayList<GetAgingIssuesForValidationDto>>();

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			String errorMsg = "";
			LOG.debug("Getting  parameters for agingIssuesForValReq , " + agingIssuesForValReq);
			if (isUserValid) {
				if (agingIssuesForValReq != null) {
					// Fetch validation details
					long idApp = agingIssuesForValReq.getValidationId();
					LOG.debug("\n===> ValidationId: " + idApp);

					ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

					if (listApplication != null) {
						// Get DQRID
						String dqrId = agingIssuesForValReq.getDqrId();
						LOG.debug("\n===> DQRID: " + dqrId);

						if (dqrId != null && !dqrId.trim().isEmpty()) {
							// Identify the Domain name of the validation
							String domainName = iTaskDAO.getDomainNameById(listApplication.getDomainId());

							if (domainName != null && !domainName.trim().isEmpty()) {

								ArrayList<GetAgingIssuesForValidationDto> jsonArray = new ArrayList<GetAgingIssuesForValidationDto>();

								String host = agingIssuesForValReq.getHiveserverName();
								LOG.debug("\n===> Hive server name:" + host);

								long port = agingIssuesForValReq.getHiveserverport();
								LOG.debug("\n===> Hive server port:" + port);

								// ProjectName is equivalent to database name
								String hiveDatabase = domainName;
								LOG.debug("\n===> Hive database name:" + hiveDatabase);

								// Prepare quer to get the Aging issues list upto 1000 rows
								String query = "select Validation_ID,Open_Date,Run,DQR_ID,Key_Value,Key_Column,Exposure_Amount,Date_Closed from exception_data_master where Validation_ID="
										+ idApp + " and DQR_ID='" + dqrId + "' order by Open_Date desc limit 1000";

								// Run the Hive Script to get the rows
								String databuckHome = databuckFileUtility.getDatabuckHome();
								String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
										+ " com.databuck.mapr.hive.ReadQueryResult " + host + ":" + port + " "
										+ hiveDatabase + " \"" + query + "\"";

								LOG.debug("\n===> shellCommand - " + shellCommand);

								// Execute Script
								ProcessBuilder processBuilder = new ProcessBuilder();
								processBuilder.command("bash", "-c", shellCommand);
								Process process = processBuilder.start();

								// Read output
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(process.getInputStream()));

								String line;
								boolean resultStart = false;

								LOG.debug("\n===> Reading Result..");
								while ((line = reader.readLine()) != null) {
									if (resultStart && !line.contains("*QueryResult End*")) {
										String[] data = line.split(",");

										GetAgingIssuesForValidationDto jsonObj = new GetAgingIssuesForValidationDto();
										jsonObj.setValidation_ID(data[0]);
										jsonObj.setOpen_Date(data[1]);
										jsonObj.setRun(data[2]);
										jsonObj.setDQR_ID(data[3]);
										jsonObj.setKey_Value(data[4]);
										jsonObj.setKey_Column(data[5]);
										jsonObj.setExposure_Amount(data[6]);
										jsonObj.setDate_Closed(data[7]);
										jsonArray.add(jsonObj);
									}

									// Start of Query result
									if (line.contains("*QueryResult Start*")) {
										resultStart = true;
									}
								}

								int exitVal = process.waitFor();

								if (exitVal == 0) {
									LOG.info("\n===> Reading Query Result is Successful!");
									json.setStatus("success");
									json.setResult(jsonArray);
									return json;
								} else {
									errorMsg = "Failed to get data";
								}

							} else
								errorMsg = "Failed to get Domain name of validation";

						} else
							errorMsg = "Invalid DQRID";

					} else
						errorMsg = "Invalid ValidationId";

				} else
					errorMsg = "Invalid Json Request";

				LOG.error(errorMsg);
				json.setStatus("failed");
				json.setMessage(errorMsg);
			} else {
				LOG.error("Invalid Authorization");
				json.setStatus("failed");
				json.setMessage("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			json.setStatus("failed");
			json.setMessage("unexpected exception occurred");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		LOG.info("restapi/aging/getAgingIssuesForValidation - END");
		return json;
	}

	// API - For Failed Notification Details
	@ApiOperation(value = "Get failed API notifications", notes = "This method is to get the list of notifications which were failed to "
			+ "publish to netcool and steel for a specific time range.", tags = "Notification")
	@ResponseBody
	@RequestMapping(value = "restapi/notification/getFailedAPINotifications", method = RequestMethod.GET, produces = "application/json")
	public GenericResponse<ArrayList<String>> getFailedAPINotifications(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam @ApiParam(name = "externalApiType", value = "External API Type") String externalApiType,
			@RequestParam @ApiParam(name = "startDate", value = "Start Date", example = "yyyy-MM-dd HH:mm:ss") String startDate,
			@RequestParam @ApiParam(name = "endDate", value = "End Date", example = "yyyy-MM-dd HH:mm:ss") String endDate) {

		LOG.info("restapi/notification/getFailedAPINotifications - START");

		GenericResponse<ArrayList<String>> json = new GenericResponse<ArrayList<String>>();
		String errorMsg = "";
		String status = "failed";
		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			// validate user
			if (isUserValid) {

				if (externalApiType != null && !externalApiType.trim().isEmpty()) {

					// externalApiType can be steel or netcool only
					if (externalApiType.trim().equalsIgnoreCase("steel")
							|| externalApiType.trim().equalsIgnoreCase("netcool")) {

						// startDate and endDate can not be empty
						if (startDate != null && !startDate.trim().isEmpty() && endDate != null
								&& !endDate.trim().isEmpty()) {

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							boolean isStartDateValid = false;
							boolean isEndDateValid = false;
							Date valid_start_date = null;
							Date valid_end_date = null;

							// Validate Start Date
							try {
								valid_start_date = sdf.parse(startDate.trim());
								isStartDateValid = true;
							} catch (Exception e) {
								e.printStackTrace();
								LOG.error("Exception  " + e.getMessage());
							}

							// Validate End Date
							try {
								valid_end_date = sdf.parse(endDate.trim());
								isEndDateValid = true;
							} catch (Exception e) {
								e.printStackTrace();
								LOG.error("Exception  " + e.getMessage());
							}

							if (!isStartDateValid)
								errorMsg = "StartDate is not valid, valid format is yyyy-MM-dd HH:mm:ss";
							else if (!isEndDateValid)
								errorMsg = "EndDate is not valid, valid format is yyyy-MM-dd HH:mm:ss";
							else if (isStartDateValid && isEndDateValid && valid_start_date.after(valid_end_date))
								errorMsg = "EndDate must be greater than startDate";
							else {
								// Get List of ExternalAlertAPIPojo
								List<ExternalAPIAlertPOJO> externalAPIAlertPOJOList = iResultsDAO
										.getFailedExternalNotificationDetails(externalApiType, startDate, endDate);

								// verify list of pojos
								if (externalAPIAlertPOJOList != null && externalAPIAlertPOJOList.size() > 0) {

									ArrayList<String> externalAPIAlertArr = new ArrayList<String>();

									for (ExternalAPIAlertPOJO apiAlertPOJO : externalAPIAlertPOJOList) {
										externalAPIAlertArr.add(apiAlertPOJO.getAlertJson());
									}
									status = "success";
									json.setResult(externalAPIAlertArr);

								} else
									errorMsg = "No failed notifications found for API Type[" + externalApiType + "]";
							}
						} else
							errorMsg = "Start and End date should not be empty";
					} else
						errorMsg = "Invalid external API Type";

				} else
					errorMsg = "External API Type is Empty";

			} else {
				errorMsg = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			LOG.error(errorMsg);

		} catch (Exception e) {
			errorMsg = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			e.printStackTrace();
		}
		json.setStatus(status);
		json.setMessage(errorMsg);
		LOG.info("restapi/notification/getFailedAPINotifications - END");
		return json;
	}

	@ApiOperation(value = "Rerun template by id", notes = "This API is used to rerun an existing template", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/template/reRunTemplate", method = RequestMethod.GET, produces = "application/json")
	public RunProjectByNameResponse reRunTemplate(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @ApiParam(name = "idData", value = "Id", example = "1") long idData) {
		LOG.info("restapi/template/reRunTemplate - START");

		RunProjectByNameResponse json = new RunProjectByNameResponse();

		String status = "failed";
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");

			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				// check if the Template Id is valid or not
				LOG.debug("idData " + idData);
				ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);

				if (listDataSource != null) {

					boolean isActive = listDataSource.getActive().trim().equalsIgnoreCase("yes") ? true : false;

					if (isActive) {
						// Check Current status of template
						boolean isTemplateInProgress = iTaskDAO.isTemplateJobQueuedOrInProgress(idData);

						if (!isTemplateInProgress) {
							// Place template to queue
							String uniqueId = "";
							if (listDataSource.getTemplateCreateSuccess() != null
									&& listDataSource.getTemplateCreateSuccess().equalsIgnoreCase("N")) {
								uniqueId = iTaskDAO.placeTemplateJobInQueue(idData,
										TemplateRunTypes.newtemplate.toString());
							} else {
								uniqueId = iTaskDAO.placeTemplateJobInQueue(idData,
										TemplateRunTypes.templatererun.toString());
							}
							message = "Template rerun job is placed in queue successfully";
							status = "success";
							LOG.info(message);
							json.setUniqueId(uniqueId);
						} else {
							message = "Template run is already in progress";
							LOG.error(message);
						}

					} else {
						message = "Template is InActive";
						LOG.error(message);
					}

				} else {
					message = "Invalid templateId";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}

		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			e.printStackTrace();
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/template/reRunTemplate - END");
		return json;
	}

	@ApiOperation(value = "Get template job status by id", notes = "This API is used to check the status of the template job using the "
			+ "uniqueId", tags = "Template")
	@ResponseBody
	@RequestMapping(value = "restapi/template/checkTemplateJobStatusById", method = RequestMethod.GET, produces = "application/json")
	public RunProjectByNameResponse checkTemplateStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @ApiParam(name = "id", value = "Id", example = "1") String id) {

		LOG.info("restapi/template/checkTemplateJobStatusById - START");

		RunProjectByNameResponse json = new RunProjectByNameResponse();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				// check if the Template Id is valid or not
				LOG.debug("id " + id);
				boolean isUniqueIdValid = iTaskDAO.isTemplateUniqueIdValid(id);

				if (isUniqueIdValid) {

					// Check Current status of template
					status = iTaskDAO.getTemplateRunJobStatusById(id);

					json.setUniqueId(id);

				} else {
					message = "Invalid uniqueId";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
			message = "unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/template/checkTemplateJobStatusById - END");
		return json;
	}

	@ApiOperation(value = "Kill validation run by id", notes = "This API is used to kill the validation job using the Unique Id", tags = "Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/validation/killValidationRun", method = RequestMethod.GET, produces = "application/json")
	public GenericResponse<String> killValidationRun(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @ApiParam(name = "id", value = "Id", example = "1") String id) {

		LOG.info("restapi/validation/killValidationRun - START");

		GenericResponse<String> json = new GenericResponse<String>();
		String status = "failed";
		Boolean isStatus = false;
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				LOG.debug("id " + id);
				// get run details by unique Id
				RunningTaskDTO runningTaskDTO = iTaskDAO.getValidationRunDetailsByUniqueId(id);

				if (runningTaskDTO != null) {
					boolean isValidationJobRunning = true;
					String validationRunStatus = runningTaskDTO.getStatus().trim();

					// check if validation is running
					if (validationRunStatus.equalsIgnoreCase("completed")
							|| validationRunStatus.equalsIgnoreCase("failed")
							|| validationRunStatus.equalsIgnoreCase("killed"))
						isValidationJobRunning = false;

					// kill the validation if its running
					if (isValidationJobRunning) {
						String taskType = runningTaskDTO.getTaskType();
						Long taskId = runningTaskDTO.getApplicationId();
						Long processId = runningTaskDTO.getProcessId();
						String deployMode = runningTaskDTO.getDeployMode();
						String sparkAppId = runningTaskDTO.getSparkAppId();
						String uniqueId = id;
//						boolean killStatus = runningJobService.killTemplateOrValidationJob(
//								runningTaskDTO.getApplicationId(), runningTaskDTO.getTaskType(),
//								runningTaskDTO.getProcessId(), runningTaskDTO.getDeployMode(),
//								runningTaskDTO.getSparkAppId(), id);
//						if (killStatus) {
//							message = "Validation run job is killed for uniqueId[" + id + "]";
//							status = "success";
//							LOG.info(message);
//						} else {
//							message = "Failed to kill the Validation run job";
//							LOG.error(message);
//						}
//							

						LOG.info("\n====> Stopping the running Job <====");
						LOG.debug("TaskId: " + taskId);
						LOG.debug("TaskType: " + taskType);
						LOG.debug("SparkAppId: " + sparkAppId);
						LOG.debug("UniqueId:" + uniqueId);
						LOG.debug("DeployMode:" + deployMode);
						LOG.debug("ProcessId:" + processId);

						if (uniqueId != null && !uniqueId.isEmpty()) {

							if (taskType.equalsIgnoreCase("connection"))
								isStatus = runningJobService.killSchemaJob(taskId, uniqueId, processId, sparkAppId,
										deployMode);

							else if (taskType.equalsIgnoreCase("appgroup"))
								isStatus = runningJobService.killAppGroupJob(taskId, uniqueId);

							else if (taskType.equalsIgnoreCase("project"))
								isStatus = runningJobService.killProjectJob(taskId, uniqueId, processId, sparkAppId,
										deployMode);

							else if (taskType.equalsIgnoreCase("domain"))
								isStatus = runningJobService.killDomainJob(taskId, uniqueId, processId, sparkAppId,
										deployMode);

							else if (taskType.equalsIgnoreCase("template") || taskType.equalsIgnoreCase("validation"))
								isStatus = runningJobService.killTemplateOrValidationJob(taskId, taskType, processId,
										deployMode, sparkAppId, uniqueId);

						} else {
							LOG.info("\n====>Unable to kill the job, UniqueId is missing!!");
						}
						if (isStatus) {
							message = "Validation run job is killed for uniqueId[" + id + "]";
							status = "success";
						} else {
							status = "failed";
							message = "Failed to kill the Validation run job";
						}

					} else {
						message = "Validation Job with uniqueId[" + id + "] is not running";
						LOG.error(message);
					}

				} else {
					message = "Invalid uniqueId";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
			e.printStackTrace();
			message = "Unexpected exception occurred";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/validation/killValidationRun - END");
		return json;
	}

	@ApiOperation(value = "Kill schema job by id", notes = "This API is used to kill the Schema job using the Unique Id", tags = "Schema")
	@ResponseBody
	@RequestMapping(value = "restapi/schema/killSchemaJob", method = RequestMethod.GET, produces = "application/json")
	public GenericResponse<String> killSchemaJob(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @ApiParam(name = "id", value = "Id", example = "1") String id) {

		LOG.info("restapi/schema/killSchemaJob - START");

		GenericResponse<String> json = new GenericResponse<String>();
		String status = "failed";
		String message = "";

		try {
			LOG.debug("token   " + request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {
				LOG.debug("id " + id);
				// get run details by unique Id
				RunningTaskDTO runningTaskDTO = iTaskDAO.getSchemaRunDetailsByUniqueId(id);

				if (runningTaskDTO != null) {
					boolean isSchemaJobRunning = true;
					String schemaRunStatus = runningTaskDTO.getStatus().trim();

					// check if schema job is running
					if (schemaRunStatus.equalsIgnoreCase("completed") || schemaRunStatus.equalsIgnoreCase("failed")
							|| schemaRunStatus.equalsIgnoreCase("killed"))
						isSchemaJobRunning = false;

					// kill the schema job if its running
					if (isSchemaJobRunning) {

						boolean killStatus = runningJobService.killSchemaJob(runningTaskDTO.getApplicationId(), id,
								runningTaskDTO.getProcessId(), runningTaskDTO.getSparkAppId(),
								runningTaskDTO.getDeployMode());

						if (killStatus) {
							message = "Schema run job is killed for uniqueId[" + id + "]";
							status = "success";
							LOG.info(message);
						} else {
							message = "Failed to kill Schema job";
							LOG.error(message);
						}

					} else {
						message = "Schema Job with uniqueId[" + id + "] is not running";
						LOG.error(message);
					}

				} else {
					message = "Invalid uniqueId";
					LOG.error(message);
				}

			} else {
				message = "Invalid Authorization";
				LOG.error(message);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "unexpected exception occurred";
			LOG.error("Exception  " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		json.setStatus(status);
		json.setMessage(message);
		LOG.info("restapi/schema/killSchemaJob - END");
		return json;
	}

	private JSONObject prepareDQDashBoardResultForUniqueId(String uniqueId, long idApp, String execDate, long run) {
		JSONObject obj = new JSONObject();
		LOG.info("prepareDQDashBoardResultForUniqueId - START");
		// Decimal values format
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		LOG.debug(" uniqueId " + uniqueId + " idApp " + idApp + " execDate " + execDate + " run " + run);

		// ReadTemplateInfo
		ListDataSource listDataSource = validationcheckdao.getTemplateDetailsForAppId(idApp);

		long idData = 0l;
		String templateName = "";

		if (listDataSource != null) {
			idData = listDataSource.getIdData();
			templateName = listDataSource.getName();
		}

		// Get runType of the run
		String testRun = validationcheckdao.getTestRunByUniqueId(uniqueId, idApp);

		// Read listApplications details
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		Map<String, String> tranRuleMap = iResultsDAO.getDataFromListDFTranRuleForMap(idApp);
		String flagGrca = listApplicationsData.getKeyGroupRecordCountAnomaly();

		obj.put("idApp", idApp);
		obj.put("uniqueId", uniqueId);
		obj.put("name", listApplicationsData.getName());
		obj.put("idData", idData);
		obj.put("templateName", templateName);
		obj.put("flagGrca", flagGrca);
		obj.put("execDate", execDate);
		obj.put("run", run);
		obj.put("testRun", testRun);
		obj.put("exceptionDataFile", "");

		JSONArray dqChecksMetrics = new JSONArray();

		// calculateScore
		int totalCount = 0;
		Double totalDQI = 0.0;

		DecimalFormat df = new DecimalFormat("#.00");

		// RCA
		try {

			JSONObject rca_dqMetric = new JSONObject();

			SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
					"DQ_Record Count Fingerprint", execDate, run);

			String DQI = "";
			String status = "";
			String Key_Matric_1 = "";
			String Key_Matric_2 = "";
			String Key_Matric_3 = "";
			while (dashboardDetails.next()) {
				DQI = dashboardDetails.getString(4);
				status = dashboardDetails.getString(5);
				Key_Matric_1 = dashboardDetails.getString(6);
				Key_Matric_2 = dashboardDetails.getString(7);
				Key_Matric_3 = dashboardDetails.getString(8);

			}
			String PercentageDF = "0";
			if (DQI == null) {
				rca_dqMetric.put("recordAnomalyScore", "0.0");
			} else {
				double Percentage = Double.parseDouble(DQI);
				Percentage = Math.floor(Percentage);
				PercentageDF = df.format(Percentage);
				rca_dqMetric.put("recordAnomalyScore", PercentageDF);

			}
			rca_dqMetric.put("RCAStatus", status);
			rca_dqMetric.put("RCAKey_Matric_1", Key_Matric_1);
			rca_dqMetric.put("RCAKey_Matric_2", Key_Matric_2);
			rca_dqMetric.put("RCAKey_Matric_3", Key_Matric_3);
			dqChecksMetrics.put(rca_dqMetric);

			totalDQI = totalDQI + Double.valueOf(PercentageDF);
			totalDQI = Math.floor(totalDQI);
			totalCount++;

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// BadData
		try {
			if (listApplicationsData.getBadData().equalsIgnoreCase("Y")) {

				JSONObject badData_metric = new JSONObject();

				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp, "DQ_Bad_Data",
						execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					badData_metric.put("badDataScore", 0.0);
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					badData_metric.put("badDataScore", PercentageDF);

				}
				badData_metric.put("badDataStatus", status);
				badData_metric.put("badDataKey_Matric_1", Key_Matric_1);
				badData_metric.put("badDataKey_Matric_2", Key_Matric_2);
				badData_metric.put("badDataKey_Matric_3", Key_Matric_3);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				dqChecksMetrics.put(badData_metric);
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// pattern unmatch
		try {
			if (listApplicationsData.getPatternCheck().equalsIgnoreCase("Y")) {

				JSONObject pattern_metric = new JSONObject();

				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Pattern_Data", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					pattern_metric.put("patternDataScore", 0.0);
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					pattern_metric.put("patternDataScore", PercentageDF);

				}
				pattern_metric.put("patternDataStatus", status);

				DecimalFormat df12 = new DecimalFormat(",###");
				String key_mat1 = df12.format(Double.parseDouble(Key_Matric_1));
				String key_mat2 = df12.format(Double.parseDouble(Key_Matric_2));
				pattern_metric.put("patternDataKey_Matric_1", key_mat1);
				pattern_metric.put("patternDataKey_Matric_2", key_mat2);
				pattern_metric.put("patternDataKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
				dqChecksMetrics.put(pattern_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

				LOG.debug("-------------patternData Total count====" + totalCount);
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Date Rule check
		try {
			if (listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {

				JSONObject daterule_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_DateRuleCheck", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					daterule_metric.put("dateRuleCheckScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					daterule_metric.put("dateRuleCheckScore", PercentageDF);
				}

				daterule_metric.put("dateRuleChkStatus", status);
				daterule_metric.put("dateRuleKey_Matric_1", Key_Matric_1);
				daterule_metric.put("dateRuleKey_Matric_2", Key_Matric_2);
				daterule_metric.put("dateRuleKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(daterule_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Numerical Field
		try {
			if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {

				JSONObject numstat_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Numerical Field Fingerprint", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					numstat_metric.put("numericalFieldScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					numstat_metric.put("numericalFieldScore", PercentageDF);

				}

				numstat_metric.put("numericalFieldStatsStatus", status);
				numstat_metric.put("numericalFieldKey_Matric_1", Key_Matric_1);
				numstat_metric.put("numericalFieldKey_Matric_2", Key_Matric_2);
				numstat_metric.put("numericalFieldKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(numstat_metric);

				totalDQI = totalDQI + Double.valueOf(PercentageDF);
				totalDQI = Math.floor(totalDQI);
				totalCount++;
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// string Field
		if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
			JSONObject stringstat_metric = new JSONObject();
			String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData, idApp, "", listApplicationsData);
			if (stringFieldScore.contains("∞")) {
				stringFieldScore = "0";
			}
			stringstat_metric.put("stringFieldScore", stringFieldScore);

			String stringFieldStatus = iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);
			stringstat_metric.put("stringFieldStatus", stringFieldStatus);
			dqChecksMetrics.put(stringstat_metric);

			if (stringFieldStatus != null) {
				totalDQI = totalDQI + Double.valueOf(stringFieldScore);
				totalDQI = Math.floor(totalDQI);
				totalCount++;
			}
		}
		// NullCountScore
		try {
			if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
				JSONObject nullCheck_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Completeness", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					nullCheck_metric.put("nullCountScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					nullCheck_metric.put("nullCountScore", PercentageDF);
				}
				nullCheck_metric.put("nullCountStatus", status);
				nullCheck_metric.put("nullCountKey_Matric_1", Key_Matric_1);
				nullCheck_metric.put("nullCountKey_Matric_2", Key_Matric_2);
				nullCheck_metric.put("nullCountKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(nullCheck_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// All FieldsScore
		try {
			if (tranRuleMap.get("all").equalsIgnoreCase("Y")) {

				JSONObject dupcheckAll_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Uniqueness -Seleted Fields", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					dupcheckAll_metric.put("allFieldsScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					dupcheckAll_metric.put("allFieldsScore", PercentageDF);

				}
				dupcheckAll_metric.put("allFieldsStatus", status);
				dupcheckAll_metric.put("allFieldsKey_Matric_1", Key_Matric_1);
				dupcheckAll_metric.put("allFieldsKey_Matric_2", Key_Matric_2);
				dupcheckAll_metric.put("allFieldsKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(dupcheckAll_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}
		// identityFieldsScore
		try {
			if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) {

				JSONObject dupcheckIdentity_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Uniqueness -Primary Keys", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					dupcheckIdentity_metric.put("identityFieldsScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					DecimalFormat df1 = new DecimalFormat("#0.0");
					PercentageDF = df1.format(Percentage);
					dupcheckIdentity_metric.put("identityFieldsScore", PercentageDF);
				}
				dupcheckIdentity_metric.put("identityfieldsStatus", status);
				dupcheckIdentity_metric.put("identityfieldsKey_Matric_1", Key_Matric_1);
				dupcheckIdentity_metric.put("identityfieldsKey_Matric_2", Key_Matric_2);
				dupcheckIdentity_metric.put("identityfieldsKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(dupcheckIdentity_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Record Fingerprint
		try {
			if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {

				JSONObject recordAnomaly_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_Record Anomaly", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					recordAnomaly_metric.put("recordFieldScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					DecimalFormat df1 = new DecimalFormat("#0.0");
					PercentageDF = df1.format(Percentage);
					recordAnomaly_metric.put("recordFieldScore", PercentageDF);

				}
				recordAnomaly_metric.put("recordAnomalyStatus", status);
				recordAnomaly_metric.put("recordAnomalyKey_Matric_1", Key_Matric_1);
				recordAnomaly_metric.put("recordAnomalyKey_Matric_2", Key_Matric_2);
				recordAnomaly_metric.put("recordAnomalyKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(recordAnomaly_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}
		// Length Check
		try {
			if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {

				JSONObject lengthChk_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_LengthCheck", execDate, run);
				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					lengthChk_metric.put("lengthCheckScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					lengthChk_metric.put("lengthCheckScore", PercentageDF);

				}

				lengthChk_metric.put("lengthStatus", status);
				lengthChk_metric.put("lengthKey_Matric_1", Key_Matric_1);
				lengthChk_metric.put("lengthKey_Matric_2", Key_Matric_2);
				lengthChk_metric.put("lengthKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(lengthChk_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				LOG.debug("-------------length Total count====" + totalCount);
			}

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}
		// Max Length Check
		try {
			if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {

				JSONObject lengthChk_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_MaxLengthCheck", execDate, run);
				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					lengthChk_metric.put("maxLengthCheckScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					lengthChk_metric.put("maxLengthCheckScore", PercentageDF);

				}

				lengthChk_metric.put("maxLengthStatus", status);
				lengthChk_metric.put("maxLengthKey_Matric_1", Key_Matric_1);
				lengthChk_metric.put("maxLengthKey_Matric_2", Key_Matric_2);
				lengthChk_metric.put("maxLengthKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(lengthChk_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				LOG.debug("-------------Max length Total count====" + totalCount);
			}

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		Integer custrule_count = 0;
		Integer globalrule_count = 0;

		// RegexCheck
		try {
			SqlRowSet rowSet = iResultsDAO.custom_rules_configured_count(idData);

			if (!rowSet.next()) {
				LOG.info("no results");
				custrule_count = -1;
			} else {
				custrule_count = rowSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}

		SqlRowSet rowSet1 = iResultsDAO.global_rules_configured_count(idData);
		try {
			if (!rowSet1.next()) {
				LOG.info("no results");
				globalrule_count = -1;
			} else {
				globalrule_count = rowSet1.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}

		try {
			if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && custrule_count > 0) {
				JSONObject rules_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp, "DQ_Rules",
						execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					rules_metric.put("ruleScoreDF", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					rules_metric.put("ruleScoreDF", PercentageDF);

				}
				rules_metric.put("ruleStatus", status);
				rules_metric.put("ruleKey_Matric_1", Key_Matric_1);
				rules_metric.put("ruleKey_Matric_2", Key_Matric_2);
				rules_metric.put("ruleKey_Matric_3", Key_Matric_3);
				rules_metric.put("custrule_count", custrule_count);
				dqChecksMetrics.put(rules_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		try {
			if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && globalrule_count > 0) {

				JSONObject globalrules_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_GlobalRules", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";

				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					globalrules_metric.put("GlobalruleScoreDF", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					globalrules_metric.put("GlobalruleScoreDF", PercentageDF);
				}
				globalrules_metric.put("GlobalruleStatus", status);
				globalrules_metric.put("ruleKey_Matric_3", Key_Matric_1);
				globalrules_metric.put("ruleKey_Matric_4", Key_Matric_2);
				globalrules_metric.put("globalrule_count", globalrule_count);
				dqChecksMetrics.put(globalrules_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// datadrift
		try {
			if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {

				JSONObject datadrift_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp, "DQ_Data Drift",
						execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					datadrift_metric.put("dataDriftScore", "0.0");
				} else {
					datadrift_metric.put("dataDriftScore", "100.0");
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					datadrift_metric.put("dataDriftScore", PercentageDF);
				}

				datadrift_metric.put("dataDriftStatus", status);
				datadrift_metric.put("dataDriftKey_Matric_1", Key_Matric_1);
				datadrift_metric.put("dataDriftKey_Matric_2", Key_Matric_2);
				datadrift_metric.put("dataDriftKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(datadrift_metric);

				if (status != null || status == null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Timeliness
		try {
			if (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {

				JSONObject timeliness_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp, "DQ_Timeliness",
						execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					timeliness_metric.put("TimelinessCheckScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					timeliness_metric.put("TimelinessCheckScore", PercentageDF);
				}
				timeliness_metric.put("TimelinessCheckStatus", status);
				timeliness_metric.put("TimelinessCheckKey_Matric_1", Key_Matric_1);
				timeliness_metric.put("TimelinessCheckKey_Matric_2", Key_Matric_2);
				timeliness_metric.put("TimelinessCheckKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(timeliness_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Default Check
		try {
			if (listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {

				JSONObject defaultChk_metric = new JSONObject();
				SqlRowSet dashboardDetails = iResultsDAO.calculateDashboardDetailsForExecDateRun(idApp,
						"DQ_DefaultCheck", execDate, run);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					defaultChk_metric.put("DefaultCheckScore", "0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					defaultChk_metric.put("DefaultCheckScore", PercentageDF);

				}
				defaultChk_metric.put("DefaultCheckStatus", status);
				defaultChk_metric.put("DefaultCheckKey_Matric_1", Key_Matric_1);
				defaultChk_metric.put("DefaultCheckKey_Matric_2", Key_Matric_2);
				defaultChk_metric.put("DefaultCheckKey_Matric_3", Key_Matric_3);
				dqChecksMetrics.put(defaultChk_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// get DQI from DB
		String avgDQIByQuery = iResultsDAO.getAggregateDQIForDataQualityDashboardForExecDateRun(idApp, execDate, run);
		obj.put("totalDQI", avgDQIByQuery);
		obj.put("DQMetrics", dqChecksMetrics);
		LOG.info("prepareDQDashBoardResultForUniqueId - END");
		return obj;
	}

	private JSONObject prepareDashboardSummaryForConnection(ListDataSchema listDataSchema, String valIdsStr) {

		LOG.info("prepareDashboardSummaryForConnection - START");

		LOG.debug("listDataSchema " + listDataSchema + " valIdsStr " + valIdsStr);
		JSONObject respJson = new JSONObject();

		respJson.put("connectionId", listDataSchema.getIdDataSchema());
		respJson.put("connectionName", listDataSchema.getSchemaName());
		respJson.put("totalValidationCount", valIdsStr.split(",").length);

		// Get the no of validations executed in the total list
		Integer executedValidationCount = iResultsDAO.getTotalExecutedValidationCountForSchema(valIdsStr);
		respJson.put("executedValidationCount", executedValidationCount);

		DecimalFormat df = new DecimalFormat("#.00");

		List<String> dqCheckNames = new ArrayList<String>();
		dqCheckNames.add("DQ_Record Count Fingerprint");
		dqCheckNames.add("DQ_Bad_Data");
		dqCheckNames.add("DQ_Pattern_Data");
		dqCheckNames.add("DQ_DateRuleCheck");
		dqCheckNames.add("DQ_Numerical Field Fingerprint");
		dqCheckNames.add("DQ_Completeness");
		dqCheckNames.add("DQ_Uniqueness -Seleted Fields");
		dqCheckNames.add("DQ_Uniqueness -Primary Keys");
		dqCheckNames.add("DQ_Record Anomaly");
		dqCheckNames.add("DQ_LengthCheck");
		dqCheckNames.add("DQ_MaxLengthCheck");
		dqCheckNames.add("DQ_Rules");
		dqCheckNames.add("DQ_GlobalRules");
		dqCheckNames.add("DQ_Data Drift");
		dqCheckNames.add("DQ_Timeliness");
		dqCheckNames.add("DQ_DefaultCheck");

		JSONArray dqChecksMetrics = new JSONArray();

		for (String dqCheck : dqCheckNames) {
			try {
				JSONObject dqMetric = new JSONObject();

				SqlRowSet dashboardDetails = iResultsDAO.calculateAvgMetricsForCheck(valIdsStr, dqCheck);

				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String avgDQI = "0.0";

				if (dashboardDetails != null) {
					while (dashboardDetails.next()) {
						String DQI = dashboardDetails.getString(1);
						if (DQI != null) {
							double Percentage = Double.parseDouble(DQI);
							avgDQI = df.format(Math.floor(Percentage));
						}

						Key_Matric_1 = dashboardDetails.getString(2);
						Key_Matric_2 = dashboardDetails.getString(3);
						status = dashboardDetails.getString(4);
						break;
					}
				}

				// RCA
				if (dqCheck.equalsIgnoreCase("DQ_Record Count Fingerprint")) {
					dqMetric.put("recordAnomalyScore", avgDQI);
					dqMetric.put("RCAStatus", status);
					dqMetric.put("RCAKey_Matric_1", Key_Matric_1);
					dqMetric.put("RCAKey_Matric_2", Key_Matric_2);
					dqMetric.put("RCAKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// BadData
				else if (dqCheck.equalsIgnoreCase("DQ_Bad_Data")) {
					dqMetric.put("badDataScore", avgDQI);
					dqMetric.put("badDataStatus", status);
					dqMetric.put("badDataKey_Matric_1", Key_Matric_1);
					dqMetric.put("badDataKey_Matric_2", Key_Matric_2);
					dqMetric.put("badDataKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// pattern unmatch
				else if (dqCheck.equalsIgnoreCase("DQ_Pattern_Data")) {
					dqMetric.put("patternDataScore", avgDQI);
					dqMetric.put("patternDataStatus", status);
					dqMetric.put("patternDataKey_Matric_1", Key_Matric_1);
					dqMetric.put("patternDataKey_Matric_2", Key_Matric_2);
					dqMetric.put("patternDataKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Date Rule check
				else if (dqCheck.equalsIgnoreCase("DQ_DateRuleCheck")) {
					dqMetric.put("dateRuleCheckScore", avgDQI);
					dqMetric.put("dateRuleChkStatus", status);
					dqMetric.put("dateRuleKey_Matric_1", Key_Matric_1);
					dqMetric.put("dateRuleKey_Matric_2", Key_Matric_2);
					dqMetric.put("dateRuleKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				else if (dqCheck.equalsIgnoreCase("DQ_Numerical Field Fingerprint")) {
					dqMetric.put("numericalFieldScore", avgDQI);
					dqMetric.put("numericalFieldStatsStatus", status);
					dqMetric.put("numericalFieldKey_Matric_1", Key_Matric_1);
					dqMetric.put("numericalFieldKey_Matric_2", Key_Matric_2);
					dqMetric.put("numericalFieldKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);

				}

				// NullCheck
				else if (dqCheck.equalsIgnoreCase("DQ_Completeness")) {
					dqMetric.put("nullCountScore", avgDQI);
					dqMetric.put("nullCountStatus", status);
					dqMetric.put("nullCountKey_Matric_1", Key_Matric_1);
					dqMetric.put("nullCountKey_Matric_2", Key_Matric_2);
					dqMetric.put("nullCountKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Duplicate check - All Selected Fields
				else if (dqCheck.equalsIgnoreCase("DQ_Uniqueness -Seleted Fields")) {
					dqMetric.put("allFieldsScore", avgDQI);
					dqMetric.put("allFieldsStatus", status);
					dqMetric.put("allFieldsKey_Matric_1", Key_Matric_1);
					dqMetric.put("allFieldsKey_Matric_2", Key_Matric_2);
					dqMetric.put("allFieldsKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Duplicate check - IdentityFields
				else if (dqCheck.equalsIgnoreCase("DQ_Uniqueness -Primary Keys")) {
					dqMetric.put("identityFieldsScore", avgDQI);
					dqMetric.put("identityfieldsStatus", status);
					dqMetric.put("identityfieldsKey_Matric_1", Key_Matric_1);
					dqMetric.put("identityfieldsKey_Matric_2", Key_Matric_2);
					dqMetric.put("identityfieldsKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Record Anomaly
				else if (dqCheck.equalsIgnoreCase("DQ_Record Anomaly")) {
					dqMetric.put("recordFieldScore", avgDQI);
					dqMetric.put("recordAnomalyStatus", status);
					dqMetric.put("recordAnomalyKey_Matric_1", Key_Matric_1);
					dqMetric.put("recordAnomalyKey_Matric_2", Key_Matric_2);
					dqMetric.put("recordAnomalyKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Length Check
				else if (dqCheck.equalsIgnoreCase("DQ_LengthCheck")) {
					dqMetric.put("lengthCheckScore", avgDQI);
					dqMetric.put("lengthStatus", status);
					dqMetric.put("lengthKey_Matric_1", Key_Matric_1);
					dqMetric.put("lengthKey_Matric_2", Key_Matric_2);
					dqMetric.put("lengthKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Max Length Check
				else if (dqCheck.equalsIgnoreCase("DQ_MaxLengthCheck")) {
					dqMetric.put("maxLengthCheckScore", avgDQI);
					dqMetric.put("maxLengthStatus", status);
					dqMetric.put("maxLengthKey_Matric_1", Key_Matric_1);
					dqMetric.put("maxLengthKey_Matric_2", Key_Matric_2);
					dqMetric.put("maxLengthKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Custom Rules
				else if (dqCheck.equalsIgnoreCase("DQ_Rules")) {
					dqMetric.put("ruleScoreDF", avgDQI);
					dqMetric.put("ruleStatus", status);
					dqMetric.put("ruleKey_Matric_1", Key_Matric_1);
					dqMetric.put("ruleKey_Matric_2", Key_Matric_2);
					dqMetric.put("ruleKey_Matric_3", "");

					// Get total Custom Rule count
					Integer custrule_count = iResultsDAO.getTotalCustomRulesExecutedForSchema(valIdsStr);

					dqMetric.put("custrule_count", custrule_count);
					dqChecksMetrics.put(dqMetric);
				}

				// Global Rules
				else if (dqCheck.equalsIgnoreCase("DQ_GlobalRules")) {
					dqMetric.put("GlobalruleScoreDF", avgDQI);
					dqMetric.put("GlobalruleStatus", status);
					dqMetric.put("ruleKey_Matric_3", Key_Matric_1);
					dqMetric.put("ruleKey_Matric_4", Key_Matric_2);

					// Get total Global Rule count
					Integer globalrule_count = iResultsDAO.getTotalGlobalRulesExecutedForSchema(valIdsStr);

					dqMetric.put("globalrule_count", globalrule_count);
					dqChecksMetrics.put(dqMetric);
				}

				// Data Drift
				else if (dqCheck.equalsIgnoreCase("DQ_Data Drift")) {
					dqMetric.put("dataDriftScore", avgDQI);
					dqMetric.put("dataDriftStatus", status);
					dqMetric.put("dataDriftKey_Matric_1", Key_Matric_1);
					dqMetric.put("dataDriftKey_Matric_2", Key_Matric_2);
					dqMetric.put("dataDriftKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Timeliness check
				else if (dqCheck.equalsIgnoreCase("DQ_Timeliness")) {
					dqMetric.put("TimelinessCheckScore", avgDQI);
					dqMetric.put("TimelinessCheckStatus", status);
					dqMetric.put("TimelinessCheckKey_Matric_1", Key_Matric_1);
					dqMetric.put("TimelinessCheckKey_Matric_2", Key_Matric_2);
					dqMetric.put("TimelinessCheckKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

				// Default check
				else if (dqCheck.equalsIgnoreCase("DQ_DefaultCheck")) {
					dqMetric.put("DefaultCheckScore", avgDQI);
					dqMetric.put("DefaultCheckStatus", status);
					dqMetric.put("DefaultCheckKey_Matric_1", Key_Matric_1);
					dqMetric.put("DefaultCheckKey_Matric_2", Key_Matric_2);
					dqMetric.put("DefaultCheckKey_Matric_3", "");
					dqChecksMetrics.put(dqMetric);
				}

			} catch (Exception E) {
				E.getMessage();
				LOG.error("Exception  " + E.getMessage());
			}
		}

		// get DQI from DB
		Double totalAvgDQI = iResultsDAO.getAggregateDQIForSchema(valIdsStr);
		respJson.put("totalDQI", totalAvgDQI);
		respJson.put("DQMetrics", dqChecksMetrics);
		LOG.info("prepareDashboardSummaryForConnection - END");
		return respJson;
	}

	private DashboardResultByIdDto prepareDQDashBoardResult(long idApp) {
		LOG.info("prepareDQDashBoardResult - START");
		DashboardResultByIdDto obj = new DashboardResultByIdDto();
		LOG.debug("idApp " + idApp);
		// Decimal values format
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);

		// ReadTemplateInfo
		ListDataSource listDataSource = validationcheckdao.getTemplateDetailsForAppId(idApp);

		long idData = 0l;
		String templateName = "";

		if (listDataSource != null) {
			idData = listDataSource.getIdData();
			templateName = listDataSource.getName();
		}

		// Read listApplications details
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		Map<String, String> tranRuleMap = iResultsDAO.getDataFromListDFTranRuleForMap(idApp);
		String flagGrca = listApplicationsData.getKeyGroupRecordCountAnomaly();

		// Get MaxDate and MaxRun
		String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

		if (flagGrca != null && flagGrca.equalsIgnoreCase("Y")) {
			rca_tran_table = "DATA_QUALITY_Transactionset_sum_dgroup";
		}

		Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);
		String strMaxDate = "";
		if (maxDate != null) {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			strMaxDate = dt.format(maxDate);
		}
		int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);
		LOG.debug("maxRun:" + maxRun);

		obj.setIdApp(idApp);
		obj.setName(listApplicationsData.getName());
		obj.setIdData(idData);
		obj.setTemplateName(templateName);
		obj.setFlagGrca(flagGrca);
		obj.setMaxDate(strMaxDate);
		obj.setMaxRun(maxRun);

		ArrayList<DQmetricdto> dqChecksMetrics = new ArrayList<DQmetricdto>();

		// calculateScore
		int totalCount = 0;
		Double totalDQI = 0.0;

		DecimalFormat df = new DecimalFormat("#.00");

		// RCA
		try {

			DQmetricdto rca_dqMetric = new DQmetricdto();

			SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Record Count Fingerprint",
					listApplicationsData);

			String DQI = "";
			String status = "";
			String Key_Matric_1 = "";
			String Key_Matric_2 = "";
			String Key_Matric_3 = "";
			while (dashboardDetails.next()) {
				DQI = dashboardDetails.getString(4);
				status = dashboardDetails.getString(5);
				Key_Matric_1 = dashboardDetails.getString(6);
				Key_Matric_2 = dashboardDetails.getString(7);
				Key_Matric_3 = dashboardDetails.getString(8);

			}
			String PercentageDF = "0";
			if (DQI == null) {
				rca_dqMetric.setRecordAnomalyScore("0.0");
			} else {
				double Percentage = Double.parseDouble(DQI);
				Percentage = Math.floor(Percentage);
				PercentageDF = df.format(Percentage);
				rca_dqMetric.setRecordAnomalyScore(PercentageDF);

			}
			rca_dqMetric.setRCAStatus(status);
			rca_dqMetric.setRCAKey_Matric_1(Key_Matric_1);
			rca_dqMetric.setRCAKey_Matric_2(Key_Matric_2);
			rca_dqMetric.setRCAKey_Matric_3(Key_Matric_3);
			dqChecksMetrics.add(rca_dqMetric);

			if (maxRun > 2) {
				totalDQI = totalDQI + Double.valueOf(PercentageDF);
				totalDQI = Math.floor(totalDQI);
				totalCount++;
			}

		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// BadData
		try {
			if (listApplicationsData.getBadData().equalsIgnoreCase("Y")) {

				DQmetricdto badData_metric = new DQmetricdto();

				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Bad_Data",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					badData_metric.setBadDataScore(0.0);
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					badData_metric.setBadDataScore(Percentage);

				}
				badData_metric.setBadDataStatus(status);
				badData_metric.setBadDataKey_Matric_1(Key_Matric_1);
				badData_metric.setBadDataKey_Matric_2(Key_Matric_2);
				badData_metric.setBadDataKey_Matric_3(Key_Matric_3);

				if (status != null) {
					totalDQI = totalDQI + Double.parseDouble(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				dqChecksMetrics.add(badData_metric);
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// pattern unmatch
		try {
			if (listApplicationsData.getPatternCheck().equalsIgnoreCase("Y")) {

				DQmetricdto pattern_metric = new DQmetricdto();

				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Pattern_Data",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					pattern_metric.setPatternDataScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					pattern_metric.setPatternDataScore(PercentageDF);

				}
				pattern_metric.setPatternDataStatus(status);

				DecimalFormat df12 = new DecimalFormat(",###");
				String key_mat1 = df12.format(Double.parseDouble(Key_Matric_1));
				String key_mat2 = df12.format(Double.parseDouble(Key_Matric_2));
				pattern_metric.setPatternDataKey_Matric_1(key_mat1);
				pattern_metric.setPatternDataKey_Matric_2(key_mat2);
				pattern_metric.setPatternDataKey_Matric_3(Key_Matric_3.replace(",", "<br>"));
				dqChecksMetrics.add(pattern_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

				LOG.debug("-------------patternData Total count====" + totalCount);
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Date Rule check
		try {
			if (listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {

				DQmetricdto daterule_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_DateRuleCheck",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					daterule_metric.setDateRuleCheckScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					daterule_metric.setDateRuleCheckScore(PercentageDF);
				}

				daterule_metric.setDateRuleChkStatus(status);
				daterule_metric.setDateRuleKey_Matric_1(Key_Matric_1);
				daterule_metric.setDateRuleKey_Matric_2(Key_Matric_2);
				daterule_metric.setDateRuleKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(daterule_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			E.getMessage();
			LOG.error("Exception  " + E.getMessage());
		}

		// Numerical Field
		try {
			if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {

				DQmetricdto numstat_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
						"DQ_Numerical Field Fingerprint", listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					numstat_metric.setNumericalFieldScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					numstat_metric.setNumericalFieldScore(PercentageDF);

				}

				numstat_metric.setNumericalFieldStatsStatus(status);
				numstat_metric.setNumericalFieldKey_Matric_1(Key_Matric_1);
				numstat_metric.setNumericalFieldKey_Matric_2(Key_Matric_2);
				numstat_metric.setNumericalFieldKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(numstat_metric);
				if (maxRun > 2) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {

			LOG.error("Exception  " + E.getMessage());
		}

		// string Field
		if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
			DQmetricdto stringstat_metric = new DQmetricdto();
			String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData, idApp, "", listApplicationsData);
			if (stringFieldScore.contains("∞")) {
				stringFieldScore = "0";
			}
			stringstat_metric.setStringFieldScore(stringFieldScore);

			String stringFieldStatus = iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);
			stringstat_metric.setStringFieldStatus(stringFieldStatus);
			dqChecksMetrics.add(stringstat_metric);

			if (stringFieldStatus != null) {
				totalDQI = totalDQI + Double.valueOf(stringFieldScore);
				totalDQI = Math.floor(totalDQI);
				totalCount++;
			}
		}
		// NullCountScore
		try {
			if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
				DQmetricdto nullCheck_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Completeness",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					nullCheck_metric.setNullCountScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					nullCheck_metric.setNullCountScore(PercentageDF);
				}
				nullCheck_metric.setNullCountStatus(status);
				nullCheck_metric.setNullCountKey_Matric_1(Key_Matric_1);
				nullCheck_metric.setNullCountKey_Matric_2(Key_Matric_2);
				nullCheck_metric.setNullCountKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(nullCheck_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// All FieldsScore
		try {
			if (tranRuleMap.get("all").equalsIgnoreCase("Y")) {

				DQmetricdto dupcheckAll_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
						"DQ_Uniqueness -Seleted Fields", listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					dupcheckAll_metric.setAllFieldsScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					dupcheckAll_metric.setAllFieldsScore(PercentageDF);

				}
				dupcheckAll_metric.setAllFieldsStatus(status);
				dupcheckAll_metric.setAllFieldsKey_Matric_1(Key_Matric_1);
				dupcheckAll_metric.setAllFieldsKey_Matric_2(Key_Matric_2);
				dupcheckAll_metric.setAllFieldsKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(dupcheckAll_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}
		// identityFieldsScore
		try {
			if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) {

				DQmetricdto dupcheckIdentity_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
						"DQ_Uniqueness -Primary Keys", listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					dupcheckIdentity_metric.setIdentityFieldsScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					DecimalFormat df1 = new DecimalFormat("#0.0");
					PercentageDF = df1.format(Percentage);
					dupcheckIdentity_metric.setIdentityFieldsScore(PercentageDF);
				}
				dupcheckIdentity_metric.setIdentityfieldsStatus(status);
				dupcheckIdentity_metric.setIdentityfieldsKey_Matric_1(Key_Matric_1);
				dupcheckIdentity_metric.setIdentityfieldsKey_Matric_2(Key_Matric_2);
				dupcheckIdentity_metric.setIdentityfieldsKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(dupcheckIdentity_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// Record Fingerprint
		try {
			if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {

				DQmetricdto recordAnomaly_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Record Anomaly",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					recordAnomaly_metric.setRecordFieldScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					DecimalFormat df1 = new DecimalFormat("#0.0");
					PercentageDF = df1.format(Percentage);
					recordAnomaly_metric.setRecordFieldScore(PercentageDF);

				}
				recordAnomaly_metric.setRecordAnomalyStatus(status);
				recordAnomaly_metric.setRecordAnomalyKey_Matric_1(Key_Matric_1);
				recordAnomaly_metric.setRecordAnomalyKey_Matric_2(Key_Matric_2);
				recordAnomaly_metric.setRecordAnomalyKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(recordAnomaly_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}
		// Length Check
		try {
			if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {

				DQmetricdto lengthChk_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_LengthCheck",
						listApplicationsData);
				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					lengthChk_metric.setLengthCheckScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					lengthChk_metric.setLengthCheckScore(PercentageDF);

				}

				lengthChk_metric.setLengthStatus(status);
				lengthChk_metric.setLengthKey_Matric_1(Key_Matric_1);
				lengthChk_metric.setLengthKey_Matric_2(Key_Matric_2);
				lengthChk_metric.setLengthKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(lengthChk_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				LOG.debug("-------------length Total count====" + totalCount);
			}

		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// Max Length Check
		try {
			if (listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {

				DQmetricdto lengthChk_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_MaxLengthCheck",
						listApplicationsData);
				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);

				}
				String PercentageDF = "0";
				if (DQI == null) {
					lengthChk_metric.setMaxLengthCheckScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					lengthChk_metric.setMaxLengthCheckScore(PercentageDF);

				}

				lengthChk_metric.setMaxLengthStatus(status);
				lengthChk_metric.setMaxLengthKey_Matric_1(Key_Matric_1);
				lengthChk_metric.setMaxLengthKey_Matric_2(Key_Matric_2);
				lengthChk_metric.setMaxLengthKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(lengthChk_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
				LOG.debug("------------ Max length Total count====" + totalCount);
			}

		} catch (Exception E) {

			LOG.error("Exception  " + E.getMessage());
		}

		Integer custrule_count = 0;
		Integer globalrule_count = 0;

		// RegexCheck
		try {
			SqlRowSet rowSet = iResultsDAO.custom_rules_configured_count(idData);

			if (!rowSet.next()) {
				LOG.info("no results");
				custrule_count = -1;
			} else {
				custrule_count = rowSet.getInt(1);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
		}

		SqlRowSet rowSet1 = iResultsDAO.global_rules_configured_count(idData);
		try {
			if (!rowSet1.next()) {
				LOG.info("no results");
				globalrule_count = -1;
			} else {
				globalrule_count = rowSet1.getInt(1);
			}
		} catch (Exception e) {
			LOG.error("Exception  " + e.getMessage());
		}

		try {
			if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && custrule_count > 0) {
				DQmetricdto rules_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Rules",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					rules_metric.setRuleScoreDF("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					rules_metric.setRuleScoreDF(PercentageDF);

				}
				rules_metric.setRuleStatus(status);
				rules_metric.setRuleKey_Matric_1(Key_Matric_1);
				rules_metric.setRuleKey_Matric_2(Key_Matric_2);
				rules_metric.setRuleKey_Matric_3(Key_Matric_3);
				rules_metric.setCustrule_count(custrule_count);
				dqChecksMetrics.add(rules_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {

			LOG.error("Exception  " + E.getMessage());
		}

		try {
			if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && globalrule_count > 0) {

				DQmetricdto globalrules_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_GlobalRules",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";

				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					globalrules_metric.setGlobalruleScoreDF("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					globalrules_metric.setGlobalruleScoreDF(PercentageDF);
				}
				globalrules_metric.setGlobalruleStatus(status);
				globalrules_metric.setRuleKey_Matric_3(Key_Matric_1);
				globalrules_metric.setRuleKey_Matric_4(Key_Matric_2);
				globalrules_metric.setGlobalrule_count(globalrule_count);
				dqChecksMetrics.add(globalrules_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}

			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// datadrift
		try {
			if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {

				DQmetricdto datadrift_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Data Drift",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					datadrift_metric.setDataDriftScore("0.0");
				} else {
					datadrift_metric.setDataDriftScore("100.0");
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					if (maxRun == 1)
						datadrift_metric.setDataDriftScore(null);
					else
						datadrift_metric.setDataDriftScore(PercentageDF);
				}

				datadrift_metric.setDataDriftStatus(status);
				datadrift_metric.setDataDriftKey_Matric_1(Key_Matric_1);
				datadrift_metric.setDataDriftKey_Matric_2(Key_Matric_2);
				datadrift_metric.setDataDriftKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(datadrift_metric);

				if (status != null || status == null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}
		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// Timeliness
		try {
			if (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {

				DQmetricdto timeliness_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Timeliness",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					timeliness_metric.setTimelinessCheckScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					timeliness_metric.setTimelinessCheckScore(PercentageDF);
				}
				timeliness_metric.setTimelinessCheckStatus(status);
				timeliness_metric.setTimelinessCheckKey_Matric_1(Key_Matric_1);
				timeliness_metric.setTimelinessCheckKey_Matric_2(Key_Matric_2);
				timeliness_metric.setTimelinessCheckKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(timeliness_metric);
				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}

		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// Default Check
		try {
			if (listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {

				DQmetricdto defaultChk_metric = new DQmetricdto();
				SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_DefaultCheck",
						listApplicationsData);

				String DQI = "";
				String status = "";
				String Key_Matric_1 = "";
				String Key_Matric_2 = "";
				String Key_Matric_3 = "";
				while (dashboardDetails.next()) {
					DQI = dashboardDetails.getString(4);
					status = dashboardDetails.getString(5);
					Key_Matric_1 = dashboardDetails.getString(6);
					Key_Matric_2 = dashboardDetails.getString(7);
					Key_Matric_3 = dashboardDetails.getString(8);
				}
				String PercentageDF = "0";
				if (DQI == null) {
					defaultChk_metric.setDefaultCheckScore("0.0");
				} else {
					double Percentage = Double.parseDouble(DQI);
					Percentage = Math.floor(Percentage);
					PercentageDF = df.format(Percentage);
					defaultChk_metric.setDefaultCheckScore(PercentageDF);

				}
				defaultChk_metric.setDefaultCheckStatus(status);
				defaultChk_metric.setDefaultCheckKey_Matric_1(Key_Matric_1);
				defaultChk_metric.setDefaultCheckKey_Matric_2(Key_Matric_2);
				defaultChk_metric.setDefaultCheckKey_Matric_3(Key_Matric_3);
				dqChecksMetrics.add(defaultChk_metric);

				if (status != null) {
					totalDQI = totalDQI + Double.valueOf(PercentageDF);
					totalDQI = Math.floor(totalDQI);
					totalCount++;
				}
			}

		} catch (Exception E) {
			LOG.error("Exception  " + E.getMessage());
		}

		// get DQI from DB
		String avgDQIByQuery = iResultsDAO.getAggregateDQIForDataQualityDashboard(idApp);
		obj.setTotalDQI(avgDQIByQuery);
		obj.setDQMetrics(dqChecksMetrics);
		LOG.info("prepareDQDashBoardResult - END");
		return obj;
	}

	private JSONObject prepareDQDashBoardDetails(long idApp) {
		LOG.info("prepareDQDashBoardDetails - START");
		JSONObject obj = new JSONObject();
		LOG.debug("idApp " + idApp);

		// Read listApplications details
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);

		if (listApplicationsData != null) {
			// ReadTemplateInfo
			ListDataSource listDataSource = validationcheckdao.getTemplateDetailsForAppId(idApp);

			long idData = 0l;
			String templateName = "";

			if (listDataSource != null) {
				idData = listDataSource.getIdData();
				templateName = listDataSource.getName();
			}

			// Get MaxDate and MaxRun
			String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

			Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);

			String strMaxDate = "";
			if (maxDate != null) {
				SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
				strMaxDate = dt.format(maxDate);
			}

			int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);
			obj.put("idApp", idApp);
			obj.put("name", listApplicationsData.getName());
			obj.put("templateId", idData);
			obj.put("templateName", templateName);
			obj.put("maxDate", strMaxDate);
			obj.put("maxRun", maxRun);

			LOG.debug("maxDate:" + strMaxDate);
			LOG.debug("maxrun:" + maxRun);
			JSONArray dqChecks = new JSONArray();

			SqlRowSet tableNamesFromResultMasterTable = iResultsDAO.getTableNamesFromResultMasterTable(idApp);

			while (tableNamesFromResultMasterTable.next()) {

				String tableName = tableNamesFromResultMasterTable.getString("Table_Name");

				// Length check
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Length_Check")) {

					SqlRowSet queryForRowSet = iResultsDAO.getValidationCheckDetails(idApp, tableName, strMaxDate,
							maxRun);

					while (queryForRowSet.next()) {
						JSONObject lengthCheckRowObj = new JSONObject();
						lengthCheckRowObj.put("columnCheck", "Length check");
						lengthCheckRowObj.put("columnName", queryForRowSet.getString("ColName"));
						lengthCheckRowObj.put("totalRecordCount", queryForRowSet.getLong("RecordCount"));
						lengthCheckRowObj.put("totalFailedRecords", queryForRowSet.getDouble("TotalFailedRecords"));
						lengthCheckRowObj.put("failedRecordsPercentage",
								queryForRowSet.getDouble("FailedRecords_Percentage"));
						lengthCheckRowObj.put("threshold", queryForRowSet.getDouble("Length_Threshold"));
						lengthCheckRowObj.put("status", queryForRowSet.getString("Status"));

						dqChecks.put(lengthCheckRowObj);
					}
				}

				// Max Length check
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Max_Length_Check")) {

					SqlRowSet queryForRowSet = iResultsDAO.getValidationCheckDetails(idApp, tableName, strMaxDate,
							maxRun);

					while (queryForRowSet.next()) {
						JSONObject maxLengthCheckRowObj = new JSONObject();
						maxLengthCheckRowObj.put("columnCheck", "Max Length check");
						maxLengthCheckRowObj.put("columnName", queryForRowSet.getString("ColName"));
						maxLengthCheckRowObj.put("totalRecordCount", queryForRowSet.getLong("RecordCount"));
						maxLengthCheckRowObj.put("totalFailedRecords", queryForRowSet.getDouble("TotalFailedRecords"));
						maxLengthCheckRowObj.put("failedRecordsPercentage",
								queryForRowSet.getDouble("FailedRecords_Percentage"));
						maxLengthCheckRowObj.put("threshold", queryForRowSet.getDouble("Length_Threshold"));
						maxLengthCheckRowObj.put("status", queryForRowSet.getString("Status"));

						dqChecks.put(maxLengthCheckRowObj);
					}
				}

				// NullCheck
				if (tableName.equalsIgnoreCase("DATA_QUALITY_NullCheck_Summary")) {

					SqlRowSet queryForRowSet = iResultsDAO.getValidationCheckDetails(idApp, tableName, strMaxDate,
							maxRun);

					while (queryForRowSet.next()) {
						JSONObject nullCheckRowObj = new JSONObject();
						long recordCount = queryForRowSet.getLong("Record_Count");
						double nullPercentage = queryForRowSet.getDouble("Null_Percentage");
						long totalFailedRecords = (long) (recordCount * nullPercentage);

						nullCheckRowObj.put("columnCheck", "Null Check");
						nullCheckRowObj.put("columnName", queryForRowSet.getString("ColName"));
						nullCheckRowObj.put("totalRecordCount", recordCount);
						nullCheckRowObj.put("totalFailedRecords", totalFailedRecords);
						nullCheckRowObj.put("failedRecordsPercentage", nullPercentage);
						nullCheckRowObj.put("threshold", queryForRowSet.getDouble("Null_Threshold"));
						nullCheckRowObj.put("status", queryForRowSet.getString("Status"));

						dqChecks.put(nullCheckRowObj);
					}
				}

				// DefaultCheck
				if (tableName.equalsIgnoreCase("DATA_QUALITY_default_value")) {

					SqlRowSet queryForRowSet = iResultsDAO.getDefaultValueValidationCheckDetails(idApp, strMaxDate,
							maxRun);

					while (queryForRowSet.next()) {
						JSONObject defaultCheckRowObj = new JSONObject();
						defaultCheckRowObj.put("columnCheck", "Default Check");
						defaultCheckRowObj.put("columnName", queryForRowSet.getString("ColName"));
						defaultCheckRowObj.put("totalPassedRecords", queryForRowSet.getString("Default_Count"));
						defaultCheckRowObj.put("passedRecordsPercentage",
								queryForRowSet.getString("Default_Percentage"));

						dqChecks.put(defaultCheckRowObj);
					}
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_badData")) {
					SqlRowSet queryForRowSet = iResultsDAO.getValidationCheckDetails(idApp, tableName, strMaxDate,
							maxRun);

					while (queryForRowSet.next()) {
						JSONObject badDataCheckRowObj = new JSONObject();
						badDataCheckRowObj.put("columnCheck", "BadData Check");
						badDataCheckRowObj.put("columnName", queryForRowSet.getString("ColName"));
						badDataCheckRowObj.put("totalRecordCount", queryForRowSet.getLong("TotalRecord"));
						badDataCheckRowObj.put("totalFailedRecords", queryForRowSet.getLong("TotalBadRecord"));
						badDataCheckRowObj.put("failedRecordsPercentage",
								queryForRowSet.getDouble("badDataPercentage"));
						badDataCheckRowObj.put("threshold", queryForRowSet.getDouble("badDataThreshold"));
						badDataCheckRowObj.put("status", queryForRowSet.getString("status"));

						dqChecks.put(badDataCheckRowObj);
					}
				}
			}
			obj.put("dqCheckDetails", dqChecks);
		}
		LOG.info("prepareDQDashBoardDetails - END");
		return obj;
	}

	private JSONArray prepareColumnAggregateDataForChecks(ListApplications listApplications,
			ColumnAggregateRequest columnAggregateRequest) {
		LOG.info("prepareColumnAggregateDataForChecks - START");
		LOG.debug("listApplications " + listApplications + " columnAggregateRequest " + columnAggregateRequest);
		long idApp = Long.parseLong(columnAggregateRequest.getIdApp());
		String type = columnAggregateRequest.getType().trim();
		String selected_microseg_cols = columnAggregateRequest.getMicrosegmentCols().trim();
		List<String> all_microseg_cols = new ArrayList<>();
		List<String> groupby_microseg_cols = new ArrayList<>();

		// Get Template Details
		String templateDescription = "";
		ListDataSource listDataSource = listdatasourcedao
				.getDataFromListDataSourcesOfIdData(listApplications.getIdData());
		if (listDataSource != null && listDataSource.getDataLocation().trim().equalsIgnoreCase("Derived")) {
			ListDerivedDataSource listDerivedDataSource = listdatasourcedao
					.getDataFromListDerivedDataSourcesOfIdData(listApplications.getIdData());
			// Get Dataset name
			templateDescription = listDerivedDataSource.getDescription();
		} else {
			// Get Dataset name
			templateDescription = listDataSource.getDescription();
		}
		// Get MaxDate and MaxRun
		String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

		Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);

		String strMaxDate = "";
		if (maxDate != null) {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			strMaxDate = dt.format(maxDate);
		}

		int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);
		LOG.debug("maxDate:" + strMaxDate);
		LOG.debug("maxrun:" + maxRun);

		// Check it is test run by execution date and run
		boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);

		// Get RuleCatalog details
		List<RuleCatalog> ruleCatalogList = null;
		if (isTestRun)
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
		else
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

		JSONArray dqChecks = new JSONArray();

		SqlRowSet tableNamesFromResultMasterTable = iResultsDAO.getTableNamesFromResultMasterTable(idApp);

		DecimalFormat numberFormat = new DecimalFormat("#0.00");

		while (tableNamesFromResultMasterTable.next()) {

			try {

				String tableName = tableNamesFromResultMasterTable.getString("Table_Name");

				// NullCheck
				if (tableName.equalsIgnoreCase("DATA_QUALITY_NullCheck_Summary")) {

					// Identify microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForNullCheck(idApp, strMaxDate, maxRun);

					if (type.equalsIgnoreCase("individual")) {

						if (selected_microseg_cols != null && !selected_microseg_cols.isEmpty())
							groupby_microseg_cols = Arrays.asList(selected_microseg_cols.split(","));
						else
							groupby_microseg_cols = all_microseg_cols;
					}
					SqlRowSet queryForRowSet = null;
					// Default Dimension
					String default_null_check_dmn = "Completeness";
					String check_name = "Null Check";

					// Get the NullCheck column dimensions from RuleCatalog
					Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name, idApp);
					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {
						queryForRowSet = iResultsDAO.getColumnAggregateDetailsForMicrosegmentNullCheck(idApp, tableName,
								strMaxDate, maxRun, columnAggregateRequest, all_microseg_cols, groupby_microseg_cols);
					} else {
						queryForRowSet = iResultsDAO.getColumnAggregateDetailsForNullCheck(idApp, tableName, strMaxDate,
								maxRun, columnAggregateRequest);
					}

					JSONArray colAggArray = new JSONArray();
					while (queryForRowSet.next()) {
						JSONObject aggObj = new JSONObject();
						String columnName = queryForRowSet.getString("ColName");
						String datasetName = "";
						String systemName = "";
						String row_identifer_type = "";
						String batch_id = "";
						if (templateDescription != null && !templateDescription.trim().isEmpty()) {
							String[] dataset_fields = templateDescription.split(",");
							datasetName = dataset_fields[0];
							if (dataset_fields.length > 1) {
								systemName = dataset_fields[1];
							}
							if (dataset_fields.length > 2) {
								row_identifer_type = dataset_fields[2];
							}
							String temp = strMaxDate.replace("-", "");
							batch_id = datasetName + "_" + temp;
						}

						aggObj.put("executionDate", strMaxDate);
						aggObj.put("run", maxRun);
						aggObj.put("dataset", datasetName);
						aggObj.put("systemName", systemName);
						aggObj.put("row_identifer_type", row_identifer_type);
						aggObj.put("batch_id", batch_id);
						aggObj.put("columnName", columnName);
						aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
						aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
						Double failPercentage = queryForRowSet.getDouble("failPercentage");

						String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
						aggObj.put("failPercentage", fail_perc_str);

						Double passPercentage = queryForRowSet.getDouble("passPercentage");

						String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
						aggObj.put("passPercentage", pass_perc_str);

						if (type.equalsIgnoreCase("individual")) {
							for (String gb_col : groupby_microseg_cols) {
								aggObj.put(gb_col, queryForRowSet.getString(gb_col));
							}
						}

						// Set Dimension
						String col_dmn = default_null_check_dmn;

						if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
							col_dmn = col_dimension_map.get(columnName);
						}
						aggObj.put("dimensionName", col_dmn);

						// Get BusinessAttribute and DQID
						String dqId = "";
						String businessAttributes = "";
						RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
								columnName);
						if (ruleCatalog != null) {
							// Get DQID
							dqId = ruleCatalog.getReviewComments();
							dqId = (dqId != null) ? dqId.trim() : "";

							// GetBusinessAttribute
							businessAttributes = ruleCatalog.getBusinessAttributes();
							businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";
						}
						aggObj.put("dqId", dqId);
						String businessAttributesArray[] = businessAttributes.split(",");
						if (businessAttributesArray.length > 0) {
							for (String businessAttribute : businessAttributesArray) {
								aggObj.put("businessAttribute", businessAttribute);
								colAggArray.put(new JSONObject(aggObj.toString()));
							}
						} else {
							aggObj.put("businessAttribute", "");
							colAggArray.put(aggObj);
						}

					}

					JSONObject nullCheckRowObj = new JSONObject();
					nullCheckRowObj.put("checkName", "Null Check");
					nullCheckRowObj.put("resultType", type);
					nullCheckRowObj.put("dqAggregate", colAggArray);
					dqChecks.put(nullCheckRowObj);
				}

				// GlobalRules
				if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules")) {

					// Identify microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForCheck(idApp, tableName, strMaxDate, maxRun);

					if (type.equalsIgnoreCase("individual")) {

						if (selected_microseg_cols != null && !selected_microseg_cols.isEmpty())
							groupby_microseg_cols = Arrays.asList(selected_microseg_cols.split(","));
						else
							groupby_microseg_cols = all_microseg_cols;
					}
					SqlRowSet queryForRowSet = null;
					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {
						queryForRowSet = iResultsDAO.getColumnAggregateDetailsForMicrosegmentGlobalRules(idApp,
								tableName, strMaxDate, maxRun, columnAggregateRequest, all_microseg_cols,
								groupby_microseg_cols);
					} else {
						queryForRowSet = iResultsDAO.getColumnAggregateDetailsForGlobalRule(idApp, tableName,
								strMaxDate, maxRun, columnAggregateRequest);
					}

					JSONArray colAggArray = new JSONArray();
					while (queryForRowSet.next()) {
						String ruleName = queryForRowSet.getString("ruleName");
						String datasetName = "";
						String systemName = "";
						String row_identifer_type = "";
						String batch_id = "";
						if (templateDescription != null && !templateDescription.trim().isEmpty()) {
							String[] dataset_fields = templateDescription.split(",");
							datasetName = dataset_fields[0];
							if (dataset_fields.length > 1) {
								systemName = dataset_fields[1];
							}
							if (dataset_fields.length > 2) {
								row_identifer_type = dataset_fields[2];
							}
							String temp = strMaxDate.replace("-", "");
							batch_id = datasetName + "_" + temp;
						}
						JSONObject aggObj = new JSONObject();
						aggObj.put("executionDate", strMaxDate);
						aggObj.put("run", maxRun);
						aggObj.put("dataset", datasetName);
						aggObj.put("systemName", systemName);
						aggObj.put("row_identifer_type", row_identifer_type);
						aggObj.put("batch_id", batch_id);
						aggObj.put("ruleName", ruleName);
						aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
						aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
						Double failPercentage = queryForRowSet.getDouble("failPercentage");

						String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
						aggObj.put("failPercentage", fail_perc_str);

						Double passPercentage = queryForRowSet.getDouble("passPercentage");

						String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
						aggObj.put("passPercentage", pass_perc_str);

						if (type.equalsIgnoreCase("individual")) {
							for (String gb_col : groupby_microseg_cols) {
								aggObj.put(gb_col, queryForRowSet.getString(gb_col));
							}
						}

						aggObj.put("dimensionName", queryForRowSet.getString("dimension_name"));

						// Get BusinessAttribute and DQID
						String dqId = "";
						String businessAttributes = "";
						RuleCatalog ruleCatalog = getRuleCatalogDetailsForGlobalRule(ruleCatalogList,
								DatabuckConstants.RULE_TYPE_GLOBAL_RULE, ruleName);
						if (ruleCatalog != null) {
							// Get DQID
							dqId = ruleCatalog.getReviewComments();
							dqId = (dqId != null) ? dqId.trim() : "";

							// GetBusinessAttribute
							businessAttributes = ruleCatalog.getBusinessAttributes();
							businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";
						}
						aggObj.put("dqId", dqId);
						String businessAttributesArray[] = businessAttributes.split(",");
						if (businessAttributesArray.length > 0) {
							for (String businessAttribute : businessAttributesArray) {
								aggObj.put("businessAttribute", businessAttribute);
								colAggArray.put(new JSONObject(aggObj.toString()));
							}
						} else {
							aggObj.put("businessAttribute", "");
							colAggArray.put(aggObj);
						}
					}

					JSONObject nullCheckRowObj = new JSONObject();
					nullCheckRowObj.put("checkName", "Global Rules");
					nullCheckRowObj.put("resultType", type);
					nullCheckRowObj.put("dqAggregate", colAggArray);
					dqChecks.put(nullCheckRowObj);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  " + e.getMessage());
			}
		}
		LOG.info("prepareColumnAggregateDataForChecks - END");
		return dqChecks;
	}

	private JSONArray prepareColumnNonAggregateDataForChecks(ListApplications listApplications) {
		LOG.info("prepareColumnNonAggregateDataForChecks - START");
		LOG.debug("listApplications " + listApplications);
		long idApp = listApplications.getIdApp();
		String templateDescription = "";

		// Get Template Details
		ListDataSource listDataSource = listdatasourcedao
				.getDataFromListDataSourcesOfIdData(listApplications.getIdData());
		if (listDataSource != null && listDataSource.getDataLocation().trim().equalsIgnoreCase("Derived")) {
			ListDerivedDataSource listDerivedDataSource = listdatasourcedao
					.getDataFromListDerivedDataSourcesOfIdData(listApplications.getIdData());
			// get dataset
			templateDescription = listDerivedDataSource.getDescription();
		} else {
			// get dataset
			templateDescription = listDataSource.getDescription();
		}

		List<String> all_microseg_cols = new ArrayList<>();

		// Get MaxDate and MaxRun
		String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

		Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);

		String strMaxDate = "";
		if (maxDate != null) {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			strMaxDate = dt.format(maxDate);
		}

		int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);
		LOG.debug("maxDate:" + strMaxDate);
		LOG.debug("maxrun:" + maxRun);

		// Check it is test run by execution date and run
		boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);

		// Get RuleCatalog details
		List<RuleCatalog> ruleCatalogList = null;
		if (isTestRun)
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
		else
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

		JSONArray dqChecks = new JSONArray();

		SqlRowSet tableNamesFromResultMasterTable = iResultsDAO.getTableNamesFromResultMasterTable(idApp);

		DecimalFormat numberFormat = new DecimalFormat("#0.00");

		while (tableNamesFromResultMasterTable.next()) {

			try {

				String tableName = tableNamesFromResultMasterTable.getString("Table_Name");

				// NullCheck
				if (tableName.equalsIgnoreCase("DATA_QUALITY_NullCheck_Summary")) {

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForNullCheck(idApp, strMaxDate, maxRun);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_COMPLETENESS;
						String check_name = DatabuckConstants.RULE_TYPE_NULLCHECK;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO.getColumnNonAggregateDetailsForMicrosegmentNullCheck(
								idApp, tableName, strMaxDate, maxRun, all_microseg_cols);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
							aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
							Double failPercentage = queryForRowSet.getDouble("failPercentage");

							String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
							aggObj.put("failPercentage", fail_perc_str);

							Double passPercentage = queryForRowSet.getDouble("passPercentage");

							String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
							aggObj.put("passPercentage", pass_perc_str);

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";

							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";

								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject nullCheckRowObj = new JSONObject();
						nullCheckRowObj.put("checkName", "Null Check");
						nullCheckRowObj.put("dqAggregate", colAggArray);
						dqChecks.put(nullCheckRowObj);
					}
				}

				// GlobalRules
				if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules")) {

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForCheck(idApp, tableName, strMaxDate, maxRun);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						SqlRowSet queryForRowSet = iResultsDAO.getColumnNonAggregateDetailsForMicrosegmentGlobalRules(
								idApp, tableName, strMaxDate, maxRun, all_microseg_cols);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							String ruleName = queryForRowSet.getString("ruleName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							JSONObject aggObj = new JSONObject();
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("ruleName", ruleName);
							aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
							aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
							Double failPercentage = queryForRowSet.getDouble("failPercentage");

							String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
							aggObj.put("failPercentage", fail_perc_str);

							Double passPercentage = queryForRowSet.getDouble("passPercentage");

							String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
							aggObj.put("passPercentage", pass_perc_str);

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							aggObj.put("dimensionName", queryForRowSet.getString("dimension_name"));

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";

							RuleCatalog ruleCatalog = getRuleCatalogDetailsForGlobalRule(ruleCatalogList,
									DatabuckConstants.RULE_TYPE_GLOBAL_RULE, ruleName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";

								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject globalRowRowObj = new JSONObject();
						globalRowRowObj.put("checkName", "Global Rules");
						globalRowRowObj.put("dqAggregate", colAggArray);
						dqChecks.put(globalRowRowObj);
					}
				}

				// Duplicate check - Selected fields
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_All")) {

					String dupcheck_summaryTable = "DATA_QUALITY_Duplicate_Check_Summary";
					String type = "all";

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForDuplicateCheck(idApp, dupcheck_summaryTable,
							strMaxDate, maxRun, type);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_UNIQUENESS;
						String check_name = DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO
								.getColumnNonAggregateDetailsForMicrosegmentDuplicateCheck(idApp, dupcheck_summaryTable,
										strMaxDate, maxRun, all_microseg_cols, type);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
							aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
							Double failPercentage = queryForRowSet.getDouble("failPercentage");

							String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
							aggObj.put("failPercentage", fail_perc_str);

							Double passPercentage = queryForRowSet.getDouble("passPercentage");

							String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
							aggObj.put("passPercentage", pass_perc_str);

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";

								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject dupCheckSelectedRow = new JSONObject();
						dupCheckSelectedRow.put("checkName", DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS);
						dupCheckSelectedRow.put("dqAggregate", colAggArray);
						dqChecks.put(dupCheckSelectedRow);
					}
				}

				// Duplicate check - Primary fields
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_Identity")) {

					String dupcheck_summaryTable = "DATA_QUALITY_Duplicate_Check_Summary";
					String type = "identity";

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForDuplicateCheck(idApp, dupcheck_summaryTable,
							strMaxDate, maxRun, type);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_UNIQUENESS;
						String check_name = DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO
								.getColumnNonAggregateDetailsForMicrosegmentDuplicateCheck(idApp, dupcheck_summaryTable,
										strMaxDate, maxRun, all_microseg_cols, type);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("totalRecords", queryForRowSet.getLong("totalRecords"));
							aggObj.put("failedCount", queryForRowSet.getLong("failedCount"));
							Double failPercentage = queryForRowSet.getDouble("failPercentage");

							String fail_perc_str = (failPercentage != null) ? numberFormat.format(failPercentage) : "";
							aggObj.put("failPercentage", fail_perc_str);

							Double passPercentage = queryForRowSet.getDouble("passPercentage");

							String pass_perc_str = (passPercentage != null) ? numberFormat.format(passPercentage) : "";
							aggObj.put("passPercentage", pass_perc_str);

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";
								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}

						}

						JSONObject dupcheckPrimaryRow = new JSONObject();
						dupcheckPrimaryRow.put("checkName", DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS);
						dupcheckPrimaryRow.put("dqAggregate", colAggArray);
						dqChecks.put(dupcheckPrimaryRow);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  " + e.getMessage());
			}
		}
		LOG.info("prepareColumnNonAggregateDataForChecks - END");
		return dqChecks;
	}

	private RuleCatalog getRuleCatalogDetailsForCheck(List<RuleCatalog> ruleCatalogList, String checkName,
			String checkColumn) {
//		LOG.info("getRuleCatalogDetailsForCheck - START");
//		LOG.debug("ruleCatalogList "+ruleCatalogList+" checkName "+checkName+" checkColumn "+checkColumn);
		RuleCatalog ruleCatalog = null;
		try {
			if (ruleCatalogList != null) {
				for (RuleCatalog rc : ruleCatalogList) {
					String rc_checkName = rc.getRuleType();
					String rc_checkColumn = rc.getColumnName();

					rc_checkName = (rc_checkName != null) ? rc_checkName = rc_checkName.trim() : "";

					rc_checkColumn = (rc_checkColumn != null) ? rc_checkColumn = rc_checkColumn.trim() : "";

					checkName = (checkName != null) ? checkName = checkName.trim() : "";

					checkColumn = (checkColumn != null) ? checkColumn = checkColumn.trim() : "";

					if (rc_checkName.equalsIgnoreCase(checkName) && rc_checkColumn.equalsIgnoreCase(checkColumn)) {
						ruleCatalog = rc;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
//		LOG.info("getRuleCatalogDetailsForCheck - END");
		return ruleCatalog;
	}

	private RuleCatalog getRuleCatalogDetailsForGlobalRule(List<RuleCatalog> ruleCatalogList, String checkName,
			String ruleName) {
//		LOG.info("getRuleCatalogDetailsForGlobalRule - START");
//		LOG.debug("ruleCatalogList "+ruleCatalogList+" checkName "+checkName+" ruleName "+ruleName);
		RuleCatalog ruleCatalog = null;
		try {
			if (ruleCatalogList != null) {
				for (RuleCatalog rc : ruleCatalogList) {
					String rc_checkName = rc.getRuleType();
					String rc_ruleName = rc.getRuleName();

					rc_checkName = (rc_checkName != null) ? rc_checkName = rc_checkName.trim() : "";

					rc_ruleName = (rc_ruleName != null) ? rc_ruleName = rc_ruleName.trim() : "";

					checkName = (checkName != null) ? checkName = checkName.trim() : "";

					ruleName = (ruleName != null) ? ruleName = ruleName.trim() : "";

					if (rc_checkName.equalsIgnoreCase(checkName) && rc_ruleName.equalsIgnoreCase(ruleName)) {
						ruleCatalog = rc;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
//		LOG.info("getRuleCatalogDetailsForGlobalRule - END");
		return ruleCatalog;
	}

	private JSONArray prepareColumnNonAggregateCountSummaryDataForChecks(ListApplications listApplications) {
		LOG.info("prepareColumnNonAggregateCountSummaryDataForChecks - START");
		LOG.debug("listApplications " + listApplications);
		long idApp = listApplications.getIdApp();
		String templateDescription = "";

		// Get Template Details
		ListDataSource listDataSource = listdatasourcedao
				.getDataFromListDataSourcesOfIdData(listApplications.getIdData());

		if (listDataSource != null && listDataSource.getDataLocation().trim().equalsIgnoreCase("Derived")) {
			ListDerivedDataSource listDerivedDataSource = listdatasourcedao
					.getDataFromListDerivedDataSourcesOfIdData(listApplications.getIdData());
			// get Dataset name
			templateDescription = listDerivedDataSource.getDescription();
		} else {
			// get Dataset name
			templateDescription = listDataSource.getDescription();
		}

		List<String> all_microseg_cols = new ArrayList<>();

		// Get MaxDate and MaxRun
		String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

		Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);

		String strMaxDate = "";
		if (maxDate != null) {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			strMaxDate = dt.format(maxDate);
		}

		int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);
		LOG.debug("maxDate:" + strMaxDate);
		LOG.debug("maxrun:" + maxRun);

		// Check it is test run by execution date and run
		boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);

		// Get RuleCatalog details
		List<RuleCatalog> ruleCatalogList = null;
		if (isTestRun)
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
		else
			ruleCatalogList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

		JSONArray dqChecks = new JSONArray();

		SqlRowSet tableNamesFromResultMasterTable = iResultsDAO.getTableNamesFromResultMasterTable(idApp);

		while (tableNamesFromResultMasterTable.next()) {

			try {

				String tableName = tableNamesFromResultMasterTable.getString("Table_Name");

				// NullCheck
				if (tableName.equalsIgnoreCase("DATA_QUALITY_NullCheck_Summary")) {

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForNullCheck(idApp, strMaxDate, maxRun);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_COMPLETENESS;
						String check_name = DatabuckConstants.RULE_TYPE_NULLCHECK;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO.getColumnNonAggregateSummaryForMicrosegmentNullCheck(
								idApp, tableName, strMaxDate, maxRun, all_microseg_cols);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}

							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("quality", queryForRowSet.getString("quality"));
							aggObj.put("count", queryForRowSet.getLong("count"));

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute, DQID and ruleTags
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								if (dqId != null)
									businessAttributes = businessAttributes.trim();

								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if (businessAttributesArray.length > 0) {
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else {
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject nullCheckRowObj = new JSONObject();
						nullCheckRowObj.put("checkName", "Null Check");
						nullCheckRowObj.put("dqAggregate", colAggArray);
						dqChecks.put(nullCheckRowObj);
					}
				}

				// GlobalRules
				if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules")) {

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForCheck(idApp, tableName, strMaxDate, maxRun);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						SqlRowSet queryForRowSet = iResultsDAO.getColumnNonAggregateSummaryForMicrosegmentGlobalRules(
								idApp, tableName, strMaxDate, maxRun, all_microseg_cols);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							String ruleName = queryForRowSet.getString("ruleName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							JSONObject aggObj = new JSONObject();
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("ruleName", ruleName);
							aggObj.put("quality", queryForRowSet.getString("quality"));
							aggObj.put("count", queryForRowSet.getLong("count"));

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							aggObj.put("dimensionName", queryForRowSet.getString("dimension_name"));

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForGlobalRule(ruleCatalogList,
									DatabuckConstants.RULE_TYPE_GLOBAL_RULE, ruleName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";

								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);

							String[] businessAttributesArray = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");

							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject globalRowRowObj = new JSONObject();
						globalRowRowObj.put("checkName", "Global Rules");
						globalRowRowObj.put("dqAggregate", colAggArray);
						dqChecks.put(globalRowRowObj);
					}
				}

				// Duplicate check - Selected fields
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_All")) {

					String dupcheck_summaryTable = "DATA_QUALITY_Duplicate_Check_Summary";
					String type = "all";

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForDuplicateCheck(idApp, dupcheck_summaryTable,
							strMaxDate, maxRun, type);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_UNIQUENESS;
						String check_name = DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO
								.getColumnNonAggregateSummaryForMicrosegmentDuplicateCheck(idApp, dupcheck_summaryTable,
										strMaxDate, maxRun, all_microseg_cols, type);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("quality", queryForRowSet.getString("quality"));
							aggObj.put("count", queryForRowSet.getLong("count"));

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";
								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");
							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if(businessAttributesArray.length>0){
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else{
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}
						}

						JSONObject dupCheckSelectedRow = new JSONObject();
						dupCheckSelectedRow.put("checkName", DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS);
						dupCheckSelectedRow.put("dqAggregate", colAggArray);
						dqChecks.put(dupCheckSelectedRow);
					}
				}

				// Duplicate check - Primary fields
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_Identity")) {

					String dupcheck_summaryTable = "DATA_QUALITY_Duplicate_Check_Summary";
					String type = "identity";

					// Identify Microsegments
					all_microseg_cols = iResultsDAO.getMicrosegmentsForDuplicateCheck(idApp, dupcheck_summaryTable,
							strMaxDate, maxRun, type);

					if (all_microseg_cols != null && all_microseg_cols.size() > 0) {

						// Default Dimension
						String default_null_check_dmn = DatabuckConstants.DIMENSION_UNIQUENESS;
						String check_name = DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS;

						// Get the NullCheck column dimensions from RuleCatalog
						Map<String, String> col_dimension_map = iResultsDAO.getColumnDimensionsForCheck(check_name,
								idApp);

						SqlRowSet queryForRowSet = iResultsDAO
								.getColumnNonAggregateSummaryForMicrosegmentDuplicateCheck(idApp, dupcheck_summaryTable,
										strMaxDate, maxRun, all_microseg_cols, type);

						JSONArray colAggArray = new JSONArray();
						while (queryForRowSet.next()) {
							JSONObject aggObj = new JSONObject();
							String columnName = queryForRowSet.getString("ColName");
							String datasetName = "";
							String systemName = "";
							String row_identifer_type = "";
							String batch_id = "";
							if (templateDescription != null && !templateDescription.trim().isEmpty()) {
								String[] dataset_fields = templateDescription.split(",");
								datasetName = dataset_fields[0];
								if (dataset_fields.length > 1) {
									systemName = dataset_fields[1];
								}
								if (dataset_fields.length > 2) {
									row_identifer_type = dataset_fields[2];
								}
								String temp = strMaxDate.replace("-", "");
								batch_id = datasetName + "_" + temp;
							}
							aggObj.put("executionDate", strMaxDate);
							aggObj.put("run", maxRun);
							aggObj.put("dataset", datasetName);
							aggObj.put("systemName", systemName);
							aggObj.put("row_identifer_type", row_identifer_type);
							aggObj.put("batch_id", batch_id);
							aggObj.put("columnName", columnName);
							aggObj.put("quality", queryForRowSet.getString("quality"));
							aggObj.put("count", queryForRowSet.getLong("count"));

							for (String gb_col : all_microseg_cols) {
								if (gb_col.equalsIgnoreCase("project_name")) {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col).replaceAll("::", "-"));
								} else {
									aggObj.put(gb_col, queryForRowSet.getString(gb_col));
								}
							}

							// Set Dimension
							String col_dmn = default_null_check_dmn;

							if (col_dimension_map != null && col_dimension_map.containsKey(columnName)) {
								col_dmn = col_dimension_map.get(columnName);
							}
							aggObj.put("dimensionName", col_dmn);

							// Get BusinessAttribute and DQID
							String dqId = "";
							String businessAttributes = "";
							String ruleTags = "";
							RuleCatalog ruleCatalog = getRuleCatalogDetailsForCheck(ruleCatalogList, check_name,
									columnName);
							if (ruleCatalog != null) {
								// Get DQID
								dqId = ruleCatalog.getReviewComments();
								dqId = (dqId != null) ? dqId.trim() : "";

								// GetBusinessAttribute
								businessAttributes = ruleCatalog.getBusinessAttributes();
								businessAttributes = (businessAttributes != null) ? businessAttributes.trim() : "";
								// Get ruleTags
								ruleTags = ruleCatalog.getRuleTags();
							}
							aggObj.put("dqId", dqId);
							String businessAttributesArray[] = businessAttributes.split(",");

							String project_name = queryForRowSet.getString("project_name");
							// Creating multiple objects for multiple tags
							if (project_name != null && !project_name.trim().equalsIgnoreCase("default")
									&& ruleTags != null && !ruleTags.trim().isEmpty()) {
								String[] ruleTagsArray = ruleTags.split(",");
								for (String ruleTag : ruleTagsArray) {
									if (ruleTag != null && project_name.trim().equalsIgnoreCase(ruleTag)) {
										if (businessAttributesArray.length > 0) {
											for (String businessAttribute : businessAttributesArray) {
												aggObj.put("businessAttribute", businessAttribute);
												colAggArray.put(new JSONObject(aggObj.toString()));
											}
										} else {
											aggObj.put("businessAttribute", "");
											colAggArray.put(aggObj);
										}
									}
								}
							}

							JSONObject dupcheckPrimaryRow = new JSONObject();
							dupcheckPrimaryRow.put("checkName", DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS);
							dupcheckPrimaryRow.put("dqAggregate", colAggArray);
							dqChecks.put(dupcheckPrimaryRow);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  " + e.getMessage());
			}
		}
		LOG.info("prepareColumnNonAggregateCountSummaryDataForChecks - END");
		return dqChecks;
	}

	private String getExceptionDataFileLocation(String projectName, String domainName, long idApp, String date,
			long run, String exceptionFolder) {
		LOG.info("getExceptionDataFileLocation - START");
		LOG.debug("projectName" + projectName + " domainName " + domainName + " idApp " + idApp + " date " + date
				+ "  run " + run + " exceptionFolder " + exceptionFolder);
		String csvLocation = "";
		String deploymentMode = clusterProperties.getProperty("deploymentMode");

		if (deploymentMode != null) {
			// When deploymentMode is S3
			if (deploymentMode.trim().equalsIgnoreCase("s3")) {
				String s3CsvPath = appDbConnectionProperties.getProperty("s3CsvPath");
				csvLocation = s3CsvPath + "/csvFiles";

			}
			// When deploymentMode is local
			else if (deploymentMode.trim().equalsIgnoreCase("local")) {
				csvLocation = "file:///" + databuckFileUtility.getDatabuckHome() + "/csvFiles";

			}
			// When deploymentMode is hdfs
			else if (deploymentMode.trim().equalsIgnoreCase("hdfs")) {
				String hdfs_result_directory = clusterProperties.getProperty("hdfs_result_directory");
				if (hdfs_result_directory != null)
					hdfs_result_directory = hdfs_result_directory.trim();

				// Mapr ticket enabled is enabled store the data in project/domain location
				String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

				if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y"))
					csvLocation = hdfs_result_directory + "/" + domainName + "/" + projectName;
				else
					csvLocation = hdfs_result_directory + "/csvFiles";

			}

			csvLocation = csvLocation + "/" + idApp + "/" + date + "/" + run + "/" + exceptionFolder;
		}
		LOG.info("getExceptionDataFileLocation - END");
		return csvLocation;
	}

	private Map<String, String> getCheckToFileNameMap() {
		LOG.info("getCheckToFileNameMap - START");
		Map<String, String> checkToFileNameMap = new HashMap<>();
		checkToFileNameMap.put("Null Check", "NullData");
		checkToFileNameMap.put("Length Check", "LengthCheckData");
		checkToFileNameMap.put("Max Length Check", "MaxLengthCheckData");
		checkToFileNameMap.put("Data Drift Check", "DataDriftData");
		checkToFileNameMap.put("Bad Data Check", "BadData");
		checkToFileNameMap.put("Numerical Statistics Check", "NumStatData");
		checkToFileNameMap.put("Record Anomaly Check", "RecordAnomalyData");
		checkToFileNameMap.put("Date Rule Check", "DateRuleFailedData");
		checkToFileNameMap.put("Default Check", "DefaultData");
		checkToFileNameMap.put("Pattern Check", "BadData_Pattern_Check");
		LOG.info("getCheckToFileNameMap - END");
		return checkToFileNameMap;
	}

	private Map<String, Object> getMaxDateAndRun(Long idApp) {
		Map<String, Object> maxDateAndRunMap = new HashMap<>();
		LOG.info("getMaxDateAndRun - START");
		LOG.debug("idApp " + idApp);
		// Read listApplications details
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		String flagGrca = listApplicationsData.getKeyGroupRecordCountAnomaly();

		// Get MaxDate and MaxRun
		String rca_tran_table = "DATA_QUALITY_Transactionset_sum_A1";

		if (flagGrca != null && flagGrca.equalsIgnoreCase("Y")) {
			rca_tran_table = "DATA_QUALITY_Transactionset_sum_dgroup";
		}

		// get max date and run

		Date maxDate = validationcheckdao.getMaxDateForTable(rca_tran_table, idApp);
		String strMaxDate = "";
		if (maxDate != null) {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			strMaxDate = dt.format(maxDate);

			int maxRun = validationcheckdao.getMaxRunForDate(strMaxDate, rca_tran_table, idApp);

			if (maxRun > 0) {
				maxDateAndRunMap.put("maxDate", strMaxDate);
				maxDateAndRunMap.put("maxRun", maxRun);
			}
		}
		LOG.info("getMaxDateAndRun - END");
		return maxDateAndRunMap;
	}

}
