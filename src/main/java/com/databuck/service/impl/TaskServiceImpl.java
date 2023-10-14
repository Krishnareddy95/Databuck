package com.databuck.service.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.databuck.bean.DATA_QUALITY_INDEX;
import com.databuck.bean.ListApplications;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.ITaskService;
import com.databuck.util.DatabuckUtility;
import org.apache.log4j.Logger;

@Component
public class TaskServiceImpl implements ITaskService {
	@Autowired
	public ITaskDAO iTaskDAO;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	@Autowired
	private Properties appDbConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(TaskServiceImpl.class);

	public String getTaskStatusFromRunScheduledTasks(Long idApp, String uniqueId) {
		return iTaskDAO.getTaskStatusFromRunScheduledTask(idApp, uniqueId);
	}

	public int getColumnCountYesInListApplicationsAndListDFTranrule(Long idApp) {
		try {
			String sql = "SELECT nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,dataDriftCheck,"
					+ "recordCountAnomaly,keyGroupRecordCountAnomaly FROM listApplications WHERE idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			int count = 0;

			while (queryForRowSet.next()) {
				for (int i = 1; i <= queryForRowSet.getMetaData().getColumnCount(); i++) {
					if (queryForRowSet.getString(i) != null) {
						if (queryForRowSet.getString(i).equalsIgnoreCase("Y")) {
							count++;
						}
					}
				}
			}

			String sql1 = "SELECT count(*) FROM listDFTranRule WHERE dupRow='Y' and idApp=" + idApp;
			Integer count2 = jdbcTemplate.queryForObject(sql1, Integer.class);
			count = count + count2;
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public SqlRowSet getAppTypeFromListApplications(Long idApp) {
		try {
			String sql = "select appType,name from listApplications where idApp=" + idApp;
			return jdbcTemplate.queryForRowSet(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int getStatusForMatching(Long idApp) {
		int per = 5;
		try {
			String sql = "SELECT dfread,dfread2,matchingStatus FROM task_progress_status WHERE idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString(1) != null && queryForRowSet.getString(1).equalsIgnoreCase("passed")) {
					per = 30;
				}
				if (queryForRowSet.getString(2) != null && queryForRowSet.getString(2).equalsIgnoreCase("passed")) {
					per = per + 30;
				}
				if (queryForRowSet.getString(3) != null && queryForRowSet.getString(3).equalsIgnoreCase("passed")) {
					return per = 100;
				}
				return per;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 5;
	}

	public int getStatusForSchemaMatching(Long idApp) {
		try {
			String sql = "SELECT ((schemaMatchingCompleted *95 / schemaMatchingTotal)+5) AS percentage FROM  "
					+ "task_progress_status WHERE idApp =" + idApp + " limit 1";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString(1) != null) {
					if (queryForRowSet.getString(1).equalsIgnoreCase(null)) {
						return 5;
					}
					return (int) queryForRowSet.getDouble(1);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 5;
	}

	public int getTaskStatusForPassed(Long idApp) {
		try {
			String sql = "SELECT rca,gbrca,numstat,strstat,nullcheck,dupidcheck,dupallcheck,ra,datadrift FROM task_progress_status WHERE idApp="
					+ idApp;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			int statusCount = 0;
			while (queryForRowSet.next()) {
				for (int i = 1; i <= queryForRowSet.getMetaData().getColumnCount(); i++) {
					if (queryForRowSet.getString(i) != null) {
						if (queryForRowSet.getString(i).equalsIgnoreCase("passed")) {
							statusCount++;
						}
					}
				}
			}
			return statusCount;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 5;
	}

	public int getStatusOfDfReadFromTaskProgressStatus(Long idApp) {
		try {
			String sql = "SELECT dfread FROM task_progress_status WHERE idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			int statusCount = 5;
			while (queryForRowSet.next()) {
				for (int i = 1; i <= queryForRowSet.getMetaData().getColumnCount(); i++) {
					if (queryForRowSet != null && queryForRowSet.getString(i) != null
							&& queryForRowSet.getString(i).equalsIgnoreCase("passed")) {
						statusCount = 30;
					}
				}
			}
			return statusCount;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 5;
	}

	@Override
	public void insertDataIntoDATA_QUALITY_INDEX(DATA_QUALITY_INDEX DQI, String recordCountAnomaly) {
		try {
			SqlRowSet queryForRowSet;
			long idApp = DQI.getValidation_check_id();
			String RCATableName = "";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				RCATableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				RCATableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			String sql = "SELECT max(run) FROM " + RCATableName + " where idApp=?";
			LOG.debug("sql:" + sql);
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);

			while (queryForRowSet.next()) {
				DQI.setRun(queryForRowSet.getLong(1));

			}
			DQI.setDate(new Date());
			/*
			 * SimpleDateFormat smf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			 * DQI.setCreated_at(smf.format(new Date())); DQI.setUpdate_at(new DateTime());
			 */
			sql = "insert into DATA_QUALITY_INDEX (validation_check_name,validation_check_id,date,run,overall_status,"
					+ "overall_score,created_at,update_at,record_count_status,record_count_score,null_count_status,null_count_score,"
					+ "all_fields_status,all_fields_score,user_selected_fields_status,user_selected_fields_score,"
					+ "numerical_field_status,numerical_field_score,string_field_status,string_field_score,"
					+ "record_anomaly_status,record_anomaly_score) values(?,?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate1.update(sql, DQI.getValidation_check_name(), DQI.getValidation_check_id(), DQI.getDate(),
					DQI.getRun(), DQI.getOverall_status(), DQI.getOverall_score(), DQI.getRecord_count_status(),
					DQI.getRecord_count_score(), DQI.getNull_count_status(), DQI.getNull_count_score(),
					DQI.getAll_fields_status(), DQI.getAll_fields_score(), DQI.getUser_selected_fields_status(),
					DQI.getUser_selected_fields_score(), DQI.getNumerical_field_status(),
					DQI.getNumerical_field_score(), DQI.getString_field_status(), DQI.getString_field_score(),
					DQI.getRecord_anomaly_status(), DQI.getRecord_anomaly_score());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public int getCountOfTasksPassedForTemplate(Long idData) {
		try {
			String sql = "SELECT count(*) FROM template_task_status WHERE status='passed' and idData=" + idData;
			Integer count = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (count == null) {
				count = 0;
			}
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int getCountOfTasksEnabledForTemplate(Long idData) {
		try {
			String sql = "SELECT count(*) FROM template_task_status WHERE idData=" + idData;
			Integer count = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (count == null) {
				count = 0;
			}
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public String getTemplateCreationJobStatusById(Long idData, String uniqueId) {
		return iTaskDAO.getTemplateCreationJobStatusById(idData, uniqueId);
	}

	public int getStatusOfDfReadTaskForTemplate(Long idData) {
		try {
			String sql = "SELECT status FROM template_task_status WHERE taskName='dfread' and idData=" + idData;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			int statusCount = 5;
			while (queryForRowSet.next()) {
				for (int i = 1; i <= queryForRowSet.getMetaData().getColumnCount(); i++) {
					if (queryForRowSet.getString(i).equalsIgnoreCase("passed")) {
						statusCount = 30;
					}
				}
			}
			return statusCount;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 5;
	}

	@Override
	public void insertTaskListForTemplate(Long idData, String profilingEnabled, String advancedRulesEnabled) {
		List<String> templateTasksList = new ArrayList<String>();
		templateTasksList.add("dfread");
		templateTasksList.add("sampling");
		templateTasksList.add("metarules");
		templateTasksList.add("analysis");
		templateTasksList.add("dataDefinitions");
		templateTasksList.add("validationCreation");

		if (profilingEnabled != null && profilingEnabled.equalsIgnoreCase("Y")) {
			templateTasksList.add("profilingRules1");
			
			// Only when detailed Profiling is enabled all profiles will be executed
			String detailedProfilingEnabled = appDbConnectionProperties.getProperty("detailed.profiling.enabled");
			if (detailedProfilingEnabled != null && detailedProfilingEnabled.trim().equalsIgnoreCase("Y")) {
				templateTasksList.add("profilingRules2");
				templateTasksList.add("profilingRules3");
			}
		}

		if (advancedRulesEnabled != null && advancedRulesEnabled.equalsIgnoreCase("Y")) {
			templateTasksList.add("advCorrectionRules");
			templateTasksList.add("advRangeRules");
			templateTasksList.add("advNullTargetRules");
			templateTasksList.add("advDriftRules");
			templateTasksList.add("advColumnRelationshipRules");
		}

		for (String task : templateTasksList) {
			String sql = "insert into template_task_status (idData,taskName,status) values(?,?,?)";
			jdbcTemplate1.update(sql, idData, task, "");
		}

	}
	
	@Override
	public int runRebootScript() {

		int status = 0;
		Process process = null;
		String databuckHome = DatabuckUtility.getDatabuckHome();

		String scriptLocation = databuckHome + "/scripts/restartDatabuckApp.sh";
		// Preparing command and arguments
		List<String> commandList = new ArrayList<String>();
		String cmd = "";

		String serverPath = appDbConnectionProperties.getProperty("application.server.path");
		LOG.debug("\n====>application.server.path : " + serverPath);

		if (serverPath != null && !serverPath.trim().isEmpty()) {
			commandList.add(scriptLocation);
			commandList.add(serverPath);

			cmd = scriptLocation + "  " + serverPath;

			LOG.debug("\n**** Command : " + cmd);
			try {
				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command(commandList);
				processBuilder.redirectOutput(new File(databuckHome + "/databuck_restart.log"));
				processBuilder.redirectErrorStream(true);
				process = processBuilder.start();

				// process = Runtime.getRuntime().exec(cmd);
				long pid = -1;

				try {
					if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
						Field f = process.getClass().getDeclaredField("pid");
						f.setAccessible(true);
						pid = f.getLong(process);
						f.setAccessible(false);
					}
					status = 1;

				} catch (Exception e) {
					LOG.error("\n====>Exception occurred failed to get the process Id!!");
					LOG.error(e.getMessage());
					pid = -1;
				}

				LOG.debug("\n====>Process Id: " + pid);
			} catch (Exception e1) {
				LOG.error("\n====>Exception occurred when triggering script !!!");
				LOG.error(e1.getMessage());
				e1.printStackTrace();
			}
		} else {
			LOG.error("\n====>application.server.path property is missing, cannot restart the application!!");
		}

		return status;
	}
	
	public boolean enableScheduleCheck(long idSchedule) {
		boolean result=true;
		if(iTaskDAO.enableSchedulingCheckAppGroup(idSchedule)||iTaskDAO.enableSchedulingCheckTrigger(idSchedule)) {
			result=false;
		}
		return result;
	}
}