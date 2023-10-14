package com.databuck.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ColumnCombinationProfile_DP;
import com.databuck.bean.ColumnProfileDelta_DP;
import com.databuck.bean.ColumnProfileDetails_DP;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.NumericalProfile_DP;
import com.databuck.bean.Project;
import com.databuck.bean.RowProfile_DP;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.SendEmailNotificationUtil;

@Controller
public class DataProfilingController {

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	public Properties appDbConnectionProperties;

	@Autowired
	private SendEmailNotificationUtil sendEmailNotificationUtil;
	
	@Autowired
	private IProjectDAO iProjectDAO;

	@RequestMapping(value = "/dataProfiling_View")
	public ModelAndView dataProfilingView(HttpServletRequest request, ModelAndView model, HttpSession session,
			HttpServletResponse response) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Results", "R", session);

		// Get TemplateId
		String idDataStr = request.getParameter("idData");
		Long idData = Long.parseLong(idDataStr);

		session.setAttribute("idData", "true");

		// Get Template Name
		String appName = "";
		List<ListDataSource> listApplicationsData = listdatasourcedao.getListDataSourceTableId(idData);

		for (ListDataSource ld : listApplicationsData) {
			appName = ld.getName();
		}

		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dataProfiling_View");

			// for RowProfile_DP
			List<RowProfile_DP> rowProfileList = listdatasourcedao.readRowProfileForTemplate(idData);

			// for NumericalProfile_DP
			List<NumericalProfile_DP> numericProfileList = listdatasourcedao.readNumericProfileForTemplate(idData);

			// for ColumnProfileDetails_DP
			List<ColumnProfileDetails_DP> columnProfileDetailsList = listdatasourcedao
					.readColumnProfileDetailsForTemplate(idData);

			// for ColumnProfile
			List<ColumnProfile_DP> precolumnProfileList = listdatasourcedao.readColumnProfileForTemplate(idData);
			List<ColumnProfile_DP> newColumnProfileList = getNewColumnsDelta(idData, precolumnProfileList);
			List<ColumnProfile_DP> missingColumnProfileList = getMissingColumnsDelta(idData, precolumnProfileList);
			List columnProfileList = precolumnProfileList;

			if (precolumnProfileList != null && precolumnProfileList.size() > 0) {
				List<ColumnProfileDelta_DP> deltaList = getColumnProfileDeltaProcess(idData, precolumnProfileList);
				if (deltaList != null && deltaList.size() > 0) {
					columnProfileList = deltaList;
				}
			}

			// for Column Combination Profile Headers Reading
			List<ColumnCombinationProfile_DP> columnCombinationProfileList = listdatasourcedao
					.readColumnCombinationProfileForTemplate(idData);

			// To check if user have permission to profile view data
			boolean rbacProfileDataViewAccess = RBACController.rbac("Profile Data View", "C", session);
			String profileDataViewEnabled = appDbConnectionProperties.getProperty("profile_data_view_enabled");
			String profileDataViewAccess = "N";
			if(profileDataViewEnabled!=null && !profileDataViewEnabled.trim().isEmpty()&&profileDataViewEnabled.equalsIgnoreCase("Y") && rbacProfileDataViewAccess){
				profileDataViewAccess = "Y";
			}

			modelAndView.addObject("idData", idData);
			modelAndView.addObject("appName", appName);
			modelAndView.addObject("profileDataViewAccess", profileDataViewAccess);

