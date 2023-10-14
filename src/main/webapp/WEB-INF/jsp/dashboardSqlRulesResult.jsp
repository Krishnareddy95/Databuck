<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
	<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<style>
	#DataTables_Table_1 a {
		font-size: 26px;
		text-decoration: none;
		cursor: pointer !important;
	}
    #DataTables_Table_0 a {
        font-size: 26px;
        text-decoration: none;
        cursor: pointer !important;
    }
    #SqlResultsDataTable a {
       font-size: 26px;
       text-decoration: none;
       cursor: pointer !important;
    }
    .modal {
    	text-align: center;
    	padding: 0 !important;
    }

    .modal:before {
    	content: '';
    	display: inline-block;
    	height: 100%;
    	vertical-align: middle;
    	margin-right: -4px; /* Adjusts for spacing */
    }

    .modal-dialog {
    	display: inline-block;
    	text-align: left;
    	vertical-align: middle;
    	width: 100%;
    }
</style>
</head>
<body>
	<jsp:include page="dashboardTableCommon.jsp" />


	<div class="row">
		<div class="col-md-12">
			<div class="tabcontainer">
				<ul class="nav nav-tabs responsive" role="tablist" style="border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">

					<li ><a onClick="validateHrefVulnerability(this)"
						href="dashboard_table?idApp=${idApp}" style="padding: 2px 2px;">
							<strong>Count Reasonability</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="validity?idApp=${idApp}"
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
					<li class="active" ><a onClick="validateHrefVulnerability(this)"
						href="sqlRules?idApp=${idApp}" style="padding: 2px 2px;"><strong>Custom
								Rules</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="stringstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Drift & Orphan</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="numericstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Distribution Check</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"
						href="recordAnomaly?idApp=${idApp}" style="padding: 2px 2px;">
							<strong>Record Anomaly</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"
						href="timelinessCheck?idApp=${idApp}" style="padding: 2px 2px;"><strong>Sequence</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"
				                         		href="exceptions?idApp=${idApp}"
				                         		style="padding: 2px 2px;"><strong>Exceptions</strong>
					                         </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"
						href="rootCauseAnalysis?idApp=${idApp}" style="padding: 2px 2px;"><strong>RootCause Analysis
						</strong> </a></li>

				</ul>
				<input type="hidden" id="keyGroupRecordAnomaly" value = "${keyGroupRecordAnomaly}" />
			</div>
		</div>
	</div>


	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">SQL Rules <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${processing_Data_TableName}','SqlRules')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a>
			</span>
		</div>
	</div>
	<hr />

	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="tableName"
		value="${processing_Data_TableName}">

	<div class="portlet-body">
		<table id="SqlResultsDataTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<th>Rule_Name</th>
					<th>Date</th>
					<th>Run</th>
					<th>Status</th>
					<th style="width: 20px;">Total_Records</th>
					<th style="width: 20px;">Failed_Records</th>
					<th style="width: 20px;">Rule_Threshold</th>
					<th>Learn</th>
					<th>Download_File</th>
				</tr>
			</thead>
		</table>
	</div>

	<br />
	<br />
	<input type="hidden" id="tableName3"
		value="${DataQualityRulesTableName}">
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Custom Rules <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${DataQualityRulesResultsTableName}','CustomRules')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a>
			</span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="rulesResultsId"
		value="${DataQualityRulesResultsTableName}">
	<div class="progress percentagebar hidden" id=rulesdivbar>
		<div class="progress">
			<div class="progress-bar progressbar" role="progressbar"
				style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
				aria-valuemax="100">5%</div>
		</div>
	</div>
	<div class="portlet-body">
		<table id=""
			class="RulesTable table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<%
						String data1 = (String) request.getAttribute("DataQualityRulesTrue");
						if (data1 != null && data1.equals("DataQualityRulesTrue")) {
					%>
					<th>Id</th>
					<th>Date</th>
					<th>Run</th>
					<th>Rule_Name</th>
					<th>Total_Records</th>
					<th>Total_Failed</th>
					<th>Failed_Percentage</th>
					<th>Rule_Threshold</th>
					<th>Learn</th>
					<th>Status</th>
					<th>Download_File</th>
					<%
						} else {
					%>
					<td>Id</td>
					<td>Date</td>
					<td>Run</td>
					<td>Rule_Name</td>
					<td>Rule_Type<td>
					<td>Total_Records</td>
					<td>Matched_Records</td>
					<td>Unmatched_Records</td>

					<%
						}
					%>
				</tr>
			</thead>


			<%-- <tbody>
	<%
	if (data1 != null && data1.equals("DataQualityRulesTrue")) {
	%>
	<%
	try {
	SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("DataQualityRules");
	while (sqlRowSet.next()) {
	out.println("<tr>");
	for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
	out.println("<td>" + sqlRowSet.getString(k) + "</td>");
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
	<br />

	<!-- Added Global Rule & Orphan Rule --Priyanka -->

	<br />
	<input type="hidden" id="tableName4"
		value="${DataQualityGlobalRulesTableName}">
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Global Rules <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${DataQualityRulesResultsTableName}','GlobalRules')"   href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="rulesResultsId1"
		value="${DataQualityRulesResultsTableName}">
	<div class="progress percentagebar hidden" id=rulesdivbar1>
		<div class="progress">
			<div class="progress-bar progressbar" role="progressbar"
				style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
				aria-valuemax="100">5%</div>
		</div>
	</div>
	<div class="portlet-body">
		<table id=""
			class="GlobalRulesTable table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<th>Id</th>
					<th>Date</th>
					<th>Run</th>
					<th>Rule_Name</th>
					<th>Total_Records</th>
					<th>Total_Failed</th>
					<th>Failed_Percentage</th>
				    <th>Rule_Threshold</th>
				    <th>Microsegment_Col</th>
				    <th>Microsegment_Val</th>
                    <th>Dimension</th>
				    <th>Learn</th>
				    <th>Status</th>
					<th>Download_File</th>
				</tr>
			</thead>
		</table>
	</div>


	<div class="cd-popup2" id="popSample">
		<div class="cd-popup-container1" id="outerContainer1">
			<div id="loading-data-progress" class="ajax-loader hidden">
				<h4>
					<b>Summary DQI Data Loading in Progress..</b>
				</h4>
				<img
					src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
					class="img-responsive" />
			</div>
			<br /> <br /> <br /> <br />
			<div id="chartDiv"
				style="height: 500px; width: 100%; margin: 0 auto;"
				class="dashboard-summary-chart"></div>
			<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
		</div>
	</div>
	    	<div id="propModal" class="modal fade" role="dialog">
                <div class="modal-dialog">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">

                            <h4 class="modal-title"
                                style="font-size: 25px; color: #EE4B2B;font-weight: 500;">WARNING</h4>
                        </div>
                        <div class="modal-body">
                            <p>
                                <span style="color: red">If Global Rule Threshold updated, it will affect every related one.</span><br /><br />
                                <span style="color: #1261A0">Press Okay to update Threshold, else Press Cancel.</span>
                            </p>
                        </div>
                        <div class="modal-footer">
                            <a id="modalButtonOk"
                                style="padding: 2px 2px;"><strong class="blue btn">Okay</strong> </a>
                            <button type="button" id="modalButton" class="btn blue"
                                data-dismiss="modal">Cancel</button>

                        </div>
                    </div>

                </div>
            </div>

	<jsp:include page="downloadCsvReports.jsp" />
	<jsp:include page="footer.jsp" />
</body>

<script>
	var table;

	tableName = $("#tableName").val();
	idApp = $("#idApp").val();

	//alert(tableName);
	console.log(tableName);
	table = $('#SqlResultsDataTable').dataTable(
			{
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],
				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,
				"sAjaxSource" : path + "/SqlResultsDataTable?tableName="
						+ tableName + "&idApp=" + idApp,
                 "aoColumns": [
                          {"data" : "rulename"},
                          {"data" : "date"},
                          {"data" : "run"},
                          {"data" : "status",
                           "render": function(data, type, row, meta){
                              if (data==="true"){
                                  data = "<span class='label label-success label-sm'>"
                                          + 'PASSED' + "</span>";

                              } else if (data==="false"){
                                  data = "<span class='label label-danger label-sm'>"
                                          + 'FAILED' + "</span>";
                              }

                              return data;
                              }
                          },
                          {"data" : "totalRecords"},
                          {"data" : "total_failed_records"},
                          {"data" : "ruleThreshold"},
                          {"data" : "total_failed_records",
                            "render": function(data, type, row, meta){
                                  var total_failed_records = row.total_failed_records;
                                  var total_records = row.totalRecords;
                                  var failPercentage = (total_failed_records/total_records)*100;
                                  var customRuleName = row.rulename;
                                  data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Custom Rule','"+customRuleName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\"></a>";
                                  return data;
                              }
                           },
                          {"data" : "rulename",
                                  "render": function(data, type, row, meta){
                                     data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','SqlRules/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
                                      return data;
                                  }
                          }
                ],//DC-344 amol
                'columnDefs': [ {
                    'targets': [8], // column index (start from 0)
                    'orderable': false, // set orderable false for selected columns
                 }],
				"dom" : 'C<"clear">lfrtip',
				colVis : {
					"align" : "left",
					restore : "Restore",
					showAll : "Show all",
					showNone : "Show none",
					order : 'alpha'
				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',

			});

	tableName = $("#tableName3").val();
	//alert(tableName);
	table = $('.RulesTable').dataTable(
			{
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],

				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,

				"sAjaxSource" : path + "/TranDetailIdentityTable?tableName="
						+ tableName + "&idApp=" + idApp,
				 "aoColumns": [
				    {"data" : "Id"},
				    {"data" : "Date"},
				    {"data" : "Run"},
				    {"data" : "ruleName"},
				    {"data" : "totalRecords"},
				    {"data" : "totalFailed"},
				    {"data" : "rulePercentage"},
				    {"data" : "ruleThreshold"},
				    {"data" : "ruleThreshold",
                      "render": function(data, type, row, meta){
                           var failPercentage = row.rulePercentage;
                           var customRuleName = row.ruleName;
                           data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Custom Rule','"+customRuleName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\"></a>";
                       return data;
                       }
                    },
				    {"data" : "status",
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
				    {"data" : "ruleName",
					    "render": function(data, type, row, meta){
					       data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','CustomRules/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
					        return data;
					    }
				    }
                ],
                //DC-344 amol
                'columnDefs': [ {
                    'targets': [10], // column index (start from 0)
                    'orderable': false, // set orderable false for selected columns
                 }],
				"dom" : 'C<"clear">lfrtip',
				colVis : {
					"align" : "right",
					restore : "Restore",
					showAll : "Show all",
					showNone : "Show none",
					order : 'alpha'

				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',

			});

   tableName = $("#tableName4").val();
	//alert(tableName);

	table = $('.GlobalRulesTable').dataTable(
			{
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],

				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,

				"sAjaxSource" : path + "/GlobalRuleResultsDataTable?tableName="
						+ tableName + "&idApp=" + idApp,
				 "aoColumns": [
  				    {"data": "Id"},
                    {"data" : "Date"},
                    {"data" : "Run"},
                    {"data" : "ruleName"},
                    {"data" : "totalRecords"},
                    {"data" : "totalFailed"},
                    {"data" : "rulePercentage"},
                    {"data" : "ruleThreshold"},
                    {"data" : "dGroupCol"},
                    {"data" : "dGroupVal"},
                    {"data" : "dimension_name"},
                    {"data" : "rulePercentage",
                      "render": function(data, type, row, meta){
                           var failPercentage = row.rulePercentage;
                           var globalRuleName = row.ruleName;
                           data = "<a onClick=\"javascript:globalRuleConfirmation("+idApp+",'Global Rule','"+globalRuleName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\" </a>";
                       return data;
                       }
                    },
                    {"data" : "status",
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
                    {"data" : "ruleName",
                            "render": function(data, type, row, meta){
                               data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','GlobalRules/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
                                return data;
                            }
                    }
                ],
				"dom" : 'C<"clear">lfrtip',
				colVis : {
					"align" : "right",
					restore : "Restore",
					showAll : "Show all",
					showNone : "Show none",
					order : 'alpha'

				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',
				//DC-344 amol
                'columnDefs': [ {
                    'targets': [13], // column index (start from 0)
                    'orderable': false, // set orderable false for selected columns
                 }],

			});


	$("#rulesdiv").on(
			"click",
			function() {
				var form_data = {
					tableName : $('#rulesResultsId').val(),
				};
				var myInterval = setInterval(function() {
					$
							.ajax({
								url : "./statusBarCsvDownload",
								type : 'POST',
								headers: { 'token':$("#token").val()},
								datatype : 'json',
								data : form_data,
								success : function(message1) {
									var j_obj1 = $.parseJSON(message1);
									var percentage = j_obj1.percentage;
									$('.CSVStatus').addClass('hidden');
									$('#rulesdivbar').removeClass('hidden');
									$('.progressbar').css("width",
											percentage + '%');
									$('.progressbar').html(
											"Downloading CSV...   "
													+ percentage + '%');
									console.log(j_obj1);//return false;
									if (percentage > 95) {
										//toastr.info("CSV Downloaded successfully");
										clearInterval(myInterval);
										$('#rulesdivbar').addClass('hidden');
										$('.CSVStatus').removeClass('hidden');
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									console.log(errorThrown);
								}

							})
				}, 3000);
			});


	$("#rulesdiv").on(
			"click",
			function() {
				var form_data = {
					tableName : $('#rulesResultsId1').val(),
				};
				var myInterval = setInterval(function() {
					$
							.ajax({
								url : "./statusBarCsvDownload",
								type : 'POST',
								headers: { 'token':$("#token").val()},
								datatype : 'json',
								data : form_data,
								success : function(message1) {
									var j_obj1 = $.parseJSON(message1);
									var percentage = j_obj1.percentage;
									$('.CSVStatus').addClass('hidden');
									$('#rulesdivbar1').removeClass('hidden');
									$('.progressbar').css("width",
											percentage + '%');
									$('.progressbar').html(
											"Downloading CSV...   "
													+ percentage + '%');
									console.log(j_obj1);//return false;
									if (percentage > 95) {
										//toastr.info("CSV Downloaded successfully");
										clearInterval(myInterval);
										$('#rulesdivbar1').addClass('hidden');
										$('.CSVStatus').removeClass('hidden');
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									console.log(errorThrown);
								}

							})
				}, 3000);
			});
</script>




<script>
	$(document)
			.ready(
					function() {
                        initializeJwfSpaInfra();
						$('.hrefCall')
								.click(
										function() {
											$('#popSample').addClass(
													'is-visible');
											var validationCheck = $(this).attr(
													'id');
											// alert($(this).attr('id'));
											var form_data = {
												validationCheck : validationCheck,
												idApp : $("#idApp").val(),
											};
											// alert("formData ->"+form_data);

											$('#loading-data-progress')
													.removeClass('hidden')
													.show();
											console.log(form_data);
											$
													.ajax({
														url : './getDQIScoresMap',
														type : 'POST',
														headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																listOfValues) {
															console
																	.log(listOfValues);
															if (typeof listOfValues !== "undefined"
																	&& listOfValues != "") {

																google
																		.load(
																				"visualization",
																				"1",
																				{
																					packages : [ "corechart" ]
																				});
																google
																		.setOnLoadCallback(drawChart);
																var data1 = new google.visualization.DataTable();
																data1
																		.addColumn(
																				'string',
																				'Date (Run)');
																data1
																		.addColumn(
																				'number',
																				'DQI');

																//data1.addRow([new Date("2016-02-04"),90]);
																$
																		.each(
																				listOfValues,
																				function(
																						key,
																						value) {
																					$
																							.each(
																									value,
																									function(
																											i,
																											l) {
																										// alert( "Index #" + i + ": " + l );
																										data1
																												.addRow([
																														key,
																														l ]);
																									});
																				});
																data1.sort({
																	column : 0,
																	asc : true
																});
																var title = "";
																if (validationCheck == "Aggregate DQI Summary") {
																	title = validationCheck;
																} else {
																	title = validationCheck
																			+ " DQI";
																}
																var len = $
																		.map(
																				listOfValues,
																				function(
																						n,
																						i) {
																					return i;
																				}).length;
																console
																		.log(len);
																var showTextEvery=2;
										                      	if(len<=10){
										                      		showTextEvery=1;
										                      	}else if(len>10 && len<=20){
										                      		showTextEvery=2;
										                      	}else if(len>20){
										                      		showTextEvery=(len/10);
										                      	}
										                      	showTextEvery=Math.round(showTextEvery);

																//alert(len);
																var options = {
																	title : title,
																	hAxis : {
																		title : 'Date (Run)',
																		titleTextStyle : {
																			color : '#333'
																		},
																		direction:-1,
																		slantedText:true,
																		slantedTextAngle:45,
																		showTextEvery : showTextEvery
																	},
																	vAxis : {
																		minValue : 0
																	},
																	chartArea: {bottom: 100},
																	height : 400,
																	width : 1000
																};

																//alert("options :"+options);

																$(
																		'#loading-data-progress')
																		.addClass(
																				'hidden');
																var chart = new google.visualization.AreaChart(
																		document
																				.getElementById('chartDiv'));
																chart
																		.draw(
																				data1,
																				options);

																$('#chartDiv')
																		.removeClass(
																				'hidden');
															} else {
																$('#chartDiv')
																		.addClass(
																				'hidden');
															}

														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										});
						$("#dqiDataCloseBtn").click(function() {
							$('#popSample').removeClass('is-visible');
						});
					});
					function globalRuleConfirmation(idApp,Rule,customRuleName,failPercentage) {
                                         $('#propModal').modal('show');
                                          $('#modalButtonOk').on('click', function() {
                                                         $('#propModal').modal('hide');
                                                         var defaultPattern='';
                                                     	 var updated_threshold = parseFloat(parseFloat(failPercentage)  + 0.01).toFixed(2);
                                                     	 msg =  "<span style='color:blue;'><i> Updated threshold for " + Rule + " is " + updated_threshold+ ". Please change to edit the threshold.</i><br> <br> <label>New Threshold Value : &nbsp; </label><input type='text' id='thresholdValue'><br><span style='color:red;'>Note: Please enter value in the range 0-100</span><br><span id='outOfRange' style='color:red;'></span>";
                                                           Modal.confirmDialog(2, 'Threshold update Status', msg, ['Submit', 'Cancel'], { onDialogClose: cbPromoteDeleteDimensionDialog, onDialogLoad: cbPromoteDeleteDimensionDialog  });
                                                           $("#thresholdValue").val(updated_threshold);
                                                           if(updated_threshold>100){
                                                            $("#thresholdValue").val("100");
                                                            }
                                                           $('#thresholdValue').keyup(function(e)
                                                            {
                                                             if (/[^0-9.]/g.test(this.value))
                                                             {
                                                               // Filter non-digits from input value.

                                                                   $('#outOfRange').text("Please Enter Positive Number only");

                                                               this.value = this.value.replace(/[^0-9.]/g, '');
                                                             }
                                                              if (this.value < 0) this.value = 0;
                                                              if (this.value > 100) this.value = 100;
                                                              setTimeout(function () {
                                                                 $('#outOfRange').text("");
                                                               }, 5000);
                                                           });

                                                            function	cbPromoteDeleteDimensionDialog(oEvent) {
                                                                 var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
                                                                 var update_Threshold = $("#thresholdValue").val();
                                                                               switch(sEventId) {
                                                                                   case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
                                                                                       PageCtrl.debug.log('on Dialog load','ok');
                                                                                       break;

                                                                                   case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
                                                                                       if (sBtnSelected === 'Submit' && update_Threshold<=100 && !isNaN(update_Threshold)) { updateThreshold(oEvent); }
                                                                                       break;
                                                                               }
                                                            }

                                                            function updateThreshold(oEvent) {
                                                                       var updateThreshold = $("#thresholdValue").val();

                                                                       var form_data = {
                                                                                  idApp: idApp,
                                                                                  checkName:Rule,
                                                                                  column_or_rule_name:customRuleName,
                                                                                  failed_Threshold:updateThreshold,
                                                                                  defaultPattern:defaultPattern
                                                                      };
                                                                  $.ajax({
                                                                          url: './updateColumnCheckThreshold',
                                                                          type: 'POST',
                                                                          headers: { 'token':$("#token").val()},
                                                                          datatype: 'json',
                                                                          data: form_data,
                                                                          beforeSend: function () {
                                                                              $("#updatethresholdWaitModel").show();
                                                                          },
                                                                          success: function (data) {
                                                                               try {
                                                                                    var j_obj = jQuery.parseJSON(data);
                                                                                    var status= j_obj.status;
                                                                                    var msg= j_obj.message;
                                                                                    //toastr.info(msg);
                                                                                    msg = msg + "<span style='color:blue;'><i>. Threshold changes were auto approved in validation rule catalog.</i></span>";
                                                                                    Modal.confirmDialog(0, 'Threshold update Status', msg, ['Ok'], { onDialogClose: reloadResultsPage });
                                                                               } catch(e) {
                                                                                   console.log(e);
                                                                                    toastr.info('Failed to update Check Threshold.');
                                                                               }
                                                                          },
                                                                          error: function (textStatus) {
                                                                          console.log("hii");
                                                                               toastr.info('Failed to update Check Threshold.');
                                                                          },
                                                                           complete: function(){
                                                                             $("#updatethresholdWaitModel").hide();
                                                                           }
                                                                  });

                                                           }

                                          });


                                          $('#modalButton').on('click', function() {
                                               toastr.info("Action has been cancelled! Threshold cann't be updated.");
                                               $('#propModal').modal('hide');
                                          });



                   }
                   function reloadResultsPage(oEvent) {
                           	 window.location.reload();
                  }
</script>
</html>