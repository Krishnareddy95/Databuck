package com.databuck.service;

import com.databuck.bean.Domain;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.listDataAccess;
import com.databuck.controller.DataTemplateController;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IValidationDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

@Service
public class DomainLiteJobService {

	@Autowired
	private IListDataSourceDAO iListDataSourceDAO;

	@Autowired
	private IProjectDAO iProjectDAO;

	@Autowired
	private DataTemplateController dataTemplateController;

	@Autowired
	private IDataAlgorithService iDataAlgorithService;

	@Autowired
	private IValidationDAO iValidationDAO;
	
	private static final Logger LOG = Logger.getLogger(DomainLiteJobService.class);

	/**
	 * This method executes Domain Lite job and gets Json result
	 */
	public String prepareDomainLiteJson(Domain domain) {
		JSONObject domainObj = new JSONObject();

		try {
			// Get domain details
			int domainId = domain.getDomainId();
			domainObj.put("domainId", domainId);
			domainObj.put("domainName", domain.getDomainName());

			// Get list of Projects for domain
			List<Project> projectList = iProjectDAO.getAllProjectsForDomain(domainId);

			JSONArray projectArr = new JSONArray();

			for (Project project : projectList) {
				JSONObject projectObj = new JSONObject();

				// Get project details
				long projectId = project.getIdProject();
				projectObj.put("projectId", projectId);
				projectObj.put("projectName", project.getProjectName());

				// Fetch connection list for the project
				Map<Long, String> connectionMap = iListDataSourceDAO.getConnectionsByDomainProject(domainId, projectId);

				if (connectionMap != null) {
					JSONArray connectionArr = new JSONArray();

					for (Map.Entry<Long, String> connection : connectionMap.entrySet()) {

						// Get connection details
						long idDataSchema = connection.getKey();
						List<ListDataSchema> schemaList = iListDataSourceDAO.getListDataSchemaId(idDataSchema);

						if (schemaList != null && !schemaList.isEmpty()) {

							ListDataSchema listDataSchema = schemaList.get(0);

							JSONObject connectionObj = new JSONObject();
							connectionObj.put("connectionId", idDataSchema);
							connectionObj.put("connectionName", listDataSchema.getSchemaName());

							// Get the tables list for connection
							String schemaType = listDataSchema.getSchemaType();
							List<String> tablesList = dataTemplateController.getTableListForSchema("" + idDataSchema,
									schemaType, null);

							if (tablesList != null && tablesList.size() > 0) {

								List<String> newTablesList = new ArrayList<>();
								List<String> missingTablesList = new ArrayList<>();
								List<String> existingTablesList = new ArrayList<String>();
								JSONArray modifiedTables = new JSONArray();

								// Get the list of templates associated with the schema
								List<ListDataSource> templateList = iListDataSourceDAO.getListDataSource(idDataSchema);

								if (templateList != null && templateList.size() > 0) {

									// Identify the tables which has templates created
									for (ListDataSource listDataSource : templateList) {

										// Get template details
										Integer idData = listDataSource.getIdData();
										listDataAccess lDataAccess = iListDataSourceDAO
												.getListDataAccess(idData.longValue());

										// Get table name
										String tableName = lDataAccess.getFolderName();
										LOG.debug("Table Name:"+tableName);
										if (existingTablesList.contains(tableName))
											continue;

										// Consider only non query based templates
										if ((lDataAccess.getWhereCondition() == null
												|| lDataAccess.getWhereCondition().trim().isEmpty())
												&& (lDataAccess.getQuery() == null
														|| !lDataAccess.getQuery().equalsIgnoreCase("Y"))) {

											LOG.debug("table=" + tableName);
											existingTablesList.add(tableName);

											// Preparing missing table list
											if (!tablesList.contains(tableName)) {
												missingTablesList.add(tableName);
												continue;
											}

											List<String> tableColumnList = iDataAlgorithService.getTableColumns(
													idDataSchema, schemaType, tableName, "", "N", null);

											LOG.info("Detecting new and missing columns ...");

											// Get the columns details from template
											List<ListDataDefinition> listDataDefinitions = iValidationDAO
													.getListDataDefinitionsByIdData(idData.longValue());

											// List of column names from listDataDefinition for this template
											List<String> template_cols = new ArrayList<String>();
											for (ListDataDefinition ldd : listDataDefinitions) {
												template_cols.add(ldd.getDisplayName());
											}

											// Prepare list with column names of table
											List<String> newColumns = new ArrayList<String>(tableColumnList);

											// Prepare list with column names of template listDataDefinition
											List<String> missingColumns = new ArrayList<String>(template_cols);

											// Remove all template columns from table columns list, remaining will be
											// new columns
											newColumns.removeAll(template_cols);

											// Remove all table columns from template columns list, remaining will be
											// missing columns
											missingColumns.removeAll(tableColumnList);

											JSONObject modifiedTableObj = new JSONObject();

											if (missingColumns != null && missingColumns.size() > 0)
												modifiedTableObj.put("missingColumnList",
														String.join(",", missingColumns));

											if (newColumns != null && newColumns.size() > 0)
												modifiedTableObj.put("newColumnList", String.join(",", newColumns));

											if (modifiedTableObj.length() > 0) {
												modifiedTableObj.put("tableName", tableName);
												modifiedTables.put(modifiedTableObj);
											}

										}

									}

									// preparing new table list
									for (String table : tablesList) {
										if (!existingTablesList.contains(table)) {
											newTablesList.add(table);
										}
									}

								} else
										newTablesList = tablesList;

								// Adding new, missing and modified tables details to response
								if (newTablesList != null && newTablesList.size() > 0)
									connectionObj.put("newTableList", String.join(",", newTablesList));

								if (missingTablesList != null && missingTablesList.size() > 0)
									connectionObj.put("missingTableList", String.join(",", missingTablesList));

								if (modifiedTables != null && modifiedTables.length() > 0)
									connectionObj.put("modifiedTableList", modifiedTables);

								connectionArr.put(connectionObj);
							}
						}

					}
					projectObj.put("associatedConnections", connectionArr);
				}
				projectArr.put(projectObj);
			}
			domainObj.put("associatedProjects", projectArr);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return domainObj.toString();
	}
}
