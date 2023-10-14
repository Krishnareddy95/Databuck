package com.databuck.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserLDAPGroupHolder {

	private static Map<String, String> userLDAPGroupMap = new HashMap<String, String>();

	public static void addOrUpdateUserLDAPGroup(String username, String ldapGroups) {
		if (username != null && !username.trim().isEmpty()) {
			userLDAPGroupMap.put(username.trim(), ldapGroups.trim());
		}
	}

	public static String getLDAPGroupsForUser(String username) {
		String ldapGroups = "";
		if (username != null && !username.trim().isEmpty()) {
			ldapGroups = userLDAPGroupMap.get(username.trim());
		}

		return ldapGroups;
	}
}
