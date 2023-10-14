package com.databuck.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;

@Component
public class TokenValidator {

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	public boolean isValid(String token) {
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
		if (userToken != null) {
			String tokenStatus = "";
			if (userToken != null) {
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			}
			if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE"))
				return true;
			else
				return false;

		} else {
			return false;
		}
	}

}
