package com.databuck.integration;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.databuck.dao.ITaskDAO;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.databuck.bean.ListDataSchema;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;

@Service
public class AlationIntegrationService {

	@Autowired
	private Properties integrationProperties;

	@Autowired
	private IListDataSourceDAO listDataSourceDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	private AlationAPIService alationAPIService;

	@Autowired
	private IResultsDAO resultsDAO;

	@Autowired
	private ITaskDAO taskDAO;

	private String alationBaseUrl = "";
	private String accessToken = "";
	private String refreshToken = "";

	public JSONObject isAlationEnabledForDatabaseSchema(String connectionType, String schemaName) {

		JSONObject alation_integration_json = new JSONObject();
		String status = "failed";
		String message = "";
		try {

			int ds_id = 0;

			if (schemaName != null && !schemaName.trim().isEmpty()) {
				
				// Check if Alation Integration is enabled
				String alationIntegrationEnabled = integrationProperties.getProperty("alation.integration.enabled");
				System.out.println("\n====> alation.integration.enabled: " + alationIntegrationEnabled);

				if (alationIntegrationEnabled != null && alationIntegrationEnabled.trim().equalsIgnoreCase("Y")) {
					// Get BaseUrl
					alationBaseUrl = integrationProperties.getProperty("alation.integration.baseurl");
					System.out.println("\n====> Alation Base url: " + alationBaseUrl);

					// Get accessToken
					accessToken = integrationProperties.getProperty("alation.integration.accesstoken");
					System.out.println("\n====> Alation Access token: " + accessToken);

					// Get refreshToken
					refreshToken = integrationProperties.getProperty("alation.integration.refreshtoken");
//					System.out.println("\n====> Alation Refresh token: " + refreshToken);

					if (refreshToken == null || refreshToken.trim().isEmpty()) {
						// isReqParamsValid = false;
						System.out.println("\n====> Alation refresh token is missing !!");
						message = "";
					}

					boolean isReqParamsValid = true;
					if (alationBaseUrl == null || alationBaseUrl.trim().isEmpty()) {
						isReqParamsValid = false;
						System.out.println("\n====> Alation base url is missing !!");
						message = "Alation base url is missing";
					}

					accessToken = validateAndGenerateNewAccessToken(accessToken,refreshToken,alationBaseUrl);

					if (accessToken == null || accessToken.trim().isEmpty()) {
						isReqParamsValid = false;
						System.out.println("\n====> Alation Access Token validation and creation failure !!");
						message = "Alation Access Token validation and creation failure";
					}

					if (isReqParamsValid) {
						alationBaseUrl = alationBaseUrl.trim();
						accessToken = accessToken.trim();
						schemaName = schemaName.toLowerCase();

						// For Snowflake connection, we need to take database and schema
						if (connectionType.equalsIgnoreCase("snowflake")) {
							String[] dbtokens = schemaName.split(",");
							schemaName = dbtokens[1] + "." + dbtokens[2];
						}
						
						// Get DS_ID of the schema
						System.out.println("\n====> Get ds_id of schema from Alation ..");
						ds_id = getSchemaDS_IDFromAlation(schemaName);
						System.out.println("\n====>ds_id: " + ds_id);

						if (ds_id > 0) {
							status = "success";
						} else {
							System.out.println(
									"\n====> Failed to get ds_id for schema[" + schemaName + "] from Alation!!");
							message = "Alation Integration is not enabled for schema[" + schemaName + "]";
						}

					}
				} else {
					System.out.println("\n====> Alation Integration property is not enabled !!");
					message = "Property alation.integration.enabled is not enabled !!";
				}

			} else {
				message = "Database schema name is missing, cannot proceed further";
				System.out.println("\n====> Database schema name is missing, cannot proceed further !!");
			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while posting DQ response to Alation !!");
			e.printStackTrace();
			message = "Could not get Alation Integration properties";
		}
		alation_integration_json.put("status", status);
		alation_integration_json.put("message", message);
		return alation_integration_json;
	}

	public JSONObject publishSchemaSummaryToAlation(long idDataSchema) {

		System.out.println("\n******** PublishSchemaSummaryToAlation - START ********");

		JSONObject responseJson = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			// Check if Alation Integration is enabled
			String alationIntegrationEnabled = integrationProperties.getProperty("alation.integration.enabled");
			System.out.println("\n====> alation.integration.enabled: " + alationIntegrationEnabled);

			if (alationIntegrationEnabled != null && alationIntegrationEnabled.trim().equalsIgnoreCase("Y")) {

				List<ListDataSchema> dataConnectionList = listDataSourceDAO
						.getListDataSchemaForIdDataSchema(idDataSchema);

				if (dataConnectionList != null && dataConnectionList.size() > 0 && dataConnectionList.get(0) != null) {
					ListDataSchema listDataSchema = dataConnectionList.get(0);

					// Check if Alation integration is enabled in the connection
					if (listDataSchema.getAlation_integration_enabled() != null
							&& listDataSchema.getAlation_integration_enabled().trim().equalsIgnoreCase("Y")) {

						String schemaName = listDataSchema.getDatabaseSchema();
						System.out.println("\n====> schemaName: " + schemaName);

						if (schemaName != null && !schemaName.trim().isEmpty()) {

							// Get the list of validation Id's associated with idDataSchema
							List<Long> validationIdList = validationCheckDAO.getValidationIdListForSchema(idDataSchema);

							if (validationIdList != null && !validationIdList.isEmpty()) {

								// Get BaseUrl
								alationBaseUrl = integrationProperties.getProperty("alation.integration.baseurl");
								System.out.println("\n====> Alation Base url: " + alationBaseUrl);

								// Get accessToken
								accessToken = integrationProperties.getProperty("alation.integration.accesstoken");

								// Get refreshToken
								refreshToken = integrationProperties.getProperty("alation.integration.refreshtoken");

								boolean isReqParamsValid = true;
								if (alationBaseUrl == null || alationBaseUrl.trim().isEmpty()) {
									isReqParamsValid = false;
									System.out.println("\n====> Alation base url is missing !!");
								}

								if (refreshToken == null || refreshToken.trim().isEmpty()) {
									// isReqParamsValid = false;
									System.out.println("\n====> Alation refresh token is missing !!");
								}

								accessToken = validateAndGenerateNewAccessToken(accessToken,refreshToken,alationBaseUrl);

								if (accessToken == null || accessToken.trim().isEmpty()) {
									isReqParamsValid = false;
									System.out.println("\n====> Alation Access Token validation and creation failure !!");
									message = "Alation Access Token validation and creation failure";
								}

								if (isReqParamsValid) {
									alationBaseUrl = alationBaseUrl.trim();
									accessToken = accessToken.trim();
									schemaName = schemaName.toLowerCase();

									// For Snowflake connection, we need to take database and schema 
									String connectionType =  listDataSchema.getSchemaType();
									if (connectionType.equalsIgnoreCase("snowflake")) {
										String[] dbtokens = schemaName.split(",");
										schemaName = dbtokens[1] + "." + dbtokens[2];
									}
									
									// Get DS_ID of the schema
									System.out.println("\n====> Get ds_id of schema from Alation ..");
									int ds_id = getSchemaDS_IDFromAlation(schemaName);
									System.out.println("\n====> ds_id: " + ds_id);

									if (ds_id > 0) {

										// Get CustomFields and corresponding IDs
										System.out.println("\n====> Get CustomFields and corresponding IDs ..");

										Map<String, Integer> customFieldsMap = readCustomFieldNames();
										System.out.println("\n====> customFieldsMap: " + customFieldsMap);

										// Publish Table level details
										System.out.println("\n====> Publish Schema level details ..");
										boolean dq_post_status = postSchemaLevelDQDetailsToAlation(listDataSchema,
												schemaName, validationIdList, ds_id, customFieldsMap);

										if (dq_post_status)
											status = "success";

									} else {
										System.out.println("\n====> Failed to get ds_id for schema[" + schemaName
												+ "] from Alation!!");
										message = "Failed to publish schema summary to Alation";
									}
								} else
									message = "Mandatory properties are missing, failed to publish summary to Alation";
							} else
								message = "No validations found for this connection, failed to publish summary to Alation";

						} else
							message = "Database Schema Name is missing, failed to publish summary to Alation";

					} else
						message = "Alation Integration is not enabled for connection";

				} else
					message = "Failed to get connection details, cannot publish schema summary to Alation";

			} else
				message = "Alation Integration is not enabled, failed to publish summary to Alation";

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while posting Schema summary to Alation !!");
			message = "Failed to publish schema summary to Alation";
			e.printStackTrace();
		}

		System.out.println("\n====> Status: " + status);
		System.out.println("\n====> Message: " + message);
		responseJson.put("status", status);
		responseJson.put("message", message);

		System.out.println("\n******** PublishSchemaSummaryToAlation - END   ********\n");

		return responseJson;
	}

