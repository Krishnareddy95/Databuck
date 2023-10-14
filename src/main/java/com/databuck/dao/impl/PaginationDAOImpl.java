package com.databuck.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.constants.ResultTableConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IPaginationDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dto.ValidationListRequest;
import com.databuck.dto.ValidationResultRequest;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.ToCamelCase;

@Repository
public class PaginationDAOImpl implements IPaginationDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IListDataSourceDAO listDataSourceDAO;

	private static final Logger LOG = Logger.getLogger(PaginationDAOImpl.class);

	@Override
	public Map<String, Object> getAllValidations(ValidationListRequest validationRequest, String applicationIds) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		LOG.debug("Into getAllValidations method");

		Map<String, Object> result = new HashMap<>();
		String appDbSchemaName = "";

		// Reading app db schema name for query.
		if (appDbConnectionProperties.containsKey("db.schema.name"))
			appDbSchemaName = appDbConnectionProperties.getProperty("db.schema.name") + ".";
		if (appDbSchemaName == null || appDbSchemaName.isEmpty()) {
			appDbSchemaName = "";
		}
		if (!applicationIds.trim().isEmpty()) {

			try {
				Map<String, Object> filterObject = validationRequest.getFilterCondtionMap();
				String filterCondition = "";
				if (filterObject != null) {
					filterCondition = getDQIFilterCondition(filterObject);
					LOG.debug("Filter options are : " + filterCondition);
				}

				// Setting up default sort options date and idApp
				String sortOption = " t1.date desc, t1.idapp ";
				if (validationRequest.getSort() != null && !validationRequest.getSort().isEmpty()) {
					sortOption = getSortOption(validationRequest.getSort());
					if(sortOption.trim().equalsIgnoreCase("")) {
						sortOption = " t1.date desc, t1.idapp ";
					}
					LOG.debug("Sort options are : " + sortOption);
				}

				// Creating global search options for query
				String globalSearchOption = " and 1 = (case when validationCheckName like 'LIKE-TEXT' then 1 when sourceName like 'LIKE-TEXT' then 1 else 0 end ) "
						.replaceAll("LIKE-TEXT", "%" + validationRequest.getGlobalSearchOption() + "%");
				globalSearchOption = (validationRequest.getGlobalSearchOption() == null
						|| validationRequest.getGlobalSearchOption().isEmpty()) ? "" : globalSearchOption;

				LOG.debug("Global Search options : " + globalSearchOption);
				int count = 0;

				if (validationRequest.getCountRequired()) {
					count = getCount(validationRequest, applicationIds, appDbSchemaName, globalSearchOption,
							filterCondition);
					if (count > 0) {
						result.put("count", count);
					}
				}
//		String sql = "select t1.*, t2.test_run,t3.recordCount, t4.folderName, t5.idData, t5.schemaName from data_quality_dashboard t1  "
//			+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
//			+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
//			+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
//			+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run left join (Select idApp, folderName from processData group by idApp, folderName) t4 on t1.IdApp=t4.idApp "
//			+ "left join ( select " + appDbSchemaName + "listApplications.idApp, " + appDbSchemaName
//			+ "listApplications.idData, " + appDbSchemaName + "listDataSchema.schemaName from "
//			+ appDbSchemaName + "listApplications join " + appDbSchemaName + "listDataSources on "
//			+ appDbSchemaName + "listApplications.idData=" + appDbSchemaName
//			+ "listDataSources.idData left join " + appDbSchemaName + "listDataSchema on " + appDbSchemaName
//			+ "listDataSources.idDataSchema=" + appDbSchemaName
//			+ "listDataSchema.idDataSchema) t5 on t1.IdApp=t5.idApp where t1.idApp in (" + applicationIds
//			+ ") and (execution_date between '" + validationRequest.getFromDate() + "' and '"
//			+ validationRequest.getToDate() + "') " + globalSearchOption + filterCondition + " order by "
//			+ sortOption + " limit " + validationRequest.getPageSize() + " offset "
//			+ validationRequest.getPageNo() * validationRequest.getPageSize();

				String sql = "select t1.*, t2.test_run,t3.recordCount, t4.folderName from data_quality_dashboard t1  "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run left join (Select idApp, folderName from processData group by idApp, folderName) t4 on t1.IdApp=t4.idApp where "
						+ "t1.idApp in ("
						+ applicationIds + ") and (execution_date between '" + validationRequest.getFromDate()
						+ "' and '" + validationRequest.getToDate() + "') " + globalSearchOption + filterCondition
						+ " order by " + sortOption + " limit " + validationRequest.getPageSize() + " offset "
						+ validationRequest.getPageNo() * validationRequest.getPageSize();

				LOG.debug("Getting validation result for query : " + sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

				while (queryForRowSet.next()) {
					DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
					Long idApp = queryForRowSet.getLong("idApp");
					// dashboard.setTemplateId(queryForRowSet.getLong("idData"));
					// dashboard.setConnectionName(queryForRowSet.getString("schemaName"));
					dashboard.setFileName(queryForRowSet.getString("folderName"));
					dashboard.setIdApp(idApp);
					dashboard.setDate(queryForRowSet.getString("date"));
					dashboard.setRun(queryForRowSet.getLong("run"));
					dashboard.setTestRun(queryForRowSet.getString("test_run"));
					dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));

					try {
						String templateSql = "select la.idData,la.active as active, ls.schemaName as connectionName, case when lds.folderName = 'Query' then lds.name else lds.folderName end as fileName, applyRules, lds.profilingEnabled,lds.dataLocation, lds.databaseName "
								+ " from listApplications la"
								+ " join (select listDataSources.*,COALESCE(NULLIF(listDataAccess.schemaName, ''), listDataSources.dataLocation) as databaseName,listDataAccess.folderName "
								+ " from listDataSources left join listDataAccess on listDataSources.idData = listDataAccess.idData) as lds on la.idData=lds.idData "
								+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp="
								+ idApp;
						Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
						dashboard.setTemplateId(Long.valueOf(la_data.get("idData").toString()));
						dashboard.setConnectionName(String.valueOf(la_data.get("connectionName")));
						dashboard.setApplyRules(String.valueOf(la_data.get("applyRules")));
						dashboard.setProfilingEnabled(String.valueOf(la_data.get("profilingEnabled")));
						dashboard.setDataLocation(String.valueOf(la_data.get("dataLocation")));
						dashboard.setDatabaseName(String.valueOf(la_data.get("databaseName")));
						dashboard.setStatus(String.valueOf(la_data.get("active")));
						ListDerivedDataSource listDerivedDataSource = listDataSourceDAO
								.getDataFromListDerivedDataSourcesOfIdData(
										Long.valueOf(la_data.get("idData").toString()));
						String isDerivedTemplate = listDerivedDataSource != null ? "Y" : "N";
						dashboard.setIsDerivedTemplate(isDerivedTemplate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(dashboard.getIsDerivedTemplate()!=null && dashboard.getIsDerivedTemplate().equals("Y")) {
						dashboard.setSource1(dashboard.getValidationCheckName());
					}else {
						dashboard.setSource1(queryForRowSet.getString("sourceName"));
					}
					dashboard.setRecordCountStatus(queryForRowSet.getString("recordCountStatus"));
					dashboard.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
					dashboard.setStringFieldStatus(queryForRowSet.getString("stringFieldStatus"));
					dashboard.setNumericalFieldStatus(queryForRowSet.getString("numericalFieldStatus"));
					dashboard.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
					dashboard.setUserSelectedFieldsStatus(queryForRowSet.getString("userSelectedFieldStatus"));
					dashboard.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
					dashboard.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
					dashboard.setProjectName(getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
					dashboard.setAggreagteDQI(queryForRowSet.getDouble("aggregateDQI")); // added
					dashboard.setRecordCount(queryForRowSet.getLong("recordCount"));
					masterDashboard.add(dashboard);
				}

				if (masterDashboard.isEmpty()) {
					LOG.debug("List is empty.");
				}
				if(filterObject.containsKey("datasource")||filterObject.containsKey("database")) {
						String datasourceStr = "" + filterObject.get("datasource");
						String databaseStr = "" + filterObject.get("database");
						masterDashboard = masterDashboard.stream().filter(md->md.getDatabaseName().toLowerCase().contains(databaseStr.toLowerCase()) 
								|| md.getConnectionName().toLowerCase().contains(datasourceStr.toLowerCase())).collect(Collectors.toList());
						
				}
				if (validationRequest.getSort() != null && !validationRequest.getSort().isEmpty()) {
					String sort = validationRequest.getSort();
					String[] sorts = sort.split("=");
					String field = getFieldMappings(sorts[0]);
					if(sort.equalsIgnoreCase("DESC")) {
						if(field.equalsIgnoreCase("datasource")) {
							masterDashboard.sort(Comparator.comparing(DataQualityMasterDashboard::getConnectionName, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
						}else if (field.equalsIgnoreCase("database")) {
							masterDashboard.sort(Comparator.comparing(DataQualityMasterDashboard::getDatabaseName, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
						}
					}else {
						if(field.equalsIgnoreCase("datasource")) {
							masterDashboard.sort(Comparator.comparing(DataQualityMasterDashboard::getConnectionName, Comparator.nullsFirst(Comparator.naturalOrder())));
						}else if (field.equalsIgnoreCase("database")) {
							masterDashboard.sort(Comparator.comparing(DataQualityMasterDashboard::getDatabaseName, Comparator.nullsFirst(Comparator.naturalOrder())));
						}
					}
				}
				LOG.debug("Got the total number of validations are : " + masterDashboard.size());
				result.put("validations", masterDashboard);

			} catch (Exception e) {
				LOG.error("Exception occured while fetching details from data_quality_dashboard : " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	// Method provides column name mappings according to tables
	private String getSortOption(String sort) {
		try {
			LOG.debug("Getting sort option for : " + sort);
			String[] sorts = sort.split("=");
			String field = getFieldMappings(sorts[0]);
			if (field.equalsIgnoreCase("recordCount"))
				field = "t3." + field;
			else if (field.equalsIgnoreCase("testRun"))
				field = "t2." + field;
			else if (field.equalsIgnoreCase("folderName"))
				field = "t4." + field;
			else if (field.equalsIgnoreCase("schemaName"))
				field = "t5." + field;
			else if (field.equalsIgnoreCase("idData"))
				field = "t5." + field;
			else 
				field = "t1." + field;
			
			if(!field.equalsIgnoreCase("t1.datasource") && !field.equalsIgnoreCase("t1.database")) {
				return " " + field + " " + sorts[1] + " ";
			}else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// Method will provide count for provided parameters for validation table
	private int getCount(ValidationListRequest validationRequest, String applicationIds, String appDbSchemaName,
			String globalSearchOption, String filterCondition) {

		try {
//	    String sql = "select count(*) as count from data_quality_dashboard t1  "
//		    + "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
//		    + "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
//		    + "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
//		    + "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run left join (Select idApp, folderName from processData group by idApp, folderName) t4 on t1.IdApp=t4.idApp "
//		    + "left join ( select la.idApp, la.idData, ls.schemaName from " + appDbSchemaName
//		    + "listApplications la join " + appDbSchemaName
//		    + "listDataSources lds on la.idData=lds.idData left join " + appDbSchemaName
//		    + "listDataSchema ls on lds.idDataSchema=ls.idDataSchema) t5 on t1.IdApp=t5.idApp where t1.idApp in ("
//		    + applicationIds + ") and (execution_date between '" + validationRequest.getFromDate() + "' and '"
//		    + validationRequest.getToDate() + "') " + globalSearchOption + filterCondition;

//	    String sql = "select count(*) as count from data_quality_dashboard t1  "
//		    + "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
//		    + "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
//		    + "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
//		    + "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run left join (Select idApp, folderName from processData group by idApp, folderName) t4 on t1.IdApp=t4.idApp "
//		    + "left join ( select " + appDbSchemaName + "listApplications.idApp, " + appDbSchemaName
//		    + "listApplications.idData, " + appDbSchemaName + "listDataSchema.schemaName from "
//		    + appDbSchemaName + "listApplications join " + appDbSchemaName + "listDataSources on "
//		    + appDbSchemaName + "listApplications.idData=" + appDbSchemaName
//		    + "listDataSources.idData left join " + appDbSchemaName + "listDataSchema on " + appDbSchemaName
//		    + "listDataSources.idDataSchema=" + appDbSchemaName
//		    + "listDataSchema.idDataSchema) t5 on t1.IdApp=t5.idApp where t1.idApp in (" + applicationIds
//		    + ") and (execution_date between '" + validationRequest.getFromDate() + "' and '"
//		    + validationRequest.getToDate() + "') " + globalSearchOption + filterCondition;

			String sql = "select count(*) as count from data_quality_dashboard t1  "
					+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
					+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
					+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
					+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run where t1.idApp in (" + applicationIds
					+ ") and (execution_date between '" + validationRequest.getFromDate() + "' and '"
					+ validationRequest.getToDate() + "') " + globalSearchOption + filterCondition;

			LOG.debug("Getting count : " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				return queryForRowSet.getInt("count");
			}
		} catch (Exception ex) {
			LOG.error("Getting exception while getting count : " + ex.getMessage());
			ex.printStackTrace();
			return 0;
		}
		return 0;
	}

	private String getDQIFilterCondition(Map<String, Object> filterObject) {
		String filterCondition = "";
		try {
			Set<String> keySet = filterObject.keySet();
			for (String filterColumn : keySet) {
				if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					String filterValue = "" + filterObject.get(filterColumn);
					// Adding filter attribute according to the table
					//In Postgres column names are already in lower case
					if (filterColumn.equals("recordCount") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " t3." + getFieldMappings(filterColumn) + "::text LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equals("testRun") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " t2." + getFieldMappings(filterColumn) + "::text LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equals("recordCountStatus") && filterValue.contains("N")
							&& !filterValue.isEmpty()) {
						filterCondition = filterCondition + getFieldMappings(filterColumn) + " is null and";
					} else if (filterColumn.equalsIgnoreCase("fileName") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " t4." + getFieldMappings(filterColumn) + "::text LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equalsIgnoreCase("connectionName") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " t5." + getFieldMappings(filterColumn) + "::text LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equalsIgnoreCase("templateId") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " t5." + getFieldMappings(filterColumn) + "::text LIKE '%"
								+ filterValue + "%' and ";
					} else {
						if (!filterValue.isEmpty() && !filterColumn.isEmpty()) {
							if(!filterColumn.equalsIgnoreCase("datasource") && !filterColumn.equalsIgnoreCase("database")) {
							filterCondition = filterCondition + " t1." + getFieldMappings(filterColumn) + "::text LIKE '%"
									+ filterValue + "%' and ";
							}
						}
					}
				}else {
					String filterValue = "" + filterObject.get(filterColumn);
					// Adding filter attribute according to the table
					if (filterColumn.equals("recordCount") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(t3." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equals("testRun") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(t2." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equals("recordCountStatus") && filterValue.contains("N")
							&& !filterValue.isEmpty()) {
						filterCondition = filterCondition + getFieldMappings(filterColumn) + " is null and";
					} else if (filterColumn.equalsIgnoreCase("fileName") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(t4." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equalsIgnoreCase("connectionName") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(t5." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and ";
					} else if (filterColumn.equalsIgnoreCase("templateId") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(t5." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and ";
					} else {
						if (!filterValue.isEmpty() && !filterColumn.isEmpty()) {
							if(!filterColumn.equalsIgnoreCase("datasource") && !filterColumn.equalsIgnoreCase("database")) {
								filterCondition = filterCondition + " LOWER(t1." + getFieldMappings(filterColumn) + ") LIKE '%"
									+ filterValue + "%' and ";
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filterCondition.length() > 0) {
			filterCondition = " and " + filterCondition.substring(0, filterCondition.lastIndexOf("and"));
		}
		LOG.debug("filter condition=" + filterCondition);
		return filterCondition;
	}

	private String getFieldMappings(String field) {
		switch (field) {
		case "source1":
			return "sourceName";
		case "testRun":
			return "test_run";
		case "aggreagteDQI":
			return "aggregateDQI";
		case "connectionName":
			return "schemaName";
		case "templateId":
			return "idData";
		case "fileName":
			return "folderName";
		default:
			return field;
		}
	}

	public String getProjectNameOfIdapp(Long idApp) {
		String projname = "";
		try {
			String sql = "select a.projectName  from project a , listApplications b"
					+ " where b.project_id = a.idProject and b.idApp = ?";
			projname = (String) jdbcTemplate.queryForObject(sql, new Object[] { idApp }, String.class);
		} catch (Exception e) {
			LOG.error("Failed to get project name : " + e.getMessage());
			e.printStackTrace();
		}
		return projname;
	}

	@Override
	public String getAppIdsListForDomainProject(int domainId, int projectId) {
		String idAppStr = "";
		String idAppStrDefaultValue = "-10";
		List<String> idApps = new ArrayList<String>();

		try {
			String query1 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
					+ " where domain_id =" + domainId + ") and project_id =" + projectId;
			System.out.println("listofvalidation " + query1);
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query1);
			while (oSqlRowSet.next()) {
				idApps.add(String.valueOf(oSqlRowSet.getLong("idApp")));
			}
			idAppStr = String.join(",", idApps);
			idAppStr = (idAppStr.length() < 1) ? idAppStrDefaultValue : idAppStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idAppStr;
	}

	@Override
	public Map<String, Object> getDataDriftResult(ValidationResultRequest resultRequest) {
		List<Map<String, Object>> dataDriftList = new ArrayList<>();
		LOG.debug("Into getDataDriftResult method");
		Map<String, Object> result = new HashMap<>();
		try {
			String filterCondition = "";

			if (resultRequest.getFilterCondtionMap() != null && !resultRequest.getFilterCondtionMap().isEmpty()) {
				Set<String> keySet = resultRequest.getFilterCondtionMap().keySet();

				for (String filterColumn : keySet) {
					String filterValue = "" + resultRequest.getFilterCondtionMap().get(filterColumn);
					if (filterColumn.equalsIgnoreCase("forgotRunEnabled") && !filterValue.isEmpty()) {
						filterCondition = filterCondition + " LOWER(forgot_run_enabled) LIKE '%" + filterValue
								+ "%' and ";
					} else {
						if (!filterValue.isEmpty())
							filterCondition = filterCondition + " LOWER(" + filterColumn + ") LIKE '%" + filterValue
									+ "%' and ";
					}
				}
				if (filterCondition.length() > 0) {
					filterCondition = " and " + filterCondition.substring(0, filterCondition.lastIndexOf("and"));
				}
				LOG.debug("Filter options are : " + filterCondition);
			}

			String sortOption = "";
			if (resultRequest.getSort() != null && !resultRequest.getSort().isEmpty()) {
				LOG.debug("Getting sort option for : " + resultRequest.getSort());
				String[] sorts = resultRequest.getSort().split("=");
				if (!sorts[0].isEmpty() && sorts[0].equalsIgnoreCase("forgotRunEnabled")) {
					sortOption = "forgotRunEnabled " + sorts[1];
				} else {
					if (!sorts[0].isEmpty())
						sortOption = sorts[0] + " " + sorts[1];
				}
				sortOption = " order by " + sortOption;
				LOG.debug("Sort options are : " + sortOption);
			}

			int count = 0;
			if (resultRequest.getCountRequired()) {
				count = getCountOfDrift(resultRequest, filterCondition);
				if (count > 0) {
					result.put("count", count);
				}
			}

			String resultQuery = "";

			if (resultRequest.getHistoricalData() && resultRequest.getColumnValueMap() != null) {
				resultQuery = "select * from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE + " where idApp = "
						+ resultRequest.getIdApp() + " and id not in (" + resultRequest.getColumnValueMap().getId()
						+ ") " + " and colName like " + ResultTableConstants.BINARY_KEY + " '"
						+ resultRequest.getColumnValueMap().getColName() + "' "
						+ getMicrosegmentValCondition(resultRequest.getColumnValueMap().getDGroupVal())
						+ " and Date between '" + resultRequest.getFromDate() + "' and '" + resultRequest.getToDate()
						+ "' limit " + resultRequest.getPageSize() + " offset "
						+ resultRequest.getPageNo() * resultRequest.getPageSize();
			} else {
				resultQuery = " select * from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE
						+ " where Id in (select max(Id) from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE
						+ " where idApp = " + resultRequest.getIdApp() + " and Date between '"
						+ resultRequest.getFromDate() + "' and '" + resultRequest.getToDate() + "' " + filterCondition
						+ " group by " + ResultTableConstants.IF_NULL_FUNCTION + "(dGroupVal,''), colName)" + sortOption
						+ "  limit " + resultRequest.getPageSize() + " offset "
						+ resultRequest.getPageNo() * resultRequest.getPageSize();
			}

			LOG.debug("Getting drift result for query : " + resultQuery);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(resultQuery);

			while (queryForRowSet.next()) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("idApp", queryForRowSet.getLong("idApp"));
				resultMap.put("id", queryForRowSet.getLong("Id"));
				resultMap.put("run", queryForRowSet.getInt("run"));
				resultMap.put("uniqueValuesCount", queryForRowSet.getInt("uniqueValuesCount"));
				resultMap.put("missingValueCount", queryForRowSet.getInt("missingValueCount"));
				resultMap.put("newValueCount", queryForRowSet.getInt("newValueCount"));
				resultMap.put("colName", queryForRowSet.getString("colName"));
				resultMap.put("forgotRunEnabled", queryForRowSet.getString("forgot_run_enabled"));
				resultMap.put("dGroupCol", queryForRowSet.getString("dGroupCol"));
				resultMap.put("dGroupVal", queryForRowSet.getString("dGroupVal"));
				resultMap.put("date", queryForRowSet.getString("Date"));
				dataDriftList.add(resultMap);
			}

			if (dataDriftList.isEmpty()) {
				LOG.debug("List is empty.");
			}

			LOG.debug("Got the total number of drift results : " + dataDriftList.size());
			result.put(ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE, dataDriftList);

		} catch (Exception ex) {
			LOG.error("Exception occured while fetching details from "
					+ ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE + " : " + ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	private int getCountOfDrift(ValidationResultRequest resultRequest, String filterCondition) {
		String sql = "";
		if (resultRequest.getHistoricalData() && resultRequest.getColumnValueMap() != null) {
			sql = "select count(*) as count from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE
					+ " where idApp = " + resultRequest.getIdApp() + " and id not in ("
					+ resultRequest.getColumnValueMap().getId() + ") " + " and colName like "
					+ ResultTableConstants.BINARY_KEY + " '" + resultRequest.getColumnValueMap().getColName() + "' "
					+ getMicrosegmentValCondition(resultRequest.getColumnValueMap().getDGroupVal())
					+ " and Date between '" + resultRequest.getFromDate() + "' and '" + resultRequest.getToDate()
					+ "' ";
		} else {
			sql = " select count(*) as count from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE
					+ " where Id in (select max(Id) from " + ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE
					+ " where idApp = " + resultRequest.getIdApp() + " and Date between '" + resultRequest.getFromDate()
					+ "' and '" + resultRequest.getToDate() + "' " + filterCondition + " group by "
					+ ResultTableConstants.IF_NULL_FUNCTION + "(dGroupVal,''), colName)";
		}
		return getCountOfQuery(sql);
	}

	private int getCountOfQuery(String sqlQuery) {

		try {
			LOG.debug("Getting count : " + sqlQuery);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sqlQuery);
			while (queryForRowSet.next()) {
				return queryForRowSet.getInt("count");
			}
		} catch (Exception ex) {
			LOG.error("Getting exception while getting count : " + ex.getMessage());
			ex.printStackTrace();
			return 0;
		}
		return 0;
	}

	private String getMicrosegmentValCondition(String microSegmentVal) {
		String microSegmentValCon = "";
		String binary_key = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "" : "BINARY";
		if (microSegmentVal != null && !microSegmentVal.equals("null")) {
			microSegmentValCon = " and dGroupVal like " + binary_key + " '" + microSegmentVal + "'";
		} else {
			microSegmentValCon = " and dGroupVal IS NULL";
		}
		return microSegmentValCon;
	}
	
	public static <String> Set<String> convertArrayToSet(String array[])
    {
        Set<String> set = new HashSet<>();
          for (String t : array) {          
            set.add(t);
        }
        return set;
    }

	/* Abhijeet[06-FEB-2023] Server side pagination validation view page */
	public Map<String, Object> getPaginatedValidationsJsonData(ValidationListRequest validationRequest)
			throws Exception {

		List<Map<String, Object>> aDataViewList = new ArrayList<>();
		String sDataSql, sOption1Sql, globalSearchOption;

		Map<String, Object> aRetValue = new HashMap<String, Object>();
		
		String menuFilter = "";
		if (validationRequest.getMenuFilter() != null) {
			List<String> strings=Arrays.asList(validationRequest.getMenuFilter());
			
			menuFilter = "  and t.appType in ("+(strings.isEmpty() ? "" : "'" + String.join("', '", strings) + "'" )+")" ;
		}
		
		

		String domainIdCondition = "";
		if (validationRequest.getDomainId() != null) {
			domainIdCondition = " and t.domain_id in (" + validationRequest.getDomainId() + ")";
		}

		LOG.debug("getPaginatedValidationsJsonData 01" + String.format("validationRequest = %1$s", validationRequest));

		try {
			sOption1Sql = String.format("and (t.CreatedOn between '%1$s' and '%2$s') \n",
					validationRequest.getFromDate(), validationRequest.getToDate());

			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				globalSearchOption = " and 1 = case when t.name like 'LIKE-TEXT' then 1  when ls.name like 'LIKE-TEXT' then 1 when t.approve_date::text like 'LIKE-TEXT' then 1   when t.appType like 'LIKE-TEXT' then 1  when t.active like 'LIKE-TEXT' then 1  when t.approve_status_name like 'LIKE-TEXT'  then 1 when t.createdByUser like 'LIKE-TEXT'  then 1 when t.CreatedOn::text like 'LIKE-TEXT'  then 1 when ap.element_text like 'LIKE-TEXT'  then 1 when p.projectName like 'LIKE-TEXT'  then 1 when AppMode like 'LIKE-TEXT'  then 1 else 0 end "
						.replaceAll("LIKE-TEXT", "%" + validationRequest.getGlobalSearchOption() + "%");
			} else {
				globalSearchOption = " and 1 = case when t.name like 'LIKE-TEXT' then 1  when ls.name like 'LIKE-TEXT' then 1 when t.approve_date like 'LIKE-TEXT' then 1   when t.appType like 'LIKE-TEXT' then 1  when t.active like 'LIKE-TEXT' then 1  when t.approve_status_name like 'LIKE-TEXT'  then 1 when t.createdByUser like 'LIKE-TEXT'  then 1 when t.CreatedOn like 'LIKE-TEXT'  then 1 when ap.element_text like 'LIKE-TEXT'  then 1 when p.projectName like 'LIKE-TEXT'  then 1 when AppMode like 'LIKE-TEXT'  then 1 else 0 end "
						.replaceAll("LIKE-TEXT", "%" + validationRequest.getGlobalSearchOption() + "%");
			}

			globalSearchOption = (validationRequest.getGlobalSearchOption() == null
					|| validationRequest.getGlobalSearchOption().isEmpty()) ? "" : globalSearchOption;

			Map<String, Object> filterObject = validationRequest.getFilterCondtionMap();
			String filterCondition = "";
			if (filterObject != null) {
				filterCondition = getValidationFilterCondition(filterObject);
				LOG.debug("Filter options are : " + filterCondition);
			}

			// Setting up default sort options date and idApp
			String sortOption = " t.idapp desc ";
			if (validationRequest.getSort() != null && !validationRequest.getSort().isEmpty()) {
				sortOption = getValidationsSortOption(validationRequest.getSort());
				LOG.debug("Sort options are : " + sortOption);
			}

			int count = 0;
			if (validationRequest.getCountRequired()) {
				count = getCountForValidation(validationRequest, domainIdCondition, globalSearchOption, filterCondition,
						sOption1Sql,menuFilter);
				if (count > 0) {
					aRetValue.put("count", count);
				}
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "select "
						+ "		t.idApp as ValidationId, t.name as ValidationCheckName, t.appType as AppType, t.idRightData as RightTemplateId,"
						+ " t.incrementalMatching as IncrementalMatching , t.data_domain_id as DataDomainId, t.matchingThreshold as MatchingThreshold,"
						+ "t.validityThreshold as ValidityThreshold,"
						+ " case when(t.incrementalMatching  = 'Y') then (case when (t.buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode, "
						+ " t.createdByUser as CreatedBy, t.CreatedOn, ls.name as  DataTemplateName, ls.idData as DataTemplateId,ls.advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "ls.profilingEnabled as ProfilingEnabled, t.approve_status_name as ApprovalStatus,t.approve_date as ApprovedOn, t.approver_name as ApprovedBy,t.active as Status, "
						+ "case when ap.element_text is null  then 'Not Started' else ap.element_text end as StagingStatus, t.data_domain_id as DataDomainId,t.project_id as ProjectId, "
						+ "p.projectName as ProjectName,t.description as Description,d.domainName As DomainName, t.validation_job_size,ls.dataLocation, ls.databaseName, case when ls.folderName = 'Query' then ls.name else ls.folderName end as tableName "
						+ " from ( "
						+ "		select listApplications.*, to_date(listApplications.createdAt::text, 'YYYY-MM-DD') as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name, "
						+ "     case when(incrementalMatching  = 'Y') then (case when (buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join (select listDataSources.*,COALESCE(NULLIF(listDataAccess.schemaName, ''), listDataSources.dataLocation) as databaseName  ,listDataAccess.folderName"
						+ " from listDataSources left join listDataAccess on listDataSources.idData = listDataAccess.idData) as ls on ls.idData = t.idData "
						+ "join project p on t.project_id = p.idProject JOIN domain d ON t.domain_id = d.domainId "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", validationRequest.getProjectId())
						+ domainIdCondition + sOption1Sql + globalSearchOption + filterCondition + menuFilter+" order by "
						+ sortOption + " limit " + validationRequest.getPageSize() + " offset "
						+ validationRequest.getPageNo() * validationRequest.getPageSize();

				LOG.debug("Getting rule catlog validation result for postgres query : " + sDataSql);

			} else {
				sDataSql = "" + "select "
						+ "		t.idApp as ValidationId, t.name as ValidationCheckName, t.appType as AppType, t.idRightData as RightTemplateId,"
						+ " t.incrementalMatching as IncrementalMatching , t.data_domain_id as DataDomainId, t.matchingThreshold as MatchingThreshold,"
						+ "t.validityThreshold as ValidityThreshold, "
						+ " case when(t.incrementalMatching  = 'Y') then (case when (t.buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode, "
						+ " t.createdByUser as CreatedBy, t.CreatedOn, ls.name as  DataTemplateName, ls.idData as DataTemplateId,ls.advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "ls.profilingEnabled as ProfilingEnabled, t.approve_status_name as ApprovalStatus,t.approve_date as ApprovedOn, t.approver_name as ApprovedBy,t.active as Status, "
						+ "case when ap.element_text is null  then 'Not Started' else ap.element_text end as StagingStatus, t.data_domain_id as DataDomainId,t.project_id as ProjectId, "
						+ "p.projectName as ProjectName,t.description as Description,d.domainName As DomainName ,t.validation_job_size,ls.dataLocation, ls.databaseName, case when ls.folderName = 'Query' then ls.name else ls.folderName end as tableName"
						+ " from ( "
						+ "		select listApplications.*, cast(date_format(listApplications.createdAt, '%y-%m-%d') as date) as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name ,"
						+ "     case when(incrementalMatching  = 'Y') then (case when (buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join (select listDataSources.*,COALESCE(NULLIF(listDataAccess.schemaName, ''), listDataSources.dataLocation) as databaseName  ,listDataAccess.folderName"
						+ " from listDataSources left join"
						+ " listDataAccess on listDataSources.idData = listDataAccess.idData) as ls on ls.idData = t.idData join project p on t.project_id = p.idProject JOIN domain d ON t.domain_id = d.domainId "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", validationRequest.getProjectId())
						+ domainIdCondition + " " + sOption1Sql + " " + globalSearchOption + filterCondition+menuFilter
						+ " order by " + sortOption + " limit " + validationRequest.getPageSize() + " offset "
						+ validationRequest.getPageNo() * validationRequest.getPageSize();

				LOG.debug("Getting rule catlog validation result for query : " + sDataSql);
			}

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sDataSql);

			while (queryForRowSet.next()) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("Status", queryForRowSet.getString("Status"));
				resultMap.put("AdvancedRulesEnabled", queryForRowSet.getString("AdvancedRulesEnabled"));
				resultMap.put("CreatedBy", queryForRowSet.getString("CreatedBy"));
				resultMap.put("MatchingThreshold", queryForRowSet.getString("MatchingThreshold"));
				resultMap.put("ProjectName", queryForRowSet.getString("ProjectName"));
				resultMap.put("AppMode", queryForRowSet.getString("AppMode"));
				resultMap.put("IncrementalMatching", queryForRowSet.getString("IncrementalMatching"));
				resultMap.put("DataTemplateName", queryForRowSet.getString("DataTemplateName"));
				resultMap.put("ValidityThreshold", queryForRowSet.getString("ValidityThreshold"));
				resultMap.put("ApprovalStatus", queryForRowSet.getString("ApprovalStatus"));
				resultMap.put("ApprovedOn", queryForRowSet.getString("ApprovedOn"));
				resultMap.put("DataDomainId", queryForRowSet.getInt("DataDomainId"));
				resultMap.put("ProfilingEnabled", queryForRowSet.getString("ProfilingEnabled"));
				resultMap.put("ValidationId", queryForRowSet.getInt("ValidationId"));
				resultMap.put("ApprovedBy", queryForRowSet.getString("ApprovedBy"));
				resultMap.put("StagingStatus", queryForRowSet.getString("StagingStatus"));
				resultMap.put("CreatedOn", queryForRowSet.getString("CreatedOn"));
				resultMap.put("AppType", queryForRowSet.getString("AppType"));
				resultMap.put("DataTemplateId", queryForRowSet.getInt("DataTemplateId"));
				resultMap.put("RightTemplateId", queryForRowSet.getInt("RightTemplateId"));
				resultMap.put("ValidationCheckName", queryForRowSet.getString("ValidationCheckName"));
				resultMap.put("DomainName", queryForRowSet.getString("DomainName"));
				resultMap.put("validationJobSize", queryForRowSet.getString("validation_job_size"));
				resultMap.put("dataLocation", queryForRowSet.getString("dataLocation"));
				resultMap.put("databaseName", queryForRowSet.getString("databaseName"));
				resultMap.put("tableName", queryForRowSet.getString("tableName"));

				aDataViewList.add(resultMap);

			}

			if (aDataViewList.isEmpty()) {
				LOG.debug("List is empty.");
			}

			LOG.debug("Got the total number of validation list : " + aDataViewList.size());
			aRetValue.put("validations", aDataViewList);

		} catch (Exception ex) {
			LOG.error("Exception occured while fetching validation list " + " : " + ex.getMessage());
			ex.printStackTrace();
		}
		return aRetValue;
	}

	private String getValidationFilterCondition(Map<String, Object> filterObject) {
		String filterCondition = "";
		try {
			Set<String> keySet = filterObject.keySet();
			for (String filterColumn : keySet) {

				String filterValue = "" + filterObject.get(filterColumn);
				// Adding filter attribute according to the table

				if (filterColumn.equalsIgnoreCase("DataTemplateName") && !filterValue.isEmpty()) {
					filterCondition = filterCondition + " LOWER(ls." + getValidationFieldMappings(filterColumn)
							+ ") LIKE '%" + filterValue + "%' and ";
				} else if (filterColumn.equalsIgnoreCase("StagingStatus") && !filterValue.isEmpty()) {
					filterCondition = filterCondition + " LOWER(ap." + getValidationFieldMappings(filterColumn)
							+ ") LIKE '%" + filterValue + "%' and ";
				} else if (filterColumn.equalsIgnoreCase("AppMode") && !filterValue.isEmpty()) {
					filterCondition = filterCondition + " LOWER(" + getValidationFieldMappings(filterColumn)
							+ ") LIKE '%" + filterValue + "%' and ";
				}

				else if (filterColumn.equalsIgnoreCase("ProjectName") && !filterValue.isEmpty()) {
					filterCondition = filterCondition + " LOWER(p." + getValidationFieldMappings(filterColumn)
							+ ") LIKE '%" + filterValue + "%' and ";
				} else if (filterColumn.equalsIgnoreCase("ValidationId") && !filterValue.isEmpty()) {
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						filterCondition = filterCondition + " LOWER(t." + getValidationFieldMappings(filterColumn)
								+ "::TEXT) LIKE '%" + filterValue + "%' and ";
					} else {
						filterCondition = filterCondition + " LOWER(t." + getValidationFieldMappings(filterColumn)
								+ ") LIKE '%" + filterValue + "%' and ";
					}
				}

				else {
					if (!filterValue.isEmpty() && !filterValue.isEmpty())
						filterCondition = filterCondition + " LOWER(t." + getValidationFieldMappings(filterColumn)
								+ ") LIKE '%" + filterValue + "%' and ";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filterCondition.length() > 0) {
			filterCondition = " and " + filterCondition.substring(0, filterCondition.lastIndexOf("and"));
		}
		LOG.debug("filter condition=" + filterCondition);
		return filterCondition;
	}

	private String getValidationFieldMappings(String field) {
		switch (field) {
		case "ValidationId":
			return "idApp";
		case "ValidationCheckName":
			return "name";
		case "AppType":
			return "appType";
		case "ApprovalStatus":
			return "approve_status_name";
		case "Status":
			return "active";
		case "DataTemplateName":
			return "name";
		case "ProjectName":
			return "projectName";
		case "StagingStatus":
			return "element_text";
		case "AppMode":
			return "AppMode";
		case "CreatedOn":
			return "CreatedOn";
		case "CreatedBy":
			return "createdByUser";
		case "ApprovedOn":
			return "approve_date";

		default:
			return field;
		}
	}

	// Method provides column name mappings according to tables
	private String getValidationsSortOption(String sort) {
		try {
			LOG.debug("Getting sort option for : " + sort);
			String[] sorts = sort.split("=");
			String field = getValidationFieldMappings(sorts[0]);
			;
			if (field.equalsIgnoreCase("name"))
				field = "ls." + field;
			else if (field.equalsIgnoreCase("element_text"))
				field = "ap." + field;
			else if (field.equalsIgnoreCase("AppMode"))
				field = "" + field;
			else if (field.equalsIgnoreCase("projectName"))
				field = "p." + field;
			else
				field = "t." + field;

			return " " + field + " " + sorts[1] + " ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private int getCountForValidation(ValidationListRequest validationRequest, String domainIdCondition,
			String globalSearchOption, String filterCondition, String sOption1Sql,String menuFilter) {

		try {
			String sDataSql = "";
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "" + "select " + "		count(*) as count  " + "from ( "
						+ "		select listApplications.*, to_date(listApplications.createdAt::text, 'YYYY-MM-DD') as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name, "
						+ "     case when(incrementalMatching  = 'Y') then (case when (buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join listDataSources ls on ls.idData = t.idData join project p on t.project_id = p.idProject "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", validationRequest.getProjectId())
						+ domainIdCondition + sOption1Sql + globalSearchOption + filterCondition+menuFilter;

				LOG.debug("Getting validation count for query : " + sDataSql);

			} else {
				sDataSql = "" + "select count(*) as count " + "from ( "
						+ "		select listApplications.*, cast(date_format(listApplications.createdAt, '%y-%m-%d') as date) as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name, "
						+ "     case when(incrementalMatching  = 'Y') then (case when (buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join listDataSources ls on ls.idData = t.idData join project p on t.project_id = p.idProject "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", validationRequest.getProjectId())
						+ domainIdCondition + sOption1Sql + globalSearchOption + filterCondition+menuFilter;

				LOG.debug("Getting validation count for query : " + sDataSql);
			}

			LOG.debug("Getting count : " + sDataSql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sDataSql);

			while (queryForRowSet.next()) {
				return queryForRowSet.getInt("count");
			}
		} catch (Exception ex) {
			LOG.error("Getting exception while getting validation count : " + ex.getMessage());
			ex.printStackTrace();
			return 0;
		}
		return 0;
	}

	@Override
	public Map<String, Object> getDistributionResult(ValidationResultRequest resultRequest) {

		List<Map<String, Object>> distributionResultList = new ArrayList<>();
		LOG.debug("Into getDataDriftResult method");
		Map<String, Object> result = new HashMap<>();
		try {
			String filterCondition = "";

//	    if (resultRequest.getFilterCondtionMap() != null && !resultRequest.getFilterCondtionMap().isEmpty()) {
//		Set<String> keySet = resultRequest.getFilterCondtionMap().keySet();
//
//		for (String filterColumn : keySet) {
//		    String filterValue = "" + resultRequest.getFilterCondtionMap().get(filterColumn);
//		    if (filterColumn.equalsIgnoreCase("forgotRunEnabled") && !filterValue.isEmpty()) {
//			filterCondition = filterCondition + " LOWER(forgot_run_enabled) LIKE '%" + filterValue
//				+ "%' and ";
//		    } else {
//			if (!filterValue.isEmpty())
//			    filterCondition = filterCondition + " LOWER(" + filterColumn + ") LIKE '%" + filterValue
//				    + "%' and ";
//		    }
//		}
//		if (filterCondition.length() > 0) {
//		    filterCondition = " and " + filterCondition.substring(0, filterCondition.lastIndexOf("and"));
//		}
//		LOG.debug("Filter options are : " + filterCondition);
//	    }

			String sortOption = "";
//	    if (resultRequest.getSort() != null && !resultRequest.getSort().isEmpty()) {
//		LOG.debug("Getting sort option for : " + resultRequest.getSort());
//		String[] sorts = resultRequest.getSort().split("=");
//		if (!sorts[0].isEmpty() && sorts[0].equalsIgnoreCase("forgotRunEnabled")) {
//		    sortOption = "forgotRunEnabled " + sorts[1];
//		} else {
//		    if (!sorts[0].isEmpty())
//			sortOption = sorts[0] + " " + sorts[1];
//		}
//		sortOption = " order by " + sortOption;
//		LOG.debug("Sort options are : " + sortOption);
//	    }

			int count = 0;
			if (resultRequest.getCountRequired()) {
				count = getCountOfDistribution(resultRequest, filterCondition);
				if (count > 0) {
					result.put("count", count);
				}
			}

			String resultQuery = "";

			if (resultRequest.getHistoricalData() && resultRequest.getColumnValueMap() != null) {
				resultQuery = "select *,  CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ " ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from "
						+ ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY + " where idApp = " + resultRequest.getIdApp()
						+ " and id not in (" + resultRequest.getColumnValueMap().getId() + ") " + " and colName like "
						+ ResultTableConstants.BINARY_KEY + " '" + resultRequest.getColumnValueMap().getColName() + "' "
						+ getMicrosegmentValCondition(resultRequest.getColumnValueMap().getDGroupVal())
						+ " and Date between '" + resultRequest.getFromDate() + "' and '" + resultRequest.getToDate();
			} else {
				resultQuery = "select *,  CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ " ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi   from "
						+ ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY + " where Id in(select Max(Id) from  "
						+ ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY + " where idApp=" + resultRequest.getIdApp()
						+ " and Date between '" + resultRequest.getFromDate() + "' and '" + resultRequest.getToDate()
						+ "'  group by ColName," + ResultTableConstants.IF_NULL_FUNCTION + " (dGroupVal, '')) "
						+ sortOption + "  limit " + resultRequest.getPageSize() + " offset "
						+ resultRequest.getPageNo() * resultRequest.getPageSize();
			}

			LOG.debug("Getting drift result for query : " + resultQuery);
			distributionResultList = getValidationResultsByCheckDetails(resultQuery);

			if (distributionResultList.isEmpty()) {
				LOG.debug("List is empty.");
			}

			LOG.debug("Got the total number of distribution results : " + distributionResultList.size());
			result.put(ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY, distributionResultList);

		} catch (Exception ex) {
			LOG.error("Exception occured while fetching details from " + ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY
					+ " : " + ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	private int getCountOfDistribution(ValidationResultRequest resultRequest, String filterCondition) {
		String sql = " select count(*) as count from " + ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY
				+ " where Id in (select max(Id) from " + ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY
				+ " where idApp = " + resultRequest.getIdApp() + " and Date between '" + resultRequest.getFromDate()
				+ "' and '" + resultRequest.getToDate() + "' " + filterCondition + " group by "
				+ ResultTableConstants.IF_NULL_FUNCTION + "(dGroupVal,''), colName)";
		return getCountOfQuery(sql);
	}

	private List<Map<String, Object>> getValidationResultsByCheckDetails(String sql) {
		List<Map<String, Object>> resultArr = new ArrayList<Map<String, Object>>();
		try {
			LOG.debug("<<==sqlQuery==>>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();

			while (queryForRowSet.next()) {
				Map<String, Object> resultObj = new HashMap<String, Object>();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					// camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null) {
						resultObj.put(camCaseCol, camCaseCol.equals("dGroupVal") ? "null" : "");
					} else {
						resultObj.put(camCaseCol, object);
					}
				}
				if (resultObj != null) {
					if (resultObj.containsKey("dGroupRcStatus") && resultObj.containsKey("action")) {
						String dGroupRcStatus = String.valueOf(resultObj.get("dGroupRcStatus"));
						String action = String.valueOf(resultObj.get("action"));
						if (dGroupRcStatus.trim().equalsIgnoreCase("review")
								&& action.trim().equalsIgnoreCase("accepted")) {
							resultObj.put("dGroupRcStatus", "reviewed");
							resultArr.add(resultObj);
						}
					}
					resultArr.add(resultObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQNullCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			System.out.println("\n====>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("recordCount", queryForRowSet.getObject("Record_Count") == null ? ""
						: queryForRowSet.getObject("Record_Count"));
				resultObj.put("historicNullMean", queryForRowSet.getObject("Historic_Null_Mean") == null ? ""
						: queryForRowSet.getObject("Historic_Null_Mean"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("nullThreshold", queryForRowSet.getObject("Null_Threshold") == null ? ""
						: queryForRowSet.getObject("Null_Threshold"));
				resultObj.put("nullValue",
						queryForRowSet.getObject("Null_Value") == null ? "" : queryForRowSet.getObject("Null_Value"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("historicNullStatus", queryForRowSet.getObject("Historic_Null_Status") == null ? ""
						: queryForRowSet.getObject("Historic_Null_Status"));
				resultObj.put("historicNullStddev", queryForRowSet.getObject("Historic_Null_stddev") == null ? ""
						: queryForRowSet.getObject("Historic_Null_stddev"));
				resultObj.put("nullPercentage", queryForRowSet.getObject("Null_Percentage") == null ? ""
						: queryForRowSet.getObject("Null_Percentage"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQMicroNullCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			System.out.println("\n====>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("recordCount", queryForRowSet.getObject("Record_Count") == null ? ""
						: queryForRowSet.getObject("Record_Count"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("nullThreshold", queryForRowSet.getObject("Null_Threshold") == null ? ""
						: queryForRowSet.getObject("Null_Threshold"));
				resultObj.put("nullValue",
						queryForRowSet.getObject("Null_Value") == null ? "" : queryForRowSet.getObject("Null_Value"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("nullPercentage", queryForRowSet.getObject("Null_Percentage") == null ? ""
						: queryForRowSet.getObject("Null_Percentage"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public Map<String, String> getMaxRunAndDateByQuery(String fromDate, String toDate, long idApp) {
		Map<String, String> dateRunMap = new HashMap<>();
		String maxDate = "";
		String maxRun = "";

		try {
			String sql = "SELECT MAX(Date) as Date , MAX(Run) as Run from DATA_QUALITY_Transactionset_sum_A1 where Date= (SELECT MAX(Date) as Date from DATA_QUALITY_Transactionset_sum_A1 where Date between '"
					+ fromDate + "' and '" + toDate + "' and idApp=" + idApp + " limit 1) and idApp=" + idApp;

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			System.out.println("\n====>sql:" + sql);

			if (queryForRowSet.next()) {
				maxDate = queryForRowSet.getString("Date");
				maxRun = queryForRowSet.getString("Run");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dateRunMap.put("maxDate", maxDate);
		dateRunMap.put("maxRun", maxRun);
		return dateRunMap;
	}

	@Override
	public JSONArray getDQLengthCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			System.out.println("\n====>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("failedRecordsPercentage",
						queryForRowSet.getObject("FailedRecords_Percentage") == null ? ""
								: queryForRowSet.getObject("FailedRecords_Percentage"));
				resultObj.put("lengthThreshold", queryForRowSet.getObject("Length_Threshold") == null ? ""
						: queryForRowSet.getObject("Length_Threshold"));
				resultObj.put("recordCount",
						queryForRowSet.getObject("RecordCount") == null ? "" : queryForRowSet.getObject("RecordCount"));
				resultObj.put("length",
						queryForRowSet.getObject("Length") == null ? "" : queryForRowSet.getObject("Length"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("totalFailedRecords", queryForRowSet.getObject("TotalFailedRecords") == null ? ""
						: queryForRowSet.getObject("TotalFailedRecords"));
				resultObj.put("maxLengthCheckEnabled", queryForRowSet.getObject("max_length_check_enabled") == null ? ""
						: queryForRowSet.getObject("max_length_check_enabled"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getAutoDiscoverdPatternResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			System.out.println("\n====>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("Col_Name") == null ? "" : queryForRowSet.getObject("Col_Name"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("totalRecords", queryForRowSet.getObject("Total_Records") == null ? ""
						: queryForRowSet.getObject("Total_Records"));
				resultObj.put("totalFailedRecords", queryForRowSet.getObject("Total_Failed_Records") == null ? ""
						: queryForRowSet.getObject("Total_Failed_Records"));
				resultObj.put("totalMatchedRecords", queryForRowSet.getObject("Total_Matched_Records") == null ? ""
						: queryForRowSet.getObject("Total_Matched_Records"));
				resultObj.put("patternsList", queryForRowSet.getObject("Patterns_List") == null ? ""
						: queryForRowSet.getObject("Patterns_List"));
				resultObj.put("newPattern",
						queryForRowSet.getObject("New_Pattern") == null ? "" : queryForRowSet.getObject("New_Pattern"));
				resultObj.put("failedRecordsPercentage",
						queryForRowSet.getObject("FailedRecords_Percentage") == null ? ""
								: queryForRowSet.getObject("FailedRecords_Percentage"));
				resultObj.put("patternThreshold", queryForRowSet.getObject("Pattern_Threshold") == null ? ""
						: queryForRowSet.getObject("Pattern_Threshold"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultObj.put("csvFilePath", queryForRowSet.getObject("Csv_File_Path") == null ? ""
						: queryForRowSet.getObject("Csv_File_Path"));
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getUserDefinedPatternResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("Result Query for user defined pattern result" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("pattern_list",
						queryForRowSet.getObject("idApp") == null || queryForRowSet.getObject("Col_Name") == null ? "" : getPatternForIdApp(queryForRowSet.getLong("idApp"),queryForRowSet.getString("Col_Name")));
				resultObj.put("colName",
						queryForRowSet.getObject("Col_Name") == null ? "" : queryForRowSet.getObject("Col_Name"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("totalRecords", queryForRowSet.getObject("Total_Records") == null ? ""
						: queryForRowSet.getObject("Total_Records"));
				resultObj.put("totalFailedRecords", queryForRowSet.getObject("Total_Failed_Records") == null ? ""
						: queryForRowSet.getObject("Total_Failed_Records"));
				resultObj.put("failedRecordsPercentage",
						queryForRowSet.getObject("FailedRecords_Percentage") == null ? ""
								: queryForRowSet.getObject("FailedRecords_Percentage"));
				resultObj.put("patternThreshold", queryForRowSet.getObject("Pattern_Threshold") == null ? ""
						: queryForRowSet.getObject("Pattern_Threshold"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	public JSONArray getDQBadDataCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("totalBadRecord", queryForRowSet.getObject("TotalBadRecord") == null ? ""
						: queryForRowSet.getObject("TotalBadRecord"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("run", queryForRowSet.getObject("run") == null ? "" : queryForRowSet.getObject("run"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("totalRecord",
						queryForRowSet.getObject("TotalRecord") == null ? "" : queryForRowSet.getObject("TotalRecord"));
				resultObj.put("badDataPercentage", queryForRowSet.getObject("badDataPercentage") == null ? ""
						: queryForRowSet.getObject("badDataPercentage"));
				resultObj.put("badDataThreshold", queryForRowSet.getObject("badDataThreshold") == null ? ""
						: queryForRowSet.getObject("badDataThreshold"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}
	
	
	public String getPatternForIdApp(long idApp,String colname) {
		String pattern="";
		try {
			 pattern = jdbcTemplate.queryForObject("select patterns from listDataDefinition "
					+ " where idData=(select idData from listApplications where idApp=" + idApp+") "
							+ " and displayname='"+colname+"'",
					String.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return pattern;
	}

	public JSONArray getDQDefaultValueCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("defaultValue", queryForRowSet.getObject("Default_Value") == null ? ""
						: queryForRowSet.getObject("Default_Value"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("defaultPercentage", queryForRowSet.getObject("Default_Percentage") == null ? ""
						: queryForRowSet.getObject("Default_Percentage"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("defaultCount", queryForRowSet.getObject("Default_Count") == null ? ""
						: queryForRowSet.getObject("Default_Count"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDataDriftCountSummaryResults(String sql, Long idApp) {
		JSONArray resultArr = new JSONArray();
		double threshold = 0d;
		try {
			LOG.debug("SQL" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Map<String, Double> threshold_values = ruleCatalogDao.getRuleCatalogThresholdValues(idApp, DatabuckConstants.RULE_TYPE_DATADRIFTCHECK);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("colName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("newValueCount", queryForRowSet.getObject("newValueCount") == null ? ""
						: queryForRowSet.getObject("newValueCount"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("uniqueValuesCount", queryForRowSet.getObject("uniqueValuesCount") == null ? ""
						: queryForRowSet.getObject("uniqueValuesCount"));
				resultObj.put("missingValueCount", queryForRowSet.getObject("missingValueCount") == null ? ""
						: queryForRowSet.getObject("missingValueCount"));

				if (threshold_values.containsKey(queryForRowSet.getString("colName")))
					threshold = threshold_values.get(queryForRowSet.getString("colName"));

				resultObj.put("threshold", threshold == 0 ? 0.00 : threshold);
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDataDriftSummaryResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("colName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("operation",
						queryForRowSet.getObject("Operation") == null ? "" : queryForRowSet.getObject("Operation"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("uniqueValues", queryForRowSet.getObject("uniqueValues") == null ? ""
						: queryForRowSet.getObject("uniqueValues"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultObj.put("time", queryForRowSet.getObject("Time") == null ? ""
						: queryForRowSet.getObject("Time"));
				resultObj.put("userName",
						queryForRowSet.getObject("userName") == null ? "" : queryForRowSet.getObject("userName"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDuplicateCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL==> " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("duplicateCheckFields", queryForRowSet.getObject("duplicateCheckFields") == null ? ""
						: queryForRowSet.getObject("duplicateCheckFields"));
				resultObj.put("duplicateCheckValues", queryForRowSet.getObject("duplicateCheckValues") == null ? ""
						: queryForRowSet.getObject("duplicateCheckValues"));
				resultObj.put("dupcount",
						queryForRowSet.getObject("dupcount") == null ? "" : queryForRowSet.getObject("dupcount"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDuplicateCheckSummaryResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL : " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			String type = "";

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("duplicateCheckFields", queryForRowSet.getObject("duplicateCheckFields") == null ? ""
						: queryForRowSet.getObject("duplicateCheckFields"));
				resultObj.put("percentage",
						queryForRowSet.getObject("Percentage") == null ? "" : queryForRowSet.getObject("Percentage"));
				resultObj.put("duplicate",
						queryForRowSet.getObject("Duplicate") == null ? "" : queryForRowSet.getObject("Duplicate"));
				resultObj.put("totalCount",
						queryForRowSet.getObject("TotalCount") == null ? "" : queryForRowSet.getObject("TotalCount"));
				resultObj.put("threshold",
						queryForRowSet.getObject("Threshold") == null ? "" : queryForRowSet.getObject("Threshold"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				type = String.valueOf(queryForRowSet.getObject("Type") == null ? "" : queryForRowSet.getObject("Type"));
				if (type != null && !type.trim().isEmpty()) {
					if (type.equalsIgnoreCase("all")) {
						type = "Individual";
					} else if (type.equalsIgnoreCase("identity")) {
						type = "Composite";
					}
				}
				resultObj.put("type", type);
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getGlobalRuleResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL: " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("ruleName",
						queryForRowSet.getObject("ruleName") == null ? "" : queryForRowSet.getObject("ruleName"));
				resultObj.put("totalFailed",
						queryForRowSet.getObject("totalFailed") == null ? "" : queryForRowSet.getObject("totalFailed"));
				resultObj.put("totalRecords", queryForRowSet.getObject("totalRecords") == null ? ""
						: queryForRowSet.getObject("totalRecords"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("rulePercentage", queryForRowSet.getObject("rulePercentage") == null ? ""
						: queryForRowSet.getObject("rulePercentage"));
				resultObj.put("status",
						queryForRowSet.getObject("status") == null ? "" : queryForRowSet.getObject("status"));
				resultObj.put("dimensionName", queryForRowSet.getObject("dimension_name") == null ? ""
						: queryForRowSet.getObject("dimension_name"));
				resultObj.put("ruleThreshold", queryForRowSet.getObject("ruleThreshold") == null ? ""
						: queryForRowSet.getObject("ruleThreshold"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getTimeSequenceCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL :" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("sDate",
						queryForRowSet.getObject("SDate") == null ? "" : queryForRowSet.getObject("SDate"));
				resultObj.put("eDate",
						queryForRowSet.getObject("EDate") == null ? "" : queryForRowSet.getObject("EDate"));
				resultObj.put("timelinessKey", queryForRowSet.getObject("TimelinessKey") == null ? ""
						: queryForRowSet.getString("TimelinessKey").split(" ")[0]);
				resultObj.put("noOfDays",
						queryForRowSet.getObject("No_Of_Days") == null ? "" : queryForRowSet.getObject("No_Of_Days"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultObj.put("totalCount",
						queryForRowSet.getObject("TotalCount") == null ? "" : queryForRowSet.getObject("TotalCount"));
				resultObj.put("totalFailedCount", queryForRowSet.getObject("TotalFailedCount") == null ? ""
						: queryForRowSet.getObject("TotalFailedCount"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQRecordAnomalyResults(String sql) {
		JSONArray resultArr = new JSONArray();
		DecimalFormat dc = new DecimalFormat("0.00");
		try {
			LOG.debug("SQL : " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("colVal",
						queryForRowSet.getObject("ColVal") == null ? "" : queryForRowSet.getObject("ColVal"));
				resultObj.put("mean", queryForRowSet.getObject("mean") == null ? ""
						: Double.valueOf(dc.format(queryForRowSet.getObject("mean"))));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("stddev", queryForRowSet.getObject("stddev") == null ? ""
						: Double.valueOf(dc.format(queryForRowSet.getObject("stddev"))));
				resultObj.put("status",
						queryForRowSet.getObject("status") == null ? "" : queryForRowSet.getObject("status"));
				resultObj.put("raDeviation", queryForRowSet.getObject("ra_Deviation") == null ? ""
						: Double.valueOf(dc.format(queryForRowSet.getObject("ra_Deviation"))));
				resultObj.put("raDqi",
						queryForRowSet.getObject("RA_Dqi") == null ? "" : queryForRowSet.getObject("RA_Dqi"));
				resultObj.put("threshold",
						queryForRowSet.getObject("threshold") == null ? "" : queryForRowSet.getObject("threshold"));
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage() + " " + e.getCause());
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDistributionCheckResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("Sql : " + sql);
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("colName",
						queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("recordCount", queryForRowSet.getObject("Record_Count") == null ? ""
						: queryForRowSet.getObject("Record_Count"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("status",
						queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultObj.put("numSDDeviation", queryForRowSet.getObject("NumSDDeviation") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSDDeviation")));
				resultObj.put("min", queryForRowSet.getObject("Min") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("Min")));
				resultObj.put("stdDev", queryForRowSet.getObject("Std_Dev") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("Std_Dev")));
				resultObj.put("numMeanAvg", queryForRowSet.getObject("NumMeanAvg") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumMeanAvg")));
				resultObj.put("numMeanDeviation", queryForRowSet.getObject("NumMeanDeviation") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumMeanDeviation")));
				resultObj.put("numSDAvg", queryForRowSet.getObject("NumSDAvg") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSDAvg")));
				resultObj.put("numSumStdDev", queryForRowSet.getObject("NumSumStdDev") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSumStdDev")));
				resultObj.put("numMeanStatus", queryForRowSet.getObject("NumMeanStatus") == null ? ""
						: queryForRowSet.getObject("NumMeanStatus"));
				resultObj.put("numSumStatus", queryForRowSet.getObject("numSumStatus") == null ? ""
						: queryForRowSet.getObject("numSumStatus"));
				resultObj.put("max", queryForRowSet.getObject("Max") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("Max")));
				resultObj.put("numDqi",
						queryForRowSet.getObject("NumDqi") == null ? "" : queryForRowSet.getObject("NumDqi"));
				resultObj.put("numMeanStdDev", queryForRowSet.getObject("NumMeanStdDev") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumMeanStdDev")));
				resultObj.put("numMeanThreshold", queryForRowSet.getObject("NumMeanThreshold") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumMeanThreshold")));
				resultObj.put("mean", queryForRowSet.getObject("Mean") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("Mean")));
				resultObj.put("count",
						queryForRowSet.getObject("Count") == null ? "" : queryForRowSet.getObject("Count"));
				resultObj.put("numSDStatus",
						queryForRowSet.getObject("NumSDStatus") == null ? "" : queryForRowSet.getObject("NumSDStatus"));
				resultObj.put("numSDStdDev", queryForRowSet.getObject("NumSDStdDev") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSDStdDev")));
				resultObj.put("numSumAvg", queryForRowSet.getObject("NumSumAvg") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSumAvg")));
				resultObj.put("numSDThreshold", queryForRowSet.getObject("NumSDThreshold") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSDThreshold")));
				resultObj.put("numSumThreshold", queryForRowSet.getObject("NumSumThreshold") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("NumSumThreshold")));
				resultObj.put("sumOfNumStat", queryForRowSet.getObject("sumOfNumStat") == null ? ""
						: numberFormat.format(queryForRowSet.getObject("sumOfNumStat")));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDateConsistencyResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL :  " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dateField",
						queryForRowSet.getObject("DateField") == null ? "" : queryForRowSet.getObject("DateField"));
				resultObj.put("totalFailedRecords", queryForRowSet.getObject("TotalFailedRecords") == null ? ""
						: queryForRowSet.getObject("TotalFailedRecords"));
				resultObj.put("totalNumberOfRecords", queryForRowSet.getObject("TotalNumberOfRecords") == null ? ""
						: queryForRowSet.getObject("TotalNumberOfRecords"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQDateConsistencyFailedResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL : " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dateFieldCols", queryForRowSet.getObject("DateFieldCols") == null ? ""
						: queryForRowSet.getObject("DateFieldCols"));
				resultObj.put("dateFieldValues", queryForRowSet.getObject("DateFieldValues") == null ? ""
						: queryForRowSet.getString("DateFieldValues").split(" ")[0]);
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "null" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("failureReason", queryForRowSet.getObject("FailureReason") == null ? ""
						: queryForRowSet.getObject("FailureReason"));
				resultObj.put("forgot_run_enabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQRecordCountAnomalyResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL : " + sql);
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dayOfYear",
						queryForRowSet.getObject("dayOfYear") == null ? "" : queryForRowSet.getObject("dayOfYear"));
				resultObj.put("month",
						queryForRowSet.getObject("month") == null ? "" : queryForRowSet.getObject("month"));
				resultObj.put("dayOfMonth",
						queryForRowSet.getObject("dayOfMonth") == null ? "" : queryForRowSet.getObject("dayOfMonth"));
				resultObj.put("dayOfWeek",
						queryForRowSet.getObject("dayOfWeek") == null ? "" : queryForRowSet.getObject("dayOfWeek"));
				resultObj.put("hourOfDay",
						queryForRowSet.getObject("hourOfDay") == null ? "" : queryForRowSet.getObject("hourOfDay"));
				resultObj.put("recordCount",
						queryForRowSet.getObject("RecordCount") == null ? "" : queryForRowSet.getObject("RecordCount"));
				resultObj.put("fileNameValidationStatus",
						queryForRowSet.getObject("fileNameValidationStatus") == null ? ""
								: queryForRowSet.getObject("fileNameValidationStatus"));
				resultObj.put("columnOrderValidationStatus",
						queryForRowSet.getObject("columnOrderValidationStatus") == null ? ""
								: queryForRowSet.getObject("columnOrderValidationStatus"));
				resultObj.put("duplicateDataSet", queryForRowSet.getObject("DuplicateDataSet") == null ? ""
						: queryForRowSet.getObject("DuplicateDataSet"));
				resultObj.put("rcStdDev", queryForRowSet.getObject("RC_Std_Dev") == null ? "" : queryForRowSet.getObject("RC_Std_Dev"));
				resultObj.put("rcMean", queryForRowSet.getObject("RC_Mean") == null ? "" : queryForRowSet.getObject("RC_Mean"));
				resultObj.put("rcDeviation", queryForRowSet.getObject("RC_Deviation") == null ? ""
						: queryForRowSet.getObject("RC_Deviation"));
				resultObj.put("rcStdDevStatus", queryForRowSet.getObject("RC_Std_Dev_Status") == null ? ""
						: queryForRowSet.getObject("RC_Std_Dev_Status"));
				resultObj.put("rcMeanMovingAvg", queryForRowSet.getObject("RC_Mean_Moving_Avg") == null ? ""
						: queryForRowSet.getObject("RC_Mean_Moving_Avg"));
				resultObj.put("mMeanMovingAvgStatus", queryForRowSet.getObject("M_Mean_Moving_Avg_Status") == null ? ""
						: queryForRowSet.getObject("M_Mean_Moving_Avg_Status"));
				resultObj.put("recordAnomalyCount", queryForRowSet.getObject("recordAnomalyCount") == null ? ""
						: queryForRowSet.getObject("recordAnomalyCount"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("missingDates", queryForRowSet.getObject("missingDates") == null ? ""
						: queryForRowSet.getObject("missingDates"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getDQRecordCountAnomalyDgroupResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL : " + sql);
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp",
						queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dayOfYear",
						queryForRowSet.getObject("dayOfYear") == null ? "" : queryForRowSet.getObject("dayOfYear"));
				resultObj.put("month",
						queryForRowSet.getObject("month") == null ? "" : queryForRowSet.getObject("month"));
				resultObj.put("dayOfMonth",
						queryForRowSet.getObject("dayOfMonth") == null ? "" : queryForRowSet.getObject("dayOfMonth"));
				resultObj.put("dayOfWeek",
						queryForRowSet.getObject("dayOfWeek") == null ? "" : queryForRowSet.getObject("dayOfWeek"));
				resultObj.put("hourOfDay",
						queryForRowSet.getObject("hourOfDay") == null ? "" : queryForRowSet.getObject("hourOfDay"));
				resultObj.put("recordCount",
						queryForRowSet.getObject("RecordCount") == null ? "" : queryForRowSet.getObject("RecordCount"));
				resultObj.put("fileNameValidationStatus",
						queryForRowSet.getObject("fileNameValidationStatus") == null ? ""
								: queryForRowSet.getObject("fileNameValidationStatus"));
				resultObj.put("columnOrderValidationStatus",
						queryForRowSet.getObject("columnOrderValidationStatus") == null ? ""
								: queryForRowSet.getObject("columnOrderValidationStatus"));
				resultObj.put("rcStdDev", queryForRowSet.getObject("RC_Std_Dev") == null ? ""
						: queryForRowSet.getObject("RC_Std_Dev"));
				resultObj.put("rcMean", queryForRowSet.getObject("RC_Mean") == null ? ""
						: queryForRowSet.getObject("RC_Mean"));
				resultObj.put("dGroupDeviation", queryForRowSet.getObject("dGroupDeviation") == null ? ""
						: queryForRowSet.getObject("dGroupDeviation"));
				resultObj.put("dGroupRcStatus", queryForRowSet.getObject("dGroupRcStatus") == null ? ""
						: queryForRowSet.getObject("dGroupRcStatus"));
				resultObj.put("measurementCol", queryForRowSet.getObject("measurementCol") == null ? ""
						: queryForRowSet.getObject("measurementCol"));
				resultObj.put("sumOfMCol",
						queryForRowSet.getObject("SumOf_M_Col") == null ? "" : queryForRowSet.getObject("SumOf_M_Col"));
				resultObj.put("mStdDev",
						queryForRowSet.getObject("M_Std_Dev") == null ? "" : queryForRowSet.getObject("M_Std_Dev"));
				resultObj.put("dGroupVal",
						queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol",
						queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("mMean",
						queryForRowSet.getObject("M_Mean") == null ? "" : queryForRowSet.getObject("M_Mean"));
				resultObj.put("dgDqi", queryForRowSet.getObject("dgDqi") == null ? ""
						: queryForRowSet.getObject("dgDqi"));
				resultObj.put("mDeviation",
						queryForRowSet.getObject("M_Deviation") == null ? "" : queryForRowSet.getObject("M_Deviation"));
				resultObj.put("mStdDevStatus", queryForRowSet.getObject("M_Std_Dev_Status") == null ? ""
						: queryForRowSet.getObject("M_Std_Dev_Status"));
				resultObj.put("recordAnomalyCount", queryForRowSet.getObject("recordAnomalyCount") == null ? ""
						: queryForRowSet.getObject("recordAnomalyCount"));
				resultObj.put("missingDates", queryForRowSet.getObject("missingDates") == null ? ""
						: queryForRowSet.getObject("missingDates"));
				resultObj.put("duplicateDataSet", queryForRowSet.getObject("DuplicateDataSet") == null ? ""
						: queryForRowSet.getObject("DuplicateDataSet"));
				resultObj.put("action",
						queryForRowSet.getObject("Action") == null ? "" : queryForRowSet.getObject("Action"));
				resultObj.put("userName",
						queryForRowSet.getObject("UserName") == null ? "" : queryForRowSet.getObject("UserName"));
				resultObj.put("time", queryForRowSet.getObject("Time") == null ? "" : queryForRowSet.getObject("Time"));
				resultObj.put("validity",
						queryForRowSet.getObject("Validity") == null ? "" : queryForRowSet.getObject("Validity"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? ""
						: queryForRowSet.getObject("forgot_run_enabled"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}
	@Override
	public JSONArray getDQCustomDistributionResults(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("SQL : " + sql);
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				resultObj.put("id", queryForRowSet.getObject("Id") == null ? "" : queryForRowSet.getObject("Id"));
				resultObj.put("idApp", queryForRowSet.getObject("idApp") == null ? "" : queryForRowSet.getObject("idApp"));
				resultObj.put("date", queryForRowSet.getObject("Date") == null ? "" : queryForRowSet.getObject("Date"));
				resultObj.put("run", queryForRowSet.getObject("Run") == null ? "" : queryForRowSet.getObject("Run"));
				resultObj.put("dayOfYear", queryForRowSet.getObject("dayOfYear") == null ? "" : queryForRowSet.getObject("dayOfYear"));
				resultObj.put("month", queryForRowSet.getObject("month") == null ? "" : queryForRowSet.getObject("month"));
				resultObj.put("dayOfMonth", queryForRowSet.getObject("dayOfMonth") == null ? "" : queryForRowSet.getObject("dayOfMonth"));
				resultObj.put("dayOfWeek", queryForRowSet.getObject("dayOfWeek") == null ? "" : queryForRowSet.getObject("dayOfWeek"));
				resultObj.put("hourOfDay", queryForRowSet.getObject("hourOfDay") == null ? "" : queryForRowSet.getObject("hourOfDay"));
				resultObj.put("colName", queryForRowSet.getObject("ColName") == null ? "" : queryForRowSet.getObject("ColName"));
				resultObj.put("count", queryForRowSet.getObject("Count") == null ? "" : queryForRowSet.getObject("Count"));
				resultObj.put("min", queryForRowSet.getObject("Min") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("Min"))));
				resultObj.put("max", queryForRowSet.getObject("Max") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("Max"))));
				resultObj.put("cardinality", queryForRowSet.getObject("Cardinality") == null ? "" : queryForRowSet.getObject("Cardinality"));
				resultObj.put("stdDev", queryForRowSet.getObject("Std_Dev") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("Std_Dev"))));
				resultObj.put("mean", queryForRowSet.getObject("Mean") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("Mean"))));
				resultObj.put("nullValue", queryForRowSet.getObject("Null_Value") == null ? "" : queryForRowSet.getObject("Null_Value"));
				resultObj.put("status", queryForRowSet.getObject("Status") == null ? "" : queryForRowSet.getObject("Status"));
				resultObj.put("stringCardinalityAvg", queryForRowSet.getObject("StringCardinalityAvg") == null ? "" : queryForRowSet.getObject("StringCardinalityAvg"));
				resultObj.put("stringCardinalityStdDev", queryForRowSet.getObject("StringCardinalityStdDev") == null ? "" : queryForRowSet.getObject("StringCardinalityStdDev"));
				resultObj.put("strCardinalityDeviation", queryForRowSet.getObject("StrCardinalityDeviation") == null ? "" : queryForRowSet.getObject("StrCardinalityDeviation"));
				resultObj.put("stringThreshold", queryForRowSet.getObject("String_Threshold") == null ? "" : queryForRowSet.getObject("String_Threshold"));
				resultObj.put("stringStatus", queryForRowSet.getObject("String_Status") == null ? "" : queryForRowSet.getObject("String_Status"));
				resultObj.put("numMeanAvg",queryForRowSet.getObject("NumMeanAvg") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumMeanAvg"))));
				resultObj.put("numMeanStdDev", queryForRowSet.getObject("NumMeanStdDev") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumMeanStdDev"))));
				resultObj.put("numMeanDeviation", queryForRowSet.getObject("NumMeanDeviation") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumMeanDeviation"))));
				resultObj.put("numMeanThreshold", queryForRowSet.getObject("NumMeanThreshold") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumMeanThreshold"))));
				resultObj.put("numMeanStatus", queryForRowSet.getObject("NumMeanStatus") == null ? "" : queryForRowSet.getObject("NumMeanStatus"));
				resultObj.put("numSDAvg", queryForRowSet.getObject("NumSDAvg") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSDAvg"))));
				resultObj.put("numSDStdDev", queryForRowSet.getObject("NumSDStdDev") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSDStdDev"))));
				resultObj.put("numSDDeviation", queryForRowSet.getObject("NumSDDeviation") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSDDeviation"))));
				resultObj.put("numSDThreshold", queryForRowSet.getObject("NumSDThreshold") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSDThreshold"))));
				resultObj.put("numSDStatus", queryForRowSet.getObject("NumSDStatus") == null ? "" : queryForRowSet.getObject("NumSDStatus"));
				resultObj.put("outOfNormStatStatus", queryForRowSet.getObject("outOfNormStatStatus") == null ? "" : queryForRowSet.getObject("outOfNormStatStatus"));
				resultObj.put("sumOfNumStat", queryForRowSet.getObject("sumOfNumStat") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("sumOfNumStat"))));
				resultObj.put("numSumAvg", queryForRowSet.getObject("NumSumAvg") == null ? "" : String.valueOf(queryForRowSet.getObject("NumSumAvg")));
				resultObj.put("numSumStdDev", queryForRowSet.getObject("NumSumStdDev") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSumStdDev"))));
				resultObj.put("numSumThreshold", queryForRowSet.getObject("NumSumThreshold") == null ? "" : String.valueOf(numberFormat.format(queryForRowSet.getObject("NumSumThreshold"))));
				resultObj.put("dGroupVal", queryForRowSet.getObject("dGroupVal") == null ? "" : queryForRowSet.getObject("dGroupVal"));
				resultObj.put("dGroupCol", queryForRowSet.getObject("dGroupCol") == null ? "" : queryForRowSet.getObject("dGroupCol"));
				resultObj.put("dataDriftCount", queryForRowSet.getObject("dataDriftCount") == null ? "" : queryForRowSet.getObject("dataDriftCount"));
				resultObj.put("dataDriftStatus", queryForRowSet.getObject("dataDriftStatus") == null ? "" : queryForRowSet.getObject("dataDriftStatus"));
				resultObj.put("defaultValue", queryForRowSet.getObject("Default_Value") == null ? "" : queryForRowSet.getObject("Default_Value"));
				resultObj.put("defaultCount", queryForRowSet.getObject("Default_Count") == null ? "" : queryForRowSet.getObject("Default_Count"));
				resultObj.put("recordCount", queryForRowSet.getObject("Record_Count") == null ? "" : queryForRowSet.getObject("Record_Count"));
				resultObj.put("nullPercentage", queryForRowSet.getObject("Null_Percentage") == null ? "" : queryForRowSet.getObject("Null_Percentage"));
				resultObj.put("defaultPercentage", queryForRowSet.getObject("Default_Percentage") == null ? "" : queryForRowSet.getObject("Default_Percentage"));
				resultObj.put("nullThreshold", queryForRowSet.getObject("Null_Threshold") == null ? "" : queryForRowSet.getObject("Null_Threshold"));
				resultObj.put("defaultThreshold", queryForRowSet.getObject("Default_Threshold") == null ? "" : queryForRowSet.getObject("Default_Threshold"));
				resultObj.put("forgotRunEnabled", queryForRowSet.getObject("forgot_run_enabled") == null ? "" : queryForRowSet.getObject("forgot_run_enabled"));
				resultObj.put("numSumStatus", queryForRowSet.getObject("numSumStatus") == null ? "" : queryForRowSet.getObject("numSumStatus"));
				resultObj.put("numDqi", queryForRowSet.getObject("numDqi") == null ? "" : queryForRowSet.getObject("numDqi"));

				resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return resultArr;
	}
}
