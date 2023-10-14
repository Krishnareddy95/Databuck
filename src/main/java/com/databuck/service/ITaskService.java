package com.databuck.service;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.DATA_QUALITY_INDEX;

public interface ITaskService {
	public String getTaskStatusFromRunScheduledTasks(Long idApp, String uniqueId);
	public int getColumnCountYesInListApplicationsAndListDFTranrule(Long idApp);
	public int getTaskStatusForPassed(Long idApp);
	public int getStatusOfDfReadFromTaskProgressStatus(Long idApp);
	public SqlRowSet getAppTypeFromListApplications(Long idApp);
	public int getStatusForMatching(Long idApp);
	public void insertDataIntoDATA_QUALITY_INDEX(DATA_QUALITY_INDEX dQI, String recordCountAnomaly);
	public int getStatusForSchemaMatching(Long idApp);
	public int getCountOfTasksPassedForTemplate(Long idData);
	public int getCountOfTasksEnabledForTemplate(Long idData);
	public String getTemplateCreationJobStatusById(Long idData, String uniqueId);
	public int getStatusOfDfReadTaskForTemplate(Long idData);
	public void insertTaskListForTemplate(Long idData,String profilingEnabled, String advancedRulesEnabled);
	public int runRebootScript();
	public boolean enableScheduleCheck(long idSchedule);
}