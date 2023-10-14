package com.databuck.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.databuck.util.DatabuckUtility;

@Service
public class LogonManager {

	public Map<String, String> getCredentialsFromLogonCmd(String kmsKey) {
		Map<String, String> dbDetailsMap = null;
		try {
			System.out.println("\n**==> Reading the connection details from logon manager for Key [" + kmsKey + "]..");

			String cmd = "sh " + DatabuckUtility.getDatabuckHome() + "/scripts/runLogonCmd.sh " + kmsKey;

			System.out.println("\n====> logoncmd: " + cmd);

			Process process = Runtime.getRuntime().exec(cmd);

			System.out.println("\n====> Waiting for the script execution to complete ..");
			while (process.isAlive()) {
			}

			// Check if process exited normally or not
			if (process.exitValue() != 0) {
				System.out.println(
						"\n====> Exception occurred while executing logon manager script for key[" + kmsKey + "] !!");

				// Read error stream
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line;

				System.out.println("\n====> Printing error log ..");
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}

				if (bufferedReader != null)
					bufferedReader.close();

			} else {
				System.out.println("\n====> Logon manager script execution is successful for key[" + kmsKey + "] !!");

				// Fetch the results
				dbDetailsMap = fetchResults(process);
			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while retrieving details from logon manager for key["
					+ kmsKey + "] !!");
			e.printStackTrace();
		}
		return dbDetailsMap;

	}

	private Map<String, String> fetchResults(Process process) throws IOException {
		System.out.println("\n====> Fetching results ..");

		Map<String, String> dbDetails = new HashMap<String, String>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			int index = 0;
			String username = "";
			String password = "";
			String hostname = "";
			String port = "";

			while ((line = bufferedReader.readLine()) != null) {
				if (index == 0) {
					username = (line.equalsIgnoreCase("None")) ? "" : line;

				} else if (index == 1) {
					password = (line.equalsIgnoreCase("None")) ? "" : line;

				} else if (index == 2) {
					if (!line.equalsIgnoreCase("None")) {
						String[] fields = line.split(":");
						hostname = fields[0];
						if (fields.length > 1)
							port = fields[1];
					}
				}

				++index;
			}

			dbDetails.put("username", username);
			dbDetails.put("password", password);
			dbDetails.put("hostname", hostname);
			dbDetails.put("port", port);

			if (bufferedReader != null) {
				bufferedReader.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbDetails;
	}

	public boolean validateLogonManagerResponseForDB(Map<String, String> detailsMap) {
		System.out.println("\n**==> Validating LogonManager Response For db connection ..");
		boolean status = false;

		if (detailsMap != null) {
			String username = detailsMap.get("username");
			String password = detailsMap.get("password");
			String hostname = detailsMap.get("hostname");
			String port = detailsMap.get("port");

			int missingValuesCount = 0;

			if (username == null || username.trim().isEmpty() || username.trim().equalsIgnoreCase("None")) {
				System.out.println("====> Username is missing !!");
				++missingValuesCount;

			}

			if (password == null || password.trim().isEmpty() || password.trim().equalsIgnoreCase("None")) {
				System.out.println("====> password is missing !!");
				++missingValuesCount;

			}

			if (hostname == null || hostname.trim().isEmpty() || hostname.trim().equalsIgnoreCase("None")) {
				System.out.println("====> hostname is missing !!");
				++missingValuesCount;
			}
			if (port == null || port.trim().isEmpty() || port.trim().equalsIgnoreCase("None")) {
				System.out.println("====> port is missing !!");
				++missingValuesCount;
			}

			if (missingValuesCount == 0) {
				System.out.println("**==> LogonManager Response is valid!!");
				status = true;
			} else {
				System.out.println("**==> LogonManager Response is invalid!!");
			}
		}

		return status;
	}

	public boolean validateLogonManagerResponseForAPI(Map<String, String> detailsMap) {
		System.out.println("\n**==> Validating LogonManager Response For external API ..");
		boolean status = false;

		if (detailsMap != null) {
			String username = detailsMap.get("username");
			String password = detailsMap.get("password");
			int missingValuesCount = 0;

			if (username == null || username.trim().isEmpty() || username.trim().equalsIgnoreCase("None")) {
				System.out.println("====> Username is missing !!");
				++missingValuesCount;

			}

			if (password == null || password.trim().isEmpty() || password.trim().equalsIgnoreCase("None")) {
				System.out.println("====> password is missing !!");
				++missingValuesCount;

			}

			if (missingValuesCount == 0) {
				System.out.println("\n**==> LogonManager Response is valid!!");
				status = true;
			} else {
				System.out.println("\n**==> LogonManager Response is invalid!!");
			}
		}

		return status;
	}
}
