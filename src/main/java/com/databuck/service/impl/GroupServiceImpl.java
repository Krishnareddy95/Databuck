package com.databuck.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import com.databuck.dao.IUserDAO;

import com.databuck.bean.User;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.dao.IGroupDAO;
import com.databuck.service.IGroupService;

@Service
public class GroupServiceImpl implements IGroupService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private IUserDAO UserDAO;

	@Autowired
	private IGroupDAO groupDAO;

	@Override
	public List<User> getAllGroups() {
		return groupDAO.getAllGroups();
	}

	@Override
	public List<User> getAllGroupsfromActiveDirectory() {
		// TODO Auto-generated method stub
		return groupDAO.getAllGroupsfromActiveDirectory();
	}
	@Override
	public List<User> getAllassignGroups(Long projectId) {
		return groupDAO.getAllassignGroups(projectId);
	}

	@Override
	public List<User> getAllassignGroupsfromActiveDirectory(Long projectId) {
		// TODO Auto-generated method stub
		return groupDAO.getAllassignGroupsfromActiveDirectory(projectId);
	}
	

}
