package com.databuck.bean;

import com.databuck.bean.DBKFileMonitoringRules;
import java.util.List;

public class FMUpdateReq {
	int idApp;
	List<DBKFileMonitoringRules> dataList;
	
	
	
	public int getIdApp() {
		return idApp;
	}
	public void setIdApp(int idApp) {
		this.idApp = idApp;
	}
	public List<DBKFileMonitoringRules> getDataList() {
		return dataList;
	}
	public void setDataList(List<DBKFileMonitoringRules> dataList) {
		this.dataList = dataList;
	}
}
