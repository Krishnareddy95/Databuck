<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.IdentityHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.databuck.bean.DataMatchingSummary"%>
<%@page import="java.util.List"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="viewDatatable.jsp" />
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap.min.css">
<script
	src="./assets/global/plugins/jquery.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
</head>
<body>
	<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
	<script type="text/javascript">
    google.load("visualization", "1", {
        packages : [ "corechart" ]
    });
    google.setOnLoadCallback(drawChart);
    function drawChart() {
        
        var data = google.visualization.arrayToDataTable([
            [ 'Date', 'Status' ],
            <c:forEach var="leftGraphobj" items="${leftGraph}">
          		['${leftGraphobj.date}',  ${leftGraphobj.status}],
    		</c:forEach>
        ]);

        var options = {
            title : 'Source1 Only Status',
            hAxis : {
                title : 'Date',
                titleTextStyle : {
                    color : '#333'
                }
            },
            vAxis : {
                minValue : 0
            },
            height : 250,
            width : 1000
        };

        var chart = new google.visualization.AreaChart(document
                .getElementById('left_summary_div'));
        chart.draw(data, options);

        var data = google.visualization.arrayToDataTable([
            [ 'Date', 'Status' ],
            <c:forEach var="rightGraphobj" items="${rightGraph}">
          		['${rightGraphobj.date}',  ${rightGraphobj.status}],
   			 </c:forEach>
        ]);

        var options = {
            title : 'Source2 Only Status',
            hAxis : {
                title : 'Date',
                titleTextStyle : {
                    color : '#333'
                }
            },
            vAxis : {
                minValue : 0
            },
            height : 250,
            width : 1000
        };

        var chart = new google.visualization.AreaChart(document
                .getElementById('right_summary_div'));
        chart.draw(data, options);

        
        var data = google.visualization.arrayToDataTable([ 
            [ 'Date', 'Status' ],
                <c:forEach var="unmatchedGraphobj" items="${unmatchedGraph}">
                  ['${unmatchedGraphobj.date}',  ${unmatchedGraphobj.status}],
            	</c:forEach>
             ]);

        var options = {
            title : 'Unmatched Status',
            hAxis : {
                title : 'Date',
                titleTextStyle : {
                    color : '#333'
                }
            },
            vAxis : {
                minValue : 0
            },
            height : 250,
            width : 1000
        };

        var chart = new google.visualization.AreaChart(document
                .getElementById('unmatched_summary_div'));
        chart.draw(data, options);
    }
