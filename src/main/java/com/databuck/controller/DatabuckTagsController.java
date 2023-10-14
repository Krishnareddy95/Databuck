package com.databuck.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.dao.DatabuckTagsDao;
import com.databuck.service.DatabuckTagsService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;

@Controller
public class DatabuckTagsController {

	@Autowired
	private DatabuckTagsService databuckTagsService;

	@Autowired
	private DatabuckTagsDao databuckTagsDao;

	@Autowired
	private RBACController rbacController;


	@RequestMapping(value = "/databuckTags")
	public ModelAndView tagViewList(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("User Settings", "R", session);
		if (rbac) {
			model.setViewName("databuckTagsView");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "View Tags");
			return model;
		} else {
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/getDatabuckTags", method = RequestMethod.POST, produces = "application/json")
	public void getDatabuckTags(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		System.out.println("\n======> getDatabuckTags - START <======");
		try {

			JSONArray databuckTags = databuckTagsDao.getAllDatabuckTags();
			oJsonResponse.put("databuckTag", databuckTags);
			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/updateDatabuckTagInfo", method = RequestMethod.POST)
	public void updateDatabuckTagInfo(@RequestParam int tagId, @RequestParam String tagName,
			@RequestParam String description, HttpServletRequest req, HttpServletResponse response,
			HttpSession session) {
		System.out.println("\n====> updateDatabuckTagInfo - START ");

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				int result = databuckTagsDao.updateDatabuckTagInfo(tagId, tagName, description);

				if (result > 0) {
					status = "success";
					message = "Databuck Tag Updated successfully";
				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to update Databuck Tag ";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value = "/addDatabuckTags", method = RequestMethod.POST)
	public void addDatabuckTags(@RequestParam String tagName, String description, HttpServletRequest req,
			HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> addDatabuckTags - START ");

		JSONObject jsonResult = new JSONObject();

		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {
				JSONObject jsonObject = databuckTagsService.addDatabuckTags(tagName, description);
				message = jsonObject.getString("message");
				status = jsonObject.getString("status");
			}else
				message="Invalid User";
		} catch (Exception e) {
			message = "Failed to add databuck tag";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/loadTagRecordList", method = RequestMethod.GET, produces = "application/json")
	public void loadTagRecordList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadDomainList 01", "Begin controller processing for tag list");

			JSONArray databuckTag = databuckTagsDao.getAllDatabuckTags();
			oJsonResponse.put("databuckTag", databuckTag);

			DateUtility.DebugLog("loadDomainList 02", "Got data sending to client");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/linkDatabuckTagToRule", method = RequestMethod.POST)
	public void linkDatabuckTagToRule(@RequestParam int ruleId, @RequestParam int tagId, @RequestParam int idApp,
			HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> linkDatabuckTagToRule - START ");

		JSONObject jsonResult = new JSONObject();

		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {
				JSONObject jsonObject = databuckTagsService.linkDatabuckTagToRule(ruleId, tagId, idApp);
				message = jsonObject.getString("message");
				status = jsonObject.getString("status");
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to add Tag to Rule";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