			modelAndView.addObject("rowProfileList", rowProfileList);
			modelAndView.addObject("numericProfileList", numericProfileList);
			modelAndView.addObject("columnProfileList", columnProfileList);
			modelAndView.addObject("columnProfileDetailsList", columnProfileDetailsList);
			modelAndView.addObject("columnCombinationProfileList", columnCombinationProfileList);
			modelAndView.addObject("newColumnProfileList", newColumnProfileList);
			modelAndView.addObject("missingColumnProfileList", missingColumnProfileList);

			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Profiling");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	// profileDataTemplateView
	@RequestMapping(value = "/profileDataTemplateView")
	public ModelAndView getListDataSource(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		
		boolean rbac = RBACController.rbac("Data Template", "R", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");
			
			// When project is not selected in search filter, default selected project will
			// be considered for search filter
			Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
					? searchfilter_projectId
					: projectId;
			Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
			
			List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForProfiling(selected_projectId,
					projList, fromDate, toDate);
			model.addObject("projectList", projList);
			model.addObject("selectedProject", selectedProject);
			model.addObject("listdatasource", listdatasource);
			model.setViewName("profileDataTemplateView");
			model.addObject("currentSection", "Dashboard");
			model.addObject("currentLink", "Data Profiling");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	// profileColumnDataTemplateView
	@RequestMapping(value = "/profileColumnDataTemplateView")
	public ModelAndView profileColumnDataTemplateView(HttpServletRequest request, ModelAndView model,
			HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Data Template", "R", session);
		if (rbac) {

			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");
			
			// When project is not selected in search filter, default selected project will
			// be considered for search filter
			Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
					? searchfilter_projectId
					: projectId;
			Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
			
			// for ColumnProfile
			List<ColumnProfile_DP> columnProfileList = listdatasourcedao.readColumnDataProfile(selected_projectId, fromDate, toDate);

			model.setViewName("columnDataProfiling_View");
			model.addObject("projectList", projList);
			model.addObject("selectedProject", selectedProject);
			model.addObject("columnProfileList", columnProfileList);
			model.addObject("currentSection", "Dashboard");
			model.addObject("currentLink", "Data Profiling");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/sendProfileResultsMail", method = RequestMethod.POST, produces = "application/json")
	public void sendProfileResultsMail(@RequestParam String EmailDataToSend, HttpSession oSession,
			HttpServletResponse oResponse) {

		JSONObject oJsonResponse = new JSONObject();
		JSONObject oEmailDataToSend = new JSONObject();
		oEmailDataToSend = new JSONObject(EmailDataToSend);
		try {
			DateUtility.DebugLog("sendProfileResultsMail 01", "Begin controller processing for sendProfileResultsMail");
			oJsonResponse = sendEmailData(oEmailDataToSend);
			DateUtility.DebugLog("sendProfileResultsMail 02",
					"End controller for sendProfileResultsMail sending response");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}

	}

	private JSONObject sendEmailData(JSONObject oEmailDataToSend) throws Exception {
		JSONObject oJsonRetValue = new JSONObject();
		try {
			int mailStatus = sendEmailNotificationUtil.sendEmailBySmtpWithEmail(oEmailDataToSend.getString("Subject"),
					oEmailDataToSend.getString("Message"), oEmailDataToSend.getString("Email"));
			if (mailStatus == 1) {
				oJsonRetValue.put("Result", true);
				oJsonRetValue.put("Msg", "Mail sent successfully");
			} else if (mailStatus == 2) {
				oJsonRetValue.put("Result", false);
				oJsonRetValue.put("Msg", "Some or all of the properties required for email configuration are missing.");
			} else if (mailStatus == 3) {
				oJsonRetValue.put("Result", false);
				oJsonRetValue.put("Msg", "Exception occurred while sending email.");
			}
			return oJsonRetValue;

		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			oException.printStackTrace();
			oJsonRetValue.put("Result", false);
			oJsonRetValue.put("Msg", sExceptionMsg);
			return oJsonRetValue;

		}

	}

	// -------------------------------- For Profiling --------------------------

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/profilingDownloadCsvS3")
	public void profilingDownloadCsvS3(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		try {
			String tableNickName = request.getParameter("tableNickName");
			String tableName = request.getParameter("tableName");
			String idDataStr = request.getParameter("idData");
			long idData = Long.parseLong(idDataStr);

			List resultList = null;
			String header = "";

			if (!tableName.trim().equals("")) {

				if (tableNickName.contains("RowProfile")) {
					System.out.println("\n====> Fetching RowProfile Results ...");
					header = "Number_of_Columns_with_NULL, Number_of_Records, Percentage_Missing";

					resultList = listdatasourcedao.readRowProfileForTemplate(idData);

				} else if (tableNickName.contains("NumericalProfile")) {
					System.out.println("\n====> Fetching NumericalProfile Results ...");
					header = "Column_Name_1, Column_Name_2, Correlation";

					resultList = listdatasourcedao.readNumericProfileForTemplate(idData);

				} else if (tableNickName.equals("ColumnProfile")) {
					System.out.println("\n====> Fetching ColumnProfile Results ...");
					header = "idData,table_or_fileName, Column_Name, Data_Type,Total_Record_Count, Missing_Value, Percentage_Missing, Unique_Count, Min_Length, Max_Length, Mean, Std_Dev, Min, Max, 99_percentaile, 75_percentile, 25_percentile, 1_percentile,Default_Patterns";

					if (idData == 01) {
						Long projectId = (Long) session.getAttribute("projectId");
						String fromDate = (String) session.getAttribute("fromDate");
						String toDate = (String) session.getAttribute("toDate");
						
						Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");
						
						// When project is not selected in search filter, default selected project will
						// be considered for search filter
						Long selectedProjectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
								? searchfilter_projectId
								: projectId;
						
						List<ColumnProfile_DP> resultFinal = listdatasourcedao.readColumnDataProfile(selectedProjectId, fromDate, toDate);
						resultFinal.forEach(eachDP -> eachDP.setDefaultPatterns("\""+eachDP.getDefaultPatterns().replaceAll("val:", "").replaceAll("per:", "").replaceAll("\"", "\\\"")+"\""));						
						resultList = resultFinal;
						
					} else {
						
						List<ColumnProfile_DP> resultFinal = listdatasourcedao.readColumnProfileForTemplate(idData);
						resultFinal.forEach(eachDP -> eachDP.setDefaultPatterns("\""+eachDP.getDefaultPatterns().replaceAll("val:", "").replaceAll("per:", "").replaceAll("\"", "\\\"")+"\""));	
						resultList = resultFinal;
					}

				} else if (tableNickName.equals("ColumnProfileDetail")) {
					System.out.println("\n====> Fetching ColumnProfileDetail Results ...");
					header = "Column_Name, Column_Value, Count,Percentage";
					resultList = listdatasourcedao.readColumnProfileDetailsForTemplate(idData);
					List<ColumnProfileDetails_DP> resultFinal = listdatasourcedao.readColumnProfileDetailsForTemplate(idData);
					resultFinal.forEach(eachDP -> eachDP.setColumnValue("\""+eachDP.getColumnValue()+"\""));
					resultList = resultFinal;
				} else {
					System.out.println("\n====> Fetching Column_Combination Results ...");
					header = "Column_Group_Name, Column_Group_Value, Count,Percentage";

					resultList = listdatasourcedao.readColumnCombinationProfileForTemplate(idData);
				}
			}

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", tableNickName + ".csv");
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			// Write header
			outStream.write(header.getBytes());
			outStream.write("\n".getBytes());
			if (resultList != null && resultList.size() > 0) {
				// Write data
				for (Object obj : resultList) {
					outStream.write(obj.toString().getBytes());
					outStream.write("\n".getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<ColumnProfileDelta_DP> getColumnProfileDeltaProcess(long idData,
			List<ColumnProfile_DP> columnProfileList) {

		List<ColumnProfileDelta_DP> deltaList = new ArrayList<ColumnProfileDelta_DP>();

		try {
			// Get present and last run (Date,Run) details
			SqlRowSet sqlRowSet = listdatasourcedao.getPresentAndLastRunDetailsOfColumnProfile(idData);
			boolean presentRunFound = false;
			boolean previousRunFound = false;
			Date previousExecDate = null;
			Long previousRun = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					if (!presentRunFound) {
						presentRunFound = true;
					} else if (presentRunFound && !previousRunFound) {
						previousRunFound = true;
						previousExecDate = sqlRowSet.getDate("Date");
						previousRun = sqlRowSet.getLong("Run");
					}
				}

				if (previousRunFound) {
					List<ColumnProfile_DP> previouscolumnProfileList = listdatasourcedao
							.readColumnProfileForTemplate(idData, previousExecDate, previousRun);

					if (previouscolumnProfileList != null && previouscolumnProfileList.size() > 0) {

						for (ColumnProfile_DP colProf : columnProfileList) {
							for (ColumnProfile_DP prevColProf : previouscolumnProfileList) {

								if (colProf.getColumnName().equals(prevColProf.getColumnName())) {

									ColumnProfileDelta_DP colProfileDelta = new ColumnProfileDelta_DP();
									colProfileDelta.setExecDate(colProf.getExecDate());
									colProfileDelta.setRun(colProf.getRun());
									colProfileDelta.setColumnName(colProf.getColumnName());
									colProfileDelta.setDataType(colProf.getDataType());

									Long pre_totalRecordCount = colProf.getTotalRecordCount();
									Long prev_totalRecordCount = prevColProf.getTotalRecordCount();

									if (pre_totalRecordCount != null && prev_totalRecordCount != null
											&& pre_totalRecordCount > prev_totalRecordCount) {
										colProfileDelta.setTotalRecordCount("<span title='Old Value: "
												+ prev_totalRecordCount + "' class='label label-success label-sm'>"
												+ pre_totalRecordCount + "</span>");
									} else if (pre_totalRecordCount != null && prev_totalRecordCount != null
											&& pre_totalRecordCount < prev_totalRecordCount) {
										colProfileDelta.setTotalRecordCount("<span title='Old Value: "
												+ prev_totalRecordCount + "' class='label label-danger label-sm'>"
												+ pre_totalRecordCount + "</span>");
									} else {
										colProfileDelta.setTotalRecordCount("" + pre_totalRecordCount);
									}

									Long pre_missingValue = colProf.getMissingValue();
									Long prev_missingValue = prevColProf.getMissingValue();

									if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue > prev_missingValue) {
										colProfileDelta.setMissingValue("<span title='Old Value: " + prev_missingValue
												+ "' class='label label-success label-sm'>" + pre_missingValue
												+ "</span>");

									} else if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue < prev_missingValue) {
										colProfileDelta.setMissingValue("<span title='Old Value: " + prev_missingValue
												+ "' class='label label-danger label-sm'>" + pre_missingValue
												+ "</span>");
									} else {
										colProfileDelta.setMissingValue("" + pre_missingValue);
									}

									Double pre_percentageMissing = colProf.getPercentageMissing();
									Double prev_percentageMissing = prevColProf.getPercentageMissing();

									if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing > prev_percentageMissing) {
										colProfileDelta.setPercentageMissing("<span title='Old Value: "
												+ prev_percentageMissing + "' class='label label-success label-sm'>"
												+ pre_percentageMissing + "</span>");

									} else if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing < prev_percentageMissing) {
										colProfileDelta.setPercentageMissing("<span title='Old Value: "
												+ prev_percentageMissing + "' class='label label-danger label-sm'>"
												+ pre_percentageMissing + "</span>");
									} else {
										colProfileDelta.setPercentageMissing("" + pre_percentageMissing);
									}

									Long pre_uniqueCount = colProf.getUniqueCount();
									Long prev_uniqueCount = prevColProf.getUniqueCount();

									if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount > prev_uniqueCount) {
										colProfileDelta.setUniqueCount("<span title='Old Value: " + prev_uniqueCount
												+ "' class='label label-success label-sm'>" + pre_uniqueCount
												+ "</span>");
									} else if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount < prev_uniqueCount) {
										colProfileDelta.setUniqueCount("<span title='Old Value: " + prev_uniqueCount
												+ "' class='label label-danger label-sm'>" + pre_uniqueCount
												+ "</span>");
									} else {
										colProfileDelta.setUniqueCount("" + pre_uniqueCount);
									}

									Long pre_minLength = colProf.getMinLength();
									Long prev_minLength = prevColProf.getMinLength();

									if (pre_minLength != null && prev_minLength != null
											&& pre_minLength > prev_minLength) {
										colProfileDelta.setMinLength("<span title='Old Value: " + prev_minLength
												+ "' class='label label-success label-sm'>" + pre_minLength
												+ "</span>");
									} else if (pre_minLength != null && prev_minLength != null
											&& pre_minLength < prev_minLength) {
										colProfileDelta.setMinLength("<span title='Old Value: " + prev_minLength
												+ "' class='label label-danger label-sm'>" + pre_minLength + "</span>");
									} else {
										colProfileDelta.setMinLength("" + pre_minLength);
									}

									Long pre_maxLength = colProf.getMaxLength();
									Long prev_maxLength = prevColProf.getMaxLength();

									if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength > prev_maxLength) {
										colProfileDelta.setMaxLength("<span title='Old Value: " + prev_maxLength
												+ "' class='label label-success label-sm'>" + pre_maxLength
												+ "</span>");
									} else if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength < prev_maxLength) {
										colProfileDelta.setMaxLength("<span title='Old Value: " + prev_maxLength
												+ "' class='label label-danger label-sm'>" + pre_maxLength + "</span>");
									} else {
										colProfileDelta.setMaxLength("" + pre_maxLength);
									}
									
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
									if(colProf.getMean() != null && !colProf.getMean().trim().isEmpty() && prevColProf.getMean() != null
											&& !prevColProf.getMean().trim().isEmpty()) {
										Double preMean = Double.valueOf(colProf.getMean());
										Double prevMean =  Double.valueOf(prevColProf.getMean());
										if(preMean>prevMean) {
											colProfileDelta.setMean("<span title='Old Value: " + decimalFormat.format(prevMean)
													+ "' class='label label-success label-sm'>" + decimalFormat.format(preMean) + "</span>");
										}else if(preMean<prevMean) {
											colProfileDelta.setMean("<span title='Old Value: " + decimalFormat.format(prevMean)
													+ "' class='label label-danger label-sm'>" + decimalFormat.format(preMean) + "</span>");
										}else {
											colProfileDelta.setMean(decimalFormat.format(preMean));
										}
									}else {	
										colProfileDelta.setMean(colProf.getMean());
									}
									
									if(colProf.getStdDev() != null && !colProf.getStdDev().trim().isEmpty() && prevColProf.getStdDev() != null
											&& !prevColProf.getStdDev().trim().isEmpty()) {
										Double preStddev = Double.valueOf(colProf.getStdDev());
										Double prevStddev = Double.valueOf(prevColProf.getStdDev());
										if(preStddev>prevStddev) {
											colProfileDelta.setStdDev("<span title='Old Value: " + decimalFormat.format(prevStddev)
													+ "' class='label label-success label-sm'>" + decimalFormat.format(preStddev) + "</span>");
										}else if(preStddev<prevStddev) {
											colProfileDelta.setStdDev("<span title='Old Value: " + decimalFormat.format(prevStddev)
													+ "' class='label label-danger label-sm'>" +decimalFormat.format( preStddev) + "</span>");
										} else {
											String col_prof_std_dev_str = colProf.getStdDev();
											Double std_dev = 0.0;
											try {
												if (col_prof_std_dev_str != null
														&& !col_prof_std_dev_str.trim().isEmpty()) {
													std_dev = Double.parseDouble(col_prof_std_dev_str.trim());
												}
											} catch (Exception e) {
											}
											colProfileDelta.setStdDev(decimalFormat.format(std_dev));
										}
									}else {
										colProfileDelta.setStdDev(colProf.getStdDev());
									}
									
									String pre_min = colProf.getMin();
									String prev_min = prevColProf.getMin();

									if (pre_min != null && prev_min != null && !pre_min.trim().isEmpty()
											&& !prev_min.trim().isEmpty()) {

										if (!colProf.getDataType().equalsIgnoreCase("Date")) {
											if (Double.parseDouble(pre_min) > Double.parseDouble(prev_min)) {
												colProfileDelta.setMin("<span title='Old Value: " + prev_min
														+ "' class='label label-success label-sm'>" + pre_min
														+ "</span>");
											} else if (Double.parseDouble(pre_min) < Double.parseDouble(prev_min)) {
												colProfileDelta.setMin("<span title='Old Value: " + prev_min
														+ "' class='label label-danger label-sm'>" + pre_min
														+ "</span>");
											}
										} else {
											colProfileDelta.setMin(pre_min);
										}
									} else {
										colProfileDelta.setMin(pre_min);
									}

									String pre_max = colProf.getMax();
									String prev_max = prevColProf.getMax();

									if (pre_max != null && prev_max != null && !pre_max.trim().isEmpty()
											&& !prev_max.trim().isEmpty()) {
										if (!colProf.getDataType().equalsIgnoreCase("Date")) {
											if (Double.parseDouble(pre_max) > Double.parseDouble(prev_max)) {
												colProfileDelta.setMax("<span title='Old Value: " + prev_max
														+ "' class='label label-success label-sm'>" + pre_max
														+ "</span>");
											} else if (Double.parseDouble(pre_max) < Double.parseDouble(prev_max)) {
												colProfileDelta.setMax("<span title='Old Value: " + prev_max
														+ "' class='label label-danger label-sm'>" + pre_max
														+ "</span>");
											}
										} else {
											colProfileDelta.setMax(pre_max);
										}
									} else {
										colProfileDelta.setMax(pre_max);
									}

									String pre_Percentile_99 = colProf.getPercentile_99();
									String prev_Percentile_99 = prevColProf.getPercentile_99();

									if (pre_Percentile_99 != null && prev_Percentile_99 != null
											&& !pre_Percentile_99.trim().isEmpty()
											&& !prev_Percentile_99.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_99) > Double
													.parseDouble(prev_Percentile_99)) {
										colProfileDelta.setPercentile_99("<span title='Old Value: " + prev_Percentile_99
												+ "' class='label label-success label-sm'>" + pre_Percentile_99
												+ "</span>");
									} else if (pre_Percentile_99 != null && prev_Percentile_99 != null
											&& !pre_Percentile_99.trim().isEmpty()
											&& !prev_Percentile_99.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_99) < Double
													.parseDouble(prev_Percentile_99)) {
										colProfileDelta.setPercentile_99("<span title='Old Value: " + prev_Percentile_99
												+ "' class='label label-danger label-sm'>" + pre_Percentile_99
												+ "</span>");
									} else {
										colProfileDelta.setPercentile_99(pre_Percentile_99);
									}

									String pre_Percentile_75 = colProf.getPercentile_75();
									String prev_Percentile_75 = prevColProf.getPercentile_75();

									if (pre_Percentile_75 != null && prev_Percentile_75 != null
											&& !pre_Percentile_75.trim().isEmpty()
											&& !prev_Percentile_75.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_75) > Double
													.parseDouble(prev_Percentile_75)) {
										colProfileDelta.setPercentile_75("<span title='Old Value: " + prev_Percentile_75
												+ "' class='label label-success label-sm'>" + pre_Percentile_75
												+ "</span>");
									} else if (pre_Percentile_75 != null && prev_Percentile_75 != null
											&& !pre_Percentile_75.trim().isEmpty()
											&& !prev_Percentile_75.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_75) < Double
													.parseDouble(prev_Percentile_75)) {
										colProfileDelta.setPercentile_75("<span title='Old Value: " + prev_Percentile_75
												+ "' class='label label-danger label-sm'>" + pre_Percentile_75
												+ "</span>");
									} else {
										colProfileDelta.setPercentile_75(pre_Percentile_75);
									}

									String pre_Percentile_25 = colProf.getPercentile_25();
									String prev_Percentile_25 = prevColProf.getPercentile_25();

									if (pre_Percentile_25 != null && prev_Percentile_25 != null
											&& !pre_Percentile_25.trim().isEmpty()
											&& !prev_Percentile_25.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_25) > Double
													.parseDouble(prev_Percentile_25)) {
										colProfileDelta.setPercentile_25("<span title='Old Value: " + prev_Percentile_25
												+ "' class='label label-success label-sm'>" + pre_Percentile_25
												+ "</span>");
									} else if (pre_Percentile_25 != null && prev_Percentile_25 != null
											&& !pre_Percentile_25.trim().isEmpty()
											&& !prev_Percentile_25.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_25) < Double
													.parseDouble(prev_Percentile_25)) {
										colProfileDelta.setPercentile_25("<span title='Old Value: " + prev_Percentile_25
												+ "' class='label label-danger label-sm'>" + pre_Percentile_25
												+ "</span>");
									} else {
										colProfileDelta.setPercentile_25(pre_Percentile_25);
									}

									String pre_Percentile_1 = colProf.getPercentile_1();
									String prev_Percentile_1 = prevColProf.getPercentile_1();

									if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) > Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1("<span title='Old Value: " + prev_Percentile_1
												+ "' class='label label-success label-sm'>" + pre_Percentile_1
												+ "</span>");
									} else if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) < Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1("<span title='Old Value: " + prev_Percentile_1
												+ "' class='label label-danger label-sm'>" + pre_Percentile_1
												+ "</span>");
									} else {
										colProfileDelta.setPercentile_1(pre_Percentile_1);
									}
									
									colProfileDelta.setDefaultPatterns(colProf.getDefaultPatterns());

									deltaList.add(colProfileDelta);
									break;
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deltaList;
	}

	private List<ColumnProfile_DP> getNewColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {

		List<ColumnProfile_DP> newColumnProfileList = new ArrayList<ColumnProfile_DP>();

		try {
			// Get present and last run (Date,Run) details
			SqlRowSet sqlRowSet = listdatasourcedao.getPresentAndLastRunDetailsOfColumnProfile(idData);
			boolean presentRunFound = false;
			boolean previousRunFound = false;
			Date previousExecDate = null;
			Long previousRun = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					if (!presentRunFound) {
						presentRunFound = true;
					} else if (presentRunFound && !previousRunFound) {
						previousRunFound = true;
						previousExecDate = sqlRowSet.getDate("Date");
						previousRun = sqlRowSet.getLong("Run");
					}
				}

				if (previousRunFound) {
					List<ColumnProfile_DP> previouscolumnProfileList = listdatasourcedao
							.readColumnProfileForTemplate(idData, previousExecDate, previousRun);

					if (previouscolumnProfileList != null && previouscolumnProfileList.size() > 0) {

						for (ColumnProfile_DP colProf : columnProfileList) {
							boolean columnFound = false;
							for (ColumnProfile_DP prevColProf : previouscolumnProfileList) {
								if (colProf.getColumnName().equals(prevColProf.getColumnName())) {
									columnFound = true;
									break;
								}
							}
							if (!columnFound) {
								newColumnProfileList.add(colProf);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newColumnProfileList;
	}

	private List<ColumnProfile_DP> getMissingColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {

		List<ColumnProfile_DP> missingColumnProfileList = new ArrayList<ColumnProfile_DP>();

		try {
			// Get present and last run (Date,Run) details
			SqlRowSet sqlRowSet = listdatasourcedao.getPresentAndLastRunDetailsOfColumnProfile(idData);
			boolean presentRunFound = false;
			boolean previousRunFound = false;
			Date previousExecDate = null;
			Long previousRun = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					if (!presentRunFound) {
						presentRunFound = true;
					} else if (presentRunFound && !previousRunFound) {
						previousRunFound = true;
						previousExecDate = sqlRowSet.getDate("Date");
						previousRun = sqlRowSet.getLong("Run");
					}
				}

				if (previousRunFound) {
					List<ColumnProfile_DP> previouscolumnProfileList = listdatasourcedao
							.readColumnProfileForTemplate(idData, previousExecDate, previousRun);

					if (previouscolumnProfileList != null && previouscolumnProfileList.size() > 0) {

						for (ColumnProfile_DP prevColProf : previouscolumnProfileList) {
							boolean columnFound = false;
							for (ColumnProfile_DP colProf : columnProfileList) {
								if (colProf.getColumnName().equals(prevColProf.getColumnName())) {
									columnFound = true;
									break;
								}
							}
							if (!columnFound) {
								System.out.println("missing column:" + prevColProf.getColumnName());
								missingColumnProfileList.add(prevColProf);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return missingColumnProfileList;
	}

}