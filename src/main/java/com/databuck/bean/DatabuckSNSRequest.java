package com.databuck.bean;

public class DatabuckSNSRequest {
	private Long id;
	private String msgId;
	private String snsMsgBody;
	private String snsAlertEnabled;
	private String snsAlertSent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSnsMsgBody() {
		return snsMsgBody;
	}

	public void setSnsMsgBody(String snsMsgBody) {
		this.snsMsgBody = snsMsgBody;
	}

	public String getSnsAlertEnabled() {
		return snsAlertEnabled;
	}

	public void setSnsAlertEnabled(String snsAlertEnabled) {
		this.snsAlertEnabled = snsAlertEnabled;
	}

	public String getSnsAlertSent() {
		return snsAlertSent;
	}

	public void setSnsAlertSent(String snsAlertSent) {
		this.snsAlertSent = snsAlertSent;
	}

}
