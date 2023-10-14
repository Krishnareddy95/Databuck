package com.databuck.restcontroller;

import com.databuck.bean.User;
import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import com.databuck.service.IUserService;
import jnr.ffi.annotations.In;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.databuck.bean.AlertEventMaster;
import com.databuck.bean.AlertEventSubscription;
import com.databuck.dao.AlertEventDao;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.service.AlertManagementService;
import com.databuck.service.ExecutiveSummaryService;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*")
@RestController
public class AlertManagementRestController {

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private AlertEventDao alertEventDao;

	@Autowired
	private AlertManagementService alertManagementService;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private Properties integrationProperties;

	@Autowired
	private IUserService iUserService;

	@Autowired
	ITaskDAO iTaskDAO;
	
	private static final Logger LOG = Logger.getLogger(AlertManagementRestController.class);

	@RequestMapping(value = "/dbconsole/getAllAlertEvents", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllAlertEvents(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAllAlertEvents - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					// get all event list
					JSONArray alertEventList = alertEventDao.getAllAlertEvents();
					// validate eventlist
					if (alertEventList != null && alertEventList.length() > 0) {
						status = "success";
						message = "Alert Event List Fetched successfully";
						LOG.info(message);
						result.put("eventList", alertEventList);
					} else {
						message = "Failed to fetch alert events";
						LOG.error(message);
					}

				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
			LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getAllAlertEvents - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAllSubscribedEvents", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllSubscribedEvents(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAllSubscribedEvents - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		JSONArray subscribedEventList = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					// get all subscribedEventList
					subscribedEventList = alertEventDao.getSubscribedEvents();

					// validate subscribedEventList
					if (subscribedEventList != null && subscribedEventList.length() > 0) {
						result.put("subscribedEventList", subscribedEventList);
						status = "success";
						message = "Subscribed Event List Fetched Successfully";
						LOG.info(message);
					} else {
						message = "Failed To Fetch Subscribed Events";
						LOG.error(message);
					}

					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_ALERT_NOTIFICATIONS, formatter.format(new Date()), 0l,
							DatabuckConstants.ACTIVITY_TYPE_VIEWED,"View Alert Events");
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getAllSubscribedEvents - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAlertEventById", method = RequestMethod.POST)
	public ResponseEntity<Object> getAlertEventById(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getAlertEventById - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						int eventId = inputJson.getInt("eventId");
						AlertEventMaster alertEventMaster = alertEventDao.getAlertEventById(eventId);

						if (alertEventMaster != null) {
							JSONObject alertEventObj = new JSONObject();
							alertEventObj.put("eventId", eventId);
							alertEventObj.put("eventName", alertEventMaster.getEventName());
							alertEventObj.put("eventMessegeSubject", alertEventMaster.getEventMessageSubject());
							alertEventObj.put("alertEventMessege", alertEventMaster.getEventMessageBody());
							status = "success";
							message = "Alert Event Fetched Successfully";
							LOG.info(message);
							result.put("alertEvent", alertEventObj);
						} else {
							message = "Invalid eventId";
							LOG.error(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result); 
		LOG.info("dbconsole/getAlertEventById - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAllCommunicationModes", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllCommunicationModes(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAllCommunicationModes - START");
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		JSONArray commModeList = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					// Fetch list of communicationModes
					commModeList = alertEventDao.getAllCommunicationModes();
					// validate commModeList
					if (commModeList != null && commModeList.length() > 0) {
						status = "success";
						message = "Communication Mode List Fetched successfully";
						LOG.info(message);
						result.put("commModeList", commModeList);
					} else {
						message = "Failed to  Fetched Communication Modes";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getAllCommunicationModes - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAllSubscribedEventByEventId", method = RequestMethod.POST)
	public ResponseEntity<Object> getAllSubscribedEventByEventId(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getAllSubscribedEventByEventId - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		JSONArray subscribedEventList = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						Long eventId = inputJson.getLong("eventId");

						// Fetch Subscribed Event List by eventId
						subscribedEventList = alertEventDao.getAllSubscribedEventByEventId(eventId);
						// validate subscribed event list
						if (subscribedEventList != null && subscribedEventList.length() > 0) {
							status = "success";
							message = "Subscribed Event Fetched Successfully";
							LOG.info(message);
							result.put("subscribedEventList", subscribedEventList);
						} else {
							message = "Failed to fetch subscribed details for eventId->[" + eventId + "]";
							LOG.error(message);
							status = "success";
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getAllSubscribedEventByEventId - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/addEventSubscription", method = RequestMethod.POST)
	public ResponseEntity<Object> addEventSubscription(@RequestHeader HttpHeaders headers,
			@RequestBody List<AlertEventSubscription> alertEventSubscriptionList) {
		LOG.info("dbconsole/addEventSubscription - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (alertEventSubscriptionList != null && !alertEventSubscriptionList.isEmpty()) {
						LOG.debug("Getting request parameters  " + alertEventSubscriptionList);
						JSONObject resJson = alertManagementService.addEventSubscription(alertEventSubscriptionList);
						String eventName = alertEventDao.getEventNameByEventId(alertEventSubscriptionList.get(0).getEventId());
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_ALERT_EVENT_SUBSCRIPTION, formatter.format(new Date()), (long) alertEventSubscriptionList.get(0).getEventId(),
								DatabuckConstants.ACTIVITY_TYPE_CREATED, eventName);
						status = resJson.getString("status");
						message = resJson.getString("message");
						LOG.info(message);
					} else{
						message = "Invalid request";
						LOG.error(message);
				}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/addEventSubscription - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/updateEventSubscription", method = RequestMethod.POST)
	public ResponseEntity<Object> updateEventSubscription(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/updateEventSubscription - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						int eventId = inputJson.getInt("eventId");
						String msgBody = inputJson.getString("msgBody");
						
						// validate EventId
						AlertEventMaster alertEventMaster = alertEventDao.getAlertEventById(eventId);
						if (alertEventMaster != null) {
							int response = alertEventDao.updateAlertEventMessage(eventId, msgBody);
							if (response > 0) {
								// changes regarding Audit trail
								UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
										DatabuckConstants.DBK_FEATURE_ALERT_NOTIFICATIONS, formatter.format(new Date()), (long) eventId,
										DatabuckConstants.ACTIVITY_TYPE_EDITED,alertEventMaster.getEventName());
								status = "success";
								message = "Event Subscription updated successfully";
								LOG.info(message);
							} else {
								message = "Failed to update Subscription";
								LOG.error(message);
							}
						} else {
							message = "Invalid eventId=[" + eventId + "]";
							LOG.error(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/updateEventSubscription - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/updateAlertSubscriptionCommunicationValues", method = RequestMethod.POST)
	public ResponseEntity<Object> updateAlertSubscriptionCommunicationValues(@RequestHeader HttpHeaders headers,
																			 @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/updateAlertSubscriptionCommunicationValues - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "Failed to update communication values";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						Long alertSubscriptionId = inputJson.getLong("alertSubscriptionId");
						String communicationValues = inputJson.getString("communicationValues");
						boolean updateStatus = alertEventDao.updateAlertSubscriptionCommunicationValues(alertSubscriptionId,
								communicationValues);
						String eventNameByAlertSubId = alertEventDao.getEventNameByAlertSubId(alertSubscriptionId);
						if (updateStatus) {
							// changes regarding Audit trail
							UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
									DatabuckConstants.DBK_FEATURE_ALERT_EVENT_SUBSCRIPTION, formatter.format(new Date()), alertSubscriptionId,
									DatabuckConstants.ACTIVITY_TYPE_EDITED,eventNameByAlertSubId);
							status = "success";
							message = "Communication Values updated successfully";
							LOG.info(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			}else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/updateAlertSubscriptionCommunicationValues - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAllAlertEventNames", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllAlertEventNames(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAllAlertEventNames - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		JSONArray eventNames = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					// get all event list
					eventNames = alertEventDao.getAllAlertEventNameList();
					// validate commModeList
					if (eventNames != null && eventNames.length() > 0) {
						status = "success";
						message = "eventNames  List Fetched successfully";
						LOG.info(message);
						result.put("eventNames", eventNames);
					} else{
						message = "Failed to fetch alert events";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getAllAlertEventNames - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
	
	@RequestMapping(value = "/dbconsole/getDatabuckAlertLog", method = RequestMethod.POST)
	public ResponseEntity<Object> getDatabuckAlertLog(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/getDatabuckAlertLog - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					// Check whether all required parameters are available.
					if (!requestBody.containsKey("EventIds") || !requestBody.containsKey("TaskName")
							|| !requestBody.containsKey("FromDate") || !requestBody.containsKey("ToDate")
							|| !requestBody.containsKey("ProjectIds") || !requestBody.containsKey("SearchText")) {
						throw new Exception("Required parameters not found.");
					}
					LOG.debug("Getting request parameters  " + requestBody);
					for (String sParmName : new String[] { "EventIds", "TaskName", "FromDate", "ToDate", "ProjectIds",
							"SearchText", "SearchText" }) {
						oPaginationParms.put(sParmName, requestBody.get(sParmName));
					}
					// fetch Alert log details
					jsonArray = alertEventDao.getDatabuckAlertLogs(oPaginationParms);

					if (jsonArray != null && jsonArray.length() > 0) {
						result.put("ViewAlertEventDataList", jsonArray);
						message = "ViewAlertEventDataList fetch successful";
						LOG.info(message);
						status = "success";
						json.put("result", result);
					} else{
						result.put("ViewAlertEventDataList", jsonArray);
						status = "success";
					}

					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_ALERT_INBOX, formatter.format(new Date()), 0l,
							DatabuckConstants.ACTIVITY_TYPE_VIEWED,"View Event Message");
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getDatabuckAlertLog - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/deleteEventSubscriptionById", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteEventSubscriptionById(@RequestHeader HttpHeaders headers,
															  @RequestBody String inputJsonStr) {

		LOG.info("dbconsole/deleteEventSubscriptionById - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						Integer alertSubId = inputJson.getInt("alertSubId");
						if (alertSubId != null && alertSubId > 0) {
							String eventNameByAlertSubId = alertEventDao.getEventNameByAlertSubId(Long.valueOf(alertSubId));
							if ( alertEventDao.deleteEventSubscriptionById(alertSubId)!= 0) {
								// changes regarding Audit trail
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
										DatabuckConstants.DBK_FEATURE_ALERT_EVENT_SUBSCRIPTION, formatter.format(new Date()), Long.valueOf(alertSubId),
										DatabuckConstants.ACTIVITY_TYPE_DELETED,eventNameByAlertSubId);
								message = "Event subscription deleted successfully";
								LOG.info(message);
								status = "success";
							} else {
								message = "Invalid subscription";
								LOG.error(message);
							}
						} else {
							message = "Invalid subscription";
							LOG.error(message);
						}
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Failed to delete the event subscription";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/deleteEventSubscriptionById - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/isCommunicationModeEnable", method = RequestMethod.GET)
	public ResponseEntity<Object> isCommunicationModeEnable(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/isCommunicationModeEnable - START");
		JSONObject json = new JSONObject();
		JSONArray result = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					JSONArray communicationModeEnable = alertManagementService.isCommunicationModeEnable();
					if (communicationModeEnable != null && communicationModeEnable.length() > 0) {
						result.put(communicationModeEnable);
						status="success";
						message="success";
						LOG.info(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("result", result);
		json.put("message", message);
		LOG.info("dbconsole/isCommunicationModeEnable - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

}
