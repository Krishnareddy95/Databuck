package com.databuck.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.Role;
import com.databuck.dao.IRoleDAO;
import com.databuck.service.IRoleServie;

@Service
public class RoleServiceImpl implements IRoleServie {

	@Autowired
	private IRoleDAO roleDao;
	public List<Role> getData() {
		
		List<Role> role = roleDao.getData();
		
		return role;
	}
	
	
	public long deleteRole(long idRole) {
		
		long count=roleDao.deleteRole(idRole);
		return count;
	}

}
