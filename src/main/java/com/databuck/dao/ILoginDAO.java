package com.databuck.dao;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

public interface ILoginDAO {

	public Long userAuthentication(String email, String password);
	public Long getRoleIdFromRoleTable(String roleName);
	public void insertDatabuckLoginActivity(Long idUser, String username, String sReqURL, String session_id,String databuck_feature);
	public Long insertDatabuckLoginAccessLog(Long idUser, String username, String sReqURL, String session_id,String databuck_feature,String login_status);

	public int updateDatabuckLoginAccessLog(Long row_id,Long idUser,String login_status);
}
