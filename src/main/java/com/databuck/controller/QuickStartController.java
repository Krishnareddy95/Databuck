package com.databuck.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.QuickStartTaskTracker;
import com.databuck.service.DataConnectionService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.databuck.bean.BucketInfo;
import com.databuck.bean.BucketInfoOne;
import com.databuck.bean.GloabalRule;
import com.databuck.bean.ListApplications;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.ITaskService;
import com.databuck.service.RBACController;

@Controller
public class QuickStartController {

	@Autowired
	DataProfilingTemplateService dataProilingTemplateService;

	@Autowired
	public ITaskService iTaskService;

	@Autowired
	private ITemplateViewDAO templateViewDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDao;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private SchemaDAOI schemaDAOI;

	@Autowired
	private BigQueryConnection bigQueryConnection;

	@Autowired
	private DataConnectionService dataConnectionService;

	@RequestMapping(value = "/getQuickStartHome")
	public ModelAndView getQuickStartHome(HttpServletRequest request, HttpSession session) throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = RBACController.rbac("QuickStart", "C", session);
		if (rbac) {
			request.setAttribute("currentSection", "QuickStart");
			request.setAttribute("currentLink", "quickStartHome");
			return new ModelAndView("quickStartHome");
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getAWSQuickStartHome")
	public ModelAndView getAWSQuickStartHome(HttpServletRequest request, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = RBACController.rbac("QuickStart", "C", session);
		if (rbac) {
			request.setAttribute("currentSection", "QuickStart");
			request.setAttribute("currentLink", "awsQuickStart");
			return new ModelAndView("awsQuickStart");
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getGCPQuickStartHome")
	public ModelAndView getGCPQuickStartHome(HttpServletRequest request, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = RBACController.rbac("QuickStart", "C", session);
		if (rbac) {
			request.setAttribute("currentSection", "QuickStart");
			request.setAttribute("currentLink", "gcpQuickStart");
			return new ModelAndView("gcpQuickStart");
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getAppDetailsByTemplate", method = RequestMethod.POST)
	public void getAppDetailsByTemplate(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam Long idData, @RequestParam String templateName) throws Exception {
		long appId = 0l;
		String appName = "";
		String validationName = templateName + "_Validation";

		List<ListApplications> listApplications = templateViewDAO.getValidationCheckOfTemplateById(idData,
				validationName);

		if (listApplications != null && listApplications.size() > 0) {
			appId = listApplications.get(0).getIdApp();
			appName = listApplications.get(0).getName();
		}
		JSONObject json = new JSONObject();
		json.put("success", "success");
		json.put("appId", appId);
		json.put("appName", appName);
		json.put("validationRunType", DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD);
		response.getWriter().println(json);
		System.out.println("\n====>response json: " + json);
		response.getWriter().flush();
	}

	@RequestMapping(value = "/createDataMatchingValidtion", method = RequestMethod.POST)
	public void createDataMatchingValidtion(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam Long srcTemplateId, @RequestParam String srcTemplateName, @RequestParam Long trgtTemplateId,
			@RequestParam String trgtTemplateName, @RequestParam String dmValidationName) throws Exception {

		System.out.println("\n==========> createDataMatchingValidtion <==========");
		long appId = 0l;
		try {
			String validationName = srcTemplateName + "_Validation";

			Long projectId = (Long) session.getAttribute("projectId");
			Integer domainId = (Integer) session.getAttribute("domainId");

			List<ListApplications> listApplications = templateViewDAO.getValidationCheckOfTemplateById(srcTemplateId,
					validationName);

			if (listApplications != null && listApplications.size() > 0) {
				appId = listApplications.get(0).getIdApp();
			}

			// delete the extra validation of source
			validationCheckDao.deletefromlistapplications(appId, null);

			validationName = trgtTemplateName + "_Validation";

			listApplications = templateViewDAO.getValidationCheckOfTemplateById(trgtTemplateId, validationName);

			if (listApplications != null && listApplications.size() > 0) {
				appId = listApplications.get(0).getIdApp();
			}

			// delete the extra validation of target
			validationCheckDao.deletefromlistapplications(appId, null);

			long idUser = (Long) session.getAttribute("idUser");
			String createdByUser = userDAO.getUserNameByUserId(idUser);

			// Create DataMatching validation
			long idApp = validationCheckDao.insertintolistapplications(dmValidationName, "", "Data Matching",
					srcTemplateId, idUser, 1.0, "N", "", "", "", "N", projectId, createdByUser, domainId);

			validationCheckDao.updateApplicationNameWithIdApp(validationName, idApp);

			validationCheckDao.updateDataIntoListApplications(idApp, trgtTemplateId, 1.0, "N", "N", "", "", "", "Y",
					"Y", "N", 1.0);

			validationCheckDao.deleteEntryFromListDMRules(idApp, "Measurements Match");

			Long idDM = validationCheckDao.getIddmFromListDMRules(idApp, "Key Fields Match");
			if (idDM == 0) {
				idDM = validationCheckDao.insertIntoListDMRules(idApp, "Key Fields Match", "One to One");
				System.out.println("idDM=" + idDM);
			}

			List<String> rightSourceColumnNames = validationCheckDao
					.getDisplayNamesFromListDataDefinition(srcTemplateId);
			List<String> LeftSourceColumnNames = validationCheckDao
					.getDisplayNamesFromListDataDefinition(trgtTemplateId);
			validationCheckDao.updateMatchingRuleAutomaticIntoListDMCriteria(rightSourceColumnNames,
					LeftSourceColumnNames, idDM, idApp);

			JSONObject json = new JSONObject();
			if (idApp != 0l) {
				json.put("status", "success");
				json.put("appId", idApp);
				json.put("appName", dmValidationName);
				json.put("validationRunType", DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD);
			} else {
				json.put("status", "failed");
			}
			System.out.println("\n====>response json: " + json);

			response.getWriter().println(json);
			response.getWriter().flush();

		} catch (Exception e) {
			JSONObject json = new JSONObject();
			json.put("status", "failed");
			response.getWriter().println(json);
			System.out.println("\n====>response json: " + json);
			response.getWriter().flush();
		}
	}

	private List<GloabalRule> getSelectedRuleDetails(HttpSession session) {
		List<GloabalRule> selected_list = new ArrayList<GloabalRule>();

		List<String> selected_rule_ids = (List<String>) session.getAttribute("selected_rule_ids");
		System.out.println(selected_rule_ids);

		if (selected_rule_ids != null) {

			for (int i = 0; i < selected_rule_ids.size(); i++) {
				List<GloabalRule> selected_rules_details = (List<GloabalRule>) session
						.getAttribute("rulesForSelection");
				System.out.println("selected_rules_details +++++++++" + selected_rules_details);
				// List<ListColRules2>
				// selected_rules_details=getMainRuleExpression(Integer.parseInt(selected_rule_ids.get(i)));
				for (int z1 = 0; z1 < selected_rules_details.size(); z1++) {
					System.out.println(selected_rule_ids.get(i));
					System.out.println(selected_rules_details.get(z1).getGloabal_rule_id());
					if (Integer.parseInt(
							selected_rule_ids.get(i)) == (selected_rules_details.get(z1).getGloabal_rule_id())) {
						System.out.println("ruleid  is ::" + selected_rule_ids.get(i) + "rule name::"
								+ selected_rules_details.get(z1).getRule_name() + " rule expression:: "
								+ selected_rules_details.get(z1).getRule_expression() + " rule type:: "
								+ selected_rules_details.get(z1).getRule_Type());
						// System.out.println("modified rule expression "+
						// eligible_rules_formula.get(z1).getExpression().replaceAll(regex,
						// replacement));
						GloabalRule g = new GloabalRule();
						g.setGloabal_rule_id(Integer.parseInt(selected_rule_ids.get(i)));
						g.setRule_name(selected_rules_details.get(z1).getRule_name());
						g.setRule_expression(selected_rules_details.get(z1).getRule_expression());
						g.setRule_Type(selected_rules_details.get(z1).getRule_Type());
						selected_list.add(g);
					} else {
						System.out.println("No rules");
					}

				}
			}
		}

		System.out.println("selected rules and details " + selected_list);
		return selected_list;
	}

	@RequestMapping(value = "/processDQQuickStart", method = RequestMethod.POST)
	public ModelAndView processLocalFSQuickStart(ModelAndView model, @RequestParam("dataupload") MultipartFile file,
			HttpSession session) throws Exception {

		System.out.println("\n==========> processDQQuickStart <==========");

		String filename = file.getOriginalFilename();

		String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart";

		// Create Path if not exists
		File hostUri_folder = new File(hostUri);
		if(!hostUri_folder.exists() || !hostUri_folder.isDirectory()) {
			hostUri_folder.mkdir();
		}
		
		String path = hostUri + "/" + filename;
		String fileDownloadStatus = "failed";
		String fileDownloadMessage = "";
		long templateId = 0l;
		String templateUniqueId = "";
		String dataTemplateName = "QuickStart_" + filename;

		Long projectId = (Long) session.getAttribute("projectId");
		List<GloabalRule> selected_list = getSelectedRuleDetails(session);

		if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

			// Download file to DATABUCK_HOME directory
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path)));) {
				byte[] bytes = file.getBytes();
				stream.write(bytes);
				fileDownloadStatus = "success";
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
				e.printStackTrace();
			}

			int count = 0;
			if(fileDownloadStatus.equalsIgnoreCase("success")){
				try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)));) {
					while (reader.readLine()!=null){
						count++;
						if(count>2)
							break;
					}
				} catch (Exception e) {
					System.out.println("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
					e.printStackTrace();
				}
			}

			// When file download is success create template
			if (fileDownloadStatus.equalsIgnoreCase("success") && count>=2) {

				// Getting createdByUser
				long idUser = (Long) session.getAttribute("idUser");
				String createdByUser = userDAO.getUserNameByUserId(idUser);
				System.out.println("=== Created by in createTemplateForQuickStart ==>" + createdByUser);

				// Create a template
				CompletableFuture<Long> result = dataProilingTemplateService.createDataTemplate(session, 0l,
						"File System", filename, dataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "", "",
						idUser, hostUri, filename, "csv", "", "", "", file, "", "", "Y", selected_list, projectId, "Y",
						createdByUser, "N", null, "", null, null);
				templateId = result.get();
				templateUniqueId = dataProilingTemplateService.triggerDataTemplate(templateId, "File System", "Y", "Y")
						.get();

			} else if(count == 1){
				fileDownloadMessage = "Unable to process this file!! It only contains header!!";
				fileDownloadStatus = "failed";
			} else {
				System.out.println("\n====>Failed to download the file to DATABUCK_HOME!!");
				fileDownloadMessage = "Exception occurred while saving file to DATABUCK_HOME !!";
			}
		} else {
			System.out.println("\n====>Unable to process this file!! Currently this feature supports only csv files!!");
			fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
		}

		model.setViewName("quickStartDQProcessPage");
		model.addObject("currentSection", "QuickStart");
		model.addObject("currentLink", "quickStartHome");
		model.addObject("qs_title", filename);
		model.addObject("templateId", templateId);
		model.addObject("templateName", dataTemplateName);
		model.addObject("templateUniqueId", templateUniqueId);
		model.addObject("file_path", path);
		model.addObject("fileDownloadStatus", fileDownloadStatus);
		model.addObject("fileDownloadMessage", fileDownloadMessage);
		model.addObject("templateDataLocation", "File System");
		model.addObject("templateDescription", "");
		return model;
	}

	@RequestMapping(value = "/processDQForSampleFile", method = RequestMethod.GET)
	public ModelAndView processDQForSampleFile(ModelAndView model, @RequestParam String filename, HttpSession session)
			throws Exception {

		System.out.println("\n==========> processDQForSampleFile <==========");

		String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart/samples";

		String path = hostUri + "/" + filename;
		System.out.println("Path:" + path);

		long templateId = 0l;
		String templateUniqueId = "";
		String fileDownloadStatus = "failed";
		String fileDownloadMessage = "";
		String dataTemplateName = "QuickStart_" + filename;

		Long projectId = (Long) session.getAttribute("projectId");
		List<GloabalRule> selected_list = getSelectedRuleDetails(session);

		File sampleFile = new File(path);
		MultipartFile multipartFile = null;

		// Check if the file extension is 'CSV'
		if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

			if (sampleFile != null && sampleFile.exists() && sampleFile.isFile()) {
				try {
					DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, sampleFile.getName(),
							(int) sampleFile.length(), sampleFile.getParentFile());
					fileItem.getOutputStream();
					multipartFile = new CommonsMultipartFile(fileItem);
					fileDownloadStatus = "success";
				} catch (Exception e) {
					System.out.println("\n====>Exception occurred while reading file!!");
					e.printStackTrace();
				}

			}

			// When file download is success create template
			if (fileDownloadStatus.equalsIgnoreCase("success")) {

				// getting createdByUser
				long idUser = (Long) session.getAttribute("idUser");
				String createdByUser = userDAO.getUserNameByUserId(idUser);
				System.out.println("=== Created by in createTemplateForQuickStart ==>" + createdByUser);

				// Create a template
				CompletableFuture<Long> result = dataProilingTemplateService.createDataTemplate(session, 0l,
						"File System", filename, dataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "", "",
						idUser, hostUri, filename, "csv", "", "", "", multipartFile, "", "", "Y", selected_list,
						projectId, "Y", createdByUser, "N", null, "", null, null);
				templateId = result.get();
				templateUniqueId = dataProilingTemplateService.triggerDataTemplate(templateId, "File System", "Y", "Y")
						.get();
			} else {
				System.out.println("\n====>File doesn't exist in DATABUCK_HOME!!");
				fileDownloadMessage = "File doesn't exist!!";
			}
		} else {
			System.out.println("\n====>Unable to process this file!! Currently this feature supports only csv files!!");
			fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
		}

		model.setViewName("quickStartDQProcessPage");
		model.addObject("currentSection", "QuickStart");
		model.addObject("currentLink", "quickStartHome");
		model.addObject("qs_title", filename);
		model.addObject("templateId", templateId);
		model.addObject("templateName", dataTemplateName);
		model.addObject("templateUniqueId", templateUniqueId);
		model.addObject("file_path", path);
		model.addObject("fileDownloadStatus", fileDownloadStatus);
		model.addObject("fileDownloadMessage", fileDownloadMessage);
		model.addObject("templateDataLocation", "File System");
		model.addObject("templateDescription", "");
		return model;
	}

	@RequestMapping(value = "/processDMForSampleFile", method = RequestMethod.GET)
	public ModelAndView processDMForSampleFile(ModelAndView model, @RequestParam String srcFileName,
			@RequestParam String trgtFileName, HttpSession session) throws Exception {

		System.out.println("\n==========> processDMForSampleFile <==========");

		String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart/samples";

		String validationName = "DataMatching_" + srcFileName.split("\\.")[0] + "_" + trgtFileName.split("\\.")[0];
		// Getting createdByUser
		long idUser = (Long) session.getAttribute("idUser");
		String createdByUser = userDAO.getUserNameByUserId(idUser);
		System.out.println("=== Created by in createTemplateForQuickStart ==>" + createdByUser);

		long srcTemplateId = 0l;
		String srcTemplateUniqueId = "";
		String srcFileDownloadMessage = "";
		String srcFileDownloadStatus = "failed";
		String srcDataTemplateName = "QuickStart_" + srcFileName;

		long trgtTemplateId = 0l;
		String trgtTemplateUniqueId = "";
		String trgtFileDownloadMessage = "";
		String trgtFileDownloadStatus = "failed";
		String trgtDataTemplateName = "QuickStart_" + trgtFileName;

		// Get full path of source file
		String srcPath = hostUri + "/" + srcFileName;
		System.out.println("srcPath:" + srcPath);

		Long projectId = (Long) session.getAttribute("projectId");
		List<GloabalRule> selected_list = getSelectedRuleDetails(session);

		// Read source file
		File sampleSrcFile = new File(srcPath);
		MultipartFile srcMultipartFile = null;

		// Check if the file extension is csv or not
		if (srcFileName.split("\\.")[1].equalsIgnoreCase("csv")) {

			// Check if the file exists or not
			if (sampleSrcFile != null && sampleSrcFile.exists() && sampleSrcFile.isFile()) {
				try {
					DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, sampleSrcFile.getName(),
							(int) sampleSrcFile.length(), sampleSrcFile.getParentFile());
					fileItem.getOutputStream();
					srcMultipartFile = new CommonsMultipartFile(fileItem);
					srcFileDownloadStatus = "success";
				} catch (Exception e) {
					System.out.println("\n====>Exception occurred while reading file!!");
					e.printStackTrace();
				}
			}

			if (!srcFileDownloadStatus.equalsIgnoreCase("success")) {
				System.out.println("\n====>Source File doesn't exist in DATABUCK_HOME!!");
				srcFileDownloadMessage = "File doesn't exist!!";
			}

		} else {
			System.out.println("\n====>Unable to process this file!! Currently this feature supports only csv files!!");
			srcFileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
		}

		// Get full path of target file
		String trgtPath = hostUri + "/" + trgtFileName;
		System.out.println("trgtPath:" + trgtPath);

		// Read target file
		File sampleTrgtFile = new File(trgtPath);
		MultipartFile trgtMultipartFile = null;

		// Check if the file extension is csv or not
		if (trgtFileName.split("\\.")[1].equalsIgnoreCase("csv")) {

			// Check if the file exists or not
			if (sampleTrgtFile != null && sampleTrgtFile.exists() && sampleTrgtFile.isFile()) {
				try {
					DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, sampleTrgtFile.getName(),
							(int) sampleTrgtFile.length(), sampleTrgtFile.getParentFile());
					fileItem.getOutputStream();
					trgtMultipartFile = new CommonsMultipartFile(fileItem);
					trgtFileDownloadStatus = "success";
				} catch (Exception e) {
					System.out.println("\n====>Exception occurred while reading file!!");
					e.printStackTrace();
				}
			}

			if (!trgtFileDownloadStatus.equalsIgnoreCase("success")) {
				System.out.println("\n====>Target File doesn't exist in DATABUCK_HOME!!");
				trgtFileDownloadMessage = "File doesn't exist!!";
			}

		} else {
			System.out.println("\n====>Unable to process this file!! Currently this feature supports only csv files!!");
			trgtFileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
		}

		// Create Source template
		if (srcFileDownloadStatus.equalsIgnoreCase("success") && trgtFileDownloadStatus.equalsIgnoreCase("success")) {

			CompletableFuture<Long> srcResult = dataProilingTemplateService.createDataTemplate(session, 0l,
					"File System", srcFileName, srcDataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "", "",
					idUser, hostUri, srcFileName, "csv", "", "", "", srcMultipartFile, "", "", "Y", selected_list,
					projectId, "Y", createdByUser, "N", null, "", null, null);
			srcTemplateId = srcResult.get();
			srcTemplateUniqueId = dataProilingTemplateService
					.triggerDataTemplate(srcTemplateId, "File System", "Y", "Y").get();

			// Create Target template
			CompletableFuture<Long> trgtResult = dataProilingTemplateService.createDataTemplate(session, 0l,
					"File System", trgtFileName, trgtDataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "",
					"", idUser, hostUri, trgtFileName, "csv", "", "", "", trgtMultipartFile, "", "", "Y", selected_list,
					projectId, "Y", createdByUser, "N", null, "", null, null);
			trgtTemplateId = trgtResult.get();
			trgtTemplateUniqueId = dataProilingTemplateService
					.triggerDataTemplate(trgtTemplateId, "File System", "Y", "Y").get();

		}

		model.setViewName("quickStartDMProcessPage");
		model.addObject("currentSection", "QuickStart");
		model.addObject("currentLink", "quickStartHome");
		model.addObject("qs_title", "DataMatching(" + srcFileName + " - " + trgtFileName + ")");
		model.addObject("srcTemplateId", srcTemplateId);
		model.addObject("srcTemplateName", srcDataTemplateName);
		model.addObject("srcTemplateUniqueId", srcTemplateUniqueId);
		model.addObject("src_file_path", srcPath);
		model.addObject("srcFileDownloadStatus", srcFileDownloadStatus);
		model.addObject("srcFileDownloadMessage", srcFileDownloadMessage);
		model.addObject("srcTemplateDataLocation", "File System");
		model.addObject("srcTemplateDescription", "");
		model.addObject("trgtTemplateId", trgtTemplateId);
		model.addObject("trgtTemplateName", trgtDataTemplateName);
		model.addObject("trgtTemplateUniqueId", trgtTemplateUniqueId);
		model.addObject("trgt_file_path", trgtPath);
		model.addObject("trgtFileDownloadStatus", trgtFileDownloadStatus);
		model.addObject("trgtFileDownloadMessage", trgtFileDownloadMessage);
		model.addObject("trgtTemplateDataLocation", "File System");
		model.addObject("validationName", validationName);

		System.out.println("srcFileDownloadStatus:" + srcFileDownloadStatus);
		System.out.println("srcFileDownloadMessage:" + srcFileDownloadMessage);

		System.out.println("trgtFileDownloadStatus:" + trgtFileDownloadStatus);
		System.out.println("trgtFileDownloadMessage:" + trgtFileDownloadMessage);
		System.out.println("validationName:" + validationName);
		return model;
	}

	@RequestMapping(value = "/processDMQuickStart", method = RequestMethod.POST)
	public ModelAndView processDMQuickStart(ModelAndView model, @RequestParam("srcDataUpload") MultipartFile srcFile,
			@RequestParam("targetDataUpload") MultipartFile trgtFile, HttpSession session) throws Exception {

		System.out.println("\n==========> processDMQuickStart <==========");

		String srcFileName = srcFile.getOriginalFilename();
		String trgtFileName = trgtFile.getOriginalFilename();
		String validationName = "DataMatching_" + srcFileName.split("\\.")[0] + "_" + trgtFileName.split("\\.")[0];

		// Getting createdByUser
		long idUser = (Long) session.getAttribute("idUser");
		String createdByUser = userDAO.getUserNameByUserId(idUser);
		System.out.println("=== Created by in createTemplateForQuickStart ==>" + createdByUser);

		long srcTemplateId = 0l;
		String srcTemplateUniqueId = "";
		String srcFileDownloadMessage = "";
		String srcFileDownloadStatus = "failed";
		String srcDataTemplateName = "QuickStart_" + srcFileName;

		long trgtTemplateId = 0l;
		String trgtTemplateUniqueId = "";
		String trgtFileDownloadMessage = "";
		String trgtFileDownloadStatus = "failed";
		String trgtDataTemplateName = "QuickStart_" + trgtFileName;

		Long projectId = (Long) session.getAttribute("projectId");
		List<GloabalRule> selected_list = getSelectedRuleDetails(session);

		String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart";

		// Create Path if not exists
		File hostUri_folder = new File(hostUri);
		if(!hostUri_folder.exists() || !hostUri_folder.isDirectory()) {
			hostUri_folder.mkdir();
		}
				
		// Get full path of source file
		String srcPath = hostUri + "/" + srcFileName;
		System.out.println("srcPath:" + srcPath);

		// Check if the file extension is csv or not
		if (srcFileName.split("\\.")[1].equalsIgnoreCase("csv")) {

			// Download file to DATABUCK_HOME directory
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(srcPath)));) {
				byte[] bytes = srcFile.getBytes();
				stream.write(bytes);
				srcFileDownloadStatus = "success";
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred while saving source file to DATABUCK_HOME !!");
				srcFileDownloadStatus = "Exception occurred while saving source file to DATABUCK_HOME !!";
				e.printStackTrace();
			}

		} else {
			System.out
					.println("\n====>Unable to process source file!! Currently this feature supports only csv files!!");
			srcFileDownloadMessage = "Unable to process source file!! Currently this feature supports only csv files!!";
		}

		// Get full path of target file
		String trgtPath = hostUri + "/" + trgtFileName;
		System.out.println("trgtPath:" + trgtPath);

		// Check if the file extension is csv or not
		if (trgtFileName.split("\\.")[1].equalsIgnoreCase("csv")) {

			// Download file to DATABUCK_HOME directory
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(trgtPath)));) {
				byte[] bytes = trgtFile.getBytes();
				stream.write(bytes);
				trgtFileDownloadStatus = "success";
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred while saving target file to DATABUCK_HOME !!");
				trgtFileDownloadMessage = "Exception occurred while saving target file to DATABUCK_HOME !!";
				e.printStackTrace();
			}

		} else {
			System.out
					.println("\n====>Unable to process target file!! Currently this feature supports only csv files!!");
			trgtFileDownloadMessage = "Unable to process target file!! Currently this feature supports only csv files!!";
		}

		// Create Source template
		if (srcFileDownloadStatus.equalsIgnoreCase("success") && trgtFileDownloadStatus.equalsIgnoreCase("success")) {

			CompletableFuture<Long> srcResult = dataProilingTemplateService.createDataTemplate(session, 0l,
					"File System", srcFileName, srcDataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "", "",
					idUser, hostUri, srcFileName, "csv", "", "", "", srcFile, "", "", "Y", selected_list, projectId,
					"Y", createdByUser, "N", null, "", null, null);
			srcTemplateId = srcResult.get();
			srcTemplateUniqueId = dataProilingTemplateService
					.triggerDataTemplate(srcTemplateId, "File System", "Y", "Y").get();

			// Create Target template
			CompletableFuture<Long> trgtResult = dataProilingTemplateService.createDataTemplate(session, 0l,
					"File System", trgtFileName, trgtDataTemplateName, "", "", "Y", "", "", "N", "", null, "", "", "",
					"", idUser, hostUri, trgtFileName, "csv", "", "", "", trgtFile, "", "", "Y", selected_list,
					projectId, "Y", createdByUser, "N", null, "", null, null);
			trgtTemplateId = trgtResult.get();
			trgtTemplateUniqueId = dataProilingTemplateService
					.triggerDataTemplate(trgtTemplateId, "File System", "Y", "Y").get();

		}

		model.setViewName("quickStartDMProcessPage");
		model.addObject("currentSection", "QuickStart");
		model.addObject("currentLink", "quickStartHome");
		model.addObject("qs_title", "DataMatching(" + srcFileName + " - " + trgtFileName + ")");
		model.addObject("srcTemplateId", srcTemplateId);
		model.addObject("srcTemplateName", srcDataTemplateName);
		model.addObject("srcTemplateUniqueId", srcTemplateUniqueId);
		model.addObject("src_file_path", srcPath);
		model.addObject("srcFileDownloadStatus", srcFileDownloadStatus);
		model.addObject("srcFileDownloadMessage", srcFileDownloadMessage);
		model.addObject("srcTemplateDataLocation", "File System");
		model.addObject("srcTemplateDescription", "");
		model.addObject("trgtTemplateId", trgtTemplateId);
		model.addObject("trgtTemplateName", trgtDataTemplateName);
		model.addObject("trgtTemplateUniqueId", trgtTemplateUniqueId);
		model.addObject("trgt_file_path", trgtPath);
		model.addObject("trgtFileDownloadStatus", trgtFileDownloadStatus);
		model.addObject("trgtFileDownloadMessage", trgtFileDownloadMessage);
		model.addObject("trgtTemplateDataLocation", "File System");
		model.addObject("validationName", validationName);

		System.out.println("srcFileDownloadStatus:" + srcFileDownloadStatus);
		System.out.println("srcFileDownloadMessage:" + srcFileDownloadMessage);

		System.out.println("trgtFileDownloadStatus:" + trgtFileDownloadStatus);
		System.out.println("trgtFileDownloadMessage:" + trgtFileDownloadMessage);
		System.out.println("validationName:" + validationName);
		return model;
	}

	@RequestMapping(value = "/loadS3Buckets", method = RequestMethod.POST)
	public ModelAndView loadS3Buckets(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String schemaName, @RequestParam String schemaType, @RequestParam String accessKey,
			@RequestParam String secretKey, @RequestParam String fileDataFormat, @RequestParam String bucketNamePattern,
			@RequestParam String fileNamePattern, @RequestParam String headerFilePath,
			@RequestParam String headerFileNamePattern, @RequestParam String headerFileDataFormat,
			@RequestParam String headerPresent, ModelAndView modelAndView) {

		Object user = session.getAttribute("user");
		session.setAttribute("licenseExpired", "false");
		modelAndView.addObject("currentSection", "QuickStart");
		modelAndView.addObject("currentLink", "awsQuickStart");
		System.out.println("user : " + user);

		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\n====> S3 Batch details <====");

		System.out.println("\n** schemaName: " + schemaName);
		System.out.println("\n** schemaType: " + schemaType);
		//System.out.println("\n** accessKey: " + accessKey);
		//System.out.println("\n** secretKey: " + secretKey);
		System.out.println("\n** fileDataFormat: " + fileDataFormat);
		System.out.println("\n** bucketNamePattern: " + bucketNamePattern);
		System.out.println("\n** fileNamePattern: " + fileNamePattern);
		System.out.println("\n** Header Present: " + headerPresent);
		System.out.println("\n** Header File Path: " + headerFilePath);
		System.out.println("\n** Header FileName Pattern: " + headerFileNamePattern);
		System.out.println("\n** Header File Data Pattern: " + headerFileDataFormat);

		try {

			AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
			List<Bucket> buckets = s3Client.listBuckets();
			System.out.println("\n** Available Number Of Buckets: " + buckets.size());

			List<String> bucketNames = new ArrayList<>();
			for (Bucket bucket : buckets)
				bucketNames.add(bucket.getName());

			Pattern pattern = Pattern.compile(bucketNamePattern);

			List<String> matchingBucketNames = bucketNames.stream().filter(pattern.asPredicate())
					.collect(Collectors.toList());

			List<BucketInfo> listBuckets = new ArrayList<BucketInfo>();
			int intrid = 0;
			for (Bucket bucket : buckets) {

				String bucketName = bucket.getName();
				if (!matchingBucketNames.contains(bucketName))
					continue;
				BucketInfo bucketInfo = new BucketInfo();
				
				// TODO: Check if bucket has files with that file pattern
				bucketInfo.setCheck(false);
				bucketInfo.setName(bucketName);
				bucketInfo.setCreationDate(bucket.getCreationDate());
				bucketInfo.setSingle(false);
				bucketInfo.setItrid(intrid);
				listBuckets.add(bucketInfo);
				intrid++;

			}
			session.setAttribute("S3schemaName", schemaName);
			session.setAttribute("S3schemaType", schemaType);
			session.setAttribute("S3accessKey", accessKey);
			session.setAttribute("S3secretKey", secretKey);
			session.setAttribute("S3fileDataFormat", fileDataFormat);
			session.setAttribute("S3bucketNamePattern", bucketNamePattern);
			session.setAttribute("S3fileNamePattern", fileNamePattern);
			session.setAttribute("S3headerPresent", headerPresent);
			session.setAttribute("S3headerFilePath", headerFilePath);
			session.setAttribute("S3headerFileNamePattern", headerFileNamePattern);
			session.setAttribute("S3headerFileDataFormat", headerFileDataFormat);

			modelAndView.addObject("buckets", listBuckets);
			modelAndView.setViewName("dispBuckets");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/loadBigQueryTables", method = RequestMethod.POST)
	public ModelAndView loadBigQueryTables(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			ModelAndView modelAndView) {

		Object user = session.getAttribute("user");
		System.out.println("user : " + user);

		String uschemaName = req.getParameter("schemaName");
		session.setAttribute("soschemaName", uschemaName);

		String uschemaType = req.getParameter("schemaType");
		session.setAttribute("souschemaType", uschemaType);

		String uprojectName = req.getParameter("projectName");
		session.setAttribute("souprojectName", uprojectName);

		String uprivateKeyId = req.getParameter("privateKeyId");
		session.setAttribute("souprivateKeyId", uprivateKeyId);

		String uprivateKey = req.getParameter("privateKey");
		session.setAttribute("souprivateKey", uprivateKey);

		String uclientId = req.getParameter("clientId");
		session.setAttribute("souclientId", uclientId);

		String uclientEmail = req.getParameter("clientEmail");
		session.setAttribute("souclientEmail", uclientEmail);

		String udatabase = req.getParameter("database");
		session.setAttribute("soudatabase", udatabase);

		String stype = "service_account";
		session.setAttribute("sostype", stype);

		String sauth_uri = "https://accounts.google.com/o/oauth2/auth";
		session.setAttribute("soauth_uri", sauth_uri);

		String utoken_uri = "https://oauth2.googleapis.com/token";
		session.setAttribute("sotoken_uri", utoken_uri);

		String uauth_provider_x509_cert_url = "https://www.googleapis.com/oauth2/v1/certs";
		session.setAttribute("soauth_provider_x509_cert_url", uauth_provider_x509_cert_url);

		String uclient_x509_cert_url = "https://www.googleapis.com/robot/v1/metadata/x509/angsumandutta1974%40pricchaa-144923.iam.gserviceaccount.com";
		session.setAttribute("soclient_x509_cert_url", uclient_x509_cert_url);

		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\n** SchemaName: " + uschemaName);
		System.out.println("\n** SchemaType: " + uschemaType);
		System.out.println("\n** ProjectName: " + uprojectName);
		System.out.println("\n** PrivateKeyId: " + uprivateKeyId);
		System.out.println("\n** PrivateKey: " + uprivateKey);
		System.out.println("\n** ClientId: " + uclientId);
		System.out.println("\n** ClientEmail: " + uclientEmail);
		System.out.println("\n** Database: " + udatabase);

		/* Getting All the Tables From BigQuery */
		try {
			List<String> tableNameData = bigQueryConnection.getListOfTableNamesFromBigQuery(uprojectName, uprivateKeyId,
					uprivateKey, uclientId, uclientEmail, udatabase);
			System.out.println("tableNameData : " + tableNameData);
			System.out.println("Total tables : " + ((tableNameData != null) ? tableNameData.size() : 0));

			modelAndView.addObject("tableNames", tableNameData);
			session.setAttribute("sessionObjecttableNames", tableNameData);
			modelAndView.addObject("currentSection", "QuickStart");
			modelAndView.addObject("currentLink", "gcpQuickStart");
			modelAndView.setViewName("dispBigQueryTables");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return modelAndView;

	}

	@RequestMapping(value = "/createAWSS3DataTemplate", method = RequestMethod.POST, consumes = "application/json")
	public ModelAndView createAWSS3DataTemplate(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, @RequestBody BucketInfoOne[] postParamValues) {

		System.out.println("calling........ createAWSS3DataTemplate: ");

		List<QuickStartTaskTracker> taskTrackerList = new ArrayList<QuickStartTaskTracker>();
		ModelAndView modelAndView = new ModelAndView("quickStartTaskTracking");

		modelAndView.addObject("currentSection", "QuickStart");
		modelAndView.addObject("currentLink", "awsQuickStart");
		modelAndView.addObject("quickStartSource", "awsQuickStart");

		String qs_status="";
		String qs_message="";
		int connection_success_count=0;

		try {
			Object user = session.getAttribute("user");
			long idUser = (Long) session.getAttribute("idUser");
			System.out.println("idUser=" + idUser);
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage.jsp");
			}

			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

			String createdByUser = "";
			if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
				createdByUser = (String) session.getAttribute("createdByUser");
				System.out.println("======= createdByUser ===>" + createdByUser);
			} else {
				System.out.println("======= idUser ===>" + idUser);

				createdByUser = userDAO.getUserNameByUserId(idUser);
				System.out.println("======= createdByUser ===>" + createdByUser);
			}

			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage.jsp");
			}

			List<BucketInfoOne> bucketList = Arrays.asList(postParamValues);
			System.out.println("bucketList : " + bucketList);

			String datalocation = (String) session.getAttribute("S3schemaType");
			String s3accessKey = (String) session.getAttribute("S3accessKey");
			String s3secretKey = (String) session.getAttribute("S3secretKey");
			String s3schemaName = (String) session.getAttribute("S3schemaName");
			String fileDataFormat = (String) session.getAttribute("S3fileDataFormat");
			String fileNamePattern = (String) session.getAttribute("S3fileNamePattern");
			String headerPresent = (String) session.getAttribute("S3headerPresent");
			String headerFilePath = (String) session.getAttribute("S3headerFilePath");
			String headerFileNamePattern = (String) session.getAttribute("S3headerFileNamePattern");
			String headerFileDataFormat = (String) session.getAttribute("S3headerFileDataFormat");

			Long projectId = (Long) session.getAttribute("projectId");
			Integer domainId = (Integer) session.getAttribute("domainId");

			boolean rbac = RBACController.rbac("QuickStart", "C", session);
			if (rbac) {

				// Iterate the buckets
				if (bucketList != null && bucketList.size() > 0) {

					for (BucketInfoOne bucketInfoOne : bucketList) {
						QuickStartTaskTracker quickStartTaskTracker = new QuickStartTaskTracker();

						// Check if it is a single file bucket or multiple file
						String bucketName = bucketInfoOne.getBucket_name();
						String schemaName = s3schemaName + "_" + bucketName;

						System.out.println(
								"\n=========> create schema and template for bucket[" + bucketName + "] <=========");
						String folderPath="";

						// Create schema
						Long idDataSchema = schemaDAOI.saveDataIntoListDataSchema("", "", "", "", "", schemaName,
								datalocation, "", "", "", "", "", "", "", "", "", "", "", "", "", "", folderPath,
								fileNamePattern, fileDataFormat, headerPresent, headerFilePath, headerFileNamePattern,
								headerFileDataFormat, projectId, createdByUser, s3accessKey, s3secretKey, bucketName,
								"", "", "", "", "", "", "", "", "", "", "", "N", "Y", "N", 0, 0, 2, "N", domainId, "N","","","","","","","Y","N","N","N","cluster","N","N","","","");
						System.out.println("\n====> idDataSchema for bucket[" + bucketName + "] : " + idDataSchema);

						if (idDataSchema != null && idDataSchema != 0l) {

							connection_success_count++;

							quickStartTaskTracker.setIdDataSchema(idDataSchema);
							quickStartTaskTracker.setConnectionName(schemaName);
							
							// invoke schema job
							JSONObject json = dataConnectionService.runSchemaJob(idDataSchema,"Y");

							if(json!=null && json.length()>0){
								if(json.getString("status").trim().equalsIgnoreCase("success")){
									String schemaJobId = json.getString("schemaJobUniqueId");
									quickStartTaskTracker.setSchemaJobId(schemaJobId);

									String message= json.getString("message");
									quickStartTaskTracker.setStatus(message);
								}
							}else
								quickStartTaskTracker.setStatus("Failed to start health check Job");

							taskTrackerList.add(quickStartTaskTracker);
						} else {
							System.out.println(
									"\n====> Failed to create data connection for bucket[" + bucketName + "] !!");
						}

					}
				} else {
					System.out.println("\n====> No Buckets selected !!");
				}

			} else {
				return new ModelAndView("loginPage.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(connection_success_count > 0) {
			qs_status="success";
			qs_message = "Created trust check jobs for the connection details!!";
		} else {
			qs_status="failed";
			qs_message = "Failed to create health check jobs for the connection details!!";
		}
		
		modelAndView.addObject("taskTrackerList", taskTrackerList);
		modelAndView.addObject("qs_status", qs_status);
		modelAndView.addObject("qs_message", qs_message);
		return modelAndView;
	}

	@RequestMapping(value = "/createBigQueryDataTemplate")
	public ModelAndView createBigQueryDataTemplate(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, @RequestBody String[] postParamValues) {

		System.out.println("calling........ createBigQueryDataTemplate: ");

		List<QuickStartTaskTracker> taskTrackerList = new ArrayList<QuickStartTaskTracker>();
		ModelAndView modelAndView = new ModelAndView("quickStartTaskTracking");
		request.setAttribute("currentSection", "QuickStart");
		request.setAttribute("currentLink", "gcpQuickStart");
		request.setAttribute("quickStartSource", "gcpQuickStart");

		String qs_status="";
		String qs_message="";
		int connection_success_count=0;
		
		try {
			Object user = session.getAttribute("user");
			long idUser = (Long) session.getAttribute("idUser");
			System.out.println("idUser=" + idUser);
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage.jsp");
			}

			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

			String createdByUser = "";
			if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
				createdByUser = (String) session.getAttribute("createdByUser");
				System.out.println("======= createdByUser ===>" + createdByUser);
			} else {
				System.out.println("======= idUser ===>" + idUser);

				createdByUser = userDAO.getUserNameByUserId(idUser);
				System.out.println("======= createdByUser ===>" + createdByUser);
			}

			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage.jsp");
			}

			String gcpSchemaName = (String) session.getAttribute("soschemaName");
			String datalocation = (String) session.getAttribute("souschemaType");
			String bigQueryprojectName = (String) session.getAttribute("souprojectName");
			String privateKeyId = (String) session.getAttribute("souprivateKeyId");
			String privateKey = (String) session.getAttribute("souprivateKey");
			String clientId = (String) session.getAttribute("souclientId");
			String clientEmail = (String) session.getAttribute("souclientEmail");
			String database = (String) session.getAttribute("soudatabase");

			List<String> tableList = Arrays.asList(postParamValues);
			System.out.println("tableList : " + tableList);

			Long projectId = (Long) session.getAttribute("projectId");
			Integer domainId = (Integer) session.getAttribute("domainId");
			boolean rbac = RBACController.rbac("QuickStart", "C", session);
			if (rbac) {

				// Iterate the tables
				if (tableList != null && tableList.size() > 0) {

					// Create schema
					Long idDataSchema = schemaDAOI.saveDataIntoListDataSchema("", "", "", "", "", gcpSchemaName,
							datalocation, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "N", "",
							"", "", projectId, createdByUser, "", "", "", bigQueryprojectName, privateKeyId, privateKey,
							clientId, clientEmail, database, "", "", "", "", "", "N", "N", "N", 0, 0, 2, "N", domainId,
							"N", "", "", "", "", "", "", "Y", "N", "N","N","cluster","N", "N","","","");
					System.out.println("\n====> idDataSchema for BigQuery Schema : " + idDataSchema);

					if (idDataSchema != null && idDataSchema != 0l) {

						++ connection_success_count;
						
						QuickStartTaskTracker quickStartTaskTracker = new QuickStartTaskTracker();
						quickStartTaskTracker.setIdDataSchema(idDataSchema);
						quickStartTaskTracker.setConnectionName(gcpSchemaName);
						
						// invoke schema job
						JSONObject json = dataConnectionService.runSchemaJob(idDataSchema,"Y");

						if(json!=null && json.length()>0){
							if(json.getString("status").trim().equalsIgnoreCase("success")){
								String schemaJobId = json.getString("schemaJobUniqueId");
								quickStartTaskTracker.setSchemaJobId(schemaJobId);

								String message= json.getString("message");
								quickStartTaskTracker.setStatus(message);
							}
						}else
							quickStartTaskTracker.setStatus("Failed to start health check Job");

						taskTrackerList.add(quickStartTaskTracker);

					} else {
						System.out.println("\n====> Failed to create data connection for BigQuery Project["
								+ bigQueryprojectName + "] !!");
					}
				} else {
					System.out.println("\n====> No Tables selected !!");
				}

			} else {
				return new ModelAndView("loginPage.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(connection_success_count > 0) {
			qs_status="success";
			qs_message = "Created trust check jobs for the connection details!!";
		} else {
			qs_status="failed";
			qs_message = "Failed to create health check jobs for the connection details!!";
		}
		
		modelAndView.addObject("taskTrackerList", taskTrackerList);
		modelAndView.addObject("qs_status", qs_status);
		modelAndView.addObject("qs_message", qs_message);
		
		return modelAndView;
	}

}
