package com.databuck.controller;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMSummaryDetails;
import com.databuck.filemonitoring.FMService;
import com.databuck.service.AuthorizationService;
import com.databuck.service.DBKFileMonitoringService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.databuck.bean.FileMonitorMasterDashboard;
import com.databuck.bean.FileMonitorRequest;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.DBKFileMonitoringDao;
import com.databuck.service.RBACController;
import com.databuck.bean.DbkFMHistory;

/**
 * This class is used for updating the anomaly detection results tables
 * periodically based on the scheduled time.
 * 
 * @author Sreelakshmi
 *
 */
//@EnableScheduling
@RestController
public class FileMonitorController {
	private static final Logger logger = LoggerFactory.getLogger(FileMonitorController.class);

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	private DBKFileMonitoringDao dbkFileMonitoringDao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private RBACController rbacController;

	@Autowired
	private DBKFileMonitoringService dbkFileMonitoringService;
	
	@Autowired
	private IValidationCheckDAO validationCheckDAO;
	
	@Autowired
	private FMService fmService;
	
	@Autowired
	private AuthorizationService authorizationService;

	// Formats
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	@RequestMapping(value = "/getFileMonitoringImportSampleFile")
	public void getFileMonitoringImportSampleFile(HttpServletResponse response, HttpSession session) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean rbac = RBACController.rbac("Data Template", "R", session);
			if (rbac) {

				// Prepare file for download
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", "FileMonitoring_sample.csv");

				OutputStream outStream = response.getOutputStream();
				response.setHeader(headerKey, headerValue);

				String headerLine = "schema_name,table_name,file_indicator,dayOfWeek,hourOfDay,expected_time,expected_file_count,start_hour,end_hour,frequency";
				String dataLine = "conn_files,dataset1_allChecks.csv,frequency,Wednesday,,,3,10,17,20";
				String dataLines = "conn_files,ArrayData_allChecks.csv,hourly,Wednesday,15,10,3,,,";
				outStream.write(headerLine.toString().getBytes());
				outStream.write("\n".getBytes());
				outStream.write(dataLine.toString().getBytes());
				outStream.write("\n".getBytes());
				outStream.write(dataLines.toString().getBytes());
				outStream.write("\n".getBytes());
				outStream.flush();

			} else
				response.sendRedirect("loginPage");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@RequestMapping(value="/processFMQueueRequest", method=RequestMethod.POST)
	public void processFMQueueRequest(@RequestBody FileMonitorRequest request) {
		System.out.println("*********************** Processing FM Queue Request ***********************");
		try {
			if (request != null) {

				// Getting current Time details
				Calendar cal = Calendar.getInstance();
				Date curDate = new Date();
				cal.setTime(curDate);
				Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

				// Calculate Start hour and end Hour for hourly rules
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				Date startHour = cal.getTime();
				cal.add(Calendar.HOUR, +1);
				Date endHour = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				Date cDate = cal.getTime();
				String curDateStr = dateFormat.format(curDate);

				System.out.println("\nstartHour    :" + startHour);
				System.out.println("endHour      :" + endHour);
				System.out.println("current Date :" + cDate);
				System.out.println("curDateStr   :" + curDateStr);
				System.out.println("dayOfWeek    :" + dayOfWeek);

				// Create FileTrackingRequest
				FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
				fileTrackingHistory.setRequestId(request.getRequestId());
				fileTrackingHistory.setBucketName(request.getBucketName());
				fileTrackingHistory.setFolderPath(request.getFolderPath());
				fileTrackingHistory.setFileName(request.getFileName());
				String fileArrivalDateTime = request.getFileArrivalDateTime();
				Date fDate = dateFormat.parse(fileArrivalDateTime);
				Date fDateTime = dateTimeFormat.parse(fileArrivalDateTime);
				String fTime = timeFormat.format(fDateTime);
				System.out.println("\nfDate       :" + fDate);
				System.out.println("fDateTime    :" + fDateTime);
				System.out.println("fTime        :" + fTime);

				fileTrackingHistory.setFileArrivalDate(fDate);
				fileTrackingHistory.setFileArrivalTime(fTime);
				fileTrackingHistory.setDate(cDate);
				// set status "InProgress"
				fileTrackingHistory.setStatus("InProgress");

				// Insert into FileTrackingHistory
				fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);

				// Identify the FileMonitorRules of this request
				System.out.println("*****Identying the rules for this request .......\n");

				List<FileMonitorRules> rulesList = fileMonitorDao.findFileMonitorRules(request.getBucketName(),
						request.getFolderPath());
				if (rulesList != null && rulesList.size() > 0) {
					for (FileMonitorRules rule : rulesList) {
						System.out.println("=====> Checking the Rule [ID:" + rule.getId() + "] for match ....");
						String filePattern = rule.getFilePattern();
						String fileName = request.getFileName();

						boolean matchFound = false;
						// file pattern Matching
						if (filePattern.contains("*")) {
							String[] tokens = filePattern.split("\\*");
							if (filePattern.startsWith("*") && fileName.endsWith(tokens[1])) {
								matchFound = true;
							} else if (filePattern.endsWith("*") && fileName.startsWith(tokens[0])) {
								matchFound = true;
							} else if (fileName.startsWith(tokens[0]) && fileName.endsWith(tokens[1])) {
								matchFound = true;
							}
						} else if (fileName.equals(filePattern)) {
							matchFound = true;
						}
						System.out.println("=====> File and FilePattern match:" + matchFound);

						// If a rule match found, identify its entry in the FileTrackingSummary table
						if (matchFound) {
							String[] hourTokens = fTime.split(":");
							int fHour = Integer.parseInt(hourTokens[0]);
							int fMinutes = Integer.parseInt(hourTokens[1]);

							String[] ruleHourTokens = rule.getTimeOfCheck().split(":");
							int ruleHour = Integer.parseInt(ruleHourTokens[0]);
							int ruleMinutes = Integer.parseInt(ruleHourTokens[1]);

							boolean hourMatch = false;
							String hourOfDay = rule.getTimeOfCheck();

							// Match hour of the day of file
							if (rule.getFrequency().equals("daily") && cDate.equals(fDate)
									&& (fHour < ruleHour || (fHour == ruleHour && fMinutes <= ruleMinutes))) {
								hourMatch = true;
							} else if ((rule.getFrequency().equals("weekly") && cDate.equals(fDate)
									&& dayOfWeek == rule.getDayOfCheck())
									&& (fHour < ruleHour || (fHour == ruleHour && fMinutes <= ruleMinutes))) {
								hourMatch = true;
							} else if (rule.getFrequency().equals("hourly")
									&& (fDateTime.after(startHour) || fDateTime.equals(startHour))
									&& (fDateTime.before(endHour) || fDateTime.equals(endHour))) {
								hourMatch = true;
								hourOfDay = timeFormat.format(endHour);
							}
							System.out.println("=====> File Arrival time matched:" + matchFound);

							if (hourMatch) {
								List<FileTrackingSummary> summaryList = fileMonitorDao
										.getFileTrackingSummaryForRule(rule.getId(), curDateStr, dayOfWeek, hourOfDay);
								if (summaryList != null && summaryList.size() > 0) {
									for (FileTrackingSummary summary : summaryList) {
										Integer fileCount = summary.getFileCount() + 1;
										String countStatus = "unmatched";
										if (fileCount == rule.getFileCount()) {
											countStatus = "matched";
										}
										// Update fileCount in TrakingSummary
										summary.setFileCount(fileCount);
										summary.setCountStatus(countStatus);
										summary.setLastUpdateTimeStamp(curDate);
										fileMonitorDao.addFileTrackingSummary(summary);
									}
								} else {
									// No entry found hence make a new entry
									// Get the properties
									Integer fileCount = 1;
									String countStatus = "unmatched";
									if (fileCount == rule.getFileCount()) {
										countStatus = "matched";
									}

									// Insert into FileTrackingSummary
									FileTrackingSummary fileTrackingSummary = new FileTrackingSummary();
									fileTrackingSummary.setDate(cDate);
									fileTrackingSummary.setFileMonitorRules(rule);
									fileTrackingSummary.setDayOfWeek(dayOfWeek);
									fileTrackingSummary.setHourOfDay(hourOfDay);
									fileTrackingSummary.setFileCount(fileCount);
									fileTrackingSummary.setCountStatus(countStatus);
									fileTrackingSummary.setLastUpdateTimeStamp(curDate);

									fileMonitorDao.addFileTrackingSummary(fileTrackingSummary);
								}
								System.out.println("=====> REQUEST PASSED SUCCESSFULLY!!\n");
							} else {
								System.out.println(
										"=====> REQUEST FAILED: fileArrival time and HourOfDay in Rule doesn't match!!\n");
							}
						} else {
							System.out.println(
									"=====> REQUEST FAILED: filePattern and filename doesn't match for Rule !!\n");
						}
						// Update the status of the request in the FileTrackingHistory
						fileTrackingHistory.setStatus("processed");
						fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);
					}
				} else {
					fileTrackingHistory.setStatus("Failed");
					fileTrackingHistory.setStatusMessage("No matching rules for this request.");
					fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);
					System.out.println("=====> REQUEST FAILED: No matching rules for this request!!\n");
				}
			} else {
				System.out.println("=====> REQUEST FAILED: Request is blank!!\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("=====> REQUEST FAILED: Failed to convert json string to object!!\n");
		}
		System.out.println("*********************** END ***********************");
	}

	// @Scheduled(fixedDelay = 30000)
	@RequestMapping("/readFileMonitorRules")
	public void readFileMonitorRules() {
		System.out.println("*********************** Read File Monitor Rules ***********************");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		// Current date object
		Date curDate = cal.getTime();
		// Current day
		int currentDay = cal.get(Calendar.DAY_OF_WEEK);

		// Get Date String
		String dateString = dateTimeFormat.format(curDate);
		String dateStr = dateString.split(" ")[0];

		// Get Time String
		String timeStr = timeFormat.format(curDate);
		System.out.println("Current Date         :" + dateString);
		System.out.println("dateStr              :" + dateStr);
		System.out.println("timeStr              :" + timeStr);

		// Fetch Active Rules at current time
		List<FileMonitorRules> activeRulesList = fileMonitorDao.getCurrentTimeMonitorRules(timeStr, currentDay);

		if (activeRulesList != null && activeRulesList.size() > 0) {
			Map<Long, FileMonitorRules> activeRulesMap = new HashMap<Long, FileMonitorRules>();
			for (FileMonitorRules fm : activeRulesList) {
				activeRulesMap.put(fm.getId(), fm);
			}

			String dailyRulesIds = StringUtils.join(activeRulesMap.keySet(), ",");
			System.out.println("Active Rule Id's list       :[" + dailyRulesIds + "]");

			// Get the Tracking summary for the Monitor rules whose count doesn't match
			List<FileTrackingSummary> activeRulesSummaryList = fileMonitorDao.getActiveRulesSummary(dailyRulesIds,
					dateStr, timeStr, currentDay);
			List<FileMonitorRules> matchingRules = new ArrayList<FileMonitorRules>();
			List<FileMonitorRules> missingRules = new ArrayList<FileMonitorRules>();
			List<FileMonitorRules> failedRules = new ArrayList<FileMonitorRules>();
			Map<Long, FileTrackingSummary> summaryStatusMap = new HashMap<Long, FileTrackingSummary>();

			if (activeRulesSummaryList != null && activeRulesSummaryList.size() > 0) {
				for (FileTrackingSummary summary : activeRulesSummaryList) {
					summaryStatusMap.put(summary.getFileMonitorRules().getId(), summary);
				}

				// Identify the matched/failed/missing rules
				for (long id : activeRulesMap.keySet()) {
					FileMonitorRules rule = activeRulesMap.get(id);
					if (summaryStatusMap.get(id) != null) {
						FileTrackingSummary summary = summaryStatusMap.get(id);
						if (summary.getCountStatus().equals("matched")) {
							matchingRules.add(rule);
						} else if (summary.getCountStatus().equals("unmatched")) {
							failedRules.add(rule);
						}
					} else {
						missingRules.add(rule);
					}
				}
			} else {
				// All rules are missing
				missingRules.addAll(activeRulesList);
			}
			System.out.println("Total Active rules count    :" + activeRulesMap.size());
			System.out.println("Missing rules count         :" + missingRules.size());
			System.out.println("Failed rules count          :" + failedRules.size());
			System.out.println("Matched rules count         :" + matchingRules.size());
			if (missingRules.size() > 0 || failedRules.size() > 0) {
				String subject = "FileMonitorRules Report : " + dateString;
				StringBuilder sb = new StringBuilder();
				sb.append("Dear Sir/Madam,");
				sb.append(
						"\n\nThis email is to notify the details of File Monitor Rules which are failed at the time of check '"
								+ dateString + "'. Refer to below details.");
				sb.append(
						"\n\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
				sb.append("\t\t\t\t\t\t\t\t\t\t\t	 Report");
				sb.append(
						"\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
				sb.append(
						"RuleId\tBucketName\t\tFolderPath\t\tFilePattern\t\tFrequency\t\tExpectedCount\t\tActualCount\t\tStatus\n");
				for (FileMonitorRules rule : failedRules) {
					sb.append("\n" + StringUtils.rightPad(String.valueOf(rule.getId()), 7) + "\t"
							+ StringUtils.rightPad(rule.getBucketName(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFolderPath(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFilePattern(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFrequency(), 10) + "\t\t\t"
							+ StringUtils.rightPad(String.valueOf(rule.getFileCount()), 15) + "\t\t\t" + StringUtils
									.rightPad(String.valueOf(summaryStatusMap.get(rule.getId()).getFileCount()), 15)
							+ "\t\t" + StringUtils.rightPad("Failed", 15));
				}
				for (FileMonitorRules rule : missingRules) {
					sb.append("\n" + StringUtils.rightPad(String.valueOf(rule.getId()), 7) + "\t"
							+ StringUtils.rightPad(rule.getBucketName(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFolderPath(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFilePattern(), 20) + "\t"
							+ StringUtils.rightPad(rule.getFrequency(), 10) + "\t\t\t"
							+ StringUtils.rightPad(String.valueOf(rule.getFileCount()), 15) + "\t\t\t"
							+ StringUtils.rightPad("0", 15) + "\t\t" + StringUtils.rightPad("Failed", 15));
				}

				sb.append(
						"\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");

				System.out.println("Sending notification to user ...........");
				sendSNSNotification(sb.toString(), subject);
			}
		} else {
			System.out.println("No Active rules at this time:" + dateString);
		}
		System.out.println("********************************  END   *******************************");
	}

	private void sendSNSNotification(String message, String subject) {
		String topicArn = appDbConnectionProperties.getProperty("filemonitor.sns.aws.topicARN");
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
				appDbConnectionProperties.getProperty("filemonitor.aws.accessKey"),
				appDbConnectionProperties.getProperty("filemonitor.aws.secretKey"));
		AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard();
		AmazonSNS snsClient = builder.withRegion(Regions.US_EAST_1.getName())
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		try {
			PublishResult result = snsClient
					.publish(new PublishRequest().withMessage(message).withSubject(subject).withTopicArn(topicArn));
			System.out.println(result);
		} catch (Exception e) {
			System.out.println("Exception while sending sns notification:" + e.getMessage());
		}
	}

	// added UI Things for File Monitoring Configuration

	@RequestMapping(value = "/fileMonitoringUI")
	public ModelAndView addNewUser(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		/*
		 * Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
		 * model.addObject("Roles", Roles);
		 */

		model.setViewName("fileMonitoringUI");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "File Monitoring");
		return model;
	}

	@RequestMapping(value = "/fileMonitorResults")
	public ModelAndView getFileMonitoringResultsForAppId(ModelAndView model, HttpSession session,
			@RequestParam Long idApp, @RequestParam String appName) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		List<FileTrackingSummary> listFileTrackingSumm = fileMonitorDao.getFileTrackingRulesSummaryDetails(idApp);

		/*System.out.println("listFileTrackingSumm =>" + listFileTrackingSumm);*/

		model.addObject("listFileTrackingSumm", listFileTrackingSumm);

		List<FileTrackingHistory> listFileTrackingHistory = fileMonitorDao.getFileTrackingHistoryDetails(idApp);

		model.addObject("listFileTrackingHistory", listFileTrackingHistory);

		List<DbkFMSummaryDetails> listFileFMSummaryDetails = dbkFileMonitoringDao.getDBKFMSummaryDetails(idApp);

		model.addObject("listFileFMSummaryDetails", listFileFMSummaryDetails);
		List<DbkFMFileArrivalDetails> listFileFMArrivalDetails = dbkFileMonitoringDao.getDBKFMFileArrivalDetails(idApp);

		model.addObject("listFileFMArrivalDetails", listFileFMArrivalDetails);

		model.setViewName("fileMonitoringResults");
		model.addObject("idApp", idApp);
		model.addObject("appName", appName);
		model.addObject("currentSection", "Dashboard");
		model.addObject("currentLink", "File Monitoring");
		return model;
	}

	@RequestMapping(value = "/fileMonitoringView")
	public ModelAndView fileMonitoringView(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		// Get the validation checks of type FileMonitoring
		List<FileMonitorMasterDashboard> listappslist = fileMonitorDao.getFileMonitoringAppsList();
		
		for (FileMonitorMasterDashboard fm : listappslist) {
			
			String fileCountStatus = fileMonitorDao.getOverallCountStatusOfFM(fm.getIdApp());
			String fileSizeStatus = fileMonitorDao.getOverallFileSizeStatusOfFM(fm.getIdApp());
			fm.setFileCountStatus(fileCountStatus);
			fm.setFileSizeStatus(fileSizeStatus);
		}

		// Get list  of Applications with File Monitoring type(Snowflake, S3, Azure) - new code from file monitoring engine(backend)
		List<FileMonitorMasterDashboard> fm_listappslist = fileMonitorDao.getDbkFileMonitoringAppsList();
		
		if(fm_listappslist!=null && !fm_listappslist.isEmpty()) {
			if(listappslist != null) 
				listappslist.addAll(fm_listappslist);
			else
				listappslist = fm_listappslist;
		}
		
		model.setViewName("fileMonitoringView");
		model.addObject("listappslistds", listappslist);
		model.addObject("currentSection", "Dashboard");
		model.addObject("currentLink", "File Monitoring");
		return model;
	}

	@RequestMapping(value = "/fileMonitoringUploadCSV")
	public ModelAndView fileMonitoringUploadCSV(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		/*
		 * Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
		 * model.addObject("Roles", Roles);
		 */

		model.setViewName("fileMonitoringUploadCSV");
		model.addObject("currentSection", "Validation Check");
		model.addObject("currentLink", "File Monitoring");
		return model;
	}

	@RequestMapping(value = "/viewFileMonitoringCSV")
	public ModelAndView viewFileMonitoringCSV(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		/*
		 * Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
		 * model.addObject("Roles", Roles);
		 */

		model.setViewName("viewFileMonitoringCSV");
		model.addObject("currentSection", "Validation Check");
		model.addObject("currentLink", "File Monitoring");
		return model;
	}

	/*
	 * @RequestMapping(value = "/fileMonitorResults") public ModelAndView
	 * fileMonitorResults(ModelAndView model, HttpSession session) { Object user =
	 * session.getAttribute("user"); System.out.println("user:" + user); if ((user
	 * == null) || (!user.equals("validUser"))) { return new
	 * ModelAndView("loginPage"); }
	 * 
	 * Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
	 * model.addObject("Roles", Roles);
	 * 
	 * 
	 * 
	 * 
	 * 
	 * model.setViewName("fileMonitoringResults"); model.addObject("currentSection",
	 * "User Settings"); model.addObject("currentLink", "File Monitoring"); return
	 * model; }
	 */
	/*
	 * @RequestMapping(value = "/viewEditFileMonitoring") public ModelAndView
	 * viewEditFileMonitoring(ModelAndView model, HttpSession session) { Object user
	 * = session.getAttribute("user"); System.out.println("user:" + user); if ((user
	 * == null) || (!user.equals("validUser"))) { return new
	 * ModelAndView("loginPage"); }
	 * 
	 * Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
	 * model.addObject("Roles", Roles);
	 * 
	 * 
	 * model.setViewName("editFileMonitoring"); model.addObject("currentSection",
	 * "User Settings"); model.addObject("currentLink", "File Monitoring"); return
	 * model; }
	 */

	@RequestMapping(value = "/submitFileMonitoringCSV", method = RequestMethod.POST)
	public @ResponseBody ModelAndView uploadImportFileHandler(@RequestParam("dataupload") MultipartFile file,
			@RequestParam("fileMonitoringType") String fileMonitoringType,@RequestParam("connectionId") int connectionId,
		  	HttpSession session, HttpServletRequest request, HttpServletResponse res)
			throws DataAccessException, Exception {

		Object user = session.getAttribute("user");
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		System.out.println("user:" + user);

		// int idApp = Integer.parseInt((String) session.getAttribute("idAppVal"));

		Object objIdApp = session.getAttribute("idAppVal");

		String strIdApp = objIdApp.toString();
		int idApp = Integer.parseInt(strIdApp);

		String viewName="viewFileMonitoringCSV";

		if(fileMonitoringType!=null && !fileMonitoringType.trim().isEmpty() &&
				(fileMonitoringType.trim().equalsIgnoreCase("snowflake") || fileMonitoringType.trim().equalsIgnoreCase("azuredatalakestoragegen2batch") || fileMonitoringType.trim().equalsIgnoreCase("aws s3")))
				viewName="dbkFileMonitoringView";
		System.out.println("idAppVal --------------------------------" + idApp);
		// Map finalMap = new HashMap();

		// String schemaName = request.getParameter("schemaName");
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {

			System.out.println("hello create form");

			System.out.println("@RequestParam(\"dataupload\") MultipartFile file =>" + file.getOriginalFilename());

			File file1 = convert(file);

			System.out.println("Path =======================>" + file1.getAbsolutePath());

			ArrayList<String> csvFileDataArr = new ArrayList<String>();

			/*
			 * String line = br.readLine();
			 * 
			 * System.out.println("line =>"+line);
			 */

			ModelAndView modelAndView = new ModelAndView(viewName);

			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			modelAndView.addObject("fileMonitoringType", fileMonitoringType);

			if(fileMonitoringType.equalsIgnoreCase("snowflake") || fileMonitoringType.equalsIgnoreCase("azuredatalakestoragegen2batch") || fileMonitoringType.equalsIgnoreCase("aws s3")){

				List<DBKFileMonitoringRules> dbkFileMonitoringRules = dbkFileMonitoringService.submitFileMonitoringCSVForSnowFlake(file1,file.getOriginalFilename(),idApp);

				modelAndView.addObject("dbkFileMonitoringRules", dbkFileMonitoringRules);

				modelAndView.addObject("connectionId", connectionId);

			}else{

				BufferedReader br = new BufferedReader(new FileReader(file.getOriginalFilename()));
				System.out.println("File data =>" + br.readLine());
				// Delimiters used in the CSV file
				final String COMMA_DELIMITER = ",";
				String line = "";

				// Create List for holding FileMonitorRules objects
				List<FileMonitorRules> arrListFileMonitorRule = new ArrayList<FileMonitorRules>();

				while ((line = br.readLine()) != null) {

					String[] fileDetails = line.split(COMMA_DELIMITER);

					if (fileDetails.length > 0) {

						// save the filedetails in FileMonitorRules object

						// String fId = fileDetails[0];

						// long id = 101L;

						// System.out.println();

						String bucketName = fileDetails[0];
						String strDayOfchk = fileDetails[1];

						Integer dayOfCheck = 0;

						if (strDayOfchk.equalsIgnoreCase("Sun") || strDayOfchk.equalsIgnoreCase("Sunday")) {
							dayOfCheck = 1;
						} else if (strDayOfchk.equalsIgnoreCase("Mon") || strDayOfchk.equalsIgnoreCase("Monday")) {
							dayOfCheck = 2;
						} else if (strDayOfchk.equalsIgnoreCase("Tue") || strDayOfchk.equalsIgnoreCase("Tuesday")) {
							dayOfCheck = 3;

						} else if (strDayOfchk.equalsIgnoreCase("wed") || strDayOfchk.equalsIgnoreCase("Wednesday")) {
							dayOfCheck = 4;

						} else if (strDayOfchk.equalsIgnoreCase("thur") || strDayOfchk.equalsIgnoreCase("Thursday")) {
							dayOfCheck = 5;

						} else if (strDayOfchk.equalsIgnoreCase("fri") || strDayOfchk.equalsIgnoreCase("Friday")) {
							dayOfCheck = 6;

						} else if (strDayOfchk.equalsIgnoreCase("sat") || strDayOfchk.equalsIgnoreCase("Saturday")) {
							dayOfCheck = 7;

						}

						Integer fileCount = Integer.parseInt(fileDetails[2]);
						String filePattern = fileDetails[3];

						String folderPath = fileDetails[4];

						String frequency = fileDetails[5];

						String timeOfCheck = fileDetails[7];

						System.out.println("timeOfCheck:" + timeOfCheck);

						Integer fileSizeThreshold = Integer.parseInt(fileDetails[8]);

						String partitionedFolders = "N";

						int maxFolderDepth = 2;

						Date lastProcessedDate = null;

						FileMonitorRules fm = new FileMonitorRules(bucketName, folderPath, filePattern, frequency,
								dayOfCheck, timeOfCheck, fileCount, lastProcessedDate, idApp, fileSizeThreshold, partitionedFolders, maxFolderDepth);

						System.out.println("FM =>" + fm);

						arrListFileMonitorRule.add(fm);

					}

				}
				System.out.println("arrListFileMonitorRule =>" + arrListFileMonitorRule);
				// Lets print the Employee List

				br.close();
				modelAndView.addObject("arrListFileMonitorRule", arrListFileMonitorRule);
				modelAndView.addObject("connectionId", -1);
			}

			return modelAndView;
		}

		return new ModelAndView("loginPage");

	}

	@RequestMapping(value = "/editFileMonitoring", method = RequestMethod.POST)
	public @ResponseBody ModelAndView editFileMonitoring(HttpSession session, HttpServletRequest request,
			HttpServletResponse res) throws DataAccessException, Exception {

		Object user = session.getAttribute("user");
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		System.out.println("user:" + user);
		// Map finalMap = new HashMap();

		// String schemaName = request.getParameter("schemaName");
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {

			int idApp = 0;

			List<FileMonitorRules> arrListFileMonitorRule = fileMonitorDao.getAllFileMonitorRuleDetailsByIdApp(idApp);

			System.out.println("arrListFileMonitorRule =>" + arrListFileMonitorRule);

			ModelAndView modelAndView = new ModelAndView("editFileMonitoring");

			modelAndView.addObject("arrListFileMonitorRule", arrListFileMonitorRule);

			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");

			return modelAndView;
		}

		return new ModelAndView("loginPage");

	}

	public File convert(MultipartFile file) {
		File convFile = new File(file.getOriginalFilename());

		try {
			convFile.createNewFile();
			FileOutputStream fos;
			fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convFile;
	}

	@RequestMapping(value = "/editDBKFileMonitoringRule", method = RequestMethod.POST)
	public void editDBKFileMonitoringRule(HttpSession session, HttpServletRequest request, HttpServletResponse response,
												@RequestBody List<DBKFileMonitoringRules> dataList) {
		JSONObject json = new JSONObject();
		try {
			Object objIdApp = session.getAttribute("idAppVal");

			String strIdApp = objIdApp.toString();
			int idApp = Integer.parseInt(strIdApp);
			System.out.println("\n====> idApp="+idApp);

			for(DBKFileMonitoringRules fMRules:dataList){

				boolean rowStatus= fileMonitorDao.isRowIdExitForDbkFileMonitorRules(fMRules.getId());

				if (rowStatus) {
					fileMonitorDao.updateToDbkFileMonitorRules(fMRules, idApp);
				} else
					fileMonitorDao.insertToDbkFileMonitorRules(
							new Object[] { fMRules.getConnectionId(), idApp, fMRules.getSchemaName(),
									fMRules.getTableName(), fMRules.getFileIndicator(), fMRules.getDayOfWeek(),
									fMRules.getHourOfDay(), fMRules.getExpectedTime(), fMRules.getExpectedFileCount(),
									fMRules.getStartHour(), fMRules.getEndHour(), fMRules.getFrequency() });
			}

			// json.append("success", "Deleted successfully");
			json.put("success", "Submitted successfully");
			response.getWriter().println(json);
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/saveDBKFileMonitoringRule", method = RequestMethod.POST)
	public void saveDBKFileMonitoringRule(HttpSession session, HttpServletRequest request, HttpServletResponse response,
												@RequestBody List<DBKFileMonitoringRules> dataList) {
		JSONObject json = new JSONObject();
		try {
			Object objIdApp = session.getAttribute("idAppVal");

			String strIdApp = objIdApp.toString();
			int idApp = Integer.parseInt(strIdApp);
			System.out.println("\n====>idApp="+idApp);

			for (DBKFileMonitoringRules fMRules : dataList) {

				fileMonitorDao.insertToDbkFileMonitorRules(
						new Object[] { fMRules.getConnectionId(), idApp, fMRules.getSchemaName(),
								fMRules.getTableName(), fMRules.getFileIndicator(), fMRules.getDayOfWeek(),
								fMRules.getHourOfDay(), fMRules.getExpectedTime(), fMRules.getExpectedFileCount(),
								fMRules.getStartHour(), fMRules.getEndHour(), fMRules.getFrequency() });

			}

			// json.append("success", "Deleted successfully");
			json.put("success", "Submitted successfully");
			response.getWriter().println(json);
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/saveFileMonitoringRule", method = RequestMethod.POST)
	public void saveFileMonitoringRule(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String singleRowData) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		/*
		 * if ((user == null) || (!user.equals("validUser"))) { return new
		 * ModelAndView("loginPage"); }
		 */

		// System.out.println("FileDetails from json =>"+ singleRowData);

		// converting string to str arr

		// "101||bucket_S2||1 ||20||filepatt||C:/test||Daily ||13:14||

		// Map<String, String> tempmap = new HashMap<String, String>();

		Object objIdApp = session.getAttribute("idAppVal");

		String strIdApp = objIdApp.toString();
		int idApp = Integer.parseInt(strIdApp);
		/*
		 * try {
		 */
		System.out.println("singleRowData =>" + singleRowData);

		/*
		 * if(singleRowData.contains("\"\"")) {
		 * System.out.println("In IF................................"); }
		 */

		System.out.println("idApp Val*******************" + session.getAttribute("idAppVal"));

		if (singleRowData != null && !singleRowData.contains("\"\"")) {

			System.out.println("data=" + singleRowData);
			String s1 = singleRowData.replaceFirst("\\|\\|", "");

			String subStr = s1.substring(1, s1.length()).replace("||\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\"", "");

			System.out.println("subStr =>" + subStr);

			boolean result = false;

			String[] s2 = subStr.trim().split("\\|\\|");

			// List<String> elements = new ArrayList<String>();

			System.out.println("s2 =>" + s2.length);

			FileMonitorRules f = new FileMonitorRules();

			System.out.println("s2[0] =>" + s2[0]);
			f.setBucketName(s2[0]);

			// String dayNameFromVal = ;
			// System.out.println(" DayOfWeek.valueOf((s2[1]).trim()).getValue() Value
			// ->"+DayOfWeek.valueOf((s2[1]).trim()).getValue());

			String strDayOfchk1 = s2[1].trim();

			System.out.println("strDayOfchk =>" + strDayOfchk1);

			String[] strSelect = strDayOfchk1.split("#");

			// System.out.println("strSelect ->"+strSelect[1]);

			// splitting of string MondayDaily

			String strDayOfchk = strSelect[0];

			Integer dayOfCheck = 0;

			if (strDayOfchk.equalsIgnoreCase("Sun") || strDayOfchk.equalsIgnoreCase("Sunday")) {
				dayOfCheck = 1;
			} else if (strDayOfchk.equalsIgnoreCase("Mon") || strDayOfchk.equalsIgnoreCase("Monday")) {
				dayOfCheck = 2;
			} else if (strDayOfchk.equalsIgnoreCase("Tue") || strDayOfchk.equalsIgnoreCase("Tuesday")) {
				dayOfCheck = 3;

			} else if (strDayOfchk.equalsIgnoreCase("wed") || strDayOfchk.equalsIgnoreCase("Wednesday")) {
				dayOfCheck = 4;

			} else if (strDayOfchk.equalsIgnoreCase("thur") || strDayOfchk.equalsIgnoreCase("Thursday")) {
				dayOfCheck = 5;

			} else if (strDayOfchk.equalsIgnoreCase("fri") || strDayOfchk.equalsIgnoreCase("Friday")) {
				dayOfCheck = 6;

			} else if (strDayOfchk.equalsIgnoreCase("sat") || strDayOfchk.equalsIgnoreCase("Saturday")) {
				dayOfCheck = 7;
			}

			f.setDayOfCheck(dayOfCheck);
			// f.setDayOfCheck(Integer.parseInt((s2[1]).trim()));
			f.setFileCount(Integer.parseInt((s2[2]).trim()));
			f.setFilePattern(s2[3]);
			f.setFolderPath(s2[4]);

			String strFreq = strSelect[1];
			f.setFrequency(strFreq);

			System.out.println("s2[6]=" + s2[6]);

			// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy",
			// Locale.ENGLISH);

			f.setLastProcessedDate(null);

			f.setTimeOfCheck(s2[6]);

			f.setFileSizeThreshold(Integer.parseInt(s2[7]));
			
			f.setPartitionedFolders(s2[8]);
			
			int maxFolderDepth = 2;
			if(s2[9] !=null && !s2[9].trim().isEmpty()) {
				maxFolderDepth = Integer.parseInt(s2[9]);
			}
			f.setMaxFolderDepth(maxFolderDepth);

			f.setIdApp(idApp);

			System.out.println("FM After Subm =>" + f);

			result = fileMonitorDao.addMonitorRule(f);

			JSONObject json = new JSONObject();

			if (result) {
				try {
					// json.append("success", "Deleted successfully");
					json.put("success", "Submitted successfully");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					json.put("fail", "There was a problem");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/*
			 * if(result) {
			 * 
			 * System.out.println("-------- In Result");
			 * 
			 * //modelAndView.setViewName("FileMonitoringSuccess");
			 * 
			 * 
			 * modelAndView.addObject("currentSection","Validation Check");
			 * modelAndView.addObject("currentLink", "DTView");
			 * System.out.println("File Monitoring configurations saved successfully");
			 * modelAndView.addObject("fm",
			 * "File Monitoring configurations saved successfully"); }
			 */

		}

		/*
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	@RequestMapping(value = "/displaySuccess")
	public void fileMonitoringDispSuccess(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		ModelAndView modelAndView = new ModelAndView();

		System.out.println("In display Success");
		/*
		 * modelAndView.setViewName("message"); modelAndView.addObject("msg",
		 * "File Monitoring configurations saved successfully");
		 * modelAndView.addObject("currentSection", "Validation Check");
		 * modelAndView.addObject("currentLink", "VCView");
		 */
		// return modelAndView;

	}

	/*
	 * } else { modelAndView.setViewName("message"); modelAndView.addObject("msg",
	 * "Problem in Configuration"); modelAndView.addObject("currentSection",
	 * "Validation Check"); modelAndView.addObject("currentLink", "VCView"); return
	 * modelAndView; }
	 */
	@RequestMapping(value = "/updateFileMonitoringById", method = RequestMethod.POST)
	public void updateFileMonitoringById(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String singleRowData) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		/*
		 * if ((user == null) || (!user.equals("validUser"))) { try {
		 * response.sendRedirect("loginPage.jsp"); } catch (IOException e) {
		 * e.printStackTrace(); } }
		 */

		System.out.println("In update......");

		System.out.println("singleRowData =>" + singleRowData);
		ModelAndView modelAndView = new ModelAndView();
		try {

			// ||823||bucket_S6||Monday||24||filepatt||C:/test||Monday||13:14||

			if (singleRowData != null && !singleRowData.contains("\"\"")) {

				System.out.println("data=" + singleRowData);
				String s1 = singleRowData.replaceFirst("\\|\\|", "");

				String subStr = s1.substring(1, s1.length()).replace("||\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\"", "");

				System.out.println("subStr =>" + subStr);

				int result = 0;

				String[] s2 = subStr.trim().split("\\|\\|");

				// List<String> elements = new ArrayList<String>();

				Object objIdApp = session.getAttribute("idApp");

				String strIdApp = objIdApp.toString();
				int idApp = Integer.parseInt(strIdApp);

				System.out.println("s2 =>" + s2.length);

				FileMonitorRules f = new FileMonitorRules();

				System.out.println("s2[0] =>" + s2[0]);

				System.out.println("s2[0] =>" + s2[0]);

				f.setId(Long.parseLong(s2[0]));
				f.setBucketName(s2[1]);

				// String dayNameFromVal = ;
				// System.out.println(" DayOfWeek.valueOf((s2[1]).trim()).getValue() Value
				// ->"+DayOfWeek.valueOf((s2[1]).trim()).getValue());

				String strDayOfchk1 = s2[2].trim();

				System.out.println("strDayOfchk =>" + strDayOfchk1);

				String[] strSelect = strDayOfchk1.split("#");

				System.out.println("strSelect ->" + strSelect[1]);

				// splitting of string MondayDaily

				String strDayOfchk = strSelect[0];

				// String strDayOfchk = "Mon";

				Integer dayOfCheck = 0;

				if (strDayOfchk.equalsIgnoreCase("Sun") || strDayOfchk.equalsIgnoreCase("Sunday")) {
					dayOfCheck = 1;
				} else if (strDayOfchk.equalsIgnoreCase("Mon") || strDayOfchk.equalsIgnoreCase("Monday")) {
					dayOfCheck = 2;
				} else if (strDayOfchk.equalsIgnoreCase("Tue") || strDayOfchk.equalsIgnoreCase("Tuesday")) {
					dayOfCheck = 3;

				} else if (strDayOfchk.equalsIgnoreCase("wed") || strDayOfchk.equalsIgnoreCase("Wednesday")) {
					dayOfCheck = 4;

				} else if (strDayOfchk.equalsIgnoreCase("thur") || strDayOfchk.equalsIgnoreCase("Thursday")) {
					dayOfCheck = 5;

				} else if (strDayOfchk.equalsIgnoreCase("fri") || strDayOfchk.equalsIgnoreCase("Friday")) {
					dayOfCheck = 6;

				} else if (strDayOfchk.equalsIgnoreCase("sat") || strDayOfchk.equalsIgnoreCase("Saturday")) {
					dayOfCheck = 7;
				}

				f.setDayOfCheck(dayOfCheck);
				// f.setDayOfCheck(Integer.parseInt((s2[1]).trim()));
				f.setFileCount(Integer.parseInt((s2[3]).trim()));
				f.setFilePattern(s2[4]);
				f.setFolderPath(s2[5]);

				String strFreq = strSelect[1];

				f.setFrequency(strFreq);

//				f.setFrequency(s2[6]);

				System.out.println("s2[6]=" + s2[6]);

				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

				f.setTimeOfCheck(s2[7]);

				f.setFileSizeThreshold(Integer.parseInt(s2[8]));
				
				f.setPartitionedFolders(s2[9]);
				
				int maxFolderDepth = 2;
				if(s2[9] !=null && !s2[9].trim().isEmpty()) {
					maxFolderDepth = Integer.parseInt(s2[10]);
				}
				f.setMaxFolderDepth(maxFolderDepth);
				
				f.setIdApp(idApp);

				System.out.println("FM After Subm =>" + f);

				long id = f.getId();

				System.out.println("id....................." + id);

				// Get FileMonitorRules based on Id
				FileMonitorRules fmRule = fileMonitorDao.getFileMonitorRulesById(id);

				if (fmRule != null) {
					f.setIdDataSchema(fmRule.getIdDataSchema());
					f.setLastProcessedDate(fmRule.getLastProcessedDate());
				}

				ArrayList<FileMonitorRules> fileMonitorArr = new ArrayList<FileMonitorRules>();

				fileMonitorArr.add(f);

				for (FileMonitorRules rule : fileMonitorArr) {

					result = fileMonitorDao.updateFileMonitorRulesById(rule.getId(), rule);
				}
				/*
				 * if (result > 0) { System.out.println("In result...." + result);
				 * modelAndView.setViewName("message"); modelAndView.addObject("msg",
				 * "Updated Successfully!!!!"); modelAndView.addObject("currentSection",
				 * "Validation Check"); modelAndView.addObject("currentLink", "VCView"); } else
				 * { modelAndView.setViewName("message"); modelAndView.addObject("msg",
				 * "Problem creating data template"); modelAndView.addObject("currentSection",
				 * "Validation Check"); modelAndView.addObject("currentLink", "VCView"); }
				 */
				JSONObject json = new JSONObject();

				if (result > 0) {
					try {
						// json.append("success", "Deleted successfully");
						json.put("success", "Updated successfully");
						response.getWriter().println(json);
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						json.put("fail", "There was a problem");
						response.getWriter().println(json);
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				System.out.println("result--------->" + result);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return modelAndView;
	}

	@RequestMapping(value = "/deleteFileMonitorRuleById", method = RequestMethod.GET)
	public void deleteFileMonitorRuleById(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long id, @RequestParam int idApp) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		JSONObject json = new JSONObject();

		// long id = Long.parseLong(request.getParameter("fileMoId"));

		// long id = Long.parseLong(request.getParameter("id"));

		System.out.println("-- deleteFileMonitorRuleById ----" + request.getParameter("id"));

		boolean res = fileMonitorDao.deleteFileMonitorRuleById(id);

		System.out.println("result Delete----->" + res);

		response.sendRedirect(
				"http://localhost:8082/databuck/customizeValidation?idApp=3829&laName=3829_test_pri_fm&idData=7&lsName=%20oracle_source_50m");

	}

	@RequestMapping(value = "/deleteDBKFileMonitorRule", method = RequestMethod.GET)
	public void deleteDBKFileMonitorRule(HttpServletRequest request, HttpSession session, HttpServletResponse response,
										  @RequestParam long id, @RequestParam long idApp) throws IOException {
		JSONObject json = new JSONObject();
		String status="failed";
		String msg="Failed to delete the file row";
		try{
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			boolean res = fileMonitorDao.deleteDBKFileMonitorRuleById(id,idApp);
			if(res){
				msg="Deleted successfully";
				status="success";
			}

		}catch (Exception e){
			e.printStackTrace();
		}

		json.put(status, msg);
		response.getWriter().println(json);
	}
	
	
	@RequestMapping(value = "/fmApproveChanges", method = RequestMethod.GET)
	public void fmApproveChanges(HttpServletRequest request, HttpSession session, HttpServletResponse response,
					@RequestParam long idApp) throws IOException {
		JSONObject json = new JSONObject();
		String status="failed";
		String msg="Failed to approval rule changes";
		try{
			// Delete data from actual table
			validationCheckDAO.deleteDBkFileMonitoringRules(idApp);
			
			// Copy  the data from staging to actual table
			validationCheckDAO.copyDbkFileMonitorRulesFromStaging(idApp);
			
			// Delete the data from staging
			validationCheckDAO.deleteStagingDbkFileMonitorRules(idApp);
			
			status="success";
		}catch (Exception e){
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", msg);
		response.getWriter().println(json);
	}
	
	@RequestMapping(value = "/fmRejectChanges", method = RequestMethod.GET)
	public void fmRejectChanges(HttpServletRequest request, HttpSession session, HttpServletResponse response,
					@RequestParam long idApp) throws IOException {
		JSONObject json = new JSONObject();
		String status="failed";
		String msg="Failed to reject rule changes";
		try{
			// Delete the data from staging
			validationCheckDAO.deleteStagingDbkFileMonitorRules(idApp);
			
			status="success";
		}catch (Exception e){
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", msg);
		response.getWriter().println(json);
	}
	
	@RequestMapping(value = "restapi/azureFileTrigger", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String azureFileTrigger(HttpServletRequest request, HttpSession session,
			@RequestBody Map<String,String> requestMap) {

		System.out.println("\n=====> FM Data Processing for azureFileTrigger  - START <=====");
		
		String status = "failure";
		String reason = "";
		
		try {
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			if(isUserValid){
				String  table_or_subfolder_name =null;
				String last_load_time = null;
				String file_name = null;
				Integer file_size = null;
				String account_name = null;
				String container_name = null;
				String folder_path =null;
				String schema_name = null;
				boolean isRequestValid=true;


				if(requestMap != null && requestMap.size()>0) {
					table_or_subfolder_name = requestMap.get("table_or_subfolder_name");
					System.out.println("\n=====> table_or_subfolder_name: "+table_or_subfolder_name);
					if(table_or_subfolder_name ==null || table_or_subfolder_name.trim().isEmpty())
						reason = "Invalid table_or_subfolder_name";

					file_size = Integer.parseInt(requestMap.get("file_size"));
					System.out.println("\n=====> record_count: "+file_size);
					if(file_size ==null || file_size< 0)
						reason = "Invalid record_count";

					last_load_time = requestMap.get("last_load_time");
					System.out.println("\n=====> last_load_time: "+last_load_time);
					if(last_load_time ==null || last_load_time.trim().isEmpty())
						reason = "Invalid last_load_time";

					folder_path = requestMap.get("folder_path");
					System.out.println("\n=====> folder_path: "+folder_path);
					if(folder_path ==null || folder_path.trim().isEmpty())
						reason = "Invalid folder_path";

					file_name = requestMap.get("file_name");
					System.out.println("\n=====> file_name: "+file_name);
					if(file_name ==null || file_name.trim().isEmpty())
						reason = "Invalid file_name";

					account_name = requestMap.get("account_name");
					System.out.println("\n=====> account_name: "+account_name);
					if(account_name ==null || account_name.trim().isEmpty())
						reason = "Invalid account_name";

					container_name = requestMap.get("container_name");
					System.out.println("\n=====> container_name: "+container_name);
					if(container_name ==null || container_name.trim().isEmpty())
						reason = "Invalid container_name";
					schema_name = requestMap.get("schema_name");
					System.out.println("\n=====> schema_name: "+schema_name);
					if(schema_name ==null || schema_name.trim().isEmpty())
						reason = "Invalid schema_name";
				}


				if(reason !=null && !reason.trim().isEmpty())
					isRequestValid=false;


				if(isRequestValid) {
					DbkFMHistory loadToFMhistory = 	new DbkFMHistory();
					loadToFMhistory.setConnection_type("azureDataLakeGen2Batch");
					loadToFMhistory.setTable_or_subfolder_name(table_or_subfolder_name);
					loadToFMhistory.setLast_load_time(last_load_time);
					loadToFMhistory.setLast_altered(last_load_time);
					loadToFMhistory.setFile_name(file_name);
					loadToFMhistory.setAccount_name(account_name);
					loadToFMhistory.setContainer_name(container_name);
					loadToFMhistory.setSchema_name(schema_name);
					loadToFMhistory.setRecord_count(file_size);
					loadToFMhistory.setFolderPath(folder_path);
					loadToFMhistory.setCurrentLoadTime(OffsetDateTime.now(ZoneOffset.UTC));
//					azureDatalakeGen2BatchFileMonitoringService.azureDatalakeGen2Monitoring(loadToFMhistory,session);
                    Map<String, Object> connectionAndValidation = fileMonitorDao.getAzureConnectionByAccountKeyAndContainer(account_name,container_name,schema_name);
                    Long connection_Id = null;
                    Long validation_Id = null;
                    if(connectionAndValidation!=null && !connectionAndValidation.isEmpty()){
                        System.out.println("connection and Validation for "+connectionAndValidation.toString());

                        connection_Id = Long.valueOf((connectionAndValidation.get("idDataSchema")).toString());

                        validation_Id = Long.valueOf((connectionAndValidation.get("idApp")).toString());

                        loadToFMhistory.setConnection_id(connection_Id);
                        loadToFMhistory.setValidation_id(validation_Id);

                        long file_arrival_id= fmService.executeFileMonitoring(loadToFMhistory);
                        if(file_arrival_id > 0) {
							loadToFMhistory.setFile_arrival_id(file_arrival_id);
							fmService.processAzureFMTemplate(loadToFMhistory);
						}

                        status = "success";
                    }else{
                        System.out.println("No connection found for the combination:account:["+account_name+"], container["+container_name+"],folderPath:["+schema_name+"]");
                    }

				}
			}else
				reason="Invalid Authorization";

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in azureFileTrigger API !!");
			reason = "Unexpected error Occurred";
			e.printStackTrace();
		}
		
		JSONObject jobj = new JSONObject();
		jobj.put("status", status);
		jobj.put("message",reason);
		String output = jobj.toString();
		System.out.println(output);
		System.out.println("\n=====> FM Data Processing for azureFileTrigger - END  <=====");
		return output;
	}
	
	@RequestMapping(value = "/restapi/s3FileTrigger", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String s3FileTrigger(HttpServletRequest request, HttpSession session,
			HttpServletResponse response, @RequestBody Map<String, String> requestMap) {

		System.out.println("\n=====> s3FileTrigger - START  <=====");

		String status = "passed";
		String message = "";
		try {
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {

				if (requestMap != null) {
                    boolean isRequestValid=true;

					String table_or_subfolder_name = null;
					Integer file_size = null;
					String last_load_time = null;
					String folder_path = null;
					String fullpath =null;
					String bucketName = null;
					String file_name = null;

					try{
						table_or_subfolder_name = requestMap.get("table_or_subfolder_name");
						System.out.println("\n=====> table_or_subfolder_name: " + table_or_subfolder_name);
						if (table_or_subfolder_name == null || table_or_subfolder_name.trim().isEmpty())
							message = "Invalid table_or_subfolder_name";

						file_size = Integer.parseInt(requestMap.get("fileSize"));
						System.out.println("\n=====> record_count: "+file_size);
						if(file_size ==null || file_size< 0)
							message = "Invalid record_count";

						last_load_time = requestMap.get("fileArrivalTime");
						System.out.println("\n=====> last_load_time: "+last_load_time);
						if(last_load_time ==null || last_load_time.trim().isEmpty())
							message = "Invalid last_load_time";

						folder_path = requestMap.get("folder_path");
						System.out.println("\n=====> folder_path: "+folder_path);
						if(folder_path ==null || folder_path.trim().isEmpty())
							message = "Invalid schema_name";

						file_name = requestMap.get("fileName");
						System.out.println("\n=====> file_name: "+file_name);
						if(file_name ==null || file_name.trim().isEmpty())
							message = "Invalid file_name";

						bucketName = requestMap.get("bucketName");
						System.out.println("\n=====> bucketName: "+bucketName);
						if(bucketName ==null || bucketName.trim().isEmpty())
							message = "Invalid bucketName";

						fullpath = requestMap.get("fullpath");
						System.out.println("\n=====> fullpath: " + fullpath);
						if(fullpath ==null || fullpath.trim().isEmpty())
							message = "Invalid fullpath";

					}catch (Exception e){
						e.printStackTrace();
					}

					if(message !=null && !message.trim().isEmpty())
						isRequestValid=false;

					System.out.println("isRequestValid="+isRequestValid);
					String connectionType= "S3 Batch";

					if(isRequestValid) {
						DbkFMHistory loadToFMhistory = 	new DbkFMHistory();
						loadToFMhistory.setConnection_type(connectionType);
						loadToFMhistory.setTable_or_subfolder_name(table_or_subfolder_name);
						loadToFMhistory.setLast_load_time(last_load_time);
						loadToFMhistory.setLast_altered(last_load_time);
						loadToFMhistory.setFile_name(file_name);
						loadToFMhistory.setBucketName(bucketName);
						loadToFMhistory.setSchema_name(folder_path);
						loadToFMhistory.setFolderPath(fullpath);
						loadToFMhistory.setRecord_count(file_size);
						loadToFMhistory.setCurrentLoadTime(OffsetDateTime.now(ZoneOffset.UTC));
						Map<String, Object> connectionAndValidation = fileMonitorDao.getS3BucketConnectionDetailsByName(bucketName,connectionType,folder_path);

						Long connection_Id = null;
						Long validation_Id = null;

						if(connectionAndValidation!=null && !connectionAndValidation.isEmpty()){
							connection_Id = Long.valueOf((connectionAndValidation.get("idDataSchema")).toString());
							validation_Id = Long.valueOf((connectionAndValidation.get("idApp")).toString());
							loadToFMhistory.setConnection_id(connection_Id);
							loadToFMhistory.setValidation_id(validation_Id);

							long file_arrival_id= fmService.executeFileMonitoring(loadToFMhistory);
							if(file_arrival_id > 0) {
								loadToFMhistory.setFile_arrival_id(file_arrival_id);
								fmService.processS3FMTemplate(loadToFMhistory);
							}
						}else{
							status="failed";
							message ="No connection information found for the combination: bucket:["+bucketName+"] and folder_path:["+folder_path+"]";
						}
					}

				}

			} else {
				status = "failed";
				message = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in s3FileTrigger API !!");
			message = "unexpected exception occurred";
			e.printStackTrace();
		}

		JSONObject jobj = new JSONObject();
		jobj.put("status", status);
		jobj.put("message", message);

		System.out.println(jobj.toString());
		System.out.println("\n=====> s3FileTrigger - END  <=====");
		return jobj.toString();
	}
	
}
