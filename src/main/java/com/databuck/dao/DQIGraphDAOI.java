package com.databuck.dao;

import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;

import com.databuck.bean.ListApplications;

public interface DQIGraphDAOI {

	MultiValueMap getRecordCountGraph(Long idApp, ListApplications listApplicationsData);

	MultiValueMap getNullCountGraph(Long idApp, ListApplications listApplicationsData);
	
	MultiValueMap GetGraphForDashboard(Long idApp, ListApplications listApplicationsData, String tabName);
	MultiValueMap GetGraphForDashboardWithStatus(Long idApp, ListApplications listApplicationsData, String tabName);

	MultiValueMap getPrimaryKeyGraph(Long idApp, ListApplications listApplicationsData);

	MultiValueMap getUserSelectedKeyGraph(Long idApp, ListApplications listApplicationsData);

	MultiValueMap getNumericalFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData);

	MultiValueMap getStringFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData);
	
	MultiValueMap getRAFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData);

	MultiValueMap getAggregateDQISummaryGraph(Long idApp, ListApplications listApplicationsData, Map<String, String> tranRuleMap);
	
	MultiValueMap getGraphForDQI_ExecutiveSumm(String projectId) ;
	
	MultiValueMap getHistoricAvgForAllRunsAggDQI_graph(Long idApp); 

}
