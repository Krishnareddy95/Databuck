package com.databuck.service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.NumericalAnalysisData;
import com.databuck.bean.StringAnalysisData;

public interface IDataAlgorithService {

	
	Map generateAnalysisData(ResultSet resultSetFromDb, List<String> primaryKeys, HttpSession session,
			String selTableName) throws Exception;
	
	Map populateTableAnalysisDataForParquet(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, String dataLocation, String hostURI, String folder, String userLogin,
			String password, String dataFormat) throws Exception;
	
	public Map populateTableAnalysisDataForS3(HttpSession session,  String dataLocation, 
			Map<String, String> columnMetaData, MultipartFile file, String hostURI, String folder, String userLogin, String password, String dataFormat);
	
	public void refineGroupByColumns( Map<String, NumericalAnalysisData> numericalData, Map<String, StringAnalysisData> stringData,
			 List<ListDataDefinition> lstDataDefinition);
	
	public List<String> getTableColumns(@RequestParam Long idDataSchema, @RequestParam String dataLocation,
			@RequestParam String tableName, String queryString, String isQuery, HttpSession session);
	
	public Map<String, String> getTableColumnsForFile(MultipartFile file);
	
}
