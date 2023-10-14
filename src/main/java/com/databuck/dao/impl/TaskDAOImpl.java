package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.AppGroupJobDTO;
import com.databuck.bean.AppGroupJobQueue;
import com.databuck.bean.AppGroupMapping;
import com.databuck.bean.DataQualitySQSRequest;
import com.databuck.bean.DatabuckProperties;
import com.databuck.bean.DatabuckSNSRequest;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainJobDTO;
import com.databuck.bean.DomainJobQueue;
import com.databuck.bean.DomainLiteJobDTO;
import com.databuck.bean.DomainLiteJobQueue;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.bean.JiraIntegrationBean;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListSchedule;
import com.databuck.bean.ListTrigger;
import com.databuck.bean.LoggingActivity;
import com.databuck.bean.NotificationTopics;
import com.databuck.bean.Project;
import com.databuck.bean.ProjectJobDTO;
import com.databuck.bean.ProjectJobQueue;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobDTO;
import com.databuck.bean.SchemaJobQueue;
import com.databuck.bean.Task;
import com.databuck.bean.ValidationRunDTO;
import com.databuck.bean.LoginTrail;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import com.databuck.exception.AppGroupTriggerFailedException;
import com.databuck.exception.ConnectionTriggerFailedException;
import com.databuck.exception.DomainLiteJobTriggerFailedException;
import com.databuck.exception.ProjectTriggerFailedException;
import com.databuck.exception.TemplateTriggerFailedException;
import com.databuck.exception.ValidationTriggerFailedException;
import com.databuck.service.IProjectService;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.DateUtility;

@Repository
public class TaskDAOImpl implements ITaskDAO {
	int j = 0;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	@Autowired
	private Properties appDbConnectionProperties;
	@Autowired
	private Properties clusterProperties;

	@Autowired
	private IProjectService IProjectservice;
	@Autowired
	private DatabuckUtility databuckUtility;
	
	private static final Logger LOG = Logger.getLogger(TaskDAOImpl.class);

	public Map<Long, String> getListScheduleData(Long project_id, List<Project> projList) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
			// jdbcTemplate.queryForRowSet("select idSchedule,name from listSchedule where
			// project_id="+projectId);
			// SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet("select
			// idSchedule,name from listSchedule");
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(
					"select idSchedule,name from listSchedule where project_id in (" + listprojids + ")");
			Map<Long, String> map = new HashMap<Long, String>();
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return map;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public SqlRowSet getTriggers(Long Project_id) {
		String sql = "SELECT st.id,la.name, ls.name FROM scheduledTasks st, listSchedule ls, listApplications la "
				+ "WHERE st.idApp = la.idApp AND st.idSchedule = ls.idSchedule and st.project_id=" + Project_id;
		return jdbcTemplate.queryForRowSet(sql);

		/*
		 * Multimap<String, String> triggerData = LinkedHashMultimap.create(); try {
		 * jdbcTemplate.query("select idApp,idSchedule from scheduledTasks", new
		 * ResultSetExtractor<Multimap>() {
		 * 
		 * @Override public Multimap extractData(ResultSet rs) throws SQLException,
		 * DataAccessException { Multimap<Long, Long> mapRet =
		 * LinkedHashMultimap.create(); while (rs.next()) {
		 * mapRet.put(rs.getLong("idApp"), rs.getLong("idSchedule")); }
		 * LOG.debug(mapRet.size()); for (Entry<Long, Long> m :
		 * mapRet.entries()) { String appname = jdbcTemplate.queryForObject(
		 * "select name from listApplications where idApp=" + m.getKey(), String.class);
		 * String schedulename = jdbcTemplate.queryForObject(
		 * "select name from listSchedule where idSchedule=" + m.getValue(),
		 * String.class); triggerData.put(appname, schedulename); //
		 * LOG.debug("Key="+m.getKey()+"Value="+m.getValue()); }
		 * LOG.debug("triggerData" + triggerData.size()); return null; } }); }
		 * catch (Exception e) { e.printStackTrace(); } return triggerData;
		 */
	}

	public List<ListSchedule> getSchedulers(Long project_id, List<Project> projlst) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String Query = "select *,p.projectName from listSchedule ls join project p on ls.project_id = p.idProject where ls.project_id in ("
					+ listprojids + ")";
			LOG.debug(" getSchedulers query: " + Query);
			RowMapper<ListSchedule> rowMapper = (rs, i) -> {
				ListSchedule ls = new ListSchedule();
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setFrequency(rs.getString("frequency"));
				ls.setScheduleDay(rs.getString("scheduleDay"));
				ls.setTime(rs.getString("time"));
				ls.setProjectName(rs.getString("projectName"));

				return ls;
			};

			List<ListSchedule> listSchedule = jdbcTemplate.query(Query, rowMapper);
			return listSchedule;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int insertIntolistSchedule(String name, String description, String frequency, String scheduledDay,
			String day, String scheduleTimer, Long project_id) {

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql;
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "insert into listSchedule(time,name,description,frequency,scheduleDay,project_id,exceptionMatching) "
					+ " VALUES (?::time,?,?,?,?,?,'N')";
		} else {
			sql = "insert into listSchedule(time,name,description,frequency,scheduleDay,project_id,exceptionMatching) "
					+ " VALUES (?,?,?,?,?,?,'N')";
		}

