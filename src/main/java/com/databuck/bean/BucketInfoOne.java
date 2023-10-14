package com.databuck.bean;

import java.io.Serializable;

public class BucketInfoOne implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bucket_name;
	private String created_date;
	private boolean is_single;
	
	public BucketInfoOne() {
		
	}

	public String getBucket_name() {
		return bucket_name;
	}

	public void setBucket_name(String bucket_name) {
		this.bucket_name = bucket_name;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public boolean isIs_single() {
		return is_single;
	}

	public void setIs_single(boolean is_single) {
		this.is_single = is_single;
	}

	@Override
	public String toString() {
		return "BucketInfoOne [bucket_name=" + bucket_name + ", created_date=" + created_date + ", is_single="
				+ is_single + "]";
	}
	
	
}
