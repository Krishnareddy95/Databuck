package com.databuck.bean;

import java.io.Serializable;

/*
 *
 * this class
 * UserLogin used for 
 * login the user page field
 * 
 * @Author vivek jaiswal
 * @created date 23-08-16
 * 
 */
public class UserLogin implements Serializable {

	// data member
	private String email;
	private String password;
	// settet and getter method

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserLogin email=  " + email + " password=   " + password;
	}

	
	
}
