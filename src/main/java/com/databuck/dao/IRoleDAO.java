package com.databuck.dao;

import java.util.List;

import com.databuck.bean.Role;



public interface IRoleDAO {
	
	// get Task
		public List<Role> getData();
		
		
		// Delete A Role
		public long deleteRole(long idRole);

}
