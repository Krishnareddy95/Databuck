package com.databuck.dao;

import com.databuck.bean.CustomMicrosegment;

import java.util.List;

public interface CustomMicrosegmentDao {
	// Dao method to fetch all rows from table 'custom_microsegment_details'
	public List<CustomMicrosegment> getCustomMicroSegmentsByIdData(long idData);

	// Dao method to getColumnNames
	public List<String> getCustomMicroSegmentsColumnNamesForCheck(long idData, String checkName);

	// Dao method to add row to table 'custom_microsegment_details'
	public boolean addCustomMicrosegments(CustomMicrosegment customMicrosegment);

	// Dao method to get microsegments as comma seperated list
	public boolean isDuplicateCustomMicrosegments(CustomMicrosegment customMicrosegment);

	public boolean deleteCustomMicrosegments(long microsegmentRowId);
}
