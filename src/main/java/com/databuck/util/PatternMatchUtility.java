package com.databuck.util;

import com.databuck.bean.DateRuleMap;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

import java.util.*;
import java.util.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

public class PatternMatchUtility {
	
	private static final Logger LOG = Logger.getLogger(PatternMatchUtility.class);
	
	public static ArrayList<Integer> arr = new ArrayList<Integer>();

	// generate 2^n strings
	public static ArrayList<Integer> generatePattern(int index, int bits, int number) {

		if (index == 0) {
			if (bits == 0) { // all required bits have been used

				arr.add(number);
			}
		}

		if (index - 1 >= bits) { // If we can afford to put a 0 here
			generatePattern(index - 1, bits, number);

		}

		if (bits > 0) { // If we have any 1s left to place
			generatePattern(index - 1, bits - 1, number | (1 << (index - 1)));

		}
		return arr;
	}

	public static ArrayList<String> patternGeneration(List<String> lines) {

		String[] text = lines.toArray(new String[0]);
		ArrayList<String> regexArray = new ArrayList<String>();
		ArrayList<Integer> arr2 = new ArrayList<Integer>();
		Long unique = Arrays.stream(text).distinct().count();
		int length = text[0].length();

		LOG.debug("Length of each record:  " + text[0].length());
		int max = length;
		for (int min = 6; min <= length; min++) {
			// int min = 6;

			arr2 = generatePattern(length, min, 0);
		}
		LOG.debug("Total count in file:  " + text.length);
		boolean isDataValid = true;
		// ArrayList<String> arr3 = new ArrayList<String>();
		// List<String, Integer> arr3_minkey = new HashMap<String,
		// Integer>();
		Multimap<String, Integer> arr3_minkey = ArrayListMultimap.create();
		for (int a1 = 0; a1 < text.length; a1++) {
			if (text[a1].length() != length) {
				isDataValid = false;
				break;
			}
			// LOG.debug(arr2);
			for (int a = 0; a < arr2.size(); a++) {
					char[] z = new char[length + 1];
					int h = 0;
					int num_one = 0;
					for (int i = length - 1; i >= 0; i--) {
						if (((arr2.get(a) & (1 << i)) >> i) == 0)
							z[h] = '0';
						if (((arr2.get(a) & (1 << i)) >> i) == 1) {
							z[h] = '1';
							num_one++;
						}
						h++;
					}
					StringBuilder g = new StringBuilder(text[a1]);
                    //LOG.debug("SttringLen-->"+z.length);
					for (int j = 0; j < z.length; j++) {
						if (z[j] == '0')
							g.setCharAt(j, '.');
						
					}				
				// String g1 = g.toString().replaceAll("(\\*)\\1+","$1");
				arr3_minkey.put(g.toString(), num_one);
			}
		}
		if (isDataValid) {
			// LOG.debug(arr3_minkey);
			int len = 0;
			int keyval = 0;
			for (String maxkey : arr3_minkey.keySet()) {
				if (arr3_minkey.get(maxkey).size() >= len) {
					len = arr3_minkey.get(maxkey).size();

				}
			}

			LOG.debug("Total Match:  " + len);
			for (String key : arr3_minkey.keySet()) {

				if (arr3_minkey.get(key).size() == len) {
					for (Integer s : arr3_minkey.get(key)) {
						if (s >= keyval) {
							keyval = s;
							// LOG.debug("key:"+keyval);

						}

					}
				}
			}

			for (String mainkey : arr3_minkey.keySet()) {

				if (arr3_minkey.get(mainkey).size() == len) {

					for (Integer s : arr3_minkey.get(mainkey)) {
						if (s == keyval) {
							// keyval = s;
							LOG.debug("Pattern : " + mainkey);
							regexArray.add(mainkey);
							break;

						}

					}
				}
			}
		}
		return regexArray;

	}

