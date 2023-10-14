package com.databuck.dao.impl;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.databuck.dao.IimportBusinessRulesDAO;

@Repository
public class ImportBusinessRulesDAOImpl implements IimportBusinessRulesDAO{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Properties appDbConnectionProperties;

	private static final Logger LOG = Logger.getLogger(ImportBusinessRulesDAOImpl.class);
	
	@Override
	public int deleteBusinessRules(Long idData1, Long idData2) {
		
		try {
			LOG.info("!!!!!!!!! In deleteBusinessRules!!!!!!!!!!!");
			//delete FROM listColRules where idData in
			int update = jdbcTemplate.update("DELETE FROM listColRules WHERE idData in (?,?)", new Object[] { idData1,idData2 });

			return 1;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 2;
		}
		
		
		
	}
	
	

}
