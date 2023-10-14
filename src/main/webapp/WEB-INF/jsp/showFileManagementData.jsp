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
								<span class="caption-subject bold ">File Management Table for : ${appName}</span>
							</div>
						</div>
							<div class="portlet-body">
								<input type="hidden" id="tableName1"
									value="${table_Name}">
								<table id="TranDetailAllTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<%
											
                                String data = (String)request.getAttribute("fileManagementTableTrue");
                                if (data!=null && data.equals("fileManagementTableTrue")) {
                                %>
											<%
											try{
									SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("fileManagementTable");
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
                             
                                if (data!=null && data.equals("fileManagementTableTrue")) {
                                %>
										<%
										try{
								SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("fileManagementTable");
								while (sqlRowSet.next()) {
									out.println("<tr>");
									for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
										out.println("<td>" + sqlRowSet.getString(k) + "</td>");
									}
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
							</div>
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
			   
			    tableName=$("#tableName1").val();
				//alert(tableName);
				console.log("tableName1");
				table = $('#TranDetailAllTable').dataTable({
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
				
	});

	
</script>

</html>
