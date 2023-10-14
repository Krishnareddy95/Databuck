package com.databuck.constants;

import com.databuck.config.DatabuckEnv;

public class ResultTableConstants {

    public static final String IF_NULL_FUNCTION = (DatabuckEnv.DB_TYPE
	    .equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "COALESCE" : "ifnull";
    public static final String BINARY_KEY = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
	    ? ""
	    : "BINARY";
    public static final String DATA_DRIFT_COUNT_SUMMERY_TABLE = "DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY";
    
    public static final String DISTRIBUTION_RESULT_SUMMRY = "DATA_QUALITY_Column_Summary";

}
