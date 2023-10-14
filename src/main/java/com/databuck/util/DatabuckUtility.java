package com.databuck.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Properties;
import org.apache.log4j.Logger;

@Service
public class DatabuckUtility {

	@Autowired
	private Properties appDbConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(DatabuckUtility.class);

	public String getCurrentHostName() {
		InetAddress ip;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			LOG.debug("\n====>Current Hostname : " + hostname);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return hostname;
	}
	
	public static String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		return databuckHome;
	}

	public Long getGlobalRulesFetchLimit(){
		long globalRuleFetchLimit=30000;
		try{
			String globalRulesResultLimitStr = appDbConnectionProperties.getProperty("globalrules.results.limit");
			globalRuleFetchLimit = Long.parseLong(globalRulesResultLimitStr);
			if(globalRuleFetchLimit <=0)
				globalRuleFetchLimit= 30000;
		}catch (Exception e){
			LOG.error("Syntax issue with property 'globalrules.results.limit', hence continuing with default value 30000");
			LOG.error(e.getMessage());
		}
		return globalRuleFetchLimit;
	}

	public Long getResultsFetchLimit(){
		long resultsFetchLimit=30000;
		try{
			String resultsFetchLimitStr = appDbConnectionProperties.getProperty("results.fetch.limit");
			resultsFetchLimit = Long.parseLong(resultsFetchLimitStr);
			if(resultsFetchLimit <=0)
				resultsFetchLimit= 30000;
		}catch (Exception e){
			LOG.error("Syntax issue with property 'results.fetch.limit', hence continuing with default value 30000");
			LOG.error(e.getMessage());
		}
		return resultsFetchLimit;
	}

	public static String getIntegerValue(String d){
		return String.format("%d",(long)Double.parseDouble(d));
	}

	public static String getIntegerValue(Double d){
		return ""+d.longValue();
	}
}
