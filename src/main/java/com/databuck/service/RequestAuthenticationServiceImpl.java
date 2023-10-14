package com.databuck.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RequestAuthenticationServiceImpl implements RequestAuthenticationService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(RequestAuthenticationServiceImpl.class);

	@Override
	public Long getProjectOfValidation(Long idApp) {
		LOG.debug("idApp"+idApp);
		long projectId = 0;
		String sql = String.format("select project_id from listApplications where active ='yes' and idApp= %1$s",
				idApp);
		projectId = jdbcTemplate.queryForObject(sql, Long.class);
		return projectId;
	}

}
