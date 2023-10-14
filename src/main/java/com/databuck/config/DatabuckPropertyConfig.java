package com.databuck.config;

import java.util.LinkedHashMap;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import com.databuck.econstants.DatabuckPropertyCategory;
import com.databuck.interceptor.CacheHeaderFilter;
import com.databuck.interceptor.CacheHeaderFilter.CacheMode;

@Service
@DependsOn({ "jdbcTemplate" })
public class DatabuckPropertyConfig {
	@Autowired
	private DatabuckPropertyLoader databuckPropertyLoader;

	@Autowired
	private Properties appDBInit;

	@Autowired
	private Properties resultDBInit;

	private String activeDirectoryEnabled;
	private String app_mode;

	@Bean
	public Properties appDbConnectionProperties() {
		java.util.Properties appdbProps = new java.util.Properties();

		// Fetch AppDB properties from DB
		appdbProps = databuckPropertyLoader.getPropertiesFromDB(DatabuckPropertyCategory.appdb.toString());

		activeDirectoryEnabled = appDBInit.getProperty("isActiveDirectoryAuthentication");
		if(activeDirectoryEnabled != null && activeDirectoryEnabled.trim().equalsIgnoreCase("Y"))
			activeDirectoryEnabled = "Y";
		else
			activeDirectoryEnabled = "N";			
		appDBInit.setProperty("isActiveDirectoryAuthentication",activeDirectoryEnabled);
		System.out.println("activeDirectoryEnabled:" + activeDirectoryEnabled);

		for (Object key : appDBInit.keySet()) {
			String prop_key = (String) key;
			String value = appDBInit.getProperty(prop_key);
			appdbProps.setProperty(prop_key, value);
		}
		return appdbProps;
	}

	@Bean
	public Properties resultDBConnectionProperties() {
		java.util.Properties resultdbProps = new java.util.Properties();

		// Fetch ResultDB properties from DB
		resultdbProps = databuckPropertyLoader.getPropertiesFromDB(DatabuckPropertyCategory.resultsdb.toString());

		for (Object key : resultDBInit.keySet()) {
			String prop_key = (String) key;
			String value = resultDBInit.getProperty(prop_key);
			resultdbProps.setProperty(prop_key, value);
		}
		return resultdbProps;
	}

	@Bean
	public Properties activeDirectoryConnectionProperties() {

		java.util.Properties activeDirProps = new java.util.Properties();

		if (activeDirectoryEnabled != null && activeDirectoryEnabled.trim().equalsIgnoreCase("Y")) {

			// Fetch Fetch DB properties from DB
			activeDirProps = databuckPropertyLoader
					.getPropertiesFromDB(DatabuckPropertyCategory.activedirectory.toString());

			// When azure secrets is enabled, read AD bind username and password from Key
			// Vault
			if (DatabuckPropertyInitializer.azureSecretsEnabled != null
					&& DatabuckPropertyInitializer.azureSecretsEnabled.trim().equalsIgnoreCase("Y")) {
				activeDirProps.put("Context.SECURITY_PRINCIPAL", DatabuckPropertyInitializer.azureSecretsADUser);
				activeDirProps.put("Context.SECURITY_CREDENTIALS", DatabuckPropertyInitializer.azureSecretsADPwd);
			}

			activeDirProps.setProperty("credentials", activeDirProps.getProperty("Context.SECURITY_CREDENTIALS"));
			activeDirProps.setProperty("context", activeDirProps.getProperty("Context.INITIAL_CONTEXT_FACTORY"));
			activeDirProps.setProperty("url", activeDirProps.getProperty("Context.PROVIDER_URL"));
			activeDirProps.setProperty("auth", activeDirProps.getProperty("Context.SECURITY_AUTHENTICATION"));
			activeDirProps.setProperty("principal", activeDirProps.getProperty("Context.SECURITY_PRINCIPAL"));
			activeDirProps.setProperty("searchBase", activeDirProps.getProperty("searchBase"));
			activeDirProps.setProperty("Domainforalluser", activeDirProps.getProperty("Domainforalluser"));
			activeDirProps.setProperty("Loginuid", activeDirProps.getProperty("Loginuid"));
			activeDirProps.setProperty("attributetofetch", activeDirProps.getProperty("attributetofetch"));
			activeDirProps.setProperty("defaultrole", activeDirProps.getProperty("defaultrole"));
			activeDirProps.setProperty("userObjectClass", activeDirProps.getProperty("userObjectClass"));
			activeDirProps.setProperty("domainPosition", activeDirProps.getProperty("domainPosition"));
			activeDirProps.setProperty("sorPosition", activeDirProps.getProperty("sorPosition"));
			activeDirProps.setProperty("rolePosition", activeDirProps.getProperty("rolePosition"));
			activeDirProps.setProperty("groupFilter", activeDirProps.getProperty("groupFilter"));

			// ldapProperties
			String sLdapAdmin = activeDirProps.getProperty("default_ldap_admin"),
					sLdapRole = activeDirProps.getProperty("default_ldap_role");
			sLdapAdmin = ((sLdapAdmin == null) || sLdapAdmin.isEmpty()) ? "" : sLdapAdmin.trim();
			sLdapRole = ((sLdapRole == null) || sLdapRole.isEmpty()) ? "" : sLdapRole.trim();
			activeDirProps.setProperty("default_ldap_admin", sLdapAdmin);
			activeDirProps.setProperty("default_ldap_role", sLdapRole);
		}
		return activeDirProps;
	}

	@Bean
	public Properties mongoDbProperties() {
		java.util.Properties mongoDbProps = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.mongodb.toString());
		return mongoDbProps;
	}

	@Bean
	public Properties clusterProperties() {
		Properties clusterProps = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.cluster.toString());
		app_mode = clusterProps.getProperty("app_mode");

		return clusterProps;
	}

	@Bean
	public Properties licenseProperties() {
		Properties propFile = databuckPropertyLoader.getPropertiesFromDB(DatabuckPropertyCategory.license.toString());
		return propFile;
	}

	@Bean
	public Properties dbDependencyProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.dbdependency.toString());
		return propFile;
	}

	@Bean
	public Properties integrationProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.integration.toString());
		return propFile;
	}
	
	@Bean
	public GenericFilterBean cacheHeaderFilter() {
	    final LinkedHashMap<String, CacheMode> cacheMap = new LinkedHashMap<>();
	    cacheMap.put("/", CacheHeaderFilter.CacheMode.FORCE_CHECK); // Static
	    cacheMap.put("/**/*.*", CacheHeaderFilter.CacheMode.FORCE_CHECK); // Static resources
	    cacheMap.put("/**", CacheHeaderFilter.CacheMode.NO_CACHE); // RESTful API call

	    return new CacheHeaderFilter(cacheMap);
	}

	@Bean
	public Properties cdpProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.cdp.toString());
		return propFile;
	}

	@Bean
	public Properties gcpProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.gcp.toString());
		return propFile;
	}

	@Bean
	public Properties azureProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.azure.toString());
		return propFile;
	}

	@Bean
	public Properties maprProperties() {
		Properties propFile = databuckPropertyLoader
				.getPropertiesFromDB(DatabuckPropertyCategory.mapr.toString());
		return propFile;
	}

}
