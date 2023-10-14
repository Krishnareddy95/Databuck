package com.databuck.dao;

import java.util.List;
import java.util.Map;

import com.databuck.bean.ListDfTranRule;

public interface JsonDaoI {
	public Map<String, String> getDateAndRunForSummaryOfLastRun(Long idApp);

	public double getDQIByCheckName(Long idApp, String date, String run, String testName);

	public double getAggregateDQIForDataQualityDashboard(Long idApp, String date, String run);

	public List<ListDfTranRule> getDataFromListDfTranRule(Long idApp);
}
