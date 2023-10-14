package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.databuck.bean.Role;
import com.databuck.dao.IRoleDAO;

@Repository
public class RoleDAOImpl implements IRoleDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 
	  view the Group
	 * this getData() is used 
	 *  for retriving the
	 *   Data from role
	 */
	public List<Role> getData() {
		String sql = " SELECT idRole, roleName, description FROM Role";
		List<Role> role = jdbcTemplate.query(sql, new RowMapper<Role>() {

			public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
				Role role = new Role();
                   
				role.setIdRole(rs.getLong("idRole"));
				
				role.setRoleName(rs.getString("roleName"));

				role.setDescription(rs.getString("description"));
				
				return role;
			}

		});

		return role;
	}

	

	/**
	 * Delete the role
	 * 
	 * 
	 */
	

	public long deleteRole(long idRole) {

		String sql = "DELETE FROM Role WHERE idRole=?";
		long count = jdbcTemplate.update(sql, idRole);
		
		return count;
	}
	
	
	
}
