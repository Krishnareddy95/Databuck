package com.databuck.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.ColumnCombinationProfile_DP;
import com.databuck.bean.ColumnProfileDetails_DP;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.ListAdvancedRules;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.NumericalProfile_DP;
import com.databuck.bean.Project;
import com.databuck.bean.RowProfile_DP;
import com.databuck.bean.listDataAccess;

public interface IListDataSourceDAO {

	// public ListDataSource get(int contactId);

	public List<ListDataSource> getListDataSource(long idDataSchema);

	public List<ListDataSource> getListDataSourceTable(Long project_id, List<Project> projlst, String fromDate,
			String toDate);

	public List<ListDataSource> getListDataSourceTable(Long project_id, List<Project> projlst, String fromDate,
			String toDate, String objName);

	// delete the Recourd
	public ListDataSource delete(int idData);

	public int deleteDataSource(int idData);

	public List<ListDataSchema> getListDataSchema(Long project_id, List<Project> projList, String fromDate,
			String toDate);

	public List<ListDataSchema> getListDataSchemaForIdDataSchema(long idDataSchema);

	public boolean deactivateSchema(long idDataSchema);

	public boolean activateSchema(long idDataSchema);

	public ListDataSource getDataFromListDataSourcesOfIdData(Long idData);

	// Added as part of derived template changes
	public ListDerivedDataSource getDataFromListDerivedDataSourcesOfIdData(Long idData);

	public List<ListApplications> getDataFromListApplicationsforValidationCheckAddNewCustomize(
			List<Long> listApplicationsIdList, String databaseSchema);

	public Map<Long, String> getDataTemplateForRequiredLocation(String location, Long project_id, Integer domainId);

	public listDataAccess getListDataAccess(Long iData);

	public boolean updateDataTemplate(String sql_lds, String sql_lda);

	// Added as part of derived template changes
	public boolean updateDerivedDataTemplate(String sql_ldds);

	public List<ListDataSchema> getListDataSchemaId(long idDataSchema);

	public List<ListDataSource> getListDataSourceTableId(long idData);

	// added for export func. 14jan2019
	public List<ListDataSchema> getListDataSchemaForExport();

	public List<ListDataSource> getListDataSourceTableForExport();

	// added for getting schemaname from idDataSchema
	public String getSchemaNameByIdData(long idData);

	// added for Profiling getting DataSource details for profilingEnabled template
	// only
	public List<ListDataSource> getListDataSourceTableForProfiling(Long projectId, List<Project> projList,
			String fromDate, String toDate);

	List<ListDataSource> getListDataSourceTableForAdvancedRules(Long projectId, List<Project> projList);

	List<ListAdvancedRules> getAdvancedRulesForId(long idData);

	public int getAdvancedRulesCount(long idData);

	void updateAdvancedRulesActiveStatus(long ruleId, String status, Long idListColrules);

	public Map<Long, String> getRollDataTargetSchemaList(Long projectId);

	public List<ListDataSource> getListDataSourceTableForRef(String projList,String domainId);

	// Reading Profiling tables
	public List<RowProfile_DP> readRowProfileForTemplate(Long idData);

	public List<NumericalProfile_DP> readNumericProfileForTemplate(Long idData);

	public List<ColumnProfileDetails_DP> readColumnProfileDetailsForTemplate(Long idData);

	public List<ColumnProfile_DP> readColumnProfileForTemplate(Long idData);

	public List<ColumnCombinationProfile_DP> readColumnCombinationProfileForTemplate(Long idData);

	public SqlRowSet getPresentAndLastRunDetailsOfColumnProfile(long idData);

	public List<ColumnProfile_DP> readColumnProfileForTemplate(long idData, Date previousExecDate, Long previousRun);

	// Check if a global rule is linked to template
	public boolean isGlobalruleLinkedToTemplate(long idData, long globalRuleId);

	// Check if a global threshold is linked to template
	public boolean isGlobalThresholdLinkedToTemplate(long idData, long idGlobalThreshold, long idColumn);

	public List<ColumnProfile_DP> readColumnDataProfile(Long selectedProjectId, String fromDate, String toDate);

	public boolean updateTemplateDeltaApprovalStatus(Long newIdData, String string);

	public boolean deactivateDataSource(long idData);

	public void insertListDataDefintionsFromStagingForIdData(Long newIdData, Long oldIdData);

	public void clearListDataDefinitonStagingForIdData(long idData);

	public void copyListDataDefintionsOfTemplateToStaging(Long idData);

	public int deleteListDataDefinitionByIdColumn(long idColumn);

	public int insertListDataDefinitionForIdData(long idData, ListDataDefinition ldd);

	public SqlRowSet getTemplatesColumnChangesForProject(long projectId);

	public String getAlreadyLinkedGlobalRulesToDataTemplate(long nDataTemplateRowId);

	public boolean unlinkGlobalRuleFromDataTemplate(long nDataTemplateRowId, long nGlobalRuleId);

	public List<ListDataSchema> getAllActiveAndInActiveConnections(Long project_id, List<Project> projList,
			String fromDate, String toDate);

	public Map<Long, String> getConnectionsByDomainProject(Integer domainId, Long projectId);

	public void deactivateGlobalRuleOfTemplate(long idData, long globalRuleId);

	public JSONArray getPaginatedDataTemplateJsonData(HashMap<String, String> oPaginationParms) throws Exception;

	public List<Map<String, Object>> findProfilingResultsByFilter(String sql);

	public List<ListDataSource> getListDataSourceTableForAdvancedRules(String projList);

	public List<ListAdvancedRules> getAdvancedRulesForChecks(Long idData, String checkType, JSONObject filterAtrr);

	public boolean isGlobalruleLinkedToTemplateDeactivated(long idData, Long globalRuleId);

	public void activateGlobalruleLinkedToTemplate(long idData, ListColGlobalRules listColGlobalRules);

	public boolean insertToRuleTemplateMapping(long idData, ListColGlobalRules listColGlobalRules,
			String anchorColumns);

	public boolean updateRuleTemplateMapping(long idData, ListColGlobalRules listColGlobalRules, String anchorColumns);

	public List<ListDataSource> getListDataSourceBySource(long idDataSchema, String sourceName);

	public List<Map<String, Object>> getDataTemplateForRequiredConnectionType(String connectionType, Long projectId,
			Integer domainId);

	public boolean enableSSLForConnectionById(long idDataSchema);

	public List<HashMap<String, String>> getPaginatedDataTemplateJsonDatawithDomain(
			HashMap<String, String> oPaginationParms) throws Exception;

	List<ListDataSource> getListDataSourceTableForProfilingDate(Long projectId, List<Project> projlst, String fromDate,
			String toDate);

	List<ColumnProfile_DP> readColumnDataProfileDate(Long selectedProjectId, String fromDate, String toDate);

	String checkTemplateStatus(int dataTemplateId);

	public long getIdDataByDerivedId(long idDerivedData);

	public ListDataSource getDataFromListDataSourcesByName(String templateName);

	public List<Map<String, Object>> getListDataSchemaForDropDown(Long long1);

	public long getActiveIdDataByTableName(String tableName);

	boolean deactivateTemplate(long idData);
	
	public boolean activateTemplate(long idDataSchema);
}
