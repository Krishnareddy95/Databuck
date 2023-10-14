package com.databuck.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import javax.security.auth.login.LoginContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.Project;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;

@Controller
public class DownloadCsv {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	@Autowired
	public SchemaDAOI SchemaDAOI;
	@Autowired
	private IResultsDAO iResultsDAO;
	@Autowired
	MatchingResultDao matchingresultdao;
	@Autowired
	private Properties clusterProperties;
	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	IValidationCheckDAO oValidationCheckDAO;

	@Autowired
	ITemplateViewDAO oTemplateviewdao;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1) {
		this.jdbcTemplate1 = jdbcTemplate1;
		this.jdbcTemplate = jdbcTemplate;
	}

	@RequestMapping(value = "/sqlRuleDownloadCsv", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> sqlRuleDownloadCsv(HttpServletRequest request) {
		String sPath = request.getParameter("sPath");
		String sDownCsvName = request.getParameter("sDownCsvName");

		DateUtility.DebugLog("sqlRuleDownloadCsv 01", String.format("Entered download SQL rule CSV via Anker click with parms %1$s,%2$s", sPath, sDownCsvName));

		try {

			File oDirectory = new File(sPath);
			File[] aCsvFiles = oDirectory.listFiles((dir, name) -> name.endsWith(".csv"));

			DateUtility.DebugLog("sqlRuleDownloadCsv 02", String.format("No of CSV files expected in path '%1$s' is 1, files found are %2$s" ,sPath,aCsvFiles.length));

			String sSparkCsvFileName = aCsvFiles[0].getName();
			String sDownloadCsvFileName = sDownCsvName;

			File oDownloadCsvFile = new File(sPath + "/" + sSparkCsvFileName);

			HttpHeaders oResponseHeaders = new HttpHeaders();
			MediaType oMediaType = new MediaType("text","csv");
			oResponseHeaders.setContentType(oMediaType);
			oResponseHeaders.setContentDispositionFormData("attachment", sDownloadCsvFileName);

			DateUtility.DebugLog("sqlRuleDownloadCsv 03", String.format("All set about to push download file '%1$s' to browser as attachement response", sDownloadCsvFileName));

			InputStreamResource oInputStream = new InputStreamResource(new FileInputStream(oDownloadCsvFile));

			return new ResponseEntity<InputStreamResource>(oInputStream, oResponseHeaders, HttpStatus.OK);
		} catch (Exception oException) {
			System.out.println("sqlRuleDownloadCsv 99 Exception " + oException.getMessage());
			return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/statusBarCsvDownload", method = RequestMethod.POST, produces = "application/json")
	public void startStatuspoll(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam String tableName) {
		System.out.println("tableName=" + tableName);
		JSONObject json = new JSONObject();
		try {
			/*
			 * double currentCount = (Long) session.getAttribute(tableName +
			 * "_CurrentCount"); double totalCount = (Long) session.getAttribute(tableName +
			 * "_TotalCount"); System.out.println(currentCount + "" + totalCount);
			 * System.out.println(((currentCount / totalCount) * 100));
			 */
			Long percentage = (Long) session.getAttribute(tableName + "_Percentage");
			System.out.println(percentage);
			json.put("percentage", session.getAttribute(tableName + "_Percentage"));
			System.out.println(json);
			/*
			 * if(percentage>=100){ session.removeAttribute(tableName + "_Percentage"); }
			 */ response.getWriter().println(json);

		} catch (Exception e) {
			json.put("percentage", 100);
			try {
				response.getWriter().println(json);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	@RequestMapping(value = "/downloadCsv")
	public void doDownload(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String tableName = request.getParameter("tableName");

		// listFilesAndFilesSubDirectories("C:/Arif/First_Eigen/csvFiles/2103",
		// response, request);

		System.out.println("In downloadCsv ----------------" + tableName);

		try {

			System.out.println("tableName::" + tableName);

			if (clusterProperties.getProperty("app_mode").trim().equalsIgnoreCase("1")) {
				writeMongoToCsv(tableName, request);
			} else {
				System.out.println("session.setAttribute(tableName + _Percentage, 100");
				session.setAttribute(tableName + "_Percentage", 100);
			}
			if (tableName.equalsIgnoreCase("dq_dashboard")) {
				System.out.println("createQualityCsv");
				Long nProjectId= (Long)session.getAttribute("projectId");
				List<Project> aProjList = (List<Project>)session.getAttribute("userProjectList");
				iResultsDAO.createQualityCsv(nProjectId,aProjList);
			}
			if (tableName.equalsIgnoreCase("dm_dashboard")) {
				System.out.println("createMatchingCsv");
				matchingresultdao.createMatchingCsv(tableName);
			}if (tableName.equalsIgnoreCase("pkm_dashboard")) {
				System.out.println("createPrimaryKeyMatchingCsv");
				matchingresultdao.createMatchingCsv(tableName);
			}
			if (!tableName.trim().equals("")) {

				String fileFullPath = null;
				if (tableName.toUpperCase().contains("DATA_QUALITY")) {

					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv";

				} else {
					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv";
				}
				// String
				// fileFullPath="C:\\Users\\appzop6\\Downloads\\"+tableName+".csv";
				// String fileFullPath="E:\\backup\\Tuesday\\databuck.zip";
				System.out.println("table for csv+" + tableName);
				// get absolute path of the application
				ServletContext context = request.getSession().getServletContext();
				String appPath = context.getRealPath("");
				System.out.println("appPath = " + appPath);

				// construct the complete absolute path of the file
				File downloadFile = new File(fileFullPath);
				FileInputStream inputStream = new FileInputStream(downloadFile);

				// get MIME type of the file
				String mimeType = context.getMimeType(fileFullPath);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}
				System.out.println("MIME type: " + mimeType);

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", tableName + ".csv");
				response.setHeader(headerKey, headerValue);

				// get output stream of the response
				OutputStream outStream = response.getOutputStream();

				byte[] buffer = new byte[1024 * 1000];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}

				inputStream.close();
				outStream.close();
			}

		} catch (Exception e) {
			session.setAttribute(tableName + "_Percentage", 100);
			System.out.println("Exception");
			try {
				PrintWriter pw = null;
				pw = new PrintWriter(
						new FileWriter(new File(System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv")));
				String sql = "select * from " + tableName;
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				// set count
				queryForRowSet.last();
				queryForRowSet.first();
				SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
				StringJoiner csvData = new StringJoiner(",");
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					csvData.add(metaData.getColumnName(i));
				}
				pw.println(csvData);
				long j = 0;
				do {
					j++;
					csvData = new StringJoiner(",");
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						csvData.add(queryForRowSet.getString(i));
					}
					pw.println(csvData);
				} while (queryForRowSet.next());
				pw.flush();
				pw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			e.printStackTrace();
			doDownload(request, response);
		}
	}

	public boolean downloadCsvLocal(HttpServletResponse response, HttpServletRequest request, String appId,
			String reportDate, String reportRun, String tableName, String tableNickName, String resultFileType, boolean directFileFound, String directCsvPath) throws IOException {

		boolean result = true;
		File[] fList = null;
		HttpSession session = request.getSession();

		try {
			String folderName = System.getenv("DATABUCK_HOME") + "/csvFiles/" + appId + "/" + reportDate + "/"
					+ reportRun + "/" + tableNickName;
			if (directFileFound)
				folderName = System.getenv("DATABUCK_HOME") + "/csvFiles/" + appId + "/" + directCsvPath;
			System.out.println("folderName:" + folderName);

			File directory = new File(folderName);
			int totalFileLength = 0;
			String mimeType = "";

			// get all the files from a directory
			fList = directory.listFiles();

			System.out.println("downloadCsvLocal ........ fList =>" + fList);

			// changes for appending validation name to downloadCSVFileName
			String appName = matchingresultdao.getAppNameFromListApplication(Long.parseLong(appId));

			// set headers for the response
			String headerKey = "Content-Disposition";

			String headerValue = String.format("attachment; filename=\"%s\"", tableNickName+"_"+appName + "."+resultFileType);
			response.setHeader(headerKey, headerValue);

			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();


			if (fList != null) {
				// get output stream of the response
				OutputStream outStream = null;

				for (File file : fList) {
					if (file.isFile()) {
						System.out.println(file.getName());
						String fileName = file.getName();

						byte[] buffer = new byte[1024 * 1000];
						int bytesRead = -1;
						if (fileName.endsWith("."+resultFileType)) {

							if(outStream == null) {
								outStream = response.getOutputStream();
							}
							String fileFullPath = folderName + "/" + fileName;
							System.out.println("downloadCsvLocal ........ table for csv+" + fileName);


							String appPath = context.getRealPath("");
							System.out.println("downloadCsvLocal ........ appPath = " + appPath);

							// construct the complete absolute path of the file
							File downloadFile = new File(fileFullPath);
							FileInputStream inputStream = new FileInputStream(downloadFile);

							// get MIME type of the file
							mimeType = context.getMimeType(fileFullPath);
							if (mimeType == null) {
								// set to binary type if MIME mapping not found
								mimeType = "application/octet-stream";
							}
							totalFileLength = totalFileLength + (int)downloadFile.length();
							System.out.println("MIME type: " + mimeType);
							System.out.println("downloadFile.length() =>" + downloadFile.length());

							// write bytes read from the input stream into the
							// output stream
							while ((bytesRead = inputStream.read(buffer)) != -1) {
								outStream.write(buffer, 0, bytesRead);
							}

							result = false;
							inputStream.close();

						}

					}
				}

				response.setContentLength(totalFileLength);
				// set content attributes for the response
				response.setContentType(mimeType);

				if(outStream != null) {
					outStream.close();
				}

			}
		} catch (Exception e) {
			System.out.println("Failed to Download [" + tableNickName + "] from local: File not found !!");
			e.printStackTrace();
		}
		/*if (fList == null) {
			System.out.println("In FList Null...............");
			String message = "'" + tableNickName + "' Data file is not available for Current Run !!";
			session.setAttribute("errormsg", message);
			JSONObject json = new JSONObject();
			json.put("fail", message);
			//response.setContentType("text/html");
			response.getWriter().println(json);
		}*/
		return result;
	}

	@RequestMapping(value = "/DownloadCustomDateRangeReportAsCsv")
	public void DownloadCustomDateRangeReportAsCsv(HttpServletRequest oRequest, HttpServletResponse oResponse) {
		HttpSession session = oRequest.getSession();

		String sIdApp = oRequest.getParameter("dr_idApp");
		String sFeatureNickName = oRequest.getParameter("dr_tableNickName");
		String sIsDataRangeSubmit = oRequest.getParameter("isCustomDateRangeDownload");
		String sDate1 = oRequest.getParameter("Date1");
		String sDate2 = oRequest.getParameter("Date2");

		String sDataBuckHome = "";
		String sDataFileFolder = "";
		String sRandomPath = "";
		String sDownloadFileName = "";
		String sDownloadFileFullName = "";

		DateUtility.DebugLog("DownloadCustomDateRangeReportAsCsv 01", String.format("Parameters %1$s, %2$s, %3$s, %4$s, %5$s", sIdApp, sFeatureNickName, sIsDataRangeSubmit, sDate1, sDate2));

		try {
			sDataBuckHome = System.getenv("DATABUCK_HOME");
			sRandomPath = String.format("%1$s_CustomDateRange_%2$s", sIdApp, DateUtility.getSysDateTimeStamp(true));  //
			sDataFileFolder = String.format("%1$s/csvFiles/%2$s",	sDataBuckHome, sRandomPath);

			sDownloadFileName = getDownloadCsvFileForDateRange(sIdApp, sFeatureNickName, sDate1, sDate2, sDataBuckHome, sRandomPath);
			sDownloadFileFullName = String.format("%1$s/%2$s", sDataFileFolder,sDownloadFileName);

			DateUtility.DebugLog("DownloadCustomDateRangeReportAsCsv 02", String.format("All values needed are '%1$s', '%2$s', '%3$s' '%44s'", sDataBuckHome, sDataFileFolder, sDownloadFileName, sDownloadFileFullName));

			ServletContext oContext = oRequest.getSession().getServletContext();

			File oDownloadFile = new File(sDownloadFileFullName);
			FileInputStream inputStream = new FileInputStream(oDownloadFile);

			String mimeType = oContext.getMimeType(sDownloadFileFullName);
			if (mimeType == null) {	mimeType = "application/octet-stream";	}
			System.out.println("MIME type: " + mimeType);

			oResponse.setContentType(mimeType);
			oResponse.setContentLength((int) oDownloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", sDownloadFileName);
			oResponse.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = oResponse.getOutputStream();

			byte[] buffer = new byte[1024 * 1000];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();

			DateUtility.DebugLog("DownloadCustomDateRangeReportAsCsv 04", "Download Completed without error");

		} catch (Exception oException) {
			DateUtility.DebugLog("DownloadCustomDateRangeReportAsCsv 05", String.format("Download Completed with Exception %1$s", oException.getMessage()));
			oException.printStackTrace();
		}
	}

	private String getDownloadCsvFileForDateRange(String sIdApp, String sFeatureNickName, String sDate1, String sDate2, String sDataBuckHome, String sRandomPath) {
		HashMap<String, String> oGenerateCsvResult = null;

		String sShellScriptNameWithPath = sDataBuckHome+"/scripts/DownloadCsvFileForDateRange.sh";
		String sIncrementalColumn = sFeatureNickName.equalsIgnoreCase("RCAData") ? "Date" : getIncrementalColumnFromAppId(Long.parseLong(sIdApp));
		String sRetValue = String.format("%1$s_%2$s.csv", sIdApp, sFeatureNickName);

		String[] cmd_args  = {sShellScriptNameWithPath, sIdApp, sDate1, sDate2, sFeatureNickName, sRandomPath,  sIncrementalColumn};

		DateUtility.DebugLog("getDownloadCsvFileForDateRange 01", String.format("About to run command line program ", cmd_args.toString()));

		oGenerateCsvResult = JwfSpaInfra.runProgramAndSearchPatternInOutput(cmd_args, null);

		DateUtility.DebugLog("getDownloadCsvFileForDateRange 02", String.format("Run results '%1$s'", oGenerateCsvResult));

		return sRetValue;
	}

	private String getIncrementalColumnFromAppId(long nIdApp) {
		ListApplications oApplicationObject = oValidationCheckDAO.getdatafromlistapplications(nIdApp);
		List<ListDataDefinition> aListDataDefinition = oTemplateviewdao.view(oApplicationObject.getIdData());
		String sRetValue = "";
		boolean lFound = false;

		DateUtility.DebugLog("getIncrementalColumnFromAppId 01", String.format("About to scan all columns for '%1$s' / '%2$s'", oApplicationObject.getIdData(), aListDataDefinition.size()));

		for (ListDataDefinition oColumnDefination : aListDataDefinition) {
			lFound = oColumnDefination.getIncrementalCol().equalsIgnoreCase("Y");
			if (lFound) {
				sRetValue = oColumnDefination.getDisplayName();
				break;
			}
		}
		DateUtility.DebugLog("getIncrementalColumnFromAppId 02", String.format("Column info'%1$s' / '%2$s'", lFound, sRetValue));
		return sRetValue;
	}

	// download CSV From S3
	@RequestMapping(value = "/downloadCsvS3", method = RequestMethod.POST )
	public void downloadCsvS3(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("**** downloadCsvS3Test *****");
		String tableName = request.getParameter("dr_tableName");
		String idApp = request.getParameter("dr_idApp");
		String tableNickName = request.getParameter("dr_tableNickName");
		String reportDate = request.getParameter("reportDate");
		String reportRun = request.getParameter("reportRun");
		String dr_fileSelected = request.getParameter("dr_fileSelected");
		String isDirect = request.getParameter("isDirect");
		String directCsvPath = request.getParameter("directCsvPath");
		System.out.println("idApp:" + idApp);
		System.out.println("tableName:" + tableName);
		System.out.println("tableNickName:" + tableNickName);
		System.out.println("reportDate:" + reportDate);
		System.out.println("reportRun:" + reportRun);
		System.out.println("dr_fileSelected:" + dr_fileSelected);
		System.out.println("isDirect:" + isDirect);
		System.out.println("directCsvPath:" + directCsvPath);

		String folderName = "";
		boolean fileNotFound = true;
		HttpSession session = request.getSession();
		boolean directFileFound = false;

		// Get the result file type
		String resultFileType = appDbConnectionProperties.getProperty("databuck.result.fileType");

		// Consolidated Row Summary will be saved in csv, so result type is csv
		// Default type is 'CSV'
		if (tableNickName != null && tableNickName.trim().equalsIgnoreCase("RowSummary"))
			resultFileType = "csv";
		else if (resultFileType != null && resultFileType.trim().equalsIgnoreCase("parquet"))
			resultFileType = "parquet";
		else
			resultFileType = "csv";

		// changes for appending validation name to downloadCSVFileName
		String appName = matchingresultdao.getAppNameFromListApplication(Long.parseLong(idApp));

		try {
			if (tableName.equalsIgnoreCase("dq_dashboard")) {
				System.out.println("downloadCsvS3 createQualityCsv.............");
			//	iResultsDAO.createQualityCsv();
			} else if (tableName.equalsIgnoreCase("dm_dashboard")) {
				System.out.println("downloadCsvS3 createMatchingCsv ............");
				matchingresultdao.createMatchingCsv(tableName);
			} else if (!tableName.trim().equals("")) {
				
				if (isDirect.equalsIgnoreCase("Y")) {
					System.out.println("direct download ............");
					if (directCsvPath != null && directCsvPath.trim().length() > 0) {
						directFileFound=true;
						folderName = "csvFiles/" + idApp + "/"+ directCsvPath;
					} else {
						folderName = "csvFiles/" + idApp + "/" + reportDate + "/" + reportRun + "/" + tableNickName;
					}
				} else if (dr_fileSelected == null || dr_fileSelected.equalsIgnoreCase("LATEST_FILE")) {
					System.out.println("\n====>Fetching MaxDate and Run of table to download latest file !!");
					SqlRowSet tableDateAndRun = iResultsDAO.getTableMaxDateAndRun(Long.parseLong(idApp));
					if (tableDateAndRun.next()) {
						reportDate = tableDateAndRun.getString("Date");
						int run = tableDateAndRun.getInt("Run");
						reportRun = String.valueOf(run);
						System.out.println("\n===>Max Date:"+reportDate);
						System.out.println("\n===>Max Run:"+reportRun);
					}
				}
				if (!directFileFound) {
					System.out.println("directqqqq download ............");
					folderName = "csvFiles/" + idApp + "/" + reportDate + "/" + reportRun + "/" + tableNickName;
				}

				if (clusterProperties.getProperty("deploymentMode").equals("s3")) {
					System.out.println("\n******* DownloadCsv from S3 *******");

					String s3CsvPath = appDbConnectionProperties.getProperty("s3CsvPath");
					AWSCredentials credentials = new BasicAWSCredentials(
							appDbConnectionProperties.getProperty("s3.aws.accessKey"),
							appDbConnectionProperties.getProperty("s3.aws.secretKey"));

					AmazonS3 s3client = new AmazonS3Client(credentials);
					System.out.println("folderName:" + folderName);
					System.out.println("s3CsvPath:" + s3CsvPath);

					// Get files list in the folder
					List<String> getObjectslistFromFolder = getObjectslistFromFolder(s3CsvPath.split("//")[1],
							folderName);
					Iterator<String> iterator = getObjectslistFromFolder.iterator();

					// get output stream of the response
					OutputStream outStream = null;
					// set headers for the response
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", tableNickName+"_"+appName + "."+resultFileType);

					while (iterator.hasNext()) {
						String list = iterator.next();
						if (list.endsWith("."+resultFileType)) {
							folderName = list;

							System.out.println("File full path:" + folderName);
							S3Object s3object = s3client.getObject(new GetObjectRequest(s3CsvPath.split("//")[1], folderName));

							System.out.println("downloadCsvS3 s3object getContentType()......"
									+ s3object.getObjectMetadata().getContentType());

							System.out.println("downloadCsvS3 s3object getContentLength()......"
									+ s3object.getObjectMetadata().getContentLength());
							BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));

							String line;

							if (outStream == null) {
								outStream = response.getOutputStream();
								response.setHeader(headerKey, headerValue);
							}
							while ((line = reader.readLine()) != null) {
								outStream.write(line.getBytes());
								outStream.write("\n".getBytes());
							}
							outStream.write("\n".getBytes());
							fileNotFound = false;
						}
					}
				} else if (clusterProperties.getProperty("deploymentMode").equals("hdfs")) {
					System.out.println("\n***** Download CSV from HDFS *****");

					String hdfsCvsPath = clusterProperties.getProperty("hdfs_result_directory");

					FileSystem hdfsClient = getHDFSClient();
					System.out.println("folderName:" + folderName);
					System.out.println("hdfsCvsPath:" + hdfsCvsPath);

					String fullFolderPath = hdfsCvsPath + "/" + folderName;
					System.out.println("fullFolderPath:" + fullFolderPath);

					// Get files list in the folder
					RemoteIterator<LocatedFileStatus> fileStatusListIterator = hdfsClient
							.listFiles(new Path(fullFolderPath), false);

					Path fileHdfsPath = null;
					while (fileStatusListIterator.hasNext()) {
						LocatedFileStatus fileStatus = fileStatusListIterator.next();
						if (fileStatus.getPath().toString().endsWith("."+resultFileType)) {
							fileHdfsPath = fileStatus.getPath();
						}
					}

					FSDataInputStream inputStream = hdfsClient.open(fileHdfsPath);

					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					// set headers for the response
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", tableNickName+"_"+appName + "."+resultFileType);
					response.setHeader(headerKey, headerValue);

					// get output stream of the response
					OutputStream outStream = response.getOutputStream();
					String line;

					while ((line = reader.readLine()) != null) {
						outStream.write(line.getBytes());
						outStream.write("\n".getBytes());
					}
					fileNotFound = false;

				} else {
					fileNotFound = downloadCsvLocal(response, request, idApp, reportDate, reportRun, tableName,
							tableNickName, resultFileType, directFileFound, directCsvPath);
				}
			}

		} catch (Exception e) {
			System.out.println("Failed to Download [" + tableNickName + "]: File not found !!");
			e.printStackTrace();
		}

		if (fileNotFound) {
			System.out.println("In fileNotFound============"+fileNotFound);
			String message = "'" + tableNickName + "' Data file is not available for Current Run !!";
			session.setAttribute("errormsg", message);
			JSONObject json = new JSONObject();
			json.put("fail", message);
			response.getWriter().println(json);
		}
	}

	// ------------------

	public List<String> getObjectslistFromFolder(String bucketName, String folderKey) {
		System.out.println("\n====> Getting Object list from folder ....");
		System.out.println("\n====> BucketName:" + bucketName);
		System.out.println("\n====> folderKey:" + folderKey);

		String s3CsvPath = appDbConnectionProperties.getProperty("s3CsvPath");
		AWSCredentials credentials = new BasicAWSCredentials(appDbConnectionProperties.getProperty("s3.aws.accessKey"),
				appDbConnectionProperties.getProperty("s3.aws.secretKey"));

		AmazonS3 s3client = new AmazonS3Client(credentials);

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName)
				.withPrefix(folderKey + "/");

		List<String> keys = new ArrayList<>();

		ObjectListing objects = s3client.listObjects(listObjectsRequest);
		for (;;) {
			List<S3ObjectSummary> summaries = objects.getObjectSummaries();
			if (summaries.size() < 1) {
				break;
			}
			summaries.forEach(s -> keys.add(s.getKey()));
			objects = s3client.listNextBatchOfObjects(objects);
		}

		return keys;
	}

	@RequestMapping(value = "/downloadDefaultCsv")
	public void doDownloadDefault(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String tableName = request.getParameter("tableName");
		try {
			System.out.println("tableName::" + tableName);

			if (clusterProperties.getProperty("app_mode").trim().equalsIgnoreCase("1")) {
				writeMongoToCsv(tableName, request);
			} else {
				System.out.println("session.setAttribute(tableName + _Percentage, 100");
				session.setAttribute(tableName + "_Percentage", 100);
			}
			if (tableName.equalsIgnoreCase("dq_dashboard")) {
				System.out.println("createQualityCsv");
			//	iResultsDAO.createQualityCsv();
			}
			if (tableName.equalsIgnoreCase("dm_dashboard")) {
				System.out.println("createMatchingCsv");
				matchingresultdao.createMatchingCsv(tableName);
			}
			if (!tableName.trim().equals("")) {
				String fileFullPath = null;
				if (tableName.toUpperCase().contains("DATA_QUALITY")) {
					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + "_Default.csv";
				} else {
					fileFullPath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + "_Default.csv";
				}
				// String
				// fileFullPath="C:\\Users\\appzop6\\Downloads\\"+tableName+".csv";
				// String fileFullPath="E:\\backup\\Tuesday\\databuck.zip";
				System.out.println("table for csv+" + tableName);
				// get absolute path of the application
				ServletContext context = request.getSession().getServletContext();
				String appPath = context.getRealPath("");
				System.out.println("appPath = " + appPath);

				// construct the complete absolute path of the file
				File downloadFile = new File(fileFullPath);
				FileInputStream inputStream = new FileInputStream(downloadFile);

				// get MIME type of the file
				String mimeType = context.getMimeType(fileFullPath);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}
				System.out.println("MIME type: " + mimeType);

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", tableName + "_Default.csv");
				response.setHeader(headerKey, headerValue);

				// get output stream of the response
				OutputStream outStream = response.getOutputStream();

				byte[] buffer = new byte[1024 * 1000];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}

				inputStream.close();
				outStream.close();
			}

		} catch (Exception e) {
			session.setAttribute(tableName + "_Percentage", 100);
			System.out.println("Exception");
			try {
				PrintWriter pw = null;
				pw = new PrintWriter(new FileWriter(
						new File(System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + "_Default.csv")));
				String sql = "select Date, Run, ColName as 'Default ColNames', Default_Value, Default_Count, Default_Percentage from "
						+ tableName;

				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				// set count
				queryForRowSet.last();
				queryForRowSet.first();
				SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
				StringJoiner csvData = new StringJoiner(",");
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					csvData.add(metaData.getColumnName(i));
				}
				pw.println(csvData);
				long j = 0;
				do {
					j++;
					csvData = new StringJoiner(",");
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						csvData.add(queryForRowSet.getString(i));
					}
					pw.println(csvData);
				} while (queryForRowSet.next());
				pw.flush();
				pw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			e.printStackTrace();
			doDownloadDefault(request, response);
		}
	}

	private void writeMongoToCsv(String tableName, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			Process process = null;
			try {
				Properties prop = new Properties();
				InputStream is = new FileInputStream(
						new File(System.getenv("DATABUCK_HOME") + "/propertiesFiles/mongodb.properties"));
				prop.load(is);
				String database = prop.getProperty("databaseName");
				String ipAddress = prop.getProperty("ipAddress");
				String port = prop.getProperty("port");

				System.out.println("writing from mongo db");

				List<String> getallColumns = iResultsDAO.getallColumns(tableName);

				String allColumns = StringUtils.collectionToCommaDelimitedString(getallColumns);

				String command = "mongoexport --db " + database + "  --host " + ipAddress + " --port " + port
						+ "  --type=csv --collection " + tableName + " --fields " + allColumns + "  --out  "
						+ System.getenv("DATABUCK_HOME") + "/csvFiles/" + tableName + ".csv";

				System.out.println(command);
				process = Runtime.getRuntime().exec(command);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			BufferedReader reader = null;
			if (process != null) {
				reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			}
			String line = "";
			long percentage = -1;
			try {
				while ((line = reader.readLine()) != null) {
					System.out.println(line + "\n");
					if (line.contains("exported")) {
						session.setAttribute(tableName + "_Percentage", 100);
					} else {
						int first = line.indexOf("(");
						int last = line.indexOf(")");
						try {
							if (first != -1 && last != -1) {
								percentage = Math.round(Double.parseDouble(line.substring(first + 1, last - 1)));
								System.out.println("percentage:" + percentage);
								session.setAttribute(tableName + "_Percentage", percentage);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				process.waitFor();
			} catch (InterruptedException e3) {
				e3.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileSystem getHDFSClient() {
		//DistributedFileSystem dFS = null;
		try {

			// Get the name of the cluster to get the hdfs cluster name
			System.out.println(" HDFS kerberos integartion ");

			Configuration conf = new Configuration();

			// Pull this from properties file for now
			conf.set("hadoop.security.authentication", "kerberos");
			conf.set("dfs.namenode.kerberos.principal.pattern", "*");

			String gssJassFilePath = appDbConnectionProperties.getProperty("gssJass_File_Path");
			System.out.println("gssJassFilePath:" + gssJassFilePath);

			System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
			System.setProperty("java.security.auth.login.config", gssJassFilePath);
			System.setProperty("hadoop.security.authentication", "kerberos");
			System.setProperty("sun.security.krb5.debug", "true");

			LoginContext lc = new LoginContext("com.sun.security.jgss.krb5.initiate");
			lc.login();

			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation.loginUserFromSubject(lc.getSubject());

			String hdfsUri = clusterProperties.getProperty("hdfs_uri");
			System.out.println("hdfsUri:" + hdfsUri);

			/*dFS = new DistributedFileSystem() {
				{
					initialize(new URI(hdfsUri), conf);
				}
			};
			*/
		} catch (Exception e) {
			System.out.println("\n=====>Caught exception while reading creating HDFS client !!");
			e.printStackTrace();

		}

		return null;
	}
}