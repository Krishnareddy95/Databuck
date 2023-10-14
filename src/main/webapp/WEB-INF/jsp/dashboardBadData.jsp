
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
<style>	
	#lengthCheckTable a {
		font-size: 26px;
		text-decoration: none;
		cursor: pointer !important;
	}
	#PatternBadDataTable a {
        		font-size: 26px;
        		text-decoration: none;
        		cursor: pointer !important;
    }
        	#BadDataTable a {
                		font-size: 26px;
                		text-decoration: none;
                		cursor: pointer !important;
    }
    #DefaultPatterntable a {
	font-size: 26px;
	text-decoration: none;
	cursor: pointer !important;
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
					<li class="active" ><a onClick="validateHrefVulnerability(this)" 
						href="badData?idApp=${idApp}" style="padding: 2px 2px;"><strong>Conformity</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)" 
						href="sqlRules?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Custom Rules</strong>
					</a></li>
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
						href="exceptions?idApp=${idApp}" style="padding: 2px 2px;"><strong>Exceptions</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)" 
						href="rootCauseAnalysis?idApp=${idApp}" style="padding: 2px 2px;"><strong>RootCause Analysis</strong> 
					</a></li>
				</ul>
			</div>
		</div>
	</div>

	<!-- [priyanka 25-12-2018] -->
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Length Check
			<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${LengthCheck_TableName}','LengthCheckData')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
			
		</div>
	</div>
	<hr />
	<div class="progress percentagebar hidden" id="rcdivbar">
		<div class="progress">
			<div class="progress-bar progressbar" role="progressbar"
				style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
				aria-valuemax="100">5%</div>
		</div>
	</div>
	<input type="hidden" id="LengthCheckTableName"
		value="${LengthCheck_TableName}">
	<div class="portlet-body">
		<table id="lengthCheckTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<th>Date</th>
					<th>Run</th>
					<th>Status</th>
					<th>Col_Names</th>
					<th>max_length_check_enabled</th>
					<th>Length</th>
					<th>RecordCount</th>
					<th>Total_Failed_Records</th>
					<th>FailedRecords_Percentage</th>
					<th>Length_Threshold</th>
					<th>Learn</th>
					<th>Download File</th>
				</tr>
			</thead>

		</table>
	</div>
	<!--  -->

	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Bad Data Check
			<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Bad_Data_TableName}','BadData')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
			
			
		</div>
	</div>
	<hr />
	<input type="hidden" id="tableName" value="${Bad_Data_TableName}">
	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="idData" value="${idData}">
	<div class="portlet-body">
		<table id="BadDataTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<%
						/* try {
							SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("headerBadData");
							SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
						 for (int i = 1; i <= metaData.getColumnCount(); i++) {
								out.println("<th>" + metaData.getColumnName(i) + "</th>");
							} 
							
							
						} catch (NullPointerException e) {
						
						} */
					%>
					<th>Date</th>
					<th>Run</th>
					<th>Status</th>
					<th>Col_Name</th>
					<th>Total_Record</th>
					<th>Total_Bad_Record</th>
					<th>Bad_Data_Percentage</th>
					<th>Bad_Data_Threshold</th>
					<th>Learn</th>
					<th>Download File</th>
				</tr>
			</thead>

		</table>



	</div>
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<!--<span class="caption-subject bold ">Regex Pattern Unmatch Column Summary-->
			<span class="caption-subject bold ">Regex Pattern Check
			<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Pattern_Bad_Data_TableName}','BadData_Pattern_Check')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="PatterntableName"
		value="${Pattern_Bad_Data_TableName}">
		<input type="hidden" id="isPatternEnable"
		value="${isPatternEnable}">
	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="idData" value="${idData}">
	<div class="portlet-body">
		<table id="PatternBadDataTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<%
					/* 	try {
							SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("headerPattern");
							SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
							for (int i = 1; i <= metaData.getColumnCount(); i++) {
								out.println("<th>" + metaData.getColumnName(i) + "</th>");
							}
						} catch (NullPointerException e) {

							
						} */
					%>
					<th>Date</th>
					<th>Run</th>
					<th>Status</th>
					<th>Col_Name</th>
					<th>Total_Record</th>
					<th>Total_Failed_Record</th>
					<th>FailedRecords_Percentage</th>
					<th>Pattern_Threshold</th>
					<th>Learn</th>
					<th>Download File</th>
				</tr>
			</thead>
			<tbody></tbody>

		</table>


	</div>
		<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Default Pattern Check
				Column Summary <a
				onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Default_Pattern_TableName}','DefaultPatternCheck')"
				href="#" class="CSVStatus" id="">(Download CSV)</a>
			</span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="DefaultPatterntableName"
		value="${Default_Pattern_TableName}">
	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="idData" value="${idData}">
	<div class="portlet-body">
		<table id="DefaultPatternTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<th>Date</th>
					<th>Run</th>
					<th>Status</th>
					<th>Col_Name</th>
					<th>Total_Records</th>
					<th>Total_Failed_Records</th>
					<th>Total_Matched_Records</th>
					<th>Patterns_List</th>
					<th>FailedRecords_Percentage</th>
					<th>Default_Pattern_Threshold</th>	
					<th>Learn</th>									
					<th>Download File</th>
				</tr>
			</thead>
			<tbody></tbody>

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
			<br />
			<br />
			<br />
			<br />
			<div id="chartDiv"
				style="height: 500px; width: 100%; margin: 0 auto;"
				class="dashboard-summary-chart"></div>
			<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
		</div>
	</div>
	<jsp:include page="downloadCsvReports.jsp"/>
	<jsp:include page="footer.jsp" />
