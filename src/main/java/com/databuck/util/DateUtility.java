package com.databuck.util;

import com.databuck.constants.DatabuckConstants;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.TimeZone;

import org.apache.log4j.Logger;


public class DateUtility {
	private static final Logger LOG = Logger.getLogger(DateUtility.class);

	public static <T> double calculatePercentOfNullValues(List<T> numberList){
		int numberOfNullValues = 0;
		int totalValues = 0;
		for(T singleVal: numberList){
			if(singleVal == null ){
				numberOfNullValues++;
			}
			totalValues++;
		}

		return ((1.0 * numberOfNullValues)/totalValues) * 100;
	}

	public static Date calculateMinDate(List<Date> dateList){
		Date minDate = new Date(Long.MAX_VALUE);

		for(Date currDate: dateList){
			if(currDate != null && currDate.before(minDate)){
				minDate = currDate;
			}
		}

		return minDate;
	}

	public static Date calculateMaxDate(List<Date> dateList){
		Date maxDate = new Date(Long.MIN_VALUE);

		for(Date currDate: dateList){
			if(currDate != null && currDate.after(maxDate)){
				maxDate = currDate;
			}
		}

		return maxDate;
	}

	public static void DebugLog(String sDebugContext, String sDebugText) {
	   	DateTimeFormatter oDtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	   	LocalDateTime oNow = LocalDateTime.now();
	   	LOG.debug(oDtf.format(oNow) + " " + sDebugContext + " " + sDebugText);
	}

	public static String getSysDateTimeStamp(boolean lIncludeTime) {
		DateTimeFormatter oDateTimeFormatter = (lIncludeTime) ? DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss") : DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime oNow = LocalDateTime.now();
		return oDateTimeFormatter.format(oNow);
	}

	/* set of methods returning start and end dates range for Daily, Weekly, Bi-Monthly, Monthly, Quarterly, Yearly */

	public static HashMap<String, String> getDailyDateRange(LocalDate oDate) {
		LocalDate oWorkingDate = oDate;
		HashMap<String, String> oRetValue = new HashMap<String, String>();

		//oWorkingDate = oWorkingDate.plusDays(-1);

		oRetValue.put("CurrentPeriodStartDate", String.format("%tF", oWorkingDate));
		oRetValue.put("CurrentPeriodToDate", String.format("%tF", oWorkingDate));

		oWorkingDate = oWorkingDate.plusDays(-1);

		oRetValue.put("PreviousPeriodStartDate", String.format("%tF", oWorkingDate));
		oRetValue.put("PreviousPeriodToDate", String.format("%tF", oWorkingDate));

		return oRetValue;
	}

	public static HashMap<String, String> getWeeklyDateRange(LocalDate oDate) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();
		LocalDate oWorkingDate = oDate.with(DayOfWeek.MONDAY);

		oRetValue.put("CurrentPeriodStartDate", String.format("%tF", oWorkingDate));

		oWorkingDate = oWorkingDate.plusDays(6);
		oRetValue.put("CurrentPeriodToDate", String.format("%tF", oWorkingDate));

		oWorkingDate = oWorkingDate.plusDays(-13);
		oRetValue.put("PreviousPeriodStartDate", String.format("%tF", oWorkingDate));

		oWorkingDate = oWorkingDate.plusDays(6);
		oRetValue.put("PreviousPeriodToDate", String.format("%tF", oWorkingDate));

