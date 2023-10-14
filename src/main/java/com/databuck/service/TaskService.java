package com.databuck.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.databuck.util.DatabuckUtility;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.AccessLog;
import com.databuck.bean.ListApplications;
import com.databuck.bean.UserToken;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;

@Service
public class TaskService {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private IUserDAO IUserdAO ;

	@Autowired
	private IValidationCheckDAO valcheckDAO;

	@Autowired
	private RuleCatalogService catalogService;
	
	private static final Logger LOG = Logger.getLogger(TaskService.class);

	public JSONObject getRolePermissionsByRoleId(long idRole) {
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			LOG.debug("idRole "+idRole);
			if (loginService.validateIdRole(idRole)) {
				JSONArray moduleArr= new JSONArray();
				SqlRowSet roleModuleTable = loginService.getIdTaskandAccessControlFromRoleModuleTable(idRole);

				while (roleModuleTable.next()) {
					long idTask = roleModuleTable.getLong("idTask");
					String moduleName = loginService.getTaskNameFromModuleTable(idTask);
					String accessControl = roleModuleTable.getString("accessControl");

					JSONObject module = new JSONObject();
					module.put("moduleName", moduleName);
					module.put("accessControl", accessControl);

					moduleArr.put(module);
				}
				if(moduleArr!=null && moduleArr.length() > 0){
					json.put("result",moduleArr);
					message = "success";
					status = "success";
				}
			} else {
				message = "Invalid idRole";
			}
		} catch (Exception e) {
			message = e.getMessage();
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		return json;

	}