</body>

<script>

jQuery(document).ready(function() {
	
	var isPatternEnable = $("#isPatternEnable").val();

	var idApp = $("#idApp").val();
	var idData = $("#idData").val();
	//alert(tableName);

	var LengthChktableName = $("#LengthCheckTableName").val();

	table = $('#lengthCheckTable')
	.dataTable(
			{
				"bPaginate" : true,
				"columnDefs": [
			  	     { orderable: false, targets: [11]}
			  	 		  	  ],
				"order" : [ 0, 'asc' ],
				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,
				"sAjaxSource" : path
						+ "/lengthCheckTableName?tableName="
						+ LengthChktableName + "&idApp=" + idApp,
				"aoColumns": [
                    {"data" : "Date"},
                    {"data" : "Run"},
                    {"data" : "Status",
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
                    {"data" : "ColName"},
                    {"data" : "max_length_check_enabled"},
                    {"data" : "Length"},
                    {"data" : "RecordCount"},
                    {"data" : "TotalFailedRecords"},
                    {"data" : "FailedRecords_Percentage"},
                    {"data" : "Length_Threshold"},
                    {"data" : "FailedRecords_Percentage",
                      "render": function(data, type, row, meta){
                           var failPercentage = row.FailedRecords_Percentage;
                           var lengthColName = row.ColName;
                           var max_length_check_enabled = row.max_length_check_enabled;
                           var checkName='Length Check';
                           if(max_length_check_enabled==='Y'){
                                checkName='Max Length Check';
                           }
                           data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'"+checkName+"','"+lengthColName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\"></a>";
                       return data;
                       }
                    },
                    {"data" : "ColName",
                           "render": function(data, type, row, meta){
                              data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','LengthCheckData/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
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
				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',

			});
});

jQuery(document).ready(function() {
	
	var idApp = $("#idApp").val();
	var idData = $("#idData").val();
	var tableName = $("#tableName").val();
	
	table = $('#BadDataTable')
	.dataTable(
			{
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],
				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,
				"sAjaxSource" : path
						+ "/ColSummTableForBadDataTableTab?tableName="
						+ tableName + "&idApp="+idApp,
				"aoColumns": [
                    {"data" : "Date"},
                    {"data" : "Run"},
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
                    {"data" : "ColName"},
                    {"data" : "TotalRecord"},
                    {"data" : "TotalBadRecord"},
                    {"data" : "badDataPercentage"},
                    {"data" : "badDataThreshold"},
                    {"data" : "badDataThreshold",
                      "render": function(data, type, row, meta){
                           var failPercentage = row.badDataPercentage;
                           var badDatacheckColName = row.ColName;
                           data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Bad Data Check','"+badDatacheckColName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\" </a>";
                       return data;
                       }
                    },
                    {"data" : "ColName",
                           "render": function(data, type, row, meta){
                              data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','BadData/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
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
				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',

			});
	
});

 jQuery(document).ready(function() {
	
	var idApp = $("#idApp").val();
	var idData = $("#idData").val();
	var tableName3 = $("#PatterntableName").val();
	
	table = $('#PatternBadDataTable')
	.dataTable({

				"bPaginate" : true,
				"order" : [ 0, 'asc' ],
				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,
				"sAjaxSource" : path
						+ "/ColSummTableForPatternTableTab?tableName="
						+ tableName3+"&idApp="+idApp,
						"aoColumns": [
                        {"data" : "Date"},
                        {"data" : "Run"},
                        {"data" : "Status",
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
                        {"data" : "Col_Name"},
                        {"data" : "Total_Records"},
                        {"data" : "Total_Failed_Records"},
                        {"data" : "FailedRecords_Percentage"},
                        {"data" : "Pattern_Threshold"},
                        {"data" : "Pattern_Threshold",
                          "render": function(data, type, row, meta){
                               var failPercentage = row.FailedRecords_Percentage;
                               var patterncheckColName = row.Col_Name;
                               data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Pattern Check','"+patterncheckColName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\" </a>";
                           return data;
                           }
                        },
                        {"data" : "Col_Name",
                                "render": function(data, type, row, meta){
                                  data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName3+"','BadData_Pattern_Check/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
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
				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
				},
				"language" : {
					"infoFiltered" : ""
				},
				"dom" : 'Cf<"toolbar"">rtip',

			});
	
}); 
 jQuery(document).ready(function() {
		
		var idApp = $("#idApp").val();
		var idData = $("#idData").val();
		var DefaultPatternTableName = $("#DefaultPatterntableName").val();
		
		table = $('#DefaultPatternTable')
		.dataTable({

					"bPaginate" : true,
					"order" : [[ 0, 'asc' ], [ 1, 'asc' ]],
					"bInfo" : true,
					"iDisplayStart" : 0,
					"bProcessing" : true,
					"bServerSide" : true,
					"dataSrc" : "",
					'sScrollX' : true,
					"sAjaxSource" : path
							+ "/ColSummTableForDefaultPatternTab?tableName="
							+ DefaultPatternTableName+"&idApp="+idApp,
							"aoColumns": [
							    {"data" : "Date"},
		                        {"data" : "Run"},
		                        {"data" : "Status",
		                             "render": function(data, type, row, meta){
		                                if ( row.New_Pattern === "Y") {
			                                data = "<span class='label label-sm' style=\"background-color: #00BFFF;color: #ffffff; padding-right: 14px; padding-left: 14px;\">"+
			                                             "NEW </span>";

			                            } else if (data === "passed" && row.New_Pattern === "N") {
		                                    data = "<span class='label label-success label-sm'>"
		                                            + data + "</span>";

		                                } else if (data === "failed") {
		                                    data = "<span class='label label-danger label-sm' style=\" padding-right: 8px; padding-left: 8px;\">"
		                                            + data + "</span>";
		                                }

		                                return data;
		                                }
		                        },
		                        {"data" : "Col_Name"},
		                        {"data" : "Total_Records"},
		                        {"data" : "Total_Failed_Records"},
		                        {"data" : "Total_Matched_Records"},
		                        {"data" : "Patterns_List"},
		                        {"data" : "FailedRecords_Percentage"},
		                        {"data" : "Pattern_Threshold",
		                        	"render": function(data, type, row, meta){
		                                if ( row.New_Pattern === "Y") {
			                               return data;
			                            } else {
			                               return null;	
			                            }
		                        	}
			                    },
		                        {"data" : "Learn",
		                          "render": function(data, type, row, meta){
		                               var failPercentage = row.FailedRecords_Percentage;
		                               var patterncheckColName = row.Col_Name;
		                               if (row.New_Pattern === "Y"){	
		                               data = "<a onClick=\"javascript:updateColumnCheckPatterns("+idApp+",'Default Pattern Check','"+patterncheckColName+"','"+row.Patterns_List+"','"+row.Total_Matched_Records+"','"+row.Total_Records+"')\" class=\"fa fa-thumbs-up\" </a>";
		                               return data;
		                               }else{
		                                	return '';
		                                } 
		                           }
		                        },
		                        {"data" : "Col_Name",
		                           "render": function(data, type, row, meta){
		                                if (row.Total_Failed_Records != "0"){	
		                                  data = "<a onclick=\"javascript:downloadCsvReportsDirect('"+idApp+"','"+row.Date+"','"+row.Run+"','DefaultPatternCheck/"+data+"','"+row.Csv_File_Path+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
		                                  return data;
		                                }else{
		                                	return '';
		                                }                                 
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
					//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
					},
					"language" : {
						"infoFiltered" : ""
					},
					"dom" : 'Cf<"toolbar"">rtip',

				});
		
	}); 
</script>


<script>
	$(document)
			.ready(
					function() {

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
											$('#loading-data-progress')
													.removeClass('hidden')
													.show();
											console.log(form_data);
											$
													.ajax({
														url : './getDQIScoresMap',
														type : 'POST',
														datatype : 'json',
														headers: { 'token':$("#token").val()},
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
																				'Date(Run)');
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
																var showTextEvery = 2;
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
													    			height: 400,
																	width : 1000
																};
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
</script>
</html>