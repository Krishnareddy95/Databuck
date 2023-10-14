<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
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
								<span class="caption-subject bold ">Model Governance Dashboard for : ${appName}</span>
							</div>
						</div>
							<div class="portlet-body">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "></span>
							</div>
							<br/>
								<table
									class="table table-striped table-bordered  table-hover datatable-master"
									style="width: 100%;">
									<thead>
										<tr>
										<th>Model_id</th>
										<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
										<th>Model Governance Type</th>
										<th>Status</th>
										</tr>
									</thead>
									<tbody>
									
										<c:forEach items="${failedOfDecileEquality}" var="failedOfDecileEquality">
											<tr>
										<td>${failedOfDecileEquality}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${date}</td>
										<td>Decile Equality (${decileEqualityAppName})</td>
										<td><span class='label label-danger label-sm'>failed</span></td>
										</tr>
										</c:forEach>
										
										<c:forEach items="${allModelsOfDecileEquality}" var="allModelsOfDecileEquality">
											<tr>
										<td>${allModelsOfDecileEquality}</td>
										<td>${date}</td>
										<td>Decile Equality (${decileEqualityAppName})</td>
										<td><span class='label label-success label-sm'>passed</span></td>
										</tr>
										</c:forEach>
										
										<c:forEach items="${failedOfDecileConsistency}" var="failedOfDecileConsistency">
											<tr>
										<td>${failedOfDecileConsistency}</td>
										<td>${date}</td>
										<td>Decile Consistency (${decileConsistencyAppName})</td>
										<td><span class='label label-danger label-sm'>failed</span></td>
										</tr>
										</c:forEach>
										
										<c:forEach items="${allModelsOfDecileConsistency}" var="allModelsOfDecileConsistency">
											<tr>
										<td>${allModelsOfDecileConsistency}</td>
										<td>${date}</td>
										<td>Decile Consistency (${decileConsistencyAppName})</td>
										<td><span class='label label-success label-sm'>passed</span></td>
											</tr>
										</c:forEach>
										
										<c:forEach items="${failedOfScoreConsistency}" var="failedOfScoreConsistency">
											<tr>
										<td>${failedOfScoreConsistency}</td>
										<td>${date}</td>
										<td>Score Consistency (${scoreConsistencyAppName})</td>
										<td><span class='label label-danger label-sm'>failed</span></td>
										</tr>
										</c:forEach>
										
										<c:forEach items="${allModelsOfScoreConsistency}" var="allModelsOfScoreConsistency">
											<tr>
										<td>${allModelsOfScoreConsistency}</td>
										<td>${date}</td>
										<td>Score Consistency (${scoreConsistencyAppName})</td>
										<td><span class='label label-success label-sm'>passed</span></td>
											</tr>
										</c:forEach>
										
									</tbody>
								</table>
							</div>
							<br/><br/><br/>
							<%-- <div class="portlet-body">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">DECILE EQUALITY</span>
							</div>
							<br/>
								<table
									class="table table-striped table-bordered  table-hover datatable-master"
									style="width: 100%;">
									<thead>
										<tr>
											<%
											
                                String data1 = (String)request.getAttribute("decileEqualityTableTrue");
                                if (data1!=null && data1.equals("decileEqualityTableTrue")) {
                                %>
											<%
											try{
									SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("decileEqualityTable");
									SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
									for (int i = 1; i <= metaData.getColumnCount(); i++) {
										out.println("<th>" + metaData.getColumnName(i) + "</th>");
									}
											}catch(NullPointerException e)
											{
												%>
											<%
											}
											%>
											<%
											} else {
											%>
											<% }%>
										</tr>
									</thead>
									<tbody>
										<%
                             
                                if (data1!=null && data1.equals("decileEqualityTableTrue")) {
                                %>
										<%
										try{
								SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("decileEqualityTable");
								while (sqlRowSet.next()) {
									out.println("<tr>");
									for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
										if(sqlRowSet.getString(k)!=null){
											if(sqlRowSet.getString(k).equalsIgnoreCase("passed")){
												out.println("<td><span class='label label-success label-sm'>" + sqlRowSet.getString(k) + "</span></td>");
											}else if(sqlRowSet.getString(k).equalsIgnoreCase("failed")){
												out.println("<td><span class='label label-danger label-sm'>" + sqlRowSet.getString(k) + "</span></td>");
											}else{									
												out.println("<td>" + sqlRowSet.getString(k) + "</td>");
										}
										}else{
											out.println("<td>" + sqlRowSet.getString(k) + "</td>");
										}}
									out.println("</tr>");
								}}catch(NullPointerException e)
								{
									%><p>No data available in table</p>
										<%
								}
										%>
										<%
										} else {
										%>
											<p>No data available in table</p>
										<% }%>
									</tbody>
								</table>
							</div> --%>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
	</div>
	<!-- close if -->
	<!-- close if -->
	<jsp:include page="footer.jsp" />
</body>
</html>
