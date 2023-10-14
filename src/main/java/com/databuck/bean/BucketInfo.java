package com.databuck.bean;
import java.util.*;
public class BucketInfo {

	private int itrid; // dummy id
	private boolean check;
	private String name;
	private boolean isSingle;
	private Date creationDate;
	
	public BucketInfo() {
	}
	public int getItrid() {
		return itrid;
	}
	public void setItrid(int itrid) {
		this.itrid = itrid;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSingle() {
		return isSingle;
	}
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	@Override
	public String toString() {
		return "BucketInfo [itrid=" + itrid + ", check=" + check + ", name=" + name + ", isSingle=" + isSingle
				+ ", creationDate=" + creationDate + "]";
	}
	
	
	
}
