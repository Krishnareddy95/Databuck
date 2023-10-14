package com.databuck.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class LoginRequest {

	@NotNull
	@ApiModelProperty(notes = "email", example = "example@gmail.com", required = true)
	private String email;
	
	@NotNull
	@ApiModelProperty(notes = "password", required = true)
	private String password;
	
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
	
	
}
