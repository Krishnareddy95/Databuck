package com.databuck.service;

import java.util.List;

import com.databuck.bean.Role;

public interface IRoleServie {
	
	public List<Role> getData();
	
	// Delete A Role
			public long deleteRole(long idRole);

}
