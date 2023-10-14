package com.databuck.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.security.LogonManager;

@RestController
public class LogonManagerController {

	@Autowired
	private LogonManager logonManager;

	@RequestMapping(value = "restapi/getDataByKMSKey", method = RequestMethod.GET, produces = "application/json")
	public String getDataByKMSKey(HttpServletResponse response, HttpServletRequest request, @RequestParam String key) {

		JSONObject json = new JSONObject();
		String status = "failed";
		String result = null;
		try {

			// Fetch the details for the key
			Map<String, String> dbDetailsMap = logonManager.getCredentialsFromLogonCmd(key);

			if (dbDetailsMap != null) {
				String host = dbDetailsMap.get("hostname");
				if (host == null || host.trim().isEmpty() || host.equalsIgnoreCase("None"))
					host = "";

				String port = dbDetailsMap.get("port");
				if (port == null || port.trim().isEmpty() || port.equalsIgnoreCase("None"))
					port = "";

				String username = dbDetailsMap.get("username");
				if (username == null || username.trim().isEmpty() || username.equalsIgnoreCase("None"))
					username = "";

				String password = dbDetailsMap.get("password");
				if (password == null || password.trim().isEmpty() || password.equalsIgnoreCase("None"))
					password = "";

				String data = host + "," + port + "," + username + "," + password;

				// Encrypt the result
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
				result = encryptor.encrypt(data);

				status = "passed";
			} else {
				System.out.println("\n====> Failed to get details from logon manager for Key:[" + key + "]!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.put("status", status);
		json.put("result", result);
		return json.toString();
	}

}
