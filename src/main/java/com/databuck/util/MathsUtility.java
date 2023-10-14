package com.databuck.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathsUtility {

	public static double calculateAverage(List<Double> numberList) {
	    double sum = 0;
	    for (Double mark : numberList) {
	        sum += mark;
	    }
	    return numberList.isEmpty()? 0: 1.0*sum/numberList.size();
	}
	
	public static double calculateMedian(List<Double> numberList) {
	    Collections.sort(numberList);
	    if(numberList.size()%2 == 1){
	    	int middlePosition = numberList.size()/2 ;
	    	return numberList.get(middlePosition);
	    }else{
	    	Double firstNumber = numberList.get(numberList.size()/2 - 1);
	    	Double secondNumber = numberList.get(numberList.size()/2);
	    	return (firstNumber + secondNumber)/2;
	    }
	}
	
	public static <T> double calculatePercentOfNullValues(List<T> numberList){
		int numberOfNullValues = 0;
		int totalValues = 0;
		for(T singleVal: numberList){
			if((Double)singleVal == 0.0 ){
				numberOfNullValues++;
			}
			totalValues++;
		}
		
		return ((1.0 * numberOfNullValues)/totalValues) * 100;
	}
	
	public static <T> double calculatePercentOfUniqueValues(List<T> numberList){
		List<T> uniqueValuesList = new ArrayList<T>();
		
		for(T numVal: numberList){
			if(!uniqueValuesList.contains(numVal)){
				uniqueValuesList.add(numVal);
			}
		}
		
		return ((1.0 * uniqueValuesList.size())/numberList.size()) * 100;		
	}
}
