package com.databuck.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.controller.JSONController;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.JwfSpaInfra;
import org.apache.log4j.Logger;

@Service
public class AuthorizationService {
	@Autowired
	JSONController Jsoncontroller;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private Properties activeDirectoryConnectionProperties;

	@Autowired
	public LoginService loginService;
	
	private static final Logger LOG = Logger.getLogger(AuthorizationService.class);


	public boolean authenticateUser(String authorization) {
		boolean retValue = false;
		String serviceAuthenticationType = appDbConnectionProperties.getProperty("serviceAuthenticationType");
		if (serviceAuthenticationType == null || serviceAuthenticationType.trim().isEmpty()
				|| serviceAuthenticationType.equalsIgnoreCase("Token")) {
			retValue = authenticateUserSecureAPI(authorization);
		} else if (serviceAuthenticationType.equalsIgnoreCase("Ldap")) {
			retValue = authenticateUserLdap(authorization);
		}

		return retValue;

	}

	private boolean authenticateUserLdap(String authorization) {
		LOG.info(String.format("authenticateUserLdap 01 Start."));
		boolean isUserValid = false;
		HashMap<String, String> oProgramOutput = null;
		String databuckHome = DatabuckUtility.getDatabuckHome();
		String sCmdLine = databuckHome + "/scripts/ldap_login.sh";
		String domainname = "";
		String principal = activeDirectoryConnectionProperties.getProperty("principal");
		String Adminpasssword = activeDirectoryConnectionProperties.getProperty("credentials");
		String email = "", userPassword = "";
		ArrayList<String> alist = new ArrayList<String>();

		// Auth 1st phase start
		if (authorization != null && authorization.startsWith("Basic")) {

			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

			// credentials = username:password
			final String[] values = credentials.split(":", 2);
			email = values[0];
			userPassword = values[1];
		}
		
		LOG.debug("\n=====> Script command: " + databuckHome + "/scripts/ldap_login.sh " + email + " xxxxxxx xxxxxxx\n");
		String[] ldap_login_cmd_args  = {sCmdLine, email, Adminpasssword, principal};

		oProgramOutput = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_login_cmd_args, new String[] { "result: 0 Success" });
		/* LOG.debug("oProgramOutput : " + oProgramOutput); */
		String previousLine = null;
		if (oProgramOutput.get("Program Result").equalsIgnoreCase("1")) {
			String ProgramOutput = oProgramOutput.get("Program Std Out");
			String[] arrOfStr = ProgramOutput.split("\n");
			for (String a : arrOfStr) { // dn: //getting DN from cn
				if (previousLine != null) {
					// compare
					if (a.startsWith("memberOf:")) {

						String[] groupname = previousLine.split(":");
						domainname = groupname[1];
						
						break;
					} else {
						previousLine = previousLine.trim() + "" + a.trim();
						String[] groupname = previousLine.split(":");
						domainname = groupname[1];
					
						break;
					}
				}
				if (a.startsWith("dn:")) {
					previousLine = a;
				}

			}
		}

		// Auth 2nd phase start
		if (domainname.equalsIgnoreCase("")) {
			LOG.debug(String.format("authenticateUserLdap 02 Invalid cn entered (DN not found): %1$s", email));
		} else {
			String ProgramOutput = oProgramOutput.get("Program Std Out");
			alist = loginService.getgroupfrom_Program_Std_Out(ProgramOutput);
			String serviceLdapGroup = appDbConnectionProperties.getProperty("serviceLdapGroup");

			if (serviceLdapGroup != null && serviceLdapGroup.length() > 0) {
				boolean isServiceLdapGroup = false;
				for (String group : alist) {
					if (serviceLdapGroup.equalsIgnoreCase(group)) {
						isServiceLdapGroup = true;
					}
				}
				try {
					if (isServiceLdapGroup) {
						if (testBind(domainname, userPassword)) {
							LOG.debug(String.format(
									"authenticateUserLdap 03 User authentication succeeded dn:%1$s", domainname));
							isUserValid = true;
						} else {
							LOG.error(String.format(
									"authenticateUserLdap 04 User authentication failed, dn & password are not binded."));
							isUserValid = false;
						}
					} else {
						isUserValid = false;
						LOG.error(String.format(
								"authenticateUserLdap 05 User authentication failed, User does not belong to serviceLdapGroup."));
					}
				} catch (Exception e) {
					LOG.error(String.format("authenticateUserLdap 06 Exception ocuurred."));
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			} else {
				LOG.info(String.format("authenticateUserLdap 07 serviceLdapGroup is null."));
			}
		}
		LOG.info(String.format("authenticateUserLdap 08 End."));
		return isUserValid;
	}

	private boolean testBind(String dn, String password) throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();

		Properties propFile = activeDirectoryConnectionProperties;

		env.put(Context.SECURITY_AUTHENTICATION, propFile.getProperty("auth"));
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.INITIAL_CONTEXT_FACTORY, propFile.getProperty("context"));
		env.put(Context.PROVIDER_URL, propFile.getProperty("url"));
		
		try {
			DirContext ctx = new InitialDirContext(env);
		} catch (javax.naming.AuthenticationException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return true;
	}

	private boolean authenticateUserSecureAPI(String authorization) {
		LOG.info(String.format("authenticateUserSecureAPI 01 Start."));
		boolean isUserValid = false;
		try {
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();

			if (authorization != null && authorization.startsWith("Basic")) {

				// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				String credentials = new String(Base64.getDecoder().decode(base64Credentials),
						Charset.forName("UTF-8"));

				// credentials = username:password
				final String[] values = credentials.split(":", 2);

				if (values[0].equals(secureAPIMap.get("accessTokenId"))
						&& values[1].equals(secureAPIMap.get("secretAccessToken"))) {

					isUserValid = true;
				}
			} else {
				LOG.error("\n====>Authentication failed, Blank or Invalid Authorization!!");
			}
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred, failed to authenticate!!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info(String.format("authenticateUserSecureAPI 02 End."));
		return isUserValid;
	}
}
