package com.databuck.integration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.databuck.bean.EmailTemplateDto;
import com.databuck.constants.DatabuckConstants;
import com.databuck.service.EmailServiceImpl;

@Service
public class EmailIntegrationService {

	@Autowired
	private Properties appDbConnectionProperties;
	
    @Autowired
    private EmailServiceImpl emailServiceImpl;
    
	@Autowired
	public Properties licenseProperties;


	public boolean sendAlertNotificationByEmail(String subject, String message, String email) {
		boolean status = false;
		try {
			System.out.println("\n====> Processing Alert Notification by Email");
			if (email != null && !email.trim().isEmpty()) {
//				status = sendEmailBySmtpWithEmail(subject, message, email);

				status = sendEmailByMimeMessage(subject, message, email);
			} else
				System.out.println("\n====> Email Id is missing");

		} catch (Exception oException) {
			System.out.println("Exception occurred while sending Notification:" + oException.getMessage());
			oException.printStackTrace();
		}
		return status;
	}

	public boolean sendEmailByMimeMessage(String subject, String message, String sEmail){

		boolean emailStatus=false;
		try {
			// Smtp host
			String HOST = appDbConnectionProperties.getProperty("smtp_host").trim();

			// smtp port
			String PORT = appDbConnectionProperties.getProperty("smtp_port").trim();

			// smtp username
			String authMode = appDbConnectionProperties.getProperty("smtp_mode").trim();

			System.out.println("\n====>HOST: " + HOST);
			System.out.println("\n====>PORT: " + PORT);
			System.out.println("\n====>authMode: " + authMode);

			String SMTP_USERNAME = "";
			String SMTP_PASSWORD = "";

			try {
				SMTP_USERNAME = appDbConnectionProperties.getProperty("smtp_username").trim();
				SMTP_PASSWORD = appDbConnectionProperties.getProperty("smtp_password").trim();
			}catch (Exception e){
				e.printStackTrace();
			}

			String mailSender = appDbConnectionProperties.getProperty("mailSender").trim();

			System.out.println("\n====>mailSender: " + mailSender);
			System.out.println("\n====>mailRecepients: " + sEmail);

			if ((authMode.isEmpty() || authMode.equalsIgnoreCase("noAuth"))
					&& (HOST.isEmpty() || PORT.isEmpty() || mailSender.isEmpty() || sEmail.isEmpty()))
					 {
				System.out.println("Some or all of the properties required for email noAuth mode configuration are missing.");
			} else if ((!authMode.isEmpty() && authMode.equalsIgnoreCase("Auth"))
					&& (HOST.isEmpty() || PORT.isEmpty() || SMTP_USERNAME.isEmpty() || SMTP_PASSWORD.isEmpty()
					|| mailSender.isEmpty() || sEmail.isEmpty())) {
				System.out.println("Some or all of the properties required for email Auth mode configuration are missing.");
			} else {
				Properties props = new Properties();
				props.put("mail.smtp.host", HOST);
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.port", PORT);

				Session session = null;

				if (authMode.equalsIgnoreCase("Auth")) {
					System.out.println("\n====>Setting Auth mode properties");

					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.ssl.trust", HOST);
					props.put("mail.smtp.ssl.protocols", "TLSv1.2");

					final String userName = appDbConnectionProperties.getProperty("smtp_username");
					final String password = appDbConnectionProperties.getProperty("smtp_password");

					// Get the Session object.
					session = Session.getInstance(props,
							new javax.mail.Authenticator() {
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication(userName, password);
								}
							});
				}else{
					session = Session.getDefaultInstance(props, null);
				}

				emailStatus = sendEmail(session, mailSender, sEmail, subject, message);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return emailStatus;
	}

	private boolean sendEmail(Session session,String fromEmail, String toEmail, String subject, String body){
		try
		{
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));

			msg.setReplyTo(InternetAddress.parse(toEmail, false));

			msg.setSubject(subject, "UTF-8");

			msg.setText(body, "UTF-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("\n====>Message is ready to sent");
			Transport.send(msg);

			System.out.println("\n====>Email Sent Successfully!!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    @Scheduled(cron = DatabuckConstants.EVERYDAY_AT_ELEVEN_CRON)
	public String sendLicenseExpiryMail() { 
    	String result = "mail not sent";
    	String isExpiryMailAvailable = licenseProperties.getProperty("licenseExpiryMail", "N");
		if(isExpiryMailAvailable!=null && isExpiryMailAvailable.trim().equalsIgnoreCase("Y")) {
	    	String from = appDbConnectionProperties.getProperty("mailSender");
			String to = appDbConnectionProperties.getProperty("mailRecepients");
			String cc = appDbConnectionProperties.getProperty("mailCC");
			String mailRecepientName = appDbConnectionProperties.getProperty("mailRecepientName","Client");
			String licenseRenewLink = appDbConnectionProperties.getProperty("licenseRenewLink","https://firsteigen.com/contact-us/");
			try {
				Integer expiryDays = getDaysBeforeLicenseRenewal();
				if(expiryDays==15 || expiryDays==30 || expiryDays ==45 || expiryDays == 60) {
					EmailTemplateDto emailtemplate = new EmailTemplateDto().licenseExpiryTemplate("LICENSEEXP",from,to,cc);
					HashMap<String,String> tags = new HashMap<String,String> ();
					tags.put("<EXPIRYDATE>",lastDateofLicense());
					tags.put("<NAME>", mailRecepientName);
					tags.put("<RENEWLINK>",licenseRenewLink);
					tags.put("<EXPIRYDAYS>", expiryDays.toString());
					String textBody = emailServiceImpl.replaceNameTags(emailtemplate.getBody(), tags);
					emailServiceImpl.sendHTMLMessageWithoutAttachment(emailtemplate.getTo(),emailtemplate.getFrom(), emailtemplate.getSubject(), textBody);
					result = "mail sent to "+to ;
				}
			} catch (Exception e) {
				e.printStackTrace();
				result =  e.getMessage();
			}
		}
		return result;
	}
	
	private int getDaysBeforeLicenseRenewal() {
		  String licenseKey = licenseProperties.getProperty("LicenseKey");
		  int daysToExpire = 29;
		  if(licenseKey!=null && !licenseKey.trim().isEmpty()) {
		    	StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		    	decryptor.setPassword(DatabuckConstants.LIECENSE_DECRYPTOR_PASSWORD);
		    	//String encrypted = decryptor.encrypt("ADMI-03-4.2-02222023");
		    	String decryptedLicense = decryptor.decrypt(licenseKey).split("-")[3];
		    	SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
		    	Date licenseExpiryDate = null;
		    	try {
					licenseExpiryDate = format.parse(decryptedLicense);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	daysToExpire = Math.abs(Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()), new LocalDate(Calendar.getInstance().getTime())).getDays());
		    	return daysToExpire;
		  } else {
			  System.out.println("LicenseKey not found !!");
			  return daysToExpire;
		  }
	    }
	
	private String lastDateofLicense() {
		  String licenseKey = licenseProperties.getProperty("LicenseKey");
		  if(licenseKey!=null && !licenseKey.trim().isEmpty()) {
		    	StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		    	decryptor.setPassword(DatabuckConstants.LIECENSE_DECRYPTOR_PASSWORD);
		    	String decryptedLicense = decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[3];
		    	SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
		    	Date licenseExpiryDate = null;
		    	try {
					licenseExpiryDate = format.parse(decryptedLicense);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		    	String dateAsString = df.format(licenseExpiryDate);
		    	return dateAsString;
		  } else {
			  System.out.println("LicenseKey not found !!");
			  return "";
		  }
	    }


}