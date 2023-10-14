package com.databuck.dao;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDmCriteria;
import com.databuck.bean.ValidationView;

public interface IValidationDAO {

	List<ListDataSchema> getData();

	public List<ListApplications> getDataFromListApplicationsOfDQType();

	public boolean deleteValidationViewApplication(Long idApp);

	public int[] updateSchemavaluesinListDfTranRule(Long threshold, Long identity, Long idApp);

	public List<ListDataDefinition> getListDataDefinitionsByIdData(Long idData);

	public boolean changeAllNonNullsToYes(Long idData);

	public boolean changeAllMicrosegmentToYes(Long idData);

	public boolean changeAlllastReadTimeToYes(Long idData);

	public boolean changeAllIsMaskedToYes(Long idData);

	public boolean changeAllPartitionByToYes(Long idData);

	public boolean changeAllDataDriftToYes(Long idData);

	public boolean changeAllStartDateToYes(Long idData);

	public boolean changeAllEndDateToYes(Long idData);

	public boolean changeAllTimelinessKeyToYes(Long idData);

	public boolean changeAllRecordAnomalyToYes(Long idData);

	public boolean changeAllDefaultCheckToYes(Long idData);

	public boolean changeAllDateRuleToYes(Long idData);

	public boolean changeAllPatternCheckToYes(Long idData);
	
	public boolean changeAllDefaultPatternCheckToYes(Long idData);

	public boolean changeAllBadDataToYes(Long idData);

	public boolean changeAllLengthCheckToYes(Long idData);

	public boolean changeAllMaxLengthCheckToYes(Long idData); // added for Max Length Check

	public boolean changeAllMatchValuetToYes(Long idData);

	SqlRowSet getdatafromresultmaster();

	public JSONArray getValidationResultsByCheck(long idApp, String fromDate, String toDate, String tableName,
			String dGroupCondition);

	public JSONArray getFilteredValidationResultsByCheck(long idApp, String fromDate, String toDate, String tableName,
			JSONObject filterAttribute, String dGroupCondition);

	JSONArray getValidationResultsByCheckWithColName(long idApp, String fromDate, String toDate, String tableName,
			String dGroupCondition, String colName, String string, String checkName, JSONObject columnValues);

	public int getResultCount(long idApp, String fromDate, String toDate, String tableName,
			String dGroupCondition, String checkName);
	
	JSONArray getValidationResultsByCheckWithColNameNew(long idApp, String fromDate, String toDate, String tableName,
			String dGroupCondition, String colName, String string, String checkName, JSONObject columnValues,
			int offset, int records);
	
	JSONArray getValidationResultsByCheckInitialRecords(long idApp, String fromDate, String toDate, String tableName);

	
	JSONArray getValidationResultsForDataDrift(long idApp, String fromDate, String toDate, String colName,
			JSONObject columnValues);

	public List<ListDmCriteria> getDataFromListDMCriteria(Long idApp);

	public int getFirstRunCount(long idApp);

	
}