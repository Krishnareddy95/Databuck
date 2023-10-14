package com.databuck.bean;

import javax.validation.constraints.NotNull;


public class EmailTemplateDto {
    private Integer id;
    private String templateCode;
    private String subject;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String body;
    private String attachments;
    private Boolean status;
    

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public EmailTemplateDto() {}
	
	public EmailTemplateDto licenseExpiryTemplate(String templateCode,String from,String to,String cc) {
		if(templateCode.trim().equalsIgnoreCase("LICENSEEXP")) {
			return new EmailTemplateDto(1,"LICENSEEXP","License Renewal Reminder", from, to, cc, null,
					"<html><body>Hello <NAME>,<br/>Your license key for databuck is expiring within <EXPIRYDAYS> days."
					+ "<br/>It is important to keep your license up to date in order to continue"
					+ " getting updates for databuck and continued support.<br/>If you wish to renew"
					+ " your license, simply click the link below and follow the instructions.<br/>"
					+ "Your license expires on: <EXPIRYDATE>.<br/>Renew now: <RENEWLINK>.<br/>Best"
					+ " regards,<br/>Databuck </body></html>", null, true);
		}else {
			return null;
		}
	}
	
	public EmailTemplateDto(Integer id, @NotNull String templateCode,String subject, String from, String to, String cc, String bcc,
			String body, String attachments, Boolean status) {
		super();
		this.id = id;
		this.templateCode = templateCode;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.body = body;
		this.attachments = attachments;
		this.status = status;
	}

	@Override
	public String toString() {
		return "EmailTemplateDto [id=" + id + ", templateCode=" + templateCode + ", subject=" + subject + ", from="
				+ from + ", to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", body=" + body + ", attachments="
				+ attachments + ", status=" + status + "]";
	}

	
}
