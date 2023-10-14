package com.databuck.filemonitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.datatemplate.S3BatchConnection;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.ITaskService;
import com.databuck.service.NotificationService;
import com.databuck.util.DatabuckFileUtility;
import com.databuck.filemonitoring.FileMonitorExternalFile;

@Service
@EnableScheduling
public class FileMonitoringService {

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private ITaskDAO iTaskDAO;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ITaskService iTaskService;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	private S3BatchConnection s3BatchConnection;

	@Autowired
	private NotificationService NotificationService;

	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	@Autowired
	private DataProfilingTemplateService dataProilingTemplateService;

	/*
	 * private static Map<Long, Map<String, List<String>>> externalPatternFileMap =
	 * new HashMap<Long, Map<String, List<String>>>();
	 */

	// Fixed time every 60 seconds
	@Scheduled(fixedDelay = 60000)
	public void triggerContinuousFileMonitoring() {
		try {
			// Get the list of FileMonitoring applications with continuous Monitoring
			// enabled
			List<ListApplications> fmAppsList = fileMonitorDao.getContinuousFileMonitoringAppsListByType("local");

			if (fmAppsList != null) {
				for (ListApplications listApplications : fmAppsList) {
					long idApp = listApplications.getIdApp();

					// Read the FileMonitor Rules
					List<FileMonitorRules> fileMonitorRules = fileMonitorDao.getFileMonitorRulesByAppId(idApp);

					if (fileMonitorRules != null && fileMonitorRules.size() > 0) {
						// Local FileMonitoring
						processLocalFileMonitoring(idApp, fileMonitorRules);
					}

				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Excetion occurred while processing FileMonitoring ****");
			e.printStackTrace();
		}
	}

	// Fixed time every 60 seconds
	@Scheduled(fixedDelay = 60000)
	public void triggerContinuousS3FileMonitoring() {
		try {
			// Get the list of FileMonitoring applications with continuous Monitoring
			// enabled
			List<ListApplications> fmAppsList = fileMonitorDao.getContinuousFileMonitoringAppsListByType("s3");

			if (fmAppsList != null) {
				for (ListApplications listApplications : fmAppsList) {
					long idApp = listApplications.getIdApp();

					// Read the FileMonitor Rules
					List<FileMonitorRules> fileMonitorRules = fileMonitorDao.getFileMonitorRulesByAppId(idApp);

					if (fileMonitorRules != null && fileMonitorRules.size() > 0) {
						// S3 FileMonitoring
						processS3FileMonitoring(idApp, fileMonitorRules);
					}

				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Excetion occurred while processing S3 FileMonitoring ****");
			e.printStackTrace();
		}
	}

	// Fixed time every 10 minutes
	@Scheduled(fixedDelay = 600000)
	public void triggerContinuousS3IAMRoleFileMonitoring() {
		try {
			// Get the list of FileMonitoring applications with continuous Monitoring
			// enabled
			List<ListApplications> fmAppsList = fileMonitorDao.getContinuousFileMonitoringAppsListByType("s3iamrole");

			if (fmAppsList != null) {
				for (ListApplications listApplications : fmAppsList) {
					long idApp = listApplications.getIdApp();

					// Read the FileMonitor Rules
					List<FileMonitorRules> fileMonitorRules = fileMonitorDao.getFileMonitorRulesByAppId(idApp);

					if (fileMonitorRules != null && fileMonitorRules.size() > 0) {
						// S3 FileMonitoring
						processS3IAMRoleFileMonitoring(idApp, fileMonitorRules);
					}

				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Excetion occurred while processing S3 FileMonitoring ****");
			e.printStackTrace();
		}
	}

	@Async
	@Scheduled(fixedDelay = 60000)
	public void triggerContinuousS3IAMRoleConfigFileMonitoring() {
		try {
			// Get the list of FileMonitoring applications with continuous Monitoring
			// enabled
			List<ListApplications> fmAppsList = fileMonitorDao
					.getContinuousFileMonitoringAppsListByType("s3iamroleconfig");

			if (fmAppsList != null) {
				for (ListApplications listApplications : fmAppsList) {
					long idApp = listApplications.getIdApp();

					// Read the FileMonitor Rules
					List<FileMonitorRules> fileMonitorRules = fileMonitorDao.getFileMonitorRulesByAppId(idApp);

					if (fileMonitorRules != null && fileMonitorRules.size() > 0) {
						// S3 FileMonitoring
						
						processS3IAMRoleFileMonitoringWithConfig(idApp, fileMonitorRules);
					}

				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Excetion occurred while processing S3 FileMonitoring ****");
			e.printStackTrace();
		}
	}

	@Scheduled(fixedDelay = 100000)
	public void executeValidFiles() {
		try {
			List<FileTrackingHistory> fth_list = fileMonitorDao.getValidUnProcessedFilesFromHistory();
			if (fth_list != null) {
				for (FileTrackingHistory fileTrackingHistory : fth_list) {
					updateTemplateAndTriggerValidation(fileTrackingHistory);
				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Excetion occurred while executing valid files ****");
			e.printStackTrace();
		}
	}

	/**
	 * This method to process local File Monitoring
	 * 
	 * @param idApp
	 * @throws Exception
	 */
	public void processLocalFileMonitoring(long idApp, List<FileMonitorRules> fileMonitorRules) throws Exception {

		try {
			// Get Execution Date
			Date execDate = new Date(); // current date
			DateTime dt = new DateTime(); // current time

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(execDate);
			int cur_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			String cur_month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			int cur_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int cur_dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			String cur_hourOfDay = String.valueOf(dt.getHourOfDay());
			int cur_minuteOfHour = dt.getMinuteOfHour();

			// Get TrackingDate
			String curDateStr = dateFormat.format(execDate);
			Date trackingDate = null;
			String trackingDateStr = "";
			for (FileMonitorRules fmRule : fileMonitorRules) {

				int dayOfYear = cur_dayOfYear;

				// Get currentRun
				int currentRun = 1;
				String month = cur_month;
				int dayOfMonth = cur_dayOfMonth;
				int dayOfWeek = cur_dayOfWeek;
				String hourOfDay = cur_hourOfDay;
				trackingDate = dateFormat.parse(curDateStr);
				trackingDateStr = curDateStr;

				// Current Hour and Minutes
				int fHour = Integer.parseInt(cur_hourOfDay);
				int fMinutes = cur_minuteOfHour;

				// Rule Hour and minutes
				String[] ruleHourTokens = fmRule.getTimeOfCheck().split(":");
				int ruleHour = Integer.parseInt(ruleHourTokens[0]);
				int ruleMinutes = Integer.parseInt(ruleHourTokens[1]);

				boolean istimeOfCheckExceeded = false;
				int daysDiff = 0;

				switch (fmRule.getFrequency().toLowerCase()) {
				case "weekly":
					/*
					 * For Weekly rule check if the DayOfCheck and TimeOfCheck are matching to
					 * currentDate else the tracking date and time to DayOfCheck in Next Calendar
					 * week
					 */
					if (cur_dayOfWeek != fmRule.getDayOfCheck() || fHour > ruleHour
							|| (fHour == ruleHour && fMinutes > ruleMinutes)) {
						// Next day of week of the rule
						int cur_ruleDiff = cur_dayOfWeek - fmRule.getDayOfCheck();
						if (cur_ruleDiff < 0) {
							daysDiff = Math.abs(cur_ruleDiff);
						} else {
							daysDiff = 7 - (Math.abs(cur_ruleDiff));
						}
						istimeOfCheckExceeded = true;
					}
					break;
				case "daily":
					/*
					 * For Daily rule check if the TimeOfCheck is less than or equal to current time
					 * else the tracking date and time to Next Calendar day
					 */
					if (fHour > ruleHour || (fHour == ruleHour && fMinutes > ruleMinutes)) {
						daysDiff = 1;
						istimeOfCheckExceeded = true;
					}
					break;
				}

				if (istimeOfCheckExceeded) {
					Calendar nextCal = Calendar.getInstance();
					nextCal.add(Calendar.DAY_OF_MONTH, daysDiff);
					dayOfYear = nextCal.get(Calendar.DAY_OF_YEAR);
					month = nextCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
					dayOfMonth = nextCal.get(Calendar.DAY_OF_MONTH);
					dayOfWeek = nextCal.get(Calendar.DAY_OF_WEEK);
					hourOfDay = String.valueOf(nextCal.get(Calendar.HOUR_OF_DAY));

					Date nextDayOfCheck = nextCal.getTime();
					String nextDayOfCheckStr = dateFormat.format(nextDayOfCheck);
					trackingDate = dateFormat.parse(nextDayOfCheckStr);
					trackingDateStr = nextDayOfCheckStr;
				}

				String folderPath = fmRule.getFolderPath();

				String filePattern = fmRule.getFilePattern();

				// Get average Sum of files from history
				long avgFileSize = fileMonitorDao.getAvgFileSizeOfRule(idApp, fmRule.getId());

				int matchedFileCount = 0;
				long totalFileSize = 0l;

				// Check if the directory or not
				File rootDir = new File(folderPath);

				Date fm_lastProcessedTime = new Date();

				if (rootDir.isDirectory() && rootDir.exists()) {

					// Get lastProcessedTime of the rule
					Date lastProcessedTime = fmRule.getLastProcessedDate();

					// If last processed time is null then take current Date and time
					if (lastProcessedTime == null) {
						lastProcessedTime = dateFormat.parse(curDateStr);
					}

					// Get the list of files based on pattern
					// should pass list of patterns
					Collection<File> fileList = FileUtils.listFiles(rootDir, new WildcardFileFilter(filePattern), null);

					// Updating lastProcessedTime after file list fetching
					fm_lastProcessedTime = new Date();

					// Ignore .head and .control files from list
					List<File> filteredFileList = new ArrayList<File>();
					for (File r_file : fileList) {
						if (r_file.getName().endsWith(".control") || r_file.getName().endsWith(".head")) {
							continue;
						} else {
							filteredFileList.add(r_file);
						}
					}

					for (File l_file : filteredFileList) {
						String fileName = l_file.getName();

						Date fileArrivalDateTime = new Date(l_file.lastModified());

						// Check if file arrived after last processing time
						if (l_file.lastModified() > lastProcessedTime.getTime()) {

							System.out.println("\n\n=======****=========> Processing FileMonitorRule Id:["
									+ fmRule.getId() + "] - File <=======****=========");
							System.out.println("\n====> CurrentRun: " + currentRun);
							System.out.println("\n====> File name: " + fileName);
							System.out.println("\n====> Folder Path: " + folderPath);
							System.out.println("\n====> File Pattern: " + filePattern);
							System.out.println("\n====> LastProcessingTime of Rule: " + lastProcessedTime);
							System.out.println("\n====> LastProcessingTime of Rule (milliseconds): "
									+ lastProcessedTime.getTime());
							System.out.println("\n====> LastModifiedTime of File: " + fileArrivalDateTime);
							System.out.println(
									"\n====> LastModifiedTime of File (milliseconds): " + l_file.lastModified());

							// Increment the match file count
							++matchedFileCount;

							// Get fileSize
							long fileSize = l_file.length();
							System.out.println("\n====> File size: " + fileSize);
							totalFileSize = totalFileSize + fileSize;

							// Add details to History table
							FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
							fileTrackingHistory.setIdApp(idApp);
							fileTrackingHistory.setFileMonitorRuleId(fmRule.getId());
							fileTrackingHistory.setFolderPath(folderPath);
							fileTrackingHistory.setFileName(fileName);
							fileTrackingHistory.setFileArrivalDate(fileArrivalDateTime);
							fileTrackingHistory.setDate(trackingDate);
							fileTrackingHistory.setRun(currentRun);
							fileTrackingHistory.setDayOfYear(dayOfYear);
							fileTrackingHistory.setMonth(month);
							fileTrackingHistory.setDayOfMonth(dayOfMonth);
							fileTrackingHistory.setDayOfWeek(dayOfWeek);
							fileTrackingHistory.setHourOfDay(hourOfDay);
							fileTrackingHistory.setFileSize(fileSize);
							fileTrackingHistory.setFileExecutionStatus("unprocessed");
							fileTrackingHistory.setStatus("failed");
							fileTrackingHistory.setZeroSizeFileCheck(fileSize > 0 ? "passed" : "failed");

							boolean sendEmailAlert = true;

							// Identify the template related to FileName
							System.out.println("\n====> Identifying the template Id for the Rule ..");
							ListDataSource listDataSource = null;
							if (fmRule.getIdDataSchema() != null && fmRule.getIdDataSchema() != 0l) {
								listDataSource = fileMonitorDao
										.getLatestDataSourceForIdDataSchema(fmRule.getIdDataSchema());
							} else {
								listDataSource = fileMonitorDao.getDataSourceForFilePathAndPattern(folderPath,
										filePattern);
							}

							long idData = 0l;
							if (listDataSource != null) {
								idData = listDataSource.getIdData();
								long idDataSchema = listDataSource.getIdDataSchema();
								System.out.println("\n====> Template Id:" + idData);
								System.out.println("\n====> Schema Id:" + idDataSchema);

								// Set idData i.e., templateId to FileTrackingHistory
								fileTrackingHistory.setIdData(idData);

								System.out.println("\n====> Fetching ListDataSchema details ..");
								List<ListDataSchema> listDataSchema_list = listDataSourceDao
										.getListDataSchemaForIdDataSchema(idDataSchema);

								if (listDataSchema_list != null && listDataSchema_list.size() > 0) {
									ListDataSchema listDataSchema = listDataSchema_list.get(0);

									// Perform checks
									System.out.println("\n====> Performing file level checks ..");
									FileCheckRules fileCheckRules = fileFormatRulesValidation(l_file, listDataSchema,
											idData);

									if (fileCheckRules != null) {
										fileTrackingHistory.setFileFormat(fileCheckRules.getFileFormat());
										fileTrackingHistory.setStatus(fileCheckRules.getIsFileValid());
										fileTrackingHistory.setZeroSizeFileCheck(fileCheckRules.getZeroSizeFile());
										fileTrackingHistory.setRecordLengthCheck(fileCheckRules.getRecordLengthCheck());
										fileTrackingHistory.setRecordMaxLengthCheck(fileCheckRules.getRecordMaxLengthCheck());
										fileTrackingHistory.setColumnCountCheck(fileCheckRules.getColumnCountCheck());
										fileTrackingHistory
												.setColumnSequenceCheck(fileCheckRules.getColumnSequenceCheck());

										if (fileCheckRules.getIsFileValid() != null
												&& fileCheckRules.getIsFileValid().equalsIgnoreCase("passed")) {
											sendEmailAlert = false;
										}
									}
								}
							} else {
								System.out.println(
										"\n====> Failed to find valid template Id for the rule, hence can't perform file level checks !!");
							}

							// Insert into FileTrackingHistory
							fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);

							// Send FileMonitor SQS Alert
							sendFileMonitorSQSAlert(fileTrackingHistory, folderPath, fileName, idData);

							if (sendEmailAlert) {
								sendFileMonitorEmailAlert(fileTrackingHistory, folderPath, fileName);
							}

							System.out.println("\n=======****=========> Process File -  End   <=======****=========");

						}
					}
				}

				// Get the existing FileTrackingSummary
				FileTrackingSummary fileTrackingSummary = fileMonitorDao.getLatestFileTrackingSummaryForRule(idApp,
						fmRule.getId(), trackingDateStr);

				String fileSizeStatus = "";

				if (fileTrackingSummary == null) {
					// Since the date is changed here, we need to alert user about the rule summary
					// alert user about the failed file

					// Get the MaxDate of the Rule
					Date maxDate = fileMonitorDao.getMaxDateForRule(idApp, fmRule.getId());

					if (maxDate != null) {
						String maxDateStr = dateFormat.format(maxDate);

						FileTrackingSummary previous_FileTrackingSmry = fileMonitorDao
								.getLatestFileTrackingSummaryForRule(idApp, fmRule.getId(), maxDateStr);

						int totalCount = fmRule.getFileCount();
						int arr_fileCount = previous_FileTrackingSmry.getFileCount();
						int missingFileCount = (totalCount >= arr_fileCount) ? (totalCount - arr_fileCount) : 0;
						int dupFileCount = (arr_fileCount > totalCount) ? (arr_fileCount - totalCount) : 0;

						// Send Notification
						String DayOfCheck = (fmRule.getFrequency().equalsIgnoreCase("weekly"))
								? "" + fmRule.getDayOfCheck()
								: "";
						sendFileMonitorEmailForSuccess(fmRule.getId(), maxDateStr, fmRule.getFrequency(), DayOfCheck,
								fmRule.getTimeOfCheck(), folderPath, fmRule.getFilePattern(), totalCount, arr_fileCount,
								missingFileCount, dupFileCount);

					}
					// Preparing the FileTrackingSummary
					fileTrackingSummary = new FileTrackingSummary();
					fileTrackingSummary.setIdApp(idApp);
					fileTrackingSummary.setDate(trackingDate);
					fileTrackingSummary.setRun(currentRun);
					fileTrackingSummary.setDayOfYear(dayOfYear);
					fileTrackingSummary.setMonth(month);
					fileTrackingSummary.setDayOfMonth(dayOfMonth);
					fileTrackingSummary.setDayOfWeek(dayOfWeek);
					fileTrackingSummary.setHourOfDay(hourOfDay);
					fileTrackingSummary.setFileMonitorRules(fmRule);
					fileTrackingSummary.setFileCount(0);
				} else {
					fileSizeStatus = fileTrackingSummary.getFileSizeStatus();
				}

				// Calculate fileSizeStatus
				if (matchedFileCount > 0 && avgFileSize != 0l) {

					System.out.println("\n====> Calculating the fileSizeStatus ....");

					System.out.println("Average File Size from history : " + avgFileSize);

					long thresholdSize = (avgFileSize * fmRule.getFileSizeThreshold()) / 100;

					long maxSize = avgFileSize + thresholdSize;
					System.out.println("MaxSize: " + maxSize);

					long minSize = avgFileSize - thresholdSize;
					System.out.println("MinSize: " + minSize);

					System.out.println("Total File Size : " + totalFileSize);

					fileSizeStatus = (totalFileSize >= minSize && totalFileSize <= maxSize) ? "passed" : "failed";

					System.out.println("\n====> File Size Status : " + fileSizeStatus);
				}
				fileTrackingSummary.setFileSizeStatus(fileSizeStatus);

				matchedFileCount = matchedFileCount + fileTrackingSummary.getFileCount();
				fileTrackingSummary.setFileCount(matchedFileCount);
				String countStatus = (fmRule.getFileCount() == matchedFileCount) ? "passed" : "failed";
				fileTrackingSummary.setCountStatus(countStatus);
				fileTrackingSummary.setLastUpdateTimeStamp(new Date());

				fileMonitorDao.addFileTrackingSummary(fileTrackingSummary);

				// Update lastProcessingDate
				fileMonitorDao.updateFMRuleLastProcessingTime(idApp, fmRule.getId(), fm_lastProcessedTime);

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while processing FileMonitoring ****");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * This method to process S3 IAM Role File Monitoring
	 * 
	 * @param idApp
	 * @throws Exception
	 */
	public void processS3IAMRoleFileMonitoring(long idApp, List<FileMonitorRules> fileMonitorRules) throws Exception {

		try {
			// Get Execution Date
			Date execDate = new Date(); // current date
			DateTime dt = new DateTime(); // current time

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(execDate);
			int cur_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			String cur_month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			int cur_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int cur_dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			String cur_hourOfDay = String.valueOf(dt.getHourOfDay());
			int cur_minuteOfHour = dt.getMinuteOfHour();

			// Get TrackingDate
			String curDateStr = dateFormat.format(execDate);
			Date trackingDate = null;
			String trackingDateStr = "";
			for (FileMonitorRules fmRule : fileMonitorRules) {

				int dayOfYear = cur_dayOfYear;

				// Get currentRun
				int currentRun = 1;
				String month = cur_month;
				int dayOfMonth = cur_dayOfMonth;
				int dayOfWeek = cur_dayOfWeek;
				String hourOfDay = cur_hourOfDay;
				trackingDate = dateFormat.parse(curDateStr);
				trackingDateStr = curDateStr;

				// Current Hour and Minutes
				int fHour = Integer.parseInt(cur_hourOfDay);
				int fMinutes = cur_minuteOfHour;

				// Rule Hour and minutes
				String[] ruleHourTokens = fmRule.getTimeOfCheck().split(":");
				int ruleHour = Integer.parseInt(ruleHourTokens[0]);
				int ruleMinutes = Integer.parseInt(ruleHourTokens[1]);

				boolean istimeOfCheckExceeded = false;
				int daysDiff = 0;

				switch (fmRule.getFrequency().toLowerCase()) {
				case "weekly":
					/*
					 * For Weekly rule check if the DayOfCheck and TimeOfCheck are matching to
					 * currentDate else the tracking date and time to DayOfCheck in Next Calendar
					 * week
					 */
					if (cur_dayOfWeek != fmRule.getDayOfCheck() || fHour > ruleHour
							|| (fHour == ruleHour && fMinutes > ruleMinutes)) {
						// Next day of week of the rule
						int cur_ruleDiff = cur_dayOfWeek - fmRule.getDayOfCheck();
						if (cur_ruleDiff < 0) {
							daysDiff = Math.abs(cur_ruleDiff);
						} else {
							daysDiff = 7 - (Math.abs(cur_ruleDiff));
						}
						istimeOfCheckExceeded = true;
					}
					break;
				case "daily":
					/*
					 * For Daily rule check if the TimeOfCheck is less than or equal to current time
					 * else the tracking date and time to Next Calendar day
					 */
					if (fHour > ruleHour || (fHour == ruleHour && fMinutes > ruleMinutes)) {
						daysDiff = 1;
						istimeOfCheckExceeded = true;
					}
					break;
				}

				if (istimeOfCheckExceeded) {
					Calendar nextCal = Calendar.getInstance();
					nextCal.add(Calendar.DAY_OF_MONTH, daysDiff);
					dayOfYear = nextCal.get(Calendar.DAY_OF_YEAR);
					month = nextCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
					dayOfMonth = nextCal.get(Calendar.DAY_OF_MONTH);
					dayOfWeek = nextCal.get(Calendar.DAY_OF_WEEK);
					hourOfDay = String.valueOf(nextCal.get(Calendar.HOUR_OF_DAY));

					Date nextDayOfCheck = nextCal.getTime();
					String nextDayOfCheckStr = dateFormat.format(nextDayOfCheck);
					trackingDate = dateFormat.parse(nextDayOfCheckStr);
					trackingDateStr = nextDayOfCheckStr;
				}

				String folderPath = fmRule.getFolderPath();

				String filePattern = fmRule.getFilePattern();

				// Get average Sum of files from history
				long avgFileSize = fileMonitorDao.getAvgFileSizeOfRule(idApp, fmRule.getId());

				int matchedFileCount = 0;
				long totalFileSize = 0l;

				Date fm_lastProcessedTime = new Date();

				// Get lastProcessedTime of the rule
				Date lastProcessedTime = fmRule.getLastProcessedDate();

				// If last processed time is null then take current Date and time
				if (lastProcessedTime == null) {
					lastProcessedTime = dateFormat.parse(curDateStr);
				}

				String bucketName = fmRule.getBucketName();
				String partitionedFolders = fmRule.getPartitionedFolders();
				int maxFolderDepth = fmRule.getMaxFolderDepth();

				// Get the list of files based on pattern
				// should pass list of patterns
				List<String> filteredFileList = s3BatchConnection.getListOfTableNamesInFolderForS3IAMRole(bucketName,
						folderPath, filePattern, partitionedFolders, maxFolderDepth);

				// Updating lastProcessedTime after file list fetching
				fm_lastProcessedTime = new Date();

				for (String fileName : filteredFileList) {

					String pfullPath = (bucketName + "/" + folderPath + "/" + fileName).replace("//", "/");
					pfullPath = "s3://" + pfullPath;

					String f_awsCliCommand = "aws s3 ls " + pfullPath;

					Process p_child = Runtime.getRuntime().exec(f_awsCliCommand);

					BufferedReader fbr = new BufferedReader(new InputStreamReader(p_child.getInputStream()));
					String f_line = null;

					String fileArrivalDateTime = "";
					String f_fileSize = "";
					while ((f_line = fbr.readLine()) != null) {

						if (!f_line.endsWith("/")) {
							f_line = f_line.replaceAll("\\t", " ").replaceAll("   ", " ").replaceAll("  ", " ");

							String[] fieldsList = f_line.split("\\s");
							fileArrivalDateTime = fieldsList[0] + " " + fieldsList[1];

							for (int i = 2; i < fieldsList.length; ++i) {
								if (fieldsList[i] != null && !fieldsList[i].trim().isEmpty()) {
									f_fileSize = fieldsList[i];
									break;
								}
							}
						}

						break;
					}

					fbr.close();

					// Get the last modified of file
					Date dt_fileArrivalDateTime = null;
					try {
						dt_fileArrivalDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.parse(fileArrivalDateTime.trim());
					} catch (Exception e) {
						System.out.println("\n====> Exception occurred while converting fileArrivalDateTime of file:["
								+ pfullPath + "] !!");
						System.out.println("\n====> fileArrivalDateTime: " + fileArrivalDateTime);
						e.printStackTrace();
					}

					// Check if file arrived after last processing time
					if (dt_fileArrivalDateTime != null
							&& dt_fileArrivalDateTime.getTime() > lastProcessedTime.getTime()) {

						System.out.println("\n\n=======****=========> Processing FileMonitorRule Id:[" + fmRule.getId()
								+ "] - File <=======****=========");
						System.out.println("\n====> CurrentRun: " + currentRun);
						System.out.println("\n====> File name: " + fileName);
						System.out.println("\n====> Folder Path: " + folderPath);
						System.out.println("\n====> File Pattern: " + filePattern);
						System.out.println("\n====> LastProcessingTime of Rule: " + lastProcessedTime);
						System.out.println(
								"\n====> LastProcessingTime of Rule (milliseconds): " + lastProcessedTime.getTime());
						System.out.println("\n====> LastModifiedTime of File: " + fileArrivalDateTime);
						System.out.println(
								"\n====> LastModifiedTime of File (milliseconds): " + dt_fileArrivalDateTime.getTime());

						// Increment the match file count
						++matchedFileCount;

						// Get fileSize
						System.out.println("\n====> f_fileSize: " + f_fileSize);
						Long fileSize = 0l;
						try {
							fileSize = Long.parseLong(f_fileSize);
						} catch (Exception e) {

						}
						System.out.println("\n====> File size: " + fileSize);

						totalFileSize = totalFileSize + fileSize;

						// Add details to History table
						FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
						fileTrackingHistory.setIdApp(idApp);
						fileTrackingHistory.setFileMonitorRuleId(fmRule.getId());
						fileTrackingHistory.setBucketName(bucketName);
						fileTrackingHistory.setFolderPath(folderPath);
						fileTrackingHistory.setFileName(fileName);
						fileTrackingHistory.setFileArrivalDate(dt_fileArrivalDateTime);
						fileTrackingHistory.setDate(trackingDate);
						fileTrackingHistory.setRun(currentRun);
						fileTrackingHistory.setDayOfYear(dayOfYear);
						fileTrackingHistory.setMonth(month);
						fileTrackingHistory.setDayOfMonth(dayOfMonth);
						fileTrackingHistory.setDayOfWeek(dayOfWeek);
						fileTrackingHistory.setHourOfDay(hourOfDay);
						fileTrackingHistory.setFileSize(fileSize);
						fileTrackingHistory.setFileExecutionStatus("unprocessed");
						fileTrackingHistory.setStatus("failed");
						fileTrackingHistory.setZeroSizeFileCheck(fileSize > 0 ? "passed" : "failed");

						boolean sendEmailAlert = true;

						// Identify the connection associated with the fileMonitoring rule
						System.out.println("\n====> Identifying the connection associated with the Rule ..");

						Long idDataSchema = 0l;
						if (fmRule.getIdDataSchema() != null && fmRule.getIdDataSchema() != 0l) {
							// Check if connection is active
							boolean isConActive = fileMonitorDao.isListDataSchemaActive(fmRule.getIdDataSchema());
							if (isConActive)
								idDataSchema = fmRule.getIdDataSchema();

						} else {
							idDataSchema = fileMonitorDao.getS3IAMConnectionForFilePathAndPattern(bucketName,
									folderPath, filePattern, partitionedFolders);
						}

						Long idData = 0l;
						if (idDataSchema != null && idDataSchema != 0l) {

							System.out.println("\n====> Schema Id:" + idDataSchema);

							System.out.println("\n====> Fetching ListDataSchema details ..");
							List<ListDataSchema> listDataSchema_list = listDataSourceDao
									.getListDataSchemaForIdDataSchema(idDataSchema);

							if (listDataSchema_list != null && listDataSchema_list.size() > 0) {
								ListDataSchema listDataSchema = listDataSchema_list.get(0);

								// Identify the template related to FileName
								System.out.println("\n====> Identifying the template Id for the Rule ..");

								String multiPattern = listDataSchema.getMultiPattern();

								System.out.println("partitionedFolders: " + partitionedFolders);
								System.out.println("multiPattern: " + multiPattern);

								String childFilePattern = null;
								String subFolderName = null;

								// When partitioned folders and multi Pattern both are enabled
								if (partitionedFolders != null && partitionedFolders.equalsIgnoreCase("Y")
										&& multiPattern != null && multiPattern.equalsIgnoreCase("Y")) {

									// Identify FileName
									String fullFileName = fileName.replaceAll("//", "/");
									String[] fileNameTokens = fullFileName.split("/");
									String actualFileName = "";
									if (fileNameTokens.length > 1) {
										for (int i = 0; i < fileNameTokens.length - 1; ++i) {
											String fToken = fileNameTokens[i];

											if (fToken != null && !fToken.trim().isEmpty()) {
												subFolderName = (subFolderName != null
														&& !subFolderName.trim().isEmpty())
																? (subFolderName + "/" + fToken)
																: fToken;
											}
										}
										actualFileName = fileNameTokens[fileNameTokens.length - 1];
									} else {
										// if sub-folder is not found reading it from folder path
										String[] subFolderFromFolderPath = folderPath.split("/");
										subFolderName = subFolderFromFolderPath[subFolderFromFolderPath.length - 1];
										actualFileName = fileNameTokens[0];
									}

									System.out.println("\n====> actualFileName: " + actualFileName);
									System.out.println("\n====> subFolderName: " + subFolderName);

									// Identify childPattern
									String externalfileNamePattern = listDataSchema.getExtenalFileNamePattern();

									Map<Long, Map<String, List<String>>> externalPatternFileMap = new HashMap<Long, Map<String, List<String>>>();

									if (externalfileNamePattern != null
											&& externalfileNamePattern.trim().equalsIgnoreCase("Y")) {
										String externalPatternfilePath = listDataSchema.getExtenalFileName();
										String filePatternColumn = listDataSchema.getPatternColumn();

										Map<String, List<String>> ex_patternList = databuckFileUtility
												.getPatternsListFromExternalFileOld(externalPatternfilePath,
														filePatternColumn);

										System.out.println("\n=====> External pattern details for the connection["
												+ idDataSchema + "] : " + ex_patternList);

										if (ex_patternList != null && ex_patternList.get(subFolderName) != null) {
											List<String> f_patternList = ex_patternList.get(subFolderName);
											for (String fileNamePattern : f_patternList) {
												if (databuckFileUtility.isPatternMatchedWithRegex(actualFileName,
														fileNamePattern)) {
													childFilePattern = fileNamePattern;
													break;
												}
												;
											}
										}

									} else {
										childFilePattern = identifyChildPattern(listDataSchema, actualFileName);
									}

									if (childFilePattern != null && !childFilePattern.trim().isEmpty()) {

										// Identify a unique name from child pattern to create template and schema names
										String uniqueName = childFilePattern.replaceAll("\\*", "");
										uniqueName = uniqueName.replace("\\\\d+", "");

										// local variables to create connection and template
										String dataTemplateName = "templ_" + listDataSchema.getSchemaName() + "_"
												+ uniqueName;
										String datalocation = "S3 IAMRole Batch";
										String fileDataFormat = listDataSchema.getFileDataFormat();
										String headerPresent = listDataSchema.getHeaderPresent();
										long idUser = listDataSchema.getCreatedBy();
										long projectId = listDataSchema.getProjectId();
										String createdByUser = listDataSchema.getCreatedByUser();

										// Fetch the template Id based on subfolder and child pattern
										idData = fileMonitorDao.getTemplateIdByPattern(idDataSchema, subFolderName,
												childFilePattern);

										if (idData == null || idData <= 0l) {

											System.out
													.println("\n====> TemplateId not found. creating new template !!");

											// Create new template
											CompletableFuture<Long> trgtResult = dataProilingTemplateService
													.createDataTemplate(null, idDataSchema, datalocation, fileName,
															dataTemplateName, "", "", headerPresent, "", "", "N", "",
															null, "", "", "", "", idUser, "", "", fileDataFormat, "",
															"", "", null, "", "", "N", null, projectId, "N",
															createdByUser, "N", null, "", childFilePattern,
															subFolderName);

											idData = (trgtResult != null && trgtResult.get() != null
													&& trgtResult.get() != 0l) ? trgtResult.get() : 0l;

											if (idData != null && idData > 0l) {
												// Save the templateId, subFolder and Pattern
												fileMonitorDao.saveTemplateMultiPatternInfo(idDataSchema, idData,
														subFolderName, childFilePattern);

												// Trigger the template
												dataProilingTemplateService.triggerDataTemplate(idData, datalocation,
														"N", "N");
											}
										} else {
											// Check if the template is active or not
											boolean isTemplateActive = fileMonitorDao.isTemplateActive(idData);
											if (!isTemplateActive) {
												System.out.println(
														"\n====> TemplateId is inactive. creating new template !!");
												// Create new template
												CompletableFuture<Long> trgtResult = dataProilingTemplateService
														.createDataTemplate(null, idDataSchema, datalocation, fileName,
																dataTemplateName, "", "", headerPresent, "", "", "N",
																"", null, "", "", "", "", idUser, "", "",
																fileDataFormat, "", "", "", null, "", "", "N", null,
																projectId, "N", createdByUser, "N", null, "",
																childFilePattern, subFolderName);

												idData = (trgtResult != null && trgtResult.get() != null
														&& trgtResult.get() != 0l) ? trgtResult.get() : 0l;

												if (idData != null && idData > 0l) {
													// update the templateId, subFolder and Pattern
													fileMonitorDao.updateTemplateIdInMultiPatternInfo(idDataSchema,
															subFolderName, childFilePattern, idData);

													// Trigger the template
													dataProilingTemplateService.triggerDataTemplate(idData,
															datalocation, "N", "N");
												}
											}
										}

									} else {
										System.out.println(
												"\n====> Failed to identify pattern, can't proceed further !!");
									}

								} else {
									ListDataSource listDataSource = fileMonitorDao
											.getLatestDataSourceForIdDataSchema(fmRule.getIdDataSchema());
									if (listDataSource != null) {
										idData = (long) listDataSource.getIdData();
									}
								}

								System.out.println("\n====> Template Id: " + idData);

								if (idData != null && idData > 0l) {

									// Perform checks
									System.out.println("\n====> Performing file level checks ..");
									FileCheckRules fileCheckRules = s3IAMRoleFileFormatRulesValidation(bucketName,
											folderPath, fileName, listDataSchema, idData, fileSize, childFilePattern,
											subFolderName,false,null);

									if (fileCheckRules != null) {
										fileTrackingHistory.setFileFormat(fileCheckRules.getFileFormat());
										fileTrackingHistory.setStatus(fileCheckRules.getIsFileValid());
										fileTrackingHistory.setZeroSizeFileCheck(fileCheckRules.getZeroSizeFile());
										fileTrackingHistory.setRecordLengthCheck(fileCheckRules.getRecordLengthCheck());
										fileTrackingHistory.setRecordMaxLengthCheck(fileCheckRules.getRecordMaxLengthCheck());
										fileTrackingHistory.setColumnCountCheck(fileCheckRules.getColumnCountCheck());
										fileTrackingHistory
												.setColumnSequenceCheck(fileCheckRules.getColumnSequenceCheck());

										if (fileCheckRules.getIsFileValid() != null
												&& fileCheckRules.getIsFileValid().equalsIgnoreCase("passed")) {
											sendEmailAlert = false;
										}
									}

									// Set idData i.e., templateId to FileTrackingHistory
									fileTrackingHistory.setIdData(idData);

								} else {
									System.out.println(
											"\n====> Failed to find valid template Id for the rule, hence can't perform file level checks !!");
								}

							} else {
								System.out.println(
										"\n====> Failed to fetch connection details, hence can't perform file level checks !!");
							}

						} else {
							System.out.println(
									"\n====> Failed to find valid connection for the rule, hence can't perform file level checks !!");
						}

						// Insert into FileTrackingHistory
						fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);

						// Send FileMonitor SQS Alert
						sendFileMonitorSQSAlert(fileTrackingHistory, bucketName + "/" + folderPath, fileName, idData);

						if (sendEmailAlert) {
							sendFileMonitorEmailAlert(fileTrackingHistory, folderPath, fileName);
						}

						System.out.println("\n=======****=========> Process File -  End   <=======****=========");

					}
				}

				// Get the existing FileTrackingSummary
				FileTrackingSummary fileTrackingSummary = fileMonitorDao.getLatestFileTrackingSummaryForRule(idApp,
						fmRule.getId(), trackingDateStr);

				String fileSizeStatus = "";

				if (fileTrackingSummary == null) {
					// Since the date is changed here, we need to alert user about the rule summary
					// alert user about the failed file

					// Get the MaxDate of the Rule
					Date maxDate = fileMonitorDao.getMaxDateForRule(idApp, fmRule.getId());

					if (maxDate != null) {
						String maxDateStr = dateFormat.format(maxDate);

						FileTrackingSummary previous_FileTrackingSmry = fileMonitorDao
								.getLatestFileTrackingSummaryForRule(idApp, fmRule.getId(), maxDateStr);

						int totalCount = fmRule.getFileCount();
						int arr_fileCount = previous_FileTrackingSmry.getFileCount();
						int missingFileCount = (totalCount >= arr_fileCount) ? (totalCount - arr_fileCount) : 0;
						int dupFileCount = (arr_fileCount > totalCount) ? (arr_fileCount - totalCount) : 0;

						// Send Notification
						String DayOfCheck = (fmRule.getFrequency().equalsIgnoreCase("weekly"))
								? "" + fmRule.getDayOfCheck()
								: "";
						sendFileMonitorEmailForSuccess(fmRule.getId(), maxDateStr, fmRule.getFrequency(), DayOfCheck,
								fmRule.getTimeOfCheck(), folderPath, fmRule.getFilePattern(), totalCount, arr_fileCount,
								missingFileCount, dupFileCount);
					}
					// Preparing the FileTrackingSummary
					fileTrackingSummary = new FileTrackingSummary();
					fileTrackingSummary.setIdApp(idApp);
					fileTrackingSummary.setDate(trackingDate);
					fileTrackingSummary.setRun(currentRun);
					fileTrackingSummary.setDayOfYear(dayOfYear);
					fileTrackingSummary.setMonth(month);
					fileTrackingSummary.setDayOfMonth(dayOfMonth);
					fileTrackingSummary.setDayOfWeek(dayOfWeek);
					fileTrackingSummary.setHourOfDay(hourOfDay);
					fileTrackingSummary.setFileMonitorRules(fmRule);
					fileTrackingSummary.setFileCount(0);
				} else {
					fileSizeStatus = fileTrackingSummary.getFileSizeStatus();
				}

				// Calculate fileSizeStatus
				if (matchedFileCount > 0 && avgFileSize != 0l) {

					System.out.println("\n====> Calculating the fileSizeStatus of all the files arrived....");

					System.out.println("Average File Size from history : " + avgFileSize);

					long thresholdSize = (avgFileSize * fmRule.getFileSizeThreshold()) / 100;

					long maxSize = avgFileSize + thresholdSize;
					System.out.println("MaxSize: " + maxSize);

					long minSize = avgFileSize - thresholdSize;
					System.out.println("MinSize: " + minSize);

					System.out.println("Total File Size : " + totalFileSize);

					fileSizeStatus = (totalFileSize >= minSize && totalFileSize <= maxSize) ? "passed" : "failed";

					System.out.println("\n====> File Size Status : " + fileSizeStatus);
				}
				fileTrackingSummary.setFileSizeStatus(fileSizeStatus);

				matchedFileCount = matchedFileCount + fileTrackingSummary.getFileCount();
				fileTrackingSummary.setFileCount(matchedFileCount);
				String countStatus = (fmRule.getFileCount() == matchedFileCount) ? "passed" : "failed";
				fileTrackingSummary.setCountStatus(countStatus);
				fileTrackingSummary.setLastUpdateTimeStamp(new Date());

				fileMonitorDao.addFileTrackingSummary(fileTrackingSummary);

				// Update lastProcessingDate
				fileMonitorDao.updateFMRuleLastProcessingTime(idApp, fmRule.getId(), fm_lastProcessedTime);

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while processing FileMonitoring ****");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * This method to process S3 IAM Role File Monitoring
	 * 
	 * @param idApp
	 * @throws Exception
	 */
	
	public void processS3IAMRoleFileMonitoringWithConfig(long idApp, List<FileMonitorRules> fileMonitorRules)
			throws Exception {

		try {
			// Get Execution Date
			Date execDate = new Date(); // current date
			DateTime dt = new DateTime(); // current time

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(execDate);
			int cur_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			String cur_month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			int cur_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int cur_dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			String cur_hourOfDay = String.valueOf(dt.getHourOfDay());
			int cur_minuteOfHour = dt.getMinuteOfHour();

			// Get TrackingDate
			String curDateStr = dateFormat.format(execDate);
			Date trackingDate = null;
			String trackingDateStr = "";
			for (FileMonitorRules fmRule : fileMonitorRules) {

				int dayOfYear = cur_dayOfYear;

				// Get currentRun
				int currentRun = 1;
				String month = cur_month;
				int dayOfMonth = cur_dayOfMonth;
				int dayOfWeek = cur_dayOfWeek;
				String hourOfDay = cur_hourOfDay;
				trackingDate = dateFormat.parse(curDateStr);
				trackingDateStr = curDateStr;

				// Current Hour and Minutes
				int fHour = Integer.parseInt(cur_hourOfDay);
				int fMinutes = cur_minuteOfHour;

				// Rule Hour and minutes
				String[] ruleHourTokens = fmRule.getTimeOfCheck().split(":");
				int ruleHour = Integer.parseInt(ruleHourTokens[0]);
				int ruleMinutes = Integer.parseInt(ruleHourTokens[1]);

				boolean istimeOfCheckExceeded = false;
				int daysDiff = 0;

				switch (fmRule.getFrequency().toLowerCase()) {
				case "weekly":
					/*
					 * For Weekly rule check if the DayOfCheck and TimeOfCheck are matching to
					 * currentDate else the tracking date and time to DayOfCheck in Next Calendar
					 * week
					 */
					if (cur_dayOfWeek != fmRule.getDayOfCheck() || fHour > ruleHour
							|| (fHour == ruleHour && fMinutes > ruleMinutes)) {
						// Next day of week of the rule
						int cur_ruleDiff = cur_dayOfWeek - fmRule.getDayOfCheck();
						if (cur_ruleDiff < 0) {
							daysDiff = Math.abs(cur_ruleDiff);
						} else {
							daysDiff = 7 - (Math.abs(cur_ruleDiff));
						}
						istimeOfCheckExceeded = true;
					}
					break;
				case "daily":
					/*
					 * For Daily rule check if the TimeOfCheck is less than or equal to current time
					 * else the tracking date and time to Next Calendar day
					 */
					if (fHour > ruleHour || (fHour == ruleHour && fMinutes > ruleMinutes)) {
						daysDiff = 1;
						istimeOfCheckExceeded = true;
					}
					break;
				}

				if (istimeOfCheckExceeded) {
					Calendar nextCal = Calendar.getInstance();
					nextCal.add(Calendar.DAY_OF_MONTH, daysDiff);
					dayOfYear = nextCal.get(Calendar.DAY_OF_YEAR);
					month = nextCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
					dayOfMonth = nextCal.get(Calendar.DAY_OF_MONTH);
					dayOfWeek = nextCal.get(Calendar.DAY_OF_WEEK);
					hourOfDay = String.valueOf(nextCal.get(Calendar.HOUR_OF_DAY));

					Date nextDayOfCheck = nextCal.getTime();
					String nextDayOfCheckStr = dateFormat.format(nextDayOfCheck);
					trackingDate = dateFormat.parse(nextDayOfCheckStr);
					trackingDateStr = nextDayOfCheckStr;
				}

				String folderPath = fmRule.getFolderPath();

				String filePattern = fmRule.getFilePattern();

				// Get average Sum of files from history
				long avgFileSize = fileMonitorDao.getAvgFileSizeOfRule(idApp, fmRule.getId());

				int matchedFileCount = 0;
				long totalFileSize = 0l;

				Date fm_lastProcessedTime = new Date();

				// Get lastProcessedTime of the rule
				Date lastProcessedTime = fmRule.getLastProcessedDate();

				// If last processed time is null then take current Date and time
				if (lastProcessedTime == null || lastProcessedTime.toString().isEmpty() || lastProcessedTime.toString().length() == 0) {
					
					lastProcessedTime = dateFormat.parse(curDateStr);
				}

				String bucketName = fmRule.getBucketName();
				String partitionedFolders = fmRule.getPartitionedFolders();
				int maxFolderDepth = fmRule.getMaxFolderDepth();

				// Get the list of files based on pattern
				// should pass list of patterns
				List<String> filteredFileList = s3BatchConnection.getListOfTableNamesInFolderForS3IAMRole(bucketName,
						folderPath, filePattern, partitionedFolders, maxFolderDepth);
				List<String> filteredFileListFinale = new ArrayList<String>();
				for (String fileName : filteredFileList) {
					if(fileName.trim() != "0" && !fileName.trim().equalsIgnoreCase("0") && !fileName.trim().endsWith("/0") ) {
						filteredFileListFinale.add(fileName);
						}
					}
				
				
				// Updating lastProcessedTime after file list fetching
				fm_lastProcessedTime = new Date();
				

				for (String fileName : filteredFileListFinale) {

					String pfullPath = (bucketName + "/" + folderPath + "/" + fileName).replace("//", "/");
					pfullPath = "s3://" + pfullPath;

					String f_awsCliCommand = "aws s3 ls " + pfullPath;

					Process p_child = Runtime.getRuntime().exec(f_awsCliCommand);

					BufferedReader fbr = new BufferedReader(new InputStreamReader(p_child.getInputStream()));
					String f_line = null;

					String fileArrivalDateTime = "";
					String f_fileSize = "";
					while ((f_line = fbr.readLine()) != null) {

						if (!f_line.endsWith("/")) {
							f_line = f_line.replaceAll("\\t", " ").replaceAll("   ", " ").replaceAll("  ", " ");

							String[] fieldsList = f_line.split("\\s");
							fileArrivalDateTime = fieldsList[0] + " " + fieldsList[1];

							for (int i = 2; i < fieldsList.length; ++i) {
								if (fieldsList[i] != null && !fieldsList[i].trim().isEmpty()) {
									f_fileSize = fieldsList[i];
									break;
								}
							}
						}

						break;
					}

					fbr.close();

					// Get the last modified of file
					Date dt_fileArrivalDateTime = null;
					try {
						dt_fileArrivalDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.parse(fileArrivalDateTime.trim());
					} catch (Exception e) {
						System.out.println("\n====> Exception occurred while converting fileArrivalDateTime of file:["
								+ pfullPath + "] !!");
						System.out.println("\n====> fileArrivalDateTime: " + fileArrivalDateTime);
						e.printStackTrace();
					}

					// Check if file arrived after last processing time
					if (dt_fileArrivalDateTime != null
							&& dt_fileArrivalDateTime.getTime() > lastProcessedTime.getTime()) {

						System.out.println("\n\n=======****=========> Processing FileMonitorRule Id:[" + fmRule.getId()
								+ "] - File <=======****=========");
						System.out.println("\n====> CurrentRun: " + currentRun);
						System.out.println("\n====> File name: " + fileName);
						System.out.println("\n====> Folder Path: " + folderPath);
						System.out.println("\n====> File Pattern: " + filePattern);
						System.out.println("\n====> LastProcessingTime of Rule: " + lastProcessedTime);
						System.out.println(
								"\n====> LastProcessingTime of Rule (milliseconds): " + lastProcessedTime.getTime());
						System.out.println("\n====> LastModifiedTime of File: " + fileArrivalDateTime);
						System.out.println(
								"\n====> LastModifiedTime of File (milliseconds): " + dt_fileArrivalDateTime.getTime());

						// Increment the match file count
						++matchedFileCount;

						// Get fileSize
						System.out.println("\n====> f_fileSize: " + f_fileSize);
						Long fileSize = 0l;
						try {
							fileSize = Long.parseLong(f_fileSize);
						} catch (Exception e) {

						}
						System.out.println("\n====> File size: " + fileSize);

						totalFileSize = totalFileSize + fileSize;

						// Add details to History table
						FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
						fileTrackingHistory.setIdApp(idApp);
						fileTrackingHistory.setFileMonitorRuleId(fmRule.getId());
						fileTrackingHistory.setBucketName(bucketName);
						fileTrackingHistory.setFolderPath(folderPath);
						fileTrackingHistory.setFileName(fileName);
						fileTrackingHistory.setFileArrivalDate(dt_fileArrivalDateTime);
						fileTrackingHistory.setDate(trackingDate);
						fileTrackingHistory.setRun(currentRun);
						fileTrackingHistory.setDayOfYear(dayOfYear);
						fileTrackingHistory.setMonth(month);
						fileTrackingHistory.setDayOfMonth(dayOfMonth);
						fileTrackingHistory.setDayOfWeek(dayOfWeek);
						fileTrackingHistory.setHourOfDay(hourOfDay);
						fileTrackingHistory.setFileSize(fileSize);
						fileTrackingHistory.setFileExecutionStatus("unprocessed");
						fileTrackingHistory.setStatus("failed");
						fileTrackingHistory.setZeroSizeFileCheck(fileSize > 0 ? "passed" : "failed");

						boolean sendEmailAlert = true;

						// Identify the connection associated with the fileMonitoring rule
						System.out.println("\n====> Identifying the connection associated with the Rule ..");

						Long idDataSchema = 0l;
						if (fmRule.getIdDataSchema() != null && fmRule.getIdDataSchema() != 0l) {
							// Check if connection is active
							boolean isConActive = fileMonitorDao.isListDataSchemaActive(fmRule.getIdDataSchema());
							if (isConActive)
								idDataSchema = fmRule.getIdDataSchema();

						} else {
							idDataSchema = fileMonitorDao.getS3IAMConnectionForFilePathAndPattern(bucketName,
									folderPath, filePattern, partitionedFolders);
						}

						Long idData = 0l;
						if (idDataSchema != null && idDataSchema != 0l) {

							System.out.println("\n====> Schema Id:" + idDataSchema);

							System.out.println("\n====> Fetching ListDataSchema details ..");
							List<ListDataSchema> listDataSchema_list = listDataSourceDao
									.getListDataSchemaForIdDataSchema(idDataSchema);

							if (listDataSchema_list != null && listDataSchema_list.size() > 0) {
								ListDataSchema listDataSchema = listDataSchema_list.get(0);

								// Identify the template related to FileName
								System.out.println("\n====> Identifying the template Id for the Rule ..");

								String multiPattern = listDataSchema.getMultiPattern();
								
								//Added for EDI files
								String xsltFileName="";
								boolean xsltFileFound=false;
								
								
								System.out.println("partitionedFolders: " + partitionedFolders);
								System.out.println("multiPattern: " + multiPattern);

								String childFilePattern = null;
								String subFolderName = null;

								// When partitioned folders and multi Pattern both are enabled
								if (partitionedFolders != null && partitionedFolders.equalsIgnoreCase("Y")
										&& multiPattern != null && multiPattern.equalsIgnoreCase("Y")) {

									// Identify FileName
									String fullFileName = fileName.replaceAll("//", "/");
									String[] fileNameTokens = fullFileName.split("/");
									String actualFileName = "";
									if (fileNameTokens.length > 1) {
										for (int i = 0; i < fileNameTokens.length - 1; ++i) {
											String fToken = fileNameTokens[i];

											if (fToken != null && !fToken.trim().isEmpty()) {
												subFolderName = (subFolderName != null
														&& !subFolderName.trim().isEmpty())
																? (subFolderName + "/" + fToken)
																: fToken;
											}
										}
										actualFileName = fileNameTokens[fileNameTokens.length - 1];
									} else {
										// if sub-folder is not found reading it from folder path
										String[] subFolderFromFolderPath = folderPath.split("/");
										subFolderName = subFolderFromFolderPath[subFolderFromFolderPath.length - 1];
										actualFileName = fileNameTokens[0];
									}

									System.out.println("\n====> actualFileName: " + actualFileName);
									System.out.println("\n====> subFolderName: " + subFolderName);

									// Identify childPattern
									String externalfileNamePattern = listDataSchema.getExtenalFileNamePattern();

									Map<Long, Map<String, List<String>>> externalPatternFileMap = new HashMap<Long, Map<String, List<String>>>();
									String fileDataFormatFromMappingFile = "default";

									if (externalfileNamePattern != null
											&& externalfileNamePattern.trim().equalsIgnoreCase("Y")) {
										String externalPatternfilePath = listDataSchema.getExtenalFileName();
										String filePatternColumn = listDataSchema.getPatternColumn();

										List<FileMonitorExternalFile> ex_patternList = databuckFileUtility
												.getPatternsListFromExternalFile(externalPatternfilePath,
														filePatternColumn);

										System.out.println("\n=====> External pattern details for the connection["
												+ idDataSchema + "] : " + ex_patternList);

										/*if (ex_patternList != null && ex_patternList.get(subFolderName) != null) {
											List<String> f_patternList = ex_patternList.get(subFolderName);*/

											for (FileMonitorExternalFile fileMonitorExternalFile : ex_patternList) {
												//String[] fp = fileNamePattern.split("=");
												if (fileMonitorExternalFile.getSubFolderName().contains(subFolderName)) {
													if (databuckFileUtility.isPatternMatchedWithRegex(actualFileName,
															fileMonitorExternalFile.getFilePattern())) {
														childFilePattern = fileMonitorExternalFile.getFilePattern();
														if (fileMonitorExternalFile.getFieldSeparator() != null) {
															fileDataFormatFromMappingFile = formatBasedOnSeparator(fileMonitorExternalFile.getFieldSeparator().trim());
														}
														if (fileMonitorExternalFile.getXsltFileName() != null) {
															xsltFileName = fileMonitorExternalFile.getXsltFileName().trim();
														}
														break;
													}
												}

											}
										//}

									} else {
										childFilePattern = identifyChildPattern(listDataSchema, actualFileName);
									}
									
									if (!xsltFileName.equalsIgnoreCase("NOT-AVAILABLE") && xsltFileName.trim().length() > 0)										
									{
										xsltFileFound=true;
									}

									if (childFilePattern != null && !childFilePattern.trim().isEmpty()) {

										// Identify a unique name from child pattern to create template and schema names
										String uniqueName = childFilePattern.replaceAll("\\*", "");
										uniqueName = uniqueName.replace("\\\\d+", "");

										// local variables to create connection and template
										String dataTemplateName = "templ_" + listDataSchema.getSchemaName() + "_"
												+ uniqueName;
										String datalocation = "S3 IAMRole Batch Config";
										String fileDataFormat = listDataSchema.getFileDataFormat();
										System.out.println("\n=====fileDataFormate------------->"+fileDataFormatFromMappingFile);
										if(!fileDataFormatFromMappingFile.equalsIgnoreCase("default")) {
											fileDataFormat=fileDataFormatFromMappingFile;
										}
										String headerPresent = listDataSchema.getHeaderPresent();
										long idUser = listDataSchema.getCreatedBy();
										long projectId = listDataSchema.getProjectId();
										String createdByUser = listDataSchema.getCreatedByUser();
										

										// Fetch the template Id based on subfolder and child pattern
										idData = fileMonitorDao.getTemplateIdByPattern(idDataSchema, subFolderName,
												childFilePattern);

										if (idData == null || idData <= 0l) {

											System.out
													.println("\n====> TemplateId not found. creating new template !!");

											// Create new template
											CompletableFuture<Long> trgtResult = dataProilingTemplateService
													.createDataTemplate(null, idDataSchema, datalocation, fileName,
															dataTemplateName, "", "", headerPresent, "", "", "N", "",
															null, "", "", "", "", idUser, "", "", fileDataFormat, "",
															"", "", null, "", "", "Y", null, projectId, "Y",
															createdByUser, "N", null, "", childFilePattern,
															subFolderName);

											idData = (trgtResult != null && trgtResult.get() != null
													&& trgtResult.get() != 0l) ? trgtResult.get() : 0l;

											if (idData != null && idData > 0l) {
												// Save the templateId, subFolder and Pattern
												fileMonitorDao.saveTemplateMultiPatternInfo(idDataSchema, idData,
														subFolderName, childFilePattern);

												// Trigger the template
												dataProilingTemplateService.triggerDataTemplate(idData, datalocation,
														"N", "N");
											}
										} else {
											// Check if the template is active or not
											boolean isTemplateActive = fileMonitorDao.isTemplateActive(idData);
											if (!isTemplateActive) {
												System.out.println(
														"\n====> TemplateId is inactive. creating new template !!");
												// Create new template
												CompletableFuture<Long> trgtResult = dataProilingTemplateService
														.createDataTemplate(null, idDataSchema, datalocation, fileName,
																dataTemplateName, "", "", headerPresent, "", "", "N",
																"", null, "", "", "", "", idUser, "", "",
																fileDataFormat, "", "", "", null, "", "", "Y", null,
																projectId, "Y", createdByUser, "N", null, "",
																childFilePattern, subFolderName);

												idData = (trgtResult != null && trgtResult.get() != null
														&& trgtResult.get() != 0l) ? trgtResult.get() : 0l;

												if (idData != null && idData > 0l) {
													// update the templateId, subFolder and Pattern
													fileMonitorDao.updateTemplateIdInMultiPatternInfo(idDataSchema,
															subFolderName, childFilePattern, idData);

													// Trigger the template
													dataProilingTemplateService.triggerDataTemplate(idData,
															datalocation, "N", "N");
												}
											}
										}
										
									

									} else {
										System.out.println(
												"\n====> Failed to identify pattern, can't proceed further !!");
										String msg = "\n====> Failed to identify pattern, can't proceed further !!";
										sendFileMonitorEmailAlertForFailedPatter(msg,fileName);
									}

								} else {
									ListDataSource listDataSource = fileMonitorDao
											.getLatestDataSourceForIdDataSchema(fmRule.getIdDataSchema());
									if (listDataSource != null) {
										idData = (long) listDataSource.getIdData();
									}
								}

								System.out.println("\n====> Template Id: " + idData);

								if (idData != null && idData > 0l) {
																		

									// Perform checks
									System.out.println("\n====> Performing file level checks ..");
									FileCheckRules fileCheckRules = s3IAMRoleFileFormatRulesValidation(bucketName,
											folderPath, fileName, listDataSchema, idData, fileSize, childFilePattern,
											subFolderName, xsltFileFound, xsltFileName);

									if (fileCheckRules != null) {
										fileTrackingHistory.setFileFormat(fileCheckRules.getFileFormat());
										fileTrackingHistory.setStatus(fileCheckRules.getIsFileValid());
										fileTrackingHistory.setZeroSizeFileCheck(fileCheckRules.getZeroSizeFile());
										fileTrackingHistory.setRecordLengthCheck(fileCheckRules.getRecordLengthCheck());
										fileTrackingHistory.setRecordMaxLengthCheck(fileCheckRules.getRecordMaxLengthCheck());
										fileTrackingHistory.setColumnCountCheck(fileCheckRules.getColumnCountCheck());
										fileTrackingHistory
												.setColumnSequenceCheck(fileCheckRules.getColumnSequenceCheck());

										if (fileCheckRules.getIsFileValid() != null
												&& fileCheckRules.getIsFileValid().equalsIgnoreCase("passed")) {
											sendEmailAlert = false;
										}
									}

									// Set idData i.e., templateId to FileTrackingHistory
									fileTrackingHistory.setIdData(idData);

								} else {
									System.out.println(
											"\n====> Failed to find valid template Id for the rule, hence can't perform file level checks !!");
								}

							} else {
								System.out.println(
										"\n====> Failed to fetch connection details, hence can't perform file level checks !!");
							}

						} else {
							System.out.println(
									"\n====> Failed to find valid connection for the rule, hence can't perform file level checks !!");
						}

						// Insert into FileTrackingHistory
						fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);

						// Send FileMonitor SQS Alert
						sendFileMonitorSQSAlert(fileTrackingHistory, bucketName + "/" + folderPath, fileName, idData);

						if (sendEmailAlert) {
							sendFileMonitorEmailAlert(fileTrackingHistory, folderPath, fileName);
						}

						System.out.println("\n=======****=========> Process File -  End   <=======****=========");

					}
				}

				// Get the existing FileTrackingSummary
				FileTrackingSummary fileTrackingSummary = fileMonitorDao.getLatestFileTrackingSummaryForRule(idApp,
						fmRule.getId(), trackingDateStr);

				String fileSizeStatus = "";

				if (fileTrackingSummary == null) {
					// Since the date is changed here, we need to alert user about the rule summary
					// alert user about the failed file

					// Get the MaxDate of the Rule
					Date maxDate = fileMonitorDao.getMaxDateForRule(idApp, fmRule.getId());

					if (maxDate != null) {
						String maxDateStr = dateFormat.format(maxDate);

						FileTrackingSummary previous_FileTrackingSmry = fileMonitorDao
								.getLatestFileTrackingSummaryForRule(idApp, fmRule.getId(), maxDateStr);

						int totalCount = fmRule.getFileCount();
						int arr_fileCount = previous_FileTrackingSmry.getFileCount();
						int missingFileCount = (totalCount >= arr_fileCount) ? (totalCount - arr_fileCount) : 0;
						int dupFileCount = (arr_fileCount > totalCount) ? (arr_fileCount - totalCount) : 0;

						// Send Notification
						String DayOfCheck = (fmRule.getFrequency().equalsIgnoreCase("weekly"))
								? "" + fmRule.getDayOfCheck()
								: "";
						sendFileMonitorEmailForSuccess(fmRule.getId(), maxDateStr, fmRule.getFrequency(), DayOfCheck,
								fmRule.getTimeOfCheck(), folderPath, fmRule.getFilePattern(), totalCount, arr_fileCount,
								missingFileCount, dupFileCount);
					}
					// Preparing the FileTrackingSummary
					fileTrackingSummary = new FileTrackingSummary();
					fileTrackingSummary.setIdApp(idApp);
					fileTrackingSummary.setDate(trackingDate);
					fileTrackingSummary.setRun(currentRun);
					fileTrackingSummary.setDayOfYear(dayOfYear);
					fileTrackingSummary.setMonth(month);
					fileTrackingSummary.setDayOfMonth(dayOfMonth);
					fileTrackingSummary.setDayOfWeek(dayOfWeek);
					fileTrackingSummary.setHourOfDay(hourOfDay);
					fileTrackingSummary.setFileMonitorRules(fmRule);
					fileTrackingSummary.setFileCount(0);
				} else {
					fileSizeStatus = fileTrackingSummary.getFileSizeStatus();
				}

				// Calculate fileSizeStatus
				if (matchedFileCount > 0 && avgFileSize != 0l) {

					System.out.println("\n====> Calculating the fileSizeStatus of all the files arrived....");

					System.out.println("Average File Size from history : " + avgFileSize);

					long thresholdSize = (avgFileSize * fmRule.getFileSizeThreshold()) / 100;

					long maxSize = avgFileSize + thresholdSize;
					System.out.println("MaxSize: " + maxSize);

					long minSize = avgFileSize - thresholdSize;
					System.out.println("MinSize: " + minSize);

					System.out.println("Total File Size : " + totalFileSize);

					fileSizeStatus = (totalFileSize >= minSize && totalFileSize <= maxSize) ? "passed" : "failed";

					System.out.println("\n====> File Size Status : " + fileSizeStatus);
				}
				fileTrackingSummary.setFileSizeStatus(fileSizeStatus);

				matchedFileCount = matchedFileCount + fileTrackingSummary.getFileCount();
				fileTrackingSummary.setFileCount(matchedFileCount);
				String countStatus = (fmRule.getFileCount() == matchedFileCount) ? "passed" : "failed";
				fileTrackingSummary.setCountStatus(countStatus);
				fileTrackingSummary.setLastUpdateTimeStamp(new Date());

				fileMonitorDao.addFileTrackingSummary(fileTrackingSummary);

				// Update lastProcessingDate
				fileMonitorDao.updateFMRuleLastProcessingTime(idApp, fmRule.getId(), fm_lastProcessedTime);

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while processing FileMonitoring ****");
			e.printStackTrace();
			throw e;
		}

	}
	
	private void sendFileMonitorEmailAlertForFailedPatter(String msg, String fileName) {
		HashMap<String, String> oTokens = new HashMap<>();
		oTokens.put("User", "User");
		oTokens.put("msg", msg);
		oTokens.put("FileName", fileName);
		NotificationService.SendNotification("FILE_MONITORING_PROCESS_FAILED", oTokens, null);
		
	}

	private String formatBasedOnSeparator(String Separator) {
		String fileFormat = "";
		if (Separator.equalsIgnoreCase("Comma")) {
			fileFormat="CSV";
		}else if(Separator.equalsIgnoreCase("Pipe")) {
			fileFormat="PSV";
		}else {
			fileFormat="CSV";
		}
		return fileFormat;
	}

	/**
	 * This method to process S3 File Monitoring
	 * 
	 * @param idApp
	 * @throws Exception
	 */
	public void processS3FileMonitoring(long idApp, List<FileMonitorRules> fileMonitorRules) throws Exception {
		try {
			// Get Execution Date
			Date execDate = new Date(); // current date
			DateTime dt = new DateTime(); // current time

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(execDate);
			int cur_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			String cur_month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			int cur_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int cur_dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			String cur_hourOfDay = String.valueOf(dt.getHourOfDay());
			int cur_minuteOfHour = dt.getMinuteOfHour();

			// Get TrackingDate
			String curDateStr = dateFormat.format(execDate);
			Date trackingDate = null;
			String trackingDateStr = "";
			for (FileMonitorRules fmRule : fileMonitorRules) {

				int dayOfYear = cur_dayOfYear;

				// Get currentRun
				int currentRun = 1;
				String month = cur_month;
				int dayOfMonth = cur_dayOfMonth;
				int dayOfWeek = cur_dayOfWeek;
				String hourOfDay = cur_hourOfDay;
				trackingDate = dateFormat.parse(curDateStr);
				trackingDateStr = curDateStr;

				// Current Hour and Minutes
				int fHour = Integer.parseInt(cur_hourOfDay);
				int fMinutes = cur_minuteOfHour;

				// Rule Hour and minutes
				String[] ruleHourTokens = fmRule.getTimeOfCheck().split(":");
				int ruleHour = Integer.parseInt(ruleHourTokens[0]);
				int ruleMinutes = Integer.parseInt(ruleHourTokens[1]);

				boolean istimeOfCheckExceeded = false;
				int daysDiff = 0;

				switch (fmRule.getFrequency().toLowerCase()) {
				case "weekly":
					/*
					 * For Weekly rule check if the DayOfCheck and TimeOfCheck are matching to
					 * currentDate else the tracking date and time to DayOfCheck in Next Calendar
					 * week
					 */
					if (cur_dayOfWeek != fmRule.getDayOfCheck() || fHour > ruleHour
							|| (fHour == ruleHour && fMinutes > ruleMinutes)) {
						// Next day of week of the rule
						int cur_ruleDiff = cur_dayOfWeek - fmRule.getDayOfCheck();
						if (cur_ruleDiff < 0) {
							daysDiff = Math.abs(cur_ruleDiff);
						} else {
							daysDiff = 7 - (Math.abs(cur_ruleDiff));
						}
						istimeOfCheckExceeded = true;
					}
					break;
				case "daily":
					/*
					 * For Daily rule check if the TimeOfCheck is less than or equal to current time
					 * else the tracking date and time to Next Calendar day
					 */
					if (fHour > ruleHour || (fHour == ruleHour && fMinutes > ruleMinutes)) {
						daysDiff = 1;
						istimeOfCheckExceeded = true;
					}
					break;
				}

				if (istimeOfCheckExceeded) {
					Calendar nextCal = Calendar.getInstance();
					nextCal.add(Calendar.DAY_OF_MONTH, daysDiff);
					dayOfYear = nextCal.get(Calendar.DAY_OF_YEAR);
					month = nextCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
					dayOfMonth = nextCal.get(Calendar.DAY_OF_MONTH);
					dayOfWeek = nextCal.get(Calendar.DAY_OF_WEEK);
					hourOfDay = String.valueOf(nextCal.get(Calendar.HOUR_OF_DAY));

					Date nextDayOfCheck = nextCal.getTime();
					String nextDayOfCheckStr = dateFormat.format(nextDayOfCheck);
					trackingDate = dateFormat.parse(nextDayOfCheckStr);
					trackingDateStr = nextDayOfCheckStr;
				}

				String folderPath = fmRule.getFolderPath();

				String filePattern = fmRule.getFilePattern();

				// Get average Sum of files from history
				long avgFileSize = fileMonitorDao.getAvgFileSizeOfRule(idApp, fmRule.getId());

				int matchedFileCount = 0;
				long totalFileSize = 0l;

				// Get lastProcessedTime of the rule
				Date lastProcessedTime = fmRule.getLastProcessedDate();

				// If last processed time is null then take current Date and time
				if (lastProcessedTime == null) {
					lastProcessedTime = dateFormat.parse(curDateStr);
				}

				Date fm_lastProcessedTime = new Date();

				// Get the connection details associated with the rule
				long idDataSchema = fmRule.getIdDataSchema();

				List<ListDataSchema> listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);

				if (listDataSchema != null && listDataSchema.size() > 0) {
					String accessKey = listDataSchema.get(0).getAccessKey();
					String secretKey = listDataSchema.get(0).getSecretKey();
					String bucketName = listDataSchema.get(0).getBucketName();

					// Get the list of files based on pattern
					// should pass list of patterns
					AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

					ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName)
							.withPrefix(folderPath).withDelimiter("/");

					ListObjectsV2Result listing = s3Client.listObjectsV2(req);

					List<S3ObjectSummary> fileList = listing.getObjectSummaries();

					// Updating lastProcessedTime after file list fetching
					fm_lastProcessedTime = new Date();

					for (S3ObjectSummary summary : fileList) {
						String fileName = summary.getKey();
						fileName = fileName.replace(folderPath, "");

						if (!fileName.equals(folderPath)) {

							boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, filePattern);

							if (patternMatched) {
								Date fileArrivalDateTime = summary.getLastModified();

								// Check if file arrived after last processing time
								if (fileArrivalDateTime.getTime() > lastProcessedTime.getTime()) {

									System.out.println("\n\n=======****=========> Processing FileMonitorRule Id:["
											+ fmRule.getId() + "] - File <=======****=========");
									System.out.println("\n====> CurrentRun: " + currentRun);
									System.out.println("\n====> File name: " + fileName);
									System.out.println("\n====> Folder Path: " + folderPath);
									System.out.println("\n====> File Pattern: " + filePattern);
									System.out.println("\n====> FileArrivalDateTime: " + fileArrivalDateTime);
									System.out.println("\n====> LastProcessingTime of Rule: " + lastProcessedTime);

									// Increment the match file count
									++matchedFileCount;

									// Get fileSize
									long fileSize = summary.getSize();
									System.out.println("\n====> File size: " + fileSize);
									totalFileSize = totalFileSize + fileSize;

									// Add details to History table
									FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
									fileTrackingHistory.setIdApp(idApp);
									fileTrackingHistory.setFileMonitorRuleId(fmRule.getId());
									fileTrackingHistory.setFolderPath(folderPath);
									fileTrackingHistory.setFileName(fileName);
									fileTrackingHistory.setFileArrivalDate(fileArrivalDateTime);
									fileTrackingHistory.setDate(trackingDate);
									fileTrackingHistory.setRun(currentRun);
									fileTrackingHistory.setDayOfYear(dayOfYear);
									fileTrackingHistory.setMonth(month);
									fileTrackingHistory.setDayOfMonth(dayOfMonth);
									fileTrackingHistory.setDayOfWeek(dayOfWeek);
									fileTrackingHistory.setHourOfDay(hourOfDay);
									fileTrackingHistory.setFileSize(fileSize);
									fileTrackingHistory.setFileExecutionStatus("unprocessed");
									fileTrackingHistory.setStatus("failed");
									fileTrackingHistory.setZeroSizeFileCheck(fileSize > 0 ? "passed" : "failed");

									boolean sendEmailAlert = true;

									// Identify the template related to FileName
									System.out.println("\n====> Identifying the template Id for the Rule ..");
									ListDataSource listDataSource = fileMonitorDao
											.getLatestDataSourceForIdDataSchema(idDataSchema);

									long idData = 0l;
									if (listDataSource != null) {
										idData = listDataSource.getIdData();
										System.out.println("\n====> Template Id:" + idData);
										System.out.println("\n====> Schema Id:" + idDataSchema);

										// Set idData i.e., templateId to FileTrackingHistory
										fileTrackingHistory.setIdData(idData);

										// Perform checks
										System.out.println("\n====> Performing file level checks ..");
										FileCheckRules fileCheckRules = s3FileFormatRulesValidation(s3Client, summary,
												fileName, listDataSchema.get(0), idData);

										if (fileCheckRules != null) {
											fileTrackingHistory.setBucketName(fmRule.getBucketName());
											fileTrackingHistory.setFileFormat(fileCheckRules.getFileFormat());
											fileTrackingHistory.setStatus(fileCheckRules.getIsFileValid());
											fileTrackingHistory.setZeroSizeFileCheck(fileCheckRules.getZeroSizeFile());
											fileTrackingHistory
													.setRecordLengthCheck(fileCheckRules.getRecordLengthCheck());
											fileTrackingHistory
											.setRecordMaxLengthCheck(fileCheckRules.getRecordMaxLengthCheck());
											fileTrackingHistory
													.setColumnCountCheck(fileCheckRules.getColumnCountCheck());
											fileTrackingHistory
													.setColumnSequenceCheck(fileCheckRules.getColumnSequenceCheck());

											if (fileCheckRules.getIsFileValid() != null
													&& fileCheckRules.getIsFileValid().equalsIgnoreCase("passed")) {
												sendEmailAlert = false;
											}
										}
									} else {
										System.out.println(
												"\n====> Failed to find valid template Id for the rule, hence can't perform file level checks !!");
									}

									// Insert into FileTrackingHistory
									fileMonitorDao.addFileTrackingHistoryRequest(fileTrackingHistory);

									// Send FileMonitor SQS Alert
									sendFileMonitorSQSAlert(fileTrackingHistory, bucketName + "/" + folderPath,
											fileName, idData);

									if (sendEmailAlert) {
										sendFileMonitorEmailAlert(fileTrackingHistory, folderPath, fileName);
									}

									System.out.println(
											"\n=======****=========> Process File -  End   <=======****=========");

								}
							}
						}
					}
				}

				// Get the existing FileTrackingSummary
				FileTrackingSummary fileTrackingSummary = fileMonitorDao.getLatestFileTrackingSummaryForRule(idApp,
						fmRule.getId(), trackingDateStr);

				String fileSizeStatus = "";

				if (fileTrackingSummary == null) {
					// Since the date is changed here, we need to alert user about the rule summary
					// alert user about the failed file

					// Get the MaxDate of the Rule
					Date maxDate = fileMonitorDao.getMaxDateForRule(idApp, fmRule.getId());

					if (maxDate != null) {
						String maxDateStr = dateFormat.format(maxDate);

						FileTrackingSummary previous_FileTrackingSmry = fileMonitorDao
								.getLatestFileTrackingSummaryForRule(idApp, fmRule.getId(), maxDateStr);

						int totalCount = fmRule.getFileCount();
						int arr_fileCount = previous_FileTrackingSmry.getFileCount();
						int missingFileCount = (totalCount >= arr_fileCount) ? (totalCount - arr_fileCount) : 0;
						int dupFileCount = (arr_fileCount > totalCount) ? (arr_fileCount - totalCount) : 0;

						// Send Notification
						String DayOfCheck = (fmRule.getFrequency().equalsIgnoreCase("weekly"))
								? "" + fmRule.getDayOfCheck()
								: "";
						sendFileMonitorEmailForSuccess(fmRule.getId(), maxDateStr, fmRule.getFrequency(), DayOfCheck,
								fmRule.getTimeOfCheck(), folderPath, fmRule.getFilePattern(), totalCount, arr_fileCount,
								missingFileCount, dupFileCount);
					}
					// Preparing the FileTrackingSummary
					fileTrackingSummary = new FileTrackingSummary();
					fileTrackingSummary.setIdApp(idApp);
					fileTrackingSummary.setDate(trackingDate);
					fileTrackingSummary.setRun(currentRun);
					fileTrackingSummary.setDayOfYear(dayOfYear);
					fileTrackingSummary.setMonth(month);
					fileTrackingSummary.setDayOfMonth(dayOfMonth);
					fileTrackingSummary.setDayOfWeek(dayOfWeek);
					fileTrackingSummary.setHourOfDay(hourOfDay);
					fileTrackingSummary.setFileMonitorRules(fmRule);
					fileTrackingSummary.setFileCount(0);
				} else {
					fileSizeStatus = fileTrackingSummary.getFileSizeStatus();
				}

				// Calculate fileSizeStatus
				if (matchedFileCount > 0 && avgFileSize != 0l) {

					System.out.println("\n====> Calculating the fileSizeStatus ....");

					System.out.println("Average File Size from history : " + avgFileSize);

					long thresholdSize = (avgFileSize * fmRule.getFileSizeThreshold()) / 100;

					long maxSize = avgFileSize + thresholdSize;
					System.out.println("MaxSize: " + maxSize);

					long minSize = avgFileSize - thresholdSize;
					System.out.println("MinSize: " + minSize);

					System.out.println("Total File Size : " + totalFileSize);

					fileSizeStatus = (totalFileSize >= minSize && totalFileSize <= maxSize) ? "passed" : "failed";

					System.out.println("\n====> File Size Status : " + fileSizeStatus);
				}
				fileTrackingSummary.setFileSizeStatus(fileSizeStatus);

				matchedFileCount = matchedFileCount + fileTrackingSummary.getFileCount();
				fileTrackingSummary.setFileCount(matchedFileCount);
				String countStatus = (fmRule.getFileCount() == matchedFileCount) ? "passed" : "failed";
				fileTrackingSummary.setCountStatus(countStatus);
				fileTrackingSummary.setLastUpdateTimeStamp(new Date());

				fileMonitorDao.addFileTrackingSummary(fileTrackingSummary);

				// Update lastProcessingDate
				fileMonitorDao.updateFMRuleLastProcessingTime(idApp, fmRule.getId(), fm_lastProcessedTime);

			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while processing FileMonitoring ****");
			e.printStackTrace();
			throw e;
		}

	}

	public FileCheckRules fileFormatRulesValidation(File l_file, ListDataSchema listDataSchema, long idData) {
		FileCheckRules fileCheckRules = null;
		try {
			String isFileValid = "failed";
			String zeroSizeFile = "failed";
			String recordLengthCheck = "";
			String recordMaxLengthCheck = "";
			
			String columnCountCheck = "";
			String columnSequenceCheck = "";
			int totalRecordLength = 0;

			// Get Data file format
			String fileDataFormat = listDataSchema.getFileDataFormat();
			String headerFileFormat = listDataSchema.getHeaderFileDataFormat();
			String isHeaderPresent = listDataSchema.getHeaderPresent();

			String dataFile_delimiter = databuckFileUtility.getDemiliterByFormat(fileDataFormat);
			if (dataFile_delimiter == null || dataFile_delimiter.trim().isEmpty()) {
				dataFile_delimiter = "\\,";
			}

			String headerFile_delimiter = databuckFileUtility.getDemiliterByFormat(headerFileFormat);
			if (headerFile_delimiter == null || headerFile_delimiter.trim().isEmpty()) {
				headerFile_delimiter = "\\,";
			}

			// Get the listDataDefinitions
			List<String> columnsList = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

			// Check if file is zero size
			if (l_file.length() > 0) {
				zeroSizeFile = "passed";
				columnCountCheck = "failed";
				columnSequenceCheck = "failed";

				// Read first line from data file
				String firstLine = "";
				try {
					BufferedReader br = new BufferedReader(new FileReader(l_file));
					String line = "";
					while ((line = br.readLine()) != null) {
						firstLine = line;
						break;
					}
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// If File has header, check the columns count and columns sequence
				if (isHeaderPresent != null && isHeaderPresent.equalsIgnoreCase("Y")) {

					String headerline = firstLine;
					if (headerline != null && !headerline.trim().isEmpty()) {
						String[] h_columnList = headerline.split(dataFile_delimiter);

						if (columnsList.size() == h_columnList.length) {
							columnCountCheck = "passed";
							columnSequenceCheck = "passed";
							int i = 0;
							for (String col : columnsList) {
								if (col == null || h_columnList[i] == null
										|| !col.trim().equalsIgnoreCase(h_columnList[i].trim())) {
									columnSequenceCheck = "failed";
									break;
								}
								++i;
							}
						}
					}

					if (columnCountCheck.equalsIgnoreCase("passed") && columnSequenceCheck.equalsIgnoreCase("passed")) {
						isFileValid = "passed";
					}

				} else {
					File headerPath = new File(listDataSchema.getHeaderFilePath());
					String headerFileNamePattern = listDataSchema.getHeaderFileNamePattern();
					boolean headerFileFound = false;
					String headerFileName = "";

					// Identify the header file
					System.out.println("\n====> Identifying header file ..");
					if (headerPath != null && headerPath.exists() && headerPath.isDirectory()) {

						Collection<File> fileList = FileUtils.listFiles(headerPath,
								new WildcardFileFilter(headerFileNamePattern), null);

						// If only one header file is found for that pattern
						if (fileList != null && fileList.size() == 1) {
							headerFileName = fileList.iterator().next().getName();
							headerFileFound = true;
						} else {
							String[] fileNameParts = l_file.getName().split("\\.");

							for (File h_file : fileList) {
								String fileName = h_file.getName();

								if (fileName.toUpperCase().startsWith(fileNameParts[0].toUpperCase())) {
									headerFileName = fileName;
									headerFileFound = true;
									break;
								}
							}
						}

					}
					if (headerFileFound) {
						String dataLine = firstLine;

						File h_file = new File(headerPath + "/" + headerFileName);
						System.out.println("\n====> Header file: " + headerPath + "/" + headerFileName);

						if (fileDataFormat.equalsIgnoreCase("FLAT")) {
							recordLengthCheck = "failed";
							recordMaxLengthCheck = "failed";
							List<String> flatFile_colList = new ArrayList<String>();
							int lineNumber = 0;
							try {
								BufferedReader br = new BufferedReader(new FileReader(h_file));
								String line = "";
								while ((line = br.readLine()) != null) {
									if (lineNumber != 0) {
										String[] st = line.split(headerFile_delimiter);
										flatFile_colList.add(st[0]);
										totalRecordLength += Integer.parseInt(st[1]);
									}
									++lineNumber;
								}
								br.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (dataLine.length() == totalRecordLength) {
								recordLengthCheck = "passed";
								recordMaxLengthCheck = "passed";
							}

							if (flatFile_colList.size() == columnsList.size()) {
								columnCountCheck = "passed";
								columnSequenceCheck = "passed";
								int i = 0;
								for (String col : columnsList) {
									if (!col.equalsIgnoreCase(flatFile_colList.get(i))) {
										columnSequenceCheck = "failed";
										break;
									}
									++i;
								}
							}

							if (recordLengthCheck.equalsIgnoreCase("passed")
									&& columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}
							if (recordMaxLengthCheck.equalsIgnoreCase("passed")
									&& columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}

						} else {
							String headerLine = "";
							try {
								BufferedReader br = new BufferedReader(new FileReader(h_file));
								String line = "";
								while ((line = br.readLine()) != null) {
									headerLine = line;
									break;
								}
								br.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

							// column count check
							if (headerLine != null && !headerLine.trim().isEmpty()) {
								String[] h_columnList = headerLine.split(dataFile_delimiter);
								String[] data_columnList = dataLine.split(headerFile_delimiter, -1);

								if (columnsList.size() == h_columnList.length
										&& columnsList.size() == data_columnList.length) {
									columnCountCheck = "passed";
									columnSequenceCheck = "passed";
									int i = 0;
									for (String col : columnsList) {
										if (col == null || h_columnList[i] == null
												|| !col.trim().equalsIgnoreCase(h_columnList[i].trim())) {
											columnSequenceCheck = "failed";
											break;
										}
										++i;
									}
								}
							}

							if (columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}
						}

					} else {
						System.out.println("\n====>Header file not found!!");
					}

				}

				// Check full File column count check
				String fullFileColumChkEnabled = appDbConnectionProperties.getProperty("filemonitoring.fullfile.check");
				System.out.println("\n====>Full File column count check enabled: " + fullFileColumChkEnabled);

				if (fullFileColumChkEnabled != null && fullFileColumChkEnabled.equalsIgnoreCase("Y")
						&& isFileValid.equalsIgnoreCase("passed")) {
					BufferedReader bufReader = null;
					try {
						bufReader = new BufferedReader(new FileReader(l_file));
					} catch (Exception e) {
						e.printStackTrace();
					}

					String fullFileChkStatus = performFullFileColumnCountCheck(bufReader, fileDataFormat,
							dataFile_delimiter, totalRecordLength, columnsList.size());
					System.out.println("\n====>Full File column count check Status: " + fullFileChkStatus);

					if (fileDataFormat.equalsIgnoreCase("FLAT")) {
						recordLengthCheck = fullFileChkStatus;
						recordMaxLengthCheck = fullFileChkStatus;
					}
					else
						columnCountCheck = fullFileChkStatus;

					isFileValid = fullFileChkStatus;
				} else {
					System.out.println("\n====>File is not valid, cannot perform full file check !!");
				}
			}

			fileCheckRules = new FileCheckRules();
			fileCheckRules.setFileName(l_file.getName());
			fileCheckRules.setFilePath(l_file.getAbsolutePath());
			fileCheckRules.setFileFormat(fileDataFormat);
			fileCheckRules.setZeroSizeFile(zeroSizeFile);
			fileCheckRules.setRecordLengthCheck(recordLengthCheck);
			fileCheckRules.setRecordMaxLengthCheck(recordMaxLengthCheck);
			fileCheckRules.setColumnCountCheck(columnCountCheck);
			fileCheckRules.setColumnSequenceCheck(columnSequenceCheck);
			fileCheckRules.setIsFileValid(isFileValid);

			System.out.println(fileCheckRules.toString());
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while performing file checks!!");
			e.printStackTrace();
		}
		return fileCheckRules;
	}

	public FileCheckRules s3FileFormatRulesValidation(AmazonS3 s3Client, S3ObjectSummary summary, String s3fileName,
			ListDataSchema listDataSchema, long idData) {
		FileCheckRules fileCheckRules = null;
		try {
			String isFileValid = "failed";
			String zeroSizeFile = "failed";
			String recordLengthCheck = "";
			String recordMaxLengthCheck = "";
			String columnCountCheck = "";
			String columnSequenceCheck = "";
			int totalRecordLength = 0;

			// Get Data file format
			String fileDataFormat = listDataSchema.getFileDataFormat();
			String headerFileFormat = listDataSchema.getHeaderFileDataFormat();
			String isHeaderPresent = listDataSchema.getHeaderPresent();

			String dataFile_delimiter = databuckFileUtility.getDemiliterByFormat(fileDataFormat);
			if (dataFile_delimiter == null || dataFile_delimiter.trim().isEmpty()) {
				dataFile_delimiter = "\\,";
			}

			String headerFile_delimiter = databuckFileUtility.getDemiliterByFormat(headerFileFormat);
			if (headerFile_delimiter == null || headerFile_delimiter.trim().isEmpty()) {
				headerFile_delimiter = "\\,";
			}

			// Get the listDataDefinitions
			List<String> columnsList = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

			// Check if file is zero size
			if (summary.getSize() > 0) {
				zeroSizeFile = "passed";
				columnCountCheck = "failed";
				columnSequenceCheck = "failed";

				// Read first line from data file
				String firstLine = "";
				try {
					S3Object s3object = s3Client
							.getObject(new GetObjectRequest(summary.getBucketName(), summary.getKey()));
					BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
					String line = "";
					while ((line = br.readLine()) != null) {
						firstLine = line;
						break;
					}
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// If File has header, check the columns count and columns sequence
				if (isHeaderPresent != null && isHeaderPresent.equalsIgnoreCase("Y")) {

					String headerline = firstLine;
					if (headerline != null && !headerline.trim().isEmpty()) {
						String[] h_columnList = headerline.split(dataFile_delimiter);

						if (columnsList.size() == h_columnList.length) {
							columnCountCheck = "passed";
							columnSequenceCheck = "passed";
							int i = 0;
							for (String col : columnsList) {
								if (col == null || h_columnList[i] == null
										|| !col.trim().equalsIgnoreCase(h_columnList[i].trim())) {
									columnSequenceCheck = "failed";
									break;
								}
								++i;
							}
						}
					}

					if (columnCountCheck.equalsIgnoreCase("passed") && columnSequenceCheck.equalsIgnoreCase("passed")) {
						isFileValid = "passed";
					}

				} else {
					String headerPath = listDataSchema.getHeaderFilePath();
					String headerFileNamePattern = listDataSchema.getHeaderFileNamePattern();
					String bucketName = listDataSchema.getBucketName();
					boolean headerFileFound = false;
					S3ObjectSummary h_file = null;

					// Check if header path exists
					headerPath = headerPath.replace("//", "/");

					if (!headerPath.endsWith("/")) {
						headerPath = headerPath + "/";
					}

					ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName)
							.withPrefix(headerPath).withDelimiter("/");

					ListObjectsV2Result listing = s3Client.listObjectsV2(req);

					List<S3ObjectSummary> headerFileList = new ArrayList<S3ObjectSummary>();

					for (S3ObjectSummary hsummary : listing.getObjectSummaries()) {
						String fileName = hsummary.getKey();
						fileName = fileName.replace(headerPath, "");
						System.out.println(fileName);

						if (!fileName.equals(headerPath)) {

							boolean patternMatched = databuckFileUtility.isPatternMatched(fileName,
									headerFileNamePattern);
							if (patternMatched) {
								headerFileList.add(hsummary);
							}
						}
					}

					// If only one header file is found for that pattern
					if (headerFileList != null && headerFileList.size() == 1) {
						h_file = headerFileList.get(0);
						headerFileFound = true;
					} else {
						String[] fileNameParts = s3fileName.split("\\.");

						for (S3ObjectSummary hfSummary : headerFileList) {
							String fileName = hfSummary.getKey();
							fileName = fileName.replace(headerPath, "");

							if (fileName.toUpperCase().startsWith(fileNameParts[0].toUpperCase())) {
								h_file = hfSummary;
								headerFileFound = true;
								break;
							}
						}
					}

					if (headerFileFound) {
						String dataLine = firstLine;

						if (fileDataFormat.equalsIgnoreCase("FLAT")) {
							recordLengthCheck = "failed";
							recordMaxLengthCheck = "failed";
							List<String> flatFile_colList = new ArrayList<String>();
							int lineNumber = 0;
							try {
								S3Object s3object = s3Client
										.getObject(new GetObjectRequest(h_file.getBucketName(), h_file.getKey()));
								BufferedReader br = new BufferedReader(
										new InputStreamReader(s3object.getObjectContent()));
								String line = "";
								while ((line = br.readLine()) != null) {
									if (lineNumber != 0) {
										String[] st = line.split(headerFile_delimiter);
										flatFile_colList.add(st[0]);
										totalRecordLength += Integer.parseInt(st[1]);
									}
									++lineNumber;
								}
								br.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (dataLine.length() == totalRecordLength) {
								recordLengthCheck = "passed";
								recordMaxLengthCheck = "passed";
							}

							if (flatFile_colList.size() == columnsList.size()) {
								columnCountCheck = "passed";
								columnSequenceCheck = "passed";
								int i = 0;
								for (String col : columnsList) {
									if (!col.equalsIgnoreCase(flatFile_colList.get(i))) {
										columnSequenceCheck = "failed";
										break;
									}
									++i;
								}
							}

							if (recordLengthCheck.equalsIgnoreCase("passed")
									&& columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}
							if (recordMaxLengthCheck.equalsIgnoreCase("passed")
									&& columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}

						} else {
							String headerLine = "";
							try {
								S3Object s3object = s3Client
										.getObject(new GetObjectRequest(h_file.getBucketName(), h_file.getKey()));
								BufferedReader br = new BufferedReader(
										new InputStreamReader(s3object.getObjectContent()));
								String line = "";
								while ((line = br.readLine()) != null) {
									headerLine = line;
									break;
								}
								br.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

							// column count check
							if (headerLine != null && !headerLine.trim().isEmpty()) {
								String[] h_columnList = headerLine.split(headerFile_delimiter);
								String[] data_columnList = dataLine.split(dataFile_delimiter, -1);

								if (columnsList.size() == h_columnList.length
										&& columnsList.size() == data_columnList.length) {
									columnCountCheck = "passed";
									columnSequenceCheck = "passed";
									int i = 0;
									for (String col : columnsList) {
										if (col == null || h_columnList[i] == null
												|| !col.trim().equalsIgnoreCase(h_columnList[i].trim())) {
											columnSequenceCheck = "failed";
											break;
										}
										++i;
									}
								}
							}

							if (columnCountCheck.equalsIgnoreCase("passed")
									&& columnSequenceCheck.equalsIgnoreCase("passed")) {
								isFileValid = "passed";
							}
						}

					} else {
						System.out.println("\n====>Header file not found!!");
					}
				}

				// Check full File column count check
				String fullFileColumChkEnabled = appDbConnectionProperties.getProperty("filemonitoring.fullfile.check");
				System.out.println("\n====>Full File column count check enabled: " + fullFileColumChkEnabled);

				if (fullFileColumChkEnabled != null && fullFileColumChkEnabled.equalsIgnoreCase("Y")
						&& isFileValid.equalsIgnoreCase("passed")) {
					BufferedReader bufReader = null;
					try {
						S3Object s3object = s3Client
								.getObject(new GetObjectRequest(summary.getBucketName(), summary.getKey()));
						bufReader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
					} catch (Exception e) {
						e.printStackTrace();
					}

					String fullFileChkStatus = performFullFileColumnCountCheck(bufReader, fileDataFormat,
							dataFile_delimiter, totalRecordLength, columnsList.size());
					System.out.println("\n====>Full File column count check Status: " + fullFileChkStatus);

					if (fileDataFormat.equalsIgnoreCase("FLAT")) {
						recordLengthCheck = fullFileChkStatus;
						recordMaxLengthCheck = fullFileChkStatus;
					}
					else
						columnCountCheck = fullFileChkStatus;

					isFileValid = fullFileChkStatus;
				} else {
					System.out.println("\n====>File is not valid, cannot perform full file check !!");
				}
			}

			fileCheckRules = new FileCheckRules();
			fileCheckRules.setFileName(s3fileName);
			fileCheckRules.setFilePath(summary.getKey().replace(s3fileName, ""));
			fileCheckRules.setFileFormat(fileDataFormat);
			fileCheckRules.setZeroSizeFile(zeroSizeFile);
			fileCheckRules.setRecordLengthCheck(recordLengthCheck);
			fileCheckRules.setRecordMaxLengthCheck(recordMaxLengthCheck);
			fileCheckRules.setColumnCountCheck(columnCountCheck);
			fileCheckRules.setColumnSequenceCheck(columnSequenceCheck);
			fileCheckRules.setIsFileValid(isFileValid);

			System.out.println(fileCheckRules.toString());
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while performing file checks!!");
			e.printStackTrace();
		}
		return fileCheckRules;
	}

	public FileCheckRules s3IAMRoleFileFormatRulesValidation(String bucketName, String folderPath, String s3fileName,
			ListDataSchema listDataSchema, long idData, long fileSize, String childFilePattern, String subFolder, boolean xsltFileFound, String xsltFileName) {
		FileCheckRules fileCheckRules = null;
		try {
			String isFileValid = "failed";
			String zeroSizeFile = "failed";
			String recordLengthCheck = "";
			String recordMaxLengthCheck = "";
			String columnCountCheck = "";
			String columnSequenceCheck = "";
			String isXsltFileFound="Not-Applicable";

			// Get the listDataDefinitions
			List<String> columnsList = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
			System.out.println("ColumnsList from Template: " + columnsList);

			// Check if file is zero size
			if (fileSize > 0) {
				zeroSizeFile = "passed";
				columnCountCheck = "failed";
				columnSequenceCheck = "failed";

				// Read first line from data file
				Map<String, String> metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(s3fileName,
						listDataSchema, childFilePattern, subFolder, "");////// Arif
				System.out.println("ColumnsList from New File: " + metadata.keySet() + "\n");

				if (metadata != null && columnsList.size() == metadata.size()) {
					columnCountCheck = "passed";
					columnSequenceCheck = "passed";
					int i = 0;
					for (String hcol : metadata.keySet()) {
						if (hcol == null || columnsList.get(i) == null
								|| !hcol.trim().equalsIgnoreCase(columnsList.get(i).trim())) {
							columnSequenceCheck = "failed";
							break;
						}
						++i;
					}
				}

				if (columnCountCheck.equalsIgnoreCase("passed") && columnSequenceCheck.equalsIgnoreCase("passed")) {
					isFileValid = "passed";
				}

				// Check full File column count check
				String fullFileColumChkEnabled = appDbConnectionProperties.getProperty("filemonitoring.fullfile.check");
				System.out.println("\n====>Full File column count check enabled: " + fullFileColumChkEnabled);

				if (fullFileColumChkEnabled != null && fullFileColumChkEnabled.equalsIgnoreCase("Y")
						&& isFileValid.equalsIgnoreCase("passed")) {

					String isFileEncrypted = listDataSchema.getFileEncrypted();

					String fileDataFormat = listDataSchema.getFileDataFormat();
					
					String xsltFolderPath = listDataSchema.getXsltFolderPath();

					String dataFile_delimiter = databuckFileUtility.getDemiliterByFormat(fileDataFormat);

					String d_filePath = (bucketName + "/" + folderPath + "/" + s3fileName).replace("//", "/");

					BufferedReader bufReader = null;
					
					BufferedReader bufReaderXslt = null;

					String decryptedFilePath = "";
					
					String ediFilePath="";
				
					
					if (xsltFileFound && xsltFolderPath != null){
				            String totalXsltFile=(bucketName + "/" +xsltFolderPath + "/" + xsltFileName).replace("//", "/");
							xsltFolderPath="s3://"+totalXsltFile;
					}
				
					
					if (xsltFileFound && xsltFolderPath != null)
					{
						String awsCliCommand = "aws s3 cp " + xsltFolderPath + " - ";
						Process child = Runtime.getRuntime().exec(awsCliCommand);
						System.out.println("\n====> awsCliCommand: " + awsCliCommand);
						bufReaderXslt = new BufferedReader(new InputStreamReader(child.getInputStream()));
						isXsltFileFound=(bufReaderXslt != null) ? "passed" : "failed";
					}
					
					
						

					try {

						/*
						 * Check if the File encrypted, download and decrypt it
						 */
						if (isFileEncrypted != null && isFileEncrypted.trim().equalsIgnoreCase("Y") ) {

							decryptedFilePath = databuckFileUtility.downloadAndGetDecryptFilePathForS3(d_filePath);
							
							if (xsltFileFound && xsltFolderPath != null)
							{						
								System.out.println("\n====> Found PGP EDI File, Extracting after decryption<=======");
								ediFilePath=databuckFileUtility.ediParserForS3(s3fileName, decryptedFilePath, bufReaderXslt, true);
								bufReader = databuckFileUtility.readFile(ediFilePath);
																
							} else { 
							      bufReader = databuckFileUtility.readFile(decryptedFilePath);
							      
							}

						} else {
							
							d_filePath = "s3://" + d_filePath;
							System.out.println("\n====> d_filePath:" + d_filePath);
							
							if (xsltFileFound && xsltFolderPath != null)
							{
								System.out.println("\n====> Found EDI File, Extracting <=======");
								ediFilePath = databuckFileUtility.ediParserForS3(s3fileName,d_filePath, bufReaderXslt, false);
								bufReader = databuckFileUtility.readFile(ediFilePath);								
								
							} else {
							
							    String awsCliCommand = "aws s3 cp " + d_filePath + " - ";
							    Process child = Runtime.getRuntime().exec(awsCliCommand);
     							System.out.println("\n====> awsCliCommand: " + awsCliCommand);
                                bufReader = new BufferedReader(new InputStreamReader(child.getInputStream()));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					String fullFileChkStatus = performFullFileColumnCountCheck(bufReader,
							listDataSchema.getFileDataFormat(), dataFile_delimiter, 0, columnsList.size());
					System.out.println("\n====>Full File column count check Status: " + fullFileChkStatus);

					if (fileDataFormat.equalsIgnoreCase("FLAT")) {
						recordLengthCheck = fullFileChkStatus;
						recordMaxLengthCheck = fullFileChkStatus;
					}
					else
						columnCountCheck = fullFileChkStatus;

					isFileValid = fullFileChkStatus;

					// delete decrypted file, if exists
					databuckFileUtility.deleteFile(decryptedFilePath);
					databuckFileUtility.deleteFile(ediFilePath);

				} else {
					System.out.println("\n====>File is not valid, cannot perform full file check !!");
				}

			}
			fileCheckRules = new FileCheckRules();
			fileCheckRules.setFileName(s3fileName);
			fileCheckRules.setFilePath(bucketName + "/" + folderPath);
			fileCheckRules.setFileFormat(listDataSchema.getFileDataFormat());
			fileCheckRules.setZeroSizeFile(zeroSizeFile);
			fileCheckRules.setRecordLengthCheck(recordLengthCheck);
			fileCheckRules.setRecordMaxLengthCheck(recordMaxLengthCheck);
			fileCheckRules.setColumnCountCheck(columnCountCheck);
			fileCheckRules.setColumnSequenceCheck(columnSequenceCheck);
			fileCheckRules.setIsFileValid(isFileValid);
			

			System.out.println(fileCheckRules.toString());
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while performing file checks!!");
			e.printStackTrace();
		}
		return fileCheckRules;
	}

	private String performFullFileColumnCountCheck(BufferedReader br, String fileFormat, String delimiter,
			int totalRecordLength, int columnCount) {
		System.out.println("\n====> Full File ColumnCount check - START ..");

		String status = "failed";
		try {
			String line = "";
			long lineNumber = 1;
			if (br != null) {
				while ((line = br.readLine()) != null) {
					if (fileFormat.equalsIgnoreCase("FLAT")) {
						if (line.length() != totalRecordLength) {
							status = "failed";
							System.out.println("\n====> Failed Data row Number: " + lineNumber);
							System.out.println("\n====> Failed row Data record length: [" + line.length()
									+ "] TotalRecordLength:[" + totalRecordLength + "]");
							System.out.println("\n====> Failed Data record: " + line);
							break;
						}
					} else {
						String[] fields = line.split(delimiter, -1);
						if (fields == null || fields.length != columnCount) {
							status = "failed";
							System.out.println("\n====> Failed Data row Number: " + lineNumber);
							System.out.println("\n====> Failed Data columnCount: [" + fields.length
									+ "] Expected column count:[" + columnCount + "]");
							System.out.println("\n====> Failed Data record: " + line);
							break;
						}
					}
					status = "passed";
					++lineNumber;
				}
				br.close();
			}
		} catch (Exception e) {
			status = "failed";
			e.printStackTrace();
		}
		return status;
	}

	private void updateTemplateAndTriggerValidation(FileTrackingHistory fileTrackingHistory) {
		try {
			// If file is valid get the validation Id of the template
			if (fileTrackingHistory.getStatus() != null && fileTrackingHistory.getStatus().equalsIgnoreCase("passed")) {

				// Check if template creation is in-progress
				long idData = fileTrackingHistory.getIdData();

				if (idData != 0l) {
					boolean isTemplateInProgress = iTaskDAO.isTemplateJobQueuedOrInProgress(idData);

					if (!isTemplateInProgress) {

						long f_idApp = fileMonitorDao.getDQApplicationsForIdData(fileTrackingHistory.getIdData());

						if (f_idApp != 0l) {
							// Check if the validation is already in Queued, Started or in-progress
							boolean jobInProgress = iTaskDAO.checkIfValidationJobQueuedOrInProgress(f_idApp);

							if (!jobInProgress) {
								System.out.println("\n====>BucketName:[" + fileTrackingHistory.getBucketName()
										+ "] FolderPath:[" + fileTrackingHistory.getFolderPath() + "] FileName:["
										+ fileTrackingHistory.getFileName()
										+ "] -- is Valid, identifying the validation Id ..");

								System.out.println("\n====> Validation Id:" + f_idApp);

								// Change the folderName to fileName
								System.out.println(
										"\n====> Updating the foldername in the template to the latest file ..");
								fileMonitorDao.updateFolderNameOfTemplate(fileTrackingHistory.getIdData(),
										fileTrackingHistory.getFileName());

								// Change the fileExecutionStatus to 'processed'
								System.out.println("\n====> Updating the fileExecutionStatus to 'processed' ..");
								fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(), "processed",
										"Validation Triggered");

								// Place the validation in queue
								System.out.println("\n====> Placing validation job in queue ..");
								String deployMode = clusterProperties.getProperty("deploymode");
								if (deployMode.trim().equalsIgnoreCase("2")) {
									deployMode = "local";
								} else {
									deployMode = "cluster";
								}
								iTaskDAO.insertRunScheduledTask(f_idApp, "queued", deployMode, null, null);
							} else {
								fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(),
										fileTrackingHistory.getFileExecutionStatus(),
										"Validation is already under progress, this new file will be processed next");
							}
						} else {
							fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(), "processed",
									"Valid validation is not found");
						}

					} else {
						fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(),
								fileTrackingHistory.getFileExecutionStatus(),
								"Template creation is in progress, please wait");
					}
				} else {
					System.out.println("\n====> Invalid Template Id can't proceed further !!");

					fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(), "processed",
							"Template Id is invalid");
				}
			}

		} catch (Exception e) {
			fileMonitorDao.updateFileExecutionStatusAndMsg(fileTrackingHistory.getId(), "processed",
					"Execution occurred");
			e.printStackTrace();
		}
	}

	private void sendFileMonitorSQSAlert(FileTrackingHistory fileTrackingHistory, String folderPath, String fileName,
			long templateId) {

		try {
			String sqsMessageEnabled = appDbConnectionProperties.getProperty("sqs.notifications");

			if (sqsMessageEnabled != null && sqsMessageEnabled.trim().equalsIgnoreCase("Y")) {

				// Get SQS Queue URL and region
				String sqsQueueUrl = appDbConnectionProperties.getProperty("sqs.notifications.queue.url");

				String region = appDbConnectionProperties.getProperty("sqs.notifications.queue.region");

				if (sqsQueueUrl != null && !sqsQueueUrl.trim().isEmpty() && region != null
						&& !region.trim().isEmpty()) {
					String nextStepProceed = "";
					String errorCode = "";

					// Send SQS message only when overall status is failed
					if (fileTrackingHistory.getStatus().equalsIgnoreCase("failed")) {
						nextStepProceed = "N";

						if (fileTrackingHistory.getZeroSizeFileCheck() != null
								&& fileTrackingHistory.getZeroSizeFileCheck().equalsIgnoreCase("failed")) {
							errorCode = "File Size Issue";
						} else if (fileTrackingHistory.getRecordLengthCheck() != null
								&& fileTrackingHistory.getRecordLengthCheck().equalsIgnoreCase("failed")) {
							errorCode = "Flat File Record Length Issue";
						} else if (fileTrackingHistory.getRecordMaxLengthCheck() != null
								&& fileTrackingHistory.getRecordMaxLengthCheck().equalsIgnoreCase("failed")) {
							errorCode = "Flat File Record Max Length Issue";
						} else if (fileTrackingHistory.getColumnSequenceCheck() != null
								&& fileTrackingHistory.getColumnSequenceCheck().equalsIgnoreCase("failed")) {
							errorCode = "Record does not match column names and numbers";
						} else if (fileTrackingHistory.getColumnCountCheck() != null
								&& fileTrackingHistory.getColumnCountCheck().equalsIgnoreCase("failed")) {
							errorCode = "Record does not match column count";
						} else if (templateId == 0l) {
							errorCode = "Failed to find valid template";
						}

						// Prepare feDataQuality json
						JSONObject feDataQuality = new JSONObject();
						long idApp = fileTrackingHistory.getIdApp();
						ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);
						String validationName = listApplications != null ? listApplications.getName() : "";
						feDataQuality.put("idApp", idApp);
						feDataQuality.put("validationName", validationName);
						feDataQuality.put("testType", "");
						feDataQuality.put("date", "");
						feDataQuality.put("run", "");
						feDataQuality.put("templateId", "");
						feDataQuality.put("templateName", "");
						feDataQuality.put("connectionId", "");
						feDataQuality.put("connectionName", "");
						feDataQuality.put("aggregateDqi", "");

						JSONArray feRowDQArray = new JSONArray();
						JSONObject rca_feRowDQ = new JSONObject();
						rca_feRowDQ.put("validationTest", "Record Count Fingerprint");
						rca_feRowDQ.put("definition", "Record count reasonability based on historical trends");
						rca_feRowDQ.put("dqScore", "");
						feRowDQArray.put(rca_feRowDQ);
						feDataQuality.put("feRowDQ", feRowDQArray);
						
						//File path modification
						String[] b = fileName.split("/");
						String filename = b[b.length-1];
						String[] subFolder = fileName.split(filename);
						
						 if(b.length > 1) {
							 folderPath = folderPath.replace("//","/")+subFolder[0];
						 }
						

						// Prepare message body
						JSONObject msgJSONObject = new JSONObject();
						msgJSONObject.put("nextStepProceed", nextStepProceed);
						msgJSONObject.put("locationName", folderPath);
						msgJSONObject.put("tableOrFileName", filename);
						msgJSONObject.put("errorCode", errorCode);
						msgJSONObject.put("uniqueId", "");
						msgJSONObject.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						msgJSONObject.put("feDataQuality", feDataQuality);

						String messageBody = msgJSONObject.toString();

						// Place message in queue
						SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(sqsQueueUrl)
								.withMessageBody(messageBody);

						AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(region).build();
						sqs.sendMessage(send_msg_request);

					}

				} else {
					System.out.println("\n====> SQS queue Url/ region is missing !!");
				}
			}

		} catch (Exception e) {
			System.out.println("Exception while placing message in SQS queue: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private String identifyChildPattern(ListDataSchema listDataSchema, String fileName) {
		String childPattern = "";
		try {
			String fileNamePattern = listDataSchema.getFileNamePattern();
			int firstUniqueCharsCount = listDataSchema.getStartingUniqueCharCount();
			int lastUniqueCharsCount = listDataSchema.getEndingUniqueCharCount();

			String[] filePatternTokens = fileNamePattern.split("\\*");

			// Identify the child pattern
			if (firstUniqueCharsCount > 0 && lastUniqueCharsCount > 0) {
				String firstChars = fileName.substring(0, firstUniqueCharsCount);
				String lastChars = fileName.substring((fileName.length() - lastUniqueCharsCount + 1),
						fileName.length());
				childPattern = firstChars + "*" + lastChars;

			} else if (firstUniqueCharsCount > 0) {
				String firstChars = fileName.substring(0, firstUniqueCharsCount);

				if (fileNamePattern.startsWith("*") && fileName.endsWith(filePatternTokens[1])) {
					childPattern = firstChars + "*" + filePatternTokens[1];
				} else if (fileNamePattern.endsWith("*") && fileName.startsWith(filePatternTokens[0])) {
					childPattern = firstChars + "*";
				} else if (fileNamePattern.contains("*") && fileName.startsWith(filePatternTokens[0])
						&& fileName.endsWith(filePatternTokens[1])) {
					childPattern = firstChars + "*" + filePatternTokens[1];
				} else {
					childPattern = firstChars + "*";
				}

			} else if (lastUniqueCharsCount > 0) {
				String lastChars = fileName.substring((fileName.length() - lastUniqueCharsCount + 1),
						fileName.length());

				if (fileNamePattern.startsWith("*") && fileName.endsWith(filePatternTokens[1])) {
					childPattern = "*" + lastChars;
				} else if (fileNamePattern.endsWith("*") && fileName.startsWith(filePatternTokens[0])) {
					childPattern = filePatternTokens[0] + "*" + lastChars;
				} else if (fileNamePattern.contains("*") && fileName.startsWith(filePatternTokens[0])
						&& fileName.endsWith(filePatternTokens[1])) {
					childPattern = filePatternTokens[0] + "*" + lastChars;
				} else {
					childPattern = "*" + lastChars;
				}

			} else {
				childPattern = fileNamePattern;
			}

			System.out.println("\n====> Child pattern: " + childPattern);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return childPattern;
	}

	private void sendFileMonitorEmailAlert(FileTrackingHistory fileTrackingHistory, String folderPath,
			String fileName) {

		// alert user about the failed file
		System.out.println("\n====> File checks failed, sending email notification ... ");

		String zeroSizeFileCheck = fileTrackingHistory.getZeroSizeFileCheck();
		zeroSizeFileCheck = zeroSizeFileCheck != null ? zeroSizeFileCheck.toUpperCase() : "";

		String recordLengthCheck = fileTrackingHistory.getRecordLengthCheck();
		recordLengthCheck = recordLengthCheck != null ? recordLengthCheck.toUpperCase() : "";

		String recordMaxLengthCheck = fileTrackingHistory.getRecordMaxLengthCheck();
		recordMaxLengthCheck = recordMaxLengthCheck != null ? recordMaxLengthCheck.toUpperCase() : "";

		
		String columnCountCheck = fileTrackingHistory.getColumnCountCheck();
		columnCountCheck = columnCountCheck != null ? columnCountCheck.toUpperCase() : "";

		String columnSequenceCheck = fileTrackingHistory.getColumnSequenceCheck();
		columnSequenceCheck = columnSequenceCheck != null ? columnSequenceCheck.toUpperCase() : "";

		HashMap<String, String> oTokens = new HashMap<>();
		oTokens.put("User", "User");
		oTokens.put("folderPath", folderPath);
		oTokens.put("fileName", fileName);
		oTokens.put("ZeroSizeFileCheck", zeroSizeFileCheck);
		oTokens.put("RecordLengthCheck", recordLengthCheck);
		oTokens.put("RecordMaxLengthCheck", recordMaxLengthCheck);
		oTokens.put("ColumnCountCheck", columnCountCheck);
		oTokens.put("ColumnSequenceCheck", columnSequenceCheck);

		NotificationService.SendNotification("FILE_MONITORING_FAILED", oTokens, null);
	}

	private void sendFileMonitorEmailForSuccess(long FocusObjectId, String MaxDate, String FileFrequency,
			String DayOfCheck, String Time, String FilePath, String FilePattern, int ExpectedCount, int ArrivedCount,
			int MissingCount, int DuplicateCount) {

		HashMap<String, String> oTokens = new HashMap<>();
		oTokens.put("User", "User");
		oTokens.put("FocusObjectId", "" + FocusObjectId);
		oTokens.put("MaxDate", MaxDate);
		oTokens.put("FileFrequency", FileFrequency);
		oTokens.put("DayOfCheck", DayOfCheck);
		oTokens.put("Time", Time);
		oTokens.put("FilePath", FilePath);
		oTokens.put("FilePattern", FilePattern);
		oTokens.put("ExpectedCount", "" + ExpectedCount);
		oTokens.put("ArrivedCount", "" + ArrivedCount);
		oTokens.put("MissingCount", "" + MissingCount);
		oTokens.put("DuplicateCount", "" + DuplicateCount);

		NotificationService.SendNotification("FILE_MONITORING_SUCCESS", oTokens, null);

	}
}
