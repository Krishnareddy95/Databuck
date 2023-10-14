<%@page import="org.springframework.jdbc.support.xml.SqlXmlValue"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.IdentityHashMap"%>
<%@page import="java.util.Map"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
	<jsp:include page="viewDatatable.jsp" />
	<jsp:include page="header.jsp" />
	<jsp:include page="checkVulnerability.jsp" />
	<jsp:include page="container.jsp" />

	<style>
		table.dataTable tbody th,
		table.dataTable tbody td {
			white-space: nowrap;
		}

		.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
	</style>
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<!-- BEGIN CONTENT BODY -->
		<div class="page-content">
			<!-- BEGIN PAGE TITLE- ->
		<!-- END PAGE TITLE-->
			<!-- END PAGE HEADER-->
			<div class="row">
				<br>
			</div>
			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Table Dashboard For :
									${appName} <br> <br> Left Schema Name : ${leftSchemaName }
									<br><br> Right Schema Name : ${ rightSchemaName}
								</span>
							</div>
						</div>

						<c:if test="${tableSummaryExist eq 'Y'}">

							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Table Meta Data
										Comparison<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${schemaMatchingTransactionTable}','schemaMatchingTableData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>

							<div class="portlet-body">
								<table
									class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
									<thead>
										<tr>
											<%-- <%
												try {
														SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("tablesummaryrowset");
														SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
														for (int i = 1; i <= metaData.getColumnCount(); i++) {
															//System.out.println(metaData.getColumnName(i));
															out.println("<th>" + metaData.getColumnLabel(i) + "</th>");
														}
													} catch (NullPointerException e) {
											%>
											<%
												}
											%> --%>
											<th>Id</th>
											<th>Date</th>
											<th>Run</th>
											<th>Common_Tables</th>
											<th>Left_Only_Tables</th>
											<th>Right_Only_Tables</th>
											
										</tr>
									</thead>
									<tbody>
										<%
											try {
													SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("tablesummaryrowset");
													while (sqlRowSet.next()) {
														out.println("<tr>");
														for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
															if (sqlRowSet.getString(k) != null) {
																if (sqlRowSet.getString(k).equalsIgnoreCase("passed")) {
																	out.println("<td style='word-break: break-all;'><span class='label label-success label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																} else if (sqlRowSet.getString(k).equalsIgnoreCase("failed")) {
																	out.println("<td style='word-break: break-all;'><span class='label label-danger label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																} else {
																	out.println("<td style='word-break: break-all;'>" + sqlRowSet.getString(k) + "</td>");
																}
															} else {
																out.println("<td style='word-break: break-all;'>" + sqlRowSet.getString(k) + "</td>");
															}		
														}
														out.println("</tr>");
													}
												} catch (NullPointerException e) {
										%><p>No Data Available in Table</p>
										<%
											}
										%>
									</tbody>
								</table>
							</div>
						</c:if>


						<c:if test="${columnSummaryExist eq 'Y'}">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Column Meta Data
										Comparision<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${schemaMatchingTransactionTable}','schemaMatchingColumnData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>

							<div class="portlet-body">
								<table
									class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
									<thead>
										<tr>
											<%-- <%
												try {
														SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("columnSummaryrowset");
														SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
														for (int i = 1; i <= metaData.getColumnCount(); i++) {
															//System.out.println(metaData.getColumnName(i));
															out.println("<th>" + metaData.getColumnLabel(i) + "</th>");
														}
													} catch (NullPointerException e) {
											%>
											<%
												}
											%> --%>
											
											<th>Id</th>
											<th>Date</th>
											<th>Run</th>
											<th>Common_Columns</th>
											<th>Table_Name</th>
											<th>Left_Only_Columns</th>
											<th>Right_Only_Columns</th>
											
										</tr>
									</thead>
									<tbody>
										<%
											try {
													SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("columnSummaryrowset");
													while (sqlRowSet.next()) {
														out.println("<tr>");
														for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
															if (sqlRowSet.getString(k) != null) {
																if (sqlRowSet.getString(k).equalsIgnoreCase("passed")) {
																	out.println("<td><span class='label label-success label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																} else if (sqlRowSet.getString(k).equalsIgnoreCase("failed")) {
																	out.println("<td><span class='label label-danger label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																} else {
																	out.println("<td>" + sqlRowSet.getString(k) + "</td>");
																}
															} else {
																out.println("<td>" + sqlRowSet.getString(k) + "</td>");
															}
														}
														out.println("</tr>");
													}
												} catch (NullPointerException e) {
										%><p>No Data Available in Table</p>
										<%
											}
										%>
									</tbody>
								</table>
							</div>
						</c:if>
						<c:if test="${recordSummaryExist eq 'Y'}">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Record Count Meta
										Data Comparision <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${schemaMatchingTransactionTable}','schemaMatchingCommon')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>

							<div class="portlet-body">
								<table
									class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
									<thead>
										<tr>
											<%-- <%
												try {
														SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("recordSummaryrowset");
														SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
														for (int i = 1; i <= metaData.getColumnCount(); i++) {
															//System.out.println(metaData.getColumnName(i));
															out.println("<th>" + metaData.getColumnLabel(i) + "</th>");
														}
													} catch (NullPointerException e) {
											%>
											<%
												}
											%> --%>
											<th>Id</th>
											<th>Date</th>
											<th>Run</th>
											<th>Common_Table_Name</th>
											<th>RC_Left</th>
											<th>RC_Right</th>
											<th>RC_Difference</th>
											<th>Status</th>
											
										</tr>
									</thead>
									<tbody>
										<%
											try {
													SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("recordSummaryrowset");
													DecimalFormat df = new DecimalFormat(",###");
													System.out.println("totalColumnCount = =>"+sqlRowSet.getMetaData().getColumnCount());
													//f.format(sqlRowSet.getDouble(k))
													while (sqlRowSet.next()) {
														out.println("<tr>");
														for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
															 if (sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("rc_difference")) {
																out.println("<td>" + Math.round(sqlRowSet.getDouble(k)) + "</td>");
															} 
															else if (sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("status")) {
																if (sqlRowSet.getString(k).equalsIgnoreCase("PASS")) {
																	out.println("<td><span class='label label-success label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																} else if (sqlRowSet.getString(k).equalsIgnoreCase("FAIL"))  {
																	out.println("<td><span class='label label-danger label-sm'>"
																			+ sqlRowSet.getString(k) + "</span></td>");
																}
																
															}
													
															else {
																
																if(sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("rc_left")){
																	//df.format(93883982)
																	out.println("<td>" + df.format(Double.parseDouble(sqlRowSet.getString(k))) + "</td>");
																	
																} else if(sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("rc_right")){
																	//df.format(93883982)
																	//String t = "90809.565";
																	out.println("<td>" + df.format(Double.parseDouble(sqlRowSet.getString(k))) + "</td>");
																	
																}
																else
																out.println("<td>" + sqlRowSet.getString(k) + "</td>");
															}	
															
														
														
													}
														out.println("</tr>");
												 }
											
												
												}catch (NullPointerException e) {
										%><p>No data available in Table</p>
										<%
											}
										%>
									</tbody>
								</table>
							</div>
						</c:if>



<c:if test="${recordSummaryExistuncommon eq 'Y'}">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Record Count - Uncommon Meta
										Data Comparision<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${schemaMatchingTransactionTable}','schemaMatchingUnCommon')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>
							
							
							<div class="portlet-body">
								<table
									class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
									<thead>
										<tr>
										<%-- 	<%
												try {
														SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("recordSummaryrowsetuncommon");
														SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
														for (int i = 1; i <= metaData.getColumnCount(); i++) {
															//System.out.println(metaData.getColumnName(i));
															out.println("<th>" + metaData.getColumnLabel(i) + "</th>");
														}
													} catch (NullPointerException e) {
											%>
											<%
												}
											%> --%>
											<th>Id</th>
											<th>Date</th>
											<th>Run</th>
											<th>Left_Only_Table_Name</th>
											<th>Left_Only_Count</th>
											<th>Right_Only_Table_Name</th>
											<th>Right_only_Count</th>
											
										</tr>
									</thead>
									<tbody>
									
										<%
											try {
													SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("recordSummaryrowsetuncommon");
													DecimalFormat df = new DecimalFormat(",###");
													while (sqlRowSet.next()) {
														out.println("<tr>");
														for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
															 String str="";
															 
															 if(sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("leftonly_count")){
																	//String s1 = "93883982.99";
																	out.println("<td>" + df.format(Double.parseDouble(sqlRowSet.getString(k))) + "</td>");
																	
															} else  if(sqlRowSet.getMetaData().getColumnName(k).equalsIgnoreCase("rightonly_count")){
																//String s1 = "93883982.99";
																out.println("<td>" + df.format(Double.parseDouble(sqlRowSet.getString(k))) + "</td>");
															}
															 else 
															if(sqlRowSet.getString(k).equals("-1")){
															out.println("<td>" +str + "</td>");
															}else {
															
																out.println("<td>" + sqlRowSet.getString(k) + "</td>");
															}
																
																
															
														}
														out.println("</tr>");
													}
												} catch (NullPointerException e) {
										%><p>No Data Available in Table</p>
										
										<%
											}
										%>
									
									</tbody>
									</table>
									</div>
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
						</c:if>










					</div>
					<!-- END EXAMPLE TABLE PORTLET-->
				</div>
			</div>
		</div>
	</div>
	<!-- close if -->
	<!-- close if -->
<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript">
</script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
		type="text/javascript">
</script>

 <script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
});

</script>
	<jsp:include page="downloadCsvReports.jsp"/>
	<jsp:include page="footer.jsp" />
</body>
</html>
