package com.databuck.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtility {
	
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
	
	public static <T> int calculateNumberOfUniqueValues(List<String> strList){
		List<String> uniqueValuesList = new ArrayList<String>();
		
		for(String strVal: strList){
			if(!uniqueValuesList.contains(strVal)){
				uniqueValuesList.add(strVal);
			}
		}
		
		return uniqueValuesList.size();
		
	}
	
	
	public static <T> double calculatePercentOfUniqueValues(List<String> strList){
		List<String> uniqueValuesList = new ArrayList<String>();
		
		for(String strVal: strList){
			if(!uniqueValuesList.contains(strVal)){
				uniqueValuesList.add(strVal);
			}
		}
		
		return ((1.0 * uniqueValuesList.size())/strList.size()) * 100;
		
	}
	
	public static int calculateMinLength(List<String> strList){
		int minLength = 99999999;
		for(String str: strList){
			if(str != null && str.length() < minLength){
				minLength = str.length();
			}
		}
		
		return minLength;
	}
	
	public static int calculateMaxLength(List<String> strList){
		int maxLength = 0;
		for(String str: strList){
			if(str != null && str.length() > maxLength){
				maxLength = str.length();
			}
		}
		
		return maxLength;
	}
}
