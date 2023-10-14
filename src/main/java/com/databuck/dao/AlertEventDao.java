package com.databuck.dao;

import com.databuck.bean.AlertEventMaster;
import com.databuck.bean.AlertEventSubscription;
import com.databuck.bean.DatabuckAlertLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface AlertEventDao {

	public JSONArray getAllAlertEvents();

	public int updateAlertEventMessage(int alert_event_id, String alert_event_message);

	public AlertEventMaster getAlertEventById(int eventId);

	public JSONArray getAllCommunicationModes();

	public List<DatabuckAlertLog> getNonPublishedDatabuckAlertLogs();

	public List<AlertEventSubscription> getAlertSubscriptionByEventId(long projectId, int eventId);

	public boolean updateDatabuckAlertLogsPublicationDetails(long alertLogId, String isPublished);

	public JSONArray getSubscribedEvents();

	public JSONArray getDatabuckAlertLogs(HashMap<String, String> oPaginationParms);

	public List<AlertEventMaster> getListOfAlertEvents();

	public JSONArray getAllSubscribedEventByEventId(Long eventId);

	public int addAlertEventSubscription(AlertEventSubscription alertEventSubscription);

	public boolean isAlertEventSubscriptionExists(AlertEventSubscription alertEventSubscription);

	public int updateAlertEventSubscription(AlertEventSubscription alertEventSubscription);
	
	public List<String> geTaskNameList();
	
	public List<String> getAlertEventNameList();

	public boolean updateAlertSubscriptionCommunicationValues(long alertSubscriptionId, String communicationValues);

	public JSONObject getEventDetailsByEventName(String eventName) ;

	public boolean isEventSubscribed(int eventId, long projectId) ;

	public boolean saveDatabuckAlertLog(DatabuckAlertLog databuckAlertLog);

	public JSONArray getAllAlertEventNameList();

	public List<AlertEventSubscription> getGlobalAlertSubscriptionByEventId(int eventId);

	public int deleteEventSubscriptionById(int alertSubId);

	public String  getEventNameByAlertSubId(Long alertSubId);

	public String getEventNameByEventId(int eventId);

}