	public JSONObject checkIfRuleCatalogApprovalRequired(long idApp) {

		String message = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();
		LOG.debug("idApp "+idApp);
		try {
			// validate idApp
			ListApplications listApp = valcheckDAO.getdatafromlistapplications(idApp);
			if (listApp != null) {

				// Check if RuleCatalog enabled and validation staging activated
				boolean rcenabled = catalogService.isRuleCatalogEnabled();
				boolean valstgactive = catalogService.isValidationStagingActivated(idApp);

				if (rcenabled == true && valstgactive == true) {

					// to check deltaType is new,missing and changed in rule catalog record list
					JSONObject jsonobj = catalogService.getRuleCatalogRecordList(idApp);
					JSONArray array = jsonobj.getJSONArray("DataSet");
					String deltype = "";

					for (int i = 0; i < array.length() - 1; i++) {
						LOG.debug("JsonArray:" + array.get(i));
						deltype = array.getJSONObject(i).getString("deltaType");

						if (deltype.trim().equalsIgnoreCase("NEW") || deltype.trim().equalsIgnoreCase("MISSING")
								|| deltype.trim().equalsIgnoreCase("CHANGED")) {
							status = "success";
							message = "Approval is Required";
							approvalRequired = "Y";
						} else {
							message = "Approval is not required";
							approvalRequired = "N";
						}
					}
				} else {
					message = "Rule catalog is not enabled or validation staging is not activated";
				}

			} else {
				message = "Invalid validation Id";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("exception "+e.getMessage());
			message = "Failed";
		}
		response.put("message", message);
		response.put("approvalRequired", approvalRequired);
		response.put("status", status);
		return response;
	}
	
	public boolean checkUserPermission(UserToken userToken, String permission, String accessControl) {
		SqlRowSet roleModuleTable = loginService.getIdTaskandAccessControlFromRoleModuleTable(userToken.getIdRole());
		long idTask = 0l;
		Map<String, String> module = new LinkedHashMap<String, String>();
		while (roleModuleTable.next()) {
			idTask = roleModuleTable.getLong("idTask");
			String taskName = loginService.getTaskNameFromModuleTable(idTask);
			module.put(taskName, roleModuleTable.getString("accessControl"));
		}
		return RBACController.rbac("Tasks", "R", module);

	}
	
	public List<AccessLog> getAllAccessLog() {

		List<AccessLog> list = new ArrayList<AccessLog>();
		SqlRowSet loggingActivityData = IUserdAO.getlogging_activity();
		try {
			while (loggingActivityData.next()) {

				String userName = loggingActivityData.getString("user_name");
				String activity = loggingActivityData.getString("databuck_feature");
				String activityLogtime = loggingActivityData.getString("activity_log_time");
				String applicationUrl = loggingActivityData.getString("access_url");

				AccessLog accesslog = new AccessLog(userName, activity, activityLogtime, applicationUrl);

				list.add(accesslog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("exception "+e.getMessage());
		}
		return list;
	}

	public int runAzureJob(String scriptLocation, long idApp){
		int pid=-1;

		try{
			ProcessBuilder processBuilder = new ProcessBuilder();
			List<String> commandList= new ArrayList<>();

			try (Stream<String> lines = Files.lines(Paths.get(scriptLocation))) {
				commandList = lines.collect(Collectors.toList());
			}

			try{
				for(String shellCommand:commandList){

					System.out.println(" shellCommand - " + shellCommand);

					if(shellCommand.startsWith("#")){

					}else if(shellCommand.contains("com.databuck.demo.Demo") || shellCommand.contains("com.databuck.template.TemplateApp")){
						shellCommand = shellCommand.replace("$1",""+idApp);
						processBuilder.command("bash", "-c", shellCommand);

						Process process = null;

						try {
							process = processBuilder.start();

							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

							String line;
							String jsonString="";

							while ((line = reader.readLine()) != null) {
								jsonString = jsonString +" "+ line;
							}

							int exitVal = process.waitFor();

							if (exitVal == 0) {
								System.out.println("Success!");
								if(!jsonString.trim().isEmpty()){
									JSONObject levyObj= new JSONObject(jsonString);
									pid = levyObj.getInt("id");
								}
							} else {
								// abnormal...
								System.out.println("Failure!");
								new Exception("Command Execution failed");
							}
						}catch (Exception e){
							e.printStackTrace();
						}
					}else{

						processBuilder.command("bash", "-c", shellCommand);

						Process process = null;

						try {
							process = processBuilder.start();

							int exitVal = process.waitFor();

							if (exitVal == 0) {
								System.out.println("Success!");
							} else {
								// abnormal...
								System.out.println("Failure!");
								new Exception("Command Execution failed");
							}
						}catch (Exception e){
							e.printStackTrace();
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return pid;
	}

	public boolean checkIfAzureJobRunning(long levyId){
		boolean azureJobStatus= false;
		try{
			String databuckHome= DatabuckUtility.getDatabuckHome();
			String scriptLocation = databuckHome + "/scripts/viewJobStatus_azure.sh";


			ProcessBuilder processBuilder = new ProcessBuilder();
			List<String> commandList= new ArrayList<>();

			try (Stream<String> lines = Files.lines(Paths.get(scriptLocation))) {
				commandList = lines.collect(Collectors.toList());
			}catch (Exception e){
				e.printStackTrace();
			}

			for(String shellCommand:commandList){

				if(shellCommand.startsWith("#")){

				}else if(shellCommand.contains("livy-id")){

					shellCommand = shellCommand.replace("$1",""+levyId);
					System.out.println(" shellCommand - " + shellCommand);

					processBuilder.command("bash", "-c", shellCommand);

					Process process = null;

					try {
						process = processBuilder.start();

						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

						String line;
						String jsonString="";

						while ((line = reader.readLine()) != null) {
							jsonString = jsonString +" "+ line;
						}

						int exitVal = process.waitFor();

						if (exitVal == 0) {
							System.out.println("Success!");
							if(!jsonString.trim().isEmpty()){
								JSONObject levyObj= new JSONObject(jsonString);
								String state = levyObj.getString("state");

								if(!state.trim().equalsIgnoreCase("killed")
										&& !state.trim().equalsIgnoreCase("dead")
										&& !state.trim().equalsIgnoreCase("success"))
									azureJobStatus = true;
							}
						} else {
							// abnormal...
							System.out.println("Failure!");
							new Exception("Command Execution failed");
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}else{

					processBuilder.command("bash", "-c", shellCommand);

					Process process = null;

					try {
						process = processBuilder.start();

						int exitVal = process.waitFor();

						if (exitVal != 0) {
							// abnormal...
							System.out.println("Failure!");
							new Exception("Command Execution failed");
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return azureJobStatus;
	}

}
