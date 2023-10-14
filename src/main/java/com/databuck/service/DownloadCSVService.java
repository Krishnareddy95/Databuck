package com.databuck.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.security.auth.login.LoginContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.ListApplications;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.util.DatabuckUtility;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.dao.SchemaDAOI;

@Service
public class DownloadCSVService {

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
	private DownloadCSVService downloadCSVService;

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	private ITaskDAO taskDAO;

	private static final Logger LOG = Logger.getLogger(DownloadCSVService.class);

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1) {
		this.jdbcTemplate1 = jdbcTemplate1;
		this.jdbcTemplate = jdbcTemplate;
	}

    public String downloadCSV(Map<String, String> params, HttpServletResponse response,
                            HttpServletRequest request) {

        String message = "";
        HttpSession session = request.getSession();
        boolean fileNotFound = true;

        try {
            long idApp = 0l;
            int runNumber = 0;
            String runDate = "" + params.get("runDate");
            String checkName = "" + params.get("checkName");
            String columnName = "" + params.get("columnName");

            String exceptionDataPath = "";
            boolean isRequestValid = true;

            try {
                idApp = Long.parseLong(params.get("idApp"));
                runNumber = Integer.parseInt(params.get("runNumber"));
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }

            if (idApp <= 0l || runNumber <= 0)
                isRequestValid = false;
            else if (runDate.trim().isEmpty() || checkName.trim().isEmpty() || columnName.trim().isEmpty())
                isRequestValid = false;

            String appName ="";
            try {
                appName = matchingresultdao.getAppNameFromListApplication(Long.parseLong("" + idApp));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(appName==null || appName.trim().isEmpty()){
                message="Invalid validation";
                isRequestValid=false;
            }

            try {
                boolean pluginAvailable = false;
                String databuckHome = DatabuckUtility.getDatabuckHome();
                System.out.println("\n====>databuckHome:" + databuckHome);

                String jarPath = databuckHome + "/csv-downloader.jar";
                if (appDbConnectionProperties.containsKey("csv_downloader_path"))
                    jarPath = appDbConnectionProperties.getProperty("csv_downloader_path");

                if (!Files.exists(Paths.get(jarPath))) {
                    message = "Plugin jar file ["+jarPath+"] does not exists";
                }else
                    pluginAvailable = true;

                if (isRequestValid) {

                    Properties exceptionPathProperties = new Properties(appDbConnectionProperties);

                    appDbConnectionProperties.forEach((key, value) -> exceptionPathProperties.setProperty((String) key, ""+value));

                    exceptionDataPath = "csvFiles/" + idApp + "/" + runDate + "/" + runNumber + "/" + checkName + "/" + columnName;

                    // Get the result file type
                    String resultFileType = appDbConnectionProperties.getProperty("databuck.result.fileType");

                    // Consolidated Row Summary will be saved in csv, so result type is csv
                    // Default type is 'CSV'
                    if (resultFileType == null || resultFileType.trim().isEmpty())
                        resultFileType = "csv";

                    if (clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("local")) {
                        fileNotFound = downloadCsvLocal(response, request, "" + idApp,
                                runDate, "" + runNumber, "", checkName + "/" + columnName, resultFileType,
                                false, "");
                    } else if(pluginAvailable){

                        if (clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("hdfs")) {
                            String hdfs_result_directory = clusterProperties.getProperty("hdfs_result_directory");
                            if (hdfs_result_directory != null)
                                hdfs_result_directory = hdfs_result_directory.trim();

                            // Mapr ticket enabled is enabled store the data in project/domain location
                            String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

                            String csvLocation = "";
                            if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {

                                ListApplications listApplications = oValidationCheckDAO.getdatafromlistapplications(idApp);
                                Long projectId = listApplications.getProjectId();
                                String sorName = projectDAO.getProjectNameByProjectid(projectId);
                                String domain = taskDAO.getDomainNameById(listApplications.getDomainId());

                                if (resultFileType != null && resultFileType.trim().equalsIgnoreCase("hivetable")) {
                                    csvLocation = hdfs_result_directory + "/" + domain + "/" + sorName + "/idapp=" + idApp + "/date="
                                            + runDate + "/run=" + runNumber + "/" + "checkname=" + checkName + "/columnname=" + columnName;
                                } else {
                                    csvLocation = hdfs_result_directory + "/" + domain + "/" + sorName + "/" + idApp + "/" + runDate + "/"
                                            + runNumber + "/" + checkName + "/" + columnName;
                                }

                            } else {

                                if (resultFileType != null && resultFileType.trim().equalsIgnoreCase("hivetable")) {
                                    csvLocation = hdfs_result_directory + "/csvFiles/idapp=" + idApp + "/date=" + runDate + "/run=" + runNumber
                                            + "/" + "checkname=" + checkName + "/columnname=" + columnName;
                                } else {
                                    csvLocation = hdfs_result_directory + "/csvFiles/" + idApp + "/" + runDate + "/" + runNumber + "/" + checkName + "/" + columnName;
                                }
                            }
                            System.out.println("\n====>csvLocation:" + csvLocation);

                            if (!csvLocation.trim().isEmpty())
                                exceptionDataPath = csvLocation;

                            String hadoopUser = appDbConnectionProperties.getProperty("hdfs_user");
                            String hdfsTargetFileName = checkName.replace("/", "_") + "_" + idApp;

                            exceptionPathProperties.setProperty("hadoopUser", hadoopUser);
                            exceptionPathProperties.setProperty("hdfsTargetFileName", hdfsTargetFileName);
                        }

                        System.out.println("\n====>Exception Data Path=" + exceptionDataPath);

                        String className = "com.databuck.plugin." + clusterProperties.getProperty("deploymentMode").trim().toUpperCase() + "CsvDownloader";

                        System.out.println("\n====>Executing:" + clusterProperties.getProperty("deploymentMode").trim().toUpperCase() + "CsvDownloader");

                        // set headers for the response
                        String headerKey = "Content-Disposition";
                        String headerValue = String.format("attachment; filename=\"%s\"",
                                checkName + "_" + appName + "." + resultFileType);
                        response.setHeader(headerKey, headerValue);

                        // get output stream of the response
                        OutputStream outStream = response.getOutputStream();
                        response.setHeader(headerKey, headerValue);

                        URL[] urls = {new URL("jar:file:" + jarPath + "!/")};

                        Object downloaderObj = null;
                        Method downloadCSV = null;

                        try {
                            URLClassLoader cl = URLClassLoader.newInstance(urls);
                            Class downloader = cl.loadClass(className);
                            downloadCSV = downloader.getMethod("downloadCSV", Properties.class);
                            downloaderObj = downloader.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                            pluginAvailable = false;
                            message = e.getLocalizedMessage();
                        }

                        if (pluginAvailable) {
                            if (outStream == null) {
                                outStream = response.getOutputStream();
                                response.setHeader(headerKey, headerValue);
                            }
                            exceptionPathProperties.setProperty("filePath", exceptionDataPath);
                            exceptionPathProperties.setProperty("resultFileType", resultFileType);

                            String data = (String) downloadCSV.invoke(downloaderObj, exceptionPathProperties);

                            if (data == null || data.trim().isEmpty()) {
                                fileNotFound = true;
                                System.out.println("\n====>File Not found");
                            } else {
                                System.out.println("\n====>Data is received to databuck through plugin");
                                fileNotFound = false;
                                outStream.write(data.getBytes());
                            }
                        } else {
                            message = "\n====>Downloader Plugin Not available for " + clusterProperties.getProperty("deploymentMode");
                        }
                    }else
                        System.out.println(message);
                }else{
                    System.out.println("Request is invalid");
                    if(message.trim().isEmpty())
                        message="Request is invalid";
                }

            } catch (Exception e) {
                LOG.error("Failed to Download [" + exceptionDataPath + "]: File not found !!");
                LOG.error(e.getMessage());
                e.printStackTrace();
                if (message.trim().isEmpty())
                    message = e.getMessage();
            }

            if (fileNotFound) {
                LOG.error("In fileNotFound============" + fileNotFound);
                if (message.trim().isEmpty())
                    message = "'" + exceptionDataPath + "' Data file is not available for Current Run !!";
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (message.trim().isEmpty())
                message = e.getMessage();
        }
        return message;
    }

	public void downloadCSV(String tableName, String idApp, String tableNickName, String reportDate, String reportRun,
			String dr_fileSelected, String isDirect, String directCsvPath, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
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
				LOG.info("downloadCsvS3 createQualityCsv.............");
				// iResultsDAO.createQualityCsv();
			} else if (tableName.equalsIgnoreCase("dm_dashboard")) {
				LOG.info("downloadCsvS3 createMatchingCsv ............");
				matchingresultdao.createMatchingCsv(tableName);
			} else if (!tableName.trim().equals("")) {

				if (isDirect.equalsIgnoreCase("Y")) {
					LOG.info("direct download ............");
					if (directCsvPath != null && directCsvPath.trim().length() > 0) {
						directFileFound = true;
						folderName = "csvFiles/" + idApp + "/" + directCsvPath;
					} else {
						folderName = "csvFiles/" + idApp + "/" + reportDate + "/" + reportRun + "/" + tableNickName;
					}
				} else if (dr_fileSelected == null || dr_fileSelected.equalsIgnoreCase("LATEST_FILE")) {
					LOG.info("\n====>Fetching MaxDate and Run of table to download latest file !!");
					SqlRowSet tableDateAndRun = iResultsDAO.getTableMaxDateAndRun(Long.parseLong(idApp));
					if (tableDateAndRun.next()) {
						reportDate = tableDateAndRun.getString("Date");
						int run = tableDateAndRun.getInt("Run");
						reportRun = String.valueOf(run);
						LOG.debug("\n===>Max Date:" + reportDate);
						LOG.debug("\n===>Max Run:" + reportRun);
					}
				}
				if (!directFileFound) {
					LOG.info("directqqqq download ............");
					folderName = "csvFiles/" + idApp + "/" + reportDate + "/" + reportRun + "/" + tableNickName;
				}

				if (clusterProperties.getProperty("deploymentMode").equals("s3")) {
					LOG.info("\n******* DownloadCsv from S3 *******");

					String s3CsvPath = appDbConnectionProperties.getProperty("s3CsvPath");
					AWSCredentials credentials = new BasicAWSCredentials(
							appDbConnectionProperties.getProperty("s3.aws.accessKey"),
							appDbConnectionProperties.getProperty("s3.aws.secretKey"));

					AmazonS3 s3client = new AmazonS3Client(credentials);
					LOG.debug("folderName:" + folderName);
					LOG.debug("s3CsvPath:" + s3CsvPath);

					// Get files list in the folder
					List<String> getObjectslistFromFolder = getObjectslistFromFolder(s3CsvPath.split("//")[1],
							folderName);
					Iterator<String> iterator = getObjectslistFromFolder.iterator();

					// get output stream of the response
					OutputStream outStream = null;
					// set headers for the response
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"",
							tableNickName + "_" + appName + "." + resultFileType);

					while (iterator.hasNext()) {
						String list = iterator.next();
						if (list.endsWith("." + resultFileType)) {
							folderName = list;

							LOG.debug("File full path:" + folderName);
							S3Object s3object = s3client
									.getObject(new GetObjectRequest(s3CsvPath.split("//")[1], folderName));

							LOG.debug("downloadCsvS3 s3object getContentType()......"
									+ s3object.getObjectMetadata().getContentType());

							LOG.debug("downloadCsvS3 s3object getContentLength()......"
									+ s3object.getObjectMetadata().getContentLength());
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(s3object.getObjectContent()));

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
					try {
						LOG.info("\n***** Download CSV from HDFS *****");

						String hdfsCvsPath = clusterProperties.getProperty("hdfs_result_directory");

						FileSystem hdfsClient = getHDFSClient();
						LOG.debug("folderName:" + folderName);
						LOG.debug("hdfsCvsPath:" + hdfsCvsPath);

						String fullFolderPath = hdfsCvsPath + "/" + folderName;
						LOG.debug("fullFolderPath:" + fullFolderPath);

						// Get files list in the folder
						RemoteIterator<LocatedFileStatus> fileStatusListIterator = hdfsClient
								.listFiles(new Path(fullFolderPath), false);

						Path fileHdfsPath = null;
						while (fileStatusListIterator.hasNext()) {
							LocatedFileStatus fileStatus = fileStatusListIterator.next();
							if (fileStatus.getPath().toString().endsWith("." + resultFileType)) {
								fileHdfsPath = fileStatus.getPath();
							}
						}

						FSDataInputStream inputStream = hdfsClient.open(fileHdfsPath);

						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

						// set headers for the response
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"",
								tableNickName + "_" + appName + "." + resultFileType);
						response.setHeader(headerKey, headerValue);

						// get output stream of the response
						OutputStream outStream = response.getOutputStream();
						String line;

						while ((line = reader.readLine()) != null) {
							outStream.write(line.getBytes());
							outStream.write("\n".getBytes());
						}
						fileNotFound = false;
					} catch (Exception ex) {
						fileNotFound = true;
						LOG.error(ex.getMessage());
						ex.printStackTrace();
					}

				} else {
					fileNotFound = downloadCsvLocal(response, request, idApp, reportDate, reportRun, tableName,
							tableNickName, resultFileType, directFileFound, directCsvPath);
				}
			}

		} catch (Exception e) {
			LOG.error("Failed to Download [" + tableNickName + "]: File not found !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		if (fileNotFound) {
			LOG.error("In fileNotFound============" + fileNotFound);
			String message = "'" + tableNickName + "' Data file is not available for Current Run !!";
			session.setAttribute("errormsg", message);
			JSONObject json = new JSONObject();
			json.put("fail", message);
			response.getWriter().println(json);
		}
	}

	public List<String> getObjectslistFromFolder(String bucketName, String folderKey) {
		LOG.info("\n====> Getting Object list from folder ....");
		LOG.debug("\n====> BucketName:" + bucketName);
		LOG.debug("\n====> folderKey:" + folderKey);

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

	public FileSystem getHDFSClient() throws Exception {
		// DistributedFileSystem dFS = null;
		try {

			// Get the name of the cluster to get the hdfs cluster name
			LOG.info(" HDFS kerberos integartion ");

			Configuration conf = new Configuration();

			// Pull this from properties file for now
			conf.set("hadoop.security.authentication", "kerberos");
			conf.set("dfs.namenode.kerberos.principal.pattern", "*");

			String gssJassFilePath = appDbConnectionProperties.getProperty("gssJass_File_Path");
			LOG.debug("gssJassFilePath:" + gssJassFilePath);

			System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
			System.setProperty("java.security.auth.login.config", gssJassFilePath);
			System.setProperty("hadoop.security.authentication", "kerberos");
			System.setProperty("sun.security.krb5.debug", "true");

			LoginContext lc = new LoginContext("com.sun.security.jgss.krb5.initiate");
			lc.login();

			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation.loginUserFromSubject(lc.getSubject());

			String hdfsUri = clusterProperties.getProperty("hdfs_uri");
			LOG.debug("hdfsUri:" + hdfsUri);

			/*
			 * dFS = new DistributedFileSystem() { { initialize(new URI(hdfsUri), conf); }
			 * };
			 */
		} catch (Exception e) {
			LOG.error("\n=====>Caught exception while reading creating HDFS client !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw new Exception("Caught exception while reading creating HDFS client");
		}

		return null;
	}

	public boolean downloadCsvLocal(HttpServletResponse response, HttpServletRequest request, String appId,
			String reportDate, String reportRun, String tableName, String tableNickName, String resultFileType,
			boolean directFileFound, String directCsvPath) throws IOException {

		boolean result = true;
		File[] fList = null;
		HttpSession session = request.getSession();

		try {
			String folderName = System.getenv("DATABUCK_HOME") + "/csvFiles/" + appId + "/" + reportDate + "/"
					+ reportRun + "/" + tableNickName;
			if (directFileFound)
				folderName = System.getenv("DATABUCK_HOME") + "/csvFiles/" + appId + "/" + directCsvPath;
			LOG.debug("folderName:" + folderName);

			File directory = new File(folderName);
			int totalFileLength = 0;
			String mimeType = "";

			// get all the files from a directory
			fList = directory.listFiles();

			LOG.debug("downloadCsvLocal ........ fList =>" + fList);

			// changes for appending validation name to downloadCSVFileName
			String appName = matchingresultdao.getAppNameFromListApplication(Long.parseLong(appId));

			// set headers for the response
			String headerKey = "Content-Disposition";

			String headerValue = String.format("attachment; filename=\"%s\"",
					tableNickName + "_" + appName + "." + resultFileType);
			response.setHeader(headerKey, headerValue);

			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();

			if (fList != null) {
				// get output stream of the response
				OutputStream outStream = null;

				for (File file : fList) {
					if (file.isFile()) {
						LOG.debug("File Name" + file.getName());
						String fileName = file.getName();

						byte[] buffer = new byte[1024 * 1000];
						int bytesRead = -1;
						if (fileName.endsWith("." + resultFileType)) {

							if (outStream == null) {
								outStream = response.getOutputStream();
							}
							String fileFullPath = folderName + "/" + fileName;
							LOG.debug("downloadCsvLocal ........ table for csv+" + fileName);

							String appPath = context.getRealPath("");
							LOG.debug("downloadCsvLocal ........ appPath = " + appPath);

							// construct the complete absolute path of the file
							File downloadFile = new File(fileFullPath);
							FileInputStream inputStream = new FileInputStream(downloadFile);

							// get MIME type of the file
							mimeType = context.getMimeType(fileFullPath);
							if (mimeType == null) {
								// set to binary type if MIME mapping not found
								mimeType = "application/octet-stream";
							}
							totalFileLength = totalFileLength + (int) downloadFile.length();
							LOG.debug("MIME type: " + mimeType);
							LOG.debug("downloadFile.length() =>" + downloadFile.length());

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

				//response.setContentLength(totalFileLength);
				// set content attributes for the response
				response.setContentType(mimeType);

				if (outStream != null) {
					outStream.close();
				}

			}
		} catch (Exception e) {
			LOG.error("Failed to Download [" + tableNickName + "] from local: File not found !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		/*
		 * if (fList == null) { LOG.info("In FList Null...............");
		 * String message = "'" + tableNickName +
		 * "' Data file is not available for Current Run !!";
		 * session.setAttribute("errormsg", message); JSONObject json = new
		 * JSONObject(); json.put("fail", message);
		 * //response.setContentType("text/html"); response.getWriter().println(json); }
		 */
		return result;
	}

	public boolean downloadCSVDateRange(String tableName, String idApp, List<String> tableNickNameList, String reportFromDate,String reportToDate, String fileSelected, String directCsvPath, HttpServletResponse response,
			HttpServletRequest request) {
		boolean result = true;
		File[] fList = null;
		try {
			TreeMap<String,ArrayList<TreeMap<String,java.nio.file.Path>>> folderList = new TreeMap<String,ArrayList<TreeMap<String,java.nio.file.Path>>>();
			List<String> dateList = new ArrayList<String>();
			LocalDate start = LocalDate.parse(reportFromDate);
			LocalDate end = LocalDate.parse(reportToDate);
			while (!start.isAfter(end)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");		        
				dateList.add(start.format(formatter));
			    start = start.plusDays(1);
			}
			for (String reportDate : dateList) {
				ArrayList<TreeMap<String,java.nio.file.Path>> folderListforDate = new ArrayList<TreeMap<String,java.nio.file.Path>>();
				String reportRunpath = System.getenv("DATABUCK_HOME") + "/csvFiles/" + idApp + "/" + reportDate + "/";
				File file = new File(reportRunpath);
				if(file.exists() && file.isDirectory()) {
					String[] directories = file.list(new FilenameFilter() {
			            @Override
			            public boolean accept(File dir, String name) {
			                return new File(dir, name).isDirectory();
			            }
			        });
					Integer reportRun = getMaxRun (reportRunpath,directories);
					if(reportRun!=null) {
				        for (String tableNickName : tableNickNameList) { // search all the csv files from the folder
				        	java.nio.file.Path fileFullPath = getValidFileforRun(idApp,reportDate,reportRun,tableNickName,fileSelected);
				        	if(fileFullPath!=null) {
				        		String[] NickNameList = tableNickName.split("/");
				        		String columnName = NickNameList[1];
				        		TreeMap<String,java.nio.file.Path> columnPath = new TreeMap<String,java.nio.file.Path>();
				        		columnPath.put(columnName,fileFullPath);
				        		folderListforDate.add(columnPath);
				        	}else { //for some checks folder name is different 
				        		java.nio.file.Path fileFullPathwithDateinName = getValidFileforRun(idApp,reportDate,reportRun,tableNickName+"_"+reportDate,fileSelected);
					        	if(fileFullPathwithDateinName!=null) {
					        		String[] NickNameList = tableNickName.split("/");
					        		String columnName = NickNameList[1]; 
					        		TreeMap<String,java.nio.file.Path> columnPath = new TreeMap<String,java.nio.file.Path>();
					        		columnPath.put(columnName,fileFullPathwithDateinName);
					        		folderListforDate.add(columnPath);
					        	}
				        	}
				        }
					}
					folderList.put(reportDate, folderListforDate);
				}
			}
			int totalFileLength = 0;
			String mimeType = "";
			LOG.debug("downloadCsvLocal ........ fList =>" + fList);

			// changes for appending validation name to downloadCSVFileName
			String appName = matchingresultdao.getAppNameFromListApplication(Long.parseLong(idApp));

			// set headers for the response
			String headerKey = "Content-Disposition";

			String headerValue = String.format("attachment; filename=\"%s\"",
					"Consolidates_latest_Run_" + appName + "." + fileSelected);
			response.setHeader(headerKey, headerValue);

			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			File directory = new File(System.getenv("DATABUCK_HOME") + "/csvFiles/mergedFiles");
		    if (! directory.exists()){
		        directory.mkdir();
		    }
			java.nio.file.Path target = Paths.get(System.getenv("DATABUCK_HOME") + "/csvFiles/mergedFiles/merged"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".csv");
			List<String> mergedLines = new ArrayList<String>();
			Boolean isHeader = true;
			for (Map.Entry<String, ArrayList<TreeMap<String,java.nio.file.Path>>> entry : folderList.entrySet()) {
				mergedLines.addAll(getMergedLines(entry.getKey(),entry.getValue(),isHeader)); // Add all csv data into single file
				if(isHeader)
					isHeader = !isHeader;
			}
			Files.write(target, mergedLines, Charset.forName("UTF-8"));
			// get output stream of the response
			OutputStream outStream = null;
			if (outStream == null) {
				outStream = response.getOutputStream();
			}

			byte[] buffer = new byte[1024 * 1000];
			int bytesRead = -1;
			// construct the complete absolute path of the file
			File downloadFile = new File(target.toString()); 
			FileInputStream inputStream = new FileInputStream(downloadFile);
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			

			// get MIME type of the file
			mimeType = context.getMimeType(target.toString());
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			totalFileLength = totalFileLength + (int) downloadFile.length();
			LOG.debug("MIME type: " + mimeType);
			LOG.debug("downloadFile.length() =>" + downloadFile.length());

			// write bytes read from the input stream into the
			// output stream
			result = false;
			inputStream.close();
			
			response.setContentType(mimeType);
			if (outStream != null) {
				outStream.close();
			}
		} catch (Exception e) {
			LOG.error("Failed to Download from local: File not found !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static List<String> getMergedLines(String date,List<TreeMap<String,java.nio.file.Path>> paths,Boolean isHeader) throws IOException {
	    List<String> mergedLines = new ArrayList<> ();
	    for (TreeMap<String,java.nio.file.Path> p : paths){
	    	for (Map.Entry<String,java.nio.file.Path> entry : p.entrySet()) {
		        List<String> lines = Files.readAllLines(entry.getValue(), Charset.forName("UTF-8"));
		        if (!lines.isEmpty()) {
		            if (mergedLines.isEmpty()) {
		            	mergedLines.add(date);
		            	if(isHeader) {
		            		mergedLines.add("Column Name & Date,"+lines.get(0)); // Add header only once
		            	}
		            }
		            for(int i = 0;i<lines.size();i++)
		            {
		            	lines.set(i,"\" \","+lines.get(i));// Add extra column for column name & date
		            }
		            mergedLines.add(entry.getKey());
		            mergedLines.addAll(lines.subList(1,lines.size()));
		        }
	    	}
	    }
	    return mergedLines;
	}
	
	private Integer getMaxRun (String reportRunpath,String[] directories) {
        int[] reportRunArr = Arrays.stream(directories).mapToInt(Integer::parseInt).toArray();  
        Integer reportRun = Arrays.stream(reportRunArr).max().orElse(0);
		return reportRun;
	}
	
	private java.nio.file.Path getValidFileforRun (String idApp,String reportDate, Integer reportRun,String tableNickName,String fileSelected ) {
		File[] fList = null;
		String folderName = System.getenv("DATABUCK_HOME") + "/csvFiles/" + idApp + "/" + reportDate + "/"+ reportRun + "/" + tableNickName;
		File directory = new File(folderName);
		fList = directory.listFiles();
		if (fList != null && reportRun>0) {
			for (File csvfile : fList) {
				if (csvfile.isFile()) {
					String fileName = csvfile.getName();
					if (fileName.endsWith("." + fileSelected)) {
						String fileFullPath = directory.getAbsolutePath() + "/" + fileName;
						return Paths.get(fileFullPath);
					}else {
						getValidFileforRun(idApp,reportDate, --reportRun,tableNickName,fileSelected);
					}

				}
			}
		}
		return null;
	}
}
