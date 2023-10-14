package com.databuck.integration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class SQSIntegrationService {
	@Autowired
	private Properties appDbConnectionProperties;

	public boolean sendAlertNotificationBySQS(String sqsQueueUrl, String messageBody) {
		try {
			String sqsMessageEnabled = appDbConnectionProperties.getProperty("sqs.notifications");

			if (sqsMessageEnabled != null && sqsMessageEnabled.trim().equalsIgnoreCase("Y")) {

				// Get SQS Queue Url and region
				String region = appDbConnectionProperties.getProperty("sqs.notifications.queue.region");

				if (sqsQueueUrl != null && !sqsQueueUrl.trim().isEmpty() && region != null
						&& !region.trim().isEmpty()) {

					// Place message in queue
					SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(sqsQueueUrl)
							.withMessageBody(messageBody);

					AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(region).build();
					sqs.sendMessage(send_msg_request);

				} else {
					System.out.println("\n====> SQS queue Url/ region is missing !!");
				}
			}

		} catch (Exception e) {
			System.out.println("Exception while placing message in SQS queue: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}
}
