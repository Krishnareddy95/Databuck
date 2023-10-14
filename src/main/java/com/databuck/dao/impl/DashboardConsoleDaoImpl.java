package com.databuck.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DashboardCheckComponent;
import com.databuck.bean.DashboardColorGrade;
import com.databuck.bean.DashboardConnection;
import com.databuck.bean.DashboardConnectionValidion;
import com.databuck.bean.DashboardTableCount;
import com.databuck.bean.EssentialCheckRuleSummaryReport;
import com.databuck.bean.EssentialCheckSummaryReport;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ReportUIDQIIndexHistory;
import com.databuck.bean.ReportUIDashboardSummary;
import com.databuck.bean.ReportUIFailedAsset;
import com.databuck.bean.ReportUIFailedFilesSummary;
import com.databuck.bean.ReportUIOverallDQIIndex;
import com.databuck.bean.ReportUIPerformanceSummary;
import com.databuck.bean.ReportUIProjectCoverage;
import com.databuck.bean.ReportUISchemaCoverage;
import com.databuck.bean.ReportUITableCoverage;
import com.databuck.bean.ReportUITableSummary;
import com.databuck.bean.UserToken;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dto.DashboardTableCountSummary;

@Repository
public class DashboardConsoleDaoImpl implements IDashboardConsoleDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private IProjectDAO iProjectDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDao;

	private static final Logger LOG = Logger.getLogger(DashboardConsoleDaoImpl.class);

	@Override
	public List<DashboardConnection> getConnectionsForDashboard(long domainId, long projectId) {
		List<DashboardConnection> dashboardConnectionList = null;

		try {
			String sql = "select t1.idDataSchema,t1.schemaName,t1.domain_id as domainId,t1.project_id,t2.displayName,t2.displayOrder,t2.enabled from listDataSchema t1 "
					+ " left outer join (select 'true' as enabled, dpc.* from dashboard_project_conn_list dpc) t2 on "
					+ " t1.project_id=t2.projectId and t1.idDataSchema=t2.connectionId where t1.Action='Yes' and t1.domain_id=? and t1.project_id=?";

			dashboardConnectionList = jdbcTemplate.query(sql, new RowMapper<DashboardConnection>() {
				@Override
				public DashboardConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardConnection dashboardConnection = new DashboardConnection();
					dashboardConnection.setDomainId(rs.getLong("domainId"));
					dashboardConnection.setProjectId(rs.getLong("project_id"));
					dashboardConnection.setConnectionId(rs.getLong("idDataSchema"));
					dashboardConnection.setConnectionName(rs.getString("schemaName"));
					dashboardConnection.setDisplayName(rs.getString("displayName"));
					dashboardConnection.setDisplayOrder(rs.getInt("displayOrder"));
					dashboardConnection.setEnabled(rs.getBoolean("enabled"));
					return dashboardConnection;
				}

			}, domainId, projectId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardConnectionList;
	}

	@Override
	public String updateConnectionsForDashboard(List<DashboardConnection> dashboardConnectionList, long domainId,
			long projectId) {
		String status = "failed";
		try {
			// delete all the associations of this project
			String deleteSql = "delete from dashboard_conn_app_list where domainId=? and projectId=?";
			jdbcTemplate.update(deleteSql, domainId, projectId);

			// delete existing DashboardConnection mapping
			deleteSql = "delete from dashboard_project_conn_list where domainId=? and projectId=?";
			jdbcTemplate.update(deleteSql, domainId, projectId);

			if (dashboardConnectionList != null && dashboardConnectionList.size() > 0) {
				for (DashboardConnection dashboardConnection : dashboardConnectionList) {
					String sql = "insert into dashboard_project_conn_list(domainId,projectId,connectionId,displayName,displayOrder) values(?,?,?,?,?)";
					jdbcTemplate.update(sql, dashboardConnection.getDomainId(), dashboardConnection.getProjectId(),
							dashboardConnection.getConnectionId(), dashboardConnection.getDisplayName(),
							dashboardConnection.getDisplayOrder());
				}
			}
			status = "passed";
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<DashboardColorGrade> getColorGrading(long domainId, long projectId) {

		List<DashboardColorGrade> dashboardColorGradeList = null;

		try {
			String countSql = "select count(*) from dashboard_project_color_grade where domainId=? and projectId=?";
			int count = jdbcTemplate.queryForObject(countSql, Integer.class, domainId, projectId);

			if (count == 0) {
				// Insert grading mapping for project
				addColorGrading(domainId, projectId);
			}

			String sql = "select gradeId,domainId,projectId,color,logic,color_percentage from dashboard_project_color_grade where domainId=? and projectId=?";

			dashboardColorGradeList = jdbcTemplate.query(sql, new RowMapper<DashboardColorGrade>() {
				@Override
				public DashboardColorGrade mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardColorGrade dashboardColorGrade = new DashboardColorGrade();
					dashboardColorGrade.setGradeId(rs.getLong("gradeId"));
					dashboardColorGrade.setDomainId(rs.getLong("domainId"));
					dashboardColorGrade.setProjectId(rs.getLong("projectId"));
					dashboardColorGrade.setColor(rs.getString("color"));
					dashboardColorGrade.setLogic(rs.getString("logic"));
					dashboardColorGrade.setColorPercentage(rs.getDouble("color_percentage"));
					return dashboardColorGrade;
				}

			}, domainId, projectId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardColorGradeList;
	}

	@Override
	public void addColorGrading(long domainId, long projectId) {
		try {
			List<DashboardColorGrade> dashboardColorGradeList = new ArrayList<DashboardColorGrade>();

			DashboardColorGrade redColorGrade = new DashboardColorGrade();
			redColorGrade.setDomainId(domainId);
			redColorGrade.setProjectId(projectId);
			redColorGrade.setColor("RED");
			redColorGrade.setLogic("less than");
			redColorGrade.setColorPercentage(90);
			dashboardColorGradeList.add(redColorGrade);

			DashboardColorGrade yellowColorGrade = new DashboardColorGrade();
			yellowColorGrade.setDomainId(domainId);
			yellowColorGrade.setProjectId(projectId);
			yellowColorGrade.setColor("YELLOW");
			yellowColorGrade.setLogic("less than");
			yellowColorGrade.setColorPercentage(95);
			dashboardColorGradeList.add(yellowColorGrade);

			DashboardColorGrade greenColorGrade = new DashboardColorGrade();
			greenColorGrade.setDomainId(domainId);
			greenColorGrade.setProjectId(projectId);
			greenColorGrade.setColor("GREEN");
			greenColorGrade.setLogic("greater than equal to");
			greenColorGrade.setColorPercentage(95);
			dashboardColorGradeList.add(greenColorGrade);

			for (DashboardColorGrade dashboardColorGrade : dashboardColorGradeList) {
				String sql = "insert into dashboard_project_color_grade(domainId,projectId,color,logic,color_percentage) values(?,?,?,?,?)";
				jdbcTemplate.update(sql, domainId, dashboardColorGrade.getProjectId(), dashboardColorGrade.getColor(),
						dashboardColorGrade.getLogic(), dashboardColorGrade.getColorPercentage());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String updateColorGrading(List<DashboardColorGrade> dashboardColorGradeList) {
		String status = "failed";
		try {
			for (DashboardColorGrade dashboardColorGrade : dashboardColorGradeList) {
				String sql = "update dashboard_project_color_grade set color_percentage=? where gradeId=? and domainId=? and projectId=?";
				jdbcTemplate.update(sql, dashboardColorGrade.getColorPercentage(), dashboardColorGrade.getGradeId(),
						dashboardColorGrade.getDomainId(), dashboardColorGrade.getProjectId());
			}
			status = "passed";
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<DashboardCheckComponent> getCheckComponentList() {
		List<DashboardCheckComponent> dashboardCheckComponentList = null;
		try {
			String sql = "Select componentId, checkName, description, component, entity_name, technical_name, technical_check_value, technical_result_name from dashboard_check_component_list";
			dashboardCheckComponentList = jdbcTemplate.query(sql, new RowMapper<DashboardCheckComponent>() {
				@Override
				public DashboardCheckComponent mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardCheckComponent dashboardCheckComponent = new DashboardCheckComponent();
					dashboardCheckComponent.setComponentId(rs.getLong("componentId"));
					dashboardCheckComponent.setCheckName(rs.getString("checkName"));
					dashboardCheckComponent.setDescription(rs.getString("description"));
					dashboardCheckComponent.setComponent(rs.getString("component"));
					dashboardCheckComponent.setEntityName(rs.getString("entity_name"));
					dashboardCheckComponent.setTechnicalName(rs.getString("technical_name"));
					dashboardCheckComponent.setTechnicalCheckValue(rs.getString("technical_check_value"));
					dashboardCheckComponent.setTechnicalResultName(rs.getString("technical_result_name"));
					return dashboardCheckComponent;
				}

			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardCheckComponentList;
	}

	@Override
	public List<DashboardConnectionValidion> getConnectionValidtionMap(long domainId, long projectId,
			long connectionId) {
		List<DashboardConnectionValidion> dashboardConnectionValidionList = null;
		try {
			String sql = "select m2.conn_app_id, m1.*, m2.datasource,m2.source,m2.fileName from "
					+ " (select t1.domain_id as domainId, t1.project_id as projectId,t2.idDataSchema as connectionId,t1.idApp,t1.name as validationName ,"
					+ " t2.name as templateName from listApplications t1 join listDataSources t2 on t1.idData=t2.idData where "
					+ " t1.domain_id=? and t1.project_id=? and t2.idDataSchema=?) m1 left outer join dashboard_conn_app_list m2 on "
					+ " m1.connectionId=m2.connectionId and m1.idApp=m2.idApp and m1.domainId=m2.domainId and m1.projectId=m2.projectId";

			dashboardConnectionValidionList = jdbcTemplate.query(sql, new RowMapper<DashboardConnectionValidion>() {
				@Override
				public DashboardConnectionValidion mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardConnectionValidion dashboardConnectionValidion = new DashboardConnectionValidion();
					dashboardConnectionValidion.setConAppId(rs.getLong("conn_app_id"));
					dashboardConnectionValidion.setDomainId(rs.getLong("domainId"));
					dashboardConnectionValidion.setProjectId(rs.getLong("projectId"));
					dashboardConnectionValidion.setConnectionId(rs.getLong("connectionId"));
					dashboardConnectionValidion.setIdApp(rs.getLong("idApp"));
					dashboardConnectionValidion.setValidationName(rs.getString("validationName"));
					dashboardConnectionValidion.setTemplateName(rs.getString("templateName"));
					dashboardConnectionValidion.setDatasource(rs.getString("datasource"));
					dashboardConnectionValidion.setSource(rs.getString("source"));
					dashboardConnectionValidion.setFileName(rs.getString("fileName"));
					return dashboardConnectionValidion;
				}

			}, domainId, projectId, connectionId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardConnectionValidionList;
	}

	@Override
	public String updateConnectionValidtionMap(List<DashboardConnectionValidion> dashboardConnectionValidionList) {
		String status = "failed";
		try {
			for (DashboardConnectionValidion dsConnVal : dashboardConnectionValidionList) {

				// delete the connection mapping
				String sql = "";
				if (dsConnVal.getConAppId() != null && dsConnVal.getConAppId() != 0l) {
					sql = "delete from dashboard_conn_app_list where conn_app_id=?";
					jdbcTemplate.update(sql, dsConnVal.getConAppId());
				}

				// delete if same mapping exist
				sql = "delete from dashboard_conn_app_list where domainId=? and projectId=? and connectionId=? and idApp=?";
				jdbcTemplate.update(sql, dsConnVal.getDomainId(), dsConnVal.getProjectId(), dsConnVal.getConnectionId(),
						dsConnVal.getIdApp());

				// Insert the mapping into table
				sql = "insert into dashboard_conn_app_list(domainId,projectId,connectionId,idApp,datasource,source,fileName) values(?,?,?,?,?,?,?)";
				jdbcTemplate.update(sql, dsConnVal.getDomainId(), dsConnVal.getProjectId(), dsConnVal.getConnectionId(),
						dsConnVal.getIdApp(), dsConnVal.getDatasource(), dsConnVal.getSource(),
						dsConnVal.getFileName());

			}
			status = "passed";
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public void insertUserToken(UserToken userToken) {
		try {
			String sql = "INSERT into user_token(idUser,userName,email,userRole,userRoleName,loginTime,expiryTime,token,refreshtoken,status,activeDirectoryUser,user_ldap_groups) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql, userToken.getIdUser(), userToken.getUserName(), userToken.getEmail(),
					userToken.getIdRole(), userToken.getRolename(), userToken.getLoginTime(), userToken.getExpiryTime(),
					userToken.getToken(), userToken.getRefreshtoken(), userToken.getTokenStatus(),
					userToken.getActiveDirectoryUser(), userToken.getUserLDAPGroups());

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public UserToken getUserDetailsOfToken(String token) {
		UserToken userToken = null;
		try {
			String sql = "SELECT * from user_token WHERE token = ?";
			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, token);
			while (rs.next()) {
				userToken = new UserToken();
				userToken.setIdUser(Long.parseLong(rs.getString("idUser")));
				userToken.setUserName(rs.getString("userName"));
				userToken.setEmail(rs.getString("email"));
				userToken.setIdRole(rs.getLong("userRole"));
				userToken.setRolename(rs.getString("userRoleName"));
				userToken.setLoginTime(rs.getDate("loginTime"));
				userToken.setExpiryTime(rs.getDate("expiryTime"));
				userToken.setToken(rs.getString("token"));
				userToken.setTokenStatus(rs.getString("status"));
				userToken.setActiveDirectoryUser(rs.getString("activeDirectoryUser"));
				userToken.setUserLDAPGroups(rs.getString("user_ldap_groups"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return userToken;
	}

	public String getUserTokenStatus(String token) {
		String status = "";
		try {
			String sql = "SELECT status from user_token WHERE token = ?";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, token);
			while (queryForRowSet.next()) {
				status = queryForRowSet.getString("status");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public void updateUserTokenStatus(String token, String status) {
		try {
			String sql = "UPDATE user_token SET status = ? WHERE token = ?";
			jdbcTemplate.update(sql, new Object[] { status, token });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public UserToken extendTokenValidity(String refreshtoken,String ExpiryTime) {
		UserToken usertoken = null;				
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			Date oldExpiryTime = dateFormat.parse(ExpiryTime);
			Date newExpiryTime =new Date(oldExpiryTime.getTime() + (8 * 60 * 60 * 1000));
			String sql = "";
			 sql = "update user_token set expiryTime=? where refreshtoken= ?";
			int update = jdbcTemplate.update(sql,new Object[] { newExpiryTime, refreshtoken });
			if(update>0) {
				usertoken=new UserToken();
				usertoken.setRefreshtoken(refreshtoken);
				usertoken.setExpiryTime(newExpiryTime);
				return usertoken;
			}else {
				return usertoken;
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return usertoken;
		}
		
	}

	@Override
	public String checkForExistingUserToken(Long idUser, String email, String activeDirectoryUser) {
		String token = "";
		try {
			String sql = "SELECT token from user_token WHERE  idUser =? and email = ? and activeDirectoryUser =? and status='ACTIVE'";

			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, idUser, email, activeDirectoryUser);
			while (rs.next()) {
				token = rs.getString("token");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return token;
	}

	@Override
	public UserToken checkForExistingUserTokenAndRefreshToken(Long idUser, String email, String activeDirectoryUser) {
		String token = "", refreshtoken = "";
		UserToken usertoken = new UserToken();
		try {
			String sql = "SELECT token,refreshtoken,expiryTime from user_token WHERE  idUser =? and email = ? and activeDirectoryUser =? and status='ACTIVE'";

			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, idUser, email, activeDirectoryUser);
			while (rs.next()) {
				usertoken.setToken(rs.getString("token"));
				usertoken.setRefreshtoken(rs.getString("refreshtoken"));
				usertoken.setExpiryTime(rs.getDate("expiryTime"));

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return usertoken;
	}

	@Override
	public List<DashboardConnection> getEnabledConnectionsForDashboard(long domainId, long projectId) {
		List<DashboardConnection> dashboardConnectionList = null;

		try {
			String sql = "select t1.idDataSchema,t1.schemaName,t1.domain_id as domainId, t1.project_id,t2.displayName,t2.displayOrder,t2.enabled from listDataSchema t1 "
					+ " join (select 'true' as enabled, dpc.* from dashboard_project_conn_list dpc) t2 on t1.domain_id = t2.domainId and "
					+ " t1.project_id=t2.projectId and t1.idDataSchema=t2.connectionId where t1.Action='Yes' and t1.domain_id=? and t1.project_id=?";

			dashboardConnectionList = jdbcTemplate.query(sql, new RowMapper<DashboardConnection>() {
				@Override
				public DashboardConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardConnection dashboardConnection = new DashboardConnection();
					dashboardConnection.setDomainId(rs.getLong("domainId"));
					dashboardConnection.setProjectId(rs.getLong("project_id"));
					dashboardConnection.setConnectionId(rs.getLong("idDataSchema"));
					dashboardConnection.setConnectionName(rs.getString("schemaName"));
					dashboardConnection.setDisplayName(rs.getString("displayName"));
					dashboardConnection.setDisplayOrder(rs.getInt("displayOrder"));
					dashboardConnection.setEnabled(rs.getBoolean("enabled"));
					return dashboardConnection;
				}

			}, domainId, projectId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardConnectionList;
	}

	@Override
	public ReportUIDashboardSummary getDashboardSummaryForConnection(long domainId, long projectId,
			DashboardConnection dashboardConnection, String startDate, String endDate) {

		ReportUIDashboardSummary reportUIDashboardSummary = new ReportUIDashboardSummary();

		try {
			long connectionId = dashboardConnection.getConnectionId();

			// Get the DataQuality listOfValidations linked to connections
			// TODO: For all kinds of validations
			List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
					"Data Forensics");

			long totalCount = 0l;
			long passedCount = 0l;
			long failedCount = 0l;
			Double passPercentage = 0.0;
			Double failPercentage = 0.0;

			if (dashValidationList != null && dashValidationList.size() > 0) {
				String idAppListStr = "";
				for (Long idApp : dashValidationList) {
					if (idApp != null && idApp != 0l) {
						idAppListStr = idAppListStr + idApp + ",";
					}
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				// Checking for DataQuality validations
				String sql = "select sum(m4.DQI=100) as passCount, sum(m4.DQI<100) as failCount from (select m3.AppId,m3.date,m3.Run, Avg(m3.DQI) as DQI from (select m2.AppId, m2.date, m2.Run,case when (m1.DQI IS NULL) then 0 else m1.DQI end as DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where  AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.DQI is not null) m3 group by m3.AppId,m3.date,m3.Run) m4;";

				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passedCount = pCount.intValue();
					}
					if (fCount != null) {
						failedCount = fCount.intValue();
					}
					totalCount = passedCount + failedCount;
				}

				passPercentage = ((double) passedCount / totalCount) * 100;
				if (passPercentage == null || passPercentage.isNaN()) {
					passPercentage = 0.0;
				}
				failPercentage = ((double) failedCount / totalCount) * 100;
				if (failPercentage == null || failPercentage.isNaN()) {
					failPercentage = 0.0;
				}

			}

			reportUIDashboardSummary.setConnectionId(connectionId);
			reportUIDashboardSummary.setConnectionName(dashboardConnection.getConnectionName());
			reportUIDashboardSummary.setDisplayName(dashboardConnection.getDisplayName());
			reportUIDashboardSummary.setDisplayOrder(dashboardConnection.getDisplayOrder());
			reportUIDashboardSummary.setTotalCount(totalCount);
			reportUIDashboardSummary.setPassedCount(passedCount);
			reportUIDashboardSummary.setPassPercentage(passPercentage);
			reportUIDashboardSummary.setFailedCount(failedCount);
			reportUIDashboardSummary.setFailPercentage(failPercentage);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return reportUIDashboardSummary;
	}

	private List<Long> getDQValidationOfConnection(long domainId, long projectId, long connectionId, String appType) {
		List<Long> dashConnValList = null;
		try {
			String sql = "select m1.idApp from dashboard_conn_app_list m1 join listApplications m2 on m1.idApp=m2.idApp and m1.domainId=? and m1.projectId=? and m1.connectionId=? and m2.appType=?";
			dashConnValList = jdbcTemplate.queryForList(sql, Long.class, domainId, projectId, connectionId, appType);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashConnValList;
	}

	@Override
	public List<String> getUniqueDatasourcesFromConnValidationMap(long domainId, long projectId) {
		List<String> datasourceList = null;
		try {
			String sql = "Select distinct datasource from dashboard_conn_app_list where domainId=? and projectId=?";
			datasourceList = jdbcTemplate.queryForList(sql, String.class, domainId, projectId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return datasourceList;
	}

	@Override
	public ReportUITableSummary getSummaryForSourceTypeDateRange(long domainId, long projectId, String datasource,
			String startDate, String endDate) {
		ReportUITableSummary summary = new ReportUITableSummary();
		try {

			// Get the DataQuality listOfValidations linked to datasource
			// TODO: For all kinds of validations
			List<Long> dashValidationList = getDQValidationOfDatasource(domainId, projectId, datasource,
					"Data Forensics");

			if (dashValidationList != null && dashValidationList.size() > 0) {
				int totalCount = 0;
				int passCount = 0;
				int failCount = 0;

				String idAppListStr = "";
				for (Long idApp : dashValidationList) {
					if (idApp != null && idApp != 0l) {
						idAppListStr = idAppListStr + idApp + ",";
					}
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				String sql = "select sum(m4.DQI=100) as passCount, sum(m4.DQI<100) as failCount from (select m3.AppId,m3.date,m3.Run, Avg(m3.DQI) as DQI from (select m2.AppId, m2.date, m2.Run,case when (m1.DQI IS NULL) then 0 else m1.DQI end as DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where  AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.DQI is not null) m3 group by m3.AppId,m3.date,m3.Run) m4;";
				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passCount = pCount.intValue();
					}
					if (fCount != null) {
						failCount = fCount.intValue();
					}
					totalCount = passCount + failCount;
				}
				summary.setDatasource(datasource);
				summary.setProcessedCount(totalCount);
				summary.setPassedCount(passCount);
				summary.setFailedCount(failCount);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return summary;
	}

	private List<Long> getDQValidationOfDatasource(long domainId, long projectId, String datasource, String appType) {
		List<Long> dashConnValList = null;
		try {
			String sql = "select m1.idApp from dashboard_conn_app_list m1 join listApplications m2 on m1.idApp=m2.idApp and m1.domainId =? and m1.projectId=? and m1.datasource=? and m2.appType=?";
			dashConnValList = jdbcTemplate.queryForList(sql, Long.class, domainId, projectId, datasource, appType);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashConnValList;
	}

	@Override
	public List<ReportUIPerformanceSummary> getDailyPerformanceTrend(long domainId, long projectId, long connectionId,
			String startDate, String endDate) {
		List<ReportUIPerformanceSummary> summryList = new ArrayList<ReportUIPerformanceSummary>();
		try {
			// Get the DataQuality listOfValidations linked to connections
			// TODO: For all kinds of validations
			List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
					"Data Forensics");

			String idAppListStr = "";
			if (dashValidationList != null && dashValidationList.size() > 0) {
				for (Long idApp : dashValidationList) {
					if (idApp != null && idApp != 0l) {
						idAppListStr = idAppListStr + idApp + ",";
					}
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				// Get the processed,pass and fail files count for each date
				String statusCountSql = "select t4.Date,sum(t4.DQI=100) as passedfiles, sum(t4.DQI<100) as failedfiles, count(*) as processedFiles  from (select t3.Date,t3.Run,t3.AppId,Avg(t3.DQI) as DQI from (select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and Date<='" + endDate
						+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr + ") and t1.Date<='" + endDate
						+ "') t3 group by t3.Date,t3.Run,t3.AppId) t4 group by t4.Date order by t4.Date desc limit 10";
				LOG.debug("statusCountSql: " + statusCountSql);

				List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

				if (statusList != null && statusList.size() > 0) {
					for (Map<String, Object> statusMap : statusList) {
						if (statusMap != null && statusMap.size() > 0) {

							String g_date = (String) statusMap.get("date");

							BigDecimal p_count = (BigDecimal) statusMap.get("passedfiles");
							long passedFilesCount = 0l;
							if (p_count != null) {
								passedFilesCount = p_count.longValue();
							}

							BigDecimal f_count = (BigDecimal) statusMap.get("failedfiles");
							long failedFilesCount = 0l;
							if (f_count != null) {
								failedFilesCount = f_count.longValue();
							}

							Long processedFilesCount = (Long) statusMap.get("processedFiles");
							if (processedFilesCount == null) {
								processedFilesCount = 0l;
							}

							Double failPercentage = ((double) failedFilesCount / processedFilesCount) * 100;
							if (failPercentage == null || failPercentage.isInfinite() || failPercentage.isNaN()) {
								failPercentage = 0.0;
							}

							ReportUIPerformanceSummary summry = new ReportUIPerformanceSummary();
							summry.setDate(g_date);
							summry.setProcessedFilesCount(processedFilesCount);
							summry.setFailedFilesCount(failedFilesCount);
							summry.setPassedFilesCount(passedFilesCount);
							summry.setFailPercentage(failPercentage);
							summryList.add(summry);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return summryList;
	}

	@Override
	public Map<String, Map<String, Double>> getDailyPassTrend(long domainId, long projectId, long connectionId,
			String startDate, String endDate) {
		Map<String, Map<String, Double>> resultMap = new LinkedHashMap<String, Map<String, Double>>();
		try {
			// Get the DataQuality listOfValidations linked to connections
			// TODO: For all kinds of validations
			List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
					"Data Forensics");

			String idAppListStr = "";
			if (dashValidationList != null && dashValidationList.size() > 0) {
				for (Long idApp : dashValidationList) {
					if (idApp != null && idApp != 0l) {
						idAppListStr = idAppListStr + idApp + ",";
					}
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				// Get the sourceType of each idApp
				String fileNameSql = "select idApp, datasource from dashboard_conn_app_list where idApp in ("
						+ idAppListStr + ")";
				List<Map<String, Object>> idAppFileNameList = jdbcTemplate.queryForList(fileNameSql);

				Map<Long, String> app_datasource_map = new HashMap<Long, String>();

				if (idAppFileNameList != null && idAppFileNameList.size() > 0) {
					for (Map<String, Object> s_map : idAppFileNameList) {
						if (s_map != null) {
							Long l_appId = (Long) s_map.get("idApp");
							String datasource = (String) s_map.get("datasource");
							app_datasource_map.put(l_appId, datasource);
						}
					}

					// Get the processed,pass and fail files count for each date
					String statusCountSql = "select t3.Date,t3.AppId,Avg(t3.DQI) as DQI from (select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr + ") and t1.Date<='" + endDate
							+ "') t3 group by t3.Date,t3.Run,t3.AppId order by t3.Date desc";

					LOG.debug("statusCountSql: " + statusCountSql);

					List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

					Map<String, Map<String, Long>> ds_totalCount_map = new LinkedHashMap<String, Map<String, Long>>();
					Map<String, Map<String, Long>> ds_passedCount_map = new LinkedHashMap<String, Map<String, Long>>();
					Set<String> datasourceList = new HashSet<String>();

					if (statusList != null && statusList.size() > 0) {
						for (Map<String, Object> statusMap : statusList) {
							if (statusMap != null && statusMap.size() > 0) {
								String g_date = (String) statusMap.get("date");

								Integer l_idApp = (Integer) statusMap.get("AppId");
								Long idApp = Long.parseLong(l_idApp.toString());

								String datasource = app_datasource_map.get(idApp);

								// Store the unique sourceType
								datasourceList.add(datasource);

								Double dqi = (Double) statusMap.get("DQI");
								long totalCount = 1l;
								long passCount = 0l;
								if (dqi == null) {
									dqi = 0.0;
								}

								if (dqi == 100) {
									passCount = 1l;
								}
								// Check if the group of date already exists
								Map<String, Long> tc_groupMap = null;
								Map<String, Long> pc_groupMap = null;

								if (ds_totalCount_map.containsKey(g_date)) {
									tc_groupMap = ds_totalCount_map.get(g_date);
								} else {
									tc_groupMap = new HashMap<String, Long>();
								}

								if (ds_passedCount_map.containsKey(g_date)) {
									pc_groupMap = ds_passedCount_map.get(g_date);
								} else {
									pc_groupMap = new HashMap<String, Long>();
								}

								if (tc_groupMap.containsKey(datasource)) {
									totalCount = totalCount + tc_groupMap.get(datasource);
								}
								tc_groupMap.put(datasource, totalCount);

								if (pc_groupMap.containsKey(datasource)) {
									passCount = passCount + pc_groupMap.get(datasource);
								}
								pc_groupMap.put(datasource, passCount);

								ds_totalCount_map.put(g_date, tc_groupMap);
								ds_passedCount_map.put(g_date, pc_groupMap);
							}
						}

						for (String date : ds_totalCount_map.keySet()) {
							Map<String, Long> source_totalcountMap = ds_totalCount_map.get(date);
							Map<String, Long> source_passcountMap = ds_passedCount_map.get(date);

							Map<String, Double> s_groupMap = null;
							if (resultMap.containsKey(date)) {
								s_groupMap = resultMap.get(date);
							} else {
								s_groupMap = new LinkedHashMap<String, Double>();
							}

							for (String g_sourceType : source_totalcountMap.keySet()) {

								Double passPercentage = 0.0;
								Long totalCount = source_totalcountMap.get(g_sourceType);
								Long passCount = source_passcountMap.get(g_sourceType);

								if (totalCount == null) {
									totalCount = 0l;
								}

								if (passCount == null) {
									passCount = 0l;
								}
								passPercentage = ((double) passCount / totalCount) * 100;
								if (passPercentage == null || passPercentage.isInfinite() || passPercentage.isNaN()) {
									passPercentage = 0.0;
								}
								s_groupMap.put(g_sourceType, passPercentage);

							}

							resultMap.put(date, s_groupMap);

						}

						for (String r_date : resultMap.keySet()) {
							Map<String, Double> s_Map = resultMap.get(r_date);

							for (String sourceType : datasourceList) {
								if (!s_Map.containsKey(sourceType)) {
									s_Map.put(sourceType, 0.0);
								}
							}

							resultMap.put(r_date, s_Map);
						}
					}

				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public List<String> getSourceListForDatasource(long domainId, long projectId, String datasource) {
		List<String> sourceList = null;
		try {
			String sql = "Select distinct source from dashboard_conn_app_list where domainId=? and projectId=? and datasource=?";
			sourceList = jdbcTemplate.queryForList(sql, String.class, domainId, projectId, datasource);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return sourceList;
	}

	@Override
	public List<String> getFileNameListForSource(long domainId, long projectId, String datasource, String source) {
		List<String> filenameList = null;
		try {
			String sql = "Select distinct fileName from dashboard_conn_app_list where domainId=? and projectId=? and datasource=? and source=?";
			filenameList = jdbcTemplate.queryForList(sql, String.class, domainId, projectId, datasource, source);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return filenameList;
	}

	@Override
	public List<Long> getValdiationsForSourceFile(long domainId, long projectId, long connectionId, String datasource,
			String source, String fileName) {
		List<Long> validationList = null;
		try {
			String sql = "Select idApp from dashboard_conn_app_list where domainId=? and projectId=? and connectionId=? and datasource=? and source=? and fileName=?";
			validationList = jdbcTemplate.queryForList(sql, Long.class, domainId, projectId, connectionId, datasource,
					source, fileName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return validationList;
	}

	@Override
	public Date getMaxDateForValidation(Long idApp, String startDate, String endDate) {
		Date maxDate = null;
		try {
			String sql = "Select max(Date) from DATA_QUALITY_Transactionset_sum_A1 where Date>='" + startDate
					+ "' and Date<='" + endDate + "' and idApp=?";
			maxDate = jdbcTemplate1.queryForObject(sql, Date.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return maxDate;
	}

	@Override
	public List<DashboardCheckComponent> getChecksByComponentType(String component) {
		List<DashboardCheckComponent> dashboardCheckComponentList = null;
		try {
			String sql = "Select componentId, checkName, description, component, entity_name, technical_name, technical_check_value, technical_result_name from dashboard_check_component_list where component=?";
			dashboardCheckComponentList = jdbcTemplate.query(sql, new RowMapper<DashboardCheckComponent>() {
				@Override
				public DashboardCheckComponent mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardCheckComponent dashboardCheckComponent = new DashboardCheckComponent();
					dashboardCheckComponent.setComponentId(rs.getLong("componentId"));
					dashboardCheckComponent.setCheckName(rs.getString("checkName"));
					dashboardCheckComponent.setDescription(rs.getString("description"));
					dashboardCheckComponent.setComponent(rs.getString("component"));
					dashboardCheckComponent.setEntityName(rs.getString("entity_name"));
					dashboardCheckComponent.setTechnicalName(rs.getString("technical_name"));
					dashboardCheckComponent.setTechnicalCheckValue(rs.getString("technical_check_value"));
					dashboardCheckComponent.setTechnicalResultName(rs.getString("technical_result_name"));
					return dashboardCheckComponent;
				}

			}, component);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardCheckComponentList;
	}

	@Override
	public boolean isDQComponentCheckEnabledForApp(Long idApp, String componentType) {
		LOG.debug("\n========> isDQComponentCheckEnabledForApp -- idApp:[" + idApp + "]  componentType:["
				+ componentType + "] - START <========");
		boolean status = false;
		try {
			List<DashboardCheckComponent> compCheckList = getChecksByComponentType(componentType);

			if (compCheckList != null && compCheckList.size() > 0) {
				Map<String, String> entity_check_map = new HashMap<String, String>();

				// Fetch all the checks for the component
				for (DashboardCheckComponent checkComp : compCheckList) {
					String entityName = checkComp.getEntityName();
					String technicalName = checkComp.getTechnicalName();
					String technicalCheckValue = checkComp.getTechnicalCheckValue();

					if (technicalName != null && !technicalName.trim().isEmpty() && technicalCheckValue != null
							&& !technicalCheckValue.trim().isEmpty()) {
						String checkCond = "";
						if (entity_check_map.containsKey(entityName)) {
							checkCond = entity_check_map.get(entityName);
						}
						if (checkCond != null && !checkCond.trim().isEmpty()) {
							checkCond = checkCond + " OR ";
						}

						checkCond = checkCond + technicalName + " = '" + technicalCheckValue + "'";
						entity_check_map.put(entityName, checkCond);
					}
				}

				// Create and execute queries to verify if Essential Check/Advanced check are
				// enabled
				for (String entity : entity_check_map.keySet()) {
					String sql = "Select count(*) from " + entity + " where idApp=? and ("
							+ entity_check_map.get(entity) + ")";
					LOG.debug("Sql : " + sql);

					Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idApp);
					LOG.debug("count : " + count);

					if (count != null && count > 0) {
						status = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.debug("status : " + status);
		LOG.debug("\n========> isDQComponentCheckEnabledForApp -- idApp:[" + idApp + "]  componentType:["
				+ componentType + "] - END  <========");
		return status;
	}

	@Override
	public List<ReportUIFailedFilesSummary> getDailyFailedFiles(long domainId, long projectId, long connectionId,
			String startDate, String endDate) {
		List<ReportUIFailedFilesSummary> summryList = new ArrayList<ReportUIFailedFilesSummary>();
		try {
			// Get the DataQuality listOfValidations linked to connections
			// TODO: For all kinds of validations
			List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
					"Data Forensics");

			String idAppListStr = "";
			if (dashValidationList != null && dashValidationList.size() > 0) {
				for (Long idApp : dashValidationList) {
					if (idApp != null && idApp != 0l) {
						idAppListStr = idAppListStr + idApp + ",";
					}
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				// Get the fileName for validations
				List<DashboardConnectionValidion> dashConnValList = getConnectionValidtionMap(domainId, projectId,
						connectionId);
				Map<Long, DashboardConnectionValidion> app_file_map = new HashMap<Long, DashboardConnectionValidion>();

				if (dashConnValList != null && dashConnValList.size() > 0) {
					for (DashboardConnectionValidion dashConnVal : dashConnValList) {

						// TODO: Filtering out the validations which are not of type "Data Forensics"
						if (dashConnVal != null && dashValidationList.contains(dashConnVal.getIdApp())) {
							app_file_map.put(dashConnVal.getIdApp(), dashConnVal);
						}
					}

					// Get AdvancedCheck ResultName List
					Set<String> advCheckResultNameList = getDQTechnicalResultNamesForComponent("Advanced Check");

					// Get EssentialCheck ResultName List
					Set<String> essCheckResultNameList = getDQTechnicalResultNamesForComponent("Essential Check");

					Map<Long, ReportUIFailedFilesSummary> app_check_map = new HashMap<Long, ReportUIFailedFilesSummary>();

					// Get the distinct date and max Run of date for each validation
					String sql = "select m2.AppId, m2.date, m2.Run,m1.DQI, m1.Test from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
							+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
							+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run";
					LOG.debug("Sql: " + sql);

					List<Map<String, Object>> appCheckList = jdbcTemplate1.queryForList(sql);

					if (appCheckList != null && appCheckList.size() > 0) {

						for (Map<String, Object> statusMap : appCheckList) {

							if (statusMap != null && statusMap.size() > 0) {
								Integer l_idApp = (Integer) statusMap.get("AppId");
								Long appId = Long.parseLong(l_idApp.toString());

								if (appId != null) {
									ReportUIFailedFilesSummary reportUIFailedFilesSummary = null;
									if (app_check_map.containsKey(appId)) {
										reportUIFailedFilesSummary = app_check_map.get(appId);
									} else {
										reportUIFailedFilesSummary = new ReportUIFailedFilesSummary();
										reportUIFailedFilesSummary.setIdApp(appId);
										DashboardConnectionValidion dashConnVal = app_file_map.get(appId);
										reportUIFailedFilesSummary.setDatasource(dashConnVal.getDatasource());
										reportUIFailedFilesSummary.setSource(dashConnVal.getSource());
										reportUIFailedFilesSummary.setFileName(dashConnVal.getFileName());
										reportUIFailedFilesSummary.setOverallStatus("failed");
									}
									Double dqi = (Double) statusMap.get("DQI");
									String status = "";
									if (dqi != null) {
										if (dqi == 100) {
											status = "passed";
										} else {
											status = "failed";
										}
									}

									String test = (String) statusMap.get("Test");
									if (essCheckResultNameList != null && essCheckResultNameList.contains(test)) {
										String prevStatus = reportUIFailedFilesSummary.getEssentialCheckStatus();
										if (prevStatus == null || !prevStatus.equalsIgnoreCase("failed")) {
											reportUIFailedFilesSummary.setEssentialCheckStatus(status);
										}
									}

									if (advCheckResultNameList != null && advCheckResultNameList.contains(test)) {
										String prevStatus = reportUIFailedFilesSummary.getAdvancedCheckStatus();
										if (prevStatus == null || !prevStatus.equalsIgnoreCase("failed")) {
											reportUIFailedFilesSummary.setAdvancedCheckStatus(status);
										}
									}

									app_check_map.put(appId, reportUIFailedFilesSummary);
								}
							}
						}

						for (long appId : app_check_map.keySet()) {
							ReportUIFailedFilesSummary reportUIFailedFilesSummary = app_check_map.get(appId);

							if (reportUIFailedFilesSummary != null) {
								String essChk_status = reportUIFailedFilesSummary.getEssentialCheckStatus();
								String advChk_status = reportUIFailedFilesSummary.getAdvancedCheckStatus();

								if ((essChk_status != null && essChk_status.equalsIgnoreCase("failed"))
										|| (advChk_status != null && advChk_status.equalsIgnoreCase("failed"))) {
									summryList.add(reportUIFailedFilesSummary);
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return summryList;
	}

	private Set<String> getDQTechnicalResultNamesForComponent(String componentType) {
		Set<String> resultNameList = new HashSet<String>();
		try {

			List<DashboardCheckComponent> compCheckList = getChecksByComponentType(componentType);

			if (compCheckList != null && compCheckList.size() > 0) {

				// Fetch all the checks for the component
				for (DashboardCheckComponent checkComp : compCheckList) {
					String technicalResultName = checkComp.getTechnicalResultName();

					if (technicalResultName != null && !technicalResultName.trim().isEmpty()) {
						if (technicalResultName.contains(",")) {
							String[] names = technicalResultName.split(",");
							for (String name : names) {
								resultNameList.add(name);
							}
						} else {
							resultNameList.add(technicalResultName);
						}
					}
				}

			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultNameList;
	}

	@Override
	public Double getTotalDQIOfIdAppForComponentType(Long idApp, String maxDate, String componentType) {
		Double totalDQI = null;
		try {
			// Get Technical ResultName List of component
			Set<String> resultNameList = getDQTechnicalResultNamesForComponent(componentType);
			String resultCheckCond = "";
			if (resultNameList != null) {
				for (String rName : resultNameList) {
					if (!resultCheckCond.trim().isEmpty()) {
						resultCheckCond += ",";
					}
					resultCheckCond = resultCheckCond + "'" + rName + "'";
				}
			}

			String sql = "";
			if (maxDate != null) {
				sql = "select Avg(DQI) from DashBoard_Summary where AppId=" + idApp + " and Date='" + maxDate
						+ "' and Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp + " "
						+ "and Date = '" + maxDate + "') and Test in (" + resultCheckCond + ")";
			} else {
				sql = "select Avg(DQI) from DashBoard_Summary where AppId=" + idApp
						+ " and Date=(select max(Date) from DashBoard_Summary where AppId=" + idApp
						+ ") and Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp
						+ "and Date = (select max(Date) from DashBoard_Summary where AppId=" + idApp
						+ ")) and Test in (" + resultCheckCond + ")";
			}
			LOG.debug("\n========> getTotalDQIOfIdAppForComponentType: SQL :: " + sql);

			totalDQI = jdbcTemplate1.queryForObject(sql, Double.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return totalDQI;
	}

	@Override
	public List<EssentialCheckSummaryReport> getEssentialCheckSummaryDetailsOfFile(String processedDate, Long idApp) {

		List<EssentialCheckSummaryReport> resultList = new ArrayList<EssentialCheckSummaryReport>();

		try {
			if (idApp != null && idApp != 0l) {

				// Get the listApplication details
				ListApplications listApplications = validationCheckDao.getdatafromlistapplications(idApp);

				if (listApplications != null) {

					String rcaTableName = "DATA_QUALITY_Transactionset_sum_A1";

					// Get maxDate and Run
					String dateRunSql = "select max(Run) from " + rcaTableName + " where idApp=" + idApp + " and Date='"
							+ processedDate + "'";
					Long maxRun = jdbcTemplate1.queryForObject(dateRunSql, Long.class);

					if (maxRun != null && maxRun != 0l) {

						// Get the list of checks associated with component Essential check
						List<DashboardCheckComponent> checksList = getChecksByComponentType("Essential Check");

						if (checksList != null && checksList.size() > 0) {
							String dataSql = "";

							for (DashboardCheckComponent dashboardCheckComponent : checksList) {
								String checkDataSql = "";
								String resultTableName = "";
								long checkComponentId = dashboardCheckComponent.getComponentId();

								// Null Check
								if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("nonNullCheck")
										&& listApplications.getNonNullCheck() != null
										&& listApplications.getNonNullCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_NullCheck_Summary";

									checkDataSql = "(select 'NullCheck' as Rule, 'nonNullCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, Status , '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
											+ "' and Run = " + maxRun + ")";
								}
								// DGroup Null check
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("dGroupNullCheck")
										&& listApplications.getdGroupNullCheck() != null
										&& listApplications.getdGroupNullCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Column_Summary";

									checkDataSql = "(select 'DGroupNullCheck' as Rule, 'dGroupNullCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId,colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, dGroupCol, dGroupVal, Status from "
											+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
											+ "' and Run = " + maxRun + ")";
								}
								// Default Check
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("defaultCheck")
										&& listApplications.getDefaultCheck() != null
										&& listApplications.getDefaultCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_default_value";
									// TODO: we don't have total count and failed percentage

								}
								// Length Check
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("lengthCheck")
										&& listApplications.getlengthCheck() != null
										&& listApplications.getlengthCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Length_Check";

									checkDataSql = "(select 'LengthCheck' as Rule, 'lengthCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, Length_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp = " + idApp + " and  Date='"
											+ processedDate + "' and Run = " + maxRun + ")";
								}
								// Max Length Check
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("maxLengthCheck")
										&& listApplications.getMaxLengthCheck() != null
										&& listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Max_Length_Check";

									checkDataSql = "(select 'maxLengthCheck' as Rule, 'maxLengthCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, maxLength_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp = " + idApp + " and  Date='"
											+ processedDate + "' and Run = " + maxRun + ")";
								}
								// dateRuleCheck
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("dateRuleCheck")
										&& listApplications.getDateRuleChk() != null
										&& listApplications.getDateRuleChk().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_DateRule_Summary";
									checkDataSql = "(select 'DateRuleCheck' as Rule, 'dateRuleCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, DateField as ColumnName, TotalNumberOfRecords as RecordCount, TotalFailedRecords as RecordFailed, ((TotalFailedRecords/TotalNumberOfRecords)*100) as FailedPerc, '0' as Threshold, case when (((TotalFailedRecords/TotalNumberOfRecords)*100)>0) then 'failed' else 'passed' end as Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp = " + idApp + " and Date='"
											+ processedDate + "' and Run = " + maxRun + ")";
								}
								// dGroupDateRuleCheck
								else if (dashboardCheckComponent.getTechnicalName()
										.equalsIgnoreCase("dGroupDateRuleCheck")
										&& listApplications.getdGroupDateRuleCheck() != null
										&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_DateRule_Summary";
									checkDataSql = "(select 'DGroupDateRuleCheck' as Rule, 'dGroupDateRuleCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, DateField as ColumnName, TotalNumberOfRecords as RecordCount, TotalFailedRecords as RecordFailed, ((TotalFailedRecords/TotalNumberOfRecords)*100) as FailedPerc, '0' as Threshold, case when (((TotalFailedRecords/TotalNumberOfRecords)*100)>0) then 'failed' else 'passed' end as Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp = " + idApp + " and  Date='"
											+ processedDate + "' and Run = " + maxRun + ")";

								}
								// patternCheck
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("patternCheck")
										&& listApplications.getPatternCheck() != null
										&& listApplications.getPatternCheck().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Unmatched_Pattern_Data";

									checkDataSql = "(select 'PatternCheck' as Rule,  'patternCheck' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, Col_Name as ColumnName, Total_Records as RecordCount, Total_Failed_Records as RecordFailed, FailedRecords_Percentage as FailedPerc, Pattern_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
											+ "' and Run = " + maxRun + ")";
								}
								// Duplicate check All (dupRow)
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("dupRow")
										&& isDuplicateCheckAllEnabled(idApp).equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Transaction_Summary";

									checkDataSql = "(select 'DuplicateCheckAll' as Rule,  'dupRow' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, '' as ColumnName, TotalCount as RecordCount, Duplicate as RecordFailed, Percentage as FailedPerc, Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
											+ "' and Run = " + maxRun + " and Type='all')";

								}
								// Duplicate check Identity (dupRow)
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("dupRow")
										&& isDuplicateCheckIdentityEnabled(idApp).equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Transaction_Summary";

									checkDataSql = "(select 'DuplicateCheckIdentity' as Rule,  'dupRow' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, '' as ColumnName, TotalCount as RecordCount, Duplicate as RecordFailed, Percentage as FailedPerc, Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
											+ "' and Run = " + maxRun + " and Type='identity')";
								}
								// applyRules
								else if (dashboardCheckComponent.getTechnicalName().equalsIgnoreCase("applyRules")
										&& listApplications.getApplyRules() != null
										&& listApplications.getApplyRules().equalsIgnoreCase("Y")) {

									resultTableName = "DATA_QUALITY_Rules";
									String globalRulesTableName = "DATA_QUALITY_GlobalRules";

									checkDataSql = "(select ruleName as Rule, 'applyRules' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ resultTableName + " where  idApp=" + idApp + " and  Date='"
											+ processedDate + "' and Run = " + maxRun + ")";
									checkDataSql = checkDataSql + " union ";
									checkDataSql = checkDataSql
											+ "(select ruleName as Rule, 'applyRules' as technicalName, "
											+ checkComponentId
											+ " as checkComponentId, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
											+ globalRulesTableName + " where  idApp=" + idApp + " and  Date='"
											+ processedDate + "' and Run = " + maxRun + ")";
								}

								if (!checkDataSql.trim().isEmpty()) {
									if (!dataSql.trim().isEmpty()) {
										dataSql = dataSql + " union ";
									}
									dataSql = dataSql + checkDataSql;
								}
							}

							LOG.debug("dataSql: " + dataSql);

							if (dataSql != null && !dataSql.isEmpty()) {
								resultList = jdbcTemplate1.query(dataSql, new RowMapper<EssentialCheckSummaryReport>() {
									@Override
									public EssentialCheckSummaryReport mapRow(ResultSet rs, int rowNum)
											throws SQLException {
										EssentialCheckSummaryReport essCheckSummaryReport = new EssentialCheckSummaryReport();
										essCheckSummaryReport.setRule(rs.getString("Rule"));
										essCheckSummaryReport.setCheckComponentId(rs.getLong("checkComponentId"));
										essCheckSummaryReport.setRuleTechnicalName(rs.getString("technicalName"));
										essCheckSummaryReport.setColumnName(rs.getString("ColumnName"));
										essCheckSummaryReport.setdGroupCol(rs.getString("dGroupCol"));
										essCheckSummaryReport.setdGroupVal(rs.getString("dGroupVal"));
										essCheckSummaryReport.setRecordCount(rs.getLong("RecordCount"));
										essCheckSummaryReport.setRecordFailed(rs.getLong("RecordFailed"));
										essCheckSummaryReport.setFailPercentage(rs.getDouble("FailedPerc"));
										essCheckSummaryReport.setThreshold(rs.getDouble("Threshold"));
										essCheckSummaryReport.setStatus(rs.getString("Status"));
										return essCheckSummaryReport;
									}
								});

							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultList;

	}

	@Override
	public List<EssentialCheckRuleSummaryReport> getEssentalCheckRuleSummaryDetailsOfFile(String processedDate,
			Long idApp, long checkComponentId, String technicalName, String ruleName, String columnName) {

		List<EssentialCheckRuleSummaryReport> resultList = new ArrayList<EssentialCheckRuleSummaryReport>();

		try {
			if (idApp != null && idApp != 0l) {

				// Get the listApplication details
				ListApplications listApplications = validationCheckDao.getdatafromlistapplications(idApp);

				if (listApplications != null) {

					String dataSql = "";

					// Null Check
					if (technicalName.equalsIgnoreCase("nonNullCheck") && listApplications.getNonNullCheck() != null
							&& listApplications.getNonNullCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_NullCheck_Summary";

						dataSql = "select Date, Run, 'NullCheck' as Rule, 'nonNullCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, Status , '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
								+ "' and colName = '" + columnName + "'";
					}
					// DGroup Null check
					else if (technicalName.equalsIgnoreCase("dGroupNullCheck")
							&& listApplications.getdGroupNullCheck() != null
							&& listApplications.getdGroupNullCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_Column_Summary";

						dataSql = "select Date, Run, 'DGroupNullCheck' as Rule, 'dGroupNullCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId,colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, dGroupCol, dGroupVal, Status from "
								+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
								+ "' and colName = '" + columnName + "'";
					}
					// Default Check
					else if (technicalName.equalsIgnoreCase("defaultCheck")
							&& listApplications.getDefaultCheck() != null
							&& listApplications.getDefaultCheck().equalsIgnoreCase("Y")) {

						// TODO: we don't have total count and failed percentage
						// String resultTableName = "DATA_QUALITY_default_value";

					}
					// Length Check
					else if (technicalName.equalsIgnoreCase("lengthCheck") && listApplications.getlengthCheck() != null
							&& listApplications.getlengthCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_Length_Check";

						dataSql = "select Date, Run, 'LengthCheck' as Rule, 'lengthCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, Length_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp = " + idApp + " and  Date='" + processedDate
								+ "' and ColName = '" + columnName + "'";
					}
					// Max Length Check
					else if (technicalName.equalsIgnoreCase("maxLengthCheck")
							&& listApplications.getMaxLengthCheck() != null
							&& listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_Max_Length_Check";

						dataSql = "select Date, Run, 'maxLengthCheck' as Rule, 'maxLengthCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, maxLength_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp = " + idApp + " and  Date='" + processedDate
								+ "' and ColName = '" + columnName + "'";
					}
					// dateRuleCheck
					else if (technicalName.equalsIgnoreCase("dateRuleCheck")
							&& listApplications.getDateRuleChk() != null
							&& listApplications.getDateRuleChk().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_DateRule_Summary";

						dataSql = "select Date, Run, 'DateRuleCheck' as Rule, 'dateRuleCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, DateField as ColumnName, TotalNumberOfRecords as RecordCount, TotalFailedRecords as RecordFailed, ((TotalFailedRecords/TotalNumberOfRecords)*100) as FailedPerc, '0' as Threshold, case when (((TotalFailedRecords/TotalNumberOfRecords)*100)>0) then 'failed' else 'passed' end as Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp = " + idApp + " and  Date='" + processedDate
								+ "' and DateField = '" + columnName + "";
					}
					// dGroupDateRuleCheck
					else if (technicalName.equalsIgnoreCase("dGroupDateRuleCheck")
							&& listApplications.getdGroupDateRuleCheck() != null
							&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_DateRule_Summary";

						dataSql = "select Date, Run, 'DGroupDateRuleCheck' as Rule, 'dGroupDateRuleCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, DateField as ColumnName, TotalNumberOfRecords as RecordCount, TotalFailedRecords as RecordFailed, ((TotalFailedRecords/TotalNumberOfRecords)*100) as FailedPerc, '0' as Threshold, case when (((TotalFailedRecords/TotalNumberOfRecords)*100)>0) then 'failed' else 'passed' end as Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp = " + idApp + " and  Date='" + processedDate
								+ "' and DateField = '" + columnName + "'";

					}
					// patternCheck
					else if (technicalName.equalsIgnoreCase("patternCheck")
							&& listApplications.getPatternCheck() != null
							&& listApplications.getPatternCheck().equalsIgnoreCase("Y")) {

						String resultTableName = "DATA_QUALITY_Unmatched_Pattern_Data";

						dataSql = "select Date, Run, 'PatternCheck' as Rule,  'patternCheck' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, Col_Name as ColumnName, Total_Records as RecordCount, Total_Failed_Records as RecordFailed, FailedRecords_Percentage as FailedPerc, Pattern_Threshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
								+ "' and Col_Name = '" + columnName + "'";
					}
					// Duplicate check All (dupRow)
					else if (technicalName.equalsIgnoreCase("dupRow")
							&& ruleName.equalsIgnoreCase("DuplicateCheckAll")) {

						String resultTableName = "DATA_QUALITY_Transaction_Summary";

						dataSql = "select Date, Run, 'DuplicateCheckAll' as Rule,  'dupRow' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, '' as ColumnName, TotalCount as RecordCount, Duplicate as RecordFailed, Percentage as FailedPerc, Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
								+ "' and Type='all'";

					}
					// Duplicate check Identity (dupRow)
					else if (technicalName.equalsIgnoreCase("dupRow")
							&& ruleName.equalsIgnoreCase("DuplicateCheckIdentity")) {

						String resultTableName = "DATA_QUALITY_Transaction_Summary";

						dataSql = "select Date, Run, 'DuplicateCheckIdentity' as Rule,  'dupRow' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, '' as ColumnName, TotalCount as RecordCount, Duplicate as RecordFailed, Percentage as FailedPerc, Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ resultTableName + " where idApp=" + idApp + " and Date='" + processedDate
								+ "' and Type='identity'";
					}
					// applyRules
					else if (technicalName.equalsIgnoreCase("applyRules") && listApplications.getApplyRules() != null
							&& listApplications.getApplyRules().equalsIgnoreCase("Y")) {

						String rulesTableName = "DATA_QUALITY_Rules";
						String globalRulesTableName = "DATA_QUALITY_GlobalRules";

						dataSql = "(select Date, Run, ruleName as Rule, 'applyRules' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ rulesTableName + " where  idApp=" + idApp + " and Date='" + processedDate
								+ "' and ruleName = '" + ruleName + "') union "
								+ "(select Date, Run, ruleName as Rule, 'applyRules' as technicalName, "
								+ checkComponentId
								+ " as checkComponentId, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status, '' as dGroupCol, '' as  dGroupVal from "
								+ globalRulesTableName + " where  idApp=" + idApp + " and  Date='" + processedDate
								+ "' and ruleName = '" + ruleName + "')";
					}

					LOG.debug("dataSql: " + dataSql);

					resultList = jdbcTemplate1.query(dataSql, new RowMapper<EssentialCheckRuleSummaryReport>() {
						@Override
						public EssentialCheckRuleSummaryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
							EssentialCheckRuleSummaryReport essCheckRuleSummaryReport = new EssentialCheckRuleSummaryReport();
							essCheckRuleSummaryReport.setDate(rs.getString("Date"));
							essCheckRuleSummaryReport.setRun(rs.getLong("Run"));
							essCheckRuleSummaryReport.setRule(rs.getString("Rule"));
							essCheckRuleSummaryReport.setCheckComponentId(rs.getLong("checkComponentId"));
							essCheckRuleSummaryReport.setRuleTechnicalName(rs.getString("technicalName"));
							essCheckRuleSummaryReport.setColumnName(rs.getString("ColumnName"));
							essCheckRuleSummaryReport.setdGroupCol(rs.getString("dGroupCol"));
							essCheckRuleSummaryReport.setdGroupVal(rs.getString("dGroupVal"));
							essCheckRuleSummaryReport.setRecordCount(rs.getLong("RecordCount"));
							essCheckRuleSummaryReport.setRecordFailed(rs.getLong("RecordFailed"));
							essCheckRuleSummaryReport.setFailPercentage(rs.getDouble("FailedPerc"));
							essCheckRuleSummaryReport.setThreshold(rs.getDouble("Threshold"));
							essCheckRuleSummaryReport.setStatus(rs.getString("Status"));
							return essCheckRuleSummaryReport;
						}
					});
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultList;

	}

	@Override
	public String isDuplicateCheckAllEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from listDFTranRule where type='all' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String isDuplicateCheckIdentityEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public ReportUIProjectCoverage getProjectCoverage(ReportUIProjectCoverage reportUIProjectCoverage, long domainId,
			long projectId) {
		try {
			List<ReportUISchemaCoverage> connectionList = new ArrayList<ReportUISchemaCoverage>();

			// Get connections list of project for which dashboard configuration is done
			List<DashboardConnection> projConList = getDashboardEnabledConnectionsForProject(domainId, projectId);

			if (projConList != null && projConList.size() > 0) {
				for (DashboardConnection dashboardConnection : projConList) {
					ReportUISchemaCoverage schemaCoverage = new ReportUISchemaCoverage();
					long connectionId = dashboardConnection.getConnectionId();
					schemaCoverage.setConnectionId(connectionId);
					schemaCoverage.setDisplayName(dashboardConnection.getDisplayName());
					schemaCoverage.setDisplayOrder(dashboardConnection.getDisplayOrder());

					List<ReportUITableCoverage> tableCoverageList = new ArrayList<ReportUITableCoverage>();

					Map<String, List<Long>> schemaTableMap = new HashMap<String, List<Long>>();

					// Get the no of tables associated and its validation count
					String countSql = "select lsa.folderName as tableName, la.idApp as validationId from listDataSources lds join listDataAccess lsa on lds.idData= lsa.idData join listApplications la on lds.idData=la.idData and lsa.idData=la.idData where  lsa.folderName!='Query' and lsa.idDataSchema=?";
					SqlRowSet rs = jdbcTemplate.queryForRowSet(countSql, connectionId);

					while (rs.next()) {
						String tableName = rs.getString("tableName");
						Long idApp = rs.getLong("validationId");

						List<Long> appList = new ArrayList<Long>();
						if (schemaTableMap.containsKey("tableName")) {
							appList = schemaTableMap.get("tableName");
						}
						appList.add(idApp);

						schemaTableMap.put(tableName, appList);
					}

					if (schemaTableMap != null && schemaTableMap.size() > 0) {

						// Get the coverage percentage using validations list
						for (String tableName : schemaTableMap.keySet()) {
							List<Long> appList = schemaTableMap.get(tableName);

							long totalChecks = 0;
							long selectedChecks = 0;

							if (appList != null && appList.size() > 0) {

								// Get the essential checks list
								List<DashboardCheckComponent> essCheckList = getChecksByComponentType(
										"Essential Check");

								if (essCheckList != null && essCheckList.size() > 0) {
									totalChecks = essCheckList.size();
									selectedChecks = getCountOfChecksEnabledForValidations(appList, essCheckList);
								}
								// Get Advanced checks list
								List<DashboardCheckComponent> advCheckList = getChecksByComponentType("Advanced Check");

								if (advCheckList != null && advCheckList.size() > 0) {
									totalChecks = totalChecks + advCheckList.size();
									selectedChecks = selectedChecks
											+ getCountOfChecksEnabledForValidations(appList, advCheckList);
								}
							}

							double coveragePerc = (((double) totalChecks - selectedChecks) / totalChecks) * 100;
							DecimalFormat df = new DecimalFormat("#.00");
							String cov_per_str = "" + coveragePerc;
							if (coveragePerc != 0) {
								cov_per_str = df.format(coveragePerc);
							}

							// >70% --- red
							// 70 - 30 % --- yellow
							// <30 --- green
							String coverageColor = "";
							if (coveragePerc > 70) {
								coverageColor = "red";
							} else if (coveragePerc > 30 && coveragePerc < 70) {
								coverageColor = "yellow";
							} else {
								coverageColor = "green";
							}

							ReportUITableCoverage reportUITableCoverage = new ReportUITableCoverage();
							reportUITableCoverage.setTableName(tableName);
							reportUITableCoverage.setTableCoverage(cov_per_str);
							reportUITableCoverage.setColor(coverageColor);
							tableCoverageList.add(reportUITableCoverage);
						}
						schemaCoverage.setSchemaTableList(tableCoverageList);
					}
					connectionList.add(schemaCoverage);
				}

				if (connectionList != null && connectionList.size() > 0) {
					reportUIProjectCoverage.setConnectionList(connectionList);
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return reportUIProjectCoverage;
	}

	private Long getCountOfChecksEnabledForValidations(List<Long> appList,
			List<DashboardCheckComponent> compCheckList) {
		Long selectedChksCount = 0l;
		try {
			if (appList != null && appList.size() > 0) {
				String idAppStr = "";
				for (Long idApp : appList) {
					idAppStr = (idAppStr.length() > 0) ? idAppStr + "," + idApp : "" + idApp;
				}

				if (compCheckList != null && compCheckList.size() > 0) {

					for (DashboardCheckComponent checkComp : compCheckList) {
						String entityName = checkComp.getEntityName();
						String technicalName = checkComp.getTechnicalName();
						String technicalCheckValue = checkComp.getTechnicalCheckValue();

						if (technicalName != null && !technicalName.trim().isEmpty() && technicalCheckValue != null
								&& !technicalCheckValue.trim().isEmpty()) {

							// Prepare the sql to check if this check is enabled in any validation
							String sql = "select count(*) from " + entityName + " where " + technicalName + "='"
									+ technicalCheckValue + "' and idApp in (" + idAppStr + ")";

							Long chkCount = jdbcTemplate.queryForObject(sql, Long.class);
							if (chkCount > 0) {
								++selectedChksCount;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return selectedChksCount;
	}

	@Override
	public List<ReportUIFailedAsset> getTopFailedAssets(long domainId, long projectId) {
		List<ReportUIFailedAsset> failedAssetList = new ArrayList<ReportUIFailedAsset>();
		try {

			// Get the list of connections associated with project
			List<DashboardConnection> projConList = getDashboardEnabledConnectionsForProject(domainId, projectId);

			if (projConList != null && projConList.size() > 0) {

				String idAppListStr = "";
				Map<Long, DashboardConnectionValidion> app_file_map = new HashMap<Long, DashboardConnectionValidion>();
				Map<Long, DashboardConnection> app_conn_map = new HashMap<Long, DashboardConnection>();

				for (DashboardConnection dashboardConnection : projConList) {

					long connectionId = dashboardConnection.getConnectionId();

					// Get the DataQuality listOfValidations linked to connections
					// TODO: For all kinds of validations
					List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
							"Data Forensics");

					if (dashValidationList != null && dashValidationList.size() > 0) {
						for (Long idApp : dashValidationList) {
							if (idApp != null && idApp != 0l) {
								idAppListStr = idAppListStr + idApp + ",";
								app_conn_map.put(idApp, dashboardConnection);
							}
						}

						// Get the fileName for validations
						List<DashboardConnectionValidion> dashConnValList = getConnectionValidtionMap(domainId,
								projectId, connectionId);

						if (dashConnValList != null && dashConnValList.size() > 0) {
							for (DashboardConnectionValidion dashConnVal : dashConnValList) {

								// TODO: Filtering out the validations which are not of type "Data Forensics"
								if (dashConnVal != null && dashValidationList.contains(dashConnVal.getIdApp())) {
									app_file_map.put(dashConnVal.getIdApp(), dashConnVal);
								}
							}
						}
					}

				}

				if (idAppListStr.length() > 1 && idAppListStr.endsWith(",")) {
					idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);
				}

				// Get the validations which have least DQI
				String sql = "select t3.Date,t3.AppId as appId ,Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null) t3 group by t3.Date,t3.Run,t3.AppId order by total_DQI asc limit 10";
				LOG.debug("Sql: " + sql);

				List<Map<String, Object>> appList = jdbcTemplate1.queryForList(sql);

				if (appList != null && appList.size() > 0) {
					for (Map<String, Object> r_map : appList) {
						String date = (String) r_map.get("Date");
						Integer appId = (Integer) r_map.get("appId");
						Long l_appId = (appId != null) ? Long.parseLong(appId.toString()) : null;

						Double dqi = (Double) r_map.get("total_DQI");

						if (l_appId != null && l_appId != 0l && app_file_map.get(l_appId) != null
								&& app_conn_map.get(l_appId) != null) {
							DashboardConnectionValidion dashConnVal = app_file_map.get(l_appId);
							DashboardConnection dashConn = app_conn_map.get(l_appId);

							ReportUIFailedAsset failedAsset = new ReportUIFailedAsset();
							failedAsset.setDate(date);
							failedAsset.setValidationId(l_appId);
							failedAsset.setValidationDQI(dqi);
							failedAsset.setConnectionId(dashConn.getConnectionId());
							failedAsset.setDisplayName(dashConn.getDisplayName());
							failedAsset.setDatasource(dashConnVal.getDatasource());
							failedAsset.setFileName(dashConnVal.getFileName());
							failedAsset.setSource(dashConnVal.getSource());
							failedAssetList.add(failedAsset);
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return failedAssetList;
	}

	@Override
	public List<DashboardConnection> getDashboardEnabledConnectionsForProject(long domainId, long projectId) {
		List<DashboardConnection> dashboardConnectionList = null;

		try {
			String sql = "select connectionId,displayName,displayOrder from dashboard_project_conn_list where domainId=? and projectId=?";
			LOG.debug(sql);
			dashboardConnectionList = jdbcTemplate.query(sql, new RowMapper<DashboardConnection>() {
				@Override
				public DashboardConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
					DashboardConnection dashboardConnection = new DashboardConnection();
					dashboardConnection.setDomainId(domainId);
					dashboardConnection.setProjectId(projectId);
					dashboardConnection.setConnectionId(rs.getLong("connectionId"));
					dashboardConnection.setDisplayName(rs.getString("displayName"));
					dashboardConnection.setDisplayOrder(rs.getInt("displayOrder"));
					return dashboardConnection;
				}

			}, domainId, projectId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashboardConnectionList;
	}

	@Override
	public ReportUIOverallDQIIndex getOverallDQIIndexForProject(long domainId, long projectId) {
		ReportUIOverallDQIIndex reportUIOverallDQIIndex = new ReportUIOverallDQIIndex();
		try {

			// Set project id
			reportUIOverallDQIIndex.setDomainId(domainId);
			reportUIOverallDQIIndex.setProjectId(projectId);

			// Get the list of connections associated with project
			List<DashboardConnection> projConList = getDashboardEnabledConnectionsForProject(domainId, projectId);

			if (projConList != null && projConList.size() > 0) {

				String idAppListStr = "";

				for (DashboardConnection dashboardConnection : projConList) {

					long connectionId = dashboardConnection.getConnectionId();

					// Get the DataQuality listOfValidations linked to connections
					// TODO: For all kinds of validations
					List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
							"Data Forensics");

					if (dashValidationList != null && dashValidationList.size() > 0) {
						for (Long idApp : dashValidationList) {
							if (idApp != null && idApp != 0l) {
								idAppListStr = idAppListStr + idApp + ",";
							}
						}

					}

				}

				if (idAppListStr.length() > 1 && idAppListStr.endsWith(",")) {
					idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);
				}

				String sql = "";
				Map<String, Object> resultMap = null;

				/*
				 * COMPLETENESS -- Null check, Default value
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null and t1.Test in ('DQ_Completeness','DQ_DefaultCheck')) t3";
				LOG.debug("COMPLETENESS Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setCompletenessDqi((Double) resultMap.get("total_DQI"));
				}

				/*
				 * ACCURACY -- Duplicate check, Length check, Bad data check, Pattern check,
				 * custom Rules
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null and t1.Test in ('DQ_Uniqueness -Primary Keys', 'DQ_Uniqueness -Seleted Fields','DQ_LengthCheck','DQ_maxLengthCheck','DQ_Bad_Data','DQ_Pattern_Data','DQ_Sql_Rule','DQ_Rules')) t3";
				LOG.debug("ACCURACY Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setAccuracyDqi((Double) resultMap.get("total_DQI"));
				}

				/*
				 * CONSISTENCY -- Drift, Distribution check, Record Anomaly
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null and t1.Test in ('DQ_Data Drift','DQ_Numerical Field Fingerprint','DQ_Record Anomaly')) t3";
				LOG.debug("CONSISTENCY Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setConsistencyDqi((Double) resultMap.get("total_DQI"));
				}

				/*
				 * VALIDITY -- Record count check, KGRCA
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null and t1.Test in ('DQ_Record Count Fingerprint')) t3";
				LOG.debug("VALIDITY Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setValidityDqi((Double) resultMap.get("total_DQI"));
				}

				/*
				 * TIMELINESS -- timeliness check, Date Rule
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr
						+ ") where t1.DQI is not null and t1.Test in ('DQ_Timeliness','DQ_DateRuleCheck')) t3";
				LOG.debug("TIMELINESS Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setTimelinessDqi((Double) resultMap.get("total_DQI"));
				}

				/*
				 * Overall DQI
				 */
				sql = "select Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr
						+ ") group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr + ") where t1.DQI is not null) t3";
				LOG.debug("Overall DQI Index Sql: " + sql);

				resultMap = jdbcTemplate1.queryForMap(sql);
				if (resultMap != null && resultMap.get("total_DQI") != null) {
					reportUIOverallDQIIndex.setOverallDqi((Double) resultMap.get("total_DQI"));
				}

			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return reportUIOverallDQIIndex;
	}

	@Override
	public ReportUIDQIIndexHistory getIndexTrendHistory(long domainId, long projectId, String indexName,
			String startDate, String endDate) {
		ReportUIDQIIndexHistory reportUIDQIIndexHistory = new ReportUIDQIIndexHistory();
		try {

			// Set domain Id
			reportUIDQIIndexHistory.setDomainId(domainId);
			// Set project id
			reportUIDQIIndexHistory.setProjectId(projectId);
			// Set Index Name
			reportUIDQIIndexHistory.setIndexName(indexName);

			// Get the list of connections associated with project
			List<DashboardConnection> projConList = getDashboardEnabledConnectionsForProject(domainId, projectId);

			if (projConList != null && projConList.size() > 0) {

				String idAppListStr = "";

				for (DashboardConnection dashboardConnection : projConList) {

					long connectionId = dashboardConnection.getConnectionId();

					// Get the DataQuality listOfValidations linked to connections
					// TODO: For all kinds of validations
					List<Long> dashValidationList = getDQValidationOfConnection(domainId, projectId, connectionId,
							"Data Forensics");

					if (dashValidationList != null && dashValidationList.size() > 0) {
						for (Long idApp : dashValidationList) {
							if (idApp != null && idApp != 0l) {
								idAppListStr = idAppListStr + idApp + ",";
							}
						}

					}

				}

				if (idAppListStr.length() > 1 && idAppListStr.endsWith(",")) {
					idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);
				}

				String sql = "";
				/*
				 * COMPLETENESS -- Null check, Default value
				 */
				if (indexName.equalsIgnoreCase("COMPLETENESS")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr
							+ ") where t1.DQI is not null and t1.Test in ('DQ_Completeness','DQ_DefaultCheck')  and t1.Date>='"
							+ startDate + "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * ACCURACY -- Duplicate check, Length check, Bad data check, Pattern check,
				 * custom Rules
				 */
				else if (indexName.equalsIgnoreCase("ACCURACY")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr
							+ ") where t1.DQI is not null and t1.Test in ('DQ_Uniqueness -Primary Keys', 'DQ_Uniqueness -Seleted Fields','DQ_LengthCheck','DQ_maxLengthCheck','DQ_Bad_Data','DQ_Pattern_Data','DQ_Sql_Rule','DQ_Rules')  and t1.Date>='"
							+ startDate + "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * CONSISTENCY -- Drift, Distribution check, Record Anomaly
				 */
				else if (indexName.equalsIgnoreCase("CONSISTENCY")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr
							+ ") where t1.DQI is not null and t1.Test in ('DQ_Data Drift','DQ_Numerical Field Fingerprint','DQ_Record Anomaly') and t1.Date>='"
							+ startDate + "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * VALIDITY -- Record count check, KGRCA
				 */
				else if (indexName.equalsIgnoreCase("VALIDITY")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr
							+ ") where t1.DQI is not null and t1.Test in ('DQ_Record Count Fingerprint') and t1.Date>='"
							+ startDate + "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * TIMELINESS -- timeliness check, Date Rule
				 */
				else if (indexName.equalsIgnoreCase("TIMELINESS")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr
							+ ") where t1.DQI is not null and t1.Test in ('DQ_Timeliness','DQ_DateRuleCheck') and t1.Date>='"
							+ startDate + "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * Overall DQI
				 */
				else if (indexName.equalsIgnoreCase("DQI")) {
					sql = "select t3.Date, Avg(t3.DQI) as total_DQI from (select t1.Date,t1.Run,t1.AppId,t1.DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
							+ idAppListStr + ") and Date>='" + startDate + "' and Date<='" + endDate
							+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
							+ idAppListStr + ") where t1.DQI is not null and t1.Date>='" + startDate
							+ "' and t1.Date<='" + endDate + "') t3 group by t3.Date order by t3.Date desc";
				}
				/*
				 * Unsupported Index
				 */
				else {
					LOG.error("\n====> Unsupported Index !!");
					return reportUIDQIIndexHistory;
				}
				LOG.debug("Sql: " + sql);

				Map<String, Double> indexHistoryMap = new LinkedHashMap<String, Double>();

				List<Map<String, Object>> resultList = jdbcTemplate1.queryForList(sql);
				if (resultList != null && resultList.size() > 0) {
					for (Map<String, Object> r_map : resultList) {

						Double dqi = (Double) r_map.get("total_DQI");
						if (dqi == null) {
							dqi = 0.0;
						}

						// String execDate = new SimpleDateFormat("yyyy-MM-dd").format((Date)
						// r_map.get("Date"));

						indexHistoryMap.put((String) r_map.get("Date"), dqi);
					}
				}
				reportUIDQIIndexHistory.setIndexHistory(indexHistoryMap);
			}

		} catch (

		Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return reportUIDQIIndexHistory;
	}

	@Override
	public Integer insertIntoDashboardTableCount(long slId, long schemaId, long totalNoTable, long tableMonitored,
			long issuesDetected,long unValidatedTableCount, long totalRulesExecuted, long highTrustTable,long lowTrustTable,
			String hoursSaved, String date) {
		Integer id = null;
		Long dummySchemaId = (long) 1;
		List<DashboardTableCount>  listDashboardTableCount = getDashboardTableCount();
		if(listDashboardTableCount==null || listDashboardTableCount.size()==0) {
			final String sql = "INSERT INTO dashboard_table_count "
					+ "(schema_id,total_table_count,monitored_table_count,unvalidated_table_count, "
					+ "high_trust_table_count,low_trust_table_count,issues_detected,hours_saved,rules_executed,updated_date_time) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,now())";
			id = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql);
					pst.setLong(1, dummySchemaId);
					pst.setLong(2, totalNoTable);
					pst.setLong(3, tableMonitored);
					pst.setLong(4, unValidatedTableCount);
					pst.setLong(5, highTrustTable);
					pst.setLong(6, lowTrustTable);
					pst.setLong(7, issuesDetected);
					pst.setString(8, hoursSaved);
					pst.setLong(9, totalRulesExecuted);
					return pst;
				}
			});
		}else {
			final String sql = "UPDATE dashboard_table_count "
					+ "SET total_table_count = ?, monitored_table_count = ?, unvalidated_table_count = ?, high_trust_table_count = ?, "
					+ "low_trust_table_count = ?, issues_detected = ?, hours_saved = ?,rules_executed = ?, updated_date_time = now()";
			id = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql);
					pst.setLong(1, totalNoTable);
					pst.setLong(2, tableMonitored);
					pst.setLong(3, unValidatedTableCount);
					pst.setLong(4, highTrustTable);
					pst.setLong(5, lowTrustTable);
					pst.setLong(6, issuesDetected);
					pst.setString(7, hoursSaved);
					pst.setLong(8, totalRulesExecuted);
					return pst;
				}
			});
		}
		return id;
	}
	
	public List<DashboardTableCount> getDashboardTableCount() {

		String sql = "SELECT * " + "from dashboard_table_count";
		List<DashboardTableCount> listdashboardTableCount = jdbcTemplate.query(sql, new RowMapper<DashboardTableCount>() {
			@Override
			public DashboardTableCount mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardTableCount dashboardTableCount = new DashboardTableCount();
				dashboardTableCount.setId(rs.getLong("id"));
				dashboardTableCount.setMonitoredTableCount(rs.getInt("monitored_table_count"));
				dashboardTableCount.setSchemaId(rs.getLong("schema_id"));
				dashboardTableCount.setTableCount(rs.getInt("total_table_count"));
				dashboardTableCount.setUpdatedDateTime(rs.getString("updated_date_time"));
				return dashboardTableCount;
			}
		});
		return listdashboardTableCount;
	}
	
	public DashboardTableCountSummary getSumOfDashboardTableCount() {
		String sql = "";
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = 	"SELECT total_table_count as total_table_count,monitored_table_count as monitored_table_count, "
			+ "high_trust_table_count as high_trust_table_count, low_trust_table_count as low_trust_table_count, "
			+ "issues_detected as issues_detected, hours_saved as hours_saved, rules_executed as rules_executed, "
			+ "unvalidated_table_count as unvalidated_table_count, TO_CHAR(updated_date_time, 'YYYY-MM-DD') AS latest_date  FROM dashboard_table_count;";
		}else {
			sql = 	"SELECT total_table_count as total_table_count,monitored_table_count as monitored_table_count, "
					+ "high_trust_table_count as high_trust_table_count, low_trust_table_count as low_trust_table_count, "
					+ "issues_detected as issues_detected, hours_saved as hours_saved, rules_executed as rules_executed, "
					+ "unvalidated_table_count as unvalidated_table_count, DATE_FORMAT(updated_date_time, '%Y-%m-%d') AS latest_date  FROM dashboard_table_count;";
		}
		List<DashboardTableCountSummary> listdashboardTableCount = jdbcTemplate.query(sql, new RowMapper<DashboardTableCountSummary>() {
			@Override
			public DashboardTableCountSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardTableCountSummary dashboardTableCount = new DashboardTableCountSummary();
				dashboardTableCount.setTotalTablesCount(rs.getInt("total_table_count"));
				dashboardTableCount.setMonitoredTablesCount(rs.getInt("monitored_table_count"));
				dashboardTableCount.setHighTrustTablesCount(rs.getInt("high_trust_table_count"));
				dashboardTableCount.setLowTrustTablesCount(rs.getInt("low_trust_table_count"));
				dashboardTableCount.setIssuesDetected(rs.getInt("issues_detected"));
				dashboardTableCount.setUnValidatedTablesCount(rs.getInt("unvalidated_table_count"));
				dashboardTableCount.setEffortsSavedHrs(rs.getString("hours_saved"));
				dashboardTableCount.setRulesExecuted(rs.getInt("rules_executed"));
				dashboardTableCount.setLatestDate(rs.getString("latest_date"));
				return dashboardTableCount;
			}
		});
		if(listdashboardTableCount!=null && listdashboardTableCount.size()>0) {
			return listdashboardTableCount.get(0);
		}else {
			return new DashboardTableCountSummary();
		}
		
	}
}
