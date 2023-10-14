package com.databuck.service;

import java.util.List;

import com.databuck.bean.ListDataSource;

public interface IDashBoardService {
	public List<ListDataSource> getDataFromDataSources(Long idDataSchema );
}
