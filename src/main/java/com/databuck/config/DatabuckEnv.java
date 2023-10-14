package com.databuck.config;

import org.springframework.stereotype.Service;

import com.databuck.constants.DatabuckConstants;

@Service
public class DatabuckEnv {

	public static String DB_TYPE=DatabuckConstants.DB_TYPE_MYSQL;
}
