package com.databuck.integration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

@Service
public class SlackIntegrationService {

	@Autowired
	private Properties integrationProperties;

	void sendAlertNotificationBySlack(String channel_id, String message) {
		try {
			// Get slack token
			//String slack_token = "xoxb-4086339287778-4092422634054-7jJMu8xRwGmpBT8R2QG4Zslf";
			 String slackToken = integrationProperties.getProperty("slack.token");

			if (slackToken != null && !slackToken.trim().isEmpty()) {

				// Get Slack Instance
				MethodsClient client = Slack.getInstance().methods();

				// Call the chat.postMessage method using the built-in WebClient
				ChatPostMessageResponse result = client
						.chatPostMessage(r -> r.token(slackToken.trim()).channel(channel_id).text(message));

				System.out.println("result :" + result);

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while publishing message to slack channel");
			e.printStackTrace();
		}
	}
}
