package com.databuck.integration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SNSIntegrationService {

	@Autowired
	private Properties appDbConnectionProperties;

	public void sendAlertNotificationBySNS(String topicArn, String message) {
		try {
			String snsNotifications = appDbConnectionProperties.getProperty("SNSNotifications");
			String sns_iamRole = appDbConnectionProperties.getProperty("sns.notifications.iamrole");
			String region = appDbConnectionProperties.getProperty("sns.topic.region");

			if (snsNotifications != null && snsNotifications.trim().equalsIgnoreCase("Y")) {

				if (topicArn != null && !topicArn.trim().isEmpty() && region != null && !region.trim().isEmpty()) {

					AmazonSNS snsClient = null;

					if (sns_iamRole != null && sns_iamRole.trim().equalsIgnoreCase("Y")) {
						snsClient = AmazonSNSClientBuilder.standard().withRegion(region).build();

					} else {
						String secretKey = appDbConnectionProperties.getProperty("sns.aws.secretKey");
						String accessKey = appDbConnectionProperties.getProperty("sns.aws.accessKey");

						if (secretKey != null && accessKey != null && !accessKey.trim().isEmpty()
								&& !secretKey.trim().isEmpty()) {

							AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
							snsClient = AmazonSNSClientBuilder.standard().withRegion(region)
									.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
						} else {
							System.out.println("\n=====> Either Secret Key or Access Key is Null or Blank");
						}
					}
					if (snsClient != null) {
						PublishResult result = snsClient
								.publish(new PublishRequest().withMessage(message).withTopicArn(topicArn));
						System.out.println("\n=====> SNS notification sent successfully : " + result);
					}

				} else {
					System.out.println("\n=====> SNS topicARN or region is missing !!");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception while sending sns notification:" + e.getMessage());
		}
	}
}
