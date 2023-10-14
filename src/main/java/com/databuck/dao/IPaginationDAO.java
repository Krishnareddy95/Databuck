package com.databuck.dao;

import java.util.Map;

import com.databuck.dto.ValidationListRequest;
import com.databuck.dto.ValidationResultRequest;
import org.json.JSONArray;

public interface IPaginationDAO {

	public String getAppIdsListForDomainProject(int domainId, int projectId);

	public Map<String, Object> getAllValidations(ValidationListRequest validationRequest,
			String applicationIds);

	public Map<String, Object> getDataDriftResult(ValidationResultRequest resultRequest);
	
	public Map<String, Object> getPaginatedValidationsJsonData(ValidationListRequest validationRequest) throws Exception;

	public Map<String, Object> getDistributionResult(ValidationResultRequest resultRequest);

	public JSONArray getDQNullCheckResults(String sql);

	public JSONArray getDQMicroNullCheckResults(String sql);

	public Map<String,String> getMaxRunAndDateByQuery(String fromDate,String toDate,long idApp);

	public JSONArray getDQLengthCheckResults(String sql);

	public JSONArray getAutoDiscoverdPatternResults(String sql);

	public JSONArray getUserDefinedPatternResults(String sql);

	public JSONArray getDQBadDataCheckResults(String sql) ;

	public JSONArray getDQDefaultValueCheckResults(String sql);

	public JSONArray getDQDataDriftCountSummaryResults(String sql, Long idApp);
	
	public JSONArray getDQDataDriftSummaryResults(String sql);

	public JSONArray getDQDuplicateCheckResults(String sql);

	public JSONArray getDQDuplicateCheckSummaryResults(String sql);

	public JSONArray getGlobalRuleResults(String sql);
	
	public JSONArray getTimeSequenceCheckResults(String sql);

	public JSONArray getDQRecordAnomalyResults(String sql);

	public JSONArray getDQDistributionCheckResults(String sql);

	public JSONArray getDQDateConsistencyResults(String sql);

	public JSONArray getDQDateConsistencyFailedResults(String sql);

	public  JSONArray getDQRecordCountAnomalyResults(String sql);

	public  JSONArray getDQRecordCountAnomalyDgroupResults(String sql);

	public JSONArray getDQCustomDistributionResults(String sql);
}
