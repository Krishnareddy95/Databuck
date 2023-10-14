package com.databuck.restcontroller;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.databuck.bean.ListApplications;
import com.databuck.controller.JSONController;
import com.databuck.util.ReturnHolder;
import org.apache.log4j.Logger;

@org.springframework.web.bind.annotation.RestController
public class JsonRestController {
	@Autowired
	JSONController Jsoncontroller;
	
	private static final Logger LOG = Logger.getLogger(JsonRestController.class);

	private static final String key = "!%$FQ6E-eH8Q.z!";

	@RequestMapping(value = "/application", method = RequestMethod.GET)
	public ReturnHolder getEmployeeInJSON(HttpServletRequest request) {
		LOG.info("application - START");
		ListApplications la = new ListApplications();
		String xAuth = request.getHeader("Authorization");
		LOG.debug("Authorization " + xAuth);
		ReturnHolder holder = new ReturnHolder();
		if (xAuth.equals(key)) {
			la.setApplyRules("Y");
			la.setApplyDerivedColumns("Y");
			// holder.setPayLoad(la);
		} else {
			holder.setStatus(500);
			holder.setMessage("Unauthorized User");
			LOG.error("Unauthorized User");
		}
		LOG.info("application - END");
		return holder;
	}

	@RequestMapping(value = "restapi/v1/dqi", produces = "application/json")
	public String getTableDataAsDynamic(HttpServletRequest request, @RequestParam("idApp") Long idApp) {
		LOG.info("restapi/v1/dqi - START");
		try {
			LOG.debug("idApp=" + idApp);
			String date = request.getParameter("date");
			LOG.debug("date " + date);
			int run = 0;
			if (request.getParameter("run") != null) {
				run = Integer.valueOf(request.getParameter("run"));
				LOG.debug("run " + run);
			}
			LOG.info("date=" + date);
			LOG.info("run=" + run);
			JSONObject JSONDataDQI = Jsoncontroller.prepareJSONData(idApp, date, run, false);
			//LOG.info("restapi/v1/dqi - END");
			return JSONDataDQI.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("restapi/v1/dqi - END");
		return null;
	}

	@RequestMapping(value = "restapi/v1Secure/dqi", produces = "application/json")
	public String getTableDataAsDynamicv1Secure(HttpServletRequest request, @RequestParam("idApp") Long idApp) {
		LOG.info("restapi/v1Secure/dqi - START");
		JSONObject JSONDataDQI = new JSONObject();
		try {
			
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String date = request.getParameter("date");
			LOG.debug("date " + date);
			int run = 0;
			if (request.getParameter("run") != null) {
				run = Integer.valueOf(request.getParameter("run"));
				LOG.debug("run " + run);
			}
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);
			if (authorization != null && authorization.startsWith("Basic")) {
				// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				String credentials = new String(Base64.getDecoder().decode(base64Credentials),
						Charset.forName("UTF-8"));
				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				for (int i = 0; i < values.length; i++) {
					LOG.info("values=" + values[i]);
				}

				if (values[0].equals(secureAPIMap.get("accessTokenId"))
						&& values[1].equals(secureAPIMap.get("secretAccessToken"))) {
					/*
					 * System.out.println("idApp=" + idApp);
					 * System.out.println("date=" + date);
					 * System.out.println("run=" + run);
					 */
					JSONDataDQI = Jsoncontroller.prepareJSONData(idApp, date, run, false);
					//LOG.info("restapi/v1Secure/dqi - END");
					return JSONDataDQI.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		JSONDataDQI.put("message", "Invalid Token");
		LOG.error("Invalid Token");
		LOG.info("restapi/v1Secure/dqi - END");
		return JSONDataDQI.toString();
	}

	@RequestMapping(value = "restapi/v2Secure/dqi", produces = "application/json")
	public String getTableDataAsDynamicv2(HttpServletRequest request, @RequestParam("idApp") Long idApp,
			@RequestParam("date") String date, @RequestParam("run") int run) {
		LOG.info("restapi/v2Secure/dqi - START");
		JSONObject JSONDataDQI = new JSONObject();
		try {
			LOG.debug("idApp=" + idApp);
			LOG.debug("date=" + date);
			LOG.debug("run=" + run);
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);
			if (authorization != null && authorization.startsWith("Basic")) {
				// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				String credentials = new String(Base64.getDecoder().decode(base64Credentials),
						Charset.forName("UTF-8"));
				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				for (int i = 0; i < values.length; i++) {
					LOG.info("values=" + values[i]);
				}

				if (values[0].equals(secureAPIMap.get("accessTokenId"))
						&& values[1].equals(secureAPIMap.get("secretAccessToken"))) {
					/*
					 * System.out.println("idApp=" + idApp);
					 * System.out.println("date=" + date);
					 * System.out.println("run=" + run);
					 */
					boolean checkDateAndRun = Jsoncontroller.checkDateAndRun(idApp, date, run);
					if (checkDateAndRun) {
						JSONDataDQI = Jsoncontroller.prepareJSONData(idApp, date, run, false);
					} else {
						JSONDataDQI.put("message", "Invalid Date And Run Combination");
						LOG.error("Invalid Date And Run Combination");
					}
					//LOG.info("restapi/v2Secure/dqi - END");
					return JSONDataDQI.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		JSONDataDQI.put("message", "Invalid Token");
		LOG.error("Invalid Token");
		LOG.info("restapi/v2Secure/dqi - END");
		return JSONDataDQI.toString();
	}

}
