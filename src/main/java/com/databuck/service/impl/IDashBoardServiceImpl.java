package com.databuck.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ListDataSource;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.service.IDashBoardService;
@Repository
public class IDashBoardServiceImpl implements IDashBoardService  {
	@Autowired
	public IListDataSourceDAO iListDataSourceDAO;
	
	public List<ListDataSource> getDataFromDataSources(Long idDataSchema )
	{
		return iListDataSourceDAO.getListDataSource(idDataSchema);
	}
}
