package com.databuck.dao;

import java.util.List;

import com.databuck.csvmodel.NullCheck;

public interface IEssentialChecksRepo {
	public List<NullCheck> getNullChecks(Long idApp, String fromDate, String toDate, String checkName, String tableName) throws Exception;

}