	private boolean postSchemaLevelDQDetailsToAlation(ListDataSchema listDataSchema, String databaseSchemaName,
			List<Long> validationIdList, int ds_id, Map<String, Integer> customFieldsMap) {
		boolean res_status = false;
		try {
			String url = "https://" + alationBaseUrl + "/integration/v2/schema/?ds_id=" + ds_id;

			JSONArray resArray = new JSONArray();
			DecimalFormat df = new DecimalFormat("#0.00");

			// Get Connection summary response
			String valIdsStr = "";
			for (Long idApp : validationIdList) {
				if (idApp != null && idApp > 0l)
					valIdsStr = valIdsStr + idApp + ",";
			}
			valIdsStr = valIdsStr.substring(0, valIdsStr.length() - 1);

			long connectionId = listDataSchema.getIdDataSchema();
			String connectionName = listDataSchema.getSchemaName();
			long totalValidationCount = validationIdList.size();

			// Get the no of validations executed in the total list
			Integer executedValidationCount = resultsDAO.getTotalExecutedValidationCountForSchema(valIdsStr);

			// get DQI from DB
			double totalAvgDQI = resultsDAO.getAggregateDQIForSchema(valIdsStr);

			String fieldValue = "<div>Connection Id: <strong>" + connectionId + "</strong></div><br>";
			fieldValue = fieldValue + "<div>Connection Name: <strong>" + connectionName + "</strong></div><br>";
			fieldValue = fieldValue + "<div>Total Validation Count: <strong>" + totalValidationCount
					+ "</strong></div><br>";
			fieldValue = fieldValue + "<div>Executed Validation Count: <strong>" + executedValidationCount
					+ "</strong></div><br>";
			fieldValue = fieldValue + "<div>Total Avearge DTS: <strong>" + df.format(totalAvgDQI)
					+ "</strong></div><br><br>";

			fieldValue = fieldValue + "<table style='font-size: 10px; border-style: inset;'><thead<tr>";
			fieldValue = fieldValue + "<th style=\"width: 50%;\">DQ Check Name</th>";
			fieldValue = fieldValue + "<th>Average DTS</th>";
			fieldValue = fieldValue + "</tr></thead><tbody>";

			Map<String, String> dqCheckNames = new HashMap<String, String>();
			dqCheckNames.put("DQ_Record Count Fingerprint", "Record Count Reasonability");
			dqCheckNames.put("DQ_LengthCheck", "Length Check (Conformity)");
			dqCheckNames.put("DQ_Completeness", "Data Completeness");
			dqCheckNames.put("DQ_Data Drift", "String Value Drift (Drift and Orphan)");
			dqCheckNames.put("DQ_Numerical Field Fingerprint", "Distribution Check");
			dqCheckNames.put("DQ_Record Anomaly", "Record Anomaly");
			dqCheckNames.put("DQ_Uniqueness -Seleted Fields", "Selected Field Uniqueness (User Selected Fields)");
			dqCheckNames.put("DQ_Uniqueness -Primary Keys", "Data Uniqueness (Primary Keys)");
			dqCheckNames.put("DQ_DateRuleCheck", "Date Rule Check (Record Anomaly)");
			dqCheckNames.put("DQ_Rules",
					"Custom Rules <p>(Referential, Orphan, Cross Referential and Regex Rules)</p>");
			dqCheckNames.put("DQ_Pattern_Data", "Pattern UnMatch Data (Conformity)");
			dqCheckNames.put("DQ_Bad_Data", "Bad Data (Conformity)");
			dqCheckNames.put("DQ_Timeliness", "Timeliness");
			dqCheckNames.put("DQ_GlobalRules", "Global Rules");
			dqCheckNames.put("DQ_DefaultCheck", "Default Check");
			dqCheckNames.put("DQ_Sql_Rule", "SQL Rules (Custom Rules)");

			for (String dqCheck : dqCheckNames.keySet()) {
				try {

					SqlRowSet dashboardDetails = resultsDAO.calculateAvgMetricsForCheck(valIdsStr, dqCheck);

					double avgDQI = 0.0;
					String keyMetric = "";

					if (dashboardDetails != null) {
						while (dashboardDetails.next()) {
							String DQI = dashboardDetails.getString(1);
							keyMetric = dashboardDetails.getString(3);
							if (DQI != null)
								avgDQI = Double.parseDouble(DQI);
							break;
						}
					}

					if (avgDQI > 0.0) {
						fieldValue = fieldValue + "<tr><td>" + dqCheckNames.get(dqCheck) + "</td>";
						if(dqCheck.equalsIgnoreCase("DQ_Data Drift") && (keyMetric==null || keyMetric.isEmpty())){
							fieldValue = fieldValue + "<td></td>";
						} else {
							if (avgDQI > 90)
								fieldValue = fieldValue + "<td bgcolor=\"#69F95E\"><b>" + df.format(avgDQI) + "</b></td>";
							else
								fieldValue = fieldValue + "<td bgcolor=\"#FE5648\"><b>" + df.format(avgDQI) + "</b></td>";
						}
						fieldValue = fieldValue + "</tr>";
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			fieldValue = fieldValue + "</tbody></table></div>";

			// Set the key
			JSONObject keyObj = new JSONObject();
			String key = ds_id + "." + databaseSchemaName;
			keyObj.put("key", key);

			JSONArray customFieldsArray = new JSONArray();
			JSONObject fe_Quality_Score = new JSONObject();
			fe_Quality_Score.put("field_id", customFieldsMap.get("FE Quality Score"));
			fe_Quality_Score.put("value", fieldValue);
			customFieldsArray.put(fe_Quality_Score);
			keyObj.put("custom_fields", customFieldsArray);
			resArray.put(keyObj);

			String requestBody = resArray.toString();

			res_status = postMessageToAlation(url, requestBody);

			// Update Flag Status for Schema
			int ds_schema_id = getSchemaIDFromAlation(databaseSchemaName);
			updateFlagStatusInAlationForSchema(ds_schema_id, totalAvgDQI);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res_status;
	}

	private void updateFlagStatusInAlationForSchema(int ds_id, double totalAvgDQI) {
		System.out.println("\n====> Update Flag Status for Schema - START <=====");

		try {
			// Read threshold DQI
			String dqi_threshold_prop = integrationProperties.getProperty("alation.integration.dqi.threshold");
			double dqi_threshold = 90.0;

			if (dqi_threshold_prop != null && !dqi_threshold_prop.trim().isEmpty()) {
				try {
					dqi_threshold = Double.valueOf(dqi_threshold_prop.trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * Delete old flags
			 */
			System.out.println("\n====> Fetch the details of existing flags for schema ....");
			String deleteFlagsUrl = "https://" + alationBaseUrl + "/integration/flag/?oid=" + ds_id + "&otype=schema";

			JSONArray flagsList = readAlationApiResponseDetail(deleteFlagsUrl);

			if (flagsList != null) {

				for (int i = 0; i < flagsList.length(); ++i) {
					int flagId = ((JSONObject) flagsList.get(i)).getInt("id");
					System.out.println("\n====> Deleting flag with Id : " + flagId);

					// Delete the flag
					deleteFlagFromAlation(flagId);
				}
			}

            DecimalFormat df = new DecimalFormat("#0.00");

			/*
			 * Update the flag status in Alation based on overall DQI
			 */
			System.out.println("\n====> Preparing flag status request ...");
			String flagType = "";
			String flagReason = "";
			if (totalAvgDQI >= dqi_threshold)
				flagType = "ENDORSEMENT";
			else if (totalAvgDQI > (dqi_threshold - 10) && totalAvgDQI < dqi_threshold) {
				flagType = "WARNING";
				flagReason = "Current DQI [" + df.format(totalAvgDQI) + "] of the dataset is below the threshold";
			} else {
				flagType = "DEPRECATION";
				flagReason = "Current DQI [" + df.format(totalAvgDQI) + "] of the dataset is below the threshold";
			}

			JSONObject subjectObj = new JSONObject();
			subjectObj.put("id", ds_id);
			subjectObj.put("otype", "schema");
			subjectObj.put("url", "/schema/" + ds_id);

			JSONObject flagStatusReqObj = new JSONObject();
			flagStatusReqObj.put("flag_type", flagType);
			if (flagType != "ENDORSEMENT") {
				flagStatusReqObj.put("flag_reason", flagReason);
			}
			flagStatusReqObj.put("subject", subjectObj);

			String flagStatusReqBody = flagStatusReqObj.toString();
			String flagStatusUrl = "https://" + alationBaseUrl + "/integration/flag/";

			System.out.println("\n====> Post flag status request ..");
			postMessageToAlation(flagStatusUrl, flagStatusReqBody);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> Update Flag Status for Schema - END <=====");
	}

	// Database id from alation api
	private int getSchemaDS_IDFromAlation(String schemaName) {
		int DS_ID = 0;
		try {
			String url = "https://" + alationBaseUrl + "/integration/v2/schema/?name=" + schemaName;
			JSONArray schemaDS_Json = readAlationApiResponseDetail(url);

			if (schemaDS_Json != null && schemaDS_Json.length() > 0) {
				for (int i = 0; i < schemaDS_Json.length(); i++) {
					JSONObject obj = schemaDS_Json.getJSONObject(i);
					DS_ID = obj.getInt("ds_id");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DS_ID;
	}

	// schema id from alation api
	private int getSchemaIDFromAlation(String schemaName) {

	    int DS_SCHEMA_ID = 0;

	    try {
			String url = "https://" + alationBaseUrl + "/integration/v2/schema/?name=" + schemaName;
			JSONArray schemaDS_Json = readAlationApiResponseDetail(url);

			if (schemaDS_Json != null && schemaDS_Json.length() > 0) {

			    for (int i = 0; i < schemaDS_Json.length(); i++) {

				    JSONObject obj = schemaDS_Json.getJSONObject(i);
					DS_SCHEMA_ID = obj.getInt("id");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DS_SCHEMA_ID;
	}

	// Custom Field with Id from alation api
	private Map<String, Integer> readCustomFieldNames() {
		Map<String, Integer> customFieldsMap = new HashMap<String, Integer>();
		try {
			String url = "https://" + alationBaseUrl + "/integration/v2/custom_field/?name_singular__icontains=FE";
			JSONArray customFields_Json = readAlationApiResponseDetail(url);

			if (customFields_Json != null && customFields_Json.length() > 0) {

				for (int i = 0; i < customFields_Json.length(); i++) {
					JSONObject obj = customFields_Json.getJSONObject(i);
					customFieldsMap.put(obj.getString("name_singular"), obj.getInt("id"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customFieldsMap;
	}

	private JSONArray readAlationApiResponseDetail(String url) {

		JSONArray schemaDS = null;
		System.out.println("\n====> url:" + url);
		try {
			// Invoking the API
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("TOKEN", accessToken);

			// set my entity
			HttpEntity<Object> entity = new HttpEntity<Object>(headers);

			ResponseEntity<String> out = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			System.out.println("\n====> Response status: " + out.getStatusCode());
			System.out.println("\n====> Response body: " + out.getBody());

			if (out.getStatusCode() == HttpStatus.ACCEPTED || out.getStatusCode() == HttpStatus.OK) {
				String responseBody = out.getBody();
				schemaDS = new JSONArray(responseBody);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemaDS;
	}

	private boolean postMessageToAlation(String publishUrl, String requestBody) {
		boolean status = false;
		try {
			// Invoking the API
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("TOKEN", accessToken);

			// set my entity
			HttpEntity<Object> entity = new HttpEntity<Object>(requestBody, headers);

			System.out.println("\n====>url: " + publishUrl);

			System.out.println("\n====> request: " + requestBody);

			ResponseEntity<String> out = restTemplate.exchange(publishUrl, HttpMethod.POST, entity, String.class);

			System.out.println("\n====> status code: " + out.getStatusCode());
			System.out.println("\n====> response body: " + out.getBody());

			if (out.getStatusCode() == HttpStatus.ACCEPTED || out.getStatusCode() == HttpStatus.OK)
				status = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	private boolean deleteFlagFromAlation(int flag_id) {
		boolean status = false;
		try {
			// Invoking the API
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("TOKEN", accessToken);

			// set my entity
			HttpEntity<Object> entity = new HttpEntity<Object>(headers);

			String publishUrl = "https://databuckdq.alationcatalog.com/integration/flag/" + flag_id + "/";
			System.out.println("\n====>url: " + publishUrl);

			ResponseEntity<String> out = restTemplate.exchange(publishUrl, HttpMethod.DELETE, entity, String.class);

			System.out.println("\n====> status code: " + out.getStatusCode());

			if (out.getStatusCode() == HttpStatus.NO_CONTENT)
				status = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public String validateAndGenerateNewAccessToken(String accessToken,String refreshToken,String alationBaseUrl){
		//Get accessToken if its expired
		System.out.println("\n====>Validating Alation API Access Token...");
		try {
			String userIdStr = integrationProperties.getProperty("alation.integration.userid");
			if (!userIdStr.trim().isEmpty()) {
				int userId = Integer.parseInt(userIdStr.trim());

				boolean isAPIAccessTokenValid =false;
				if(accessToken!=null && !accessToken.trim().isEmpty())
					isAPIAccessTokenValid = alationAPIService.validateAlationAccessToken(alationBaseUrl,accessToken,userId);

				if(!isAPIAccessTokenValid) {
					System.out.println("\n====>Validation failed for API Access token ["+accessToken+"] ");
					try {
						accessToken = alationAPIService.generateAlationAccessToken(alationBaseUrl, refreshToken, userId);

						if (accessToken != null && !accessToken.trim().isEmpty()) {
							try {
								StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
								encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
								String encryptedAccessToken = encryptor.encrypt(accessToken);

								resultsDAO.updateApplicationPropertyByNameAndCategory("Integration".toLowerCase(), "alation.integration.accesstoken", encryptedAccessToken);
								System.out.println("\n====>New Alation API Access Token is generated and updated to DB");

								integrationProperties = taskDAO.getPropertiesFromDB("integration");
							} catch (Exception e) {
								System.out.println("\n====>Exception occurred while encrypting property:[accessToken] of Category:[Integration]!!, Please check and update correct value.");
								e.printStackTrace();
							}
						}
					}catch (Exception e){
						e.printStackTrace();
						accessToken="";
					}
				}else
					System.out.println("\n====>Existing Alation API Access Token is valid");
			}
		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
		return accessToken;
	}
}