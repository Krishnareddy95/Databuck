package com.databuck.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.ColumnProfileDelta_DP;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.dao.IListDataSourceDAO;

@Service
public class DataProfilingDetailsService {

	@Autowired
	private IListDataSourceDAO listdatasourcedao;
	
	private static final Logger LOG = Logger.getLogger(DataProfilingDetailsService.class);

	public List<ColumnProfile_DP> getNewColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {

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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return newColumnProfileList;
	}

	public List<ColumnProfile_DP> getMissingColumnsDelta(long idData, List<ColumnProfile_DP> columnProfileList) {

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
								LOG.debug("missing column:" + prevColProf.getColumnName());
								missingColumnProfileList.add(prevColProf);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return missingColumnProfileList;
	}

	public List<ColumnProfileDelta_DP> getColumnProfileDeltaProcess(long idData,
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

									String pre_mean = colProf.getMean();
									String prev_mean = prevColProf.getMean();

									if (pre_mean != null && !pre_mean.trim().isEmpty() && prev_mean != null
											&& !prev_mean.trim().isEmpty()
											&& Double.parseDouble(pre_mean) > Double.parseDouble(prev_mean)) {
										colProfileDelta.setMean("<span title='Old Value: " + prev_mean
												+ "' class='label label-success label-sm'>" + pre_mean + "</span>");
									} else if (pre_mean != null && !pre_mean.trim().isEmpty() && prev_mean != null
											&& !prev_mean.trim().isEmpty()
											&& Double.parseDouble(pre_mean) < Double.parseDouble(prev_mean)) {
										colProfileDelta.setMean("<span title='Old Value: " + prev_mean
												+ "' class='label label-danger label-sm'>" + pre_mean + "</span>");
									} else {
										colProfileDelta.setMean(pre_mean);
									}

									String pre_stddev = colProf.getStdDev();
									String prev_stddev = prevColProf.getStdDev();

									if (pre_stddev != null && !pre_stddev.trim().isEmpty() && prev_stddev != null
											&& !prev_stddev.trim().isEmpty()
											&& Double.parseDouble(pre_stddev) > Double.parseDouble(prev_stddev)) {
										colProfileDelta.setStdDev("<span title='Old Value: " + prev_stddev
												+ "' class='label label-success label-sm'>" + pre_stddev + "</span>");
									} else if (pre_stddev != null && !pre_stddev.trim().isEmpty() && prev_stddev != null
											&& !prev_stddev.trim().isEmpty()
											&& Double.parseDouble(pre_stddev) < Double.parseDouble(prev_stddev)) {
										colProfileDelta.setStdDev("<span title='Old Value: " + prev_stddev
												+ "' class='label label-danger label-sm'>" + pre_stddev + "</span>");
									} else {
										colProfileDelta.setStdDev(pre_stddev);
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

									deltaList.add(colProfileDelta);
									break;
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return deltaList;
	}

	public List<ColumnProfileDelta_DP> getColumnProfileDeltaProcessRest(long idData,
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
									colProfileDelta.setDefaultPatterns(colProf.getDefaultPatterns());
									colProfileDelta.setExecDate(colProf.getExecDate());
									colProfileDelta.setRun(colProf.getRun());
									colProfileDelta.setColumnName(colProf.getColumnName());
									colProfileDelta.setDataType(colProf.getDataType());

									Long pre_totalRecordCount = colProf.getTotalRecordCount();
									Long prev_totalRecordCount = prevColProf.getTotalRecordCount();

									if (pre_totalRecordCount != null && prev_totalRecordCount != null
											&& pre_totalRecordCount > prev_totalRecordCount) {
										colProfileDelta.setTotalRecordCount("success," + pre_totalRecordCount);
									} else if (pre_totalRecordCount != null && prev_totalRecordCount != null
											&& pre_totalRecordCount < prev_totalRecordCount) {
										colProfileDelta.setTotalRecordCount("danger," + pre_totalRecordCount);
									} else {
										colProfileDelta.setTotalRecordCount("primary," + pre_totalRecordCount);
									}

									Long pre_missingValue = colProf.getMissingValue();
									Long prev_missingValue = prevColProf.getMissingValue();

									if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue > prev_missingValue) {
										colProfileDelta.setMissingValue("success," + pre_missingValue);
									} else if (pre_missingValue != null && prev_missingValue != null
											&& pre_missingValue < prev_missingValue) {
										colProfileDelta.setMissingValue("danger," + pre_missingValue);
									} else {
										colProfileDelta.setMissingValue("primary," + pre_missingValue);
									}

									Double pre_percentageMissing = colProf.getPercentageMissing();
									Double prev_percentageMissing = prevColProf.getPercentageMissing();

									if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing > prev_percentageMissing) {
										colProfileDelta.setPercentageMissing("success," + pre_percentageMissing);

									} else if (pre_percentageMissing != null && prev_percentageMissing != null
											&& pre_percentageMissing < prev_percentageMissing) {
										colProfileDelta.setPercentageMissing("danger," + pre_percentageMissing);
									} else {
										colProfileDelta.setPercentageMissing("primary," + pre_percentageMissing);
									}

