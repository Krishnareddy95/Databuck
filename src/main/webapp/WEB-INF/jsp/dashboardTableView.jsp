<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
		<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
			<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet" %>
				<%@page import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData" %>
				<jsp:include page="checkVulnerability.jsp" />
					<!DOCTYPE html
						PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
					<html>

					<head>
						<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
						<link rel="stylesheet" href="./assets/js/bootstrap.min.css">
						<script src="./assets/js/jquery.min.js"></script>
						<script src="./assets/js/bootstrap.min.js"></script>

						<style type="text/css">
							.loader {
								border: 16px solid #f3f3f3;
								border-radius: 50%;
								border-top: 16px solid #337ab7;
								border-bottom: 16px solid #337ab7;
								width: 80px;
								height: 80px;
								-webkit-animation: spin 2s linear infinite;
								animation: spin 2s linear infinite;
							}

							@-webkit-keyframes spin {
								0% {
									-webkit-transform: rotate(0deg);
								}

								100% {
									-webkit-transform: rotate(360deg);
								}
							}

							@keyframes spin {
								0% {
									transform: rotate(0deg);
								}

								100% {
									transform: rotate(360deg);
								}
							}

							.scroller {
								overflow-x: hidden;
								position: absolute;
								left: 0;
							}
						</style>

						<!-- google chart plugin -->
						<script type="text/javascript" src="./assets/global/plugins/loader.js">

						</script>
						<script type="text/javascript">
							google.charts.load('current', {
								packages: ['corechart', 'line']
							});
						</script>
						<!-- google chart plugin ends -->
					</head>

					<body>
						<jsp:include page="dashboardTableCommon.jsp" />
						<div class="row">
							<div class="col-md-12">
								<div class="tabcontainer">
									<ul class="nav nav-tabs responsive "
										 role="tablist" style="border-top: 1px solid #ddd;border-bottom: 1px solid #ddd;">

										<li class="active"><a onClick="validateHrefVulnerability(this)"  href="dashboard_table?idApp=${idApp}"
												style="padding: 2px 2px;">
												<strong>Count Reasonability</strong>
											</a></li>
										<li><a onClick="validateHrefVulnerability(this)"  href="validity?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Uniqueness</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="nullstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Completeness</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="badData?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Conformity</strong> </a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="sqlRules?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Custom Rules</strong> </a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="stringstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Drift & Orphan</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="numericstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Distribution Check</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="recordAnomaly?idApp=${idApp}"
												style="padding: 2px 2px;">
												<strong>Record Anomaly</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="timelinessCheck?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Sequence</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="exceptions?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Exceptions</strong>
											</a></li>
										<li ><a onClick="validateHrefVulnerability(this)"  href="rootCauseAnalysis?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>RootCause Analysis
												</strong> </a></li>

									</ul>
								</div>
							</div>
						</div>

						<!-- <div class="portlet-title" id="tableLoader">
							<input type="hidden" id="flagGrcaId" value="${flagGrca}"> -->
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Transaction Set <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Transactionset_sum_A1_TableName}','RCAData')"

										href="#" class="CSVStatus" id="">(Download CSV)</a></span>
							</div>

						</div>

						<hr />
						<!-- Transactionset_sum_A1_TableName tab starts -->
						<ul class="nav nav-tabs">
							<li class="active"><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#home-Transactionset-sum">Home</a></li>
							<li><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#menu1-Transactionset-sum"
									id="TransactionsetSumTrendChartTabId">Trend Chart</a></li>
						</ul>

						<div class="tab-content">
							<div id="home-Transactionset-sum" class="tab-pane fade in active">
								<div class="portlet-body">
									<input type="hidden" id="tableName" value="${Transactionset_sum_A1_TableName}">
									<table class="TxsetTable table table-striped table-bordered  table-hover"
										style="width: 100%;">
										<thead>
											<tr>
												<th>Id</th>
                                                    <th>Date</th>
                                                    <th>Run</th>
                                                    <th>Forget</th>
                                                    <th>Day</th>
                                                    <th>Month</th>
                                                    <th>Day_Of_Month</th>
                                                    <th>Day_Of_Week</th>
                                                    <th>Hour</th>
                                                    <th>Std_Dev_Status</th>
                                                    <th>Mean_Status</th>
                                                    <th>Record_Count</th>
                                                    <th>Std_Dev</th>
                                                    <th>Mean</th>
                                                    <th>Deviation</th>
                                                    <th>Learn</th>
                                                    <th>RC_Mean_Moving_Avg</th>
                                                    <th>Microsegment_Val</th>
                                                    <th>Duplicate_Data_Set</th>
                                                    <th>File_Name_Validation_Status</th>
                                                    <th>Column_Order_Validation_Status</th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
							<div id="menu1-Transactionset-sum" class="tab-pane fade">
								<div class="row" id="TransactionsetSumTrendChart-header-id">
									<div class="col-md-3"></div>
									<div class="col-md-1">
										<div class="loader"></div>
									</div>

									<div class="col-md-1"></div>
									<div class="col-md-1"></div>

									<div class="col-md-3"></div>
								</div>
								<div class="row">
									<div id="transactionset-sum-draw-trend-chart-header-msg-Id"></div>
								</div>
								<div id="transactionset-sum-draw-trend-chart-Id"></div>
							</div>
						</div>
							<br />
							<br />
							<br />
							<input type="hidden" id="tableName2" value="${sum_dgroup_tableName}">
							<div class="portlet-title" id="dgroupId">
								<input type="hidden" id="flagGrcaId" value="${flagGrca}">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Microsegment <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${sum_dgroup_tableName}','DGroupRCAData')"

											href="#" class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>
							<div id="dgroup_tabs_Id">

								<hr />

								<div class="progress percentagebar hidden" id="UnmatchedDivBar">
									<div class="progress">
										<div class="progress-bar progressbar" role="progressbar" style="width: 5%;"
											aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">5%</div>
									</div>
								</div>
								<!-- filter button -->
								<div>
									<button id="addAdvanceFilter" class="btn w3-black w3-button w3-circle">+</button>
									<label id="addAdvanceFilter_v" class="text-info"> Add Advance
										Filter</label>
								</div>
								<div>
									<button id="rmAdvanceFilter"
										class="btn w3-black w3-button w3-circle hidden">-</button>
									<label id="rmAdvanceFilter_v" class="text-info hidden"> Remove
										Advance Filter</label>
								</div>
								<hr />
								<div class="row">
									<div>
										<div class="col-md-0"></div>
										<div class="col-md-12">
											<div class="row hidden" id="nav_button-bar"></div>
										</div>
									</div>
								</div>
								<!-- filter button ends -->
								<!-- bootstrap nav tabs -->
								<ul class="nav nav-tabs">
									<li id="tabfortable" class="active"><a onClick="validateHrefVulnerability(this);closeModal()"  data-toggle="tab"
											href="#SumDgrouphome">Table</a></li>
									<li id="tabfortrade"><a onClick="validateHrefVulnerability(this);openModal()"  data-toggle="tab" href="#chart-content"
											>Trend</a></li>
									<li><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#SumDgroupClusterTab"
											id="SumDgroupClusterTabId">Cluster Map</a></li>
								</ul>
							</div>
							<!-- bootstrap nav tabs end -->
							<div class="tab-content">
								<div id="SumDgrouphome" class="tab-pane fade in active">
									<div class="dgroupValTables">
										<div class="portlet-body" id="dgroupbodyId">
											<input type="hidden" id="flagGrcaId" value="${flagGrca}">
											<table class="DGroupTable table table-striped table-bordered  table-hover"
												id="DGroupTableId" style="width: 100%;">
												<thead>
													<tr>
														<th>Id</th>
														<th>Date</th>
														<th>Run</th>
														<th>Day</th>
														<th>Month</th>
														<th>Day_Of_Month</th>
														<th>Day_Of_Week</th>
														<th>Hour</th>
														<th>Record_Count</th>
														<th>Std_Dev</th>
														<th>Mean</th>
														<th>Deviation</th>
														<th>DQI</th>
														<th>Microsegment_Val</th>
														<th>Microsegment</th>
														<th>Status</th>
	
														<td>Action<a onClick="validateHrefVulnerability(this)" 
																href='rejectAll?rejectAll=True&tableName=${sum_dgroup_tableName}&tab=GBRCA&idApp=${idApp}'>
																<b>(Reject All)</b>
															</a></td>
															<th>Learn</th>
														<th>User_Name</th>
														<th>Time</th>

													</tr>
												</thead>
											</table>
										</div>
									</div>
								</div>

								<!-- Graph starts -->

								<div id="chart-content" class="tab-pane fade">

									<div class="row" style="padding-top: 5%;"></div>
									<div class="row" id="nochartdata">
										<div class="col-md-3"></div>
										<div class="col-md-1">
											<div class="loader"></div>
										</div>

										<div class="col-md-1"></div>
										<div class="col-md-1"></div>

										<div class="col-md-3"></div>
									</div>
									<div class="row">
										<div>
											<div class="modal-body" id="modal-body"></div>
										</div>
									</div>

									<div id="filter-chart-content">

										<div class="row" style="padding-top: 5%;"></div>
										<div class="row" id="filter-nochartdata">
											<div class="col-md-3"></div>
											<div class="col-md-3 text-right text-info">Loading Trend Graph ...</div>
											<div class="col-md-1">
												<div class="loader"></div>
											</div>

											<div class="col-md-1"></div>
											<div class="col-md-1"></div>

											<div class="col-md-3"></div>
										</div>
										<div class="row" id="filter-nochartdata">
											<div class="col-md-2"></div>
											<div class="col-md-8 text-right text-info">Advance filter Chart. To view all
												mircrosegments refresh Advance Filter</div>


											<div class="col-md-2"></div>
										</div>
										<div class="row">
											<div class="col-md-12" style="width: 450px;">
												<div class="modal-body" id="filter-modal-body"></div>
											</div>
										</div>
									</div>
									<!-- Graph ends -->


									<!-- tab-content ends -->
								</div>

								<div id="SumDgroupClusterTab" class="tab-pane fade">
									<div class="row" id="SumDgroupCluster-header-id">
										<div class="col-md-3"></div>
										<div class="col-md-1">
											<div class="loader"></div>
										</div>

										<div class="col-md-1"></div>
										<div class="col-md-1"></div>

										<div class="col-md-3"></div>
									</div>
									<div id="bubbleChart"> </div>
								</div>

							</div>


							<br>
							<br>
							<br>
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Processed Data<a onClick="validateHrefVulnerability(this)"  href="#">(Download CSV)</a>
									</span>
								</div>
							</div>

							<input type="hidden" id="processDatatableNameId" value="${processing_Data_TableName}">

							<div class="portlet-body">
								<table id="ProcessDataTable" class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th>idApp</th>
											<th>Run</th>
											<th>Date</th>
											<th>File_Name/Table_Name</th>
										</tr>
									</thead>
								</table>
							</div>
							<br />
							<br /><br /></br>
								<div class="cd-popup2" id="popSample">
									<div class="cd-popup-container1" id="outerContainer1">
										<div id="loading-data-progress" class="ajax-loader hidden">
											<h4>
												<b>Summary DQI Data Loading in Progress..</b>
											</h4>
											<img src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
												class="img-responsive" />
										</div>
										<br /> <br />
										<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
										<div id="chartDiv" style="height: 500px; width: 100%; margin: 0 auto;"
											class="dashboard-summary-chart"></div>
										</div>
								</div>
								<!--=========================data table - bind data - destroy data table - initialize datatable ================================ -->
								<script src="./assets/pages/scripts/advance-filter-search.js"
									type="text/javascript"></script>
								<jsp:include page="downloadCsvReports.jsp" />
								<jsp:include page="footer.jsp" />
					</body>

					<head>

						<script type="text/javascript" src="./assets/global/plugins/d3.v4.min.js"></script>
							
						<script>

							var showFilterChart;

							$("#addAdvanceFilter").click(function () {

								$("#nav_button-bar").removeClass('hidden').show();
								$("#rmAdvanceFilter").removeClass('hidden').show();
								$("#addAdvanceFilter").addClass('hidden');
								$("#addAdvanceFilter_v").addClass('hidden');
								$("#rmAdvanceFilter_v").removeClass('hidden').show();
								//$("#tableresult").addClass('hidden');
								$('#portletbodyfilter').show();
								$('#portletbody').hide();
								$('#load_refresh').hide();

							});
							$("#rmAdvanceFilter").click(function () {

								$("#nav_button-bar").addClass('hidden');
								$("#rmAdvanceFilter").addClass('hidden');
								$("#addAdvanceFilter").removeClass('hidden').show();
								$("#addAdvanceFilter_v").removeClass('hidden').show();
								$("#rmAdvanceFilter_v").addClass('hidden');
								$('#dgroupbodyId').show();
							});
						</script>

						<script>
							$(document).ready(function () {
								var processTable;

								processTable = $("#processDatatableNameId").val();
								idApp = $("#idApp").val();

								console.log(tableName);
								table = $('#ProcessDataTable').dataTable({
									"bPaginate": true,
									"order": [0, 'asc'],
									"bInfo": true,
									"iDisplayStart": 0,
									"bProcessing": true,
									"bServerSide": true,
									"dataSrc": "",
									'sScrollX': true,
									"sAjaxSource": path + "/ProcessDataTable?tableName=" + processTable + "&idApp=" + idApp,
									"aoColumns": [
                                        {"data" : "idApp"},
                                        {"data" : "Run"},
                                        {"data" : "Date"},
                                        {"data" : "folderName"}

                                    ],
									"dom": 'C<"clear">lfrtip',
									colVis: {
										"align": "left",
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

						<script>
							$(document).ready(function () {

								$('#TransactionsetSumTrendChartTabId').click(function () {
									$('#TransactionsetSumTrendChart-header-id').show();
									var transactionsetSumTablename = $("#tableName").val();

									$(function () {
										var form_data = {
											tableName: transactionsetSumTablename,
											idApp: $("#idApp").val()
										};
										var request = $.ajax({
											url: './transactionsetSumTrendChart',
											type: 'POST',
											headers: { 'token':$("#token").val()},
											data: form_data,
											dataType: 'json'
										});
										request.done(function (msg) {
											drawTransactionsetSumTrendChart(msg, 'Date & Run', 'Record-Count'); //to bind data
											$('#TransactionsetSumTrendChart-header-id').hide();
										});
										request.fail(function (jqXHR, textStatus) {
											console.log("callRollUpComparisonChart function Request failed: " + textStatus);
										});
									});

								});


								$('#SumDgroupClusterTabId').click(function () {
									document.getElementById("bubbleChart").innerHTML = "";
									$('#SumDgroupCluster-header-id').show();
									var tableName = $("#tableName2").val();
									$('#chart-content').hide();
									$(function () {
										var form_data = {
											tableName: tableName,
											idApp: $("#idApp").val()
										};
										var request = $.ajax({
											url: './countReasionabilitySumDgroupCluster',
											type: 'POST',
											headers: { 'token':$("#token").val()},
											data: form_data,
											dataType: 'json'
										});
										request.done(function (msg) {
											//	drawSumDgroupClusterMap(msg); //to bind data
											$("#bubbleChart").append(preparebubbleChart(msg));
											$('#SumDgroupCluster-header-id').hide();
											//			document.getElementById("rollupComparisonChartDivId-chart-headerid").innerHTML = "";
										});
										request.fail(function (jqXHR, textStatus) {
											console.log("callRollUpComparisonChart function Request failed: " + textStatus);
										});
									});
								});

								/*
								set active class to graph tap on page load and hide trend contents
								*/
								$('#tabfortable').addClass('active');
								$("#chart-content").hide();
								$('#filter-chart-content').hide();
								globalbooleanTrendChartStatus = false;
								globalbooleanTableStatus = true;

								/*tab code ends*/
								var table;
								var tableName = $("#tableName").val();

								$('#filter-chart-content').hide();
                                var i=0;
								var idApp = $("#idApp").val();
								console.log(idApp);
								table = $('.TxsetTable').dataTable({
									"bPaginate": true,
									"order": [0, 'asc'],
									"bInfo": true,
									"iDisplayStart": 0,
									"bProcessing": true,
									"bServerSide": true,
									"dataSrc": "",
									'sScrollX': true,
									"sAjaxSource": path + "/pagination?tableName=" + tableName + "&idApp=" + idApp,
									"aoColumns": [
										{"data" : "Id"},
										{"data" : "Date"},
										{"data" : "Run1"},
										{"data" : "forgot_run_enabled",
                                           "render": function(data, type, row, meta){
                                               var maxdate =row.Date;
                                               var run =row.Run1;

                                               if (data === "Y") {
                                                   data = "<a onClick='unforgotRun("+idApp+",\""+maxdate+"\","+run+")' title='This run is forgot. Click to unforget' ><img src='./assets/img/forgot-run.png' width='20px' height='25px'/></a>";
                                               } else {
                                                   data = "<a onClick='forgotRun("+idApp+",\""+maxdate+"\","+run+")' title='This run is unforgot. Click to forget' ><i class='fa fa-lock' aria-hidden='true' style='font-size: 30px;color:green;'></i></a>";
                                               }
                                               return data;
                                           }
                                        },
										{"data" : "dayOfYear"},
										{"data" : "month"},
										{"data" : "dayOfMonth"},
										{"data" : "dayOfWeek"},
										{"data" : "hourOfDay"},
										{"data" : "RC_Std_Dev_Status",
											
											"render": function(data, type, row, meta){
												if (data === "passed") {
													data = "<span class='label label-success label-sm'>"
															+ data + "</span>";
												
												} else if (data === "failed") {
													data = "<span class='label label-danger label-sm'>"
															+ data + "</span>";
												} 
												
												return data;
											}
										},
										{"data" : "RC_Mean_Moving_Avg_Status",
											
											"render": function(data, type, row, meta){
												if (data === "passed") {
													data = "<span class='label label-success label-sm'>"
															+ data + "</span>";
												
												} else if (data === "failed") {
													data = "<span class='label label-danger label-sm'>"
															+ data + "</span>";
												} 
												
												return data;
											}
										},
										{"data" : "RecordCount1"},
										{"data" : "RC_Std_Dev1"},
										{"data" : "RC_Mean1"},
										{"data" : "RC_Deviation"},
										{"data" : "RC_Deviation",
											"render": function(data, type, row, meta){
								                   if (data != null && data != undefined && data.length>0){
								                        data = "<a onClick=\"javascript:updateRecordCountAnomalyThreshold("+idApp+",'Record Count Anomaly','','"+data+"')\" class=\"fa fa-thumbs-down\" style=\"font-size:30px\"></a>";
								                   }
								                   
								                   return data;
										      }
							                },
										{"data" : "RC_Mean_Moving_Avg"},
										{"data" : "dGroupVal"},
										{"data" : "DuplicateDataSet"},
										{"data" : "fileNameValidationStatus"},
                                        {"data" : "columnOrderValidationStatus"}
									],
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
								tableName = $("#tableName2").val();
								tableName = $("#tableName2").val();
							
							$('.TxsetTable > thead> tr> th:nth-child(2)').css('min-width' , '100px !important');
							$('.TxsetTable > tbody> tr> td:nth-child(2)').css('min-width' , '100px !important');
								loadfreshdata('DGroupTableId', 'DGroupTable', $("#tableName2").val(), idApp, ""); // 1: tableid/class, 2: api call 3:tableName & 4: idApp

								var flagGrcas = $("#flagGrcaId").val();
								if (flagGrcas == "N") {
									$('#dgroupId').addClass('hidden');
									$('#dgroupbodyId').addClass('hidden');
									$('#dgroup_tabs_Id').addClass('hidden');
								}

								$('.hrefCall').click(function () {
									$('#popSample').addClass('is-visible');
									var validationCheck = $(this).attr('id');
									// alert($(this).attr('id'));
									var form_data = {
										validationCheck: validationCheck,
										idApp: $("#idApp").val(),
									};
									$('#loading-data-progress').removeClass('hidden').show();
									console.log(form_data);
									$.ajax({
										url: './getDQIScoresMap',
										type: 'POST',
										headers: { 'token':$("#token").val()},
										datatype: 'json',
										data: form_data,
										success: function (listOfValues) {
											console.log(listOfValues);
											if (typeof listOfValues !== "undefined" && listOfValues != "") {

												google.load("visualization", "1", { packages: ["corechart"] });
												google.setOnLoadCallback(drawChart);
												var data1 = new google.visualization.DataTable();
												data1.addColumn('string', 'Date(Run)');
												data1.addColumn('number', 'DQI');

												//data1.addRow([new Date("2016-02-04"),90]);
												$.each(listOfValues, function (key, value) {
													$.each(value, function (i, l) {
														// alert( "Index #" + i + ": " + l );
														data1.addRow([key, l]);
													});
												});
												data1.sort({ column: 0, asc: true });
												var title = "";
												if (validationCheck == "Aggregate DQI Summary") {
													title = validationCheck;
												} else {
													title = validationCheck + " DQI";
												}
												var len = $.map(listOfValues, function (n, i) { return i; }).length;
												console.log(len);
												var showTextEvery = 2;
												if (len <= 10) {
													showTextEvery = 1;
												} else if (len > 10 && len <= 20) {
													showTextEvery = 2;
												} else if (len > 20) {
													showTextEvery = (len / 10);
												}
												showTextEvery = Math.round(showTextEvery);

												//alert(len);
												var options = {
													title: title,
													hAxis: { title: 'Date (Run)', titleTextStyle: { color: '#333' }, direction: -1, slantedText: true, slantedTextAngle: 45, showTextEvery: showTextEvery },
													vAxis: { minValue: 0 },
													chartArea: { bottom: 100 },
													height: 400,
													width: 1000
												};
												$('#loading-data-progress').addClass('hidden');
												var chart = new google.visualization.AreaChart(document.getElementById('chartDiv'));
												chart.draw(data1, options);


												$('#chartDiv').removeClass('hidden');
											} else {
												$('#chartDiv').addClass('hidden');
											}

										},
										error: function (xhr, textStatus,
											errorThrown) {
											$('#initial').hide();
											$('#fail').show();
										}
									});
								});
								$("#dqiDataCloseBtn").click(function () {
									$('#popSample').removeClass('is-visible');
								});

							});
						</script>
						<script>
							var strSearchValue = "";
							var formdatatableName = $("#tableName2").val();
							var microDataValueData = {
								tableName: formdatatableName
							};
							var form_data = {
								idApp: $("#idApp").val(),
								// times: new Date().getTime()
							};
							var column_name = [];
							var _this = this;
							var intSeperators = 0;
							var uniqueArray = [];
							$(document).ready(function () {

								advancefilter($("#idApp").val(), formdatatableName, 'DGroupTable', 'DGroupTableId'); //function code is written in search.js


							});
							function onlyUnique(value, index, self) {
								return self.indexOf(value) === index;
							}
							$(document).ready(function () {
								$("#Refresh").click(function () {
									console.log("test");
								});

								$("#Refresh").on('click', log)
							});

							function log() {
								var element = document.getElementById('nav_button-bar');

								console.log(element);
							}


							function openModal() {
								$("#dgroupbodyId").hide();
								var tableName = $("#tableName2").val();
								var idApp = $("#idApp").val();
								$("#modal-body").show();
								if (showFilterChart == true) {

									$('#chart-content').show();
									$('#filter-chart-content').show();
								} else {
									loadChart('googleChart', tableName, idApp, 'date', 'Record Count');
								}


								// call /googleChart controller from PaginationController.java
							}

							/*
							Update Date : 28thApril,2020
							Code By: Anant S. Mahale
							Bind object to Google Combined Chart, 
							*/
							function drawTransactionsetSumTrendChart(array, xaxis, yaxis) {

								booleanHideGraphLoadStatus = true;
								var header = array.header;
								var chartdata = array.chart;
								var data = new google.visualization.DataTable();
								data.addColumn('string', 'Date & Run');
								data.addColumn('number', 'RecordCount')
								//for (i = 0; i < header.length; i++) {
								//	data.addColumn('number', header[i]);
								//}
								//console.log("chartdata: "+chartdata)
								data.addRows(chartdata);

								data.sort({
									column: 0,
									asc: true
								});

								var options = {
									'title': '',
									hAxis: {
										title: xaxis, direction: -1, slantedText: true, slantedTextAngle: 45
									},
									vAxis: {
										title: yaxis,
										vAxis: {
											viewWindowMode: "explicit",
											viewWindow: {
												min: 0
											}
										}
									},
									'width': 900,
									'height': 750,
									legend: {
										maxLines: 1,
										textStyle: {
											fontSize: 15
										}
									},
									chartArea: { left: 120, top: 50, width: '70%' },
									isStacked: true
								};
								console.log("before clicking on legend");
								var chart = new google.visualization.LineChart(document.getElementById('transactionset-sum-draw-trend-chart-Id')); // on this element google chart
								// will load
								chart.draw(data, options);
								var columns = [];
								var series = {};
								for (var i = 0; i < data.getNumberOfColumns(); i++) {
									columns.push(i);
									if (i > 0) {
										series[i - 1] = {};
									}
								}

								var options = {
									'title': '',
									hAxis: {
										title: xaxis, direction: -1, slantedText: true, slantedTextAngle: 45
									},
									vAxis: {
										title: yaxis,
										vAxis: {
											viewWindowMode: "explicit",
											viewWindow: {
												min: 0
											}
										}
									},
									'width': 900,
									'height': 750,
									legend: {
										maxLines: 1,
										textStyle: {
											fontSize: 15
										}
									},
									chartArea: { left: 120, top: 50, width: '70%' },
									isStacked: true,
									series: series
								};

								google.visualization.events.addListener(chart, 'select', function () {
									//			console.log("trying to hide line on click of legend");
									var sel = chart.getSelection();
									// if selection length is 0, we deselected an element
									console.log("sel");
									console.log(sel);
									if (sel.length > 0) {
										// if row is undefined, we clicked on the legend
										if (sel[0].row === null) {
											var col = sel[0].column;
											console.log("column : ");
											console.log(col);
											if (columns[col] == col) {
												// hide the data series
												//                	 console.log("column condition true ");
												//              	 console.log(data.getColumnLabel(col));
												//            	 console.log(data.getColumnType(col));
												columns[col] = {
													label: data.getColumnLabel(col),
													type: data.getColumnType(col),
													calc: function () {
														return 0;
													}
												};
												// grey out the legend entry
												series[col - 1].color = '#CCCCCC';
											} else {
												// show the data series
												columns[col] = col;
												series[col - 1].color = null;
											}
											//          console.log("column dfn ");
											//         console.log(columns);
											var view = new google.visualization.DataView(data);
											view.setColumns(columns);
											chart.draw(view, options);
										}
									}
								});
							}

							function preparebubbleChart(dataset) {

								//console.log('preparebubbleChart : '+obj)
								//	console.log(obj)
								//	var dataset = obj.children;

								/*	dataset = 
									{
											"children" : [  {"Name":"-Bond","Count":1},
												{"Name":"Canada-Bond","Count":1},
												{"Name":"Canada-Fixed Income","Count":2}]
										}; */

								console.log(dataset);
								var diameter = 600;
								var color = d3.scaleOrdinal(d3.schemeCategory20);

								var bubble = d3.pack(dataset)
									.size([diameter, diameter])
									.padding(1.5);

								var svg = d3.select("#bubbleChart").append("svg")
									.attr("width", '900px')
									.attr("height", '750px')
									.append("g")
									.attr("transform", "translate(2%,2%)");

								var nodes = d3.hierarchy(dataset)
									.sum(function (d) { return d.Count; });

								var node = svg.selectAll(".node")
									.data(bubble(nodes).descendants())
									.enter()
									.filter(function (d) {
										return !d.children
									})
									.append("g")
									.attr("class", "node")
									.attr("transform", function (d) {
										return "translate(" + d.x + "," + d.y + ")";
									});

								node.append("title")
									.text(function (d) {
										return d.Name + ": " + d.Count;
									});

								node.append("circle")
									.attr("r", function (d) {
										return d.r;
									})
									.style("fill", function (d, i) {
										return color(i);
									});

								node.append("text")
									.attr("dy", ".2em")
									.style("text-anchor", "middle")
									.text(function (d) {
										return d.data.Name.substring(0, d.r / 3);
									})
									.attr("font-family", "sans-serif")
									.attr("font-size", function (d) {
										return d.r / 5;
									})
									.attr("fill", "white");

								node.append("text")
									.attr("dy", "1.3em")
									.style("text-anchor", "middle")
									.text(function (d) {
										return d.data.Count;
									})
									.attr("font-family", "Gill Sans", "Gill Sans MT")
									.attr("font-size", function (d) {
										return d.r / 5;
									})
									.attr("fill", "white");

								d3.select(self.frameElement)
									.style("height", diameter + "px");

							}
						</script>
						<script>
                           function unforgotRun(idApp,date,run) {

                                 var form_data = {
                                                idApp: idApp,
                                                maxExecDate:date,
                                                maxExecRun:run
                                 };

                                 $.ajax({
                                      type : 'POST',
                                      headers: { 'token':$("#token").val()},
                                      url : "./unforgotRun",
                                      data : form_data,
                                      success : function(message) {
                                          try{
                                              var j_obj1 = $.parseJSON(message);
                                              var status= j_obj1.status;
                                              var msg= j_obj1.message;
                                              toastr.info(msg);

                                              if (status==='success') {
                                                this.value="Unforgot Run";
                                                setTimeout(function(){
                                                   window.location.reload();
                                                },1000);

                                              }

                                          } catch(e) {
                                              toastr.info('Failed to perform Forgot Run');
                                          }
                                      },
                                      error: function (textStatus) {
                                        toastr.info('Failed to perform Forgot Run');
                                      }
                                });
                           }

                           function forgotRun(idApp,date,run) {

                                 var form_data = {
                                           idApp: idApp,
                                           maxExecDate:date,
                                           maxExecRun:run
                                 };

                                 $.ajax({
                                     type : 'POST',
                                     headers: { 'token':$("#token").val()},
                                     url : "./forgotRun",
                                     data : form_data,
                                     success : function(message) {
                                         try{
                                             var j_obj1 = $.parseJSON(message);
                                             var status= j_obj1.status;
                                             var msg= j_obj1.message;
                                             toastr.info(msg);

                                             if (status==='success') {
                                               this.value="Unforgot Run";
                                               setTimeout(function(){
                                                  window.location.reload();
                                               },1000);

                                             }

                                         } catch(e) {
                                             toastr.info('Failed to perform Forgot Run');
                                         }
                                     },
                                     error: function (textStatus) {
                                       toastr.info('Failed to perform Forgot Run');
                                     }
                                 });
                            }
                        </script>
					</html>