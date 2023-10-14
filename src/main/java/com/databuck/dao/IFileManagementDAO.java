package com.databuck.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.FingerprintMatchingDashboard;

public interface IFileManagementDAO {

	SqlRowSet getdatafromresultmaster();

	SqlRowSet getTableNamesForAppIDinResultMaster(Long appId);
	
	public SqlRowSet getTableFromResultsDB(String tableName, Long appId);

	public SqlRowSet getDataFromResultMasterForStatisticalMatching();
	
	public SqlRowSet getTableFromResultsDBForStatisticalMatching(String tableName, Long appId, String appType);

	public SqlRowSet getDataFromResultMasterForModelGovernance();

	SqlRowSet getTableFromResultsDBForModelGovernance(String table_Name, Long appId, String modelGovernanceType);

	public SqlRowSet getModelGovernanceDashboardFromListApplications();

	public SqlRowSet getcsvDirectoryFromListApplications(Long idApp);

	List<String> getAllModelsFromModelGovernanceResultsTable(String decileEqualityTableName, String date);
	public List<String> getfailedModelsFromModelGovernanceResultsTable(String decileEqualityTableName, String date,String statusColumnName);

	String getApplicationNameFromApplicationName(String string);

	public SqlRowSet getDataFromResultMasterForDataMatchingGroup();

	public SqlRowSet getMaxRecordForDashBoard(String table_Name, Long appId);
	public SqlRowSet getDataFromResultMasterForSchemaMatching() ;

	public SqlRowSet getTableDataForSchemMatching(String tableName);

	List<FingerprintMatchingDashboard> getDashBoardDataForFingerprintMatching(Map<Long, String> resultmasterdata);
	public SqlRowSet getTableNamesForAppIDinResultMasterForSchemaMatching(Long appId, String resultType);
}
