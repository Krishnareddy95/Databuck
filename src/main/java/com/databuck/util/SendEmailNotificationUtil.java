package com.databuck.util;

import java.io.File;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.databuck.dao.ITaskDAO;
import org.apache.log4j.Logger;

@Service
public class SendEmailNotificationUtil {
	@Autowired
	private Properties appDbConnectionProperties;
	@Autowired
	public ITaskDAO iTaskDAO;
	private static final Logger LOG = Logger.getLogger(SendEmailNotificationUtil.class);
	
	public void sendEmail(String status, Long idApp, StringBuffer sb){
		if(iTaskDAO.recordExistInRunningtaskStatus(idApp)){
			iTaskDAO.updateRunningtaskStatus(status, idApp);
		}else{
			iTaskDAO.insertIntoRunningtaskStatus(status, idApp);
		}
		if(status.equalsIgnoreCase("Failed")){
		if(appDbConnectionProperties.getProperty("SNSNotifications").trim().equalsIgnoreCase("Y")){
            String schemaFolderNames = iTaskDAO.getSchemaNameFolderNameListDataAccess(idApp);
			sendSNSNotification(idApp, schemaFolderNames.split(";")[0], schemaFolderNames.split(";")[1], status, null);
		}else{
			System.setProperty("java.net.preferIPv4Stack" , "true");

	        Properties props = new Properties();
	        Session session = Session.getDefaultInstance(props, null);

	        String msgBody ="Status: Databuck Processing "+status+" for AppId " + idApp + ".\n";
			 try {
		            Message msg = new MimeMessage(session);
		            msg.setFrom(new InternetAddress(appDbConnectionProperties.getProperty("mailSender")));
		           // msg.addRecipient(Message.RecipientType.TO,new InternetAddress(to, "Dear All"));
		            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(appDbConnectionProperties.getProperty("mailRecepients")));
		            msg.setSubject("Databuck Processing "+status);
		            msg.setText(msgBody);
		            Transport.send(msg);
		            LOG.info("Email sent successfully...");

		        } catch (AddressException e) {
		        	LOG.error(e.getMessage());
		            throw new RuntimeException(e);
		        } catch (MessagingException e) {
		        	LOG.error(e.getMessage());
		            throw new RuntimeException(e);
		        }
		}
		}
	}
	
	
	private void sendSNSNotification(Long idApp, String schemaName, String fileName, String status, String sb){
		
		String secretKey = appDbConnectionProperties.getProperty("sns.aws.secretKey");
		String accessKey = appDbConnectionProperties.getProperty("sns.aws.accessKey");
		String topicArn = appDbConnectionProperties.getProperty("sns.aws.topicARN");
		String region = appDbConnectionProperties.getProperty("sns.topic.region");

		LOG.debug("\n=====> topicArn  : " + topicArn);
		LOG.debug("\n=====> region  : " + region);
		
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		
		String message = null;
		if(null == sb){
		String resultUrlLink = schemaName.concat("/dashboard_table?idApp="+idApp);
		//String message = "Databuck: Data quality checks for validation:"+idApp+" are complete.\n"+
		//"Results can be checked at url:"+resultUrlLink+" after some time.";
		if(status.equalsIgnoreCase("Success")){
		message = "Status: Data quality validation ran for " + fileName + " successfully.\n"+
		"Data Quality Score:\n"+ 
		"For more details click on this link\n "+
		resultUrlLink;
		}else{
			message = "Status: Data quality validation ran for " + fileName + " with idApp "+idApp+" failed.\n";
		}
		}else{
			message = "Status: Data quality validation ran for idApp's " + sb + " is still in progress. Please check\n";
		}
		
		try{
			PublishResult result = snsClient.publish(new PublishRequest()
	                    .withMessage(message)
	                    .withTopicArn(topicArn));
			LOG.debug(result);
		}catch(Exception e){
			LOG.error("Exception while sending sns notification:"+e.getMessage());
		}
	}
	
	public void sendMail(String subject, String message) {
		try {
			LOG.info("\n=====> Send Email - Start <=====");

			String mailxEmailNotification = appDbConnectionProperties.getProperty("mailxEmailNotification");
			String snsNotifications = appDbConnectionProperties.getProperty("SNSNotifications");

			LOG.debug("\n====> mailxEmailNotification :" + mailxEmailNotification);
			LOG.debug("\n====> snsNotifications :" + snsNotifications);
			LOG.debug("\n====> Messsage :" + message);
			LOG.debug("\n====> subject :" + subject);

			if (snsNotifications != null && snsNotifications.trim().equalsIgnoreCase("Y")) {
				sendSNSNotification(message);
			} else if (mailxEmailNotification != null && mailxEmailNotification.trim().equalsIgnoreCase("Y")) {
				sendEmailByMailx(subject, message);
			} else {
				sendEmailBySmtp(subject, message);
			}
		} catch (Exception e) {
			LOG.error("Exception while sending email notification:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendSNSNotification(String message) {
		LOG.info("\n=====> sendSNSNotification - Start <=====");

		try {
			String snsNotifications = appDbConnectionProperties.getProperty("SNSNotifications");
			String sns_iamRole = appDbConnectionProperties.getProperty("sns.notifications.iamrole");
			String topicArn = appDbConnectionProperties.getProperty("sns.aws.topicARN");
			String region = appDbConnectionProperties.getProperty("sns.topic.region");

			LOG.debug("\n=====> snsNotifications  : " + snsNotifications);
			LOG.debug("\n=====> sns.notifications.iamrole  : " + sns_iamRole);
			LOG.debug("\n=====> topicArn  : " + topicArn);
			LOG.debug("\n=====> region  : " + region);

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
							LOG.info("\n=====> Either Secret Key or Access Key is Null or Blank");
						}
					}
					if (snsClient != null) {
						PublishResult result = snsClient
								.publish(new PublishRequest().withMessage(message).withTopicArn(topicArn));
						LOG.debug("\n=====> SNS notification sent successfully : " + result);
					}

				} else {
					LOG.info("\n=====> SNS topicARN or region is missing !!");
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while sending sns notification:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendEmailBySmtp(String subject, String message) {
		LOG.info("\n***** sendEmailBySmtp Started !!");
		try {

			System.setProperty("java.net.preferIPv4Stack", "true");

			// Smtp host
			final String HOST = appDbConnectionProperties.getProperty("smtp_host");

			// smtp port
			final String PORT = appDbConnectionProperties.getProperty("smtp_port");
			String SMTP_USERNAME = "";
			String SMTP_PASSWORD = "";

			// smtp username
			String authMode = appDbConnectionProperties.getProperty("smtp_mode");

			if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
				SMTP_USERNAME = appDbConnectionProperties.getProperty("smtp_username");	
				SMTP_PASSWORD = appDbConnectionProperties.getProperty("smtp_password");
			}

			String mailSender = appDbConnectionProperties.getProperty("mailSender");

			String mailRecepients = appDbConnectionProperties.getProperty("mailRecepients");
			if ((authMode == null || !authMode.equalsIgnoreCase("noAuth")) && (HOST == null || PORT == null || SMTP_USERNAME == null || SMTP_PASSWORD == null || mailSender == null
					|| mailRecepients == null)) {
				LOG.info("Some or all of the properties required for email configuration are missing.");
			}else if ((authMode != null && authMode.equalsIgnoreCase("noAuth")) && (HOST == null || PORT == null || mailSender == null
					|| mailRecepients == null)) {
				LOG.info("Some or all of the properties required for email configuration are missing.");
			} else {

				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", PORT);
				if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.auth", "true");
					//props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
					props.put("mail.smtp.ssl.trust", HOST);
					props.put("mail.smtp.ssl.protocols", "TLSv1.2");
				}else{
					 props.put("mail.smtp.host", HOST);
				}
				Session session = Session.getDefaultInstance(props, null);

				try {
					Message msg = new MimeMessage(session);
					msg.setFrom(new InternetAddress(mailSender));
					msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailRecepients));
					msg.setSubject(subject);
					msg.setText(message);
					Transport transport = session.getTransport();
					if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
						LOG.debug("In transport");
						transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
						LOG.debug("In transport");
						transport.sendMessage(msg, msg.getAllRecipients());
					}else{
						transport.send(msg);
					}
					LOG.info("Email sent successfully...");
				} catch (AddressException e) {
					LOG.error("Exception while sending email:" + e.getMessage());
				} catch (MessagingException e) {
					LOG.error("Exception while sending email" + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Exception while sending email notification by smtp:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public int sendEmailBySmtpWithEmail(String subject, String message, String sEmail) {
		LOG.info("\n***** sendEmailBySmtp Started !!");
		try {

			System.setProperty("java.net.preferIPv4Stack", "true");

			// Smtp host
			final String HOST = appDbConnectionProperties.getProperty("smtp_host");

			// smtp port
			final String PORT = appDbConnectionProperties.getProperty("smtp_port");
			String SMTP_USERNAME = "";
			String SMTP_PASSWORD = "";

			// smtp username
			String authMode = appDbConnectionProperties.getProperty("smtp_mode");

			if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
				SMTP_USERNAME = appDbConnectionProperties.getProperty("smtp_username");	
				SMTP_PASSWORD = appDbConnectionProperties.getProperty("smtp_password");
			}

			String mailSender = appDbConnectionProperties.getProperty("mailSender");

			String mailRecepients = sEmail;
			if ((authMode == null || !authMode.equalsIgnoreCase("noAuth")) && (HOST == null || PORT == null || SMTP_USERNAME == null || SMTP_PASSWORD == null || mailSender == null
					|| mailRecepients == null)) {
				LOG.info("Some or all of the properties required for email configuration are missing.");
				return 2;
			}else if ((authMode != null && authMode.equalsIgnoreCase("noAuth")) && (HOST == null || PORT == null || mailSender == null
					|| mailRecepients == null)) {
				LOG.info("Some or all of the properties required for email configuration are missing.");
				return 2;
			} else {

				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", PORT);
				if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.auth", "true");
					//props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
					props.put("mail.smtp.ssl.trust", HOST);
					props.put("mail.smtp.ssl.protocols", "TLSv1.2");
				}else{
					 props.put("mail.smtp.host", HOST);
				}
				Session session = Session.getDefaultInstance(props, null);

				try {
					Message msg = new MimeMessage(session);
					msg.setFrom(new InternetAddress(mailSender));
					msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailRecepients));
					msg.setSubject(subject);
					msg.setText(message);
					Transport transport = session.getTransport();
					if(authMode == null || !authMode.equalsIgnoreCase("noAuth")){
						//LOG.debug("In transport");
						transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
						//LOG.debug("In transport");
						transport.sendMessage(msg, msg.getAllRecipients());
					}else{
						transport.send(msg);
					}
					LOG.info("Email sent successfully...");
					return 1;
				} catch (AddressException e) {
					LOG.error("Exception while sending email:" + e.getMessage());
					return 3;
				} catch (MessagingException e) {
					LOG.error("Exception while sending email" + e.getMessage());
					return 3;
				}
			}

		} catch (Exception e) {
			LOG.error("Exception while sending email notification by smtp:" + e.getMessage());
			e.printStackTrace();
			return 3;
		}
	}

	public void sendEmailByMailx(String subject, String message) {
		LOG.info("\n***** sendEmailByMailx Started !!");

		try {
			String databuckHome = "/opt/databuck";

			if (System.getenv("DATABUCK_HOME") != null) {
				databuckHome = System.getenv("DATABUCK_HOME");
			} else if (System.getProperty("DATABUCK_HOME") != null) {
				databuckHome = System.getProperty("DATABUCK_HOME");
			}
			String scriptLocation = databuckHome + "/scripts/sendEmail.sh";

			LOG.debug("**** script location: " + scriptLocation);

			message = message.replace("\n", "");
			LOG.debug("**** Message body: " + message);

			LOG.debug("**** Subject: " + subject);

			String mailRecepients = appDbConnectionProperties.getProperty("mailRecepients");
			LOG.debug("**** mailRecepients: " + mailRecepients);

			String cmd[] = { scriptLocation, message, subject, mailRecepients };

			ProcessBuilder builder = new ProcessBuilder().command(cmd);

			builder.start();

		} catch (Exception e) {
			LOG.error("Exception while sending email notification by mailx:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
