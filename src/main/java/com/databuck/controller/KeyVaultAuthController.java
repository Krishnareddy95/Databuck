package com.databuck.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.security.KeyVaultAuthService;

@RestController
public class KeyVaultAuthController {

	@Autowired
	private KeyVaultAuthService keyVaultAuthService;

	@RequestMapping(value = "restapi/getSecretVauleFromKeyVault", method = RequestMethod.GET, produces = "application/json")
	public String getSecretVauleFromKeyVault(HttpServletResponse response, HttpServletRequest request,
			@RequestParam String key) {

		JSONObject json = new JSONObject();
		String status = "failed";
		String secretValue = null;

		try {
			// Fetch the details for the key
			secretValue = keyVaultAuthService.getSecretValueFromKeyVault(key);

			if (secretValue != null && !secretValue.trim().isEmpty())
				status = "passed";
			else
				System.out.println("\n====> Failed to get details from KeyVault for Key:[" + key + "]!!");

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.put("status", status);
		json.put("result", secretValue);
		return json.toString();
	}

}
