package com.databuck.util;

import com.databuck.integration.IntegrationMasterService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.apache.log4j.Logger;

public class DebugUtil {
	private static final Logger LOG = Logger.getLogger(DebugUtil.class);

    public static String getInitialExceptionDetails(Exception e){
        //System.out.println("Inside get mapping error");
        String mappingErrors= "";
        try {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            String[] lines= stacktrace.split("\n");
            int lineLimit= lines.length <=2?lines.length:2;
            for(int i=0;i<lineLimit;i++) {
                String line = lines[i];
                mappingErrors = mappingErrors + line+"\n";
            }
        }catch (Exception ex){
        	LOG.error(ex.getMessage());
            ex.printStackTrace();
        }
        return mappingErrors;
    }
}
