package com.databuck.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * this class is reletd to Admintrator--> module-->group--->view group
 * 
 * 
 * @author appzop6
 *
 */
public class Role implements Serializable {

	private long idRole;
	private String roleName;
	private String description;
	private Date createdAt;
	private Date updatedAt;

	public long getIdRole() {
		return idRole;
	}

	public void setIdRole(long idRole) {
		this.idRole = idRole;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Role [idRole=" + idRole + ", roleName=" + roleName + ", description=" + description + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", getIdRole()=" + getIdRole() + ", getRoleName()="
				+ getRoleName() + ", getDescription()=" + getDescription() + ", getCreatedAt()=" + getCreatedAt()
				+ ", getUpdatedAt()=" + getUpdatedAt() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public Role() {
		
		this.idRole = idRole;
		this.roleName = roleName;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	

}
