package com.databuck.service.impl;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.IDataQualityResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.databuck.dao.IResultsDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.apache.log4j.Logger;

@Service
public class DataQualityResultsService implements IDataQualityResultsService {

    @Autowired
    private IValidationCheckDAO validationCheckDao;

    @Autowired
    private IResultsDAO resultsDAO;
    
    private static final Logger LOG = Logger.getLogger(DataQualityResultsService.class);

    @Override
    public boolean forgotSelectedRunOfValidation(long idApp,String curExecDate,long currExecRun,String checkValue){
        LOG.info("\n=======> forgot/Unforgot SelectedRunOfValidation - START <=======");
        boolean forgotRunStatus = false;
        boolean rollback = false;
        try{
            LOG.debug("\n====> forgot run: " + checkValue);
            LOG.debug("\n====> Execution Date: " + curExecDate);
            LOG.debug("\n====> Execution run: " + currExecRun);

            List<String> resultTableList = getDataQualityResultsTableList();
            List<String> updatedTablesList = new ArrayList<String>();

            //update forgot_run_enabled column for all results table
            for(String tableName : resultTableList) {
                updatedTablesList.add(tableName);

                forgotRunStatus = validationCheckDao.updateForgotRunStatusOfValidationLatestRun(idApp, curExecDate,
                        currExecRun, checkValue, tableName);
                if (!forgotRunStatus) {
                    rollback = true;
                    break;
                }
            }

            //rollback logic
            if(rollback){
                forgotRunStatus = false;
                LOG.info("\n====> Failed to update some tables, Preparing to rollback transaction ...");
                if(checkValue !=null && checkValue.equalsIgnoreCase("Y"))
                    checkValue ="N";
                else
                    checkValue ="Y";
                for(String tableName : updatedTablesList) {
                    forgotRunStatus = validationCheckDao.updateForgotRunStatusOfValidationLatestRun(idApp,curExecDate,
                            currExecRun,checkValue,tableName);

                }
            }

        }catch (Exception e) {
        	LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return forgotRunStatus;
    }
    private List<String> getDataQualityResultsTableList(){
        List<String> resultTableList = new ArrayList<String>();
        //DATA_QUALITY_DateRule_Reference rule is not included since it Date and Run column names are not there in actual table
        resultTableList.add("DATA_QUALITY_Transactionset_sum_A1");
        resultTableList.add("DATA_QUALITY_Transactionset_sum_dgroup");
        resultTableList.add("DATA_QUALITY_NullCheck_Summary");
        resultTableList.add("DATA_QUALITY_Record_Anomaly");
        resultTableList.add("DATA_QUALITY_default_value");
        resultTableList.add("DATA_QUALITY_badData");
        resultTableList.add("DATA_QUALITY_Length_Check");
        resultTableList.add("DATA_QUALITY_DATA_DRIFT");
        resultTableList.add("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY");
        resultTableList.add("DATA_QUALITY_DATA_DRIFT_SUMMARY");
        resultTableList.add("DATA_QUALITY_Transaction_Detail_Identity");
        resultTableList.add("DATA_QUALITY_Transaction_Detail_All");
        resultTableList.add("DATA_QUALITY_Transaction_Summary");
        resultTableList.add("DATA_QUALITY_Unmatched_Pattern_Data");
        resultTableList.add("DATA_QUALITY_Unmatched_Default_Pattern_Data");
        resultTableList.add("DATA_QUALITY_Rules");
        resultTableList.add("DATA_QUALITY_timeliness_check");
        resultTableList.add("DATA_QUALITY_Column_Summary");
        resultTableList.add("DATA_QUALITY_DateRule_FailedRecords");
        resultTableList.add("DATA_QUALITY_DateRule_Summary");
        resultTableList.add("DATA_QUALITY_GlobalRules");
        resultTableList.add("DATA_QUALITY_History_Anomaly");
        resultTableList.add("data_quality_sql_rules");
        resultTableList.add("processData");
        return resultTableList;
    }

}