		if (frequency.equalsIgnoreCase("weekly")) {
			if (scheduledDay.contains("All")) {
				return jdbcTemplate.update(sql, scheduleTimer, name, description, frequency, "All", project_id);
			} else {
				return jdbcTemplate.update(sql, scheduleTimer, name, description, frequency, scheduledDay, project_id);
			}
		} else
			return jdbcTemplate.update(sql, scheduleTimer, name, description, frequency, day, project_id);
	}

	public List<ListApplications> listApplicationsView(Long project_id, List<Project> projlst) {
		String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
		String sql = " SELECT name,idApp FROM listApplications WHERE active='yes'" + " and project_id in ("
				+ listprojids + ")" + " ORDER BY idApp DESC";
		// String sql = " SELECT name,idApp FROM listApplications WHERE active='yes'
		// ORDER BY idApp DESC";
		LOG.debug(sql);
		List<ListApplications> listApplications = jdbcTemplate.query(sql, new RowMapper<ListApplications>() {

			public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListApplications listApplication = new ListApplications();
				listApplication.setName(rs.getString("name"));
				listApplication.setIdApp(rs.getLong("idApp"));
				return listApplication;
			}
		});
		return listApplications;
	}

	@Override
	public int insertintoscheduledtasks(Long idApp, Long idDataSchema, Long idScheduler, Long project_id) {
		String sql = "insert into scheduledTasks(idApp,idDataSchema,idSchedule,status,project_id) "
				+ " VALUES (?,?,?,?,?)";
		return jdbcTemplate.update(sql, idApp, idDataSchema, idScheduler, "Scheduled", project_id);
	}

	@Override
	public int getCountscheduledtasks(Long idDataSchema, Long project_id) {
		String scheduleChkSql = "select count(*) from scheduledTasks where project_id=? and idDataSchema=? and runDate is null and idApp is null;";
		int count = jdbcTemplate.queryForObject(scheduleChkSql, Integer.class, project_id, idDataSchema);
		return count;
	}

	public long deleteTask(long idTask) {

		String sql = "DELETE FROM Task WHERE idTask=?";
		long count = jdbcTemplate.update(sql, idTask);
		return count;
	}

	public Task get(long idTask) {

		String sql = "SELECT idTask,taskName FROM Task WHERE idTask=" + idTask;
		return jdbcTemplate.query(sql, new ResultSetExtractor<Task>() {

			public Task extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Task task = new Task();
					task.setIdTask(rs.getLong("idTask"));
					task.setTaskName(rs.getString("taskName"));

					return task;
				}

				return null;
			}
		});
	}

	public List<Task> getData() {

		String sql = " SELECT idTask, taskName FROM Task";
		List<Task> task = jdbcTemplate.query(sql, new RowMapper<Task>() {

			public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
				Task task = new Task();

				task.setIdTask(rs.getLong("idTask"));
				task.setTaskName(rs.getString("taskName"));

				return task;
			}

		});
		return task;
	}

	@Override
	public int saveTask(String task) {

		String sql = "INSERT INTO Task (taskName,createdAt,updatedAt)" + " VALUES (?, ?, ?)";

		int count = jdbcTemplate.update(sql, task, new Date(), new Date());
		return count;
	}

	public void saveOrUpdate(Task task) {
		if (task.getIdTask() > 0) {
			// update
			String sql = "UPDATE Task SET taskName=? WHERE idTask=?";
			jdbcTemplate.update(sql, task.getTaskName(), task.getIdTask());
		} else {
			// insert
			String sql = "INSERT INTO Task (taskName,createdAt,updatedAt)" + " VALUES (?, ?, ?)";
			jdbcTemplate.update(sql, task.getTaskName(), new Date(), new Date());
		}
	}

	public String getTaskStatusFromRunScheduledTask(Long idApp, String uniqueId) {
		String status = jdbcTemplate.queryForObject(
				"select status from runScheduledTasks where idApp=" + idApp + " and uniqueId='" + uniqueId + "'",
				String.class);
		return status;
	}

	@Override
	public boolean updateRunScheduledTask(Long idApp, String status, String uniqueId) {
		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			int count = 0;
			if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("completed")
					|| status.equalsIgnoreCase("killed")) {
				String sql = "update runScheduledTasks set status=?, endTime=? where idApp=? and uniqueId=?";
				count = jdbcTemplate.update(sql, status, currentDate, idApp, uniqueId);
			} else {
				LOG.info("\n====>Changing status of validation to [" + status + "] is not allowed !!");
			}

			if (count > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean startValidationByUniqueId(Long idApp, String uniqueId) throws ValidationTriggerFailedException {
		int count = 0;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		String status = "started";

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update runScheduledTasks set status=?, triggeredByHost=?, startTime=? where idApp=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, currentDate, idApp, uniqueId);
			if (count == 0)
				throw new ValidationTriggerFailedException("Job is already started");
		} else {
			throw new ValidationTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	@Override
	public String insertRunScheduledTask(Long idApp, String status, String deployMode, String triggeredBy,
			String validationRunType) {
		String uniqueId = "";
		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			uniqueId = idApp + "_" + String.valueOf(currentDate.getTime());
			// Check if triggeredBy is empty
			if (triggeredBy == null || triggeredBy.trim().isEmpty()) {
				triggeredBy = "system";
			}

			// set default validationRunType to 'full_load'
			if (validationRunType == null
					|| !validationRunType.trim().equalsIgnoreCase(DatabuckConstants.VAL_RUN_TYPE_UNIT_TESTING)) {
				validationRunType = DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD;
			}

			jdbcTemplate.update(
					"insert into runScheduledTasks(idApp,status,deployMode,uniqueId,startTime,triggered_by,validationRunType) values(?,?,?,?,?,?,?)",
					idApp, status, deployMode, uniqueId, currentDate, triggeredBy, validationRunType);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public String addIncrementalFileValidationToQueue(Long idApp, String incrementalFileName) {
		String uniqueId = "";
		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			uniqueId = idApp + "_" + String.valueOf(currentDate.getTime());
			// Check if triggeredBy is empty
			String triggeredBy = "system";
			// set default validationRunType to 'full_load'
			String validationRunType = DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD;
			String deployMode = clusterProperties.getProperty("deploymode");

			if (deployMode.trim().equalsIgnoreCase("2")) {
				deployMode = "local";
			} else {
				deployMode = "cluster";
			}
			String status = "queued";
			jdbcTemplate.update(
					"insert into runScheduledTasks(idApp,status,deployMode,uniqueId,startTime,triggered_by,validationRunType,incremental_file_name) values(?,?,?,?,?,?,?,?)",
					idApp, status, deployMode, uniqueId, currentDate, triggeredBy, validationRunType,
					incrementalFileName);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	public String getTypeOfApplication(long idApp) {
		return jdbcTemplate.queryForObject("select appType from listApplications where idApp=?", String.class, idApp);
	}

	public void insertIntoSub_Task_Status(Long idApp) {
		String sql = "insert into sub_task_status(idapp) values(" + idApp + ")";
		jdbcTemplate1.update(sql);
	}

	public void deleteFromScheduledTasks(Long id) {
		String sql = "DELETE FROM scheduledTasks WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public void deleteFromListSchedule(Long idSchedule) {
		String sql = "DELETE FROM scheduledTasks WHERE idSchedule=?";
		jdbcTemplate.update(sql, idSchedule);

		sql = "DELETE FROM listSchedule WHERE idSchedule=?";
		jdbcTemplate.update(sql, idSchedule);
	}

	public boolean enableSchedulingCheckAppGroup(long idSchedule) {
		boolean result = true;
		List<Map<String, Object>> appGroupList = null;
		String sql = "select idSchedule from listAppGroup where idSchedule=?";
		try {
			appGroupList = jdbcTemplate.queryForList(sql, idSchedule);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
		}
		if (appGroupList.isEmpty()) {
			result = false;
		}
		return result;
	}

	public boolean enableSchedulingCheckTrigger(long idSchedule) {
		boolean result = true;
		List<Map<String, Object>> triggerList = null;
		String sql = "select idSchedule from scheduledTasks where idSchedule=?";
		try {
			triggerList = jdbcTemplate.queryForList(sql, idSchedule);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
		}
		if (triggerList.isEmpty()) {
			result = false;
		}
		return result;
	}

	public boolean insertIntoRunningtaskStatus(String status, Long idApp) {
		try {
			jdbcTemplate.execute("insert into runningtaskStatus(status,idApp, start_at) values('" + status + "',"
					+ idApp + ",current_timestamp())");
			return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public boolean updateRunningtaskStatus(String status, Long idApp) {
		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "update runningtaskStatus set status='started' where idApp=" + idApp
						+ " and id in (select id from runningtaskStatus where idApp=" + idApp
						+ " order by start_at desc limit 1)";
			} else {
				sql = "update runningtaskStatus set status='" + status + "' where idApp=" + idApp
						+ " order by start_at desc Limit 1";
			}
			jdbcTemplate.execute(sql);
			return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public String getSchemaNameFolderNameListDataAccess(Long idApp) {
		String sql = "select schemaName, folderName from listDataAccess where idData= (Select idData from listApplications where idApp ="
				+ idApp + ")";
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		String schemaName = "";
		String folderName = "";
		while (queryForRowSet.next()) {
			schemaName = queryForRowSet.getString("schemaName");
			folderName = queryForRowSet.getString("folderName");

		}
		return schemaName + ";" + folderName;
	}

	public boolean recordExistInRunningtaskStatus(Long idApp) {
		Map rs = jdbcTemplate.queryForMap("select * from runningtaskStatus where idApp=? Limit 1", idApp);
		if (rs.size() >= 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getTemplateCreationJobStatusById(Long idData, String uniqueId) {
		String status = "";
		try {
			String sql = "select status from runTemplateTasks where idData=? and uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, idData, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception Occurred in getTemplateCreationJobStatusById for IdData:" + idData);
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String placeTemplateJobInQueue(Long idData, String templateRunType) {
		String uniqueId = "";
		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			uniqueId = "Templ_" + idData + "_" + String.valueOf(currentDate.getTime());

			// Get deployMode
			String deployMode = clusterProperties.getProperty("deploymode");

			if (deployMode.trim().equalsIgnoreCase("2")) {
				deployMode = "local";
			} else {
				deployMode = "cluster";
			}

			String sql = "insert into runTemplateTasks(idData,uniqueId,templateRunType,status,deployMode,startTime) values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql, idData, uniqueId, templateRunType, "queued", deployMode, currentDate);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return uniqueId;
	}

	@Override
	public boolean updateTemplateCreationJobStatus(Long idData, String uniqueId, String status) {
		try {
			String sql = "";
			int count = 0;
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("completed")
					|| status.equalsIgnoreCase("killed")) {
				sql = "update runTemplateTasks set status=?, endTime=? where idData=? and uniqueId=?";
				count = jdbcTemplate.update(sql, status, currentDate, idData, uniqueId);
			} else {
				LOG.info("\n====>Changing status of template to [" + status + "] is not allowed !!");
			}
			if (count > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean startTemplateByUniqueId(Long idData, String uniqueId) throws TemplateTriggerFailedException {
		int count = 0;
		String status = "started";

		// Start Time

		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update runTemplateTasks set status=?, triggeredByHost=?, startTime=? where idData=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, currentDate, idData, uniqueId);
			if (count == 0)
				throw new TemplateTriggerFailedException("Job is already started");
		} else {
			throw new TemplateTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	@Override
	public void updateTemplateCreationJobPid(long idData, String uniqueId, long pid) {
		try {
			LOG.info("\n====>Inside updateTemplateCreationJobPid ...");
			String sql = "update runTemplateTasks set processId=? where idData=? and uniqueId=?";
			jdbcTemplate.update(sql, pid, idData, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateRunScheduledTaskPid(Long idApp, long pid, String uniqueId) {
		try {
			LOG.info("\n====>Inside updateRunScheduledTaskPid ...");
			String sql = "update runScheduledTasks set processId=? where idApp=? and uniqueId=?";
			jdbcTemplate.update(sql, pid, idApp, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<RunningTaskDTO> getRunningTemplateJobList(List<Project> projList) {
		List<RunningTaskDTO> templateTaskResults = new ArrayList<RunningTaskDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select t1.*, t2.name as applicationName, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runTemplateTasks t1 ";
			sql = sql + "left outer join listDataSources t2 on t1.idData=t2.idData ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idData!=0 and t1.status in ('started','in progress') ";
			sql = sql + "and t2.project_id in ( " + projIds + ") limit 500";
			LOG.debug("Sql: " + sql);

			// Fetch KillTime
			String killTimeParameter = appDbConnectionProperties.getProperty("killTime");

			long killTimeInMillisecs = 0;
			if (killTimeParameter != null) {
				try {
					String[] hourAndMins = killTimeParameter.trim().split(":");
					int killHour = Integer.parseInt(hourAndMins[0]);
					int killMins = Integer.parseInt(hourAndMins[1]);
					killTimeInMillisecs = killHour * 60 * 60 * 1000 + killMins * 60 * 1000;
				} catch (Exception e) {
					LOG.error(
							"\n=====>Exception occurred while fetching killTime!! Hence setting the default value to 1!!");
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
			}

			final long overTimeLimit = (killTimeInMillisecs > 0) ? killTimeInMillisecs : (60 * 60 * 1000);
			LOG.debug("\n====>OverTime Limit:" + overTimeLimit);

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Template");
				runningTaskDTO.setApplicationId(rs.getLong("idData"));
				runningTaskDTO.setApplicationName(rs.getString("applicationName"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				runningTaskDTO.setTriggeredByHost(rs.getString("triggeredByHost"));

				Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					duration = Math.abs(currentDate.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				runningTaskDTO.setDuration(duration);
				runningTaskDTO.setFullDuration(fullDuration);

				String jobDurationStatus = "";
				if (duration >= overTimeLimit) {
					jobDurationStatus = "OVERTIME";
				}
				runningTaskDTO.setJobDurationStatus(jobDurationStatus);
				runningTaskDTO.setProjectId(rs.getLong("projectId"));
				runningTaskDTO.setProjectName(rs.getString("projectName"));

				return runningTaskDTO;
			};
			templateTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting Template Tasks!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return templateTaskResults;
	}

	@Override
	public List<RunningTaskDTO> getRunningValidationJobList(List<Project> projList) {
		List<RunningTaskDTO> validationTaskResults = new ArrayList<RunningTaskDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select t1.*, t2.name as applicationName, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runScheduledTasks t1 ";
			sql = sql + "left outer join listApplications t2 on t1.idApp=t2.idApp ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idApp!=0 and t1.status in ('started','in progress') ";
			sql = sql + "and t2.project_id in ( " + projIds + ") limit 500";
			LOG.debug("Sql: " + sql);

			// Fetch KillTime
			String killTimeParameter = appDbConnectionProperties.getProperty("killTime");

			long killTimeInMillisecs = 0;
			if (killTimeParameter != null) {
				try {
					String[] hourAndMins = killTimeParameter.trim().split(":");
					int killHour = Integer.parseInt(hourAndMins[0]);
					int killMins = Integer.parseInt(hourAndMins[1]);
					killTimeInMillisecs = killHour * 60 * 60 * 1000 + killMins * 60 * 1000;
				} catch (Exception e) {
					LOG.error(
							"\n=====>Exception occurred while fetching killTime!! Hence setting the default value to 1!!");
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
			}

			final long overTimeLimit = (killTimeInMillisecs > 0) ? killTimeInMillisecs : (60 * 60 * 1000);
			LOG.debug("\n====>OverTime Limit:" + overTimeLimit);

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Validation");
				runningTaskDTO.setApplicationId(rs.getLong("idApp"));
				runningTaskDTO.setApplicationName(rs.getString("applicationName"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				runningTaskDTO.setTriggeredByHost(rs.getString("triggeredByHost"));

				Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				// Calculate duration
				long duration = 0l;
				String fullDuration = "";
				Date currentTime = DateUtility
						.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

				if (startTime != null) {
					duration = Math.abs(currentTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				runningTaskDTO.setDuration(duration);
				runningTaskDTO.setFullDuration(fullDuration);

				String jobDurationStatus = "";
				if (duration >= overTimeLimit) {
					jobDurationStatus = "OVERTIME";
				}
				runningTaskDTO.setJobDurationStatus(jobDurationStatus);
				runningTaskDTO.setProjectId(rs.getLong("projectId"));
				runningTaskDTO.setProjectName(rs.getString("projectName"));

				return runningTaskDTO;
			};
			validationTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting validation Tasks!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationTaskResults;
	}

	@Override
	public void killRunScheduledTask(long taskId, String uniqueId) {
		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			jdbcTemplate.update("update runScheduledTasks set status='killed', endTime=? where idApp=? and uniqueId=?",
					currentDate, taskId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int getRunningTasksCount() {
		int activeJobCount = 0;
		try {
			String sql = "select sum(t.jobCount) as activeJobCount from ((select count(*) as jobCount from runTemplateTasks where idData!=0 and status in ('started','in progress')) union (select count(*) as jobCount from runScheduledTasks where idApp!=0 and status in ('started','in progress'))) t;";

			activeJobCount = jdbcTemplate.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching list of active jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return activeJobCount;
	}

	@Override
	public List<RunningTaskDTO> getJobsInQueue() {
		List<RunningTaskDTO> validationTaskResults = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select t.taskType, t.applicationId, t.startTime, t.uniqueId from ( (select 'template' as taskType, idData as applicationId, startTime, uniqueId from runTemplateTasks where idData!=0 and status='queued') union (select 'validation' as taskType, idApp as applicationId,startTime,uniqueId from runScheduledTasks where idApp!=0 and status='queued') ) t order by t.startTime;";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType(rs.getString("taskType"));
				runningTaskDTO.setApplicationId(rs.getLong("applicationId"));
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};
			validationTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting list of jobs queued!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationTaskResults;
	}

	@Override
	public String getDataLocationForIdData(long idData) {
		String dataLocation = null;
		try {
			String sql = "SELECT dataLocation from listDataSources where idData =" + idData;
			dataLocation = jdbcTemplate.queryForObject(sql, String.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			
		}
		return dataLocation;
	}

	@Override
	public void deleteValidationJobFromQueue(long taskId, String uniqueId) {
		try {
			LOG.info("\n====>Inside deleteValidationJobFromQueue ...");
			String sql = "delete from runScheduledTasks where status='queued' and idApp=? and uniqueId=?";
			jdbcTemplate.update(sql, taskId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void deleteTemplateJobFromQueue(long taskId, String uniqueId) {
		try {
			LOG.info("\n====>Inside deleteTemplateJobFromQueue ...");
			String sql = "delete from runTemplateTasks where status='queued' and idData=? and uniqueId=?";
			jdbcTemplate.update(sql, taskId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<RunningTaskDTO> getQueuedTemplateJobList(List<Project> projList) {
		List<RunningTaskDTO> templateTaskResults = new ArrayList<RunningTaskDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select t1.*, t2.name as applicationName, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runTemplateTasks t1 ";
			sql = sql + "left outer join listDataSources t2 on t1.idData=t2.idData ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idData!=0 and t1.status = 'queued' ";
			sql = sql + "and t2.project_id in ( " + projIds + ") limit 500";
			
			LOG.debug("sql "+sql);

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Template");
				runningTaskDTO.setApplicationId(rs.getLong("idData"));
				runningTaskDTO.setApplicationName(rs.getString("applicationName"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null) {

					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					long diffInMillies = Math.abs(currentDate.getTime() - startTime.getTime());
					duration = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

					long diffMinutes = diffInMillies / (60 * 1000) % 60;
					long diffHours = diffInMillies / (60 * 60 * 1000) % 24;
					long diffDays = diffInMillies / (24 * 60 * 60 * 1000);

					if (diffDays > 0) {
						fullDuration = diffDays + " Days   ";
					}

					if (diffHours > 0) {
						fullDuration = fullDuration + diffHours + " Hrs   ";
					}

					if (diffMinutes > 0) {
						fullDuration = fullDuration + diffMinutes + " Mins";
					}
				}
				runningTaskDTO.setDuration(duration);
				runningTaskDTO.setFullDuration(fullDuration);
				runningTaskDTO.setProjectId(rs.getLong("projectId"));
				runningTaskDTO.setProjectName(rs.getString("projectName"));

				return runningTaskDTO;
			};
			templateTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting Template Tasks!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return templateTaskResults;
	}

	@Override
	public List<RunningTaskDTO> getQueuedValidationJobList(List<Project> projList) {
		List<RunningTaskDTO> validationTaskResults = new ArrayList<RunningTaskDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select t1.*, t2.name as applicationName, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runScheduledTasks t1 ";
			sql = sql + "left outer join listApplications t2 on t1.idApp=t2.idApp ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idApp!=0 and t1.status = 'queued' ";
			sql = sql + "and t2.project_id in ( " + projIds + ") limit 500";
			
			LOG.debug("sql "+sql);

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Validation");
				runningTaskDTO.setApplicationId(rs.getLong("idApp"));
				runningTaskDTO.setApplicationName(rs.getString("applicationName"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					long diffInMillies = Math.abs(currentDate.getTime() - startTime.getTime());

					long diffMinutes = diffInMillies / (60 * 1000) % 60;
					long diffHours = diffInMillies / (60 * 60 * 1000) % 24;
					long diffDays = diffInMillies / (24 * 60 * 60 * 1000);

					if (diffDays > 0) {
						fullDuration = diffDays + " Days   ";
					}

					if (diffHours > 0) {
						fullDuration = fullDuration + diffHours + " Hrs   ";
					}

					if (diffMinutes > 0) {
						fullDuration = fullDuration + diffMinutes + " Mins";
					}

					duration = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
				}

				runningTaskDTO.setDuration(duration);
				runningTaskDTO.setFullDuration(fullDuration);
				runningTaskDTO.setProjectId(rs.getLong("projectId"));
				runningTaskDTO.setProjectName(rs.getString("projectName"));

				return runningTaskDTO;
			};
			validationTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting validation Tasks!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationTaskResults;
	}

	@Override
	public void deleteAllQueuedTemplateJobs() {
		try {
			LOG.info("\n====>Inside deleteAllQueuedTemplateJobs ...");
			String sql = "delete from runTemplateTasks where status='queued'";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAllQueuedValidationJobs() {
		try {
			LOG.info("\n====>Inside deleteAllQueuedValidationJobs ...");
			String sql = "delete from runScheduledTasks where status='queued'";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<Long> getValditionsForSchedule(Long idScheduler) {
		List<Long> idAppList = new ArrayList<Long>();
		try {
			String sql = "Select idApp from scheduledTasks where idSchedule =?";
			List<Long> resultList = jdbcTemplate.queryForList(sql, Long.class, idScheduler);
			if (resultList != null && resultList.size() > 0) {
				idAppList = resultList;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idAppList;
	}

	@Override
	public ListSchedule getSchedulerById(long idSchedule) {
		ListSchedule schedule = new ListSchedule();
		try {
			String Query = "select * from listSchedule where idSchedule=?";
			RowMapper<ListSchedule> rowMapper = (rs, i) -> {
				ListSchedule ls = new ListSchedule();
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setFrequency(rs.getString("frequency"));
				ls.setScheduleDay(rs.getString("scheduleDay"));
				ls.setTime(rs.getString("time"));
				ls.setProjectId(rs.getLong("project_id"));
				ls.setDomainId(rs.getLong("domain_id"));
				return ls;
			};
			List<ListSchedule> listSchedule = jdbcTemplate.query(Query, rowMapper, idSchedule);
			if (listSchedule != null && listSchedule.size() > 0) {
				schedule = listSchedule.get(0);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return schedule;
	}

	@Override
	public int updateListSchedule(long idSchedule, String name, String description, String frequency,
			String scheduledDay, String day, String scheduleTimer) {
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql;
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "update listSchedule set time=?::time,name=?,description=?,frequency=?,scheduleDay=? where idSchedule=?";
		} else {
			sql = "update listSchedule set time=?,name=?,description=?,frequency=?,scheduleDay=? where idSchedule=?";
		}

		String s_Day = scheduledDay;

		if (frequency.equalsIgnoreCase("weekly")) {
			if (scheduledDay.contains("All")) {
				s_Day = "All";
			}
		} else {
			s_Day = day;
		}

		return jdbcTemplate.update(sql, scheduleTimer, name, description, frequency, s_Day, idSchedule);
	}

	@Override
	public long insertIntolistAppGroup(String name, String description, String schedulerEnabled, Long idScheduler,
			Long projectId, String idAppList) {
		boolean result = false;
		Long idAppGroup=0l;
		try {
			String sql = "Insert into listAppGroup(name,description,project_id,enableScheduling,idSchedule) values(?,?,?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idappgroup"
					: "idAppGroup";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setString(1, name);
					pst.setString(2, description);
					pst.setLong(3, projectId);
					pst.setString(4, schedulerEnabled);
					pst.setObject(5, idScheduler);
					return pst;
				}
			}, keyHolder);

			 idAppGroup = keyHolder.getKey().longValue();
			if (idAppGroup != null && idAppGroup != 0l) {
				result = true;

				// Check if enableScheduling is 'Y'
				boolean enableSchedule = false;
				if (schedulerEnabled != null && schedulerEnabled.equalsIgnoreCase("Y") && idScheduler != null) {
					enableSchedule = true;
				}

				String[] idApps = idAppList.split(",");
				for (String idAppStr : idApps) {
					long appId = Long.parseLong(idAppStr);

					// Insert appGroup Mapping
					String appMapSql = "Insert into appGroupMapping(idAppGroup,idApp) values(?,?)";
					jdbcTemplate.update(appMapSql, idAppGroup, appId);

					if (enableSchedule) {
						// Check if the validation and schedule combination exists
						String scheduleChkSql = "select count(*) from scheduledTasks where idSchedule=? and idApp=?";
						int count = jdbcTemplate.queryForObject(scheduleChkSql, Integer.class, idScheduler, appId);

						// Make entry to scheduled tasks
						if (count == 0) {
							insertintoscheduledtasks(appId, null, idScheduler, projectId);
						}
					}
				}

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idAppGroup;
	}

	@Override
	public List<ListAppGroup> getAppGroupsForProject(Long projectId, List<Project> projlst) {
		List<ListAppGroup> appGroupList = null;
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
		try {
			//DC 2850 
			String Query = "select t1.*,t2.frequency,t2.scheduleDay,t2.time ,p.projectName from listAppGroup t1 left outer join listSchedule t2 on t1.idSchedule= t2.idSchedule "
					+ "join project p on  t1.project_id = p.idProject where p.idProject in (" + projIds + ")" + " ORDER BY t1.idAppGroup DESC";
			LOG.debug("getAppGroupsForProject :  " + Query);
			RowMapper<ListAppGroup> rowMapper = (rs, i) -> {
				ListAppGroup ls = new ListAppGroup();
				ls.setIdAppGroup(rs.getLong("idAppGroup"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setEnableScheduling(rs.getString("enableScheduling"));
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setFrequency(rs.getString("frequency"));
				ls.setScheduleDay(rs.getString("scheduleDay"));
				ls.setTime(rs.getString("time"));
				ls.setProjectName(rs.getString("projectName"));
				return ls;
			};
			appGroupList = jdbcTemplate.query(Query, rowMapper);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return appGroupList;
	}

	@Override
	public List<AppGroupMapping> getApplicationMappingForGroup(Long idAppGroup) {
		List<AppGroupMapping> appGroupMappings = null;
		try {
			String Query = "select t1.idAppGroupMapping, t2.idApp, t2.name from appGroupMapping t1 join listApplications t2 on t1.idApp=t2.idApp where t1.idAppGroup=?";
			RowMapper<AppGroupMapping> rowMapper = (rs, i) -> {
				AppGroupMapping appgroupMap = new AppGroupMapping();
				appgroupMap.setIdAppGroupMapping(rs.getLong("idAppGroupMapping"));
				appgroupMap.setAppId(rs.getLong("idApp"));
				appgroupMap.setAppName(rs.getString("name"));
				return appgroupMap;
			};
			appGroupMappings = jdbcTemplate.query(Query, rowMapper, idAppGroup);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return appGroupMappings;
	}

	@Override
	public void deleteAppGroupById(Long idAppGroup) {
		try {
			// Get the AppIds list
			String sql = "select idApp from appGroupMapping where idAppGroup=?";
			List<Long> appIds = jdbcTemplate.queryForList(sql, Long.class, idAppGroup);

			String idAppList = "";
			if (appIds != null && appIds.size() > 0) {
				for (long idApp : appIds) {
					idAppList = idAppList + idApp + ",";
				}
				idAppList = idAppList.substring(0, idAppList.length() - 1);
			}

			// Check if scheduling is enabled and get the scheduler id
			String enabledScheduling = "";
			Long idSchedule = null;

			sql = "select enableScheduling,idSchedule from listAppGroup where idAppGroup=?";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, idAppGroup);

			while (sqlRowSet.next()) {
				enabledScheduling = sqlRowSet.getString("enableScheduling");
				idSchedule = sqlRowSet.getLong("idSchedule");
			}

			if (enabledScheduling != null && enabledScheduling.equalsIgnoreCase("Y") && idSchedule != null
					&& appIds != null && appIds.size() > 0) {

				for (long idApp : appIds) {
					// Get the list of AppGroups which has same schedulerId and same App mapping
					sql = "select count(*) as groupCount from appGroupMapping where idAppGroup in (select idAppGroup from listAppGroup where idSchedule=? and idAppGroup!=?) and idApp=?";
					int appUsed_appsCount = jdbcTemplate.queryForObject(sql, Integer.class, idSchedule, idAppGroup,
							idApp);

					// Delete the mapping in scheduledTasks only when it not used by other AppGroups
					if (appUsed_appsCount == 0) {
						sql = "delete from scheduledTasks where idSchedule=? and idApp=?";
						jdbcTemplate.update(sql, idSchedule, idApp);
					}
				}

			}

			// delete the mappings
			sql = "delete from appGroupMapping where idAppGroup=?";
			jdbcTemplate.update(sql, idAppGroup);

			// delete the appGroup
			sql = "delete from listAppGroup where idAppGroup=?";
			jdbcTemplate.update(sql, idAppGroup);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAppGroupMapping(Long idAppGroupMapping) {
		try {
			// Get the AppId and idAppGroup
			Long idAppGroup = null;
			Long idApp = null;

			String sql = "select idAppGroup, idApp from appGroupMapping where idAppGroupMapping=?";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, idAppGroupMapping);

			while (sqlRowSet.next()) {
				idAppGroup = sqlRowSet.getLong("idAppGroup");
				idApp = sqlRowSet.getLong("idApp");
			}

			// Check if scheduling is enabled and get the scheduler id
			String enabledScheduling = "";
			Long idSchedule = null;

			sql = "select enableScheduling,idSchedule from listAppGroup where idAppGroup=?";
			SqlRowSet app_sqlRowSet = jdbcTemplate.queryForRowSet(sql, idAppGroup);

			while (app_sqlRowSet.next()) {
				enabledScheduling = app_sqlRowSet.getString("enableScheduling");
				idSchedule = app_sqlRowSet.getLong("idSchedule");
			}

			if (enabledScheduling != null && enabledScheduling.equalsIgnoreCase("Y") && idSchedule != null) {
				// Get the list of AppGroups which has same schedulerId and same App mapping
				sql = "select count(*) as groupCount from appGroupMapping where idAppGroup in (select idAppGroup from listAppGroup where idSchedule=? and idAppGroup!=?) and idApp=?";
				int appUsed_appsCount = jdbcTemplate.queryForObject(sql, Integer.class, idSchedule, idAppGroup, idApp);

				// Delete the mapping in scheduledTasks only when it not used by other AppGroups
				if (appUsed_appsCount == 0) {
					sql = "delete from scheduledTasks where idSchedule=? and idApp=?";
					jdbcTemplate.update(sql, idSchedule, idApp);
				}
			}

			// delete the mapping
			sql = "delete from appGroupMapping where idAppGroupMapping=?";
			jdbcTemplate.update(sql, idAppGroupMapping);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getApprovedValidationNamesListForProject(Long projectId) {
		List<String> validationNames = new ArrayList<String>();
		try {
			String sql = "select la.idApp,la.name from listApplications la join (select t1.row_id,t1.element_reference from app_option_list_elements t1 join app_option_list t2 on t1.elements2app_list=t2.row_id where t1.element_reference='"
					+ DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2
					+ "' and t2.list_reference='DQ_RULE_CATALOG_STATUS') t3 on la.approve_status=t3.row_id where la.active='yes' and la.project_id="
					+ projectId + " ORDER BY la.idApp DESC";

			validationNames = jdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					List<String> validationNameData = new ArrayList<String>();
					validationNameData.add(rs.getInt("idApp") + "-" + rs.getString("name"));
					return validationNameData.toString();
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationNames;
	}

	@Override
	public ListAppGroup getListAppGroupById(Long idAppGroup) {
		ListAppGroup listAppGroup = null;
		try {
			String Query = "select * from listAppGroup where idAppGroup=?";
			RowMapper<ListAppGroup> rowMapper = (rs, i) -> {
				ListAppGroup ls = new ListAppGroup();
				ls.setIdAppGroup(rs.getLong("idAppGroup"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setEnableScheduling(rs.getString("enableScheduling"));
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setProjectId(rs.getLong("project_id"));
				return ls;
			};
			List<ListAppGroup> appGroupList = jdbcTemplate.query(Query, rowMapper, idAppGroup);
			if (appGroupList != null && appGroupList.size() > 0) {
				listAppGroup = appGroupList.get(0);
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return listAppGroup;
	}

	@Override
	public boolean updateIntolistAppGroup(Long idAppGroup, String name, String description, String schedulerEnabled,
			Long idScheduler, Long projectId, String idAppList) {
		boolean result = false;
		try {
			// Get the old details of AppGroup
			ListAppGroup listAppGroup = getListAppGroupById(idAppGroup);

			// Get the old appGroupMapping list
			String sql = "select idApp from appGroupMapping where idAppGroup=?";
			List<Long> oldAppGroupMappings = jdbcTemplate.queryForList(sql, Long.class, idAppGroup);

			sql = "update listAppGroup set name=?, description=? ,project_id=? ,enableScheduling=? ,idSchedule=? where idAppGroup=?";
			int count = jdbcTemplate.update(sql, name, description, projectId, schedulerEnabled, idScheduler,
					idAppGroup);

			// Delete the old scheduler mapping
			if (listAppGroup.getEnableScheduling() != null
					&& listAppGroup.getEnableScheduling().equalsIgnoreCase("Y")) {
				long oldIdScheduler = listAppGroup.getIdSchedule();

				if (oldAppGroupMappings != null && oldAppGroupMappings.size() > 0) {
					for (Long old_idApp : oldAppGroupMappings) {
						// Get the list of AppGroups which has same schedulerId and same App mapping
						sql = "select count(*) as groupCount from appGroupMapping where idAppGroup in (select idAppGroup from listAppGroup where idSchedule=? and idAppGroup!=?) and idApp=?";
						int appUsed_appsCount = jdbcTemplate.queryForObject(sql, Integer.class, oldIdScheduler,
								idAppGroup, old_idApp);

						// Delete the mapping in scheduledTasks only when it not used by other AppGroups
						if (appUsed_appsCount == 0) {
							sql = "delete from scheduledTasks where idSchedule=? and idApp=?";
							jdbcTemplate.update(sql, oldIdScheduler, old_idApp);
						}
					}

				}
			}
			// Check if enableScheduling is 'Y'
			boolean enableSchedule = false;
			if (schedulerEnabled != null && schedulerEnabled.equalsIgnoreCase("Y") && idScheduler != null) {
				enableSchedule = true;
			}

			// Delete appGroup Mapping
			String delete_AppMapSql = "delete from appGroupMapping where idAppGroup=?";
			jdbcTemplate.update(delete_AppMapSql, idAppGroup);

			// New appGroupMapping list
			String[] idApps = idAppList.split(",");

			for (String idAppStr : idApps) {
				long appId = Long.parseLong(idAppStr);

				// Insert appGroup Mapping
				String appMapSql = "Insert into appGroupMapping(idAppGroup,idApp) values(?,?)";
				jdbcTemplate.update(appMapSql, idAppGroup, appId);

				if (enableSchedule) {
					// Check if the validation and schedule combination exists
					String scheduleChkSql = "select count(*) from scheduledTasks where idSchedule=? and idApp=?";
					count = jdbcTemplate.queryForObject(scheduleChkSql, Integer.class, idScheduler, appId);

					// Make entry to scheduled tasks
					if (count == 0) {
						insertintoscheduledtasks(appId, null, idScheduler, projectId);
					}
				}

				result = true;

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Long> getValidationsOfAppGroup(Long idAppGroup) {
		List<Long> appIds = null;
		try {
			String Query = "select idApp from appGroupMapping where idAppGroup=?";
			appIds = jdbcTemplate.queryForList(Query, Long.class, idAppGroup);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return appIds;
	}

	@Override
	public SqlRowSet getValidationTriggers(Long Project_id, List<Project> projlst) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String sql = "SELECT st.id,la.name, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listApplications la ,project p"
					+ " WHERE st.idApp = la.idApp AND st.idSchedule = ls.idSchedule and st.idApp is not null and st.project_id in ("
					+ listprojids + ") and ls.project_id = p.idProject";
			LOG.debug("getValidationTriggers :" + sql);
			return jdbcTemplate.queryForRowSet(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<ListTrigger> getValidationTriggersList(Long project_id, List<Project> projlst) {
		try {
			// String listprojids =
			// IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String listprojids = project_id.toString();
			String sql = "SELECT st.id,la.name, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listApplications la ,project p"
					+ " WHERE st.idApp = la.idApp AND st.idSchedule = ls.idSchedule and st.idApp is not null and st.project_id in ("
					+ listprojids + ") and ls.project_id = p.idProject";
			LOG.debug("getValidationTriggers :" + sql);
			RowMapper<ListTrigger> rowMapper = (rs, i) -> {
				ListTrigger lt = new ListTrigger();
				lt.setIdTrigger(rs.getLong(1));
				lt.setValidationCheck(rs.getString(2));
				lt.setScheduleName(rs.getString(3));
				lt.setProjectName(rs.getString(4));

				return lt;
			};

			List<ListTrigger> listTrigger = jdbcTemplate.query(sql, rowMapper);
			return listTrigger;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<ListTrigger> getValidationTriggersListProjectName(Long project_id, List<Project> projlst) {
		try {
			// String listprojids =
			// IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String listprojids = project_id.toString();
			String sql = "SELECT st.id,la.name, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listApplications la ,project p"
					+ " WHERE st.idApp = la.idApp AND st.idSchedule = ls.idSchedule and st.idApp is not null and st.project_id in ("
					+ listprojids + ") and st.project_id = p.idProject";
			LOG.debug("getValidationTriggers :" + sql);
			RowMapper<ListTrigger> rowMapper = (rs, i) -> {
				ListTrigger lt = new ListTrigger();
				lt.setIdTrigger(rs.getLong(1));
				lt.setValidationCheck(rs.getString(2));
				lt.setScheduleName(rs.getString(3));
				lt.setProjectName(rs.getString(4));

				return lt;
			};

			List<ListTrigger> listTrigger = jdbcTemplate.query(sql, rowMapper);
			return listTrigger;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SqlRowSet getSchemaTriggers(Long Project_id, List<Project> projlst) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String sql = "SELECT st.id,la.schemaName, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listDataSchema la ,project p"
					+ " WHERE st.idDataSchema = la.idDataSchema AND st.idSchedule = ls.idSchedule and st.idDataSchema is not null and st.project_id in ("
					+ listprojids + ")and ls.project_id = p.idProject";
			LOG.debug("getSchemaTriggers :" + sql);
			return jdbcTemplate.queryForRowSet(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<ListTrigger> getSchemaTriggersList(Long project_id, List<Project> projlst) {
		try {
			// String listprojids =
			// IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String listprojids = project_id.toString();
			String sql = "SELECT st.id,la.schemaName, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listDataSchema la ,project p"
					+ " WHERE st.idDataSchema = la.idDataSchema AND st.idSchedule = ls.idSchedule and st.idDataSchema is not null and st.project_id in ("
					+ listprojids + ")and ls.project_id = p.idProject";
			LOG.debug("getSchemaTriggers :" + sql);
			RowMapper<ListTrigger> rowMapper = (rs, i) -> {
				ListTrigger lt = new ListTrigger();
				lt.setIdTrigger(rs.getLong(1));
				lt.setSchemaName(rs.getString(2));
				lt.setScheduleName(rs.getString(3));
				lt.setProjectName(rs.getString(4));

				return lt;
			};

			List<ListTrigger> listTrigger = jdbcTemplate.query(sql, rowMapper);
			return listTrigger;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ListTrigger> getSchemaTriggersListWithDomain(Long project_id, Long domainId) {
		try {
			// String listprojids =
			// IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
			String listprojids = project_id.toString();
			String sql = "SELECT st.id,la.schemaName, ls.name ,p.projectName FROM scheduledTasks st, listSchedule ls, listDataSchema la ,project p"
					+ " WHERE st.idDataSchema = la.idDataSchema AND st.idSchedule = ls.idSchedule and st.idDataSchema is not null and st.project_id in ("
					+ listprojids
					+ ")and p.idProject=(select project_id from domain_to_project where project_id in ( "
					+ listprojids + ") and domain_id in (" + domainId + "))";
			LOG.debug("getSchemaTriggers :" + sql);
			RowMapper<ListTrigger> rowMapper = (rs, i) -> {
				ListTrigger lt = new ListTrigger();
				lt.setIdTrigger(rs.getLong(1));
				lt.setSchemaName(rs.getString(2));
				lt.setScheduleName(rs.getString(3));
				lt.setProjectName(rs.getString(4));

				return lt;
			};

			List<ListTrigger> listTrigger = jdbcTemplate.query(sql, rowMapper);
			return listTrigger;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<RunningTaskDTO> getCompletedTemplateValidationJobList(List<Project> projList) {
		List<RunningTaskDTO> templateTaskResults = new ArrayList<RunningTaskDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select * from ( ";
			sql = sql
					+ "(select 'Template' as taskType, t1.idData as applicationId, t2.name as applicationName, t1.uniqueId, t1.processId, ";
			sql = sql
					+ "t1.deployMode, t1.sparkAppId, t1.startTime, t1.endTime, t1.status, t1.triggeredByHost, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runTemplateTasks t1 ";
			sql = sql + "left outer join listDataSources t2 on t1.idData=t2.idData ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idData!=0 and t1.status in ('completed','failed','killed') ";
			sql = sql + "and t2.project_id in ( " + projIds + ")";
			sql = sql + "order by t1.endTime desc limit 500 ) ";
			sql = sql + "union ";
			sql = sql
					+ "(select 'Validation' as taskType, t1.idApp as applicationId, t2.name as applicationName, t1.uniqueId, t1.processId, ";
			sql = sql
					+ "t1.deployMode, t1.sparkAppId, t1.startTime, t1.endTime, t1.status, t1.triggeredByHost, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from runScheduledTasks t1 ";
			sql = sql + "left outer join listApplications t2 on t1.idApp=t2.idApp ";
			sql = sql + "left outer join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t1.idApp!=0 and t1.status in ('completed','failed','killed') ";
			sql = sql + "and t2.project_id in ( " + projIds + ")";
			sql = sql + "order by t1.endTime desc limit 500 )";
			sql = sql + ") a order by endTime desc limit 500";
			LOG.debug("Sql: " + sql);

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType(rs.getString("taskType"));
				runningTaskDTO.setApplicationId(rs.getLong("applicationId"));
				runningTaskDTO.setApplicationName(rs.getString("applicationName"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				runningTaskDTO.setTriggeredByHost(rs.getString("triggeredByHost"));

				Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));

				Date endTime = rs.getTimestamp("endTime");
				runningTaskDTO.setEndTime(endTime);

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null && endTime != null) {
					duration = Math.abs(endTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				runningTaskDTO.setDuration(duration);
				runningTaskDTO.setFullDuration(fullDuration);
				runningTaskDTO.setProjectId(rs.getLong("projectId"));
				runningTaskDTO.setProjectName(rs.getString("projectName"));

				return runningTaskDTO;
			};
			templateTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting Completed Template and validation Tasks!!");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return templateTaskResults;
	}

	@Override
	public List<Long> getSchemasForSchedule(Long idScheduler) {
		List<Long> idAppList = new ArrayList<Long>();
		try {
			String sql = "Select idDataSchema from scheduledTasks where idSchedule =?";
			List<Long> resultList = jdbcTemplate.queryForList(sql, Long.class, idScheduler);
			if (resultList != null && resultList.size() > 0) {
				idAppList = resultList;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idAppList;
	}

	@Override
	public List<SchemaJobQueue> getSchemaJobsInQueue() {
		List<SchemaJobQueue> schemaJobsList = null;
		try {
			String sql = "select * from schema_jobs_queue where status = 'queued'";
			schemaJobsList = jdbcTemplate.query(sql, new RowMapper<SchemaJobQueue>() {
				@Override
				public SchemaJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					SchemaJobQueue schemaJobQueue = new SchemaJobQueue();
					schemaJobQueue.setQueueId(rs.getLong("queueId"));
					schemaJobQueue.setIdDataSchema(rs.getLong("idDataSchema"));
					schemaJobQueue.setUniqueId(rs.getString("uniqueId"));
					schemaJobQueue.setStatus(rs.getString("status"));
					return schemaJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return schemaJobsList;
	}

	@Override
	public String addSchemaJobToQueue(long idDataSchema, String healthCheckFlag) {
		String uniqueId = "";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			String sql = "insert into schema_jobs_queue(idDataSchema,uniqueId,status,createdAt,healthCheck) values(?,?,?,?,?)";
			uniqueId = "sch_" + idDataSchema + "_" + currentDate.getTime();
			jdbcTemplate.update(sql, idDataSchema, uniqueId, "queued", currentDate, healthCheckFlag);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public boolean updateSchemaJobRunStatus(long idDataSchema, String uniqueId, String status) {
		boolean updateStatus = false;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("killed")
					|| status.equalsIgnoreCase("failed"))) {
				String sql = "update schema_jobs_queue set status=?, endTime=? where uniqueId=? and idDataSchema=?";
				int count = jdbcTemplate.update(sql, status, currentDate, uniqueId, idDataSchema);
				if (count > 0)
					updateStatus = true;
			} else {
				LOG.info("\n====>Changing status of Schema job to [" + status + "] is not allowed !!");
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating schema job with id[" + idDataSchema
					+ "] uniqueId[" + uniqueId + "] with status [" + status + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public boolean startSchemaJobByUniqueId(long idDataSchema, String uniqueId)
			throws ConnectionTriggerFailedException {
		int count = 0;
		String status = "started";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		// Get DeployMode
		String deployMode = getDeployModeOfApplication();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update schema_jobs_queue set status=?, triggeredByHost=?, deployMode=?, startTime=? where idDataSchema=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, deployMode, currentDate, idDataSchema, uniqueId);
			if (count == 0)
				throw new ConnectionTriggerFailedException("Job is already started");
		} else {
			throw new ConnectionTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean checkIfSchemaJobInProgress(Long idDataSchema) {
		boolean status = false;
		try {
			String sql = "select count(*) from schema_jobs_queue where status in ('started','in progress','subtasks in progress') and idDataSchema=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idDataSchema);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfSchemaJobInProgress !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean checkIfSchemaJobInQueue(Long idDataSchema) {
		boolean status = false;
		try {
			String sql = "select count(*) from schema_jobs_queue where status in ('queued') and idDataSchema=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idDataSchema);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfSchemaJobInQueue !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public void deleteOldQueueEntriesAndAssociationsOfSchema(Long idDataSchema) {
		try {
			String sql = "select queueId, idDataSchema, uniqueId from schema_jobs_queue where status in ('completed','killed','failed') and idDataSchema=?";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, idDataSchema);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					Long queueId = sqlRowSet.getLong("queueId");
					String uniqueId = sqlRowSet.getString("uniqueId");

					// Delete associations
					sql = "delete from schema_jobs_tracking where idDataSchema=? and uniqueId=?";
					jdbcTemplate.update(sql, idDataSchema, uniqueId);

					// Delete queue entry
					sql = "delete from schema_jobs_queue where idDataSchema=? and queueId=?";
					jdbcTemplate.update(sql, idDataSchema, queueId);
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<SchemaJobQueue> getAssociationsInProgressSchemaJobList() {
		List<SchemaJobQueue> schemaJobsList = null;
		try {
			String sql = "select * from schema_jobs_queue where status = 'subtasks in progress'";
			schemaJobsList = jdbcTemplate.query(sql, new RowMapper<SchemaJobQueue>() {
				@Override
				public SchemaJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					SchemaJobQueue schemaJobQueue = new SchemaJobQueue();
					schemaJobQueue.setQueueId(rs.getLong("queueId"));
					schemaJobQueue.setIdDataSchema(rs.getLong("idDataSchema"));
					schemaJobQueue.setUniqueId(rs.getString("uniqueId"));
					schemaJobQueue.setStatus(rs.getString("status"));
					schemaJobQueue.setHealthCheck(rs.getString("healthCheck"));
					return schemaJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return schemaJobsList;
	}

	@Override
	public List<Map<String, Object>> getAssociatedTemplatesForSchema(String uniqueId, Long idDataSchema) {
		List<Map<String, Object>> templateIdList = null;
		try {
			String sql = "select idData,template_uniqueId from schema_jobs_tracking where idDataSchema=? and uniqueId=?";
			templateIdList = jdbcTemplate.queryForList(sql, idDataSchema, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			
		}
		return templateIdList;
	}

	@Override
	public List<SchemaJobDTO> getSchemaJobsList(List<Project> projList) {
		List<SchemaJobDTO> schemaJobDTOList = new ArrayList<SchemaJobDTO>();
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "";
			sql = sql + "select t1.*,t2.schemaName, t2.project_id as projectId, t3.projectName ";
			sql = sql + "from schema_jobs_queue t1 ";
			sql = sql + "join listDataSchema t2 on t1.idDataSchema=t2.idDataSchema ";
			sql = sql + "join project t3 on t2.project_id = t3.idProject ";
			sql = sql + "where t2.project_id in ( " + projIds + ") limit 500";
			LOG.debug("Sql: " + sql);

			RowMapper<SchemaJobDTO> rowMapper = (rs, i) -> {
				SchemaJobDTO schemaJobDTO = new SchemaJobDTO();
				schemaJobDTO.setQueueId(rs.getLong("queueId"));
				schemaJobDTO.setIdDataSchema(rs.getLong("idDataSchema"));
				schemaJobDTO.setSchemaName(rs.getString("schemaName"));
				schemaJobDTO.setUniqueId(rs.getString("uniqueId"));
				schemaJobDTO.setStatus(rs.getString("status"));
				schemaJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				schemaJobDTO.setProjectId(rs.getLong("projectId"));
				schemaJobDTO.setProjectName(rs.getString("projectName"));
				schemaJobDTO.setTriggeredByHost(rs.getString("triggeredByHost"));
				schemaJobDTO.setProcessId(rs.getLong("processId"));
				schemaJobDTO.setDeployMode(rs.getString("deployMode"));
				schemaJobDTO.setSparkAppId(rs.getString("sparkAppId"));

				Date startTime = rs.getTimestamp("startTime");
				schemaJobDTO.setStartTime(startTime);

				Date endTime = rs.getTimestamp("endTime");
				schemaJobDTO.setEndTime(endTime);

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null && endTime != null) {
					duration = Math.abs(endTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);

				} else if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					duration = Math.abs(currentDate.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				schemaJobDTO.setFullDuration(fullDuration);
				return schemaJobDTO;
			};
			schemaJobDTOList = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching schema jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return schemaJobDTOList;
	}

	@Override
	public List<RunningTaskDTO> getJobsUnderProcessing() {
		List<RunningTaskDTO> validationTaskResults = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select t.taskType, t.applicationId, t.startTime, t.uniqueId, t.processId from ( (select 'template' as taskType, idData as applicationId, startTime, uniqueId, processId from runTemplateTasks where idData!=0 and status in ('started','in progress')) union (select 'validation' as taskType, idApp as applicationId,startTime, uniqueId, processId from runScheduledTasks where idApp!=0 and status in ('started','in progress')) ) t order by t.startTime";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType(rs.getString("taskType"));
				runningTaskDTO.setApplicationId(rs.getLong("applicationId"));
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				return runningTaskDTO;
			};
			validationTaskResults = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting list of jobs running!!"+e.getMessage());
			e.printStackTrace();
		}
		return validationTaskResults;
	}

	@Override
	public List<RunningTaskDTO> getSchemaJobTemplatesStatus(String uniqueId, Long idDataSchema) {
		List<RunningTaskDTO> templateList = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select t1.idData, t3.name, t1.template_uniqueId, t2.status, t2.sparkAppId, t2.processId, t2.startTime, t2.deployMode from "
					+ " (select * from schema_jobs_tracking where idDataSchema=? and uniqueId=?) t1 left outer join runTemplateTasks t2 on "
					+ " t1.idData=t2.idData and t1.template_uniqueId=t2.uniqueId left outer join listDataSources t3 on t1.idData=t3.idData";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Template");
				runningTaskDTO.setApplicationId(rs.getLong("idData"));
				runningTaskDTO.setApplicationName(rs.getString("name"));
				runningTaskDTO.setUniqueId(rs.getString("template_uniqueId"));
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				return runningTaskDTO;
			};
			templateList = jdbcTemplate.query(sql, rowMapper, idDataSchema, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting list of templates associated with schema job!!"+e.getMessage());
			e.printStackTrace();
		}
		return templateList;
	}

	@Override
	public boolean checkIfAppGroupJobInQueue(Long idAppGroup) {
		boolean status = false;
		try {
			String sql = "select count(*) from appgroup_jobs_queue where status in ('queued') and idAppGroup=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idAppGroup);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfAppGroupJobInProgress !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean checkIfAppGroupJobInProgress(Long idAppGroup) {
		boolean status = false;
		try {
			String sql = "select count(*) from appgroup_jobs_queue where status in ('started','in progress','subtasks in progress') and idAppGroup=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idAppGroup);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfAppGroupJobInProgress !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String addAppGroupJobToQueue(Long idAppGroup) {
		String uniqueId = null;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			String sql = "insert into appgroup_jobs_queue(idAppGroup,uniqueId,status,createdAt) values(?,?,?,?)";
			String uid = "appgrp_" + idAppGroup + "_" + currentDate.getTime();
			jdbcTemplate.update(sql, idAppGroup, uid, "queued", currentDate);
			uniqueId = uid;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public boolean updateAppGroupJobRunStatus(long idAppGroup, String uniqueId, String status) {
		boolean updateStatus = false;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("killed")
					|| status.equalsIgnoreCase("failed"))) {
				String sql = "update appgroup_jobs_queue set status=?, endTime=? where idAppGroup=? and uniqueId=?";
				int count = jdbcTemplate.update(sql, status, currentDate, idAppGroup, uniqueId);
				if (count > 0)
					updateStatus = true;

			} else {
				LOG.info("\n====>Changing status of AppGroup job to [" + status + "] is not allowed !!");
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating appgroup job with id[" + idAppGroup
					+ "] uniqueId[" + uniqueId + "] with status [" + status + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public boolean startAppGroupJobByUniqueId(long idAppGroup, String uniqueId) throws AppGroupTriggerFailedException {
		int count = 0;
		String status = "started";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		// Get deployMode
		String deployMode = getDeployModeOfApplication();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update appgroup_jobs_queue set status=?, triggeredByHost=?, deployMode=?, startTime=? where idAppGroup=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, deployMode, currentDate, idAppGroup, uniqueId);
			if (count == 0)
				throw new AppGroupTriggerFailedException("Job is already started");
		} else {
			throw new AppGroupTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	@Override
	public List<AppGroupJobQueue> getAssociationsInProgressAppGroupList() {
		List<AppGroupJobQueue> appGroupJobsList = null;
		try {
			String sql = "select * from appgroup_jobs_queue where status = 'subtasks in progress'";
			appGroupJobsList = jdbcTemplate.query(sql, new RowMapper<AppGroupJobQueue>() {
				@Override
				public AppGroupJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					AppGroupJobQueue appGroupJobQueue = new AppGroupJobQueue();
					appGroupJobQueue.setQueueId(rs.getLong("queueId"));
					appGroupJobQueue.setIdAppGroup(rs.getLong("idAppGroup"));
					appGroupJobQueue.setUniqueId(rs.getString("uniqueId"));
					appGroupJobQueue.setStatus(rs.getString("status"));
					return appGroupJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return appGroupJobsList;
	}

	@Override
	public List<AppGroupJobQueue> getAppGroupJobsInQueue() {
		List<AppGroupJobQueue> appGroupJobsList = null;
		try {
			String sql = "select * from appgroup_jobs_queue where status = 'queued'";
			appGroupJobsList = jdbcTemplate.query(sql, new RowMapper<AppGroupJobQueue>() {
				@Override
				public AppGroupJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					AppGroupJobQueue appGroupJobQueue = new AppGroupJobQueue();
					appGroupJobQueue.setQueueId(rs.getLong("queueId"));
					appGroupJobQueue.setIdAppGroup(rs.getLong("idAppGroup"));
					appGroupJobQueue.setUniqueId(rs.getString("uniqueId"));
					appGroupJobQueue.setStatus(rs.getString("status"));
					return appGroupJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return appGroupJobsList;
	}

	@Override
	public List<Map<String, Object>> getAssociatedValidationsForAppGroupJob(String appGroupUniqueId, Long idAppGroup) {
		List<Map<String, Object>> templateIdList = null;
		try {
			String sql = "select idApp,validation_uniqueId from appgroup_jobs_tracking where idAppGroup=? and uniqueId=?";
			templateIdList = jdbcTemplate.queryForList(sql, idAppGroup, appGroupUniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return templateIdList;
	}

	@Override
	public String getValidationJobStatusById(Long idApp, String validation_uniqueId) {
		String status = "";
		try {
			String sql = "select status from runScheduledTasks where idApp=? and uniqueId=?";
			List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, idApp, validation_uniqueId);
			if (result != null && result.size() > 0) {
				String val = (String) result.get(0).get("status");
				if (val != null) {
					status = val;
				}
			}
		} catch (Exception e) {
			LOG.error("\n====>Exception Occurred in getValidationJobStatusById for IdData:" + idApp);
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<AppGroupJobDTO> getAppGroupJobsList() {
		List<AppGroupJobDTO> appGroupJobDTOList = new ArrayList<AppGroupJobDTO>();
		try {
			String sql = "select t1.*,t2.name as appGroupName from appgroup_jobs_queue t1 join listAppGroup t2 on t1.idAppGroup=t2.idAppGroup limit 500";
			LOG.debug("Sql: " + sql);

			RowMapper<AppGroupJobDTO> rowMapper = (rs, i) -> {
				AppGroupJobDTO appGroupJobDTO = new AppGroupJobDTO();
				appGroupJobDTO.setQueueId(rs.getLong("queueId"));
				appGroupJobDTO.setIdAppGroup(rs.getLong("idAppGroup"));
				appGroupJobDTO.setAppGroupName(rs.getString("appGroupName"));
				appGroupJobDTO.setUniqueId(rs.getString("uniqueId"));
				appGroupJobDTO.setStatus(rs.getString("status"));
				appGroupJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				appGroupJobDTO.setTriggeredByHost(rs.getString("triggeredByHost"));
				appGroupJobDTO.setProcessId(rs.getLong("processId"));
				appGroupJobDTO.setDeployMode(rs.getString("deployMode"));
				appGroupJobDTO.setSparkAppId(rs.getString("sparkAppId"));

				Date startTime = rs.getTimestamp("startTime");
				appGroupJobDTO.setStartTime(startTime);

				Date endTime = rs.getTimestamp("endTime");
				appGroupJobDTO.setEndTime(endTime);

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null && endTime != null) {
					duration = Math.abs(endTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);

				} else if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					duration = Math.abs(currentDate.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				appGroupJobDTO.setFullDuration(fullDuration);
				return appGroupJobDTO;
			};
			appGroupJobDTOList = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching AppGroup jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return appGroupJobDTOList;
	}

	@Override
	public List<Map<String, Object>> getAppGroupJobValidationsMaps(String uniqueId, Long idAppGroup) {
		List<Map<String, Object>> templateList = new ArrayList<Map<String, Object>>();
		try {
			String sql = "select t1.idApp, t3.name, t2.uniqueId, t2.status, t2.sparkAppId, t2.processId, t2.startTime, t2.deployMode from "
					+ " (select * from appgroup_jobs_tracking where uniqueId='" + uniqueId + "' and idAppGroup="
					+ idAppGroup + ") t1 left outer join runScheduledTasks t2 on "
					+ " t1.idApp=t2.idApp and t1.validation_uniqueId=t2.uniqueId left outer join listApplications t3 on t1.idApp=t3.idApp";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			if (queryForRowSet != null) {
				while (queryForRowSet.next()) {
					Map<String, Object> runningTaskDTO = new HashMap<>();
					runningTaskDTO.put("taskType", "Validation");
					runningTaskDTO.put("applicationId", queryForRowSet.getLong("idApp"));
					runningTaskDTO.put("applicationName", queryForRowSet.getString("name"));
					runningTaskDTO.put("uniqueId", queryForRowSet.getString("uniqueId"));
					Object date = queryForRowSet.getTimestamp("startTime");
					String startTime = "";
					if (date != null) {
						startTime = queryForRowSet.getTimestamp("startTime").toString();
					}
					runningTaskDTO.put("startTime", startTime);
					runningTaskDTO.put("status", queryForRowSet.getString("status"));
					runningTaskDTO.put("processId", queryForRowSet.getLong("processId"));
					runningTaskDTO.put("sparkAppId", queryForRowSet.getString("sparkAppId"));
					runningTaskDTO.put("deployMode", queryForRowSet.getString("deployMode"));
					templateList.add(runningTaskDTO);
				}
			}
		} catch (Exception e) {
			LOG.error(
					"\n====>Exception occurred while getting list of validations associated with AppGroup job!!"+e.getMessage());
			e.printStackTrace();
		}
		return templateList;
	}

	@Override
	public List<RunningTaskDTO> getAppGroupJobValidationsStatus(String uniqueId, Long idAppGroup) {
		List<RunningTaskDTO> templateList = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select t1.idApp, t3.name, t2.uniqueId, t2.status, t2.sparkAppId, t2.processId, t2.startTime, t2.deployMode from "
					+ " (select * from appgroup_jobs_tracking where idAppGroup=? and uniqueId=?) t1 left outer join runScheduledTasks t2 on "
					+ " t1.idApp=t2.idApp and t1.validation_uniqueId=t2.uniqueId left outer join listApplications t3 on t1.idApp=t3.idApp";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Validation");
				runningTaskDTO.setApplicationId(rs.getLong("idApp"));
				runningTaskDTO.setApplicationName(rs.getString("name"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				// runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				// Mamta 12-Sep-2022
				// Date startTime = rs.getTimestamp("startTime");
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				return runningTaskDTO;
			};
			templateList = jdbcTemplate.query(sql, rowMapper, idAppGroup, uniqueId);
		} catch (Exception e) {
			LOG.error(
					"\n====>Exception occurred while getting list of validations associated with AppGroup job!!"+e.getMessage());
			e.printStackTrace();
		}
		return templateList;
	}

	@Override
	public boolean isApplicationInProgress(long idApp) {
		boolean status = false;
		try {
			String sql = "select count(*) from runScheduledTasks where status in ('queued','started','in progress') and idApp=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idApp);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			status = true;
			LOG.error("\n====> Exception occurred while checking application is in progress ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from runScheduledTasks where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the jobstatus by UniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getAppGroupJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from appgroup_jobs_queue where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the AppGroup jobstatus by UniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<AppGroupJobDTO> getAppGroupHistoryById(Long idAppGroup) {
		List<AppGroupJobDTO> appGroupJobDTOList = new ArrayList<AppGroupJobDTO>();
		try {
			String sql = "select t1.*,t2.name as appGroupName from appgroup_jobs_queue t1 join listAppGroup t2 on t1.idAppGroup=t2.idAppGroup where t1.idAppGroup = ? order by t1.queueId desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<AppGroupJobDTO> rowMapper = (rs, i) -> {
				AppGroupJobDTO appGroupJobDTO = new AppGroupJobDTO();
				appGroupJobDTO.setQueueId(rs.getLong("queueId"));
				appGroupJobDTO.setIdAppGroup(rs.getLong("idAppGroup"));
				appGroupJobDTO.setAppGroupName(rs.getString("appGroupName"));
				appGroupJobDTO.setUniqueId(rs.getString("uniqueId"));
				appGroupJobDTO.setStatus(rs.getString("status"));
				appGroupJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				appGroupJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				appGroupJobDTO.setDeployMode(rs.getString("deployMode"));
				appGroupJobDTO.setProcessId(rs.getLong("processId"));
				appGroupJobDTO.setStartTime(rs.getTimestamp("startTime"));
				appGroupJobDTO.setEndTime(rs.getTimestamp("endTime"));
				return appGroupJobDTO;
			};
			appGroupJobDTOList = jdbcTemplate.query(sql, rowMapper, idAppGroup);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching AppGroup History!!"+e.getMessage());
			e.printStackTrace();
		}
		return appGroupJobDTOList;
	}

	@Override
	public boolean checkIfValidationJobQueuedOrInProgress(Long idApp) {
		boolean status = false;
		try {
			String sql = "select count(*) from runScheduledTasks where status in ('queued','started','in progress') and idApp=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idApp);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfValidationJobInProgress !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public void updateSQSAlertSentStatus(long requestId, String sqsAlertSentStatus) {
		try {
			String sql = "update DQ_SQS_MSG_QUEUE set sqs_alert_sent=? where id=?";
			jdbcTemplate1.update(sql, sqsAlertSentStatus, requestId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<DataQualitySQSRequest> getDataQualitySQSRequestList() {
		List<DataQualitySQSRequest> dqSQSList = new ArrayList<DataQualitySQSRequest>();
		try {
			String sql = "select * from DQ_SQS_MSG_QUEUE where sqs_alert_enabled='Y' and sqs_alert_sent='N'";

			RowMapper<DataQualitySQSRequest> rowMapper = (rs, i) -> {
				DataQualitySQSRequest dqSQSRequest = new DataQualitySQSRequest();
				dqSQSRequest.setId(rs.getLong("id"));
				dqSQSRequest.setIdApp(rs.getLong("idApp"));
				dqSQSRequest.setUniqueId(rs.getString("uniqueId"));
				dqSQSRequest.setExecutionDate(rs.getDate("execution_date"));
				dqSQSRequest.setRun(rs.getLong("run"));
				dqSQSRequest.setSqsAlertEnabled(rs.getString("sqs_alert_enabled"));
				dqSQSRequest.setSqsAlertSent(rs.getString("sqs_alert_sent"));
				return dqSQSRequest;
			};
			dqSQSList = jdbcTemplate1.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching Data from DQ_SQS_MSG_QUEUE table!!"+e.getMessage());
			e.printStackTrace();
		}
		return dqSQSList;
	}

	@Override
	public List<DatabuckSNSRequest> getSNSMessageList() {
		List<DatabuckSNSRequest> snsReqList = new ArrayList<DatabuckSNSRequest>();
		try {
			String sql = "select * from DQ_SNS_MSG_QUEUE where sns_alert_enabled='Y' and sns_alert_sent='N'";
			LOG.debug("sql "+sql);
			RowMapper<DatabuckSNSRequest> rowMapper = (rs, i) -> {
				DatabuckSNSRequest snsRequest = new DatabuckSNSRequest();
				snsRequest.setId(rs.getLong("id"));
				snsRequest.setMsgId(rs.getString("msg_id"));
				snsRequest.setSnsMsgBody(rs.getString("sns_msg_body"));
				snsRequest.setSnsAlertEnabled(rs.getString("sns_alert_enabled"));
				snsRequest.setSnsAlertSent(rs.getString("sns_alert_sent"));
				return snsRequest;
			};
			snsReqList = jdbcTemplate1.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching Data from DQ_SNS_MSG_QUEUE table!!"+e.getMessage());
			e.printStackTrace();
		}
		return snsReqList;
	}

	@Override
	public void updateSNSAlertSentStatus(long requestId, String snsAlertSentStatus) {
		try {
			String sql = "update DQ_SNS_MSG_QUEUE set sns_alert_sent=? where id=?";
			jdbcTemplate1.update(sql, snsAlertSentStatus, requestId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProcessedMessagesFromSQSQueue() {
		try {
			String sql = "delete from DQ_SQS_MSG_QUEUE where sqs_alert_enabled='Y' and sqs_alert_sent='Y'";
			jdbcTemplate1.execute(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProcessedMessagesFromSNSQueue() {
		try {
			String sql = "delete from DQ_SNS_MSG_QUEUE where sns_alert_enabled='Y' and sns_alert_sent='Y'";
			jdbcTemplate1.execute(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<ValidationRunDTO> getValidationRunHistory(Long idApp) {
		List<ValidationRunDTO> valRunDTOList = new ArrayList<ValidationRunDTO>();
		try {
			String sql = "select idApp, uniqueId, execution_date, run, test_run from app_uniqueId_master_table where idApp = ? order by id desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<ValidationRunDTO> rowMapper = (rs, i) -> {
				ValidationRunDTO validationRunDTO = new ValidationRunDTO();
				validationRunDTO.setIdApp(rs.getLong("idApp"));

				String uniqueId = rs.getString("uniqueId");
				validationRunDTO.setUniqueId(uniqueId);

				Date execution_date = rs.getDate("execution_date");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String execDate = "";
				if (execution_date != null) {
					execDate = sdf.format(execution_date);
				}
				validationRunDTO.setExecDate(execDate);
				validationRunDTO.setRun(rs.getLong("run"));
				validationRunDTO.setTestRun(rs.getString("test_run"));

				String statusSql = "select status from runScheduledTasks where idApp=? and uniqueId=?";
				String status = jdbcTemplate.queryForObject(statusSql, String.class, idApp, uniqueId);
				validationRunDTO.setStatus(status);

				return validationRunDTO;
			};
			valRunDTOList = jdbcTemplate1.query(sql, rowMapper, idApp);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching validation run History!!"+e.getMessage());
			e.printStackTrace();
		}
		return valRunDTOList;
	}

	@Override
	public String getSchemaJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from schema_jobs_queue where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the Schema jobstatus by UniqueId ..!! "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<SchemaJobDTO> getSchemaJobHistoryById(Long idDataSchema) {
		List<SchemaJobDTO> schemaJobDTOList = new ArrayList<SchemaJobDTO>();
		try {
			String sql = "select t1.*,t2.schemaName from schema_jobs_queue t1 join listDataSchema t2 on t1.idDataSchema=t2.idDataSchema where t1.idDataSchema=? order by t1.queueId desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<SchemaJobDTO> rowMapper = (rs, i) -> {
				SchemaJobDTO schemaJobDTO = new SchemaJobDTO();
				schemaJobDTO.setQueueId(rs.getLong("queueId"));
				schemaJobDTO.setIdDataSchema(rs.getLong("idDataSchema"));
				schemaJobDTO.setSchemaName(rs.getString("schemaName"));
				schemaJobDTO.setUniqueId(rs.getString("uniqueId"));
				schemaJobDTO.setStatus(rs.getString("status"));
				schemaJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				schemaJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				schemaJobDTO.setDeployMode(rs.getString("deployMode"));
				schemaJobDTO.setProcessId(rs.getLong("processId"));
				schemaJobDTO.setStartTime(rs.getTimestamp("startTime"));
				schemaJobDTO.setEndTime(rs.getTimestamp("endTime"));
				return schemaJobDTO;
			};
			schemaJobDTOList = jdbcTemplate.query(sql, rowMapper, idDataSchema);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching schema jobs!! "+e.getMessage());
			e.printStackTrace();
		}
		return schemaJobDTOList;
	}

	@Override
	public boolean isTemplateJobQueuedOrInProgress(Long templateId) {
		boolean isJobInProgress = false;
		try {
			String query = "select count(*) from runTemplateTasks where status in ('queued','started','in progress') and idData=?";
			int count = jdbcTemplate.queryForObject(query, Integer.class, templateId);
			if (count > 0) {
				isJobInProgress = true;
			}
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred configDao - isTemplateJobQueuedOrInProgress API "+e.getMessage());
			e.printStackTrace();
		}
		return isJobInProgress;
	}

	@Override
	public boolean checkIfProjectJobInProgress(Long projectId) {
		boolean status = false;
		try {
			String sql = "select count(*) from project_jobs_queue where status in ('started','in progress','subtasks in progress') and projectId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfProjectJobInProgress !! "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String addProjectJobToQueue(long projectId) {
		String uniqueId = "";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			String sql = "insert into project_jobs_queue(projectId,uniqueId,status,createdAt) values(?,?,?,?)";
			String uid = "prj_" + projectId + "_" + currentDate.getTime();
			jdbcTemplate.update(sql, projectId, uid, "queued", currentDate);
			uniqueId = uid;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public String getProjectJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from project_jobs_queue where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the Project jobstatus by uniqueId ..!! "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<ProjectJobDTO> getProjectJobHistoryById(long projectId) {
		List<ProjectJobDTO> projectJobDTOList = new ArrayList<>();
		try {
			String sql = "select t1.*,t2.projectName from project_jobs_queue t1 join project t2 on t1.projectId=idProject where t1.projectId=? order by t1.queueId desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<ProjectJobDTO> rowMapper = (rs, i) -> {
				ProjectJobDTO projectJobDTO = new ProjectJobDTO();
				projectJobDTO.setQueueId(rs.getLong("queueId"));
				projectJobDTO.setProjectId(rs.getLong("projectId"));
				projectJobDTO.setProjectName(rs.getString("projectName"));
				projectJobDTO.setUniqueId(rs.getString("uniqueId"));
				projectJobDTO.setStatus(rs.getString("status"));
				projectJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				projectJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				projectJobDTO.setDeployMode(rs.getString("deployMode"));
				projectJobDTO.setProcessId(rs.getLong("processId"));
				projectJobDTO.setStartTime(rs.getTimestamp("startTime"));
				projectJobDTO.setEndTime(rs.getTimestamp("endTime"));
				return projectJobDTO;
			};
			projectJobDTOList = jdbcTemplate.query(sql, rowMapper, projectId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching project jobs!! "+e.getMessage());
			e.printStackTrace();
		}
		return projectJobDTOList;
	}

	@Override
	public List<SchemaJobDTO> getProjectJobAssociatedConnections(String uniqueId, long projectId) {
		List<SchemaJobDTO> projectConnList = new ArrayList<>();
		String sql = "select t1.idDataSchema, t3.schemaName as name, t1.connection_uniqueId, t2.status, t2.startTime, t2.processId, t2.sparkAppId from (select * from project_jobs_tracking where projectId=? and uniqueId=?) t1 left outer join schema_jobs_queue t2 on t1.idDataSchema=t2.idDataSchema and t1.connection_uniqueId=t2.uniqueId left outer join listDataSchema t3 on t1.idDataSchema=t3.idDataSchema";

		LOG.debug("Sql: " + sql);
		try {
			RowMapper<SchemaJobDTO> rowMapper = (rs, i) -> {
				SchemaJobDTO schemaJobDTO = new SchemaJobDTO();
				schemaJobDTO.setIdDataSchema(rs.getLong("idDataSchema"));
				schemaJobDTO.setSchemaName(rs.getString("name"));
				schemaJobDTO.setUniqueId(rs.getString("connection_uniqueId"));
				schemaJobDTO.setStatus(rs.getString("status"));
				schemaJobDTO.setStartTime(rs.getDate("startTime"));
				schemaJobDTO.setProcessId(rs.getLong("processId"));
				schemaJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				return schemaJobDTO;
			};
			projectConnList = jdbcTemplate.query(sql, rowMapper, projectId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return projectConnList;
	}

	@Override
	public boolean isProjectUniqueIdValid(String uniqueId) {
		int count = 0;
		try {
			String sql = "select count(*) from project_jobs_queue where uniqueId=?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkIfProjectJobInQueue(Long projectId) {
		boolean status = false;
		try {
			String sql = "select count(*) from project_jobs_queue where status in ('queued') and projectId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfProjectJobInQueue !! "+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<ProjectJobQueue> getAssociationsInProgressProjectJobList() {
		List<ProjectJobQueue> projectJobsList = null;
		try {
			String sql = "select * from project_jobs_queue where status = 'subtasks in progress'";
			projectJobsList = jdbcTemplate.query(sql, new RowMapper<ProjectJobQueue>() {
				@Override
				public ProjectJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					ProjectJobQueue projectJobQueue = new ProjectJobQueue();
					projectJobQueue.setQueueId(rs.getLong("queueId"));
					projectJobQueue.setProjectId(rs.getLong("projectId"));
					projectJobQueue.setUniqueId(rs.getString("uniqueId"));
					projectJobQueue.setStatus(rs.getString("status"));
					return projectJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return projectJobsList;
	}

	@Override
	public List<Map<String, Object>> getAssociatedConnectionsForProject(String uniqueId, Long projectId) {
		List<Map<String, Object>> connectionIdList = null;
		try {
			String sql = "select idDataSchema,connection_uniqueId from project_jobs_tracking where projectId=? and uniqueId=?";
			connectionIdList = jdbcTemplate.queryForList(sql, projectId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return connectionIdList;
	}

	@Override
	public boolean updateProjectJobRunStatus(Long projectId, String uniqueId, String status) {
		boolean updateStatus = false;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("killed")
					|| status.equalsIgnoreCase("failed"))) {
				String sql = "update project_jobs_queue set status=?, endTime=? where uniqueId=? and projectId=?";
				int count = jdbcTemplate.update(sql, status, currentDate, uniqueId, projectId);
				if (count > 0)
					updateStatus = true;
			} else {
				LOG.info("\n====>Changing status of Project job to [" + status + "] is not allowed !!");
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating project job with id[" + projectId
					+ "] uniqueId[" + uniqueId + "] with status [" + status + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public List<ProjectJobQueue> getProjectJobsInQueue() {
		List<ProjectJobQueue> projectJobsList = null;
		try {
			String sql = "select * from project_jobs_queue where status = 'queued'";
			projectJobsList = jdbcTemplate.query(sql, new RowMapper<ProjectJobQueue>() {
				@Override
				public ProjectJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					ProjectJobQueue projectJobQueue = new ProjectJobQueue();
					projectJobQueue.setQueueId(rs.getLong("queueId"));
					projectJobQueue.setProjectId(rs.getLong("projectId"));
					projectJobQueue.setUniqueId(rs.getString("uniqueId"));
					projectJobQueue.setStatus(rs.getString("status"));
					return projectJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return projectJobsList;
	}

	@Override
	public void deleteOldQueueEntriesAndAssociationsOfProject(Long projectId) {
		try {
			String sql = "select uniqueId from project_jobs_queue where status in ('completed','killed','failed') and projectId=?";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, projectId);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					String uniqueId = sqlRowSet.getString("uniqueId");

					// Delete associations
					sql = "delete from project_jobs_tracking where projectId=? and uniqueId=?";
					jdbcTemplate.update(sql, projectId, uniqueId);

					// Delete queue entry
					sql = "delete from project_jobs_queue where projectId=? and uniqueId=?";
					jdbcTemplate.update(sql, projectId, uniqueId);
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean startProjectJobByUniqueId(long projectId, String uniqueId) throws ProjectTriggerFailedException {
		int count = 0;
		String status = "started";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		// Get DeployMode
		String deployMode = getDeployModeOfApplication();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update project_jobs_queue set status=?, triggeredByHost=?, deployMode=?, startTime=? where projectId=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, deployMode, currentDate, projectId, uniqueId);
			if (count == 0)
				throw new ConnectionTriggerFailedException("Job is already started");
		} else {
			throw new ConnectionTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	public String getDeployModeOfApplication() {
		// Get deployMode
		String deployMode = clusterProperties.getProperty("deploymode");

		if (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) {
			deployMode = "local";
		} else {
			deployMode = "cluster";
		}

		return deployMode;
	}

	@Override
	public void updateAppGroupJobPid(long idAppGroup, String uniqueId, long pid) {
		try {
			String sql = "update appgroup_jobs_queue set processId=? where idAppGroup =? and uniqueId=?";
			jdbcTemplate.update(sql, pid, idAppGroup, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred in updateAppGroupJobPid API ..." +e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateSchemaJobPid(long idDataSchema, String uniqueId, long pid) {
		try {
			String sql = "update schema_jobs_queue set processId=? where idDataSchema =? and uniqueId=?";
			jdbcTemplate.update(sql, pid, idDataSchema, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred in updateSchemaJobPid API ..."+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateProjectJobPid(long projectId, String uniqueId, long pid) {
		try {
			String sql = "update project_jobs_queue set processId=? where projectId =? and uniqueId=?";
			jdbcTemplate.update(sql, pid, projectId, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred in updateProjectJobPid API ..."+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String getDomainNameById(Long domainId) {
		String domainName = "";
		try {
			String query = "select domainName from domain where domainId=?";
			domainName = jdbcTemplate.queryForObject(query, String.class, domainId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred in getDomainNameById !!"+e.getMessage());
			e.printStackTrace();
		}
		return domainName;
	}

	@Override
	public Map<String, Object> getRuleCheckDetails(long idApp, long dqrId) {
		Map<String, Object> ruleCheckMap = new HashMap<>();
		try {
			String sql = "select rule_type,column_name from listApplicationsRulesCatalog where idApp=? and rule_reference=?";
			ruleCheckMap = jdbcTemplate.queryForMap(sql, idApp, dqrId);

		} catch (Exception e) {
			LOG.error(
					"\n=====> Exception occured while retrieving rule_type,column_name from listApplicationsRulesCatalog !!"+e.getMessage());
			e.printStackTrace();
		}
		return ruleCheckMap;
	}

	@Override
	public ListAppGroup getListAppGroupByName(String appGroupName) {
		ListAppGroup listAppGroup = null;
		try {
			String Query = "select * from listAppGroup where name=?";
			RowMapper<ListAppGroup> rowMapper = (rs, i) -> {
				ListAppGroup ls = new ListAppGroup();
				ls.setIdAppGroup(rs.getLong("idAppGroup"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setEnableScheduling(rs.getString("enableScheduling"));
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setProjectId(rs.getLong("project_id"));
				return ls;
			};
			List<ListAppGroup> appGroupList = jdbcTemplate.query(Query, rowMapper, appGroupName);
			if (appGroupList != null && appGroupList.size() > 0) {
				listAppGroup = appGroupList.get(0);
			}

		} catch (Exception e) {
			LOG.error("\n=======>Exception occured while retrieving appGroup details from listAppGroup "+e.getMessage());
			e.printStackTrace();
		}
		return listAppGroup;
	}

	@Override
	public JSONObject getAlertNotificationDetailsByTopic(String sTopic) {
		JSONObject alertNotificationObj = new JSONObject();
		try {
			String Query = "select * from notification_alert_api where topic_row_id= (select row_id from notification_topics where topic_title=?)";
			LOG.debug("\n====> Query: " + Query);
			LOG.debug("\n====> Query Parameter - topic_title: " + sTopic);

			SqlRowSet rs = jdbcTemplate.queryForRowSet(Query, sTopic);
			while (rs.next()) {
				alertNotificationObj.put("alert_msg", rs.getString("alert_msg"));
				alertNotificationObj.put("alert_msg_code", rs.getString("alert_msg_code"));
				alertNotificationObj.put("alert_label", rs.getString("alert_label"));
				break;
			}

		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occurred when fetching notification details from notification_alert_api ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return alertNotificationObj;
	}

	@Override
	public NotificationTopics getNotificationInfoByTopic(String topic) {
		NotificationTopics notificationTopics = null;
		try {
			String Query = "select * from notification_topics where topic_title=?";

			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");

			RowMapper<NotificationTopics> rowMapper = (rs, i) -> {
				NotificationTopics notificationObj = new NotificationTopics();
				notificationObj.setRow_id(rs.getInt("row_id"));
				notificationObj.setTopic_title(rs.getString("topic_title"));
				notificationObj.setFocus_type(rs.getInt("focus_type"));
				notificationObj.setManaged_by(rs.getInt("managed_by"));
				notificationObj.setPublish_url_1(rs.getString("publish_url_1"));
				notificationObj.setPublish_url_2(rs.getString("publish_url_2"));
				notificationObj.setAuthorization(rs.getString("authorization"));
				notificationObj.setService_id(rs.getString("service_id"));
				// Decrypt publish_url_1 password
				String password = rs.getString("password");
				if (password != null && !password.trim().isEmpty()) {
					password = decryptor.decrypt(password);
				}
				notificationObj.setPassword(password);
				notificationObj.setUrl2_authorization(rs.getString("url2_authorization"));
				notificationObj.setUrl2_service_id(rs.getString("url2_service_id"));

				// Decrypt publish_url_2 password
				String url2_password = rs.getString("password");
				if (url2_password != null && !url2_password.trim().isEmpty()) {
					url2_password = decryptor.decrypt(url2_password);
				}
				notificationObj.setUrl2_password(rs.getString("url2_password"));

				return notificationObj;
			};
			List<NotificationTopics> notificationObjList = jdbcTemplate.query(Query, rowMapper, topic);
			if (notificationObjList != null && notificationObjList.size() > 0) {
				notificationTopics = notificationObjList.get(0);
			}
		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occurred when fetching notification details from notification_alert_api ..!!"+e.getMessage());
			e.printStackTrace();
		}

		return notificationTopics;
	}

	private String getFullDurationAsText(long duration) {
		// Calculate duration
		String fullDuration = "";

		if (duration > 0l) {

			long diffMinutes = duration / (60 * 1000) % 60;
			long diffHours = duration / (60 * 60 * 1000) % 24;
			long diffDays = duration / (24 * 60 * 60 * 1000);

			if (diffDays > 0) {
				fullDuration = diffDays + " Days   ";
			}

			if (diffHours > 0) {
				fullDuration = fullDuration + diffHours + " Hrs   ";
			}

			if (diffMinutes > 0) {
				fullDuration = fullDuration + diffMinutes + " Mins";
			}

			if (fullDuration.trim().isEmpty()) {
				fullDuration = "0 Mins";
			}

		}
		return fullDuration;
	}

	@Override
	public List<ProjectJobDTO> getProjectJobsList(List<Project> projList) {
		List<ProjectJobDTO> projectJobDTOList = new ArrayList<>();

		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		try {
			String sql = "select t1.*,t2.projectName from project_jobs_queue t1 join project t2 on t1.projectId = t2.idProject where t1.projectId in ( "
					+ projIds + ") limit 500";

			RowMapper<ProjectJobDTO> rowMapper = (rs, i) -> {
				ProjectJobDTO projectJobDTO = new ProjectJobDTO();
				projectJobDTO.setQueueId(rs.getLong("queueId"));
				projectJobDTO.setUniqueId(rs.getString("uniqueId"));
				projectJobDTO.setStatus(rs.getString("status"));
				projectJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				projectJobDTO.setProjectId(rs.getLong("projectId"));
				projectJobDTO.setProjectName(rs.getString("projectName"));
				projectJobDTO.setTriggeredByHost(rs.getString("triggeredByHost"));
				projectJobDTO.setProcessId(rs.getLong("processId"));
				projectJobDTO.setDeployMode(rs.getString("deployMode"));
				projectJobDTO.setSparkAppId(rs.getString("sparkAppId"));

				Date startTime = rs.getTimestamp("startTime");
				projectJobDTO.setStartTime(startTime);

				Date endTime = rs.getTimestamp("endTime");
				projectJobDTO.setEndTime(endTime);

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null && endTime != null) {
					duration = Math.abs(endTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);

				} else if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					duration = Math.abs(currentDate.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				projectJobDTO.setFullDuration(fullDuration);
				return projectJobDTO;
			};
			projectJobDTOList = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching project jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return projectJobDTOList;
	}

	@Override
	public boolean updatePropertyValue(String propertyCategoryName, String propertyName, String propertyValue) {
		boolean updateStatus = false;
		try {
			String sql = "update databuck_property_details set property_value=?, last_updated_at=? where property_name=? and property_category_id="
					+ "(select property_category_id from databuck_properties_master where property_category_name=?) ";
			int count = jdbcTemplate.update(sql, propertyValue, new Date(), propertyName, propertyCategoryName);
			if (count > 0)
				updateStatus = true;
		} catch (Exception e) {
			LOG.error("\n====>Exception Occurred while updating the property in category["
					+ propertyCategoryName + "] with name [" + propertyName + "] to value [" + propertyValue + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public List<RunningTaskDTO> getJobsUnderProcessingForCurrentHost() {
		// Get current host name
		String triggeredByHost = databuckUtility.getCurrentHostName();

		List<RunningTaskDTO> validationTaskResults = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select * from ((select 'template' as taskType, idData as applicationId, startTime, uniqueId, processId, sparkAppId, deployMode from runTemplateTasks where idData!=0 and status in ('started','in progress') and triggeredByHost=?) "
					+ " union (select 'validation' as taskType, idApp as applicationId,startTime, uniqueId, processId, sparkAppId, deployMode from runScheduledTasks where idApp!=0 and status in ('started','in progress') and triggeredByHost=?) ) t order by t.startTime";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType(rs.getString("taskType"));
				runningTaskDTO.setApplicationId(rs.getLong("applicationId"));
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				return runningTaskDTO;
			};
			validationTaskResults = jdbcTemplate.query(sql, rowMapper, triggeredByHost, triggeredByHost);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting list of jobs running for current host!!"+e.getMessage());
			e.printStackTrace();
		}
		return validationTaskResults;
	}

	@Override
	public void addExternalAPIMsgToQueue(ExternalAPIAlertPOJO externalAPIAlertPOJO) {
		try {
			String query = "insert into external_api_alert_msg_queue("
					+ "external_api_type,taskType,taskId,uniqueId,execution_date,run,test_run,alter_timeStamp,alert_msg,alert_msg_code"
					+ ",alert_label,alert_json) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate1.update(query, externalAPIAlertPOJO.getExternalAPIType(), externalAPIAlertPOJO.getTaskType(),
					externalAPIAlertPOJO.getTaskId(), externalAPIAlertPOJO.getUniqueId(),
					externalAPIAlertPOJO.getExecDate(), externalAPIAlertPOJO.getRun(),
					externalAPIAlertPOJO.getTestRun(), externalAPIAlertPOJO.getAlertTimeStamp(),
					externalAPIAlertPOJO.getAlertMsg(), externalAPIAlertPOJO.getAlertMsgCode(),
					externalAPIAlertPOJO.getAlertLabel(), externalAPIAlertPOJO.getAlertJson());
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while inserting data to external_api_alert_msg_queue !!"+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateAlertMsgDeliverStatusForMessage(String externalAPIType, long taskId, String taskType,
			String uniqueId, String alert_deliver_status) {
		try {
			String sql = "update external_api_alert_msg_queue set alert_msg_deliver_status=? where external_api_type=? and taskId=? and taskType=? and uniqueId=?";
			jdbcTemplate1.update(sql, alert_deliver_status, externalAPIType, taskId, taskType, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while updating alert message deliver status!!"+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public long getDomainIdByProjectId(long projectId) {
		long domainId = 0l;

		try {
			String sql = "select domain_id from domain_to_project where project_id=? order by domain_id desc limit 1";
			domainId = jdbcTemplate.queryForObject(sql, Long.class, projectId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while retrieving domain Id from domain_to_project!!"+e.getMessage());
			e.printStackTrace();
		}
		return domainId;
	}

	@Override
	public Domain getDomainDetailsById(Long domainId) {
		String sql = "SELECT domainId, domainName, is_enterprise_domain, description from domain Where domainId="
				+ domainId;
		List<Domain> domainList = jdbcTemplate.query(sql, new RowMapper<Domain>() {
			public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
				Domain domain = new Domain();

				domain.setDomainId(rs.getInt("domainId"));
				domain.setDomainName(rs.getString("domainName"));
				return domain;
			}

		});
		Domain domain = null;
		if (domainList.size() > 0)
			return domainList.get(0);

		return domain;
	}

	@Override
	public boolean checkIfDomainJobInProgress(Long domainId) {
		boolean status = false;
		try {
			String sql = "select count(*) from domain_jobs_queue where status in ('started','in progress','subtasks in progress') and domainId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, domainId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfDomainJobInProgress !!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean checkIfDomainJobInQueue(Long domainId) {
		boolean status = false;
		try {
			String sql = "select count(*) from domain_jobs_queue where status in ('queued') and domainId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, domainId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfDomainJobInQueue !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String addDomainJobToQueue(long domainId) {
		String uniqueId = "";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			String sql = "insert into domain_jobs_queue(domainId,uniqueId,status,createdAt) values(?,?,?,?)";
			String uid = "dmn_" + domainId + "_" + currentDate.getTime();
			jdbcTemplate.update(sql, domainId, uid, "queued", currentDate);
			uniqueId = uid;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public boolean isDomainUniqueIdValid(String uniqueId) {
		int count = 0;
		try {
			String sql = "select count(*) from domain_jobs_queue where uniqueId=?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getDomainJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from domain_jobs_queue where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the Domain jobstatus by uniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<ProjectJobDTO> getDomainJobAssociatedProjects(String uniqueId, long domainId) {
		List<ProjectJobDTO> projectConnList = new ArrayList<>();
		String sql = "select t1.projectId, t3.projectName, t1.project_uniqueId, t2.status, t2.startTime, "
				+ "t2.processId, t2.sparkAppId from (select * from domain_jobs_tracking where domainId=? and uniqueId=?) "
				+ "t1 left outer join project_jobs_queue t2 on t1.projectId=t2.projectId and t1.project_uniqueId=t2.uniqueId "
				+ "left outer join project t3 on t1.projectId=t3.idProject";

		LOG.debug("Sql: " + sql);
		try {
			RowMapper<ProjectJobDTO> rowMapper = (rs, i) -> {
				ProjectJobDTO projectJobDTO = new ProjectJobDTO();
				projectJobDTO.setProjectId(rs.getLong("projectId"));
				projectJobDTO.setProjectName(rs.getString("projectName"));
				projectJobDTO.setUniqueId(rs.getString("project_uniqueId"));
				projectJobDTO.setStatus(rs.getString("status"));
				projectJobDTO.setStartTime(rs.getDate("startTime"));
				projectJobDTO.setProcessId(rs.getLong("processId"));
				projectJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				return projectJobDTO;
			};
			projectConnList = jdbcTemplate.query(sql, rowMapper, domainId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return projectConnList;
	}

	@Override
	public List<RunningTaskDTO> getProjectJobSchemaStatus(String uniqueId, Long projectId) {
		List<RunningTaskDTO> schemaList = new ArrayList<RunningTaskDTO>();
		try {
			String sql = "select t1.idDataSchema, t3.schemaName, t1.connection_uniqueId, t2.status, t2.sparkAppId,"
					+ "t2.processId, t2.startTime, t2.deployMode from (select * from project_jobs_tracking where projectId=? "
					+ "and uniqueId=?) t1 left outer join schema_jobs_queue t2 on t1.idDataSchema=t2.idDataSchema and "
					+ "t1.connection_uniqueId=t2.uniqueId left outer join listDataSchema t3 on t1.idDataSchema=t3.idDataSchema;";
			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("Schema");
				runningTaskDTO.setApplicationId(rs.getLong("idDataSchema"));
				runningTaskDTO.setApplicationName(rs.getString("schemaName"));
				runningTaskDTO.setUniqueId(rs.getString("connection_uniqueId"));
				runningTaskDTO.setStartTime(rs.getTimestamp("startTime"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				return runningTaskDTO;
			};
			schemaList = jdbcTemplate.query(sql, rowMapper, projectId, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while getting list of schemas associated with project job!!"+e.getMessage());
			e.printStackTrace();
		}
		return schemaList;
	}

	@Override
	public List<DomainJobDTO> getDomainJobHistoryById(long domainId) {
		List<DomainJobDTO> domainJobDTOList = new ArrayList<>();
		try {
			String sql = "select t1.*,t2.domainName from domain_jobs_queue t1 join domain t2 on t1.domainId= t2.domainId where t1.domainId=? order by t1.queueId desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<DomainJobDTO> rowMapper = (rs, i) -> {
				DomainJobDTO domainJobDTO = new DomainJobDTO();
				domainJobDTO.setQueueId(rs.getLong("queueId"));
				domainJobDTO.setDomainId(rs.getLong("domainId"));
				domainJobDTO.setDomainName(rs.getString("domainName"));
				domainJobDTO.setUniqueId(rs.getString("uniqueId"));
				domainJobDTO.setStatus(rs.getString("status"));
				domainJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				domainJobDTO.setSparkAppId(rs.getString("sparkAppId"));
				domainJobDTO.setDeployMode(rs.getString("deployMode"));
				domainJobDTO.setProcessId(rs.getLong("processId"));
				domainJobDTO.setStartTime(rs.getTimestamp("startTime"));
				domainJobDTO.setEndTime(rs.getTimestamp("endTime"));
				return domainJobDTO;
			};
			domainJobDTOList = jdbcTemplate.query(sql, rowMapper, domainId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching domain jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return domainJobDTOList;
	}

	@Override
	public List<DomainLiteJobDTO> getDomainLiteJobHistoryById(long domainId) {
		List<DomainLiteJobDTO> domainJobDTOList = new ArrayList<>();
		try {
			String sql = "select t1.*,t2.domainName from domain_lite_jobs_queue t1 join domain t2 on t1.domainId= t2.domainId where t1.domainId=? order by t1.queueId desc limit 5";
			LOG.debug("Sql: " + sql);

			RowMapper<DomainLiteJobDTO> rowMapper = (rs, i) -> {
				DomainLiteJobDTO domainLiteJobDTO = new DomainLiteJobDTO();
				domainLiteJobDTO.setQueueId(rs.getLong("queueId"));
				domainLiteJobDTO.setDomainId(rs.getLong("domainId"));
				domainLiteJobDTO.setDomainName(rs.getString("domainName"));
				domainLiteJobDTO.setUniqueId(rs.getString("uniqueId"));
				domainLiteJobDTO.setStatus(rs.getString("status"));
				domainLiteJobDTO.setTriggeredByHost(rs.getString("triggeredByHost"));

				Date createdAt = rs.getTimestamp("createdAt");
				domainLiteJobDTO.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt));

				Date startTime = rs.getTimestamp("startTime");
				domainLiteJobDTO.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));

				Date endTime = rs.getTimestamp("endTime");
				domainLiteJobDTO.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime));

				return domainLiteJobDTO;
			};
			domainJobDTOList = jdbcTemplate.query(sql, rowMapper, domainId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching domain jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return domainJobDTOList;
	}

	@Override
	public List<DomainJobQueue> getInProgressDomainJobList() {
		List<DomainJobQueue> domainJobsList = null;
		try {
			String sql = "select * from domain_jobs_queue where status = 'subtasks in progress'";
			domainJobsList = jdbcTemplate.query(sql, new RowMapper<DomainJobQueue>() {
				@Override
				public DomainJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					DomainJobQueue domainJobQueue = new DomainJobQueue();
					domainJobQueue.setQueueId(rs.getLong("queueId"));
					domainJobQueue.setDomainId(rs.getLong("domainId"));
					domainJobQueue.setUniqueId(rs.getString("uniqueId"));
					domainJobQueue.setStatus(rs.getString("status"));
					return domainJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return domainJobsList;
	}

	@Override
	public List<Map<String, Object>> getAssociatedProjectsForDomain(String uniqueId, Long domainId) {
		List<Map<String, Object>> projectIdList = null;
		try {
			String sql = "select projectId,project_uniqueId from domain_jobs_tracking where domainId=? and uniqueId=?";
			projectIdList = jdbcTemplate.queryForList(sql, domainId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return projectIdList;
	}

	@Override
	public boolean updateDomainJobRunStatus(Long domainId, String uniqueId, String status) {
		boolean updateStatus = false;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("killed")
					|| status.equalsIgnoreCase("failed"))) {
				String sql = "update domain_jobs_queue set status=?, endTime=? where uniqueId=? and domainId=?";
				int count = jdbcTemplate.update(sql, status, currentDate, uniqueId, domainId);
				if (count > 0)
					updateStatus = true;
			} else {
				LOG.info("\n====>Changing status of Domain job to [" + status + "] is not allowed !!");
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating domain job with id[" + domainId + "] uniqueId["
					+ uniqueId + "] with status [" + status + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public List<DomainJobQueue> getDomainJobsInQueue() {
		List<DomainJobQueue> domainJobsList = null;
		try {
			String sql = "select * from domain_jobs_queue where status = 'queued'";
			domainJobsList = jdbcTemplate.query(sql, new RowMapper<DomainJobQueue>() {
				@Override
				public DomainJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					DomainJobQueue domainJobQueue = new DomainJobQueue();
					domainJobQueue.setQueueId(rs.getLong("queueId"));
					domainJobQueue.setDomainId(rs.getLong("domainId"));
					domainJobQueue.setUniqueId(rs.getString("uniqueId"));
					domainJobQueue.setStatus(rs.getString("status"));
					return domainJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return domainJobsList;
	}

	@Override
	public boolean startDomainJobByUniqueId(long domainId, String uniqueId) throws ProjectTriggerFailedException {
		int count = 0;
		String status = "started";
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		// Get DeployMode
		String deployMode = getDeployModeOfApplication();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update domain_jobs_queue set status=?, triggeredByHost=?, deployMode=?, startTime=? where domainId=? and uniqueId=? and triggeredByHost is null and status='queued'";
			count = jdbcTemplate.update(sql, status, triggeredByHost, deployMode, currentDate, domainId, uniqueId);
			if (count == 0)
				throw new ConnectionTriggerFailedException("Job is already started");
		} else {
			throw new ConnectionTriggerFailedException("Job cannot be started with host name null");
		}

		if (count > 0)
			return true;
		else
			return false;
	}

	@Override
	public void updateDomainJobPid(long domainId, String uniqueId, long pid) {
		try {
			String sql = "update domain_jobs_queue set processId=? where domainId =? and uniqueId=?";
			jdbcTemplate.update(sql, pid, domainId, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred in updateDomainJobPid API ..."+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<DomainJobDTO> getDomainJobsList() {
		List<DomainJobDTO> domainJobDTOList = new ArrayList<DomainJobDTO>();
		try {
			String sql = "select t1.*,t2.domainName from domain_jobs_queue t1 join domain t2 on t1.domainId=t2.domainId limit 500";
			LOG.debug("Sql: " + sql);

			RowMapper<DomainJobDTO> rowMapper = (rs, i) -> {
				DomainJobDTO domainJobDTO = new DomainJobDTO();
				domainJobDTO.setQueueId(rs.getLong("queueId"));
				domainJobDTO.setDomainId(rs.getLong("domainId"));
				domainJobDTO.setDomainName(rs.getString("domainName"));
				domainJobDTO.setUniqueId(rs.getString("uniqueId"));
				domainJobDTO.setStatus(rs.getString("status"));
				domainJobDTO.setCreatedAt(rs.getTimestamp("createdAt"));
				domainJobDTO.setTriggeredByHost(rs.getString("triggeredByHost"));
				domainJobDTO.setProcessId(rs.getLong("processId"));
				domainJobDTO.setDeployMode(rs.getString("deployMode"));
				domainJobDTO.setSparkAppId(rs.getString("sparkAppId"));

				Date startTime = rs.getTimestamp("startTime");
				domainJobDTO.setStartTime(startTime);

				Date endTime = rs.getTimestamp("endTime");
				domainJobDTO.setEndTime(endTime);

				// Calculate duration
				long duration = 0l;
				String fullDuration = "";

				if (startTime != null && endTime != null) {
					duration = Math.abs(endTime.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);

				} else if (startTime != null) {
					Date currentDate = DateUtility
							.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
					duration = Math.abs(currentDate.getTime() - startTime.getTime());

					fullDuration = getFullDurationAsText(duration);
				}
				domainJobDTO.setFullDuration(fullDuration);
				return domainJobDTO;
			};
			domainJobDTOList = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching Domain jobs!!"+e.getMessage());
			e.printStackTrace();
		}
		return domainJobDTOList;
	}

	@Override
	public List<RunningTaskDTO> getRunningTemplatesOfSchemaJob(Long idDataSchema, String uniqueId) {

		List<RunningTaskDTO> templatesList = null;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			// Kill all the queued template jobs
			String queuedJobSql = "update runTemplateTasks set status='killed', endTime=? where uniqueId in (select template_uniqueId from schema_jobs_tracking where idDataSchema=? and uniqueId=?) and status='queued'";
			jdbcTemplate.update(queuedJobSql, currentDate, idDataSchema, uniqueId);

			// Get the list of running template Jobs;
			String sql = "select * from runTemplateTasks where uniqueId in (select template_uniqueId from schema_jobs_tracking where idDataSchema=? and uniqueId=?) and status not in ('killed','completed','failed')";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("template");
				runningTaskDTO.setApplicationId(rs.getLong("idData"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};
			templatesList = jdbcTemplate.query(sql, rowMapper, idDataSchema, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while retrieving templates for schema Id[" + idDataSchema
					+ "], uniqueId[" + uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return templatesList;
	}

	@Override
	public List<RunningTaskDTO> getRunningValidationsOfAppGroupJob(Long idAppGroup, String uniqueId) {
		List<RunningTaskDTO> validationsList = null;

		try {
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			// Kill all the queued Validation jobs
			String queuedJobSql = "update runScheduledTasks set status='killed', endTime=? where uniqueId in (select validation_uniqueId from appgroup_jobs_tracking where idAppGroup=? and uniqueId=?) and status='queued'";
			jdbcTemplate.update(queuedJobSql, currentDate, idAppGroup, uniqueId);

			// Get the list of running Validation Jobs;
			String sql = "select * from runScheduledTasks where uniqueId in (select validation_uniqueId from appgroup_jobs_tracking where idAppGroup=? and uniqueId=?) and status not in ('killed','completed','failed')";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("validation");
				runningTaskDTO.setApplicationId(rs.getLong("idApp"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};
			validationsList = jdbcTemplate.query(sql, rowMapper, idAppGroup, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while retrieving validation jobs for AppGroup Id["
					+ idAppGroup + "] with uniqueId[" + uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return validationsList;
	}

	@Override
	public List<RunningTaskDTO> getRunningSchemasOfProjectJob(Long projectId, String uniqueId) {
		List<RunningTaskDTO> schemaList = null;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			// Kill all the queued Schema jobs
			String queuedJobSql = "update schema_jobs_queue set status='killed', endTime=? where uniqueId in (select connection_uniqueId from project_jobs_tracking where projectId=? and uniqueId=?) and status='queued'";
			jdbcTemplate.update(queuedJobSql, currentDate, projectId, uniqueId);

			// Get the list of running Schema Jobs;
			String sql = "select * from schema_jobs_queue where uniqueId in (select connection_uniqueId from project_jobs_tracking where projectId=? and uniqueId=?) and status not in ('killed','completed','failed')";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("schema");
				runningTaskDTO.setApplicationId(rs.getLong("idDataSchema"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};
			schemaList = jdbcTemplate.query(sql, rowMapper, projectId, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while retrieving schema jobs for Project Id[" + projectId
					+ "] with uniqueId[" + uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return schemaList;
	}

	@Override
	public List<RunningTaskDTO> getRunningProjectsOfDomainJob(Long domainId, String uniqueId) {
		List<RunningTaskDTO> projectList = null;
		Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
		try {
			// Kill all the queued Project jobs
			String queuedJobSql = "update project_jobs_queue set status='killed', endTime=? where uniqueId in (select project_uniqueId from domain_jobs_tracking where domainId=? and uniqueId=?) and status='queued'";
			jdbcTemplate.update(queuedJobSql, currentDate, domainId, uniqueId);

			// Get the list of running Project Jobs;
			String sql = "select * from project_jobs_queue where uniqueId in (select project_uniqueId from domain_jobs_tracking where domainId=? and uniqueId=?) and status not in ('killed','completed','failed')";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("project");
				runningTaskDTO.setApplicationId(rs.getLong("projectId"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};
			projectList = jdbcTemplate.query(sql, rowMapper, domainId, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while retrieving project jobs for Domain Id[" + domainId
					+ "] with uniqueId[" + uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return projectList;
	}

	@Override
	public boolean isAppGroupNameDuplicated(String name) {
		boolean isDuplicateName = false;
		try {
			String sql = "Select count(*) from listAppGroup where name=?";
			int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
			if (count > 0)
				isDuplicateName = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return isDuplicateName;
	}

	@Override
	public boolean checkIfDomainLiteJobInProgress(Long domainId) {
		boolean status = false;
		try {
			String sql = "select count(*) from domain_lite_jobs_queue where status in ('started','in progress','subtasks in progress') and domainId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, domainId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfDomainJobInProgress !!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getJobTemplateUniqueIdApi(String uniqueId) {
		String status = "";
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "select COALESCE(max(status), 'notstarted') as status from runTemplateTasks  where uniqueId= ?";
			else
				sql = "select ifnull(max(status), 'notstarted') as status from runTemplateTasks  where uniqueId= ?";

			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the jobstatus by UniqueId ..!! "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getJobStatusByIdapp(Long idApp) {
		String status = "";
		try {
			String sql = "select max(uniqueId) as uniqueId from runScheduledTasks where idapp = ? ";
			status = jdbcTemplate.queryForObject(sql, String.class, idApp);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the uniqueId by idApp ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getJobStatusByUniqueIdApi(String uniqueId) {
		String status = "";
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "select COALESCE(max(status), 'notstarted') as status from runScheduledTasks  where uniqueId= ?";
			else
				sql = "select ifnull(max(status), 'notstarted') as status from runScheduledTasks  where uniqueId= ?";

			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the jobstatus by UniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean checkIfDomainLiteJobInQueue(Long domainId) {
		boolean status = false;
		try {
			String sql = "select count(*) from domain_lite_jobs_queue where status in ('queued') and domainId=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, domainId);
			if (count != null && count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred in checkIfDomainLiteJobInQueue !!"+e.getMessage());
			status = true;
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String addDomainLiteJobToQueue(long domainId) {
		String uniqueId = "";
		try {
			String sql = "insert into domain_lite_jobs_queue(domainId,uniqueId,status,createdAt) values(?,?,?,?)";
			String uid = "dlt_" + domainId + "_" + new Date().getTime();
			jdbcTemplate.update(sql, domainId, uid, "queued", new Date());
			uniqueId = uid;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public boolean isDomainLiteUniqueIdValid(String uniqueId) {
		int count = 0;
		try {
			String sql = "select count(*) from domain_lite_jobs_queue where uniqueId=?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getDomainLiteJobStatusByUniqueId(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from domain_lite_jobs_queue where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when fetching the Domain lite job status by uniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getDomainLiteJobResultByUniqueId(String uniqueId) {
		String result_json = "";
		try {
			String sql = "select resultJson from domain_lite_jobs_queue where uniqueId=?";
			result_json = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occurred when fetching the Domain lite job resultJson by uniqueId ..!!"+e.getMessage());
			e.printStackTrace();
		}
		return result_json;
	}

	@Override
	public boolean updateDomainLite(Long domainId, String uniqueId, String result_json) {
		boolean updateStatus = false;
		try {
			String sql = "update domain_lite_jobs_queue set resultJson=?, endTime=? where uniqueId=? and domainId=?";
			int count = jdbcTemplate.update(sql, result_json, new Date(), uniqueId, domainId);
			if (count > 0)
				updateStatus = true;
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating domain job lite with id[" + domainId
					+ "] uniqueId[" + uniqueId + "] for given json");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	@Override
	public boolean updateDomainLiteJobRunStatus(Long domainId, String uniqueId, String status) {
		boolean updateStatus = false;
		try {
			if (status != null && (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("killed")
					|| status.equalsIgnoreCase("failed"))) {
				String sql = "update domain_lite_jobs_queue set status=?, endTime=? where uniqueId=? and domainId=?";
				int count = jdbcTemplate.update(sql, status, new Date(), uniqueId, domainId);
				if (count > 0)
					updateStatus = true;
			} else {
				LOG.info("\n====>Changing status of Domain Lite job to [" + status + "] is not allowed !!");
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred when updating domain lite job with id[" + domainId
					+ "] uniqueId[" + uniqueId + "] with status [" + status + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return updateStatus;
	}

	// Since domain jobs are lengthy and complex we are considering only 1 jobs at a
	// time
	@Override
	public List<DomainLiteJobQueue> getDomainLiteJobsInQueue() {
		List<DomainLiteJobQueue> domainLiteJobsList = null;
		try {
			String sql = "select * from domain_lite_jobs_queue where status = 'queued' limit 1";
			domainLiteJobsList = jdbcTemplate.query(sql, new RowMapper<DomainLiteJobQueue>() {
				@Override
				public DomainLiteJobQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
					DomainLiteJobQueue domainJobQueue = new DomainLiteJobQueue();
					domainJobQueue.setQueueId(rs.getLong("queueId"));
					domainJobQueue.setDomainId(rs.getLong("domainId"));
					domainJobQueue.setUniqueId(rs.getString("uniqueId"));
					domainJobQueue.setStatus(rs.getString("status"));
					return domainJobQueue;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return domainLiteJobsList;
	}

	@Override
	public boolean startDomainLiteJobByUniqueId(long domainId, String uniqueId)
			throws DomainLiteJobTriggerFailedException {
		boolean response = false;

		// Get HostName
		String triggeredByHost = databuckUtility.getCurrentHostName();

		if (triggeredByHost != null && !triggeredByHost.trim().isEmpty()) {
			String sql = " update domain_lite_jobs_queue set status=?, triggeredByHost=?,startTime=? where domainId=? and uniqueId=? and triggeredByHost is null and status='queued'";
			int count = jdbcTemplate.update(sql, "started", triggeredByHost, new Date(), domainId, uniqueId);
			if (count == 0)
				throw new DomainLiteJobTriggerFailedException("Job is already started");
			else
				response = true;
		} else {
			throw new DomainLiteJobTriggerFailedException("Job cannot be started with host name null");
		}
		return response;

	}

	@Override
	public String getTemplateRunJobStatusById(String uniqueId) {
		String status = "";
		try {
			String sql = "select status from runTemplateTasks where uniqueId=?";
			status = jdbcTemplate.queryForObject(sql, String.class, uniqueId);
		} catch (Exception e) {
			LOG.error("\n====>Exception Occurred in getTemplateRunJobStatusById for uniqueId:" + uniqueId);
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean isTemplateUniqueIdValid(String uniqueId) {
		boolean isTemplateUniqueIdValid = false;
		int count = 0;
		try {
			String sql = "select count(*) from runTemplateTasks where uniqueId =?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, uniqueId);
			if (count > 0)
				isTemplateUniqueIdValid = true;
		} catch (Exception e) {
			LOG.error(
					"\n====>Exception occurred while fetching template uniqueId details from runTemplateTasks for uniqueId["
							+ uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return isTemplateUniqueIdValid;
	}

	@Override
	public RunningTaskDTO getValidationRunDetailsByUniqueId(String uniqueId) {
		RunningTaskDTO validationRunningTaskDTO = null;

		try {
			String sql = "select * from runScheduledTasks where uniqueId=?";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("validation");
				runningTaskDTO.setApplicationId(rs.getLong("idApp"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};

			List<RunningTaskDTO> detailsList = jdbcTemplate.query(sql, rowMapper, uniqueId);

			if (detailsList != null && detailsList.size() > 0)
				validationRunningTaskDTO = detailsList.get(0);

		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occured while getting Validation details from runScheduledTasks for uniqueId["
							+ uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationRunningTaskDTO;
	}

	@Override
	public RunningTaskDTO getSchemaRunDetailsByUniqueId(String uniqueId) {
		RunningTaskDTO schemaRunningTaskDTO = null;

		try {
			String sql = "select * from schema_jobs_queue where uniqueId=?";

			RowMapper<RunningTaskDTO> rowMapper = (rs, i) -> {
				RunningTaskDTO runningTaskDTO = new RunningTaskDTO();
				runningTaskDTO.setTaskType("schema");
				runningTaskDTO.setApplicationId(rs.getLong("idDataSchema"));
				runningTaskDTO.setStatus(rs.getString("status"));
				runningTaskDTO.setProcessId(rs.getLong("processId"));
				runningTaskDTO.setDeployMode(rs.getString("deployMode"));
				runningTaskDTO.setSparkAppId(rs.getString("sparkAppId"));
				runningTaskDTO.setUniqueId(rs.getString("uniqueId"));
				return runningTaskDTO;
			};

			List<RunningTaskDTO> detailsList = jdbcTemplate.query(sql, rowMapper, uniqueId);

			if (detailsList != null && detailsList.size() > 0)
				schemaRunningTaskDTO = detailsList.get(0);

		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occured while getting Schema details from schema_jobs_queue for uniqueId["
							+ uniqueId + "]");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return schemaRunningTaskDTO;
	}

	@Override
	public List<Long> getValidationsForTemplateSuccess(long idDataSchema, String schemaJobUniqueId) {
		List<Long> validationsList = new ArrayList<>();
		try {

			String isRuleCatalogDiscovery = appDbConnectionProperties.getProperty("isRuleCatalogDiscovery");

			isRuleCatalogDiscovery = (isRuleCatalogDiscovery != null
					&& isRuleCatalogDiscovery.trim().equalsIgnoreCase("Y")) ? "Y" : "N";

			String sql = "";

			if (isRuleCatalogDiscovery.equalsIgnoreCase("Y"))
				sql = "select max(a.idApp) from listApplications a join app_option_list_elements b  on a.approve_status=b.row_id "
						+ "and a.approve_status in (select row_id from app_option_list_elements  where element_reference in ('CREATED')) "
						+ "where a.idData in (select c.idData from schema_jobs_tracking c join listDataSources d on c.idData=d.idData "
						+ "and d.template_create_success='Y' where c.idDataSchema=? and c.uniqueId=?) and LOWER(a.active)='yes'  "
						+ "and LOWER(a.incrementalMatching)!='y' and LOWER(a.keyGroupRecordCountAnomaly)!='y' and LOWER(a.buildHistoricFingerPrint)!='y' group by a.idData";
			else
				sql = "select max(a.idApp) from listApplications a where a.idData in (select c.idData from schema_jobs_tracking c join listDataSources d on c.idData=d.idData "
						+ "and d.template_create_success='Y' where c.idDataSchema=? and c.uniqueId=?) and LOWER(a.active)='yes'  "
						+ "and LOWER(a.incrementalMatching)!='y' and LOWER(a.keyGroupRecordCountAnomaly)!='y' and LOWER(a.buildHistoricFingerPrint)!='y' group by a.idData";

			validationsList = jdbcTemplate.queryForList(sql, Long.class, idDataSchema, schemaJobUniqueId);

		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while getting Validation details from listApplication " +e.getMessage());
			e.printStackTrace();
		}
		return validationsList;
	}

	/*
	 * @Override public String duplicateschedulername(String schedulerName) {
	 * System.out.
	 * println("Enter Duplicate Schedular #####################################");
	 * System.out.println("schedulername in DAO IMPL=" + schedulerName);
	 * 
	 * String Name = null; if(schedulerName != null) schedulerName =
	 * schedulerName.trim(); String q =
	 * "SELECT name FROM listschedule WHERE name=?"; Object[] inputs = new Object[]
	 * { schedulerName }; try { Name = jdbcTemplate.queryForObject(q, inputs,
	 * String.class);
	 * 
	 * return Name; } catch (Exception e) { return Name; } }
	 */
	// By Mamta (22-Jan-2022)
	@Override
	public boolean duplicateschedulername(String schedulerName, long projectId) {
		boolean status = false;
		try {
			String query = "Select count(1) as dup_count from listSchedule where name='" + schedulerName + "'"
					+ " and project_id=?";

			Long duplicateCount = jdbcTemplate.queryForObject(query, Long.class, projectId);
			if (duplicateCount != null && duplicateCount > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<JiraIntegrationBean> getNonSubmittedJiraTicketDetails() {
		List<JiraIntegrationBean> jiraIntegrationBeanList = null;
		try {
			// Get the rows from DQ_JIRA_MSG_QUEUE which are not yet submitted;
			String sql = "select * from DQ_JIRA_MSG_QUEUE where ticket_process_status='N' and ticket_submit_status='N' order by createdAt limit 100";

			RowMapper<JiraIntegrationBean> rowMapper = (rs, i) -> {
				JiraIntegrationBean jiraIntegrationBean = new JiraIntegrationBean();
				jiraIntegrationBean.setId(rs.getLong("id"));
				jiraIntegrationBean.setMsgBody(rs.getString("msg_body"));
				jiraIntegrationBean.setTicketProcessStatus(rs.getString("ticket_process_status"));
				jiraIntegrationBean.setTicketSubmitStatus(rs.getString("ticket_submit_status"));
				jiraIntegrationBean.setCreatedAt(rs.getTimestamp("createdAt").toString());

				return jiraIntegrationBean;
			};
			jiraIntegrationBeanList = jdbcTemplate1.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error(
					"\n====> Exception occurred while retrieving jira ticket details from the table DQ_JIRA_MSG_QUEUE "+e.getMessage());
			e.printStackTrace();
		}
		return jiraIntegrationBeanList;
	}

	@Override
	public boolean updateJiraTicketSubmitStatus(long id, String submitStatus) {
		boolean updateStatus = false;
		try {
			String updateSql = "update DQ_JIRA_MSG_QUEUE set ticket_submit_status = ? where id=?";

			int count = jdbcTemplate1.update(updateSql, submitStatus, id);

			if (count > 0)
				updateStatus = true;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"\n====> Exception occurred while updating jira ticket submit status into the table 'DQ_JIRA_MSG_QUEUE'"+e.getMessage());
		}
		return updateStatus;
	}

	@Override
	public boolean updateJiraTicketProcessStatus(long id, String processStatus) {
		boolean updateStatus = false;
		try {
			String updateSql = "update DQ_JIRA_MSG_QUEUE set ticket_process_status = ? where id=?";

			int count = jdbcTemplate1.update(updateSql, processStatus, id);

			if (count > 0)
				updateStatus = true;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"\n====> Exception occurred while updating jira ticket process status into the table 'DQ_JIRA_MSG_QUEUE'"+e.getMessage());
		}
		return updateStatus;
	}

	@Override
	public boolean deleteAllPublishedJiraTickets() {
		boolean updateStatus = false;
		try {
			String sql = "delete from DQ_JIRA_MSG_QUEUE where ticket_process_status='Y' and ticket_submit_status='Y'";
			int count = jdbcTemplate1.update(sql);
			if (count > 0)
				updateStatus = true;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"\n====> Exception occurred while deleting jira tickets from the table 'DQ_JIRA_MSG_QUEUE'"+e.getMessage());
		}
		return updateStatus;
	}

	@Override
	public long getIdDataSchemaByIdData(long idData) {
		long idDataSchema = 0l;
		try {
			String sql = "select idDataSchema from listDataSources where idData=" + idData;
			LOG.debug("sql "+sql);
			idDataSchema = jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idDataSchema;
	}

	@Override
	public Properties getPropertiesFromDB(String propertyCategory) {
		LOG.debug("\n====>Loading properties for the category [" + propertyCategory + "] ...");

		Properties prop = new Properties();

		List<DatabuckProperties> propertyFieldList = new ArrayList<>();
		try {

			String sql = " select a.*, b.property_category_name from databuck_property_details a, databuck_properties_master b where a.property_category_id = b.property_category_id and  b.property_category_name=?";

			RowMapper<DatabuckProperties> rowMapper = (rs, i) -> {
				String propertyCategoryName = rs.getString("property_category_name");
				String propertyName = rs.getString("property_name");
				String propertyValue = rs.getString("property_value");

				// Check if property is mandatory
				String is_mandatory_field = rs.getString("is_mandatory_field");
				boolean isMandatory = (is_mandatory_field != null && is_mandatory_field.equalsIgnoreCase("Y")) ? true
						: false;

				// Check if property is password
				String is_password_field = rs.getString("is_password_field");
				boolean isPassword = (is_password_field != null && is_password_field.equalsIgnoreCase("Y")) ? true
						: false;

				// Check if property value is encrypted
				String is_value_encrypted = rs.getString("is_value_encrypted");
				boolean isEncrypted = (is_value_encrypted != null && is_value_encrypted.equalsIgnoreCase("Y")) ? true
						: false;

				boolean warning = (isMandatory && (propertyValue == null || propertyValue.isEmpty())) ? true : false;

				if (isPassword || isEncrypted)
					LOG.debug("Property name:[" + propertyName + "]  value:[Encrypted Data - Do not display]");
				else
					LOG.debug("Property name:[" + propertyName + "]  value:[" + propertyValue + "]");

				// If value is encrypted, decrypt and set it
				if (isEncrypted && propertyValue != null && !propertyValue.trim().isEmpty()) {
					try {
						StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
						decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
						propertyValue = decryptor.decrypt(propertyValue);

					} catch (Exception e) {
						LOG.error("\n====> Exception occurred while decrypting the encrypted property:["
								+ propertyName + "] of Category:[" + propertyCategoryName
								+ "]!!, Please check and update correct value.");
						LOG.error("exception "+e.getMessage());
						e.printStackTrace();
					}
				}
				DatabuckProperties propertyFields = new DatabuckProperties();
				propertyFields.setPropertyId(rs.getInt("property_id"));
				propertyFields.setPropertyCategoryId(rs.getInt("property_category_id"));
				propertyFields.setPropertyName(rs.getString("property_name"));
				propertyFields.setPropertyValue(propertyValue);
				propertyFields.setDescription(rs.getString("description"));
				propertyFields.setMandatoryField(isMandatory);
				propertyFields.setPasswordField(isPassword);
				propertyFields.setValueEncrypted(isEncrypted);
				propertyFields.setWarning(warning);
				propertyFields.setPropertyDefaultvalue(rs.getString("property_default_value"));
				propertyFields.setPropertyDataType(rs.getString("property_data_type"));
				propertyFields.setPropRequiresRestart(rs.getString("prop_requires_restart"));
				propertyFields.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));
				return propertyFields;
			};

			propertyFieldList = jdbcTemplate.query(sql, rowMapper, propertyCategory);

			for (DatabuckProperties propertyFields : propertyFieldList) {
				prop.setProperty(propertyFields.getPropertyName(), propertyFields.getPropertyValue());
			}

		} catch (Exception e) {
			LOG.error("\n======> For the category[" + propertyCategory
					+ "] properties could not be loaded from databases");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return prop;
	}


	@Override
	public Map<String, String> getClusterCategoryNameBySchemaId(Long idDataSchema) {

		Map<String, String>  clusterCategoryName = new HashMap<>();
		SqlRowSet sqlResult = null;
		clusterCategoryName.put("cluster_property_category", "cluster");
		try {
			String sql = "SELECT cluster_property_category, cluster_policy_id  FROM listDataSchema where idDataSchema=" + idDataSchema;
			LOG.debug(sql);
			sqlResult = jdbcTemplate.queryForRowSet(sql);
			if(sqlResult.next()) {
				clusterCategoryName.put("cluster_property_category",sqlResult.getString("cluster_property_category"));
				clusterCategoryName.put("cluster_policy_id",sqlResult.getString("cluster_policy_id"));
			}
		} catch (EmptyResultDataAccessException e) {
			LOG.error(
					"Exception occurred while reading ClusterCategoryNameBySchemaId, hence reading default value :"
							+ e.getMessage());
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return clusterCategoryName;
	}

	@Override
	public String getMaxUniqueIdByIdApp(Long idApp) {
		String uniqueId = "";
		try {
			String Query = "select max(uniqueId) from runScheduledTasks where idApp=? and status in ('queued','started','in progress')";
			uniqueId = jdbcTemplate.queryForObject(Query, String.class, idApp);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public JiraIntegrationBean getJIRATicketDetailsByTaskUniqueId(String taskUniqueId) {
		JiraIntegrationBean result = null;
		try {
			// Get the rows from DQ_JIRA_MSG_QUEUE which are not yet submitted;
			String sql = "select * from DQ_JIRA_MSG_QUEUE where ticket_process_status='N' and ticket_submit_status='N' and task_unique_id=? limit 1";

			RowMapper<JiraIntegrationBean> rowMapper = (rs, i) -> {
				JiraIntegrationBean jiraIntegrationBean = new JiraIntegrationBean();
				jiraIntegrationBean.setId(rs.getLong("id"));
				jiraIntegrationBean.setMsgBody(rs.getString("msg_body"));
				jiraIntegrationBean.setTicketProcessStatus(rs.getString("ticket_process_status"));
				jiraIntegrationBean.setTicketSubmitStatus(rs.getString("ticket_submit_status"));
				jiraIntegrationBean.setCreatedAt(rs.getTimestamp("createdAt").toString());

				return jiraIntegrationBean;
			};
			List<JiraIntegrationBean> jiraIntegrationBeanList = jdbcTemplate1.query(sql, rowMapper, taskUniqueId);
			if (jiraIntegrationBeanList != null && jiraIntegrationBeanList.size() > 0) {
				result = jiraIntegrationBeanList.get(0);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<Long, String> getListScheduleDatawithDomain(long projectId, List<Project> projList, long domainId) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
			// jdbcTemplate.queryForRowSet("select idSchedule,name from listSchedule where
			// project_id="+projectId);
			// SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet("select
			// idSchedule,name from listSchedule");
			SqlRowSet queryForRowSet = jdbcTemplate
					.queryForRowSet("select idSchedule,name from listSchedule where project_id in (" + listprojids
							+ ") and domain_id = " + domainId);
			Map<Long, String> map = new HashMap<Long, String>();
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return map;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int insertIntolistSchedule(String name, String description, String frequency, String scheduledDay,
			String day, String scheduleTimer, long projectId, long domainId) {
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql,idColumn;
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "insert into listSchedule(time,name,description,frequency,scheduleDay,project_id,exceptionMatching,domain_id) "
					+ " VALUES (?::time,?,?,?,?,?,'N',?)";
			idColumn="idSchedule";
			
		} else {
			sql = "insert into listSchedule(time,name,description,frequency,scheduleDay,project_id,exceptionMatching,domain_id) "
					+ " VALUES (?,?,?,?,?,?,'N',?)";
			idColumn="idSchedule";
		}
		if (frequency.equalsIgnoreCase("weekly")) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[]{idColumn});
					pst.setString(1, scheduleTimer);
					pst.setString(2, name);
					pst.setString(3, description);
					pst.setString(4, frequency);
					pst.setString(5, scheduledDay);
					pst.setLong(6, projectId);
					pst.setLong(7, domainId);
					return pst;
				}
			}, keyHolder);
			
			return keyHolder.getKey().intValue();
				
			}else {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[]{idColumn});
					pst.setString(1, scheduleTimer);
					pst.setString(2, name);
					pst.setString(3, description);
					pst.setString(4, frequency);
					pst.setString(5, day);
					pst.setLong(6, projectId);
					pst.setLong(7, domainId);
					return pst;
				}
			}, keyHolder);

			return keyHolder.getKey().intValue();
		}
	}

	@Override
	public List<ListSchedule> getSchedulers(long projectId, List<Project> projList, long domainId) {
		try {
			String listprojids = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
			String Query = "select *,p.projectName from listSchedule ls join project p on ls.project_id = p.idProject where ls.project_id in ("
					+ listprojids + ") and domain_id = " + domainId;
			LOG.debug(" getSchedulers query: " + Query);
			RowMapper<ListSchedule> rowMapper = (rs, i) -> {
				ListSchedule ls = new ListSchedule();
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setFrequency(rs.getString("frequency"));
				ls.setScheduleDay(rs.getString("scheduleDay"));
				ls.setTime(rs.getString("time"));
				ls.setProjectName(rs.getString("projectName"));

				return ls;
			};

			List<ListSchedule> listSchedule = jdbcTemplate.query(Query, rowMapper);
			return listSchedule;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ListSchedule> getSchedulersForProjectId(long projectId, long domainId) {
		try {
			String Query = "select *,p.projectName from listSchedule ls join project p on ls.project_id = p.idProject where ls.project_id = "
					+ projectId + " and domain_id = " + domainId;
			LOG.debug(" getSchedulers query: " + Query);
			RowMapper<ListSchedule> rowMapper = (rs, i) -> {
				ListSchedule ls = new ListSchedule();
				ls.setIdSchedule(rs.getLong("idSchedule"));
				ls.setName(rs.getString("name"));
				ls.setDescription(rs.getString("description"));
				ls.setFrequency(rs.getString("frequency"));
				ls.setScheduleDay(rs.getString("scheduleDay"));
				ls.setTime(rs.getString("time"));
				ls.setProjectName(rs.getString("projectName"));

				return ls;
			};

			List<ListSchedule> listSchedule = jdbcTemplate.query(Query, rowMapper);
			return listSchedule;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<LoggingActivity> getLatestAuditTrailDetails(String toDate, String fromDate) {
		List<LoggingActivity> loggingActivitiesData = new ArrayList<>();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT * FROM logging_activity WHERE activity_log_time::date BETWEEN '"+fromDate+"'::date AND '"+toDate+"'::date AND activity_name IS NOT NULL " +
						"AND activity_name <> '' AND activity_name != 'N' ORDER BY row_id DESC LIMIT 10000";
			} else {
				sql = "SELECT * FROM logging_activity WHERE STR_TO_DATE(activity_log_time, '%Y-%m-%d') BETWEEN '"+fromDate+"' AND '"+toDate+"' AND activity_name IS NOT NULL " +
						"AND activity_name <> '' AND activity_name  != 'N' ORDER BY row_id DESC LIMIT 10000;";
			}
			RowMapper<LoggingActivity> rowMapper = (rs, i) -> {
				LoggingActivity loggingActivity = new LoggingActivity();
				loggingActivity.setUserId(rs.getInt("user_id"));
				loggingActivity.setUserName(rs.getString("user_name"));
				loggingActivity.setAccessUrl(rs.getString("access_url"));
				loggingActivity.setDatabuckFeature(rs.getString("databuck_feature"));
				loggingActivity.setSessionId(rs.getString("session_id"));
				loggingActivity.setActivityLogTime(rs.getString("activity_log_time"));
				loggingActivity.setEntityId(rs.getInt("entity_id"));
				loggingActivity.setEntityName(rs.getString("entity_name"));
				loggingActivity.setActivityName(rs.getString("activity_name"));
				return loggingActivity;
			};
			loggingActivitiesData = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return loggingActivitiesData;
	}

	@Override
	public void addAuditTrailDetail(Long userId, String userName, String moduleName, String date, Long entityId,
			String activityName, String entityName) {
		try {
			String query = "INSERT INTO logging_activity (user_id, user_name, databuck_feature, activity_log_time, entity_id, activity_name, entity_name) VALUES (?,?,?,?,?,?,?)";
			LOG.debug("Query: " + query);
			jdbcTemplate.update(query, userId, userName, moduleName, date, entityId, activityName, entityName);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int clearLogs(String fromDate, String toDate) {
		try {
			String sql = "DELETE FROM logging_activity WHERE activity_log_time BETWEEN '" + fromDate + "' AND '"
					+ toDate + "'";
			return jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String insertRunGlobalRuleValidateTask(String deployMode, String triggeredBy, long processId, JSONObject inputJson) {
		String uniqueId = "";
		LOG.info("\n====>Inside insertRunGlobalRuleValidateTask ...");
		try {
			long idData = inputJson.getLong("idData");
			long idRightData = inputJson.getLong("idRightData");
			String ruleName = inputJson.getString("ruleName");
			String ruleType = inputJson.getString("ruleType");
//			String ruleExpression = inputJson.getString("ruleExpression");
			String ruleExpression = inputJson.optString("expression", "");
			String matchingRules = inputJson.optString("matchingRules", "");
			String filterCondition = inputJson.optString("filterCondition", "");
			String rightTemplateFilterCondition = inputJson.optString("rightTemplateFilterCondition", "");
			String status="started";
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			uniqueId = "VCR_"+idData + "_" + String.valueOf(currentDate.getTime());
			// Check if triggeredBy is empty
			if (triggeredBy == null || triggeredBy.trim().isEmpty()) {
				triggeredBy = "system";
			}

			jdbcTemplate.update(
					"insert into run_global_rule_validate_tasks(unique_id,process_id,deploy_mode,start_time,triggered_by,triggered_by_host,execution_percentage,id_data,id_right_data,rule_name,rule_type,rule_expression,matching_rules,filter_condition,right_template_filter_condition,status) " +
							"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					uniqueId, processId, deployMode, currentDate, triggeredBy, triggeredBy, 0.0, idData, idRightData, ruleName, ruleType, ruleExpression, matchingRules, filterCondition, rightTemplateFilterCondition,status);
			LOG.info("\n====>detaild inserted into run_global_rule_validate_tasks");
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public void updateRunGlobalRuleUpdateTask(String uniqueId, long processId) {
		try {
			LOG.info("\n====>Inside updateRunScheduledTaskPid ...");
			String sql = "update run_global_rule_validate_tasks set process_id=? where unique_id=?";
			LOG.debug(sql);
			jdbcTemplate.update(sql, processId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String getclusterPropertyCategoryForApplicationId(long applicationId) {
			// Get current host name
			String clusterPropertyCategory = "";
			try {
				String query = "select ls.cluster_property_category as cluster_property_category from listDataSchema as ls join listDataAccess as ld on ls.idDataSchema=ld.idDataSchema join listDataAccess la on la.idDataSchema = ld.idDataSchema join listApplications las on la.idData=las.idData where las.idApp=? limit 1";
				clusterPropertyCategory = jdbcTemplate.queryForObject(query, String.class, applicationId);
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred in getClusterPropertyById !!");
				e.printStackTrace();
			}
			return clusterPropertyCategory;
		}


	@Override
	public String insertRunGlobalFilterValidateTask(String deployMode, String triggeredBy, long processId, JSONObject inputJson) {
		String uniqueId = "";
		LOG.info("\n====>Inside insertRunGlobalFilterValidateTask ...");
		try {
			long idData = inputJson.getLong("idData");
			String filterCondition = inputJson.optString("filterCondition", "");
			String status="started";
			Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
			uniqueId = "VGF_"+idData + "_" + String.valueOf(currentDate.getTime());
			// Check if triggeredBy is empty
			if (triggeredBy == null || triggeredBy.trim().isEmpty()) {
				triggeredBy = "system";
			}

			jdbcTemplate.update(
					"insert into run_global_filter_validate_tasks(unique_id,process_id,deploy_mode,start_time,triggered_by,execution_percentage,id_data,filter_condition,status) " +
							"values(?,?,?,?,?,?,?,?,?)",
					uniqueId, processId, deployMode, currentDate, triggeredBy, 0.0, idData,filterCondition, status);
			LOG.info("\n====>detaild inserted into run_global_rule_validate_tasks");
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return uniqueId;
	}

	@Override
	public void updateRunGlobalFilterUpdateTask(String uniqueId, long processId) {
		try {
			LOG.info("\n====>Inside updateRunGlobalFilterUpdateTask ...");
			String sql = "update run_global_filter_validate_tasks set process_id=? where unique_id=?";
			LOG.debug(sql);
			jdbcTemplate.update(sql, processId, uniqueId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}
	@Override
	public List<LoginTrail> getLatestLoginActivityDetails(String toDate, String fromDate) {
		List<LoginTrail> loggingActivitiesData = new ArrayList<>();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT * FROM databuck_login_access_logs WHERE activity_log_time::date BETWEEN '"+fromDate+"'::date AND '"+toDate+"'::date ORDER BY row_id DESC LIMIT 10000";
			} else {
				sql = "SELECT * FROM databuck_login_access_logs WHERE STR_TO_DATE(activity_log_time, '%Y-%m-%d') BETWEEN '"+fromDate+"' AND '"+toDate+"'ORDER BY row_id DESC LIMIT 10000;";
			}
			RowMapper<LoginTrail> rowMapper = (rs, i) -> {
				LoginTrail loginTrail = new LoginTrail();
				loginTrail.setUserId(rs.getInt("user_id"));
				loginTrail.setUserName(rs.getString("user_name"));
				loginTrail.setAccessUrl(rs.getString("access_url"));
				loginTrail.setDatabuckFeature(rs.getString("databuck_feature"));
				loginTrail.setSessionId(rs.getString("session_id"));
				loginTrail.setActivityLogTime(sdf.format(rs.getTimestamp("activity_log_time")));
				return loginTrail;
			};
			loggingActivitiesData = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return loggingActivitiesData;
	}
}