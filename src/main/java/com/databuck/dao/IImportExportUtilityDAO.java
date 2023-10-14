package com.databuck.dao;

import java.util.List;
import java.util.Map;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.SynonymLibrary;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.ListColRules;
import com.databuck.bean.listModelGovernance;
import com.databuck.bean.listStatisticalMatchingConfig;
import com.google.common.collect.Multimap;

public interface IImportExportUtilityDAO {

	public ListApplications getdatafromlistapplications(Long idApp);
	public ListDataSource getDataFromListDataSources(Long idData);
	public listDataAccess getListDataAccess(Long iData);
	public List<ListDataDefinition> getListDataDefinitionData(Long idData);
	public List<ListDataSchema> getListDataSchema(long idDataSchema);
	
	//import
	public String insertIntoListApplicationsForImport(String readLine, Long idData, Long projectId) throws Exception ;
	public String insertIntoListDataSourcesForImport(String readLine, Long idDataSchema, Long projectId);
	public String insertIntoListDataAccessForImport(String readLine, Long idDataSchema, Long idData);
	public String insertIntoListDataDefinationForImport(String readLine, Long idDataSchema, Long idData);
	public String insertIntoConnectionForImport(String readLine, Long projectId, Integer domainId);
	public List<ListColRules> getListColRulesData(Long id);
	public List<ListColGlobalRules> getListColGlobalRulesData(Long id);
	public List<SynonymLibrary> getListSynonymLibraryData(Long id);

}