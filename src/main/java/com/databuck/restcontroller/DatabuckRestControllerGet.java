package com.databuck.restcontroller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.controller.JSONController;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.impl.ValidationCheckDAOImpl;
import com.databuck.dto.DataconnectionDto;
import com.databuck.service.AuthorizationService;
import com.databuck.util.TokenValidator;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckRestControllerGet {

	@Autowired
	DatabuckRestDAOImpl databuckRestDAOImpl;
	
	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	JSONController Jsoncontroller;

	@Autowired
	IListDataSourceDAO listdatasourcedao;
	
	
	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	ValidationCheckDAOImpl validationCheckDAOImpl;
	
	@Autowired
	private TokenValidator tokenValidator;
	
	private static final Logger LOG = Logger.getLogger(DatabuckRestControllerGet.class);
	
	
	
	@RequestMapping(value = "restapi/dataconnection/all", method = RequestMethod.GET)
	public String viewAllDataConnections(HttpServletRequest req, HttpServletResponse response,
			HttpServletRequest request, HttpSession session) {
		LOG.info("restapi/dataconnection/all - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("projectId  " + session.getAttribute("projectId")+" userProjectList "+session.getAttribute("userProjectList"));
				Long projectId= (Long)session.getAttribute("projectId");
				List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
				List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId,projList,"","");
				JSONArray jsonArray = new JSONArray();
				JSONObject mainObj = new JSONObject();
				for (ListDataSchema lds : listdataschema) {
					JSONObject obj = new JSONObject();
					obj.put("idDataSchema", lds.getIdDataSchema());
					obj.put("schemaName", lds.getSchemaName());
					obj.put("schemaType", lds.getSchemaType());
					obj.put("ipAddress", lds.getIpAddress());
					obj.put("databaseSchema", lds.getDatabaseSchema());
					obj.put("port", lds.getPort());
					if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle")
							|| lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle RAC")) {
						obj.put("serviceName", lds.getKeytab());
					}
					if (lds.getSchemaType().toString().trim().equalsIgnoreCase("MSSQLActiveDirectory")) {
						obj.put("domain", lds.getDomain());
					}
					if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Hive Kerberos")) {
						obj.put("principal", lds.getDomain());
						obj.put("gss_jaas", lds.getKeytab());
						obj.put("krb5conf", lds.getKrb5conf());
					}
					jsonArray.put(obj);
				}
				mainObj.put("All data Connections", jsonArray);
				return mainObj.toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/dataconnection/all - END");
		return json.toString();
	}

	@ApiOperation(value = "Get data connection by schema id", notes = "This API used to get a data connection as per the id", tags="Connection")
	@ResponseBody
	@RequestMapping(value = "restapi/dataconnection", method = RequestMethod.GET ,produces = "application/json")
	public DataconnectionDto viewIdDataConnections(HttpServletRequest req, HttpServletResponse response,
			HttpServletRequest request, HttpSession session,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema) {
		
		LOG.info("restapi/dataconnection - START");
		DataconnectionDto json = new DataconnectionDto();

		try {
			LOG.debug("token "+request.getHeader("token"));
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization!=null?authorization:"");
				// Fetch Details of idDataSchema
			if (isUserValid) {
				LOG.debug("idDataSchema "+idDataSchema);
				List<ListDataSchema> schemaList = listdatasourcedao.getListDataSchemaId(idDataSchema);
				DataconnectionDto obj = new DataconnectionDto();

				if (schemaList != null && !schemaList.isEmpty()) {

					ListDataSchema lds = schemaList.get(0);

					if (lds != null) {
						obj.setIdDataSchema( lds.getIdDataSchema());
						obj.setSchemaName( lds.getSchemaName());
						obj.setSchemaType( lds.getSchemaType());
						obj.setIpAddress( lds.getIpAddress());
						obj.setDatabaseSchema( lds.getDatabaseSchema());
						obj.setPort( lds.getPort());
						if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle")
								|| lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle RAC")) {
							obj.setServiceName( lds.getKeytab());
						}
						if (lds.getSchemaType().toString().trim().equalsIgnoreCase("MSSQLActiveDirectory")) {
							obj.setDomain( lds.getDomain());
						}
						if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Hive Kerberos")) {
							obj.setPrincipal( lds.getDomain());
							obj.setGss_jaas(lds.getKeytab());
							obj.setKrb5conf( lds.getKrb5conf());
						}
						obj.setIsActive( lds.getAction());
						return obj;
					} else {
						LOG.error("Invalid SchemaId");
						json.setFail( "Invalid SchemaId");
					}
						

				} else {
					json.setFail( "Invalid SchemaId");
					LOG.error("Invalid SchemaId");
				}
			} else {
				LOG.error("Invalid Authorization");
				json.setFail("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.setFail("Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/dataconnection - END");
		return json;
	}

	@RequestMapping(value = "restapi/datatemplate", method = RequestMethod.GET)
	public String viewAllDataTemplate(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {
		LOG.info("restapi/datatemplate - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("Session "+session);
			if (isUserValid) {
				Long projectId= (Long)session.getAttribute("projectId");
				List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
				String fromDate = (String) session.getAttribute("fromDate");
				String toDate = (String) session.getAttribute("toDate");
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTable(projectId,projList,fromDate,toDate);
				JSONArray jsonArray = new JSONArray();
				JSONObject mainObj = new JSONObject();
				for (ListDataSource lds : listdatasource) {
					JSONObject obj = new JSONObject();
					obj.put("idData", lds.getIdData());
					obj.put("name", lds.getName());
					obj.put("dataLocation", lds.getDataLocation());
					obj.put("tableName", lds.getTableName());
					obj.put("createdAt", lds.getCreatedAt());
					jsonArray.put(obj);
				}

				mainObj.put("All data Template", jsonArray);
				return mainObj.toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/datatemplate - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/datatemplate", params = "idData", method = RequestMethod.GET)
	public String viewIdDataTemplate(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, long idData) {
		LOG.info("restapi/datatemplate - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				LOG.debug("idData "+idData);
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableId(idData);
				List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
				JSONObject obj = new JSONObject();
				for (ListDataSource lds : listdatasource) {
					if (idData == lds.getIdData()) {
						obj.put("idData", lds.getIdData());
						obj.put("name", lds.getName());
						obj.put("dataLocation", lds.getDataLocation());
						obj.put("tableName", lds.getTableName());
						obj.put("createdAt", lds.getCreatedAt());
						JSONArray columnsArray =new JSONArray();

						for (ListDataDefinition ldd:listdatadefinition) {
							
							JSONObject columns = new JSONObject();
							columns.put("columnName", ldd.getDisplayName());
							columns.put("columnType", ldd.getFormat());
							columns.put("primaryCheck", ldd.getPrimaryKey());
							columns.put("subsegmentCheck", ldd.getDgroup());
							columns.put("lastReadTimeCheck", ldd.getIncrementalCol());
							columns.put("doNotDisplayCheck", ldd.getIsMasked());
							columns.put("partitionCheck", ldd.getPartitionBy());
							columns.put("nullCheck", ldd.getNonNull());
							columns.put("duplicateCheck", ldd.getDupkey());
							columns.put("numericalCheck", ldd.getNumericalStat());
							columns.put("textCheck", ldd.getStringStat());
							columns.put("dataDriftCheck", ldd.getDataDrift());
							columns.put("recordAnomalyCheck", ldd.getRecordAnomaly());
							columns.put("nullThreshold", ldd.getNullCountThreshold());
							columns.put("numFingerprintThreshold", ldd.getNumericalThreshold());
							columns.put("textFingerprintThreshold", ldd.getStringStatThreshold());
							columns.put("dataDriftThreshold", ldd.getDataDriftThreshold());
							columns.put("recordAnomalyThreshold", ldd.getRecordAnomalyThreshold());
							columns.put("matchValueCheck", ldd.getMeasurement());
							columnsArray.put(columns);
						}
						obj.put("columns", columnsArray);
					}
				}
				return obj.toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		
		LOG.info("restapi/datatemplate - END");

		return json.toString();
	}

	@RequestMapping(value = "restapi/validationcheck", method = RequestMethod.GET)
	public String viewAllValidationChecks(HttpServletRequest req, HttpServletResponse response,
			HttpServletRequest request, HttpSession session) {
		LOG.info("restapi/validationcheck - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("session "+session);
			if (isUserValid) {
				Long projectId= (Long)session.getAttribute("projectId");
				String fromDate = (String) session.getAttribute("fromDate");
				String toDate = (String) session.getAttribute("toDate");
				List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
				List<ListApplicationsandListDataSources> listappslistds = validationcheckdao.getdatafromlistappsandlistdatasources(projectId,projList,toDate,fromDate);
				JSONArray jsonArray = new JSONArray();
				JSONObject mainObj = new JSONObject();
				for (ListApplicationsandListDataSources lad : listappslistds) {
					JSONObject obj = new JSONObject();
					
					obj.put("idApp", lad.getIdApp());
					obj.put("validationCheckName", lad.getLaName());
					obj.put("DataTemplateName", lad.getLsName());
					obj.put("appType", lad.getAppType());
					obj.put("createdAt", lad.getCreatedAt());
					jsonArray.put(obj);
				}
				mainObj.put("All Application Checks", jsonArray);
				return mainObj.toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("fail", "Request failed");
			LOG.error("Exception  "+e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/validationcheck - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/validationcheck", params = "idApp", method = RequestMethod.GET)
	public String viewIdValidationChecks(HttpServletRequest req, HttpServletResponse response,
			HttpServletRequest request, HttpSession session, long idApp) {
		LOG.info("restapi/validationcheck - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			LOG.debug("idApp "+idApp);
			if (isUserValid) {
				return databuckRestDAOImpl.viewValidationCheck(idApp).toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/validationcheck - END");
		return json.toString();
	}
	
	@RequestMapping(value = "restapi/taskstatus", method = RequestMethod.GET)
	public String startStatuspoll(HttpServletRequest request, HttpSession session, Long idApp,
			HttpServletResponse response) {
		LOG.info("restapi/taskstatus - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("idApp "+idApp);
			if (isUserValid) {
				return databuckRestDAOImpl.taskstatus(idApp);
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/taskstatus - END");
		return json.toString();
	}


	@RequestMapping(value = "restapi/result", method = RequestMethod.GET)
	public String result(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Long idApp) {
		LOG.info("restapi/result - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("idApp "+idApp);
			if (isUserValid) {
				return Jsoncontroller.prepareJSONData(idApp, null, 0, true).toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/result - END");
		return json.toString();
	}
	@RequestMapping(value = "restapi/result", method = RequestMethod.DELETE)
	public String delete(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ValidationCheckBean validationCheckBean) {
		LOG.info("restapi/result - START");
		JSONObject json = new JSONObject();
		
		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("Getting  parameters for validationCheckBean , " + validationCheckBean);
			if (isUserValid) {
				json = databuckRestDAOImpl.deleteResultFromDB(Long.valueOf(validationCheckBean.getIdApp()));
				return json.toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/result - END");
		return json.toString();
	}


	@RequestMapping(value = "restapi/dashboard", method = RequestMethod.GET)
	public String dashboard(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Long idApp) {
		LOG.info("restapi/dashboard - START");
		JSONObject json = new JSONObject();

		try {
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			LOG.debug("idApp "+idApp);
			if (isUserValid) {
				return databuckRestDAOImpl.prepareJSONDataForDashboardFast(idApp).toString();
			}
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/dashboard - END");
		return json.toString();
	}
}
