package com.databuck.util;

public class ReturnHolder {

	private Integer status;

	private Object payLoad;

	private String message;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public ReturnHolder(){
		//this.status=200;
		//this.message="Data Sent Successfully";
	}

}
