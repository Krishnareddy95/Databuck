package com.databuck.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import com.databuck.util.DatabuckUtility;

@Service
public class KeyVaultAuthService {

	public String getSecretValueFromKeyVault(String secretKey) {

		String secretValue = null;

		try {
			System.out.println("\n**==> Reading the secret value from KeyVault for Key [" + secretKey + "]..");

			String cmd = "sh " + DatabuckUtility.getDatabuckHome() + "/scripts/runKeyVaultAuth.sh " + secretKey;

			System.out.println("\n====> Script cmd: " + cmd);

			Process process = Runtime.getRuntime().exec(cmd);

			System.out.println("\n====> Waiting for the script execution to complete ..");
			while (process.isAlive()) {
			}

			// Check if process exited normally or not
			if (process.exitValue() != 0) {
				System.out.println("\n====> Exception occurred while executing KeyVault Auth script for key["
						+ secretKey + "] !!");

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
				System.out.println("\n====> KeyVault Auth execution is successful for key[" + secretKey + "] !!");

				// Fetch the results
				System.out.println("\n====> Fetching the secret value for key[" + secretKey + "] !!");

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;

				while ((line = bufferedReader.readLine()) != null) {

					if (line.startsWith("KV_SUCCESS##")) {
						secretValue = line.split("##")[1];
						break;
					}
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while retrieving details from KeyVault for key["
					+ secretKey + "] !!");
			e.printStackTrace();
		}
		return secretValue;

	}

}
