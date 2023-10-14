<%@page import="com.databuck.bean.PrimaryMatchingSummary"%>
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
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
<script src="./assets/global/plugins/jquery.min.js"></script>
<script src="./assets/global/plugins/bootstrap.min.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script src="./assets/global/plugins/datatables-fixedcolumns-3.0.2"></script>
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
          ['${leftGraphobj.date}',  ${leftGraphobj.leftOnlyStatus}],
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
          ['${rightGraphobj.date}',  ${rightGraphobj.rightOnlyStatus}],
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
                  ['${unmatchedGraphobj.date}',  ${unmatchedGraphobj.unMatchedStatus}],
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




	<%
		//System.out.println("showPrimaryKeyMatching" + request.getAttribute("Left_Only"));
	%>
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
											<th>Status & Record Count</th>
											<th>Percentage</th>
											<th>Threshold</th>
											<th>Summary</th>
										</tr>
										<%
											DecimalFormat df = new DecimalFormat(",###");

											Map<String, String> map = new IdentityHashMap<String, String>();
											Object obj = request.getAttribute("primaryKeyMatchingMap");
											//System.out.println("obj ==>"+obj.toString());
											if (obj != null) {
												map = (Map<String, String>) obj;
											}

											System.out.println("############  primaryKeyMatchingMap------" + map.toString());
										%>
										<tr>
											<!-- request.getAttribute("leftOnlyCount") -->
											<td>Number of Records on Left <%="  [ " + request.getAttribute("source1") + " ]"%>
											</td>
											<td>
												<%
													System.out.println("####### request.getAttribute('leftOnlyCount') ---" + map.get("leftOnlyCount"));
													String totalRecordsInSource1 = map.get("leftTotalCount");
													double sourceVal1 = Double.parseDouble(totalRecordsInSource1);
													//double sourceVal1 = 2387987.9;
													//out.println(map.get("totalRecordsInSource1"));  showing null
													out.println(df.format(sourceVal1));
												%>
											</td>
											<td><%=map.get("leftTotalCount")%></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td>Number of Records on Right <%="  [ " + request.getAttribute("source2") + " ]"%></td>
											<td>
												<%
													String totalRecordsInSource2 = (String) map.get("rightTotalCount");
													//double sourceVal2 = 45845676;
													System.out.println("%%%%%%%%sourceVal2 ==>> " + totalRecordsInSource2);
													double sourceVal2 = Double.parseDouble(totalRecordsInSource2);
													out.println(df.format(sourceVal2));
													//out.println(map.get("totalRecordsInSource2")); //showing null
												%>
											</td>
											<td><%=map.get("rightTotalCount")%></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td>Records in Left not in Right <%="    [ " + request.getAttribute("source1") + " ]   "%></td>
											<td>
												<%
													String source1OnlyRecords = (String) map.get("leftOnlyCount");
													//double source1 = 2532655;
													double source1 = Double.parseDouble(source1OnlyRecords);
													out.println(df.format(source1));
													//out.println(map.get("source1OnlyRecords")); showing null
												%>
											</td>
											<td>
												<%
													System.out.println("************* request.getAttribute('leftOnlyStatus') =>" + map.get("leftOnlyStatus"));
													if (map.get("leftOnlyStatus") == null) {
												%> <span class="label label-danger label-sm">
													ZeroCount </span>
    <%
 	} else {
 		if (map.get("leftOnlyPercentage") != null) {
 			Double percentage = Double.parseDouble(map.get("leftOnlyPercentage"));
 			//Double threshold = 2.0;
 			if (request.getAttribute("threshold") != null) {
 				Double threshold = Double.parseDouble(request.getAttribute("threshold").toString());
 				if (percentage > threshold) {
 %> <span class="label label-danger label-sm"> Failed </span> <%
 	} else {
 %> <span class="label label-success label-sm"> Passed </span> <%
 	}
 			}
 		}
 	}
 %>
											</td>
											<td>
												<%
													out.println(map.get("leftOnlyPercentage"));
												%>
											</td>
											<td>${threshold }</td>
											<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
												id="left_summary"> <i class="fa fa-line-chart">
														Chart</i>
											</a></td>
										</tr>
										<tr>
											<td>Records in Right not in Left <%="  [ " + request.getAttribute("source2") + " ]"%></td>
											<td>
												<%
													String source2OnlyRecords = map.get("rightOnlyCount");
													//double source2 = 8897654;
													double source2 = Double.parseDouble(source2OnlyRecords);
													out.println(df.format(source2));
													//out.println(map.get("source2OnlyRecords"));
												%>
											</td>
											<td>
												<%
													if (map.get("rightOnlyStatus") == null) {
												%> <span class="label label-danger label-sm">
													ZeroCount </span> <%
 	} else {

 		if (map.get("rightOnlyPercentage") != null) {
 			Double percentage = Double.parseDouble(map.get("rightOnlyPercentage"));
 			//Double threshold = 2.0;
 			if (request.getAttribute("threshold") != null) {
 				Double threshold = Double.parseDouble(request.getAttribute("threshold").toString());
 				if (percentage > threshold) {
 %> <span class="label label-danger label-sm"> Failed </span> <%
 	} else {
 %> <span class="label label-success label-sm"> Passed </span> <%
 	}
 			}
 		}
 	}
 %>
											</td>
											<td>
												<%
													out.println(map.get("rightOnlyPercentage"));
												%>
											</td>
											<td>${threshold }</td>
											<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
												id="right_summary"> <i class="fa fa-line-chart">
														Chart</i>
											</a></td>
										</tr>
											<tr>
												<td>Number of Unmatched Item</td>
												<td>
													<%
														out.println(map.get("unMatchedCount"));
													%>
												</td>
												<td>
													<%
														Object unmatchedStatus = map.get("unMatchedStatus");
															if (unmatchedStatus != null) {
																if (unmatchedStatus.toString().toLowerCase().equalsIgnoreCase("passed")) {
													%> <span class="label label-success label-sm">
														Passed </span> <%
 	} else {
 %> <span class="label label-danger label-sm"> Failed </span> <%
 	}
 		}
 %>
												</td>
												<td>
													<%
														out.println(map.get("unMatchedPercentage"));
													%>
												</td>
												<td>${threshold }</td>
												<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
													id="unmatched_summary"> <i class="fa fa-line-chart">
															Chart</i>
												</a></td>
											</tr>
											<%-- <tr>
											    <td>Number of Matched Item</td>
											    <td>
											    <%
                                                    out.println(map.get("totalMatchedCount"));
                                                %>
											    </td>
											    <td></td>
											    <td>
											    <%
                                                    out.println(map.get("totalMatchedPercentage"));
                                                %>
											    </td>
                                                <td></td>
                                                <td></td>
											</tr> --%>
									</tbody>
								</table>

								<br><br>

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
												<th>Left Record Count</th>
												<th>Right Record Count</th>
												<th>Left Only Records</th>
												<th>Left Only Percentage</th>
												<th>Right Only Records</th>
												<th>Right Only Percentage</th>
												<th>Left Null Count</th>
												<th>Right Null Count</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="dmSummaryDataObj"
												items="${dmPrimarySummaryData}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${ dmSummaryDataObj.date}</td>
													<td>${dmSummaryDataObj.run}</td>
													<%-- 	<c:if test="${measurement eq 'true'}">
														<td><c:if
																test="${dmSummaryDataObj.leftOnlyStatus eq 'passed'}">
																<span class="label label-success label-sm">${dmSummaryDataObj.leftOnlyStatus}</span>
															</c:if> <c:if test="${dmSummaryDataObj.leftOnlyStatus eq 'failed'}">
																<span class="label label-danger label-sm">${dmSummaryDataObj.leftOnlyStatus}</span>
															</c:if></td>
														<td>${dmSummaryDataObj.unmatchedRecords }</td>
													</c:if> --%>
													<%
														List<PrimaryMatchingSummary> dataMatchingSummary = (List) request.getAttribute("dmPrimarySummaryData");

															//Object obj1 = dmSummaryDataObj.totalRecordsInSource1;
															String totalrecord1 = null;
															String totalrecord2 = null;
															for (PrimaryMatchingSummary l : dataMatchingSummary) {
																totalrecord1 = l.getLeftTotalCount().toString();
																totalrecord2 = l.getRightTotalCount().toString();
															}

															//double Sourceval1 = 837983;
															//double Sourceval2 = 456231574;
															double Sourceval1 = Double.parseDouble(totalrecord1);
															double Sourceval2 = Double.parseDouble(totalrecord2);
													%>


													<td>${dmSummaryDataObj.leftTotalCount }</td>
													<td>${dmSummaryDataObj.rightTotalCount }</td>
													<%-- <td><%=df.format(Sourceval1)%></td>
													<td><%=df.format(Sourceval2)%></td> --%>
													<td>${dmSummaryDataObj.leftOnlyCount }</td>
													<td>${dmSummaryDataObj.leftOnlyPercentage }</td>
													<td>${dmSummaryDataObj.rightOnlyCount }</td>
													<td>${dmSummaryDataObj.rightOnlyPercentage }</td>
													<td>${dmSummaryDataObj.leftNullCount }</td>
                                                    <td>${dmSummaryDataObj.rightNullCount }</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>

                                <br>

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
								
								<!-- Download CSV Code -->
								
								<c:if test="${unMatchedTableName ne null }">
									<%-- 	<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Unmatched (Total
												Records=${Unmatched_result_Count}) <a onClick="validateHrefVulnerability(this)" 
												href="downloadCsv?tableName=${unMatchedTableName}"
												class="CSVStatus" id="UnmatchedDiv">(Download CSV)</a>
											</span>
										</div>
									</div> --%>
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Unmatched (Total
												Records=${Unmatched_result_Count}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${unMatchedTableName}','unMatchTableData')"

												href="#" class="CSVStatus" id="">(Download CSV)</a>
											</span>
										</div>
									</div>
									<br />
									<div class="progress percentagebar hidden" id="UnmatchedDivBar">
										<div class="progress">
											<div class="progress-bar progressbar" role="progressbar"
												style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
												aria-valuemax="100">5%</div>
										</div>
									</div>
									<div class="portlet-body">
										<input type="hidden" id="tableName1"
											value="${Unmatched_Table_Name}">
										<table id="TranDetailAllTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("Unmatched_result") != null) {
																SqlRowSet rs2 = (SqlRowSet) request.getAttribute("Unmatched_result");
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
											<%-- <tbody>
												<%
													DecimalFormat numberFormat = new DecimalFormat("#.00");
															while (rs2.next()) {
																out.println("<tr>");
																for (int i = 1; i <= metadata2.getColumnCount(); i++) {
																	if (metadata2.getColumnTypeName(i).equalsIgnoreCase("double")
																			|| metadata2.getColumnTypeName(i).equalsIgnoreCase("decimal")
																			|| metadata2.getColumnTypeName(i).equalsIgnoreCase("float")) {
																		//System.out.println("rs.getString(i):" + rs2.getString(i));
																		out.println("<td>" + numberFormat.format(rs2.getDouble(i)) + "</td>");
																	} else {
																		out.println("<td>" + rs2.getString(i) + "</td>");
																	}
																}
																out.println("</tr>");
															}
														}
												%>
											</tbody> --%>
										</table>
									</div>
								</c:if>
								<br />

								<c:if test="${unmatchedGroupbyTableData ne null }">
									<%-- <div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Unmatched GroupBy
												(Total Records=${unmatchedGroupbyRecordCount}) <a onClick="validateHrefVulnerability(this)" 
												href="downloadCsv?tableName=${unMatchedGroupByTableName}"
												class="CSVStatus" id="UnmatchedGroupByDiv">(Download
													CSV)</a>
											</span>
										</div>
									</div> --%>
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Unmatched GroupBy
												(Total Records=${unmatchedGroupbyRecordCount}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${unMatchedGroupByTableName}','unMatchGroupByTableData')"

												href="#" class="CSVStatus" id="">(Download CSV)</a>
											</span>
										</div>
									</div>
									<br />
									<div class="progress percentagebar hidden"
										id="UnmatchedGroupByDivBar">
										<div class="progress">
											<div class="progress-bar progressbar" role="progressbar"
												style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
												aria-valuemax="100">5%</div>
										</div>
									</div>
									<div class="portlet-body">
										<input type="hidden" id="tableName4"
											value="${UnmatchedGroupBy_Table_Name}">
										<table id="UnmatchedGroupByTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("unmatchedGroupbyTableData") != null) {
																SqlRowSet rs2 = (SqlRowSet) request.getAttribute("unmatchedGroupbyTableData");
																SqlRowSetMetaData metadata2 = rs2.getMetaData();
																for (int i = 1; i <= metadata2.getColumnCount(); i++) {
																	out.println("<th>" + metadata2.getColumnName(i) + "</th>");
																}
															}
													%>

												</tr>
											</thead>
											<%-- <tbody>
												<%
													DecimalFormat numberFormat = new DecimalFormat("#.00");
															while (rs2.next()) {
																out.println("<tr>");
																for (int i = 1; i <= metadata2.getColumnCount(); i++) {
																	if (metadata2.getColumnTypeName(i).equalsIgnoreCase("double")
																			|| metadata2.getColumnTypeName(i).equalsIgnoreCase("decimal")
																			|| metadata2.getColumnTypeName(i).equalsIgnoreCase("float")) {
																		//System.out.println("rs.getString(i):" + rs2.getString(i));
																		out.println("<td>" + numberFormat.format(rs2.getDouble(i)) + "</td>");
																	} else {
																		out.println("<td>" + rs2.getString(i) + "</td>");
																	}
																}
																out.println("</tr>");
															}
														}
												%>
											</tbody> --%>
										</table>
									</div>
								</c:if>
								<br /> <br />
								<c:if test="${Unmatched_Anamoly_result ne null }">
									<div class="portlet-title">
										<input type="hidden" id="tableName6"
											value="${Unmatched_Anamoly_Table_Name}">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Unmatched
												Anomaly(Total Records=${Unmatched_Anamoly_result_Count}) </span>
										</div>
									</div>
									<div class="portlet-body">
										<table id="UnmatchedAnamolyTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("Unmatched_Anamoly_result") != null) {
																SqlRowSet rs1 = (SqlRowSet) request.getAttribute("Unmatched_Anamoly_result");
																SqlRowSetMetaData metadata1 = rs1.getMetaData();
																for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																	out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																}
															}
													%>

												</tr>
											</thead>
											<%-- <tbody>
													<%
														DecimalFormat numberFormat = new DecimalFormat("#.00");
																	if (rs1.first()) {
																		do {
																			out.println("<tr>");
																			for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																				if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																					//System.out.println("rs.getString(i):" + rs1.getString(i));
																					out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																				} else {
																					out.println("<td>" + rs1.getString(i) + "</td>");
																				}
																			}
																			out.println("</tr>");
																		} while (rs1.next());
																	}

																}
													%>
												</tbody> --%>
										</table>
									</div>
								</c:if>
								<c:if test="${leftOnlyTableName ne null }">
									<%-- 	<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Left_Only <%="    [ " + request.getAttribute("source1") + " ]   "%>(Total
												Records=${Left_Only_Count}) <a onClick="validateHrefVulnerability(this)" 
												href="downloadCsv?tableName=${leftOnlyTableName }"
												class="CSVStatus" id="leftOnlyDiv">(Download CSV)</a>
											</span>
										</div>
									</div> --%>
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Left_Only <%="    [ " + request.getAttribute("leftOnlyCount") + " ]   "%>(Total
												Records=${Left_Only_Count}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${leftOnlyTableName}','DataMatchingLeft')"

												href="#" class="CSVStatus" id="">(Download CSV)</a>
											</span>
										</div>
									</div>
									<br />
									<div class="progress percentagebar hidden" id="leftOnlyDivBar">
										<div class="progress">
											<div class="progress-bar progressbar" role="progressbar"
												style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
												aria-valuemax="100">5%</div>
										</div>
									</div>

									<div class="portlet-body">
										<input type="hidden" id="tableName2"
											value="${leftOnlyTableName}">
										<table id="leftOnlyTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("Left_Only") != null) {
																SqlRowSet rs = (SqlRowSet) request.getAttribute("Left_Only");
																SqlRowSetMetaData metadata = rs.getMetaData();
																for (int i = 1; i <= metadata.getColumnCount(); i++) {
																	out.println("<th>" + metadata.getColumnName(i) + "</th>");
																}
															}
													%>

												</tr>
											</thead>
											<%-- <tbody>
												<%
													DecimalFormat numberFormat = new DecimalFormat("#.00");
															while (rs.next()) {
																out.println("<tr>");
																for (int i = 1; i <= metadata.getColumnCount(); i++) {
																	if (metadata.getColumnTypeName(i).equalsIgnoreCase("double")
																			|| metadata.getColumnTypeName(i).equalsIgnoreCase("decimal")
																			|| metadata.getColumnTypeName(i).equalsIgnoreCase("float")) {
																		//System.out.println("rs.getString(i):" + rs.getString(i));
																		out.println("<td>" + numberFormat.format(rs.getDouble(i)) + "</td>");
																	} else {
																		out.println("<td>" + rs.getString(i) + "</td>");
																	}

																}
																out.println("</tr>");
															}
														}
												%>
											</tbody> --%>
										</table>
									</div>
								</c:if>
								<br /> <br />
								<c:if test="${rightOnlyTableName ne null }">
									<%-- <div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Right_Only <%="   [ " + request.getAttribute("source2") + " ]  "%>
												(Total Records=${Right_Only_Count}) <a onClick="validateHrefVulnerability(this)" 
												href="downloadCsv?tableName=${rightOnlyTableName }"
												class="CSVStatus" id="RightOnlyDiv">(Download CSV)</a>
											</span>
										</div>
									</div> --%>
									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Right_Only <%="   [ " + request.getAttribute("source2") + " ]  "%>
												(Total Records=${Right_Only_Count}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${rightOnlyTableName}','DataMatchingRight')"

												href="#" class="CSVStatus" id="">(Download CSV)</a>
											</span>
										</div>
									</div>
									<br />
									<div class="progress percentagebar hidden" id="RightOnlyDivBar">
										<div class="progress">
											<div class="progress-bar progressbar" role="progressbar"
												style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
												aria-valuemax="100">5%</div>
										</div>
									</div>
									<div class="portlet-body">
										<input type="hidden" id="tableName3"
											value="${rightOnlyTableName}">
										<table id="rightOnlyTable"
											class="table table-striped table-bordered  table-hover"
											style="width: 100%;">
											<thead>
												<tr>
													<%
														if (request.getAttribute("Right_Only") != null) {
																SqlRowSet rs1 = (SqlRowSet) request.getAttribute("Right_Only");
																SqlRowSetMetaData metadata1 = rs1.getMetaData();
																for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																	out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																}
															}
													%>

												</tr>
											</thead>
											<%-- <tbody>
												<%
													DecimalFormat numberFormat = new DecimalFormat("#.00");
															while (rs1.next()) {
																out.println("<tr>");
																for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																	if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																			|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																			|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																		//System.out.println("rs.getString(i):" + rs1.getString(i));
																		out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																	} else {
																		out.println("<td>" + rs1.getString(i) + "</td>");
																	}
																}
																out.println("</tr>");
															}
														}
												%>
											</tbody> --%>
										</table>
									</div>
								</c:if>
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
															List<PrimaryMatchingSummary> dataMatchingSummary = (List) request.getAttribute("dmSummaryData");

																	//Object obj1 = dmSummaryDataObj.totalRecordsInSource1;
																	String totalrecord1 = null;
																	String totalrecord2 = null;
																	for (PrimaryMatchingSummary l : dataMatchingSummary) {
																		totalrecord1 = l.getLeftTotalCount().toString();
																		totalrecord2 = l.getRightTotalCount().toString();
																	}
																	double Sourceval1 = Double.parseDouble(totalrecord1);
																	//double Sourceval1 = 837983;
																	//double Sourceval2 = 456231574;
																	double Sourceval2 = Double.parseDouble(totalrecord2);

																	System.out.println("Sourceval1 =>" + Sourceval1);
																	System.out.println("Sourceval1 =>" + Sourceval2);

																	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Sourceval1 => " + df.format(Sourceval1));
														%>
														<td><%=df.format(Sourceval1)%></td>
														<td><%=df.format(Sourceval2)%></td>
														<%-- 	 <td>${dmSummaryDataObj.totalRecordsInSource1 }</td>
														 <td>${dmSummaryDataObj.totalRecordsInSource2 }</td>   --%>
														<td>${dmSummaryDataObj.rcDifference }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</c:if>
								<c:if test="${PrimaryKeyMatching ne null }">

									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Primary_Unmatched
												(Total Records=${Primary_Unmatched_Count}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${PrimaryUnMatchedTableName}','PrimaryUnMatchTableData')"

												href="#" class="CSVStatus" id="">(Download CSV)</a>
											</span>
										</div>
									</div>
									<br />
									<c:if test="${Primary_Unmatched_result ne null }">
										<div class="portlet-body">
											<input type="hidden" id="tableName7"
												value="${PrimaryUnMatchedTableName}">
											<table id="primaryUnmatchedTable"
												class="table table-striped table-bordered  table-hover"
												style="width: 100%;">
												<thead>
													<tr>
														<%
															if (request.getAttribute("Primary_Unmatched_result") != null) {
																		SqlRowSet rs1 = (SqlRowSet) request.getAttribute("Primary_Unmatched_result");
																		SqlRowSetMetaData metadata1 = rs1.getMetaData();
																		for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																			out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																		}
																	}
														%>

													</tr>
												</thead>
												<%-- <tbody>
													<%
														DecimalFormat numberFormat = new DecimalFormat("#.00");
																	if (rs1.first()) {
																		do {
																			out.println("<tr>");
																			for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																				if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																					//System.out.println("rs.getString(i):" + rs1.getString(i));
																					out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																				} else {
																					out.println("<td>" + rs1.getString(i) + "</td>");
																				}
																			}
																			out.println("</tr>");
																		} while (rs1.next());
																	}

																}
													%>
												</tbody> --%>
											</table>
										</div>
									</c:if>
									<c:if test="${primary_Unmatched_Anamoly_result ne null }">
										<div class="portlet-title">
											<div class="caption font-red-sunglo">
												<span class="caption-subject bold ">Primary Unmatched
													Anomaly(Total
													Records=${primary_Unmatched_Anamoly_result_Count}) </span> <input
													type="hidden" id="tableName5"
													value="${primaryUnMatchedAnamoly}">
											</div>
										</div>
										<div class="portlet-body">
											<table id="primaryUnmatchedAnamolyTable"
												class="table table-striped table-bordered  table-hover"
												style="width: 100%;">
												<thead>
													<tr>
														<%
															if (request.getAttribute("primary_Unmatched_Anamoly_result") != null) {
																		SqlRowSet rs1 = (SqlRowSet) request.getAttribute("primary_Unmatched_Anamoly_result");
																		SqlRowSetMetaData metadata1 = rs1.getMetaData();
																		for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																			out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																		}
																	}
														%>

													</tr>
												</thead>
												<%-- <tbody>
													<%
														DecimalFormat numberFormat = new DecimalFormat("#.00");
																	if (rs1.first()) {
																		do {
																			out.println("<tr>");
																			for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																				if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																					//System.out.println("rs.getString(i):" + rs1.getString(i));
																					out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																				} else {
																					out.println("<td>" + rs1.getString(i) + "</td>");
																				}
																			}
																			out.println("</tr>");
																		} while (rs1.next());
																	}

																}
													%>
												</tbody> --%>
											</table>
										</div>
									</c:if>

									<c:if test="${Primary_Left_Only ne null }">
										<div class="portlet-title">
											<div class="caption font-red-sunglo">
												<span class="caption-subject bold ">Primary_Left_Only
													(Total Records=${PrimaryLeftOnlyCount}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${PrimaryLeftOnlyTableName}','PrimaryMatching/leftOnly')"
	
													href="#" class="CSVStatus" id="">(Download CSV)</a>
												</span>
											</div>
										</div>
										<br />
									
										<div class="portlet-body">
											<table id="primary_left_only_id"
												class="table table-striped table-bordered table-hover dataTable no-footer "
												style="width: 100%;">
												<thead>
													<tr>
														<%
															if (request.getAttribute("Primary_Left_Only") != null) {
																		SqlRowSet rs1 = (SqlRowSet) request.getAttribute("Primary_Left_Only");
																		SqlRowSetMetaData metadata1 = rs1.getMetaData();
																		for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																			out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																		}
														%>
													</tr>
												</thead>
												<tbody>
													<%
														DecimalFormat numberFormat = new DecimalFormat("#.00");
																	if (rs1.first()) {
																		do {
																			out.println("<tr>");
																			for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																				if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																						|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																					//System.out.println("rs.getString(i):" + rs1.getString(i));
																					out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																				} else {
																					out.println("<td>" + rs1.getString(i) + "</td>");
																				}
																			}
																			out.println("</tr>");
																		} while (rs1.next());
																	}

																}
													%>
												</tbody>
											</table>
										</div>
									</c:if>

									<c:if test="${Primary_Right_Only ne null }">
										<div class="portlet-title">
											<div class="caption font-red-sunglo">
												<span class="caption-subject bold ">Primary_Right_Only
													(Total Records=${PrimaryRightOnlyCount}) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${PrimaryRightOnlyTableName}','PrimaryMatching/rightOnly')"
	
													href="#" class="CSVStatus" id="">(Download CSV)</a>
												</span>
											</div>
										</div>
										<br />
										<div class="portlet-body">
											<table id="primary_right_only_id"
												class="table table-striped table-bordered table-hover dataTable no-footer "
												style="width: 100%;">
												<thead>
													<tr>
														<%
															if (request.getAttribute("Primary_Right_Only") != null) {
																		SqlRowSet rs1 = (SqlRowSet) request.getAttribute("Primary_Right_Only");
																		SqlRowSetMetaData metadata1 = rs1.getMetaData();
																		for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																			out.println("<th>" + metadata1.getColumnName(i) + "</th>");
																		}
														%>

													</tr>
												</thead>
												</tbody>
												<%
													DecimalFormat numberFormat = new DecimalFormat("#.00");
																if (rs1.first()) {
																	do {
																		out.println("<tr>");
																		for (int i = 1; i <= metadata1.getColumnCount(); i++) {
																			if (metadata1.getColumnTypeName(i).equalsIgnoreCase("double")
																					|| metadata1.getColumnTypeName(i).equalsIgnoreCase("decimal")
																					|| metadata1.getColumnTypeName(i).equalsIgnoreCase("float")) {
																				//System.out.println("rs.getString(i):" + rs1.getString(i));
																				out.println("<td>" + numberFormat.format(rs1.getDouble(i)) + "</td>");
																			} else {
																				out.println("<td>" + rs1.getString(i) + "</td>");
																			}
																		}
																		out.println("</tr>");
																	} while (rs1.next());
																}
															}
												%>
												</tbody>
											</table>
										</div>
									</c:if>
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
          $('#primary_left_only_id').dataTable({

		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });

	       $('#primary_right_only_id').dataTable({

            order: [[ 0, "desc" ]],
		  	"scrollX": true
          });


	var table;
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
					 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
				
				  tableName=$("#tableName2").val();
					//alert(tableName);
					console.log(tableName);
					table = $('#leftOnlyTable').dataTable({
						  	"bPaginate": true,
						  	"order": [ 0, 'asc' ],
						  	"bInfo": true,
						  	"iDisplayStart":0,
						  	"bProcessing" : true,
						 	"bServerSide" : true,
						 	"dataSrc": "",
						 	'sScrollX' : true,
						 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
					
					 tableName=$("#tableName3").val();
						//alert(tableName);
						console.log(tableName);
						table = $('#rightOnlyTable').dataTable({
							  	"bPaginate": true,
							  	"order": [ 0, 'asc' ],
							  	"bInfo": true,
							  	"iDisplayStart":0,
							  	"bProcessing" : true,
							 	"bServerSide" : true,
							 	"dataSrc": "",
							 	'sScrollX' : true,
							 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
						tableName=$("#tableName4").val();
						//alert(tableName);
						console.log(tableName);
						table = $('#UnmatchedGroupByTable').dataTable({
							  	"bPaginate": true,
							  	"order": [ 0, 'asc' ],
							  	"bInfo": true,
							  	"iDisplayStart":0,
							  	"bProcessing" : true,
							 	"bServerSide" : true,
							 	"dataSrc": "",
							 	'sScrollX' : true,
							 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
						tableName=$("#tableName5").val();
						//alert(tableName);
						console.log(tableName);
						table = $('#primaryUnmatchedAnamolyTable').dataTable({
							  	"bPaginate": true,
							  	"order": [ 0, 'asc' ],
							  	"bInfo": true,
							  	"iDisplayStart":0,
							  	"bProcessing" : true,
							 	"bServerSide" : true,
							 	"dataSrc": "",
							 	'sScrollX' : true,
							 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
						tableName=$("#tableName6").val();
						//alert(tableName);
						console.log(tableName);
						table = $('#UnmatchedAnamolyTable').dataTable({
							  	"bPaginate": true,
							  	"order": [ 0, 'asc' ],
							  	"bInfo": true,
							  	"iDisplayStart":0,
							  	"bProcessing" : true,
							 	"bServerSide" : true,
							 	"dataSrc": "",
							 	'sScrollX' : true,
							 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
						tableName=$("#tableName7").val();
						//alert(tableName);
						console.log(tableName);
						table = $('#primaryUnmatchedTable').dataTable({
							  	"bPaginate": true,
							  	"order": [ 0, 'asc' ],
							  	"bInfo": true,
							  	"iDisplayStart":0,
							  	"bProcessing" : true,
							 	"bServerSide" : true,
							 	"dataSrc": "",
							 	'sScrollX' : true,
							 	"sAjaxSource" : path+"/TranDetailAllTable?tableName="+tableName+"&matchingPage=true&maxRun=true",
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
<script>
$( "#leftOnlyDiv" ).on( "click", function() {
	 var form_data = {
			 tableName : $('#tableName2').val(),
         };
	var myInterval = setInterval(function(){
		  $.ajax({
              url: "./statusBarCsvDownload",
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype: 'json',
              data: form_data,
              success: function(message1) {
                 var j_obj1 = $.parseJSON(message1);
                 var percentage=j_obj1.percentage;
                 $('.CSVStatus').addClass('hidden');
                 $('#leftOnlyDivBar').removeClass('hidden');
                 $('.progressbar').css("width", percentage+'%');
                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
                  console.log(j_obj1);//return false;
                  if(percentage>95){
                	  //toastr.info("CSV Downloaded successfully");
                          clearInterval(myInterval);
                          $('#leftOnlyDivBar').addClass('hidden');
                          $('.CSVStatus').removeClass('hidden');
                      }
              },
              error: function(xhr, textStatus, errorThrown) 
              {
                  console.log(errorThrown);
              }
      })
        }, 3000);
});
$( "#RightOnlyDiv" ).on( "click", function() {
	 var form_data = {
			 tableName : $('#tableName3').val(),
         };
	var myInterval = setInterval(function(){
		  $.ajax({
              url: "./statusBarCsvDownload",
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype: 'json',
              data: form_data,
              success: function(message1) {
                 var j_obj1 = $.parseJSON(message1);
                 var percentage=j_obj1.percentage;
                 $('.CSVStatus').addClass('hidden');
                 $('#RightOnlyDivBar').removeClass('hidden');
                 $('.progressbar').css("width", percentage+'%');
                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
                  console.log(j_obj1);//return false;
                  if(percentage>95){
                	  //toastr.info("CSV Downloaded successfully");
                          clearInterval(myInterval);
                          $('#RightOnlyDivBar').addClass('hidden');
                          $('.CSVStatus').removeClass('hidden');
                      }
              },
              error: function(xhr, textStatus, errorThrown) 
              {
                  console.log(errorThrown);
              }
         
      })
        }, 3000);
});
$( "#UnmatchedDiv" ).on( "click", function() {
	var form_data = {
			 tableName : $('#tableName1').val(),
        };
	var myInterval = setInterval(function(){
		  $.ajax({
              url: "./statusBarCsvDownload",
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype: 'json',
              data: form_data,
              success: function(message1) {
                 var j_obj1 = $.parseJSON(message1);
                 var percentage=j_obj1.percentage;
                 $('.CSVStatus').addClass('hidden');
                 $('#UnmatchedDivBar').removeClass('hidden');
                 $('.progressbar').css("width", percentage+'%');
                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
                  console.log(j_obj1);//return false;
                  if(percentage>95){
                	  //toastr.info("CSV Downloaded successfully");
                          clearInterval(myInterval);
                          $('#UnmatchedDivBar').addClass('hidden');
                          $('.CSVStatus').removeClass('hidden');
                      }
              },
              error: function(xhr, textStatus, errorThrown) 
              {
                  console.log(errorThrown);
              }
         
      })
        }, 3000);
});
$( "#UnmatchedGroupByDiv" ).on( "click", function() {
	var form_data = {
			 tableName : $('#tableName4').val(),
        };
	var myInterval = setInterval(function(){
		  $.ajax({
              url: "./statusBarCsvDownload",
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype: 'json',
              data: form_data,
              success: function(message1) {
                 var j_obj1 = $.parseJSON(message1);
                 var percentage=j_obj1.percentage;
                 $('.CSVStatus').addClass('hidden');
                 $('#UnmatchedGroupByDivBar').removeClass('hidden');
                 $('.progressbar').css("width", percentage+'%');
                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
                  console.log(j_obj1);//return false;
                  if(percentage>99){
                	  //toastr.info("CSV Downloaded successfully");
                          clearInterval(myInterval);
                          $('#UnmatchedGroupByDivBar').addClass('hidden');
                          $('.CSVStatus').removeClass('hidden');
                         
                      }
              },
              error: function(xhr, textStatus, errorThrown) 
              {
                  console.log(errorThrown);
              }
      })
        }, 3000)
});

</script>
<script>
var ManagePrimaryKeyMatchingResultsView = {
		DataSet: {},
		IdApp: ${appId}, 								// new line added
		loadPrimaryKeyMatchingResultsTable: function() {
		//	var sViewTable = '#primeryKeyMatchingResultsTBL';
			var oMatchingResultsData = {};						// new line added	
			oMatchingResultsData.IdApp = ManagePrimaryKeyMatchingResultsView.IdApp	// new line added
			JwfAjaxWrapper({
				WaitMsg: 'Loading Primary Key Matching Results Records',
				Url: 'loadPrimaryKeyMatchingResultsTable',
				Headers:$("#token").val(),
				Data: { MatchingResultsData: JSON.stringify(oMatchingResultsData) },	// new line added
				CallBackFunction: function(oResponse) {
					
					ManagePrimaryKeyMatchingResultsView.DataSet = oResponse.DataSet;
					PageCtrl.debug.log("ManagePrimaryKeyMatchingResultsView.DataSet", ManagePrimaryKeyMatchingResultsView.DataSet);
					

					var columnsData = ${columnsData};
					var aDataColumnArray = [{"data":"Run","title":"Run"},{"data":"Date","title":"Date"}];
					var aColumnArray = columnsData.ResultColumns.split(",");
					PageCtrl.debug.log("columnsData",columnsData);
										
					aColumnArray.forEach(function(column) {
							aDataColumnArray.push({ "data" : column,"title": column});
					});
					PageCtrl.debug.log("aDataColumnArray",aDataColumnArray);

				}
			});
			
		}
}

$(document).ready(function () {
	initializeJwfSpaInfra();	
	ManagePrimaryKeyMatchingResultsView.loadPrimaryKeyMatchingResultsTable();
});
</script>

</html>