		return oRetValue;
	}

	public static HashMap<String, String> getBiMonthlyDateRange(LocalDate oDate) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();
		boolean lAcrossTwoMonths = (oDate.getDayOfMonth() <= 15) ? true : false;

    	LocalDate oWorkingDate = oDate;
    	Calendar oCalendar = Calendar.getInstance();

    	/* Get current date range values */
    	if (lAcrossTwoMonths) {
    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 1);
    		oRetValue.put("CurrentPeriodStartDate", String.format("%1$s", oWorkingDate));

    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 15);
    		oRetValue.put("CurrentPeriodToDate", String.format("%1$s", oWorkingDate));
    	} else {
    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 16);
    		oRetValue.put("CurrentPeriodStartDate", String.format("%1$s", oWorkingDate));

    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), oWorkingDate.lengthOfMonth());
    		oRetValue.put("CurrentPeriodToDate", String.format("%1$s", oWorkingDate));
    	}

    	/* Get previous date range values */
    	if (lAcrossTwoMonths) { 
    		oCalendar.add(Calendar.MONTH,-1);    		
    	}
    	oWorkingDate = LocalDate.parse(String.format("%tF", oCalendar.getTime()));

    	if (lAcrossTwoMonths) {
    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 16);
    		oRetValue.put("PreviousPeriodStartDate", String.format("%1$s", oWorkingDate));

    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), oWorkingDate.lengthOfMonth());
    		oRetValue.put("PreviousPeriodToDate", String.format("%1$s", oWorkingDate));
    	} else {
    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 1);
    		oRetValue.put("PreviousPeriodStartDate", String.format("%1$s", oWorkingDate));

    		oWorkingDate = LocalDate.of(oWorkingDate.getYear(), oWorkingDate.getMonthValue(), 15);
    		oRetValue.put("PreviousPeriodToDate", String.format("%1$s", oWorkingDate));
    	}
    	
    	return oRetValue;
    }

	public static HashMap<String, String> getQuanterlyDateRange(LocalDate oDate) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();

		String sQtrResult = "";
		sQtrResult = getStartEndQuarter(oDate.getYear(), oDate.getMonthValue());

		oRetValue.put("CurrentPeriodStartDate", sQtrResult.split(",")[0]);
		oRetValue.put("CurrentPeriodToDate", sQtrResult.split(",")[1]);

		oDate = oDate.plusMonths(-3);

		sQtrResult = getStartEndQuarter(oDate.getYear(), oDate.getMonthValue());
		oRetValue.put("PreviousPeriodStartDate", sQtrResult.split(",")[0]);
		oRetValue.put("PreviousPeriodToDate", sQtrResult.split(",")[1]);

		return oRetValue;
	}

	public static String getStartEndQuarter(int year, int month) {
		if (month % 3 == 2) {
			month = month - 1;
		}
		else if (month % 3 == 0) {
			month = month - 2;
		}
		LocalDate start = LocalDate.of(year, month, 1);
		Month endMonth = start.getMonth().plus(2);

		LocalDate end = LocalDate.of(year, endMonth, endMonth.length(start.isLeapYear()));

		return String.format("%1$s,%2$s",  start, end);
	}

	public static HashMap<String, String> getMonthlyDateRange(LocalDate oDate) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();
		SimpleDateFormat oSqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate oWorkingDay = oDate;

		oRetValue.put("CurrentPeriodStartDate", String.format("%1$s", oWorkingDay.withDayOfMonth(1)) );
		oRetValue.put("CurrentPeriodToDate", String.format("%1$s", oWorkingDay.withDayOfMonth(oWorkingDay.lengthOfMonth())) );

		Calendar oCalendar = Calendar.getInstance();

		oCalendar.add(Calendar.MONTH, -1);
		oCalendar.set(Calendar.DATE, 1);

		Date oFirstDateOfPreviousMonth = oCalendar.getTime();
		oCalendar.set(Calendar.DATE,     oCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date oLastDateOfPreviousMonth = oCalendar.getTime();

		oRetValue.put("PreviousPeriodStartDate", String.format("%1$s", oSqlDateFormat.format(oFirstDateOfPreviousMonth)) );
		oRetValue.put("PreviousPeriodToDate", String.format("%1$s", oSqlDateFormat.format(oLastDateOfPreviousMonth)) );

		return oRetValue;
	}

	public static HashMap<String, String> getYearlyDateRange(LocalDate oDate) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();

		SimpleDateFormat oSqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate oToday = LocalDate.now();

		oRetValue.put("CurrentPeriodStartDate", String.format("%1$s", oToday.withDayOfYear(1)) );
		oRetValue.put("CurrentPeriodToDate", String.format("%1$s", String.format("%1$s",oToday.withDayOfYear(365))) );

		Calendar oCalendar = Calendar.getInstance();

		oCalendar.add(Calendar.YEAR, -1);
		oCalendar.set(Calendar.DAY_OF_YEAR, 1);

		Date oFirstDateOfPreviousYear = oCalendar.getTime();
		oCalendar.set(Calendar.DATE,     oCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date oLastDateOfPreviousYear = oCalendar.getTime();

		oRetValue.put("PreviousPeriodStartDate", String.format("%1$s", oSqlDateFormat.format(oFirstDateOfPreviousYear)) );
		oRetValue.put("PreviousPeriodToDate", String.format("%1$s", oSqlDateFormat.format(oLastDateOfPreviousYear)) );

		return oRetValue;
	}

	public static Date getCurrentDateTimeByTimeZone(String timeZone) {
		Date date_local = new Date();
		if (timeZone.equalsIgnoreCase(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC)) {
			try {
				Instant timeStamp = Instant.now();
				LocalDateTime ldt = LocalDateTime.ofInstant(timeStamp, ZoneOffset.UTC);
				Date date_utc = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
				return date_utc;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
				return date_local;
			}
		} else
			return date_local;

	}

	public static String getCurrentUTCTimeByFormat(String DATE_FORMAT) {
		String utcTimeStr="";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			utcTimeStr = sdf.format(new Date());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return utcTimeStr;
	}
	
	public static Date getYesterday() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}
	
	public static Date getPreviousYear() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.YEAR, -1);
	    return cal.getTime();
	}

	public static String getDateinString(Date date,String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
	}
}
