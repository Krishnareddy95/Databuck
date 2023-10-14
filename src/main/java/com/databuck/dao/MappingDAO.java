package com.databuck.dao;

import com.databuck.bean.MappingDetail;

import java.util.List;
import java.util.Map;

public interface MappingDAO {
	public List<MappingDetail> getMappingEntries(String refMappingTableName, String dataTableName);

	public Map<String, String> getMatchedLeftColumnMappingDetails(String refMappingTableName, String dataTableName, String microsegColsStr);

	public Map<String, String> getUnmatchedLeftColumnMappingDetails(String refMappingTableName, String dataTableName, String microsegColsStr);

	public Map<String, String> getCompulsoryColumnMappingDetails(String refMappingTableName, String dataTableName);

	public List<String> getHeadersForMappingDetails(String refMappingTableName, String dataTableName);

}