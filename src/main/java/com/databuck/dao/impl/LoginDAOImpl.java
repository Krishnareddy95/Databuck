package com.databuck.dao.impl;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ILoginDAO;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Date;
import org.apache.log4j.Logger;
@Repository
public class LoginDAOImpl implements ILoginDAO {
	 //static Logger logger= Logger.getLogger(LoginDAOImpl.class.getName());
	private static final Logger LOG = Logger.getLogger(LoginDAOImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Override
	public Long userAuthentication(String email, String password) {
		String encrypted = null;
		SqlRowSet queryForRowSet =null;
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		String sql = "select password,idUser from " + user_table + " where email='" + email + "'";
		
		try
		{
			queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		}catch(org.springframework.dao.RecoverableDataAccessException e)
		{
			try{
			LOG.info("problem with connection pool");
			 queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			}catch (Exception w) {
				LOG.error("problem with connection pool2");
				LOG.error(w.getMessage());
				 queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			}
		}
		
		Long idUser=null;
		if (queryForRowSet.next()) {
			encrypted = queryForRowSet.getString(1);
			idUser=queryForRowSet.getLong(2);
		}
		if (encrypted != null) {
			boolean checkpw = BCrypt.checkpw(password, encrypted);
			if(checkpw)
			{
				return idUser;
			}
			return null;
		} else {
			return null;
		}
	}
	@Override
	public Long getRoleIdFromRoleTable(String roleName) {
		String query = "select idRole from Role where roleName = " + "'" + roleName + "'";
		Long idRole = jdbcTemplate.queryForObject(query, Long.class);
		return idRole;
	}



	@Override
	public void insertDatabuckLoginActivity(Long idUser, String username, String sReqURL, String session_id, String databuck_feature) {
		try
		{
			Date date=new Date();
			String sql = "insert into logging_activity(user_id, user_name, access_url,activity_log_time,session_id,databuck_feature) "
					+ " VALUES (?,?,?,?,?,?)";
			int update = jdbcTemplate.update(sql,idUser,username,sReqURL,date,session_id,databuck_feature);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Long insertDatabuckLoginAccessLog(Long idUser, String username, String sReqURL, String session_id, String databuck_feature,String login_status) {
		try
		{
			Date date=new Date();
			String sql = "insert into databuck_login_access_logs(user_id, user_name, access_url,activity_log_time,session_id,databuck_feature,login_status) "
					+ " VALUES (?,?,?,?,?,?,?)";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

					PreparedStatement pst = con.prepareStatement(sql, new String[] { "row_id" });
					pst.setObject(1,idUser);
					pst.setString(2,username);
					pst.setString(3,sReqURL);
					pst.setTimestamp(4, new java.sql.Timestamp(date.getTime()));
					pst.setString(5,session_id);
					pst.setString(6,databuck_feature);
					pst.setString(7,login_status);
					return pst;
				}
			},keyHolder);
			//LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return idUser;
	}

	@Override
	public int updateDatabuckLoginAccessLog(Long row_id, Long idUser, String login_status) {
		int update = 0;
		try {
			String sql = "";
			sql = "update databuck_login_access_logs set user_id=" + idUser + ", login_status='"+ login_status +"' where row_id=" + row_id;
			update = jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return update;
	}


}
