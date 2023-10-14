package com.databuck.restcontroller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.ColumnCombinationProfile_DP;
import com.databuck.bean.ColumnProfileDelta_DP;
import com.databuck.bean.ColumnProfileDetails_DP;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.NumericalProfile_DP;
import com.databuck.bean.Project;
import com.databuck.bean.RowProfile_DP;
import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IProjectService;
import com.databuck.service.LoginService;
import com.databuck.service.PrimaryKeyMatchingResultService;

@CrossOrigin(origins = "*")
@RestController
public class DataProfilingRestController {
	
	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private IProjectDAO iProjectDAO; 
	
	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;
	
    @Autowired
    private Properties appDbConnectionProperties;
    
	@Autowired
	private LoginService loginService;
	
	@Autowired
	public IProjectService projService;
	
	@Autowired
	private ChecksCSVService csvService;
	
	@Autowired
	private ExecutiveSummaryService executiveSummaryService;
	
	@Autowired
	PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

	private static final Logger LOG = Logger.getLogger(DataProfilingRestController.class);
	
	
	@PostMapping(value = "/dbconsole/dataProfiling_View")
	public ResponseEntity<Object> dataProfilingView(@RequestHeader HttpHeaders headers,
			@RequestBody  Map<String, Long> request) throws IOException {
		LOG.info("/dbconsole/dataProfiling_View - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			LOG.debug("Getting request parameters , " + request.containsKey("idData"));
			if(!request.containsKey("idData")) {
				response.put("message", "Required parameters are missing.");
				response.put("status", "failed");
				LOG.error("Required parameters are missing.");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			
			JSONObject tokenStatusObj = executiveSummaryService.validateToken(headers.get("token").get(0));
			String tokenStatus = tokenStatusObj.getString("status");

			if (tokenStatus.equals("success")) {
			Long idData = request.get("idData");

			String appName = "";
			List<ListDataSource> listApplicationsData = listdatasourcedao.getListDataSourceTableId(idData);
	
			for (ListDataSource ld : listApplicationsData) {
				appName = ld.getName();
			}

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
			Map<String, Object> results = new HashMap<>();
			results.put("idData", idData);
			results.put("appName", appName);
			results.put("rowProfileList", rowProfileList);
			results.put("numericProfileList", numericProfileList);
			results.put("columnProfileList", columnProfileList);
			results.put("columnProfileDetailsList", columnProfileDetailsList);
			results.put("columnCombinationProfileList", columnCombinationProfileList);
			results.put("newColumnProfileList", newColumnProfileList);
			results.put("missingColumnProfileList", missingColumnProfileList);
			
			response.put("result", results);
			response.put("status", "success");
			response.put("message", "success");
			LOG.info("/dbconsole/dataProfiling_View - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
			}else {
				response.put("message", "Token expired.");
				response.put("status", "failed");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("status", "failed");
			response.put("message", "There was a problem");
			
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	private List<ColumnProfile_DP> getNewColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {
		
		LOG.info("getNewColumnsDelta - START");
		LOG.debug("Getting  parameters for idData , " + idData+" ColumnProfile_DP List  "+columnProfileList);

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
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("getNewColumnsDelta - END");
		return newColumnProfileList;
	}

	private List<ColumnProfile_DP> getMissingColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {

		LOG.info("getMissingColumnsDelta - START");
		LOG.debug("Getting  parameters for idData , " + idData+" ColumnProfile_DP List  "+columnProfileList);
		
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
								
								LOG.error("missing column:" + prevColProf.getColumnName());
								missingColumnProfileList.add(prevColProf);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("getMissingColumnsDelta - END");
		return missingColumnProfileList;
	}

	
	private List<ColumnProfileDelta_DP> getColumnProfileDeltaProcess(long idData,
			List<ColumnProfile_DP> columnProfileList) {
		LOG.info("getColumnProfileDeltaProcess - START");
		LOG.debug("Getting  parameters for idData , " + idData+" ColumnProfile_DP List  "+columnProfileList);

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
										colProfileDelta.setTotalRecordCount(prev_totalRecordCount.toString());
									} else if (pre_totalRecordCount != null && prev_totalRecordCount != null
											&& pre_totalRecordCount < prev_totalRecordCount) {
										colProfileDelta.setTotalRecordCount(prev_totalRecordCount.toString());
									} else {
										colProfileDelta.setTotalRecordCount(pre_totalRecordCount.toString());
									}

									Long pre_missingValue = colProf.getMissingValue();
									Long prev_missingValue = prevColProf.getMissingValue();

									if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue > prev_missingValue) {
										colProfileDelta.setMissingValue(prev_missingValue.toString());

									} else if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue < prev_missingValue) {
										colProfileDelta.setMissingValue(pre_missingValue.toString());
									} else {
										colProfileDelta.setMissingValue(pre_missingValue.toString());
									}

									Double pre_percentageMissing = colProf.getPercentageMissing();
									Double prev_percentageMissing = prevColProf.getPercentageMissing();

									if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing > prev_percentageMissing) {
										colProfileDelta.setPercentageMissing(prev_percentageMissing.toString());

									} else if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing < prev_percentageMissing) {
										colProfileDelta.setPercentageMissing(prev_percentageMissing.toString());
									} else {
										colProfileDelta.setPercentageMissing(pre_percentageMissing.toString());
									}

									Long pre_uniqueCount = colProf.getUniqueCount();
									Long prev_uniqueCount = prevColProf.getUniqueCount();

									if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount > prev_uniqueCount) {
										colProfileDelta.setUniqueCount(pre_uniqueCount.toString());
									} else if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount < prev_uniqueCount) {
										colProfileDelta.setUniqueCount(pre_uniqueCount.toString());
									} else {
										colProfileDelta.setUniqueCount(pre_uniqueCount.toString());
									}

									Long pre_minLength = colProf.getMinLength();
									Long prev_minLength = prevColProf.getMinLength();

									if (pre_minLength != null && prev_minLength != null
											&& pre_minLength > prev_minLength) {
										colProfileDelta.setMinLength(pre_minLength.toString());
									} else if (pre_minLength != null && prev_minLength != null
											&& pre_minLength < prev_minLength) {
										colProfileDelta.setMinLength(pre_minLength.toString());
									} else {
										colProfileDelta.setMinLength(pre_minLength.toString());
									}

									Long pre_maxLength = colProf.getMaxLength();
									Long prev_maxLength = prevColProf.getMaxLength();

									if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength > prev_maxLength) {
										colProfileDelta.setMaxLength( pre_maxLength.toString());
									} else if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength < prev_maxLength) {
										colProfileDelta.setMaxLength(pre_maxLength.toString());
									} else {
										colProfileDelta.setMaxLength("" + pre_maxLength);
									}
									
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
									if(colProf.getMean() != null && !colProf.getMean().trim().isEmpty() && prevColProf.getMean() != null
											&& !prevColProf.getMean().trim().isEmpty()) {
										Double preMean = Double.valueOf(colProf.getMean());
										Double prevMean =  Double.valueOf(prevColProf.getMean());
										if(preMean>prevMean) {
											colProfileDelta.setMean(decimalFormat.format(preMean));
										}else if(preMean<prevMean) {
											colProfileDelta.setMean(decimalFormat.format(preMean));
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
											colProfileDelta.setStdDev( decimalFormat.format(preStddev));
										}else if(preStddev<prevStddev) {
											colProfileDelta.setStdDev(decimalFormat.format( preStddev));
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
												colProfileDelta.setMin(pre_min);
											} else if (Double.parseDouble(pre_min) < Double.parseDouble(prev_min)) {
												colProfileDelta.setMin(pre_min);
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
												colProfileDelta.setMax( pre_max);
											} else if (Double.parseDouble(pre_max) < Double.parseDouble(prev_max)) {
												colProfileDelta.setMax(pre_max);
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
										colProfileDelta.setPercentile_99( pre_Percentile_99);
									} else if (pre_Percentile_99 != null && prev_Percentile_99 != null
											&& !pre_Percentile_99.trim().isEmpty()
											&& !prev_Percentile_99.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_99) < Double
													.parseDouble(prev_Percentile_99)) {
										colProfileDelta.setPercentile_99( pre_Percentile_99);
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
										colProfileDelta.setPercentile_75(pre_Percentile_75);
									} else if (pre_Percentile_75 != null && prev_Percentile_75 != null
											&& !pre_Percentile_75.trim().isEmpty()
											&& !prev_Percentile_75.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_75) < Double
													.parseDouble(prev_Percentile_75)) {
										colProfileDelta.setPercentile_75(pre_Percentile_75);
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
										colProfileDelta.setPercentile_25(pre_Percentile_25);
									} else if (pre_Percentile_25 != null && prev_Percentile_25 != null
											&& !pre_Percentile_25.trim().isEmpty()
											&& !prev_Percentile_25.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_25) < Double
													.parseDouble(prev_Percentile_25)) {
										colProfileDelta.setPercentile_25(pre_Percentile_25);
									} else {
										colProfileDelta.setPercentile_25(pre_Percentile_25);
									}

									String pre_Percentile_1 = colProf.getPercentile_1();
									String prev_Percentile_1 = prevColProf.getPercentile_1();

									if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) > Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1(pre_Percentile_1);
									} else if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) < Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1(pre_Percentile_1);
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
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("getColumnProfileDeltaProcess - END");
		return deltaList;
	}
	
	// profileDataTemplateView
		@PostMapping(value = "/dbconsole/profileDataTemplateView")
		public ResponseEntity<Object> getListDataSource(@RequestHeader HttpHeaders headers,
				@RequestBody  Map<String, String> request)
				throws IOException {
			LOG.info("/dbconsole/profileDataTemplateView - START");
			
			Map<String, Object> response = new HashMap<>();
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
					
					response.put("message", "Please provide token.");
					response.put("status", "failed");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
				
				LOG.info("projectId  "+request.containsKey("projectId") +" fromDate :"+request.containsKey("fromDate") +"  toDate: " +request.containsKey("toDate"));
			
				if(!request.containsKey("projectId") || !request.containsKey("fromDate") || !request.containsKey("toDate")) {
					response.put("message", "Required parameters are missing.");
					response.put("status", "failed");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
				if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				
				// Get ProjectList 
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				
				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
					String strUserLDAPGroups = userToken.getUserLDAPGroups();
					LOG.info("LDAP Group : "+strUserLDAPGroups);
					if(strUserLDAPGroups!=null) {
						String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
						ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
						projList = loginService.getProjectListOfUser(userLdapGroups);
					}
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}
				Long projectId = Long.parseLong(request.get("projectId"));
				String fromDate = request.get("fromDate");
				String toDate = request.get("toDate");
				Long searchfilter_projectId = projectId;
				
				// When project is not selected in search filter, default selected project will
				// be considered for search filter
				Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
						? searchfilter_projectId
						: projectId;
				Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
				
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForProfilingDate(selected_projectId,
						projList, fromDate, toDate);
				
				Map<String, Object> result = new HashMap<>();
				
				//result.put("projectList", projList);
				//result.put("selectedProject", selectedProject);
				result.put("listdatasource", listdatasource);
				response.put("result", result);
				response.put("status", "success");
				response.put("message", "success");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
				}else {
					response.put("message", "Token expired.");
					response.put("status", "failed");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				response.put("status", "failed");
				response.put("message", "There was a problem");
				return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@PostMapping("/dbconsole/tableprofilingCSV")
		public ResponseEntity<Object> tableprofilingCSV(@RequestHeader HttpHeaders headers,
				HttpServletResponse httpResponse ,@RequestBody  Map<String, String> request) {
			LOG.info("/dbconsole/tableprofilingCSV - START");
			Map<String, Object> response = new HashMap<String, Object>();
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				String token = headers.get("token").get(0);
				if (token != null && !token.isEmpty()) {
					if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						// Get ProjectList 
						List<Project> projList = null;
						String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
						
						if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
							
							String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
							ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
							
							projList = loginService.getProjectListOfUser(userLdapGroups);
						} else {
							projList = projService.getAllProjectsOfAUser(userToken.getEmail());
						}
						Long projectId = Long.parseLong(request.get("projectId"));
						String fromDate = request.get("fromDate");
						String toDate = request.get("toDate");
						Long searchfilter_projectId = projectId;
						
						// When project is not selected in search filter, default selected project will
						// be considered for search filter
						Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
								? searchfilter_projectId
								: projectId;
						Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
						
						List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForProfilingDate(selected_projectId,
								projList, fromDate, toDate);
						if (!listdatasource.isEmpty()) {
							httpResponse.setContentType("text/csv");
							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = { "idData", "CreDate", "name", "schemaName", "dataLocation", "projectName", "tableName", "active"};
							String[] header = { "Template Id", "Created At", "Name", "Data Connection Name", "Location" , "Project Name", "Table Name" , "Template status"
								};
							csvWriter.writeHeader(header);
							for (ListDataSource listdata : listdatasource) {
								csvWriter.write(listdata, fields);
							}
							csvWriter.close();
							response.put("status", "success");
							response.put("message", "File sent");
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						} else {
							response.put("status", "failed");
							response.put("message", "Records not found.");
							LOG.error("Records not found ");
							return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						response.put("status", "failed");
						response.put("message", "Token is expired.");
						LOG.error("Token is expired.");
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is missing in the headers.");
					LOG.error("Please provide token.");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  "+e.getMessage());
				try {
					httpResponse.sendError(0, e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				response.put("message", e.getMessage());
				response.put("status", "failed");
				LOG.info("/dbconsole/tableprofilingCSV - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			
		}
		
		
//		@GetMapping("/dbconsole/columnprofilingCSV")
//		public ResponseEntity<Object> columnprofilingCSV(@RequestHeader HttpHeaders headers,
//				HttpServletResponse httpResponse ,@RequestBody  Map<String, Long> request) {
//			Map<String, Object> response = new HashMap<String, Object>();
//			try {
//				String token = headers.get("token").get(0);
//				if (token != null && !token.isEmpty()) {
//					if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
//						Long idData = request.get("idData");
//
//						String appName = "";
//						List<ListDataSource> listApplicationsData = listdatasourcedao.getListDataSourceTableId(idData);
//				
//						for (ListDataSource ld : listApplicationsData) {
//							appName = ld.getName();
//						}
//
//						// for ColumnProfile
//						List<ColumnProfile_DP> precolumnProfileList = listdatasourcedao.readColumnProfileForTemplate(idData);
//						List columnProfileList = precolumnProfileList;
//						List<ColumnProfileDelta_DP> deltaList =  new ArrayList<ColumnProfileDelta_DP>();
//
//						if (precolumnProfileList != null && precolumnProfileList.size() > 0) {
//							deltaList = getColumnProfileDeltaProcess(idData, precolumnProfileList);
//							if (deltaList != null && deltaList.size() > 0) {
//								columnProfileList = deltaList;
//							}
//						}
//
//						if (!columnProfileList.isEmpty()) {
//							httpResponse.setContentType("text/csv");
//							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
//							String headerKey = "Content-Disposition";
//							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
//							httpResponse.setHeader(headerKey, headerValue);
//							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
//									CsvPreference.STANDARD_PREFERENCE);
//							String[] fields = 
//								{"idData" , "execDate", "run" , "table_or_fileName" ,"columnName" ,"dataType" ,"totalRecordCount" ,"missingValue" ,"percentageMissing" , "uniqueCount",
//					                "minLength" ,"maxLength" , "mean" ,"stdDev" ,"min" ,"max" , "percentile_99" ,"percentile_75" , "percentile_25" ,"percentile_1" ,"projectName" , "defaultPatterns"};
//							String[] header = { "Template Id", "Created At","Run", "Table/File Name", "Column Name", "Project Name ", "Data Type" , "Total Record Count" ,"Missing Value" ,
//									"Percentage Missing" , "Unique Count" ,"Min Lenght" ,"Max Lenght", "Mean" ,"Std Dev" , "Min", "Max" , " 99 Percentile", "75 Percentile","25 Percentile"
//									, "25 Percentile", "1 Percentile" ,"Default Pattern"
//								};
//							csvWriter.writeHeader(header);
//							for (ColumnProfileDelta_DP listdata : deltaList) {
//								csvWriter.write(listdata, fields);
//							}
//						
//							csvWriter.close();
//							response.put("status", "success");
//							response.put("message", "File sent");
//							return new ResponseEntity<Object>(response, HttpStatus.OK);
//						} else {
//							response.put("status", "failed");
//							response.put("message", "Records not found.");
//							return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//						}
//					} else {
//						response.put("status", "failed");
//						response.put("message", "Token is expired.");
//						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//					}
//				} else {
//					response.put("status", "failed");
//					response.put("message", "Token is missing in the headers.");
//					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				try {
//					httpResponse.sendError(0, e.getMessage());
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				e.printStackTrace();
//				response.put("message", e.getMessage());
//				response.put("status", "failed");
//				return new ResponseEntity<Object>(response, HttpStatus.OK);
//			}
//		}
		
		// profileColumnDataTemplateView
		@RequestMapping(value = "/dbconsole/profileColumnDataTemplateView")
		public ResponseEntity<Object> profileColumnDataTemplateView(@RequestHeader HttpHeaders headers,
				@RequestBody  Map<String, String> request)
				throws IOException {
			LOG.info("/dbconsole/profileColumnDataTemplateView - START");
			Map<String, Object> response = new HashMap<>();
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
					LOG.error("Please provide token.");
					response.put("message", "Please provide token.");
					response.put("status", "failed");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
				
				LOG.info("projectId :"+request.containsKey("projectId") +" fromDate :"+ request.containsKey("fromDate") +" toDate :"+ request.containsKey("toDate"));
			
				if(!request.containsKey("projectId") || !request.containsKey("fromDate") || !request.containsKey("toDate")) {
					response.put("message", "Required parameters are missing.");
					response.put("status", "failed");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
				if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				
				// Get ProjectList 
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				
				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
					
					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
					
					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}
				Long projectId = Long.parseLong(request.get("projectId"));
				String fromDate = request.get("fromDate");
				String toDate = request.get("toDate");
				Long searchfilter_projectId = projectId;
				
				// When project is not selected in search filter, default selected project will
				// be considered for search filter
				Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
						? searchfilter_projectId
						: projectId;
				Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
				
				// for ColumnProfile
				List<ColumnProfile_DP> columnProfileList = listdatasourcedao.readColumnDataProfileDate(selected_projectId, 
						fromDate, toDate);
				
				Map<String, Object> result = new HashMap<>();
				
				//result.put("projectList", projList);
				//result.put("selectedProject", selectedProject);
				result.put("columnProfileList", columnProfileList);
				response.put("result", result);
				response.put("status", "success");
				response.put("message", "success");
				LOG.info("/dbconsole/profileColumnDataTemplateView - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
				}else {
					LOG.error("Token is expired.");
					response.put("message", "Token expired.");
					response.put("status", "failed");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				response.put("status", "failed");
				response.put("message", "There was a problem");
				
				return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@PostMapping("/dbconsole/columnprofilingCSV")
		public ResponseEntity<Object> columnprofilingCSV(@RequestHeader HttpHeaders headers,
				HttpServletResponse httpResponse ,@RequestBody  Map<String, String> request) {
			LOG.info("/dbconsole/columnprofilingCSV - START");
			Map<String, Object> response = new HashMap<String, Object>();
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				String token = headers.get("token").get(0);
				if (token != null && !token.isEmpty()) {
					if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						// Get ProjectList 
						List<Project> projList = null;
						String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
						
						if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
							
							String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
							ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
							
							projList = loginService.getProjectListOfUser(userLdapGroups);
						} else {
							projList = projService.getAllProjectsOfAUser(userToken.getEmail());
						}
						Long projectId = Long.parseLong(request.get("projectId"));
						String fromDate = request.get("fromDate");
						String toDate = request.get("toDate");
						Long searchfilter_projectId = projectId;
						
						// When project is not selected in search filter, default selected project will
						// be considered for search filter
						Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
								? searchfilter_projectId
								: projectId;
						Project selectedProject = iProjectDAO.getSelectedProject(selected_projectId);
						
						List<ColumnProfile_DP> columnProfileList = listdatasourcedao.readColumnDataProfileDate(selected_projectId, fromDate, toDate);
						
						if (!columnProfileList.isEmpty()) {
							httpResponse.setContentType("text/csv");
							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = 
								{"idData" , "execDate", "run" , "table_or_fileName" ,"columnName" ,"dataType" ,"totalRecordCount" ,"missingValue" ,"percentageMissing" , "uniqueCount",
					                "minLength" ,"maxLength" , "mean" ,"stdDev" ,"min" ,"max" , "percentile_99" ,"percentile_75" , "percentile_25" ,"percentile_1" ,"projectName" , "defaultPatterns"};
							String[] header = { "Template Id", "Created At","Run", "Table/File Name", "Column Name", "Data Type" , "Total Record Count" ,"Missing Value" ,
									"Percentage Missing" , "Unique Count" ,"Min Lenght" ,"Max Lenght", "Mean" ,"Std Dev" , "Min", "Max" , " 99 Percentile", "75 Percentile","25 Percentile"
									, "1 Percentile" , "Project Name","Default Pattern"
								};
							csvWriter.writeHeader(header);
							for (ColumnProfile_DP listdata : columnProfileList) {
								csvWriter.write(listdata, fields);
							}
							csvWriter.close();
							LOG.info("File sent");
							response.put("status", "success");
							response.put("message", "File sent");
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						} else {
							LOG.error("Records not found.");
							response.put("status", "failed");
							response.put("message", "Records not found.");
							return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						response.put("status", "failed");
						response.put("message", "Token is expired.");
						LOG.error("Token is expired.");
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is missing in the headers.");
					LOG.error("Please provide token.");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  "+e.getMessage());
				try {
					httpResponse.sendError(0, e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				response.put("message", e.getMessage());
				response.put("status", "failed");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		}
}
