package com.databuck.restcontroller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.AbsaDownloadCSVRequest;									
import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ParseLocalDate;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.bean.SqlRule;
import com.databuck.csvmodel.AdvDataDriftCheck;
import com.databuck.csvmodel.AdvDataDriftCountSummary;
import com.databuck.csvmodel.CustomRule;
import com.databuck.csvmodel.CustomUniqueness;
import com.databuck.csvmodel.CustomeSummary;
import com.databuck.csvmodel.DataTypeCheck;
import com.databuck.csvmodel.DateAnomalyCheck;
import com.databuck.csvmodel.DateConsSummary;
import com.databuck.csvmodel.DefaultPatternCheck;
import com.databuck.csvmodel.DefaultValueCheck;
import com.databuck.csvmodel.DistributionCheck;
import com.databuck.csvmodel.DuplicateSummary;
import com.databuck.csvmodel.GlobalRule;
import com.databuck.csvmodel.LengthCheck;
import com.databuck.csvmodel.MicroDateRuleCheck;
import com.databuck.csvmodel.MicroNullCheck;
import com.databuck.csvmodel.NullCheck;
import com.databuck.csvmodel.PatternCheck;
import com.databuck.csvmodel.PrimaryKeyDashboardTable;
import com.databuck.csvmodel.ProcessData;
import com.databuck.csvmodel.RecordAnomaly;
import com.databuck.csvmodel.RecordAnomalyHistory;
import com.databuck.csvmodel.RecordCountReasonability;
import com.databuck.csvmodel.RecordCountResTransaction;
import com.databuck.csvmodel.RecordCountSumGroup;
import com.databuck.csvmodel.SequenceCheck;
import com.databuck.csvmodel.StringDuplicate;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.DownloadCSVService;
import com.databuck.service.IMatchingResultService;
import com.databuck.util.TokenValidator;

@CrossOrigin(origins = "*")
@RestController
public class ChecksCSVRestController {

	@Autowired
	private ChecksCSVService csvService;

	@Autowired
	public IMatchingResultService iMatchingResultService;

	@Autowired
	private DownloadCSVService downloadCSVService;

	@Autowired
	private TokenValidator tokenValidator;

	private static final Logger LOG = Logger.getLogger(ChecksCSVRestController.class);
	