</script>
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
								<span class="caption-subject bold ">Table Dashboard :
									${appName } </span>
							</div>
						</div>
						<div class="portlet-body">
							<div class="portlet-body">
								<table
									class="table table-striped table-bordered  table-hover">
									<tbody>
										<tr>
											<th>Key Metrics</th>
											<th>Measurement</th>
											<th>Status & RecordCount</th>
											<th>Percentage</th>
											<th>Threshold</th>
											<th>Summary</th>
										</tr>
										<%
											DecimalFormat df = new DecimalFormat(",###");

											Map<String, String> map = new IdentityHashMap<String, String>();
											Object obj = request.getAttribute("dataMatchingMap");
											if (obj != null) {
												map = (Map<String, String>) obj;
											}
										%>
										<tr>
											<td>Number of Records on Source 1 <%="  [ " + request.getAttribute("source1") + " ]"%>
											</td>
											<td>
												<%
													String totalRecordsInSource1 = map.get("totalRecordsInSource1");
													double sourceVal1 = Double.parseDouble(totalRecordsInSource1);
													//double sourceVal1 = 2387987.9;
													//out.println(map.get("totalRecordsInSource1"));
													out.println(df.format(sourceVal1));
												%>
											</td>
											<td><%=request.getAttribute("leftTotalRecord")%></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td>Number of Records on Source 2 <%="  [ " + request.getAttribute("source2") + " ]"%></td>
											<td>
												<%
													String totalRecordsInSource2 = map.get("totalRecordsInSource2");
													//double sourceVal2 = 45845676;
													double sourceVal2 = Double.parseDouble(totalRecordsInSource2);
													out.println(df.format(sourceVal2));
													//out.println(map.get("totalRecordsInSource2"));
												%>
											</td>
											<td><%=request.getAttribute("rightTotalRecord")%></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td>Records in Source 1 not in Source 2 <%="    [ " + request.getAttribute("source1") + " ]   "%></td>
											<td>
												<%
													String source1OnlyRecords = map.get("source1OnlyRecords");
													//double source1 = 2532655;
													double source1 = Double.parseDouble(source1OnlyRecords);
													out.println(df.format(source1));
													//out.println(map.get("source1OnlyRecords"));
												%>
											</td>
											<td>
												<%
													if (request.getAttribute("leftStatus").equals("ZeroCount")) {
												%> <span class="label label-danger label-sm">
													ZeroCount </span> <%
												 	} else {
												 		if (map.get("source1OnlyPercentage") != null) {
												 			Double percentage = Double.parseDouble(map.get("source1OnlyPercentage"));
												 			if (request.getAttribute("threshold") != null) {
												 				Double threshold = Double.parseDouble(request.getAttribute("threshold").toString());
												 				if (percentage > threshold) {
												 %> <span class="label label-danger label-sm"> Failed
											</span> <%
												 	} else {
												 %> <span class="label label-success label-sm">
													Passed </span> <%
												 	}
												 			}
												 		}
												 	}
												 %>
											</td>
											<td>
												<%
													out.println(map.get("source1OnlyPercentage"));
												%>
											</td>
											<td>${threshold }</td>
											<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
												id="left_summary"> <i class="fa fa-line-chart">
														Chart</i>
											</a></td>
										</tr>
										<tr>
											<td>Records in Source 2 not in Source 1 <%="  [ " + request.getAttribute("source2") + " ]"%></td>
											<td>
												<%
													String source2OnlyRecords = map.get("source2OnlyRecords");
													double source2 = Double.parseDouble(source2OnlyRecords);
													out.println(df.format(source2));
												%>
											</td>
											<td>
												<%
													if (request.getAttribute("rightStatus").equals("ZeroCount")) {
												%> <span class="label label-danger label-sm">
													ZeroCount </span> <%
												 	} else {
												
												 		if (map.get("source2OnlyPercentage") != null) {
												 			Double percentage = Double.parseDouble(map.get("source2OnlyPercentage"));
												 			if (request.getAttribute("threshold") != null) {
												 				Double threshold = Double.parseDouble(request.getAttribute("threshold").toString());
												 				if (percentage > threshold) {
												 %> <span class="label label-danger label-sm"> Failed
													</span> <%
												 	} else {
												 %> <span class="label label-success label-sm">
													Passed </span> <%
												 	}
												 			}
												 		}
												 	}
												 %>
											</td>
											<td>
												<%
													out.println(map.get("source2OnlyPercentage"));
												%>
											</td>
											<td>${threshold }</td>
											<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
												id="right_summary"> <i class="fa fa-line-chart">
														Chart</i>
											</a></td>
										</tr>
										<c:if test="${measurement eq 'true'}">
											<tr>
												<td>Number of Unmatched Item</td>
												<td>
													<%
														out.println(map.get("unmatchedRecords"));
													%>
												</td>
												<td>
													<%
														Object unmatchedStatus = map.get("unmatchedStatus");
															if (unmatchedStatus != null) {
																if (unmatchedStatus.toString().toLowerCase().equalsIgnoreCase("passed")) {
													%> <span class="label label-success label-sm">
														Passed </span> <%
													 	} else {
													 %> <span class="label label-danger label-sm">
														Failed </span> <%
													 	}
													 		}
													 %>
												</td>
												<td>
													<%
														out.println(map.get("unmatchedPercentage"));
													%>
												</td>
												<td>${threshold }</td>
												<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
													id="unmatched_summary"> <i class="fa fa-line-chart">
															Chart</i>
												</a></td>
											</tr>
										</c:if>
									</tbody>
								</table>

								<div class="portlet-body">
									<table
										class="table table-striped table-bordered  table-hover datatable_">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
												<th>Run</th>
												<c:if test="${measurement eq 'true'}">
													<th>Match Status</th>
													<th>Unmatched Records</th>
												</c:if>
												<th>Source 1 Record Count</th>
												<th>Source 2 Record Count</th>
												<th>Source 1 Only Records</th>
												<th>Source 1 Only Percentage</th>
												<th>Source 2 Only Records</th>
												<th>Source 2 Only Percentage</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="dmSummaryDataObj" items="${dmSummaryData}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${ dmSummaryDataObj.date}</td>
													<td>${dmSummaryDataObj.run}</td>
													<c:if test="${measurement eq 'true'}">
														<td><c:if
																test="${dmSummaryDataObj.status eq 'passed'}">
																<span class="label label-success label-sm">${dmSummaryDataObj.status}</span>
															</c:if> <c:if test="${dmSummaryDataObj.status eq 'failed'}">
																<span class="label label-danger label-sm">${dmSummaryDataObj.status}</span>
															</c:if></td>
														<td>${dmSummaryDataObj.unmatchedRecords }</td>
													</c:if>
													<%
														List<DataMatchingSummary> dataMatchingSummary = (List) request.getAttribute("dmSummaryData");

															//Object obj1 = dmSummaryDataObj.totalRecordsInSource1;
															String totalrecord1 = null;
															String totalrecord2 = null;
															for (DataMatchingSummary l : dataMatchingSummary) {
																totalrecord1 = l.getTotalRecordsInSource1().toString();
																totalrecord2 = l.getTotalRecordsInSource2().toString();
															}

															//double Sourceval1 = 837983;
															//double Sourceval2 = 456231574;
															double Sourceval1 = Double.parseDouble(totalrecord1);
															double Sourceval2 = Double.parseDouble(totalrecord2);
													%>

													<td><%=df.format(Sourceval1)%></td>
													<td><%=df.format(Sourceval2)%></td>
													<td>${dmSummaryDataObj.source1OnlyRecords }</td>
													<td>${dmSummaryDataObj.soure1OnlyPercenage }</td>
													<td>${dmSummaryDataObj.source2OnlyRecods }</td>
													<td>${dmSummaryDataObj.soure2OnlyPercenage }</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>


								<!--Div that will hold the chart-->
								<div id="left_summary_div"
									style="height: 300px; width: 100%; margin: 0 auto;"
									class="dashboard-summary-chart"></div>

								<!--Div that will hold the chart-->
								<div id="right_summary_div"
									style="height: 300px; width: 100%; margin: 0 auto; display: none;"
									class="dashboard-summary-chart"></div>

								<div id="unmatched_summary_div"
									style="height: 300px; width: 100%; margin: 0 auto; display: none;"
									class="dashboard-summary-chart"></div>
								<br />
								<c:if test="${rollSummaryTableName ne null }">
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Summary (Total
												Records=${rollSummary_result_Count}) <a onClick="validateHrefVulnerability(this)" 
												href="downloadRollDataCsv?appId=${appId}&tableName=${rollSummaryTableName}&rollTargetSchemaId=${rollTargetSchemaId}"
												class="CSVStatus" id="UnmatchedDiv">(Download CSV)</a>
											</span>
											<a onClick="validateHrefVulnerability(this)"  href="${averReportUILink}" style="padding-left: 10px;">AverReportUI</a>
										</div>
									</div>
									<div class="portlet-body">
										<input type="hidden" id="tableName1" value="${rollSummary_Table_Name}">
										<input type="hidden" id="appId" value="${appId}">
										<input type="hidden" id="rollTargetSchemaId" value="${rollTargetSchemaId}">
										<table id="RollTranDetailAllTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("rollSummary_result") != null) {
																SqlRowSet rs2 = (SqlRowSet) request.getAttribute("rollSummary_result");
																SqlRowSetMetaData metadata2 = rs2.getMetaData();
																for (int i = 1; i <= metadata2.getColumnCount(); i++) {
																	String columnNameCheck = metadata2.getColumnName(i);

																	if (columnNameCheck.equalsIgnoreCase("fe_leftRecordCount")) {
																		out.println("<th>" + "FE_Left_Record_Count" + "</th>");
																	} else if (columnNameCheck.equalsIgnoreCase("fe_rightRecordCount")) {

																		out.println("<th>" + "FE_Right_Record_Count" + "</th>");
																	} else if (columnNameCheck.equalsIgnoreCase("fe_leftMeasurementSum")) {

																		out.println("<th>" + "FE_Left_Measurement_Sum" + "</th>");
																	} else if (columnNameCheck.equalsIgnoreCase("fe_rightMeasurementSum")) {

																		out.println("<th>" + "FE_Right_Measurement_Sum" + "</th>");
																	} else if (columnNameCheck.equalsIgnoreCase("fe_valDiff")) {

																		out.println("<th>" + "FE_Val_Diff" + "</th>");
																	} else {

																		out.println("<th>" + metadata2.getColumnName(i) + "</th>");
																	}

																}
															}
													%>

												</tr>
											</thead>
										</table>
									</div>
								</c:if>
								<br />

								<c:if test="${RecordCountMatching ne null }">
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Record Count
												Matching Table </span>
										</div>
									</div>
									<br />
									<div class="portlet-body">
										<table
											class="table table-striped table-bordered table-hover dataTable no-footer "
											style="width: 100%;">
											<thead>
												<tr>
													<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
													<th>Run</th>
													<th>Source 1 Record Count <%="  [ " + request.getAttribute("source1") + " ]"%></th>
													<th>Source 2 Record Count <%="  [ " + request.getAttribute("source2") + " ]"%></th>
													<th>RC Difference</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="dmSummaryDataObj" items="${dmSummaryData}">
													<tr>
														<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${ dmSummaryDataObj.date}</td>
														<td>${dmSummaryDataObj.run}</td>

														<%
															List<DataMatchingSummary> dataMatchingSummary = (List) request.getAttribute("dmSummaryData");

																	String totalrecord1 = null;
																	String totalrecord2 = null;
																	for (DataMatchingSummary l : dataMatchingSummary) {
																		totalrecord1 = l.getTotalRecordsInSource1().toString();
																		totalrecord2 = l.getTotalRecordsInSource2().toString();
																	}
																	double Sourceval1 = Double.parseDouble(totalrecord1);
																	double Sourceval2 = Double.parseDouble(totalrecord2);
														%>
														<td><%=df.format(Sourceval1)%></td>
														<td><%=df.format(Sourceval2)%></td>
														<td>${dmSummaryDataObj.rcDifference }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
	</div>
	<!-- close if -->
	<!-- close if -->
	<jsp:include page="downloadCsvReports.jsp" />
	<jsp:include page="footer.jsp" />
</body>

<script type="text/javascript">
jQuery(document).ready(function() {
	var table;
	var tableName=$( "#tableName" ).val();
	var appId=$( "#appId" ).val();
			   
			    tableName=$("#tableName1").val();
				console.log("tableName1");
				table = $('#RollTranDetailAllTable').dataTable({
					  	"bPaginate": true,
					  	"order": [ 0, 'asc' ],
					  	"bInfo": true,
					  	"iDisplayStart":0,
					  	"bProcessing" : true,
					 	"bServerSide" : true,
					 	"dataSrc": "",
					 	'sScrollX' : true,
					 	"sAjaxSource" : path+"/getRollDMSummaryData?appId="+appId+"&tableName="+tableName,
					 	"dom": 'C<"clear">lfrtip',
						colVis: {
							"align": "right",
				            restore: "Restore",
				            showAll: "Show all",
				            showNone: "Show none",
							order: 'alpha'
				        },
					    "language": {
				            "infoFiltered": ""
				        },
				        "dom": 'Cf<"toolbar"">rtip',
				
				      });
	});
</script>
</html>

