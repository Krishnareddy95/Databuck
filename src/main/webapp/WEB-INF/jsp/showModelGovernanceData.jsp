<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="viewDatatable.jsp" />
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
								<span class="caption-subject bold ">Model Governance
									Table for : ${appName}</span>
							</div>
						</div>
						<c:if test="${DecileConsistency eq true }">
							<div class="portlet-body">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">${DECILECONSISTENCY}</span>
								</div>
								<br />
								<input type="hidden" id="tableName"
									value="${tableName}">
								<table id="DECILECONSISTENCYTABLE"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<%
												String data = (String) request.getAttribute("decileConsistencyTableTrue");
													if (data != null && data.equals("decileConsistencyTableTrue")) {
											%>
											<%
												try {
															SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("decileConsistencyTable");
															SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
															for (int i = 1; i <= metaData.getColumnCount(); i++) {
																out.println("<th>" + metaData.getColumnName(i) + "</th>");
															}
														} catch (NullPointerException e) {
											%>
											<%
												}
											%>
											<%
												} else {
											%>
											<%
												}
											%>
										</tr>
									</thead>
									<%-- <tbody>
										<%
											if (data != null && data.equals("decileConsistencyTableTrue")) {
										%>
										<%
											try {
														SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("decileConsistencyTable");
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
										%><p>No data available in table</p>
										<%
											}
										%>
										<%
											} else {
										%>
										<p>No data available in table</p>
										<%
											}
										%>
									</tbody> --%>
								</table>
							</div>
						</c:if>

						<br />
						<c:if test="${DecileEquality eq true }">
							<div class="portlet-body">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">${DECILEEQUALITY}</span>
								</div>
								<br />
								<input type="hidden" id="tableName1"
									value="${tableName1}">
								<table id="DECILEEQUALITYYTABLE"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<%
												String data1 = (String) request.getAttribute("decileEqualityTableTrue");
													if (data1 != null && data1.equals("decileEqualityTableTrue")) {
											%>
											<%
												try {
															SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("decileEqualityTable");
															SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
															for (int i = 1; i <= metaData.getColumnCount(); i++) {
																out.println("<th>" + metaData.getColumnName(i) + "</th>");
															}
														} catch (NullPointerException e) {
											%>
											<%
												}
											%>
											<%
												} else {
											%>
											<%
												}
											%>
										</tr>
									</thead>
									<%-- <tbody>
										<%
											if (data1 != null && data1.equals("decileEqualityTableTrue")) {
										%>
										<%
											try {
														SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("decileEqualityTable");
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
										%><p>No data available in table</p>
										<%
											}
										%>
										<%
											} else {
										%>
										<p>No data available in table</p>
										<%
											}
										%>
									</tbody> --%>
								</table>
							</div>
						</c:if>

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
<script type="text/javascript">

var table;


jQuery(document).ready(function() {
	console.log("hello");
	var tableName=$( "#tableName" ).val();
	//alert(tableName);
			   
			    tableName=$("#tableName").val();
				//alert(tableName);
				console.log(tableName);
				table = $('#DECILECONSISTENCYTABLE').dataTable({
					  	"bPaginate": true,
					  	"order": [ 0, 'asc' ],
					  	"bInfo": true,
					  	"iDisplayStart":0,
					  	"bProcessing" : true,
					 	"bServerSide" : true,
					 	"dataSrc": "",
					 	'sScrollX' : true,
					 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true",
					 	"dom": 'C<"clear">lfrtip',
						colVis: {
							"align": "right",
				            restore: "Restore",
				            showAll: "Show all",
				            showNone: "Show none",
							order: 'alpha'
							//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
				        },
					    "language": {
				            "infoFiltered": ""
				        },
				        "dom": 'Cf<"toolbar"">rtip',
				
				      });
				
				  tableName=$("#tableName1").val();
					//alert(tableName);
					console.log(tableName);
					table = $('#DECILEEQUALITYYTABLE').dataTable({
						  	"bPaginate": true,
						  	"order": [ 0, 'asc' ],
						  	"bInfo": true,
						  	"iDisplayStart":0,
						  	"bProcessing" : true,
						 	"bServerSide" : true,
						 	"dataSrc": "",
						 	'sScrollX' : true,
						 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName,
						 	"dom": 'C<"clear">lfrtip',
							colVis: {
								"align": "right",
					            restore: "Restore",
					            showAll: "Show all",
					            showNone: "Show none",
								order: 'alpha'
								//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
					        },
						    "language": {
					            "infoFiltered": ""
					        },
					        "dom": 'Cf<"toolbar"">rtip',
					
					      });
					
					 
				
	});


</script>

</html>
