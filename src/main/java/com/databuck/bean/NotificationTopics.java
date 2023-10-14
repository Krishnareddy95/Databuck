package com.databuck.bean;

public class NotificationTopics {
	private int row_id;
	private String topic_title;
	private int focus_type;
	private int managed_by;
	private String publish_url_1;
	private String publish_url_2;
	private String authorization;
	private String service_id;
	private String password;
	private String url2_authorization;
	private String url2_service_id;
	private String url2_password;

	public int getRow_id() {
		return row_id;
	}

	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}

	public String getTopic_title() {
		return topic_title;
	}

	public void setTopic_title(String topic_title) {
		this.topic_title = topic_title;
	}

	public int getFocus_type() {
		return focus_type;
	}

	public void setFocus_type(int focus_type) {
		this.focus_type = focus_type;
	}

	public int getManaged_by() {
		return managed_by;
	}

	public void setManaged_by(int managed_by) {
		this.managed_by = managed_by;
	}

	public String getPublish_url_1() {
		return publish_url_1;
	}

	public void setPublish_url_1(String publish_url_1) {
		this.publish_url_1 = publish_url_1;
	}

	public String getPublish_url_2() {
		return publish_url_2;
	}

	public void setPublish_url_2(String publish_url_2) {
		this.publish_url_2 = publish_url_2;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl2_authorization() {
		return url2_authorization;
	}

	public void setUrl2_authorization(String url2_authorization) {
		this.url2_authorization = url2_authorization;
	}

	public String getUrl2_service_id() {
		return url2_service_id;
	}

	public void setUrl2_service_id(String url2_service_id) {
		this.url2_service_id = url2_service_id;
	}

	public String getUrl2_password() {
		return url2_password;
	}

	public void setUrl2_password(String url2_password) {
		this.url2_password = url2_password;
	}
	
}
