package com.databuck.controller;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.dao.IUserDAO;
import com.databuck.service.LoginService;
import com.databuck.service.NotificationService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;


@Controller
public class NotificationsController {
	@Autowired
	private RBACController rbacController;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	public LoginService loginService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	IUserDAO userDAO;

	@RequestMapping(value = "/notificationView")
	public ModelAndView notificationView(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("notificationViewManagement");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "notificationView");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/loadNotificationDataTable", method = RequestMethod.POST, produces = "application/json")
	public void loadNotificationDataTable(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			oJsonResponse = notificationService.loadNotificationDataList();

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	
	@RequestMapping(value = "/mainNotificationHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainNotificationHandler(HttpSession oSession,  @RequestParam String NotificationData, HttpServletResponse oResponse) throws IOException {
		JSONObject oNotificationData = new JSONObject(NotificationData);
		JSONObject oJsonResponse = new JSONObject();
		String sContext, sNewVersionRowId = "";
		String sMsg = "";
		boolean lStatus = true;

		try {
			DateUtility.DebugLog("notificationCopy 01",	String.format("Begin controller Context = %1$s", oNotificationData.getString("Context")));
			sContext = oNotificationData.getString("Context");

			switch (sContext) {

			case "saveFullNotificationData":
				JSONObject statusJson= notificationService.saveFullNotificationData(oNotificationData.getJSONObject("Data"));
				sMsg = (statusJson != null)? statusJson.getString("message"):"Error while saving FullNotificationData";
				break;

				case "makeCopyOfSelectedVersion":
					sNewVersionRowId = String.format("%1$s", notificationService.makeCopyOfSelectedVersion(oNotificationData.getJSONObject("Data")));
					sMsg = "Successfully created new copy from selected notification, kindly edit, verify it and then make it active";
					oJsonResponse.put("NewVersionRowId", sNewVersionRowId);
					break;

				case "saveSelectedVersionAsActive":
					lStatus = notificationService.saveSelectedVersionAsActive(oNotificationData.getJSONObject("Data"));
					sMsg = (lStatus) ? "Successfully made selected version as active" : "Error while making selected version as active";
					break;

				case "deleteSelectedVersion":
					notificationService.deleteSelectedVersion(oNotificationData.getJSONObject("Data"));
					sMsg =  "Successfully deleted version";
					break;

				case "verfiySelectedVersion":
					JSONObject verfiyVersionJson = notificationService.verfiySelectedVersion(oNotificationData.getJSONObject("Data"));
					sMsg = (verfiyVersionJson!=null)? "Successfully send notification": "Failed to send notification";
					break;

				default:
			}

			oJsonResponse.put("Status", lStatus);
			oJsonResponse.put("Msg", sMsg);

			DateUtility.DebugLog("notificationCopy 02",	"End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	@RequestMapping(value = "/externalApiAlertView")
	public ModelAndView externalApiAlertView(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("externalApiAlertViewManagement");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "externalApiAlertView");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}
	
	@RequestMapping(value = "/loadExternalApiAlertDataTable", method = RequestMethod.POST, produces = "application/json")
	public void loadExternalApiAlertDataTable(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadExternalApiAlertDataTable 01", "Begin controller");
			oJsonResponse = notificationService.loadExternalApiAlertDataList();
			DateUtility.DebugLog("loadExternalApiAlertDataTable 02", "End controller");
			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/mainExternalApiAlertHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainExternalApiAlertHandler(HttpSession oSession,  @RequestParam String ExternalApiAlertData, HttpServletResponse oResponse) throws IOException {

		JSONObject oJsonResponse = new JSONObject();
		String sContext;
		String sMsg = "";
		boolean lStatus = false;

		try {
			JSONObject oExternalApiAlertData = new JSONObject(ExternalApiAlertData);
			DateUtility.DebugLog("ExternalApiAlertDataCopy 01",	String.format("Begin controller Context = %1$s, Data = %2$s", oExternalApiAlertData.getString("Context"), oExternalApiAlertData.getJSONObject("Data")));
			sContext = oExternalApiAlertData.getString("Context");

			switch (sContext) {

				case "saveFullExternalApiAlertData":

					JSONObject dataObj= oExternalApiAlertData.getJSONObject("Data");
					String alertMsg= dataObj.getString("AlertMsg");

					if(alertMsg!=null && !alertMsg.trim().isEmpty()){

						String alertMsgCode = dataObj.getString("AlertMsgCode");

						if(alertMsgCode!=null && !alertMsgCode.trim().isEmpty()){
							JSONObject statusJson= notificationService.saveFullExternalApiAlertData(dataObj);
							sMsg = statusJson.getString("message");
							String updateStatus = statusJson.getString("status");
							if(updateStatus != null && updateStatus.trim().equalsIgnoreCase("success"))
								lStatus = true;
						}else
							sMsg="Alert Message Code is missing";
					}else
						sMsg="Alert Message is missing";

					break;

				default:
			}

			DateUtility.DebugLog("ExternalApiAlertDataCopy 03",	"End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			sMsg="Error occurred while updating External Alert API";
		}
		oJsonResponse.put("Status", lStatus);
		oJsonResponse.put("Msg", sMsg);
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}
}
