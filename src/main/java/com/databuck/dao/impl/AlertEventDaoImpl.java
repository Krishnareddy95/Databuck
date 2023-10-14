package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.AlertEventMaster;
import com.databuck.bean.AlertEventSubscription;
import com.databuck.bean.DatabuckAlertLog;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.AlertEventDao;

@Repository
public class AlertEventDaoImpl implements AlertEventDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(AlertEventDaoImpl.class);

	@Override
	public JSONArray getAllAlertEvents() {
		JSONArray jsonArray = new JSONArray();

		try {
			String sql = "select * from alert_event_master";
			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
			
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("eventId", sqlRowSet.getInt("event_id"));
				jsonObject.put("eventName", sqlRowSet.getString("event_name"));
				jsonObject.put("eventModuleName", sqlRowSet.getString("event_module_name").replaceAll("([a-z])([A-Z])","$1 $2").trim());
				jsonObject.put("eventCommunicationType", sqlRowSet.getString("event_communication_type"));
				jsonObject.put("eventMessageCode", sqlRowSet.getString("event_message_code"));
				jsonObject.put("eventCompletionMessage", sqlRowSet.getString("event_completion_message"));
				jsonObject.put("eventMessageBody", sqlRowSet.getString("event_message_body"));
				jsonObject.put("eventMessageSubject", sqlRowSet.getString("event_message_subject"));
				jsonArray.put(jsonObject);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public List<AlertEventMaster> getListOfAlertEvents() {
		List<AlertEventMaster> AlertEventList = null;

		try {
			String sql = "select * from alert_event_master";
			LOG.debug(sql);
			RowMapper<AlertEventMaster> rowMapper = (rs, i) -> {
				AlertEventMaster alertEventMaster = new AlertEventMaster();
				alertEventMaster.setEventId(rs.getInt("event_id"));
				alertEventMaster.setEventName(rs.getString("event_name"));
				alertEventMaster.setEventModuleName(rs.getString("event_module_name"));
				alertEventMaster.setEventCommunicationType(rs.getString("event_communication_type"));
				alertEventMaster.setEventMessageCode(rs.getInt("event_message_code"));
				alertEventMaster.setEventCompletionMessage(rs.getString("event_completion_message"));
				alertEventMaster.setEventCompletionStatus(rs.getString("event_completion_status"));
				alertEventMaster.setEventMessageBody(rs.getString("event_message_body"));
				alertEventMaster.setEventFocusObject(rs.getString("event_focus_object"));
				alertEventMaster.setEventMessageSubject(rs.getString("event_message_subject"));
				return alertEventMaster;
			};
			AlertEventList = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return AlertEventList;
	}

	@Override
	public int updateAlertEventMessage(int alert_event_id, String alert_event_message) {
		try {
			String sql = "update alert_event_master set event_message_body=? where event_id=?";
			LOG.debug(sql);
			int update = jdbcTemplate.update(sql, alert_event_message, alert_event_id);
			return update;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public AlertEventMaster getAlertEventById(int eventId) {
		AlertEventMaster alertEvent = null;
		try {
			String sql = "select * from alert_event_master where  event_id=" + eventId;
			LOG.debug(sql);

			RowMapper<AlertEventMaster> rowMapper = (rs, i) -> {
				AlertEventMaster alertEventMaster = new AlertEventMaster();
				alertEventMaster.setEventId(rs.getInt("event_id"));
				alertEventMaster.setEventName(rs.getString("event_name"));
				alertEventMaster.setEventModuleName(rs.getString("event_module_name"));
				alertEventMaster.setEventCommunicationType(rs.getString("event_communication_type"));
				alertEventMaster.setEventMessageCode(rs.getInt("event_message_code"));
				alertEventMaster.setEventCompletionMessage(rs.getString("event_completion_message"));
				alertEventMaster.setEventCompletionStatus(rs.getString("event_completion_status"));
				alertEventMaster.setEventMessageBody(rs.getString("event_message_body"));
				alertEventMaster.setEventFocusObject(rs.getString("event_focus_object"));
				alertEventMaster.setEventMessageSubject(rs.getString("event_message_subject"));
				return alertEventMaster;
			};
			List<AlertEventMaster> AlertEventList = jdbcTemplate.query(sql, rowMapper);

			if (AlertEventList != null && AlertEventList.size() > 0)
				alertEvent = AlertEventList.get(0);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return alertEvent;
	}

	@Override
	public JSONArray getAllCommunicationModes() {
		JSONArray jsonArray = new JSONArray();
		try {
			String sql = "select * from alert_communication_modes";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("commModeId", sqlRowSet.getInt("comm_mode_id"));
				jsonObject.put("commModeName", sqlRowSet.getString("comm_mode_name"));

				jsonArray.put(jsonObject);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public JSONArray getDatabuckAlertLogs(HashMap<String, String> oPaginationParms) {
		JSONArray jsonArray = new JSONArray();
		String sOption1Sql, sOption2Sql, sOption3Sql, sOption4Sql;
		try {

			sOption1Sql = String.format(
					" Where (job_execution_date between '%1$s' and '%2$s') and project_id IN(%3$s) \n",
					oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"),
					oPaginationParms.get("ProjectIds"));

			sOption2Sql = "and 1 = case when alert_message like 'LIKE-TEXT' then 1 else 0 end \n"
					.replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			sOption3Sql = String.format("and a.event_id IN(%1$s)", oPaginationParms.get("EventIds"));
			sOption4Sql = String.format("and a.task_name IN('%1$s')",
					oPaginationParms.get("TaskName").toLowerCase().replace(" ", "").replace(",", "','"));

			String sql = "select a.*,b.projectName,c.event_name as eventName from databuck_alert_log a join project b "
					+ "on a.project_id = b.idProject join alert_event_master c on a.event_id=c.event_id";

			if (oPaginationParms != null && !oPaginationParms.isEmpty()) {
				sql = sql + sOption1Sql;

				if (!oPaginationParms.get("EventIds").isEmpty() || oPaginationParms.get("EventIds").length() > 0)
					sql = sql + sOption3Sql;

				if (!oPaginationParms.get("TaskName").isEmpty() || oPaginationParms.get("TaskName").length() > 0)
					sql = sql + sOption4Sql;

				if (!oPaginationParms.get("SearchText").isEmpty())
					sql = sql + sOption2Sql;

				sql = sql + "limit 1000;";
			}

			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("alertLogId", sqlRowSet.getLong("alert_log_id"));
				jsonObject.put("publishDate", sqlRowSet.getString("alert_publish_date") == null ? ""
						: sqlRowSet.getString("alert_publish_date"));
				jsonObject.put("jobExecutionDate", sqlRowSet.getString("job_execution_date").substring(0, 10));
				jsonObject.put("taskId", sqlRowSet.getString("task_id"));
				jsonObject.put("taskUniqueId", sqlRowSet.getString("task_unique_id"));
				jsonObject.put("jobRunNumber", sqlRowSet.getLong("job_run_number"));
				jsonObject.put("projectName", sqlRowSet.getString("projectName"));
				jsonObject.put("eventName", sqlRowSet.getString("eventName"));
				jsonObject.put("taskName", sqlRowSet.getString("task_name"));
				jsonObject.put("alertMessage", sqlRowSet.getString("alert_message"));
				jsonObject.put("isEventSubscribed", sqlRowSet.getString("is_event_subscribed"));
				jsonObject.put("isAlertPublished", sqlRowSet.getString("is_event_published"));
				jsonObject.put("executionErrors",
						sqlRowSet.getString("execution_errors") == null ? "" : sqlRowSet.getString("execution_errors"));

				jsonArray.put(jsonObject);

			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public JSONArray getSubscribedEvents() {
		JSONArray jsonArray = new JSONArray();
		try {
			String sql = "select a.alert_sub_id,b.projectName, c.event_name as eventName,d.comm_mode_name,"
					+ "a.is_global_subscription, a.communication_values from alert_event_subscriptions a left join project b on a.project_id = b.idProject join "
					+ " alert_event_master c on a.event_id=c.event_id join alert_communication_modes d on a.comm_mode_id = d.comm_mode_id";

			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("alertSubId", sqlRowSet.getString("alert_sub_id"));
				String projectName = sqlRowSet.getString("projectName");
				if (projectName == null || projectName.trim().isEmpty()) {
					projectName = "";
				}
				jsonObject.put("projectName", projectName);
				jsonObject.put("eventName", sqlRowSet.getString("eventName"));
				jsonObject.put("commModeName", sqlRowSet.getString("comm_mode_name"));
				jsonObject.put("isGlobalSubscription", sqlRowSet.getString("is_global_subscription"));
				jsonObject.put("communicationValues", sqlRowSet.getString("communication_values") == null ? ""
						: sqlRowSet.getString("communication_values"));

				jsonArray.put(jsonObject);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public JSONArray getAllAlertEventNameList() {
		JSONArray jsonArray = new JSONArray();
		try {
			String sql = "select event_name,event_id from alert_event_master ";
			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", sqlRowSet.getString("event_name"));
				jsonObject.put("value", sqlRowSet.getInt("event_id"));
				jsonArray.put(jsonObject);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public List<DatabuckAlertLog> getNonPublishedDatabuckAlertLogs() {
		List<DatabuckAlertLog> databuckAlertLogList = new ArrayList<DatabuckAlertLog>();
		try {
			String alertEventDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String sql = "select * from  databuck_alert_log  where is_event_subscribed = 'Y' and is_event_published= 'N' and job_execution_date ='"+alertEventDate+"' order by alert_log_id asc limit 20";

			databuckAlertLogList = jdbcTemplate.query(sql, new RowMapper<DatabuckAlertLog>() {

				@Override
				public DatabuckAlertLog mapRow(ResultSet rs, int rowNum) throws SQLException {
					DatabuckAlertLog databuckAlertLog = new DatabuckAlertLog();
					databuckAlertLog.setAlertId(rs.getLong("alert_log_id"));
					databuckAlertLog.setAlertPublishDate(rs.getString("alert_publish_date"));
					databuckAlertLog.setJobExecutionDate(rs.getString("job_execution_date"));
					databuckAlertLog.setTaskUniqueId(rs.getString("task_unique_id"));
					databuckAlertLog.setJobRunNumber(rs.getLong("job_run_number"));
					databuckAlertLog.setProjectId(rs.getLong("project_id"));
					databuckAlertLog.setEventid(rs.getInt("event_id"));
					databuckAlertLog.setTaskId(rs.getLong("task_id"));
					databuckAlertLog.setTaskName(rs.getString("task_name"));
					databuckAlertLog.setAlertMessage(rs.getString("alert_message"));
					databuckAlertLog.setExecutionErrors(rs.getString("execution_errors"));
					databuckAlertLog.setIsEventPublished(rs.getString("is_event_published"));
					databuckAlertLog.setIsEventSubscribed(rs.getString("is_event_subscribed"));
					databuckAlertLog.setAlertMessageSubject(rs.getString("event_message_subject"));
					return databuckAlertLog;
				}

			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return databuckAlertLogList;
	}

	@Override
	public List<AlertEventSubscription> getAlertSubscriptionByEventId(long projectId, int eventId) {
		List<AlertEventSubscription> alertEventSubscriptions = null;
		try {
			String sql = "select a.*,b.comm_mode_name from alert_event_subscriptions a join alert_communication_modes b on a.comm_mode_id=b.comm_mode_id "
					+ "where a.project_id=" + projectId + " and event_id=" + eventId;

			LOG.debug(sql);
			alertEventSubscriptions = jdbcTemplate.query(sql, new RowMapper<AlertEventSubscription>() {
				@Override
				public AlertEventSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
					AlertEventSubscription subscriptions = new AlertEventSubscription();
					subscriptions.setAlertSubId(rs.getInt("alert_sub_id"));
					subscriptions.setProjectId(rs.getInt("project_id"));
					subscriptions.setEventId(rs.getInt("event_id"));
					subscriptions.setCommModeId(rs.getInt("comm_mode_id"));
					subscriptions.setIsGlobalSubscription(rs.getString("is_global_subscription"));
					subscriptions.setCommunicationValues(rs.getString("communication_values"));
					subscriptions.setCommunicaionMode(rs.getString("comm_mode_name"));
					return subscriptions;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return alertEventSubscriptions;
	}

	public JSONArray getAllSubscribedEventByEventId(Long eventId) {
		JSONArray jsonArray = new JSONArray();
		try {
			String sql = "select a.alert_sub_id,b.projectName,d.comm_mode_name,"
					+ "a.is_global_subscription, a.communication_values from alert_event_subscriptions a left join project b on a.project_id = b.idProject join "
					+ " alert_event_master c on a.event_id=c.event_id join alert_communication_modes d on a.comm_mode_id = d.comm_mode_id and a.event_id="
					+ eventId;

			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("alertSubId", sqlRowSet.getString("alert_sub_id"));
				String projectName = sqlRowSet.getString("projectName");
				if (projectName == null || projectName.trim().isEmpty()) {
					projectName = "";
				}
				jsonObject.put("projectName", projectName);
				jsonObject.put("commModeName", sqlRowSet.getString("comm_mode_name"));
				jsonObject.put("isGlobalSubscription", sqlRowSet.getString("is_global_subscription"));
				jsonObject.put("communicationValues", sqlRowSet.getString("communication_values") == null ? ""
						: sqlRowSet.getString("communication_values"));

				jsonArray.put(jsonObject);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	public boolean updateDatabuckAlertLogsPublicationDetails(long alertLogId, String isPublished) {
		try {
			String sql = "update databuck_alert_log set is_event_published='" + isPublished + "' where alert_log_id="
					+ alertLogId;
			int count = jdbcTemplate.update(sql);
			if (count > 0)
				return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int addAlertEventSubscription(AlertEventSubscription alertEventSubscription) {
		try {
			String sql = "insert into alert_event_subscriptions (project_id,event_id,comm_mode_id,is_global_subscription,communication_values) values(?,?,?,?,?)";
			return jdbcTemplate.update(sql, alertEventSubscription.getProjectId(), alertEventSubscription.getEventId(),
					alertEventSubscription.getCommModeId(), alertEventSubscription.getIsGlobalSubscription(),
					alertEventSubscription.getCommunicationValues());
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isAlertEventSubscriptionExists(AlertEventSubscription alertEventSubscription) {
		boolean is_sub_exists = false;
		try {
			String sql = "select count(*) from alert_event_subscriptions where project_id=? and event_id=? and comm_mode_id=? and is_global_subscription=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, alertEventSubscription.getProjectId(),
					alertEventSubscription.getEventId(), alertEventSubscription.getCommModeId(),
					alertEventSubscription.getIsGlobalSubscription());
			if (count != null && count > 0)
				is_sub_exists = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return is_sub_exists;
	}

	@Override
	public int updateAlertEventSubscription(AlertEventSubscription alertEventSubscription) {
		try {
			String sql = "update alert_event_subscriptions set communication_values=? where project_id=? and event_id=? and comm_mode_id=? and is_global_subscription=?";
			return jdbcTemplate.update(sql, alertEventSubscription.getCommunicationValues(),
					alertEventSubscription.getProjectId(), alertEventSubscription.getEventId(),
					alertEventSubscription.getCommModeId(), alertEventSubscription.getIsGlobalSubscription());
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<String> getAlertEventNameList() {
		try {
			String sql = "select event_name from alert_event_master";
			List<String> alertEventNames = jdbcTemplate.queryForList(sql, String.class);
			if (alertEventNames != null && alertEventNames.size() > 0)
				return alertEventNames;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> geTaskNameList() {
		try {
			String sql = "select distinct(lower(event_module_name)) from alert_event_master";
			List<String> taskNames = jdbcTemplate.queryForList(sql, String.class);
			if (taskNames != null && taskNames.size() > 0)
				return taskNames;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean updateAlertSubscriptionCommunicationValues(long alertSubscriptionId, String communicationValues) {
		boolean status = false;
		try {
			String sql = "update alert_event_subscriptions set communication_values=? where alert_sub_id=?";
			int count = jdbcTemplate.update(sql, communicationValues, alertSubscriptionId);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public JSONObject getEventDetailsByEventName(String eventName) {
		JSONObject eventObj = new JSONObject();
		try {
			String sql = "select event_id,event_message_body,event_message_subject from alert_event_master where event_name like '%"
					+ eventName + "%'";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				eventObj.put("event_id", queryForRowSet.getInt("event_id"));
				eventObj.put("event_message_body", queryForRowSet.getString("event_message_body"));
				eventObj.put("event_message_subject", queryForRowSet.getString("event_message_subject"));
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return eventObj;
	}

	@Override
	public boolean isEventSubscribed(int eventId, long projectId) {
		try{
			String sql= "select count(*) from alert_event_subscriptions where event_id=? and is_global_subscription='Y'";
			int count= jdbcTemplate.queryForObject(sql,Integer.class,eventId);
			if(count > 0)
				return true;
			else {
				count=0;
				sql = "select count(*) from alert_event_subscriptions where event_id=? and project_id=?";
				count = jdbcTemplate.queryForObject(sql, Integer.class, eventId, projectId);
				if (count > 0)
					return true;
			}
		}catch (Exception e){
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean saveDatabuckAlertLog(DatabuckAlertLog databuckAlertLog) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "insert into databuck_alert_log(job_execution_date,task_unique_id,job_Run_number,project_id,\n"
						+ "    event_id,task_id,task_name,alert_message,is_event_subscribed,is_event_published,execution_errors,event_message_subject) values (\n"
						+ "    ?::date,?,?,?,?,?,?,?,?,?,?,?)";
			} else {
				sql = "insert into databuck_alert_log(job_execution_date,task_unique_id,job_Run_number,project_id,\n"
						+ "    event_id,task_id,task_name,alert_message,is_event_subscribed,is_event_published,execution_errors,event_message_subject) values (\n"
						+ "    ?,?,?,?,?,?,?,?,?,?,?,?)";
			}
			jdbcTemplate.update(sql, databuckAlertLog.getJobExecutionDate(), databuckAlertLog.getTaskUniqueId(),
					databuckAlertLog.getJobRunNumber(), databuckAlertLog.getProjectId(), databuckAlertLog.getEventid(),
					databuckAlertLog.getTaskId(), databuckAlertLog.getTaskName(), databuckAlertLog.getAlertMessage(),
					databuckAlertLog.getIsEventSubscribed(), databuckAlertLog.getIsEventPublished(),
					databuckAlertLog.getExecutionErrors(),databuckAlertLog.getEventMessageSubject());
			return true;

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<AlertEventSubscription> getGlobalAlertSubscriptionByEventId(int eventId) {
		List<AlertEventSubscription> alertEventSubscriptions = null;
		try {
			String sql = "select a.*,b.comm_mode_name from alert_event_subscriptions a join alert_communication_modes b on a.comm_mode_id=b.comm_mode_id "
					+ "where is_global_subscription='Y' and event_id=" + eventId;

			LOG.debug(sql);
			alertEventSubscriptions = jdbcTemplate.query(sql, new RowMapper<AlertEventSubscription>() {
				@Override
				public AlertEventSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
					AlertEventSubscription subscriptions = new AlertEventSubscription();
					subscriptions.setAlertSubId(rs.getInt("alert_sub_id"));
					subscriptions.setProjectId(rs.getInt("project_id"));
					subscriptions.setEventId(rs.getInt("event_id"));
					subscriptions.setCommModeId(rs.getInt("comm_mode_id"));
					subscriptions.setIsGlobalSubscription(rs.getString("is_global_subscription"));
					subscriptions.setCommunicationValues(rs.getString("communication_values"));
					subscriptions.setCommunicaionMode(rs.getString("comm_mode_name"));
					return subscriptions;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return alertEventSubscriptions;
	}

	public int deleteEventSubscriptionById(int alertSubId) {
		try {
			String sql = "delete from alert_event_subscriptions where alert_sub_id=" + alertSubId;
			return jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public String getEventNameByEventId(int eventId){
		try{
			String sql="Select event_name from alert_event_master where event_id="+eventId;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				return queryForRowSet.getString("event_name");
			}
		}catch (Exception e){
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public String  getEventNameByAlertSubId(Long alertSubId){
		try{
			String sql="select event_name from alert_event_master where event_id=(select event_id from alert_event_subscriptions where alert_sub_id="+alertSubId+")";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				return queryForRowSet.getString("event_name");
			}
		}catch (Exception e){
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
