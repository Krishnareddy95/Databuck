package com.databuck.filemonitoring;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.dao.FileMonitorDao;

@Service
public class FileMonitoringUtilService{
	
	@Autowired
	private FileMonitorDao fileMonitorDao;
	
    public static String getUtcDate(OffsetDateTime current_utc_time) {
        String currentUtcDate = null;
        try {
            String current_month =null;
            String currentDayofMonth = null;
            String year = "" + current_utc_time.getYear();
            int month = current_utc_time.getMonthValue();
            if (month > 9)
                current_month = "" + month;
            else
                current_month = "0" + month;

            int dayofMonth = current_utc_time.getDayOfMonth();

            if (dayofMonth > 9)
            	currentDayofMonth = "" + dayofMonth;
            else
            	currentDayofMonth = "0" + dayofMonth;


            currentUtcDate = year + "-" + current_month + "-" + currentDayofMonth;

        } catch (Exception e){
            e.printStackTrace();
        }
        return currentUtcDate;
    }
}