									Long pre_uniqueCount = colProf.getUniqueCount();
									Long prev_uniqueCount = prevColProf.getUniqueCount();
									Double uniqueCount = 0.0;
									if (pre_uniqueCount != null) {
										uniqueCount = (Double.valueOf(pre_uniqueCount)
												/ Double.valueOf(pre_totalRecordCount)) * 100;
									}
									if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount > prev_uniqueCount) {
										colProfileDelta.setUniqueCount("success," + uniqueCount);
									} else if (pre_uniqueCount != null && prev_uniqueCount != null
											&& pre_uniqueCount < prev_uniqueCount) {
										colProfileDelta.setUniqueCount("danger," + uniqueCount);
									} else {
										colProfileDelta.setUniqueCount("primary," + uniqueCount);
									}

									Long pre_minLength = colProf.getMinLength();
									Long prev_minLength = prevColProf.getMinLength();

									if (pre_minLength != null && prev_minLength != null
											&& pre_minLength > prev_minLength) {
										colProfileDelta.setMinLength("success," + pre_minLength);
									} else if (pre_minLength != null && prev_minLength != null
											&& pre_minLength < prev_minLength) {
										colProfileDelta.setMinLength("danger," + pre_minLength);
									} else {
										colProfileDelta.setMinLength("primary," + pre_minLength);
									}

									Long pre_maxLength = colProf.getMaxLength();
									Long prev_maxLength = prevColProf.getMaxLength();

									if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength > prev_maxLength) {
										colProfileDelta.setMaxLength("success," + pre_maxLength);
									} else if (pre_maxLength != null && prev_maxLength != null
											&& pre_maxLength < prev_maxLength) {
										colProfileDelta.setMaxLength("danger," + pre_maxLength);
									} else {
										colProfileDelta.setMaxLength("primary," + pre_maxLength);
									}

									String pre_mean = colProf.getMean();
									String prev_mean = prevColProf.getMean();

									if (pre_mean != null && !pre_mean.trim().isEmpty() && prev_mean != null
											&& !prev_mean.trim().isEmpty()
											&& Double.parseDouble(pre_mean) > Double.parseDouble(prev_mean)) {
										colProfileDelta.setMean("success," + pre_mean);
									} else if (pre_mean != null && !pre_mean.trim().isEmpty() && prev_mean != null
											&& !prev_mean.trim().isEmpty()
											&& Double.parseDouble(pre_mean) < Double.parseDouble(prev_mean)) {
										colProfileDelta.setMean("danger," + pre_mean);
									} else {
										colProfileDelta.setMean("primary," + pre_mean);
									}

									String pre_stddev = colProf.getStdDev();
									String prev_stddev = prevColProf.getStdDev();

									if (pre_stddev != null && !pre_stddev.trim().isEmpty() && prev_stddev != null
											&& !prev_stddev.trim().isEmpty()
											&& Double.parseDouble(pre_stddev) > Double.parseDouble(prev_stddev)) {
										colProfileDelta.setStdDev("success," + pre_stddev);
									} else if (pre_stddev != null && !pre_stddev.trim().isEmpty() && prev_stddev != null
											&& !prev_stddev.trim().isEmpty()
											&& Double.parseDouble(pre_stddev) < Double.parseDouble(prev_stddev)) {
										colProfileDelta.setStdDev("danger," + pre_stddev);
									} else {
										colProfileDelta.setStdDev("primary," + pre_stddev);
									}

									String pre_min = colProf.getMin();
									String prev_min = prevColProf.getMin();

									if (pre_min != null && prev_min != null && !pre_min.trim().isEmpty()
											&& !prev_min.trim().isEmpty()) {

										if (!colProf.getDataType().equalsIgnoreCase("Date")) {
											if (Double.parseDouble(pre_min) > Double.parseDouble(prev_min)) {
												colProfileDelta.setMin("success," + pre_min);
											} else if (Double.parseDouble(pre_min) < Double.parseDouble(prev_min)) {
												colProfileDelta.setMin("danger," + pre_min);
											}
										} else {
											colProfileDelta.setMin("primary," + pre_min);
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
												colProfileDelta.setMax("success," + pre_max);
											} else if (Double.parseDouble(pre_max) < Double.parseDouble(prev_max)) {
												colProfileDelta.setMax("danger," + pre_max);
											}
										} else {
											colProfileDelta.setMax("primary" + pre_max);
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
										colProfileDelta.setPercentile_99("success," + pre_Percentile_99);
									} else if (pre_Percentile_99 != null && prev_Percentile_99 != null
											&& !pre_Percentile_99.trim().isEmpty()
											&& !prev_Percentile_99.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_99) < Double
													.parseDouble(prev_Percentile_99)) {
										colProfileDelta.setPercentile_99("danger," + pre_Percentile_99);
									} else {
										colProfileDelta.setPercentile_99("primary," + pre_Percentile_99);
									}

									String pre_Percentile_75 = colProf.getPercentile_75();
									String prev_Percentile_75 = prevColProf.getPercentile_75();

									if (pre_Percentile_75 != null && prev_Percentile_75 != null
											&& !pre_Percentile_75.trim().isEmpty()
											&& !prev_Percentile_75.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_75) > Double
													.parseDouble(prev_Percentile_75)) {
										colProfileDelta.setPercentile_75("success," + pre_Percentile_75);
									} else if (pre_Percentile_75 != null && prev_Percentile_75 != null
											&& !pre_Percentile_75.trim().isEmpty()
											&& !prev_Percentile_75.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_75) < Double
													.parseDouble(prev_Percentile_75)) {
										colProfileDelta.setPercentile_75("danger," + pre_Percentile_75);
									} else {
										colProfileDelta.setPercentile_75("primary," + pre_Percentile_75);
									}

									String pre_Percentile_25 = colProf.getPercentile_25();
									String prev_Percentile_25 = prevColProf.getPercentile_25();

									if (pre_Percentile_25 != null && prev_Percentile_25 != null
											&& !pre_Percentile_25.trim().isEmpty()
											&& !prev_Percentile_25.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_25) > Double
													.parseDouble(prev_Percentile_25)) {
										colProfileDelta.setPercentile_25("success," + pre_Percentile_25);
									} else if (pre_Percentile_25 != null && prev_Percentile_25 != null
											&& !pre_Percentile_25.trim().isEmpty()
											&& !prev_Percentile_25.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_25) < Double
													.parseDouble(prev_Percentile_25)) {
										colProfileDelta.setPercentile_25("danger," + pre_Percentile_25);
									} else {
										colProfileDelta.setPercentile_25("primary," + pre_Percentile_25);
									}

									String pre_Percentile_1 = colProf.getPercentile_1();
									String prev_Percentile_1 = prevColProf.getPercentile_1();

									if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) > Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1("success," + pre_Percentile_1);
									} else if (pre_Percentile_1 != null && prev_Percentile_1 != null
											&& !pre_Percentile_1.trim().isEmpty() && !prev_Percentile_1.trim().isEmpty()
											&& Double.parseDouble(pre_Percentile_1) < Double
													.parseDouble(prev_Percentile_1)) {
										colProfileDelta.setPercentile_1("danger," + pre_Percentile_1);
									} else {
										colProfileDelta.setPercentile_1("primary," + pre_Percentile_1);
									}

									deltaList.add(colProfileDelta);
									break;
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return deltaList;
	}

	public List<ColumnProfile_DP> getColumnProfileFirstRunResult(long idData, List<ColumnProfile_DP> columnProfileList){
		List<ColumnProfile_DP> deltaList = new ArrayList<ColumnProfile_DP>();
		try {
			SqlRowSet sqlRowSet = listdatasourcedao.getPresentAndLastRunDetailsOfColumnProfile(idData);
			boolean presentRunFound = false;
			boolean previousRunFound = false;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					if (!presentRunFound) {
						presentRunFound = true;
					} else if (presentRunFound && !previousRunFound) {
						previousRunFound = true;
					}
				}
				if(!previousRunFound) {
					deltaList.addAll(columnProfileList);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return deltaList;
	}

	public List<Map<String, Object>> findProfilingByFilter(Long idData, Object filter, String profileName) {
		JSONObject filterAttribute = new JSONObject(filter);
		String filterCondition = "";
		Set<String> keySet = filterAttribute.keySet();
		for (String filterColumn : keySet) {
			try {
				String filterValue = "" + filterAttribute.get(filterColumn);
				if (!filterValue.isEmpty())
					filterCondition = "and " + filterCondition + " LOWER(" + filterColumn + ") LIKE '%" + filterValue
							+ "%";
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		String profileTable = getProfileTable(profileName);
		String sql = "select * from " + profileTable + " where" + filterCondition + " and idApp = " + idData
				+ " limit 1000";
		return listdatasourcedao.findProfilingResultsByFilter(sql);
	}

	private String getProfileTable(String profileName) {
		switch (profileName) {
		case "columnCombinationProfileList":
			return "column_combination_profile_master_table";
		case "numericProfileList":
			return "numerical_profile_master_table";
		case "columnProfileDetailsList":
			return "column_profile_detail_master_table";
		case "rowProfileList":
			return "row_profile_master_table";
		case "newColumnProfileList":
			return "";
		case "missingColumnProfileList":
			return "";
		case "columnProfileList":
			return "";
		}
		return "";
	}
}
