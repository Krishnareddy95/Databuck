package com.databuck.integration;

import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.JiraIntegrationBean;
import com.databuck.dao.ITaskDAO;

import javax.net.ssl.SSLContext;

@Service
public class JiraIntegrationService {

	@Autowired
	private Properties integrationProperties;

	@Autowired
	private ITaskDAO iTaskDAO;

	public boolean sendAlertNotificationByJIRA(String projectKey, String taskUniqueId) {

		boolean status = false;

		try {

			// Check if Jira Integration is enabled
			String jiraIntegrationEnabled = integrationProperties.getProperty("jira.integration.enabled");

			if (jiraIntegrationEnabled != null && jiraIntegrationEnabled.trim().equalsIgnoreCase("Y")) {

				System.out.println("\n====> Creating Jira Ticket .. ");

				// Get Jira host name and port
				String hostport = integrationProperties.getProperty("jira.api.hostport").trim();

				// Get Jira username
				String username = integrationProperties.getProperty("jira.api.username").trim();

				// Get API Token
				String apiToken = integrationProperties.getProperty("jira.api.apitoken").trim();

				boolean isReqParamsValid = true;

				if (hostport == null || hostport.trim().isEmpty()) {
					isReqParamsValid = false;
					System.out.println("\n====> Jira host port details are missing !!");
				}

				if (username == null || username.trim().isEmpty()) {
					isReqParamsValid = false;
					System.out.println("\n====> username is missing !!");
				}

				if (apiToken == null || apiToken.trim().isEmpty()) {
					isReqParamsValid = false;
					System.out.println("\n====> APIToken is missing !!");
				}

				if (projectKey == null || projectKey.trim().isEmpty()) {
					isReqParamsValid = false;
					System.out.println("\n====> ProjectKey is missing !!");
				}

				if (isReqParamsValid) {
					// Get JIRA Ticket details by TaskUniqueId
					JiraIntegrationBean jiraIntegrationBean = iTaskDAO.getJIRATicketDetailsByTaskUniqueId(taskUniqueId);

					if (jiraIntegrationBean != null) {

						long id = jiraIntegrationBean.getId();

						// Update Jira process status to 'Y'
						iTaskDAO.updateJiraTicketProcessStatus(id, "Y");

						String msgBody = jiraIntegrationBean.getMsgBody();

						JSONObject msgObj = new JSONObject(msgBody);

						JSONObject fieldsObj = msgObj.getJSONObject("fields");

						JSONObject projectObj = fieldsObj.getJSONObject("project");

						projectObj.put("key", projectKey);

						msgBody = msgObj.toString();

						String notEncoded = username + ":" + apiToken;
						String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

						// JIRA API URL
						String publishUrl = "https://" + hostport + "/rest/api/2/issue/";
						System.out.println("\n====> JIRA API Url: " + publishUrl);

						// To ignore ssl
						TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

						SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
								.loadTrustMaterial(null, acceptingTrustStrategy)
								.build();

						SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

//						org.apache.http.impl.client.CloseableHttpClient httpclient = HttpClients.createDefault();
						org.apache.http.impl.client.CloseableHttpClient httpclient = HttpClients.custom()
								.setSSLSocketFactory(csf)
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
								.build();
						HttpPost httpPost = new HttpPost(publishUrl);
						httpPost.setHeader(HttpHeaders.AUTHORIZATION, encodedAuth);

						HttpEntity stringEntity = new StringEntity(msgBody, ContentType.APPLICATION_JSON);
						httpPost.setEntity(stringEntity);

						String responseBody = "";
						String responseCode = "";

						try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

							HttpEntity entity = response.getEntity();
							responseBody = EntityUtils.toString(entity);

							responseCode = "" + response.getStatusLine().getStatusCode();

							EntityUtils.consume(entity);
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("\n====> Jira Status code: " + responseCode);
						System.out.println("\n====> Jira ticket response: " + responseBody);

						if (Integer.parseInt(responseCode) == 201) {
							status = true;
							iTaskDAO.updateJiraTicketSubmitStatus(id, "Y");
						}

					}
				}
			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while posting issue to jira !!");

			e.printStackTrace();
		}

		// Deleting all published Jira Tickets
		iTaskDAO.deleteAllPublishedJiraTickets();

		return status;

	}

}
