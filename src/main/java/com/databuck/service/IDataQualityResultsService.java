package com.databuck.service;

public interface IDataQualityResultsService {
    public boolean forgotSelectedRunOfValidation(long idApp,String maxExecDate,long maxExecRun,String checkValue);
}