	@PostMapping("/dbconsole/nullCheckCSV")
	public void getNullcheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/nullCheckCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters for NullcheckCSV  " + inputsJsonStr);
					List<NullCheck> checks = csvService.getNullChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "NullCheckReport" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "ExecDate", "Run", "Status", "ColumnName", "TotalRecords", "NullValue",
								"NullPercentage", "NullThreshold", "HistoricNullMean", "HistoricNullStdDev",
								"HistoricNullStatus" };
						String[] header = { "Execution Date", "Run", "Status", "Column Name", "Total Records",
								"Nulls", "Null %", "Null Threshold", "Historic Null Mean",
								"Historic Null Std Dev", "Historic Null Status" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (NullCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in NullcheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/nullCheckCSV - END");
	}

	@PostMapping("/dbconsole/microNullCheckCSV")
	public void getMicroNullcheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/microNullCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters   " + inputsJsonStr);
					List<MicroNullCheck> checks = csvService.getMicroNullChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "NullCheckReport" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "ColName", "RecordCount", "MicrosegmentVal",
								"NullValue", "NullPercentage", "NullThreshold" };
						String[] header = { "Execution Date", "Run", "Status", "Column Name", "Total Records",
								"Microsegment Value", "Null Value", "Null Percentage", "Null Threshold" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (MicroNullCheck check : checks) {
							if (check.getMicrosegmentVal() != null && !check.getMicrosegmentVal().trim().isEmpty())
								check.setdGroupVal(check.getMicrosegmentVal().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
						
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in microNullCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/microNullCheckCSV - END");
	}

	@PostMapping("/dbconsole/lengthCheckCSV")
	public void getLengthcheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/lengthCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<LengthCheck> checks = csvService.getLengthChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "LengthCheckCSV" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "Status", "RecordCount", "TotalFailedRecords",
								"Length", "LengthThreshold", "FailedRecordsPercentage", "MaxLengthCheckEnabled" };
						String[] header = { "Execution Date", "Run", "Column Name", "Status", "Total Records",
								"Failed Records", "Length", "Length Threshold", "Failed Records %",
								"Max LengthCheck Enabled" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null };

						csvWriter.writeHeader(header);
						for (LengthCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/lengthCheckCSV - END");
	}

	@PostMapping("/dbconsole/advDataDriftCheckCSV")
	public void getAdvDataDriftCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/advDataDriftCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters , " + inputsJsonStr);
					List<AdvDataDriftCheck> checks = csvService.getAdvDataDriftChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "AdvDataDriftCheckCSV" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "UniqueValues", "Operation", "Microsegment",
								"MicrosegmentValue", "UserName" };
						String[] header = { "Execution Date", "Run", "Column Name", "Unique Value", "Operation",
								"Microsegment", "Microsegment Value", "User Name" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null };
						csvWriter.writeHeader(header);
						for (AdvDataDriftCheck check : checks) {
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setDGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setDGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in advDataDriftCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/advDataDriftCheckCSV - END");
	}

	@PostMapping("/dbconsole/advDataDriftCountSummaryCSV")
	public void getAdvDataDriftCountSummaryCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/advDataDriftCountSummaryCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<AdvDataDriftCountSummary> checks = csvService.getAdvDataDriftCountSummaryChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "AdvDataDriftCountSummaryCSV" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "UniqueValuesCount", "MissingValueCount",
								"NewValueCount" };
						String[] header = { "Execution Date", "Run", "Column Name", "Unique Values Count",
								"Missing Value Count", "New Values Count" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null };
						csvWriter.writeHeader(header);
						for (AdvDataDriftCountSummary check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in advDataDriftCountSummaryCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/advDataDriftCountSummaryCSV - END");
	}

	@PostMapping("/dbconsole/dataTypeCheckCSV")
	public void getDataTypeCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/dataTypeCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<DataTypeCheck> checks = csvService.getDataTypeChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "AdvDataDriftCountSummaryCSV" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "Status", "TotalRecord", "TotalBadRecord",
								"BadDataPercentage", "BadDataThreshold" };
						String[] header = { "Execution Date", "Run", "Column Name", "Status", "Total Records",
								"Total Bad Records", "Data Type %", "Data Type Threshold" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null };
						csvWriter.writeHeader(header);
						for (DataTypeCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.info("Records not found ");
						throw new Exception("Records not found.");
						
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in dataTypeCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/dataTypeCheckCSV - END");
	}

	@PostMapping("/dbconsole/defaultValueCheckCSV")
	public void getDefaultValueCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/defaultValueCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<DefaultValueCheck> checks = csvService.getDefaultValueChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "DefaultValues" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "DefaultCount", "DefaultValue",
								"DefaultPercentage" };
						String[] header = { "Execution Date", "Run", "Column Name", "Default Count", "Default Values",
								"Default %" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null };
						csvWriter.writeHeader(header);
						for (DefaultValueCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in defaultValueCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/defaultValueCheckCSV - END");
	}

	// TODO need to check
	@PostMapping("/dbconsole/patternCheckCSV")
	public void getPatternCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/patternCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<PatternCheck> checks = csvService.getPatternChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "AdvDataDriftCountSummaryCSV" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "ColName", "TotalRecords", "TotalFailedRecords",
								"Pattern_list", "FailedRecordsPercentage", "PatternThreshold" };
						String[] header = { "Execution Date", "Run", "Status", "Column Name", "Total Records",
								"Failed Records", "Pattern List", "Failed Record %", "Pattern Threshold" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (PatternCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			LOG.error("Exception in patternCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/patternCheckCSV - END");
	}

	@PostMapping("/dbconsole/recordAnomalyCheckCSV")
	public void getRecordAnomalyCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/recordAnomalyCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<RecordAnomaly> checks = csvService.getRecordAnomalyChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "RecordAnomaly" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "ColVal", "Stddev","Mean",
								"RaDeviation", "Microsegment", "MicrosegmentValue" };
						String[] header = { "Execution Date", "Run", "Column Name", "Column Value", "Std Dev",
								"Mean", "Z-Score", "Microsegment", "Microsegment Value" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null};
						csvWriter.writeHeader(header);
						for (RecordAnomaly check : checks) {
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setDGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setDGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.info("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			LOG.error("Exception in recordAnomalyCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/recordAnomalyCheckCSV - END");
	}

	@PostMapping("/dbconsole/recordAnomalyHistoryCSV")
	public void getRecordAnomalyHistoryCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/recordAnomalyHistoryCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<RecordAnomalyHistory> checks = csvService.getRecordAnomalyHistory(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "RecordAnomalyHistory" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "ColVal", "Stddev", "Mean", "DeviationRA",
								"Microsegment", "MicrosegmentValue", "DqiRA" };
						String[] header = { "Execution Date", "Run", "Column Name", "Column Value", "Std Dev", "Mean",
								"Deviation", "Microsegment", "Microsegment Value", "RA DQI" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (RecordAnomalyHistory check : checks) {
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setdGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setdGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in recordAnomalyHistoryCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/recordAnomalyHistoryCSV - END");
	}

	@PostMapping("/dbconsole/recordCountReasonabilityCheckCSV")
	public void getRecordCountReasonabilityCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/recordCountReasonabilityCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters" + inputsJsonStr);
					List<RecordCountReasonability> checks = csvService.getRecordCountReasonabilityChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CountReasonability" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "RCStdDevStatus", "RecordCount", "StandardDevRC", "MeanRC",
								"DayOfYear", "Month", "DayOfMonth", "DayOfWeek", "HourOfDay", "MeanStatus",
								"MeanMovingAvgRC", "MicrosegmentValue", "DuplicateDataSet", "FileNameValidationStatus",
								"ColumnOrderValidationStatus" };
						String[] header = { "Execution Date", "Run", "Std Dev Status", "Record Count", "Std Dev",
								"Mean", "Day", "Month", "Day Of Month", "Day Of Week", "Hour", "Mean Status",
								"Deviation", "RC Mean Moving Avg", "Microsegment Value", "Duplicate Dataset",
								"File Name validation Status", "Column Order Validation Status" };
						csvWriter.writeHeader(header);

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null, null, null, null };
						for (RecordCountReasonability check : checks) {
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setDGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							if (check.getDGroupCol() != null && !check.getDGroupCol().trim().isEmpty())
								check.setDGroupCol(check.getDGroupCol().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in recordCountReasonabilityCheckCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/recordCountReasonabilityCheckCSV - END");
	}

	@PostMapping("/dbconsole/recordCountTranCSV")
	public void getRecordCountReasonabilityTranCSV(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/recordCountTranCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<RecordCountResTransaction> checks = csvService.getRecordCountTranChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CountReasonability" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						/*String[] fields = { "Date", "Run", "RecordCount", "RacStdDev", "RacMean", "DayOfYear", "Month",
								"DayOfMonth", "DayOfWeek", "HourOfDay", "RacDeviation", "RacMeanMovingAvg", "MicroVal",
								"DuplicateDataSet", "FileNameValidationStatus", "ColumnOrderValidationStatus" };
						String[] header = { "Execution Date", "Run", "Record Count", "Std Dev", "Mean", "Day of Year",
								"Month", "Day Of Month", "Day Of Week", "Hour", "Deviation", "RC Mean Moving Avg",
								"Microsegment Value", "Duplicate Data Set", "File Name Validation Status",
								"Column Order Validation Status" };*/
						
						//DC 2946
							
						String[] fields = { "Date", "Run", "RecordCount", "RacStdDev", "RacMean", "DayOfYear", "Month",
								"DayOfMonth", "DayOfWeek", "HourOfDay", "RacDeviation", "RacMeanMovingAvg", "MicroVal",
								"DuplicateDataSet" };
						String[] header = { "Execution Date", "Run", "Record Count", "Std Dev", "Mean", "Day of Year",
								"Month", "Day Of Month", "Day Of Week", "Hour", "Deviation", "RC Mean Moving Avg",
								"Microsegment Value", "Duplicate Data Set" };

						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null};
						csvWriter.writeHeader(header);
						for (RecordCountResTransaction check : checks) {
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setdGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicroCol() != null && !check.getMicroCol().trim().isEmpty())
								check.setdGroupCol(check.getMicroCol().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in recordCountTranCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/recordCountTranCSV - END");
	}

	@PostMapping("/dbconsole/recordCountDGroupCSV")
	public void getRecordCountDGroupCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/recordCountDGroupCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<RecordCountSumGroup> checks = csvService.getRecordCountDGroupChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CountReasonabilitySum" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "RecordCount", "MxMean", "MxStdDev", "MicroVal", "DayOfYear",
								"Month", "DayOfMonth", "DayOfWeek", "HourOfDay", "MxDeviation", "UserName", "Time" };
						String[] header = { "Execution Date", "Run", "Record Count", "Mean", "Std Dev",
								"Microsegment Value", "Day of Year", "Month", "Day Of Month", "Day Of Week", "Hour",
								"Deviation", "User Name", "Time" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (RecordCountSumGroup check : checks) {
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setDGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));

							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in recordCountDGroupCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/recordCountDGroupCSV - END");
	}

	@PostMapping("/dbconsole/processDataCSV")
	public void getProcessDataCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/processDataCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					List<ProcessData> checks = csvService.getProcessData(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "IdApp", "Date", "Run", "FolderName" };
						String[] header = { "ID App", "Execution Date", "Run", "Folder Name" };
						final CellProcessor[] processors = new CellProcessor[] { null, new ParseLocalDate(), null,
								null };
						csvWriter.writeHeader(header);
						for (ProcessData check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found ");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception in processDataCSV "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/processDataCSV - END");
	}

	@PostMapping("/dbconsole/customSummaryCheckCSV")
	public void getCustomeSummaryCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/customSummaryCheckCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {				
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<CustomeSummary> checks = csvService.getCustomeSummaryChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeSummary" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "Type", "TotalCount", "Duplicate", "Percentage",
								"Threshold" };
						String[] header = { "Execution Date", "Run", "Status", "Type", "Total Records", "Duplicate",
								"Duplicate %", "Threshold" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null };
						csvWriter.writeHeader(header);
						for (CustomeSummary check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/customSummaryCheckCSV - END");
	}

	@PostMapping("/dbconsole/customUniquenessCSV")
	public void getCustomeUniqueCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/customUniquenessCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<CustomUniqueness> checks = csvService.getCustomeUniqueChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeUniqueness" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "DuplicateCheckFields", "DuplicateCheckValues", "Dupcount",
								"MicroVal", "MicroCol" };
						String[] header = { "Execution Date", "Run", "Duplicate Fields", "Duplicate Value",
								"Duplicate Count", "Microsegment Value", "Microsegment Column" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null };
						csvWriter.writeHeader(header);
						for (CustomUniqueness check : checks) {
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setdGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicroCol() != null && !check.getMicroCol().trim().isEmpty())
								check.setdGroupCol(check.getMicroCol().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/customUniquenessCSV - END");
	}

	@PostMapping("/dbconsole/primaryKeysCSV")
	public void getPrimaryKeysCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/primaryKeysCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<CustomUniqueness> checks = csvService.getCustomeUniqueChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeys" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "DuplicateCheckFields", "DuplicateCheckValues", "Dupcount",
								"MicroVal", "MicroCol" };
						String[] header = { "Execution Date", "Run", "Duplicate Fields", "Duplicate Value",
								"Duplicate Count", "Microsegment Value", "Microsegment Column" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null };
						csvWriter.writeHeader(header);
						for (CustomUniqueness check : checks) {
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setdGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicroCol() != null && !check.getMicroCol().trim().isEmpty())
								check.setdGroupCol(check.getMicroCol().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			e.printStackTrace();
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/primaryKeysCSV - END");
	}

	@PostMapping("/dbconsole/distributionCheckCSV")
	public void getDistributionCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/distributionCheckCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DistributionCheck> checks = csvService.getDistributionChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "DistributionCheck" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "Count", "Mean", "Microsegment",
								"MicrosegmentValue", "ColName", "Min", "Max", "StdDev", "NumMeanAvg", "NumMeanStdDev",
								"NumMeanDeviation", "NumMeanThreshold", "NumMeanStatus", "NumSDStdDev",
								"NumSDDeviation", "NumSDThreshold", "NumSDStatus", "NumSumThreshold", "sumOfNumStat",
								"NumSumStatus" };
						String[] header = { "Execution Date", "Run", "Status", "Count", "Mean", "Microsegment",
								"Microsegment Value", "Column Name", "Min", "Max", "Std Dev", "Number Mean Avg",
								"Num Mean Std Dev", "Num Mean Dev", "Num Mean Threshold", "Num Mean Status",
								"Num SD Std Dev", "Num SD Dev", "Num SD Threshold", "Num SD Status",
								"Num Sum Threshold", "Sum Of Num Stat", "Num Sum Status" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null, null, null, null, null,
								null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (DistributionCheck check : checks) {
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setDGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setDGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/distributionCheckCSV - END");
	}

	@PostMapping("/dbconsole/sequenceCheckCSV")
	public void getSequenceCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/sequenceCheckCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<SequenceCheck> checks = csvService.getSequenceChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "SequenceCheck" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "TimelinessKey", "NoOfDays", "TotalCount" };
						String[] header = { "Execution Date", "Run", "Status", "Timeliness Key", "No Of Days",
								"Total Count" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null };
						csvWriter.writeHeader(header);
						for (SequenceCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/sequenceCheckCSV - END");
	}

	@PostMapping("/dbconsole/stringDupCheckCSV")
	public void getStringDupCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/stringDupCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<StringDuplicate> checks = csvService.getStringDuplicateChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "StringDuplicate" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "ColName", "UniqueValues", "Microsegment",
								"MicrosegmentValue", "PotentialDuplicates" };
						String[] header = { "Execution Date", "Run", "Column Name", "Unique Values", "Microsegment",
								"Microsegment Value", "Potential Duplicate" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null };
						csvWriter.writeHeader(header);
						for (StringDuplicate check : checks) {
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setDGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setDGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/stringDupCheckCSV - END");
	}

	@PostMapping("/dbconsole/dateConsSummaryCSV")
	public void getDateConsSummaryCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/dateConsSummaryCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DateConsSummary> checks = csvService.getDateConsSummary(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "DateConsSummary" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "DateField", "TotalNumberOfRecords", "TotalFailedRecords",
								"ForgotRunEnabled" };
						String[] header = { "Execution Date", "Run", "Date Field", "Total Number Of Records",
								"Total Failed Records", "Forgot Run Enable" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null };
						csvWriter.writeHeader(header);
						for (DateConsSummary check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/dateConsSummaryCSV - END");
	}

	@PostMapping("/dbconsole/microDateRuleCheckCSV")
	public void getMicroDateRuleCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/microDateRuleCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<MicroDateRuleCheck> checks = csvService.getMicroDateRuleChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "MicroDateRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "DateFieldCols", "DateFieldValues", "Microsegment",
								"MicrosegmentValue", "FailureReason" };
						String[] header = { "Execution Date", "Run", "Date Field Cols", "Date Field Values",
								"Microsegment", "Microsegment Value", "Failure Reason" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null };
						csvWriter.writeHeader(header);
						for (MicroDateRuleCheck check : checks) {
							if (check.getMicrosegment() != null && !check.getMicrosegment().trim().isEmpty())
								check.setdGroupCol(check.getMicrosegment().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicrosegmentValue() != null && !check.getMicrosegmentValue().trim().isEmpty())
								check.setdGroupVal(check.getMicrosegmentValue().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/microDateRuleCheckCSV - END");
	}

	@PostMapping("/dbconsole/dateAnomalyCheckCSV")
	public void getDateAnomalyCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/dateAnomalyCheckCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DateAnomalyCheck> checks = csvService.getDateAnomalyChecks(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "DateAnomalyCheck" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "DateField", "TotalNumberOfRecords", "TotalFailedRecords" };
						String[] header = { "Execution Date", "Run", "Date Field", "Total Number Of Records",
								"Total Failed Records" };
						csvWriter.writeHeader(header);
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null };
						for (DateAnomalyCheck check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			e.printStackTrace();
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/dateAnomalyCheckCSV - END");
	}

	@PostMapping("/dbconsole/sqlRuleCSV")
	public void getSQLRuleCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/sqlRuleCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<SqlRule> checks = csvService.getSqlRules(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "DateAnomalyCheck" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "RuleName", "Status", "TotalRecords", "TotalFailedRecords",
								"RuleThreshold" };
						String[] header = { "Execution Date", "Run", "Rule Name", "Status", "Total Records",
								"Total Failed Records", "Rule Threshold" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null };
						csvWriter.writeHeader(header);
						for (SqlRule check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/sqlRuleCSV - END");
	}

	@PostMapping("/dbconsole/customRuleCSV")
	public void getCustomeRuleCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/customRuleCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<CustomRule> checks = csvService.getCustomRules(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "RuleName", "TotalRecords", "TotalFailed", "RulePercentage",
								"RuleThreshold", "Status" };
						String[] header = { "Execution Date", "Run", "Rule Name", "Total Records", "Total Failed",
								"Rule %", "Rule Threshold", "Status" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null };
						csvWriter.writeHeader(header);
						for (CustomRule check : checks) {
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/customRuleCSV - END");
	}

	@PostMapping("/dbconsole/globalRuleCSV")
	public void getGlobalRuleCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/globalRuleCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<GlobalRule> checks = csvService.getGlobalRules(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "RuleName", "TotalRecords", "TotalFailed", "dGroupVal",
								"RulePercentage", "RuleThreshold", "Status", "DimensionName" };
						String[] header = { "Execution Date", "Run", "Rule Name", "Total Records", "Total Failed",
								"Microsegment Value", "Rule %", "Rule Threshold", "Status", "Dimension" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (GlobalRule check : checks) {
							if(check.getDGroupVal()!=null && !check.getDGroupVal().trim().isEmpty())
								check.setDGroupVal(check.getDGroupVal().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/globalRuleCSV - END");
		
	}

	@PostMapping("/dbconsole/defaultPatternCheckCSV")
	public void getDefaultPatternCheckCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/defaultPatternCheckCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DefaultPatternCheck> checks = csvService.getDefaultPatternCheck(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "ColName", "TotalRecords", "TotalFailedRecords",
								"PatternsList", "FailedRecordsPercentage", "TotalMatchedRecords", "PatternThreshold" };
						String[] header = { "Execution Date", "Run", "Status", "Column Name", "Total Records",
								"Total Failed Records", "Patterns List", "Failed Records %", "Total Matched Records",
								"Pattern Threshold" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (DefaultPatternCheck check : checks) {
							if ("passed".equalsIgnoreCase(check.getStatus())
									&& "Y".equalsIgnoreCase(check.getNewPattern())) {
								check.setStatus("new");
							}
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			e.printStackTrace();
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/defaultPatternCheckCSV - END");
	}

	@PostMapping("/dbconsole/duplicateSummaryAllCSV")
	public void getDuplicateSummaryAllCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/duplicateSummaryAllCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DuplicateSummary> checks = csvService.getDuplicateSummary(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "Type", "MicroVal", "MicroCol",
								"DuplicateCheckFields", "Duplicate", "TotalCount", "Percentage", "Threshold" };
						String[] header = { "Execution Date", "Run", "Status", "Type", "Microsegment Value",
								"Microsegment Column", "Duplicate Check Fields", "Duplicate", "Total Count",
								"Percentage %", "Duplicate Threshold" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (DuplicateSummary check : checks) {
							if (check.getMicroCol() != null && !check.getMicroCol().trim().isEmpty())
								check.setdGroupCol(check.getMicroCol().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setdGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/duplicateSummaryAllCSV - END");
	}

	@PostMapping("/dbconsole/duplicateSummaryIdentityCSV")
	public void getDuplicateSummaryIdentityCSV(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/duplicateSummaryIdentityCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					LOG.debug("Getting request parameters " + inputsJsonStr);
					List<DuplicateSummary> checks = csvService.getDuplicateSummary(inputsJsonStr);
					if (checks != null && checks.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "CustomeRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "Date", "Run", "Status", "Type", "MicroVal", "MicroCol",
								"DuplicateCheckFields", "Duplicate", "TotalCount", "Percentage", "Threshold" };
						String[] header = { "Execution Date", "Run", "Status", "Type", "Microsegment Value",
								"Microsegment Column", "Duplicate Check Fields", "Duplicate", "Total Count",
								"Percentage %", "Threshold" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (DuplicateSummary check : checks) {
							if (check.getMicroCol() != null && !check.getMicroCol().trim().isEmpty())
								check.setdGroupCol(check.getMicroCol().trim().replaceAll("\\?::\\?", ","));
							if (check.getMicroVal() != null && !check.getMicroVal().trim().isEmpty())
								check.setdGroupVal(check.getMicroVal().trim().replaceAll("\\?::\\?", ","));
							csvWriter.write(check, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/duplicateSummaryIdentityCSV - END");
	}

	@PostMapping("/dbconsole/getPrimaryKeyMatchingRecordsCSV")
	public void getPrimaryKeyMatchingRecordsCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getPrimaryKeyMatchingRecordsCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("appId")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					LOG.debug("appId   " + requestBody.get("appId"));
					List<HashMap<String, String>> tranDetailsResult = csvService
							.getPrimaryKeyMatchingRecordsDetails(requestBody.get("appId"));
					if (tranDetailsResult != null && tranDetailsResult.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchRecords" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						List<LinkedHashMap<String, String>> orderedTransDetail = new ArrayList<LinkedHashMap<String, String>>();
						for (HashMap<String, String> map : tranDetailsResult) {
							LinkedHashMap<String, String> lmap = new LinkedHashMap<String, String>();
							if(map.get("Date")!=null) {
								lmap.put("Date", map.get("Date"));
							}else if (map.get("date")!=null) {
								lmap.put("Date", map.get("date"));
							}
							if(map.get("Run")!=null) {
								lmap.put("Run", map.get("Run"));
							}else if (map.get("run")!=null) {
								lmap.put("Run", map.get("run"));
							}
							map.entrySet().stream().forEach(x -> {
								if (!x.getKey().equalsIgnoreCase("Date") && !x.getKey().equalsIgnoreCase("Run")) {
									lmap.put(x.getKey(), x.getValue());
								}
							});
							orderedTransDetail.add(lmap);
						}
						httpResponse.getWriter().print(toCSV(orderedTransDetail));
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			e.printStackTrace();
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getPrimaryKeyMatchingRecordsCSV - END");
	}

	@GetMapping("/dbconsole/getAppGroupsCSV")
	public void getAppGroupsCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getAppGroupsCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if (tokenValidator.isValid(token)) {
					List<ListAppGroup> listAppGroupData = csvService.getAppGroupsForProject(token);
					if (listAppGroupData != null && listAppGroupData.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchSummary" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "idAppGroup", "name", "description", "enableScheduling", "frequency",
								"scheduleDay", "time", "projectName" };
						String[] header = { "Id", "Name", "Description", "Scheduling Enabled", "Frequency",
								"Schedule Day", "Time", "Project Name" };
						csvWriter.writeHeader(header);
						for (ListAppGroup summ : listAppGroupData) {
							csvWriter.write(summ, fields);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getAppGroupsCSV - END");
	}

	@PostMapping("/dbconsole/getPrimaryKeyMatchingDashboardCSV")
	public void getPrimaryKeyMatchingDashboardCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getPrimaryKeyMatchingDashboardCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// Check whether all required parameters are available.
				LOG.debug("appId   " + requestBody.get("appId")+" source1 : "+ requestBody.get("source1")+" source2 : "+ requestBody.get("source2"));
				if (!requestBody.containsKey("appId")) {					
					LOG.debug("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					List<PrimaryKeyDashboardTable> primaryKeySumm = csvService.getPrimaryKeyMatchingDashboardData(
							requestBody.get("appId"), requestBody.get("source1"), requestBody.get("source2"));
					if (primaryKeySumm != null && primaryKeySumm.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchDashboard" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "keyMetrics", "measurement", "statusNRecordCount", "percentage",
								"threshold" };
						String[] header = { "Key Metrics", "Measurement", "Status & Record Count", "Percentage",
								"Threshold" };
						csvWriter.writeHeader(header);
						for (PrimaryKeyDashboardTable summ : primaryKeySumm) {
							csvWriter.write(summ, fields);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getPrimaryKeyMatchingDashboardCSV - END");
	}

	@PostMapping("/dbconsole/getPrimaryKeyMatchingResultsDetailsCSV")
	public void getPrimaryKeyMatchingResultsDetailsCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getPrimaryKeyMatchingResultsDetailsCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters " + requestBody);
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("domainId") || !requestBody.containsKey("projectId")
						|| !requestBody.containsKey("fromDate") || !requestBody.containsKey("toDate")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
					
				}
				if (tokenValidator.isValid(token)) {
					List<KeyMeasurementMatchingDashboard> primaryKeyResultDetails = csvService
							.getPrimaryKeyMatchingResultsDetails(requestBody.get("domainId"),
									requestBody.get("projectId"), requestBody.get("fromDate"),
									requestBody.get("toDate"));
					if (primaryKeyResultDetails != null && primaryKeyResultDetails.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchResultDetails" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "date", "run", "validationCheckName", "source1", "source2", "source1Count",
								"source2Count", "unmatchedRecords", "source1Records", "source2Records", "idApp",
								"source1OnlyStatus", "source2OnlyStatus", "unmatchedStatus" };
						String[] header = { "Date", "Run", "Validation Check Name", "First Source Name", "Second Source Name",
								"First Source Count", "Second Source Count", "Unmatched Records", "Only First Source Records",
								"Only Second Source Records", "Validation Id", "First Source Status", "Second Source Status",
								"Unmatched Status" }; 
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (KeyMeasurementMatchingDashboard primaryKeyResultDetail : primaryKeyResultDetails) {
							csvWriter.write(primaryKeyResultDetail, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
						
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getPrimaryKeyMatchingResultsDetailsCSV - END");
	}

	@PostMapping("/dbconsole/getKeyMeasurementMatchingResultsCSV")
	public void getKeyMeasurementMatchingResultsCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getKeyMeasurementMatchingResultsCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters " + requestBody);
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("domainId") || !requestBody.containsKey("projectId")
						|| !requestBody.containsKey("fromDate") || !requestBody.containsKey("toDate")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					List<KeyMeasurementMatchingDashboard> keyMeasurementResultDetails = csvService
							.getKeyMeasurementMatchingDashboardByProjectNDateFilter(requestBody.get("domainId"),
									requestBody.get("projectId"), requestBody.get("fromDate"),
									requestBody.get("toDate"));
					if (keyMeasurementResultDetails != null && keyMeasurementResultDetails.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "KeyMeasurementMatchResultDetails" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "date", "run", "validationCheckName", "source1", "source2", "source1Count",
								"source2Count", "unmatchedRecords", "source1Records", "source2Records", "idApp",
								"source1OnlyStatus", "source2OnlyStatus", "unmatchedStatus" };
						String[] header = { "Date", "Run", "Validation Check Name", "Source1 Name", "Source2 Name",
								"Source1 Count", "Source2 Count", "Unmatched Records", "Source1 Records Only",
								"Source2 Records Only", "Validation Id", "Source 1 Status", "Source 2 Status",
								"Unmatched Status" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null, null, null, null, null };
						csvWriter.writeHeader(header);
						for (KeyMeasurementMatchingDashboard primaryKeyResultDetail : keyMeasurementResultDetails) {
							csvWriter.write(primaryKeyResultDetail, fields, processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getKeyMeasurementMatchingResultsCSV - END");
	}

	@PostMapping("/dbconsole/getTranTableCSV")
	public void getTranTableCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> requestBody,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getTranTableCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters " + requestBody);
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("appId") || !requestBody.containsKey("category")
						|| !requestBody.containsKey("matchingType")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					String tableName = "DATA_MATCHING_" + requestBody.get("appId");
					String csvFileName = "KeyMeasurementMatchResultDetails";
					if (requestBody.get("matchingType").equalsIgnoreCase("primary")) {
						tableName = tableName + "_PRIMARY";
						csvFileName = "PrimaryKeyMeasurementMatchResultDetails";
					}
					if (requestBody.get("category").contains("left")) {
						tableName = tableName + "_LEFT";
					} else if (requestBody.get("category").contains("right")) {
						tableName = tableName + "_RIGHT";
					} else if (requestBody.get("category").contains("unmatched")) {
						tableName = tableName + "_UNMATCHED";
					}
					List<Map<String, Object>> tranDetailsResult = csvService.getTranTablesDetails(tableName);
					if (tranDetailsResult != null && tranDetailsResult.size() > 0) {
						httpResponse.setContentType("text/csv");
						csvFileName = csvFileName + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						httpResponse.getWriter().print(mapToCSV(tranDetailsResult));
					

					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getTranTableCSV - END");
	}

	@PostMapping("/dbconsole/getKeyMeasurementMatchingDetailsCSV")
	public void getKeyMeasurementMatchingDetailsCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getKeyMeasurementMatchingDetailsCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters " + requestBody.containsKey("appId"));
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("appId")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					SqlRowSet sqlRowSet = iMatchingResultService
							.getDataMatchingResultsTableNames(Long.parseLong(requestBody.get("appId")));
					List<DataMatchingSummary> dmSummaryData = new ArrayList<DataMatchingSummary>();
					while (sqlRowSet.next()) {
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
							dmSummaryData = iMatchingResultService
									.getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
							break;
						}
					}
					if (dmSummaryData != null && dmSummaryData.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "KeyMeasurementMatchingDetails" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "totalRecordsInSource1", "totalRecordsInSource2", "unmatchedRecords",
								"source1OnlyRecords", "source2OnlyRecods", "status", "soure1OnlyPercenage",
								"soure2OnlyPercenage" };
						String[] header = { "Source 1 Record Count", "Source 2 Record Count", "Unmatched Records",
								"Source 1 Only Records", "Source 2 Only Records", "Unmatched Status", "Source 1 Only %",
								"Source 2 Only %" };
						csvWriter.writeHeader(header);
						for (DataMatchingSummary keyMeasurementMatchingDetail : dmSummaryData) {
							csvWriter.write(keyMeasurementMatchingDetail, fields);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("/dbconsole/getKeyMeasurementMatchingDetailsCSV - END");
	}

	@PostMapping("/dbconsole/getKeyMeasurementMatchingDashboardCSV")
	public void getKeyMeasurementMatchingDashboardCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getKeyMeasurementMatchingDashboardCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// Check whether all required parameters are available.
				LOG.debug("appId "+requestBody.containsKey("appId"));
				if (!requestBody.containsKey("appId")) {
					LOG.error("Required parameters not found.");
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					List<PrimaryKeyDashboardTable> primaryKeySumm = csvService.getKeyMeasurementMatchDashboardData(
							requestBody.get("appId"), requestBody.get("source1"), requestBody.get("source2"));

					if (primaryKeySumm != null && primaryKeySumm.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchDashboard" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "keyMetrics", "measurement", "statusNRecordCount", "percentage",
								"threshold" };
						String[] header = { "Key Metrics", "Measurement", "Status & Record Count", "Percentage",
								"Threshold" };
						csvWriter.writeHeader(header);
						for (PrimaryKeyDashboardTable summ : primaryKeySumm) {
							csvWriter.write(summ, fields);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception "+e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@PostMapping(value = "/dbconsole/downloadCsvS3")
	public void downloadCsvS3(@RequestHeader HttpHeaders headers, HttpServletRequest request,
			HttpServletResponse response, @RequestBody Map<String, String> params) throws IOException {
		LOG.info("/dbconsole/downloadCsvS3 - START");		
		String tableName = params.get("tableName");
		String idApp = params.get("idApp");
		String tableNickName = params.get("tableNickName");
		String reportDate = params.get("reportDate");
		String reportRun = params.get("reportRun");
		String fileSelected = params.get("fileSelected");
		String isDirect = params.get("isDirect");
		String directCsvPath = params.get("directCsvPath");
		
		LOG.info("idApp:" + idApp);
		LOG.info("tableName:" + tableName);
		LOG.info("tableNickName:" + tableNickName);
		LOG.info("reportDate:" + reportDate);
		LOG.info("reportRun:" + reportRun);
		LOG.info("dr_fileSelected:" + fileSelected);
		LOG.info("isDirect:" + isDirect);
		LOG.info("directCsvPath:" + directCsvPath);
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token == null || token.isEmpty()) {
				throw new Exception("Token is missing in headers.");
			}
			if (!tokenValidator.isValid(token)) {
				throw new Exception("Token is expired.");
			}
			this.downloadCSVService.downloadCSV(tableName, idApp, tableNickName, reportDate, reportRun, fileSelected,
					isDirect, directCsvPath, response, request);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception "+ex.getMessage());
			try {
				response.sendError(0, ex.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("/dbconsole/downloadCsvS3 - END");
	}
	
	@PostMapping(value = "/dbconsole/absa/downloadCsvDateRange")
	public void absaDownloadCsvS3(@RequestHeader HttpHeaders headers, HttpServletRequest request,
			HttpServletResponse response, @RequestBody AbsaDownloadCSVRequest params) throws IOException {
		LOG.info("/dbconsole/absa/downloadCsvDateRange - START");		
		String tableName = params.getTableName();
		String idApp = params.getIdApp();
		List<String> tableNickName = params.getTableNickName();
		String reportFromDate = params.getReportFromDate();
		String reportToDate = params.getReportToDate();
		String reportRun = params.getReportRun(); 
		String fileSelected = params.getFileSelected(); 
		String directCsvPath = params.getDirectCsvPath();
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token == null || token.isEmpty()) {
				throw new Exception("Token is missing in headers.");
			}
			if (!tokenValidator.isValid(token)) {
				throw new Exception("Token is expired.");
			} 
			this.downloadCSVService.downloadCSVDateRange(tableName, idApp, tableNickName, reportFromDate,reportToDate,
					 fileSelected, directCsvPath, response, request);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception "+ex.getMessage());
			try {
				response.sendError(0, ex.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("/dbconsole/absa/downloadCsvDateRange - END");
	}

	@PostMapping("/dbconsole/getPaginatedDataTemplatesCSV")
	public void getPaginatedDataTemplatesCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getPaginatedDataTemplatesCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (token != null && !token.isEmpty()) {
				// Check whether all required parameters are available.
				if (!requestBody.containsKey("SearchByOption") || !requestBody.containsKey("FromDate")
						|| !requestBody.containsKey("ToDate") || !requestBody.containsKey("ProjectIds")
						|| !requestBody.containsKey("SearchText")) {
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {

					HashMap<String, String> oPaginationParms = new HashMap<String, String>();
					for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds",
							"SearchText" }) {
						oPaginationParms.put(sParmName, requestBody.get(sParmName));
					}
					
					
					if(requestBody.containsKey("filterByDerived")) {
						oPaginationParms.put("filterByDerived", requestBody.get("filterByDerived"));
					}
					
 
					List<HashMap<String, String>> tranDetailsResult = csvService
							.getPaginatedDataTemplates(oPaginationParms);
					if (tranDetailsResult != null && tranDetailsResult.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchRecords" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						List<String> headerList = Arrays.asList("Template ID", "Validation Template Name",
								"Connection Type", "Table Name", "Project", "Created On", "Updated On", "Created By");
						List<String> keyList = Arrays.asList("TemplateId", "TemplateName", "DataLocation", "Tablename",
								"ProjectName", "CreatedAt", "UpdatedAt", "CreatedByUser");
						httpResponse.getWriter().print(toCSVByHeaders(tranDetailsResult, headerList, keyList));
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			httpResponse.setContentType("text/csv");
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try {
				httpResponse.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("dbconsole/getPaginatedDataTemplatesCSV - END");
	}

	@PostMapping("/dbconsole/getPrimaryKeyMatchingSummaryCSV")
	public void getPrimaryKeyMatchingSummaryCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getPrimaryKeyMatchingSummaryCSV - START");
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// Check whether all required parameters are available.
				LOG.debug("appId   " + requestBody.containsKey("appId"));
				if (!requestBody.containsKey("appId")) {
					
					throw new Exception("Required parameters not found.");
				}
				if (tokenValidator.isValid(token)) {
					List<PrimaryMatchingSummary> primaryKeySumm = csvService
							.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(requestBody.get("appId"));
					if (primaryKeySumm != null && primaryKeySumm.size() > 0) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "PrimaryKeyMatchSummary" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						
						
						String[] fields = { "date", "run", "leftTotalCount", "rightTotalCount", "leftOnlyCount",
								"leftOnlyPercentage", "rightOnlyCount", "rightOnlyPercentage", "leftNullCount",
								"rightNullCount" };
						String[] header = { "Date", "Run", "Left Record Count", "Right Record Count",
								"Left Only Records", "Left Only Percentage", "Right Only Records",
								"Right Only Percentage", "Left Null Count", "Right Null Count" };
						final CellProcessor[] processors = new CellProcessor[] { new ParseLocalDate(), null, null, null,
								null, null, null, null, null, null};

						csvWriter.writeHeader(header);
						for (PrimaryMatchingSummary summ : primaryKeySumm) {
							csvWriter.write(summ, fields,processors);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found");
						throw new Exception("Records not found.");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Please provide token.");
				throw new Exception("Please provide token.");
			}
		} catch (Exception e) {
			LOG.error("Exception "+e.getMessage());
			e.printStackTrace();
			
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private String mapToCSV(List<Map<String, Object>> tranDetailsResult) {
		List<String> headers = tranDetailsResult.stream().flatMap(map -> map.keySet().stream()).distinct()
				.collect(Collectors.toList());
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append(i == headers.size() - 1 ? "\n" : ",");
		}
		for (Map<String, Object> map : tranDetailsResult) {
			for (int i = 0; i < headers.size(); i++) {
				sb.append(map.get(headers.get(i)));
				sb.append(i == headers.size() - 1 ? "\n" : ",");
			}
		}
		return sb.toString();
	}

	private String toCSV(List<LinkedHashMap<String, String>> tranDetailsResult) {
		LOG.info("toCSV - START");
		List<String> headers = tranDetailsResult.stream().flatMap(map -> map.keySet().stream()).distinct()
				.collect(Collectors.toList());
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append(i == headers.size() - 1 ? "\n" : ",");
		}
		for (HashMap<String, String> map : tranDetailsResult) {
			for (int i = 0; i < headers.size(); i++) {
				sb.append(map.get(headers.get(i)));
				sb.append(i == headers.size() - 1 ? "\n" : ",");
			}
		}
		LOG.info("toCSV - END");
		return sb.toString();
	}

	private String toCSVByHeaders(List<HashMap<String, String>> tranDetailsResult, List<String> headers,
			List<String> keyList) {
		LOG.info("toCSVByHeaders - START");
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append("\""+headers.get(i));
			sb.append(i == headers.size() - 1 ? "\" \n" : "\",");
		}
		for (HashMap<String, String> map : tranDetailsResult) {
			for (int i = 0; i < keyList.size(); i++) {
				sb.append("\""+map.get(keyList.get(i)));
				sb.append(i == keyList.size() - 1 ? "\" \n" : "\",");
			}
		}
		LOG.info("toCSVByHeaders - END");
		return sb.toString();
	}

	private String toCSVByHeadersWithObject(List<Map<String, Object>> tranDetailsResult, List<String> headers,
			List<String> keyList) {

		LOG.info("toCSVByHeadersWithObject - START");
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append(i == headers.size() - 1 ? "\n" : ",");
		}
		for (Map<String, Object> map : tranDetailsResult) {
			for (int i = 0; i < keyList.size(); i++) {
				sb.append(map.get(keyList.get(i).trim()));
				sb.append(i == keyList.size() - 1 ? "\n" : ",");
			}
		}
		LOG.info("toCSVByHeadersWithObject - END");
		return sb.toString();
	}

	@PostMapping(value = "/dbconsole/downloadExceptionData")
	public void downloadExceptionData(@RequestHeader HttpHeaders headers, HttpServletRequest request,
									  HttpServletResponse response, @RequestBody Map<String, String> params) throws IOException {
		LOG.info("/dbconsole/downloadExceptionData - START");

		String idApp = "" + params.get("idApp");
		String runDate = "" + params.get("runDate");
		String runNumber = "" + params.get("runNumber");
		String checkName = "" + params.get("checkName");
		String columnName = "" + params.get("columnName");

		LOG.info("idApp:" + idApp);
		LOG.info("runDate:" + runDate);
		LOG.info("runNumber:" + runNumber);
		LOG.info("checkName:" + checkName);
		LOG.info("columnName:" + columnName);
		String message="";
		try {
			String token = headers.get("token").get(0);
			if (token == null || token.isEmpty()) {
//				throw new Exception("Token is missing in headers.");
				message= "Token is missing in headers.";
			}
			if (!tokenValidator.isValid(token)) {
//				throw new Exception("Token is expired.");
				message= "Token is expired.";
			}else {
				Map<String, String> downloadParams = new HashMap<>();
				downloadParams.put("idApp", idApp);
				downloadParams.put("runDate", runDate);
				downloadParams.put("runNumber", runNumber);
				downloadParams.put("checkName", checkName);
				downloadParams.put("columnName", columnName);

				message = downloadCSVService.downloadCSV(downloadParams, response, request);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception " + ex.getMessage());
			message= ex.getMessage();
			try {
				response.sendError(0, ex.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("/dbconsole/downloadExceptionData - END");

		if(message!=null && !message.trim().isEmpty()) {
			try {
				HttpSession session = request.getSession();
				session.setAttribute("errormsg", message);
				JSONObject json = new JSONObject();
				json.put("failed", message);
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
