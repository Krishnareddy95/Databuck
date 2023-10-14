package com.databuck.restcontroller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.NotificationService;

@CrossOrigin(origins = "*")
@RestController
public class NotificationsRestController {

	@Autowired
	ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private NotificationService notificationService;
	
	private static final Logger LOG = Logger.getLogger(NotificationsRestController.class);

	@RequestMapping(value = "/dbconsole/notify/getExternalApiAlertList", method = RequestMethod.GET)
	public ResponseEntity<Object> getExternalApiAlertList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/notify/getExternalApiAlertList - START");

		JSONObject json = new JSONObject();
		JSONObject externalApiAlertDataList = new JSONObject();
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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {
					externalApiAlertDataList = notificationService.loadExternalApiAlertDataList();
					JSONArray actualExternalApiAlertDataList = externalApiAlertDataList.getJSONArray("DataSet");
					JSONArray modifiedApiAlertList = new JSONArray();

					if (externalApiAlertDataList != null) {

						if (actualExternalApiAlertDataList != null && actualExternalApiAlertDataList.length() > 0) {
							for (int i = 0; i < actualExternalApiAlertDataList.length(); i++) {
								JSONObject modifyAlertList = actualExternalApiAlertDataList.getJSONObject(i);
								modifyAlertList.remove("SubTopicRowId-edit");
								modifiedApiAlertList.put(modifyAlertList);
							}
							// Update the externalApiAlert list in the output
							externalApiAlertDataList.remove("DataSet");
							externalApiAlertDataList.put("DataSet", modifiedApiAlertList);
							status = "success";
							message = "success";
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
			}
			LOG.info("Message "+message);
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message);
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", message);
		json.put("result", externalApiAlertDataList);
		LOG.info("dbconsole/notify/getExternalApiAlertList - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "dbconsole/notify/saveExternalApiAlertData", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveExternalApiAlertData(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {

		LOG.info("dbconsole/notify/saveExternalApiAlertData - START");
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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);

						JSONObject oExternalApiAlertData = new JSONObject(inputJsonStr);
						String alertMsg = oExternalApiAlertData.getString("AlertMsg");
						if (alertMsg != null && !alertMsg.trim().isEmpty()) {

							String alertMsgCode = oExternalApiAlertData.getString("AlertMsgCode");
							if (alertMsgCode != null && !alertMsgCode.trim().isEmpty()) {
								JSONObject jsonObj = notificationService
										.saveFullExternalApiAlertData(oExternalApiAlertData);

								if (jsonObj != null && jsonObj.length() > 0) {
									message = jsonObj.getString("message");
									status = jsonObj.getString("status");
								}
							} else {
								message = "Alert Message Code is missing";
								LOG.error(message);
							}
						} else {
							message = "Alert Message is missing";
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
				message = "Token is missing in headers";
				LOG.error(message);
			}
			LOG.info("Message "+message);

		} catch (Exception e) {
			message = "Error occurred while updating External Alert API";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/notify/saveExternalApiAlertData - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "dbconsole/notify/copyNotificationTopic", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> copyNotificationTopic(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {

		LOG.info("dbconsole/notify/copyNotificationTopic - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		int sNewVersionRowId = -1;

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr); 
						JSONObject cpyNotification = new JSONObject(inputJsonStr);
						sNewVersionRowId = notificationService.makeCopyOfSelectedVersion(cpyNotification);

						if (sNewVersionRowId > 0) {
							json.put("NewVersionRowId", sNewVersionRowId);
							message = "successfully created new copy from selected notification, kindly edit, verify it and then make it active";
							status = "success";
						} else {
							message = "Failed to create new copy of selected notification";
							LOG.error(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
			}
			LOG.info(message);

		} catch (Exception e) {
			message = "Error occurred while copying selected notification";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/notify/copyNotificationTopic - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "dbconsole/notify/saveNotificationTopicChanges", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNotificationTopicChanges(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {

		LOG.info("dbconsole/notify/saveNotificationTopicChanges - START");
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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					// Check if request is not empty
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {

						JSONObject saveNotification = new JSONObject(inputJsonStr);
						JSONObject statusJson = notificationService.saveFullNotificationData(saveNotification);

						if (statusJson != null && statusJson.length() > 0) {
							message = statusJson.getString("message");
							status = statusJson.getString("status");
						} else {
							message = "Error while saving FullNotificationData";
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
				message = "Token is missing in headers";
				LOG.error(message);
			}
			LOG.info(message);

		} catch (Exception e) {
			message = "Error occurred while saving notification";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/notify/saveNotificationTopicChanges - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/notify/getNotificationTopicList", method = RequestMethod.GET)
	public ResponseEntity<Object> getNotificationTopicList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/notify/getNotificationTopicList - START");
		JSONObject json = new JSONObject();
		JSONObject notificationData = new JSONObject();

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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					notificationData = notificationService.loadNotificationDataList();
					JSONArray actualNotificationList = notificationData.getJSONArray("DataSet");
					JSONArray modifiedNotificationList = new JSONArray();

					if (actualNotificationList != null && actualNotificationList.length() > 0) {
						for (int i = 0; i < actualNotificationList.length(); i++) {
							JSONObject notificationTopic = actualNotificationList.getJSONObject(i);
							notificationTopic.remove("VersionRowId-delete");
							notificationTopic.remove("VersionRowId-edit");
							notificationTopic.remove("VersionRowId-check");
							notificationTopic.remove("VersionRowId-copy");
							notificationTopic.remove("VerifyIcon");
							modifiedNotificationList.put(notificationTopic);
						}
						// Update the notification list in the output
						notificationData.remove("DataSet");
						notificationData.put("DataSet", modifiedNotificationList);

						message = "success";
						status = "success";

					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token is missing in headers";
				LOG.error(message);
			}
			LOG.info(message);
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message);
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", message);
		json.put("result", notificationData);
		LOG.info("dbconsole/notify/getNotificationTopicList - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/notify/verifyNotificationTopic", method = RequestMethod.POST)
	public ResponseEntity<Object> verifyNotificationTopic(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/notify/verifyNotificationTopic - START");

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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					// Validate the input
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);

						// Validate topic title
						String topicTitle = inputJson.getString("TopicTitle");
						if (topicTitle != null && !topicTitle.trim().isEmpty()) {

							JSONObject verify = notificationService.verfiySelectedVersion(inputJson);

							message = verify.getString("message");
							status = verify.getString("status");
						} else {
							message = "Invalid Topic title";
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
				message = "Token is missing in headers";
				LOG.error(message);
			}
			LOG.info(message);
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/notify/verifyNotificationTopic - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

}