	/*public static Map<String, List<String>> dateRulePattern(List<Integer> dateColumnsNumber,
			ResultSet resultSetFromDb, int maxrows, HttpSession session) {

		Map<String, List<String>> dataRuleMap = new HashMap<String, List<String>>();
		
		try {
			
			
			
			//int numcol = 5;
			 //maxrows = 989;
			
			LOG.debug(maxrows);
			String dateformat1 = "yyyy-MM-dd";
			String dateformat = "yyyyMMdd";
			
			int datecollist[]= dateColumnsNumber.stream().mapToInt(Integer::intValue).toArray();
			int numcol= dateColumnsNumber.size();
			//int datecollist[] = { 7, 9, 12, 17, 19 };
			List<String> colName = new ArrayList<String>();
			
			SimpleDateFormat dt = new SimpleDateFormat(dateformat);

			String[][] tableValues = new String[maxrows][numcol];
			LocalDate[][] xx = new LocalDate[maxrows][numcol];
			for(int val = 0; val < datecollist.length; val++){
				String name = resultSetFromDb.getMetaData().getColumnName(datecollist[val]);
				LOG.debug("xx Column Names xx");
				LOG.debug(name);
				colName.add(name);
			}
			
			int i = 0;

			while (resultSetFromDb.next()) {
				for (int j = 0; j < datecollist.length; j++) {

				if (resultSetFromDb.getString(datecollist[j]) != null
							&& !resultSetFromDb.getString(datecollist[j]).isEmpty()) {
						if ((isvaliddate(resultSetFromDb.getString(datecollist[j]), dateformat)) || (isvaliddate(resultSetFromDb.getString(datecollist[j]), dateformat1))) {
							tableValues[i][j] = resultSetFromDb.getString(datecollist[j]);
						} else {
							tableValues[i][j] = "NA";
						}
					} else {
						tableValues[i][j] = "NA";
					}

				}

				i++;
			}

			// create all possible combinations and start Analyzing relations

			int[] arr = new int[numcol];

			for (int kk = 0; kk < numcol; kk++) {
				arr[kk] = kk;
			}

			// create rules for each columns

			for (int kk = 0; kk < numcol; kk++) {

				List<Double> listdays = new ArrayList<Double>();
				String nullAccepgtable = "N";

				for (int row = 0; row < maxrows - 1; row++) {
					// LOG.debug("Array value: "+row+" number
					// "+x[row][kk]);
					if (!tableValues[row][kk].equals("NA")) {
						
						DateTimeFormatter formatter;
						if(tableValues[row][kk].length() == 8){
							 formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
						}
						else{
							 formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						}
						LocalDate date1 = LocalDate.parse(tableValues[row][kk], formatter);
						LocalDate date2 = LocalDate.now();

						long elapsedDays = ChronoUnit.DAYS.between(date2, date1);

						// LOG.debug("Number of day " + date1+ "
						// "+date2+ " " +elapsedDays );
						listdays.add((double) elapsedDays);

						// list.add(kk);

					} else {
						nullAccepgtable = "Y";
					}
				}

				// Map<String,Object> tableRuleMap = new
				// HashMap<String,Object>();

				List<String> dataArray = new ArrayList<String>();
				// Double[] tableRuleDataArray = new Double[3];

				// rule 1
				Double nullcountpercentage = (double) ((maxrows - listdays.size()) / maxrows);

				// Code-Rule Column 0 can not have NULL or Invalid Date Values

				int isNull;
				isNull = dateNArule(kk, nullcountpercentage);

				// rule 2

				List<String> acceptablerange = new ArrayList<String>();

				acceptablerange = daterules(listdays);
				LOG.debug("Rule 2.2: the currentdate - Column " + kk + " : Minimum value greater than "
						+ acceptablerange.get(0) + " max value : " + acceptablerange.get(1));

				if (nullAccepgtable.equals("Y")) {
					dataArray.add(nullAccepgtable);
					dataArray.add("Null");
					dataArray.add("Null");
					dataArray.add("NA");
				} else {
					dataArray.add(nullAccepgtable);
					dataArray.add(acceptablerange.get(0));
					dataArray.add(acceptablerange.get(1));
					dataArray.add("NA");
				}
				dataRuleMap.put(colName.get(kk), dataArray);

			}

			int n = arr.length;

			String combination = printCombination(arr, n, 2);

			String combinationArray[] = combination.split(",");

			for (int m = 0; m < combinationArray.length; m++) {
				if (combinationArray[m].length() > 3) {
					LOG.debug("Combination" + combinationArray[m]);
					String test1[] = combinationArray[m].trim().split("-");

					// split the data into three groups

					// 1. NA-NA
					// 2. NA - Present
					// 3. Present - NA
					// 4. Present - Present

					int firstcolumn = Integer.parseInt(test1[1]);
					int secondcolumn = Integer.parseInt(test1[2]);

					int bothnullvalues = 0;
					int firstNull = 0;
					int secondNull = 0;
					String nullFlagConbination = "";
					String nullColumnCombination = "";

					List<Double> listdays_2col_1stnull = new ArrayList<Double>();
					List<Double> listdays_1col_2ndnull = new ArrayList<Double>();
					List<Double> listdays_1_2_col = new ArrayList<Double>();

					for (int row = 0; row < maxrows - 1; row++) {
						if (tableValues[row][firstcolumn].equals("NA") && tableValues[row][secondcolumn].equals("NA")) {

							bothnullvalues++;

						}

						if (tableValues[row][firstcolumn].equals("NA") && !tableValues[row][secondcolumn].equals("NA")) {
							firstNull++;
							DateTimeFormatter formatter;
							if(tableValues[row][secondcolumn].length() == 8){
								 formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
							}
							else{
								 formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							}
							LocalDate date1 = LocalDate.parse(tableValues[row][secondcolumn], formatter);
							LocalDate date2 = LocalDate.now();

							long elapsedDays = ChronoUnit.DAYS.between(date2, date1);

							// LOG.debug("Number of day " + date1+ "
							// "+date2+ " " +elapsedDays );
							listdays_2col_1stnull.add((double) elapsedDays);

							// list.add(kk);

						}

						if (!tableValues[row][firstcolumn].equals("NA") && tableValues[row][secondcolumn].equals("NA")) {
							secondNull++;
							DateTimeFormatter formatter;
							if(tableValues[row][firstcolumn].length() == 8){
								 formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
							}
							else{
								 formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							}
							LocalDate date1 = LocalDate.parse(tableValues[row][firstcolumn], formatter);
							LocalDate date2 = LocalDate.now();

							long elapsedDays = ChronoUnit.DAYS.between(date2, date1);

							// LOG.debug("Number of day " + date1+ "
							// "+date2+ " " +elapsedDays );
							listdays_1col_2ndnull.add((double) elapsedDays);

							// list.add(kk);

						}

						if (!tableValues[row][firstcolumn].equals("NA") && !tableValues[row][secondcolumn].equals("NA")) {
							DateTimeFormatter formatter;
							DateTimeFormatter formatter1;
							if(tableValues[row][firstcolumn].length() == 8){
								 formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
							}
							else{
								 formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							}
							
							if(tableValues[row][secondcolumn].length() == 8){
								 formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
							}
							else{
								 formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							}
							LocalDate date1 = LocalDate.parse(tableValues[row][firstcolumn], formatter);
							LocalDate date2 = LocalDate.parse(tableValues[row][secondcolumn], formatter1);

							long elapsedDays = ChronoUnit.DAYS.between(date2, date1);

							// LOG.debug("Number of day " + date1+ "
							// "+date2+ " " +elapsedDays );
							listdays_1_2_col.add((double) elapsedDays);

							// list.add(kk);

						}

					}

					double bothnullpercentage = (double) bothnullvalues / maxrows;

					dateNA1rule(firstcolumn, secondcolumn, bothnullpercentage);

					List<String> acceptablerange = new ArrayList<String>();

					if (firstNull != 0) {
						nullColumnCombination = colName.get(firstcolumn);
						acceptablerange = daterules(listdays_2col_1stnull);
						LOG.debug("Rule 3.2: When the Column " + firstcolumn + " is Null then column:"
								+ secondcolumn + " has Minimum value greater than " + acceptablerange.get(0)
								+ " max value : " + acceptablerange.get(1));
					}
					if (secondNull != 0) {
						nullColumnCombination = colName.get(secondcolumn);
						acceptablerange = daterules(listdays_1col_2ndnull);
						LOG.debug("Rule 3.2: When the Column " + secondcolumn + " is Null then column:"
								+ firstcolumn + " has Minimum value greater than " + acceptablerange.get(0)
								+ " max value : " + acceptablerange.get(1));
					}
					if (firstNull == 0 && secondNull == 0) {
						nullColumnCombination = "NA";
						acceptablerange = daterules(listdays_1_2_col);
						LOG.debug("Rule 2.2: the difference column : " + secondcolumn + " and Column : "
								+ firstcolumn + " : has Minimum value greater than " + acceptablerange.get(0)
								+ " max value : " + acceptablerange.get(1));
					}
					List<String> dataArrayCombination = new ArrayList<String>();
					if (firstNull != 0 && secondNull != 0) {
						nullFlagConbination = "NA";
						nullColumnCombination = colName.get(firstcolumn) + "," + colName.get(secondcolumn);
						dataArrayCombination.add(nullFlagConbination);
						dataArrayCombination.add("Null");
						dataArrayCombination.add("Null");
						dataArrayCombination.add(nullColumnCombination);
					} else {

						dataArrayCombination.add("NA");
						dataArrayCombination.add(acceptablerange.get(0));
						dataArrayCombination.add(acceptablerange.get(1));
						dataArrayCombination.add(nullColumnCombination);
					}
					dataRuleMap.put(colName.get(firstcolumn) + "," + colName.get(secondcolumn), dataArrayCombination);
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		 Map<String, Object> dataRuleMapList = new HashMap<String, Object>();
			for (Map.Entry<String,List<String>> entry : dataRuleMap.entrySet()) {
				DateRuleMap DateRuleMap = new DateRuleMap();
				
				DateRuleMap.setNAAcceptable(entry.getValue().get(0));
				DateRuleMap.setMinAcceptable(entry.getValue().get(1));
				DateRuleMap.setMaxAcceptable(entry.getValue().get(2));
				DateRuleMap.setNullColumn(entry.getValue().get(3));
				dataRuleMapList.put(entry.getKey(), DateRuleMap);
				
			}
			
			LOG.debug(dataRuleMapList.size());
			session.setAttribute("dataRuleMap", dataRuleMapList);
			
		
		return dataRuleMap;

	}

	
	//Supporting Methods..
	public static int dateNArule(int columnind, double nullpercentage) {

		if (nullpercentage < 0.005) {
			LOG.debug("Column " + columnind + " can not have NULL or Invalid Date Values");
			return 0;
		}
		return 1;

	}

	public static void dateNA1rule(int columnind1, int columnind2, double nullpercentage) {

		if (nullpercentage < 0.005) {
			LOG.debug("Column: " + columnind1 + " and Column: " + columnind2
					+ " Both can not have NULL or Invalid Date Values");
		}

	}

	public static List<String> daterules(List<Double> listdays) {

		String dateformat = "yyyyMMdd";
		List<String> acceptablerange = new ArrayList<String>();
		double[] arrayToSume = ArrayUtils.toPrimitive(listdays.toArray(new Double[listdays.size()]));

		int min = (int) StatUtils.min(arrayToSume);
		int max = (int) StatUtils.max(arrayToSume);
		Double mean = StatUtils.mean(arrayToSume);
		Double stdev = Math.sqrt(StatUtils.variance(arrayToSume));

		LOG.debug(
				" Min : " + min + " , " + " Max : " + max + " , " + " avg : " + mean + " , " + " stdev : " + stdev);

		int minaccetablevalue = (int) (min - (int) 3 * stdev);
		int maxaccetablevalue = (int) (max + (int) 3 * stdev);

		Calendar minDate = Calendar.getInstance();
		minDate.set(Calendar.DAY_OF_YEAR, minaccetablevalue);

		Calendar maxDate = Calendar.getInstance();
		maxDate.set(Calendar.DAY_OF_YEAR, maxaccetablevalue);

		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String minAccetablevalueDate = format.format(minDate.getTime());
	    String maxAccetablevalueDate = format.format(maxDate.getTime());
	    LOG.debug(maxAccetablevalueDate);
		LOG.debug(maxAccetablevalueDate);

		if (max < 0) {

			maxaccetablevalue = -1;

		}
		if (max == 0) {

			maxaccetablevalue = 0;

		}

		if (min > 0) {

			minaccetablevalue = 1;

		}
		if (min == 0) {

			minaccetablevalue = 0;

		}

		acceptablerange.add(minAccetablevalueDate);
		acceptablerange.add(maxAccetablevalueDate);

		return acceptablerange;

	}

	public static String combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
		// Current combination is ready to be printed, print it

		String tmp1 = "";

		if (index == r) {
			String tmp = "";
			for (int j = 0; j < r; j++) {
				// System.out.print(data[j]+"-");
				tmp = tmp + "-" + data[j];
			}

			return tmp;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = arr[i];

			tmp1 = tmp1 + "," + combinationUtil(arr, data, i + 1, end, index + 1, r);
		}

		return tmp1;
	}

	// The main function that prints all combinations of size r
	// in arr[] of size n. This function mainly uses combinationUtil()
	public static String printCombination(int arr[], int n, int r) {
		// A temporary array to store all combination one by one
		int data[] = new int[r];

		// Print all combination using temprary array 'data[]'
		return combinationUtil(arr, data, 0, n - 1, 0, r);
	}*/

	public static boolean isvaliddate(String s, String fmt) {

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
			LocalDate date = LocalDate.parse(s, formatter);

			return true;
		} catch (DateTimeParseException exc) {
			LOG.error(exc.getMessage());

			return false;
		}

	}

}
